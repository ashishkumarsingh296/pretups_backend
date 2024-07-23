/**
 * @(#)EricssionSocketWrapper.java
 *                                 Copyright(c) 2007, Bharti Telesoft Int.
 *                                 Public Ltd.
 *                                 All Rights Reserved
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Author Date History
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Ashish Kumar Feb 28, 2007 Initial Creation
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 ------
 *                                 This class is responsible to instantiate the
 *                                 Wrapper of socket connection.
 * 
 */
package com.inter.ericssionsync;

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

public class EricssionSocketWrapper {
    public Log _log = LogFactory.getLog(this.getClass().getName());
    private BufferedReader _in = null; // Contains the BufferedReader object
                                       // that would read the response.
    private PrintWriter _out = null;// Contains the PrintWriter object that
                                    // would write the request to the socket.
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
    public EricssionSocketWrapper(String p_interfaceId) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("EricssionSocketWrapper[constructor]", "Entered p_interfaceId::" + p_interfaceId);
        try {
            if (InterfaceUtil.isNullString(p_interfaceId)) {
                // Confirm for event handling
                throw new BTSLBaseException(this, "EricssionSocketWrapper[constructor]", InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }
            this._interfaceID = p_interfaceId;
            // Fetch the IP address from the INFile.
            String ip = FileCache.getValue(_interfaceID, "SOCKET_IP");
            if (InterfaceUtil.isNullString(ip)) {
                _log.error("EricssionSocketWrapper[constructor]", "SOCKET_IP is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionSocketWrapper[constructor]", "", " INTERFACE ID = " + _interfaceID, "", "SOCKET_IP is not defined in the INFile");
                throw new BTSLBaseException(this, "SocketWrapper[constructor]", InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }
            // Fetch the Port from the INFile.
            String portStr = FileCache.getValue(p_interfaceId, "SOCKET_PORT");
            if (InterfaceUtil.isNullString(portStr) || !InterfaceUtil.isNumeric(portStr.trim())) {
                _log.error("EricssionSocketWrapper[constructor]", "SOCKET_PORT is either not defined in the INFile or its value is not Numeric");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionSocketWrapper[constructor]", "", " INTERFACE ID = " + _interfaceID, "", "SOCKET_PORT is not defined in IN File or not numeric");
                throw new BTSLBaseException(this, "SocketWrapper[constructor]", InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }
            // If the port would not be numeric the exception will be catch in
            // the catch block.
            int port = Integer.parseInt(portStr.trim());
            // Fetch the connection time out from the INFile.
            String conTimeOutStr = FileCache.getValue(_interfaceID, "SOCKET_TIMEOUT");
            if (InterfaceUtil.isNullString(conTimeOutStr) || !InterfaceUtil.isNumeric(conTimeOutStr.trim())) {
                _log.error("EricssionSocketWrapper[constructor]", "SOCKET_TIMEOUT is either not defined in the INFile or its value is not Numeric");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionSocketWrapper[constructor]", "", " INTERFACE ID = " + _interfaceID, "", "SOCKET_TIMEOUT is not defined in IN File or not numeric");
                throw new BTSLBaseException(this, "SocketWrapper[constructor]", InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }
            // If the SOCKET_TIMEOUT would not be numeric the exception will be
            // catch in the catch block.
            int connectionTimeOut = Integer.parseInt(conTimeOutStr.trim());
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
        } catch (BTSLBaseException be) {
            _log.error("EricssionSocketWrapper[constructor]", "BTSLBaseException be:" + be.getMessage());
            if (_socketConnection != null) {
                try {
                    _socketConnection.close();
                } catch (Exception e) {
                }
            }
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("EricssionSocketWrapper[constructor]", "Exception e:" + e.getMessage());
            if (_socketConnection != null) {
                try {
                    _socketConnection.close();
                } catch (Exception ex) {
                }
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionSocketWrapper[constructor]", "", " INTERFACE ID = " + _interfaceID, "", "While getting socket connection get the Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "EricssionSocketWrapper[constructor]", InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("EricssionSocketWrapper[constructor]", "Exited _interfaceID:" + _interfaceID + " _socketConnection:" + _socketConnection);
        }
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
