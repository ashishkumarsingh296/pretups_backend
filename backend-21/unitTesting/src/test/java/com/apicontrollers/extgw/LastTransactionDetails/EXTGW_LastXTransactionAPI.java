package com.apicontrollers.extgw.LastTransactionDetails;

import java.util.HashMap;

import com.utils._APIUtil;

public class EXTGW_LastXTransactionAPI {

	public final String EXTNWCODE = "EXTNWCODE";
	public final String LANGUAGE1 = "LANGUAGE1";
	public final String LOGINID = "LOGINID";
	public final String PASSWORD = "PASSWORD";
	public final String EXTREFNUM = "EXTREFNUM";
	public final String MSISDN = "MSISDN";
	public final String PIN = "PIN";
	public final String EXTCODE = "EXTCODE";
	public final String DATE = "DATE";
	
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public static final String REQSTATUS = "COMMAND.REQSTATUS";
	/**
	 * @category RoadMap LastTransaction API
	 * @author simarnoor.bains
	 */
	private final String API_LastXTransaction_RMP = "<?xml version=\"1.0\"?><COMMAND>"
			+ "<TYPE>EXLST3TRFREQ</TYPE>"
			+ "<DATE></DATE>"
			+ "<EXTNWCODE></EXTNWCODE>"
			+ "<MSISDN></MSISDN>"
			+ "<PIN></PIN>"
			+ "<LOGINID></LOGINID>"
			+ "<PASSWORD></PASSWORD>"
			+ "<EXTCODE></EXTCODE>"
			+ "<EXTREFNUM></EXTREFNUM>"
			+ "<LANGUAGE1>0</LANGUAGE1></COMMAND>";
	
	
	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_LastXTransaction_RMP;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}
}
