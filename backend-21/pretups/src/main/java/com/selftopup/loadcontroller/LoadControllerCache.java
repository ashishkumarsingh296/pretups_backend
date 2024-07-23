package com.selftopup.loadcontroller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.Constants;
import com.selftopup.util.OracleUtil;

/*
 * LoadControllerCache.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 22/06/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Class for updating the memory values at the startup
 */

public class LoadControllerCache {

    private static Hashtable _instanceLoadHash = new Hashtable();
    private static Hashtable _networkLoadHash = new Hashtable();
    private static Hashtable _interfaceLoadHash = new Hashtable();
    private static Hashtable _transactionLoadHash = new Hashtable();
    private static Hashtable _networkServiceLoadHash = new Hashtable();

    private static Hashtable _networkServiceHourlyLoadHash = new Hashtable();
    private static HashMap _allTransactionLoadListMap = new HashMap();
    // private static HashMap _allRecieverTransLoadListMap = new HashMap();
    private static Log _log = LogFactory.getLog(LoadControllerCache.class.getName());
    private static String _instanceID = null;
    private static String _loadType = null;

    private static HashMap _instanceLoadObjectMap = new HashMap();
    private static HashMap _networkLoadObjectMap = new HashMap();
    private static HashMap _interfaceLoadObjectMap = new HashMap();
    private static HashMap _transactionLoadObjectMap = new HashMap();

    // HashTable to load instance details with network
    private static Hashtable _instanceLoadForNetworkHash = new Hashtable();

    public static InstanceLoadVO getInstanceLoadForNetworkHash(String p_networkCode) {
        return (InstanceLoadVO) _instanceLoadForNetworkHash.get(p_networkCode);
    }

    public static String getInstanceID() {
        return _instanceID;
    }

    public static void setInstanceID(String instanceID) {
        _instanceID = instanceID;
    }

    public static String getLoadType() {
        return _loadType;
    }

    public static void setLoadType(String loadType) {
        _loadType = loadType;
    }

    public static Hashtable getInstanceLoadHash() {
        return _instanceLoadHash;
    }

    public static void setInstanceLoadHash(Hashtable instanceLoadHash) {
        _instanceLoadHash = instanceLoadHash;
    }

    public static Hashtable getNetworkLoadHash() {
        return _networkLoadHash;
    }

    public static void setNetworkLoadHash(Hashtable networkLoadHash) {
        _networkLoadHash = networkLoadHash;
    }

    public static void refreshInstanceLoad(String p_instanceID, boolean p_fileRead) // Pass
                                                                                    // from
                                                                                    // config
                                                                                    // servlets
    {
        if (_log.isDebugEnabled())
            _log.debug("refreshInstanceLoad", "Entered: p_instanceID: " + p_instanceID + " p_fileRead: " + p_fileRead);
        refreshInstanceLoad(p_instanceID);
        if (p_fileRead) {
            try {
                File file = new File(Constants.getProperty("FILE_PATH") + Constants.getProperty("INSTANCE_COUNTER_REPORT"));
                if (file.exists()) {
                    if (_log.isDebugEnabled())
                        _log.debug("refreshInstanceLoad", "File exists file absolute path: " + file.getAbsolutePath());
                    Hashtable instanceHashRead = read(file);
                    if (!(instanceHashRead == null || instanceHashRead.isEmpty())) {
                        modifyInstanceLoadVO(_instanceLoadHash, instanceHashRead);
                    }
                } else {
                    if (_log.isDebugEnabled())
                        _log.debug("refreshInstanceLoad", "File does not exist file absolute path: " + file.getAbsolutePath());
                }
            } catch (Exception e) {
                _log.errorTrace("refreshInstanceLoad: Exception print stack trace:=", e);
            }

        }
    }

    public static void refreshNetworkLoad(boolean p_fileRead) {
        if (_log.isDebugEnabled())
            _log.debug("refreshNetworkLoad", "Entered p_fileRead:" + p_fileRead);
        refreshNetworkLoad();
        if (p_fileRead) {
            try {
                File file = new File(Constants.getProperty("FILE_PATH") + Constants.getProperty("NETWORK_COUNTER_REPORT"));
                if (file.exists()) {
                    if (_log.isDebugEnabled())
                        _log.debug("refreshNetworkLoad", "File exists file absolute path: " + file.getAbsolutePath());
                    Hashtable networkHashRead = read(file);
                    if (!(networkHashRead == null || networkHashRead.isEmpty())) {
                        modifyNetworkLoadVO(_networkLoadHash, networkHashRead);
                    }
                } else {
                    if (_log.isDebugEnabled())
                        _log.debug("refreshNetworkLoad", "File does not exist file absolute path: " + file.getAbsolutePath());
                }
            } catch (Exception e) {
                _log.errorTrace("refreshNetworkLoad: Exception print stack trace:=", e);
            }

        }
    }

    public static void refreshInterfaceLoad(boolean p_fileRead) {
        if (_log.isDebugEnabled())
            _log.debug("refreshInterfaceLoad", "Entered p_fileRead: " + p_fileRead);
        refreshInterfaceLoad();
        if (p_fileRead) {
            try {
                File file = new File(Constants.getProperty("FILE_PATH") + Constants.getProperty("INTERFACE_COUNTER_REPORT"));
                if (file.exists()) {
                    if (_log.isDebugEnabled())
                        _log.debug("refreshInterfaceLoad", "File exists file absolute path: " + file.getAbsolutePath());
                    Hashtable interfaceHashRead = read(file);
                    if (!(interfaceHashRead == null || interfaceHashRead.isEmpty())) {
                        modifyInterfaceLoadVO(_interfaceLoadHash, interfaceHashRead);
                    }
                } else {
                    if (_log.isDebugEnabled())
                        _log.debug("refreshInterfaceLoad", "File does not exist file absolute path: " + file.getAbsolutePath());
                }
            } catch (Exception e) {
                _log.errorTrace("refreshInterfaceLoad: Exception print stack trace:=", e);
            }

        }
    }

    public static void refreshTransactionLoad(boolean p_fileRead) {
        if (_log.isDebugEnabled())
            _log.debug("refreshTransactionLoad", "Entered p_fileRead: " + p_fileRead);
        refreshTransactionLoad();
        if (p_fileRead) {
            try {
                File file = new File(Constants.getProperty("FILE_PATH") + Constants.getProperty("TRANSACTION_COUNTER_REPORT"));
                if (file.exists()) {
                    if (_log.isDebugEnabled())
                        _log.debug("refreshTransactionLoad", "File exists file absolute path: " + file.getAbsolutePath());
                    Hashtable transactionHashRead = read(file);
                    if (!(transactionHashRead == null || transactionHashRead.isEmpty())) {
                        modifyTransactionLoadVO(_transactionLoadHash, transactionHashRead);
                    }
                } else {
                    if (_log.isDebugEnabled())
                        _log.debug("refreshTransactionLoad", "File does not exist file absolute path: " + file.getAbsolutePath());
                }
            } catch (Exception e) {
                _log.errorTrace("refreshTransactionLoad: Exception print stack trace:=", e);
            }

        }
    }

    public static void refreshNetworkServiceCounters(String p_instanceID, boolean p_fileRead) // Pass
                                                                                              // from
                                                                                              // config
                                                                                              // servlets
    {
        if (_log.isDebugEnabled())
            _log.debug("refreshNetworkServiceCounters", "Entered p_instanceID: " + p_instanceID + " p_fileRead: " + p_fileRead);
        refreshNetworkServiceCounters(p_instanceID);
        if (p_fileRead) {
            try {
                File file = new File(Constants.getProperty("FILE_PATH") + Constants.getProperty("NETWORK_SERVICE_COUNTER_REPORT"));
                if (file.exists()) {
                    if (_log.isDebugEnabled())
                        _log.debug("refreshNetworkServiceCounters", "File exists file absolute path: " + file.getAbsolutePath());
                    Hashtable networkServiceLoadHashRead = read(file);
                    if (!(networkServiceLoadHashRead == null || networkServiceLoadHashRead.isEmpty())) {
                        modifyNetworkServiceLoadVO(_networkServiceLoadHash, networkServiceLoadHashRead);
                    }
                } else {
                    if (_log.isDebugEnabled())
                        _log.debug("refreshNetworkServiceCounters", "File does not exist file absolute path: " + file.getAbsolutePath());
                }
            } catch (Exception e) {
                _log.errorTrace("refreshNetworkServiceCounters: Exception print stack trace:=", e);
            }

        }

    }

    public static void refreshNetworkServiceHourlyCounters(String p_instanceID, boolean p_fileRead) // Pass
                                                                                                    // from
                                                                                                    // config
                                                                                                    // servlets
    {
        if (_log.isDebugEnabled())
            _log.debug("refreshNetworkServiceHourlyCounters", "Entered p_instanceID: " + p_instanceID + " p_fileRead: " + p_fileRead);
        refreshNetworkServiceHourlyCounters(p_instanceID);
        if (p_fileRead) {
            try {
                File file = new File(Constants.getProperty("FILE_PATH") + Constants.getProperty("NETWORK_SERVICE_HOURLY_COUNTER_REPORT"));
                if (file.exists()) {
                    if (_log.isDebugEnabled())
                        _log.debug("refreshNetworkServiceHourlyCounters", "File exists file absolute path: " + file.getAbsolutePath());
                    Hashtable networkServiceLoadHoulryHashRead = read(file);
                    if (!(networkServiceLoadHoulryHashRead == null || networkServiceLoadHoulryHashRead.isEmpty())) {
                        modifyNetworkServiceHourlyLoadVO(_networkServiceHourlyLoadHash, networkServiceLoadHoulryHashRead);
                    }
                } else {
                    if (_log.isDebugEnabled())
                        _log.debug("refreshNetworkServiceHourlyCounters", "File does not exist file absolute path: " + file.getAbsolutePath());
                }
            } catch (Exception e) {
                _log.errorTrace("refreshNetworkServiceCounters: Exception print stack trace:=", e);
            }

        }

    }

    public static void refreshInstanceLoad(String p_instanceID) // Pass from
                                                                // config
                                                                // servlets
    {
        if (_log.isDebugEnabled())
            _log.debug("refreshInstanceLoad", "Entered: ");
        try {
            _instanceID = p_instanceID;
            if (_log.isDebugEnabled())
                _log.debug("refreshInstanceLoad", " Before loading:" + _instanceLoadHash.size());
            LoadControllerDAO loadControllerDAO = new LoadControllerDAO();
            _instanceLoadHash = loadControllerDAO.loadInstanceLoadDetails(p_instanceID);
            InstanceLoadVO instanceLoadVO = (InstanceLoadVO) _instanceLoadHash.get(_instanceID);
            if (instanceLoadVO != null)
                _loadType = instanceLoadVO.getLoadType();

            if (!_instanceLoadObjectMap.containsKey(_instanceID))
                _instanceLoadObjectMap.put(_instanceID, new InstanceLoadController());
            _instanceLoadForNetworkHash = loadControllerDAO.loadInstanceLoadDetailsForNetwork();
            if (_log.isDebugEnabled())
                _log.debug("refreshInstanceLoad", " After loading:" + _instanceLoadHash.size());

        } catch (Exception e) {
            _log.error("refreshInstanceLoad", "Exception " + e.getMessage());
            _log.errorTrace("refreshInstanceLoad: Exception print stack trace:=", e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[refreshInstanceLoad]", "", "", "", "Exception:" + e.getMessage());
        }
    }

    public static void refreshNetworkLoad() {
        if (_log.isDebugEnabled())
            _log.debug("refreshNetworkLoad", "Entered: ");
        try {
            if (_log.isDebugEnabled())
                _log.debug("refreshNetworkLoad", " Before loading:" + _networkLoadHash.size());
            LoadControllerDAO loadControllerDAO = new LoadControllerDAO();
            _networkLoadHash = loadControllerDAO.loadNetworkLoadDetails(_instanceID, _loadType, true);
            if (_log.isDebugEnabled())
                _log.debug("refreshNetworkLoad", " After loading:" + _networkLoadHash.size());

        } catch (Exception e) {
            _log.error("refreshNetworkLoad", "Exception " + e.getMessage());
            _log.errorTrace("refreshNetworkLoad: Exception print stack trace:=", e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[refreshNetworkLoad]", "", "", "", "Exception:" + e.getMessage());
        }
    }

    public static void refreshInterfaceLoad() {
        if (_log.isDebugEnabled())
            _log.debug("refreshInterfaceLoad", "Entered: ");
        try {
            if (_log.isDebugEnabled())
                _log.debug("refreshInterfaceLoad", " Before loading:" + _interfaceLoadHash.size());
            LoadControllerDAO loadControllerDAO = new LoadControllerDAO();
            _interfaceLoadHash = loadControllerDAO.loadInterfaceLoadDetails(_instanceID, _loadType, true);
            if (_log.isDebugEnabled())
                _log.debug("refreshInterfaceLoad", " After loading:" + _interfaceLoadHash.size());

        } catch (Exception e) {
            _log.error("refreshInterfaceLoad", "Exception " + e.getMessage());
            _log.errorTrace("refreshInterfaceLoad: Exception print stack trace:=", e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[refreshInterfaceLoad]", "", "", "", "Exception:" + e.getMessage());
        }
    }

    public static void refreshTransactionLoad() {
        if (_log.isDebugEnabled())
            _log.debug("refreshTransactionLoad", "Entered: ");
        try {
            if (_log.isDebugEnabled())
                _log.debug("refreshTransactionLoad", " Before loading:" + _transactionLoadHash.size());
            LoadControllerDAO loadControllerDAO = new LoadControllerDAO();
            _transactionLoadHash = loadControllerDAO.loadTransactionLoadDetails(_instanceID, _loadType, true);
            if (_log.isDebugEnabled())
                _log.debug("refreshTransactionLoad", " After loading:" + _transactionLoadHash.size());

        } catch (Exception e) {
            _log.error("refreshTransactionLoad", "Exception " + e.getMessage());
            _log.errorTrace("refreshTransactionLoad: Exception print stack trace:=", e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[refreshTransactionLoad]", "", "", "", "Exception:" + e.getMessage());
        }
    }

    public static Hashtable getInterfaceLoadHash() {
        return _interfaceLoadHash;
    }

    public static void setInterfaceLoadHash(Hashtable interfaceLoadHash) {
        _interfaceLoadHash = interfaceLoadHash;
    }

    public static Hashtable getTransactionLoadHash() {
        return _transactionLoadHash;
    }

    public static void setTransactionLoadHash(Hashtable transactionLoadHash) {
        _transactionLoadHash = transactionLoadHash;
    }

    public static HashMap getAllTransactionLoadListMap() {
        return _allTransactionLoadListMap;
    }

    public static void setAllTransactionLoadListMap(HashMap allTransactionLoadListMap) {
        _allTransactionLoadListMap = allTransactionLoadListMap;
    }

    /*
     * public static HashMap getAllRecieverTransLoadListMap() {
     * return _allRecieverTransLoadListMap;
     * }
     * 
     * public static void setAllRecieverTransLoadListMap(
     * HashMap allRecieverTransLoadListMap) {
     * _allRecieverTransLoadListMap = allRecieverTransLoadListMap;
     * }
     */
    public static HashMap getInstanceLoadObjectMap() {
        return _instanceLoadObjectMap;
    }

    public static void setInstanceLoadObjectMap(HashMap instanceLoadObjectMap) {
        _instanceLoadObjectMap = instanceLoadObjectMap;
    }

    public static HashMap getInterfaceLoadObjectMap() {
        return _interfaceLoadObjectMap;
    }

    public static void setInterfaceLoadObjectMap(HashMap interfaceLoadObjectMap) {
        _interfaceLoadObjectMap = interfaceLoadObjectMap;
    }

    public static HashMap getNetworkLoadObjectMap() {
        return _networkLoadObjectMap;
    }

    public static void setNetworkLoadObjectMap(HashMap networkLoadObjectMap) {
        _networkLoadObjectMap = networkLoadObjectMap;
    }

    public static HashMap getTransactionLoadObjectMap() {
        return _transactionLoadObjectMap;
    }

    public static void setTransactionLoadObjectMap(HashMap transactionLoadObjectMap) {
        _transactionLoadObjectMap = transactionLoadObjectMap;
    }

    /**
     * Method to initialize Instance load
     * 
     * @param p_instanceID
     */
    public static void initializeInstanceLoad(String p_instanceID) {
        if (_log.isDebugEnabled())
            _log.debug("initializeInstanceLoad", "Entered: with p_instanceID=" + p_instanceID);
        try {
            java.util.Date date = new Date();
            Timestamp t1 = new Timestamp(date.getTime());
            LoadControllerDAO loadControllerDAO = new LoadControllerDAO();
            Hashtable tempHash = loadControllerDAO.loadInstanceLoadDetails(p_instanceID);
            InstanceLoadVO instanceLoadVO = null;
            if (tempHash != null) {
                Enumeration size = tempHash.keys();
                while (size.hasMoreElements()) {
                    String key = (String) size.nextElement();
                    if (_log.isDebugEnabled())
                        _log.debug("initializeInstanceLoad", "Got Key =" + key);

                    if (_instanceLoadHash != null && _instanceLoadHash.containsKey(key)) {
                        instanceLoadVO = (InstanceLoadVO) _instanceLoadHash.get(key);
                        instanceLoadVO.setCurrentTransactionLoad(0);
                        instanceLoadVO.setFirstRequestCount(0);
                        // instanceLoadVO.setCurrentTPS(0);
                        instanceLoadVO.setFirstRequestTime(0);
                        instanceLoadVO.setLastInitializationTime(date);
                        if (_log.isDebugEnabled())
                            _log.debug("initializeInstanceLoad", " last initialize time is changed to :" + instanceLoadVO.getLastInitializationTime());
                        instanceLoadVO.setLastReceievedTime(t1);
                        // instanceLoadVO.setLastRefusedTime(t1);
                        instanceLoadVO.setLastRefusedTime(null);
                        instanceLoadVO.setNoOfRequestSameSec(0);
                        instanceLoadVO.setReceiverCurrentTransactionLoad(0);
                        instanceLoadVO.setRecievedCount(0);
                        instanceLoadVO.setRequestCount(0);
                        instanceLoadVO.setRequestTimeoutSec(0);
                        instanceLoadVO.setTotalRefusedCount(0);
                    } else {
                        if (_log.isDebugEnabled())
                            _log.debug("initializeInstanceLoad", "Not found Key =" + key + " in _instanceLoadHash=" + _instanceLoadHash + " Thus inserting the same");

                        if (_instanceLoadHash == null)
                            _instanceLoadHash = new Hashtable();
                        _instanceLoadHash.put(key, tempHash.get(key));
                        if (_instanceLoadObjectMap == null)
                            _instanceLoadObjectMap = new HashMap();
                        if (!_instanceLoadObjectMap.containsKey(_instanceID))
                            _instanceLoadObjectMap.put(_instanceID, new InstanceLoadController());
                    }

                }
            }
        } catch (Exception e) {
            _log.error("initializeInstanceLoad", "Exception " + e.getMessage());
            _log.errorTrace("initializeInstanceLoad: Exception print stack trace:=", e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[initializeInstanceLoad]", "", "", "", "Exception:" + e.getMessage());
        }
    }

    /**
     * Method to initialize Network load
     */
    public static void initializeNetworkLoad() {
        if (_log.isDebugEnabled())
            _log.debug("initializeNetworkLoad", "Entered ");
        try {
            java.util.Date date = new Date();
            Timestamp t1 = new Timestamp(date.getTime());

            LoadControllerDAO loadControllerDAO = new LoadControllerDAO();
            Hashtable tempHash = loadControllerDAO.loadNetworkLoadDetails(_instanceID, _loadType, false);
            NetworkLoadVO networkLoadVO = null;
            HashMap hashMap = null;

            if (tempHash != null) {
                Enumeration size = tempHash.keys();
                while (size.hasMoreElements()) {
                    hashMap = LoadControllerCache.getNetworkLoadObjectMap();
                    String key = (String) size.nextElement();
                    if (_log.isDebugEnabled())
                        _log.debug("initializeNetworkLoad", "Got Key =" + key);

                    if (_networkLoadHash != null && _networkLoadHash.containsKey(key)) {
                        networkLoadVO = (NetworkLoadVO) _networkLoadHash.get(key);
                        networkLoadVO.setCurrentTransactionLoad(0);
                        networkLoadVO.setFirstRequestCount(0);
                        // networkLoadVO.setCurrentTPS(0);
                        networkLoadVO.setFirstRequestTime(0);
                        networkLoadVO.setLastInitializationTime(date);
                        networkLoadVO.setLastReceievedTime(t1);
                        // networkLoadVO.setLastRefusedTime(t1);
                        networkLoadVO.setLastRefusedTime(null);
                        networkLoadVO.setNoOfRequestSameSec(0);
                        networkLoadVO.setReceiverCurrentTransactionLoad(0);
                        networkLoadVO.setRecievedCount(0);
                        networkLoadVO.setRequestCount(0);
                        networkLoadVO.setRequestTimeoutSec(0);
                        networkLoadVO.setTotalRefusedCount(0);
                    } else {
                        if (_log.isDebugEnabled())
                            _log.debug("initializeNetworkLoad", "Not found Key =" + key + " in _networkLoadHash=" + _networkLoadHash + " Thus inserting the same");
                        if (_networkLoadHash == null)
                            _networkLoadHash = new Hashtable();
                        _networkLoadHash.put(key, tempHash.get(key));
                        if (hashMap == null)
                            hashMap = new HashMap();
                        if (!hashMap.containsKey(key)) {
                            hashMap.put(key, new NetworkLoadController());
                            LoadControllerCache.setNetworkLoadObjectMap(hashMap);
                        }
                    }
                }
            }
            if (_log.isDebugEnabled())
                _log.debug("initializeNetworkLoad", " After loading:" + _networkLoadHash.size());
        } catch (Exception e) {
            _log.error("initializeNetworkLoad", "Exception " + e.getMessage());
            _log.errorTrace("initializeNetworkLoad: Exception print stack trace:=", e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[initializeNetworkLoad]", "", "", "", "Exception:" + e.getMessage());
        }
    }

    /**
     * Method to initialize Interface load
     */
    public static void initializeInterfaceLoad() {
        if (_log.isDebugEnabled())
            _log.debug("initializeInterfaceLoad", "Entered ");
        try {
            java.util.Date date = new Date();
            Timestamp t = new Timestamp(date.getTime());

            LoadControllerDAO loadControllerDAO = new LoadControllerDAO();
            Hashtable tempHash = loadControllerDAO.loadInterfaceLoadDetails(_instanceID, _loadType, false);
            InterfaceLoadVO interfaceLoadVO = null;
            HashMap hashMap = null;

            if (tempHash != null) {
                Enumeration size = tempHash.keys();
                while (size.hasMoreElements()) {
                    hashMap = LoadControllerCache.getInterfaceLoadObjectMap();
                    String key = (String) size.nextElement();
                    if (_log.isDebugEnabled())
                        _log.debug("initializeInterfaceLoad", "Got Key =" + key);
                    if (_interfaceLoadHash != null && _interfaceLoadHash.containsKey(key)) {
                        interfaceLoadVO = (InterfaceLoadVO) _interfaceLoadHash.get(key);
                        interfaceLoadVO.setCurrentTransactionLoad(0);
                        interfaceLoadVO.setFirstRequestCount(0);
                        // interfaceLoadVO.setCurrentTPS(0);
                        interfaceLoadVO.setFirstRequestTime(0);
                        interfaceLoadVO.setLastInitializationTime(date);
                        interfaceLoadVO.setLastReceievedTime(t);
                        // interfaceLoadVO.setLastRefusedTime(t);
                        interfaceLoadVO.setLastRefusedTime(null);
                        interfaceLoadVO.setNoOfRequestSameSec(0);
                        interfaceLoadVO.setReceiverCurrentTransactionLoad(0);
                        interfaceLoadVO.setRecievedCount(0);
                        interfaceLoadVO.setRequestCount(0);
                        interfaceLoadVO.setRequestTimeoutSec(0);
                        interfaceLoadVO.setTotalRefusedCount(0);
                        interfaceLoadVO.setCurrentQueueSize(0);
                        interfaceLoadVO.setLastQueueAdditionTime(t);
                        interfaceLoadVO.setLastQueueAdditionTime(t);
                        interfaceLoadVO.setLastQueueCaseCheckTime(0);
                        interfaceLoadVO.setLastReceievedTime(t);
                        // interfaceLoadVO.setLastRefusedTime(t);
                        interfaceLoadVO.setLastTxnProcessStartTime(t);
                    } else {

                        if (_log.isDebugEnabled())
                            _log.debug("initializeInterfaceLoad", "Not found Key =" + key + " in _interfaceLoadHash=" + _interfaceLoadHash + " Thus inserting the same");
                        if (_interfaceLoadHash == null)
                            _interfaceLoadHash = new Hashtable();
                        _interfaceLoadHash.put(key, tempHash.get(key));
                        if (hashMap == null)
                            hashMap = new HashMap();
                        if (!hashMap.containsKey(key)) {
                            hashMap.put(key, new InterfaceLoadController());
                            LoadControllerCache.setInterfaceLoadObjectMap(hashMap);
                        }
                    }
                }
            }
            if (_log.isDebugEnabled())
                _log.debug("initializeInterfaceLoad", " After loading:" + _interfaceLoadHash.size());
        } catch (Exception e) {
            _log.error("initializeInterfaceLoad", "Exception " + e.getMessage());
            _log.errorTrace("initializeInterfaceLoad: Exception print stack trace:=", e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[initializeInterfaceLoad]", "", "", "", "Exception:" + e.getMessage());
        }
    }

    /**
     * Method to initialize Transaction load
     */
    public static void initializeTransactionLoad() {
        if (_log.isDebugEnabled())
            _log.debug("initializeTransactionLoad", "Entered ");
        try {
            java.util.Date date = new Date();
            Timestamp t = new Timestamp(date.getTime());

            LoadControllerDAO loadControllerDAO = new LoadControllerDAO();
            Hashtable tempHash = loadControllerDAO.loadTransactionLoadDetails(_instanceID, _loadType, false);
            TransactionLoadVO transactionLoadVO = null;
            TransactionLoadVO alternateTransactionLoadVO = null;
            HashMap hashMap = null;

            if (tempHash != null) {
                Enumeration size = tempHash.keys();
                while (size.hasMoreElements()) {
                    hashMap = LoadControllerCache.getTransactionLoadObjectMap();
                    String key = (String) size.nextElement();
                    if (_log.isDebugEnabled())
                        _log.debug("initializeTransactionLoad", "Got Key =" + key);
                    if (_transactionLoadHash != null && _transactionLoadHash.containsKey(key)) {
                        transactionLoadVO = (TransactionLoadVO) _transactionLoadHash.get(key);
                        transactionLoadVO.setCurrentTransactionLoad(0);
                        transactionLoadVO.setFirstRequestCount(0);
                        // transactionLoadVO.setCurrentTPS(0);
                        transactionLoadVO.setFirstRequestTime(0);
                        transactionLoadVO.setLastInitializationTime(date);
                        transactionLoadVO.setLastReceievedTime(t);
                        // transactionLoadVO.setLastRefusedTime(t);
                        transactionLoadVO.setLastRefusedTime(null);
                        transactionLoadVO.setNoOfRequestSameSec(0);
                        transactionLoadVO.setReceiverCurrentTransactionLoad(0);
                        transactionLoadVO.setRecievedCount(0);
                        transactionLoadVO.setRequestCount(0);
                        transactionLoadVO.setRequestTimeoutSec(0);
                        transactionLoadVO.setTotalRefusedCount(0);
                        transactionLoadVO.setLastReceievedTime(t);
                        // transactionLoadVO.setLastRefusedTime(t);
                        transactionLoadVO.setLastTxnProcessStartTime(t);
                        transactionLoadVO.setCurrentRecieverTopupCount(0);
                        transactionLoadVO.setCurrentSenderTopupCount(0);
                        transactionLoadVO.setCurrentRecieverValidationCount(0);
                        transactionLoadVO.setCurrentSenderValidationCount(0);
                        transactionLoadVO.setReceiverCurrentTransactionLoad(0);
                        transactionLoadVO.setTotalInternalFailCount(0);
                        transactionLoadVO.setTotalRecieverTopupCount(0);
                        transactionLoadVO.setTotalRecieverTopupFailCount(0);
                        transactionLoadVO.setTotalRecieverValFailCount(0);
                        transactionLoadVO.setTotalRecieverValidationCount(0);
                        transactionLoadVO.setTotalSenderTopupCount(0);
                        transactionLoadVO.setTotalRecieverValidationCount(0);
                        transactionLoadVO.setTotalSenderTopupFailCount(0);
                        transactionLoadVO.setTotalSenderValFailCount(0);
                        transactionLoadVO.setTotalSenderValidationCount(0);
                        ArrayList newAlternateList = ((TransactionLoadVO) tempHash.get(key)).getAlternateServiceLoadType();
                        if (_log.isDebugEnabled())
                            _log.debug("initializeTransactionLoad", "newAlternateList=" + newAlternateList);

                        if (newAlternateList != null) {
                            for (int i = 0; i < newAlternateList.size(); i++) {
                                TransactionLoadVO newAlternateTransactionLoadVO = (TransactionLoadVO) newAlternateList.get(i);
                                boolean isRecordFound = false;

                                ArrayList alternateList = transactionLoadVO.getAlternateServiceLoadType();
                                if (_log.isDebugEnabled())
                                    _log.debug("initializeTransactionLoad", "alternateList=" + alternateList);

                                if (alternateList != null) {
                                    for (int j = 0; j < alternateList.size(); j++) {
                                        alternateTransactionLoadVO = (TransactionLoadVO) alternateList.get(i);
                                        if (_log.isDebugEnabled())
                                            _log.debug("initializeTransactionLoad", "Old Service Type=" + alternateTransactionLoadVO.getServiceType() + " New Service type=" + newAlternateTransactionLoadVO.getServiceType());

                                        if (alternateTransactionLoadVO.getServiceType().equals(newAlternateTransactionLoadVO.getServiceType())) {
                                            isRecordFound = true;
                                            alternateTransactionLoadVO.setCurrentTransactionLoad(0);
                                            alternateTransactionLoadVO.setFirstRequestCount(0);
                                            // alternateTransactionLoadVO.setCurrentTPS(0);
                                            alternateTransactionLoadVO.setFirstRequestTime(0);
                                            alternateTransactionLoadVO.setLastInitializationTime(date);
                                            alternateTransactionLoadVO.setLastReceievedTime(t);
                                            alternateTransactionLoadVO.setLastRefusedTime(t);
                                            alternateTransactionLoadVO.setNoOfRequestSameSec(0);
                                            alternateTransactionLoadVO.setReceiverCurrentTransactionLoad(0);
                                            alternateTransactionLoadVO.setRecievedCount(0);
                                            alternateTransactionLoadVO.setRequestCount(0);
                                            alternateTransactionLoadVO.setRequestTimeoutSec(0);
                                            alternateTransactionLoadVO.setTotalRefusedCount(0);
                                            alternateTransactionLoadVO.setLastReceievedTime(t);
                                            alternateTransactionLoadVO.setLastRefusedTime(t);
                                            alternateTransactionLoadVO.setLastTxnProcessStartTime(t);
                                            alternateTransactionLoadVO.setCurrentRecieverTopupCount(0);
                                            alternateTransactionLoadVO.setCurrentSenderTopupCount(0);
                                            alternateTransactionLoadVO.setCurrentRecieverValidationCount(0);
                                            alternateTransactionLoadVO.setCurrentSenderValidationCount(0);
                                            alternateTransactionLoadVO.setReceiverCurrentTransactionLoad(0);
                                            alternateTransactionLoadVO.setTotalInternalFailCount(0);
                                            alternateTransactionLoadVO.setTotalRecieverTopupCount(0);
                                            alternateTransactionLoadVO.setTotalRecieverTopupFailCount(0);
                                            alternateTransactionLoadVO.setTotalRecieverValFailCount(0);
                                            alternateTransactionLoadVO.setTotalRecieverValidationCount(0);
                                            alternateTransactionLoadVO.setTotalSenderTopupCount(0);
                                            alternateTransactionLoadVO.setTotalRecieverValidationCount(0);
                                            alternateTransactionLoadVO.setTotalSenderTopupFailCount(0);
                                            alternateTransactionLoadVO.setTotalSenderValFailCount(0);
                                            alternateTransactionLoadVO.setTotalSenderValidationCount(0);
                                            break;
                                        }

                                    }
                                    if (!isRecordFound) {
                                        if (_log.isDebugEnabled())
                                            _log.debug("initializeTransactionLoad", "Not found in " + newAlternateTransactionLoadVO.getServiceType() + " In Old List thus adding it");

                                        alternateList.add(newAlternateTransactionLoadVO);
                                        transactionLoadVO.setAlternateServiceLoadType(alternateList);
                                    }
                                } else {
                                    if (_log.isDebugEnabled())
                                        _log.debug("initializeTransactionLoad", "Previous Alternate List Empty Thus Add Key  " + newAlternateTransactionLoadVO.getServiceType() + " In Old List");
                                    alternateList = new ArrayList();
                                    alternateList.add(newAlternateTransactionLoadVO);
                                    transactionLoadVO.setAlternateServiceLoadType(alternateList);
                                }
                            }
                        } else {
                            if (_log.isDebugEnabled())
                                _log.debug("initializeTransactionLoad", "Setting new List as it is Empty in new List");
                            transactionLoadVO.setAlternateServiceLoadType(((TransactionLoadVO) tempHash.get(key)).getAlternateServiceLoadType());
                        }
                    } else {
                        if (_log.isDebugEnabled())
                            _log.debug("initializeTransactionLoad", "Not found Key =" + key + " in _transactionLoadHash=" + _transactionLoadHash + " Thus inserting the same");
                        if (_transactionLoadHash == null)
                            _transactionLoadHash = new Hashtable();
                        _transactionLoadHash.put(key, tempHash.get(key));
                        if (hashMap == null)
                            hashMap = new HashMap();
                        if (!hashMap.containsKey(key)) {
                            hashMap.put(key, new TransactionLoadController());
                            LoadControllerCache.setTransactionLoadObjectMap(hashMap);
                        }
                    }
                }
            }
            if (_log.isDebugEnabled())
                _log.debug("initializeTransactionLoad", " After loading:" + _transactionLoadHash.size());
        } catch (Exception e) {
            _log.error("initializeTransactionLoad", "Exception " + e.getMessage());
            _log.errorTrace("initializeTransactionLoad: Exception print stack trace:=", e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[initializeTransactionLoad]", "", "", "", "Exception:" + e.getMessage());
        }
    }

    /**
     * For updating Instance Load
     * 
     * @param p_instanceID
     */
    public static void updateInstanceLoad(String p_instanceID) {
        if (_log.isDebugEnabled())
            _log.debug("updateInstanceLoad", "Entered: with p_instanceID=" + p_instanceID);
        try {

            LoadControllerDAO loadControllerDAO = new LoadControllerDAO();
            Hashtable tempHash = loadControllerDAO.loadInstanceLoadDetails(p_instanceID);
            InstanceLoadVO instanceLoadVO = null;
            InstanceLoadVO newInstanceLoadVO = null;
            if (tempHash != null) {
                Enumeration size = tempHash.keys();
                while (size.hasMoreElements()) {
                    String key = (String) size.nextElement();
                    if (_log.isDebugEnabled())
                        _log.debug("updateInstanceLoad", "Got Key =" + key);
                    newInstanceLoadVO = (InstanceLoadVO) tempHash.get(key);
                    if (_instanceLoadHash != null && _instanceLoadHash.containsKey(key)) {
                        instanceLoadVO = (InstanceLoadVO) _instanceLoadHash.get(key);
                        instanceLoadVO.setInstanceName(newInstanceLoadVO.getInstanceName());
                        instanceLoadVO.setCurrentStatus(newInstanceLoadVO.getCurrentStatus());
                        instanceLoadVO.setHostAddress(newInstanceLoadVO.getHostAddress());
                        instanceLoadVO.setHostPort(newInstanceLoadVO.getHostPort());
                        instanceLoadVO.setDefinedTransactionLoad(newInstanceLoadVO.getDefinedTransactionLoad());
                        instanceLoadVO.setTransactionLoad(newInstanceLoadVO.getTransactionLoad());
                        instanceLoadVO.setRequestTimeoutSec(newInstanceLoadVO.getRequestTimeoutSec());
                        instanceLoadVO.setDefinedTPS(newInstanceLoadVO.getDefinedTPS());
                        instanceLoadVO.setDefualtTPS(newInstanceLoadVO.getDefualtTPS());
                        instanceLoadVO.setCurrentTPS(newInstanceLoadVO.getCurrentTPS());
                        _loadType = newInstanceLoadVO.getLoadType();
                        instanceLoadVO.setLoadType(_loadType);
                        instanceLoadVO.setInstanceLoadStatus(newInstanceLoadVO.isInstanceLoadStatus());
                    } else {
                        if (_log.isDebugEnabled())
                            _log.debug("updateInstanceLoad", "Not found Key =" + key + " in _instanceLoadHash=" + _instanceLoadHash + " Thus inserting the same");
                        if (_instanceLoadHash == null)
                            _instanceLoadHash = new Hashtable();
                        _instanceLoadHash.put(key, tempHash.get(key));
                        if (_instanceLoadObjectMap == null)
                            _instanceLoadObjectMap = new HashMap();
                        if (!_instanceLoadObjectMap.containsKey(_instanceID))
                            _instanceLoadObjectMap.put(_instanceID, new InstanceLoadController());
                    }
                }
            }
            if (_log.isDebugEnabled())
                _log.debug("updateInstanceLoad", " After loading:" + _instanceLoadHash.size());
        } catch (Exception e) {
            _log.error("updateInstanceLoad", "Exception " + e.getMessage());
            _log.errorTrace("updateInstanceLoad: Exception print stack trace:=", e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[updateInstanceLoad]", "", "", "", "Exception:" + e.getMessage());
        }
    }

    /**
     * For updating Network Load
     * 
     */
    public static void updateNetworkLoadDetails() {
        if (_log.isDebugEnabled())
            _log.debug("updateNetworkLoadDetails", "Entered ");
        try {
            LoadControllerDAO loadControllerDAO = new LoadControllerDAO();
            Hashtable tempHash = loadControllerDAO.loadNetworkLoadDetails(_instanceID, _loadType, false);
            NetworkLoadVO networkLoadVO = null;
            NetworkLoadVO newNetworkLoadVO = null;
            HashMap hashMap = null;

            if (tempHash != null) {
                Enumeration size = tempHash.keys();
                while (size.hasMoreElements()) {
                    hashMap = LoadControllerCache.getNetworkLoadObjectMap();
                    String key = (String) size.nextElement();
                    if (_log.isDebugEnabled())
                        _log.debug("updateNetworkLoadDetails", "Got Key =" + key);
                    newNetworkLoadVO = (NetworkLoadVO) tempHash.get(key);
                    if (_networkLoadHash != null && _networkLoadHash.containsKey(key)) {
                        networkLoadVO = (NetworkLoadVO) _networkLoadHash.get(key);
                        networkLoadVO.setInstanceID(newNetworkLoadVO.getInstanceID());
                        networkLoadVO.setNetworkCode(newNetworkLoadVO.getNetworkCode());
                        networkLoadVO.setDefinedTransactionLoad(newNetworkLoadVO.getDefinedTransactionLoad());
                        networkLoadVO.setTransactionLoad(newNetworkLoadVO.getTransactionLoad());
                        networkLoadVO.setRequestTimeoutSec(newNetworkLoadVO.getRequestTimeoutSec());
                        networkLoadVO.setDefinedTPS(newNetworkLoadVO.getDefinedTPS());
                        networkLoadVO.setDefualtTPS(newNetworkLoadVO.getDefualtTPS());
                        networkLoadVO.setCurrentTPS(newNetworkLoadVO.getCurrentTPS());
                        networkLoadVO.setC2sInstanceID(newNetworkLoadVO.getC2sInstanceID());
                        networkLoadVO.setP2pInstanceID(newNetworkLoadVO.getP2pInstanceID());
                        networkLoadVO.setLoadType(_loadType);
                    } else {
                        if (_log.isDebugEnabled())
                            _log.debug("updateNetworkLoadDetails", "Not found Key =" + key + " in _networkLoadHash=" + _networkLoadHash + " Thus inserting the same");
                        if (_networkLoadHash == null)
                            _networkLoadHash = new Hashtable();
                        _networkLoadHash.put(key, tempHash.get(key));
                        if (hashMap == null)
                            hashMap = new HashMap();
                        if (!hashMap.containsKey(key)) {
                            hashMap.put(key, new NetworkLoadController());
                            LoadControllerCache.setNetworkLoadObjectMap(hashMap);
                        }
                    }
                }
            }
            if (_log.isDebugEnabled())
                _log.debug("updateNetworkLoadDetails", " After loading:" + _networkLoadHash.size());
        } catch (Exception e) {
            _log.error("updateNetworkLoadDetails", "Exception " + e.getMessage());
            _log.errorTrace("updateNetworkLoadDetails: Exception print stack trace:=", e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[updateNetworkLoadDetails]", "", "", "", "Exception:" + e.getMessage());
        }
    }

    /**
     * For updating interface Load
     * 
     */
    public static void updateInterfaceLoadDetails() {
        if (_log.isDebugEnabled())
            _log.debug("updateInterfaceLoadDetails", "Entered ");
        try {

            LoadControllerDAO loadControllerDAO = new LoadControllerDAO();
            Hashtable tempHash = loadControllerDAO.loadInterfaceLoadDetails(_instanceID, _loadType, false);
            InterfaceLoadVO interfaceLoadVO = null;
            InterfaceLoadVO newInterfaceLoadVO = null;
            HashMap hashMap = null;

            if (tempHash != null) {
                Enumeration size = tempHash.keys();
                while (size.hasMoreElements()) {
                    hashMap = LoadControllerCache.getInterfaceLoadObjectMap();
                    String key = (String) size.nextElement();
                    if (_log.isDebugEnabled())
                        _log.debug("updateInterfaceLoadDetails", "Got Key =" + key);
                    newInterfaceLoadVO = (InterfaceLoadVO) tempHash.get(key);
                    if (_interfaceLoadHash != null && _interfaceLoadHash.containsKey(key)) {
                        interfaceLoadVO = (InterfaceLoadVO) _interfaceLoadHash.get(key);
                        interfaceLoadVO.setInstanceID(newInterfaceLoadVO.getInstanceID());
                        interfaceLoadVO.setNetworkCode(newInterfaceLoadVO.getNetworkCode());
                        interfaceLoadVO.setInterfaceID(newInterfaceLoadVO.getInterfaceID());
                        interfaceLoadVO.setTransactionLoad(newInterfaceLoadVO.getTransactionLoad());
                        interfaceLoadVO.setRequestTimeoutSec(newInterfaceLoadVO.getRequestTimeoutSec());
                        interfaceLoadVO.setDefualtTPS(newInterfaceLoadVO.getDefualtTPS());
                        interfaceLoadVO.setCurrentTPS(newInterfaceLoadVO.getCurrentTPS());
                        interfaceLoadVO.setQueueSize(newInterfaceLoadVO.getQueueSize());
                        interfaceLoadVO.setQueueTimeOut(newInterfaceLoadVO.getQueueTimeOut());
                        interfaceLoadVO.setNextQueueCheckCaseAfterSec(newInterfaceLoadVO.getNextQueueCheckCaseAfterSec());
                        interfaceLoadVO.setLoadType(_loadType);
                    } else {
                        if (_log.isDebugEnabled())
                            _log.debug("updateInterfaceLoadDetails", "Not found Key =" + key + " in _interfaceLoadHash=" + _interfaceLoadHash + " Thus inserting the same");
                        if (_interfaceLoadHash == null)
                            _interfaceLoadHash = new Hashtable();
                        _interfaceLoadHash.put(key, tempHash.get(key));
                        if (hashMap == null)
                            hashMap = new HashMap();
                        if (!hashMap.containsKey(key)) {
                            hashMap.put(key, new InterfaceLoadController());
                            LoadControllerCache.setInterfaceLoadObjectMap(hashMap);
                        }
                    }
                }
            }
            if (_log.isDebugEnabled())
                _log.debug("updateInterfaceLoadDetails", " After loading:" + _interfaceLoadHash.size());
        } catch (Exception e) {
            _log.error("updateInterfaceLoadDetails", "Exception " + e.getMessage());
            _log.errorTrace("updateInterfaceLoadDetails: Exception print stack trace:=", e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[updateInterfaceLoadDetails]", "", "", "", "Exception:" + e.getMessage());
        }
    }

    /**
     * For updating Transaction Load
     * 
     */
    public static void updateTransactionLoadDetails() {
        if (_log.isDebugEnabled())
            _log.debug("updateTransactionLoadDetails", "Entered ");
        try {
            LoadControllerDAO loadControllerDAO = new LoadControllerDAO();
            Hashtable tempHash = loadControllerDAO.loadTransactionLoadDetails(_instanceID, _loadType, false);
            TransactionLoadVO transactionLoadVO = null;
            TransactionLoadVO newTransactionLoadVO = null;
            TransactionLoadVO alternateTransactionLoadVO = null;
            TransactionLoadVO newAlternateTransactionLoadVO = null;
            HashMap hashMap = null;

            if (tempHash != null) {
                Enumeration size = tempHash.keys();
                while (size.hasMoreElements()) {
                    hashMap = LoadControllerCache.getTransactionLoadObjectMap();
                    String key = (String) size.nextElement();
                    if (_log.isDebugEnabled())
                        _log.debug("updateTransactionLoadDetails", "Got Key =" + key);
                    newTransactionLoadVO = (TransactionLoadVO) tempHash.get(key);
                    if (_transactionLoadHash != null && _transactionLoadHash.containsKey(key)) {
                        transactionLoadVO = (TransactionLoadVO) _transactionLoadHash.get(key);
                        transactionLoadVO.setInstanceID(newTransactionLoadVO.getInstanceID());
                        transactionLoadVO.setNetworkCode(newTransactionLoadVO.getNetworkCode());
                        transactionLoadVO.setInterfaceID(newTransactionLoadVO.getInterfaceID());
                        transactionLoadVO.setServiceType(newTransactionLoadVO.getServiceType());
                        transactionLoadVO.setDefinedTransactionLoad(newTransactionLoadVO.getDefinedTransactionLoad());
                        transactionLoadVO.setTransactionLoad(newTransactionLoadVO.getTransactionLoad());
                        transactionLoadVO.setRequestTimeoutSec(newTransactionLoadVO.getRequestTimeoutSec());
                        transactionLoadVO.setDefinedTPS(newTransactionLoadVO.getDefinedTPS());
                        transactionLoadVO.setDefualtTPS(newTransactionLoadVO.getDefualtTPS());
                        transactionLoadVO.setCurrentTPS(newTransactionLoadVO.getCurrentTPS());
                        transactionLoadVO.setMinimumServiceTime(newTransactionLoadVO.getMinimumServiceTime());
                        transactionLoadVO.setDefinedOverFlowCount(newTransactionLoadVO.getDefinedOverFlowCount());
                        transactionLoadVO.setNextCheckTimeOutCaseAfterSec(newTransactionLoadVO.getNextCheckTimeOutCaseAfterSec());
                        transactionLoadVO.setLoadType(_loadType);
                        ArrayList newAlternateList = newTransactionLoadVO.getAlternateServiceLoadType();
                        if (_log.isDebugEnabled())
                            _log.debug("updateTransactionLoadDetails", "newAlternateList=" + newAlternateList);

                        if (newAlternateList != null) {
                            for (int i = 0; i < newAlternateList.size(); i++) {
                                newAlternateTransactionLoadVO = (TransactionLoadVO) newAlternateList.get(i);
                                boolean isRecordFound = false;

                                ArrayList alternateList = transactionLoadVO.getAlternateServiceLoadType();
                                if (_log.isDebugEnabled())
                                    _log.debug("updateTransactionLoadDetails", "alternateList=" + alternateList);

                                if (alternateList != null) {
                                    for (int j = 0; j < alternateList.size(); j++) {
                                        alternateTransactionLoadVO = (TransactionLoadVO) alternateList.get(i);
                                        if (_log.isDebugEnabled())
                                            _log.debug("updateTransactionLoadDetails", "Old Service Type=" + alternateTransactionLoadVO.getServiceType() + " New Service type=" + newAlternateTransactionLoadVO.getServiceType());

                                        if (alternateTransactionLoadVO.getServiceType().equals(newAlternateTransactionLoadVO.getServiceType())) {
                                            isRecordFound = true;
                                            alternateTransactionLoadVO.setDefinedTransactionLoad(newAlternateTransactionLoadVO.getDefinedTransactionLoad());
                                            alternateTransactionLoadVO.setTransactionLoad(newAlternateTransactionLoadVO.getTransactionLoad());
                                            alternateTransactionLoadVO.setRequestTimeoutSec(newAlternateTransactionLoadVO.getRequestTimeoutSec());
                                            alternateTransactionLoadVO.setDefinedTPS(newAlternateTransactionLoadVO.getDefinedTPS());
                                            alternateTransactionLoadVO.setDefualtTPS(newAlternateTransactionLoadVO.getDefualtTPS());
                                            alternateTransactionLoadVO.setCurrentTPS(newAlternateTransactionLoadVO.getCurrentTPS());
                                            alternateTransactionLoadVO.setMinimumServiceTime(newAlternateTransactionLoadVO.getMinimumServiceTime());
                                            alternateTransactionLoadVO.setOverFlowCount(newAlternateTransactionLoadVO.getOverFlowCount());
                                            alternateTransactionLoadVO.setNextCheckTimeOutCaseAfterSec(newAlternateTransactionLoadVO.getNextCheckTimeOutCaseAfterSec());
                                            alternateTransactionLoadVO.setLoadType(_loadType);
                                            break;
                                        }

                                    }
                                    if (!isRecordFound) {
                                        if (_log.isDebugEnabled())
                                            _log.debug("updateTransactionLoadDetails", "Not found in " + newAlternateTransactionLoadVO.getServiceType() + " In Old List thus adding it");

                                        alternateList.add(newAlternateTransactionLoadVO);
                                        transactionLoadVO.setAlternateServiceLoadType(alternateList);
                                    }
                                } else {
                                    if (_log.isDebugEnabled())
                                        _log.debug("updateTransactionLoadDetails", "Previous Alternate List Empty Thus Add Key  " + newAlternateTransactionLoadVO.getServiceType() + " In Old List");

                                    alternateList = new ArrayList();
                                    alternateList.add(newAlternateTransactionLoadVO);
                                    transactionLoadVO.setAlternateServiceLoadType(alternateList);
                                }
                            }
                        } else {
                            if (_log.isDebugEnabled())
                                _log.debug("updateTransactionLoadDetails", "Setting new List as it is Empty in new List");

                            transactionLoadVO.setAlternateServiceLoadType(newTransactionLoadVO.getAlternateServiceLoadType());
                        }
                    } else {

                        if (_log.isDebugEnabled())
                            _log.debug("updateTransactionLoadDetails", "Not found Key =" + key + " in _transactionLoadHash=" + _transactionLoadHash + " Thus inserting the same");
                        if (_transactionLoadHash == null)
                            _transactionLoadHash = new Hashtable();
                        _transactionLoadHash.put(key, tempHash.get(key));
                        if (hashMap == null)
                            hashMap = new HashMap();
                        if (!hashMap.containsKey(key)) {
                            hashMap.put(key, new TransactionLoadController());
                            LoadControllerCache.setTransactionLoadObjectMap(hashMap);
                        }
                    }
                }
            }
            if (_log.isDebugEnabled())
                _log.debug("updateTransactionLoadDetails", " After loading:" + _transactionLoadHash.size());
        } catch (Exception e) {
            _log.error("updateTransactionLoadDetails", "Exception " + e.getMessage());
            _log.errorTrace("updateTransactionLoadDetails: Exception print stack trace:=", e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[updateTransactionLoadDetails]", "", "", "", "Exception:" + e.getMessage());
        }
    }

    /**
     * @return Returns the networkServiceLoadHash.
     */
    public static Hashtable getNetworkServiceLoadHash() {
        return _networkServiceLoadHash;
    }

    /**
     * @param networkServiceLoadHash
     *            The networkServiceLoadHash to set.
     */
    public static void setNetworkServiceLoadHash(Hashtable networkServiceLoadHash) {
        _networkServiceLoadHash = networkServiceLoadHash;
    }

    /**
     * @return Returns the networkServiceLoadHash.
     */
    public static Hashtable getNetworkServiceHourlyLoadHash() {
        return _networkServiceHourlyLoadHash;
    }

    /**
     * @param networkServiceLoadHash
     *            The networkServiceLoadHash to set.
     */
    public static void setNetworkServiceHourlyLoadHash(Hashtable networkServiceHourlyLoadHash) {
        _networkServiceHourlyLoadHash = networkServiceHourlyLoadHash;
    }

    public static void refreshNetworkServiceCounters(String p_instanceID) // Pass
                                                                          // from
                                                                          // config
                                                                          // servlets
    {
        if (_log.isDebugEnabled())
            _log.debug("refreshNetworkServiceCounters", "Entered: ");
        try {
            _instanceID = p_instanceID;
            if (_log.isDebugEnabled())
                _log.debug("refreshNetworkServiceCounters", " Before loading:" + _networkServiceLoadHash.size());
            LoadControllerDAO loadControllerDAO = new LoadControllerDAO();
            _networkServiceLoadHash = loadControllerDAO.loadNetworkSeriveDetails(p_instanceID);

            if (_log.isDebugEnabled())
                _log.debug("refreshNetworkServiceCounters", " After loading:" + _networkServiceLoadHash.size());
        } catch (Exception e) {
            _log.error("refreshNetworkServiceCounters", "Exception " + e.getMessage());
            _log.errorTrace("refreshNetworkServiceCounters: Exception print stack trace:=", e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[refreshNetworkServiceCounters]", "", "", "", "Exception:" + e.getMessage());
        }
    }

    public static void refreshNetworkServiceHourlyCounters(String p_instanceID) // Pass
                                                                                // from
                                                                                // config
                                                                                // servlets
    {
        if (_log.isDebugEnabled())
            _log.debug("refreshNetworkServiceHourlyCounters", "Entered: ");
        try {
            _instanceID = p_instanceID;
            if (_log.isDebugEnabled())
                _log.debug("refreshNetworkServiceHourlyCounters", " Before loading:" + _networkServiceHourlyLoadHash.size());
            LoadControllerDAO loadControllerDAO = new LoadControllerDAO();
            _networkServiceHourlyLoadHash = loadControllerDAO.loadNetworkServiceHourlyDetails(p_instanceID);

            if (_log.isDebugEnabled())
                _log.debug("refreshNetworkServiceHourlyCounters", " After loading:" + _networkServiceHourlyLoadHash.size());
        } catch (Exception e) {
            _log.error("refreshNetworkServiceHourlyCounters", "Exception " + e.getMessage());
            _log.errorTrace("refreshNetworkServiceHourlyCounters: Exception print stack trace:=", e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[refreshNetworkServiceHourlyCounters]", "", "", "", "Exception:" + e.getMessage());
        }
    }

    /**
     * Method to update network Service Load Details
     * 
     */
    public static void updateNetworkServiceLoadDetails() {
        if (_log.isDebugEnabled())
            _log.debug("updateNetworkServiceLoadDetails", "Entered ");
        try {

            LoadControllerDAO loadControllerDAO = new LoadControllerDAO();
            Hashtable tempHash = loadControllerDAO.loadNetworkSeriveDetails(_instanceID);

            if (tempHash != null) {
                Enumeration size = tempHash.keys();
                while (size.hasMoreElements()) {
                    String key = (String) size.nextElement();
                    if (_log.isDebugEnabled())
                        _log.debug("updateNetworkServiceLoadDetails", "Got Key =" + key);
                    if (_networkServiceLoadHash != null && !_networkServiceLoadHash.containsKey(key)) {
                        if (_log.isDebugEnabled())
                            _log.debug("updateNetworkServiceLoadDetails", "Not found Key =" + key + " in _networkServiceLoadHash=" + _networkServiceLoadHash + " Thus inserting the same");
                        if (_networkServiceLoadHash == null)
                            _networkServiceLoadHash = new Hashtable();
                        _networkServiceLoadHash.put(key, tempHash.get(key));
                    }
                }
            }
            if (_log.isDebugEnabled())
                _log.debug("updateNetworkServiceLoadDetails", " After loading:" + _interfaceLoadHash.size());
        } catch (Exception e) {
            _log.error("updateNetworkServiceLoadDetails", "Exception " + e.getMessage());
            _log.errorTrace("updateNetworkServiceLoadDetails: Exception print stack trace:=", e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[updateNetworkServiceLoadDetails]", "", "", "", "Exception:" + e.getMessage());
        }
    }

    /**
     * Method to initialize Network Service Load
     * 
     */
    public static void initializeNetworkServiceLoad() {
        if (_log.isDebugEnabled())
            _log.debug("initializeNetworkServiceLoad", "Entered ");
        try {
            java.util.Date date = new Date();
            Timestamp t = new Timestamp(date.getTime());

            LoadControllerDAO loadControllerDAO = new LoadControllerDAO();
            Hashtable tempHash = loadControllerDAO.loadNetworkSeriveDetails(_instanceID);
            NetworkServiceLoadVO networkServiceLoadVO = null;

            if (tempHash != null) {
                Enumeration size = tempHash.keys();
                while (size.hasMoreElements()) {
                    String key = (String) size.nextElement();
                    if (_log.isDebugEnabled())
                        _log.debug("initializeNetworkServiceLoad", "Got Key =" + key);
                    if (_networkServiceLoadHash != null && _networkServiceLoadHash.containsKey(key)) {
                        networkServiceLoadVO = (NetworkServiceLoadVO) _networkServiceLoadHash.get(key);
                        networkServiceLoadVO.setRecievedCount(0);
                        networkServiceLoadVO.setAverageServiceTime(0);
                        networkServiceLoadVO.setBeforeGatewayFoundError(0);
                        networkServiceLoadVO.setLastInitializationTime(date);
                        networkServiceLoadVO.setLastReceievedTime(t);
                        networkServiceLoadVO.setBeforeNetworkFoundError(0);
                        networkServiceLoadVO.setBeforeServiceTypeFoundError(0);
                        networkServiceLoadVO.setFailCount(0);
                        networkServiceLoadVO.setLastRequestServiceTime(0);
                        networkServiceLoadVO.setOtherNetworkReqCount(0);
                        networkServiceLoadVO.setOthersFailCount(0);
                        networkServiceLoadVO.setSuccessCount(0);
                        networkServiceLoadVO.setUnderProcessCount(0);
                    } else {

                        if (_log.isDebugEnabled())
                            _log.debug("initializeNetworkServiceLoad", "Not found Key =" + key + " in _networkServiceLoadHash=" + _networkServiceLoadHash + " Thus inserting the same");
                        if (_networkServiceLoadHash == null)
                            _networkServiceLoadHash = new Hashtable();
                        _networkServiceLoadHash.put(key, tempHash.get(key));
                    }
                }
            }
            if (_log.isDebugEnabled())
                _log.debug("initializeNetworkServiceLoad", " After loading:" + _networkServiceLoadHash.size());
        } catch (Exception e) {
            _log.error("initializeNetworkServiceLoad", "Exception " + e.getMessage());
            _log.errorTrace("initializeNetworkServiceLoad: Exception print stack trace:=", e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[initializeNetworkServiceLoad]", "", "", "", "Exception:" + e.getMessage());
        }
    }

    /**
     * Method to initialize Network Service Load
     * 
     */
    public static void initializeNetworkServiceHourlyLoad() {
        if (_log.isDebugEnabled())
            _log.debug("initializeNetworkServiceHourlyLoad", "Entered ");
        try {
            java.util.Date date = new Date();
            Timestamp t = new Timestamp(date.getTime());

            LoadControllerDAO loadControllerDAO = new LoadControllerDAO();
            Hashtable tempHash = loadControllerDAO.loadNetworkServiceHourlyDetails(_instanceID);
            NetworkServiceHourlyLoadVO networkServiceHourlyLoadVO = null;

            if (tempHash != null) {
                Enumeration size = tempHash.keys();
                while (size.hasMoreElements()) {
                    String key = (String) size.nextElement();
                    if (_log.isDebugEnabled())
                        _log.debug("initializeNetworkServiceHourlyLoad", "Got Key =" + key);
                    if (_networkServiceHourlyLoadHash != null && _networkServiceHourlyLoadHash.containsKey(key)) {
                        networkServiceHourlyLoadVO = (NetworkServiceHourlyLoadVO) _networkServiceHourlyLoadHash.get(key);

                        networkServiceHourlyLoadVO.setLastInitializationTime(date);
                        networkServiceHourlyLoadVO.setLastReceievedTime(t);

                        networkServiceHourlyLoadVO.setFailCount(0);

                        networkServiceHourlyLoadVO.setSuccessCount(0);

                    } else {

                        if (_log.isDebugEnabled())
                            _log.debug("initializeNetworkServiceHourlyLoad", "Not found Key =" + key + " in _networkServiceHourlyLoadHash=" + _networkServiceHourlyLoadHash + " Thus inserting the same");
                        if (_networkServiceHourlyLoadHash == null)
                            _networkServiceHourlyLoadHash = new Hashtable();
                        _networkServiceHourlyLoadHash.put(key, tempHash.get(key));
                    }
                }
            }
            if (_log.isDebugEnabled())
                _log.debug("initializeNetworkServiceHourlyLoad", " After loading:" + _networkServiceHourlyLoadHash.size());
        } catch (Exception e) {
            _log.error("initializeNetworkServiceHourlyLoad", "Exception " + e.getMessage());
            _log.errorTrace("initializeNetworkServiceHourlyLoad: Exception print stack trace:=", e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[initializeNetworkServiceHourlyLoad]", "", "", "", "Exception:" + e.getMessage());
        }
    }

    /**
     * Method to initialize the network service load
     * 
     * @param p_instanceID
     * @param p_reqType
     * @param p_networkID
     * @param p_serviceType
     */
    public static void initializeNetworkServiceLoad(String p_instanceID, String p_reqType, String p_networkID, String p_serviceType) {
        if (_log.isDebugEnabled())
            _log.debug("initializeNetworkServiceLoad", "Entered: with p_instanceID=" + p_instanceID + " p_reqType=" + p_reqType + " p_networkID=" + p_networkID + "p_serviceType=" + p_serviceType);
        try {
            String loadType = null;
            java.util.Date date = new Date();
            Timestamp t = new Timestamp(date.getTime());

            if (_networkServiceLoadHash != null) {
                if (_log.isDebugEnabled())
                    _log.debug("initializeNetworkServiceLoad", " Before loading:" + _networkServiceLoadHash.size());
                Enumeration size = _networkServiceLoadHash.keys();
                while (size.hasMoreElements()) {
                    NetworkServiceLoadVO networkServiceLoadVO = (NetworkServiceLoadVO) _networkServiceLoadHash.get(size.nextElement());
                    String instanceID = networkServiceLoadVO.getInstanceID();
                    String reqType = networkServiceLoadVO.getGatewayType();
                    String networkID = networkServiceLoadVO.getNetworkCode();
                    String serviceType = networkServiceLoadVO.getServiceType();
                    if ("ALL".equals(p_instanceID) || instanceID.equals(p_instanceID)) {
                        if ("ALL".equals(p_networkID) || reqType.equals(p_networkID)) {
                            if ("ALL".equals(p_reqType) || networkID.equals(p_reqType)) {
                                if ("ALL".equals(p_serviceType) || serviceType.equals(p_serviceType)) {
                                    networkServiceLoadVO.setRecievedCount(0);
                                    networkServiceLoadVO.setAverageServiceTime(0);
                                    networkServiceLoadVO.setBeforeGatewayFoundError(0);
                                    networkServiceLoadVO.setLastInitializationTime(date);
                                    networkServiceLoadVO.setLastReceievedTime(t);
                                    networkServiceLoadVO.setBeforeNetworkFoundError(0);
                                    networkServiceLoadVO.setBeforeServiceTypeFoundError(0);
                                    networkServiceLoadVO.setFailCount(0);
                                    networkServiceLoadVO.setLastRequestServiceTime(0);
                                    networkServiceLoadVO.setOtherNetworkReqCount(0);
                                    networkServiceLoadVO.setOthersFailCount(0);
                                    networkServiceLoadVO.setSuccessCount(0);
                                    networkServiceLoadVO.setUnderProcessCount(0);
                                }
                            }
                        }
                    }
                }
                if (_log.isDebugEnabled())
                    _log.debug("initializeNetworkServiceLoad", " After loading:" + _networkServiceLoadHash.size());
            }
        } catch (Exception e) {
            _log.error("initializeNetworkServiceLoad", "Exception " + e.getMessage());
            _log.errorTrace("initializeNetworkServiceLoad: Exception print stack trace:=", e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[initializeNetworkServiceLoad]", "", "", "", "Exception:" + e.getMessage());
        }
    }

    /**
     * Method to initialize Network load
     * 
     * @param p_instanceID
     * @param p_networkID
     */
    public static void initializeNetworkLoad(String p_instanceID, String p_networkID) {
        if (_log.isDebugEnabled())
            _log.debug("initializeNetworkLoad", "Entered: with p_instanceID=" + p_instanceID + " p_networkID=" + p_networkID);
        try {
            String loadType = null;
            java.util.Date date = new Date();
            Timestamp t1 = new Timestamp(date.getTime());

            if (_networkLoadHash != null) {
                if (_log.isDebugEnabled())
                    _log.debug("initializeNetworkLoad", " Before loading:" + _networkLoadHash.size());
                Enumeration size = _networkLoadHash.keys();
                while (size.hasMoreElements()) {
                    NetworkLoadVO networkLoadVO = (NetworkLoadVO) _networkLoadHash.get(size.nextElement());
                    String instanceID = networkLoadVO.getInstanceID();
                    String networkID = networkLoadVO.getNetworkCode();
                    if ("ALL".equals(p_instanceID) || instanceID.equals(p_instanceID)) {
                        if ("ALL".equals(p_networkID) || networkID.equals(p_networkID)) {
                            networkLoadVO.setCurrentTransactionLoad(0);
                            networkLoadVO.setFirstRequestCount(0);
                            // networkLoadVO.setCurrentTPS(0);
                            networkLoadVO.setFirstRequestTime(0);
                            networkLoadVO.setLastInitializationTime(date);
                            networkLoadVO.setLastReceievedTime(t1);
                            networkLoadVO.setLastRefusedTime(t1);
                            networkLoadVO.setNoOfRequestSameSec(0);
                            networkLoadVO.setReceiverCurrentTransactionLoad(0);
                            networkLoadVO.setRecievedCount(0);
                            networkLoadVO.setRequestCount(0);
                            networkLoadVO.setRequestTimeoutSec(0);
                            networkLoadVO.setTotalRefusedCount(0);
                        }
                    }
                }
                if (_log.isDebugEnabled())
                    _log.debug("initializeNetworkLoad", " After loading:" + _networkLoadHash.size());
            }
        } catch (Exception e) {
            _log.error("initializeNetworkLoad", "Exception " + e.getMessage());
            _log.errorTrace("initializeNetworkLoad: Exception print stack trace:=", e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[initializeNetworkLoad]", "", "", "", "Exception:" + e.getMessage());
        }
    }

    /**
     * Method to initialize Interface load
     * 
     * @param p_instanceID
     * @param p_networkID
     * @param p_interfaceID
     */
    public static void initializeInterfaceLoad(String p_instanceID, String p_networkID, String p_interfaceID) {
        if (_log.isDebugEnabled())
            _log.debug("initializeInterfaceLoad", "Entered: with p_instanceID=" + p_instanceID + " p_networkID=" + p_networkID + "p_interfaceID=" + p_interfaceID);
        try {
            String loadType = null;
            java.util.Date date = new Date();
            Timestamp t = new Timestamp(date.getTime());

            if (_interfaceLoadHash != null) {
                if (_log.isDebugEnabled())
                    _log.debug("initializeInterfaceLoad", " Before loading:" + _interfaceLoadHash.size());
                Enumeration size = _interfaceLoadHash.keys();
                while (size.hasMoreElements()) {
                    InterfaceLoadVO interfaceLoadVO = (InterfaceLoadVO) _interfaceLoadHash.get(size.nextElement());
                    String instanceID = interfaceLoadVO.getInstanceID();
                    String networkID = interfaceLoadVO.getNetworkCode();
                    String interfaceID = interfaceLoadVO.getInterfaceID();
                    if ("ALL".equals(p_instanceID) || instanceID.equals(p_instanceID)) {
                        if ("ALL".equals(p_networkID) || networkID.equals(p_networkID)) {
                            if ("ALL".equals(p_interfaceID) || interfaceID.equals(p_interfaceID)) {
                                interfaceLoadVO.setCurrentTransactionLoad(0);
                                interfaceLoadVO.setFirstRequestCount(0);
                                // interfaceLoadVO.setCurrentTPS(0);
                                interfaceLoadVO.setFirstRequestTime(0);
                                interfaceLoadVO.setLastInitializationTime(date);
                                interfaceLoadVO.setLastReceievedTime(t);
                                interfaceLoadVO.setLastRefusedTime(t);
                                interfaceLoadVO.setNoOfRequestSameSec(0);
                                interfaceLoadVO.setReceiverCurrentTransactionLoad(0);
                                interfaceLoadVO.setRecievedCount(0);
                                interfaceLoadVO.setRequestCount(0);
                                interfaceLoadVO.setRequestTimeoutSec(0);
                                interfaceLoadVO.setTotalRefusedCount(0);
                                interfaceLoadVO.setCurrentQueueSize(0);
                                interfaceLoadVO.setLastQueueAdditionTime(t);
                                interfaceLoadVO.setLastQueueAdditionTime(t);
                                interfaceLoadVO.setLastQueueCaseCheckTime(0);
                                interfaceLoadVO.setLastReceievedTime(t);
                                interfaceLoadVO.setLastRefusedTime(t);
                                interfaceLoadVO.setLastTxnProcessStartTime(t);
                            }
                        }
                    }
                }
                if (_log.isDebugEnabled())
                    _log.debug("initializeInterfaceLoad", " After loading:" + _interfaceLoadHash.size());
            }
        } catch (Exception e) {
            _log.error("initializeInterfaceLoad", "Exception " + e.getMessage());
            _log.errorTrace("initializeInterfaceLoad: Exception print stack trace:=", e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[initializeInterfaceLoad]", "", "", "", "Exception:" + e.getMessage());
        }
    }

    /**
     * Method to initialize Transaction load
     * 
     * @param p_instanceID
     * @param p_networkID
     * @param p_interfaceID
     * @param p_serviceType
     */
    public static void initializeTransactionLoad(String p_instanceID, String p_networkID, String p_interfaceID, String p_serviceType) {
        if (_log.isDebugEnabled())
            _log.debug("initializeTransactionLoad", "Entered: with p_instanceID=" + p_instanceID + " p_networkID=" + p_networkID + "p_interfaceID=" + p_interfaceID + "p_serviceType=" + p_serviceType);
        try {
            String loadType = null;
            java.util.Date date = new Date();
            Timestamp t = new Timestamp(date.getTime());

            if (_transactionLoadHash != null) {
                if (_log.isDebugEnabled())
                    _log.debug("initializeTransactionLoad", " Before loading:" + _transactionLoadHash.size());
                Enumeration size = _transactionLoadHash.keys();
                TransactionLoadVO alternateTransactionLoadVO = null;
                while (size.hasMoreElements()) {
                    TransactionLoadVO transactionLoadVO = (TransactionLoadVO) _transactionLoadHash.get(size.nextElement());
                    String instanceID = transactionLoadVO.getInstanceID();
                    String networkID = transactionLoadVO.getNetworkCode();
                    String interfaceID = transactionLoadVO.getInterfaceID();
                    String serviceType = transactionLoadVO.getServiceType();
                    if ("ALL".equals(p_instanceID) || instanceID.equals(p_instanceID)) {
                        if ("ALL".equals(p_networkID) || networkID.equals(p_networkID)) {
                            if ("ALL".equals(p_interfaceID) || interfaceID.equals(p_interfaceID)) {
                                if ("ALL".equals(p_serviceType) || serviceType.equals(p_serviceType)) {
                                    transactionLoadVO.setCurrentTransactionLoad(0);
                                    transactionLoadVO.setFirstRequestCount(0);
                                    // transactionLoadVO.setCurrentTPS(0);
                                    transactionLoadVO.setFirstRequestTime(0);
                                    transactionLoadVO.setLastInitializationTime(date);
                                    transactionLoadVO.setLastReceievedTime(t);
                                    transactionLoadVO.setLastRefusedTime(t);
                                    transactionLoadVO.setNoOfRequestSameSec(0);
                                    transactionLoadVO.setReceiverCurrentTransactionLoad(0);
                                    transactionLoadVO.setRecievedCount(0);
                                    transactionLoadVO.setRequestCount(0);
                                    transactionLoadVO.setRequestTimeoutSec(0);
                                    transactionLoadVO.setTotalRefusedCount(0);
                                    transactionLoadVO.setLastReceievedTime(t);
                                    transactionLoadVO.setLastRefusedTime(t);
                                    transactionLoadVO.setLastTxnProcessStartTime(t);
                                    transactionLoadVO.setCurrentRecieverTopupCount(0);
                                    transactionLoadVO.setCurrentSenderTopupCount(0);
                                    transactionLoadVO.setCurrentRecieverValidationCount(0);
                                    transactionLoadVO.setCurrentSenderValidationCount(0);
                                    transactionLoadVO.setReceiverCurrentTransactionLoad(0);
                                    transactionLoadVO.setTotalInternalFailCount(0);
                                    transactionLoadVO.setTotalRecieverTopupCount(0);
                                    transactionLoadVO.setTotalRecieverTopupFailCount(0);
                                    transactionLoadVO.setTotalRecieverValFailCount(0);
                                    transactionLoadVO.setTotalRecieverValidationCount(0);
                                    transactionLoadVO.setTotalSenderTopupCount(0);
                                    transactionLoadVO.setTotalRecieverValidationCount(0);
                                    transactionLoadVO.setTotalSenderTopupFailCount(0);
                                    transactionLoadVO.setTotalSenderValFailCount(0);
                                    transactionLoadVO.setTotalSenderValidationCount(0);
                                    ArrayList alternateList = transactionLoadVO.getAlternateServiceLoadType();
                                    if (alternateList != null) {
                                        for (int i = 0; i < alternateList.size(); i++) {
                                            alternateTransactionLoadVO = (TransactionLoadVO) alternateList.get(i);
                                            alternateTransactionLoadVO.setCurrentTransactionLoad(0);
                                            alternateTransactionLoadVO.setFirstRequestCount(0);
                                            // alternateTransactionLoadVO.setCurrentTPS(0);
                                            alternateTransactionLoadVO.setFirstRequestTime(0);
                                            alternateTransactionLoadVO.setLastInitializationTime(date);
                                            alternateTransactionLoadVO.setLastReceievedTime(t);
                                            alternateTransactionLoadVO.setLastRefusedTime(t);
                                            alternateTransactionLoadVO.setNoOfRequestSameSec(0);
                                            alternateTransactionLoadVO.setReceiverCurrentTransactionLoad(0);
                                            alternateTransactionLoadVO.setRecievedCount(0);
                                            alternateTransactionLoadVO.setRequestCount(0);
                                            alternateTransactionLoadVO.setRequestTimeoutSec(0);
                                            alternateTransactionLoadVO.setTotalRefusedCount(0);
                                            alternateTransactionLoadVO.setLastReceievedTime(t);
                                            alternateTransactionLoadVO.setLastRefusedTime(t);
                                            alternateTransactionLoadVO.setLastTxnProcessStartTime(t);
                                            alternateTransactionLoadVO.setCurrentRecieverTopupCount(0);
                                            alternateTransactionLoadVO.setCurrentSenderTopupCount(0);
                                            alternateTransactionLoadVO.setCurrentRecieverValidationCount(0);
                                            alternateTransactionLoadVO.setCurrentSenderValidationCount(0);
                                            alternateTransactionLoadVO.setReceiverCurrentTransactionLoad(0);
                                            alternateTransactionLoadVO.setTotalInternalFailCount(0);
                                            alternateTransactionLoadVO.setTotalRecieverTopupCount(0);
                                            alternateTransactionLoadVO.setTotalRecieverTopupFailCount(0);
                                            alternateTransactionLoadVO.setTotalRecieverValFailCount(0);
                                            alternateTransactionLoadVO.setTotalRecieverValidationCount(0);
                                            alternateTransactionLoadVO.setTotalSenderTopupCount(0);
                                            alternateTransactionLoadVO.setTotalRecieverValidationCount(0);
                                            alternateTransactionLoadVO.setTotalSenderTopupFailCount(0);
                                            alternateTransactionLoadVO.setTotalSenderValFailCount(0);
                                            alternateTransactionLoadVO.setTotalSenderValidationCount(0);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (_log.isDebugEnabled())
                    _log.debug("initializeTransactionLoad", " After loading:" + _transactionLoadHash.size());
            }
        } catch (Exception e) {
            _log.error("initializeTransactionLoad", "Exception " + e.getMessage());
            _log.errorTrace("initializeTransactionLoad: Exception print stack trace:=", e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[initializeTransactionLoad]", "", "", "", "Exception:" + e.getMessage());
        }
    }

    /**
     * Method to update particular Network Load
     * 
     * @param p_instanceID
     * @param p_networkID
     */
    public static void updateNetworkLoadDetails(String p_instanceID, String p_networkID) {
        if (_log.isDebugEnabled())
            _log.debug("updateNetworkLoadDetails", "Entered: with p_instanceID=" + p_instanceID + " p_networkID=" + p_networkID);
        Connection con = null;
        try {
            String loadType = null;
            if (_networkLoadHash != null) {
                if (_log.isDebugEnabled())
                    _log.debug("updateNetworkLoadDetails", " Before loading:" + _networkLoadHash.size());
                Enumeration size = _networkLoadHash.keys();
                con = OracleUtil.getConnection();
                while (size.hasMoreElements()) {
                    NetworkLoadVO networkLoadVO = (NetworkLoadVO) _networkLoadHash.get(size.nextElement());
                    String instanceID = networkLoadVO.getInstanceID();
                    String networkID = networkLoadVO.getNetworkCode();
                    if ("ALL".equals(p_instanceID) || instanceID.equals(p_instanceID)) {
                        if ("ALL".equals(p_networkID) || networkID.equals(p_networkID)) {
                            new LoadControllerDAO().updateNetworkLoadDetails(con, networkLoadVO, instanceID, networkID, loadType);
                        }
                    }
                }
                if (_log.isDebugEnabled())
                    _log.debug("updateNetworkLoadDetails", " After loading:" + _networkLoadHash.size());
            }
        } catch (Exception e) {
            _log.error("updateNetworkLoadDetails", "Exception " + e.getMessage());
            _log.errorTrace("updateNetworkLoadDetails: Exception print stack trace:=", e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[updateNetworkLoadDetails]", "", "", "", "Exception:" + e.getMessage());
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                }
                ;
            }
        }
    }

    /**
     * Method to load particular Interface Load
     * 
     * @param p_instanceID
     * @param p_networkID
     * @param p_interfaceID
     */
    public static void updateInterfaceLoadDetails(String p_instanceID, String p_networkID, String p_interfaceID) {
        if (_log.isDebugEnabled())
            _log.debug("updateInterfaceLoadDetails", "Entered: with p_instanceID=" + p_instanceID + " p_networkID=" + p_networkID + "p_interfaceID=" + p_interfaceID);
        Connection con = null;
        try {
            String loadType = null;
            if (_interfaceLoadHash != null) {
                if (_log.isDebugEnabled())
                    _log.debug("updateInterfaceLoadDetails", " Before loading:" + _interfaceLoadHash.size());
                Enumeration size = _interfaceLoadHash.keys();
                con = OracleUtil.getConnection();
                while (size.hasMoreElements()) {
                    InterfaceLoadVO interfaceLoadVO = (InterfaceLoadVO) _interfaceLoadHash.get(size.nextElement());
                    String instanceID = interfaceLoadVO.getInstanceID();
                    String networkID = interfaceLoadVO.getNetworkCode();
                    String interfaceID = interfaceLoadVO.getInterfaceID();
                    if ("ALL".equals(p_instanceID) || instanceID.equals(p_instanceID)) {
                        if ("ALL".equals(p_networkID) || networkID.equals(p_networkID)) {
                            if ("ALL".equals(p_interfaceID) || interfaceID.equals(p_interfaceID)) {
                                new LoadControllerDAO().updateInterfaceLoadDetails(con, interfaceLoadVO, instanceID, networkID, interfaceID, loadType);
                            }
                        }
                    }
                }
                if (_log.isDebugEnabled())
                    _log.debug("updateInterfaceLoadDetails", " After loading:" + _interfaceLoadHash.size());
            }
        } catch (Exception e) {
            _log.error("updateInterfaceLoadDetails", "Exception " + e.getMessage());
            _log.errorTrace("updateInterfaceLoadDetails: Exception print stack trace:=", e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[updateInterfaceLoadDetails]", "", "", "", "Exception:" + e.getMessage());
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                }
                ;
            }
        }
    }

    /**
     * Method to load particular Transaction Load
     * 
     * @param p_instanceID
     * @param p_networkID
     * @param p_interfaceID
     * @param p_serviceType
     */
    public static void updateTransactionLoadDetails(String p_instanceID, String p_networkID, String p_interfaceID, String p_serviceType) {
        if (_log.isDebugEnabled())
            _log.debug("updateTransactionLoadDetails", "Entered: with p_instanceID=" + p_instanceID + " p_networkID=" + p_networkID + "p_interfaceID=" + p_interfaceID + "p_serviceType=" + p_serviceType);
        Connection con = null;
        try {
            String loadType = null;
            if (_transactionLoadHash != null) {
                if (_log.isDebugEnabled())
                    _log.debug("updateTransactionLoadDetails", " Before loading:" + _transactionLoadHash.size());
                Enumeration size = _transactionLoadHash.keys();
                con = OracleUtil.getConnection();
                while (size.hasMoreElements()) {
                    TransactionLoadVO transactionLoadVO = (TransactionLoadVO) _transactionLoadHash.get(size.nextElement());
                    String instanceID = transactionLoadVO.getInstanceID();
                    String networkID = transactionLoadVO.getNetworkCode();
                    String interfaceID = transactionLoadVO.getInterfaceID();
                    String serviceType = transactionLoadVO.getServiceType();
                    if (_log.isDebugEnabled())
                        _log.debug("updateTransactionLoadDetails", " instanceID=" + instanceID + " networkID=" + networkID + " interfaceID=" + interfaceID + " serviceType=" + serviceType);

                    if ("ALL".equals(p_instanceID) || instanceID.equals(p_instanceID)) {
                        if ("ALL".equals(p_networkID) || networkID.equals(p_networkID)) {
                            if ("ALL".equals(p_interfaceID) || interfaceID.equals(p_interfaceID)) {
                                if ("ALL".equals(p_serviceType) || serviceType.equals(p_serviceType)) {
                                    new LoadControllerDAO().updateTransactionLoadDetails(con, transactionLoadVO, instanceID, networkID, interfaceID, serviceType, loadType);
                                }
                            }
                        }
                    }
                }
                if (_log.isDebugEnabled())
                    _log.debug("updateTransactionLoadDetails", " After loading:" + _transactionLoadHash.size());
            }
        } catch (Exception e) {
            _log.error("updateTransactionLoadDetails", "Exception " + e.getMessage());
            _log.errorTrace("updateTransactionLoadDetails: Exception print stack trace:=", e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LoadControllerCache[updateTransactionLoadDetails]", "", "", "", "Exception:" + e.getMessage());
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                }
                ;
            }
        }
    }

    /**
     * Method read.
     * This method will read the object's last state from file .
     * 
     * @param file
     * @author nitin.rohilla
     * @return Hashtable
     */
    public static Hashtable read(File file) {
        Hashtable hashRead = null;
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            hashRead = (Hashtable) in.readObject();
            in.close();
        } catch (Exception e) {
            _log.error("updateTransactionLoadDetails", "Exception While Reading Object From File Exception " + e.getMessage());
            _log.errorTrace("Hashtable: Exception print stack trace:=", e);
        }
        return hashRead;
    }

    /**
     * Method modifyInstanceLoadVO
     * this method is used to modify the InstanceLoadVO
     * 
     * @param Hashtable
     * @param Hashtable
     * @author amit.arora
     */
    private static void modifyInstanceLoadVO(Hashtable p_instanceLoad, Hashtable p_instanceHashRead) {

        Set keySet1 = p_instanceLoad.keySet();
        Iterator key1 = keySet1.iterator();
        InstanceLoadVO instanceLoadVO1 = null;
        InstanceLoadVO instanceLoadVO2 = null;
        String tempKey1 = null;
        while (key1.hasNext()) {
            tempKey1 = (String) key1.next();
            if (p_instanceHashRead.containsKey(tempKey1)) {
                instanceLoadVO1 = (InstanceLoadVO) p_instanceLoad.get(tempKey1);
                instanceLoadVO2 = (InstanceLoadVO) p_instanceHashRead.get(tempKey1);
                instanceLoadVO1.setRequestCount(instanceLoadVO2.getRequestCount());
                instanceLoadVO1.setTotalRefusedCount(instanceLoadVO2.getTotalRefusedCount());
                instanceLoadVO1.setLastReceievedTime(instanceLoadVO2.getLastReceievedTime());
                instanceLoadVO1.setLastRefusedTime(instanceLoadVO2.getLastRefusedTime());
                instanceLoadVO1.setLastTxnProcessStartTime(instanceLoadVO2.getLastTxnProcessStartTime());
                instanceLoadVO1.setRecievedCount(instanceLoadVO2.getRecievedCount());
                // instanceLoadVO1.setCurrentStatus(instanceLoadVO2.getCurrentStatus());
            }
        }
    }

    /**
     * Method modifyNetworkLoadVO
     * this method is used to modify the NetworkLoadVO
     * 
     * @param Hashtable
     * @param Hashtable
     * @author amit.arora
     */

    private static void modifyNetworkLoadVO(Hashtable p_networkLoad, Hashtable p_networkHashRead) {
        Set keySet1 = p_networkLoad.keySet();
        Iterator key1 = keySet1.iterator();
        NetworkLoadVO networkVO1 = null;
        NetworkLoadVO networkVO2 = null;
        String tempKey1 = null;
        while (key1.hasNext()) {
            tempKey1 = (String) key1.next();
            if (p_networkHashRead.containsKey(tempKey1)) {
                networkVO1 = (NetworkLoadVO) p_networkLoad.get(tempKey1);
                networkVO2 = (NetworkLoadVO) p_networkHashRead.get(tempKey1);
                networkVO1.setTotalRefusedCount(networkVO2.getTotalRefusedCount());
                networkVO1.setLastReceievedTime(networkVO2.getLastReceievedTime());
                networkVO1.setRequestCount(networkVO2.getRequestCount());
                networkVO1.setLastRefusedTime(networkVO2.getLastRefusedTime());
                networkVO1.setLastTxnProcessStartTime(networkVO2.getLastTxnProcessStartTime());
                networkVO1.setRecievedCount(networkVO2.getRecievedCount());
                // networkVO1.setC2sInstanceID(networkVO2.getC2sInstanceID());
                // networkVO1.setP2pInstanceID(networkVO2.getP2pInstanceID());
            }
        }
    }

    /**
     * Method modifyInterfaceLoadVO
     * this method is used to modify the InterfaceLoadVO
     * 
     * @param Hashtable
     * @param Hashtable
     * @author amit.arora
     */
    private static void modifyInterfaceLoadVO(Hashtable p_interfaceLoad, Hashtable p_interfaceHashRead) {
        Set keySet1 = p_interfaceLoad.keySet();
        Iterator key1 = keySet1.iterator();
        InterfaceLoadVO interfaceLoadVO1 = null;
        InterfaceLoadVO interfaceLoadVO2 = null;
        String tempKey1 = null;
        while (key1.hasNext()) {
            tempKey1 = (String) key1.next();
            if (p_interfaceHashRead.containsKey(tempKey1)) {
                interfaceLoadVO1 = (InterfaceLoadVO) p_interfaceLoad.get(tempKey1);
                interfaceLoadVO2 = (InterfaceLoadVO) p_interfaceHashRead.get(tempKey1);
                interfaceLoadVO1.setRecievedCount(interfaceLoadVO2.getRecievedCount());
                // interfaceLoadVO1.setCurrentTransactionLoad(interfaceLoadVO2.getCurrentTransactionLoad());
                // interfaceLoadVO1.setCurrentQueueSize(interfaceLoadVO2.getCurrentQueueSize());
                // interfaceLoadVO1.setNoOfRequestSameSec(interfaceLoadVO2.getNoOfRequestSameSec());
                interfaceLoadVO1.setRequestCount(interfaceLoadVO2.getRequestCount());
                interfaceLoadVO1.setTotalRefusedCount(interfaceLoadVO2.getTotalRefusedCount());
                interfaceLoadVO1.setLastReceievedTime(interfaceLoadVO2.getLastReceievedTime());
                interfaceLoadVO1.setLastRefusedTime(interfaceLoadVO2.getLastRefusedTime());
                interfaceLoadVO1.setLastTxnProcessStartTime(interfaceLoadVO2.getLastTxnProcessStartTime());
                interfaceLoadVO1.setLastQueueAdditionTime(interfaceLoadVO2.getLastQueueAdditionTime());
            }
        }
    }

    /**
     * Method modifyTransactionLoadVO
     * this method is used to modify the TransactionLoadVO
     * 
     * @param Hashtable
     * @param Hashtable
     * @author amit.arora
     */
    private static void modifyTransactionLoadVO(Hashtable p_transactionLoadVO, Hashtable p_transactionHashRead) {

        Set keySet1 = p_transactionLoadVO.keySet();
        Iterator key1 = keySet1.iterator();
        TransactionLoadVO transactionLoadVO1 = null;
        TransactionLoadVO transactionLoadVO2 = null;
        String tempKey1 = null;
        while (key1.hasNext()) {
            tempKey1 = (String) key1.next();
            if (p_transactionHashRead.containsKey(tempKey1)) {
                transactionLoadVO1 = (TransactionLoadVO) p_transactionLoadVO.get(tempKey1);
                transactionLoadVO2 = (TransactionLoadVO) p_transactionHashRead.get(tempKey1);
                transactionLoadVO1.setRecievedCount(transactionLoadVO2.getRecievedCount());
                // transactionLoadVO1.setCurrentTransactionLoad(transactionLoadVO2.getCurrentTransactionLoad());
                // transactionLoadVO1.setNoOfRequestSameSec(transactionLoadVO2.getNoOfRequestSameSec());
                transactionLoadVO1.setRequestCount(transactionLoadVO2.getRequestCount());
                transactionLoadVO1.setTotalRefusedCount(transactionLoadVO2.getTotalRefusedCount());
                // transactionLoadVO1.setDefinedOverFlowCount(transactionLoadVO2.getDefinedOverFlowCount());
                // transactionLoadVO1.setOverFlowCount(transactionLoadVO2.getOverFlowCount());
                transactionLoadVO1.setTotalSenderValidationCount(transactionLoadVO2.getTotalSenderValidationCount());
                // transactionLoadVO1.setCurrentRecieverValidationCount(transactionLoadVO2.getCurrentSenderValidationCount());
                transactionLoadVO1.setTotalRecieverValidationCount(transactionLoadVO2.getTotalRecieverValidationCount());
                // transactionLoadVO1.setCurrentRecieverValidationCount(transactionLoadVO2.getCurrentRecieverValidationCount());
                transactionLoadVO1.setTotalSenderTopupCount(transactionLoadVO2.getTotalSenderTopupCount());
                // transactionLoadVO1.setCurrentSenderTopupCount(transactionLoadVO2.getCurrentSenderTopupCount());
                transactionLoadVO1.setTotalRecieverTopupCount(transactionLoadVO2.getTotalRecieverTopupCount());
                // transactionLoadVO1.setCurrentRecieverValidationCount(transactionLoadVO2.getCurrentRecieverValidationCount());
                transactionLoadVO1.setTotalInternalFailCount(transactionLoadVO2.getTotalInternalFailCount());
                transactionLoadVO1.setTotalSenderValFailCount(transactionLoadVO2.getTotalSenderValFailCount());
                transactionLoadVO1.setTotalRecieverValFailCount(transactionLoadVO2.getTotalRecieverValFailCount());
                transactionLoadVO1.setTotalSenderTopupFailCount(transactionLoadVO2.getTotalSenderTopupFailCount());
                transactionLoadVO1.setTotalRecieverTopupFailCount(transactionLoadVO2.getTotalRecieverTopupFailCount());
                transactionLoadVO1.setLastReceievedTime(transactionLoadVO2.getLastReceievedTime());
                transactionLoadVO1.setLastRefusedTime(transactionLoadVO2.getLastRefusedTime());
                transactionLoadVO1.setLastTxnProcessStartTime(transactionLoadVO2.getLastTxnProcessStartTime());
            }
        }

    }

    /**
     * Method modifyNetworkServiceLoadVO
     * this method is used to modify the NetworkServiceLoadVO
     * 
     * @param Hashtable
     * @param Hashtable
     * @author amit.arora
     */

    private static void modifyNetworkServiceLoadVO(Hashtable p_networkServiceLoad, Hashtable p_networkServiceLoadHashRead) {
        Set keySet1 = p_networkServiceLoad.keySet();
        Iterator key1 = keySet1.iterator();
        NetworkServiceLoadVO networkServiceLoadVO1 = null;
        NetworkServiceLoadVO networkServiceLoadVO2 = null;
        String tempKey1 = null;
        while (key1.hasNext()) {
            tempKey1 = (String) key1.next();
            if (p_networkServiceLoadHashRead.containsKey(tempKey1)) {
                networkServiceLoadVO1 = (NetworkServiceLoadVO) p_networkServiceLoad.get(tempKey1);
                networkServiceLoadVO2 = (NetworkServiceLoadVO) p_networkServiceLoadHashRead.get(tempKey1);
                networkServiceLoadVO1.setServiceType(networkServiceLoadVO2.getServiceType());
                networkServiceLoadVO1.setGatewayType(networkServiceLoadVO2.getGatewayType());
                networkServiceLoadVO1.setServiceName(networkServiceLoadVO2.getServiceName());
                networkServiceLoadVO1.setRecievedCount(networkServiceLoadVO2.getRecievedCount());
                networkServiceLoadVO1.setSuccessCount(networkServiceLoadVO2.getSuccessCount());
                networkServiceLoadVO1.setFailCount(networkServiceLoadVO2.getFailCount());
                networkServiceLoadVO1.setUnderProcessCount(networkServiceLoadVO2.getUnderProcessCount());
                networkServiceLoadVO1.setOthersFailCount(networkServiceLoadVO2.getOthersFailCount());
                networkServiceLoadVO1.setOtherNetworkReqCount(networkServiceLoadVO2.getOtherNetworkReqCount());
                networkServiceLoadVO1.setBeforeGatewayFoundError(networkServiceLoadVO2.getBeforeGatewayFoundError());
                networkServiceLoadVO1.setBeforeNetworkFoundError(networkServiceLoadVO2.getBeforeNetworkFoundError());
                networkServiceLoadVO1.setBeforeServiceTypeFoundError(networkServiceLoadVO2.getBeforeServiceTypeFoundError());
                networkServiceLoadVO1.setLastReceievedTime(networkServiceLoadVO2.getLastReceievedTime());
                networkServiceLoadVO1.setAverageServiceTime(networkServiceLoadVO2.getAverageServiceTime());
                networkServiceLoadVO1.setLastRequestServiceTime(networkServiceLoadVO2.getLastRequestServiceTime());
                networkServiceLoadVO1.setLastRequestID(networkServiceLoadVO2.getLastRequestID());
            }
        }
    }

    /**
     * Method modifyNetworkServiceHourlyLoadVO
     * this method is used to modify the NetworkServiceHourlyLoadVO
     * 
     * @param Hashtable
     * @param Hashtable
     * @author amit.arora
     */

    private static void modifyNetworkServiceHourlyLoadVO(Hashtable p_networkServiceHourlyLoad, Hashtable p_networkServiceLoadHourlyHashRead) {
        Set keySet1 = p_networkServiceHourlyLoad.keySet();
        Iterator key1 = keySet1.iterator();
        NetworkServiceHourlyLoadVO networkServiceHourlyLoadVO1 = null;
        NetworkServiceHourlyLoadVO networkServiceHourlyLoadVO2 = null;
        String tempKey1 = null;
        while (key1.hasNext()) {
            tempKey1 = (String) key1.next();
            if (p_networkServiceLoadHourlyHashRead.containsKey(tempKey1)) {
                networkServiceHourlyLoadVO1 = (NetworkServiceHourlyLoadVO) p_networkServiceHourlyLoad.get(tempKey1);
                networkServiceHourlyLoadVO2 = (NetworkServiceHourlyLoadVO) p_networkServiceLoadHourlyHashRead.get(tempKey1);
                networkServiceHourlyLoadVO1.setServiceType(networkServiceHourlyLoadVO2.getServiceType());
                networkServiceHourlyLoadVO1.setGatewayType(networkServiceHourlyLoadVO2.getGatewayType());
                networkServiceHourlyLoadVO1.setServiceName(networkServiceHourlyLoadVO2.getServiceName());
                networkServiceHourlyLoadVO1.setSuccessCount(networkServiceHourlyLoadVO2.getSuccessCount());
                networkServiceHourlyLoadVO1.setFailCount(networkServiceHourlyLoadVO2.getFailCount());
                networkServiceHourlyLoadVO1.setLastReceievedTime(networkServiceHourlyLoadVO2.getLastReceievedTime());
                networkServiceHourlyLoadVO1.setLastRequestID(networkServiceHourlyLoadVO2.getLastRequestID());
            }
        }
    }

    /**
     * Method writeToFile.
     * This method will save the object's latest state into file .
     * 
     * @author nitin.rohilla
     * @return void
     */
    public static void writeToFile() {
        if (_log.isDebugEnabled())
            _log.debug("writeToFile", " Entered ");
        String path = Constants.getProperty("FILE_PATH");
        File pathDir = new File(path);
        if (!pathDir.exists())
            if (!pathDir.mkdirs()) {
                _log.error("writeToFile", "Unable to create Network counters storage directory");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "LoadControllerCache", "", "", "", "Unable to create Network counters storage directory");
            }

        // writing object for Request COUNTER
        try {
            Hashtable networkServiceLoadHashWrite = LoadControllerCache.getNetworkServiceLoadHash();
            File file = new File(path + Constants.getProperty("NETWORK_SERVICE_COUNTER_REPORT"));
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutput out = new ObjectOutputStream(fos);
            out.writeObject(networkServiceLoadHashWrite);
            out.close();
            if (_log.isDebugEnabled())
                _log.debug("writeToFile", " File Created file: " + file.getAbsolutePath());
        } catch (Exception e) {
            _log.errorTrace("writeToFile: Exception print stack trace:=", e);
        }
        // writing object for INSTANCE COUNTER
        try {
            Hashtable instanceHashWrite = LoadControllerCache.getInstanceLoadHash();
            File file = new File(path + Constants.getProperty("INSTANCE_COUNTER_REPORT"));
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutput out = new ObjectOutputStream(fos);
            out.writeObject(instanceHashWrite);
            out.close();
            if (_log.isDebugEnabled())
                _log.debug("writeToFile", " File Created file: " + file.getAbsolutePath());
        } catch (Exception e) {
            _log.errorTrace("writeToFile: Exception print stack trace:=", e);
        }
        // Writing Object For Transaction Counter
        try {
            Hashtable transactionHashWrite = LoadControllerCache.getTransactionLoadHash();
            File file = new File(path + Constants.getProperty("TRANSACTION_COUNTER_REPORT"));
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutput out = new ObjectOutputStream(fos);
            out.writeObject(transactionHashWrite);
            out.close();
            if (_log.isDebugEnabled())
                _log.debug("writeToFile", " File Created file: " + file.getAbsolutePath());
        } catch (Exception e) {
            _log.errorTrace("writeToFile: Exception print stack trace:=", e);
        }
        // Writing Object For Network(Location) Counter
        try {
            Hashtable networkHashWrite = LoadControllerCache.getNetworkLoadHash();
            File file = new File(path + Constants.getProperty("NETWORK_COUNTER_REPORT"));
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutput out = new ObjectOutputStream(fos);
            out.writeObject(networkHashWrite);
            out.close();
            if (_log.isDebugEnabled())
                _log.debug("writeToFile", " File Created file: " + file.getAbsolutePath());
        } catch (Exception e) {
            _log.errorTrace("writeToFile: Exception print stack trace:=", e);
        }
        // Writing Object For Interface Counter
        try {
            Hashtable interfaceHashWrite = LoadControllerCache.getInterfaceLoadHash();
            File file = new File(path + Constants.getProperty("INTERFACE_COUNTER_REPORT"));
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutput out = new ObjectOutputStream(fos);
            out.writeObject(interfaceHashWrite);
            out.close();
            if (_log.isDebugEnabled())
                _log.debug("writeToFile", " File Created file: " + file.getAbsolutePath());
        } catch (Exception e) {
            _log.errorTrace("writeToFile: Exception print stack trace:=", e);
        }

        // writing object for Hourly Request COUNTER
        try {
            Hashtable networkServiceHourlyLoadHashWrite = LoadControllerCache.getNetworkServiceHourlyLoadHash();
            File file = new File(path + Constants.getProperty("NETWORK_SERVICE_HOURLY_COUNTER_REPORT"));
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutput out = new ObjectOutputStream(fos);
            out.writeObject(networkServiceHourlyLoadHashWrite);
            out.close();
            if (_log.isDebugEnabled())
                _log.debug("writeToFile", " File Created file: " + file.getAbsolutePath());
        } catch (Exception e) {
            _log.errorTrace("writeToFile: Exception print stack trace:=", e);
        }

    }

}
