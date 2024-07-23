package com.apicontrollers.ussd.InquiryByTxnID;

import java.util.HashMap;

import com.pretupsControllers.BTSLUtil;

public class USSDPlain_Inquiry_API {

	final String MSISDN1 = "MSISDN1";
	final String TXNID = "TXNID";
	final String PIN = "PIN";
	
	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public static final String TXNID1 = "COMMAND.TXNID";
	
	/**
	 * @category RoadMap Inquiry API
	 * @author simarnoor.bains
	 */
	private final String API_InquiryAPI = "TYPE=TXENQREQ";
	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_InquiryAPI;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return BTSLUtil.getQueryString(API, dataMap);
	}
	


}
