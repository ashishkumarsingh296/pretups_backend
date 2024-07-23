package com.apicontrollers.ussd.BalanceEnquiry;

import java.util.HashMap;

import com.utils._APIUtil;

public class USSD_BEAPI {

	final String MSISDN1 = "MSISDN1";
	final String PIN = "PIN";
	
	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public static final String TXNID = "COMMAND.TXNID";
	
	/**
	 * @category RoadMap Set Notification API
	 * @author simarnoor.bains
	 */
	private final String API_BalanceEnquiryAPI = "<?xml version=\"1.0\"?><COMMAND>"
			+ "<TYPE>BALREQ</TYPE>"
			+ "<MSISDN1></MSISDN1>"
			+ "<PIN></PIN>"
			+ "</COMMAND>";

	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_BalanceEnquiryAPI;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}
	
	
}
