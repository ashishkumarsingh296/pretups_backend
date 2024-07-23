package com.restapi.user.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.LocaleMasterDAO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${RegularExpressionController.name}", description = "${RegularExpressionController.desc}")//@Api(tags= "Regular Expression", value="Regular Expressions")
@RestController
@RequestMapping(value = "/v1")
public class RegularExpressionController {
	public static final Log log = LogFactory.getLog(RegularExpressionController.class.getName());
	/*@GET
    @Path("/associateProfile/{id}")*/
	//@Context
	//private HttpServletResponse response;
	
	@GetMapping(value= "/regex", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
    //@Produces(MediaType.APPLICATION_JSON)
	/*@ApiOperation(tags= "Regular Expression", value = "Get Regular Expression "
				  )
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = RegularExpressionResponseVO.class),
	        @ApiResponse(code = 400, message = "Bad Request"),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${regex.summary}", description="${regex.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = RegularExpressionResponseVO.class))
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

	public RegularExpressionResponseVO getRegularExpression(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = "language", required = true) 
	        @RequestParam("language") String language,
	        @Parameter(description = "country", required = true) 
	        @RequestParam("country") String country,HttpServletResponse response
			) throws IOException, SQLException, BTSLBaseException {
		final String methodName =  "getRegularExpression";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
        MComConnectionI mcomCon = null;
        RegularExpressionResponseVO response1 = null;
        String messageArray[] = new String[1];
        HashMap<String, Object> resMap = new HashMap<>();
        try {
        	response1= new RegularExpressionResponseVO();
			
			
			if(BTSLUtil.isNullString(language))
			{
				throw new BTSLBaseException("RegularExpressionController", methodName, PretupsErrorCodesI.LANGUAGE_DOES_NOT_EXIST);
	            
			}
			if(BTSLUtil.isNullString(country))
			{
				throw new BTSLBaseException("RegularExpressionController", methodName, PretupsErrorCodesI.COUNTRY_DOES_NOT_EXIST);
	            
			}
			String langCoun=language+"_"+country;
			ArrayList languageList=LocaleMasterDAO.loadLocaleMasterData();
            if (!BTSLUtil.isNullString(langCoun)) {
            boolean flag1=true;
            for(int k=0;k<languageList.size();k++)
            {
            	if(((ListValueVO)languageList.get(k)).getValue().equals(langCoun))
            	{
            		flag1=false;
            	}
            }
			if (flag1 == true) {
				throw new BTSLBaseException("RegularExpressionController", methodName,
						PretupsErrorCodesI.LOCALE_DOES_NOT_EXIST);
			}
		}
            
            Locale locale = new Locale(language, country);

			String msisdnRegex = Constants.getProperty("MSISDN_REGEX");
			String msisdnRegex1=msisdnRegex.replace(PretupsI.MIN, String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH))).intValue()));
			String msisdnFinal=msisdnRegex1.replace(PretupsI.MAX, String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue()));
			String amountRegex = Constants.getProperty("AMOUNT_REGEX");
			String nameRegex = Constants.getProperty("NAME_REGEX");
			String payment = Constants.getProperty("PAYMENT_REGEX");
			String searchMsisdn = Constants.getProperty("SEARCH_MSISDN_REGEX");			
			String searchMsisdnFinal=searchMsisdn.replace(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MAX,
					null), String.valueOf(SystemPreferences.MAX_MSISDN_TEXTBOX_LENGTH));
			String refRegex = Constants.getProperty("REFERENCE_NUMBER_REGEX");
			String payInstRegex =  Constants.getProperty("PAYMENT_INST_NUM_REGEX");
			String fromSerialRegex =  Constants.getProperty("FROM_SERIAL_NO_REGEX");
			String toSerialRegex = Constants.getProperty("TO_SERIAL_NO_REGEX");
			String quantityRegex =  Constants.getProperty("QUANTITY_REGEX");
			String emailRegex =  Constants.getProperty("EMAIL_REGEX");
			String ip4Regex=Constants.getProperty("IP4_REGEX");
			String ip6Regex=Constants.getProperty("IP6_REGEX");
			String ip4CSRegex=Constants.getProperty("IP4CS_REGEX");
			String ip6CSRegex=Constants.getProperty("IP6CS_REGEX");
			String textOnlyRegex=Constants.getProperty("TEXT_ONLY_REGEX"); 
			String longitudeLatitudeRegex=Constants.getProperty("LONGITUDE_LATITUDE_REGEX");
			String passwordRegex=Constants.getProperty("PASSOWRD_REGEX");
			String nameRegexAlphanumeric=Constants.getProperty("NAME_REGEX_ALPHANUMERIC");
			String namewithSpaceRegex=Constants.getProperty("NAMEWITHSPACE_REGEX");
			String timeSlabRegex=Constants.getProperty("TIME_SLAB_REGEX");
			String numberRegex=Constants.getProperty("NUMBER_REGEX");
			String stockTransRegex=Constants.getProperty("STOCK_TRANS_REGEX");
			String numberRegex1=Constants.getProperty("NUMBER_REGEX1");
			String spaceRegex=Constants.getProperty("SPACE_REGEX");
			String loginRegex = Constants.getProperty("LOGIN_REGEX");
			String alphaNumericWithComma = Constants.getProperty("ALPHA_NUMERIC_WITH_COMMA");
			String numericAndSpecialCharacter = Constants.getProperty("NUMERIC_SPECIAL_CHARACTER");
			String multipleMobileNumber = Constants.getProperty("MULTIPLE_MOBILE_NUMBER");
			String cellIdInputRegex = Constants.getProperty("CELL_ID_REGEX");
			String label2Pattern = Constants.getProperty(PretupsI.STK_LABEL2_PATTERN);
			String nameRegexStartWithoutSpace = Constants.getProperty("NAME_REGEX_START_WITHOUTSPACE");
			String alphaNumericWithSingleSpace = Constants.getProperty("ALPHA_NUMERIC_WITH_SPACE");
			String alphabetsWithSingleSpace = Constants.getProperty("ALPHABETS_WITH_SPACE");

			resMap.put("msisdn", msisdnFinal);
			resMap.put("amount", amountRegex);
			resMap.put("gifterName", nameRegex);
			resMap.put("searchMsisdn", searchMsisdnFinal);
			resMap.put("refrenceNumber", refRegex);
			resMap.put("paymentInstNum", payInstRegex);
			resMap.put("fromserialNo", fromSerialRegex);
			resMap.put("toserialNo", toSerialRegex);
			resMap.put("quantity", quantityRegex);
			resMap.put("email", emailRegex);
			resMap.put("ipaddress4", ip4Regex);
			resMap.put("ipaddress6", ip6Regex);
			resMap.put("ipaddress4CS", ip4CSRegex);
			resMap.put("ipaddress6CS", ip6CSRegex);
			resMap.put("textOnly", textOnlyRegex);
			resMap.put("longitudeLatitude", longitudeLatitudeRegex);
			resMap.put("passwordRegex", passwordRegex);
			resMap.put("nameRegexAlphanumeric", nameRegexAlphanumeric);
			resMap.put("payment", payment);
			resMap.put("namewithSpaceRegex",namewithSpaceRegex);
			resMap.put("timeSlabRegex", timeSlabRegex);
			resMap.put("numberRegex", numberRegex);
			resMap.put("stockTransRegex", stockTransRegex);
			resMap.put("numberRegex1", numberRegex1);
			resMap.put("spaceRegex", spaceRegex);
			resMap.put("loginRegex", loginRegex);
			resMap.put("alphaNumericWithComma", alphaNumericWithComma);
			resMap.put("numericAndSpecialCharacter", numericAndSpecialCharacter);
			resMap.put("multipleMobileNumber", multipleMobileNumber);
			resMap.put("cellIdInputRegex",cellIdInputRegex);
			resMap.put("label2Pattern", label2Pattern);
			resMap.put("nameRegexStartWithoutSpace", nameRegexStartWithoutSpace);
			resMap.put("alphaNumericWithSingleSpace", alphaNumericWithSingleSpace);
			resMap.put("alphabetsWithSingleSpace", alphabetsWithSingleSpace);
			response1.setRegMap(resMap);
			response1.setStatus(String.valueOf(PretupsI.RESPONSE_SUCCESS));
			response1.setService("regexresp");
            response1.setMessageCode(PretupsErrorCodesI.SUCCESS);
            String resmsg  = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
            response1.setMessage(resmsg);
        	
        }
        catch (BTSLBaseException be) {
        	 log.error(methodName, "Exception:e=" + be);
             log.errorTrace(methodName, be);
           
              response.setStatus(HttpStatus.SC_BAD_REQUEST);
              response1.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
           
     	   String resmsg  = RestAPIStringParser.getMessage(new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE),(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), be.getMessage(), messageArray);
     	    response1.setMessageCode(be.getMessage());
     	   response1.setMessage(resmsg);
     	   
	}
        catch (Exception e) {
        	 response.setStatus(HttpStatus.SC_BAD_REQUEST);
        	response1.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
         	  response1.setStatus(String.valueOf(PretupsI.RESPONSE_FAIL));
         	  
        } finally {
            try {
            	if (mcomCon != null) {
    				mcomCon.close("RegularExpressionController#getRegularExpression");
    				mcomCon = null;
    			}
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exited ");
            }
        }
		return response1;
	}
}
