package com.apicontrollers.ussd.Withdraw;

import java.util.HashMap;

import com.utils._APIUtil;

public class USSD_Withdraw_API {

	final String MSISDN1 = "MSISDN1";
	final String MSISDN2 = "MSISDN2";
	final String TOPUPVALUE = "TOPUPVALUE";
	final String PRODUCTCODE = "PRODUCTCODE";
	final String LANGUAGE1 = "LANGUAGE1";
	final String PIN = "PIN";
	
	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public static final String TXNID = "COMMAND.TXNID";
	
	/**
	 * @category RoadMap Withdraw API
	 * @author simarnoor.bains
	 */
	private final String API_WithdrawAPI = "<?xml version=\"1.0\"?><COMMAND>"
			+ "<TYPE>WDTHREQ</TYPE>"
			+ "<MSISDN1></MSISDN1>"
			+ "<MSISDN2></MSISDN2>"
			+ "<TOPUPVALUE></TOPUPVALUE>"
			+ "<PRODUCTCODE></PRODUCTCODE>"
			+ "<LANGUAGE1></LANGUAGE1>"
			+ "<PIN></PIN>"
			+ "</COMMAND>";

	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_WithdrawAPI;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}
}
