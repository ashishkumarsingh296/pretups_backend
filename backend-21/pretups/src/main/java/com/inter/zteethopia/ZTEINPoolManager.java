package com.inter.zteethopia;

/**
 * @(#)ZTEPoolManager.java
 *                         Copyright(c) 2009, Bharti Telesoft Int. Public Ltd.
 *                         All Rights Reserved
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Author Date History
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Shamit June 27, 2009 Initial Creation
 *                         ----------------------------------------------------
 *                         -------------------------------------------
 *                         This class is responsible to instantiate the Client
 *                         object and maintain a pool.
 */
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
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
import com.btsl.util.BTSLUtil;

public class ZTEINPoolManager {

    static Log _logger = LogFactory.getLog(ZTEINPoolManager.class.getName());

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

    public static HashMap<String, Vector<Object>> _inactiveNodes = new HashMap<String, Vector<Object>>();// Contains
                                                                                                         // the
                                                                                                         // list
                                                                                                         // of
                                                                                                         // busy
                                                                                                         // client
                                                                                                         // objects,associated
                                                                                                         // with
                                                                                                         // interface
                                                                                                         // id.

    static public Hashtable<String, Integer> ZTEHashLastUsed = new Hashtable<String, Integer>(20);
    static public Hashtable<String, Integer> ZTEHashMaxIp = new Hashtable<String, Integer>(20);
    public static int LAST_USED = 0;
    public static int MAX_IP = 0;

    private static ZTEINHeartBeatController _ZTEHeartBeatController = null;
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
     * 
     * @param p_arr
     * @return
     */
    public static boolean isNullArray(String[] p_arr) {
        if (_logger.isDebugEnabled())
            _logger.debug("isNullArray Entered ", " p_arr: " + p_arr);
        boolean isNull = true;
        if (p_arr != null) {
            for (int i = 0, j = p_arr.length; i < j; i++) {
                if (!BTSLUtil.isNullString(p_arr[i])) {
                    isNull = false;
                    break;
                }
            }
        }
        if (_logger.isDebugEnabled())
            _logger.debug("isNullArray Exited ", " isNull: " + isNull);
        return isNull;
    }

    /**
     * This method is responsible to store the instance of Client objects into a
     * HashMap with interface id as Key.
     * 
     * @param String
     *            p_interfaceIDs
     * @throws BTSLBaseException
     */
    public static void initialize(String p_interfaceIDs) throws BTSLBaseException {
        if (_logger.isDebugEnabled())
            _logger.debug("initialize Entered ", " p_interfaceIDs::" + p_interfaceIDs);
        String interfaceId = null;
        Object object = null;
        ZTEINSocket _zteINSocket = null;
        int poolSize = 0;
        long poolSleep = 0;
        int nodes = 0;
        String[] inStrArray = null;

        String[] virtualIPArray = null;

        Vector<Object> objectList = null;// To synchronizes the pooled objects
        try {
            _isPoolInitializing = true;
            inStrArray = p_interfaceIDs.split(",");
            if (isNullArray(inStrArray))
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_OBJECT_POOL_INITIALIZATION);

            // Confirm while creating instances,if any error occurs for an
            // interface,
            // should we stop the process with handling the event and throw
            // exception
            // Or only event should be handled corresponding to that interface
            // and continue to other.

            ZTEINHBLogger.logMessage("======CONNECTION POOLING LOGS=====");
            ZTEINHBLogger.logMessage("Total Network IDs= " + inStrArray.length);

            for (int i = 0, size = inStrArray.length; i < size; i++) {
                // Create a list in which store the client object equal to
                // configured number.
                interfaceId = inStrArray[i].trim();
                String fileCacheId = interfaceId;
                try {
                    MAX_IP = Integer.parseInt(FileCache.getValue(fileCacheId, "NODES_SIZE_" + interfaceId));
                    LAST_USED = 0;
                    ZTEINHBLogger.logMessage("");
                    ZTEINHBLogger.logMessage("Pick  Circle = " + interfaceId + " and Total Nodes=" + MAX_IP);
                    // Get the Max pool size from the INFile.

                    nodes = Integer.parseInt(FileCache.getValue(fileCacheId, "NODES_SIZE_" + interfaceId));
                } catch (Exception e) {
                    e.printStackTrace();
                    _logger.error("initialize ", " Exception e:" + e.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEPoolManager[initialize]", "", "", "", "While instantiation of the Client objects MAX_POOL_SIZE/POOL_SLEEP is either not defined or not numeric, in the INFile");
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_OBJECT_POOL_INITIALIZATION);
                }

                for (int node = 1; node <= nodes; node++) {
                    interfaceId = inStrArray[i].trim() + "_" + node;
                    try {

                        // poolSize =
                        // Integer.parseInt(FileCache.getValue(fileCacheId,"MAX_ZTE_POOL_SIZE_"+interfaceId));

                        String virtualIPS = FileCache.getValue(fileCacheId, "VIRTUAL_ZTE_SOCKET_IP_" + interfaceId);
                        virtualIPArray = virtualIPS.split(",");

                        if (isNullArray(virtualIPArray))
                            break;

                        poolSize = virtualIPArray.length;

                        poolSleep = Long.parseLong(FileCache.getValue(fileCacheId, "POOL_SLEEP"));
                        ZTEINHBLogger.logMessage("Pick  Node = " + fileCacheId + " and Pool Size=" + poolSize);
                        objectList = new Vector<Object>(poolSize);
                    } catch (Exception e) {
                        e.printStackTrace();
                        _logger.error("initialize ", " Exception e:" + e.getMessage());
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEPoolManager[initialize]", "", "", "", "While instantiation of the Client objects MAX_POOL_SIZE/POOL_SLEEP is either not defined or not numeric, in the INFile");
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_OBJECT_POOL_INITIALIZATION);
                    }

                    try {
                        for (int loop = 0; loop < poolSize; loop++) {
                            object = null;
                            _zteINSocket = null;
                            ZTEINHBLogger.logMessage("Going to create the connection for circle =" + interfaceId + " connection number" + loop);
                            try {
                                _zteINSocket = new ZTEINSocket();
                                _zteINSocket.setVirtualIP(InetAddress.getByName(virtualIPArray[loop]));
                                object = new ZTEINNewClientConnection().getNewClientObject(fileCacheId, interfaceId, _zteINSocket);
                                if (object != null)
                                    _zteINSocket.setZteINSocketWrapper((ZTEINSocketWrapper) object);

                                objectList.add(_zteINSocket);
                                Thread.sleep(poolSleep);
                            } catch (Exception e) {
                                e.printStackTrace();
                                _logger.error("initialize ", " Exception e:" + e.getMessage());
                                ZTEINHBLogger.logMessage("Exception Going to create the connection for circle =" + interfaceId + " connection number" + loop + " object" + object);
                                EventHandler.handle(EventIDI.SYSTEM_ERROR, p_interfaceIDs, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PoolLoader[initialize]", "", "", "", "While instantiation of the Client objects got the Exception:" + e.getMessage());
                                if (object != null)
                                    _zteINSocket.setZteINSocketWrapper((ZTEINSocketWrapper) object);
                                objectList.add(_zteINSocket);
                                objectList.add(object);
                            }

                        }// end of for loop

                        _freeBucket.put(interfaceId, objectList);
                        _busyBucket.put(interfaceId, new Vector<Object>(poolSize));
                    } catch (Exception e) {
                        ZTEINHBLogger.logMessage("Going to barred the interfaceId=" + interfaceId);
                        ZTEINStatus.getInstance().barredAir(interfaceId);
                    }
                } // next node

                ZTEHashLastUsed.put(inStrArray[i].trim(), LAST_USED);
                ZTEHashMaxIp.put(inStrArray[i].trim(), MAX_IP);

            }// end of while.
            ZTEINHBLogger.logMessage("Summarize of the connection pooling ::::_freeBucket" + _freeBucket.toString() + " _busyBucket" + _busyBucket.toString() + "ZTEINStatus.getInstance() = " + ZTEINStatus.getInstance().getairtable() + "ZTEHashLastUsed" + ZTEHashLastUsed.toString() + "ZTEHashMaxIp" + ZTEHashMaxIp.toString() + "=================END of the connection POOL================");
        } catch (BTSLBaseException be) {
            _logger.error("initialize ", "BTSLBaseException be:" + be.getMessage());
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            e.printStackTrace();
            _logger.error("initialize ", "Exception e::" + e.getMessage());
            // Destroying the Client Objects from Hash table _freeBucket
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEPoolManager[initialize]", "String of interface ids=" + p_interfaceIDs, "", "", "While initializing the instance of Client Object for the INTERFACE_ID =" + interfaceId + " get Exception=" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_OBJECT_POOL_INITIALIZATION);// New
                                                                                               // Error
                                                                                               // Code-Confirm
        }// end of catch-Exception
        finally {
            _isPoolInitializing = false;
            if (_logger.isDebugEnabled())
                _logger.debug("initialize", " Exited _freeBucket::" + _freeBucket);
        }// end of finally
    }// end of initialize

    // raj-variable name should be interface_id
    public synchronized static int RoundRobin(String p_interfaceID) {

        int last_used = ZTEHashLastUsed.get(p_interfaceID).intValue();
        int max_ip = ZTEHashMaxIp.get(p_interfaceID).intValue();

        if (last_used < max_ip) {
            last_used++;
        } else if ((last_used == max_ip) || (last_used > max_ip)) {
            last_used = 1;
        }
        ZTEHashLastUsed.put(p_interfaceID, new Integer(last_used));
        return last_used;
    }

    public static synchronized boolean isConnectionFree(String p_interfaceID) throws BTSLBaseException {
        try {
            return true;
        } catch (Exception e) {
            return false;
        }

    }

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
    public static synchronized ZTEINSocket getClientObject(String p_interfaceID) throws BTSLBaseException {
        if (_logger.isDebugEnabled())
            _logger.debug("getClientObject", " Entered p_interfaceID::" + p_interfaceID);

        ZTEINSocket socket = null;
        // ZTEINSocketWrapper clientObject=null;
        Vector<Object> freeList = null;
        Vector<Object> busyList = null;

        int listSize = 0;
        try {
            if (_freeBucket.size() == 0 || !_freeBucket.containsKey(p_interfaceID)) {
                _logger.error("getClientObject POOL is not initialized ", " and contains no objects in the pool");
                throw new BTSLBaseException("ZTEPoolManager[getClientObject]" + InterfaceErrorCodesI.ERROR_OBJECT_POOL_INITIALIZATION);
            }

            // Getting the list of client objects from pooledMap based on the
            // interfaceID.
            freeList = (Vector<Object>) _freeBucket.get(p_interfaceID);
            listSize = freeList.size();

            if (listSize == 0)
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NO_FREE_OBJ_IN_POOL);

            busyList = (Vector<Object>) _busyBucket.get(p_interfaceID);
            if (_logger.isDebugEnabled())
                _logger.debug("getClientObject before getting the client object ", "  size of freeList.size():" + listSize + " and busyList.size()" + busyList.size());

            // If the client object is present then remove the client object
            // from the free list and add it to busyList
            socket = (ZTEINSocket) freeList.remove(0);

            // Check the clientObject for the NULL value
            if (socket.getZteINSocketWrapper() == null) {
                freeList.add(socket);
                return socket;
            }

            _logger.debug("getClientObject After getting the client object ", "  size of freeList.size():" + listSize + " and busyList.size()" + busyList.size() + " and socket =" + socket);

            // Add the client object into busyList.
            busyList.add(socket);
            if (_logger.isDebugEnabled())
                _logger.debug("getClientObject After getting the client object from freeList ", "  size of freeList.size():" + freeList.size() + " and busyList.size()" + busyList.size());
            // It should be removed after the request has been completed(success
            // or fail) In the Hander Class.
            // And should be added to the freeBucketList.
        } catch (BTSLBaseException be) {
            _logger.error("getClinetObject ", "  BTSLBaseException be:" + be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _logger.error("getClinetObject ", "  Exception e::" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEPoolManager[getClientObject]", "INTERFACE_ID=" + p_interfaceID, "", "", "While getting the the instance of ClintObject for the interfaceID =" + p_interfaceID + " get Exception=" + e.getMessage());
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
            if (_logger.isDebugEnabled())
                _logger.debug("getClinetObject ", "  Exited socket::" + socket);
        }// end of finally
        return socket;
    }// end of getClinetObject

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
    public static synchronized ZTEINSocket getClientObject(String p_interfaceID, String p_transactionId, boolean isRoundRobin) throws BTSLBaseException {
        // if(_logger.isDebugEnabled())
        // _logger.debug("getClientObject Entered p_interfaceID::"+p_interfaceID);
        // ZTEINSocketWrapper clientObject=null;
        ZTEINSocket socket = null;
        Vector<Object> freeList = null;
        Vector<Object> busyList = null;
        if (isRoundRobin) {
            int last_used = ZTEHashLastUsed.get(p_interfaceID).intValue();
            int max_ip = ZTEHashMaxIp.get(p_interfaceID).intValue();

            if (last_used < max_ip) {
                last_used++;
            } else if ((last_used == max_ip) || (last_used > max_ip)) {
                last_used = 1;
            }
            ZTEHashLastUsed.put(p_interfaceID, new Integer(last_used));
            p_interfaceID = p_interfaceID + "_" + last_used;
            if (ZTEINStatus.getInstance().isBarredAir(p_interfaceID)) {
                return socket;

            }

        }

        int listSize = 0;
        try {
            if (_freeBucket.size() == 0 || !_freeBucket.containsKey(p_interfaceID)) {
                _logger.error("getClientObject POOL ", " is not initialized and contains no objects in the pool");
                throw new BTSLBaseException("ZTEPoolManager[getClientObject]" + InterfaceErrorCodesI.ERROR_OBJECT_POOL_INITIALIZATION);
            }

            // Getting the list of client objects from pooledMap based on the
            // interfaceID.
            freeList = (Vector<Object>) _freeBucket.get(p_interfaceID);
            listSize = freeList.size();

            if (listSize == 0)
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NO_FREE_OBJ_IN_POOL);

            busyList = (Vector<Object>) _busyBucket.get(p_interfaceID);
            // if(_logger.isDebugEnabled())
            // _logger.debug("getClientObject before getting the client object, size of freeList.size():"+listSize
            // +" and busyList.size()"+busyList.size());

            // If the client object is present then remove the client object
            // from the free list and add it to busyList
            socket = (ZTEINSocket) freeList.remove(0);

            // Check the clientObject for the NULL value
            if (socket.getZteINSocketWrapper() == null) {
                freeList.add(socket);
                return socket;
            }
            _logger.debug("getClientObject After getting the client object ", "  size of freeList.size():" + listSize + " and busyList.size()" + busyList.size() + " and socket =" + socket);

            // Add the client object into busyList.
            busyList.add(socket);
            // if(_logger.isDebugEnabled())
            // _logger.debug("getClientObject After getting the client object from freeList, size of freeList.size():"+freeList.size()
            // +" and busyList.size()"+busyList.size());
            // It should be removed after the request has been completed(success
            // or fail) In the Hander Class.
            // And should be added to the freeBucketList.
        } catch (BTSLBaseException be) {
            _logger.error("getClinetObject ", " BTSLBaseException be:" + be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _logger.error("getClinetObject", "  Exception e::" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEPoolManager[getClientObject]", "INTERFACE_ID=" + p_interfaceID, "", "", "While getting the the instance of ClintObject for the interfaceID =" + p_interfaceID + " get Exception=" + e.getMessage());
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
            if (_logger.isDebugEnabled())
                _logger.debug("getClinetObject ", "Exited socket::" + socket);
        }// end of finally
        return socket;
    }// end of getClinetObject

    /**
     * This method used to get the next active node in the robin round fashion
     * 
     * @param p_interfaceID
     * @param count
     * @return
     */
    public static String getNextActivenode(String p_interfaceID) {
        String nodeId = null;
        try {
            _logger.debug("Enter the method ", "  getNextActivenode");
            // get the active Node ID in robin round

            int lastusednode = RoundRobin(p_interfaceID);
            nodeId = p_interfaceID + "_" + lastusednode;
        } catch (Exception e) {
            _logger.error("Exception", "  " + e.getMessage());
            e.printStackTrace();
        }
        return nodeId;

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
        if (_logger.isDebugEnabled())
            _logger.debug("destroy Entered ", "  p_interfaceId:" + p_interfaceId);
        int nodes = 0;
        try {
            Set ketSetCode = null;
            Iterator iter = null;
            String key = null;
            if (BTSLUtil.isNullString(p_interfaceId))
                throw new Exception(" No Pool Id for destroying");
            String strINId = null;
            String fileCacheId = p_interfaceId;
            String interfaceId = null;
            StringTokenizer strTokens = new StringTokenizer(p_interfaceId, ",");
            if (_freeBucket != null) {
                ketSetCode = _freeBucket.keySet();
                iter = ketSetCode.iterator();
                _logger.info("destroy Destroying ", " pool of client objects for Interface ID=" + strINId);
                Vector objectList = null;

                while (strTokens.hasMoreElements()) {
                    strINId = strTokens.nextToken().trim();

                    try {
                        MAX_IP = Integer.parseInt(FileCache.getValue(fileCacheId, "NODES_SIZE_" + strINId));
                        LAST_USED = 0;
                        ZTEINHBLogger.logMessage("");
                        ZTEINHBLogger.logMessage("Pick  Circle = " + strINId + " and Total Nodes=" + MAX_IP);
                        // Get the Max pool size from the INFile.

                        nodes = Integer.parseInt(FileCache.getValue(fileCacheId, "NODES_SIZE_" + strINId));
                    } catch (Exception e) {
                        e.printStackTrace();
                        _logger.error("initialize ", " Exception e:" + e.getMessage());
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEPoolManager[initialize]", "", "", "", "While instantiation of the Client objects MAX_POOL_SIZE/POOL_SLEEP is either not defined or not numeric, in the INFile");
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_OBJECT_POOL_INITIALIZATION);
                    }

                    for (int node = 1; node <= nodes; node++) {
                        interfaceId = strINId + "_" + node;
                        while (iter.hasNext()) {
                            key = (String) iter.next();

                            if (interfaceId.equals(key.trim())) {
                                _logger.info("destroy ", " Destroying ClientObjects from _freeBucket for Interface ID=" + key);
                                try {
                                    if (_ZTEHeartBeatController != null)
                                        _ZTEHeartBeatController.stopHeartBeat(key);
                                    objectList = (Vector) _freeBucket.remove(key);
                                    if (objectList != null) {
                                        int size = objectList.size();
                                        for (int i = 0; i < size; i++) {
                                            ZTEINSocketWrapper socketWrapper = (ZTEINSocketWrapper) objectList.get(i);
                                            socketWrapper.destroy(fileCacheId);
                                        }
                                    }
                                } catch (Exception e) {
                                    // e.printStackTrace();
                                    // Confirm for event handling.
                                    objectList = null;
                                }
                            }// end of if
                        }// end of inner-while.
                    }// end of the for loop
                }// end of outer while
            }// end of if
            if (_busyBucket != null) {
                ketSetCode = _busyBucket.keySet();
                iter = ketSetCode.iterator();
                _logger.info("destroy ", " Destroying pool of client objects from busyBucket for Interface ID=" + strINId);
                Vector objectList = null;

                while (strTokens.hasMoreElements()) {
                    strINId = strTokens.nextToken().trim();

                    try {
                        MAX_IP = Integer.parseInt(FileCache.getValue(fileCacheId, "NODES_SIZE_" + strINId));
                        LAST_USED = 0;
                        ZTEINHBLogger.logMessage("");
                        ZTEINHBLogger.logMessage("Pick  Circle = " + strINId + " and Total Nodes=" + MAX_IP);
                        // Get the Max pool size from the INFile.

                        nodes = Integer.parseInt(FileCache.getValue(fileCacheId, "NODES_SIZE_" + strINId));
                    } catch (Exception e) {
                        e.printStackTrace();
                        _logger.error("initialize ", "Exception e:" + e.getMessage());
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEPoolManager[initialize]", "", "", "", "While instantiation of the Client objects MAX_POOL_SIZE/POOL_SLEEP is either not defined or not numeric, in the INFile");
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_OBJECT_POOL_INITIALIZATION);
                    }

                    for (int node = 1; node <= nodes; node++) {
                        interfaceId = strINId + "_" + node;

                        while (iter.hasNext()) {
                            key = (String) iter.next();
                            if (interfaceId.equals(key.trim())) {
                                _logger.info("destroy ", " Destroying ClientObjects from _busyBucket for Interface ID=" + key);
                                try {
                                    if (_ZTEHeartBeatController != null)
                                        _ZTEHeartBeatController.stopHeartBeat(key);
                                    objectList = (Vector) _busyBucket.remove(key);
                                    if (objectList != null) {
                                        int size = objectList.size();
                                        for (int i = 0; i < size; i++) {
                                            ZTEINSocketWrapper socketWrapper = (ZTEINSocketWrapper) objectList.get(i);
                                            socketWrapper.destroy(fileCacheId);
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
            }
        } catch (Exception e) {
            e.printStackTrace();
            _logger.error("destroy ", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEPoolManager[destroy]", "", "", "", "While destorying the Client objects got the Exception " + e.getMessage());
        }// end of catch-Exception
        finally {
            if (_logger.isDebugEnabled())
                _logger.debug("destroy", " Exiting");
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
        if (_logger.isDebugEnabled())
            _logger.debug("destroy ", "Entered p_ids=" + p_interfaceId + " p_all=" + p_all);
        try {
            HashMap<String, Vector> map = null;
            Set ketSetCode = null;
            Iterator iter = null;
            String key = null;
            String fileCacheId = p_interfaceId;
            String interfaceId = null;
            int nodes = 0;
            if (BTSLUtil.isNullString(p_interfaceId))
                throw new Exception("No Pool Id for destroying");
            String strINId = null;
            StringTokenizer strTokens = new StringTokenizer(p_interfaceId, ",");
            if (_freeBucket != null) {
                map = new HashMap<String, Vector>(_freeBucket);

                ketSetCode = map.keySet();
                iter = ketSetCode.iterator();
                Vector objectList = null;
                while (strTokens.hasMoreElements()) {
                    strINId = strTokens.nextToken().trim();

                    try {
                        MAX_IP = Integer.parseInt(FileCache.getValue(fileCacheId, "NODES_SIZE_" + strINId));
                        LAST_USED = 0;
                        ZTEINHBLogger.logMessage("");
                        ZTEINHBLogger.logMessage("Pick  Circle = " + strINId + " and Total Nodes=" + MAX_IP);

                        nodes = Integer.parseInt(FileCache.getValue(fileCacheId, "NODES_SIZE_" + strINId));
                    } catch (Exception e) {
                        e.printStackTrace();
                        _logger.error("initialize ", " Exception e:" + e.getMessage());
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEPoolManager[initialize]", "", "", "", "While instantiation of the Client objects MAX_POOL_SIZE/POOL_SLEEP is either not defined or not numeric, in the INFile");
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_OBJECT_POOL_INITIALIZATION);
                    }

                    for (int node = 1; node <= nodes; node++) {
                        interfaceId = strINId + "_" + node;
                        while (iter.hasNext()) {
                            key = (String) iter.next();

                            if (interfaceId.equals(key.trim())) {
                                _logger.info("destroy ", " Destroying ClientObjects from _freeBucket for Interface ID=" + key);

                                try {
                                    if (_ZTEHeartBeatController != null)
                                        _ZTEHeartBeatController.stopHeartBeat(key);
                                    objectList = (Vector) _freeBucket.remove(key);
                                    if (objectList != null) {
                                        int size = objectList.size();
                                        for (int i = 0; i < size; i++) {
                                            ZTEINSocketWrapper socketWrapper = (ZTEINSocketWrapper) objectList.get(i);
                                            socketWrapper.destroy(fileCacheId);
                                        }
                                    }
                                } catch (Exception e) {
                                    // e.printStackTrace();
                                    objectList = null;
                                }
                            }// end of if
                        }// end of inner-while.
                    }
                }// end of outer while
            }// end of if
            if (_busyBucket != null) {
                map = new HashMap<String, Vector>(_busyBucket);

                ketSetCode = map.keySet();
                iter = ketSetCode.iterator();
                Vector objectList = null;
                while (strTokens.hasMoreElements()) {
                    strINId = strTokens.nextToken().trim();

                    try {
                        MAX_IP = Integer.parseInt(FileCache.getValue(fileCacheId, "NODES_SIZE_" + strINId));
                        LAST_USED = 0;
                        ZTEINHBLogger.logMessage("");
                        ZTEINHBLogger.logMessage("Pick  Circle = " + strINId + " and Total Nodes=" + MAX_IP);
                        // Get the Max pool size from the INFile.

                        nodes = Integer.parseInt(FileCache.getValue(fileCacheId, "NODES_SIZE_" + strINId));
                    } catch (Exception e) {
                        e.printStackTrace();
                        _logger.error("initialize ", " Exception e:" + e.getMessage());
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEPoolManager[initialize]", "", "", "", "While instantiation of the Client objects MAX_POOL_SIZE/POOL_SLEEP is either not defined or not numeric, in the INFile");
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_OBJECT_POOL_INITIALIZATION);
                    }

                    for (int node = 1; node <= nodes; node++) {
                        interfaceId = strINId + "_" + node;

                        while (iter.hasNext()) {
                            key = (String) iter.next();
                            if (interfaceId.equals(key.trim())) {
                                _logger.info("destroy", " Destroying ClientObjects from _busyBucket for Interface ID=" + key);
                                try {
                                    if (_ZTEHeartBeatController != null)
                                        _ZTEHeartBeatController.stopHeartBeat(key);
                                    objectList = (Vector) _busyBucket.remove(key);
                                    if (objectList != null) {
                                        int size = objectList.size();
                                        for (int i = 0; i < size; i++) {
                                            ZTEINSocketWrapper socketWrapper = (ZTEINSocketWrapper) objectList.get(i);
                                            socketWrapper.destroy(fileCacheId);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    objectList = null;
                                }
                            }
                        }// end of while.
                    }
                }
            }// end of if
        } catch (Exception e) {
            e.printStackTrace();
            _logger.error("destroy ", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEPoolManager[destroy]", "", "", "", "While destorying the Client objects got the Exception " + e.getMessage());
        }// end of catch-Exception
        finally {
            if (_logger.isDebugEnabled())
                _logger.debug("destroy ", "Exiting");
        }// end of finally
    }// end of destroy

    public static String printMap(HashMap<String, Vector<Object>> _bucket) {
        String keyValue = "";
        if (_bucket != null) {
            Iterator it = _bucket.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                if (keyValue.equalsIgnoreCase(""))
                    keyValue = "Key= " + pairs.getKey() + " ,Value= " + pairs.getValue();
                else
                    keyValue = keyValue + ",Key= " + pairs.getKey() + " ,Value= " + pairs.getValue();
            }

        }
        return keyValue;
    }
}
