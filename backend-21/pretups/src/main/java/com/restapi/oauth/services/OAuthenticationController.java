package com.restapi.oauth.services;

import static java.lang.String.format;
import static java.util.Optional.of;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;


import com.btsl.common.TypesI;
import com.btsl.login.LoginLogger;
import com.btsl.login.LoginLoggerVO;
import com.btsl.util.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.login.LoginDAO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.PretupsStarter;
import com.btsl.user.businesslogic.CommonReportRequest;
import com.btsl.user.businesslogic.CommonReportVariablesRequest;
import com.btsl.user.businesslogic.LoginRequest;
import com.btsl.user.businesslogic.LoginResponse;
import com.btsl.user.businesslogic.LoginService;
import com.btsl.user.businesslogic.LogoutRequest;
import com.btsl.user.businesslogic.LogoutResponse;
import com.btsl.user.businesslogic.LogoutService;
import com.btsl.user.businesslogic.OAuthRefTokenRequest;
import com.btsl.user.businesslogic.OAuthTokenRequest;
import com.btsl.user.businesslogic.OAuthTokenRes;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.PagesUI;
import com.btsl.user.businesslogic.ReportTemplatesResponse;
import com.btsl.user.businesslogic.RuleRoleCodes;
import com.btsl.user.businesslogic.RulesUIMapping;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.ValidationException;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.Resource;
import java.nio.file.Path;
import java.nio.file.Paths;

/*@Path("/auth")*/
//@io.swagger.v3.oas.annotations.tags.Tag(name = "${}", description = "${}")//@Api(tags = "Authentication Management")
@Tag(name = "${OAuthenticationController.name}", description = "${OAuthenticationController.desc}")
@RestController
@RequestMapping(value = "/v1")
public class OAuthenticationController {

	private final Log log = LogFactory.getLog(this.getClass().getName());

	@Autowired
	private LoginService loginService;
	
	
	@Autowired
	private LogoutService logoutService;
	
	
	@PostMapping("/shutdownServer")
	public void shutdown() {
		PretupsStarter.shutdown();
	}

/*	
	
	@GetMapping(value = "v1/getPreferenceCache", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Prefe", response = LocaleResponse.class)
	public LocaleResponse fetchRedis() {
	
		String methodName = "getinstanceLoadHashFromRedis";
		if (_log.isDebugEnabled()) {
	        _log.debug(methodName, "Entered key: " + key);
	    }
	    HashMap<String,InstanceLoadVO> insMap =new HashMap<String,InstanceLoadVO>();
	    Jedis jedis = null;
		 try {
			 RedisActivityLog.log("LoadControllerCache->getinstanceLoadHashFromRedis->Start");
			 jedis = RedisConnectionPool.getPoolInstance().getResource();
			 Map<String,String> json = jedis.hgetAll(key);
			 RedisActivityLog.log("LoadControllerCache->getinstanceLoadHashFromRedis->End");

			 for (Entry<String, String> entry :json.entrySet()) {
				 insMap.put(entry.getKey(), gson.fromJson(entry.getValue(), InstanceLoadVO.class));
				
			}
		 }catch(JedisConnectionException je){
		 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
			        _log.errorTrace(methodName, je);
		            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[" + methodName + "]", "", "", "", "JedisConnectionException :" + je.getMessage());
		 }catch(NoSuchElementException  ex){
		 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
			        _log.errorTrace(methodName, ex);
		            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[" + methodName + "]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		 }catch (Exception e) {
		 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
		 	_log.errorTrace(methodName, e);
	        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[" + methodName + "]", "", "", "", "Exception :" + e.getMessage());
		 }
		 finally {
	        	if (jedis != null) {
	        	jedis.close();
	        	}
	        }
	  return insMap;
	}
*/
	
	
	@GetMapping(value = "/getLocaleList", produces = MediaType.APPLICATION_JSON_VALUE)
	//@ApiOperation(value = "Locale List", response = LocaleResponse.class)
	@io.swagger.v3.oas.annotations.Operation(summary = "${getLocaleList.summary}", description="${getLocaleList.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = LocaleResponse.class))
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


	/*@Timed(value = "getLocalistRequest", extraTags = { "version", "v1" }, percentiles = { 0.95,
			0.99 }, histogram = true)
	*/@Transactional(timeout = 120)
	public LocaleResponse getLocaleList() {
		//long startTime = clock.getTime();
		log.info("getLocaleList","Login getLocaleList request");
		LocaleResponse localeResponse = new LocaleResponse();
		LocaleRequest localeRequest = new LocaleRequest();
		localeResponse = loginService.getLocaleList(localeRequest);
		log.info("Login getLocalelist Response:  {}", localeResponse);
		/*long executionTime = clock.getTime() - startTime;
		this.metricRegistry.timer(Constants.STARTEXECUTION.getStrValue()).update(executionTime, TimeUnit.MILLISECONDS);
		this.metricRegistry.meter(Constants.ENDEXECUTION.getStrValue()).mark();*/
		return localeResponse;
	}
	
	@PostMapping(value = "/calculateSignature", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	//@ApiOperation(value = "Calculate Signature", response = String.class)


	@io.swagger.v3.oas.annotations.Operation(summary = "${calculateSignature.summary}", description="${calculateSignature.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = String.class))
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

	//@CrossOrigin(origins = "http://localhost:4200")
	public Optional<String> calculateSignature(
			
			 String body,
			 String nonce,
			 String token
			
			) {
		String KEY = format("%s.%s",token,nonce);
		
		try {
			return resolveEncodedBody(body,KEY);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	   private Optional<String> resolveEncodedBody(String body, final String KEY) throws IOException {
	        final HmacUtils hmacUtils = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, KEY);
	  	            String jsonBody =body;
	            return of(hmacUtils.hmacHex(jsonBody));
	    }

	   
		@PostMapping(value = "/logout", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	/*	@ApiOperation(value = "User Logout ", response = LogoutResponse.class, authorizations = {
				@Authorization(value = "Authorization") })
	*/
		@io.swagger.v3.oas.annotations.Operation(summary = "${logout.summary}", description="${logout.description}",

				responses = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = LogoutResponse.class))
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


		public LogoutResponse logout(
				HttpServletRequest httpServletRequest,
				@Parameter(description = "${swagger.param.checkSum}", required = true) @RequestHeader(value = "checkSum", required = true, defaultValue = " ") String checkSum,
				@Valid @RequestBody LogoutRequest logoutRequest) {
					String methodName = "logout" ;
					LoginLoggerVO loggerVO=new LoginLoggerVO();
					LogoutResponse logoutResponse=new LogoutResponse();
					try{
						loggerVO.setIpAddress(httpServletRequest.getRemoteAddr());
						loggerVO.setBrowser(httpServletRequest.getHeader("User-Agent"));
						if(logoutRequest.getRemarks().equalsIgnoreCase(PretupsI.LOGOUT_REQUEST)) {
							loggerVO.setLogType(TypesI.LOG_TYPE_LOGOUT);
							loggerVO.setOtherInformation("Logout Successfully");
						}else{
							loggerVO.setLogType(TypesI.LOG_TYPE_EXPIRED);
							loggerVO.setOtherInformation("Session Expired");
						}
						loggerVO.setLoginTime(null);
						loggerVO.setLogoutTime(new Date());
						String xForwardedFor = httpServletRequest.getHeader("x-forwarded-for");
						if (BTSLUtil.isNullString(xForwardedFor)) {
							loggerVO.setIpAddress(httpServletRequest.getRemoteAddr());
						} else {
							loggerVO.setIpAddress(xForwardedFor);
						}
						log.info("logout", "User Logout Request");
						logoutResponse = logoutService.execute(logoutRequest,loggerVO);
						log.info("User Logout Response:  {}", logoutResponse);
					}catch (Exception ex) {
						log.error(methodName, "Exception:e=" + ex);
						log.errorTrace(methodName, ex);
						loggerVO.setOtherInformation( ex.getMessage());
						//loggerVO.setOtherInformation(resmsg);
					}finally{
						LoginLogger.log(loggerVO);
					}
					return logoutResponse;
				}

		
	
	@PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	//@ApiOperation(value = "User Login", response = LoginResponse.class)
	@io.swagger.v3.oas.annotations.Operation(summary = "${login.summary}", description="${login.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = LoginResponse.class))
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

	//@Timed(value = "LoginRequest", extraTags = { "version", "v1" }, percentiles = { 0.95, 0.99 }, histogram = true)
	//@Transactional(timeout = 120, noRollbackForClassName = { "ValidationException" })
												
	public LoginResponse login(
			HttpServletRequest httpServletRequest,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = "${swagger.param.requestGatewayType}", required = true) @RequestParam(value = "requestGatewayType", required = true) String requestGatewayType,
			@Parameter(description = "${swagger.param.requestGatewayCode}", required = true) @RequestParam(value = "requestGatewayCode", required = true) String requestGatewayCode,
			@Parameter(description = "${swagger.param.requestGatewayLoginId}", required = true) @RequestParam(value = "requestGatewayLoginId", required = true) String requestGatewayLoginId,
			@Parameter(description = "${swagger.param.requestGatewayPassword}", required = true) @RequestParam(value = "requestGatewayPassword", required = true) String requestGatewayPassword,
			@Parameter(description = "${swagger.param.servicePort}", required = true) @RequestParam(value = "servicePort", required = true) String servicePort,
			@Parameter(description = "${swagger.param.RegexLang}", required = true) @RequestParam(value = "language", required = true) String language,
			@Parameter(description = "${swagger.param.checkSum}", required = true) @RequestHeader(value = "checkSum", required = true, defaultValue = " ") String checkSum,
			@RequestBody LoginRequest requestLog, HttpServletResponse response) {
		
		String methodName = "login" ;
		//long startTime = clock.getTime();
		LoginResponse loginResponse = new LoginResponse();
		log.info(methodName, "User Login Request");
		LoginLoggerVO loggerVO = new LoginLoggerVO();
		loggerVO.setIpAddress(httpServletRequest.getRemoteAddr());
		loggerVO.setBrowser(httpServletRequest.getHeader("User-Agent"));
		loggerVO.setLogType(TypesI.LOG_TYPE_LOGIN);
		loggerVO.setLoginTime(new Date());
		loggerVO.setLogoutTime(null);
		String xForwardedFor = httpServletRequest.getHeader("x-forwarded-for");
		if (BTSLUtil.isNullString(xForwardedFor)) {
			loggerVO.setIpAddress(httpServletRequest.getRemoteAddr());
		} else {
			loggerVO.setIpAddress(xForwardedFor);

		}
		//ConfigParams configParams2 = new ConfigParams();
		//configParams2.setLanguageSelected(language);
		//VMSSessionHolder.put(configParams2);
		try {
			loginResponse = loginService.execute(requestGatewayType, requestGatewayCode, requestGatewayLoginId,
					requestGatewayPassword, servicePort, requestLog, language, httpServletRequest,loggerVO);
			log.info(methodName, "User Login Response:  {}", loginResponse);
			loggerVO.setOtherInformation(loginResponse.getMessage());
		}catch(BTSLBaseException be) {
        	log.error(methodName, "Exception:e=" + be);
            log.errorTrace(methodName, be);
            String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
			String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
			Locale locale = new Locale(defaultLanguage, defaultCountry);
			String resmsg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
			loginResponse.setMessageCode(be.getMessage());
			loginResponse.setMessage(resmsg);
			if(be.getMessage().equalsIgnoreCase(PretupsErrorCodesI.PASSWORD_BLOCKED)||
					be.getMessage().equalsIgnoreCase(PretupsErrorCodesI.AUTHENDICATION_ERROR)){
              	 response.setStatus(HttpStatus.SC_UNAUTHORIZED);
              	loginResponse.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
             }else {
            	response.setStatus(HttpStatus.SC_BAD_REQUEST);
               	loginResponse.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
             }
			loggerVO.setOtherInformation(resmsg);
        }catch (ValidationException ex) {
        	log.error(methodName, "Exception:e=" + ex);
        	log.errorTrace(methodName, ex);
        	response.setStatus(HttpStatus.SC_BAD_REQUEST);
        	loginResponse.setStatus(String.valueOf(PretupsI.RESPONSE_FAIL));
        	loginResponse.setMessage(ex.getField());
    		loginResponse.setMessageCode(ex.getErrorCode());
			loggerVO.setOtherInformation(ex.getMessage());
	    }catch (Exception ex) {
        	log.error(methodName, "Exception:e=" + ex);
        	log.errorTrace(methodName, ex);
        	response.setStatus(HttpStatus.SC_BAD_REQUEST);
        	loginResponse.setStatus(String.valueOf(PretupsI.RESPONSE_FAIL));
        	String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
			String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
			Locale locale = new Locale(defaultLanguage, defaultCountry);
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USER_LOGIN_WENT_WRONG, null);
        	loginResponse.setMessage(resmsg);
    		loginResponse.setMessageCode(PretupsErrorCodesI.USER_LOGIN_WENT_WRONG);
			loggerVO.setOtherInformation(resmsg);
	    }
		finally {
			LoginLogger.log(loggerVO);
		}

		return loginResponse;
	}

	
	@PostMapping(value = "/generateTokenAPI")
	@ResponseBody
	/*@ApiOperation(value = "Generate Token API", response = OAuthTokenRes.class,
	notes = SwaggerAPIDescriptionI.OAUTHENTICATION_API_INFO)
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = OAuthTokenRes.class),
	        @ApiResponse(code = 201, message = "Created"),
	        @ApiResponse(code = 400, message = "Bad Request"),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${generateTokenAPI.summary}", description="${generateTokenAPI.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = OAuthTokenRes.class))
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

	public OAuthTokenRes generateTokenApi(@Parameter(description = "Generate Token API", required= true) @RequestBody OAuthTokenRequest oAuthTokenReq,

			@Parameter(description = SwaggerAPIDescriptionI.CLIENT_ID, required = true, example="a")
			@RequestHeader("CLIENT_ID") String clientId,
			
			@Parameter(description = SwaggerAPIDescriptionI.CLIENT_SECRET, required = true, example="a")
			@RequestHeader("CLIENT_SECRET") String clientSecret,

			/*@Parameter(description  = SwaggerAPIDescriptionI.LANGUAGE, required = false)
			@RequestHeader("language") String language,
			*/
			@Parameter(description  = SwaggerAPIDescriptionI.REQ_GATEWAY_CODE, required = true, example="REST")
			@RequestHeader("requestGatewayCode") String requestGatewayCode,
			
			@Parameter(description  = SwaggerAPIDescriptionI.REQ_GATEWAY_LOGIN_ID, required = true, example="pretups123" )
			@RequestHeader("requestGatewayLoginId") String requestGatewayLoginId,
			
			@Parameter(description  = SwaggerAPIDescriptionI.REQ_GATEWAY_PWD, required = true,  example="1357")
			@RequestHeader("requestGatewayPsecure") String requestGatewayPsecure,
			
			@Parameter(description  = SwaggerAPIDescriptionI.REQ_GATEWAY_TYPE, required = true, example="REST")
			@RequestHeader("requestGatewayType") String requestGatewayType,
			
			@Parameter(description  = SwaggerAPIDescriptionI.SERVICE_PORT, required = true,  example="190")
			@RequestHeader("servicePort") String servicePort,
			
			@Parameter(description  = SwaggerAPIDescriptionI.SCOPE, required = true)// allowableValues = "All,Read,Write")
			@RequestHeader("scope") String scope, HttpServletRequest req
			)
			throws Exception {
		String methodName = "generateToken";
		try {
			System.out.println(req.getMethod());
			
			
			oAuthTokenReq.setReqGatewayCode(requestGatewayCode);
			oAuthTokenReq.setReqGatewayLoginId(requestGatewayLoginId);
			if ("SHA".equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE))) {
				oAuthTokenReq.setReqGatewayPassword(BTSLUtil.encryptText(requestGatewayPsecure));
			}else {
				oAuthTokenReq.setReqGatewayPassword(requestGatewayPsecure);
			}
			
			oAuthTokenReq.setReqGatewayType(requestGatewayType);
			oAuthTokenReq.setServicePort(servicePort);
			oAuthTokenReq.setClientId(clientId);
			oAuthTokenReq.setClientSecret(clientSecret);
			oAuthTokenReq.setScope(scope);
			return OAuthenticationUtil.generateTokenApi(oAuthTokenReq, scope, req);
		} catch (BTSLBaseException be) {
			
			log.error(methodName, "BTSLBaseException occured " + be);
			log.errorTrace(methodName, be);
			OAuthTokenRes oAuthTokenRes = new OAuthTokenRes() ;
			oAuthTokenRes.setStatus(PretupsI.UNAUTHORIZED_ACCESS);
			String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
			String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
			Locale locale = new Locale(defaultLanguage, defaultCountry);
			String resmsg = RestAPIStringParser.getMessage(locale, be.getMessage(),
					be.getArgs());
			oAuthTokenRes.setMessageCode(be.getMessage());
			oAuthTokenRes.setMessage(resmsg);
			return oAuthTokenRes;
		} catch (Exception e) {

			log.error(methodName, "Exception occured " + e);
			log.errorTrace(methodName, e);
			OAuthTokenRes oAuthTokenRes = new OAuthTokenRes() ;
			oAuthTokenRes.setStatus(PretupsI.UNAUTHORIZED_ACCESS);
			String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
			String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
			Locale locale = new Locale(defaultLanguage, defaultCountry);
			String resmsg = RestAPIStringParser.getMessage(locale, e.getMessage(), null);
			oAuthTokenRes.setMessageCode(e.getMessage());
			oAuthTokenRes.setMessage(resmsg);
			return oAuthTokenRes;
		}finally {
			if(log.isDebugEnabled()) {
				log.debug(methodName, "Exiting");
			}
		} 
	}

	
	/*@PostMapping(value = "/generateToken")
	@ResponseBody
	@ApiOperation(value = "Generate Token API", response = OAuthTokenRes.class,
	notes = SwaggerAPIDescriptionI.OAUTHENTICATION_API_INFO)
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = OAuthTokenRes.class),
	        @ApiResponse(code = 201, message = "Created"),
	        @ApiResponse(code = 400, message = "Bad Request"),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })
	
	public OAuthTokenRes generateToken(@Parameter(description = "Generate Token API", required= true) @RequestBody OAuthTokenReq oAuthTokenReq)
			throws Exception {
		String methodName = "generateToken";
		try {
			
			return OAuthenticationUtil.generateToken(oAuthTokenReq);
		} catch (Exception e) {

			log.error(methodName, "Exception occured " + e);
			OAuthTokenRes oAuthTokenRes = new OAuthTokenRes() ;
			oAuthTokenRes.setStatus(PretupsI.UNAUTHORIZED_ACCESS);
			String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))), e.getMessage(),
					null);
			oAuthTokenRes.setMessageCode(e.getMessage());
			oAuthTokenRes.setMessage(resmsg);
			return oAuthTokenRes;
		}
	}

	@PostMapping(value = "/refreshToken")
	@ResponseBody
	@ApiOperation(value = "Refresh Token API", response = OAuthTokenRes.class, 
	notes= SwaggerAPIDescriptionI.REFRESH_TOKEN_API_INFO)
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = OAuthTokenRes.class),
	        @ApiResponse(code = 201, message = "Created"),
	        @ApiResponse(code = 400, message = "Bad Request"),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })
	public OAuthTokenRes refreshToken(@Parameter(description = "Refresh Token API", required= true) @RequestBody OAuthRefTokenReq oAuthRefTokenReq)
			 {
		String methodName = "refreshToken";
		try {
			return OAuthenticationUtil.refreshToken(oAuthRefTokenReq);
		} catch (Exception e) {
			log.error(methodName, "Exception occured " + e);
			OAuthTokenRes oAuthTokenRes = new OAuthTokenRes() ; 
			oAuthTokenRes.setStatus(PretupsI.UNAUTHORIZED_ACCESS);
			String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))), e.getMessage(),
					null);
			oAuthTokenRes.setMessageCode(e.getMessage());
			oAuthTokenRes.setMessage(resmsg);
			return oAuthTokenRes;
		}
	}
	
*/	
	
	@PostMapping(value = "/refreshTokenApi")
	@ResponseBody
	/*@ApiOperation(value = "Refresh Token API", response = OAuthTokenRes.class,
	notes= SwaggerAPIDescriptionI.REFRESH_TOKEN_API_INFO)
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = OAuthTokenRes.class),
	        @ApiResponse(code = 201, message = "Created"),
	        @ApiResponse(code = 400, message = "Bad Request"),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${refreshTokenApi.summary}", description="${refreshTokenApi.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = OAuthTokenRes.class))
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

	public OAuthTokenRes refreshTokenApi(@Parameter(description = "Refresh Token API", required= true) @RequestBody OAuthRefTokenRequest oAuthRefTokenReq,
			
			@Parameter(description = SwaggerAPIDescriptionI.CLIENT_ID, required = true)
			@RequestHeader("CLIENT_ID") String clientId,
			
			@Parameter(description = SwaggerAPIDescriptionI.CLIENT_SECRET, required = true)
			@RequestHeader("CLIENT_SECRET") String clientSecret,
			@Parameter(description  = SwaggerAPIDescriptionI.SCOPE, required = true)// allowableValues = "All,Read,Write")
			@RequestHeader("scope") String scope, HttpServletRequest req

)
			 {
		String methodName = "refreshToken";
		try {
			oAuthRefTokenReq.setScope(scope);
			oAuthRefTokenReq.setClientId(clientId);
			oAuthRefTokenReq.setClientSecret(clientSecret);
			
			return OAuthenticationUtil.refreshTokenApi(oAuthRefTokenReq);
		} catch (Exception e) {
			log.error(methodName, "Exception occured " + e);
			OAuthTokenRes oAuthTokenRes = new OAuthTokenRes() ; 
			oAuthTokenRes.setStatus(PretupsI.UNAUTHORIZED_ACCESS);
			String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
			String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
			Locale locale = new Locale(defaultLanguage, defaultCountry);
			String resmsg = RestAPIStringParser.getMessage(locale, e.getMessage(), null);
			oAuthTokenRes.setMessageCode(e.getMessage());
			oAuthTokenRes.setMessage(resmsg);
			return oAuthTokenRes;
		}
	}

	


	@PostMapping(value = "/commonReportAPI")
	//@ApiOperation(value = "Common Reports", response = String.class)
	@io.swagger.v3.oas.annotations.Operation(summary = "${commonReportAPI.summary}", description="${commonReportAPI.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = String.class))
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


	//@CrossOrigin(origins = "http://localhost:4200")
	@ResponseBody
	public String commonReportAPI(
			@RequestBody CommonReportRequest request
			) {

		UserDAO userDao = new UserDAO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		
		try {
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
		return userDao.fetchCommonReport(con, request);
		}catch(Exception e) {
			
		}finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("fetchRoles#");
					mcomCon = null;
				}
			} catch (Exception e) {
				log.errorTrace("fetchRoles", e);
			}
		}
		
		return null;


	
	}


	
	
	@PostMapping(value = "/getLOVList", consumes = MediaType.APPLICATION_JSON_VALUE
			, produces = MediaType.APPLICATION_JSON_VALUE)	
	//@ApiOperation(value = "Common Reports", response = String.class)
	@io.swagger.v3.oas.annotations.Operation(summary = "${getLOVList.summary}", description="${getLOVList.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = String.class))
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

	//@CrossOrigin(origins = "http://localhost:4200")
	@ResponseBody
	public String lovList(
			@RequestBody CommonReportVariablesRequest request
			) {

		UserDAO userDao = new UserDAO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		
		try {
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
		return userDao.fetchCommonReportVariables(con, request);
		}catch(Exception e) {
			
		}finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("fetchRoles#");
					mcomCon = null;
				}
			} catch (Exception e) {
				log.errorTrace("fetchRoles", e);
			}
		}
		
		return null;


	
	}

	private java.nio.file.Path foundFile;

	public org.springframework.core.io.Resource getFileAsResource(String fileCode) throws IOException {
		//final java.nio.file.Path  foundFile;

		java.nio.file.Path dirPath = java.nio.file.Paths.get("Files-Upload");

		java.nio.file.Files.list(dirPath).forEach(file -> {
			if (file.getFileName().toString().startsWith(fileCode)) {
				foundFile = file;
				return;
			}
		});

		if (foundFile != null) {
			return new org.springframework.core.io.UrlResource(foundFile.toUri());
		}

		return null;
	}

	public Resource loadFile(String fileName) {
		// TODO Auto-generated method stub
		try {
			//Path file = this.dirLocation.resolve(fileName).normalize();
			String filepath = Constants.getProperty("COMMON_REPORTS_PATH");
			Path file = Paths.get(filepath + fileName);

			Resource resource = new UrlResource(file.toUri());

			if (resource.exists() || resource.isReadable()) {
				return resource;
			}
			else {
				throw new FileNotFoundException("Could not find file");
			}
		}
		catch (Exception e) {
			//throw new Exception("Could not download file");
		}
		return null;
	}

	@GetMapping("/downloadFileTest")
	@ResponseBody
	public ResponseEntity<Resource> downloadFile(
			@RequestParam("fileName") String fileName
	) {


		Resource resource = loadFile(fileName);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
         /*

		  String path = "D:/"+fileCode;
	        File fileDownload = new File(path);
	        javax.ws.rs.core.Response.ResponseBuilder response = javax.ws.rs.core.Response.ok((Object) fileDownload);
	        response.header("Content-Disposition", "attachment;filename="+fileCode);
	        return response.build();

*/
	}

	@GetMapping(value = "/fetchRoles", produces = MediaType.APPLICATION_JSON_VALUE)
	//@ApiOperation(value = "Fetch Roles", response = String.class)
	@io.swagger.v3.oas.annotations.Operation(summary = "${fetchRoles.summary}", description="${fetchRoles.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = RuleRoleCodes.class))
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

	//@CrossOrigin(origins = "http://localhost:4200")
	public RuleRoleCodes fetchRoles(
			) {
		
		UserDAO userDao = new UserDAO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		
		try {
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
		return userDao.fetchUserRolesUI(con);
		}catch(Exception e) {
			
		}finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("fetchRoles#");
					mcomCon = null;
				}
			} catch (Exception e) {
				log.errorTrace("fetchRoles", e);
			}
		}
		
		return null;
		//return "{ \"RuleRoleCodes\": [{ \"module\": \"C2S\", \"ruleRole\": [{ \"rule_id\": \"Reversal\", \"role_code\": \"C2SREV\" }, { \"rule_id\": \"Recharge\", \"role_code\": \"C2SRECHARGE\" }, { \"rule_id\": \"Batch_recharge\", \"role_code\": \"SCHEDULETOPUP\" } ] }, { \"module\": \"C2C\", \"ruleRole\": [{ \"rule_id\": \"Buy_Stock\", \"role_code\": \"C2CTRFINI\" }, { \"rule_id\": \"Return_Stock\", \"role_code\": \"C2CRETURN\" }, { \"rule_id\": \"Transfer_Stock\", \"role_code\": \"C2CTRF\" }, { \"rule_id\": \"Transfer_Approval_1\", \"role_code\": \"C2CTRFAPR1\" }, { \"rule_id\": \"Transfer_Approval_2\", \"role_code\": \"C2CTRFAPR2\" }, { \"rule_id\": \"Transfer_Approval_3\", \"role_code\": \"C2CTRFAPR3\" }, { \"rule_id\": \"Withdraw_Stock\", \"role_code\": \"C2CWDL\" }, { \"rule_id\": \"Buy_Vouchers\", \"role_code\": \"C2CBUYVINI\" }, { \"rule_id\": \"Transfer_Vouchers\", \"role_code\": \"C2CVINI\" }, { \"rule_id\": \"Transfer_Vouchers_Approval_1\", \"role_code\": \"C2CVCTRFAPR1\" }, { \"rule_id\": \"Transfer_Vouchers_Approval_2\", \"role_code\": \"C2CVCTRFAPR2\" }, { \"rule_id\": \"Transfer_Vouchers_Approval_3\", \"role_code\": \"C2CVCTRFAPR3\" } , { \"rule_id\": \"Approve_Batch_Transfer\", \"role_code\": \"BC2CAPPROVE\" }, { \"rule_id\": \"Approve_Batch_Withdraw\", \"role_code\": \"BC2CWDRAPP\" }, { \"rule_id\": \"Initiate_Batch_Transfer\", \"role_code\": \"BC2CINITIATE\" }, { \"rule_id\": \"Initiate_Batch_Withdraw\", \"role_code\": \"BC2CWDRW\" } ] } ] }";
	}
	

	

	@PostMapping(value = "/customReports", produces = MediaType.APPLICATION_JSON_VALUE)
	//@ApiOperation(value = "Fetch Reports Templates", response = String.class)
	@io.swagger.v3.oas.annotations.Operation(summary = "${customReports.summary}", description="${customReports.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ReportTemplatesResponse.class))
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

	public ReportTemplatesResponse customReports(@RequestBody CustomReportRequestVO customReportRequestVO) {
		
		UserDAO userDao = new UserDAO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		
		try {
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
		return userDao.fetchReportsTemplates(con,customReportRequestVO.getRoleCodeSet());
		/*	
			BufferedReader br = new BufferedReader(new FileReader("d:/reports_templates.txt"));
			
			String str="";
			String response="";
			while((str=br.readLine()) != null) {
				
				response = response + str;
			}
			
			br.close();
			
			return response;
		*/	
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("fetchRoles#");
					mcomCon = null;
				}
			} catch (Exception e) {
				log.errorTrace("fetchRoles", e);
			}
		}
		
		return null;
	}


	@GetMapping(value = "/fetchRules", produces = MediaType.APPLICATION_JSON_VALUE)
	//@ApiOperation(value = "Fetch Rules", response = String.class)
	@io.swagger.v3.oas.annotations.Operation(summary = "${fetchRules.summary}", description="${fetchRules.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = RulesUIMapping.class))
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

	//@CrossOrigin(origins = "http://localhost:4200")
	public RulesUIMapping fetchRules(
			) {
		
		UserDAO userDao = new UserDAO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		
		try {
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
		return userDao.fetchUserRulesUI(con);
		
		}catch(Exception e) {
			
		}finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("fetchRoles#");
					mcomCon = null;
				}
			} catch (Exception e) {
				log.errorTrace("fetchRoles", e);
			}
		}
		
		return null;
	}
	
	
	
	

	@GetMapping(value = "/fetchPages", produces = MediaType.APPLICATION_JSON_VALUE)
	//@ApiOperation(value = "Fetch Pages", response = String.class)
	@io.swagger.v3.oas.annotations.Operation(summary = "${fetchPages.summary}", description="${fetchPages.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = PagesUI.class))
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

	public PagesUI pages(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers
			, HttpServletRequest request1
			, HttpServletResponse response1
			) {
		
		UserDAO userDao = new UserDAO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		OAuthUser oAuthUser = null;
		OAuthUserData oAuthUserData = null;
		
		try {
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			oAuthUser = new OAuthUser();
			oAuthUserData =new OAuthUserData();
			
			oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers,response1);
			
			
			
			try {
		
				
				LoginDAO _loginDAO = new LoginDAO();
				
				
				ChannelUserVO channelUserVO = _loginDAO.loadUserDetails(con, oAuthUser.getData().getLoginid(), null, BTSLUtil.getBTSLLocale(request1));
				
				if (channelUserVO != null) {
	                channelUserVO.setActiveUserID(channelUserVO.getUserID());
	                if (PretupsI.STAFF_USER_TYPE.equals(channelUserVO.getUserType())) {
	                    ChannelUserVO parentChannelUserVO = new UserDAO().loadUserDetailsFormUserID(con, channelUserVO.getParentID());
	                    staffUserDetails(channelUserVO, parentChannelUserVO);
	                    channelUserVO.setUserCode(parentChannelUserVO.getUserCode());
	                }
	              //  staffLoginDetails(channelUserVO, loginLoggerVO);
	                //channelUserVO.setSessionInfoVO(sessionInfoVO);
	            }
				
				
				String tabName = null;
				
				if(headers.get("tab") != null) {
					tabName = headers.get("tab").get(0) ; 
				}
				
				
				
				
				 if (channelUserVO.isStaffUser()) {
					 
					 
					 if(headers.get("pagetype") == null) {
						 return userDao.fetchPagesUI(con, channelUserVO.getActiveUserID(), channelUserVO.getDomainTypeCode(), channelUserVO.getCategoryCode(), channelUserVO.getCategoryVO().getFixedRoles(), null, tabName);
					 }else {
					 return userDao.fetchPagesUI(con, channelUserVO.getActiveUserID(), channelUserVO.getDomainTypeCode(), channelUserVO.getCategoryCode(), channelUserVO.getCategoryVO().getFixedRoles(), headers.get("pagetype").get(0), tabName);
					 }
				 }else {
					 if(headers.get("pagetype") == null) {
						 return userDao.fetchPagesUI(con, channelUserVO.getUserID(), channelUserVO.getDomainTypeCode(), channelUserVO.getCategoryCode(),  channelUserVO.getCategoryVO().getFixedRoles(), null, tabName); 
					 }else {
					 return userDao.fetchPagesUI(con, channelUserVO.getUserID(), channelUserVO.getDomainTypeCode(), channelUserVO.getCategoryCode(),  channelUserVO.getCategoryVO().getFixedRoles(), headers.get("pagetype").get(0), tabName);
					 }
				 }
			
			
				
			}catch(Exception e) {
				log.errorTrace("fetchPages", e);
			}
		
		}catch(Exception e) {
			
		}finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("fetchRoles#");
					mcomCon = null;
				}
			} catch (Exception e) {
				log.errorTrace("fetchRoles", e);
			}
		}
		
		return null;
	}
	
	

	@GetMapping(value = "/fetchTabs", produces = MediaType.APPLICATION_JSON_VALUE)
	//@ApiOperation(value = "Fetch Tabs", response = String.class)
	@io.swagger.v3.oas.annotations.Operation(summary = "${fetchTabs.summary}", description="${fetchTabs.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = String.class))
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

	public String tabs(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers
			, HttpServletRequest request1
			, HttpServletResponse response1
			) {
		
		UserDAO userDao = new UserDAO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		OAuthUser oAuthUser = null;
		OAuthUserData oAuthUserData = null;
		
		try {
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			oAuthUser = new OAuthUser();
			oAuthUserData =new OAuthUserData();
			
			oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers,response1);
			
			
			
			try {
		
				
				LoginDAO _loginDAO = new LoginDAO();
				
				
				ChannelUserVO channelUserVO = _loginDAO.loadUserDetails(con, oAuthUser.getData().getLoginid(), null, BTSLUtil.getBTSLLocale(request1));
				
				if (channelUserVO != null) {
	                channelUserVO.setActiveUserID(channelUserVO.getUserID());
	                if (PretupsI.STAFF_USER_TYPE.equals(channelUserVO.getUserType())) {
	                    ChannelUserVO parentChannelUserVO = new UserDAO().loadUserDetailsFormUserID(con, channelUserVO.getParentID());
	                    staffUserDetails(channelUserVO, parentChannelUserVO);
	                    channelUserVO.setUserCode(parentChannelUserVO.getUserCode());
	                }
	            }
				
				
				String tabName = "ALL";
			
				
				 if (channelUserVO.isStaffUser()) {
					 
					 
					 if(headers.get("pagetype") == null) {
						 return userDao.fetchTabsUI(con, channelUserVO.getActiveUserID(), channelUserVO.getDomainTypeCode(), channelUserVO.getCategoryCode(), channelUserVO.getCategoryVO().getFixedRoles(), null, tabName);
					 }else {
					 return userDao.fetchTabsUI(con, channelUserVO.getActiveUserID(), channelUserVO.getDomainTypeCode(), channelUserVO.getCategoryCode(), channelUserVO.getCategoryVO().getFixedRoles(), headers.get("pagetype").get(0), tabName);
					 }
				 }else {
					 if(headers.get("pagetype") == null) {
						 return userDao.fetchTabsUI(con, channelUserVO.getUserID(), channelUserVO.getDomainTypeCode(), channelUserVO.getCategoryCode(),  channelUserVO.getCategoryVO().getFixedRoles(), null, tabName); 
					 }else {
					 return userDao.fetchTabsUI(con, channelUserVO.getUserID(), channelUserVO.getDomainTypeCode(), channelUserVO.getCategoryCode(),  channelUserVO.getCategoryVO().getFixedRoles(), headers.get("pagetype").get(0), tabName);
					 }
				 }
			
			
				
			}catch(Exception e) {
				log.errorTrace("fetchPages", e);
			}
		
		}catch(Exception e) {
			
		}finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("fetchRoles#");
					mcomCon = null;
				}
			} catch (Exception e) {
				log.errorTrace("fetchRoles", e);
			}
		}
		
		return null;
	}

	@GetMapping(value = "/validate-token", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Validate Token", description = "Validate Token", tags = { "OAuthentication" }, responses = {
			@ApiResponse(responseCode = "200", description = "Token is valid", content = @Content(schema = @Schema(implementation = TokenValidationResponse.class))),
			@ApiResponse(responseCode = "400", description = "Due to some technical reasons, your request could not be processed at this time. Please try later", content = @Content(schema = @Schema(implementation = TokenValidationResponse.class))),
			@ApiResponse(responseCode = "401", description = "Token is invalid", content = @Content(schema = @Schema(implementation = TokenValidationResponse.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = TokenValidationResponse.class))) })
	public TokenValidationResponse validateToken(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletRequest request1, HttpServletResponse responseSwag) {
		final String methodName = "validateToken";
		OAuthUser oAuthUser = null;
		OAuthUserData oAuthUserData = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		TokenValidationResponse response = new TokenValidationResponse();
		try {
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			oAuthUser = new OAuthUser();
			oAuthUserData =new OAuthUserData();
			oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers,responseSwag);
			response.setLoginId(oAuthUser.getData().getLoginid());
			response.setStatus(String.valueOf(HttpStatus.SC_OK));
			response.setMessageCode("200");
			response.setMessage("Token is valid");
		}catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);
			if(be.getMessage().equalsIgnoreCase("1080001")||be.getMessage().equalsIgnoreCase("1080002")||be.getMessage().equalsIgnoreCase("1080003")||
					be.getMessage().equalsIgnoreCase("241023")||be.getMessage().equalsIgnoreCase("241018")){
				response.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else{
				response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
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
			response.setStatus(String.valueOf(PretupsI.RESPONSE_FAIL));
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


	
	 protected void staffUserDetails(ChannelUserVO channelUserVO, ChannelUserVO parentChannelUserVO) {
	        channelUserVO.setUserID(channelUserVO.getParentID());
	        channelUserVO.setParentID(parentChannelUserVO.getParentID());
	        channelUserVO.setOwnerID(parentChannelUserVO.getOwnerID());
	        channelUserVO.setStatus(parentChannelUserVO.getStatus());
	        channelUserVO.setUserType(parentChannelUserVO.getUserType());
	        channelUserVO.setStaffUser(true);
	        channelUserVO.setMsisdn(parentChannelUserVO.getMsisdn());
	        channelUserVO.setPinRequired(parentChannelUserVO.getPinRequired());
	        channelUserVO.setSmsPin(parentChannelUserVO.getSmsPin());
	        channelUserVO.setParentLoginID(parentChannelUserVO.getLoginID());
	    }

}
