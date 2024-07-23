package com.btsl.loadcontroller;

import java.util.Enumeration;
import java.util.Hashtable;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

/*
 * RequestTimeOutManagerThread.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 22/06/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Thread Class for checking the request for time out cases
 */

public class RequestTimeOutManagerThread extends Thread {
    private static final Log logger = LogFactory.getLog(RequestTimeOutManagerThread.class.getName());
	public static final String CLASS_NAME = "RequestTimeOutManagerThread";
    public void run() {
        if (logger.isDebugEnabled()) {
        	logger.debug("RequestTimeOutManagerThread[run]", " Entered ");
        }

        long currentTime = System.currentTimeMillis();
        Hashtable transactionLoadHash = LoadControllerCache.getTransactionLoadHash();
        TransactionLoadVO transactionLoadVO = null;
        Enumeration interfaceServiceKeys = transactionLoadHash.keys();
        while (interfaceServiceKeys.hasMoreElements()) {
            transactionLoadVO = (TransactionLoadVO) transactionLoadHash.get(interfaceServiceKeys.nextElement());
            if (logger.isDebugEnabled()) {
            	logger.debug("RequestTimeOutManagerThread[run]", "currentTime=" + currentTime + " transactionLoadVO.getLastTimeOutCaseCheckTime()=" + transactionLoadVO.getLastTimeOutCaseCheckTime() + " transactionLoadVO.getNextCheckTimeOutCaseAfterSec()=" + transactionLoadVO.getNextCheckTimeOutCaseAfterSec());
            }
            if (currentTime > transactionLoadVO.getLastTimeOutCaseCheckTime() + transactionLoadVO.getNextCheckTimeOutCaseAfterSec()) {
                transactionLoadVO.setLastTimeOutCaseCheckTime(currentTime);
                CheckTimeOutCaseThread checkTimeOutCases = new CheckTimeOutCaseThread(transactionLoadVO);
                checkTimeOutCases.start();
            }
        }
    }

    public static void main(String[] args) {
        RequestTimeOutManagerThread tt = new RequestTimeOutManagerThread();
        tt.setName("Time Out thread");
        tt.start();

        // pause for a bit
        try {
            Thread.sleep(200); // Minimum Time of all the interfaces
        } catch (InterruptedException x) {
        	logger.error(CLASS_NAME,x);   // ignore
        }
    }
}
