package com.btsl.pretups.channel.transfer.requesthandler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
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
import com.btsl.common.ListValueVO;
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
import com.btsl.pretups.channel.transfer.businesslogic.AllTransactionRequest;
import com.btsl.pretups.channel.transfer.businesslogic.C2SNProdTxnDetailsRequestParentVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransactionDetails;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransactionRequestParentVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.C2SnProdTxnRequest;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCacheVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.btsl.util.XMLTagValueValidation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.txn.pretups.channel.transfer.businesslogic.C2STransferTxnDAO;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;


@Path("")
@Tag(name = "${dummy.name}", description = "${dummy.desc}")//@Api(value="Transactional Data")
public class C2STransactionController  implements ServiceKeywordControllerI{
	private static Log log = LogFactory.getLog(C2STransactionController.class.getName());
	@Context
 	private HttpServletRequest httpServletRequest;
 	@POST
 	@Path("/c2s-receiver/c2sprodtxndetails")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
 	//@ApiOperation(value = "Channel User Service ", response = PretupsResponse.class)


	@io.swagger.v3.oas.annotations.Operation(summary = "${c2sprodtxndetails.summary}", description="${c2sprodtxndetails.description}",

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



	public PretupsResponse<JsonNode> processCP2PUserRequest(@Parameter(description = SwaggerAPIDescriptionI.C2S_PROD_TXN_DETAILS)C2STransactionRequestParentVO c2STransactionRequestParentVO) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
 		PretupsResponse<JsonNode> response;
         RestReceiver restReceiver;
         RestReceiver.updateRequestIdChannel();
         final String requestIDStr = String.valueOf(RestReceiver.getRequestIdChannel());
         restReceiver = new RestReceiver();
         response = restReceiver.processCP2PRequestOperator(httpServletRequest,(JsonNode)(PretupsRestUtil.convertJSONToObject(PretupsRestUtil.convertObjectToJSONString(c2STransactionRequestParentVO), new TypeReference<JsonNode>(){})), PretupsI.SERVICE_TYPE_C2S_PROD_TXN,requestIDStr);
         return response;
     }
 	
 	@POST
 	@Path("/c2s-receiver/c2snprodtxndetails")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
 	//@ApiOperation(value = "Channel User Service ", response = PretupsResponse.class)

	@io.swagger.v3.oas.annotations.Operation(summary = "${c2snprodtxndetails.summary}", description="${c2snprodtxndetails.description}",

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



	public PretupsResponse<JsonNode> processC2SDetailsRequest(@Parameter(description = SwaggerAPIDescriptionI.C2S_N_PROD_TXN_DETAILS)C2SNProdTxnDetailsRequestParentVO c2SNProdTxnDetailsRequestParentVO) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
 		PretupsResponse<JsonNode> response;
         RestReceiver restReceiver;
         RestReceiver.updateRequestIdChannel();
         final String requestIDStr = String.valueOf(RestReceiver.getRequestIdChannel());
         restReceiver = new RestReceiver();
         response = restReceiver.processCP2PRequestOperator(httpServletRequest,(JsonNode)(PretupsRestUtil.convertJSONToObject(PretupsRestUtil.convertObjectToJSONString(c2SNProdTxnDetailsRequestParentVO), new TypeReference<JsonNode>(){})), PretupsI.SERVICE_TYPE_C2S_N_PROD_TXN,requestIDStr);
         return response;
     }
	
 	public void process(RequestVO p_requestVO) {
		final String methodName = "process";
		if (log.isDebugEnabled()) {
			log.debug("process", "Entered p_requestVO: " + p_requestVO);
		}
		String serviceType="";
		final ChannelUserVO senderVO = (ChannelUserVO) p_requestVO.getSenderVO();
		if (log.isDebugEnabled()) {
			log.debug("process", "Entered Sender VO: " + senderVO);
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		C2STransferDAO c2sTransferDAO = new C2STransferDAO();
		C2STransferTxnDAO c2STransferTxnDAO = new C2STransferTxnDAO();
		Gson gson = new Gson();
		ArrayList<C2STransactionDetails> c2sTransactionList = new ArrayList<C2STransactionDetails>();
		HashMap<String,Object> responseMap = new HashMap<String,Object>();
		StringBuffer responseStr = new StringBuffer("");
		String serviceKeyword="";
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			HashMap reqMap = p_requestVO.getRequestMap();
			String userId = p_requestVO.getActiverUserId();
			serviceKeyword = ((String) reqMap.get("TYPE")).toUpperCase();
			String fromDate = "";
			String toDate = "";
			String topProducts="";
			String value = "";
			if (reqMap != null && (reqMap.get("MSISDN") != null || reqMap.get("LOGINID") != null) && p_requestVO.getRequestMessage() != null  &&  !p_requestVO.getRequestMessage().trim().startsWith("{")) {
				fromDate = (String) reqMap.get("FROMDATE");
			    toDate = (String) reqMap.get("TODATE");
			    serviceType = (String)reqMap.get("SERVICETYPE");
				if (PretupsI.SERVICE_TYPE_C2S_N_PROD_TXN.equals(serviceKeyword)) {
					 topProducts=(String) reqMap.get("TOPPRODUCTS");
					 value = (String) reqMap.get("NUMBEROFPRODORDENO");
				}
				validateData(serviceType, senderVO.getAssociatedServiceTypeList(),fromDate,toDate,serviceKeyword,topProducts,value);
				
				Date currFrom = BTSLUtil.getDateFromDateString((String) reqMap.get("FROMDATE"));
				Date previousFrom = BTSLUtil.getDateOneMonthBeforeFromDate(currFrom);
				Date currTo = BTSLUtil.getDateFromDateString((String) reqMap.get("TODATE"));
				Date previousTo = BTSLUtil.getDateOneMonthBeforeToDate(currTo);
				
            	HashMap<String, Object> currentData = null;
				HashMap<String, Object> previousData = null;
				if(PretupsI.SERVICE_TYPE_C2S_PROD_TXN.equals(serviceKeyword)){
					currentData  = c2sTransferDAO.getC2STxnDetailsAllCount(con, userId, currFrom, currTo, serviceType);
					previousData = c2sTransferDAO.getC2STxnDetailsAllCount(con, userId, previousFrom, previousTo, serviceType);
					c2sTransactionList = c2sTransferDAO.getC2STxnDetailsAll(con, userId, currFrom, currTo,serviceType);
				}
				else if (PretupsI.SERVICE_TYPE_C2S_N_PROD_TXN.equals(serviceKeyword)) {
					 topProducts=(String) reqMap.get("TOPPRODUCTS");
					 value = (String) reqMap.get("NUMBEROFPRODORDENO");
					if(topProducts.equals(PretupsI.TOP_PRODUCTS)){
						 currentData = c2STransferTxnDAO.getC2SnProdTxnDetails(con, userId, currFrom, currTo,serviceType,value);
						 previousData = c2STransferTxnDAO.getC2SnProdTxnDetails(con, userId, previousFrom, previousTo,serviceType,value);
						 StringBuffer amount = new StringBuffer();
						 ArrayList<C2STransactionDetails>  data = (ArrayList<C2STransactionDetails>) currentData.get("data");
						 for(C2STransactionDetails obj : data){
							 if(!BTSLUtil.isNullString(amount.toString())){
								 amount.append(",");
								 amount.append(PretupsBL.getSystemAmount(obj.getAmount()));
							 }else{
								 amount.append(PretupsBL.getSystemAmount(obj.getAmount()));
							 }
						 }
						 c2sTransactionList = c2STransferTxnDAO.getC2STxnDetailsByAmt(con, userId, currFrom, currTo,serviceType,amount.toString());
					}
					else{
						String amount = String.valueOf(PretupsBL.getSystemAmount(value));
						c2sTransactionList = c2STransferTxnDAO.getC2STxnDetailsByAmt(con, userId, currFrom, currTo,serviceType,amount);
						currentData = c2STransferTxnDAO.getC2SnProdTxnDetailsCount(con, userId, currFrom, currTo, serviceType,amount);
						previousData = c2STransferTxnDAO.getC2SnProdTxnDetailsCount(con, userId, previousFrom, previousTo, serviceType,amount);

					}
				}
				
				if (PretupsI.MOBILE_APP_GATEWAY.equalsIgnoreCase(p_requestVO.getRequestGatewayCode())){
					String resType = null;
					resType = reqMap.get("TYPE") + "RES";
					responseStr.append("{ \"type\": \"" + resType + "\" ,");
				}
				responseStr.append(generateResponseForUssdMappgw(currentData, previousData, c2sTransactionList, p_requestVO.getRequestGatewayCode(),topProducts,serviceKeyword));
				log.debug("response ", "transactional data responseStr  " + responseStr);
				responseMap.put("RESPONSE", responseStr);
				p_requestVO.setResponseMap(responseMap);
			}
			else{
				if(PretupsI.SERVICE_TYPE_C2S_PROD_TXN.equals(serviceKeyword)){
					AllTransactionRequest reqMsgObj = gson.fromJson(p_requestVO.getRequestMessage(), AllTransactionRequest.class);
					serviceType=reqMsgObj.getServiceType();
					fromDate = reqMsgObj.getFromDate();
					toDate = reqMsgObj.getToDate();
				}
				else if (PretupsI.SERVICE_TYPE_C2S_N_PROD_TXN.equals(serviceKeyword)) {
					C2SnProdTxnRequest reqMsgObj = gson.fromJson(p_requestVO.getRequestMessage(), C2SnProdTxnRequest.class);
					serviceType=reqMsgObj.getServiceType();
				    fromDate = reqMsgObj.getFromDate();
					toDate = reqMsgObj.getToDate();
					value = reqMsgObj.getNumberOfProdOrDeno();
					topProducts = reqMsgObj.getTopProducts();
				}
				
				validateData(serviceType, senderVO.getAssociatedServiceTypeList(),fromDate,toDate,serviceKeyword,topProducts,value);
				Date currFrom = BTSLUtil.getDateFromDateString(fromDate);
				Date previousFrom = BTSLUtil.getDateOneMonthBeforeFromDate(currFrom);
				Date currTo = BTSLUtil.getDateFromDateString(toDate);
				Date previousTo = BTSLUtil.getDateOneMonthBeforeToDate(currTo);
				HashMap<String, Object> currentData = null;
				HashMap<String, Object> previousData = null;
				if(PretupsI.SERVICE_TYPE_C2S_PROD_TXN.equals(serviceKeyword)){
					currentData  = c2sTransferDAO.getC2STxnDetailsAllCount(con, userId, currFrom, currTo, serviceType);
					previousData = c2sTransferDAO.getC2STxnDetailsAllCount(con, userId, previousFrom, previousTo, serviceType);
					c2sTransactionList = c2sTransferDAO.getC2STxnDetailsAll(con, userId, currFrom, currTo,serviceType);
				}
				else if (PretupsI.SERVICE_TYPE_C2S_N_PROD_TXN.equals(serviceKeyword)) {
					if(topProducts.equals(PretupsI.TOP_PRODUCTS)){
						 currentData = c2STransferTxnDAO.getC2SnProdTxnDetails(con, userId, currFrom, currTo,serviceType,value);
						 previousData = c2STransferTxnDAO.getC2SnProdTxnDetails(con, userId, previousFrom, previousTo,serviceType,value);
						 StringBuffer amount = new StringBuffer();
						 ArrayList<C2STransactionDetails>  data = (ArrayList<C2STransactionDetails>) currentData.get("data");
						 for(C2STransactionDetails obj : data){
							 if(!BTSLUtil.isNullString(amount.toString())){
								 amount.append(",");
								 amount.append(PretupsBL.getSystemAmount(obj.getAmount()));
							 }else{
								 amount.append(PretupsBL.getSystemAmount(obj.getAmount()));
							 }
						 }
						 c2sTransactionList = c2STransferTxnDAO.getC2STxnDetailsByAmt(con, userId, currFrom, currTo,serviceType,amount.toString());
					}
					else{
						String amount = String.valueOf(PretupsBL.getSystemAmount(value));
						c2sTransactionList = c2STransferTxnDAO.getC2STxnDetailsByAmt(con, userId, currFrom, currTo,serviceType,amount);
						currentData = c2STransferTxnDAO.getC2SnProdTxnDetailsCount(con, userId, currFrom, currTo, serviceType,amount);
						previousData = c2STransferTxnDAO.getC2SnProdTxnDetailsCount(con, userId, previousFrom, previousTo, serviceType,amount);
					}
				}
				responseMap.put("currentData", currentData);
				responseMap.put("previousData", previousData);
				responseMap.put("transferList",c2sTransactionList);
				p_requestVO.setResponseMap(responseMap);
			}
			p_requestVO.setSuccessTxn(true);
			p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_TRANSFER_SUCCESS);
			p_requestVO.setSenderReturnMessage("Enquiry has been successfully done!");
		} catch (BTSLBaseException be) {
			p_requestVO.setSuccessTxn(false);
			p_requestVO.setSenderReturnMessage("Enquiry is not successful!");
			if ("MAPPGW".equalsIgnoreCase(p_requestVO.getRequestGatewayCode())) {
				String resType = null;
				HashMap reqMap = p_requestVO.getRequestMap();
				resType = reqMap.get("TYPE") + "RES";
				responseStr.append("{ \"Type\": \"" + resType + "\" ,");
				responseStr.append(" \"TxnStatus\": \"" + PretupsI.TXN_STATUS_FAIL + "\" ,");
				responseMap.put("RESPONSE", responseStr);

				p_requestVO.setResponseMap(responseMap);

			} 
			try {
				if (mcomCon != null) {
					mcomCon.finalRollback();
				}
			}catch (SQLException esql) {
				log.error(methodName,"SQLException : ", esql.getMessage());
			}
			log.error("process", "BTSLBaseException " + be.getMessage());
			if (be.getMessageList() != null && be.getMessageList().size() > 0) {
				final String[] array = {
						BTSLUtil.getMessage(p_requestVO.getLocale(), (ArrayList) be.getMessageList()) };
				p_requestVO.setMessageArguments(array);
			}
			if (be.getArgs() != null) {
				p_requestVO.setMessageArguments(be.getArgs());
			}

			if (be.getMessageKey() != null) {
				p_requestVO.setMessageCode(be.getMessageKey());
			} else {
				p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER);
			}
			log.errorTrace(methodName, be);
			return;
		} catch (Exception e) {
			log.error(methodName, "Exception " + e);
			log.errorTrace(methodName, e);
			p_requestVO.setSuccessTxn(false);
			p_requestVO.setSenderReturnMessage("Enquiry is not successful!");
			if ("MAPPGW".equalsIgnoreCase(p_requestVO.getRequestGatewayCode())) {
				responseStr.append("Exception occured - " + e);
				responseMap.put("RESPONSE", responseStr);

				p_requestVO.setResponseMap(responseMap);

			} 

			try {
				if (mcomCon != null) {
					mcomCon.finalRollback();
				}
			} 
			
			catch (SQLException esql) {
				log.error(methodName,"SQLException : ", esql.getMessage());
			}
			log.error(methodName, "BTSLBaseException " + e.getMessage());
			log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"C2STransactionController[process]", "", "", "", "Exception:" + e.getMessage());
			p_requestVO.setMessageCode(e.getMessage());
			return;
		} finally {
			if (mcomCon != null) {
				mcomCon.close("C2STransactionController#process");
				mcomCon = null;
			}
			p_requestVO.setSenderReturnMessage(BTSLUtil.getMessage(p_requestVO.getLocale(),p_requestVO.getMessageCode(),p_requestVO.getMessageArguments()));
			if (log.isDebugEnabled()) {
				log.debug("process", " Exited ");
			}
		} // end of finally
	}
	
	/**
	 * @param currentData
	 * @param previousData
	 * @param c2sTransactionList
	 * @param gateway
	 * @param topProducts
	 * @return
	 * @throws ParseException
	 */
	public StringBuffer generateResponseForUssdMappgw(HashMap<String,Object> currentData,HashMap<String,Object> previousData,ArrayList<C2STransactionDetails> c2sTransactionList,String gateway, String topProducts ,String serviceKeyword ) throws ParseException{
	 StringBuffer responseStr = new StringBuffer();
	 StringBuffer amount = new StringBuffer();
	 ArrayList<C2STransactionDetails>  data = (ArrayList<C2STransactionDetails>) currentData.get("data");
		if (PretupsI.MOBILE_APP_GATEWAY.equalsIgnoreCase(gateway)){
			responseStr.append(" \"txnStatus\": \"" + PretupsI.TXN_STATUS_SUCCESS + "\" ,");
			responseStr.append(" \"currentData\": {");
			responseStr.append(" \"fromDate\": \"" + BTSLUtil.getDateStringFromDate((Date)currentData.get("fromDate")) + "\" ,");
			responseStr.append(" \"toDate\": \"" + BTSLUtil.getDateStringFromDate((Date)currentData.get("toDate")) + "\" ,");
			responseStr.append(" \"totalCount\": \"" + currentData.get("totalCount") + "\" ,");
			responseStr.append(" \"totalValue\": \"" + currentData.get("totalValue") + "\"" );
		}else{
			responseStr.append("<CURRENTDATA>");
			responseStr.append("<FROMDATE>" + BTSLUtil.getDateStringFromDate((Date)currentData.get("fromDate")) + "</FROMDATE>");
			responseStr.append("<TODATE>" + BTSLUtil.getDateStringFromDate((Date)currentData.get("toDate")) + "</TODATE>");
			responseStr.append("<TOTALCOUNT>" + currentData.get("totalCount") + "</TOTALCOUNT>");
			responseStr.append("<TOTALVALUE>" + currentData.get("totalValue")  + "</TOTALVALUE>");
		}
		int i =0;
		if(PretupsI.SERVICE_TYPE_C2S_N_PROD_TXN.equals(serviceKeyword)){
		if(topProducts.equals(PretupsI.TOP_PRODUCTS)){
			ArrayList<C2STransactionDetails> data1 = (ArrayList<C2STransactionDetails>) currentData.get("data");
			if (PretupsI.MOBILE_APP_GATEWAY.equalsIgnoreCase(gateway)) {
				responseStr.append(" , \"data\": [");
			}else{
				responseStr.append("<DATA>");
			}
			for (C2STransactionDetails resObj : data1) {
			if (PretupsI.MOBILE_APP_GATEWAY.equalsIgnoreCase(gateway)) {
				i++;
				responseStr.append("{ \"amount\": \"" + resObj.getAmount() + "\" ,");
				responseStr.append("\"transferCount\": \"" + resObj.getTransferCount() + "\" ,");
				if(i == data1.size()){
					responseStr.append("   \"transferValue\":  \"" + resObj.getTransferValue() + "\" }");
				}
				else{
				responseStr.append("   \"transferValue\":  \"" + resObj.getTransferValue() + "\" },");
				}
			} 
			else {
				responseStr.append("<TRANSACTIONS>");
				responseStr.append("<AMOUNT>" + resObj.getAmount() + "</AMOUNT>");
				responseStr.append("<TRANSFERCOUNT>" + resObj.getTransferCount() + "</TRANSFERCOUNT>");
				responseStr.append("<TRANSFERVALUE>" + resObj.getTransferValue() + "</TRANSFERVALUE>");
				responseStr.append("</TRANSACTIONS>");
				}
			}
		  	if (PretupsI.MOBILE_APP_GATEWAY.equalsIgnoreCase(gateway)) {
				responseStr.append("]");
			}else{
				responseStr.append("</DATA>");
			}
		}
		}
		if (PretupsI.MOBILE_APP_GATEWAY.equalsIgnoreCase(gateway)){
			responseStr.append("},");
			responseStr.append(" \"previousData\": {");
			responseStr.append(" \"fromDate\": \"" +  BTSLUtil.getDateStringFromDate((Date)previousData.get("fromDate")) + "\" ,");
			responseStr.append(" \"toDate\": \"" + BTSLUtil.getDateStringFromDate((Date)previousData.get("toDate")) + "\" ,");
			responseStr.append(" \"totalCount\": \"" + previousData.get("totalCount") + "\" ,");
			responseStr.append(" \"totalValue\": \"" + previousData.get("totalValue")+ "\"" );
		}else{
			responseStr.append("</CURRENTDATA>");
			responseStr.append("<PREVIOUSDATA>");
			responseStr.append("<FROMDATE>" +  BTSLUtil.getDateStringFromDate((Date)previousData.get("fromDate"))+ "</FROMDATE>");
			responseStr.append("<TODATE>" +  BTSLUtil.getDateStringFromDate((Date)previousData.get("toDate")) + "</TODATE>");
			responseStr.append("<TOTALCOUNT>" + previousData.get("totalCount") + "</TOTALCOUNT>");
			responseStr.append("<TOTALVALUE>" + previousData.get("totalValue")  + "</TOTALVALUE>");
		}
		if(PretupsI.SERVICE_TYPE_C2S_N_PROD_TXN.equals(serviceKeyword)){
		if(topProducts.equals(PretupsI.TOP_PRODUCTS)){
			 ArrayList<C2STransactionDetails> data2 = (ArrayList<C2STransactionDetails>) previousData.get("data");
			 if (PretupsI.MOBILE_APP_GATEWAY.equalsIgnoreCase(gateway)) {
				responseStr.append(" , \"data\": [");
			}else{
				responseStr.append("<DATA>");
			}
			 i=0;
			for (C2STransactionDetails resObj : data2) {
			if (PretupsI.MOBILE_APP_GATEWAY.equalsIgnoreCase(gateway)) {
				i++;
				responseStr.append("{ \"amount\": \"" + resObj.getAmount() + "\" ,");
				responseStr.append("\"transferCount\": \"" + resObj.getTransferCount() + "\" ,");
				if(i == data2.size()){
					responseStr.append("   \"transferValue\":  \"" + resObj.getTransferValue() + "\" }");
				}
				else{
				responseStr.append("   \"transferValue\":  \"" + resObj.getTransferValue() + "\" },");
				}
			} 
			else {
				responseStr.append("<TRANSACTIONS>");
				responseStr.append("<AMOUNT>" + resObj.getAmount() + "</AMOUNT>");
				responseStr.append("<TRANSFERCOUNT>" + resObj.getTransferCount() + "</TRANSFERCOUNT>");
				responseStr.append("<TRANSFERVALUE>" + resObj.getTransferValue() + "</TRANSFERVALUE>");
				responseStr.append("</TRANSACTIONS>");
				}
			}
			if (PretupsI.MOBILE_APP_GATEWAY.equalsIgnoreCase(gateway)) {
				responseStr.append("]");
			}else{
				responseStr.append("</DATA>");
			}
		}
		}
		
		if (PretupsI.MOBILE_APP_GATEWAY.equalsIgnoreCase(gateway)){
			responseStr.append("},");
			responseStr.append(" \"transferList\": [");
		}else{
			responseStr.append("</PREVIOUSDATA>");
			responseStr.append("<TRANSFERLIST>");
		}
		
		 i = 0;
		for (C2STransactionDetails resObj : c2sTransactionList) {
		if (PretupsI.MOBILE_APP_GATEWAY.equalsIgnoreCase(gateway)) {
			i++;
			responseStr.append("{ \"transferdate\": \"" + BTSLUtil.getDateStringFromDate(resObj.getTransferdate()) + "\" ,");
			responseStr.append("\"transferCount\": \"" + resObj.getTransferCount() + "\" ,");
			if(i == c2sTransactionList.size()){
				responseStr.append("   \"transferValue\":  \"" + resObj.getTransferValue() + "\" }");
			}
			else{
			responseStr.append("   \"transferValue\":  \"" + resObj.getTransferValue() + "\" },");
			}
		} 
		else {
			responseStr.append("<TRANSACTIONDATA>");
			responseStr.append("<TRANSFERDATE>" + BTSLUtil.getDateStringFromDate(resObj.getTransferdate()) + "</TRANSFERDATE>");
			responseStr.append("<TRANSFERCOUNT>" + resObj.getTransferCount() + "</TRANSFERCOUNT>");
			responseStr.append("<TRANSFERVALUE>" + resObj.getTransferValue() + "</TRANSFERVALUE>");
			responseStr.append("</TRANSACTIONDATA>");
		}
	}
	if (PretupsI.MOBILE_APP_GATEWAY.equalsIgnoreCase(gateway)){
		responseStr.append("]}");
	}
	else{
		responseStr.append("</TRANSFERLIST>");
	}

		return responseStr;
	}
	
	
	/**
	 * @param serviceType
	 * @param associatedServiceTypeList
	 * @param fromDate
	 * @param toDate
	 * @param serviceKeyword
	 * @param topProducts
	 * @param value
	 * @throws BTSLBaseException
	 * @throws ParseException
	 */
	public void validateData(String serviceType,ArrayList associatedServiceTypeList,String fromDate,String toDate,String serviceKeyword,String topProducts,String value) throws BTSLBaseException, ParseException{
		 ServiceKeywordCacheVO serviceKeywordCacheVO = ServiceKeywordCache.getServiceTypeObject(serviceType, PretupsI.C2S_MODULE);
		 String arg[]={serviceType};
		 if(BTSLUtil.isNullObject(serviceKeywordCacheVO)){
			 throw new BTSLBaseException(PretupsErrorCodesI.SERVICE_TYPE_INVALID, arg);
		 }
		 final ListValueVO listValueVO = BTSLUtil.getOptionDesc(serviceType,associatedServiceTypeList);
         if (listValueVO == null || BTSLUtil.isNullString(listValueVO.getLabel())) {
        	 throw new BTSLBaseException(PretupsErrorCodesI.SERVICE_TYPE_NOT_ALLOWED, arg);
         } 
         
         String tDate = BTSLUtil.getDateStringFromDate(new Date());
		 BTSLDateUtil.validateDate(fromDate, toDate);
		 if(tDate.equals(fromDate)){
		 	throw new BTSLBaseException(PretupsErrorCodesI.C2S_ERROR_FROMDATE_EQUAL_CURRENTDATE);
		 }
		 else if(tDate.equals(toDate)){
			throw new BTSLBaseException(PretupsErrorCodesI.C2S_ERROR_TODATE_EQUAL_CURRENTDATE);
		 }
		 if(PretupsI.SERVICE_TYPE_C2S_N_PROD_TXN.equals(serviceKeyword)){
			 XMLTagValueValidation.validateTopProductFlag(topProducts, true, "TOPPRODUCTS");
			 XMLTagValueValidation.validateNoOfProd(value, true, "NUMBEROFPRODORDENO");
		 }
		}
}
