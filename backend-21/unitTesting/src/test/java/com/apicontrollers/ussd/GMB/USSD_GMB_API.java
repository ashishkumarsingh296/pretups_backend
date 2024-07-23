package com.apicontrollers.ussd.GMB;
import com.utils._APIUtil;

import java.util.HashMap;
public class USSD_GMB_API {

    final String MSISDN1 = "MSISDN1";
    final String LANGUAGE1 = "LANGUAGE1";
    final String MSISDN2 = "MSISDN2";
    final String AMOUNT = "AMOUNT";
    final String LANGUAGE2 = "LANGUAGE2";


    //Response Parameters
    public static final String TXNSTATUS = "COMMAND.TXNSTATUS";

    private final String API_TransferAPI = "<?xml version=\"1.0\"?><COMMAND>"
            + "<TYPE>CGMBALREQ</TYPE>"
            + "<MSISDN1></MSISDN1>"
            +"<MSISDN2></MSISDN2>"
            +"<AMOUNT></AMOUNT>"
            + "<LANGUAGE1></LANGUAGE1>"
            + "<LANGUAGE2></LANGUAGE2>"
            + "</COMMAND>";


    private String getAPI() {
        return API_TransferAPI;
    }

    public String prepareAPI(HashMap<String, String> dataMap) {
        String API = getAPI();
        return _APIUtil.buildAPI(API, dataMap);
    }


}
