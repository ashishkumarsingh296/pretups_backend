package com.restapi.networkadmin.ICCIDIMSIkeymanagement.controller;

import java.sql.Connection;
import java.util.Locale;

import javax.ws.rs.core.MediaType;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.restapi.networkadmin.ICCIDIMSIkeymanagement.requestVO.*;
import com.restapi.networkadmin.ICCIDIMSIkeymanagement.responseVO.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import com.btsl.common.BaseResponse;
import com.btsl.common.BaseResponseRedoclyCommon;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.Constants;
import com.btsl.util.OAuthenticationUtil;
import com.restapi.networkadmin.ICCIDIMSIkeymanagement.serviceI.ICCIDIMSIKeyManagementServiceI;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Tag(name = "${iccidimsikeymanagementController.name}", description = "${iccidimsikeymanagementController.desc}")
@RestController
@RequestMapping(value = "/v1/networkadmin/iccidimsikeymanagement/")
public class ICCIDIMSIKeyManagementController {

	@Autowired
	private ICCIDIMSIKeyManagementServiceI service;
	public static final Log LOG = LogFactory.getLog(ICCIDIMSIKeyManagementController.class.getName());
	public static final String CLASS_NAME = "AssociateMSISDNWithICCIDController";

	@PostMapping(value = "/associate", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	@Operation(summary = "${iccidimsikeymanagementController.associateMSISDNWithICCID.name}", description = "${iccidimsikeymanagementController.associateMSISDNWithICCID.desc}",

			responses = {
					@ApiResponse(responseCode =Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AssociateMSISDNWithICCIDResponseVO.class))) }

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
	public AssociateMSISDNWithICCIDResponseVO associateMSISDNWithICCID(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest,
			@RequestBody MSISDNAndICCIDRequestVO requestVO) throws Exception {

		final String METHOD_NAME = "associateMSISDNWithICCID";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		UserVO userVO = null;
		UserDAO userDAO = null;
		AssociateMSISDNWithICCIDResponseVO response = new AssociateMSISDNWithICCIDResponseVO();
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
			service.associateMSISDNWithICCID(con, userVO, requestVO, response);

		}  finally {

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

	@PostMapping(value = "/reassociate", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	@Operation(summary = "${iccidimsikeymanagementController.reassociateMSISDNWithICCID.name}", description = "${iccidimsikeymanagementController.reassociateMSISDNWithICCID.desc}",

	responses = {
			@ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
					@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AssociateMSISDNWithICCIDResponseVO.class))) }

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

	public AssociateMSISDNWithICCIDResponseVO reAssociateMSISDNWithICCID(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest,
			@RequestBody MSISDNAndICCIDRequestVO requestVO) throws Exception {

		final String METHOD_NAME = "reAssociateMSISDNWithICCID";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		UserVO userVO = null;
		UserDAO userDAO = null;
		AssociateMSISDNWithICCIDResponseVO response = new AssociateMSISDNWithICCIDResponseVO();
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
			service.reAssociateMSISDNWithICCID(con, userVO, requestVO, response);

		}  finally {

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



	@PostMapping(value = "/icciddelete", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	@Operation(summary = "${iccidimsikeymanagementController.icciddelete.name}", description = "${iccidimsikeymanagementController.icciddelete.desc}",

	responses = {
			@ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
					@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = DeleteICCIDResponseVO.class))) }

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

		public DeleteICCIDResponseVO deleteICCID(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest,
			@RequestBody DeleteICCIDRequestVO requestVO) throws Exception {

		final String METHOD_NAME = "deleteICCID";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		UserVO userVO = null;
		UserDAO userDAO = null;
		DeleteICCIDResponseVO response = new DeleteICCIDResponseVO();
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
			service.deleteICCID(con, userVO, requestVO, response);

		}  finally {

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

	@PostMapping(value = "/uploadmsisdn", produces = MediaType.APPLICATION_JSON)
	@ResponseBody

	@Operation(summary = "${iccidimsikeymanagementController.uploadmsisdn.name}", description = "${iccidimsikeymanagementController.uploadmsisdn.desc}",

	responses = {
			@ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
					@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UploadMSISDNWithICCIDResponseVO.class))) }

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



	public UploadMSISDNWithICCIDResponseVO uploadMSISDNWithICCID(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest,
			@RequestBody MSISDNWithICCIDFileRequestVO requestVO) throws Exception {

		final String METHOD_NAME = "uploadMSISDNWithICCID";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		UserVO userVO = null;
		UserDAO userDAO = null;
		UploadMSISDNWithICCIDResponseVO response = new UploadMSISDNWithICCIDResponseVO();
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
			response = service.uploadMSISDNWithICCID(con, userVO, requestVO, response);

		}  finally {

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

	@PostMapping(value = "/loadcrticcidmsisdn", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	@Operation(summary = "${iccidimsikeymanagementController.loadcorrecticcidmsisdn.name}", description = "${iccidimsikeymanagementController.loadcorrecticcidmsisdn.desc}",

	responses = {
			@ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
					@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CorrectMSISDNWithICCIDResponseVO.class))) }

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



	public CorrectMSISDNWithICCIDResponseVO loadCorrectIccIdWithMsisdn(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest,
			@RequestBody MSISDNAndICCIDRequestVO requestVO) throws Exception {

		final String METHOD_NAME = "loadCorrectIccIdWithMsisdn";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		UserVO userVO = null;
		UserDAO userDAO = null;
		CorrectMSISDNWithICCIDResponseVO response = new CorrectMSISDNWithICCIDResponseVO();
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
			response = service.loadCorrectMSISDNICCIDMapping(con, userVO, requestVO, response);

		} finally {

			if (mcomCon != null) {
				mcomCon.close(CLASS_NAME + "#" + METHOD_NAME);
				mcomCon = null;
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug(METHOD_NAME, "Exiting ");
			}
		}
		return response;
	}

	@PostMapping(value = "/crticcidmsisdn", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	@Operation(summary = "${iccidimsikeymanagementController.correcticcidmsisdn.name}", description = "${iccidimsikeymanagementController.correcticcidmsisdn.desc}",

	responses = {
			@ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
					@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = DeleteICCIDResponseVO.class))) }

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



	public DeleteICCIDResponseVO correctIccIdWithMsisdn(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest,
			@RequestBody CorrectMSISDNICCIDMappingRequestVO requestVO) throws Exception {

		final String METHOD_NAME = "correctIccIdMsisdn";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		UserVO userVO = null;
		UserDAO userDAO = null;
		DeleteICCIDResponseVO response = new DeleteICCIDResponseVO();
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
			response = service.correctMSISDNICCIDMapping(con, userVO, requestVO, response);

		} finally {

			if (mcomCon != null) {
				mcomCon.close(CLASS_NAME + "#" + METHOD_NAME);
				mcomCon = null;
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug(METHOD_NAME, "Exiting ");
			}
		}
		return response;
	}






	@GetMapping(value = "/iccidimsimsisdnlistfilterbyiccid", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	@Operation(summary = "${iccidimsikeymanagementController.iccidImsiMsisdnListFilterByIccid.name}", description = "${iccidimsikeymanagementController.iccidImsiMsisdnListFilterByIccid.desc}",
			responses = {
					@ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AssociateMSISDNWithICCIDResponseVO.class))) }

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

	public IccidImsiMsisdnListResponseVO iccidImsiMsisdnListFilterByIccid(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest,
			@Parameter(name = "iccidImsi", required = true) @RequestParam("iccidImsi") String iccidImsi) throws BTSLBaseException,Exception {

		final String METHOD_NAME = "iccidImsiMsisdnListFilterByIccid";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		UserVO userVO = null;
		UserDAO userDAO = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		IccidImsiMsisdnListResponseVO response = new IccidImsiMsisdnListResponseVO();
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
			service.iccidImsiMsisdnListFilterByIccid(con, userVO, response, iccidImsi);

		}
		finally {

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






	@GetMapping(value = "/iccidimsimsisdnlistfilterbymsisdn", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	@Operation(summary = "${iccidimsikeymanagementController.iccidImsiMsisdnListFilterByMsisdn.name}", description = "${iccidimsikeymanagementController.iccidImsiMsisdnListFilterByMsisdn.desc}",

			responses = {
					@ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = IccidImsiMsisdnListResponseVO.class))) }

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

	public IccidImsiMsisdnListResponseVO iccidImsiMsisdnListFilterByMsisdn(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest,
			@Parameter(name = "msisdn", required = true) @RequestParam("msisdn") String msisdn) throws BTSLBaseException,Exception {

		final String METHOD_NAME = "iccidImsiMsisdnListFilterByMsisdn";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		UserVO userVO = null;
		UserDAO userDAO = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		IccidImsiMsisdnListResponseVO response = new IccidImsiMsisdnListResponseVO();
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
			service.iccidImsiMsisdnListFilterByMsisdn(con, userVO, response, msisdn);

		}
		finally {

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


	@GetMapping(value = "/iccidimsimsisdnlistfilterbydate", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	@Operation(summary = "${iccidimsikeymanagementController.iccidImsiMsisdnListFilterByDate.name}", description = "${iccidimsikeymanagementController.iccidImsiMsisdnListFilterByDate.desc}",
			responses = {
					@ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AssociateMSISDNWithICCIDResponseVO.class))) }

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

	public IccidImsiMsisdnListResponseVO iccidImsiMsisdnListFilterByDateRange(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest,
			@Parameter(name = "dateRange", required = true) @RequestParam("dateRange") String dateRange) throws BTSLBaseException,Exception {

		final String METHOD_NAME = "iccidImsiMsisdnListFilterByDateRange";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		UserVO userVO = null;
		UserDAO userDAO = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		IccidImsiMsisdnListResponseVO response = new IccidImsiMsisdnListResponseVO();
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
			service.iccidImsiMsisdnListFilterByDate(con, userVO, response, dateRange);

		}
		finally {

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
	
    @PostMapping(value = "/iccidhistory", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${iccidimsikeymanagementController.iccidhistory.name}", description = "${iccidimsikeymanagementController.iccidhistory.desc}",

            responses = {
                    @ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ICCIDMSISDNHistoryResponseVO.class))) }

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



	
	public ICCIDMSISDNHistoryResponseVO iccIdWithMsisdnHistory(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest,
			@RequestBody MSISDNAndICCIDRequestVO requestVO) throws Exception {

		final String METHOD_NAME = "iccIdWithMsisdnHistory";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		UserVO userVO = null;
		UserDAO userDAO = null;
		ICCIDMSISDNHistoryResponseVO response = new ICCIDMSISDNHistoryResponseVO();
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
			response = service.iccidHistory(con, userVO, requestVO.getIccID(), requestVO.getMsisdn(), response);

		} finally {

			if (mcomCon != null) {
				mcomCon.close(CLASS_NAME + "#" + METHOD_NAME);
				mcomCon = null;
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug(METHOD_NAME, "Exiting ");
			}
		}
		return response;
	}






	@PostMapping(value = "/icciddeletebulk", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	@Operation(summary = "${iccidimsikeymanagementController.icciddeletebulk.name}", description = "${iccidimsikeymanagementController.icciddeletebulk.desc}",

			responses = {
					@ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = DeleteICCIDBulkResponseVO.class))) }

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

	public DeleteICCIDBulkResponseVO iccidDeleteBulk(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest,
			@RequestBody DeleteICCIDBulkRequestVO requestVO) throws Exception {

		final String METHOD_NAME = "iccidDeleteBulk";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		UserVO userVO = null;
		UserDAO userDAO = null;
		DeleteICCIDBulkResponseVO response = new DeleteICCIDBulkResponseVO();
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
			response = service.iccidDeleteBulk(con,mcomCon, userVO, requestVO, response);

		}  finally {

			if (mcomCon != null) {
				mcomCon.close(METHOD_NAME);
				mcomCon = null;
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug(METHOD_NAME, "Exiting");
			}
		}
		return response;
	}


}
