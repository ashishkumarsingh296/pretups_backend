package com.apicontrollers.extgw.VMS;

import java.util.HashMap;

import com.utils._APIUtil;

public class EXTGW_VoucherConsumption_API {

    //Request Parameters
	
	public final String TYPE = "TYPE";
	public final String DATE = "DATE";
	public final String EXTNWCODE = "EXTNWCODE";
	public final String MSISDN = "MSISDN";
	public final String PIN = "PIN";
	public final String MSISDN2 = "MSISDN2";
	public final String AMOUNT = "AMOUNT";
	public final String LOGINID = "LOGINID";
	public final String PASSWORD = "PASSWORD";
	public final String EXTCODE = "EXTCODE";
	public final String EXTREFNUM = "EXTREFNUM";
	public final String VOUCHERCODE = "VOUCHERCODE";
	public final String SERIALNUMBER = "SERIALNUMBER";
	public final String LANGUAGE1 = "LANGUAGE1";
	public final String LANGUAGE2 = "LANGUAGE2";
	public final String SELECTOR = "SELECTOR";
	public final String INFO1 = "INFO1";
	public final String INFO2 = "INFO2";
	public final String INFO3 = "INFO3";
	public final String INFO4 = "INFO4";
	public final String INFO5 = "INFO5";
	
	//Response Parameters
	public final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public final String RECORD = "COMMAND.RECORD";
	public final String PRODUCTCODE = "COMMAND.RECORD.PRODUCTCODE";
	public final String BALANCE = "COMMAND.RECORD.BALANCE";
	
	/**
	 * @category RoadMap Voucher Consumption
	 * @author simarnoor.bains
	 */
	private final String API_VoucherConsumption_RMP = "<?xml version=\"1.0\"?><COMMAND>"
			+ "<TYPE>VOMSCONSREQ</TYPE>"
			+ "<DATE></DATE>"
			+ "<EXTNWCODE></EXTNWCODE>"
			+ "<MSISDN></MSISDN>"
			+ "<PIN></PIN>"
			+ "<MSISDN2></MSISDN2>"
			+ "<AMOUNT></AMOUNT>"
			/*+ "<LOGINID></LOGINID>"
            + "<PASSWORD></PASSWORD>"*/
            + "<EXTCODE></EXTCODE>"
			+ "<EXTREFNUM></EXTREFNUM>"
			+ "<VOUCHERCODE></VOUCHERCODE>"
			+ "<SERIALNUMBER></SERIALNUMBER>"
			+ "<LANGUAGE1></LANGUAGE1>"
			+ "<LANGUAGE2></LANGUAGE2>"
			+ "<SELECTOR></SELECTOR>"
			+ "<INFO1>Voucher</INFO1>"
			+ "<INFO2>REcharge</INFO2>"
			+ "<INFO3>By</INFO3>"
			+ "<INFO4>Others</INFO4>"
			+ "<INFO5>Rightel</INFO5>"
			+ "</COMMAND>";
	
	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_VoucherConsumption_RMP;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}

}
