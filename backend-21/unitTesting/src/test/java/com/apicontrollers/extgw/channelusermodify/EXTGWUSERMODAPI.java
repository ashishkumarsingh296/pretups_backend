package com.apicontrollers.extgw.channelusermodify;

import java.util.HashMap;

import com.utils._APIUtil;



public class EXTGWUSERMODAPI {

	public final String DATE="DATE";
	public final String EXTNWCODE="EXTNWCODE";
	public final String EMPCODE="EMPCODE";
	public final String LOGINID="LOGINID";
	public final String PASSWORD="PASSWORD";
	public final String MSISDN="MSISDN";
	public final String PIN="PIN";
	public final String USERMSISDN="USERMSISDN";
	public final String EXTREFNUM="EXTREFNUM";
	public final String EXTERNALCODE="EXTERNALCODE";
	public final String NEWEXTERNALCODE="NEWEXTERNALCODE";
	public final String USERNAME="USERNAME";
	public final String SHORTNAME="SHORTNAME";
	public final String USERNAMEPREFIX="USERNAMEPREFIX";
	public final String SUBSCRIBERCODE="SUBSCRIBERCODE";
	public final String CONTACTPERSON="CONTACTPERSON";
	public final String CONTACTNUMBER="CONTACTNUMBER";
	public final String SSN="SSN";
	public final String ADDRESS1="ADDRESS1";
	public final String ADDRESS2="ADDRESS2";
	public final String CITY="CITY";
	public final String STATE="STATE";
	public final String COUNTRY="COUNTRY";
	public final String EMAILID="EMAILID";
	public final String WEBLOGINID="WEBLOGINID";
	public final String WEBPASSWORD="WEBPASSWORD";
	public final String MSISDN1="MSISDN1";
	public final String MSISDN2="MSISDN2";
	public final String MSISDN3="MSISDN3";
			
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
	private static String USERADDAPI ="<?xml version=\"1.0\"?>"
			+ "<COMMAND>"
			+ "<TYPE>USERMODREQ</TYPE>"
			+ "<DATE></DATE>"
			+ "<EXTNWCODE></EXTNWCODE>"
			+ "<EMPCODE></EMPCODE>"
			+ "<LOGINID></LOGINID>"
			+ "<PASSWORD></PASSWORD>"
			+ "<MSISDN></MSISDN>"
			+ "<PIN></PIN>"
			+ "<EXTREFNUM></EXTREFNUM>"
			+ "<DATA>"
			+ "<USERMSISDN></USERMSISDN>"
			+ "<EXTERNALCODE></EXTERNALCODE>"
			+ "<NEWEXTERNALCODE></NEWEXTERNALCODE>"
			+ "<USERNAME></USERNAME>"
			+ "<SHORTNAME></SHORTNAME>"
			+ "<USERNAMEPREFIX></USERNAMEPREFIX>"
			+ "<SUBSCRIBERCODE></SUBSCRIBERCODE>"
			+ "<CONTACTPERSON></CONTACTPERSON>"
			+ "<CONTACTNUMBER></CONTACTNUMBER>"
			+ "<SSN></SSN>"
			+ "<ADDRESS1></ADDRESS1>"
			+ "<ADDRESS2></ADDRESS2>"
			+ "<CITY></CITY>"
			+ "<STATE></STATE>"
			+ "<COUNTRY></COUNTRY>"
			+ "<EMAILID></EMAILID>"
			+ "<WEBLOGINID></WEBLOGINID>"
			+ "<WEBPASSWORD></WEBPASSWORD>"
			+ "<MSISDNS><MSISDN1></MSISDN1>"
			+ "<MSISDN2></MSISDN2>"
			+ "<MSISDN3></MSISDN3>"
			+ "</MSISDNS>"
			+ "</DATA></COMMAND>";
	
	private String getAPI() {
		return USERADDAPI;
	}

	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}
}
