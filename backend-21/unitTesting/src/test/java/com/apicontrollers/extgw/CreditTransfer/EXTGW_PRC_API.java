package com.apicontrollers.extgw.CreditTransfer;

import java.util.HashMap;

import com.utils._APIUtil;

public class EXTGW_PRC_API {
	

	public final String MSISDN1 = "MSISDN1";
	public final String MSISDN2 = "MSISDN2";
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
	private final String API_TransferAPI = "<?xml version=\"1.0\"?><COMMAND>"
			+ "<TYPE>CCTRFREQ</TYPE>"
			+ "<MSISDN1></MSISDN1>"
			+ "<PIN></PIN>"
			+ "<MSISDN2></MSISDN2>"
			+ "<AMOUNT></AMOUNT>"
			+ "<LANGUAGE1></LANGUAGE1>"
			+ "<LANGUAGE2></LANGUAGE2>"
			+ "<SELECTOR></SELECTOR>"
			+"<INFO1></INFO1>"
			+"<INFO2></INFO2>"
			+"<INFO3></INFO3>"
			+"<INFO4></INFO4>"
			+"<INFO5></INFO5>"
			+ "</COMMAND>";
			
			

	
	
	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_TransferAPI;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}

}
