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
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.OAuthenticationUtil;
import com.restapi.channelAdmin.requestVO.BulkCUStatusChangeRequestVO;
import com.restapi.channelAdmin.responseVO.BulkCUStatusChangeResponseVO;
import com.restapi.channelAdmin.serviceI.ChangeUserStatusByAdminImpl;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${ChangeUserStatusByAdminController.name}", description = "${ChangeUserStatusByAdminController.desc}")//@Api(tags = "Channel Admin", defaultValue = "Channel Admin")
@RestController
@RequestMapping(value = "/v1/channeladmin")

public class ChangeUserStatusByAdminController {

	public static final Log log = LogFactory.getLog(ChangeUserStatusByAdminController.class.getName());
	public static final String classname = "ChangeUserStatusByAdminController";

	@Autowired
	ChangeUserStatusByAdminImpl changeUserStatusByAdminImpl;
	@Autowired
	static OperatorUtilI operatorUtili = null;

	/**
	 * @author harshita.bajaj
	 * @param headers
	 * @param response1
	 * @param httpServletRequest
	 * @param msisdn
	 * @param status
	 * @return
	 * @throws Exception
	 */

	@GetMapping(value = "/ChangeUserStatusByAdmin", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Change User Status By Admin", response = BaseResponse.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = BaseResponse.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${ChangeUserStatusByAdmin.summary}", description="${ChangeUserStatusByAdmin.description}",

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



	public BaseResponse ChangeUserStatusByAdmin(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest,
			@Parameter(description = "msisdn", required = true) @RequestParam("msisdn") String msisdn,
			@Parameter(description = "remarks", required = true) @RequestParam("remarks") String remarks,
			@Parameter(description = "status", required = true) @RequestParam("status") String status) throws Exception {

		final String methodName = "ChangeUserStatusByAdmin";
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
			UserVO userVO = new UserDAO().loadAllUserDetailsByLoginID(con, OAuthUserData.getData().getLoginid());
			userVO.setUserPhoneVO(new UserDAO().loadUserAnyPhoneVO(con, OAuthUserData.getMsisdn()));
			response = changeUserStatusByAdminImpl.changeUserStatusAdmin(con,loginID, msisdn, response1, status ,remarks);

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
				mcomCon.close("ChangeUserStatusByAdmin");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting:=" + methodName);
			}

		}
		return response;
	} 
	
	/**
	 * @author Vamshikrishna.v
	 * @param  headers
	 * @param  HttpServletResponse
	 * @param  BulkCUStatusChangeRequestVO
	 * @return BulkCUStatusChangeResponseVO
	 * @throws Exception
	 */
	   @PostMapping(value = "/channelUserBulkStatusChange", produces = MediaType.APPLICATION_JSON)
		@ResponseBody
	    /*@ApiOperation(value = "Channel User Bulk Status Change", response = BulkCUStatusChangeResponseVO.class ,
	    		authorizations = {
	    	            @Authorization(value = "Authorization")
	    })
	    
		@ApiResponses(value = { 
				@ApiResponse(code = 200, message = "OK", response = BulkCUStatusChangeResponseVO.class),
				@ApiResponse(code = 400, message = "Bad Request"),
				@ApiResponse(code = 401, message = "Unauthorized"),
				@ApiResponse(code = 404, message = "Not Found") })
*/

	   @io.swagger.v3.oas.annotations.Operation(summary = "${channelUserBulkStatusChange.summary}", description="${channelUserBulkStatusChange.description}",

			   responses = {
					   @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							   @io.swagger.v3.oas.annotations.media.Content(
									   mediaType = "application/json",
									   array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = BulkCUStatusChangeResponseVO.class))
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



	   public BulkCUStatusChangeResponseVO channelUserBulkStatusChange(
	    		@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
	            @Parameter(description = "Bulk Channel User Status Change", required = true)
	            @RequestBody BulkCUStatusChangeRequestVO requestVO,HttpServletResponse response1) throws Exception {
		    final String methodName = "channelUserBulkStatusChange";
		    BulkCUStatusChangeResponseVO responseVO=new BulkCUStatusChangeResponseVO();
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Entered ");
			}
			Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
			OAuthUser oAuthUser = null;
		    OAuthUserData oAuthUserData = null;   
			try {
					oAuthUser = new OAuthUser();
				    oAuthUserData = new OAuthUserData();
				    oAuthUser.setData(oAuthUserData);
					OAuthenticationUtil.validateTokenApi(oAuthUser, headers, response1);
			        changeUserStatusByAdminImpl.channelUserBulkStatusChangeImpl(requestVO,oAuthUserData.getMsisdn(),responseVO,response1);
				
			}catch (BTSLBaseException be) {
			    	log.error("", "Exceptin:e=" + be);
					log.errorTrace(methodName, be);
					String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), be.getArgs());
					responseVO.setMessageCode(be.getMessageKey());
					responseVO.setMessage(msg);

					if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
						response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
						responseVO.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
					} else {
						response1.setStatus(HttpStatus.SC_BAD_REQUEST);
						responseVO.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
					}
					
					
			 }catch (Exception e) {
					 log.error("", "Exceptin:e=" + e);
					 log.errorTrace(methodName, e);
					 responseVO.setMessageCode(String.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR));
					 response1.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
					 responseVO.setMessage(e.getMessage());
			   	 
			  }
		      if (log.isDebugEnabled()) {
				log.debug(methodName, "Exit ");
			  }
			
			return responseVO;
	 }
	
}
