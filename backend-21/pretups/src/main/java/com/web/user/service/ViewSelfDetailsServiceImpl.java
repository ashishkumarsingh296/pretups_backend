package com.web.user.service;

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
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.user.businesslogic.ViewSelfDetailsRestService;
import com.fasterxml.jackson.core.type.TypeReference;

@Service("viewSelfDetailsService")
public class ViewSelfDetailsServiceImpl  implements ViewSelfDetailsService {

	public static final Log _log = LogFactory.getLog(ViewSelfDetailsServiceImpl.class.getName());
	private static final String CLASS_NAME = "ViewSelfDetailsServiceImpl";
	
	@Autowired
	private PretupsRestClient pretupsRestClient;
	private ViewSelfDetailsRestService viewSelfDetailsRestService;
	private static final String LOGIN_ID = "loginId";
	
	@SuppressWarnings("unchecked")
	@Override
	public UserVO loadData(String loginId) throws IOException {
		final String methodName = "#loadModules";
		if (_log.isDebugEnabled()) {
			_log.debug(CLASS_NAME+methodName, PretupsI.ENTERED);
		}
		Map<String, Object> data = new HashMap<>();
		data.put(LOGIN_ID,loginId);
		Map<String, Object> requestObject = new HashMap<>();
		requestObject.put("data",  data);
		String responseString = pretupsRestClient.postJSONRequest(requestObject, PretupsI.VIEWSELFDETAIL);
		PretupsResponse <ChannelUserVO> response = null;
		try {
			 response = (PretupsResponse <ChannelUserVO>) PretupsRestUtil
					.convertJSONToObject(responseString, new TypeReference<PretupsResponse<ChannelUserVO>>() {
					});
		} catch (IOException e) {

			_log.errorTrace(methodName, e);
		} 
		ChannelUserVO userVO =  response.getDataObject();
		
		
		if (_log.isDebugEnabled()) {
			_log.debug(CLASS_NAME+methodName, PretupsI.EXITED);
		}
		return userVO;
		
		
	}

	
}
