package com.apicontrollers.extgw.c2sTransfer.promoRecharge;

import java.util.HashMap;

import com.utils._APIUtil;

public class EXTGWPC2SAPI {
	
	final String DATE = "DATE";
	final String EXTNWCODE = "EXTNWCODE";
	final String MSISDN = "MSISDN";
	final String PIN = "PIN";
	final String LOGINID = "LOGINID";
	final String PASSWORD = "PASSWORD";
	final String EXTCODE = "EXTCODE";
	final String EXTREFNUM = "EXTREFNUM";
	final String MSISDN2 = "MSISDN2";
	final String AMOUNT = "AMOUNT";
	final String PRODUCTCODE = "PRODUCTCODE";
	final String LANGUAGE1 = "LANGUAGE1";
	final String LANGUAGE2 = "LANGUAGE2";
	final String SELECTOR = "SELECTOR";
	
	//Response Parameters
		public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
		public static final String TXNID = "COMMAND.TXNID";
	
	/**
	 * @category Robi Promo C2S Transfer API
	 */
	private final String API_C2STransfer_ROBI = "<?xml version=\"1.0\"?>" 
			+ "<COMMAND>"
			+ "<TYPE>PRRCTRFREQ</TYPE>" 
			+ "<DATE></DATE>" 
			+ "<EXTNWCODE></EXTNWCODE>" 
			+ "<MSISDN></MSISDN>" 
			+ "<PIN></PIN>" 
			+ "<LOGINID></LOGINID>" 
			+ "<PASSWORD></PASSWORD>" 
			+ "<EXTCODE></EXTCODE>" 
			+ "<EXTREFNUM></EXTREFNUM>" 
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
		return API_C2STransfer_ROBI;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}
}
