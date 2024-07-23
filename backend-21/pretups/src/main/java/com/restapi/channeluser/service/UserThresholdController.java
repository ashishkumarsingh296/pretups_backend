package com.restapi.channeluser.service;


import io.swagger.v3.oas.annotations.Parameter;

import java.sql.Connection;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Parameter;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.ProfileThresholdResponseVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.util.OAuthenticationUtil;


@io.swagger.v3.oas.annotations.tags.Tag(name = "${UserThresholdController.name}", description = "${UserThresholdController.desc}")//@Api(tags="Channel Users",value="Channel Users")
@RestController
@RequestMapping(value = "/v1/channelUsers")
public class UserThresholdController {
public static final Log log = LogFactory.getLog(UserThresholdController.class.getName());


	@Autowired
	UserThresholdServiceI userThresholdServiceI;
	
    @GetMapping(value ="/userthreshold", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
	/*@ApiOperation(tags="Channel Users", value = "View user profile threshold",response =PretupsResponse.class,
			authorizations = {
    	            @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = PretupsResponse.class),
	        @ApiResponse(code = 400, message = "Bad Request" ),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${userthreshold.summary}", description="${userthreshold.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = PretupsResponse.class))
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


	public PretupsResponse<ProfileThresholdResponseVO> viewUserProfileThreshold(
			@Parameter(description = "Enter the LoginID (not UserId) ", required = true)
	        @RequestParam("userId") String userId,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse responseSwag 

	) {

    	final String methodName = "viewUserProfileThreshold";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		Connection con = null;
	    MComConnectionI mcomCon = null;
	    
	    OAuthUser oAuthUser= null;
		OAuthUserData oAuthUserData =null;
		
		String statusUsed = PretupsI.STATUS_NOTIN;
		String status = PretupsBL.userStatusNotIn();
		
		PretupsResponse<ProfileThresholdResponseVO> response = new PretupsResponse<ProfileThresholdResponseVO>();
		String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
	    String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
	    try {
	    	
		    oAuthUser = new OAuthUser();
			oAuthUserData =new OAuthUserData();
			
			oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers,responseSwag);
			String loggedIdUser=oAuthUserData.getLoginid();
			
			final String identifierType = userId;
			
			mcomCon = new MComConnection();
	        con = mcomCon.getConnection();
	        
			response=userThresholdServiceI.userThresholdProcess(identifierType,loggedIdUser,con,statusUsed,status,response,responseSwag);
			
	 } catch (BTSLBaseException be) {
      	 log.error(methodName, "Exception:e=" + be);
         log.errorTrace(methodName, be);
         if(be.getMessage().equalsIgnoreCase("1080001")||be.getMessage().equalsIgnoreCase("1080002")||be.getMessage().equalsIgnoreCase("1080003")||
         		 be.getMessage().equalsIgnoreCase("241023")||be.getMessage().equalsIgnoreCase("241018")){
        	response.setStatusCode(HttpStatus.SC_UNAUTHORIZED);
         	responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED); 	 
         }
          else{
        
          response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
          responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
        
          }
         String resmsg ="";
         if(be.getArgs()!=null) {
        	 resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), be.getMessage(), be.getArgs());

         }else {
        	 resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), be.getMessage(), null);

         }
 	   response.setMessageCode(be.getMessage());
 	   response.setMessage(resmsg);
	}
    catch (Exception e) {
        log.error(methodName, "Exceptin:e=" + e);
        log.errorTrace(methodName, e);
        response.setStatusCode(PretupsI.RESPONSE_FAIL);
		response.setMessageCode("error.general.processing");
		response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");
    } finally {
    	if(mcomCon != null)
    	{
    		mcomCon.close("ChannelUserServices#viewUserProfileThreshold");
    		mcomCon=null;
    		}
        if (log.isDebugEnabled()) {
            log.debug(methodName, " Exited ");
        }
    }
	return response;
	}
}

