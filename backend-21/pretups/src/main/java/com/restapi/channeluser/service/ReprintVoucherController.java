package com.restapi.channeluser.service;

import java.sql.Connection;
import java.text.SimpleDateFormat;


import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.OracleUtil;
import com.google.gson.JsonObject;

import io.swagger.v3.oas.annotations.Parameter;


@io.swagger.v3.oas.annotations.tags.Tag(name = "${ReprintVoucherController.name}", description = "${ReprintVoucherController.desc}")//@Api(tags="Reprint Voucher",value="Reprint Voucher")
@RestController
@RequestMapping(value = "/v1/reprintVoucher")
public class ReprintVoucherController {
	
	public static final Log log = LogFactory.getLog(ReprintVoucherController.class.getName());
	
	@Autowired
	private ReprintVoucherServiceI reprintVoucherServiceI;
	
	
	@GetMapping("/voucher/")
	@ResponseBody
	/*@ApiOperation(value = "Load all reprint vouchers", response = ReprintVoucherResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") }, notes = "Api Info:" + ("\n")
					+ "1.Provide all reprint vouchers" + ("\n")
					+ "2.\'%' between start serial no to end serial no")

	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = ReprintVoucherResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${voucher.summary}", description="${voucher.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ReprintVoucherResponseVO.class))
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


	public ReprintVoucherResponseVO loadVoucherDetails(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@RequestParam("transactionId") String transactionId,@RequestParam("pin") String pin, HttpServletResponse responseSwag) throws Exception {
		
		final String methodName = "loadVoucherDetails";
		if(log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		Connection con = null;
		MComConnectionI mcomCon = null;
		
		ReprintVoucherResponseVO response =new ReprintVoucherResponseVO();
		try
		{
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, responseSwag);
			
			
			
			//Pin validation code starts
			
			String msisdn = OAuthUserData.getData().getMsisdn();
 	        ChannelUserVO senderVO = new ChannelUserDAO().loadChannelUserDetails(con,msisdn);
			
			if (senderVO.getUserPhoneVO().getPinRequired().equals(PretupsI.YES)) {
				try {
					if(!BTSLUtil.isNullString(pin))
					ChannelUserBL.validatePIN(con, senderVO, pin);
					else{
						throw new BTSLBaseException(PretupsErrorCodesI.CHNL_ERROR_SNDR_BLANK_PIN);
					}
				} catch (BTSLBaseException be) {
					if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN))
							|| (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
						OracleUtil.commit(con);
					}
		             JsonObject json = new JsonObject();
		            String responseStr=null;
		                     //json.addProperty("txnstatus", "206");
		                 
		                     final java.util.Date date = new java.util.Date();
		                     final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.DATE_FORMAT_DDMMYYYY);
		                 json.addProperty("date", sdf.format(date));
		                 json.addProperty("message",
		                         PretupsRestUtil.getMessageString(be.getMessageKey(), null));
		             
		             responseStr = json.toString();
//		             response.setDataObject(PretupsI.RESPONSE_FAIL, false,
//		                     (JsonNode) PretupsRestUtil.convertJSONToObject(responseStr, new TypeReference<JsonNode>() {
//		                     }));
		             response.setMessageCode(responseStr);
		             response.setStatus(HttpStatus.SC_BAD_REQUEST);
		 			 responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		 			 //responseVO.setMessageCode(e.toString());
		 			 //response.setMessage(e.toString() + " : " + e.getMessage());
		 			 response.setMessage(PretupsI.FAIL);
		 			 
					return response;
				}
			}
			//pin validation code ends
			
			
			
			
			
			response = reprintVoucherServiceI.loadVoucherDetails(headers,transactionId,responseSwag,con);
		}
		finally {
			if (mcomCon != null) {
				mcomCon.close("");
				mcomCon = null;
			}
		}
		
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exiting: " );
		}
		//return this.reprintVoucherServiceI.loadVoucherDetails(transactionId);
		
		return response;
		
	}
}
