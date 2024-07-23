package com.apicontrollers.extgw.c2sTransfer.EXTGW_FixLineRC;

import java.util.HashMap;

import com.utils._APIUtil;

public class EXTGW_FIXLINERCAPI {
	
	final String EXTNWCODE = "EXTNWCODE";
	final String LANGUAGE1 = "LANGUAGE1";
	final String LOGINID = "LOGINID";
	final String PASSWORD = "PASSWORD";
	final String EXTREFNUM = "EXTREFNUM";
	final String LANGUAGE2 = "LANGUAGE2";
	final String MSISDN = "MSISDN";
	final String PIN = "PIN";
	final String MSISDN2 = "MSISDN2";
	final String EXTCODE = "EXTCODE";
	final String AMOUNT = "AMOUNT";
	final String DATE = "DATE";
	final String SELECTOR = "SELECTOR";
	final String NOTIFICATION_MSISDN = "NOTIFICATION_MSISDN";
	
	//Response Parameters
		public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
		public static final String MESSAGE = "COMMAND.MESSAGE";
	
	/**
	 * @category RoadMap Internet Recharge API
	 * @author shallu
	 */
	private final String API_INTERNETRC = "<?xml version=\"1.0\"?><COMMAND>"
			+ "<TYPE>EXPSTNRCREQ</TYPE>"
			+"<DATE></DATE>"
			+ "<EXTNWCODE></EXTNWCODE>"
			+ "<MSISDN></MSISDN>"
			+ "<PIN></PIN>"
			+"<LOGINID></LOGINID>"
			+"<PASSWORD></PASSWORD>"
			+"<EXTCODE></EXTCODE>"
		    +"<EXTREFNUM></EXTREFNUM>"	
			+ "<MSISDN2></MSISDN2>"
			+ "<AMOUNT></AMOUNT>"
			+ "<LANGUAGE1></LANGUAGE1>"
			+ "<LANGUAGE2></LANGUAGE2>"
			+ "<SELECTOR></SELECTOR>"
			+"<NOTIFICATION_MSISDN></NOTIFICATION_MSISDN>"
			+"</COMMAND>";
				
	

	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_INTERNETRC;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}



}
