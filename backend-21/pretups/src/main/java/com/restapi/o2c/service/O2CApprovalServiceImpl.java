package com.restapi.o2c.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
//import org.apache.struts.action.ActionForm;
//import org.apache.struts.action.ActionForward;
//import org.apache.struts.action.ActionMapping;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.BaseResponse;
import com.btsl.common.BaseResponseMultiple;
import com.btsl.common.EMailSender;
import com.btsl.common.ErrorMap;
import com.btsl.common.ListValueVO;
import com.btsl.common.MasterErrorList;
import com.btsl.common.RowErrorMsgList;
import com.btsl.common.RowErrorMsgLists;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelVoucherItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.OneLineTXNLog;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyBL;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyDAO;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyVO;
import com.btsl.pretups.loyaltymgmt.businesslogic.PromotionDetailsVO;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.processes.TargetBasedCommissionMessages;
import com.btsl.pretups.product.businesslogic.NetworkProductDAO;
import com.btsl.pretups.product.businesslogic.ProductVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.OracleUtil;
import com.btsl.voms.vomscategory.businesslogic.VomsCategoryVO;
import com.btsl.voms.vomscategory.businesslogic.VomsPackageVO;
import com.btsl.voms.vomscategory.businesslogic.VomsPackageVoucherVO;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductVO;
import com.btsl.voms.voucher.businesslogic.VomsBatchVO;
import com.client.pretups.gateway.businesslogic.USSDPushMessage;
import com.ibm.icu.text.SimpleDateFormat;
import com.restapi.channelAdmin.responseVO.O2CTransferDetailsResponseVO;
import com.web.pretups.channel.transfer.web.ChannelTransferApprovalForm;
import com.web.pretups.channel.transfer.web.ChannelTransferEnquiryModel;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
import com.web.voms.vomscategory.businesslogic.VomsCategoryWebDAO;
/**
 * 
 * @author md.sohail
 * @param <O2CTxnApprovalDetailsResponseVO>
 *
 */
@Service("O2CApprovalServiceI")
public class O2CApprovalServiceImpl implements O2CApprovalServiceI {
	
	private final Log _log = LogFactory.getLog(this.getClass().getName());
	Locale locale = null;
	private HttpServletResponse responseSwag = null;
	private BaseResponseMultiple response = null;
	private ErrorMap errorMap = null;
	private List<BaseResponse> successList = null;
	private int successCount ;
	private int failureCount; 
	
	@SuppressWarnings("rawtypes")
	@Override 
	public BaseResponseMultiple processO2CStockApproval(O2CStockAppRequestVO o2cStockAppRequestVO,
			MultiValueMap<String, String> headers, HttpServletResponse responseSwag) throws BTSLBaseException 
	{

		final String methodName = "processO2CStockApproval";
		if (_log.isDebugEnabled()) 
		{
			_log.debug("processO2CStockApproval", "Entered");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		ChannelTransferApprovalForm theForm = null;
		List<O2CDataStApp> o2cStockAppRequestList = null;
		OAuthUser oAuthUser = null;
		OAuthUserData oAuthUserData = null;
		int totalApprCount = 0;
		successCount = 0;
		failureCount = 0;
		response = new BaseResponseMultiple();
        try {
        	response.setService("stockApproval");
        	this.responseSwag = responseSwag;
			errorMap = new ErrorMap();
			successList = new ArrayList<>();
        	locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
        	theForm= new ChannelTransferApprovalForm();
        	theForm.flush();
        	
        	//Authentication 
        	oAuthUser = new OAuthUser();
			oAuthUserData = new OAuthUserData();
			oAuthUser.setData(oAuthUserData);
 	        OAuthenticationUtil.validateTokenApi(oAuthUser, headers,responseSwag);
 	        
 	       if( !BTSLUtil.isNullObject(o2cStockAppRequestVO) && !BTSLUtil.isNullOrEmptyList((ArrayList) o2cStockAppRequestVO.getO2cStockAppRequests()) ) {
 	    	  totalApprCount = o2cStockAppRequestVO.getO2cStockAppRequests().size();
 	       } else {
 	    	  throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.O2C_APP_REQUEST_EMPTY);
 	       }
 	       
 	        mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
        	o2cStockAppRequestList = o2cStockAppRequestVO.getO2cStockAppRequests();
        	
        	//getting loogedIn user details
        	final UserDAO userDao = new UserDAO();
        	ChannelUserVO  sessionUserVO = (ChannelUserVO)userDao.loadAllUserDetailsByLoginID(con, oAuthUser.getData().getLoginid());
        	
        	this.validateOperatorUserType( sessionUserVO ); // throw exception

            List<RowErrorMsgLists> outerRowErrorMsgLists = null;
            RowErrorMsgLists outerRowErrorMsgListObj = null;
            int count = 0;
            
            try 
            {
            	for(O2CDataStApp o2CDataAppRequest: o2cStockAppRequestList) 
    			{   
    				count++;
    				//check for preferences
		            this.checkPreferences(theForm, o2CDataAppRequest);
				
    				ArrayList<RowErrorMsgList> rowError = new ArrayList<RowErrorMsgList>();
    				//validating request
    				boolean isValid = this.validateRequest(con, o2CDataAppRequest, theForm,  count, rowError);
    				if(!isValid) {
    					failureCount +=1; 
    					outerRowErrorMsgLists = new ArrayList<RowErrorMsgLists>();
    					outerRowErrorMsgListObj = new RowErrorMsgLists();
    					outerRowErrorMsgListObj.setRowErrorMsgList(rowError);
    					outerRowErrorMsgLists.add(outerRowErrorMsgListObj);
    					continue;
    				}
    				
    				this.transferApprovalConfirmation(theForm, o2CDataAppRequest.getTxnId(), o2CDataAppRequest);
    				this.processOptToChannelTransfer(theForm, o2CDataAppRequest);
    				this.confirmOptToChannelTransfer(theForm, o2CDataAppRequest.getStatus(), sessionUserVO); // confirm
    			
    			} //loop end
            } catch (BTSLBaseException be) 
			{   
            	failureCount +=1; 
				_log.error(methodName, "Exception:e=" + be);
				_log.errorTrace(methodName, be);
				outerRowErrorMsgLists = new ArrayList<RowErrorMsgLists>();
				outerRowErrorMsgListObj = new RowErrorMsgLists();
				ArrayList<RowErrorMsgList> rowError = new ArrayList<RowErrorMsgList>();
				
				RowErrorMsgList rowErrorMsgList = new RowErrorMsgList();
				ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
				MasterErrorList masterErrorListObj = new MasterErrorList();
				masterErrorListObj.setErrorCode(be.getMessageKey());
				if(!BTSLUtil.isNullorEmpty(be.getArgs())) {
					masterErrorListObj.setErrorMsg( getMessage(locale, be.getMessageKey(), be.getArgs()) );
				} else {
					masterErrorListObj.setErrorMsg(getMessage(locale, be.getMessageKey()));
				}
				
				masterErrorLists.add(masterErrorListObj);
				RowErrorMsgLists rowErrorMsgListsObj2  = new RowErrorMsgLists();
				rowErrorMsgListsObj2.setMasterErrorList(masterErrorLists);
				ArrayList<RowErrorMsgLists> innerRow = new ArrayList<RowErrorMsgLists>();
				innerRow.add(rowErrorMsgListsObj2);
				rowErrorMsgList.setRowErrorMsgLists(innerRow);
				rowError.add(rowErrorMsgList);
				outerRowErrorMsgListObj.setRowErrorMsgList(rowError);
				outerRowErrorMsgLists.add(outerRowErrorMsgListObj);
				
			} catch (Exception e) 
            {
				_log.error(methodName, "Exceptin:e=" + e);
				throw new BTSLBaseException(this, methodName, e.getMessage());
			}
            
            
        errorMap.setRowErrorMsgLists(outerRowErrorMsgLists);   
		this.createFinalResponse(totalApprCount);

        } catch (BTSLBaseException be) 
        {
			_log.error(methodName, "Exception:e=" + be);
			_log.errorTrace(methodName, be);
			  if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
				  response.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
            	 responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
            }
             else{
            	 response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
             responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
             }
			String errorCode = be.getMessageKey();
			if(BTSLUtil.isNullorEmpty(errorCode))  {
				errorCode = PretupsErrorCodesI.O2C_APPROVAL_WENT_WRONG;
			}
			String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), errorCode,
					be.getArgs());
			response.setMessageCode(errorCode);
			response.setMessage(resmsg);
		} catch (Exception e) 
        {
			_log.error(methodName, "Exceptin:e=" + e);
			_log.errorTrace(methodName, e);
			response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setMessageCode(e.toString());
			response.setMessage(e.toString() + " : " + e.getMessage());
		} finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("O2CApprovalServiceImpl#" + methodName);
					mcomCon = null;
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			if (_log.isDebugEnabled()) {
				_log.debug(methodName, " Exited ");
			}
		}
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, response);
			_log.debug(methodName, "Exiting ");
		}

		return response;
	}
	
	/**
	 * 
	 * @param sessionUserVO
	 * @throws BTSLBaseException
	 */
	private void validateOperatorUserType(ChannelUserVO sessionUserVO) throws BTSLBaseException{
		
		final String methodName = "validateOperatorUserType";
		if (_log.isDebugEnabled()) {
			_log.debug("validateOperatorUserType", "Entered: ");
			_log.debug(methodName, sessionUserVO.getUserType());
		}
		if( !sessionUserVO.getUserType().equals(PretupsI.OPERATOR_USER_TYPE) ){
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_NOT_ALLOWED_TO_APPROVE);
		}
	}
	
    /**
     * 
     * @param theForm
     * @param o2CDataAppRequest
     */
	private void checkPreferences(ChannelTransferApprovalForm theForm, O2CDataStApp o2CDataAppRequest){
		/*
		 * 1) To check external txn num is required at which level.
		 * if its value contain 0 then externalTxnexist is Y at order
		 * initiation time
		 * if its value contain 1 then externalTxnexist is Y at level 1
		 * approval time
		 * if its value contain 2 then externalTxnexist is Y at level 2
		 * approval time
		 * if its value contain 3 then externalTxnexist is Y at level 3
		 * approval time
		 * 2) To check external txn mandatory is required at which level.
		 * if its value contain 0 then externalTxnMandatory is Y at order
		 * initiation time
		 * if its value contain 1 then externalTxnMandatory is Y at level 1
		 * approval time
		 * if its value contain 2 then externalTxnMandatory is Y at level 2
		 * approval time
		 * if its value contain 3 then externalTxnMandatory is Y at level 3
		 * approval time
		 */
		if(PretupsI.O2C_APPROVE.equalsIgnoreCase(o2CDataAppRequest.getStatus()))
		{
			final String externalTxnLevel = (String) PreferenceCache
					.getSystemPreferenceValue(PretupsI.TRANSFER_EXTERNAL_TXN_LEVEL);
			final String externalTxnMandatory = (String) PreferenceCache
					.getSystemPreferenceValue(PretupsI.TRANSFER_EXTERNAL_TXN_MANDATORY);
			if (PretupsI.CHANNEL_TRANSFER_ORDER_NEW.equalsIgnoreCase(o2CDataAppRequest.getCurrentStatus())) {
				if (!BTSLUtil.isNullString(externalTxnLevel) && externalTxnLevel.indexOf("1") != -1) {
					theForm.setExternalTxnExist(PretupsI.YES);
					if (!BTSLUtil.isNullString(externalTxnMandatory) && externalTxnMandatory.indexOf("1") != -1) {
						theForm.setExternalTxnMandatory(PretupsI.YES);
					}
				}
			} else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1
					.equalsIgnoreCase(o2CDataAppRequest.getCurrentStatus())) {
				if (!BTSLUtil.isNullString(externalTxnLevel) && externalTxnLevel.indexOf("2") != -1) {
					theForm.setExternalTxnExist(PretupsI.YES);
					if (!BTSLUtil.isNullString(externalTxnMandatory) && externalTxnMandatory.indexOf("2") != -1) {
						theForm.setExternalTxnMandatory(PretupsI.YES);
					}
				}
			} else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2
					.equalsIgnoreCase(o2CDataAppRequest.getCurrentStatus())) {
				if (!BTSLUtil.isNullString(externalTxnLevel) && externalTxnLevel.indexOf("3") != -1) {
					theForm.setExternalTxnExist(PretupsI.YES);
					if (!BTSLUtil.isNullString(externalTxnMandatory) && externalTxnMandatory.indexOf("3") != -1) {
						theForm.setExternalTxnMandatory(PretupsI.YES);
					}
				}
			}

			// pyment details is mandatory or not
			final int paymentDetailsLevel = (Integer) PreferenceCache
					.getSystemPreferenceValue(PreferenceI.PAYMENTDETAILSMANDATE_O2C);
			if (paymentDetailsLevel >= 0 && paymentDetailsLevel <= 1) {
				if (paymentDetailsLevel == 1) {
					theForm.setShowPaymentInstrumentType(true);
				}
				theForm.setShowPaymentDetails(true);
			} else {
				theForm.setShowPaymentDetails(false);
			}

		} else {
			theForm.setExternalTxnExist(PretupsI.NO);
			theForm.setExternalTxnMandatory(PretupsI.NO);
			theForm.setShowPaymentInstrumentType(false);
			theForm.setShowPaymentDetails(false);
		}
    }
    /**
     * 
     * @param con
     * @param theForm
     * @param channelTransferVO
     * @return
     * @throws BTSLBaseException
     * @throws ParseException
     */
	private void loadChannelTransferVO(Connection con, ChannelTransferApprovalForm theForm, ChannelTransferVO channelTransferVO) throws BTSLBaseException, ParseException 
	{
		channelTransferVO.setTransferID(theForm.getTransferNum());
		channelTransferVO.setNetworkCode(theForm.getNetworkCode());
		channelTransferVO.setNetworkCodeFor(theForm.getNetworkCode());
		channelTransferVO.setTransferSubType("T");
		final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
		channelTransferDAO.loadChannelTransfersVO(con, channelTransferVO);
	}
	
	/**
	 * 
	 * @param con
	 * @param o2CDataAppRequest
	 * @param theForm
	 * @param count
	 * @return
	 * @throws BTSLBaseException
	 * @throws ParseException
	 */
	private boolean validateRequest(Connection con, O2CDataStApp o2CDataAppRequest, ChannelTransferApprovalForm theForm, int count, 
			ArrayList<RowErrorMsgList> rowError) throws BTSLBaseException, ParseException {
		RowErrorMsgList rowErrorMsgList = new RowErrorMsgList();
		ArrayList<RowErrorMsgLists> rowErrorMsgLists = new ArrayList<RowErrorMsgLists>();
		
		
		
		 boolean isValidReq = true;
		 boolean addToRow = false;
		 //UserPhoneVO phoneVO = userDAO.loadUserAnyPhoneVO(con, o2CDataAppRequest.getToMsisdn());
		 ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		 ChannelUserVO receiverVO = null;
		
		 //toMsisdn : mandatory
		 RowErrorMsgLists rowErrorMsgListsObj2 = null;
		 ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
		 if(!BTSLUtil.isNullorEmpty( o2CDataAppRequest.getToMsisdn() )) 
		 {
			 receiverVO = channelUserDAO.loadChannelUserDetails(con,  o2CDataAppRequest.getToMsisdn());
			 if(BTSLUtil.isNullorEmpty(receiverVO)) {
				 String[] args = new String[] { o2CDataAppRequest.getToMsisdn() };
				 throw new BTSLBaseException(this, "validateRequest", PretupsErrorCodesI.INVALID_TO_MSISDN, args);
			 }
			 theForm.setUserCode(receiverVO.getUserCode());
			 theForm.setNetworkCode(receiverVO.getNetworkID());
			 theForm.setUserID(receiverVO.getUserID());
		 } else 
		{
			MasterErrorList masterErrorListObj = new MasterErrorList();
			masterErrorListObj.setErrorCode(PretupsErrorCodesI.NULL_OR_INVALID_VALUE_IN_REQ);
			String[] args = { "toMsisdn" };
			masterErrorListObj.setErrorMsg(getMessage(locale, PretupsErrorCodesI.NULL_OR_INVALID_VALUE_IN_REQ, args));
			isValidReq = false;
			masterErrorLists.add(masterErrorListObj);
			addToRow = true;
		}
		 if(addToRow) {
			 rowErrorMsgListsObj2 = new RowErrorMsgLists();
			 rowErrorMsgListsObj2.setMasterErrorList(masterErrorLists);
			 rowErrorMsgLists.add(rowErrorMsgListsObj2);
		 }
		//paymentDetails
		addToRow = false;
		if (theForm.getShowPaymentDetails())  // checking for preferenc :: not needed for rejection
		{ 
			masterErrorLists = new ArrayList<>();
			if (!BTSLUtil.isNullorEmpty(o2CDataAppRequest.getPaymentDetails())) 
			{
				if (!BTSLUtil.isNullorEmpty(o2CDataAppRequest.getPaymentDetails().getPaymentDate())
						&& BTSLUtil.isValidDatePattern(o2CDataAppRequest.getPaymentDetails().getPaymentDate()) ) 
				{
					theForm.setPaymentInstrumentDate(o2CDataAppRequest.getPaymentDetails().getPaymentDate());
				} else 
				{
					MasterErrorList masterErrorListObj = new MasterErrorList();
					masterErrorListObj.setErrorCode(PretupsErrorCodesI.NULL_OR_INVALID_VALUE_IN_REQ);
					String[] args = { "paymentDate" };
					masterErrorListObj
							.setErrorMsg(getMessage(locale, PretupsErrorCodesI.NULL_OR_INVALID_VALUE_IN_REQ, args));
					isValidReq = false;
					addToRow = true;
					masterErrorLists.add(masterErrorListObj);
				}
				if( "Cash".equalsIgnoreCase(o2CDataAppRequest.getPaymentDetails().getPaymentType()) ) { //in case of "cash" paymentInstNumb not required
					theForm.setPaymentInstNum(o2CDataAppRequest.getPaymentDetails().getPaymentInstNumber());
				} else 
				{
					if (!BTSLUtil.isNullorEmpty(o2CDataAppRequest.getPaymentDetails().getPaymentInstNumber())
							&& BTSLUtil.isValidNumber(o2CDataAppRequest.getPaymentDetails().getPaymentInstNumber()) 
							&& o2CDataAppRequest.getPaymentDetails().getPaymentInstNumber().length() <=15) 
					{
						theForm.setPaymentInstNum(o2CDataAppRequest.getPaymentDetails().getPaymentInstNumber());
					} else 
					{
						MasterErrorList masterErrorListObj = new MasterErrorList();
						masterErrorListObj.setErrorCode(PretupsErrorCodesI.NULL_OR_INVALID_VALUE_IN_REQ);
						String[] args = { "paymentInstNumber" };
						masterErrorListObj
								.setErrorMsg(getMessage(locale, PretupsErrorCodesI.NULL_OR_INVALID_VALUE_IN_REQ, args));
						isValidReq = false;
						addToRow = true;
						masterErrorLists.add(masterErrorListObj);
					}
				}
				
			} else 
			{
				MasterErrorList masterErrorListObj = new MasterErrorList();
				masterErrorListObj.setErrorCode(PretupsErrorCodesI.NULL_VALUE_IN_REQ);
				String[] args = { "paymentDetails" };
				masterErrorListObj.setErrorMsg(getMessage(locale, PretupsErrorCodesI.NULL_VALUE_IN_REQ, args));
				isValidReq = false;
				addToRow = true;
				masterErrorLists.add(masterErrorListObj);
			}
			if (addToRow) {
				rowErrorMsgListsObj2 = new RowErrorMsgLists();
				rowErrorMsgListsObj2.setMasterErrorList(masterErrorLists);
				rowErrorMsgLists.add(rowErrorMsgListsObj2);
			}
		}
		 
		 //txnId
		 addToRow = false;
		 masterErrorLists = new ArrayList<MasterErrorList>();
		 if( !BTSLUtil.isNullorEmpty(o2CDataAppRequest.getTxnId()) )  { //mandatory for approval/rejection
			 theForm.setTransferNum(o2CDataAppRequest.getTxnId());
		 } else {
			MasterErrorList masterErrorListObj = new MasterErrorList();
			masterErrorListObj.setErrorCode(PretupsErrorCodesI.NULL_OR_INVALID_VALUE_IN_REQ);
			String[] args = { "txnId" };
			masterErrorListObj.setErrorMsg(getMessage(locale, PretupsErrorCodesI.NULL_OR_INVALID_VALUE_IN_REQ, args ));
			isValidReq = false;
			addToRow = true;
			masterErrorLists.add(masterErrorListObj);
		 }
		 if(addToRow) {
			 rowErrorMsgListsObj2 = new RowErrorMsgLists();
			 rowErrorMsgListsObj2.setMasterErrorList(masterErrorLists);
			 rowErrorMsgLists.add(rowErrorMsgListsObj2);
		 }
		 
		 //refNumber : non-mandatory
		 addToRow = false;
		 masterErrorLists = new ArrayList<>();
		 if( !BTSLUtil.isNullorEmpty(o2CDataAppRequest.getRefNumber()) )  { 
			 theForm.setRefrenceNum(o2CDataAppRequest.getRefNumber());
		 }
		 
		//products : non-mandatory for rejection
		 addToRow = false;
		 masterErrorLists = new ArrayList<>();
		 if( PretupsI.O2C_APPROVE.equalsIgnoreCase(o2CDataAppRequest.getStatus()) && BTSLUtil.isNullOrEmptyList((ArrayList) o2CDataAppRequest.getProducts()) ) 
		 {  
				MasterErrorList masterErrorListObj = new MasterErrorList();
				masterErrorListObj.setErrorCode(PretupsErrorCodesI.NULL_VALUE_IN_REQ);
				String[] args = {"productDetails"};
				masterErrorListObj.setErrorMsg(getMessage(locale, PretupsErrorCodesI.NULL_VALUE_IN_REQ, args));
				isValidReq = false;
				addToRow = true;
				masterErrorLists.add(masterErrorListObj);
		 }
		 if(addToRow) {
			 rowErrorMsgListsObj2 = new RowErrorMsgLists();
			 rowErrorMsgListsObj2.setMasterErrorList(masterErrorLists);
			 rowErrorMsgLists.add(rowErrorMsgListsObj2);
		 }
		 
		 MasterErrorList masterErrorListObj = null;
		//extTxnDate and extTxnNumber
		 if( PretupsI.YES.equals(theForm.getExternalTxnMandatory()) ) {  //mandatory for approval, non-mandatory for rejection
			 addToRow = false;
			 masterErrorLists = new ArrayList<>();
			
			 if(!BTSLUtil.isNullorEmpty(o2CDataAppRequest.getExtTxnDate()) 
					 && BTSLUtil.isValidDatePattern(o2CDataAppRequest.getExtTxnDate())) 
			 {
				 theForm.setExternalTxnDate(o2CDataAppRequest.getExtTxnDate());
			 } else {
				 masterErrorListObj = new MasterErrorList();
				 masterErrorListObj.setErrorCode(PretupsErrorCodesI.NULL_OR_INVALID_VALUE_IN_REQ);
				 String[] args = {"extTxnDate"};
				 masterErrorListObj.setErrorMsg(getMessage(locale, PretupsErrorCodesI.NULL_OR_INVALID_VALUE_IN_REQ, args));
				 isValidReq = false;
				 addToRow = true;
				 masterErrorLists.add(masterErrorListObj);
			 }
			 if(addToRow) {
				 rowErrorMsgListsObj2 = new RowErrorMsgLists();
				 rowErrorMsgListsObj2.setMasterErrorList(masterErrorLists);
				 rowErrorMsgLists.add(rowErrorMsgListsObj2);
			 }
			 
			//extTxnNumber
			 addToRow = false;
			 masterErrorLists = new ArrayList<>();
			 if( !BTSLUtil.isNullorEmpty(o2CDataAppRequest.getExtTxnNumber()) ) 
			 {
				 theForm.setExternalTxnNum(o2CDataAppRequest.getExtTxnNumber());
			 } else {
				 masterErrorListObj = new MasterErrorList();
				 masterErrorListObj.setErrorCode(PretupsErrorCodesI.NULL_OR_INVALID_VALUE_IN_REQ);
				 String[] args = {"extTxnNumber"};
				 masterErrorListObj.setErrorMsg(getMessage(locale, PretupsErrorCodesI.NULL_OR_INVALID_VALUE_IN_REQ, args));
				 isValidReq = false;
				 addToRow = true;
				 masterErrorLists.add(masterErrorListObj);
			 }
			 if(addToRow) {
				 rowErrorMsgListsObj2 = new RowErrorMsgLists();
				 rowErrorMsgListsObj2.setMasterErrorList(masterErrorLists);
				 rowErrorMsgLists.add(rowErrorMsgListsObj2);
			 }
			
		 } else {
				
                //validate date format
				if (!BTSLUtil.isNullorEmpty(o2CDataAppRequest.getExtTxnDate()) && !BTSLUtil.isValidDatePattern(o2CDataAppRequest.getExtTxnDate())) 
				{
					isValidReq = false;
					masterErrorListObj = new MasterErrorList();
					masterErrorListObj.setErrorCode(PretupsErrorCodesI.INVALID_FORMAT_FOR_DATE);
					String[] args = { (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT) !=null ? (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT) : "dd/MM/yy" } ;
					masterErrorListObj.setErrorMsg(getMessage(locale, PretupsErrorCodesI.NULL_OR_INVALID_VALUE_IN_REQ, args));
					
					masterErrorLists = new ArrayList<>();
					masterErrorLists.add(masterErrorListObj);
					
					rowErrorMsgListsObj2 = new RowErrorMsgLists();
					rowErrorMsgListsObj2.setMasterErrorList(masterErrorLists);
					rowErrorMsgLists.add(rowErrorMsgListsObj2);
					
				}
				theForm.setExternalTxnDate(null);
				theForm.setExternalTxnNum(o2CDataAppRequest.getExtTxnNumber());
			}
		 
		 //currentStatus
		 addToRow = false;
		 masterErrorLists = new ArrayList<>();
		 if(!BTSLUtil.isNullorEmpty(o2CDataAppRequest.getCurrentStatus())) {
			 if(PretupsI.CHANNEL_TRANSFER_ORDER_NEW.equalsIgnoreCase(o2CDataAppRequest.getCurrentStatus())) 
			 {
				 theForm.setCurrentApprovalLevel(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
				 theForm.setApprovalLevel(1);
				 theForm.setApprove1Remark(o2CDataAppRequest.getRemarks());
			 } 
			 else if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equalsIgnoreCase(o2CDataAppRequest.getCurrentStatus())) 
			 {
				 theForm.setCurrentApprovalLevel(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
				 theForm.setApprovalLevel(2);
				 theForm.setApprove2Remark(o2CDataAppRequest.getRemarks());
			 } 
			 else if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equalsIgnoreCase(o2CDataAppRequest.getCurrentStatus())) 
			 {
				 theForm.setCurrentApprovalLevel(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3);
				 theForm.setApprovalLevel(3);
				 theForm.setApprove3Remark(o2CDataAppRequest.getRemarks());
			 } else 
			 {
				 masterErrorListObj = new MasterErrorList();
				 masterErrorListObj.setErrorCode(PretupsErrorCodesI.NULL_OR_INVALID_VALUE_IN_REQ);
				 String[] args = {"currentStatus"};
				 masterErrorListObj.setErrorMsg(getMessage(locale, PretupsErrorCodesI.NULL_OR_INVALID_VALUE_IN_REQ, args));
				 isValidReq = false;
				 addToRow = true;
				 masterErrorLists.add(masterErrorListObj);
			 }
		 } else {
			 masterErrorListObj = new MasterErrorList();
			 masterErrorListObj.setErrorCode(PretupsErrorCodesI.NULL_OR_INVALID_VALUE_IN_REQ);
			 String[] args = {"currentStatus"};
			 masterErrorListObj.setErrorMsg(getMessage(locale, PretupsErrorCodesI.NULL_OR_INVALID_VALUE_IN_REQ, args));
			 isValidReq = false;
			 addToRow = true;
			 masterErrorLists.add(masterErrorListObj);
		 }
		 if(addToRow) {
			 rowErrorMsgListsObj2 = new RowErrorMsgLists();
			 rowErrorMsgListsObj2.setMasterErrorList(masterErrorLists);
			 rowErrorMsgLists.add(rowErrorMsgListsObj2);
		 }
		 
		 //remarks : non-mandatory
		 addToRow = false;
		 masterErrorLists = new ArrayList<>();
		 if (!BTSLUtil.isNullString(o2CDataAppRequest.getRemarks())) {
			 if(o2CDataAppRequest.getRemarks().length() > 100) {
				 masterErrorListObj = new MasterErrorList();
				 masterErrorListObj.setErrorCode("pretups.jsp.messaage.textareacharsaremorethanmax");
				 String[] args = {"Approval remarks", "100"};
				 masterErrorListObj.setErrorMsg(getMessage(locale, "pretups.jsp.messaage.textareacharsaremorethanmax", args));
				 isValidReq = false;
				 masterErrorLists.add(masterErrorListObj);
			 }
		 } 
		 if(addToRow) {
			 rowErrorMsgListsObj2 = new RowErrorMsgLists();
			 rowErrorMsgListsObj2.setMasterErrorList(masterErrorLists);
			 rowErrorMsgLists.add(rowErrorMsgListsObj2);
		  }

		 //setting inner row and return
		 addToRow = false;
		 if(!isValidReq) {
			 rowErrorMsgList.setRowErrorMsgLists(rowErrorMsgLists);
			 rowError.add(rowErrorMsgList);
			 return isValidReq;
		 }
		 
		final ChannelTransferVO channelTransferVO = new ChannelTransferVO();
		this.loadChannelTransferVO(con, theForm, channelTransferVO);
		
		 
		//currentStatus 
		 addToRow = false;
		 masterErrorLists = new ArrayList<>();
		if ( theForm.getUserID().equals(channelTransferVO.getToUserID()) ) //valiadtion for ext transaction number 
		{
			if(!BTSLUtil.isNullorEmpty(channelTransferVO.getStatus()) && !channelTransferVO.getStatus().equals(o2CDataAppRequest.getCurrentStatus())) 
			{  // checking for current status
				 masterErrorListObj = new MasterErrorList();
				 masterErrorListObj.setErrorCode(PretupsErrorCodesI.NULL_OR_INVALID_VALUE_IN_REQ);
				 String[] args = {"currentStatus"};
				 masterErrorListObj.setErrorMsg(getMessage(locale, PretupsErrorCodesI.NULL_OR_INVALID_VALUE_IN_REQ, args));
				 isValidReq = false;
				 addToRow = true;
				 masterErrorLists.add(masterErrorListObj);
			}
		 } else
		 {
			 masterErrorListObj = new MasterErrorList();
			 masterErrorListObj.setErrorCode(PretupsErrorCodesI.NULL_TRANSACTION_DETAILS);
			 String[] args = { o2CDataAppRequest.getExtTxnNumber() };
			 masterErrorListObj.setErrorMsg(getMessage(locale, PretupsErrorCodesI.NULL_TRANSACTION_DETAILS, args));
			 isValidReq = false;
			 addToRow = true;
			 masterErrorLists.add(masterErrorListObj);
		 }
		if(addToRow) {
			 rowErrorMsgListsObj2 = new RowErrorMsgLists();
			 rowErrorMsgListsObj2.setMasterErrorList(masterErrorLists);
			 rowErrorMsgLists.add(rowErrorMsgListsObj2);
		}
		
		 //setting inner row
		 if(!isValidReq) {
			 rowErrorMsgList.setRowErrorMsgLists(rowErrorMsgLists);
			 rowError.add(rowErrorMsgList);
			 return isValidReq;
		 }
		theForm.setChannelTransferVO(channelTransferVO);
		return isValidReq;
	}
	
	/**
	 * 
	 * @param form
	 * @throws Exception 
	 */
	public void transferApprovalConfirmation(  ChannelTransferApprovalForm theForm, String transferId, O2CDataStApp o2CDataAppRequest) throws Exception 
	{
		final String methodName = "transferApprovalConfirmation";
		if (_log.isDebugEnabled()) {
			_log.debug("transferApprovalConfirmation", "Entered");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			ChannelTransferVO channelTransferVO = theForm.getChannelTransferVO();
	
			// throw exception
			this.validateUserInformation(con, theForm, channelTransferVO.getToUserID());  //receiver
			
			//construct formVO
			theForm.setProductType(((LookupsVO) LookupsCache.getObject(PretupsI.PRODUCT_TYPE, channelTransferVO.getProductType())).getLookupName());  //check it's values

			theForm.setPaymentInstrumentList(LookupsCache.loadLookupDropDown(PretupsI.PAYMENT_INSTRUMENT_TYPE, true));  // check the values
			if (theForm.getShowPaymentDetails()) {  

				if (!BTSLUtil.isNullString(channelTransferVO.getPayInstrumentType())) {
					theForm.setPaymentInstrumentName(((LookupsVO) LookupsCache.getObject(PretupsI.PAYMENT_INSTRUMENT_TYPE, channelTransferVO.getPayInstrumentType()))
							.getLookupName());
				}
				theForm.setPaymentInstrumentCode(channelTransferVO.getPayInstrumentType());
				if( channelTransferVO.getPayInstrumentNum() != null && BTSLUtil.isNullorEmpty(theForm.getPaymentInstNum()) ) {
					theForm.setPaymentInstNum(channelTransferVO.getPayInstrumentNum());
				}
				
				if ( channelTransferVO.getPayInstrumentDate() != null && BTSLUtil.isNullorEmpty(theForm.getPaymentInstrumentDate()) ) 
				{
					theForm.setPaymentInstrumentDate(BTSLUtil.getDateStringFromDate(channelTransferVO.getPayInstrumentDate()));
				}
			}
			theForm.setFirstApprovalLimit(String.valueOf(channelTransferVO.getFirstApproverLimit()));  // imp
			theForm.setSecondApprovalLimit(String.valueOf(channelTransferVO.getSecondApprovalLimit()));   //imp
			theForm.setPrimaryTxnNum(channelTransferVO.getUserMsisdn());
			//end
			
			if (SystemPreferences.SECONDARY_NUMBER_ALLOWED)   // required
			{
				final UserDAO userDAO = new UserDAO();
				UserPhoneVO phoneVO = userDAO.loadUserAnyPhoneVO(con, theForm.getPrimaryTxnNum());   //check it
				if ("Y".equalsIgnoreCase(phoneVO.getPrimaryNumber())) 
				{
					theForm.setToPrimaryMSISDN(phoneVO.getMsisdn());
					theForm.setPrimaryNumber(true);
				} else 
				{
					phoneVO = userDAO.loadUserPhoneVO(con, phoneVO.getUserId());
					theForm.setToPrimaryMSISDN(phoneVO.getMsisdn());
					theForm.setPrimaryNumber(false);
				}
			}
			
			if("T".equals(channelTransferVO.getTransferSubType()))
			{
				final ArrayList itemsList = ChannelTransferBL.loadChannelTransferItemsWithBalances(
						con, channelTransferVO.getTransferID(), channelTransferVO.getNetworkCode(),
						channelTransferVO.getNetworkCodeFor(), channelTransferVO.getToUserID());
				
				if (itemsList != null && !itemsList.isEmpty()) 
				{
					if( PretupsI.O2C_APPROVE.equalsIgnoreCase(o2CDataAppRequest.getStatus())) {

						//md.sohail
						double approvedQuantity =0, requestedQuantity = 0;
						long requestedQuantityLong =0;
						ArrayList<O2CProductAppr> productDetails = (ArrayList) o2CDataAppRequest.getProducts();
						ChannelTransferItemsVO channelTransferItemsVO1 = null;
						for(int i=0; i< productDetails.size(); i++) 
						{
							O2CProductAppr o2cProductAppr = productDetails.get(i);
							for(int j = 0; j< itemsList.size(); j++) 
							{  
								channelTransferItemsVO1 = (ChannelTransferItemsVO) itemsList.get(j);
								approvedQuantity = BTSLUtil.getDisplayAmount((channelTransferItemsVO1.getApprovedQuantity()));
								if(channelTransferItemsVO1.getProductCode().equals(o2cProductAppr.getProductCode())) 
								{
									if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(theForm.getCurrentApprovalLevel())) 
									{   
										if(!BTSLUtil.isNullorEmpty(o2cProductAppr.getAppQuantity()) ) 
										{
											requestedQuantity = Double.parseDouble(o2cProductAppr.getAppQuantity());
											requestedQuantityLong = Long.parseLong(o2cProductAppr.getAppQuantity());
											if(requestedQuantity <= approvedQuantity) 
											{
												channelTransferItemsVO1.setFirstApprovedQuantity(String.valueOf(requestedQuantityLong));
											} else 
											{
												String[] args = {String.valueOf(requestedQuantity), String.valueOf(approvedQuantity), channelTransferItemsVO1.getProductCode()};
												throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.APPR_QUANTITY_MORE, args);
											}
											
										} else 
										{
											channelTransferItemsVO1.setFirstApprovedQuantity(String.valueOf(approvedQuantity));
										}
									}
									if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(theForm.getCurrentApprovalLevel())) 
									{
										if(!BTSLUtil.isNullorEmpty(o2cProductAppr.getAppQuantity()) ) 
										{
											requestedQuantity = Double.parseDouble(o2cProductAppr.getAppQuantity());
											requestedQuantityLong = Long.parseLong(o2cProductAppr.getAppQuantity());
											if(requestedQuantity <= approvedQuantity) 
											{
												channelTransferItemsVO1.setSecondApprovedQuantity(String.valueOf(requestedQuantityLong));
											} else {
												String[] args = {String.valueOf(requestedQuantity), String.valueOf(approvedQuantity), channelTransferItemsVO1.getProductCode()};
												throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.APPR_QUANTITY_MORE, args);
											}
											
										} else {
											channelTransferItemsVO1.setSecondApprovedQuantity(String.valueOf(approvedQuantity));
										}
									}
									if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(theForm.getCurrentApprovalLevel())) 
									{
										if(!BTSLUtil.isNullorEmpty(o2cProductAppr.getAppQuantity()) ) 
										{
											requestedQuantity = Double.parseDouble(o2cProductAppr.getAppQuantity());
											requestedQuantityLong = Long.parseLong(o2cProductAppr.getAppQuantity());
											if(requestedQuantity <= approvedQuantity) 
											{
												channelTransferItemsVO1.setThirdApprovedQuantity(String.valueOf(requestedQuantityLong));
											} else 
											{
												String[] args = {String.valueOf(requestedQuantity), String.valueOf(approvedQuantity), channelTransferItemsVO1.getProductCode()};
												throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.APPR_QUANTITY_MORE, args);
											}
											
										} else {
											channelTransferItemsVO1.setThirdApprovedQuantity(String.valueOf(approvedQuantity));
										}
									}
								}
								
							}
							
						} //product details check end
					}  //O2C_APPROVE condition end
				} //itemList
				else {
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NULL_TRANSACTION_DETAILS);  // item list for the transaction id is empty
				}
                
				theForm.setTransferItemList(itemsList);
			} else {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_TRANSFER_ID); //only occur if passed transfer id is not a stock transfer id
			}
			
		} catch (BTSLBaseException be) {
			_log.error("transferApprovalConfirmation", "BTSLBaseException:e=" + be);
			_log.errorTrace(methodName, be);
			throw be;
		} catch (ParseException e) {
			_log.error("transferApprovalConfirmation", "ParseException:e=" + e);
			_log.errorTrace(methodName, e);
			throw e;
		} catch (Exception e) {
			_log.error("transferApprovalConfirmation", "ParseException:e=" + e);
			_log.errorTrace(methodName, e);
			throw e;
		} finally {
			if (mcomCon != null) {
				mcomCon.close("O2CApprovalServiceImpl#transferApprovalConfirmation");
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) {
				_log.debug("transferApprovalConfirmation", "Exiting...");
			}
		}
	}
	
	
	/**
	 * After this user can cancel the order and approve the order and he can go
	 * back screen .
	 * this is handled by this method
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 * @throws Exception 
	 */
	public void processOptToChannelTransfer( ChannelTransferApprovalForm theForm, O2CDataStApp o2CDataAppRequest) throws Exception {

		final String methodName = "processO2CStockApproval";
		if (_log.isDebugEnabled()) 
		{
			_log.debug("processO2CStockApproval", "Entered");
		}
		//ActionForward forward = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		ArrayList itemsList = null;
		ChannelTransferVO p_channelTransferVO = null;
		ChannelTransferItemsVO channelTransferItemsVO = new ChannelTransferItemsVO();
		NetworkProductDAO productDAO= new NetworkProductDAO();
		ProductVO productVO = new ProductVO();	
		CommissionProfileDAO commissionProfileDAO = new CommissionProfileDAO();
		try 
		{
			locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
			if ( PretupsI.O2C_APPROVE.equalsIgnoreCase( o2CDataAppRequest.getStatus() ) ) 
			{
				theForm.getChannelTransferVO().setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_NEW);

			} else if( PretupsI.O2C_REJECT.equalsIgnoreCase( o2CDataAppRequest.getStatus() ) )
			{
				theForm.getChannelTransferVO().setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
			} else {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.O2C_APPROVE_OR_REJECT);
			}
			
			if ( PretupsI.O2C_APPROVE.equalsIgnoreCase( o2CDataAppRequest.getStatus() ) ) //should enter
			{
				mcomCon = new MComConnection();
				con = mcomCon.getConnection();

				if ( (!BTSLUtil.isNullString(o2CDataAppRequest.getExtTxnNumber()) && PretupsI.YES.equals(theForm.getExternalTxnMandatory()))
						|| (!BTSLUtil.isNullString(o2CDataAppRequest.getExtTxnNumber())
								&& PretupsI.YES.equals(theForm.getExternalTxnExist()))) 
				{
					if (SystemPreferences.EXTERNAL_TXN_NUMERIC) 
					{//enter
						try 
						{
							final long externalTxnIDLong = Long.parseLong(o2CDataAppRequest.getExtTxnNumber());
							if (externalTxnIDLong < 0) 
							{
								throw new BTSLBaseException(this, "processOptToChannelTransfer",
										"message.channeltransfer.externaltxnnumbernotnumeric");
							}
							theForm.setExternalTxnNum(String.valueOf(externalTxnIDLong));
						} catch (Exception e) {
							_log.errorTrace(methodName, e);
							throw new BTSLBaseException(this, "processOptToChannelTransfer",
									"message.channeltransfer.externaltxnnumbernotnumeric");
						}
					}

					if (SystemPreferences.EXTERNAL_TXN_UNIQUE) {
						final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
						final boolean isExternalTxnNotUnique = channelTransferDAO.isExtTxnExists(con,
								theForm.getExternalTxnNum(),  theForm.getTransferNum());
						if (isExternalTxnNotUnique) {
							throw new BTSLBaseException(this, "processOptToChannelTransfer",
									"message.channeltransfer.externaltxnnumbernotunique");
						}
					}
				}

				p_channelTransferVO = theForm.getChannelTransferVO();
				channelTransferItemsVO.setRequestedQuantity(Long.toString(BTSLUtil.parseDoubleToLong(BTSLUtil.getDisplayAmount(p_channelTransferVO.getRequestedQuantity()))));// total quantity
				if (theForm.getTransferNum().equalsIgnoreCase(p_channelTransferVO.getTransferID())) { 
					productVO = productDAO.loadProductCode(con, p_channelTransferVO.getProductType(),
							PretupsI.C2S_MODULE);
					channelTransferItemsVO.setProductCode(productVO.getProductCode());
				}

				double quantity = 0,quantity1=0;
				for (int i = 0, k = theForm.getTransferItemList().size(); i < k; i++) {
					channelTransferItemsVO = (ChannelTransferItemsVO) theForm.getTransferItemList().get(i);
					if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(theForm.getCurrentApprovalLevel())) 
					{
						if (!BTSLUtil.isNullString(channelTransferItemsVO.getFirstApprovedQuantity())
								&& !channelTransferItemsVO.getFirstApprovedQuantity().equalsIgnoreCase("0")) 
						{
							quantity = Double.parseDouble(channelTransferItemsVO.getFirstApprovedQuantity());
						} else {
							quantity = Double.parseDouble(channelTransferItemsVO.getInitialRequestedQuantityStr());
							channelTransferItemsVO.setFirstApprovedQuantity(String.valueOf(quantity));
						}
					} else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(theForm.getCurrentApprovalLevel())) {
						if (!BTSLUtil.isNullString(channelTransferItemsVO.getSecondApprovedQuantity())                  
								&& !channelTransferItemsVO.getSecondApprovedQuantity().equalsIgnoreCase("0")) 
						{
							quantity = Double.parseDouble(channelTransferItemsVO.getSecondApprovedQuantity());
						} else {
							quantity = Double.parseDouble(channelTransferItemsVO.getApprovedQuantityAsString());
							channelTransferItemsVO.setSecondApprovedQuantity(String.valueOf(quantity));
						}
					} else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(theForm.getCurrentApprovalLevel())) 
					{
						if (!BTSLUtil.isNullString(channelTransferItemsVO.getThirdApprovedQuantity())
								&& !channelTransferItemsVO.getThirdApprovedQuantity().equalsIgnoreCase("0")) 
						{
							quantity = Double.parseDouble(channelTransferItemsVO.getThirdApprovedQuantity());
						} else 
						{
							quantity = Double.parseDouble(channelTransferItemsVO.getApprovedQuantityAsString());
							channelTransferItemsVO.setThirdApprovedQuantity(String.valueOf(quantity));
						}
					}
					channelTransferItemsVO.setRequiredQuantity(Math.round(quantity));
					channelTransferItemsVO.setRequestedQuantity(new DecimalFormat("#############.###").format(quantity));

					quantity1+=quantity;
				}

				

				final ArrayList<ChannelTransferItemsVO> itemsList1 = new ArrayList<ChannelTransferItemsVO>();
				itemsList1.add(channelTransferItemsVO);  //slab 2

				p_channelTransferVO.setTransferMRP(PretupsBL.getSystemAmount(Math.round(quantity1)));

				p_channelTransferVO.setChannelTransferitemsVOList(itemsList1);
				itemsList = theForm.getTransferItemList();  //slab 1
				String type = (SystemPreferences.TRANSACTION_TYPE_ALWD) ? PretupsI.TRANSFER_TYPE_O2C : PretupsI.ALL;
				String paymentMode = (SystemPreferences.TRANSACTION_TYPE_ALWD && SystemPreferences.PAYMENT_MODE_ALWD)
						? p_channelTransferVO.getPayInstrumentType() : PretupsI.ALL;
				//size 1
				ArrayList list = commissionProfileDAO.loadProductListWithTaxes(con,
						p_channelTransferVO.getCommProfileSetId(), p_channelTransferVO.getCommProfileVersion(),
						p_channelTransferVO.getChannelTransferitemsVOList(), type, paymentMode);
				if (!((ChannelTransferItemsVO) list.get(0)).isSlabDefine()) 
				{
					throw new BTSLBaseException(this, "processOptToChannelTransfer",
							"message.channeltransfer.approvedquantity.notInSlab");
				}
				theForm.setCloseTransaction(false);
				if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(theForm.getCurrentApprovalLevel())) 
				{
					if (p_channelTransferVO.getTransferMRP() <= p_channelTransferVO.getFirstApproverLimit()) //100 coming insteam of 110
					{ 
						theForm.setCloseTransaction(true);
					}
				}
				else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(theForm.getCurrentApprovalLevel())) 
				{
					if (p_channelTransferVO.getTransferMRP() <= p_channelTransferVO.getSecondApprovalLimit()) 
					{
						theForm.setCloseTransaction(true);
					}
				} 
				else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(theForm.getCurrentApprovalLevel())) 
				{
					theForm.setCloseTransaction(true);
				}
				if (theForm.isCloseTransaction() || theForm.getReconcilationFlag()) 
				{
					if ((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,
							p_channelTransferVO.getNetworkCode())) {
						ChannelTransferBL.increaseOptOTFCounts(con, p_channelTransferVO);
						theForm.setUserOTFCountsVO(p_channelTransferVO.getUserOTFCountsVO());
						p_channelTransferVO.setOtfFlag(true);
					}
				}
				//me
				theForm.setChannelTransferVO(p_channelTransferVO);

				ChannelTransferBL.calculateMRPWithTaxAndDiscount(theForm.getTransferItemList(), PretupsI.TRANSFER_TYPE_O2C);
				ChannelTransferBL.calculateTotalMRPFromTaxAndDiscount(theForm.getTransferItemList(),PretupsI.TRANSFER_TYPE_O2C, theForm.getCurrentApprovalLevel(), p_channelTransferVO);
				((ChannelTransferItemsVO) itemsList.get(0))
						.setNetPayableAmountApproval(channelTransferItemsVO.getNetPayableAmount());
				((ChannelTransferItemsVO) itemsList.get(0))
						.setPayableAmountApproval(channelTransferItemsVO.getPayableAmount());
				double payableAmt = 0L, netPayableAmt = 0L;
				long mrpAmt = 0L, totMRP = 0L, totPayableAmt = 0L, totnetPayableAmt = 0L;
				double commissionQty = 0, senderDebitQty = 0, receiverCreditQty = 0, commissionValue = 0;
				double frstAppQty = 0, totFrstAppQty = 0L, secAppQty = 0, totSecAppQty = 0L, thrAppQty = 0,
						totThrAppQty = 0L;
				long otfValue = 0;
				long tax1Value = 0;
				long tax2Value = 0;
				long tax3Value = 0;

				if (itemsList != null && !itemsList.isEmpty()) 
				{
					for (int i = 0, j = itemsList.size(); i < j; i++) 
					{
						channelTransferItemsVO = (ChannelTransferItemsVO) itemsList.get(i);
						mrpAmt = channelTransferItemsVO.getProductTotalMRP();
						channelTransferItemsVO.setProductMrpStr(PretupsBL.getDisplayAmount(mrpAmt));
						payableAmt = channelTransferItemsVO.getPayableAmount();
						netPayableAmt = channelTransferItemsVO.getNetPayableAmount();
						// code for o2c transfer quantity change by amit
						// 28-May-2009
						if (!BTSLUtil.isNullString(channelTransferItemsVO.getFirstApprovedQuantity())) 
						{
							frstAppQty = Double.parseDouble(channelTransferItemsVO.getFirstApprovedQuantity());
							channelTransferItemsVO.setFirstApprovedQuantity(
									new DecimalFormat("#############.###").format(frstAppQty));
						}
						if (!BTSLUtil.isNullString(channelTransferItemsVO.getSecondApprovedQuantity())) 
						{
							secAppQty = Double.parseDouble(channelTransferItemsVO.getSecondApprovedQuantity());
							channelTransferItemsVO.setSecondApprovedQuantity(
									new DecimalFormat("#############.###").format(secAppQty));
						}
						if (!BTSLUtil.isNullString(channelTransferItemsVO.getThirdApprovedQuantity())) 
						{
							thrAppQty = Double.parseDouble(channelTransferItemsVO.getThirdApprovedQuantity());
							channelTransferItemsVO
									.setThirdApprovedQuantity(new DecimalFormat("#############.###").format(thrAppQty));
						}
						totFrstAppQty += frstAppQty;
						totSecAppQty += secAppQty;
						totThrAppQty += thrAppQty;
						totMRP += mrpAmt;
						totPayableAmt += payableAmt;
						totnetPayableAmt += netPayableAmt;
						commissionQty += channelTransferItemsVO.getCommQuantity();
						senderDebitQty += channelTransferItemsVO.getSenderDebitQty();
						receiverCreditQty += channelTransferItemsVO.getReceiverCreditQty();
						commissionValue += channelTransferItemsVO.getCommValue();
						otfValue += channelTransferItemsVO.getOtfAmount();
						tax1Value += channelTransferItemsVO.getTax1Value();
						tax2Value += channelTransferItemsVO.getTax2Value();
						tax3Value += channelTransferItemsVO.getTax3Value();

					}
				}
				if (_log.isDebugEnabled()) {
					_log.debug("processOptToChannelTransfer", "Entered totPayableAmt " + totPayableAmt);
				}
				theForm.setPayableAmount(PretupsBL.getDisplayAmount(totPayableAmt));
				theForm.setPayableAmountApproval(PretupsBL.getDisplayAmount(totPayableAmt));
				if (_log.isDebugEnabled()) 
				{
					_log.debug("processOptToChannelTransfer", "Entered PretupsBL.getDisplayAmount(totPayableAmt)"
							+ PretupsBL.getDisplayAmount(totPayableAmt));
				}
				if (_log.isDebugEnabled()) {
					_log.debug("processOptToChannelTransfer", "Entered totnetPayableAmt" + totnetPayableAmt);
				}
				theForm.setNetPayableAmount(PretupsBL.getDisplayAmount(totnetPayableAmt));
				theForm.setNetPayableAmountApproval(PretupsBL.getDisplayAmount(totnetPayableAmt));
				if (_log.isDebugEnabled()) {
					_log.debug("processOptToChannelTransfer", "Entered PretupsBL.getDisplayAmount(totnetPayableAmt)"
							+ PretupsBL.getDisplayAmount(totnetPayableAmt));
				}
				if (_log.isDebugEnabled()) {
					_log.debug("processOptToChannelTransfer", "Entered totnetPayableAmt" + totnetPayableAmt);
				}
				theForm.setPaymentInstrumentAmt(PretupsBL.getDisplayAmount(totnetPayableAmt));
				if (_log.isDebugEnabled()) {
					_log.debug("processOptToChannelTransfer", "Entered PretupsBL.getDisplayAmount(totnetPayableAmt)"
							+ PretupsBL.getDisplayAmount(totnetPayableAmt));
				}
				if (_log.isDebugEnabled()) {
					_log.debug("processOptToChannelTransfer", "Entered totMRP" + totMRP);
				}
				theForm.setTotalMRP(PretupsBL.getDisplayAmount(totMRP));
				if (_log.isDebugEnabled()) {
					_log.debug("processOptToChannelTransfer",
							"Entered PretupsBL.getDisplayAmount(totMRP)" + PretupsBL.getDisplayAmount(totMRP));
				}
				theForm.setFirstLevelApprovedQuantity(new DecimalFormat("#############.###").format(totFrstAppQty));
				theForm.setSecondLevelApprovedQuantity(new DecimalFormat("#############.###").format(totSecAppQty));
				theForm.setThirdLevelApprovedQuantity(new DecimalFormat("#############.###").format(totThrAppQty));
				theForm.setTransferItemList(itemsList);
				theForm.setValidatePaymentInstruments(true);
				theForm.setCommissionQuantity(PretupsBL.getDisplayAmount(commissionQty));
				theForm.setSenderDebitQuantity(PretupsBL.getDisplayAmount(senderDebitQty));
				theForm.setReceiverCreditQuantity(PretupsBL.getDisplayAmount(receiverCreditQty));
				theForm.setTotalComm(PretupsBL.getDisplayAmount(commissionValue));
				theForm.setTotalCommValue(PretupsBL.getDisplayAmount(commissionValue));
				theForm.setTotalOtfValue(PretupsBL.getDisplayAmount(otfValue));
				theForm.setTotalTax1(PretupsBL.getDisplayAmount(tax1Value));
				theForm.setTotalTax2(PretupsBL.getDisplayAmount(tax2Value));
				theForm.setTotalTax3(PretupsBL.getDisplayAmount(tax3Value));
				if (!BTSLUtil.isNullString(channelTransferItemsVO.getFirstApprovedQuantity())) {
					theForm.setTotalReqQty(Double.toString(totFrstAppQty));
				}
				if (!BTSLUtil.isNullString(channelTransferItemsVO.getSecondApprovedQuantity())) {
					theForm.setTotalReqQty(Double.toString(totSecAppQty));
				}
				if (!BTSLUtil.isNullString(channelTransferItemsVO.getThirdApprovedQuantity())) {
					theForm.setTotalReqQty(Double.toString(totThrAppQty));
				}

				if (theForm.getShowPaymentDetails()) 
				{
					theForm.setPaymentInstrumentName(BTSLUtil.getOptionDesc(theForm.getPaymentInstrumentCode(), theForm.getPaymentInstrumentList()).getLabel());
				}
			} 
			else 
			{ //rejectd
				 theForm.setRejectOrder("Y");
				double firAppQty = 0, secAppQty = 0, thrAppQty = 0;  //pass form here
				for (int i = 0, j = ((ChannelTransferApprovalForm) theForm).getTransferItemList().size(); i < j; i++) 
				{
					channelTransferItemsVO = (ChannelTransferItemsVO) ((ChannelTransferApprovalForm) theForm)
							.getTransferItemList().get(i);
					if (!BTSLUtil.isNullString(channelTransferItemsVO.getFirstApprovedQuantity())) 
					{
						firAppQty += Double.parseDouble(channelTransferItemsVO.getFirstApprovedQuantity());
					}
					if (!BTSLUtil.isNullString(channelTransferItemsVO.getSecondApprovedQuantity())) 
					{
						secAppQty += Double.parseDouble(channelTransferItemsVO.getSecondApprovedQuantity());
					}
					if (!BTSLUtil.isNullString(channelTransferItemsVO.getThirdApprovedQuantity())) 
					{
						thrAppQty += Double.parseDouble(channelTransferItemsVO.getThirdApprovedQuantity());
					}
				}
				((ChannelTransferApprovalForm) theForm).setFirstLevelApprovedQuantity(String.valueOf(firAppQty));
				((ChannelTransferApprovalForm) theForm).setSecondLevelApprovedQuantity(String.valueOf(secAppQty));
				((ChannelTransferApprovalForm) theForm).setThirdLevelApprovedQuantity(String.valueOf(thrAppQty));
				//forward = mapping.findForward("confirmapproval");
			}
			
		} catch (Exception e) {
			try {
				if(con!=null)
				{
					mcomCon.finalRollback();
				}
			} catch (SQLException e1) {	
				_log.errorTrace(methodName, e1);
			}
			_log.error("transferApprovalConfirmation", "Exception:e=" + e);
			_log.errorTrace(methodName, e);
			throw e;
		} finally {
			if (mcomCon != null) {
				mcomCon.close("O2CApprovalServiceImpl#processOptToChannelTransfer");
				mcomCon = null;
			}

			/*if (_log.isDebugEnabled()) {
				_log.debug("processOptToChannelTransfer", "Exited forwad=" + forward);
			}*/
		}
	}
	
	/**
	 * 
	 * @param p_con
	 * @param theForm
	 * @param userID
	 * @throws Exception
	 */
	private void validateUserInformation(Connection p_con, ChannelTransferApprovalForm theForm, String userID) throws Exception {  //sub-method

		if (_log.isDebugEnabled()) {
			_log.debug("validateUserInformation", "Entered userID = " + userID);
		}
		int receiverStatusAllowed = 0;
		final String methodName = "validateUserInformation";
		try {
			final ChannelUserDAO channelUserDAO = new ChannelUserDAO();

			final Date curDate = new Date();
			final ChannelUserVO channelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(p_con, userID, false, curDate,false);  //2395
			if (channelUserVO == null) 
			{
				throw new BTSLBaseException(this, methodName, "channeltransfer.approval.msg.userdetailnotfound");
			} 
			else 
			{
				final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(channelUserVO.getNetworkID(), channelUserVO.getCategoryCode(), 
						channelUserVO.getUserType(), PretupsI.REQUEST_SOURCE_TYPE_WEB);
				if (userStatusVO != null) 
				{
					final String userStatusAllowed = userStatusVO.getUserReceiverAllowed();
					final String status[] = userStatusAllowed.split(",");
					for (int i = 0; i < status.length; i++) {
						if (status[i].equals(channelUserVO.getStatus())) 
						{
							receiverStatusAllowed = 1;
						}
					}
				} 
				else 
				{
					throw new BTSLBaseException(this, methodName, "channeltransfer.approval.msg.usersuspended");
				}
				theForm.setChannelUserStatus(channelUserVO.getStatus()); // /
				
			}

			if (receiverStatusAllowed == 0) 
			{
				throw new BTSLBaseException(this, methodName, "channeltransfer.approval.msg.usersuspended");
			} 
			else if (channelUserVO.getCommissionProfileApplicableFrom().after(curDate)) 
			{
				throw new BTSLBaseException(this, methodName, "channeltransfer.approval.msg.usernocommprofileapplicable");
			} 
			else if (!PretupsI.YES.equals(channelUserVO.getCommissionProfileStatus())) 
			{
				final Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
				String args[] = null;
				args = new String[] { channelUserVO.getCommissionProfileLang2Msg() };
				final LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
				if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) 
				{
					args = new String[] { channelUserVO.getCommissionProfileLang1Msg() };
				}
				
				throw new BTSLBaseException(this, methodName, "channeltransfer.approval.msg.notactive.commporfile", args);
			} 
			else if (!PretupsI.YES.equals(channelUserVO.getTransferProfileStatus())) 
			{
				throw new BTSLBaseException(this, methodName, "channeltransfer.approval.msg.notactive.transferprofile");
			}

			// to check user status
			if (channelUserVO.getInSuspend() != null && PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(channelUserVO.getInSuspend())) 
			{
				throw new BTSLBaseException(this, methodName, "channeltransfer.approval.msg.usersuspended");
			}
			theForm.setDomainTypeCode(channelUserVO.getDomainTypeCode());
		} catch (BTSLBaseException be) 
		{
			_log.error("validateUserInformation", "Exception:be=" + be);
			_log.errorTrace(methodName, be);
			throw be;
		}
		catch (Exception e) {
			_log.error("validateUserInformation", "Exception:e=" + e);
			_log.errorTrace(methodName, e);
			throw e;
		} finally {
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exiting method:=" + methodName);
			}
		}
	}

	/**
	 * This method is called to approve or reject stock transfer
	 * 
	 * @param form
	 * @param p_channelUserVO
	 * @param isConfirm
	 * @throws Exception 
	 */
	public void confirmOptToChannelTransfer( ChannelTransferApprovalForm theForm, String approveOrReject, ChannelUserVO p_channelUserVO) throws Exception {
		final String methodName = "confirmOptToChannelTransfer";
		if (_log.isDebugEnabled()) {
			_log.debug("confirmOptToChannelTransfer", "Entered");
		}
		//ActionForward forward = null;
		((ChannelTransferApprovalForm) theForm).setRejectOrder(null);
		
		try 
		{
			if ( PretupsI.O2C_APPROVE.equalsIgnoreCase(approveOrReject)) 
			{
				orderApproval( theForm, p_channelUserVO );
			} else
			{	
				cancelOrder( theForm, p_channelUserVO );
			} 
		} catch (Exception e) 
		{
			_log.error(methodName, "Exception:e=" + e);
			_log.errorTrace(methodName, e);
			throw e;

		} /*finally {
			if (_log.isDebugEnabled()) {
				_log.debug("confirmOptToChannelTransfer", "Exited forward:=" + forward);
			}
		}*/
		
	}
	
	/**
	 *  For order approval.
	 * 
	 * This method handles all level of approval and also approve the order
	 * 
	 * @param theForm
	 * @param p_channelUserVO: sessionUserVO
	 * @throws BTSLBaseException
	 * @throws ParseException
	 * @throws SQLException
	 */
	private void orderApproval( ChannelTransferApprovalForm theForm, ChannelUserVO p_channelUserVO) throws BTSLBaseException, ParseException, SQLException {
		final String methodName = "orderApproval";
		if (_log.isDebugEnabled()) {
			_log.debug("orderApproval", "Entered");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		String forwardPath = "approve1";
		String domainCode = null;
		boolean _receiverMessageSendReq=false;
		boolean _ussdReceiverMessageSendReq=false;
		
		String Status = null;
		OperatorUtilI operatorUtili = null;
		ChannelUserWebDAO channelUserWebDAO = null;
		try {
			final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
			operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
		} catch (Exception e) {
			_log.errorTrace("orderApproval", e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferApprovalAction[orderApproval]", "", "",
					"", "Exception while loading the class at the call:" + e.getMessage());
		}
		final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
		final UserDAO userDAO = new UserDAO();
		try {
			channelUserWebDAO = new ChannelUserWebDAO();
			final Date date = new Date();
			//final ChannelTransferApprovalForm theForm = (ChannelTransferApprovalForm) form;
			final ChannelUserVO channelUserVO = (ChannelUserVO) p_channelUserVO; //NGBC0000001564     //check in old code
            final ChannelTransferVO channelTransferVOTemp = theForm.getChannelTransferVO(); // transferID  OT201031.0525.100001,
			ChannelTransferVO channelTransferVO = null;		
			channelTransferVO = new ChannelTransferVO(channelTransferVOTemp);
			channelTransferVO.setUserOTFCountsVO(theForm.getUserOTFCountsVO());
			
			boolean sendOrderToApproval = false;
			String _serviceType=PretupsI.SERVICE_TYPE_CHNL_O2C_INTR;
			_receiverMessageSendReq=((Boolean)PreferenceCache.getControlPreference(PreferenceI.REC_MSG_SEND_ALLOW,channelUserVO.getNetworkCode(),_serviceType)).booleanValue(); //true

			_ussdReceiverMessageSendReq=((Boolean)PreferenceCache.getControlPreference(PreferenceI.USSD_REC_MSG_SEND_ALLOW,channelUserVO.getNetworkCode(),_serviceType)).booleanValue();//false

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			domainCode = channelTransferVO.getDomainCode(); 
			Status = channelTransferDAO.getStatusOfDomain(con, domainCode); 
			if ("N".equals(Status)) 
			{
				throw new BTSLBaseException(this, methodName, "O2C.approval.error.invalidDomain");

			}
			
			// it means onces jsp forward to another action. then from forwarded
			// action where we will forward it further.
			String nextForwardPath = "viewlistlevelone";

			String message = "channeltransfer.approval.levelone.msg.success";
			String failLevel = "one";
			if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(theForm.getCurrentApprovalLevel())) 
			{
				channelTransferVO.setFirstApprovalRemark(theForm.getApprove1Remark()); 
				channelTransferVO.setFirstApprovedBy(channelUserVO.getUserID());  
				channelTransferVO.setFirstApprovedOn(date);
				if(!theForm.getReconcilationFlag())
				{
					channelTransferVO.setStatus(theForm.getCurrentApprovalLevel());
					if(PretupsI.PAYMENT_INSTRUMENT_TYPE_ONLINE.equalsIgnoreCase(theForm.getPaymentInstrumentCode()) && 
							PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equalsIgnoreCase(channelTransferVO.getTransferSubType())) 
					{
						sendOrderToApproval = true;
						channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
					}
				}
				else
				{
					if(channelTransferVO.getStatus().equals(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE) || 
							channelTransferVO.getStatus().equals(PretupsI.CHANNEL_TRANSFER_ORDER_NEW))
					{
						channelTransferVO.setPayInstrumentStatus("PAID");
						message = "channeltransfer.success.reconciliation.msg.success";
						
					}
					else
					{ 
						channelTransferVO.setPayInstrumentStatus("REJECT");
						message = "channeltransfer.fail.reconciliation.msg.success";
					}
				}

				// for o2c transfer quantity change
				channelTransferVO.setLevelOneApprovedQuantity(theForm.getFirstLevelApprovedQuantity()); 
				channelTransferVO.setPayableAmount(PretupsBL.getSystemAmount(theForm.getPayableAmount())); 
				channelTransferVO.setNetPayableAmount(PretupsBL.getSystemAmount(theForm.getNetPayableAmount()));
				channelTransferVO.setPayInstrumentAmt(PretupsBL.getSystemAmount(theForm.getPaymentInstrumentAmt()));
				channelTransferVO.setTransferMRP(PretupsBL.getSystemAmount(theForm.getTotalMRP())); 
				channelTransferVO.setTotalTax1(PretupsBL.getSystemAmount(theForm.getTotalTax1())); 
				channelTransferVO.setTotalTax2(PretupsBL.getSystemAmount(theForm.getTotalTax2()));
				channelTransferVO.setTotalTax3(PretupsBL.getSystemAmount(theForm.getTotalTax3()));
				if(PretupsI.YES.equals(theForm.getExternalTxnMandatory())){
				channelTransferVO.setExternalTxnDate(BTSLUtil.getDateFromDateString(theForm.getExternalTxnDate()));
				channelTransferVO.setExternalTxnNum(theForm.getExternalTxnNum());
			
			}
				
				channelTransferVO.setPayInstrumentNum(theForm.getPaymentInstNum());
				if (channelTransferVO.getTransferMRP() <= channelTransferVO.getFirstApproverLimit()) { 
					sendOrderToApproval = true;
				}
			} else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(theForm.getCurrentApprovalLevel())) {
				channelTransferVO.setSecondApprovalRemark(theForm.getApprove2Remark());
				channelTransferVO.setSecondApprovedBy(channelUserVO.getUserID());
				channelTransferVO.setSecondApprovedOn(date);
				if(!theForm.getReconcilationFlag()){
					channelTransferVO.setStatus(theForm.getCurrentApprovalLevel());
				}
				forwardPath = "approve2";
				message = "channeltransfer.approval.leveltwo.msg.success";
				nextForwardPath = "viewlistleveltwo";
				// for o2c transfer quantity change
				channelTransferVO.setLevelOneApprovedQuantity(theForm.getFirstLevelApprovedQuantity());
				channelTransferVO.setLevelTwoApprovedQuantity(theForm.getSecondLevelApprovedQuantity());
				channelTransferVO.setPayableAmount(PretupsBL.getSystemAmount(theForm.getPayableAmount()));
				channelTransferVO.setNetPayableAmount(PretupsBL.getSystemAmount(theForm.getNetPayableAmount()));
				channelTransferVO.setPayInstrumentAmt(PretupsBL.getSystemAmount(theForm.getPaymentInstrumentAmt()));
				channelTransferVO.setTransferMRP(PretupsBL.getSystemAmount(theForm.getTotalMRP()));
				channelTransferVO.setTotalTax1(PretupsBL.getSystemAmount(theForm.getTotalTax1()));
				channelTransferVO.setTotalTax2(PretupsBL.getSystemAmount(theForm.getTotalTax2()));
				channelTransferVO.setTotalTax3(PretupsBL.getSystemAmount(theForm.getTotalTax3()));

				if (channelTransferVO.getTransferMRP() <= channelTransferVO.getSecondApprovalLimit()) {
					sendOrderToApproval = true;
				}
			} else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(theForm.getCurrentApprovalLevel())) {
				channelTransferVO.setThirdApprovalRemark(theForm.getApprove3Remark());
				channelTransferVO.setThirdApprovedBy(channelUserVO.getUserID());
				channelTransferVO.setThirdApprovedOn(date);
				forwardPath = "approve3";
				nextForwardPath = "viewlistlevelthree";
				message = "channeltransfer.approval.msg.success";
				// for o2c transfer quantity change
				channelTransferVO.setLevelOneApprovedQuantity(theForm.getFirstLevelApprovedQuantity());
				channelTransferVO.setLevelTwoApprovedQuantity(theForm.getSecondLevelApprovedQuantity());
				channelTransferVO.setLevelThreeApprovedQuantity(theForm.getThirdLevelApprovedQuantity());
				channelTransferVO.setPayableAmount(PretupsBL.getSystemAmount(theForm.getPayableAmount()));
				channelTransferVO.setNetPayableAmount(PretupsBL.getSystemAmount(theForm.getNetPayableAmount()));
				channelTransferVO.setPayInstrumentAmt(PretupsBL.getSystemAmount(theForm.getPaymentInstrumentAmt()));
				channelTransferVO.setTransferMRP(PretupsBL.getSystemAmount(theForm.getTotalMRP()));
				channelTransferVO.setTotalTax1(PretupsBL.getSystemAmount(theForm.getTotalTax1()));
				channelTransferVO.setTotalTax2(PretupsBL.getSystemAmount(theForm.getTotalTax2()));
				channelTransferVO.setTotalTax3(PretupsBL.getSystemAmount(theForm.getTotalTax3()));
				sendOrderToApproval = true;
			}
			if(PretupsI.PAYMENT_INSTRUMENT_TYPE_ONLINE.equals(theForm.getPaymentInstrumentCode()))
			{
				sendOrderToApproval = true;
			}
			channelTransferVO.setReconciliationFlag(theForm.getReconcilationFlag());
			

			channelTransferVO.setModifiedBy(channelUserVO.getUserID());
			channelTransferVO.setModifiedOn(date);
			// set payment instrument no and date (it may be changed at the time
			// of approval)

			if (theForm.getShowPaymentDetails()) {  //system preference
				channelTransferVO.setPayInstrumentType(theForm.getPaymentInstrumentCode());
				channelTransferVO.setPayInstrumentNum(theForm.getPaymentInstNum());
				channelTransferVO.setPayInstrumentDate(BTSLUtil.getDateFromDateString(theForm.getPaymentInstrumentDate()));
			}

			// set the items list from form to VO
			channelTransferVO.setChannelTransferitemsVOList(theForm.getTransferItemList());
			
		       /*if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,channelTransferVO.getNetworkCode()))
				{
		       	 final ArrayList<ChannelTransferItemsVO> list = new ArrayList<>();
		       	 if(channelTransferVO.getChannelTransferitemsVOListforOTF()!=null && channelTransferVO.getChannelTransferitemsVOList()!=null )
		       	 {
		       		 int channelTransferListSize = channelTransferVO.getChannelTransferitemsVOList().size();
		        for(int i=0; i < channelTransferListSize; i++){
		     	  ChannelTransferItemsVO ctiVO =  (ChannelTransferItemsVO) channelTransferVO.getChannelTransferitemsVOList().get(i);
		     	  ChannelTransferItemsVO ctiOTFVO =  (ChannelTransferItemsVO) channelTransferVO.getChannelTransferitemsVOListforOTF().get(i);
		     	  ctiVO.setOtfApplicable(ctiOTFVO.isOtfApplicable());
		     	   list.add(ctiVO);
		        }
		        channelTransferVO.setChannelTransferitemsVOList(list);
		       	 }
				
				}*/
			if(sendOrderToApproval)
			{
				if ((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,channelTransferVO.getNetworkCode())
						&& (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(channelTransferVO.getTransferSubType()) || "V".equals(channelTransferVO.getTransferSubType()))) 
				{
					ChannelTransferBL.increaseOptOTFCounts(con, channelTransferVO);
				}
			}//serial 1
			ChannelTransferItemsVO  channelTransferItemVO =(ChannelTransferItemsVO) channelTransferVO.getChannelTransferitemsVOList().get(0); 
			if (sendOrderToApproval) {
				if(theForm.getReconcilationFlag())
					message = "channeltransfer.success.reconciliation.msg.success";
				
				else
					message = "channeltransfer.approval.msg.success";
				
				channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
				channelTransferVO.setWalletType(PretupsI.SALE_WALLET_TYPE);
				//Validate MRP && Successive Block for channel transaction
				long successiveReqBlockTime4ChnlTxn = ((Long)PreferenceCache.getSystemPreferenceValue(PreferenceI.SUCCESS_REQUEST_BLOCK_SEC_CODE_O2C)).longValue();
				try {
					ChannelTransferBL.validateChannelLastTransferMrpSuccessiveBlockTimeout(con, channelTransferVO, date, successiveReqBlockTime4ChnlTxn);					
				} catch (Exception e) {
					message = "o2c.approval.error.mrpblocktimeout";
					//prepare the message
					String args[] = {channelTransferVO.getUserMsisdn(), PretupsBL.getDisplayAmount(channelTransferVO.getTransferMRP()),String.valueOf(successiveReqBlockTime4ChnlTxn/60)};
					throw new BTSLBaseException(this, methodName, message, args);
				}
				this.approveOrder(con, channelTransferVO, channelUserVO.getUserID(), date, nextForwardPath);
			}
			// added by nilesh

			final String email = channelUserWebDAO.loadUserEmail(con, channelTransferVO.getToUserID());
			channelTransferVO.setEmail(email);
			// end
			// commented as disscussed with Sanjay Sir, GSB, AC need not to be
			// updated in WEB
			// int updateCount =(new
			// ChannelUserDAO()).updateUserPhoneAfterTxn(con,channelTransferVO,channelTransferVO.getToUserCode(),channelTransferVO.getToUserID(),true);
			int updateCount = 0;
			channelTransferVO.setExternalTxnNum(theForm.getExternalTxnNum());
			if (!BTSLUtil.isNullString(theForm.getExternalTxnDate())) {
				channelTransferVO.setExternalTxnDate(BTSLUtil.getDateFromDateString(theForm.getExternalTxnDate()));
			}
			// added for editable reference number
			channelTransferVO.setReferenceNum(BTSLUtil.NullToString(theForm.getRefrenceNum()));
			// added for logger
			if (channelTransferVO.getTransferMRP() <= channelTransferVO.getFirstApproverLimit() && PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(theForm
					.getCurrentApprovalLevel())) {
				OneLineTXNLog.log(channelTransferVO, null);
			} else if (channelTransferVO.getTransferMRP() <= channelTransferVO.getSecondApprovalLimit() && PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(theForm
					.getCurrentApprovalLevel())) {
				OneLineTXNLog.log(channelTransferVO, null);
			} else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(theForm.getCurrentApprovalLevel())) {
				OneLineTXNLog.log(channelTransferVO, null);
			}
			// end
			if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(theForm.getCurrentApprovalLevel())) {
				channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
				channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
				if(PretupsI.PAYMENT_INSTRUMENT_TYPE_ONLINE.equals(theForm.getPaymentInstrumentCode()))
				{
					updateCount = channelTransferDAO.updateChannelTransferApproval(con, channelTransferVO, sendOrderToApproval , theForm.getReconcilationFlag() ? true: false);
				}
				else
				{
					updateCount = channelTransferDAO.updateChannelTransferApprovalLevelOne(con, channelTransferVO, sendOrderToApproval);
				}
			} else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(theForm.getCurrentApprovalLevel())) {
				channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
				channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
				updateCount = channelTransferDAO.updateChannelTransferApprovalLevelTwo(con, channelTransferVO, sendOrderToApproval);
			} else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(theForm.getCurrentApprovalLevel())) {
				channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
				channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
				updateCount = channelTransferDAO.updateChannelTransferApprovalLevelThree(con, channelTransferVO, sendOrderToApproval);
			}
			if (updateCount > 0) {
		
				mcomCon.finalCommit();
				if (SystemPreferences.LMS_APPL) {
					try {
						if (channelTransferVO.getStatus().equalsIgnoreCase(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE)) {
							final Date currentdate = new Date();

							final LoyaltyBL _loyaltyBL = new LoyaltyBL();
							final LoyaltyVO loyaltyVO = new LoyaltyVO();
							PromotionDetailsVO promotionDetailsVO = new PromotionDetailsVO();
							final ArrayList arr = new ArrayList();
							final LoyaltyDAO _loyaltyDAO = new LoyaltyDAO();
							loyaltyVO.setServiceType(PretupsI.O2C_MODULE);
							loyaltyVO.setModuleType(PretupsI.O2C_MODULE);

							if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType())) {
								loyaltyVO.setTransferamt(channelTransferVO.getSenderDrQty());
							} else {
								loyaltyVO.setTransferamt(channelTransferVO.getTransferMRP());
							}

							loyaltyVO.setCategory(channelTransferVO.getReceiverCategoryCode());
							loyaltyVO.setUserid(channelTransferVO.getToUserID());
							loyaltyVO.setNetworkCode(channelTransferVO.getNetworkCode());
							loyaltyVO.setSenderMsisdn(channelTransferVO.getUserMsisdn());
							loyaltyVO.setTxnId(channelTransferVO.getTransferID());
							loyaltyVO.setCreatedOn(currentdate);
							loyaltyVO.setProductCode(channelTransferItemVO.getProductCode());
							arr.add(loyaltyVO.getUserid());
							promotionDetailsVO = _loyaltyDAO.loadSetIdByUserId(con, arr);
							loyaltyVO.setSetId(promotionDetailsVO.get_setId());
							if (loyaltyVO.getSetId() == null) {
								_log.error("process", "Exception durign LMS Module Profile Details are not found");
							} else {
								_loyaltyBL.distributeLoyaltyPoints(PretupsI.O2C_MODULE, channelTransferVO.getTransferID(), loyaltyVO);
							}
						}
					} catch (Exception ex) {
						_log.error("process", "Exception durign LMS Module " + ex.getMessage());
						_log.errorTrace(methodName, ex);
					}
				}

				// user life cycle by Akanksha
				if (channelTransferVO.getStatus().equalsIgnoreCase(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE)) {
					if (!PretupsI.USER_STATUS_ACTIVE.equals(theForm.getChannelUserStatus())) {
						// int
						// updatecount=operatorUtili.changeUserStatusToActive(
						// con,channelTransferVO.getToUserID(),theForm.getChannelUserStatus());
						int updatecount = 0;
						final String str[] = SystemPreferences.TXN_RECEIVER_USER_STATUS_CHANG.split(","); // "CH:Y,EX:Y".split(",");
						String newStatus[] = null;
						boolean changeStatusRequired = false;
						int strLength = str.length;
						for (int i = 0; i < strLength; i++) {
							newStatus = str[i].split(":");
							if (newStatus[0].equals(theForm.getChannelUserStatus())&&operatorUtili!=null) {
								changeStatusRequired = true;
								updatecount = operatorUtili.changeUserStatusToActive(con, channelTransferVO.getToUserID(), theForm.getChannelUserStatus(), newStatus[1]);
								break;
							}
						}
						if (changeStatusRequired) {
							if (updatecount > 0) {
							
								mcomCon.finalCommit();
							} else {
						
								mcomCon.finalRollback();
								throw new BTSLBaseException(this, methodName, "channeltransfer.approval.msg.unsuccess");
							}
						}
					}
				}

				// added by nilesh:for email notification
				if (SystemPreferences.O2C_EMAIL_NOTIFICATION) {
					if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(theForm.getCurrentApprovalLevel())) {
						final String firstApprvLimit = PretupsBL.getDisplayAmount(Long.parseLong(theForm.getFirstApprovalLimit()));
						if(channelTransferVO.isReconciliationFlag() && "T".equals(channelTransferVO.getTransferSubType()))
						{
							sendEmailNotification(con, channelTransferVO, p_channelUserVO, channelTransferDAO, "", PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1, "o2c.email.notification.content.transfer.completed");
						}
						else {
							if (Float.parseFloat(theForm.getTotalMRP()) > Float.parseFloat(firstApprvLimit)) {
							sendEmailNotification(con, channelTransferVO, p_channelUserVO, channelTransferDAO, "APV2O2CTRF", "", "o2c.email.notification.subject.approver");
						} else {
							sendEmailNotification(con, channelTransferVO, p_channelUserVO, channelTransferDAO, "", PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1, "o2c.email.notification.content.transfer.completed");
						}
						}
					} else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(theForm.getCurrentApprovalLevel())) {
						// TODO
						final String secondApprvLimit = PretupsBL.getDisplayAmount(Long.parseLong(theForm.getSecondApprovalLimit()));
						if (Float.parseFloat(theForm.getTotalMRP()) > Float.parseFloat(secondApprvLimit)) {
							sendEmailNotification(con, channelTransferVO, p_channelUserVO, channelTransferDAO, "APV3O2CTRF", "", "o2c.email.notification.subject.approver");
						} else {
							sendEmailNotification(con, channelTransferVO, p_channelUserVO, channelTransferDAO, "", PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2, "o2c.email.notification.content.transfer.completed");
						}
					} else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(theForm.getCurrentApprovalLevel())) {
						sendEmailNotification(con, channelTransferVO, p_channelUserVO, channelTransferDAO, "", PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3, "o2c.email.notification.content.transfer.completed");
					}
				}
				// end
				
				theForm.setApprovalDone(true);

				if (sendOrderToApproval) {
					
					if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,channelTransferVO.getNetworkCode()))
					{
 						if(channelTransferVO.isTargetAchieved() && PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(channelTransferVO.getStatus()))
						{
							//Message handling for OTF
							TargetBasedCommissionMessages tbcm =new TargetBasedCommissionMessages();
							/*channelUserVO.setCommissionProfileSetID(channelTransferVO.getCommProfileSetId());
							channelUserVO.setNetworkCode(channelTransferVO.getNetworkCode());
							channelUserVO.setCommissionProfileSetVersion(channelTransferVO.getCommProfileVersion());
							channelUserVO.setUserID(channelTransferVO.getToUserID());
							tbcm.loadBaseCommissionProfileDetailsForTargetMessages(con,channelUserVO);*/
							tbcm.loadBaseCommissionProfileDetailsForTargetMessages(con,channelTransferVO.getToUserID(),channelTransferVO.getMessageArgumentList());
						}
					}
					
					ChannelTransferBL.prepareUserBalancesListForLogger(channelTransferVO);
					
					UserPhoneVO primaryPhoneVO = null;
					if (SystemPreferences.SECONDARY_NUMBER_ALLOWED && (SystemPreferences.MESSAGE_TO_PRIMARY_REQUIRED && !theForm.isPrimaryNumber())) {
						primaryPhoneVO = userDAO.loadUserAnyPhoneVO(con, theForm.getToPrimaryMSISDN());
					}
					
					final UserPhoneVO phoneVO = userDAO.loadUserAnyPhoneVO(con, channelTransferVO.getUserMsisdn());
					String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
					String language = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
					
					
					 if(_receiverMessageSendReq){if (primaryPhoneVO != null) {
						country = primaryPhoneVO.getCountry();
						language = primaryPhoneVO.getPhoneLanguage();
						final Object[] smsListArr = ChannelTransferBL.prepareSMSMessageListForReceiver(con, channelTransferVO, PretupsErrorCodesI.C2S_OPT_CHNL_TRANSFER_SMS2,
								PretupsErrorCodesI.C2S_OPT_CHNL_TRANSFER_SMS_BALSUBKEY);
						final Locale locale = new Locale(language, country);
						final String[] array = { channelTransferVO.getTransferID(), BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]), PretupsBL
								.getDisplayAmount(channelTransferVO.getNetPayableAmount()), BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1]) };
						final BTSLMessages messages = new BTSLMessages(PretupsErrorCodesI.C2S_OPT_CHNL_TRANSFER_SMS1, array);
						final PushMessage pushMessage = new PushMessage(primaryPhoneVO.getMsisdn(), messages, channelTransferVO.getTransferID(), null, locale,
								channelTransferVO.getNetworkCode());
						pushMessage.push();
					}
					if (phoneVO != null) {
						country = phoneVO.getCountry();
						language = phoneVO.getPhoneLanguage();
						final Object[] smsListArr = ChannelTransferBL.prepareSMSMessageListForReceiver(con, channelTransferVO, PretupsErrorCodesI.C2S_OPT_CHNL_TRANSFER_SMS2,
								PretupsErrorCodesI.C2S_OPT_CHNL_TRANSFER_SMS_BALSUBKEY);
						final Locale locale = new Locale(language, country);
						final String[] array = { channelTransferVO.getTransferID(), BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]), PretupsBL
								.getDisplayAmount(channelTransferVO.getNetPayableAmount()), BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1]) };
						final BTSLMessages messages = new BTSLMessages(PretupsErrorCodesI.C2S_OPT_CHNL_TRANSFER_SMS1, array);
						final PushMessage pushMessage = new PushMessage(phoneVO.getMsisdn(), messages, channelTransferVO.getTransferID(), null, locale, channelTransferVO
								.getNetworkCode());
						pushMessage.push();
					} else {
						final String arr[] = { channelTransferVO.getTransferID(), channelTransferVO.getToUserName() };
						throw new BTSLBaseException(this, methodName, "channeltransfer.phoneinfo.notexist.msg", arr );
					}}

				if (_ussdReceiverMessageSendReq) {

					if (primaryPhoneVO != null) {
						country = primaryPhoneVO.getCountry();
						language = primaryPhoneVO.getPhoneLanguage();
						Object[] smsListArr = ChannelTransferBL.prepareSMSMessageListForReceiver(con, channelTransferVO,
								PretupsErrorCodesI.C2S_OPT_CHNL_TRANSFER_SMS2,
								PretupsErrorCodesI.C2S_OPT_CHNL_TRANSFER_SMS_BALSUBKEY);
						Locale locale = new Locale(language, country);
						String[] array = { channelTransferVO.getTransferID(),
								BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]),
								PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()),
								BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1]) };
						BTSLMessages messages = new BTSLMessages(PretupsErrorCodesI.C2S_OPT_CHNL_TRANSFER_SMS1, array);
						USSDPushMessage pushMessage = new USSDPushMessage(primaryPhoneVO.getMsisdn(), messages,
								channelTransferVO.getTransferID(), null, locale, channelTransferVO.getNetworkCode());
						pushMessage.push();
					}
					if (phoneVO != null) {
						country = phoneVO.getCountry();
						language = phoneVO.getPhoneLanguage();
						Object[] smsListArr = ChannelTransferBL.prepareSMSMessageListForReceiver(con, channelTransferVO,
								PretupsErrorCodesI.C2S_OPT_CHNL_TRANSFER_SMS2,
								PretupsErrorCodesI.C2S_OPT_CHNL_TRANSFER_SMS_BALSUBKEY);
						Locale locale = new Locale(language, country);
						String[] array = { channelTransferVO.getTransferID(),
								BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]),
								PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()),
								BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1]) };
						BTSLMessages messages = new BTSLMessages(PretupsErrorCodesI.C2S_OPT_CHNL_TRANSFER_SMS1, array);
						USSDPushMessage pushMessage = new USSDPushMessage(phoneVO.getMsisdn(), messages,
								channelTransferVO.getTransferID(), null, locale, channelTransferVO.getNetworkCode());
						pushMessage.push();
					} else {
						String arr[] = { channelTransferVO.getTransferID(), channelTransferVO.getToUserName() };
						throw new BTSLBaseException(this, methodName, "channeltransfer.phoneinfo.notexist.msg", arr);
					}
				}
				}

		     	/*
		     	 *  prepare the message for success response
		     	 */
				final String args[] = { channelTransferVO.getTransferID() };//setting response
				String responseMessage = this.getMessage(locale, message, args);
				this.createSuccessResponse(200, responseMessage, message, channelTransferVO.getTransferID());
				
			} else 
			{
				//con.rollback();
				mcomCon.finalRollback();
				throw new BTSLBaseException(this, "orderApproval", "channeltransfer.approval.msg.unsuccess", forwardPath);
			}
			
		} catch (BTSLBaseException be) {
			_log.error("orderApproval", "Exception:e=" + be);
			_log.errorTrace(methodName, be);
			 try {
		            if (con != null) {
		                con.rollback();
		            }
		        } catch (SQLException e1) {
		            _log.errorTrace(methodName, e1);
		        }
			throw be;
		} catch (SQLException e) {
			_log.error("orderApproval", "Exception:e=" + e);
			_log.errorTrace(methodName, e);
			 try {
		            if (con != null) {
		                con.rollback();
		            }
		        } catch (SQLException e1) {
		            _log.errorTrace(methodName, e1);
		        }
			throw e;
		} catch (ParseException e) {
			_log.error("orderApproval", "ParseException:e=" + e);
			_log.errorTrace(methodName, e);
			 try {
		            if (con != null) {
		                con.rollback();
		            }
		        } catch (SQLException e1) {
		            _log.errorTrace(methodName, e1);
		        }
			throw e;
		} catch (Exception e) {
			_log.error("orderApproval", "ParseException:e=" + e);
			_log.errorTrace(methodName, e);
			 try {
		            if (con != null) {
		                con.rollback();
		            }
		        } catch (SQLException e1) {
		            _log.errorTrace(methodName, e1);
		        }
			throw e;
		} finally {
			if (mcomCon != null) {
				mcomCon.close("ChannelTransferApprovalAction#orderApproval");
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) {
				_log.debug("orderApproval", "Exiting forward:=" + methodName);
			}
		}
	}
	
	/**
	 * if order clear all approval check then this method will approve the order
	 * and made entry
	 * in diffrent tables as required by order approval.
	 * 
	 * @param p_con
	 * @param p_channelTransferVO : has transferItamVO
	 * @param p_userId
	 * @param p_date
	 * @param p_forwardPath
	 * @throws BTSLBaseException
	 */
	//sub method of orderApproval
	private void approveOrder(Connection p_con, ChannelTransferVO p_channelTransferVO, String p_userId, Date p_date, String p_forwardPath) throws BTSLBaseException {
		final boolean debit = true;
		String Status ;
		String domainCode ;
		final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
		if (_log.isDebugEnabled()) {
			_log.debug("approveOrder", "Entered p_channelTransferVO  " + p_channelTransferVO + " p_userId " + p_userId + " p_date " + p_date);
		}

		// prepare networkStockList and debit the network stock
		domainCode = p_channelTransferVO.getDomainCode();
		Status = channelTransferDAO.getStatusOfDomain(p_con, domainCode);
		if ("N".equals(Status)) {
			throw new BTSLBaseException("O2C.approval.error.invalidDomain");

		}
		p_channelTransferVO.setTransferDate(p_date);
		if(!PretupsI.CHANNEL_TRANSFER_SUB_TYPE_VOUCHER.equals(p_channelTransferVO.getTransferSubType()))
		{
			ChannelTransferBL.prepareNetworkStockListAndCreditDebitStock(p_con, p_channelTransferVO, p_userId, p_date, debit);
			ChannelTransferBL.updateNetworkStockTransactionDetails(p_con, p_channelTransferVO, p_userId, p_date);  //NGBC0000001564
			if (PretupsI.COMM_TYPE_POSITIVE.equals(p_channelTransferVO.getDualCommissionType())) {
				ChannelTransferBL.prepareNetworkStockListAndCreditDebitStockForCommision(p_con, p_channelTransferVO, p_userId, p_date, debit);
				ChannelTransferBL.updateNetworkStockTransactionDetailsForCommision(p_con, p_channelTransferVO, p_userId, p_date);
			}
			final UserBalancesDAO userBalancesDAO = new UserBalancesDAO();
			userBalancesDAO.updateUserDailyBalances(p_con, p_date, constructBalanceVOFromTxnVO(p_channelTransferVO));
			final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
			if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
				channelUserDAO.creditUserBalancesForMultipleWallet(p_con, p_channelTransferVO, true, p_forwardPath);
			} else {
				channelUserDAO.creditUserBalances(p_con, p_channelTransferVO, true, p_forwardPath);
			}
		}
		p_channelTransferVO.setTransactionCode(PretupsI.TRANSFER_TYPE_O2C);
		ChannelTransferBL.updateOptToChannelUserInCounts(p_con, p_channelTransferVO, p_forwardPath, p_date);


		if (_log.isDebugEnabled()) {
			_log.debug("approveOrder", "Exiting ");
		}
	}
	
	/**
	 * Method constructBalanceVOFromTxnVO.
	 * 
	 * @param p_channelTransferVO
	 *            ChannelTransferVO
	 * @return UserBalancesVO
	 */
	private UserBalancesVO constructBalanceVOFromTxnVO(ChannelTransferVO p_channelTransferVO) {
		if (_log.isDebugEnabled()) {
			_log.debug("constructBalanceVOFromTxnVO", "Entered:NetworkStockTxnVO=>" + p_channelTransferVO);
		}
		final UserBalancesVO userBalancesVO = new UserBalancesVO();
		userBalancesVO.setUserID(p_channelTransferVO.getToUserID());
		userBalancesVO.setLastTransferType(p_channelTransferVO.getTransferType());
		userBalancesVO.setLastTransferID(p_channelTransferVO.getTransferID());
		userBalancesVO.setLastTransferOn(p_channelTransferVO.getModifiedOn());
		// Added to log user MSISDN on 13/02/2008
		userBalancesVO.setUserMSISDN(p_channelTransferVO.getUserMsisdn());
		if (_log.isDebugEnabled()) {
			_log.debug("constructBalanceVOFromTxnVO", "Exiting userBalancesVO=" + userBalancesVO);
		}
		return userBalancesVO;
	}
	
	/**
	 * Cancel the order
	 * This method is called to cancel the order at any level.
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 * @throws SQLException 
	 * @throws BTSLBaseException 
	 */
	private void cancelOrder( ChannelTransferApprovalForm theForm, ChannelUserVO P_channelUserVO) throws SQLException, BTSLBaseException {
		final String methodName = "cancelOrder";
		if (_log.isDebugEnabled()) {
			_log.debug("cancelOrder", "Entered");
		}
		//ActionForward forward = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		String forwardPath = "cancellevelone";
		
		try {
			//final ChannelTransferApprovalForm theForm = (ChannelTransferApprovalForm) form;
			final ChannelUserVO channelUserVO = (ChannelUserVO) P_channelUserVO;
			final ChannelTransferVO channelTransferVO = (ChannelTransferVO) theForm.getChannelTransferVO();

			// it means onces jsp forward to another action. then from forwarded
			// action where we will forward it further.
			String nextForwardPath = "viewlistlevelone";
			String failLevel = null;
			String message = "channeltransfer.approval.levelone.msg.cancel";
			if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(theForm.getCurrentApprovalLevel())) 
			{
				channelTransferVO.setFirstApprovalRemark(theForm.getApprove1Remark());
				failLevel = "one";
			} else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(theForm.getCurrentApprovalLevel())) 
			{
				channelTransferVO.setSecondApprovalRemark(theForm.getApprove2Remark());
				message = "channeltransfer.approval.leveltwo.msg.cancel";
				failLevel = "two";
			} else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(theForm.getCurrentApprovalLevel())) 
			{
				channelTransferVO.setThirdApprovalRemark(theForm.getApprove3Remark());
				message = "channeltransfer.approval.levelthree.msg.cancel";
				failLevel = "three";
			}
          
			final Date date = new Date();
			if(theForm.getReconcilationFlag())
			{
				channelTransferVO.setFirstApprovedBy(channelUserVO.getUserID());
				channelTransferVO.setFirstApprovedOn(date);
				message = "channeltransfer.fail.reconciliation.msg.success";

			}
			channelTransferVO.setCanceledBy(channelUserVO.getUserID());
			channelTransferVO.setCanceledOn(date);
			channelTransferVO.setModifiedBy(channelUserVO.getUserID());
			channelTransferVO.setModifiedOn(date);
			String prevStatus = channelTransferVO.getStatus();
			channelTransferVO.setPayInstrumentStatus("REJECT");
			if(theForm.getReconcilationFlag()) 
			{
				channelTransferVO.setPreviousStatus(PretupsI.CHANNEL_TRANSFER_ORDER_PENDING);
			} else 
			{
				channelTransferVO.setPreviousStatus(prevStatus);				
			}
			channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
			channelTransferVO.setReconciliationFlag(theForm.getReconcilationFlag());
			mcomCon = new MComConnection();con=mcomCon.getConnection();
			final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
			// commented as disscussed with Sanjay Sir, GSB, AC need not to be
			// updated in WEB
			// updateCount =(new
			// ChannelUserDAO()).updateUserPhoneAfterTxn(con,channelTransferVO,channelTransferVO.getToUserCode(),channelTransferVO.getToUserID(),true);
			
				final int updateCount = channelTransferDAO.cancelTransferOrder(con, channelTransferVO, theForm.getCurrentApprovalLevel());
				
			if (updateCount > 0) {
		
				mcomCon.finalCommit();
				final UserDAO userDAO = new UserDAO();
				final UserPhoneVO phoneVO = userDAO.loadUserPhoneVO(con, channelTransferVO.getToUserID());
				String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
				String language = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
				if (phoneVO != null) {
					channelTransferVO.setChannelTransferitemsVOList(theForm.getTransferItemList());
					country = phoneVO.getCountry();
					language = phoneVO.getPhoneLanguage();
				} else {
					final String arr[] = { channelTransferVO.getTransferID(), channelTransferVO.getToUserName() };
					throw new BTSLBaseException(this, methodName, "channeltransfer.cancelorder.phoneinfo.notexist.msg", arr);
				}

				final Locale locale = new Locale(language, country);
				
				BTSLMessages messages = null; 
				final Object[] smsListArr = ChannelTransferBL.prepareSMSMessageListForReceiver(con, channelTransferVO,
						PretupsErrorCodesI.C2S_OPT_CHNL_TRANSFER_CANCEL_TXNSUBKEY,
						PretupsErrorCodesI.C2S_OPT_CHNL_TRANSFER_CANCEL_BALSUBKEY);
				final String[] array = { channelTransferVO.getTransferID(), BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]),
						BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1]) };
				messages = new BTSLMessages(PretupsErrorCodesI.C2S_OPT_CHNL_TRANSFER_CANCEL, array);
				
				final PushMessage pushMessage = new PushMessage(phoneVO.getMsisdn(), messages, channelTransferVO.getTransferID(), null, locale, channelTransferVO
						.getNetworkCode());
				// pushMessage.push();
				// prepare the message
				final String args[] = { channelTransferVO.getTransferID() };
				pushMessage.push();
				/*
		     	 *  prepare the message for success response
		     	 */
				String responseMessage = this.getMessage(locale, message, args);
				this.createSuccessResponse(200, responseMessage, message, channelTransferVO.getTransferID());
				// set it in the request attribute.
				// this is because we have to display this message on the list
				// screen
				// here we set it in request and in the forwarded method we
				// retrvie the message and set it,
				// to dispaly.
				
				
				//sending channel user email notification on cancel order
				ChannelUserWebDAO channelUserWebDAO = new ChannelUserWebDAO();
				if (SystemPreferences.O2C_EMAIL_NOTIFICATION) 
				{
					final String email = channelUserWebDAO.loadUserEmail(con, channelTransferVO.getToUserID());
					channelTransferVO.setEmail(email);
					sendEmailNotification(con, channelTransferVO, channelUserVO, channelTransferDAO, "", failLevel, "o2c.email.notification.subject.failed");
				}
				
				
				
			} else 
			{
				//con.rollback();
				mcomCon.finalRollback();
				throw new BTSLBaseException(this, "cancelOrder", "channeltransfer.cancel.msg.unsuccess");
			}
		} catch (SQLException e) {
			_log.error("cancelOrder", "SQLException:e=" + e);
			_log.errorTrace(methodName, e);
			 try {
		            if (con != null) {
		                con.rollback();
		            }
		        } catch (SQLException e1) {
		            _log.errorTrace(methodName, e1);
		        }
			throw e;
		} catch (BTSLBaseException be) {
			_log.error("cancelOrder", "BTSLBaseException:e=" + be);
			_log.errorTrace(methodName, be);
			try {
	            if (con != null) {
	                con.rollback();
	            }
	        } catch (SQLException e1) {
	            _log.errorTrace(methodName, e1);
	        }
			throw be;
		} catch (Exception e) {
			_log.error("cancelOrder", "BTSLBaseException:e=" + e);
			_log.errorTrace(methodName, e);
			try {
	            if (con != null) {
	                con.rollback();
	            }
	        } catch (SQLException e1) {
	            _log.errorTrace(methodName, e1);
	        }
			throw e;
		} finally {
			if (mcomCon != null) {
				mcomCon.close("O2CApprovalServiceImpl#cancelOrder");
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) {
				_log.debug("cancelOrder", "Exiting method:=" + methodName);
			}
		}
	}
	
	/**
	 * 
	 * @param p_con
	 * @param p_channelTransferVO
	 * @param request
	 * @param p_channelTransferDAO
	 * @param p_roleCode
	 * @param approvalLevel
	 * @param p_subject
	 */
	private void sendEmailNotification(Connection p_con,  ChannelTransferVO p_channelTransferVO, ChannelUserVO p_channelUserVO, ChannelTransferDAO p_channelTransferDAO, String p_roleCode, String approvalLevel, String p_subject) {
		final String methodName = "sendEmailNotification";
        final Locale locale = BTSLUtil.getSystemLocaleForEmail();
        
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");
		}

		try {
			final ChannelUserVO userVO = (ChannelUserVO) p_channelUserVO;
			final String from =  getMessage(locale,"o2c.email.notification.from");
			String cc = PretupsI.EMPTY;
			String message1 = null;
			final String bcc = "";
			String subject = "";
			boolean isHeaderAdded = false;
			p_channelTransferVO.setToUserMsisdn(p_channelTransferVO.getUserMsisdn());
			String notifyContent = "";
			if (!BTSLUtil.isNullString(p_roleCode) && "APV1O2CTRF".equals(p_roleCode)) { 
				notifyContent = getMessage(locale,"o2c.email.notification.content");
			}
			else if (!BTSLUtil.isNullString(p_roleCode) && "APV2O2CTRF".equals(p_roleCode)) { 
				notifyContent = getMessage(locale,"o2c.email.notification.content");
			} else if (!BTSLUtil.isNullString(p_roleCode) && "APV3O2CTRF".equals(p_roleCode)) { 
				notifyContent = getMessage(locale,"o2c.email.notification.content");
			}
			else if(p_subject.equalsIgnoreCase("o2c.email.notification.subject.initiate"))
				notifyContent = getMessage(locale,"o2c.email.notification.subject.initiate");
			else if(p_subject.equalsIgnoreCase("o2c.email.notification.subject.failed"))
				notifyContent = getMessage(locale,"o2c.email.notification.subject.failed");
			else{ 
				notifyContent = getMessage(locale,"o2c.email.notification.content.transfer.completed");
			}
			String appr1Quan = null;
			String appr2Quan = null;
			String appr3Quan = null;
			if(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_VOUCHER.equals(p_channelTransferVO.getTransferSubType())) {
				if(p_channelTransferVO.getLevelOneApprovedQuantity()!=null)
					appr1Quan = PretupsBL.getDisplayAmount(Double.parseDouble(p_channelTransferVO.getLevelOneApprovedQuantity()));
				if(p_channelTransferVO.getLevelTwoApprovedQuantity()!=null)
					appr2Quan = PretupsBL.getDisplayAmount(Double.parseDouble(p_channelTransferVO.getLevelTwoApprovedQuantity()));
				if(p_channelTransferVO.getLevelThreeApprovedQuantity()!=null)
					appr3Quan = PretupsBL.getDisplayAmount(Double.parseDouble(p_channelTransferVO.getLevelThreeApprovedQuantity()));
			} else {
				appr1Quan = p_channelTransferVO.getLevelOneApprovedQuantity();
				appr2Quan = p_channelTransferVO.getLevelTwoApprovedQuantity();
				appr3Quan = p_channelTransferVO.getLevelThreeApprovedQuantity();
			}
			
			//For getting name and msisdn of initiator
            ArrayList arrayList = new ArrayList();
            ChannelUserWebDAO channelUserWebDAO = new ChannelUserWebDAO();
            arrayList = channelUserWebDAO.loadUserNameAndEmail(p_con, p_channelTransferVO.getCreatedBy());

            String message = null;
            
            message = notifyContent + "<br>" + getMessage(locale,"o2c.email.channeluser.details") + "<br>" +
        			getMessage(locale,"o2c.email.transferid") + " " + p_channelTransferVO.getTransferID() + 
        			"<br>" + getMessage(locale,"o2c.email.channeluser.name") + " " + p_channelTransferVO.getToUserName() + 
        			"<br>" + getMessage(locale,"o2c.email.channeluser.msisdn") + " " + p_channelTransferVO.getUserMsisdn() +
        			"<br>" + getMessage(locale,"o2c.email.transfer.mrp") + " " + p_channelTransferVO.getTransferMRPAsString() +
        			"<br>" + getMessage(locale,"o2c.email.notification.content.req.amount")+" " + PretupsBL.getDisplayAmount(p_channelTransferVO.getRequestedQuantity());
            
            
            /*Message Content exclusively for transfer rejects
            	Showing only Fail Subject, TranferID, Reject User, Fail Remarks
            */
            
            if(p_subject.equalsIgnoreCase("o2c.email.notification.subject.failed"))
            {
            	message = message + "<br>" + getMessage(locale,"o2c.email.transfer.type") + " " +
            			p_channelTransferVO.getType() + "<br>" + getMessage(locale,"o2c.email.initiator.name") + " " + arrayList.get(0) + 
            			"<br>" + getMessage(locale,"o2c.email.initiator.msisdn") + " " + arrayList.get(1) +
            			"<br>" + getMessage(locale,"o2c.email.notification.content.rejected.by") + 
            			userVO.getUserName() +
            			"<br>" + getMessage(locale,"o2c.email.notification.content.rejection.remarks");
            			if("three".equals(approvalLevel))	
            				message += p_channelTransferVO.getThirdApprovalRemark();
            	
            			else if("two".equals(approvalLevel))
            				message += p_channelTransferVO.getSecondApprovalRemark();
            	
            			else if("one".equals(approvalLevel))
            				message += p_channelTransferVO.getFirstApprovalRemark();        	
            }
 
            else{
			message = message + "<br>" + getMessage(locale,"o2c.email.notification.content.net.payable.amount")+ " " + PretupsBL.getDisplayAmount(p_channelTransferVO.getNetPayableAmount());
			if (!BTSLUtil.isNullString(p_roleCode) && "APV2O2CTRF".equals(p_roleCode)) {
				message = message + "<br>" + getMessage(locale,"o2c.email.notification.content.appr.one.quantity") + " " + appr1Quan;
			} else if (!BTSLUtil.isNullString(p_roleCode) && "APV3O2CTRF".equals(p_roleCode)) { 
				message = message + "<br>" + getMessage(locale,"o2c.email.notification.content.appr.one.quantity") + " " + appr1Quan + 
				"<br>" + getMessage(locale,"o2c.email.notification.content.appr.two.quantity") + " " + appr2Quan;
			} else {
				if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(approvalLevel)) {
					message = message + "<br>" + getMessage(locale,"o2c.email.notification.content.appr.quantity") + " " + appr1Quan;					
				} else if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(approvalLevel)) {
					message = message + "<br>" + getMessage(locale,"o2c.email.notification.content.appr.quantity") + " " + appr2Quan;	
				} else if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(approvalLevel)) {
					message = message + "<br>" + getMessage(locale,"o2c.email.notification.content.appr.quantity") + " " + appr3Quan;
				}
			}
			
			message = message + "<br>" + getMessage(locale,"o2c.email.transfer.type") + " " + p_channelTransferVO.getType() + "<br>" + getMessage(locale,"o2c.email.initiator.name") + " " + arrayList.get(0) + 
                    "<br>" + getMessage(locale,"o2c.email.initiator.msisdn") + " " + arrayList.get(1);
            }
            
			if(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_VOUCHER.equals(p_channelTransferVO.getTransferSubType()))
            {
				  if(!(p_subject.equalsIgnoreCase("o2c.email.notification.subject.failed"))){
	                  String totalCommission = PretupsBL.getDisplayAmount(((ChannelTransferItemsVO)p_channelTransferVO.getChannelTransferitemsVOList().get(0)).getCommQuantity());
	                  message = message + "<br>" + getMessage(locale,"o2c.email.total.commission") + " " + totalCommission;
	                  if(PretupsI.COMM_TYPE_POSITIVE.equals(p_channelTransferVO.getDualCommissionType())) {
	                        message = message + "<br>" + getMessage(locale,"o2c.email.offline.settlement");
	                  }
				  }
				  
                  if(p_channelTransferVO.getChannelVoucherItemsVoList() != null)
                  {
                        for(int i=0 ;i < p_channelTransferVO.getChannelVoucherItemsVoList().size();i++)
                        {
                              if(((ChannelVoucherItemsVO)p_channelTransferVO.getChannelVoucherItemsVoList().get(i)).getToSerialNum() != null && ((ChannelVoucherItemsVO)p_channelTransferVO.getChannelVoucherItemsVoList().get(i)).getFromSerialNum() != null)
                              {
                                    if(!isHeaderAdded) {
                                          isHeaderAdded = true;
                                          message1 = "<br>" + "<table><tr>"
                                        		  	  + " <td style='width: 5%;'>"+ getMessage(locale, "o2c.email.notification.serialNumber") + "</td>"
                                                      + " <td style='width: 10%;'>"+ getMessage(locale, "o2c.email.notification.denomination") + "</td>"
                                                      + " <td style='width: 10%;'>"+ getMessage(locale, "o2c.email.notification.quantity") + "</td>"
                                                      + " <td style='width: 25%;'>"+ getMessage(locale, "o2c.email.notification.fromSerialNo") + "</td>"
                                                      + " <td style='width: 25%;'>"+ getMessage(locale, "o2c.email.notification.toSerialNo") + "</td>"
//                                                      + " <td style='width: 13%;'>"+ messages.getMessage(locale, "o2c.email.notification.product") + "</td>"
                                                      + " <td style='width: 12%;'>"+ getMessage(locale, "o2c.email.notification.voucherType") + "</td>"
                                                      + "</tr>";
                                    }
                                    message1 = message1 + "<tr><td style='width: 5%;'>" +(i + 1) + "</td>" + 
                                                   "<td style='width: 10%;'>" +((ChannelVoucherItemsVO)p_channelTransferVO.getChannelVoucherItemsVoList().get(i)).getTransferMrp() + "</td>" +  
                                                   "<td style='width: 10%;'>" + ((ChannelVoucherItemsVO)p_channelTransferVO.getChannelVoucherItemsVoList().get(i)).getRequiredQuantity() + "</td>" +  
                                                   "<td style='width: 25%;'>" + ((ChannelVoucherItemsVO)p_channelTransferVO.getChannelVoucherItemsVoList().get(i)).getFromSerialNum() + "</td>" + 
                                                   "<td style='width: 25%;'>" + ((ChannelVoucherItemsVO)p_channelTransferVO.getChannelVoucherItemsVoList().get(i)).getToSerialNum() + "</td>" +
//                                                   "<td style='width: 13%;'>" + ((ChannelVoucherItemsVO)p_channelTransferVO.getChannelVoucherItemsVoList().get(i)).getProductName() + "</td>" +
                                                   "<td style='width: 12%;'>" + new VomsProductDAO().getNameFromVoucherType(p_con,((ChannelVoucherItemsVO)p_channelTransferVO.getChannelVoucherItemsVoList().get(i)).getVoucherType()) + "</td>" +
                                                   "</tr>";
                              }
                              else
                              {
                                    if(!isHeaderAdded) {
                                          isHeaderAdded = true;
                                          message1 = "<br>" + "<table><tr>"
                                                      + "   <td> S.No. </td>"
                                                      + "   <td>"+ getMessage(locale, "o2c.email.notification.denomination") + "</td>"
                                                      + " <td>"+ getMessage(locale, "o2c.email.notification.quantity") + "</td>"
                                                      + "<td>"+ getMessage(locale, "o2c.email.notification.voucherType") + "</td>"
                                                      + "</tr>";
                                    }
                                    message1 = message1 + "<tr><td>" +(i + 1) + "</td>" + 
                                                   "<td>" + ((ChannelVoucherItemsVO)p_channelTransferVO.getChannelVoucherItemsVoList().get(i)).getTransferMrp() + "</td>" +  
                                                   "<td>" + ((ChannelVoucherItemsVO)p_channelTransferVO.getChannelVoucherItemsVoList().get(i)).getRequiredQuantity() + "</td>" +
                                                   "<td>" + new VomsProductDAO().getNameFromVoucherType(p_con,((ChannelVoucherItemsVO)p_channelTransferVO.getChannelVoucherItemsVoList().get(i)).getVoucherType()) + "</td>" +
                                                   "</tr>";
                              }
                        }
                        message = message + message1 + "</table>";
                  }
            }
			
			final boolean isAttachment = false;
			final String pathofFile = "";
			final String fileNameTobeDisplayed = "";
			String to = "";
			if (!BTSLUtil.isNullString(p_roleCode)) {
				//subject = messages.getMessage(locale,"o2c.email.notification.content");
				subject = getMessage(locale,p_subject);
				to = p_channelTransferDAO.getEmailIdOfApprover(p_con, p_roleCode, p_channelTransferVO.getToUserID());
			} else {
				//subject = messages.getMessage(locale,"o2c.email.notification.subject.user");
				subject = getMessage(locale,p_subject);
				to = p_channelTransferVO.getEmail();
			}
			//subject =  messages.getMessage(locale,p_subject);

			if (_log.isDebugEnabled()) {
				_log.debug("MAIL CONTENT",message );
			}
			// Send email
			EMailSender.sendMail(to, from, bcc, cc, subject, message, isAttachment, pathofFile, fileNameTobeDisplayed);
		} catch (Exception e) {
			if (_log.isDebugEnabled()) {
				_log.error(methodName, " Email sending failed" + e.getMessage());
			}
			_log.errorTrace(methodName, e);
		}
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Exiting ....");
		}
	}
	
	/**
	 * 
	 * @param status
	 * @param message
	 * @param errorCode
	 * @param transactionID
	 * @param voucherProfileID
	 */
	private void createSuccessResponse(int status, String message, String errorCode, String transactionID) {
		BaseResponse baseResponse = new BaseResponse();
		baseResponse.setStatus(status);
    	baseResponse.setMessage(message);
    	baseResponse.setMessageCode(errorCode);
    	successList.add(baseResponse);
    	successCount += 1;
	}
	
	/**
	 * 
	 * @param rowCount
	 */
	private void createFinalResponse(int totalApprCount){
		String message = null;
		int successCount = totalApprCount - failureCount;
		if(totalApprCount == 0) {
			response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			message = getMessage(locale,PretupsErrorCodesI.O2C_APP_REQUEST_EMPTY, null);
			response.setMessage(message);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}else if(failureCount == totalApprCount) {
			response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			message = RestAPIStringParser.getMessage(new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
					PretupsErrorCodesI.ALL_FAIL, null);
			response.setMessage(message);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);;
		}else {
			response.setStatus(String.valueOf(HttpStatus.SC_OK));
			message = RestAPIStringParser.getMessage(new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
					PretupsErrorCodesI.PARTIAL_PROCESS, new String[] { String.valueOf(successCount), String.valueOf(totalApprCount) });
			
			response.setMessage(message);
			responseSwag.setStatus(HttpStatus.SC_OK);
		}
		response.setSuccessList((ArrayList) successList);
		response.setErrorMap(errorMap);
	}
	
	/**
	 * 
	 * @param locale
	 * @param errorCode
	 * @return
	 */
	public String getMessage(Locale locale, String errorCode) 
	{
		return RestAPIStringParser.getMessage(locale,errorCode, null);
	}
	/**
	 * 
	 * @param locale
	 * @param errorCode
	 * @param args
	 * @return
	 */
	public String getMessage(Locale locale, String errorCode, String[] args) 
	{
		return RestAPIStringParser.getMessage(locale, errorCode, args);
	}
//	/**
//	 * 
//	 * @param date
//	 * @return
//	 * @throws ParseException
//	 */
//	private boolean isGreaterOrEqualToCurrentDate(String date) throws ParseException{
//		Date currentDate = new Date();
//		Date date1 = new SimpleDateFormat("dd/MM/yy").parse(date);
//		return (date1.compareTo(currentDate) >= 0) ? true: false;
//	}

	
	/*
	 * @Override public O2CTransferDetailsResponseVO
	 * enquiryDetail(MultiValueMap<String, String> headers, String transactionID,
	 * HttpServletResponse responseSwag) { final String methodName =
	 * "enquiryDetail"; if (log.isDebugEnabled()) { log.debug(methodName,
	 * "Entered"); } Connection con = null; MComConnectionI mcomCon = null;
	 * O2CTransferDetailsResponseVO responseVO = null; ChannelTransferEnquiryModel
	 * theForm = null; UserDAO userDao = null; OAuthUser oAuthUser = null;
	 * OAuthUserData oAuthUserData = null; Locale locale = null; try {
	 * 
	 * // locale = new Locale((String)
	 * PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), //
	 * (String)
	 * PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)); //
	 * validate token oAuthUser = new OAuthUser(); oAuthUserData = new
	 * OAuthUserData(); oAuthUser.setData(oAuthUserData);
	 * OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
	 * 
	 * mcomCon = new MComConnection(); con = mcomCon.getConnection(); theForm = new
	 * ChannelTransferEnquiryModel(); responseVO = new
	 * O2CTransferDetailsResponseVO(); userDao = new UserDAO(); final ChannelUserVO
	 * loginUserVO = userDao.loadAllUserDetailsByLoginID(con,
	 * oAuthUser.getData().getLoginid());
	 * 
	 * if( !PretupsI.USER_TYPE_OPERATOR.equalsIgnoreCase(loginUserVO.getUserType())
	 * ) { throw new BTSLBaseException(this, methodName,
	 * PretupsErrorCodesI.NOT_AUTHORIZED_TO_REVERSE_O2C, PretupsI.RESPONSE_FAIL,
	 * null); }
	 * 
	 * if(BTSLUtil.isNullString(transactionID)) { throw new BTSLBaseException(this,
	 * methodName, PretupsErrorCodesI.TRANSFER_ID_REQUIRED, PretupsI.RESPONSE_FAIL,
	 * null); }else { transactionID = transactionID.trim(); }
	 * 
	 * final ChannelTransferVO channelTransferVO =
	 * this.loadChannelTransferDetails(con, loginUserVO, transactionID);
	 * this.constructFormFromVO(theForm, channelTransferVO); final ArrayList
	 * itemsList = ChannelTransferBL.loadChannelTransferItemsWithBalances(con,
	 * channelTransferVO.getTransferID(), channelTransferVO.getNetworkCode(),
	 * channelTransferVO.getNetworkCodeFor(), channelTransferVO.getToUserID());
	 * 
	 * long totTax1 = 0L, totTax2 = 0L, totTax3 = 0L, totReqQty = 0L, totStock = 0L,
	 * totComm = 0L, totMRP = 0L, totOthComm=0L, otfValue=0L; double mrpAmt = 0.0 ,
	 * firAppQty = 0.0, secAppQty = 0.0, thrAppQty = 0.0, commissionQty =
	 * 0.0,recQty=0.0; if (itemsList != null && !itemsList.isEmpty()) {
	 * ChannelTransferItemsVO channelTransferItemsVO = null; for (int i = 0, j =
	 * itemsList.size(); i < j; i++) { channelTransferItemsVO =
	 * (ChannelTransferItemsVO) itemsList.get(i); mrpAmt =
	 * channelTransferItemsVO.getReceiverCreditQty() *
	 * channelTransferItemsVO.getUnitValue() / (((Integer)
	 * (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).
	 * intValue());
	 * channelTransferItemsVO.setProductMrpStr(PretupsBL.getDisplayAmount(mrpAmt));
	 * otfValue +=channelTransferItemsVO.getOtfAmount(); totTax1 +=
	 * channelTransferItemsVO.getTax1Value(); totTax2 +=
	 * channelTransferItemsVO.getTax2Value(); totTax3 +=
	 * channelTransferItemsVO.getTax3Value(); totComm +=
	 * channelTransferItemsVO.getCommValue(); commissionQty +=
	 * channelTransferItemsVO.getCommQuantity(); recQty
	 * +=channelTransferItemsVO.getReceiverCreditQty(); if(((Boolean)
	 * (PreferenceCache.getSystemPreferenceValue(PreferenceI.OTH_COM_CHNL))).
	 * booleanValue()){ totOthComm += channelTransferItemsVO.getOthCommValue(); }
	 * totMRP += mrpAmt; totReqQty += channelTransferItemsVO.getRequiredQuantity();
	 * totStock += channelTransferItemsVO.getWalletbalance(); if
	 * (!BTSLUtil.isNullString(channelTransferItemsVO.getFirstApprovedQuantity())) {
	 * firAppQty +=
	 * Double.parseDouble(channelTransferItemsVO.getFirstApprovedQuantity()); } else
	 * { channelTransferItemsVO.setFirstApprovedQuantity("NA"); } if
	 * (!BTSLUtil.isNullString(channelTransferItemsVO.getSecondApprovedQuantity()))
	 * { secAppQty +=
	 * Double.parseDouble(channelTransferItemsVO.getSecondApprovedQuantity()); }
	 * else { channelTransferItemsVO.setSecondApprovedQuantity("NA"); } if
	 * (!BTSLUtil.isNullString(channelTransferItemsVO.getThirdApprovedQuantity())) {
	 * thrAppQty +=
	 * Double.parseDouble(channelTransferItemsVO.getThirdApprovedQuantity()); } else
	 * { channelTransferItemsVO.setThirdApprovedQuantity("NA"); } } }
	 * theForm.setTotalComm(PretupsBL.getDisplayAmount(totComm));
	 * theForm.setTotalTax1(PretupsBL.getDisplayAmount(totTax1));
	 * theForm.setTotalTax2(PretupsBL.getDisplayAmount(totTax2));
	 * theForm.setTotalTax3(PretupsBL.getDisplayAmount(totTax3));
	 * theForm.setTotalStock(PretupsBL.getDisplayAmount(totStock));
	 * theForm.setTotalReqQty(PretupsBL.getDisplayAmount(totReqQty));
	 * theForm.setTotalMRP(PretupsBL.getDisplayAmount(totMRP));
	 * theForm.setTotalOthComm(PretupsBL.getDisplayAmount(totOthComm));
	 * theForm.setTotalOtf(PretupsBL.getDisplayAmount(otfValue));
	 * theForm.setCommissionQuantity(PretupsBL.getDisplayAmount(commissionQty));
	 * theForm.setReceiverCreditQuantity(PretupsBL.getDisplayAmount(recQty)); if
	 * (BTSLUtil.floatEqualityCheck(firAppQty, 0d, "!=")) {
	 * theForm.setFirstLevelApprovedQuantity(String.valueOf(firAppQty)); } else {
	 * theForm.setFirstLevelApprovedQuantity("NA"); } if
	 * (BTSLUtil.floatEqualityCheck(secAppQty, 0d, "!=")) {
	 * theForm.setSecondLevelApprovedQuantity(String.valueOf(secAppQty)); } else {
	 * theForm.setSecondLevelApprovedQuantity("NA"); } if
	 * (BTSLUtil.floatEqualityCheck(thrAppQty, 0d, "!=")) {
	 * theForm.setThirdLevelApprovedQuantity(String.valueOf(thrAppQty)); } else {
	 * theForm.setThirdLevelApprovedQuantity("NA"); }
	 * theForm.setTransferItemsList(itemsList);
	 * 
	 * // setting response responseVO.setChannelTransferVO(channelTransferVO);
	 * responseVO.setTransferDetails(theForm); String msg =
	 * RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.TXN_STATUS_SUCCESS,
	 * null); responseVO.setMessageCode(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
	 * responseVO.setMessage(msg); responseVO.setStatus(HttpStatus.SC_OK); } catch
	 * (BTSLBaseException be) { log.error(methodName, "Exception:e=" + be);
	 * log.errorTrace(methodName, be); String msg =
	 * RestAPIStringParser.getMessage(locale, be.getMessageKey(), be.getArgs());
	 * responseVO.setMessageCode(be.getMessageKey()); responseVO.setMessage(msg);
	 * 
	 * if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
	 * responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
	 * responseVO.setStatus(HttpStatus.SC_UNAUTHORIZED); } else {
	 * responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
	 * responseVO.setStatus(HttpStatus.SC_BAD_REQUEST); } } catch (ParseException e)
	 * { log.error(methodName, "Exception:e=" + e); log.errorTrace(methodName, e);
	 * responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
	 * responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
	 * responseVO.setMessageCode(e.toString()); responseVO.
	 * setMessage("Your request cannot be processed at this time, please try again later."
	 * ); } catch (Exception e) { log.error(methodName, "Exception:e=" + e);
	 * log.errorTrace(methodName, e);
	 * responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
	 * responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
	 * responseVO.setMessageCode(e.toString()); responseVO.
	 * setMessage("Your request cannot be processed at this time, please try again later."
	 * ); } finally { // setDatesToDisplayInForm(theForm); if (mcomCon != null) {
	 * mcomCon.close("O2cTxnReversalServiceImpl#" +methodName); mcomCon = null; } if
	 * (log.isDebugEnabled()) { log.debug(methodName, "Exiting forward=" +
	 * responseVO); } }
	 * 
	 * return responseVO;
	 * 
	 * }
	 */
	
	
	
	
	
	/**
	 *  Transfer approval details for Approval level one,two and three
	 *  
	 */
	@Override
	public O2CApprovalTxnDetailsResponseVO transferApprovalDetails(MultiValueMap<String, String> headers, 
			String transactionID, HttpServletResponse responseSwag) {
		
		final String METHOD_NAME = "transferApprovalConfirmation";
		if (_log.isDebugEnabled()) {
			_log.debug("transferApprovalConfirmation", "Entered");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		
		O2CApprovalTxnDetailsResponseVO theForm = null;
		UserDAO userDao = null;
		OAuthUser oAuthUser = null;
		OAuthUserData oAuthUserData = null;
		Locale locale = null;
		
		try {
			
			theForm = new O2CApprovalTxnDetailsResponseVO();
			locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE),
					(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
			// validate token
			oAuthUser = new OAuthUser();
			oAuthUserData = new OAuthUserData();
			oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
			userDao = new UserDAO();
			final ChannelUserVO loginUserVO = userDao.loadAllUserDetailsByLoginID(con,
					oAuthUser.getData().getLoginid());
			
			if( !PretupsI.USER_TYPE_OPERATOR.equalsIgnoreCase(loginUserVO.getUserType()) ) {
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.NOT_AUTHORIZED_TO_REVERSE_O2C, PretupsI.RESPONSE_FAIL, null);
			}

			if(BTSLUtil.isNullString(transactionID)) {
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.TRANSFER_ID_REQUIRED, PretupsI.RESPONSE_FAIL, null);
			}else {
				transactionID = transactionID.trim();
			}
			
//			boolean userProductMultipleWallet = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET);
//			boolean messageToPrimaryRequired = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MESSAGE_TO_PRIMARY_REQUIRED);
			boolean secondaryNumberAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED);
			

			// load transfer details of given transactionID
			final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
			final ChannelTransferVO channelTransferVO = new ChannelTransferVO();
			
			channelTransferVO.setTransferID(transactionID);
			channelTransferVO.setNetworkCode(loginUserVO.getNetworkID());
			channelTransferVO.setNetworkCodeFor(loginUserVO.getNetworkID());
			channelTransferDAO.loadChannelTransfersVO(con, channelTransferVO);
			
			// validate toUser
			this.validateUserInformation(con, channelTransferVO.getToUserID(), theForm);
			
			// construct responseVO
			this.constructFormFromVO(theForm, channelTransferVO);
			
			theForm.setCloseTransaction(false);
			
			if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(theForm.getCurrentApprovalLevel()) ) {
				if (channelTransferVO.getRequestedQuantity() <= channelTransferVO.getFirstApproverLimit()) {
					theForm.setCloseTransaction(true);
				}
			}
			else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(theForm.getCurrentApprovalLevel()) ) {
				if (Long.parseLong(channelTransferVO.getLevelOneApprovedQuantity()) <= channelTransferVO.getSecondApprovalLimit()) {
					theForm.setCloseTransaction(true);
				}
			}
			else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(theForm.getCurrentApprovalLevel()) ) {
					theForm.setCloseTransaction(true);
			}
			
			// check secondary number allowed : TODO
			if (secondaryNumberAllow) {
				final UserDAO userDAO = new UserDAO();
				UserPhoneVO phoneVO = userDAO.loadUserAnyPhoneVO(con, theForm.getPrimaryTxnNum());
				if ("Y".equalsIgnoreCase(phoneVO.getPrimaryNumber())) {
					theForm.setToPrimaryMSISDN(phoneVO.getMsisdn());
					theForm.setPrimaryNumber(true);
				} else {
					phoneVO = userDAO.loadUserPhoneVO(con, phoneVO.getUserId());
					theForm.setToPrimaryMSISDN(phoneVO.getMsisdn());
					theForm.setPrimaryNumber(false);
				}
			}
			
			
			// load transaction details
			if("V".equals(channelTransferVO.getTransferSubType())){
				this.loadVoucherDetails(con, channelTransferVO, theForm);
			}else{
				this.loadStockDetails(con, channelTransferVO, theForm);
			}
			
			// setting response
			theForm.setChannelTransferVO(channelTransferVO);
			theForm.setCommissionProfileID(channelTransferVO.getCommProfileSetId());
			theForm.setTransferInitatorLoginID(channelTransferVO.getTransferInitatorLoginID());
			theForm.setCommissionProfileVersion(channelTransferVO.getCommProfileVersion());
			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.TXN_STATUS_SUCCESS, null);
			theForm.setMessageCode(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
			theForm.setMessage(msg);
			theForm.setStatus(HttpStatus.SC_OK);
			
		} catch (BTSLBaseException be) {
			_log.error(METHOD_NAME, "Exception:e=" + be);
			_log.errorTrace(METHOD_NAME, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), be.getArgs());
			theForm.setMessageCode(be.getMessageKey());
			theForm.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				theForm.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				theForm.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
        } catch (ParseException e) {
            _log.error(METHOD_NAME, "Exception:e=" + e);
            _log.errorTrace(METHOD_NAME, e);
            theForm.setStatus(HttpStatus.SC_BAD_REQUEST);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			theForm.setMessageCode(e.toString());
			theForm.setMessage("Your request cannot be processed at this time, please try again later.");
		} catch (Exception e) {
			_log.error(METHOD_NAME, "Exception:e=" + e);
			_log.errorTrace(METHOD_NAME, e);
			theForm.setStatus(HttpStatus.SC_BAD_REQUEST);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			theForm.setMessageCode(e.toString());
			theForm.setMessage("Your request cannot be processed at this time, please try again later.");
		} finally {
			if (mcomCon != null) {
				mcomCon.close(this.getClass().getName() +METHOD_NAME);
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) {
				_log.debug(this.getClass().getName(), "Exiting forward=" + METHOD_NAME);
			}
		}
		return theForm;
        
	}
	
	/**
	 * 
	 * @param theForm
	 * @param p_channelTransferVO
	 * @throws ParseException
	 * @throws BTSLBaseException
	 */
	private void constructFormFromVO(O2CApprovalTxnDetailsResponseVO theForm, ChannelTransferVO p_channelTransferVO) throws ParseException, BTSLBaseException
	
	{
		if (_log.isDebugEnabled()) {
			_log.debug("constructFromFromVO", "Entered theForm  " + theForm + "  p_channelTransferVO  " + p_channelTransferVO);
		}
		theForm.setTransferNum(p_channelTransferVO.getTransferID());
		theForm.setUserNameTmp(p_channelTransferVO.getToUserName());
		theForm.setDomainName(p_channelTransferVO.getDomainCodeDesc());
		theForm.setGeoDomainNameForUser(p_channelTransferVO.getGrphDomainCodeDesc());
		theForm.setPrimaryTxnNum(p_channelTransferVO.getUserMsisdn());
		theForm.setCategoryName(p_channelTransferVO.getReceiverCategoryDesc());
		theForm.setGardeDesc(p_channelTransferVO.getReceiverGradeCodeDesc());
		theForm.setErpCode(p_channelTransferVO.getErpNum());
		theForm.setProductType(((LookupsVO) LookupsCache.getObject(PretupsI.PRODUCT_TYPE, p_channelTransferVO.getProductType())).getLookupName());
		theForm.setCommissionProfileName(p_channelTransferVO.getCommProfileName());
		if (p_channelTransferVO.getExternalTxnDate() != null) {

			theForm.setExternalTxnDate(BTSLUtil.getDateStringFromDate(p_channelTransferVO.getExternalTxnDate()));
		} else {
			theForm.setExternalTxnDate(null);
		}
		theForm.setExternalTxnNum(p_channelTransferVO.getExternalTxnNum());
		theForm.setRefrenceNum(p_channelTransferVO.getReferenceNum());
		theForm.setRemarks(p_channelTransferVO.getChannelRemarks());
		theForm.setPaymentInstrumentList(LookupsCache.loadLookupDropDown(PretupsI.PAYMENT_INSTRUMENT_TYPE, true));

		/*
		 * if (theForm.isShowPaymentDetails()) {
		 * 
		 * if (!BTSLUtil.isNullString(p_channelTransferVO.getPayInstrumentType())) {
		 * theForm.setPaymentInstrumentName(((LookupsVO)
		 * LookupsCache.getObject(PretupsI.PAYMENT_INSTRUMENT_TYPE,
		 * p_channelTransferVO.getPayInstrumentType())) .getLookupName()); }
		 * theForm.setPaymentInstrumentCode(p_channelTransferVO.getPayInstrumentType());
		 * theForm.setPaymentInstNum(p_channelTransferVO.getPayInstrumentNum()); if
		 * (p_channelTransferVO.getPayInstrumentDate() != null) {
		 * 
		 * theForm.setPaymentInstrumentDate(BTSLUtil.getDateStringFromDate(
		 * p_channelTransferVO.getPayInstrumentDate())); } }
		 */
		
		if (!BTSLUtil.isNullString(p_channelTransferVO.getPayInstrumentType())) {
			theForm.setPaymentInstrumentName(((LookupsVO) LookupsCache.getObject(PretupsI.PAYMENT_INSTRUMENT_TYPE, p_channelTransferVO.getPayInstrumentType()))
					.getLookupName());
		}
		theForm.setPaymentInstrumentCode(p_channelTransferVO.getPayInstrumentType());
		theForm.setPaymentInstNum(p_channelTransferVO.getPayInstrumentNum());
		if (p_channelTransferVO.getPayInstrumentDate() != null) {

			theForm.setPaymentInstrumentDate(BTSLUtil.getDateStringFromDate(p_channelTransferVO.getPayInstrumentDate()));
		}

		theForm.setPaymentInstrumentAmt(PretupsBL.getDisplayAmount(p_channelTransferVO.getNetPayableAmount()));
		if (p_channelTransferVO.getTransferDate() != null) {

			theForm.setTransferDate(BTSLUtil.getDateStringFromDate(p_channelTransferVO.getTransferDate()));
		}
		
	    //theForm.setCommissionQuantity(PretupsBL.getDisplayAmount(p_channelTransferVO.getCommQty()));
		theForm.setPayableAmount(PretupsBL.getDisplayAmount(p_channelTransferVO.getPayableAmount()));
		theForm.setNetPayableAmount(PretupsBL.getDisplayAmount(p_channelTransferVO.getNetPayableAmount()));
		theForm.setApprove1Remark(p_channelTransferVO.getFirstApprovalRemark());
		theForm.setApprove2Remark(p_channelTransferVO.getSecondApprovalRemark());
		theForm.setFirstApprovalLimit(String.valueOf(p_channelTransferVO.getFirstApproverLimit()));
		theForm.setSecondApprovalLimit(String.valueOf(p_channelTransferVO.getSecondApprovalLimit()));
		theForm.setAddress(p_channelTransferVO.getFullAddress());
		if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_channelTransferVO.getTransferSubType())) {
		theForm.setTransferProfileName(p_channelTransferVO.getReceiverTxnProfileName());
		}
		else
		{
		theForm.setTransferProfileName(p_channelTransferVO.getSenderTxnProfileName());
		}
		theForm.setTotalInitialRequestedQuantity(p_channelTransferVO.getRequestedQuantityAsString());
		// code for o2c transfer quantity change by amit dated 28-May-2009
		theForm.setTotalMRP(p_channelTransferVO.getTransferMRPAsString());
		theForm.setDualCommissionType(p_channelTransferVO.getDualCommissionType());
		if (!BTSLUtil.isNullString(p_channelTransferVO.getLevelOneApprovedQuantity())) {
			theForm.setFirstLevelApprovedQuantity(PretupsBL.getDisplayAmount(Long.parseLong(p_channelTransferVO.getLevelOneApprovedQuantity())));
		}
		if (!BTSLUtil.isNullString(p_channelTransferVO.getLevelTwoApprovedQuantity())) {
			theForm.setSecondLevelApprovedQuantity(PretupsBL.getDisplayAmount(Long.parseLong(p_channelTransferVO.getLevelTwoApprovedQuantity())));
		}
		if (!BTSLUtil.isNullString(p_channelTransferVO.getLevelThreeApprovedQuantity())) {
			theForm.setThirdLevelApprovedQuantity(PretupsBL.getDisplayAmount(Long.parseLong(p_channelTransferVO.getLevelThreeApprovedQuantity())));
		}
		
		
		if (_log.isDebugEnabled()) {
			_log.debug("constructFromFromVO", "Exiting");
		}
	}

	
	/**
	 *  Method validateUserInformation.
	 * This method is used to validate the information associated with the
	 * userID.
	 * 
	 * @param p_con
	 * @param p_userID
	 * @param theForm
	 * @throws Exception
	 */
	private void validateUserInformation(Connection p_con, String p_userID, O2CApprovalTxnDetailsResponseVO theForm) throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug("validateUserInformation", "Entered userID = " + p_userID);
		}
		
		int receiverStatusAllowed = 0;
		final String METHOD_NAME = "validateUserInformation";

		final ChannelUserDAO channelUserDAO = new ChannelUserDAO();

		final Date curDate = new Date();
		final ChannelUserVO channelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(p_con, p_userID, false, curDate,false);
		
		if (channelUserVO == null) {
			throw new BTSLBaseException(this, METHOD_NAME, "channeltransfer.approval.msg.userdetailnotfound");
		} else {
			final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(channelUserVO.getNetworkID(), channelUserVO.getCategoryCode(), channelUserVO
					.getUserType(), PretupsI.REQUEST_SOURCE_TYPE_WEB);
			if (userStatusVO != null) {
				final String userStatusAllowed = userStatusVO.getUserReceiverAllowed();
				final String status[] = userStatusAllowed.split(",");
				for (int i = 0; i < status.length; i++) {
					if (status[i].equals(channelUserVO.getStatus())) {
						receiverStatusAllowed = 1;
					}
				}
			} else {
				throw new BTSLBaseException(this, METHOD_NAME, "channeltransfer.approval.msg.usersuspended");
			}
			theForm.setChannelUserStatus(channelUserVO.getStatus());
		}
		
		

		if (receiverStatusAllowed == 0) {
			throw new BTSLBaseException(this, METHOD_NAME, "channeltransfer.approval.msg.usersuspended");
		} 
		else if (channelUserVO.getCommissionProfileApplicableFrom().after(curDate)) {
			throw new BTSLBaseException(this, METHOD_NAME, "channeltransfer.approval.msg.usernocommprofileapplicable");
		} 
		else if (!PretupsI.YES.equals(channelUserVO.getCommissionProfileStatus())) {
			String args[] = null;
			args = new String[] { channelUserVO.getCommissionProfileLang2Msg() };
			

			/*
			 * final Locale locale = BTSLUtil.getBTSLLocale(request); final LocaleMasterVO
			 * localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale); if
			 * (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) { args = new String[]
			 * { channelUserVO.getCommissionProfileLang1Msg() }; }
			 */
			
			throw new BTSLBaseException(this, METHOD_NAME, "channeltransfer.approval.msg.notactive.commporfile", args);
		} 
		else if (!PretupsI.YES.equals(channelUserVO.getTransferProfileStatus())) {
			throw new BTSLBaseException(this, METHOD_NAME, "channeltransfer.approval.msg.notactive.transferprofile");
		}

		// to check user status
		if (channelUserVO.getInSuspend() != null && PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(channelUserVO.getInSuspend())) {
			throw new BTSLBaseException(this, METHOD_NAME, "channeltransfer.approval.msg.user.insuspend");
		}
		theForm.setDomainTypeCode(channelUserVO.getDomainTypeCode());
	
	}
	
	/**
	 * if transactionID belongs to voucher(transfer_subtype = V),
	 *  this method will be called
	 *  
	 * @param con
	 * @param channelTransferVO
	 * @param theForm
	 * @throws BTSLBaseException
	 */
	private void loadStockDetails(Connection con, ChannelTransferVO channelTransferVO,
			O2CApprovalTxnDetailsResponseVO theForm) throws BTSLBaseException {

		final String METHOD_NAME = "loadStockDetails";
		

		String externalTxnMandatoryDomainType = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_MANDATORY_DOMAINTYPE);

		final ArrayList itemsList = ChannelTransferBL.loadChannelTransferItemsWithBalances(con,
				channelTransferVO.getTransferID(), channelTransferVO.getNetworkCode(),
				channelTransferVO.getNetworkCodeFor(), channelTransferVO.getToUserID());
		
		double totTax1 = 0, totTax2 = 0, totTax3 = 0, totComm = 0, totMRP = 0, firAppQty = 0, secAppQty = 0,
				thrAppQty = 0, totOthComm = 0;
		long totStock = 0, totReqQty = 0, totalInitialReqQty = 0, totalOTFVal = 0;
		double mrpAmt = 0, commissionQty = 0, senderDebitQty = 0, receiverCreditQty = 0;
		if (itemsList != null && !itemsList.isEmpty()) {
			ChannelTransferItemsVO channelTransferItemsVO = null;
			for (int i = 0, j = itemsList.size(); i < j; i++) {
				channelTransferItemsVO = (ChannelTransferItemsVO) itemsList.get(i);
				if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType())) {
					mrpAmt = channelTransferItemsVO.getReceiverCreditQty()
							* Long.parseLong(PretupsBL.getDisplayAmount(channelTransferItemsVO.getUnitValue()));
				}

				channelTransferItemsVO.setProductMrpStr(theForm.getTotalMRP());
				totTax1 += channelTransferItemsVO.getTax1Value();
				totTax2 += channelTransferItemsVO.getTax2Value();
				totTax3 += channelTransferItemsVO.getTax3Value();
				totComm += channelTransferItemsVO.getCommValue();
				totOthComm += channelTransferItemsVO.getOthCommValue();
				totMRP += mrpAmt;
				/*
				 * if (theForm.getApprovalLevel() == 1) { totReqQty +=
				 * channelTransferItemsVO.getRequiredQuantity(); } else if
				 * (theForm.getApprovalLevel() == 2) { totReqQty +=
				 * PretupsBL.getSystemAmount(channelTransferItemsVO.getFirstApprovedQuantity());
				 * } else if (theForm.getApprovalLevel() == 3) { totReqQty +=
				 * PretupsBL.getSystemAmount(channelTransferItemsVO.getSecondApprovedQuantity())
				 * ; }
				 */
				totReqQty += channelTransferItemsVO.getRequiredQuantity();
				totalInitialReqQty += channelTransferItemsVO.getInitialRequestedQuantity();
				totStock += channelTransferItemsVO.getAfterTransSenderPreviousStock();
				totalOTFVal += channelTransferItemsVO.getOtfAmount();
				// if(SystemPreferences.POSITIVE_COMM_APPLY){
				commissionQty += channelTransferItemsVO.getCommQuantity();
				senderDebitQty += channelTransferItemsVO.getSenderDebitQty();
				receiverCreditQty += channelTransferItemsVO.getReceiverCreditQty();
				if (!BTSLUtil.isNullString(channelTransferItemsVO.getFirstApprovedQuantity())) {
					firAppQty += Double.parseDouble(channelTransferItemsVO.getFirstApprovedQuantity());
				}
				if (!BTSLUtil.isNullString(channelTransferItemsVO.getSecondApprovedQuantity())) {
					secAppQty += Double.parseDouble(channelTransferItemsVO.getSecondApprovedQuantity());
				}
				if (!BTSLUtil.isNullString(channelTransferItemsVO.getThirdApprovedQuantity())) {
					thrAppQty += Double.parseDouble(channelTransferItemsVO.getThirdApprovedQuantity());
				}
				theForm.setTransferMultipleOff(channelTransferItemsVO.getTransferMultipleOf());

				// }
			}
		}
		theForm.setTotalComm(PretupsBL.getDisplayAmount(totComm));
		theForm.setTotalTax1(PretupsBL.getDisplayAmount(totTax1));
		theForm.setTotalTax2(PretupsBL.getDisplayAmount(totTax2));
		theForm.setTotalTax3(PretupsBL.getDisplayAmount(totTax3));
		theForm.setPayableAmountApproval(theForm.getPayableAmount());
		theForm.setNetPayableAmountApproval(theForm.getNetPayableAmount());
		theForm.setCommissionQuantity(PretupsBL.getDisplayAmount(commissionQty));
		if (_log.isDebugEnabled()) {
			_log.debug("viewTransferDetails", "totStock" + totStock);
		}
		theForm.setTotalStock(PretupsBL.getDisplayAmount(totStock));

		if (_log.isDebugEnabled()) {
			_log.debug("viewTransferDetails", " totStock" + theForm.getTotalStock());
		}

		theForm.setTotalOtfValue(PretupsBL.getDisplayAmount(totalOTFVal));
		theForm.setTotalReqQty(PretupsBL.getDisplayAmount(totReqQty));
		theForm.setTotalInitialRequestedQuantity(PretupsBL.getDisplayAmount(totalInitialReqQty));
		theForm.setTotalOthComm(PretupsBL.getDisplayAmount(totOthComm));
		if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType())) {
			theForm.setTotalMRP(PretupsBL.getDisplayAmount(totMRP));
		}

		theForm.setTransferItemList(itemsList);
		theForm.setValidatePaymentInstruments(true);
		// For Mali --- +ve Commision Apply
		// if(SystemPreferences.POSITIVE_COMM_APPLY) {

		theForm.setSenderDebitQuantity(PretupsBL.getDisplayAmount(senderDebitQty));
		theForm.setReceiverCreditQuantity(PretupsBL.getDisplayAmount(receiverCreditQty));
		// }
		theForm.setFirstLevelApprovedQuantity(new DecimalFormat("#############.###").format(firAppQty));
		theForm.setSecondLevelApprovedQuantity(new DecimalFormat("#############.###").format(secAppQty));
		theForm.setThirdLevelApprovedQuantity(new DecimalFormat("#############.###").format(thrAppQty));
		// now if domaintype is in the preference value for the mandatory of
		// the exttxn number.
		// final String externalTxnMandatoryDomainType = externalTxnMandatoryDomainType;
		if (PretupsI.YES.equals(theForm.getExternalTxnMandatory())) {
			theForm.setExternalTxnMandatory(PretupsI.NO);

			if (BTSLUtil.isNullString(externalTxnMandatoryDomainType)) {
				theForm.setExternalTxnMandatory(PretupsI.YES);
			} else {
				final String domainTypeArr[] = externalTxnMandatoryDomainType.split(",");
				for (int i = 0, j = domainTypeArr.length; i < j; i++) {
					if (theForm.getDomainTypeCode().equals(domainTypeArr[i])) {
						theForm.setExternalTxnMandatory(PretupsI.YES);
						break;
					}
				}
			}
		}

	}
	
	
	/**
	 * if transactionID belongs to voucher(transfer_subtype = V),
	 *  this method will be called
	 *  
	 * @param con
	 * @param channelTransferVO
	 * @param theForm
	 * @throws BTSLBaseException
	 */
	private void loadVoucherDetails(Connection con, ChannelTransferVO channelTransferVO,
			O2CApprovalTxnDetailsResponseVO theForm) throws BTSLBaseException {

		final String METHOD_NAME = "loadVoucherDetails";
		VomsBatchVO vomsOrderVO = null;
		// final int length =
		// Integer.parseInt(Constants.getProperty("VOMS_ORDER_SLAB_LENGTH"));
		ArrayList slabslist = new ArrayList();
		ArrayList vomsProductlist = null;
		ArrayList vomsCategoryList = null;
		VomsCategoryWebDAO vomsCategorywebDAO = null;
		ChannelVoucherItemsVO voucherItemVO = null;
		VomsCategoryVO vomsCategoryVO = null;
		String mrp = null;;
		final VomsProductDAO vomsProductDAO = new VomsProductDAO();
		String activemrpstr = null;
		ArrayList voucherTypeList = new ArrayList<VomsCategoryVO>();
		vomsCategorywebDAO = new VomsCategoryWebDAO();
//		UserVO userVO = this.getUserFormSession(request);

		ArrayList channleVoucherItemList = new ChannelTransferDAO().loadChannelVoucherItemsList(con,
				channelTransferVO.getTransferID(), channelTransferVO.getTransferDate());
		
		int length = channleVoucherItemList.size();
		channelTransferVO.setChannelVoucherItemsVoList(channleVoucherItemList);
		channelTransferVO.setChannelTransferitemsVOList(
				new ChannelTransferDAO().loadChannelTransferItems(con, channelTransferVO.getTransferID()));
		theForm.setTransferItemList((ArrayList) channelTransferVO.getChannelTransferitemsVOList().clone());
		if (length > 0) {
			vomsCategoryVO = new VomsCategoryVO();
			vomsCategoryVO.setVoucherType(((ChannelVoucherItemsVO) channleVoucherItemList.get(0)).getVoucherType());
			vomsCategoryVO.setName(((ChannelVoucherItemsVO) channleVoucherItemList.get(0)).getVoucherType());
			vomsCategoryVO.setStatus("Y");
			voucherTypeList.add(vomsCategoryVO);
		}

		ArrayList<String> mrplist = new ArrayList<>();
		if (voucherTypeList.isEmpty()) {
			throw new BTSLBaseException(this, METHOD_NAME, "vmcategory.addsubcategoryforvoms.err.msg.noparentcatfound");
		}
		theForm.setVoucherTypeList(voucherTypeList);
		if (theForm.getVoucherTypeList() != null && theForm.getVoucherTypeList().size() == 1) {
			theForm.setVoucherType(theForm.getVoucherTypeList().get(0).getVoucherType());
			theForm.setVoucherTypeDesc(((ChannelVoucherItemsVO) channleVoucherItemList.get(0)).getVoucherTypeDesc());

			vomsProductlist = vomsProductDAO.loadProductDetailsList(con, theForm.getVoucherType(),
					"'" + VOMSI.VOMS_STATUS_ACTIVE + "'", false, "",
					((ChannelVoucherItemsVO) channleVoucherItemList.get(0)).getNetworkCode(),
					((ChannelVoucherItemsVO) channleVoucherItemList.get(0)).getSegment());
			theForm.setVomsProductList(vomsProductlist);

			vomsCategoryList = vomsCategorywebDAO.loadCategoryList(con, theForm.getVoucherType(),
					VOMSI.VOMS_STATUS_ACTIVE, VOMSI.EVD_CATEGORY_TYPE_FIXED, true, channelTransferVO.getNetworkCode(),
					null);
			theForm.setVomsCategoryList(vomsCategoryList);

			slabslist = new ArrayList();
			if (vomsCategoryList.isEmpty()) {
				throw new BTSLBaseException(this, METHOD_NAME, "voms.orderinitiate.noactivedenominations");
			}
			for (int i = 0; i < vomsCategoryList.size(); i++) {
				vomsCategoryVO = (VomsCategoryVO) vomsCategoryList.get(i);
				if (BTSLUtil.isNullString(activemrpstr)) {
					activemrpstr = Double.toString(vomsCategoryVO.getMrp());
					mrp = Double.toString(vomsCategoryVO.getMrp());
					mrplist.add(mrp);
				} else {
					activemrpstr = activemrpstr + "," + vomsCategoryVO.getMrp();
					mrp = Double.toString(vomsCategoryVO.getMrp());
					mrplist.add(mrp);
				}
			}
		}

		theForm.setSegment(((ChannelVoucherItemsVO) channleVoucherItemList.get(0)).getSegment());
		theForm.setSegmentDesc(BTSLUtil.getSegmentDesc(theForm.getSegment()));

		theForm.setVomsActiveMrp(activemrpstr);
		theForm.setMrpList(mrplist);
		VomsProductVO vo = null;
		ArrayList arList = null;
		Iterator itr = null;
		for (int i = 0; i < length; i++) {
			vomsOrderVO = new VomsBatchVO();
			voucherItemVO = (ChannelVoucherItemsVO) channleVoucherItemList.get(i);
			vomsOrderVO.setSeq_id(Long.valueOf(voucherItemVO.getSNo()).intValue());
			vomsOrderVO.setDenomination(voucherItemVO.getTransferMrp() + ".0");
			vomsOrderVO.setQuantity(voucherItemVO.getRequiredQuantity() + "");
			vomsOrderVO.setFromSerialNo(voucherItemVO.getFromSerialNum());
			vomsOrderVO.setToSerialNo(voucherItemVO.getToSerialNum());
			itr = vomsProductlist.iterator();
			arList = new ArrayList();
			while (itr.hasNext()) {
				vo = (VomsProductVO) itr.next();
				if (BTSLUtil.floatEqualityCheck((double) voucherItemVO.getTransferMrp(), (double) vo.getMrp(), "==")) {
					arList.add(vo);
					vomsOrderVO.setProductName(vo.getProductName());
				}
			}
			vomsOrderVO.setPreQuantity(voucherItemVO.getRequiredQuantity() + "");
			vomsOrderVO.setPreFromSerialNo(voucherItemVO.getFromSerialNum());
			vomsOrderVO.setPreToSerialNo(voucherItemVO.getToSerialNum());
			vomsOrderVO.setPreProductId(voucherItemVO.getProductId());
			vomsOrderVO.setProductlist(arList);
			slabslist.add(vomsOrderVO);
		}

		theForm.setSlabsList(slabslist);

		long totTax1 = 0, totTax2 = 0, totTax3 = 0, totRequestedQty = 0, payableAmount = 0, netPayableAmt = 0,
				totTransferedAmt = 0, totalMRP = 0, totcommission = 0;
		long commissionQty = 0, senderDebitQty = 0, receiverCreditQty = 0, totalOTFVal = 0;

		channelTransferVO.setChannelTransferitemsVOList(
				new ChannelTransferDAO().loadChannelTransferItems(con, channelTransferVO.getTransferID()));
		ChannelTransferItemsVO transferItemsVO = null;
		int itemsLists = channelTransferVO.getChannelTransferitemsVOList().size();
		for (int k = 0; k < itemsLists; k++) {
			transferItemsVO = (ChannelTransferItemsVO) channelTransferVO.getChannelTransferitemsVOList().get(k);
			totTax1 += transferItemsVO.getTax1Value();
			totTax2 += transferItemsVO.getTax2Value();
			totTax3 += transferItemsVO.getTax3Value();
			totcommission += transferItemsVO.getCommValue();
			if (transferItemsVO.getRequestedQuantity() != null
					&& BTSLUtil.isDecimalValue(transferItemsVO.getRequestedQuantity())) {
				totRequestedQty += PretupsBL.getSystemAmount(transferItemsVO.getRequestedQuantity());
				if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType())) {

					totTransferedAmt += transferItemsVO.getReceiverCreditQty()
							* Long.parseLong(PretupsBL.getDisplayAmount(transferItemsVO.getUnitValue()));
				} else {
					totTransferedAmt += (Double.parseDouble(transferItemsVO.getRequestedQuantity())
							* transferItemsVO.getUnitValue());
				}
			}
			payableAmount += transferItemsVO.getPayableAmount();
			netPayableAmt += transferItemsVO.getNetPayableAmount();
			totalMRP += transferItemsVO.getProductTotalMRP();
			commissionQty += transferItemsVO.getCommQuantity();
			senderDebitQty += transferItemsVO.getSenderDebitQty();
			receiverCreditQty += transferItemsVO.getReceiverCreditQty();
			totalOTFVal += transferItemsVO.getOtfAmount();
			theForm.setTransferMultipleOff(transferItemsVO.getTransferMultipleOf());

		}

		theForm.setTotalMRP(transferItemsVO.getProductMrpStr());
		theForm.setTotalNetPayableAmount(PretupsBL.getDisplayAmount(netPayableAmt));
		theForm.setTotalPayableAmount(PretupsBL.getDisplayAmount(payableAmount));
		theForm.setNetPayableAmountApproval(PretupsBL.getDisplayAmount(netPayableAmt));
		theForm.setPayableAmountApproval(PretupsBL.getDisplayAmount(payableAmount));
		theForm.setTotalReqQty(PretupsBL.getDisplayAmount(totRequestedQty));
		theForm.setTotalTransferedAmount(PretupsBL.getDisplayAmount(totTransferedAmt));
		theForm.setTotalTax1(PretupsBL.getDisplayAmount(totTax1));
		theForm.setTotalTax2(PretupsBL.getDisplayAmount(totTax2));
		theForm.setTotalTax3(PretupsBL.getDisplayAmount(totTax3));
		theForm.setTotalComm(PretupsBL.getDisplayAmount(totcommission));
		theForm.setTotalCommValue(PretupsBL.getDisplayAmount(totcommission));
		theForm.setCommissionQuantity(PretupsBL.getDisplayAmount(commissionQty));
		theForm.setSenderDebitQuantity(PretupsBL.getDisplayAmount(senderDebitQty));
		theForm.setReceiverCreditQuantity(PretupsBL.getDisplayAmount(receiverCreditQty));
		theForm.setTotalOtfValue(PretupsBL.getDisplayAmount(totalOTFVal));
		if (theForm.isShowPaymentDetails()) {
			theForm.setPaymentInstDesc(BTSLUtil
					.getOptionDesc(theForm.getPaymentInstrumentCode(), theForm.getPaymentInstrumentList()).getLabel());
		}

		theForm.setTransferItemList(channelTransferVO.getChannelTransferitemsVOList());

	}




}
