package com.btsl.pretups.channel.transfer.requesthandler;
import java.io.IOException;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;


import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.DefaultValue;
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
import com.btsl.common.BaseResponseMultiple;
import com.btsl.common.ErrorMap;
import com.btsl.common.MasterErrorList;
import com.btsl.common.RowErrorMsgLists;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.transfer.businesslogic.errorfilerequest.ErrorFileRequestVO;
import com.btsl.pretups.transfer.businesslogic.errorfileresponse.ErrorFileResponse;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SqlParameterEncoder;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.restapi.c2sservices.service.ReadGenericFileUtil;
import com.restapi.user.service.FileDownloadResponse;
import com.restapi.user.service.FileDownloadResponseMulti;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;


@io.swagger.v3.oas.annotations.tags.Tag(name = "${DownloadUserListController.name}", description = "${DownloadUserListController.desc}")//@Api(tags= "File Operations", value="C2C Services")
@RestController
@RequestMapping(value = "/v1/c2cFileServices")
public class DownloadUserListController {
	protected final Log _log = LogFactory.getLog(getClass().getName());
	 StringBuilder loggerValue= new StringBuilder(); 


public static final Log log = LogFactory.getLog(DownloadUserListController.class.getName());	
	
@Autowired
private DownloadUserListService downloadUserListService;

@GetMapping(value= "/downloadUsersList", produces = MediaType.APPLICATION_JSON)	
@ResponseBody
/*
@ApiOperation(value = "Download Users List API",
           notes=("Api Info:") + ("\n") + ("TransferType = W for withdraw or T for Transfer .")+ ("\n") + ("System Prefrence C2C_BATCH_FILEEXT is used to determine the file format."),response = FileDownloadResponseMulti.class,
           authorizations = {
               @Authorization(value = "Authorization")})
@ApiResponses(value = {
      @ApiResponse(code = 200, message = "OK", response = FileDownloadResponseMulti.class),
      @ApiResponse(code = 400, message = "Bad Request" ),
      @ApiResponse(code = 401, message = "Unauthorized"),
      @ApiResponse(code = 404, message = "Not Found")
      })
*/

@io.swagger.v3.oas.annotations.Operation(summary = "${downloadUsersList.summary}", description="${downloadUsersList.description}",

		responses = {
				@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
						@io.swagger.v3.oas.annotations.media.Content(
								mediaType = "application/json",
								array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = FileDownloadResponseMulti.class))
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




public FileDownloadResponseMulti downloadUsersList(
@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,


@Parameter(description = "Operation Type", required = true)//allowableValues = "T,W")
@RequestParam("operationType") String operationType,
@Parameter(description = "Category", required = true)//allowableValues = "Super Distributor,Dealer,Agent,Retailer")
@RequestParam("category") String userCategoryName,
 HttpServletResponse responseSwag
		)throws IOException, SQLException, BTSLBaseException{

	
	final String methodName =  "downloadUsersList";
	if (log.isDebugEnabled()) {
		log.debug(methodName, "Entered ");
	}
	
	
    FileDownloadResponseMulti response=null;
    response = new FileDownloadResponseMulti();
 
	 String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
     String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
     MasterErrorList masterError=null;
    
     ErrorMap errorMap = new ErrorMap();
    try {
    	
    	userCategoryName = URLDecoder.decode(userCategoryName, "UTF-8");
    	userCategoryName =SqlParameterEncoder.encodeParams(userCategoryName);
    	operationType=SqlParameterEncoder.encodeParams(operationType);
		
        response.setService("c2cFileServices");
        response.setReferenceId(1986);
		

		/*
		 * Authentication
		 * @throws BTSLBaseException
		 */
	//	OAuthenticationUtil.validateToken(headers);
		
        OAuthUser oAuthUserData=new OAuthUser();
        oAuthUserData.setData(new OAuthUserData());
        OAuthenticationUtil.validateTokenApi(oAuthUserData,headers,new BaseResponseMultiple());
        
      String loginId =  oAuthUserData.getData().getLoginid();
		
		//basic form validation at api level
		
		ArrayList<String> errorList=new ArrayList();
		ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
		 Locale locale= new Locale(lang,country);
		 String pattern= "^[a-zA-Z]*$";
		 String numericPattern = "[0-9]+";
		 
		 if(BTSLUtil.isNullString(userCategoryName)) {
			 masterError = new MasterErrorList();
			 masterError.setErrorCode(PretupsErrorCodesI.BLANK_APLHA_CAT);
			 String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BLANK_APLHA_CAT,null);
			 masterError.setErrorMsg(msg);
			 masterErrorLists.add(masterError);
			}
		 
		 String noSpaceStr = userCategoryName.replaceAll("\\s", ""); // using built in method just to check for aplhanumeric  
		 if(!BTSLUtil.isNullString(noSpaceStr) && noSpaceStr.contains("%20")) {
			 noSpaceStr = noSpaceStr.replaceAll("%20", "");
		 }
		 if(!noSpaceStr.matches(pattern)){
			
			 masterError = new MasterErrorList();
			 masterError.setErrorCode(PretupsErrorCodesI.BLANK_APLHA_CAT);
			 String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BLANK_APLHA_CAT,null);
			 masterError.setErrorMsg(msg);
			 masterErrorLists.add(masterError);
			}
		 
		 if(BTSLUtil.isNullString(operationType)) {
			 
			 masterError = new MasterErrorList();
			 masterError.setErrorCode(PretupsErrorCodesI.BLANK_APLHA_OPERATIONTYPE);
			 String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BLANK_APLHA_OPERATIONTYPE,null);
			 masterError.setErrorMsg(msg);
			 masterErrorLists.add(masterError);

			}
		 if(!operationType.matches(pattern)){
			 
			 masterError = new MasterErrorList();
			 masterError.setErrorCode(PretupsErrorCodesI.BLANK_APLHA_OPERATIONTYPE);
			 String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BLANK_APLHA_OPERATIONTYPE,null);
			 masterError.setErrorMsg(msg);
			 masterErrorLists.add(masterError);

			}
		
		
	    		
		
		errorMap.setMasterErrorList(masterErrorLists);
		
		 if(errorMap.getMasterErrorList().size() >=1) {
			
			response.setErrorMap(errorMap);
			response.setMessageCode("MULTI_VALIDATION_ERROR");
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			String badReq=Integer.toString(HttpStatus.SC_BAD_REQUEST) ;
	         response.setStatus(badReq);

	    	
	        
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, " Exited ");
	        }
	    
			return response;
		 }
		 
		
		 userCategoryName = userCategoryName.replaceAll("%20", " ");
		 downloadUserListService.downloadC2CBatch(loginId, userCategoryName, operationType, locale, response);
		
		
		

    }  catch (BTSLBaseException be) {
      	 log.error(methodName, "Exception:e=" + be);
         log.errorTrace(methodName, be);
         if(be.getMessage().equalsIgnoreCase("1080001")||be.getMessage().equalsIgnoreCase("1080002")||be.getMessage().equalsIgnoreCase("1080003")||
         		 be.getMessage().equalsIgnoreCase("241023")||be.getMessage().equalsIgnoreCase("241018")){
        	 String unauthorised=Integer.toString(HttpStatus.SC_UNAUTHORIZED) ;
        	response.setStatus(unauthorised);
         	responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
         	
         	 
         }
          else{
          String badReq=Integer.toString(HttpStatus.SC_BAD_REQUEST) ;
          response.setStatus(badReq);
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
        String fail=Integer.toString(PretupsI.RESPONSE_FAIL) ;
        response.setStatus(fail);
		response.setMessageCode("error.general.processing");
		response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");
    } finally {
        if (log.isDebugEnabled()) {
            log.debug(methodName, " Exited ");
        }
    }
	return response;

}



@GetMapping(value= "/downloadRCUserList", produces = MediaType.APPLICATION_JSON)
@ResponseBody
/*@ApiOperation(value = "Customer Recharge Restricted User List Download",
		  notes=("Api Info:")+ ("\n") + ("System Prefrence C2C_BATCH_FILEEXT is used to determine the file format."),response = FileDownloadResponse.class,
			  authorizations = {
	    	            @Authorization(value = "Authorization")})
@ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK", response = FileDownloadResponse.class),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 404, message = "Not Found")
        })*/

@io.swagger.v3.oas.annotations.Operation(summary = "${downloadRCUserList.summary}", description="${downloadRCUserList.description}",

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



public FileDownloadResponse getCustomerRechargeUserListFile(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
		HttpServletResponse response)throws BTSLBaseException, SQLException, JsonParseException,JsonMappingException, IOException {
	final String methodName = "getCustomerRechargeUserListFile";
    if (log.isDebugEnabled()) {
        log.debug(methodName, "Entered");
    }
    FileDownloadResponse fileDownloadResponse = new FileDownloadResponse();
    try{
    	//OAuthenticationUtil.validateToken(headers);
    	String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
        Locale locale = new Locale(lang, country);
    	OAuthUser oAuthUserData=new OAuthUser();
        oAuthUserData.setData(new OAuthUserData());
        OAuthenticationUtil.validateTokenApi(oAuthUserData, headers, new BaseResponseMultiple());
    	String loginId =  oAuthUserData.getData().getLoginid();
    	downloadUserListService.downloadCustomerRechargeList(loginId, locale, fileDownloadResponse);
	   
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


@GetMapping(value= "/downloadInternetRCUserList", produces = MediaType.APPLICATION_JSON)
@ResponseBody
/*@ApiOperation(value = "Internet Recharge Restricted User List Download",
		  notes=("Api Info:")+ ("\n") + ("System Prefrence C2C_BATCH_FILEEXT is used to determine the file format."),response = FileDownloadResponse.class,
			  authorizations = {
	    	            @Authorization(value = "Authorization")})
@ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK", response = FileDownloadResponse.class),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 404, message = "Not Found")
        })*/

@io.swagger.v3.oas.annotations.Operation(summary = "${downloadInternetRCUserList.summary}", description="${downloadInternetRCUserList.description}",

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



public FileDownloadResponse getInternetRechargeUserListFile(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response) throws BTSLBaseException,
			SQLException, JsonParseException, JsonMappingException, IOException {
		final String methodName = "getInternetRechargeUserListFile";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered");
		}
		FileDownloadResponse fileDownloadResponse = new FileDownloadResponse();
		try {
			// OAuthenticationUtil.validateToken(headers);
			String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
			String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
			Locale locale = new Locale(lang, country);
			OAuthUser oAuthUserData = new OAuthUser();
			oAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(oAuthUserData, headers,
					new BaseResponseMultiple());
			String loginId = oAuthUserData.getData().getLoginid();
			downloadUserListService.downloadCustomerRechargeList(loginId,
					locale, fileDownloadResponse);

		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);
			if (be.getMessage().equalsIgnoreCase("1080001")
					|| be.getMessage().equalsIgnoreCase("1080002")
					|| be.getMessage().equalsIgnoreCase("1080003")
					|| be.getMessage().equalsIgnoreCase("241023")
					|| be.getMessage().equalsIgnoreCase("241018")) {
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
				fileDownloadResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				fileDownloadResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
			String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
			String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
			String resmsg = RestAPIStringParser.getMessage(new Locale(lang,country), be.getMessage(), null);
			fileDownloadResponse.setMessageCode(be.getMessage());
			fileDownloadResponse.setMessage(resmsg);

		} catch (Exception ex) {
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			fileDownloadResponse.setStatus(PretupsI.RESPONSE_FAIL);
			log.errorTrace(methodName, ex);
			log.error(
					methodName,
					"Unable to write data into a file Exception = "
							+ ex.getMessage());
		}
		return fileDownloadResponse;
	}



@GetMapping(value= "/downloadO2cPuchaseOrWithdrawUserList", produces = MediaType.APPLICATION_JSON)
@ResponseBody
/*@ApiOperation(value = "Internet Recharge Restricted User List Download",
		  notes=("Api Info:")+ ("\n") + ("1. System Prefrence C2C_BATCH_FILEEXT is used to determine the file format."),response = FileDownloadResponseMulti.class,
			  authorizations = {
	    	            @Authorization(value = "Authorization")})
@ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK", response = FileDownloadResponseMulti.class),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 404, message = "Not Found")
        })*/

@io.swagger.v3.oas.annotations.Operation(summary = "${downloadO2cPuchaseOrWithdrawUserList.summary}", description="${downloadO2cPuchaseOrWithdrawUserList.description}",

		responses = {
				@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
						@io.swagger.v3.oas.annotations.media.Content(
								mediaType = "application/json",
								array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = FileDownloadResponseMulti.class))
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



public FileDownloadResponseMulti getO2cPuchaseOrWithdrawUseList(
		@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
		@Parameter(description = SwaggerAPIDescriptionI.CHNL_USER_TYPE, required = true)// allowableValues = "P,W")
		@RequestParam("purchaseOrWithdraw") String purchaseOrWithdraw,

		@Parameter(description = "geoDomainCode", example ="", required = true)
		@DefaultValue("ALL")@RequestParam("geoDomainCode") String geoDomainCode,
		@Parameter(description = "domainCode", example ="", required = true)
		@DefaultValue("DIST")@RequestParam("domainCode") String domainCode,
		@Parameter(description = "categoryCode", example ="", required = true)
		@DefaultValue("DIST")@RequestParam("categoryCode") String categoryCode,
		@Parameter(description = "productCode", example ="", required = true)
		@DefaultValue("ETOPUP")@RequestParam("productCode") String productCode,
		@Parameter(description = "walletType", example ="", required = false)
		@RequestParam("walletType") Optional<String> walletTypeOpt,
			HttpServletResponse response) throws BTSLBaseException,
			SQLException, JsonParseException, JsonMappingException, IOException {
		final String methodName = "getInternetRechargeUserListFile";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered");
		}
		FileDownloadResponseMulti fileDownloadResponse = new FileDownloadResponseMulti();
		try {
			String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
			String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
			Locale locale = new Locale(lang, country);
			OAuthUser oAuthUserData = new OAuthUser();
			oAuthUserData.setData(new OAuthUserData());
			//validateToken
			OAuthenticationUtil.validateTokenApi(oAuthUserData, headers,new BaseResponseMultiple());
			
			//validating request
			MasterErrorList masterErrorListObj = null;
			ArrayList<MasterErrorList> masterErrorList = new ArrayList<MasterErrorList>();
			boolean isValidReq = true;
			if(BTSLUtil.isNullorEmpty(geoDomainCode)) 
			{
				masterErrorListObj = new MasterErrorList();
				masterErrorListObj.setErrorCode(PretupsErrorCodesI.NULL_VALUE_IN_REQ);
				String[] args = {"geoDomainCode"};
				masterErrorListObj.setErrorMsg( RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.NULL_VALUE_IN_REQ, args));
				masterErrorList.add(masterErrorListObj);
				isValidReq = false;
			}
			if(BTSLUtil.isNullorEmpty(domainCode)) 
			{
				masterErrorListObj = new MasterErrorList();
				masterErrorListObj.setErrorCode(PretupsErrorCodesI.NULL_VALUE_IN_REQ);
				String[] args = {"domainCode"};
				masterErrorListObj.setErrorMsg( RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.NULL_VALUE_IN_REQ, args));
				masterErrorList.add(masterErrorListObj);
				isValidReq = false;
			}
			if(BTSLUtil.isNullorEmpty(categoryCode)) 
			{
				masterErrorListObj = new MasterErrorList();
				masterErrorListObj.setErrorCode(PretupsErrorCodesI.NULL_VALUE_IN_REQ);
				String[] args = {"categoryCode"};
				masterErrorListObj.setErrorMsg( RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.NULL_VALUE_IN_REQ, args));
				masterErrorList.add(masterErrorListObj);
				isValidReq = false;
			}
			if(BTSLUtil.isNullorEmpty(productCode))
			{
				masterErrorListObj = new MasterErrorList();
				masterErrorListObj.setErrorCode(PretupsErrorCodesI.NULL_VALUE_IN_REQ);
				String[] args = {"productCode"};
				masterErrorListObj.setErrorMsg( RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.NULL_VALUE_IN_REQ, args));
				masterErrorList.add(masterErrorListObj);
				isValidReq = false;
			}
			if(!isValidReq) {
				ErrorMap errorMap = new ErrorMap();
				errorMap.setMasterErrorList(masterErrorList);
				fileDownloadResponse.setErrorMap(errorMap);
				fileDownloadResponse.setStatus(String.valueOf(PretupsI.RESPONSE_FAIL));
				return fileDownloadResponse;
			}
			
			String loginId = oAuthUserData.getData().getLoginid();
			HashMap<String, String> requestMap= new HashMap<String, String>();
			requestMap.put("geoDomain", geoDomainCode);
			requestMap.put("domain", domainCode);
			requestMap.put("category", categoryCode);
			requestMap.put("product", productCode);
			if("W".equalsIgnoreCase(purchaseOrWithdraw)) 
        	{   
				String walletType = SqlParameterEncoder.encodeParams(walletTypeOpt.map(Object::toString).orElse(null));
				requestMap.put("walletType", walletType);
				downloadUserListService.downloadO2CWithdrawUserList(loginId, locale, fileDownloadResponse, requestMap);
        	} 
			else if ("P".equalsIgnoreCase(purchaseOrWithdraw)) 
        	{
        		downloadUserListService.downloadO2CPurchaseUserList(loginId, locale, fileDownloadResponse, requestMap);
			} else 
			{
				String[] args = {purchaseOrWithdraw};
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_PURCH_OR_WITHD_PARAM, args);
			}

		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);
			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
				fileDownloadResponse.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
			} else {
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				fileDownloadResponse.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			}
			final Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
			String resmsg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
			fileDownloadResponse.setMessageCode(be.getMessage());
			fileDownloadResponse.setMessage(resmsg);

		} catch (Exception ex) {
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			final Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_APPROVAL_WENT_WRONG, null);
			fileDownloadResponse.setStatus(String.valueOf(PretupsI.RESPONSE_FAIL));
			fileDownloadResponse.setMessageCode(PretupsErrorCodesI.O2C_APPROVAL_WENT_WRONG);
			fileDownloadResponse.setMessage(resmsg);
			log.errorTrace(methodName, ex);
			log.error(methodName, "Unable to write data into a file Exception = "+ ex.getMessage());
		}
		return fileDownloadResponse;
	}

	@PostMapping(value= "/bulkerrorfile", produces = MediaType.APPLICATION_JSON)	
	/*@ApiOperation(value = "Append Error in Excel File",
           response = ErrorFileResponse.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = ErrorFileResponse.class),
			@ApiResponse(code = 400, message = "Bad Request" ),
			@ApiResponse(code = 404, message = "Not Found")
      	})*/


	@io.swagger.v3.oas.annotations.Operation(summary = "${bulkerrorfile.summary}", description="${bulkerrorfile.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ErrorFileResponse.class))
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



	public ErrorFileResponse getErrorFile(@RequestBody ErrorFileRequestVO errorFileRequestVO,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse responseSwag) {
	
			final String methodName =  "getErrorFile";
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Entered ");
			}
			
			ErrorFileResponse response = null;
			ReadGenericFileUtil fileUtil =null;
			Locale locale =null;
			try {
			    response = new ErrorFileResponse();
				fileUtil =  new ReadGenericFileUtil();
				locale= new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));

				String fileType= errorFileRequestVO.getFiletype();
				fileUtil.validateFileType(fileType);			
				
				String file=errorFileRequestVO.getFile();
				if (BTSLUtil.isNullorEmpty(file)) {
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EMPTY_FILE,
							 PretupsI.RESPONSE_FAIL,null);
				}
				
				List<RowErrorMsgLists> errors = errorFileRequestVO.getRowErrorMsgLists();
				if(errors.size()==0) {
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EMPTY_ROW_ERRORLIST,
							 PretupsI.RESPONSE_FAIL,null);
				}
				
			    downloadUserListService.downloadErrorFile(errorFileRequestVO, response, responseSwag);
			 
			}
			
			catch(BTSLBaseException be) {
				  log.error(methodName, "Exceptin:e=" + be);
			      log.errorTrace(methodName, be);
		       	  String msg=RestAPIStringParser.getMessage(locale, be.getMessageKey(),null);
			      response.setMessageCode(be.getMessageKey());
			      response.setMessage(msg);
		          responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		          response.setStatus("400");
		        
			} catch (Exception e) {
				log.error(methodName, "Exceptin:e=" + e);
		        log.errorTrace(methodName, e);
		        String fail=Integer.toString(PretupsI.RESPONSE_FAIL) ;
		        response.setStatus(fail);
				response.setMessageCode("error.general.processing");
				response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");
				
				responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
			}
			
			return response;
	}
}
	






 




