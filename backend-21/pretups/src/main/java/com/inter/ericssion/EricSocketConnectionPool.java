package com.inter.ericssion;

/**
 * @(#)EricSocketConnectionPool.java
 *                                   Copyright(c) 2005, Bharti Telesoft Int.
 *                                   Public Ltd.
 *                                   All Rights Reserved
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   -----------
 *                                   Author Date History
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   -----------
 *                                   Abhijit Chauhan June 22,2005 Initial
 *                                   Creation
 *                                   Ashish Kumar July 16,2006 Modification
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   ----------
 */

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.util.Constants;
import com.inter.socket.SocketConnection;

public class EricSocketConnectionPool {
    private static Log _log = LogFactory.getLog(EricSocketConnectionPool.class.getName());
    public static HashMap _ericHash = null;

    /**
     * Constructor is used to put the number of socket connections(defined by
     * POOL_SIZE, in INFile)into a storage for each interface.
     * 
     * @param String
     *            p_ids
     */
    public EricSocketConnectionPool(String p_ids) {
        if (_log.isDebugEnabled())
            _log.debug("EricSocketConnectionPool", "Entered p_ids=" + p_ids);
        int MAX_ERIC_POOL_SIZE = 0;
        String ericPoolINIds = null;
        try {
            if (_ericHash == null)
                _ericHash = new HashMap(10);
            ericPoolINIds = p_ids;
            if (ericPoolINIds.trim().length() <= 0)
                throw new Exception(" Ericsson Pool IN Ids not defined in Constansts file");

            String strINId = null;

            StringTokenizer strTokens = new StringTokenizer(ericPoolINIds, ",");
            while (strTokens.hasMoreElements()) {
                strINId = strTokens.nextToken().trim();
                _log.info("EricSocketConnectionPool", "Creating socket pool for Interface ID=" + strINId);
                try {
                    MAX_ERIC_POOL_SIZE = Integer.parseInt(FileCache.getValue(strINId, "POOL_SIZE"));
                } catch (Exception e) {
                    throw new Exception("Check the POOL_SIZE defined in INFile for Interface ID=" + strINId);
                }
                _log.info("EricSocketConnectionPool", "Creating Pool for IN::" + strINId + " of size " + MAX_ERIC_POOL_SIZE);
                SocketConnection socketConnection = null;
                for (int i = 1; i <= MAX_ERIC_POOL_SIZE; i++) {
                    Thread.sleep(100);
                    socketConnection = getNewEricConnection(strINId);
                    _log.info("EricSocketConnectionPool", "IN::" + strINId + " Pool No " + i + " socketConnection = " + socketConnection + " sleep time between the connection creation is 100 miliseconds" + " LocalPort socketConnection.getLocalPort()::" + socketConnection.getLocalPort());
                    _ericHash.put(strINId + "_" + i, socketConnection);
                }
                _log.info("EricSocketConnectionPool", "Created Pool for Interface ID=" + strINId + " Hash Map=" + _ericHash.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricSocketConnectionPool[constructor]", "", "", "", "Exception while creating socket pool for IDs=" + p_ids + " Getting=" + e.getMessage());
        } finally {
            _log.info("EricSocketConnectionPool", "Exiting for IDs=" + p_ids);
        }
    }

    /**
     * This method is used to get a connection from the Pool.
     * 
     * @param String
     *            p_strINId
     * @return SocketConnection
     */
    public static synchronized SocketConnection getEricConnection(String p_strINId) {
        if (_log.isDebugEnabled())
            _log.debug("getEricConnection", "Entered p_strINId=" + p_strINId);
        SocketConnection socketConnection = null;
        try {
            if (_ericHash == null || _ericHash.size() == 0) {
                // Added so that we are able to reconnect automatically if on
                // server startup pool was not created
                _log.error("getEricConnection", "No Eric Connection in Eric Hash , reconnecting for all IDs ");
                String poolIDs = Constants.getProperty("SOCKET_CONN_POOL_IDS");
                if (_log.isDebugEnabled())
                    _log.debug("initialize", "poolIDs = " + poolIDs);
                if (poolIDs != null)
                    new EricSocketConnectionPool(poolIDs);
            }
            if (_ericHash == null || _ericHash.size() == 0) {
                _log.error("getEricConnection", "Still No Eric Connection in Eric Hash after reconnecting for all IDs ");
                return null;
            }
            socketConnection = (SocketConnection) _ericHash.get(p_strINId);
            if (_log.isDebugEnabled())
                _log.debug("getEricConnection", "Exiting...ericHash" + _ericHash + " ..socketConnection=" + socketConnection + "::p_strINId=" + p_strINId);
            return socketConnection;
        } catch (Exception e) {
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricSocketConnectionPool[getEricConnection]", p_strINId, "", "", "Not able to get Socket from Pool, getting Exception " + e.getMessage());
            _log.error("getEricConnection", "Exception " + e.toString());
            e.printStackTrace();
            return null;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getEricConnection", "Exiting socketConnection = " + socketConnection);
        }
    }

    /**
     * This method is used to create socket connection to the IN. It returns the
     * SocketConnection object.
     * 
     * @param String
     *            p_strINId
     * @param String
     *            p_port
     * @return SocketConnection
     */
    public static SocketConnection getNewEricConnection(String p_strINId) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getNewEricConnection", "Entered p_strINId=" + p_strINId);
        SocketConnection socketConnection = null;
        int connTimeOut = 0;
        try {
            // get the IP and port of the IN to connect to
            String strINIP = FileCache.getValue(p_strINId, "IP");
            int intINPort = Integer.parseInt(FileCache.getValue(p_strINId, "PORT"));
            // open the socket connection to the IN
            socketConnection = new SocketConnection(strINIP, intINPort);
            // Getting the socket connection time out from the INFile.
            try {
                connTimeOut = Integer.parseInt(FileCache.getValue(p_strINId, "SOCKET_TIMEOUT"));
            } catch (Exception e) {
                throw new Exception("Check the SOCKET_TIMEOUT defined in INFile for Interface ID=" + p_strINId);
            }
            // Set the socket connection time out for the opening socket.
            socketConnection.setTimeout(connTimeOut);
            if (_log.isDebugEnabled())
                _log.debug("getNewEricConnection", "SocketConnection = " + socketConnection.toString() + "InterfaceID =" + p_strINId + "connTimeOut =" + connTimeOut);
        } catch (Exception ex) {
            ex.printStackTrace();
            _log.error("getNewEricConnection", "Error opening socket due to " + ex.toString());
            socketConnection = null;
            throw ex;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getNewEricConnection", "Exiting socketConnection =" + socketConnection);
        }
        return socketConnection;
    }

    /**
     * This method is used to get a Socket connection for corresponding
     * InterfaceID.
     * 
     * @param String
     *            p_strINId
     * @param String
     *            p_strInIP
     * @param int p_intInPort
     * @return SocketConnection
     */
    public synchronized static SocketConnection reconnectEricConnection(String p_strINId, String p_strInIP, int p_intInPort) {
        _log.info("reconnectEricConnection", "Entered p_strINId=" + p_strINId + " p_strInIP:" + p_strInIP + " p_intInPort: " + p_intInPort);
        SocketConnection socketConnection = null;
        int connTimeOut = 0;
        try {
            socketConnection = getEricConnection(p_strINId);
            if (socketConnection != null) {
                try {
                    // Closing the streams
                    socketConnection.getBufferedReader().close();
                    socketConnection.getPrintWriter().close();

                    socketConnection.close();
                    socketConnection = null;
                } catch (Exception ex) {
                    socketConnection = null;
                }
            }
            _ericHash.remove(p_strINId);
            _log.info("reconnectEricConnection", "After removing the connection for Interface ID=" + p_strINId + " from _ericHash ::" + _ericHash);
            socketConnection = new SocketConnection(p_strInIP, p_intInPort);

            // Fetching the socket connection time out from INFile.
            try {
                String interfaceID = p_strINId.substring(0, p_strINId.indexOf("_"));
                connTimeOut = Integer.parseInt(FileCache.getValue(interfaceID, "SOCKET_TIMEOUT"));
            } catch (Exception e) {
                e.printStackTrace();
                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricSocketConnectionPool[reconnectEricConnection]", p_strINId, p_strInIP + " " + p_intInPort, "", "Check the SOCKET_TIMEOUT defined in INFile for Interface ID=" + p_strINId + " Exception e = " + e.getMessage() + " Thus taking default");
                connTimeOut = 5000;
            }
            socketConnection.setTimeout(connTimeOut);
            _ericHash.put(p_strINId, socketConnection);
            _log.info("reconnectEricConnection", "After adding the new connection for Interface ID=" + p_strINId + " to  _ericHash ::" + _ericHash + "connTimeOut= " + connTimeOut);
            return socketConnection;
        } catch (Exception e) {
            EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricSocketConnectionPool[reconnectEricConnection]", p_strINId, p_strInIP + " " + p_intInPort, "", "Exception during reconnection to IN " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("reconnectEricConnection", "Exiting socketConnection:" + socketConnection);
        }
    }

    /**
     * Method to close the streams and socket connection on server shutdown
     * 
     * @param p_ids
     */
    public void destroy(String p_ids) {
        if (_log.isDebugEnabled())
            _log.debug("destroy", "Entered p_ids=" + p_ids);
        String ericPoolINIds = null;
        try {
            HashMap map = new HashMap(_ericHash);
            Set ketSetCode = null;
            Iterator iter = null;
            String key = null;
            ericPoolINIds = p_ids;
            if (ericPoolINIds.trim().length() <= 0)
                throw new Exception(" No Ericsson Pool IN Id for destroying");

            String strINId = null;

            StringTokenizer strTokens = new StringTokenizer(ericPoolINIds, ",");
            SocketConnection socketConnection = null;
            while (strTokens.hasMoreElements()) {
                strINId = strTokens.nextToken().trim();
                ketSetCode = map.keySet();
                iter = ketSetCode.iterator();
                _log.info("destroy", "Destroying socket pool for Interface ID=" + strINId);

                while (iter.hasNext()) {
                    key = (String) iter.next();
                    String interfaceID = key.substring(0, key.indexOf("_"));
                    _log.info("destroy", "Destroying socket pool for Interface ID=" + strINId + " In previous pool=" + interfaceID);
                    if (strINId.equalsIgnoreCase(interfaceID)) {
                        try {
                            socketConnection = (SocketConnection) _ericHash.remove(key);
                            _log.info("destroy", "IN::" + strINId + " Pool No " + key + " socketConnection = " + socketConnection);
                            if (socketConnection != null) {
                                socketConnection.close();
                                socketConnection = null;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            socketConnection = null;
                        }
                    }
                }
                if (_log.isDebugEnabled())
                    _log.debug("destroy", "destroyed Pool " + _ericHash.toString());
            }
            if (_log.isDebugEnabled())
                _log.debug("destroy", "Exiting");
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricSocketConnectionPool[destroy]", "", "", "", "Exception " + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("destroy", "Exiting for IDs=" + p_ids);
        }
    }

    /**
     * For destroying all the connection made
     * 
     * @param p_ids
     * @param p_all
     */
    public void destroy(String p_ids, String p_all) {
        if (_log.isDebugEnabled())
            _log.debug("destroy", "Entered p_ids=" + p_ids + " p_all=" + p_all);
        String ericPoolINIds = null;
        try {
            HashMap map = new HashMap(_ericHash);
            Set ketSetCode = map.keySet();
            Iterator iter = ketSetCode.iterator();
            String key = null;
            ericPoolINIds = p_ids;
            if (ericPoolINIds.trim().length() <= 0)
                throw new Exception(" No Ericsson Pool IN Id for destroying");

            SocketConnection socketConnection = null;

            while (iter.hasNext()) {
                key = (String) iter.next();
                _log.info("destroy", "Destroying socket pool for Interface ID=" + key);
                try {
                    socketConnection = (SocketConnection) _ericHash.remove(key);
                    if (socketConnection != null) {
                        socketConnection.close();
                        socketConnection = null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    socketConnection = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricSocketConnectionPool[destroy]", "", "", "", "Exception " + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("destroy", "Exiting for IDs=" + p_ids);
        }
    }
}
