package com.apicontrollers.ussd.FixedLineRecharge;

import java.util.HashMap;

import com.pretupsControllers.BTSLUtil;

public class USSDPlain_FixedLine_API {

    final String MSISDN1 = "MSISDN1";
	final String PIN = "PIN";
	final String MSISDN2 = "MSISDN2";
	final String AMOUNT = "AMOUNT";
	final String LANGUAGE1 = "LANGUAGE1";
	final String LANGUAGE2 = "LANGUAGE2";
	final String NOTIFICATION_MSISDN = "NOTIFICATION_MSISDN";
	final String SELECTOR = "SELECTOR";
	
	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public static final String TXNID = "COMMAND.TXNID";
	
	/**
	 * @category RoadMap Fixed Line Recharge API
	 * @author simarnoor.bains
	 */
	private final String API_FixedLineAPI = "TYPE=PSTNRCTRFREQ";

	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_FixedLineAPI;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return BTSLUtil.getQueryString(API, dataMap);
	}
	
	
}
