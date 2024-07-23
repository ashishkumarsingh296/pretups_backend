package com.apicontrollers.extgw.Loan;

import com.utils._APIUtil;

import java.util.HashMap;

public class OptOutAPI {


     public final String DATE = "DATE";
        public final String EXTNWCODE = "EXTNWCODE";
        public final String MSISDN = "MSISDN";
        public final String PIN = "PIN";
        public final String LOGINID = "LOGINID";
        public final String PASSWORD = "PASSWORD";
        public final String LANGUAGE1 = "LANGUAGE1";
        public final String EXTCODE = "EXTCODE";
        public final String EXTREFNUM = "EXTREFNUM";
        public static final String TXNSTATUS = "COMMAND.TXNSTATUS";

        /**
         * @category RoadMap C2C Transfer API
         * @author simarnoor.bains
         */
        private final String API_LOANOPTOUT = "<?xml version=\"1.0\"?><COMMAND>"
                +"<TYPE>LOANOPTOUTREQ</TYPE>"
                +"<DATE>10/07/20</DATE>"
                +"<EXTNWCODE>AK</EXTNWCODE>"
                +"<MSISDN></MSISDN>"
                +"<PIN></PIN>"
                +"<LOGINID></LOGINID>"
                +"<PASSWORD></PASSWORD>"
                +"<EXTCODE></EXTCODE>"
                +"<EXTREFNUM></EXTREFNUM>"
                +"<PRODUCTCODE>ETOPUP</PRODUCTCODE>"
                +"<LANGUAGE1>0</LANGUAGE1>"
                +"</COMMAND>";


        /**
         * Method to handle the Version Based API Handling
         * @return
         */
        private String getAPI() {
            return API_LOANOPTOUT;
        }

        public String prepareAPI(HashMap<String, String> dataMap) {
            String API = getAPI();
            return _APIUtil.buildAPI(API, dataMap);
        }

    }





