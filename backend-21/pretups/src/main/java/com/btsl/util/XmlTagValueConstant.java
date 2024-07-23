package com.btsl.util;

/**
 * @description : This class will be used to define & declare the member
 *              variable for tag name & it's length.
 * @author diwakar
 * @date: 28-FEB-2014
 */
public interface XmlTagValueConstant {

    // Specify Tag's length as per database
    public final int NETWORK_CODE_LENGTH = 2;
    public final int LOGIN_ID_LENGTH = 20;
    public final int EXT_CODE_LENGTH = 20;
    public final int EXTREFNUM_LENGTH = 50;
    public final int AMOUNT_LENGTH = 20;
    public final int LANGUAGE_LENGTH = 1;
    public final int SELECTOR_LENGTH = 1;
    public final int TXNID_LENGTH = 20;
    public final int MESSAGE_LENGTH = 500;
    public final int TYPE_LENGTH = 20;
    public final int PRODUCTCODE_LENGTH = 10;
    public final int QTY_LENGTH = 12;
    public final int REMARK_LENGTH = 100;
    public final int CATCODE_LENGTH = 10;
    public final int USERNAME_LENGTH = 80;
    public final int SHORTNAME_LENGTH = 15;
    public final int USERNAMEPREFIX_LENGTH = 10;
    public final int SUBSCRIBERCODE_LENGTH = 10;
    public final int CONTACTPERSON_LENGTH = 80;
    public final int SSN_LENGTH = 15;
    public final int ADDRESS_LENGTH = 50;
    public final int CITY_LENGTH = 30;
    public final int STATE_LENGTH = 30;
    public final int COUNTRY_LENGTH = 20;
    public final int EMAILID_LENGTH = 60;
    public final int EXTTXNNUMBER_LENGTH = 80;
    public final int TRFCATEGORY_LENGTH = 5;
    public final int REFNUMBER_LENGTH = 10;
    public final int PAYMENTTYPE_LENGTH = 15;
    public final int PAYMENTINSTNUMBER_LENGTH = 15;
    public final int SUBSTYPE_LENGTH = 4;
    public final int PORTTYPE_LENGTH = 3;
    // 11-MAR-2014
    public final int EMPCODE_LENGTH = 12;
    // Ended Here
    public final int BALANCETYPE_LENGTH = 10;
    public final int ACTION_LENGTH = 1;

    // Specify Tag's as per XML Request
    public final String TAG_DATE = "DATE";
    public final String TAG_EXTNWCODE = "EXTNWCODE";
    public final String TAG_MSISDN = "MSISDN";
    public final String TAG_MSISDN2 = "MSISDN2";
    public final String TAG_PIN = "PIN";
    public final String TAG_LOGIN_ID = "LOGINID";
    public final String TAG_LOGIN_ID2 = "LOGINID2";
    public final String TAG_PASSWORD = "PASSWORD";
    public final String TAG_EXTCODE = "EXTCODE";
    public final String TAG_EXTCODE2 = "EXTCODE2";
    public final String TAG_EXTREFNUM = "EXTREFNUM";
    public final String TAG_AMOUNT = "AMOUNT";
    public final String TAG_LANGUAGE1 = "LANGUAGE1";
    public final String TAG_LANGUAGE2 = "LANGUAGE2";
    public final String TAG_SELECTOR = "SELECTOR";
    public final String TAG_TXNID = "TXNID";
    public final String TAG_MESSAGE = "MESSAGE";
    public final String TAG_TYPE = "TYPE";
    public final String TAG_PRODUCTCODE = "PRODUCTCODE";
    public final String TAG_QTY = "QTY";
    public final String TAG_REMARK = "REMARK";
    public final String TAG_CATCODE = "CATCODE";
    public final String TAG_USERNAME = "USERNAME";
    public final String TAG_SHORTNAME = "SHORTNAME";
    public final String TAG_USERNAMEPREFIX = "USERNAMEPREFIX";
    public final String TAG_SUBSCRIBERCODE = "SUBSCRIBERCODE";
    public final String TAG_CONTACTPERSON = "CONTACTPERSON";
    public final String TAG_SSN = "SSN";
    public final String TAG_ADDRESS1 = "ADDRESS1";
    public final String TAG_ADDRESS2 = "ADDRESS2";
    public final String TAG_CITY = "CITY";
    public final String TAG_STATE = "STATE";
    public final String TAG_COUNTRY = "COUNTRY";
    public final String TAG_EMAILID = "EMAILID";
    public final String TAG_EXTTXNNUMBER = "EXTTXNNUMBER";
    public final String TAG_EXTTXNDATE = "EXTTXNDATE";
    public final String TAG_TRFCATEGORY = "TRFCATEGORY";
    public final String TAG_REFNUMBER = "REFNUMBER";
    public final String TAG_PAYMENTTYPE = "PAYMENTTYPE";
    public final String TAG_PAYMENTINSTNUMBER = "PAYMENTINSTNUMBER";
    public final String TAG_PAYMENTGATEWAY = "PAYMENTGATEWAY";
    public final String TAG_PAYMENTDATE = "PAYMENTDATE";
    public final String TAG_EMPCODE = "EMPCODE";
    public final String TAG_USERMSISDN = "USERMSISDN";
    public final String TAG_NEWPASSWD = "NEWPASSWD";
    public final String TAG_CONFIRMPASSWD = "CONFIRMPASSWD";
    public final String TAG_PARENTMSISDN = "PARENTMSISDN";
    public final String TAG_PARENTEXTCODE = "PARENTEXTCODE";
    public final String TAG_MSISDN1 = "MSISDN1";
    public final String TAG_MSISDN3 = "MSISDN3";
    public final String TAG_USERCATCODE = "USERCATCODE";
    public final String TAG_WEBLOGINID = "WEBLOGINID";
    public final String TAG_NEWEXTERNALCODE = "NEWEXTERNALCODE";
    public final String TAG_WEBPASSWORD = "WEBPASSWORD";
    public final String TAG_USERLOGINID = "USERLOGINID";
    public final String TAG_EXTERNALCODE = "EXTERNALCODE";
    public final String TAG_ROLECODE = "ROLECODE";
    public final String TAG_ICCID = "ICCID";
    public final String TAG_ICCIDCONFIRM = "ICCIDCONFIRM";
    public final String TAG_SUBSTYPE = "SUBSTYPE";
    public final String TAG_PORTTYPE = "PORTTYPE";
    public final String TAG_DIVISION = "DIVISION";
    public final String TAG_DEPARTMENT = "DEPARTMENT";
    public final String TAG_MOBILENUMBER = "MOBILENUMBER";
    /** START: Birendra: */
    public final String TAG_BALANCETYPE = "BALANCETYPE";
    /** STOP: Birendra: */
	public final String  TAG_BONUS="BONUS";
	public final int  SELECTOR_LENGTH_IRIS=10;
	//added for channel user transfer
	public final String  TAG_GEOGRAPHYCODE="GEOGRAPHYCODE";
	public final String  TAG_FROM_USER_MSISDN="FROM_USER_MSISDN";
	public final String  TAG_TO_PARENT_MSISDN="TO_PARENT_MSISDN";
	public final String  TAG_ACTION="ACTION";
		public final String TAG_STATUS = "STATUS";
	
	public final String TAG_GEOGRAPHYTYPE = "GEOGRAPHYTYPE";
	public final String TAG_PARENTGEOGRAPHYCODE = "PARENTGEOGRAPHYCODE";
	public final String TAG_GEOGRAPHYNAME = "GEOGRAPHYNAME";
	public final String TAG_GEOGRAPHYSHORTNAME = "GEOGRAPHYSHORTNAME";
	public final String TAG_GEOGRAPHYDESCRIPTION = "GEOGRAPHYDESCRIPTION";
	public final String TAG_GEOGRAPHYDEFAULTFLAG = "ISDEFAULT";
	public final String TAG_GEOGRAPHYACTION = "ACTION";
	public final String TAG_DESIGNATION = "DESIGNATION";
	public final String TAG_INSUSPEND = "INSUSPEND";
	public final String TAG_OUTSUSPEND = "OUTSUSPEND";
	public final String TAG_COMPANY = "COMPANY";
	public final String TAG_FAX = "FAX";
	//IRIS Change
	
	public final String  TAG_EXTERNALDATA1="EXTERNALDATA1";
	public final String  TAG_EXTERNALDATA2="EXTERNALDATA2";
	public final String  TAG_EXTERNALDATA3="EXTERNALDATA3";
	public final String  TAG_EXTERNALDATA4="EXTERNALDATA4";
	public final String  TAG_EXTERNALDATA5="EXTERNALDATA5";
	public final String  TAG_EXTERNALDATA6="EXTERNALDATA6";
	public final String  TAG_EXTERNALDATA7="EXTERNALDATA7";
	public final String  TAG_EXTERNALDATA8="EXTERNALDATA8";
	public final String  TAG_EXTERNALDATA9="EXTERNALDATA9";
	public final String  TAG_EXTERNALDATA10="EXTERNALDATA10";
	public final String  TAG_VOUCHERCODE="VOUCHERCODE";
	public final String  TAG_SERIALNUMBER="SERIALNUMBER";
	public final int  EXTERNALDATA_LENGTH=1;
	 public final String TAG_SID = "SID";
	 public final String TAG_NEWSID = "NEWSID";
	public final String  TAG_SWITCHID="SWITCHID";
	public final String  TAG_CELLID="CELLID";
	public final int  LOCATION_LENGTH=30;
	 public final int STATUS_LENGTH =10;

	public final String  TAG_VOUCHERPIN="PIN";
	public final String  TAG_SNO="SNO";
	public final String TAG_SUBID = "SUBID";
	public final String TAG_STATE_CHANGE_REASON = "STATE_CHANGE_REASON";
	public final int STATE_CHANGE_REASON_LENGTH = 100;
	public final String TAG_EXPIRY_CHANGE_REASON = "EXPIRY_CHANGE_REASON";
	public final String TAG_VOUCHERSEGMENT = "VOUCHERSEGMENT";
	public final String TAG_VOUCHERPROFILE = "VOUCHERPROFILE";
	public final String TAG_VOUCHERTYPE = "VOUCHERTYPE";
	public final String TAG_SERVICETYPE = "SERVICETYPE";
	public final String TAG_TRANSACTIONID = "TRANSACTIONID";
	public final String TAG_DATA = "DATA";
	public final String TAG_COMMAND = "COMMAND";
	public final String TAG_TXNAMT = "TXNAMT";
	public final String TAG_REVTXNID = "REVTXNID";
	public final String TAG_TXNDATE = "TXNDATE";
	public final String TAG_REVDATE = "REVDATE";
	public final String TAG_REVSTATUS = "REVSTATUS";
	public final String TAG_MESSAGE1 ="MESSAGE1";
	public final String TAG_MESSAGE2 = "MESSAGE2";
	public final String TAG_XML_LEFT_ANGULAR_BRACKET = "<";
	public final String TAG_XML_RIGHT_ANGULAR_BRACKET = "</";
	public final String TAG_XML_CLOSING_ANGULAR_BRACKET = ">";
	public final String TAG_PREV_STATUS = "PREVIOUS_STATUS";

}
