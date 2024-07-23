package com.btsl.loadcontroller;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

/*
 * CheckTimeOutCaseThread.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 22/06/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Thread for checking the various time out cases available
 */

public class CheckTimeOutCaseThread extends Thread {

    private static Log _log = LogFactory.getLog(CheckTimeOutCaseThread.class.getName());
    private TransactionLoadVO transactionLoadVO = null;
    private String loadType = null;

    public CheckTimeOutCaseThread(TransactionLoadVO p_transactionLoadVO) {
        transactionLoadVO = p_transactionLoadVO;
        loadType = LoadControllerCache.getLoadType();
    }

    public void run() {
        if (_log.isDebugEnabled()) {
            _log.debug("CheckTimeOutCaseThread[run]", "Entered ");
        }

        HashMap transactionListMap = null;
        Set<Map.Entry> transactionIDSet = null;
        if (transactionLoadVO != null) {
            transactionListMap = transactionLoadVO.getTransactionListMap();
             transactionIDSet = transactionListMap.entrySet();
        }
       
       // java.util.Iterator itr = transactionIDSet.iterator();
        long requestStartTime = 0;
        long currentTime = System.currentTimeMillis();
        String transactionID = null;
        String recieverService = null;
        TransactionLoadVO receiverTransVO = null;
        InterfaceLoadController interfaceLoadController = null;
        NetworkLoadController networkLoadController = null;
        InstanceLoadController instanceLoadController = null;

       for(Map.Entry entry : transactionIDSet){
            transactionID = (String) entry.getKey();
            requestStartTime = (Long) entry.getValue();
            if (_log.isDebugEnabled()) {
                _log.debug("CheckTimeOutCaseThread[run]", "currentTime=" + currentTime + " transactionID=" + transactionID + " requestStartTime=" + requestStartTime + "transactionLoadVO.getRequestTimeoutSec()=" + transactionLoadVO.getRequestTimeoutSec());
            }

            if ((currentTime - requestStartTime / 1000) > transactionLoadVO.getRequestTimeoutSec()) {
                _log.info("CheckTimeOutCaseThread[run]", "transactionID=" + transactionID + " Request timed out ");

                // Time out case for transaction
                // Remove from
                transactionListMap.remove(transactionID);

                HashMap allTransMap = LoadControllerCache.getAllTransactionLoadListMap();
                MiniTransVO miniTransVO = (MiniTransVO) allTransMap.get(transactionID);
                String interfaceService = miniTransVO.getSenderInterfaceService();
                boolean isSenderOverflow = miniTransVO.isOverflow();
                String originalService = miniTransVO.getSenderOriginalService();

                recieverService = miniTransVO.getReciverInterfaceService();
                if (_log.isDebugEnabled()) {
                    _log.debug("CheckTimeOutCaseThread[run]", "transactionID=" + transactionID + " Request timed out Decreasing counters interfaceService=" + interfaceService + " isSenderOverflow=" + isSenderOverflow + " Sender originalService=" + originalService + "recieverService=" + recieverService);
                }

                if (!BTSLUtil.isNullString(recieverService)) {
                    interfaceLoadController = LoadControllerUtil.getInterfaceLoadObjectByKey(recieverService);
                    receiverTransVO = (TransactionLoadVO) (LoadControllerCache.getTransactionLoadHash()).get(recieverService);
                    boolean isRecieverOverflow = miniTransVO.isRecieverOverflow();
                    String originalRecieverService = miniTransVO.getReciverOriginalService();
                    if (_log.isDebugEnabled()) {
                        _log.debug("CheckTimeOutCaseThread[run]", "transactionID=" + transactionID + " Request timed out Decreasing counters recieverService=" + recieverService + " isRecieverOverflow=" + isRecieverOverflow + " originalRecieverService=" + originalRecieverService + " receiverTransVO.getCurrentTransactionLoad()=" + receiverTransVO.getCurrentTransactionLoad());
                    }

                    synchronized (this) {
                        if (receiverTransVO.getCurrentTransactionLoad() > 0) {
                            receiverTransVO.setCurrentTransactionLoad(receiverTransVO.getCurrentTransactionLoad() - 1);
                        }
                        if (isRecieverOverflow) {
                            TransactionLoadVO origReceiverTransactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(originalRecieverService);
                            if (origReceiverTransactionLoadVO.getOverFlowCount() > 0) {
                                origReceiverTransactionLoadVO.setOverFlowCount(origReceiverTransactionLoadVO.getOverFlowCount() - 1);
                            }
                            _log.info("CheckTimeOutCaseThread[run]", "transactionID=" + transactionID + " Request timed out Decreasing counters recieverService=" + recieverService + " isRecieverOverflow=" + isRecieverOverflow + " originalRecieverService=" + originalRecieverService + " origReceiverTransactionLoadVO.getOverFlowCount()=" + origReceiverTransactionLoadVO.getOverFlowCount());
                        }
                    }
                    interfaceLoadController.decreaseCurrentInterfaceLoad(LoadControllerI.DEC_LAST_TRANS_COUNT, receiverTransVO.getNetworkCode(), receiverTransVO.getInterfaceID(), transactionID);
                }
                interfaceLoadController = LoadControllerUtil.getInterfaceLoadObject(transactionLoadVO.getInstanceID(), transactionLoadVO.getNetworkCode(), transactionLoadVO.getInterfaceID());
                networkLoadController = LoadControllerUtil.getNetworkLoadObject(transactionLoadVO.getInstanceID(), transactionLoadVO.getNetworkCode());
                instanceLoadController = LoadControllerUtil.getInstanceLoadObject(transactionLoadVO.getInstanceID());

                synchronized (this) {
                    if (transactionLoadVO.getCurrentTransactionLoad() > 0) {
                        transactionLoadVO.setCurrentTransactionLoad(transactionLoadVO.getCurrentTransactionLoad() - 1);
                    }
                    if (isSenderOverflow) {
                        TransactionLoadVO origTransactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(originalService);
                        if (origTransactionLoadVO.getOverFlowCount() > 0) {
                            origTransactionLoadVO.setOverFlowCount(origTransactionLoadVO.getOverFlowCount() - 1);
                        }
                        _log.info("CheckTimeOutCaseThread[run]", "transactionID=" + transactionID + " Request timed out Decreasing counters recieverService=" + recieverService + " isSenderOverflow=" + isSenderOverflow + " originalService=" + originalService + " transactionLoadVO.getCurrentTransactionLoad()=" + transactionLoadVO.getCurrentTransactionLoad() + " origTransactionLoadVO.getOverFlowCount()=" + origTransactionLoadVO.getOverFlowCount());
                    }
                }

                // Store the avg time also if possible
                // Calculate here
                // Also decrease from other counters as well like interface ,
                // network and instance
                interfaceLoadController.decreaseCurrentInterfaceLoad(LoadControllerI.DEC_LAST_TRANS_COUNT, transactionLoadVO.getNetworkCode(), transactionLoadVO.getInterfaceID(), transactionID);
                networkLoadController.decreaseCurrentNetworkLoad(transactionLoadVO.getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);
                instanceLoadController.decreaseCurrentInstanceLoad(transactionLoadVO.getInstanceID(), LoadControllerI.DEC_LAST_TRANS_COUNT);

                // Set average time for the instance+network+service
                transactionLoadVO.setAverageServiceTime((transactionLoadVO.getAverageServiceTime() + (currentTime - requestStartTime)) / 2);

                LoadControllerCache.getAllTransactionLoadListMap().remove(transactionID);
                if (_log.isDebugEnabled()) {
                    _log.debug("CheckTimeOutCaseThread[run]", "transactionID=" + transactionID + " Request timed out Removing from HashMap of getAllTransactionLoadListMap");
                }

            }
        }
    }
}
