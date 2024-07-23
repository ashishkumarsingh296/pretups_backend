package com.inter.alcatel;

/**
 * @(#)TelnetConnectionPool.java
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
 *                               Gurjeet Singh Bedi Oct 19,2005 Initial Creation
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               --
 *                               Connection pool class for the interface
 */

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.inter.connection.BTSLConnection;
import com.inter.connection.BTSLConnectionPool;

public class TelnetConnectionPool extends BTSLConnectionPool {
    private String _poolID;
    private String _urlStr;
    private String _userName = null;
    private String _password = null;
    private String _prompt = null;
    private boolean _underProcess = false;

    public TelnetConnectionPool(String p_urlStr, String p_poolID, int p_poolSize, int p_timeout) {
        _userName = FileCache.getValue(p_poolID, "USER");
        _password = FileCache.getValue(p_poolID, "PASSWORD");
        _prompt = FileCache.getValue(p_poolID, "PROMPT");
        if (_log.isDebugEnabled())
            _log.debug("TelnetConnectionPool", "Entered p_urlStr: " + p_urlStr + " p_poolID: " + p_poolID + " p_poolSize: " + p_poolSize + " p_timeout: " + p_timeout);
        _urlStr = p_urlStr;
        _poolID = p_poolID;
        initializePool(p_poolID, p_poolSize, p_timeout);
        if (_log.isDebugEnabled())
            _log.debug("TelnetConnectionPool", "Entered " + getParameters());
        TelnetConnection telnetConnection = null;
        for (int i = 1; i <= _poolSize; i++) {
            try {
                telnetConnection = (TelnetConnection) getNewConnection();
            } catch (Exception ex) {
                _log.error("TelnetConnectionPool", "Exception:" + ex.getMessage() + getParameters());
                ex.printStackTrace();
            }
            _freePool.add(telnetConnection);
        }
        if (_log.isDebugEnabled())
            _log.debug("TelnetConnectionPool", "Exiting " + getParameters());
    }

    public synchronized BTSLConnection getNewConnection() throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getNewConnection", getParameters());
        TelnetConnection telnetConnection = null;
        try {
            _underProcess = true;// class level
            telnetConnection = new TelnetConnection(_poolID, _urlStr, _userName, _password);
            if (telnetConnection == null)
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_CONNECTION_NULL);
            telnetConnection.setTimeout(_timeout);
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception ex) {
            _log.error("getNewConnection", "Exception:" + ex.getMessage() + getParameters());
            ex.printStackTrace();
            telnetConnection = null;
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_CONNECTION_NULL);
        } finally {
            _underProcess = false;// class level
        }
        return telnetConnection;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

    public boolean isUnderProcess() {
        return _underProcess;
    }

    public void setUnderProcess(boolean process) {
        _underProcess = process;
    }

}
