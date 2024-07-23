package com.apicontrollers.ussd.Last5TxnForParticularMSISDN;

import java.util.HashMap;

import com.pretupsControllers.BTSLUtil;

public class USSDPlainLast5TxnWidMSISDN_API {

	final String MSISDN = "MSISDN";
	final String MSISDN2 = "MSISDN2";
	final String PIN = "PIN";
	final String TYPE = "TYPE";
	
	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public static final String TXNID = "COMMAND.TXNID";
	
	/**
	 * @category RoadMap Last 5 Transactions API
	 * @author simarnoor.bains
	 */
	private final String API_Last5TxnsWidMSISDNAPI = "TYPE=CUSTXTRFREQ";

	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_Last5TxnsWidMSISDNAPI;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return BTSLUtil.getQueryString(API, dataMap);
	}

}
