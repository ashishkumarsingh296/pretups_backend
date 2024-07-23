package com.apicontrollers.extgw.C2SConsentreversal;

import com.utils._APIUtil;

import java.util.HashMap;

public class C2SConsentAPI {

    public final String EXTNWCODE = "EXTNWCODE";
    public final String LANGUAGE1 = "LANGUAGE1";
    public final String LOGINID = "LOGINID";
    public final String PASSWORD = "PASSWORD";
    public final String EXTREFNUM = "EXTREFNUM";
    public final String LOGINID2 = "LOGINID2";
    public final String MSISDN = "MSISDN";
    public final String PIN = "PIN";
    public final String EXTCODE = "EXTCODE";
    public final String TXNID = "TXNID";

    public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
    public static final String MESSAGE = "COMMAND.MESSAGE";


    /**
     * @category Airtel C2S Consent API
     * @author sonali khurana
     */

    private final String API_C2SConsentreversal_RMP = "<?xml version=\"1.0\"?><COMMAND>"
            + "<TYPE>C2SCONSENTREVREQ</TYPE>"
            + "<EXTNWCODE></EXTNWCODE>"
            + "<EXTCODE></EXTCODE>"
            + "<LOGINID></LOGINID>"
            + "<PASSWORD></PASSWORD>"
            + "<MSISDN></MSISDN>"
            + "<PIN></PIN>"
            + "<TXNID></TXNID>"
            + "<LANGUAGE1>0</LANGUAGE1>"
            + "<LANGUAGE2>0</LANGUAGE2></COMMAND>";

    /**
     * Method to handle the Version Based API Handling
     * @return
     */

    private String getAPI() {
        return API_C2SConsentreversal_RMP;
    }

    public String prepareAPI(HashMap<String, String> dataMap) {
        String API = getAPI();
        return _APIUtil.buildAPI(API, dataMap);
    }
}
