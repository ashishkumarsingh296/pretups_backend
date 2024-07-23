package com.inter.huawei84;

/**
 * @(#)Huawei84LoadTest.java
 *                           Copyright(c) 2007, Bharti Telesoft Int. Public Ltd.
 *                           All Rights Reserved
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Author Date History
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Vinay Kumar Singh December 26, 2007 Initial
 *                           Creation
 *                           --------------------------------------------------
 *                           ---------------------------------------------
 *                           This class would be responsible Invoke the various
 *                           method of HuaweiINHandler concurrently.
 */
import java.util.HashMap;

public class Huawei84LoadTest {
    private HashMap _requestMap = null;
    private String _creditAdjust = "creditAdjust";
    private String _debitAdjust = "debitAdjust";
    private String _credit = "credit";
    private String _action = null;

    public Huawei84LoadTest(HashMap p_requestMap, String p_action) {
        this._requestMap = p_requestMap;
        this._action = p_action;
        try {
            long startTime = System.currentTimeMillis();
            long totalTime = 0;
            for (int i = 0; i < 10; i++)
                new LoadTestHandler().start();
            // Thread.sleep(50);
            totalTime = System.currentTimeMillis() - startTime;
            System.out.println("Total Time in execution of Thread:" + totalTime);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Huawei84LoadTest Exception e:" + e.getMessage());
        }
    }// end of Constructor-Huawei84LoadTest

    class LoadTestHandler extends Thread {
        Huawei84INHandler inhandler = null;

        public void run() {
            try {
                inhandler = new Huawei84INHandler();
                if (_credit.equals(_action))
                    inhandler.credit(_requestMap);
                else if (_creditAdjust.equals(_action))
                    inhandler.creditAdjust(_requestMap);
                else if (_debitAdjust.equals(_action))
                    inhandler.debitAdjust(_requestMap);
                else
                    inhandler.validate(_requestMap);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Exception e=" + e.getMessage());
            }
        }// end of run method
    }// end of Class-LoadTestHandler
}// end of Class-Huawei84LoadTest
