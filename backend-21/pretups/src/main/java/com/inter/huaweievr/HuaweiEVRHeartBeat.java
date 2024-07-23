package com.inter.huaweievr;

/**
 * @(#)HuaweiEVRHeartBeat.java
 *                             Copyright(c) 2007, Bharti Telesoft Int. Public
 *                             Ltd.
 *                             All Rights Reserved
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Author Date History
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Vinay Kumar Singh December 10, 2007 Initial
 *                             Creation
 *                             ------------------------------------------------
 *                             ------------------------------------------------
 *                             This class would implements the logic to send a
 *                             heart beat message to the socket connection
 *                             corresponding to the pool of given interface id.
 */
import java.io.BufferedReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Vector;
import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceUtil;

public class HuaweiEVRHeartBeat implements Runnable {
    private Log _log = LogFactory.getLog(HuaweiEVRHeartBeat.class.getName());
    HuaweiEVRSocketWrapper _socketConnection = null;
    OutputStream _out = null;
    private String _interfaceID = null;
    private HuaweiEVRRequestFormatter _formatter = null;
    private HashMap _requestMap = null;
    private String _requestStr = null;
    private String _responseStr = null;
    private String _startFlag = null;
    private Vector _freeList = null;
    private Vector _busyList = null;
    private StringBuffer responseBuffer = null;
    private BufferedReader in = null;
    private boolean _startHeartBeat = true;
    private long _heartBeatSleepTime = 0;
    private int _heartBeatCount = 0;
    private long _heartBeatTime = 0;

    public HuaweiEVRHeartBeat(String p_interfaceID) throws Exception {
        this._interfaceID = p_interfaceID;
        _formatter = new HuaweiEVRRequestFormatter();
        _requestMap = new HashMap();
        String heartBeatCommand = FileCache.getValue(_interfaceID, "HEART_BEAT_COMMAND");
        if (InterfaceUtil.isNullString(heartBeatCommand)) {
            _log.error("HuaweiEVRHeartBeat[Constructor]", "HEART_BEAT_COMMAND is not defined in the INFile");
            throw new BTSLBaseException(this, "HuaweiEVRHeartBeat[Constructor]", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
        }
        _requestMap.put("HEART_BEAT_COMMAND", heartBeatCommand.trim());
        _startFlag = FileCache.getValue(_interfaceID, "START_FLAG");
        if (InterfaceUtil.isNullString(_startFlag)) {
            _log.error("HuaweiEVRHeartBeat[Constructor]", "START_FLAG is not defined in the INFile");
            throw new BTSLBaseException(this, "HuaweiEVRHeartBeat[Constructor]", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
        }
        _requestMap.put("START_FLAG", _startFlag.trim());

        /*
         * String heartBeatSleepTimeStr=FileCache.getValue(_interfaceID,
         * "HEART_BEAT_SLEEP_TIME");
         * if(InterfaceUtil.isNullString(heartBeatSleepTimeStr))
         * {
         * _log.error("HuaweiEVRHeartBeat[Constructor]",
         * "HEART_BEAT_SLEEP_TIME is not defined in the INFile");
         * heartBeatSleepTimeStr="20000";
         * }
         * _heartBeatSleepTime = Long.parseLong(heartBeatSleepTimeStr.trim());
         */
        String heartBeatCountStr = FileCache.getValue(_interfaceID, "OPTIMUM_HEART_BEAT_COUNT");
        if (InterfaceUtil.isNullString(heartBeatCountStr)) {
            _log.error("HuaweiEVRHeartBeat[Constructor]", "OPTIMUM_HEART_BEAT_COUNT is not defined in the INFile");
            heartBeatCountStr = "3";
        }
        _heartBeatCount = Integer.parseInt(heartBeatCountStr.trim());

        String heartBeatTimeStr = FileCache.getValue(_interfaceID, "OPTIMUM_HEART_BEAT_TIME");
        if (InterfaceUtil.isNullString(heartBeatTimeStr)) {
            _log.error("HuaweiEVRHeartBeat[Constructor]", "OPTIMUM_HEART_BEAT_TIME is not defined in the INFile");
            heartBeatTimeStr = "120000";
        }
        _heartBeatTime = Long.parseLong(heartBeatTimeStr.trim());

        int poolSize = Integer.parseInt(FileCache.getValue(_interfaceID, "MAX_POOL_SIZE"));

        _heartBeatSleepTime = Math.round(_heartBeatTime / (_heartBeatCount * poolSize));

        try {
            _requestStr = _formatter.generateRequest(HuaweiEVRI.ACTION_HEART_BEAT, _requestMap);
        } catch (Exception e) {
            e.printStackTrace();// for testing
        }
        _freeList = (Vector) HuaweiEVRPoolManager._freeBucket.get(_interfaceID);
        _busyList = (Vector) HuaweiEVRPoolManager._busyBucket.get(_interfaceID);
        Thread currentHearBeat = new Thread(this);
        currentHearBeat.start();
    }

    public void run() {
        while (_startHeartBeat) {
            try {
                if (_log.isDebugEnabled())
                    _log.debug("run", "Entered");
                _socketConnection = HuaweiEVRPoolManager.getClientObject(_interfaceID, true);
                _out = _socketConnection.getPrintWriter();// getPrintWriter
                                                          // returns the Object
                                                          // of OutPutStream.
                _out.write(_requestStr.getBytes());
                _out.flush();
                if (_log.isDebugEnabled())
                    _log.debug("run", " Heartbeat message : " + _requestStr + " for session id = " + _socketConnection.getSessionID() + "sent to IN at " + getCurrentTime() + " ,_heartBeatSleepTime :" + _heartBeatSleepTime);
            } catch (Exception e) {
                e.printStackTrace();// for testing purpose.
            } finally {
                try {
                    _busyList.remove(_socketConnection);
                    _freeList.add(_socketConnection);
                    Thread.sleep(_heartBeatSleepTime);
                } catch (Exception ex) {
                    ex.printStackTrace();// for testing purpose.
                }
            }
        } // end of while loop
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
     * @throws BTSLBaseException
     */
    public static String getCurrentTime() {
        java.util.Date mydate = new java.util.Date();
        // Change on 17/05/06 for making the TXN ID as unique in Interface
        // Transaction Table (CR00021)
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssSSSSS");
        String dateString = sdf.format(mydate);
        return dateString;
    }
}
