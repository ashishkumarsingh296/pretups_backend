package com.restapi.superadmin;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;


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
import com.btsl.common.ErrorMap;
import com.btsl.common.MasterErrorList;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.restapi.superadmin.requestVO.BatchOperatorUserInitiateRequestVO;
import com.restapi.superadmin.responseVO.BatchOperatorUserInitiateResponseVO;
import com.restapi.superadmin.service.BatchOperatorUserInitiateServiceImpl;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${BatchOperatorUserInitiateController.name}", description = "${BatchOperatorUserInitiateController.desc}")//@Api(tags="Super Admin")
@RestController
@RequestMapping(value = "/v1/superadmin")

public class BatchOperatorUserInitiateController {
	public static final Log log = LogFactory.getLog(BatchOperatorUserInitiateController.class.getName());
	
	@Autowired
	BatchOperatorUserInitiateServiceImpl BatchOperatorUserInitiateServiceI;
	
	@GetMapping(value= "/downloadBatchOperatorUserInitiateTemplate", produces = MediaType.APPLICATION_JSON)	
	@ResponseBody
	/*@ApiOperation(value = "Download Operator Users Initiate File Template",
	           response = BatchOperatorUserInitiateResponseVO.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = BatchOperatorUserInitiateResponseVO.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })
	*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${downloadBatchOperatorUserInitiateTemplate.summary}", description="${downloadBatchOperatorUserInitiateTemplate.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = BatchOperatorUserInitiateResponseVO.class))
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


	public BatchOperatorUserInitiateResponseVO downloadBatchOperatorUserInitiateTemplate(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = "categoryCode", required = true)// allowableValues = "SSADM, NWADM, SUNADM,	SUCCE, SUBCU, MONTR, CCE, BCU")
			@RequestParam("categoryCode") String categoryType,
			HttpServletResponse responseSwag
			) throws Exception{
		
		final String methodName =  "downloadBatchOperatorUserInitiateTemplate";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		BatchOperatorUserInitiateResponseVO response = null;
		response = new BatchOperatorUserInitiateResponseVO();
		
		UserDAO userDao = new UserDAO();
        Locale locale = null;

        Connection con = null;
		MComConnectionI mcomCon = null;
		
		OAuthUser oAuthUser= null;
		OAuthUserData oAuthUserData =null;
		
		try {
			locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
			oAuthUser = new OAuthUser();
			oAuthUserData =new OAuthUserData();
			oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers,responseSwag);			

			ChannelUserVO userVO = userDao.loadAllUserDetailsByLoginID( con, oAuthUser.getData().getLoginid() );
			
			BatchOperatorUserInitiateServiceI.downloadFileTemplate(con, mcomCon, locale, categoryType, userVO, response, responseSwag);

			} catch (BTSLBaseException be) {
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
	     			 resmsg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), be.getArgs());
	            	
	             }else {
	            	 resmsg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), be.getArgs());
	
	             }
	     	   response.setMessageCode(be.getMessage());
	     	   response.setMessage(resmsg);
		    	} catch (Exception e) {
		            log.error(methodName, "Exceptin:e=" + e);
		            log.errorTrace(methodName, e);
		            String fail=Integer.toString(PretupsI.RESPONSE_FAIL) ;
		            response.setStatus(fail);
		    		response.setMessageCode("error.general.processing");
		    		response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");
		        } finally {
					if (mcomCon != null) {
						mcomCon.close("");
						mcomCon = null;
					}
		        }
		
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exiting: " );
		}
	
		return response;
	}
	
	@PostMapping(value = "/uploadBatchOperatorUserInitiate", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Upload Base64 encoded file for Batch User Initiation of Operator Users",
					response = BatchOperatorUserInitiateResponseVO.class,
					authorizations = {
					@Authorization(value = "Authorization") })

	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = BatchOperatorUserInitiateResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"), 
			@ApiResponse(code = 404, message = "Not Found") })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${uploadBatchOperatorUserInitiate.summary}", description="${uploadBatchOperatorUserInitiate.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = BatchOperatorUserInitiateResponseVO.class))
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


	public BatchOperatorUserInitiateResponseVO uploadBatchOperatorUserInitiateFile(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,

			@Parameter(description = "categoryCode", required = true)//allowableValues = "SSADM, NWADM, SUNADM,	SUCCE, SUBCU, MONTR, CCE, BCU")
			@RequestParam("categoryCode") String categoryType,
			@RequestParam("batchName") String batchName,
			@RequestBody BatchOperatorUserInitiateRequestVO batchOperatorUserInitiateRequestVO,
			 HttpServletResponse responseSwag) {
		
		final String methodName = "uploadBatchOperatorUserInitiateFile";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		BatchOperatorUserInitiateRequestVO request = batchOperatorUserInitiateRequestVO;
		BatchOperatorUserInitiateResponseVO response = null;
		
		ArrayList<MasterErrorList> inputValidations = new ArrayList<>();
		 
        Locale locale = null;
        Connection con = null;
		MComConnectionI mcomCon = null;
		
		OAuthUser oAuthUser= null;
		OAuthUserData oAuthUserData =null;
		
		UserDAO userDao = new UserDAO();
		
		try
		{
			locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
			
			response = new BatchOperatorUserInitiateResponseVO();
			response.setFileAttachment(request.getFileAttachment());
			
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
			oAuthUser = new OAuthUser();
			oAuthUserData =new OAuthUserData();
			oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers,responseSwag);
			request.setFileName(batchName.trim() + PretupsI.PREFIX_UNDERSCORE+request.getFileName()); // changing the filename , as per requirement
			response.setFileName(request.getFileName());
			response.setFileType(request.getFileType());
			
			ArrayList<MasterErrorList> inputVal = BatchOperatorUserInitiateServiceI.basicFileValidations(request, response, categoryType, locale, inputValidations);
						 
			if(!BTSLUtil.isNullOrEmptyList(inputVal)) {
				response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
				response.setErrorMap(new ErrorMap());
				response.getErrorMap().setMasterErrorList(inputVal);
				response.setMessage(RestAPIStringParser.getMessage(locale,PretupsI.LIST_OF_FILE_VALIDATION_ERRORS, null));
			}
			
			else {
				ChannelUserVO userVO = userDao.loadAllUserDetailsByLoginID(con, oAuthUser.getData().getLoginid());
				boolean fileUpload = false;
				fileUpload = BatchOperatorUserInitiateServiceI.uploadAndValidateFile(con, mcomCon, userVO, request, response);
				if (fileUpload) {
					log.debug(methodName, "file uploaded successfully");
					BatchOperatorUserInitiateServiceI.processUploadedFile(con, mcomCon, userVO, categoryType, request, response, responseSwag);
				}
				else {
                    throw new BTSLBaseException(this, methodName, PretupsI.BULK_USER_FILE_NOT_UPLOADED);
                }	
			}
			

		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);
		    if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				response.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
			if(response.getMessage() == null) {
				String resmsg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(resmsg);
			}
		} catch (Exception e) {
			log.errorTrace(methodName, e);
			log.error(methodName, "Unable to write data into a file Exception = " + e.getMessage());
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
		} finally {
			try {
            	if(mcomCon != null){
            		mcomCon.partialRollback();
            	}
            } catch (Exception ee) {
                log.errorTrace(methodName, ee);
            }
			if (mcomCon != null) {
				mcomCon.close("");
				mcomCon = null;
			}
		}
		
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exiting: " );
		}
		return response;
	}

}
