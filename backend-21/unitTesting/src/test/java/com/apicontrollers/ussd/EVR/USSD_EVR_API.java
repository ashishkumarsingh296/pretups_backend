package com.apicontrollers.ussd.EVR;

import java.util.HashMap;

import com.utils._APIUtil;

public class USSD_EVR_API {

    final String MSISDN1 = "MSISDN1";
	final String PIN = "PIN";
	final String MSISDN2 = "MSISDN2";
	final String AMOUNT = "AMOUNT";
	final String LANGUAGE1 = "LANGUAGE1";
	final String LANGUAGE2 = "LANGUAGE2";
	final String SELECTOR = "SELECTOR";
	
	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public static final String TXNID = "COMMAND.TXNID";
	
	/**
	 * @category RoadMap EVR API
	 * @author simarnoor.bains
	 */
	private final String API_EVRAPI = "<?xml version=\"1.0\"?><COMMAND>"
			+ "<TYPE>EVRTRFREQ</TYPE>"
			+ "<MSISDN1></MSISDN1>"
			+ "<PIN></PIN>"
			+ "<MSISDN2></MSISDN2>"
			+ "<AMOUNT></AMOUNT>"
			+ "<LANGUAGE1></LANGUAGE1>"
			+ "<LANGUAGE2></LANGUAGE2>"
			+ "<SELECTOR></SELECTOR>"
            + "</COMMAND>";

	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_EVRAPI;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}

}
