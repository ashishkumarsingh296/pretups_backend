package com.apicontrollers.ussd.CardGroupEnquiry;

import java.util.HashMap;

import com.utils._APIUtil;

public class USSD_CardGroupEnquiryAPI {

    final String MSISDN1 = "MSISDN1";
    final String PIN = "PIN";
    final String MSISDN2 = "MSISDN2";
    final String SERVICETYPE = "SERVICETYPE";
    final String SUBSERVICE = "SUBSERVICE";
    final String AMOUNT = "AMOUNT";

    //Response Parameters
    public final String TXNSTATUS = "COMMAND.TXNSTATUS";
    public static final String SERVICECLASS = "COMMAND.SERVICECLASS";

    private final String API_CardGroupEnquiry = "<?xml version=\"1.0\"?>"
            + "<COMMAND>"
            + "<TYPE>CGENQREQ</TYPE>"
            + "<MSISDN1></MSISDN1>"
	        + "<PIN></PIN>"
            + "<MSISDN2></MSISDN2>"
            + "<SERVICETYPE></SERVICETYPE>"
            + "<SUBSERVICE></SUBSERVICE>"
            + "<AMOUNT></AMOUNT>"
            + "</COMMAND>";

    /**
     * Method to handle the Version Based API Handling
     */
    private String getAPI() {
        return API_CardGroupEnquiry;
    }

    public String prepareAPI(HashMap<String, String> Map) {
        String API = getAPI();
        return _APIUtil.buildAPI(API, Map);
    }
}
