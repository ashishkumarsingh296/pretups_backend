package com.restapi.user.service;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.util.SwaggerAPIDescriptionI;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${SuspendResumeRestController.name}", description = "${SuspendResumeRestController.desc}")//@Api(tags="User Services")
@RestController
@RequestMapping(value = "/v1/userServices")
public class SuspendResumeRestController {
	
	public static final Log log = LogFactory.getLog(SuspendResumeRestController.class.getName());
	        @Autowired
			private SuspendResumeServiceI suspendResumeServiceI;
		    @PostMapping(value = "/suspendResume", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
			@ResponseBody
		    /*@ApiOperation(value = "Suspend Resume User", response = SuspendResumeResponse.class ,
		    		authorizations = {
		    	            @Authorization(value = "Authorization")
		    })
			@ApiResponses(value = { 
					@ApiResponse(code = 200, message = "OK", response = SuspendResumeResponse.class),
					@ApiResponse(code = 400, message = "Bad Request"),
					@ApiResponse(code = 401, message = "Unauthorized"), 
					@ApiResponse(code = 404, message = "Not Found") })
*/

			@io.swagger.v3.oas.annotations.Operation(summary = "${suspendResume.summary}", description="${suspendResume.description}",

					responses = {
							@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
									@io.swagger.v3.oas.annotations.media.Content(
											mediaType = "application/json",
											array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = SuspendResumeResponse.class))
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


		    public SuspendResumeResponse processSuspendResumeRequest(
		    		@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
		            @Parameter(description = SwaggerAPIDescriptionI.SUSPEND_RESUME, required = true)
		            @RequestBody SuspendResumeUserVo requestVO,HttpServletResponse response1,HttpServletRequest httprequest) throws Exception {
			    final String methodName = "processSuspendResumeRequest";
				if (log.isDebugEnabled()) {
					log.debug(methodName, "Entered ");
				}

				Locale locale =null;
				SuspendResumeResponse response = null;
				try{
					locale= new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
					requestVO.setData(new OAuthUserData());
					if("staff".equals(requestVO.getSuspendResumeUserDetailsData().getUserType())) {
					response = suspendResumeServiceI.processRequestStaff(requestVO, httprequest, headers, response1);
					}else {
					response = suspendResumeServiceI.processRequest(requestVO, httprequest, headers, response1);
					}
					response.setService("SUSPENDRESUME");
			     }
				catch(BTSLBaseException be){
			        log.error("processRequest", "Exceptin:e=" + be);
			        log.errorTrace(methodName, be);
			        response.setService("SUSPENDRESUME");
		       	    String msg=RestAPIStringParser.getMessage(locale, be.getMessageKey(),null);
			        response.setMessageCode(be.getMessageKey());
			        response.setMessage(msg);
					response.setService("O2CBATCHTRFRESP");

		        	if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
		        		response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
		    	         response.setStatus(401);
		            }
		           else{
		        	   response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		           		response.setStatus(400);
		           }
		        }

		        return response;
		    }

}
