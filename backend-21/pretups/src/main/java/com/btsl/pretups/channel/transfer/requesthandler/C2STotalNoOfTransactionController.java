package com.btsl.pretups.channel.transfer.requesthandler;

import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


import jakarta.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
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
import com.btsl.pretups.channel.transfer.businesslogic.C2STotalTrnsVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STotalTrnxMessage;
import com.btsl.pretups.channel.transfer.businesslogic.C2SserviceAmountRequestParentVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
@Path("")
@Tag(name = "${dummy.name}", description = "${dummy.desc}")//@Api(value="C2S Transaction Count")
public class C2STotalNoOfTransactionController implements ServiceKeywordControllerI {
	
	
	protected final Log _log = LogFactory.getLog(getClass().getName());
	
	 private static final String PROCESS = "C2STotalNoOfTransactionController[process]";
	Connection con = null;
	MComConnectionI mcomCon = null;
	@Context
 	private HttpServletRequest httpServletRequest;
 	@POST
 	@Path("/c2s-receiver/c2stotaltrans")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
 	/*@ApiOperation(value = "Total Transaction Count ", response = PretupsResponse.class)
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${c2stotaltrans.summary}", description="${c2stotaltrans.description}",

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




	public PretupsResponse<JsonNode> processCP2PUserRequest(@Parameter(description = SwaggerAPIDescriptionI.C2S_TOTAL_OF_TRANSACTION_CONTROLLER)C2SserviceAmountRequestParentVO c2SserviceAmountRequestParentVO) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
 		PretupsResponse<JsonNode> response;
         RestReceiver restReceiver;
         RestReceiver.updateRequestIdChannel();
         final String requestIDStr = String.valueOf(RestReceiver.getRequestIdChannel());
         restReceiver = new RestReceiver();
         response = restReceiver.processCP2PRequestOperator(httpServletRequest,(JsonNode)(PretupsRestUtil.convertJSONToObject(PretupsRestUtil.convertObjectToJSONString(c2SserviceAmountRequestParentVO), new TypeReference<JsonNode>(){})), PretupsI.C2S_TOTAL_NO_OF_TRANSACTION,requestIDStr);
         return response;
     }	
	
	private long recentc2ctransactions(C2STotalTrnsVO c2sTotalTrnsVO, String userId) throws BTSLBaseException, Exception {

		final String methodName = "recentc2ctransactions";
		UserDAO userDao = new UserDAO();
		long recentTxn = -1;
		
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			Date fromDate = BTSLUtil.getDateFromDateString(c2sTotalTrnsVO.getFromDate());
			Date toDate = BTSLUtil.getDateFromDateString( c2sTotalTrnsVO.getToDate());
			recentTxn = userDao.totalTranBetweenDate(con,userId, fromDate, toDate); 
						} catch (Exception e) {
			throw new BTSLBaseException(this, methodName, "Error occured", "Exception " + e);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("C2CVoucherApprovalController#saveVoucherProductDetalis");
				mcomCon = null;
			}
		}
		return recentTxn;

	}
	@Override
	public void process(RequestVO p_requestVO) {
		final String methodName = "process";
		PretupsResponse<JsonNode> jsonReponse = new PretupsResponse<JsonNode>();;
		JsonNode dataObject = null;
		HashMap responseMap = new HashMap();
		Gson gson = new Gson();
		C2STotalTrnsVO c2sTotalTrnsVO = new C2STotalTrnsVO();
		C2STotalTrnxMessage reqMsgObj = null;
		long recentC2sRes = -1;
		StringBuffer responseStr = new StringBuffer("");
		try {
			HashMap reqMap = p_requestVO.getRequestMap();
			if ("REST".equalsIgnoreCase(p_requestVO.getRequestGatewayCode())) {
				reqMsgObj = gson.fromJson(p_requestVO.getRequestMessage(), C2STotalTrnxMessage.class);
				c2sTotalTrnsVO.setMsisdn(reqMsgObj.getMsisdn());
				c2sTotalTrnsVO.setLoginId(reqMsgObj.getLoginid());
				
				c2sTotalTrnsVO.setFromDate(reqMsgObj.getFromDate());
				c2sTotalTrnsVO.setToDate(reqMsgObj.getToDate());
			}
			else {
				
				if (reqMap != null && (reqMap.get("MSISDN") != null || reqMap.get("LOGINID") != null)) {
					c2sTotalTrnsVO.setMsisdn((String) reqMap.get("MSISDN"));
					c2sTotalTrnsVO.setLoginId((String) reqMap.get("LOGINID"));
					c2sTotalTrnsVO.setFromDate((String) reqMap.get("FROMDATE"));
					c2sTotalTrnsVO.setToDate((String) reqMap.get("TODATE"));
				}
			}
			
			boolean errorCheck = BTSLDateUtil.checkDate(p_requestVO, c2sTotalTrnsVO.getFromDate(), c2sTotalTrnsVO.getToDate());
			if(errorCheck)
			{
				p_requestVO.setSuccessTxn(false);
				return;
				}
			String userId = p_requestVO.getActiverUserId();
			recentC2sRes = recentc2ctransactions(c2sTotalTrnsVO,userId);
			
			if ("MAPPGW".equalsIgnoreCase(p_requestVO.getRequestGatewayCode())){
				
				
				String resType = null;
				resType = reqMap.get("TYPE") + "RES";
				responseStr.append("{ \"type\": \"" + resType + "\" ,");
				responseStr.append(" \"txnStatus\": \"" + PretupsI.TXN_STATUS_SUCCESS + "\" ,");
				responseStr.append(" \"date\": \"" +new SimpleDateFormat("dd/MM/YY").format(p_requestVO.getCreatedOn()) + "\" ,");
				responseStr.append(" \"totalTrnxCount\": " + recentC2sRes +"}");
				responseMap.put("RESPONSE", responseStr);
				
				p_requestVO.setResponseMap(responseMap);
			}
			
			if(recentC2sRes < 0) {
				p_requestVO.setSuccessTxn(false);
				p_requestVO.setSenderReturnMessage("No Transaction exist for the given Date");
				p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_NO_TRNX_EXIST);
				return;
				
			}
			
			p_requestVO.setC2sTotaltxnCount(recentC2sRes);
			p_requestVO.setSuccessTxn(true);
			p_requestVO.setMessageCode(PretupsErrorCodesI.USER_HIERRACHY_SUCCESS);
			p_requestVO.setSenderReturnMessage("Transaction has been completed!");

				
		} catch (BTSLBaseException be) {
			p_requestVO.setSuccessTxn(false);
			_log.error(methodName, "BTSLBaseException " + be.getMessage());
            _log.errorTrace(methodName, be);
            if (be.isKey()) {
            	p_requestVO.setMessageCode(be.getMessageKey());
            	p_requestVO.setMessageArguments(be.getArgs());
            } else {
            	p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
                return;
            }
        } catch (Exception e) {
        	p_requestVO.setSuccessTxn(false);
        	_log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, PROCESS, "", "", "",
                "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        }
		
		finally {
            LogFactory.printLog(methodName, " Exited ", _log);
        }

	}

}
