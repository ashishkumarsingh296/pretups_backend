package com.apicontrollers.extgw.o2cwithdraw;

import java.util.HashMap;

import com.utils._APIUtil;

public class EXTGWO2CWAPI {

	//Request Parameters
	final String EXTNWCODE = "EXTNWCODE";
	final String MSISDN = "MSISDN";
	final String PIN = "PIN";
	final String EXTCODE = "EXTCODE";
	final String PRODUCTCODE = "PRODUCTCODE";
	final String QTY = "QTY";
	final String EXTTXNNUMBER = "EXTTXNNUMBER";
	final String EXTTXNDATE = "EXTTXNDATE";
	final String REMARKS = "REMARKS";
	
	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";

	/**
	 * @category RoadMap O2C Withdraw API
	 * @author krishan.chawla
	 */
	private final String API_O2CWithdraw_RMP = "<COMMAND>"
			+ "<TYPE>O2CWDREQ</TYPE>"
			+ "<EXTNWCODE></EXTNWCODE>"
			+ "<MSISDN></MSISDN>"
			+ "<PIN></PIN>"
			+ "<EXTCODE></EXTCODE>"
			+ "<PRODUCTS>"
			+ "<PRODUCTCODE>101</PRODUCTCODE>"
			+ "<QTY></QTY>"
			+ "</PRODUCTS>"
			+ "<EXTTXNNUMBER></EXTTXNNUMBER>"
			+ "<EXTTXNDATE></EXTTXNDATE>"
			+ "<REMARKS></REMARKS>"
			+ "</COMMAND>";
	
	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_O2CWithdraw_RMP;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}
	
}
