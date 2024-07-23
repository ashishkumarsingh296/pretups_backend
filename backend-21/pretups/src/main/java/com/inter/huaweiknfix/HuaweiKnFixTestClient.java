package com.inter.huaweiknfix;

/**
 * @(#)HuaweiKnFixTestClient.java
 *                                Copyright(c) 2009, Bharti Telesoft Ltd.
 *                                All Rights Reserved
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Author Date History
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Abhay January 29, 2009 Initial Creation
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                ----
 *                                This class is to test the Huawei IN interface.
 */
import java.util.HashMap;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.util.Constants;

public class HuaweiKnFixTestClient {
    private static Log _log = LogFactory.getLog(HuaweiKnFixTestClient.class.getName());

    public HuaweiKnFixTestClient() {
    }

    public static void main(String args[]) {
        System.out.println("Syntax: java HuaweiKnFixTestClient action msisdn");
        // java HuaweiKnFixTestClient validate 7000150020

        if (args.length != 2) {
            System.out.println("Invalid Syntax/Arguments.");
            System.exit(0);
        }

        HashMap requestMap = new HashMap();
        requestMap.put("ACTION", args[0]);
        // requestMap.put("ACTION","validate");
        requestMap.put("MSISDN", args[1]);
        // requestMap.put("MSISDN","7000150020");

        requestMap.put("IN_TXN_ID", "C586666");
        requestMap.put("INTERFACE_ID", "INTID00001");
        requestMap.put("MODULE", "C2S");
        requestMap.put("TRANSACTION_ID", "1500150015001500");
        requestMap.put("INTERFACE_PREV_BALANCE", "50000");
        requestMap.put("INTERFACE_AMOUNT", "5000.879");
        requestMap.put("EXT_VALIDITY_DAYS", "10");
        requestMap.put("INT_ST_TYPE", "A");
        requestMap.put("USER_TYPE", "S");

        try {
            // Constants.load("C:\\Abhay\\PROJECTS\\pretups\\WEB-INF\\configfiles\\Constants.props");
            Constants.load("/home/pretups_kenya/tomcat5/webapps/pretups/WEB-INF/classes/configfiles/Constants.props");
            // System.out.println("INTERFACE_DIRECTORY===="+Constants.getProperty("INTERFACE_DIRECTORY"));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("HuaweiKnFixTestClient.main() while loading Constants.props Exception e=" + e.getMessage());
        }
        try {
            FileCache.loadAtStartUp();
            // System.out.println("INTFCE_CLSR_SUPPORT===="+FileCache.getValue(requestMap.get("INTERFACE_ID").toString(),
            // "INTFCE_CLSR_SUPPORT"));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("HuaweiKnFixTestClient.main() while loading IN File Exception e=" + e.getMessage());
        }
        System.out.println("HuaweiKnFixTestClient.main() before IN requestMap=" + requestMap);
        sendRequesttoIN(requestMap);
        System.out.println("HuaweiKnFixTestClient.main() after IN requestMap=" + requestMap);

        System.out.println("HuaweiKnFixTestClient.main() End...########################");
    }

    public static void sendRequesttoIN(HashMap p_requestMap) {
        if (_log.isDebugEnabled())
            _log.debug("HuaweiKnFixTestClient.sendRequesttoIN()", "Entered...");
        System.out.println("HuaweiKnFixTestClient.sendRequesttoIN() Entered...");

        HuaweiKnFixINHandler inHandler = new HuaweiKnFixINHandler();
        try {
            if (p_requestMap.get("ACTION").toString().equalsIgnoreCase("validate")) {
                inHandler.validate(p_requestMap);
            } else if (p_requestMap.get("ACTION").toString().equalsIgnoreCase("creditAdjust")) {
                inHandler.creditAdjust(p_requestMap);
            } else if (p_requestMap.get("ACTION").toString().equalsIgnoreCase("credit")) {
                inHandler.credit(p_requestMap);
            } else if (p_requestMap.get("ACTION").toString().equalsIgnoreCase("debitAdjust")) {
                inHandler.debitAdjust(p_requestMap);
            } else if (p_requestMap.get("ACTION").toString().equalsIgnoreCase("validityAdjust")) {
                inHandler.validityAdjust(p_requestMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("HuaweiKnFixTestClient.sendRequesttoIN() Exception e=" + e.getMessage());
        }
        if (_log.isDebugEnabled())
            _log.debug("HuaweiKnFixTestClient.sendRequesttoIN()", "Exited...");
        System.out.println("HuaweiKnFixTestClient.sendRequesttoIN() Exited...");
    }
}
