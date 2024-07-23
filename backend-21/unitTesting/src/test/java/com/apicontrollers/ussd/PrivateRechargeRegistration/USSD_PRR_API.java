package com.apicontrollers.ussd.PrivateRechargeRegistration;

import java.util.HashMap;

import com.utils._APIUtil;

public class USSD_PRR_API {

    final String MSISDN = "MSISDN";
	final String SID = "SID";
	final String NEWSID = "NEWSID";
	final String LANGUAGE1 = "LANGUAGE1";
	public final String TYPE="TYPE";
	
	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public static final String TXNID = "COMMAND.TXNID";
	
	/**
	 * @category RoadMap Private Recharge API
	 * @author simarnoor.bains
	 */
	private final String API_PrivateRechargeAPI = "<?xml version=\"1.0\"?><COMMAND>"
			+ "<TYPE>SIDREQ</TYPE>"
			+ "<MSISDN></MSISDN>"
			+ "<SID></SID>"
			+ "<NEWSID></NEWSID>"
			+ "<LANGUAGE1></LANGUAGE1>"
            + "</COMMAND>";

	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_PrivateRechargeAPI;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}


}
