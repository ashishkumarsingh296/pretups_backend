package com.apicontrollers.ussd.AddBuddy;

import java.util.HashMap;

import com.utils._APIUtil;

public class USSD_AddBuddyAPI {

	
	final String MSISDN1 = "MSISDN1";
	final String BUDDYNAME = "BUDDYNAME";
	final String MSISDN2 = "MSISDN2";
	final String PRFAMT = "PRFAMT";
	final String PIN = "PIN";
	
	
	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public static final String TXNID = "COMMAND.TXNID";
	
	/**
	 * @category RoadMap Transfer API
	 * @author shallu
	 */
	private final String API_TransferAPI = "<?xml version=\"1.0\"?><COMMAND>"
			+ "<TYPE>ADDBUDDYREQ</TYPE>"
			+ "<MSISDN1></MSISDN1>"
			+ "<BUDDYNAME></BUDDYNAME>"
			+ "<MSISDN2></MSISDN2>"
			+ "<PRFAMT></PRFAMT>"
			+ "<PIN></PIN>"
			+ "</COMMAND>";
			

	
	
	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_TransferAPI;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}
	
	
}
