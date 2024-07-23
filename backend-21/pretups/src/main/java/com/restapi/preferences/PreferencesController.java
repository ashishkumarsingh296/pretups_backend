package com.restapi.preferences;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.util.Constants;
import com.restapi.preferences.responseVO.ApiConfigurationResponseVO;
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
import com.btsl.common.BaseResponse;
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
import com.restapi.preferences.requestVO.UpdateSystemPreferencesRequestVO;
import com.restapi.preferences.responseVO.SystemPreferencesResponseVO;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${PreferencesController.name}", description = "${PreferencesController.desc}")//@Api(tags="Preferences")
@RestController
@RequestMapping(value = "/v1/preferences")
public class PreferencesController {
	
	public static final Log log = LogFactory.getLog(PreferencesController.class.getName());
	
	@Autowired
	PreferencesServiceI preferencesServiceI;

	@GetMapping(value= "/getSystemPreferences", produces = MediaType.APPLICATION_JSON)	
	@ResponseBody
	/*@ApiOperation(value = "Returns the list of System Preferences",
		           response = SystemPreferencesResponseVO.class,
		           authorizations = {
		               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
		      @ApiResponse(code = 200, message = "OK", response = SystemPreferencesResponseVO.class),
		      @ApiResponse(code = 400, message = "Bad Request" ),
		      @ApiResponse(code = 401, message = "Unauthorized"),
		      @ApiResponse(code = 404, message = "Not Found")
		 })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${getSystemPreferences.summary}", description="${getSystemPreferences.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = SystemPreferencesResponseVO.class))
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

	public SystemPreferencesResponseVO fetchSystemPreferences(
	@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
	HttpServletResponse responseSwag,
	@Parameter(description = "module", required = true) @RequestParam("module") String module,
	@Parameter(description = "type", required = true) @RequestParam("type") String type) {
		
		 final String methodName = "fetchSystemPreferences";
	     if (log.isDebugEnabled()) {
	         log.debug(methodName, "Entered");
	     }
	     
	     Connection con = null;
	     MComConnectionI mcomCon = null;
	     SystemPreferencesResponseVO response=null;
	     String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		 String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		    
	     try {
	         response = new SystemPreferencesResponseVO();
	         mcomCon = new MComConnection();
	         con=mcomCon.getConnection();
	         
	         OAuthUser oAuthUserData=new OAuthUser();
	         oAuthUserData.setData(new OAuthUserData());
	         OAuthenticationUtil.validateTokenApi(oAuthUserData,headers,responseSwag);
	         UserDAO userDao = new UserDAO();
	         UserVO sessionUserVO = userDao.loadAllUserDetailsByLoginID(con, oAuthUserData.getData().getLoginid());
	         preferencesServiceI.getSystemPreferences(module, type, con, response, responseSwag,sessionUserVO);
	         
	         
	     } catch (BTSLBaseException be) {
	    	 log.error(methodName, "Exception:e=" + be);
	         log.errorTrace(methodName, be);
	         if(be.getMessage().equalsIgnoreCase("1080001")||be.getMessage().equalsIgnoreCase("1080002")||be.getMessage().equalsIgnoreCase("1080003")||
	             		 be.getMessage().equalsIgnoreCase("241023")||be.getMessage().equalsIgnoreCase("241018")){
	        	 response.setStatus(HttpStatus.SC_UNAUTHORIZED);
	             responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED); 	 
	         } else{
	              response.setStatus(HttpStatus.SC_BAD_REQUEST);
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
	            response.setStatus(PretupsI.RESPONSE_FAIL);
	    		response.setMessageCode("error.general.processing");
	    		response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");
	        } finally {
	        	if(mcomCon != null)
	        	{
	        		mcomCon.close("StaffUserController#"+methodName);
	        		mcomCon=null;
	        		}
	            if (log.isDebugEnabled()) {
	                log.debug(methodName, " Exited ");
	            }
	            
	            try {
					if (con != null) {
						con.close();
					}
				} catch (Exception e) {
					log.errorTrace(methodName, e);
				}
	        }
	    	return response;
	    	
		}
	
	
	@PostMapping(value= "/UpdateSystemPreferences", produces = MediaType.APPLICATION_JSON)	
	@ResponseBody
	/*@ApiOperation(value = "Updates the list of System Preferences",
		           response = BaseResponse.class,
		           authorizations = {
		               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
		      @ApiResponse(code = 200, message = "OK", response = BaseResponse.class),
		      @ApiResponse(code = 400, message = "Bad Request" ),
		      @ApiResponse(code = 401, message = "Unauthorized"),
		      @ApiResponse(code = 404, message = "Not Found")
		 })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${UpdateSystemPreferences.summary}", description="${UpdateSystemPreferences.description}",

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

	public BaseResponse updateSystemPreferences(
	@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
	HttpServletResponse responseSwag,@RequestBody UpdateSystemPreferencesRequestVO requestVO) {
		
		 final String methodName = "updateSystemPreferences";
	     if (log.isDebugEnabled()) {
	         log.debug(methodName, "Entered");
	     }
	     
	     Connection con = null;
	     MComConnectionI mcomCon = null;
	     BaseResponse response=null;
	     String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		 String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		    
	     try {
	         response = new SystemPreferencesResponseVO();
	         mcomCon = new MComConnection();
	         con=mcomCon.getConnection();
	         OAuthUser oAuthUserData=new OAuthUser();
	         oAuthUserData.setData(new OAuthUserData());
	         OAuthenticationUtil.validateTokenApi(oAuthUserData,headers,responseSwag);
	         UserDAO userDao = new UserDAO();
	         UserVO sessionUserVO = userDao.loadAllUserDetailsByLoginID(con, oAuthUserData.getData().getLoginid()); 
	         preferencesServiceI.updateSystemPreferences(con, mcomCon, response, responseSwag , sessionUserVO , requestVO);  
	     } catch (BTSLBaseException be) {
	    	 log.error(methodName, "Exception:e=" + be);
	         log.errorTrace(methodName, be);
	         if(be.getMessage().equalsIgnoreCase("1080001")||be.getMessage().equalsIgnoreCase("1080002")||be.getMessage().equalsIgnoreCase("1080003")||
	             		 be.getMessage().equalsIgnoreCase("241023")||be.getMessage().equalsIgnoreCase("241018")){
	        	 response.setStatus(HttpStatus.SC_UNAUTHORIZED);
	             responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED); 	 
	         } else{
	              response.setStatus(HttpStatus.SC_BAD_REQUEST);
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
	            response.setStatus(PretupsI.RESPONSE_FAIL);
	    		response.setMessageCode("error.general.processing");
	    		response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");
	        } finally {
	        	if(mcomCon != null)
	        	{
	        		mcomCon.close("PreferencesController#"+methodName);
	        		mcomCon=null;
	        		}
	            if (log.isDebugEnabled()) {
	                log.debug(methodName, " Exited ");
	            }
	            
	            try {
					if (con != null) {
						con.close();
					}
				} catch (Exception e) {
					log.errorTrace(methodName, e);
				}
	        }
	    	return response;
	    	
		}


	@GetMapping(value= "/apiConfiguration", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
/*	@ApiOperation(value = "Returns the list of APIs for bypass @ client side",
			response = SystemPreferencesResponseVO.class,
			authorizations = {
					@Authorization(value = "Authorization")})
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = ApiConfigurationResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request" ),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found")
	})*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${fetchApiConfiguration.summary}", description="${fetchApiConfiguration.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ApiConfigurationResponseVO.class))
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
	public ApiConfigurationResponseVO fetchApiConfiguration(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse responseSwag) {

		final String methodName = "fetchApiConfiguration";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered");
		}

		ApiConfigurationResponseVO response = new ApiConfigurationResponseVO();
		String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		String byPassUrlString = Constants.getProperty("BYPASS_URLS");
		List<String> byPassUrls = new ArrayList<>();
		if (byPassUrlString != null && !byPassUrlString.isEmpty()) {
			String[] byPassUrlArray = byPassUrlString.split(",");
			Collections.addAll(byPassUrls, byPassUrlArray);
		}
		response.setApiConfigurationList(byPassUrls);
		response.setStatus(PretupsI.RESPONSE_SUCCESS);
		response.setMessageCode(PretupsErrorCodesI.SUCCESS);
		String resmsg = RestAPIStringParser.getMessage(new Locale(lang, country), PretupsErrorCodesI.SUCCESS, null);
		response.setMessage(resmsg);
		if(log.isDebugEnabled()) {
			log.debug(methodName, "Exited");
		}
		return response;
	}



}
