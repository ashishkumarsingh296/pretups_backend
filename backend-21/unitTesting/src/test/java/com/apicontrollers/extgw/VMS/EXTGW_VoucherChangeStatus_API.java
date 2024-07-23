package com.apicontrollers.extgw.VMS;

import java.util.HashMap;

import com.utils._APIUtil;

public class EXTGW_VoucherChangeStatus_API {

    //Request Parameters
	public final String TYPE = "TYPE";
	public final String FROM_SERIALNO = "FROM_SERIALNO";
	public final String TO_SERIALNO = "TO_SERIALNO";
	public final String MASTER_SERIALNO = "MASTER_SERIALNO";
	public final String MSISDN = "MSISDN";
	public final String STATUS = "STATUS";
	public final String PIN = "PIN";
	public final String EXTCODE = "EXTCODE";
	public final String LOGINID = "LOGINID";
	public final String PASSWORD = "PASSWORD";
	public final String EXTNWCODE = "EXTNWCODE";

	//Response Parameters
	public final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public final String RECORD = "COMMAND.RECORD";
	public final String PRODUCTCODE = "COMMAND.RECORD.PRODUCTCODE";
	public final String BALANCE = "COMMAND.RECORD.BALANCE";
	
	private final String API_VoucherChangeStatus_RMP = "<?xml version=\"1.0\"?><COMMAND>"
			+ "<TYPE>VOMSSTCHGREQ</TYPE>"
			+ "<FROM_SERIALNO></FROM_SERIALNO>"
			+ "<TO_SERIALNO></TO_SERIALNO>"
			+ "<STATUS></STATUS>"
			+ "<MASTER_SERIALNO></MASTER_SERIALNO>"
			+ "<MSISDN></MSISDN>"
			+ "<PIN></PIN>"
			+ "<LOGINID></LOGINID>"
            + "<PASSWORD></PASSWORD>"
			+ "<EXTCODE></EXTCODE>"
			+ "<EXTNWCODE></EXTNWCODE>"
			+ "</COMMAND>";
	
	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_VoucherChangeStatus_RMP;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}

}
