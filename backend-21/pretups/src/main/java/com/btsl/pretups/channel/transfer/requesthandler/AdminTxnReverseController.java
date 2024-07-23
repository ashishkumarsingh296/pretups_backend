package com.btsl.pretups.channel.transfer.requesthandler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import com.btsl.util.Constants;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
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
import com.btsl.common.BaseResponseMultiple;
import com.btsl.common.ErrorMap;
import com.btsl.common.MasterErrorList;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.login.LoginDAO;
import com.btsl.pretups.channel.transfer.businesslogic.C2CTxnsForReversalRequestVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2CTxnsForReversalResponseVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayCache;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.SessionInfoVO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SqlParameterEncoder;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.restapi.o2c.service.C2CBulkApprovalRequestVO;

import io.swagger.v3.oas.annotations.tags.Tag;

import io.swagger.v3.oas.annotations.Parameter;



import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${AdminTxnReverseController.name}", description = "${AdminTxnReverseController.desc}")//@Api(tags = "Admin Transaction Services", defaultValue = "Admin Transaction Services")

@RestController
@RequestMapping(value = "/v1/admTxn")
public class AdminTxnReverseController {
	
	protected final Log log = LogFactory.getLog(getClass().getName());
	
	@Autowired
	private AdminTxnReverseI adminTxnReverseI;

	@PostMapping(value = "/getC2CTxnsForReversal", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "C2C Txns For Reversal", response = C2CTxnsForReversalResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = C2CTxnsForReversalResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), 
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${getC2CTxnsForReversal.summary}", description="${getC2CTxnsForReversal.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = C2CTxnsForReversalResponseVO.class))
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



	public C2CTxnsForReversalResponseVO getC2CTxnsForReversal(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse responseSwag, @RequestBody C2CTxnsForReversalRequestVO c2CTxnsForReversalRequestVO)
			throws BTSLBaseException, SQLException, JsonParseException, JsonMappingException, IOException 
	{
		final String methodName = "getC2CTxnsForReversal";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		C2CTxnsForReversalResponseVO response = new C2CTxnsForReversalResponseVO();
		response.setService("LOADALLC2CTXNSFORREVERSAL");

		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);
		ArrayList<ChannelTransferVO> transferList = null;
		MComConnectionI mcomCon;
		Connection con=null;
		
		try {

			mcomCon = new MComConnection();
	        con = mcomCon.getConnection();
	        
			OAuthUser oAuthUserData = new OAuthUser();
			oAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(oAuthUserData, headers, new BaseResponseMultiple());

			String sessionUserloginId = oAuthUserData.getData().getLoginid();
			ChannelUserVO sessionUser = (new LoginDAO()).loadUserDetails(con, sessionUserloginId, oAuthUserData.getData().getPassword(), locale);

			transferList = adminTxnReverseI.getC2CTxnsForReversal(con, c2CTxnsForReversalRequestVO, responseSwag, sessionUser);
			
			response.setC2cReverseTxnList(transferList);
			response.setStatus(Integer.toString(HttpStatus.SC_OK));
			if(transferList.size() > 0) {
				response.setMessage(PretupsI.SUCCESS);
				response.setMessageCode( Integer.toString( HttpStatus.SC_OK) );
			} else {
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.NO_DETAIL_FOUND, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.NO_DETAIL_FOUND);
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
			if(con!=null)
				con.close();
			if (log.isDebugEnabled()) {
				log.debug(methodName, " Exited ");
			}
		}
		return response;
	}
	
	@GetMapping(value = "/reverseC2CTxn", produces = MediaType.APPLICATION_JSON)
	@ResponseBody	// tells spring that object returned is automatically serialized into JSON and passed back into the HttpResponse object.
	/*@ApiOperation(value = "C2C Txn Reversal", response = C2CTxnsForReversalResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = C2CTxnsForReversalResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), 
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	*/


	@io.swagger.v3.oas.annotations.Operation(summary = "${reverseC2CTxn.summary}", description="${reverseC2CTxn.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = C2CTxnsForReversalResponseVO.class))
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



	public C2CTxnsForReversalResponseVO reverseC2CTxn(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, //this param wont be set by user so set @Parameter(hidden = true)
			@Parameter(description = "txnID", required = true) @RequestParam("txnID") String txnID,
	        @Parameter(description = "networkCode", required = true) @RequestParam("networkCode") String networkCode,
	        @Parameter(description = "networkCodeFor", required = true) @RequestParam("networkCodeFor") String networkCodeFor,
	        @Parameter(description = "remark", required = true) @RequestParam("remark") String remark,
	        HttpServletResponse responseSwag
			)throws BTSLBaseException, SQLException, JsonParseException, JsonMappingException, IOException 
	{
		final String methodName = "reverseC2CTxn";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		C2CTxnsForReversalResponseVO response = new C2CTxnsForReversalResponseVO();
		response.setService("C2CTXNSREVERSAL");

		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);
		MComConnectionI mcomCon;
		Connection con=null;
		try {

			mcomCon = new MComConnection();
	        con = mcomCon.getConnection();
	        
			OAuthUser oAuthUserData = new OAuthUser();
			oAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(oAuthUserData, headers, new BaseResponseMultiple());

			String sessionUserloginId = oAuthUserData.getData().getLoginid();
//			ChannelUserVO sessionUser = (new LoginDAO()).loadUserDetails(con, sessionUserloginId, oAuthUserData.getData().getPassword(), locale);
			ChannelUserVO sessionUser = (new UserDAO()).loadAllUserDetailsByLoginID(con, sessionUserloginId);

			String defautWebGatewayCode = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WEB_GATEWAY_CODE);
			MessageGatewayVO messageGatewayVO = MessageGatewayCache.getObject(defautWebGatewayCode);
			messageGatewayVO.setGatewayCode(oAuthUserData.getReqGatewayCode());
			messageGatewayVO.setGatewayType(oAuthUserData.getReqGatewayType());
			SessionInfoVO sessionInfoVO = new SessionInfoVO();
			sessionInfoVO.setMessageGatewayVO(messageGatewayVO);
			sessionUser.setSessionInfoVO(sessionInfoVO);
			
			response = adminTxnReverseI.performC2CTxnReversal(mcomCon, con, responseSwag, sessionUser, txnID, networkCode, networkCodeFor,remark);	
			response.setStatus(Integer.toString(HttpStatus.SC_OK));
			
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
			if(con!=null) con.close();
			if (log.isDebugEnabled()) {
				log.debug(methodName, " Exited ");
			}
		}
		return response;
	}
	
}
