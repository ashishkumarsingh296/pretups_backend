package com.btsl.loadcontroller;

import java.sql.Timestamp;
import java.util.concurrent.locks.ReentrantLock;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;

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
    /**
	 * 
	 */
	private static Log _log = LogFactory.getLog(InstanceLoadController.class.getName());
    private static final ReentrantLock lock = new ReentrantLock();
    /**
     * Method to check whether the instance can cater the request or not
     * 
     * @param p_instanceID
     * @param p_event
     * @return boolean: True means request can be processed, false means request
     *         is refused
     */
    public boolean checkInstanceLoad(String p_instanceID, int p_event) {
    	StringBuilder loggerValue= new StringBuilder(); 
    	loggerValue.append("Entered p_instanceID=");
    	loggerValue.append(p_instanceID);
    	loggerValue.append(" p_event:");
    	loggerValue.append(p_event);
        _log.info("checkInstanceLoad", loggerValue);

        InstanceLoadVO instanceLoadVO = null;
        boolean retStatus = true;

        long currentTime = System.currentTimeMillis();
        Timestamp timeStampVal = new Timestamp(currentTime);
        boolean isSameSecRequest = false;

        instanceLoadVO = (InstanceLoadVO) LoadControllerCache.getInstanceLoadHash().get(p_instanceID);
        String loadType = LoadControllerCache.getLoadType();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Load Type to be used=");
        	loggerValue.append(loadType);
            _log.debug("checkInstanceLoad", loggerValue);
        }

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
                                if (_log.isDebugEnabled()) {
                                	loggerValue.setLength(0);
                                	loggerValue.append("Request in Same Second and is within the allowed limit=" );
                                	loggerValue.append(instanceLoadVO.getCurrentTPS());
                                	loggerValue.append(" Current =");
                                	loggerValue.append(instanceLoadVO.getNoOfRequestSameSec());
                                	loggerValue.append(" , checking for Transaction Load");
                                    _log.debug("checkInstanceLoad", loggerValue);
                                }
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
                                if (_log.isDebugEnabled()) {
                                	loggerValue.setLength(0);
                                	loggerValue.append("Request in Same Second and is not within the allowed limit=");
                                	loggerValue.append(instanceLoadVO.getCurrentTPS());
                                	loggerValue.append(" Current =");
                                	loggerValue.append(instanceLoadVO.getNoOfRequestSameSec());
                                	loggerValue.append(" , Refusing the request");
                                    _log.debug("checkInstanceLoad", loggerValue);
                                }
                            }
                        } else {
                            if (_log.isDebugEnabled()) {
                                _log.debug("checkInstanceLoad", "Request is Not in Same Second , checking for Transaction Load");
                            }

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
                        if (_log.isDebugEnabled()) {
                            _log.debug("checkInstanceLoad", "Not TPS based checking for Transaction Load");
                        }

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
                    if (_log.isDebugEnabled()) {
                        _log.debug("checkInstanceLoad", "Instance is blocked :Request cannot be processed");
                    }
                }
            } finally {
                lock.unlock();
            }
        }
        loggerValue.setLength(0);
    	loggerValue.append("Exiting with instanceLoadVO=");
    	loggerValue.append(instanceLoadVO);
    	loggerValue.append("retStatus:");
    	loggerValue.append(retStatus);
        _log.info("checkInstanceLoad",loggerValue);
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
        if (LoadControllerCache.getInstanceLoadHash() == null || LoadControllerCache.getInstanceLoadHash().isEmpty()) {
            return;
        }

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
                    if (instanceLoadVO.getCurrentTransactionLoad() > 0) {
                        instanceLoadVO.setCurrentTransactionLoad(instanceLoadVO.getCurrentTransactionLoad() - 1);
                    }
                }
                if (p_event == LoadControllerI.REFUSED_FROM_QUEUE) {
                    // Decrease the Current Transaction Count in Case of TXN
                    // load Type
                    if (instanceLoadVO.getCurrentTransactionLoad() > 0) {
                        instanceLoadVO.setCurrentTransactionLoad(instanceLoadVO.getCurrentTransactionLoad() - 1);
                    }
                }
            } else if (LoadControllerI.LOAD_CONTROLLER_TPS_TYPE.equalsIgnoreCase(loadType)) {
                if (p_event == LoadControllerI.DEC_SAME_SEC_RES_COUNT)
                {                    
                    if (instanceLoadVO.getCurrentTransactionLoad() > 0) {
                        instanceLoadVO.setCurrentTransactionLoad(instanceLoadVO.getCurrentTransactionLoad() - 1);
                    }
                } else if (p_event == LoadControllerI.DEC_LAST_TRANS_COUNT) {
                    if (instanceLoadVO.getCurrentTransactionLoad() > 0) {
                        instanceLoadVO.setCurrentTransactionLoad(instanceLoadVO.getCurrentTransactionLoad() - 1);
                    }
                } else if (p_event == LoadControllerI.REFUSED_FROM_QUEUE) {
                    // Decrease the Current Transaction Count in Case of TXN
                    // load Type
                    if (instanceLoadVO.getCurrentTransactionLoad() > 0) {
                        instanceLoadVO.setCurrentTransactionLoad(instanceLoadVO.getCurrentTransactionLoad() - 1);
                    }
                }
            }
        }
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
    	StringBuilder loggerValue= new StringBuilder();
    	loggerValue.append("Entered p_instanceID=");
    	loggerValue.append(p_instanceID);
		loggerValue.append(" p_event:");
    	loggerValue.append(p_event);
		loggerValue.append(" p_queueForAll=");
    	loggerValue.append(p_queueForAll);
        _log.info("checkSystemInstanceLoad", loggerValue);

        InstanceLoadVO instanceLoadVO = null;
        boolean retStatus = true;

        long currentTime = System.currentTimeMillis();
        Timestamp timeStampVal = new Timestamp(currentTime);
        boolean isSameSecRequest = false;

        instanceLoadVO = (InstanceLoadVO) LoadControllerCache.getInstanceLoadHash().get(p_instanceID);
        String loadType = LoadControllerCache.getLoadType();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Load Type to be used=");
        	loggerValue.append(loadType);
            _log.debug("checkSystemInstanceLoad", loggerValue);
        }

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
                                        if (_log.isDebugEnabled()) {
                                        	StringBuffer msg=new StringBuffer("");
                                        	msg.append("Request in Same Second and is within the allowed limit=");
                                        	msg.append(instanceLoadVO.getCurrentTPS());
                                        	msg.append(" Current =");
                                        	msg.append(instanceLoadVO.getNoOfRequestSameSec());
                                        	msg.append(" , checking for Transaction Load");
                                        	
                                        	String message=msg.toString();
                                            _log.debug("checkSystemInstanceLoad", message);
                                        }
                                        retStatus = checkSystemCurrentInstanceLoad(instanceLoadVO, timeStampVal);
                                        if (retStatus) {
                                            // Increase the counters
                                            instanceLoadVO.setLastTxnProcessStartTime(timeStampVal);
                                            instanceLoadVO.setNoOfRequestSameSec(instanceLoadVO.getNoOfRequestSameSec() + 1);
                                        }
                                    } else {
                                        // Refuse the request
                                        retStatus = false;
                                      
                                        if (_log.isDebugEnabled())
                                        {
                                        	StringBuffer msg=new StringBuffer("");
                                        	msg.append("Request in Same Second and is within the allowed limit=");
                                        	msg.append(instanceLoadVO.getCurrentTPS());
                                        	msg.append(" Current =");
                                        	msg.append(instanceLoadVO.getNoOfRequestSameSec());
                                        	msg.append(" , Refusing the request");
                                        	
                                        	String message=msg.toString();
                                            _log.debug("checkSystemInstanceLoad", message);
                                        }
                                    }
                                } else {
                                    if (_log.isDebugEnabled()) {
                                        _log.debug("checkSystemInstanceLoad", "Request is Not in Same Second , checking for Transaction Load");
                                    }

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
                                if (_log.isDebugEnabled()) {
                                    _log.debug("checkSystemInstanceLoad", "Not TPS based checking for Transaction Load");
                                }

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
                        if (_log.isDebugEnabled()) {
                            _log.debug("checkSystemInstanceLoad", "Request cannot be processed queue is full");
                        }
                    }
                } else {
                    instanceLoadVO.setTotalRefusedCount(instanceLoadVO.getTotalRefusedCount() + 1);
                    instanceLoadVO.setLastRefusedTime(timeStampVal);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "InstanceLoadController[checkSystemInstanceLoad]", "", "", "", "Refusing Request :" + PretupsErrorCodesI.REQUEST_REFUSE);
                    throw new BTSLBaseException("InstanceLoadController", "checkSystemInstanceLoad", PretupsErrorCodesI.REQUEST_REFUSE);
                }
            } finally {
                lock.unlock();
            }
        }
        loggerValue.setLength(0);
    	loggerValue.append("Exiting with instanceLoadVO=");
    	loggerValue.append(instanceLoadVO);
		loggerValue.append("retStatus:");
    	loggerValue.append(retStatus);
        _log.info("checkSystemInstanceLoad", loggerValue);
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
        }
        
        else // Refuse the request
        {
            retStatus = false;            
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
        final String METHOD_NAME = "getCurrentAvailableInstanceCounter";
        long currentcount = 0;
        InstanceLoadVO instanceLoadVO = null;
        try {
            instanceLoadVO = (InstanceLoadVO) LoadControllerCache.getInstanceLoadHash().get(p_instanceID);
            currentcount = instanceLoadVO.getTransactionLoad() - instanceLoadVO.getCurrentTransactionLoad();
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        return currentcount;
    }

}
