package com.inter.huaweicitycell;

/**
 * @HuaweiHeartBeat.java
 *                       Copyright(c) 2009, Bharti Telesoft Int. Public Ltd.
 *                       All Rights Reserved
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       Author Date History
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       Vipan Nov 17, 2010 Initial Creation
 *                       ------------------------------------------------------
 *                       -----------------------------------------
 *                       This class would implements the logic to send a heart
 *                       beat message to the socket connection
 *                       corresponding to the pool of given interface id.
 */
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Vector;

import com.btsl.common.BaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceUtil;

public class HuaweiHeartBeat implements Runnable {

    private Log _log = LogFactory.getLog(HuaweiHeartBeat.class.getName());
    OutputStream _out = null;
    InputStream _in = null;
    private String _interfaceID = null;
    private boolean _startHeartBeat = true;
    Long inProcessTime = null;
    private long _heartBeatTime = 0;
    StringBuffer responseBuffer = null;
    String _responseStr = null;
    boolean logsEnable = true;

    public HuaweiHeartBeat(String p_interfaceID) throws Exception {

        this._interfaceID = p_interfaceID;
        String hbLogs = FileCache.getValue(_interfaceID, "HEARTBEATLOGS");
        if (InterfaceUtil.isNullString(hbLogs)) {
            _log.error("HuaweiHeartBeat[Constructor]", "HEARTBEATLOGS is not defined in the INFile");
            hbLogs = "Y";
        }
        if (hbLogs != null && hbLogs.equalsIgnoreCase("Y"))
            logsEnable = true;

        String heartBeatPoolConnectionAllowedStr = FileCache.getValue(_interfaceID, "HEARTBEAT_POOL_CONNECTION_ALLOWED");
        if (InterfaceUtil.isNullString(heartBeatPoolConnectionAllowedStr)) {
            _log.error("HuaweiHeartBeat[Constructor]", "heartBeatPoolConnectionAllowedStr is not defined in the INFile");
            heartBeatPoolConnectionAllowedStr = "Y";
        }
        if ("N".equalsIgnoreCase(heartBeatPoolConnectionAllowedStr))
            return;
        String heartBeatTimeStr = FileCache.getValue(_interfaceID, "OPTIMUM_HEART_BEAT_TIME");
        if (InterfaceUtil.isNullString(heartBeatTimeStr)) {
            _log.error("HuaweiHeartBeat[Constructor]", "OPTIMUM_HEART_BEAT_TIME is not defined in the INFile");
            heartBeatTimeStr = "10000";
        }
        _heartBeatTime = Long.parseLong(heartBeatTimeStr.trim());
        try {
            if (HuaweiRequestFormatter.heartBeatRequestInByte == null)
                new HuaweiRequestFormatter().heartBeatRequest(_interfaceID);
        } catch (Exception e) {
            e.printStackTrace();// for testing
        }
        if (logsEnable)
            HuaweiProps.logMessage("HuaweiHeartBeat :: HuaweiHeartBeat() :: Entered for Interfaceid = " + _interfaceID + " , heartBeatTime = " + _heartBeatTime + " , heartBeatRequest = " + InterfaceUtil.printByteData(HuaweiRequestFormatter.heartBeatRequestInByte));
        Thread currentHearBeat = new Thread(this);
        currentHearBeat.start();
    }

    /**
     * This method manage the connections
     */
    public void run() {
        Vector<Object> busyList = null;
        Vector<Object> freeList = null;
        boolean noFreeConnection = false;
        boolean socketConnected = true;
        boolean flag = true;
        HuaweiSocketWrapper _socketConnection = null;
        boolean makeFreeConnectionnull = false;
        while (_startHeartBeat) {
            try {
                Thread.sleep(_heartBeatTime);
                socketConnected = true;
                noFreeConnection = false;
                _socketConnection = null;
                makeFreeConnectionnull = false;
                flag = true;

                if (logsEnable)
                    HuaweiProps.logMessage("HuaweiHeartBeat :: run() :: Entered for Interfaceid = " + _interfaceID);
                try {
                    busyList = (Vector<Object>) HuaweiPoolManager._busyBucket.get(_interfaceID);// get
                                                                                                // busy
                                                                                                // and
                                                                                                // free
                                                                                                // pool
                                                                                                // from
                                                                                                // pool
                                                                                                // mgr.
                    freeList = (Vector<Object>) HuaweiPoolManager._freeBucket.get(_interfaceID);

                    if (busyList != null && freeList != null) {
                        int connectionPoolSize = freeList.size() + busyList.size();
                        int counter = 0, maxPoolSize = Integer.parseInt(FileCache.getValue(_interfaceID, "MAX_POOL_SIZE"));
                        String HbConPoolAllowed = InterfaceUtil.NullToString(FileCache.getValue(_interfaceID, "HB_CON_POOL_ALLOWED"));
                        if (connectionPoolSize < maxPoolSize / 2 && HbConPoolAllowed.equals("Y")) {
                            while (connectionPoolSize < maxPoolSize && counter++ < maxPoolSize) {
                                try {
                                    _socketConnection = new HuaweiSocketWrapper(_interfaceID);
                                    freeList.add(_socketConnection);
                                } catch (Exception bex) {
                                    HuaweiProps.logMessage("HuaweiHeartBeat :: run() :: Exception while adding connection in pool , Exception = " + bex.getMessage());
                                }
                                connectionPoolSize = freeList.size() + busyList.size();
                                _socketConnection = null;
                            }
                        }
                    }

                } catch (Exception e) {
                    HuaweiProps.logMessage("HuaweiHeartBeat :: run() :: Exception while getting freelist , Exception = " + e.getMessage());
                    flag = false;
                    continue;
                }
                try {
                    _socketConnection = HuaweiPoolManager.getClientObject(_interfaceID, true);
                    if (_socketConnection == null) {
                        if (logsEnable)
                            HuaweiProps.logMessage("HuaweiHeartBeat :: run() :: After getting scoket connection and if socket connection is null , Going to get new socket connection , _socketConnection = " + _socketConnection);
                        socketConnected = false;
                        busyList.add(_socketConnection);
                        freeList.remove(_socketConnection);
                        try {
                            HuaweiSocketWrapper newSocketConnection = HuaweiPoolManager.getNewClientObject(_interfaceID);
                            busyList.remove(_socketConnection);
                            _socketConnection = newSocketConnection;
                            busyList.add(_socketConnection);
                        } catch (Exception e1) {
                            if (logsEnable)
                                HuaweiProps.logMessage("HuaweiHeartBeat :: run() ::  Exception while creating the new conection from getNewClientObject() method , Message = " + e1.getMessage());
                            socketConnected = false;
                            // raise the alarm
                        }
                    }
                } catch (Exception e) {
                    if (logsEnable)
                        HuaweiProps.logMessage("HuaweiHeartBeat :: run() ::  Exception while getting the new conection from getClientObject() method , Message = " + e.getMessage());
                    String str = e.getMessage();
                    if (str.equalsIgnoreCase(InterfaceErrorCodesI.ERROR_NO_FREE_OBJ_IN_POOL)) {
                        HuaweiProps.logMessage("[HEARTBEAT][socketConnected] " + socketConnected + ", [Status]{no free object in the pool try after some time} ,for Interface ID =" + _interfaceID);
                        noFreeConnection = true;
                        flag = false;
                        continue;
                    }
                }
                try {
                    if (!noFreeConnection && socketConnected) {
                        try {
                            sentHeartBeat(_socketConnection);
                        } catch (Exception e) {
                            e.printStackTrace();
                            try {
                                if (logsEnable)
                                    HuaweiProps.logMessage("HuaweiHeartBeat :: run() ::  Exception while sending the heartbeat , Message = " + e.getMessage() + " _socketConnection = " + _socketConnection);
                                busyList.remove(_socketConnection);
                                if (_socketConnection != null) {
                                    _socketConnection.destroy();
                                    _socketConnection.close();
                                    _socketConnection = null;
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            try {
                                if (logsEnable)
                                    HuaweiProps.logMessage("HuaweiHeartBeat :: run() ::  Creating new connection after destroy()");

                                HuaweiSocketWrapper newSocketConnection = HuaweiPoolManager.getNewClientObject(_interfaceID);
                                _socketConnection = newSocketConnection;
                                busyList.add(_socketConnection);
                                if (logsEnable)
                                    HuaweiProps.logMessage("HuaweiHeartBeat :: run() ::  After creating new connection _socketConnection =  " + _socketConnection);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                                if (logsEnable)
                                    HuaweiProps.logMessage("HuaweiHeartBeat :: run() :: Exception while creating new connection ");
                                socketConnected = false;
                                makeFreeConnectionnull = true;
                            }
                        }
                    }
                } catch (Exception e) {
                }
            } catch (Exception e) {
                e.printStackTrace();// for testing purpose.
            } finally {
                if (logsEnable)
                    HuaweiProps.logMessage("HuaweiHeartBeat :: run() :: In finally before adding socket connection in pool _socketConnection = " + _socketConnection + " , busyBucket = " + HuaweiPoolManager._busyBucket + " , freeBucket = " + HuaweiPoolManager._freeBucket);
                if (flag) {
                    busyList.remove(_socketConnection);
                    if (makeFreeConnectionnull)
                        _socketConnection = null;
                    freeList.add(_socketConnection);
                }
                if (logsEnable)
                    HuaweiProps.logMessage("HuaweiHeartBeat :: run() :: In finally after adding socket connection in pool _socketConnection = " + _socketConnection + " , busyBucket = " + HuaweiPoolManager._busyBucket + " , freeBucket = " + HuaweiPoolManager._freeBucket);
            }
        } // end of while loop
    }

    /**
     * 
     * @param p_socketConnection
     * @throws BaseException
     */
    private void sentHeartBeat(HuaweiSocketWrapper p_socketConnection) throws BaseException {
        if (logsEnable)
            HuaweiProps.logMessage("HuaweiHeartBeat :: sentHeartBeat() :: Entered p_socketConnection = " + p_socketConnection.toString() + " :: heartBeatRequestByte = " + InterfaceUtil.printByteData(HuaweiRequestFormatter.heartBeatRequestInByte));
        byte[] buf = null;

        try {
            long startTime = System.currentTimeMillis();
            _out = p_socketConnection.getPrintWriter();
            _in = p_socketConnection.getInputStream();
            _out.write(HuaweiRequestFormatter.heartBeatRequestInByte);
            _out.flush();
            buf = new byte[HuaweiRequestFormatter.heartBeatRequestInByte.length];
            _in.read(buf, 0, buf.length);
            inProcessTime = System.currentTimeMillis() - startTime;
        } catch (IOException e) {
            e.printStackTrace();
            throw new BaseException("IOException" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(" Exception " + e.getMessage());
        } finally {
            if (logsEnable)
                HuaweiProps.logMessage("HuaweiHeartBeat :: sentHeartBeat() :: Exit heartBeatResponseByte = " + InterfaceUtil.printByteData(buf) + " , Processing Time =  " + (inProcessTime) + " ms");
        }

    }

    /**
     * This method is used to stop the thread.
     * 
     */
    public void stopHearBeat() {
        _startHeartBeat = false;
    }

    /**
     * Get IN TransactionID
     * 
     * @return
     * @throws BaseException
     */
    public static String getCurrentTime() {

        java.util.Date mydate = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssSSSSS");
        String dateString = sdf.format(mydate);

        return dateString;
    }

    public void destroy(HuaweiSocketWrapper p_socket) {
        if (logsEnable)
            HuaweiProps.logMessage("HuaweiHeartBeat :: destroy() :: Entered");
        try {
            try {
                _out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                _in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                p_socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("HuaweiSocketWrapper[destroy]", "Error while destroying socket connection");
        } finally {
            if (logsEnable)
                HuaweiProps.logMessage("HuaweiHeartBeat :: destroy() :: Exit , p_socketConnection = " + p_socket);
        }
    }
}
