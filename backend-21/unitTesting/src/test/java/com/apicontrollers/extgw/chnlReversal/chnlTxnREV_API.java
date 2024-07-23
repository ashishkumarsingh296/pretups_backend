package com.apicontrollers.extgw.chnlReversal;

import java.util.HashMap;

import com.utils._APIUtil;

public class chnlTxnREV_API {
	
	final String EXTNWCODE = "EXTNWCODE";
	final String LANGUAGE1 = "LANGUAGE1";
	final String LOGINID = "LOGINID";
	final String PASSWORD = "PASSWORD";
	final String EXTREFNUM = "EXTREFNUM";
	final String TRANSACTIONID = "TRANSACTIONID";
	final String DATE = "DATE";
	final String CATCODE = "CATCODE";
	final String EMPCODE = "EMPCODE";
	final String REMARKS = "REMARKS";
	//Response Parameters
		public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
		
	
	
	/**
	 * @category Channel C2C reversal API
	 * @author akanksha.gupta
	 */
	private final String API_C2CTxnREV = "<?xml version=\"1.0\"?><COMMAND><TYPE>C2CREVREQ</TYPE><DATE></DATE><EXTNWCODE></EXTNWCODE><CATCODE></CATCODE><EMPCODE></EMPCODE><LOGINID></LOGINID><PASSWORD></PASSWORD><EXTREFNUM></EXTREFNUM><DATA><TRANSACTIONID></TRANSACTIONID><REMARK>testing</REMARK></DATA></COMMAND>";
	
	/**
	 * @category Channel O2C reversal API
	 * @author akanksha.gupta
	 */
	private final String API_O2CTxnREV = "<?xml version=\"1.0\"?><COMMAND><TYPE>O2CREVREQ</TYPE><DATE></DATE><EXTNWCODE></EXTNWCODE><CATCODE></CATCODE><EMPCODE></EMPCODE><LOGINID></LOGINID><PASSWORD></PASSWORD><EXTREFNUM></EXTREFNUM><DATA><TRANSACTIONID></TRANSACTIONID><REMARK>testing</REMARK></DATA></COMMAND>";
	
	
	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getC2CREVAPI() {
		return API_C2CTxnREV;
	}
	
	public String prepareC2CREVAPI(HashMap<String, String> Map) {
		String API = getC2CREVAPI();
		return _APIUtil.buildAPI(API, Map);
	}


	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getO2CREVAPI() {
		return API_O2CTxnREV;
	}
	
	public String prepareO2CREVAPI(HashMap<String, String> Map) {
		String API = getO2CREVAPI();
		return _APIUtil.buildAPI(API, Map);
	}
}
