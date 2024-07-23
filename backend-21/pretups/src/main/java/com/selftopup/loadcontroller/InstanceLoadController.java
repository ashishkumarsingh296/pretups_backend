package com.selftopup.loadcontroller;

import java.sql.Timestamp;
import java.util.concurrent.locks.ReentrantLock;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;

/*
 * InstanceLoadController.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 14/07/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Class to process the instance load request
 */

public class InstanceLoadController {
    private static Log _log = LogFactory.getLog(InstanceLoadController.class.getName());
    private final static ReentrantLock lock = new ReentrantLock();

    /**
     * Method to check whether the instance can cater the request or not
     * 
     * @param p_instanceID
     * @param p_event
     * @return boolean: True means request can be processed, false means request
     *         is refused
     */
    public boolean checkInstanceLoad(String p_instanceID, int p_event) {
        _log.info("checkInstanceLoad", "Entered p_instanceID=" + p_instanceID + " p_event:" + p_event);

        InstanceLoadVO instanceLoadVO = null;
        boolean retStatus = true;

        long currentTime = System.currentTimeMillis();
        Timestamp timeStampVal = new Timestamp(currentTime);
        boolean isSameSecRequest = false;

        instanceLoadVO = (InstanceLoadVO) LoadControllerCache.getInstanceLoadHash().get(p_instanceID);
        String loadType = LoadControllerCache.getLoadType();
        if (_log.isDebugEnabled())
            _log.debug("checkInstanceLoad", "Load Type to be used=" + loadType);

        if (instanceLoadVO != null) {
            // ------------------------------------------------------------------------------------------------------
            lock.lock();
            try {
                if (!instanceLoadVO.isInstanceLoadStatus()) {
                    // Processing for TPS Logic
                    if (LoadControllerI.LOAD_CONTROLLER_TPS_TYPE.equalsIgnoreCase(loadType)) {
                        // Check Same Second Request
                        // If same second request check
                        // _noOfRequestSameSec<_instanceCurrentTPS and pass the
                        // request
                        // Current Sec <> Previous Second update
                        // _previousSecond=current and _noOfRequestSameSec=0
                        isSameSecRequest = LoadControllerUtil.checkSameSecondRequest(instanceLoadVO.getPreviousSecond(), currentTime);
                        if (isSameSecRequest) {
                            if (instanceLoadVO.getNoOfRequestSameSec() < instanceLoadVO.getCurrentTPS()) {
                                // Check with the current Transaction Count
                                if (_log.isDebugEnabled())
                                    _log.debug("checkInstanceLoad", "Request in Same Second and is within the allowed limit=" + instanceLoadVO.getCurrentTPS() + " Current =" + instanceLoadVO.getNoOfRequestSameSec() + " , checking for Transaction Load");
                                retStatus = checkCurrentInstanceLoad(instanceLoadVO, timeStampVal);
                                if (retStatus) {
                                    // Increase the counters
                                    instanceLoadVO.setLastTxnProcessStartTime(timeStampVal);
                                    instanceLoadVO.setNoOfRequestSameSec(instanceLoadVO.getNoOfRequestSameSec() + 1);
                                }
                            } else {
                                // Refuse the request
                                retStatus = false;
                                instanceLoadVO.setTotalRefusedCount(instanceLoadVO.getTotalRefusedCount() + 1);
                                instanceLoadVO.setLastRefusedTime(timeStampVal);
                                if (_log.isDebugEnabled())
                                    _log.debug("checkInstanceLoad", "Request in Same Second and is not within the allowed limit=" + instanceLoadVO.getCurrentTPS() + " Current =" + instanceLoadVO.getNoOfRequestSameSec() + " , Refusing the request");
                            }
                        } else {
                            if (_log.isDebugEnabled())
                                _log.debug("checkInstanceLoad", "Request is Not in Same Second , checking for Transaction Load");

                            // Check with the current Transaction Count
                            retStatus = checkCurrentInstanceLoad(instanceLoadVO, timeStampVal);
                            if (retStatus) {
                                instanceLoadVO.setPreviousSecond(currentTime);
                                instanceLoadVO.setNoOfRequestSameSec(1);
                                instanceLoadVO.setLastTxnProcessStartTime(timeStampVal);
                            }
                        }
                    } else // If Load Type is Transaction Number Based
                    {
                        if (_log.isDebugEnabled())
                            _log.debug("checkInstanceLoad", "Not TPS based checking for Transaction Load");

                        // Check with the current Transaction Count
                        retStatus = checkCurrentInstanceLoad(instanceLoadVO, timeStampVal);
                    }
                    // Store the First Request Time
                    if (instanceLoadVO.getFirstRequestCount() == 0) {
                        instanceLoadVO.setFirstRequestCount(1);
                        instanceLoadVO.setFirstRequestTime(currentTime);
                    }
                    // Increase the Recieved Count for the Instance
                    instanceLoadVO.setRecievedCount(instanceLoadVO.getRecievedCount() + 1);
                    instanceLoadVO.setLastReceievedTime(timeStampVal);
                } else {
                    // Refuse the request
                    retStatus = false;
                    instanceLoadVO.setTotalRefusedCount(instanceLoadVO.getTotalRefusedCount() + 1);
                    instanceLoadVO.setLastRefusedTime(timeStampVal);
                    if (_log.isDebugEnabled())
                        _log.debug("checkInstanceLoad", "Instance is blocked :Request cannot be processed");
                }
            } finally {
                lock.unlock();
            }
        }
        _log.info("checkInstanceLoad", "Exiting with instanceLoadVO=" + instanceLoadVO + "retStatus:" + retStatus);
        return retStatus;
    }

    /**
     * This method decrease the total no of requests present in an instance
     * 
     * @param p_instanceID
     * @param p_event
     */
    public void decreaseCurrentInstanceLoad(String p_instanceID, int p_event) {
        lock.lock();
        try {
            decreaseCurrentInstanceLoadUnlock(p_instanceID, p_event);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Method to check the current number of request present in an instance with
     * the available load
     * True= Successful, false=Refused
     * 
     * @param p_instanceLoadVO
     * @param p_timeStampVal
     * @return boolean
     */
    public boolean checkCurrentInstanceLoad(InstanceLoadVO p_instanceLoadVO, Timestamp p_timeStampVal) {
        _log.info("checkCurrentInstanceLoad", "p_instanceLoadVO=" + p_instanceLoadVO + " p_timeStampVal=" + p_timeStampVal);

        boolean retStatus = false;
        // Check with the current Transaction Count
        if (p_instanceLoadVO.getCurrentTransactionLoad() < p_instanceLoadVO.getTransactionLoad()) {
            // Increase the counters
            retStatus = true;
            p_instanceLoadVO.setCurrentTransactionLoad(p_instanceLoadVO.getCurrentTransactionLoad() + 1);
            p_instanceLoadVO.setRequestCount(p_instanceLoadVO.getRequestCount() + 1);
            p_instanceLoadVO.setLastTxnProcessStartTime(p_timeStampVal);
        } else // Refuse the request
        {

            retStatus = false;
            p_instanceLoadVO.setTotalRefusedCount(p_instanceLoadVO.getTotalRefusedCount() + 1);
            p_instanceLoadVO.setLastRefusedTime(p_timeStampVal);
        }
        _log.info("checkCurrentInstanceLoad", "Exiting with Status (True mean Success, False means Refused=" + retStatus);
        return retStatus;
    }

    public void decreaseCurrentInstanceLoadUnlock(String p_instanceID, int p_event) {
        // if(_log.isDebugEnabled())_log.debug("decreaseCurrentInstanceLoad","Entered with p_instanceID="+p_instanceID+
        // " p_event:"+p_event);
        if (LoadControllerCache.getInstanceLoadHash() == null || LoadControllerCache.getInstanceLoadHash().size() == 0)
            return;

        // Decrease only in case of Load Type TXN and not when the system is
        // running on TPS
        String loadType = LoadControllerCache.getLoadType();
        InstanceLoadVO instanceLoadVO = (InstanceLoadVO) LoadControllerCache.getInstanceLoadHash().get(p_instanceID);

        if (instanceLoadVO != null) {
            // _log.info("decreaseCurrentInstanceLoad","Current Transaction Load="+instanceLoadVO.getCurrentTransactionLoad());
            // ------------------------------------------------------------------------------------------------------

            if (LoadControllerI.LOAD_CONTROLLER_TXN_TYPE.equalsIgnoreCase(loadType)) {
                // Decrease the Current Transaction Count in Case of TXN load
                // Type
                if (p_event == LoadControllerI.DEC_LAST_TRANS_COUNT) {
                    if (instanceLoadVO.getCurrentTransactionLoad() > 0)
                        instanceLoadVO.setCurrentTransactionLoad(instanceLoadVO.getCurrentTransactionLoad() - 1);
                }
                if (p_event == LoadControllerI.REFUSED_FROM_QUEUE) {
                    // Decrease the Current Transaction Count in Case of TXN
                    // load Type
                    if (instanceLoadVO.getCurrentTransactionLoad() > 0)
                        instanceLoadVO.setCurrentTransactionLoad(instanceLoadVO.getCurrentTransactionLoad() - 1);
                }
            } else if (LoadControllerI.LOAD_CONTROLLER_TPS_TYPE.equalsIgnoreCase(loadType)) {
                if (p_event == LoadControllerI.DEC_SAME_SEC_RES_COUNT) {
                    /*
                     * if(instanceLoadVO.getNoOfRequestSameSec()>0)
                     * instanceLoadVO.setNoOfRequestSameSec(instanceLoadVO.
                     * getNoOfRequestSameSec()-1);
                     */
                    if (instanceLoadVO.getCurrentTransactionLoad() > 0)
                        instanceLoadVO.setCurrentTransactionLoad(instanceLoadVO.getCurrentTransactionLoad() - 1);
                } else if (p_event == LoadControllerI.DEC_LAST_TRANS_COUNT) {
                    if (instanceLoadVO.getCurrentTransactionLoad() > 0)
                        instanceLoadVO.setCurrentTransactionLoad(instanceLoadVO.getCurrentTransactionLoad() - 1);
                } else if (p_event == LoadControllerI.REFUSED_FROM_QUEUE) {
                    // Decrease the Current Transaction Count in Case of TXN
                    // load Type
                    if (instanceLoadVO.getCurrentTransactionLoad() > 0)
                        instanceLoadVO.setCurrentTransactionLoad(instanceLoadVO.getCurrentTransactionLoad() - 1);
                }
            }

        }
        // _log.info("decreaseCurrentInstanceLoad","instanceLoadVO="+instanceLoadVO);
    }

    /**
     * Method to check whether the instance can cater the request or not
     * 
     * @param p_instanceID
     * @param p_event
     * @return boolean: True means request can be processed, false means request
     *         is refused
     */
    public boolean checkSystemInstanceLoad(String p_instanceID, int p_event, String p_queueForAll) throws BTSLBaseException {
        _log.info("checkSystemInstanceLoad", "Entered p_instanceID=" + p_instanceID + " p_event:" + p_event + " p_queueForAll=" + p_queueForAll);

        InstanceLoadVO instanceLoadVO = null;
        boolean retStatus = true;

        long currentTime = System.currentTimeMillis();
        Timestamp timeStampVal = new Timestamp(currentTime);
        boolean isSameSecRequest = false;

        instanceLoadVO = (InstanceLoadVO) LoadControllerCache.getInstanceLoadHash().get(p_instanceID);
        String loadType = LoadControllerCache.getLoadType();
        if (_log.isDebugEnabled())
            _log.debug("checkSystemInstanceLoad", "Load Type to be used=" + loadType);

        if (instanceLoadVO != null) {
            // ------------------------------------------------------------------------------------------------------
            lock.lock();
            try {
                if (!instanceLoadVO.isInstanceLoadStatus()) {
                    if (p_event == LoadControllerI.ENTRY_IN_QUEUE) {
                        if (!("Y".equalsIgnoreCase(p_queueForAll))) {
                            // Processing for TPS Logic
                            if (LoadControllerI.LOAD_CONTROLLER_TPS_TYPE.equalsIgnoreCase(loadType)) {
                                // Check Same Second Request
                                // If same second request check
                                // _noOfRequestSameSec<_instanceCurrentTPS and
                                // pass the request
                                // Current Sec <> Previous Second update
                                // _previousSecond=current and
                                // _noOfRequestSameSec=0
                                isSameSecRequest = LoadControllerUtil.checkSameSecondRequest(instanceLoadVO.getPreviousSecond(), currentTime);
                                if (isSameSecRequest) {
                                    if (instanceLoadVO.getNoOfRequestSameSec() < instanceLoadVO.getCurrentTPS()) {
                                        // Check with the current Transaction
                                        // Count
                                        if (_log.isDebugEnabled())
                                            _log.debug("checkSystemInstanceLoad", "Request in Same Second and is within the allowed limit=" + instanceLoadVO.getCurrentTPS() + " Current =" + instanceLoadVO.getNoOfRequestSameSec() + " , checking for Transaction Load");
                                        retStatus = checkSystemCurrentInstanceLoad(instanceLoadVO, timeStampVal);
                                        if (retStatus) {
                                            // Increase the counters
                                            instanceLoadVO.setLastTxnProcessStartTime(timeStampVal);
                                            instanceLoadVO.setNoOfRequestSameSec(instanceLoadVO.getNoOfRequestSameSec() + 1);
                                        }
                                    } else {
                                        // Refuse the request
                                        retStatus = false;
                                        // instanceLoadVO.setTotalRefusedCount(instanceLoadVO.getTotalRefusedCount()+1);
                                        // instanceLoadVO.setLastRefusedTime(timeStampVal);
                                        if (_log.isDebugEnabled())
                                            _log.debug("checkSystemInstanceLoad", "Request in Same Second and is not within the allowed limit=" + instanceLoadVO.getCurrentTPS() + " Current =" + instanceLoadVO.getNoOfRequestSameSec() + " , Refusing the request");
                                    }
                                } else {
                                    if (_log.isDebugEnabled())
                                        _log.debug("checkSystemInstanceLoad", "Request is Not in Same Second , checking for Transaction Load");

                                    // Check with the current Transaction Count
                                    retStatus = checkSystemCurrentInstanceLoad(instanceLoadVO, timeStampVal);
                                    if (retStatus) {
                                        instanceLoadVO.setPreviousSecond(currentTime);
                                        instanceLoadVO.setNoOfRequestSameSec(1);
                                        instanceLoadVO.setLastTxnProcessStartTime(timeStampVal);
                                    }
                                }
                            } else // If Load Type is Transaction Number Based
                            {
                                if (_log.isDebugEnabled())
                                    _log.debug("checkSystemInstanceLoad", "Not TPS based checking for Transaction Load");

                                // Check with the current Transaction Count
                                retStatus = checkSystemCurrentInstanceLoad(instanceLoadVO, timeStampVal);
                            }
                            // Store the First Request Time
                            if (instanceLoadVO.getFirstRequestCount() == 0) {
                                instanceLoadVO.setFirstRequestCount(1);
                                instanceLoadVO.setFirstRequestTime(currentTime);
                            }
                            // Increase the Received Count for the Instance
                            instanceLoadVO.setRecievedCount(instanceLoadVO.getRecievedCount() + 1);
                            instanceLoadVO.setLastReceievedTime(timeStampVal);
                        } else {
                            retStatus = true;
                            // Store the First Request Time
                            if (instanceLoadVO.getFirstRequestCount() == 0) {
                                instanceLoadVO.setFirstRequestCount(1);
                                instanceLoadVO.setFirstRequestTime(currentTime);
                            }
                            // Increase the Received Count for the Instance
                            instanceLoadVO.setRecievedCount(instanceLoadVO.getRecievedCount() + 1);
                            instanceLoadVO.setLastReceievedTime(timeStampVal);
                        }
                    } else if (p_event == LoadControllerI.REFUSED_FROM_QUEUE || p_event == LoadControllerI.FAIL_BEF_PROCESSED_QUEUE) {
                        // Refuse the request
                        retStatus = true;
                        instanceLoadVO.setTotalRefusedCount(instanceLoadVO.getTotalRefusedCount() + 1);
                        instanceLoadVO.setLastRefusedTime(timeStampVal);
                        if (_log.isDebugEnabled())
                            _log.debug("checkSystemInstanceLoad", "Request cannot be processed queue is full");
                    }
                } else {
                    instanceLoadVO.setTotalRefusedCount(instanceLoadVO.getTotalRefusedCount() + 1);
                    instanceLoadVO.setLastRefusedTime(timeStampVal);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "InstanceLoadController[checkSystemInstanceLoad]", "", "", "", "Refusing Request :" + SelfTopUpErrorCodesI.REQUEST_REFUSE);
                    throw new BTSLBaseException("InstanceLoadController", "checkSystemInstanceLoad", SelfTopUpErrorCodesI.REQUEST_REFUSE);
                }
            } finally {
                lock.unlock();
            }
        }
        _log.info("checkSystemInstanceLoad", "Exiting with instanceLoadVO=" + instanceLoadVO + "retStatus:" + retStatus);
        return retStatus;
    }

    /**
     * Method to check the current number of request present in an instance with
     * the available load
     * True= Successful, false=Refused
     * 
     * @param p_instanceLoadVO
     * @param p_timeStampVal
     * @return boolean
     */
    public boolean checkSystemCurrentInstanceLoad(InstanceLoadVO p_instanceLoadVO, Timestamp p_timeStampVal) {
        _log.info("checkSystemCurrentInstanceLoad", "p_instanceLoadVO=" + p_instanceLoadVO + " p_timeStampVal=" + p_timeStampVal);
        boolean retStatus = false;
        // Check with the current Transaction Count
        if (p_instanceLoadVO.getCurrentTransactionLoad() < p_instanceLoadVO.getTransactionLoad()) {
            // Increase the counters
            retStatus = true;
            p_instanceLoadVO.setCurrentTransactionLoad(p_instanceLoadVO.getCurrentTransactionLoad() + 1);
            p_instanceLoadVO.setRequestCount(p_instanceLoadVO.getRequestCount() + 1);
            p_instanceLoadVO.setLastTxnProcessStartTime(p_timeStampVal);
        } else // Refuse the request
        {
            retStatus = false;
            // p_instanceLoadVO.setTotalRefusedCount(p_instanceLoadVO.getTotalRefusedCount()+1);
            // p_instanceLoadVO.setLastRefusedTime(p_timeStampVal);
        }
        _log.info("checkSystemCurrentInstanceLoad", "Exiting with Status (True mean Success, False means Refused=" + retStatus);
        return retStatus;
    }

    /**
     * Method to check the current number of request present in an instance with
     * the available load
     * 
     * @param p_instanceID
     * @author gaurav.pandey
     * 
     */
    public long getCurrentAvailableInstanceCounter(String p_instanceID) throws Exception {
        _log.info("getCurrentInstanceCounter", "p_instanceID=" + p_instanceID);
        long currentcount = 0;
        InstanceLoadVO instanceLoadVO = null;
        try {
            instanceLoadVO = (InstanceLoadVO) LoadControllerCache.getInstanceLoadHash().get(p_instanceID);
            currentcount = instanceLoadVO.getTransactionLoad() - instanceLoadVO.getCurrentTransactionLoad();
        } catch (Exception e) {
            _log.errorTrace("getCurrentAvailableInstanceCounter: Exception print stack trace:=", e);
        }
        return currentcount;
    }

}
