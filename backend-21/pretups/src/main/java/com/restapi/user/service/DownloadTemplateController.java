package com.restapi.user.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Locale;


import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.btsl.util.SwaggerAPIDescriptionI;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

/**
 * @author pankaj.rawat
 * Controller for Download template file using API
 */

@io.swagger.v3.oas.annotations.tags.Tag(name = "${DownloadTemplateController.name}", description = "${DownloadTemplateController.desc}")//@Api(tags= "File Operations", value="C2C Services")
@RestController
@RequestMapping(value = "/v1/c2cFileServices")
public class DownloadTemplateController {

	protected final Log log = LogFactory.getLog(getClass().getName());
	@Autowired
	private DownloadTemplateService downloadTemplateService;
	@GetMapping(value= "/downloadTemplate/{transferType}", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "C2C Batch File Template Download",
			  notes=("Api Info:")+ ("\n") + ("System Prefrence C2C_BATCH_FILEEXT is used to determine the file format."),response = FileDownloadResponse.class,
				  authorizations = {
		    	            @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = FileDownloadResponse.class),
	        @ApiResponse(code = 400, message = "Bad Request"),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${downloadTemplate.summary}", description="${downloadTemplate.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = FileDownloadResponse.class))
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


	public FileDownloadResponse getFileBatchC2C(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = SwaggerAPIDescriptionI.TRANSFER_TYPE, example = "",required = true)
			@PathVariable("transferType") String transferType,
			HttpServletResponse response)throws BTSLBaseException, SQLException, JsonParseException,JsonMappingException, IOException {
		final String methodName = "getFileBatchC2C";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }
        FileDownloadResponse fileDownloadResponse = new FileDownloadResponse();
        try{
        	OAuthenticationUtil.validateTokenApi(headers);
        	if(!("TRANSFER").equalsIgnoreCase(transferType)&&!("WITHDRAW").equalsIgnoreCase(transferType)){
        		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_TRF_TYPE);
        	}
        	downloadTemplateService.downloadC2CBatchTemplate(fileDownloadResponse);
    	   
        }
        catch (BTSLBaseException be) {
          	 log.error(methodName, "Exception:e=" + be);
               log.errorTrace(methodName, be);
               if(be.getMessage().equalsIgnoreCase("1080001")||be.getMessage().equalsIgnoreCase("1080002")||be.getMessage().equalsIgnoreCase("1080003")||
               		 be.getMessage().equalsIgnoreCase("241023")||be.getMessage().equalsIgnoreCase("241018")){
               	 response.setStatus(HttpStatus.SC_UNAUTHORIZED);
               	 fileDownloadResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
               }
                else{
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
                fileDownloadResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
                }
               String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
               String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
       	   String resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), be.getMessage(), null);
       	   fileDownloadResponse.setMessageCode(be.getMessage());
       	   fileDownloadResponse.setMessage(resmsg);
       	   
        	}
        catch (Exception ex) {
        	response.setStatus(HttpStatus.SC_BAD_REQUEST);
			fileDownloadResponse.setStatus(PretupsI.RESPONSE_FAIL);
	        log.errorTrace(methodName, ex);
	        log.error(methodName, "Unable to write data into a file Exception = " + ex.getMessage());
	    }
        return fileDownloadResponse;
	}
	
	
	@GetMapping(value= "/downloadGiftRCTemplate", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Gift Recharge Template Download",
			  notes=("Api Info:")+ ("\n") + ("System Prefrence C2C_BATCH_FILEEXT is used to determine the file format."),response = FileDownloadResponse.class,
				  authorizations = {
		    	            @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = FileDownloadResponse.class),
	        @ApiResponse(code = 400, message = "Bad Request"),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${downloadGiftRCTemplate.summary}", description="${downloadGiftRCTemplate.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = FileDownloadResponse.class))
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


	public FileDownloadResponse getFileGiftRecharge(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response)throws BTSLBaseException, SQLException, JsonParseException,JsonMappingException, IOException {
		final String methodName = "getFileGiftRecharge";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }
        FileDownloadResponse fileDownloadResponse = new FileDownloadResponse();
        try{
        	OAuthenticationUtil.validateTokenApi(headers);
        	
        	downloadTemplateService.downloadGiftTemplate(fileDownloadResponse);
    	   
        } 
        catch (BTSLBaseException be) {
          	 log.error(methodName, "Exception:e=" + be);
               log.errorTrace(methodName, be);
               if(be.getMessage().equalsIgnoreCase("1080001")||be.getMessage().equalsIgnoreCase("1080002")||be.getMessage().equalsIgnoreCase("1080003")||
               		 be.getMessage().equalsIgnoreCase("241023")||be.getMessage().equalsIgnoreCase("241018")){
               	 response.setStatus(HttpStatus.SC_UNAUTHORIZED);
               	 fileDownloadResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
               }
                else{
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
                fileDownloadResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
                }
               String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
               String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
       	   String resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), be.getMessage(), null);
       	   fileDownloadResponse.setMessageCode(be.getMessage());
       	   fileDownloadResponse.setMessage(resmsg);
       	   
   	}
        catch (Exception ex) {
        	response.setStatus(HttpStatus.SC_BAD_REQUEST);
			fileDownloadResponse.setStatus(PretupsI.RESPONSE_FAIL);
	        log.errorTrace(methodName, ex);
	        log.error(methodName, "Unable to write data into a file Exception = " + ex.getMessage());
	    }
        return fileDownloadResponse;
	}
	
	
	
	@GetMapping(value= "/downloadRCTemplate", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Customer Recharge Template Download",
			  notes=("Api Info:")+ ("\n") + ("System Prefrence C2C_BATCH_FILEEXT is used to determine the file format."),response = FileDownloadResponse.class,
				  authorizations = {
		    	            @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = FileDownloadResponse.class),
	        @ApiResponse(code = 400, message = "Bad Request"),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${downloadRCTemplate.summary}", description="${downloadRCTemplate.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = FileDownloadResponse.class))
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


	public FileDownloadResponse getFileRecharge(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response)throws BTSLBaseException, SQLException, JsonParseException,JsonMappingException, IOException {
		final String methodName = "getFileGiftRecharge";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }
        FileDownloadResponse fileDownloadResponse = new FileDownloadResponse();
        try{
        	OAuthenticationUtil.validateTokenApi(headers);
        	
        	downloadTemplateService.downloadRechargeTemplate(fileDownloadResponse);
    	   
        } 
        catch (BTSLBaseException be) {
          	 log.error(methodName, "Exception:e=" + be);
               log.errorTrace(methodName, be);
               if(be.getMessage().equalsIgnoreCase("1080001")||be.getMessage().equalsIgnoreCase("1080002")||be.getMessage().equalsIgnoreCase("1080003")||
               		 be.getMessage().equalsIgnoreCase("241023")||be.getMessage().equalsIgnoreCase("241018")){
               	 response.setStatus(HttpStatus.SC_UNAUTHORIZED);
               	 fileDownloadResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
               }
                else{
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
                fileDownloadResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
                }
               String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
               String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
       	   String resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), be.getMessage(), null);
       	   fileDownloadResponse.setMessageCode(be.getMessage());
       	   fileDownloadResponse.setMessage(resmsg);
       	   
   	}
        catch (Exception ex) {
        	response.setStatus(HttpStatus.SC_BAD_REQUEST);
			fileDownloadResponse.setStatus(PretupsI.RESPONSE_FAIL);
	        log.errorTrace(methodName, ex);
	        log.error(methodName, "Unable to write data into a file Exception = " + ex.getMessage());
	    }
        return fileDownloadResponse;
	}
	
	
	
	
	
	
	
	@GetMapping(value= "/downloadFixedRCTemplate", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Fixed Line Recharge Template Download",
			  notes=("Api Info:")+ ("\n") + ("System Prefrence C2C_BATCH_FILEEXT is used to determine the file format."),response = FileDownloadResponse.class,
				  authorizations = {
		    	            @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = FileDownloadResponse.class),
	        @ApiResponse(code = 400, message = "Bad Request"),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${downloadFixedRCTemplate.summary}", description="${downloadFixedRCTemplate.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = FileDownloadResponse.class))
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


	public FileDownloadResponse getFileFixedLineRecharge(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response)throws BTSLBaseException, SQLException, JsonParseException,JsonMappingException, IOException {
		final String methodName = "getFileGiftRecharge";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }
        FileDownloadResponse fileDownloadResponse = new FileDownloadResponse();
        try{
        	OAuthenticationUtil.validateTokenApi(headers);
        	
        	downloadTemplateService.downloadFixedTemplate(fileDownloadResponse);
    	   
        } 
        catch (BTSLBaseException be) {
          	 log.error(methodName, "Exception:e=" + be);
               log.errorTrace(methodName, be);
               if(be.getMessage().equalsIgnoreCase("1080001")||be.getMessage().equalsIgnoreCase("1080002")||be.getMessage().equalsIgnoreCase("1080003")||
               		 be.getMessage().equalsIgnoreCase("241023")||be.getMessage().equalsIgnoreCase("241018")){
               	 response.setStatus(HttpStatus.SC_UNAUTHORIZED);
               	 fileDownloadResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
               }
                else{
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
                fileDownloadResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
                }
               String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
               String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
       	   String resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), be.getMessage(), null);
       	   fileDownloadResponse.setMessageCode(be.getMessage());
       	   fileDownloadResponse.setMessage(resmsg);
       	   
   	}
        catch (Exception ex) {
        	response.setStatus(HttpStatus.SC_BAD_REQUEST);
			fileDownloadResponse.setStatus(PretupsI.RESPONSE_FAIL);
	        log.errorTrace(methodName, ex);
	        log.error(methodName, "Unable to write data into a file Exception = " + ex.getMessage());
	    }
        return fileDownloadResponse;
	}
	
	
	@GetMapping(value= "/downloadInternetRCTemplate", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Internet Recharge Template Download",
			  notes=("Api Info:")+ ("\n") + ("System Prefrence C2C_BATCH_FILEEXT is used to determine the file format."),response = FileDownloadResponse.class,
				  authorizations = {
		    	            @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = FileDownloadResponse.class),
	        @ApiResponse(code = 400, message = "Bad Request"),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${downloadInternetRCTemplate.summary}", description="${downloadInternetRCTemplate.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = FileDownloadResponse.class))
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


	public FileDownloadResponse getFileInternetRecharge(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response)throws BTSLBaseException, SQLException, JsonParseException,JsonMappingException, IOException {
		final String methodName = "getFileGiftRecharge";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }
        FileDownloadResponse fileDownloadResponse = new FileDownloadResponse();
        try{
        	OAuthenticationUtil.validateTokenApi(headers);
        	
        	downloadTemplateService.downloadInternetTemplate(fileDownloadResponse);
    	   
        } 
        catch (BTSLBaseException be) {
          	 log.error(methodName, "Exception:e=" + be);
               log.errorTrace(methodName, be);
               if(be.getMessage().equalsIgnoreCase("1080001")||be.getMessage().equalsIgnoreCase("1080002")||be.getMessage().equalsIgnoreCase("1080003")||
               		 be.getMessage().equalsIgnoreCase("241023")||be.getMessage().equalsIgnoreCase("241018")){
               	 response.setStatus(HttpStatus.SC_UNAUTHORIZED);
               	 fileDownloadResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
               }
                else{
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
                fileDownloadResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
                }
               String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
               String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
       	   String resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), be.getMessage(), null);
       	   fileDownloadResponse.setMessageCode(be.getMessage());
       	   fileDownloadResponse.setMessage(resmsg);
       	   
   	}
        catch (Exception ex) {
        	response.setStatus(HttpStatus.SC_BAD_REQUEST);
			fileDownloadResponse.setStatus(PretupsI.RESPONSE_FAIL);
	        log.errorTrace(methodName, ex);
	        log.error(methodName, "Unable to write data into a file Exception = " + ex.getMessage());
	    }
        return fileDownloadResponse;
	}
	
	@GetMapping(value= "/downloadDvdMasterSheet", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "DVD Master Sheet Download",
				  authorizations = {
		    	            @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = FileDownloadResponse.class),
	        @ApiResponse(code = 400, message = "Bad Request"),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${downloadDvdMasterSheet.summary}", description="${downloadDvdMasterSheet.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = FileDownloadResponse.class))
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


	public FileDownloadResponse getDvdMasterSheet(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response)throws BTSLBaseException, SQLException, JsonParseException,JsonMappingException, IOException {
		final String methodName = "getDvdMasterSheet";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered"); 
        }
        Connection con = null;
	    MComConnectionI mcomCon = null;
	    UserVO userVO = null;
	    UserDAO userDao = new UserDAO();
        FileDownloadResponse fileDownloadResponse = new FileDownloadResponse();
        try{
        	
        	mcomCon = new MComConnection();
        	con=mcomCon.getConnection();
	        
        	OAuthUser oAuthUserData=new OAuthUser();
	        oAuthUserData.setData(new OAuthUserData());
  
	            
	         OAuthenticationUtil.validateTokenApi(oAuthUserData,headers,response);
	         
	         String loginId =  oAuthUserData.getData().getLoginid();
	         userVO = userDao.loadAllUserDetailsByLoginID(con, loginId);
        	
        	OAuthenticationUtil.validateTokenApi(headers);
        	
        	downloadTemplateService.downloadDvdMasterSheet(con,fileDownloadResponse, userVO.getUserID(), userVO.getNetworkID());
    	   
        } 
        catch (BTSLBaseException be) {
          	 log.error(methodName, "Exception:e=" + be);
          	 
          	 
               log.errorTrace(methodName, be);
               if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
               	 response.setStatus(HttpStatus.SC_UNAUTHORIZED);
               	 fileDownloadResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
               }
                else{
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
                fileDownloadResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
                }
               String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
               String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
       	   String resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), be.getMessage(), null);
       	   fileDownloadResponse.setMessageCode(be.getMessage());
       	   fileDownloadResponse.setMessage(resmsg);
       	   
   	}
        catch (Exception ex) {
        	response.setStatus(HttpStatus.SC_BAD_REQUEST);
			fileDownloadResponse.setStatus(PretupsI.RESPONSE_FAIL);
	        log.errorTrace(methodName, ex);
	        log.error(methodName, "Unable to write data into a file Exception = " + ex.getMessage());
	    }
        finally {

	    	try {
	        	if (mcomCon != null) {
					mcomCon.close("DownloadTemplateController#"+methodName);
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
        return fileDownloadResponse;
	}
	
	@GetMapping(value= "/downloadDvdTemplate", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Dvd Template Download",
			  notes=("Api Info:")+ ("\n") + ("System Prefrence C2C_BATCH_FILEEXT is used to determine the file format."),response = FileDownloadResponse.class,
				  authorizations = {
		    	            @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = FileDownloadResponse.class),
	        @ApiResponse(code = 400, message = "Bad Request"),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${downloadDvdTemplate.summary}", description="${downloadDvdTemplate.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = FileDownloadResponse.class))
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


	public FileDownloadResponse getFileDvd(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response)throws BTSLBaseException, SQLException, JsonParseException,JsonMappingException, IOException {
		final String methodName = "getFileGiftRecharge";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }
        FileDownloadResponse fileDownloadResponse = new FileDownloadResponse();
        try{
        	OAuthenticationUtil.validateTokenApi(headers);
        	
        	downloadTemplateService.downloadDvdTemplate(fileDownloadResponse);
    	   
        } 
        catch (BTSLBaseException be) {
          	 log.error(methodName, "Exception:e=" + be);
               log.errorTrace(methodName, be);
               if(be.getMessage().equalsIgnoreCase("1080001")||be.getMessage().equalsIgnoreCase("1080002")||be.getMessage().equalsIgnoreCase("1080003")||
               		 be.getMessage().equalsIgnoreCase("241023")||be.getMessage().equalsIgnoreCase("241018")){
               	 response.setStatus(HttpStatus.SC_UNAUTHORIZED);
               	 fileDownloadResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
               }
                else{
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
                fileDownloadResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
                }
               String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
               String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
       	   String resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), be.getMessage(), null);
       	   fileDownloadResponse.setMessageCode(be.getMessage());
       	   fileDownloadResponse.setMessage(resmsg);
       	   
   	}
        catch (Exception ex) {
        	response.setStatus(HttpStatus.SC_BAD_REQUEST);
			fileDownloadResponse.setStatus(PretupsI.RESPONSE_FAIL);
	        log.errorTrace(methodName, ex);
	        log.error(methodName, "Unable to write data into a file Exception = " + ex.getMessage());
	    }
        return fileDownloadResponse;
	}
	
	
	
	@GetMapping(value= "/downloadO2cPurchaseOrWithdrawTemplate", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "O2C Withdraw/Purchase template",
				  authorizations = {
		    	            @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = FileDownloadResponse.class),
	        @ApiResponse(code = 400, message = "Bad Request"),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${downloadO2cPurchaseOrWithdrawTemplate.summary}", description="${downloadO2cPurchaseOrWithdrawTemplate.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = FileDownloadResponse.class))
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


	public FileDownloadResponse getO2cWithdrawOrPuchaseFileTemplate(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = "Purchase Or Withdraw", required = true)// allowableValues = "P,W")
			@RequestParam("purchaseOrWithdraw") String purchaseOrWithdraw,
			HttpServletResponse response)throws BTSLBaseException, SQLException, JsonParseException,JsonMappingException, IOException {
		final String methodName = "getO2cWithdrawFileTemplate";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered"); 
        }
        Connection con = null;
	    MComConnectionI mcomCon = null;
	    UserVO userVO = null;
	    UserDAO userDao = new UserDAO();
        FileDownloadResponse fileDownloadResponse = new FileDownloadResponse();
        try{
        	
        	/*mcomCon = new MComConnection();
        	con=mcomCon.getConnection();*/
	        
        	OAuthUser oAuthUserData=new OAuthUser();
	        oAuthUserData.setData(new OAuthUserData());
  
	            
	         OAuthenticationUtil.validateTokenApi(oAuthUserData,headers,response);
	         
	         /*String loginId =  oAuthUserData.getData().getLoginid();
	         userVO = userDao.loadAllUserDetailsByLoginID(con, loginId);*/
        	
        	OAuthenticationUtil.validateTokenApi(headers);
        	if("W".equalsIgnoreCase(purchaseOrWithdraw)) 
        	{
        		downloadTemplateService.downloadO2CWithdrawTemplate(fileDownloadResponse);
        	} else if ("P".equalsIgnoreCase(purchaseOrWithdraw)) 
        	{
        		downloadTemplateService.downloadO2CPurchaseTemplate(fileDownloadResponse);
			} else {
				String[] args = {purchaseOrWithdraw};
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_PURCH_OR_WITHD_PARAM, args);
			}
        	
    	   
        } 
        catch (BTSLBaseException be) {
          	 log.error(methodName, "Exception:e=" + be);
          	 
          	 
               log.errorTrace(methodName, be);
               if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
               	 response.setStatus(HttpStatus.SC_UNAUTHORIZED);
               	 fileDownloadResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
               }
                else{
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
                fileDownloadResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
                }
               String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
               String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
       	   String resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), be.getMessage(), be.getArgs());
       	   fileDownloadResponse.setMessageCode(be.getMessage());
       	   fileDownloadResponse.setMessage(resmsg);
       	   
   	}
        catch (Exception ex) {
        	response.setStatus(HttpStatus.SC_BAD_REQUEST);
			fileDownloadResponse.setStatus(PretupsI.RESPONSE_FAIL);
	        log.errorTrace(methodName, ex);
	        log.error(methodName, "Unable to write data into a file Exception = " + ex.getMessage());
	    }
        finally {

	    	try {
	        	if (mcomCon != null) {
					mcomCon.close("DownloadTemplateController#"+methodName);
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
        return fileDownloadResponse;
	}
	
	
}
