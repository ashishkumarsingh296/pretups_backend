package com.inter.huaweicitycell;

/**
 * @(#)HuaweiPoolManager.java
 *                            Copyright(c) 2009, Bharti Telesoft Int. Public
 *                            Ltd.
 *                            All Rights Reserved
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Author Date History
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Vipan Nov 16, 2010 Initial Creation
 *                            --------------------------------------------------
 *                            ---------------------------------------------
 *                            This class is responsible to instantiate the
 *                            Client object and maintain a pool.
 */
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceUtil;

public class HuaweiPoolManager {
    private static Log _log = LogFactory.getLog(HuaweiPoolManager.class.getName());
    public static HashMap<String, Vector<Object>> _freeBucket = new HashMap<String, Vector<Object>>();// Contains
                                                                                                      // the
                                                                                                      // list
                                                                                                      // of
                                                                                                      // free
                                                                                                      // client
                                                                                                      // objects,associated
                                                                                                      // with
                                                                                                      // interface
                                                                                                      // id.
    public static HashMap<String, Vector<Object>> _busyBucket = new HashMap<String, Vector<Object>>();// Contains
                                                                                                      // the
                                                                                                      // list
                                                                                                      // of
                                                                                                      // busy
                                                                                                      // client
                                                                                                      // objects,associated
                                                                                                      // with
                                                                                                      // interface
                                                                                                      // id.
    private static HuaweiHeartBeatController _huaweiHeartBeatController = null;
    public static long _lastPoolInitializationTime = System.currentTimeMillis();// Defines
                                                                                // the
                                                                                // last
                                                                                // initialization
                                                                                // time
                                                                                // when
                                                                                // pool
                                                                                // was
                                                                                // initialized.
    public static boolean _isPoolInitializing = false;

    /**
     * This method is responsible to store the instance of Client objects into a
     * HashMap with interface id as Key.
     * 
     * @param String
     *            p_interfaceIDs
     * @throws BTSLBaseException
     */
    public static void initialize(String p_interfaceIDs) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("initialize", "Entered p_interfaceIDs::" + p_interfaceIDs);
        String interfaceId = null;
        Object object = null;
        int totalPoolSize = 0, poolSizePerPort = 0;
        long poolSleep = 0;
        String[] inStrArray = null, inPortArray = null;
        Vector<Object> objectList = null;// To synchronizes the pooled objects
        try {
            _isPoolInitializing = true;
            inStrArray = p_interfaceIDs.split(",");
            if (InterfaceUtil.isNullArray(inStrArray))
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_OBJECT_POOL_INITIALIZATION);

            // Confirm while creating instances,if any error occurs for an
            // interface,
            // should we stop the process with handling the event and throw
            // exception
            // Or only event should be handled corresponding to that interface
            // and continue to other.
            for (int i = 0, size = inStrArray.length; i < size; i++) {
                // Create a list in which store the client object equal to
                // configured number.
                interfaceId = inStrArray[i].trim();
                String heartBeatPoolConnectionAllowedStr = FileCache.getValue(interfaceId, "HEARTBEAT_POOL_CONNECTION_ALLOWED");
                if (InterfaceUtil.isNullString(heartBeatPoolConnectionAllowedStr)) {
                    _log.error("HuaweiHeartBeat[Constructor]", "heartBeatPoolConnectionAllowedStr is not defined in the INFile");
                    heartBeatPoolConnectionAllowedStr = "Y";
                }
                if ("N".equalsIgnoreCase(heartBeatPoolConnectionAllowedStr))
                    return;
                try {
                    // Get the Max pool size from the INFile.
                    totalPoolSize = Integer.parseInt(FileCache.getValue(interfaceId, "MAX_POOL_SIZE"));
                    poolSizePerPort = Integer.parseInt(FileCache.getValue(interfaceId, "POOL_SIZE_PER_PORT"));
                    poolSleep = Long.parseLong(FileCache.getValue(interfaceId, "POOL_SLEEP"));

                } catch (Exception e) {
                    e.printStackTrace();
                    _log.error("initialize", "Exception e:" + e.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiPoolManager[initialize]", "", "", "", "While instantiation of the Client objects MAX_POOL_SIZE/POOL_SLEEP is either not defined or not numeric, in the INFile");
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_OBJECT_POOL_INITIALIZATION);
                }

                String ip = FileCache.getValue(interfaceId, "SOCKET_IP");
                if (_log.isDebugEnabled())
                    _log.debug("HuaweiPoolManager[initialize]", "ip :: " + ip);
                if (InterfaceUtil.isNullString(ip)) {
                    _log.error("HuaweiPoolManager[instantiation]", "SOCKET_IP is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiPoolManager[initialize]", "", "_interfaceID:" + interfaceId, "", "SOCKET_IP is not defined in the INFile");
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);

                }
                String localSocketIP = FileCache.getValue(interfaceId, "LOCAL_SOCKET_IP");
                if (_log.isDebugEnabled())
                    _log.debug("HuaweiPoolManager[initialize]", "ip :: " + ip);
                if (InterfaceUtil.isNullString(localSocketIP)) {
                    _log.error("HuaweiPoolManager[instantiation]", "LOCAL_SOCKET_IP is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiPoolManager[initialize]", "", "_interfaceID:" + interfaceId, "", "LOCAL_SOCKET_IP is not defined in the INFile");
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);

                }
                String localSocketIPArray[] = localSocketIP.split(",");
                if (localSocketIPArray != null) {
                    if (localSocketIPArray.length > 1)
                        poolSizePerPort = localSocketIPArray.length;

                }
                if (InterfaceUtil.isNullString(localSocketIP)) {
                    _log.error("HuaweiPoolManager[instantiation]", "LOCAL_SOCKET_PORT is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiPoolManager[initialize]", "", "_interfaceID:" + interfaceId, "", "LOCAL_SOCKET_PORT is not defined in the INFile");
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);

                }
                // Fetch the Port from the INFile.
                String portStr = FileCache.getValue(interfaceId, "SOCKET_PORT");
                if (_log.isDebugEnabled())
                    _log.debug("HuaweiPoolManager[initialize]", "portStr :: " + portStr);
                if (InterfaceUtil.isNullString(portStr)) {
                    _log.error("HuaweiPoolManager[initialize]", "SOCKET_PORT is either not defined in the INFile or its value is not numeric");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiPoolManager[initialize]", "", "_interfaceID:" + interfaceId, "", "SOCKET_PORT is either not defined in the INFile or its value is not numeric");
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
                }
                inPortArray = portStr.split(",");
                objectList = new Vector<Object>(totalPoolSize);
                for (int k = 0; k < inPortArray.length; k++) {
                    if (InterfaceUtil.isNullString(inPortArray[k]) || !InterfaceUtil.isNumeric(inPortArray[k].trim())) {
                        _log.error("HuaweiPoolManager[initialize]", "SOCKET_PORT is either not defined in the INFile or its value is not numeric");
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiPoolManager[initialize]", "", "_interfaceID:" + interfaceId, "", "SOCKET_PORT is either not defined in the INFile or its value is not numeric");
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
                    }
                    for (int loop = 0; loop < poolSizePerPort; loop++) {
                        try {
                            object = new HuaweiSocketWrapper(interfaceId, ip, inPortArray[k], InetAddress.getByName(localSocketIPArray[loop]), Integer.parseInt(inPortArray[k]));
                            objectList.add(object);
                            Thread.sleep(poolSleep);
                        } catch (Exception e) {
                            e.printStackTrace();
                            _log.error("initialize", "Exception e:" + e.getMessage());
                            EventHandler.handle(EventIDI.RETRY_ATTEMPT_FAILED, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiPoolManager[initialize]", "", "", "", "While instantiation of the Client objects got the Exception " + e.getMessage());
                            // throw new
                            // BTSLBaseException(InterfaceErrorCodesI.ERROR_OBJECT_POOL_INITIALIZATION);
                        }
                    }// end of for loop
                }
                _freeBucket.put(interfaceId, objectList);
                _busyBucket.put(interfaceId, new Vector<Object>(totalPoolSize));
                if (_log.isDebugEnabled())
                    _log.debug("HuaweiPoolManager[initialize]", "_freeBucket :: " + _freeBucket);

            }// end of while.
        } catch (BTSLBaseException be) {
            _log.error("initialize", "BTSLBaseException be:" + be.getMessage());
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            e.printStackTrace();
            _log.error("initialize", "Exception e::" + e.getMessage());
            // Destroying the Client Objects from Hash table _freeBucket
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiPoolManager[initialize]", "String of interface ids=" + p_interfaceIDs, "", "", "While initializing the instance of Client Object for the INTERFACE_ID =" + interfaceId + " get Exception=" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_OBJECT_POOL_INITIALIZATION);// New
                                                                                               // Error
                                                                                               // Code-Confirm
        }// end of catch-Exception
        finally {
            _isPoolInitializing = false;
            if (_log.isDebugEnabled())
                _log.debug("initialize", "Exited _freeBucket::" + _freeBucket);
        }// end of finally
    }// end of initialize

    /**
     * This method is used to return the Client object based on the Interface
     * Id,this includes the following
     * 1.Based on the interface id fetch the ArrayList of Pooled client object
     * from the free bucket.
     * 2.Check whether any pooled client-object is present in the ArrayList of
     * free-bucket.
     * 3.If present then remove the client-object from the ArrayList of
     * freeBucket.
     * 4.Add the removed client-object to the ArrayList of busyBucket.(At the
     * other point of control the client object would be moved again to
     * freeBucket after the request completion.)
     * 
     * @param p_interfaceID
     * @return Object
     * @throws Exception
     */
    public static synchronized HuaweiSocketWrapper getClientObject(String p_interfaceID, boolean p_module) throws BTSLBaseException {
        HuaweiSocketWrapper clientObject = null;
        Vector<Object> freeList = null;
        Vector<Object> busyList = null;
        long poolInitializationTimeLong = 0;
        int retryAttempt = 0;
        long retrySleep = 0;
        int listSize = 0;
        try {
            if ((_freeBucket.isEmpty() && _busyBucket.isEmpty()) || !_freeBucket.containsKey(p_interfaceID)) {
                _log.error("getClientObject", "POOL is not initialized and contains no objects in the pool");
                // EventHandler.handle(EventIDI.SYSTEM_ERROR,
                // EventComponentI.SYSTEM, EventStatusI.RAISED,
                // EventLevelI.FATAL,
                // "HuaweiPoolManager[getClientObject]","INTERFACE_ID="+p_interfaceID,
                // "","",
                // "While getting the the instance of ClintObject for the interfaceID ="+p_interfaceID+"POOL is not initialized and contains no objects in the pool");
                initialize(p_interfaceID);
                _huaweiHeartBeatController = new HuaweiHeartBeatController(p_interfaceID);
                // throw new
                // BTSLBaseException("HuaweiPoolManager[getClientObject]",InterfaceErrorCodesI.ERROR_OBJECT_POOL_INITIALIZATION);
            }
            // Getting the list of client objects from pooledMap based on the
            // interfaceID.
            freeList = (Vector<Object>) _freeBucket.get(p_interfaceID);

            if (freeList == null) {
                String poolInitializationTimeStr = FileCache.getValue(p_interfaceID, "POOL_INIT_TIME");
                try {
                    poolInitializationTimeLong = Long.parseLong(poolInitializationTimeStr);
                } catch (Exception e) {
                    poolInitializationTimeLong = 300;
                }
                if (!_isPoolInitializing && ((System.currentTimeMillis() / 1000) - _lastPoolInitializationTime) > poolInitializationTimeLong) {
                    initialize(p_interfaceID);
                    _lastPoolInitializationTime = (System.currentTimeMillis() / 1000);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "HuaweiPoolManager[getClientObject]", "INTERFACE_ID=" + p_interfaceID, "", "", "Pool Container is Null for interface Id =" + p_interfaceID + " Hence reinitialized the Pool: last Initiazation time:" + _lastPoolInitializationTime);
                }
                _log.error("getClientObject", "POOL Container(Vector list) is NULL for the interface id:" + p_interfaceID);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "HuaweiPoolManager[getClientObject]", "INTERFACE_ID=" + p_interfaceID, "", "", "While getting the the instance of ClintObject for the interfaceID =" + p_interfaceID + "POOL contains no object for the p_interfaceID:" + p_interfaceID);
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_OBJECT_POOL_INITIALIZATION);
            }
            // Confirm what should be done when all the Client Object is busy
            // for that interface???????
            // 1.Either to wait for configured time (so that after the
            // completion of other request client is moved into freeBucket)
            // a.In case of wait decide how many times the sleep would be
            // applied.
            // 2.Or handle the event and throw the exception.
            // 3.Or we have to create a new reference of client object.
            // a.If a new client-object is created confirm whether to store it
            // or destroy just after processing the request?????
            listSize = freeList.size();

            if (listSize == 0) {
                try {
                    // Get the sleep time from the INFile by which the current
                    // thread would be sleep to get the Client objec from the
                    // Free list.
                    String sleepTimeStr = FileCache.getValue(p_interfaceID, "SLEEP_TIME");
                    if (InterfaceUtil.isNullString(sleepTimeStr))
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_FETCH_CLIENT_OBJECT);
                    retrySleep = Long.parseLong(sleepTimeStr.trim());

                    // When value of p_module is TRUE means C2S Module else P2P
                    // module
                    if (p_module) {
                        String retryAtStr = FileCache.getValue(p_interfaceID, "C2S_RETRY_ATTEMPT");
                        if (InterfaceUtil.isNullString(retryAtStr))
                            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_FETCH_CLIENT_OBJECT);
                        retryAttempt = Integer.parseInt(retryAtStr.trim());
                    } else {
                        String retryAtStr = FileCache.getValue(p_interfaceID, "P2P_RETRY_ATTEMPT");
                        if (InterfaceUtil.isNullString(retryAtStr))
                            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_FETCH_CLIENT_OBJECT);
                        retryAttempt = Integer.parseInt(retryAtStr.trim());
                    }
                    // In else case we can define some default value or throw
                    // the Exception--Confirm
                } catch (BTSLBaseException be) {
                    throw be;
                } catch (Exception e) {
                    e.printStackTrace();
                    _log.error("getClientObject", "Exception e::" + e.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiPoolManager[getClientObject]", "INTERFACE_ID=" + p_interfaceID, "", "", "While getting the retry attempt[SLEEP_TIME, C2S_RETRY_ATTEMPT and P2P_RETRY_ATTEMPT] for interface id=" + p_interfaceID + " get Exception=" + e.getMessage());
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_FETCH_CLIENT_OBJECT);
                }
                for (int retryToSleep = 0; retryToSleep < retryAttempt; retryToSleep++)// value
                                                                                       // of
                                                                                       // 3
                                                                                       // should
                                                                                       // be
                                                                                       // configurable
                                                                                       // not
                                                                                       // constant.
                {
                    listSize = freeList.size();
                    if (listSize > 0)
                        break;
                    Thread.sleep(retrySleep);
                }
            }

            if (listSize == 0) {
                _log.error("getClientObject", "POOL does not contain any free objects");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "HuaweiPoolManager[getClientObject]", "INTERFACE_ID=" + p_interfaceID, "", "", "While getting the retry attempt[SLEEP_TIME, C2S_RETRY_ATTEMPT and P2P_RETRY_ATTEMPT] for interface id=" + p_interfaceID + " POOL does not contain any free objects");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NO_FREE_OBJ_IN_POOL);
            }
            busyList = (Vector<Object>) _busyBucket.get(p_interfaceID);

            // If the client object is present then remove the client object
            // from the free list and add it to busyList
            clientObject = (HuaweiSocketWrapper) freeList.remove(0);
            // Check the clientObject for the NULL value
            if (clientObject == null)
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_FETCH_CLIENT_OBJECT);
            // Add the client object into busyList.
            busyList.add(clientObject);

            // It should be removed after the request has been completed(success
            // or fail) In the Hander Class.
            // And should be added to the freeBucketList.
        } catch (BTSLBaseException be) {
            _log.error("getClinetObject", "BTSLBaseException be:" + be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("getClinetObject", "Exception e::" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiPoolManager[getClientObject]", "INTERFACE_ID=" + p_interfaceID, "", "", "While getting the the instance of ClintObject for the interfaceID =" + p_interfaceID + " get Exception=" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_FETCH_CLIENT_OBJECT);// First
                                                                                        // we
                                                                                        // have
                                                                                        // to
                                                                                        // decide
                                                                                        // the
                                                                                        // Key
                                                                                        // then
                                                                                        // chnage
                                                                                        // the
                                                                                        // key
                                                                                        // ERROR_CS3_WHILE_GETTING_SCHEDULER_OBJECT
        }// end of catch
        finally {
            if (_log.isDebugEnabled())
                _log.debug("getClinetObject", "getClientObject Exited clientObject::" + clientObject);
        }// end of finally
        return clientObject;
    }// end of getClinetObject

    public static synchronized Socket getSocketConnectionObject(String p_interfaceID, boolean p_module) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getClientObject", "Entered p_interfaceID::" + p_interfaceID + " p_module:" + p_module + "_freeBucket = " + _freeBucket);
        Socket clientObject = null;
        Vector<Object> freeList = null;
        Vector<Object> busyList = null;
        long poolInitializationTimeLong = 0;
        int retryAttempt = 0;
        long retrySleep = 0;
        int listSize = 0;
        try {
            if (_freeBucket.isEmpty() || !_freeBucket.containsKey(p_interfaceID)) {
                _log.error("getClientObject", "POOL is not initialized and contains no objects in the pool");
                // EventHandler.handle(EventIDI.SYSTEM_ERROR,
                // EventComponentI.SYSTEM, EventStatusI.RAISED,
                // EventLevelI.FATAL,
                // "HuaweiPoolManager[getClientObject]","INTERFACE_ID="+p_interfaceID,
                // "","",
                // "While getting the the instance of ClintObject for the interfaceID ="+p_interfaceID+"POOL is not initialized and contains no objects in the pool");
                initialize(p_interfaceID);
                _huaweiHeartBeatController = new HuaweiHeartBeatController(p_interfaceID);
                // throw new
                // BTSLBaseException("HuaweiPoolManager[getClientObject]",InterfaceErrorCodesI.ERROR_OBJECT_POOL_INITIALIZATION);
            }
            // Getting the list of client objects from pooledMap based on the
            // interfaceID.
            freeList = (Vector<Object>) _freeBucket.get(p_interfaceID);
            if (_log.isDebugEnabled())
                _log.debug("getClientObject", "freeList::" + freeList);
            if (freeList == null) {
                String poolInitializationTimeStr = FileCache.getValue(p_interfaceID, "POOL_INIT_TIME");
                try {
                    poolInitializationTimeLong = Long.parseLong(poolInitializationTimeStr);
                } catch (Exception e) {
                    poolInitializationTimeLong = 300;
                }
                if (!_isPoolInitializing && ((System.currentTimeMillis() / 1000) - _lastPoolInitializationTime) > poolInitializationTimeLong) {
                    initialize(p_interfaceID);
                    _lastPoolInitializationTime = (System.currentTimeMillis() / 1000);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "HuaweiPoolManager[getClientObject]", "INTERFACE_ID=" + p_interfaceID, "", "", "Pool Container is Null for interface Id =" + p_interfaceID + " Hence reinitialized the Pool: last Initiazation time:" + _lastPoolInitializationTime);
                }
                _log.error("getClientObject", "POOL Container(Vector list) is NULL for the interface id:" + p_interfaceID);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "HuaweiPoolManager[getClientObject]", "INTERFACE_ID=" + p_interfaceID, "", "", "While getting the the instance of ClintObject for the interfaceID =" + p_interfaceID + "POOL contains no object for the p_interfaceID:" + p_interfaceID);
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_OBJECT_POOL_INITIALIZATION);
            }
            // Confirm what should be done when all the Client Object is busy
            // for that interface???????
            // 1.Either to wait for configured time (so that after the
            // completion of other request client is moved into freeBucket)
            // a.In case of wait decide how many times the sleep would be
            // applied.
            // 2.Or handle the event and throw the exception.
            // 3.Or we have to create a new reference of client object.
            // a.If a new client-object is created confirm whether to store it
            // or destroy just after processing the request?????
            listSize = freeList.size();
            if (_log.isDebugEnabled())
                _log.debug("getClientObject", "listSize::" + listSize);
            if (listSize == 0) {
                try {
                    // Get the sleep time from the INFile by which the current
                    // thread would be sleep to get the Client objec from the
                    // Free list.
                    String sleepTimeStr = FileCache.getValue(p_interfaceID, "SLEEP_TIME");
                    if (InterfaceUtil.isNullString(sleepTimeStr))
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_FETCH_CLIENT_OBJECT);
                    retrySleep = Long.parseLong(sleepTimeStr.trim());

                    // When value of p_module is TRUE means C2S Module else P2P
                    // module
                    if (p_module) {
                        String retryAtStr = FileCache.getValue(p_interfaceID, "C2S_RETRY_ATTEMPT");
                        if (InterfaceUtil.isNullString(retryAtStr))
                            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_FETCH_CLIENT_OBJECT);
                        retryAttempt = Integer.parseInt(retryAtStr.trim());
                    } else {
                        String retryAtStr = FileCache.getValue(p_interfaceID, "P2P_RETRY_ATTEMPT");
                        if (InterfaceUtil.isNullString(retryAtStr))
                            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_FETCH_CLIENT_OBJECT);
                        retryAttempt = Integer.parseInt(retryAtStr.trim());
                    }
                    // In else case we can define some default value or throw
                    // the Exception--Confirm
                } catch (BTSLBaseException be) {
                    throw be;
                } catch (Exception e) {
                    e.printStackTrace();
                    _log.error("getClientObject", "Exception e::" + e.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiPoolManager[getClientObject]", "INTERFACE_ID=" + p_interfaceID, "", "", "While getting the retry attempt[SLEEP_TIME, C2S_RETRY_ATTEMPT and P2P_RETRY_ATTEMPT] for interface id=" + p_interfaceID + " get Exception=" + e.getMessage());
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_FETCH_CLIENT_OBJECT);
                }
                for (int retryToSleep = 0; retryToSleep < retryAttempt; retryToSleep++)// value
                                                                                       // of
                                                                                       // 3
                                                                                       // should
                                                                                       // be
                                                                                       // configurable
                                                                                       // not
                                                                                       // constant.
                {
                    listSize = freeList.size();
                    if (listSize > 0)
                        break;
                    Thread.sleep(retrySleep);
                }
            }

            if (listSize == 0) {
                _log.error("getClientObject", "POOL does not contain any free objects");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "HuaweiPoolManager[getClientObject]", "INTERFACE_ID=" + p_interfaceID, "", "", "While getting the retry attempt[SLEEP_TIME, C2S_RETRY_ATTEMPT and P2P_RETRY_ATTEMPT] for interface id=" + p_interfaceID + " POOL does not contain any free objects");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NO_FREE_OBJ_IN_POOL);
            }
            busyList = (Vector<Object>) _busyBucket.get(p_interfaceID);
            if (_log.isDebugEnabled())
                _log.debug("getClientObject", "before getting the client object, size of freeList.size():" + listSize + " and busyList.size()" + busyList.size());
            // If the client object is present then remove the client object
            // from the free list and add it to busyList
            clientObject = (Socket) freeList.remove(0);
            if (_log.isDebugEnabled())
                _log.debug("getClientObject", "clientObject::" + clientObject);
            // Check the clientObject for the NULL value
            if (clientObject == null)
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_FETCH_CLIENT_OBJECT);
            // Add the client object into busyList.
            busyList.add(clientObject);
            if (_log.isDebugEnabled())
                _log.debug("getClientObject", "After getting the client object from freeList, size of freeList.size():" + freeList.size() + " and busyList.size()" + busyList.size());
            // It should be removed after the request has been completed(success
            // or fail) In the Hander Class.
            // And should be added to the freeBucketList.
        } catch (BTSLBaseException be) {
            _log.error("getClinetObject", "BTSLBaseException be:" + be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("getClinetObject", "Exception e::" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiPoolManager[getClientObject]", "INTERFACE_ID=" + p_interfaceID, "", "", "While getting the the instance of ClintObject for the interfaceID =" + p_interfaceID + " get Exception=" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_FETCH_CLIENT_OBJECT);// First
                                                                                        // we
                                                                                        // have
                                                                                        // to
                                                                                        // decide
                                                                                        // the
                                                                                        // Key
                                                                                        // then
                                                                                        // chnage
                                                                                        // the
                                                                                        // key
                                                                                        // ERROR_CS3_WHILE_GETTING_SCHEDULER_OBJECT
        }// end of catch
        finally {
            if (_log.isDebugEnabled())
                _log.debug("getClinetObject", "Exited clientObject::" + clientObject);
        }// end of finally
        return clientObject;
    }// end of getClinetObject

    /**
     * This method is used to get the new client object based on the interface
     * id
     * 
     * @param p_interfaceID
     * @return Object
     * @throws BTSLBaseException
     */
    public static synchronized HuaweiSocketWrapper getNewClientObject(String p_interfaceID) throws BTSLBaseException {
        HuaweiProps.logMessage("getNewClientObject Entered :: p_interfaceID::" + p_interfaceID);
        HuaweiSocketWrapper huaweiKnFixSocketWrapper = null;
        try {
            huaweiKnFixSocketWrapper = new HuaweiSocketWrapper(p_interfaceID);
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("getNewClientObject", "Exception e::" + e.getMessage());
            EventHandler.handle(EventIDI.RETRY_ATTEMPT_FAILED, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiPoolManager[getNewClientObject]", "INTERFACEID:" + p_interfaceID, "", "", "While getting the instance of Client objects got the Exception " + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_GETTIN_NEW_CLIENT_OBJECT);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getNewClientObject", "Exiting huaweiKnFixSocketWrapper:" + huaweiKnFixSocketWrapper);
        }
        return huaweiKnFixSocketWrapper;
    }

    /**
     * This method is used to destroy the ClientObject references from
     * freeBucket and busyBucket.
     * 
     * @param String
     *            p_interfaceId
     * @return void
     */
    public void destroy(String p_interfaceId) {
        if (_log.isDebugEnabled())
            _log.debug("destroy", "Entered p_interfaceId:" + p_interfaceId);
        try {
            Set ketSetCode = null;
            Iterator iter = null;
            String key = null;
            if (InterfaceUtil.isNullString(p_interfaceId))
                throw new Exception(" No Pool Id for destroying");
            String strINId = null;
            StringTokenizer strTokens = new StringTokenizer(p_interfaceId, ",");
            if (_freeBucket != null) {
                while (strTokens.hasMoreElements()) {
                    strINId = strTokens.nextToken().trim();
                    ketSetCode = _freeBucket.keySet();
                    iter = ketSetCode.iterator();
                    _log.info("destroy", "Destroying pool of client objects for Interface ID=" + strINId);
                    Vector objectList = null;
                    while (iter.hasNext()) {
                        key = (String) iter.next();
                        if (strINId.equals(key.trim())) {
                            _log.info("destroy", "Destroying ClientObjects from _freeBucket for Interface ID=" + key);
                            try {
                                if (_huaweiHeartBeatController != null)
                                    _huaweiHeartBeatController.stopHeartBeat(key);
                                objectList = (Vector) _freeBucket.remove(key);
                                if (objectList != null) {
                                    int size = objectList.size();
                                    for (int i = 0; i < size; i++) {
                                        HuaweiSocketWrapper socketWrapper = (HuaweiSocketWrapper) objectList.get(i);
                                        socketWrapper.destroy();
                                    }
                                }
                            } catch (Exception e) {
                                // e.printStackTrace();
                                // Confirm for event handling.
                                objectList = null;
                            }
                        }// end of if
                    }// end of inner-while.
                }// end of outer while
            }// end of if
            if (_busyBucket != null) {
                while (strTokens.hasMoreElements()) {
                    strINId = strTokens.nextToken().trim();
                    ketSetCode = _busyBucket.keySet();
                    iter = ketSetCode.iterator();
                    _log.info("destroy", "Destroying pool of client objects from busyBucket for Interface ID=" + strINId);
                    Vector objectList = null;
                    while (iter.hasNext()) {
                        key = (String) iter.next();
                        if (strINId.equals(key.trim())) {
                            _log.info("destroy", "Destroying ClientObjects from _busyBucket for Interface ID=" + key);
                            try {
                                if (_huaweiHeartBeatController != null)
                                    _huaweiHeartBeatController.stopHeartBeat(key);
                                objectList = (Vector) _busyBucket.remove(key);
                                if (objectList != null) {
                                    int size = objectList.size();
                                    for (int i = 0; i < size; i++) {
                                        HuaweiSocketWrapper socketWrapper = (HuaweiSocketWrapper) objectList.get(i);
                                        socketWrapper.destroy();
                                    }
                                }
                            } catch (Exception e) {
                                // e.printStackTrace();
                                // Confirm for event handling.
                                objectList = null;
                            }
                        }
                    }// end of while.
                }
            }// end of if
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("destroy", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiPoolManager[destroy]", "", "", "", "While destorying the Client objects got the Exception " + e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("destroy", "Exiting");
        }// end of finally
    }// end of destroy

    /**
     * This method is used to destroy the ALL ClientObject references from
     * freeBucket and busyBucket.
     * 
     * @param String
     *            p_interfaceId
     * @param String
     *            p_all
     * @return void
     */
    public void destroy(String p_interfaceId, String p_all) {
        if (_log.isDebugEnabled())
            _log.debug("destroy", "Entered p_ids=" + p_interfaceId + " p_all=" + p_all);
        try {
            HashMap<String, Vector> map = null;
            Set ketSetCode = null;
            Iterator iter = null;
            String key = null;
            if (InterfaceUtil.isNullString(p_interfaceId))
                throw new Exception("No Pool Id for destroying");
            String strINId = null;
            StringTokenizer strTokens = new StringTokenizer(p_interfaceId, ",");
            if (_freeBucket != null) {
                map = new HashMap<String, Vector>(_freeBucket);
                while (strTokens.hasMoreElements()) {
                    strINId = strTokens.nextToken().trim();
                    ketSetCode = map.keySet();
                    iter = ketSetCode.iterator();
                    Vector objectList = null;
                    while (iter.hasNext()) {
                        key = (String) iter.next();
                        if (strINId.equals(key.trim())) {
                            _log.info("destroy", "Destroying ClientObjects from _freeBucket for Interface ID=" + key);

                            try {
                                if (_huaweiHeartBeatController != null)
                                    _huaweiHeartBeatController.stopHeartBeat(key);
                                objectList = (Vector) _freeBucket.remove(key);
                                if (objectList != null) {
                                    int size = objectList.size();
                                    for (int i = 0; i < size; i++) {
                                        HuaweiSocketWrapper socketWrapper = (HuaweiSocketWrapper) objectList.get(i);
                                        socketWrapper.destroy();
                                    }
                                }
                            } catch (Exception e) {
                                // e.printStackTrace();
                                objectList = null;
                            }
                        }// end of if
                    }// end of inner-while.
                }// end of outer while
            }// end of if
            if (_busyBucket != null) {
                map = new HashMap<String, Vector>(_busyBucket);
                while (strTokens.hasMoreElements()) {
                    strINId = strTokens.nextToken().trim();
                    ketSetCode = map.keySet();
                    iter = ketSetCode.iterator();
                    Vector objectList = null;
                    while (iter.hasNext()) {
                        key = (String) iter.next();
                        if (strINId.equals(key.trim())) {
                            _log.info("destroy", "Destroying ClientObjects from _busyBucket for Interface ID=" + key);
                            try {
                                if (_huaweiHeartBeatController != null)
                                    _huaweiHeartBeatController.stopHeartBeat(key);
                                objectList = (Vector) _busyBucket.remove(key);
                                if (objectList != null) {
                                    int size = objectList.size();
                                    for (int i = 0; i < size; i++) {
                                        HuaweiSocketWrapper socketWrapper = (HuaweiSocketWrapper) objectList.get(i);
                                        socketWrapper.destroy();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                objectList = null;
                            }
                        }
                    }// end of while.
                }
            }// end of if
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("destroy", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiPoolManager[destroy]", "", "", "", "While destorying the Client objects got the Exception " + e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("destroy", "Exiting");
        }// end of finally
    }// end of destroy

}
