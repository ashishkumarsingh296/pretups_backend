package com.apicontrollers.extgw.selfTPinReset_TPINRSETREQ;

import java.util.HashMap;

import com.utils._APIUtil;

public class EXTGWRESETPINAPI {
	
	//Request Parameters
	final String COMMAND="COMMAND";
	final String TYPE="TYPE";
	final String DATE="DATE";
	final String EXTNWCODE="EXTNWCODE";
	final String LOGINID="LOGINID";
	final String PASSWORD="PASSWORD";		
	final String MSISDN="MSISDN";
	final String OPERATION="OPERATION";
	final String LANGUAGE1="LANGUAGE1";
	final String REMARKS="REMARKS";
	public static final String PIN = "COMMAND.PIN";
	
	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";

	/**
	 * @category C2S Transfer API
	 * 
	 */
	private final String API_ResetPin = "<?xml version=\"1.0\"?>"
			+ "<COMMAND><TYPE>TPINRSETREQ</TYPE>"
			+ "<DATE></DATE>"
			+ "<EXTNWCODE></EXTNWCODE>"
			+ "<LOGINID></LOGINID>"
			+ "<PASSWORD></PASSWORD>"
			+ "<MSISDN></MSISDN>"
			+ "<OPERATION></OPERATION>"
			+ "<LANGUAGE1></LANGUAGE1>"
			+ "<REMARKS></REMARKS>"
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
