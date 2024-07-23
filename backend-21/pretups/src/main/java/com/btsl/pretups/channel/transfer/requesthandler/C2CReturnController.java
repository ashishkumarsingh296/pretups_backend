/**
 * @(#)C2CReturnController.java
 *                              Copyright(c) 2005, Bharti Telesoft Ltd.
 *                              All Rights Reserved
 * 
 *                              <description>
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Author Date History
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              avinash.kamthan Sep 6, 2005 Initital Creation
 *                              Sandeep Goel Nov 26,2005 Modification &
 *                              customization
 *                              Sandeep Goel May 20,2006 Modification &
 *                              customization
 *                              Ankit Zindal Nov 20,2006 ChangeID=LOCALEMASTER
 *                              Ashish Kumar May 11,2007 Modification.
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              This is the controller class for the c2c return.
 *                              process method of this class is called by the
 *                              channel receiver
 *                              class and response of the processing is send
 *                              back to the channel receiver with the required
 *                              message key.
 */

package com.btsl.pretups.channel.transfer.requesthandler;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileDAO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.receiver.RestReceiver;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChnlToChnlTransferTransactionCntrl;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.common.PretupsRestI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
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
import com.btsl.util.SwaggerAPIDescriptionI;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.restapi.user.service.C2CReturnRequestVO;
import com.txn.pretups.channel.profile.businesslogic.CommissionProfileTxnDAO;
import com.txn.pretups.user.businesslogic.ChannelUserTxnDAO;
import com.btsl.pretups.logging.OneLineTXNLog;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;
import com.btsl.user.businesslogic.UserLoanVO;
/*@Path("")
@Api(value="C2C RETURN")*/
@io.swagger.v3.oas.annotations.tags.Tag(name = "${C2CReturnController.name}", description = "${C2CReturnController.desc}")//@Api(tags= "C2C Receiver")
@RestController
@RequestMapping(value = "/v1/c2cReceiver")
public class C2CReturnController implements ServiceKeywordControllerI {
    private static final  Log _log = LogFactory.getLog(C2CReturnController.class.getName());
    private String _allowedSendMessGatw = null;
    public static OperatorUtilI _operatorUtil = null;
    /*static {
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2CReturnController[initialize]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
    }*/
    
   /* @Context
	private HttpServletRequest httpServletRequest;
	
	@POST
	@Path("/c2s-rest-receiver/c2cret")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)*/
    @PostMapping(value = "/c2cret", consumes =MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "C2C RETURN", response = PretupsResponse.class,
			authorizations = {
    	            @Authorization(value = "Authorization")})
    
    @ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = PretupsResponse.class),
	        @ApiResponse(code = 201, message = "Created"),
	        @ApiResponse(code = 400, message = "Bad Request"),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })*/


    @io.swagger.v3.oas.annotations.Operation(summary = "${c2cret.summary}", description="${c2cret.description}",

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
                            )
                    }),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_UNAUTH_RESPONSE_CODE, description = com.btsl.util.Constants.API_UNAUTH_RESPONSE_DESC,  content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
                            )
                    }),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_CODE, description = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_DESC,  content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
                            )
                    }),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_DESC,  content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
                            )
                    })
            }
    )




    public PretupsResponse<JsonNode> processCP2PUserRequest(
    		HttpServletRequest httpServletRequest,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
    		@Parameter(description = SwaggerAPIDescriptionI.C2C_RETURN)
    		@RequestBody C2CReturnRequestVO c2CReturnRequestVO, HttpServletResponse response1) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
    	final String methodName = "processCP2PUserRequest_C2CReturnController";
		PretupsResponse<JsonNode> response;
        RestReceiver restReceiver;
        RestReceiver.updateRequestIdChannel();
        final String requestIDStr = String.valueOf(RestReceiver.getRequestIdChannel());
        OAuthUser oAuthUser= null;
		OAuthUserData oAuthUserData =null;
        restReceiver = new RestReceiver();
        
        ChannelUserVO channelUserVO = null;
		UserPhoneVO userPhoneVO = null;
		UserDAO userDao = new UserDAO();
		Connection con = null;
		MComConnectionI mcomCon = null;
        
        try {
			/*
			 * Authentication
			 * @throws BTSLBaseException
			 */
        	oAuthUser = new OAuthUser();
			oAuthUserData =new OAuthUserData();
			oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers,response1);
			c2CReturnRequestVO.setServicePort(oAuthUser.getServicePort());
			c2CReturnRequestVO.setReqGatewayCode(oAuthUser.getReqGatewayCode());
			c2CReturnRequestVO.setReqGatewayLoginId(oAuthUser.getReqGatewayLoginId());
			c2CReturnRequestVO.setReqGatewayPassword(oAuthUser.getReqGatewayPassword());
			c2CReturnRequestVO.setReqGatewayType(oAuthUser.getReqGatewayType());
			c2CReturnRequestVO.setSourceType(oAuthUser.getSourceType());
			
			c2CReturnRequestVO.getData().setLoginid(oAuthUser.getData().getLoginid());
			c2CReturnRequestVO.getData().setPassword(oAuthUser.getData().getPassword());
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			channelUserVO = userDao.loadAllUserDetailsByLoginID(con, oAuthUser.getData().getLoginid());
			userPhoneVO = userDao.loadUserPhoneVO(con, channelUserVO.getUserID());
			
			if(!c2CReturnRequestVO.getData().getPin().isEmpty()) {
				//Decrypting pin and setting back in requestVO
				String encryptedPin = c2CReturnRequestVO.getData().getPin();
				String decryptedPin = AESEncryptionUtil.aesDecryptor(encryptedPin, Constants.A_KEY);
				c2CReturnRequestVO.getData().setPin(decryptedPin);
			}
			if(userPhoneVO != null) {
				if(c2CReturnRequestVO.getData().getPin().isEmpty() && (!userPhoneVO.isPinRequiredBool())) {
					c2CReturnRequestVO.getData().setPin(oAuthUser.getData().getPin());
				}
			}
			
			
//			OAuthenticationUtil.validateTokenApi(headers);
			String jsonString = BTSLUtil.appendWebApiCall(PretupsRestUtil.convertObjectToJSONString(c2CReturnRequestVO));
        response = restReceiver.processCP2PRequestOperator(httpServletRequest,(JsonNode)(PretupsRestUtil.convertJSONToObject(jsonString, new TypeReference<JsonNode>(){})), PretupsRestI.C2CRETURN,requestIDStr);
        if(response.getStatusCode()!=200)
     	   response1.setStatus(HttpStatus.SC_BAD_REQUEST);
     if(response!=null && response.getDataObject()!=null && response.getDataObject().get("txnstatus") != null){
     	if(!response.getDataObject().get("txnstatus").textValue().equals("200"))
     		response1.setStatus(HttpStatus.SC_BAD_REQUEST);
     }
        return response;
    } catch (BTSLBaseException be) {
		PretupsResponse<JsonNode> baseResponse= new PretupsResponse<JsonNode>() ;
		
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
			String resmsg = RestAPIStringParser.getMessage(locale,  be.getMessageKey(),null);

        	baseResponse.setStatusCode(be.getErrorCode());
        	baseResponse.setMessage(resmsg);
            return baseResponse;
        
    } catch (Exception e) {
    	PretupsResponse<JsonNode> baseResponse= new PretupsResponse<JsonNode>() ;
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

    @Override
	public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_requestVO: ");
        	loggerValue.append(p_requestVO);
            _log.debug("process", loggerValue );
        }
        final ChannelUserVO senderVO = (ChannelUserVO) p_requestVO.getSenderVO();
        UserPhoneVO userPhoneVO = null;
        if (!senderVO.isStaffUser()) {
            userPhoneVO = senderVO.getUserPhoneVO();
        } else {
            userPhoneVO = senderVO.getStaffUserDetails().getUserPhoneVO();
        }

        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered Sender VO: ");
        	loggerValue.append(senderVO);
            _log.debug("process", loggerValue );
        }

        // String messageArr[] = p_requestVO.getRequestMessageArray();

        // ///
        // message format
        // [keyword] [usercode] [qty] [productcode] [qty]
        // [productcode][password]
        // 1.) if password is PASSWRD the not to validate the user password
        // 2.) usercode should be numeric
        // 3.) product code should be numeric
        // 4.) qty should be numeric 5.) product code and qty should be always
        // with each
        // other 6.) load the receiver information on the base of usercode Check
        // the networkcode of sender and receiver user. both should be same
        // 7.) check the transfer rule. whether transfer is allowed between
        // sender category to receiver category.
        // 8.) check the product code existance.
        // 9.) check product associated with the receiver user.
        // 10.) check the min transfer and max transfer value of the selected
        // product
        // 10.) check the receiver max balance for the product/s.
        // 11.) check the sender min residual balance for the product/s.
        // /

        Connection con = null;
        MComConnectionI mcomCon = null;
        ChannelUserTxnDAO channelUserTxnDAO = null;

        try {
            // Validate the user is out suspended or not if user is out
            // suspended then show error message
        	final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
	        try {
	            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
	        } catch (Exception e) {
	            _log.errorTrace("static", e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[initialize]", "", "", "",
	                "Exception while loading the class at the call:" + e.getMessage());
	        }
            if (senderVO != null && PretupsI.USER_TRANSFER_OUT_STATUS_SUSPEND.equals(senderVO.getOutSuspened())) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_OUT_SUSPENDED);
                throw new BTSLBaseException("C2CReturnController", "process", PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_OUT_SUSPENDED);
            }

            final Date curDate = new Date();
            final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
            channelUserTxnDAO = new ChannelUserTxnDAO();
            ChannelUserVO receiverChannelUserVO = null;
            boolean isUserDetailLoad = false;
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            UserPhoneVO PrimaryPhoneVO_R = null;
            final UserDAO userDAO = new UserDAO();
            UserPhoneVO phoneVO = null;
            boolean receiverAllowed = false;
            boolean senderAllowed = false;
            UserStatusVO senderStatusVO = null;
            UserStatusVO receiverStatusVO = null;
            if (!BTSLUtil.isNullString(p_requestVO.getReceiverExtCode())) {
                receiverChannelUserVO = channelUserTxnDAO.loadChannelUserDetailsForTransferIfReqExtgw(con, p_requestVO.getReceiverExtCode(), null, curDate);
                if (receiverChannelUserVO == null) {
                    throw new BTSLBaseException("C2CReturnController", "process", PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE, 0, null);
                }

                isUserDetailLoad = true;
            } else if (!BTSLUtil.isNullString(p_requestVO.getReceiverLoginID())) {
                receiverChannelUserVO = channelUserTxnDAO.loadChannelUserDetailsForTransferIfReqExtgw(con, null, p_requestVO.getReceiverLoginID(), curDate);
                if (receiverChannelUserVO == null) {
                    throw new BTSLBaseException("C2CReturnController", "process", PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID, 0, null);
                }

                isUserDetailLoad = true;
            }
            if (!(receiverChannelUserVO == null) && isUserDetailLoad) {
                if (!BTSLUtil.isNullString(p_requestVO.getReceiverExtCode())) {
                    if (!p_requestVO.getReceiverExtCode().equals(receiverChannelUserVO.getExternalCode())) {
                        throw new BTSLBaseException("C2CReturnController", "process", PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE, 0, null);
                    }
                }
                if (!BTSLUtil.isNullString(p_requestVO.getReceiverLoginID())) {
                    if (!p_requestVO.getReceiverLoginID().equals(receiverChannelUserVO.getLoginID())) {
                        throw new BTSLBaseException("C2CReturnController", "process", PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID, 0, null);
                    }
                }
                if (!BTSLUtil.isNullString(p_requestVO.getReceiverMsisdn())) {
                    if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue()) {
                        phoneVO = userDAO.loadUserAnyPhoneVO(con, p_requestVO.getReceiverMsisdn());
                        if (phoneVO != null && ((receiverChannelUserVO.getUserID()).equals(phoneVO.getUserId()))) {
                            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MESSAGE_TO_PRIMARY_REQUIRED)).booleanValue() && ("N".equalsIgnoreCase(phoneVO.getPrimaryNumber()))) {
                                PrimaryPhoneVO_R = userDAO.loadUserAnyPhoneVO(con, receiverChannelUserVO.getMsisdn());
                            }
                            receiverChannelUserVO.setPrimaryMsisdn(receiverChannelUserVO.getMsisdn());
                            receiverChannelUserVO.setMsisdn(p_requestVO.getReceiverMsisdn());
                        } else {
                            throw new BTSLBaseException("C2CReturnController", "process", PretupsErrorCodesI.EXT_XML_ERROR_INVALID_MSISDN, 0, null);
                        }
                    } else if (!p_requestVO.getReceiverMsisdn().equalsIgnoreCase(receiverChannelUserVO.getMsisdn())) {
                        throw new BTSLBaseException("C2CReturnController", "process", PretupsErrorCodesI.EXT_XML_ERROR_INVALID_MSISDN, 0, null);
                    }
                }

                // To set the msisdn in the request message array...
                if (BTSLUtil.isNullString(p_requestVO.getReceiverMsisdn()) && BTSLUtil.isNullString((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR))) {
                    final String[] message= p_requestVO.getRequestMessageArray();
                    final String[] newMessageArr = new String[message.length + 1];
                    // newMessageArr=p_requestVO.getRequestMessageArray();
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
            final String[] messageArr = p_requestVO.getRequestMessageArray();
            final int messageLen = messageArr.length;

            if (messageArr.length < 2) {
                throw new BTSLBaseException("C2CReturnController", "process", PretupsErrorCodesI.ERROR_INVALID_REQUESTFORMAT, 0, new String[] { p_requestVO
                    .getActualMessageFormat() }, null);
            }

            // Validate the user code (mobile number in the message) is it
            // numeric or not
            if (!BTSLUtil.isNumeric(messageArr[1])) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_USER_CODE_FORMAT);
                throw new BTSLBaseException("C2CReturnController", "process", PretupsErrorCodesI.ERROR_INVALID_USER_CODE_FORMAT);
            }// end if

            // Vlaidte the user products as the quantity and product code both
            // should be numeric value
            // and if user does not send the product code then use the default
            // product as the requested product
            final String[] productArray = ChannelTransferBL.validateUserProductsFormatForSMS(messageArr, p_requestVO);

            final int msgLen = messageArr.length;

            if (userPhoneVO.getPinRequired().equals(PretupsI.YES)) {
                try {
                    // Validation PIN
                    ChannelUserBL.validatePIN(con, (ChannelUserVO) p_requestVO.getSenderVO(), messageArr[msgLen - 1]);
                } catch (BTSLBaseException be) {
                    if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
                        .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                    	// commiting transaction of PIN if it is
                    	mcomCon.finalCommit();
                    }
                    // invalid
                    throw be;
                }
            }// end if validate PIN

            String receiverUserCode = messageArr[1];
            receiverUserCode = _operatorUtil.addRemoveDigitsFromMSISDN(PretupsBL.getFilteredMSISDN(receiverUserCode));
            if (!BTSLUtil.isValidMSISDN(receiverUserCode)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_REC_USERCODE);
                throw new BTSLBaseException("C2CReturnController", "process", PretupsErrorCodesI.ERROR_INVALID_REC_USERCODE);
            }
            final String msisdnPrefix = PretupsBL.getMSISDNPrefix(receiverUserCode);

            // Getting network details
            final NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
            if (networkPrefixVO == null) {
                throw new BTSLBaseException("C2CReturnController", "process", PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_UNSUPPORTED_NETWORK, 0,
                    new String[] { receiverUserCode }, null);
            }

            final BarredUserDAO barredUserDAO = new BarredUserDAO();

            if (barredUserDAO.isExists(con, PretupsI.C2S_MODULE, networkPrefixVO.getNetworkCode(), receiverUserCode, PretupsI.USER_TYPE_RECEIVER, null)) {
                throw new BTSLBaseException("C2CReturnController", "process", PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_RECEIVER_BAR, 0,
                    new String[] { receiverUserCode }, null);
            }
            
            String requesterMsisdn = p_requestVO.getRequestMSISDN();
			
			if (barredUserDAO.isExists(con, PretupsI.C2S_MODULE, networkPrefixVO.getNetworkCode(), requesterMsisdn,
					PretupsI.USER_TYPE_SENDER, null)) {
				throw new BTSLBaseException("C2CTrfInitiateController", "process",
						PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_SENDER_BAR, 0,
						new String[] { requesterMsisdn }, null);
			}

            if (phoneVO == null) {
                phoneVO = userDAO.loadUserAnyPhoneVO(con, receiverUserCode);
            }

            if (!isUserDetailLoad) {
                if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue()) {
                    receiverChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, receiverUserCode, true, curDate,false);
                } else {
                    if (phoneVO != null && !("Y".equalsIgnoreCase(phoneVO.getPrimaryNumber()))) {
                        receiverChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, phoneVO.getUserId(), false, curDate,false);
                        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MESSAGE_TO_PRIMARY_REQUIRED)).booleanValue()) {
                            PrimaryPhoneVO_R = userDAO.loadUserAnyPhoneVO(con, receiverChannelUserVO.getMsisdn());
                        }
                        receiverChannelUserVO.setPrimaryMsisdn(receiverChannelUserVO.getMsisdn());
                        receiverChannelUserVO.setMsisdn(receiverUserCode);
                    } else {
                        receiverChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, receiverUserCode, true, curDate,false);
                    }
                }
            }

            // Meditel changes.....checking for receiver allowed
            if (receiverChannelUserVO != null) {
                receiverAllowed = false;
                receiverStatusVO = (UserStatusVO) UserStatusCache.getObject(receiverChannelUserVO.getNetworkID(), receiverChannelUserVO.getCategoryCode(),
                    receiverChannelUserVO.getUserType(), p_requestVO.getRequestGatewayType());
                if (receiverStatusVO != null) {
                    final String receiverStatusAllowed = receiverStatusVO.getUserReceiverAllowed();
                    final String[] status = receiverStatusAllowed.split(",");
                    for (int i = 0; i < status.length; i++) {
                        if (status[i].equals(receiverChannelUserVO.getStatus())) {
                            receiverAllowed = true;
                        }
                    }
                }
            }
            //
            String args[] = { receiverUserCode };

            // Checking user found or not
            if (receiverChannelUserVO == null) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_NOT_EXIST);
                p_requestVO.setMessageArguments(args);
                throw new BTSLBaseException("C2CReturnController", "process", PretupsErrorCodesI.ERROR_USER_NOT_EXIST, 0, args, null);
            }
            // Checking User status whether it is active or not
            else if (receiverChannelUserVO.getInSuspend() != null && PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(receiverChannelUserVO.getInSuspend())) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_IN_SUSPENDED);
                p_requestVO.setMessageArguments(args);
                throw new BTSLBaseException("C2CReturnController", "process", PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_IN_SUSPENDED, 0, args, null);
            } else if (receiverStatusVO == null) {
                throw new BTSLBaseException("C2CReturnController", "process", PretupsErrorCodesI.ERROR_USERSTATUS_NOTCONFIGURED);
            } else if (!receiverAllowed) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.CHNL_ERROR_RECEIVER_NOTALLOWED);
                p_requestVO.setMessageArguments(args);
                throw new BTSLBaseException("C2CReturnController", "process", PretupsErrorCodesI.CHNL_ERROR_RECEIVER_NOTALLOWED, 0, args, null);
            }
            // is there any applicable commission profile with user or not
            else if (receiverChannelUserVO.getCommissionProfileApplicableFrom().after(curDate)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_COMMISSION_PROFILE_NOT_APPLICABLE);
                p_requestVO.setMessageArguments(args);
                throw new BTSLBaseException("C2CReturnController", "process", PretupsErrorCodesI.ERROR_USER_COMMISSION_PROFILE_NOT_APPLICABLE, 0, args, null);
            }// is user is IN suspended or not if suspended then show error
             // message
             // end if

            // /
            // Now validate the sender and receiver as
            // 1. status of commission profile of both users
            // 2. status of the transfer profile of both users
            // 3. transaction allowed based on the transfer rule
            // 4. validation of the user based on the transfer rule
            // 5. check that transaction is outSide hierarchy or not.
            // /

            // set the commission profile suspended messages
            // ChangeID=LOCALEMASTER
            // Check which language message to be sent, from the locale master
            // table for the perticuler locale.

            final LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(p_requestVO.getLocale());
            if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
                senderVO.setCommissionProfileSuspendMsg(senderVO.getCommissionProfileLang1Msg());
                receiverChannelUserVO.setCommissionProfileSuspendMsg(receiverChannelUserVO.getCommissionProfileLang1Msg());
            } else {
                senderVO.setCommissionProfileSuspendMsg(senderVO.getCommissionProfileLang2Msg());
                receiverChannelUserVO.setCommissionProfileSuspendMsg(receiverChannelUserVO.getCommissionProfileLang2Msg());
            }// end if en

            // valiadting sender and receiver with the transfer rule in this
            // method we are passing various parameters as
            // con - connection for the database access
            // senderVO - VO contains sender's information
            // receiverVO - VO contains receiver's information
            // true (p_isUserCode) - To indicate that validation will be done on
            // the user code not on the user name for message display.
            // null (p_forwardPath) - for forwarding the message only in the
            // case of WEB so null is here
            // false (p_isFromWeb) - To indicate that request is not from WEB
            // i.e. form SMS/USSD.
            // txnSubType - Type of the transaction as TRANSFER, RETURN or
            // WITHDRAW. RETURN is here
            // This method return boolean value to indicate that whether this is
            // the controlled TXN or uncontroll TXN.

            final boolean isOutsideHierarchy = ChannelTransferBL.validateSenderAndReceiverWithXfrRule(con, senderVO, receiverChannelUserVO, true, null, false,
                PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN);

            // /
            // Now validate the requested product as
            // 1. existance in the system
            // 2. mapping with the network
            // 3. having balance >0
            // 4. Applicable commission profile version
            // 5. product associated with the commisssion profile
            // 6. product associated with the transfer rule.
            // 7. user balance with the requested quantity
            // /
            final ArrayList productList = ChannelTransferBL.validReqstProdsWithDfndProdsForWdAndRet(con, senderVO, productArray, curDate, p_requestVO.getLocale());

            // /
            // NOW LOAD THE LATEST VERSION OF COMMISSION PROFILE OF THE SENDER
            // USER
            // This case arise in the SMS RETURN only since in the controller we
            // load all the information of the
            // request sender in the requestVO but we don't load the latest
            // commission profile version since it is
            // not required in any other kind of request except the RETURN. So
            // we have to load it explicitly here
            //
            // /
            final CommissionProfileDAO commissionProfileDAO = new CommissionProfileDAO();
            final CommissionProfileTxnDAO commissionProfileTxnDAO = new CommissionProfileTxnDAO();
            final CommissionProfileSetVO commissionProfileSetVO = commissionProfileTxnDAO.loadCommProfileSetDetails(con, senderVO.getCommissionProfileSetID(), curDate);
            final String commProfileVersion = commissionProfileSetVO.getCommProfileVersion();
            senderVO.setCommissionProfileSetVersion(commProfileVersion);
            senderVO.setDualCommissionType(commissionProfileSetVO.getDualCommissionType());
            
            ChannelTransferVO channelTransferVO = new ChannelTransferVO();
            channelTransferVO.setChannelTransferitemsVOList(productList);
            channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN);
            channelTransferVO.setToUserID(receiverChannelUserVO.getUserID());
            channelTransferVO.setOtfFlag(false);

            // Now load the taxes and calculate the taxes on the requested
            // product quantity for the transaction
            ChannelTransferBL.loadAndCalculateTaxOnProducts(con, senderVO.getCommissionProfileSetID(), commProfileVersion, channelTransferVO, false, null,
                PretupsI.TRANSFER_TYPE_C2C);

            // Now prepare the channelTransferVO from the product list and the
            // sender and receiver VOs
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

            channelTransferVO = this.prepareTransferProfileVO(senderVO, receiverChannelUserVO, productList, curDate);
            if (isOutsideHierarchy) {
                channelTransferVO.setControlTransfer(PretupsI.NO);
            } else {
                channelTransferVO.setControlTransfer(PretupsI.YES);
            }
            channelTransferVO.setSource(p_requestVO.getSourceType());
            channelTransferVO.setRequestGatewayCode(p_requestVO.getRequestGatewayCode());
            channelTransferVO.setRequestGatewayType(p_requestVO.getRequestGatewayType());

            // for USSD
            channelTransferVO.setCellId(p_requestVO.getCellId());
            channelTransferVO.setSwitchId(p_requestVO.getSwitchId());
            channelTransferVO.setReferenceNum(p_requestVO.getExternalReferenceNum());

            // /
            // This is the final method which do the actual transaction as
            // 1. update user daily balances
            // 2. update user balances
            // 3. update thresholds
            // 4. make the transaction
            //
            // /
            final int updateCount = ChnlToChnlTransferTransactionCntrl.withdrawAndReturnChannelToChannel(con, channelTransferVO, false, false, null, curDate);

            // setting Last transfer ID and Status
            if (!senderVO.isStaffUser()) {
                (senderVO.getUserPhoneVO()).setLastTransferID(channelTransferVO.getTransferID());
                (senderVO.getUserPhoneVO()).setLastTransferType(PretupsI.TRANSFER_TYPE_C2C);
            } else {
                (senderVO.getStaffUserDetails().getUserPhoneVO()).setLastTransferID(channelTransferVO.getTransferID());
                (senderVO.getStaffUserDetails().getUserPhoneVO()).setLastTransferType(PretupsI.TRANSFER_TYPE_C2C);
            }

            final String recAlternetGatewaySMS = BTSLUtil.NullToString(Constants.getProperty("C2S_REC_MSG_REQD_BY_ALT_GW"));
            String reqruestGW = p_requestVO.getRequestGatewayCode();
            if (!BTSLUtil.isNullString(recAlternetGatewaySMS) && (recAlternetGatewaySMS.split(":")).length >= 2) {
                if (reqruestGW.equalsIgnoreCase(recAlternetGatewaySMS.split(":")[0])) {
                    reqruestGW = (recAlternetGatewaySMS.split(":")[1]).trim();
                    if (_log.isDebugEnabled()) {
                      loggerValue.setLength(0);
                      loggerValue.append("Requested GW was:");
                      loggerValue.append(p_requestVO.getRequestGatewayCode());
                        _log.debug("process: Reciver Message push through alternate GW", reqruestGW, loggerValue );
                    }
                }
            }
            int messageLength = 0;
            final String messLength = BTSLUtil.NullToString(Constants.getProperty("MSG_LENGTH_GW"));
            if (!BTSLUtil.isNullString(messLength)) {
                messageLength = (new Integer(messLength)).intValue();
            }

            if (updateCount > 0) {
                // commiting transaction
               
            	mcomCon.finalCommit();
                // Meditel changes by Ashutosh
                if (channelTransferVO.getStatus().equals(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE)) {
                    try {
                        if (mcomCon == null) {
                        	mcomCon = new MComConnection();
                        	con=mcomCon.getConnection();
                        }
                        senderAllowed = false;
                        senderStatusVO = (UserStatusVO) UserStatusCache.getObject(senderVO.getNetworkID(), senderVO.getCategoryCode(), senderVO.getUserType(), p_requestVO
                            .getRequestGatewayType());
                        if (senderStatusVO == null) {
                            throw new BTSLBaseException("C2CReturnController", "process", PretupsErrorCodesI.ERROR_USERSTATUS_NOTCONFIGURED);
                        } else {
                            final String senderStatusAllowed = senderStatusVO.getUserSenderAllowed();
                            final String status[] = senderStatusAllowed.split(",");
                            for (int i = 0; i < status.length; i++) {
                                if (status[i].equals(senderVO.getStatus())) {
                                    senderAllowed = true;
                                }
                            }
                            /*
                             * if(senderAllowed &&
                             * !PretupsI.USER_STATUS_ACTIVE.equals
                             * (senderVO.getStatus()))
                             * _operatorUtil.changeUserStatusToActive(
                             * con,senderVO.getUserID(),senderVO.getStatus());
                             */
                            PretupsBL.chkAllwdStatusToBecomeActive(con, (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_SENDER_USER_STATUS_CHANG), senderVO.getUserID(), senderVO.getStatus());

                            /*
                             * if(receiverAllowed &&
                             * PretupsI.USER_STATUS_CHURN.equals
                             * (receiverChannelUserVO.getStatus()) ||
                             * PretupsI.USER_STATUS_EXPIRED
                             * .equals(receiverChannelUserVO.getStatus()))
                             * _operatorUtil.changeUserStatusToActive(
                             * con,receiverChannelUserVO
                             * .getUserID(),receiverChannelUserVO.getStatus());
                             */
                            PretupsBL.chkAllwdStatusToBecomeActive(con, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_RECEIVER_USER_STATUS_CHANG)), receiverChannelUserVO.getUserID(),
                                receiverChannelUserVO.getStatus());
                        }
                    } catch (Exception ex) {
                    	 loggerValue.setLength(0);
                         loggerValue.append("Exception while changing user state to active  ");
                         loggerValue.append(ex.getMessage());
                        _log.error("process", loggerValue);
                        _log.errorTrace(METHOD_NAME, ex);
                    } finally {
                        if (con != null) {
                            try {
                                mcomCon.finalCommit();
                            } catch (Exception e) {
                                _log.errorTrace("process", e);
                            }
                        }

                    }
                }// end of changes
                //Added for OneLine Logging for Channel
				OneLineTXNLog.log(channelTransferVO, null);

                // prepare balance logger
                ChannelTransferBL.prepareUserBalancesListForLogger(channelTransferVO);

                // message to receiver
                final String receiverTxnSubKey = PretupsErrorCodesI.C2S_CHNL_CHNL_RETURN_RECEIVER_TXNSUBKEY;
                final String receiverBalSubKey = PretupsErrorCodesI.C2S_CHNL_CHNL_RETURN_RECEIVER_BALSUBKEY;
                String smsKey = PretupsErrorCodesI.C2S_CHNL_CHNL_RETURN_RECEIVER;
                if (PretupsI.TRANSFER_CATEGORY_TRANSFER.equals(senderVO.getTransferCategory())) {
                    smsKey = PretupsErrorCodesI.CHNL_RETURN_SUCCESS_RECEIVER_AGENT;
                }

                // preparing message to receiver
                final Object[] smsListArr = ChannelTransferBL.prepareSMSMessageListForReceiverForC2C(con, channelTransferVO, receiverTxnSubKey, receiverBalSubKey);
                final Locale locale = new Locale(phoneVO.getPhoneLanguage(), phoneVO.getCountry());
                final String[] array1 = { BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]), BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1]), channelTransferVO
                    .getTransferID(), PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()), p_requestVO.getFilteredMSISDN() };
                final BTSLMessages messages = new BTSLMessages(smsKey, array1);

                // pushing message to receiver
                PushMessage pushMessage = new PushMessage(phoneVO.getMsisdn(), messages, channelTransferVO.getTransferID(), reqruestGW, locale, channelTransferVO
                    .getNetworkCode());
                pushMessage.push();
                if (PrimaryPhoneVO_R != null) {
                    final Locale locale1 = new Locale(PrimaryPhoneVO_R.getPhoneLanguage(), PrimaryPhoneVO_R.getCountry());
                    pushMessage = new PushMessage(receiverChannelUserVO.getPrimaryMsisdn(), messages, channelTransferVO.getTransferID(), reqruestGW, locale1,
                        channelTransferVO.getNetworkCode());
                    pushMessage.push();
                }

                // preparing message to sender
                final ArrayList itemsList = channelTransferVO.getChannelTransferitemsVOList();
                if (senderVO.isStaffUser() && senderVO.getUserPhoneVO().getMsisdn().equals(p_requestVO.getMessageSentMsisdn())) {
                    smsKey = PretupsErrorCodesI.CHNL_RETURN_SUCCESS_STAFF;
                } else {
                    smsKey = PretupsErrorCodesI.CHNL_RETURN_SUCCESS;
                }
                if (PretupsI.TRANSFER_CATEGORY_TRANSFER.equals(senderVO.getTransferCategory())) {
                    smsKey = PretupsErrorCodesI.CHNL_RETURN_SUCCESS_SENDER_AGENT;
                }

                final ArrayList txnList = new ArrayList();
                final ArrayList balList = new ArrayList();
                args = null;
                ChannelTransferItemsVO channelTransferItemsVO = null;
                KeyArgumentVO keyArgumentVO = null;

                final int lSize = itemsList.size();
                for (int i = 0; i < lSize; i++) {
                    channelTransferItemsVO = (ChannelTransferItemsVO) itemsList.get(i);
                    keyArgumentVO = new KeyArgumentVO();
                    keyArgumentVO.setKey(PretupsErrorCodesI.CHNL_RETURN_SUCCESS_TXNSUBKEY);
                    args = new String[] { String.valueOf(channelTransferItemsVO.getShortName()), channelTransferItemsVO.getRequestedQuantity() };

                    keyArgumentVO.setArguments(args);
                    txnList.add(keyArgumentVO);

                    keyArgumentVO = new KeyArgumentVO();
                    keyArgumentVO.setKey(PretupsErrorCodesI.CHNL_RETURN_SUCCESS_BALSUBKEY);
                    // args = new
                    // String[]{String.valueOf(channelTransferItemsVO.getShortName()),PretupsBL.getDisplayAmount(channelTransferItemsVO.getBalance()-channelTransferItemsVO.getRequiredQuantity())};
                    if(!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()){
                    args = new String[] { String.valueOf(channelTransferItemsVO.getShortName()), PretupsBL.getDisplayAmount(channelTransferItemsVO
                        .getAfterTransSenderPreviousStock() - channelTransferItemsVO.getRequiredQuantity()) };
                    }
                    else{
                    	args = new String[] { String.valueOf(channelTransferItemsVO.getShortName()), PretupsBL.getDisplayAmount(channelTransferItemsVO.getTotalSenderBalance() - channelTransferItemsVO.getRequiredQuantity()) };
                    }
                    keyArgumentVO.setArguments(args);
                    balList.add(keyArgumentVO);
                }// end of for
                String[] array = null;
                if (senderVO.isStaffUser() && senderVO.getUserPhoneVO().getMsisdn().equals(p_requestVO.getMessageSentMsisdn())) {
                    array = new String[] { BTSLUtil.getMessage(p_requestVO.getLocale(), txnList), BTSLUtil.getMessage(p_requestVO.getLocale(), balList), channelTransferVO
                        .getTransferID(), PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()), phoneVO.getMsisdn(), senderVO.getStaffUserDetails()
                        .getUserName() };
                } else {
                    array = new String[] { BTSLUtil.getMessage(p_requestVO.getLocale(), txnList), BTSLUtil.getMessage(p_requestVO.getLocale(), balList), channelTransferVO
                        .getTransferID(), PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()), phoneVO.getMsisdn() };
                }
                p_requestVO.setMessageArguments(array);
                p_requestVO.setMessageCode(smsKey);
                p_requestVO.setTransactionID(channelTransferVO.getTransferID());
                _allowedSendMessGatw = BTSLUtil.NullToString(Constants.getProperty("C2C_SEN_MSG_REQD_GW"));
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue() && ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MESSAGE_TO_PRIMARY_REQUIRED)).booleanValue()) {
                    if (!PretupsI.KEYWORD_TYPE_ADMIN.equals(p_requestVO.getServiceType()) && p_requestVO.isSenderMessageRequired()) {
                        if (primaryPhoneVO_S != null) {
                            final Locale locale1 = new Locale(primaryPhoneVO_S.getPhoneLanguage(), primaryPhoneVO_S.getCountry());
                            final String senderMessage = BTSLUtil.getMessage(locale1, p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
                            pushMessage = new PushMessage(senderVO.getPrimaryMsisdn(), senderMessage, p_requestVO.getRequestIDStr(), p_requestVO.getRequestGatewayCode(),
                                locale1);
                            pushMessage.push();
                        }
                    }
                }
                if (senderVO.isStaffUser() && (!senderVO.getUserPhoneVO().getMsisdn().equals(p_requestVO.getMessageSentMsisdn()) || p_requestVO.getMessageGatewayVO()
                    .getGatewayType().equals(PretupsI.GATEWAY_TYPE_PHYSICAL_POS))) {
                    final Locale parentLocale = new Locale(senderVO.getUserPhoneVO().getPhoneLanguage(), senderVO.getUserPhoneVO().getCountry());
                    final String[] arrMsg = { BTSLUtil.getMessage(parentLocale, txnList), BTSLUtil.getMessage(parentLocale, balList), channelTransferVO.getTransferID(), PretupsBL
                        .getDisplayAmount(channelTransferVO.getNetPayableAmount()), phoneVO.getMsisdn(), senderVO.getStaffUserDetails().getUserName() };
                    final String senderMessage = BTSLUtil.getMessage(parentLocale, PretupsErrorCodesI.CHNL_RETURN_SUCCESS_STAFF, arrMsg);
                    pushMessage = new PushMessage(p_requestVO.getFilteredMSISDN(), senderMessage, p_requestVO.getRequestIDStr(), p_requestVO.getRequestGatewayCode(),
                        parentLocale);
                    pushMessage.push();
                }
                final String senderMessage = BTSLUtil.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
                if (BTSLUtil.isStringIn(p_requestVO.getRequestGatewayCode(), _allowedSendMessGatw)) {
                    pushMessage = new PushMessage(p_requestVO.getFilteredMSISDN(), senderMessage, p_requestVO.getRequestIDStr(), p_requestVO.getRequestGatewayCode(),
                        p_requestVO.getLocale());
                    pushMessage.push();
                }
                if ((!reqruestGW.equalsIgnoreCase(p_requestVO.getRequestGatewayCode())) && ((messageLength > 0) && (senderMessage.length() < messageLength))) {
                    pushMessage = new PushMessage(p_requestVO.getFilteredMSISDN(), senderMessage, p_requestVO.getRequestIDStr(), reqruestGW, p_requestVO.getLocale());
                    pushMessage.push();
                }
                return;
            }
            // transaction rollback
           
            mcomCon.partialRollback();
            p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER);
            throw new BTSLBaseException("C2CReturnController", "process", PretupsErrorCodesI.ERROR_USER_TRANSFER);
        }// end try main
        catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                	mcomCon.finalRollback();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            loggerValue.setLength(0);
            loggerValue.append("BTSLBaseException ");
            loggerValue.append(be.getMessage());
            _log.error("process",  loggerValue);
            if (be.getMessageList() != null && !be.getMessageList().isEmpty()) {
                final String[] array = { BTSLUtil.getMessage(p_requestVO.getLocale(), (ArrayList) be.getMessageList()) };
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
        }// end catch BTSLBaseException
        catch (Exception e) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                	mcomCon.finalRollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(METHOD_NAME, ee);
            }
            loggerValue.setLength(0);
            loggerValue.append("BTSLBaseException ");
            loggerValue.append( e.getMessage());
            _log.error("process",  loggerValue);
            _log.errorTrace(METHOD_NAME, e);
            loggerValue.setLength(0);
            loggerValue.append("Exception:");
            loggerValue.append(  e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2CReturnController[process]", "", "", "",
            		loggerValue.toString());
            p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER);
            return;
        }// end catch Exception
        finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("C2CReturnController#process");
        		mcomCon=null;
        		}
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
        }// end finally
    }// end process

    /**
     * Method prepareTransferProfileVO
     * This method construct the VO for the Txn
     * 
     * @param p_senderVO
     * @param p_receiverVO
     * @param p_productList
     * @param p_curDate
     * @return ChannelTransferVO
     * @throws BTSLBaseException
     */
    private ChannelTransferVO prepareTransferProfileVO(ChannelUserVO p_senderVO, ChannelUserVO p_receiverVO, ArrayList p_productList, Date p_curDate) throws BTSLBaseException {
    	StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append( " Entered  p_senderVO: ");
        	loggerValue.append(p_senderVO);
        	loggerValue.append(" p_receiverVO:" );
        	loggerValue.append(p_receiverVO);
        	loggerValue.append(" p_productList:");
        	loggerValue.append(p_productList.size());
        	loggerValue.append(" p_curDate:" );
        	loggerValue.append(p_curDate);
            _log.debug("prepareTransferProfileVO",loggerValue);
        }

        final ChannelTransferVO channelTransferVO = new ChannelTransferVO();

        channelTransferVO.setNetworkCode(p_senderVO.getNetworkID());
        channelTransferVO.setNetworkCodeFor(p_senderVO.getNetworkID());
        channelTransferVO.setGraphicalDomainCode(p_senderVO.getGeographicalCode());
        channelTransferVO.setDomainCode(p_senderVO.getDomainID());
        channelTransferVO.setCategoryCode(p_senderVO.getCategoryCode());
        channelTransferVO.setSenderGradeCode(p_senderVO.getUserGrade());
        channelTransferVO.setReceiverGradeCode(p_receiverVO.getUserGrade());
        channelTransferVO.setFromUserID(p_senderVO.getUserID());
        // channelTransferVO.setFromUserCode(p_senderVO.getUserCode());
        channelTransferVO.setToUserID(p_receiverVO.getUserID());
        // channelTransferVO.setToUserCode(p_receiverVO.getUserCode());
        channelTransferVO.setTransferDate(p_curDate);
        channelTransferVO.setCommProfileSetId(p_senderVO.getCommissionProfileSetID());
        channelTransferVO.setCommProfileVersion(p_senderVO.getCommissionProfileSetVersion());
        channelTransferVO.setCreatedOn(p_curDate);
        channelTransferVO.setCreatedBy(p_senderVO.getActiveUserID());
        channelTransferVO.setModifiedOn(p_curDate);
        channelTransferVO.setModifiedBy(p_senderVO.getActiveUserID());
        channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
        channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN);
        channelTransferVO.setTransferInitatedBy(p_senderVO.getUserID());
        channelTransferVO.setSenderTxnProfile(p_senderVO.getTransferProfileID());
        channelTransferVO.setReceiverTxnProfile(p_receiverVO.getTransferProfileID());
        // channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_STK);
        channelTransferVO.setReceiverCategoryCode(p_receiverVO.getCategoryCode());
        channelTransferVO.setTransferCategory(p_senderVO.getTransferCategory());

        // adding the some additional information for sender/reciever
        channelTransferVO.setReceiverGgraphicalDomainCode(p_receiverVO.getGeographicalCode());
        channelTransferVO.setReceiverDomainCode(p_receiverVO.getDomainID());
        channelTransferVO.setToUserCode(PretupsBL.getFilteredMSISDN(p_receiverVO.getUserCode()));
        channelTransferVO.setFromUserCode(PretupsBL.getFilteredMSISDN(p_senderVO.getMsisdn()));
        channelTransferVO.setDualCommissionType(p_senderVO.getDualCommissionType());
        
        ChannelTransferItemsVO channelTransferItemsVO ;
        long totRequestQty = 0, totMRP = 0, totPayAmt = 0, totNetPayAmt = 0, totTax1 = 0, totTax2 = 0, totTax3 = 0;
        long commissionQty = 0, senderDebitQty = 0, receiverCreditQty = 0;
        String productType = null;
        for (int i = 0, k = p_productList.size(); i < k; i++) {
            channelTransferItemsVO = (ChannelTransferItemsVO) p_productList.get(i);
            totRequestQty += PretupsBL.getSystemAmount(channelTransferItemsVO.getRequestedQuantity());
			if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType())) {
                totMRP += (channelTransferItemsVO.getReceiverCreditQty()) * Long.parseLong(PretupsBL.getDisplayAmount(channelTransferItemsVO.getUnitValue()));
            } else {
                totMRP += (Double.parseDouble(channelTransferItemsVO.getRequestedQuantity()) * channelTransferItemsVO.getUnitValue());
            }
            totPayAmt += channelTransferItemsVO.getPayableAmount();
            totNetPayAmt += channelTransferItemsVO.getNetPayableAmount();
            totTax1 += channelTransferItemsVO.getTax1Value();
            totTax2 += channelTransferItemsVO.getTax2Value();
            totTax3 += channelTransferItemsVO.getTax3Value();
            commissionQty += channelTransferItemsVO.getCommQuantity();
            senderDebitQty += channelTransferItemsVO.getSenderDebitQty();
            receiverCreditQty += channelTransferItemsVO.getReceiverCreditQty();
            productType = channelTransferItemsVO.getProductType();
        }
    	channelTransferVO.setProductType(productType);
        channelTransferVO.setRequestedQuantity(totRequestQty);
        channelTransferVO.setTransferMRP(totMRP);
        channelTransferVO.setPayableAmount(totPayAmt);
        channelTransferVO.setNetPayableAmount(totNetPayAmt);
        channelTransferVO.setTotalTax1(totTax1);
        channelTransferVO.setTotalTax2(totTax2);
        channelTransferVO.setTotalTax3(totTax3);
        channelTransferVO.setType(PretupsI.CHANNEL_TYPE_C2C);
        channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN);
        channelTransferVO.setCommQty(PretupsBL.getSystemAmount(commissionQty));
        channelTransferVO.setSenderDrQty(PretupsBL.getSystemAmount(senderDebitQty));
        channelTransferVO.setReceiverCrQty(PretupsBL.getSystemAmount(receiverCreditQty));

        channelTransferVO.setChannelTransferitemsVOList(p_productList);
        channelTransferVO.setActiveUserId(p_senderVO.getActiveUserID());
        
        if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USERWISE_LOAN_ENABLE)).booleanValue()) {
    			ArrayList<UserLoanVO> userLoanVOList = new ArrayList<UserLoanVO>();
    			if(p_senderVO.getUserLoanVOList()!=null)
	   				userLoanVOList.addAll(p_senderVO.getUserLoanVOList());
	   			if(p_receiverVO.getUserLoanVOList()!=null)
	   				userLoanVOList.addAll(p_receiverVO.getUserLoanVOList());
    	   			
    				channelTransferVO.setUserLoanVOList(userLoanVOList);
    		
    	} 
        
        if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue()){
        	ArrayList<ChannelSoSVO>  chnlSoSVOList = new ArrayList<> ();
        	chnlSoSVOList.add(new ChannelSoSVO(p_senderVO.getUserID(),p_senderVO.getMsisdn(),p_senderVO.getSosAllowed(),p_senderVO.getSosAllowedAmount(),p_senderVO.getSosThresholdLimit()));
        	chnlSoSVOList.add(new ChannelSoSVO(p_receiverVO.getUserID(),p_receiverVO.getMsisdn(),p_receiverVO.getSosAllowed(),p_receiverVO.getSosAllowedAmount(),p_receiverVO.getSosThresholdLimit()));
        	channelTransferVO.setChannelSoSVOList(chnlSoSVOList);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("prepareTransferProfileVO", " Exited  ");
        }

        return channelTransferVO;
    }// end of prepareTransferProfileVO
}
