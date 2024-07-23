package com.restapi.networkadmin.vouchercardgroup.controller;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.HttpStatus;
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
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.OAuthenticationUtil;
import com.restapi.networkadmin.requestVO.LoadCardGroupStatusListRequestVO;
import com.restapi.networkadmin.responseVO.C2SCardGroupStatusSaveResponseVO;
import com.restapi.networkadmin.vouchercardgroup.request.ChangeVoucherCardGroupStatusListRequestVO;
import com.restapi.networkadmin.vouchercardgroup.request.DefaultVoucherCardGroupRequestVO;
import com.restapi.networkadmin.vouchercardgroup.request.DeleteVoucherCardGroupRequestVO;
import com.restapi.networkadmin.vouchercardgroup.request.ModifyVoucherCardGroupDetailsRequestVO;
import com.restapi.networkadmin.vouchercardgroup.request.VoucherCardGroupTransferValueRequestVO;
import com.restapi.networkadmin.vouchercardgroup.request.VoucherGroupDetailsRequestVO;
import com.restapi.networkadmin.vouchercardgroup.response.AddVoucherGroupDropDownResponseVO;
import com.restapi.networkadmin.vouchercardgroup.response.CalculateTransferValueResponseVO;
import com.restapi.networkadmin.vouchercardgroup.response.DefaultVoucherCardGroupResponseVO;
import com.restapi.networkadmin.vouchercardgroup.response.DeleteVoucherCardGroupResponseVO;
import com.restapi.networkadmin.vouchercardgroup.response.DenaminationDetailsDropdownsResponseVO;
import com.restapi.networkadmin.vouchercardgroup.response.LoadVoucherCardGroupServicesResponseVO;
import com.restapi.networkadmin.vouchercardgroup.response.SaveVoucherGroupResponseVO;
import com.restapi.networkadmin.vouchercardgroup.response.ViewVoucherCardGroupResponseVO;
import com.restapi.networkadmin.vouchercardgroup.response.VoucherCardGroupStatusResponseVO;
import com.restapi.networkadmin.vouchercardgroup.response.VoucherCardGroupVersionNumberListResponseVO;
import com.restapi.networkadmin.vouchercardgroup.response.VoucherCardGroupVersionResponseVO;
import com.restapi.networkadmin.vouchercardgroup.response.VoucherTransferValueResponseVO;
import com.restapi.networkadmin.vouchercardgroup.serviceI.VoucherCardGroupServiceI;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${VoucherCardGroupController.name}", description = "${VoucherCardGroupController.desc}")//@Api(tags = "Network Admin")
@RestController
@RequestMapping(value = "/v1/networkadmin/vouchercardgroup/")
public class VoucherCardGroupController {


	@Autowired
	private VoucherCardGroupServiceI service;
	public static final Log LOG = LogFactory.getLog(VoucherCardGroupController.class.getName());
	public static final String className = "VoucherCardGroupController";

	@GetMapping(value = "/loadvouchercrdgrpSrvs", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Load Voucher CardGroup Services", response =LoadVoucherCardGroupServicesResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = LoadVoucherCardGroupServicesResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${loadvouchercrdgrpSrvs.summary}", description="${loadvouchercrdgrpSrvs.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = LoadVoucherCardGroupServicesResponseVO.class))
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

	public LoadVoucherCardGroupServicesResponseVO loadVoucherCardGroupServices(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
																			   HttpServletResponse response1, HttpServletRequest httpServletRequest) throws Exception {

		final String methodName = "loadVoucherCardGroupServices";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}

		Connection con = null;MComConnectionI mcomCon = null;
		UserVO userVO = null;
		UserDAO userDAO = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		LoadVoucherCardGroupServicesResponseVO response = new LoadVoucherCardGroupServicesResponseVO();
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
			response = service.loadServiceAndSubServiceList(con, userVO.getNetworkID());
			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.SUCCESSFULLY_LOAD_VOUCHER_SERVICES, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.SUCCESSFULLY_LOAD_VOUCHER_SERVICES);


		} catch (BTSLBaseException e) {
			LOG.error(methodName, "Exceptin:e=" + e);
			LOG.errorTrace(methodName, e);
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

		}  catch (Exception e) {
			LOG.error(methodName, "Exceptin:e=" + e);
			LOG.errorTrace(methodName, e);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.FAILED_LOAD_VOUCHER_GROUP_SERVICES, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.FAILED_LOAD_VOUCHER_GROUP_SERVICES);
		} finally {

			if(mcomCon != null){
				mcomCon.close(className+"#"+methodName);
				mcomCon=null;}
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "Exiting forward=" );
			}
		}
		return response;
	}
	@GetMapping(value = "/viewvouchercardroupdetails", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "View Voucher CardGroup Details", response =ViewVoucherCardGroupResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = ViewVoucherCardGroupResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${viewvouchercardroupdetails.summary}", description="${viewvouchercardroupdetails.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ViewVoucherCardGroupResponseVO.class))
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


	public ViewVoucherCardGroupResponseVO  viewVoucherCardGroup(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
																HttpServletResponse response1, HttpServletRequest httpServletRequest,
																@Parameter(description = "cardGroupSetId", required = true) @RequestParam("cardGroupSetId") String cardGroupSetId,
																@Parameter(description = "version", required = true) @RequestParam("version") String version
	) throws Exception {

		final String methodName = "viewVoucherCardGroup";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}


		Connection con = null;MComConnectionI mcomCon = null;
		Date currentDate = null;
		ViewVoucherCardGroupResponseVO response = new ViewVoucherCardGroupResponseVO();
		UserVO userVO = null;
		UserDAO userDAO = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

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
			response = service.viewVoucherCardGroupDetails(con, userVO, cardGroupSetId, version);
			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.VOUCHER_GROUP_DETAILS_LOAD_SUCCESSFULLY, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.VOUCHER_GROUP_DETAILS_LOAD_SUCCESSFULLY);

		} catch (BTSLBaseException e) {
			LOG.error(methodName, "Exceptin:e=" + e);
			LOG.errorTrace(methodName, e);
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

		}  catch (Exception e) {
			LOG.error(methodName, "Exceptin:e=" + e);
			LOG.errorTrace(methodName, e);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.VOUCHER_GROUP_DETAILS_LOAD_FAILED, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.VOUCHER_GROUP_DETAILS_LOAD_FAILED);
		} finally {

			if(mcomCon != null){
				mcomCon.close(className+"#"+methodName);
				mcomCon=null;}
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "Exiting forward" );
			}

		}
		return response;


	}


	@GetMapping(value = "/loadvmscardgrpversions", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Load Voucher CardGroup Version list", response =VoucherCardGroupVersionResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = VoucherCardGroupVersionResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${loadvmscardgrpversions.summary}", description="${loadvmscardgrpversions.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = VoucherCardGroupVersionResponseVO.class))
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


	public VoucherCardGroupVersionResponseVO loadVoucherCardGroupVersionList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
																			 HttpServletResponse response1, HttpServletRequest httpServletRequest,
																			 @Parameter(description = "serviceType", required = true) @RequestParam("serviceType") String serviceType,
																			 @Parameter(description = "subService", required = true) @RequestParam("subService") String subService,
																			 @Parameter(description = "cardGroupSetType", required = true) @RequestParam("cardGroupSetType") String cardGroupSetType,
																			 @Parameter(description = "applicableFromDateAndTime", required = true) @RequestParam("applicableFromDateAndTime") String applicableFromDateAndTime
	) throws Exception {


		final String methodName = "loadVoucherCardGroupVersionList";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}


		Connection con = null;MComConnectionI mcomCon = null;
		Date currentDate = null;
		VoucherCardGroupVersionResponseVO response = new VoucherCardGroupVersionResponseVO();
		UserVO userVO = null;
		UserDAO userDAO = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

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

			Date dateAndTime = null; //new Date();

			try {
				String systemDateFormat = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATETIME_FORMAT);
				final DateFormat dateFormat =  new SimpleDateFormat(systemDateFormat);
				dateAndTime = dateFormat.parse(applicableFromDateAndTime);
			}
			catch(Exception e) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_DATE_TIME_FORMATE);
			}

			response =service.loadVoucherCardGroupversionList(con, userVO, serviceType, subService, cardGroupSetType, dateAndTime);
			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.SUCCESSFULLY_LOAD_VOUCHER_GROUP_VERSION_DETAILS, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.SUCCESSFULLY_LOAD_VOUCHER_GROUP_VERSION_DETAILS);

		} catch (BTSLBaseException e) {
			LOG.error(methodName, "Exceptin:e=" + e);
			LOG.errorTrace(methodName, e);
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

		}  catch (Exception e) {
			LOG.error(methodName, "Exceptin:e=" + e);
			LOG.errorTrace(methodName, e);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.FAILED_LOAD_VOUCHER_GROUP_VERSION_DETAILS, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.FAILED_LOAD_VOUCHER_GROUP_VERSION_DETAILS);
		} finally {

			if(mcomCon != null){
				mcomCon.close(className+"#"+methodName);
				mcomCon=null;}
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "Exiting forward=" );
			}

		}
		return response;

	}

	@GetMapping(value = "/loadvmscardgrpdropdowns", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Load Voucher CardGroup Add drop down list", response =AddVoucherGroupDropDownResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = AddVoucherGroupDropDownResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${loadvmscardgrpdropdowns.summary}", description="${loadvmscardgrpdropdowns.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = AddVoucherGroupDropDownResponseVO.class))
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


	public AddVoucherGroupDropDownResponseVO loadVoucherCardGroupdropdowns(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
																		   HttpServletResponse response1, HttpServletRequest httpServletRequest,
																		   @Parameter(description = "subService", required = true) @RequestParam("subService") String subService
	) throws Exception {


		final String methodName = "loadVoucherCardGroupdropdowns";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}


		Connection con = null;MComConnectionI mcomCon = null;
		Date currentDate = null;
		AddVoucherGroupDropDownResponseVO response = new AddVoucherGroupDropDownResponseVO();
		UserVO userVO = null;
		UserDAO userDAO = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

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



			response =service.addVoucherGroupDropDown(con, userVO, subService);
			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.SUCCESSFULLY_LOAD_VOUCHER_GROUP_DROP_DOWN, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.SUCCESSFULLY_LOAD_VOUCHER_GROUP_DROP_DOWN);

		} catch (BTSLBaseException e) {
			LOG.error(methodName, "Exceptin:e=" + e);
			LOG.errorTrace(methodName, e);
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

		}  catch (Exception e) {
			LOG.error(methodName, "Exceptin:e=" + e);
			LOG.errorTrace(methodName, e);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.FAILED_LOAD_VOUCHER_GROUP_DROP_DOWN, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.FAILED_LOAD_VOUCHER_GROUP_DROP_DOWN);
		} finally {

			if(mcomCon != null){
				mcomCon.close(className+"#"+methodName);
				mcomCon=null;}
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "Exiting forward=" );
			}

		}
		return response;

	}


	@GetMapping(value = "/loaddenominationdetails", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Load all Voucher Denomination Details Dropdowns List", response =DenaminationDetailsDropdownsResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = DenaminationDetailsDropdownsResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${loaddenominationdetails.summary}", description="${loaddenominationdetails.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = DenaminationDetailsDropdownsResponseVO.class))
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



	public DenaminationDetailsDropdownsResponseVO loadVoucherDenominationDetailsList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
																					 HttpServletResponse response1, HttpServletRequest httpServletRequest
	) throws Exception {


		final String methodName = "loadVoucherDenominationDetailsList";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}


		Connection con = null;MComConnectionI mcomCon = null;
		Date currentDate = null;
		DenaminationDetailsDropdownsResponseVO response = new DenaminationDetailsDropdownsResponseVO();
		UserVO userVO = null;
		UserDAO userDAO = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

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



			response =service.denominationDetailsList(con, userVO);

			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.SUCCESSFULLY_LOAD_VOUCHER_GROUP_DENOMINATION_PROFILE_LIST, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.SUCCESSFULLY_LOAD_VOUCHER_GROUP_DENOMINATION_PROFILE_LIST);

		} catch (BTSLBaseException e) {
			LOG.error(methodName, "Exceptin:e=" + e);
			LOG.errorTrace(methodName, e);
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

		}  catch (Exception e) {
			LOG.error(methodName, "Exceptin:e=" + e);
			LOG.errorTrace(methodName, e);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.FAILED_LOAD_VOUCHER_GROUP_DENOMINATION_PROFILE_LIST, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.FAILED_LOAD_VOUCHER_GROUP_DENOMINATION_PROFILE_LIST);
		} finally {

			if(mcomCon != null){
				mcomCon.close(className+"#"+methodName);
				mcomCon=null;}
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "Exiting forward=" );
			}

		}
		return response;

	}

	@PostMapping(value = "/savevouchercrgrp", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Save Voucher CardGroup List", response =VoucherGroupDetailsRequestVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = VoucherGroupDetailsRequestVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${savevouchercrgrp.summary}", description="${savevouchercrgrp.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = SaveVoucherGroupResponseVO.class))
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


	public SaveVoucherGroupResponseVO  saveVoucherCardGroupList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
																HttpServletResponse response1, HttpServletRequest httpServletRequest,
																@RequestBody  VoucherGroupDetailsRequestVO requestVO

	) throws Exception {

		final String methodName = "saveVoucherCardGroupList";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}


		Connection con = null;MComConnectionI mcomCon = null;
		Date currentDate = null;
		SaveVoucherGroupResponseVO response = new SaveVoucherGroupResponseVO();
		UserVO userVO = null;
		UserDAO userDAO = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

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
			response = service.saveVoucherGroup(con,response, userVO, requestVO);

			response.setStatus((HttpStatus.SC_OK));
			String arr[]= {requestVO.getCardGroupSetName()};
			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.SUCCESSFULY_ADD_VOUCHER_CARD_GROUP_LOG, arr);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.SUCCESSFULY_ADD_VOUCHER_CARD_GROUP_LOG);

		} catch (BTSLBaseException e) {
			LOG.error(methodName, "Exceptin:e=" + e);
			LOG.errorTrace(methodName, e);
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

		}  catch (Exception e) {
			LOG.error(methodName, "Exceptin:e=" + e);
			LOG.errorTrace(methodName, e);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.VOUCHER_GROUP_DETAILS_ADD_FAILED, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.VOUCHER_GROUP_DETAILS_ADD_FAILED);
		} finally {

			if(mcomCon != null){
				mcomCon.close(className+"#"+methodName);
				mcomCon=null;}
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "Exiting forward=" );
			}

		}
		return response;


	}

	@PostMapping(value = "/modifyvouchercrgrp", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Modify Voucher CardGroup List", response =VoucherGroupDetailsRequestVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = VoucherGroupDetailsRequestVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${modifyvouchercrgrp.summary}", description="${modifyvouchercrgrp.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = SaveVoucherGroupResponseVO.class))
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


	public SaveVoucherGroupResponseVO  modifyVoucherCardGroupList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
																  HttpServletResponse response1, HttpServletRequest httpServletRequest,
																  @RequestBody  ModifyVoucherCardGroupDetailsRequestVO requestVO

	) throws Exception {

		final String methodName = "modifyVoucherCardGroupList";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}


		Connection con = null;MComConnectionI mcomCon = null;
		Date currentDate = null;
		SaveVoucherGroupResponseVO response = new SaveVoucherGroupResponseVO();
		UserVO userVO = null;
		UserDAO userDAO = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

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
			response = service.modifyVoucherGroup(con,userVO, response, requestVO);
			mcomCon.finalCommit();
			response.setStatus((HttpStatus.SC_OK));
			String arr[]= {requestVO.getCardGroupSetName()};
			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.VOUCHER_GROUP_MODIFIED_SUCCESSFULLY, arr);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.VOUCHER_GROUP_MODIFIED_SUCCESSFULLY);

		} catch (BTSLBaseException e) {
			mcomCon.finalRollback();
			LOG.error(methodName, "Exceptin:e=" + e);
			LOG.errorTrace(methodName, e);
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

		}  catch (Exception e) {
			mcomCon.finalRollback();
			LOG.error(methodName, "Exceptin:e=" + e);
			LOG.errorTrace(methodName, e);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.VOUCHER_GROUP_MODIFY_FAILED, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.VOUCHER_GROUP_MODIFY_FAILED);
		} finally {

			if(mcomCon != null){
				mcomCon.close(className+"#"+methodName);
				mcomCon=null;}
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "Exiting forward=" );
			}

		}
		return response;


	}

	@GetMapping(value = "/loadc2svmscrdgrpverlistbasedonsetid", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Load Voucher CardGroup VersionList based On SetID And FromDate", response =VoucherCardGroupVersionNumberListResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = VoucherCardGroupVersionNumberListResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${loadc2svmscrdgrpverlistbasedonsetid.summary}", description="${loadc2svmscrdgrpverlistbasedonsetid.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = VoucherCardGroupVersionNumberListResponseVO.class))
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


	public VoucherCardGroupVersionNumberListResponseVO loadVoucherCardGroupVersionListbasedOnSetIDAndFromDate(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
																											  HttpServletResponse response1, HttpServletRequest httpServletRequest,
																											  @Parameter(description = "cardGroupSetId", required = true) @RequestParam("cardGroupSetId") String cardGroupSetId,
																											  @Parameter(description = "dateTime", required = true) @RequestParam("dateTime") String dateTime
	) throws Exception {


		final String methodName = "loadVoucherCardGroupVersionListbasedOnSetIDAndFromDate";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}


		Connection con = null;MComConnectionI mcomCon = null;
		Date currentDate = null;
		VoucherCardGroupVersionNumberListResponseVO response = new VoucherCardGroupVersionNumberListResponseVO();
		UserVO userVO = null;
		UserDAO userDAO = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

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
			Date date = null;
			try {
				String systemDateFormat = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATETIME_FORMAT);
				final DateFormat dateFormat =  new SimpleDateFormat(systemDateFormat);
				date = dateFormat.parse(dateTime);
			}
			catch(Exception e) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_DATE_TIME_FORMATE);
			}
			response = service.loadVersionListBasedOnCardGroupSetIDAndDate(con, cardGroupSetId, date );
			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.SUCCESSFULLY_LOAD_C2S_CARDGROUP_VERSION_LIST, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.SUCCESSFULLY_LOAD_C2S_CARDGROUP_VERSION_LIST);

		} catch (BTSLBaseException e) {
			LOG.error(methodName, "Exceptin:e=" + e);
			LOG.errorTrace(methodName, e);
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

		}  catch (Exception e) {
			LOG.error(methodName, "Exceptin:e=" + e);
			LOG.errorTrace(methodName, e);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.FAILED_LOAD_C2S_CARDGROUP_VERSION_LIST, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.FAILED_LOAD_C2S_CARDGROUP_VERSION_LIST);
		} finally {

			if(mcomCon != null){
				mcomCon.close(className+"#"+methodName);
				mcomCon=null;}
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "Exiting forward=" );
			}

		}
		return response;

	}
	@PostMapping(value="/changedefaultvmscardgroup", produces = MediaType.APPLICATION_JSON )
	@ResponseBody
	/*@ApiOperation(value = "Change Default Voucher Card Group", response = DefaultVoucherCardGroupResponseVO.class, authorizations = { @Authorization(value = "Authorization")})

	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = DefaultVoucherCardGroupResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${changedefaultvmscardgroup.summary}", description="${changedefaultvmscardgroup.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = DefaultVoucherCardGroupResponseVO.class))
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


	public DefaultVoucherCardGroupResponseVO changeDefaultVoucherCardgroup(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse responseSwagger, HttpServletRequest httpServletRequest,
			@RequestBody DefaultVoucherCardGroupRequestVO requestVO
	) throws Exception {

		final String METHOD_NAME="changeDefaultVoucherCardgroup";
		if(LOG.isDebugEnabled()) {
			LOG.debug(className, METHOD_NAME);
		}
		DefaultVoucherCardGroupResponseVO response = new DefaultVoucherCardGroupResponseVO();


		UserDAO userDAO = null;
		UserVO userVO = null;
		Connection con = null;
		MComConnectionI mcomCon=null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		StringBuilder loggerValue= new StringBuilder();
		try {


			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, responseSwagger);
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			userDAO = new UserDAO();
			String loginID = OAuthUserData.getData().getLoginid();
			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);

			response = service.changeDefaultVoucherCardGroup(con, userVO, requestVO);
			response.setStatus((HttpStatus.SC_OK));
			String arr[] = {response.getUpdateddefaultVoucherCadgroup()};
			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.VOUCHER_CARD_GROUP_CHANGE_DEFAULT_CARD_GROUP_SET, arr);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.VOUCHER_CARD_GROUP_CHANGE_DEFAULT_CARD_GROUP_SET);

		}
		catch(BTSLBaseException be) {
			loggerValue.setLength(0);
			loggerValue.append("Exception:e=");
			loggerValue.append(be);

			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);

			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			response.setMessageCode(be.getMessageKey());
			response.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
				responseSwagger.setStatus(HttpStatus.SC_UNAUTHORIZED);

			} else {
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwagger.setStatus(HttpStatus.SC_BAD_REQUEST);

			}
		}
		catch (Exception e) {

			loggerValue.setLength(0);
			loggerValue.append("Exception:e=");
			loggerValue.append(e);

			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.FAILED_CHANGE_DEFAULT_CARD_GROUP, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.FAILED_CHANGE_DEFAULT_CARD_GROUP);
			responseSwagger.setStatus(HttpStatus.SC_BAD_REQUEST);





		} finally {
			if (mcomCon != null) {
				mcomCon.close(METHOD_NAME);
				mcomCon = null;
			}
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
		}
		return response;

	}


	@PostMapping(value = "/deletevmscardgrp", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Delete Voucher CardGroup", response =DeleteVoucherCardGroupResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = DeleteVoucherCardGroupResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${deletevmscardgrp.summary}", description="${deletevmscardgrp.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = DeleteVoucherCardGroupResponseVO.class))
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


	public DeleteVoucherCardGroupResponseVO deleteVoucherCardGroup(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
																   HttpServletResponse response1, HttpServletRequest httpServletRequest,
																   @RequestBody DeleteVoucherCardGroupRequestVO requestVO
	) throws Exception {


		final String methodName = "deleteVoucherCardGroup";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}


		Connection con = null;MComConnectionI mcomCon = null;
		Date currentDate = null;
		DeleteVoucherCardGroupResponseVO response = new DeleteVoucherCardGroupResponseVO();
		UserVO userVO = null;
		UserDAO userDAO = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

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



			int count =service.deleteVoucherCardGroup(con, userVO,requestVO.getCardGroupSetId(), requestVO.getServiceTypeDesc(), requestVO.getSubServiceDesc(), requestVO.getCardGroupSetName());
			if(count>0) {
				response.setCardGroupSetId(requestVO.getCardGroupSetId());
				response.setStatus((HttpStatus.SC_OK));
				String arr[]= {requestVO.getCardGroupSetName()};
				String resmsg = RestAPIStringParser.getMessage(locale,
						PretupsErrorCodesI.SUCCESSFULLY_DELETED_VOUCHER_CARD_GROUP_SET, arr);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.SUCCESSFULLY_DELETED_VOUCHER_CARD_GROUP_SET);
			}
			else {
				throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.FAILED_DELETE_VOUCHER_CARD_GROUP_SET, "");
			}
		} catch (BTSLBaseException e) {
			LOG.error(methodName, "Exceptin:e=" + e);
			LOG.errorTrace(methodName, e);
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

		}  catch (Exception e) {
			LOG.error(methodName, "Exceptin:e=" + e);
			LOG.errorTrace(methodName, e);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.FAILED_DELETE_VOUCHER_CARD_GROUP_SET, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.FAILED_DELETE_VOUCHER_CARD_GROUP_SET);
		} finally {

			if(mcomCon != null){
				mcomCon.close(className+"#"+methodName);
				mcomCon=null;}
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "Exiting forward=" );
			}

		}
		return response;

	}

	@PostMapping(value= "/chngvmscrdgpstatuslist", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Change Voucher cardgroup status list",
			response = C2SCardGroupStatusSaveResponseVO.class,
			authorizations = {
					@Authorization(value = "Authorization")})
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = C2SCardGroupStatusSaveResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request" ),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found")
	})*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${chngvmscrdgpstatuslist.summary}", description="${chngvmscrdgpstatuslist.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = C2SCardGroupStatusSaveResponseVO.class))
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


	public C2SCardGroupStatusSaveResponseVO changeCardGroupStatusList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
																	  HttpServletResponse response1, HttpServletRequest httpServletRequest,
																	  @RequestBody  ChangeVoucherCardGroupStatusListRequestVO requestVO
	) throws Exception {
		final String methodName = "changeCardGroupStatusList";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered ");
		}
		C2SCardGroupStatusSaveResponseVO response = new C2SCardGroupStatusSaveResponseVO();

		Connection con = null;
		MComConnectionI mcomCon = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

		UserVO userVO = null;
		UserDAO userDAO = null;

		try {

			/*
			 * Authentication
			 *
			 * @throws BTSLBaseException
			 */

			OAuthUser oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, response1);
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			userVO = new UserVO();
			userDAO = new UserDAO();
			userVO  = userDAO.loadUsersDetails(con, oAuthUser.getData().getMsisdn());
			response = service.changeVoucherCardGroupStatusList(con, userVO, requestVO);
			response1.setStatus((HttpStatus.SC_OK));
			response.setStatus(HttpStatus.SC_OK);
			String msg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.SUCCESSFULLY_SAVE_CARD_GROUP_LIST_FOR_CARD_GROUP_STATUS, null);
			response.setMessage(msg);
		}
		catch (BTSLBaseException be) {
			LOG.error("", "Exceptin:e=" + be);
			LOG.errorTrace(methodName, be);
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
		}
		catch (Exception e) {
			LOG.error(methodName, "Exceptin:e=" + e.getMessage());
			LOG.errorTrace(methodName, e);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.FAILED_SAVE_CARD_GROUP_LIST_FOR_CARD_GROUP_STATUS, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.FAILED_SAVE_CARD_GROUP_LIST_FOR_CARD_GROUP_STATUS);
		}
		finally {
			if (mcomCon != null) {
				mcomCon.close(methodName);
				mcomCon = null;
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "Exiting:=" + methodName);
			}
		}


		return response;
	}

	@PostMapping(value= "/loadvmscardgroupstatuslist", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Load Voucher Cardgroup Status List",
			response = VoucherCardGroupStatusResponseVO.class,
			authorizations = {
					@Authorization(value = "Authorization")})
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = VoucherCardGroupStatusResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request" ),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found")
	})
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${loadvmscardgroupstatuslist.summary}", description="${loadvmscardgroupstatuslist.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = VoucherCardGroupStatusResponseVO.class))
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

	public VoucherCardGroupStatusResponseVO loadVoucherCardGroupStatusList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
																		   HttpServletResponse response1, HttpServletRequest httpServletRequest,
																		   @RequestBody  LoadCardGroupStatusListRequestVO requestVO) throws Exception {
		final String methodName = "loadVoucherCardGroupStatusList";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered ");
		}
		VoucherCardGroupStatusResponseVO response = new VoucherCardGroupStatusResponseVO();

		Connection con = null;
		MComConnectionI mcomCon = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

		UserVO userVO = null;
		UserDAO userDAO = null;

		try {

			/*
			 * Authentication
			 *
			 * @throws BTSLBaseException
			 */

			OAuthUser oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, response1);
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			userVO = new UserVO();
			userDAO = new UserDAO();
			userVO  = userDAO.loadUsersDetails(con, oAuthUser.getData().getMsisdn());

			response=service.loadVoucherCardGroupStatusList(con, userVO, requestVO.getRequestVOList());
			response1.setStatus((HttpStatus.SC_OK));
			response.setStatus(HttpStatus.SC_OK);
			String msg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.SUCCESSFULLY_LOAD_CARD_GROUP_LIST_FOR_CARD_GROUP_STATUS, null);
			response.setMessage(msg);
		}
		catch (BTSLBaseException be) {
			LOG.error("", "Exceptin:e=" + be);
			LOG.errorTrace(methodName, be);
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
		}
		catch (Exception e) {
			LOG.error(methodName, "Exceptin:e=" + e.getMessage());
			LOG.errorTrace(methodName, e);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.FAILED_LOAD_CARD_GROUP_LIST_FOR_CARD_GROUP_STATUS, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.FAILED_LOAD_CARD_GROUP_LIST_FOR_CARD_GROUP_STATUS);
		}
		finally {
			if (mcomCon != null) {
				mcomCon.close(methodName);
				mcomCon = null;
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "Exiting:=" + methodName);
			}
		}


		return response;
	}

	@PostMapping(value="/getvmscardgrouptrfrulevalue", produces = MediaType.APPLICATION_JSON )
	@ResponseBody
	/*@ApiOperation(value = "Get Voucher Card Group Transfer Value", response = CardGroupCalculateC2STransferValueResponseVO.class, authorizations = { @Authorization(value = "Authorization")})

	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = CardGroupCalculateC2STransferValueResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${getvmscardgrouptrfrulevalue.summary}", description="${getvmscardgrouptrfrulevalue.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = VoucherTransferValueResponseVO.class))
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


	public VoucherTransferValueResponseVO getCardGroupTransferRuleValue(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
																		HttpServletResponse response1, HttpServletRequest httpServletRequest,
																		@RequestBody  VoucherCardGroupTransferValueRequestVO requestVO) throws Exception {

		final String METHOD_NAME="getCardGroupTransferRuleValue";
		if(LOG.isDebugEnabled()) {
			LOG.debug(className, METHOD_NAME);
		}
		VoucherTransferValueResponseVO response = new VoucherTransferValueResponseVO() ;

		UserDAO userDAO = null;
		UserVO userVO = null;
		Connection con = null;
		MComConnectionI mcomCon=null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		StringBuffer loggerValue = new StringBuffer();

		try {
			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			userDAO = new UserDAO();
			String loginID = OAuthUserData.getData().getLoginid();
			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);

			response = service.viewVoucherTransferValue(con, userVO, requestVO);
			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.SAVE_CARD_GROUP_TRANSFER_RULE_VALUE_SUCCESSFULLY, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.SAVE_CARD_GROUP_TRANSFER_RULE_VALUE_SUCCESSFULLY);

		}
		catch(BTSLBaseException be) {
			loggerValue.setLength(0);
			loggerValue.append("Exception:e=");
			loggerValue.append(be);

			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if(response.getMessage()==null) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
				response.setMessageCode(be.getMessageKey());
				response.setMessage(msg);
			}
			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);

			} else {
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);

			}
		}
		catch (Exception e) {

			loggerValue.setLength(0);
			loggerValue.append("Exception:e=");
			loggerValue.append(e);

			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.FAILED_SAVE_CARD_GROUP_TRANSFER_RULE_VALUE, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.FAILED_SAVE_CARD_GROUP_TRANSFER_RULE_VALUE);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);





		} finally {
			if (mcomCon != null) {
				mcomCon.close(METHOD_NAME);
				mcomCon = null;
			}
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
		}
		return response;

	}

	@PostMapping(value = "/caltrnsfrvalue", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Calculate Voucher CardGroup Transfer Value", response =CalculateTransferValueResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = CalculateTransferValueResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${caltrnsfrvalue.summary}", description="${caltrnsfrvalue.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CalculateTransferValueResponseVO.class))
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

	public CalculateTransferValueResponseVO  CalculateVoucherTransferValue(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
																		   HttpServletResponse response1, HttpServletRequest httpServletRequest,
																		   @RequestBody  VoucherGroupDetailsRequestVO requestVO

	) throws Exception {

		final String methodName = "CalculateVoucherTransferValue";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}


		Connection con = null;MComConnectionI mcomCon = null;
		Date currentDate = null;
		CalculateTransferValueResponseVO response = new CalculateTransferValueResponseVO();
		UserVO userVO = null;
		UserDAO userDAO = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

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
			response = service.calculateTransferValue(con, userVO, response, requestVO);

			response.setStatus((HttpStatus.SC_OK));
			String arr[]= {requestVO.getCardGroupSetName()};
			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.SUCCESSFULLY_CALCULATE_VOUCHER_GROUP_TRANSFER_VALUE, arr);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.SUCCESSFULLY_CALCULATE_VOUCHER_GROUP_TRANSFER_VALUE);

		} catch (BTSLBaseException e) {
			LOG.error(methodName, "Exceptin:e=" + e);
			LOG.errorTrace(methodName, e);
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

		}  catch (Exception e) {
			LOG.error(methodName, "Exceptin:e=" + e);
			LOG.errorTrace(methodName, e);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.FAILED_CALCULATE_VOUCHER_GROUP_TRANSFER_VALUE, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.FAILED_CALCULATE_VOUCHER_GROUP_TRANSFER_VALUE);
		} finally {

			if(mcomCon != null){
				mcomCon.close(className+"#"+methodName);
				mcomCon=null;}
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "Exiting forward=" );
			}

		}
		return response;


	}




}
