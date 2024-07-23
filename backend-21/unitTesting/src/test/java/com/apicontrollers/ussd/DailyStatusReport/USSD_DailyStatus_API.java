package com.apicontrollers.ussd.DailyStatusReport;

import java.util.HashMap;

import com.utils._APIUtil;

public class USSD_DailyStatus_API {

	final String MSISDN1 = "MSISDN1";
	final String PIN = "PIN";
	
	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public static final String TXNID = "COMMAND.TXNID";
	
	/**
	 * @category RoadMap Daily Status Report API
	 * @author simarnoor.bains
	 */
	private final String API_DailyStatusReportAPI = "<?xml version=\"1.0\"?><COMMAND>"
			+ "<TYPE>DSRREQ</TYPE>"
			+ "<MSISDN1></MSISDN1>"
			+ "<PIN></PIN>"
			+ "</COMMAND>";

	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_DailyStatusReportAPI;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}
}
