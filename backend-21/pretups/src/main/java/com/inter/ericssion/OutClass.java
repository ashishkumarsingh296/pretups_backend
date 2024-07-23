package com.inter.ericssion;

/**
 * @(#)OutClass.java
 *                   Copyright(c) 2005, Bharti Telesoft Int. Public Ltd.
 *                   All Rights Reserved
 *                   ----------------------------------------------------------
 *                   ---------------------------------------
 *                   Author Date History
 *                   ----------------------------------------------------------
 *                   ---------------------------------------
 *                   Abhijit Chauhan June 22,2005 Initial Creation
 *                   Ashish Kumar July 13,2005 Modification
 *                   ----------------------------------------------------------
 *                   --------------------------------------
 */
import java.io.*;
import java.net.SocketTimeoutException;
import java.util.*;

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.util.BTSLUtil;
import com.inter.socket.SocketConnection;

public class OutClass extends Thread {
    private String _inID = null;
    private int _connectionNum = 0;
    private String _ip = null;
    private int _port = 0;
    private long _sleep = 0;
    private long _exceptionSleep = 0;
    private long _conExceptionSleep = 0;
    private int _maxRetryCount = 0;
    private Log _log = LogFactory.getLog(this.getClass().getName());

    public OutClass(String p_strINID, int p_connectionNum) {
        _inID = p_strINID;
        _connectionNum = p_connectionNum;
        _ip = FileCache.getValue(_inID, "IP");
        _port = Integer.parseInt(FileCache.getValue(_inID, "PORT"));
        refresh();
    }

    public void refresh() {
        _sleep = Long.parseLong(FileCache.getValue(_inID, "OUT_SLEEP"));
        _exceptionSleep = Long.parseLong(FileCache.getValue(_inID, "OUT_EXP_SLEEP"));
        _conExceptionSleep = Long.parseLong(FileCache.getValue(_inID, "OUT_CON_EXP_SLEEP"));
        _maxRetryCount = Integer.parseInt(FileCache.getValue(_inID, "OUT_MAX_RETRY_COUNT"));
        _log.info("refresh", "_inID = " + _inID + " _connectionNum: " + _connectionNum + " _sleep=" + _sleep + " _exceptionSleep=" + _exceptionSleep + " _conExceptionSleep=" + _conExceptionSleep + " _maxRetryCount=" + _maxRetryCount);

    }

    /**
     * Method to update the File related cache values
     * 
     * @param p_interfaceID
     * @param p_connNumber
     */
    public void update(String p_interfaceID, int p_connNumber) {
        _sleep = Long.parseLong(FileCache.getValue(p_interfaceID, "OUT_SLEEP"));
        _exceptionSleep = Long.parseLong(FileCache.getValue(p_interfaceID, "OUT_EXP_SLEEP"));
        _conExceptionSleep = Long.parseLong(FileCache.getValue(p_interfaceID, "OUT_CON_EXP_SLEEP"));
        _maxRetryCount = Integer.parseInt(FileCache.getValue(p_interfaceID, "OUT_MAX_RETRY_COUNT"));
        _log.info("update", "p_interfaceID = " + p_interfaceID + " p_connNumber: " + p_connNumber + " _sleep=" + _sleep + " _exceptionSleep=" + _exceptionSleep + " _conExceptionSleep=" + _conExceptionSleep + " _maxRetryCount=" + _maxRetryCount);
    }

    public void run() {
        if (_log.isDebugEnabled())
            _log.debug("run", "Entered" + _inID + "_connectionNum: " + _connectionNum);
        int exceptionRetryCount = 0;
        SocketConnection socketConnection = null;
        BufferedReader in = null;
        String transId = null;
        String message = null;
        while (true) {
            try {
                socketConnection = EricSocketConnectionPool.getEricConnection(_inID + "_" + _connectionNum);
                if (socketConnection == null) {
                    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "OutClass[run]", _inID, String.valueOf(_connectionNum), "", "Trying to reconnect to " + _inID + " " + _connectionNum);
                    Thread.currentThread().sleep(_conExceptionSleep);
                    EricSocketConnectionPool.reconnectEricConnection(_inID + "_" + _connectionNum, _ip, _port);
                    continue;
                }
                try {
                    in = socketConnection.getBufferedReader();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OutClass[run]", _inID, String.valueOf(_connectionNum), "", "Exception " + ex.getMessage() + " Tryingto reconnect");
                    Thread.currentThread().sleep(_conExceptionSleep);
                    EricSocketConnectionPool.reconnectEricConnection(_inID + "_" + _connectionNum, _ip, _port);
                    continue;
                }
                if (_log.isDebugEnabled())
                    _log.debug("run", "OutClass WAITING FOR IN RESPONSE_socketConnection:" + socketConnection + "::::::::::IN ID= " + _inID + " connectionNum:" + _connectionNum);
                try {
                    if (_log.isDebugEnabled())
                        _log.debug("run", "reading message");
                    message = in.readLine();
                } catch (SocketTimeoutException sex) {
                    _log.error("OutClass", "run SocketTimeoutException = " + sex.getMessage());
                    message = null;
                    Thread.currentThread().sleep(_exceptionSleep);
                    continue;
                } catch (Exception ex1) {
                    ex1.printStackTrace();
                    _log.error("OutClass", "run Exception ex1 = " + ex1.getMessage());
                    message = null;
                }
                if (_log.isDebugEnabled())
                    _log.debug("run", " AFTER READLINE _message: " + message + "_inID: " + _inID + " _connectionNum:" + _connectionNum);
                if (message == null) {
                    while (exceptionRetryCount++ < _maxRetryCount) {
                        if (_log.isDebugEnabled())
                            _log.debug("run", " Retry Count incase of message null : [" + exceptionRetryCount + "]_inID: " + _inID + " _connectionNum:" + _connectionNum + "_sleep=" + _sleep);
                        try {
                            message = in.readLine();
                        } catch (Exception ex2) {

                            ex2.printStackTrace();
                            message = null;
                        }
                        if (message != null)
                            break;
                        Thread.currentThread().sleep(_sleep);
                    }
                    if (_log.isDebugEnabled())
                        _log.debug("run", "Time at whichresponse is recieved from IN " + System.currentTimeMillis());
                    if (exceptionRetryCount > _maxRetryCount) {
                        _log.info("run", "Maximum Retry Count reached IN ID = " + _inID + "exceptionRetryCount:" + exceptionRetryCount + "connectionNum:" + _connectionNum + "_maxRetryCount: " + _maxRetryCount);
                        throw new Exception("Maximum Retry Count reached IN ID = " + _inID + "exceptionRetryCount:" + exceptionRetryCount + "connectionNum:" + _connectionNum + "_maxRetryCount: " + _maxRetryCount);
                    }
                }
                if (_log.isDebugEnabled())
                    _log.debug("run", "Raw Response from Eric IN _message: " + socketConnection);
                HashMap map = BTSLUtil.getStringToHash(message, "&", "=");
                transId = (String) map.get("TransId");
                if (_log.isDebugEnabled())
                    _log.debug("run", " _transId=" + transId + "AFTER READLINEPARSING  _message: " + message + " _inID: " + _inID + "_connectionNum: " + _connectionNum);
                Vector busyBucket = (Vector) EricClient.busyBucketMap.get(_inID);
                Map outBucket = (Map) EricClient.outBucketMap.get(_inID);
                if (_log.isDebugEnabled())
                    _log.debug("run", "outBucket = " + outBucket);
                if (busyBucket.contains(transId))
                    outBucket.put(transId, message);
                else
                    _log.info("run", "_transId: " + transId + "LATE RESPONSE_message: " + message + "_inID: " + _inID + "_connectionNum: " + _connectionNum + "TIME ----------> " + System.currentTimeMillis());
                if (_log.isDebugEnabled())
                    _log.debug("run", "ERIC RESP HASH MAP AFTER PUTTINGRESPONSE ================== outBucket: " + outBucket + "connectionNum:" + _connectionNum);
                exceptionRetryCount = 0;
            } catch (Exception e) {
                e.printStackTrace();
                EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OutClass[run]", _inID, String.valueOf(_connectionNum), "", "Exception " + e.getMessage());
                if (_log.isDebugEnabled())
                    _log.debug("run", "ERIC RECV RETRY ATTEMPT = " + exceptionRetryCount + "::TransId = " + transId + "::In Id= " + _inID);
                if (exceptionRetryCount >= _maxRetryCount)
                    EricSocketConnectionPool.reconnectEricConnection(_inID + "_" + _connectionNum, _ip, _port);

                try {
                    Thread.currentThread().sleep(_exceptionSleep);
                } catch (Exception ex) {
                }
                exceptionRetryCount = 0;
            }
        }
    }// End of Method
}