package com.restapi.o2c.service;


import com.btsl.util.BTSLUtil;
import io.swagger.v3.oas.annotations.Parameter;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Parameter;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponseMultiple;
import com.btsl.common.MasterErrorList;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.requesthandler.DownloadUserListController;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 
 * @author deepa.shyam
 * This controller initiates an O2C voucher or stock request(multi-product) by a channel admin.
 * Also lists major errors(if any) in the response
 */

@io.swagger.v3.oas.annotations.tags.Tag(name = "${O2cController.name}", description = "${O2cController.desc}")//@Api(tags = "O2C Services", defaultValue = "O2C Request")
@RestController
@RequestMapping(value = "/v1/o2c")
public class O2cController {

	public static final Log log = LogFactory.getLog(O2cController.class.getName());
	
	@Autowired
	private O2CServiceI o2cServiceI;

	@PostMapping(value = "/o2cvouchertransfer", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody

	/*@ApiOperation(value = "O2C Request for Voucher Transfer"
			, response = BaseResponseMultiple.class, authorizations = {
					@Authorization(value = "Authorization") })

	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = BaseResponseMultiple.class),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${o2cvouchertransfer.summary}", description="${o2cvouchertransfer.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = BaseResponseMultiple.class))
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


	
	    public BaseResponseMultiple<JsonNode> processVoucherO2CRequest(
	    		@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
	            @Parameter(description = SwaggerAPIDescriptionI.O2C_VOUCHER_TRF, required = true)
	            @RequestBody O2CVoucherTransferRequestVO o2CVoucherTransferRequestVO,HttpServletResponse response1) throws Exception {
			 final String methodName = "processVoucherO2CRequest";
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Entered ");
			}
			BaseResponseMultiple<JsonNode> apiResponse = new BaseResponseMultiple<>();;
			O2CVoucherTransferService voucherO2cTrfService = new O2CVoucherTransferService();
			try{
				//Aunthenticate the token
				o2CVoucherTransferRequestVO.setData(new OAuthUserData());
				OAuthenticationUtil.validateTokenApi(o2CVoucherTransferRequestVO, headers, apiResponse); 
				apiResponse=voucherO2cTrfService.processVoucherTransferRequest(o2CVoucherTransferRequestVO,response1);
			}catch (BTSLBaseException be) {
				log.error(methodName, "BTSLBaseException " + be.getMessage());
		        log.errorTrace(methodName, be);
		        	String resmsg = RestAPIStringParser.getMessage(
							new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), be.getMessageKey(),
							null);
		        	apiResponse.setMessageCode(be.getMessageKey());
		        	apiResponse.setMessage(resmsg);
		        	apiResponse.setService("O2CVOUCHERTRFRESP");
		            return apiResponse;
		        
		    } catch (Exception e) {
		    	log.error(methodName, "Exception " + e);
				log.errorTrace(methodName, e);
				apiResponse.setStatus("400");
				apiResponse.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
		        String resmsg = RestAPIStringParser.getMessage(
						new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.REQ_NOT_PROCESS,
						null);
		        apiResponse.setMessage(resmsg);
			} finally {
				if (log.isDebugEnabled()) {
					log.debug(methodName, " Exited ");
				}
			}
	        return apiResponse;
	    }


	
	@PostMapping(value = "/o2cvoucherInitiate", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody

	/*@ApiOperation(value = "O2C Request for Voucher Initiate"
			, response = BaseResponseMultiple.class, authorizations = {
					@Authorization(value = "Authorization") })

	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = BaseResponseMultiple.class),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${o2cvoucherInitiate.summary}", description="${o2cvoucherInitiate.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = BaseResponseMultiple.class))
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


	
	    public BaseResponseMultiple<JsonNode> processVoucherO2CInitiateRequest(
	    		@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
	            @Parameter(description = SwaggerAPIDescriptionI.O2C_VOUCHER_INI, required = true)
	            @RequestBody O2CVoucherInitiateRequestVO o2CVoucherInitiateRequestVO,HttpServletResponse response1) throws Exception {
			 final String methodName = "processVoucherO2CInitiateRequest";
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Entered ");
			}
			BaseResponseMultiple<JsonNode> apiResponse = new BaseResponseMultiple<>();;
			O2CVoucherInitiateService voucherO2cIniService = new O2CVoucherInitiateService();
			try{
				ArrayList<MasterErrorList> masterErrorListMain = new ArrayList<MasterErrorList>();
				//Aunthenticate the token
				o2CVoucherInitiateRequestVO.setData(new OAuthUserData());
				OAuthenticationUtil.validateTokenApi(o2CVoucherInitiateRequestVO, headers, apiResponse); 
				apiResponse=voucherO2cIniService.processVoucherInitiateRequest(o2CVoucherInitiateRequestVO,response1);
			}catch (BTSLBaseException be) {
				log.error(methodName, "BTSLBaseException " + be.getMessage());
		        log.errorTrace(methodName, be);
		        	String resmsg = RestAPIStringParser.getMessage(
							new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), be.getMessageKey(),
							null);
		        	apiResponse.setMessageCode(be.getMessageKey());
		        	apiResponse.setMessage(resmsg);
		        	apiResponse.setService("O2CVOUCHERINIRESP");
		            return apiResponse;
		        
		    } catch (Exception e) {
		    	log.error(methodName, "Exception " + e);
				log.errorTrace(methodName, e);
	        	apiResponse.setService("O2CVOUCHERINIRESP");
				apiResponse.setStatus("400");
				apiResponse.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
		        String resmsg = RestAPIStringParser.getMessage(
						new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.REQ_NOT_PROCESS,
						null);
		        apiResponse.setMessage(resmsg);
			} finally {
				if (log.isDebugEnabled()) {
					log.debug(methodName, " Exited ");
				}
			}
	        return apiResponse;
	    }
	
	@PostMapping(value = "/o2cWithdrawl", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody

	/*@ApiOperation(value = "O2C Withdrawl"
			, response = BaseResponseMultiple.class, authorizations = {
					@Authorization(value = "Authorization") })

	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = BaseResponseMultiple.class),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${o2cWithdrawl.summary}", description="${o2cWithdrawl.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = BaseResponseMultiple.class))
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
	
	    public BaseResponseMultiple<JsonNode> processO2CWithdraw(
	    		@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
	            @Parameter(description = SwaggerAPIDescriptionI.O2C_VOUCHER_INI,required = true)
	            @RequestBody O2CWithdrawlRequestVO o2CWithdrawlRequestVO,HttpServletResponse response1) throws Exception {
			 final String methodName = "processO2CWithdraw";
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Entered ");
			}
			BaseResponseMultiple<JsonNode> apiResponse = new BaseResponseMultiple<>();
			Connection con = null;
			MComConnectionI mcomCon = null;
			
			try{
				mcomCon = new MComConnection();
				con = mcomCon.getConnection();
				//Aunthenticate the token
				OAuthUser oAuthUserData=new OAuthUser();
				UserDAO userDao = new UserDAO();
				
				oAuthUserData.setData(new OAuthUserData());
				
				OAuthenticationUtil.validateTokenApi(oAuthUserData,headers,response1);
				apiResponse.setService("O2CWITHDRAWMULRESP");
				
				String loginId =  oAuthUserData.getData().getLoginid();
				ChannelUserVO channelUserVO = userDao.loadAllUserDetailsByLoginID(con, loginId);
				
				o2cServiceI.processWithdrawRequest(channelUserVO, o2CWithdrawlRequestVO, apiResponse, response1);
				

		} catch (BTSLBaseException be) {
			log.error(methodName, "BTSLBaseException " + be.getMessage());
			log.errorTrace(methodName, be);
			String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
					be.getMessageKey(), null);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				apiResponse.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
			} else {
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				apiResponse.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			}
			apiResponse.setMessageCode(be.getMessageKey());
			apiResponse.setMessage(resmsg);
			apiResponse.setService("O2CWITHDRAWMULRESP");
			return apiResponse;

		} catch (Exception e) {
		    	log.error(methodName, "Exception " + e);
				log.errorTrace(methodName, e);
	        	apiResponse.setService("O2CWITHDRAWMULRESP");
				apiResponse.setStatus("400");
				apiResponse.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
		        String resmsg = RestAPIStringParser.getMessage(
						new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.REQ_NOT_PROCESS,
						null);
		        apiResponse.setMessage(resmsg);
			} finally {
				if (log.isDebugEnabled()) {
					log.debug(methodName, " Exited ");
				}
			}
	        return apiResponse;
	    }
	
	
	@PostMapping(value = "/o2cVoucherApproval", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody

	/*@ApiOperation(value = "O2C Voucher Approval",
	           notes=("Api Info:") + ("\n") + ("status = Y for Approval or R for Rejection .")+ 
	           ("\n") + ("approvalLevel can have values one,two or three."),

			response = BaseResponseMultiple.class, authorizations = {
					@Authorization(value = "Authorization") })

	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = BaseResponseMultiple.class),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${o2cVoucherApproval.summary}", description="${o2cVoucherApproval.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = BaseResponseMultiple.class))
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

	    public BaseResponseMultiple<JsonNode> processO2CVoucherApproval(
	    		@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
	            @Parameter(description = SwaggerAPIDescriptionI.O2C_VOUCHER_APPROV, required = true)
	            @RequestBody O2CVoucherApprovalRequestVO o2CVoucherApprovalRequestVO,HttpServletResponse response1) throws Exception {
			 final String methodName = "processO2CVoucherApproval";
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Entered ");
			}
			BaseResponseMultiple<JsonNode> apiResponse = new BaseResponseMultiple<>();
			Connection con = null;
			MComConnectionI mcomCon = null;
			
			try{
				mcomCon = new MComConnection();
				con = mcomCon.getConnection();
				//Aunthenticate the token
				OAuthUser oAuthUserData=new OAuthUser();
				UserDAO userDao = new UserDAO();
				
				oAuthUserData.setData(new OAuthUserData());
				
				OAuthenticationUtil.validateTokenApi(oAuthUserData,headers,response1);
				apiResponse.setService("O2CVOUCHERAPPRVRESP");
				
				String loginId =  oAuthUserData.getData().getLoginid();
				String msisdn =  oAuthUserData.getData().getMsisdn();
				
				
				ChannelUserVO  senderVO = (ChannelUserVO)userDao.loadAllUserDetailsByLoginID(con, loginId);
				if (log.isDebugEnabled()) {
					log.debug("process", "Entered Sender VO: " + senderVO);
				}
				
				senderVO.setUserPhoneVO(userDao.loadUserAnyPhoneVO(con, msisdn));
				if (!BTSLUtil.isNullObject(senderVO.getUserPhoneVO()))
					senderVO.setPinReset(senderVO.getUserPhoneVO().getPinReset());

				//senderVO.setServiceTypes(serviceType);

				ChannelUserDAO channelUserDAO = new ChannelUserDAO();
				
				if(!senderVO.getCategoryCode().equalsIgnoreCase(PretupsI.OPERATOR_CATEGORY) 
						&& !senderVO.getCategoryCode().equalsIgnoreCase(PretupsI.PWD_CAT_CODE_CCE)
						&& !senderVO.getCategoryCode().equalsIgnoreCase(PretupsI.SUPER_CHANNEL_ADMIN))
				{
					throw new BTSLBaseException(
							DownloadUserListController.class.getName(),
							methodName,
							PretupsErrorCodesI.XML_ERROR_USER_NOT_AUTHORIZED);
				
				}
			
				
				o2cServiceI.processVoucherApprvRequest(senderVO, o2CVoucherApprovalRequestVO, apiResponse, response1);
				

		} catch (BTSLBaseException be) {
			log.error(methodName, "BTSLBaseException " + be.getMessage());
			log.errorTrace(methodName, be);
			String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
					be.getMessageKey(), null);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				apiResponse.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
			} else {
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				apiResponse.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			}
			apiResponse.setMessageCode(be.getMessageKey());
			apiResponse.setMessage(resmsg);
			apiResponse.setService("O2CVOUCHERAPPRVRESP");
			return apiResponse;

		} catch (Exception e) {
		    	log.error(methodName, "Exception " + e);
				log.errorTrace(methodName, e);
	        	apiResponse.setService("O2CVOUCHERAPPRVRESP");
				apiResponse.setStatus("400");
				apiResponse.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
		        String resmsg = RestAPIStringParser.getMessage(
						new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.REQ_NOT_PROCESS,
						null);
		        apiResponse.setMessage(resmsg);
			} finally {
				

	        	try {
	        		if (mcomCon != null) {
	        			mcomCon.close("O2CController#" + methodName);
	        			mcomCon = null;
	        		}
	        	} 
	        	catch (Exception e) {
	        		log.errorTrace(methodName, e);
	        	}
	        	if (log.isDebugEnabled()) {
	        		log.debug(methodName, " Exited ");
	        	}
	        
			}
	        return apiResponse;
	    }

	/**
	 * To get O2C products in system
	 * @param headers
	 * @param responseSwag
	 * @throws Exception
	 */
	@GetMapping(value= "/getO2CProducts", produces = MediaType.APPLICATION_JSON)	
	@ResponseBody
	/*@ApiOperation(value = "Get Products For O2C",
	           response = O2CProductsResponseVO.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = O2CProductsResponseVO.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })
	*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${getO2CProducts.summary}", description="${getO2CProducts.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = O2CProductsResponseVO.class))
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

	public O2CProductsResponseVO getO2CProducts(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			 HttpServletResponse responseSwag
					)throws Exception{
		

		final String methodName =  "getO2CProducts";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		Connection con = null;
	    MComConnectionI mcomCon = null;
	    O2CProductsResponseVO response=null;
	    response = new O2CProductsResponseVO();
		 String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
	     String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		
		 try {
			 
			    mcomCon = new MComConnection();
				con = mcomCon.getConnection();
				//Authenticate the token
				OAuthUser oAuthUserData=new OAuthUser();
				UserDAO userDao = new UserDAO();
				
				oAuthUserData.setData(new OAuthUserData());
				
				OAuthenticationUtil.validateTokenApi(oAuthUserData,headers,responseSwag);
				response.setService("O2CPRODUCTDOWNLOADRESP");
				
				String loginId =  oAuthUserData.getData().getLoginid();
				String msisdn =  oAuthUserData.getData().getMsisdn();
				ChannelUserVO  userVO = (ChannelUserVO)userDao.loadAllUserDetailsByLoginID(con, loginId);
				o2cServiceI.processO2CProductDownlaod(con , userVO , response);
				
				if(response.getProductsList().size()>0) {
					response.setStatus(String.valueOf(PretupsI.RESPONSE_SUCCESS));
					responseSwag.setStatus(HttpStatus.SC_OK);
					response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			 		String resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), PretupsErrorCodesI.SUCCESS, null);
			        response.setMessage(resmsg);
				}else {
					response.setStatus(String.valueOf(PretupsI.RESPONSE_FAIL));
					responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);					
					String badReq=Integer.toString(HttpStatus.SC_BAD_REQUEST) ;
					response.setStatus(badReq);
				}
				
		    
		 }catch (BTSLBaseException be) {
	      	 log.error(methodName, "Exception:e=" + be);
	         log.errorTrace(methodName, be);
	         if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
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
	    	try {
	        	if (mcomCon != null) {
					mcomCon.close("O2CController#"+methodName);
					mcomCon = null;
				}
	        } catch (Exception e) {
	        	log.errorTrace(methodName, e);
	        }
	    	
	        try {
	            if (con != null) {
	                con.close();
	            }
	        } catch (Exception e) {
	            log.errorTrace(methodName, e);
	        }
	        
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, " Exited ");
	        }
	    }

		return response;
	}


}
