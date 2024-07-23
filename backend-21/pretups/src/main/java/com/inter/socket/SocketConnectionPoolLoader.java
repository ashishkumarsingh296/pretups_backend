package com.inter.socket;

/**
 * @(#)SocketConnectionPoolLoader.java
 *                                     Copyright(c) 2005, Bharti Telesoft Int.
 *                                     Public Ltd.
 *                                     All Rights Reserved
 *                                     ----------------------------------------
 *                                     --
 *                                     ----------------------------------------
 *                                     ---------------
 *                                     Author Date History
 *                                     ----------------------------------------
 *                                     --
 *                                     ----------------------------------------
 *                                     ---------------
 *                                     Abhijit Chauhan June 22,2005 Initial
 *                                     Creation
 *                                     Ashish July 13,2005 Modification
 *                                     ----------------------------------------
 *                                     --
 *                                     ----------------------------------------
 *                                     --------------
 */
import java.util.HashMap;

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

/*
 * 
 * Modification for this class includes(Exception Handling) following
 * 1.Check the Values fetched from FileCache.
 * 2.While Parsing different values(port,poolsize and socket timeout) handle
 * proper exception and event.
 */

public class SocketConnectionPoolLoader {
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private String[] _ids = null;
    private static HashMap _map = new HashMap();

    public SocketConnectionPoolLoader(String p_ids) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("SocketConnectionPoolLoader", "Entered p_ids=" + p_ids);
        _map = new HashMap();
        _ids = p_ids.split(",");
        String id;
        String hostIP = null;
        int port = 0;
        int poolSize = 0;
        int socketTimeout = 0;
        SocketConnectionPool pool = null;
        for (int i = 0; i < _ids.length; i++) {
            id = _ids[i];
            try {
                // Getting the IP from the FileCache
                hostIP = FileCache.getValue(id, "IP");
                if (hostIP == null) {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SocketConnectionPoolLoader[CONSTUCTOR]", "", " INTERFACE ID = " + id, " IP =" + hostIP, "Host IP is not defined in INFile");
                    throw new BTSLBaseException(this, "SocketConnectionPoolLoader[CONSTRUCTOR]", InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
                }
                // Getting the port from the FileCache and then parse it for
                // integer value.
                String portStr = FileCache.getValue(id, "PORT");
                if (portStr == null) {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SocketConnectionPoolLoader[CONSTUCTOR]", "", " INTERFACE ID = " + id, " PORT = " + portStr, "PORT is not defined in INFile");
                    throw new BTSLBaseException(this, "SocketConnectionPoolLoader[CONSTRUCTOR]", InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
                }
                try {
                    port = Integer.parseInt(portStr);
                } catch (Exception be) {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SocketConnectionPoolLoader[CONSTUCTOR]", "", " INTERFACE ID = " + id, " PORT = " + portStr, "PORT should be Numeric in INFile");
                    throw new BTSLBaseException(this, "SocketConnectionPoolLoader[CONSTRUCTOR]", InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
                }
                // Getting the pool size from the file cache and then parse for
                // integer value.
                String poolSizeStr = FileCache.getValue(id, "POOL_SIZE");
                if (poolSizeStr == null) {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SocketConnectionPoolLoader[CONSTUCTOR]", "", " INTERFACE ID = " + id, " POOL_SIZE = " + poolSizeStr, "POOL_SIZE is not defined in INFile");
                    throw new BTSLBaseException(this, "SocketConnectionPoolLoader[CONSTRUCTOR]", InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
                }
                try {
                    poolSize = Integer.parseInt(poolSizeStr);
                } catch (Exception e) {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SocketConnectionPoolLoader[CONSTUCTOR]", "", " INTERFACE ID = " + id, " POOL_SIZE = " + poolSizeStr, "POOL_SIZE should be Numeric in INFile");
                    throw new BTSLBaseException(this, "SocketConnectionPoolLoader[CONSTRUCTOR]", InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
                }
                // Getting the socket connection time out from FileCache and
                // then parse it for integer value.
                String socketTimeoutStr = FileCache.getValue(id, "SOCKET_TIMEOUT");
                if (socketTimeoutStr == null) {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SocketConnectionPoolLoader[CONSTUCTOR]", "", " INTERFACE ID = " + id, " SOCKET_TIMEOUT = " + socketTimeoutStr, "SOCKET_TIMEOUT is not defined in INFile");
                    throw new BTSLBaseException(this, "SocketConnectionPoolLoader[CONSTRUCTOR]", InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
                }
                try {
                    socketTimeout = Integer.parseInt(socketTimeoutStr);
                } catch (Exception e) {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SocketConnectionPoolLoader[CONSTUCTOR]", "", " INTERFACE ID = " + id, " SOCKET_TIMEOUT = " + socketTimeoutStr, "SOCKET_TIMEOUT should be Numeric in INFile");
                    throw new BTSLBaseException(this, "SocketConnectionPoolLoader[CONSTRUCTOR]", InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
                }
                pool = new SocketConnectionPool(hostIP, port, id, poolSize, socketTimeout);
                _map.put(id, pool);
            } catch (BTSLBaseException be) {
                if (_log.isDebugEnabled())
                    _log.debug("SocketConnectionPoolLoader", "BTSLBaseException be = " + be.getMessage());
                throw be;
            } catch (Exception e) {
                _log.error("SocketConnectionPoolLoader", "Exception e:" + e.getMessage() + " id=" + id);
                e.printStackTrace();
                // Check the following fields in INFile "+" hostIP = "+hostIP+"
                // port= "+ port + "poolSize = "+poolSize +" socketTimeout =
                // "+socketTimeout
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SocketConnectionPoolLoader[CONSTUCTOR]", "", " INTERFACE ID = " + id, "", "Check the following fields in INFile" + " IP ,PORT, POOL_SIZE, SOCKET_TIMEOUT Exception e = " + e.getMessage());
                throw new BTSLBaseException(this, "SocketConnectionPoolLoader[CONSTRUCTOR]", InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
            } finally {
                if (_log.isDebugEnabled())
                    _log.debug("SocketConnectionPoolLoader", "id = " + id + " hostIP = " + hostIP + " port= " + port + "poolSize = " + poolSize + " socketTimeout = " + socketTimeout);
            }
        }
        if (_log.isDebugEnabled())
            _log.debug("SocketConnectionPoolLoader", "Exiting");
    }

    public static SocketConnectionPool getPool(String p_id) {
        return (SocketConnectionPool) _map.get(p_id);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
