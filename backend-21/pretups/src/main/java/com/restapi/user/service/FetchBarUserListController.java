package com.restapi.user.service;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
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
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.OAuthenticationUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${FetchBarUserListController.name}", description = "${FetchBarUserListController.desc}")//@Api(tags="User Services",value="User Services")
@RestController
@RequestMapping(value = "/v1/userServices")
public class FetchBarUserListController {
		protected final Log log = LogFactory.getLog(getClass().getName());
		
		@Autowired
		FetchBarredUserListServiceImpl fetchBarredUserListServiceImpl;
	@PostMapping(value = "/barredUserList", consumes =MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(tags="User Services", value = "Barred Channel User List", notes= SwaggerAPIDescriptionI.BARRED_USER_LIST,
	response = FetchBarredListResponseVO.class,
	authorizations = {
            @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = BarUnbarResponseVO.class),
	        @ApiResponse(code = 201, message = "Created"),
	        @ApiResponse(code = 400, message = "Bad Request"),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${barredUserList.summary}", description="${barredUserList.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = FetchBarredListResponseVO.class))
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

	public FetchBarredListResponseVO barUserList(
    		HttpServletRequest httpServletRequest,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
    		@RequestBody FetchBarredListRequestVO  fetchBarredListRequestVO, HttpServletResponse response) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		final String methodName = "barUserList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }
        Connection con = null;
	    MComConnectionI mcomCon = null;
	    FetchBarredListResponseVO fetchBarredListResponseVO = new FetchBarredListResponseVO();
        OAuthUser oAuthUser= null;
        OAuthUserData oAuthUserData =null;
        try{
        	mcomCon = new MComConnection();
	    	con=mcomCon.getConnection();
	    	ArrayList list = new ArrayList();
	    	oAuthUser = new OAuthUser();
	    	oAuthUserData =new OAuthUserData();
	    	oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers,response);
			UserVO userVO = new UserDAO().loadUsersDetails(con,  oAuthUser.getData().getMsisdn());
			userVO.setUserPhoneVO(new UserDAO().loadUserAnyPhoneVO(con, oAuthUserData.getMsisdn()));
			fetchBarredListResponseVO = fetchBarredUserListServiceImpl.viewBarredList(fetchBarredListRequestVO, userVO);
        	fetchBarredListResponseVO.setService("fetchBarUserListResp");
        	return fetchBarredListResponseVO;
        }
        catch (BTSLBaseException be) {
           log.error(methodName, "Exception:e=" + be);
           log.errorTrace(methodName, be);
           if(be.getMessage().equalsIgnoreCase("1080001")||be.getMessage().equalsIgnoreCase("1080002")||be.getMessage().equalsIgnoreCase("1080003")||
           		 be.getMessage().equalsIgnoreCase("241023")||be.getMessage().equalsIgnoreCase("241018")){
           	 response.setStatus(HttpStatus.SC_UNAUTHORIZED);
           	fetchBarredListResponseVO.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
           }
           else{
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            fetchBarredListResponseVO.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
           }
            String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
            String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
       	    String resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), be.getMessage(), null);
	       	fetchBarredListResponseVO.setMessageCode(be.getMessage());
	       	fetchBarredListResponseVO.setMessage(resmsg);
	       	fetchBarredListResponseVO.setService("barredUserListResp");
        }
        catch (Exception ex) {
        	response.setStatus(HttpStatus.SC_BAD_REQUEST);
        	fetchBarredListResponseVO.setStatus(String.valueOf(PretupsI.RESPONSE_FAIL));
        	fetchBarredListResponseVO.setService("barredUserListResp");
	        log.errorTrace(methodName, ex);
	        log.error(methodName, "Unable to fetch Barred Users List = " + ex.getMessage());
	    }
        finally {
        	try {
				if (mcomCon != null) {
					mcomCon.close("FetchBarUserListController#barUserList");
					mcomCon = null;
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}

			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, " Exited ");
	        }
	    }
        
        return fetchBarredListResponseVO;
	}
}
