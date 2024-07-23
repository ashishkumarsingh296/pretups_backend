package com.restapi.networkadmin;

import java.util.Locale;
import java.util.Optional;


import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.util.OAuthenticationUtil;
import com.restapi.networkadmin.requestVO.UpdateControlPreferenceVO;
import com.restapi.networkadmin.responseVO.ControlPreferenceListsResponseVO;
import com.restapi.networkadmin.serviceI.ControlPreferenceService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${ControlPreferenceController.name}", description = "${ControlPreferenceController.desc}")//@Api(tags="Network Admin", value="Network Admin")
@RestController
@RequestMapping(value="/v1/networkadmin")
public class ControlPreferenceController {
	
	protected final Log log = LogFactory.getLog(getClass().getName());

	@Autowired
	private ControlPreferenceService controlPreferenceService; 
	
	@GetMapping(value="/fetchctrlpreflist", produces=MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Control Preference Lists",
    response = ControlPreferenceListsResponseVO.class,
    authorizations = {
        @Authorization(value = "Authorization")})
	@ApiResponses(value = {
				@ApiResponse(code = 200, message = "OK", response = ControlPreferenceListsResponseVO.class),
				@ApiResponse(code = 400, message = "Bad Request" ),
				@ApiResponse(code = 401, message = "Unauthorized"),
				@ApiResponse(code = 404, message = "Not Found")
				})
	*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${fetchctrlpreflist.summary}", description="${fetchctrlpreflist.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ControlPreferenceListsResponseVO.class))
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

	public ControlPreferenceListsResponseVO fetchCtrlPreferenceList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse responseSwag,
			@Parameter(description = "moduleCode", required = false) @RequestParam("moduleCode") Optional<String> moduleCode,
			@Parameter(description = "controlCode", required = false) @RequestParam("controlCode") Optional<String> controlCode,
			@Parameter(description = "preferenceCode", required = false) @RequestParam("preferenceCode") Optional<String> preferenceCode

			) {
		final String methodName =  "fetchCtrlPreferenceList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		ControlPreferenceListsResponseVO response = new ControlPreferenceListsResponseVO();
		OAuthUser oAuthUser =null;
		String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
	    String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
	    Locale locale = new Locale(defaultLanguage, defaultCountry);
	    try {
			oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			String moduleCodeString = moduleCode.map(Object::toString).orElse(null);
			String controlCodeString = controlCode.map(Object::toString).orElse(null);
			String preferenceCodeString = preferenceCode.map(Object::toString).orElse(null);
			response = controlPreferenceService.fetchCtrlPreferenceLists(locale, moduleCodeString,controlCodeString,preferenceCodeString,oAuthUser.getData().getLoginid(),responseSwag);

	    }
	    catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);
			if (be.getMessage().equalsIgnoreCase("1080001") || be.getMessage().equalsIgnoreCase("1080002")
					|| be.getMessage().equalsIgnoreCase("1080003") || be.getMessage().equalsIgnoreCase("241023")
					|| be.getMessage().equalsIgnoreCase("241018")) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
			String resmsg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
			response.setMessageCode(be.getMessage());
			response.setMessage(resmsg);

		}
	    catch (Exception ex) {
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(PretupsI.RESPONSE_FAIL);
			log.errorTrace(methodName, ex);
			log.error(methodName, "Some error occured: " + ex.getMessage());
		}finally
		{
			
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, " Exited ");
	        }
		}
		return response;
	}
	
	
	@PostMapping(value = "/updatectrlpref", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody

	/*@ApiOperation(value = "Modify or Delete Control Preference"
			, response = BaseResponseMultiple.class, authorizations = {
					@Authorization(value = "Authorization") })

	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = BaseResponse.class),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"), 
			@ApiResponse(code = 404, message = "Not Found") })
	*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${updatectrlpref.summary}", description="${updatectrlpref.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = BaseResponse.class))
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
	public BaseResponse updateControlPreference(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse responseSwag,
			@RequestBody UpdateControlPreferenceVO updateControlPreferenceVO) 
			
	{	
		final String methodName = "updateControlPreference";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		BaseResponse response = new BaseResponse();
		String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
	    String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
	    Locale locale = new Locale(defaultLanguage, defaultCountry);
	   
		
		try {

			OAuthUser oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			
			
			response = controlPreferenceService.updateControlPreference( locale,responseSwag, updateControlPreferenceVO, oAuthUser.getData().getMsisdn());

		}
		 catch (BTSLBaseException be) {
				log.error(methodName, "Exception:e=" + be);
				log.errorTrace(methodName, be);
				if (be.getMessage().equalsIgnoreCase("1080001") || be.getMessage().equalsIgnoreCase("1080002")
						|| be.getMessage().equalsIgnoreCase("1080003") || be.getMessage().equalsIgnoreCase("241023")
						|| be.getMessage().equalsIgnoreCase("241018")) {
					responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
					response.setStatus(HttpStatus.SC_UNAUTHORIZED);
				} else {
					responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
					response.setStatus(HttpStatus.SC_BAD_REQUEST);
				}
				String resmsg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
				response.setMessageCode(be.getMessage());
				response.setMessage(resmsg);

			}
		    catch (Exception ex) {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(PretupsI.RESPONSE_FAIL);
				log.errorTrace(methodName, ex);
				log.error(methodName, "Some error occured: " + ex.getMessage());
			}finally
			{
				
		        if (log.isDebugEnabled()) {
		            log.debug(methodName, " Exited ");
		        }
			}
			return response;
		}
	
	
	
}
