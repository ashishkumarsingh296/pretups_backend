package com.btsl.pretups.channel.transfer.requesthandler;


import io.swagger.v3.oas.annotations.Parameter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Parameter;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.common.BaseResponseMultiple;
import com.btsl.common.ErrorMap;
import com.btsl.common.MasterErrorList;
import com.btsl.common.RowErrorMsgLists;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.receiver.RestReceiver;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

/*@Path("")
@Api(value="C2C Stock Transfer")*/
@io.swagger.v3.oas.annotations.tags.Tag(name = "${C2CTransferMultController.name}", description = "${C2CTransferMultController.desc}")//@Api(tags= "C2C Receiver",value="C2C Receiver")
@RestController
@RequestMapping(value = "/v1/c2cReceiver")
public class C2CTransferMultController {
	
	protected final Log _log = LogFactory.getLog(getClass().getName());
	@PostMapping(value = "/c2ctrfmul", consumes =MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "C2C StockTransfer Multiple", response = BaseResponseMultiple.class,
			authorizations = {
    	            @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = BaseResponseMultiple.class),
	        @ApiResponse(code = 201, message = "Created"),
	        @ApiResponse(code = 400, message = "Bad Request"),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })*/


	@io.swagger.v3.oas.annotations.Operation(summary = "${c2ctrfmul.summary}", description="${c2ctrfmul.description}",

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



	public BaseResponseMultiple<JsonNode> processCP2PUserRequest(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
    		@Parameter(description = SwaggerAPIDescriptionI.C2C_TRANSFER_MUL, required = true)
    		@RequestBody C2CStockTransferMultRequestVO c2cStockTransferMultRequestVO, HttpServletResponse response1) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException, SQLException, BTSLBaseException {
		
		final String methodName = "processCP2PUserRequest_C2CTransferMultController";
        RestReceiver.updateRequestIdChannel();
    	BaseResponseMultiple baseResponseMultiple =null;
    	Connection con = null;
		MComConnectionI mcomCon = null;

    	RequestVO p_requestVO = null;
		
        try {
			/*
			 * Authentication
			 * @throws BTSLBaseException
			 */
        	mcomCon = new MComConnection();
			con = mcomCon.getConnection();
            p_requestVO = new RequestVO();
        	baseResponseMultiple=new BaseResponseMultiple<>();
        	c2cStockTransferMultRequestVO.setData(new OAuthUserData());
        	OAuthenticationUtil.validateTokenApi(c2cStockTransferMultRequestVO, headers,baseResponseMultiple);

			final String METHOD_NAME = "process";
			if (_log.isDebugEnabled()) {
				_log.debug("process", "Entered c2cStockTransferMultRequestVO: " + c2cStockTransferMultRequestVO);
			}
			String _serviceType="";
			String msisdn = c2cStockTransferMultRequestVO.getData().getMsisdn();
			ChannelUserVO senderVO=new ChannelUserDAO().loadChannelUserDetails(con,msisdn);
			
			if (_log.isDebugEnabled()) {
				_log.debug("process", "Entered Sender VO: " + senderVO);
			}
			
			
			_serviceType="TRF";
			ErrorMap errorMap = new ErrorMap();
			ArrayList<RowErrorMsgLists> rowErrorMsgListsFinal = new ArrayList<>();
			ArrayList<BaseResponse> baseResponseFinalSucess = new ArrayList<>();
			p_requestVO.setServiceType(_serviceType);
			p_requestVO.setRequestGatewayType(c2cStockTransferMultRequestVO.getReqGatewayType());
			p_requestVO.setLocale(new Locale(senderVO.getUserPhoneVO().getPhoneLanguage(),senderVO.getUserPhoneVO().getCountry()));
			try {
				
				for (int i = 0; i < c2cStockTransferMultRequestVO.getDataStkTrfMul().size(); i++) {
					RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
					BaseResponse baseResponse = new BaseResponse();
					MasterErrorList masterErrorList = new MasterErrorList();
					RowErrorMsgLists rowErrorMsgListsValidate = new RowErrorMsgLists();
					ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
					DataStockMul dataStkTrfMul = c2cStockTransferMultRequestVO.getDataStkTrfMul().get(i);
					try {
						Boolean flag = TransferBL.C2CValidate(con,
								 dataStkTrfMul, senderVO, rowErrorMsgListsValidate,false);
						if(flag)
						{
							int rowValue=i+1;
					    	String rowName="DATA"+rowValue;
							rowErrorMsgListsValidate.setRowValue(String.valueOf(rowValue));
							rowErrorMsgListsValidate.setRowName(rowName);
							rowErrorMsgListsFinal.add(rowErrorMsgListsValidate);
							
						} else {
							TransferBL.c2cService(con, c2cStockTransferMultRequestVO, p_requestVO, dataStkTrfMul,
									senderVO,false);
							baseResponse.setMessage(RestAPIStringParser.getMessage(
						new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), p_requestVO.getMessageCode(),
						p_requestVO.getMessageArguments()));
							baseResponse.setMessageCode(p_requestVO.getMessageCode());
							baseResponse.setStatus(200);
							baseResponseFinalSucess.add(baseResponse);
							mcomCon.finalCommit();
						}
					} catch (BTSLBaseException be) {
						int rowValue=i+1;
						rowErrorMsgLists.setRowName("DATA" + rowValue);
						rowErrorMsgLists.setRowValue(String.valueOf(rowValue));
						masterErrorList.setErrorCode(String.valueOf(be.getMessageKey()));
						masterErrorList.setErrorMsg(RestAPIStringParser.getMessage(
								new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
								String.valueOf(be.getMessageKey()), be.getArgs()));
						masterErrorLists.add(masterErrorList);
						rowErrorMsgLists.setMasterErrorList(masterErrorLists);
						rowErrorMsgListsFinal.add(rowErrorMsgLists);
						mcomCon.finalRollback();
						continue;
					}
				}

					if(!BTSLUtil.isNullOrEmptyList(rowErrorMsgListsFinal))
					{
						baseResponseMultiple.setStatus("400");
						response1.setStatus(HttpStatus.SC_BAD_REQUEST);
						baseResponseMultiple.setService("C2CMULRESP");
						errorMap.setRowErrorMsgLists(rowErrorMsgListsFinal);
						baseResponseMultiple.setSuccessList(baseResponseFinalSucess);
						baseResponseMultiple.setErrorMap(errorMap);
						return baseResponseMultiple;
					}
				baseResponseMultiple.setSuccessList(baseResponseFinalSucess);
				baseResponseMultiple.setMessageCode(p_requestVO.getMessageCode());
				baseResponseMultiple.setMessage("All records processed successfully");
				baseResponseMultiple.setStatus("200");
				baseResponseMultiple.setService("C2CMULRESP");
				return baseResponseMultiple;
			} catch (Exception e) {
				_log.error(METHOD_NAME, "Exception " + e);
				_log.errorTrace(METHOD_NAME, e);
				p_requestVO.setSuccessTxn(false);
				try {
					if (mcomCon != null) {
						mcomCon.finalRollback();
					}
				} 
				
				catch (SQLException esql) {
					_log.error(METHOD_NAME,"SQLException : ", esql.getMessage());
				}
				_log.error("process", "BTSLBaseException " + e.getMessage());
				_log.errorTrace(METHOD_NAME, e);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
						"C2CTransferController[process]", "", "", "", "Exception:" + e.getMessage());
				baseResponseMultiple.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER);
				baseResponseMultiple.setMessage(RestAPIStringParser.getMessage(
						new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.REQ_NOT_PROCESS,
						null));
				baseResponseMultiple.setService("C2CMULRESP");
				baseResponseMultiple.setStatus("400");
				  response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				return baseResponseMultiple;
			} finally {
				if (mcomCon != null) {
					mcomCon.close("C2CTransferController#process");
					mcomCon = null;
				}
				if (_log.isDebugEnabled()) {
					_log.debug("process", " Exited ");
				}
			} // end of finally
    }catch (BTSLBaseException be) {
		
		_log.error(methodName, "BTSLBaseException " + be.getMessage());
        _log.errorTrace(methodName, be);
        	String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), be.getMessageKey(),
					null);
        	baseResponseMultiple.setMessageCode(be.getMessageKey());
        	baseResponseMultiple.setMessage(resmsg);
        	baseResponseMultiple.setService("C2CMULRESP");
        	 if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
              	 response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
              	baseResponseMultiple.setStatus("401");
              }
               else{
               response1.setStatus(HttpStatus.SC_BAD_REQUEST);
               baseResponseMultiple.setStatus("400");
               }
            return baseResponseMultiple;
        
    } catch (Exception e) {
    	_log.error(methodName, "Exception " + e.getMessage());
        _log.errorTrace(methodName, e);
        baseResponseMultiple.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
        String resmsg = RestAPIStringParser.getMessage(
				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.REQ_NOT_PROCESS,
				null);
    	baseResponseMultiple.setMessage(resmsg);
    	baseResponseMultiple.setService("C2CMULRESP");
    	  response1.setStatus(HttpStatus.SC_BAD_REQUEST);
    	  baseResponseMultiple.setStatus("400");
        return baseResponseMultiple;
    }
	
	finally {
		if (mcomCon != null) {
			mcomCon.close("C2CTransferController#process");
			mcomCon = null;
		}
        LogFactory.printLog(methodName, " Exited ", _log);
    }

	}	
}
