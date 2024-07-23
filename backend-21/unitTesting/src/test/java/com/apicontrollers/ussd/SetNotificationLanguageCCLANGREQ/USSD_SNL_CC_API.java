package com.apicontrollers.ussd.SetNotificationLanguageCCLANGREQ;

import java.util.HashMap;

import com.utils._APIUtil;

public class USSD_SNL_CC_API {


	final String MSISDN1 = "MSISDN1";
	final String PIN = "PIN";
	final String LANGUAGE1 = "LANGUAGE1";
	
	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public static final String TXNID = "COMMAND.TXNID";
	
	/**
	 * @category RoadMap Set Notification API
	 * @author simarnoor.bains
	 */
	private final String API_SetNotification_CC_API = "<?xml version=\"1.0\"?><COMMAND>"
			+ "<TYPE>CCLANGREQ</TYPE>"
			+ "<MSISDN1></MSISDN1>"
			+ "<PIN></PIN>"
			+ "<LANGUAGE1></LANGUAGE1>"
			+ "</COMMAND>";

	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_SetNotification_CC_API;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}
	
}
