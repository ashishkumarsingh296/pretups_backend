package com.inter.huaweiknfix;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
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

/**
 * @(#)HuaweiKnFixSocketWrapper.java
 *                                   Copyright(c) 2009, Bharti Telesoft Int.
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
 *                                   Abhay January 27, 2009 Initial Creation
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   ----------
 *                                   This class would be responsible to provide
 *                                   the Wrapper of SocketConnection class.
 */
public class HuaweiKnFixSocketWrapper extends Socket {
    public Log _log = LogFactory.getLog(this.getClass().getName());
    private int _txnCounter = 1;
    private static int _sessionIdInt = 0;
    private String _sessionId = null;
    private Socket _socketConnection = null;
    private BufferedReader _in = null;
    private OutputStream _out = null;
    private HuaweiKnFixRequestFormatter _formatter = new HuaweiKnFixRequestFormatter();
    private HashMap _responseMap = null;
    private HashMap _requestMap = null;
    private String _interfaceID = null;

    public HuaweiKnFixSocketWrapper(String p_interfaceId) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("HuaweiKnFixSocketWrapper[constructor]", "Entered p_interfaceId::" + p_interfaceId);

        try {
            this._interfaceID = p_interfaceId;
            // Generate the session id for the login request.
            _sessionId = generateSessionID();
            if (_log.isDebugEnabled())
                _log.debug("HuaweiKnFixSocketWrapper[constructor]", "_sessionId :: " + _sessionId);
            // Fetch the IP address from the INFile.
            String ip = FileCache.getValue(p_interfaceId, "SOCKET_IP");
            if (_log.isDebugEnabled())
                _log.debug("HuaweiKnFixSocketWrapper[constructor]", "ip :: " + ip);
            if (InterfaceUtil.isNullString(ip)) {
                _log.error("HuaweiKnFixSocketWrapper[constructor]", "SOCKET_IP is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiKnFixSocketWrapper[constructor]", "", "_interfaceID:" + _interfaceID, "", "SOCKET_IP is not defined in the INFile");
                throw new BTSLBaseException(this, "HuaweiKnFixSocketWrapper[constructor]", InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
            }
            // Fetch the Port from the INFile.
            String portStr = FileCache.getValue(p_interfaceId, "SOCKET_PORT");
            if (_log.isDebugEnabled())
                _log.debug("HuaweiKnFixSocketWrapper[constructor]", "portStr :: " + portStr);
            if (InterfaceUtil.isNullString(portStr) || !InterfaceUtil.isNumeric(portStr.trim())) {
                _log.error("HuaweiKnFixSocketWrapper[constructor]", "SOCKET_PORT is either not defined in the INFile or its value is not numeric");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiKnFixSocketWrapper[constructor]", "", "_interfaceID:" + _interfaceID, "", "SOCKET_PORT is either not defined in the INFile or its value is not numeric");
                throw new BTSLBaseException(this, "HuaweiKnFixSocketWrapper[constructor]", InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
            }
            int port = Integer.parseInt(portStr.trim());
            String conTimeOutStr = FileCache.getValue(p_interfaceId, "SOCKET_TIMEOUT");
            if (InterfaceUtil.isNullString(portStr) || !InterfaceUtil.isNumeric(conTimeOutStr.trim())) {
                _log.error("HuaweiKnFixSocketWrapper[constructor]", "SOCKET_TIMEOUT is either not defined in the INFile or its value is not numeric");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiKnFixSocketWrapper[constructor]", "", "_interfaceID:" + _interfaceID, "", "SOCKET_PORT is not defined in the INFile");
                throw new BTSLBaseException(this, "HuaweiKnFixSocketWrapper[constructor]", InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
            }
            // If the SOCKET_TIMEOUT would not be numeric the exception will be
            // catch in the catch block.
            int connectionTimeOut = Integer.parseInt(conTimeOutStr.trim());
            // Initializing the SocketConnection class to establish a socket
            // connection.
            // Put the interfaceId and sessionId and transaction id to the
            // _requestMap
            _requestMap = new HashMap();
            _requestMap.put("sessionID", _sessionId);
            _requestMap.put("interfaceID", p_interfaceId);
            _requestMap.put("IN_HEADER_TXN_ID", "10000000");

            String loginReqStr = _formatter.generateRequest(HuaweiKnFixI.ACTION_LOGIN, _requestMap);

            // Initialize the socket connection for given IP and PORT number.s
            _socketConnection = new Socket(ip.trim(), port);
            // set the OutPut stream object of the opened socket connection.
            setOutPutStream();
            // set the Input stream for the opened socket connection
            setInputStream();
            // Set the time out for the opened socket.
            setTimeout(connectionTimeOut);
            // Get the MML request string for the Login
            // Send the login request to the using PrintWriter object and Get
            // the response of login from the HuaweiKnFix IN.
            sendLoginRequest(loginReqStr);
            // Read the response
            String loginRespStr = getLoginResponse();
            // Parse the MML response string
            _responseMap = _formatter.parseResponse(HuaweiKnFixI.ACTION_LOGIN, loginRespStr);
            // If the login is successful then the response map would contain
            // the value of response_status
            // as COMMAN_SUCCESS,which is defined in the CommanI
            String status = (String) _responseMap.get("response_status");
            if (!HuaweiKnFixI.RESULT_OK.equals(status)) {
                _log.error("HuaweiKnFixSocketWrapper[constructor]", "Login response from IN is not OK, status:" + status);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiKnFixSocketWrapper[constructor]", "", "_interfaceID:" + _interfaceID, "", "Login response from IN is NOT OK, status:" + status);
                throw new BTSLBaseException(this, "HuaweiKnFixSocketWrapper[constructor]", InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
            }
        } catch (BTSLBaseException be) {
            _log.error("HuaweiKnFixSocketWrapper[constructor]", "BTSLBaseException be:" + be.getMessage());
            if (_socketConnection != null) {
                try {
                    _socketConnection.close();
                } catch (Exception e) {
                }
            }
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("HuaweiKnFixSocketWrapper[constructor]", "Exception e:" + e.getMessage());
            if (_socketConnection != null) {
                try {
                    _socketConnection.close();
                } catch (Exception ex) {
                }
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiKnFixSocketWrapper[constructor]", "", "_interfaceID:" + _interfaceID, "", "While intializing Socket connection get Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "HuaweiKnFixSocketWrapper[constructor]", InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
        }
    }

    /**
     * Get IN Reconciliation Transaction ID
     * 
     * @return String
     * @throws Exception
     */
    public synchronized String getINHeaderTxnID() throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getINHeaderTxnID", "Entered ");
        // This method will be used when we have transID based on database
        // sequence.
        String headerTransactionID = "";
        StringBuffer counterBuffer = null;
        int inTxnLength = 7;
        int length = 0;
        int tmpLength = 0;
        try {
            counterBuffer = new StringBuffer(10);
            if (_txnCounter < 9999998)
                headerTransactionID = String.valueOf(_txnCounter++);// headerTransactionID
                                                                    // =
                                                                    // String.valueOf(_txnCounter++);
            else
                return "1";

            if (_log.isDebugEnabled())
                _log.debug("getINHeaderTxnID", "counter :" + headerTransactionID);
            length = headerTransactionID.length();
            tmpLength = inTxnLength - length;
            if (length < inTxnLength) {
                for (int i = 0; i < tmpLength; i++)
                    counterBuffer.append("0");
            }
            counterBuffer.append(headerTransactionID);
            counterBuffer.insert(0, "1");
            headerTransactionID = counterBuffer.toString();
            // counter = counterBuffer.insert("1");
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("getINHeaderTxnID", "Exception e = " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "getINHeaderTxnID", "", "_interfaceID:" + _interfaceID, "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "getINHeaderTxnID", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("getINHeaderTxnID", "Exit headerTransactionID:" + headerTransactionID + "_sessionId:" + _sessionId);
        }// end of finally
        return headerTransactionID;
    }// end of getINReconTxnID

    /**
     * This method is used to send the Login request to the IN.
     * Counter is incremented to 1 each time and reinitializes the counter when
     * its max value is reached.
     * 
     * @param p_requestStr
     * @return String
     * @throws BTSLBaseException
     */
    private void sendLoginRequest(String p_requestStr) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendLoginRequest", "Entered p_requestStr:" + p_requestStr);
        try {
            _out.write(p_requestStr.getBytes());
            _out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("sendLoginRequest", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "getINHeaderTxnID", "", "_interfaceID:" + _interfaceID, "", "While sending the login request get Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "sendLoginRequest", InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_LOGIN_REQ_FAIL);
        }
    }

    /**
     * This method is responsible to read the login response from the IN.
     * 
     * @return String
     * @throws BTSLBaseException
     * @throws Exception
     */
    private String getLoginResponse() throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("getLoginResponse", "Entered");
        StringBuffer responseBuffer = null;
        String responseStr = null;
        try {
            int c = 0;
            responseBuffer = new StringBuffer(1028);
            while ((c = _in.read()) != -1) {
                responseBuffer.append((char) c);
                if (c == 59)
                    break;
            }
            responseStr = responseBuffer.toString();
            if (InterfaceUtil.isNullString(responseStr))
                throw new BTSLBaseException(this, "getLoginResponse", InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_LOGIN_REQ_FAIL);
        } catch (BTSLBaseException be) {
            _log.error("getLoginResponse", "BTSLBaseException be:" + be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("getLoginResponse", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "getINHeaderTxnID", "", "_interfaceID:" + _interfaceID, "", "While response of login request get Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "getLoginResponse", InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_LOGIN_REQ_FAIL);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getLoginResponse", "responseStr:" + responseStr);
        }
        return responseStr;
    }

    /**
     * This method is used to send the Logout request to the IN.
     * 
     * @param p_requestStr
     * @throws BTSLBaseException
     */
    private void sendLogoutRequest(String p_requestStr) {
        if (_log.isDebugEnabled())
            _log.debug("sendLogoutRequest", "Entered p_requestStr:" + p_requestStr);
        try {
            _out.write(p_requestStr.getBytes());
            _out.flush();
        } catch (Exception e) {
            // e.printStackTrace();//for testing
            _log.error("sendLogoutRequest", "Exception e:" + e.getMessage());
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"getINHeaderTxnID","","_interfaceID:"+_interfaceID,"","While sending the login request get Exception:"+e.getMessage());
            // //throw new
            // BTSLBaseException("Error Code:ERROR_SEND_LOGOUT_REQ");
        }
    }

    /**
     * This method is responsible to read the logout response from the IN.
     * 
     * @return String
     * @throws BTSLBaseException
     * @throws Exception
     */
    private String getLogoutResponse() {
        if (_log.isDebugEnabled())
            _log.debug("getLogoutResponse", "Entered");
        StringBuffer responseBuffer = null;
        String responseStr = null;
        try {
            int c = 0;
            responseBuffer = new StringBuffer(1028);
            while ((c = _in.read()) != -1) {
                responseBuffer.append((char) c);
                if (c == 59)
                    break;
            }
            responseStr = responseBuffer.toString();
        } catch (Exception e) {
            // e.printStackTrace();
            _log.error("getLogoutResponse", "Exception e:" + e.getMessage());
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"getINHeaderTxnID","","_interfaceID:"+_interfaceID,"","While response of login request get Exception:"+e.getMessage());
            // throw new BTSLBaseException("Error Code: ERROR_READING_RESP");
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getLogoutResponse", "responseStr:" + responseStr);
        }
        return responseStr;
    }

    /**
     * This method is responsible to set OutputStream to Socket.
     */
    protected void setOutPutStream() {
        try {
            _out = _socketConnection.getOutputStream();
        } catch (Exception e) {
        }
    }

    /**
     * This method is responsible to set OutputStream to Socket.
     */
    protected void setInputStream() {
        try {
            _in = new BufferedReader(new InputStreamReader(_socketConnection.getInputStream()));
        } catch (Exception e) {
            // Do nothing
        }
    }

    /**
     * This method is responsible to set timeout to Socket.
     */
    public void setTimeout(int p_timeout) {
        try {
            _socketConnection.setSoTimeout(p_timeout);
        } catch (Exception e) {
        }
    }

    /**
     * This method is used to generate the session id, that would be present in
     * the Session Header of request.
     * 
     * @return String
     */
    private String generateSessionID() throws BTSLBaseException {
        String counter = "";
        StringBuffer counterBuffer = null;
        int inTxnLength = 7;
        int length = 0;
        int tmpLength = 0;
        try {
            counterBuffer = new StringBuffer(10);
            if (_sessionIdInt < 9999999)
                counter = String.valueOf(_sessionIdInt++);
            else
                return "1";
            if (_log.isDebugEnabled())
                _log.debug("generateSessionID", "counter :" + counter);
            length = counter.length();
            tmpLength = inTxnLength - length;
            if (length < inTxnLength) {
                for (int i = 0; i < tmpLength; i++)
                    counterBuffer.append("0");
            }
            counterBuffer.append(counter);
            counter = "1" + counterBuffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("generateSessionID", "Exception e = " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiKnFixSocketWrapper[generateSessionID]", "", "_interfaceID:" + _interfaceID, "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "generateSessionID", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("generateSessionID", "Exiting counter = " + counter);
        }// end of finally
        return counter;
    }

    /**
     * This method is responsible to return the BufferedReader object of Opened
     * socket.
     * 
     * @return BufferedReader
     */
    public BufferedReader getBufferedReader() {
        return _in;
    }

    /**
     * This method is responsible to return the PrintWriter object.
     * 
     * @return OutputStream
     */
    public OutputStream getPrintWriter() {
        return _out;
    }

    /**
     * This method is used to send logout request to IN and close the opened
     * socket
     * 
     * @throws BTSLBaseException
     */
    public void destroy() {
        if (_log.isDebugEnabled())
            _log.debug("destroy", "Entered");
        try {
            _requestMap.put("IN_HEADER_TXN_ID", "19999999");
            String logoutReqStr = _formatter.generateRequest(HuaweiKnFixI.ACTION_LOGOUT, _requestMap);
            sendLogoutRequest(logoutReqStr);
            String logoutRespStr = getLogoutResponse();
            // Parse the MML response string
            if (!InterfaceUtil.isNullString(logoutRespStr))
                _responseMap = _formatter.parseResponse(HuaweiKnFixI.ACTION_LOGOUT, logoutRespStr);
            /*
             * //This code is commented as per discussed with sanjay sir, there
             * is no need to check the logout response.
             * String status = (String)_responseMap.get("response_status");
             * if(!HuaweiI.RESULT_OK.equals(status))
             * {
             * _log.error("HuaweiKnFixSocketWrapper[destroy]",
             * "Logout response from IN is not OK, status:"+status);
             * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.INTERFACES
             * ,EventStatusI.RAISED,EventLevelI.FATAL,
             * "HuaweiKnFixSocketWrapper[constructor]"
             * ,"","_interfaceID:"+_interfaceID
             * ,"","logout response from IN is NOT OK, status:"+status);
             * throw new
             * BTSLBaseException("Error Code:ERROR_DESTROY_SOCKET_CONNECTION");
             * }
             */
        } catch (Exception e) {
            _log.error("HuaweiKnFixSocketWrapper[destroy]", "Error while destroying socket connection");
        } finally {
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
            if (_log.isDebugEnabled())
                _log.debug("destroy", "Exited");
        }
    }

    /**
     * This method is used to send logout request to IN and close the opened
     * socket
     * 
     * @return String
     */
    public synchronized String getSessionID() {
        return _sessionId;
    }
}