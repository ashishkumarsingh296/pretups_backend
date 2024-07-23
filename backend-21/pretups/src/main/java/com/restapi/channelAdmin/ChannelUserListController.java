package com.restapi.channelAdmin;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Locale;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.OAuthenticationUtil;
import com.restapi.channelAdmin.requestVO.BulkSusResCURequestVO;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${ChannelUserListController.name}", description = "${ChannelUserListController.desc}")//@Api(tags = "Channel Admin", defaultValue = "Channel Admin")
@RestController
@RequestMapping(value = "/v1/channeladmin")
public class ChannelUserListController {

	public static final Log log = LogFactory.getLog(ChannelUserListController.class.getName());
	public static final String classname = "ChannelUserListController ";

	@Autowired
	ChannelUserListImpl ChannelUserListI;
	@Autowired
	static OperatorUtilI operatorUtili = null;

	/**
	 * @author harshita.bajaj
	 * @param headers
	 * @param type
	 * @param requestVO
	 * @param response1
	 * @param httprequest
	 * @return
	 * @throws Exception
	 */

	@PostMapping(value = "/channelUserList", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(tags = "Channel Admin", value = "channelUserList", response = ChannelUserListResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = ChannelUserListResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${channelUserList.summary}", description="${channelUserList.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ChannelUserListResponseVO.class))
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
	public ChannelUserListResponseVO channelUserListAdmin(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = "SearchType", required = true)// allowableValues = "Msisdn,LoginId,Advance")
			@RequestParam("Type") String type,
			@RequestBody ChannelUserListRequestVO requestVO, HttpServletResponse response1,
			HttpServletRequest httprequest) throws Exception {
		final String methodName = "channelUserListAdmin";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		ChannelUserListResponseVO response = new ChannelUserListResponseVO();
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
			UserVO userVO = new UserDAO().loadAllUserDetailsByLoginID(con, OAuthUserData.getData().getLoginid());
			userVO.setUserPhoneVO(new UserDAO().loadUserAnyPhoneVO(con, OAuthUserData.getMsisdn()));
			response = ChannelUserListI.getChannelUserListByAdmin(con, loginID, response1, requestVO, type);

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
		} finally {

			if (mcomCon != null) {
				mcomCon.close("channelUserListAdmin");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting:=" + methodName);
			}

		}
		return response;
	}
	

	/**
	 * @author harshita.bajaj
	 * @param headers
	 * @param type
	 * @param requestVO
	 * @param response1
	 * @param httprequest
	 * @return
	 * @throws Exception
	 */

	@PostMapping(value = "/channelUserListByParent", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(tags = "Channel Admin", value = "channelUserListByParent", response = ChannelUserListResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = ChannelUserListResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${channelUserListByParent.summary}", description="${channelUserListByParent.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ChannelUserListByParentResponseVO.class))
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
	public ChannelUserListByParentResponseVO channelUserListByParent(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@RequestBody ChannelUserListByParntReqVO requestVO, HttpServletResponse response1,
			HttpServletRequest httprequest) throws Exception {
		final String methodName = "channelUserListAdmin";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		ChannelUserListByParentResponseVO response = new ChannelUserListByParentResponseVO();
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
			//UserVO userVO = new UserDAO().loadAllUserDetailsByLoginID(con, OAuthUserData.getData().getLoginid());
			
			response = ChannelUserListI.getChannelUserListByParent(con, loginID, response1, requestVO);

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
		} finally {

			if (mcomCon != null) {
				mcomCon.close("channelUserListAdmin");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting:=" + methodName);
			}

		}
		return response;
	}

	
	
	@PostMapping(value = "/bulksusrescu", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(tags = "Channel Admin", value = "Bulk Suspend Resume Channel User", response = BaseResponse.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = ChannelUserListResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${bulksusrescu.summary}", description="${bulksusrescu.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = BulkSusResCUResponseVO.class))
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
	public BulkSusResCUResponseVO bulkSusResCU(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = "Type", required = true)//allowableValues = "MSISDN,LOGIN")
			@RequestParam("Type") String type,
			@RequestBody BulkSusResCURequestVO requestVO, HttpServletResponse response1,
			HttpServletRequest httprequest) throws Exception {
		final String methodName = "bulkSusResCU";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		BulkSusResCUResponseVO response = new BulkSusResCUResponseVO();
		Connection con = null;
		MComConnectionI mcomCon = null;
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
			UserVO userVO = new UserDAO().loadAllUserDetailsByLoginID(con, OAuthUserData.getData().getLoginid());
			userVO.setUserPhoneVO(new UserDAO().loadUserAnyPhoneVO(con, OAuthUserData.getMsisdn()));
			response = ChannelUserListI.processBulkSusResCU(con,response1, requestVO, type,locale,userVO);

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
		} finally {

			if (mcomCon != null) {
				mcomCon.close("channelUserListAdmin");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting:=" + methodName);
			}

		}
		return response;
	}
	
	/**
	 * @author harshita.bajaj
	 * @param headers
	 * @param type
	 * @param requestVO
	 * @param response1
	 * @param httprequest
	 * @return
	 * @throws Exception
	 */

	@PostMapping(value = "/staffUserListByParent", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(tags = "Channel Admin", value = "staffUserListByParent", response = ChannelUserListResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = ChannelUserListResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${staffUserListByParent.summary}", description="${staffUserListByParent.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = StaffUserListByParentResponseVO.class))
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
	public StaffUserListByParentResponseVO staffUserListByParent(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@RequestBody StaffUserListByParntReqVO requestVO, HttpServletResponse response1,
			HttpServletRequest httprequest) throws Exception {
		final String methodName = "staffUserListByParent";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		StaffUserListByParentResponseVO response = new StaffUserListByParentResponseVO();
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
			//UserVO userVO = new UserDAO().loadAllUserDetailsByLoginID(con, OAuthUserData.getData().getLoginid());
			
			response = ChannelUserListI.getstaffUserListByParent(con, loginID, response1, requestVO);

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
		} finally {

			if (mcomCon != null) {
				mcomCon.close("channelUserListAdmin");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting:=" + methodName);
			}

		}
		return response;
	}



	/**
	  * 
	  * @param userId
	  * @param headers
	  *
	  * @return
	  */
		@GetMapping("/commissionProfileBy/geography/userGrade/networkCode/categoryCode")
		/*@ApiOperation(response = CommissionProfileResponseVO.class, authorizations = {
				@Authorization(value = "Authorization") }, value = "Authorization")

		@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = BaseResponseMultiple.class),
				@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
				@ApiResponse(code = 404, message = "Not Found") })
*/
		@io.swagger.v3.oas.annotations.Operation(summary = "${commissionProfileBy.summary}", description="${commissionProfileBy.description}",

				responses = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CommissionProfileResponseVO.class))
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
		public CommissionProfileResponseVO commissionProfileBy(@RequestParam String geography,@RequestParam String userGrade,@RequestParam String networkCode,@RequestParam String categoryCode,
				@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse responseSwagger) {

			final String methodName = "commissionProfileBy";
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Entered");
			}
			OAuthUser oAuthUser = null;
			MComConnectionI mcomCon = null;
			CommissionProfileResponseVO response = new CommissionProfileResponseVO();
			Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
			try {
				
				mcomCon = new MComConnection();
				Connection con = mcomCon.getConnection();
				 
	            /*
				 * Authentication
				 * @throws BTSLBaseException
				 */
				
				oAuthUser = new OAuthUser();
				oAuthUser.setData(new OAuthUserData());
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwagger);
				response = ChannelUserListI.commissionProfileBy(con, geography,networkCode,userGrade,categoryCode);

			

			} catch (BTSLBaseException be) {

				log.error(methodName, "Exception:e=" + be);
				log.errorTrace(methodName, be);
				String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
				response.setMessageCode(be.getMessageKey());
				response.setMessage(msg);

				if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
					responseSwagger.setStatus(HttpStatus.SC_UNAUTHORIZED);
					response.setStatus((HttpStatus.SC_UNAUTHORIZED));
				} else {
					responseSwagger.setStatus(HttpStatus.SC_BAD_REQUEST);
					response.setStatus((HttpStatus.SC_BAD_REQUEST));
				}

			} catch (Exception e) {
				log.error(methodName, "Exceptin:e=" + e);
				log.errorTrace(methodName, e);



				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				String resmsg = RestAPIStringParser.getMessage(
						new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
						PretupsErrorCodesI.USER_UNAUTHORIZED, null);

				response.setMessage(resmsg);

			} finally {

				if (mcomCon != null) {
					mcomCon.close(methodName);
					mcomCon = null;
				}
				if (log.isDebugEnabled()) {
					log.debug(methodName, " Exited ");
				}

			}
			return response;
		}
		


		
		
		@PostMapping(value = "/channelUserListByAdmin", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
		@ResponseBody
		/*@ApiOperation(tags = "Channel Admin", value = "channelUserListByAdmin", response = ChannelUserListResponseVO.class, authorizations = {
				@Authorization(value = "Authorization") })
		@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = ChannelUserListResponseVO.class),
				@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
				@ApiResponse(code = 404, message = "Not Found") })
*/
		@io.swagger.v3.oas.annotations.Operation(summary = "${channelUserListByAdmin.summary}", description="${channelUserListByAdmin.description}",

				responses = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ChannelUserListResponseVO.class))
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
		public ChannelUserListResponseVO channelUserListbyAdmin(
				@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
				@Parameter(description = "SearchType", required = true)//allowableValues = "Msisdn,LoginId,ExtCode,Advance")
				@RequestParam("Type") String type,
				@RequestBody ChannelUserListRequestVO requestVO, HttpServletResponse response1,
				HttpServletRequest httprequest) throws Exception {
			final String methodName = "channelUserListbyAdmin";
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Entered ");
			}
			ChannelUserListResponseVO response = new ChannelUserListResponseVO();
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
				UserVO userVO = new UserDAO().loadAllUserDetailsByLoginID(con, OAuthUserData.getData().getLoginid());
				userVO.setUserPhoneVO(new UserDAO().loadUserAnyPhoneVO(con, OAuthUserData.getMsisdn()));
				response = ChannelUserListI.getChannelUserListByAdmin2(con, loginID, response1, requestVO, type);

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
			} finally {

				if (mcomCon != null) {
					mcomCon.close("channelUserListAdmin");
					mcomCon = null;
				}
				if (log.isDebugEnabled()) {
					log.debug(methodName, "Exiting:=" + methodName);
				}

			}
			return response;
		}


}
