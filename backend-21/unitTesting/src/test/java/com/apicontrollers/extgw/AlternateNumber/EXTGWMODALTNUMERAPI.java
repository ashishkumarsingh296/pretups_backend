package com.apicontrollers.extgw.AlternateNumber;

import java.util.HashMap;

import com.utils._APIUtil;

public class EXTGWMODALTNUMERAPI {
	
	final static String LOGINID = "LOGINID";
	final static String PASSWORD = "PASSWORD";
	public static final String MSISDN = "MSISDN";
	final static String PIN = "PIN";
	final static String EXTCODE = "EXTCODE";
	
	final static String EXTNWCODE = "EXTNWCODE";
	final static String LANGUAGE1 = "LANGUAGE1";
	public final String EXTREFNUM = "EXTREFNUM";
	
	public static final String ALTMSISDN = "ALTMSISDN";
	public static final String NEWMSISDN = "NEWMSISDN";
	
	final String DATE = "DATE";
	
	//Response Parameters
		public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
		public static final String TXNID = "COMMAND.TXNID";
		public static final String MESSAGE = "COMMAND.MESSAGE";
	
	/**
	 * @category RoadMap C2S Transfer API
	 * @author shallu
	 */
	private final String API_MODALTNMBR = "<?xml version=\"1.0\"?><COMMAND>"
			+ "<TYPE>MODALTNUMBER</TYPE>"
			+ "<DATE></DATE>"
			+ "<EXTNWCODE></EXTNWCODE>"
			+ "<MSISDN></MSISDN>"
			+ "<PIN></PIN>"
			+ "<LOGINID></LOGINID>"
			+ "<PASSWORD></PASSWORD>"
			+ "<EXTCODE></EXTCODE>"
			+ "<EXTREFNUM></EXTREFNUM>"
			+ "<ALTMSISDN></ALTMSISDN>"
			+ "<NEWMSISDN></NEWMSISDN>"
			+ "<LANGUAGE1></LANGUAGE1></COMMAND>";
	
	

	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_MODALTNMBR;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}
	

}
