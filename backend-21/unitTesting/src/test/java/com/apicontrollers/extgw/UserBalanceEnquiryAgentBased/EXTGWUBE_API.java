package com.apicontrollers.extgw.UserBalanceEnquiryAgentBased;

import java.util.HashMap;

import com.utils._APIUtil;

public class EXTGWUBE_API {

    //Request Parameters
	final String DATE = "DATE";
	final String EXTNWCODE = "EXTNWCODE";
	final String MSISDN = "MSISDN";
	final String MSISDN2 = "MSISDN2";
	final String PIN = "PIN";
	final String LOGINID = "LOGINID";
	final String PASSWORD = "PASSWORD";
	final String EXTCODE = "EXTCODE";
	final String EXTREFNUM = "EXTREFNUM";
	final String LANGUAGE1 = "LANGUAGE1";
	
	
	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public static final String RECORD = "COMMAND.RECORD";
	public static final String PRODUCTCODE = "COMMAND.RECORD.PRODUCTCODE";
	public static final String BALANCE = "COMMAND.RECORD.BALANCE";
	
	/**
	 * @category RoadMap User Balance API
	 * @author krishan.chawla
	 */
	private final String API_UserBalanceEnquiry_RMP = "<?xml version=\"1.0\"?><COMMAND>"
			+ "<TYPE>EXOTHUSRBALREQ</TYPE>"
			+ "<DATE></DATE>"
			+ "<EXTNWCODE></EXTNWCODE>"
			+ "<EXTCODE></EXTCODE>"
			+ "<LOGINID></LOGINID>"
            + "<PASSWORD></PASSWORD>"
			+ "<MSISDN></MSISDN>"
			+ "<MSISDN2></MSISDN2>"
			+ "<PIN></PIN>"
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
