package com.restapi.networkadmin.cardgroup.controller;

import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import com.btsl.pretups.cardgroup.businesslogic.CardGroupBL;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDetailsVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferItemVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.restapi.networkadmin.cardgroup.requestVO.*;
import com.restapi.networkadmin.cardgroup.responseVO.*;
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
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.OAuthenticationUtil;
import com.restapi.networkadmin.cardgroup.serviceI.C2SCardGroupServiceI;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${C2SCardGroupController.name}", description = "${C2SCardGroupController.desc}")//@Api(tags = "Network Admin")
@RestController
@RequestMapping(value = "/v1/networkadmin/c2scardgroup/")
public class C2SCardGroupController {
	
	@Autowired
	private C2SCardGroupServiceI service;
	public static final Log LOG = LogFactory.getLog(C2SCardGroupController.class.getName());
	public static final String className = "C2SCardGroupController";

	@GetMapping(value = "/loadC2SCardGroupServices", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Load C2S CardGroup Services", response =LoadC2SCardGroupResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = LoadC2SCardGroupResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${loadC2SCardGroupServices.summary}", description="${loadC2SCardGroupServices.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = LoadC2SCardGroupResponseVO.class))
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


	public LoadC2SCardGroupResponseVO loadC2SCardGroupServices(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest) throws Exception {

    final String methodName = "loadC2SCardGroupServices";
    if (LOG.isDebugEnabled()) {
        LOG.debug(methodName, "Entered");
    }
    
    Connection con = null;MComConnectionI mcomCon = null;
    UserVO userVO = null;
	UserDAO userDAO = null;
	Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
	LoadC2SCardGroupResponseVO response = new LoadC2SCardGroupResponseVO();
	try {

		/*
		 * Authentication
		 * 
		 * @throws BTSLBaseException
		 */

		OAuthUser OAuthUserData = new OAuthUser();
		OAuthUserData.setData(new OAuthUserData());
		OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
		mcomCon = new MComConnection();
		con = mcomCon.getConnection();
		userDAO = new UserDAO();
	    String loginID = OAuthUserData.getData().getLoginid();
	    userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
	    response = service.loadServiceAndSubServiceList(con, userVO.getNetworkID());
	    response.setStatus((HttpStatus.SC_OK));
		String resmsg = RestAPIStringParser.getMessage(locale,
				PretupsErrorCodesI.SUCCESSFULLY_LOAD_C2S_CARD_GROUP_SERVICE, null);
		response.setMessage(resmsg);
		response.setMessageCode(PretupsErrorCodesI.SUCCESSFULLY_LOAD_C2S_CARD_GROUP_SERVICE);
    
       
    } catch (BTSLBaseException e) {
        LOG.error(methodName, "Exceptin:e=" + e);
        LOG.errorTrace(methodName, e);
        String msg = RestAPIStringParser.getMessage(locale, e.getMessageKey(), null);
		response.setMessageCode(e.getMessageKey());
		response.setMessage(msg);

		if (Arrays.asList(PretupsI.OAUTHCODES).contains(e.getMessage())) {
			response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
			response.setStatus(HttpStatus.SC_UNAUTHORIZED);
		} else {
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

    }  catch (Exception e) {
        LOG.error(methodName, "Exceptin:e=" + e);
        LOG.errorTrace(methodName, e);
        response1.setStatus(HttpStatus.SC_BAD_REQUEST);
        response.setStatus(HttpStatus.SC_BAD_REQUEST);
        response.setMessageCode(PretupsErrorCodesI.FAILED_LOAD_C2S_CARD_GROUP_SERVICE);
    } finally {
    	
    	if(mcomCon != null){
    		mcomCon.close("AddC2SCardGroupController#loadC2SCardGroupServices");
    		mcomCon=null;}
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting forward=" );
        }
    }
		return response;
	}
	
	@GetMapping(value = "/loadC2SCardGroupAmountDetailsList", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Load C2SCardGroup AmountDetails List", response =LoadC2SCardGroupListResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = LoadC2SCardGroupResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${loadC2SCardGroupAmountDetailsList.summary}", description="${loadC2SCardGroupAmountDetailsList.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = LoadC2SCardGroupListResponseVO.class))
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


	public LoadC2SCardGroupListResponseVO loadC2SCardGroupList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest,
			@Parameter(description = "subService", required = true) @RequestParam("subService") String subService
			) throws Exception {

	
        final String methodName = "loadC2SCardGroupList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }

       
        Connection con = null;MComConnectionI mcomCon = null;
        Date currentDate = null;
        LoadC2SCardGroupListResponseVO response = new LoadC2SCardGroupListResponseVO();
        UserVO userVO = null;
    	UserDAO userDAO = null;
    	Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
    	
    	try {

    		/*
    		 * Authentication
    		 * 
    		 * @throws BTSLBaseException
    		 */

    		OAuthUser OAuthUserData = new OAuthUser();
    		OAuthUserData.setData(new OAuthUserData());
    		OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
    		mcomCon = new MComConnection();
    		con = mcomCon.getConnection();
    		userDAO = new UserDAO();
    	    String loginID = OAuthUserData.getData().getLoginid();
    	    userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
    	    response = service.loadC2SCardGroupList(con,userVO.getNetworkID(), subService);
    	    response.setStatus((HttpStatus.SC_OK));
    		String resmsg = RestAPIStringParser.getMessage(locale,
    				PretupsErrorCodesI.SUCCESSFULLY_LOAD_C2S_CARD_GROUP_LIST, null);
    		response.setMessage(resmsg);
    		response.setMessageCode(PretupsErrorCodesI.SUCCESSFULLY_LOAD_C2S_CARD_GROUP_LIST);
                   
        } catch (BTSLBaseException e) {
            LOG.error(methodName, "Exceptin:e=" + e);
            LOG.errorTrace(methodName, e);
            String msg = RestAPIStringParser.getMessage(locale, e.getMessageKey(), null);
    		response.setMessageCode(e.getMessageKey());
    		response.setMessage(msg);

    		if (Arrays.asList(PretupsI.OAUTHCODES).contains(e.getMessage())) {
    			response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
    			response.setStatus(HttpStatus.SC_UNAUTHORIZED);
    		} else {
    			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
    			response.setStatus(HttpStatus.SC_BAD_REQUEST);
    		}

        }  catch (Exception e) {
            LOG.error(methodName, "Exceptin:e=" + e);
            LOG.errorTrace(methodName, e);
            response1.setStatus(HttpStatus.SC_BAD_REQUEST);
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            response.setMessageCode(PretupsErrorCodesI.FAILED_LOAD_C2S_CARD_GROUP_LIST);
        } finally {
        	
        	if(mcomCon != null){
        		mcomCon.close("AddC2SCardGroupController#loadC2SCardGrouplist");
        		mcomCon=null;}
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting forward=" );
            }

           }
    	 return response;

	}
	
	
	@PostMapping(value = "/addTempC2SCardGroupList", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "add Temp C2S CardGroup List", response =AddTempCardGroupListResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = AddTempCardGroupListResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${addTempC2SCardGroupList.summary}", description="${addTempC2SCardGroupList.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = AddTempCardGroupListResponseVO.class))
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

	public AddTempCardGroupListResponseVO  loadTempC2SCardGroupList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest,
			@RequestBody  CardGroupDetailsRequestVO requestVO
			
			
			) throws Exception {
       
		final String methodName = "loadTempC2SCardGroupList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }

       
        Connection con = null;MComConnectionI mcomCon = null;
        Date currentDate = null;
        AddTempCardGroupListResponseVO response = new AddTempCardGroupListResponseVO();
        UserVO userVO = null;
    	UserDAO userDAO = null;
    	Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
    	
    	try {

    		/*
    		 * Authentication
    		 * 
    		 * @throws BTSLBaseException
    		 */

    		OAuthUser OAuthUserData = new OAuthUser();
    		OAuthUserData.setData(new OAuthUserData());
    		OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
    		mcomCon = new MComConnection();
    		con = mcomCon.getConnection();
    		userDAO = new UserDAO();
    	    String loginID = OAuthUserData.getData().getLoginid();
    	    userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
    	    response = service.addTempList(con,httpServletRequest, userVO, requestVO);
    	    response.setStatus((HttpStatus.SC_OK));
    		String resmsg = RestAPIStringParser.getMessage(locale,
    				PretupsErrorCodesI.LOAD_ADD_TEMP_CARD_GROUP_LIST_SUCCESSFULLY, null);
    		response.setMessage(resmsg);
    		response.setMessageCode(PretupsErrorCodesI.LOAD_ADD_TEMP_CARD_GROUP_LIST_SUCCESSFULLY);
                   
        } catch (BTSLBaseException e) {
            LOG.error(methodName, "Exceptin:e=" + e);
            LOG.errorTrace(methodName, e);
            String msg = RestAPIStringParser.getMessage(locale, e.getMessageKey(), null);
    		response.setMessageCode(e.getMessageKey());
    		response.setMessage(msg);

    		if (Arrays.asList(PretupsI.OAUTHCODES).contains(e.getMessage())) {
    			response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
    			response.setStatus(HttpStatus.SC_UNAUTHORIZED);
    		} else {
    			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
    			response.setStatus(HttpStatus.SC_BAD_REQUEST);
    		}

        }  catch (Exception e) {
            LOG.error(methodName, "Exceptin:e=" + e);
            LOG.errorTrace(methodName, e);
            response1.setStatus(HttpStatus.SC_BAD_REQUEST);
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            response.setMessageCode(PretupsErrorCodesI.LOAD_ADD_TEMP_CARD_GROUP_LIST_FAILED);
        } finally {
        	
        	if(mcomCon != null){
        		mcomCon.close("AddC2SCardGroupController#loadAddTempC2SCardGrouplist");
        		mcomCon=null;}
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting forward=" );
            }

           }
    	 return response;

		
	}
	
	@PostMapping(value = "/saveC2SCardGroupList", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "save C2S CardGroup List", response =C2SAddCardGroupSaveResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = C2SAddCardGroupSaveResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${saveC2SCardGroupList.summary}", description="${saveC2SCardGroupList.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = C2SAddCardGroupSaveResponseVO.class))
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


	public C2SAddCardGroupSaveResponseVO  SaveC2SCardGroupList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest,
			@RequestBody  C2SAddCardGroupSaveRequestVO requestVO
			
			) throws Exception {

		final String methodName = "SaveC2SCardGroupList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }

       
        Connection con = null;MComConnectionI mcomCon = null;
        Date currentDate = null;
        C2SAddCardGroupSaveResponseVO response = new C2SAddCardGroupSaveResponseVO();
        UserVO userVO = null;
    	UserDAO userDAO = null;
    	Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
    	
    	try {

    		/*
    		 * Authentication
    		 * 
    		 * @throws BTSLBaseException
    		 */

    		OAuthUser OAuthUserData = new OAuthUser();
    		OAuthUserData.setData(new OAuthUserData());
    		OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
    		mcomCon = new MComConnection();
    		con = mcomCon.getConnection();
    		userDAO = new UserDAO();
    	    String loginID = OAuthUserData.getData().getLoginid();
    	    userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
    	    response = service.saveC2SCardGroup(con,httpServletRequest,response, userVO, requestVO);
    	   
    	    response.setStatus((HttpStatus.SC_OK));
    	    String arr[]= {requestVO.getCardGroupSetName()};
    		String resmsg = RestAPIStringParser.getMessage(locale,
    				PretupsErrorCodesI.SAVE_C2S_CARD_GROUP_LIST_SUCCESS, arr);
    		response.setMessage(resmsg);
    		response.setMessageCode(PretupsErrorCodesI.SAVE_C2S_CARD_GROUP_LIST_SUCCESS);
                   
        } catch (BTSLBaseException e) {
            LOG.error(methodName, "Exceptin:e=" + e);
            LOG.errorTrace(methodName, e);
            if(response.getMessage() == null) {
            	
            	 String msg = RestAPIStringParser.getMessage(locale, e.getMessageKey(), null);
         		response.setMessageCode(e.getMessageKey());
         		response.setMessage(msg);

            }
           
    		if (Arrays.asList(PretupsI.OAUTHCODES).contains(e.getMessage())) {
    			response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
    			response.setStatus(HttpStatus.SC_UNAUTHORIZED);
    		} else {
    			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
    			response.setStatus(HttpStatus.SC_BAD_REQUEST);
    		}

        }  catch (Exception e) {
            LOG.error(methodName, "Exceptin:e=" + e);
            LOG.errorTrace(methodName, e);
            response1.setStatus(HttpStatus.SC_BAD_REQUEST);
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            response.setMessageCode(PretupsErrorCodesI.SAVE_C2S_CARD_GROUP_LIST_FAILED);
        } finally {
        	
        	if(mcomCon != null){
        		mcomCon.close("AddC2SCardGroupController#"+methodName);
        		mcomCon=null;}
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting forward=" );
            }

           }
    	 return response;


	}
	
	@GetMapping(value = "/loadC2SCardGroupSetNameList", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "load C2S CardGroup Set Name List", response =C2SCardGroupSetNameListResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = C2SCardGroupSetNameListResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${loadC2SCardGroupSetNameList.summary}", description="${loadC2SCardGroupSetNameList.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = C2SCardGroupSetNameListResponseVO.class))
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


	public C2SCardGroupSetNameListResponseVO loadC2SCardGroupSetNameList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest,
			@Parameter(description = "serviceType", required = true) @RequestParam("serviceType") String serviceType,
			@Parameter(description = "subService", required = true) @RequestParam("subService") String subService
			
			) throws Exception {

    final String methodName = "loadC2SCardGroupServices";
    if (LOG.isDebugEnabled()) {
        LOG.debug(methodName, "Entered");
    }
    
    Connection con = null;MComConnectionI mcomCon = null;
    UserVO userVO = null;
	UserDAO userDAO = null;
	Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
	C2SCardGroupSetNameListResponseVO response = new C2SCardGroupSetNameListResponseVO();
	try {

		/*
		 * Authentication
		 * 
		 * @throws BTSLBaseException
		 */

		OAuthUser OAuthUserData = new OAuthUser();
		OAuthUserData.setData(new OAuthUserData());
		OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
		mcomCon = new MComConnection();
		con = mcomCon.getConnection();
		userDAO = new UserDAO();
	    String loginID = OAuthUserData.getData().getLoginid();
	    userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
	    response = service.loadC2SCardGroupSetNameList(con, userVO.getNetworkID(),serviceType, subService);
	    response.setStatus((HttpStatus.SC_OK));
		String resmsg = RestAPIStringParser.getMessage(locale,
				PretupsErrorCodesI.SUCCESSFULLY_LOAD_C2S_CARD_GROUP_SET_NAME_LIST, null);
		response.setMessage(resmsg);
		response.setMessageCode(PretupsErrorCodesI.SUCCESSFULLY_LOAD_C2S_CARD_GROUP_SET_NAME_LIST);
    
       
    } catch (BTSLBaseException e) {
        LOG.error(methodName, "Exceptin:e=" + e);
        LOG.errorTrace(methodName, e);
        String msg = RestAPIStringParser.getMessage(locale, e.getMessageKey(), null);
		response.setMessageCode(e.getMessageKey());
		response.setMessage(msg);

		if (Arrays.asList(PretupsI.OAUTHCODES).contains(e.getMessage())) {
			response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
			response.setStatus(HttpStatus.SC_UNAUTHORIZED);
		} else {
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

    }  catch (Exception e) {
        LOG.error(methodName, "Exceptin:e=" + e);
        LOG.errorTrace(methodName, e);
        response1.setStatus(HttpStatus.SC_BAD_REQUEST);
        response.setStatus(HttpStatus.SC_BAD_REQUEST);
        response.setMessageCode(PretupsErrorCodesI.FAILED_LOAD_C2S_CARD_GROUP_SET_NAME_LIST);
    } finally {
    	
    	if(mcomCon != null){
    		mcomCon.close("AddC2SCardGroupController#loadC2SCardGroupServices");
    		mcomCon=null;}
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting forward=" );
        }
    }
		return response;
	}
	
	@GetMapping(value = "/loadC2SCardGroupVersionList", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Load C2SCardGroup version List", response =LoadC2SCardGroupVersionListResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = LoadC2SCardGroupVersionListResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${loadC2SCardGroupVersionList.summary}", description="${loadC2SCardGroupVersionList.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = LoadC2SCardGroupVersionListResponseVO.class))
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


	public LoadC2SCardGroupVersionListResponseVO loadC2SCardGroupVersionList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest,
			
			@Parameter(description = "cardGroupSetType", required = true) @RequestParam("cardGroupSetType") String cardGroupSetType,
			@Parameter(description = "service", required = true) @RequestParam("service") String reqservice,
			@Parameter(description = "subservice", required = true) @RequestParam("subservice") String subservice,
			@Parameter(description = "dateTime", required = true) @RequestParam("dateTime") String dateTime
			) throws Exception {

	
        final String methodName = "loadC2SCardGroupVersionList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }

       
        Connection con = null;MComConnectionI mcomCon = null;
        Date currentDate = null;
        LoadC2SCardGroupVersionListResponseVO response = new LoadC2SCardGroupVersionListResponseVO();
        UserVO userVO = null;
    	UserDAO userDAO = null;
    	Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
    	
    	try {

    		/*
    		 * Authentication
    		 * 
    		 * @throws BTSLBaseException
    		 */

    		OAuthUser OAuthUserData = new OAuthUser();
    		OAuthUserData.setData(new OAuthUserData());
    		OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
    		mcomCon = new MComConnection();
    		con = mcomCon.getConnection();
    		userDAO = new UserDAO();
    	    String loginID = OAuthUserData.getData().getLoginid();
    	    userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
    	    Date date = null;
    	   try {
    		   String systemDateFormat = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATETIME_FORMAT);
    	    final DateFormat dateFormat =  new SimpleDateFormat(systemDateFormat);
    	     date = dateFormat.parse(dateTime);
    	   }
    	   catch(Exception e) {
    		   throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_DATE_TIME_FORMATE);
    	   }
    	    response = service.loadVersionList(con, userVO.getNetworkID(), reqservice,  subservice, cardGroupSetType, date );
    	    response.setStatus((HttpStatus.SC_OK));
    		String resmsg = RestAPIStringParser.getMessage(locale,
    				PretupsErrorCodesI.SUCCESSFULLY_LOAD_C2S_CARD_GROUP_LIST, null);
    		response.setMessage(resmsg);
    		response.setMessageCode(PretupsErrorCodesI.SUCCESSFULLY_LOAD_C2S_CARDGROUP_VERSION_LIST);
                   
        } catch (BTSLBaseException e) {
            LOG.error(methodName, "Exceptin:e=" + e);
            LOG.errorTrace(methodName, e);
            String msg = RestAPIStringParser.getMessage(locale, e.getMessageKey(), null);
    		response.setMessageCode(e.getMessageKey());
    		response.setMessage(msg);

    		if (Arrays.asList(PretupsI.OAUTHCODES).contains(e.getMessage())) {
    			response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
    			response.setStatus(HttpStatus.SC_UNAUTHORIZED);
    		} else {
    			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
    			response.setStatus(HttpStatus.SC_BAD_REQUEST);
    		}

        }  catch (Exception e) {
            LOG.error(methodName, "Exceptin:e=" + e);
            LOG.errorTrace(methodName, e);
            response1.setStatus(HttpStatus.SC_BAD_REQUEST);
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            response.setMessageCode(PretupsErrorCodesI.FAILED_LOAD_C2S_CARDGROUP_VERSION_LIST);
        } finally {
        	
        	if(mcomCon != null){
        		mcomCon.close(className+"#"+methodName);
        		mcomCon=null;}
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting forward=" );
            }

           }
    	 return response;

	}
	@GetMapping(value = "/loadc2scardgroupversionlistbasedonsetidandfromdate", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Load C2S CardGroup VersionList based On SetID And FromDate", response =C2SCardGroupVersionNumbersListResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = C2SCardGroupVersionNumbersListResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${loadc2scardgroupversionlistbasedonsetidandfromdate.summary}", description="${loadc2scardgroupversionlistbasedonsetidandfromdate.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = C2SCardGroupVersionNumbersListResponseVO.class))
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


	public C2SCardGroupVersionNumbersListResponseVO loadC2SCardGroupVersionListbasedOnSetIDAndFromDate(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest,		
			@Parameter(description = "cardGroupSetId", required = true) @RequestParam("cardGroupSetId") String cardGroupSetId,
			@Parameter(description = "dateTime", required = true) @RequestParam("dateTime") String dateTime
			) throws Exception {

	
        final String methodName = "loadC2SCardGroupVersionListbasedOnSetIDAndFromDate";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }

       
        Connection con = null;MComConnectionI mcomCon = null;
        Date currentDate = null;
        C2SCardGroupVersionNumbersListResponseVO response = new C2SCardGroupVersionNumbersListResponseVO();
        UserVO userVO = null;
    	UserDAO userDAO = null;
    	Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
    	
    	try {

    		/*
    		 * Authentication
    		 * 
    		 * @throws BTSLBaseException
    		 */

    		OAuthUser OAuthUserData = new OAuthUser();
    		OAuthUserData.setData(new OAuthUserData());
    		OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
    		mcomCon = new MComConnection();
    		con = mcomCon.getConnection();
    		userDAO = new UserDAO();
    	    String loginID = OAuthUserData.getData().getLoginid();
    	    userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
    	    Date date = null;
    	   try {
    		   String systemDateFormat = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATETIME_FORMAT);
    		   final DateFormat dateFormat =  new SimpleDateFormat(systemDateFormat);
    		   date = dateFormat.parse(dateTime);
    	   }
    	   catch(Exception e) {
    		   throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_DATE_TIME_FORMATE);
    	   } 
    	    response = service.loadVersionListBasedOnCardGroupSetIDAndDate(con, cardGroupSetId, date );
    	    response.setStatus((HttpStatus.SC_OK));
    		String resmsg = RestAPIStringParser.getMessage(locale,
    				PretupsErrorCodesI.SUCCESSFULLY_LOAD_C2S_CARDGROUP_VERSION_LIST, null);
    		response.setMessage(resmsg);
    		response.setMessageCode(PretupsErrorCodesI.SUCCESSFULLY_LOAD_C2S_CARDGROUP_VERSION_LIST);
                   
        } catch (BTSLBaseException e) {
            LOG.error(methodName, "Exceptin:e=" + e);
            LOG.errorTrace(methodName, e);
            String msg = RestAPIStringParser.getMessage(locale, e.getMessageKey(), null);
    		response.setMessageCode(e.getMessageKey());
    		response.setMessage(msg);

    		if (Arrays.asList(PretupsI.OAUTHCODES).contains(e.getMessage())) {
    			response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
    			response.setStatus(HttpStatus.SC_UNAUTHORIZED);
    		} else {
    			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
    			response.setStatus(HttpStatus.SC_BAD_REQUEST);
    		}

        }  catch (Exception e) {
            LOG.error(methodName, "Exceptin:e=" + e);
            LOG.errorTrace(methodName, e);
            response1.setStatus(HttpStatus.SC_BAD_REQUEST);
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            response.setMessageCode(PretupsErrorCodesI.FAILED_LOAD_C2S_CARDGROUP_VERSION_LIST);
        } finally {
        	
        	if(mcomCon != null){
        		mcomCon.close(className+"#"+methodName);
        		mcomCon=null;}
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting forward=" );
            }

           }
    	 return response;

	}

	
	@GetMapping(value = "/viewc2scardroupdetails", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "view C2S CardGroup Details", response =ViewC2SCardGroupResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = ViewC2SCardGroupResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${viewc2scardroupdetails.summary}", description="${viewc2scardroupdetails.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ViewC2SCardGroupResponseVO.class))
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


	public ViewC2SCardGroupResponseVO  viewC2SCardGroup(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest,
			@Parameter(description = "cardGroupSetId", required = true) @RequestParam("cardGroupSetId") String cardGroupSetId,
			@Parameter(description = "version", required = true) @RequestParam("version") String version
            ) throws Exception {
       
		final String methodName = "viewC2SCardGroup";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }

       
        Connection con = null;MComConnectionI mcomCon = null;
        Date currentDate = null;
        ViewC2SCardGroupResponseVO response = new ViewC2SCardGroupResponseVO();
        UserVO userVO = null;
    	UserDAO userDAO = null;
    	Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
    	
    	try {

    		/*
    		 * Authentication
    		 * 
    		 * @throws BTSLBaseException
    		 */

    		OAuthUser OAuthUserData = new OAuthUser();
    		OAuthUserData.setData(new OAuthUserData());
    		OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
    		mcomCon = new MComConnection();
    		con = mcomCon.getConnection();
    		userDAO = new UserDAO();
    	    String loginID = OAuthUserData.getData().getLoginid();
    	    userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
    	    response = service.viewC2SCardGroupDetails(con, userVO.getNetworkID(), cardGroupSetId, version);
    	    response.setStatus((HttpStatus.SC_OK));
    		String resmsg = RestAPIStringParser.getMessage(locale,
    				PretupsErrorCodesI.SUCCESSFULLY_LOAD_VIEW_C2S_CARDGROUP_DETAILS, null);
    		response.setMessage(resmsg);
    		response.setMessageCode(PretupsErrorCodesI.SUCCESSFULLY_LOAD_VIEW_C2S_CARDGROUP_DETAILS);
                   
        } catch (BTSLBaseException e) {
            LOG.error(methodName, "Exceptin:e=" + e);
            LOG.errorTrace(methodName, e);
            String msg = RestAPIStringParser.getMessage(locale, e.getMessageKey(), null);
    		response.setMessageCode(e.getMessageKey());
    		response.setMessage(msg);

    		if (Arrays.asList(PretupsI.OAUTHCODES).contains(e.getMessage())) {
    			response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
    			response.setStatus(HttpStatus.SC_UNAUTHORIZED);
    		} else {
    			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
    			response.setStatus(HttpStatus.SC_BAD_REQUEST);
    		}

        }  catch (Exception e) {
            LOG.error(methodName, "Exceptin:e=" + e);
            LOG.errorTrace(methodName, e);
            response1.setStatus(HttpStatus.SC_BAD_REQUEST);
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            response.setMessageCode(PretupsErrorCodesI.FAILED_LOAD_VIEW_C2S_CARDGROUP_DETAILS);
        } finally {
        	
        	if(mcomCon != null){
        		mcomCon.close(className+"#"+methodName);
        		mcomCon=null;}
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting forward" );
            }

           }
    	 return response;

		
	}

	
	@PostMapping(value = "/addModifyC2SCardGroupTemplist", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "add Modify C2S CardGroupTemp list", response =AddTempCardGroupListResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = AddTempCardGroupListResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${addModifyC2SCardGroupTemplist.summary}", description="${addModifyC2SCardGroupTemplist.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = AddTempCardGroupListResponseVO.class))
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


	public AddTempCardGroupListResponseVO addModifyC2SCardGroupTemplist(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest,
			@RequestBody CardGroupDetailsRequestVO requestVO) throws Exception {

    final String methodName = "addModifyC2SCardGroupTemplist";
    if (LOG.isDebugEnabled()) {
        LOG.debug(methodName, "Entered");
    }
    
    Connection con = null;MComConnectionI mcomCon = null;
    UserVO userVO = null;
	UserDAO userDAO = null;
	Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
	AddTempCardGroupListResponseVO response = new AddTempCardGroupListResponseVO();
	try {

		/*
		 * Authentication
		 * 
		 * @throws BTSLBaseException
		 */

		OAuthUser OAuthUserData = new OAuthUser();
		OAuthUserData.setData(new OAuthUserData());
		OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
		mcomCon = new MComConnection();
		con = mcomCon.getConnection();
		userDAO = new UserDAO();
	    String loginID = OAuthUserData.getData().getLoginid();
	    userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
	    response = service.addModifyC2SCardGroupTempList(con,httpServletRequest, userVO, requestVO);
	    response.setStatus((HttpStatus.SC_OK));
		String resmsg = RestAPIStringParser.getMessage(locale,
				PretupsErrorCodesI.SUCCESSFULLY_ADD_MODIFY_C2S_CARDGROUP_TEMP_LIST, null);
		response.setMessage(resmsg);
		response.setMessageCode(PretupsErrorCodesI.SUCCESSFULLY_ADD_MODIFY_C2S_CARDGROUP_TEMP_LIST);
    
       
    } catch (BTSLBaseException e) {
        LOG.error(methodName, "Exceptin:e=" + e);
        LOG.errorTrace(methodName, e);
        String msg = RestAPIStringParser.getMessage(locale, e.getMessageKey(), null);
		response.setMessageCode(e.getMessageKey());
		response.setMessage(msg);

		if (Arrays.asList(PretupsI.OAUTHCODES).contains(e.getMessage())) {
			response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
			response.setStatus(HttpStatus.SC_UNAUTHORIZED);
		} else {
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

    }  catch (Exception e) {
        LOG.error(methodName, "Exceptin:e=" + e);
        LOG.errorTrace(methodName, e);
        response1.setStatus(HttpStatus.SC_BAD_REQUEST);
        response.setStatus(HttpStatus.SC_BAD_REQUEST);
        response.setMessageCode(PretupsErrorCodesI.FAILED_ADD_MODIFY_C2S_CARDGROUP_TEMP_LIST);
    } finally {
    	
    	if(mcomCon != null){
    		mcomCon.close(className+"#"+methodName);
    		mcomCon=null;}
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting forward=" );
        }
    }
		return response;
	}
	
	
	@PostMapping(value = "/deleteTempModifyC2SCardGroupList", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "delete Temp Modify C2S CardGroupList", response =DeleteModifyC2SCardGroupResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = DeleteModifyC2SCardGroupResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${deleteTempModifyC2SCardGroupList.summary}", description="${deleteTempModifyC2SCardGroupList.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = DeleteModifyC2SCardGroupResponseVO.class))
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


	public DeleteModifyC2SCardGroupResponseVO  deleteTempModifyC2SCardGroupList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest,
			@Parameter(description = "selectCardGroupSetId", required = true) @RequestParam("selectCardGroupSetId") String selectCardGroupSetId,
			@Parameter(description = "version", required = true) @RequestParam("version") String version,
			@Parameter(description = "oldApplicableFromDate", required = true) @RequestParam("oldApplicableFromDate") String oldApplicableFromDate,
			@Parameter(description = "oldApplicableFromHour", required = true) @RequestParam("oldApplicableFromHour") String oldApplicableFromHour
						) throws Exception {
       
		final String methodName = "deleteTempModifyC2SCardGroupList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }

       
        Connection con = null;MComConnectionI mcomCon = null;
        DeleteModifyC2SCardGroupResponseVO response = new DeleteModifyC2SCardGroupResponseVO();
        UserVO userVO = null;
    	UserDAO userDAO = null;
    	Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
    	
    	try {

    		/*
    		 * Authentication
    		 * 
    		 * @throws BTSLBaseException
    		 */

    		OAuthUser OAuthUserData = new OAuthUser();
    		OAuthUserData.setData(new OAuthUserData());
    		OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
    		mcomCon = new MComConnection();
    		con = mcomCon.getConnection();
    		userDAO = new UserDAO();
    	    String loginID = OAuthUserData.getData().getLoginid();
    	    userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
    	    String arr[]= {service.getCardGroupSetNameById(con, selectCardGroupSetId, version)};
    	    int deleted = service.deleteTempC2SCardGroupList(con, mcomCon, selectCardGroupSetId,  version,  oldApplicableFromDate, oldApplicableFromHour);
    	    if(deleted>0) {
    	    response.setStatus((HttpStatus.SC_OK));
    		String resmsg = RestAPIStringParser.getMessage(locale,
    				PretupsErrorCodesI.MODIFY_DELETE_SUCCESSFULLY, arr);
    		response.setMessage(resmsg);
    		response.setMessageCode(PretupsErrorCodesI.MODIFY_DELETE_SUCCESSFULLY);
    	    }else {
    	    	con.rollback();
    	    	mcomCon.finalRollback();
    	    	throw new BTSLBaseException(PretupsErrorCodesI.MODIFY_DELETE_FAILED);
    	    }
        } catch (BTSLBaseException e) {
            LOG.error(methodName, "Exceptin:e=" + e);
            LOG.errorTrace(methodName, e);
            String msg = RestAPIStringParser.getMessage(locale, e.getMessageKey(), null);
    		response.setMessageCode(e.getMessageKey());
    		response.setMessage(msg);

    		if (Arrays.asList(PretupsI.OAUTHCODES).contains(e.getMessage())) {
    			response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
    			response.setStatus(HttpStatus.SC_UNAUTHORIZED);
    		} else {
    			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
    			response.setStatus(HttpStatus.SC_BAD_REQUEST);
    		}
	    	con.rollback();
	    	mcomCon.finalRollback();
        }  catch (Exception e) {
            LOG.error(methodName, "Exceptin:e=" + e);
            LOG.errorTrace(methodName, e);
            response1.setStatus(HttpStatus.SC_BAD_REQUEST);
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            response.setMessageCode(PretupsErrorCodesI.MODIFY_DELETE_FAILED);
	    	con.rollback();
	    	mcomCon.finalRollback();
        } finally {
        	
        	if(mcomCon != null){
        		mcomCon.close(className+"#"+methodName);
        		mcomCon=null;}
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting forward=" );
            }

           }
    	 return response;
	}
	
	
	@PostMapping(value = "/modifyC2SCardGroupSave", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Modify C2S CardGroup Save", response =UpdateSaveC2SCardGroupResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = UpdateSaveC2SCardGroupResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${modifyC2SCardGroupSave.summary}", description="${modifyC2SCardGroupSave.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UpdateSaveC2SCardGroupResponseVO.class))
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


	public UpdateSaveC2SCardGroupResponseVO  modifySaveC2SCardGroupList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest,
			@RequestBody  C2SAddCardGroupSaveRequestVO requestVO
			
			) throws Exception {

		final String methodName = "modifySaveC2SCardGroupList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }

       
        Connection con = null;MComConnectionI mcomCon = null;
        Date currentDate = null;
        UpdateSaveC2SCardGroupResponseVO response = new UpdateSaveC2SCardGroupResponseVO();
        UserVO userVO = null;
    	UserDAO userDAO = null;
    	Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
    	
    	try {

    		/*
    		 * Authentication
    		 * 
    		 * @throws BTSLBaseException
    		 */

    		OAuthUser OAuthUserData = new OAuthUser();
    		OAuthUserData.setData(new OAuthUserData());
    		OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
    		mcomCon = new MComConnection();
    		con = mcomCon.getConnection();
    		userDAO = new UserDAO();
    	    String loginID = OAuthUserData.getData().getLoginid();
    	    userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
    	    response = service.modifySaveC2SCardGroup(con,httpServletRequest, userVO, requestVO);
    	   
    	    response.setStatus((HttpStatus.SC_OK));
    	    String arr[] = {requestVO.getCardGroupSetName()};
    		String resmsg = RestAPIStringParser.getMessage(locale,
    				PretupsErrorCodesI.MODIFY_C2S_CARDGROUP_SAVE_SUCCUSSFULLY, arr);
    		response.setMessage(resmsg);
    		response.setMessageCode(PretupsErrorCodesI.MODIFY_C2S_CARDGROUP_SAVE_SUCCUSSFULLY);
                   
        } catch (BTSLBaseException e) {
            LOG.error(methodName, "Exceptin:e=" + e);
            LOG.errorTrace(methodName, e);
            String msg = RestAPIStringParser.getMessage(locale, e.getMessageKey(), null);
    		response.setMessageCode(e.getMessageKey());
    		response.setMessage(msg);

    		if (Arrays.asList(PretupsI.OAUTHCODES).contains(e.getMessage())) {
    			response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
    			response.setStatus(HttpStatus.SC_UNAUTHORIZED);
    		} else {
    			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
    			response.setStatus(HttpStatus.SC_BAD_REQUEST);
    		}

        }  catch (Exception e) {
            LOG.error(methodName, "Exceptin:e=" + e);
            LOG.errorTrace(methodName, e);
            response1.setStatus(HttpStatus.SC_BAD_REQUEST);
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            response.setMessageCode(PretupsErrorCodesI.MODIFY_C2S_CARDGROUP_SAVE_FAILED);
        } finally {
        	
        	if(mcomCon != null){
        		mcomCon.close(className+methodName);
        		mcomCon=null;}
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting forward=" );
            }

           }
    	 return response;


	}
	@PostMapping(value="/getcardgrouptransferrulevalue", produces = MediaType.APPLICATION_JSON )
	@ResponseBody
	/*@ApiOperation(value = "Get Card Group TransferRule Value", response = CardGroupCalculateC2STransferValueResponseVO.class, authorizations = { @Authorization(value = "Authorization")})
	
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = CardGroupCalculateC2STransferValueResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${getcardgrouptransferrulevalue.summary}", description="${getcardgrouptransferrulevalue.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CardGroupCalculateC2STransferValueResponseVO.class))
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



	public CardGroupCalculateC2STransferValueResponseVO getCardGroupTransferRuleValue(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest,	
			@RequestBody  CardGroupCalculateC2STransferValueRequestVO requestVO) throws Exception {
		
		final String METHOD_NAME="getCardGroupTransferRuleValue";
		if(LOG.isDebugEnabled()) {
			LOG.debug(className, METHOD_NAME);
		}
		CardGroupCalculateC2STransferValueResponseVO response = new CardGroupCalculateC2STransferValueResponseVO() ;
		
		UserDAO userDAO = null;
		UserVO userVO = null;
	    Connection con = null;
		MComConnectionI mcomCon=null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		StringBuffer loggerValue = new StringBuffer();
		
		try {
			OAuthUser OAuthUserData = new OAuthUser();
    		OAuthUserData.setData(new OAuthUserData());
    		OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
    		mcomCon = new MComConnection();
    		con = mcomCon.getConnection();
    		userDAO = new UserDAO();
    	    String loginID = OAuthUserData.getData().getLoginid();
    	    userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
			
    	    response = service.getCardGroupTransferRuleValue(con, userVO, response, requestVO);
			response.setStatus((HttpStatus.SC_OK));
    		String resmsg = RestAPIStringParser.getMessage(locale,
    				PretupsErrorCodesI.SAVE_CARD_GROUP_TRANSFER_RULE_VALUE_SUCCESSFULLY, null);
    		response.setMessage(resmsg);
    		response.setMessageCode(PretupsErrorCodesI.SAVE_CARD_GROUP_TRANSFER_RULE_VALUE_SUCCESSFULLY);

		}
			catch(BTSLBaseException be) {
				 loggerValue.setLength(0); 
				 loggerValue.append("Exception:e=");
				 loggerValue.append(be); 
				 
				LOG.error(METHOD_NAME, "Exception:e=" + be);
				LOG.errorTrace(METHOD_NAME, be);
				if(response.getMessage()==null) {
					String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
					response.setMessageCode(be.getMessageKey());
					response.setMessage(msg);
				}
				if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
					response.setStatus(HttpStatus.SC_UNAUTHORIZED);
					response1.setStatus(HttpStatus.SC_UNAUTHORIZED);

				} else {
					response.setStatus(HttpStatus.SC_BAD_REQUEST);
					response1.setStatus(HttpStatus.SC_BAD_REQUEST);

				}
			}
				catch (Exception e) {
					
					  loggerValue.setLength(0); 
					  loggerValue.append("Exception:e=");
					  loggerValue.append(e);
					  
					  LOG.error(METHOD_NAME, "Exception:e=" + e);
						LOG.errorTrace(METHOD_NAME, e);
						response.setStatus((HttpStatus.SC_BAD_REQUEST));
						String resmsg = RestAPIStringParser.getMessage(
								new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
								PretupsErrorCodesI.FAILED_SAVE_CARD_GROUP_TRANSFER_RULE_VALUE, null);
						response.setMessage(resmsg);
						response.setMessageCode(PretupsErrorCodesI.FAILED_SAVE_CARD_GROUP_TRANSFER_RULE_VALUE);
						response1.setStatus(HttpStatus.SC_BAD_REQUEST);
					 
					
	 
	
			
		} finally {
				if (mcomCon != null) {
				mcomCon.close(METHOD_NAME);
				mcomCon = null;
			}
		}

				if (LOG.isDebugEnabled()) {
					LOG.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
		}
				return response;

	}
	@GetMapping(value="/loadcardgroupcalculatetransferruledropdowns", produces = MediaType.APPLICATION_JSON )
	@ResponseBody
	/*@ApiOperation(value = "Load Card Group Calculate Transfer Rule Drop Downs", response = LoadCardGroupTransferValuesResponseVO.class, authorizations = { @Authorization(value = "Authorization")})
	
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = LoadCardGroupTransferValuesResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${loadcardgroupcalculatetransferruledropdowns.summary}", description="${loadcardgroupcalculatetransferruledropdowns.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = LoadCardGroupTransferValuesResponseVO.class))
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


	public LoadCardGroupTransferValuesResponseVO loadCardGroupCalculateTransferRuleDropDowns(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse responseSwagger, HttpServletRequest httpServletRequest
			) throws Exception {
		
		final String METHOD_NAME="loadCardGroupCalculateTransferRuleDropDowns";
		if(LOG.isDebugEnabled()) {
			LOG.debug(className, METHOD_NAME);
		}
		LoadCardGroupTransferValuesResponseVO response = new LoadCardGroupTransferValuesResponseVO();
		
		
		UserDAO userDAO = null;
		UserVO userVO = null;
		Connection con = null;
		MComConnectionI mcomCon=null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		StringBuilder loggerValue= new StringBuilder();
		try {
			
			
			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, responseSwagger);
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			userDAO = new UserDAO();
    	    String loginID = OAuthUserData.getData().getLoginid();
    	    userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);			
			
			response = service.loadCardGroupCalculateTransferRuleValueDropDown(con, userVO);
			response.setStatus((HttpStatus.SC_OK));
    		String resmsg = RestAPIStringParser.getMessage(locale,
    				PretupsErrorCodesI.LOAD_VIEW_TRANSFER_RULE_CARD_GROUP_SUCCESSFULLY, null);
    		response.setMessage(resmsg);
    		response.setMessageCode(PretupsErrorCodesI.LOAD_VIEW_TRANSFER_RULE_CARD_GROUP_SUCCESSFULLY);

		}
			catch(BTSLBaseException be) {
				 loggerValue.setLength(0); 
				 loggerValue.append("Exception:e=");
				 loggerValue.append(be); 
				 
				LOG.error(METHOD_NAME, "Exception:e=" + be);
				LOG.errorTrace(METHOD_NAME, be);
				
				String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
				response.setMessageCode(be.getMessageKey());
				response.setMessage(msg);

				if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
					response.setStatus(HttpStatus.SC_UNAUTHORIZED);
					responseSwagger.setStatus(HttpStatus.SC_UNAUTHORIZED);

				} else {
					response.setStatus(HttpStatus.SC_BAD_REQUEST);
					responseSwagger.setStatus(HttpStatus.SC_BAD_REQUEST);

				}
			}
				catch (Exception e) {
					
					  loggerValue.setLength(0); 
					  loggerValue.append("Exception:e=");
					  loggerValue.append(e);
					  
					  LOG.error(METHOD_NAME, "Exception:e=" + e);
						LOG.errorTrace(METHOD_NAME, e);
						response.setStatus((HttpStatus.SC_BAD_REQUEST));
						String resmsg = RestAPIStringParser.getMessage(
								new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
								PretupsErrorCodesI.LOAD_VIEW_TRANSFER_RULE_CARD_GROUP_FAILED, null);
						response.setMessage(resmsg);
						response.setMessageCode(PretupsErrorCodesI.LOAD_VIEW_TRANSFER_RULE_CARD_GROUP_FAILED);
						responseSwagger.setStatus(HttpStatus.SC_BAD_REQUEST);
					 
					
	 
	
			
		} finally {
				if (mcomCon != null) {
				mcomCon.close(METHOD_NAME);
				mcomCon = null;
			}
		}

				if (LOG.isDebugEnabled()) {
					LOG.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
		}
				return response;

	}
	
	
	@PostMapping(value="/changedefaultcardgroup", produces = MediaType.APPLICATION_JSON )
	@ResponseBody
	/*@ApiOperation(value = "Change Default Card Group", response = ChangeDefaultCardGroupResponseVO.class, authorizations = { @Authorization(value = "Authorization")})
	
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = ChangeDefaultCardGroupResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${changedefaultcardgroup.summary}", description="${changedefaultcardgroup.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ChangeDefaultCardGroupResponseVO.class))
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


	public ChangeDefaultCardGroupResponseVO changeDefaultCardgroup(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse responseSwagger, HttpServletRequest httpServletRequest,
			@RequestBody DefaultCardGroupRequestVO requestVO
			) throws Exception {
		
		final String METHOD_NAME="changeDefaultCardgroup";
		if(LOG.isDebugEnabled()) {
			LOG.debug(className, METHOD_NAME);
		}
		ChangeDefaultCardGroupResponseVO response = new ChangeDefaultCardGroupResponseVO();
		
		
		UserDAO userDAO = null;
		UserVO userVO = null;
		Connection con = null;
		MComConnectionI mcomCon=null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		StringBuilder loggerValue= new StringBuilder();
		try {
			
			
			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, responseSwagger);
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			userDAO = new UserDAO();
    	    String loginID = OAuthUserData.getData().getLoginid();
    	    userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);			
			
			response = service.changeDefaultCardGroup(con, userVO, requestVO);
			response.setStatus((HttpStatus.SC_OK));
			String arr[] = {response.getUpdateddefaultCadgroup()};
    		String resmsg = RestAPIStringParser.getMessage(locale,
    				PretupsErrorCodesI.SUCCESSFULLY_CHANGE_DEFAULT_CARD_GROUP, arr);
    		response.setMessage(resmsg);
    		response.setMessageCode(PretupsErrorCodesI.SUCCESSFULLY_CHANGE_DEFAULT_CARD_GROUP);

		}
			catch(BTSLBaseException be) {
				 loggerValue.setLength(0); 
				 loggerValue.append("Exception:e=");
				 loggerValue.append(be); 
				 
				LOG.error(METHOD_NAME, "Exception:e=" + be);
				LOG.errorTrace(METHOD_NAME, be);
				
				String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
				response.setMessageCode(be.getMessageKey());
				response.setMessage(msg);

				if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
					response.setStatus(HttpStatus.SC_UNAUTHORIZED);
					responseSwagger.setStatus(HttpStatus.SC_UNAUTHORIZED);

				} else {
					response.setStatus(HttpStatus.SC_BAD_REQUEST);
					responseSwagger.setStatus(HttpStatus.SC_BAD_REQUEST);

				}
			}
				catch (Exception e) {
					
					  loggerValue.setLength(0); 
					  loggerValue.append("Exception:e=");
					  loggerValue.append(e);
					  
					  LOG.error(METHOD_NAME, "Exception:e=" + e);
						LOG.errorTrace(METHOD_NAME, e);
						response.setStatus((HttpStatus.SC_BAD_REQUEST));
						String resmsg = RestAPIStringParser.getMessage(
								new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
								PretupsErrorCodesI.FAILED_CHANGE_DEFAULT_CARD_GROUP, null);
						response.setMessage(resmsg);
						response.setMessageCode(PretupsErrorCodesI.FAILED_CHANGE_DEFAULT_CARD_GROUP);
						responseSwagger.setStatus(HttpStatus.SC_BAD_REQUEST);
					 
					
	 
	
			
		} finally {
				if (mcomCon != null) {
				mcomCon.close(METHOD_NAME);
				mcomCon = null;
			}
		}

				if (LOG.isDebugEnabled()) {
					LOG.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
		}
				return response;

	}


	@PostMapping(value="/calculatetransfervalue", produces = MediaType.APPLICATION_JSON )
	@ResponseBody
	/*@ApiOperation(value = "Calcuate card group transfer value, fee and tax based on amount", response = LoadCardGroupTransferValuesResponseVO.class, authorizations = { @Authorization(value = "Authorization")})

	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = LoadCardGroupTransferValuesResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${calculatetransfervalue.summary}", description="${calculatetransfervalue.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CalculateTransferValueResponseVO.class))
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


	public CalculateTransferValueResponseVO calculateC2SReceivervalue(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse responseSwagger, HttpServletRequest httpServletRequest,
			@RequestBody CalculateTransferValueRequestVO requestVO
	) throws Exception {

		final String METHOD_NAME = "calculateC2SReceivervalue";
		if (LOG.isDebugEnabled()) {
			LOG.debug(className, METHOD_NAME);
		}
		CalculateTransferValueResponseVO response = new CalculateTransferValueResponseVO();
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		StringBuilder loggerValue = new StringBuilder();
		try {


			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, responseSwagger);



			//To use same functions written in struts for reuse - preparing request objects

			// preparing Transfer Object
			final C2STransferVO c2sTransferVO = new C2STransferVO();
			c2sTransferVO.setTransferValue(PretupsBL.getSystemAmount(requestVO.getRequestedAmount()));
			c2sTransferVO.setRequestedAmount(PretupsBL.getSystemAmount(requestVO.getRequestedAmount()));
			final C2STransferItemVO itemVO1 = new C2STransferItemVO();
			final C2STransferItemVO itemVO2 = new C2STransferItemVO();
			final Date currentDate = new Date();
			itemVO2.setPreviousExpiry(currentDate);
			itemVO2.setTransferDateTime(currentDate);
			itemVO2.setTransferDate(currentDate);
			final ArrayList itemList = new ArrayList();
			itemList.add(itemVO1);
			itemList.add(itemVO2);
			c2sTransferVO.setTransferItemList(itemList);


			// preparing cardgroupVo from requestVO
			CardGroupDetailsVO cardGroupDetailVO = new CardGroupDetailsVO();
			if (!BTSLUtil.isNullString(requestVO.getCardGroupSubServiceID())) {
				final String serviceID = requestVO.getCardGroupSubServiceID();
				final int index = serviceID.indexOf(":");
				if (index != -1) {
					cardGroupDetailVO.setServiceTypeSelector(serviceID.replace(':', '_'));
				}
			}

			cardGroupDetailVO.setMultipleOf(PretupsBL.getSystemAmount(requestVO.getMultipleOf()));
			cardGroupDetailVO.setReceiverAccessFeeRate(requestVO.getReceiverAccessFeeRate());
			cardGroupDetailVO.setReceiverTax2Rate(requestVO.getReceiverTax2Rate());
			cardGroupDetailVO.setReceiverTax1Rate(requestVO.getReceiverTax1Rate());
			cardGroupDetailVO.setReceiverAccessFeeType(requestVO.getReceiverAccessFeeType());
			cardGroupDetailVO.setMinReceiverAccessFee(PretupsBL.getSystemAmount(requestVO.getMinReceiverAccessFee()));
			cardGroupDetailVO.setMaxReceiverAccessFee(PretupsBL.getSystemAmount(requestVO.getMaxReceiverAccessFee()));
			cardGroupDetailVO.setReceiverTax1Type(requestVO.getReceiverTax1Type());
			cardGroupDetailVO.setReceiverTax2Type(requestVO.getReceiverTax2Type());
			cardGroupDetailVO.setValidityPeriod(requestVO.getValidityPeriod());
			cardGroupDetailVO.setBonusValidityValue(requestVO.getBonusPeriod());
			cardGroupDetailVO.setGracePeriod(requestVO.getGracePeriod());
			cardGroupDetailVO.setBonusAccList(requestVO.getBonusAccList());
			cardGroupDetailVO.setValidityPeriodType(requestVO.getValidityPeriodType());



			CardGroupBL.calculateC2SReceiverValues(c2sTransferVO, cardGroupDetailVO, requestVO.getCardGroupSubServiceID().split(":")[1], true);

			//Preparing response
			response.setStatus(HttpStatus.SC_OK);
			response.setReceiverDate(c2sTransferVO.getValidityDateToBeSetAsString());
			response.setReceiverTax1Value(c2sTransferVO.getReceiverTax1ValueAsString());
			response.setReceiverTax2Value(c2sTransferVO.getReceiverTax2ValueAsString());
			response.setReceiverAccessFee(c2sTransferVO.getReceiverAccessFeeAsString());
			response.setReceiverTransferValue(c2sTransferVO.getReceiverTransferValueAsString());

		} catch (BTSLBaseException be) {
			loggerValue.setLength(0);
			loggerValue.append("Exception:e=");
			loggerValue.append(be);

			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);

			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), be.getArgs());
			response.setMessageCode(be.getMessageKey());
			response.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
				responseSwagger.setStatus(HttpStatus.SC_UNAUTHORIZED);

			} else {
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwagger.setStatus(HttpStatus.SC_BAD_REQUEST);

			}
		} catch (Exception e) {

			loggerValue.setLength(0);
			loggerValue.append("Exception:e=");
			loggerValue.append(e);

			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.LOAD_VIEW_TRANSFER_RULE_CARD_GROUP_FAILED, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.LOAD_VIEW_TRANSFER_RULE_CARD_GROUP_FAILED);
			responseSwagger.setStatus(HttpStatus.SC_BAD_REQUEST);

		}

		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
		}
		return response;


	}



	}
