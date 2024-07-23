package com.apicontrollers.ussd.c2stransfer;

import java.util.HashMap;

import com.utils._APIUtil;

public class USSDC2SAPI {
	
	public final String CELLID = "CELLID";
	public final String MSISDN1 = "MSISDN1";
	public final String PIN = "PIN";
	public final String MSISDN2 = "MSISDN2";
	public final String AMOUNT = "AMOUNT";
	public final String LANGUAGE1 = "LANGUAGE1";
	public final String LANGUAGE2 = "LANGUAGE2";
	public final String SELECTOR = "SELECTOR";
	
	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public static final String TXNID = "COMMAND.TXNID";
	
	/**
	 * @category RoadMap C2S Transfer API
	 * @author simarnoor.bains
	 */
	private final String API_C2STransfer_IDEA = "<?xml version=\"1.0\"?><COMMAND>"
			+ "<TYPE>RCTRFREQ</TYPE>"
			+ "<CELLID></CELLID>"
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
		return API_C2STransfer_IDEA;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}
	

}
