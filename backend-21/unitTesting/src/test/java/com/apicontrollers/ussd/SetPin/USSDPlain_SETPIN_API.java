package com.apicontrollers.ussd.SetPin;

import java.util.HashMap;

import com.pretupsControllers.BTSLUtil;

public class USSDPlain_SETPIN_API {

    final String MSISDN1 = "MSISDN1";
	final String PIN = "PIN";
	final String NEWPIN = "NEWPIN";
	final String CONFIRMPIN = "CONFIRMPIN";
	final String LANGUAGE1 = "LANGUAGE1";
	public final String TYPE="TYPE";
	
	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public static final String TXNID = "COMMAND.TXNID";
	
	/**
	 * @category RoadMap Set Pin API
	 * @author simarnoor.bains
	 */
	private final String API_SetPinAPI = "TYPE=RCPNREQ";

	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_SetPinAPI;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return BTSLUtil.getQueryString(dataMap);
	}

}
