package com.inter.ferma6;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.HashMap;

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.pretups.inter.cache.FileCache;
import com.inter.connection.BTSLConnection;
import com.inter.connection.BTSLConnectionPool;

/**
 * @(#)Ferma6UrlConnectionPool.java
 *                                  Copyright(c) 2005, Bharti Telesoft Int.
 *                                  Public Ltd.
 *                                  All Rights Reserved
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  ---------
 *                                  Author Date History
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  ---------
 *                                  Abhijit Chauhan Oct 10,2005 Initial Creation
 *                                  Manoj Kumar Oct 15,2005 Modification
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  --------
 */
public class Ferma6UrlConnectionPool extends BTSLConnectionPool {

    private String _urlStr;
    private String _userName = null;
    private String _password = null;
    private String _protocolVersion = null;
    private boolean _underProcess = false;
    private String _poolId = null;
    private String _keepAlive = null;
    private static int _transactionID = 0;
    private static Ferma6RequestResponse _formatter = new Ferma6RequestResponse();

    public Ferma6UrlConnectionPool(String p_urlStr, String p_poolID, int p_poolSize, int p_timeout, String p_keepAlive) {
        if (_log.isDebugEnabled())
            _log.debug("Ferma6UrlConnectionPool", "Entered p_urlStr: " + p_urlStr + " p_poolID: " + p_poolID + " p_poolSize: " + p_poolSize + " p_timeout: " + p_timeout);

        _userName = FileCache.getValue(p_poolID, "USER");
        _password = FileCache.getValue(p_poolID, "PASSWORD");
        _protocolVersion = FileCache.getValue(p_poolID, "PROTOCOLVERSION");
        _urlStr = p_urlStr;
        _keepAlive = p_keepAlive;
        initializePool(p_poolID, p_poolSize, p_timeout);
        if (_log.isDebugEnabled())
            _log.debug("Ferma6UrlConnectionPool", "Entered " + getParameters());
        Ferma6UrlConnection urlConnection = null;
        for (int i = 1; i <= _poolSize; i++) {
            urlConnection = (Ferma6UrlConnection) getNewConnection();
            _freePool.add(urlConnection);
        }
        if (_log.isDebugEnabled())
            _log.debug("Ferma6UrlConnectionPool", "Exiting " + getParameters());
    }

    public BTSLConnection getNewConnection() {
        if (_log.isDebugEnabled())
            _log.debug("getNewConnection", getParameters());
        Ferma6UrlConnection urlConnection = null;
        try {
            _underProcess = true;
            urlConnection = new Ferma6UrlConnection(_urlStr, 20000, 20000, _keepAlive);
            urlConnection.setTimeout(_timeout);
        } catch (Exception ex) {
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Ferma6UrlConnectionPool[getNewConnection]", "", " ", " ", "Exception while creating new connection=" + ex.getMessage());
            _log.error("getNewConnection", "Exception: ex" + ex.getMessage());
            urlConnection = null;
        } finally {
            _underProcess = false;
        }
        return urlConnection;
    }

    public void destroy() {
        String requestStr = null;
        String responseStr = null;
        String status = null;
        if (_log.isDebugEnabled())
            _log.debug("destroy", "_urlStr " + _urlStr);
        try {
            HashMap map = new HashMap();
            PrintWriter out = null;
            Ferma6UrlConnection fermaUrlCon = new Ferma6UrlConnection(_urlStr, 20000, 20000, _keepAlive);
            fermaUrlCon.setPrintWriter();
            // map.put("FERMA_INTERFACE_ID",fermaUrlCon.getInterfaceID());
            map.put("IN_TXN_ID", String.valueOf(++_transactionID));
            // generate the XML request
            requestStr = _formatter.generateRequest(Ferma6RequestResponse.ACTION_LOGOUT, map);
            if (_log.isDebugEnabled())
                _log.debug("logout", "requestStr: " + requestStr);
            fermaUrlCon.setPrintWriter();
            out = fermaUrlCon.getPrintWriter();
            // send logout request to Ferma IN
            out.println(requestStr);
            out.flush();
            fermaUrlCon.setBufferedReader();
            BufferedReader in = fermaUrlCon.getBufferedReader();
            StringBuffer sbf = new StringBuffer();
            while ((responseStr = in.readLine()) != null) {
                sbf.append(responseStr);
            }
            in.close();
            responseStr = sbf.toString();
            if (_log.isDebugEnabled())
                _log.debug("logout", " responseStr: " + responseStr);
            if (responseStr != null) {
                HashMap responseMap = _formatter.parseResponse(Ferma6RequestResponse.ACTION_LOGOUT, responseStr);
                status = (String) responseMap.get("Status");
                if (status.equalsIgnoreCase("0"))
                    if (_log.isDebugEnabled())
                        _log.debug("destroy", " logout", "Logout Successsfully");
                    else if (_log.isDebugEnabled())
                        _log.debug("destroy", " logout", "Logout failed");
            } else if (_log.isDebugEnabled())
                _log.debug("destroy", " logout", "Logout failed");
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Ferma6UrlConnectionPool[destroy]", e.getMessage(), " ", " ", "Exception while destroying=" + e.getMessage());
            _log.error("destroy", "Exception: e" + e.getMessage());
        }
        /*
         * this code for persistence coneection at present we are not using
         * try
         * {
         * if(_busyPool.size()>0)
         * {
         * for(int i=0;i<_busyPool.size();i++)
         * {
         * fermaUrlConnection=(FermaUrlConnection)_busyPool.get(i);
         * fermaUrlConnection.close();
         * }
         * }
         * }
         * catch(Exception e)
         * {
         * _log.error("destroy busyPool","Exception e:"+e.getMessage());
         * e.printStackTrace();
         * }
         * try
         * {
         * if(_freePool.size()>0)
         * {
         * for(int j=0;j<_freePool.size();j++)
         * {
         * fermaUrlConnection=(FermaUrlConnection)_freePool.get(j);
         * fermaUrlConnection.close();
         * }
         * }
         * }
         * catch(Exception e)
         * {
         * _log.error("destroy freePool","Exception e:"+e.getMessage());
         * e.printStackTrace();
         * }
         */
    }

    public String getParameters() {
        return ("_urlStr" + _urlStr + " p_poolID:" + _poolID + " _poolSize:" + _poolSize + " _sockectTimeout:" + _timeout + " _keepAlive:" + _keepAlive + " _freePool size:" + _freePool.size() + " _busyPool size:" + _busyPool.size());
    }

    public boolean isUnderProcess() {
        return _underProcess;
    }

    public void setUnderProcess(boolean process) {
        _underProcess = process;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
    }
}
