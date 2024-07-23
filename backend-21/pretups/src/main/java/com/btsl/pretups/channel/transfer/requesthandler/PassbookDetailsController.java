package com.btsl.pretups.channel.transfer.requesthandler;

import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


import jakarta.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.time.DateUtils;

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
import com.btsl.pretups.channel.transfer.businesslogic.PassbookViewRequestVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.common.PretupsRestI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.PassbookDetailsVO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OracleUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import io.swagger.v3.oas.annotations.tags.Tag;


/**
 * @author rahul.arya1
 *
 */
@Path("")
@Tag(name = "${dummy.name}", description = "${dummy.desc}")//@Api(value="PASSBOOK VIEW")
public class PassbookDetailsController implements ServiceKeywordControllerI{

	private final Log _log = LogFactory.getLog(PassbookDetailsController.class.getName());
	@Context
 	private HttpServletRequest httpServletRequest;
 	@POST
 	@Path("/c2s-rest-receiver/pasbdet")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
 	/*@ApiOperation(value = "PASSBOOK VIEW INFO", response = PretupsResponse.class)
    */

    @io.swagger.v3.oas.annotations.Operation(summary = "${pasbdet.summary}", description="${pasbdet.description}",

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
                            )
                    }),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_UNAUTH_RESPONSE_CODE, description = com.btsl.util.Constants.API_UNAUTH_RESPONSE_DESC,  content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
                            )
                    }),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_CODE, description = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_DESC,  content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
                            )
                    }),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_DESC,  content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
                            )
                    })
            }
    )



    public PretupsResponse<JsonNode> processCP2PUserRequest(PassbookViewRequestVO passbookViewRequestVO)
    		 throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {	
 		
 		PretupsResponse<JsonNode> response;
        RestReceiver restReceiver;
        RestReceiver.updateRequestIdChannel();
        final String requestIDStr = String.valueOf(RestReceiver.getRequestIdChannel());
        restReceiver = new RestReceiver();
        response = restReceiver.processPassbookView(httpServletRequest,(JsonNode)(PretupsRestUtil.convertJSONToObject(PretupsRestUtil.convertObjectToJSONString(passbookViewRequestVO), new TypeReference<JsonNode>(){})), PretupsRestI.PASSBOOKVIEW,requestIDStr);
        return response;

}
    @Override
	public void process(RequestVO p_requestVO) {
    final String METHOD_NAME = "process";
    if (_log.isDebugEnabled()) {
        _log.debug("process", " Entered p_requestVO=" + p_requestVO);
    }
    Connection con = null;MComConnectionI mcomCon = null;
    final PassbookDetailsVO passbookDetailsVO = new PassbookDetailsVO();
    UserDAO userDao = null;
    ChannelUserDAO channelUserDAO =null;
    StringBuilder responseStr = null;
    HashMap responseMap = new HashMap();
    try {
    	userDao = new UserDAO();
    	channelUserDAO = new ChannelUserDAO();
        final String messageArr[] = p_requestVO.getRequestMessageArray();
        responseStr = new StringBuilder();
        if (_log.isDebugEnabled()) {
            _log.debug("process", " Message Array " + messageArr);
        }
        mcomCon = new MComConnection();con=mcomCon.getConnection();
        HashMap reqMap = p_requestVO.getRequestMap();
			if ("MAPPGW".equalsIgnoreCase(p_requestVO.getRequestGatewayCode())){
				String resType = null;
				resType = reqMap.get("TYPE") + "RES";
				responseStr.append("{ \"type\": \"" + resType + "\" ,");
				responseStr.append(" \"txnStatus\": \"" + PretupsI.TXN_STATUS_SUCCESS + "\" ,");
			}
        if(p_requestVO.getFilteredMSISDN()!=null)
        {
        	ChannelUserVO channelUserVO= channelUserDAO.loadChannelUserDetails(con,p_requestVO.getFilteredMSISDN());
        	passbookDetailsVO.setUserID(channelUserVO.getUserID());
        }
        if("MAPPGW".equalsIgnoreCase(p_requestVO.getRequestGatewayCode()))
        {
        	String fromDate=(String) p_requestVO.getRequestMap().get("FROMDATE");
        	String toDate=(String) p_requestVO.getRequestMap().get("TODATE");
        	 Date frDate = new Date();
     		Date tDate = new Date();
     		Date currentDate = new Date();
     		SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DATESPACEHHMMSS);
             sdf.setLenient(false); // this is required else it will convert
             frDate = sdf.parse(fromDate+" 00:00:00");
             tDate = sdf.parse(toDate+" 23:59:59");
             if (BTSLUtil.getDifferenceInUtilDates(frDate, currentDate) < 0) 
 			{
 				p_requestVO.setMessageCode(PretupsErrorCodesI.CCE_ERROR_FROMDATE_GREATER_THAN_CURRENTDATE);
 				throw new BTSLBaseException("PassbookDetailsController", "process",
 						PretupsErrorCodesI.CCE_ERROR_FROMDATE_GREATER_THAN_CURRENTDATE, 0, null);
 			}
 			if (BTSLUtil.getDifferenceInUtilDates(tDate, currentDate) < 0) 
 			{
 				p_requestVO.setMessageCode(PretupsErrorCodesI.CCE_ERROR_TODATE_GREATER_THAN_CURRENTDATE);
 				throw new BTSLBaseException("PassbookDetailsController", "process",
 						PretupsErrorCodesI.CCE_ERROR_TODATE_GREATER_THAN_CURRENTDATE, 0, null);
 			}
 			if (BTSLUtil.getDifferenceInUtilDates(frDate, tDate) < 0) 
 			{
 				p_requestVO.setMessageCode(PretupsErrorCodesI.CCE_ERROR_FROMDATE_GREATER_THAN_TODATE);
 				throw new BTSLBaseException("PassbookDetailsController", "process",
 						PretupsErrorCodesI.CCE_ERROR_FROMDATE_GREATER_THAN_TODATE, 0, null);
 			}
             
        }
        else if (BTSLUtil.getDateFromDateString(messageArr[1]).after(BTSLUtil.getDateFromDateString(messageArr[2]))) {
        	p_requestVO.setMessageCode(PretupsErrorCodesI.CCE_ERROR_FROMDATE_GREATER_THAN_TODATE);
        	throw new BTSLBaseException("PassbookDetailsController", "process",
					PretupsErrorCodesI.CCE_ERROR_FROMDATE_GREATER_THAN_TODATE, 0, null);
        }
        if("REST".equalsIgnoreCase(p_requestVO.getRequestGatewayCode())||"EXTGW".equalsIgnoreCase(p_requestVO.getRequestGatewayCode())||"USSD".equalsIgnoreCase(p_requestVO.getRequestGatewayCode()))
        {
        	passbookDetailsVO.setFromDate(BTSLDateUtil.getGregorianDate(messageArr[1]));
            passbookDetailsVO.setToDate(BTSLDateUtil.getGregorianDate(messageArr[2]));
        }
        else
        {
        passbookDetailsVO.setFromDate(BTSLDateUtil.getGregorianDate((String) p_requestVO.getRequestMap().get("FROMDATE")));
        passbookDetailsVO.setToDate(BTSLDateUtil.getGregorianDate((String) p_requestVO.getRequestMap().get("TODATE")));
        }
        LinkedHashMap<Date, PassbookDetailsVO> map = userDao.loadStockSalesC2C(con, passbookDetailsVO);
        map=userDao.loadStockSalesC2S(con, passbookDetailsVO,map);
        map=userDao.loadStockPurchaseC2C(con, passbookDetailsVO, map);
        map=userDao.loadCommissionQtyC2C(con, passbookDetailsVO,map);
        map=userDao.loadCommissionQtyC2S(con, passbookDetailsVO,map);
        /*map=userDao.setClosingBalance(con,passbookDetailsVO,map);*/
        map=userDao.loadWithdrawBalance(con, passbookDetailsVO,map);
        map=userDao.loadReturnBalance(con, passbookDetailsVO,map);
        map=userDao.loadClosingBalance(con, passbookDetailsVO,map);
        HashMap<String, Object> resMap= new HashMap<>();
        resMap.put("map", map);
        if("REST".equalsIgnoreCase(p_requestVO.getRequestGatewayCode()))
        {
        	LinkedHashMap<Date, PassbookDetailsVO> mapRest =new LinkedHashMap<>();
        	 Set set = map.entrySet();
             Iterator i1 = set.iterator();
             int i=0;
             while(i1.hasNext()) {
                 i++;
             	Map.Entry me = (Map.Entry)i1.next();
             	if(((Date)me.getKey()).compareTo(DateUtils.addDays(passbookDetailsVO.getFromDate(),-1))>0&&((Date)me.getKey()).compareTo(DateUtils.addDays(passbookDetailsVO.getToDate(),1))<0)
 				{
             		mapRest.put((Date)me.getKey(), (PassbookDetailsVO)me.getValue());
 				}
             	
 				}
             resMap.put("map", mapRest);
        	 p_requestVO.setResponseMap(resMap);
        }
        
        LinkedHashMap<Date, PassbookDetailsVO> map2 =(LinkedHashMap<Date, PassbookDetailsVO>) resMap.get("map");
        TreeMap<Date, PassbookDetailsVO> map1 = new  TreeMap<Date, PassbookDetailsVO> (map2);  
        
        
        
        if ("MAPPGW".equalsIgnoreCase(p_requestVO.getRequestGatewayCode())){
			responseStr.append("\"passbook\":[");
		}
        if ("MAPPGW".equalsIgnoreCase(p_requestVO.getRequestGatewayCode())) {
            Set set = map1.entrySet();
            Iterator i1 = set.iterator();
            int i=0;
            while(i1.hasNext()) {
                i++;
            	Map.Entry me = (Map.Entry)i1.next();
            	if(((Date)me.getKey()).compareTo(DateUtils.addDays(passbookDetailsVO.getFromDate(),-1))>0&&((Date)me.getKey()).compareTo(DateUtils.addDays(passbookDetailsVO.getToDate(),1))<0)
				{
	                responseStr.append("{ \"date\": \"" + me.getKey() + "\" ,");
					responseStr.append("\"c2SStockSales\": \"" + PretupsBL.getDisplayAmount(((PassbookDetailsVO)(me.getValue())).getC2SStockSales()) + "\" ,");
					responseStr.append("\"marginAmount\":   \"" + PretupsBL.getDisplayAmount(((PassbookDetailsVO)(me.getValue())).getMarginAmount()) + "\" , ");
					responseStr.append(" \"commissionValue\":    \"" + PretupsBL.getDisplayAmount(((PassbookDetailsVO)(me.getValue())).getCommissionValue())+ "\" , ");
					responseStr.append(" \"c2CStockSales\":   \"" + PretupsBL.getDisplayAmount(((PassbookDetailsVO)(me.getValue())).getC2CStockSales()) + "\" , ");
					responseStr.append(" \"withdrawBalance\":   \"" + PretupsBL.getDisplayAmount(((PassbookDetailsVO)(me.getValue())).getWithdrawBalance()) + "\" , ");
					responseStr.append("\"openingBalance\": \"" + PretupsBL.getDisplayAmount(((PassbookDetailsVO)(me.getValue())).getOpeningBalance()) + "\" ,");
					responseStr.append("   \"returnBalance\":  \"" + PretupsBL.getDisplayAmount(((PassbookDetailsVO)(me.getValue())).getReturnBalance()) + "\",");
					responseStr.append(" \"closingBalance\":   \"" + PretupsBL.getDisplayAmount(((PassbookDetailsVO)(me.getValue())).getClosingBalance()) + "\" , ");
					responseStr.append("   \"stockPurchase\":  \"" + PretupsBL.getDisplayAmount(((PassbookDetailsVO)(me.getValue())).getStockPurchase())+"\"  ");
					responseStr.append("},");
				}
            }
            if(!BTSLUtil.isNullObject(responseStr)) {
            	responseStr.deleteCharAt(responseStr.length() - 1);
            }
            
            
        }
        else if("EXTGW".equalsIgnoreCase(p_requestVO.getRequestGatewayCode())||"USSD".equalsIgnoreCase(p_requestVO.getRequestGatewayCode()))
        {
        	Set set = map1.entrySet();
            Iterator i1 = set.iterator();
            responseStr.append("<PASSBOOKDETAILSVIEW>");
            while(i1.hasNext()) {
            	Map.Entry me = (Map.Entry)i1.next();
            	if(((Date)me.getKey()).compareTo(DateUtils.addDays(passbookDetailsVO.getFromDate(),-1))>0&&((Date)me.getKey()).compareTo(DateUtils.addDays(passbookDetailsVO.getToDate(),1))<0)
				{
            	responseStr.append("<DATE>" + me.getKey()  + "</DATE>");
				responseStr.append("<C2SSTOCKSALES>" +  ((PassbookDetailsVO)(me.getValue())).getC2SStockSales()+ "</C2SSTOCKSALES>");
				responseStr.append("<MARGINAMOUNT>"+ ((PassbookDetailsVO)(me.getValue())).getMarginAmount() + "</MARGINAMOUNT>");
				responseStr.append("<COMMISSIONVALUE>" + ((PassbookDetailsVO)(me.getValue())).getCommissionValue()+ "</COMMISSIONVALUE>");
				responseStr.append("<C2CSTOCKSALES>" + ((PassbookDetailsVO)(me.getValue())).getC2CStockSales() + "</C2CSTOCKSALES>");
				responseStr.append("<WITHDRAWBALANCE>" + ((PassbookDetailsVO)(me.getValue())).getWithdrawBalance() + "</WITHDRAWBALANCE>");
				responseStr.append("<OPENINGBALANCE>" + ((PassbookDetailsVO)(me.getValue())).getOpeningBalance() + "</OPENINGBALANCE>");
				responseStr.append("<RETURNBALANCE>" + ((PassbookDetailsVO)(me.getValue())).getReturnBalance() + "</RETURNBALANCE>");
				responseStr.append("<CLOSINGBALANCE>" + ((PassbookDetailsVO)(me.getValue())).getClosingBalance() + "</CLOSINGBALANCE>");
				responseStr.append("<STOCKPURCHASE>" + ((PassbookDetailsVO)(me.getValue())).getStockPurchase() +"</STOCKPURCHASE>");
				}
				
			}
            responseStr.append("</PASSBOOKDETAILSVIEW>");
            responseMap.put("RESPONSE", responseStr);
			p_requestVO.setResponseMap(responseMap);
			p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_TRANSFER_SUCCESS);
			p_requestVO.setSenderReturnMessage("PASSBOOK VIEW successfully done!");
        }
        if ("MAPPGW".equalsIgnoreCase(p_requestVO.getRequestGatewayCode())){
			responseStr.append("]}");
			_log.debug("response ", "Passbook View Api  " + responseStr);
			responseMap.put("RESPONSE", responseStr);
			p_requestVO.setResponseMap(responseMap);
			p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_TRANSFER_SUCCESS);
			p_requestVO.setSenderReturnMessage("PASSBOOK VIEW successfully done!");
		}
        
    } catch (BTSLBaseException be) {
        p_requestVO.setSuccessTxn(false);
        p_requestVO.setMessageCode(be.getMessageKey());
        OracleUtil.rollbackConnection(con, PassbookDetailsController.class.getName(), METHOD_NAME);
        _log.error("process", "BTSLBaseException " + be.getMessage());
        _log.errorTrace(METHOD_NAME, be);
        if (be.isKey()) {
            p_requestVO.setMessageCode(be.getMessageKey());
            p_requestVO.setMessageArguments(be.getArgs());
        } else {
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        }
    } catch (Exception e) {
        p_requestVO.setSuccessTxn(false);
        OracleUtil.rollbackConnection(con, PassbookDetailsController.class.getName(), METHOD_NAME);
        _log.error("process", "BTSLBaseException " + e.getMessage());
        _log.errorTrace(METHOD_NAME, e);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PassbookDetailsController[process]", "", "", "",
                        "Exception:" + e.getMessage());
        p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
        return;
    } finally {
		if (mcomCon != null) {
			mcomCon.close("PassbookDetailsController#process");
			mcomCon = null;
		}
        if (_log.isDebugEnabled()) {
            _log.debug("process", " Exited ");
        }
    }

}
}
