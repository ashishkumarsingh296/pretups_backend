package com.inter.huaweicitycell;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.BindException;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
 * @(#)HuaweiSocketWrapper.java
 *                              Copyright(c) 2009, Bharti Telesoft Int. Public
 *                              Ltd.
 *                              All Rights Reserved
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Author Date History
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              vipan Nov 17, 2010 Initial Creation
 *                              ------------------------------------------------
 *                              ------------------------------------------------
 *                              This class would be responsible to provide the
 *                              Wrapper of SocketConnection class.
 */
public class HuaweiSocketWrapper extends Socket {

    public Log _log = LogFactory.getLog(this.getClass().getName());
    private static int _txnCounter = 0;
    private static int _sessionIdInt = 0;
    private String _sessionId = null;
    private Socket _socketConnection = null;
    private InputStream _in = null;
    private HuaweiRequestFormatter _formatter = new HuaweiRequestFormatter();
    private OutputStream _out = null;
    private HashMap<String, String> _responseMap = null;
    private String _interfaceID = null;
    boolean logsEnable = false;
    byte heartBeatResponsebuffer[] = null;

    public HuaweiSocketWrapper() throws BTSLBaseException {
    }

    public HuaweiSocketWrapper(String p_interfaceId) throws BTSLBaseException {
        String status = null;
        try {
            this._interfaceID = p_interfaceId;

            String hbLogs = FileCache.getValue(_interfaceID, "SOCKETWRAPPERLOGS");
            if (InterfaceUtil.isNullString(hbLogs)) {
                HuaweiProps.logMessage("[HuaweiSocketWrapper] [Status] SOCKETWRAPPERLOGS is not defined in the INFile");
                hbLogs = "N";
            }
            if (hbLogs != null && hbLogs.equalsIgnoreCase("Y"))
                logsEnable = true;

            // Generate the session id for the login request.
            _sessionId = generateSessionID();
            String localportStr = FileCache.getValue(p_interfaceId, "LOCAL_SOCKET_PORT");
            if (InterfaceUtil.isNullString(localportStr)) {
                _log.error("HuaweiSocketWrapper[constructor]", "LOCAL_SOCKET_PORT is either not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiSocketWrapper[constructor]", "", "_interfaceID:" + _interfaceID, "", "LOCAL_SOCKET_PORT is either not defined in the INFile");
                throw new BTSLBaseException(this, "HuaweiSocketWrapper[constructor]", InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
            }

            // Fetch the IP address from the INFile.
            String ip = FileCache.getValue(p_interfaceId, "SOCKET_IP");
            if (InterfaceUtil.isNullString(ip)) {
                _log.error("HuaweiSocketWrapper[constructor]", "SOCKET_IP is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiSocketWrapper[constructor]", "", "_interfaceID:" + _interfaceID, "", "SOCKET_IP is not defined in the INFile");
                throw new BTSLBaseException(this, "HuaweiSocketWrapper[constructor]", InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
            }
            // Fetch the Port from the INFile.
            String portStr = FileCache.getValue(p_interfaceId, "SOCKET_PORT");
            if (InterfaceUtil.isNullString(portStr)) {
                _log.error("HuaweiSocketWrapper[constructor]", "SOCKET_PORT is either not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiSocketWrapper[constructor]", "", "_interfaceID:" + _interfaceID, "", "SOCKET_PORT is either not defined in the INFile");
                throw new BTSLBaseException(this, "HuaweiSocketWrapper[constructor]", InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
            }
            String[] inPortArray = portStr.split(",");
            if (inPortArray == null) {
                _log.error("HuaweiSocketWrapper[constructor]", "SOCKET_PORT is not defined in the INFile ");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiSocketWrapper[constructor]", "", "_interfaceID:" + _interfaceID, "", "SOCKET_PORT is either not defined in the INFile or its value is not numeric");
                throw new BTSLBaseException(this, "HuaweiSocketWrapper[constructor]", InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
            }

            String conTimeOutStr = FileCache.getValue(p_interfaceId, "SOCKET_TIMEOUT");
            if (InterfaceUtil.isNullString(portStr) || !InterfaceUtil.isNumeric(conTimeOutStr.trim())) {
                _log.error("HuaweiSocketWrapper[constructor]", "SOCKET_TIMEOUT is either not defined in the INFile or its value is not numeric");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiSocketWrapper[constructor]", "", "_interfaceID:" + _interfaceID, "", "SOCKET_PORT is not defined in the INFile");
                throw new BTSLBaseException(this, "HuaweiSocketWrapper[constructor]", InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
            }

            String localSocketIP = FileCache.getValue(p_interfaceId, "LOCAL_SOCKET_IP");
            if (InterfaceUtil.isNullString(localSocketIP)) {
                _log.error("HuaweiSocketWrapper[constructor]", "LOCAL_SOCKET_IP is either not defined in the INFile or its value is not numeric");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiSocketWrapper[constructor]", "", "_interfaceID:" + _interfaceID, "", "LOCAL_SOCKET_IP is not defined in the INFile");
                throw new BTSLBaseException(this, "HuaweiSocketWrapper[constructor]", InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
            }
            String[] localSocketIPArray = localSocketIP.split(",");
            if (localSocketIPArray == null) {
                _log.error("HuaweiSocketWrapper[constructor]", "LOCAL_SOCKET_IP is not defined in the INFile ");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiSocketWrapper[constructor]", "", "_interfaceID:" + _interfaceID, "", "LOCAL_SOCKET_IP is not defined in the INFile ");
                throw new BTSLBaseException(this, "HuaweiSocketWrapper[constructor]", InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
            }

            // If the SOCKET_TIMEOUT would not be numeric the exception will be
            // catch in the catch block.
            int connectionTimeOut = Integer.parseInt(conTimeOutStr.trim());
            if (HuaweiRequestFormatter.heartBeatRequestInByte == null)
                new HuaweiRequestFormatter().heartBeatRequest(_interfaceID);

            createConnection(ip.trim(), inPortArray, localSocketIPArray, connectionTimeOut);

            if (logsEnable)
                HuaweiProps.logMessage("[HuaweiSocketWrapper] [Status]{ After socket connection}[_socketConnection]" + _socketConnection);
            sendHeartBeatRequest(HuaweiRequestFormatter.heartBeatRequestInByte);
            _responseMap = _formatter.parseResponse(HuaweiI.ACTION_HEART_BEAT, heartBeatResponsebuffer);
            status = (String) _responseMap.get("status");
            if (logsEnable)
                HuaweiProps.logMessage("[HuaweiSocketWrapper] [Status]{ Final Heart Beat Status  }[status] " + status);
            if (!HuaweiI.HEARTBEAT_SUCCEEDED.equals(status)) {
                _log.error("HuaweiSocketWrapper[constructor]", "Login response from IN is not OK, status:" + status);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiSocketWrapper[constructor]", "", "_interfaceID:" + _interfaceID, "", "Login response from IN is NOT OK, status:" + status);
                throw new BTSLBaseException(this, "HuaweiSocketWrapper[constructor]", InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
            }
        } catch (BTSLBaseException be) {
            be.printStackTrace();
            if (logsEnable)
                HuaweiProps.logMessage("[HuaweiSocketWrapper] [Exception]{ Exception in socket Wrapper  }[Message] " + be.getMessage());
            _log.error("HuaweiSocketWrapper[constructor]", "BTSLBaseException be:" + be.getMessage());
            if (_socketConnection != null) {
                try {
                    _socketConnection.close();
                } catch (Exception e) {
                }
            }
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            if (logsEnable)
                HuaweiProps.logMessage("[HuaweiSocketWrapper] [Exception]{ Exception in socket Wrapper  }[Message] " + e.getMessage());
            _log.error("HuaweiSocketWrapper[constructor]", "Exception e:" + e.getMessage());
            if (_socketConnection != null) {
                try {
                    _socketConnection.close();
                } catch (Exception ex) {
                }
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiSocketWrapper[constructor]", "", "_interfaceID:" + _interfaceID, "", "While intializing Socket connection get Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "HuaweiSocketWrapper[constructor]", InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
        } finally {
            if (logsEnable)
                HuaweiProps.logMessage("HuaweiSocketWrapper :: HuaweiSocketWrapper() :: Exit , _socketConnection = " + _socketConnection + " , status = " + status);
        }

    }

    public HuaweiSocketWrapper(String p_interfaceId, String p_INIP, String p_INPort, InetAddress p_localAddress, int p_localPort) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("HuaweiSocketWrapper[constructor]", "Entered p_interfaceId::" + p_interfaceId + " :: p_INIP = " + p_INIP + " :: p_INPort = " + p_INPort + ", :: p_localAddress =  " + p_localAddress + "p_localPort = " + p_localPort);

        Socket socketConnection = null;
        try {
            this._interfaceID = p_interfaceId;

            String hbLogs = FileCache.getValue(_interfaceID, "SOCKETWRAPPERLOGS");
            if (InterfaceUtil.isNullString(hbLogs)) {
                HuaweiProps.logMessage("[HuaweiSocketWrapper] [Status] SOCKETWRAPPERLOGS is not defined in the INFile");
                hbLogs = "N";
            }
            if (hbLogs != null && hbLogs.equalsIgnoreCase("Y"))
                logsEnable = true;

            String conTimeOutStr = FileCache.getValue(_interfaceID, "SOCKET_TIMEOUT");
            if (InterfaceUtil.isNullString(conTimeOutStr)) {
                HuaweiProps.logMessage("[HuaweiSocketWrapper] [Status] SOCKET_TIMEOUT is not defined in the INFile");
                conTimeOutStr = "10000";
            }

            if (HuaweiRequestFormatter.heartBeatRequestInByte == null)
                new HuaweiRequestFormatter().heartBeatRequest(_interfaceID);
            if (logsEnable)
                _log.debug("HuaweiSocketWrapper[constructor] connection startup", "[Socket IP] =" + p_INIP + "[Socket port] =" + p_INPort + "[Local Socket ip] =" + p_localAddress + "[Local Socket port] =" + p_localPort + "[SOCKET TIMEOUT] =" + conTimeOutStr);

            socketConnection = new Socket(p_INIP.trim(), Integer.parseInt(p_INPort), p_localAddress, p_localPort);
            socketConnection.setSoTimeout(Integer.parseInt(conTimeOutStr.trim()));
            setSocketConnection(socketConnection);
            _out = socketConnection.getOutputStream();
            _in = socketConnection.getInputStream();
            _out.write(HuaweiRequestFormatter.heartBeatRequestInByte);
            _out.flush();
            byte buf[] = new byte[HuaweiRequestFormatter.heartBeatRequestInByte.length];
            _in.read(buf, 0, buf.length);
            _log.debug("HuaweiSocketWrapper[constructor]", "socketConnection :: " + socketConnection + ", Response = " + InterfaceUtil.printByteData(buf));
            if (buf == null)
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
            else if (buf.length != 5)
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
        } catch (Exception e) {
            e.printStackTrace();
            if (logsEnable)
                HuaweiProps.logMessage("[HuaweiSocketWrapper] [Exception]{ Exception in socket Wrapper  }[Message] " + e.getMessage());
            _log.error("HuaweiSocketWrapper[constructor]", "Exception e:" + e.getMessage());
            if (socketConnection != null) {
                try {
                    socketConnection.close();
                } catch (Exception ex) {
                }
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiSocketWrapper[constructor]", "", "_interfaceID:" + _interfaceID, "", "While intializing Socket connection get Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "HuaweiSocketWrapper[constructor]", InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
        } finally {
            _log.debug("[HuaweiSocketWrapper]", " [Status]{ After socket connection}[socketConnection]" + socketConnection);
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
    private void sendHeartBeatRequest(byte[] p_requestByte) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendLoginRequest", "Entered p_requestStr:" + p_requestByte.toString());
        try {
            _out = _socketConnection.getOutputStream();
            _in = _socketConnection.getInputStream();
            _out.write(p_requestByte);
            _out.flush();
            heartBeatResponsebuffer = new byte[HuaweiRequestFormatter.heartBeatRequestInByte.length];
            _in.read(heartBeatResponsebuffer, 0, heartBeatResponsebuffer.length);
            if (InterfaceUtil.isNullString(heartBeatResponsebuffer.toString()))
                throw new BTSLBaseException(this, "getHeartBeatResponse", InterfaceErrorCodesI.RC_HEARTBEAT_RESPONSE);
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("sendLoginRequest", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "getINHeaderTxnID", "", "_interfaceID:" + _interfaceID, "", "While sending the login request get Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "sendLoginRequest", InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_LOGIN_REQ_FAIL);
        } finally {
            if (logsEnable)
                HuaweiProps.logMessage("HuaweiSocketWrapper :: sendHeartBeatRequest() :: Exit , _socketConnection = " + _socketConnection + " , p_responseStrByte = " + InterfaceUtil.printByteData(heartBeatResponsebuffer));
        }

    }

    /**
     * This method is responsible to set OutputStream to Socket.
     */
    protected void setOutPutStream() {
        try {
            _out = _socketConnection.getOutputStream();
        } catch (Exception e) {

            _log.error("HuaweiSocketWrapper", "exception:" + e.getMessage());
        }
    }

    /**
     * This method is responsible to set OutputStream to Socket.
     */
    protected void setInputStream() {
        try {
            _in = _socketConnection.getInputStream();
        } catch (Exception e) {
            _log.error("HuaweiSocketWrapper", "exception:" + e.getMessage());
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiSocketWrapper[generateSessionID]", "", "_interfaceID:" + _interfaceID, "", "Exception:" + e.getMessage());
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
    public InputStream getInputStream() {
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
            _log.debug("destroy", "Entered _socketConnection = " + _socketConnection);
        try {
            _out.close();
            _in.close();
            _socketConnection.close();
        }

        catch (Exception e) {
            _log.error("HuaweiSocketWrapper[destroy]", "Error while destroying socket connection");
        } finally {
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

    /**
     * @author vipan.kumar
     * @param ip
     * @param port
     * @param timeout
     */
    public void createConnection(String ip, String[] p_port, String[] p_localSocketIp, int timeout) throws Exception {
        if (logsEnable)
            HuaweiProps.logMessage("HuaweiSocketWrapper :: createConnection() :: Entered , _socketConnection = " + _socketConnection);
        try {
            int port = 0;
            boolean socketCreated = false;
            InetAddress localIP = null;
            for (int k = 0; k < p_port.length; k++) {
                if (InterfaceUtil.isNullString(p_port[k]) || !InterfaceUtil.isNumeric(p_port[k].trim())) {
                    _log.error("HuaweiSocketWrapper[constructor]", "IN Port is not defined in the INFile or it is not numberic");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiSocketWrapper[constructor]", "", "_interfaceID:" + _interfaceID, "", "IN Port is not defined in the INFile or it is not numberic ");
                    throw new BTSLBaseException(this, "HuaweiSocketWrapper[constructor]", InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
                }
                port = Integer.parseInt(p_port[k]);
                for (int j = 0; j < p_localSocketIp.length; j++) {

                    String localSocketIP = p_localSocketIp[j];
                    if (InterfaceUtil.isNullString(localSocketIP)) {
                        _log.error("HuaweiSocketWrapper[constructor]", "LOCAL_SOCKET_IP is not defined in the INFile ");
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiSocketWrapper[constructor]", "", "_interfaceID:" + _interfaceID, "", "LOCAL_SOCKET_IP is not defined in the INFile ");
                        throw new BTSLBaseException(this, "HuaweiSocketWrapper[constructor]", InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
                    }
                    localIP = InetAddress.getByName(localSocketIP);
                    if (logsEnable)
                        HuaweiProps.logMessage("HuaweiSocketWrapper :: createConnection() new :: Entered, IN Socket ip=" + ip + " , IN Socket port = " + port + " , Local Socket ip = " + localIP + " , Local Socket port =  " + port + " , Socket timeout =  " + timeout);
                    try {
                        _socketConnection = new Socket(ip, port, localIP, port);
                        _socketConnection.setSoTimeout(timeout);
                        socketCreated = true;
                        break;
                    } catch (BindException be) {
                        if (logsEnable)
                            HuaweiProps.logMessage("HuaweiSocketWrapper :: createConnection() :: BindException , Message =  " + be.getMessage());
                        Thread.sleep(1000);
                    }
                }
                if (socketCreated)
                    break;
            }
            if (!socketCreated)
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
        } catch (Exception e) {
            e.printStackTrace();
            if (logsEnable)
                HuaweiProps.logMessage("HuaweiSocketWrapper :: createConnection() :: Exception , Message =  " + e.getMessage());
            throw new Exception(InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
        } finally {
            if (logsEnable)
                HuaweiProps.logMessage("HuaweiSocketWrapper :: createConnection() :: Exit, _socketConnection = " + _socketConnection);
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
        int inTxnLength = 9;
        int length = 0;
        int tmpLength = 0;
        try {
            counterBuffer = new StringBuffer(10);
            if (_txnCounter >= 1000)
                _txnCounter = 0;
            headerTransactionID = Time() + _txnCounter++;

            if (_log.isDebugEnabled())
                _log.debug("getINHeaderTxnID", "counter :" + headerTransactionID);
            length = headerTransactionID.length();
            tmpLength = inTxnLength - length;
            if (length < inTxnLength) {
                for (int i = 0; i < tmpLength; i++)
                    headerTransactionID = headerTransactionID + 0;
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
                _log.debug("getINHeaderTxnID", "Exit headerTransactionID:" + headerTransactionID);
        }// end of finally
        return headerTransactionID;
    }// end of getINReconTxnID

    public static String Time() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
        return sdf.format(cal.getTime());
    }

    public Socket getSocketConnection() {
        return _socketConnection;
    }

    public void setSocketConnection(Socket connection) {
        _socketConnection = connection;
    }

    public String toString() {
        return _socketConnection.toString() + " getLocalSocketAddress: " + _socketConnection.getLocalSocketAddress();

    }

    public boolean isConnected() {
        return _socketConnection.isConnected();
    }

    public boolean isClosed() {
        return _socketConnection.isClosed();
    }

}
