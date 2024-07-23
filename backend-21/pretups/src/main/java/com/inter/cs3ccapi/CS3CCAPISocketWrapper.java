package com.inter.cs3ccapi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
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
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.inter.pool.ClientMarkerI;

/**
 * @(#)CS3CCAPISocketWrapper.java
 *                                Copyright(c) 2007, Bharti Telesoft Int. Public
 *                                Ltd.
 *                                All Rights Reserved
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Author Date History
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Ashish K Jan 31, 2007 Initial Creation
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                ----
 *                                This class would be responsible to provide the
 *                                Wrapper of Socket connection.
 *                                This would also responsible to send the login
 *                                request after establishing
 *                                socket connection.
 */
public class CS3CCAPISocketWrapper extends Socket implements ClientMarkerI {
    public Log _log = LogFactory.getLog(this.getClass().getName());
    private BufferedReader _in = null; // Contains the BufferedReader object
                                       // that would read the response.
    private PrintWriter _out = null;// Contains the PrintWriter object that
                                    // would write the request to the socket.
    private CS3CCAPIRequestFormatter _formatter = new CS3CCAPIRequestFormatter();// Formatter
                                                                                 // instance.
    private HashMap _responseMap = null;// Contains the key value of response
                                        // parameter of login response.
    private Socket _socketConnection = null;// Contains the instance of socket
                                            // connection.
    private HashMap _requestMap = null;// Constains the required parameter to
                                       // format the request.
    private String _interfaceID = null;// Represent the interface id of the IN.

    /**
     * Constructor implements the logic to open a socket connection with given
     * ip and port number.
     * Login request would be sent to the opened connection for authentication.
     * 
     * @param String
     *            p_interfaceId
     * @throws BTSLBaseException
     */
    public CS3CCAPISocketWrapper(String p_interfaceId) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("CS3CCAPISocketWrapper[constructor]", "Entered p_interfaceId::" + p_interfaceId);
        String loginRespStr = null;
        try {
            if (InterfaceUtil.isNullString(p_interfaceId)) {
                // Confirm for event handling
                throw new BTSLBaseException(this, "CS3CCAPISocketWrapper[constructor]", InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }
            this._interfaceID = p_interfaceId;
            // Fetch the IP address from the INFile.
            String ip = FileCache.getValue(_interfaceID, "SOCKET_IP");
            if (InterfaceUtil.isNullString(ip)) {
                _log.error("CS3CCAPISocketWrapper[constructor]", "SOCKET_IP is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPISocketWrapper[constructor]", "", " INTERFACE ID = " + _interfaceID, "", "SOCKET_IP is not defined in the INFile");
                throw new BTSLBaseException(this, "SocketWrapper[constructor]", InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }
            // Fetch the Port from the INFile.
            String portStr = FileCache.getValue(p_interfaceId, "SOCKET_PORT");
            if (InterfaceUtil.isNullString(portStr) || !InterfaceUtil.isNumeric(portStr.trim())) {
                _log.error("CS3CCAPISocketWrapper[constructor]", "SOCKET_PORT is either not defined in the INFile or its value is not Numeric");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPISocketWrapper[constructor]", "", " INTERFACE ID = " + _interfaceID, "", "SOCKET_PORT is not defined in IN File or not numeric");
                throw new BTSLBaseException(this, "SocketWrapper[constructor]", InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }
            // If the port would not be numeric the exception will be catch in
            // the catch block.
            int port = Integer.parseInt(portStr.trim());
            String sleepBeforeReadResp = FileCache.getValue(_interfaceID, "SLEEP_RESP_READ");
            if (InterfaceUtil.isNullString(sleepBeforeReadResp) || !InterfaceUtil.isNumeric(sleepBeforeReadResp.trim())) {
                _log.error("CS3CCAPISocketWrapper[constructor]", "SLEEP_RESP_READ is either not defined in the INFile or its value is not Numeric");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPISocketWrapper[constructor]", "", " INTERFACE ID = " + _interfaceID, "", "SLEEP_RESP_READ is not defined in IN File or not numeric");
                throw new BTSLBaseException(this, "SocketWrapper[constructor]", InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }
            long sleepBeforeReadRespLong = Long.parseLong(sleepBeforeReadResp.trim());
            // Fetch the connection time out from the INFile.
            String conTimeOutStr = FileCache.getValue(_interfaceID, "SOCKET_TIMEOUT");
            if (InterfaceUtil.isNullString(conTimeOutStr) || !InterfaceUtil.isNumeric(conTimeOutStr.trim())) {
                _log.error("CS3CCAPISocketWrapper[constructor]", "SOCKET_TIMEOUT is either not defined in the INFile or its value is not Numeric");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPISocketWrapper[constructor]", "", " INTERFACE ID = " + _interfaceID, "", "SOCKET_TIMEOUT is not defined in IN File or not numeric");
                throw new BTSLBaseException(this, "SocketWrapper[constructor]", InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }
            // If the SOCKET_TIMEOUT would not be numeric the exception will be
            // catch in the catch block.
            int connectionTimeOut = Integer.parseInt(conTimeOutStr.trim());
            String userName = (String) FileCache.getValue(_interfaceID, "USER_NAME");
            if (InterfaceUtil.isNullString(userName)) {
                _log.error("CS3CCAPISocketWrapper[constructor]", "USER_NAME is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPISocketWrapper[constructor]", "", " INTERFACE ID = " + _interfaceID, "", "USER_NAME is not defined in the INFile");
                throw new BTSLBaseException(this, "SocketWrapper[constructor]", InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }
            String password = (String) FileCache.getValue(_interfaceID, "PASSWORD");
            if (InterfaceUtil.isNullString(password)) {
                _log.error("CS3CCAPISocketWrapper[constructor]", "PASSWORD is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPISocketWrapper[constructor]", "", " INTERFACE ID = " + _interfaceID, "", "PASSWORD is not defined in the INFile");
                throw new BTSLBaseException(this, "SocketWrapper[constructor]", InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }
            // Generate the session id for the login request.
            // Put the interfaceId and sessionId to the requestMap
            _requestMap = new HashMap();
            _requestMap.put("INTERFACE_ID", _interfaceID);
            // Initializing the SocketConnection class to establish a socket
            // connection.
            _socketConnection = new Socket(ip.trim(), port);
            // Set the Printwriter stream object of the opened socket
            // connection.
            setSocketTimeout(connectionTimeOut);
            // Set PrintWriter object
            setPrintWriter();
            // Set BufferedReader object for the opened socket.
            setBufferedReader();
            // set user name and password in to request map
            _requestMap.put("USER_NAME", userName);
            _requestMap.put("PASSWORD", password);
            // Get the request string for the Login
            String loginReqStr = _formatter.generateRequest(CS3CCAPII.ACTION_LOGIN, _requestMap);
            // Send the login request to the IN,using PrintWriter object and Get
            // the response of login from the Huawei IN.
            loginRespStr = sendLoginRequest(loginReqStr, sleepBeforeReadRespLong);
            _responseMap = _formatter.parseResponse(CS3CCAPII.ACTION_LOGIN, loginRespStr);
            // Get the status of login response.
            String status = (String) _responseMap.get("response_status");
            if (!((CS3CCAPII.RESULT_OK).equals(status))) {
                _log.error("CS3CCAPISocketWrapper[constructor]", "After getting the socket connection get the Logins response status :" + status);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPISocketWrapper[constructor]", "", " INTERFACE ID = " + _interfaceID, "", "After getting the socket connection get the Login response status :" + status);
                throw new BTSLBaseException(this, "SocketWrapper[constructor]", InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }
        } catch (BTSLBaseException be) {
            _log.error("CS3CCAPISocketWrapper[constructor]", "BTSLBaseException be:" + be.getMessage());
            if (_socketConnection != null) {
                try {
                    _socketConnection.close();
                } catch (Exception e) {
                }
            }
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("CS3CCAPISocketWrapper[constructor]", "Exception e:" + e.getMessage());
            if (_socketConnection != null) {
                try {
                    _socketConnection.close();
                } catch (Exception ex) {
                }
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPISocketWrapper[constructor]", "", " INTERFACE ID = " + _interfaceID, "", "While getting socket connection get the Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "CS3CCAPISocketWrapper[constructor]", InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("CS3CCAPISocketWrapper[constructor]", "Exited _interfaceID:" + _interfaceID + " _socketConnection:" + _socketConnection + " loginRespStr:" + loginRespStr);
        }
    }

    /**
     * This method is used to send the Login request to the IN.
     * 
     * @param String
     *            p_requestStr
     * @param long p_sleepBeforeReadRespLong
     * @return String
     * @throws BTSLBaseException
     */
    private String sendLoginRequest(String p_requestStr, long p_sleepBeforeReadRespLong) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendLoginRequest", "Entered p_requestStr:" + p_requestStr + " p_sleepBeforeReadRespLong:" + p_sleepBeforeReadRespLong);
        String responseStr = null;
        StringBuffer responseBuffer = null;
        try {
            try {
                _out.println(p_requestStr);
                _out.flush();
                // before read the login response wait for the configured time.
                Thread.sleep(p_sleepBeforeReadRespLong);
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("sendLoginRequest", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPISocketWrapper[constructor]", "", " INTERFACE ID = " + _interfaceID, "", "Login request FAIL");
                throw new BTSLBaseException(this, "sendLoginRequest", InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_LOGIN_REQ_FAIL);
            }
            try {
                responseBuffer = new StringBuffer(1028);
                int c = 0;
                while ((c = _in.read()) != -1) {
                    responseBuffer.append((char) c);
                    if (c == 59)
                        break;
                }
                responseStr = responseBuffer.toString();
                if (InterfaceUtil.isNullString(responseStr)) {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPISocketWrapper[constructor]", "", " INTERFACE ID = " + _interfaceID, "", "Login response from IN is NULL");
                    throw new BTSLBaseException(this, "sendLoginRequest", InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_LOGIN_REQ_FAIL);
                }
            } catch (BTSLBaseException be) {
                _log.error("sendLoginRequest", "BTSLBaseException be:" + be.getMessage());
                throw be;
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("sendLoginRequest", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPISocketWrapper[constructor]", "", " INTERFACE ID = " + _interfaceID, "", "While reading  the login response get the Exception e:" + e.getMessage());
                throw new BTSLBaseException(this, "sendLoginRequest", InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_LOGIN_REQ_FAIL);
            }
        } catch (BTSLBaseException be) {
            _log.error("sendLoginRequest", "BTSLBaseException be:" + be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("sendLoginRequest", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPISocketWrapper[constructor]", "", " INTERFACE ID = " + _interfaceID, "", "While sending the login request and reading the response getting Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "sendLoginRequest", InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_LOGIN_REQ_FAIL);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("sendLoginRequest", "Exited responseStr:" + responseStr);
        }
        return responseStr;
    }

    /**
     * This method sets the PrintWriter object for the opened socket.
     * 
     */
    private void setPrintWriter() {
        try {
            _out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(_socketConnection.getOutputStream())), true);
            ;
        } catch (Exception e) {
        }
    }

    /**
     * This method sets the buffered reader object for the opened socket.
     * 
     */
    private void setBufferedReader() {
        try {
            _in = new BufferedReader(new InputStreamReader(_socketConnection.getInputStream()));
        } catch (Exception e) {
        }
    }

    /**
     * This
     * 
     * @param p_timeout
     */
    private void setSocketTimeout(int p_timeout) {
        try {
            _socketConnection.setSoTimeout(p_timeout);
        } catch (Exception e) {
        }
    }

    /**
     * This method is responsible to return the BufferedReader object of Opened
     * socket.
     * 
     * @return
     */
    public BufferedReader getBufferedReader() {
        return _in;
    }

    /**
     * This method is responsible to return the PrintWriter object.
     * 
     * @return PrintWriter
     */
    public PrintWriter getPrintWriter() {
        return _out;
    }

    /**
     * This method is used to close the opened socket
     */
    public void destroy() {
        try {
            _out.close();
        } catch (Exception e) {
        }
        try {
            _in.close();
        } catch (Exception e) {
        }
        try {
            _socketConnection.close();
        } catch (Exception e) {
        }
    }
}
