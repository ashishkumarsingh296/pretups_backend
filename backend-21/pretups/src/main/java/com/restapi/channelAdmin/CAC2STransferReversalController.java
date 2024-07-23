package com.restapi.channelAdmin;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;


import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponseMultiple;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.restapi.channelAdmin.requestVO.CAC2STransferReversalConfirmVO;
import com.restapi.channelAdmin.requestVO.CAC2STransferReversalListRequestVO;
import com.restapi.channelAdmin.responseVO.CAC2STransferReversalResponseVO;
import com.restapi.channelAdmin.service.CAC2STransferReversalService;

import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${CAC2STransferReversalController.name}", description = "${CAC2STransferReversalController.desc}")//@Api(tags ="Channel Admin", value="Channel Admin")
@RestController	
@RequestMapping(value = "/v1/channeladmin")
public class CAC2STransferReversalController {

	protected final Log log = LogFactory.getLog(getClass().getName());
	private static long requestIdChannel = 0;
	@Autowired
	private CAC2STransferReversalService cac2sTransferReversalService;
	
	/**
	 * @author sarthak.saini
	 * @param requestVO
	 * @param headers
	 * @param responseSwag
	 * @return
	 */
	@PostMapping(value= "/CAC2STrfRevList", produces = MediaType.APPLICATION_JSON,consumes = MediaType.APPLICATION_JSON )	
	/*@ApiOperation(tags="Channel Admin",value = "Transfer Reversal List Info",
	           response = CAC2STransferReversalResponseVO.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = CAC2STransferReversalResponseVO.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${CAC2STrfRevList.summary}", description="${CAC2STrfRevList.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CAC2STransferReversalResponseVO.class))
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



	public CAC2STransferReversalResponseVO getTransferReversallList(@RequestBody CAC2STransferReversalListRequestVO requestVO,
			 @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			 HttpServletResponse responseSwag){
		
		final String methodName =  "getTransferReversallList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		OAuthUser oAuthUser =null;
		String loginID = null;
		CAC2STransferReversalResponseVO response = new CAC2STransferReversalResponseVO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
	    String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
	    Locale locale = new Locale(defaultLanguage, defaultCountry);
	     try {
	    	mcomCon = new MComConnection();
	    	con=mcomCon.getConnection();
			oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			
			loginID = oAuthUser.getData().getLoginid();
			
			response  = cac2sTransferReversalService.getTransferReversalList(con,loginID,requestVO);
			
			
		}catch (BTSLBaseException be) 
		{
			log.error(methodName, "Exceptin:e=" + be);
			log.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			response.setMessageCode(be.getMessageKey());
			response.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
			
		} 
		catch (Exception e) 
		{
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			String resmsg = RestAPIStringParser.getMessage(locale, e.getMessage(), null);
			response.setMessageCode(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			response.setMessage(resmsg);
		}
		finally
		{
			if (mcomCon != null) {
				mcomCon.close("CAC2STransferReversalController#"+ methodName);
				mcomCon = null;
			}
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, " Exited ");
	        }
		}
	  
		return response;
	}
	/**
	 * 
	 * @param requestVO
	 * @param headers
	 * @param responseSwag
	 * @return
	 */
	@PostMapping(value= "/CAC2STrfRev", produces = MediaType.APPLICATION_JSON,consumes = MediaType.APPLICATION_JSON )	
	/*@ApiOperation(tags="Channel Admin",value = "Transfer Reversal Info",
	           response = BaseResponseMultiple.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = BaseResponseMultiple.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${CAC2STrfRev.summary}", description="${CAC2STrfRev.description}",

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



	public BaseResponseMultiple confirmTransferReversal(@RequestBody CAC2STransferReversalConfirmVO requestVO,
			 @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			 HttpServletResponse responseSwag) {
		final String methodName = "confirmTransferReversal";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered :" + requestVO);
		}
		++requestIdChannel;
		final String requestIDStr = String.valueOf(requestIdChannel);
		BaseResponseMultiple response = new BaseResponseMultiple();
		OAuthUser oAuthUser =null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
	    String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
	    Locale locale = new Locale(defaultLanguage, defaultCountry);
	    try {
	    	mcomCon = new MComConnection();
	    	con=mcomCon.getConnection();
			oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			Date currentDate = new Date();
			Date currentDate1 =  (BTSLUtil.getTimestampFromUtilDate(currentDate));
			
	    	response = cac2sTransferReversalService.confirmTransferReversal(con,requestVO,headers,oAuthUser.getData().getLoginid(),requestIDStr,responseSwag);
	    }catch (BTSLBaseException be) 
		{
			log.error(methodName, "Exceptin:e=" + be);
			log.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			response.setMessageCode(be.getMessageKey());
			response.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(PretupsI.UNAUTHORIZED_ACCESS.toString());
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(PretupsI.RESPONSE_FAIL.toString());
			}
			
		} 
		catch (Exception e) 
		{
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(PretupsI.RESPONSE_FAIL.toString());
			String resmsg = RestAPIStringParser.getMessage(locale, e.getMessage(), null);
			response.setMessageCode(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			response.setMessage(resmsg);
		}
		finally
		{
			if (mcomCon != null) {
				mcomCon.close("CAC2STransferReversalController#"+ methodName);
				mcomCon = null;
			}
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, " Exited ");
	        }
		}
	  
		return response;
}
}
