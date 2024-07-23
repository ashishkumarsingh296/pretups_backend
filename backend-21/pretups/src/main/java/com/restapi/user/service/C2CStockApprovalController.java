package com.restapi.user.service;


import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

import java.io.IOException;
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

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.receiver.RestReceiver;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.common.PretupsRestI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.util.OAuthenticationUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

/*@Path("")
@Api(value="C2C Stock")*/
@io.swagger.v3.oas.annotations.tags.Tag(name = "${C2CStockApprovalController.name}", description = "${C2CStockApprovalController.desc}")//@Api(tags= "C2C Receiver")
@RestController
@RequestMapping(value = "/v1/c2cReceiver")
public class C2CStockApprovalController {
	protected final Log _log = LogFactory.getLog(getClass().getName());


	/*@Context
	private HttpServletRequest httpServletRequest;
	
	@POST
	@Path("/c2cTrfAppr")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)*/
	@PostMapping(value = "/c2ctrfappr", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "C2C Stock Approval", response = PretupsResponse.class,
			authorizations = {
    	            @Authorization(value = "Authorization")})
@ApiResponses(value = {
    @ApiResponse(code = 200, message = "OK", response = PretupsResponse.class),
    @ApiResponse(code = 201, message = "Created"),
    @ApiResponse(code = 400, message = "Bad Request"),
    @ApiResponse(code = 401, message = "Unauthorized"),
    @ApiResponse(code = 404, message = "Not Found")
    })*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${c2ctrfappr.summary}", description="${c2ctrfappr.description}",

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
    		@Parameter(description = "C2C Stock Approval")
    		@RequestBody C2CStockApprovalRequestVO c2CStockApprovalRequestVO, HttpServletResponse response1) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException 
	{
		final String methodName = "processCP2PUserRequest_C2CStockApprovalController";
		PretupsResponse<JsonNode> response;
        RestReceiver restReceiver;
        RestReceiver.updateRequestIdChannel();
        final String requestIDStr = String.valueOf(RestReceiver.getRequestIdChannel());
        restReceiver = new RestReceiver();
        
        OAuthUser oAuthUser= null;
		OAuthUserData oAuthUserData =null;
		
		
        try {
			/*
			 * Authentication
			 * @throws BTSLBaseException
			 */
//			OAuthenticationUtil.validateTokenApi(headers);
        	
        	oAuthUser = new OAuthUser();
			oAuthUserData =new OAuthUserData();
			oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers,response1);
			c2CStockApprovalRequestVO.setServicePort(oAuthUser.getServicePort());
			c2CStockApprovalRequestVO.setReqGatewayCode(oAuthUser.getReqGatewayCode());
			c2CStockApprovalRequestVO.setReqGatewayLoginId(oAuthUser.getReqGatewayLoginId());
			c2CStockApprovalRequestVO.setReqGatewayPassword(oAuthUser.getReqGatewayPassword());
			c2CStockApprovalRequestVO.setReqGatewayType(oAuthUser.getReqGatewayType());
			c2CStockApprovalRequestVO.setSourceType(oAuthUser.getSourceType());
			//setting password from auth
			c2CStockApprovalRequestVO.getData().setPassword( oAuthUser.getData().getPassword() );
			
			
        response = restReceiver.processCP2PRequestOperator(httpServletRequest,(JsonNode)(PretupsRestUtil.convertJSONToObject(PretupsRestUtil.convertObjectToJSONString(c2CStockApprovalRequestVO), new TypeReference<JsonNode>(){})), PretupsRestI.C2C_TRF_APPR_ST,requestIDStr);
        if(response.getStatusCode()!=200)
     	   response1.setStatus(HttpStatus.SC_BAD_REQUEST);
     if(response!=null && response.getDataObject()!=null && response.getDataObject().get("txnstatus") != null){
     	if(!response.getDataObject().get("txnstatus").textValue().equals("200"))
     		response1.setStatus(HttpStatus.SC_BAD_REQUEST);
     }
        return response;
    }catch (BTSLBaseException be) {
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

}
