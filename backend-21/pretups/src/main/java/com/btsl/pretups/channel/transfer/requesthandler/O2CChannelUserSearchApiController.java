package com.btsl.pretups.channel.transfer.requesthandler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.util.SwaggerAPIDescriptionI;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
@io.swagger.v3.oas.annotations.tags.Tag(name = "${O2CChannelUserSearchApiController.name}", description = "${O2CChannelUserSearchApiController.desc}")//@Api(tags="O2C Services")
@RestController
@RequestMapping(value = "/v1/o2c")
public class O2CChannelUserSearchApiController {
	public static final Log log = LogFactory.getLog(O2CChannelUserSearchApiController.class.getName());

	/**
	 * @(#)O2CChannelUserSearchApiController.java This method gets the owner users
	 *                                            list
	 * 
	 * @param networkCode
	 * @param identifierType
	 * @param identifierValue
	 * @param idType
	 * @param id
	 * @param msisdn
	 * @param loginId
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 * @throws BTSLBaseException
	 */
	@Context
    private HttpServletRequest httpServletRequest;
	 @Autowired
	private O2CChannelUserSearchApiServiceI o2CChannelUserSearchApiServiceI;
	@GetMapping(value="/getChannelUserDetails", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation( value = "ChannelUserList",response = SearchChannelUserVOResponseVO.class,notes = ("Api Info:") + ("\n") + ("If categoryCode and channelOwnerCatgory is same,then any input can be entered in channelOwnerCategoryUserID.") + ("\n") ,
			authorizations = {
    	            @Authorization(value = "Authorization")})
	        @ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK",response = SearchChannelUserVOResponseVO.class),
			@ApiResponse(code = 204, message = "No Content"),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })*/


	@io.swagger.v3.oas.annotations.Operation(summary = "${getChannelUserDetails.summary}", description="${getChannelUserDetails.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = SearchChannelUserVOResponseVO.class))
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




	public SearchChannelUserVOResponseVO getSearchChannelUserDetails(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = SwaggerAPIDescriptionI.USERNAME, example ="", required = true)
			@DefaultValue("superdistributer")@RequestParam("userName") String userName,
			@Parameter(description = SwaggerAPIDescriptionI.CHANNEL_OWNER_USER_ID, example ="", required = true)
			@DefaultValue("superdistributer")@RequestParam("channelOwnerCategoryUserID") String channelOwnerCategoryUserID,
			@Parameter(description = SwaggerAPIDescriptionI.CHANNEL_OWNER_CATEGORY, example ="", required = true)
			@DefaultValue("superdistributer")@RequestParam("channelOwnerCategory") String channelOwnerCategory,
			@Parameter(description = SwaggerAPIDescriptionI.CATEGORY_CODE, example ="", required = true)
			@DefaultValue("superdistributer")@RequestParam("categoryCode") String categoryCode,
			@Parameter(description = SwaggerAPIDescriptionI.GEOGRAPHICAL_DOMAIN_CODE, example ="", required = true)
			@DefaultValue("superdistributer")@RequestParam("geoDomainCode") String geoDomainCode,
	        @Parameter(description = SwaggerAPIDescriptionI.CHANNEL_DOMAIN, example ="", required = true)
	        @DefaultValue("superdistributer")@RequestParam("DomainCode") String channelDomainCode,HttpServletResponse response1)
			throws IOException, SQLException, BTSLBaseException {
		final String methodName = "getSearchChannelUserDetails";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		SearchChannelUserVOResponseVO response = new SearchChannelUserVOResponseVO();
		try {
			response = o2CChannelUserSearchApiServiceI.processRequest(userName,channelOwnerCategoryUserID,geoDomainCode,channelOwnerCategory,
					channelDomainCode,categoryCode,
					headers,
					response1);
	        return response;
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);
			response.setService("channelUserDetailsResp");
			response.setStatus(String.valueOf(PretupsI.RESPONSE_FAIL));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), be.getMessageKey(),
					be.getArgs());
			response.setMessageCode(be.getMessage());
			response.setMessage(resmsg);

		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			response.setStatus(String.valueOf(PretupsI.RESPONSE_FAIL));
			response.setMessageCode(e.toString());
			response.setMessage(e.toString() + " : " + e.getMessage());
		} finally {
			if (log.isDebugEnabled()) {
				log.debug(methodName, " Exited ");
			}
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, response);
			log.debug(methodName, "Exiting ");
		}

		return response;
	}
}
