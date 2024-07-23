package com.btsl.pretups.network.service;


import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.validator.ValidatorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestClient;
import com.btsl.common.PretupsRestUtil;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.subscriber.service.BarredUserServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;

@Service("networkStatusService")
public class NetworkStatusServiceImpl implements NetworkStatusService {
	
	
public static final Log _log = LogFactory.getLog(BarredUserServiceImpl.class.getName());
	
	@Autowired
	private PretupsRestClient pretupsRestClient;
	
	
	private static final String LOGIN_ID = "loginId";
    private static final String NETWORK_STATUS = "newNetworkStatus";
    private static final String NETWORK_CODE = "newNetworkCode";
    private static final String LANGUAGE1MESSAGE = "newLanguage1Message";
    private static final String LANGUAGE2MESSAGE = "newLanguage2Message";
    private static final String DATA_LIST = "dataList";
	/**
	 * @param userType
	 * @return
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<NetworkVO> loadData(String loginId) throws BTSLBaseException, IOException, SQLException, ValidatorException, SAXException {
		List<NetworkVO> list =null;
		try {
		if (_log.isDebugEnabled()) {
			_log.debug("NetworkStatusServiceImpl#loadData", PretupsI.ENTERED);
		}
		
		Map<String, Object> data = new HashMap<>();
		data.put(LOGIN_ID,loginId);
	
		Map<String, Object> object = new HashMap<>();
		object.put("data", data);
		String responseString = pretupsRestClient.postJSONRequest(object, PretupsI.NETWORKSTATUS);
		PretupsResponse<List<NetworkVO>> response = (PretupsResponse<List<NetworkVO>>) PretupsRestUtil
				.convertJSONToObject(responseString, new TypeReference<PretupsResponse<List<NetworkVO>>>() {
				});
		 list =  response.getDataObject();
		}catch (IOException e) {
			throw new BTSLBaseException(e);
		}
		
		if (_log.isDebugEnabled()) {
			_log.debug("NetworkStatusServiceImpl#loadData", PretupsI.EXITED);
		}
		return list;
 }
	
	
	@Override
	public String[] statusArray(List<NetworkVO> networkList)
	{
		 
		 String[] statusArr = new String[networkList.size()];
         if (!networkList.isEmpty()) {
            
             for (int i = 0, j = networkList.size(); i < j; i++) {
            	 NetworkVO networkVO  =  networkList.get(i);
                 statusArr[i] = networkVO.getStatus();
	}
             
}
         return statusArr;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean processData(NetworkVO networkVO, String loginId, Model model)throws BTSLBaseException
	{
		try {
			if (_log.isDebugEnabled()) {
				_log.debug("NetworkStatusServiceImpl#processData", PretupsI.ENTERED);
			}
			Map<String, Object> data = new HashMap<>();
			
			
			String[] newNetworkCode = null;
			if(networkVO.getNewNetworkStatus()!=null) {
				String[] networkStatus=networkVO.getNewNetworkStatus();
			 newNetworkCode= new String[networkVO.getNewNetworkStatus().length];
			int networkVONewNetwors=networkVO.getNewNetworkStatus().length;
			for(int i=0;i<networkVONewNetwors;i++)
			{
				newNetworkCode[i]=networkStatus[i].split(":")[1];
			}
			}
		
			data.put(LOGIN_ID,loginId);
			data.put(NETWORK_CODE,newNetworkCode);
			data.put(LANGUAGE1MESSAGE, networkVO.getNewLanguage1Message());
			data.put(LANGUAGE2MESSAGE, networkVO.getNewLanguage2Message());
			Map<String, Object> object = new HashMap<>();
			object.put("data", data);
		    
			String responseString = pretupsRestClient.postJSONRequest(object, PretupsI.SAVENETWORKSTATUS);
			PretupsResponse<List<NetworkVO>> response = (PretupsResponse<List<NetworkVO>>) PretupsRestUtil
					.convertJSONToObject(responseString, new TypeReference<PretupsResponse<List<NetworkVO>>>() {
					});
			 List<NetworkVO> list =  response.getDataObject();
			if(list.isEmpty()){
				model.addAttribute("fail", response.getFormError());
				return false;
				
			}
			else {
				model.addAttribute("success",PretupsRestUtil.getMessageString("network.networkstatus.successmessage"));
				 model.addAttribute(DATA_LIST, list);
					
			}
		}catch (IOException e) {
			throw new BTSLBaseException(e);
		}
		
		if (_log.isDebugEnabled()) {
			_log.debug("NetworkStatusServiceImpl#loadData", PretupsI.EXITED);
		}
			
		return true;
	}
}

	
	
	
	