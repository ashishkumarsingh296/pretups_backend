package com.inter.zteethopia;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/**
 * @(#)ZTESocketWrapper.java
 *                           Copyright(c) 2009, Bharti Telesoft Int. Public Ltd.
 *                           All Rights Reserved
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Author Date History
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Vipan Sep 13, 2013 Initial Creation
 *                           --------------------------------------------------
 *                           ----------------------------------------------
 *                           This class would be responsible to provide the
 *                           Wrapper of SocketConnection class.
 */
public class ZTEINSocketWrapper extends Socket {

    static Log _logger = LogFactory.getLog(ZTEINSocketWrapper.class.getName());

    private static int _txnCounter = 1;
    private static int _sessionIdInt = 0;
    private static int _prevSec = 0;
    public int IN_HEADER_TRANSACTION_ID_PAD_LENGTH = 2;

    private Socket _socketConnection = null;
    private BufferedReader _in = null;
    private OutputStream _out = null;
    private ZTEINRequestFormatter _formatter = new ZTEINRequestFormatter();
    private HashMap<String, String> _responseMap = null;
    private HashMap<String, String> _requestMap = null;
    private String _interfaceID = null;
    private String _ip = null;
    private String _port = null;

    public ZTEINSocketWrapper() {
    }

    public ZTEINSocketWrapper(String fileCacheId, String p_interfaceId) throws BTSLBaseException {

        if (_logger.isDebugEnabled())
            _logger.debug("ZTESocketWrapper[constructor]", " Entered p_interfaceId::" + p_interfaceId);

        try {
            this._interfaceID = p_interfaceId;

            String ip = FileCache.getValue(fileCacheId, "ZTE_IN_IP_" + _interfaceID);

            if (_logger.isDebugEnabled())
                _logger.debug("ZTESocketWrapper[constructor]", " ip :: " + ip);
            if (BTSLUtil.isNullString(ip)) {
                _logger.error("ZTESocketWrapper[constructor]", " SOCKET_IP is not defined in the INFile");
                // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ZTESocketWrapper[constructor]","","_interfaceID:"+_interfaceID,"","SOCKET_IP is not defined in the INFile");
                throw new BTSLBaseException("ZTESocketWrapper[constructor]" + InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
            }
            // Fetch the Port from the INFile.
            String portStr = FileCache.getValue(fileCacheId, "ZTE_IN_PORT_" + _interfaceID);

            if (_logger.isDebugEnabled())
                _logger.debug("ZTESocketWrapper[constructor]", " portStr :: " + portStr);
            if (BTSLUtil.isNullString(portStr) || !BTSLUtil.isNumeric(portStr.trim())) {
                _logger.error("ZTESocketWrapper[constructor] ", " SOCKET_PORT is either not defined in the INFile or its value is not numeric");
                // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ZTESocketWrapper[constructor]","","_interfaceID:"+_interfaceID,"","SOCKET_PORT is either not defined in the INFile or its value is not numeric");
                throw new BTSLBaseException("ZTESocketWrapper[constructor]" + InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
            }
            int port = Integer.parseInt(portStr.trim());

            String conTimeOutStr = FileCache.getValue(fileCacheId, "SOCKET_TIMEOUT_" + _interfaceID);

            if (BTSLUtil.isNullString(portStr) || !BTSLUtil.isNumeric(conTimeOutStr.trim())) {
                _logger.error("ZTESocketWrapper[constructor] ", " SOCKET_TIMEOUT is either not defined in the INFile or its value is not numeric");
                // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ZTESocketWrapper[constructor]","","_interfaceID:"+_interfaceID,"","SOCKET_PORT is not defined in the INFile");
                throw new BTSLBaseException("ZTESocketWrapper[constructor]" + InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
            }
            _ip = ip.trim();
            _port = Integer.toString(port);

            // If the SOCKET_TIMEOUT would not be numeric the exception will be
            // catch in the catch block.

            int connectionTimeOut = Integer.parseInt(conTimeOutStr.trim());

            // Initializing the SocketConnection class to establish a socket
            // connection.
            // Put the interfaceId and sessionId and transaction id to the
            // _requestMap

            _requestMap = new HashMap<String, String>();
            _requestMap.put("interfaceID", p_interfaceId);
            _requestMap.put("TRANSACTIONHEADERID", getINHeaderTxnID());
            _requestMap.put("SESSIONID", generateSessionID());
            String loginReqStr = _formatter.generateRequest(fileCacheId, ZTEINI.ACTION_LOGIN, _requestMap);
            // Initialize the socket connection for given IP and PORT number.s
            _socketConnection = new Socket(ip.trim(), port);

            ZTEINHBLogger.logMessage("Socket connection created succesfully IP" + ip + " port=" + port);
            // set the OutPut stream object of the opened socket connection.
            setOutPutStream();
            // set the Input stream for the opened socket connection
            setInputStream();
            // Set the time out for the opened socket.
            setTimeout(connectionTimeOut);
            // Get the MML request string for the Login
            // Send the login request to the using PrintWriter object and Get
            // the response of login from the ZTE IN.
            sendLoginRequest(loginReqStr);
            // Read the response
            String loginRespStr = getLoginResponse(fileCacheId);
            // Parse the MML response string
            _responseMap = _formatter.parseResponse(ZTEINI.ACTION_LOGIN, loginRespStr);

            // If the login is successful then the response map would contain
            // the value of response_status
            // as COMMAN_SUCCESS,which is defined in the CommanI

            String status = (String) _responseMap.get("response_status");
            if (!ZTEINI.RESULT_OK.equals(status)) {
                _logger.error("ZTESocketWrapper[constructor]", " Login response from IN is not OK, status:" + status);
                // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ZTESocketWrapper[constructor]","","_interfaceID:"+_interfaceID,"","Login response from IN is NOT OK, status:"+status);
                throw new BTSLBaseException("ZTESocketWrapper[constructor]" + InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
            }
        } catch (BTSLBaseException be) {
            ZTEINHBLogger.logMessage("ZTESocketWrapper[constructor] BTSLBaseException bIP" + _ip + " Port " + _port + " " + be.getMessage());

            if (_socketConnection != null) {
                try {
                    _socketConnection.close();
                } catch (Exception e) {
                }
            }
            throw be;
        } catch (SocketException e) {
            ZTEINHBLogger.logMessage("Socket Exception IP" + _ip + " Port " + _port + " " + e.getMessage());
            if (_socketConnection != null) {
                try {
                    _socketConnection.close();
                } catch (Exception ex) {
                }
            }
            throw new BTSLBaseException("ZTESocketWrapper[constructor]" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            ZTEINHBLogger.logMessage("ZTESocketWrapper[constructor] Exception IP" + _ip + " Port " + _port + " " + e.getMessage());

            if (_socketConnection != null) {
                try {
                    _socketConnection.close();
                } catch (Exception ex) {
                }
            }
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ZTESocketWrapper[constructor]","","_interfaceID:"+_interfaceID,"","While intializing Socket connection get Exception e:"+e.getMessage());
            throw new BTSLBaseException("ZTESocketWrapper[constructor]" + InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
        }
    }

    /**
     * Get IN Reconciliation Transaction ID
     * 
     * @return String
     * @throws Exception
     */
    private static SimpleDateFormat _sdfCompare = new SimpleDateFormat("ss");

    public synchronized String getINHeaderTxnID() throws BTSLBaseException {
        if (_logger.isDebugEnabled())
            _logger.debug("getINHeaderTxnID ", "Entered ");
        // This method will be used when we have transID based on database
        // sequence.
        String headerTransactionID = "";
        try {
            String secToCompare = null;
            Date mydate = null;

            mydate = new Date();

            secToCompare = _sdfCompare.format(mydate);
            int currentSec = Integer.parseInt(secToCompare);

            if (currentSec != _prevSec) {
                _txnCounter = 1;
                _prevSec = currentSec;
            } else if (_txnCounter >= 99) {
                _txnCounter = 1;
            } else {
                _txnCounter++;
            }

            if (_txnCounter == 0)
                throw new BTSLBaseException("ZTEEthioSocketWrapper", "getINHeaderTxnID", PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);

            headerTransactionID = BTSLUtil.padZeroesToLeft(String.valueOf(Constants.getProperty("INSTANCE_ID")), IN_HEADER_TRANSACTION_ID_PAD_LENGTH) + currentTimeFormatStringTillSec(mydate) + BTSLUtil.padZeroesToLeft(String.valueOf(_txnCounter), IN_HEADER_TRANSACTION_ID_PAD_LENGTH);
            if (headerTransactionID == null)
                throw new BTSLBaseException("ZTEEthioSocketWrapper", "getINHeaderTxnID", PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
        } catch (Exception e) {
            e.printStackTrace();
            _logger.error("getINHeaderTxnID", "Exception e = " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "getINHeaderTxnID", "", "_interfaceID:" + _interfaceID, "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "getINHeaderTxnID", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
        }// end of catch-Exception
        finally {
            if (_logger.isDebugEnabled())
                _logger.debug("getINHeaderTxnID", "Exit headerTransactionID:" + headerTransactionID);
            return headerTransactionID;
        }
    }

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
        if (_logger.isDebugEnabled())
            _logger.debug("sendLoginRequest ", " Entered p_requestStr:" + p_requestStr);
        try {
            _out.write(p_requestStr.getBytes());
            _out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            _logger.error("sendLoginRequest ", " Exception e:" + e.getMessage());
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"getINHeaderTxnID","","_interfaceID:"+_interfaceID,"","While sending the login request get Exception:"+e.getMessage());
            throw new BTSLBaseException("sendLoginRequest" + InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_LOGIN_REQ_FAIL);
        }
    }

    /**
     * This method is responsible to read the login response from the IN.
     * 
     * @return String
     * @throws BTSLBaseException
     * @throws Exception
     */
    private String getLoginResponse(String fileCacheId) throws BTSLBaseException, Exception {
        if (_logger.isDebugEnabled())
            _logger.debug("getLoginResponse ", " Entered");
        StringBuffer responseBuffer = null;
        String responseStr = null;
        try {
            int c = 0;
            responseBuffer = new StringBuffer(1028);
            boolean flag = false;
            int count = 0;
            int messagelength = 0;
            while ((c = _in.read()) != -1) {
                responseBuffer.append((char) c);
                if (!flag)
                    if (responseBuffer.toString().contains(FileCache.getValue(fileCacheId, "START_FLAG"))) {
                        ++count;
                        if (count == 7) {
                            flag = true;
                            _logger.debug("responseBuffer.toString().trim()", "  " + responseBuffer.toString().trim());
                            int i = responseBuffer.toString().lastIndexOf("`") + 1;
                            messagelength = Integer.parseInt(responseBuffer.toString().substring(i, i + 4), 16);
                            messagelength = messagelength + 16;

                        }
                    }
                if (flag)
                    if (messagelength == responseBuffer.toString().length())
                        break;
            }
            responseStr = responseBuffer.toString();
            if (BTSLUtil.isNullString(responseStr))
                throw new BTSLBaseException("getLoginResponse" + InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_LOGIN_REQ_FAIL);
        } catch (BTSLBaseException be) {
            _logger.error("getLoginResponse", " BTSLBaseException be:" + be.getMessage());
            throw be;
        } catch (SocketException se) {
            throw new BTSLBaseException("getLoginResponse SocketExceptiom" + InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_LOGIN_REQ_FAIL);
        } catch (SocketTimeoutException ste) {
            throw new BTSLBaseException("getLoginResponse SocketTimeoutException" + InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_LOGIN_REQ_FAIL);
        } catch (Exception e) {
            e.printStackTrace();
            _logger.error("getLoginResponse ", " Exception e:" + e.getMessage());
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"getINHeaderTxnID","","_interfaceID:"+_interfaceID,"","While response of login request get Exception:"+e.getMessage());
            throw new BTSLBaseException("getLoginResponse" + InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_LOGIN_REQ_FAIL);
        } finally {
            if (_logger.isDebugEnabled())
                _logger.debug("getLoginResponse ", " responseStr:" + responseStr);
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
        if (_logger.isDebugEnabled())
            _logger.debug("sendLogoutRequest ", " Entered p_requestStr:" + p_requestStr);
        try {
            _out.write(p_requestStr.getBytes());
            _out.flush();
        } catch (Exception e) {
            // e.printStackTrace();//for testing
            _logger.error("sendLogoutRequest", " Exception e:" + e.getMessage());
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
    private String getLogoutResponse(String fileCacheId) {
        if (_logger.isDebugEnabled())
            _logger.debug("getLogoutResponse ", "Entered");
        StringBuffer responseBuffer = null;
        String responseStr = null;
        try {
            int c = 0;
            responseBuffer = new StringBuffer();
            boolean flag = false;
            int count = 0;
            int messagelength = 0;
            while ((c = _in.read()) != -1) {
                responseBuffer.append((char) c);
                if (!flag)
                    if (responseBuffer.toString().contains(FileCache.getValue(fileCacheId, "START_FLAG"))) {
                        ++count;
                        if (count == 7) {
                            flag = true;
                            _logger.debug("responseBuffer.toString().trim()", " " + responseBuffer.toString().trim());
                            int i = responseBuffer.toString().lastIndexOf("`") + 1;
                            messagelength = Integer.parseInt(responseBuffer.toString().substring(i, i + 4), 16);
                            messagelength = messagelength + 16;

                        }
                    }
                if (flag)
                    if (messagelength == responseBuffer.toString().length())
                        break;
            }
            responseStr = responseBuffer.toString();
        } catch (Exception e) {
            // e.printStackTrace();
            _logger.error("getLogoutResponse", " Exception e:" + e.getMessage());
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"getINHeaderTxnID","","_interfaceID:"+_interfaceID,"","While response of login request get Exception:"+e.getMessage());
            // throw new BTSLBaseException("Error Code: ERROR_READING_RESP");
        } finally {
            if (_logger.isDebugEnabled())
                _logger.debug("getLogoutResponse", " responseStr:" + responseStr);
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
    public String generateSessionID() throws BTSLBaseException {
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
            if (_logger.isDebugEnabled())
                _logger.debug("generateSessionID", " counter :" + counter);
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
            _logger.error("generateSessionID", "Exception e = " + e.getMessage());
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ZTESocketWrapper[generateSessionID]","","_interfaceID:"+_interfaceID,"","Exception:"+e.getMessage());
            throw new BTSLBaseException("generateSessionID" + InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// end of catch-Exception
        finally {
            if (_logger.isDebugEnabled())
                _logger.debug("generateSessionID ", "Exiting counter = " + counter);
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
    public void destroy(String fileCacheId) throws BTSLBaseException {
        if (_logger.isDebugEnabled())
            ZTEINHBLogger.logMessage("destroy Entered");
        try {
            _requestMap.put("TRANSACTIONHEADERID", getINHeaderTxnID());
            _requestMap.put("SESSIONID", generateSessionID());
            String logoutReqStr = _formatter.generateRequest(fileCacheId, ZTEINI.ACTION_LOGOUT, _requestMap);
            sendLogoutRequest(logoutReqStr);
            String logoutRespStr = getLogoutResponse(fileCacheId);
            // Parse the MML response string
            if (!BTSLUtil.isNullString(logoutRespStr))
                _responseMap = _formatter.parseResponse(ZTEINI.ACTION_LOGOUT, logoutRespStr);

        } catch (Exception e) {

            ZTEINHBLogger.logMessage("ZTESocketWrapper[destroy] rror while destroying socket connection");
            throw new BTSLBaseException("ZTESocketWrapper[destroy] rror while destroying socket connection");
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
                _logger.debug("SUCCEFULLY ", "CLOSE THE CONECTION" + _socketConnection);
                if (_socketConnection != null)
                    _socketConnection.close();
                _socketConnection = null;
                _logger.debug("SUCCEFULLY ", "CLOSE THE CONECTION" + _socketConnection);
            } catch (Exception e) {
                _logger.debug("Exception ", " SUCCEFULLY CLOSE THE CONECTION" + _socketConnection);
                _logger.debug("Exception ", "in side de" + e.getMessage());
                e.printStackTrace();

                _socketConnection = null;
                _logger.debug("Exception ", "SUCCEFULLY CLOSE THE CONECTION" + _socketConnection);

            }
            if (_logger.isDebugEnabled())
                _logger.debug("destroy ", " Exited _socketConnection" + _socketConnection);
        }
    }

    /**
     * This method is responsible to return the interface id concat with node.
     * 
     * @return String
     */
    public String getInterfaceID() {
        return _interfaceID;
    }

    /**
     * This method is responsible to return the interface id concat with node.
     * 
     * @return String
     */
    public String getIP() {
        return _ip;
    }

    /**
     * This method is responsible to return the interface id concat with node.
     * 
     * @return String
     */
    public String getport() {
        return _port;
    }

    public String toString() {
        if (_socketConnection != null)
            return _socketConnection.toString();
        else
            return "";
    }

    public boolean isConnected() {
        if (_socketConnection != null)
            return _socketConnection.isConnected();
        else
            return false;

    }

    public boolean isClosed() {
        if (_socketConnection != null)
            return _socketConnection.isClosed();
        else
            return false;
    }

    public ZTEINSocketWrapper(String fileCacheId, String p_interfaceId, ZTEINSocket socket) throws BTSLBaseException {

        if (_logger.isDebugEnabled())
            _logger.debug("ZTESocketWrapper[constructor]", " Entered p_interfaceId::" + p_interfaceId + " , p_virtualIP" + socket.getVirtualIP());

        try {
            this._interfaceID = p_interfaceId;
            String ip = null;
            if (socket.getDestinationIP() == null) {
                ip = FileCache.getValue(fileCacheId, "ZTE_IN_IP_" + _interfaceID);

                if (_logger.isDebugEnabled())
                    _logger.debug("ZTESocketWrapper[constructor]", " ip :: " + ip);
                if (BTSLUtil.isNullString(ip)) {
                    _logger.error("ZTESocketWrapper[constructor]", " SOCKET_IP is not defined in the INFile");
                    // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ZTESocketWrapper[constructor]","","_interfaceID:"+_interfaceID,"","SOCKET_IP is not defined in the INFile");
                    throw new BTSLBaseException("ZTESocketWrapper[constructor]" + InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
                }
                socket.setDestinationIP(ip);
            } else {
                ip = socket.getDestinationIP();
            }

            // Fetch the Port from the INFile.
            String portStr = null;
            if (socket.getDestinationPort() == null) {
                portStr = FileCache.getValue(fileCacheId, "ZTE_IN_PORT_" + _interfaceID);

                if (_logger.isDebugEnabled())
                    _logger.debug("ZTESocketWrapper[constructor]", " portStr :: " + portStr);
                if (BTSLUtil.isNullString(portStr) || !BTSLUtil.isNumeric(portStr.trim())) {
                    _logger.error("ZTESocketWrapper[constructor] ", " SOCKET_PORT is either not defined in the INFile or its value is not numeric");
                    // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ZTESocketWrapper[constructor]","","_interfaceID:"+_interfaceID,"","SOCKET_PORT is either not defined in the INFile or its value is not numeric");
                    throw new BTSLBaseException("ZTESocketWrapper[constructor]" + InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
                }
                socket.setDestinationPort(portStr.trim());
            } else {
                portStr = socket.getDestinationPort();
            }

            int port = Integer.parseInt(portStr.trim());

            String conTimeOutStr = FileCache.getValue(fileCacheId, "SOCKET_TIMEOUT_" + _interfaceID);

            if (BTSLUtil.isNullString(portStr) || !BTSLUtil.isNumeric(conTimeOutStr.trim())) {
                _logger.error("ZTESocketWrapper[constructor] ", " SOCKET_TIMEOUT is either not defined in the INFile or its value is not numeric");
                // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ZTESocketWrapper[constructor]","","_interfaceID:"+_interfaceID,"","SOCKET_PORT is not defined in the INFile");
                throw new BTSLBaseException("ZTESocketWrapper[constructor]" + InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
            }
            _ip = ip.trim();
            _port = Integer.toString(port);

            // If the SOCKET_TIMEOUT would not be numeric the exception will be
            // catch in the catch block.

            int connectionTimeOut = Integer.parseInt(conTimeOutStr.trim());

            // Initializing the SocketConnection class to establish a socket
            // connection.
            // Put the interfaceId and sessionId and transaction id to the
            // _requestMap

            _requestMap = new HashMap<String, String>();
            _requestMap.put("interfaceID", p_interfaceId);
            _requestMap.put("TRANSACTIONHEADERID", getINHeaderTxnID());
            _requestMap.put("SESSIONID", generateSessionID());
            String loginReqStr = _formatter.generateRequest(fileCacheId, ZTEINI.ACTION_LOGIN, _requestMap);
            // Initialize the socket connection for given IP and PORT number.s
            _socketConnection = new Socket(ip.trim(), port, socket.getVirtualIP(), port);

            ZTEINHBLogger.logMessage("Socket connection created succesfully IP" + ip + " port=" + port);
            // set the OutPut stream object of the opened socket connection.
            setOutPutStream();
            // set the Input stream for the opened socket connection
            setInputStream();
            // Set the time out for the opened socket.
            setTimeout(connectionTimeOut);
            // Get the MML request string for the Login
            // Send the login request to the using PrintWriter object and Get
            // the response of login from the ZTE IN.
            sendLoginRequest(loginReqStr);
            // Read the response
            String loginRespStr = getLoginResponse(fileCacheId);
            // Parse the MML response string
            _responseMap = _formatter.parseResponse(ZTEINI.ACTION_LOGIN, loginRespStr);

            // If the login is successful then the response map would contain
            // the value of response_status
            // as COMMAN_SUCCESS,which is defined in the CommanI

            String status = (String) _responseMap.get("response_status");
            if (!ZTEINI.RESULT_OK.equals(status)) {
                _logger.error("ZTESocketWrapper[constructor]", " Login response from IN is not OK, status:" + status);
                // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ZTESocketWrapper[constructor]","","_interfaceID:"+_interfaceID,"","Login response from IN is NOT OK, status:"+status);
                throw new BTSLBaseException("ZTESocketWrapper[constructor]" + InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
            }
        } catch (BTSLBaseException be) {
            ZTEINHBLogger.logMessage("ZTESocketWrapper[constructor] BTSLBaseException bIP" + _ip + " Port " + _port + " " + be.getMessage());

            if (_socketConnection != null) {
                try {
                    _socketConnection.close();
                } catch (Exception e) {
                }
            }
            throw be;
        } catch (SocketException e) {
            ZTEINHBLogger.logMessage("Socket Exception IP" + _ip + " Port " + _port + " " + e.getMessage());
            if (_socketConnection != null) {
                try {
                    _socketConnection.close();
                } catch (Exception ex) {
                }
            }
            throw new BTSLBaseException("ZTESocketWrapper[constructor]" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            ZTEINHBLogger.logMessage("ZTESocketWrapper[constructor] Exception IP" + _ip + " Port " + _port + " " + e.getMessage());

            if (_socketConnection != null) {
                try {
                    _socketConnection.close();
                } catch (Exception ex) {
                }
            }
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ZTESocketWrapper[constructor]","","_interfaceID:"+_interfaceID,"","While intializing Socket connection get Exception e:"+e.getMessage());
            throw new BTSLBaseException("ZTESocketWrapper[constructor]" + InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
        }
    }

    public String currentTimeFormatStringTillSec(Date p_date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("mmss");
        String dateString = sdf.format(p_date);
        return dateString;
    }

}
