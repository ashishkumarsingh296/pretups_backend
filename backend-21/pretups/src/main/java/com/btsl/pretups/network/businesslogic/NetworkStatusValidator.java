package com.btsl.pretups.network.businesslogic;

import java.util.HashMap;
import java.util.Map;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.fasterxml.jackson.databind.JsonNode;

public class NetworkStatusValidator {

	public static final Log _log = LogFactory
			.getLog(NetworkStatusValidator.class.getName());
	
	
	
	
	public void validateRequestData(NetworkVO networkVO, JsonNode requestData, PretupsResponse<?> response)
			throws BTSLBaseException {
		
		String methodName = "validateRequestData";
		Map<String, String> fieldError = new HashMap<>();
		LogFactory.printLog(methodName, PretupsI.ENTERED, _log);
		try{
		String[] newNetworkCode=networkVO.getNewNetworkCode();
		String[] newLanguage1Message=networkVO.getNewLanguage1Message();
		String[] newLanguage2Message=networkVO.getNewLanguage2Message();
		
		 if(requestData.get("loginId").textValue().isEmpty())
		 {
			 fieldError.put("loginId","network.changeNetwork.errors.login.required");
			 response.setFieldError(fieldError);
			 return;
		 }
		 if(newNetworkCode != null)
		 {
		 for(int i=0;i<newNetworkCode.length;i++)
			{
				
			 if(newNetworkCode[i].isEmpty() || newNetworkCode[i].length()>2)
			 {
				 fieldError.put("newNetworkCode","network.networkStatus.errors.networkCode");
				 response.setFieldError(fieldError);
		   		 return;
			 }
			 
		    }
		 }
		if(newLanguage1Message.length == 0  || newLanguage2Message.length == 0)
		{
			fieldError.put("newLanguageMessage","network.networkstatus.errors.language.required");
			 response.setFieldError(fieldError);
			 return;
		}
		for(int i=0;i<newLanguage1Message.length;i++)
		{
			if(newLanguage1Message[i].length()>140 || newLanguage2Message[i].length()>140)
			{
				fieldError.put("newLanguageMessage","network.networkStatus.errors.language.length");
				 response.setFieldError(fieldError);
				 return;
			}
		}
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}

		finally {
			LogFactory.printLog(methodName, PretupsI.EXITED, _log);
		}
	
	}
	
	public void validateNetworkData(NetworkVO networkVO, JsonNode requestData, PretupsResponse<?> response)
			throws BTSLBaseException {
		String methodName = "validateNetworkData";
		Map<String, String> fieldError = new HashMap<>();
		LogFactory.printLog(methodName, PretupsI.ENTERED, _log);
		String networkCode = requestData.get("networkCode").textValue();
		if(networkCode.isEmpty() || networkCode.length() > 2 || networkVO.getNetworkCode().isEmpty())
		{
			fieldError.put("networkCode","network.networkStatus.errors.networkCode");
			 response.setFieldError(fieldError);
			 return;
		}
		
	
	}
	
	
	
}
