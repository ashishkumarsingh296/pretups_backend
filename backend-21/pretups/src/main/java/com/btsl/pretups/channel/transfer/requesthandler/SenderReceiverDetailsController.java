package com.btsl.pretups.channel.transfer.requesthandler;


import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
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
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.common.TypesI;
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
import com.btsl.pretups.channel.transfer.businesslogic.C2CTrfUserDetailsReqMsg;
import com.btsl.pretups.channel.transfer.businesslogic.C2CTrfUsersDetailsMsg;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.channel.transfer.businesslogic.TrfUserDetailsParentVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserProductDetails;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.common.PretupsRestI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.product.businesslogic.NetworkProductDAO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.BarredUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.OracleUtil;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;

/*@Path("")*/
@io.swagger.v3.oas.annotations.tags.Tag(name = "${SenderReceiverDetailsController.name}", description = "${SenderReceiverDetailsController.desc}")//@Api(tags= "C2S Receiver", value="Sender Receiver Details")
@RestController
@RequestMapping(value = "/v1/c2sReceiver")
public class SenderReceiverDetailsController implements ServiceKeywordControllerI{
	private static Log _log = LogFactory.getLog(SenderReceiverDetailsController.class.getName());
	public static OperatorUtilI _operatorUtil = null;
	static {/*
		final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
		try {
			_operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
		} catch (Exception e) {
			_log.errorTrace("static", e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					" C2CTransferController [initialize]", "", "", "",
					"Exception while loading the class at the call:" + e.getMessage());
		}
	*/}
	
	/*@Context
 	private HttpServletRequest httpServletRequest;*/
 	/*@POST
 	@Path("/userinfo")*/
	@PostMapping(value ="/userinfo", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody
//    @Consumes(value = MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
 	 /*@ApiOperation(tags= "C2S Receiver", value = "Sender Receiver Details", response = PretupsResponse.class,
 			authorizations = {
    	            @Authorization(value = "Authorization")
    })
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = BaseResponse.class),
	        @ApiResponse(code = 201, message = "Created"),
	        @ApiResponse(code = 400, message = "Bad Request"),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${userinfo.summary}", description="${userinfo.description}",

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
    		@Parameter(description = SwaggerAPIDescriptionI.GET_SENDER_RECEIVER_INFO)
    		@RequestBody TrfUserDetailsParentVO trfUserDetailsParentVO, HttpServletResponse response1) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
 		
		final String methodName = "processCP2PUserRequest_SenderReceiverDetailsController";
		PretupsResponse<JsonNode> response;
        RestReceiver restReceiver;
        RestReceiver.updateRequestIdChannel();
        final String requestIDStr = String.valueOf(RestReceiver.getRequestIdChannel());
        restReceiver = new RestReceiver();
        
        OAuthUser oAuthUser= null;
        OAuthUserData oAuthUserData =null;
        
        Connection con = null;
		MComConnectionI mcomCon = null;
        
        try {
			/*
			 * Authentication
			 * @throws BTSLBaseException
			 */
			//OAuthenticationUtil.validateTokenApi(headers);
        	
        	oAuthUser = new OAuthUser();
			oAuthUserData =new OAuthUserData();
			oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers,response1);
			trfUserDetailsParentVO.setServicePort(oAuthUser.getServicePort());
			trfUserDetailsParentVO.setReqGatewayCode(oAuthUser.getReqGatewayCode());
			trfUserDetailsParentVO.setReqGatewayLoginId(oAuthUser.getReqGatewayLoginId());
			trfUserDetailsParentVO.setReqGatewayPassword(oAuthUser.getReqGatewayPassword());
			trfUserDetailsParentVO.setReqGatewayType(oAuthUser.getReqGatewayType());
			trfUserDetailsParentVO.setSourceType(oAuthUser.getSourceType());
			
			//setting password from oAUTh
			trfUserDetailsParentVO.getData().setPassword( oAuthUser.getData().getPassword() );
			
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
 			UserDAO userDAO = new UserDAO();
 			ChannelUserVO userVO = userDAO.loadAllUserDetailsByLoginID(con, oAuthUser.getData().getLoginid());
 			if(userVO.getUserType().equals(TypesI.STAFF_USER_TYPE)) {
 				userVO = userDAO.loadUserDetailsFormUserID(con, userVO.getParentID());
 				trfUserDetailsParentVO.getData().setPassword( BTSLUtil.decryptText(userVO.getPassword()) );
 				trfUserDetailsParentVO.getData().setLoginid(userVO.getLoginID());
 				trfUserDetailsParentVO.getData().setExtcode(userVO.getExternalCode());
 			}

        response = restReceiver.processCP2PRequestOperator(httpServletRequest,(JsonNode)(PretupsRestUtil.convertJSONToObject(PretupsRestUtil.convertObjectToJSONString(trfUserDetailsParentVO), new TypeReference<JsonNode>(){})), PretupsRestI.USERINFO,requestIDStr);
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
            	String resmsg = RestAPIStringParser.getMessage(
    					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), be.getMessageKey(),
    					null);
  
            	baseResponse.setStatusCode(be.getErrorCode());
            	baseResponse.setMessage(resmsg);
                return baseResponse;
            
        } catch (Exception e) {
        	PretupsResponse<JsonNode> baseResponse= new PretupsResponse<JsonNode>() ;
        	_log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            baseResponse.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.REQ_NOT_PROCESS,
					null);
        	baseResponse.setStatusCode(PretupsI.UNABLE_TO_PROCESS_REQUEST);
        	baseResponse.setMessage(resmsg);
        	   response1.setStatus(HttpStatus.SC_BAD_REQUEST);
            return baseResponse;
        }
		
		finally {
            LogFactory.printLog(methodName, " Exited ", _log);
        }
    }
 	
	@Override
	public void process(RequestVO p_requestVO) {
		final String METHOD_NAME = "process";
		if (_log.isDebugEnabled()) {
			_log.debug("process", " Entered p_requestVO=" + p_requestVO);
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		ChannelUserDAO channelUserDAO = null;
		final Date curDate = new Date();
		ChannelUserVO loggedInUserVO = null, receiverUserVO = null;
		C2CTrfUsersDetailsMsg c2CTrfUsersDetailsMsg = new C2CTrfUsersDetailsMsg();
		try {
			
			

			final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
			try {
				_operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
			} catch (Exception e) {
				_log.errorTrace("static", e);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
						" C2CTransferController [initialize]", "", "", "",
						"Exception while loading the class at the call:" + e.getMessage());
			}
		
			
			channelUserDAO = new ChannelUserDAO();
			NetworkProductDAO networkProductDAO = new NetworkProductDAO();
			final String messageArr[] = p_requestVO.getRequestMessageArray();
			if (_log.isDebugEnabled()) {
				_log.debug("process", " Message Array " + messageArr);
			}
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			Gson gson = new Gson();	
			C2CTrfUserDetailsReqMsg resMsg = gson.fromJson(p_requestVO.getRequestMessage(), C2CTrfUserDetailsReqMsg.class);
			String c2cTransferType = resMsg.getC2ctrftype();
			loggedInUserVO = (ChannelUserVO)(p_requestVO.getSenderVO());
			String args1[] = {loggedInUserVO.getMsisdn()};
			if(!(c2cTransferType.equals("C") || c2cTransferType.equals("B") || c2cTransferType.equals("W") || c2cTransferType.equals("R")))
				throw new BTSLBaseException("SenderReceiverDetailsController", "process",
						PretupsErrorCodesI.C2C_INVALID_TRANSFER_MODE, 0,
						new String[] { resMsg.getC2ctrftype() }, null);
			
			if (loggedInUserVO.getCommissionProfileApplicableFrom().after(curDate)) {
				p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_COMMISSION_PROFILE_NOT_APPLICABLE);
				p_requestVO.setMessageArguments(args1);
				throw new BTSLBaseException("SenderReceiverDetailsController", "process",
						PretupsErrorCodesI.ERROR_USER_COMMISSION_PROFILE_NOT_APPLICABLE, 0, args1, null);
			}
			
			if(!BTSLUtil.isNullString(resMsg.getMsisdn()) && !BTSLUtil.isNullString(resMsg.getPin()) && loggedInUserVO.getUserPhoneVO().getPinRequired().equals(PretupsI.YES))
			try {
				ChannelUserBL.validatePIN(con, loggedInUserVO, resMsg.getPin());
			} catch (BTSLBaseException be) {
				if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN))
						|| (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
					OracleUtil.commit(con);
				}
				throw be;
			}
			
			boolean senderAllowed = false;
			UserStatusVO senderStatusVO = null;
			senderStatusVO = (UserStatusVO) UserStatusCache.getObject(loggedInUserVO.getNetworkID(), loggedInUserVO.getCategoryCode(), loggedInUserVO.getUserType(), p_requestVO.getRequestGatewayType());
			if (senderStatusVO != null) {
				final String senderStatusAllowed = senderStatusVO.getUserSenderAllowed();
				final String status[] = senderStatusAllowed.split(",");
				for (int i = 0; i < status.length; i++) {
					if (status[i].equals(loggedInUserVO.getStatus())) {
						senderAllowed = true;
					}
				}
			}
			
			if (!senderAllowed) {
				p_requestVO.setMessageCode((c2cTransferType.equals("C") || c2cTransferType.equals("R")) ? PretupsErrorCodesI.CHNL_ERROR_SENDER_NOTALLOWED : PretupsErrorCodesI.CHNL_ERROR_RECEIVER_NOTALLOWED);
				p_requestVO.setMessageArguments(args1);
				throw new BTSLBaseException("SenderReceiverDetailsController", "process",
						(c2cTransferType.equals("C") || c2cTransferType.equals("R")) ? PretupsErrorCodesI.CHNL_ERROR_SENDER_NOTALLOWED : PretupsErrorCodesI.CHNL_ERROR_RECEIVER_NOTALLOWED, 0, args1, null);
			}
			
			String receiverUserCode = p_requestVO.getReceiverMsisdn();
			receiverUserCode = _operatorUtil.addRemoveDigitsFromMSISDN(PretupsBL.getFilteredMSISDN(receiverUserCode));
			if(receiverUserCode==null) throw new BTSLBaseException(PretupsErrorCodesI.REQVIASMS_CHANNEL_USER_NOT_EXIST);
			if (!BTSLUtil.isValidMSISDN(receiverUserCode)) {
				p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_REC_USERCODE);
				throw new BTSLBaseException("C2CTransferController", "process",
						PretupsErrorCodesI.ERROR_INVALID_REC_USERCODE);
			}
			
			/* Barring checks start */
			final String msisdnPrefix = PretupsBL.getMSISDNPrefix(receiverUserCode);
			final NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
			if (networkPrefixVO == null) {
				throw new BTSLBaseException("C2CTransferController", "process",
						PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_UNSUPPORTED_NETWORK, 0,
						new String[] { receiverUserCode }, null);
			}

			final BarredUserDAO barredUserDAO = new BarredUserDAO();
			String requesterMsisdn = loggedInUserVO.getMsisdn();
			final String requesterMsisdnPrefix = PretupsBL.getMSISDNPrefix(requesterMsisdn);
			final NetworkPrefixVO requesterNetworkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(requesterMsisdnPrefix);
			if (requesterNetworkPrefixVO == null) {
				throw new BTSLBaseException("C2CTransferController", "process",
						PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_UNSUPPORTED_NETWORK, 0,
						new String[] { requesterMsisdn }, null);
			}
			
			if (c2cTransferType.equals("B") || c2cTransferType.equals("W")) {
				if (barredUserDAO.isExists(con, PretupsI.C2S_MODULE, networkPrefixVO.getNetworkCode(), receiverUserCode,
						PretupsI.USER_TYPE_SENDER, null)) {
					throw new BTSLBaseException("C2CTransferController", "process",
							PretupsErrorCodesI.ERROR_USER_TRANSFER_CHNL_SENDER_BAR, 0,
							new String[] { receiverUserCode }, null);
				}
				if (barredUserDAO.isExists(con, PretupsI.C2S_MODULE, requesterNetworkPrefixVO.getNetworkCode(), requesterMsisdn,
						PretupsI.USER_TYPE_RECEIVER, null)) {
					throw new BTSLBaseException("C2CTransferController", "process",
							PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_RECEIVER_BAR, 0,
							new String[] { requesterMsisdn }, null);
				}
			}

			if (c2cTransferType.equals("C") || c2cTransferType.equals("R")) {
				if (barredUserDAO.isExists(con, PretupsI.C2S_MODULE, requesterNetworkPrefixVO.getNetworkCode(),
						requesterMsisdn, PretupsI.USER_TYPE_SENDER, null)) {
					throw new BTSLBaseException("C2CTransferController", "process",
							PretupsErrorCodesI.ERROR_USER_TRANSFER_CHNL_SENDER_BAR, 0,
							new String[] { requesterMsisdn }, null);
				}
				if (barredUserDAO.isExists(con, PretupsI.C2S_MODULE, networkPrefixVO.getNetworkCode(), receiverUserCode,
						PretupsI.USER_TYPE_RECEIVER, null)) {
					throw new BTSLBaseException("C2CTransferController", "process",
							PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_RECEIVER_BAR, 0,
							new String[] { receiverUserCode }, null);
				}
			}
			
			/* Barring checks end*/

			receiverUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, receiverUserCode, true, curDate, false);
			String args[] = {receiverUserCode};
			if (receiverUserVO == null) {
				p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_NOT_EXIST);
				p_requestVO.setMessageArguments(args);
				throw new BTSLBaseException("SenderReceiverDetailsController", "process", PretupsErrorCodesI.ERROR_USER_NOT_EXIST,
						0, args, null);
			}
			if ((c2cTransferType.equals("B") || c2cTransferType.equals("W")) && receiverUserVO.getOutSuspened() != null
					&& PretupsI.USER_TRANSFER_OUT_STATUS_SUSPEND.equals(receiverUserVO.getOutSuspened())) {
				p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_OUT_SUSPENDED);
				p_requestVO.setMessageArguments(args);
				throw new BTSLBaseException("SenderReceiverDetailsController", "process",
						PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_OUT_SUSPENDED, 0, args, null);
			}
			
			if ((c2cTransferType.equals("B") || c2cTransferType.equals("W")) && loggedInUserVO.getInSuspend() != null
					&& PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(loggedInUserVO.getInSuspend())) {
				p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_IN_SUSPENDED);
				p_requestVO.setMessageArguments(args);
				throw new BTSLBaseException("SenderReceiverDetailsController", "process",
						PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_IN_SUSPENDED, 0, new String[] {loggedInUserVO.getMsisdn()}, null);
			}
			
			boolean receiverAllowed = false;
			UserStatusVO receiverStatusVO = null;
			receiverStatusVO = (UserStatusVO) UserStatusCache.getObject(receiverUserVO.getNetworkID(), receiverUserVO.getCategoryCode(), receiverUserVO.getUserType(), p_requestVO.getRequestGatewayType());
			if (receiverStatusVO != null) {
				final String receiverStatusAllowed = receiverStatusVO.getUserReceiverAllowed();
				final String status[] = receiverStatusAllowed.split(",");
				for (int i = 0; i < status.length; i++) {
					if (status[i].equals(receiverUserVO.getStatus())) {
						receiverAllowed = true;
					}
				}
			}
			
			if (receiverUserVO == null) {
				throw new BTSLBaseException("SenderReceiverDetailsController", "process",
						PretupsErrorCodesI.ERROR_USERSTATUS_NOTCONFIGURED);
			} else if (!receiverAllowed) {
				p_requestVO.setMessageCode((c2cTransferType.equals("C") || c2cTransferType.equals("R")) ? PretupsErrorCodesI.CHNL_ERROR_RECEIVER_NOTALLOWED : PretupsErrorCodesI.CHNL_ERROR_SENDER_NOTALLOWED);
				p_requestVO.setMessageArguments(args);
				throw new BTSLBaseException("SenderReceiverDetailsController", "process",
						(c2cTransferType.equals("C") || c2cTransferType.equals("R")) ? PretupsErrorCodesI.CHNL_ERROR_RECEIVER_NOTALLOWED : PretupsErrorCodesI.CHNL_ERROR_SENDER_NOTALLOWED, 0, args, null);
			} else if (receiverUserVO.getCommissionProfileApplicableFrom().after(curDate)) {
				p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_COMMISSION_PROFILE_NOT_APPLICABLE);
				p_requestVO.setMessageArguments(args);
				throw new BTSLBaseException("SenderReceiverDetailsController", "process",
						PretupsErrorCodesI.ERROR_USER_COMMISSION_PROFILE_NOT_APPLICABLE, 0, args, null);
			}
			
			final ChannelTransferRuleDAO channelTransferRuleDAO = new ChannelTransferRuleDAO();
			ChannelTransferRuleVO channelTransferRuleVO = channelTransferRuleDAO.loadTransferRule(con, loggedInUserVO.getNetworkID(), loggedInUserVO.getDomainID(), (c2cTransferType.equals("C") || c2cTransferType.equals("R"))? loggedInUserVO.getCategoryCode() : receiverUserVO.getCategoryCode(),
					(c2cTransferType.equals("C") || c2cTransferType.equals("R"))? receiverUserVO.getCategoryCode() : loggedInUserVO.getCategoryCode(), PretupsI.TRANSFER_RULE_TYPE_CHANNEL, false);
            if (channelTransferRuleVO == null) 
            {
                p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER_RULE_NOT_DEFINE);
                String argmnts[] = new String[2];
                if((c2cTransferType.equals("C") || c2cTransferType.equals("R")))
                {
                	argmnts[0] = loggedInUserVO.getCategoryVO().getCategoryName();
                	argmnts[1] = receiverUserVO.getCategoryVO().getCategoryName();
                }
                else
                {
                	argmnts[0] = receiverUserVO.getCategoryVO().getCategoryName();
                	argmnts[1] = loggedInUserVO.getCategoryVO().getCategoryName();
                }
                p_requestVO.setMessageArguments(argmnts);
                throw new BTSLBaseException("SenderReceiverDetailsController", "process", PretupsErrorCodesI.ERROR_USER_TRANSFER_RULE_NOT_DEFINE, 0, argmnts, null);
            }
            
			boolean isOutsideHirearchy = false;
			if(c2cTransferType.equals("C") || c2cTransferType.equals("B"))
				isOutsideHirearchy = ChannelTransferBL.validateSenderAndReceiverWithXfrRule(con, c2cTransferType.equals("C") ? loggedInUserVO : receiverUserVO , c2cTransferType.equals("C") ? receiverUserVO : loggedInUserVO , true, null, false,
	                PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
			else
				isOutsideHirearchy = ChannelTransferBL.validateSenderAndReceiverWithXfrRule(con, c2cTransferType.equals("R") ? loggedInUserVO : receiverUserVO , c2cTransferType.equals("R") ? receiverUserVO : loggedInUserVO , true, null, false,
						c2cTransferType.equals("R") ? PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN : PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW);
			String trfRuleID = receiverUserVO.getTransferRuleID();
			if(c2cTransferType.equals("R") || c2cTransferType.equals("W"))
				trfRuleID = loggedInUserVO.getTransferRuleID();
			ArrayList productList = null;
			final Locale locale = p_requestVO.getLocale();
			if(c2cTransferType.equals("B"))
				productList = ChannelTransferBL.loadC2CXfrProductsWithXfrRule(con, receiverUserVO.getUserID(), receiverUserVO.getNetworkID(),
					loggedInUserVO.getCommissionProfileSetID(), curDate, trfRuleID, null, false, receiverUserVO.getUserName(), locale, null, PretupsI.TRANSFER_TYPE_C2C);
			else if(c2cTransferType.equals("W"))
				productList = ChannelTransferBL.loadC2CXfrProductsWithXfrRule(con, receiverUserVO.getUserID(), receiverUserVO.getNetworkID(),
						receiverUserVO.getCommissionProfileSetID(), curDate, trfRuleID, null, false, receiverUserVO.getUserName(), locale, null, PretupsI.TRANSFER_TYPE_C2C);
			else
				productList = ChannelTransferBL.loadC2CXfrProductsWithXfrRule(con, loggedInUserVO.getUserID(), loggedInUserVO.getNetworkID(),
						c2cTransferType.equals("C")?receiverUserVO.getCommissionProfileSetID() : loggedInUserVO.getCommissionProfileSetID() , curDate, trfRuleID, null, false, loggedInUserVO.getUserName(), locale, null, PretupsI.TRANSFER_TYPE_C2C);
			
			int productSize = productList.size();
			UserProductDetails[] userproductdetails = new UserProductDetails[productSize];
			for(int i=0;i<productList.size();i++)
			{
				ChannelTransferItemsVO channelTransferItemsVO = (ChannelTransferItemsVO)productList.get(i);
				userproductdetails[i] = new UserProductDetails();
				userproductdetails[i].setProductShortCode(String.valueOf(channelTransferItemsVO.getProductShortCode()));
				userproductdetails[i].setProductName(channelTransferItemsVO.getShortName());
				userproductdetails[i].setProductUserBalance(channelTransferItemsVO.getBalanceAsString());
				userproductdetails[i].setProductUserMinTransferValue(channelTransferItemsVO.getMinTransferValueAsString());
				userproductdetails[i].setProductUserMaxTransferValue(channelTransferItemsVO.getMaxTransferValueAsString());
			}
			
			c2CTrfUsersDetailsMsg.setUserproductdetails(userproductdetails);
			if(c2cTransferType.equals("C") || c2cTransferType.equals("R")){
				c2CTrfUsersDetailsMsg.setSenderUserName(loggedInUserVO.getUserName());
				c2CTrfUsersDetailsMsg.setSenderCommissionProfileID(loggedInUserVO.getCommissionProfileSetID());
				c2CTrfUsersDetailsMsg.setSenderCommissionProfileName(loggedInUserVO.getCommissionProfileSetName());
				c2CTrfUsersDetailsMsg.setSenderCommissionProfileSetVersion(loggedInUserVO.getCommissionProfileSetVersion());
				c2CTrfUsersDetailsMsg.setSenderCategoryID(loggedInUserVO.getCategoryCode());
				c2CTrfUsersDetailsMsg.setSenderCategoryName(loggedInUserVO.getCategoryVO().getCategoryName());
				c2CTrfUsersDetailsMsg.setSenderUserGradeCode(loggedInUserVO.getUserGrade());
				c2CTrfUsersDetailsMsg.setSenderUserGradeName(loggedInUserVO.getUserGradeName());
				c2CTrfUsersDetailsMsg.setSenderTransferProfileID(loggedInUserVO.getTransferProfileID());
				c2CTrfUsersDetailsMsg.setSenderTransferProfileName(loggedInUserVO.getTransferProfileName());
				c2CTrfUsersDetailsMsg.setSenderMsisdn(loggedInUserVO.getMsisdn());
				c2CTrfUsersDetailsMsg.setSenderDualCommission(loggedInUserVO.getDualCommissionType());
				
				c2CTrfUsersDetailsMsg.setReceiverUserName(receiverUserVO.getUserName());
				c2CTrfUsersDetailsMsg.setReceiverCommissionProfileID(receiverUserVO.getCommissionProfileSetID());
				c2CTrfUsersDetailsMsg.setReceiverCommissionProfileName(receiverUserVO.getCommissionProfileSetName());
				c2CTrfUsersDetailsMsg.setReceiverCommissionProfileSetVersion(receiverUserVO.getCommissionProfileSetVersion());
				c2CTrfUsersDetailsMsg.setReceiverCategoryID(receiverUserVO.getCategoryCode());
				c2CTrfUsersDetailsMsg.setReceiverCategoryName(receiverUserVO.getCategoryVO().getCategoryName());
				c2CTrfUsersDetailsMsg.setReceiverUserGradeCode(receiverUserVO.getUserGrade());
				c2CTrfUsersDetailsMsg.setReceiverUserGradeName(receiverUserVO.getUserGradeName());
				c2CTrfUsersDetailsMsg.setReceiverTransferProfileID(receiverUserVO.getTransferProfileID());
				c2CTrfUsersDetailsMsg.setReceiverTransferProfileName(receiverUserVO.getTransferProfileName());
				c2CTrfUsersDetailsMsg.setReceiverMsisdn(receiverUserVO.getMsisdn());
				c2CTrfUsersDetailsMsg.setReceiverDualCommission(receiverUserVO.getDualCommissionType());
			}
			
			else if(c2cTransferType.equals("B") || c2cTransferType.equals("W"))
			{
				c2CTrfUsersDetailsMsg.setSenderUserName(receiverUserVO.getUserName());
				c2CTrfUsersDetailsMsg.setSenderCommissionProfileID(receiverUserVO.getCommissionProfileSetID());
				c2CTrfUsersDetailsMsg.setSenderCommissionProfileName(receiverUserVO.getCommissionProfileSetName());
				c2CTrfUsersDetailsMsg.setSenderCommissionProfileSetVersion(receiverUserVO.getCommissionProfileSetVersion());
				c2CTrfUsersDetailsMsg.setSenderCategoryID(receiverUserVO.getCategoryCode());
				c2CTrfUsersDetailsMsg.setSenderCategoryName(receiverUserVO.getCategoryVO().getCategoryName());
				c2CTrfUsersDetailsMsg.setSenderUserGradeCode(receiverUserVO.getUserGrade());
				c2CTrfUsersDetailsMsg.setSenderUserGradeName(receiverUserVO.getUserGradeName());
				c2CTrfUsersDetailsMsg.setSenderTransferProfileID(receiverUserVO.getTransferProfileID());
				c2CTrfUsersDetailsMsg.setSenderTransferProfileName(receiverUserVO.getTransferProfileName());
				c2CTrfUsersDetailsMsg.setSenderMsisdn(receiverUserVO.getMsisdn());
				c2CTrfUsersDetailsMsg.setSenderDualCommission(receiverUserVO.getDualCommissionType());
				
				c2CTrfUsersDetailsMsg.setReceiverUserName(loggedInUserVO.getUserName());
				c2CTrfUsersDetailsMsg.setReceiverCommissionProfileID(loggedInUserVO.getCommissionProfileSetID());
				c2CTrfUsersDetailsMsg.setReceiverCommissionProfileName(loggedInUserVO.getCommissionProfileSetName());
				c2CTrfUsersDetailsMsg.setReceiverCommissionProfileSetVersion(loggedInUserVO.getCommissionProfileSetVersion());
				c2CTrfUsersDetailsMsg.setReceiverCategoryID(loggedInUserVO.getCategoryCode());
				c2CTrfUsersDetailsMsg.setReceiverCategoryName(loggedInUserVO.getCategoryVO().getCategoryName());
				c2CTrfUsersDetailsMsg.setReceiverUserGradeCode(loggedInUserVO.getUserGrade());
				c2CTrfUsersDetailsMsg.setReceiverUserGradeName(loggedInUserVO.getUserGradeName());
				c2CTrfUsersDetailsMsg.setReceiverTransferProfileID(loggedInUserVO.getTransferProfileID());
				c2CTrfUsersDetailsMsg.setReceiverTransferProfileName(loggedInUserVO.getTransferProfileName());
				c2CTrfUsersDetailsMsg.setReceiverMsisdn(loggedInUserVO.getMsisdn());
				c2CTrfUsersDetailsMsg.setReceiverDualCommission(loggedInUserVO.getDualCommissionType());
			}
			
			c2CTrfUsersDetailsMsg.setGeographyCode(receiverUserVO.getGeographicalCode());
			c2CTrfUsersDetailsMsg.setGeographyName(receiverUserVO.getGeographicalDesc());
			c2CTrfUsersDetailsMsg.setDomainCode(receiverUserVO.getDomainID());
			c2CTrfUsersDetailsMsg.setDomainName(receiverUserVO.getDomainName());
			//Setting part
			HashMap<String, C2CTrfUsersDetailsMsg> resMap= new HashMap<String, C2CTrfUsersDetailsMsg>();
	        resMap.put("userDetails", c2CTrfUsersDetailsMsg);
	        p_requestVO.setResponseMap(resMap);
		} catch (BTSLBaseException be) {
			p_requestVO.setSuccessTxn(false);
			p_requestVO.setMessageCode(be.getMessageKey());
			_log.error("process", "BTSLBaseException " + be.getMessage());
			_log.errorTrace(METHOD_NAME, be);
			if (be.isKey()) {
				p_requestVO.setMessageCode(be.getMessageKey());
				p_requestVO.setMessageArguments(be.getArgs());
			} else {
				p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
			}
		} catch (Exception e) {
			p_requestVO.setSuccessTxn(false);
			_log.error("process", "BTSLBaseException " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SenderReceiverDetailsController[process]", "", "", "",
                        "Exception:" + e.getMessage());
			p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
			return;
		} finally {
			if (mcomCon != null) {
				mcomCon.close("SenderReceiverDetailsController#process");
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) {
				_log.debug("process", " Exited ");
			}
		}
	}
}
