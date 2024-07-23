package com.apicontrollers.extgw.P2PHistory;

import java.util.HashMap;

import com.utils._APIUtil;

public class EXTGW_P2PHistoryAPI {
	
	
	
	final String MSISDN1 = "MSISDN1";
	final String LANGUAGE1 = "LANGUAGE1";
	final String  PIN = "PIN";
	
	
	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public static final String TXNID = "COMMAND.TXNID";
	
	/**
	 * @category RoadMap Transfer API
	 * @author shallu
	 */
	private final String API_TransferAPI = "<?xml version=\"1.0\"?><COMMAND>"
			+ "<TYPE>CCHISREQ</TYPE>"
			+ "<MSISDN1></MSISDN1>"
			+"<PIN></PIN>"
			+ "<LANGUAGE1></LANGUAGE1>"
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
