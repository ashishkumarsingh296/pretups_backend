package com.restapi.user.service;


import io.swagger.v3.oas.annotations.Parameter;

import java.io.IOException;
import java.sql.Connection;
import java.util.Arrays;
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

import io.swagger.v3.oas.annotations.Parameter;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.receiver.RestReceiver;
import com.btsl.pretups.channel.transfer.requesthandler.C2CTransferDetailsVO;
import com.btsl.pretups.channel.transfer.requesthandler.C2CVoucherTransferDetailsController;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
/*@Path("")
@Api(value = "C2C Transfer Details")*/
@io.swagger.v3.oas.annotations.tags.Tag(name = "${C2CTransferDetailController.name}", description = "${C2CTransferDetailController.desc}")//@Api(tags= "C2C Receiver")
@RestController
@RequestMapping(value = "/v1/c2cReceiver")
public class C2CTransferDetailController {

	protected final Log log = LogFactory.getLog(getClass().getName());
/*	@Context
	private HttpServletRequest httpServletRequest;

	@POST
	@Path("/c2s-receiver/c2cviewvc")
	@Consumes(value = MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "C2C Transfer Detail", response = PretupsResponse.class)*/
	
	@PostMapping(value = "/c2cviewvc", consumes =MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "C2C Transfer Detail", response = ViewTxnDetailsResponseVO.class,
			authorizations = {
    	            @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = ViewTxnDetailsResponseVO.class),
	        @ApiResponse(code = 201, message = "Created"),
	        @ApiResponse(code = 400, message = "Bad Request"),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })
	*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${c2cviewvc.summary}", description="${c2cviewvc.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ViewTxnDetailsResponseVO.class))
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

	public ViewTxnDetailsResponseVO processCP2PUserRequest(
	    		HttpServletRequest httpServletRequest,
				@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
	    		@Parameter(description = SwaggerAPIDescriptionI.C2C_TRANSFER_DETAILS)
	    		@RequestBody C2CTransferDetailsVO c2CTransferDetailsVO, HttpServletResponse response1) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		final String methodName = "processCP2PUserRequest_C2CTransferDetailController";
		RequestVO response;
        RestReceiver restReceiver;
        RestReceiver.updateRequestIdChannel();
        final String requestIDStr = String.valueOf(RestReceiver.getRequestIdChannel());
        restReceiver = new RestReceiver();
       	ViewTxnDetailsResponseVO responsenew = new ViewTxnDetailsResponseVO();
        OAuthUser oAuthUser= null;
        OAuthUserData oAuthUserData =null;
        Connection con = null;
		MComConnectionI mcomCon = null;
		ChannelUserDAO channelUserDAO = null;
        
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
			c2CTransferDetailsVO.setServicePort(oAuthUser.getServicePort());
			c2CTransferDetailsVO.setReqGatewayCode(oAuthUser.getReqGatewayCode());
			c2CTransferDetailsVO.setReqGatewayLoginId(oAuthUser.getReqGatewayLoginId());
			c2CTransferDetailsVO.setReqGatewayPassword(oAuthUser.getReqGatewayPassword());
			c2CTransferDetailsVO.setReqGatewayType(oAuthUser.getReqGatewayType());
			c2CTransferDetailsVO.setSourceType(oAuthUser.getSourceType());
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();	
			//setting password in requestVO from oAuthUser
			c2CTransferDetailsVO.getData().setPassword( oAuthUser.getData().getPassword() );
			channelUserDAO = new ChannelUserDAO();
			
			RequestVO requestVO = new RequestVO();
			requestVO.setRequestGatewayType(PretupsI.REQUEST_SOURCE_TYPE_REST);
			requestVO.setRequestGatewayCode(PretupsI.REQUEST_SOURCE_TYPE_REST);
		    requestVO.setRequestMessageOrigStr(PretupsRestUtil.convertObjectToJSONString(c2CTransferDetailsVO));
		    String msisdn = oAuthUser.getData().getMsisdn();
//			ChannelUserVO channelUserVO = channelUserDAO.loadChannelUserDetails(con, msisdn);
//			if(channelUserVO==null) {
//				channelUserVO = channelUserDAO.loadChannelUserDetails(con, c2CTransferDetailsVO.getData().getMsisdn());
//			}
			requestVO.setUserLoginId(oAuthUser.getData().getLoginid());
			  C2CVoucherTransferDetailsController c2CVoucherTransferDetailsController = new C2CVoucherTransferDetailsController();
			  response =  c2CVoucherTransferDetailsController.process1(requestVO);
           if(!PretupsErrorCodesI.TXN_SUCCESSFUL.equals(response.getMessageCode()))
           { 
        	responsenew.setStatus(HttpStatus.SC_BAD_REQUEST);
        	responsenew.setMessageCode(response.getMessageCode());
        	responsenew.setDataObj(response.getChannelTransferVO());
         	String resmsg = RestAPIStringParser.getMessage(
 					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), response.getMessageCode(),
 					null);
        	responsenew.setMessage(resmsg);
           }else {
        	responsenew.setStatus(200);
        	responsenew.setMessage(response.getSenderReturnMessage());
        	responsenew.setMessageCode(response.getMessageCode());
        	responsenew.setDataObj(response.getChannelTransferVO());
           }
        	
        return responsenew;
   
    }catch (BTSLBaseException be) {
		//PretupsResponse<JsonNode> baseResponse= new PretupsResponse<JsonNode>() ;
		log.error(methodName, "BTSLBaseException " + be.getMessage());
        log.errorTrace(methodName, be);
        if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
          	 response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
          }
           else{
           response1.setStatus(HttpStatus.SC_BAD_REQUEST);
           }
        responsenew.setMessageCode(be.getMessageKey());
        	String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), be.getMessageKey(),
					null);

        	responsenew.setStatus(be.getErrorCode());
        	responsenew.setMessage(resmsg);
            return responsenew;
        
    } catch (Exception e) {
    	//PretupsResponse<JsonNode> baseResponse= new PretupsResponse<JsonNode>() ;
    	log.error(methodName, "Exception " + e.getMessage());
        log.errorTrace(methodName, e);
        responsenew.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
        String resmsg = RestAPIStringParser.getMessage(
				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.REQ_NOT_PROCESS,
				null);
        responsenew.setStatus(PretupsI.UNABLE_TO_PROCESS_REQUEST);
        responsenew.setMessage(resmsg);
    	   response1.setStatus(HttpStatus.SC_BAD_REQUEST);
        return responsenew;
    }
	
	finally {
        LogFactory.printLog(methodName, " Exited ", log);
        if (mcomCon != null) {
			mcomCon.close("C2CTransferDetailController#processCP2PUserRequest");
			mcomCon = null;
		}
    }
	}
}
