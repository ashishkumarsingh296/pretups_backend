package com.btsl.pretups.network.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestClient;
import com.btsl.common.PretupsRestUtil;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author anubhav.pandey1
 *
 */
@Service("changeNetworkService")
public class ChangeNetworkServiceImpl implements ChangeNetworkService{

	
	
public static final Log log = LogFactory.getLog(ChangeNetworkServiceImpl.class.getName());
	
	@Autowired
	private PretupsRestClient pretupsRestClient;
	
	private static final String LOGIN_ID = "loginId";
    private static final String DATA_LIST = "dataList";
    private static final String NETWORK_CODE = "networkCode";
	@SuppressWarnings("unchecked")
	@Override
	public List<NetworkVO> loadData(String loginId, Model model) throws BTSLBaseException {
		
		List<NetworkVO> list = null;
		try {
			if (log.isDebugEnabled()) {
				log.debug("ChangeNetworkServiceImpl#loadData", PretupsI.ENTERED);
			}
			Map<String, Object> data = new HashMap<>();
			data.put(LOGIN_ID,loginId);
			
			Map<String, Object> object = new HashMap<>();
			object.put("data", data);
		    
			String responseString = pretupsRestClient.postJSONRequest(object, PretupsI.CHANGENETWORK);
			PretupsResponse<List<NetworkVO>> response = (PretupsResponse<List<NetworkVO>>) PretupsRestUtil.convertJSONToObject(responseString, new TypeReference<PretupsResponse<List<NetworkVO>>>() {
					});
			 list =  response.getDataObject();
			if(list.isEmpty()){
				model.addAttribute("fail", response.getFormError());
				
			}
			else {
				
				 model.addAttribute(DATA_LIST, list);
				 return list;	
			}
		}catch (IOException e) {
			throw new BTSLBaseException(e);
		}
		
		if (log.isDebugEnabled()) {
			log.debug("ChangeNetworkServiceImpl#loadData", PretupsI.EXITED);
		}
		return list;
			
	
		
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void processData(UserVO userVO,String networkCode, Model model)throws BTSLBaseException {
		
		UserVO finalUserVO = null;
		try {
			if (log.isDebugEnabled()) {
				log.debug("ChangeNetworkServiceImpl#processData", PretupsI.ENTERED);
			}
			Map<String, Object> data = new HashMap<>();
			data.put(LOGIN_ID,userVO.getLoginID());
			data.put(NETWORK_CODE,networkCode);
			Map<String, Object> object = new HashMap<>();
			object.put("data", data);
			ArrayList<UserGeographiesVO> listUserVO;
			String responseString = pretupsRestClient.postJSONRequest(object, PretupsI.SUBMITCHANGENETWORK);
			PretupsResponse<UserVO> response = (PretupsResponse<UserVO>) PretupsRestUtil.convertJSONToObject(responseString, new TypeReference<PretupsResponse<UserVO>>() {});
	        finalUserVO = response.getDataObject();
	        if(finalUserVO == null)
	        {
	        	model.addAttribute("fail", response.getFormError());
	        	
	        }
	        else{
	        	model.addAttribute("success","Network"+ " " +finalUserVO.getNetworkName()+ " " +PretupsRestUtil.getMessageString("network.changenetwork.successmessage"));
	       
		        ObjectMapper objectMapper = new ObjectMapper();
		        userVO.setNetworkID(finalUserVO.getNetworkID());
		        userVO.setNetworkName(finalUserVO.getNetworkName());
		        userVO.setReportHeaderName(finalUserVO.getReportHeaderName());
		        userVO.setNetworkStatus(finalUserVO.getNetworkStatus());
		        userVO.setMessage(finalUserVO.getMessage());
		        listUserVO = objectMapper.readValue(objectMapper.writeValueAsString(finalUserVO.getGeographicalAreaList()) , new TypeReference<ArrayList<UserGeographiesVO>>() {});
		        userVO.setGeographicalAreaList(listUserVO);
	        }
	        	        
	}catch (IOException e) {
		throw new BTSLBaseException(e);
	}
	
	if (log.isDebugEnabled()) {
		log.debug("ChangeNetworkServiceImpl#processData", PretupsI.EXITED);
	}
	
 }
}
