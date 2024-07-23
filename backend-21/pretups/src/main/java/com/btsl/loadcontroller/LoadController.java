package com.btsl.loadcontroller;

import java.util.ArrayList;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.util.BTSLUtil;

/*
 * LoadController.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 22/06/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Main class for controlling the various load from which a request will pass
 * through
 */

public class LoadController {

    private static Log _log = LogFactory.getLog(LoadController.class.getName());

    /**
	 * ensures no instantiation
	 */
    private LoadController(){
    	
    }
    
    /**
     * Method to check the Instance Load, will delegate to that instance object
     * to handle
     * 
     * @param p_requestID
     * @param p_event
     * @throws BTSLBaseException
     */
    public static void checkInstanceLoad(long p_requestID, int p_event) throws BTSLBaseException {
    	StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.append("Entered p_requestID:");
        	loggerValue.append(p_requestID);
			loggerValue.append(" p_event=");
        	loggerValue.append(p_event);
            _log.debug("checkInstanceLoad", loggerValue);
        }
        final String METHOD_NAME = "checkInstanceLoad";
        try {
            String instanceID = LoadControllerCache.getInstanceID();
            if (BTSLUtil.isNullString(instanceID)) {
                instanceID = LoadControllerI.DEFAULT_INSTANCE_ID;
            }
            if (LoadControllerCache.getInstanceLoadHash() == null || LoadControllerCache.getInstanceLoadHash().isEmpty()) {
                LoadControllerCache.refreshInstanceLoad(instanceID);
            }

            InstanceLoadController instanceLoadController = LoadControllerUtil.getInstanceLoadObject(instanceID);
            boolean isReqThrough = instanceLoadController.checkInstanceLoad(instanceID, p_event);

            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("p_requestID:");
            	loggerValue.append(p_requestID);
				loggerValue.append(" isReqThrough=");
            	loggerValue.append(isReqThrough);
                _log.debug("checkInstanceLoad",loggerValue);
            }
            if (!isReqThrough) {
                EventHandler.handle(EventIDI.LOCATION_RECHARGESYSTEM_OVERLOADED, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadController[checkInstanceLoad]", "", "", "", "Refusing Request :" + PretupsErrorCodesI.REQUEST_REFUSE);
                throw new BTSLBaseException("LoadController", "checkInstanceLoad", PretupsErrorCodesI.REQUEST_REFUSE);
            }
        } catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.LOCATION_RECHARGESYSTEM_OVERLOADED, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadController[checkInstanceLoad]", "", "", "", "Exception:" + PretupsErrorCodesI.REQUEST_REFUSE);
            throw new BTSLBaseException("LoadController", "checkInstanceLoad", PretupsErrorCodesI.REQUEST_REFUSE);
        }
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exiting for p_requestID:");
        	loggerValue.append(p_requestID);
            _log.debug("checkInstanceLoad",loggerValue);
        }
    }

    /**
     * Method to check the network load, will delegate to that network object to
     * handle
     * 
     * @param p_requestID
     * @param p_networkID
     * @param p_event
     * @throws BTSLBaseException
     */
    public static void checkNetworkLoad(long p_requestID, String p_networkID, int p_event) throws BTSLBaseException {
    	StringBuilder loggerValue= new StringBuilder(); 
		
        if (_log.isDebugEnabled()) {
        	loggerValue.append("Entered p_requestID:");
        	loggerValue.append(p_requestID);
    		loggerValue.append(" p_networkID=");
        	loggerValue.append(p_networkID);
        	loggerValue.append(" p_event=");
        	loggerValue.append(p_event);
            _log.debug("checkNetworkLoad",loggerValue);
        }
        final String METHOD_NAME = "checkNetworkLoad";
        try {
            String instanceID = LoadControllerCache.getInstanceID();
            if (BTSLUtil.isNullString(instanceID)) {
                instanceID = LoadControllerI.DEFAULT_INSTANCE_ID;
            }
            if (LoadControllerCache.getNetworkLoadHash() == null || LoadControllerCache.getNetworkLoadHash().isEmpty()) {
                LoadControllerCache.refreshNetworkLoad();
            }

            NetworkLoadController networkLoadController = LoadControllerUtil.getNetworkLoadObject(instanceID, p_networkID);
            boolean isReqThrough = networkLoadController.checkNetworkLoad(p_networkID);

            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("p_requestID:");
            	loggerValue.append(p_requestID);
        		loggerValue.append(" isReqThrough=");
            	loggerValue.append(isReqThrough);
                _log.debug("checkNetworkLoad", loggerValue);
            }
            if (!isReqThrough) {
                EventHandler.handle(EventIDI.MAXLOCATION_LOAD_REACHED, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadController[checkNetworkLoad]", "", "", "", "Refusing Request :" + PretupsErrorCodesI.REQUEST_REFUSE_FROM_NWLOAD);
                throw new BTSLBaseException("LoadController", "checkNetworkLoad", PretupsErrorCodesI.REQUEST_REFUSE_FROM_NWLOAD);
            }
        } catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.MAXLOCATION_LOAD_REACHED, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadController[checkNetworkLoad]", "", "", "", "Exception:" + PretupsErrorCodesI.REQUEST_REFUSE_FROM_NWLOAD);
            throw new BTSLBaseException("LoadController", "checkNetworkLoad", PretupsErrorCodesI.REQUEST_REFUSE_FROM_NWLOAD);
        }
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exiting for p_requestID:");
        	loggerValue.append(p_requestID);
            _log.debug("checkNetworkLoad",loggerValue);
        }
    }

    /**
     * Method to check interface details, will delegate to that interface object
     * to handle
     * 
     * @param p_networkID
     * @param p_interfaceID
     * @param p_transactionID
     * @param p_transferVO
     * @param p_checkQueueAddition
     * @return int
     */
    public static int checkInterfaceLoad(String p_networkID, String p_interfaceID, String p_transactionID, TransferVO p_transferVO, boolean p_checkQueueAddition) {
    	StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.append("Entered p_networkID:");
        	loggerValue.append(p_networkID);
			loggerValue.append(" p_interfaceID=");
        	loggerValue.append(p_interfaceID);
        	loggerValue.append(" p_transactionID=");
        	loggerValue.append(p_transactionID);
			loggerValue.append(" p_checkQueueAddition=");
        	loggerValue.append(p_checkQueueAddition);
            _log.debug("checkInterfaceLoad",loggerValue);
        }
        int requestCode = 2;
        final String METHOD_NAME = "checkInterfaceLoad";
        try {
            String instanceID = LoadControllerCache.getInstanceID();
            if (BTSLUtil.isNullString(instanceID)) {
                instanceID = LoadControllerI.DEFAULT_INSTANCE_ID;
            }
            if (LoadControllerCache.getInterfaceLoadHash() == null || LoadControllerCache.getInterfaceLoadHash().isEmpty()) {
                LoadControllerCache.refreshInterfaceLoad();
            }

            InterfaceLoadController interfaceLoadController = LoadControllerUtil.getInterfaceLoadObject(instanceID, p_networkID, p_interfaceID);
            requestCode = interfaceLoadController.checkInterfaceLoad(p_networkID, p_interfaceID, p_transactionID, p_transferVO, p_checkQueueAddition);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadController[checkInterfaceLoad]", "", "", "", "Refusing Request Exception:" + e.getMessage());
            requestCode = 2;
        }
        if (_log.isDebugEnabled()) {
			loggerValue.setLength(0);
        	loggerValue.append("Exiting for p_transactionID:");
        	loggerValue.append(p_transactionID);
			loggerValue.append(" isReqThrough (0=Through, 1=Queue, 2=Refused) =");
        	loggerValue.append(requestCode);
            _log.debug("checkInterfaceLoad", loggerValue);
        }
        return requestCode;
    }

    /**
     * Method to check the transaction load, will delegate to that transaction
     * object to handle
     * 
     * @param p_networkID
     * @param p_interfaceID
     * @param p_serviceType
     * @param p_transactionID
     * @param p_checkAlternateService
     * @param p_userType
     * @throws BTSLBaseException
     */
    public static void checkTransactionLoad(String p_networkID, String p_interfaceID, String p_serviceType, String p_transactionID, boolean p_checkAlternateService, String p_userType) throws BTSLBaseException {
    	StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
			loggerValue.setLength(0);
        	loggerValue.append("Entered p_transactionID:");
        	loggerValue.append(p_transactionID);
			loggerValue.append(" p_networkID=");
        	loggerValue.append(p_networkID);
			loggerValue.append(" p_interfaceID=");
        	loggerValue.append(p_interfaceID);
			loggerValue.append(" p_serviceType=");
        	loggerValue.append(p_serviceType);
			loggerValue.append(" p_userType=");
        	loggerValue.append(p_userType);
			loggerValue.append(" p_checkAlternateService=");
        	loggerValue.append(p_checkAlternateService);
            _log.debug("checkTransactionLoad", loggerValue);
        }
        final String METHOD_NAME = "checkTransactionLoad";
        try {
            String instanceID = LoadControllerCache.getInstanceID();
            if (BTSLUtil.isNullString(instanceID)) {
                instanceID = LoadControllerI.DEFAULT_INSTANCE_ID;
            }
            if (LoadControllerCache.getTransactionLoadHash() == null || LoadControllerCache.getTransactionLoadHash().isEmpty()) {
                LoadControllerCache.refreshTransactionLoad();
            }

            TransactionLoadController transactionLoadController = LoadControllerUtil.getTransactionLoadObject(instanceID, p_networkID, p_interfaceID, p_serviceType);
            boolean isReqThrough = transactionLoadController.checkTransactionLoad(p_networkID, p_interfaceID, p_serviceType, p_serviceType, p_transactionID, p_checkAlternateService, p_userType);

            if (_log.isDebugEnabled()) {
				loggerValue.setLength(0);
            	loggerValue.append("p_transactionID:");
            	loggerValue.append(p_transactionID);
				loggerValue.append(" isReqThrough=");
            	loggerValue.append(isReqThrough);
                _log.debug("checkTransactionLoad",loggerValue);
            }
            if (!isReqThrough) {
                // Decreasing interface load which we had incremented before
                // 27/09/06
                InterfaceLoadController interfaceLoadController = LoadControllerUtil.getInterfaceLoadObject(instanceID, p_networkID, p_interfaceID);
                interfaceLoadController.decreaseCurrentInterfaceLoad(LoadControllerI.DEC_LAST_TRANS_COUNT, p_networkID, p_interfaceID, p_transactionID);

                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadController[checkTransactionLoad]", "", "", "", "Refusing Request :" + PretupsErrorCodesI.REQUEST_REFUSE_FROM_TXNLOAD);
                throw new BTSLBaseException("LoadController", "checkTransactionLoad", PretupsErrorCodesI.REQUEST_REFUSE_FROM_TXNLOAD);
            }
        } catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        } catch (Exception e) {
            // Decreasing interface load which we had incremented before
            // 27/09/06
            InterfaceLoadController interfaceLoadController = LoadControllerUtil.getInterfaceLoadObject(LoadControllerCache.getInstanceID(), p_networkID, p_interfaceID);
            interfaceLoadController.decreaseCurrentInterfaceLoad(LoadControllerI.DEC_LAST_TRANS_COUNT, p_networkID, p_interfaceID, p_transactionID);

            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadController[checkTransactionLoad]", "", "", "", "Exception:" + e.getMessage() + " Refusing Request ");
            throw new BTSLBaseException("LoadController", "checkTransactionLoad", PretupsErrorCodesI.REQUEST_REFUSE_FROM_TXNLOAD);
        }
        if (_log.isDebugEnabled()) {
			loggerValue.setLength(0);
        	loggerValue.append("Exiting for p_transactionID=");
        	loggerValue.append(p_transactionID);
            _log.debug("checkTransactionLoad",loggerValue);
        }
    }

    /**
     * Method to decrease the response counters
     * 
     * @param p_transactionID
     * @param p_transactionStatus
     * @param p_event
     */
    public static void decreaseResponseCounters(String p_transactionID, String p_transactionStatus, int p_event) {
        final String METHOD_NAME = "decreaseResponseCounters";
        StringBuilder loggerValue= new StringBuilder();
        try {
            if (_log.isDebugEnabled()) {
            	loggerValue.append("Entered p_transactionID:");
            	loggerValue.append(p_transactionID);
				loggerValue.append(" p_transactionStatus=");
            	loggerValue.append(p_transactionStatus);
				loggerValue.append(" p_event=");
            	loggerValue.append(p_event);
                _log.debug("decreaseResponseCounters",loggerValue);
            }
            HashMap allTransMap = LoadControllerCache.getAllTransactionLoadListMap();
            MiniTransVO miniTransVO = (MiniTransVO) allTransMap.get(p_transactionID);
            String interfaceService = miniTransVO.getSenderInterfaceService();
            TransactionLoadController transactionLoadController = (TransactionLoadController) LoadControllerUtil.getTransactionLoadObjectByKey(interfaceService);
            transactionLoadController.decreaseResponseCounters(p_transactionID, p_transactionStatus, p_event);
            if (_log.isDebugEnabled()) {
				loggerValue.setLength(0);
            	loggerValue.append("Exiting for p_transactionID=" );
            	loggerValue.append(p_transactionID);
                _log.debug("decreaseResponseCounters",loggerValue);
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadController[decreaseResponseCounters]", "", "", "", "Exception:" + e.getMessage() + " Refusing Request ");
        }
    }

    /**
     * Method to handle intermediary counters
     * 
     * @param p_transactionID
     * @param p_event
     */
    public static void incrementTransactionInterCounts(String p_transactionID, int p_event) {
        final String METHOD_NAME = "incrementTransactionInterCounts";
        try {
            if (_log.isDebugEnabled()) {
                _log.debug("incrementTransactionInterCounts", "Entered p_transactionID:" + p_transactionID + " p_event=" + p_event);
            }
            HashMap allTransMap = LoadControllerCache.getAllTransactionLoadListMap();
            MiniTransVO miniTransVO = (MiniTransVO) allTransMap.get(p_transactionID);
            TransactionLoadController transactionLoadController = (TransactionLoadController) LoadControllerUtil.getTransactionLoadObjectByKey(miniTransVO.getSenderInterfaceService());
            transactionLoadController.incrementTransactionInterCounts(p_transactionID, p_event);
            if (_log.isDebugEnabled()) {
                _log.debug("incrementTransactionInterCounts", "Exiting for p_transactionID=" + p_transactionID);
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadController[incrementTransactionInterCounts]", "", "", "", "Exception:" + e.getMessage());
        }
    }

    /**
     * Method to decrease the network load, will delegate to the network object
     * to handle
     * 
     * @param p_requestID
     * @param p_networkID
     * @param p_event
     */
    public static void decreaseCurrentNetworkLoad(long p_requestID, String p_networkID, int p_event) {
        final String METHOD_NAME = "decreaseCurrentNetworkLoad";
        StringBuilder loggerValue= new StringBuilder(); 
        try {
            if (_log.isDebugEnabled()) {
            	loggerValue.append("Entered with p_requestID=");
            	loggerValue.append(p_requestID);
				loggerValue.append(" p_networkID=");
            	loggerValue.append(p_networkID);
				loggerValue.append(" p_event=");
            	loggerValue.append(p_event);
                _log.debug("decreaseCurrentNetworkLoad",loggerValue);
            }
            String instanceID = LoadControllerCache.getInstanceID();
            if (BTSLUtil.isNullString(instanceID)) {
                instanceID = LoadControllerI.DEFAULT_INSTANCE_ID;
            }

            NetworkLoadController networkLoadController = LoadControllerUtil.getNetworkLoadObject(instanceID, p_networkID);
            networkLoadController.decreaseCurrentNetworkLoad(p_networkID, p_event);
            if (_log.isDebugEnabled()) {
				loggerValue.setLength(0);
            	loggerValue.append("Exiting for p_requestID=");
            	loggerValue.append(p_requestID);
                _log.debug("decreaseCurrentNetworkLoad",loggerValue);
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadController[decreaseCurrentNetworkLoad]", "", "", "", "Exception:" + e.getMessage() + " Refusing Request ");
        }
    }

    /**
     * Method to decrease the instance load, will delegate to the instance
     * object to handle
     * 
     * @param p_requestID
     * @param p_event
     */
    public static void decreaseCurrentInstanceLoad(long p_requestID, int p_event) {
        final String METHOD_NAME = "decreaseCurrentInstanceLoad";
        StringBuilder loggerValue= new StringBuilder(); 
        try {
            if (_log.isDebugEnabled()) {
            	loggerValue.append("Entered with p_requestID=");
            	loggerValue.append(p_requestID);
				loggerValue.append(" p_event=");
            	loggerValue.append(p_event);
                _log.debug("decreaseCurrentInstanceLoad",loggerValue);
            }
            String instanceID = LoadControllerCache.getInstanceID();
            if (BTSLUtil.isNullString(instanceID)) {
                instanceID = LoadControllerI.DEFAULT_INSTANCE_ID;
            }
            InstanceLoadController instanceLoadController = LoadControllerUtil.getInstanceLoadObject(instanceID);
            instanceLoadController.decreaseCurrentInstanceLoad(instanceID, p_event);
            if (_log.isDebugEnabled()) {
				loggerValue.setLength(0);
            	loggerValue.append("Exiting for p_requestID=");
            	loggerValue.append(p_requestID);
                _log.debug("decreaseCurrentInstanceLoad",loggerValue);
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadController[decreaseCurrentInstanceLoad]", "", "", "", "Exception:" + e.getMessage() + " Refusing Request ");
        }
    }

    public static void main(String args[]) {
        final String METHOD_NAME = "main";
        ArrayList list = new ArrayList();
        list.add("1");
        long queueInTime = System.currentTimeMillis();
        long queTimeOut = 30 * 1000;
        int count = 0;
        while (true) {
            if (((System.currentTimeMillis() - queueInTime) > queTimeOut) || count > 25) {
                break;
            }
            try {
                Thread.sleep(0);
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            count++;
        }
    }

    /**
     * This method checks whether the request has enough time to pass the
     * request or not.
     * 
     * @param p_transactionID
     * @param p_thresholdTime
     * @return boolean
     */
    public static boolean checkServiceTimeAvailable(String p_transactionID, long p_thresholdTime) {
    	StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.append("Entered with p_transactionID=");
        	loggerValue.append(p_transactionID);
    		loggerValue.append(" p_thresholdTime=");
        	loggerValue.append(p_thresholdTime);
            _log.debug("checkServiceTimeAvailable",loggerValue);
        }

        boolean timeAvailable = true;
        HashMap allTransMap = LoadControllerCache.getAllTransactionLoadListMap();
        String interfaceService = (String) allTransMap.get(p_transactionID);
        TransactionLoadVO transactionLoadVO = (TransactionLoadVO) LoadControllerCache.getTransactionLoadHash().get(interfaceService);

        long transactionStartTime = ((Long) transactionLoadVO.getTransactionListMap().get(p_transactionID)).longValue();
        long currentTime = System.currentTimeMillis();

        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("p_transactionID=");
        	loggerValue.append(p_transactionID);
    		loggerValue.append( " transactionStartTime=");
        	loggerValue.append(transactionStartTime);
        	loggerValue.append(" p_thresholdTime=");
        	loggerValue.append(p_thresholdTime);
    		loggerValue.append(" currentTime=");
        	loggerValue.append(currentTime);
            _log.debug("checkServiceTimeAvailable",loggerValue);
        }

        if (transactionStartTime + p_thresholdTime >= currentTime) {
            timeAvailable = false;
            TransactionLoadController transactionLoadController = LoadControllerUtil.getTransactionLoadObjectByKey(interfaceService);
            transactionLoadController.decreaseTransactionLoad(LoadControllerI.DEC_LAST_TRANS_COUNT, p_transactionID, transactionLoadVO.getNetworkCode());
        }
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("p_transactionID=");
        	loggerValue.append(p_transactionID);
    		loggerValue.append(" timeAvailable=");
        	loggerValue.append(timeAvailable);
            _log.debug("checkServiceTimeAvailable",loggerValue);
        }
        return timeAvailable;
    }

    /**
     * Method to decrease the transaction counters and all other related
     * counters
     * 
     * @param p_transactionID
     * @param p_event
     */
    public static void decreaseTransactionLoad(String p_transactionID, String p_networkID, int p_event) {
        final String METHOD_NAME = "decreaseTransactionLoad";
		StringBuilder loggerValue= new StringBuilder(); 
        try {
            if (_log.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append("Entered p_transactionID:");
            	loggerValue.append(p_transactionID);
        		loggerValue.append(" p_networkID=");
            	loggerValue.append(p_networkID);
        		loggerValue.append(" p_event=");
            	loggerValue.append(p_event);
                _log.debug("decreaseTransactionLoad",loggerValue);
            }
            HashMap allTransMap = LoadControllerCache.getAllTransactionLoadListMap();
            MiniTransVO miniTransVO = (MiniTransVO) allTransMap.get(p_transactionID);
            if (miniTransVO != null) {
                TransactionLoadController transactionLoadController = (TransactionLoadController) LoadControllerUtil.getTransactionLoadObjectByKey(miniTransVO.getSenderInterfaceService());
                transactionLoadController.decreaseTransactionLoad(p_event, p_transactionID, p_networkID);
            } else {
                NetworkLoadController networkLoadController = (NetworkLoadController) LoadControllerUtil.getNetworkLoadObject(LoadControllerCache.getInstanceID(), p_networkID);
                networkLoadController.decreaseCurrentNetworkLoad(p_networkID, p_event);
                InstanceLoadController instanceLoadController = (InstanceLoadController) LoadControllerUtil.getInstanceLoadObject(LoadControllerCache.getInstanceID());
                instanceLoadController.decreaseCurrentInstanceLoad(LoadControllerCache.getInstanceID(), p_event);
            }
            if (_log.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append("Exiting for p_transactionID=");
            	loggerValue.append(p_transactionID);
                _log.debug("decreaseTransactionLoad",loggerValue);
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadController[decreaseTransactionLoad]", "", "", "", "Exception:" + e.getMessage() + " Refusing Request ");
        }
    }

    /**
     * Method to decrease the Transaction and Interface Load Only
     * 
     * @param p_transactionID
     * @param p_networkID
     * @param p_event
     */
    public static void decreaseTransactionInterfaceLoad(String p_transactionID, String p_networkID, int p_event) {
        final String METHOD_NAME = "decreaseTransactionInterfaceLoad";
		StringBuilder loggerValue= new StringBuilder(); 
        try {
            if (_log.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append("Entered p_transactionID:");
            	loggerValue.append(p_transactionID);
        		loggerValue.append(" p_networkID=");
            	loggerValue.append(p_networkID);
        		loggerValue.append(" p_event=");
            	loggerValue.append(p_event);
                _log.debug("decreaseTransactionInterfaceLoad",loggerValue);
            }
            HashMap allTransMap = LoadControllerCache.getAllTransactionLoadListMap();
            MiniTransVO miniTransVO = (MiniTransVO) allTransMap.get(p_transactionID);
            if (miniTransVO != null) {
                TransactionLoadController transactionLoadController = (TransactionLoadController) LoadControllerUtil.getTransactionLoadObjectByKey(miniTransVO.getSenderInterfaceService());
                transactionLoadController.decreaseTransactionInterfaceLoad(p_event, p_transactionID);
            }
            if (_log.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append("Exiting for p_transactionID=");
            	loggerValue.append(p_transactionID);
                _log.debug("decreaseTransactionInterfaceLoad",loggerValue);
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadController[decreaseTransactionInterfaceLoad]", "", "", "", "Exception:" + e.getMessage() + " Refusing Request ");
        }
    }

    /**
     * Method to decrease the Transaction and Interface Load Only
     * 
     * @param p_transactionID
     * @param p_networkID
     * @param p_event
     */
    public static void decreaseReceiverTransactionInterfaceLoad(String p_transactionID, String p_networkID, int p_event) {
        final String METHOD_NAME = "decreaseReceiverTransactionInterfaceLoad";
		StringBuilder loggerValue= new StringBuilder(); 

        try {
            if (_log.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append("Entered p_transactionID:");
            	loggerValue.append(p_transactionID);
        		loggerValue.append(" p_networkID=");
            	loggerValue.append(p_networkID);
            	loggerValue.append(" p_event=");
            	loggerValue.append(p_event);
                _log.debug("decreaseReceiverTransactionInterfaceLoad",loggerValue);
            }
            HashMap allTransMap = LoadControllerCache.getAllTransactionLoadListMap();
            MiniTransVO miniTransVO = (MiniTransVO) allTransMap.get(p_transactionID);
            if (miniTransVO != null) {
                TransactionLoadController transactionLoadController = (TransactionLoadController) LoadControllerUtil.getTransactionLoadObjectByKey(miniTransVO.getReciverInterfaceService());
                transactionLoadController.decreaseReceiverTransactionInterfaceLoad(p_event, p_transactionID);
            }
            if (_log.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append("Exiting for p_transactionID=");
            	loggerValue.append(p_transactionID);
                _log.debug("decreaseReceiverTransactionInterfaceLoad",loggerValue);
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadController[decreaseReceiverTransactionInterfaceLoad]", "", "", "", "Exception:" + e.getMessage() + " Refusing Request ");
        }
    }

    /**
     * Method to decrease the Interface Load only
     * 
     * @param p_requestID
     * @param p_networkID
     * @param p_interfaceID
     * @param p_event
     */
    public static void decreaseCurrentInterfaceLoad(String p_requestID, String p_networkID, String p_interfaceID, int p_event) {
        final String METHOD_NAME = "decreaseCurrentInterfaceLoad";
        StringBuilder loggerValue= new StringBuilder();
        try {
            if (_log.isDebugEnabled()) {
				loggerValue.setLength(0);
            	loggerValue.append("Entered with p_requestID=");
            	loggerValue.append(p_requestID);
				loggerValue.append(" p_networkID=");
            	loggerValue.append(p_networkID);
				loggerValue.append(" p_interfaceID=");
            	loggerValue.append(p_interfaceID);
            	loggerValue.append(" p_event=");
				loggerValue.append(p_event);
                _log.debug("decreaseCurrentNetworkLoad",loggerValue);
            }
            String instanceID = LoadControllerCache.getInstanceID();
            if (BTSLUtil.isNullString(instanceID)) {
                instanceID = LoadControllerI.DEFAULT_INSTANCE_ID;
            }

            InterfaceLoadController interfaceLoadController = LoadControllerUtil.getInterfaceLoadObject(instanceID, p_networkID, p_interfaceID);
            interfaceLoadController.decreaseCurrentInterfaceLoad(LoadControllerI.DEC_LAST_TRANS_COUNT, p_networkID, p_interfaceID, p_requestID);
            if (_log.isDebugEnabled()) {
				loggerValue.setLength(0);
            	loggerValue.append("Exiting for p_requestID=");
            	loggerValue.append(p_requestID);
            	_log.debug("decreaseCurrentNetworkLoad",loggerValue);
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadController[decreaseCurrentInterfaceLoad]", "", "", "", "Exception:" + e.getMessage() + " Refusing Request ");
        }
    }

    /**
     * Method to check the system Load, will delegate to that instance object to
     * handle
     * 
     * @param p_requestID
     * @param p_event
     * @throws BTSLBaseException
     */
    public static boolean checkSystemLoad(long p_requestID, int p_event, String p_networkID, String p_queueForAll) throws BTSLBaseException {
    	StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_requestID:");
        	loggerValue.append(p_requestID);
    		loggerValue.append(" p_event=");
        	loggerValue.append(p_event);
        	loggerValue.append(" p_queueForAll=");
        	loggerValue.append(p_queueForAll);
            _log.debug("checkSystemLoad",loggerValue);
        }
        boolean isReqThrough = true;
        final String METHOD_NAME = "checkSystemLoad";
        try {
            String instanceID = LoadControllerCache.getInstanceID();
            if (BTSLUtil.isNullString(instanceID)) {
                instanceID = LoadControllerI.DEFAULT_INSTANCE_ID;
            }
            if (LoadControllerCache.getInstanceLoadHash() == null || LoadControllerCache.getInstanceLoadHash().isEmpty()) {
                LoadControllerCache.refreshInstanceLoad(instanceID);
            }

            InstanceLoadController instanceLoadController = LoadControllerUtil.getInstanceLoadObject(instanceID);
            isReqThrough = instanceLoadController.checkSystemInstanceLoad(instanceID, p_event, p_queueForAll);

            if (isReqThrough && (p_event == LoadControllerI.ENTRY_IN_QUEUE)) {
                NetworkLoadController networkLoadController = LoadControllerUtil.getNetworkLoadObject(instanceID, p_networkID);
                isReqThrough = networkLoadController.checkSystemNetworkLoad(p_networkID, p_queueForAll);
            }
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("p_requestID:");
            	loggerValue.append(p_requestID);
        		loggerValue.append(" isReqThrough=");
            	loggerValue.append(isReqThrough);
                _log.debug("checkSystemLoad",loggerValue);
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadController[checkSystemLoad]", "", "", "", "Exception:" + PretupsErrorCodesI.REQUEST_REFUSE);
            throw new BTSLBaseException("LoadController", "checkSystemLoad", PretupsErrorCodesI.REQUEST_REFUSE);
        }
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exiting for p_requestID:");
        	loggerValue.append(p_requestID);
            _log.debug("checkSystemLoad",loggerValue);
        }
        return isReqThrough;
    }

    /**
     * Method to get current instance counter value
     * 
     * @author gaurav.pandey
     * */
    public static long getCurrentInstanceCounter() throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("getCurrentInstanceCounter", "Entered");
        }
        long currentCounter = 0;
        final String METHOD_NAME = "getCurrentInstanceCounter";
        try {
            String instanceID = LoadControllerCache.getInstanceID();
            if (BTSLUtil.isNullString(instanceID)) {
                instanceID = LoadControllerI.DEFAULT_INSTANCE_ID;
            }
            if (LoadControllerCache.getInstanceLoadHash() == null || LoadControllerCache.getInstanceLoadHash().isEmpty()) {
                LoadControllerCache.refreshInstanceLoad(instanceID);
            }
            InstanceLoadController instanceLoadController = LoadControllerUtil.getInstanceLoadObject(instanceID);
            currentCounter = instanceLoadController.getCurrentAvailableInstanceCounter(instanceID);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadController[getCurrentInstanceCounter]", "", "", "", "Exception:" + PretupsErrorCodesI.REQUEST_REFUSE);
            throw new BTSLBaseException("LoadController", "getCurrentInstanceCounter", PretupsErrorCodesI.REQUEST_REFUSE);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("getCurrentInstanceCounter", "Exiting for currentCounter:" + currentCounter);
        }
        return currentCounter;

    }
}
