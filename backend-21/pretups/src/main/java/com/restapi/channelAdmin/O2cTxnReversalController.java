package com.restapi.channelAdmin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.common.BaseResponseMultiple;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SqlParameterEncoder;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.restapi.channelAdmin.requestVO.O2CTxnReversalListRequestVO;
import com.restapi.channelAdmin.requestVO.O2CTxnReversalRequestVO;
import com.restapi.channelAdmin.requestVO.OwnerListAndCUListO2cTxnRevRequestVO;
import com.restapi.channelAdmin.responseVO.O2CTransferDetailsResponseVO;
import com.restapi.channelAdmin.responseVO.O2CTxnReversalListResponseVO;
import com.restapi.channelAdmin.responseVO.OwnerListAndCUListO2cTxnRevResponseVO;
import com.restapi.channelAdmin.responseVO.ParentCategoryListResponseVO;
import com.restapi.channelAdmin.service.O2cTxnReversalService;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${O2cTxnReversalController.name}", description = "${O2cTxnReversalController.desc}")//@Api(tags ="Channel Admin", value="Channel Admin")
@RestController	
@RequestMapping(value = "/v1/channeladmin")
public class O2cTxnReversalController {
	protected final Log _log = LogFactory.getLog(getClass().getName());
	public static final Log log = LogFactory.getLog(O2cTxnReversalController.class.getName());
	StringBuilder loggerValue= new StringBuilder(); 
	
	@Autowired
	private O2cTxnReversalService o2cTxnReversalService;
	
	@PostMapping(value = "/o2CTxnReversalList",consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON )
	@ResponseBody
	/*@ApiOperation(value = "Load O2c Transfer Reversal List",
			response = O2CTxnReversalListResponseVO.class, authorizations = {
					@Authorization(value = "Authorization") })
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = O2CTxnReversalListResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${o2CTxnReversalList.summary}", description="${o2CTxnReversalList.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = O2CTxnReversalListResponseVO.class))
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
	public O2CTxnReversalListResponseVO o2CTxnReversalList(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse responseSwag, 
			@Parameter(description = "searchBy", required = true)//allowableValues = "TRANSACTIONID, MSISDN, ADVANCE")
			@RequestParam("searchBy") String searchBy,
			@RequestBody O2CTxnReversalListRequestVO requestVO)
			throws BTSLBaseException, SQLException, JsonParseException, JsonMappingException, IOException, Exception 
	{
		final String methodName = "o2CTxnReversalList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered :" + requestVO);
		}

		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);
		
		O2CTxnReversalListResponseVO response = new O2CTxnReversalListResponseVO();
		
		try {
			/*
			 * Authentication
			 */
			OAuthUser oAuthUserData = new OAuthUser();
			oAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(oAuthUserData, headers, new BaseResponseMultiple());
			
			searchBy = SqlParameterEncoder.encodeParams(searchBy);
						
			o2cTxnReversalService.getO2CTxnReversalList(requestVO, response, responseSwag, oAuthUserData, locale, searchBy);
							

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
	
	
	@GetMapping(value = "/o2ctxndetails", produces = MediaType.APPLICATION_JSON )
	@ResponseBody
	/*@ApiOperation(value = "Load O2C Transaction Details", notes = SwaggerAPIDescriptionI.O2C_TXN_ENQUIRY,
			response = O2CTxnReversalListResponseVO.class, authorizations = {
					@Authorization(value = "Authorization") })
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = O2CTransferDetailsResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${o2ctxndetails.summary}", description="${o2ctxndetails.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = O2CTransferDetailsResponseVO.class))
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
	public O2CTransferDetailsResponseVO transactionDetails(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse responseSwag, 
			@Parameter(description = "transactionID", required = true) @RequestParam("transactionID") String transactionID)
			throws Exception 
	{
		final String methodName = "transactionDetails";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered :" + transactionID);
		}

		
		O2CTransferDetailsResponseVO response = o2cTxnReversalService.enquiryDetail(headers, transactionID, responseSwag);
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exiting with status code:" + response.getStatus());
		}
		return response;

	}


	
	@PostMapping(value = "/o2ctxnreversal",consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON )
	@ResponseBody
	/*@ApiOperation(value = "Reverse O2C Transaction", notes = SwaggerAPIDescriptionI.O2C_TXN_REVERSAL,
			response = O2CTxnReversalListResponseVO.class, authorizations = {
					@Authorization(value = "Authorization") })
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Created", response = BaseResponse.class),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${o2ctxnreversal.summary}", description="${o2ctxnreversal.description}",

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
	public BaseResponse transactionReversalO2C(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse responseSwag, 
			@RequestBody O2CTxnReversalRequestVO requestVO)
			throws Exception 
	{
		final String methodName = "transactionReversalO2C";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered :" + requestVO);
		}

		
		BaseResponse response = o2cTxnReversalService.reverseO2CTxn(headers, requestVO, responseSwag);
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exiting with status code:" + response.getStatus());
		}
		return response;

	}
	
	
	@PostMapping(value = "/ownerListAndcuListO2ctxnrev",consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON )
	@ResponseBody
	/*@ApiOperation(value = "Load Owner User List or Channel User List for O2c Transaction Reversal",
			response = OwnerListAndCUListO2cTxnRevResponseVO.class, authorizations = {
					@Authorization(value = "Authorization") })
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = OwnerListAndCUListO2cTxnRevRequestVO.class),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${ownerListAndcuListO2ctxnrev.summary}", description="${ownerListAndcuListO2ctxnrev.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = OwnerListAndCUListO2cTxnRevResponseVO.class))
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



	public OwnerListAndCUListO2cTxnRevResponseVO OwnerListAndCUListO2cTxnRev(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse responseSwag, 
			@Parameter(description = "searchFor", required = true)//allowableValues = "OWNERUSER, CHANNELUSER")
			@RequestParam("searchFor") String searchFor,
			@RequestBody OwnerListAndCUListO2cTxnRevRequestVO requestVO)
			throws BTSLBaseException, SQLException, JsonParseException, JsonMappingException, IOException, Exception 
	{
		final String methodName = "OwnerListAndCUListO2cTxnRev";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered :" + requestVO);
		}

		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);
		
		OwnerListAndCUListO2cTxnRevResponseVO response = new OwnerListAndCUListO2cTxnRevResponseVO();
		Connection con = null;MComConnectionI mcomCon = null;
		UserDAO userDao =null;
		ChannelUserVO sessionUserVO = null;
		ArrayList<ListValueVO> list = new ArrayList<ListValueVO>();
		ChannelUserWebDAO channelUserWebDAO = null;
		try {
			/*
			 * Authentication
			 */
			OAuthUser oAuthUserData = new OAuthUser();
			oAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(oAuthUserData, headers, new BaseResponseMultiple());
			
			searchFor = SqlParameterEncoder.encodeParams(searchFor);
						
			final CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();

            userDao = new UserDAO();
            channelUserWebDAO = new ChannelUserWebDAO();
            
            sessionUserVO = userDao.loadAllUserDetailsByLoginID(con,oAuthUserData.getData().getLoginid());
            final CategoryVO categoryVO = categoryWebDAO.loadOwnerCategory(con, requestVO.getDomain());
            String ownerUserCat="";
            if (categoryVO != null) {
            	ownerUserCat=categoryVO.getCategoryCode();
            }
            
            if(searchFor.equals("OWNERUSER")) {
            	String ownerUsername="%" + requestVO.getOwnerUsername() + "%";
            	list = channelUserWebDAO.loadUsersForEnquiry(con, ownerUserCat, sessionUserVO.getNetworkID(), ownerUsername, null, requestVO.getGeography(),
                        sessionUserVO.getUserID(), false);
            }else if(searchFor.equals("CHANNELUSER")) {
            	String channelUsername="%" + requestVO.getChannelUserUsername() + "%";
            	list = channelUserWebDAO.loadUsersForEnquiry(con, requestVO.getCategory(), sessionUserVO.getNetworkID(), channelUsername, requestVO.getOwnerUserId(), requestVO.getGeography(), sessionUserVO.getUserID(), false);
            }
            
            response.setList(list);
            response.setSize(list.size());
            response.setMessage(PretupsI.SUCCESS);
            response.setStatus(Integer.toString(HttpStatus.SC_OK));
            responseSwag.setStatus(HttpStatus.SC_OK);
						
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
			try {
				if (mcomCon != null) {
					mcomCon.close("O2cTxnReversalController#OwnerListAndCUListO2cTxnRev");
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
				log.debug(methodName, "Exited");
			}
		}
		return response;

	}
	
	@GetMapping(value = "/getParentCategoryList",produces = MediaType.APPLICATION_JSON )
	@ResponseBody
	/*@ApiOperation(value = "Load list of Parent Category on the basis of child category",
			response = ParentCategoryListResponseVO.class, authorizations = {
					@Authorization(value = "Authorization") })
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = ParentCategoryListResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${getParentCategoryList.summary}", description="${getParentCategoryList.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ParentCategoryListResponseVO.class))
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



	public ParentCategoryListResponseVO parentCategoryList(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse responseSwag, 
			@Parameter(description = "categoryCode", required = true)
			@RequestParam("categoryCode") String categoryCode)
			throws BTSLBaseException, SQLException, JsonParseException, JsonMappingException, IOException, Exception 
	{
		final String methodName = "parentCategoryList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered :" + methodName);
		}

		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);
		
		ParentCategoryListResponseVO response = new ParentCategoryListResponseVO();
		try {
			/*
			 * Authentication
			 */
			OAuthUser oAuthUserData = new OAuthUser();
			oAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(oAuthUserData, headers, new BaseResponseMultiple());
			
			categoryCode = SqlParameterEncoder.encodeParams(categoryCode);
			
			o2cTxnReversalService.getParentCategoryList(response, responseSwag, oAuthUserData, locale, categoryCode);
						
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);
			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				Integer badReq = HttpStatus.SC_BAD_REQUEST;
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
			Integer fail = PretupsI.RESPONSE_FAIL;
			response.setStatus(fail);
			response.setMessageCode("error.general.processing");
			response.setMessage(
					"Due to some technical reasons, your request could not be processed at this time. Please try later");
		} finally {
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exited");
			}
		}
		return response;

	}
	

	
}