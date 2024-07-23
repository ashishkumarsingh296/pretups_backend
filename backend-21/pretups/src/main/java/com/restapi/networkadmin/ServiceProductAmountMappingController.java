package com.restapi.networkadmin;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Locale;


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
import org.springframework.web.bind.annotation.RestController;

import com.btsl.common.BTSLBaseException;
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
import com.btsl.util.OAuthenticationUtil;
import com.restapi.networkadmin.requestVO.AddServiceProductAmountMappingRequestVO;
import com.restapi.networkadmin.requestVO.DeleteServiceProductAmountRequestVO;
import com.restapi.networkadmin.responseVO.AddServiceProductAmountDetailsResponseVO;
import com.restapi.networkadmin.responseVO.LoadServiceAndProductListResponseVO;
import com.restapi.networkadmin.responseVO.SelectorServiceProductAmountDetailsResponseVO;
import com.restapi.networkadmin.responseVO.ServiceProductAmountMappingModifyResponseVO;
import com.restapi.networkadmin.serviceI.ServiceProductAmountMappingServiceI;

import io.swagger.v3.oas.annotations.Parameter;


@io.swagger.v3.oas.annotations.tags.Tag(name = "${ServiceProductAmountMappingController.name}", description = "${ServiceProductAmountMappingController.desc}")//@Api(tags = "Network Admin", defaultValue = "Network Admin")
@RestController
@RequestMapping(value = "/v1/networkadmin")
public class ServiceProductAmountMappingController {

	
		public static final Log LOG = LogFactory.getLog(ServiceProductAmountMappingController.class.getName());
		public static final String CLASS_NAME = "ServiceProductAmountMappingController";

		@Autowired
		ServiceProductAmountMappingServiceI service;

		@GetMapping(value = "/loadallproductamountdetails", produces = MediaType.APPLICATION_JSON)
		/*@ApiOperation(value = "Get All Service Product Mapping Amount Details", response = SelectorServiceProductAmountDetailsResponseVO.class, authorizations = {
				@Authorization(value = "Authorization") })
		@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = SelectorServiceProductAmountDetailsResponseVO.class),
				@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
				@ApiResponse(code = 404, message = "Not Found") })
		*/
		@io.swagger.v3.oas.annotations.Operation(summary = "${loadallproductamountdetails.summary}", description="${loadallproductamountdetails.description}",

				responses = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = SelectorServiceProductAmountDetailsResponseVO.class))
								)
						}

						),


						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_CODE, description = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_UNAUTH_RESPONSE_CODE, description = com.btsl.util.Constants.API_UNAUTH_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_CODE, description = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						})
				}
		)
		public SelectorServiceProductAmountDetailsResponseVO loadSelectorAmountDetails(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
				HttpServletResponse response1) throws Exception {

			final String methodName = "loadSelectorAmountDetails";
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "Entered ");
			}

			Connection con = null;
			MComConnectionI mcomCon = null;
			Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
			String loginUserID = null;
			UserDAO userDAO= null;
			UserVO userVO = null;
			SelectorServiceProductAmountDetailsResponseVO response = new SelectorServiceProductAmountDetailsResponseVO();
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
				loginUserID = OAuthUserData.getData().getLoginid();
				userDAO = new UserDAO();
	    	    String loginID = OAuthUserData.getData().getLoginid();
	    	    userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);			

				response = service.loadSelectorAmountDetails(con, userVO);
				response.setStatus((HttpStatus.SC_OK));
	    		String resmsg = RestAPIStringParser.getMessage(locale,
	    				PretupsErrorCodesI.SUCCESSFULLY_LOAD_SELECTOR_AMOUNT_DETAILS, null);
	    		response.setMessage(resmsg);
	    		response.setMessageCode(PretupsErrorCodesI.SUCCESSFULLY_LOAD_SELECTOR_AMOUNT_DETAILS);

			} catch (BTSLBaseException be) {
				LOG.error("", "Exceptin:e=" + be);
				LOG.errorTrace(methodName, be);
				String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
				response.setMessageCode(be.getMessageKey());
				response.setMessage(msg);

				if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
					response.setStatus(HttpStatus.SC_UNAUTHORIZED);
					response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				} else {
					response.setStatus(HttpStatus.SC_BAD_REQUEST);
					response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				}
			} catch (Exception e) {
				LOG.error(methodName, "Exception:e=" + e);
				LOG.errorTrace(methodName, e);
				response.setStatus((HttpStatus.SC_BAD_REQUEST));
				String resmsg = RestAPIStringParser.getMessage(
						new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
						e.getMessage(), null);
				response.setMessage(resmsg);
				response.setMessageCode(e.getMessage());
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setMessageCode(PretupsErrorCodesI.FAILED_LOAD_SELECTOR_AMOUNT_DETAILS);
				
			} finally {

				if (mcomCon != null) {
					mcomCon.close(methodName);
					mcomCon = null;
				}
				if (LOG.isDebugEnabled()) {
					LOG.debug(CLASS_NAME, "Exiting:=" + methodName);
				}

			}
			return response;

		}
		@PostMapping(value = "/addserviceproductamountmappingdetails", produces = MediaType.APPLICATION_JSON)
		/*@ApiOperation(value = "Add Service Product Amount Mapping Details", response = AddServiceProductAmountDetailsResponseVO.class, authorizations = {
				@Authorization(value = "Authorization") })
		@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = AddServiceProductAmountDetailsResponseVO.class),
				@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
				@ApiResponse(code = 404, message = "Not Found") })
		*/
		@io.swagger.v3.oas.annotations.Operation(summary = "${addserviceproductamountmappingdetails.summary}", description="${addserviceproductamountmappingdetails.description}",

				responses = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = AddServiceProductAmountDetailsResponseVO.class))
								)
						}

						),


						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_CODE, description = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_UNAUTH_RESPONSE_CODE, description = com.btsl.util.Constants.API_UNAUTH_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_CODE, description = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						})
				}
		)


		public AddServiceProductAmountDetailsResponseVO addServiceProductAmountMappingDetails(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
				HttpServletResponse response1, @RequestBody AddServiceProductAmountMappingRequestVO requestVO) throws Exception {

			final String methodName = "addServiceProductAmountMappingDetails";
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "Entered ");
			}

			Connection con = null;
			MComConnectionI mcomCon = null;
			Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
			String loginUserID = null;
			UserDAO userDAO= null;
			UserVO userVO = null;
			AddServiceProductAmountDetailsResponseVO response = new AddServiceProductAmountDetailsResponseVO();
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
				loginUserID = OAuthUserData.getData().getLoginid();
				userDAO = new UserDAO();
	    	    String loginID = OAuthUserData.getData().getLoginid();
	    	    userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);			

				response = service.addServiceProductAmountMappingDetails(con, userVO
						,requestVO);
				response.setStatus((HttpStatus.SC_OK));
	    		String resmsg = RestAPIStringParser.getMessage(locale,
	    				PretupsErrorCodesI.SUCCESSFULLY_ADD_SERVICE_PRODUCT_AMOUNT_MAPPING_DETAILS, null);
	    		response.setMessage(resmsg);
	    		response.setMessageCode(PretupsErrorCodesI.SUCCESSFULLY_ADD_SERVICE_PRODUCT_AMOUNT_MAPPING_DETAILS);

			} catch (BTSLBaseException be) {
				LOG.error("", "Exceptin:e=" + be);
				LOG.errorTrace(methodName, be);
				String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
				response.setMessageCode(be.getMessageKey());
				response.setMessage(msg);

				if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
					response.setStatus(HttpStatus.SC_UNAUTHORIZED);
					response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				} else {
					response.setStatus(HttpStatus.SC_BAD_REQUEST);
					response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				}
			} catch (Exception e) {
				LOG.error(methodName, "Exception:e=" + e);
				LOG.errorTrace(methodName, e);
				response.setStatus((HttpStatus.SC_BAD_REQUEST));
				String resmsg = RestAPIStringParser.getMessage(new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
						e.getMessage(), null);
				response.setMessage(resmsg);
				response.setMessageCode(e.getMessage());
				response.setMessageCode(PretupsErrorCodesI.FAILED_ADD_SERVICE_PRODUCT_AMOUNT_MAPPING_DETAILS);
			} finally {

				if (mcomCon != null) {
					mcomCon.close(methodName);
					mcomCon = null;
				}
				if (LOG.isDebugEnabled()) {
					LOG.debug(CLASS_NAME, "Exiting:=" + methodName);
				}

			}
			return response;

		}
		@PostMapping(value = "/modifyserviceproductamountmappingdetails", produces = MediaType.APPLICATION_JSON)
		/*@ApiOperation(value = "Modify Service Product Amount Mapping Details", response = ServiceProductAmountMappingModifyResponseVO.class, authorizations = {
				@Authorization(value = "Authorization") })
		@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = ServiceProductAmountMappingModifyResponseVO.class),
				@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
				@ApiResponse(code = 404, message = "Not Found") })
		*/

		@io.swagger.v3.oas.annotations.Operation(summary = "${modifyserviceproductamountmappingdetails.summary}", description="${modifyserviceproductamountmappingdetails.description}",

				responses = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ServiceProductAmountMappingModifyResponseVO.class))
								)
						}

						),


						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_CODE, description = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_UNAUTH_RESPONSE_CODE, description = com.btsl.util.Constants.API_UNAUTH_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_CODE, description = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						})
				}
		)

		public ServiceProductAmountMappingModifyResponseVO modifyServiceProductAmountMappingDetails(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
				HttpServletResponse response1, @RequestBody AddServiceProductAmountMappingRequestVO requestVO) throws Exception {

			final String methodName = "modifyServiceProductAmountMappingDetails";
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "Entered ");
			}

			Connection con = null;
			MComConnectionI mcomCon = null;
			Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
			String loginUserID = null;
			UserDAO userDAO= null;
			UserVO userVO = null;
			ServiceProductAmountMappingModifyResponseVO response = new ServiceProductAmountMappingModifyResponseVO();
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
				loginUserID = OAuthUserData.getData().getLoginid();
				userDAO = new UserDAO();
	    	    String loginID = OAuthUserData.getData().getLoginid();
	    	    userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);			

				int count = service.modifyServiceProductAmountMapping(con, userVO
						,requestVO);
				if(count>0) {
				response.setStatus((HttpStatus.SC_OK));
	    		String resmsg = RestAPIStringParser.getMessage(locale,
	    				PretupsErrorCodesI.SERVICE_PRODUCT_AMOUNT_MAPPING_MODIFY_SUCCESSFULLY, null);
	    		response.setMessage(resmsg);
	    		response.setMessageCode(PretupsErrorCodesI.SERVICE_PRODUCT_AMOUNT_MAPPING_MODIFY_SUCCESSFULLY);
				}
				else {
					throw new BTSLBaseException(methodName, PretupsErrorCodesI.SERVICE_PRODUCT_AMOUNT_MAPPING_MODIFY_FAILED,"");
				}
			} catch (BTSLBaseException be) {
				LOG.error("", "Exceptin:e=" + be);
				LOG.errorTrace(methodName, be);
				String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
				response.setMessageCode(be.getMessageKey());
				response.setMessage(msg);

				if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
					response.setStatus(HttpStatus.SC_UNAUTHORIZED);
					response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				} else {
					response.setStatus(HttpStatus.SC_BAD_REQUEST);
					response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				}
			} catch (Exception e) {
				LOG.error(methodName, "Exception:e=" + e);
				LOG.errorTrace(methodName, e);
				response.setStatus((HttpStatus.SC_BAD_REQUEST));
				String resmsg = RestAPIStringParser.getMessage(
						new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
						e.getMessage(), null);
				response.setMessage(resmsg);
				response.setMessageCode(e.getMessage());
				response.setMessageCode(PretupsErrorCodesI.SERVICE_PRODUCT_AMOUNT_MAPPING_MODIFY_FAILED);
			} finally {

				if (mcomCon != null) {
					mcomCon.close(methodName);
					mcomCon = null;
				}
				if (LOG.isDebugEnabled()) {
					LOG.debug(CLASS_NAME, "Exiting:=" + methodName);
				}

			}
			return response;

		}
		@PostMapping(value = "/deleteserviceproductamountmappingdetails", produces = MediaType.APPLICATION_JSON)
		/*@ApiOperation(value = "Delete Service Product Amount Mapping Detail", response = ServiceProductAmountMappingModifyResponseVO.class, authorizations = {
				@Authorization(value = "Authorization") })
		@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = ServiceProductAmountMappingModifyResponseVO.class),
				@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
				@ApiResponse(code = 404, message = "Not Found") })
		*/

		@io.swagger.v3.oas.annotations.Operation(summary = "${deleteserviceproductamountmappingdetails.summary}", description="${deleteserviceproductamountmappingdetails.description}",

				responses = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ServiceProductAmountMappingModifyResponseVO.class))
								)
						}

						),


						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_CODE, description = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_UNAUTH_RESPONSE_CODE, description = com.btsl.util.Constants.API_UNAUTH_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_CODE, description = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						})
				}
		)

		public ServiceProductAmountMappingModifyResponseVO deleteServiceProductAmountMappingDetails(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
				HttpServletResponse response1, @RequestBody DeleteServiceProductAmountRequestVO requestVO) throws Exception {

			final String methodName = "deleteServiceProductAmountMappingDetails";
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "Entered ");
			}

			Connection con = null;
			MComConnectionI mcomCon = null;
			Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
			String loginUserID = null;
			UserDAO userDAO= null;
			UserVO userVO = null;
			ServiceProductAmountMappingModifyResponseVO response = new ServiceProductAmountMappingModifyResponseVO();
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
				loginUserID = OAuthUserData.getData().getLoginid();
				userDAO = new UserDAO();
	    	    String loginID = OAuthUserData.getData().getLoginid();
	    	    userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);			

				int count = service.deleteServiceProductAmountMapping(con, userVO
						,requestVO.getServiceId(),requestVO.getProductId());
				if(count>0) {
				response.setStatus((HttpStatus.SC_OK));
	    		String resmsg = RestAPIStringParser.getMessage(locale,
	    				PretupsErrorCodesI.SERVICE_PRODUCT_AMOUNT_MAPPING_DELETE_SUCCESSFULLY, null);
	    		response.setMessage(resmsg);
	    		response.setMessageCode(PretupsErrorCodesI.SERVICE_PRODUCT_AMOUNT_MAPPING_DELETE_SUCCESSFULLY);
				}
				else {
					throw new BTSLBaseException(methodName, PretupsErrorCodesI.SERVICE_PRODUCT_AMOUNT_MAPPING_DELETE_FAILED,"");
				}
			} catch (BTSLBaseException be) {
				LOG.error("", "Exceptin:e=" + be);
				LOG.errorTrace(methodName, be);
				String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
				response.setMessageCode(be.getMessageKey());
				response.setMessage(msg);

				if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
					response.setStatus(HttpStatus.SC_UNAUTHORIZED);
					response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				} else {
					response.setStatus(HttpStatus.SC_BAD_REQUEST);
					response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				}
			} catch (Exception e) {
				LOG.error(methodName, "Exception:e=" + e);
				LOG.errorTrace(methodName, e);
				response.setStatus((HttpStatus.SC_BAD_REQUEST));
				String resmsg = RestAPIStringParser.getMessage(
						new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
						e.getMessage(), null);
				response.setMessage(resmsg);
				response.setMessageCode(e.getMessage());
				response.setMessageCode(PretupsErrorCodesI.SERVICE_PRODUCT_AMOUNT_MAPPING_DELETE_FAILED);
			} finally {

				if (mcomCon != null) {
					mcomCon.close(methodName);
					mcomCon = null;
				}
				if (LOG.isDebugEnabled()) {
					LOG.debug(CLASS_NAME, "Exiting:=" + methodName);
				}

				
				
			}
			return response;

		}
		@GetMapping(value = "/srvprdtamtmapservicelist", produces = MediaType.APPLICATION_JSON)
		/*@ApiOperation(value = "Service Product Amount Mapping Load Service and Product List", response = LoadServiceAndProductListResponseVO.class, authorizations = {
				@Authorization(value = "Authorization") })
		@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = LoadServiceAndProductListResponseVO.class),
				@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
				@ApiResponse(code = 404, message = "Not Found") })
		*/

		@io.swagger.v3.oas.annotations.Operation(summary = "${srvprdtamtmapservicelist.summary}", description="${srvprdtamtmapservicelist.description}",

				responses = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = LoadServiceAndProductListResponseVO.class))
								)
						}

						),


						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_CODE, description = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_UNAUTH_RESPONSE_CODE, description = com.btsl.util.Constants.API_UNAUTH_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_CODE, description = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						})
				}
		)

		public LoadServiceAndProductListResponseVO loadServiceProductAmountMappingServiceAndProductList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
				HttpServletResponse response1) throws Exception {

			final String methodName = "loadServiceProductAmountMappingServiceAndProductList";
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "Entered ");
			}

			Connection con = null;
			MComConnectionI mcomCon = null;
			Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
			String loginUserID = null;
			UserDAO userDAO= null;
			UserVO userVO = null;
			LoadServiceAndProductListResponseVO response = new LoadServiceAndProductListResponseVO();
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
				loginUserID = OAuthUserData.getData().getLoginid();
				userDAO = new UserDAO();
	    	    String loginID = OAuthUserData.getData().getLoginid();
	    	    userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);			

				 response = service.loadServiceAndProductList(con, userVO);
				
				response.setStatus((HttpStatus.SC_OK));
	    		String resmsg = RestAPIStringParser.getMessage(locale,
	    				PretupsErrorCodesI.SERVICE_PRODUCT_AMOUNT_MAPPING_LOAD_SERVICE_AND_PRODUCT_LIST_SUCCESSFULLY, null);
	    		response.setMessage(resmsg);
	    		response.setMessageCode(PretupsErrorCodesI.SERVICE_PRODUCT_AMOUNT_MAPPING_LOAD_SERVICE_AND_PRODUCT_LIST_SUCCESSFULLY);
				
			} catch (BTSLBaseException be) {
				LOG.error(methodName, "Exceptin:e=" + be);
				LOG.errorTrace(methodName, be);
				String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
				response.setMessageCode(be.getMessageKey());
				response.setMessage(msg);

				if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
					response.setStatus(HttpStatus.SC_UNAUTHORIZED);
					response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				} else {
					response.setStatus(HttpStatus.SC_BAD_REQUEST);
					response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				}
			} catch (Exception e) {
				LOG.error(methodName, "Exception:e=" + e);
				LOG.errorTrace(methodName, e);
				response.setStatus((HttpStatus.SC_BAD_REQUEST));
				String resmsg = RestAPIStringParser.getMessage(
						new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
						e.getMessage(), null);
				response.setMessage(resmsg);
				response.setMessageCode(e.getMessage());
				response.setMessageCode(PretupsErrorCodesI.SERVICE_PRODUCT_AMOUNT_MAPPING_LOAD_SERVICE_AND_PRODUCT_LIST_FAILED);
			} finally {

				if (mcomCon != null) {
					mcomCon.close(methodName);
					mcomCon = null;
				}
				if (LOG.isDebugEnabled()) {
					LOG.debug(CLASS_NAME, "Exiting:=" + methodName);
				}

				
				
			}
			return response;

		}

}
