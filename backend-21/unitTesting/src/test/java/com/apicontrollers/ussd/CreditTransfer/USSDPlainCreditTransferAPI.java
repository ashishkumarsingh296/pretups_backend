package com.apicontrollers.ussd.CreditTransfer;

import java.util.HashMap;

import com.pretupsControllers.BTSLUtil;

public class USSDPlainCreditTransferAPI {

	final String MSISDN1 = "MSISDN1";
	final String MSISDN2 = "MSISDN2";
	final String AMOUNT = "AMOUNT";
	final String SELECTOR = "SELECTOR";
	final String LANGUAGE1 = "LANGUAGE1";
	final String LANGUAGE2 = "LANGUAGE2";
	final String PIN = "PIN";
	final String INFO1 = "INFO1";
	final String INFO2 = "INFO2";
	final String INFO3 = "INFO3";
	final String INFO4 = "INFO4";
	final String INFO5 = "INFO5";
	
	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public static final String TXNID = "COMMAND.TXNID";
	
	/**
	 * @category RoadMap Transfer API
	 * @author shallu
	 */
	private final String API_TransferAPI = "TYPE=CCTRFREQ";
			

	
	
	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_TransferAPI;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return BTSLUtil.getQueryString(API, dataMap);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
