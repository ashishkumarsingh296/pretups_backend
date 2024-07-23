package com.btsl.pretups.channel.transfer.requesthandler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.EMailSender;
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
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductVO;
import com.btsl.pretups.channel.receiver.RestReceiver;
import com.btsl.pretups.channel.transfer.businesslogic.C2CTrfReqMessage;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChnlToChnlTransferTransactionCntrl;
import com.btsl.pretups.channel.transfer.businesslogic.PaymentDetails;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.common.PretupsRestI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyBL;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyDAO;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyVO;
import com.btsl.pretups.loyaltymgmt.businesslogic.PromotionDetailsVO;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.BarredUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelSoSVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.util.AESEncryptionUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.KeyArgumentVO;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.OracleUtil;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.btsl.util.XmlTagValueConstant;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.restapi.user.service.C2CStockInitiateRequestVO;
import com.txn.pretups.channel.profile.businesslogic.TransferProfileTxnDAO;
import com.txn.pretups.user.businesslogic.ChannelUserTxnDAO;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;


@io.swagger.v3.oas.annotations.tags.Tag(name = "${C2CTrfInitiateController.name}", description = "${C2CTrfInitiateController.desc}")//@Api(tags = "C2C Receiver")
@RestController
@RequestMapping(value = "/v1/c2cReceiver")
public class C2CTrfInitiateController implements ServiceKeywordControllerI {
	private static Log _log = LogFactory.getLog(C2CTrfInitiateController.class.getName());
	private String _allowedSendMessGatw = null;
	private boolean _receiverMessageSendReq = false;
	private boolean _ussdReceiverMessageSendReq = false;

	/*static {
		final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
		try {
			_operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
		} catch (Exception e) {
			_log.errorTrace("static", e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					" C2CTrfInitiateController [initialize]", "", "", "",
					"Exception while loading the class at the call:" + e.getMessage());
		}
	}*/

	
	@PostMapping(value = "/c2ctrfini", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "C2C INITIATE", notes = ("Api Info:") + ("\n") +  ("1. Supported File formate: jpg, png, pdf."),
	response = PretupsResponse.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = PretupsResponse.class),
			@ApiResponse(code = 201, message = "Created"), @ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 404, message = "Not Found") })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${c2ctrfini.summary}", description="${c2ctrfini.description}",

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




	public PretupsResponse<JsonNode> processCP2PUserRequest(HttpServletRequest httpServletRequest,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = SwaggerAPIDescriptionI.C2C_INITIATE) @RequestBody C2CStockInitiateRequestVO c2CStockInitiateRequestVO, HttpServletResponse response1)
			throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		final String methodName = "processCP2PUserRequest_C2CTrfInitiateController";
		PretupsResponse<JsonNode> response;
		RestReceiver restReceiver;
		C2CFileUploadService c2CFileUploadService=  new C2CFileUploadService(); 
		RestReceiver.updateRequestIdChannel();
		final String requestIDStr = String.valueOf(RestReceiver.getRequestIdChannel());
		restReceiver = new RestReceiver();
		
		OAuthUser oAuthUser= null;
		OAuthUserData oAuthUserData =null;
		
		ChannelUserVO channelUserVO = null;
		UserPhoneVO userPhoneVO = null;
		UserDAO userDao = new UserDAO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		
		
		try {
			/*
			 * Authentication
			 * 
			 * @throws BTSLBaseException
			 */
			oAuthUser = new OAuthUser();
			oAuthUserData =new OAuthUserData();
			oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers,response1);
			c2CStockInitiateRequestVO.setServicePort(oAuthUser.getServicePort());
			c2CStockInitiateRequestVO.setReqGatewayCode(oAuthUser.getReqGatewayCode());
			c2CStockInitiateRequestVO.setReqGatewayLoginId(oAuthUser.getReqGatewayLoginId());
			c2CStockInitiateRequestVO.setReqGatewayPassword(oAuthUser.getReqGatewayPassword());
			c2CStockInitiateRequestVO.setReqGatewayType(oAuthUser.getReqGatewayType());
			c2CStockInitiateRequestVO.setSourceType(oAuthUser.getSourceType());
			
			c2CStockInitiateRequestVO.getData().setLoginid( oAuthUser.getData().getLoginid());
			c2CStockInitiateRequestVO.getData().setPassword( oAuthUser.getData().getPassword());
			//setting password in requestVO from oAuthUser - removed
			//c2CStockInitiateRequestVO.getData().setPassword(oAuthUser.getData().getPassword());
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			channelUserVO = userDao.loadAllUserDetailsByLoginID(con, oAuthUser.getData().getLoginid());
			userPhoneVO = userDao.loadUserPhoneVO(con, channelUserVO.getUserID());
			if(!c2CStockInitiateRequestVO.getData().getPin().isEmpty()) {
				//Decrypting pin and setting back in requestVO
				String encryptedPin = c2CStockInitiateRequestVO.getData().getPin();
				String decryptedPin = AESEncryptionUtil.aesDecryptor(encryptedPin, Constants.A_KEY);
				c2CStockInitiateRequestVO.getData().setPin(decryptedPin);
			}
			if(userPhoneVO != null) {
				if(c2CStockInitiateRequestVO.getData().getPin().isEmpty() && (!userPhoneVO.isPinRequiredBool())) {
					c2CStockInitiateRequestVO.getData().setPin(oAuthUser.getData().getPin());
				}
			}
			
			
//			OAuthenticationUtil.validateTokenApi(headers);
			
			
			String jsonString = BTSLUtil.appendWebApiCall(PretupsRestUtil.convertObjectToJSONString(c2CStockInitiateRequestVO));

			response = restReceiver.processCP2PRequestOperator(httpServletRequest,
					(JsonNode) (PretupsRestUtil.convertJSONToObject(
							jsonString,
							new TypeReference<JsonNode>() {
							})),
					PretupsRestI.C2CINITIATE, requestIDStr);
			 if(response.getStatusCode()!=200)
	        	   response1.setStatus(HttpStatus.SC_BAD_REQUEST);
	        if(response!=null && response.getDataObject()!=null && response.getDataObject().get("txnstatus") != null){
	        	if(!response.getDataObject().get("txnstatus").textValue().equals("200"))
	        		response1.setStatus(HttpStatus.SC_BAD_REQUEST);
	        }
		
			return response;
		} catch (BTSLBaseException be) {
			PretupsResponse<JsonNode> baseResponse = new PretupsResponse<JsonNode>();

			_log.error(methodName, "BTSLBaseException " + be.getMessage());
			_log.errorTrace(methodName, be);
			 if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
	           	 response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
	           }
	            else{
	            response1.setStatus(HttpStatus.SC_BAD_REQUEST);
	            }
			baseResponse.setMessageCode(be.getMessageKey());
			String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
			String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
			Locale locale = new Locale(defaultLanguage, defaultCountry);
			String resmsg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			baseResponse.setStatusCode(be.getErrorCode());
			baseResponse.setMessage(resmsg);
			return baseResponse;

		} catch (Exception e) {
			PretupsResponse<JsonNode> baseResponse = new PretupsResponse<JsonNode>();
			_log.error(methodName, "Exception " + e.getMessage());
			_log.errorTrace(methodName, e);
			baseResponse.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
			String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
			String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
			Locale locale = new Locale(defaultLanguage, defaultCountry);
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.REQ_NOT_PROCESS, null);
			baseResponse.setStatusCode(PretupsI.UNABLE_TO_PROCESS_REQUEST);
			baseResponse.setMessage(resmsg);
			  response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			return baseResponse;
		}

		finally {
			if (mcomCon != null) {
				mcomCon.close(methodName);
				mcomCon = null;
			}
			LogFactory.printLog(methodName, " Exited ", _log);
		}

	}

	public void process(RequestVO p_requestVO) {

		final String METHOD_NAME = "process";
		if (_log.isDebugEnabled()) {
			_log.debug("process", "Entered p_requestVO: " + p_requestVO);
		}
		String _serviceType = "";
		ChannelUserTxnDAO channelUserTxnDAO = null;
		final Date curDate = new Date();
		Connection con = null;
		MComConnectionI mcomCon = null;
		ChannelUserVO senderVO = (ChannelUserVO) p_requestVO.getSenderVO();

		final HashMap requestMap = p_requestVO.getRequestMap();
		final C2CFileUploadService c2CFileUploadService = new C2CFileUploadService();
		final C2CFileUploadVO c2cFileUploadVO = new C2CFileUploadVO();
		OperatorUtilI _operatorUtil = null;
		try {
			final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
	        try {
	            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
	        } catch (Exception e) {
	            _log.errorTrace("static", e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[initialize]", "", "", "",
	                "Exception while loading the class at the call:" + e.getMessage());
	        }
			channelUserTxnDAO = new ChannelUserTxnDAO();
			final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
			ChannelUserVO receiverChannelUserVO = null;
			String receiverMsisdn = null;
			if (PretupsI.REQUEST_SOURCE_TYPE_REST.equals(p_requestVO.getRequestGatewayCode()) || PretupsI.REQUEST_SOURCE_TYPE_WEB.equals(p_requestVO.getRequestGatewayCode())) {
				Gson gson = new Gson();
				C2CTrfReqMessage resMsg = gson.fromJson(p_requestVO.getRequestMessage(), C2CTrfReqMessage.class);
				
				PaymentDetails[] paymentDetails = resMsg.getPaymentdetails();
				if (!BTSLUtil.isNullObject(paymentDetails)) {
					p_requestVO.setPaymentDate(paymentDetails[0].getPaymentdate());
					p_requestVO.setPaymentInstNumber(paymentDetails[0].getPaymentinstnumber());
					p_requestVO.setPaymentType(paymentDetails[0].getPaymenttype());
				}

				if (!BTSLUtil.isPaymentTypeValid(p_requestVO.getPaymentType())) {
					p_requestVO.setMessageArguments(new String[] { XmlTagValueConstant.TAG_PAYMENTTYPE });
					p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_PAYMENT_INST_TYPE);
					throw new BTSLBaseException("C2CTrfInitiateController", "process",
							PretupsErrorCodesI.INVALID_PAYMENT_INST_TYPE, 0, null);
				}

				if (!PretupsI.PAYMENT_INSTRUMENT_TYPE_CASH.equalsIgnoreCase(p_requestVO.getPaymentType())) {
					if (BTSLUtil.isNullString(p_requestVO.getPaymentInstNumber())) {
						p_requestVO.setMessageArguments(new String[] { XmlTagValueConstant.TAG_PAYMENTINSTNUMBER });
						p_requestVO.setMessageCode(PretupsErrorCodesI.EXTSYS_BLANK);
						throw new BTSLBaseException("C2CTrfInitiateController", "process",
								PretupsErrorCodesI.EXTSYS_BLANK, 0, null);
					}
				}
				

				//added by md.sohail
				//for FileUpload functionality
				c2cFileUploadVO.setFileAttachment(resMsg.getFileAttachment());
				c2cFileUploadVO.setFileName(resMsg.getFileName());
				c2cFileUploadVO.setFileType(resMsg.getFileType());
				c2cFileUploadVO.setFileUploaded(resMsg.getFileUploaded());
			}
			if (PretupsI.REQUEST_SOURCE_TYPE_USSD.equals(p_requestVO.getRequestGatewayCode())) {
				receiverMsisdn = p_requestVO.getRequestMessageArray()[1];
				p_requestVO.setReceiverMsisdn(p_requestVO.getRequestMessageArray()[1]);
			} else if (PretupsI.MOBILE_APP_GATEWAY.equals(p_requestVO.getRequestGatewayCode())) {
				/*String paymentInstNum = null;
				String paymentInstDate = null;
				String paymentInstCode = null;
				String remarks = null;
				String refNum = null; */
				receiverMsisdn = p_requestVO.getRequestMessageArray()[1];
				p_requestVO.setReceiverMsisdn(p_requestVO.getRequestMessageArray()[1]);

				/* paymentInstNum = (String) requestMap.get("PAYMENTINSTNUM");
				paymentInstDate = (String) requestMap.get("PAYMENTDATE");
				paymentInstCode = (String) requestMap.get("PAYMENTINSTCODE");
				remarks = (String) requestMap.get("REMARKS");
				refNum = (String) requestMap.get("REFNUM");
				if (!BTSLUtil.isNullString(refNum)) {
					p_requestVO.setReferenceNumber(refNum);
				}
				if (BTSLUtil.isNullString(remarks)) {
					p_requestVO.setMessageArguments(new String[] { "REMARKS" });
					p_requestVO.setMessageCode(PretupsErrorCodesI.EXTSYS_BLANK);
					throw new BTSLBaseException("C2CTrfInitiateController", "process", PretupsErrorCodesI.EXTSYS_BLANK,
							0, null);
				}
				if (BTSLUtil.isNullString(paymentInstDate)) {
					p_requestVO.setMessageArguments(new String[] { "PAYMENTDATE" });
					p_requestVO.setMessageCode(PretupsErrorCodesI.EXTSYS_BLANK);
					throw new BTSLBaseException("C2CTrfInitiateController", "process", PretupsErrorCodesI.EXTSYS_BLANK,
							0, null);
				}
				if (BTSLUtil.isNullString(paymentInstCode)) {
					p_requestVO.setMessageArguments(new String[] { "PAYMENTINSTCODE" });
					p_requestVO.setMessageCode(PretupsErrorCodesI.EXTSYS_BLANK);
					throw new BTSLBaseException("C2CTrfInitiateController", "process", PretupsErrorCodesI.EXTSYS_BLANK,
							0, null);
				}
				p_requestVO.setPaymentDate(paymentInstDate);
				p_requestVO.setPaymentInstNumber(paymentInstNum);
				p_requestVO.setPaymentType(paymentInstCode);

				if (!BTSLUtil.isPaymentTypeValid(p_requestVO.getPaymentType())) {
					p_requestVO.setMessageArguments(new String[] { XmlTagValueConstant.TAG_PAYMENTTYPE });
					p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_PAYMENT_INST_TYPE);
					throw new BTSLBaseException("C2CTrfInitiateController", "process",
							PretupsErrorCodesI.INVALID_PAYMENT_INST_TYPE, 0, null);
				}
				if (!BTSLUtil.isNullString(paymentInstCode)
						&& !(PretupsI.PAYMENT_INSTRUMENT_TYPE_CASH.equalsIgnoreCase(paymentInstCode))) {
					if (BTSLUtil.isNullString(paymentInstNum)) {
						p_requestVO.setMessageArguments(new String[] { XmlTagValueConstant.TAG_PAYMENTINSTNUMBER });
						p_requestVO.setMessageCode(PretupsErrorCodesI.EXTSYS_BLANK);
						throw new BTSLBaseException("C2CTrfInitiateController", "process",
								PretupsErrorCodesI.EXTSYS_BLANK, 0, null);
					}
				}
				*/

			} else {
				if (PretupsI.GATEWAY_TYPE_SMSC.equals(p_requestVO.getRequestGatewayCode())) {
					p_requestVO.setReceiverMsisdn(p_requestVO.getRequestMessageArray()[1]);
				} else {
					if (BTSLUtil.isNullString(p_requestVO.getReceiverMsisdn())
							&& BTSLUtil.isNullString(p_requestVO.getReceiverExtCode())
							&& BTSLUtil.isNullString(p_requestVO.getReceiverLoginID())) {
						throw new BTSLBaseException("C2CTrfInitiateController", "process",
								PretupsErrorCodesI.INVALID_RECIEVER_CREDENTIALS, 0, null);
					}
					receiverMsisdn = p_requestVO.getReceiverMsisdn();
				}
			}

			UserPhoneVO userPhoneVO = null;
			if (!senderVO.isStaffUser()) {
				userPhoneVO = senderVO.getUserPhoneVO();
			} else {
				userPhoneVO = senderVO.getStaffUserDetails().getUserPhoneVO();
			}
			if (_log.isDebugEnabled()) {
				_log.debug("process", "Entered Sender VO: " + senderVO);
			}
			if (senderVO != null && PretupsI.USER_TRANSFER_OUT_STATUS_SUSPEND.equals(senderVO.getOutSuspened())) {
				p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_OUT_SUSPENDED);
				throw new BTSLBaseException("C2CTrfInitiateController", "process",
						PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_OUT_SUSPENDED);
			}
			_serviceType = p_requestVO.getServiceType();
			_ussdReceiverMessageSendReq = ((Boolean) PreferenceCache
					.getControlPreference(PreferenceI.USSD_REC_MSG_SEND_ALLOW, senderVO.getNetworkCode(), _serviceType))
							.booleanValue();
			_receiverMessageSendReq = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.REC_MSG_SEND_ALLOW,
					senderVO.getNetworkCode(), _serviceType)).booleanValue();

			channelUserTxnDAO = new ChannelUserTxnDAO();

			boolean isUserDetailLoad = false;
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			UserPhoneVO PrimaryPhoneVO_R = null;
			final UserDAO userDAO = new UserDAO();
			UserPhoneVO phoneVO = null;
			boolean receiverAllowed = false;
			UserStatusVO receiverStatusVO = null;

			if (!BTSLUtil.isNullString(p_requestVO.getReceiverMsisdn())) {
				receiverChannelUserVO = channelUserDAO.loadChannelUserDetails(con, p_requestVO.getReceiverMsisdn());
				if (receiverChannelUserVO == null) {
					throw new BTSLBaseException("C2CTrfInitiateController", "process",
							PretupsErrorCodesI.EXT_XML_ERROR_INVALID_MSISDN, 0, null);
				}
			}
			if (!BTSLUtil.isNullString(p_requestVO.getReceiverExtCode())) {
				receiverChannelUserVO = channelUserTxnDAO.loadChannelUserDetailsForTransferIfReqExtgw(con,
						p_requestVO.getReceiverExtCode(), null, curDate);
				if (receiverChannelUserVO == null) {
					throw new BTSLBaseException("C2CTrfInitiateController", "process",
							PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE, 0, null);
				}

				isUserDetailLoad = true;

			} else if (!BTSLUtil.isNullString(p_requestVO.getReceiverLoginID())) {
				receiverChannelUserVO = channelUserTxnDAO.loadChannelUserDetailsForTransferIfReqExtgw(con, null,
						p_requestVO.getReceiverLoginID(), curDate);
				if (receiverChannelUserVO == null) {
					throw new BTSLBaseException("C2CTrfInitiateController", "process",
							PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID, 0, null);
				}

				isUserDetailLoad = true;
			}

			if (!(receiverChannelUserVO == null) && isUserDetailLoad) {
				if (!BTSLUtil.isNullString(p_requestVO.getReceiverExtCode())) {
					if (!p_requestVO.getReceiverExtCode().equalsIgnoreCase(receiverChannelUserVO.getExternalCode())) {
						throw new BTSLBaseException("C2CTrfInitiateController", "process",
								PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE, 0, null);
					}
				}
				if (!BTSLUtil.isNullString(p_requestVO.getReceiverLoginID())) {
					if (!p_requestVO.getReceiverLoginID().equalsIgnoreCase(receiverChannelUserVO.getLoginID())) {

						throw new BTSLBaseException("C2CTrfInitiateController", "process",
								PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID, 0, null);
					}
				}
				if (!BTSLUtil.isNullString(p_requestVO.getReceiverMsisdn())) {
					if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue()) {
						phoneVO = userDAO.loadUserAnyPhoneVO(con, p_requestVO.getReceiverMsisdn());
						if (phoneVO != null && ((receiverChannelUserVO.getUserID()).equals(phoneVO.getUserId()))) {
							if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MESSAGE_TO_PRIMARY_REQUIRED)).booleanValue()
									&& ("N".equalsIgnoreCase(phoneVO.getPrimaryNumber()))) {
								PrimaryPhoneVO_R = userDAO.loadUserAnyPhoneVO(con, receiverChannelUserVO.getMsisdn());
							}
							receiverChannelUserVO.setPrimaryMsisdn(receiverChannelUserVO.getMsisdn());
							receiverChannelUserVO.setMsisdn(p_requestVO.getReceiverMsisdn());
						} else {
							throw new BTSLBaseException("C2CTrfInitiateController", "process",
									PretupsErrorCodesI.EXT_XML_ERROR_INVALID_MSISDN, 0, null);
						}
					} else if (!p_requestVO.getReceiverMsisdn().equalsIgnoreCase(receiverChannelUserVO.getMsisdn())) {
						throw new BTSLBaseException("C2CTrfInitiateController", "process",
								PretupsErrorCodesI.EXT_XML_ERROR_INVALID_MSISDN, 0, null);
					}
				}

				if (BTSLUtil.isNullString(p_requestVO.getReceiverMsisdn())
						&& BTSLUtil.isNullString((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR))) {
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
				} else {
					final String[] mesgArr = p_requestVO.getRequestMessageArray();
					mesgArr[1] = receiverChannelUserVO.getMsisdn();
					p_requestVO.setRequestMessageArray(mesgArr);
				}
			}

			final String messageArr[] = p_requestVO.getRequestMessageArray();
			final int messageLen = messageArr.length;

			if (messageArr.length < 2) {
				throw new BTSLBaseException("C2CTrfInitiateController", "process",
						PretupsErrorCodesI.ERROR_INVALID_REQUESTFORMAT, 0,
						new String[] { p_requestVO.getActualMessageFormat() }, null);
			}

			if (!BTSLUtil.isNumeric(messageArr[1])) {
				p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_USER_CODE_FORMAT);
				throw new BTSLBaseException("C2CTrfInitiateController", "process",
						PretupsErrorCodesI.ERROR_INVALID_USER_CODE_FORMAT);
			}

			final String productArray[] = ChannelTransferBL.validateUserProductsFormatForSMS(messageArr, p_requestVO);

			final int msgLen = messageArr.length;
			if (userPhoneVO.getPinRequired().equals(PretupsI.YES)) {
				try {
					ChannelUserBL.validatePIN(con, senderVO, messageArr[msgLen - 1]);
				} catch (BTSLBaseException be) {
					if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN))
							|| (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
						OracleUtil.commit(con);
					}
					throw be;
				}
			}

			String receiverUserCode = receiverChannelUserVO.getMsisdn();

			receiverUserCode = _operatorUtil.addRemoveDigitsFromMSISDN(PretupsBL.getFilteredMSISDN(receiverUserCode));
			if (!BTSLUtil.isValidMSISDN(receiverUserCode)) {
				p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_REC_USERCODE);
				throw new BTSLBaseException("C2CTrfInitiateController", "process",
						PretupsErrorCodesI.ERROR_INVALID_REC_USERCODE);
			}
			final String msisdnPrefix = PretupsBL.getMSISDNPrefix(receiverUserCode);

			final NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
			if (networkPrefixVO == null) {
				throw new BTSLBaseException("C2CTrfInitiateController", "process",
						PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_UNSUPPORTED_NETWORK, 0,
						new String[] { receiverUserCode }, null);
			}

			if (phoneVO == null) {
				phoneVO = userDAO.loadUserAnyPhoneVO(con, receiverUserCode);
				senderVO.setUserPhoneVO(phoneVO);
			}
			if (!isUserDetailLoad) {
				if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue()) {
					receiverChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, receiverUserCode,
							true, curDate, false);
				} else {
					if (phoneVO != null && !("Y".equalsIgnoreCase(phoneVO.getPrimaryNumber()))) {
						receiverChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con,
								receiverChannelUserVO.getUserID(), false, curDate, false);
						if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MESSAGE_TO_PRIMARY_REQUIRED)).booleanValue()) {
							PrimaryPhoneVO_R = userDAO.loadUserAnyPhoneVO(con, receiverChannelUserVO.getMsisdn());
						}
						receiverChannelUserVO.setPrimaryMsisdn(receiverChannelUserVO.getMsisdn());
						receiverChannelUserVO.setMsisdn(receiverUserCode);
					} else {
						receiverChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, receiverUserCode,
								true, curDate, false);
					}
				}
			}

			// senderVO = channelUserTxnDAO.loadChannelUserDetailsForTransferIfReqExtgw(con,
			// null, senderVO.getLoginID(), curDate);

			// Exchange the VO's
			ChannelUserVO exchangeVO = senderVO;
			senderVO = receiverChannelUserVO;
			receiverChannelUserVO = exchangeVO;
			final BarredUserDAO barredUserDAO = new BarredUserDAO();
			
			String requesterMsisdn = p_requestVO.getRequestMSISDN();
			
			if (barredUserDAO.isExists(con, PretupsI.C2S_MODULE, networkPrefixVO.getNetworkCode(), requesterMsisdn,
					PretupsI.USER_TYPE_RECEIVER, null)) {
				throw new BTSLBaseException("C2CTrfInitiateController", "process",
						PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_RECEIVER_BAR, 0,
						new String[] { requesterMsisdn }, null);
			}

			// 1. is user exist or not
			// 2. is user active or not
			// 3. is there any applicable commission profile with user or not
			// 4. is user is IN suspended or not if suspended then show error
			// message
			// /

			// Meditel changes.....checking for receiver allowed
			if (receiverChannelUserVO != null) {
				receiverAllowed = false;
				receiverStatusVO = (UserStatusVO) UserStatusCache.getObject(receiverChannelUserVO.getNetworkID(),
						receiverChannelUserVO.getCategoryCode(), receiverChannelUserVO.getUserType(),
						p_requestVO.getRequestGatewayType());
				if (receiverStatusVO != null) {
					final String receiverStatusAllowed = receiverStatusVO.getUserReceiverAllowed();
					final String status[] = receiverStatusAllowed.split(",");
					for (int i = 0; i < status.length; i++) {
						if (status[i].equals(receiverChannelUserVO.getStatus())) {
							receiverAllowed = true;
						}
					}
				}
			}
			//

			String args[] = { receiverUserCode };
			if (receiverChannelUserVO == null) {
				p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_NOT_EXIST);
				p_requestVO.setMessageArguments(args);
				throw new BTSLBaseException("C2CTrfInitiateController", "process",
						PretupsErrorCodesI.ERROR_USER_NOT_EXIST, 0, args, null);
			} else if (receiverChannelUserVO.getInSuspend() != null
					&& PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(receiverChannelUserVO.getInSuspend())) {
				p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_IN_SUSPENDED);
				p_requestVO.setMessageArguments(args);
				throw new BTSLBaseException("C2CTrfInitiateController", "process",
						PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_IN_SUSPENDED, 0, args, null);
			} else if (receiverStatusVO == null) {
				throw new BTSLBaseException("C2CTrfInitiateController", "process",
						PretupsErrorCodesI.ERROR_USERSTATUS_NOTCONFIGURED);
			} else if (!receiverAllowed) {
				p_requestVO.setMessageCode(PretupsErrorCodesI.CHNL_ERROR_RECEIVER_NOTALLOWED);
				p_requestVO.setMessageArguments(args);
				throw new BTSLBaseException("C2CTrfInitiateController", "process",
						PretupsErrorCodesI.CHNL_ERROR_RECEIVER_NOTALLOWED, 0, args, null);
			} else if (receiverChannelUserVO.getCommissionProfileApplicableFrom().after(curDate)) {
				p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_COMMISSION_PROFILE_NOT_APPLICABLE);
				p_requestVO.setMessageArguments(args);
				throw new BTSLBaseException("C2CTrfInitiateController", "process",
						PretupsErrorCodesI.ERROR_USER_COMMISSION_PROFILE_NOT_APPLICABLE, 0, args, null);
			}

			// /
			// Now validate the sender and receiver as
			// 1. status of commission profile of both users
			// 2. status of the transfer profile of both users
			// 3. transaction allowed based on the transfer rule
			// 4. validation of the user based on the transfer rule
			// 5. check that transaction is outSide hierarchy or not.
			// /
			// /
			// set the commission profile suspended messages
			// /
			// ChangeID=LOCALEMASTER
			// Check which language message to be sent, from the locale master
			// table for the perticuler locale.
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
			// valiadting sender and receiver with the transfer rule in this
			// method we are passing various parameters as
			// con - connection for the database access
			// senderVO - VO contains sender's information
			// receiverChannelUserVO - VO contains receiver's information
			// true (p_isUserCode) - To indicate that validation will be done on
			// the user code not on the user name for message display.
			// null (p_forwardPath) - for forwarding the message only in the
			// case of WEB so null is here
			// false (p_isFromWeb) - To indicate that request is not from WEB
			// i.e. form SMS/USSD.
			// txnSubType - Type of the transaction as TRANSFER, RETURN or
			// WITHDRAW. TRANSFER is here
			// This method return boolean value to indicate that whether this is
			// the controlled TXN or uncontroll TXN.

			final boolean isOutsideHierarchy = ChannelTransferBL.validateSenderAndReceiverWithXfrRule(con, senderVO,
					receiverChannelUserVO, true, null, false, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
			// /
			// Now validate the requested product as
			// 1. existance in the system
			// 2. mapping with the network
			// 3. having balance >0
			// 4. Applicable commission profile version
			// 5. product associated with the commisssion profile
			// 6. product associated with the transfer rule.
			// 7. requested quantity with the minimum transfer value
			// 8. requested quantity with the maximum transfer value
			// 9. requested quantity with the multiple of factor
			// 10. user balance with the requested quantity
			// /
			final ArrayList productList = ChannelTransferBL.validateReqstProdsWithDefinedProdsForXFR(con, senderVO,
					productArray, curDate, p_requestVO.getLocale(), receiverChannelUserVO.getCommissionProfileSetID());

			// /
			// Now load the thresholds VO to validate the user balance (after
			// subtraction of the requested quantity)
			// with the thresholds as
			// 1. first check it with the MAXIMUM PERCENTAGE ALLOWED
			// 2. if not fail at previous point then check it with the MINIMUM
			// RESEDUAL BALANCE
			// /
			final TransferProfileTxnDAO transferProfileTxnDAO = new TransferProfileTxnDAO();
			final ArrayList profileProductList = transferProfileTxnDAO.loadTrfProfileProductWithCntrlValue(con,
					senderVO.getTransferProfileID());
			TransferProfileProductVO transferProfileProductVO = null;
			final ArrayList minProdResidualbalanceList = new ArrayList();
			KeyArgumentVO keyArgumentVO = null;
			ChannelTransferItemsVO channelTransferItemsVO = null;
			int maxAllowPct = 0;
			long maxAllowBalance = 0;
			for (int i = 0, k = profileProductList.size(); i < k; i++) {
				transferProfileProductVO = (TransferProfileProductVO) profileProductList.get(i);
				for (int m = 0, n = productList.size(); m < n; m++) {
					channelTransferItemsVO = (ChannelTransferItemsVO) productList.get(m);
					if (transferProfileProductVO.getProductCode().equals(channelTransferItemsVO.getProductCode())) {
						maxAllowPct = transferProfileProductVO.getAllowedMaxPercentageInt();
						maxAllowBalance = (channelTransferItemsVO.getBalance() * maxAllowPct) / 100;
						if (maxAllowBalance < channelTransferItemsVO.getRequiredQuantity()) {
							keyArgumentVO = new KeyArgumentVO();
							keyArgumentVO.setKey(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_ALLOWMAXPCT);
							args = new String[] { String.valueOf(maxAllowPct), productArray[m + 1],
									channelTransferItemsVO.getRequestedQuantity() };
							keyArgumentVO.setArguments(args);
							minProdResidualbalanceList.add(keyArgumentVO);
						} else if (transferProfileProductVO
								.getMinResidualBalanceAsLong() > (channelTransferItemsVO.getBalance()
										- channelTransferItemsVO.getRequiredQuantity())) {
							keyArgumentVO = new KeyArgumentVO();
							keyArgumentVO.setKey(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_RESIDUAL_BALANCE_LESS);
							args = new String[] { transferProfileProductVO.getMinBalance(), productArray[m + 1],
									channelTransferItemsVO.getRequestedQuantity() };
							keyArgumentVO.setArguments(args);
							minProdResidualbalanceList.add(keyArgumentVO);
						}
						break;
					}
				} // end of for
			}
			if (minProdResidualbalanceList.size() > 0) {
				final String[] array = { BTSLUtil.getMessage(p_requestVO.getLocale(), minProdResidualbalanceList) };
				p_requestVO.setMessageArguments(array);
				p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_RESIDUAL_BALANCE_LESS_MSG);
				throw new BTSLBaseException(this, "processTransfer",
						PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_RESIDUAL_BALANCE_LESS_MSG, 0, array, null);
			}

			UserPhoneVO primaryPhoneVO_S = null;
			if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue()) {
				if (!(senderVO.getMsisdn()).equalsIgnoreCase(p_requestVO.getFilteredMSISDN())) {
					senderVO.setPrimaryMsisdn(senderVO.getMsisdn());
					senderVO.setMsisdn(p_requestVO.getFilteredMSISDN());
					if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MESSAGE_TO_PRIMARY_REQUIRED)).booleanValue()) {
						primaryPhoneVO_S = userDAO.loadUserAnyPhoneVO(con, senderVO.getPrimaryMsisdn());
					}
				}
				receiverChannelUserVO.setUserCode(receiverUserCode);
			}
			ChannelTransferVO channelTransferVO = this.prepareTransferProfileVO(senderVO, receiverChannelUserVO,
					productList, curDate);
			channelTransferVO.setActiveUserId(receiverChannelUserVO.getActiveUserID());
			channelTransferVO.setChannelTransferitemsVOList(productList);
			channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
			channelTransferVO.setToUserID(receiverChannelUserVO.getUserID());
			channelTransferVO.setOtfFlag(true);
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OTH_COM_CHNL))).booleanValue()){
			channelTransferVO.setRequestGatewayCode(p_requestVO.getRequestGatewayCode());
			channelTransferVO.setRequestGatewayType(p_requestVO.getRequestGatewayType());
			channelTransferVO.setToUserMsisdn(receiverChannelUserVO.getMsisdn());
			}
			if (_log.isDebugEnabled()) {
				_log.debug("process", "Calculate Tax of products Start ");
			}

			ChannelTransferBL.loadAndCalculateTaxOnProducts(con, receiverChannelUserVO.getCommissionProfileSetID(),
					receiverChannelUserVO.getCommissionProfileSetVersion(), channelTransferVO, false, null,
					PretupsI.TRANSFER_TYPE_C2C);
			
			this.setAmountsAfterCalculation(channelTransferVO, channelTransferVO.getChannelTransferitemsVOList(), receiverChannelUserVO);

			if (isOutsideHierarchy) {
				channelTransferVO.setControlTransfer(PretupsI.NO);
			} else {
				channelTransferVO.setControlTransfer(PretupsI.YES);
			}

			channelTransferVO.setSource(p_requestVO.getSourceType());
			channelTransferVO.setRequestGatewayCode(p_requestVO.getRequestGatewayCode());
			channelTransferVO.setRequestGatewayType(p_requestVO.getRequestGatewayType());
			channelTransferVO.setReferenceNum(p_requestVO.getReferenceNumber());
			channelTransferVO.setCellId(p_requestVO.getCellId());
			channelTransferVO.setSwitchId(p_requestVO.getSwitchId());
		      String recLastC2CId="";
	            String recLastC2CAmount="";
	            String recLastC2CSenderMSISDN="";
	            String recLastC2CPostStock="";
	            String recLastC2CProductName="";
	            Date recLastC2CTime=null ;
	            
	            boolean lastInFoFlag=false;
	            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LAST_C2C_ENQ_MSG_REQ))).booleanValue()){
	                ArrayList transfersList =null;
	    	        
	            try{
	            	int xLastTxn =1;
	            	String serviceType="C2C:T";
	            	int noDays=((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LAST_X_TRF_DAYS_NO))).intValue();		//fetch only data for last these days.
	            	ChannelTransferDAO channelTransferDAO=new ChannelTransferDAO();
	            	transfersList=channelTransferDAO.loadLastXTransfersForReceiver(con,receiverChannelUserVO.getUserID(),xLastTxn, serviceType, noDays);
	            	if(transfersList!=null && transfersList.size()>0){
		            	Iterator transfersListIte=transfersList.iterator();
		            	while (transfersListIte.hasNext()) {
		            		C2STransferVO p_c2sTransferVO = (C2STransferVO) transfersListIte.next();
		            		recLastC2CId=p_c2sTransferVO.getTransferID();
		            		recLastC2CAmount=Double.toString(p_c2sTransferVO.getQuantity()/100);
		            		recLastC2CSenderMSISDN=p_c2sTransferVO.getSenderMsisdn();
		            		recLastC2CTime=p_c2sTransferVO.getTransferDate();
		            		recLastC2CProductName=p_c2sTransferVO.getProductName();
		            	}
		            	lastInFoFlag=true;
	            	}
	            }catch (Exception e) {
	            	lastInFoFlag=false;
	            	_log.error("process", "Not able to fetch info Exception: "+e.getMessage());
				}
			}
			if (_log.isDebugEnabled()) {
				_log.debug("process", "Start Transfer Process ");
			}

			final Boolean isTagReq = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_TRANSFERS_INFO_REQUIRED))).booleanValue();
			if (requestMap != null && isTagReq) {
				final String remarks = (String) requestMap.get("REMARKS");
				channelTransferVO.setChannelRemarks(remarks);
				final String info1 = (String) requestMap.get("INFO1");
				final String info2 = (String) requestMap.get("INFO2");
				channelTransferVO.setInfo1(info1);
				channelTransferVO.setInfo2(info2);
			}
			
			
			
			//added by  md.sohail
			//Added for file upload functionality
			if (PretupsI.REQUEST_SOURCE_TYPE_REST.equals(p_requestVO.getRequestGatewayCode()) || PretupsI.REQUEST_SOURCE_TYPE_WEB.equals(p_requestVO.getRequestGatewayCode())) {
				c2CFileUploadService.uploadFileToServer( c2cFileUploadVO, channelTransferVO);
			}
			
			
			
			int level = ((Integer) PreferenceCache.getControlPreference(PreferenceI.MAX_APPROVAL_LEVEL_C2C_INITIATE,
					senderVO.getNetworkID(), senderVO.getCategoryCode())).intValue();
			int updateCount = 0;

			if (PretupsI.REQUEST_SOURCE_TYPE_EXTGW.equals(channelTransferVO.getRequestGatewayCode())
					|| PretupsI.REQUEST_SOURCE_TYPE_REST.equals(p_requestVO.getRequestGatewayCode())
					|| PretupsI.REQUEST_SOURCE_TYPE_WEB.equals(p_requestVO.getRequestGatewayCode())) {
				if (p_requestVO.getPaymentDate() != null)
					channelTransferVO
							.setPayInstrumentDate(BTSLUtil.getDateFromDateString((p_requestVO.getPaymentDate())));
				if (p_requestVO.getPaymentType() != null)
					channelTransferVO.setPayInstrumentType(p_requestVO.getPaymentType());
				if (p_requestVO.getPaymentInstNumber() != null)
					channelTransferVO.setPayInstrumentNum(p_requestVO.getPaymentInstNumber());
			}
			if (!(String.valueOf(level).equals(PretupsI.CACHE_ALL))) {
				updateCount = ChnlToChnlTransferTransactionCntrl.approveChannelToChannelTransferInitiate(con,
						channelTransferVO, isOutsideHierarchy, false, null, curDate);
			}

			else {
				p_requestVO.setMessageCode(PretupsErrorCodesI.C2C_TRF_INITIATE_ZERO_LEVEL);
				return;
			}

			// manisha
			if (!receiverChannelUserVO.isStaffUser()) {
				(receiverChannelUserVO.getUserPhoneVO()).setLastTransferID(channelTransferVO.getTransferID());
				(receiverChannelUserVO.getUserPhoneVO()).setLastTransferType(PretupsI.TRANSFER_TYPE_C2C);
			} else {
				(receiverChannelUserVO.getStaffUserDetails().getUserPhoneVO())
						.setLastTransferID(channelTransferVO.getTransferID());
				(receiverChannelUserVO.getStaffUserDetails().getUserPhoneVO())
						.setLastTransferType(PretupsI.TRANSFER_TYPE_C2C);
			}

			if (updateCount > 0) {
				if (_log.isDebugEnabled()) {
					_log.debug("process", "Commit the data ");
				}
				if (mcomCon != null) {
					mcomCon.partialCommit();
				}

				if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL)).booleanValue()) {
					try {
						if (p_requestVO.isSuccessTxn()) {
							final LoyaltyBL _loyaltyBL = new LoyaltyBL();
							final LoyaltyVO loyaltyVO = new LoyaltyVO();
							PromotionDetailsVO promotionDetailsVO = new PromotionDetailsVO();
							final LoyaltyDAO _loyaltyDAO = new LoyaltyDAO();
							final ArrayList arr = new ArrayList();
							loyaltyVO.setModuleType(PretupsI.C2C_MODULE);
							loyaltyVO.setServiceType(PretupsI.C2C_MODULE);
							loyaltyVO.setTransferamt(channelTransferVO.getReceiverCrQty());
							loyaltyVO.setCategory(channelTransferVO.getCategoryCode());
							loyaltyVO.setFromuserId(channelTransferVO.getFromUserID());
							loyaltyVO.setTouserId(channelTransferVO.getToUserID());
							loyaltyVO.setNetworkCode(channelTransferVO.getNetworkCode());
							loyaltyVO.setTxnId(channelTransferVO.getTransferID());
							loyaltyVO.setCreatedOn(channelTransferVO.getCreatedOn());
							loyaltyVO.setSenderMsisdn(senderVO.getMsisdn());
							loyaltyVO.setReciverMsisdn(p_requestVO.getReceiverMsisdn());
							loyaltyVO.setProductCode(channelTransferVO.getProductCode());
							arr.add(loyaltyVO.getFromuserId());
							arr.add(loyaltyVO.getTouserId());
							promotionDetailsVO = _loyaltyDAO.loadSetIdByUserId(con, arr);
							loyaltyVO.setSetId(promotionDetailsVO.get_setId());
							loyaltyVO.setToSetId(promotionDetailsVO.get_toSetId());

							if ((loyaltyVO.getSetId() == null) && (loyaltyVO.getToSetId() == null)) {
								_log.error("process", "Exception during LMS Module.SetId not found");
							} else {
								_loyaltyBL.distributeLoyaltyPoints(PretupsI.C2C_MODULE,
										channelTransferVO.getTransferID(), loyaltyVO);
							}
						}
					} catch (Exception ex) {
						_log.error("process", "Exception durign LMS Module " + ex.getMessage());
						_log.errorTrace(METHOD_NAME, ex);
					}

				}
				PushMessage pushMessage = null;
				ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
				if (channelTransferVO.getStatus().equals(PretupsI.CHANNEL_TRANSFER_ORDER_NEW)) {
					sendEmailNotificationTrf(con, "C2CTRFAPR1", channelTransferVO.getTransferID(),
							"c2c.transfer.initiate.email.notification",channelTransferVO.getFromUserID());
					String smsKey = PretupsErrorCodesI.C2C_TRANSFER_APPROVAL;
					final String[] array1 = { channelTransferVO.getTransferID() };
					BTSLMessages messages = new BTSLMessages(smsKey, array1);
					String msisdns = channelTransferDAO.getMsisdnOfApprovers(con, "C2CTRFAPR1", channelTransferVO.getFromUserID());
					if (!BTSLUtil.isNullString(msisdns)) {
						String[] arrSplit = msisdns.split(",");
						for (int i = 0; i < arrSplit.length; i++) {
							String msisdn = arrSplit[i];
							pushMessage = new PushMessage(msisdn, messages, channelTransferVO.getTransferID(), "",
									p_requestVO.getLocale(), p_requestVO.getNetworkCode());
							pushMessage.push();
						}
					}
					p_requestVO.setMessageArguments(array1);
					p_requestVO.setMessageCode(PretupsErrorCodesI.C2C_TRF_APPROVAL);
					p_requestVO.setTransactionID(channelTransferVO.getTransferID());
					return;

				}
				ChannelTransferBL.prepareUserBalancesListForLogger(channelTransferVO);
			}
			try {
				if (mcomCon != null) {
					mcomCon.finalRollback();
				}
			} catch (SQLException esql) {
				_log.error(METHOD_NAME, "SQLException " + esql);
				_log.errorTrace(METHOD_NAME, esql);
			}
			p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER);
			throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ERROR_USER_TRANSFER);
		} catch (BTSLBaseException be) {
			p_requestVO.setSuccessTxn(false);
			try {
				if (mcomCon != null) {
					mcomCon.finalRollback();
				}
			}

			catch (SQLException esql) {
				_log.error(METHOD_NAME, "SQLException : ", esql.getMessage());
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
				_log.error(METHOD_NAME, "SQLException : ", esql.getMessage());
			}
			_log.error("process", "BTSLBaseException " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"C2CTrfInitiateController[process]", "", "", "", "Exception:" + e.getMessage());
			p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER);
			return;
		} finally {
			if (mcomCon != null) {
				mcomCon.close("C2CTrfInitiateController#process");
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) {
				_log.debug("process", " Exited ");
			}
		} // end of finally
	}// end of process()

	/**
	 * Method prepareTransferProfileVO This method construct the VO for the Txn
	 * 
	 * @param p_senderVO
	 * @param p_receiverVO
	 * @param p_productList
	 * @param p_curDate
	 * @return ChannelTransferVO
	 * @throws BTSLBaseException
	 */
	private ChannelTransferVO prepareTransferProfileVO(ChannelUserVO p_senderVO, ChannelUserVO p_receiverVO,
			ArrayList p_productList, Date p_curDate) throws BTSLBaseException {
		StringBuilder loggerValue = new StringBuilder();
		if (_log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append(" Entered  p_senderVO: ");
			loggerValue.append(p_senderVO);
			loggerValue.append(" p_receiverVO:");
			loggerValue.append(p_receiverVO);
			loggerValue.append(" p_productList:");
			loggerValue.append(p_productList.size());
			loggerValue.append(" p_curDate:");
			loggerValue.append(p_curDate);
			_log.debug("prepareTransferProfileVO", loggerValue);
		}

		final ChannelTransferVO channelTransferVO = new ChannelTransferVO();

		channelTransferVO.setNetworkCode(p_senderVO.getNetworkID());
		channelTransferVO.setNetworkCodeFor(p_senderVO.getNetworkID());
		channelTransferVO.setDomainCode(p_receiverVO.getDomainID());
		
		channelTransferVO.setGraphicalDomainCode(p_receiverVO.getGeographicalCode());
		channelTransferVO.setReceiverCategoryCode(p_receiverVO.getCategoryCode());
		channelTransferVO.setCategoryCode(p_receiverVO.getCategoryCode());
		channelTransferVO.setReceiverGradeCode(p_receiverVO.getUserGrade());
		channelTransferVO.setSenderGradeCode(p_senderVO.getUserGrade());
		channelTransferVO.setFromUserID(p_senderVO.getUserID());
		// channelTransferVO.setFromUserCode(p_receiverVO.getUserCode());
		channelTransferVO.setToUserID(p_receiverVO.getUserID());
		// channelTransferVO.setToUserCode(p_senderVO.getUserCode());
		channelTransferVO.setTransferDate(p_curDate);
		channelTransferVO.setCommProfileSetId(p_receiverVO.getCommissionProfileSetID());
		channelTransferVO.setCommProfileVersion(p_receiverVO.getCommissionProfileSetVersion());
		channelTransferVO.setDualCommissionType(p_receiverVO.getDualCommissionType());
		channelTransferVO.setCreatedOn(p_curDate);
		channelTransferVO.setCreatedBy(p_receiverVO.getActiveUserID());
		channelTransferVO.setModifiedOn(p_curDate);
		channelTransferVO.setModifiedBy(p_receiverVO.getActiveUserID());
		channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
		channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
		channelTransferVO.setTransferInitatedBy(p_receiverVO.getUserID());
		channelTransferVO.setReceiverTxnProfile(p_receiverVO.getTransferProfileID());
		channelTransferVO.setSenderTxnProfile(p_senderVO.getTransferProfileID());
		// channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_STK);

		// adding the some additional information for sender/reciever
		channelTransferVO.setReceiverGgraphicalDomainCode(p_receiverVO.getGeographicalCode());
		channelTransferVO.setReceiverDomainCode(p_receiverVO.getDomainID());
		/*06-12-2021 start*/
//		channelTransferVO.setFromUserCode(PretupsBL.getFilteredMSISDN(p_senderVO.getMsisdn()));
//		channelTransferVO.setToUserCode(PretupsBL.getFilteredMSISDN(p_receiverVO.getUserCode()));
		
		channelTransferVO.setFromUserCode(PretupsBL.getFilteredMSISDN(p_receiverVO.getUserCode()));
		channelTransferVO.setToUserCode(PretupsBL.getFilteredMSISDN(p_senderVO.getMsisdn()));
		/*06-12-2021 end*/
		channelTransferVO.setTransferCategory(p_senderVO.getTransferCategory());

		ChannelTransferItemsVO channelTransferItemsVO = null;
		String productCode = null;
		String productType = null;
		long totRequestQty = 0, totMRP = 0, totPayAmt = 0, totNetPayAmt = 0, totTax1 = 0, totTax2 = 0, totTax3 = 0;
		long commissionQty = 0, senderDebitQty = 0, receiverCreditQty = 0;
		for (int i = 0, k = p_productList.size(); i < k; i++) {
			channelTransferItemsVO = (ChannelTransferItemsVO) p_productList.get(i);
			totRequestQty += PretupsBL.getSystemAmount(channelTransferItemsVO.getRequestedQuantity());
			if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType())) {
				totMRP += (channelTransferItemsVO.getReceiverCreditQty())
						* Long.parseLong(PretupsBL.getDisplayAmount(channelTransferItemsVO.getUnitValue()));
			} else {
				totMRP += (Double.parseDouble(channelTransferItemsVO.getRequestedQuantity())
						* channelTransferItemsVO.getUnitValue());
			}
			totPayAmt += channelTransferItemsVO.getPayableAmount();
			totNetPayAmt += channelTransferItemsVO.getNetPayableAmount();
			totTax1 += channelTransferItemsVO.getTax1Value();
			totTax2 += channelTransferItemsVO.getTax2Value();
			totTax3 += channelTransferItemsVO.getTax3Value();
			commissionQty += channelTransferItemsVO.getCommQuantity();
			senderDebitQty += channelTransferItemsVO.getSenderDebitQty();
			receiverCreditQty += channelTransferItemsVO.getReceiverCreditQty();
			productCode = channelTransferItemsVO.getProductCode();
			productType = channelTransferItemsVO.getProductType();
		}
		channelTransferVO.setRequestedQuantity(totRequestQty);
		channelTransferVO.setTransferMRP(totMRP);
		channelTransferVO.setPayableAmount(totPayAmt);
		channelTransferVO.setNetPayableAmount(totNetPayAmt);
		channelTransferVO.setProductType(productType);
		channelTransferVO.setTotalTax1(totTax1);
		channelTransferVO.setTotalTax2(totTax2);
		channelTransferVO.setTotalTax3(totTax3);
		channelTransferVO.setType(PretupsI.CHANNEL_TYPE_C2C);
		channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
		channelTransferVO.setCommQty(PretupsBL.getSystemAmount(commissionQty));
		channelTransferVO.setSenderDrQty(PretupsBL.getSystemAmount(senderDebitQty));
		channelTransferVO.setReceiverCrQty(PretupsBL.getSystemAmount(receiverCreditQty));

        channelTransferVO.setChannelTransferitemsVOList(p_productList);
        channelTransferVO.setActiveUserId(p_senderVO.getActiveUserID());
        if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue()){
        	ArrayList<ChannelSoSVO> chnlSoSVOList = new ArrayList<> ();
        	chnlSoSVOList.add(new ChannelSoSVO(p_senderVO.getUserID(),p_senderVO.getMsisdn(),p_senderVO.getSosAllowed(),p_senderVO.getSosAllowedAmount(),p_senderVO.getSosThresholdLimit()));
        	chnlSoSVOList.add(new ChannelSoSVO(p_receiverVO.getUserID(),p_receiverVO.getMsisdn(),p_receiverVO.getSosAllowed(),p_receiverVO.getSosAllowedAmount(),p_receiverVO.getSosThresholdLimit()));
        	channelTransferVO.setChannelSoSVOList(chnlSoSVOList);
        }

		if (_log.isDebugEnabled()) {
			_log.debug("prepareTransferProfileVO", " Exited  ");
		}
		return channelTransferVO;
	}// end of
	
	private void setAmountsAfterCalculation(ChannelTransferVO channelTransferVO , ArrayList p_productList , ChannelUserVO p_receiverVO) throws BTSLBaseException {
		if (_log.isDebugEnabled()) {
			_log.debug("setAmountsAfterCalculation", " Entered  channelTransferVO: " + channelTransferVO + " p_receiverVO:"
					+ p_receiverVO + " p_productList:" + p_productList.size());
		}
		ChannelTransferItemsVO channelTransferItemsVO = null;
		long totRequestQty = 0, totMRP = 0, totPayAmt = 0, totNetPayAmt = 0, totTax1 = 0, totTax2 = 0, totTax3 = 0;
		long commissionQty = 0, senderDebitQty = 0, receiverCreditQty = 0;
		String productCode = null;
		String productType = null;
		for (int i = 0, k = p_productList.size(); i < k; i++) {
			channelTransferItemsVO = (ChannelTransferItemsVO) p_productList.get(i);
			totRequestQty += PretupsBL.getSystemAmount(channelTransferItemsVO.getRequestedQuantity());
			if (PretupsI.COMM_TYPE_POSITIVE.equals(p_receiverVO.getDualCommissionType())) {
				totMRP += (channelTransferItemsVO.getReceiverCreditQty())
						* Long.parseLong(PretupsBL.getDisplayAmount(channelTransferItemsVO.getUnitValue()));
			} else {
				totMRP += (Double.parseDouble(channelTransferItemsVO.getRequestedQuantity())
						* channelTransferItemsVO.getUnitValue());
			}
			totPayAmt += channelTransferItemsVO.getPayableAmount();
			totNetPayAmt += channelTransferItemsVO.getNetPayableAmount();
			totTax1 += channelTransferItemsVO.getTax1Value();
			totTax2 += channelTransferItemsVO.getTax2Value();
			totTax3 += channelTransferItemsVO.getTax3Value();
			commissionQty += channelTransferItemsVO.getCommQuantity();
			senderDebitQty += channelTransferItemsVO.getSenderDebitQty();
			receiverCreditQty += channelTransferItemsVO.getReceiverCreditQty();
			productCode = channelTransferItemsVO.getProductCode();
			productType = channelTransferItemsVO.getProductType();
		} // end of for
		channelTransferVO.setRequestedQuantity(totRequestQty);
		channelTransferVO.setTransferMRP(totMRP);
		channelTransferVO.setPayableAmount(totPayAmt);
		channelTransferVO.setNetPayableAmount(totNetPayAmt);
		channelTransferVO.setTotalTax1(totTax1);
		channelTransferVO.setTotalTax2(totTax2);
		channelTransferVO.setTotalTax3(totTax3);
		channelTransferVO.setCommQty(PretupsBL.getSystemAmount(commissionQty));
		channelTransferVO.setSenderDrQty(PretupsBL.getSystemAmount(senderDebitQty));
		channelTransferVO.setReceiverCrQty(PretupsBL.getSystemAmount(receiverCreditQty));
		if (_log.isDebugEnabled()) {
			_log.debug("setAmountsAfterCalculation", " Exited  ");
		}
	}
	
	

	private void sendEmailNotificationTrf(Connection p_con, String p_roleCode, String transferID, String p_subject, String userId) {
		final String methodName = "sendEmailNotificationTrf";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");
		}
		try {
			String cc = PretupsI.EMPTY;
			final String bcc = "";
			String subject = "";
			ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
			String to = channelTransferDAO.getEmailIdOfApprovers(p_con, p_roleCode, userId);
			final boolean isAttachment = false;
			final String pathofFile = "";
			final String fileNameTobeDisplayed = "";
			String messages = null;
			subject = PretupsRestUtil.getMessageString(p_subject);
			final Locale locale = BTSLUtil.getSystemLocaleForEmail();
			final String from = BTSLUtil.getMessage(locale, "email.notification.changestatus.log.file.from");
			messages = PretupsRestUtil.getMessageString("c2c.transfer.initiate.email.notification.content") + " "
					+ transferID
					+ PretupsRestUtil.getMessageString("c2c.transfer.initiate.email.notification.content1");
			if (!BTSLUtil.isNullString(p_roleCode)) {
				EMailSender.sendMail(to, from, bcc, cc, subject, messages, isAttachment, pathofFile,
						fileNameTobeDisplayed);
			}
			if (_log.isDebugEnabled()) {
				_log.debug("MAIL CONTENT ", messages);
			}
		} catch (Exception e) {
			if (_log.isDebugEnabled()) {
				_log.error("sendEmailNotificationTrf ", " Email sending failed" + e.getMessage());
			}
			_log.errorTrace(methodName, e);
		}
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Exiting ....");
		}
	}
}
