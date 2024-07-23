package com.restapi.c2sservices.controller;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Locale;


import com.btsl.common.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDomainCodeVO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.restapi.c2s.services.C2SBulkEvdRechargeRequestVO;
import com.restapi.c2s.services.C2SBulkEvdRechargeResponseVO;
import com.restapi.c2s.services.C2SBulkRcServiceI;
import com.restapi.c2s.services.C2SBulkRechargeRequestVO;
import com.restapi.c2s.services.C2SBulkRechargeResponseVO;
import com.restapi.c2s.services.CancelBatchC2SRequestVO;
import com.restapi.c2s.services.CancelSingleMsisdnBatchC2SRequestVO;
import com.restapi.c2s.services.CancelSingleMsisdnBatchResponseVO;
import com.restapi.c2s.services.DvdBulkResponse;
import com.restapi.c2s.services.ReconcileServiceListResponseVO;
import com.restapi.c2s.services.ServiceListResponseVO;
import com.restapi.c2s.services.ViewC2SBulkRechargeDetailsResponseVO;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;


@io.swagger.v3.oas.annotations.tags.Tag(name = "${C2SBulkRechargeController.name}", description = "${C2SBulkRechargeController.desc}")//@Api(tags ="C2S Services", value="C2S Services")
@RestController
@RequestMapping(value = "/v1/c2sServices")
public class C2SBulkRechargeController {
	public static final Log log = LogFactory.getLog(C2SBulkRechargeController.class.getName());
	public static OperatorUtilI _operatorUtil = null;
	private RequestVO p_requestVO;
	
	    private C2SBulkRechargeRequestVO request;
	    @Autowired
		private C2SBulkRcServiceI c2SBulkRcServiceI;
	    private static long requestIdChannel = 0;

	    @PostMapping(value = "/c2sbulkintrrc", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
		@ResponseBody
	    /*@ApiOperation(value = "Bulk Internet Recharge", response = C2SBulkRechargeResponseVO.class ,
	    		authorizations = {
	    	            @Authorization(value = "Authorization")
	    })
	    
		@ApiResponses(value = { 
				@ApiResponse(code = 200, message = "OK", response = C2SBulkRechargeResponseVO.class),
				@ApiResponse(code = 400, message = "Bad Request"),
				@ApiResponse(code = 401, message = "Unauthorized"),
				@ApiResponse(code = 404, message = "Not Found") })
*/

		@io.swagger.v3.oas.annotations.Operation(summary = "${c2sbulkintrrc.summary}", description="${c2sbulkintrrc.description}",

				responses = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = C2SBulkRechargeResponseVO.class))
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



		public C2SBulkRechargeResponseVO processBulkInternetRechargeRequest(
	    		@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
	            @Parameter(description = SwaggerAPIDescriptionI.BULK_INTERNET_RECHARGE, required = true)
	            @RequestBody C2SBulkRechargeRequestVO requestVO,HttpServletResponse response1) throws Exception {
		    final String methodName = "processBulkInternetRechargeRequest";
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Entered ");
			}

			p_requestVO = new RequestVO();
			this.request = requestVO;
		    ++requestIdChannel;
		    C2SBulkRechargeResponseVO response = new C2SBulkRechargeResponseVO();
	        final String requestIDStr = String.valueOf(requestIdChannel);
	        response = c2SBulkRcServiceI.processRequest(requestVO,"intrrc".toUpperCase(), requestIDStr,"schedule", headers, response1);
	        
	        return response;
	    }

	    
	    
	    @PostMapping(value = "/c2sbulkgrc", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
		@ResponseBody
	    /*@ApiOperation(value = "Bulk Gift Recharge", response = C2SBulkRechargeResponseVO.class ,
	    		authorizations = {
	    	            @Authorization(value = "Authorization")
	    })
	    
		@ApiResponses(value = { 
				@ApiResponse(code = 200, message = "OK", response = C2SBulkRechargeResponseVO.class),
				@ApiResponse(code = 400, message = "Bad Request"),
				@ApiResponse(code = 401, message = "Unauthorized"),
				@ApiResponse(code = 404, message = "Not Found")
				})
*/

		@io.swagger.v3.oas.annotations.Operation(summary = "${c2sbulkgrc.summary}", description="${c2sbulkgrc.description}",

				responses = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = C2SBulkRechargeResponseVO.class))
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



		public C2SBulkRechargeResponseVO processBulkGiftRechargeRequest(
	    		@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
	            @Parameter(description = SwaggerAPIDescriptionI.BULK_GIFT_RECHARGE, required = true)
	            @RequestBody C2SBulkRechargeRequestVO requestVO,HttpServletResponse response1) throws Exception {
		    final String methodName = "processBulkGiftRechargeRequest";
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Entered ");
			}

			p_requestVO = new RequestVO();
			this.request = requestVO;
		    ++requestIdChannel;
		    C2SBulkRechargeResponseVO response = new C2SBulkRechargeResponseVO();
	        final String requestIDStr = String.valueOf(requestIdChannel);
	        response = c2SBulkRcServiceI.processRequest(requestVO,"grc".toUpperCase(), requestIDStr,"Schedule", headers, response1);
	        
	        return response;
	    }

	    /*
	     * This method is for bulk prepaid recharge
	     */
	    @PostMapping(value = "/c2sbulkprc", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
		@ResponseBody
	    /*@ApiOperation(value = "Bulk Prepaid Recharge", response = C2SBulkRechargeResponseVO.class ,
	    		authorizations = {
	    	            @Authorization(value = "Authorization")
	    })
	    
		@ApiResponses(value = { 
				@ApiResponse(code = 200, message = "OK", response = C2SBulkRechargeResponseVO.class),
				@ApiResponse(code = 400, message = "Bad Request"),
				@ApiResponse(code = 401, message = "Unauthorized"),
				@ApiResponse(code = 404, message = "Not Found") })
*/

		@io.swagger.v3.oas.annotations.Operation(summary = "${c2sbulkprc.summary}", description="${c2sbulkprc.description}",

				responses = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = C2SBulkRechargeResponseVO.class))
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



		public C2SBulkRechargeResponseVO processBulkPrepaidRechargeRequest(
	    		@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
	            @Parameter(description = SwaggerAPIDescriptionI.BULK_C2S_RECHARGE,  required = true)
	            @RequestBody C2SBulkRechargeRequestVO requestVO,HttpServletResponse response1) throws Exception {
		    final String methodName = "processBulkPrepaidRechargeRequest";
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Entered ");
			}

			p_requestVO = new RequestVO();
			this.request = requestVO;
		    ++requestIdChannel;
		    C2SBulkRechargeResponseVO response = new C2SBulkRechargeResponseVO();
	        final String requestIDStr = String.valueOf(requestIdChannel);
	        response = c2SBulkRcServiceI.processRequest(requestVO, "rc".toUpperCase(), requestIDStr, "schedule", headers, response1);
	        
	        return response;
	    }
	    
	    
	    /*
	     * This method is for View Bluk details 
	     */
	    @RequestMapping(value = "/viewc2sbulk/batchId/msisdn", produces = MediaType.APPLICATION_JSON, method = RequestMethod.GET)
	    @ResponseBody
	    /*@ApiOperation(value = "View c2s bulk recharge details", response = ViewC2SBulkRechargeDetailsResponseVO.class ,
	    		authorizations = {
	    	            @Authorization(value = "Authorization")
	    })
		@ApiResponses(value = { 
				@ApiResponse(code = 200, message = "OK", response = ViewC2SBulkRechargeDetailsResponseVO.class),
				@ApiResponse(code = 400, message = "Bad Request"),
				@ApiResponse(code = 401, message = "Unauthorized"),
				@ApiResponse(code = 404, message = "Not Found") })
*/

		@io.swagger.v3.oas.annotations.Operation(summary = "${viewc2sbulk.summary}", description="${viewc2sbulk.description}",

				responses = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ViewC2SBulkRechargeDetailsResponseVO.class))
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



		public ViewC2SBulkRechargeDetailsResponseVO processViewBulkPrepaidRecharge(
	    		 @RequestParam("batchId") String batchId,
	    		 @RequestParam("msisdn") String msisdn,
	    		@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
	        HttpServletResponse response1) throws Exception {
		    final String methodName = "processViewBulkPrepaidRecharge";
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Entered ");
			}
		    ++requestIdChannel;
		    ViewC2SBulkRechargeDetailsResponseVO response = new ViewC2SBulkRechargeDetailsResponseVO();
	        final String requestIDStr = String.valueOf(requestIdChannel);
	        response = c2SBulkRcServiceI.processViewRequest(batchId, headers, response1,msisdn);
	        
	        return response;
	    }
	    
	    /*
	     * Controller for bulk DVD
	     */
	    @PostMapping(value = "/dvdBulk", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
		@ResponseBody
	    /*@ApiOperation(value = "Bulk Digital Voucher Distribution", response = DvdBulkResponse.class ,
	    		authorizations = {
	    	            @Authorization(value = "Authorization")
	    })
	    
	    @ApiResponses(value = {
		        @ApiResponse(code = 400, message = "Bad Request")
		        })
*/

		@io.swagger.v3.oas.annotations.Operation(summary = "${dvdBulk.summary}", description="${dvdBulk.description}",

				responses = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = DvdBulkResponse.class))
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



		public DvdBulkResponse processBulkDvdRequest(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = SwaggerAPIDescriptionI.BULK_DVD,  required = true)
			@RequestBody C2SBulkRechargeRequestVO requestVO,
			HttpServletResponse responseSwag, HttpServletRequest httpServletRequest) throws Exception {
	    	
	    	DvdBulkResponse response = null;
		   final String methodName = "processBulkDvdRequest";
		   if (log.isDebugEnabled()) {
			   log.debug(methodName, "Entered ");
		   }
		++requestIdChannel;
		final String requestIDStr = String.valueOf(requestIdChannel);

		response = c2SBulkRcServiceI.processRequestBulkDVD(requestVO, requestIDStr, headers, responseSwag, httpServletRequest, "dvdBulk".toUpperCase());
		return response;
	}
	    /*
	     * Controller for cancel batch
	     */
	    @PostMapping(value = "/cancelBatch", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
		@ResponseBody
	    /*@ApiOperation(value = "Cancel Batch Recharge", response = DvdBulkResponse.class ,
	    		authorizations = {
	    	            @Authorization(value = "Authorization")
	    })
	    
	    @ApiResponses(value = {
		        @ApiResponse(code = 400, message = "Bad Request")
		        })
*/

		@io.swagger.v3.oas.annotations.Operation(summary = "${cancelBatch.summary}", description="${cancelBatch.description}",

				responses = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = DvdBulkResponse.class))
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



		public DvdBulkResponse processCancelBatchRequest(
				@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
				@Parameter(description = SwaggerAPIDescriptionI.CANCEL_BATCH,  required = true)
				@RequestBody CancelBatchC2SRequestVO requestVO,
				HttpServletResponse responseSwag, HttpServletRequest httpServletRequest) throws Exception {
		    	
		    	DvdBulkResponse response = null;
			   final String methodName = "processCancelBatchRequest";
			   if (log.isDebugEnabled()) {
				   log.debug(methodName, "Entered ");
			   }
			++requestIdChannel;
			final String requestIDStr = String.valueOf(requestIdChannel);

			response = c2SBulkRcServiceI.processCancelBatch(requestVO, requestIDStr, headers, responseSwag, httpServletRequest, "cancelBatch".toUpperCase());
			return response;
		}
	  
	    /*
	     * Controller for cancel msisdn in batch
	     */
	    @PostMapping(value = "/cancelSingleMsisdnBatch", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
		@ResponseBody
	    /*@ApiOperation(value = "Cancel Single Msisdn in Batch Recharge", response = DvdBulkResponse.class ,
	    		authorizations = {
	    	            @Authorization(value = "Authorization")
	    })
	    
	    @ApiResponses(value = {
		        @ApiResponse(code = 400, message = "Bad Request")
		        })
*/

		@io.swagger.v3.oas.annotations.Operation(summary = "${cancelSingleMsisdnBatch.summary}", description="${cancelSingleMsisdnBatch.description}",

				responses = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CancelSingleMsisdnBatchResponseVO.class))
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



		public CancelSingleMsisdnBatchResponseVO processCancelSingleMsisdnBatchRequest(
				@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
				@Parameter(description = SwaggerAPIDescriptionI.CANCEL_SINGLE_MSISDN_BATCH,  required = true)
				@RequestBody CancelSingleMsisdnBatchC2SRequestVO requestVO,
				HttpServletResponse responseSwag, HttpServletRequest httpServletRequest) throws Exception {
		    	
		    	CancelSingleMsisdnBatchResponseVO response = null;
			   final String methodName = "processCancelSingleMsisdnBatchRequest";
			   if (log.isDebugEnabled()) {
				   log.debug(methodName, "Entered ");
			   }
			++requestIdChannel;
			final String requestIDStr = String.valueOf(requestIdChannel);

			response = c2SBulkRcServiceI.processSingleMsisdnCancelBatch(requestVO, requestIDStr, headers, responseSwag, httpServletRequest,"cancelSingleMsisdnBatch".toUpperCase());
			return response;
		}
	    /*
	     * controller for domainCode details
	     */
         
	    /**
	     * 
	     * @param headers
	     * @param response1
	     * @param httprequest
	     * @param domainCode
	     * @return
	     * @throws BTSLBaseException
	     */
		@GetMapping(value = "/domainCode", produces = MediaType.APPLICATION_JSON)
		@ResponseBody
		/*@ApiOperation(value = "Get doamin Code details", response = CategoryDomainCodeVO.class, authorizations = {
				@Authorization(value = "Authorization") })
		@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = CategoryDomainCodeVO.class),
				@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
				@ApiResponse(code = 404, message = "Not Found") })
*/

		@io.swagger.v3.oas.annotations.Operation(summary = "${domainCode.summary}", description="${domainCode.description}",

				responses = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CategoryDomainCodeVO.class))
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



		public CategoryDomainCodeVO getDomainDetials(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
				HttpServletResponse response1, HttpServletRequest httprequest,
				@Parameter(description = "domainCode", required = false) @RequestParam("domainCode") String domainCode

		) throws BTSLBaseException

		{

			final String methodName = "getDomainDetails";
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Entered ");
			}

			Connection con = null;
			MComConnectionI mcomCon = null;
			CategoryVO requestVO = new CategoryVO();
			String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
			String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
			Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
			CategoryDomainCodeVO response = new CategoryDomainCodeVO();
			try {
				mcomCon = new MComConnection();
				con = mcomCon.getConnection();
				/*
				 * Authentication
				 * 
				 * @throws BTSLBaseException
				 */
				OAuthUser OAuthUserData = new OAuthUser();
				OAuthUserData.setData(new OAuthUserData());
				OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);

				response = c2SBulkRcServiceI.getDomainCode(requestVO, con, domainCode, response1);

			} catch (BTSLBaseException be) {
				log.error("domainCodeRequest", "Exception:e=" + be);
				log.errorTrace(methodName, be);
				String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
				response.setMessageCode(be.getMessageKey());
				response.setMessage(msg);

				if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
					response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
					response.setStatus((Integer.toString(HttpStatus.SC_UNAUTHORIZED)));
				} else {
					response1.setStatus(HttpStatus.SC_BAD_REQUEST);
					response.setStatus((Integer.toString(HttpStatus.SC_BAD_REQUEST)));
				}
			} catch (Exception e) {
				log.error("domainRequest", "Exception:e=" + e);
				log.errorTrace(methodName, e);

				response.setStatus((Integer.toString(HttpStatus.SC_BAD_REQUEST)));
				String resmsg = RestAPIStringParser.getMessage(
						new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
						PretupsErrorCodesI.USER_UNAUTHORIZED, null);

				response.setMessage(resmsg);
			} finally {
				if (mcomCon != null) {
					mcomCon.close("C2SBulkRechargeController#" + methodName);
					mcomCon = null;
				}
				if (log.isDebugEnabled()) {
					log.debug(methodName, "Exited");
				}
				try {
					if (con != null) {
						con.close();
					}
				} catch (Exception e) {
					log.errorTrace(methodName, e);
				}
			}

			return response;
		}
		
		
		
		   /**
	     * @author sarthak.saini
	     * @param headers
	     * @param requestVO
	     * @param responseSwag
	     * @param httpServletRequest
	     * @return
	     * @throws Exception
	     */
	    @PostMapping(value = "/rescheduleBatchRecharge", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
		@ResponseBody
	    /*@ApiOperation(value = "Reschedule Batch Recharge", response = RescheduleBatchRechargeResponseVO.class ,
	    		authorizations = {
	    	            @Authorization(value = "Authorization")
	    })
	    
	    @ApiResponses(value = {
	    		@ApiResponse(code = 200, message = "OK", response = RescheduleBatchRechargeResponseVO.class),
				@ApiResponse(code = 400, message = "Bad Request"),
				@ApiResponse(code = 401, message = "Unauthorized"),
				@ApiResponse(code = 404, message = "Not Found")
		        })
	*/

		@io.swagger.v3.oas.annotations.Operation(summary = "${rescheduleBatchRecharge.summary}", description="${rescheduleBatchRecharge.description}",

				responses = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = RescheduleBatchRechargeResponseVO.class))
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


		public RescheduleBatchRechargeResponseVO processRescheduleBatchRecharge(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = "rescheduleBatchRechargeRequestVO",  required = true) 
			@RequestBody RescheduleBatchRechargeRequestVO requestVO,
			HttpServletResponse responseSwag, HttpServletRequest httpServletRequest) throws Exception {
	    	
	    	
		   final String methodName = "processRescheduleBatchRecharge";
		   if (log.isDebugEnabled()) {
			   log.debug(methodName, "Entered ");
		   }
		   RescheduleBatchRechargeResponseVO response =null;
		   Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));

		try {
				response = new RescheduleBatchRechargeResponseVO();
			  	OAuthUser oAuthUserData=new OAuthUser();
	  	        oAuthUserData.setData(new OAuthUserData());
	  	        OAuthenticationUtil.validateTokenApi(oAuthUserData,headers,responseSwag);
	  	        response = c2SBulkRcServiceI.processRescheduleFile(requestVO, oAuthUserData,httpServletRequest,responseSwag);
	  	        
		}catch(BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			response.setMessageCode(be.getMessageKey());
			response.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
				responseSwag.setStatus((HttpStatus.SC_UNAUTHORIZED));
			} else {
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus((HttpStatus.SC_BAD_REQUEST));
			}
		}
		
		catch(Exception e)
		{
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
					PretupsErrorCodesI.USER_UNAUTHORIZED, null);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setMessage(resmsg);
		}
		finally {
			if (log.isDebugEnabled()) {
				log.debug("userNameEntered", " Exited ");
			}
		}

		return response;
	}    
	    
	@GetMapping(value = "/getTopupBatches", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "View Topup Batches", response = ViewScheduleDetailsBatchResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = ViewScheduleDetailsBatchResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${getTopupBatches.summary}", description="${getTopupBatches.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ViewScheduleDetailsBatchResponseVO.class))
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



	public ViewScheduleDetailsBatchResponseVO processGetBatchesRequest(

			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse response1,
			HttpServletRequest httprequest,
			@Parameter(description = "searchLoginId", required = true) @RequestParam("searchLoginId") String searchLoginId,
			@Parameter(description = "scheduleStatus", required = true) @RequestParam("scheduleStatus") String scheduleStatus,
			@Parameter(description = "serviceType", required = true) @RequestParam("serviceType") String serviceType,
			@Parameter(description = "dateRange", required = true) @RequestParam("dateRange") String dateRange 
	) {

		final String methodName = "processGetBatchesRequest";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		ViewScheduleDetailsBatchResponseVO response = new ViewScheduleDetailsBatchResponseVO();
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));

		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
			String sessionUserLoginId = OAuthUserData.getData().getLoginid();

			response = c2SBulkRcServiceI.processViewBatchScheduleDetails(con, sessionUserLoginId, response1,
					searchLoginId, scheduleStatus, serviceType, dateRange);
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			response.setMessageCode(be.getMessageKey());
			response.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(Integer.toString(PretupsI.UNAUTHORIZED_ACCESS));
			} else {
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(Integer.toString(PretupsI.RESPONSE_FAIL));
			}
		} catch (Exception e) {
			log.error(methodName, "Exception:e=" + e);
			log.errorTrace(methodName, e);

			response.setStatus(Integer.toString(PretupsI.RESPONSE_FAIL));
			response.setMessageCode("error.general.processing");
			response.setMessage(
					"Due to some technical reasons, your request could not be processed at this time. Please try later");
		} finally {
			if (mcomCon != null) {
				mcomCon.close("C2SBulkRechargeController#" + methodName);
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exited");
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
		}

		return response;
	}
	
	/**
     * 
     * @param headers
     * @param loginId
     * @param msisdn
     * @param responseSwag
     * @param httpServletRequest
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/viewScheduleTopup",produces = MediaType.APPLICATION_JSON)
	@ResponseBody
    /*@ApiOperation(value = "View Schedule Topup", response = ViewScheduleDetailsListResponseVO.class,
    		authorizations = {
    	            @Authorization(value = "Authorization")
    })
    
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK", response = ViewScheduleDetailsListResponseVO.class),
	        @ApiResponse(code = 400, message = "Bad Request" ),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found") })
    */

	@io.swagger.v3.oas.annotations.Operation(summary = "${viewScheduleTopup.summary}", description="${viewScheduleTopup.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ViewScheduleDetailsListResponseVO.class))
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


	public ViewScheduleDetailsListResponseVO processViewScheduleDetails(
		@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
		@Parameter(description = "loginId", required = true) @RequestParam("searchLoginId") String loginId,
		@Parameter(description = "msisdn", required = true) @RequestParam("msisdn") String msisdn,
		HttpServletResponse responseSwag, HttpServletRequest httpServletRequest) throws Exception {
    	
    	final String methodName = "processViewScheduleDetails";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		Connection con = null;
		MComConnectionI mcomCon = null;
		UserDAO userDao = null;
		ViewScheduleDetailsListResponseVO response = new ViewScheduleDetailsListResponseVO();
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
		response.setService("VIEWSCHEDULEDETAILSRESP");
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			OAuthUser oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			// getting loggedIn user details
			userDao = new UserDAO();
			UserVO sessionUserVO = userDao.loadAllUserDetailsByLoginID(con, oAuthUser.getData().getLoginid());
			response = c2SBulkRcServiceI.processViewScheduleDetails(con , sessionUserVO , loginId , msisdn);
			if(response.getScheduleDetailList().size() == 0) {
				String msg = RestAPIStringParser.getMessage(
						new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),PretupsErrorCodesI.NO_RECORD_AVAILABLE, null);
				response.setMessage(msg);
			}else {
				String resmsg = RestAPIStringParser.getMessage(
						new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),PretupsErrorCodesI.SUCCESS, null);
				response.setMessage(resmsg);
			}
			response.setStatus(Integer.toString(PretupsI.RESPONSE_SUCCESS));
		}catch(BTSLBaseException be) {
			log.error("processRequest", "Exception:e=" + be);
			log.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), be.getArgs());
			response.setMessageCode(be.getMessageKey());
			response.setMessage(msg);
			
			if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(Integer.toString(HttpStatus.SC_UNAUTHORIZED));
			}else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(Integer.toString(HttpStatus.SC_BAD_REQUEST));
			}
		}catch(Exception e) {
			log.error("processRequest", "Exception:e=" + e);
			log.errorTrace(methodName, e);
			
			response.setStatus(Integer.toString(PretupsI.RESPONSE_FAIL));
    		response.setMessageCode("error.general.processing");
    		response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");
		}finally {
			if(mcomCon != null) {
				mcomCon.close("C2SBulkRechargeController#"+methodName);
				mcomCon=null;
			}
			if(log.isDebugEnabled()) {
				log.debug(methodName, "Exited");
			}
			try {
				if(con != null) {
					con.close();
				}
			}catch(Exception e) {
					log.errorTrace(methodName, e);
				}
			}
		
		return response;
	}
	
	/**
     * 
     * @param headers
     * @param loginId
     * @param msisdn
     * @param responseSwag
     * @param httpServletRequest
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/viewScheduleTopupReport",produces = MediaType.APPLICATION_JSON)
	@ResponseBody
    /*@ApiOperation(value = "View Schedule Topup", response = ViewScheduleDetailsListResponseVO.class,
    		authorizations = {
    	            @Authorization(value = "Authorization")
    })

    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK", response = ViewScheduleDetailsListResponseVO.class),
	        @ApiResponse(code = 400, message = "Bad Request" ),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found") })
    */


	@io.swagger.v3.oas.annotations.Operation(summary = "${viewScheduleTopupReport.summary}", description="${viewScheduleTopupReport.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ViewScheduleDetailsListResponseVO.class))
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



	public ViewScheduleDetailsListResponseVO processScheduleReportRequest(
		@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
		@Parameter(description = "loginId", required = true) @RequestParam("searchLoginId") String loginId,
		@Parameter(description = "msisdn", required = true) @RequestParam("msisdn") String msisdn,
		@Parameter(description = "staffFlag", required = true)	@RequestParam("staffFlag") String staffFlag,
		@Parameter(description = "dateRange", required = true)	@RequestParam("dateRange") String dateRange,
		HttpServletResponse responseSwag, HttpServletRequest httpServletRequest) throws Exception {
    	
    	final String methodName = "processScheduleReportRequest";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		Connection con = null;
		MComConnectionI mcomCon = null;
		UserDAO userDao = null;
		ViewScheduleDetailsListResponseVO response = new ViewScheduleDetailsListResponseVO();
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
		response.setService("VIEWSCHEDULEDETAILSRESP");
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			OAuthUser oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			// getting loggedIn user details
			userDao = new UserDAO();
			UserVO sessionUserVO = userDao.loadAllUserDetailsByLoginID(con, oAuthUser.getData().getLoginid());
			response = c2SBulkRcServiceI.processScheduleReportRequest(con , sessionUserVO , loginId , msisdn, dateRange, staffFlag);
			if(response.getScheduleDetailList().size() == 0) {
				String msg = RestAPIStringParser.getMessage(
						new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),PretupsErrorCodesI.NO_RECORD_AVAILABLE, null);
				response.setMessage(msg);
			}else {
				String resmsg = RestAPIStringParser.getMessage(
						new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),PretupsErrorCodesI.SUCCESS, null);
				response.setMessage(resmsg);
			}
			response.setStatus(Integer.toString(PretupsI.RESPONSE_SUCCESS));
		}catch(BTSLBaseException be) {
			log.error("processRequest", "Exception:e=" + be);
			log.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), be.getArgs());
			response.setMessageCode(be.getMessageKey());
			response.setMessage(msg);
			
			if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(Integer.toString(HttpStatus.SC_UNAUTHORIZED));
			}else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(Integer.toString(HttpStatus.SC_BAD_REQUEST));
			}
		}catch(Exception e) {
			log.error("processRequest", "Exception:e=" + e);
			log.errorTrace(methodName, e);
			
			response.setStatus(Integer.toString(PretupsI.RESPONSE_FAIL));
    		response.setMessageCode("error.general.processing");
    		response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");
		}finally {
			if(mcomCon != null) {
				mcomCon.close("C2SBulkRechargeController#"+methodName);
				mcomCon=null;
			}
			if(log.isDebugEnabled()) {
				log.debug(methodName, "Exited");
			}
			try {
				if(con != null) {
					con.close();
				}
			}catch(Exception e) {
					log.errorTrace(methodName, e);
				}
			}
		
		return response;
	}
    
    @GetMapping(value = "/getCategories", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
    /*@ApiOperation(value = "Get user categories for recharge", response = RescheduleBatchRechargeResponseVO.class ,
    		authorizations = {
    	            @Authorization(value = "Authorization")
    })
    
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK", response = RescheduleBatchRechargeResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found")
	        })*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${getCategories.summary}", description="${getCategories.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CategoryResponseVO.class))
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



	public CategoryResponseVO getCategoryList(
		@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
		HttpServletResponse responseSwag, HttpServletRequest httpServletRequest) throws Exception 
    {
    	final String classMethodName = "C2sBulkRechargeController#" + "getCategoryList";
    	final String methodName = "getCategoryList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		CategoryResponseVO response = new CategoryResponseVO();
		
		Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));

		UserDAO userDao = new UserDAO();
		UserVO loggedInUserVO = null;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			/*
			 * Authentication
			 * 
			 * @throws BTSLBaseException
			 */
			OAuthUser oAuthUserData = new OAuthUser();
			oAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(oAuthUserData, headers, responseSwag);
			
			loggedInUserVO = userDao.loadAllUserDetailsByLoginID(con, oAuthUserData.getData().getLoginid());
			
			c2SBulkRcServiceI.getCategoryList(loggedInUserVO, response, con);
			
			
			
		}
		catch (BTSLBaseException be) {
			log.error(classMethodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			response.setMessageCode(be.getMessageKey());
			response.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			log.error("domainRequest", "Exception:e=" + e);
			log.errorTrace(methodName, e);

			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
					PretupsErrorCodesI.USER_UNAUTHORIZED, null);

			response.setMessage(resmsg);
		} finally {
			if (mcomCon != null) {
				mcomCon.close(classMethodName);
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exited");
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
		}

		return response;
    }
    
    
    
    
    @GetMapping(value = "/getCategoriesWithoutTransferRules/categoryCode/domainCode", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
    /*@ApiOperation(value = "Get categories by hierarchy without tranfer rules", response = CategoryRespVO.class ,
    		authorizations = {
    	            @Authorization(value = "Authorization")
    })
    
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK", response = CategoryRespVO.class),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found")
	        })*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${getCategoriesWithoutTransferRules.summary}", description="${getCategoriesWithoutTransferRules.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CategoryRespVO.class))
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



	public CategoryRespVO getCategoryListWithoutTransfRules(
		@Parameter(description = SwaggerAPIDescriptionI.CATEGORY_CODE, example = "",required = true) 
		@RequestParam("categoryCode") String categoryCode,
		@Parameter(description = SwaggerAPIDescriptionI.DOMAIN_CODE, example = "",required = true) 
		@RequestParam("domainCode") String domainCode,
		@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
		HttpServletResponse responseSwag, HttpServletRequest httpServletRequest) throws Exception 
    {
    	final String classMethodName = "C2sBulkRechargeController#" + "getCategoriesWithoutTransferRules";
    	final String methodName = "getCategoriesWithoutTransferRules";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		CategoryRespVO response = new CategoryRespVO();
		
		Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));

		UserDAO userDao = new UserDAO();
		UserVO loggedInUserVO = null;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
			OAuthUser oAuthUserData = new OAuthUser();
			oAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(oAuthUserData, headers, responseSwag);
			loggedInUserVO = userDao.loadAllUserDetailsByLoginID(con, oAuthUserData.getData().getLoginid());
			c2SBulkRcServiceI.getCategoryListWithoutTransferRules(loggedInUserVO, response, con,domainCode,categoryCode);
		}
		catch (BTSLBaseException be) {
			log.error(classMethodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			response.setMessageCode(be.getMessageKey());
			response.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			log.error("domainRequest", "Exception:e=" + e);
			log.errorTrace(methodName, e);

			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
					PretupsErrorCodesI.USER_UNAUTHORIZED, null);

			response.setMessage(resmsg);
		} finally {
			if (mcomCon != null) {
				mcomCon.close(classMethodName);
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exited");
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
		}

		return response;
    }

	
    //Get Services List
    

	/**
	 * @author harshita.bajaj
	 * @param headers
	 * @param response1
	 * @param httprequest
	 * @return
	 */

    
	@GetMapping(value = "/servicesList", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(tags = "C2S Services", value = "C2S Services", response = ServiceListResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = ServiceListResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${servicesList.summary}", description="${servicesList.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ServiceListResponseVO.class))
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



	public ServiceListResponseVO processServicesList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httprequest)

	{

		final String methodName = "processServicesList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		ServiceListResponseVO response = new ServiceListResponseVO();
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));

		try {
			
			/*
			 * Authentication
			 * 
			 * @throws BTSLBaseException
			 */
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
			String loginId = OAuthUserData.getData().getLoginid();
			response = c2SBulkRcServiceI.servicesList(loginId,con, response1);
		} catch (BTSLBaseException be) {
			log.error("processRequest", "Exception:e=" + be);
			log.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			response.setMessageCode(be.getMessageKey());
			response.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(401);
			} else {
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(400);
			}
		} catch (Exception e) {
			log.error("processRequest", "Exception:e=" + e);
			log.errorTrace(methodName, e);

			response.setStatus(PretupsI.RESPONSE_FAIL);
			response.setMessageCode("error.general.processing");
			response.setMessage(
					"Due to some technical reasons, your request could not be processed at this time. Please try later");
		} finally {
			if (mcomCon != null) {
				mcomCon.close("ServicesController#" + methodName);
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exited");
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
		}

		return response;
	}
	
	
	
	/*
     * This method is for bulk prepaid recharge
     */
    @PostMapping(value = "/c2sbulkEVD", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody
    /*@ApiOperation(value = "Bulk EVD Recharge", response = C2SBulkEvdRechargeResponseVO.class ,
    		authorizations = {
    	            @Authorization(value = "Authorization")
    })
    
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = C2SBulkEvdRechargeResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${c2sbulkEVD.summary}", description="${c2sbulkEVD.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = C2SBulkEvdRechargeResponseVO.class))
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



	public C2SBulkEvdRechargeResponseVO processBulkEvdRechargeRequest(
    		@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
            @Parameter(description = SwaggerAPIDescriptionI.BULK_EVD_RECHARGE, required = true)
            @RequestBody C2SBulkEvdRechargeRequestVO requestVO,HttpServletResponse response1) throws Exception {
	    final String methodName = "processBulkEvdRechargeRequest";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		
		
	    ++requestIdChannel;
	    C2SBulkEvdRechargeResponseVO response = new C2SBulkEvdRechargeResponseVO();
        final String requestIDStr = String.valueOf(requestIdChannel);
        response = c2SBulkRcServiceI.processRequestBulkEVD(requestVO, PretupsI.SERVICE_TYPE_EVD, requestIDStr, PretupsI.SCHEDULE, headers, response1);
        
        return response;
    }
    
    
    
    
    
    
    
    
    @GetMapping(value = "/getReconcileServicesList", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(tags = "C2S Services", value = "C2S Services", response = ReconcileServiceListResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = ReconcileServiceListResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${getReconcileServicesList.summary}", description="${getReconcileServicesList.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ReconcileServiceListResponseVO.class))
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



	public ReconcileServiceListResponseVO getReconcileServicesList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httprequest)

	{

		final String methodName = "getReconcileServicesList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		ReconcileServiceListResponseVO response = new ReconcileServiceListResponseVO();
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));

		try {
			
			/*
			 * Authentication
			 * 
			 * @throws BTSLBaseException
			 */
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
			String loginId = OAuthUserData.getData().getLoginid();
			
			response = c2SBulkRcServiceI.getReconservicesList(loginId,con, response1);
		} catch (BTSLBaseException be) {
			log.error("getReconcileServicesList", "Exception:e=" + be);
			log.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			response.setMessageCode(be.getMessageKey());
			response.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(401);
			} else {
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(400);
			}
		} catch (Exception e) {
			log.error("processRequest", "Exception:e=" + e);
			log.errorTrace(methodName, e);

			response.setStatus(PretupsI.RESPONSE_FAIL);
			response.setMessageCode("error.general.processing");
			response.setMessage(
					"Due to some technical reasons, your request could not be processed at this time. Please try later");
		} finally {
			if (mcomCon != null) {
				mcomCon.close("ServicesController#" + methodName);
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exited");
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
		}

		return response;
	}
	

    
    
    
    
    
    
    
    

	
	
    
	}

