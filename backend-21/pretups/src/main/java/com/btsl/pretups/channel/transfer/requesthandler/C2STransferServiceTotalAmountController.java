package com.btsl.pretups.channel.transfer.requesthandler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;


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
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.receiver.RestReceiver;
import com.btsl.pretups.channel.transfer.businesslogic.C2CTotalTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2CTotalTrfRequestMessage;
import com.btsl.pretups.channel.transfer.businesslogic.C2SserviceAmountRequestParentVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServicesTypeDAO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.util.PretupsBL;
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
@Tag(name = "${dummy.name}", description = "${dummy.desc}")//@Api(value="Channel User API for Total Service Data")
public class C2STransferServiceTotalAmountController implements ServiceKeywordControllerI {

	protected final Log _log = LogFactory.getLog(getClass().getName());
	Connection con = null;
	MComConnectionI mcomCon = null;
	@Context
 	private HttpServletRequest httpServletRequest;
 	@POST
 	@Path("/c2s-receiver/c2ssrvtrfcnt")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
 	//@ApiOperation(value = "Channel User Service ", response = PretupsResponse.class)

	@io.swagger.v3.oas.annotations.Operation(summary = "${c2ssrvtrfcnt.summary}", description="${c2ssrvtrfcnt.description}",

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



	public PretupsResponse<JsonNode> processCP2PUserRequest(@Parameter(description = SwaggerAPIDescriptionI.C2S_SRV_CNT)C2SserviceAmountRequestParentVO c2SserviceAmountRequestParentVO) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
 		PretupsResponse<JsonNode> response;
         RestReceiver restReceiver;
         RestReceiver.updateRequestIdChannel();
         final String requestIDStr = String.valueOf(RestReceiver.getRequestIdChannel());
         restReceiver = new RestReceiver();
         response = restReceiver.processCP2PRequestOperator(httpServletRequest,(JsonNode)(PretupsRestUtil.convertJSONToObject(PretupsRestUtil.convertObjectToJSONString(c2SserviceAmountRequestParentVO), new TypeReference<JsonNode>(){})), PretupsI.SERVICE_TYPE_C2STRFSVCNT,requestIDStr);
         return response;
     }
 	
	private ArrayList<C2CTotalTransferVO> c2ctrftxn( Date currFrom, Date currTo, Date prevFrom, Date prevTo, String userId) throws BTSLBaseException, Exception {

		final String methodName = "c2ctrftxn";
		UserDAO userDao = new UserDAO();
		ArrayList<C2CTotalTransferVO> C2CTotalTransactionList = new ArrayList<C2CTotalTransferVO> ();
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			Map<String,String> c2cTrfCurrMap = new LinkedHashMap<String,String>();
			c2cTrfCurrMap = userDao.c2ctottrftxn(con,userId,currFrom,currTo);
			Map<String,String> c2cTrfPrevMap = new LinkedHashMap<String,String>();
			c2cTrfPrevMap = userDao.c2ctottrftxn(con,userId,prevFrom,prevTo);
			
			Map<String,C2CTotalTransferVO> c2cTotalTrfMap = new LinkedHashMap<String,C2CTotalTransferVO>();
			
			ServicesTypeDAO servicesDAO = new ServicesTypeDAO();
			ArrayList serviceList = servicesDAO.loadUserServicesList(con, userId);
			for(int i = 0; i<serviceList.size();i++){
				ListValueVO listValueVo  = (ListValueVO)(serviceList.get(i));
				String key = listValueVo.getValue();
				C2CTotalTransferVO  c2CTotalTransferVO = new C2CTotalTransferVO();
        		c2CTotalTransferVO.setServiceName(listValueVo.getLabel());
        		c2CTotalTransferVO.setServiceType(listValueVo.getValue());
        		c2CTotalTransferVO.setCurrentValue("0");
        		c2CTotalTransferVO.setPreviousValue("0");
        		c2CTotalTransferVO.setCurrentFrom(BTSLUtil.getDateStringFromDate(currFrom));
        		c2CTotalTransferVO.setCurrentTo(BTSLUtil.getDateStringFromDate(currTo));
        		c2CTotalTransferVO.setPreviousFrom(BTSLUtil.getDateStringFromDate(prevFrom));
        		c2CTotalTransferVO.setPreviousTo(BTSLUtil.getDateStringFromDate(prevTo));
        			
        		if(c2cTrfCurrMap.containsKey(key))
        		{	
        			c2CTotalTransferVO.setCurrentValue(PretupsBL.getDisplayAmount(Long.parseLong(c2cTrfCurrMap.get(key))));
        		}
        		if(c2cTrfPrevMap.containsKey(key))
        		{	
        			c2CTotalTransferVO.setPreviousValue(PretupsBL.getDisplayAmount(Long.parseLong(c2cTrfPrevMap.get(key))));
        		}
        		c2cTotalTrfMap.put(key, c2CTotalTransferVO);
			}
			for (Entry<String, C2CTotalTransferVO> entry : c2cTotalTrfMap.entrySet())
			{
				C2CTotalTransactionList.add(entry.getValue());
			}
		} catch (Exception e) {
			throw new BTSLBaseException(this, methodName, "Could not create Database connection", "Exception " + e);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("C2CVoucherApprovalController#saveVoucherProductDetalis");
				mcomCon = null;
			}
		}
		return C2CTotalTransactionList;

	}

	
	
	
	
	@Override
	public void process(RequestVO p_requestVO) {
		
		
		final String methodName = "process";
		
	
		PretupsResponse<JsonNode> jsonReponse = new PretupsResponse<JsonNode>();;
		JsonNode dataObject = null;
		HashMap responseMap = new HashMap();
		Gson gson = new Gson();
		C2CTotalTransferVO c2CTotalTransferVO = new C2CTotalTransferVO();
		C2CTotalTrfRequestMessage reqMsgObj = null;
		ArrayList<C2CTotalTransferVO> recentC2cRes = null;
		StringBuffer responseStr = new StringBuffer("");
		try {
			HashMap reqMap = p_requestVO.getRequestMap();
			String userId = p_requestVO.getActiverUserId();
			if (reqMap != null && (reqMap.get("MSISDN") != null || reqMap.get("LOGINID") != null) && p_requestVO.getRequestMessage() != null  &&  !p_requestVO.getRequestMessage().trim().startsWith("{")) {
				String fromDate = (String) reqMap.get("FROMDATE");
				String toDate = (String) reqMap.get("TODATE");
				Date date = new Date();
				String tDate = BTSLUtil.getDateStringFromDate(date);
				boolean errorCheck = BTSLDateUtil.checkDate(p_requestVO, fromDate, toDate);
				if(errorCheck)
					return;
				if(tDate.equals(fromDate)){
					p_requestVO.setSuccessTxn(false);
					p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_FROMDATE_EQUAL_CURRENTDATE);
					return;
				}
				else if(tDate.equals(toDate)){
					p_requestVO.setSuccessTxn(false);
					p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_TODATE_EQUAL_CURRENTDATE);
					return;
				}
				Date currFrom = BTSLUtil.getDateFromDateString((String) reqMap.get("FROMDATE"));
				Date previousFrom = BTSLUtil.getDateOneMonthBeforeFromDate(currFrom);
				Date currTo = BTSLUtil.getDateFromDateString((String) reqMap.get("TODATE"));
				Date previousTo = BTSLUtil.getDateOneMonthBeforeToDate(currTo);
				recentC2cRes = c2ctrftxn(currFrom,currTo,previousFrom,previousTo,userId);
				
				if ("MAPPGW".equalsIgnoreCase(p_requestVO.getRequestGatewayCode())){
					
					
					String resType = null;
					resType = reqMap.get("TYPE") + "RES";
					responseStr.append("{ \"type\": \"" + resType + "\" ,");
					responseStr.append(" \"txnStatus\": \"" + PretupsI.TXN_STATUS_SUCCESS + "\" ,");
					responseStr.append(" \"c2sServiceDetails\": [");
				}
				int i = 0;
				for (C2CTotalTransferVO resObj : recentC2cRes) {
				
				if (PretupsI.MOBILE_APP_GATEWAY.equalsIgnoreCase(p_requestVO.getRequestGatewayCode())) {
					
					i++;
					responseStr.append("{ \"serviceType\": \"" + resObj.getServiceType() + "\" ,");
					responseStr.append("\"serviceName\": \"" + resObj.getServiceName() + "\" ,");
					responseStr.append("\"currentFrom\":   \"" + resObj.getCurrentFrom() + "\" , ");
					responseStr.append(" \"currentTo\":    \"" + resObj.getCurrentTo() + "\" , ");
					responseStr.append(" \"currentValue\":   \"" + resObj.getCurrentValue() + "\" , ");
					responseStr.append(" \"previousFrom\":   \"" + resObj.getPreviousFrom() + "\" , ");
					responseStr.append("   \"previousTo\":  \"" + resObj.getPreviousTo() + "\",");
					if(i == recentC2cRes.size()){
						responseStr.append("   \"previousValue\":  \"" + resObj.getPreviousValue() + "\" }");
					}
					else{
					responseStr.append("   \"previousValue\":  \"" + resObj.getPreviousValue() + "\" },");
					}

				} 
				else {
					responseStr.append("<C2SSERVICEDEATILS>");
					responseStr.append("<SERVICETYPE>" + resObj.getServiceType() + "</SERVICETYPE>");
					responseStr.append("<SERVICENAME>" + resObj.getServiceName() + "</SERVICENAME>");
					responseStr.append("<CURRENTFROM>" + resObj.getCurrentFrom() + "</CURRENTFROM>");
					responseStr.append("<CURRENTTO>" + resObj.getCurrentTo() + "</CURRENTTO>");
					responseStr.append("<CURRENTVALUE>" + resObj.getCurrentValue() + "</CURRENTVALUE>");
					responseStr.append("<PREVIOUSFROM>" + resObj.getPreviousFrom() + "</PREVIOUSFROM>");
					responseStr.append("<PREVIOUSTO>" + resObj.getPreviousTo() + "</PREVIOUSTO>");
					responseStr.append("<PREVIOUSVALUE>" + resObj.getPreviousValue() + "</PREVIOUSVALUE>");
					responseStr.append("</C2SSERVICEDEATILS>");
				}
			}
			if (PretupsI.MOBILE_APP_GATEWAY.equalsIgnoreCase(p_requestVO.getRequestGatewayCode())){
				responseStr.append("]}");
			}
			_log.debug("response ", "C2C Recent User responseStr  " + responseStr);

			responseMap.put("RESPONSE", responseStr);
			
			p_requestVO.setResponseMap(responseMap);
			}
			
	
			else{

				reqMsgObj = gson.fromJson(p_requestVO.getRequestMessage(), C2CTotalTrfRequestMessage.class);
				c2CTotalTransferVO.setCurrentFrom(reqMsgObj.getFromdate());
				c2CTotalTransferVO.setCurrentTo(reqMsgObj.getToDate());
				String fromDate = reqMsgObj.getFromdate();
				String toDate = reqMsgObj.getToDate();
				String tDate = BTSLUtil.getDateStringFromDate(new Date());
				boolean errorCheck = BTSLDateUtil.checkDate(p_requestVO, fromDate, toDate);
				if(errorCheck)
					return;
				if(tDate.equals(fromDate)){
					p_requestVO.setSuccessTxn(false);
					p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_FROMDATE_EQUAL_CURRENTDATE);
					return;
				}
				else if(tDate.equals(toDate)){
					p_requestVO.setSuccessTxn(false);
					p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_TODATE_EQUAL_CURRENTDATE);
					return;
				}
				Date currFrom = BTSLUtil.getDateFromDateString(reqMsgObj.getFromdate());
				Date previousFrom = BTSLUtil.getDateOneMonthBeforeFromDate(currFrom);
				
				Date currTo = BTSLUtil.getDateFromDateString(reqMsgObj.getToDate());
				Date previousTo = BTSLUtil.getDateOneMonthBeforeToDate(currTo);
				
				
				
				recentC2cRes = c2ctrftxn(currFrom,currTo,previousFrom,previousTo,userId);
				dataObject = (JsonNode) PretupsRestUtil.convertJSONToObject(
						PretupsRestUtil.convertObjectToJSONString(recentC2cRes), new TypeReference<JsonNode>() {
						});

				jsonReponse.setDataObject(dataObject);

				p_requestVO.setJsonReponse(jsonReponse);

			} 
			p_requestVO.setSuccessTxn(true);
			p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_TRANSFER_SUCCESS);
			p_requestVO.setSenderReturnMessage("Enquiry has been successfully done!");
		}

	

		catch (BTSLBaseException be) {
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
			}

			catch (SQLException esql) {
				_log.error(methodName, "SQLException : ", esql.getMessage());
			}
			_log.error("process", "BTSLBaseException " + be.getMessage());
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
			_log.errorTrace(methodName, be);
			return;

		} catch (Exception e) {

			
			
			if ("MAPPGW".equalsIgnoreCase(p_requestVO.getRequestGatewayCode())) {
				responseStr.append("Exception occured - " + e);
				responseMap.put("RESPONSE", responseStr);

				p_requestVO.setResponseMap(responseMap);

			} 

			
			p_requestVO.setSenderReturnMessage("Enquiry is not successful!");
			p_requestVO.setSuccessTxn(false);

			_log.error(methodName, "Exception " + e);
			_log.errorTrace(methodName, e);
			p_requestVO.setSuccessTxn(false);
			try {
				if (mcomCon != null) {
					mcomCon.finalRollback();
				}
			}

			catch (SQLException esql) {
				_log.error(methodName, "SQLException : ", esql.getMessage());
			}
			_log.error("process", "BTSLBaseException " + e.getMessage());
			_log.errorTrace(methodName, e);
			
			p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_TRANSFER_FAIL);

			return;
		}
		finally {
			if (mcomCon != null) {
				mcomCon.close("C2STransferServiceTotalAmountController#process");
				mcomCon = null;
			}
			p_requestVO.setSenderReturnMessage(BTSLUtil.getMessage(p_requestVO.getLocale(),p_requestVO.getMessageCode(),p_requestVO.getMessageArguments()));
			if (_log.isDebugEnabled()) {
				_log.debug("process", " Exited ");
			}
		} 

	}

}

