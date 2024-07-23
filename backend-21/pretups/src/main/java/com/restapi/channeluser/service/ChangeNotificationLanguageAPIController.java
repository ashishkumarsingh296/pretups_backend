package com.restapi.channeluser.service;

import java.util.HashMap;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

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

import com.btsl.common.BaseResponse;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

/**
 * 
 * @author md.sohail
 *
 */

@io.swagger.v3.oas.annotations.tags.Tag(name = "${ChangeNotificationLanguageAPIController.name}", description = "${ChangeNotificationLanguageAPIController.desc}")//@Api(tags = "Change Notification Language", defaultValue = "Change Notification Language")
@RestController
@RequestMapping(value = "/v1/changeNotificationLang")
public class ChangeNotificationLanguageAPIController {

	public static final Log log = LogFactory.getLog(ChangeNotificationLanguageAPIController.class.getName());

	@Context
	private HttpServletRequest httpServletRequest;

	@Autowired
	private ChangeNotificationLanguageAPIService changeNotificationLanguageAPIService;

	@GetMapping(value = "/loadAllUsers", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Load all users in logged in user hierarchy", response = NotificationLanguageResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") }, notes = "Api Info:" + ("\n")
					+ "1.Provide userName with category code" + ("\n")
					+ "2.\'%' allowed in userName to search all hierarchy users with in given category")

	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = NotificationLanguageResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${loadAllUsers.summary}", description="${loadAllUsers.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = NotificationLanguageResponseVO.class))
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

	public NotificationLanguageResponseVO loadUsers(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = "user category", required = true) @RequestParam("categoryCode") String categoryCode,
			@Parameter(description = "user name", required = false) @RequestParam("userName") String userName,
			HttpServletResponse responseSwag) throws Exception {

		final String methodName = "loadUsers";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		HashMap<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("categoryCode", categoryCode);
		requestMap.put("userName", userName);
		NotificationLanguageResponseVO responseVO = changeNotificationLanguageAPIService.loadUsersDetails(headers,
				requestMap, responseSwag);

		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exiting: " + responseVO);
		}
		return responseVO;
	}

	@GetMapping(value = "/loadUserPhoneList", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Load all mobile number details", response = NotificationLanguageResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") }, notes = "Api Info:" + ("\n")
					+ "1. Search by \'MSISDN\' will load only one msisdn (searched msisdn) details." + ("\n")
					+ "2. \'ADVANCED\': " + ("\n")
					+ "2a. In advance search, user can search either with msisdn or user name with category" + ("\n")
					+ "2b. In case of multiple user with same user name, an error response will be given.")

	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = NotificationLanguageResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${loadUserPhoneList.summary}", description="${loadUserPhoneList.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = NotificationLanguageResponseVO.class))
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

	public NotificationLanguageResponseVO loadUserPhoneDetails(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = "search by", required = true)// allowableValues = "MSISDN,ADVANCED")
			@RequestParam("searchBy") String searchBy,
			@Parameter(description = "mobile number", required = false) @RequestParam("msisdn") String msisdn,
			@Parameter(description = "user name", required = false) @RequestParam("userName") String userName,
			@Parameter(description = "user category", required = false) @RequestParam("categoryCode") String categoryCode,
			HttpServletResponse responseSwag) throws Exception {

		final String methodName = "loadUserPhoneDetails";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		HashMap<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("searchBy", searchBy);
		requestMap.put("msisdn", msisdn);
		requestMap.put("userName", userName);
		requestMap.put("categoryCode", categoryCode);
		NotificationLanguageResponseVO responseVO = changeNotificationLanguageAPIService
				.loadUserPhoneDetailsByMsisdn(headers, requestMap, responseSwag);

		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exiting: " + responseVO);
		}
		return responseVO;
	}
	
	@PostMapping(value = "/updateNotificationLang", consumes = MediaType.APPLICATION_JSON,produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Load all mobile number details", response = BaseResponse.class, authorizations = {
			@Authorization(value = "Authorization") }, notes = "Api Info:" + ("\n")
					+ "1. Update Notification language of multiple msisdn of single user.")

	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = BaseResponse.class),
			@ApiResponse(code = 400, message = "Bad Request"), 
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") 
			})
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${updateNotificationLang.summary}", description="${updateNotificationLang.description}",

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

	public BaseResponse updateNotificationLanguage(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@RequestBody NotificationLanguageRequestVO requestVO,
			HttpServletResponse responseSwag) throws Exception {

		final String methodName = "loadUserPhoneDetails";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		BaseResponse responseVO = changeNotificationLanguageAPIService
				.changeNotificationLanguage(headers, requestVO, responseSwag);

		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exiting: " + responseVO);
		}
		return responseVO;
	}


}
