package com.apicontrollers.extgw.C2SRechargeReversal;

import java.util.HashMap;

import com.utils._APIUtil;

public class C2SReversal_API {
	
	final String EXTNWCODE = "EXTNWCODE";
	final String LANGUAGE1 = "LANGUAGE1";
	final String LOGINID = "LOGINID";
	final String PASSWORD = "PASSWORD";
	public final String EXTREFNUM = "EXTREFNUM";
	final String LANGUAGE2 = "LANGUAGE2";
	public final String MSISDN = "MSISDN";
	final String PIN = "PIN";
	public final String MSISDN2 = "MSISDN2";
	final String EXTCODE = "EXTCODE";
	final String SELECTOR = "SELECTOR";
	final String TXNID = "TXNID";
	
	//Response Parameters
		public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
		
	
	/**
	 * @category RoadMap C2S Reversal API
	 * @author shallu
	 */
	private final String API_C2STransfer = "<?xml version=\"1.0\"?><COMMAND>"
			+ "<TYPE>RCREVREQ</TYPE>"
			+ "<EXTNWCODE></EXTNWCODE>"
			+ "<DATE></DATE>"
			+ "<EXTNWCODE></EXTNWCODE>"
			+ "<MSISDN></MSISDN>"
			+ "<PIN></PIN>"
			+ "<LOGINID></LOGINID>"
			+ "<PASSWORD></PASSWORD>"
			+ "<EXTCODE></EXTCODE>"
			+ "<EXTREFNUM></EXTREFNUM>"
			+ "<MSISDN2></MSISDN2>"
			+ "<TXNID></TXNID>"
			+ "<LANGUAGE1></LANGUAGE1>"
			+ "<LANGUAGE2></LANGUAGE2>"
	        +"</COMMAND>";
	
	//<?xml version="1.0"?><!DOCTYPE COMMAND PUBLIC "-//Ocam//DTD XML Command 1.0//EN" "xml/command.dtd">
	


	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_C2STransfer;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}


}
