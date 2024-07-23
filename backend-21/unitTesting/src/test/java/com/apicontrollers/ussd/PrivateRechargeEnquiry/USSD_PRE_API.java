package com.apicontrollers.ussd.PrivateRechargeEnquiry;

import java.util.HashMap;

import com.utils._APIUtil;

public class USSD_PRE_API {

    final String MSISDN = "MSISDN";
	final String SID = "SID";
	final String NEWSID = "NEWSID";
	final String LANGUAGE1 = "LANGUAGE1";
	public final String TYPE="TYPE";
	
	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public static final String TXNID = "COMMAND.TXNID";
	
	/**
	 * @category RoadMap Private Recharge Enquiry API
	 * @author simarnoor.bains
	 */
	private final String API_PrivateRechargeEnquiryAPI = "<?xml version=\"1.0\"?><COMMAND>"
			+ "<TYPE>ENQSID</TYPE>"
			+ "<MSISDN></MSISDN>"
            + "</COMMAND>";

	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_PrivateRechargeEnquiryAPI;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}





	

}
