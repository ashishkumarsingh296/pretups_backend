package com.restapi.c2s.services;


import io.swagger.v3.oas.annotations.Parameter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Parameter;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingDAO;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SwaggerAPIDescriptionI;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${C2SSubServicesController.name}", description = "${C2SSubServicesController.desc}")//@Api(tags ="C2S Services", value="C2S Services")
@RestController
@RequestMapping(value = "/v1/c2sServices")

public class C2SSubServicesController {
	public static final Log _log = LogFactory.getLog(C2SSubServicesController.class.getName());
	@SuppressWarnings("unchecked")
	@GetMapping(value="/subServices/serviceName", produces =MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(tags= "C2S Services", value = "Fetch Sub Services For Service Types", response = SubServicesListVO.class,
			notes = "Give input 'ALL' to get sub-services list for every service type",

			authorizations = {
    	            @Authorization(value = "Authorization")})


	@ApiResponses(value = {
	        @ApiResponse(code = 400, message = "Bad Request")
	        })
	*/


	@io.swagger.v3.oas.annotations.Operation(summary = "${subServices.summary}", description="${subServices.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = SubServicesListVO.class))
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



	public SubServicesListVO subServicesList(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = SwaggerAPIDescriptionI.SERVICE_TYPES,  required = true)
			@RequestParam("serviceName") String serviceName, HttpServletResponse response1
			//@Parameter(description = SwaggerAPIDescriptionI.SERVICE_TYPES, required = true,  allowableValues = "ALL,GRC,DTH,RC,PPB,INTRRC,EVD,MVD,PSTNRC,EVR,VAS,DVD")
			//@PathVariable("serviceName") String serviceName
			) throws IOException, SQLException, BTSLBaseException
	{
		final String methodName =  "subServicesList";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");
		}
		Connection con = null;
        MComConnectionI mcomCon = null;
        SubServicesListVO response = null;
        ArrayList<String> arguments = null;
        try {
			
			mcomCon = new MComConnection();
            con=mcomCon.getConnection();
			response = new SubServicesListVO();
			
			OAuthenticationUtil.validateTokenApi(headers);
			
			
			HashMap<String, ServiceSelectorMappingVO> serviceTypeSelectorMasterMap = new ServiceSelectorMappingDAO().loadServiceTypeSelectorMap();
			HashMap<String, List<SubServiceVO> > subServicesListResp = new HashMap<String, List<SubServiceVO> >();
			for(Entry<String, ServiceSelectorMappingVO> entry : serviceTypeSelectorMasterMap.entrySet()) {
				String key = entry.getKey();
				ServiceSelectorMappingVO serviceSelectorMappingVO = entry.getValue();
				if(serviceName.equalsIgnoreCase("ALL") || key.split("_")[0].equalsIgnoreCase(serviceName))
				{
					if(!(subServicesListResp.containsKey(key.split("_")[0])))
						subServicesListResp.put(key.split("_")[0], new ArrayList<SubServiceVO> ());
					
					SubServiceVO subServiceVO = new SubServiceVO();
					subServiceVO.setSubServiceCode(serviceSelectorMappingVO.getSelectorCode());
					subServiceVO.setSubServiceName(serviceSelectorMappingVO.getSelectorName());
					subServicesListResp.get(key.split("_")[0]).add(subServiceVO);
				}
			}
			response.setSubServicesList(subServicesListResp);
			response.setStatus(PretupsI.RESPONSE_SUCCESS);
            response.setMessageCode(PretupsErrorCodesI.SUCCESS);
            String resmsg  = RestAPIStringParser.getMessage(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))), PretupsErrorCodesI.SUCCESS, null);
            response.setMessage(resmsg);
        }
        catch (BTSLBaseException be) {
       	 	_log.error(methodName, "Exception:e=" + be);
            _log.errorTrace(methodName, be);
            if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
            	 response.setStatus(HttpStatus.SC_UNAUTHORIZED);
            	 response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
            }
             else{
             response.setStatus(HttpStatus.SC_BAD_REQUEST);
             response1.setStatus(HttpStatus.SC_BAD_REQUEST);
             }
            String resmsg  = RestAPIStringParser.getMessage(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))), be.getMessage(), BTSLUtil.isNullOrEmptyList(arguments)?null:arguments.toArray(new String[arguments.size()]));
    	    response.setMessageCode(be.getMessage());
    	    response.setMessage(resmsg);
        }
        catch (Exception e) {
        	_log.error(methodName, "Exception:e=" + e);
            _log.errorTrace(methodName, e);
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
        	response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setMessageCode(e.getMessage());
			response.setMessage(e.getMessage());
        } finally {
        	try {
        		if (mcomCon != null) {
        			mcomCon.close("C2SSubServicesController#subServicesList");
        			mcomCon = null;
        		}
        	} 
        	catch (Exception e) {
            _log.errorTrace(methodName, e);
        	}
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, " Exited ");
        	}
        }
        return response;
	}
}
