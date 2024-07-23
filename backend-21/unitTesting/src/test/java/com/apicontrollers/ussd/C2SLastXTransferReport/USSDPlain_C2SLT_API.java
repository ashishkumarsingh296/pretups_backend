package com.apicontrollers.ussd.C2SLastXTransferReport;

import java.util.HashMap;

import com.pretupsControllers.BTSLUtil;

public class USSDPlain_C2SLT_API {

	final String MSISDN1 = "MSISDN1";
	final String PIN = "PIN";
	
	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public static final String TXNID = "COMMAND.TXNID";
	
	/**
	 * @category RoadMap C2S Last Transfer API
	 * @author simarnoor.bains
	 */
	private final String API_C2SLastTransferAPI = "TYPE=LXC2STSREQ";

	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_C2SLastTransferAPI;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return BTSLUtil.getQueryString(API, dataMap);
	}
	
}
