package com.btsl.pretups.channel.transfer.requesthandler;

import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.HashMap;


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
import com.btsl.pretups.channel.transfer.businesslogic.SendOtpForForgotPinRequestParentVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.parsers.MobileAppParsers;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

@Path("")
@Tag(name = "${dummy.name}", description = "${dummy.desc}")//@Api(value = "Channel User Forgot PIN")
public class SendOtpForForgotPinController implements ServiceKeywordControllerI {

	protected final Log _log = LogFactory.getLog(getClass().getName());

	private static final String PROCESS = "SendOtpForForgotPinController[process]";
	Connection con = null;
	MComConnectionI mcomCon = null;
	@Context
	private HttpServletRequest httpServletRequest;

	@POST
	@Path("/c2s-receiver/otpforforgotpin")
	@Consumes(value = MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	/*@ApiOperation(value = "Send Otp for Forgot Pin", response = PretupsResponse.class)
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${otpforforgotpin.summary}", description="${otpforforgotpin.description}",

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



	public PretupsResponse<JsonNode> processCP2PUserRequest(
			@Parameter(description = SwaggerAPIDescriptionI.OTP_FOR_FORGOT_PIN_CONTROLLER) SendOtpForForgotPinRequestParentVO sendOtpForForgotPinRequestParentVO)
			throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		PretupsResponse<JsonNode> response;
		RestReceiver restReceiver;
		RestReceiver.updateRequestIdChannel();
		final String requestIDStr = String.valueOf(RestReceiver.getRequestIdChannel());
		restReceiver = new RestReceiver();
		response = restReceiver.processCP2PRequestOperator(httpServletRequest,
				(JsonNode) (PretupsRestUtil.convertJSONToObject(
						PretupsRestUtil.convertObjectToJSONString(sendOtpForForgotPinRequestParentVO),
						new TypeReference<JsonNode>() {
						})),
				PretupsI.SEND_OTP_FOR_FORGOT_PIN, requestIDStr);
		return response;
	}

	private int recentOtpGeneration(ChannelUserVO p_userVO, int validity, int duration, int times) throws BTSLBaseException, Exception {

		final String methodName = "recentOtpGeneration";
		UserOtpDAO userOtpDAO = new UserOtpDAO();
		int otpResendDuration = 0;

		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			// calling function to generate otp and send it
			otpResendDuration = userOtpDAO.generateSendOTPForForgotPin(con, p_userVO, "",  validity, duration, times);

		} catch (Exception e) {
			throw new BTSLBaseException(this, methodName, "Error occured", "Exception " + e);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("SendOtpForForgotPinController");
				mcomCon = null;
			}
		}
		return otpResendDuration;

	}

	@Override
	public void process(RequestVO p_requestVO) {
		final String methodName = "process";
		HashMap responseMap = new HashMap();

		StringBuffer responseStr = new StringBuffer("");
		StringBuffer responseMessage = new StringBuffer("");
		ChannelUserVO p_userVO = null;
		try {
			int otpValidityPeriodInPreference = (Integer) PreferenceCache
					.getSystemPreferenceValue(PreferenceI.OTP_VALIDITY_PERIOD);
			int otpResendTImesInPreference = ((Integer) (PreferenceCache
					.getSystemPreferenceValue(PreferenceI.OTP_RESEND_TIMES)));
			int otpResendDurationInPreference = ((Integer) (PreferenceCache
					.getSystemPreferenceValue(PreferenceI.OTP_RESEND_DURATION)));
			boolean otpOnEmail= (boolean) (PreferenceCache
					.getSystemPreferenceValue(PreferenceI.TWO_FA_REQ_FOR_PIN));
			boolean otpOnSms = (boolean) (PreferenceCache
					.getSystemPreferenceValue(PreferenceI.OTP_ON_SMS));
			
			String validForMessage = BTSLDateUtil.getTimeFromSeconds((int) otpValidityPeriodInPreference);
			
			HashMap reqMap = p_requestVO.getRequestMap();
			p_userVO = (ChannelUserVO) (p_requestVO.getSenderVO());
           
			//getting time in format
			int otpResendDuration = recentOtpGeneration(p_userVO, otpValidityPeriodInPreference, otpResendDurationInPreference, otpResendTImesInPreference);
            String otpResendDurationMessage = BTSLDateUtil.getTimeFromSeconds(otpResendDuration);
             
			if ( p_userVO.getOTP() != null) {
				if(otpOnSms && otpOnEmail) {
					p_requestVO.setMessageCode(PretupsErrorCodesI.OTP_SENT_MESSAGE);
					String[] errorArg = { validForMessage };
					p_requestVO.setMessageArguments(errorArg);
				}else if(otpOnSms) {
					p_requestVO.setMessageCode(PretupsErrorCodesI.OTP_SENT_ON_SMS);
					String[] errorArg = { validForMessage };
					p_requestVO.setMessageArguments(errorArg);
				}else{
					p_requestVO.setMessageCode(PretupsErrorCodesI.OTP_SENT_ON_EMAIL);
					String[] errorArg = { validForMessage };
					p_requestVO.setMessageArguments(errorArg);
				}
				
				p_requestVO.setSuccessTxn(true);
				
				p_requestVO.setSenderReturnMessage("OTP successfully sent!");

				if ("MAPPGW".equalsIgnoreCase(p_requestVO.getRequestGatewayCode())) {

					String resType = null;
					resType = reqMap.get("TYPE") + "RES";
					responseStr.append("{ \"type\": \"" + resType + "\" ,");
					responseStr.append(" \"txnStatus\": \"" + PretupsI.TXN_STATUS_SUCCESS + "\" ,");
					responseStr.append(" \"date\": \"" + new SimpleDateFormat("dd/MM/YY").format(p_requestVO.getCreatedOn()) + "\" ,");
					responseStr.append(" \"message\": \"" +MobileAppParsers.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()) + "\" ,");
					responseStr.append(" \"validityPeriod\": \"" + validForMessage + "\" }");
					responseMap.put("RESPONSE", responseStr);
					p_requestVO.setResponseMap(responseMap);
				}

				else {
					responseMap.put("validityPeriod", validForMessage);
					p_requestVO.setResponseMap(responseMap);

				}
			} else {

				p_requestVO.setSuccessTxn(false);
				p_requestVO.setMessageCode(PretupsErrorCodesI.RESEND_OTP_REACHED_LIMIT);
					String[] errorArg = { Integer.toString(otpResendTImesInPreference), otpResendDurationMessage  };
					p_requestVO.setMessageArguments(errorArg);
			}

		} catch (BTSLBaseException be) {
			p_requestVO.setSuccessTxn(false);
			_log.error(methodName, "BTSLBaseException " + be.getMessage());
			_log.errorTrace(methodName, be);
			if (be.isKey()) {
				p_requestVO.setMessageCode(be.getMessageKey());
				p_requestVO.setMessageArguments(be.getArgs());
			} else {
				p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
				return;
			}
		} catch (Exception e) {
			p_requestVO.setSuccessTxn(false);
			_log.error(methodName, "Exception " + e.getMessage());
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					PROCESS, "", "", "", "Exception:" + e.getMessage());
			p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
			return;
		}

		finally {
			LogFactory.printLog(methodName, " Exited ", _log);
		}

	}

}
