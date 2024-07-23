package com.restapi.user.service;

import java.util.Arrays;
import java.util.Locale;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.util.OAuthenticationUtil;
import com.restapi.channeluser.service.ChannelUserTransferController;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${TransactionPinManagementController.name}", description = "${TransactionPinManagementController.desc}")//@Api(tags="Transaction Pin Management",value="Transaction Pin Management")
@RestController
@RequestMapping(value = "/v1/txPinManagement")
public class TransactionPinManagementController {
	
	private static final Log log = LogFactory.getLog(ChannelUserTransferController.class.getName());
    private static final String  classname = "TransactionPinManagementController";
	
	@Autowired
	private TransactionPinManagementService service;

    /**
	 * @author Vamshikrishna.v
	 * @param headers
	 * @param response1
	 * @param httprequests
	 * @return
	 * @throws Exception
	 */

	@GetMapping(value = "/isPinChangeOnTxRequired", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Checking pin change on transaction",
			authorizations = {
		            @Authorization(value = "Authorization")},
	
	response = TransactionPinManagementResponseVO.class)

	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = PasswordChangeOnLoginVO.class),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${isPinChangeOnTxRequired.summary}", description="${isPinChangeOnTxRequired.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = TransactionPinManagementResponseVO.class))
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


	public TransactionPinManagementResponseVO isPinChangeOnTxRequired(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse response1,
			HttpServletRequest httpServletRequest,
			@Parameter(description = "msisdn", required = true) @RequestParam("msisdn") String msisdn)
			throws Exception {
		final String methodName = "isPinChangeOnTxRequired";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
        TransactionPinManagementResponseVO response=new TransactionPinManagementResponseVO();
		OAuthUser oAuthUser= null;
		OAuthUserData oAuthUserData =null;
		
		try {
			oAuthUser = new OAuthUser();
			oAuthUserData =new OAuthUserData();
			oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers,response1);
			
			//code modified by anand starts
			if(oAuthUser.getData().getMsisdn()!=null) {
				msisdn=oAuthUser.getData().getMsisdn();
			}
			//code modified by anand ends
			
			response=service.validatePinModifyRequired(msisdn);
			
		}catch (BTSLBaseException be) {
			log.error(classname, "Exceptin:e=" + be);
			log.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			response.setMessageCode(be.getMessageKey());
			response.setMessage(msg);
			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(methodName, e);
			response.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
			if (response.getMessage() == null) {
				String resmsg = RestAPIStringParser.getMessage(
				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
				PretupsErrorCodesI.REQ_NOT_PROCESS, null);
				response.setMessage(resmsg);
			}

			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exiting:=" + methodName);
		}	
            return response;
	}
}