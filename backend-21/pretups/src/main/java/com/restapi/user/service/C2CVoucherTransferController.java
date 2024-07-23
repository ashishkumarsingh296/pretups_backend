
package com.restapi.user.service;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

import java.io.IOException;
import java.sql.Connection;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.ws.rs.core.MediaType;

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
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.receiver.RestReceiver;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.common.PretupsRestI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.AESEncryptionUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

/*@Path("")
@Api(value="C2C Voucher Transfer")*/
@io.swagger.v3.oas.annotations.tags.Tag(name = "${C2CVoucherTransferController.name}", description = "${C2CVoucherTransferController.desc}")//@Api(tags= "C2C Receiver")
@RestController
@RequestMapping(value = "/v1/c2cReceiver")
public class  C2CVoucherTransferController {
	protected final Log _log = LogFactory.getLog(getClass().getName());

//	@Context
//	private HttpServletRequest httpServletRequest;
	/*@POST
	@Path("/c2cVomsTrf")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)*/
	
	@PostMapping(value = "/c2cvomstrf", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "C2C Voucher Transfer", response = PretupsResponse.class,
			authorizations = {
    	            @Authorization(value = "Authorization")})
	
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = PretupsResponse.class),
	        @ApiResponse(code = 201, message = "Created"),
	        @ApiResponse(code = 400, message = "Bad Request"),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })
    */
	@io.swagger.v3.oas.annotations.Operation(summary = "${c2cvomstrf.summary}", description="${c2cvomstrf.description}",

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
    		HttpServletRequest httpServletRequest,
    		@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
    		@Parameter(description = SwaggerAPIDescriptionI.C2C_TRANSFER_VOUCHER_INITIATE)
    		@RequestBody C2CVoucherTransferRequestVO c2CVoucherTransferRequestVO, HttpServletResponse response1) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		final String methodName = "processCP2PUserRequest_C2CVoucherTransferController";
		PretupsResponse<JsonNode> response;
        RestReceiver restReceiver;
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
			 * @throws BTSLBaseException
			 */
        	
        	oAuthUser = new OAuthUser();
			oAuthUserData =new OAuthUserData();
			oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers,response1);
			c2CVoucherTransferRequestVO.setServicePort(oAuthUser.getServicePort());
			c2CVoucherTransferRequestVO.setReqGatewayCode(oAuthUser.getReqGatewayCode());
			c2CVoucherTransferRequestVO.setReqGatewayLoginId(oAuthUser.getReqGatewayLoginId());
			c2CVoucherTransferRequestVO.setReqGatewayPassword(oAuthUser.getReqGatewayPassword());
			c2CVoucherTransferRequestVO.setReqGatewayType(oAuthUser.getReqGatewayType());
			c2CVoucherTransferRequestVO.setSourceType(oAuthUser.getSourceType());
			
			c2CVoucherTransferRequestVO.getDatabuyvcr().setLoginid(oAuthUser.getData().getLoginid());
			
			//setting password in requestVO from oAuthUser - removed
			c2CVoucherTransferRequestVO.getDatabuyvcr().setPassword(oAuthUser.getData().getPassword());
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			channelUserVO = userDao.loadAllUserDetailsByLoginID(con, oAuthUser.getData().getLoginid());
			userPhoneVO = userDao.loadUserPhoneVO(con, channelUserVO.getUserID());
			if(!c2CVoucherTransferRequestVO.getDatabuyvcr().getPin().isEmpty()) {
				//Decrypting pin and setting back in requestVO
				String encryptedPin = c2CVoucherTransferRequestVO.getDatabuyvcr().getPin();
				String decryptedPin = AESEncryptionUtil.aesDecryptor(encryptedPin, Constants.A_KEY);
				c2CVoucherTransferRequestVO.getDatabuyvcr().setPin(decryptedPin);
			}
			if(userPhoneVO != null) {
				if(c2CVoucherTransferRequestVO.getDatabuyvcr().getPin().isEmpty() && (!userPhoneVO.isPinRequiredBool())) {
					c2CVoucherTransferRequestVO.getDatabuyvcr().setPin(oAuthUser.getData().getPin());
				}
			}
			
			
//			OAuthenticationUtil.validateTokenApi(headers);
			String jsonString = BTSLUtil.appendWebApiCall(PretupsRestUtil.convertObjectToJSONString(c2CVoucherTransferRequestVO));
        response = restReceiver.processCP2PRequestOperator(httpServletRequest,(JsonNode)(PretupsRestUtil.convertJSONToObject(jsonString, new TypeReference<JsonNode>(){})), PretupsRestI.C2C_VOMSTRF,requestIDStr);
        return response;
    }catch (BTSLBaseException be) {
		PretupsResponse<JsonNode> baseResponse= new PretupsResponse<JsonNode>() ;
		
		_log.error(methodName, "BTSLBaseException " + be.getMessage());
        _log.errorTrace(methodName, be);

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

}
