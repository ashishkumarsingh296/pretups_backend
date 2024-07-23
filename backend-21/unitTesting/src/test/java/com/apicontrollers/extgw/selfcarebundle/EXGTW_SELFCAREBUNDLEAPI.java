package com.apicontrollers.extgw.selfcarebundle;

import java.util.HashMap;
import com.utils._APIUtil;

public class EXGTW_SELFCAREBUNDLEAPI {

    //Request Parameters
    final String COMMAND="COMMAND";
    final String TYPE="TYPE";
    final String EXTREFNUM="EXTREFNUM";
    final String DATE="DATE";
    final String EXTNWCODE="EXTNWCODE";
    final String LOGINID="LOGINID";
    final String PASSWORD="PASSWORD";
    final String MSISDN="MSISDN";
    //final static String OPERATION="OPERATION";
    final  String LANGUAGE1="LANGUAGE1";
    final  String PIN="PIN";
    //final String REMARKS="REMARKS";
    final  String EXTCODE="EXTCODE";
    final  String MSISDN2="MSISDN2";

    //Payment
    final String PAYMENTTYPE="PAYMENTTYPE";
    final String PAYMENTMODE="PAYMENTMODE";
    final String PAYMENTINFO="PAYMENTINFO";
    final String PAYEEMSISDN="PAYEEMSISDN";
    final String AMOUNT="AMOUNT";
    final String PROMOCODE="PROMOCODE";

    //Bonus parameters
    final String FLAG="FLAG";
    final String BONUSTYPE="BONUSTYPE";
    final String BONUSVALUE="BONUSVALUE";
    final String BENEFICIARYMSIDN="BENEFICIARYMSIDN";
    final String BONUSAMOUNT="BONUSAMOUNT";
    final String BONUSREFILLID="BONUSREFILLID";
    final String INFO1="INFO1";
    final String INFO2="INFO2";
    final String INFO3="INFO3";

    //public static final String PIN = "COMMAND.PIN";


    //Response Parameters
    public static final String RESPTYPE="COMMAND.TYPE";
    public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
    public static final String TXNID = "COMMAND.TXNID";
    public static final String RESPDATE = "COMMAND.DATE";
    public static final String EXTREF = "COMMAND.EXTREFNUM";
    public static final String MESSAGE = "COMMAND.MESSAGE";

    private static final String API_SELFCARE = "<?xml version=\"1.0\"?><COMMAND>"
            + "<TYPE>Bundle</TYPE>"
            +"<EXTREFNUM></EXTREFNUM>"
            +"<DATE></DATE>"
            +"<EXTNWCODE></EXTNWCODE>"
            +"<MSISDN></MSISDN>"
            +"<PIN></PIN>"
            +"<LOGINID></LOGINID>"
            +"<PASSWORD></PASSWORD>"
            +"<EXTCODE></EXTCODE>"
            +"<MSISDN2></MSISDN2>"
            +"<PRODUCTS><PRODUCTCODE>101</PRODUCTCODE>"
            +"<OFFERID>12345</OFFERID>"
            +"<REFILLID>R1221</REFILLID></PRODUCTS>"
            + "<PAYMENTDETAILS><PAYMENTTYPE></PAYMENTTYPE><PAYMENTMODE></PAYMENTMODE>"
            + "<PAYMENTINFO></PAYMENTINFO>"
            +"<PAYEEMSISDN></PAYEEMSISDN>"
            +"<AMOUNT></AMOUNT>"
            +"<PROMOCODE></PROMOCODE></PAYMENTDETAILS>"
            +"<BONUS><FLAG></FLAG>"
            +"<BONUSTYPE></BONUSTYPE>"
            +"<BONUSVALUE></BONUSVALUE>"
            +"<BENEFICIARYMSIDN></BENEFICIARYMSIDN>"
            +"<BONUSAMOUNT></BONUSAMOUNT>"
            +"<BONUSREFILLID></BONUSREFILLID></BONUS>"
            +"<INFO1>info1</INFO1>"
            +"<INFO2>info2</INFO2>"
            +"<INFO3>info3</INFO3>"
            +"</COMMAND>";

    private static String getAPI() {
        return API_SELFCARE;
    }

    public static String prepareAPI(HashMap<String, String> dataMap) {
        String API = getAPI();
        return _APIUtil.buildAPI(API, dataMap);
    }
}
