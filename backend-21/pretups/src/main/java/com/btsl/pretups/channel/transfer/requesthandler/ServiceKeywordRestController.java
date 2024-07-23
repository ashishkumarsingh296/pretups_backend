package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;


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
import com.btsl.common.ErrorMap;
import com.btsl.common.MasterErrorList;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.AddServiceKeywordReq;
import com.btsl.pretups.channel.transfer.businesslogic.AddServiceKeywordResp;
import com.btsl.pretups.channel.transfer.businesslogic.DeleteServiceKeywordResp;
import com.btsl.pretups.channel.transfer.businesslogic.GetGatewayListResp;
import com.btsl.pretups.channel.transfer.businesslogic.GetServiceKeywordListResp;
import com.btsl.pretups.channel.transfer.businesslogic.GetServiceTypeListResp;
import com.btsl.pretups.channel.transfer.businesslogic.ModifyServiceKeywordReq;
import com.btsl.pretups.channel.transfer.businesslogic.ModifyServiceKeywordResp;
import com.btsl.pretups.channel.transfer.businesslogic.ServiceKeywordResp;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.restapi.c2s.services.ServiceKeywordServiceI;
import com.web.pretups.gateway.businesslogic.MessageGatewayWebDAO;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${SenderReceiverDetailsController.name}", description = "${SenderReceiverDetailsController.desc}")//@Api(tags = "Service keyword operations", defaultValue = "Service keywod ops")
@RestController
@RequestMapping(value = "/v1/servicekeywordOps")
public class ServiceKeywordRestController {
//	protected final Log _log = LogFactory.getLog(getClass().getName());
public static final Log log = LogFactory.getLog(ServiceKeywordRestController.class.getName());
	StringBuilder loggerValue = new StringBuilder();

	@Autowired
	private ServiceKeywordServiceI serviceKeywordServiceI;
	
	private ServiceKeywordDAO serviceKeywordDAO = new ServiceKeywordDAO();


	
	
	@GetMapping(value= "/getserviceTypeList", produces = MediaType.APPLICATION_JSON)	
	@ResponseBody
	/*@ApiOperation(value = "Get service type  list",
	           response = GetServiceTypeListResp.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = GetServiceTypeListResp.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${getserviceTypeList.summary}", description="${getserviceTypeList.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = GetServiceTypeListResp.class))
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



	public GetServiceTypeListResp getserviceTypeList(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			 HttpServletResponse responseSwag
					)throws Exception{
		
		final String methodName = "getserviceTypeList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		Connection con = null;
		MComConnectionI mcomCon = null;
		
		UserDAO userDao = null;
		OAuthUser oAuthUser= null;
		OAuthUserData oAuthUserData =null;
		
		
		
		MasterErrorList masterError = null;
		ErrorMap errorMap = null;
		ArrayList<MasterErrorList> masterErrorLists = null;
		ChannelUserDAO channelUserDAO = null;
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);

		GetServiceTypeListResp getServiceTypeListResp = new GetServiceTypeListResp();
		try {
			errorMap = new ErrorMap();
			masterErrorLists = new ArrayList<>();
			
			channelUserDAO = new ChannelUserDAO();
			oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());
			userDao= new UserDAO();

			try {
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			} catch (Exception ex) {
				throw new BTSLBaseException(ServiceKeywordRestController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
			}

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			getServiceTypeListResp = serviceKeywordServiceI.getServiceTypeList(con);
			// final response message
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			getServiceTypeListResp.setStatus(success);
			getServiceTypeListResp.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(
				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),PretupsErrorCodesI.SUCCESS, null);
			getServiceTypeListResp.setMessage(resmsg);
			
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				getServiceTypeListResp.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				getServiceTypeListResp.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			}

			masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
			String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
			errorMap.setMasterErrorList(masterErrorLists);

			getServiceTypeListResp.setMessageCode(msg);
			getServiceTypeListResp.setErrorMap(errorMap);

		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			String fail = Integer.toString(PretupsI.RESPONSE_FAIL);
			getServiceTypeListResp.setStatus(fail);
			getServiceTypeListResp.setMessageCode("error.general.processing");
			getServiceTypeListResp.setMessage(
					"Due to some technical reasons, your request could not be processed at this time. Please try later");

			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("PretupsUIReportsController#fetchUserNameAutoSearch");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("fetchUserNameAutoSearch", " Exited ");
			}
		}

		return getServiceTypeListResp;
	}

	
	

	@GetMapping(value= "/getserviceKeywordbyServiceType", produces = MediaType.APPLICATION_JSON)	
	@ResponseBody
	/*@ApiOperation(value = "Get service keywords by service type",
	           response = GetServiceKeywordListResp.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = GetServiceKeywordListResp.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${getserviceKeywordbyServiceType.summary}", description="${getserviceKeywordbyServiceType.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = GetServiceKeywordListResp.class))
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



	public GetServiceKeywordListResp getserviceKeywordbyServiceType(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = SwaggerAPIDescriptionI.SERVICE_TYPE, example = "",required = true)
			@RequestParam("serviceType") String serviceType,
			 HttpServletResponse responseSwag
					)throws Exception{
		
		final String methodName = "getserviceTypeList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		Connection con = null;
		MComConnectionI mcomCon = null;
		UserDAO userDao = null;
		OAuthUser oAuthUser= null;
		OAuthUserData oAuthUserData =null;
		MasterErrorList masterError = null;
		ErrorMap errorMap = null;
		ArrayList<MasterErrorList> masterErrorLists = null;
		ChannelUserDAO channelUserDAO = null;
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);

		GetServiceKeywordListResp getServiceKeywordListResp = new  GetServiceKeywordListResp();
		try {
			errorMap = new ErrorMap();
			masterErrorLists = new ArrayList<>();
			
			channelUserDAO = new ChannelUserDAO();
			oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());
			userDao= new UserDAO();

			try {
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			} catch (Exception ex) {
				throw new BTSLBaseException(ServiceKeywordRestController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
			}

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
		
			getServiceKeywordListResp = serviceKeywordServiceI.searchServiceKeywordbyServiceType(con, serviceType);
			// final response message
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			getServiceKeywordListResp.setStatus(success);
			getServiceKeywordListResp.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(
				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),PretupsErrorCodesI.SUCCESS, null);
			getServiceKeywordListResp.setMessage(resmsg);
			
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				getServiceKeywordListResp.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				getServiceKeywordListResp.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			}

			masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
			String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
			errorMap.setMasterErrorList(masterErrorLists);

			getServiceKeywordListResp.setMessageCode(msg);
			getServiceKeywordListResp.setErrorMap(errorMap);

		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			String fail = Integer.toString(PretupsI.RESPONSE_FAIL);
			getServiceKeywordListResp.setStatus(fail);
			getServiceKeywordListResp.setMessageCode(PretupsErrorCodesI.GENERIC_SERVER_ERROR);
			getServiceKeywordListResp.setMessage(
					"Due to some technical reasons, your request could not be processed at this time. Please try later");

			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("ServiceKeywordRestController#getserviceKeywordbyServiceType");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("getserviceKeywordbyServiceType", " Exited ");
			}
		}

		return getServiceKeywordListResp;
	}

	

	@PostMapping(value = "/addServiceKeyword", produces = MediaType.APPLICATION_JSON)
	/*@ApiOperation(value = "Add service keyword", notes = ("Api Info:") + ("\n")
			+ ("1.serviceType  .") + ("\n")
			+ ("2. Service type  ") + ("\n")
			+ ("3. Message Gateway ") + ("\n")
			+ ("4. Receiver port. ") + ("\n")
			+ ("5. name  ") + ("\n")
			+ ("6. status.") + ("\n")
			+ ("6. menu.") + ("\n")
			+ ("7. subMenu") + ("\n")
			+ ("8. Allowed version ") 
			+ ("9. Keyword modified allowed yes or no ")
			+ ("\n"), response = AddServiceKeywordResp.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = AddServiceKeywordResp.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${addServiceKeyword.summary}", description="${addServiceKeyword.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = AddServiceKeywordResp.class))
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



	public AddServiceKeywordResp addserviceKeyword(@RequestBody AddServiceKeywordReq addServiceKeywordReq,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse responseSwag) { 
		Instant start = Instant.now();
		Instant end=null;
		boolean reportOffline=  (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.REPORT_OFFLINE);
		long stopTime=0l;
		final String methodName = "addserviceKeyword";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		AddServiceKeywordResp response = null;
		OAuthUser oAuthUser = null;
		MasterErrorList masterError = null;
		ErrorMap errorMap = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		ArrayList<MasterErrorList> masterErrorLists = null;
		
		ChannelUserDAO channelUserDAO = null;
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);
		Map<String,String> mp=null; 
		try {
			errorMap = new ErrorMap();
			masterErrorLists = new ArrayList<>();
			response = new AddServiceKeywordResp();
			channelUserDAO = new ChannelUserDAO();
			oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());

			try {
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			} catch (Exception ex) {
				throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
			}

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			String loginID =oAuthUser.getData().getLoginid();
	       UserDAO userDAO = new UserDAO();
			DateTimeFormatter patternDate = DateTimeFormatter.ofPattern((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATETIME_FORMAT));
			UserVO userVO = userDAO.loadUsersDetailsByLoginID(con,loginID);
			validateServicekeywordReq(con,addServiceKeywordReq,PretupsI.OPERATION_ADD);
			response = serviceKeywordServiceI.addServiceKeyword(con, addServiceKeywordReq, userVO, locale);
				
	         
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			}
			
			try {
				con.rollback();
				mcomCon.finalRollback();
			} catch (SQLException e1) {
				log.error(methodName, e1);
			}

			masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
			String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
			errorMap.setMasterErrorList(masterErrorLists);

			response.setMessageCode(be.getMessage());
			response.setMessage(msg);
			response.setErrorMap(errorMap);

		} catch (Exception e) {
			try {
				mcomCon.finalRollback();
			} catch (SQLException e1) {
				log.error(methodName, e1);
			}
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			String fail = Integer.toString(PretupsI.RESPONSE_FAIL);
			response.setStatus(fail);
			response.setMessageCode("error.general.processing");
			response.setMessage(
					"Due to some technical reasons, your request could not be processed at this time. Please try later");

			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("ServiceKeywordRestController#addServicekeyword");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("passbookView", " Exited ");
			}
		}
		
		return response;
		
	}
	
	
	private void validateServicekeywordReq(Connection con, AddServiceKeywordReq addServiceKeywordReq,String operation) throws BTSLBaseException {
		ServiceKeywordDAO serviceKeywordDAO = new ServiceKeywordDAO();
		
		 if(BTSLUtil.isNullString(addServiceKeywordReq.getServiceType())){
	    		throw new BTSLBaseException("ServiceKeywordRestController", "validateAddServicekeywordReq",
						PretupsErrorCodesI.SERVICE_TYPE_BLANK, 0, null); 

		 }
		 
		 if(!BTSLUtil.isNullString(addServiceKeywordReq.getServiceType())){
			if( !serviceKeywordDAO.validateServiceType(con, addServiceKeywordReq.getServiceType())) {
				throw new BTSLBaseException("ServiceKeywordRestController", "validateAddServicekeywordReq",
						PretupsErrorCodesI.SERVICE_TYPE_INVALID, 0, null);
		 }
			
		
			if(BTSLUtil.isNullString(addServiceKeywordReq.getKeyword())){
				throw new BTSLBaseException("ServiceKeywordRestController", "validateAddServicekeywordReq",
						PretupsErrorCodesI.SERVICE_KEYWORD_REQUIRED, 0, null);
			 }
			
			
			

			
			

		 }

		
		
		
	}
	


	@GetMapping(value= "/fetchserviceKeywordbyID", produces = MediaType.APPLICATION_JSON)	
	@ResponseBody
	/*@ApiOperation(value = "Fetch service keywords by service keyword ID",
	           response = ServiceKeywordResp.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = ServiceKeywordResp.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${fetchserviceKeywordbyID.summary}", description="${fetchserviceKeywordbyID.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ServiceKeywordResp.class))
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



	public ServiceKeywordResp fetchserviceKeywordbyID(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = SwaggerAPIDescriptionI.SERVICE_KEYWORD_ID, example = "",required = true)
			@RequestParam("serviceKeywordID") String serviceKeywordID,
			 HttpServletResponse responseSwag
					)throws Exception{
		
		final String methodName = "fetchserviceKeywordbyID";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		Connection con = null;
		MComConnectionI mcomCon = null;
		UserDAO userDao = null;
		OAuthUser oAuthUser= null;
		OAuthUserData oAuthUserData =null;
		MasterErrorList masterError = null;
		ErrorMap errorMap = null;
		ArrayList<MasterErrorList> masterErrorLists = null;
		ChannelUserDAO channelUserDAO = null;
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);

		ServiceKeywordResp serviceKeywordResp = new  ServiceKeywordResp();
		try {
			errorMap = new ErrorMap();
			masterErrorLists = new ArrayList<>();
			
			channelUserDAO = new ChannelUserDAO();
			oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());
			userDao= new UserDAO();

			try {
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			} catch (Exception ex) {
				throw new BTSLBaseException(ServiceKeywordRestController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
			}

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
		
			serviceKeywordResp = serviceKeywordServiceI.searchServiceKeywordbyID(con, serviceKeywordID);
			// final response message
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			serviceKeywordResp.setStatus(success);
			serviceKeywordResp.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(
				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),PretupsErrorCodesI.SUCCESS, null);
			serviceKeywordResp.setMessage(resmsg);
			
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				serviceKeywordResp.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				serviceKeywordResp.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			}

			masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
			String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
			errorMap.setMasterErrorList(masterErrorLists);

			serviceKeywordResp.setMessageCode(msg);
			serviceKeywordResp.setErrorMap(errorMap);

		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			String fail = Integer.toString(PretupsI.RESPONSE_FAIL);
			serviceKeywordResp.setStatus(fail);
			serviceKeywordResp.setMessageCode(PretupsErrorCodesI.GENERIC_SERVER_ERROR);
			serviceKeywordResp.setMessage(
					"Due to some technical reasons, your request could not be processed at this time. Please try later");

			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("ServiceKeywordRestController#getserviceKeywordbyServiceType");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("getserviceKeywordbyServiceType", " Exited ");
			}
		}

		return serviceKeywordResp;
	}



	
	@PostMapping(value = "/modifyServiceKeyword", produces = MediaType.APPLICATION_JSON)
	/*@ApiOperation(value = "Modify service keyword", notes = ("Api Info:") + ("\n")
			+ ("1.serviceType  .") + ("\n")
			+ ("2. Service type  ") + ("\n")
			+ ("3. Message Gateway ") + ("\n")
			+ ("4. Receiver port. ") + ("\n")
			+ ("5. name  ") + ("\n")
			+ ("6. status.") + ("\n")
			+ ("6. menu.") + ("\n")
			+ ("7. subMenu") + ("\n")
			+ ("8. Allowed version ") 
			+ ("9. Keyword modified allowed yes or no ")
			+ ("\n"), response = ModifyServiceKeywordResp.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = AddServiceKeywordResp.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${modifyServiceKeyword.summary}", description="${modifyServiceKeyword.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ModifyServiceKeywordResp.class))
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



	public ModifyServiceKeywordResp modifyserviceKeyword(@RequestBody ModifyServiceKeywordReq modifyServiceKeywordReq,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse responseSwag) { 
		Instant start = Instant.now();
		Instant end=null;
		boolean reportOffline=  (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.REPORT_OFFLINE);
		long stopTime=0l;
		final String methodName = "modifyserviceKeyword";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		ModifyServiceKeywordResp response = null;
		OAuthUser oAuthUser = null;
		MasterErrorList masterError = null;
		ErrorMap errorMap = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		ArrayList<MasterErrorList> masterErrorLists = null;
		
		ChannelUserDAO channelUserDAO = null;
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);
		Map<String,String> mp=null; 
		try {
			errorMap = new ErrorMap();
			masterErrorLists = new ArrayList<>();
			response = new ModifyServiceKeywordResp();
			channelUserDAO = new ChannelUserDAO();
			oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());

			try {
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			} catch (Exception ex) {
				throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
			}

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			String loginID =oAuthUser.getData().getLoginid();
	       UserDAO userDAO = new UserDAO();
			DateTimeFormatter patternDate = DateTimeFormatter.ofPattern((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATETIME_FORMAT));
			UserVO userVO = userDAO.loadUsersDetailsByLoginID(con,loginID);
			validateServicekeywordReq(con,modifyServiceKeywordReq,PretupsI.OPERATION_EDIT);
			response = serviceKeywordServiceI.modifyServiceKeyword(con, modifyServiceKeywordReq, userVO, locale);
				
	         
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			}
			 try {
				if(!con.isClosed()) {
					con.rollback();
				 }
			} catch (SQLException e) {
				log.error(methodName, "SQLException:e=" + e);
			}
					masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
			String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
			errorMap.setMasterErrorList(masterErrorLists);

			response.setMessageCode(be.getMessage());
			response.setMessage(msg);
			response.setErrorMap(errorMap);

		} catch (Exception e) {
			
			try {
				if(!con.isClosed()) {
					con.rollback();
				}
			} catch (SQLException se) {
				log.error(methodName, "SQLException:e=" + se);
			}
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			String fail = Integer.toString(PretupsI.RESPONSE_FAIL);
			response.setStatus(fail);
			response.setMessageCode("error.general.processing");
			response.setMessage(
					"Due to some technical reasons, your request could not be processed at this time. Please try later");

			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
	

		} finally {
			if (mcomCon != null) {
				mcomCon.close("ServiceKeywordRestController#addServicekeyword");
				mcomCon = null;
			}
			
			if (log.isDebugEnabled()) {
				log.debug("passbookView", " Exited ");
			}
		}
		
		return response;
		
	}
	
	

	
	
	
	 
	
	
	@GetMapping(value= "/deleteserviceKeywordbyID", produces = MediaType.APPLICATION_JSON)	
	@ResponseBody
	/*@ApiOperation(value = "Delete service keywords by service keyword ID",
	           response = ServiceKeywordResp.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = ServiceKeywordResp.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${deleteserviceKeywordbyID.summary}", description="${deleteserviceKeywordbyID.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = DeleteServiceKeywordResp.class))
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



	public DeleteServiceKeywordResp deleteserviceKeywordbyID(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = SwaggerAPIDescriptionI.SERVICE_KEYWORD_ID, example = "",required = true)
			@RequestParam("serviceKeywordID") String serviceKeywordID,
			 HttpServletResponse responseSwag
					)throws Exception{
		
		final String methodName = "deleteserviceKeywordbyID";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		Connection con = null;
		MComConnectionI mcomCon = null;
		UserDAO userDao = null;
		OAuthUser oAuthUser= null;
		OAuthUserData oAuthUserData =null;
		MasterErrorList masterError = null;
		ErrorMap errorMap = null;
		ArrayList<MasterErrorList> masterErrorLists = null;
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);

		DeleteServiceKeywordResp serviceKeywordResp = new  DeleteServiceKeywordResp();
		try {
			errorMap = new ErrorMap();
			masterErrorLists = new ArrayList<>();
			oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());
			userDao= new UserDAO();

			try {
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			} catch (Exception ex) {
				throw new BTSLBaseException(ServiceKeywordRestController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
			} 

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			String loginID = oAuthUser.getData().getLoginid();
			UserDAO userDAO = new UserDAO();
			UserVO userVO =userDAO.loadUsersDetailsByLoginID(con, loginID);
			serviceKeywordResp = serviceKeywordServiceI.deleteServiceKeywordbyID(con, serviceKeywordID,userVO);
			
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				serviceKeywordResp.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				serviceKeywordResp.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			}

			masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
			String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
			errorMap.setMasterErrorList(masterErrorLists);

			serviceKeywordResp.setMessageCode(msg);
			serviceKeywordResp.setErrorMap(errorMap);

		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			String fail = Integer.toString(PretupsI.RESPONSE_FAIL);
			serviceKeywordResp.setStatus(fail);
			serviceKeywordResp.setMessageCode(PretupsErrorCodesI.GENERIC_SERVER_ERROR);
			serviceKeywordResp.setMessage(
					"Due to some technical reasons, your request could not be processed at this time. Please try later");

			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("ServiceKeywordRestController#getserviceKeywordbyServiceType");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("getserviceKeywordbyServiceType", " Exited ");
			}
		}

		return serviceKeywordResp;
	}
	
	
	
	
	@GetMapping(value = "/getMessageGatewayList", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Get message gateway  list", response = GetGatewayListResp.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = GetGatewayListResp.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${getMessageGatewayList.summary}", description="${getMessageGatewayList.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = GetGatewayListResp.class))
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



	public GetGatewayListResp getMessageGatewayList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse responseSwag) throws Exception {

		final String methodName = "getMessageGatewayList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		Connection con = null;
		MComConnectionI mcomCon = null;

		UserDAO userDao = null;
		OAuthUser oAuthUser = null;
		OAuthUserData oAuthUserData = null;

		MasterErrorList masterError = null;
		ErrorMap errorMap = null;
		ArrayList<MasterErrorList> masterErrorLists = null;
		ChannelUserDAO channelUserDAO = null;
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);

		GetGatewayListResp getGatewayListResp = new GetGatewayListResp();
		MessageGatewayWebDAO messageGatewayWebDAO = new MessageGatewayWebDAO();
		try {
			errorMap = new ErrorMap();
			masterErrorLists = new ArrayList<>();

			channelUserDAO = new ChannelUserDAO();
			oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());
			userDao = new UserDAO();

			try {
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			} catch (Exception ex) {
				throw new BTSLBaseException(ServiceKeywordRestController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
			}

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			ArrayList listofGateways = messageGatewayWebDAO.loadGatewayTypeList(con, PretupsI.GATEWAY_DISPLAY_ALLOW_YES,
					null);
			if (BTSLUtil.isNullOrEmptyList(listofGateways)) {
				throw new BTSLBaseException(this, methodName, "error.noMessagegatewaydata");
			}

			getGatewayListResp.setListServiceListObj(listofGateways);
			// final response message
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			getGatewayListResp.setStatus(success);
			getGatewayListResp.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE),
							(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
					PretupsErrorCodesI.SUCCESS, null);
			getGatewayListResp.setMessage(resmsg);

		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				getGatewayListResp.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				getGatewayListResp.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			}

			masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
			String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
			errorMap.setMasterErrorList(masterErrorLists);

			getGatewayListResp.setMessageCode(msg);
			getGatewayListResp.setErrorMap(errorMap);

		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			String fail = Integer.toString(PretupsI.RESPONSE_FAIL);
			getGatewayListResp.setStatus(fail);
			getGatewayListResp.setMessageCode("error.general.processing");
			getGatewayListResp.setMessage(
					"Due to some technical reasons, your request could not be processed at this time. Please try later");

			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("ServicekeywordRestController#getMessageGatewayList");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("getMessageGatewayList", " Exited ");
			}
		}

		return getGatewayListResp;
	}	

	
	

}

