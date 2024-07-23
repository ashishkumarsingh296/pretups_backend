package com.selftopup.loadcontroller;

import java.sql.Timestamp;
import java.util.concurrent.locks.ReentrantLock;

import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;

/*
 * NetworkLoadController.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 14/07/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Class to process the network load request
 */

public class NetworkLoadController {

    private static Log _log = LogFactory.getLog(NetworkLoadController.class.getName());
    private final static ReentrantLock lock = new ReentrantLock();

    /**
     * This method checks the network load whether the request can be processed
     * or not.
     * 
     * @param p_networkID
     * @return boolean : True means network load can cater this request, false
     *         means refuse the request
     */
    public boolean checkNetworkLoad(String p_networkID) {
        _log.info("checkNetworkLoad", "Entered p_networkID=" + p_networkID);

        boolean retStatus = true;
        long currentTime = System.currentTimeMillis();
        Timestamp timeStampVal = new Timestamp(currentTime);
        long previousUPTime = 0;
        boolean isSameSecRequest = false;
        String instanceID = LoadControllerCache.getInstanceID();

        NetworkLoadVO networkLoadVO = (NetworkLoadVO) LoadControllerCache.getNetworkLoadHash().get(instanceID + "_" + p_networkID);
        String loadType = LoadControllerCache.getLoadType();
        if (_log.isDebugEnabled())
            _log.debug("checkNetworkLoad", "Load Type to be used=" + loadType);

        if (networkLoadVO != null) {
            // ------------------------------------------------------------------------------------------------------
            lock.lock();
            try {
                // Processing for TPS Logic
                if (LoadControllerI.LOAD_CONTROLLER_TPS_TYPE.equalsIgnoreCase(loadType)) {
                    // Check Same Second Request
                    // If same second request check
                    // _noOfRequestSameSec<_networkCurrentTPS and pass the
                    // request
                    // Current Sec <> Previous Second update
                    // _previousSecond=current and _noOfRequestSameSec=0
                    isSameSecRequest = LoadControllerUtil.checkSameSecondRequest(networkLoadVO.getPreviousSecond(), currentTime);
                    if (isSameSecRequest) {
                        if (networkLoadVO.getNoOfRequestSameSec() < networkLoadVO.getCurrentTPS()) {
                            if (_log.isDebugEnabled())
                                _log.debug("checkNetworkLoad", "Request in Same Second and is within the allowed limit=" + networkLoadVO.getCurrentTPS() + " Current =" + networkLoadVO.getNoOfRequestSameSec() + " , checking for Transaction Load");

                            // Check with the current Transaction Count
                            retStatus = checkCurrentNetworkLoad(networkLoadVO, timeStampVal);
                            if (retStatus) {
                                networkLoadVO.setLastTxnProcessStartTime(timeStampVal);
                                networkLoadVO.setNoOfRequestSameSec(networkLoadVO.getNoOfRequestSameSec() + 1);
                            }
                        } else {
                            // Refuse the request
                            retStatus = false;
                            networkLoadVO.setTotalRefusedCount(networkLoadVO.getTotalRefusedCount() + 1);
                            networkLoadVO.setLastRefusedTime(timeStampVal);
                            if (_log.isDebugEnabled())
                                _log.debug("checkNetworkLoad", "Request in Same Second and is not within the allowed limit=" + networkLoadVO.getCurrentTPS() + " Current =" + networkLoadVO.getNoOfRequestSameSec() + " , Refusing the request");

                        }
                    } else {
                        if (_log.isDebugEnabled())
                            _log.debug("checkNetworkLoad", "Request is Not in Same Second , checking for Transaction Load");

                        // Check with the current Transaction Count
                        retStatus = checkCurrentNetworkLoad(networkLoadVO, timeStampVal);
                        if (retStatus) {
                            networkLoadVO.setPreviousSecond(currentTime);
                            networkLoadVO.setLastTxnProcessStartTime(timeStampVal);
                            networkLoadVO.setNoOfRequestSameSec(1);
                        }
                    }
                } else {
                    if (_log.isDebugEnabled())
                        _log.debug("checkNetworkLoad", "Not TPS based checking for Transaction Load");

                    // Check with the current Transaction Count
                    retStatus = checkCurrentNetworkLoad(networkLoadVO, timeStampVal);
                }
                // Store the First Request Time
                if (networkLoadVO.getFirstRequestCount() == 0) {
                    networkLoadVO.setFirstRequestCount(1);
                    networkLoadVO.setFirstRequestTime(currentTime);
                }
                // Increase the Recieved Count for the Instance
                networkLoadVO.setRecievedCount(networkLoadVO.getRecievedCount() + 1);
                networkLoadVO.setLastReceievedTime(timeStampVal);
            } finally {
                lock.unlock();
            }
        }
        _log.info("checkNetworkLoad", "Exiting with networkLoadVO=" + networkLoadVO + "retStatus:" + retStatus);
        return retStatus;
    }

    /**
     * This method decrease the total no of requests present in an network
     * 
     * @param p_event
     * @param p_networkID
     */
    public void decreaseCurrentNetworkLoad(String p_networkID, int p_event) {
        lock.lock();
        try {
            decreaseCurrentNetworkLoadUnlock(p_networkID, p_event);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Method to check the current number of request present in an network with
     * the available load
     * True= Successful, false=Refused
     * 
     * @param p_networkLoadVO
     * @param p_timeStampVal
     * @return boolean
     */
    public static boolean checkCurrentNetworkLoad(NetworkLoadVO p_networkLoadVO, Timestamp p_timeStampVal) {
        _log.info("checkCurrentNetworkLoad", "p_networkLoadVO=" + p_networkLoadVO + " p_timeStampVal=" + p_timeStampVal);

        boolean retStatus = false;
        // Check with the current Transaction Count
        if (p_networkLoadVO.getCurrentTransactionLoad() < p_networkLoadVO.getTransactionLoad()) {
            // Increase the counters
            retStatus = true;
            p_networkLoadVO.setCurrentTransactionLoad(p_networkLoadVO.getCurrentTransactionLoad() + 1);
            p_networkLoadVO.setRequestCount(p_networkLoadVO.getRequestCount() + 1);
            p_networkLoadVO.setLastTxnProcessStartTime(p_timeStampVal);
        } else // Refuse the request
        {
            retStatus = false;
            p_networkLoadVO.setTotalRefusedCount(p_networkLoadVO.getTotalRefusedCount() + 1);
            p_networkLoadVO.setLastRefusedTime(p_timeStampVal);
        }
        _log.info("checkCurrentNetworkLoad", "Exiting with Status (True mean Success, False means Refused=" + retStatus);
        return retStatus;
    }

    public void decreaseCurrentNetworkLoadUnlock(String p_networkID, int p_event) {
        // if(_log.isDebugEnabled())_log.debug("decreaseCurrentNetworkLoad","Entered p_networkID:"+p_networkID+" p_event:"+p_event);
        long currentTime = System.currentTimeMillis();
        String instanceID = LoadControllerCache.getInstanceID();
        if (LoadControllerCache.getNetworkLoadHash() == null || LoadControllerCache.getNetworkLoadHash().size() == 0)
            return;

        // Decrease only in case of Load Type TXN and not when the system is
        // running on TPS
        String loadType = LoadControllerCache.getLoadType();
        NetworkLoadVO networkLoadVO = (NetworkLoadVO) LoadControllerCache.getNetworkLoadHash().get(instanceID + "_" + p_networkID);
        if (networkLoadVO != null) {
            // _log.info("decreaseCurrentNetworkLoad","Current Network Load="+networkLoadVO.getCurrentTransactionLoad());

            // ------------------------------------------------------------------------------------------------------

            if (LoadControllerI.LOAD_CONTROLLER_TXN_TYPE.equalsIgnoreCase(loadType)) {
                // Decrease the Current Transaction Count in Case of TXN load
                // Type
                if (p_event == LoadControllerI.DEC_LAST_TRANS_COUNT) {
                    if (networkLoadVO.getCurrentTransactionLoad() > 0)
                        networkLoadVO.setCurrentTransactionLoad(networkLoadVO.getCurrentTransactionLoad() - 1);
                } else if (p_event == LoadControllerI.REFUSED_FROM_QUEUE) {
                    // Decrease the Current Transaction Count in Case of TXN
                    // load Type
                    if (networkLoadVO.getCurrentTransactionLoad() > 0)
                        networkLoadVO.setCurrentTransactionLoad(networkLoadVO.getCurrentTransactionLoad() - 1);
                }
            } else {
                if (p_event == LoadControllerI.DEC_SAME_SEC_RES_COUNT) {
                    /*
                     * if(networkLoadVO.getNoOfRequestSameSec()>0)
                     * networkLoadVO.setNoOfRequestSameSec(networkLoadVO.
                     * getNoOfRequestSameSec()+1);
                     */
                    if (networkLoadVO.getCurrentTransactionLoad() > 0)
                        networkLoadVO.setCurrentTransactionLoad(networkLoadVO.getCurrentTransactionLoad() - 1);
                }
                // Decrease the Current Transaction Count in Case of TXN load
                // Type
                else if (p_event == LoadControllerI.DEC_LAST_TRANS_COUNT) {
                    if (networkLoadVO.getCurrentTransactionLoad() > 0)
                        networkLoadVO.setCurrentTransactionLoad(networkLoadVO.getCurrentTransactionLoad() - 1);
                } else if (p_event == LoadControllerI.REFUSED_FROM_QUEUE) {
                    // Decrease the Current Transaction Count in Case of TXN
                    // load Type
                    if (networkLoadVO.getCurrentTransactionLoad() > 0)
                        networkLoadVO.setCurrentTransactionLoad(networkLoadVO.getCurrentTransactionLoad() - 1);
                }
            }

        }
        // _log.info("decreaseCurrentNetworkLoad","networkLoadVO="+networkLoadVO);
    }

    /**
     * This method checks the network load whether the request can be processed
     * or not.
     * 
     * @param p_networkID
     * @return boolean : True means network load can cater this request, false
     *         means refuse the request
     */
    public boolean checkSystemNetworkLoad(String p_networkID, String p_queueForAll) {
        _log.info("checkSystemNetworkLoad", "Entered p_networkID=" + p_networkID);

        boolean retStatus = true;
        long currentTime = System.currentTimeMillis();
        Timestamp timeStampVal = new Timestamp(currentTime);
        long previousUPTime = 0;
        boolean isSameSecRequest = false;
        String instanceID = LoadControllerCache.getInstanceID();

        NetworkLoadVO networkLoadVO = (NetworkLoadVO) LoadControllerCache.getNetworkLoadHash().get(instanceID + "_" + p_networkID);
        String loadType = LoadControllerCache.getLoadType();
        if (_log.isDebugEnabled())
            _log.debug("checkSystemNetworkLoad", "Load Type to be used=" + loadType);

        if (networkLoadVO != null) {
            // ------------------------------------------------------------------------------------------------------
            lock.lock();
            try {
                if (!("Y".equalsIgnoreCase(p_queueForAll))) {
                    // Processing for TPS Logic
                    if (LoadControllerI.LOAD_CONTROLLER_TPS_TYPE.equalsIgnoreCase(loadType)) {
                        // Check Same Second Request
                        // If same second request check
                        // _noOfRequestSameSec<_networkCurrentTPS and pass the
                        // request
                        // Current Sec <> Previous Second update
                        // _previousSecond=current and _noOfRequestSameSec=0
                        isSameSecRequest = LoadControllerUtil.checkSameSecondRequest(networkLoadVO.getPreviousSecond(), currentTime);
                        if (isSameSecRequest) {
                            if (networkLoadVO.getNoOfRequestSameSec() < networkLoadVO.getCurrentTPS()) {
                                if (_log.isDebugEnabled())
                                    _log.debug("checkSystemNetworkLoad", "Request in Same Second and is within the allowed limit=" + networkLoadVO.getCurrentTPS() + " Current =" + networkLoadVO.getNoOfRequestSameSec() + " , checking for Transaction Load");

                                // Check with the current Transaction Count
                                retStatus = checkSystemCurrentNetworkLoad(networkLoadVO, timeStampVal);
                                if (retStatus) {
                                    networkLoadVO.setLastTxnProcessStartTime(timeStampVal);
                                    networkLoadVO.setNoOfRequestSameSec(networkLoadVO.getNoOfRequestSameSec() + 1);
                                }
                            } else {
                                // Refuse the request
                                retStatus = false;
                                // networkLoadVO.setTotalRefusedCount(networkLoadVO.getTotalRefusedCount()+1);
                                // networkLoadVO.setLastRefusedTime(timeStampVal);
                                if (_log.isDebugEnabled())
                                    _log.debug("checkSystemNetworkLoad", "Request in Same Second and is not within the allowed limit=" + networkLoadVO.getCurrentTPS() + " Current =" + networkLoadVO.getNoOfRequestSameSec() + " , Refusing the request");

                            }
                        } else {
                            if (_log.isDebugEnabled())
                                _log.debug("checkSystemNetworkLoad", "Request is Not in Same Second , checking for Transaction Load");

                            // Check with the current Transaction Count
                            retStatus = checkSystemCurrentNetworkLoad(networkLoadVO, timeStampVal);
                            if (retStatus) {
                                networkLoadVO.setPreviousSecond(currentTime);
                                networkLoadVO.setLastTxnProcessStartTime(timeStampVal);
                                networkLoadVO.setNoOfRequestSameSec(1);
                            }
                        }
                    } else {
                        if (_log.isDebugEnabled())
                            _log.debug("checkSystemNetworkLoad", "Not TPS based checking for Transaction Load");
                        // Check with the current Transaction Count
                        retStatus = checkSystemCurrentNetworkLoad(networkLoadVO, timeStampVal);
                    }
                    // Store the First Request Time
                    if (networkLoadVO.getFirstRequestCount() == 0) {
                        networkLoadVO.setFirstRequestCount(1);
                        networkLoadVO.setFirstRequestTime(currentTime);
                    }
                    // Increase the Received Count for the Instance
                    networkLoadVO.setRecievedCount(networkLoadVO.getRecievedCount() + 1);
                    networkLoadVO.setLastReceievedTime(timeStampVal);
                } else {
                    retStatus = true;
                    // Store the First Request Time
                    if (networkLoadVO.getFirstRequestCount() == 0) {
                        networkLoadVO.setFirstRequestCount(1);
                        networkLoadVO.setFirstRequestTime(currentTime);
                    }
                    // Increase the Received Count for the Instance
                    networkLoadVO.setRecievedCount(networkLoadVO.getRecievedCount() + 1);
                    networkLoadVO.setLastReceievedTime(timeStampVal);
                }

            } finally {
                lock.unlock();
            }
        }
        _log.info("checkSystemNetworkLoad", "Exiting with networkLoadVO=" + networkLoadVO + "retStatus:" + retStatus);
        return retStatus;
    }

    /**
     * Method to check the current number of request present in an network with
     * the available load
     * True= Successful, false=Refused
     * 
     * @param p_networkLoadVO
     * @param p_timeStampVal
     * @return boolean
     */
    public static boolean checkSystemCurrentNetworkLoad(NetworkLoadVO p_networkLoadVO, Timestamp p_timeStampVal) {
        _log.info("checkSystemCurrentNetworkLoad", "p_networkLoadVO=" + p_networkLoadVO + " p_timeStampVal=" + p_timeStampVal);

        boolean retStatus = false;
        // Check with the current Transaction Count
        if (p_networkLoadVO.getCurrentTransactionLoad() < p_networkLoadVO.getTransactionLoad()) {
            // Increase the counters
            retStatus = true;
            p_networkLoadVO.setCurrentTransactionLoad(p_networkLoadVO.getCurrentTransactionLoad() + 1);
            p_networkLoadVO.setRequestCount(p_networkLoadVO.getRequestCount() + 1);
            p_networkLoadVO.setLastTxnProcessStartTime(p_timeStampVal);
        } else // Refuse the request
        {
            retStatus = false;
            // p_networkLoadVO.setTotalRefusedCount(p_networkLoadVO.getTotalRefusedCount()+1);
            // p_networkLoadVO.setLastRefusedTime(p_timeStampVal);
        }
        _log.info("checkSystemCurrentNetworkLoad", "Exiting with Status (True mean Success, False means Refused=" + retStatus);
        return retStatus;
    }

}
