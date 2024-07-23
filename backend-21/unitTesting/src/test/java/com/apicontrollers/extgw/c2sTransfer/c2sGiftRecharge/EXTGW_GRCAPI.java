package com.apicontrollers.extgw.c2sTransfer.c2sGiftRecharge;

import java.util.HashMap;

import com.utils._APIUtil;

public class EXTGW_GRCAPI {
	
	final String EXTNWCODE = "EXTNWCODE";
	final String LANGUAGE1 = "LANGUAGE1";
	final String LOGINID = "LOGINID";
	final String PASSWORD = "PASSWORD";
	public final String EXTREFNUM = "EXTREFNUM";
	final String LANGUAGE2 = "LANGUAGE2";
	final String MSISDN = "MSISDN";
	final String PIN = "PIN";
	final String MSISDN2 = "MSISDN2";
	final String EXTCODE = "EXTCODE";
	final String PRODUCTCODE = "PRODUCTCODE";
	final String AMOUNT = "AMOUNT";
	final String DATE = "DATE";
	final String SELECTOR = "SELECTOR";
	final String GIFTER_MSISDN = "GIFTER_MSISDN";
	final String GIFTER_NAME = "GIFTER_NAME";
	final String GIFTER_LANGUAGE  = "GIFTER_LANGUAGE";
	
	//Response Parameters
		public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
		public static final String TXNID = "COMMAND.TXNID";
	
	/**
	 * @category RoadMap C2S Transfer API
	 * @author shallu
	 */
	private final String API_C2STransfer = "<?xml version=\"1.0\"?><COMMAND>"
			+ "<TYPE>EXGFTRCREQ</TYPE>"
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
			+"<GIFTER_MSISDN></GIFTER_MSISDN>"
			+"<GIFTER_NAME></GIFTER_NAME>"
			+"<GIFTER_LANGUAGE></GIFTER_LANGUAGE></COMMAND>";
	
	

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
