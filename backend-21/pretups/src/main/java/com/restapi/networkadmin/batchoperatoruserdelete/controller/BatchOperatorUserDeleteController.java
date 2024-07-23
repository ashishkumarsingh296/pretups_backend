package com.restapi.networkadmin.batchoperatoruserdelete.controller;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Locale;

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
import com.restapi.channelAdmin.restrictedlistmgmt.requestVO.UploadFileRequestVO;
import com.restapi.networkadmin.batchoperatoruserdelete.response.BatchOperatorUserDeleteResponseVO;
import com.restapi.networkadmin.batchoperatoruserdelete.serviceI.BatchUserOperatorDeleteServiceI;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${BatchOperatorUserDeleteController.name}", description = "${BatchOperatorUserDeleteController.desc}")//@Api(tags = "Network Admin")
@RestController
@RequestMapping(value = "/v1/networkadmin/batchoperatoruser/")
public class BatchOperatorUserDeleteController {

	@Autowired
	private BatchUserOperatorDeleteServiceI service;
	public static final Log LOG = LogFactory.getLog(BatchOperatorUserDeleteController.class.getName());
	public static final String CLASS_NAME = "BatchOperatorUserDeleteController";

	@PostMapping(value = "/batchoperateuserdelete", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Batch Operate User Delete", response =BatchOperatorUserDeleteResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = BatchOperatorUserDeleteResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${batchoperateuserdelete.summary}", description="${batchoperateuserdelete.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = BatchOperatorUserDeleteResponseVO.class))
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


	public BatchOperatorUserDeleteResponseVO batchOperateUserDelete(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest,
			@Valid @RequestBody  UploadFileRequestVO uploadFileRequestVO) throws Exception {

		final String methodName = "batchOperateUserDelete";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered");
		}
  
		Connection con = null;MComConnectionI mcomCon = null;
		UserVO userVO = null;
		UserDAO userDAO = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		BatchOperatorUserDeleteResponseVO response = new BatchOperatorUserDeleteResponseVO();
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
			response = service.batchOperatorUserDelete(con, userVO, uploadFileRequestVO, response, response1);
			
  
     
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
      String resmsg = RestAPIStringParser.getMessage(locale,
				PretupsErrorCodesI.OPERATOR_USER_DELETE_FAILED, null);
      response.setMessage(resmsg);
      response.setMessageCode(PretupsErrorCodesI.OPERATOR_USER_DELETE_FAILED);
  } finally {
  	
  	if(mcomCon != null){
  		mcomCon.close(CLASS_NAME+"#"+methodName);
  		mcomCon=null;}
      if (LOG.isDebugEnabled()) {
          LOG.debug(methodName, "Exiting forward=" );
      }
  }
		return response;
	}

}
