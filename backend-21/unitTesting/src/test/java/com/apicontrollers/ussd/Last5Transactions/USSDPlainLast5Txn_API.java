package com.apicontrollers.ussd.Last5Transactions;

import java.util.HashMap;

import com.pretupsControllers.BTSLUtil;

public class USSDPlainLast5Txn_API {

	final String MSISDN = "MSISDN";
	final String PIN = "PIN";
	final String TYPE = "TYPE";
	
	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public static final String TXNID = "COMMAND.TXNID";
	
	/**
	 * @category RoadMap Last 5 Transactions API
	 * @author simarnoor.bains
	 */
	private final String API_Last5TxnsAPI = "TYPE=EXLST3TRFREQ";

	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_Last5TxnsAPI;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return BTSLUtil.getQueryString(API, dataMap);
	}
	




}
