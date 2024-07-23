package com.apicontrollers.extgw.selfpinreset;

import java.util.HashMap;

import com.utils._APIUtil;

public class EXTGWSELFRESETPINAPI {
	
	//Request Parameters
	final String COMMAND="COMMAND";
	final String TYPE="TYPE";
	//final String DATE="DATE";
	final static String EXTNWCODE="EXTNWCODE";
	final static String LOGINID="LOGINID";
	final static String PASSWORD="PASSWORD";		
	final static String MSISDN="MSISDN";
	final static String OPERATION="OPERATION";
	final String LANGUAGE1="LANGUAGE1";
	final static String PIN="PIN";
	//final String REMARKS="REMARKS";
	final static String EXTCODE="EXTCODE";
	//public static final String PIN = "COMMAND.PIN";
	
	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";

	/**
	 * @category C2S Transfer API
	 * 
	 */
	private final String API_ResetPin = "<?xml version=\"1.0\"?>"
			+ "<COMMAND><TYPE>SELFPINRESETREQ</TYPE>"
			//+ "<DATE></DATE>"
			+ "<EXTNWCODE></EXTNWCODE>"
			+ "<LOGINID></LOGINID>"
			+ "<PASSWORD></PASSWORD>"
			+ "<MSISDN></MSISDN>"
			+ "<PIN></PIN>"
			+ "<EXTCODE></EXTCODE>"
			+ "<OPERATION></OPERATION>"
			+ "<LANGUAGE1></LANGUAGE1>"
			//+ "<REMARKS></REMARKS>"
			+ "</COMMAND>";
	
	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_ResetPin;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}
}
