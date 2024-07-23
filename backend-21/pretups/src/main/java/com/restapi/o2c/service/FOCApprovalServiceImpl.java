package com.restapi.o2c.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.BaseResponse;
import com.btsl.common.BaseResponseMultiple;
import com.btsl.common.ErrorMap;
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
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.OneLineTXNLog;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
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
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.client.pretups.gateway.businesslogic.USSDPushMessage;



/**
 * 
 * @author md.sohail FOCApprovalServiceImpl will process the approval or reject
 *         request for FOC(O2CCommission)
 * 
 */
@Service("FOCApprovalServiceI")
public class FOCApprovalServiceImpl implements FOCApprovalServiceI {

	private final Log _log = LogFactory.getLog(this.getClass().getName());
	private HttpServletResponse responseSwag = null;
	private BaseResponseMultiple response = null;
	private ErrorMap errorMap = null;
	private List<BaseResponse> successList = null;
	private int failureCount;

	@SuppressWarnings("rawtypes")
	@Override
	public BaseResponseMultiple processFOCApproval(FOCApprovalRequestVO focApprovalRequest,
			MultiValueMap<String, String> headers, HttpServletResponse responseSwag) throws BTSLBaseException {

		final String methodName = "processFOCApproval";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, focApprovalRequest);
			_log.debug("processFOCApproval", "Entered");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		ApprovalVO theForm = null;
		List<FOCApprovalData> focApprovalDataList = null;
		OAuthUser oAuthUser = null;
		OAuthUserData oAuthUserData = null;
		Locale locale = null;
		int totalApprCount = 0;
		failureCount = 0;
		response = new BaseResponseMultiple();
		try {
             
			response.setService("focApproval");
			// Authentication
			oAuthUser = new OAuthUser();
			oAuthUserData = new OAuthUserData();
			oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);

			this.responseSwag = responseSwag;
			errorMap = new ErrorMap();
			successList = new ArrayList<>();
			locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
			theForm = new ApprovalVO();

			if (!BTSLUtil.isNullObject(focApprovalRequest)
					&& !BTSLUtil.isNullOrEmptyList((ArrayList) focApprovalRequest.getFocApprovalRequests())) {
				totalApprCount = focApprovalRequest.getFocApprovalRequests().size();
			} else {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.O2C_APP_REQUEST_EMPTY);
			}

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			focApprovalDataList = focApprovalRequest.getFocApprovalRequests();

			// getting loogedIn user details
			final UserDAO userDao = new UserDAO();
			ChannelUserVO sessionUserVO = (ChannelUserVO) userDao.loadAllUserDetailsByLoginID(con,
					oAuthUser.getData().getLoginid());
			
			this.validateOperatorUserType( sessionUserVO ); // throw exception

			List<RowErrorMsgLists> outerRowErrorMsgLists = null;
			RowErrorMsgLists outerRowErrorMsgListObj = null;
			try {
				for (FOCApprovalData focApprovalData : focApprovalDataList) {
					// check for preferences
					this.checkPreferences(theForm, focApprovalData);
					theForm.setExternalTxnDate(focApprovalData.getExtTxnDate());
					theForm.setExternalTxnNum(focApprovalData.getExtTxnNumber());
					ArrayList<RowErrorMsgList> rowError = new ArrayList<>();
					// validating request
					boolean isValid = this.validateRequest(con, focApprovalData, theForm, rowError, locale);
					if (!isValid) {
						failureCount += 1;
						outerRowErrorMsgLists = new ArrayList<>();
						outerRowErrorMsgListObj = new RowErrorMsgLists();
						outerRowErrorMsgListObj.setRowErrorMsgList(rowError);
						outerRowErrorMsgLists.add(outerRowErrorMsgListObj);
						continue;
					}
                    this.detailViewFOCApproval( theForm );
					this.viewFOCTransferDetails(theForm, locale);
					this.saveFOCApproval(theForm, sessionUserVO, locale);
				} // loop end

			} catch (BTSLBaseException be) 
			{
				failureCount += 1;
				_log.error(methodName, "Exception:e=" + be);
				_log.errorTrace(methodName, be);
				outerRowErrorMsgLists = new ArrayList<RowErrorMsgLists>();
				outerRowErrorMsgListObj = new RowErrorMsgLists();
				ArrayList<RowErrorMsgList> rowError = new ArrayList<RowErrorMsgList>();

				RowErrorMsgList rowErrorMsgList = new RowErrorMsgList();
				ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
				MasterErrorList masterErrorListObj = new MasterErrorList();
				masterErrorListObj.setErrorCode(be.getMessageKey());
				if (!BTSLUtil.isNullorEmpty(be.getArgs())) {
					masterErrorListObj.setErrorMsg(getMessage(locale, be.getMessageKey(), be.getArgs()));
				} else {
					masterErrorListObj.setErrorMsg(getMessage(locale, be.getMessageKey()));
				}

				masterErrorLists.add(masterErrorListObj);
				RowErrorMsgLists rowErrorMsgListsObj2 = new RowErrorMsgLists();
				rowErrorMsgListsObj2.setMasterErrorList(masterErrorLists);
				ArrayList<RowErrorMsgLists> innerRow = new ArrayList<RowErrorMsgLists>();
				innerRow.add(rowErrorMsgListsObj2);
				rowErrorMsgList.setRowErrorMsgLists(innerRow);
				rowError.add(rowErrorMsgList);
				outerRowErrorMsgListObj.setRowErrorMsgList(rowError);
				outerRowErrorMsgLists.add(outerRowErrorMsgListObj);

			} catch (Exception e) {
				_log.error(methodName, "Exceptin:e=" + e);
				throw new BTSLBaseException(this, methodName, e.getMessage());
			}

			errorMap.setRowErrorMsgLists(outerRowErrorMsgLists);
			this.createFinalResponse(totalApprCount, locale);

		} catch (BTSLBaseException be) 
		{
			_log.error(methodName, "Exception:e=" + be);
			_log.errorTrace(methodName, be);
			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				response.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
			String errorCode = be.getMessageKey();
			if (BTSLUtil.isNullorEmpty(errorCode)) {
				errorCode = PretupsErrorCodesI.O2C_APPROVAL_WENT_WRONG;
			}
			String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), errorCode,
					be.getArgs());
			response.setMessageCode(errorCode);
			response.setMessage(resmsg);
		} catch (Exception e) {
			_log.error(methodName, "Exceptin:e=" + e);
			_log.errorTrace(methodName, e);
			response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setMessageCode(e.toString());
			response.setMessage(e.toString() + " : " + e.getMessage());
		} finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("FOCApprovalServiceImpl#" + methodName);
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
	 * @param focApprovalData
	 */
	private void checkPreferences(ApprovalVO theForm, FOCApprovalData focApprovalData) {

		if (PretupsI.O2C_APPROVE.equalsIgnoreCase(focApprovalData.getStatus())) {

			/*
			 * if FOC_ODR_APPROVAL_LVL = 1 level 1 approval required, if
			 * FOC_ODR_APPROVAL_LVL = 2 level 2 approval required' if FOC_ODR_APPROVAL_LVL =
			 * 3 level 3 approval required'
			 */
			// set the level of approval from the system preference
			try {
				theForm.setFocOrderApprovalLevel(
						((Integer) PreferenceCache.getSystemPreferenceValue(PretupsI.FOC_ORDER_APPROVAL_LVL))
								.intValue());
			} catch (Exception ex) {
				theForm.setFocOrderApprovalLevel(1);
				_log.error("loadDomainList",
						"Exception: = System Preference value FOC_ODR_APPROVAL_LVL not properly defined");
				_log.errorTrace("checkPreferences", ex);
			}

			/*
			 * 1) To check external txn num is required at which level. if its value contain
			 * 0 then externalTxnexist is Y at order initiation time if its value contain 1
			 * then externalTxnexist is Y at level 1 approval time if its value contain 2
			 * then externalTxnexist is Y at level 2 approval time if its value contain 3
			 * then externalTxnexist is Y at level 3 approval time 2) To check external txn
			 * mandatory is required at which level. if its value contain 0 then
			 * externalTxnMandatory is Y at order initiation time if its value contain 1
			 * then externalTxnMandatory is Y at level 1 approval time if its value contain
			 * 2 then externalTxnMandatory is Y at level 2 approval time if its value
			 * contain 3 then externalTxnMandatory is Y at level 3 approval time
			 */
			final String externalTxnLevel = (String) PreferenceCache
					.getSystemPreferenceValue(PretupsI.TRANSFER_EXTERNAL_TXN_LEVEL);
			final String externalTxnMandatory = SystemPreferences.EXTERNAL_TXN_MANDATORY_FORFOC;
			if (PretupsI.CHANNEL_TRANSFER_ORDER_NEW.equalsIgnoreCase(focApprovalData.getCurrentStatus())) {
				if (!BTSLUtil.isNullString(externalTxnLevel) && externalTxnLevel.indexOf("1") != -1) {
					theForm.setExternalTxnExist(PretupsI.YES);
					if (!BTSLUtil.isNullString(externalTxnMandatory) && externalTxnMandatory.indexOf("1") != -1) {
						theForm.setExternalTxnMandatory(PretupsI.YES);
					}
				}
			} else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equalsIgnoreCase(focApprovalData.getCurrentStatus())) {
				if (!BTSLUtil.isNullString(externalTxnLevel) && externalTxnLevel.indexOf("2") != -1) {
					theForm.setExternalTxnExist(PretupsI.YES);
					if (!BTSLUtil.isNullString(externalTxnMandatory) && externalTxnMandatory.indexOf("2") != -1) {
						theForm.setExternalTxnMandatory(PretupsI.YES);
					}
				}
			} else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equalsIgnoreCase(focApprovalData.getCurrentStatus())) {
				if (!BTSLUtil.isNullString(externalTxnLevel) && externalTxnLevel.indexOf("3") != -1) {
					theForm.setExternalTxnExist(PretupsI.YES);
					if (!BTSLUtil.isNullString(externalTxnMandatory) && externalTxnMandatory.indexOf("3") != -1) {
						theForm.setExternalTxnMandatory(PretupsI.YES);
					}
				}
			}

		} else {
			theForm.setExternalTxnExist(PretupsI.NO);
			theForm.setExternalTxnMandatory(PretupsI.NO);
		}
	}

	/**
	 * 
	 * @param con
	 * @param focApprovalData
	 * @param theForm
	 * @param rowError
	 * @param locale
	 * @return
	 * @throws BTSLBaseException
	 * @throws ParseException
	 */
	private boolean validateRequest(Connection con, FOCApprovalData focApprovalData, ApprovalVO theForm,
			ArrayList<RowErrorMsgList> rowError, Locale locale) throws BTSLBaseException, ParseException {

		RowErrorMsgList rowErrorMsgList = new RowErrorMsgList();
		ArrayList<RowErrorMsgLists> rowErrorMsgLists = new ArrayList<RowErrorMsgLists>();

		boolean isValidReq = true;
		boolean addToRow = false;
		ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		ChannelUserVO receiverVO = null;

		// toMsisdn : mandatory
		RowErrorMsgLists rowErrorMsgListsObj2 = null;
		ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
		if (!BTSLUtil.isNullorEmpty(focApprovalData.getToMsisdn())) {
			receiverVO = channelUserDAO.loadChannelUserDetails(con, focApprovalData.getToMsisdn());
			if (BTSLUtil.isNullorEmpty(receiverVO)) {
				String[] args = new String[] { focApprovalData.getToMsisdn() };
				throw new BTSLBaseException(this, "validateRequest", PretupsErrorCodesI.INVALID_TO_MSISDN, args);
			}
			theForm.setUserCode(receiverVO.getUserCode());
			theForm.setNetworkCode(receiverVO.getNetworkID());
			theForm.setUserID(receiverVO.getUserID());
		} else {
			MasterErrorList masterErrorListObj = new MasterErrorList();
			masterErrorListObj.setErrorCode(PretupsErrorCodesI.NULL_OR_INVALID_VALUE_IN_REQ);
			String[] args = { "toMsisdn" };
			masterErrorListObj.setErrorMsg(getMessage(locale, PretupsErrorCodesI.NULL_OR_INVALID_VALUE_IN_REQ, args));
			isValidReq = false;
			masterErrorLists.add(masterErrorListObj);
			addToRow = true;
		}
		if (addToRow) {
			rowErrorMsgListsObj2 = new RowErrorMsgLists();
			rowErrorMsgListsObj2.setMasterErrorList(masterErrorLists);
			rowErrorMsgLists.add(rowErrorMsgListsObj2);
		}

		// txnId
		addToRow = false;
		masterErrorLists = new ArrayList<>();
		if (!BTSLUtil.isNullorEmpty(focApprovalData.getTxnId())) { // mandatory for approval/rejection
			theForm.setTransferNumber(focApprovalData.getTxnId());
		} else {
			MasterErrorList masterErrorListObj = new MasterErrorList();
			masterErrorListObj.setErrorCode(PretupsErrorCodesI.NULL_OR_INVALID_VALUE_IN_REQ);
			String[] args = { "txnId" };
			masterErrorListObj.setErrorMsg(getMessage(locale, PretupsErrorCodesI.NULL_OR_INVALID_VALUE_IN_REQ, args));
			isValidReq = false;
			addToRow = true;
			masterErrorLists.add(masterErrorListObj);
		}
		if (addToRow) {
			rowErrorMsgListsObj2 = new RowErrorMsgLists();
			rowErrorMsgListsObj2.setMasterErrorList(masterErrorLists);
			rowErrorMsgLists.add(rowErrorMsgListsObj2);
		}

		// refNumber : non-mandatory
		if (!BTSLUtil.isNullorEmpty(focApprovalData.getRefNumber())) {
			theForm.setRefrenceNum(focApprovalData.getRefNumber());
		}

		MasterErrorList masterErrorListObj = null;
		// extTxnDate and extTxnNumber
		if (PretupsI.YES.equals(theForm.getExternalTxnMandatory())) { // mandatory for approval, non-mandatory for
																		// rejection
			addToRow = false;
			masterErrorLists = new ArrayList<>();

			if (!BTSLUtil.isNullorEmpty(focApprovalData.getExtTxnDate()) && BTSLUtil.isValidDatePattern(focApprovalData.getExtTxnDate())) {
				theForm.setExternalTxnDate(focApprovalData.getExtTxnDate());
			} else {
				masterErrorListObj = new MasterErrorList();
				masterErrorListObj.setErrorCode(PretupsErrorCodesI.NULL_OR_INVALID_VALUE_IN_REQ);
				String[] args = { "extTxnDate" };
				masterErrorListObj
						.setErrorMsg(getMessage(locale, PretupsErrorCodesI.NULL_OR_INVALID_VALUE_IN_REQ, args));
				isValidReq = false;
				addToRow = true;
				masterErrorLists.add(masterErrorListObj);
			}
			if (addToRow) {
				rowErrorMsgListsObj2 = new RowErrorMsgLists();
				rowErrorMsgListsObj2.setMasterErrorList(masterErrorLists);
				rowErrorMsgLists.add(rowErrorMsgListsObj2);
			}
			// extTxnNumber
			addToRow = false;
			masterErrorLists = new ArrayList<>();
			if (!BTSLUtil.isNullorEmpty(focApprovalData.getExtTxnNumber()) ) {
				theForm.setExternalTxnNum(focApprovalData.getExtTxnNumber());
			} else {
				masterErrorListObj = new MasterErrorList();
				masterErrorListObj.setErrorCode(PretupsErrorCodesI.NULL_OR_INVALID_VALUE_IN_REQ);
				String[] args = { "extTxnNumber" };
				masterErrorListObj
						.setErrorMsg(getMessage(locale, PretupsErrorCodesI.NULL_OR_INVALID_VALUE_IN_REQ, args));
				isValidReq = false;
				addToRow = true;
				masterErrorLists.add(masterErrorListObj);
			}
			if (addToRow) {
				rowErrorMsgListsObj2 = new RowErrorMsgLists();
				rowErrorMsgListsObj2.setMasterErrorList(masterErrorLists);
				rowErrorMsgLists.add(rowErrorMsgListsObj2);
			}

		} else {
			
			//validate date format
			if (!BTSLUtil.isNullorEmpty(focApprovalData.getExtTxnDate()) && !BTSLUtil.isValidDatePattern(focApprovalData.getExtTxnDate())) 
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
			theForm.setExternalTxnDate(focApprovalData.getExtTxnDate());
			theForm.setExternalTxnNum(focApprovalData.getExtTxnNumber());
		}

		// remarks : non-mandatory
		addToRow = false;
		masterErrorLists = new ArrayList<>();
		if (!BTSLUtil.isNullString(focApprovalData.getRemarks()) && focApprovalData.getRemarks().length() > 100) {
			masterErrorListObj = new MasterErrorList();
			masterErrorListObj.setErrorCode("pretups.jsp.messaage.textareacharsaremorethanmax");
			String[] args = { "Approval remarks", "100" };
			masterErrorListObj
					.setErrorMsg(getMessage(locale, "pretups.jsp.messaage.textareacharsaremorethanmax", args));
			isValidReq = false;
			masterErrorLists.add(masterErrorListObj);
		}
		if (addToRow) {
			rowErrorMsgListsObj2 = new RowErrorMsgLists();
			rowErrorMsgListsObj2.setMasterErrorList(masterErrorLists);
			rowErrorMsgLists.add(rowErrorMsgListsObj2);
		}

		// language1: non-mandatory
		theForm.setDefaultLang(focApprovalData.getLanguage1());
		addToRow = false;
		masterErrorLists = new ArrayList<>();
		if (!BTSLUtil.isNullString(focApprovalData.getLanguage1()) && focApprovalData.getLanguage1().length() > 30) {
			masterErrorListObj = new MasterErrorList();
			masterErrorListObj.setErrorCode("pretups.jsp.messaage.textareacharsaremorethanmax");
			String[] args = { "Languge1", "30" };
			masterErrorListObj
					.setErrorMsg(getMessage(locale, "pretups.jsp.messaage.textareacharsaremorethanmax", args));
			isValidReq = false;
			masterErrorLists.add(masterErrorListObj);

		}
		if (addToRow) {
			rowErrorMsgListsObj2 = new RowErrorMsgLists();
			rowErrorMsgListsObj2.setMasterErrorList(masterErrorLists);
			rowErrorMsgLists.add(rowErrorMsgListsObj2);
		}

		// language2: non-mandatory2
		theForm.setSecondLang(focApprovalData.getLanguage2());
		addToRow = false;
		masterErrorLists = new ArrayList<>();
		if (!BTSLUtil.isNullString(focApprovalData.getLanguage1()) && focApprovalData.getLanguage1().length() > 30) {
			masterErrorListObj = new MasterErrorList();
			masterErrorListObj.setErrorCode("pretups.jsp.messaage.textareacharsaremorethanmax");
			String[] args = { "Languge", "30" };
			masterErrorListObj
					.setErrorMsg(getMessage(locale, "pretups.jsp.messaage.textareacharsaremorethanmax", args));
			isValidReq = false;
			masterErrorLists.add(masterErrorListObj);

		}
		if (addToRow) {
			rowErrorMsgListsObj2 = new RowErrorMsgLists();
			rowErrorMsgListsObj2.setMasterErrorList(masterErrorLists);
			rowErrorMsgLists.add(rowErrorMsgListsObj2);
		}

		// status
		addToRow = false;
		masterErrorLists = new ArrayList<MasterErrorList>();
		if (!BTSLUtil.isNullorEmpty(focApprovalData.getStatus())
				&& ("approve".equalsIgnoreCase(focApprovalData.getStatus())
						|| "reject".equalsIgnoreCase(focApprovalData.getStatus()))) {
			// mandatory for approval/rejection
			theForm.setStatus(focApprovalData.getStatus());
		} else {
			masterErrorListObj = new MasterErrorList();
			masterErrorListObj.setErrorCode(PretupsErrorCodesI.NULL_OR_INVALID_VALUE_IN_REQ);
			String[] args = { "status" };
			masterErrorListObj.setErrorMsg(getMessage(locale, PretupsErrorCodesI.NULL_OR_INVALID_VALUE_IN_REQ, args));
			isValidReq = false;
			addToRow = true;
			masterErrorLists.add(masterErrorListObj);
		}
		if (addToRow) {
			rowErrorMsgListsObj2 = new RowErrorMsgLists();
			rowErrorMsgListsObj2.setMasterErrorList(masterErrorLists);
			rowErrorMsgLists.add(rowErrorMsgListsObj2);
		}

		// currentStatus
		addToRow = false;
		masterErrorLists = new ArrayList<>();
		if (!BTSLUtil.isNullorEmpty(focApprovalData.getCurrentStatus())) {
			if (PretupsI.CHANNEL_TRANSFER_ORDER_NEW.equalsIgnoreCase(focApprovalData.getCurrentStatus())) {
				theForm.setRequestType("approval1");
				theForm.setApprove1Remark(focApprovalData.getRemarks());
			} else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equalsIgnoreCase(focApprovalData.getCurrentStatus())) {
				theForm.setRequestType("approval2");
				theForm.setApprove2Remark(focApprovalData.getRemarks());
			} else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equalsIgnoreCase(focApprovalData.getCurrentStatus())) {
				theForm.setRequestType("approval3");
				theForm.setApprove3Remark(focApprovalData.getRemarks());
			} else {
				masterErrorListObj = new MasterErrorList();
				masterErrorListObj.setErrorCode(PretupsErrorCodesI.NULL_OR_INVALID_VALUE_IN_REQ);
				String[] args = { "currentStatus" };
				masterErrorListObj
						.setErrorMsg(getMessage(locale, PretupsErrorCodesI.NULL_OR_INVALID_VALUE_IN_REQ, args));
				isValidReq = false;
				addToRow = true;
				masterErrorLists.add(masterErrorListObj);
			}
		} else {
			masterErrorListObj = new MasterErrorList();
			masterErrorListObj.setErrorCode(PretupsErrorCodesI.NULL_OR_INVALID_VALUE_IN_REQ);
			String[] args = { "currentStatus" };
			masterErrorListObj.setErrorMsg(getMessage(locale, PretupsErrorCodesI.NULL_OR_INVALID_VALUE_IN_REQ, args));
			isValidReq = false;
			addToRow = true;
			masterErrorLists.add(masterErrorListObj);
		}
		if (addToRow) {
			rowErrorMsgListsObj2 = new RowErrorMsgLists();
			rowErrorMsgListsObj2.setMasterErrorList(masterErrorLists);
			rowErrorMsgLists.add(rowErrorMsgListsObj2);
		}

		// setting inner row and return
		if (!isValidReq) {
			rowErrorMsgList.setRowErrorMsgLists(rowErrorMsgLists);
			rowError.add(rowErrorMsgList);
			return isValidReq;
		}

		// Loading channelTransferVO: for server side validation
		final ChannelTransferVO channelTransferVO = new ChannelTransferVO();
		this.loadChannelTransferVO(con, theForm, channelTransferVO);

		// currentStatus
		addToRow = false;
		masterErrorLists = new ArrayList<>();
		if (theForm.getUserID().equals(channelTransferVO.getToUserID())) // Validation for ext transaction number
		{
			if (!BTSLUtil.isNullorEmpty(channelTransferVO.getStatus())
					&& !channelTransferVO.getStatus().equals(focApprovalData.getCurrentStatus())) { // checking for
																									// current status
				masterErrorListObj = new MasterErrorList();
				masterErrorListObj.setErrorCode(PretupsErrorCodesI.NULL_OR_INVALID_VALUE_IN_REQ);
				String[] args = { "currentStatus" };
				masterErrorListObj
						.setErrorMsg(getMessage(locale, PretupsErrorCodesI.NULL_OR_INVALID_VALUE_IN_REQ, args));
				isValidReq = false;
				addToRow = true;
				masterErrorLists.add(masterErrorListObj);
			}
		} else {
			masterErrorListObj = new MasterErrorList();
			masterErrorListObj.setErrorCode(PretupsErrorCodesI.NULL_TRANSACTION_DETAILS);
			String[] args = { focApprovalData.getExtTxnNumber() };
			masterErrorListObj.setErrorMsg(getMessage(locale, PretupsErrorCodesI.NULL_TRANSACTION_DETAILS, args));
			isValidReq = false;
			addToRow = true;
			masterErrorLists.add(masterErrorListObj);
		}
		if (addToRow) {
			rowErrorMsgListsObj2 = new RowErrorMsgLists();
			rowErrorMsgListsObj2.setMasterErrorList(masterErrorLists);
			rowErrorMsgLists.add(rowErrorMsgListsObj2);
		}

		// setting inner row
		if (!isValidReq) {
			rowErrorMsgList.setRowErrorMsgLists(rowErrorMsgLists);
			rowError.add(rowErrorMsgList);
			return isValidReq;
		}
		theForm.setChannelTransferVO(channelTransferVO);
		return isValidReq;
	}

	/**
	 * 
	 * @param con
	 * @param theForm
	 * @param channelTransferVO
	 * @throws BTSLBaseException
	 */
	private void loadChannelTransferVO(Connection con, ApprovalVO theForm, ChannelTransferVO channelTransferVO)
			throws BTSLBaseException {
		channelTransferVO.setTransferID(theForm.getTransferNumber());
		channelTransferVO.setNetworkCode(theForm.getNetworkCode());
		channelTransferVO.setNetworkCodeFor(theForm.getNetworkCode());
		channelTransferVO.setTransferSubType("T");
		final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
		channelTransferDAO.loadChannelTransfersVO(con, channelTransferVO);
	}
	
	/**
	 * 
	 * @param theForm
	 * @throws BTSLBaseException
	 * @throws Exception
	 */
	public void detailViewFOCApproval( ApprovalVO theForm ) throws BTSLBaseException, Exception {
		
        final String methodName = "detailViewFOCApproval";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
		try {

			// only server side validation added to this method

			// The condition below is used to check if external transaction
			// number is mandatory
			// and in system preference external transaction number unique
			// required is true
			// then check if external transaction number is unique within
			// the system.
			if ("approve".equals(theForm.getStatus())
					&& (!BTSLUtil.isNullString(theForm.getExternalTxnNum())
							&& PretupsI.YES.equals(theForm.getExternalTxnMandatory()))
					|| (!BTSLUtil.isNullString(theForm.getExternalTxnNum())
							&& PretupsI.YES.equals(theForm.getExternalTxnExist()))) 
			{
				if (SystemPreferences.EXTERNAL_TXN_NUMERIC) {
					try {
						final long externalTxnIDLong = Long.parseLong(theForm.getExternalTxnNum());
						if (externalTxnIDLong < 0) {
							throw new BTSLBaseException(this, methodName, "message.channeltransfer.externaltxnnumbernotnumeric");
						}
						theForm.setExternalTxnNum(String.valueOf(externalTxnIDLong));
					} catch (Exception e) {
						_log.errorTrace(methodName, e);
						throw new BTSLBaseException(this, methodName, "message.channeltransfer.externaltxnnumbernotnumeric");
					}
				}
				if (SystemPreferences.EXTERNAL_TXN_UNIQUE) {
					mcomCon = new MComConnection();
					con = mcomCon.getConnection();
					final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
					final boolean isExternalTxnNotUnique = channelTransferDAO.isExtTxnExists(con,
							theForm.getExternalTxnNum(), theForm.getTransferNumber());
					if (isExternalTxnNotUnique) {
						throw new BTSLBaseException(this, "processOptToChannelTransfer", "message.channeltransfer.externaltxnnumbernotunique");
					}
				}
			}

		}catch (BTSLBaseException be) {
            _log.error(methodName, "Exception:e=" + be);
            throw be;
        } catch (Exception e) {
            _log.error(methodName, "Exception:e=" + e);
            throw e;
        } finally {
			if (mcomCon != null) {
				mcomCon.close("FOCApprovalServiceImpl#detailViewFOCApproval");
				mcomCon = null;
			}
			if (con != null) {
				con.close();
				con = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exit");
            }
        }
    }

	/**
	 * view the transfer details for different level one , level two and level three
	 * 
	 * @param theForm
	 * @param locale
	 * @throws Exception
	 */
	public void viewFOCTransferDetails(ApprovalVO theForm, Locale locale) throws Exception {
		final String methodName = "viewFOCTransferDetails";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered");
		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		try {

			final ChannelTransferVO channelTransferVO = theForm.getChannelTransferVO();
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			// validateUserInformation method throw an exception
			this.validateUserInformation(con, theForm, locale, channelTransferVO.getToUserID()); // userId: 2395

			theForm.setUserMsisdn(channelTransferVO.getUserMsisdn());
			if (SystemPreferences.SECONDARY_NUMBER_ALLOWED) {
				final UserDAO userDAO = new UserDAO();
				UserPhoneVO phoneVO = userDAO.loadUserAnyPhoneVO(con, theForm.getUserMsisdn()); // ydist
				if ("Y".equalsIgnoreCase(phoneVO.getPrimaryNumber())) {
					theForm.setToPrimaryMSISDN(phoneVO.getMsisdn());
				} else {
					phoneVO = userDAO.loadUserPhoneVO(con, phoneVO.getUserId());
					theForm.setToPrimaryMSISDN(phoneVO.getMsisdn());
				}
			}
			// Loading itemList
			final ArrayList itemsList = ChannelTransferBL.loadChannelTransferItemsWithBalances(con,
					channelTransferVO.getTransferID(), channelTransferVO.getNetworkCode(),
					channelTransferVO.getNetworkCodeFor(), channelTransferVO.getToUserID()); // call update itemList
			long totTax1 = 0L, totTax2 = 0L, totReqQty = 0L, totStock = 0L, totMRP = 0L;
			long mrpAmt = 0L;
			if (itemsList != null && !itemsList.isEmpty()) {
				ChannelTransferItemsVO channelTransferItemsVO = null;
				for (int i = 0, j = itemsList.size(); i < j; i++) {
					channelTransferItemsVO = (ChannelTransferItemsVO) itemsList.get(i);
					mrpAmt = channelTransferItemsVO.getRequiredQuantity()
							* Long.parseLong(PretupsBL.getDisplayAmount(channelTransferItemsVO.getUnitValue()));
					channelTransferItemsVO.setProductMrpStr(PretupsBL.getDisplayAmount(mrpAmt));
					totTax1 += channelTransferItemsVO.getTax1Value();
					totTax2 += channelTransferItemsVO.getTax2Value();
					// totTax3 += channelTransferItemsVO.getTax3Value();
					// totComm += channelTransferItemsVO.getCommValue();
					totMRP += mrpAmt;
					totReqQty += channelTransferItemsVO.getRequiredQuantity();
					// added by nilesh : condition for multiple wallet allowed
					if (SystemPreferences.MULTIPLE_WALLET_APPLY) {
						totStock += channelTransferItemsVO.getNetworkFOCStock();
					} else {
						totStock += channelTransferItemsVO.getNetworkStock();
					}
				}
			}

			// theForm.setTotalComm(PretupsBL.getDisplayAmount(totComm));
			theForm.setTotalTax1(PretupsBL.getDisplayAmount(totTax1));
			theForm.setTotalTax2(PretupsBL.getDisplayAmount(totTax2));
			// theForm.setTotalTax3(PretupsBL.getDisplayAmount(totTax3));
			theForm.setTotalStock(PretupsBL.getDisplayAmount(totStock));
			theForm.setTotalReqQty(PretupsBL.getDisplayAmount(totReqQty));
			theForm.setTotalMRP(PretupsBL.getDisplayAmount(totMRP));
			theForm.setTransferItemList(itemsList);

		} catch (BTSLBaseException be) {
			_log.error(methodName, "BTSLBaseException: " + be);
			throw be;
		} catch (ParseException e) {
			_log.error(methodName, "ParseException: " + e);
			throw e;
		} catch (Exception e) {
			_log.error(methodName, "Exception: " + e);
			throw e;
		} finally {
			if (mcomCon != null) {
				mcomCon.close("FOCApprovalServiceImpl#viewFOCTransferDetails");
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exiting: ");
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				_log.debug(methodName, "Exiting: ");
			}
		}
	}

	/**
	 * Method validateUserInformation. This method is used to validate the
	 * information associated with the userID.
	 * 
	 * Called from the viewFOCTransferDetails() method of the same class
	 * 
	 * @param p_con
	 * @param theForm
	 * @param locale
	 * @param p_userID
	 * @throws Exception
	 */
	private void validateUserInformation(Connection p_con, ApprovalVO theForm, Locale locale, String p_userID)
			throws Exception {
		final String methodName = "validateUserInformation";
		if (_log.isDebugEnabled()) {
			_log.debug("validateUserInformation", "Entered userID = " + p_userID);
		}
		boolean receiverStatusAllowed = false;
		try {

			final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
			final Date curDate = new Date();
			final ChannelUserVO channelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(p_con, p_userID, false,
					curDate, false); // ydist
			if (channelUserVO == null) {
				throw new BTSLBaseException(this, methodName,
						"channeltransfer.approvalvalidation.errormsg.usernotfound");
			} else {
				final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(channelUserVO.getNetworkID(),
						channelUserVO.getCategoryCode(), channelUserVO.getUserType(), PretupsI.REQUEST_SOURCE_TYPE_WEB);
				if (userStatusVO != null) {
					final String userStatusAllowed = userStatusVO.getUserReceiverAllowed();
					final String status[] = userStatusAllowed.split(",");
					for (int i = 0; i < status.length; i++) {
						if (status[i].equals(channelUserVO.getStatus())) {
							receiverStatusAllowed = true;
						}
					}
				} else {
					throw new BTSLBaseException(this, methodName,
							"channeltransfer.selectcategoryforfoctransfer.errormsg.usernotallowed",
							new String[] { theForm.getUserCode() });
				}
			}
			if (!receiverStatusAllowed) {
				throw new BTSLBaseException(this, methodName,
						"channeltransfer.approvalvalidation.errormsg.usersuspended");
			} else if (channelUserVO.getCommissionProfileApplicableFrom().after(curDate)) {
				throw new BTSLBaseException(this, methodName,
						"channeltransfer.approvalvalidation.errormsg.nocommprofileapplicable");
			}

			if (!PretupsI.YES.equals(channelUserVO.getCommissionProfileStatus())) {
				String args[] = null;
				args = new String[] { channelUserVO.getUserName(), channelUserVO.getCommissionProfileLang2Msg() };
				// ChangeID=LOCALEMASTER
				// commission profile suspend message has to be set in VO
				// Check which language message to be set from the locale master
				// table for the particular locale.
				final LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
				if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
					args = new String[] { channelUserVO.getUserName(), channelUserVO.getCommissionProfileLang1Msg() };
				}
				throw new BTSLBaseException(this, methodName,
						"channeltransfer.selectcategoryforfoctransfer.errormsg.commissionprofilenotactive", args);
			} else if (!PretupsI.YES.equals(channelUserVO.getTransferProfileStatus())) {
				final String args[] = { channelUserVO.getUserName() };
				throw new BTSLBaseException(this, methodName,
						"channeltransfer.selectcategoryforfoctransfer.errormsg.transferprofilenotactive", args);
			}
			// to check user status
			if (channelUserVO.getInSuspend() != null
					&& PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(channelUserVO.getInSuspend())) {
				throw new BTSLBaseException(this, methodName,
						"channeltransfer.selectcategoryforfoctransfer.errormsg.transfernotallowed");
			}
			theForm.setDomainTypeCode(channelUserVO.getDomainTypeCode());
			theForm.setChannelUserStatus(channelUserVO.getStatus());

		} catch (Exception e) {
			_log.error("validateUserInformation", "Exception:e=" + e);
			throw e;
		} finally {
			if (_log.isDebugEnabled()) {
				_log.debug("validateUserInformation", "Exiting");
			}
		}
	}

	/**
	 * 
	 * @param theForm
	 * @param sessionUserVO
	 * @param locale
	 * @throws BTSLBaseException
	 * @throws SQLException
	 * @throws Exception
	 */
	private void saveFOCApproval(ApprovalVO theForm, ChannelUserVO sessionUserVO, Locale locale)
			throws BTSLBaseException, SQLException {
		if (_log.isDebugEnabled()) {
			_log.debug("saveFOCApproval", "Entered");
		}
		try {
			if ("approve".equals(theForm.getStatus())) {
				this.orderApproval(theForm, sessionUserVO, locale); // pass session user: btchadm
			} else {
				this.cancelOrder(theForm, sessionUserVO, locale); // pass session user: btchadm
			}

		} catch (BTSLBaseException be) {
			_log.error("saveFOCApproval", "Exception=" + be);
			throw be;
		} catch (SQLException se) {
			_log.error("saveFOCApproval", "Exception=" + se);
			throw se;
		} catch (Exception e) {
			_log.error("saveFOCApproval", "Exception:e=" + e);
			throw e;
		}
	}

	/**
	 * 
	 * @param theForm
	 * @param channelUserVO
	 * @param locale
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	private void orderApproval(ApprovalVO theForm, ChannelUserVO channelUserVO, Locale locale)
			throws BTSLBaseException, SQLException {
		final String methodName = "orderApproval";
		if (_log.isDebugEnabled()) {
			_log.debug("orderApproval", "Entered");
		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		String[] array = null;
		boolean receiverMessageSendReq = false;
		boolean ussdReceiverMessageSendReq = false;

		OperatorUtilI operatorUtili = null;
		try {
			final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
			operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
		} catch (Exception e) {
			_log.errorTrace("orderApproval", e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"FOCApprovalServiceImpl[orderApproval]", "", "", "",
					"Exception while loading the class at the call:" + e.getMessage());
		}
		try {
			final Date date = new Date();

			final ChannelTransferVO channelTransferVO = theForm.getChannelTransferVO();

			String serviceType = PretupsI.SERVICE_TYPE_CHNL_O2C_INTR;
			receiverMessageSendReq = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.REC_MSG_SEND_ALLOW,
					channelUserVO.getNetworkCode(), serviceType)).booleanValue();
			ussdReceiverMessageSendReq = ((Boolean) PreferenceCache.getControlPreference(
					PreferenceI.USSD_REC_MSG_SEND_ALLOW, channelUserVO.getNetworkCode(), serviceType)).booleanValue();

			boolean sendOrderToApproval = false;
			String message = null;

			if ("approval1".equals(theForm.getRequestType())) {
				// message = Transfer Order Id {0} has been successful at Level
				// One
				message = "channeltransfer.foctransferapprovaldetailview.approval.level1success";
				channelTransferVO.setFirstApprovalRemark(theForm.getApprove1Remark());
				channelTransferVO.setFirstApprovedBy(channelUserVO.getUserID());
				channelTransferVO.setFirstApprovedOn(date);
				channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
				if (theForm.getFocOrderApprovalLevel() <= 1) {
					sendOrderToApproval = true;
				}
				channelTransferVO.setLevelOneApprovedQuantity(channelTransferVO.getRequestedQuantityAsString());
			} else if ("approval2".equals(theForm.getRequestType())) {
				// message = Transfer Order Id {0} has been successful at Level
				// Two
				message = "channeltransfer.foctransferapprovaldetailview.approval.level2success";
				channelTransferVO.setSecondApprovalRemark(theForm.getApprove2Remark());
				channelTransferVO.setSecondApprovedBy(channelUserVO.getUserID());
				channelTransferVO.setSecondApprovedOn(date);
				channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
				if (theForm.getFocOrderApprovalLevel() <= 2) {
					sendOrderToApproval = true;
				}
				channelTransferVO.setLevelTwoApprovedQuantity(channelTransferVO.getRequestedQuantityAsString());
			} else// request for approval 3
			{
				// message = FOC Transfer Order Id {0} has been approved
				// successfully
				message = "channeltransfer.foctransferapprovaldetailview.approval.success";
				channelTransferVO.setThirdApprovalRemark(theForm.getApprove3Remark());
				channelTransferVO.setThirdApprovedBy(channelUserVO.getUserID());
				channelTransferVO.setThirdApprovedOn(date);
				sendOrderToApproval = true;
				channelTransferVO.setLevelThreeApprovedQuantity(channelTransferVO.getRequestedQuantityAsString());
			}
			if (BTSLUtil.isNullString(theForm.getExternalTxnDate())) {
				channelTransferVO.setExternalTxnDate(null);
			}

			channelTransferVO.setExternalTxnDateAsString(theForm.getExternalTxnDate());
			// added for editable reference number
			channelTransferVO.setReferenceNum(BTSLUtil.NullToString(theForm.getRefrenceNum()));
			channelTransferVO.setExternalTxnNum(theForm.getExternalTxnNum());
			channelTransferVO.setModifiedBy(channelUserVO.getUserID());
			channelTransferVO.setModifiedOn(date);

			channelTransferVO.setDefaultLang(theForm.getDefaultLang());// Added
			// by
			// Dhiraj
			// on
			// 08/03/2007
			channelTransferVO.setSecondLang(theForm.getSecondLang());// Added by
			// Dhiraj
			// on
			// 08/03/2007

			// set the items list from form to VO
			channelTransferVO.setChannelTransferitemsVOList(theForm.getTransferItemList());
			channelTransferVO.setChannelUserStatus(theForm.getChannelUserStatus());
			// prepare the connection
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();

			if (sendOrderToApproval) {
				// message = FOC Transfer Order Id {0} has been approved
				// successfully
				message = "channeltransfer.foctransferapprovaldetailview.approval.success";

				channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
				if (SystemPreferences.MULTIPLE_WALLET_APPLY) {
					channelTransferVO.setWalletType(PretupsI.FOC_WALLET_TYPE);
				} else {
					channelTransferVO.setWalletType(PretupsI.SALE_WALLET_TYPE);
				}
				
				BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getExternalTxnDate());
				this.approveOrder(con, channelTransferVO, channelUserVO.getUserID(), date);
				OneLineTXNLog.log(channelTransferVO, null);
			}

			// commented as disscussed with Sanjay Sir, GSB, AC need not to be
			// updated in WEB
			// int updateCount =(new
			// ChannelUserDAO()).updateUserPhoneAfterTxn(con,channelTransferVO,channelTransferVO.getToUserCode(),channelTransferVO.getToUserID(),true);
			int updateCount = 0;
			if ("approval1".equals(theForm.getRequestType())) {
				updateCount = channelTransferDAO.updateChannelTransferApprovalLevelOne(con, channelTransferVO,
						sendOrderToApproval);
			} else if ("approval2".equals(theForm.getRequestType())) {
				updateCount = channelTransferDAO.updateChannelTransferApprovalLevelTwo(con, channelTransferVO,
						sendOrderToApproval);
			} else// level 3 approval
			{
				updateCount = channelTransferDAO.updateChannelTransferApprovalLevelThree(con, channelTransferVO,
						sendOrderToApproval);
			}

			if (updateCount <= 0) {
				mcomCon.finalRollback();
				throw new BTSLBaseException(this, methodName,
						"channeltransfer.foctransferapprovaldetailview.approval.notsuccess",
						new String[] { channelTransferVO.getTransferID() });
			} else {

				mcomCon.finalCommit();
				if (channelTransferVO.getStatus().equals(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE)) {
					if (!PretupsI.USER_STATUS_ACTIVE.equals(channelTransferVO.getChannelUserStatus())) {
						// int updatecount =
						// operatorUtili.changeUserStatusToActive(con,
						// channelTransferVO.getToUserID(),
						// channelTransferVO.getChannelUserStatus());
						int updatecount = 0;
						final String str[] = SystemPreferences.TXN_RECEIVER_USER_STATUS_CHANG.split(","); // "CH:Y,EX:Y".split(",");
						String newStatus[] = null;
						boolean changeStatusRequired = false;
						for (int i = 0; i < str.length; i++) {
							newStatus = str[i].split(":");
							if (newStatus[0].equals(channelTransferVO.getChannelUserStatus())) {
								changeStatusRequired = true;
								updatecount = operatorUtili.changeUserStatusToActive(con,
										channelTransferVO.getToUserID(), channelTransferVO.getChannelUserStatus(),
										newStatus[1]);
								break;
							}
						}
						if (changeStatusRequired) {
							if (updatecount > 0) {
								mcomCon.finalCommit();
							} else {
								mcomCon.finalRollback();
								throw new BTSLBaseException(this, methodName,
										"channeltransfer.foctransferapprovaldetailview.approval.notsuccess",
										new String[] { channelTransferVO.getTransferID() });
							}
						}

					}
				}

				if (sendOrderToApproval) {
					ChannelTransferBL.prepareUserBalancesListForLogger(channelTransferVO);
					final UserDAO userDAO = new UserDAO();
					UserPhoneVO phoneVO = null;
					UserPhoneVO primaryPhoneVOS = null;

					if (SystemPreferences.SECONDARY_NUMBER_ALLOWED) {
						if (SystemPreferences.MESSAGE_TO_PRIMARY_REQUIRED
								&& !((theForm.getUserMsisdn()).equalsIgnoreCase(theForm.getToPrimaryMSISDN()))) {
							primaryPhoneVOS = userDAO.loadUserAnyPhoneVO(con, theForm.getToPrimaryMSISDN());
						}
						phoneVO = userDAO.loadUserAnyPhoneVO(con, theForm.getUserMsisdn());
					} else {
						phoneVO = userDAO.loadUserPhoneVO(con, channelTransferVO.getToUserID());
					}
					final Object[] smsListArr = ChannelTransferBL.prepareSMSMessageListForReceiver(con,
							channelTransferVO, PretupsErrorCodesI.FOC_OPT_CHNL_TRANSFER_SMS2,
							PretupsErrorCodesI.FOC_OPT_CHNL_TRANSFER_SMS_BALSUBKEY);

					if (receiverMessageSendReq) {
						if (primaryPhoneVOS != null) {
							String focNotifyMsg = null;
							if (SystemPreferences.FOC_SMS_NOTIFY) {
								final LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
								if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
									focNotifyMsg = channelTransferVO.getDefaultLang();
								} else {
									focNotifyMsg = channelTransferVO.getSecondLang();
								}
								array = new String[] { channelTransferVO.getTransferID(),
										BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]),
										BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1]),
										channelTransferVO.getChannelRemarks(), focNotifyMsg };
							} else {
								array = new String[] { channelTransferVO.getTransferID(),
										BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]),
										BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1]),
										channelTransferVO.getChannelRemarks() };
							}
							// final BTSLMessages messages = new
							// BTSLMessages(PretupsErrorCodesI.FOC_OPT_CHNL_TRANSFER_SMS1, array);
							BTSLMessages messages = null;
							if (BTSLUtil.isNullString(channelTransferVO.getChannelRemarks()))
								messages = new BTSLMessages(PretupsErrorCodesI.FOC_OPT_CHNL_TRANSFER_SMS1, array);
							else
								messages = new BTSLMessages(PretupsErrorCodesI.FOC_OPT_CHNL_TRANSFER_SMS4, array);

							final PushMessage pushMessage = new PushMessage(primaryPhoneVOS.getMsisdn(), messages,
									channelTransferVO.getTransferID(), null, locale,
									channelTransferVO.getNetworkCode());
							pushMessage.push();
						}
						if (phoneVO != null) {
							String country = phoneVO.getCountry();
							String language = phoneVO.getPhoneLanguage();

							if (BTSLUtil.isNullString(country)) {
								country = (String) PreferenceCache
										.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
							}
							if (BTSLUtil.isNullString(language)) {
								language = (String) PreferenceCache
										.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
							}

							// Object []smsListArr =
							// ChannelTransferBL.prepareSMSMessageListForReceiver(con,channelTransferVO,PretupsErrorCodesI.FOC_OPT_CHNL_TRANSFER_SMS2,PretupsErrorCodesI.FOC_OPT_CHNL_TRANSFER_SMS_BALSUBKEY);
							String focNotifyMsg = null;

							if (SystemPreferences.FOC_SMS_NOTIFY) {
								final LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
								if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
									focNotifyMsg = channelTransferVO.getDefaultLang();
								} else {
									focNotifyMsg = channelTransferVO.getSecondLang();
								}
								array = new String[] { channelTransferVO.getTransferID(),
										BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]),
										BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1]),
										channelTransferVO.getChannelRemarks(), focNotifyMsg };
							} else {
								array = new String[] { channelTransferVO.getTransferID(),
										BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]),
										BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1]),
										channelTransferVO.getChannelRemarks() };
							}

							BTSLMessages messages = null;
							if (BTSLUtil.isNullString(channelTransferVO.getChannelRemarks()))
								messages = new BTSLMessages(PretupsErrorCodesI.FOC_OPT_CHNL_TRANSFER_SMS1, array);
							else
								messages = new BTSLMessages(PretupsErrorCodesI.FOC_OPT_CHNL_TRANSFER_SMS4, array);
							// final BTSLMessages messages = new
							// BTSLMessages(PretupsErrorCodesI.FOC_OPT_CHNL_TRANSFER_SMS1, array);
							final PushMessage pushMessage = new PushMessage(phoneVO.getMsisdn(), messages,
									channelTransferVO.getTransferID(), null, locale,
									channelTransferVO.getNetworkCode());
							pushMessage.push();
						} else {
							final String arr[] = { channelTransferVO.getToUserName() };
							String responseMessage = this.getMessage(locale,
									"channeltransfer.foctransferapprovaldetailview.phoneinfo.notexist.msg", arr);
							this.createSuccessResponse(200, responseMessage, message,
									channelTransferVO.getTransferID());
						}
					}

					if (ussdReceiverMessageSendReq) {
						if (primaryPhoneVOS != null) {
							String focNotifyMsg = null;
							if (SystemPreferences.FOC_SMS_NOTIFY) {
								LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
								if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage()))
									focNotifyMsg = channelTransferVO.getDefaultLang();
								else
									focNotifyMsg = channelTransferVO.getSecondLang();
								array = new String[] { channelTransferVO.getTransferID(),
										BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]),
										BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1]), focNotifyMsg };
							} else
								array = new String[] { channelTransferVO.getTransferID(),
										BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]),
										BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1]) };
							BTSLMessages messages = new BTSLMessages(PretupsErrorCodesI.FOC_OPT_CHNL_TRANSFER_SMS1,
									array);
							USSDPushMessage pushMessage = new USSDPushMessage(primaryPhoneVOS.getMsisdn(), messages,
									channelTransferVO.getTransferID(), null, locale,
									channelTransferVO.getNetworkCode());
							pushMessage.push();
						}
						if (phoneVO != null) {
							String country = phoneVO.getCountry();
							String language = phoneVO.getPhoneLanguage();

							if (BTSLUtil.isNullString(country)) {
								country = (String) PreferenceCache
										.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
							}
							if (BTSLUtil.isNullString(language)) {
								language = (String) PreferenceCache
										.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
							}

							// Object []smsListArr =
							// ChannelTransferBL.prepareSMSMessageListForReceiver(con,channelTransferVO,PretupsErrorCodesI.FOC_OPT_CHNL_TRANSFER_SMS2,PretupsErrorCodesI.FOC_OPT_CHNL_TRANSFER_SMS_BALSUBKEY);
							String focNotifyMsg = null;

							if (SystemPreferences.FOC_SMS_NOTIFY) {
								LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
								if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage()))
									focNotifyMsg = channelTransferVO.getDefaultLang();
								else
									focNotifyMsg = channelTransferVO.getSecondLang();
								array = new String[] { channelTransferVO.getTransferID(),
										BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]),
										BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1]), focNotifyMsg };
							} else
								array = new String[] { channelTransferVO.getTransferID(),
										BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]),
										BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1]) };
							BTSLMessages messages = new BTSLMessages(PretupsErrorCodesI.FOC_OPT_CHNL_TRANSFER_SMS1,
									array);
							USSDPushMessage pushMessage = new USSDPushMessage(phoneVO.getMsisdn(), messages,
									channelTransferVO.getTransferID(), null, locale,
									channelTransferVO.getNetworkCode());
							pushMessage.push();
						} else {
							String arr[] = { channelTransferVO.getToUserName() };
							String responseMessage = this.getMessage(locale,
									"channeltransfer.foctransferapprovaldetailview.phoneinfo.notexist.msg", arr);
							this.createSuccessResponse(200, responseMessage, message,
									channelTransferVO.getTransferID());
						}
					}

				}

				/*
				 * prepare the message for success response
				 */
				final String args[] = { channelTransferVO.getTransferID() };
				String responseMessage = this.getMessage(locale, message, args);
				this.createSuccessResponse(200, responseMessage, message, channelTransferVO.getTransferID());
			}
		} catch (BTSLBaseException be) {
			_log.error("orderApproval", "Exception:e=" + be);
			try {
				if (con != null) {
					con.rollback();
				}
			} catch (SQLException e1) {
				_log.errorTrace(methodName, e1);
			}
			throw be;
		} catch (SQLException se) {
			_log.error("orderApproval", "Exception:e=" + se);
			try {
				if (con != null) {
					con.rollback();
				}
			} catch (SQLException e1) {
				_log.errorTrace(methodName, e1);
			}
			throw se;
		} catch (Exception e) {
			_log.error("orderApproval", "Exception:e=" + e);
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
				mcomCon.close("FOCApprovalServiceImpl#orderApproval");
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) {
				_log.debug("orderApproval", "Exiting");
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				_log.debug(methodName, "Exiting: ");
			}
		}
	}

	/**
	 * if order clear all approval check then this method will approve the order and
	 * made entry in different tables as required by order approval.
	 * 
	 * @param p_con
	 * @param p_channelTransferVO
	 * @param p_userId            login user
	 * @param p_date              current date
	 * 
	 * @throws BTSLBaseException
	 */
	private void approveOrder(Connection p_con, ChannelTransferVO p_channelTransferVO, String p_userId, Date p_date)
			throws BTSLBaseException {
		final boolean debit = true;
		if (_log.isDebugEnabled()) {
			_log.debug("approveOrder", "Entered p_channelTransferVO  " + p_channelTransferVO + " p_userId " + p_userId
					+ " p_date " + p_date);
		}
		// prepare networkStockList and debit the network stock
		ChannelTransferBL.prepareNetworkStockListAndCreditDebitStock(p_con, p_channelTransferVO, p_userId, p_date,
				debit);
		ChannelTransferBL.updateNetworkStockTransactionDetails(p_con, p_channelTransferVO, p_userId, p_date);

		// update user daily balances
		final UserBalancesDAO userBalancesDAO = new UserBalancesDAO();
		userBalancesDAO.updateUserDailyBalances(p_con, p_date, constructBalanceVOFromTxnVO(p_channelTransferVO));

		// channel credit the user balance
		final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		// set the transfer date to the current date as it is treated as the
		// last transfer date in the user balances table.
		p_channelTransferVO.setTransferDate(p_date);
		if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
			channelUserDAO.creditUserBalancesForMultipleWallet(p_con, p_channelTransferVO, true, null);
		} else {
			channelUserDAO.creditUserBalances(p_con, p_channelTransferVO, true, null);
		}
		p_channelTransferVO.setTransactionCode(PretupsI.TRANSFER_TYPE_FOC);
		ChannelTransferBL.updateOptToChannelUserInCounts(p_con, p_channelTransferVO, null, p_date);
		if (_log.isDebugEnabled()) {
			_log.debug("approveOrder", "Exiting ");
		}
	}

	/**
	 * Cancel the order
	 * 
	 * Called from the saveFOCApproval() method of the same class
	 * 
	 * This method handles all level of approval and also cancel the order request
	 * 
	 * @param theForm
	 * @param channelUserVO
	 * @param locale
	 * @throws SQLException
	 * @throws BTSLBaseException
	 */
	private void cancelOrder(ApprovalVO theForm, ChannelUserVO channelUserVO, Locale locale)
			throws SQLException, BTSLBaseException {
		final String methodName = "cancelOrder";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered");
		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		try {

			final ChannelTransferVO channelTransferVO = theForm.getChannelTransferVO();

			String message = null;
			String currentLevel = null;
			if ("approval1".equals(theForm.getRequestType())) {
				channelTransferVO.setFirstApprovalRemark(theForm.getApprove1Remark());
				currentLevel = PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1;
				// message = Transfer Order Id {0} has been cancelled at Level
				// One
				message = "channeltransfer.foctransferapprovaldetailview.msg.level1cancel";
			} else if ("approval2".equals(theForm.getRequestType())) {
				channelTransferVO.setSecondApprovalRemark(theForm.getApprove2Remark());
				currentLevel = PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2;
				// message = Transfer Order Id {0} has been cancelled at Level
				// Two
				message = "channeltransfer.foctransferapprovaldetailview.msg.level2cancel";
			} else if ("approval3".equals(theForm.getRequestType())) {
				channelTransferVO.setThirdApprovalRemark(theForm.getApprove3Remark());
				currentLevel = PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3;
				// message = Transfer Order Id {0} has been cancelled at Level
				// Three
				message = "channeltransfer.foctransferapprovaldetailview.msg.level3cancel";
			}

			final Date date = new Date();
			channelTransferVO.setExternalTxnDateAsString(theForm.getExternalTxnDate());
			channelTransferVO.setExternalTxnNum(theForm.getExternalTxnNum());
			channelTransferVO.setCanceledBy(channelUserVO.getUserID());
			channelTransferVO.setCanceledOn(date);
			channelTransferVO.setModifiedBy(channelUserVO.getUserID());
			channelTransferVO.setModifiedOn(date);
			channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
			// set the items list from form to VO
			channelTransferVO.setChannelTransferitemsVOList(theForm.getTransferItemList());
			channelTransferVO.setDefaultLang(theForm.getDefaultLang());
			channelTransferVO.setSecondLang(theForm.getSecondLang());
			channelTransferVO.setPayInstrumentStatus("REJECT");

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();

			// commented as disscussed with Sanjay Sir, GSB, AC need not to be
			// updated in WEB

			// int updateCount =(new
			// ChannelUserDAO()).updateUserPhoneAfterTxn(con,channelTransferVO,channelTransferVO.getToUserCode(),channelTransferVO.getToUserID(),true);
			final int updateCount = channelTransferDAO.cancelTransferOrder(con, channelTransferVO, currentLevel);

			if (updateCount <= 0) {
				mcomCon.finalRollback();
				throw new BTSLBaseException(this, methodName,
						"channeltransfer.foctransferapprovaldetailview.approval.notsuccess",
						new String[] { channelTransferVO.getTransferID() });
			} else {
				mcomCon.finalCommit();
				// As disscussed with Sanjay Sir Date 01/06/2006 following code
				// is commented for not to send
				// SMS to the channel user in the case of FOC order cancelation
				/*
				 * UserDAO userDAO = new UserDAO(); UserPhoneVO phoneVO =
				 * userDAO.loadUserPhoneVO(con,channelTransferVO.getToUserID()); if(phoneVO !=
				 * null) { String country = phoneVO.getCountry(); String language =
				 * phoneVO.getPhoneLanguage(); Locale locale= new Locale(language,country);
				 * Object []smsListArr = ChannelTransferBL.prepareSMSMessageListForReceiver
				 * (con,channelTransferVO
				 * ,PretupsErrorCodesI.FOC_OPT_CHNL_TRANSFER_CANCEL_TXNSUBKEY
				 * ,PretupsErrorCodesI.FOC_OPT_CHNL_TRANSFER_CANCEL_BALSUBKEY); String[] array=
				 * {channelTransferVO.getTransferID(),BTSLUtil.getMessage (locale,
				 * (ArrayList)smsListArr[0]),BTSLUtil.getMessage(locale,(
				 * ArrayList)smsListArr[1])}; BTSLMessages messages=new
				 * BTSLMessages(PretupsErrorCodesI.FOC_OPT_CHNL_TRANSFER_SMS3 ,array);
				 * PushMessage pushMessage=new PushMessage(phoneVO.getMsisdn(),messages
				 * ,channelTransferVO.getTransferID
				 * (),null,locale,channelTransferVO.getNetworkCode()); pushMessage.push(); }
				 * else { String arr[] = {channelTransferVO.getToUserName()}; map.put(
				 * "channeltransfer.foctransferapprovaldetailview.phoneinfo.notexist.msg" ,arr);
				 * }
				 */

				/*
				 * prepare the message for success response
				 */
				final String args[] = { channelTransferVO.getTransferID() };// setting response
				String responseMessage = this.getMessage(locale, message, args);
				this.createSuccessResponse(200, responseMessage, message, channelTransferVO.getTransferID());

			}
		} catch (BTSLBaseException be) {
			_log.error(methodName, "BTSLBaseException:e=" + be);
			try {
				if (con != null) {
					con.rollback();
				}
			} catch (SQLException e1) {
				_log.errorTrace(methodName, e1);
			}
			throw be;
		} catch (SQLException se) {
			_log.error(methodName, "SQLException:e=" + se);
			try {
				if (con != null) {
					con.rollback();
				}
			} catch (SQLException e1) {
				_log.errorTrace(methodName, e1);
			}
			throw se;
		} catch (Exception e) {
			_log.error(methodName, "Exception:e=" + e);
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
				mcomCon.close("FOCApprovalServiceImpl#cancelOrder");
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exiting");
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				_log.debug(methodName, "Exiting: ");
			}
		}
	}

	/**
	 * Method constructBalanceVOFromTxnVO.
	 * 
	 * @param p_channelTransferVO ChannelTransferVO
	 * @return UserBalancesVO
	 */
	private UserBalancesVO constructBalanceVOFromTxnVO(ChannelTransferVO p_channelTransferVO) {
		final String methodName = "constructBalanceVOFromTxnVO";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered p_channelTransferVO= " + p_channelTransferVO);
		}
		final UserBalancesVO userBalancesVO = new UserBalancesVO();
		userBalancesVO.setUserID(p_channelTransferVO.getToUserID());
		userBalancesVO.setLastTransferType(p_channelTransferVO.getTransferType());
		userBalancesVO.setLastTransferID(p_channelTransferVO.getTransferID());
		userBalancesVO.setLastTransferOn(p_channelTransferVO.getModifiedOn());

		/** START: Birendra: 28JAN2015 */
		userBalancesVO.setProductType(p_channelTransferVO.getProductType());
		userBalancesVO.setNetworkFor(p_channelTransferVO.getNetworkCodeFor());
		userBalancesVO.setNetworkCode(p_channelTransferVO.getNetworkCode());
		if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
			userBalancesVO.setWalletCode(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.WALLET_FOR_ADNL_CMSN)));
			p_channelTransferVO.setUserWalletCode(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.WALLET_FOR_ADNL_CMSN)));
		} else {
			userBalancesVO.setWalletCode(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
			p_channelTransferVO.setUserWalletCode(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
		}
		/** START: Birendra: 28JAN2015 */
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Exiting userBalancesVO= " + userBalancesVO);
		}
		return userBalancesVO;
	}

	/**
	 * 
	 * @param status
	 * @param message
	 * @param errorCode
	 * @param transactionID
	 */
	private void createSuccessResponse(int status, String message, String errorCode, String transactionID) {
		BaseResponse baseResponse = new BaseResponse();
		baseResponse.setStatus(status);
		baseResponse.setMessage(message);
		baseResponse.setMessageCode(errorCode);
		successList.add(baseResponse);
	}

	/**
	 * 
	 * @param totalApprCount
	 * @param locale
	 */
	private void createFinalResponse(int totalApprCount, Locale locale) {
		String message = null;
		int successCount = totalApprCount - failureCount;
		if (totalApprCount == 0) {
			response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			message = getMessage(locale, PretupsErrorCodesI.O2C_APP_REQUEST_EMPTY, null);
			response.setMessage(message);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		} else if (failureCount == totalApprCount) {
			response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			message = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
					PretupsErrorCodesI.ALL_FAIL, null);
			response.setMessage(message);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			;
		} else {
			response.setStatus(String.valueOf(HttpStatus.SC_OK));
			message = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
					PretupsErrorCodesI.PARTIAL_PROCESS,
					new String[] { String.valueOf(successCount), String.valueOf(totalApprCount) });

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
	public String getMessage(Locale locale, String errorCode) {
		return RestAPIStringParser.getMessage(locale, errorCode, null);
	}

	/**
	 * 
	 * @param locale
	 * @param errorCode
	 * @param args
	 * @return
	 */
	public String getMessage(Locale locale, String errorCode, String[] args) {
		return RestAPIStringParser.getMessage(locale, errorCode, args);
	}

}
