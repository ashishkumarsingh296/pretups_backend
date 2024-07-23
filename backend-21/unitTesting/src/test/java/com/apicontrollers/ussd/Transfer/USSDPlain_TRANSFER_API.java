package com.apicontrollers.ussd.Transfer;

import java.util.HashMap;

import com.pretupsControllers.BTSLUtil;

public class USSDPlain_TRANSFER_API {

	final String MSISDN1 = "MSISDN";
	final String MSISDN2 = "MSISDN2";
	final String TOPUPVALUE = "TOPUPVALUE";
	final String PRODUCTCODE = "PRODUCTCODE";
	final String LANGUAGE1 = "LANGUAGE1";
	final String PIN = "PIN";
	
	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public static final String TXNID = "COMMAND.TXNID";
	
	/**
	 * @category RoadMap Transfer API
	 * @author simarnoor.bains
	 */
	private final String API_TransferAPI = "TYPE=TRFREQ";

	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_TransferAPI;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return BTSLUtil.getQueryString(API, dataMap);
	}
	
}
