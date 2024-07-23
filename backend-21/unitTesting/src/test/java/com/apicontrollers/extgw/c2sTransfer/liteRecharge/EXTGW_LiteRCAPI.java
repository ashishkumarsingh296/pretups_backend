package com.apicontrollers.extgw.c2sTransfer.liteRecharge;

import java.util.HashMap;

import com.utils._APIUtil;

public class EXTGW_LiteRCAPI {
	
	final String EXTNWCODE = "EXTNWCODE";
	final String LANGUAGE1 = "LANGUAGE1";
	final String LOGINID = "LOGINID";
	final String PASSWORD = "PASSWORD";
	public final String EXTREFNUM = "EXTREFNUM";
	final String LANGUAGE2 = "LANGUAGE2";
	final String MSISDN1 = "MSISDN1";
	final String PIN = "PIN";
	final String MSISDN2 = "MSISDN2";
	final String EXTCODE = "EXTCODE";
	final String PRODUCTCODE = "PRODUCTCODE";
	final String AMOUNT = "AMOUNT";
	final String DATE = "DATE";
	final String SELECTOR = "SELECTOR";
	final String SERVICECLASS = "SERVICECLASS";
	
	//Response Parameters
		public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
		public static final String MESSAGE = "COMMAND.MESSAGE";
	
	/**
	 * @category RoadMap Lite Recharge API
	 * @author shallu
	 */
	private final String API_LITERC = "<?xml version=\"1.0\"?><COMMAND>"
			+ "<TYPE>RCTRFSERREQ</TYPE>"
			+ "<EXTNWCODE></EXTNWCODE>"
			+ "<MSISDN1></MSISDN1>"
			+ "<PIN></PIN>"
			+ "<MSISDN2></MSISDN2>"
			+ "<AMOUNT></AMOUNT>"
			+ "<LANGUAGE1></LANGUAGE1>"
			+ "<LANGUAGE2></LANGUAGE2>"
			+ "<SELECTOR></SELECTOR>"
			+"<SERVICECLASS></SERVICECLASS>"
			+"</COMMAND>";
				
	

	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_LITERC;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}


}
