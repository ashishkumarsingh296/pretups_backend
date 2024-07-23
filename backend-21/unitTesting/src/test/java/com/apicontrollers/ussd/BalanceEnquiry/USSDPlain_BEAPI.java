package com.apicontrollers.ussd.BalanceEnquiry;

import java.util.HashMap;

import com.pretupsControllers.BTSLUtil;

public class USSDPlain_BEAPI {

	final String MSISDN1 = "MSISDN1";
	final String PIN = "PIN";
	final String TYPE = "TYPE";
	
	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public static final String TXNID = "COMMAND.TXNID";
	
	/**
	 * @category RoadMap Set Notification API
	 * @author simarnoor.bains
	 */
	private final String API_BalanceEnquiryAPI = "TYPE=BALREQ";
	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_BalanceEnquiryAPI;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return BTSLUtil.getQueryString(API, dataMap);
	}
}
