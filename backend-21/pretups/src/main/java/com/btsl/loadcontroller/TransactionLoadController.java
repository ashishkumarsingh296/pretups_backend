package com.btsl.loadcontroller;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

/*
 * TransactionLoadController.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 14/07/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Class to process the transaction load request
 */

public class TransactionLoadController {

    /**
	 * 
	 */
	private Log _log = LogFactory.getLog(this.getClass().getName());
    private static final ReentrantLock lock = new ReentrantLock();
    /**
     * This method checks the service type load whether the request can be
     * processed or not.
     * Else check for overflow is allowed or not for the service
     * 
     * @param p_networkID
     * @param p_interfaceID
     * @param p_serviceType
     * @param p_originalService
     * @param p_transactionNo
     * @param p_checkAlternateService
     *            : Check for alternate service block,
     * @return boolean: True means Successful, False = Refused
     */
    public boolean checkTransactionLoad(String p_networkID, String p_interfaceID, String p_serviceType, String p_originalService, String p_transactionNo, boolean p_checkAlternateService, String p_userType) {
		StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
    	loggerValue.append("Entered with p_networkID=");
    	loggerValue.append(p_networkID);
		loggerValue.append(" p_interfaceID=");
    	loggerValue.append(p_interfaceID);
    	loggerValue.append(" p_serviceType=");
    	loggerValue.append(p_serviceType);
		loggerValue.append(" p_originalService=");
    	loggerValue.append(p_originalService);
    	loggerValue.append(" p_transactionNo=");
    	loggerValue.append(p_transactionNo);
		loggerValue.append(" p_checkAlternateService=");
    	loggerValue.append(p_checkAlternateService);
		loggerValue.append(" p_userType=");
    	loggerValue.append(p_userType);
        _log.info(" checkTransactionLoad",loggerValue);
        boolean retStatus = true;
        String instanceID = LoadControllerCache.getInstanceID();

        long currentTime = System.currentTimeMillis();
        Timestamp timeStampVal = new Timestamp(currentTime);

        boolean isSameSecRequest = false;
        TransactionLoadVO transactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(instanceID + "_" + p_networkID + "_" + p_interfaceID + "_" + p_serviceType);

        String loadType = LoadControllerCache.getLoadType();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Load Type to be used=");
        	loggerValue.append(loadType);
            _log.debug("checkTransactionLoad",loggerValue);
        }

        if (transactionLoadVO != null) {
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
                    isSameSecRequest = LoadControllerUtil.checkSameSecondRequest(transactionLoadVO.getPreviousSecond(), currentTime);
                    if (isSameSecRequest) {
                        if (transactionLoadVO.getNoOfRequestSameSec() < transactionLoadVO.getCurrentTPS()) {
                            if (_log.isDebugEnabled()) {
                            	loggerValue.setLength(0);
                            	loggerValue.append("Request in Same Second and is within the allowed limit=");
                            	loggerValue.append(transactionLoadVO.getCurrentTPS());
                        		loggerValue.append(" Current =");
                            	loggerValue.append(transactionLoadVO.getNoOfRequestSameSec());
                            	loggerValue.append(" , checking for Transaction Load");
                                _log.debug("checkTransactionLoad",loggerValue);
                            }
                            retStatus = checkCurrentInterServiceLoad(transactionLoadVO, instanceID, p_networkID, p_interfaceID, p_serviceType, p_originalService, currentTime, timeStampVal, p_transactionNo, p_checkAlternateService, p_userType);
                            if (retStatus) {
                                transactionLoadVO.setLastTxnProcessStartTime(timeStampVal);
                                transactionLoadVO.setNoOfRequestSameSec(transactionLoadVO.getNoOfRequestSameSec() + 1);
                            }
                        } else {
                            // Refuse the request
                            retStatus = false;
                            transactionLoadVO.setTotalRefusedCount(transactionLoadVO.getTotalRefusedCount() + 1);
                            transactionLoadVO.setLastRefusedTime(timeStampVal);
                            if (_log.isDebugEnabled()) {
                            	loggerValue.setLength(0);
                            	loggerValue.append("Request in Same Second and is not within the allowed limit=");
                            	loggerValue.append(transactionLoadVO.getCurrentTPS());
                        		loggerValue.append(" Current =");
                            	loggerValue.append(transactionLoadVO.getNoOfRequestSameSec());
                        		loggerValue.append(" , Refusing the request");
                                _log.debug("checkTransactionLoad",loggerValue);
                            }

                        }
                    } else {
                        if (_log.isDebugEnabled()) {
                            _log.debug("checkTransactionLoad", "Request is Not in Same Second , checking for Transaction Load");
                        }

                        retStatus = checkCurrentInterServiceLoad(transactionLoadVO, instanceID, p_networkID, p_interfaceID, p_serviceType, p_originalService, currentTime, timeStampVal, p_transactionNo, p_checkAlternateService, p_userType);
                        if (retStatus) {
                            transactionLoadVO.setPreviousSecond(currentTime);
                            transactionLoadVO.setNoOfRequestSameSec(1);
                            transactionLoadVO.setLastTxnProcessStartTime(timeStampVal);
                        }
                    }
                } else {
                    if (_log.isDebugEnabled()) {
                        _log.debug("checkTransactionLoad", "Not TPS based checking for Transaction Load");
                    }

                    retStatus = checkCurrentInterServiceLoad(transactionLoadVO, instanceID, p_networkID, p_interfaceID, p_serviceType, p_originalService, currentTime, timeStampVal, p_transactionNo, p_checkAlternateService, p_userType);
                }
                // Store the First Request Time
                if (transactionLoadVO.getFirstRequestCount() == 0) {
                    transactionLoadVO.setFirstRequestCount(1);
                    transactionLoadVO.setFirstRequestTime(currentTime);
                }
                // Increase the Recieved Count for the Service
                transactionLoadVO.setRecievedCount(transactionLoadVO.getRecievedCount() + 1);
                transactionLoadVO.setLastReceievedTime(timeStampVal);
            } finally {
                lock.unlock();
            }
        }
        loggerValue.setLength(0);
    	loggerValue.append("Exiting with transactionLoadVO=");
    	loggerValue.append(transactionLoadVO);
		loggerValue.append( "retStatus :");
    	loggerValue.append(retStatus);
        _log.info("checkTransactionLoad",loggerValue);
        return retStatus;
    }

    /**
     * This method checks for the alternate services available, only checks if
     * overflow is allowed
     * 
     * @param p_instanceID
     * @param p_networkID
     * @param p_interfaceID
     * @param p_originalService
     * @param p_transactionNo
     * @param p_transactionLoadVO
     * @return boolean
     */
    public boolean checkAlternateService(String p_instanceID, String p_networkID, String p_interfaceID, String p_originalService, String p_transactionNo, TransactionLoadVO p_transactionLoadVO, String p_userType) {
		StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
    	loggerValue.append("p_transactionNo=");
    	loggerValue.append(p_transactionNo);
		loggerValue.append(" p_interfaceID=");
    	loggerValue.append(p_interfaceID);
    	loggerValue.append(" p_originalService=");
    	loggerValue.append(p_originalService);
		loggerValue.append(" p_userType=");
    	loggerValue.append(p_userType);
        _log.info("checkAlternateService",loggerValue);
        boolean alterateService = false;
        if (p_transactionLoadVO.getAlternateServiceLoadType() != null && !p_transactionLoadVO.getAlternateServiceLoadType().isEmpty()) {
            if (_log.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append("Alternate Service Load type=");
            	loggerValue.append(p_transactionLoadVO.getAlternateServiceLoadType());
                _log.debug("checkAlternateService",loggerValue);
            }

            for (int i = 0; i < p_transactionLoadVO.getAlternateServiceLoadType().size(); i++) {
            	TransactionLoadVO alternateTransVO = (TransactionLoadVO) p_transactionLoadVO.getAlternateServiceLoadType().get(i);
        		loggerValue.setLength(0);
            	loggerValue.append("Performing checks for Alternate Service type=");
            	loggerValue.append(alternateTransVO.getServiceType());
        		loggerValue.append(" For p_transactionNo=");
            	loggerValue.append(p_transactionNo);
                _log.info("checkAlternateService",loggerValue);

                boolean requestOK = checkTransactionLoad(p_networkID, p_interfaceID, alternateTransVO.getServiceType(), p_originalService, p_transactionNo, false, p_userType);
                if (requestOK) {
                    alterateService = true;
                } else {
                    continue;
                }
            }
        }
		loggerValue.setLength(0);
    	loggerValue.append("Exiting for p_transactionNo=");
    	loggerValue.append(p_transactionNo);
		loggerValue.append(" alterateService=");
    	loggerValue.append(alterateService);
        _log.info("checkAlternateService",loggerValue);
        return alterateService;
    }

    /**
     * Method to decrease the counters, also call different method to decrease
     * the interface, network and instance load
     * 
     * @param p_event
     * @param p_transactionID
     */
    public void decreaseTransactionLoad(int p_event, String p_transactionID, String p_senderNetwork) {
		StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
    	loggerValue.append("Entered p_transactionID:");
    	loggerValue.append(p_transactionID);
		loggerValue.append(" p_event:");
    	loggerValue.append(p_event);
    	loggerValue.append("p_senderNetwork=");
    	loggerValue.append(p_senderNetwork);
        _log.info("decreaseTransactionLoad",loggerValue);
        final String METHOD_NAME = "decreaseTransactionLoad";
        try {
            HashMap allTransMap = LoadControllerCache.getAllTransactionLoadListMap();
            MiniTransVO miniTransVO = (MiniTransVO) allTransMap.get(p_transactionID);
            String interfaceService = miniTransVO.getSenderInterfaceService();
            boolean isSenderOverflow = miniTransVO.isOverflow();
            String originalService = miniTransVO.getSenderOriginalService();
            TransactionLoadVO transactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(interfaceService);
            long transactionStartTime = 0;
            if (transactionLoadVO != null) {
                transactionStartTime = ((Long) transactionLoadVO.getTransactionListMap().get(p_transactionID)).longValue();
            }
            long currentTime = System.currentTimeMillis();

            if (_log.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append("p_transactionID:");
            	loggerValue.append(p_transactionID);
        		loggerValue.append( " p_event:");
            	loggerValue.append(p_event);
            	loggerValue.append("transactionLoadVO=");
            	loggerValue.append(transactionLoadVO);
                _log.debug("decreaseTransactionLoad",loggerValue);
            }

            if (transactionLoadVO != null) {
                // ------------------------------------------------------------------------------------------------------
                lock.lock();
                try {
                    if (p_event == LoadControllerI.DEC_LAST_TRANS_COUNT) {
                        transactionLoadVO.getTransactionListMap().remove(p_transactionID);
                       
                        InterfaceLoadController interfaceLoadController = LoadControllerUtil.getInterfaceLoadObject(transactionLoadVO.getInstanceID(), transactionLoadVO.getNetworkCode(), transactionLoadVO.getInterfaceID());
                        NetworkLoadController networkLoadController = LoadControllerUtil.getNetworkLoadObject(transactionLoadVO.getInstanceID(), p_senderNetwork);
                        InstanceLoadController instanceLoadController = LoadControllerUtil.getInstanceLoadObject(transactionLoadVO.getInstanceID());

                        // Processing for Transaction count
                        /*
                         * if(LoadControllerI.LOAD_CONTROLLER_TXN_TYPE.
                         * equalsIgnoreCase(loadType))
                         * {
                         * if(transactionLoadVO.getCurrentTransactionLoad()>0)
                         * transactionLoadVO.setCurrentTransactionLoad(
                         * transactionLoadVO.getCurrentTransactionLoad()-1);
                         * 
                         * if(isSenderOverflow)
                         * {
                         * TransactionLoadVO
                         * origTransactionLoadVO=(TransactionLoadVO
                         * )LoadControllerCache
                         * .getTransactionLoadHash().get(originalService);
                         * if(origTransactionLoadVO.getOverFlowCount()>0)
                         * origTransactionLoadVO.setOverFlowCount(
                         * origTransactionLoadVO.getOverFlowCount()-1);
                         * if(_log.isDebugEnabled())_log.debug(
                         * "decreaseTransactionLoad"
                         * ,"p_transactionID="+p_transactionID
                         * +" isSenderOverflow ="
                         * +isSenderOverflow+" Decreasing Count to ="
                         * +origTransactionLoadVO.getOverFlowCount());
                         * }
                         * interfaceLoadController.
                         * decreaseCurrentInterfaceLoadUnlock
                         * (LoadControllerI.DEC_LAST_TRANS_COUNT
                         * ,transactionLoadVO
                         * .getNetworkCode(),transactionLoadVO.
                         * getInterfaceID(),p_transactionID);
                         * networkLoadController.decreaseCurrentNetworkLoadUnlock
                         * (
                         * p_senderNetwork,LoadControllerI.DEC_LAST_TRANS_COUNT)
                         * ;
                         * instanceLoadController.decreaseCurrentInstanceLoadUnlock
                         * (transactionLoadVO.getInstanceID(),LoadControllerI.
                         * DEC_LAST_TRANS_COUNT);
                         * }
                         * else //For TPS Control also
                         */{
                            if (transactionLoadVO.getCurrentTransactionLoad() > 0) {
                                transactionLoadVO.setCurrentTransactionLoad(transactionLoadVO.getCurrentTransactionLoad() - 1);
                            }
                            if (isSenderOverflow) {
                                TransactionLoadVO origTransactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(originalService);
                                if (origTransactionLoadVO.getOverFlowCount() > 0) {
                                    origTransactionLoadVO.setOverFlowCount(origTransactionLoadVO.getOverFlowCount() - 1);
                                }
                                if (_log.isDebugEnabled()) {
                            		loggerValue.setLength(0);
                                	loggerValue.append("p_transactionID=" );
                                	loggerValue.append(p_transactionID);
                            		loggerValue.append(" isSenderOverflow =");
                                	loggerValue.append(isSenderOverflow);
                                	loggerValue.append(" Decreasing Count to =");
                                	loggerValue.append(origTransactionLoadVO.getOverFlowCount());
                                    _log.debug("decreaseTransactionLoad",loggerValue);
                                }

                            }
                            interfaceLoadController.decreaseCurrentInterfaceLoadUnlock(LoadControllerI.DEC_LAST_TRANS_COUNT, transactionLoadVO.getNetworkCode(), transactionLoadVO.getInterfaceID(), p_transactionID);
                            networkLoadController.decreaseCurrentNetworkLoadUnlock(p_senderNetwork, LoadControllerI.DEC_LAST_TRANS_COUNT);
                            instanceLoadController.decreaseCurrentInstanceLoadUnlock(transactionLoadVO.getInstanceID(), LoadControllerI.DEC_LAST_TRANS_COUNT);

                            /*
                             * boolean isSameSecResponse=LoadControllerUtil.
                             * checkSameSecondRequest
                             * (transactionStartTime,currentTime);
                             * if(isSameSecResponse)
                             * {
                             * if(transactionLoadVO.getNoOfRequestSameSec()>0)
                             * transactionLoadVO.setNoOfRequestSameSec(
                             * transactionLoadVO.getNoOfRequestSameSec()-1);
                             * //Store the avg time also if possible
                             * //Calculate here
                             * //Also decrease from other counters as well like
                             * interface , network and instance
                             * decreaseCurrentInterfaceLoad(LoadControllerI.
                             * DEC_SAME_SEC_RES_COUNT
                             * ,transactionLoadVO.getNetworkCode
                             * (),transactionLoadVO
                             * .getInterfaceID(),p_transactionID);
                             * decreaseCurrentNetworkLoad(LoadControllerI.
                             * DEC_SAME_SEC_RES_COUNT
                             * ,transactionLoadVO.getNetworkCode());
                             * decreaseCurrentInstanceLoad(LoadControllerI.
                             * DEC_SAME_SEC_RES_COUNT);
                             * }
                             */
                        }
                        // Set average time for the instance+network+service
                        transactionLoadVO.setAverageServiceTime((transactionLoadVO.getAverageServiceTime() + (currentTime - transactionStartTime)) / 2);

                        String recieverService = miniTransVO.getReciverInterfaceService();
                        if (recieverService != null && !BTSLUtil.isNullString(recieverService)) {

                            if (_log.isDebugEnabled()) {
                        		loggerValue.setLength(0);
                            	loggerValue.append(" Transaction ID=" );
                            	loggerValue.append(p_transactionID);
                        		loggerValue.append(" recieverService=");
                            	loggerValue.append(recieverService);
                                _log.debug("decreaseTransactionLoad",loggerValue);
                            }

                            boolean isRecieverOverflow = miniTransVO.isRecieverOverflow();
                            String originalRecieverService = miniTransVO.getReciverOriginalService();

                            TransactionLoadVO receiverTransVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(recieverService);
                            interfaceLoadController = LoadControllerUtil.getInterfaceLoadObject(receiverTransVO.getInstanceID(), receiverTransVO.getNetworkCode(), receiverTransVO.getInterfaceID());
                            if (_log.isDebugEnabled()) {
                        		loggerValue.setLength(0);
                            	loggerValue.append(" Transaction ID=");
                            	loggerValue.append(p_transactionID);
                        		loggerValue.append(" isRecieverOverflow=");
                            	loggerValue.append(isRecieverOverflow);
                            	loggerValue.append( " originalRecieverService=" );
                            	loggerValue.append(originalRecieverService);
                        		loggerValue.append(" interfaceLoadController=");
                            	loggerValue.append(interfaceLoadController);
                            	loggerValue.append(" receiverTransVO.getCurrentTransactionLoad()=");
                            	loggerValue.append(receiverTransVO.getCurrentTransactionLoad());
                                _log.debug("decreaseTransactionLoad",loggerValue);
                            }

                            if (receiverTransVO.getCurrentTransactionLoad() > 0) {
                                receiverTransVO.setCurrentTransactionLoad(receiverTransVO.getCurrentTransactionLoad() - 1);
                            }
                            interfaceLoadController.decreaseCurrentInterfaceLoad(LoadControllerI.DEC_LAST_TRANS_COUNT, receiverTransVO.getNetworkCode(), receiverTransVO.getInterfaceID(), p_transactionID);

                            if (isRecieverOverflow) {
                                TransactionLoadVO origReceiverTransactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(originalRecieverService);
                                if (origReceiverTransactionLoadVO.getOverFlowCount() > 0) {
                                    origReceiverTransactionLoadVO.setOverFlowCount(origReceiverTransactionLoadVO.getOverFlowCount() - 1);
                                }
                                if (_log.isDebugEnabled()) {
                            		loggerValue.setLength(0);
                                	loggerValue.append("p_transactionID=");
                                	loggerValue.append(p_transactionID);
                            		loggerValue.append(" isRecieverOverflow =");
                                	loggerValue.append(isRecieverOverflow);
                                	loggerValue.append(" Decreasing Count to =");
                                	loggerValue.append(origReceiverTransactionLoadVO.getOverFlowCount());
                                    _log.debug("decreaseTransactionLoad",loggerValue);
                                }
                            }

                            // Set average time for the instance+network+service
                            receiverTransVO.setAverageServiceTime((transactionLoadVO.getAverageServiceTime() + (currentTime - transactionStartTime)) / 2);
                        }
                        allTransMap.remove(p_transactionID);
                    } else if (p_event == LoadControllerI.SENDER_VAL_SUCCESS) {
                        if (transactionLoadVO.getCurrentSenderValidationCount() > 0) {
                            transactionLoadVO.setCurrentSenderValidationCount(transactionLoadVO.getCurrentSenderValidationCount() - 1);
                        }
                        if (_log.isDebugEnabled()) {
                    		loggerValue.setLength(0);
                        	loggerValue.append("p_transactionID:");
                        	loggerValue.append(p_transactionID);
                    		loggerValue.append(" p_event:");
                        	loggerValue.append(p_event);
                        	loggerValue.append("transactionLoadVO.getCurrentSenderValidationCount()");
                        	loggerValue.append(transactionLoadVO.getCurrentSenderValidationCount());
                            _log.debug("decreaseTransactionLoad",loggerValue);
                        }

                    } else if (p_event == LoadControllerI.SENDER_VAL_FAILED) {
                        if (transactionLoadVO.getCurrentSenderValidationCount() > 0) {
                            transactionLoadVO.setCurrentSenderValidationCount(transactionLoadVO.getCurrentSenderValidationCount() - 1);
                        }
                        transactionLoadVO.setTotalSenderValFailCount(transactionLoadVO.getTotalSenderValFailCount() + 1);
                        if (_log.isDebugEnabled()) {
                    		loggerValue.setLength(0);
                        	loggerValue.append("p_transactionID:");
                        	loggerValue.append(p_transactionID);
                    		loggerValue.append(" p_event:");
                        	loggerValue.append(p_event);
                        	loggerValue.append("transactionLoadVO.getCurrentSenderValidationCount()");
                        	loggerValue.append(transactionLoadVO.getCurrentSenderValidationCount());
                            _log.debug("decreaseTransactionLoad", loggerValue);
                        }
                    } else if (p_event == LoadControllerI.SENDER_TOP_SUCCESS) {
                        if (transactionLoadVO.getCurrentSenderTopupCount() > 0) {
                            transactionLoadVO.setCurrentSenderTopupCount(transactionLoadVO.getCurrentSenderTopupCount() - 1);
                        }
                        if (_log.isDebugEnabled()) {
                    		loggerValue.setLength(0);
                        	loggerValue.append("p_transactionID:");
                        	loggerValue.append(p_transactionID);
                    		loggerValue.append( " p_event:");
                        	loggerValue.append(p_event);
                        	loggerValue.append("transactionLoadVO.getCurrentSenderTopupCount()");
                        	loggerValue.append(transactionLoadVO.getCurrentSenderTopupCount());
                            _log.debug("decreaseTransactionLoad",loggerValue);
                        }
                    } else if (p_event == LoadControllerI.SENDER_TOP_FAILED) {
                        if (transactionLoadVO.getCurrentSenderTopupCount() > 0) {
                            transactionLoadVO.setCurrentSenderTopupCount(transactionLoadVO.getCurrentSenderTopupCount() - 1);
                        }
                        transactionLoadVO.setTotalSenderTopupFailCount(transactionLoadVO.getTotalSenderTopupFailCount() + 1);
                        if (_log.isDebugEnabled()) {
                    		loggerValue.setLength(0);
                        	loggerValue.append("p_transactionID:");
                        	loggerValue.append(p_transactionID);
                    		loggerValue.append(" p_event:" );
                        	loggerValue.append(p_event);
                        	loggerValue.append("transactionLoadVO.getCurrentSenderTopupCount()");
                        	loggerValue.append(transactionLoadVO.getCurrentSenderTopupCount());
                            _log.debug("decreaseTransactionLoad",loggerValue);
                        }
                    } else if (p_event == LoadControllerI.RECEIVER_VAL_SUCCESS) {
                        interfaceService = miniTransVO.getReciverInterfaceService();
                        if (_log.isDebugEnabled()) {
                    		loggerValue.setLength(0);
                        	loggerValue.append("p_transactionID:");
                        	loggerValue.append(p_transactionID );
                    		loggerValue.append(" RECEIVER_VAL_SUCCESS interfaceService=");
                        	loggerValue.append(interfaceService);
                            _log.debug("decreaseTransactionLoad",loggerValue);
                        }
                        transactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(interfaceService);

                        if (transactionLoadVO.getCurrentRecieverValidationCount() > 0) {
                            transactionLoadVO.setCurrentRecieverValidationCount(transactionLoadVO.getCurrentRecieverValidationCount() - 1);
                        }
                        if (_log.isDebugEnabled()) {
                    		loggerValue.setLength(0);
                        	loggerValue.append("p_transactionID:");
                        	loggerValue.append(p_transactionID);
                    		loggerValue.append(" p_event:");
                        	loggerValue.append(p_event);
                        	loggerValue.append("transactionLoadVO.getCurrentRecieverValidationCount()");
                        	loggerValue.append(transactionLoadVO.getCurrentRecieverValidationCount());
                            _log.debug("decreaseTransactionLoad",loggerValue);
                        }
                    } else if (p_event == LoadControllerI.RECEIVER_VAL_FAILED) {
                        interfaceService = miniTransVO.getReciverInterfaceService();
                        if (_log.isDebugEnabled()) {
                    		loggerValue.setLength(0);
                        	loggerValue.append("p_transactionID:");
                        	loggerValue.append(p_transactionID);
                    		loggerValue.append(" RECEIVER_VAL_FAILED interfaceService=");
                        	loggerValue.append(interfaceService);
                            _log.debug("decreaseTransactionLoad",loggerValue);
                        }
                        transactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(interfaceService);

                        if (transactionLoadVO.getCurrentRecieverValidationCount() > 0) {
                            transactionLoadVO.setCurrentRecieverValidationCount(transactionLoadVO.getCurrentRecieverValidationCount() - 1);
                        }
                        transactionLoadVO.setTotalRecieverValFailCount(transactionLoadVO.getTotalRecieverValFailCount() + 1);
                        if (_log.isDebugEnabled()) {
                    		loggerValue.setLength(0);
                        	loggerValue.append("p_transactionID:");
                        	loggerValue.append(p_transactionID);
                    		loggerValue.append(" p_event:");
                        	loggerValue.append(p_event);
                        	loggerValue.append("transactionLoadVO.getCurrentRecieverValidationCount()");
                        	loggerValue.append(transactionLoadVO.getCurrentRecieverValidationCount());
                            _log.debug("decreaseTransactionLoad",loggerValue);
                        }
                    } else if (p_event == LoadControllerI.RECEIVER_TOP_SUCCESS) {
                        interfaceService = miniTransVO.getReciverInterfaceService();
                        if (_log.isDebugEnabled()) {
                    		loggerValue.setLength(0);
                        	loggerValue.append("p_transactionID:");
                        	loggerValue.append(p_transactionID);
                    		loggerValue.append(" RECEIVER_TOP_SUCCESS interfaceService=");
                        	loggerValue.append(interfaceService);
                            _log.debug("decreaseTransactionLoad", loggerValue);
                        }
                        transactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(interfaceService);

                        if (transactionLoadVO.getCurrentRecieverTopupCount() > 0) {
                            transactionLoadVO.setCurrentRecieverTopupCount(transactionLoadVO.getCurrentRecieverTopupCount() - 1);
                        }
                        if (_log.isDebugEnabled()) {
                    		loggerValue.setLength(0);
                        	loggerValue.append("p_transactionID:");
                        	loggerValue.append(p_transactionID);
                    		loggerValue.append(" p_event:");
                        	loggerValue.append(p_event);
                        	loggerValue.append("transactionLoadVO.getCurrentRecieverTopupCount()");
                        	loggerValue.append(transactionLoadVO.getCurrentRecieverTopupCount());
                            _log.debug("decreaseTransactionLoad",loggerValue);
                        }

                    } else if (p_event == LoadControllerI.RECEIVER_TOP_FAILED) {
                        interfaceService = miniTransVO.getReciverInterfaceService();
                        if (_log.isDebugEnabled()) {
                    		loggerValue.setLength(0);
                        	loggerValue.append("p_transactionID:");
                        	loggerValue.append(p_transactionID);
                    		loggerValue.append(" RECEIVER_TOP_FAILED interfaceService=" );
                        	loggerValue.append(interfaceService);
                            _log.debug("decreaseTransactionLoad",loggerValue);
                        }
                        transactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(interfaceService);

                        if (transactionLoadVO.getCurrentRecieverTopupCount() > 0) {
                            transactionLoadVO.setCurrentRecieverTopupCount(transactionLoadVO.getCurrentRecieverTopupCount() - 1);
                        }
                        transactionLoadVO.setTotalRecieverTopupFailCount(transactionLoadVO.getTotalRecieverTopupFailCount() + 1);
                        if (_log.isDebugEnabled()) {
                    		loggerValue.setLength(0);
                        	loggerValue.append("p_transactionID:");
                        	loggerValue.append(p_transactionID);
                    		loggerValue.append(" p_event:" );
                        	loggerValue.append(p_event);
                        	loggerValue.append("transactionLoadVO.getCurrentRecieverTopupCount()");
                        	loggerValue.append(transactionLoadVO.getCurrentRecieverTopupCount());
                            _log.debug("decreaseTransactionLoad",loggerValue);
                        }
                    }
                    
                } finally {
                    lock.unlock();
                }
            }
    		loggerValue.setLength(0);
        	loggerValue.append("p_transactionID=" );
        	loggerValue.append(p_transactionID);
    		loggerValue.append(" transactionLoadVO=");
        	loggerValue.append(transactionLoadVO);
            _log.info("decreaseTransactionLoad",loggerValue);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
    		loggerValue.setLength(0);
        	loggerValue.append("Getting exception =");
        	loggerValue.append( e.getMessage());
            _log.error("decreaseTransactionLoad",loggerValue);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "TransactionLoadController[decreaseTransactionLoad]", "", "", "", "Exception:" + e.getMessage());
        }
    }

    /**
     * This method will increase the various counters at the intermediary level
     * at the service type level
     * 
     * @param p_event
     * @param p_transactionID
     * @return boolean
     */
    public boolean incrementTransactionInterCounts(String p_transactionID, int p_event) {
		StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.append(" Entered:: p_event=");
        	loggerValue.append(p_event);
    		loggerValue.append(" p_transactionID=");
        	loggerValue.append(p_transactionID);
            _log.debug(" incrementTransactionInterCounts() ",loggerValue);
        }
        final String METHOD_NAME = "incrementTransactionInterCounts";
        boolean flag = false;

        String instanceID = LoadControllerCache.getInstanceID();
        if (BTSLUtil.isNullString(instanceID)) {
            instanceID = LoadControllerI.DEFAULT_INSTANCE_ID;
        }
        try {

            HashMap allTransMap = LoadControllerCache.getAllTransactionLoadListMap();
            MiniTransVO miniTransVO = (MiniTransVO) allTransMap.get(p_transactionID);
            String interfaceService = miniTransVO.getSenderInterfaceService();
            TransactionLoadVO transactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(interfaceService);
            if (_log.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append(" p_event=");
            	loggerValue.append(p_event);
        		loggerValue.append(" p_transactionID=");
            	loggerValue.append(p_transactionID);
        		loggerValue.append(" transactionLoadVO=");
            	loggerValue.append(transactionLoadVO);
                _log.debug(" incrementTransactionInterCounts() ", loggerValue);
            }

            if (transactionLoadVO != null) {
                // ------------------------------------------------------------------------------------------------------
                lock.lock();
                try {
                    if (p_event == LoadControllerI.SENDER_UNDER_VAL) {
                        flag = true;
                        transactionLoadVO.setCurrentSenderValidationCount(transactionLoadVO.getCurrentSenderValidationCount() + 1);
                        transactionLoadVO.setTotalSenderValidationCount(transactionLoadVO.getTotalSenderValidationCount() + 1);
                    } else if (p_event == LoadControllerI.RECEIVER_UNDER_VAL) {
                        interfaceService = miniTransVO.getReciverInterfaceService();
                        if (_log.isDebugEnabled()) {
                    		loggerValue.setLength(0);
                        	loggerValue.append("p_transactionID:");
                        	loggerValue.append(p_transactionID);
                    		loggerValue.append(" RECEIVER_UNDER_VAL interfaceService=");
                        	loggerValue.append(interfaceService);
                            _log.debug("incrementTransactionInterCounts",loggerValue);
                        }
                        transactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(interfaceService);

                        flag = true;
                        transactionLoadVO.setCurrentRecieverValidationCount(transactionLoadVO.getCurrentRecieverValidationCount() + 1);
                        transactionLoadVO.setTotalRecieverValidationCount(transactionLoadVO.getTotalRecieverValidationCount() + 1);
                    } else if (p_event == LoadControllerI.SENDER_UNDER_TOP) {
                        flag = true;
                        transactionLoadVO.setCurrentSenderTopupCount(transactionLoadVO.getCurrentSenderTopupCount() + 1);
                        transactionLoadVO.setTotalSenderTopupCount(transactionLoadVO.getTotalSenderTopupCount() + 1);
                    } else if (p_event == LoadControllerI.RECEIVER_UNDER_TOP) {
                        interfaceService = miniTransVO.getReciverInterfaceService();
                        if (_log.isDebugEnabled()) {
                    		loggerValue.setLength(0);
                        	loggerValue.append("p_transactionID:");
                        	loggerValue.append(p_transactionID);
                    		loggerValue.append(" RECEIVER_UNDER_TOP interfaceService=");
                        	loggerValue.append(interfaceService);
                            _log.debug("incrementTransactionInterCounts",loggerValue);
                        }
                        transactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(interfaceService);

                        flag = true;
                        transactionLoadVO.setCurrentRecieverTopupCount(transactionLoadVO.getCurrentRecieverTopupCount() + 1);
                        transactionLoadVO.setTotalRecieverTopupCount(transactionLoadVO.getTotalRecieverTopupCount() + 1);
                    } else if (p_event == LoadControllerI.INTERNAL_FAIL_COUNT) {
                        flag = true;
                        transactionLoadVO.setTotalInternalFailCount(transactionLoadVO.getTotalInternalFailCount() + 1);
                    }
                } finally {
                    lock.unlock();
                }
            }
            if (_log.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append(" p_event=");
            	loggerValue.append(p_event);
        		loggerValue.append(" p_transactionID=");
            	loggerValue.append(p_transactionID);
        		loggerValue.append(" transactionLoadVO=");
            	loggerValue.append(transactionLoadVO);
            	loggerValue.append(" flag=");
            	loggerValue.append(flag);
                _log.debug(" incrementTransactionInterCounts() ",loggerValue);
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
    		loggerValue.setLength(0);
        	loggerValue.append("Getting exception =");
        	loggerValue.append(e.getMessage());
            _log.error("incrementTransactionInterCounts",loggerValue);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "TransactionLoadController[incrementTransactionInterCounts]", "", "", "", "Exception:" + e.getMessage());
        }
        return flag;
    }

    /**
     * Method to check the current number of request present in an service type
     * level with the available load
     * Also checks the overflow
     * 
     * @param p_transactionLoadVO
     * @param p_instanceID
     * @param p_networkID
     * @param p_interfaceID
     * @param p_serviceType
     * @param p_currentTime
     * @param p_timeStampVal
     * @param p_transactionNo
     * @param p_checkAlternateService
     * @return boolean
     */
    public boolean checkCurrentInterServiceLoad(TransactionLoadVO p_transactionLoadVO, String p_instanceID, String p_networkID, String p_interfaceID, String p_serviceType, String p_originalService, long p_currentTime, Timestamp p_timeStampVal, String p_transactionNo, boolean p_checkAlternateService, String p_userType) {
		StringBuilder loggerValue= new StringBuilder(); 
    	loggerValue.append(" Entered with p_transactionLoadVO=");
    	loggerValue.append(p_transactionLoadVO);
		loggerValue.append( " p_instanceID=");
    	loggerValue.append(p_instanceID);
    	loggerValue.append(" p_networkID=");
    	loggerValue.append(p_networkID);
		loggerValue.append(" p_interfaceID=" );
    	loggerValue.append(p_interfaceID);
    	loggerValue.append(" p_serviceType=");
    	loggerValue.append(p_serviceType);
    	loggerValue.append(" p_originalService=");
    	loggerValue.append(p_originalService);
    	loggerValue.append(" p_transactionNo=");
    	loggerValue.append(p_transactionNo);
    	loggerValue.append(" p_checkAlternateService=");
    	loggerValue.append(p_checkAlternateService);
    	loggerValue.append(" p_userType=");
    	loggerValue.append(p_userType);
        _log.info("checkCurrentInterServiceLoad",loggerValue);
        boolean retStatus = false;
        boolean isServiceChanged = false;
        if (!p_serviceType.equalsIgnoreCase(p_originalService)) {
            isServiceChanged = true;
        }
        if (_log.isDebugEnabled()) {
    		loggerValue.setLength(0);
        	loggerValue.append("Current Load =");
        	loggerValue.append(p_transactionLoadVO.getCurrentTransactionLoad());
    		loggerValue.append(" Allowed=");
        	loggerValue.append(p_transactionLoadVO.getTransactionLoad());
            _log.debug("checkCurrentInterServiceLoad",loggerValue);
        }

        if (p_transactionLoadVO.getCurrentTransactionLoad() < p_transactionLoadVO.getTransactionLoad()) {
            if (_log.isDebugEnabled()) {
                _log.debug("checkCurrentInterServiceLoad", "Allowing Request to go through");
            }

            // Increase the counters
            retStatus = true;
            // Add in HashMap
            // Separate Thread will be used for timeout of Transactions which
            // will be get from distinct combination of
            // Intansce+network_Interface+service
            // If tiemout is there then decrease of counters will happen from
            // bottom to top

            if (p_userType.equalsIgnoreCase(LoadControllerI.USERTYPE_SENDER)) {
                HashMap hash = p_transactionLoadVO.getTransactionListMap();
                hash.put(p_transactionNo, Long.valueOf(p_currentTime));
                p_transactionLoadVO.setTransactionListMap(hash);

                HashMap mainHash = LoadControllerCache.getAllTransactionLoadListMap();
                mainHash.put(p_transactionNo, new MiniTransVO(p_instanceID + "_" + p_networkID + "_" + p_interfaceID + "_" + p_serviceType, p_currentTime, isServiceChanged, p_instanceID + "_" + p_networkID + "_" + p_interfaceID + "_" + p_originalService));

                if (_log.isDebugEnabled()) {
            		loggerValue.setLength(0);
                	loggerValue.append("Setting Sender in Main Hash Table for all Transactions For Transaction ID=");
                	loggerValue.append(p_transactionNo);
                    _log.debug("checkCurrentInterServiceLoad",loggerValue);
                }

                LoadControllerCache.setAllTransactionLoadListMap(mainHash);
                p_transactionLoadVO.setCurrentTransactionLoad(p_transactionLoadVO.getCurrentTransactionLoad() + 1);
                p_transactionLoadVO.setRequestCount(p_transactionLoadVO.getRequestCount() + 1);
                p_transactionLoadVO.setLastTxnProcessStartTime(p_timeStampVal);
            } else {
                p_transactionLoadVO.setCurrentTransactionLoad(p_transactionLoadVO.getCurrentTransactionLoad() + 1);
                p_transactionLoadVO.setRequestCount(p_transactionLoadVO.getRequestCount() + 1);
                p_transactionLoadVO.setLastTxnProcessStartTime(p_timeStampVal);

                HashMap mainHash = LoadControllerCache.getAllTransactionLoadListMap();
                MiniTransVO miniTransVO = (MiniTransVO) mainHash.get(p_transactionNo);
                miniTransVO.setRecieverOverflow(isServiceChanged);
                miniTransVO.setReciverOriginalService(p_instanceID + "_" + p_networkID + "_" + p_interfaceID + "_" + p_originalService);
                miniTransVO.setReciverInterfaceService(p_instanceID + "_" + p_networkID + "_" + p_interfaceID + "_" + p_serviceType);
                mainHash.remove(p_transactionNo);
                if (_log.isDebugEnabled()) {
            		loggerValue.setLength(0);
                	loggerValue.append("Setting Receiver VO as " );
                	loggerValue.append(miniTransVO);
            		loggerValue.append(" For Transaction ID=" );
                	loggerValue.append(p_transactionNo);
                    _log.debug("checkCurrentInterServiceLoad",loggerValue);
                }

                mainHash.put(p_transactionNo, miniTransVO);
            }

        } else if (p_checkAlternateService) {
            if (_log.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append("Checking for Alternate Service Load Check Current Overflow Count=");
            	loggerValue.append(p_transactionLoadVO.getOverFlowCount());
        		loggerValue.append(" defined Overflow=");
            	loggerValue.append(p_transactionLoadVO.getDefinedOverFlowCount());
                _log.debug("checkCurrentInterServiceLoad",loggerValue);
            }

            if (p_transactionLoadVO.getOverFlowCount() < p_transactionLoadVO.getDefinedOverFlowCount()) { // Check
                                                                                                          // the
                                                                                                          // Alternate
                                                                                                          // Service
                                                                                                          // Type
                                                                                                          // Concept
                                                                                                          // (Overflow)
                boolean alternateService = checkAlternateService(p_instanceID, p_networkID, p_interfaceID, p_originalService, p_transactionNo, p_transactionLoadVO, p_userType);
                if (alternateService) {
                    p_transactionLoadVO.setOverFlowCount(p_transactionLoadVO.getOverFlowCount() + 1);
                    retStatus = true;
                } else {
                    retStatus = false;
                    p_transactionLoadVO.setTotalRefusedCount(p_transactionLoadVO.getTotalRefusedCount() + 1);
                    p_transactionLoadVO.setLastRefusedTime(p_timeStampVal);
                }
            } else {
                retStatus = false;
                p_transactionLoadVO.setTotalRefusedCount(p_transactionLoadVO.getTotalRefusedCount() + 1);
                p_transactionLoadVO.setLastRefusedTime(p_timeStampVal);
            }
        } else // Refuse the request
        {
            retStatus = false;
            p_transactionLoadVO.setTotalRefusedCount(p_transactionLoadVO.getTotalRefusedCount() + 1);
            p_transactionLoadVO.setLastRefusedTime(p_timeStampVal);
        }
		loggerValue.setLength(0);
    	loggerValue.append(" Exiting with status=");
    	loggerValue.append(retStatus);
        _log.info("checkCurrentInterServiceLoad",loggerValue);
        return retStatus;
    }

    public void decreaseTransactionLoad(int p_event, String p_transactionID) {
    	StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
    	loggerValue.append("Entered p_transactionID:");
    	loggerValue.append(p_transactionID);
		loggerValue.append( " p_event:");
    	loggerValue.append(p_event);
        _log.info("decreaseTransactionLoad",loggerValue);
        final String METHOD_NAME = "decreaseTransactionLoad";
        try {
            HashMap allTransMap = LoadControllerCache.getAllTransactionLoadListMap();
            MiniTransVO miniTransVO = (MiniTransVO) allTransMap.get(p_transactionID);
            String interfaceService = miniTransVO.getSenderInterfaceService();
            boolean isSenderOverflow = miniTransVO.isOverflow();
            String originalService = miniTransVO.getSenderOriginalService();
            TransactionLoadVO transactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(interfaceService);
            long transactionStartTime = 0;
            if (transactionLoadVO != null) {
                transactionStartTime = ((Long) transactionLoadVO.getTransactionListMap().get(p_transactionID)).longValue();
            }
            long currentTime = System.currentTimeMillis();

            if (_log.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append("p_transactionID:");
            	loggerValue.append(p_transactionID);
        		loggerValue.append(" p_event:");
            	loggerValue.append(p_event);
            	loggerValue.append("transactionLoadVO=");
            	loggerValue.append(transactionLoadVO);
                _log.debug("decreaseTransactionLoad",loggerValue);
            }

            if (transactionLoadVO != null) {
                // ------------------------------------------------------------------------------------------------------
                lock.lock();
                try {
                    if (p_event == LoadControllerI.DEC_LAST_TRANS_COUNT) {
                        transactionLoadVO.getTransactionListMap().remove(p_transactionID);

                        InterfaceLoadController interfaceLoadController = LoadControllerUtil.getInterfaceLoadObject(transactionLoadVO.getInstanceID(), transactionLoadVO.getNetworkCode(), transactionLoadVO.getInterfaceID());
                        NetworkLoadController networkLoadController = LoadControllerUtil.getNetworkLoadObject(transactionLoadVO.getInstanceID(), transactionLoadVO.getNetworkCode());
                        InstanceLoadController instanceLoadController = LoadControllerUtil.getInstanceLoadObject(transactionLoadVO.getInstanceID());

                        // Processing for Transaction count
                        {
                            if (transactionLoadVO.getCurrentTransactionLoad() > 0) {
                                transactionLoadVO.setCurrentTransactionLoad(transactionLoadVO.getCurrentTransactionLoad() - 1);
                            }
                            if (isSenderOverflow) {
                                TransactionLoadVO origTransactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(originalService);
                                if (origTransactionLoadVO.getOverFlowCount() > 0) {
                                    origTransactionLoadVO.setOverFlowCount(origTransactionLoadVO.getOverFlowCount() - 1);
                                }
                                if (_log.isDebugEnabled()) {
                            		loggerValue.setLength(0);
                                	loggerValue.append( "p_transactionID=");
                                	loggerValue.append(p_transactionID);
                            		loggerValue.append(" isSenderOverflow =");
                                	loggerValue.append(isSenderOverflow);
                                	loggerValue.append(" Decreasing Count to =");
                                	loggerValue.append(origTransactionLoadVO.getOverFlowCount());
                                    _log.debug("decreaseTransactionLoad",loggerValue);
                                }

                            }
                            interfaceLoadController.decreaseCurrentInterfaceLoad(LoadControllerI.DEC_LAST_TRANS_COUNT, transactionLoadVO.getNetworkCode(), transactionLoadVO.getInterfaceID(), p_transactionID);
                            networkLoadController.decreaseCurrentNetworkLoad(transactionLoadVO.getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);
                            instanceLoadController.decreaseCurrentInstanceLoad(transactionLoadVO.getInstanceID(), LoadControllerI.DEC_LAST_TRANS_COUNT);

                        }
                        // Set average time for the instance+network+service
                        transactionLoadVO.setAverageServiceTime((transactionLoadVO.getAverageServiceTime() + (currentTime - transactionStartTime)) / 2);

                        String recieverService = miniTransVO.getReciverInterfaceService();
                        if (recieverService != null && !BTSLUtil.isNullString(recieverService)) {

                            if (_log.isDebugEnabled()) {
                        		loggerValue.setLength(0);
                            	loggerValue.append(" Transaction ID=");
                            	loggerValue.append(p_transactionID);
                        		loggerValue.append(" recieverService=" );
                            	loggerValue.append(recieverService);
                                _log.debug("decreaseTransactionLoad",loggerValue);
                            }

                            boolean isRecieverOverflow = miniTransVO.isRecieverOverflow();
                            String originalRecieverService = miniTransVO.getReciverOriginalService();

                            TransactionLoadVO receiverTransVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(recieverService);
                            interfaceLoadController = LoadControllerUtil.getInterfaceLoadObject(receiverTransVO.getInstanceID(), receiverTransVO.getNetworkCode(), receiverTransVO.getInterfaceID());
                            if (_log.isDebugEnabled()) {
                        		loggerValue.setLength(0);
                            	loggerValue.append(" Transaction ID=" );
                            	loggerValue.append(p_transactionID);
                        		loggerValue.append(" isRecieverOverflow=");
                            	loggerValue.append(isRecieverOverflow);
                            	loggerValue.append(" originalRecieverService=");
                            	loggerValue.append(originalRecieverService);
                            	loggerValue.append(" interfaceLoadController=");
                            	loggerValue.append(interfaceLoadController);
                            	loggerValue.append(" receiverTransVO.getCurrentTransactionLoad()=");
                            	loggerValue.append(receiverTransVO.getCurrentTransactionLoad());
                                _log.debug("decreaseTransactionLoad",loggerValue);
                            }

                            if (receiverTransVO.getCurrentTransactionLoad() > 0) {
                                receiverTransVO.setCurrentTransactionLoad(receiverTransVO.getCurrentTransactionLoad() - 1);
                            }
                            interfaceLoadController.decreaseCurrentInterfaceLoad(LoadControllerI.DEC_LAST_TRANS_COUNT, receiverTransVO.getNetworkCode(), receiverTransVO.getInterfaceID(), p_transactionID);

                            if (isRecieverOverflow) {
                                TransactionLoadVO origReceiverTransactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(originalRecieverService);
                                if (origReceiverTransactionLoadVO.getOverFlowCount() > 0) {
                                    origReceiverTransactionLoadVO.setOverFlowCount(origReceiverTransactionLoadVO.getOverFlowCount() - 1);
                                }
                                if (_log.isDebugEnabled()) {
                            		loggerValue.setLength(0);
                                	loggerValue.append("p_transactionID=");
                                	loggerValue.append(p_transactionID);
                            		loggerValue.append(" isRecieverOverflow =" );
                                	loggerValue.append(isRecieverOverflow);
                                	loggerValue.append(" Decreasing Count to =");
                                	loggerValue.append(origReceiverTransactionLoadVO.getOverFlowCount());
                                    _log.debug("decreaseTransactionLoad",loggerValue);
                                }
                            }

                            // Set average time for the instance+network+service
                            receiverTransVO.setAverageServiceTime((transactionLoadVO.getAverageServiceTime() + (currentTime - transactionStartTime)) / 2);
                        }
                        allTransMap.remove(p_transactionID);
                    } else if (p_event == LoadControllerI.SENDER_VAL_SUCCESS) {
                        if (transactionLoadVO.getCurrentSenderValidationCount() > 0) {
                            transactionLoadVO.setCurrentSenderValidationCount(transactionLoadVO.getCurrentSenderValidationCount() - 1);
                        }
                        if (_log.isDebugEnabled()) {
                    		loggerValue.setLength(0);
                        	loggerValue.append("p_transactionID:");
                        	loggerValue.append(p_transactionID);
                    		loggerValue.append(" p_event:");
                        	loggerValue.append(p_event);
                        	loggerValue.append("transactionLoadVO.getCurrentSenderValidationCount()");
                        	loggerValue.append(transactionLoadVO.getCurrentSenderValidationCount());
                            _log.debug("decreaseTransactionLoad",loggerValue);
                        }

                    } else if (p_event == LoadControllerI.SENDER_VAL_FAILED) {
                        if (transactionLoadVO.getCurrentSenderValidationCount() > 0) {
                            transactionLoadVO.setCurrentSenderValidationCount(transactionLoadVO.getCurrentSenderValidationCount() - 1);
                        }
                        transactionLoadVO.setTotalSenderValFailCount(transactionLoadVO.getTotalSenderValFailCount() + 1);
                        if (_log.isDebugEnabled()) {
                    		loggerValue.setLength(0);
                        	loggerValue.append("p_transactionID:");
                        	loggerValue.append(p_transactionID);
                    		loggerValue.append(" p_event:");
                        	loggerValue.append(p_event);
                        	loggerValue.append("transactionLoadVO.getCurrentSenderValidationCount()");
                        	loggerValue.append(transactionLoadVO.getCurrentSenderValidationCount());
                            _log.debug("decreaseTransactionLoad",loggerValue);
                        }
                    } else if (p_event == LoadControllerI.SENDER_TOP_SUCCESS) {
                        if (transactionLoadVO.getCurrentSenderTopupCount() > 0) {
                            transactionLoadVO.setCurrentSenderTopupCount(transactionLoadVO.getCurrentSenderTopupCount() - 1);
                        }
                        if (_log.isDebugEnabled()) {
                    		loggerValue.setLength(0);
                        	loggerValue.append("p_transactionID:");
                        	loggerValue.append(p_transactionID);
                    		loggerValue.append(" p_event:");
                        	loggerValue.append(p_event);
                        	loggerValue.append("transactionLoadVO.getCurrentSenderTopupCount()");
                        	loggerValue.append(transactionLoadVO.getCurrentSenderTopupCount());
                            _log.debug("decreaseTransactionLoad",loggerValue);
                        }
                    } else if (p_event == LoadControllerI.SENDER_TOP_FAILED) {
                        if (transactionLoadVO.getCurrentSenderTopupCount() > 0) {
                            transactionLoadVO.setCurrentSenderTopupCount(transactionLoadVO.getCurrentSenderTopupCount() - 1);
                        }
                        transactionLoadVO.setTotalSenderTopupFailCount(transactionLoadVO.getTotalSenderTopupFailCount() + 1);
                        if (_log.isDebugEnabled()) {
                    		loggerValue.setLength(0);
                        	loggerValue.append("p_transactionID:" );
                        	loggerValue.append(p_transactionID);
                    		loggerValue.append(" p_event:");
                        	loggerValue.append(p_event);
                        	loggerValue.append("transactionLoadVO.getCurrentSenderTopupCount()");
                        	loggerValue.append(transactionLoadVO.getCurrentSenderTopupCount());
                            _log.debug("decreaseTransactionLoad",loggerValue);
                        }
                    } else if (p_event == LoadControllerI.RECEIVER_VAL_SUCCESS) {
                        interfaceService = miniTransVO.getReciverInterfaceService();
                        if (_log.isDebugEnabled()) {
                    		loggerValue.setLength(0);
                        	loggerValue.append("p_transactionID:");
                        	loggerValue.append(p_transactionID);
                    		loggerValue.append( " RECEIVER_VAL_SUCCESS interfaceService=" );
                        	loggerValue.append(interfaceService);
                            _log.debug("decreaseTransactionLoad",loggerValue);
                        }
                        transactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(interfaceService);

                        if (transactionLoadVO.getCurrentRecieverValidationCount() > 0) {
                            transactionLoadVO.setCurrentRecieverValidationCount(transactionLoadVO.getCurrentRecieverValidationCount() - 1);
                        }
                        if (_log.isDebugEnabled()) {
                    		loggerValue.setLength(0);
                        	loggerValue.append("p_transactionID:");
                        	loggerValue.append(p_transactionID);
                    		loggerValue.append(" p_event:");
                        	loggerValue.append("transactionLoadVO.getCurrentRecieverValidationCount()");
                        	loggerValue.append(transactionLoadVO.getCurrentRecieverValidationCount());
                            _log.debug("decreaseTransactionLoad",loggerValue);
                        }
                    } else if (p_event == LoadControllerI.RECEIVER_VAL_FAILED) {
                        interfaceService = miniTransVO.getReciverInterfaceService();
                        if (_log.isDebugEnabled()) {
                    		loggerValue.setLength(0);
                        	loggerValue.append("p_transactionID:");
                        	loggerValue.append(p_transactionID);
                    		loggerValue.append(" RECEIVER_VAL_FAILED interfaceService=");
                        	loggerValue.append(interfaceService);
                            _log.debug("decreaseTransactionLoad",loggerValue);
                        }
                        transactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(interfaceService);

                        if (transactionLoadVO.getCurrentRecieverValidationCount() > 0) {
                            transactionLoadVO.setCurrentRecieverValidationCount(transactionLoadVO.getCurrentRecieverValidationCount() - 1);
                        }
                        transactionLoadVO.setTotalRecieverValFailCount(transactionLoadVO.getTotalRecieverValFailCount() + 1);
                        if (_log.isDebugEnabled()) {
                    		loggerValue.setLength(0);
                        	loggerValue.append("p_transactionID:");
                        	loggerValue.append(p_transactionID);
                    		loggerValue.append(" p_event:");
                        	loggerValue.append(p_event);
                        	loggerValue.append("transactionLoadVO.getCurrentRecieverValidationCount()");
                        	loggerValue.append(transactionLoadVO.getCurrentRecieverValidationCount());
                            _log.debug("decreaseTransactionLoad",loggerValue);
                        }
                    } else if (p_event == LoadControllerI.RECEIVER_TOP_SUCCESS) {
                        interfaceService = miniTransVO.getReciverInterfaceService();
                        if (_log.isDebugEnabled()) {
                    		loggerValue.setLength(0);
                        	loggerValue.append("p_transactionID:");
                        	loggerValue.append(p_transactionID);
                    		loggerValue.append(" RECEIVER_TOP_SUCCESS interfaceService=");
                        	loggerValue.append(interfaceService);
                            _log.debug("decreaseTransactionLoad",loggerValue);
                        }
                        transactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(interfaceService);

                        if (transactionLoadVO.getCurrentRecieverTopupCount() > 0) {
                            transactionLoadVO.setCurrentRecieverTopupCount(transactionLoadVO.getCurrentRecieverTopupCount() - 1);
                        }
                        if (_log.isDebugEnabled()) {
                    		loggerValue.setLength(0);
                        	loggerValue.append("p_transactionID:");
                        	loggerValue.append(p_transactionID);
                    		loggerValue.append(" p_event:");
                        	loggerValue.append(p_event);
                        	loggerValue.append("transactionLoadVO.getCurrentRecieverTopupCount()");
                        	loggerValue.append(transactionLoadVO.getCurrentRecieverTopupCount());
                            _log.debug("decreaseTransactionLoad",loggerValue);
                        }

                    } else if (p_event == LoadControllerI.RECEIVER_TOP_FAILED) {
                        interfaceService = miniTransVO.getReciverInterfaceService();
                        if (_log.isDebugEnabled()) {
                    		loggerValue.setLength(0);
                        	loggerValue.append("p_transactionID:");
                        	loggerValue.append(p_transactionID);
                    		loggerValue.append(" RECEIVER_TOP_FAILED interfaceService=");
                        	loggerValue.append(interfaceService);
                            _log.debug("decreaseTransactionLoad",loggerValue);
                        }
                        transactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(interfaceService);

                        if (transactionLoadVO.getCurrentRecieverTopupCount() > 0) {
                            transactionLoadVO.setCurrentRecieverTopupCount(transactionLoadVO.getCurrentRecieverTopupCount() - 1);
                        }
                        transactionLoadVO.setTotalRecieverTopupFailCount(transactionLoadVO.getTotalRecieverTopupFailCount() + 1);
                        if (_log.isDebugEnabled()) {
                    		loggerValue.setLength(0);
                        	loggerValue.append("p_transactionID:");
                        	loggerValue.append(p_transactionID);
                    		loggerValue.append( " p_event:");
                        	loggerValue.append(p_event);
                        	loggerValue.append("transactionLoadVO.getCurrentRecieverTopupCount()");
                        	loggerValue.append(transactionLoadVO.getCurrentRecieverTopupCount());
                            _log.debug("decreaseTransactionLoad",loggerValue);
                        }
                    }
                } finally {
                    lock.unlock();
                }
            }
    		loggerValue.setLength(0);
        	loggerValue.append("p_transactionID=");
        	loggerValue.append(p_transactionID);
    		loggerValue.append(" transactionLoadVO=");
        	loggerValue.append(transactionLoadVO);
            _log.info("decreaseTransactionLoad",loggerValue);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
    		loggerValue.setLength(0);
        	loggerValue.append("Getting exception =");
        	loggerValue.append(e.getMessage());
            _log.error("decreaseTransactionLoad",loggerValue);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "TransactionLoadController[decreaseTransactionLoad]", "", "", "", "Exception:" + e.getMessage());
        }
    }

    /**
     * This method decreases the intermediary response counters
     * 
     * @param p_transactionID
     * @param p_transactionStatus
     * @param p_event
     */
    public void decreaseResponseCounters(String p_transactionID, String p_transactionStatus, int p_event) {
		StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
    	loggerValue.append(" Entered with p_transactionID=");
    	loggerValue.append(p_transactionID);
		loggerValue.append(" p_transactionStatus=");
    	loggerValue.append(p_transactionStatus);
    	loggerValue.append(" p_event=");
    	loggerValue.append(p_event);
        if (_log.isDebugEnabled()) {
            _log.debug("decreaseResponseCounters",loggerValue );
        }
        boolean isResponseSuccess = LoadControllerUtil.isResponseSuccess(p_transactionStatus);
        // ------------------------------------------------------------------------------------------------------
        lock.lock();
        try {
            if (p_event == LoadControllerI.SENDER_VAL_RESPONSE) {
                if (isResponseSuccess) {
                    decreaseTransactionLoad(LoadControllerI.SENDER_VAL_SUCCESS, p_transactionID);
                } else {
                    decreaseTransactionLoad(LoadControllerI.SENDER_VAL_FAILED, p_transactionID);
                }
            } else if (p_event == LoadControllerI.RECEIVER_VAL_RESPONSE) {
                if (isResponseSuccess) {
                    decreaseTransactionLoad(LoadControllerI.RECEIVER_VAL_SUCCESS, p_transactionID);
                } else {
                    decreaseTransactionLoad(LoadControllerI.RECEIVER_VAL_FAILED, p_transactionID);
                }
            } else if (p_event == LoadControllerI.SENDER_TOP_RESPONSE) {
                if (isResponseSuccess) {
                    decreaseTransactionLoad(LoadControllerI.SENDER_TOP_SUCCESS, p_transactionID);
                } else {
                    decreaseTransactionLoad(LoadControllerI.SENDER_TOP_FAILED, p_transactionID);
                }
            } else if (p_event == LoadControllerI.RECEIVER_TOP_RESPONSE) {
                if (isResponseSuccess) {
                    decreaseTransactionLoad(LoadControllerI.RECEIVER_TOP_SUCCESS, p_transactionID);
                } else {
                    decreaseTransactionLoad(LoadControllerI.RECEIVER_TOP_FAILED, p_transactionID);
                }
            }
        } finally {
            lock.unlock();
        }
        if (_log.isDebugEnabled()) {
    		loggerValue.setLength(0);
        	loggerValue.append(" Exiting for p_transactionID=");
        	loggerValue.append(p_transactionID);
            _log.debug("decreaseResponseCounters",loggerValue);
        }
    }

    /**
     * Method to decrease the Transaction and Interface Load Only
     * 
     * @param p_event
     * @param p_transactionID
     */
    public void decreaseTransactionInterfaceLoad(int p_event, String p_transactionID) {
		StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
    	loggerValue.append("Entered p_transactionID:");
    	loggerValue.append(p_transactionID);
		loggerValue.append(" p_event:" );
    	loggerValue.append(p_event);
        _log.info("decreaseTransactionInterfaceLoad",loggerValue);
        final String METHOD_NAME = "decreaseTransactionInterfaceLoad";
        try {
            HashMap allTransMap = LoadControllerCache.getAllTransactionLoadListMap();
            MiniTransVO miniTransVO = (MiniTransVO) allTransMap.get(p_transactionID);
            String interfaceService = miniTransVO.getSenderInterfaceService();
            boolean isSenderOverflow = miniTransVO.isOverflow();
            String originalService = miniTransVO.getSenderOriginalService();
            TransactionLoadVO transactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(interfaceService);
            String loadType = LoadControllerCache.getLoadType();
            if (_log.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append(" p_transactionID=");
            	loggerValue.append(p_transactionID);
        		loggerValue.append("loadType=");
            	loggerValue.append(loadType);
                _log.debug("decreaseTransactionInterfaceLoad",loggerValue);
            }
            long transactionStartTime = 0;
            if (transactionLoadVO != null) {
                transactionStartTime = ((Long) transactionLoadVO.getTransactionListMap().get(p_transactionID)).longValue();
            }
            long currentTime = System.currentTimeMillis();

            if (_log.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append(" p_transactionID=" );
            	loggerValue.append(p_transactionID);
        		loggerValue.append("transactionLoadVO=");
            	loggerValue.append(transactionLoadVO);
                _log.debug("decreaseTransactionInterfaceLoad",loggerValue);
            }

            if (transactionLoadVO != null) {
                // ------------------------------------------------------------------------------------------------------
                lock.lock();
                try {
                    if (p_event == LoadControllerI.DEC_LAST_TRANS_COUNT) {
                        transactionLoadVO.getTransactionListMap().remove(p_transactionID);

                        InterfaceLoadController interfaceLoadController = LoadControllerUtil.getInterfaceLoadObject(transactionLoadVO.getInstanceID(), transactionLoadVO.getNetworkCode(), transactionLoadVO.getInterfaceID());

                            if (transactionLoadVO.getCurrentTransactionLoad() > 0) {
                                transactionLoadVO.setCurrentTransactionLoad(transactionLoadVO.getCurrentTransactionLoad() - 1);
                            }
                            if (isSenderOverflow) {
                                TransactionLoadVO origTransactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(originalService);
                                if (origTransactionLoadVO.getOverFlowCount() > 0) {
                                    origTransactionLoadVO.setOverFlowCount(origTransactionLoadVO.getOverFlowCount() - 1);
                                }
                                if (_log.isDebugEnabled()) {
                            		loggerValue.setLength(0);
                                	loggerValue.append("p_transactionID=");
                                	loggerValue.append(p_transactionID);
                            		loggerValue.append(" isSenderOverflow =");
                                	loggerValue.append(isSenderOverflow);
                                	loggerValue.append(" Decreasing Count to =");
                                	loggerValue.append(origTransactionLoadVO.getOverFlowCount());
                                    _log.debug("decreaseTransactionInterfaceLoad",loggerValue);
                                }
                            }
                            interfaceLoadController.decreaseCurrentInterfaceLoad(LoadControllerI.DEC_LAST_TRANS_COUNT, transactionLoadVO.getNetworkCode(), transactionLoadVO.getInterfaceID(), p_transactionID);

                        
                        // Set average time for the instance+network+service
                        transactionLoadVO.setAverageServiceTime((transactionLoadVO.getAverageServiceTime() + (currentTime - transactionStartTime)) / 2);

                        String recieverService = miniTransVO.getReciverInterfaceService();
                        if (recieverService != null && !BTSLUtil.isNullString(recieverService)) {

                            boolean isRecieverOverflow = miniTransVO.isRecieverOverflow();
                            String originalRecieverService = miniTransVO.getReciverOriginalService();

                            TransactionLoadVO receiverTransVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(recieverService);
                            interfaceLoadController = LoadControllerUtil.getInterfaceLoadObject(receiverTransVO.getInstanceID(), receiverTransVO.getNetworkCode(), receiverTransVO.getInterfaceID());
                            if (_log.isDebugEnabled()) {
                        		loggerValue.setLength(0);
                            	loggerValue.append(" p_transactionID=");
                            	loggerValue.append(p_transactionID);
                        		loggerValue.append(" isRecieverOverflow=");
                            	loggerValue.append(isRecieverOverflow);
                            	loggerValue.append(" originalRecieverService=");
                            	loggerValue.append(originalRecieverService);
                            	loggerValue.append(" recieverService=");
                            	loggerValue.append(recieverService);
                            	loggerValue.append("interfaceLoadController=");
                            	loggerValue.append(interfaceLoadController);
                            	loggerValue.append(" receiverTransVO.getCurrentTransactionLoad()=");
                            	loggerValue.append(receiverTransVO.getCurrentTransactionLoad());
                                _log.debug("decreaseTransactionInterfaceLoad",loggerValue);
                            }

                            if (receiverTransVO.getCurrentTransactionLoad() > 0) {
                                receiverTransVO.setCurrentTransactionLoad(receiverTransVO.getCurrentTransactionLoad() - 1);
                            }
                            interfaceLoadController.decreaseCurrentInterfaceLoad(LoadControllerI.DEC_LAST_TRANS_COUNT, receiverTransVO.getNetworkCode(), receiverTransVO.getInterfaceID(), p_transactionID);

                            if (isRecieverOverflow) {
                                TransactionLoadVO origReceiverTransactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(originalRecieverService);
                                if (origReceiverTransactionLoadVO.getOverFlowCount() > 0) {
                                    origReceiverTransactionLoadVO.setOverFlowCount(origReceiverTransactionLoadVO.getOverFlowCount() - 1);
                                }
                                if (_log.isDebugEnabled()) {
                            		loggerValue.setLength(0);
                                	loggerValue.append("p_transactionID=");
                                	loggerValue.append(p_transactionID);
                            		loggerValue.append(" isRecieverOverflow =");
                                	loggerValue.append(isRecieverOverflow);
                                	loggerValue.append(" Decreasing Count to =");
                                	loggerValue.append(origReceiverTransactionLoadVO.getOverFlowCount());
                                    _log.debug("decreaseTransactionInterfaceLoad",loggerValue);
                                }
                            }

                            // Set average time for the instance+network+service
                            receiverTransVO.setAverageServiceTime((transactionLoadVO.getAverageServiceTime() + (currentTime - transactionStartTime)) / 2);
                        }
                        allTransMap.remove(p_transactionID);
                    }
                } finally {
                    lock.unlock();
                }
            }
    		loggerValue.setLength(0);
        	loggerValue.append(" p_transactionID=");
        	loggerValue.append(p_transactionID);
    		loggerValue.append("transactionLoadVO.getCurrentTransactionLoad()=");
        	loggerValue.append(((null != transactionLoadVO) ? transactionLoadVO.getCurrentTransactionLoad():0 ));
            _log.info("decreaseTransactionInterfaceLoad", loggerValue);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
    		loggerValue.setLength(0);
        	loggerValue.append("Getting exception =");
        	loggerValue.append(e.getMessage());
            _log.error("decreaseTransactionInterfaceLoad",loggerValue);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "TransactionLoadController[decreaseTransactionInterfaceLoad]", "", "", "", "Exception:" + e.getMessage());
        }
    }

    /**
     * Method to decrease the Transaction and Interface Load Only
     * 
     * @param p_event
     * @param p_transactionID
     */
    public void decreaseReceiverTransactionInterfaceLoad(int p_event, String p_transactionID) {
		StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
    		loggerValue.setLength(0);
        	loggerValue.append("Entered p_transactionID:");
        	loggerValue.append(p_transactionID);
    		loggerValue.append(" p_event:");
        	loggerValue.append(p_event);
            _log.debug("decreaseReceiverTransactionInterfaceLoad",loggerValue);
        }
        final String METHOD_NAME = "decreaseReceiverTransactionInterfaceLoad";
        try {
            // ------------------------------------------------------------------------------------------------------
            lock.lock();
            try {
                HashMap allTransMap = LoadControllerCache.getAllTransactionLoadListMap();
                MiniTransVO miniTransVO = (MiniTransVO) allTransMap.get(p_transactionID);
                String recieverService = miniTransVO.getReciverInterfaceService();

                String interfaceService = miniTransVO.getSenderInterfaceService();
                TransactionLoadVO transactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(interfaceService);

                long transactionStartTime = ((Long) transactionLoadVO.getTransactionListMap().get(p_transactionID)).longValue();
                long currentTime = System.currentTimeMillis();

                if (recieverService != null && !BTSLUtil.isNullString(recieverService)) {
                    boolean isRecieverOverflow = miniTransVO.isRecieverOverflow();
                    String originalRecieverService = miniTransVO.getReciverOriginalService();

                    TransactionLoadVO receiverTransVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(recieverService);
                    InterfaceLoadController interfaceLoadController = LoadControllerUtil.getInterfaceLoadObject(receiverTransVO.getInstanceID(), receiverTransVO.getNetworkCode(), receiverTransVO.getInterfaceID());
                    if (_log.isDebugEnabled()) {
                		loggerValue.setLength(0);
                    	loggerValue.append(" p_transactionID=");
                    	loggerValue.append(p_transactionID);
                		loggerValue.append(" recieverService=");
                    	loggerValue.append(recieverService);
                    	loggerValue.append(" originalRecieverService=");
                    	loggerValue.append(originalRecieverService);
                    	loggerValue.append(" interfaceLoadController=");
                    	loggerValue.append(interfaceLoadController);
                    	loggerValue.append("receiverTransVO.getCurrentTransactionLoad()=");
                    	loggerValue.append(receiverTransVO.getCurrentTransactionLoad());
                        _log.debug("decreaseReceiverTransactionInterfaceLoad",loggerValue);
                    }

                    if (receiverTransVO.getCurrentTransactionLoad() > 0) {
                        receiverTransVO.setCurrentTransactionLoad(receiverTransVO.getCurrentTransactionLoad() - 1);
                    }
                    interfaceLoadController.decreaseCurrentInterfaceLoad(LoadControllerI.DEC_LAST_TRANS_COUNT, receiverTransVO.getNetworkCode(), receiverTransVO.getInterfaceID(), p_transactionID);

                    if (isRecieverOverflow) {
                        TransactionLoadVO origReceiverTransactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(originalRecieverService);
                        if (origReceiverTransactionLoadVO.getOverFlowCount() > 0) {
                            origReceiverTransactionLoadVO.setOverFlowCount(origReceiverTransactionLoadVO.getOverFlowCount() - 1);
                        }
                        if (_log.isDebugEnabled()) {
                    		loggerValue.setLength(0);
                        	loggerValue.append("p_transactionID=");
                        	loggerValue.append(p_transactionID);
                    		loggerValue.append(" isRecieverOverflow =");
                        	loggerValue.append(isRecieverOverflow);
                        	loggerValue.append(" Decreasing Count to =");
                        	loggerValue.append(origReceiverTransactionLoadVO.getOverFlowCount());
                            _log.debug("decreaseReceiverTransactionInterfaceLoad",loggerValue);
                        }
                    }

                    // Set average time for the instance+network+service
                    receiverTransVO.setAverageServiceTime((receiverTransVO.getAverageServiceTime() + (currentTime - transactionStartTime)) / 2);
                    
                }
            } finally {
                lock.unlock();
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
    		loggerValue.setLength(0);
        	loggerValue.append("Getting exception =" );
        	loggerValue.append(e.getMessage());
            _log.error("decreaseReceiverTransactionInterfaceLoad",loggerValue);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "TransactionLoadController[decreaseReceiverTransactionInterfaceLoad]", "", "", "", "Exception:" + e.getMessage());
        }
		loggerValue.setLength(0);
    	loggerValue.append(" p_transactionID=");
    	loggerValue.append(p_transactionID);
        _log.info("decreaseReceiverTransactionInterfaceLoad",loggerValue);
    }
}
