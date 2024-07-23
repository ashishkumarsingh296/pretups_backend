package com.apicontrollers.extgw.PrivateRecharge;

import java.util.HashMap;

import com.utils._APIUtil;

public class EXTGW_SID_API {

    public final String MSISDN = "MSISDN";
	public final String TYPE="TYPE";
	
	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public static final String TXNID = "COMMAND.TXNID";
	
	/**
	 * @category RoadMap SID API
	 * @author simarnoor.bains
	 */
	private final String API_SID_API = "<?xml version=\"1.0\"?><COMMAND>"
			+ "<TYPE>ENQSID</TYPE>"
			+ "<MSISDN></MSISDN>"
            + "</COMMAND>";

	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_SID_API;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}


}
