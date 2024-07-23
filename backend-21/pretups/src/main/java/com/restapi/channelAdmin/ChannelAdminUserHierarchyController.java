package com.restapi.channelAdmin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import jakarta.servlet.http.HttpServletResponse;
//import jakarta.ws.rs.core.MediaType;

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
import com.btsl.common.ErrorMap;
import com.btsl.common.ListValueVO;
import com.btsl.common.MasterErrorList;
import com.btsl.common.TypesI;
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
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.restapi.channelAdmin.requestVO.ApprovalBarredForDltRequestVO;
import com.restapi.channelAdmin.requestVO.BarredusersrequestVO;
import com.restapi.channelAdmin.requestVO.BulkModifyUserRequestVO;
import com.restapi.channelAdmin.requestVO.BulkUserUploadRequestVO;
import com.restapi.channelAdmin.requestVO.SuspendResumeUserHierarchyRequestVO;
import com.restapi.channelAdmin.responseVO.AllDomainsResponseVO;
import com.restapi.channelAdmin.responseVO.AllGeoDomainsResponseVO;
import com.restapi.channelAdmin.responseVO.ApprovalBarredforDltResponseVO;
import com.restapi.channelAdmin.responseVO.BarredusersresponseVO;
import com.restapi.channelAdmin.responseVO.BulkModifyUserResponseVO;
import com.restapi.channelAdmin.responseVO.BulkUserUploadResponseVO;
import com.restapi.channelAdmin.responseVO.GetOwnerListResponseVO;
import com.restapi.channelAdmin.responseVO.GetParentListResponseVO;
import com.restapi.channelAdmin.service.ChannelAdminService;
import com.restapi.channelAdmin.service.ChannelAdminUserHierarchyService;
import com.restapi.user.service.DownloadTemplateService;
import com.restapi.user.service.FileDownloadResponse;
import com.restapi.user.service.FileDownloadResponseMulti;
import com.restapi.user.service.UserHierachyCARequestVO;
import com.restapi.user.service.UserHierarchyCAUIResponseVO;
import com.restapi.user.service.UserHierarchyUIResponseData;
import com.web.pretups.channel.user.businesslogic.ChannelUserTransferWebDAO;
import com.web.pretups.channel.user.web.BatchUserForm;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
//import springfox.documentation.annotations.ApiIgnore;

@Tag(name = "${ChannelAdminUserHierarchyController.name}", description = "${ChannelAdminUserHierarchyController.desc}")//@Api(tags ="Channel Admin", value="Channel Admin")//@Api(tags ="Channel Admin", value="Channel Admin")
@RestController	
@RequestMapping(value = "/v1/channeladmin")
public class ChannelAdminUserHierarchyController {
	protected final Log _log = LogFactory.getLog(getClass().getName());
	protected final Log LOG = LogFactory.getLog(getClass().getName());



	public static final Log log = LogFactory.getLog(ChannelAdminUserHierarchyController.class.getName());
	StringBuilder loggerValue= new StringBuilder(); 
	
	@Autowired
	private ChannelAdminUserHierarchyService channelAdminUserHierarchyService;
	@Autowired
	private DownloadTemplateService downloadTemplateService;
	@Autowired
	private ChannelAdminService channelAdminService;
	
	@PostMapping(value= "/UserHierarchyListCA")
	/*@ApiOperation(value = "User hierarchy info",
	           response = UserHierarchyCAUIResponseVO.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = UserBalanceResponseVO.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${UserHierarchyListCA.summary}", description="${UserHierarchyListCA.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UserHierarchyCAUIResponseVO.class))
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



	public UserHierarchyCAUIResponseVO getUserHierarchyCA(@RequestBody UserHierachyCARequestVO requestVO,
			 //
			 @RequestHeader MultiValueMap<String, String> headers,
			 HttpServletResponse responseSwag) {
		
		final String methodName =  "getUserHierarchyCA";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		OAuthUser oAuthUser = new OAuthUser();
		String loginID = null;
		UserHierarchyCAUIResponseVO response = new UserHierarchyCAUIResponseVO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
	    String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
	    Locale locale = new Locale(defaultLanguage, defaultCountry);
		oAuthUser.setData(new OAuthUserData());
		try
		{
			mcomCon = new MComConnection();
	    	con=mcomCon.getConnection();
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);

			loginID = oAuthUser.getData().getLoginid();
			
			List<UserHierarchyUIResponseData> responseObj = new ArrayList<UserHierarchyUIResponseData>();
			response.setUserHierarchyUIResponseData(responseObj);
			
			int maxLevel = channelAdminUserHierarchyService.getUserHierarchyListCA(con, loginID, requestVO, response.getUserHierarchyUIResponseData(), responseSwag);
			
			
			response.setStatus(HttpStatus.SC_OK);
			response.setLevel(maxLevel);
			response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USER_HIERARCHY_SUCCESS_MESSAGE, null));
		}
		catch (BTSLBaseException be) 
		{
			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
			}
			else
			{
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				String resmsg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(resmsg);
			}
			
		} 
		catch (Exception e) 
		{
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			String resmsg = RestAPIStringParser.getMessage(locale, e.getMessage(), null);
			response.setMessageCode(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			response.setMessage("error.general.processing");
		}
		finally
		{
			if (mcomCon != null) {
				mcomCon.close("ChannelAdminUserHierarchyController#getUserHierarchyCA");
				mcomCon = null;
			}
	        if (_log.isDebugEnabled()) {
	            _log.debug(methodName, " Exited ");
	        }
		}
		return response;
		}

	@PostMapping(value= "/suspendResumeUserHierarchyCA")
	/*@ApiOperation(value = "Suspend/Resume user hierarchy by channel admin",
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

	@io.swagger.v3.oas.annotations.Operation(summary = "${suspendResumeUserHierarchyCA.summary}", description="${suspendResumeUserHierarchyCA.description}",

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


	public BaseResponse suspendResumeUserHierarchyCA(@RequestBody SuspendResumeUserHierarchyRequestVO requestVO,
			 //
			 @RequestHeader MultiValueMap<String, String> headers,
			 HttpServletResponse responseSwag) {
		
		final String methodName =  "suspendResumeUserHierarchyCA";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		OAuthUser oAuthUser = new OAuthUser();
		String loginID = null;
		BaseResponse response = new BaseResponse();
		Connection con = null;
		MComConnectionI mcomCon = null;
		String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
	    String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
	    Locale locale = new Locale(defaultLanguage, defaultCountry);
		oAuthUser.setData(new OAuthUserData());
		try
		{
			mcomCon = new MComConnection();
	    	con=mcomCon.getConnection();
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);

			loginID = oAuthUser.getData().getLoginid();;
			
			
			channelAdminUserHierarchyService.suspendResumeUserHierarchyListCA(con, loginID, requestVO, responseSwag);
			
			
			response.setStatus(HttpStatus.SC_OK);
			if(PretupsI.STATUS_ACTIVE.equals(requestVO.getRequestType())) {
				response.setMessageCode(PretupsErrorCodesI.USERS_RESUMED_SUCCESSFULLY);
				response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USERS_RESUMED_SUCCESSFULLY, null));
			}else if(PretupsI.STATUS_SUSPEND.equals(requestVO.getRequestType())) {
				response.setMessageCode(PretupsErrorCodesI.USERS_SUSPENDED_SUCCESSFULLY);
				response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USERS_SUSPENDED_SUCCESSFULLY, null));
			}
		}
		catch (BTSLBaseException be) 
		{
			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setMessageCode(be.getMessage());
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
			}
			else
			{
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setMessageCode(be.getMessage());
			}
			
			String resmsg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
			response.setMessage(resmsg);
			
		} 
		catch (Exception e) 
		{
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			String resmsg = RestAPIStringParser.getMessage(locale, e.getMessage(), null);
			response.setMessageCode(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			response.setMessage("error.general.processing");
		}
		finally
		{
			if (mcomCon != null) {
				mcomCon.close("ChannelAdminUserHierarchyController#" + methodName);
				mcomCon = null;
			}
	        if (_log.isDebugEnabled()) {
	            _log.debug(methodName, " Exited ");
	        }
		}
		return response;
		}

	
	
	@GetMapping(value= "/alldomains")
	/*@ApiOperation(value = "Get All domains",
	           response = AllDomainsResponseVO.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = UserBalanceResponseVO.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${alldomains.summary}", description="${alldomains.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = AllDomainsResponseVO.class))
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



	public AllDomainsResponseVO getAllDomains(
			//
												  @RequestHeader MultiValueMap<String, String> headers,
			 HttpServletResponse responseSwag) {
		
		final String methodName =  "getAllDomains";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		OAuthUser oAuthUser = new OAuthUser();
		String userID = null;
		AllDomainsResponseVO response = new AllDomainsResponseVO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
	    String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
	    DomainDAO domainDao;
	    Locale locale = new Locale(defaultLanguage, defaultCountry);
		oAuthUser.setData(new OAuthUserData());
		try
		{
			mcomCon = new MComConnection();
	    	con=mcomCon.getConnection();
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			userID = oAuthUser.getData().getUserid();
			
			domainDao = new DomainDAO();	
			ArrayList<ListValueVO> domains = domainDao.loadDomainListByUserId(con,userID);
			
			response.setDomains(domains);
			response.setStatus(HttpStatus.SC_OK);
			response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null));
		}
		catch (BTSLBaseException be) 
		{
			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
			}
			else
			{
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				String resmsg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(resmsg);
			}
			
		} 
		catch (Exception e) 
		{
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			String resmsg = RestAPIStringParser.getMessage(locale, e.getMessage(), null);
			response.setMessageCode(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			response.setMessage("error.general.processing");
		}
		finally
		{
			if (mcomCon != null) {
				mcomCon.close("ChannelAdminUserHierarchyController#getAllDomains");
				mcomCon = null;
			}
	        if (_log.isDebugEnabled()) {
	            _log.debug(methodName, " Exited ");
	        }
		}
		return response;
	}
	
	@GetMapping(value= "/allgeodomains")
	/*@ApiOperation(value = "Get All Geodomains",
	           response = AllGeoDomainsResponseVO.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = UserBalanceResponseVO.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${allgeodomains.summary}", description="${allgeodomains.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = AllGeoDomainsResponseVO.class))
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


	public AllGeoDomainsResponseVO getAllGeoDomains(
			//
														@RequestHeader MultiValueMap<String, String> headers,
			 HttpServletResponse responseSwag) {
		
		final String methodName =  "getAllGeoDomains";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		OAuthUser oAuthUser = new OAuthUser();
		String userID = null;
		String msisdn = null;
		AllGeoDomainsResponseVO response = new AllGeoDomainsResponseVO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
	    String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
	    GeographicalDomainDAO geoDomainDao;
	    Locale locale = new Locale(defaultLanguage, defaultCountry);
		oAuthUser.setData(new OAuthUserData());
		try
		{
			mcomCon = new MComConnection();
	    	con=mcomCon.getConnection();
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			userID = oAuthUser.getData().getUserid();
			msisdn= oAuthUser.getData().getMsisdn();
			
			String filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdn);
            String msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
            NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
            String networkCode = networkPrefixVO.getNetworkCode();

			geoDomainDao = new GeographicalDomainDAO();	
			ArrayList<UserGeographiesVO> geoDomains = geoDomainDao.loadUserGeographyList(con,userID,networkCode);
			
			response.setGeoDomains(geoDomains);
			response.setStatus(HttpStatus.SC_OK);
			response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null));
		}
		catch (BTSLBaseException be) 
		{
			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
			}
			else
			{
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				String resmsg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(resmsg);
			}
			
		} 
		catch (Exception e) 
		{
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			String resmsg = RestAPIStringParser.getMessage(locale, e.getMessage(), null);
			response.setMessageCode(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			response.setMessage("error.general.processing");
		}
		finally
		{
			if (mcomCon != null) {
				mcomCon.close("ChannelAdminUserHierarchyController#getAllGeoDomains");
				mcomCon = null;
			}
	        if (_log.isDebugEnabled()) {
	            _log.debug(methodName, " Exited ");
	        }
		}
		return response;
	}
	
	
	@GetMapping(value= "/owneruserlist")	
	/*@ApiOperation(value = "Get All owner users",
			notes=("Api Info:")+ ("\n") + (SwaggerAPIDescriptionI.BALANCE_DETAILS),
	           response = GetOwnerListResponseVO.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = GetOwnerListResponseVO.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${owneruserlist.summary}", description="${owneruserlist.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = GetOwnerListResponseVO.class))
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


	public GetOwnerListResponseVO getOwnerUserList(
			@Parameter(description = "DomainCode",  required = true) @RequestParam("domainCode") String domainCode,
			@Parameter(description = "GeoDomainCode",  required = true) @RequestParam("geoDomainCode") String geoDomainCode,
			@Parameter(description = "ownerName") @RequestParam("ownerName") String ownerName,
			@Parameter(description = "searchBy", required = true)//allowableValues = "ADD, HIERARCHY")
			@RequestParam("searchBy") String searchBy,
			 @RequestHeader MultiValueMap<String, String> headers,
			 HttpServletResponse responseSwag) {
		
		final String methodName =  "getOwnerUserList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		OAuthUser oAuthUser = new OAuthUser();
		String userID = null;
		GetOwnerListResponseVO response = new GetOwnerListResponseVO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
	    String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
	    UserDAO userDao;
	    Locale locale = new Locale(defaultLanguage, defaultCountry);
		oAuthUser.setData(new OAuthUserData());
		String ownerStatus="";
		try
		{
			mcomCon = new MComConnection();
	    	con=mcomCon.getConnection();
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			userID = oAuthUser.getData().getUserid();
			
			userDao = new UserDAO();
			
			if(BTSLUtil.isEmpty(domainCode)) {
				throw new BTSLBaseException(PretupsErrorCodesI.INVALID_DOMAIN);
			}
			
			if(BTSLUtil.isEmpty(geoDomainCode)) {
				throw new BTSLBaseException(PretupsErrorCodesI.GRPH_INVALID_DOMAIN);
			}
			
			if(BTSLUtil.isEmpty(ownerName))
				ownerName="";
			
			if(searchBy.equals("ADD")) {
				ownerStatus=PretupsBL.userStatusActive();
			}else if(searchBy.equals("HIERARCHY"))
				ownerStatus= "'" + PretupsI.USER_STATUS_ACTIVE + "','" + PretupsI.USER_STATUS_SUSPEND + "', '" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "'";
			final ArrayList<UserVO> ownerList = userDao.loadOwnerUserList(con, geoDomainCode,"%"+ownerName+"%",domainCode, PretupsI.STATUS_IN, ownerStatus);
			
			response.setOwnerList(ownerList);
			response.setStatus(HttpStatus.SC_OK);
			response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null));
		}
		catch (BTSLBaseException be) 
		{
			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
			}
			else
			{
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				String resmsg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(resmsg);
			}
			
		} 
		catch (Exception e) 
		{
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			String resmsg = RestAPIStringParser.getMessage(locale, e.getMessage(), null);
			response.setMessageCode(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			response.setMessage("error.general.processing");
		}
		finally
		{
			if (mcomCon != null) {
				mcomCon.close("ChannelAdminUserHierarchyController#getOwnerUserList");
				mcomCon = null;
			}
	        if (_log.isDebugEnabled()) {
	            _log.debug(methodName, " Exited ");
	        }
		}
		return response;
	}
	
	@GetMapping(value= "/parentuserlist")	
	/*@ApiOperation(value = "Get All Parent users",
	           response = GetParentListResponseVO.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = GetParentListResponseVO.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${parentuserlist.summary}", description="${parentuserlist.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = GetParentListResponseVO.class))
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



	public GetParentListResponseVO getParentUserList(
			@Parameter(description = "categoryCode",  required = true) @RequestParam("categoryCode") String categoryCode,
			@Parameter(description = "ownerUserId", required = true) @RequestParam("ownerUserId") String ownerUserId,
			@Parameter(description = "parentName") @RequestParam("parentName") String parentName,
			@Parameter(description = "searchBy", required = true)//allowableValues = "ADD, HIERARCHY")
			@RequestParam("searchBy") String searchBy,
			 @RequestHeader MultiValueMap<String, String> headers,
			 HttpServletResponse responseSwag) {
		
		final String methodName =  "getParentUserList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		OAuthUser oAuthUser = new OAuthUser();
		String userID = null;
		String msisdn = null;
		GetParentListResponseVO response = new GetParentListResponseVO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
	    String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
	    ChannelUserDAO channelUserDao;
	    Locale locale = new Locale(defaultLanguage, defaultCountry);
		oAuthUser.setData(new OAuthUserData());
		String status="";
		try
		{
			mcomCon = new MComConnection();
	    	con=mcomCon.getConnection();
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			userID = oAuthUser.getData().getUserid();
			msisdn= oAuthUser.getData().getMsisdn();
			
			String filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdn);
            String msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
            NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
            String networkCode = networkPrefixVO.getNetworkCode();
			
            if(BTSLUtil.isEmpty(categoryCode)) {
				throw new BTSLBaseException(PretupsErrorCodesI.CATEGORY_NOT_EXIST);
			}
			
			if(BTSLUtil.isEmpty(ownerUserId)) {
				throw new BTSLBaseException(PretupsErrorCodesI.OWNER_USER_DOES_NOT_EXIST);
			}
			
			if(BTSLUtil.isEmpty(parentName))
				parentName="";
			
			channelUserDao = new ChannelUserDAO();
			if(searchBy.equals("ADD")) {
				status=PretupsBL.userStatusActive();
			}else if(searchBy.equals("HIERARCHY"))
				status= "'" + PretupsI.USER_STATUS_ACTIVE + "','" + PretupsI.USER_STATUS_SUSPEND + "', '" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "'";
			final ArrayList<ListValueVO> parentList = channelUserDao.loadCategoryUsers(con, categoryCode, "%"+parentName+"%",networkCode, ownerUserId,
	                PretupsI.STATUS_IN, status);
			
			response.setParentList(parentList);
			response.setStatus(HttpStatus.SC_OK);
			response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null));
		}
		catch (BTSLBaseException be) 
		{
			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
			}
			else
			{
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				String resmsg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(resmsg);
			}
			
		} 
		catch (Exception e) 
		{
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			String resmsg = RestAPIStringParser.getMessage(locale, e.getMessage(), null);
			response.setMessageCode(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			response.setMessage("error.general.processing");
		}
		finally
		{
			if (mcomCon != null) {
				mcomCon.close("ChannelAdminUserHierarchyController#getParentUserList");
				mcomCon = null;
			}
	        if (_log.isDebugEnabled()) {
	            _log.debug(methodName, " Exited ");
	        }
		}
		return response;
	}
	@PostMapping(value = "/trfuserhierarchy")
	/*@ApiOperation(tags="Channel Admin", value = "Channel Admin User Transfer Hierarchy",
	  
	  authorizations = {
	            @Authorization(value = "Authorization")})
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = BaseResponse.class),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found")
			})
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${trfuserhierarchy.summary}", description="${trfuserhierarchy.description}",

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


	public BaseResponse confirmUserTransfer(
			 @RequestHeader MultiValueMap<String, String> headers,
			 @Parameter(description =SwaggerAPIDescriptionI.TRANSFER_USER_HIERARCHY, required = true)
			 @RequestBody ChannelAdminTransferVO requestVO, HttpServletResponse response1
			 )throws Exception {
		final String methodName =  "confirmUserTransfer";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		Connection con = null;
        MComConnectionI mcomCon = null;
        BaseResponse response = null;
        UserVO sessionUserVO = new UserVO();
        response = new BaseResponse();
        ChannelUserTransferWebDAO channelUserTransferwebDAO = null;
        ChannelUserVO channelUserVO = new ChannelUserVO();
        UserDAO userDAO =new UserDAO();
		Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
		try {
			OAuthUser oAuthUserData=new OAuthUser();
    		oAuthUserData.setData(new OAuthUserData());
    		OAuthenticationUtil.validateTokenApi(oAuthUserData,headers,response1);
    		mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			channelUserTransferwebDAO = new ChannelUserTransferWebDAO();
			sessionUserVO = userDAO.loadUserDetailsFormUserID(con,requestVO.getToParentUser());
			channelUserVO = userDAO.loadUserDetailsFormUserID(con, requestVO.getUserId());
			if(channelUserVO==null){
				throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.USER_NOT_FOUND_DELETE, 0,null,null);
			}
			final ArrayList userList = channelUserTransferwebDAO.loadChannelUserList(con, channelUserVO.getUserID(), channelUserVO.getCategoryCode(), null, PretupsI.USER_STATUS_SUSPEND);
			String [] user = {channelUserVO.getUserName()};
			if(channelUserVO.getParentID().equalsIgnoreCase(sessionUserVO.getUserID())){
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USER_ALREADY_PRESENT, user);
				response.setMessageCode(PretupsErrorCodesI.USER_ALREADY_PRESENT);
				response.setMessage(msg);
				 throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.USER_ALREADY_PRESENT, 0,user,null);  //add error code user already under same parent
			}
			response = channelAdminUserHierarchyService.confirmChannelUserTransfer(con,mcomCon, response,response1,userList,channelUserVO,sessionUserVO,requestVO);	
            
		}
		catch(BTSLBaseException be) {
			log.error(methodName, "Exceptin:e=" + be);
			log.errorTrace(methodName, be);
			if(response.getMessage()==null) {
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			response.setMessageCode(be.getMessageKey());
			response.setMessage(msg);
			}
			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
	 }
	 finally {

			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting:=" + methodName);
			}

		}
        return response;
	}
	
	@GetMapping(value = "/downloadBulkUserTemplate")
	@ResponseBody
	/*@ApiOperation(value = "Bulk Add User Template Download", notes = ("Api Info:"), response = FileDownloadResponse.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = FileDownloadResponse.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${downloadBulkUserTemplate.summary}", description="${downloadBulkUserTemplate.description}",

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



	public FileDownloadResponse getBulkUserTemplate( @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse responseswag,
			@Parameter(description = "geographyCode", required = true) @RequestParam("geographyCode") String geographyCode,
			@Parameter(description = "domainCode", required = true) @RequestParam("domainCode") String domainCode)
			throws BTSLBaseException, SQLException, JsonParseException, JsonMappingException, IOException {
		final String methodName = "getBulkUserTemplate";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered");
		}

		FileDownloadResponse fileDownloadResponse = new FileDownloadResponse();
		Connection con = null;
		MComConnectionI mcomCon = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

		try {
			
			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, responseswag);

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			String user1 = OAuthUserData.getData().getLoginid();
			UserVO userVO = new UserDAO().loadAllUserDetailsByLoginID(con, user1);
			downloadTemplateService.downloadBulkUserTemplate(con, userVO,domainCode,geographyCode,fileDownloadResponse, responseswag);

		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);
			if (be.getMessage().equalsIgnoreCase("1080001") || be.getMessage().equalsIgnoreCase("1080002")
					|| be.getMessage().equalsIgnoreCase("1080003") || be.getMessage().equalsIgnoreCase("241023")
					|| be.getMessage().equalsIgnoreCase("241018")) {
				responseswag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				fileDownloadResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				responseswag.setStatus(HttpStatus.SC_BAD_REQUEST);
				fileDownloadResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
			String resmsg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
			fileDownloadResponse.setMessageCode(be.getMessage());
			fileDownloadResponse.setMessage(resmsg);

		} catch (Exception ex) {
			responseswag.setStatus(HttpStatus.SC_BAD_REQUEST);
			fileDownloadResponse.setStatus(PretupsI.RESPONSE_FAIL);
			log.errorTrace(methodName, ex);
			log.error(methodName, "Exception = " + ex.getMessage());
		}finally{
			if(con != null)
				con.close();
		}
		return fileDownloadResponse;
	}

	@PostMapping(value = "/initiateBulkUpload")
	@ResponseBody
	/*@ApiOperation(value = "Process Bulk Users addition", response = C2SBulkRechargeResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })

	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = C2SBulkRechargeResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${initiateBulkUpload.summary}", description="${initiateBulkUpload.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = BulkUserUploadResponseVO.class))
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



	public BulkUserUploadResponseVO initiateBulkUsersUpload(
			 @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = SwaggerAPIDescriptionI.BULK_UPLOAD_API,  required = true) @RequestBody BulkUserUploadRequestVO requestVO,
			HttpServletResponse responseSwag) throws Exception {
		final String methodName = "initiateBulkUsersUpload";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		BulkUserUploadResponseVO response1 = new BulkUserUploadResponseVO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		Instant start = Instant.now();
		try {
			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, responseSwag);
	
			// getting loggedIn user details
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			String user1 = OAuthUserData.getData().getLoginid();
			UserVO userVO = new UserDAO().loadAllUserDetailsByLoginID(con, user1);
			
			channelAdminService.initiateBulkUsersUpload(con, requestVO, userVO, response1, responseSwag);
		
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);
			if (be.getMessage().equalsIgnoreCase("1080001") || be.getMessage().equalsIgnoreCase("1080002")
					|| be.getMessage().equalsIgnoreCase("1080003") || be.getMessage().equalsIgnoreCase("241023")
					|| be.getMessage().equalsIgnoreCase("241018")) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
			String resmsg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
			response1.setMessageCode(be.getMessage());
			response1.setMessage(resmsg);

		} catch (Exception ex) {
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			response1.setStatus(PretupsI.RESPONSE_FAIL);
			log.errorTrace(methodName, ex);
			log.error(methodName, "Some error occured: " + ex.getMessage());
		}finally{
			if(con != null)
				con.close();
		}
		Instant end = Instant.now();
		Duration timeElapsed = Duration.between(start, end); 
		log.debug(methodName, "time taken by api : "+ timeElapsed.getSeconds()/3600 + " hrs and " + timeElapsed.getSeconds()/60 + " minutes and " + timeElapsed.getSeconds()%60+" secs" );
		return response1;
	}
	@GetMapping(value= "/downloadBulkModifyUsersList")	
	@ResponseBody
	/*@ApiOperation(value = "Download Bulk Modify Users List",
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

	@io.swagger.v3.oas.annotations.Operation(summary = "${downloadBulkModifyUsersList.summary}", description="${downloadBulkModifyUsersList.description}",

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



	public FileDownloadResponseMulti downloadBulkModifyUsersList(
	 @RequestHeader MultiValueMap<String, String> headers,

	@Parameter(description = "domainCode", required = true)
	@RequestParam("domainCode") String domainType,
	@Parameter(description = "categoryCode", required = true)
	@RequestParam("categoryCode") String categoryType,
	@Parameter(description = "geoDomainCode", required = true)
	@RequestParam("geoDomainCode") String geoDomainType,
	 HttpServletResponse responseSwag
			)throws Exception{

		
		final String methodName =  "downloadBulkModifyUsersList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
	    FileDownloadResponseMulti response=null;
	    response = new FileDownloadResponseMulti();

		final BatchUserForm theForm = new BatchUserForm() ;
		UserDAO userDao = new UserDAO();
        Locale locale = null;

        Connection con = null;
		MComConnectionI mcomCon = null;
		
		OAuthUser oAuthUser= null;
		OAuthUserData oAuthUserData =null;

		try
		{
			locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers,responseSwag);
			String loginID = null;
	        try {
	            CategoryDAO categoryDAO = null;

	            // getting loogedIn user details
	            loginID = OAuthUserData.getData().getLoginid();
				UserVO userVO = new UserDAO().loadAllUserDetailsByLoginID(con, loginID);				
				
	            final ArrayList domainListAll = new DomainDAO().loadCategoryDomainList(con);
	            theForm.setDomainAllList(domainListAll);
	            ArrayList domainList = userVO.getDomainList();
	            if ((domainList == null || domainList.isEmpty()) && PretupsI.YES.equals(userVO.getCategoryVO().getDomainAllowed()) && PretupsI.DOMAINS_FIXED.equals(userVO
	                .getCategoryVO().getFixedDomains())) {
	                domainList = domainListAll;
	            }
	            if (domainList == null || domainList.isEmpty()) {
	                throw new BTSLBaseException(this, "downloadBulkModifyUsersList", "bulkuser.selectdomainforupload.error.msg.nodomainlist", "selectDomainForBatchModify");
	            } else {
	                theForm.setDomainList(BTSLUtil.displayDomainList(domainList));
	            }
	            if (domainList.size() == 1) {
	                final ListValueVO listVO = BTSLUtil.getOptionDesc(((ListValueVO) domainList.get(0)).getValue(), domainListAll);
	                theForm.setDomainName(listVO.getLabel());
	                theForm.setDomainCode(listVO.getValue());
	                theForm.setDomainType(listVO.getOtherInfo());
	            }

	            categoryDAO = new CategoryDAO();
	            final ArrayList catlist = categoryDAO.loadOtherCategorList(con, PretupsI.OPERATOR_TYPE_OPT);
	            if (catlist == null || catlist.isEmpty()) {
	                throw new BTSLBaseException(this, "downloadBulkModifyUsersList", "bulkuser.selectdomainforupload.error.msg.nocategorylist");
	            }
	            theForm.setCategoryList(catlist);

	            final ArrayList userGeoList = userVO.getGeographicalAreaList();

	            if (userGeoList == null || userGeoList.isEmpty()) {
	                throw new BTSLBaseException(this, "downloadBulkModifyUsersList", "bulkuser.selectdomainforupload.error.msg.nogeodomain", "downloadBulkModifyUsersList");
	            }
	            theForm.setGeographyList(userGeoList);
	            if (userGeoList.size() == 1) {
	                final UserGeographiesVO userGeographiesVO = (UserGeographiesVO) userGeoList.get(0);
	                theForm.setGeographyCode(userGeographiesVO.getGraphDomainCode());
	                theForm.setGeographyName(userGeographiesVO.getGraphDomainName());
	                theForm.setGeographyStr(userGeographiesVO.getGraphDomainName());
	            }
	            
	            
	            CategoryVO categoryVO = null;
	            for (int i = 0, j = catlist.size(); i < j; i++) {
	            	categoryVO = (CategoryVO) catlist.get(i);
	            	if ((categoryType.equals(categoryVO.getCategoryCode())) || (categoryType.equals(categoryVO.getCategoryName())))  {
	            		theForm.setCategoryName(categoryVO.getCategoryName());
	            		theForm.setCategoryCode(categoryVO.getCombinedKey());
	            		theForm.setDomainCode(categoryVO.getDomainCodeforCategory());
	            		theForm.setDomainName(categoryVO.getDomainName());
	            		break;
	            	}
	            }
	            
	            UserGeographiesVO userGeographiesVO = null;
	            for (int i = 0, j = userGeoList.size(); i < j; i++) {
	            	userGeographiesVO = (UserGeographiesVO) userGeoList.get(i);
	            	if ((geoDomainType.equals(userGeographiesVO.getGraphDomainCode())) || (geoDomainType.equals(userGeographiesVO.getGraphDomainName()))) {
	            		theForm.setGeographyName(userGeographiesVO.getGraphDomainName());
                        theForm.setGeographyStr(userGeographiesVO.getGraphDomainName());
                        theForm.setGeographyCode(userGeographiesVO.getGraphDomainCode());
                        break;
	            	}
	            }
	            
	            channelAdminUserHierarchyService.downloadBulkModifyUsersList(headers,domainType, categoryType, geoDomainType, theForm, userVO,	
						 response, con);
	            
				responseSwag.setStatus(HttpStatus.SC_OK);

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
	            if (log.isDebugEnabled()) {
	                log.debug(methodName, " Exited ");
	            }
	        }
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
	

	
	
	



	@PostMapping(value = "/uploadBulkModifyUser")
	@ResponseBody
	/*@ApiOperation(value = "Upload Base64 encoded file for Batch User Modification",
					response = BulkModifyUserResponseVO.class,
					authorizations = {
					@Authorization(value = "Authorization") })

	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = BulkModifyUserResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${uploadBulkModifyUser.summary}", description="${uploadBulkModifyUser.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = BulkModifyUserResponseVO.class))
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

	public  BulkModifyUserResponseVO uploadBulkModifyUser(
			 @RequestHeader MultiValueMap<String, String> headers,

			@Parameter(description = "domainCode", required = true)
			@RequestParam("domainCode") String domainType,
			@Parameter(description = "categoryCode", required = true)
			@RequestParam("categoryCode") String categoryType,
			@Parameter(description = "geoDomainCode", required = true)
			@RequestParam("geoDomainCode") String geoDomainType,
			@RequestBody BulkModifyUserRequestVO c2CFileUploadApiRequest,
			 HttpServletResponse responseSwag
					)throws Exception{
		
			
		final String methodName =  "uploadBulkModifyUser";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		BulkModifyUserRequestVO request = c2CFileUploadApiRequest;
		BulkModifyUserResponseVO response = null;
		
		ArrayList<MasterErrorList> inputValidations = new ArrayList<>();
		 
        Locale locale = null;
        Connection con = null;
		MComConnectionI mcomCon = null;
		
		OAuthUser oAuthUser= null;
		OAuthUserData oAuthUserData =null;
		Date currentDate = new Date();
		
		UserDAO userDao = new UserDAO();
		AdminOperationVO adminOperationVO = new AdminOperationVO();

		try
		{
			locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
			
			response = new BulkModifyUserResponseVO();
			response.setFileAttachment(request.getFileAttachment());
			response.setFileName("ErrorLogs");
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
			oAuthUser = new OAuthUser();
			oAuthUserData =new OAuthUserData();
			oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers,responseSwag);
			
			ArrayList<MasterErrorList> inputVal = channelAdminUserHierarchyService.basicFileValidations(request, response, domainType, categoryType, geoDomainType, locale, inputValidations);
			
						 
			if(!BTSLUtil.isNullOrEmptyList(inputVal)) {
				response.setStatus("400");
				response.setErrorMap(new ErrorMap());
				response.getErrorMap().setMasterErrorList(inputVal);
				response.setMessage("Invalid File");
			}
			
			else {
				ChannelUserVO userVO = userDao.loadAllUserDetailsByLoginID(con, oAuthUser.getData().getLoginid());
				
                adminOperationVO.setSource(TypesI.LOGGER_MODIFY_BULK_USER);
                adminOperationVO.setDate(currentDate);
                adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
                adminOperationVO.setLoginID(userVO.getLoginID());
                adminOperationVO.setUserID(userVO.getUserID());
                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                adminOperationVO.setNetworkCode(userVO.getNetworkID());
                adminOperationVO.setMsisdn(userVO.getMsisdn());
				boolean fileUpload = false;
		    	final ArrayList fileErrorList = new ArrayList();
				fileUpload = channelAdminUserHierarchyService.uploadAndValidateModifyBulkUserFile(con, mcomCon, userVO, request, response,fileErrorList);
				if (fileUpload) {
					adminOperationVO.setInfo("Bulk user modification file " + request.getFileName() +"."  +  String.valueOf(request.getFileType()).toLowerCase()  +  " has been uploaded  successfully");	
	                AdminOperationLog.log(adminOperationVO);
					
					log.debug(methodName, "file uploaded successfully");
					boolean fileProcessed = false;
					adminOperationVO.setInfo("Bulk user modification file " + request.getFileName() +"."  +  String.valueOf(request.getFileType()).toLowerCase()  +  " processing  Started...");
					AdminOperationLog.log(adminOperationVO);
					fileProcessed = channelAdminUserHierarchyService.processUploadedModifyBulkUserFile(con, mcomCon, userVO, categoryType, geoDomainType, request, response, responseSwag,fileErrorList,fileErrorList.size());
					AdminOperationLog.log(adminOperationVO);
					if (fileProcessed) {
						adminOperationVO.setInfo("Bulk user modification file " + request.getFileName() +"."  +  String.valueOf(request.getFileType()).toLowerCase()  +  " processing  Successful...");
						AdminOperationLog.log(adminOperationVO);
						response.setStatus("200");
					}
					
					adminOperationVO.setInfo("Bulk user modification file " + request.getFileName() +"."  +  String.valueOf(request.getFileType()).toLowerCase()  +  " processing  Completed...");
					AdminOperationLog.log(adminOperationVO);
				}
				else {
					adminOperationVO.setInfo("Bulk user modification file " + request.getFileName() +"."  +  String.valueOf(request.getFileType()).toLowerCase()  +  " upload failed");
					AdminOperationLog.log(adminOperationVO);
                    throw new BTSLBaseException(this, methodName, "bulkuser.uploadandvalidatebulkuserfile.error.filenotuploaded");
                }	
			}
			

		}catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);
			if (be.getMessage().equalsIgnoreCase("1080001") || be.getMessage().equalsIgnoreCase("1080002")
					|| be.getMessage().equalsIgnoreCase("1080003") || be.getMessage().equalsIgnoreCase("241023")
					|| be.getMessage().equalsIgnoreCase("241018")) {
				response.setStatus("401");
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				response.setStatus("400");
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
			String resmsg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
			response.setMessageCode(be.getMessage());
			response.setMessage(resmsg);
			adminOperationVO.setInfo("Bulk user modification file " + request.getFileName() +"."  +  String.valueOf(request.getFileType()).toLowerCase()  + "Procecessed with MessageCode : "  +  response.getMessageCode() + ", Message =" +response.getMessage() );
			AdminOperationLog.log(adminOperationVO);
		} catch (Exception e) {
			log.errorTrace(methodName, e);
			log.error(methodName, "Unable to write data into a file Exception = " + e.getMessage());
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus("400");
			adminOperationVO.setInfo("Bulk user modification file " + request.getFileName() +"."  +  String.valueOf(request.getFileType()).toLowerCase()  + " processing failed due to system error.");
			AdminOperationLog.log(adminOperationVO);
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
	
	@SuppressWarnings("unchecked")
	@PostMapping(value= "/BarreduserList")
	@ResponseBody
	/*@ApiOperation(tags= "Channel admin", value = "Barred Approval Users ", response = BarredusersresponseVO.class,
	  notes = ("Api Info:") + ("\n") + (""
	  		+ " 1.  SearchType : MSISDN \r\n"
	  		+ "     MobileNumber,loggedUserNeworkCode,userStatus Required\r\n"
	  		+ "   \r\n"
	  		+ " 2. SearchType :LOGINID\r\n"
	  		+ "    loginID,loggedUserNeworkCode,userStatus Required\r\n"
	  		+ "   \r\n"
	  		+ " 3. SearchType : Advanced\r\n"
	  		+ "    category,domain,geography,loggedUserNeworkCode,userStatus Required\r\n"
	  		+ " 4. SearchType : External Code\r\n"
	  		+ "    Externalcode,loggedUserNeworkCode,userStatus Required") + ("\n")  
				,
				 authorizations = {
		    	            @Authorization(value = "Authorization")})
	        @ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = ApprovalUsersResponse.class),
	        @ApiResponse(code = 201, message = "Created"),
	        @ApiResponse(code = 400, message = "Bad Request"),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })	
	*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${BarreduserList.summary}", description="${BarreduserList.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = BarredusersresponseVO.class))
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
	)public BarredusersresponseVO getBarredfordltChannelUsers(
			HttpServletResponse responseSwag,
			 @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = SwaggerAPIDescriptionI.BARRED_USER_APPRV_LIST, required = true)
			@RequestBody BarredusersrequestVO requestVO)throws IOException, SQLException, BTSLBaseException
	{
		final String methodName =  "getBarredfordltUsers";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered ");	
		}
		LogFactory.printLog(methodName, "Entered requestVO=" + requestVO, LOG);
		 
		 OAuthUser oAuthUser = null;
		 OAuthUserData oAuthUserData = null;
		 Connection con = null;MComConnectionI mcomCon = null;
		 BarredusersresponseVO response = new BarredusersresponseVO();
		 response.setService("BarredApproveList");
		 Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue
				        (PreferenceI.DEFAULT_LANGUAGE)), (String)
				        (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
		 try {
			    mcomCon = new MComConnection();
			    con=mcomCon.getConnection();
				OAuthenticationUtil.validateTokenApi(headers);
			    oAuthUser = new OAuthUser();
				oAuthUserData = new OAuthUserData();
			    oAuthUser.setData(oAuthUserData);
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
				 List<ChannelUserVO> userList= channelAdminUserHierarchyService.fetchChannelUsersByStatusForBarredfrdltReq(con, requestVO);
			     if(userList.size()==0) {
			    	 response.setMessage("No User Found");
			     }
				 response.setUsersList(userList);
				 response.setStatus(String.valueOf(HttpServletResponse.SC_OK));
				
				 return response;
		 }catch (BTSLBaseException be) {
	        	response.setStatus(String.valueOf(HttpServletResponse.SC_BAD_REQUEST));
	            LOG.error(methodName, "BTSLBaseException " + be.getMessage());
	            try {
	                if (con != null) {
	                    con.rollback();
	                }
	            } catch (Exception e) {
	                LOG.errorTrace(methodName, e);
	            }
	            LOG.errorTrace(methodName, be);
	            response.setMessageCode(be.getMessageKey());
				response.setMessage(RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs()));
				responseSwag.setStatus(HttpServletResponse.SC_BAD_REQUEST);   
				return response;
	            
	        } catch (Exception e) {
	        	response.setStatus(String.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
	        	responseSwag.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	        	try {
	                if (con != null) {
	                    con.rollback();
	                }
	            } catch (Exception ee) {
	                LOG.errorTrace(methodName, ee);
	            }
	            LOG.error(methodName, "Exception " + e.getMessage());
	            LOG.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "", "", "",
	                "Exception:" + e.getMessage());
	            response.setMessage(e.getMessage());
	            return response;
	        } finally {
	            LogFactory.printLog(methodName, " Exited ", LOG);
	            if(mcomCon != null){mcomCon.close("ChannelUserApprovalBarred#BarredUsers");mcomCon=null;}
	        }
	}
	
	
	
	@PostMapping(value= "/approvalbarredfordlt")
	@ResponseBody
	/*@ApiOperation(tags= "Channel admin", value = "Channel users in barred request to barred  ", response = ApprovalBarredforDltResponseVO.class,
	  notes = ("Api Info:") + ("\n") + ("") + ("\n")  
				,
				 authorizations = {
		    	            @Authorization(value = "Authorization")})
	        @ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = ActionOnUserResVo.class),
	        @ApiResponse(code = 201, message = "Created"),
	        @ApiResponse(code = 400, message = "Bad Request"),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })
    */
	@io.swagger.v3.oas.annotations.Operation(summary = "${approvalbarredfordlt.summary}", description="${approvalbarredfordlt.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ApprovalBarredforDltResponseVO.class))
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


	public ApprovalBarredforDltResponseVO saveBarforDlt(HttpServletResponse responseSwag,
			 @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = "Approval Barred for Dlt",required = true)
			@RequestBody ApprovalBarredForDltRequestVO requestVO) {
		final String methodName =  "saveBarforDlt";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered ");
		}
		
		 OAuthUser oAuthUser = null;
		 OAuthUserData oAuthUserData = null;
		 Connection con = null;MComConnectionI mcomCon = null;
		 boolean changeStatus=false;
		 ApprovalBarredforDltResponseVO response = new ApprovalBarredforDltResponseVO();
		 response.setService("BarredUserService");
		 response.setChangeStatus(changeStatus);
		 Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue
				        (PreferenceI.DEFAULT_LANGUAGE)), (String)
				        (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
		
		 try {
			    mcomCon = new MComConnection();
			    con=mcomCon.getConnection();
			    oAuthUser = new OAuthUser();
				oAuthUserData = new OAuthUserData();
			    oAuthUser.setData(oAuthUserData);
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
				if(BTSLUtil.isNullorEmpty(requestVO.getRequestType())) {
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CAN_NOT_NULL, PretupsI.RESPONSE_FAIL, new String[] {"Request type"}, null);
				}
				if(BTSLUtil.isNullorEmpty(requestVO.getAction())){
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CAN_NOT_NULL, PretupsI.RESPONSE_FAIL, new String[] {"Action type"}, null);
				}
				if(BTSLUtil.isNullorEmpty(requestVO.getLoginId())){
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CAN_NOT_NULL, PretupsI.RESPONSE_FAIL, new String[] {"Login type"}, null);
				}
				if(BTSLUtil.isNullorEmpty(requestVO.getRemarks())) {
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CAN_NOT_NULL, PretupsI.RESPONSE_FAIL, new String[] {"Remarks"}, null);
				}
				if(!(requestVO.getAction().equalsIgnoreCase(PretupsI.USER_REJECTED)||
						requestVO.getAction().equalsIgnoreCase(PretupsI.USER_APPROVE))){
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PROPERTY_INVALID, PretupsI.RESPONSE_FAIL, new String[] {"Login type"}, null);
				}
				if(requestVO.getRequestType().equalsIgnoreCase("BARREDAPPROVAL1") || requestVO.getRequestType().equalsIgnoreCase("BARREDAPPROVAL2")) {
	            	changeStatus=channelAdminUserHierarchyService.approvalOrRejectBarredUser(con, requestVO, oAuthUserData);
	            }
			     else {
	            	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PROPERTY_INVALID, PretupsI.RESPONSE_FAIL, new String[] {"Request type"}, null);
	            }
				response.setChangeStatus(changeStatus);
				response.setStatus(String.valueOf(HttpServletResponse.SC_OK));
				response.setMessage("sucessful");
				response.setMessageCode("200");
		  }catch (BTSLBaseException be) {
	        	response.setStatus(String.valueOf(HttpServletResponse.SC_BAD_REQUEST));
	        	responseSwag.setStatus(HttpServletResponse.SC_BAD_REQUEST);   
	            LOG.error(methodName, "BTSLBaseException " + be.getMessage());
	            try {
	                if (con != null) {
	                    con.rollback();
	                }
	            } catch (Exception e) {
	                LOG.errorTrace(methodName, e);
	            }
	            LOG.errorTrace(methodName, be);
	            response.setMessageCode(be.getMessageKey());
				response.setMessage(RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs()));
	               return response;
	        } catch (Exception e) {
	        	response.setStatus(String.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
	        	responseSwag.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);   
	        	try {
	                if (con != null) {
	                    con.rollback();
	                }
	            } catch (Exception ee) {
	                LOG.errorTrace(methodName, ee);
	            }
	            LOG.error(methodName, "Exception " + e.getMessage());
	            LOG.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "", "", "",
	                "Exception:" + e.getMessage());
	            response.setMessage(e.getMessage());
	            return response;
	        } finally {
	            LogFactory.printLog(methodName, " Exited ", LOG);
	            if(mcomCon != null){mcomCon.close("ChannelUserApprovalBarred#saveBarred");mcomCon=null;}
	        }
		   return response;
	}
	
	}