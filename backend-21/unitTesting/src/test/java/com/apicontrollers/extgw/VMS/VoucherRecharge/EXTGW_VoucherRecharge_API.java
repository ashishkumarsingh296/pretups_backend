package com.apicontrollers.extgw.VMS.VoucherRecharge;

import java.util.HashMap;

import com.utils._APIUtil;

public class EXTGW_VoucherRecharge_API {
    //Request Parameters
	
	public final String TYPE = "TYPE";
	public final String DATE = "DATE";
	public final String PIN = "PIN";
	public final String SNO = "SNO";
	public final String SUBID = "SUBID";
	public final String EXTNWCODE = "EXTNWCODE";
	public final String EXTREFNUM = "EXTREFNUM";
	public final String LANGUAGE1 = "LANGUAGE1";

	//Response Parameters
	public final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public final String SERIALNO = "COMMAND.SNO";
	public final String PROFILE = "COMMAND.PROFILE";
	public final String SUBSCRIBERMSISDN = "COMMAND.SUBID";
	public final String TXNID = "COMMAND.TXNID";
	public final String MESSAGE = "COMMAND.MESSAGE";
	
	
	private final String API_VoucherRecharge = "<?xml version=\"1.0\"?><COMMAND>"
			+"<TYPE>VOMSDCONSREQ</TYPE>"
			+"<DATE></DATE>"
			+"<PIN></PIN>"
			+"<SNO></SNO>"
			+"<SUBID></SUBID>"
			+"<EXTNWCODE></EXTNWCODE>"
			+"<EXTREFNUM></EXTREFNUM>"
			+"<LANGUAGE1></LANGUAGE1>"
			+"</COMMAND>";

	
	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_VoucherRecharge;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}



}
