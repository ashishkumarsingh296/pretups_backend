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
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.restapi.o2c.service.bulko2capprovalrequestvo.BulkO2CApprovalRequestVO;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${O2CBatchApprovalController.name}", description = "${O2CBatchApprovalController.desc}")//@Api(tags= "O2C Batch Services",value="O2C Batch Services")
@RestController
@RequestMapping(value = "/v1/o2c")
public class O2CBatchApprovalController {
	public static final Log log = LogFactory.getLog(O2CBatchApprovalController.class.getName());
	public static OperatorUtilI _operatorUtil = null;
	private C2CStockTransferMultRequestVO c2cStockTransferMultRequestVOs;
	private static OperatorUtilI calculatorI = null;
	private static long requestIdChannel = 0;
	    
	    @Autowired
		private O2CBatchApprovalServiceI o2CBatchApprovalServiceI;
	    
	    @Autowired
	    private O2CBatchProcessServiceI o2CBatchProcessServiceI;

	    @PostMapping(value = "/o2CBatchProcess", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
		@ResponseBody
	    /*@ApiOperation( value = "O2CBatchProcess",response = O2CBatchWithdrawFileResponse.class,
				authorizations = {
	    	            @Authorization(value = "Authorization")})
		@ApiResponses(value = { 
				@ApiResponse(code = 200, message = "OK", response = O2CBatchWithdrawFileResponse.class),
				@ApiResponse(code = 400, message = "Bad Request"),
				@ApiResponse(code = 401, message = "Unauthorized"), 
				@ApiResponse(code = 404, message = "Not Found") })
*/

		@io.swagger.v3.oas.annotations.Operation(summary = "${o2CBatchProcess.summary}", description="${o2CBatchProcess.description}",

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


	    public O2CBatchWithdrawFileResponse processBatchO2CBatchProcess( 
				@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
				HttpServletResponse response1,HttpServletRequest httprequest,
				@Parameter(description = "Request Type", required = true) 
		        @RequestParam("requestType") String requestType,
		        @Parameter(description = "Batch ID", required = true) 
		        @RequestParam("batchID") String batchID,
		        @Parameter(description = "Service Type", required = true) 
		        @RequestParam("serviceType") String serviceType,
				@RequestBody O2CBatchWithdrawFileRequest o2CFileUploadApiRequest ) throws Exception {
		    final String methodName = "processBatchO2CBatchProcess";
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Entered ");
			}

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
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchProcessController[initialize]", "", "", "",
	                "Exception while loading the class at the call:" + e.getMessage());
	        }
			locale= new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
			OAuthenticationUtil.validateTokenApi(c2cStockTransferMultRequestVOs, headers,new BaseResponseMultiple<>());
			String msisdn=c2cStockTransferMultRequestVOs.getData().getMsisdn();
	        response = o2CBatchProcessServiceI.processRequest(serviceType,o2CFileUploadApiRequest,msisdn,calculatorI,locale,con,requestType,batchID,"o2CBatchProcess", requestIDStr, httprequest, headers, response1);
			}
			catch(BTSLBaseException be){
		        log.error("processBatchO2CBatchProcess", "Exceptin:e=" + be);
		        log.errorTrace(methodName, be);
	       	    String msg=RestAPIStringParser.getMessage(locale, be.getMessageKey(),null);
		        response.setMessageCode(be.getMessageKey());
		        response.setMessage(msg);
				response.setService("o2cBatchProcessResp");

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
					mcomCon.close("O2CBatchProcessController#processBatchO2CBatchProcess");
					mcomCon = null;
				}
	            if (log.isDebugEnabled()) {
	                log.debug(methodName, "Exiting:=" + methodName);
	            }
	        
	        }
	        return response;
	    }
	    
	    
	    
	    
	    @PostMapping(value = "/getBulkApprvList", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
		@ResponseBody
	    /*@ApiOperation( value = "Bulk O2C Approval List",response = O2CApprovalListVO.class,
				authorizations = {
	    	            @Authorization(value = "Authorization")})
		@ApiResponses(value = { 
				@ApiResponse(code = 200, message = "OK", response = O2CApprovalListVO.class),
				@ApiResponse(code = 400, message = "Bad Request"),
				@ApiResponse(code = 401, message = "Unauthorized"), 
				@ApiResponse(code = 404, message = "Not Found") })
*/

		@io.swagger.v3.oas.annotations.Operation(summary = "${getBulkApprvList.summary}", description="${getBulkApprvList.description}",

				responses = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = O2CApprovalListVO.class))
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


	    public O2CApprovalListVO bulkO2CApprovalList( 
	    		@Parameter(description = SwaggerAPIDescriptionI.BATCH_APPROVAL_LIST_DETAILS)
	    		@RequestBody BulkO2CApprovalRequestVO bulkO2CApprovalRequestVO,
				@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
				HttpServletResponse responseSwag
				 ) throws Exception {
		    final String methodName = "bulkO2CApprovalList";
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Entered ");
			}

			O2CApprovalListVO response = new O2CApprovalListVO();
			Locale locale =null;
			Connection con = null;
			MComConnectionI mcomCon = null;
			String msisdn=null;
			try 
			{
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
			OAuthUser oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());
			
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			
			msisdn= oAuthUser.getData().getMsisdn();
			locale= new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
			
			o2CBatchApprovalServiceI.getBulkO2CApprovalList(bulkO2CApprovalRequestVO, msisdn, locale, response, responseSwag, con);
			}
			catch(BTSLBaseException be){
		        log.error(methodName, "Exceptin:e=" + be);
		        log.errorTrace(methodName, be);
	       	    String msg=RestAPIStringParser.getMessage(locale,be.getMessageKey(),be.getArgs());
		        response.setMessageCode(be.getMessageKey());
		        response.setMessage(msg);

	        	if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
	        		responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
	    	         response.setStatus(HttpStatus.SC_UNAUTHORIZED);
	            }
	           else{
	        	   responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
	           	   response.setStatus(HttpStatus.SC_BAD_REQUEST);
	           }
	        }finally {
	            if (mcomCon != null) {
					mcomCon.close("O2CBatchApprovalController#"+methodName);
					mcomCon = null;
				}
	            if (log.isDebugEnabled()) {
	                log.debug(methodName, "Exiting:=" + methodName);
	            }
	        
	        }
	        return response;
	    }
	    
	    @Autowired
	    private O2CBatchApproveRejectServiceI o2CBatchApproveRejectServiceI;
	    
	    @PostMapping(value = "/o2CBatchAppRej", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
		@ResponseBody
	    /*@ApiOperation( value = "O2CBatchApprovalReject",response = O2CBulkApprovalOrRejectRequestVO.class,
				authorizations = {
	    	            @Authorization(value = "Authorization")})
		@ApiResponses(value = { 
				@ApiResponse(code = 200, message = "OK", response = O2CBatchWithdrawFileResponse.class),
				@ApiResponse(code = 400, message = "Bad Request"),
				@ApiResponse(code = 401, message = "Unauthorized"), 
				@ApiResponse(code = 404, message = "Not Found") })
*/

		@io.swagger.v3.oas.annotations.Operation(summary = "${o2CBatchAppRej.summary}", description="${o2CBatchAppRej.description}",

				responses = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = O2CBatchApRejTransferResponse.class))
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


	    public O2CBatchApRejTransferResponse approveOrRejectBatchO2CBatch( 
				@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
				HttpServletResponse response1,HttpServletRequest httprequest,
				@RequestBody O2CBulkApprovalOrRejectRequestVO O2CBulkApprovalOrRejectRequestVO) throws Exception {
		    final String methodName = "approveOrRejectBatchO2CBatch";
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Entered ");
			}

		    ++requestIdChannel;
		    O2CBatchApRejTransferResponse response = new O2CBatchApRejTransferResponse();
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
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchProcessController[initialize]", "", "", "",
	                "Exception while loading the class at the call:" + e.getMessage());
	        }
			locale= new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
			OAuthenticationUtil.validateTokenApi(c2cStockTransferMultRequestVOs, headers,new BaseResponseMultiple<>());
			String msisdn=c2cStockTransferMultRequestVOs.getData().getMsisdn();
			
	        response = o2CBatchApproveRejectServiceI.processO2CApproveOrReject(O2CBulkApprovalOrRejectRequestVO,msisdn,calculatorI,locale,con,"o2CBatchApp", requestIDStr, httprequest, headers, response1);
			}
			catch(BTSLBaseException be){
		        log.error(methodName, "Exceptin:e=" + be);
		        log.errorTrace(methodName, be);
	       	    String msg=RestAPIStringParser.getMessage(locale, be.getMessageKey(),null);
		        response.setMessageCode(be.getMessageKey());
		        response.setMessage(msg);
				response.setService("o2cBatchAppRej");

	        	if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
	        		response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
	    	         response.setStatus("401");
	            }
	           else{
	        	   response1.setStatus(HttpStatus.SC_BAD_REQUEST);
	           		response.setStatus("400");
	           }
	        }finally {
	            if (mcomCon != null) {
					mcomCon.close("O2CBatchProcessController#"+methodName);
					mcomCon = null;
				}
	            if (log.isDebugEnabled()) {
	                log.debug(methodName, "Exiting:=" + methodName);
	            }
	        
	        }
	        return response;
	    }
	    
	    @PostMapping(value = "/getBulkApprDetail", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	  		@ResponseBody
	  	    /*@ApiOperation( value = "Bulk O2C Approval Deails",response = O2CBatchApprovalDetailsResponse.class,
	  				authorizations = {
	  	    	            @Authorization(value = "Authorization")})
	  		@ApiResponses(value = { 
	  				@ApiResponse(code = 200, message = "OK", response = O2CBatchApprovalDetailsResponse.class),
	  				@ApiResponse(code = 400, message = "Bad Request"),
	  				@ApiResponse(code = 401, message = "Unauthorized"), 
	  				@ApiResponse(code = 404, message = "Not Found") })
*/

		@io.swagger.v3.oas.annotations.Operation(summary = "${getBulkApprDetail.summary}", description="${getBulkApprDetail.description}",

				responses = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = O2CBatchApprovalDetailsResponse.class))
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


	  	    public O2CBatchApprovalDetailsResponse o2CBatchApprovalDetails( 
	  	    		@Parameter(description = SwaggerAPIDescriptionI.BATCH_APPROVAL_DETAILS)
	  	    		@RequestBody O2CBatchApprovalDetailsRequestVO batchApprovalDetailsRequestVO,
	  				@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
	  				HttpServletResponse responseSwag,HttpServletRequest httprequest
	  				 ) throws Exception {
	  		    final String methodName = "o2CBatchApprovalDetails";
	  			if (log.isDebugEnabled()) {
	  				log.debug(methodName, "Entered ");
	  			}

	  			O2CBatchApprovalDetailsResponse response = new O2CBatchApprovalDetailsResponse();
	  			Locale locale =null;
	  			Connection con = null;
	  			MComConnectionI mcomCon = null;
	  			String msisdn=null;
	  			try 
	  			{
	  			mcomCon = new MComConnection();
	  			con = mcomCon.getConnection();
	  			
	  			OAuthUser oAuthUser = new OAuthUser();
	  			oAuthUser.setData(new OAuthUserData());
	  			
	  			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
	  			
	  			msisdn= oAuthUser.getData().getMsisdn();
	  			locale= new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
	  			
	  			response=o2CBatchApprovalServiceI.processO2CBatchApprovalDetails(con, batchApprovalDetailsRequestVO, msisdn, locale, response, responseSwag, httprequest);
	  			}
	  			catch(BTSLBaseException be){
	  		        log.error(methodName, "Exceptin:e=" + be);
	  		        log.errorTrace(methodName, be);
	  	       	    String msg=RestAPIStringParser.getMessage(locale,be.getMessageKey(),be.getArgs());
	  		        response.setMessageCode(be.getMessageKey());
	  		        response.setMessage(msg);

	  	        	if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
	  	        		responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
	  	    	         response.setStatus(HttpStatus.SC_UNAUTHORIZED);
	  	            }
	  	           else{
	  	        	   responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
	  	           	   response.setStatus(HttpStatus.SC_BAD_REQUEST);
	  	           }
	  	        }finally {
	  	            if (mcomCon != null) {
	  					mcomCon.close("O2CBatchApprovalController#"+methodName);
	  					mcomCon = null;
	  				}
	  	            if (log.isDebugEnabled()) {
	  	                log.debug(methodName, "Exiting:=" + methodName);
	  	            }
	  	        
	  	        }
	  	        return response;
	  	    }
	  	    
	    

		   @PostMapping(value = "/commissionBatchAppRej", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
				@ResponseBody
			    /*@ApiOperation( value = "Bulk Commission Batch Approval Reject",response = O2CBatchApRejTransferResponse.class,
						authorizations = {
			    	            @Authorization(value = "Authorization")})
				@ApiResponses(value = { 
						@ApiResponse(code = 200, message = "OK", response = O2CBatchApRejTransferResponse.class),
						@ApiResponse(code = 400, message = "Bad Request"),
						@ApiResponse(code = 401, message = "Unauthorized"), 
						@ApiResponse(code = 404, message = "Not Found") })
*/

		   @io.swagger.v3.oas.annotations.Operation(summary = "${commissionBatchAppRej.summary}", description="${commissionBatchAppRej.description}",

				   responses = {
						   @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
								   @io.swagger.v3.oas.annotations.media.Content(
										   mediaType = "application/json",
										   array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = O2CBatchApRejTransferResponse.class))
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


			    public O2CBatchApRejTransferResponse approveOrRejectCommsionBatch( 
						@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
						HttpServletResponse response1,HttpServletRequest httprequest,
						@RequestBody CommisionBulkApprovalOrRejectRequestVO commisionBulkApprovalOrRejectRequestVO) throws Exception {
				    final String methodName = "approveOrRejectCommsionBatch";
					if (log.isDebugEnabled()) {
						log.debug(methodName, "Entered ");
					}

				    //++requestIdChannel;
				    O2CBatchApRejTransferResponse response = new O2CBatchApRejTransferResponse();
			      //  final String requestIDStr = String.valueOf(requestIdChannel);
			       // c2cStockTransferMultRequestVOs = new C2CStockTransferMultRequestVO();
					//c2cStockTransferMultRequestVOs.setData(new OAuthUserData());
					Locale locale =null;
					Connection con = null;
					MComConnectionI mcomCon = null;
					try 
					{
					mcomCon = new MComConnection();
					con = mcomCon.getConnection();
					final String taxClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
//			        try {
//			            calculatorI = (OperatorUtilI) Class.forName(taxClass).newInstance();
//			        } catch (Exception e) {
//			            log.errorTrace("static block", e);
//			            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchProcessController[initialize]", "", "", "",
//			                "Exception while loading the class at the call:" + e.getMessage());
//			        }
					locale= new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
					
					OAuthUser oAuthUserData=new OAuthUser();
					UserDAO userDao = new UserDAO();
					
					oAuthUserData.setData(new OAuthUserData());
					
					OAuthenticationUtil.validateTokenApi(oAuthUserData,headers,response1);
					//response.setService("");
					
					String loginId =  oAuthUserData.getData().getLoginid();
					String msisdn =  oAuthUserData.getData().getMsisdn();
					
					
			        response = o2CBatchApproveRejectServiceI.processBulkCommApproveOrReject(commisionBulkApprovalOrRejectRequestVO,msisdn,locale,con, httprequest, headers, response1);
					}
					catch(BTSLBaseException be){
				        log.error(methodName, "Exceptin:e=" + be);
				        log.errorTrace(methodName, be);
			       	    String msg=RestAPIStringParser.getMessage(locale, be.getMessageKey(),null);
				        response.setMessageCode(be.getMessageKey());
				        response.setMessage(msg);
						response.setService("COMMBATCHAPPROVALRESP");

			        	if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
			        		response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
			    	         response.setStatus("401");
			            }
			           else{
			        	   response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			           		response.setStatus("400");
			           }
			        }finally {
			            if (mcomCon != null) {
							mcomCon.close("O2CBatchProcessController#"+methodName);
							mcomCon = null;
						}
			            if (log.isDebugEnabled()) {
			                log.debug(methodName, "Exiting:=" + methodName);
			            }
			        
			        }
			        return response;
			    }

		   @PostMapping(value = "/BulkComBatchProcess", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
			@ResponseBody
		    /*@ApiOperation( value = "Bulk Commission Batch Process",response = O2CBatchWithdrawFileResponse.class,
					authorizations = {
		    	            @Authorization(value = "Authorization")})
			@ApiResponses(value = { 
					@ApiResponse(code = 200, message = "OK", response = O2CBatchWithdrawFileResponse.class),
					@ApiResponse(code = 400, message = "Bad Request"),
					@ApiResponse(code = 401, message = "Unauthorized"), 
					@ApiResponse(code = 404, message = "Not Found") })
*/

		   @io.swagger.v3.oas.annotations.Operation(summary = "${BulkComBatchProcess.summary}", description="${BulkComBatchProcess.description}",

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


		    public O2CBatchWithdrawFileResponse processBatchO2CBatchProcess( 
					@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
					HttpServletResponse response1,HttpServletRequest httprequest,
					@Parameter(description = "Request Type", required = true) 
			        @RequestParam("requestType") String requestType,
			        @Parameter(description = "Batch ID", required = true) 
			        @RequestParam("batchID") String batchID,
					@RequestBody O2CBatchWithdrawFileRequest bulkComProcessApiRequest ) throws Exception {
			    final String methodName = "processBatchO2CBatchProcess";
				if (log.isDebugEnabled()) {
					log.debug(methodName, "Entered ");
				}

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
		            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchProcessController[initialize]", "", "", "",
		                "Exception while loading the class at the call:" + e.getMessage());
		        }
				locale= new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
				OAuthenticationUtil.validateTokenApi(c2cStockTransferMultRequestVOs, headers,new BaseResponseMultiple<>());
				String msisdn=c2cStockTransferMultRequestVOs.getData().getMsisdn();
		        response = o2CBatchProcessServiceI.processBulkComBatchProcessRequest(bulkComProcessApiRequest,msisdn,calculatorI,locale,con,requestType,batchID,"o2CBatchProcess", httprequest, headers, response1);
				}
				catch(BTSLBaseException be){
			        log.error("processBatchO2CBatchProcess", "Exceptin:e=" + be);
			        log.errorTrace(methodName, be);
		       	    String msg=RestAPIStringParser.getMessage(locale, be.getMessageKey(),null);
			        response.setMessageCode(be.getMessageKey());
			        response.setMessage(msg);
					response.setService("o2cBatchProcessResp");

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
						mcomCon.close("O2CBatchProcessController#"+methodName);
						mcomCon = null;
					}
		            if (log.isDebugEnabled()) {
		                log.debug(methodName, "Exiting:=" + methodName);
		            }
		        
		        }
		        return response;
		    }


	    
	    
	    
	    
}
