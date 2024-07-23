/*
 * Created on Jul 2, 2009
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.claroca.iat;

public class IATI {

    public static String VALIDATION_RESULT = "200";

    public static String ACTION_CREDIT = "CREDIT";
    public static String ACTION_CHECK_STATUS = "CHKSTATUS";

    public static String CREDIT_RESPONSE_CODE = "200";
    public static String CHECKSTATUS_RESPONSE_CODE = "0";
    public static String IAT_INTERNAL_SERVER_ERROR_CODE = "19001";

    /*
     * public static String INVALID_IATTRXID="";
     * public static String INVALID_SNWTRXID="";
     * public static String REC_ZEBRA_TXN_STATUS_FAIL="";
     */
    public static String REC_ZEBRA_TXN_STATUS_AMBIGUOUS = "250";
    public static String REC_ZEBRA_TXN_STATUS_UNDER_PROCESS = "205";

    public static String CHECKSTATUS_RESPONSE_SUCCESS = "200";
    public static String CHECKSTATUS_RESPONSE_FAIL = "206";
    public static String RECHARGE_CREDIT_RESPONSE_SUCCESS = "200";
    public static String RECHARGE_CREDIT_RESPONSE_FAIL = "205";
    public static String CHECKSTATUS_RESPONSE_AMBIGIOUS = "250";
    public static String RECHARGE_CREDIT_RESPONSE_AMBIGIOUS = "250";

    public static String CREDIT_RESPONSE_SUCCESS_ERROR_CODE_LIST = "0";
    public static String CREDIT_RESPONSE_FAIL_ERROR_CODE_LIST = "19001,19002";
    public static String CREDIT_RESPONSE_AMBIGIOUS_ERROR_CODE_LIST = "";

    public static String CHK_STATUS_RESPONSE_SUCCESS_ERROR_CODE_LIST = "0";
    public static String CHK_STATUS_RESPONSE_FAIL_ERROR_CODE_LIST = "206,1001,1002,1003,21000,25002,5002";
    public static String CHK_STATUS_RESPONSE_AMBIGIOUS_ERROR_CODE_LIST = "205,250,25001,5001,29001,29002";

    public static String REQUEST_SOURCE_WEB = "WEB";
    public static String REQUEST_SOURCE_INMODULE = "INMODULE";
    public static String REQUEST_SOURCE_PROCESS = "PROCESS";

    public static String FAILES_LOCATION_IAT_HUB = "Iat Hub";
    public static String FAILES_LOCATION_REC_ZEBRA = "Receiving Zebra";

}
