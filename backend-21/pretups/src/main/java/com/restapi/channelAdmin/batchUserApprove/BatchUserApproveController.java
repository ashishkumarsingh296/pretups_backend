package com.restapi.channelAdmin.batchUserApprove;

import java.io.File;
import java.sql.Connection;
import java.util.Arrays;
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
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OAuthenticationUtil;
import com.restapi.channelAdmin.batchUserApprove.requestVO.BulkUserApproveRejectRequestVO;
import com.restapi.channelAdmin.batchUserApprove.requestVO.BulkUserProcessRequestVO;
import com.restapi.channelAdmin.batchUserApprove.responseVO.BulkUserApproveRejectResponseVO;
import com.restapi.channelAdmin.batchUserApprove.responseVO.BulkUserProcessResponseVO;
import com.restapi.channelAdmin.batchUserApprove.responseVO.LoadBatchListForApprovalResponseVO;
import com.restapi.channelAdmin.batchUserApprove.service.BatchUserApproveServiceI;
import com.restapi.user.service.FileDownloadResponseMulti;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${BatchUserApproveController.name}", description = "${BatchUserApproveController.desc}")//@Api(tags = "Channel Admin", defaultValue = "Channel Admin")
@RestController
@RequestMapping(value = "/v1/channeladmin/batchuserapprove")
public class BatchUserApproveController {

	public static final Log log = LogFactory.getLog(BatchUserApproveController.class.getName());
	public static final String classname = "BatchUserApproveController";
	
	@Autowired
	BatchUserApproveServiceI batchUserApproveServiceI;
	
	
	
	
	@GetMapping(value= "/loadbatchlistforapproval", produces = MediaType.APPLICATION_JSON)	
	@ResponseBody
	/*@ApiOperation(value = "This method load batch listing, based on domainCode and geographyCode",
	           response = LoadBatchListForApprovalResponseVO.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = LoadBatchListForApprovalResponseVO.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })
	*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${loadbatchlistforapproval.summary}", description="${loadbatchlistforapproval.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = LoadBatchListForApprovalResponseVO.class))
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
	public LoadBatchListForApprovalResponseVO loadBatchListForApproval(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest, @RequestParam("domainCode") String domainCode, @RequestParam("geographyCode") String geographyCode) throws Exception {
		final String methodName = "loadBatchListForApproval";
		if(log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		
		Connection con = null;
		MComConnectionI mcomCon = null;
		
		UserVO userVO = null;
		UserDAO userDAO = null;
		
		LoadBatchListForApprovalResponseVO response = new LoadBatchListForApprovalResponseVO();
		
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
			OAuthUser oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, response1);
			
			userVO = new UserVO();
			userDAO = new UserDAO();
			userVO  = userDAO.loadUsersDetails(con, oAuthUser.getData().getMsisdn());
			
			ChannelUserVO channelUserVO = userDAO.loadAllUserDetailsByLoginID(con, oAuthUser.getData().getLoginid());
			
			response = batchUserApproveServiceI.loadBatchListForApproval(headers,response1,con,mcomCon,locale,userVO,channelUserVO,response,domainCode,geographyCode);
		}
		catch (BTSLBaseException be) {
			log.error("", "Exceptin:e=" + be);
			log.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), be.getArgs());
			response.setMessageCode(be.getMessageKey());
			response.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		}
		catch (Exception e) {
			log.error(methodName, "Exception:e=" + e);
			log.errorTrace(methodName, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.LIST_NOT_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.LIST_NOT_FOUND);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		finally {
			if (mcomCon != null) {
				mcomCon.close(methodName);
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting:=" + methodName);
			}
		}
			
		return response;
	}
	
	

	
	
	@GetMapping(value= "/downloadbulkapprovalfile", produces = MediaType.APPLICATION_JSON)	
	@ResponseBody
	/*@ApiOperation(value = "Download bulk file which contains users list for approval",
	           response = FileDownloadResponseMulti.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = FileDownloadResponseMulti.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${downloadbulkapprovalfile.summary}", description="${downloadbulkapprovalfile.description}",

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
	public FileDownloadResponseMulti downloadBulkApprovalFile(
	@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,

	@Parameter(description = "batchID", required = true)
	@RequestParam("batchID") String batchID,
	HttpServletResponse response1
			)throws Exception{

		
		final String methodName =  "downloadBulkApprovalFile";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
	    FileDownloadResponseMulti response=null;
	    response = new FileDownloadResponseMulti();

        Locale locale = null;

        Connection con = null;
		MComConnectionI mcomCon = null;
		
		try
		{
			locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers,response1);
			String loginID = null;
	        try {
	            // getting loggedIn user details
	            loginID = OAuthUserData.getData().getLoginid();
				UserVO userVO = new UserDAO().loadAllUserDetailsByLoginID(con, loginID);				
				  
				batchUserApproveServiceI.downloadBulkApprovalFile(headers,response1,con,mcomCon,locale,userVO,response,batchID);
	        } catch (BTSLBaseException be) {
	        	log.error(methodName, "Exception:e=" + be);
				log.errorTrace(methodName, be);
				if (!BTSLUtil.isNullString(be.getMessage())) {
					String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
					response.setMessageCode(be.getMessage());
					response.setMessage(msg);
					response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
					response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				}
	        } catch (Exception e) {
				log.error(methodName, "Exception:e=" + e);
				log.errorTrace(methodName, e);
				response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
				String resmsg = RestAPIStringParser.getMessage(
						new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
						PretupsErrorCodesI.LIST_NOT_FOUND, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.LIST_NOT_FOUND);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			} finally {
	            if (log.isDebugEnabled()) {
	                log.debug(methodName, " Exited ");
	            }
	        }
		} finally {
			if (mcomCon != null) {
				mcomCon.close(methodName);
				mcomCon = null;
			}
		}
		
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exiting: " );
		}
		return response;
	}
	
	
	
	
	
	
	
	
	
	@PostMapping(value= "/bulkuserapprovereject", produces = MediaType.APPLICATION_JSON)	
	@ResponseBody
	/*@ApiOperation(value = "Approve or Reject users in bulk based on batchAction",
	           response = BulkUserApproveRejectResponseVO.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = BulkUserApproveRejectResponseVO.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${bulkuserapprovereject.summary}", description="${bulkuserapprovereject.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = BulkUserApproveRejectResponseVO.class))
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
	public BulkUserApproveRejectResponseVO bulkUserApproveReject(
	@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,

	@RequestBody BulkUserApproveRejectRequestVO bulkUserApproveRejectRequestVO,
	HttpServletResponse response1
			)throws Exception{
		
		final String methodName =  "bulkUserApproveReject";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		BulkUserApproveRejectResponseVO response=null;
	    response = new BulkUserApproveRejectResponseVO();

        Locale locale = null;

        Connection con = null;
		MComConnectionI mcomCon = null;
		
		try
		{
			locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers,response1);
			String loginID = null;
			
            // getting loggedIn user details
            loginID = OAuthUserData.getData().getLoginid();
			UserVO userVO = new UserDAO().loadAllUserDetailsByLoginID(con, loginID);				
			
            
			batchUserApproveServiceI.bulkUserApproveReject(headers,response1,con,mcomCon,locale,userVO,response,bulkUserApproveRejectRequestVO.getBatchID(),bulkUserApproveRejectRequestVO.getBatchAction());

		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		} 
		catch (Exception e) {
			log.error(methodName, "Exception:e=" + e);
			log.errorTrace(methodName, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.USER_APPROVAL_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.USER_APPROVAL_FAIL);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
        } 
		finally {
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exited ");
            }
        }
		return response;
	}
	
	
	
	
	
	
	
	//****************bulkUserProcess**********//
	@PostMapping(value= "/bulkuserprocess", produces = MediaType.APPLICATION_JSON)	
	@ResponseBody
	/*@ApiOperation(value = "Upload Base64 encoded file for Batch User Approval of Channel Users",
	           response = BulkUserProcessResponseVO.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = BulkUserProcessResponseVO.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${bulkuserprocess.summary}", description="${bulkuserprocess.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = BulkUserProcessResponseVO.class))
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

	public BulkUserProcessResponseVO bulkUserProcess(
	@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,

	@RequestBody BulkUserProcessRequestVO bulkUserProcessRequestVO,
	HttpServletResponse response1
			)throws Exception{
		
		final String methodName =  "bulkUserProcess";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		BulkUserProcessResponseVO response=null;
	    response = new BulkUserProcessResponseVO();

        Locale locale = null;

        Connection con = null;
		MComConnectionI mcomCon = null;
		
		try
		{
			locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers,response1);
			String loginID = null;
			
            // getting loggedIn user details
            loginID = OAuthUserData.getData().getLoginid();
			UserVO userVO = new UserDAO().loadAllUserDetailsByLoginID(con, loginID);				
			
			
			//basic file validation code starts
			// basic file validation code ends
			
			//fileUpload starts
			boolean fileUpload = false;
			fileUpload = batchUserApproveServiceI.uploadAndValidateFile(con, mcomCon, loginID, bulkUserProcessRequestVO,
					response,userVO);
			
			if (fileUpload) {
				String fileStr = Constants.getProperty("UploadBatchUserApproveFilePath");
				fileStr = fileStr + bulkUserProcessRequestVO.getFileName();
				final File f = new File(fileStr);
				final String filePathAndFileName = (fileStr + ".xls");

				response = batchUserApproveServiceI.processUploadedFileForBatchApprove(con,mcomCon, response1,
						bulkUserProcessRequestVO, filePathAndFileName, loginID, locale, userVO, response);

			} else {
				throw new BTSLBaseException(classname, methodName,
						PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFIL_ERROR_TRYLATER);

			}
			//fileUpload ends

		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		} 
		catch (Exception e) {
			log.error(methodName, "Exception:e=" + e);
			log.errorTrace(methodName, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.USER_APPROVAL_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.USER_APPROVAL_FAIL);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
        } 
		finally {
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exited ");
            }
        }
		return response;
	}
	
	
}
