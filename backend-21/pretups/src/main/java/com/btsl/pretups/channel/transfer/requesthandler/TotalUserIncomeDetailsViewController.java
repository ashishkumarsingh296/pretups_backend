package com.btsl.pretups.channel.transfer.requesthandler;

import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;


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
import com.btsl.pretups.channel.transfer.businesslogic.TotalIncomeDetailsViewVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.common.PretupsRestI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.TotalDailyUserIncomeResponseVO;
import com.btsl.user.businesslogic.TotalUserIncomeDetailsVO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OracleUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @author Yogesh.Dixit
 *
 */
@Path("")
@Tag(name = "${dummy.name}", description = "${dummy.desc}")//@Api(value="Total Income detailed VIEW")
public class TotalUserIncomeDetailsViewController implements ServiceKeywordControllerI {

	private final Log _log = LogFactory.getLog(TotalUserIncomeDetailsViewController.class.getName());
	@Context
 	private HttpServletRequest httpServletRequest;
 	@POST
 	@Path("/c2s-receiver/usrincview")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
 	//@ApiOperation(value = "Total Income detailed VIEW", response = PretupsResponse.class)

	@io.swagger.v3.oas.annotations.Operation(summary = "${usrincview.summary}", description="${usrincview.description}",

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



	public PretupsResponse<JsonNode> processCP2PUserRequest(TotalIncomeDetailsViewVO totalIncomeDetailsViewVO)
    		 throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {	
 		
 		PretupsResponse<JsonNode> response;
        RestReceiver restReceiver;
        RestReceiver.updateRequestIdChannel();
        final String requestIDStr = String.valueOf(RestReceiver.getRequestIdChannel());
        restReceiver = new RestReceiver();
        response = restReceiver.processTotalIncomeDetailsView(httpServletRequest,(JsonNode)(PretupsRestUtil.convertJSONToObject(PretupsRestUtil.convertObjectToJSONString(totalIncomeDetailsViewVO), new TypeReference<JsonNode>(){})), PretupsRestI.TOATLINCOMEDETAILSVIEW,requestIDStr);
        return response;

}
	
	@Override
	public void process(RequestVO p_requestVO) {
	    final String METHOD_NAME = "process";
	    if (_log.isDebugEnabled()) {
	        _log.debug("process", " Entered p_requestVO=" + p_requestVO);
	    }
	    Connection con = null;MComConnectionI mcomCon = null;
	    UserDAO userDao = null;
	    ChannelUserDAO channelUserDAO = null;
	    StringBuilder responseStr = null;
	    HashMap<String,Object> responseMap = new HashMap();
	    Gson gson = new Gson();
	    TotalUserIncomeDetailsVO totalIncomeDetailsViewVO = new TotalUserIncomeDetailsVO();
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
	        
		if ( reqMap != null ) {
					totalIncomeDetailsViewVO.setMsisdn((String) reqMap.get("MSISDN"));
					totalIncomeDetailsViewVO.setFromdatestring((String) reqMap.get("FROMDATE"));
					totalIncomeDetailsViewVO.setTodatestring((String) reqMap.get("TODATE"));
					totalIncomeDetailsViewVO.setExtnwCode((String) reqMap.get("EXTNWCODE"));
		}
        if(p_requestVO.getFilteredMSISDN()!=null)
        {
        	ChannelUserVO channelUserVO= channelUserDAO.loadChannelUserDetails(con,p_requestVO.getFilteredMSISDN());
        	totalIncomeDetailsViewVO.setUserID(channelUserVO.getUserID());
        	if( totalIncomeDetailsViewVO.getExtnwCode() == null || !totalIncomeDetailsViewVO.getExtnwCode().equals(channelUserVO.getNetworkID())){
        		p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_NETWORK_CODE);
				throw new BTSLBaseException("TotalUserIncomeDetailsViewController", "process",
						PretupsErrorCodesI.INVALID_NETWORK_CODE, 0, null);
        	}
        } 
    
         String fromDate=totalIncomeDetailsViewVO.getFromdatestring();
    	 String toDate=totalIncomeDetailsViewVO.getTodatestring();
    	 Date frDate = new Date();
 		 Date tDate = new Date();
 		 Date currentDate = new Date();
 		 SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DATESPACEHHMMSS);
         sdf.setLenient(false); // this is required else it will convert
         //checking for date format(System)
	       
	        String patternDate= "\\d{2}/\\d{2}/\\d{2}";
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
		 
         
         frDate = sdf.parse(fromDate+" 00:00:00");
         tDate = sdf.parse(toDate+" 23:59:59");
         Date previousFrom = BTSLUtil.getDateOneMonthBeforeFromDate(frDate);
		 Date previousTo = BTSLUtil.getDateOneMonthBeforeToDate(tDate);
		
		
         if (BTSLUtil.getDifferenceInUtilDates(frDate, currentDate) < 0) 
			{
				p_requestVO.setMessageCode(PretupsErrorCodesI.CCE_ERROR_FROMDATE_GREATER_THAN_CURRENTDATE);
				throw new BTSLBaseException("TotalUserIncomeDetailsViewController", "process",
						PretupsErrorCodesI.CCE_ERROR_FROMDATE_GREATER_THAN_CURRENTDATE, 0, null);
			}
			if (BTSLUtil.getDifferenceInUtilDates(tDate, currentDate) < 0) 
			{
				p_requestVO.setMessageCode(PretupsErrorCodesI.CCE_ERROR_TODATE_GREATER_THAN_CURRENTDATE);
				throw new BTSLBaseException("TotalUserIncomeDetailsViewController", "process",
						PretupsErrorCodesI.CCE_ERROR_TODATE_GREATER_THAN_CURRENTDATE, 0, null);
			}
			if (BTSLUtil.getDifferenceInUtilDates(frDate, tDate) < 0) 
			{
				p_requestVO.setMessageCode(PretupsErrorCodesI.CCE_ERROR_FROMDATE_GREATER_THAN_TODATE);
				throw new BTSLBaseException("TotalUserIncomeDetailsViewController", "process",
						PretupsErrorCodesI.CCE_ERROR_FROMDATE_GREATER_THAN_TODATE, 0, null);
			}
			totalIncomeDetailsViewVO.setFromDate(frDate);
			totalIncomeDetailsViewVO.setToDate(tDate);
			LinkedHashMap<Date, TotalUserIncomeDetailsVO> map = new LinkedHashMap<Date, TotalUserIncomeDetailsVO>();
			userDao.loadUserIncomeC2CandO2C(con, totalIncomeDetailsViewVO,map);
			userDao.loadUserIncomeC2S(con, totalIncomeDetailsViewVO,map);
			totalIncomeDetailsViewVO.setTotalIncome1(totalIncomeDetailsViewVO.getToatalincomec2co2c()+totalIncomeDetailsViewVO.getTotalincomec2s());
			totalIncomeDetailsViewVO.setDetilInfoMap(map);
			// Get a set of all the entries (key - value pairs) contained in the LinkesHashMap
			LinkedList<TotalDailyUserIncomeResponseVO> object = new LinkedList<TotalDailyUserIncomeResponseVO>();
			Set entrySet = map.entrySet();
			// Obtain an Iterator for the entries Set
			Iterator it = entrySet.iterator();
			while(it.hasNext()) {
				Map.Entry me = (Map.Entry)it.next();
				TotalUserIncomeDetailsVO totalDailyUserIncomeVO1=(TotalUserIncomeDetailsVO) me.getValue();
				TotalDailyUserIncomeResponseVO totalDailyUserIncomeResponseVO = new TotalDailyUserIncomeResponseVO();
				Date date1 = (Date) me.getKey();
				totalDailyUserIncomeResponseVO.setDate(Long. toString(date1.getTime()));
				totalDailyUserIncomeResponseVO.setCac(PretupsBL.getDisplayAmount(totalDailyUserIncomeVO1.getCac()));
				totalDailyUserIncomeResponseVO.setCbc(PretupsBL.getDisplayAmount((totalDailyUserIncomeVO1.getCbc())));
				totalDailyUserIncomeResponseVO.setAdditionalCommission(PretupsBL.getDisplayAmount((totalDailyUserIncomeVO1.getAdditionalCommission())));
				totalDailyUserIncomeResponseVO.setBaseCommission(PretupsBL.getDisplayAmount((totalDailyUserIncomeVO1.getBaseCommission())));
				totalDailyUserIncomeResponseVO.setTotalIncome(PretupsBL.getDisplayAmount(totalDailyUserIncomeVO1.getAdditionalCommission()+totalDailyUserIncomeVO1.getBaseCommission()+totalDailyUserIncomeVO1.getCbc()+totalDailyUserIncomeVO1.getCac()));
				object.add(totalDailyUserIncomeResponseVO);
			}
			userDao.loadUserTotalIncomeDetailsBetweenRange(con,totalIncomeDetailsViewVO,previousFrom,previousTo);
			totalIncomeDetailsViewVO.setDetailedInfoList(object);

			if ("MAPPGW".equalsIgnoreCase(p_requestVO.getRequestGatewayCode())){
				String resType = null;
				resType = reqMap.get("TYPE") + "RES";
				responseStr.append("{ \"type\": \"" + resType + "\" ,");
				responseStr.append(" \"txnStatus\": \"" + PretupsI.TXN_STATUS_SUCCESS + "\" ,");
				responseStr.append(" \"detailedinfo\": [");
		
			int i = 0;
			for (TotalDailyUserIncomeResponseVO resObj : totalIncomeDetailsViewVO.getDetailedInfoList()) {	
				i++;
				responseStr.append("{ \"date\": \"" + resObj.getDate() + "\" ,");
				responseStr.append("\"totalIncome\": \"" + resObj.getTotalIncome() + "\" ,");
				
				responseStr.append("\"baseCommission\":   \"" + resObj.getBaseCommission() + "\" , ");
				if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TARGET_BASED_BASE_COMMISSION))).booleanValue()) {
				responseStr.append(" \"cbc\":    \"" + resObj.getCbc() + "\" , ");}
				if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TARGET_BASED_COMMISSION))).booleanValue()) {
				responseStr.append(" \"cac\":   \"" + resObj.getCac() + "\" , ");}
				if(i == totalIncomeDetailsViewVO.getDetailedInfoList().size()){
					responseStr.append(" \"additionalCommission\":   \"" + resObj.getAdditionalCommission() + "\" }");
				
				}
				else{
				responseStr.append(" \"additionalCommission\":   \"" + resObj.getAdditionalCommission()+ "\" },");
				}
		}
			responseStr.append("],");
			responseStr.append("\"totalIncome\": \"" + PretupsBL.getDisplayAmount(totalIncomeDetailsViewVO.getTotalIncome1()) + "\" ,");
			responseStr.append("\"previousTotalIncome\": \"" + PretupsBL.getDisplayAmount(totalIncomeDetailsViewVO.getPreviousTotalIncome()) + "\" ,");
			responseStr.append("\"totalBaseCom\":   \"" + PretupsBL.getDisplayAmount(totalIncomeDetailsViewVO.getTotalBaseCom()) + "\" , ");
			responseStr.append(" \"previousTotalBaseComm\":    \"" + PretupsBL.getDisplayAmount(totalIncomeDetailsViewVO.getPreviousTotalBaseComm()) + "\" , ");
			responseStr.append("\"totalAdditionalBaseCom\": \"" + PretupsBL.getDisplayAmount(totalIncomeDetailsViewVO.getTotalAdditionalBaseCom()) + "\" ,");
			responseStr.append("\"previousTotalAdditionalBaseCom\": \"" + PretupsBL.getDisplayAmount(totalIncomeDetailsViewVO.getPreviousTotalAdditionalBaseCom()) + "\" ,");
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TARGET_BASED_COMMISSION))).booleanValue()) {
			responseStr.append("\"totalCac\":   \"" + PretupsBL.getDisplayAmount(totalIncomeDetailsViewVO.getTotalCac())+ "\" , ");
			responseStr.append(" \"previousTotalCac\":    \"" + PretupsBL.getDisplayAmount(totalIncomeDetailsViewVO.getPreviousTotalCac()) + "\" , ");
			}
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TARGET_BASED_BASE_COMMISSION))).booleanValue()) {
			responseStr.append("\"totalCbc\": \"" + PretupsBL.getDisplayAmount(totalIncomeDetailsViewVO.getTotalCbc()) + "\" ,");
			responseStr.append("\"previousTotalCbc\": \"" + PretupsBL.getDisplayAmount(totalIncomeDetailsViewVO.getPreviousTotalCbc()) + "\" ,");
			}
			responseStr.append("\"fromDate\":    \"" + Long. toString(frDate.getTime()) + "\" , ");
			responseStr.append("\"toDate\": \"" + Long. toString(tDate.getTime()) + "\" ,");
			responseStr.append("\"previousFromDate\": \"" + Long. toString(previousFrom.getTime()) + "\" ,");
			responseStr.append(" \"previousToDate\":    \"" + Long. toString(previousTo.getTime())+ "\" ");
			responseStr.append("}");
			_log.debug("response ", "Total user income details view   " + responseStr);
			responseMap.put("RESPONSE", responseStr);		
	} else {
			responseMap.put("detailedinfo", totalIncomeDetailsViewVO);
			responseMap.put("fromDate", Long. toString(frDate.getTime()));
			responseMap.put("toDate",Long. toString(tDate.getTime()));
			responseMap.put("previousFromDate" ,Long. toString(previousFrom.getTime()));
			responseMap.put("previousToDate",Long. toString(previousTo.getTime()));
	}
		    p_requestVO.setResponseMap(responseMap);
	    } catch (BTSLBaseException be) {
	        p_requestVO.setSuccessTxn(false);
	        p_requestVO.setMessageCode(be.getMessageKey());
	        OracleUtil.rollbackConnection(con, TotalUserIncomeDetailsViewController.class.getName(), METHOD_NAME);
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
	        OracleUtil.rollbackConnection(con, TotalUserIncomeDetailsViewController.class.getName(), METHOD_NAME);
	        _log.error("process", "BTSLBaseException " + e.getMessage());
	        _log.errorTrace(METHOD_NAME, e);
	        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TotalUserIncomeDetailsViewController[process]", "", "", "",
	                        "Exception:" + e.getMessage());
	        p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
	        return;
	    } finally {
			if (mcomCon != null) {
				mcomCon.close("TotalUserIncomeDetailsViewController#process");
				mcomCon = null;
			}
	        if (_log.isDebugEnabled()) {
	            _log.debug("process", " Exited ");
	        }
	    }

}
	
	
	

}
