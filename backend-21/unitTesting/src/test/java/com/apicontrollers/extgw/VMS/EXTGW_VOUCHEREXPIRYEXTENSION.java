package com.apicontrollers.extgw.VMS;

import java.util.HashMap;

import com.utils._APIUtil;

public class EXTGW_VOUCHEREXPIRYEXTENSION {
    //Request Parameters
	
	public final String TYPE = "TYPE";
	public final String DATE = "DATE";
	public final String LOGINID = "LOGINID";
	public final String PASSWORD = "PASSWORD";
	public final String EXTNWCODE = "EXTNWCODE";
	public final String EXTREFNUM = "EXTREFNUM";
	public final String VOUCHER_TYPE = "VOUCHER_TYPE";
	public final String FROM_SERIALNO = "FROM_SERIALNO";
	public final String TO_SERIALNO = "TO_SERIALNO";
	public final String NEW_EXPIRY_DATE = "NEW_EXPIRY_DATE";
	public final String EXPIRY_CHANGE_REASON = "EXPIRY_CHANGE_REASON";

	//Response Parameters
	public final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public final String RECORD = "COMMAND.RECORD";
	public final String PRODUCTCODE = "COMMAND.RECORD.PRODUCTCODE";
	public final String BALANCE = "COMMAND.RECORD.BALANCE";
	
	/**
	 * @category RoadMap Voucher Expiry Extension
	 * @author simarnoor.bains
	 */
	private final String API_VoucherExpiryExtension_RMP = "<?xml version=\"1.0\"?><COMMAND>"
			+ "<TYPE>VMSPINEXT</TYPE>"
			+ "<DATE></DATE>"
			+ "<LOGINID></LOGINID>"
			+ "<PASSWORD></PASSWORD>"
			+ "<VOUCHER_TYPE></VOUCHER_TYPE>"
			+ "<FROM_SERIALNO></FROM_SERIALNO>"
			+ "<TO_SERIALNO></TO_SERIALNO>"
			+ "<NEW_EXPIRY_DATE></NEW_EXPIRY_DATE>"
			+ "<EXTREFNUM></EXTREFNUM>"
            + "<EXTNWCODE></EXTNWCODE>"
            + "<EXPIRY_CHANGE_REASON></EXPIRY_CHANGE_REASON>"
			+ "</COMMAND>";
	
	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_VoucherExpiryExtension_RMP;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}



}
