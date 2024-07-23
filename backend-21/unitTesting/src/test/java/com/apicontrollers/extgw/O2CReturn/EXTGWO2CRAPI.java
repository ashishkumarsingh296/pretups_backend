package com.apicontrollers.extgw.O2CReturn;

import java.util.HashMap;

import com.utils._APIUtil;

public class EXTGWO2CRAPI {
	
	//Request Parameters
	final String EXTNWCODE = "EXTNWCODE";
	final String MSISDN = "MSISDN";
	final String PIN = "PIN";
	final String EXTCODE = "EXTCODE";
	final String EXTTXNNUMBER = "EXTTXNNUMBER";
	final String EXTTXNDATE = "EXTTXNDATE";
	final String PRODUCTCODE = "PRODUCTCODE";
	final String QTY = "QTY";
	final String TRFCATEGORY = "TRFCATEGORY";
	final String REFNUMBER = "REFNUMBER";
	final String PAYMENTTYPE = "PAYMENTTYPE";
	final String PAYMENTINSTNUMBER = "PAYMENTINSTNUMBER";
	final String PAYMENTDATE = "PAYMENTDATE";
	final String REMARKS = "REMARKS";
	
	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";

	/**
	 * @category RoadMap O2C Transfer API
	 * @author krishan.chawla
	 */
	private final String API_O2CReturn_RMP = "<?xml version=\"1.0\"?><COMMAND>"
			+ "<TYPE>O2CRETREQ</TYPE>"
			+ "<EXTNWCODE></EXTNWCODE>"
			+ "<MSISDN></MSISDN>"
			+ "<PIN></PIN>"
			+ "<EXTCODE></EXTCODE>"
			+ "<PRODUCTS><PRODUCTCODE>101</PRODUCTCODE>"
			+ "<QTY></QTY></PRODUCTS>"
			+ "<EXTTXNNUMBER></EXTTXNNUMBER>"
			+ "<EXTTXNDATE></EXTTXNDATE>"
			+ "<REMARKS></REMARKS></COMMAND>";
	
	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_O2CReturn_RMP;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}
}
