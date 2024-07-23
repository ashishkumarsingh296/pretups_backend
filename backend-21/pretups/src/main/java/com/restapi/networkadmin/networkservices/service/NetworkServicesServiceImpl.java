package com.restapi.networkadmin.networkservices.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.BaseResponse;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileDAO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.NetworkServiceDAO;
import com.btsl.pretups.master.businesslogic.NetworkServiceVO;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.restapi.networkadmin.commissionprofile.requestVO.CommissionProfileCombinedVONew;
import com.restapi.networkadmin.networkservices.requestVO.UpdateNetworkServicesRequestVO;
import com.restapi.networkadmin.networkservices.responseVO.NetworkServicesDataResponseVO;
import com.restapi.networkadmin.networkservices.responseVO.ServiceTypeListResponseVO;
import com.web.pretups.channel.profile.businesslogic.CommissionProfileWebDAO;

@Service("NetworkServicesServiceI")
public class NetworkServicesServiceImpl implements NetworkServicesServiceI{

	public static final Log log = LogFactory.getLog(NetworkServicesServiceImpl.class.getName());
	public static final String classname = "NetworkServicesServiceImpl";
	
	
	@Override
	public ServiceTypeListResponseVO loadServiceTypeList(MultiValueMap<String, String> headers,
			HttpServletResponse response1, Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO,
			ServiceTypeListResponseVO response) throws BTSLBaseException {
		
		final String METHOD_NAME = "loadServiceTypeList";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		
		ServiceKeywordDAO serviceKeywordDAO = null;
		ArrayList moduleList;
		ArrayList serviceTypeList;
		try {
			serviceKeywordDAO = new ServiceKeywordDAO();
			moduleList = LookupsCache.loadLookupDropDown(PretupsI.MODULE_TYPE, true);
			if (moduleList.isEmpty()) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LIST_NOT_FOUND, 0, null);
			}
			
			serviceTypeList=serviceKeywordDAO.loadServiceTypeListForNetworkServices(con);
			if (serviceTypeList.isEmpty()) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LIST_NOT_FOUND, 0, null);
			}
			
			response.setModuleList(moduleList);
			response.setServiceTypeList(serviceTypeList);
			
            String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LIST_FOUND, null);
            response.setMessageCode(Integer.toString(HttpStatus.SC_OK));
			response.setMessage(resmsg);
			response1.setStatus(HttpStatus.SC_OK);
			response.setStatus(HttpStatus.SC_OK);
            
		}
		finally {
			if (log.isDebugEnabled()) {
				log.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
			}
		}
		return response;
	}

	
	
	
	
	
	

	@Override
	public NetworkServicesDataResponseVO loadNetworkServicesData(MultiValueMap<String, String> headers,
			HttpServletResponse response1, Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO,
			NetworkServicesDataResponseVO response, String moduleCode, String serviceTypeCode) throws BTSLBaseException {
		final String METHOD_NAME = "loadNetworkServicesData";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		
		NetworkServiceDAO networkServiceDAO = null;
		ArrayList networkServicesVOList;
		try {
			networkServiceDAO = new NetworkServiceDAO();
			
			// getting the code of selected service type.
            int index = serviceTypeCode.indexOf(":");
            String serviceType = serviceTypeCode.substring(index + 1);
            
            networkServicesVOList = networkServiceDAO.loadNetworkServicesList(con, moduleCode, serviceType, userVO.getNetworkID());
            if (networkServicesVOList.isEmpty()) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LIST_NOT_FOUND, 0, null);
			}
            response.setNetworkServicesVOList(networkServicesVOList);
            
            
            String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LIST_FOUND, null);
            response.setMessageCode(Integer.toString(HttpStatus.SC_OK));
			response.setMessage(resmsg);
			response1.setStatus(HttpStatus.SC_OK);
			response.setStatus(HttpStatus.SC_OK);
		}
		finally {
			if (log.isDebugEnabled()) {
				log.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
			}
		}
		return response;
	}








	@Override
	public BaseResponse updateNetworkServices(MultiValueMap<String, String> headers, HttpServletResponse response1,
			Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, BaseResponse response,
			UpdateNetworkServicesRequestVO updateNetworkServicesRequestVO) throws BTSLBaseException, SQLException {
		final String METHOD_NAME = "updateNetworkServices";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		
		NetworkServiceDAO networkServiceDAO = null;
        NetworkServiceVO networkServiceVO = null;
        int updateCount = 0;
		try {
			 for (int i = 0, j = updateNetworkServicesRequestVO.getNetworkServicesVOList().size(); i < j; i++) {
                 networkServiceVO = (NetworkServiceVO) updateNetworkServicesRequestVO.getNetworkServicesVOList().get(i);
                 if (!BTSLUtil.isNullString(networkServiceVO.getStatus())) {
                     networkServiceVO.setStatus(networkServiceVO.getStatus().trim());
                 }
                 if (!BTSLUtil.isNullString(networkServiceVO.getLanguage1Message())) {
                     networkServiceVO.setLanguage1Message(networkServiceVO.getLanguage1Message().trim());
                 }
                 if (!BTSLUtil.isNullString(networkServiceVO.getLanguage2Message())) {
                     networkServiceVO.setLanguage2Message(networkServiceVO.getLanguage2Message().trim());
                 }
             }
			
			 Date currentDate = new Date();
             networkServiceVO = new NetworkServiceVO();
			 
             // set the loging user information as created by/on and modified
             // by/on
             networkServiceVO.setCreatedBy(userVO.getUserID());
             networkServiceVO.setCreatedOn(currentDate);
             networkServiceVO.setModifiedBy(userVO.getUserID());
             networkServiceVO.setModifiedOn(currentDate);

             // set the list of all network services for the updation or
             // insertation.
             networkServiceVO.setNetworkServicesVOList(updateNetworkServicesRequestVO.getNetworkServicesVOList());
			 
             networkServiceDAO = new NetworkServiceDAO();
             updateCount = networkServiceDAO.updateNetworkServices(con, networkServiceVO);
			
             if (con != null) {
                 if (updateCount > 0) {
                    /* con.commit();*/
                 	mcomCon.finalCommit();
                     AdminOperationVO adminOperationVO = null;
                     ArrayList servicesList = updateNetworkServicesRequestVO.getNetworkServicesVOList();
                     for (int i = 0, j = servicesList.size(); i < j; i++) {
                         networkServiceVO = (NetworkServiceVO) servicesList.get(i);
                         // log the data in adminOperationLog.log
                         adminOperationVO = new AdminOperationVO();
                         adminOperationVO.setSource(TypesI.LOGGER_NETWORK_SERVICE_SOURCE);
                         adminOperationVO.setDate(currentDate);
                         adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
                         adminOperationVO.setInfo("Network (" + userVO.getNetworkName() + ") Service (" + networkServiceVO.getServiceType() + ")  Status (" + networkServiceVO.getStatus() + ") has successfully modified");
                         adminOperationVO.setLoginID(userVO.getLoginID());
                         adminOperationVO.setUserID(userVO.getUserID());
                         adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                         adminOperationVO.setNetworkCode(userVO.getNetworkID());
                         adminOperationVO.setMsisdn(userVO.getMsisdn());
                         AdminOperationLog.log(adminOperationVO);
                         EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "NetworkServiceAction[updateNetworkServices]", "", "", "", "Message Network (" + userVO.getNetworkName() + ") Service (" + networkServiceVO.getServiceType() + ")  Status (" + networkServiceVO.getStatus() + ") has successfully modified");
                     }

                     //BTSLMessages btslMessage = new BTSLMessages("master.confirmnetworkservices.msg.success", "selectservicetypepage");
                     //return super.handleMessage(btslMessage, request, mapping);
                     
                     String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SERVICES_STATUS_UPDATE_SUCCESS, null);
                     response.setMessageCode(Integer.toString(HttpStatus.SC_OK));
          			 response.setMessage(resmsg);
          			 response1.setStatus(HttpStatus.SC_OK);
          			 response.setStatus(HttpStatus.SC_OK);
          			 
                 } else {
                    /* con.rollback();*/
                 	mcomCon.finalRollback();
                    // throw new BTSLBaseException(this, "updateNetworkServices", "master.confirmnetworkservices.msg.success", "updatenetworkservices");
                 	throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.SERVICES_STATUS_UPDATE_FAIL);
                 }
             }
             
		}
		finally {
			if (log.isDebugEnabled()) {
				log.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
			}
		}
		return response;
	}
	
	
	
	
	
	

}
