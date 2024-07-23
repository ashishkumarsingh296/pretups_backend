package com.apicontrollers.extgw.suspendResume_SRCUSRREQEX;

import java.util.HashMap;

import com.utils._APIUtil;

public class EXTGWSUSPENDRESUMEAPI {
	
	//Request Parameters
	final String COMMAND="COMMAND";
	final String TYPE="TYPE";
	final String NETWORK="NETWORK";		
	public final String MSISDN1="MSISDN1";
	public final String MSISDN2="MSISDN2";
	public final String ACTION="ACTION";
	final String LANGUAGE1="LANGUAGE1";
	
	final String PIN = "PIN";
	
	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";

	/**
	 * @category Suspend Resume channel user API
	 * 
	 */
	private final String API_ResetPin = "<?xml version=\"1.0\"?><COMMAND>"
			+ "<TYPE>SRCUSRREQEX</TYPE>"
			+ "<MSISDN1></MSISDN1>"
			+ "<NETWORK></NETWORK>"
			+ "<PIN></PIN>"
			+ "<MSISDN2></MSISDN2>"
			+ "<ACTION></ACTION>"
			+ "<LANGUAGE1></LANGUAGE1>"
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
