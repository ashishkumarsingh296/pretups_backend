package com.restapi.redisapi;

import java.sql.Connection;

import com.btsl.common.BaseResponse;
import com.btsl.common.BaseResponseRedoclyCommon;
import com.btsl.util.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.restapi.user.service.FileDownloadResponseMulti;

import io.swagger.v3.oas.annotations.tags.Tag;




import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${RedisController.name}", description = "${RedisController.desc}")//@Api(tags="REDIS")
@RestController
@RequestMapping(value = "/v1/redis")
public class RedisController {
	public static final Log log = LogFactory.getLog(RedisController.class.getName());
	
	@Autowired
	RedisServiceI redisService;
	
	@GetMapping(value= "/getLookupsCache", produces = MediaType.APPLICATION_JSON)	
	@ResponseBody
	/*
	@ApiResponses(value = {
		      @ApiResponse(code = 200, message = "OK", response = LookupsCacheResponse.class),
		      @ApiResponse(code = 400, message = "Bad Request" ),
		      @ApiResponse(code = 401, message = "Unauthorized"),
		      @ApiResponse(code = 404, message = "Not Found")
		 })
*/
	@Operation(summary = "${RedisController.lookupsCache.name}", description = "${RedisController.lookupsCache.desc}",

			responses = {
					@ApiResponse(responseCode =Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = LookupsCacheResponse.class))) }

					),

					@ApiResponse(responseCode = Constants.API_BAD_REQ_RESPONSE_CODE, description = Constants.API_BAD_REQ_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class))

									, examples = {
									@ExampleObject(value = BaseResponseRedoclyCommon.BAD_REQUEST) }

							) }),
					@ApiResponse(responseCode = Constants.API_UNAUTH_RESPONSE_CODE, description = Constants.API_UNAUTH_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
									@ExampleObject(value = BaseResponseRedoclyCommon.UNAUTH) }

							) }),
					@ApiResponse(responseCode = Constants.API_NOT_FOUND_RESPONSE_CODE, description = Constants.API_NOT_FOUND_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
									@ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND) }

							) }),
					@ApiResponse(responseCode = Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = Constants.API_INTERNAL_ERROR_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
									@ExampleObject(value = BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR) }) }) })
	public LookupsCacheResponse lookupsCache (
	@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
	HttpServletResponse responseSwag) {
		final String methodName = "lookupsCache";
	     if (log.isDebugEnabled()) {
	         log.debug(methodName, "Entered");
	     }
	     Connection con = null;
	     MComConnectionI mcomCon = null;
	     String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		 String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		 
		 LookupsCacheResponse response = new LookupsCacheResponse();
		 try {
			 mcomCon = new MComConnection();
	         con=mcomCon.getConnection();
	         response = redisService.lookupsCache(con, response, responseSwag);
			 
		 }catch (Exception e) {
	            log.error(methodName, "Exceptin:e=" + e);
	            log.errorTrace(methodName, e);
	            response.setStatus(PretupsI.RESPONSE_FAIL);
	    		response.setMessageCode("error.general.processing");
	    		response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");
	        } finally {
	        	if(mcomCon != null)
	        	{
	        		mcomCon.close("RedisControllerr#"+methodName);
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
	@GetMapping(value= "/getSublookupsCache", produces = MediaType.APPLICATION_JSON)	
	@ResponseBody
	/*
	@ApiResponses(value = {
		      @ApiResponse(code = 200, message = "OK", response = SublookupsCacheResponse.class),
		      @ApiResponse(code = 400, message = "Bad Request" ),
		      @ApiResponse(code = 401, message = "Unauthorized"),
		      @ApiResponse(code = 404, message = "Not Found")
		 })

	 */
	@Operation(summary = "${RedisController.sublookupsCache.name}", description = "${RedisController.sublookupsCache.desc}",

			responses = {
					@ApiResponse(responseCode =Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SublookupsCacheResponse.class))) }

					),

					@ApiResponse(responseCode = Constants.API_BAD_REQ_RESPONSE_CODE, description = Constants.API_BAD_REQ_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class))

									, examples = {
									@ExampleObject(value = BaseResponseRedoclyCommon.BAD_REQUEST) }

							) }),
					@ApiResponse(responseCode = Constants.API_UNAUTH_RESPONSE_CODE, description = Constants.API_UNAUTH_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
									@ExampleObject(value = BaseResponseRedoclyCommon.UNAUTH) }

							) }),
					@ApiResponse(responseCode = Constants.API_NOT_FOUND_RESPONSE_CODE, description = Constants.API_NOT_FOUND_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
									@ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND) }

							) }),
					@ApiResponse(responseCode = Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = Constants.API_INTERNAL_ERROR_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
									@ExampleObject(value = BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR) }) }) })
	public SublookupsCacheResponse sublookupsCache (
	@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
	HttpServletResponse responseSwag) {
		final String methodName = "sublookupsCache";
	     if (log.isDebugEnabled()) {
	         log.debug(methodName, "Entered");
	     }
	     Connection con = null;
	     MComConnectionI mcomCon = null;
	     String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		 String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		 SublookupsCacheResponse response = new SublookupsCacheResponse();
		 try {
			 mcomCon = new MComConnection();
	         con=mcomCon.getConnection();
	         response=redisService.sublookupsCache(con, response, responseSwag);
		 }catch (Exception e) {
	            log.error(methodName, "Exceptin:e=" + e);
	            log.errorTrace(methodName, e);
	            response.setStatus(PretupsI.RESPONSE_FAIL);
	    		response.setMessageCode("error.general.processing");
	    		response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");
	        } finally {
	        	if(mcomCon != null)
	        	{
	        		mcomCon.close("RedisControllerr#"+methodName);
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
	@GetMapping(value= "/getPreferenceCache", produces = MediaType.APPLICATION_JSON)	
	@ResponseBody
	/*
	@ApiResponses(value = {
		      @ApiResponse(code = 200, message = "OK", response = PreferenceCacheResponse.class),
		      @ApiResponse(code = 400, message = "Bad Request" ),
		      @ApiResponse(code = 401, message = "Unauthorized"),
		      @ApiResponse(code = 404, message = "Not Found")
		 })
	*/
	@Operation(summary = "${RedisController.preferenceCache.name}", description = "${RedisController.preferenceCache.desc}",

			responses = {
					@ApiResponse(responseCode =Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PreferenceCacheResponse.class))) }

					),

					@ApiResponse(responseCode = Constants.API_BAD_REQ_RESPONSE_CODE, description = Constants.API_BAD_REQ_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class))

									, examples = {
									@ExampleObject(value = BaseResponseRedoclyCommon.BAD_REQUEST) }

							) }),
					@ApiResponse(responseCode = Constants.API_UNAUTH_RESPONSE_CODE, description = Constants.API_UNAUTH_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
									@ExampleObject(value = BaseResponseRedoclyCommon.UNAUTH) }

							) }),
					@ApiResponse(responseCode = Constants.API_NOT_FOUND_RESPONSE_CODE, description = Constants.API_NOT_FOUND_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
									@ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND) }

							) }),
					@ApiResponse(responseCode = Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = Constants.API_INTERNAL_ERROR_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
									@ExampleObject(value = BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR) }) }) })
	public PreferenceCacheResponse preferenceCache (
	@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
	HttpServletResponse responseSwag) {
		final String methodName = "preferenceCache";
	     if (log.isDebugEnabled()) {
	         log.debug(methodName, "Entered");
	     }
	     Connection con = null;
	     MComConnectionI mcomCon = null;
	     String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		 String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		 PreferenceCacheResponse response = new PreferenceCacheResponse();
		 try {
			 mcomCon = new MComConnection();
	         con=mcomCon.getConnection();
	         response = redisService.preferenceCache(con, response, responseSwag);
		 }catch (Exception e) {
	            log.error(methodName, "Exceptin:e=" + e);
	            log.errorTrace(methodName, e);
	            response.setStatus(PretupsI.RESPONSE_FAIL);
	    		response.setMessageCode("error.general.processing");
	    		response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");
	        } finally {
	        	if(mcomCon != null)
	        	{
	        		mcomCon.close("RedisControllerr#"+methodName);
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
}
