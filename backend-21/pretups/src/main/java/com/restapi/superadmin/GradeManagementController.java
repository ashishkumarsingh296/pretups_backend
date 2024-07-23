package com.restapi.superadmin;

import java.sql.Connection;
import java.util.Locale;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.OAuthenticationUtil;
import com.restapi.superadmin.responseVO.GradeTypeListResponseVO;
import com.restapi.superadmin.serviceI.GradeManagementServiceI;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${GradeManagementController.name}", description = "${GradeManagementController.desc}")//@Api(tags = "Super Admin")
@RestController
@RequestMapping(value = "/v1/superadmin")
public class GradeManagementController {
	
	public static final Log log = LogFactory.getLog(GradeManagementController.class.getName());
	public static final String classname = "GradeManagementController";
	
	@Autowired
	GradeManagementServiceI GradeManagementServiceI;
	
	@GetMapping(value= "/getGradeTypeList", produces = MediaType.APPLICATION_JSON)	
	@ResponseBody
	/*@ApiOperation(value = "Get Category and Domain List for Grade Management",
	           response = GradeTypeListResponseVO.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = GradeTypeListResponseVO.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${getGradeTypeList.summary}", description="${getGradeTypeList.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = GradeTypeListResponseVO.class))
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


	public GradeTypeListResponseVO getGradeTypeList(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = "reqType", required = true) @RequestParam("reqType") String reqType,
			HttpServletResponse responseSwag) throws Exception{
		
		final String methodName =  "getGradeTypeList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		GradeTypeListResponseVO response =null;
		response = new GradeTypeListResponseVO();
		
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
			
			response = GradeManagementServiceI.getGradeTypeList(con, mcomCon, locale, responseSwag,reqType);
		} catch (BTSLBaseException be) {
	       	 log.error(methodName, "Exception:e=" + be);
	         log.errorTrace(methodName, be);
	         if(be.getMessage().equalsIgnoreCase("1080001")||be.getMessage().equalsIgnoreCase("1080002")||be.getMessage().equalsIgnoreCase("1080003")||
	         		 be.getMessage().equalsIgnoreCase("241023")||be.getMessage().equalsIgnoreCase("241018")){
	        	response.setStatus(HttpStatus.SC_UNAUTHORIZED);
	         	responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
	         	
	         	 
	         }
	          else{
	          response.setStatus(HttpStatus.SC_BAD_REQUEST);
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
            response.setStatus(PretupsI.RESPONSE_FAIL);
    		response.setMessageCode("error.general.processing");
    		response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");
        } finally {
			if (mcomCon != null) {
				mcomCon.close("GradeManagementController#getGradeTypeList");
				mcomCon = null;
			}
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, " Exited ");
	        }
        }
		
		return response;
	}
	
	@GetMapping(value= "/viewGradeList", produces = MediaType.APPLICATION_JSON)	
	@ResponseBody
	/*@ApiOperation(value = "View Grade List",
	           response = GradeTypeListResponseVO.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = GradeTypeListResponseVO.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${viewGradeList.summary}", description="${viewGradeList.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = GradeTypeListResponseVO.class))
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


	public GradeTypeListResponseVO viewGradeList(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse responseSwag,
			@Parameter(description = "domainCode", required = true) @RequestParam("domainCode") String domainCode,
			@Parameter(description = "categoryCode", required = true) @RequestParam("categoryCode") String categoryCode) throws Exception{
		
		final String methodName =  "viewGradeList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		GradeTypeListResponseVO response =null;
		response = new GradeTypeListResponseVO();
		
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
			
			response = GradeManagementServiceI.viewGradeList(con, mcomCon, locale, domainCode, categoryCode, responseSwag);
		} catch (BTSLBaseException be) {
	       	 log.error(methodName, "Exception:e=" + be);
	         log.errorTrace(methodName, be);
	         if(be.getMessage().equalsIgnoreCase("1080001")||be.getMessage().equalsIgnoreCase("1080002")||be.getMessage().equalsIgnoreCase("1080003")||
	         		 be.getMessage().equalsIgnoreCase("241023")||be.getMessage().equalsIgnoreCase("241018")){
	        	response.setStatus(HttpStatus.SC_UNAUTHORIZED);
	         	responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
	         	
	         	 
	         }
	          else{
	          response.setStatus(HttpStatus.SC_BAD_REQUEST);
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
            response.setStatus(PretupsI.RESPONSE_FAIL);
    		response.setMessageCode("error.general.processing");
    		response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");
        } finally {
			if (mcomCon != null) {
				mcomCon.close("GradeManagementController#viewGradeList");
				mcomCon = null;
			}
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, " Exited ");
	        }
        }
		
		return response;
	}
	
	@PostMapping(value = "/addGrade", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Add Grade", response = GradeTypeListResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = GradeTypeListResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${addGrade.summary}", description="${addGrade.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = GradeTypeListResponseVO.class))
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


	public GradeTypeListResponseVO addGrade(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse responseSwag, HttpServletRequest httpServletRequest,
			@Parameter(description = "categoryCode", required = true) @RequestParam("categoryCode") String categoryCode,
			@Parameter(description = "gradeCode", required = true) @RequestParam("gradeCode") String gradeCode,
			@Parameter(description = "gradeName", required = true) @RequestParam("gradeName") String gradeName, 
			@Parameter(description = "defaultGrade", required = true) @RequestParam("defaultGrade") String defaultGrade) throws Exception {
		
		final String methodName =  "addGrade";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		GradeTypeListResponseVO response =null;
		response = new GradeTypeListResponseVO();
		
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
			
			response = GradeManagementServiceI.addGrade(con, mcomCon, locale, userVO, categoryCode, gradeCode, gradeName, defaultGrade, responseSwag);
		} catch (BTSLBaseException be) {
	       	 log.error(methodName, "Exception:e=" + be);
	         log.errorTrace(methodName, be);
	         if(be.getMessage().equalsIgnoreCase("1080001")||be.getMessage().equalsIgnoreCase("1080002")||be.getMessage().equalsIgnoreCase("1080003")||
	         		 be.getMessage().equalsIgnoreCase("241023")||be.getMessage().equalsIgnoreCase("241018")){
	        	response.setStatus(HttpStatus.SC_UNAUTHORIZED);
	         	responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
	         	
	         	 
	         }
	          else{
	          response.setStatus(HttpStatus.SC_BAD_REQUEST);
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
            response.setStatus(PretupsI.RESPONSE_FAIL);
    		response.setMessageCode("error.general.processing");
    		response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");
        } finally {
			if (mcomCon != null) {
				mcomCon.close("GradeManagementController#addGrade");
				mcomCon = null;
			}
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, " Exited ");
	        }
        }
		
		
		return response;

	}
	
	@PostMapping(value = "/modifyGrade", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Modify Grade", response = GradeTypeListResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = GradeTypeListResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${modifyGrade.summary}", description="${modifyGrade.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = GradeTypeListResponseVO.class))
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


	public GradeTypeListResponseVO modifyGrade(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse responseSwag, HttpServletRequest httpServletRequest,
			@Parameter(description = "gradeCode", required = true) @RequestParam("gradeCode") String gradeCode,
			@Parameter(description = "gradeName", required = true) @RequestParam("gradeName") String gradeName,
			@Parameter(description = "defaultGrade", required = true) @RequestParam("defaultGrade") String defaultGrade) throws Exception {
		
		final String methodName =  "modifyGrade";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		GradeTypeListResponseVO response =null;
		response = new GradeTypeListResponseVO();
		
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
			
			response = GradeManagementServiceI.modifyGrade(con, mcomCon, locale, userVO, gradeCode, gradeName, defaultGrade, responseSwag);
		} catch (BTSLBaseException be) {
	       	 log.error(methodName, "Exception:e=" + be);
	         log.errorTrace(methodName, be);
	         if(be.getMessage().equalsIgnoreCase("1080001")||be.getMessage().equalsIgnoreCase("1080002")||be.getMessage().equalsIgnoreCase("1080003")||
	         		 be.getMessage().equalsIgnoreCase("241023")||be.getMessage().equalsIgnoreCase("241018")){
	        	response.setStatus(HttpStatus.SC_UNAUTHORIZED);
	         	responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
	         	
	         	 
	         }
	          else{
	          response.setStatus(HttpStatus.SC_BAD_REQUEST);
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
            response.setStatus(PretupsI.RESPONSE_FAIL);
    		response.setMessageCode("error.general.processing");
    		response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");
        } finally {
			if (mcomCon != null) {
				mcomCon.close("GradeManagementController#modifyGrade");
				mcomCon = null;
			}
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, " Exited ");
	        }
        }
		
		
		return response;

	}
	
	
	@PostMapping(value = "/deleteGrade", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Delete Grade", response = GradeTypeListResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = GradeTypeListResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${deleteGrade.summary}", description="${deleteGrade.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = GradeTypeListResponseVO.class))
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


	public GradeTypeListResponseVO deleteGrade(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse responseSwag, HttpServletRequest httpServletRequest,
			@Parameter(description = "gradeCode", required = true) @RequestParam("gradeCode") String gradeCode) throws Exception {
		
		final String methodName =  "deleteGrade";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		GradeTypeListResponseVO response =null;
		response = new GradeTypeListResponseVO();
		
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
			
			response = GradeManagementServiceI.deleteGrade(con, mcomCon, locale, userVO, gradeCode, responseSwag);
		} catch (BTSLBaseException be) {
	       	 log.error(methodName, "Exception:e=" + be);
	         log.errorTrace(methodName, be);
	         if(be.getMessage().equalsIgnoreCase("1080001")||be.getMessage().equalsIgnoreCase("1080002")||be.getMessage().equalsIgnoreCase("1080003")||
	         		 be.getMessage().equalsIgnoreCase("241023")||be.getMessage().equalsIgnoreCase("241018")){
	        	response.setStatus(HttpStatus.SC_UNAUTHORIZED);
	         	responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
	         	
	         	 
	         }
	          else{
	          response.setStatus(HttpStatus.SC_BAD_REQUEST);
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
            response.setStatus(PretupsI.RESPONSE_FAIL);
    		response.setMessageCode("error.general.processing");
    		response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");
        } finally {
			if (mcomCon != null) {
				mcomCon.close("GradeManagementController#deleteGrade");
				mcomCon = null;
			}
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, " Exited ");
	        }
        }
		
		
		return response;

	}
}






































