package com.apicontrollers.extgw.c2cwithdraw;

import java.util.HashMap;

import com.utils._APIUtil;

public class EXTGWC2CWAPI {

	//Request Parameters
	final String DATE="DATE";
	final String EXTNWCODE="EXTNWCODE";
	final String MSISDN1="MSISDN1";
	final String PIN="PIN";
	final String LOGINID="LOGINID";
	final String PASSWORD="PASSWORD";
	final String EXTCODE="EXTCODE";
	final String EXTREFNUM="EXTREFNUM";
	final String MSISDN2="MSISDN2";
	final String EXTCODE2="EXTCODE2";
	final String LOGINID2="LOGINID2";
	final String PRODUCTCODE="PRODUCTCODE";
	final String LANGUAGE1="LANGUAGE1";
	final String QTY="QTY";

	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";

	/**
	 * @category RoadMap C2C Withdraw API
	 * 
	 */
	private final String API_C2CWITHDRAW_RMP = "<?xml version=\"1.0\"?><COMMAND>"
			+ "<TYPE>EXC2CWDREQ</TYPE>"
			+ "<DATE></DATE>"
			+ "<EXTNWCODE></EXTNWCODE>"
			+ "<MSISDN1></MSISDN1>"
			+ "<PIN></PIN>"
			+ "<LOGINID></LOGINID>"
			+ "<PASSWORD></PASSWORD>"
			+ "<EXTCODE></EXTCODE>"
			+ "<EXTREFNUM></EXTREFNUM>"
			+ "<MSISDN2></MSISDN2>"
			+ "<EXTCODE2></EXTCODE2>"
			+ "<LOGINID2></LOGINID2>"
			+ "<PRODUCTS><PRODUCTCODE></PRODUCTCODE>"
			+ "<QTY></QTY>"
			+ "</PRODUCTS><LANGUAGE1></LANGUAGE1>"
			+ "</COMMAND>";

	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_C2CWITHDRAW_RMP;
	}

	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}
}
