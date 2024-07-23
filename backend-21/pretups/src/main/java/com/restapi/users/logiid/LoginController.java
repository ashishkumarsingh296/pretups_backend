package com.restapi.users.logiid;

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
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
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
import com.btsl.util.AESEncryptionUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.web.pretups.forgotpassword.web.ChangePasswordVO;
import com.web.pretups.forgotpassword.web.ForgotPasswordVO;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;


@io.swagger.v3.oas.annotations.tags.Tag(name = "${LoginController.name}", description = "${LoginController.desc}")//@Api(tags ="Login Services", value="Login Services")
@RestController
@RequestMapping(value = "/v1/login")
public class LoginController {
	public static final Log log = LogFactory.getLog(LoginController.class.getName());
	 private static long requestIdChannel = 0;
	    public static final String classname = "LoginController";

		@Autowired
	    LoginService loginService;
		@Autowired
		static OperatorUtilI operatorUtili = null;

		@PostMapping(value = "/getOTP", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
		@ResponseBody
	    /*@ApiOperation(value = "Get OTP", response = BaseResponse.class ,
	    		notes = ("Api Info:") + ("\n") + 
				("1. Resend: 'N' if sending first time") + ("\n"),
	    		authorizations = {
	    	     
	    })
		@ApiResponses(value = { 
				@ApiResponse(code = 200, message = "OK", response = BaseResponse.class),
				@ApiResponse(code = 400, message = "Bad Request"),
				@ApiResponse(code = 401, message = "Unauthorized"), 
				@ApiResponse(code = 404, message = "Not Found") })
	 */

		@io.swagger.v3.oas.annotations.Operation(summary = "${getOTP.summary}", description="${getOTP.description}",

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

		public BaseResponse getOTP(
			 	@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
	            @Parameter(description = SwaggerAPIDescriptionI.OTP_FOR_FORGOT_PASSWORD_CONTROLLER, required = true)
	            @RequestBody OTPRequestVO requestVO,HttpServletResponse responseSwag,HttpServletRequest httprequest){

		 final String methodName = "getOTP";
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Entered ");
			}
			   ++requestIdChannel;
			   BaseResponse response = null;
			
			   Locale locale= new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));

			try {
				
				
					response= new BaseResponse();
					
					if(BTSLUtil.isNullString(requestVO.getReSend())){
						response.setMessageCode(PretupsErrorCodesI.EXTSYS_BLANK);
						String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.EXTSYS_BLANK,new String[]{"Resend Field"});
						response.setMessage(msg);
						responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		           		response.setStatus(HttpStatus.SC_BAD_REQUEST);
		           		return response;
					}
					if(BTSLUtil.isNullString(requestVO.getMode())){
						response.setMessageCode(PretupsErrorCodesI.EXTSYS_BLANK);
						String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.EXTSYS_BLANK,new String[]{"Mode"});
						response.setMessage(msg);
						responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		           		response.setStatus(HttpStatus.SC_BAD_REQUEST);
		           		return response;
					}
					if(BTSLUtil.isNullString(requestVO.getLoginId())){
						response.setMessageCode(PretupsErrorCodesI.EXTSYS_BLANK);
						String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.EXTSYS_BLANK,new String[]{"Login Id"});
						response.setMessage(msg);
						responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		           		response.setStatus(HttpStatus.SC_BAD_REQUEST);
		           		return response;
					}
				  if(requestVO.getReSend().equals("N"))
		        	 loginService.validateMsisdnEmail(requestVO);
				  	 String utilClassName = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
					try {
						operatorUtili = (OperatorUtilI) Class.forName(utilClassName).newInstance();
					} catch (Exception e) {
						log.errorTrace("static", e);
						EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
								classname+"["+methodName+"]", "", "", "",
								"Exception while loading the class at the call:" + e.getMessage());
					}
				
		         loginService.sendRandomPassword(requestVO.getMode(), operatorUtili,response,responseSwag,requestVO);
		         
			}
			catch (BTSLBaseException be) {
		        log.error(classname, "Exceptin:e=" + be);
		        log.errorTrace(methodName, be);
	       	    if(response.getMessage()==null) {
		        String msg=RestAPIStringParser.getMessage(locale, be.getMessageKey(),be.getArgs());
		        response.setMessage(msg);
		        }
		        response.setMessageCode(be.getMessageKey());
		        if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
	        		 responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
	    	         response.setStatus(HttpStatus.SC_UNAUTHORIZED);
	            }
	           else{
	        	    responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
	           		response.setStatus(HttpStatus.SC_BAD_REQUEST);
	           }
	        	
	          
	        }
			catch(Exception e) {
				log.error(methodName, e);
				 response.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
		         if(response.getMessage()==null) {  
		        	 String resmsg = RestAPIStringParser.getMessage(
		    				new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY), PretupsErrorCodesI.REQ_NOT_PROCESS,
		    				null);
		            response.setMessage(resmsg);
		            }
		         
		            responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		        	response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
			
		 return response;
	 }
	
		
		@PostMapping(value = "/validateOTP", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
		@ResponseBody
	    /*@ApiOperation(value = "Get OTP", response = ValidateOTPResponseVO.class ,
	    		notes = ("Api Info:") + ("\n") + 
				("1. Resend: 'N' if sending first time") + ("\n"),
	    		authorizations = {
	    	     
	    })
		@ApiResponses(value = { 
				@ApiResponse(code = 200, message = "OK", response = ValidateOTPResponseVO.class),
				@ApiResponse(code = 400, message = "Bad Request"),
				@ApiResponse(code = 401, message = "Unauthorized"), 
				@ApiResponse(code = 404, message = "Not Found") })
		*/

		@io.swagger.v3.oas.annotations.Operation(summary = "${validateOTP.summary}", description="${validateOTP.description}",

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

		public BaseResponse validateOTP(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
	            @Parameter(description = SwaggerAPIDescriptionI.OTP_FOR_FORGOT_PASSWORD_CONTROLLER, required = true)
	            @RequestBody ValidateOTPRequestVO requestVO,HttpServletResponse responseSwag,HttpServletRequest httprequest) {
				final String methodName = "process";

				if (log.isDebugEnabled()) {
					log.debug(methodName, "Entered ");
				}
				   ++requestIdChannel;
				   ValidateOTPResponseVO response = null;
				
				   Locale locale= new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
				   try {
					   response = new ValidateOTPResponseVO();
					   if(BTSLUtil.isNullString(requestVO.getOtp())){
							response.setMessageCode(PretupsErrorCodesI.EXTSYS_BLANK);
							String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.EXTSYS_BLANK,new String[]{"OTP"});
							response.setMessage(msg);							
							 responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				           		response.setStatus(HttpStatus.SC_BAD_REQUEST);
				           		return response;
					   }
					  
					   loginService.validateOTP(response, AESEncryptionUtil.aesDecryptor(requestVO.getOtp(), Constants.A_KEY), responseSwag);
					   
				   }
				
					catch(Exception e) {
						log.error(methodName, e);
						 	response.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
				            String resmsg = RestAPIStringParser.getMessage(
				    				new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY), PretupsErrorCodesI.REQ_NOT_PROCESS,
				    				null);
				            response.setMessage(resmsg);
				            responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				        	response.setStatus(HttpStatus.SC_BAD_REQUEST);
					}
			return response;
		}
		
		// to change the user password

				/**
				 * @author harshita.bajaj
				 * @param headers
				 * @param requestVO
				 * @param response1
				 * @param httprequest
				 * @return
				 * @throws Exception
				 */

				@PostMapping(value = "/changePassword", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
				@ResponseBody
				/*@ApiOperation(tags = "Login Services", value = "change password", response = PasswordChangeResponseVO.class, authorizations = {
						@Authorization(value = "Authorization") })
				@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = PasswordChangeResponseVO.class),
						@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
						@ApiResponse(code = 404, message = "Not Found") })
*/
				@io.swagger.v3.oas.annotations.Operation(summary = "${changePassword.summary}", description="${changePassword.description}",

						responses = {
								@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
										@io.swagger.v3.oas.annotations.media.Content(
												mediaType = "application/json",
												array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = PasswordChangeResponseVO.class))
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


				public PasswordChangeResponseVO changePassword(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
						@Parameter(description = SwaggerAPIDescriptionI.PASSWORD_CHANGE,  required = true) @RequestBody ChangePasswordVO requestVO,
						HttpServletResponse response1, HttpServletRequest httprequest) throws Exception {
					final String methodName = "changePassword";
					if (log.isDebugEnabled()) {
						log.debug(methodName, "Entered ");
					}
					PasswordChangeResponseVO response = new PasswordChangeResponseVO();
					Connection con = null;
					MComConnectionI mcomCon = null;
					String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
					String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
					Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
					
					try {
						mcomCon = new MComConnection();
						con = mcomCon.getConnection();
						/*
						 * Authentication
						 * 
						 * @throws BTSLBaseException
						 */

						OAuthUser OAuthUserData = new OAuthUser();
						OAuthUserData.setData(new OAuthUserData());
						OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
						String loginId = OAuthUserData.getData().getLoginid();
						response = loginService.validateNewPassword(loginId, con, response1, requestVO);

					} catch (BTSLBaseException be) {
						log.error("changePassword", "Exceptin:e=" + be);
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
							mcomCon.close("changePassword");
							mcomCon = null;
						}
						if (log.isDebugEnabled()) {
							log.debug(methodName, "Exiting:=" + methodName);
						}

					}
					return response;
				}
				
				// to change the user password if the user has forgotten the password

				/**
				 * @author harshita.bajaj
				 * @param headers
				 * @param requestVO
				 * @param response1
				 * @param httprequest
				 * @return
				 * @throws Exception
				 */

				@PostMapping(value = "/forgotPassword", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
				@ResponseBody
				/*@ApiOperation(tags = "Login Services", value = "Forgot password", response = PasswordChangeResponseVO.class)
				@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = PasswordChangeResponseVO.class),
						@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
						@ApiResponse(code = 404, message = "Not Found") })
*/
				@io.swagger.v3.oas.annotations.Operation(summary = "${forgotPassword.summary}", description="${forgotPassword.description}",

						responses = {
								@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
										@io.swagger.v3.oas.annotations.media.Content(
												mediaType = "application/json",
												array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = PasswordChangeResponseVO.class))
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

				public PasswordChangeResponseVO forgotPassword(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
						@Parameter(description = SwaggerAPIDescriptionI.FORGOT_PASSWORD,  required = true) @RequestBody  ForgotPasswordVO requestVO,
						HttpServletResponse response1, HttpServletRequest httprequest) throws Exception {
					final String methodName = "forgot password";
					if (log.isDebugEnabled()) {
						log.debug(methodName, "Entered ");
					}
					PasswordChangeResponseVO response = new PasswordChangeResponseVO();
					Connection con = null;
					MComConnectionI mcomCon = null;
					String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
					String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
					Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
					
					try {
						mcomCon = new MComConnection();
						con = mcomCon.getConnection();
						response = loginService.forgotPassword(con, response1, requestVO);

					} catch (BTSLBaseException be) {
						log.error("forgotPassword", "Exceptin:e=" + be);
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
							mcomCon.close("forgotPassword");
							mcomCon = null;
						}
						if (log.isDebugEnabled()) {
							log.debug(methodName, "Exiting:=" + methodName);
						}

					}
					return response;
				}
				
				// to change the user password if first time login , password reset or if login after X no of days.
				
			/**
			 * @author harshita.bajaj
			 * @param headers
			 * @param response1
			 * @param httprequest
			 * @return
			 * @throws Exception
			 */

			@GetMapping(value = "/passwordChangeOnLogin", produces = MediaType.APPLICATION_JSON)
			@ResponseBody
			/*@ApiOperation(value = "password change on login", response = PasswordChangeOnLoginVO.class)

			@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = PasswordChangeOnLoginVO.class),
					@ApiResponse(code = 400, message = "Bad Request"),
					@ApiResponse(code = 401, message = "Unauthorized"),
					@ApiResponse(code = 404, message = "Not Found") })
*/
			@io.swagger.v3.oas.annotations.Operation(summary = "${passwordChangeOnLogin.summary}", description="${passwordChangeOnLogin.description}",

					responses = {
							@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
									@io.swagger.v3.oas.annotations.media.Content(
											mediaType = "application/json",
											array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = PasswordChangeOnLoginVO.class))
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

			
			public PasswordChangeOnLoginVO passwordChangeOnLogin(
					@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse response1,
					HttpServletRequest httpServletRequest,
					@Parameter(description = "loginId", required = true) @RequestParam("loginId") String loginId)
					throws Exception {
				final String methodName = "passwordChangeOnLogin";
				if (log.isDebugEnabled()) {
					log.debug(methodName, "Entered ");
				}
				PasswordChangeOnLoginVO response=null;
				Connection con = null;
				MComConnectionI mcomCon = null;
				String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
				String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
				Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
				response = new PasswordChangeOnLoginVO();
				try {
					mcomCon = new MComConnection();
					con = mcomCon.getConnection();
					/*
					 * Authentication
					 * 
					 * @throws BTSLBaseException
					 */

					response = loginService.validatePasswordOnLogin(loginId, con, response1);

				} catch (BTSLBaseException be) {
					log.error("passwordChangeOnLogin", "Exceptin:e=" + be);
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
						mcomCon.close("passwordChangeOnLoginController#" + methodName);
						mcomCon = null;
					}
					if (log.isDebugEnabled()) {
						log.debug(methodName, "Exiting:=" + methodName);
					}

				}
				return response;
			}
		}
