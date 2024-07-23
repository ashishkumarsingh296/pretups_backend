package com.restapi.networkadmin.networkinterfaces;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.HttpStatus;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.interfaces.businesslogic.InterfaceDAO;
import com.btsl.pretups.interfaces.businesslogic.InterfaceNetworkMappingDAO;
import com.btsl.pretups.interfaces.businesslogic.InterfaceNetworkMappingVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.OAuthenticationUtil;

import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${NetworkInterfacesController.name}", description = "${NetworkInterfacesController.desc}")//@Api(tags="Network Interfaces")
@RestController
@RequestMapping(value = "/v1/networkadmin")
public class NetworkInterfacesController {
	
	public static final Log log = LogFactory.getLog(NetworkInterfacesController.class.getName());
	public static final String classname = "NetworkInterfacesController";
	
	private NetworkInterfaceService networkInterfaceService;
	
	
	
	public NetworkInterfacesController(NetworkInterfaceService networkInterfaceService) {
		super();
		this.networkInterfaceService = networkInterfaceService;
	}



	@GetMapping(value = "/networkInterfaceList", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "List of Network Interfaces", response = NetworkInterfacesVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = NetworkInterfacesVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${networkInterfaceList.summary}", description="${networkInterfaceList.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = NetworkInterfacesVO.class))
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


	public NetworkInterfacesVO getNetworkInterfaceList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) throws Exception {
		
		final String methodName = "getNetworkInterfaceList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		String loginID = null;
		NetworkInterfacesVO networkInterfacesVO = new NetworkInterfacesVO() ;
		try {

			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, httpServletResponse);
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			loginID = OAuthUserData.getData().getLoginid();
			UserVO userVO = new UserDAO().loadAllUserDetailsByLoginID(con, OAuthUserData.getData().getLoginid());
			networkInterfacesVO = this.networkInterfaceService.loadInterfaceNetworkMappingList(userVO, httpServletRequest, httpServletResponse, locale);
			
		}
		catch(BTSLBaseException be) {
			log.error("", "Exceptin:e=" + be);
			log.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			networkInterfacesVO.setMessageCode(be.getMessageKey());
			networkInterfacesVO.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				httpServletResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
				networkInterfacesVO.setStatus(HttpStatus.SC_UNAUTHORIZED);

			} else {
				httpServletResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
				networkInterfacesVO.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		}
		catch (Exception e) {
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            String msg = RestAPIStringParser.getMessage(locale, e.getMessage(), null);
            networkInterfacesVO.setStatus(HttpStatus.SC_BAD_REQUEST);
			networkInterfacesVO.setMessageCode(e.getMessage());
			networkInterfacesVO.setMessage(msg);
			httpServletResponse.setStatus(HttpStatus.SC_BAD_REQUEST);

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

		return networkInterfacesVO;

	}
	
	@GetMapping(value = "/interfaceCategoryListTemplate", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "List all Interface categories", response = NetworkCategoryListVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = NetworkInterfacesVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${interfaceCategoryListTemplate.summary}", description="${interfaceCategoryListTemplate.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = NetworkCategoryListVO.class))
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


	public NetworkCategoryListVO getInterfaceCategoryList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) throws Exception {
		
		final String methodName = "getInterfaceCategoryList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		ArrayList categoryList =new ArrayList<>();
		Connection con = null;
		MComConnectionI mcomCon = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		String loginID = null;
		NetworkCategoryListVO networkCategoryListVO = new NetworkCategoryListVO() ;
		try {

			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, httpServletResponse);
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			loginID = OAuthUserData.getData().getLoginid();
			UserVO userVO = new UserDAO().loadAllUserDetailsByLoginID(con, OAuthUserData.getData().getLoginid());
			categoryList = LookupsCache.loadLookupDropDown(PretupsI.INTERFACE_CATEGORY, true);
			InterfaceDAO interfaceDAO = new InterfaceDAO();
			ArrayList interfaceList =new ArrayList<>();
			interfaceList = interfaceDAO.loadInterfaceList(con, userVO.getNetworkID(), PretupsI.INTERFACE_CATEGORY, null);
			networkCategoryListVO.setInterfaceNameList(interfaceList);
			networkCategoryListVO.setCategoryList(categoryList);
			networkCategoryListVO.setStatus(HttpStatus.SC_OK);
			networkCategoryListVO.setStatus(HttpStatus.SC_OK);
			networkCategoryListVO.setMessage("success");
		}
		catch(BTSLBaseException be) {
			log.error("", "Exceptin:e=" + be);
			log.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			networkCategoryListVO.setMessageCode(be.getMessageKey());
			networkCategoryListVO.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				httpServletResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
				networkCategoryListVO.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				httpServletResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
				networkCategoryListVO.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		}
		catch (Exception e) {
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            String msg = RestAPIStringParser.getMessage(locale, e.getMessage(), null);
            networkCategoryListVO.setStatus(HttpStatus.SC_BAD_REQUEST);
            networkCategoryListVO.setMessageCode(e.getMessage());
            networkCategoryListVO.setMessage(msg);
			httpServletResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
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

		return networkCategoryListVO;

	}
	

	@GetMapping(value = "/interfaceNamesList", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "List all Interface Names", response = InterfaceNamesVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = NetworkInterfacesVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${interfaceNamesList.summary}", description="${interfaceNamesList.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = InterfaceNamesVO.class))
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


	public InterfaceNamesVO getInterfaceNameList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) throws Exception {
		
		final String methodName = "getInterfaceNameList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		ArrayList interfaceList =new ArrayList<>();
		Connection con = null;
		MComConnectionI mcomCon = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		String loginID = null;
		InterfaceNamesVO interfaceNamesVO = new InterfaceNamesVO() ;
		try {

			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, httpServletResponse);
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			loginID = OAuthUserData.getData().getLoginid();
			UserVO userVO = new UserDAO().loadAllUserDetailsByLoginID(con, OAuthUserData.getData().getLoginid());
			InterfaceDAO interfaceDAO = new InterfaceDAO();
			interfaceList = interfaceDAO.loadInterfaceList(con, userVO.getNetworkID(), PretupsI.INTERFACE_CATEGORY, null);
			interfaceNamesVO.setInterfaceNamesList(interfaceList);
			interfaceNamesVO.setStatus(HttpStatus.SC_OK);
			interfaceNamesVO.setStatus(HttpStatus.SC_OK);
			interfaceNamesVO.setMessage("success");
		}
		catch(BTSLBaseException be) {
			log.error("", "Exceptin:e=" + be);
			log.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			interfaceNamesVO.setMessageCode(be.getMessageKey());
			interfaceNamesVO.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				httpServletResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
				interfaceNamesVO.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				httpServletResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
				interfaceNamesVO.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		}
		catch (Exception e) {
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            String msg = RestAPIStringParser.getMessage(locale, e.getMessage(), null);
            interfaceNamesVO.setStatus(HttpStatus.SC_BAD_REQUEST);
            interfaceNamesVO.setMessageCode(e.getMessage());
            interfaceNamesVO.setMessage(msg);
			httpServletResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
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

		return interfaceNamesVO;

	}
	
	
	@GetMapping(value = "/deletNetworkInterface/networkInterfaceId") //, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON
	@ResponseBody
	/*@ApiOperation(value = "delete network interface", response = InterfaceNamesVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = NetworkInterfacesVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${deletNetworkInterface.summary}", description="${deletNetworkInterface.description}",

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


	public BaseResponse deleteNetworkInterface(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest,
			@RequestParam("networkInterfaceId")  String networkInterfaceId) throws Exception {
		
		final String methodName = "deleteNetworkInterface";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		BaseResponse responseVO = new BaseResponse();
		Connection con = null;
		MComConnectionI mcomCon = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		String loginID = null;
		InterfaceNamesVO interfaceNamesVO = new InterfaceNamesVO() ;
		try {

			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, httpServletResponse);
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			loginID = OAuthUserData.getData().getLoginid();
			UserVO userVO = new UserDAO().loadAllUserDetailsByLoginID(con, OAuthUserData.getData().getLoginid());
			responseVO = this.networkInterfaceService.deleteNetworkInterfaces(userVO, httpServletRequest, httpServletResponse, networkInterfaceId, locale);
		}
		catch(BTSLBaseException be) {
			log.error("", "Exceptin:e=" + be);
			log.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			interfaceNamesVO.setMessageCode(be.getMessageKey());;
			interfaceNamesVO.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				httpServletResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
				interfaceNamesVO.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				httpServletResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
				interfaceNamesVO.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		}
		catch (Exception e) {
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            String msg = RestAPIStringParser.getMessage(locale, e.getMessage(), null);
            interfaceNamesVO.setStatus(HttpStatus.SC_BAD_REQUEST);
            interfaceNamesVO.setMessageCode(e.getMessage());
            interfaceNamesVO.setMessage(msg);
			httpServletResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
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

		return responseVO;

	}
	
	//save
	
	@PostMapping(value = "/addNetworkInterface", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "save interface", response = InterfaceNamesVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = NetworkInterfacesVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${addNetworkInterface.summary}", description="${addNetworkInterface.description}",

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


	public BaseResponse addNetworkInterface(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest, 
			@RequestBody NetworkInterfaceRequestVO requestVO) throws Exception {
		
		final String methodName = "addNetworkInterface";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		BaseResponse responseVO = new BaseResponse();
		Connection con = null;
		MComConnectionI mcomCon = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		String loginID = null;
		InterfaceNamesVO interfaceNamesVO = new InterfaceNamesVO() ;
		try {

			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, httpServletResponse);
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			loginID = OAuthUserData.getData().getLoginid();
			UserVO userVO = new UserDAO().loadAllUserDetailsByLoginID(con, OAuthUserData.getData().getLoginid());
			responseVO = this.networkInterfaceService.addNetworkInterfaceInterfaces(userVO, httpServletRequest, httpServletResponse, requestVO, locale);
		}
		catch(BTSLBaseException be) {
			log.error("", "Exceptin:e=" + be);
			log.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			interfaceNamesVO.setMessageCode(be.getMessageKey());
			interfaceNamesVO.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				httpServletResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
				interfaceNamesVO.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				httpServletResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
				interfaceNamesVO.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		}
		catch (Exception e) {
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            String msg = RestAPIStringParser.getMessage(locale, e.getMessage(), null);
            interfaceNamesVO.setStatus(HttpStatus.SC_BAD_REQUEST);
            interfaceNamesVO.setMessageCode(e.getMessage());
            interfaceNamesVO.setMessage(msg);
			httpServletResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		finally {

			if (mcomCon != null) {
				mcomCon.close(methodName);
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting:=");
			}

		}

		return responseVO;

	}
	//Get one
	@GetMapping(value = "/networkInterface/networkInterfaceId", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "List all Interface Names", response = InterfaceNamesVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = NetworkInterfacesVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${networkInterface.summary}", description="${networkInterface.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = InterfaceNetworkMappingVO.class))
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


	public InterfaceNetworkMappingVO getNetworkInterface(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest, 
			@RequestParam("networkInterfaceId")  String networkInterfaceId) throws Exception {
		
		final String methodName = "getNetworkInterface";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		NetworkInterfaceRequestVO networkInterfaceRequestVO =new NetworkInterfaceRequestVO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		InterfaceNetworkMappingVO interfaceNetworkMappingVO = new InterfaceNetworkMappingVO() ;
		try {

			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, httpServletResponse);
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			UserVO userVO = new UserDAO().loadAllUserDetailsByLoginID(con, OAuthUserData.getData().getLoginid());
			InterfaceNetworkMappingDAO interfaceNetworkMappingDAO = new InterfaceNetworkMappingDAO();
			interfaceNetworkMappingVO =interfaceNetworkMappingDAO.loadInterfaceNetworkMappingById(con, userVO.getNetworkID(), PretupsI.INTERFACE_CATEGORY, networkInterfaceId);
			
		}
		catch(BTSLBaseException be) {
			log.error("", "Exceptin:e=" + be);
			log.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				httpServletResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				httpServletResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		}
		catch (Exception e) {
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            String msg = RestAPIStringParser.getMessage(locale, e.getMessage(), null);
            httpServletResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
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

		return interfaceNetworkMappingVO;

	}

	
	@PostMapping(value = "/modifyNetworkInterface", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "save interface", response = InterfaceNamesVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = NetworkInterfacesVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${modifyNetworkInterface.summary}", description="${modifyNetworkInterface.description}",

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


	public BaseResponse modifyNetworkInterface(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest, 
			@RequestBody NetworkInterfaceRequestVO requestVO) throws Exception {
		
		final String methodName = "modifyNetworkInterface";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		BaseResponse responseVO = new BaseResponse();
		Connection con = null;
		MComConnectionI mcomCon = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		String loginID = null;
		InterfaceNamesVO interfaceNamesVO = new InterfaceNamesVO() ;
		try {

			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, httpServletResponse);
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			loginID = OAuthUserData.getData().getLoginid();
			UserVO userVO = new UserDAO().loadAllUserDetailsByLoginID(con, OAuthUserData.getData().getLoginid());
			responseVO = this.networkInterfaceService.modifyNetworkInterfaceInterfaces(userVO, httpServletRequest, httpServletResponse, requestVO, locale);
			
		}
		catch(BTSLBaseException be) {
			log.error("", "Exceptin:e=" + be);
			log.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			interfaceNamesVO.setMessageCode(be.getMessageKey());
			interfaceNamesVO.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				httpServletResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
				interfaceNamesVO.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				httpServletResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
				interfaceNamesVO.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		}
		catch (Exception e) {
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            String msg = RestAPIStringParser.getMessage(locale, e.getMessage(), null);
			interfaceNamesVO.setStatus(HttpStatus.SC_BAD_REQUEST);
            interfaceNamesVO.setMessageCode(e.getMessage());
            interfaceNamesVO.setMessage(msg);
			httpServletResponse.setStatus(HttpStatus.SC_BAD_REQUEST);            
		}
		finally {

			if (mcomCon != null) {
				mcomCon.close(methodName);
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting:=");
			}

		}

		return responseVO;

	}
}
