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
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.OAuthenticationUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${BarUnbarUserController.name}", description = "${BarUnbarUserController.desc}")//@Api(tags="User Services",value="User Services")
@RestController
@RequestMapping(value = "/v1/userServices")
public class BarUnbarUserController {
	protected final Log log = LogFactory.getLog(getClass().getName());
	
	@Autowired
	BarUnbarUserService barUnbarUserServiceImpl;
	
	@PostMapping(value = "/barUnbarUser", consumes =MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(tags= "User Services", value = "Bar a Channel User", response = BarUnbarResponseVO.class,
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
	@io.swagger.v3.oas.annotations.Operation(summary = "${barUnbarUser.summary}", description="${barUnbarUser.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = BarUnbarResponseVO.class))
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

	public BarUnbarResponseVO barUser(
    		HttpServletRequest httpServletRequest,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = "Type", required = true)//allowableValues = "Bar,Un-bar")
			@RequestParam("Type") String type,
    		@RequestBody BarUnbarRequestVO barUnbarRequestVO, HttpServletResponse response) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		final String methodName = "barUser";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }
        Connection con = null;
	    MComConnectionI mcomCon = null;
	    BarUnbarResponseVO barUnbarResponseVO = new BarUnbarResponseVO();
        OAuthUser oAuthUser= null;
        OAuthUserData oAuthUserData =null;
        try{
        	mcomCon = new MComConnection();
	    	con=mcomCon.getConnection();
	    	ArrayList list = new ArrayList();
	    	oAuthUser = new OAuthUser();
	    	oAuthUserData =new OAuthUserData();
	    	oAuthUser.setData(oAuthUserData);
			UserDAO userDAO = new UserDAO();
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers,response);
			UserVO userVO = userDAO.loadAllUserDetailsByLoginID(con,  oAuthUser.getData().getLoginid());
			if(userVO.getUserType().equals(PretupsI.STAFF_USER_TYPE)){
				userVO = userDAO.loadUserDetailsFormUserID(con, userVO.getParentID());
			}
			userVO.setUserPhoneVO(new UserDAO().loadUserAnyPhoneVO(con, oAuthUserData.getMsisdn()));
			if(type.equalsIgnoreCase("Bar")){
				barUnbarResponseVO = barUnbarUserServiceImpl.addBarredUser(barUnbarRequestVO, userVO);
			}else{
				barUnbarResponseVO = barUnbarUserServiceImpl.unBarredUser(barUnbarRequestVO, userVO);
			}
        	barUnbarResponseVO.setService("barUserResp");
        	return barUnbarResponseVO;
        }
        catch (BTSLBaseException be) {
          	 log.error(methodName, "Exception:e=" + be);
               log.errorTrace(methodName, be);
               if(be.getMessage().equalsIgnoreCase("1080001")||be.getMessage().equalsIgnoreCase("1080002")||be.getMessage().equalsIgnoreCase("1080003")||
               		 be.getMessage().equalsIgnoreCase("241023")||be.getMessage().equalsIgnoreCase("241018")){
               	 response.setStatus(HttpStatus.SC_UNAUTHORIZED);
               	barUnbarResponseVO.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
               }
                else{
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
                barUnbarResponseVO.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
                }
               String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
               String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
       	   String resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), be.getMessage(), null);
       	barUnbarResponseVO.setMessageCode(be.getMessage());
       	barUnbarResponseVO.setMessage(resmsg);
       	barUnbarResponseVO.setService("barUserResp");
        	}
        catch (Exception ex) {
        	response.setStatus(HttpStatus.SC_BAD_REQUEST);
        	barUnbarResponseVO.setStatus(String.valueOf(PretupsI.RESPONSE_FAIL));
        	barUnbarResponseVO.setService("barUserResp");
	        log.errorTrace(methodName, ex);
	        log.error(methodName, "Unable to bar the user = " + ex.getMessage());
	    }
        finally {
	    	try {
				if (mcomCon != null) {
					mcomCon.close("BarUnbarUserController#baruser");
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
	            log.debug("baruser", " Exited ");
	        }
	    }
        return barUnbarResponseVO;
	}
	
	
	@GetMapping(value = "/getUserInfoForBarring", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(tags= "User Services", value = "Get User Info For Barring", response = BarUserInfoResponseVO.class,
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
	@io.swagger.v3.oas.annotations.Operation(summary = "${getUserInfoForBarring.summary}", description="${getUserInfoForBarring.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = BarUserInfoResponseVO.class))
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

	public BarUserInfoResponseVO getUserInfoforBarring(HttpServletRequest httpServletRequest,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@RequestParam("msisdn") String msisdn, HttpServletResponse response) {
	
		final String methodName = "getUserInfoforBarring";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }
        Connection con = null;
	    MComConnectionI mcomCon = null;
	    BarUserInfoResponseVO barUserInfoResponseVO = new BarUserInfoResponseVO();
        OAuthUser oAuthUser= null;
        OAuthUserData oAuthUserData =null;
        try{
        	mcomCon = new MComConnection();
	    	con=mcomCon.getConnection();
	    	oAuthUser = new OAuthUser();
	    	oAuthUserData =new OAuthUserData();
	    	oAuthUser.setData(oAuthUserData);
			UserDAO userDAO = new UserDAO();
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers,response);
			UserVO userVO = userDAO.loadAllUserDetailsByLoginID(con,  oAuthUser.getData().getLoginid());
			if(userVO.getUserType().equals(PretupsI.STAFF_USER_TYPE)){
				userVO = userDAO.loadUserDetailsFormUserID(con, userVO.getParentID());
			}
			barUnbarUserServiceImpl.processGetUserInfoForBarring(con, msisdn , userVO , barUserInfoResponseVO);
			response.setStatus(HttpStatus.SC_OK);
			String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),PretupsErrorCodesI.SUCCESS, null);
			barUserInfoResponseVO.setMessage(resmsg);
        }catch(BTSLBaseException be) {
        	log.error(methodName, "Exception:e=" + be);
            log.errorTrace(methodName, be);
            if(be.getMessage().equalsIgnoreCase("1080001")||be.getMessage().equalsIgnoreCase("1080002")||be.getMessage().equalsIgnoreCase("1080003")||
            		 be.getMessage().equalsIgnoreCase("241023")||be.getMessage().equalsIgnoreCase("241018")){
            	 response.setStatus(HttpStatus.SC_UNAUTHORIZED);
            	 barUserInfoResponseVO.setStatus(HttpStatus.SC_UNAUTHORIZED);
            }
             else{
             response.setStatus(HttpStatus.SC_BAD_REQUEST);
             barUserInfoResponseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
             }
            String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
            String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
    	   String resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), be.getMessage(), be.getArgs());
    	   barUserInfoResponseVO.setMessageCode(be.getMessage());
    	   barUserInfoResponseVO.setMessage(resmsg);
        }catch (Exception ex) {
        	response.setStatus(HttpStatus.SC_BAD_REQUEST);
        	barUserInfoResponseVO.setStatus(PretupsI.RESPONSE_FAIL);
	        log.errorTrace(methodName, ex);
	    }
        finally {
	    	try {
				if (mcomCon != null) {
					mcomCon.close("BarUnbarUserController#"+methodName);
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
	            log.debug("baruser", " Exited ");
	        }
	    }
        return barUserInfoResponseVO;

			
	}
}
