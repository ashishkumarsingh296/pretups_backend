package com.apicontrollers.ussd.CommissionEnquiry;

import com.utils._APIUtil;

import java.util.HashMap;

public class USSD_ComEnquiry_API {


    final String MSISDN1 = "MSISDN1";
    final String NO_OF_DAYS= "NO_OF_DAYS";
    //final String NO_OF_DAYS="3";
    final String PIN = "PIN";
    final String TYPE = "TYPE";
    final String DATE = "DATE";
    public static final String TXNSTATUS = "COMMAND.TXNSTATUS";


    private final String API_ComEnquiry_API = "<?xml version=\"1.0\"?><COMMAND>"
           +  "<TYPE>LTCOMREQ</TYPE>"
            + "<DATE></DATE>"
            + "<NO_OF_DAYS></NO_OF_DAYS>"
            + "<MSISDN1></MSISDN1>"
            +  "<PIN></PIN>"
            +  "</COMMAND>";

    private String getAPI() {
        return API_ComEnquiry_API;
    }

    public String prepareAPI(HashMap<String, String> dataMap) {
        String API = getAPI();
        return _APIUtil.buildAPI(API, dataMap);


    }
}
