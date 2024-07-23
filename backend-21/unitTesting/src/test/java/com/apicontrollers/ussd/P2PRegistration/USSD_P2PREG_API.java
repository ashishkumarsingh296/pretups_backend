package com.apicontrollers.ussd.P2PRegistration;

import java.util.HashMap;

import com.utils._APIUtil;

public class USSD_P2PREG_API {
	
	
	public final String MSISDN = "MSISDN1";
	 public final String TYPE="TYPE";
	 public final String SUB_TYPE = "SUB_TYPE";
		//Response Parameters
		public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
		public static final String TXNID = "COMMAND.TXNID";
		
		/**
		 * @category RoadMap Private Recharge Modification API
		 * @author simarnoor.bains
		 */
		private final String API_P2PRegistration = "<?xml version=\"1.0\"?><COMMAND>"
				+ "<TYPE>REGREQ</TYPE>"
				+ "<MSISDN1></MSISDN1>"
				+ "<SUB_TYPE>PRE</SUB_TYPE>"
	            + "</COMMAND>";

		/**
		 * Method to handle the Version Based API Handling
		 * @return
		 */
		private String getAPI() {
			return API_P2PRegistration;
		}
		
		public String prepareAPI(HashMap<String, String> dataMap) {
			String API = getAPI();
			
			return _APIUtil.buildAPI(API, dataMap);
		}

	
	
	

}
