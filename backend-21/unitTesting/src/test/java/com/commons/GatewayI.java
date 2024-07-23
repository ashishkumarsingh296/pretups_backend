package com.commons;

import java.util.Arrays;
import java.util.HashMap;

import com.utils.Decrypt;
import com.utils.Log;
import com.utils._masterVO;

public class GatewayI {

	public final static String EXTGW = "EXTGW";
	public final static String SMSC = "SMSC";
	public final static String USSD = "USSD";
	public final static String USSD_PLAIN = "USSD_PLAIN";
	public final static String XMLGW = "XMLGW";
	
	public static HashMap<String, String> getGatewayInfo(String gatewayType) {
		final String methodname = "getGatewayInfo";
		
		Log.debug("Entered " + methodname + "(" + gatewayType + ")");
		HashMap<String, String> gatewayInfo = new HashMap<String, String>();
		
		for (int i =0; i<_masterVO.gatewayObject.length; i++) {
			if (_masterVO.gatewayObject[i][1].equals(gatewayType)) {
				gatewayInfo.put("REQUEST_GATEWAY_CODE", _masterVO.gatewayObject[i][0].toString());
				gatewayInfo.put("REQUEST_GATEWAY_TYPE", _masterVO.gatewayObject[i][1].toString());
				gatewayInfo.put("LOGIN", _masterVO.gatewayObject[i][2].toString());
				gatewayInfo.put("PASSWORD", _masterVO.gatewayObject[i][3].toString());
				gatewayInfo.put("SOURCE_TYPE", _masterVO.gatewayObject[i][4].toString());
				gatewayInfo.put("SERVICE_PORT", _masterVO.gatewayObject[i][5].toString());
				gatewayInfo.put("REQ_PASSWORD_PLAIN", _masterVO.gatewayObject[i][6].toString());
				break;
			}
		}
		Log.debug("Exiting " + methodname + "(" + Arrays.asList(gatewayInfo) + ")");
		return gatewayInfo;
	}
	
	
	public static String getPOSTURL(String gateway, String serviceName) {
		
		HashMap<String, String> gatewayInfo = getGatewayInfo(gateway);
		
		StringBuilder PostRequestURL = new StringBuilder("/pretups/");
		PostRequestURL.append(serviceName);
		PostRequestURL.append("?REQUEST_GATEWAY_CODE=" + gatewayInfo.get("REQUEST_GATEWAY_CODE"));
		PostRequestURL.append("&REQUEST_GATEWAY_TYPE=" + gatewayInfo.get("REQUEST_GATEWAY_TYPE"));
		PostRequestURL.append("&LOGIN=" + gatewayInfo.get("LOGIN"));
		if(gatewayInfo.get("REQ_PASSWORD_PLAIN").equalsIgnoreCase("N"))
			PostRequestURL.append("&PASSWORD=" + Decrypt.APIEncryption(Decrypt.decryption(gatewayInfo.get("PASSWORD"))));
		else
			PostRequestURL.append("&PASSWORD=" + Decrypt.decryption(gatewayInfo.get("PASSWORD")));
		PostRequestURL.append("&SOURCE_TYPE=" + gatewayInfo.get("SOURCE_TYPE"));
		PostRequestURL.append("&SERVICE_PORT=" + gatewayInfo.get("SERVICE_PORT"));
		
		return PostRequestURL.toString();
	}
	
	public static boolean isServiceExist(String gateway, String serviceType) {
		if (gateway.equals(EXTGW) && _masterVO.getEXTGWServices().contains(serviceType)) {
			return true;
		} else if (gateway.equals(USSD) && _masterVO.getUSSDServices().contains(serviceType)) {
			return true;
		} else
			return false;
	}
}
