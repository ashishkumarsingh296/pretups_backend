package com.apicontrollers.extgw.c2ctransfer;

import java.util.HashMap;

import com.utils._APIUtil;

public class EXTGWC2CAPI {

	public final String EXTNWCODE = "EXTNWCODE";
	public final String LANGUAGE1 = "LANGUAGE1";
	public final String LOGINID = "LOGINID";
	public final String PASSWORD = "PASSWORD";
	public final String EXTREFNUM = "EXTREFNUM";
	public final String LOGINID2 = "LOGINID2";
	public final String EXTCODE2 = "EXTCODE2";
	public final String MSISDN1 = "MSISDN1";
	public final String PIN = "PIN";
	public final String MSISDN2 = "MSISDN2";
	public final String EXTCODE = "EXTCODE";
	public final String PRODUCTCODE = "PRODUCTCODE";
	public final String QTY = "QTY";
	public final String DATE = "DATE";
	
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public static final String TXNID = "COMMAND.TXNID";
	public static final String EXTTXNNO = "COMMAND.EXTTXNNUMBER";
	
	/**
	 * @category RoadMap C2C Transfer API
	 * @author simarnoor.bains
	 */
	private final String API_C2CTransfer_RMP = "<?xml version=\"1.0\"?><COMMAND>"
			+ "<TYPE>EXC2CTRFREQ</TYPE>"
			+ "<EXTNWCODE></EXTNWCODE>"
			+ "<LANGUAGE1>0</LANGUAGE1>"
			+ "<LOGINID></LOGINID>"
			+ "<PASSWORD></PASSWORD>"
			+ "<EXTREFNUM></EXTREFNUM>"
			+ "<LOGINID2></LOGINID2>"
			+ "<EXTCODE2></EXTCODE2>"
			+ "<MSISDN1></MSISDN1>"
			+ "<PIN></PIN>"
			+ "<MSISDN2></MSISDN2>"
			+ "<EXTCODE></EXTCODE>"
			+ "<PRODUCTS><PRODUCTCODE></PRODUCTCODE><QTY></QTY></PRODUCTS>"
			+ "<DATE>15/01/18</DATE></COMMAND>";
	
	
	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_C2CTransfer_RMP;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}
	
}
