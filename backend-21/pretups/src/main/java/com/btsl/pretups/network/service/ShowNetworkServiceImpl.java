package com.btsl.pretups.network.service;

import java.io.IOException;
import java.util.HashMap;
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
import com.btsl.pretups.network.businesslogic.ShowNetworkRestService;
import com.fasterxml.jackson.core.type.TypeReference;


@Service("showNetworkService")
public class ShowNetworkServiceImpl implements ShowNetworkService{

	public static final Log _log = LogFactory.getLog(ViewNetworkServiceImpl.class.getName());
	private static final String CLASS_NAME = "ShowNetworkServiceImpl";
	
	@Autowired
	private PretupsRestClient pretupsRestClient;
	private ShowNetworkRestService showNetworkRestService;
	private static final String LOOK_UP_TYPE = "lookupType";
	private static final String LOGIN_ID = "loginId";
	private static final String NETWORKCODE = "networkCode";
	
	@SuppressWarnings("unchecked")
	@Override
	public NetworkVO showData(String networkCode)throws IOException {
		final String methodName = "#showModules";
		if (_log.isDebugEnabled()) {
			_log.debug(CLASS_NAME+methodName, PretupsI.ENTERED);
		}
		Map<String, Object> data = new HashMap<>();
		
		data.put(NETWORKCODE, networkCode);
		Map<String, Object> requestObject = new HashMap<>();
		requestObject.put("data",  data);
		String responseString = pretupsRestClient.postJSONRequest(requestObject, PretupsI.SHOWNETWORKDETAIL);
		PretupsResponse<NetworkVO> response = null;
		NetworkVO networkVO = null;
		try {
			 response = (PretupsResponse<NetworkVO>) PretupsRestUtil
					.convertJSONToObject(responseString, new TypeReference<PretupsResponse<NetworkVO>>() {
					});
		} catch (IOException e) {
             _log.errorTrace(methodName, e);
		} 
		networkVO =  response.getDataObject();
		
		if (_log.isDebugEnabled()) {
			_log.debug(CLASS_NAME+methodName, PretupsI.EXITED);
		}
		return networkVO;
		
		
	}

}
