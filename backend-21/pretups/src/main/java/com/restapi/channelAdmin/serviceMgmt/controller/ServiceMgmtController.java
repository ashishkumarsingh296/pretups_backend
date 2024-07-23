package com.restapi.channelAdmin.serviceMgmt.controller;

import java.util.Arrays;
import java.util.Locale;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
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
import com.btsl.common.MasterErrorList;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.restapi.channelAdmin.serviceMgmt.requestVO.AddServiceMgmtReqVO;
import com.restapi.channelAdmin.serviceMgmt.responseVO.AddServiceMgmtRespVO;
import com.restapi.channelAdmin.serviceMgmt.responseVO.SearchServiceMgmtRespVO;
import com.restapi.channelAdmin.serviceMgmt.responseVO.ServiceMgmtInptRespVO;
import com.restapi.channelAdmin.serviceMgmt.service.ServiceMgmtServiceImpl;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${ServiceMgmtController.name}", description = "${ServiceMgmtController.desc}")//@Api(tags ="Service Management", value="Service Management")
@RestController	
@RequestMapping(value = "/v1/channeladmin/serviceManagement")
public class ServiceMgmtController {
	public static final Log log = LogFactory.getLog(ServiceMgmtController.class.getName());
	public static final String classname = "ServiceMgmtController";

	@Autowired
	private ServiceMgmtServiceImpl serviceMgmtI;
	
	@GetMapping(value = "/servicMgmtInputValues", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Input drop down values data", response = ServiceMgmtInptRespVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = ServiceMgmtInptRespVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${servicMgmtInputValues.summary}", description="${servicMgmtInputValues.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ServiceMgmtInptRespVO.class))
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

	public ServiceMgmtInptRespVO servicMgmtInputValues(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse response1,
			HttpServletRequest httprequest
			) throws Exception {	
		
		
	
	final String methodName = "servicMgmtInputValues";
	if (log.isDebugEnabled()) {
		log.debug(methodName, "servicMgmtInputValues ");
	}
	Locale locale = LocaleContextHolder.getLocale();
	ServiceMgmtInptRespVO response=new ServiceMgmtInptRespVO();
	String loginID = null;
	try {
		OAuthUser oAuthUserData = new OAuthUser();
		oAuthUserData.setData(new OAuthUserData());
		OAuthenticationUtil.validateTokenApi(oAuthUserData, headers, response1);
		loginID=oAuthUserData.getData().getLoginid();
		response = serviceMgmtI.getServiceMgmtInputValues(loginID,locale);
		
	} catch (BTSLBaseException be) {
		log.error("", "Exceptin:e=" + be);
		log.errorTrace(methodName, be);
		String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(),be.getArgs());
		response.setMessageCode(be.getMessageKey());
		response.setMessage(msg);

		if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
			response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
			response.setStatus(HttpStatus.SC_UNAUTHORIZED);
		} else {
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
	} finally {

		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exiting:=" + methodName);
		}

	}
	return response;
}


	
	
	
	@GetMapping(value = "/searchServiceMgmtData", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Search service managemet data", response = SearchServiceMgmtRespVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = SearchServiceMgmtRespVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${searchServiceMgmtData.summary}", description="${searchServiceMgmtData.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = SearchServiceMgmtRespVO.class))
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
	public SearchServiceMgmtRespVO searchServiceMgmtData(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse response1,
			@Parameter(description = SwaggerAPIDescriptionI.SERVICE_TYPE, example = "",required = true)
			@RequestParam("serviceType") String serviceType,
			@Parameter(description = SwaggerAPIDescriptionI.DOMAIN_CODE, example = "",required = true)
			@RequestParam("domainCode") String domainCode,
			HttpServletRequest httprequest
			) throws Exception {	
		
		
	
	final String methodName = "searchServiceMgmtData";
	if (log.isDebugEnabled()) {
		log.debug(methodName, "searchServiceMgmtData ");
	}
	Locale locale = LocaleContextHolder.getLocale();
	SearchServiceMgmtRespVO response=new SearchServiceMgmtRespVO();
	String loginID = null;
	try {
		OAuthUser oAuthUserData = new OAuthUser();
		oAuthUserData.setData(new OAuthUserData());
		OAuthenticationUtil.validateTokenApi(oAuthUserData, headers, response1);
		loginID=oAuthUserData.getData().getLoginid();
		response = serviceMgmtI.searchServiceMgmtData(loginID,serviceType,domainCode, locale);
		
	} catch (BTSLBaseException be) {
		log.error("", "Exceptin:e=" + be);
		log.errorTrace(methodName, be);
		String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(),be.getArgs());
		response.setMessageCode(be.getMessageKey());
		response.setMessage(msg);

		if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
			response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
			response.setStatus(HttpStatus.SC_UNAUTHORIZED);
		} else {
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
	} finally {

		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exiting:=" + methodName);
		}

	}
	return response;
}

	
	
	
	
	
	
	@PostMapping(value = "/addserviceMgmtData", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Add service mgmt data", response = AddServiceMgmtReqVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = AddServiceMgmtReqVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${addserviceMgmtData.summary}", description="${addserviceMgmtData.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = AddServiceMgmtRespVO.class))
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
	public AddServiceMgmtRespVO addserviceMgmtData(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest,
			@Parameter(description = "ADD_SERVICE_MGMT", required = true) @RequestBody AddServiceMgmtReqVO requestVO)
			throws Exception {
		
		final String methodName = "addserviceMgmtData";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "addserviceMgmtData request reached  ");
		}
		MasterErrorList masterError=null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		AddServiceMgmtRespVO response=new AddServiceMgmtRespVO();
		String loginID = null;
		try {
			OAuthUser oAuthUserData = new OAuthUser();
			oAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(oAuthUserData, headers, response1);
			response = serviceMgmtI.addserviceMgmtData(requestVO, oAuthUserData.getData().getLoginid(),locale);
			
		} catch (BTSLBaseException be) {
			log.error("", "Exceptin:e=" + be);
			log.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), be.getArgs());
			response.setMessageCode(be.getMessageKey());
			response.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		} finally {

			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting:=" + methodName);
			}

		}
		return response;
	}
	

	
	
	
	
}
