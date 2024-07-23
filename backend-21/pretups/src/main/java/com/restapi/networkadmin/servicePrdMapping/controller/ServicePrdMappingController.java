package com.restapi.networkadmin.servicePrdMapping.controller;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Date;
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
import com.btsl.pretups.master.businesslogic.SelectorAmountMappingVO;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.restapi.networkadmin.servicePrdMapping.requestVO.DeleteServicePrdMappingReqVO;
import com.restapi.networkadmin.servicePrdMapping.requestVO.SaveServicePrdMappingReqVO;
import com.restapi.networkadmin.servicePrdMapping.responseVO.SearchServicePrdMappingRespVO;
import com.restapi.networkadmin.servicePrdMapping.responseVO.ServicePrdInputRespVO;
import com.restapi.networkadmin.servicePrdMapping.service.ServicePrdMappingInfImpl;

import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${ServicePrdMappingController.name}", description = "${ServicePrdMappingController.desc}")//@Api(tags="Service Product Mapping")
@RestController
@RequestMapping(value = "/v1/networkadmin/serviceProductMapping")
public class ServicePrdMappingController {
	
	public static final Log log = LogFactory.getLog(ServicePrdMappingController.class.getName());
	
	@Autowired
	ServicePrdMappingInfImpl servicePrdMappingInfImpl;
	
	
	@GetMapping(value= "/getUIinputDetails", produces = MediaType.APPLICATION_JSON)	
	@ResponseBody
	/*@ApiOperation(value = "This API is to fetch the UI details for drop down values",
	           response = ServicePrdInputRespVO.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = ServicePrdInputRespVO.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${getUIinputDetails.summary}", description="${getUIinputDetails.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ServicePrdInputRespVO.class))
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

	public ServicePrdInputRespVO getUIinputDetails(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest
			) throws Exception {
		
		
		final String methodName = "getUIinputDetails";
		if(log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		Connection con = null;
		MComConnectionI mcomCon = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

		UserVO userVO = null;
		UserDAO userDAO = null;
		
		ServicePrdInputRespVO response = new ServicePrdInputRespVO();
		
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
			OAuthUser oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, response1);
			
			userVO = new UserVO();
			userDAO = new UserDAO();
			userVO  = userDAO.loadUsersDetails(con, oAuthUser.getData().getMsisdn());
			
			response = servicePrdMappingInfImpl.getServicePrdMappingUIInputValues(userVO,locale);
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
	

	
	
	
	
	
	@GetMapping(value= "/serarchServicePrdMapping", produces = MediaType.APPLICATION_JSON)	
	@ResponseBody
	/*@ApiOperation(value = "",
	           response = SearchServicePrdMappingRespVO.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = SearchServicePrdMappingRespVO.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${serarchServicePrdMapping.summary}", description="${serarchServicePrdMapping.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = SearchServicePrdMappingRespVO.class))
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

	public SearchServicePrdMappingRespVO serarchServicePrdMapping(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest,
			@RequestParam("serviceType") String serviceType,
			@RequestParam("selectorCode") String selectorCode
			) throws Exception {
		
		
		final String methodName = "serarchServicePrdMapping";
		if(log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		Connection con = null;
		MComConnectionI mcomCon = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

		UserVO userVO = null;
		UserDAO userDAO = null;
		
		SearchServicePrdMappingRespVO response = new SearchServicePrdMappingRespVO();
		
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
			OAuthUser oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, response1);
			
			userVO = new UserVO();
			userDAO = new UserDAO();
			userVO  = userDAO.loadUsersDetails(con, oAuthUser.getData().getMsisdn());
			
			response = servicePrdMappingInfImpl.searchServicePrdMapping(serviceType,selectorCode, userVO,locale);
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
	
	
	
	@PostMapping(value= "/saveServicePrdMapping", produces = MediaType.APPLICATION_JSON)	
	@ResponseBody
	/*@ApiOperation(value = "This API is to save the details of Service Product mapping",
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
	@io.swagger.v3.oas.annotations.Operation(summary = "${saveServicePrdMapping.summary}", description="${saveServicePrdMapping.description}",

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

	public BaseResponse saveServicePrdMapping(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest,
			@RequestBody SaveServicePrdMappingReqVO saveServicePrdMappingReqVO
			) throws Exception {
		
		
		final String methodName = "saveServicePrdMapping";
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
			userVO  = userDAO.loadUsersDetails(con, oAuthUser.getData().getMsisdn());
			
			if(saveServicePrdMappingReqVO.getAmount()<=0) {
		        throw new BTSLBaseException(this, methodName,
								PretupsI.SERVICEPRDMAPPING_INVALID_AMOUNT);
		        
			}
			
			
			SelectorAmountMappingVO  p_amountMappingVO =constructVOFromForm( saveServicePrdMappingReqVO,  userVO);
			response =servicePrdMappingInfImpl.saveServicePrdMapping(p_amountMappingVO, userVO, locale);
			
			
		//	response = networkServicesServiceI.updateNetworkServices(headers,response1,con,mcomCon,locale,userVO,response,updateNetworkServicesRequestVO);
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
	

	
	public SelectorAmountMappingVO constructVOFromForm( SaveServicePrdMappingReqVO p_form, UserVO p_userVO) throws Exception {
		final String methodName ="constructVOFromForm";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exiting:=" + methodName);
		}
		SelectorAmountMappingVO  p_amountMappingVO = new SelectorAmountMappingVO();
        Date currentDate = new Date();
        p_amountMappingVO.setAmount(PretupsBL.getSystemAmount(p_form.getAmount()));
        if (p_form.getSelectorCode().split(":").length == 2) {
            p_amountMappingVO.setSelectorCode(p_form.getSelectorCode().split(":")[1]);
        } else {
            p_amountMappingVO.setSelectorCode(p_form.getSelectorCode());
        }
        p_amountMappingVO.setServiceType(p_form.getServiceType());
        p_amountMappingVO.setStatus(PretupsI.YES);
        
        if(BTSLUtil.isNullString(p_form.getModifyAllowed())) {
        	p_amountMappingVO.setModifiedAllowed(PretupsI.NO);	
        }else {
        p_amountMappingVO.setModifiedAllowed(p_form.getModifyAllowed());
        }
        p_amountMappingVO.setCreatedOn(currentDate);
        p_amountMappingVO.setCreatedBy(p_userVO.getUserID());
        p_amountMappingVO.setModifiedOn(currentDate);
        p_amountMappingVO.setModifiedBy(p_userVO.getUserID());

        if (log.isDebugEnabled()) {
        	log.debug("constructVOFromForm", "Exited");
        }
        return p_amountMappingVO;
    }


	
	@PostMapping(value= "/modifyServicePrdMapping", produces = MediaType.APPLICATION_JSON)	
	@ResponseBody
	/*@ApiOperation(value = "This API is to modify the details of Service Product mapping",
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

	@io.swagger.v3.oas.annotations.Operation(summary = "${modifyServicePrdMapping.summary}", description="${modifyServicePrdMapping.description}",

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

	public BaseResponse modifyServicePrdMapping(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest,
			@RequestBody SaveServicePrdMappingReqVO saveServicePrdMappingReqVO
			) throws Exception {
		
		
		final String methodName = "modifyServicePrdMapping";
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
			userVO  = userDAO.loadUsersDetails(con, oAuthUser.getData().getMsisdn());
			if(saveServicePrdMappingReqVO.getAmount()<=0) {
		        throw new BTSLBaseException(this, methodName,
								PretupsI.SERVICEPRDMAPPING_INVALID_AMOUNT);
			}
			SelectorAmountMappingVO  p_amountMappingVO =constructVOFromForm( saveServicePrdMappingReqVO,  userVO);
			response =servicePrdMappingInfImpl.modifyServicePrdMapping(p_amountMappingVO, userVO, locale);
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


	@PostMapping(value= "/deleteServicePrdMapping", produces = MediaType.APPLICATION_JSON)	
	@ResponseBody
	/*@ApiOperation(value = "This API is to delete the details of Service Product mapping",
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

	@io.swagger.v3.oas.annotations.Operation(summary = "${deleteServicePrdMapping.summary}", description="${deleteServicePrdMapping.description}",

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

	public BaseResponse deleteServicePrdMapping(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest,
			@RequestBody DeleteServicePrdMappingReqVO deleteServicePrdMappingReqVO
			) throws Exception {
		
		
		final String methodName = "deleteServicePrdMapping";
		if(log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		Connection con = null;
		MComConnectionI mcomCon = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

		UserVO userVO = null;
		UserDAO userDAO = null;
		
		BaseResponse response = new BaseResponse();
		SaveServicePrdMappingReqVO saveServicePrdMappingReqVO = new SaveServicePrdMappingReqVO();
		
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
			OAuthUser oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, response1);
			
			userVO = new UserVO();
			userDAO = new UserDAO();
			userVO  = userDAO.loadUsersDetails(con, oAuthUser.getData().getMsisdn());
			saveServicePrdMappingReqVO.setServiceType(deleteServicePrdMappingReqVO.getServiceType());
			saveServicePrdMappingReqVO.setSelectorCode(deleteServicePrdMappingReqVO.getSelectorCode());
			SelectorAmountMappingVO  p_amountMappingVO =constructVOFromForm( saveServicePrdMappingReqVO,  userVO);
			response =servicePrdMappingInfImpl.deleteServicePrdMapping(p_amountMappingVO, userVO, locale);
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
