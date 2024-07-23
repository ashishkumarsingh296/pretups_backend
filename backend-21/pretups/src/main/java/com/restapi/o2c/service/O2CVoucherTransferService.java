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
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.receiver.RestReceiver;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.subscriber.businesslogic.BarredUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.KeyArgumentVO;
import com.btsl.util.OracleUtil;
import com.btsl.voms.util.VomsUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductVO;
import com.btsl.voms.voucher.businesslogic.VomsBatchVO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherDAO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.txn.pretups.user.businesslogic.ChannelUserTxnDAO;
import com.web.pretups.channel.transfer.businesslogic.ChannelTransferWebDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;

/**
 * @author deepa.shyam
 *
 */
public class O2CVoucherTransferService {
	private static Log log = LogFactory.getLog(O2CVoucherTransferService.class.getName());
	public static OperatorUtilI _operatorUtil = null;
	private static final float EPSILON=0.0000001f; 

	static {
		final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
		try {
			_operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
		} catch (Exception e) {
			log.errorTrace("static", e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					" O2CVoucherTransferService [initialize]", "", "", "",
					"Exception while loading the class at the call:" + e.getMessage());
		}
	}
	
	/**
	 * @param o2CVoucherTransferRequestVO
	 * @param response1
	 * @return
	 */
	public BaseResponseMultiple<JsonNode> processVoucherTransferRequest(O2CVoucherTransferRequestVO o2CVoucherTransferRequestVO,HttpServletResponse response1) {
		final String methodName = "processVoucherTransferRequest";
        RestReceiver.updateRequestIdChannel();
    	BaseResponseMultiple<JsonNode> baseResponseMultiple =null;
    	Connection con = null;
		MComConnectionI mcomCon = null;
    	RequestVO p_requestVO = null;
    	ErrorMap errorMap = new ErrorMap();
		BaseResponse baseResponse = new BaseResponse();
    	String serviceType = PretupsI.SERVICE_TYPE_O2C_VOUCHER_TRF;
        try {
        	mcomCon = new MComConnection();
			con = mcomCon.getConnection();
            p_requestVO = new RequestVO();
        	baseResponseMultiple=new BaseResponseMultiple<>();
    		ArrayList<BaseResponse> baseResponseFinalSucess = new ArrayList<>();
    		ArrayList<VomsBatchVO> vomsBatchList = new ArrayList<VomsBatchVO>();
			if (log.isDebugEnabled()) {
				log.debug("process", "Entered o2CVoucherInitiateRequestVO: " + o2CVoucherTransferRequestVO);
			}
			String msisdn = o2CVoucherTransferRequestVO.getData().getMsisdn();
			UserDAO userDAO = new UserDAO();
			ChannelUserVO  senderVO = (ChannelUserVO)userDAO.loadUsersDetails(con, msisdn);
			if (log.isDebugEnabled()) {
				log.debug("process", "Entered Sender VO: " + senderVO);
			}
			
			senderVO.setUserPhoneVO(userDAO.loadUserAnyPhoneVO(con, msisdn));
			if (senderVO.getUserPhoneVO() != null) {
				senderVO.setPinReset(senderVO.getUserPhoneVO().getPinReset());
			}
			senderVO.setServiceTypes(serviceType);
			p_requestVO.setServiceType(serviceType);
			p_requestVO.setRequestGatewayType(o2CVoucherTransferRequestVO.getReqGatewayType());
			p_requestVO.setLocale(new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY));

			O2CVoucherTransferReqData O2CVoucherTransferReqData = o2CVoucherTransferRequestVO.getO2CTrasfereReqData();
			ChannelUserVO receiverChannelUserVO = new ChannelUserVO();
			Date curDate = new Date();
			//validate receiver
			Boolean isUserDetailLoad = false;
			ChannelUserTxnDAO channelUserTxnDAO = new ChannelUserTxnDAO();
			if (!BTSLUtil.isNullString(O2CVoucherTransferReqData.getExtcode2())) {
				receiverChannelUserVO = channelUserTxnDAO.loadChannelUserDetailsForTransferIfReqExtgw(con,
						O2CVoucherTransferReqData.getExtcode2(), null, curDate);
				if (receiverChannelUserVO == null) {
					throw new BTSLBaseException("O2CVoucherTransferService", "process",
							PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE, 0, null);
				}

				isUserDetailLoad = true;

			} else if (!BTSLUtil.isNullString(O2CVoucherTransferReqData.getLoginid2())) {
				receiverChannelUserVO = channelUserTxnDAO.loadChannelUserDetailsForTransferIfReqExtgw(con, null,
						O2CVoucherTransferReqData.getLoginid2(), curDate);
				if (receiverChannelUserVO == null) {
					throw new BTSLBaseException("O2CVoucherTransferService", "process",
							PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID, 0, null);
				}

				isUserDetailLoad = true;
			}
			else if(!BTSLUtil.isNullString(O2CVoucherTransferReqData.getMsisdn2())) {
				ChannelUserDAO channelUserDAO = new ChannelUserDAO();
				receiverChannelUserVO = channelUserDAO.loadChannelUserDetails(con,
						O2CVoucherTransferReqData.getMsisdn2());
				if (receiverChannelUserVO == null) {
					throw new BTSLBaseException("O2CVoucherTransferService", "process",
							PretupsErrorCodesI.EXT_XML_ERROR_INVALID_MSISDN, 0, null);
				}

				isUserDetailLoad = true;
			}
			if (!(receiverChannelUserVO == null) && isUserDetailLoad) {
				if (!BTSLUtil.isNullString(O2CVoucherTransferReqData.getExtcode2())) {
					if (!O2CVoucherTransferReqData.getExtcode2().equalsIgnoreCase(receiverChannelUserVO.getExternalCode())) {
						throw new BTSLBaseException("O2CVoucherTransferService", "process",
								PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE, 0, null);
					}
				}
				if (!BTSLUtil.isNullString(O2CVoucherTransferReqData.getLoginid2())) {
					if (!O2CVoucherTransferReqData.getLoginid2().equalsIgnoreCase(receiverChannelUserVO.getLoginID())) {

						throw new BTSLBaseException("O2CVoucherTransferService", "process",
								PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID, 0, null);
					}
				}
				if (!BTSLUtil.isNullString(O2CVoucherTransferReqData.getMsisdn2())) {
					if (SystemPreferences.SECONDARY_NUMBER_ALLOWED) {
						UserPhoneVO phoneVO = userDAO.loadUserAnyPhoneVO(con, O2CVoucherTransferReqData.getMsisdn2());
						if (phoneVO != null && ((receiverChannelUserVO.getUserID()).equals(phoneVO.getUserId()))) {
							receiverChannelUserVO.setPrimaryMsisdn(receiverChannelUserVO.getMsisdn());
							receiverChannelUserVO.setMsisdn(O2CVoucherTransferReqData.getMsisdn2());
						} else {
							throw new BTSLBaseException("O2CVoucherTransferService", "process",
									PretupsErrorCodesI.EXT_XML_ERROR_INVALID_MSISDN, 0, null);
						}
					} else if (!O2CVoucherTransferReqData.getMsisdn2().equalsIgnoreCase(receiverChannelUserVO.getMsisdn())) {
						throw new BTSLBaseException("O2CVoucherTransferService", "process",
								PretupsErrorCodesI.EXT_XML_ERROR_INVALID_MSISDN, 0, null);
					}
				}
				
			}
			Boolean flag = o2CValidate(con, O2CVoucherTransferReqData, senderVO, receiverChannelUserVO, errorMap, vomsBatchList);
			if(!flag){
				o2cService(con, receiverChannelUserVO, o2CVoucherTransferRequestVO, senderVO, vomsBatchList,p_requestVO);
				baseResponse.setMessage(RestAPIStringParser.getMessage(
				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), p_requestVO.getMessageCode(),
				p_requestVO.getMessageArguments()));
				baseResponse.setMessageCode(p_requestVO.getMessageCode());
				baseResponse.setStatus(200);
				baseResponse.setTransactionId(p_requestVO.getTransactionID());
				baseResponseFinalSucess.add(baseResponse);
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
        
        if(be.getMessageKey()==null){
	    	baseResponseMultiple.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
			baseResponseMultiple.setMessage(RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.REQ_NOT_PROCESS,
					null));
        }
        else{
        	String resmsg = RestAPIStringParser.getMessage(
				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), be.getMessageKey(),
				null);
	    	baseResponseMultiple.setMessageCode(be.getMessageKey());
	    	baseResponseMultiple.setMessage(resmsg);
        }
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


	
	/**
	 * @param con
	 * @param o2CVoucherTransferReqData
	 * @param senderVO
	 * @param receiverVO
	 * @param errorMap
	 * @param vomsBatchList
	 * @return
	 * @throws Exception
	 */
	public static Boolean o2CValidate(Connection con,O2CVoucherTransferReqData o2CVoucherTransferReqData,ChannelUserVO senderVO,ChannelUserVO receiverVO,ErrorMap errorMap, ArrayList<VomsBatchVO> vomsBatchList) throws Exception {
		ArrayList<MasterErrorList> masterErrorListMain = new ArrayList<MasterErrorList>();
		ArrayList<RowErrorMsgLists> rowErrorMsgListsVoucher = new ArrayList<RowErrorMsgLists>();
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		Boolean error = false;
		if(BTSLUtil.isNullString(o2CVoucherTransferReqData.getMsisdn2()) && BTSLUtil.isNullString(o2CVoucherTransferReqData.getExtcode2()) && BTSLUtil.isNullString(o2CVoucherTransferReqData.getLoginid2())){
			error = true;
			MasterErrorList masterErrorList = new MasterErrorList();
			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_RECIEVER_CREDENTIALS, null);
			masterErrorList.setErrorCode(PretupsErrorCodesI.INVALID_RECIEVER_CREDENTIALS);
			masterErrorList.setErrorMsg(msg);
			masterErrorListMain.add(masterErrorList);
		}
		HashMap<String,Object> reqData = new HashMap<String,Object>();
		reqData.put("remarks", o2CVoucherTransferReqData.getRemarks());
		reqData.put("refNumber", o2CVoucherTransferReqData.getRefnumber());
		reqData.put("paymentDetails", o2CVoucherTransferReqData.getPaymentDetails());
		reqData.put("voucherDetails", o2CVoucherTransferReqData.getVoucherDetails());
		boolean reqError = O2cCommonService.validateRequestData(masterErrorListMain, locale,reqData);
		if(reqError){
	    	 errorMap.setMasterErrorList(masterErrorListMain);
			 }
		error = validateVoucher(con, vomsBatchList, o2CVoucherTransferReqData.getVoucherDetails(), rowErrorMsgListsVoucher, receiverVO.getUserID(), senderVO.getNetworkID());
		 
		 if(error){
			 errorMap.setRowErrorMsgLists(rowErrorMsgListsVoucher);
			 }
		 if(reqError||error){
			 return true;
		 }
		 return false;
	}

	
	/**
	 * @param con
	 * @param vomsBatchlist
	 * @param voucherDetailsList
	 * @param rowErrorMsgListsVoucher
	 * @param userId
	 * @param toUserId
	 * @param networkCode
	 * @return
	 * @throws BTSLBaseException
	 */public static boolean validateVoucher(Connection con,ArrayList<VomsBatchVO> vomsBatchlist,List<VoucherDetails> voucherDetailsList,ArrayList<RowErrorMsgLists> rowErrorMsgListsVoucher,String toUserId,String networkCode) throws BTSLBaseException{
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
				ChannelTransferWebDAO channelTransferWebDAO = new ChannelTransferWebDAO();
				
				final Map<String, String> denomMap = new HashMap<String, String>();
				int i =0;
				Date currentDate = new Date();
				for(VoucherDetails voucherDetails : voucherDetailsList){
					VomsBatchVO vomsBatchVO = new VomsBatchVO();
					long quantity = 0;
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

				   if (BTSLUtil.isNullString(voucherDetails.getFromSerialNo())){
					    error=true;
						MasterErrorList masterErrorListss = new MasterErrorList();
						masterErrorListss.setErrorCode(PretupsErrorCodesI.O2C_FROMSNO_REQ);
						masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_FROMSNO_REQ, null));
						masterErrorLists1.add(masterErrorListss);
					}
				   else if (!BTSLUtil.isNumeric(voucherDetails.getFromSerialNo())){
					    error=true;
						MasterErrorList masterErrorListss = new MasterErrorList();
						masterErrorListss.setErrorCode(PretupsErrorCodesI.O2C_FROMSNO_NUMERIC);
						masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_FROMSNO_NUMERIC, null));
						masterErrorLists1.add(masterErrorListss);
					}
				   if (BTSLUtil.isNullString(voucherDetails.getToSerialNo())) {
					    error=true;
						MasterErrorList masterErrorListss = new MasterErrorList();
						masterErrorListss.setErrorCode(PretupsErrorCodesI.O2C_TOSNO_REQ);
						masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_TOSNO_REQ, null));
						masterErrorLists1.add(masterErrorListss);
					}
				   else if (!BTSLUtil.isNumeric(voucherDetails.getToSerialNo())) {
					    error=true;
						MasterErrorList masterErrorListss = new MasterErrorList();
						masterErrorListss.setErrorCode(PretupsErrorCodesI.O2C_TOSNO_NUMERIC);
						masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_TOSNO_NUMERIC, null));
						masterErrorLists1.add(masterErrorListss);
					}
					if (!BTSLUtil.isNullString(voucherDetails.getToSerialNo()) && !BTSLUtil.isNullString(voucherDetails.getFromSerialNo()) 
							&& BTSLUtil.isNumeric(voucherDetails.getToSerialNo()) && BTSLUtil.isNumeric(voucherDetails.getFromSerialNo()) 
							&& !BTSLUtil.isNullString(voucherDetails.getDenomination()) && BTSLUtil.isDecimalValue(voucherDetails.getDenomination())) {
	                    if (Long.parseLong(voucherDetails.getToSerialNo()) < Long.parseLong(voucherDetails.getFromSerialNo())) {
	                    	error=true;
							MasterErrorList masterErrorListss = new MasterErrorList();
							masterErrorListss.setErrorCode(PretupsErrorCodesI.O2C_FROM_TO_INVALID);
							masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_FROM_TO_INVALID, null));
							masterErrorLists1.add(masterErrorListss);
	                    }
	                    
	                    quantity = Long.parseLong(voucherDetails.getToSerialNo()) - Long.parseLong(voucherDetails.getFromSerialNo()) + 1;
	    				 if(channelTransferWebDAO.doesRangeContainMultipleProfilesforO2c(con, voucherDetails.getFromSerialNo(), voucherDetails.getToSerialNo(), quantity,voucherDetails.getVoucherType(),voucherDetails.getVouchersegment(),voucherDetails.getDenomination(),networkCode)){
	    					error=true;
							MasterErrorList masterErrorListss = new MasterErrorList();
							masterErrorListss.setErrorCode(PretupsErrorCodesI.O2C_SNO_MULTPROF);
							masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_SNO_MULTPROF, new String[] {voucherDetails.getFromSerialNo(), voucherDetails.getToSerialNo()}));
							masterErrorLists1.add(masterErrorListss);
	    				}
	    				else{
	    					vomsBatchVO.setQuantity(String.valueOf(quantity));
	    				}
					  if (denomMap.containsKey(voucherDetails.getDenomination())) {
	                      if (Long.parseLong(voucherDetails.getFromSerialNo()) <= Long.parseLong(denomMap.get(voucherDetails.getDenomination()))) {
	                    	  error=true;
							  MasterErrorList masterErrorListss = new MasterErrorList();
							  masterErrorListss.setErrorCode(PretupsErrorCodesI.O2C_SNO_SEQUENTIAL);
							  masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_SNO_SEQUENTIAL, null));
							  masterErrorLists1.add(masterErrorListss);
	                      } else {
	                          denomMap.put(voucherDetails.getDenomination(), voucherDetails.getToSerialNo());
	                      }
	                  } else {
	                      denomMap.put(voucherDetails.getDenomination(), voucherDetails.getToSerialNo());
	                  }
	    				  
	    				VomsVoucherDAO vomsVoucherDAO = new VomsVoucherDAO();
	    				VomsVoucherVO vomsVoucherVO=vomsVoucherDAO.getVoucherDetails(con,voucherDetails.getFromSerialNo());
			            if(vomsVoucherVO!=null){
			            	vomsBatchVO.setFromSerialNo(voucherDetails.getFromSerialNo());
		    				vomsBatchVO.setToSerialNo(voucherDetails.getToSerialNo());
				            if (!vomsVoucherVO.getUserLocationCode().equals(networkCode)) 
				            {
			            	  error=true;
							  MasterErrorList masterErrorListss = new MasterErrorList();
							  masterErrorListss.setErrorCode(PretupsErrorCodesI.VOMS_O2C_VOUCHERS_FROM_DIFFERENT_NETWORK);
							  masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.VOMS_O2C_VOUCHERS_FROM_DIFFERENT_NETWORK, null));
							  masterErrorLists1.add(masterErrorListss);
			            	}
		    		           
							final double denomination =(vomsVoucherVO.getMRP()/((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue());
		    					
							
							vomsBatchVO.setProductID(vomsVoucherVO.getProductID());
		    				vomsBatchVO.setVoucherType(vomsVoucherVO.getVoucherType());
		    				vomsBatchVO.setVouchersegment(vomsVoucherVO.getVoucherSegment());
		    				if(denomination!= Double.parseDouble(voucherDetails.getDenomination())){
		    					 error=true;
								 MasterErrorList masterErrorListss = new MasterErrorList();
								 masterErrorListss.setErrorCode(PretupsErrorCodesI.O2C_SNO_DENO_DIFF);
								 masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_SNO_DENO_DIFF, new String[]{voucherDetails.getDenomination()}));
								 masterErrorLists1.add(masterErrorListss);
				            }else{
				            	vomsBatchVO.setDenomination(""+denomination);
				            }
			            }
	    				vomsBatchVO.setCreatedBy(PretupsI.CATEGORY_TYPE_OPT);
	    				vomsBatchVO.set_NetworkCode(networkCode);
	    				vomsBatchVO.setCreatedDate(currentDate);
	    				vomsBatchVO.setModifiedDate(currentDate);
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
	        				VomsProductDAO vomsProductDAO = new VomsProductDAO();

	    					// load product for vouchertype
		    				ArrayList<VomsProductVO>vomsProductlist = vomsProductDAO.loadProductDetailsList(con, vomsBatchVO.getVoucherType(), "'" + VOMSI.VOMS_STATUS_ACTIVE + "'", false, "", networkCode, vomsBatchVO.getSegment());
		    				if(!BTSLUtil.isNullOrEmptyList(vomsProductlist)) {
		    					ArrayList<VomsProductVO> itemlist = new ArrayList<>();
		    					for (VomsProductVO vomsProductVO : vomsProductlist) {
		                     		if (Math.abs(Double.parseDouble(vomsBatchVO.getDenomination())-vomsProductVO.getMrp()) < EPSILON) {
		                     			itemlist.add(vomsProductVO);
		                     		}
		                     	}
		                     	vomsBatchVO.setProductlist(itemlist);
		    				} 

					    	String type = vomsProductDAO.getTypeFromVoucherType(con, vomsBatchVO.getVoucherType());
							
		    				if (channelTransferWebDAO.validateVoucherSerialNo(con, vomsBatchVO.getFromSerialNo(),"null",PretupsI.TRANSFER_TYPE_O2C)) {
		     					 error=true;
								 MasterErrorList masterErrorListss = new MasterErrorList();
								 masterErrorListss.setErrorCode(PretupsErrorCodesI.FROM_SERIAL_PENDING);
								 masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.FROM_SERIAL_PENDING, new String[]{voucherDetails.getDenomination()}));
								 masterErrorLists1.add(masterErrorListss);
		    				
							}else if (!channelTransferWebDAO.validateSerialNo(con, vomsBatchVO.getFromSerialNo(), vomsBatchVO.getProductID(),type)) {
		    					 error=true;
								 MasterErrorList masterErrorListss = new MasterErrorList();
								 masterErrorListss.setErrorCode(PretupsErrorCodesI.FROM_SERIAL_SOLD);
								 masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.FROM_SERIAL_SOLD, new String[]{voucherDetails.getDenomination()}));
								 masterErrorLists1.add(masterErrorListss);
							}
		    				
							if (channelTransferWebDAO.validateVoucherSerialNo(con, vomsBatchVO.getToSerialNo(), "null",PretupsI.TRANSFER_TYPE_O2C)) {
								 error=true;
								MasterErrorList masterErrorListss = new MasterErrorList();
								masterErrorListss.setErrorCode(PretupsErrorCodesI.TO_SERIAL_PENDING);
								masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.TO_SERIAL_PENDING, new String[]{voucherDetails.getDenomination()}));
								masterErrorLists1.add(masterErrorListss);
		    				
							}else if (!channelTransferWebDAO.validateSerialNo(con, vomsBatchVO.getToSerialNo(), vomsBatchVO.getProductID(),type)) {
								error=true;
								MasterErrorList masterErrorListss = new MasterErrorList();
								masterErrorListss.setErrorCode(PretupsErrorCodesI.TO_SERIAL_SOLD);
								masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.TO_SERIAL_SOLD,  new String[]{voucherDetails.getDenomination()}));
								masterErrorLists1.add(masterErrorListss);
							}
							
		    				final ArrayList<VomsBatchVO> usedBatches = channelTransferWebDAO.validateBatch(con, vomsBatchVO);
		    				if (usedBatches != null && !usedBatches.isEmpty()) {
		    					error=true;
								MasterErrorList masterErrorListss = new MasterErrorList();
								masterErrorListss.setErrorCode(PretupsErrorCodesI.VOMS_INVALID_BATCH_C2C);
								masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.VOMS_INVALID_BATCH_C2C, new String[]{vomsBatchVO.getFromSerialNo(),vomsBatchVO.getToSerialNo()}));
								masterErrorLists1.add(masterErrorListss);
		    				}
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
		          throw new BTSLBaseException("O2CVoucherTransferService", methodName, e.getMessage());
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting ....");
			}
			return error;
		}
		
		/**
	 * @param con
	 * @param receiverVo
	 * @param y
	 * @param senderVO
	 * @param vomsBatchList
	 * @param p_requestVO
	 * @throws Exception
	 */
	public static void o2cService(Connection con,ChannelUserVO receiverVo,O2CVoucherTransferRequestVO o2cVoucherTransferRequestVO,ChannelUserVO senderVO,ArrayList<VomsBatchVO> vomsBatchList,RequestVO p_requestVO )
			throws Exception {
		 Locale locale= new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
    	 Date currentDate = new Date();
		 UserPhoneVO userPhoneVO = null;
		 UserPhoneVO phoneVO = null;
         BarredUserDAO barredUserDAO = new BarredUserDAO();
		 UserDAO userDAO = new UserDAO();
		 ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		 ChannelUserVO receiverChannelUserVO = new ChannelUserVO();
     	 ChannelUserTxnDAO channelUserTxnDAO = new ChannelUserTxnDAO();
     	 Boolean isUserDetailLoad = false;
     	 O2CVoucherTransferReqData o2CVoucherTransferReqData = o2cVoucherTransferRequestVO.getO2CTrasfereReqData();
		 UserPhoneVO PrimaryPhoneVO_R = new UserPhoneVO();
            if (!senderVO.isStaffUser()) {
                userPhoneVO = (UserPhoneVO) senderVO.getUserPhoneVO();
            } else {
                userPhoneVO = (UserPhoneVO) senderVO.getStaffUserDetails().getUserPhoneVO();
            }
		p_requestVO.setLocale(locale);
		p_requestVO.setSenderVO(senderVO);
		p_requestVO.setSourceType(o2cVoucherTransferRequestVO.getSourceType());
		p_requestVO.setServiceType(PretupsI.SERVICE_TYPE_O2C_VOUCHER_TRF);
		p_requestVO.setRequestGatewayType(o2cVoucherTransferRequestVO.getReqGatewayType());
    	 // validate the channel user
		// check that the channel user is barred or not
 		if(barredUserDAO.isExists(con, PretupsI.C2S_MODULE, senderVO.getNetworkID(), senderVO.getMsisdn(), PretupsI.USER_TYPE_SENDER, null)){
 			throw new BTSLBaseException("O2CVoucherTransferService", "process", PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_SENDER_BAR);
 		}


    	if(BTSLUtil.isNullString(o2CVoucherTransferReqData.getMsisdn2()) && BTSLUtil.isNullString(o2CVoucherTransferReqData.getExtcode2()) && BTSLUtil.isNullString(o2CVoucherTransferReqData.getLoginid2())){
			throw new BTSLBaseException("O2CVoucherTransferService", "process",
					PretupsErrorCodesI.INVALID_RECIEVER_CREDENTIALS, 0, null);
		}
    	
    	
    	if (!BTSLUtil.isNullString(o2CVoucherTransferReqData.getExtcode2())) {
			receiverChannelUserVO = channelUserTxnDAO.loadChannelUserDetailsForTransferIfReqExtgw(con,
					o2CVoucherTransferReqData.getExtcode2(), null, currentDate);
			if (receiverChannelUserVO == null) {
				throw new BTSLBaseException("O2CVoucherTransferService", "process",
						PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE, 0, null);
			}
			isUserDetailLoad = true;
		} else if (!BTSLUtil.isNullString(o2CVoucherTransferReqData.getLoginid2())) {
			receiverChannelUserVO = channelUserTxnDAO.loadChannelUserDetailsForTransferIfReqExtgw(con, null,
					o2CVoucherTransferReqData.getLoginid2(), currentDate);
			if (receiverChannelUserVO == null) {
				throw new BTSLBaseException("O2CVoucherTransferService", "process",
						PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID, 0, null);
			}
			isUserDetailLoad = true;
		}else{
			receiverChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, o2CVoucherTransferReqData.getMsisdn2(),
					true, currentDate, false);
			isUserDetailLoad = true;
		}
        	
		if (!(receiverChannelUserVO == null) && isUserDetailLoad) {
			if (!BTSLUtil.isNullString(o2CVoucherTransferReqData.getExtcode2())) {
				if (!o2CVoucherTransferReqData.getExtcode2().equalsIgnoreCase(receiverChannelUserVO.getExternalCode())) {
					throw new BTSLBaseException("O2CVoucherTransferService", "process",
							PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE, 0, null); 
				}
			}
			if (!BTSLUtil.isNullString(o2CVoucherTransferReqData.getLoginid2())) {
				if (!o2CVoucherTransferReqData.getLoginid2().equalsIgnoreCase(receiverChannelUserVO.getLoginID())) {
					throw new BTSLBaseException("O2CVoucherTransferService", "process",
							PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID, 0, null);
				}
			}
			if (!BTSLUtil.isNullString(o2CVoucherTransferReqData.getMsisdn2())) {
				if (SystemPreferences.SECONDARY_NUMBER_ALLOWED) {
					 phoneVO = userDAO.loadUserAnyPhoneVO(con, o2CVoucherTransferReqData.getMsisdn2());
					if (phoneVO != null && ((receiverChannelUserVO.getUserID()).equals(phoneVO.getUserId()))) {
						if (SystemPreferences.MESSAGE_TO_PRIMARY_REQUIRED
								&& ("N".equalsIgnoreCase(phoneVO.getPrimaryNumber()))) {
							PrimaryPhoneVO_R = userDAO.loadUserAnyPhoneVO(con, receiverChannelUserVO.getMsisdn());
						}
						receiverChannelUserVO.setPrimaryMsisdn(receiverChannelUserVO.getMsisdn());
						receiverChannelUserVO.setMsisdn(o2CVoucherTransferReqData.getMsisdn2());
					} else {
						throw new BTSLBaseException("O2CVoucherTransferService", "process",
								PretupsErrorCodesI.EXT_XML_ERROR_INVALID_MSISDN, 0, null);
					}
				} else if (!o2CVoucherTransferReqData.getMsisdn2().equalsIgnoreCase(receiverChannelUserVO.getMsisdn())) {
					throw new BTSLBaseException("O2CVoucherTransferService", "process",
							PretupsErrorCodesI.EXT_XML_ERROR_INVALID_MSISDN, 0, null);
				}
			}
			
			
			String messageArray=p_requestVO.getServiceType()+" "+o2CVoucherTransferReqData.getPin();
			p_requestVO.setRequestMessageArray(messageArray.split(" "));
			
			// To set the msisdn in the request message array...
			if (BTSLUtil.isNullString(o2CVoucherTransferReqData.getMsisdn2())
					&& BTSLUtil.isNullString(((String)PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR)))) {
				final String message[] = p_requestVO.getRequestMessageArray();
				final String[] newMessageArr = new String[message.length + 1];
				for (int j = 0; j < newMessageArr.length - 1; j++) {
					newMessageArr[j] = message[j];
				}
				for (int i = newMessageArr.length; i > 0; i--) {
					String temp;
					if (i < newMessageArr.length - 1) {
						temp = newMessageArr[i];
						newMessageArr[i + 1] = newMessageArr[i];
						newMessageArr[i] = temp;
					}
				}
				newMessageArr[1] = receiverChannelUserVO.getMsisdn();
				p_requestVO.setRequestMessageArray(newMessageArr);
			} 
			else {
				final String[] mesgArr = p_requestVO.getRequestMessageArray();
				mesgArr[1] = receiverChannelUserVO.getMsisdn();
				p_requestVO.setRequestMessageArray(mesgArr);
			}
		}
		
		final String messageArr[] = p_requestVO.getRequestMessageArray();

		if (messageArr.length < 2) {
			throw new BTSLBaseException("C2CTransferController", "process",
					PretupsErrorCodesI.ERROR_INVALID_REQUESTFORMAT, 0,
					new String[] { p_requestVO.getActualMessageFormat() }, null);
		}
		

		if (!BTSLUtil.isNumeric(messageArr[1])) {
			p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_USER_CODE_FORMAT);
			throw new BTSLBaseException("C2CTransferController", "process",
					PretupsErrorCodesI.ERROR_INVALID_USER_CODE_FORMAT);
		}


	     // validate the PIN if it is in the request
		if 	(PretupsI.YES.equals(senderVO.getCategoryVO().getSmsInterfaceAllowed())) {
			try {
				ChannelUserBL.validatePIN(con, senderVO, o2CVoucherTransferReqData.getPin());
			} catch (BTSLBaseException be) {
				if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN))
						|| (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
					OracleUtil.commit(con);
				}
				throw be;
			}
		}
    	// Validate the receiver
		String receiverUserCode = messageArr[1];
		receiverUserCode = _operatorUtil.addRemoveDigitsFromMSISDN(PretupsBL.getFilteredMSISDN(receiverUserCode));
		if (!BTSLUtil.isValidMSISDN(receiverUserCode)) {
			p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_REC_USERCODE);
			throw new BTSLBaseException("O2CVoucherTransferService", "process",
					PretupsErrorCodesI.ERROR_INVALID_REC_USERCODE);
			}
		final String msisdnPrefix = PretupsBL.getMSISDNPrefix(receiverUserCode);
		// Getting network details
		final NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
		if (networkPrefixVO == null) {
			throw new BTSLBaseException("O2CVoucherTransferService", "process",
					PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_UNSUPPORTED_NETWORK, 0,
					new String[] { receiverUserCode }, null);
		}

		if (barredUserDAO.isExists(con, PretupsI.C2S_MODULE, networkPrefixVO.getNetworkCode(), receiverUserCode,
				PretupsI.USER_TYPE_RECEIVER, null)) { 
			throw new BTSLBaseException("O2CVoucherTransferService", "process",
					PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_RECEIVER_BAR, 0,
					new String[] { receiverUserCode }, null);
			}

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
				receiverChannelUserVO.setMsisdn(receiverUserCode);
			} else {
				receiverChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, receiverUserCode,
						true, currentDate, false);
			}
		}
	
    	ChannelTransferBL.c2cTransferUserValidateReceiver(con, receiverChannelUserVO, p_requestVO, currentDate, receiverUserCode);

    		
    	final LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(p_requestVO.getLocale());
		if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
			senderVO.setCommissionProfileSuspendMsg(senderVO.getCommissionProfileLang1Msg());
			receiverChannelUserVO
					.setCommissionProfileSuspendMsg(receiverChannelUserVO.getCommissionProfileLang1Msg());
		} else {
			senderVO.setCommissionProfileSuspendMsg(senderVO.getCommissionProfileLang2Msg());
			receiverChannelUserVO
					.setCommissionProfileSuspendMsg(receiverChannelUserVO.getCommissionProfileLang2Msg());
		}
		// load the approval limit of the user
		final ChannelTransferRuleVO channelTransferRuleVO = new ChannelTransferRuleDAO().loadTransferRule(con, receiverChannelUserVO.getNetworkID(), receiverChannelUserVO.getDomainID(),
				PretupsI.CATEGORY_TYPE_OPT, receiverChannelUserVO.getCategoryCode(), PretupsI.TRANSFER_RULE_TYPE_OPT, true);
		if (channelTransferRuleVO == null) {
			throw new BTSLBaseException("O2CVoucherTransferService", "process",
					PretupsErrorCodesI.USER_TRANSFER_RULE_NOT_EXIST, 0,
					null, null);
		} else if (PretupsI.NO.equals(channelTransferRuleVO.getTransferAllowed())) {
			throw new BTSLBaseException("O2CVoucherTransferService", "process",
					PretupsErrorCodesI.ERROR_USER_RETURN_NOT_ALLOWED, 0,
					null, null);
		} else if (channelTransferRuleVO.getProductVOList() == null || channelTransferRuleVO.getProductVOList().isEmpty()) {
			throw new BTSLBaseException("O2CVoucherTransferService", "process",
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
			throw new BTSLBaseException("O2CVoucherTransferService", "process",
					PretupsErrorCodesI.PRODUCT_NOT_ASSOSIATED, 0,
					new String[] { senderVO.getLoginID(),VOMSI.DEFAULT_PRODUCT_CODE }, null);
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
			throw new BTSLBaseException("O2CVoucherTransferService", "process",
					PretupsErrorCodesI.PRODUCT_NOT_MATCH_WITH_TRANSFERRULE, 0,
					new String[] { senderVO.getLoginID() }, null);
		}

		
		final ArrayList<ChannelTransferItemsVO> channelTransferItemsList = new ArrayList<ChannelTransferItemsVO>();
		 Long totalVoucherQty = 0L;
		 double totalRequestedQuantity = 0.0;
		  double requestedMrp = 0.0;
		for (int i = 0; i < vomsBatchList.size(); i++) {
			final VomsBatchVO vomsBatchVO = (VomsBatchVO) vomsBatchList.get(i);
			
			
			/* if (vomsBatchVO.getProductlist() != null) { */
			try{
				final double denomination = Double.parseDouble(vomsBatchVO.getDenomination());
				final Long quantity = (Long.parseLong(vomsBatchVO.getToSerialNo())) - (Long.parseLong(vomsBatchVO.getFromSerialNo())) + 1;
				 requestedMrp = denomination * quantity;
				vomsBatchVO.setQuantity(String.valueOf(quantity));
				totalVoucherQty = totalVoucherQty+quantity;
				totalRequestedQuantity = totalRequestedQuantity + requestedMrp;
				//vomsBatchVO.setProductName("XYZ");
			}catch(NumberFormatException ex){
				throw new BTSLBaseException("", "", "channeltransfer.transferdetails.error.serialnumbernumeric", "voucherproductdetails");	
			}
		
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
		
		if (SystemPreferences.SECONDARY_NUMBER_ALLOWED) {
			if (!(senderVO.getMsisdn()).equalsIgnoreCase(p_requestVO.getFilteredMSISDN())) {
				senderVO.setPrimaryMsisdn(senderVO.getMsisdn());
				senderVO.setMsisdn(p_requestVO.getFilteredMSISDN());
				if (SystemPreferences.MESSAGE_TO_PRIMARY_REQUIRED) {
					primaryPhoneVO_S = userDAO.loadUserAnyPhoneVO(con, senderVO.getPrimaryMsisdn());
				}
			}
			receiverChannelUserVO.setUserCode(receiverUserCode);
		}
		
		//prepare channeltransferVO
		ChannelTransferVO channelTransferVO = O2cCommonService.prepareChannelTransferProfileVO(o2CVoucherTransferReqData.getPaymentDetails(), con, receiverChannelUserVO, channelTransferItemsList, vomsBatchList);
		channelTransferVO.setSource(o2cVoucherTransferRequestVO.getSourceType());
		channelTransferVO.setRequestGatewayCode(o2cVoucherTransferRequestVO.getReqGatewayCode());
	    channelTransferVO.setRequestGatewayType(o2cVoucherTransferRequestVO.getReqGatewayType());
		channelTransferVO.setFirstApproverLimit(channelTransferRuleVO.getFirstApprovalLimit());
		channelTransferVO.setSecondApprovalLimit(channelTransferRuleVO.getSecondApprovalLimit());
		channelTransferVO.setTransferCategory(channelTransferRuleVO.getTransferType());
		channelTransferVO.setActiveUserId(senderVO.getUserID());
		channelTransferVO.setTransferInitatedBy(senderVO.getUserID());
		channelTransferVO.setReferenceNum(o2CVoucherTransferReqData.getRefnumber());
		channelTransferVO.setChannelRemarks(o2CVoucherTransferReqData.getRemarks());
		channelTransferVO.setCreatedBy(PretupsI.CATEGORY_TYPE_OPT);
		channelTransferVO.setModifiedBy(PretupsI.CATEGORY_TYPE_OPT);
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
			channelTransferVO.setSource(o2cVoucherTransferRequestVO.getSourceType());
			channelTransferVO.setRequestGatewayCode(o2cVoucherTransferRequestVO.getReqGatewayCode());
			channelTransferVO.setRequestGatewayType(o2cVoucherTransferRequestVO.getReqGatewayType());
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
			int insertCount = 0;
			insertCount = channelTransferDAO.addChannelTransfer(con, channelTransferVO);
			if (insertCount > 0) {
				con.commit();
				UserPhoneVO primaryPhoneVO = null;
				phoneVO = userDAO.loadUserAnyPhoneVO(con, channelTransferVO.getToUserCode());
				try{
					boolean _receiverMessageSendReq=false;
					_receiverMessageSendReq=((Boolean)PreferenceCache.getControlPreference(PreferenceI.REC_MSG_SEND_ALLOW,senderVO.getNetworkCode(),p_requestVO.getServiceType())).booleanValue();

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
				
				ChannelUserWebDAO channelUserWebDAO = new ChannelUserWebDAO();
				final String email = channelUserWebDAO.loadUserEmail(con, channelTransferVO.getToUserID());
				channelTransferVO.setEmail(email);

				if (SystemPreferences.O2C_EMAIL_NOTIFICATION) {
						O2cCommonService.sendEmailNotification(con, channelTransferVO, channelTransferDAO, "APV1O2CTRF", "o2c.email.notification.content");
						
						// send email notification to channel user only when operator is initiating the transfer
						O2cCommonService.sendEmailNotification(con, channelTransferVO, channelTransferDAO, "", "o2c.email.notification.subject.initiate");

					//}
				}
				p_requestVO.setMessageArguments(new String[]{ channelTransferVO.getTransferID() });
				p_requestVO.setMessageCode(PretupsErrorCodesI.O2C_VOUCHER_TRF_SUCCESS);
				p_requestVO.setTransactionID(channelTransferVO.getTransferID());
			}else {
				con.commit();
				throw new BTSLBaseException("O2CVoucherTransferService", "o2cService", "channeltransfer.transferdetailssuccess.msg.unsuccess", "");
				}
			}


}
