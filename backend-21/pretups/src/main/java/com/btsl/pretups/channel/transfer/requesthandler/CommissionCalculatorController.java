package com.btsl.pretups.channel.transfer.requesthandler;

import java.io.IOException;
import java.sql.Connection;
import java.text.ParseException;
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

import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.receiver.RestReceiver;
import com.btsl.pretups.channel.transfer.businesslogic.CommissionCalculatorRequestVO;
import com.btsl.pretups.channel.transfer.businesslogic.CommissionCalculatorResponseVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.common.PretupsRestI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.txn.pretups.channel.profile.businesslogic.CommissionProfileTxnDAO;

import io.swagger.v3.oas.annotations.tags.Tag;

@Path("")
@Tag(name = "${dummy.name}", description = "${dummy.desc}")//@Api(value="C2S Income Calculator")
public class CommissionCalculatorController implements ServiceKeywordControllerI {
	
	protected final Log _log = LogFactory.getLog(getClass().getName());
	
	@Context
 	private HttpServletRequest httpServletRequest;
 	@POST
 	@Path("/c2s-receiver/commissioncalculator")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
 	//@ApiOperation(value = "User commission info ", response = PretupsResponse.class)

	@io.swagger.v3.oas.annotations.Operation(summary = "${commissioncalculator.summary}", description="${commissioncalculator.description}",

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



	public PretupsResponse<JsonNode> processCP2PUserRequest(CommissionCalculatorRequestVO commissionCalculatorRequestVO)
    		 throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {	
 		
 		PretupsResponse<JsonNode> response;
        RestReceiver restReceiver;
        RestReceiver.updateRequestIdChannel();
        final String requestIDStr = String.valueOf(RestReceiver.getRequestIdChannel());
        restReceiver = new RestReceiver();
        response = restReceiver.processCommsission(httpServletRequest,(JsonNode)(PretupsRestUtil.convertJSONToObject(PretupsRestUtil.convertObjectToJSONString(commissionCalculatorRequestVO), new TypeReference<JsonNode>(){})), PretupsRestI.COMMISSION_INCOME,requestIDStr);
        return response;

}
	@Override
	public void process(RequestVO p_requestVO) {
		
		final String methodName = "process";
		_log.debug(methodName, "entered");
	
		CommissionCalculatorResponseVO commissionCalculatorResponseVO = new CommissionCalculatorResponseVO();
		
		PretupsResponse<JsonNode> jsonReponse = new PretupsResponse<JsonNode>();
		String msisdn = "";
		String loginId = "";
		String totalIncomeCurrent = "";
		String totalIncomePrevious = "";
		Connection con = null;
		ChannelUserVO channelUserVO = null;
        MComConnectionI mcomCon = null;
        UserDAO userDao = new UserDAO();
        JsonNode dataObject;
        HashMap responseMap = new HashMap<>();
		CommissionProfileTxnDAO commissionProfileTxnDAO = new CommissionProfileTxnDAO();
		StringBuffer responseStr = new StringBuffer();
		
		HashMap reqMap = p_requestVO.getRequestMap();
		
		try
		{
			if (reqMap != null && (reqMap.get("MSISDN2") != null || reqMap.get("LOGINID2") != null) && p_requestVO.getRequestMessage() != null ) 
			{
				
				msisdn = (String) reqMap.get("MSISDN2");
				loginId = (String) reqMap.get("LOGINID2");
			}
			else if ("MAPPGW".equalsIgnoreCase(p_requestVO.getRequestGatewayCode()))
			{
				_log.debug(methodName, "getting msisdn in mobile gateway");
				msisdn = (String) reqMap.get("MSISDN");
			}
			else
			{
				p_requestVO.setSenderReturnMessage("Msisdn or loginId is required.");
            	p_requestVO.setSuccessTxn(false);
				p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_REQ_EITHER_MSISDN_LOGINID_REQ);
				return;
			}
			
			mcomCon = new MComConnection();
	        con=mcomCon.getConnection();
	        String fromDate = (String) reqMap.get("FROMDATE");
	        String toDate =  (String) reqMap.get("TODATE");
	        
	        if(fromDate == null || "".equals(fromDate))
	        {
	        	p_requestVO.setSuccessTxn(false);
	        	p_requestVO.setMessageCode(PretupsErrorCodesI.CCE_XML_ERROR_FROM_DATE_REQUIRED);
				return;
	        	
	        }
	        if(toDate == null || "".equals(toDate))
	        {
	        	p_requestVO.setSuccessTxn(false);
	        	p_requestVO.setMessageCode(PretupsErrorCodesI.CCE_XML_ERROR_TO_DATE_REQUIRED);
				return;
	        }
	        
	        if(!"".equals(msisdn))
	        {
	        	channelUserVO = userDao.loadUserDetailsByMsisdn(con, msisdn);
	        }
	        else if(!"".equals(loginId))
	        {
	        	channelUserVO = userDao.loadAllUserDetailsByLoginID(con, loginId);
	        }
	        
	        Date frDate = new Date();
    		Date tDate = new Date();
    		Date currentDate = new Date();
	        
           
    		
    		SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DATESPACEHHMMSS);
            sdf.setLenient(false); // this is required else it will convert
            
            frDate = sdf.parse(fromDate+" 00:00:00");
            tDate = sdf.parse(toDate+" 23:59:59");
    		
            
            if (BTSLUtil.getDifferenceInUtilDates(frDate, currentDate) < 0) 
			{
            	p_requestVO.setSuccessTxn(false);
            	p_requestVO.setMessageCode(PretupsErrorCodesI.CCE_ERROR_FROMDATE_GREATER_THAN_CURRENTDATE);
				return;
			}
			if (BTSLUtil.getDifferenceInUtilDates(tDate, currentDate) < 0) 
			{
            	p_requestVO.setSuccessTxn(false);
				p_requestVO.setMessageCode(PretupsErrorCodesI.CCE_ERROR_TODATE_GREATER_THAN_CURRENTDATE);
				return;
			}
			if (BTSLUtil.getDifferenceInUtilDates(frDate, tDate) < 0) 
			{
            	p_requestVO.setSuccessTxn(false);
				p_requestVO.setMessageCode(PretupsErrorCodesI.CCE_ERROR_FROMDATE_GREATER_THAN_TODATE);
				return;
			}
            
            currentDate = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(currentDate));
	        
	        totalIncomeCurrent = commissionProfileTxnDAO.calculateTotalIncome(con, frDate, tDate, channelUserVO.getUserID());
	        
	        commissionCalculatorResponseVO.setCurrentFromDate(com.btsl.util.BTSLUtil.getDateStringFromDate(frDate));
	        commissionCalculatorResponseVO.setCurrentToDate(com.btsl.util.BTSLUtil.getDateStringFromDate(tDate));
	        
	        frDate = com.btsl.util.BTSLUtil.getDateOneMonthBeforeFromDate(frDate);
	        tDate = com.btsl.util.BTSLUtil.getDateOneMonthBeforeToDate(tDate);
	        
	        totalIncomePrevious = commissionProfileTxnDAO.calculateTotalIncome(con, frDate, tDate, channelUserVO.getUserID());
	        
	        commissionCalculatorResponseVO.setCurrentMonthIncome(totalIncomeCurrent);
	        commissionCalculatorResponseVO.setPreviousMonthIncome(totalIncomePrevious);

	        
	        commissionCalculatorResponseVO.setPreviousMonthFromDate(com.btsl.util.BTSLUtil.getDateStringFromDate(frDate));
	        commissionCalculatorResponseVO.setPreviousMonthToDate(com.btsl.util.BTSLUtil.getDateStringFromDate(tDate));
	        
	        
	        if("REST".equalsIgnoreCase(p_requestVO.getRequestGatewayCode()))
	        {
	        	_log.debug(methodName, "Preparing rest response");
	        	p_requestVO.setMessageCode(PretupsErrorCodesI.COMMISSION_SUCCESS);
		        dataObject = (JsonNode) PretupsRestUtil.convertJSONToObject(
						PretupsRestUtil.convertObjectToJSONString(commissionCalculatorResponseVO), new TypeReference<JsonNode>() {
						});
	
				jsonReponse.setDataObject(dataObject);
	
				p_requestVO.setJsonReponse(jsonReponse);
	        }
	        else if("MAPPGW".equalsIgnoreCase(p_requestVO.getRequestGatewayCode()))
	        {
	        	_log.debug(methodName, "Preparing mobile app gateway response");
	        	String resType = null;
				resType = reqMap.get("TYPE") + "RES";
				responseStr.append("{ \"type\": \"" + resType + "\" ,");
				responseStr.append(" \"txnStatus\": \"" + PretupsI.TXN_STATUS_SUCCESS + "\" ,");
				responseStr.append(" \"totalIncome\": [");
				responseStr.append("{ \"currentMonthIncome\": \"" + commissionCalculatorResponseVO.getCurrentMonthIncome() + "\" ,");
				responseStr.append("\"previousMonthIncome\": \"" + commissionCalculatorResponseVO.getPreviousMonthIncome() + "\" ,");
				responseStr.append("\"currentMonthFromDate\": \"" + commissionCalculatorResponseVO.getCurrentFromDate() + "\" ,");
				responseStr.append("\"currentMonthToDate\": \"" + commissionCalculatorResponseVO.getCurrentToDate() + "\" ,");
				responseStr.append("\"previousMonthFromDate\": \"" + commissionCalculatorResponseVO.getPreviousMonthFromDate() + "\" ,");
				responseStr.append("\"previousMonthToDate\": \"" + commissionCalculatorResponseVO.getPreviousMonthToDate() + "\"");

				responseStr.append("}]}");
				responseMap.put("RESPONSE", responseStr);
				p_requestVO.setResponseMap(responseMap);
				
				p_requestVO.setResponseMap(responseMap);
				p_requestVO.setSuccessTxn(true);
				p_requestVO.setMessageCode("20000");
				p_requestVO.setSenderReturnMessage("Transaction has been completed!");
				

	        }
	        else
	        {
	        	responseStr.append("<COMMISSIONINCOME>");
				responseStr.append("<INCOMECURRENTMONTH>" + commissionCalculatorResponseVO.getCurrentMonthIncome() + "</INCOMECURRENTMONTH>");
				responseStr.append("<INCOMEPREVIOUSMONTH>" + commissionCalculatorResponseVO.getPreviousMonthIncome() + "</INCOMEPREVIOUSMONTH>");
				responseStr.append("<PREVIOUSMONTHFROMDATE>" + commissionCalculatorResponseVO.getPreviousMonthFromDate() + "</PREVIOUSMONTHFROMDATE>");
				responseStr.append("<PREVIOUSMONTHTODATE>" + commissionCalculatorResponseVO.getPreviousMonthToDate() + "</PREVIOUSMONTHTODATE>");

				responseStr.append("</COMMISSIONINCOME>");
				
				responseMap.put("RESPONSE", responseStr);
				p_requestVO.setResponseMap(responseMap);
	        }
	        
	        
		}
		
		catch (ParseException pxe) {
			
			p_requestVO.setSuccessTxn(false);
			_log.debug(methodName, "In catch block");
      	  	_log.error(methodName, "Exception:e=" + pxe);
      	  	p_requestVO.setMessageCode(PretupsErrorCodesI.COMMISSION_DATE_ERROR);			
		}
		catch (Exception e) {
			p_requestVO.setSuccessTxn(false);
			_log.debug(methodName, "In catch block");
      	  	_log.error(methodName, "Exception:e=" + e);
      	  p_requestVO.setMessageCode(PretupsErrorCodesI.COMMISSION_FAILURE);				
		}
		finally
		{

			try {
				if (mcomCon != null) {
					mcomCon.close("CommissionCalculatorController#" + methodName);
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
