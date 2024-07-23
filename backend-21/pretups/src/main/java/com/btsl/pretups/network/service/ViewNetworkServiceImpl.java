package com.btsl.pretups.network.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestClient;
import com.btsl.common.PretupsRestUtil;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.network.businesslogic.ViewNetworkRestService;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * @author 
 *
 */
@Service("viewNetworkService")
public class ViewNetworkServiceImpl implements ViewNetworkService{
	
public static final Log _log = LogFactory.getLog(ViewNetworkServiceImpl.class.getName());
private static final String CLASS_NAME = "ViewNetworkServiceImpl";
	
	@Autowired
	private PretupsRestClient pretupsRestClient;
	private ViewNetworkRestService viewNetworkRestService;
	private static final String LOOK_UP_TYPE = "lookupType";
	private static final String LOGIN_ID = "loginId";
	private static final String Status = "status";
	private static final String NETWORK_CODE = "networkCode";

	@SuppressWarnings("unchecked")
	@Override
	public List<NetworkVO> loadData(String loginId,String status,String networkCode) throws IOException {
		final String methodName = "#loadModules";
		if (_log.isDebugEnabled()) {
			_log.debug(CLASS_NAME+methodName, PretupsI.ENTERED);
		}
		Map<String, Object> data = new HashMap<>();
		data.put(LOGIN_ID,loginId);
		data.put(Status, status);
		data.put(NETWORK_CODE,networkCode);
		Map<String, Object> requestObject = new HashMap<>();
		requestObject.put("data",  data);
		String responseString = pretupsRestClient.postJSONRequest(requestObject, PretupsI.VIEWNETWORKDETAIL);
		PretupsResponse<List<NetworkVO>> response = null;
		try {
			 response = (PretupsResponse<List<NetworkVO>>) PretupsRestUtil
					.convertJSONToObject(responseString, new TypeReference<PretupsResponse<List<NetworkVO>>>() {
					});
		} catch (IOException e) {
           _log.errorTrace(methodName, e);
		} 
		List<NetworkVO> list =  response.getDataObject();
		
		if (_log.isDebugEnabled()) {
			_log.debug(CLASS_NAME+methodName, PretupsI.EXITED);
		}
		return list;
		
	}
		
	}



