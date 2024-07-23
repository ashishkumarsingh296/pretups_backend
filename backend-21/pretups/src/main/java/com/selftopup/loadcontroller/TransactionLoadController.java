package com.selftopup.loadcontroller;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.util.BTSLUtil;

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

    private Log _log = LogFactory.getLog(this.getClass().getName());
    private final static ReentrantLock lock = new ReentrantLock();

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
        _log.info(" checkTransactionLoad", "Entered with p_networkID=" + p_networkID + " p_interfaceID=" + p_interfaceID + " p_serviceType=" + p_serviceType + " p_originalService=" + p_originalService + " p_transactionNo=" + p_transactionNo + " p_checkAlternateService=" + p_checkAlternateService + " p_userType=" + p_userType);
        boolean retStatus = true;
        String instanceID = LoadControllerCache.getInstanceID();

        long currentTime = System.currentTimeMillis();
        Timestamp timeStampVal = new Timestamp(currentTime);
        long previousUPTime = 0;
        boolean isSameSecRequest = false;
        TransactionLoadVO transactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(instanceID + "_" + p_networkID + "_" + p_interfaceID + "_" + p_serviceType);

        String loadType = LoadControllerCache.getLoadType();
        if (_log.isDebugEnabled())
            _log.debug("checkTransactionLoad", "Load Type to be used=" + loadType);

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
                            if (_log.isDebugEnabled())
                                _log.debug("checkTransactionLoad", "Request in Same Second and is within the allowed limit=" + transactionLoadVO.getCurrentTPS() + " Current =" + transactionLoadVO.getNoOfRequestSameSec() + " , checking for Transaction Load");
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
                            if (_log.isDebugEnabled())
                                _log.debug("checkTransactionLoad", "Request in Same Second and is not within the allowed limit=" + transactionLoadVO.getCurrentTPS() + " Current =" + transactionLoadVO.getNoOfRequestSameSec() + " , Refusing the request");

                        }
                    } else {
                        if (_log.isDebugEnabled())
                            _log.debug("checkTransactionLoad", "Request is Not in Same Second , checking for Transaction Load");

                        retStatus = checkCurrentInterServiceLoad(transactionLoadVO, instanceID, p_networkID, p_interfaceID, p_serviceType, p_originalService, currentTime, timeStampVal, p_transactionNo, p_checkAlternateService, p_userType);
                        if (retStatus) {
                            transactionLoadVO.setPreviousSecond(currentTime);
                            transactionLoadVO.setNoOfRequestSameSec(1);
                            transactionLoadVO.setLastTxnProcessStartTime(timeStampVal);
                        }
                    }
                } else {
                    if (_log.isDebugEnabled())
                        _log.debug("checkTransactionLoad", "Not TPS based checking for Transaction Load");

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
        _log.info("checkTransactionLoad", "Exiting with transactionLoadVO=" + transactionLoadVO + "retStatus :" + retStatus);
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
        _log.info("checkAlternateService", "p_transactionNo=" + p_transactionNo + " p_interfaceID=" + p_interfaceID + " p_originalService=" + p_originalService + " p_userType=" + p_userType);
        boolean alterateService = false;
        TransactionLoadVO alternateTransVO = null;
        TransactionLoadVO serviceTransactionLoadVO = null;
        if (p_transactionLoadVO.getAlternateServiceLoadType() != null && !p_transactionLoadVO.getAlternateServiceLoadType().isEmpty()) {
            if (_log.isDebugEnabled())
                _log.debug("checkAlternateService", "Alternate Service Load type=" + p_transactionLoadVO.getAlternateServiceLoadType());

            for (int i = 0; i < p_transactionLoadVO.getAlternateServiceLoadType().size(); i++) {
                alternateTransVO = (TransactionLoadVO) p_transactionLoadVO.getAlternateServiceLoadType().get(i);
                serviceTransactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(p_instanceID + "_" + p_networkID + "_" + p_interfaceID + "_" + alternateTransVO.getServiceType());
                _log.info("checkAlternateService", "Performing checks for Alternate Service type=" + alternateTransVO.getServiceType() + " For p_transactionNo=" + p_transactionNo);

                boolean requestOK = checkTransactionLoad(p_networkID, p_interfaceID, alternateTransVO.getServiceType(), p_originalService, p_transactionNo, false, p_userType);
                if (requestOK)
                    alterateService = true;
                else
                    continue;
            }
        }
        _log.info("checkAlternateService", "Exiting for p_transactionNo=" + p_transactionNo + " alterateService=" + alterateService);
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
        _log.info("decreaseTransactionLoad", "Entered p_transactionID:" + p_transactionID + " p_event:" + p_event + "p_senderNetwork=" + p_senderNetwork);
        try {
            HashMap allTransMap = LoadControllerCache.getAllTransactionLoadListMap();
            MiniTransVO miniTransVO = (MiniTransVO) allTransMap.get(p_transactionID);
            String interfaceService = miniTransVO.getSenderInterfaceService();
            boolean isSenderOverflow = miniTransVO.isOverflow();
            String originalService = miniTransVO.getSenderOriginalService();
            TransactionLoadVO transactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(interfaceService);
            String loadType = LoadControllerCache.getLoadType();

            long transactionStartTime = ((Long) transactionLoadVO.getTransactionListMap().get(p_transactionID)).longValue();
            long currentTime = System.currentTimeMillis();

            if (_log.isDebugEnabled())
                _log.debug("decreaseTransactionLoad", "p_transactionID:" + p_transactionID + " p_event:" + p_event + "transactionLoadVO=" + transactionLoadVO);

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
                        if (LoadControllerI.LOAD_CONTROLLER_TXN_TYPE.equalsIgnoreCase(loadType)) {
                            if (transactionLoadVO.getCurrentTransactionLoad() > 0)
                                transactionLoadVO.setCurrentTransactionLoad(transactionLoadVO.getCurrentTransactionLoad() - 1);

                            if (isSenderOverflow) {
                                TransactionLoadVO origTransactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(originalService);
                                if (origTransactionLoadVO.getOverFlowCount() > 0)
                                    origTransactionLoadVO.setOverFlowCount(origTransactionLoadVO.getOverFlowCount() - 1);
                                if (_log.isDebugEnabled())
                                    _log.debug("decreaseTransactionLoad", "p_transactionID=" + p_transactionID + " isSenderOverflow =" + isSenderOverflow + " Decreasing Count to =" + origTransactionLoadVO.getOverFlowCount());
                            }
                            interfaceLoadController.decreaseCurrentInterfaceLoadUnlock(LoadControllerI.DEC_LAST_TRANS_COUNT, transactionLoadVO.getNetworkCode(), transactionLoadVO.getInterfaceID(), p_transactionID);
                            networkLoadController.decreaseCurrentNetworkLoadUnlock(p_senderNetwork, LoadControllerI.DEC_LAST_TRANS_COUNT);
                            instanceLoadController.decreaseCurrentInstanceLoadUnlock(transactionLoadVO.getInstanceID(), LoadControllerI.DEC_LAST_TRANS_COUNT);
                        } else // For TPS Control also
                        {
                            if (transactionLoadVO.getCurrentTransactionLoad() > 0)
                                transactionLoadVO.setCurrentTransactionLoad(transactionLoadVO.getCurrentTransactionLoad() - 1);
                            if (isSenderOverflow) {
                                TransactionLoadVO origTransactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(originalService);
                                if (origTransactionLoadVO.getOverFlowCount() > 0)
                                    origTransactionLoadVO.setOverFlowCount(origTransactionLoadVO.getOverFlowCount() - 1);
                                if (_log.isDebugEnabled())
                                    _log.debug("decreaseTransactionLoad", "p_transactionID=" + p_transactionID + " isSenderOverflow =" + isSenderOverflow + " Decreasing Count to =" + origTransactionLoadVO.getOverFlowCount());

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

                            if (_log.isDebugEnabled())
                                _log.debug("decreaseTransactionLoad", " Transaction ID=" + p_transactionID + " recieverService=" + recieverService);

                            boolean isRecieverOverflow = miniTransVO.isRecieverOverflow();
                            String originalRecieverService = miniTransVO.getReciverOriginalService();

                            TransactionLoadVO receiverTransVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(recieverService);
                            interfaceLoadController = LoadControllerUtil.getInterfaceLoadObject(receiverTransVO.getInstanceID(), receiverTransVO.getNetworkCode(), receiverTransVO.getInterfaceID());
                            if (_log.isDebugEnabled())
                                _log.debug("decreaseTransactionLoad", " Transaction ID=" + p_transactionID + " isRecieverOverflow=" + isRecieverOverflow + " originalRecieverService=" + originalRecieverService + " interfaceLoadController=" + interfaceLoadController + " receiverTransVO.getCurrentTransactionLoad()=" + receiverTransVO.getCurrentTransactionLoad());

                            if (receiverTransVO.getCurrentTransactionLoad() > 0)
                                receiverTransVO.setCurrentTransactionLoad(receiverTransVO.getCurrentTransactionLoad() - 1);
                            interfaceLoadController.decreaseCurrentInterfaceLoad(LoadControllerI.DEC_LAST_TRANS_COUNT, receiverTransVO.getNetworkCode(), receiverTransVO.getInterfaceID(), p_transactionID);

                            if (isRecieverOverflow) {
                                TransactionLoadVO origReceiverTransactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(originalRecieverService);
                                if (origReceiverTransactionLoadVO.getOverFlowCount() > 0)
                                    origReceiverTransactionLoadVO.setOverFlowCount(origReceiverTransactionLoadVO.getOverFlowCount() - 1);
                                if (_log.isDebugEnabled())
                                    _log.debug("decreaseTransactionLoad", "p_transactionID=" + p_transactionID + " isRecieverOverflow =" + isRecieverOverflow + " Decreasing Count to =" + origReceiverTransactionLoadVO.getOverFlowCount());
                            }

                            // Set average time for the instance+network+service
                            receiverTransVO.setAverageServiceTime((transactionLoadVO.getAverageServiceTime() + (currentTime - transactionStartTime)) / 2);
                        }
                        allTransMap.remove(p_transactionID);
                    } else if (p_event == LoadControllerI.SENDER_VAL_SUCCESS) {
                        if (transactionLoadVO.getCurrentSenderValidationCount() > 0)
                            transactionLoadVO.setCurrentSenderValidationCount(transactionLoadVO.getCurrentSenderValidationCount() - 1);
                        if (_log.isDebugEnabled())
                            _log.debug("decreaseTransactionLoad", "p_transactionID:" + p_transactionID + " p_event:" + p_event + "transactionLoadVO.getCurrentSenderValidationCount()" + transactionLoadVO.getCurrentSenderValidationCount());

                    } else if (p_event == LoadControllerI.SENDER_VAL_FAILED) {
                        if (transactionLoadVO.getCurrentSenderValidationCount() > 0)
                            transactionLoadVO.setCurrentSenderValidationCount(transactionLoadVO.getCurrentSenderValidationCount() - 1);
                        transactionLoadVO.setTotalSenderValFailCount(transactionLoadVO.getTotalSenderValFailCount() + 1);
                        if (_log.isDebugEnabled())
                            _log.debug("decreaseTransactionLoad", "p_transactionID:" + p_transactionID + " p_event:" + p_event + "transactionLoadVO.getCurrentSenderValidationCount()" + transactionLoadVO.getCurrentSenderValidationCount());
                    } else if (p_event == LoadControllerI.SENDER_TOP_SUCCESS) {
                        if (transactionLoadVO.getCurrentSenderTopupCount() > 0)
                            transactionLoadVO.setCurrentSenderTopupCount(transactionLoadVO.getCurrentSenderTopupCount() - 1);
                        if (_log.isDebugEnabled())
                            _log.debug("decreaseTransactionLoad", "p_transactionID:" + p_transactionID + " p_event:" + p_event + "transactionLoadVO.getCurrentSenderTopupCount()" + transactionLoadVO.getCurrentSenderTopupCount());
                    } else if (p_event == LoadControllerI.SENDER_TOP_FAILED) {
                        if (transactionLoadVO.getCurrentSenderTopupCount() > 0)
                            transactionLoadVO.setCurrentSenderTopupCount(transactionLoadVO.getCurrentSenderTopupCount() - 1);
                        transactionLoadVO.setTotalSenderTopupFailCount(transactionLoadVO.getTotalSenderTopupFailCount() + 1);
                        if (_log.isDebugEnabled())
                            _log.debug("decreaseTransactionLoad", "p_transactionID:" + p_transactionID + " p_event:" + p_event + "transactionLoadVO.getCurrentSenderTopupCount()" + transactionLoadVO.getCurrentSenderTopupCount());
                    } else if (p_event == LoadControllerI.RECEIVER_VAL_SUCCESS) {
                        interfaceService = miniTransVO.getReciverInterfaceService();
                        if (_log.isDebugEnabled())
                            _log.debug("decreaseTransactionLoad", "p_transactionID:" + p_transactionID + " RECEIVER_VAL_SUCCESS interfaceService=" + interfaceService);
                        transactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(interfaceService);

                        if (transactionLoadVO.getCurrentRecieverValidationCount() > 0)
                            transactionLoadVO.setCurrentRecieverValidationCount(transactionLoadVO.getCurrentRecieverValidationCount() - 1);
                        if (_log.isDebugEnabled())
                            _log.debug("decreaseTransactionLoad", "p_transactionID:" + p_transactionID + " p_event:" + p_event + "transactionLoadVO.getCurrentRecieverValidationCount()" + transactionLoadVO.getCurrentRecieverValidationCount());
                    } else if (p_event == LoadControllerI.RECEIVER_VAL_FAILED) {
                        interfaceService = miniTransVO.getReciverInterfaceService();
                        if (_log.isDebugEnabled())
                            _log.debug("decreaseTransactionLoad", "p_transactionID:" + p_transactionID + " RECEIVER_VAL_FAILED interfaceService=" + interfaceService);
                        transactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(interfaceService);

                        if (transactionLoadVO.getCurrentRecieverValidationCount() > 0)
                            transactionLoadVO.setCurrentRecieverValidationCount(transactionLoadVO.getCurrentRecieverValidationCount() - 1);
                        transactionLoadVO.setTotalRecieverValFailCount(transactionLoadVO.getTotalRecieverValFailCount() + 1);
                        if (_log.isDebugEnabled())
                            _log.debug("decreaseTransactionLoad", "p_transactionID:" + p_transactionID + " p_event:" + p_event + "transactionLoadVO.getCurrentRecieverValidationCount()" + transactionLoadVO.getCurrentRecieverValidationCount());
                    } else if (p_event == LoadControllerI.RECEIVER_TOP_SUCCESS) {
                        interfaceService = miniTransVO.getReciverInterfaceService();
                        if (_log.isDebugEnabled())
                            _log.debug("decreaseTransactionLoad", "p_transactionID:" + p_transactionID + " RECEIVER_TOP_SUCCESS interfaceService=" + interfaceService);
                        transactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(interfaceService);

                        if (transactionLoadVO.getCurrentRecieverTopupCount() > 0)
                            transactionLoadVO.setCurrentRecieverTopupCount(transactionLoadVO.getCurrentRecieverTopupCount() - 1);
                        if (_log.isDebugEnabled())
                            _log.debug("decreaseTransactionLoad", "p_transactionID:" + p_transactionID + " p_event:" + p_event + "transactionLoadVO.getCurrentRecieverTopupCount()" + transactionLoadVO.getCurrentRecieverTopupCount());

                    } else if (p_event == LoadControllerI.RECEIVER_TOP_FAILED) {
                        interfaceService = miniTransVO.getReciverInterfaceService();
                        if (_log.isDebugEnabled())
                            _log.debug("decreaseTransactionLoad", "p_transactionID:" + p_transactionID + " RECEIVER_TOP_FAILED interfaceService=" + interfaceService);
                        transactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(interfaceService);

                        if (transactionLoadVO.getCurrentRecieverTopupCount() > 0)
                            transactionLoadVO.setCurrentRecieverTopupCount(transactionLoadVO.getCurrentRecieverTopupCount() - 1);
                        transactionLoadVO.setTotalRecieverTopupFailCount(transactionLoadVO.getTotalRecieverTopupFailCount() + 1);
                        if (_log.isDebugEnabled())
                            _log.debug("decreaseTransactionLoad", "p_transactionID:" + p_transactionID + " p_event:" + p_event + "transactionLoadVO.getCurrentRecieverTopupCount()" + transactionLoadVO.getCurrentRecieverTopupCount());
                    }
                } finally {
                    lock.unlock();
                }
            }
            _log.info("decreaseTransactionLoad", "p_transactionID=" + p_transactionID + " transactionLoadVO=" + transactionLoadVO);
        } catch (Exception e) {
            _log.errorTrace("decreaseTransactionLoad: Exception print stack trace:=", e);
            _log.error("decreaseTransactionLoad", "Getting exception =" + e.getMessage());
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
        if (_log.isDebugEnabled())
            _log.debug(" incrementTransactionInterCounts() ", " Entered:: p_event=" + p_event + " p_transactionID=" + p_transactionID);
        boolean flag = false;
        long currentTime = System.currentTimeMillis();
        Timestamp timeStampVal = new Timestamp(currentTime);

        String instanceID = LoadControllerCache.getInstanceID();
        if (BTSLUtil.isNullString(instanceID))
            instanceID = LoadControllerI.DEFAULT_INSTANCE_ID;
        try {

            HashMap allTransMap = LoadControllerCache.getAllTransactionLoadListMap();
            MiniTransVO miniTransVO = (MiniTransVO) allTransMap.get(p_transactionID);
            String interfaceService = miniTransVO.getSenderInterfaceService();
            TransactionLoadVO transactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(interfaceService);
            if (_log.isDebugEnabled())
                _log.debug(" incrementTransactionInterCounts() ", " p_event=" + p_event + " p_transactionID=" + p_transactionID + " transactionLoadVO=" + transactionLoadVO);

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
                        if (_log.isDebugEnabled())
                            _log.debug("incrementTransactionInterCounts", "p_transactionID:" + p_transactionID + " RECEIVER_UNDER_VAL interfaceService=" + interfaceService);
                        transactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(interfaceService);

                        flag = true;
                        transactionLoadVO.setCurrentRecieverValidationCount(transactionLoadVO.getCurrentRecieverValidationCount() + 1);
                        transactionLoadVO.setTotalRecieverValidationCount(transactionLoadVO.getTotalRecieverValidationCount() + 1);
                    } else if (p_event == LoadControllerI.SENDER_UNDER_TOP) {
                        flag = true;
                        // transactionLoadVO.setCurrentSenderValidationCount(transactionLoadVO.getCurrentSenderValidationCount()-1);
                        transactionLoadVO.setCurrentSenderTopupCount(transactionLoadVO.getCurrentSenderTopupCount() + 1);
                        transactionLoadVO.setTotalSenderTopupCount(transactionLoadVO.getTotalSenderTopupCount() + 1);
                    } else if (p_event == LoadControllerI.RECEIVER_UNDER_TOP) {
                        interfaceService = miniTransVO.getReciverInterfaceService();
                        if (_log.isDebugEnabled())
                            _log.debug("incrementTransactionInterCounts", "p_transactionID:" + p_transactionID + " RECEIVER_UNDER_TOP interfaceService=" + interfaceService);
                        transactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(interfaceService);

                        flag = true;
                        // transactionLoadVO.setCurrentRecieverValidationCount(transactionLoadVO.getCurrentRecieverValidationCount()-1);
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
            if (_log.isDebugEnabled())
                _log.debug(" incrementTransactionInterCounts() ", " p_event=" + p_event + " p_transactionID=" + p_transactionID + " transactionLoadVO=" + transactionLoadVO + " flag=" + flag);
        } catch (Exception e) {
            _log.errorTrace("incrementTransactionInterCounts: Exception print stack trace:=", e);
            _log.error("incrementTransactionInterCounts", "Getting exception =" + e.getMessage());
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
        _log.info("checkCurrentInterServiceLoad", " Entered with p_transactionLoadVO=" + p_transactionLoadVO + " p_instanceID=" + p_instanceID + " p_networkID=" + p_networkID + " p_interfaceID=" + p_interfaceID + " p_serviceType=" + p_serviceType + " p_originalService=" + p_originalService + " p_transactionNo=" + p_transactionNo + " p_checkAlternateService=" + p_checkAlternateService + " p_userType=" + p_userType);
        boolean retStatus = false;
        boolean isServiceChanged = false;
        if (!p_serviceType.equalsIgnoreCase(p_originalService))
            isServiceChanged = true;
        if (_log.isDebugEnabled())
            _log.debug("checkCurrentInterServiceLoad", "Current Load =" + p_transactionLoadVO.getCurrentTransactionLoad() + " Allowed=" + p_transactionLoadVO.getTransactionLoad());

        if (p_transactionLoadVO.getCurrentTransactionLoad() < p_transactionLoadVO.getTransactionLoad()) {
            if (_log.isDebugEnabled())
                _log.debug("checkCurrentInterServiceLoad", "Allowing Request to go through");

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
                hash.put(p_transactionNo, new Long(p_currentTime));
                p_transactionLoadVO.setTransactionListMap(hash);

                HashMap mainHash = LoadControllerCache.getAllTransactionLoadListMap();
                mainHash.put(p_transactionNo, new MiniTransVO(p_instanceID + "_" + p_networkID + "_" + p_interfaceID + "_" + p_serviceType, p_currentTime, isServiceChanged, p_instanceID + "_" + p_networkID + "_" + p_interfaceID + "_" + p_originalService));

                if (_log.isDebugEnabled())
                    _log.debug("checkCurrentInterServiceLoad", "Setting Sender in Main Hash Table for all Transactions For Transaction ID=" + p_transactionNo);

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
                if (_log.isDebugEnabled())
                    _log.debug("checkCurrentInterServiceLoad", "Setting Receiver VO as " + miniTransVO + " For Transaction ID=" + p_transactionNo);

                mainHash.put(p_transactionNo, miniTransVO);
            }

        } else if (p_checkAlternateService) {
            if (_log.isDebugEnabled())
                _log.debug("checkCurrentInterServiceLoad", "Checking for Alternate Service Load Check Current Overflow Count=" + p_transactionLoadVO.getOverFlowCount() + " defined Overflow=" + p_transactionLoadVO.getDefinedOverFlowCount());

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
        _log.info("checkCurrentInterServiceLoad", " Exiting with status=" + retStatus);
        return retStatus;
    }

    public void decreaseTransactionLoad(int p_event, String p_transactionID) {
        _log.info("decreaseTransactionLoad", "Entered p_transactionID:" + p_transactionID + " p_event:" + p_event);
        try {
            HashMap allTransMap = LoadControllerCache.getAllTransactionLoadListMap();
            MiniTransVO miniTransVO = (MiniTransVO) allTransMap.get(p_transactionID);
            String interfaceService = miniTransVO.getSenderInterfaceService();
            boolean isSenderOverflow = miniTransVO.isOverflow();
            String originalService = miniTransVO.getSenderOriginalService();
            TransactionLoadVO transactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(interfaceService);
            String loadType = LoadControllerCache.getLoadType();

            long transactionStartTime = ((Long) transactionLoadVO.getTransactionListMap().get(p_transactionID)).longValue();
            long currentTime = System.currentTimeMillis();

            if (_log.isDebugEnabled())
                _log.debug("decreaseTransactionLoad", "p_transactionID:" + p_transactionID + " p_event:" + p_event + "transactionLoadVO=" + transactionLoadVO);

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
                        if (LoadControllerI.LOAD_CONTROLLER_TXN_TYPE.equalsIgnoreCase(loadType)) {
                            if (transactionLoadVO.getCurrentTransactionLoad() > 0)
                                transactionLoadVO.setCurrentTransactionLoad(transactionLoadVO.getCurrentTransactionLoad() - 1);

                            if (isSenderOverflow) {
                                TransactionLoadVO origTransactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(originalService);
                                if (origTransactionLoadVO.getOverFlowCount() > 0)
                                    origTransactionLoadVO.setOverFlowCount(origTransactionLoadVO.getOverFlowCount() - 1);
                                if (_log.isDebugEnabled())
                                    _log.debug("decreaseTransactionLoad", "p_transactionID=" + p_transactionID + " isSenderOverflow =" + isSenderOverflow + " Decreasing Count to =" + origTransactionLoadVO.getOverFlowCount());
                            }
                            interfaceLoadController.decreaseCurrentInterfaceLoad(LoadControllerI.DEC_LAST_TRANS_COUNT, transactionLoadVO.getNetworkCode(), transactionLoadVO.getInterfaceID(), p_transactionID);
                            networkLoadController.decreaseCurrentNetworkLoad(transactionLoadVO.getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);
                            instanceLoadController.decreaseCurrentInstanceLoad(transactionLoadVO.getInstanceID(), LoadControllerI.DEC_LAST_TRANS_COUNT);
                        } else // For TPS Control also
                        {
                            if (transactionLoadVO.getCurrentTransactionLoad() > 0)
                                transactionLoadVO.setCurrentTransactionLoad(transactionLoadVO.getCurrentTransactionLoad() - 1);
                            if (isSenderOverflow) {
                                TransactionLoadVO origTransactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(originalService);
                                if (origTransactionLoadVO.getOverFlowCount() > 0)
                                    origTransactionLoadVO.setOverFlowCount(origTransactionLoadVO.getOverFlowCount() - 1);
                                if (_log.isDebugEnabled())
                                    _log.debug("decreaseTransactionLoad", "p_transactionID=" + p_transactionID + " isSenderOverflow =" + isSenderOverflow + " Decreasing Count to =" + origTransactionLoadVO.getOverFlowCount());

                            }
                            interfaceLoadController.decreaseCurrentInterfaceLoad(LoadControllerI.DEC_LAST_TRANS_COUNT, transactionLoadVO.getNetworkCode(), transactionLoadVO.getInterfaceID(), p_transactionID);
                            networkLoadController.decreaseCurrentNetworkLoad(transactionLoadVO.getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);
                            instanceLoadController.decreaseCurrentInstanceLoad(transactionLoadVO.getInstanceID(), LoadControllerI.DEC_LAST_TRANS_COUNT);

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

                            if (_log.isDebugEnabled())
                                _log.debug("decreaseTransactionLoad", " Transaction ID=" + p_transactionID + " recieverService=" + recieverService);

                            boolean isRecieverOverflow = miniTransVO.isRecieverOverflow();
                            String originalRecieverService = miniTransVO.getReciverOriginalService();

                            TransactionLoadVO receiverTransVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(recieverService);
                            interfaceLoadController = LoadControllerUtil.getInterfaceLoadObject(receiverTransVO.getInstanceID(), receiverTransVO.getNetworkCode(), receiverTransVO.getInterfaceID());
                            if (_log.isDebugEnabled())
                                _log.debug("decreaseTransactionLoad", " Transaction ID=" + p_transactionID + " isRecieverOverflow=" + isRecieverOverflow + " originalRecieverService=" + originalRecieverService + " interfaceLoadController=" + interfaceLoadController + " receiverTransVO.getCurrentTransactionLoad()=" + receiverTransVO.getCurrentTransactionLoad());

                            if (receiverTransVO.getCurrentTransactionLoad() > 0)
                                receiverTransVO.setCurrentTransactionLoad(receiverTransVO.getCurrentTransactionLoad() - 1);
                            interfaceLoadController.decreaseCurrentInterfaceLoad(LoadControllerI.DEC_LAST_TRANS_COUNT, receiverTransVO.getNetworkCode(), receiverTransVO.getInterfaceID(), p_transactionID);

                            if (isRecieverOverflow) {
                                TransactionLoadVO origReceiverTransactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(originalRecieverService);
                                if (origReceiverTransactionLoadVO.getOverFlowCount() > 0)
                                    origReceiverTransactionLoadVO.setOverFlowCount(origReceiverTransactionLoadVO.getOverFlowCount() - 1);
                                if (_log.isDebugEnabled())
                                    _log.debug("decreaseTransactionLoad", "p_transactionID=" + p_transactionID + " isRecieverOverflow =" + isRecieverOverflow + " Decreasing Count to =" + origReceiverTransactionLoadVO.getOverFlowCount());
                            }

                            // Set average time for the instance+network+service
                            receiverTransVO.setAverageServiceTime((transactionLoadVO.getAverageServiceTime() + (currentTime - transactionStartTime)) / 2);
                        }
                        allTransMap.remove(p_transactionID);
                    } else if (p_event == LoadControllerI.SENDER_VAL_SUCCESS) {
                        if (transactionLoadVO.getCurrentSenderValidationCount() > 0)
                            transactionLoadVO.setCurrentSenderValidationCount(transactionLoadVO.getCurrentSenderValidationCount() - 1);
                        if (_log.isDebugEnabled())
                            _log.debug("decreaseTransactionLoad", "p_transactionID:" + p_transactionID + " p_event:" + p_event + "transactionLoadVO.getCurrentSenderValidationCount()" + transactionLoadVO.getCurrentSenderValidationCount());

                    } else if (p_event == LoadControllerI.SENDER_VAL_FAILED) {
                        if (transactionLoadVO.getCurrentSenderValidationCount() > 0)
                            transactionLoadVO.setCurrentSenderValidationCount(transactionLoadVO.getCurrentSenderValidationCount() - 1);
                        transactionLoadVO.setTotalSenderValFailCount(transactionLoadVO.getTotalSenderValFailCount() + 1);
                        if (_log.isDebugEnabled())
                            _log.debug("decreaseTransactionLoad", "p_transactionID:" + p_transactionID + " p_event:" + p_event + "transactionLoadVO.getCurrentSenderValidationCount()" + transactionLoadVO.getCurrentSenderValidationCount());
                    } else if (p_event == LoadControllerI.SENDER_TOP_SUCCESS) {
                        if (transactionLoadVO.getCurrentSenderTopupCount() > 0)
                            transactionLoadVO.setCurrentSenderTopupCount(transactionLoadVO.getCurrentSenderTopupCount() - 1);
                        if (_log.isDebugEnabled())
                            _log.debug("decreaseTransactionLoad", "p_transactionID:" + p_transactionID + " p_event:" + p_event + "transactionLoadVO.getCurrentSenderTopupCount()" + transactionLoadVO.getCurrentSenderTopupCount());
                    } else if (p_event == LoadControllerI.SENDER_TOP_FAILED) {
                        if (transactionLoadVO.getCurrentSenderTopupCount() > 0)
                            transactionLoadVO.setCurrentSenderTopupCount(transactionLoadVO.getCurrentSenderTopupCount() - 1);
                        transactionLoadVO.setTotalSenderTopupFailCount(transactionLoadVO.getTotalSenderTopupFailCount() + 1);
                        if (_log.isDebugEnabled())
                            _log.debug("decreaseTransactionLoad", "p_transactionID:" + p_transactionID + " p_event:" + p_event + "transactionLoadVO.getCurrentSenderTopupCount()" + transactionLoadVO.getCurrentSenderTopupCount());
                    } else if (p_event == LoadControllerI.RECEIVER_VAL_SUCCESS) {
                        interfaceService = miniTransVO.getReciverInterfaceService();
                        if (_log.isDebugEnabled())
                            _log.debug("decreaseTransactionLoad", "p_transactionID:" + p_transactionID + " RECEIVER_VAL_SUCCESS interfaceService=" + interfaceService);
                        transactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(interfaceService);

                        if (transactionLoadVO.getCurrentRecieverValidationCount() > 0)
                            transactionLoadVO.setCurrentRecieverValidationCount(transactionLoadVO.getCurrentRecieverValidationCount() - 1);
                        if (_log.isDebugEnabled())
                            _log.debug("decreaseTransactionLoad", "p_transactionID:" + p_transactionID + " p_event:" + p_event + "transactionLoadVO.getCurrentRecieverValidationCount()" + transactionLoadVO.getCurrentRecieverValidationCount());
                    } else if (p_event == LoadControllerI.RECEIVER_VAL_FAILED) {
                        interfaceService = miniTransVO.getReciverInterfaceService();
                        if (_log.isDebugEnabled())
                            _log.debug("decreaseTransactionLoad", "p_transactionID:" + p_transactionID + " RECEIVER_VAL_FAILED interfaceService=" + interfaceService);
                        transactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(interfaceService);

                        if (transactionLoadVO.getCurrentRecieverValidationCount() > 0)
                            transactionLoadVO.setCurrentRecieverValidationCount(transactionLoadVO.getCurrentRecieverValidationCount() - 1);
                        transactionLoadVO.setTotalRecieverValFailCount(transactionLoadVO.getTotalRecieverValFailCount() + 1);
                        if (_log.isDebugEnabled())
                            _log.debug("decreaseTransactionLoad", "p_transactionID:" + p_transactionID + " p_event:" + p_event + "transactionLoadVO.getCurrentRecieverValidationCount()" + transactionLoadVO.getCurrentRecieverValidationCount());
                    } else if (p_event == LoadControllerI.RECEIVER_TOP_SUCCESS) {
                        interfaceService = miniTransVO.getReciverInterfaceService();
                        if (_log.isDebugEnabled())
                            _log.debug("decreaseTransactionLoad", "p_transactionID:" + p_transactionID + " RECEIVER_TOP_SUCCESS interfaceService=" + interfaceService);
                        transactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(interfaceService);

                        if (transactionLoadVO.getCurrentRecieverTopupCount() > 0)
                            transactionLoadVO.setCurrentRecieverTopupCount(transactionLoadVO.getCurrentRecieverTopupCount() - 1);
                        if (_log.isDebugEnabled())
                            _log.debug("decreaseTransactionLoad", "p_transactionID:" + p_transactionID + " p_event:" + p_event + "transactionLoadVO.getCurrentRecieverTopupCount()" + transactionLoadVO.getCurrentRecieverTopupCount());

                    } else if (p_event == LoadControllerI.RECEIVER_TOP_FAILED) {
                        interfaceService = miniTransVO.getReciverInterfaceService();
                        if (_log.isDebugEnabled())
                            _log.debug("decreaseTransactionLoad", "p_transactionID:" + p_transactionID + " RECEIVER_TOP_FAILED interfaceService=" + interfaceService);
                        transactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(interfaceService);

                        if (transactionLoadVO.getCurrentRecieverTopupCount() > 0)
                            transactionLoadVO.setCurrentRecieverTopupCount(transactionLoadVO.getCurrentRecieverTopupCount() - 1);
                        transactionLoadVO.setTotalRecieverTopupFailCount(transactionLoadVO.getTotalRecieverTopupFailCount() + 1);
                        if (_log.isDebugEnabled())
                            _log.debug("decreaseTransactionLoad", "p_transactionID:" + p_transactionID + " p_event:" + p_event + "transactionLoadVO.getCurrentRecieverTopupCount()" + transactionLoadVO.getCurrentRecieverTopupCount());
                    }
                } finally {
                    lock.unlock();
                }
            }
            _log.info("decreaseTransactionLoad", "p_transactionID=" + p_transactionID + " transactionLoadVO=" + transactionLoadVO);
        } catch (Exception e) {
            _log.errorTrace("decreaseTransactionLoad: Exception print stack trace:=", e);
            _log.error("decreaseTransactionLoad", "Getting exception =" + e.getMessage());
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
        if (_log.isDebugEnabled())
            _log.debug("decreaseResponseCounters", " Entered with p_transactionID=" + p_transactionID + " p_transactionStatus=" + p_transactionStatus + " p_event=" + p_event);
        boolean isResponseSuccess = LoadControllerUtil.isResponseSuccess(p_transactionStatus);
        // ------------------------------------------------------------------------------------------------------
        lock.lock();
        try {
            if (p_event == LoadControllerI.SENDER_VAL_RESPONSE) {
                if (isResponseSuccess)
                    decreaseTransactionLoad(LoadControllerI.SENDER_VAL_SUCCESS, p_transactionID);
                else
                    decreaseTransactionLoad(LoadControllerI.SENDER_VAL_FAILED, p_transactionID);
            } else if (p_event == LoadControllerI.RECEIVER_VAL_RESPONSE) {
                if (isResponseSuccess)
                    decreaseTransactionLoad(LoadControllerI.RECEIVER_VAL_SUCCESS, p_transactionID);
                else
                    decreaseTransactionLoad(LoadControllerI.RECEIVER_VAL_FAILED, p_transactionID);
            } else if (p_event == LoadControllerI.SENDER_TOP_RESPONSE) {
                if (isResponseSuccess)
                    decreaseTransactionLoad(LoadControllerI.SENDER_TOP_SUCCESS, p_transactionID);
                else
                    decreaseTransactionLoad(LoadControllerI.SENDER_TOP_FAILED, p_transactionID);
            } else if (p_event == LoadControllerI.RECEIVER_TOP_RESPONSE) {
                if (isResponseSuccess)
                    decreaseTransactionLoad(LoadControllerI.RECEIVER_TOP_SUCCESS, p_transactionID);
                else
                    decreaseTransactionLoad(LoadControllerI.RECEIVER_TOP_FAILED, p_transactionID);
            }
        } finally {
            lock.unlock();
        }
        if (_log.isDebugEnabled())
            _log.debug("decreaseResponseCounters", " Exiting for p_transactionID=" + p_transactionID);
    }

    /**
     * Method to decrease the Transaction and Interface Load Only
     * 
     * @param p_event
     * @param p_transactionID
     */
    public void decreaseTransactionInterfaceLoad(int p_event, String p_transactionID) {
        _log.info("decreaseTransactionInterfaceLoad", "Entered p_transactionID:" + p_transactionID + " p_event:" + p_event);
        try {
            HashMap allTransMap = LoadControllerCache.getAllTransactionLoadListMap();
            MiniTransVO miniTransVO = (MiniTransVO) allTransMap.get(p_transactionID);
            String interfaceService = miniTransVO.getSenderInterfaceService();
            boolean isSenderOverflow = miniTransVO.isOverflow();
            String originalService = miniTransVO.getSenderOriginalService();
            TransactionLoadVO transactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(interfaceService);
            String loadType = LoadControllerCache.getLoadType();
            if (_log.isDebugEnabled())
                _log.debug("decreaseTransactionInterfaceLoad", " p_transactionID=" + p_transactionID + "loadType=" + loadType);

            long transactionStartTime = ((Long) transactionLoadVO.getTransactionListMap().get(p_transactionID)).longValue();
            long currentTime = System.currentTimeMillis();

            if (_log.isDebugEnabled())
                _log.debug("decreaseTransactionInterfaceLoad", " p_transactionID=" + p_transactionID + "transactionLoadVO=" + transactionLoadVO);

            if (transactionLoadVO != null) {
                // ------------------------------------------------------------------------------------------------------
                lock.lock();
                try {
                    if (p_event == LoadControllerI.DEC_LAST_TRANS_COUNT) {
                        transactionLoadVO.getTransactionListMap().remove(p_transactionID);

                        InterfaceLoadController interfaceLoadController = LoadControllerUtil.getInterfaceLoadObject(transactionLoadVO.getInstanceID(), transactionLoadVO.getNetworkCode(), transactionLoadVO.getInterfaceID());

                        // Processing for Transaction count
                        if (LoadControllerI.LOAD_CONTROLLER_TXN_TYPE.equalsIgnoreCase(loadType)) {
                            if (transactionLoadVO.getCurrentTransactionLoad() > 0)
                                transactionLoadVO.setCurrentTransactionLoad(transactionLoadVO.getCurrentTransactionLoad() - 1);

                            if (isSenderOverflow) {
                                TransactionLoadVO origTransactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(originalService);
                                if (origTransactionLoadVO.getOverFlowCount() > 0)
                                    origTransactionLoadVO.setOverFlowCount(origTransactionLoadVO.getOverFlowCount() - 1);
                                if (_log.isDebugEnabled())
                                    _log.debug("decreaseTransactionInterfaceLoad", "p_transactionID=" + p_transactionID + " isSenderOverflow =" + isSenderOverflow + " Decreasing Count to =" + origTransactionLoadVO.getOverFlowCount());
                            }
                            interfaceLoadController.decreaseCurrentInterfaceLoad(LoadControllerI.DEC_LAST_TRANS_COUNT, transactionLoadVO.getNetworkCode(), transactionLoadVO.getInterfaceID(), p_transactionID);
                        } else // For TPS Control also
                        {
                            if (transactionLoadVO.getCurrentTransactionLoad() > 0)
                                transactionLoadVO.setCurrentTransactionLoad(transactionLoadVO.getCurrentTransactionLoad() - 1);
                            if (isSenderOverflow) {
                                TransactionLoadVO origTransactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(originalService);
                                if (origTransactionLoadVO.getOverFlowCount() > 0)
                                    origTransactionLoadVO.setOverFlowCount(origTransactionLoadVO.getOverFlowCount() - 1);
                                if (_log.isDebugEnabled())
                                    _log.debug("decreaseTransactionInterfaceLoad", "p_transactionID=" + p_transactionID + " isSenderOverflow =" + isSenderOverflow + " Decreasing Count to =" + origTransactionLoadVO.getOverFlowCount());
                            }
                            interfaceLoadController.decreaseCurrentInterfaceLoad(LoadControllerI.DEC_LAST_TRANS_COUNT, transactionLoadVO.getNetworkCode(), transactionLoadVO.getInterfaceID(), p_transactionID);

                        }
                        // Set average time for the instance+network+service
                        transactionLoadVO.setAverageServiceTime((transactionLoadVO.getAverageServiceTime() + (currentTime - transactionStartTime)) / 2);

                        String recieverService = miniTransVO.getReciverInterfaceService();
                        if (recieverService != null && !BTSLUtil.isNullString(recieverService)) {

                            boolean isRecieverOverflow = miniTransVO.isRecieverOverflow();
                            String originalRecieverService = miniTransVO.getReciverOriginalService();

                            TransactionLoadVO receiverTransVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(recieverService);
                            interfaceLoadController = LoadControllerUtil.getInterfaceLoadObject(receiverTransVO.getInstanceID(), receiverTransVO.getNetworkCode(), receiverTransVO.getInterfaceID());
                            if (_log.isDebugEnabled())
                                _log.debug("decreaseTransactionInterfaceLoad", " p_transactionID=" + p_transactionID + " isRecieverOverflow=" + isRecieverOverflow + " originalRecieverService=" + originalRecieverService + " recieverService=" + recieverService + "interfaceLoadController=" + interfaceLoadController + " receiverTransVO.getCurrentTransactionLoad()=" + receiverTransVO.getCurrentTransactionLoad());

                            if (receiverTransVO.getCurrentTransactionLoad() > 0)
                                receiverTransVO.setCurrentTransactionLoad(receiverTransVO.getCurrentTransactionLoad() - 1);
                            interfaceLoadController.decreaseCurrentInterfaceLoad(LoadControllerI.DEC_LAST_TRANS_COUNT, receiverTransVO.getNetworkCode(), receiverTransVO.getInterfaceID(), p_transactionID);

                            if (isRecieverOverflow) {
                                TransactionLoadVO origReceiverTransactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(originalRecieverService);
                                if (origReceiverTransactionLoadVO.getOverFlowCount() > 0)
                                    origReceiverTransactionLoadVO.setOverFlowCount(origReceiverTransactionLoadVO.getOverFlowCount() - 1);
                                if (_log.isDebugEnabled())
                                    _log.debug("decreaseTransactionInterfaceLoad", "p_transactionID=" + p_transactionID + " isRecieverOverflow =" + isRecieverOverflow + " Decreasing Count to =" + origReceiverTransactionLoadVO.getOverFlowCount());
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
            _log.info("decreaseTransactionInterfaceLoad", " p_transactionID=" + p_transactionID + "transactionLoadVO.getCurrentTransactionLoad()=" + transactionLoadVO.getCurrentTransactionLoad());
        } catch (Exception e) {
            _log.errorTrace("decreaseTransactionInterfaceLoad: Exception print stack trace:=", e);
            _log.error("decreaseTransactionInterfaceLoad", "Getting exception =" + e.getMessage());
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
        if (_log.isDebugEnabled())
            _log.debug("decreaseReceiverTransactionInterfaceLoad", "Entered p_transactionID:" + p_transactionID + " p_event:" + p_event);

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
                    if (_log.isDebugEnabled())
                        _log.debug("decreaseReceiverTransactionInterfaceLoad", " p_transactionID=" + p_transactionID + " recieverService=" + recieverService + " originalRecieverService=" + originalRecieverService + " interfaceLoadController=" + interfaceLoadController + " receiverTransVO.getCurrentTransactionLoad()=" + receiverTransVO.getCurrentTransactionLoad());

                    if (receiverTransVO.getCurrentTransactionLoad() > 0)
                        receiverTransVO.setCurrentTransactionLoad(receiverTransVO.getCurrentTransactionLoad() - 1);
                    interfaceLoadController.decreaseCurrentInterfaceLoad(LoadControllerI.DEC_LAST_TRANS_COUNT, receiverTransVO.getNetworkCode(), receiverTransVO.getInterfaceID(), p_transactionID);

                    if (isRecieverOverflow) {
                        TransactionLoadVO origReceiverTransactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(originalRecieverService);
                        if (origReceiverTransactionLoadVO.getOverFlowCount() > 0)
                            origReceiverTransactionLoadVO.setOverFlowCount(origReceiverTransactionLoadVO.getOverFlowCount() - 1);
                        if (_log.isDebugEnabled())
                            _log.debug("decreaseReceiverTransactionInterfaceLoad", "p_transactionID=" + p_transactionID + " isRecieverOverflow =" + isRecieverOverflow + " Decreasing Count to =" + origReceiverTransactionLoadVO.getOverFlowCount());
                    }

                    // Set average time for the instance+network+service
                    receiverTransVO.setAverageServiceTime((receiverTransVO.getAverageServiceTime() + (currentTime - transactionStartTime)) / 2);
                }
            } finally {
                lock.unlock();
            }
        } catch (Exception e) {
            _log.errorTrace("decreaseReceiverTransactionInterfaceLoad: Exception print stack trace:=", e);
            _log.error("decreaseReceiverTransactionInterfaceLoad", "Getting exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "TransactionLoadController[decreaseReceiverTransactionInterfaceLoad]", "", "", "", "Exception:" + e.getMessage());
        }
        _log.info("decreaseReceiverTransactionInterfaceLoad", " p_transactionID=" + p_transactionID + "Exiting ");
    }
}
