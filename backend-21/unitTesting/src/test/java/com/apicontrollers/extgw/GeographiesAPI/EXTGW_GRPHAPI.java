package com.apicontrollers.extgw.GeographiesAPI;

import java.util.HashMap;

import com.utils._APIUtil;

public class EXTGW_GRPHAPI {



    //Request Parameters
	final String DATE = "DATE";
	final String EXTNWCODE = "EXTNWCODE";
	final String MSISDN = "MSISDN";
	final String USERLOGINID = "USERLOGINID";
	final String CATCODE = "CATCODE";
	final String EXTCODE = "EXTCODE";
	final String EXTREFNUM = "EXTREFNUM";
	final String GEOCODE = "GEOCODE";
	
	
	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public static final String PRODUCTCODE = "COMMAND.RECORD.PRODUCTCODE";
	public static final String BALANCE = "COMMAND.RECORD.BALANCE";
	
	/**
	 * @category RoadMap User Balance API
	 * @author krishan.chawla
	 */
	private final String API_Geographies_RMP = "<?xml version=\"1.0\"?><COMMAND>"
			+ "<TYPE>EXTGRPH</TYPE>"
			+ "<DATE></DATE>"
			+ "<EXTNWCODE></EXTNWCODE>"
			+ "<EXTREFNUM></EXTREFNUM>"
			+ "<CATCODE></CATCODE>"
			+ "<MSISDN></MSISDN>"
			+ "<USERLOGINID></USERLOGINID>"
			+ "<EXTCODE></EXTCODE>"
			+ "<GEOCODE></GEOCODE>"
			+ "</COMMAND>";
	
	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_Geographies_RMP;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}


}
