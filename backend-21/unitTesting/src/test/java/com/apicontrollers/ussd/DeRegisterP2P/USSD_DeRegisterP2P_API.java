package com.apicontrollers.ussd.DeRegisterP2P;

import java.util.HashMap;

import com.utils._APIUtil;

public class USSD_DeRegisterP2P_API {
	public final String MSISDN = "MSISDN1";
	 public final String TYPE="TYPE";
	 public final String PIN = "PIN";
		//Response Parameters
		public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
		public static final String TXNID = "COMMAND.TXNID";
		
		/**
		 * @category RoadMap Private Recharge Modification API
		 * @author simarnoor.bains
		 */
		private final String API_P2PDeRegistration = "<?xml version=\"1.0\"?><COMMAND>"
				+ "<TYPE>DREGREQ</TYPE>"
				+ "<MSISDN1></MSISDN1>"
				+ "<PIN></PIN>"
	            + "</COMMAND>";


		/**
		 * Method to handle the Version Based API Handling
		 * @return
		 */
		private String getAPI() {
			return API_P2PDeRegistration;
		}
		
		public String prepareAPI(HashMap<String, String> dataMap) {
			String API = getAPI();
			
			return _APIUtil.buildAPI(API, dataMap);
		}

	
	
	
	
	
}
