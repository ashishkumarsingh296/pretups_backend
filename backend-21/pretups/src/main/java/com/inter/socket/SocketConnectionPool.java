package com.inter.socket;

import com.inter.connection.BTSLConnection;
import com.inter.connection.BTSLConnectionPool;

/**
 * @(#)SocketConnectionPool.java
 *                               Copyright(c) 2005, Bharti Telesoft Int. Public
 *                               Ltd.
 *                               All Rights Reserved
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Author Date History
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Abhijit Chauhan June 22,2005 Initial Creation
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               --
 */
public class SocketConnectionPool extends BTSLConnectionPool {

    private String _ip;
    private int _port;

    public SocketConnectionPool(String p_ip, int p_port, String p_poolID, int p_poolSize, int p_sockectTimeout) {
        _ip = p_ip;
        _port = p_port;
        initializePool(p_poolID, p_poolSize, p_sockectTimeout);
        if (_log.isDebugEnabled())
            _log.debug("SocketConnectionPool", "Entered " + getParameters());
        SocketConnection socketConnection = null;
        for (int i = 1; i <= _poolSize; i++) {
            socketConnection = (SocketConnection) getNewConnection();
            _freePool.add(socketConnection);
        }
        if (_log.isDebugEnabled())
            _log.debug("SocketConnectionPool", "Exiting " + getParameters());
    }

    public BTSLConnection getNewConnection() {
        if (_log.isDebugEnabled())
            _log.debug("getNewConnection", getParameters());
        SocketConnection socketConnection = null;
        try {
            socketConnection = new SocketConnection(_ip, _port);
            socketConnection.setTimeout(_timeout);
        } catch (Exception ex) {
            _log.error("getNewConnection", "Exception:" + ex.getMessage() + getParameters());
            ex.printStackTrace();
            // PreTUplogs.errLog("0", Events.BIND,
            // "EricPool[getNewEricConnection]", "0",
            // "Error opening socket due to " + skex.toString(), "500");
            socketConnection = null;
        }
        return socketConnection;
    }

    public String getParameters() {
        return ("_ip:" + _ip + " _port:" + _port + " p_poolID:" + _poolID + " _poolSize:" + _poolSize + " _sockectTimeout:" + _timeout + " _freePool size:" + _freePool.size() + " _busyPool size:" + _busyPool.size());
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
