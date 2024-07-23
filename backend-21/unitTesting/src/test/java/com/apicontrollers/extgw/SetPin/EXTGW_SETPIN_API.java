package com.apicontrollers.extgw.SetPin;

import java.util.HashMap;

import com.utils._APIUtil;

public class EXTGW_SETPIN_API {
	
		final String MSISDN1 = "MSISDN1";
		final String PIN = "PIN";
		final String NEWPIN = "NEWPIN";
		final String CONFIRMPIN = "CONFIRMPIN";
		final String LANGUAGE1 = "LANGUAGE1";
		public final String TYPE="TYPE";
		
		//Response Parameters
		public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
		public static final String TXNID = "COMMAND.TXNID";
		
		/**
		 * @category RoadMap Set Pin API
		 * @author Chetan.Chawla
		 */
		private final String API_SetPinAPI = "<?xml version=\"1.0\"?><COMMAND>"
				+ "<TYPE>CCPNREQ</TYPE>"
				+ "<MSISDN1></MSISDN1>"
				+ "<PIN></PIN>"
				+ "<NEWPIN></NEWPIN>"
				+ "<CONFIRMPIN></CONFIRMPIN>"
				+ "<LANGUAGE1></LANGUAGE1>"
	            + "</COMMAND>";

		/**
		 * Method to handle the Version Based API Handling
		 * @return
		 */
		private String getAPI() {
			return API_SetPinAPI;
		}
		
		public String prepareAPI(HashMap<String, String> dataMap) {
			String API = getAPI();
			return _APIUtil.buildAPI(API, dataMap);
		}
	
}
