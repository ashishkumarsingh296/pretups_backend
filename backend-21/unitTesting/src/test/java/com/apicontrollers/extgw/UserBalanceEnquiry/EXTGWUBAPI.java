package com.apicontrollers.extgw.UserBalanceEnquiry;

import java.util.HashMap;

import com.utils._APIUtil;

public class EXTGWUBAPI {


	//Request Parameters
	final String DATE = "DATE";
	final String EXTNWCODE = "EXTNWCODE";
	final String MSISDN = "MSISDN";
	final String PIN = "PIN";
	final String LOGINID = "LOGINID";
	final String PASSWORD = "PASSWORD";
	final String EXTCODE = "EXTCODE";
	final String EXTREFNUM = "EXTREFNUM";
	final String LANGUAGE1 = "LANGUAGE1";
	
	
	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public static final String PRODUCTCODE = "COMMAND.RECORD.PRODUCTCODE";
	public static final String BALANCE = "COMMAND.RECORD.BALANCE";
	public static final String RECORD = "COMMAND.RECORD";
	
	
	/**
	 * @category RoadMap User Balance API
	 * @author krishan.chawla
	 */
	private final String API_UserBalanceEnquiry_RMP = "<?xml version=\"1.0\"?><COMMAND>"
			+ "<TYPE>EXUSRBALREQ</TYPE>"
			+ "<DATE></DATE>"
			+ "<EXTNWCODE></EXTNWCODE>"
			+ "<MSISDN></MSISDN>"
			+ "<PIN></PIN>"
			+ "<LOGINID></LOGINID>"
            + "<PASSWORD></PASSWORD>"
			+ "<EXTCODE></EXTCODE>"
			+ "<EXTREFNUM></EXTREFNUM>"
			+ "<LANGUAGE1></LANGUAGE1>"
			+ "</COMMAND>";
	
	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_UserBalanceEnquiry_RMP;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}
}

