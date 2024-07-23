package com.inter.connection;

import java.net.URL;
import java.util.Vector;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.inter.socket.SocketConnection;

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
public abstract class BTSLConnectionPool {

    protected Vector _freePool = null;
    protected Vector _busyPool = null;
    protected int _poolSize = 0;
    protected int _timeout = 0;
    protected String _poolID = null;
    protected Log _log = LogFactory.getLog(this.getClass().getName());

    public abstract BTSLConnection getNewConnection() throws BTSLBaseException;

    public void initializePool(String p_poolID, int p_poolSize, int p_socketTimeout) {
        _poolSize = p_poolSize;
        _poolID = p_poolID;
        _timeout = p_socketTimeout;
        _freePool = new Vector();
        _busyPool = new Vector();
    }

    public synchronized BTSLConnection getConnection() throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getConnection", getParameters());
        BTSLConnection btslConnection = null;
        try {
            if (_freePool.size() == 0) {
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_CONNECTION_NULL);
            }
            btslConnection = (BTSLConnection) _freePool.remove(0);
            btslConnection.setAcquiredTime(System.currentTimeMillis());
            btslConnection.flush();
            _busyPool.add(btslConnection);
            return btslConnection;
        } catch (Exception ex) {
            _log.error("getConnection", "Exception:" + ex.getMessage() + getParameters());
            ex.printStackTrace();
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_CONNECTION_NULL);
        }
    }

    public synchronized void freeConnection(BTSLConnection connection) {
        if (_log.isDebugEnabled())
            _log.debug("freeConnection", getParameters());
        _busyPool.remove(connection);
        _freePool.add(connection);
    }

    public void replaceBusyConnection(BTSLConnection p_oldConnection, BTSLConnection p_newConnection) {
        if (_log.isDebugEnabled())
            _log.debug("replaceBusyConnection", getParameters());
        _busyPool.remove(p_oldConnection);
        _busyPool.add(p_newConnection);
    }

    public String getParameters() {
        return (" p_poolID:" + _poolID + " _poolSize:" + _poolSize + " _freePool size:" + _freePool.size() + " _busyPool size:" + _busyPool.size());
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
