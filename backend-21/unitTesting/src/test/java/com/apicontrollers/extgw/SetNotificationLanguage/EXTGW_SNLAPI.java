package com.apicontrollers.extgw.SetNotificationLanguage;

import java.util.HashMap;

import com.utils._APIUtil;

public class EXTGW_SNLAPI {
	

	//Request Parameters
	final String EXTNWCODE = "EXTNWCODE";
	final String MSISDN1 = "MSISDN1";
	final String PIN = "PIN";
	final String LANGUAGE1 = "LANGUAGE1";
	
	
	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public static final String PRODUCTCODE = "COMMAND.RECORD.PRODUCTCODE";
	public static final String BALANCE = "COMMAND.RECORD.BALANCE";
	

	private final String API_SetNotificationLanguage_RMP = "<?xml version=\"1.0\"?><COMMAND>"
			+ "<TYPE>RCNLANGREQ</TYPE>"
			+ "<EXTNWCODE></EXTNWCODE>"
			+ "<MSISDN1></MSISDN1>"
			+ "<PIN></PIN>"
			+ "<LANGUAGE1></LANGUAGE1>"
			+ "</COMMAND>";
	
	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_SetNotificationLanguage_RMP;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}

}
