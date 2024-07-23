package com.apicontrollers.extgw.c2sTransactionEnquiry;

import java.util.HashMap;

import com.utils._APIUtil;

public class c2sTxnENQ_API {
	
	final String EXTNWCODE = "EXTNWCODE";
	final String LANGUAGE1 = "LANGUAGE1";
	final String LOGINID = "LOGINID";
	final String PASSWORD = "PASSWORD";
	final String EXTREFNUM = "EXTREFNUM";
	final String SENDERMSISDN = "SENDERMSISDN";
	final String PIN = "PIN";
	final String EXTCODE = "EXTCODE";
	final String TRANSACTIONID = "TRANSACTIONID";
	final String AMOUNT = "AMOUNT";
	final String DATE = "DATE";
	final String CATCODE = "CATCODE";
	final String EMPCODE = "EMPCODE";
	final String SRVTYPE = "SRVTYPE";
	final String FROMDATE = "FROMDATE";
	final String TODATE = "TODATE";
	final String MSISDN2 = "MSISDN2";
	
	
	//Response Parameters
		public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
		
	
	
	/**
	 * @category RoadMap C2S Transfer Enquiry API
	 * @author shallu
	 */
	private final String API_C2STxnENQ = "<?xml version=\"1.0\"?><COMMAND>"
	+"<TYPE>C2STRFANSFERENQ</TYPE>"
	+"<DATE></DATE>"
	+"<EXTNWCODE></EXTNWCODE>" //External Network Code
	+"<CATCODE></CATCODE>" // Category Code of the Operator user
	+"<EMPCODE></EMPCODE>" // Employee Code of the Operator user
	+"<LOGINID></LOGINID>" //<Channel user Login ID>
	+"<PASSWORD></PASSWORD>" //<Channel User Login Password
	+"<EXTREFNUM></EXTREFNUM>" //<Unique Reference number in the external system>
	+"<DATA>"
	+"<SRVTYPE></SRVTYPE>" //Service Type
	+"<FROMDATE></FROMDATE>" //FROMDATE
	+"<TODATE></TODATE>"
	+"<TRANSACTIONID></TRANSACTIONID>" //<PreTUPS TXN ID>
	+"<SENDERMSISDN></SENDERMSISDN>" //<Retailer MSISDN>
	+"<MSISDN2></MSISDN2>" //Receiver MSISDN
	+"</DATA>"	
	+"</COMMAND>";
	
	
	
	

	
	
	
	

	

	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_C2STxnENQ;
	}
	
	public String prepareAPI(HashMap<String, String> Map) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, Map);
	}

	
}
