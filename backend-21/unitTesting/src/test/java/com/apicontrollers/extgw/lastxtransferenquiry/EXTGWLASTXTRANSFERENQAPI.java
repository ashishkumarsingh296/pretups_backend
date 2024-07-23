package com.apicontrollers.extgw.lastxtransferenquiry;

import java.util.HashMap;

import com.utils._APIUtil;

public class EXTGWLASTXTRANSFERENQAPI {
	
	final static String LOGINID = "LOGINID";
	final static String PASSWORD = "PASSWORD";
	public static final String MSISDN = "MSISDN";
	final static String PIN = "PIN";
	final static String EXTCODE = "EXTCODE";
	
	
	final static String EXTNWCODE = "EXTNWCODE";
	final static String LANGUAGE1 = "LANGUAGE1";
	final static String LANGUAGE2 = "LANGUAGE2";
	
	final static String SERVICETYPE = "SERVICETYPE";
	final static String SELECTOR = "SELECTOR";
	final static String TXNID="TXNID";
	public static final String MSISDN2 = "MSISDN2";
	
	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public static final String MESSAGE = "COMMAND.MESSAGE";
	
	/**
	 * @category Vocuher pin Resend api
	 * @author
	 */
	private final String API_ADDALTNMBR = "<?xml version=\"1.0\"?><COMMAND>"
			+ "<TYPE>LASTXTRANS</TYPE>"
			//+ "<DATE></DATE>"
			+ "<EXTNWCODE></EXTNWCODE>"
			+ "<MSISDN></MSISDN>"
			+ "<PIN></PIN>"
			+ "<LOGINID></LOGINID>"
			+ "<PASSWORD></PASSWORD>"
			+ "<EXTCODE></EXTCODE>"
			+ "<LANGUAGE2></LANGUAGE2>"
			+ "<SERVICETYPE></SERVICETYPE>"
			+ "<SELECTOR></SELECTOR>"
			+ "<MSISDN2></MSISDN2>"
			//+ "<TXNID></TXNID>"
			+ "<LANGUAGE1></LANGUAGE1></COMMAND>";
	
	

	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_ADDALTNMBR;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}
	

}
