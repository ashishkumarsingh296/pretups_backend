package com.apicontrollers;

import java.util.HashMap;

import com.utils.Log;

public class O2C_API {

	public static String getIdeaAPI(HashMap<String, String>apiDetails) {
		
		StringBuilder O2CAPI = new StringBuilder("<?xml version=\"1.0\"?><COMMAND>");
		O2CAPI.append("<TYPE>O2CINTREQ</TYPE>");
		
		if (apiDetails.get("EXTNWCODE") != null)
			O2CAPI.append("<EXTNWCODE>"+ apiDetails.get("EXTNWCODE") +"</EXTNWCODE>");
		else
			O2CAPI.append("<EXTNWCODE></EXTNWCODE>");
		
		if (apiDetails.get("MSISDN") != null)
			O2CAPI.append("<MSISDN>"+ apiDetails.get("MSISDN") +"</MSISDN>");
		else
			O2CAPI.append("<MSISDN></MSISDN>");
		
		if (apiDetails.get("USERORIGINID") != null)
			O2CAPI.append("<USERORIGINID>"+ apiDetails.get("USERORIGINID") +"</USERORIGINID>");
		else
			O2CAPI.append("<USERORIGINID></USERORIGINID>");
		
		if (apiDetails.get("PIN") != null)
			O2CAPI.append("<PIN>"+ apiDetails.get("PIN") +"</PIN>");
		else
			O2CAPI.append("<PIN></PIN>");
		
		if (apiDetails.get("EXTCODE") != null)
			O2CAPI.append("<EXTCODE>"+ apiDetails.get("EXTCODE") +"</EXTCODE>");
		else
			O2CAPI.append("<EXTCODE></EXTCODE>");
		
		if (apiDetails.get("EXTTXNNUMBER") != null)
			O2CAPI.append("<EXTTXNNUMBER>"+ apiDetails.get("EXTTXNNUMBER") +"</EXTTXNNUMBER>");
		else
			O2CAPI.append("<EXTTXNNUMBER></EXTTXNNUMBER>");
		
		if (apiDetails.get("EXTTXNDATE") != null)
			O2CAPI.append("<EXTTXNDATE>"+ apiDetails.get("EXTTXNDATE") +"</EXTTXNDATE>");
		else
			O2CAPI.append("<EXTTXNDATE></EXTTXNDATE>");
		
		O2CAPI.append("<PRODUCTS>");
		
		if (apiDetails.get("PRODUCTCODE") != null)
			O2CAPI.append("<PRODUCTCODE>"+ apiDetails.get("PRODUCTCODE") +"</PRODUCTCODE>");
		else
			O2CAPI.append("<PRODUCTCODE></PRODUCTCODE>");
		
		if (apiDetails.get("QTY") != null)
			O2CAPI.append("<QTY>"+ apiDetails.get("QTY") +"</QTY>");
		else
			O2CAPI.append("<QTY></QTY>");
		
		O2CAPI.append("</PRODUCTS>");
		
		if (apiDetails.get("TRFCATEGORY") != null)
			O2CAPI.append("<TRFCATEGORY>"+ apiDetails.get("TRFCATEGORY") +"</TRFCATEGORY>");
		else
			O2CAPI.append("<TRFCATEGORY></TRFCATEGORY>");
		
		if (apiDetails.get("REFNUMBER") != null)
			O2CAPI.append("<REFNUMBER>"+ apiDetails.get("REFNUMBER") +"</REFNUMBER>");
		else
			O2CAPI.append("<REFNUMBER></REFNUMBER>");
		
		O2CAPI.append("<PAYMENTDETAILS>");
		
		if (apiDetails.get("PAYMENTTYPE") != null)
			O2CAPI.append("<PAYMENTTYPE>"+ apiDetails.get("PAYMENTTYPE") +"</PAYMENTTYPE>");
		else
			O2CAPI.append("<PAYMENTTYPE></PAYMENTTYPE>");
		
		if (apiDetails.get("PAYMENTINSTNUMBER") != null)
			O2CAPI.append("<PAYMENTINSTNUMBER>"+ apiDetails.get("PAYMENTINSTNUMBER") +"</PAYMENTINSTNUMBER>");
		else
			O2CAPI.append("<PAYMENTINSTNUMBER></PAYMENTINSTNUMBER>");
		
		if (apiDetails.get("PAYMENTDATE") != null)
			O2CAPI.append("<PAYMENTDATE>"+ apiDetails.get("PAYMENTDATE") +"</PAYMENTDATE>");
		else
			O2CAPI.append("<PAYMENTDATE></PAYMENTDATE>");
		
		if (apiDetails.get("NETPAYABLEAMOUNT") != null)
			O2CAPI.append("<NETPAYABLEAMOUNT>"+ apiDetails.get("NETPAYABLEAMOUNT") +"</NETPAYABLEAMOUNT>");
		else
			O2CAPI.append("<NETPAYABLEAMOUNT></NETPAYABLEAMOUNT>");
		
		O2CAPI.append("</PAYMENTDETAILS>");
		
		if (apiDetails.get("REMARKS") != null)
			O2CAPI.append("<REMARKS>"+ apiDetails.get("REMARKS") +"</REMARKS>");
		else
			O2CAPI.append("<REMARKS></REMARKS>");
		
		O2CAPI.append("</COMMAND>");
		
		Log.info("<pre><b>Prepared API:<br></b><xmp>" + O2CAPI.toString() + "</xmp></pre>");
		return O2CAPI.toString();
	}
}
