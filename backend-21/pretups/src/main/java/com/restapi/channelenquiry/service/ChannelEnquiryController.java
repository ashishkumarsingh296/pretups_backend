package com.restapi.channelenquiry.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Locale;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponseMultiple;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SqlParameterEncoder;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${ChannelEnquiryController.name}", description = "${ChannelEnquiryController.desc}")//@Api(tags = "Channel Enquiry", defaultValue = "Channel Enquiry")
@RestController
@RequestMapping(value = "/v1/channelEnquiry")
public class ChannelEnquiryController {
	
	public static final Log log = LogFactory.getLog(ChannelEnquiryController.class.getName());

	@Context
	private HttpServletRequest httpServletRequest;

	@Autowired
	private ChannelEnquiryService channelEnquiryService;
	
	/**
	 * @author sarthak.saini
	 * @param headers
	 * @param requestVO
	 * @param response1
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "/c2sEnquiry",consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody
    /*@ApiOperation(value = "C2S Transfer Enquiry", response = C2SEnquiryResponseVO.class ,
    		authorizations = {
    	            @Authorization(value = "Authorization")
    })
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = C2SEnquiryResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"), 
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${c2sEnquiry.summary}", description="${c2sEnquiry.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = C2SEnquiryResponseVO.class))
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

	public C2SEnquiryResponseVO c2sTransferEnquiry(
    		@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
            @Parameter(description = SwaggerAPIDescriptionI.C2STRANSFER_ENQUIRY, required = true)
            @RequestBody C2SEnquiryRequestVO requestVO,HttpServletResponse response1) throws Exception{
		   final String methodName = "c2sTransferEnquiry";
					if (log.isDebugEnabled()) {
						log.debug(methodName, "Entered ");
					}
					UserVO userVO = null;
					UserDAO userDAO = null;
					Connection con = null;
					C2SEnquiryResponseVO response = null;
					MComConnectionI mcomCon = null;
					Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
					
					try {
						response = new C2SEnquiryResponseVO();
						userVO = new UserVO();
						userDAO = new UserDAO();
						mcomCon = new MComConnection();
						con = mcomCon.getConnection();
						OAuthUser oAuthUser= null;
						OAuthUserData oAuthUserData =null;
						if(BTSLUtil.isNullString(requestVO.getReceiverMsisdn()) && BTSLUtil.isNullString(requestVO.getSenderMsisdn()) && BTSLUtil.isNullString(requestVO.getTransferID())) {
							response.setMessageCode(PretupsErrorCodesI.C2SENQRY_FILEDS_EMPTY);
							String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.C2SENQRY_FILEDS_EMPTY,new String[]{"Resend Field"});
							response.setMessage(msg);
							response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			           		response.setStatus(HttpStatus.SC_BAD_REQUEST);
			           		return response;
						}
						if(!BTSLUtil.isNumeric(requestVO.getSenderMsisdn())) {
							response.setMessageCode(PretupsErrorCodesI.C2S_USER_CODE_NOT_NUMERIC);
							String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.C2S_USER_CODE_NOT_NUMERIC,new String[]{"Resend Field"});
							response.setMessage(msg);
							response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			           		response.setStatus(HttpStatus.SC_BAD_REQUEST);
			           		return response;
						}
						if(!BTSLUtil.isNumeric(requestVO.getReceiverMsisdn())) {
							response.setMessageCode(PretupsErrorCodesI.INVALID_RECEIVER_MOBILENUMBER);
							String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_RECEIVER_MOBILENUMBER,new String[]{"Resend Field"});
							response.setMessage(msg);
							response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			           		response.setStatus(HttpStatus.SC_BAD_REQUEST);
			           		return response;
						}
						oAuthUser = new OAuthUser();
						oAuthUserData =new OAuthUserData();
						oAuthUser.setData(oAuthUserData);
						OAuthenticationUtil.validateTokenApi(oAuthUser, headers,response1);
						userVO  = userDAO.loadUsersDetails(con, oAuthUser.getData().getMsisdn());
						channelEnquiryService.loadC2STransferEnquiryList(con, userVO, requestVO, response, response1,locale);
					} catch (BTSLBaseException be) {
						log.error(methodName, "Exceptin:e=" + be);
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
	
	
	


	@PostMapping(value = "/enquiryO2cAndC2c",consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON )
	@ResponseBody
	/*@ApiOperation(value = "Load C2C OR O2C Enquiry List", notes = SwaggerAPIDescriptionI.C2C_OR_O2C_ENQ,
			response = C2cAndO2cEnquiryResponseVO.class, authorizations = {
					@Authorization(value = "Authorization") })
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = C2cAndO2cEnquiryResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), 
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${enquiryO2cAndC2c.summary}", description="${enquiryO2cAndC2c.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = C2cAndO2cEnquiryResponseVO.class))
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

	public C2cAndO2cEnquiryResponseVO loadO2cAndC2cDetails(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse responseSwag, 
			@Parameter(description = "enquiryType", required = true)//allowableValues = "O2C, C2C")
			@RequestParam("enquiryType") String enquiryType,
			@Parameter(description = "searchBy", required = true)//allowableValues = "TRANSACTIONID, MSISDN, ADVANCE")
			@RequestParam("searchBy") String searchBy,
			@RequestBody C2cAndO2cEnquiryRequestVO requestVO)
			throws BTSLBaseException, SQLException, JsonParseException, JsonMappingException, IOException, Exception 
	{
		final String methodName = "loadO2cAndC2cDetails";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered :" + requestVO);
		}

		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);
		
		C2cAndO2cEnquiryResponseVO response = new C2cAndO2cEnquiryResponseVO();
		
		try {
			/*
			 * Authentication
			 */
			OAuthUser oAuthUserData = new OAuthUser();
			oAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(oAuthUserData, headers, new BaseResponseMultiple());
			
			enquiryType = SqlParameterEncoder.encodeParams(enquiryType);
			searchBy = SqlParameterEncoder.encodeParams(searchBy);
			
			if (PretupsI.CHANNEL_TYPE_O2C.equals(enquiryType)) {
				
				channelEnquiryService.processChannelEnquiryO2c(requestVO, response, responseSwag, oAuthUserData, locale, enquiryType, searchBy);
				
			} else if (PretupsI.CHANNEL_TYPE_C2C.equals(enquiryType)) {
				
				channelEnquiryService.processChannelEnquiryC2c(requestVO, response, responseSwag, oAuthUserData, locale, enquiryType, searchBy);
			} else {
				
				if (log.isDebugEnabled()) {
					log.debug(methodName, "Enquiry type is invalid. Valid enquiry type is C2C or O2C");
				}
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_ENQUIRY_TYPE);
			}
			
			

		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);
			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				response.setStatus(Integer.toString( HttpStatus.SC_UNAUTHORIZED) );
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				String badReq = Integer.toString(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(badReq);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);

			}
			String resmsg = "";
			if (be.getArgs() != null) {
				resmsg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());

			} else {
				resmsg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);

			}
			response.setMessageCode(be.getMessage());
			response.setMessage(resmsg);
		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			String fail = Integer.toString(PretupsI.RESPONSE_FAIL);
			response.setStatus(fail);
			response.setMessageCode("error.general.processing");
			response.setMessage(
					"Due to some technical reasons, your request could not be processed at this time. Please try later");
		} finally {
			if (log.isDebugEnabled()) {
				log.debug(methodName, " Exited ");
			}
		}
		return response;

	}


	@PostMapping(value = "/userClosingBalEnq",consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody
    /*@ApiOperation(value = "User Closing Balance Eqnuiry", response = ClosingBalanceEnquiryResponseVO.class ,
    		authorizations = {
    	            @Authorization(value = "Authorization")
    })
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = ClosingBalanceEnquiryResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"), 
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${userClosingBalEnq.summary}", description="${userClosingBalEnq.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ClosingBalanceEnquiryResponseVO.class))
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


	public ClosingBalanceEnquiryResponseVO userClosingBalanceEnquiry(
    		@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
            @Parameter(description = SwaggerAPIDescriptionI.USERCLOSINGBALANCE_ENQUIRY, required = true)
            @RequestBody ClosingBalanceEnquiryRequestVO requestVO,HttpServletResponse response1) throws Exception{
		   final String methodName = "userClosingBalanceEnquiry";
					if (log.isDebugEnabled()) {
						log.debug(methodName, "Entered ");
					}
					UserVO userVO = null;
					UserDAO userDAO = null;
					Connection con = null;
					ClosingBalanceEnquiryResponseVO response = null;
					MComConnectionI mcomCon = null;
					Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
					
					try {
						response = new ClosingBalanceEnquiryResponseVO();
						userVO = new UserVO();
						userDAO = new UserDAO();
						mcomCon = new MComConnection();
						con = mcomCon.getConnection();
						OAuthUser oAuthUser= null;
						OAuthUserData oAuthUserData =null;
//						if(BTSLUtil.isNullString(requestVO.getReceiverMsisdn()) && BTSLUtil.isNullString(requestVO.getSenderMsisdn()) && BTSLUtil.isNullString(requestVO.getTransferID())) {
//							response.setMessageCode(PretupsErrorCodesI.C2SENQRY_FILEDS_EMPTY);
//							String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.C2SENQRY_FILEDS_EMPTY,new String[]{"Resend Field"});
//							response.setMessage(msg);
//							response1.setStatus(HttpStatus.SC_BAD_REQUEST);
//			           		response.setStatus(HttpStatus.SC_BAD_REQUEST);
//			           		return response;
//						}
//						if(!BTSLUtil.isNumeric(requestVO.getSenderMsisdn())) {
//							response.setMessageCode(PretupsErrorCodesI.C2S_USER_CODE_NOT_NUMERIC);
//							String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.C2S_USER_CODE_NOT_NUMERIC,new String[]{"Resend Field"});
//							response.setMessage(msg);
//							response1.setStatus(HttpStatus.SC_BAD_REQUEST);
//			           		response.setStatus(HttpStatus.SC_BAD_REQUEST);
//			           		return response;
//						}
//						if(!BTSLUtil.isNumeric(requestVO.getReceiverMsisdn())) {
//							response.setMessageCode(PretupsErrorCodesI.INVALID_RECEIVER_MOBILENUMBER);
//							String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_RECEIVER_MOBILENUMBER,new String[]{"Resend Field"});
//							response.setMessage(msg);
//							response1.setStatus(HttpStatus.SC_BAD_REQUEST);
//			           		response.setStatus(HttpStatus.SC_BAD_REQUEST);
//			           		return response;
//						}
						oAuthUser = new OAuthUser();
						oAuthUserData =new OAuthUserData();
						oAuthUser.setData(oAuthUserData);
						OAuthenticationUtil.validateTokenApi(oAuthUser, headers,response1);
						userVO = userDAO.loadAllUserDetailsByLoginID(con, oAuthUser.getData().getLoginid());
						channelEnquiryService.loadClosingBalanceData(con, userVO, requestVO, response, response1,locale);
					} catch (BTSLBaseException be) {
						log.error(methodName, "Exceptin:e=" + be);
						log.errorTrace(methodName, be);
						String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), be.getArgs());
						response.setMessageCode(be.getMessageKey());
						response.setMessage(msg);

						if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
							response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
							response.setStatus(Integer.toString(HttpStatus.SC_UNAUTHORIZED));
						} else {
							response1.setStatus(HttpStatus.SC_BAD_REQUEST);
							response.setStatus(Integer.toString(HttpStatus.SC_BAD_REQUEST));
						}
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
	
	
	@PostMapping(value = "/alertCounterSummary",consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody
    /*@ApiOperation(value = "Alert Counter Summary Enquiry", response = AlertCounterSummaryResponseVO.class ,
    		authorizations = {
    	            @Authorization(value = "Authorization")
    })
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = AlertCounterSummaryResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"), 
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${alertCounterSummary.summary}", description="${alertCounterSummary.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = AlertCounterSummaryResponseVO.class))
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

	public AlertCounterSummaryResponseVO alertCounterSummary(
    		@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
            @Parameter(description = SwaggerAPIDescriptionI.USERCLOSINGBALANCE_ENQUIRY,required = true)
            @RequestBody AlertCounterSummaryRequestVO requestVO,HttpServletResponse response1) throws Exception{
		   final String methodName = "alertCounterSummary";
					if (log.isDebugEnabled()) {
						log.debug(methodName, "Entered ");
					}
					UserVO userVO = null;
					UserDAO userDAO = null;
					Connection con = null;
					AlertCounterSummaryResponseVO response = null;
					MComConnectionI mcomCon = null;
					Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
					
					try {
						response = new AlertCounterSummaryResponseVO();
						userVO = new UserVO();
						userDAO = new UserDAO();
						mcomCon = new MComConnection();
						con = mcomCon.getConnection();
						OAuthUser oAuthUser= null;
						OAuthUserData oAuthUserData =null;
						oAuthUser = new OAuthUser();
						oAuthUserData =new OAuthUserData();
						oAuthUser.setData(oAuthUserData);
						OAuthenticationUtil.validateTokenApi(oAuthUser, headers,response1);
						userVO = userDAO.loadAllUserDetailsByLoginID(con, oAuthUser.getData().getLoginid());
						channelEnquiryService.getAlertCounterSummaryData(con, userVO, requestVO, response, response1,locale);
					} catch (BTSLBaseException be) {
						log.error(methodName, "Exceptin:e=" + be);
						log.errorTrace(methodName, be);
						String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), be.getArgs());
						response.setMessageCode(be.getMessageKey());
						response.setMessage(msg);

						if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
							response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
							response.setStatus(Integer.toString(HttpStatus.SC_UNAUTHORIZED));
						} else {
							response1.setStatus(HttpStatus.SC_BAD_REQUEST);
							response.setStatus(Integer.toString(HttpStatus.SC_BAD_REQUEST));
						}
					} catch(ParseException e) {
						log.error(methodName, "Exceptin:e=" + e);
						log.errorTrace(methodName, e);
						String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROPERTY_INVALID, new String[]{"Date"});
						response.setMessageCode(PretupsErrorCodesI.PROPERTY_INVALID);
						response.setMessage(msg);
						response1.setStatus(HttpStatus.SC_BAD_REQUEST);
						response.setStatus(Integer.toString(HttpStatus.SC_BAD_REQUEST));
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
	
	@PostMapping(value = "/batchC2cTransferDetails",consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON )
	@ResponseBody
	/*@ApiOperation(value = "Batch C2C Transfer Details", notes = SwaggerAPIDescriptionI.BATCH_C2C_TRANSFER_DETAILS,
			response = C2cAndO2cEnquiryResponseVO.class, authorizations = {
					@Authorization(value = "Authorization") })
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = C2cAndO2cEnquiryResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), 
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${batchC2cTransferDetails.summary}", description="${batchC2cTransferDetails.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = BatchC2cTransferResponseVO.class))
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

	public BatchC2cTransferResponseVO loadBatchC2cDatails(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse responseSwag, 
			@Parameter(description = "searchBy", required = true)//allowableValues = "BATCHNO, ADVANCE")
			@RequestParam("searchBy") String searchBy,
			@RequestBody BatchC2cTransferRequestVO requestVO)
			throws BTSLBaseException, SQLException, JsonParseException, JsonMappingException, IOException, Exception 
	{
		final String methodName = "loadBatchC2cDatails";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered :" + requestVO);
		}
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);		
		BatchC2cTransferResponseVO  response=new BatchC2cTransferResponseVO();
	       try { 

				OAuthUser oAuthUserData = new OAuthUser();
				oAuthUserData.setData(new OAuthUserData());
				OAuthenticationUtil.validateTokenApi(oAuthUserData, headers, new BaseResponseMultiple());
				channelEnquiryService.getBatchC2cTransferdetails(requestVO, response, responseSwag, oAuthUserData,
						locale, searchBy);
			} catch (BTSLBaseException be) {
				log.error(methodName, "Exception:e=" + be);
				log.errorTrace(methodName, be);
				if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
					response.setStatus(Integer.toString( HttpStatus.SC_UNAUTHORIZED) );
					responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
					response.setMessage("Unauthorized Request");
				} else {
					String badReq = Integer.toString(HttpStatus.SC_BAD_REQUEST);
					response.setStatus(badReq);
					responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				}
				String resmsg = "";
				if (be.getArgs() != null) {
					resmsg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
				} else {
					resmsg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				}
				response.setMessageCode(be.getMessage());
				//response.setMessage(resmsg);
			} catch (Exception e) {
				log.error(methodName, "Exceptin:e=" + e);
				log.errorTrace(methodName, e);
				String fail = Integer.toString(PretupsI.RESPONSE_FAIL);
				response.setStatus(fail);
				response.setMessageCode("error.general.processing");
				response.setMessage(
						"Due to some technical reasons, your request could not be processed at this time. Please try later");
			} finally {
				if (log.isDebugEnabled()) {
					log.debug(methodName, " Exited ");
				}
			}
		
		return response;
	}


}
