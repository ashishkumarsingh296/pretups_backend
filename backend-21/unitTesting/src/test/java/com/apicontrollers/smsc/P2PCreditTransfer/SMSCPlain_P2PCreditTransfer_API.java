package com.apicontrollers.smsc.P2PCreditTransfer;


public class SMSCPlain_P2PCreditTransfer_API {

	final String MSISDN1 = "MSISDN1";
	final String PIN = "PIN";
	final String MSISDN2 = "MSISDN2";
	final String AMOUNT = "AMOUNT";
	final String LANGUAGE1 = "LANGUAGE1";
	final String LANGUAGE2 = "LANGUAGE2";
	final String SELECTOR = "SELECTOR";
	
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public static final String TXNID = "COMMAND.TXNID";
	
	private final String API_EVDAPI = "MESSAGE=PRC";
	
	private String getAPI() {
		return API_EVDAPI;
	}
	
	public String prepareAPI(String dataString) {
		return getAPI() + "+" + dataString;
	}
}
