package com.apicontrollers.ussd.GMB;

import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.*;

import java.util.HashMap;


public class USSD_GMB_DP {



    public static HashMap<String, String> getAPIdata() throws Exception {

        /*
         * Variable Declaration
         */
        HashMap<String, String> apiData = new HashMap<String, String>();

        USSD_GMB_API GMBAPI = new USSD_GMB_API();

        RandomGeneration RandomGeneration = new RandomGeneration();

        GenerateMSISDN gnMsisdn = new GenerateMSISDN();

        String CreditTransferCode = _masterVO.getProperty("CreditTransferCode");
        String ProductType = _masterVO.getProperty("PrepaidProductType");
        int dataRowCounter = 0;
        String productCode = null;
        String UserCategory = null;


        /*
         * Variable initializations
         */
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.TRANSFER_RULE_SHEET);
        dataRowCounter = ExcelUtility.getRowCount();
        for (int i=0; i <= dataRowCounter; i++) {
            String categoryServices = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
            String gatewayType = ExcelUtility.getCellData(0, ExcelI.ACCESS_BEARER, i);
            if (categoryServices.contains(CreditTransferCode) && gatewayType.contains("EXTGW")) {
                UserCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
                break;
            }
        }




       // String MSISDN = DBHandler.AccessHandler.getP2PSubscriberMSISDN("PRE", "Y");
      //  String MSISDN_Length = DBHandler.AccessHandler.getSystemPreference("MSISDN_LENGTH");

        String prefix = _masterVO.getMasterValue(MasterI.SUBSCRIBER_PREPAID_PREFIX);

        apiData.put(GMBAPI.MSISDN1, prefix + RandomGeneration.randomNumeric(gnMsisdn.generateMSISDN()));
        apiData.put(GMBAPI.MSISDN2, prefix + RandomGeneration.randomNumeric(gnMsisdn.generateMSISDN()));
        apiData.put(GMBAPI.LANGUAGE1, DBHandler.AccessHandler.checkForLangCode(_masterVO.getMasterValue(MasterI.LANGUAGE)));
        apiData.put(GMBAPI.LANGUAGE2, DBHandler.AccessHandler.checkForLangCode(_masterVO.getMasterValue(MasterI.LANGUAGE)));
        apiData.put(GMBAPI.AMOUNT, "100");
        return apiData;

    }

}





