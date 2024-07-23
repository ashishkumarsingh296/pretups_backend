package com.apicontrollers.ussd.P2PAccInfo;

import java.util.HashMap;

import com.utils._APIUtil;

public class USSD_AccInfoAPI {
	
	
	
	final String MSISDN1 = "MSISDN1";
	final String SELECTOR = "SELECTOR";
	
	
	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public static final String TXNID = "COMMAND.TXNID";
	
	/**
	 * @category RoadMap Transfer API
	 * @author shallu
	 */
	private final String API_TransferAPI = "<?xml version=\"1.0\"?><COMMAND>"
			+ "<TYPE>CACINFREQ</TYPE>"
			+ "<MSISDN1></MSISDN1>"
			+ "<SELECTOR></SELECTOR>"
			+ "</COMMAND>";
			
			

	
	
	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_TransferAPI;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}


}
