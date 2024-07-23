package com.apicontrollers.extgw.LendMeBalance;

import java.util.HashMap;

import com.utils._APIUtil;

public class LMB_API {
	

	final String MSISDN1 = "MSISDN1";
	final String AMOUNT = "AMOUNT";
	final String LANGUAGE1 = "LANGUAGE1";
	final String LANGUAGE2 = "LANGUAGE2";
	final String CELLID = "CELLID";
	final String SWITCHID = "SWITCHID";
	
	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public static final String TXNID = "COMMAND.TXNID";
	
	/**
	 * @category RoadMap Transfer API
	 * @author shallu
	 */
	private final String API_TransferAPI = "<?xml version=\"1.0\"?><COMMAND>"
			+ "<TYPE>LMBREQ</TYPE>"
			+ "<MSISDN1></MSISDN1>"
			+ "<AMOUNT></AMOUNT>"
			+ "<LANGUAGE1></LANGUAGE1>"
			+ "<LANGUAGE2></LANGUAGE2>"
			+"<CELLID></CELLID>"
			+"<SWITCHID></SWITCHID>"
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
