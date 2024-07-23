package com.btsl.pretups.channel.queue;

/*
 * @(#)RequestQueueTimeOutManager.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Anu Garg 15/07/2013 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 */

import java.util.ArrayList;

import com.btsl.loadcontroller.LoadController;
import com.btsl.loadcontroller.LoadControllerI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.logging.QueueLogger;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.util.BTSLUtil;

public class RequestQueueTimeOutManager extends Thread {
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private static boolean stopFlag = false;
    private ThreadPoolForQueue threadPoolForQueue = null;
    private DataQueue dataQueue = null;

    public RequestQueueTimeOutManager(
                    ThreadPoolForQueue threadPoolForQueue, DataQueue dataQueue) {
        this.threadPoolForQueue = threadPoolForQueue;
        this.dataQueue = dataQueue;
    }

    /**
     * Method to run the thread
     * 
     */
    public void run() {
        final String METHOD_NAME = "run";
        ArrayList<ThreadPoolClient> listOfRequestsRemoved = new ArrayList<ThreadPoolClient>();
        try {
            while (!stopFlag) {
                if (_log.isDebugEnabled()) {
                    _log.debug("RequestQueueTimeOutManager", "run() ", "Entered: ");
                }
                sleep(1000);
                listOfRequestsRemoved = threadPoolForQueue.checkTimeOut(dataQueue);
                ThreadPoolClient threadPoolClient = null;
                if (listOfRequestsRemoved != null && !listOfRequestsRemoved.isEmpty()) {
                    final int size = listOfRequestsRemoved.size();
                    for (int i = 0; i < size; i++) {
                        threadPoolClient = listOfRequestsRemoved.get(i);
                        QueueLogger.log("Enter in timeout for request ID " + threadPoolClient.getRequestQueueVO().getRequestIDMethod());
                        LoadController.checkSystemLoad(threadPoolClient.getRequestQueueVO().getRequestIDMethod(), LoadControllerI.REFUSED_FROM_QUEUE, threadPoolClient
                            .getRequestQueueVO().getRequestVO().getRequestNetworkCode(), threadPoolClient.getRequestQueueVO().getQueueForAll());
                        // LoadController.checkInstanceLoad(threadPoolClient.getRequestQueueVO().getRequestIDMethod(),LoadControllerI.REFUSED_FROM_QUEUE);
                        final String senderMsisdn = threadPoolClient.getRequestQueueVO().getSenderMsisdn();
                        final String senderMessage = BTSLUtil.getMessage(threadPoolClient.getRequestQueueVO().getRequestVO().getLocale(),
                            PretupsErrorCodesI.C2S_ERROR_QUEUE_TIMEOUT, null);
                        QueueLogger.log(senderMessage);
                        final PushMessage pushMessage = new PushMessage(senderMsisdn, senderMessage, Long.toString(threadPoolClient.getRequestQueueVO().getRequestIDMethod()),
                            threadPoolClient.getRequestQueueVO().getRequestVO().getRequestGatewayCode(), threadPoolClient.getRequestQueueVO().getRequestVO().getLocale());
                        pushMessage.push();
                    }
                }
            }
        } catch (Exception exception) {
            _log.errorTrace(METHOD_NAME, exception);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("RequestQueueTimeOutManager", "run() ", "Exited: ");
            }
        }
    }

    public void shutdown() {
        stopFlag = true;
    }
}
