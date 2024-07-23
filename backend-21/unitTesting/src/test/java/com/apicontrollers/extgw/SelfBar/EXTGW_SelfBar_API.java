package com.apicontrollers.extgw.SelfBar;

import java.util.HashMap;

import com.utils._APIUtil;

public class EXTGW_SelfBar_API {

	final String MSISDN1 = "MSISDN1";
	
	//Response Parameters
		public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
		public static final String TXNID = "COMMAND.TXNID";
		
		/**
		 * @category RoadMap Self Bar API
		 * @author simarnoor.bains
		 */
		private final String API_SelfBarAPI = "<?xml version=\"1.0\"?><COMMAND>"
				+ "<TYPE>BARREQ</TYPE>"
				+ "<MSISDN1></MSISDN1>"
				+ "</COMMAND>";

		/**
		 * Method to handle the Version Based API Handling
		 * @return
		 */
		private String getAPI() {
			return API_SelfBarAPI;
		}
		
		public String prepareAPI(HashMap<String, String> dataMap) {
			String API = getAPI();
			return _APIUtil.buildAPI(API, dataMap);
		}
		


}
