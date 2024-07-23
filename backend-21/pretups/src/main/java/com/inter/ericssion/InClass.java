package com.inter.ericssion;

/**
 * @(#)InClass.java
 *                  Copyright(c) 2005, Bharti Telesoft Int. Public Ltd.
 *                  All Rights Reserved
 *                  ------------------------------------------------------------
 *                  -------------------------------------
 *                  Author Date History
 *                  ------------------------------------------------------------
 *                  -------------------------------------
 *                  Abhijit Chauhan June 22,2005 Initial Creation
 *                  ------------------------------------------------------------
 *                  ------------------------------------
 */
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.util.BTSLUtil;
import com.inter.socket.SocketConnection;

public class InClass extends Thread {
    private String _inID = null;
    private Log _log = LogFactory.getLog(this.getClass().getName());
    long _expRetrySleep = 0;
    long _sleepB4Send = 0;
    long _sleepIfNoData = 0;
    int _connectionNum = 0;
    int _inMaxRetryCount = 0;

    public InClass(String p_strINID, int p_connectionNum) {
        _inID = p_strINID;
        _connectionNum = p_connectionNum;
        refresh();
    }

    public void refresh() {
        try {
            _expRetrySleep = Long.parseLong(FileCache.getValue(_inID, "IN_EXP_RETRY_SLEEP"));// Defines
                                                                                             // the
                                                                                             // interval
                                                                                             // by
                                                                                             // which
                                                                                             // the
                                                                                             // In
                                                                                             // thread
                                                                                             // sleeps
                                                                                             // to
                                                                                             // read
                                                                                             // the
                                                                                             // response
                                                                                             // by
                                                                                             // Out
                                                                                             // Thread.
            _sleepIfNoData = Long.parseLong(FileCache.getValue(_inID, "IN_SLEEP_NO_DATA"));// Defines
                                                                                           // the
                                                                                           // interval
                                                                                           // by
                                                                                           // which
                                                                                           // the
                                                                                           // In
                                                                                           // thread
                                                                                           // sleeps
                                                                                           // if
                                                                                           // there
                                                                                           // is
                                                                                           // no
                                                                                           // data
                                                                                           // for
                                                                                           // request.
            _sleepB4Send = Long.parseLong(FileCache.getValue(_inID, "IN_SLEEP_B4_SEND"));// Defines
                                                                                         // the
                                                                                         // interval
                                                                                         // between
                                                                                         // the
                                                                                         // request
                                                                                         // to
                                                                                         // be
                                                                                         // send.
            _inMaxRetryCount = Integer.parseInt(FileCache.getValue(_inID, "IN_MAX_RETRY_COUNT"));// Defines
                                                                                                 // the
                                                                                                 // max
                                                                                                 // retry
                                                                                                 // count
            _log.info("refresh", "_inID = " + _inID + " _connectionNum: " + _connectionNum + " _expRetrySleep=" + _expRetrySleep + " _sleepIfNoData=" + _sleepIfNoData + " _sleepB4Send=" + _sleepB4Send + " _inMaxRetryCount=" + _inMaxRetryCount);
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("refresh", "_inID = " + _inID + " _connectionNum: " + _connectionNum + " Check Values for _expRetrySleep , _sleepIfNoData, _sleepB4Send,_inMaxRetryCount in IN File");
        }
    }

    /**
     * Method to update the File related cache values
     * 
     * @param p_interfaceID
     * @param p_connNumber
     */
    public void update(String p_interfaceID, int p_connNumber) {
        try {
            _expRetrySleep = Long.parseLong(FileCache.getValue(p_interfaceID, "IN_EXP_RETRY_SLEEP"));// Defines
                                                                                                     // the
                                                                                                     // interval
                                                                                                     // by
                                                                                                     // which
                                                                                                     // the
                                                                                                     // In
                                                                                                     // thread
                                                                                                     // sleeps
                                                                                                     // to
                                                                                                     // read
                                                                                                     // the
                                                                                                     // response
                                                                                                     // by
                                                                                                     // Out
                                                                                                     // Thread.
            _sleepIfNoData = Long.parseLong(FileCache.getValue(p_interfaceID, "IN_SLEEP_NO_DATA"));// Defines
                                                                                                   // the
                                                                                                   // interval
                                                                                                   // by
                                                                                                   // which
                                                                                                   // the
                                                                                                   // In
                                                                                                   // thread
                                                                                                   // sleeps
                                                                                                   // if
                                                                                                   // there
                                                                                                   // is
                                                                                                   // no
                                                                                                   // data
                                                                                                   // for
                                                                                                   // request.
            _sleepB4Send = Long.parseLong(FileCache.getValue(p_interfaceID, "IN_SLEEP_B4_SEND"));// Defines
                                                                                                 // the
                                                                                                 // interval
                                                                                                 // between
                                                                                                 // the
                                                                                                 // request
                                                                                                 // to
                                                                                                 // be
                                                                                                 // send.
            _inMaxRetryCount = Integer.parseInt(FileCache.getValue(p_interfaceID, "IN_MAX_RETRY_COUNT"));// Defines
                                                                                                         // the
                                                                                                         // max
                                                                                                         // retry
                                                                                                         // count
            _log.info("refresh", "_inID = " + p_interfaceID + "p_connNumber=" + p_connNumber + " _expRetrySleep=" + _expRetrySleep + " _sleepIfNoData=" + _sleepIfNoData + " _sleepB4Send=" + _sleepB4Send + " _inMaxRetryCount=" + _inMaxRetryCount);
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("refresh", "_inID = " + p_interfaceID + "p_connNumber=" + p_connNumber + " Check Values for _expRetrySleep , _sleepIfNoData, _sleepB4Send,_inMaxRetryCount in IN File");
        }
    }

    public void run() {
        if (_log.isDebugEnabled())
            _log.debug("run", "Entered" + "InterfaceID = " + _inID + " _connectionNum: " + _connectionNum);
        int exceptionRetryCount = 0;
        boolean responseFlag = true;
        String inRequestStr = null;
        String errorStr = null;
        String message = null;
        SocketConnection socketConnection = null;
        PrintWriter out = null;
        String transId = null;
        Vector inBucket = null;
        Vector busyBucket = null;
        String requestStr = null;
        HashMap map = null;
        while (true) {
            // if(_log.isDebugEnabled())
            // _log.debug("run","ENTERED WHILE TIME = "+System.currentTimeMillis());
            inBucket = null;
            busyBucket = null;
            requestStr = null;
            map = null;
            errorStr = null;
            message = null;
            try {
                responseFlag = true;
                // Get the inBucket for corressponding interfaceId that contains
                // the request string.
                inBucket = (Vector) EricClient.inBucketMap.get(_inID);
                // Get the busyBucket for corressponding interfaceId.
                busyBucket = (Vector) EricClient.busyBucketMap.get(_inID);
                // Get the request string from the inBucket

                // There is no request to be send to IN
                if (inBucket.isEmpty()) {
                    Thread.currentThread().sleep(_sleepIfNoData);
                    continue;
                }
                requestStr = (String) inBucket.remove(0);
                String subReqStr = requestStr.substring(requestStr.indexOf("?") + 1, requestStr.length());
                map = BTSLUtil.getStringToHash(subReqStr, "&", "=");
                transId = (String) map.get("TransId");
                // Put the transaction id of request into busyBucket to mark
                // that request is in process.
                busyBucket.add(transId);
                inRequestStr = requestStr;
                errorStr = "200";
                try {
                    // Get a socket connection from connection pool and send the
                    // request to IN
                    socketConnection = EricSocketConnectionPool.getEricConnection(_inID + "_" + _connectionNum);
                    ;
                    out = socketConnection.getPrintWriter();
                    // Send message to IN.
                    if (_log.isDebugEnabled())
                        _log.debug("run", transId, "Sending request to IN _socketConnection: " + socketConnection + " _inID: " + _inID + " _connectionNum=" + _connectionNum + " _inRequestStr: " + inRequestStr);
                    out.println(inRequestStr);
                    out.flush();
                    if (_log.isDebugEnabled())
                        _log.debug("run", transId, "Request to IN sent _inID: " + _inID);
                    exceptionRetryCount = 0;
                } catch (Exception eRetry) {
                    eRetry.printStackTrace();
                    _log.error("run", transId, "Exception " + eRetry.getMessage() + " exceptionRetryCount: " + exceptionRetryCount + " _inID: " + _inID + " _connectionNum=" + _connectionNum);
                    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "InClass[run]", _inID, String.valueOf(_connectionNum), "", "Exception " + eRetry.getMessage() + " Trying to send request again");
                    if (exceptionRetryCount <= _inMaxRetryCount) {
                        exceptionRetryCount++;
                        // Adding the request back in inbucket so that it can be
                        // sent again
                        inBucket.add(requestStr);
                        Thread.sleep(_expRetrySleep);
                        continue;
                    }

                    _log.info("run", transId, "Not able to send request after +" + _inMaxRetryCount + " retries , Setting " + InterfaceErrorCodesI.INTERFACE_REQ_NOT_SEND + " as error Exception " + eRetry.getMessage() + " exceptionRetryCount: " + exceptionRetryCount + " _inID: " + _inID + " _connectionNum=" + _connectionNum);

                    errorStr = InterfaceErrorCodesI.INTERFACE_REQ_NOT_SEND;
                    message = "StatusCode=" + errorStr + "&Status=" + errorStr;
                    ((Map) EricClient.outBucketMap.get(_inID)).put(transId, message);
                    // EricPool.reconnectEricConnection(strINID);
                }
                Thread.currentThread().sleep(_sleepB4Send);
                /*
                 * while(responseFlag)
                 * {
                 * if(busyBucket.contains(transId))
                 * {
                 * if(_log.isDebugEnabled())
                 * _log.debug("run","LOOPING for  busyBucket: "
                 * +busyBucket+"TIME = "
                 * +System.currentTimeMillis()+" _sleepB4Send: "+_sleepB4Send);
                 * responseFlag = true;
                 * Thread.currentThread().sleep(_sleepB4Send);
                 * }
                 * else
                 * {
                 * responseFlag = false;
                 * }
                 * }
                 */
            } catch (Exception e) {
                // Commented for the Testing so that log can be visible
                e.printStackTrace();
                try {
                    Thread.currentThread().sleep(_sleepIfNoData);
                } catch (Exception ex) {
                }
            }
        }// end of while
    }// End of Method
}
