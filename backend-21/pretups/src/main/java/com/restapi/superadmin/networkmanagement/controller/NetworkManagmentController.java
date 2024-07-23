package com.restapi.superadmin.networkmanagement.controller;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import com.btsl.common.*;
import com.restapi.superadmin.networkmanagement.requestVO.ModifyNetworkRequestVO;
import com.restapi.superadmin.networkmanagement.responseVO.NetworkListResponseVO;
import com.restapi.superadmin.networkmanagement.responseVO.ServiceIDListResponseVO;
import com.restapi.superadmin.networkmanagement.service.NetworkManagmentServiceImpl;
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
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.httpclient.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.network.businesslogic.NetworkDAO;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.restapi.superadmin.responseVO.NetworkListResponse;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.util.Constants;


@Tag(name = "${networkManagmentController.name}", description = "${networkManagmentController.desc}")
@RestController
@RequestMapping(value = "/v1/superadmin")
public class NetworkManagmentController {
	
	

	public static final Log log = LogFactory.getLog(NetworkManagmentController.class.getName());
	public static final String classname = "NetworkManagmentController";
	
	@Autowired
	NetworkManagmentServiceImpl networkManagmentImpl;
	
	@Autowired
	static OperatorUtil operatorUtil = null;
	
	/**
	 * @author sachin.singh,
	 * @param headers
	 * @param response1
	 * @param httpServletRequest
	 * @return
	 * @throws Exception
	 */
	
	@GetMapping(value = "/networkList",produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	@Operation(summary = "${networkManagmentController.getNetworkListByAdmin.name}", description = "${networkManagmentController.getNetworkListByAdmin.desc}",

			responses = {
					@ApiResponse(responseCode =Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = NetworkListResponseVO.class))) }

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
	public NetworkListResponseVO GetNetworkListByAdmin(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest)throws Exception {
		
		final String methodName = "getNetworkListByAdmin";
		if(log.isDebugEnabled()) {
			log.debug(methodName, "Entered : ");
		}
		
		NetworkListResponseVO response = new NetworkListResponseVO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE,SystemPreferences.DEFAULT_COUNTRY);
		String loginID = null;
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
			loginID = OAuthUserData.getData().getLoginid();
			response = networkManagmentImpl.viewNetworkList(con, loginID, response1);
			
			
		}catch(BTSLBaseException btslBaseException) {
			log.error("", "Exceptin:e=" + btslBaseException);
			log.errorTrace(methodName, btslBaseException);
			String msg = RestAPIStringParser.getMessage(locale, btslBaseException.getMessageKey(), null);
			response.setMessageCode(btslBaseException.getMessageKey());
			response.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(btslBaseException.getMessage())) {
				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		}catch(Exception exception) {
			log.error("", "Exceptin:e=" + exception);
			log.errorTrace(methodName, exception);
			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.NETWORK_LIST_WENT_WRONG, null);
			response.setMessageCode(PretupsErrorCodesI.NETWORK_LIST_WENT_WRONG);
			response.setMessage(msg);
		}
		finally {

			if (mcomCon != null) {
				mcomCon.close("GetNetworkListByAdmin");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting:=" + methodName);
			}

		}
		
		
		return response;
		
	}
	
	
	/**
	 * @author sachin.singh,
	 * @param headers
	 * @param response1
	 * @param httpServletRequest
	 * @return
	 * @throws Exception
	 */
	
	@GetMapping(value= "/networkStatusList", produces = MediaType.APPLICATION_JSON)	
	@ResponseBody
	@Operation(summary = "${networkManagmentController.loadNetworkList.name}", description = "${networkManagmentController.loadNetworkList.desc}",

			responses = {
					@ApiResponse(responseCode =Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = NetworkListResponse.class))) }

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

	public NetworkListResponseVO loadNetworkList (@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest request)throws Exception {
		final String methodName = "loadNetworkList";
    	if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }
		
    	NetworkListResponseVO response = new NetworkListResponseVO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE,SystemPreferences.DEFAULT_COUNTRY);
		String loginID = null;
		
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
			loginID = OAuthUserData.getData().getLoginid();
			response = networkManagmentImpl.viewNetworkList(con, loginID, response1);
			
			
		}catch(BTSLBaseException btslBaseException) {
			log.error("", "Exceptin:e=" + btslBaseException);
			log.errorTrace(methodName, btslBaseException);
			String msg = RestAPIStringParser.getMessage(locale, btslBaseException.getMessageKey(), null);
			response.setMessageCode(btslBaseException.getMessageKey());
			response.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(btslBaseException.getMessage())) {
				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		}catch(Exception exception) {
			log.error("", "Exceptin:e=" + exception);
			log.errorTrace(methodName, exception);
			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.NETWORK_LIST_WENT_WRONG, null);
			response.setMessageCode(PretupsErrorCodesI.NETWORK_LIST_WENT_WRONG);
			response.setMessage(msg);
		}
		finally {

			if (mcomCon != null) {
				mcomCon.close("loadNetworkList");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting:=" + methodName);
			}

		}
		
		
		return response;
		
    	
		
	}

	
	/**
	 * @author sachin.singh,
	 * @param headers
	 * @param NetworkList
	 * @param response1
	 * @param httpServletRequest
	 * @return
	 * @throws Exception
	 */
	
	@RequestMapping(value = "/updateNetworkList", produces = MediaType.APPLICATION_JSON,method = RequestMethod.POST)
	@ResponseBody
	@Operation(summary = "${networkManagmentController.modifyNetwork.name}", description = "${networkManagmentController.modifyNetwork.desc}",

			responses = {
					@ApiResponse(responseCode =Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class))) }

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
	public BaseResponse ModifyNetwork(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
									  HttpServletResponse response1, HttpServletRequest httpServletRequest,
									  @Parameter(description = SwaggerAPIDescriptionI.MODIFY_NETWORK, name = "MODIFY_NETWORK", required = true) @RequestBody ModifyNetworkRequestVO requestVO)
			throws Exception {

		final String methodName = "modifyNetwork";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		BaseResponse response = new BaseResponse();
		Connection con = null;
		MComConnectionI mcomCon = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		String loginID = null;

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
			loginID = OAuthUserData.getData().getLoginid();
			response = networkManagmentImpl.modifyNetworkDetails(con, loginID, requestVO, response1);

		} catch (BTSLBaseException be) {
			log.error("", "Exceptin:e=" + be);
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
		}catch(Exception exception) {
			log.error("", "Exceptin:e=" + exception);
			log.errorTrace(methodName, exception);
			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MODIFY_NETWORK_WENT_WRONG, null);
			response.setMessageCode(PretupsErrorCodesI.MODIFY_NETWORK_WENT_WRONG);
			response.setMessage(msg);
		} finally {

			if (mcomCon != null) {
				mcomCon.close("ModifyNetwork");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting:=" + methodName);
			}

		}
		return response;
	}
	
	
	/**
	 * @author sachin.singh,
	 * @param headers
	 * @param response1
	 * @param httpServletRequest
	 * @return
	 * @throws Exception
	 */
	
	@GetMapping(value= "/serviceSetId", produces = MediaType.APPLICATION_JSON)	
	@ResponseBody
	@Operation(summary = "${networkManagmentController.getServiceSetId.name}", description = "${networkManagmentController.getServiceSetId.desc}",

			responses = {
					@ApiResponse(responseCode =Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ServiceIDListResponseVO.class))) }

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
	public BaseResponse getServiceSetId(@Parameter(hidden = true)  @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest)throws Exception{
			
			final String methodName = "getServiceSetId";
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Entered ");
			}

			BaseResponse response = new BaseResponse();
			Connection con = null;
			MComConnectionI mcomCon = null;
			Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
			String loginID = null;

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
				loginID = OAuthUserData.getData().getLoginid();
				//implementation method
				response = networkManagmentImpl.loadServiceSetList(con, response1);

			} catch (BTSLBaseException be) {
				log.error("", "Exceptin:e=" + be);
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
			}catch(Exception exception) {
				log.error("", "Exceptin:e=" + exception);
				log.errorTrace(methodName, exception);
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GET_SERVICE_SETID_WENT_WRONG, null);
				response.setMessageCode(PretupsErrorCodesI.GET_SERVICE_SETID_WENT_WRONG);
				response.setMessage(msg);
			} finally {

				if (mcomCon != null) {
					mcomCon.close("getServiceSetId");
					mcomCon = null;
				}
				if (log.isDebugEnabled()) {
					log.debug(methodName, "Exiting:=" + methodName);
				}

			}
			return response;
	}

	/**
	 * @author sachin.singh,
	 * @param headers
	 * @param networkDetail
	 * @param response1
	 * @param httpServletRequest
	 * @return
	 * @throws Exception
	 */
	
	@RequestMapping(value = "/modifyNetworkDetails", produces = MediaType.APPLICATION_JSON,method = RequestMethod.POST)
	@ResponseBody
	@Operation(summary = "${networkManagmentController.modifyNetworkDetail.name}", description = "${networkManagmentController.modifyNetworkDetail.desc}",

			responses = {
					@ApiResponse(responseCode =Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class))) }

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
	public BaseResponse modifyNetworkDetail(@Parameter(hidden = true)  @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest,
											@Parameter(description = SwaggerAPIDescriptionI.MODIFY_NETWORK, name = "MODIFY_NETWORK", required = true)@RequestBody NetworkVO requestVO)
			throws Exception {

		final String methodName = "modifyNetworkDetail";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		BaseResponse response = new BaseResponse();
		Connection con = null;
		MComConnectionI mcomCon = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		String loginID = null;

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
			loginID = OAuthUserData.getData().getLoginid();
			response = networkManagmentImpl.modifyNetworkDetail(con, loginID, requestVO, response1);

		} catch (BTSLBaseException be) {
			log.error("", "Exceptin:e=" + be);
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
		}catch(Exception exception) {
				log.error("", "Exceptin:e=" + exception);
				log.errorTrace(methodName, exception);
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MODIFY_NETWORK_WENT_WRONG, null);
				response.setMessageCode(PretupsErrorCodesI.MODIFY_NETWORK_WENT_WRONG);
				response.setMessage(msg);
		} finally {

			if (mcomCon != null) {
				mcomCon.close("ModifyNetworkDetail");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting:=" + methodName);
			}

		}
		return response;
	}
}
