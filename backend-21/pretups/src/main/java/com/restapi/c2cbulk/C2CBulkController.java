package com.restapi.c2cbulk;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.SwaggerAPIDescriptionI;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;


@io.swagger.v3.oas.annotations.tags.Tag(name = "${C2CBulkController.name}", description = "${C2CBulkController.desc}")//@Api(tags = "C2C Batch Services", defaultValue = "C2C Batch Services")
@RestController
@RequestMapping(value = "/v1/c2cbatchservices")
public class C2CBulkController {

	public static final Log log = LogFactory.getLog(C2CBulkController.class.getName());

	
	 @Autowired
	 private C2CBulkServiceI c2CBulkServiceI;
	 
	 
	 private static long requestIdChannel = 0;

	    @PostMapping(value = "/c2cbatchapprocess", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
		@ResponseBody
	    /*@ApiOperation(value = "C2C Bulk process Approval", response = C2CProcessBulkApprovalResponseVO.class ,
	    		authorizations = {
	    	            @Authorization(value = "Authorization")
	    })
	    
		@ApiResponses(value = { 
				@ApiResponse(code = 200, message = "OK", response = C2CProcessBulkApprovalResponseVO.class),
				@ApiResponse(code = 400, message = "Bad Request"),
				@ApiResponse(code = 401, message = "Unauthorized"), 
				@ApiResponse(code = 404, message = "Not Found") })
*/

		@io.swagger.v3.oas.annotations.Operation(summary = "${c2cbatchapprocess.summary}", description="${c2cbatchapprocess.description}",

				responses = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = C2CProcessBulkApprovalResponseVO.class))
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



		public C2CProcessBulkApprovalResponseVO processBulkC2cApprovalprocessRequest(
	    		@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
	            @Parameter(description = SwaggerAPIDescriptionI.BULK_C2C_PROCESS_APPROVAL, required = true)
	            @RequestBody C2CProcessBulkRequestVO requestVO,HttpServletResponse response1,HttpServletRequest httprequest) throws Exception {
	    	
	    	C2CProcessBulkApprovalResponseVO response = new C2CProcessBulkApprovalResponseVO();
	    	
	    	response =	c2CBulkServiceI.processC2cBulkTrfAppProcess(headers, response1, requestVO,httprequest);
	         return response;
	    
	           }
	    
	    
	 
	 
	 
	 
	 
	 
	 
	 
	
}
