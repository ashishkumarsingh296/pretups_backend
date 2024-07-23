package com.apicontrollers.ussd.GRCTransfer;

import java.util.HashMap;

import com.utils._APIUtil;

public class USSDGRCAPI {
	
	final String CELLID = "CELLID";
	final String MSISDN1 = "MSISDN1";
	final String PIN = "PIN";
	final String MSISDN2 = "MSISDN2";
	final String AMOUNT = "AMOUNT";
	final String LANGUAGE1 = "LANGUAGE1";
	final String LANGUAGE2 = "LANGUAGE2";
	final String SELECTOR = "SELECTOR";
	final String GIFTER_MSISDN = "GIFTER_MSISDN";
	final String GIFTER_NAME = "GIFTER_NAME";
	final String GIFTER_LANGUAGE  = "GIFTER_LANGUAGE";
	final String PRODUCT_CODE = "PRODUCT_CODE";
	
	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public static final String TXNID = "COMMAND.TXNID";
	
	/**
	 * @category RoadMap C2S Transfer API
	 * @author Shalllu
	 */
	private final String API_GRCTransfer = "<?xml version=\"1.0\"?><COMMAND>"
			+ "<TYPE>GFTRCREQ</TYPE>"
			+ "<MSISDN1></MSISDN1>"
			+ "<PIN></PIN>"
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
		return API_GRCTransfer;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}
	

}
