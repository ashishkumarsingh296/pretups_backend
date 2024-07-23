package com.apicontrollers.extgw.VMS.VoucherStatusChange;

import java.util.HashMap;

import com.utils._APIUtil;

public class EXTGW_VoucherStatusChange_API {
    //Request Parameters
	
	public final String TYPE = "TYPE";
	public final String DATE = "DATE";
	public final String FROM_SERIALNO = "FROM_SERIALNO";
	public final String TO_SERIALNO = "TO_SERIALNO";
	public final String EXTREFNUM = "EXTREFNUM";
	public final String STATUS = "STATUS";
	public final String EXTNWCODE = "EXTNWCODE";
	public final String STATE_CHANGE_REASON = "STATE_CHANGE_REASON";
	public final String LOGINID = "LOGINID";
	public final String PASSWORD = "PASSWORD";
	public final String EXTCODE = "EXTCODE";
	

	//Response Parameters
	public final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public final String PRE_STATUS = "COMMAND.PRE_STATUS";
	public final String REQ_STATUS = "COMMAND.REQ_STATUS";
	public final String FROM_SERIALNO_RESP = "COMMAND.FROM_SERIALNO";
	public final String TO_SERIALNO_RESP = "COMMAND.TO_SERIALNO";
	public final String MESSAGE = "COMMAND.MESSAGE";
	
	private final String API_VoucherStatusChange = "<?xml version=\"1.0\"?><COMMAND>"
			+"<TYPE>VOMSSTCHGREQ</TYPE>"
			+"<DATE></DATE>"
			+"<FROM_SERIALNO></FROM_SERIALNO>"
			+"<TO_SERIALNO></TO_SERIALNO>"
			+"<STATUS></STATUS>"
			+"<EXTREFNUM></EXTREFNUM>"
			+"<EXTNWCODE></EXTNWCODE>"
			+"<LOGINID></LOGINID>"
			+"<PASSWORD></PASSWORD>"
			+"<EXTCODE></EXTCODE>"
			+"<STATE_CHANGE_REASON></STATE_CHANGE_REASON>"
			+"</COMMAND>";
	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_VoucherStatusChange;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}



}
