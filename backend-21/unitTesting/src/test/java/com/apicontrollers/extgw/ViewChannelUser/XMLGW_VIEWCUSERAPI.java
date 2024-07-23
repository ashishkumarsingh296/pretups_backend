package com.apicontrollers.extgw.ViewChannelUser;

import com.utils._APIUtil;

import java.util.HashMap;

public class XMLGW_VIEWCUSERAPI {
    //Request Parameters
    final String COMMAND="COMMAND";
    final String TYPE="TYPE";
    final String DATA="DATA";
    final static String DATE="DATE";
    final static String EXTNWCODE="EXTNWCODE";
    final static String CATCODE="CATCODE";
    final static String EMPCODE="EMPCODE";
    final static String LOGINID="LOGINID";
    final static String PASSWORD="PASSWORD";
    final static String EXTREFNUM="EXTREFNUM";
    final static String USERLOGINID="USERLOGINID";
    final static String MSISDN="MSISDN";
    //final String REMARKS="REMARKS";

    //public static final String PIN = "COMMAND.PIN";

    //Response Parameters
    public static final String TXNSTATUS = "COMMAND.TXNSTATUS";



    private final String API_ViewCUser = "<?xml version=\"1.0\"?>"
            + "<COMMAND><TYPE>VIEWCUSER</TYPE>"
            + "<DATE></DATE>"
            + "<EXTNWCODE></EXTNWCODE>"
            + "<CATCODE></CATCODE>"
            + "<EMPCODE></EMPCODE>"
            + "<LOGINID></LOGINID>"
            + "<PASSWORD></PASSWORD>"
            + "<EXTREFNUM></EXTREFNUM>"
            + "<DATA>"
            + "<USERLOGINID></USERLOGINID>"
            + "<MSISDN></MSISDN>"
            + "</DATA>"
            + "</COMMAND>";

    /**
     * Method to handle the Version Based API Handling
     * @return
     */
    private String getAPI() {
        return API_ViewCUser;
    }

    public String prepareAPI(HashMap<String, String> dataMap) {
        String API = getAPI();
        return _APIUtil.buildAPI(API, dataMap);
    }
}


