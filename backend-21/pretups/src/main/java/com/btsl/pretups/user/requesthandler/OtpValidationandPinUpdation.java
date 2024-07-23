package com.btsl.pretups.user.requesthandler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


//import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.login.UserOtpDAO;
import com.btsl.pretups.channel.receiver.RestReceiver;
import com.btsl.pretups.channel.transfer.businesslogic.UserOtpRequestMessage;
import com.btsl.pretups.channel.transfer.businesslogic.UserOtpVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserOtpValidationRequestParentVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserEventRemarksVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.ibm.icu.util.Calendar;
import com.web.user.businesslogic.UserWebDAO;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

@Path("")
@Tag(name = "${dummy.name}", description = "${dummy.desc}")//@Api(value="Channel User OTP Validate")
public class OtpValidationandPinUpdation implements ServiceKeywordControllerI {
	@Context
 	private HttpServletRequest httpServletRequest;
 	@POST
 	@Path("/c2s-receiver/otpvdpinrst")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
 	//@ApiOperation(value = "Channel User OTP Validation & PIN Reset ", response = PretupsResponse.class)

	@io.swagger.v3.oas.annotations.Operation(summary = "${otpvdpinrst.summary}", description="${otpvdpinrst.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = PretupsResponse.class))
							)
					}

					),


					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_CODE, description = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_DESC,  content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))

									 , examples ={@io.swagger.v3.oas.annotations.media.ExampleObject(value = com.btsl.common.BaseResponseRedoclyCommon.BAD_REQUEST)}

							)
					}),
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_UNAUTH_RESPONSE_CODE, description = com.btsl.util.Constants.API_UNAUTH_RESPONSE_DESC,  content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
							, examples ={@io.swagger.v3.oas.annotations.media.ExampleObject(value = com.btsl.common.BaseResponseRedoclyCommon.UNAUTH)}

									 )
					}),
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_CODE, description = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_DESC,  content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
							, examples ={@io.swagger.v3.oas.annotations.media.ExampleObject(value = com.btsl.common.BaseResponseRedoclyCommon.NOT_FOUND)}

							)
					}),
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_DESC,  content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								, examples ={@io.swagger.v3.oas.annotations.media.ExampleObject(value = com.btsl.common.BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR)}
					)})
			}
	)



	public PretupsResponse<JsonNode> processCP2PUserRequest(@Parameter(description = SwaggerAPIDescriptionI.OTP_VD_PIN_RST)UserOtpValidationRequestParentVO userOTPValidationRequestParentVO) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
 		PretupsResponse<JsonNode> response;
         RestReceiver restReceiver;
         RestReceiver.updateRequestIdChannel();
         final String requestIDStr = String.valueOf(RestReceiver.getRequestIdChannel());
         restReceiver = new RestReceiver();
         response = restReceiver.processCP2PRequestOperator(httpServletRequest,(JsonNode)(PretupsRestUtil.convertJSONToObject(PretupsRestUtil.convertObjectToJSONString(userOTPValidationRequestParentVO), new TypeReference<JsonNode>(){})), PretupsI.OTP_VALID_PIN_RST,requestIDStr);
         return response;
     }
 	
 	private static Log _log = LogFactory.getLog(OtpValidationandPinUpdation.class.getName());
	private static OperatorUtilI _operatorUtil = null;
	static {
	        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
	        try {
	            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
	        } catch (Exception e) {
	            _log.errorTrace("static", e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChangePinController[initialize]", "", "", "",
	                            "Exception while loading the class at the call:" + e.getMessage());
	        }
	    }
	@Override
	public void process(RequestVO p_requestVO){
		
		final String METHOD_NAME = "process";
		Connection con = null;
		MComConnectionI mcomCon = null;
		String otp = null;
		String newPin = null;
		String confirmNewPin = null;
		String msisdn = null;
		try{
		HashMap requestMap =  p_requestVO.getRequestMap();
		UserOtpVO userOtpVO = new UserOtpVO();
		mcomCon = new MComConnection();
		con = mcomCon.getConnection();
		UserOtpRequestMessage reqMsgObj = null;
		Gson gson = new Gson();
		ChannelUserVO channelUserVO = null;
        final ChannelUserVO userVO = (ChannelUserVO) p_requestVO.getSenderVO();
		msisdn = userVO.getMsisdn();
		if(requestMap!=null && PretupsI.MOBILE_APP_GATEWAY.equals(p_requestVO.getRequestGatewayCode())){
			//msisdn = (String) requestMap.get("MSISDN");
			otp = (String) requestMap.get("OTP");
			newPin = (String) requestMap.get("NEWPIN");
			confirmNewPin = (String) requestMap.get("CONFIRMPIN");
		}
		else if(PretupsI.REQUEST_SOURCE_TYPE_REST.equals(p_requestVO.getRequestGatewayCode()))
		{
			reqMsgObj = gson.fromJson(p_requestVO.getRequestMessage(), UserOtpRequestMessage.class);
			//msisdn = reqMsgObj.getMsisdn();
			otp = reqMsgObj.getOtp();
			newPin = reqMsgObj.getNewpin();
			confirmNewPin = reqMsgObj.getConfirmpin();
		}
			if(BTSLUtil.isNullString(otp)){
				p_requestVO.setMessageArguments(new String[] {"OTP"});
				p_requestVO.setMessageCode(PretupsErrorCodesI.EXTSYS_BLANK);
				throw new BTSLBaseException("OtpValidationandPinUpdation", "process",
						PretupsErrorCodesI.EXTSYS_BLANK, 0, null);
			}
			if(BTSLUtil.isNullString(newPin)){
				p_requestVO.setMessageArguments(new String[] {"NEWPIN"});
				p_requestVO.setMessageCode(PretupsErrorCodesI.EXTSYS_BLANK);
				throw new BTSLBaseException("OtpValidationandPinUpdation", "process",
						PretupsErrorCodesI.EXTSYS_BLANK, 0, null);
			}
			if(BTSLUtil.isNullString(confirmNewPin)){
				p_requestVO.setMessageArguments(new String[] {"CONFIRMPIN"});
				p_requestVO.setMessageCode(PretupsErrorCodesI.EXTSYS_BLANK);
				throw new BTSLBaseException("OtpValidationandPinUpdation", "process",
						PretupsErrorCodesI.EXTSYS_BLANK, 0, null);
			}
			UserOtpDAO userOtpDAO = new UserOtpDAO();
			userOtpVO = userOtpDAO.getDetailsOfUser(con, msisdn);
			int validityPeriodOtp = (Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OTP_VALIDITY_PERIOD));  //in minutes
			int invalidCountLimit = (Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_INVALID_OTP));
			int blockTime = (Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.BLOCK_TIME_INVALID_OTP));   //in minutes
			Date generatedTime = null;
			Date barredDate = null;
			Date consumedOn = null;
			barredDate = userOtpVO.getBarredDate();
			generatedTime = userOtpVO.getGeneratedOn();
			consumedOn = userOtpVO.getConsumedOn();
			Date currDate = new Date();
			String correctOtp = null;
			int invalidCount = 0;
			if(!BTSLUtil.isNullString(userOtpVO.getInvalidCount())){
			invalidCount = Integer.parseInt(userOtpVO.getInvalidCount());
			}
			if(!BTSLUtil.isNullString(userOtpVO.getOtppin())){	
			correctOtp = BTSLUtil.decryptText(userOtpVO.getOtppin());
			}
			int updateCnt = 0;
			if((BTSLUtil.getDifferenceInUtilDatesinSeconds(generatedTime, currDate) > validityPeriodOtp)){
				p_requestVO.setMessageCode(PretupsErrorCodesI.OTP_EXPIRED);
				throw new BTSLBaseException("OtpValidationandPinUpdation", "process",
						PretupsErrorCodesI.OTP_EXPIRED, 0, null);
			}
			
			else{
				if((invalidCountLimit == invalidCount) && (BTSLUtil.getDifferenceInUtilDatesinSeconds(barredDate, currDate) < blockTime)){
					int blockTimeRem = BTSLUtil.parseLongToInt((blockTime - BTSLUtil.getDifferenceInUtilDatesinSeconds(barredDate, currDate)));
					String blocktimeConverted = BTSLDateUtil.getTimeFromSeconds(blockTimeRem);
					p_requestVO.setMessageArguments(new String[] {blocktimeConverted});
					p_requestVO.setMessageCode(PretupsErrorCodesI.OTP_MAX_INVALID);
					throw new BTSLBaseException("OtpValidationandPinUpdation", "process",
							PretupsErrorCodesI.OTP_MAX_INVALID, 0, null);
				}
				else{
				if( otp.equals(correctOtp) && consumedOn==null){
					//update the pin
					UserPhoneVO userPhoneVO = null;
			     
		            if (!userVO.isStaffUser()) {
		                channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
		            } else {
		                channelUserVO = ((ChannelUserVO) p_requestVO.getSenderVO()).getStaffUserDetails();
		            }
		            userPhoneVO = channelUserVO.getUserPhoneVO();
		            final String[] messageArr = new String[4];
		            messageArr[1] = BTSLUtil.decryptText(userPhoneVO.getSmsPin());
		            messageArr[2] = newPin;
		            messageArr[3] = confirmNewPin;
		            final String modifificationType = PretupsI.USER_PIN_MANAGEMENT;
		            final UserDAO userDAO = new UserDAO();
		                if (!BTSLUtil.isNullString(messageArr[2])) {

							mcomCon = new MComConnection();
							con = mcomCon.getConnection();
		                    userPhoneVO.setForcePinCheckReqd(false);

		                    if (!((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_SMSPIN)).equals(messageArr[2]))
		                    {
		                        if (!messageArr[2].equals(messageArr[1])) {
		                            try {
		                                _operatorUtil.validatePINRules(messageArr[2]);
		                            } catch (BTSLBaseException be) {
		                                _log.errorTrace(METHOD_NAME, be);
		                                if (be.isKey()) {
		                                    if (PretupsErrorCodesI.PIN_LENGTHINVALID.equals(be.getMessageKey())) {
		                                        final String[] lenArr = new String[2];
		                                        lenArr[0] = String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_SMS_PIN_LENGTH))).intValue());
		                                        lenArr[1] = String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_SMS_PIN_LENGTH))).intValue());
		                                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_PIN_LENGTHINVALID, 0, lenArr, null);
		                                    } else if (PretupsErrorCodesI.NEWPIN_NOTNUMERIC.equals(be.getMessageKey())) {
		                                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_NEWPIN_NOTNUMERIC);
		                                    } else if (PretupsErrorCodesI.PIN_SAMEDIGIT.equals(be.getMessageKey())) {
		                                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_PIN_SAMEDIGIT);
		                                    } else if (PretupsErrorCodesI.PIN_CONSECUTIVE.equals(be.getMessageKey())) {
		                                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_PIN_CONSECUTIVE);
		                                    } else {
		                                        throw be;
		                                    }
		                                }
		                            }
		                           
		                            if (messageArr[2].equals(messageArr[3])) {
		                                final Date currentDate = new Date();
		                                channelUserVO.setModifiedOn(currentDate);
		                                if(!BTSLUtil.isNullString(channelUserVO.getActiveUserID())){
		                                	channelUserVO.setModifiedBy(channelUserVO.getActiveUserID());
		                                }
		                                
		                                final ChannelUserDAO channelUserDAO = new ChannelUserDAO();

		                                // check if new pin already exist in history
		                                // table
		                                final boolean pinStatus = userDAO.checkPasswordHistory(con, modifificationType, channelUserVO.getActiveUserID(), userPhoneVO.getMsisdn(),
		                                                BTSLUtil.encryptText(messageArr[2]));
		                                if (pinStatus) {
		                                    final String[] lenArr = new String[2];
		                                    lenArr[0] = String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PREV_PIN_NOT_ALLOW))).intValue());
		                                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_PIN_CHECK_HISTORY_EXIST, 0, lenArr, null);
		                                }
		                                final Calendar cal = BTSLDateUtil.getInstance();
		                                cal.setTime(userPhoneVO.getPinModifiedOn());
		                                final int resetPinExpiredInHours = ((Integer) PreferenceCache.getControlPreference(PreferenceI.RESET_PIN_EXPIRED_TIME_IN_HOURS, channelUserVO
		                                                .getNetworkID(), channelUserVO.getCategoryCode())).intValue();
		                                cal.add(Calendar.HOUR, resetPinExpiredInHours);
		                                final Date resetPinExpiredTime = cal.getTime();

		                                int count = 0;
		                                int isConsumed = 0;
		                                int remarksCount=0;
		                                if (PretupsI.STATUS_ACTIVE.equals(channelUserVO.getPinReset()) && p_requestVO.getCreatedOn().after(resetPinExpiredTime)) {
		                                    final String[] arr = { resetPinExpiredTime.toString() };
		                                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_RESET_PIN_EXPIRED, arr);
		                                } else {
		                                    count = channelUserDAO.changePin(con, messageArr[2], channelUserVO);
		                                    if(count>0)
		                                    isConsumed = userOtpDAO.updatePinSuccess(con,msisdn);
		                                    else {
			                                    con.rollback();
			                                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_PIN_CHANGE_FAILED);
			                                }
		                                }

		                                if (isConsumed > 0) {
		                                	if(((Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_EVENT_REMARKS)).booleanValue())
		                      	           { 
		                                		if(BTSLUtil.isNullString(p_requestVO.getRemarks())){

		                                			channelUserVO.setRemarks(PretupsI.SYSTEM);
		                                		}else{
		                                		channelUserVO.setRemarks(p_requestVO.getRemarks());
		                                		}
		                                	UserEventRemarksVO userRemarskVO=null;
		                   					ArrayList<UserEventRemarksVO> changePinRemarks=null;
		                   					if(channelUserVO!=null)
		                   		    		   {
		                   		    			   
		                   		    			   changePinRemarks=new ArrayList<UserEventRemarksVO>();
		                   	                  	userRemarskVO=new UserEventRemarksVO();
		                   	                  	userRemarskVO.setCreatedBy(channelUserVO.getCreatedBy());
		                   	                  	userRemarskVO.setCreatedOn(new Date());
		                   	                  	userRemarskVO.setEventType(PretupsI.CHANGE_PIN);
		                   	                  	userRemarskVO.setRemarks(channelUserVO.getRemarks());
		                   	                  	userRemarskVO.setMsisdn(channelUserVO.getMsisdn());
		                   	                  	userRemarskVO.setUserID(channelUserVO.getUserID());
		                   	                  	userRemarskVO.setUserType("CHANNEL");
		                   	                  	userRemarskVO.setModule(PretupsI.C2S_MODULE);
		                   	                  	changePinRemarks.add(userRemarskVO);
		                   	                 remarksCount=new UserWebDAO().insertEventRemark(con, changePinRemarks);
		                   		    		   }
		                      	           
		                                	if(remarksCount>0){
		                                    con.commit();
		                                    // set the argument which will be send to
		                                    // user as SMS part
		                                    final String[] arr = { messageArr[2] };
		                                    p_requestVO.setMessageArguments(arr);
		                                    p_requestVO.setMessageCode(PretupsErrorCodesI.PIN_CHNG_SUCCESS);
		                                    return;
		                   		    		   }
		                                     else{
		                                		 con.rollback();
		                                         throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_PIN_CHANGE_FAILED);
		                                	}
		                      	           }
		                                	else{
		                                		con.commit();
		                                        // set the argument which will be send to
		                                        // user as SMS part
		                                        final String[] arr = { messageArr[2] };
		                                        p_requestVO.setMessageArguments(arr);
		                                        p_requestVO.setMessageCode(PretupsErrorCodesI.PIN_CHNG_SUCCESS);
		                                        return;	
		                                	}
		                                } else {
		                                    con.rollback();
		                                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_PIN_CHANGE_FAILED);
		                                }
		                            } else {
		                                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_PIN_NEWCONFIRMNOTSAME);
		                            }
		                   
		                        } else {
		                            throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_PIN_OLDNEWSAME);
		                        }
		                    } else {
		                        final String[] arr = { ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_SMSPIN)) };
		                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CHNL_PIN_SAME_TO_DEFAULT_PIN, 0, arr, null);
		                    }
		                } else {
		                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_PIN_BLANK);
		                }
		            
		        
				}
				else{
					invalidCount++;
					
					if(invalidCount > invalidCountLimit){
						invalidCount = 1;
					}
					updateCnt = userOtpDAO.updateInvalidCountOfOtp(con, msisdn, invalidCount,currDate);
					if(invalidCount == invalidCountLimit){
						
						String blocktimeConverted = BTSLDateUtil.getTimeFromSeconds(blockTime);
						p_requestVO.setMessageArguments(new String[] {blocktimeConverted});
						p_requestVO.setMessageCode(PretupsErrorCodesI.OTP_MAX_INVALID);
						throw new BTSLBaseException("OtpValidationandPinUpdation", "process",
								PretupsErrorCodesI.OTP_MAX_INVALID, 0, null);
					}
					int attemptsLeft = invalidCountLimit-invalidCount;
					p_requestVO.setMessageArguments(new String[] {String.valueOf(attemptsLeft)});
					p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_OTP_ATTEMPT);
					throw new BTSLBaseException("OtpValidationandPinUpdation", "process",
							PretupsErrorCodesI.INVALID_OTP_ATTEMPT, 0, null);
				}//end of else of incorrect otp
				}//end of else of unbarred user
			}//end of else of inexpired otp
		}//end of main try
		catch (BTSLBaseException be) {
			p_requestVO.setSuccessTxn(false);
			try {
				if (mcomCon != null) {
					mcomCon.finalRollback();
				}
			} 
			
			catch (SQLException esql) {
				_log.error(METHOD_NAME,"SQLException : ", esql.getMessage());
			}
			_log.error("process", "BTSLBaseException " + be.getMessage());
			if (be.getMessageList() != null && be.getMessageList().size() > 0) {
				final String[] array = {
						BTSLUtil.getMessage(p_requestVO.getLocale(), (ArrayList) be.getMessageList()) };
				p_requestVO.setMessageArguments(array);
			}
			if (be.getArgs() != null) {
				p_requestVO.setMessageArguments(be.getArgs());
			}

			if (be.getMessageKey() != null) {
				p_requestVO.setMessageCode(be.getMessageKey());
			} else {
				p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER);
			}
			_log.errorTrace(METHOD_NAME, be);
			return;
		} catch (Exception e) {
			_log.error(METHOD_NAME, "Exception " + e);
			_log.errorTrace(METHOD_NAME, e);
			p_requestVO.setSuccessTxn(false);
			try {
				if (mcomCon != null) {
					mcomCon.finalRollback();
				}
			} 
			
			catch (SQLException esql) {
				_log.error(METHOD_NAME,"SQLException : ", esql.getMessage());
			}
			_log.error("process", "BTSLBaseException " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"OtpValidationandPinUpdation[process]", "", "", "", "Exception:" + e.getMessage());
			p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER);
			return;
		} finally {
			if (mcomCon != null) {
				mcomCon.close("OtpValidationandPinUpdation#process");
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) {
				_log.debug("process", " Exited ");
			}
		}
		
	}//end of process

}//end of class
