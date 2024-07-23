package com.apicontrollers.extgw.o2ctransfer;

import java.util.HashMap;

import com.utils._APIUtil;

public class EXTGWO2CAPI {
	
	//Request Parameters
	public final String EXTNWCODE = "EXTNWCODE";
	public final String MSISDN = "MSISDN";
	public final String PIN = "PIN";
	public final String EXTCODE = "EXTCODE";
	public final String EXTTXNNUMBER = "EXTTXNNUMBER";
	public final String EXTTXNDATE = "EXTTXNDATE";
	public final String PRODUCTCODE = "PRODUCTCODE";
	public final String QTY = "QTY";
	public final String TRFCATEGORY = "TRFCATEGORY";
	public final String REFNUMBER = "REFNUMBER";
	public final String PAYMENTTYPE = "PAYMENTTYPE";
	public final String PAYMENTINSTNUMBER = "PAYMENTINSTNUMBER";
	public final String PAYMENTDATE = "PAYMENTDATE";
	public final String REMARKS = "REMARKS";
	
	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public static final String TXNID = "COMMAND.TXNID";
	public static final String EXTTXNNO = "COMMAND.EXTTXNNUMBER";
	/**
	 * @category RoadMap O2C Transfer API
	 * @author krishan.chawla
	 */
	private final String API_O2CTransfer_RMP = "<?xml version=\"1.0\"?><COMMAND>"
			+ "<TYPE>O2CINTREQ</TYPE>"
			+ "<EXTNWCODE></EXTNWCODE>"
			+ "<MSISDN></MSISDN>"
			+ "<PIN></PIN>"
			+ "<EXTCODE></EXTCODE>"
			+ "<EXTTXNNUMBER></EXTTXNNUMBER>"
			+ "<EXTTXNDATE></EXTTXNDATE>"
			+ "<PRODUCTS><PRODUCTCODE>101</PRODUCTCODE>"
			+ "<QTY></QTY></PRODUCTS><TRFCATEGORY></TRFCATEGORY><REFNUMBER></REFNUMBER>"
			+ "<PAYMENTDETAILS><PAYMENTTYPE></PAYMENTTYPE><PAYMENTINSTNUMBER></PAYMENTINSTNUMBER>"
			+ "<PAYMENTDATE></PAYMENTDATE></PAYMENTDETAILS><REMARKS></REMARKS></COMMAND>";
	
	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_O2CTransfer_RMP;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}
}
