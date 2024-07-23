package com.apicontrollers.ussd.SetPinCP2P;

import java.util.HashMap;

import com.pretupsControllers.BTSLUtil;

public class USSDPlain_SP_CP2P_API {

    final String MSISDN1 = "MSISDN1";
	final String PIN = "PIN";
	final String NEWPIN = "NEWPIN";
	final String CONFIRMPIN = "CONFIRMPIN";
	final String LANGUAGE1 = "LANGUAGE1";
	public final String TYPE="TYPE";
	
	//Response Parameters
	public static final String TXNSTATUS = "TXNSTATUS";
	public static final String TXNID = "TXNID";
	
	/**
	 * @category RoadMap Set Pin API
	 * @author simarnoor.bains
	 */
	private final String API_SetPinAPI = "TYPE=CCPNREQ";
	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_SetPinAPI;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return BTSLUtil.getQueryString(API, dataMap);
	}

}
