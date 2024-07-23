package com.apicontrollers.extgw.VMS.VoucherEnquiry;

import java.util.HashMap;

import com.utils._APIUtil;

public class EXTGW_VoucherEnquiry_API {
    //Request Parameters
	
	public final String TYPE = "TYPE";
	public final String DATE = "DATE";
	public final String PIN = "PIN";
	public final String SNO = "SNO";
	public final String EXTNWCODE = "EXTNWCODE";
	public final String EXTREFNUM = "EXTREFNUM";

	//Response Parameters
	public final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public final String SERIALNO = "COMMAND.SNO";
	public final String PROFILE = "COMMAND.PROFILE";
	public final String SUBSCRIBERMSISDN = "COMMAND.SUBID";
	public final String STATUS = "COMMAND.STATUS";
	public final String STATUS_DESCRIPTION = "COMMAND.STATUS_DESCRIPTION";
	public final String VOUCHER_EXPIRY_DATE = "COMMAND.VOUCHER_EXPIRY_DATE";
	public final String VOUCHER_CONSUMED_DATE = "COMMAND.VOUCHER_CONSUMED_DATE";
	public final String VOUCHERPROFILEID = "COMMAND.VOUCHERPROFILEID";
	
	
	private final String API_VoucherEnquiry = "<?xml version=\"1.0\"?><COMMAND>"
			+"<TYPE>VOUQRYREQ</TYPE>"
			+"<PIN></PIN>"
			+"<SNO></SNO>"
			+"<EXTNWCODE></EXTNWCODE>"
			+"<EXTREFNUM></EXTREFNUM>"
			+"</COMMAND>";

	
	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_VoucherEnquiry;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}



}
