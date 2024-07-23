package com.restapi.o2c.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.BaseResponse;
import com.btsl.common.BaseResponseMultiple;
import com.btsl.common.ErrorMap;
import com.btsl.common.IDGenerator;
import com.btsl.common.MasterErrorList;
import com.btsl.common.RowErrorMsgLists;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.gateway.razorpay.RazorPay;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.receiver.RestReceiver;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.requesthandler.O2cInitiateResponseVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.PGTransactionLog;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.KeyArgumentVO;
import com.btsl.util.OracleUtil;
import com.btsl.voms.util.VomsUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductVO;
import com.btsl.voms.voucher.businesslogic.VomsBatchVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;

public class O2CVoucherInitiateService {
	private static Log log = LogFactory.getLog(O2CVoucherInitiateService.class.getName());
	public static OperatorUtilI _operatorUtil = null;
	private static final float EPSILON=0.0000001f; 

	static {
		final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
		try {
			_operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
		} catch (Exception e) {
			log.errorTrace("static", e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					" O2CVoucherInitiateService [initialize]", "", "", "",
					"Exception while loading the class at the call:" + e.getMessage());
		}
	}
	
	
	/**
	 * @param o2CVoucherInitiateRequestVO
	 * @param response1
	 * @return
	 */
	public BaseResponseMultiple<JsonNode> processVoucherInitiateRequest(O2CVoucherInitiateRequestVO o2CVoucherInitiateRequestVO,HttpServletResponse response1) {
		final String methodName = "processVoucherInitiateRequest";
        RestReceiver.updateRequestIdChannel();
    	BaseResponseMultiple<JsonNode> baseResponseMultiple =null;
    	Connection con = null;
		MComConnectionI mcomCon = null;
    	RequestVO p_requestVO = null;
    	ErrorMap errorMap = new ErrorMap();
		ArrayList<BaseResponse> baseResponseFinalSucess = new ArrayList<>();
//		BaseResponse baseResponse = new BaseResponse();
		O2cInitiateResponseVO o2cInitiateResponseVO = new O2cInitiateResponseVO();
		ArrayList<VomsBatchVO> vomsBatchList = new ArrayList<VomsBatchVO>();
		ChannelUserVO receiverChannelUserVO = null;
        String serviceType = PretupsI.SERVICE_TYPE_O2C_VOUCHER_INI;
        try {
        	mcomCon = new MComConnection();
			con = mcomCon.getConnection();
            p_requestVO = new RequestVO();
        	baseResponseMultiple=new BaseResponseMultiple<>();
			if (log.isDebugEnabled()) {
				log.debug("process", "Entered o2CVoucherInitiateRequestVO: " + o2CVoucherInitiateRequestVO);
			}
			UserDAO userDAO = new UserDAO();
			receiverChannelUserVO = new ChannelUserVO();
			receiverChannelUserVO = (ChannelUserVO)userDAO.loadUsersDetails(con, o2CVoucherInitiateRequestVO.getData().getMsisdn());
			if (log.isDebugEnabled()) {
				log.debug("process", "Entered Receiver VO: " + receiverChannelUserVO);
			}
			receiverChannelUserVO.setUserPhoneVO(userDAO.loadUserAnyPhoneVO(con, o2CVoucherInitiateRequestVO.getData().getMsisdn()));
			receiverChannelUserVO.setPinReset(receiverChannelUserVO.getUserPhoneVO().getPinReset());
			receiverChannelUserVO.setServiceTypes(serviceType);
			p_requestVO.setServiceType(serviceType);
			p_requestVO.setRequestGatewayType(o2CVoucherInitiateRequestVO.getReqGatewayType());
			p_requestVO.setLocale(new Locale(receiverChannelUserVO.getUserPhoneVO().getPhoneLanguage(),receiverChannelUserVO.getUserPhoneVO().getCountry()));

			O2CVoucherInitiateReqData o2CVoucherInitiateReqData = o2CVoucherInitiateRequestVO.getO2CInitiateReqData();
			
			//validate receiver
			if (!(receiverChannelUserVO == null)) {
				UserPhoneVO phoneVO = userDAO.loadUserAnyPhoneVO(con, o2CVoucherInitiateRequestVO.getData().getMsisdn());
				if (phoneVO != null && ((receiverChannelUserVO.getUserID()).equals(phoneVO.getUserId()))) {
					receiverChannelUserVO.setPrimaryMsisdn(receiverChannelUserVO.getMsisdn());
					receiverChannelUserVO.setMsisdn(o2CVoucherInitiateRequestVO.getData().getMsisdn());
				} else {
					throw new BTSLBaseException("O2CVoucherInitiateService", "process",
							PretupsErrorCodesI.EXT_XML_ERROR_INVALID_MSISDN, 0, null);
				}
			
			}
			Boolean flag = o2CValidate(con, o2CVoucherInitiateReqData, receiverChannelUserVO, errorMap, vomsBatchList);
			if(!flag){
				o2CVoucherInitiateRequestVO.setSourceType(PretupsI.REQUEST_SOURCE_TYPE_WEB);
				o2CVoucherInitiateRequestVO.setReqGatewayCode(PretupsI.GATEWAY_TYPE_WEB);
				o2CVoucherInitiateRequestVO.setReqGatewayType(PretupsI.GATEWAY_TYPE_WEB);
				o2cService(con, receiverChannelUserVO, o2CVoucherInitiateRequestVO, vomsBatchList,p_requestVO);
				//For RazorPay Gateway
				if(p_requestVO.getChannelTransferVO() != null) {
					if(PretupsI.PAYMENT_INSTRUMENT_TYPE_ONLINE.equals(p_requestVO.getChannelTransferVO().getPayInstrumentType())) {
			        	String gatewayOrderId = RazorPay.createNewOrder(con, p_requestVO.getChannelTransferVO());
			        	o2cInitiateResponseVO.setGatewayOrderId(gatewayOrderId);
			        	o2cInitiateResponseVO.setOnlinePayment(true);
			        }
				}
				o2cInitiateResponseVO.setMessage(RestAPIStringParser.getMessage(
				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), p_requestVO.getMessageCode(),
				p_requestVO.getMessageArguments()));
				o2cInitiateResponseVO.setMessageCode(p_requestVO.getMessageCode());
				o2cInitiateResponseVO.setStatus(200);
				o2cInitiateResponseVO.setTransactionId(p_requestVO.getTransactionID());
				baseResponseFinalSucess.add(o2cInitiateResponseVO);
				mcomCon.finalCommit();
			}

			if(!BTSLUtil.isNullObject(errorMap.getMasterErrorList()) || !BTSLUtil.isNullObject(errorMap.getRowErrorMsgLists()))
			{
				baseResponseMultiple.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
				baseResponseMultiple.setMessage(RestAPIStringParser.getMessage(
						new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.REQ_NOT_PROCESS,
						null));
				baseResponseMultiple.setStatus("400");
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				baseResponseMultiple.setService(serviceType + "RESP");
				baseResponseMultiple.setSuccessList(baseResponseFinalSucess);
				baseResponseMultiple.setErrorMap(errorMap);
				return baseResponseMultiple;
			}
		baseResponseMultiple.setSuccessList(baseResponseFinalSucess);
		baseResponseMultiple.setMessageCode(p_requestVO.getMessageCode());
		baseResponseMultiple.setMessage("All records processed successfully");
		baseResponseMultiple.setStatus("200");
		baseResponseMultiple.setService(serviceType + "RESP");
		return baseResponseMultiple;
    }catch (BTSLBaseException be) {
    	try {
			if (mcomCon != null) {
				mcomCon.finalRollback();
			}
		} 
		catch (SQLException esql) {
			log.error(methodName,"SQLException : ", esql.getMessage());
		}
		
		log.error(methodName, "BTSLBaseException " + be.getMessage());
        log.errorTrace(methodName, be);
    	String resmsg = RestAPIStringParser.getMessage(
				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), be.getMessageKey(),
				null);
    	baseResponseMultiple.setMessageCode(be.getMessageKey());
    	baseResponseMultiple.setMessage(resmsg);
    	baseResponseMultiple.setService(serviceType + "RESP");
    	 if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
          	 response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
          	baseResponseMultiple.setStatus("401");
          }
           else{
           response1.setStatus(HttpStatus.SC_BAD_REQUEST);
           baseResponseMultiple.setStatus("400");
           }
        return baseResponseMultiple;
    } catch (Exception e) {
    	try {
			if (mcomCon != null) {
				mcomCon.finalRollback();
			}
		} 
		catch (SQLException esql) {
			log.error(methodName,"SQLException : ", esql.getMessage());
		}
    	log.error(methodName, "Exception " + e.getMessage());
        log.errorTrace(methodName, e);
        baseResponseMultiple.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
        String resmsg = RestAPIStringParser.getMessage(
				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.REQ_NOT_PROCESS,
				null);
    	baseResponseMultiple.setMessage(resmsg);
    	baseResponseMultiple.setService(serviceType + "RESP");
    	  response1.setStatus(HttpStatus.SC_BAD_REQUEST);
    	  baseResponseMultiple.setStatus("400");
        return baseResponseMultiple;
    }
	finally {
		if (mcomCon != null) {
			mcomCon.close("C2CTransferController#process");
			mcomCon = null;
		}
        LogFactory.printLog(methodName, " Exited ", log);
    }

	
	}// end of process


	
	public static Boolean o2CValidate(Connection con,O2CVoucherInitiateReqData o2CVoucherInitiateReqData,ChannelUserVO receiverVO,ErrorMap errorMap, ArrayList<VomsBatchVO> vomsBatchList) throws Exception {
		ArrayList<MasterErrorList> masterErrorListMain = new ArrayList<MasterErrorList>();
		ArrayList<RowErrorMsgLists> rowErrorMsgListsVoucher = new ArrayList<RowErrorMsgLists>();
		Locale locale = new Locale(receiverVO.getUserPhoneVO().getPhoneLanguage(), receiverVO.getUserPhoneVO().getCountry());
		Boolean error = false;
		HashMap<String,Object> reqData = new HashMap<String,Object>();
		reqData.put("remarks", o2CVoucherInitiateReqData.getRemarks());
		reqData.put("refNumber", o2CVoucherInitiateReqData.getRefnumber());
		reqData.put("paymentDetails", o2CVoucherInitiateReqData.getPaymentDetails());
		Boolean reqError = O2cCommonService.validateRequestData(masterErrorListMain, locale,reqData);
		error = validateVoucher(con, vomsBatchList, o2CVoucherInitiateReqData.getVoucherDetails(), rowErrorMsgListsVoucher, receiverVO.getUserID(), receiverVO.getNetworkID());
		if(reqError){
	    	 errorMap.setMasterErrorList(masterErrorListMain);
			 }
		 if(error){
			 errorMap.setRowErrorMsgLists(rowErrorMsgListsVoucher);
			 }
		 if(reqError || error)
			 return true;
		 return false;
	}
	
	public static boolean validateVoucher(Connection con,ArrayList<VomsBatchVO> vomsBatchlist,List<VoucherDetailsIni> voucherDetailsList,ArrayList<RowErrorMsgLists> rowErrorMsgListsVoucher,String toUserId,String networkCode) throws BTSLBaseException{
		String methodName = "validateVoucher";
		if (log.isDebugEnabled()) {
			StringBuilder loggerValue = new StringBuilder();
			loggerValue.setLength(0);
        	loggerValue.append("Entered voucherDetailsList.size(): ");
        	loggerValue.append(voucherDetailsList.size());
        	loggerValue.append("toUserId: ");
        	loggerValue.append(toUserId);
        	loggerValue.append("networkCode: ");
        	loggerValue.append(networkCode);
            log.debug(methodName,loggerValue );
		}
		boolean error = false;
		Locale locale= new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
		try{
			
			int i =0;
			Date currentDate = new Date();
			for(VoucherDetailsIni voucherDetails : voucherDetailsList){
				VomsBatchVO vomsBatchVO = new VomsBatchVO();
				int row = i+1;
				error = false;
				RowErrorMsgLists rowErrorMsgListssVoucher= new RowErrorMsgLists();
				rowErrorMsgListssVoucher.setRowValue(String.valueOf(row));
				rowErrorMsgListssVoucher.setRowName("Voucher "+row);
				ArrayList<MasterErrorList> masterErrorLists1 = new ArrayList<MasterErrorList>();
				if (BTSLUtil.isNullString(voucherDetails.getVoucherType())){
				    error=true;
					MasterErrorList masterErrorListss = new MasterErrorList();
					masterErrorListss.setErrorCode(PretupsErrorCodesI.VOUCHER_TYPE_REQUIRED);
					masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.VOUCHER_TYPE_REQUIRED, null));
					masterErrorLists1.add(masterErrorListss);
				}
				if (BTSLUtil.isNullString(voucherDetails.getVouchersegment())){
				    error=true;
					MasterErrorList masterErrorListss = new MasterErrorList();
					masterErrorListss.setErrorCode(PretupsErrorCodesI.VOUCHER_SEGMENT_REQUIRED);
					masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.VOUCHER_SEGMENT_REQUIRED, null));
					masterErrorLists1.add(masterErrorListss);
				}
				if(BTSLUtil.isNullString(voucherDetails.getDenomination())){
					error=true;
					MasterErrorList masterErrorListss = new MasterErrorList();
					masterErrorListss.setErrorCode(PretupsErrorCodesI.O2_DENO_BLANK);
					masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2_DENO_BLANK, null));
					masterErrorLists1.add(masterErrorListss);
				}
				else if(!BTSLUtil.isDecimalValue(voucherDetails.getDenomination())){
					error=true;
					MasterErrorList masterErrorListss = new MasterErrorList();
					masterErrorListss.setErrorCode(PretupsErrorCodesI.O2C_DENO_INVALID);
					masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_DENO_INVALID, null));
					masterErrorLists1.add(masterErrorListss);
				}

			   if (BTSLUtil.isNullString(voucherDetails.getQuantity())){
				    error=true;
					MasterErrorList masterErrorListss = new MasterErrorList();
					masterErrorListss.setErrorCode(PretupsErrorCodesI.O2C_QTY_REQ);
					masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_QTY_REQ, null));
					masterErrorLists1.add(masterErrorListss);
				}
			   else if(!BTSLUtil.isNumeric(voucherDetails.getQuantity())){
				    error=true;
					MasterErrorList masterErrorListss = new MasterErrorList();
					masterErrorListss.setErrorCode(PretupsErrorCodesI.O2C_QTY_NUMERIC);
					masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_QTY_NUMERIC, null));
					masterErrorLists1.add(masterErrorListss);
				}
			   	
			    vomsBatchVO.setQuantity(voucherDetails.getQuantity());
				vomsBatchVO.setDenomination(voucherDetails.getDenomination());
				vomsBatchVO.setVoucherType(voucherDetails.getVoucherType());
				vomsBatchVO.setVouchersegment(voucherDetails.getVouchersegment());
				vomsBatchVO.setCreatedBy(PretupsI.CATEGORY_TYPE_OPT);
				vomsBatchVO.set_NetworkCode(networkCode);
				vomsBatchVO.setCreatedDate(currentDate);
				vomsBatchVO.setModifiedDate(currentDate);
				vomsBatchVO.setModifiedBy(toUserId);
				vomsBatchVO.setModifiedOn(currentDate);
				vomsBatchVO.setCreatedOn(currentDate);
				vomsBatchVO.setToUserID(toUserId); 
				if (SystemPreferences.VOUCHER_EN_ON_TRACKING) {
						vomsBatchVO.setBatchType(VOMSI.BATCH_ENABLED);
					} 
				else {
						vomsBatchVO.setBatchType(VOMSI.VOMS_PRE_ACTIVE_STATUS);
					}
				String batch_no = String.valueOf(IDGenerator.getNextID(VOMSI.VOMS_BATCHES_DOC_TYPE, String.valueOf(BTSLUtil.getFinancialYear()), VOMSI.ALL));
				vomsBatchVO.setBatchNo(new VomsUtil().formatVomsBatchID(vomsBatchVO, batch_no));
				
				if(BTSLUtil.isNullOrEmptyList(masterErrorLists1)){
					// load product for vouchertype
					VomsProductDAO vomsProductDAO = new VomsProductDAO();
					ArrayList<VomsProductVO>vomsProductlist = vomsProductDAO.loadProductDetailsList(con, vomsBatchVO.getVoucherType(), "'" + VOMSI.VOMS_STATUS_ACTIVE + "'", false, "", networkCode, vomsBatchVO.getVouchersegment());
					if(!BTSLUtil.isNullOrEmptyList(vomsProductlist)) {
						ArrayList<VomsProductVO> itemlist = new ArrayList<>();
						for (VomsProductVO vomsProductVO : vomsProductlist) {
	                		if (Math.abs(Double.parseDouble(vomsBatchVO.getDenomination())-vomsProductVO.getMrp()) < EPSILON) {
	                			itemlist.add(vomsProductVO);
	                		}
	                	}
	                	vomsBatchVO.setProductlist(itemlist);
					}
					if(BTSLUtil.isNullOrEmptyList(vomsBatchVO.getProductlist())){
						error=true;
						MasterErrorList masterErrorListss = new MasterErrorList();
						masterErrorListss.setErrorCode(PretupsErrorCodesI.O2C_VOMS_PROF_NOT_EXIST);
						masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_VOMS_PROF_NOT_EXIST, null));
						masterErrorLists1.add(masterErrorListss);
					}
				}
				if(error){
					rowErrorMsgListssVoucher.setMasterErrorList(masterErrorLists1);
					rowErrorMsgListsVoucher.add(rowErrorMsgListssVoucher);
				}
				vomsBatchlist.add(vomsBatchVO);
				i++;
			  }

	}catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.error(methodName, "Exception in validation of voucher list" + e.getMessage());
			}
			  log.errorTrace(methodName, e);
	          throw new BTSLBaseException("O2CVoucherInitiateService", methodName, e.getMessage());
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exiting ....");
		}
		return error;
	}
	

	
	public static void o2cService(Connection con,ChannelUserVO receiverChannelUserVO,O2CVoucherInitiateRequestVO o2cVoucherInitiateRequestVO,ArrayList<VomsBatchVO> vomsBatchList,RequestVO p_requestVO )
			throws Exception {
		 Locale locale= new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
    	 Date currentDate = new Date();
		 UserPhoneVO userPhoneVO = null;
		 UserPhoneVO phoneVO = null;
		 UserDAO userDAO = new UserDAO();
		 ChannelUserDAO channelUserDAO = new ChannelUserDAO();
     	 O2CVoucherInitiateReqData o2CVoucherInitiateReqData = o2cVoucherInitiateRequestVO.getO2CInitiateReqData();
		 UserPhoneVO PrimaryPhoneVO_R = new UserPhoneVO();
		p_requestVO.setLocale(locale);
		p_requestVO.setSourceType(o2cVoucherInitiateRequestVO.getSourceType());
		p_requestVO.setServiceType(PretupsI.SERVICE_TYPE_O2C_VOUCHER_INI);
		p_requestVO.setRequestGatewayType(o2cVoucherInitiateRequestVO.getReqGatewayType());
    	
    	String receiverUserCode = receiverChannelUserVO.getMsisdn();
		if (phoneVO == null) {
			phoneVO = userDAO.loadUserAnyPhoneVO(con, receiverUserCode);
		}
		if (!SystemPreferences.SECONDARY_NUMBER_ALLOWED) {
			receiverChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, receiverUserCode,
					true, currentDate, false);
		} else {
			if (phoneVO != null && !("Y".equalsIgnoreCase(phoneVO.getPrimaryNumber()))) {
				receiverChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con,
						phoneVO.getUserId(), false, currentDate, false);
				if (SystemPreferences.MESSAGE_TO_PRIMARY_REQUIRED) {
					PrimaryPhoneVO_R = userDAO.loadUserAnyPhoneVO(con, receiverChannelUserVO.getMsisdn());
				}
				receiverChannelUserVO.setPrimaryMsisdn(receiverChannelUserVO.getMsisdn());
				receiverChannelUserVO.setUserCode(receiverUserCode);
			} else {
				receiverChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, receiverUserCode,
						true, currentDate, false);
			}
		}
		
			receiverChannelUserVO.setUserPhoneVO(phoneVO);
          if (receiverChannelUserVO != null &&PretupsI.USER_TYPE_CHANNEL.equalsIgnoreCase(receiverChannelUserVO.getUserType())) {
        	  receiverChannelUserVO.setActiveUserID(receiverChannelUserVO.getUserID());
  	    	  userPhoneVO = (UserPhoneVO) receiverChannelUserVO.getUserPhoneVO();
          }else{
        	  String staffuser =receiverChannelUserVO.getUserID();
        	  receiverChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con,
        			  staffuser, false, currentDate, false);
          	  userPhoneVO = (UserPhoneVO) receiverChannelUserVO.getStaffUserDetails().getUserPhoneVO();
        	  receiverChannelUserVO.setActiveUserID(staffuser);
          }
		 // validate the PIN if it is in the request
    	if (userPhoneVO.getPinRequired().equals(PretupsI.YES)) {
		try {
			ChannelUserBL.validatePIN(con, receiverChannelUserVO, o2CVoucherInitiateReqData.getPin());
		} catch (BTSLBaseException be) {
			if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN))
					|| (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
				OracleUtil.commit(con);
			}
			throw be;
		}
    	}
    	
    	ChannelTransferBL.c2cTransferUserValidateReceiver(con, receiverChannelUserVO, p_requestVO, currentDate, receiverUserCode);

    		
    	final LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(p_requestVO.getLocale());
		if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
			receiverChannelUserVO
					.setCommissionProfileSuspendMsg(receiverChannelUserVO.getCommissionProfileLang1Msg());
		} else {
			receiverChannelUserVO
					.setCommissionProfileSuspendMsg(receiverChannelUserVO.getCommissionProfileLang2Msg());
		}
		// load the approval limit of the user
		final ChannelTransferRuleVO channelTransferRuleVO = new ChannelTransferRuleDAO().loadTransferRule(con, receiverChannelUserVO.getNetworkID(), receiverChannelUserVO.getDomainID(),
				PretupsI.CATEGORY_TYPE_OPT, receiverChannelUserVO.getCategoryCode(), PretupsI.TRANSFER_RULE_TYPE_OPT, true);
		if (channelTransferRuleVO == null) {
			throw new BTSLBaseException("O2CVoucherInitiateService", "process",
					PretupsErrorCodesI.USER_TRANSFER_RULE_NOT_EXIST, 0,
					null, null);
		} else if (PretupsI.NO.equals(channelTransferRuleVO.getTransferAllowed())) {
			throw new BTSLBaseException("O2CVoucherInitiateService", "process",
					PretupsErrorCodesI.ERROR_USER_RETURN_NOT_ALLOWED, 0,
					null, null);
		} else if (channelTransferRuleVO.getProductVOList() == null || channelTransferRuleVO.getProductVOList().isEmpty()) {
			throw new BTSLBaseException("O2CVoucherInitiateService", "process",
					PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_RULE_NOTMATCH, 0,
					null, null);
		}

		
		ArrayList list = ChannelTransferBL.loadO2CXfrProductList(con, VOMSI.DEFAULT_PRODUCT_CODE, receiverChannelUserVO.getNetworkID(), receiverChannelUserVO.getCommissionProfileSetID(), currentDate, "");
		/*
		 * User associated with commission profile.Commission profile
		 * associated with products. Display only those products which have
		 * commission profile same as user?s commission profile. If above
		 * condition fail then display error message.
		 */

		if (list.isEmpty()) {
			throw new BTSLBaseException("O2CVoucherInitiateService", "process",
					PretupsErrorCodesI.PRODUCT_NOT_ASSOSIATED, 0,
					new String[] { receiverChannelUserVO.getLoginID(),VOMSI.DEFAULT_PRODUCT_CODE }, null);
		}
		/*
		 * Now further filter the list with the transfer rules list and the
		 * above list
		 * of commission profile products.
		 */
		list = O2cCommonService.filterProductWithTransferRule(list, channelTransferRuleVO.getProductVOList());

		/*
		 * This case arises
		 * suppose in transfer rule products A and B are associated
		 * In commission profile product C and D are associated.
		 * sWe load product with intersection of transfer rule products and
		 * commission profile products.
		 * if no product found then display below message
		 */
		if (list.isEmpty()) {
			throw new BTSLBaseException("O2CVoucherInitiateService", "process",
					PretupsErrorCodesI.PRODUCT_NOT_MATCH_WITH_TRANSFERRULE, 0,
					new String[] { receiverChannelUserVO.getLoginID() }, null);
		}

		/*// to check external txn num is required at which level . if it is
		// intial level then externalTxn exist is Y
		if (PretupsI.TRANSFER_EXTERNAL_TXN_INTIAL_LEVEL.equals(PreferenceCache.getSystemPreferenceValue(PretupsI.TRANSFER_EXTERNAL_TXN_LEVEL))) {
			theForm.setExternalTxnExist(PretupsI.YES);
			if (PretupsI.YES.equals(PreferenceCache.getSystemPreferenceValue(PretupsI.TRANSFER_EXTERNAL_TXN_MANDATORY))) {
				theForm.setExternalTxnMandatory(PretupsI.YES);
			}
		}*/
		final ArrayList<ChannelTransferItemsVO> channelTransferItemsList = new ArrayList<ChannelTransferItemsVO>();
		 Long totalVoucherQty = 0L;
		 double totalRequestedQuantity = 0.0;
		 double requestedMrp = 0.0;
		for (int i = 0; i < vomsBatchList.size(); i++) {
			final VomsBatchVO vomsBatchVO = (VomsBatchVO) vomsBatchList.get(i);
			
			/* if (!BTSLUtil.isNullOrEmptyList(vomsBatchVO.getProductlist())) { */
				final double denomination = Double.parseDouble(vomsBatchVO.getDenomination());
				final Long quantity = (Long.parseLong(vomsBatchVO.getQuantity()));
				 requestedMrp = denomination * quantity;
				vomsBatchVO.setQuantity(String.valueOf(quantity));
				totalVoucherQty = totalVoucherQty+quantity;
				totalRequestedQuantity = totalRequestedQuantity + requestedMrp;
				//vomsBatchVO.setProductName("XYZ");
				/* } */
		}
		int fromArrayLists=list.size();
		ChannelTransferItemsVO tempChannelTransferItemsVO = new ChannelTransferItemsVO();
		for (int j = 0, k = fromArrayLists; j < k; j++) {
			tempChannelTransferItemsVO = (ChannelTransferItemsVO) list.get(j);
			if(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_PRODUCT_CODE)).equals(tempChannelTransferItemsVO.getProductCode())) {
				tempChannelTransferItemsVO.setRequestedQuantity(Double.toString(totalRequestedQuantity));
				tempChannelTransferItemsVO.setVoucherQuantity(totalVoucherQty);
				/*
				 * tempChannelTransferItemsVO.setRequestedQuantity(String.valueOf(requestedMrp))
				 * ;
				 */
				tempChannelTransferItemsVO.setRequiredQuantity(PretupsBL.getSystemAmount(totalRequestedQuantity));
				tempChannelTransferItemsVO.setPayableAmount(BTSLUtil.parseDoubleToLong((tempChannelTransferItemsVO.getUnitValue() * totalRequestedQuantity)));
				tempChannelTransferItemsVO.setNetPayableAmount(BTSLUtil.parseDoubleToLong((tempChannelTransferItemsVO.getUnitValue() * totalRequestedQuantity)));
				channelTransferItemsList.add(tempChannelTransferItemsVO);
				
				break;
			}
		}
		KeyArgumentVO keyArgumentVO = null;
		UserPhoneVO primaryPhoneVO_S = null;

		//prepare channeltransferVO
		ChannelTransferVO channelTransferVO = O2cCommonService.prepareChannelTransferProfileVO(o2CVoucherInitiateReqData.getPaymentDetails(), con, receiverChannelUserVO, channelTransferItemsList, vomsBatchList);
		channelTransferVO.setSource(o2cVoucherInitiateRequestVO.getSourceType());
		channelTransferVO.setFirstApproverLimit(channelTransferRuleVO.getFirstApprovalLimit());
		channelTransferVO.setSecondApprovalLimit(channelTransferRuleVO.getSecondApprovalLimit());
		channelTransferVO.setTransferCategory(channelTransferRuleVO.getTransferType());
		channelTransferVO.setActiveUserId(receiverChannelUserVO.getActiveUserID());
		channelTransferVO.setTransferInitatedBy(receiverChannelUserVO.getActiveUserID());
		channelTransferVO.setReferenceNum(o2CVoucherInitiateReqData.getRefnumber());
		channelTransferVO.setChannelRemarks(o2CVoucherInitiateReqData.getRemarks());
		channelTransferVO.setCreatedBy(receiverChannelUserVO.getActiveUserID());
		channelTransferVO.setModifiedBy(receiverChannelUserVO.getActiveUserID());
		// generate transfer ID for the O2C transfer
	      ChannelTransferBL.genrateTransferID(channelTransferVO);

		if(SystemPreferences.OTH_COM_CHNL){
			channelTransferVO.setRequestGatewayCode(p_requestVO.getRequestGatewayCode());
			channelTransferVO.setRequestGatewayType(p_requestVO.getRequestGatewayType());
			channelTransferVO.setToUserMsisdn(receiverChannelUserVO.getMsisdn());
			}
		if (log.isDebugEnabled()) {
			log.debug("process", "Calculate Tax of products Start ");
		}

		ChannelTransferBL.loadAndCalculateTaxOnDenominations(con, receiverChannelUserVO.getCommissionProfileSetID(), receiverChannelUserVO.getCommissionProfileSetVersion(), channelTransferVO, false, null, PretupsI.TRANSFER_TYPE_O2C);
		
		if(PretupsI.PAYMENT_INSTRUMENT_TYPE_ONLINE.equals(channelTransferVO.getPaymentInstType()))
		{
			ChannelTransferBL.calculateTotalMRPFromTaxAndDiscount(channelTransferItemsList, PretupsI.TRANSFER_TYPE_O2C, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1,
				channelTransferVO);
		}
		channelTransferVO.setControlTransfer(PretupsI.YES);
		channelTransferVO.setSource(o2cVoucherInitiateRequestVO.getSourceType());
		channelTransferVO.setRequestGatewayCode(o2cVoucherInitiateRequestVO.getReqGatewayCode());
		channelTransferVO.setRequestGatewayType(o2cVoucherInitiateRequestVO.getReqGatewayType());
		channelTransferVO.setCellId(p_requestVO.getCellId());
		channelTransferVO.setSwitchId(p_requestVO.getSwitchId());
		channelTransferVO.setPayableAmount(((ChannelTransferItemsVO)channelTransferVO.getChannelTransferitemsVOList().get(0)).getPayableAmount());
		channelTransferVO.setNetPayableAmount(((ChannelTransferItemsVO)channelTransferVO.getChannelTransferitemsVOList().get(0)).getNetPayableAmount());
		
			
		 // set the transfer ID in each ChannelTransferItemsVO of productList
          for (int i = 0, j = channelTransferItemsList.size(); i < j; i++) {
        	  ChannelTransferItemsVO channelTransferItemsVO = (ChannelTransferItemsVO) channelTransferItemsList.get(i);
              channelTransferItemsVO.setTransferID(channelTransferVO.getTransferID());
              channelTransferItemsVO.setSenderDebitQty(0);
              channelTransferItemsVO.setReceiverCreditQty(0);
              channelTransferItemsVO.setSenderPostStock(0);
              channelTransferItemsVO.setReceiverPostStock(0);
              channelTransferItemsVO.setAfterTransSenderPreviousStock(0);
              channelTransferItemsVO.setAfterTransReceiverPreviousStock(0);
          }
          

		ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
		p_requestVO.setChannelTransferVO(channelTransferVO);
		int insertCount = 0;
		insertCount = channelTransferDAO.addChannelTransfer(con, channelTransferVO);
		if (insertCount > 0) {
			con.commit();
			UserPhoneVO primaryPhoneVO = null;
			phoneVO = userDAO.loadUserAnyPhoneVO(con, channelTransferVO.getToUserCode());
			try{
				boolean _receiverMessageSendReq=false;
				_receiverMessageSendReq=((Boolean)PreferenceCache.getControlPreference(PreferenceI.REC_MSG_SEND_ALLOW,receiverChannelUserVO.getNetworkCode(),p_requestVO.getServiceType())).booleanValue();

				if (SystemPreferences.SECONDARY_NUMBER_ALLOWED && (SystemPreferences.MESSAGE_TO_PRIMARY_REQUIRED && "N".equals(phoneVO.getPrimaryNumber()))) {
					primaryPhoneVO = userDAO.loadUserAnyPhoneVO(con, receiverChannelUserVO.getPrimaryMsisdn());
				}

				if(_receiverMessageSendReq){
					if (primaryPhoneVO != null) {
						final BTSLMessages messages;
						final Object[] smsListArr = O2cCommonService.prepareSMSMessageListForVoucher(con, channelTransferVO);
						final String[] array = { channelTransferVO.getTransferID(), BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]) };
						if(PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType()))
						{
							messages = new BTSLMessages(PretupsErrorCodesI.O2C_TRANSFER_VOUCHER_OUTSIDE_SETTLEMENT, array);
						}
						else
						{
							messages = new BTSLMessages(PretupsErrorCodesI.O2C_TRANSFER_VOUCHER_INITIATE, array);
						}
						final PushMessage pushMessage = new PushMessage(primaryPhoneVO.getMsisdn(), messages, channelTransferVO.getTransferID(), null, locale,
								channelTransferVO.getNetworkCode());
						pushMessage.push();
					}
					if (phoneVO != null) {
						final BTSLMessages messages;
						final Object[] smsListArr = O2cCommonService.prepareSMSMessageListForVoucher(con, channelTransferVO);
						final String[] array = { channelTransferVO.getTransferID(), BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]) };
						if(PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType()))
						{
							messages = new BTSLMessages(PretupsErrorCodesI.O2C_TRANSFER_VOUCHER_OUTSIDE_SETTLEMENT, array);
						}
						else
						{
							messages = new BTSLMessages(PretupsErrorCodesI.O2C_TRANSFER_VOUCHER_INITIATE, array);
						}
						final PushMessage pushMessage = new PushMessage(phoneVO.getMsisdn(), messages, channelTransferVO.getTransferID(), null, locale, channelTransferVO
								.getNetworkCode());
						pushMessage.push();
					} 
				}
			} catch (Exception e) {
				log.error("o2cService", " SMS notification failed" + e.getMessage());
				log.errorTrace("o2cService", e);
			}
			
			if (SystemPreferences.O2C_EMAIL_NOTIFICATION && 
					!(SystemPreferences.PG_INTEFRATION_ALLOWED && PretupsI.PAYMENT_INSTRUMENT_TYPE_ONLINE.equalsIgnoreCase(o2CVoucherInitiateReqData.getPaymentDetails().get(0).getPaymenttype()))) {
		        ChannelUserWebDAO channelUserWebDAO = new ChannelUserWebDAO();
				final String email = channelUserWebDAO.loadUserEmail(con, channelTransferVO.getToUserID());
				channelTransferVO.setEmail(email);
				O2cCommonService.sendEmailNotification(con, channelTransferVO, channelTransferDAO, "APV1O2CTRF","o2c.email.notification.contento2c.email.notification.content");
			}
			}else {
				con.commit();
				throw new BTSLBaseException("O2CVoucherInitiateService", "o2cService", "channeltransfer.transferdetailssuccess.msg.unsuccess", "");
				}
	
		if(SystemPreferences.PG_INTEFRATION_ALLOWED && PretupsI.PAYMENT_INSTRUMENT_TYPE_ONLINE.equalsIgnoreCase(channelTransferVO.getPaymentInstSource())) {
			channelTransferVO.setStatusDesc(PretupsI.CHANNEL_TRANSFER_ORDER_PENDING);
			
			final OperatorUtil operatorUtil = new OperatorUtil();
			Map map = operatorUtil.getInitMap(con, channelTransferVO, receiverChannelUserVO, channelTransferVO.getPaymentInstType());
			
	        Map<String, String> paymentGatewayMap = new HashMap<String, String>();
	        paymentGatewayMap.put("pgUrl", (String)map.get("PG_URL"));
	    if(!BTSLUtil.isNullString(Constants.getProperty("PG_SIMULATOR")) && Constants.getProperty("PG_SIMULATOR").equalsIgnoreCase("N"))
	        {
	        	paymentGatewayMap.put("pgDatanew", (String)map.get("PG_DATA"));
	        }else{
	        	paymentGatewayMap.put("pgDatanew", (String)map.get("PG_STR"));
	        }
	        paymentGatewayMap.put("pgData", (String)map.get("PG_STR"));
	        paymentGatewayMap.put("callbackUrl", (String)map.get("CALLBACK_URL"));
	        paymentGatewayMap.put("callbackData", (String)map.get("CALLBACK_STR"));
	        
	        PGTransactionLog.log(channelTransferVO.getTransferID(), PretupsI.EMPTY, channelTransferVO.getMsisdn(), 
            		channelTransferVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_DEBIT,
            		paymentGatewayMap.get("pgUrl"), paymentGatewayMap.get("callbackUrl"), PretupsI.TXN_LOG_STATUS_SUCCESS, "");
			
		} else {
			p_requestVO.setMessageArguments(new String[]{ channelTransferVO.getTransferID() });
			p_requestVO.setMessageCode(PretupsErrorCodesI.O2C_VOUCHER_TRF_SUCCESS);
			p_requestVO.setTransactionID(channelTransferVO.getTransferID());
		}
    }
}
