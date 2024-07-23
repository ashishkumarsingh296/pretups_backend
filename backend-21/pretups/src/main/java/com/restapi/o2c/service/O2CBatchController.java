package com.restapi.o2c.service;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Locale;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
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
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.requesthandler.C2CStockTransferMultRequestVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.restapi.user.service.FileDownloadResponse;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${O2CBatchController.name}", description = "${O2CBatchController.desc}")//@Api(tags= "O2C Batch Services",value="O2C Batch Services")
@RestController
@RequestMapping(value = "/v1/o2c")
public class O2CBatchController {
	public static final Log log = LogFactory.getLog(O2CBatchController.class.getName());
	public static OperatorUtilI _operatorUtil = null;
	private C2CStockTransferMultRequestVO c2cStockTransferMultRequestVOs;
	private static OperatorUtilI calculatorI = null;
	@Autowired
		private O2CBatchServiceI o2cBatchServiceI;
	    private static long requestIdChannel = 0;

	    RequestVO p_requestVO = null;
	    @PostMapping(value = "/o2cBatchStockTrf", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
		@ResponseBody
	    /*@ApiOperation(value = "O2C Batch Stock Transfer", response = O2CBatchTransferResponse.class ,
	    		authorizations = {
	    	            @Authorization(value = "Authorization")
	    })
		@ApiResponses(value = { 
				@ApiResponse(code = 200, message = "OK", response = O2CBatchTransferResponse.class),
				@ApiResponse(code = 400, message = "Bad Request"),
				@ApiResponse(code = 401, message = "Unauthorized"),
				@ApiResponse(code = 404, message = "Not Found") })
*/

		@io.swagger.v3.oas.annotations.Operation(summary = "${o2cBatchStockTrf.summary}", description="${o2cBatchStockTrf.description}",

				responses = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = O2CBatchTransferResponse.class))
								)
						}

						),


						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_CODE, description = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_UNAUTH_RESPONSE_CODE, description = com.btsl.util.Constants.API_UNAUTH_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_CODE, description = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						})
				}
		)


	    public O2CBatchTransferResponse processBatchO2CTrfRequest(
	    		@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
	            @Parameter(description = SwaggerAPIDescriptionI.O2C_TRANSFER_BATCH, required = true)
	            @RequestBody O2CBatchTransferRequestVO requestVO,HttpServletResponse response1,HttpServletRequest httprequest) throws Exception {
		    final String methodName = "processBatchO2CTrfRequest";
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Entered ");
			}

			Locale locale =null;
			Connection con = null;
			MComConnectionI mcomCon = null;
			O2CBatchTransferResponse response = new O2CBatchTransferResponse();
			try 
				{
				mcomCon = new MComConnection();
				con = mcomCon.getConnection();
				/*
				 * Authentication
				 * @throws BTSLBaseException
				 */
	        	final String taxClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
		        try {
		            calculatorI = (OperatorUtilI) Class.forName(taxClass).newInstance();
		        } catch (Exception e) {
		            log.errorTrace("static block", e);
		            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchWithdrawAction[initialize]", "", "", "",
		                "Exception while loading the class at the call:" + e.getMessage());
		        }
				locale= new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
				requestVO.setData(new OAuthUserData());
	        	OAuthenticationUtil.validateTokenApi(requestVO, headers,response1);
			    ++requestIdChannel;
		        final String requestIDStr = String.valueOf(requestIdChannel);
		        response = o2cBatchServiceI.processRequest(requestVO, "O2CBATCHTRF",calculatorI,requestIDStr, con, locale,httprequest, headers, response1);
			}
			catch(BTSLBaseException be){
		        log.error("processRequest", "Exceptin:e=" + be);
		        log.errorTrace(methodName, be);
	       	    String msg=RestAPIStringParser.getMessage(locale, be.getMessageKey(),null);
		        response.setMessageCode(be.getMessageKey());
		        response.setMessage(msg);
				response.setService("O2CBATCHTRFRESP");

	        	if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
	        		response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
	    	         response.setStatus("401");
	            }
	           else{
	        	   response1.setStatus(HttpStatus.SC_BAD_REQUEST);
	           		response.setStatus("400");
	           }
	        }

	        return response;
	    }
	    
	    @Autowired
		private O2CBatchWithdrawServiceI o2cBatchWithdrawServiceI;

	    @PostMapping(value = "/o2CBatchWithdraw", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
		@ResponseBody
	    /*@ApiOperation( value = "O2CBatchWithdraw",response = O2CBatchWithdrawFileResponse.class,
				authorizations = {
	    	            @Authorization(value = "Authorization")})
		@ApiResponses(value = { 
				@ApiResponse(code = 200, message = "OK", response = O2CBatchTransferResponse.class),
				@ApiResponse(code = 400, message = "Bad Request"),
				@ApiResponse(code = 401, message = "Unauthorized"),
				@ApiResponse(code = 404, message = "Not Found") })
*/

		@io.swagger.v3.oas.annotations.Operation(summary = "${o2CBatchWithdraw.summary}", description="${o2CBatchWithdraw.description}",

				responses = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = O2CBatchWithdrawFileResponse.class))
								)
						}

						),


						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_CODE, description = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_UNAUTH_RESPONSE_CODE, description = com.btsl.util.Constants.API_UNAUTH_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_CODE, description = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						})
				}
		)


	    public O2CBatchWithdrawFileResponse processBatchO2CWidRequest( 
				@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
				HttpServletResponse response1,HttpServletRequest httprequest,

				@Parameter(description = "Geographical Domain", required = true)
		        @RequestParam("geoDomain") String geoDomain,
		        @Parameter(description = "Channel Domain", required = true)
		        @RequestParam("channelDomain") String channelDomain,
				@Parameter(description = "Category", required = true)
		        @RequestParam("category") String userCategoryName,
		        @Parameter(description = "Product", required = true)
		        @RequestParam("product") String product,
		        @Parameter(description = "Wallet Type", required = true)
		        @RequestParam("walletType") String walletType,
				@RequestBody O2CBatchWithdrawFileRequest o2CFileUploadApiRequest ) throws Exception {
		    final String methodName = "processBatchO2CWidRequest";
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Entered ");
			}

			p_requestVO = new RequestVO();
		    ++requestIdChannel;
		    O2CBatchWithdrawFileResponse response = new O2CBatchWithdrawFileResponse();
	        final String requestIDStr = String.valueOf(requestIdChannel);
	        c2cStockTransferMultRequestVOs = new C2CStockTransferMultRequestVO();
			c2cStockTransferMultRequestVOs.setData(new OAuthUserData());
			Locale locale =null;
			Connection con = null;
			MComConnectionI mcomCon = null;
			try 
			{
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			final String taxClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
	        try {
	            calculatorI = (OperatorUtilI) Class.forName(taxClass).newInstance();
	        } catch (Exception e) {
	            log.errorTrace("static block", e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchWithdrawAction[initialize]", "", "", "",
	                "Exception while loading the class at the call:" + e.getMessage());
	        }
			locale= new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
			OAuthenticationUtil.validateTokenApi(c2cStockTransferMultRequestVOs, headers,new BaseResponseMultiple<>());
			String msisdn=c2cStockTransferMultRequestVOs.getData().getMsisdn();
	        response = o2cBatchWithdrawServiceI.processRequest(o2CFileUploadApiRequest,msisdn,calculatorI,locale,con,geoDomain,channelDomain,userCategoryName,product,walletType,"o2cBatchWithdraw", requestIDStr, httprequest, headers, response1);
			}
			catch(BTSLBaseException be){
		        log.error("processRequest", "Exceptin:e=" + be);
		        log.errorTrace(methodName, be);
	       	    String msg=RestAPIStringParser.getMessage(locale, be.getMessageKey(),null);
		        response.setMessageCode(be.getMessageKey());
		        response.setMessage(msg);
				response.setService("o2cBatchWithdrawResp");

	        	if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
	        		response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
	    	         response.setStatus("401");
	            }
	           else{
	        	   response1.setStatus(HttpStatus.SC_BAD_REQUEST);
	           		response.setStatus("400");
	           }
	        }finally {

	            // as to make the status of the batch o2c process as complete into
	            // the table so that only
	            // one instance should be executed for batch o2c

	            if (mcomCon != null) {
					mcomCon.close("O2CBatchController#processBatchO2CWidRequest");
					mcomCon = null;
				}
	            if (log.isDebugEnabled()) {
	                log.debug(methodName, "Exiting:=" + methodName);
	            }
	        
	        }
	        return response;
	    }
	    
	    
	    @Autowired
		private FocBatchInitiateServiceI focBatchInitiateServiceI;
	    @PostMapping(value = "/focBatchStockTrf", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
		@ResponseBody
	    /*@ApiOperation(value = "Foc Batch Stock Transfer", response = FOCBatchTransferResponse.class ,
	    		authorizations = {
	    	            @Authorization(value = "Authorization")
	    })
		@ApiResponses(value = { 
				@ApiResponse(code = 200, message = "OK", response = FOCBatchTransferResponse.class),
				@ApiResponse(code = 400, message = "Bad Request"),
				@ApiResponse(code = 401, message = "Unauthorized"),
				@ApiResponse(code = 404, message = "Not Found") })
*/

		@io.swagger.v3.oas.annotations.Operation(summary = "${focBatchStockTrf.summary}", description="${focBatchStockTrf.description}",

				responses = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = FOCBatchTransferResponse.class))
								)
						}

						),


						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_CODE, description = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_UNAUTH_RESPONSE_CODE, description = com.btsl.util.Constants.API_UNAUTH_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_CODE, description = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						})
				}
		)


	    public FOCBatchTransferResponse processBatchFOCTrfRequest(
	    		@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
	            @Parameter(description = SwaggerAPIDescriptionI.FOC_TRANSFER_BATCH, required = true)
	            @RequestBody FOCBatchTransferRequestVO requestVO,HttpServletResponse response1,HttpServletRequest httprequest) throws Exception {
		    final String methodName = "processBatchFOCTrfRequest";
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Entered ");
			}

			Locale locale =null;
			Connection con = null;
			MComConnectionI mcomCon = null;
			FOCBatchTransferResponse response = new FOCBatchTransferResponse();
			try 
				{
				mcomCon = new MComConnection();
				con = mcomCon.getConnection();
				/*
				 * Authentication
				 * @throws BTSLBaseException
				 */
	        	final String taxClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
		        try {
		            calculatorI = (OperatorUtilI) Class.forName(taxClass).newInstance();
		        } catch (Exception e) {
		            log.errorTrace("static block", e);
		            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchWithdrawAction[initialize]", "", "", "",
		                "Exception while loading the class at the call:" + e.getMessage());
		        }
				locale= new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
				requestVO.setData(new OAuthUserData());
	        	OAuthenticationUtil.validateTokenApi(requestVO, headers,response1);
			    ++requestIdChannel;
		        final String requestIDStr = String.valueOf(requestIdChannel);
		        final String operatorSelectOption =requestVO.getFOCBatchTransferDetails().getOperatorWalletOption();
		        String processWallet = null;
		        
		        	  if(PretupsI.COMM_PAYOUT.equalsIgnoreCase(operatorSelectOption)){
		        		  	response = focBatchInitiateServiceI.processRequest(requestVO, "DPBATCHTRF",calculatorI,requestIDStr, con, locale,httprequest, headers, response1);	  
		        	  }else {
		        		  response = focBatchInitiateServiceI.processRequest(requestVO, "FOCBATCHTRF",calculatorI,requestIDStr, con, locale,httprequest, headers, response1);
		        	  }

			}
			catch(BTSLBaseException be){
		        log.error("processRequest", "Exceptin:e=" + be);
		        log.errorTrace(methodName, be);
	       	    String msg=RestAPIStringParser.getMessage(locale, be.getMessageKey(),null);
		        response.setMessageCode(be.getMessageKey());
		        response.setMessage(msg);
				response.setService("O2CBATCHTRFRESP");

	        	if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
	        		response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
	    	         response.setStatus("401");
	            }
	           else{
	        	   response1.setStatus(HttpStatus.SC_BAD_REQUEST);
	           		response.setStatus("400");
	           }
	        }

	        return response;
	    }
	    
	    @PostMapping(value = "/batchCommissionUserListDownload", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	  		@ResponseBody
	  	    /*@ApiOperation( value = "O2C Batch Commission user List Download",response = O2CBatchWithdrawFileResponse.class,
	  				authorizations = {
	  	    	            @Authorization(value = "Authorization")})
	  		@ApiResponses(value = { 
	  				@ApiResponse(code = 200, message = "OK", response = O2CBatchTransferResponse.class),
	  				@ApiResponse(code = 400, message = "Bad Request"),
	  				@ApiResponse(code = 401, message = "Unauthorized"),
	  				@ApiResponse(code = 404, message = "Not Found") })
*/

		@io.swagger.v3.oas.annotations.Operation(summary = "${batchCommissionUserListDownload.summary}", description="${batchCommissionUserListDownload.description}",

				responses = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = FileDownloadResponse.class))
								)
						}

						),


						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_CODE, description = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_UNAUTH_RESPONSE_CODE, description = com.btsl.util.Constants.API_UNAUTH_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_CODE, description = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						})
				}
		)


	  	    public FileDownloadResponse userListDownloadBatchFOC( 
	  				@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
	  				@RequestBody BatchFOCFileDownloadRequestVO batchFOCFileDownloadRequestVO,
	  				HttpServletResponse response1,HttpServletRequest httprequest) throws Exception {
	  		    final String methodName = "userListDownloadBatchFOC";
	  			if (log.isDebugEnabled()) {
	  				log.debug(methodName, "Entered ");
	  			}


	  		    FileDownloadResponse response = new FileDownloadResponse();
	  	       
	  			Locale locale =null;
	  			Connection con = null;
	  			MComConnectionI mcomCon = null;
	  			UserDAO channelUserDAO = new UserDAO();
	  			try 
		  		{
					OAuthUser oAuthUserData = new OAuthUser();
					oAuthUserData.setData(new OAuthUserData());
		
					OAuthenticationUtil.validateTokenApi(oAuthUserData, headers, response1);
					mcomCon = new MComConnection();
					con = mcomCon.getConnection();
					
					UserVO userVO = channelUserDAO.loadAllUserDetailsByLoginID(con, oAuthUserData.getData().getLoginid());
		
					locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
					
					if(BTSLUtil.isEmpty(batchFOCFileDownloadRequestVO.getFileType()))
					{
						throw new BTSLBaseException(PretupsErrorCodesI.INVALID_FILE_TYPE);
					}
					else
					{
						if(!("userList".equals(batchFOCFileDownloadRequestVO.getFileType()) || 
								"template".equals(batchFOCFileDownloadRequestVO.getFileType())))
						{
							throw new BTSLBaseException(PretupsErrorCodesI.INVALID_FILE_TYPE);						
						}
					}
					if("userList".equals(batchFOCFileDownloadRequestVO.getFileType()))
					{
						if(BTSLUtil.isEmpty(batchFOCFileDownloadRequestVO.getCategory()))
						{
							throw new BTSLBaseException(PretupsErrorCodesI.BLANK_APLHA_CAT);
						}
						if(BTSLUtil.isEmpty(batchFOCFileDownloadRequestVO.getDomain()))
						{
							throw new BTSLBaseException(PretupsErrorCodesI.BLANK_DOMAIN);
						}
						if(BTSLUtil.isEmpty(batchFOCFileDownloadRequestVO.getProduct()))
						{
							throw new BTSLBaseException(PretupsErrorCodesI.BLANK_PRODUCTCODE);
						}
						
						if(BTSLUtil.isEmpty(batchFOCFileDownloadRequestVO.getGeography()))
						{
							throw new BTSLBaseException(PretupsErrorCodesI.EXT_USRADD_INVALID_GEOGRAPHY);
						}
					}
					
					
					response = focBatchInitiateServiceI.userListDownload(con, batchFOCFileDownloadRequestVO, userVO);
		
				}
	  			catch(BTSLBaseException be){
	  		        log.error("processRequest", "Exceptin:e=" + be);
	  		        log.errorTrace(methodName, be);
	  	       	    String msg=RestAPIStringParser.getMessage(locale, be.getMessageKey(),null);
	  		        response.setMessageCode(be.getMessageKey());
	  		        response.setMessage(msg);

	  	        	if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
	  	        		response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
	  	        		response.setStatus(HttpStatus.SC_UNAUTHORIZED);
	  	            }
	  	           else{
	  	        	   response1.setStatus(HttpStatus.SC_BAD_REQUEST);
	  	        	   response.setStatus(HttpStatus.SC_BAD_REQUEST);
	  	           }
	  	        }
	  			catch(Exception e)
	  			{
	  				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
	  	        	response.setStatus(HttpStatus.SC_BAD_REQUEST);
	  	        	response.setMessage(e.getMessage());
	  	        	response.setMessageCode(String.valueOf(HttpStatus.SC_BAD_REQUEST));
	  			}
	  			finally {
	  	            // as to make the status of the batch o2c process as complete into
	  	            // the table so that only
	  	            // one instance should be executed for batch o2c

	  	            if (mcomCon != null) {
	  					mcomCon.close("O2CBatchController#"+methodName);
	  					mcomCon = null;
	  				}
	  	            if (log.isDebugEnabled()) {
	  	                log.debug(methodName, "Exiting:=" + methodName);
	  	            }
	  	        
	  	        }
	  	        return response;
	  	    }
}