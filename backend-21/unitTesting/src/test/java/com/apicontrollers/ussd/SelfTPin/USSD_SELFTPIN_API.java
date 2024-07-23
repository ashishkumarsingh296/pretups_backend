package com.apicontrollers.ussd.SelfTPin;

import java.util.HashMap;

import com.utils._APIUtil;

public class USSD_SELFTPIN_API {

    final String MSISDN = "MSISDN";
	public final String TYPE="TYPE";
	
	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public static final String TXNID = "COMMAND.TXNID";
	
	/**
	 * @category RoadMap Self TPin API
	 * @author simarnoor.bains
	 */
	private final String API_SelfTPinAPI = "<?xml version=\"1.0\"?><COMMAND>"
			+ "<TYPE>INPRESET</TYPE>"
			+ "<MSISDN></MSISDN>"
            + "</COMMAND>";

	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_SelfTPinAPI;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}


}
