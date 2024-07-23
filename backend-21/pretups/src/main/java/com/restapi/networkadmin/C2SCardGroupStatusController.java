package com.restapi.networkadmin;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Locale;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
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
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.OAuthenticationUtil;
import com.restapi.networkadmin.requestVO.SaveC2ScardGroupStatusListRequestVO;
import com.restapi.networkadmin.requestVO.LoadCardGroupStatusListRequestVO;
import com.restapi.networkadmin.responseVO.C2SCardGroupStatusResponseVO;
import com.restapi.networkadmin.responseVO.C2SCardGroupStatusSaveResponseVO;
import com.restapi.networkadmin.serviceI.C2SCardGroupStatusServiceI;

import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${C2SCardGroupStatusController.name}", description = "${C2SCardGroupStatusController.desc}")//@Api(tags="Network Admin")
@RestController
@RequestMapping(value = "/v1/networkadmin")
public class C2SCardGroupStatusController {
	public static final Log log = LogFactory.getLog(AssociateO2CTransferRuleController.class.getName());
	
	
	
	
		
		@Autowired
		C2SCardGroupStatusServiceI service;
		
		
		@PostMapping(value= "/loadc2scardgroupstatuslist", produces = MediaType.APPLICATION_JSON)	
		@ResponseBody
		/*@ApiOperation(value = "load c2s cardgroup status list",
		           response = C2SCardGroupStatusResponseVO.class,
		           authorizations = {
		               @Authorization(value = "Authorization")})
		@ApiResponses(value = {
		      @ApiResponse(code = 200, message = "OK", response = C2SCardGroupStatusResponseVO.class),
		      @ApiResponse(code = 400, message = "Bad Request" ),
		      @ApiResponse(code = 401, message = "Unauthorized"),
		      @ApiResponse(code = 404, message = "Not Found")
		      })
		*/

		@io.swagger.v3.oas.annotations.Operation(summary = "${loadc2scardgroupstatuslist.summary}", description="${loadc2scardgroupstatuslist.description}",

				responses = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = C2SCardGroupStatusResponseVO.class))
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


		public C2SCardGroupStatusResponseVO loadC2SCardGroupStatusList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
				HttpServletResponse response1, HttpServletRequest httpServletRequest,
				@RequestBody  LoadCardGroupStatusListRequestVO requestVO) throws Exception {
			final String methodName = "loadC2SCardGroupStatusList";
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Entered ");
			}
			C2SCardGroupStatusResponseVO response = new C2SCardGroupStatusResponseVO();
			
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
				
				response=service.loadC2SCardGroupStatusList(con, userVO, requestVO);
				response1.setStatus((HttpStatus.SC_OK));
			}
			catch (BTSLBaseException be) {
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
			}
			catch (Exception e) {
	            log.error(methodName, "Exceptin:e=" + e.getMessage());
	            log.errorTrace(methodName, e);
	            response1.setStatus(HttpStatus.SC_BAD_REQUEST);
	            response.setStatus(HttpStatus.SC_BAD_REQUEST);
	            response.setMessageCode(PretupsErrorCodesI.FAILED_LOAD_CARD_GROUP_LIST_FOR_CARD_GROUP_STATUS);
	        }
			finally {
				if (mcomCon != null) {
					mcomCon.close(methodName);
					mcomCon = null;
				}
				if (log.isDebugEnabled()) {
					log.debug(methodName, "Exiting:=" + methodName);
				}
			}
			

			return response;
		}
		
		@PostMapping(value= "/savec2scardgroupstatuslist", produces = MediaType.APPLICATION_JSON)	
		@ResponseBody
		/*@ApiOperation(value = "save c2s cardgroup status list",
		           response = C2SCardGroupStatusSaveResponseVO.class,
		           authorizations = {
		               @Authorization(value = "Authorization")})
		@ApiResponses(value = {
		      @ApiResponse(code = 200, message = "OK", response = C2SCardGroupStatusSaveResponseVO.class),
		      @ApiResponse(code = 400, message = "Bad Request" ),
		      @ApiResponse(code = 401, message = "Unauthorized"),
		      @ApiResponse(code = 404, message = "Not Found")
		      })
		*/

		@io.swagger.v3.oas.annotations.Operation(summary = "${savec2scardgroupstatuslist.summary}", description="${savec2scardgroupstatuslist.description}",

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

		public C2SCardGroupStatusSaveResponseVO saveC2SCardGroupStatusList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
				HttpServletResponse response1, HttpServletRequest httpServletRequest,
				@RequestBody  SaveC2ScardGroupStatusListRequestVO requestVO
				) throws Exception {
			final String methodName = "loadC2SCardGroupStatusList";
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Entered ");
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
				response = service.saveC2SCardGroupStatusList(con, mcomCon, userVO, requestVO);
		            response1.setStatus((HttpStatus.SC_OK));
		            response.setStatus(HttpStatus.SC_OK);
		            String msg = RestAPIStringParser.getMessage(locale,
							PretupsErrorCodesI.SUCCESSFULLY_SAVE_CARD_GROUP_LIST_FOR_CARD_GROUP_STATUS, null);
		            response.setMessage(msg);
			}
			catch (BTSLBaseException be) {
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
			}
			catch (Exception e) {
	            log.error(methodName, "Exceptin:e=" + e.getMessage());
	            log.errorTrace(methodName, e);
	            response1.setStatus(HttpStatus.SC_BAD_REQUEST);
	            response.setStatus(HttpStatus.SC_BAD_REQUEST);
	            response.setMessageCode(PretupsErrorCodesI.FAILED_SAVE_CARD_GROUP_LIST_FOR_CARD_GROUP_STATUS);
	        }
			finally {
				if (mcomCon != null) {
					mcomCon.close(methodName);
					mcomCon = null;
				}
				if (log.isDebugEnabled()) {
					log.debug(methodName, "Exiting:=" + methodName);
				}
			}
			

			return response;
		}

}
