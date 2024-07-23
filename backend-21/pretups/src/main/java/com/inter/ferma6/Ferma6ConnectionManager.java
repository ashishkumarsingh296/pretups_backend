package com.inter.ferma6;

/**
 * @(#)Ferma6ConnectionManager.java
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
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  --------
 */
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;

public class Ferma6ConnectionManager {
    private String _fermaInterfaceID = null;
    private String _inID = null;
    private HttpURLConnection _urlConnection = null;
    private String _userName = null;
    private String _password = null;
    private String _protocolVersion = null;
    private String _url = null;
    private String _status = null;
    private static Ferma6RequestFormatter _formatter = new Ferma6RequestFormatter();
    private int _transactionID = 0;
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private long _lastLoginTime = 0;
    private long _minLoginRetryTime = 0;

    public Ferma6ConnectionManager(String p_inID) {
        _inID = p_inID;
    }

    public synchronized void login(String p_url) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("login", "Entered p_url: " + p_url + " _inID: " + _inID + " _minLoginRetryTime: " + _minLoginRetryTime);
        long currentTime = System.currentTimeMillis();
        _minLoginRetryTime = Integer.parseInt(FileCache.getValue(_inID, "MIN_LOGIN_RETRY_TIME"));
        if (_log.isDebugEnabled())
            _log.debug("login", "currentTime: " + currentTime + " _lastLoginTime: " + _lastLoginTime + " minLoginRetryTime: " + _minLoginRetryTime);
        if (_lastLoginTime != 0 && ((currentTime - _lastLoginTime) < _minLoginRetryTime)) {
            if (_log.isDebugEnabled())
                _log.debug("login", "Skipping Login Request for IN ID: " + _inID);
            return;
        }
        Ferma6UrlConnection fermaUrlConnection = null;
        PrintWriter out = null;
        BufferedReader bufferedReader = null;
        try {
            _userName = FileCache.getValue(_inID, "USER");
            _password = FileCache.getValue(_inID, "PASSWORD");
            _protocolVersion = FileCache.getValue(_inID, "PROTOCOLVERSION");
            int timeout = Integer.parseInt(FileCache.getValue(_inID, "CONNECT_TIMEOUT"));
            int readtimeout = Integer.parseInt(FileCache.getValue(_inID, "READ_TIMEOUT"));
            String keepAlive = FileCache.getValue(_inID, "KEEP_ALIVE");
            if (_log.isDebugEnabled())
                _log.debug("login", "_userName: " + _userName + " _password: " + _password + " _protocolVersion: " + _protocolVersion + " keepAlive: " + keepAlive);
            URL url = new URL(p_url);
            fermaUrlConnection = new Ferma6UrlConnection(p_url, timeout, readtimeout, keepAlive);

            String requestStr = null;
            String responseStr = null;
            HashMap map = new HashMap();
            map.put("UserName", _userName);
            map.put("Password", _password);
            map.put("ProtocolVersion", _protocolVersion);
            map.put("IN_TXN_ID", String.valueOf(++_transactionID));
            requestStr = _formatter.generateRequest(Ferma6RequestResponse.ACTION_LOGIN, map);
            if (_log.isDebugEnabled())
                _log.debug("login", "requestStr: " + requestStr); // interface
                                                                  // logger
            fermaUrlConnection.setPrintWriter();
            out = fermaUrlConnection.getPrintWriter();
            // send login request
            out.println(requestStr);
            out.flush();
            fermaUrlConnection.setBufferedReader();
            bufferedReader = fermaUrlConnection.getBufferedReader();

            StringBuffer buffer = new StringBuffer();
            String resString = null;
            while ((resString = bufferedReader.readLine()) != null) {
                buffer.append(resString);
            }
            responseStr = buffer.toString();
            if (_log.isDebugEnabled())
                _log.debug("login", "responseStr " + responseStr);
            if (responseStr != null) {
                HashMap responseMap = _formatter.parseResponse(Ferma6RequestResponse.ACTION_LOGIN, responseStr);
                _fermaInterfaceID = (String) responseMap.get("InterfaceId");
                _status = (String) responseMap.get("Status");
            }
            _lastLoginTime = System.currentTimeMillis();
        } catch (Exception e) {
            _log.error("login", "Exception e:" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Ferma6ConnectionManager[login]", "", "", "", "Problem in Login on Ferma IN with _interfaceID: " + _inID + " Exception: " + e.getMessage());
            // generate Alarm
        } finally {
            // following will close connection and all input and output streams
            // associated with it
            try {
                if (fermaUrlConnection != null)
                    fermaUrlConnection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            ;
            if (_log.isDebugEnabled())
                _log.debug("login", "Exiting _fermaInterfaceID: " + _fermaInterfaceID + " _status: " + _status + " _inID" + _inID);
        }
    }

    public synchronized void logout(String p_url) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("logout", "Entered p_url: " + p_url + " _inID: " + _inID);
        Ferma6UrlConnection fermaUrlConnection = null;
        PrintWriter out = null;
        BufferedReader bufferedReader = null;
        try {
            URL url = new URL(p_url);
            int timeout = Integer.parseInt(FileCache.getValue(_inID, "CONNECT_TIMEOUT"));
            int readtimeout = Integer.parseInt(FileCache.getValue(_inID, "READ_TIMEOUT"));
            String keepAlive = FileCache.getValue(_inID, "KEEP_ALIVE");
            fermaUrlConnection = new Ferma6UrlConnection(p_url, timeout, readtimeout, keepAlive);

            // send login request
            String requestStr = null;
            String responseStr = null;
            // send login request
            HashMap map = new HashMap();
            map.put("FERMA_INTERFACE_ID", _fermaInterfaceID);
            map.put("IN_TXN_ID", String.valueOf(++_transactionID));
            requestStr = _formatter.generateRequest(Ferma6RequestResponse.ACTION_LOGOUT, map);
            if (_log.isDebugEnabled())
                _log.debug("logout", "requestStr: " + requestStr); // interface
                                                                   // logger
            fermaUrlConnection.setPrintWriter();
            out = fermaUrlConnection.getPrintWriter();
            out.println(requestStr);
            out.flush();
            fermaUrlConnection.setBufferedReader();
            bufferedReader = fermaUrlConnection.getBufferedReader();

            StringBuffer buffer = new StringBuffer(500);
            String resString = null;
            while ((resString = bufferedReader.readLine()) != null) {
                buffer.append(resString);
            }
            responseStr = buffer.toString();
            if (_log.isDebugEnabled())
                _log.debug("logout", "responseStr " + responseStr);
            if (responseStr != null) {
                HashMap responseMap = _formatter.parseResponse(Ferma6RequestResponse.ACTION_LOGOUT, responseStr);
                String status = (String) responseMap.get("Status");
                if (status.equalsIgnoreCase(Ferma6INHandler.XML_STATUS_SUCCESS))
                    if (_log.isDebugEnabled())
                        _log.debug("logout", "Logout Successsfully");
                    else if (_log.isDebugEnabled())
                        _log.debug("logout", "Logout failed");
            } else if (_log.isDebugEnabled())
                _log.debug("logout", "Logout failed");
        } catch (Exception e) {
            _log.error("logout", "Exception e:" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Ferma6ConnectionManager[logout]", "", "", "", "Connection not established with _interfaceID: " + _inID + " Exception: " + e.getMessage());
            // generate Alarm
        } finally {
            try {
                if (fermaUrlConnection != null)
                    fermaUrlConnection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            ;
            if (_log.isDebugEnabled())
                _log.debug("logout", "Exiting _fermaInterfaceID: " + _fermaInterfaceID + " _status=" + _status + " _inID: " + _inID);
        }
    }

    public String getFermaInterfaceID() {
        return _fermaInterfaceID;
    }

    public void setFermaInterfaceID(String fermaInterfaceID) {
        _fermaInterfaceID = fermaInterfaceID;
    }
}
