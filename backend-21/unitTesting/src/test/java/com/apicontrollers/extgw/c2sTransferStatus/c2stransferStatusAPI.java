package com.apicontrollers.extgw.c2sTransferStatus;

import java.util.HashMap;

import com.utils._APIUtil;

public class c2stransferStatusAPI {
	
	
 public	final  String EXTNWCODE = "EXTNWCODE";
 public	final  String LANGUAGE1 = "LANGUAGE1";
 public	final String LOGINID = "LOGINID";
 public	final  String PASSWORD = "PASSWORD";
 public	final  String EXTREFNUM = "EXTREFNUM";
 public	final String MSISDN = "MSISDN";
 public	final  String PIN = "PIN";
 public	final String EXTCODE = "EXTCODE";
 public	final  String TXNID = "TXNID";
 public	final  String AMOUNT = "AMOUNT";
 public	final String DATE = "DATE";
	
	
	//Response Parameters
		public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
		
	
	
	/**
	 * @category RoadMap C2S Transfer Status API
	 * @author shallu
	 */
	private final String API_C2STransferStatus = "<?xml version=\"1.0\"?><COMMAND>"
	 + "<TYPE>EXRCSTATREQ</TYPE>"
	+ "<DATE></DATE>"
	+"<EXTNWCODE></EXTNWCODE>" //External Network Code
	+ "<MSISDN></MSISDN>" //<Retailer MSISDN>
	+ "<PIN></PIN>"
	+ "<LOGINID></LOGINID>" //<Channel user Login ID>
	+ "<PASSWORD></PASSWORD>" //<Channel User Login Password
	+ "<EXTCODE></EXTCODE>" //< Channel user unique External code>
	+ "<EXTREFNUM></EXTREFNUM>" //<Unique Reference number in the external system>
	+ "<TXNID></TXNID>" //<PreTUPS TXN ID>
	+ "<LANGUAGE1></LANGUAGE1>" //<Retailer Language>
	+ "</COMMAND>";


	
	

	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_C2STransferStatus;
	}
	
	public String prepareAPI(HashMap<String, String> Map) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, Map);
	}

	
}
