package com.apicontrollers.ussd.Return;

import java.util.HashMap;

import com.pretupsControllers.BTSLUtil;

public class USSDPlain_Return_API {

	final String MSISDN1 = "MSISDN1";
	final String MSISDN2 = "MSISDN2";
	final String TOPUPVALUE = "TOPUPVALUE";
	final String PRODUCTCODE = "PRODUCTCODE";
	final String LANGUAGE1 = "LANGUAGE1";
	final String PIN = "PIN";
	
	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public static final String TXNID = "COMMAND.TXNID";
	
	/**
	 * @category RoadMap Return API
	 * @author simarnoor.bains
	 */
	private final String API_ReturnAPI = "TYPE=RETREQ";
	
	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_ReturnAPI;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return BTSLUtil.getQueryString(API, dataMap);
	}
}
