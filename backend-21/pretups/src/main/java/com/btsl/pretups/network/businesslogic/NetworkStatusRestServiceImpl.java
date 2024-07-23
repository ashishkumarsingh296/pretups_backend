package com.btsl.pretups.network.businesslogic;


import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.validator.ValidatorException;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.network.service.NetworkStatusService;
import com.btsl.pretups.network.service.NetworkStatusServiceImpl;
import com.btsl.user.businesslogic.UserVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.web.pretups.network.businesslogic.NetworkWebDAO;



/**
 * @This class implements BarredUserRestService and provides methods for
 * processing Bar User request 
 * 
 *
 */

public class NetworkStatusRestServiceImpl implements NetworkStatusRestService{

	
	


	public static final Log log = LogFactory.getLog(NetworkStatusRestServiceImpl.class.getName());
	private static final String CLASS_NAME = "NetworkStatusRestServiceImpl";
	
	@SuppressWarnings("unchecked")
	@Override
	public PretupsResponse<List<NetworkVO>> loadnetworkStatus(String requestData)
			throws BTSLBaseException, IOException, SQLException, ValidatorException, SAXException{
		final String methodName = "#NetworkStatus";
		 Connection con = null;
		 MComConnectionI mcomCon = null;
		PretupsResponse<List<NetworkVO>> response ;
		try {
		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME+methodName, "Entered ");
		}
		
		
	    response = new PretupsResponse<>();
		JsonNode requestNode = (JsonNode) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<JsonNode>() {});
		JsonNode dataNode =  requestNode.get("data");
		
		mcomCon = new MComConnection();
		con=mcomCon.getConnection();
		 PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
		 Map<String, String> fieldError = new HashMap<>();
		 if(dataNode.get("loginId").textValue().isEmpty())
		 {
			   
			   fieldError.put("loginId","network.changeNetwork.errors.login.required");
			    response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				response.setFieldError(fieldError);
				return response; 
		 }
		 else {
			 UserVO userVO = pretupsRestUtil.getUserVOByLoginIdOrExternalCode(dataNode, con);
			 if(userVO == null)
			 {
				 fieldError.put("loginId","network.changeNetwork.errors.login.invalid");
				    response.setStatus(false);
					response.setStatusCode(PretupsI.RESPONSE_FAIL);
					response.setFieldError(fieldError);
					return response; 
			 }
	  if (TypesI.SUPER_ADMIN.equals(userVO.getCategoryCode())) {
		NetworkWebDAO networkwebDAO = new NetworkWebDAO();
        List<NetworkVO> networkList = networkwebDAO.loadNetworkStatusList(con);
        if(networkList.isEmpty()) {
        	response.setResponse(PretupsI.RESPONSE_SUCCESS, true, "List is Empty");
        }
        response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, networkList);
		}
	  else {
		  NetworkWebDAO networkwebDAO = new NetworkWebDAO();
		  String networkCode = userVO.getNetworkID();
		  NetworkVO networkVO = networkwebDAO.loadNetworkStatus(con, networkCode);
		  List networkList = new ArrayList<>();
		  networkList.add(networkVO);
		  response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, networkList);	  
	  
	  }
		 }
		}catch (BTSLBaseException | IOException  |  SQLException e) {
			throw new BTSLBaseException(e);
		}finally{
			if (mcomCon != null) {
				mcomCon.close("NetworkStatusRestServiceImpl#loadnetworkStatus");
				mcomCon = null;
			}
		 if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME+methodName, "Exiting");
			}
		}
	  return response;

	}

	
@SuppressWarnings("unchecked")
	@Override
	public PretupsResponse<List<NetworkVO>> saveNetworkStatus(String requestData)
			throws BTSLBaseException, IOException, SQLException, ValidatorException, SAXException{
				
		final String methodName = "#saveNetworkStatus";
		 Connection con = null;
		 MComConnectionI mcomCon = null;
		PretupsResponse<List<NetworkVO>> response = null ;
		List<NetworkVO> networkList1 = new ArrayList<NetworkVO>();
		NetworkStatusService networkStatusService = new NetworkStatusServiceImpl();
		response = new PretupsResponse<>();
		try {
		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME+methodName, "Entered ");
		}
			int updateCount = 0;
			
			JsonNode requestNode = (JsonNode) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<JsonNode>() {});
			NetworkVO networkVO = (NetworkVO) PretupsRestUtil.convertJSONToObject(requestNode.get("data").toString(), new TypeReference<NetworkVO>() {});
			JsonNode dataNode=requestNode.get("data");
			NetworkStatusValidator networkStatusValidator=new NetworkStatusValidator();
			networkStatusValidator.validateRequestData(networkVO, dataNode, response);
			if (response.hasFieldError()) {
				response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				return response;
			}
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			 PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
			 int size=networkVO.getNewLanguage1Message().length;
			 
			
			 Map<String, String> fieldError = new HashMap<>();
			 if(dataNode.get("loginId").textValue().isEmpty())
			 {
				   
				   fieldError.put("loginId","network.changeNetwork.errors.login.required");
				    response.setStatus(false);
					response.setStatusCode(PretupsI.RESPONSE_FAIL);
					response.setFieldError(fieldError);
					return response; 
			 }
			 else {
				 UserVO userVO = pretupsRestUtil.getUserVOByLoginIdOrExternalCode(dataNode, con);
				 if(userVO == null)
				 {
					 fieldError.put("loginId","network.changeNetwork.errors.login.invalid");
					    response.setStatus(false);
						response.setStatusCode(PretupsI.RESPONSE_FAIL);
						response.setFieldError(fieldError);
						return response; 
				 }
			 Date currentDate = new Date();
			 NetworkWebDAO networkwebDAO = new NetworkWebDAO();
			 List<NetworkVO> networkList2 = new ArrayList();
	        if(userVO.getCategoryCode().equalsIgnoreCase("SUADM"))
	        {
	        	networkList1 = networkwebDAO.loadNetworkStatusList(con);
	        	
	        }
	        else
	        	{ 
	        	NetworkVO userNetworkVO = networkwebDAO.loadNetworkStatus(con, userVO.getNetworkID());
	        	networkList1.add(userNetworkVO); 
	        	
	        	}
	        
	         String[] statusArr=null;
	         statusArr=networkStatusService.statusArray(networkList1);
	         if(statusArr.length!=size){
	        	 response.setResponse(PretupsI.RESPONSE_FAIL, false, "bad.request");
	        	 return response;
	         }
	        
	        	  if (size!=0)  {
	         
	         for(int i=0,j=0; i<size ;i++){
	        	
	                	 NetworkVO networkVO2 = networkList1.get(i);
	                	 networkVO2.setLanguage1Message(networkVO.getNewLanguage1Message()[i]);
	                     networkVO2.setLanguage2Message(networkVO.getNewLanguage2Message()[i]);
	                     networkVO2.setModifiedBy(userVO.getLoginID());
	                     networkVO2.setModifiedOn(currentDate);
	                     networkVO2.setDataListStatusOld(statusArr);

	                    
	                    if(networkVO.getNewNetworkCode()!=null && j<networkVO.getNewNetworkCode().length){
	                     if((networkVO2.getNetworkCode()).equalsIgnoreCase(networkVO.getNewNetworkCode()[j]))
	                     {
	                    	 networkVO2.setStatus("Y");
	                    	 j++;
	                     }else{
	                    	 networkVO2.setStatus("S");
	                     }
	                      }
	                     else{
	                    	 networkVO2.setStatus("S");
	                     }
	                     networkList2.add(networkVO2);
	                 }
	        	  }
	         updateCount = networkwebDAO.updateNetworkStatus(con, networkList2);
	         if (con != null) {
                 try {
                     if (updateCount > 0) {
                    	 mcomCon.finalCommit();
                         if (!networkList2.isEmpty()) {
                          
                             AdminOperationVO adminOperationVO = null;
                             for (int i = 0, j = networkList2.size(); i < j; i++) {
                            	 NetworkVO networkVO3 = null;
                            	 networkVO3 = networkList2.get(i);
                                 if (!networkVO3.getStatus().equals(networkVO3.getDataListStatusOld()[i])) {
                                     adminOperationVO = new AdminOperationVO();
                                     adminOperationVO.setSource(TypesI.LOGGER_NETWORK_SOURCE);
                                     adminOperationVO.setDate(currentDate);
                                     if (TypesI.YES.equals(networkVO3.getStatus())) {
                                         adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ACTIVATED);
                                         adminOperationVO.setInfo("Network " + networkVO3.getNetworkName() + " has activated");
                                         EventHandler.handle(EventIDI.ADMIN_OPT_NW_STATUS, EventComponentI.SYSTEM, EventStatusI.CLEARED, EventLevelI.MAJOR, "NetworkAction ["+networkVO3.getNetworkName()+" Activated]", "", "", networkVO3.getNetworkCode(), "Network " + networkVO3.getNetworkCode() + " has activated");
                                     } else {
                                         adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_SUSPENDED);
                                         adminOperationVO.setInfo("Network " + networkVO3.getNetworkName() + " has suspended");
                                         EventHandler.handle(EventIDI.ADMIN_OPT_NW_STATUS, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "NetworkAction ["+networkVO3.getNetworkName()+" Suspended]", "", "", networkVO3.getNetworkCode(), "Network " + networkVO3.getNetworkCode() + " has suspended");
                                     }
                                     adminOperationVO.setLoginID(userVO.getLoginID());
                                     adminOperationVO.setUserID(userVO.getUserID());
                                     adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                                     adminOperationVO.setNetworkCode(networkVO3.getNetworkCode());
                                     adminOperationVO.setMsisdn(userVO.getMsisdn());
                                     AdminOperationLog.log(adminOperationVO);
                               
                                 }
	         

	        	  }
				
                         }
                         BTSLMessages btslMessage = new BTSLMessages("network.networkstatus.successmessage", "list");
                         response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, networkList2);

                         

                     }
                     else {
                    	 mcomCon.finalRollback();
                         
                         response.setFormError("network.networkstatus.failedmessage");
     					response.setStatus(false);
     					response.setStatusCode(PretupsI.RESPONSE_FAIL);
     					
     					
                         log.error(methodName, "Error: while updating network status");

                       
                     }
                         
                 } catch (Exception e) {
                     log.errorTrace(methodName, e);

                 }
                 
	         }
	         
		}
		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("NetworkStatusRestServiceImpl#saveNetworkStatus");
				mcomCon = null;
			}

			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting");
			}
		}
		return response;

	}
}
			
			
			
                     
               
			
			   
		           
			
                            
                

