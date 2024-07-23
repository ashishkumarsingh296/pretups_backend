package com.btsl.pretups.channel.transfer.requesthandler;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;


import io.swagger.v3.oas.annotations.media.ExampleObject;
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
import com.btsl.common.BaseResponseMultiple;
import com.btsl.common.ErrorMap;
import com.btsl.common.MasterErrorList;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SqlParameterEncoder;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.restapi.o2c.service.C2CBulkApprovalRequestVO;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${C2CBulkApprovalController.name}", description = "${C2CBulkApprovalController.desc}")//@Api(tags = "C2C Batch Services", defaultValue = "C2C Batch Services")
@RestController
@RequestMapping(value = "/v1/c2cBatch")
public class C2CBulkApprovalController {
	
	protected final Log log = LogFactory.getLog(getClass().getName());

	@Autowired
	private C2CBulkApprovalServiceI c2cBulkApprovalServiceI;

	@GetMapping(value = "/loadAllApprovalBatches", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Load All Bulk Approval List", notes = ("Api Info:") + ("\n")
			+ ("Domain and Geography is fixed.") + ("\n")
			+ ("Category: All categories allowed to the channel users"),
			response = C2cBatchesApprovalDetailsVO.class, authorizations = {
					@Authorization(value = "Authorization") })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = C2cBatchesApprovalDetailsVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${loadAllApprovalBatches.summary}", description="${loadAllApprovalBatches.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = C2cBatchesApprovalDetailsVO.class))
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



	public C2cBatchesApprovalDetailsVO loadC2cBulkApprovalDetails(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,

			@Parameter(description = "domain", required = true)// allowableValues = "ALL")
			@RequestParam("domain") String domain,
			@Parameter(description = "category", required = true)// allowableValues = "ALL, DIST, SE, AG, RET")
			@RequestParam("category") String category,
			@Parameter(description = "geography", required = true)// allowableValues = "ALL")
			@RequestParam("geography") String geography,
			HttpServletResponse responseSwag) throws IOException, SQLException, BTSLBaseException {

		final String methodName = "loadAllC2cBulkApprovalDetails";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		C2cBatchesApprovalDetailsVO response = new C2cBatchesApprovalDetailsVO();
		response.setService("LOADALLAPPROVALBATCHES");

		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);
		
		try {

			domain = SqlParameterEncoder.encodeParams(domain);
			category = SqlParameterEncoder.encodeParams(category);
			geography = SqlParameterEncoder.encodeParams(geography);

			/*
			 * Authentication
			 * 
			 * @throws BTSLBaseException
			 */
			OAuthUser oAuthUserData = new OAuthUser();
			oAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(oAuthUserData, headers, new BaseResponseMultiple());

			String loginId = oAuthUserData.getData().getLoginid();

			// basic form validation at api level
			
			MasterErrorList masterError = null;

			ErrorMap errorMap = new ErrorMap();

			ArrayList<String> errorList = new ArrayList();
			ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
			
			
			
			if (BTSLUtil.isNullString(category)) {
				masterError = new MasterErrorList();
				masterError.setErrorCode(PretupsErrorCodesI.BLANK_APLHA_CAT);
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BLANK_APLHA_CAT, null);
				masterError.setErrorMsg(msg);
				masterErrorLists.add(masterError);
			}

			errorMap.setMasterErrorList(masterErrorLists);

			if (errorMap.getMasterErrorList().size() >= 1) {

				response.setErrorMap(errorMap);
				response.setMessageCode("MULTI_VALIDATION_ERROR");
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				String badReq = Integer.toString(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(badReq);

				if (log.isDebugEnabled()) {
					log.debug(methodName, " Exited ");
				}

				return response;
			}

			c2cBulkApprovalServiceI.loadAllC2cBulkApprovalDetails(oAuthUserData, response, responseSwag, category);

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
	
	@PostMapping(value = "/c2cbulkApproval", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody

	/*@ApiOperation(value = "C2C bulk Approval", response = BaseResponseMultiple.class, authorizations = {
			@Authorization(value = "Authorization") })

	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = BaseResponseMultiple.class),
			@ApiResponse(code = 400, message = "Bad Request"), 
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${c2cbulkApproval.summary}", description="${c2cbulkApproval.description}",

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
									,examples = {
									@io.swagger.v3.oas.annotations.media.ExampleObject(name = "The String example", value = "urgheiurgheirghieurg"),
									@io.swagger.v3.oas.annotations.media.ExampleObject(name = "The Integer example", value = "311414") })
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



	public BaseResponseMultiple approvec2cBulkTransfer(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse responseSwag, @RequestBody C2CBulkApprovalRequestVO c2cBulkApprovalRequestVO)
			throws BTSLBaseException, SQLException, JsonParseException, JsonMappingException, IOException 
	{
		final String methodName = "approvec2cBulkTransfer";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		BaseResponseMultiple response = new BaseResponseMultiple();

		response = c2cBulkApprovalServiceI.processc2cBulkApproval(headers,c2cBulkApprovalRequestVO, responseSwag);

		return response;

	}

}
