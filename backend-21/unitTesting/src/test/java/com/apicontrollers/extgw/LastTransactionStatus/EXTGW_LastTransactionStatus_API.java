package com.apicontrollers.extgw.LastTransactionStatus;

import java.util.HashMap;

import com.utils._APIUtil;

public class EXTGW_LastTransactionStatus_API {

    public final String MSISDN1 = "MSISDN1";
    public final String PIN = "PIN";
	public final String TYPE="TYPE";
	
	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public static final String TXNID = "COMMAND.TXNID";
	
	/**
	 * @category RoadMap SID API
	 * @author simarnoor.bains
	 */
	private final String API_LastTransactionStatus_API = "<?xml version=\"1.0\"?><COMMAND>"
			+ "<TYPE>PLTREQ</TYPE>"
			+ "<MSISDN1></MSISDN1>"
			+ "<PIN></PIN>"
            + "</COMMAND>";

	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_LastTransactionStatus_API;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}



}
