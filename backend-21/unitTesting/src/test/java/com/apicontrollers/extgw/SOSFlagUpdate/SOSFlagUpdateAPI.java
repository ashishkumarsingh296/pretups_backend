package com.apicontrollers.extgw.SOSFlagUpdate;

import java.util.HashMap;

import com.utils._APIUtil;

public class SOSFlagUpdateAPI {
	public final String DATE = "DATE";
	public final String EXTNWCODE = "EXTNWCODE";
	public final String MSISDN = "MSISDN";
	public final String PIN = "PIN";
	public final String LOGINID = "LOGINID";
	public final String PASSWORD = "PASSWORD";
	public final String EXTCODE = "EXTCODE";
	public final String EXTREFNUM = "EXTREFNUM";
	public final String MSISDN2 = "MSISDN2";
	public final String SOSALLOWED = "SOSALLOWED";
	public final String SOSALLOWEDAMOUNT = "SOSALLOWEDAMOUNT";
	public final String SOSTHRESHOLDLIMIT = "SOSTHRESHOLDLIMIT";
	public final String SOSTXNID = "SOSTXNID";
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public static final String TXNID = "COMMAND.TXNID";
	public static final String EXTTXNNO = "COMMAND.EXTTXNNUMBER";
	
	/**
	 * @category RoadMap C2C Transfer API
	 * @author simarnoor.bains
	 */
	private final String API_SOSFlagUpdateAPI = "<?xml version=\"1.0\"?><COMMAND>"
			+ "<TYPE>SOSFLAGUPDATEREQ</TYPE>"
			+ "<DATE>1/17/2019 12:01:21 PM</DATE>"
			+ "<EXTNWCODE></EXTNWCODE>"
			+ "<MSISDN></MSISDN>"
			+ "<PIN></PIN>"
			+ "<LOGINID></LOGINID>"
			+ "<PASSWORD></PASSWORD>"
			+ "<EXTCODE></EXTCODE>"
			+ "<EXTREFNUM></EXTREFNUM>"
			+ "<MSISDN2></MSISDN2>"
			+ "<SOSALLOWED></SOSALLOWED>"
			+ "<SOSALLOWEDAMOUNT></SOSALLOWEDAMOUNT>"
			+ "<SOSTHRESHOLDLIMIT></SOSTHRESHOLDLIMIT>"
			+ "<SOSTXNID></SOSTXNID></COMMAND>";
			
	
	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_SOSFlagUpdateAPI;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}
	
}
