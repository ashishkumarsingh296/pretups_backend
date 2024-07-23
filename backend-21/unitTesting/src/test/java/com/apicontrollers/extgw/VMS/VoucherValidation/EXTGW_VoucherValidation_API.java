package com.apicontrollers.extgw.VMS.VoucherValidation;

import java.util.HashMap;

import com.utils._APIUtil;

public class EXTGW_VoucherValidation_API {
    //Request Parameters
	
	public final String TYPE = "TYPE";
	public final String DATE = "DATE";
	public final String PIN = "PIN";
	public final String SNO = "SNO";
	public final String EXTNWCODE = "EXTNWCODE";
	public final String EXTREFNUM = "EXTREFNUM";
	public final String LANGUAGE1 = "LANGUAGE1";

	//Response Parameters
	public final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public final String SERIALNO = "COMMAND.SNO";
	public final String ISVALID = "COMMAND.ISVALID";
	
	/**
	 * Voucher validation Request is used for validating the Voucher PIN
	 */
	private final String API_VoucherValidation = "<?xml version=\"1.0\"?><COMMAND>"
			+"<TYPE>VOMSVALREQ</TYPE>"
			+"<DATE></DATE>"
			+"<PIN></PIN>"
			+"<SNO></SNO>"
			+"<EXTNWCODE></EXTNWCODE>"
			+"<EXTREFNUM></EXTREFNUM>"
			+"<LANGUAGE1></LANGUAGE1>"
			+"</COMMAND>";
	
	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_VoucherValidation;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}



}
