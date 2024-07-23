package com.apicontrollers.ussd.suspend;

import java.util.HashMap;

import com.pretupsControllers.BTSLUtil;

public class USSDPlainSUSPENDAPI {
	
	//Request Parameters
	final String COMMAND="COMMAND";
	final String TYPE="TYPE";	
	public final String MSISDN1="MSISDN1";
	public final String SWITCHID="SWITCHID";
	public final String CELLID="CELLLID";
	final String PIN = "PIN";
	
	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";

	/**
	 * @category Suspend channel user API
	 * 
	 */
	private final String API_ResetPin = "TYPE=SUSREQ";
	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_ResetPin;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return BTSLUtil.getQueryString(API, dataMap);
	}
}
