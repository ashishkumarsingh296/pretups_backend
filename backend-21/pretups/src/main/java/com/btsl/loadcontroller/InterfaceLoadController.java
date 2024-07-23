package com.btsl.loadcontroller;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.concurrent.locks.ReentrantLock;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.util.BTSLUtil;

/*
 * InterfaceLoadController.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 14/07/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Class to process the interface load request
 */

public class InterfaceLoadController implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Log _log = LogFactory.getLog(InterfaceLoadController.class.getName());
    private static final ReentrantLock lock = new ReentrantLock();

    /**
     * This method checks the interface load whether the request can be
     * processed or not. else check for queue whether it needs to added in that
     * 
     * @param p_networkID
     * @param p_interfaceID
     * @param p_transactionNo
     * @param p_transferVO
     *            Transfer VO needed in case of Queue Addition
     * @param p_checkQueueAddition
     *            : whether to check that request needs to be added in queue or
     *            not
     * @return int 0 means success, 1=Added in Queue, 2 = Refuse
     */
    public int checkInterfaceLoad(String p_networkID, String p_interfaceID, String p_transactionNo, TransferVO p_transferVO, boolean p_checkQueueAddition) {
    	StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
    	loggerValue.append("Entered p_networkID=");
    	loggerValue.append(p_networkID);
		loggerValue.append(" p_interfaceID=");
    	loggerValue.append(p_interfaceID);
		loggerValue.append(" p_transactionNo=");
    	loggerValue.append(p_transactionNo);
    	loggerValue.append(" p_checkQueueAddition=");
    	loggerValue.append(p_checkQueueAddition);
        _log.info("checkInterfaceLoad", loggerValue);

        int retStatus = 2;
        long currentTime = System.currentTimeMillis();
        Timestamp timeStampVal = new Timestamp(currentTime);
        boolean isSameSecRequest = false;
        String instanceID = LoadControllerCache.getInstanceID();

        InterfaceLoadVO interfaceLoadVO = (InterfaceLoadVO) LoadControllerCache.getInterfaceLoadHash().get(instanceID + "_" + p_networkID + "_" + p_interfaceID);
        String loadType = LoadControllerCache.getLoadType();
        if (_log.isDebugEnabled()) {
			loggerValue.setLength(0);
        	loggerValue.append("Load Type to be used=");
        	loggerValue.append(loadType);
            _log.debug("checkInterfaceLoad", loggerValue);
        }

        if (interfaceLoadVO != null) {
            // ------------------------------------------------------------------------------------------------------
            lock.lock();
            try {
                // Processing for TPS Logic, Queue in TPS or not?
                if (LoadControllerI.LOAD_CONTROLLER_TPS_TYPE.equalsIgnoreCase(loadType)) {
                    // Check Same Second Request
                    // If same second request check
                    // _noOfRequestSameSec<_networkCurrentTPS and pass the
                    // request
                    // Current Sec <> Previous Second update
                    // _previousSecond=current and _noOfRequestSameSec=0
                    isSameSecRequest = LoadControllerUtil.checkSameSecondRequest(interfaceLoadVO.getPreviousSecond(), currentTime);
                    if (isSameSecRequest) {
                        if (interfaceLoadVO.getNoOfRequestSameSec() < interfaceLoadVO.getCurrentTPS()) {
                            if (_log.isDebugEnabled()) {
                                _log.debug("checkInterfaceLoad", "Request in Same Second and is within the allowed limit=" + interfaceLoadVO.getCurrentTPS() + " Current =" + interfaceLoadVO.getNoOfRequestSameSec() + " , checking for Transaction Load");
                            }

                            // Increase the counters
                            retStatus = checkCurrentInterfaceLoad(interfaceLoadVO, currentTime, timeStampVal, p_transferVO, false);
                            if (retStatus == 0) {
                                interfaceLoadVO.setLastTxnProcessStartTime(timeStampVal);
                                interfaceLoadVO.setNoOfRequestSameSec(interfaceLoadVO.getNoOfRequestSameSec() + 1);
                            }
                        } else {
                            // Refuse the request
                            retStatus = 2;
                            interfaceLoadVO.setTotalRefusedCount(interfaceLoadVO.getTotalRefusedCount() + 1);
                            interfaceLoadVO.setLastRefusedTime(timeStampVal);
                            if (_log.isDebugEnabled()) {
                				loggerValue.setLength(0);
                            	loggerValue.append("Request in Same Second and is not within the allowed limit=");
                            	loggerValue.append(interfaceLoadVO.getCurrentTPS());
                				loggerValue.append(" Current =");
                            	loggerValue.append(interfaceLoadVO.getNoOfRequestSameSec());
                            	loggerValue.append(" , Refusing the request");
                                _log.debug("checkInterfaceLoad",loggerValue);
                            }
                        }
                    } else {
                        if (_log.isDebugEnabled()) {
                            _log.debug("checkInterfaceLoad", "Request is Not in Same Second , checking for Transaction Load");
                        }

                        retStatus = checkCurrentInterfaceLoad(interfaceLoadVO, currentTime, timeStampVal, p_transferVO, false);
                        if (retStatus == 0) {
                            interfaceLoadVO.setLastTxnProcessStartTime(timeStampVal);
                            interfaceLoadVO.setPreviousSecond(currentTime);
                            interfaceLoadVO.setNoOfRequestSameSec(1);
                        }
                    }
                } else {
                    if (_log.isDebugEnabled()) {
                        _log.debug("checkInterfaceLoad", "Not TPS based checking for Transaction Load");
                    }

                    retStatus = checkCurrentInterfaceLoad(interfaceLoadVO, currentTime, timeStampVal, p_transferVO, p_checkQueueAddition);
                }
                // Store the First Request Time
                if (interfaceLoadVO.getFirstRequestCount() == 0) {
                    interfaceLoadVO.setFirstRequestCount(1);
                    interfaceLoadVO.setFirstRequestTime(currentTime);
                }
                // Increase the Recieved Count for the Instance
                interfaceLoadVO.setRecievedCount(interfaceLoadVO.getRecievedCount() + 1);
                interfaceLoadVO.setLastReceievedTime(timeStampVal);
            } finally {
                lock.unlock();
            }
        }
		loggerValue.setLength(0);
    	loggerValue.append("Exiting with interfaceLoadVO=");
    	loggerValue.append(interfaceLoadVO);
		loggerValue.append("retStatus (0 means success, 1=Added in Queue, 2 = Refuse):");
    	loggerValue.append(retStatus);
        _log.info("checkInterfaceLoad", loggerValue);
        return retStatus;
    }

    /**
     * WHETHER THIS IS REQUIRED OR NOT:
     * If some inter mediary counters need to be stored then this can be used
     * 
     * @param p_event
     * @param p_networkID
     * @param p_interfaceID
     * @param p_transactionNo
     * @return boolean
     */
    public boolean incrementInterfaceCounts(int p_event, String p_networkID, String p_interfaceID, String p_transactionNo) {
    	StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
			loggerValue.setLength(0);
        	loggerValue.append(" Entered:: p_event=");
        	loggerValue.append(p_event);
			loggerValue.append("  p_networkID=");
        	loggerValue.append(p_networkID);
			loggerValue.append(" p_interfaceID :");
        	loggerValue.append(p_interfaceID);
			loggerValue.append(" p_transactionNo=");
        	loggerValue.append(p_transactionNo);
            _log.debug(" incrementInterfaceCounts() ", loggerValue);
        }
        final String METHOD_NAME = "incrementInterfaceCounts";
        boolean flag = false;
      

        String instanceID = LoadControllerCache.getInstanceID();
        if (BTSLUtil.isNullString(instanceID)) {
            instanceID = LoadControllerI.DEFAULT_INSTANCE_ID;
        }
        try {

            // if the _memoryHash is null then initalze it
            if (LoadControllerCache.getInterfaceLoadHash() == null || LoadControllerCache.getInterfaceLoadHash().isEmpty()) {
                LoadControllerCache.refreshInterfaceLoad();
            }

            InterfaceLoadVO interfaceLoadVO = (InterfaceLoadVO) LoadControllerCache.getInterfaceLoadHash().get(instanceID + "_" + p_networkID + "_" + p_interfaceID);

            if (interfaceLoadVO != null) {
                /*
                 * if(p_event==CSMSTypesI.PRETUPS_VALIDATION)
                 * {
                 * flag=true;
                 * loadBalancerVO.setCurrentValidateCount(loadBalancerVO.
                 * getCurrentValidateCount()+1);
                 * loadBalancerVO.setTotalValidateCount(loadBalancerVO.
                 * getTotalValidateCount()+1);
                 * }
                 * else if(p_event==CSMSTypesI.PRETUPS_TOPUP)
                 * {
                 * flag=true;
                 * loadBalancerVO.setCurrentTopUpCount(loadBalancerVO.
                 * getCurrentTopUpCount()+1);
                 * loadBalancerVO.setTotalTopUpCount(loadBalancerVO.
                 * getTotalTopUpCount()+1);
                 * loadBalancerVO.setLastTopUpTime(timeStampVal);
                 * }
                 */
            }

        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        return flag;
    }

    /**
     * This method decrease the various type of counters present in an interface
     * 
     * @param p_event
     * @param p_networkID
     * @param p_interfaceID
     * @param p_transactionID
     */
    public void decreaseCurrentInterfaceLoad(int p_event, String p_networkID, String p_interfaceID, String p_transactionID) {
        lock.lock();
        try {
            decreaseCurrentInterfaceLoadUnlock(p_event, p_networkID, p_interfaceID, p_transactionID);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Method to check the current number of request present in an interface
     * with the available load
     * 
     * @param p_interfaceLoadVO
     * @param p_currentTime
     * @param p_timeStampVal
     * @param p_transferVO
     * @param p_checkQueueAddition
     *            : whether to check that request needs to be added in queue or
     *            not
     * @return int 0 means success, 1=Added in Queue, 2 = Refuse
     */
    public int checkCurrentInterfaceLoad(InterfaceLoadVO p_interfaceLoadVO, long p_currentTime, Timestamp p_timeStampVal, TransferVO p_transferVO, boolean p_checkQueueAddition) {
    	StringBuilder loggerValue= new StringBuilder(); 
    	loggerValue.append("p_interfaceLoadVO=");
    	loggerValue.append(p_interfaceLoadVO);
		loggerValue.append(" Transfer ID=");
    	loggerValue.append(p_transferVO.getTransferID());
    	loggerValue.append(" p_currentTime=");
    	loggerValue.append(p_currentTime);
		loggerValue.append(" p_timeStampVal=");
    	loggerValue.append(p_timeStampVal);
    	loggerValue.append(" p_checkQueueAddition=");
    	loggerValue.append(p_checkQueueAddition);
        _log.info("checkCurrentInterfaceLoad", loggerValue);

        int retStatus = 2;
        // Check with the current Transaction Count
        if (p_interfaceLoadVO.getCurrentTransactionLoad() < p_interfaceLoadVO.getTransactionLoad()) {
            // Increase the counters
            retStatus = 0;
            p_interfaceLoadVO.setCurrentTransactionLoad(p_interfaceLoadVO.getCurrentTransactionLoad() + 1);
            p_interfaceLoadVO.setRequestCount(p_interfaceLoadVO.getRequestCount() + 1);
            p_interfaceLoadVO.setLastTxnProcessStartTime(p_timeStampVal);
        } else if (p_checkQueueAddition) {
            if (_log.isDebugEnabled()) {
                _log.debug("checkCurrentInterfaceLoad", "Checking For Queue Addition Current Size=" + p_interfaceLoadVO.getCurrentQueueSize() + " Allowed Queue Size=" + p_interfaceLoadVO.getQueueSize());
            }

            // TO DO :- Here the Queue Logic Will come and status returned will
            // be true-----
            if (p_interfaceLoadVO.getCurrentQueueSize() < p_interfaceLoadVO.getQueueSize()) {
                // Add in Queue
                p_transferVO.setQueueAdditionTime(p_currentTime);
                p_transferVO.setRequestThroughQueue(PretupsI.YES);
                p_interfaceLoadVO.getQueueList().add(p_transferVO);
                retStatus = 1;
                p_interfaceLoadVO.setCurrentQueueSize(p_interfaceLoadVO.getCurrentQueueSize() + 1);
                p_interfaceLoadVO.setRequestCount(p_interfaceLoadVO.getRequestCount() + 1);
                p_interfaceLoadVO.setLastTxnProcessStartTime(p_timeStampVal);
                p_interfaceLoadVO.setLastQueueAdditionTime(p_timeStampVal);
                if (_log.isDebugEnabled()) {
    				loggerValue.setLength(0);
                	loggerValue.append("Added in Queue for Interface=");
                	loggerValue.append(p_interfaceLoadVO.getInterfaceID());
    				loggerValue.append(" Queue Size=");
                	loggerValue.append(p_interfaceLoadVO.getQueueSize());
                	loggerValue.append(" Current =");
                	loggerValue.append(p_interfaceLoadVO.getCurrentQueueSize());
                    _log.debug("checkCurrentInterfaceLoad", loggerValue);
                }
            }
        } else {
            // Refuse the request
            retStatus = 2;
            p_interfaceLoadVO.setTotalRefusedCount(p_interfaceLoadVO.getTotalRefusedCount() + 1);
            p_interfaceLoadVO.setLastRefusedTime(p_timeStampVal);
        }
		loggerValue.setLength(0);
    	loggerValue.append("Exiting with Status (0 means success, 1=Added in Queue, 2 = Refuse)=");
    	loggerValue.append(retStatus);
        _log.info("checkCurrentInterfaceLoad", loggerValue);

        return retStatus;
    }

    public void decreaseCurrentInterfaceLoadUnlock(int p_event, String p_networkID, String p_interfaceID, String p_transactionID) {
        String instanceID = LoadControllerCache.getInstanceID();
        StringBuilder loggerValue= new StringBuilder(); 
        if (LoadControllerCache.getInterfaceLoadHash() == null || LoadControllerCache.getInterfaceLoadHash().isEmpty()) {
            return;
        }

        // Decrease only in case of Load Type TXN and not when the system is
        // running on TPS
        String loadType = LoadControllerCache.getLoadType();
        InterfaceLoadVO interfaceLoadVO = (InterfaceLoadVO) LoadControllerCache.getInterfaceLoadHash().get(instanceID + "_" + p_networkID + "_" + p_interfaceID);
        if (interfaceLoadVO != null) {

            if (LoadControllerI.LOAD_CONTROLLER_TXN_TYPE.equalsIgnoreCase(loadType)) {
                // Decrease the Current Transaction Count in Case of TXN load
                // Type and
                if (p_event == LoadControllerI.DEC_LAST_TRANS_COUNT) {
                    if (interfaceLoadVO.getCurrentTransactionLoad() > 0) {
                        interfaceLoadVO.setCurrentTransactionLoad(interfaceLoadVO.getCurrentTransactionLoad() - 1);
                    }
                    if (_log.isDebugEnabled()) {
                    	loggerValue.append("Entered p_networkID:");
                    	loggerValue.append(p_networkID);
        				loggerValue.append(" p_interfaceID=");
                    	loggerValue.append(p_interfaceID);
                    	loggerValue.append(" p_transactionID=");
                    	loggerValue.append(p_transactionID);
        				loggerValue.append("interfaceLoadVO.getCurrentTransactionLoad()=");
                    	loggerValue.append(interfaceLoadVO.getCurrentTransactionLoad());
                        _log.debug("decreaseCurrentInterfaceLoad", loggerValue);
                    }

                }
                // also decrease the Current Queue Count
                else if (p_event == LoadControllerI.PROCESSED_FROM_QUEUE) {
                    if (interfaceLoadVO.getCurrentQueueSize() > 0) {
                        interfaceLoadVO.setCurrentQueueSize(interfaceLoadVO.getCurrentQueueSize() - 1);
                    }
                } else if (p_event == LoadControllerI.REFUSED_FROM_QUEUE) {
                    if (interfaceLoadVO.getCurrentQueueSize() > 0) {
                        interfaceLoadVO.setCurrentQueueSize(interfaceLoadVO.getCurrentQueueSize() - 1);
                    }
                } else if (p_event == LoadControllerI.TIMEOUT_FROM_QUEUE) {
                    if (interfaceLoadVO.getCurrentQueueSize() > 0) {
                        interfaceLoadVO.setCurrentQueueSize(interfaceLoadVO.getCurrentQueueSize() - 1);
                    }
                }
            } else {
                if (p_event == LoadControllerI.DEC_SAME_SEC_RES_COUNT) {
                    if (interfaceLoadVO.getCurrentTransactionLoad() > 0) {
                        interfaceLoadVO.setCurrentTransactionLoad(interfaceLoadVO.getCurrentTransactionLoad() - 1);
                    }
                } else if (p_event == LoadControllerI.DEC_LAST_TRANS_COUNT) {
                    if (interfaceLoadVO.getCurrentTransactionLoad() > 0) {
                        interfaceLoadVO.setCurrentTransactionLoad(interfaceLoadVO.getCurrentTransactionLoad() - 1);
                    }
                    if (_log.isDebugEnabled()) {
        				loggerValue.setLength(0);
                    	loggerValue.append("Entered p_networkID:");
                    	loggerValue.append(p_networkID);
        				loggerValue.append(" p_interfaceID=");
                    	loggerValue.append(p_interfaceID);
                    	loggerValue.append("p_transactionID=");
                    	loggerValue.append(p_transactionID);
                    	loggerValue.append("interfaceLoadVO.getCurrentTransactionLoad()=");
                    	loggerValue.append(interfaceLoadVO.getCurrentTransactionLoad());
                        _log.debug("decreaseCurrentInterfaceLoad", loggerValue);
                    }
                } else if (p_event == LoadControllerI.REFUSED_FROM_QUEUE) {
                    if (interfaceLoadVO.getCurrentQueueSize() > 0) {
                        interfaceLoadVO.setCurrentQueueSize(interfaceLoadVO.getCurrentQueueSize() - 1);
                    }
                } else if (p_event == LoadControllerI.PROCESSED_FROM_QUEUE) {
                    if (interfaceLoadVO.getCurrentQueueSize() > 0) {
                        interfaceLoadVO.setCurrentQueueSize(interfaceLoadVO.getCurrentQueueSize() - 1);
                    }
                } else if (p_event == LoadControllerI.TIMEOUT_FROM_QUEUE) {
                    if (interfaceLoadVO.getCurrentQueueSize() > 0) {
                        interfaceLoadVO.setCurrentQueueSize(interfaceLoadVO.getCurrentQueueSize() - 1);
                    }
                }
            }
        }
    }

}
