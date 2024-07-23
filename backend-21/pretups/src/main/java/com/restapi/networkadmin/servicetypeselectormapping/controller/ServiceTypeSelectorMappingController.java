package com.restapi.networkadmin.servicetypeselectormapping.controller;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

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
import com.btsl.common.BaseResponseRedoclyCommon;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.Constants;
import com.btsl.util.OAuthenticationUtil;
import com.restapi.networkadmin.servicetypeselectormapping.requestVO.ModifyServiceTypeSelectorMappingRequestVO;
import com.restapi.networkadmin.servicetypeselectormapping.requestVO.ServiceTypeSelectorMappingRequestVO;
import com.restapi.networkadmin.servicetypeselectormapping.responseVO.SaveServiceTypeSelectorMappingResponseVO;
import com.restapi.networkadmin.servicetypeselectormapping.responseVO.ServiceTypeSelectorMappingDropDownResponseVO;
import com.restapi.networkadmin.servicetypeselectormapping.responseVO.ServiceTypeSelectorMappingListResponseVO;
import com.restapi.networkadmin.servicetypeselectormapping.responseVO.ViewServiceTypeSelectorMappingResponseVO;
import com.restapi.networkadmin.servicetypeselectormapping.serviceI.ServiceTypeSelectorMappingServiceI;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${ServiceTypeSelectorMappingController.name}", description = "${ServiceTypeSelectorMappingController.desc}") // @Api(tags
																																								// =
																																								// "Network
																																								// Admin")
@RestController
@RequestMapping(value = "/v1/networkadmin/servicetypeselectormapping/")
public class ServiceTypeSelectorMappingController {

	@Autowired
	private ServiceTypeSelectorMappingServiceI service;
	public static final Log LOG = LogFactory.getLog(ServiceTypeSelectorMappingController.class.getName());
	public static final String CLASS_NAME = "ServiceTypeSelectorMappingController";

	@GetMapping(value = "/loadsrvtypedropdown", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*
	@ApiOperation(value = "Load  service type lIST", response = ServiceTypeSelectorMappingDropDownResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = ServiceTypeSelectorMappingDropDownResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })

	 */

	@Operation(summary = "${ServiceTypeSelectorMappingController.loadsrvtypedropdown.name}", description = "${ServiceTypeSelectorMappingController.loadsrvtypedropdown.des}",

			responses = {
					@ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ServiceTypeSelectorMappingDropDownResponseVO.class))) }

					),

					@ApiResponse(responseCode = Constants.API_BAD_REQ_RESPONSE_CODE, description = Constants.API_BAD_REQ_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class))

									, examples = {
									@ExampleObject(value = BaseResponseRedoclyCommon.BAD_REQUEST) }

							) }),
					@ApiResponse(responseCode = Constants.API_UNAUTH_RESPONSE_CODE, description = Constants.API_UNAUTH_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
									@ExampleObject(value = BaseResponseRedoclyCommon.UNAUTH) }

							) }),
					@ApiResponse(responseCode = Constants.API_NOT_FOUND_RESPONSE_CODE, description = Constants.API_NOT_FOUND_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
									@ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND) }

							) }),
					@ApiResponse(responseCode = Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = Constants.API_INTERNAL_ERROR_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
									@ExampleObject(value = BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR) }) }) })

	public ServiceTypeSelectorMappingDropDownResponseVO loadServicetypeList(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest) throws Exception {

		final String METHOD_NAME = "loadServicetypeList";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered");
		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		UserVO userVO = null;
		UserDAO userDAO = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		ServiceTypeSelectorMappingDropDownResponseVO response = new ServiceTypeSelectorMappingDropDownResponseVO();
		try {

			/*
			 * Authentication
			 * 
			 * @throws BTSLBaseException
			 */

			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			userDAO = new UserDAO();
			String loginID = OAuthUserData.getData().getLoginid();
			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
			ArrayList serviceTypeList = service.loadservicetypeList(con, userVO);
			if (serviceTypeList == null && serviceTypeList.size() <= 0) {
				throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SERVICE_TYPE_BLANK, "");

			}
			response.setServiceTypeList(serviceTypeList);

			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.LOAD_SERVICE_TYPE_SELECTOR_MAPPING_DROP_DOWN_SUCCESSFULLY, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.LOAD_SERVICE_TYPE_SELECTOR_MAPPING_DROP_DOWN_SUCCESSFULLY);

		} catch (BTSLBaseException e) {
			LOG.error(METHOD_NAME, "Exceptin:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			String msg = RestAPIStringParser.getMessage(locale, e.getMessageKey(), null);
			response.setMessageCode(e.getMessageKey());
			response.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(e.getMessage())) {
				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		} catch (Exception e) {
			LOG.error(METHOD_NAME, "Exceptin:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.LOAD_SERVICE_TYPE_SELECTOR_MAPPING_DROP_DOWN_FAILED, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.LOAD_SERVICE_TYPE_SELECTOR_MAPPING_DROP_DOWN_FAILED);
		} finally {

			if (mcomCon != null) {
				mcomCon.close(CLASS_NAME + "#" + METHOD_NAME);
				mcomCon = null;
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug(METHOD_NAME, "Exiting forward=");
			}
		}
		return response;
	}

	@GetMapping(value = "/load", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Load  service type  selector mapping lIST", response = ServiceTypeSelectorMappingListResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = ServiceTypeSelectorMappingListResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })*/

	@Operation(summary = "${ServiceTypeSelectorMappingController.load.name}", description = "${ServiceTypeSelectorMappingController.load.desc}",

			responses = {
					@ApiResponse(responseCode =Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ServiceTypeSelectorMappingListResponseVO.class))) }

					),

					@ApiResponse(responseCode = Constants.API_BAD_REQ_RESPONSE_CODE, description = Constants.API_BAD_REQ_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class))

									, examples = {
									@ExampleObject(value = BaseResponseRedoclyCommon.BAD_REQUEST) }

							) }),
					@ApiResponse(responseCode = Constants.API_UNAUTH_RESPONSE_CODE, description = Constants.API_UNAUTH_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
									@ExampleObject(value = BaseResponseRedoclyCommon.UNAUTH) }

							) }),
					@ApiResponse(responseCode = Constants.API_NOT_FOUND_RESPONSE_CODE, description = Constants.API_NOT_FOUND_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
									@ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND) }

							) }),
					@ApiResponse(responseCode = Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = Constants.API_INTERNAL_ERROR_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
									@ExampleObject(value = BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR) }) }) })

	public ServiceTypeSelectorMappingListResponseVO loadServiceTypeSelectorMappingList(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest,
			@Parameter(description = "serviceType", required = true) @RequestParam("serviceType") String serviceType)
			throws Exception {

		final String METHOD_NAME = "loadServiceTypeSelectorMappingList";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered");
		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		UserVO userVO = null;
		UserDAO userDAO = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		ServiceTypeSelectorMappingListResponseVO response = new ServiceTypeSelectorMappingListResponseVO();
		try {

			/*
			 * Authentication
			 *
			 * @throws BTSLBaseException
			 */

			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			userDAO = new UserDAO();
			String loginID = OAuthUserData.getData().getLoginid();
			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
			response = service.loadServiceTypeSelectorMappingList(con, userVO, serviceType, response);
			response.setStatus((HttpStatus.SC_OK));
			if (response.getServiceSelectorMappingList().size() != 0) {
				String resmsg = RestAPIStringParser.getMessage(locale,
						PretupsErrorCodesI.LOAD_SERVICE_TYPE_SELECTOR_MAPPING_LIST_SUCCESSFULLY, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.LOAD_SERVICE_TYPE_SELECTOR_MAPPING_LIST_SUCCESSFULLY);
			}
			if (response.getServiceSelectorMappingList().size() == 0) {
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.NO_MAPPING_FOUND, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.NO_MAPPING_FOUND);
			}

		} catch (BTSLBaseException e) {
			LOG.error(METHOD_NAME, "Exceptin:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			if(response.getMessage() == null) {
				String msg = RestAPIStringParser.getMessage(locale, e.getMessageKey(), null);
				response.setMessageCode(e.getMessageKey());
				response.setMessage(msg);
			}

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(e.getMessage())) {
				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		} catch (Exception e) {
			LOG.error(METHOD_NAME, "Exceptin:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.LOAD_SERVICE_TYPE_SELECTOR_MAPPING_LIST_FAILED, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.LOAD_SERVICE_TYPE_SELECTOR_MAPPING_LIST_FAILED);
		} finally {

			if (mcomCon != null) {
				mcomCon.close(CLASS_NAME + "#" + METHOD_NAME);
				mcomCon = null;
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug(METHOD_NAME, "Exiting forward=");
			}
		}
		return response;
	}

	@PostMapping(value = "/add", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Add  service type  selector mapping", response = SaveServiceTypeSelectorMappingResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = SaveServiceTypeSelectorMappingResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })*/

	@Operation(summary = "${ServiceTypeSelectorMappingController.add.name}", description = "${ServiceTypeSelectorMappingController.add.desc}",

			responses = {
					@ApiResponse(responseCode =Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SaveServiceTypeSelectorMappingResponseVO.class))) }

					),

					@ApiResponse(responseCode = Constants.API_BAD_REQ_RESPONSE_CODE, description = Constants.API_BAD_REQ_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class))

									, examples = {
									@ExampleObject(value = BaseResponseRedoclyCommon.BAD_REQUEST) }

							) }),
					@ApiResponse(responseCode = Constants.API_UNAUTH_RESPONSE_CODE, description = Constants.API_UNAUTH_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
									@ExampleObject(value = BaseResponseRedoclyCommon.UNAUTH) }

							) }),
					@ApiResponse(responseCode = Constants.API_NOT_FOUND_RESPONSE_CODE, description = Constants.API_NOT_FOUND_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
									@ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND) }

							) }),
					@ApiResponse(responseCode = Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = Constants.API_INTERNAL_ERROR_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
									@ExampleObject(value = BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR) }) }) })

	public SaveServiceTypeSelectorMappingResponseVO AddServiceTypeSelectorMapping(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest,
			@RequestBody ServiceTypeSelectorMappingRequestVO requestVO) throws Exception {

		final String METHOD_NAME = "AddServiceTypeSelectorMapping";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered");
		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		UserVO userVO = null;
		UserDAO userDAO = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		SaveServiceTypeSelectorMappingResponseVO response = new SaveServiceTypeSelectorMappingResponseVO();
		try {

			/*
			 * Authentication
			 *
			 * @throws BTSLBaseException
			 */

			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			userDAO = new UserDAO();
			String loginID = OAuthUserData.getData().getLoginid();
			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
			response = service.addServiceTypeSelectorMapping(con, userVO, requestVO, response);

		} catch (BTSLBaseException e) {
			LOG.error(METHOD_NAME, "Exceptin:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			if(response.getMessage() == null) {
				String msg = RestAPIStringParser.getMessage(locale, e.getMessageKey(), null);
				response.setMessageCode(e.getMessageKey());
				response.setMessage(msg);
			}
			if (Arrays.asList(PretupsI.OAUTHCODES).contains(e.getMessage())) {
				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		} catch (Exception e) {
			LOG.error(METHOD_NAME, "Exceptin:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.SERVICE_TYPE_SELECTOR_MAPPING_ADD_FAILED, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.SERVICE_TYPE_SELECTOR_MAPPING_ADD_FAILED);
		} finally {

			if (mcomCon != null) {
				mcomCon.close(CLASS_NAME + "#" + METHOD_NAME);
				mcomCon = null;
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug(METHOD_NAME, "Exiting forward=");
			}
		}
		return response;
	}

	@PostMapping(value = "/modify", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Modify  service type  selector mapping", response = SaveServiceTypeSelectorMappingResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = SaveServiceTypeSelectorMappingResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })*/

	@Operation(summary = "${ServiceTypeSelectorMappingController.modify.name}", description = "${ServiceTypeSelectorMappingController.modify.desc}",

			responses = {
					@ApiResponse(responseCode =Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SaveServiceTypeSelectorMappingResponseVO.class))) }

					),

					@ApiResponse(responseCode = Constants.API_BAD_REQ_RESPONSE_CODE, description = Constants.API_BAD_REQ_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class))

									, examples = {
									@ExampleObject(value = BaseResponseRedoclyCommon.BAD_REQUEST) }

							) }),
					@ApiResponse(responseCode = Constants.API_UNAUTH_RESPONSE_CODE, description = Constants.API_UNAUTH_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
									@ExampleObject(value = BaseResponseRedoclyCommon.UNAUTH) }

							) }),
					@ApiResponse(responseCode = Constants.API_NOT_FOUND_RESPONSE_CODE, description = Constants.API_NOT_FOUND_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
									@ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND) }

							) }),
					@ApiResponse(responseCode = Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = Constants.API_INTERNAL_ERROR_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
									@ExampleObject(value = BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR) }) }) })

	public SaveServiceTypeSelectorMappingResponseVO modifyServiceTypeSelectorMapping(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest,
			@RequestBody ModifyServiceTypeSelectorMappingRequestVO requestVO) throws Exception {

		final String METHOD_NAME = "ModifyServiceTypeSelectorMapping";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered");
		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		UserVO userVO = null;
		UserDAO userDAO = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		SaveServiceTypeSelectorMappingResponseVO response = new SaveServiceTypeSelectorMappingResponseVO();
		try {

			/*
			 * Authentication
			 *
			 * @throws BTSLBaseException
			 */

			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			userDAO = new UserDAO();
			String loginID = OAuthUserData.getData().getLoginid();
			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
			response = service.modifyServiceTypeSelectorMapping(con, userVO, requestVO, response);

		} catch (BTSLBaseException e) {
			LOG.error(METHOD_NAME, "Exceptin:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			String msg = RestAPIStringParser.getMessage(locale, e.getMessageKey(), null);
			response.setMessageCode(e.getMessageKey());
			response.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(e.getMessage())) {
				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		} catch (Exception e) {
			LOG.error(METHOD_NAME, "Exceptin:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.SERVICE_TYPE_SELECTOR_MAPPING_MODIFY_FAILED, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.SERVICE_TYPE_SELECTOR_MAPPING_MODIFY_FAILED);
		} finally {

			if (mcomCon != null) {
				mcomCon.close(CLASS_NAME + "#" + METHOD_NAME);
				mcomCon = null;
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug(METHOD_NAME, "Exiting forward=");
			}
		}
		return response;
	}

	@GetMapping(value = "/view", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "View  service type  selector mapping", response = ViewServiceTypeSelectorMappingResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = ViewServiceTypeSelectorMappingResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })*/
	@Operation(summary = "${ServiceTypeSelectorMappingController.view.name}", description = "${ServiceTypeSelectorMappingController.view.desc}",

			responses = {
					@ApiResponse(responseCode =Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ViewServiceTypeSelectorMappingResponseVO.class))) }

					),

					@ApiResponse(responseCode = Constants.API_BAD_REQ_RESPONSE_CODE, description = Constants.API_BAD_REQ_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class))

									, examples = {
									@ExampleObject(value = BaseResponseRedoclyCommon.BAD_REQUEST) }

							) }),
					@ApiResponse(responseCode = Constants.API_UNAUTH_RESPONSE_CODE, description = Constants.API_UNAUTH_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
									@ExampleObject(value = BaseResponseRedoclyCommon.UNAUTH) }

							) }),
					@ApiResponse(responseCode = Constants.API_NOT_FOUND_RESPONSE_CODE, description = Constants.API_NOT_FOUND_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
									@ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND) }

							) }),
					@ApiResponse(responseCode = Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = Constants.API_INTERNAL_ERROR_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
									@ExampleObject(value = BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR) }) }) })


	public ViewServiceTypeSelectorMappingResponseVO viewServiceTypeSelectorMapping(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest,
			@Parameter(description = "sNO", required = true) @RequestParam("sNO") String sNO) throws Exception {

		final String METHOD_NAME = "viewServiceTypeSelectorMapping";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered");
		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		UserVO userVO = null;
		UserDAO userDAO = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		ViewServiceTypeSelectorMappingResponseVO response = new ViewServiceTypeSelectorMappingResponseVO();
		try {

			/*
			 * Authentication
			 *
			 * @throws BTSLBaseException
			 */

			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			userDAO = new UserDAO();
			String loginID = OAuthUserData.getData().getLoginid();
			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
			service.viewServiceTypeSelectorMapping(con, userVO, sNO, response);

			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.LOAD_SERVICE_TYPE_SELECTOR_MAPPING_SUCCESSFULLY, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.LOAD_SERVICE_TYPE_SELECTOR_MAPPING_SUCCESSFULLY);

		} catch (BTSLBaseException e) {
			LOG.error(METHOD_NAME, "Exceptin:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			String msg = RestAPIStringParser.getMessage(locale, e.getMessageKey(), null);
			response.setMessageCode(e.getMessageKey());
			response.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(e.getMessage())) {
				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		} catch (Exception e) {
			LOG.error(METHOD_NAME, "Exceptin:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.LOAD_SERVICE_TYPE_SELECTOR_MAPPING_FAILED, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.LOAD_SERVICE_TYPE_SELECTOR_MAPPING_FAILED);
		} finally {

			if (mcomCon != null) {
				mcomCon.close(CLASS_NAME + "#" + METHOD_NAME);
				mcomCon = null;
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug(METHOD_NAME, "Exiting forward=");
			}
		}
		return response;
	}

	@GetMapping(value = "/delete", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Delete  service type  selector mapping", response = SaveServiceTypeSelectorMappingResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = SaveServiceTypeSelectorMappingResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })*/

	@Operation(summary = "${ServiceTypeSelectorMappingController.delete.name}", description = "${ServiceTypeSelectorMappingController.delete.desc}",

			responses = {
					@ApiResponse(responseCode =Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SaveServiceTypeSelectorMappingResponseVO.class))) }

					),

					@ApiResponse(responseCode = Constants.API_BAD_REQ_RESPONSE_CODE, description = Constants.API_BAD_REQ_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class))

									, examples = {
									@ExampleObject(value = BaseResponseRedoclyCommon.BAD_REQUEST) }

							) }),
					@ApiResponse(responseCode = Constants.API_UNAUTH_RESPONSE_CODE, description = Constants.API_UNAUTH_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
									@ExampleObject(value = BaseResponseRedoclyCommon.UNAUTH) }

							) }),
					@ApiResponse(responseCode = Constants.API_NOT_FOUND_RESPONSE_CODE, description = Constants.API_NOT_FOUND_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
									@ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND) }

							) }),
					@ApiResponse(responseCode = Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = Constants.API_INTERNAL_ERROR_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
									@ExampleObject(value = BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR) }) }) })

	public SaveServiceTypeSelectorMappingResponseVO deleteServiceTypeSelectorMapping(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest,
			@Parameter(description = "sNO", required = true) @RequestParam("sNO") String sNO) throws Exception {

		final String METHOD_NAME = "deleteServiceTypeSelectorMapping";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered");
		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		UserVO userVO = null;
		UserDAO userDAO = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		SaveServiceTypeSelectorMappingResponseVO response = new SaveServiceTypeSelectorMappingResponseVO();
		try {

			/*
			 * Authentication
			 *
			 * @throws BTSLBaseException
			 */

			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			userDAO = new UserDAO();
			String loginID = OAuthUserData.getData().getLoginid();
			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
			response = service.deleteServiceTypeSelectorMapping(con, userVO, sNO, response);
		} catch (BTSLBaseException e) {
			LOG.error(METHOD_NAME, "Exceptin:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			String msg = RestAPIStringParser.getMessage(locale, e.getMessageKey(), null);
			response.setMessageCode(e.getMessageKey());
			response.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(e.getMessage())) {
				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		} catch (Exception e) {
			LOG.error(METHOD_NAME, "Exceptin:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.SERVICE_TYPE_SELECTOR_MAPPING_DELETE_FAILED, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.SERVICE_TYPE_SELECTOR_MAPPING_DELETE_FAILED);
		} finally {

			if (mcomCon != null) {
				mcomCon.close(CLASS_NAME + "#" + METHOD_NAME);
				mcomCon = null;
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug(METHOD_NAME, "Exiting forward=");
			}
		}
		return response;
	}
}
