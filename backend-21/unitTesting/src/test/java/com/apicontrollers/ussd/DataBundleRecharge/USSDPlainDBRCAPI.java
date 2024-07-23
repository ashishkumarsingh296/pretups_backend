package com.apicontrollers.ussd.DataBundleRecharge;

import java.util.HashMap;

import com.pretupsControllers.BTSLUtil;

public class USSDPlainDBRCAPI {
	
	public final String MSISDN = "MSISDN";
	public final String PIN = "PIN";
	public final String MSISDN2 = "MSISDN2";
	public final String AMOUNT = "AMOUNT";
	public final String SELECTOR = "SELECTOR";
	
	//Response Parameters
	public static final String TXNSTATUS = "TXNSTATUS";
	public static final String TXNID = "TXNID";

	private final String API_DBRC = "TYPE=DBRCTRFREQ";
	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_DBRC;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return BTSLUtil.getQueryString(API, dataMap);
	}
	
	public String prepareAPI(String apiTypeTag,HashMap<String, String> dataMap) {
		return BTSLUtil.getQueryString(apiTypeTag, dataMap);
	}

}
