package com.apicontrollers.ussd.MultipleEVD;

import java.util.HashMap;

import com.pretupsControllers.BTSLUtil;

public class USSDPlain_MEVD_API {

    final String MSISDN1 = "MSISDN1";
	final String PIN = "PIN";
	final String MSISDN2 = "MSISDN2";
	final String AMOUNT = "AMOUNT";
	final String LANGUAGE1 = "LANGUAGE1";
	final String LANGUAGE2 = "LANGUAGE2";
	final String SELECTOR = "SELECTOR";
	final String QTY = "QTY";
	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public static final String TXNID = "COMMAND.TXNID";
	
	/**
	 * @category RoadMap Multiple EVD API
	 * @author simarnoor.bains
	 */
	private final String API_MEVDAPI = "TYPE=MVDREQ";
	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_MEVDAPI;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return BTSLUtil.getQueryString(API, dataMap);
	}
}
