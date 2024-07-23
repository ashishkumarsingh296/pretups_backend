package com.apicontrollers.extgw.cardGroupEnquiry;

import java.util.HashMap;

import com.utils._APIUtil;

public class EXTGW_CRDGRPENQ_API {
	
	final String EXTNWCODE = "EXTNWCODE";
	final String SERVICETYPE = "SERVICETYPE";
	final String LOGINID = "LOGINID";
	final String PASSWORD = "PASSWORD";
	final String SUBSERVICE = "SUBSERVICE";
	final String MSISDN1 = "MSISDN1";
	final String PIN = "PIN";
	final String TXNID = "TXNID";
	final String AMOUNT = "AMOUNT";
	final String MSISDN2 = "MSISDN2";
	
	
	//Response Parameters
		public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
		public static final String SERVICECLASS = "COMMAND.SERVICECLASS";
		
	
	
	/**
	 * @category RoadMap C2S Transfer Status API
	 * @author shallu
	 */
	private final String API_CardGroupEnquiry = "<COMMAND>"
	 + "<TYPE>CGENQREQ</TYPE>"
	 + "<MSISDN1></MSISDN1>" //<Channel MSISDN>
		+ "<PIN></PIN>"
	+"<EXTNWCODE></EXTNWCODE>" //External Network Code
	+ "<LOGINID></LOGINID>" //<Channel user Login ID>
	+ "<PASSWORD></PASSWORD>" //<Channel User Login Password
	+"<MSISDN2></MSISDN2>" //Subscriber MSISDN
	+"<SERVICETYPE></SERVICETYPE>"
	+"<SUBSERVICE></SUBSERVICE>"
	+"<AMOUNT></AMOUNT>"
	+ "</COMMAND>";



	
	
	

	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_CardGroupEnquiry;
	}
	
	public String prepareAPI(HashMap<String, String> Map) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, Map);
	}


}
