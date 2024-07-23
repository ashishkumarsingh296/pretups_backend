package com.restapi.phoneApi;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.util.VOMSProductVO;
import com.btsl.pretups.inter.util.VOMSVoucherDAO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.OAuthenticationUtil;

import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${PrintVoucherController.name}", description = "${PrintVoucherController.desc}")//@Api(tags="Phone Api",value="Phone Api")
@RestController
@RequestMapping(value = "/v1/phone")
public class PrintVoucherController {

	public static final Log log = LogFactory.getLog(PrintVoucherController.class.getName());
	
	
	@GetMapping(value="/getEVDAvailableVoucherDenom", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation( value = "View available voucher denominations for evd",response = GetChannelUsersListResponseVo.class,
			authorizations = {
    	            @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = GetChannelUsersListResponseVo.class),
	        @ApiResponse(code = 400, message = "Bad Request" ),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${getEVDAvailableVoucherDenom.summary}", description="${getEVDAvailableVoucherDenom.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = PretupsResponse.class))
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


	public PretupsResponse<List<EVDVoucherCountResponseVO>>getEVDAvailableVoucherDenom(
				@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, 
				HttpServletResponse response1 ){
		
		final String methodName =  "getEVDAvailableVoucherDenom";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		Connection con = null;
        MComConnectionI mcomCon = null;
        UserDAO userDao = new UserDAO();
        ChannelUserVO channelUserVO = null;
        String networkCode = "";
        PretupsResponse<List<EVDVoucherCountResponseVO>> response =  new PretupsResponse<List<EVDVoucherCountResponseVO>>();
        EVDVoucherCountResponseVO voucherCountResp = new EVDVoucherCountResponseVO();

		String messageArray[] = new String[1];
		
		try {
            OAuthUser oAuthUserData=new OAuthUser();
            oAuthUserData.setData(new OAuthUserData());
            
            OAuthenticationUtil.validateTokenApi(oAuthUserData,headers,response1);
            
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            
			channelUserVO = userDao.loadUserDetailsByMsisdn(con, oAuthUserData.getData().getMsisdn());
			
			if (channelUserVO == null) {
				response.setResponse(PretupsI.RESPONSE_FAIL, false, "invalid login credentials");
				return response;
			}
			
			networkCode =  channelUserVO.getNetworkID();
			
			VOMSVoucherDAO vomsDAO = new VOMSVoucherDAO();
			Date currDate = new Date();
			String _interfaceID = "INTID00023";
			String timeStampCheck = FileCache.getValue(_interfaceID, "TIME_STAMP_CHK");
            boolean timeStmpChk = false;
            if ("Y".equals(timeStampCheck))
                timeStmpChk = true;
            String reqService = PretupsI.SERVICE_TYPE_EVD;
			
			HashMap<String, VOMSProductVO> profileMap = null;
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue())
            	profileMap=vomsDAO.loadActiveProfilesForPrivateRecharge(con,networkCode,currDate,timeStmpChk,reqService);
            else
            	profileMap=vomsDAO.loadActiveProfiles(con,networkCode,currDate,timeStmpChk,reqService);
            
            if (profileMap.isEmpty()) {
            	String resmsg = RestAPIStringParser.getMessage(
						new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.NO_ACTIVE_DENOM_LIST, messageArray);
				response.setMessageCode(PretupsErrorCodesI.NO_ACTIVE_DENOM_LIST);

            	response.setResponse(PretupsI.RESPONSE_FAIL, false, resmsg);
            	response1.setStatus(HttpStatus.SC_BAD_REQUEST);

            	return response;
			}
            int multiplicationFactor = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue();
            List<VoucherProfile> lst = new ArrayList<>();
            profileMap.forEach((k,v)->{
            	
            	VoucherProfile vp = new VoucherProfile();
            	vp.setVoucherProfileID(v.getProductID());
            	vp.setVoucherProfileName(v.getProductName());
            	int mrp = Integer.parseInt(v.getMrpStr());
            	mrp	= (int)mrp/multiplicationFactor;
            	vp.setDenomination(String.valueOf(mrp));
            	lst.add(vp);
            });
            
            voucherCountResp.setVoucherDetails(lst);
            voucherCountResp.setVoucherName(PretupsI.SERVICE_TYPE_EVD);
            voucherCountResp.setVoucherType(PretupsI.VOUCHER_TYPE_ELECTRONICS);
            List<EVDVoucherCountResponseVO> evdResp = new ArrayList<>();
            evdResp.add(voucherCountResp);

            response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, evdResp);
			
		}
		catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);
			response.setStatusCode(PretupsI.RESPONSE_FAIL);
			if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
	          	 response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
	          }
	           else{
	           response1.setStatus(HttpStatus.SC_BAD_REQUEST);
	           }
			String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), be.getMessageKey(),
					be.getArgs());
			response.setMessageCode(be.getMessage());
			response.setMessage(resmsg);

		} 
		catch(Exception e)
		{
			response.setDataObject(PretupsI.RESPONSE_FAIL, false,null);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close(PrintVoucherController.class.getName()+"#"+methodName);
        		mcomCon=null;
        		}
            if (log.isDebugEnabled()) {
               log.debug(methodName, "Exiting");
            }
        }     
		
		return response;
	}
	
}
