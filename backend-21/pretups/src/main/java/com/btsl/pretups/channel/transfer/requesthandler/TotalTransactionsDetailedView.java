package com.btsl.pretups.channel.transfer.requesthandler;

import java.io.IOException;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;


import jakarta.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.receiver.RestReceiver;
import com.btsl.pretups.channel.transfer.businesslogic.TotTrnxDetailMsg;
import com.btsl.pretups.channel.transfer.businesslogic.TotalTransactionsDetailedViewRequestVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.common.PretupsRestI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.TotalTransactionsDetailedViewResponseVO;
import com.btsl.user.businesslogic.UserDAO;
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
@Tag(name = "${dummy.name}", description = "${dummy.desc}")//@Api(value="Total Transactions Detailed View")
public class TotalTransactionsDetailedView implements ServiceKeywordControllerI{
	protected final Log _log = LogFactory.getLog(getClass().getName());
	@Context
 	private HttpServletRequest httpServletRequest;
 	@POST
 	@Path("/c2s-receiver/tottransdetail")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
 	/*@ApiOperation(value = "Total Transactions Detailed View", response = PretupsResponse.class)
 	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${tottransdetail.summary}", description="${tottransdetail.description}",

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



	public PretupsResponse<JsonNode> processCP2PUserRequest(@Parameter(description = SwaggerAPIDescriptionI.TOTAL_TRANSACTIONS_DETAILED_VIEW)TotalTransactionsDetailedViewRequestVO totalTransactionsDetailedViewRequestVO) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
 		
 		PretupsResponse<JsonNode> response;
        RestReceiver restReceiver;
        RestReceiver.updateRequestIdChannel();
        final String requestIDStr = String.valueOf(RestReceiver.getRequestIdChannel());
        restReceiver = new RestReceiver();
        response = restReceiver.processCP2PRequestOperator(httpServletRequest,(JsonNode)(PretupsRestUtil.convertJSONToObject(PretupsRestUtil.convertObjectToJSONString(totalTransactionsDetailedViewRequestVO), new TypeReference<JsonNode>(){})), PretupsRestI.TRANSACTIONDETAIL,requestIDStr);
        return response;

}
	@Override
	public void process(RequestVO p_requestVO) {
		final String methodName = "process";
		_log.debug(methodName, "entered");
	
		String msisdn = "";
		String msisdn2 = "";
		String loginId = "";
		String extCode = "";
		String fromRow="";
		String toRow="";
		String transactionId="";
		
		Connection con = null;
		TotTrnxDetailMsg reqMsgObj = null;
		ChannelUserVO channelUserVO = null;
        MComConnectionI mcomCon = null;
        UserDAO userDao = new UserDAO();
        Gson gson = new Gson();
		StringBuffer responseStr = new StringBuffer();
        ArrayList<TotalTransactionsDetailedViewResponseVO> responseList = new ArrayList<TotalTransactionsDetailedViewResponseVO>();
        HashMap responseMap = new HashMap<>();
		
		HashMap reqMap = p_requestVO.getRequestMap();
		try
		{
			if (reqMap != null && (reqMap.get("MSISDN") != null || reqMap.get("LOGINID") != null) ) 
			{
				
				msisdn = (String) reqMap.get("MSISDN");
				loginId = (String) reqMap.get("LOGINID");
				msisdn2=(String)reqMap.get("MSISDN2");
			}
			else if ("MAPPGW".equalsIgnoreCase(p_requestVO.getRequestGatewayCode()))
			{
				_log.debug(methodName, "getting msisdn in mobile gateway");
				
			}
			else
			{
				p_requestVO.setSenderReturnMessage("Msisdn or loginId is required.");
				p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_REQ_EITHER_MSISDN_LOGINID_REQ);
				return;
			}
			
			mcomCon = new MComConnection();
	        con=mcomCon.getConnection();
	        String fromDate = (String) reqMap.get("FROMDATE");
	        String toDate =  (String) reqMap.get("TODATE");
	        String status = (String)reqMap.get("STATUS");
	        transactionId= (String)reqMap.get("TRANSACTIONID");
	        fromRow=(String)reqMap.get("FROMROW");
	        toRow=(String)reqMap.get("TOROW");
	        
	        if("REST".equalsIgnoreCase(p_requestVO.getRequestGatewayCode())) {
	        	reqMsgObj = gson.fromJson(p_requestVO.getRequestMessage(), TotTrnxDetailMsg.class);
		         fromRow=reqMsgObj.getFromRow();
				 toRow=reqMsgObj.getToRow();
				 transactionId=reqMsgObj.getTransactionID();
	        }
	        

	        if(!"".equals(msisdn))
	        {
	        	channelUserVO = userDao.loadUserDetailsByMsisdn(con, msisdn);
	        }
	        else if(!"".equals(loginId))
	        {
	        	channelUserVO = userDao.loadAllUserDetailsByLoginID(con, loginId);
	        	msisdn=channelUserVO.getMsisdn();
	        }
	        else if(!"".equals(extCode)) {
	        	channelUserVO = userDao.loadAllUserDetailsByExternalCode(con, extCode);
	        	msisdn=channelUserVO.getMsisdn();
	        	
	        }
	        //checking for date format(dd/mm/yy)
	       
	        String patternDate="\\d{2}/\\d{2}/\\d{2}";
	        if (!fromDate.matches(patternDate)) {
	        	p_requestVO.setSuccessTxn(false);
				p_requestVO.setMessageCode(PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT);
				return;
	        }
	        if (!toDate.matches(patternDate)) {
	        	p_requestVO.setSuccessTxn(false);
				p_requestVO.setMessageCode(PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT);
				return;
	        }
	        
	        
			
	        
	        
	        Date frDate = new Date();
    		Date tDate = new Date();
    		Date currentDate = new Date();
	        
           
    		
    		SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DATESPACEHHMMSS);
            sdf.setLenient(false); // this is required else it will convert
            
            frDate = sdf.parse(fromDate+" 00:00:00");
            tDate = sdf.parse(toDate+" 23:59:59");
    		
            
            //validations
            if (BTSLUtil.getDifferenceInUtilDates(frDate, currentDate) < 0) 
			{	p_requestVO.setSuccessTxn(false);
				p_requestVO.setMessageCode(PretupsErrorCodesI.CCE_ERROR_FROMDATE_GREATER_THAN_CURRENTDATE);
				return;
			}
			if (BTSLUtil.getDifferenceInUtilDates(tDate, currentDate) < 0) 
			{	p_requestVO.setSuccessTxn(false);
				p_requestVO.setMessageCode(PretupsErrorCodesI.CCE_ERROR_TODATE_GREATER_THAN_CURRENTDATE);
				return;
			}
			if (BTSLUtil.getDifferenceInUtilDates(frDate, tDate) < 0) 
			{	p_requestVO.setSuccessTxn(false);
				p_requestVO.setMessageCode(PretupsErrorCodesI.CCE_ERROR_FROMDATE_GREATER_THAN_TODATE);
				return;
			}
			
			if(!status.equalsIgnoreCase("PASS") && !status.equalsIgnoreCase("FAIL") && !status.equalsIgnoreCase("ALL")){
				p_requestVO.setSuccessTxn(false);
				p_requestVO.setMessageCode(PretupsErrorCodesI.EXTSYS_REQ_STATUS_INVALID);
			
				return;
			}
			
			if(!BTSLUtil.isNullString(fromRow)&&!BTSLUtil.isNullString(toRow)) {
				if(!BTSLUtil.isNumeric(fromRow) || !BTSLUtil.isNumeric(toRow) ){
					p_requestVO.setSuccessTxn(false);
					p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_ROW_VALUES);
					return;
				}
				
				int fromRow1=Integer.parseInt(fromRow);
				int toRow1=Integer.parseInt(toRow);
				if(fromRow1>toRow1) {
					p_requestVO.setSuccessTxn(false);
					p_requestVO.setMessageCode(PretupsErrorCodesI.FROM_ROW_GREATER);
					return;
				}
				
				if(fromRow1<0 || toRow1 <0 ) {
					p_requestVO.setSuccessTxn(false);
					p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_ROW_VALUES);
					return;
				}
			
			}
			
			if(!BTSLUtil.isNullString(transactionId)&&BTSLUtil.isNullString(toRow)){
				p_requestVO.setSuccessTxn(false);
				p_requestVO.setMessageCode(PretupsErrorCodesI.PROVIDE_ROW_VALUES);
				return;
			}
			
			
			Date fromDate1=BTSLUtil.getDateFromDateString(fromDate);
        	Date toDate1=BTSLUtil.getDateFromDateString(toDate);
        	LinkedHashMap<String, Object> map=new LinkedHashMap<String, Object>() ;
        	//when transactionId and toRow is given
        	if(!BTSLUtil.isNullString(toRow)&& !BTSLUtil.isNullString(transactionId) && BTSLUtil.isNullString(fromRow)) {
        		int toRow1=Integer.parseInt(toRow);
        		if(status.equalsIgnoreCase("ALL")) { 
        		map = userDao.loadAllTranDetPagntranxid(con,msisdn, msisdn2,fromDate1,toDate1,status,p_requestVO,transactionId,toRow1);
        			}
        		else {map = userDao.loadTranDetPagntranxid(con, msisdn, msisdn2,fromDate1,toDate1,status,p_requestVO,transactionId,toRow1);
        			}
        		
        	}
        		//if  fromRow and toRow is provided
        	if(!BTSLUtil.isNullString(fromRow)&&!BTSLUtil.isNullString(toRow)){
        		int fromRow1=Integer.parseInt(fromRow);
				int toRow1=Integer.parseInt(toRow);
        		if(status.equalsIgnoreCase("ALL")) {
	        		map=userDao.loadAllTransDetPagn(con, msisdn, msisdn2,fromDate1,toDate1,p_requestVO,fromRow1,toRow1);
	        	}
	        	else {
	        		map = userDao.loadTranDetPagn(con,msisdn, msisdn2,fromDate1,toDate1,status,p_requestVO,fromRow1,toRow1);
	        		} 	  
        	}
        	//if fromRow toRow not provided it will get all transaction according to provided status
        	if(BTSLUtil.isNullString(fromRow)&&BTSLUtil.isNullString(toRow)){
        		
        		if(status.equalsIgnoreCase("ALL")) {
	        		map=userDao.loadAllTransactionDetails(con, msisdn, msisdn2,fromDate1,toDate1,p_requestVO);
	        	}
	        	else {
	        		map = userDao.loadTransactionDetails(con, msisdn, msisdn2,fromDate1,toDate1,status,p_requestVO);
	        		}
        		
        		
        	}
        	responseList = (ArrayList<TotalTransactionsDetailedViewResponseVO>)map.get("data");    	
        	HashMap<String, Object> resMap= new HashMap<>();
         
        	if(BTSLUtil.isNullOrEmptyList(responseList)) {
        		p_requestVO.setSuccessTxn(false);
 				p_requestVO.setMessageCode(PretupsErrorCodesI.TOTAL_TRANSACTION_DETAILED_VIEW_FAILURE);
 				return;
        		
        	}
			      
	        if("REST".equalsIgnoreCase(p_requestVO.getRequestGatewayCode()))
	        {
	        	_log.debug(methodName, "Preparing rest response");
	        	p_requestVO.setMessageCode(PretupsErrorCodesI.COMMISSION_SUCCESS);
	        	
	        	
	        	 resMap.put("map", map);
	            if(!p_requestVO.getSenderReturnMessage().equals("NO Details Found For Input")) {
	            p_requestVO.setResponseMap(resMap);
	            p_requestVO.setSuccessTxn(true);
				p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_TRANSFER_SUCCESS);
				p_requestVO.setSenderReturnMessage("Enquiry has been successfully done!");}
	            else {
	            	p_requestVO.setResponseMap(resMap);
	            	p_requestVO.setSuccessTxn(false);
	 				p_requestVO.setMessageCode(PretupsErrorCodesI.TOTAL_TRANSACTION_DETAILED_VIEW_FAILURE);
	 				p_requestVO.setSenderReturnMessage("NO Details Found For Input");
	            	
	            }
	            
	       }
	        else if("MAPPGW".equalsIgnoreCase(p_requestVO.getRequestGatewayCode()))
	        {    _log.debug(methodName, "Preparing mobile app gateway response");
	        String resType = null;
	        int i=0;
			resType = reqMap.get("TYPE") + "RES";
			responseStr.append("{ \"type\": \"" + resType + "\" ,");
			responseStr.append(" \"txnStatus\": \"" + PretupsI.TXN_STATUS_SUCCESS + "\" ,");
			responseStr.append(" \"transactionDetails\": [");
	        	for (TotalTransactionsDetailedViewResponseVO resObj : responseList) {
	        	 	
					if(i==responseList.size()-1) {
						responseStr.append("{ \"transactionId\": \"" + resObj.getTransactionId() + "\" ,");
						responseStr.append("\"recieverMsisdn\": \"" + resObj.getRecieverMsisdn()+ "\" ,");
						responseStr.append("\"rechargeAmount\": \"" + resObj.getRechargeAmount() + "\" ,");
						responseStr.append("\"status\": \"" + resObj.getStatus() + "\" ,");
						responseStr.append("\"rechargeDateTime\": \"" + resObj.getRechargeDateTime() + "\" ,");
						responseStr.append("\"serviceType\": \"" + resObj.getServiceType() + "\"");

						responseStr.append("}");
					}else {
						responseStr.append("{ \"transactionId\": \"" + resObj.getTransactionId() + "\" ,");
						responseStr.append("\"recieverMsisdn\": \"" + resObj.getRecieverMsisdn()+ "\" ,");
						responseStr.append("\"rechargeAmount\": \"" + resObj.getRechargeAmount() + "\" ,");
						responseStr.append("\"status\": \"" + resObj.getStatus() + "\" ,");
						responseStr.append("\"rechargeDateTime\": \"" + resObj.getRechargeDateTime() + "\" ,");
						responseStr.append("\"serviceType\": \"" + resObj.getServiceType() + "\"");

						responseStr.append("},");
					}
					i++;
				
	        	}
	        	responseStr.append("]}");
	        	responseMap.put("RESPONSE", responseStr);
				p_requestVO.setResponseMap(responseMap);
				p_requestVO.setSuccessTxn(true);
				p_requestVO.setMessageCode("20000");
				p_requestVO.setSenderReturnMessage("Transaction has been completed!");
				

	        }
	        else
	        {	for (TotalTransactionsDetailedViewResponseVO resObj : responseList) {
	        	
	        	responseStr.append("<TRANSACTIONDETAIL>");
				responseStr.append("<TRANSACTIONID>" + resObj.getTransactionId() + "</TRANSACTIONID>");
				responseStr.append("<RECIEVERMSISDN>" + resObj.getRecieverMsisdn() + "</RECIEVERMSISDN>");
				responseStr.append("<RECIEVERAMOUNT>" + resObj.getRechargeAmount() + "</RECIEVERAMOUNT>");	
				responseStr.append("<STATUS>" + resObj.getStatus() + "</STATUS>");
				responseStr.append("<RECHARGEDATETIME>" + resObj.getRechargeDateTime() + "</RECHARGEDATETIME>");
				responseStr.append("<SERVICETYPE>" + resObj.getServiceType() + "</SERVICETYPE>");
				responseStr.append("</TRANSACTIONDETAIL>");
	        	
	        }	
				responseMap.put("RESPONSE", responseStr);
				p_requestVO.setSuccessTxn(true);
				p_requestVO.setResponseMap(responseMap);
	        }
	        
	        
		}
		
		catch (ParseException pxe) {
			
			_log.debug(methodName, "In catch block");
      	  	_log.error(methodName, "Exception:e=" + pxe);
      	  	p_requestVO.setMessageCode(PretupsErrorCodesI.COMMISSION_DATE_ERROR);			
		}
		catch (Exception e) {
			
			_log.debug(methodName, "In catch block");
      	  	_log.error(methodName, "Exception:e=" + e);
      	  p_requestVO.setMessageCode(PretupsErrorCodesI.TOTAL_TRANSACTION_DETAILED_VIEW_FAILURE);				
		}
		finally
		{


			try {
				if (mcomCon != null) {
					mcomCon.close("TotalTransactionDetailedView#" + methodName);
					mcomCon = null;
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			if (_log.isDebugEnabled()) {
				_log.debug(methodName, " Exited ");
			}

		
			
		}
		
	}
 	
	
}
