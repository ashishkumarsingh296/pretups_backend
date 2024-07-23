package com.restapi.channelAdmin.restrictedlistmgmt.controller;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import javax.ws.rs.core.MediaType;

import com.restapi.channelAdmin.restrictedlistmgmt.requestVO.*;
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
import com.restapi.channelAdmin.restrictedlistmgmt.responseVO.ApprovalRestrictedListResponseVO;
import com.restapi.channelAdmin.restrictedlistmgmt.responseVO.LoadDropdownsResponseVO;
import com.restapi.channelAdmin.restrictedlistmgmt.responseVO.LoadSubscriberListForBlackListSingleResponseVO;
import com.restapi.channelAdmin.restrictedlistmgmt.responseVO.SearchUserListBasedOnkeywordResponseVO;
import com.restapi.channelAdmin.restrictedlistmgmt.responseVO.UploadFileResponseVO;
import com.restapi.channelAdmin.restrictedlistmgmt.responseVO.ViewRestrictedListResponseVO;
import com.restapi.channelAdmin.restrictedlistmgmt.responseVO.ViewRestrictedResponseVO;
import com.restapi.channelAdmin.restrictedlistmgmt.responseVO.LoadSubscriberListForDeleteResponseVO;
import com.restapi.channelAdmin.restrictedlistmgmt.responseVO.LoadSubscriberListForUnBlackResponseVO;
import com.restapi.channelAdmin.restrictedlistmgmt.serviceI.RestrictedListMgmtServiceI;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${RestrictedListMgmtController.name}", description = "${RestrictedListMgmtController.desc}")//@Api(tags = "Channel Admin", value = "Channel Admin")
@RestController
@RequestMapping(value = "/v1/channeladmin/restrictedlistmgmt")
public class RestrictedListMgmtController {
	
	public static final Log log = LogFactory.getLog(RestrictedListMgmtController.class.getName());
	public static final String classname = "RestrictedListMgmtController";
	
	@Autowired
	RestrictedListMgmtServiceI restrictedListMgmtServiceI;
	
	
	@GetMapping(value = "/loaddropdowns", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "This method is used to load the information related to the login user", response = LoadDropdownsResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = LoadDropdownsResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${loaddropdowns.summary}", description="${loaddropdowns.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = LoadDropdownsResponseVO.class))
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

	public LoadDropdownsResponseVO loadDropdowns(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse response1,
			HttpServletRequest httpServletRequest)
			throws Exception {

		final String methodName = "loadDropdowns";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		LoadDropdownsResponseVO response = new LoadDropdownsResponseVO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		String loginID = null;
		
		UserVO userVO = null;
		UserDAO userDAO = null;

		try {

			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			loginID = OAuthUserData.getData().getLoginid();
			
			userVO = new UserVO();
			userDAO = new UserDAO();
			userVO  = userDAO.loadUsersDetails(con, OAuthUserData.getData().getMsisdn());
			
			
			response = restrictedListMgmtServiceI.loadDropdowns(headers,httpServletRequest,response1,con,mcomCon,locale,userVO,response);

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
		}
		catch (Exception e) {
			log.error(methodName, "Exception:e=" + e);
			log.errorTrace(methodName, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.LIST_NOT_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.LIST_NOT_FOUND);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@GetMapping(value = "/searchuserlistbasedonkeyword", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "This method is used to load the user list whose name is entered in the text box.", response = SearchUserListBasedOnkeywordResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = SearchUserListBasedOnkeywordResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${searchuserlistbasedonkeyword.summary}", description="${searchuserlistbasedonkeyword.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = SearchUserListBasedOnkeywordResponseVO.class))
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


	public SearchUserListBasedOnkeywordResponseVO searchUserListBasedOnkeyword(
			@Parameter(hidden = true)@RequestHeader MultiValueMap<String, String> headers, HttpServletResponse response1,
			HttpServletRequest httpServletRequest,
			@Parameter(description = "userName", required = true) @RequestParam("userName") String userName,
			@Parameter(description = "categoryCode", required = true) @RequestParam("categoryCode") String categoryCode,
			@Parameter(description = "geoDomain", required = true) @RequestParam("geoDomain") String geoDomain
			)
			throws Exception {
		
		final String methodName = "searchuserlistbasedonkeyword";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		SearchUserListBasedOnkeywordResponseVO response = new SearchUserListBasedOnkeywordResponseVO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		String loginID = null;
		
		UserVO userVO = null;
		UserDAO userDAO = null;

		try {

			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			loginID = OAuthUserData.getData().getLoginid();
			
			userVO = new UserVO();
			userDAO = new UserDAO();
			userVO  = userDAO.loadUsersDetails(con, OAuthUserData.getData().getMsisdn());
			
			
			response = restrictedListMgmtServiceI.searchUserListBasedOnkeyword(headers,httpServletRequest,response1,con,mcomCon,locale,userVO,response,userName,categoryCode,geoDomain);

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
		} 
		catch (Exception e) {
			log.error(methodName, "Exception:e=" + e);
			log.errorTrace(methodName, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.LIST_NOT_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.LIST_NOT_FOUND);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@GetMapping(value = "/loadsubscriberlistfordelete", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "This methods populate the Subscriber List for Delete", response = LoadSubscriberListForDeleteResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = LoadSubscriberListForDeleteResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${loadsubscriberlistfordelete.summary}", description="${loadsubscriberlistfordelete.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = LoadSubscriberListForDeleteResponseVO.class))
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


	public LoadSubscriberListForDeleteResponseVO loadSubscriberListForDelete(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse response1,
			HttpServletRequest httpServletRequest,
			@Parameter(description = "msisdnStr", required = true) @RequestParam("msisdnStr") String msisdnStr,
			@Parameter(description = "ownerID", required = true) @RequestParam("ownerID") String ownerID
			)
			throws Exception {
		
		final String methodName = "loadSubscriberListForDelete";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		LoadSubscriberListForDeleteResponseVO response = new LoadSubscriberListForDeleteResponseVO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		String loginID = null;
		
		UserVO userVO = null;
		UserDAO userDAO = null;

		try {

			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			loginID = OAuthUserData.getData().getLoginid();
			
			userVO = new UserVO();
			userDAO = new UserDAO();
			userVO  = userDAO.loadUsersDetails(con, OAuthUserData.getData().getMsisdn());
			
			
			response = restrictedListMgmtServiceI.loadSubscriberListForDelete(headers,httpServletRequest,response1,con,mcomCon,locale,userVO,response,msisdnStr,ownerID);

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
		} 
		catch (Exception e) {
			log.error(methodName, "Exception:e=" + e);
			log.errorTrace(methodName, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.LIST_NOT_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.LIST_NOT_FOUND);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
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
	
	
	
	
	
	
	
	
	
	
	
	
	@PostMapping(value = "/deleterestrictedsubscriber", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "This methods will process the deletion of subscribers", response = com.restapi.networkadmin.commissionprofile.responseVO.CommissionProfileViewResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = com.restapi.networkadmin.commissionprofile.responseVO.CommissionProfileViewResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${deleterestrictedsubscriber.summary}", description="${deleterestrictedsubscriber.description}",

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


	public BaseResponse deleteRestrictedSubscriber(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse response1, HttpServletRequest httpServletRequest,
			@Parameter(description = "Delete Restricted Subscriber RequestVO",required = true) @RequestBody DeleteRestrictedSubscriberRequestVO requestVO)
			throws Exception {

		final String methodName = "deleteRestrictedSubscriber";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		
		BaseResponse response = new BaseResponse();
		Connection con = null;
		MComConnectionI mcomCon = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		String loginID = null;
		
		UserVO userVO = null;
		UserDAO userDAO = null;

		try {

			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			loginID = OAuthUserData.getData().getLoginid();
			
			userVO = new UserVO();
			userDAO = new UserDAO();
			userVO  = userDAO.loadUsersDetails(con, OAuthUserData.getData().getMsisdn());
			
			
			response = restrictedListMgmtServiceI.deleteRestrictedSubscriber(headers,httpServletRequest,response1,con,mcomCon,locale,userVO,response,requestVO);

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
		} 
		catch (Exception e) {
			log.error(methodName, "Exception:e=" + e);
			log.errorTrace(methodName, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.SUBSCRIBER_DELETION_FAILED, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.SUBSCRIBER_DELETION_FAILED);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
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
	

@GetMapping(value="/viewrestrictedlist", produces = MediaType.APPLICATION_JSON )
	@ResponseBody
	/*@ApiOperation(value = "View Restricted List", response = ViewRestrictedListResponseVO.class, authorizations = { @Authorization(value = "Authorization")})

	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = ViewRestrictedListResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

@io.swagger.v3.oas.annotations.Operation(summary = "${viewrestrictedlist.summary}", description="${viewrestrictedlist.description}",

		responses = {
				@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
						@io.swagger.v3.oas.annotations.media.Content(
								mediaType = "application/json",
								array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ViewRestrictedListResponseVO.class))
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


	public ViewRestrictedListResponseVO viewRestrictedList(
		@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse responseSwagger, HttpServletRequest httpServletRequest,
			@Parameter(description = "userName", required = true) @RequestParam("userName") String userName,
			@Parameter(description = "categoryCode", required = true) @RequestParam("categoryCode") String categoryCode,
			@Parameter(description = "geoDomain", required = true) @RequestParam("geoDomain") String geoDomain,
			@Parameter(description = "fromDateStr", required = true) @RequestParam("fromDateStr") String fromDateStr,
			@Parameter(description = "toDateStr", required = true) @RequestParam("toDateStr") String toDateStr
			
	) throws Exception {
		final String methodName = "viewRestrictedList";
        
		if (log.isDebugEnabled()) {
			log.debug(classname, methodName);
		}
		 Connection con = null;MComConnectionI mcomCon = null;
	        Date currentDate = null;
	        ViewRestrictedListResponseVO response = new ViewRestrictedListResponseVO();
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
	    		OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, responseSwagger);
	    		mcomCon = new MComConnection();
	    		con = mcomCon.getConnection();
	    	    String loginID = OAuthUserData.getData().getLoginid();
	    	    userDAO = new UserDAO();
	    	    userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
	    	    ArrayList<ViewRestrictedResponseVO> subList=restrictedListMgmtServiceI.loadRestrictedSubs( con,userVO, userName, categoryCode, geoDomain, fromDateStr, toDateStr );

	    	    //To use same functions written in struts for reuse - preparing request objects

	    	    // preparing Transfer Object
	    	    response.setSubRestrictedList(subList);
	    	    response.setStatus((HttpStatus.SC_OK));
	    	    String resmsg = RestAPIStringParser.getMessage(locale,
	    				PretupsErrorCodesI.SUCCESSFULLY_LOAD_VIEW_RESTRICTED_LIST, null);
	    		response.setMessage(resmsg);
	    		response.setMessageCode(PretupsErrorCodesI.SUCCESSFULLY_LOAD_VIEW_RESTRICTED_LIST);
	                   
	        } catch (BTSLBaseException e) {
	            log.error(methodName, "Exceptin:e=" + e);
	            log.errorTrace(methodName, e);
	            String msg = RestAPIStringParser.getMessage(locale, e.getMessageKey(), null);
	    		response.setMessageCode(e.getMessageKey());
	    		response.setMessage(msg);

	    		if (Arrays.asList(PretupsI.OAUTHCODES).contains(e.getMessage())) {
	    			responseSwagger.setStatus(HttpStatus.SC_UNAUTHORIZED);
	    			response.setStatus(HttpStatus.SC_UNAUTHORIZED);
	    		} else {
	    			responseSwagger.setStatus(HttpStatus.SC_BAD_REQUEST);
	    			response.setStatus(HttpStatus.SC_BAD_REQUEST);
	    		}

	        }  catch (Exception e) {
	            log.error(methodName, "Exceptin:e=" + e);
	            log.errorTrace(methodName, e);
	            responseSwagger.setStatus(HttpStatus.SC_BAD_REQUEST);
	            response.setStatus(HttpStatus.SC_BAD_REQUEST);
	            String resmsg = RestAPIStringParser.getMessage(
						new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
						PretupsErrorCodesI.FAILED_LOAD_VIEW_RESTRICTED_LIST, null);
	            response.setMessage(resmsg);
	            response.setMessageCode(PretupsErrorCodesI.FAILED_LOAD_VIEW_RESTRICTED_LIST);
	        } finally {
	        	
	        	if(mcomCon != null){
	        		mcomCon.close(classname+methodName);
	        		mcomCon=null;}
	            if (log.isDebugEnabled()) {
	                log.debug(methodName, "Exiting forward=" );
	            }

	           }
	    	 return response;


	}

	@PostMapping(value="/uploadrestrictedlist", produces = MediaType.APPLICATION_JSON )
	@ResponseBody
	/*@ApiOperation(value = "upload Restricted List", response = UploadFileResponseVO.class, authorizations = { @Authorization(value = "Authorization")})

	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = UploadFileResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${uploadrestrictedlist.summary}", description="${uploadrestrictedlist.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UploadFileResponseVO.class))
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

	public UploadFileResponseVO uploadRestrictedList(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse responseSwagger, HttpServletRequest httpServletRequest,
			@Parameter(description = "userName", required = true) @RequestParam("userName") String userName,
			@Parameter(description = "categoryCode", required = true) @RequestParam("categoryCode") String categoryCode,
			@Parameter(description = "domain", required = true) @RequestParam("domain") String domain,
			@Parameter(description = "subscriberType", required = true) @RequestParam("subscriberType") String subscriberType,
			@Parameter(description = "geoDomain", required = true) @RequestParam("geoDomain") String geoDomain,
			@Valid @RequestBody  UploadFileRequestVO uploadFileRequestVO
			
	) throws Exception {
		final String methodName = "uploadRestrictedList";
		
		if (log.isDebugEnabled()) {
			log.debug(classname, methodName);
		}
		 Connection con = null;MComConnectionI mcomCon = null;
	        Date currentDate = null;
	        UploadFileResponseVO response = new UploadFileResponseVO();
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
	    		OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, responseSwagger);
	    		mcomCon = new MComConnection();
	    		con = mcomCon.getConnection();
	    		userDAO = new UserDAO();
	    	    String loginID = OAuthUserData.getData().getLoginid();
	    	    userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
	    	    response =restrictedListMgmtServiceI.uploadRestrictedList( con,userVO, userName, categoryCode, geoDomain, domain, subscriberType, uploadFileRequestVO , httpServletRequest, response, responseSwagger);
	        } catch (BTSLBaseException e) {
	            log.error(methodName, "Exceptin:e=" + e);
	            log.errorTrace(methodName, e);
	            String msg = RestAPIStringParser.getMessage(locale, e.getMessageKey(), null);
	    		response.setMessageCode(e.getMessageKey());
	    		response.setMessage(msg);

	    		if (Arrays.asList(PretupsI.OAUTHCODES).contains(e.getMessage())) {
	    			responseSwagger.setStatus(HttpStatus.SC_UNAUTHORIZED);
	    			response.setStatus(HttpStatus.SC_UNAUTHORIZED);
	    		} else {
	    			responseSwagger.setStatus(HttpStatus.SC_BAD_REQUEST);
	    			response.setStatus(HttpStatus.SC_BAD_REQUEST);
	    		}

	        }  catch (Exception e) {
	            log.error(methodName, "Exceptin:e=" + e);
	            log.errorTrace(methodName, e);
	            responseSwagger.setStatus(HttpStatus.SC_BAD_REQUEST);
	            response.setStatus(HttpStatus.SC_BAD_REQUEST);
	            String resmsg = RestAPIStringParser.getMessage(
						new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
						PretupsErrorCodesI.FILE_UPLOAD_FAILED, null);
	            response.setMessage(resmsg);
	            response.setMessageCode(PretupsErrorCodesI.FILE_UPLOAD_FAILED);
	        } finally {
	        	
	        	if(mcomCon != null){
	        		mcomCon.close(classname+methodName);
	        		mcomCon=null;}
	            if (log.isDebugEnabled()) {
	                log.debug(methodName, "Exiting forward=" );
	            }

	           }
	    	 return response;


	}
	
	@GetMapping(value="/approvalrestrictedlist", produces = MediaType.APPLICATION_JSON )
	@ResponseBody
	/*@ApiOperation(value = "Approval Restricted List", response = ApprovalRestrictedListResponseVO.class, authorizations = { @Authorization(value = "Authorization")})

	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = ApprovalRestrictedListResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${approvalrestrictedlist.summary}", description="${approvalrestrictedlist.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ApprovalRestrictedListResponseVO.class))
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


	public ApprovalRestrictedListResponseVO approvalRestrictedList(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse responseSwagger, HttpServletRequest httpServletRequest,
			@Parameter(description = "userName", required = true) @RequestParam("userName") String userName,
			@Parameter(description = "categoryCode", required = true) @RequestParam("categoryCode") String categoryCode,
			@Parameter(description = "geoDomain", required = true) @RequestParam("geoDomain") String geoDomain
			
	) throws Exception {
		final String methodName = "approvalRestrictedList";
		ApprovalRestrictedListResponseVO responseVO = new ApprovalRestrictedListResponseVO();
		if (log.isDebugEnabled()) {
			log.debug(classname, methodName);
		}
		 Connection con = null;MComConnectionI mcomCon = null;
	        Date currentDate = null;
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
	    		OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, responseSwagger);
	    		mcomCon = new MComConnection();
	    		con = mcomCon.getConnection();
	    		userDAO = new UserDAO();
	    	    String loginID = OAuthUserData.getData().getLoginid();
	    	    userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
	    	    responseVO =restrictedListMgmtServiceI.approvalRestrictedList( con,userVO, userName, categoryCode,geoDomain,  responseVO);

			//To use same functions written in struts for reuse - preparing request objects

			// preparing Transfer Object

			  responseVO.setStatus((HttpStatus.SC_OK));
	    	   String resmsg = RestAPIStringParser.getMessage(locale,
	    				PretupsErrorCodesI.SUCCESSFULLY_LOAD_APPROVAL_RESTRICTED_LIST, null);
	    		responseVO.setMessage(resmsg);
	    		responseVO.setMessageCode(PretupsErrorCodesI.SUCCESSFULLY_LOAD_APPROVAL_RESTRICTED_LIST);
	                   
	        } catch (BTSLBaseException e) {
	            log.error(methodName, "Exceptin:e=" + e);
	            log.errorTrace(methodName, e);
	            String msg = RestAPIStringParser.getMessage(locale, e.getMessageKey(), null);
	    		responseVO.setMessageCode(e.getMessageKey());
	    		responseVO.setMessage(msg);

	    		if (Arrays.asList(PretupsI.OAUTHCODES).contains(e.getMessage())) {
	    			responseSwagger.setStatus(HttpStatus.SC_UNAUTHORIZED);
	    			responseVO.setStatus(HttpStatus.SC_UNAUTHORIZED);
	    		} else {
	    			responseSwagger.setStatus(HttpStatus.SC_BAD_REQUEST);
	    			responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
	    		}

	        }  catch (Exception e) {
	            log.error(methodName, "Exceptin:e=" + e);
	            log.errorTrace(methodName, e);
	            responseSwagger.setStatus(HttpStatus.SC_BAD_REQUEST);
	            responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
	            String resmsg = RestAPIStringParser.getMessage(
						new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
						PretupsErrorCodesI.FAILED_LOAD_APPROVAL_RESTRICTED_LIST, null);
	            responseVO.setMessage(resmsg);
	            responseVO.setMessageCode(PretupsErrorCodesI.FAILED_LOAD_APPROVAL_RESTRICTED_LIST);
	        } finally {
	        	
	        	if(mcomCon != null){
	        		mcomCon.close(classname+methodName);
	        		mcomCon=null;}
	            if (log.isDebugEnabled()) {
	                log.debug(methodName, "Exiting forward=" );
	            }

	           }
	    	 return responseVO;


	}
	@PostMapping(value="/updateapprstrctdlist", produces = MediaType.APPLICATION_JSON )
	@ResponseBody
	/*@ApiOperation(value = "Update Approval Restricted List", response = BaseResponse.class, authorizations = { @Authorization(value = "Authorization")})

	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = BaseResponse.class),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${updateapprstrctdlist.summary}", description="${updateapprstrctdlist.description}",

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

	public BaseResponse updateApprovalRestrictedList(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse responseSwagger, HttpServletRequest httpServletRequest,
			@Valid @RequestBody List<@Valid SubscriberDetailsRequestVO> request
	) throws Exception {
		final String METHOD_NAME = "updateApprovalRestrictedList";
		BaseResponse responseVO = new BaseResponse();
		if (log.isDebugEnabled()) {
			log.debug(classname, METHOD_NAME);
		}
		 Connection con = null;MComConnectionI mcomCon = null;
	        Date currentDate = null;
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
	    		OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, responseSwagger);
	    		mcomCon = new MComConnection();
	    		con = mcomCon.getConnection();
	    		userDAO = new UserDAO();
	    	    String loginID = OAuthUserData.getData().getLoginid();
	    	    userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
	    	    String status =request.get(0).getStatus();
	    	    
	    	    Integer count =restrictedListMgmtServiceI.updateApprovalSubscriberList( con,userVO, request);

			//To use same functions written in struts for reuse - preparing request objects

			// preparing Transfer Object
	    	    if (count==request.size()) {
	    	    	responseVO.setStatus((HttpStatus.SC_OK));
	    	    	responseSwagger.setStatus(HttpStatus.SC_OK);
	    	    	String msg = null;
	    	    	if(status.equalsIgnoreCase(PretupsI.RES_MSISDN_STATUS_APPROVED)){
	            		if(request.size() == 1) {
	            			msg = RestAPIStringParser.getMessage(locale,
	    							PretupsErrorCodesI.RESTRICTED_SUBSCRIBER_APPROVED_SUCCESSFULLY, new String[] { "" });
	    	            			responseVO.setMessageCode(PretupsErrorCodesI.RESTRICTED_SUBSCRIBER_APPROVED_SUCCESSFULLY);

	            		}else {
	            			msg = RestAPIStringParser.getMessage(locale,
							PretupsErrorCodesI.RESTRICTED_SUBSCRIBER_LIST_APPROVED_SUCCESSFULLY, new String[] { "" });
	            			responseVO.setMessageCode(PretupsErrorCodesI.RESTRICTED_SUBSCRIBER_LIST_APPROVED_SUCCESSFULLY);
	            		}
	    	    	}
	            	else {
	            		if(request.size() == 1) {
	            			msg = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.RESTRICTED_SUBSCRIBER_REJECTED_SUCCESSFULLY, new String[] { "" });
	            			responseVO.setMessageCode(PretupsErrorCodesI.RESTRICTED_SUBSCRIBER_LIST_REJECTED_SUCCESSFULLY);
	            		}
	            		else {
	            			msg = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.RESTRICTED_SUBSCRIBER_LIST_REJECTED_SUCCESSFULLY, new String[] { "" });
		            		responseVO.setMessageCode(PretupsErrorCodesI.RESTRICTED_SUBSCRIBER_LIST_REJECTED_SUCCESSFULLY);
		            	
	            		}
	            	}
	    	        	responseVO.setMessage(msg);
	    	     }
	    	    else if (count>0) {
	    	    	responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
	    	    	responseSwagger.setStatus(PretupsI.RESPONSE_SUCCESS);
                	String msg = null;
	    	    	if(status.equalsIgnoreCase(PretupsI.RES_MSISDN_STATUS_APPROVED)){
	            		msg = RestAPIStringParser.getMessage(locale,
							PretupsErrorCodesI.RESTRICTED_SUBSCRIBER_LIST_APPROVED_PARTIALLY_SUCCESSFULLY, new String[] { "" });
	            		responseVO.setMessageCode(PretupsErrorCodesI.RESTRICTED_SUBSCRIBER_LIST_APPROVED_PARTIALLY_SUCCESSFULLY);

	    	    	}
	            	else {
	            		msg = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.RESTRICTED_SUBSCRIBER_LIST_REJECTED_PARTIALLY_SUCCESSFULLY, new String[] { "" });
	            		responseVO.setMessageCode(PretupsErrorCodesI.RESTRICTED_SUBSCRIBER_LIST_REJECTED_PARTIALLY_SUCCESSFULLY);

	            	}
	    	        	responseVO.setMessage(msg);
	    	   }

	    	    else {
	    	    	if(status.equalsIgnoreCase(PretupsI.RES_MSISDN_STATUS_APPROVED)){
	    	    		if(request.size() == 1) {
	    	    			throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.RESTRICTED_SUBSCRIBER_APPROVED_FAILED, "");
	    	    		}
	    	    		else
	    	    			throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.RESTRICTED_SUBSCRIBER_LIST_APPROVED_FAILED, "");
	    	    		
	    	    	}
	    	    	else {
	    	    		if(request.size() == 1) {
	    	    			throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.RESTRICTED_SUBSCRIBER_REJECTED_FAILED, "");
	    	    			  
	    	    		}
	    	    		
	    	    		else {
	    	    			throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.RESTRICTED_SUBSCRIBER_LIST_REJECTED_FAILED, "");
	    	    		}
	    	    	}
	    	    }
	        } catch (BTSLBaseException e) {
	            log.error(METHOD_NAME, "Exceptin:e=" + e);
	            log.errorTrace(METHOD_NAME, e);
	            String msg = RestAPIStringParser.getMessage(locale, e.getMessageKey(), null);
	    		responseVO.setMessageCode(e.getMessageKey());
	    		responseVO.setMessage(msg);

	    		if (Arrays.asList(PretupsI.OAUTHCODES).contains(e.getMessage())) {
	    			responseSwagger.setStatus(HttpStatus.SC_UNAUTHORIZED);
	    			responseVO.setStatus(HttpStatus.SC_UNAUTHORIZED);
	    		} else {
	    			responseSwagger.setStatus(HttpStatus.SC_BAD_REQUEST);
	    			responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
	    		}

	        }  catch (Exception e) {
	            log.error(METHOD_NAME, "Exceptin:e=" + e);
	            log.errorTrace(METHOD_NAME, e);
	            responseSwagger.setStatus(HttpStatus.SC_BAD_REQUEST);
	            responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
	            String resmsg= null;
	            if(request.get(0).getStatus().equalsIgnoreCase(PretupsI.RES_MSISDN_STATUS_APPROVED)){
	            	 resmsg = RestAPIStringParser.getMessage(
						locale,
						PretupsErrorCodesI.RESTRICTED_SUBSCRIBER_LIST_APPROVED_FAILED, null);
	            	 responseVO.setMessageCode(PretupsErrorCodesI.RESTRICTED_SUBSCRIBER_LIST_APPROVED_FAILED);
	            }
	            else {
	            	resmsg = RestAPIStringParser.getMessage(
	            			locale,PretupsErrorCodesI.RESTRICTED_SUBSCRIBER_LIST_REJECTED_FAILED, null);
		            	 responseVO.setMessageCode(PretupsErrorCodesI.RESTRICTED_SUBSCRIBER_LIST_REJECTED_FAILED);
	            }
	            responseVO.setMessage(resmsg);
	            
	        } finally {
	        	
	        	if(mcomCon != null){
	        		mcomCon.close(classname+METHOD_NAME);
	        		mcomCon=null;}
	            if (log.isDebugEnabled()) {
	                log.debug(METHOD_NAME, "Exiting forward=" );
	            }

	           }
	    	 return responseVO;


	}
	
	
	
	//****UNBLACK api's starts here*******//
	
	
	@GetMapping(value = "/loadsubscriberlistforunblack", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "This methods populate the Subscriber List for Unblack", response = LoadSubscriberListForUnBlackResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = LoadSubscriberListForUnBlackResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${loadsubscriberlistforunblack.summary}", description="${loadsubscriberlistforunblack.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = LoadSubscriberListForUnBlackResponseVO.class))
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

	public LoadSubscriberListForUnBlackResponseVO loadSubscriberListForUnBlack(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse response1,
			HttpServletRequest httpServletRequest,
			@Parameter(description = "msisdnStr", required = true) @RequestParam("msisdnStr") String msisdnStr,
			@Parameter(description = "ownerID", required = true) @RequestParam("ownerID") String ownerID,
			@Parameter(description = "cp2pPayer", required = true) @RequestParam("cp2pPayer") String cp2pPayer,
			@Parameter(description = "cp2pPayee", required = true) @RequestParam("cp2pPayee") String cp2pPayee,
			@Parameter(description = "c2sPayee", required = true) @RequestParam("c2sPayee") String c2sPayee
			)
			throws Exception {
		
		final String methodName = "loadSubscriberListForUnBlack";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		LoadSubscriberListForUnBlackResponseVO response = new LoadSubscriberListForUnBlackResponseVO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		String loginID = null;
		
		UserVO userVO = null;
		UserDAO userDAO = null;

		try {

			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			loginID = OAuthUserData.getData().getLoginid();
			
			userVO = new UserVO();
			userDAO = new UserDAO();
			userVO  = userDAO.loadUsersDetails(con, OAuthUserData.getData().getMsisdn());
			
			
			response = restrictedListMgmtServiceI.loadSubscriberListForUnBlack(headers,httpServletRequest,response1,con,mcomCon,locale,userVO,response,msisdnStr,ownerID,cp2pPayer,cp2pPayee,c2sPayee);

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
		} 
		catch (Exception e) {
			log.error(methodName, "Exception:e=" + e);
			log.errorTrace(methodName, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.LIST_NOT_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.LIST_NOT_FOUND);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
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
	
	
	
	
	
	
	
	@PostMapping(value = "/unblacklistallsubscriber", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "This methods will process the unBlackListing of all the subscribers", response = BaseResponse.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = BaseResponse.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${unblacklistallsubscriber.summary}", description="${unblacklistallsubscriber.description}",

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

	public BaseResponse unBlackListAllSubscriber(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse response1, HttpServletRequest httpServletRequest,
			@Parameter(description = "Delete Restricted Subscriber RequestVO",  required = true) @RequestBody UnBlackListAllSubscriberRequestVO requestVO)
			throws Exception {

		final String methodName = "unBlackListAllSubscriber";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		BaseResponse response = new BaseResponse();
		Connection con = null;
		MComConnectionI mcomCon = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		String loginID = null;
		
		UserVO userVO = null;
		UserDAO userDAO = null;

		try {

			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			loginID = OAuthUserData.getData().getLoginid();
			
			userVO = new UserVO();
			userDAO = new UserDAO();
			userVO  = userDAO.loadUsersDetails(con, OAuthUserData.getData().getMsisdn());
	
			response = restrictedListMgmtServiceI.unBlackListAllSubscriber(headers,httpServletRequest,response1,con,mcomCon,locale,userVO,response,requestVO);
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
		} 
		catch (Exception e) {
			log.error(methodName, "Exception:e=" + e);
			log.errorTrace(methodName, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.UNBLACK_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.UNBLACK_FAIL);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
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
	
	
	
	
	
	
	
	@PostMapping(value = "/unblacklistselectedsubscriber", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "This methods will process the unBlackListing of the selected subscribers", response = BaseResponse.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = BaseResponse.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${unblacklistselectedsubscriber.summary}", description="${unblacklistselectedsubscriber.description}",

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

	public BaseResponse unBlackListSelectedSubscriber(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse response1, HttpServletRequest httpServletRequest,
			@Parameter(description = "Delete Restricted Subscriber RequestVO",  required = true) @RequestBody UnBlackListSelectedSubscriberRequestVO requestVO)
			throws Exception {

		final String methodName = "unBlackListSelectedSubscriber";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		
		BaseResponse response = new BaseResponse();
		Connection con = null;
		MComConnectionI mcomCon = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		String loginID = null;
		
		UserVO userVO = null;
		UserDAO userDAO = null;

		try {

			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			loginID = OAuthUserData.getData().getLoginid();
			
			userVO = new UserVO();
			userDAO = new UserDAO();
			userVO  = userDAO.loadUsersDetails(con, OAuthUserData.getData().getMsisdn());
			
			response = restrictedListMgmtServiceI.unBlackListSelectedSubscriber(headers,httpServletRequest,response1,con,mcomCon,locale,userVO,response,requestVO);

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
		} 
		catch (Exception e) {
			log.error(methodName, "Exception:e=" + e);
			log.errorTrace(methodName, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.UNBLACK_SELECTED_FAILURE, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.UNBLACK_SELECTED_FAILURE);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
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

	
	//****UNBLACK api's ends here*******//
	
	
	
	
	//****BLACKLIST api's starts here*******//
	
	@GetMapping(value = "/loadsubscriberlistforblacklistsingle", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "This methods populate the Subscriber List for BlackList", response = LoadSubscriberListForBlackListSingleResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = LoadSubscriberListForBlackListSingleResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${loadsubscriberlistforblacklistsingle.summary}", description="${loadsubscriberlistforblacklistsingle.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = LoadSubscriberListForBlackListSingleResponseVO.class))
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

	public LoadSubscriberListForBlackListSingleResponseVO loadSubscriberListForBlackListSingle(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse response1,
			HttpServletRequest httpServletRequest,
			@Parameter(description = "msisdnStr", required = true) @RequestParam("msisdnStr") String msisdnStr,
			@Parameter(description = "ownerID", required = true) @RequestParam("ownerID") String ownerID,
			@Parameter(description = "cp2pPayer", required = true) @RequestParam("cp2pPayer") String cp2pPayer,
			@Parameter(description = "cp2pPayee", required = true) @RequestParam("cp2pPayee") String cp2pPayee,
			@Parameter(description = "c2sPayee", required = true) @RequestParam("c2sPayee") String c2sPayee
			)
			throws Exception {
		
		final String methodName = "loadSubscriberListForBlackListSingle";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		LoadSubscriberListForBlackListSingleResponseVO response = new LoadSubscriberListForBlackListSingleResponseVO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		String loginID = null;
		
		UserVO userVO = null;
		UserDAO userDAO = null;

		try {

			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			loginID = OAuthUserData.getData().getLoginid();
			
			userVO = new UserVO();
			userDAO = new UserDAO();
			userVO  = userDAO.loadUsersDetails(con, OAuthUserData.getData().getMsisdn());
			
			
			response = restrictedListMgmtServiceI.loadSubscriberListForBlackListSingle(headers,httpServletRequest,response1,con,mcomCon,locale,userVO,response,msisdnStr,ownerID,cp2pPayer,cp2pPayee,c2sPayee);

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
		} 
		catch (Exception e) {
			log.error(methodName, "Exception:e=" + e);
			log.errorTrace(methodName, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.LIST_NOT_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.LIST_NOT_FOUND);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
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
	
	
	
	
	
	
	
	@PostMapping(value = "/blacklistsinglesubscriber", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "This methods will process the BlackListing of the single subscriber", response = BaseResponse.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = BaseResponse.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${blacklistsinglesubscriber.summary}", description="${blacklistsinglesubscriber.description}",

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


	public BaseResponse blackListSingleSubscriber(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse response1, HttpServletRequest httpServletRequest,
			@Parameter(description = "BlackList Single Subscriber RequestVO",  required = true) @RequestBody BlackListSingleSubscriberRequestVO requestVO)
			throws Exception {

		final String methodName = "blackListSingleSubscriber";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		
		BaseResponse response = new BaseResponse();
		Connection con = null;
		MComConnectionI mcomCon = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		String loginID = null;
		
		UserVO userVO = null;
		UserDAO userDAO = null;

		try {

			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			loginID = OAuthUserData.getData().getLoginid();
			
			userVO = new UserVO();
			userDAO = new UserDAO();
			userVO  = userDAO.loadUsersDetails(con, OAuthUserData.getData().getMsisdn());
			
			response = restrictedListMgmtServiceI.blackListSingleSubscriber(headers,httpServletRequest,response1,con,mcomCon,locale,userVO,response,requestVO);

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
		} 
		catch (Exception e) {
			log.error(methodName, "Exception:e=" + e);
			log.errorTrace(methodName, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.BLACKLIST_SUBSCRIBER_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.BLACKLIST_SUBSCRIBER_FAIL);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
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
	
	
	
	
	
	
	@PostMapping(value = "/blacklistallsubscriber", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "This methods will process the BlackListing of the single subscriber", response = BaseResponse.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = BaseResponse.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${blacklistallsubscriber.summary}", description="${blacklistallsubscriber.description}",

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


	public BaseResponse blackListAllSubscriber(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse response1, HttpServletRequest httpServletRequest,
			@Parameter(description = "BlackList All Subscriber RequestVO",  required = true) @RequestBody BlackListAllSubscriberRequestVO requestVO)
			throws Exception {

		final String methodName = "blackListAllSubscriber";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		
		BaseResponse response = new BaseResponse();
		Connection con = null;
		MComConnectionI mcomCon = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		String loginID = null;
		
		UserVO userVO = null;
		UserDAO userDAO = null;

		try {

			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			loginID = OAuthUserData.getData().getLoginid();
			
			userVO = new UserVO();
			userDAO = new UserDAO();
			userVO  = userDAO.loadUsersDetails(con, OAuthUserData.getData().getMsisdn());
			
			response = restrictedListMgmtServiceI.blackListAllSubscriber(headers,httpServletRequest,response1,con,mcomCon,locale,userVO,response,requestVO);

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
		} 
		catch (Exception e) {
			log.error(methodName, "Exception:e=" + e);
			log.errorTrace(methodName, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.BLACKLIST_SUBSCRIBER_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.BLACKLIST_SUBSCRIBER_FAIL);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
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
	
	//****BLACKLIST api's ends here*******//

	@PostMapping(value = "/blacklistmultiplesubscribers", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "This methods will process the BlackListing of the Multiple subscribers", response = BaseResponse.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = BaseResponse.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${blacklistmultiplesubscribers.summary}", description="${blacklistmultiplesubscribers.description}",

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

	public BaseResponse blackListMultipleSubscribers(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse response1, HttpServletRequest httpServletRequest,
			@Parameter(description = "Blacklist Multiple Subscribers RequestVO",  required = true) @RequestBody BlacklistMultipleSubscribersRequestVO requestVO)
			throws Exception {

		final String methodName = "blackListMultipleSubscribers";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		BaseResponse response = new BaseResponse();
		Connection con = null;
		MComConnectionI mcomCon = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		String loginID = null;

		UserVO userVO = null;
		UserDAO userDAO = null;

		try {

			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			loginID = OAuthUserData.getData().getLoginid();

			userVO = new UserVO();
			userDAO = new UserDAO();
			userVO  = userDAO.loadUsersDetails(con, OAuthUserData.getData().getMsisdn());

			response = restrictedListMgmtServiceI.uploadAndProcessBlackListMultipleSubscriberFile(headers,httpServletRequest,response1,con,mcomCon,locale,userVO,requestVO);
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
		}
		catch (Exception e) {
			log.error(methodName, "Exception:e=" + e);
			log.errorTrace(methodName, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.BLACKLIST_SUBSCRIBER_FAILED, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.BLACKLIST_SUBSCRIBER_FAILED);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
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
