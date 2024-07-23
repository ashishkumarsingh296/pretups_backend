package com.btsl.loadcontroller;

import java.util.Enumeration;
import java.util.Hashtable;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

/*
 * QueueLoadManagerThread.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 22/06/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Class that manages all the queue available at the various interface
 */

public class QueueLoadManagerThread extends Thread {
    private static final Log logger = LogFactory.getLog(QueueLoadManagerThread.class.getName());
    private static long sleepTime = 200;

    public QueueLoadManagerThread() {
    }

    public QueueLoadManagerThread(long p_sleepTime) {
        sleepTime = p_sleepTime;
    }

    public void setSleepTime(long p_sleepTime) {
        sleepTime = p_sleepTime;
    }

    // Get the Queue of the various interfaces
    public void run() {
        final String methodName="run";
    	long currentTime = System.currentTimeMillis();
        Hashtable interfaceLoadHash = LoadControllerCache.getInterfaceLoadHash();
        Enumeration interfaceKeys = interfaceLoadHash.keys();
        InterfaceLoadVO interfaceLoadVO = null;
        while (interfaceKeys.hasMoreElements()) {
            interfaceLoadVO = (InterfaceLoadVO) interfaceLoadHash.get(interfaceKeys.nextElement());
            if (logger.isDebugEnabled()) {
                logger.debug("run", "interfaceLoadVO=" + interfaceLoadVO + " currentTime=" + currentTime);
            }
            if (currentTime > interfaceLoadVO.getLastQueueCaseCheckTime() + interfaceLoadVO.getNextQueueCheckCaseAfterSec()) {
                interfaceLoadVO.setLastQueueCaseCheckTime(currentTime);
                QueueRequestCaseThread queueRequestCaseThread = new QueueRequestCaseThread(interfaceLoadVO);
                queueRequestCaseThread.start();
            }
        }
        try {
            Thread.sleep(sleepTime); // Minimum Time of all the interfaces
        } catch (InterruptedException x) {
           logger.error(methodName, x); // ignore
        }

    }

    public static void main(String[] args) {
        final String methodName="main";
    	QueueLoadManagerThread tt = new QueueLoadManagerThread();
        tt.setName("Interface Queue Manager thread");
        tt.start();

        // pause for a bit
        try {
            Thread.sleep(200); // Minimum Time of all the interfaces
        } catch (InterruptedException x) {
           logger.error(methodName,x); // ignore
        }
    }
}
