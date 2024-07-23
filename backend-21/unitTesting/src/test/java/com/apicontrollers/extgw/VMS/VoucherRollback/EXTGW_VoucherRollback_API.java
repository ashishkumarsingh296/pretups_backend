package com.apicontrollers.extgw.VMS.VoucherRollback;

import java.util.HashMap;

import com.utils._APIUtil;

public class EXTGW_VoucherRollback_API {
    //Request Parameters
	
	public final String TYPE = "TYPE";
	public final String DATE = "DATE";
	public final String PIN = "PIN";
	public final String SNO = "SNO";
	public final String SUBID = "SUBID";
	public final String EXTREFNUM = "EXTREFNUM";
	public final String EXTNWCODE = "EXTNWCODE";
	public final String STATE_CHANGE_REASON = "STATE_CHANGE_REASON";
	public final String LANGUAGE1 = "LANGUAGE1";
	

	//Response Parameters
	public final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public final String PRE_STATE = "COMMAND.PRE_STATE";
	public final String CUR_STATE = "COMMAND.CUR_STATE";
	public final String SERIALNO = "COMMAND.SNO";
	public final String SUBSCRIBERID = "COMMAND.SUBID";
	public final String VOUCHERPIN = "COMMAND.PIN";
	
	private final String API_VoucherRollback = "<?xml version=\"1.0\"?><COMMAND>"
			+"<TYPE>VOMSROLLBACKREQ</TYPE>"
			+"<DATE></DATE>"
			+"<PIN></PIN>"
			+"<SNO></SNO>"
			+"<SUBID></SUBID>"
			+"<EXTNWCODE></EXTNWCODE>"
			+"<EXTREFNUM></EXTREFNUM>"
			+"<LANGUAGE1></LANGUAGE1>"
			+"<STATE_CHANGE_REASON></STATE_CHANGE_REASON>"
			+"</COMMAND>";

	
	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_VoucherRollback;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}



}
