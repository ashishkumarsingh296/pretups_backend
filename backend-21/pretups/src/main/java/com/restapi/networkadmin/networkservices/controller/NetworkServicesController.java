package com.restapi.networkadmin.networkservices.controller;

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
import com.btsl.common.BaseResponse;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.OAuthenticationUtil;
import com.restapi.networkadmin.networkservices.requestVO.UpdateNetworkServicesRequestVO;
import com.restapi.networkadmin.networkservices.responseVO.NetworkServicesDataResponseVO;
import com.restapi.networkadmin.networkservices.responseVO.ServiceTypeListResponseVO;
import com.restapi.networkadmin.networkservices.service.NetworkServicesServiceI;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${NetworkServicesController.name}", description = "${NetworkServicesController.desc}")//@Api(tags="Network Admin")
@RestController
@RequestMapping(value = "/v1/networkadmin/networkServices")
public class NetworkServicesController {
	
	public static final Log log = LogFactory.getLog(NetworkServicesController.class.getName());
	
	@Autowired
	NetworkServicesServiceI networkServicesServiceI;
	
	
	@GetMapping(value= "/loadServiceTypeList", produces = MediaType.APPLICATION_JSON)	
	@ResponseBody
	/*@ApiOperation(value = "This method loads list of modules form the cache and load the list of service type which are availabe in the network with the EXTERNAL INTERFACE is equals Y.",
	           response = ServiceTypeListResponseVO.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = ServiceTypeListResponseVO.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${loadServiceTypeList.summary}", description="${loadServiceTypeList.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ServiceTypeListResponseVO.class))
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

	public ServiceTypeListResponseVO loadServiceTypeList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest
			) throws Exception {
		
		
		final String methodName = "loadServiceTypeList";
		if(log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		Connection con = null;
		MComConnectionI mcomCon = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

		UserVO userVO = null;
		UserDAO userDAO = null;
		
		ServiceTypeListResponseVO response = new ServiceTypeListResponseVO();
		
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
			OAuthUser oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, response1);
			
			userVO = new UserVO();
			userDAO = new UserDAO();
			userVO  = userDAO.loadUsersDetails(con, oAuthUser.getData().getMsisdn());
			
			response = networkServicesServiceI.loadServiceTypeList(headers,response1,con,mcomCon,locale,userVO,response);
		}
		catch (BTSLBaseException be) {
			log.error("", "Exceptin:e=" + be);
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
	
	
	
	
	
	@GetMapping(value= "/loadNetworkServicesData", produces = MediaType.APPLICATION_JSON)	
	@ResponseBody
	/*@ApiOperation(value = "This method loads the data of the network services form the database .\r\n"
			+ "    Based on the previous selection of the module and service type and also\r\n"
			+ "    gets the description of the selected\r\n"
			+ "    module and service type", 
	           response = NetworkServicesDataResponseVO.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = NetworkServicesDataResponseVO.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${loadNetworkServicesData.summary}", description="${loadNetworkServicesData.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = NetworkServicesDataResponseVO.class))
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

	public NetworkServicesDataResponseVO loadNetworkServicesData(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest,
			@Parameter(description = "moduleCode", required = true) @RequestParam("moduleCode") String moduleCode,
			@Parameter(description = "serviceTypeCode", required = true) @RequestParam("serviceTypeCode") String serviceTypeCode
			) throws Exception {
		
		
		final String methodName = "loadNetworkServicesData";
		if(log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		Connection con = null;
		MComConnectionI mcomCon = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

		UserVO userVO = null;
		UserDAO userDAO = null;
		
		NetworkServicesDataResponseVO response = new NetworkServicesDataResponseVO();
		
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
			OAuthUser oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, response1);
			
			userVO = new UserVO();
			userDAO = new UserDAO();
			userVO  = userDAO.loadUsersDetails(con, oAuthUser.getData().getMsisdn());
			
			response = networkServicesServiceI.loadNetworkServicesData(headers,response1,con,mcomCon,locale,userVO,response,moduleCode,serviceTypeCode);
		}
		catch (BTSLBaseException be) {
			log.error("", "Exceptin:e=" + be);
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
	
	
	
	
	
	
	@PostMapping(value= "/updateNetworkServices", produces = MediaType.APPLICATION_JSON)	
	@ResponseBody
	/*@ApiOperation(value = "This is the final method of the NETWORK SERVICES Module. which calles the\r\n"
			+ "   DAO method for the actual updation", 
	           response = ServiceTypeListResponseVO.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = BaseResponse.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })
	*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${updateNetworkServices.summary}", description="${updateNetworkServices.description}",

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

	public BaseResponse updateNetworkServices(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest,
			@RequestBody UpdateNetworkServicesRequestVO updateNetworkServicesRequestVO
			) throws Exception {
		
		
		final String methodName = "updateNetworkServices";
		if(log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		Connection con = null;
		MComConnectionI mcomCon = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

		UserVO userVO = null;
		UserDAO userDAO = null;
		
		BaseResponse response = new BaseResponse();
		
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
			OAuthUser oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, response1);
			
			userVO = new UserVO();
			userDAO = new UserDAO();
			//userVO  = userDAO.loadUsersDetails(con, oAuthUser.getData().getMsisdn());
			userVO  = userDAO.loadAllUserDetailsByLoginID(con, oAuthUser.getData().getLoginid());
			
			response = networkServicesServiceI.updateNetworkServices(headers,response1,con,mcomCon,locale,userVO,response,updateNetworkServicesRequestVO);
		}
		catch (BTSLBaseException be) {
			log.error("", "Exceptin:e=" + be);
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
		}
		catch (Exception e) {
			log.error(methodName, "Exception:e=" + e);
			log.errorTrace(methodName, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.SERVICES_STATUS_UPDATE_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.SERVICES_STATUS_UPDATE_FAIL);
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
	

}
