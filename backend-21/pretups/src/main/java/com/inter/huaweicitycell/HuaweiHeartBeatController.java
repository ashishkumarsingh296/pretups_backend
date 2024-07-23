package com.inter.huaweicitycell;

/**
 * @HuaweiHeartBeatController.java
 *                                 Copyright(c) 2009, Bharti Telesoft Int.
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
 *                                 vipan Nov 17, 2010 Initial Creation
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -----
 *                                 This class is responsible to control the
 *                                 HeartBeat Thread.
 */

import java.util.HashMap;
import org.apache.log4j.Logger;
import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseException;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.util.BTSLUtil;

public class HuaweiHeartBeatController {
    static Logger _logger = Logger.getLogger(HuaweiHeartBeatController.class.getName());
    private HashMap<String, HuaweiHeartBeat> _heartBeatThreadMap = null;

    public HuaweiHeartBeatController(String p_poolIDs) {
        String[] inStrArray = null;
        String interfaceID = null;
        if (_logger.isDebugEnabled())
            _logger.debug("HuaweiHeartBeatController[constructor] Entered p_poolIDs:" + p_poolIDs);
        try {
            inStrArray = p_poolIDs.split(",");
            if (isNullArray(inStrArray))
                throw new BTSLBaseException(this, "HuaweiHeartBeatController[constructor]", InterfaceErrorCodesI.HEARTBEAT_ERROR_OBJECT_POOL_INIT);

            // Confirm while creating instances,if any errror occurs for an
            // interface,should we stop the process with handling the event and
            // throw exception
            // Or only event should be handled corresponding to that interface
            // and continue to other.
            _heartBeatThreadMap = new HashMap<String, HuaweiHeartBeat>(inStrArray.length);// initialize
                                                                                          // the
                                                                                          // Map
                                                                                          // size
                                                                                          // equal
                                                                                          // to
                                                                                          // number
                                                                                          // of
                                                                                          // the
                                                                                          // pool
                                                                                          // Ids.
            for (int i = 0, size = inStrArray.length; i < size; i++) {
                interfaceID = inStrArray[i];
                _heartBeatThreadMap.put(interfaceID.trim(), new HuaweiHeartBeat(interfaceID));

            }

        } catch (Exception e) {
            e.printStackTrace();
            _logger.error("HuaweiHeartBeatController[constructor] Exception e:" + e.getMessage());
        }
    }

    // Implement the logic to destroy the heart beat thread based on the INIDs
    // This method
    public void stopHeartBeat(String p_interfaceID) {
        if (_logger.isDebugEnabled())
            _logger.debug("stopHeartBeat Entered p_interfaceID:" + p_interfaceID);
        String[] inStrArray = null;
        String interfaceID = null;
        HuaweiHeartBeat huaweiHearBeat = null;
        try {

            inStrArray = p_interfaceID.split(",");
            if (isNullArray(inStrArray))
                throw new BaseException("HuaweiHeartBeatController[stopHeartBeat]" + InterfaceErrorCodesI.HEARTBEAT_ERROR_OBJECT_POOL_DESTROY);

            for (int i = 0, size = inStrArray.length; i < size; i++) {
                interfaceID = inStrArray[i].trim();
                huaweiHearBeat = (HuaweiHeartBeat) _heartBeatThreadMap.remove(interfaceID);
                HuaweiProps.logMessage("Stop the Heart BEAT" + huaweiHearBeat.toString());
                huaweiHearBeat.stopHearBeat();
                HuaweiProps.logMessage("Succefuly stop the Heart BEAT" + huaweiHearBeat.toString() + " _heartBeatThreadMap" + _heartBeatThreadMap.toString());

            }
        } catch (Exception e) {
            e.printStackTrace();// For testing.
        }

    }

    /**
     * This method will check the array for null.
     * If all the entries in array is null then return true otherwise return
     * false
     * 
     * @param p_arr
     * @return
     */
    public static boolean isNullArray(String[] p_arr) {
        if (_logger.isDebugEnabled())
            _logger.debug("isNullArray Entered p_arr: " + p_arr);
        boolean isNull = true;
        if (p_arr != null) {
            for (int i = 0, j = p_arr.length; i < j; i++) {
                if (!BTSLUtil.isNullString(p_arr[i])) {
                    isNull = false;
                    break;
                }
            }
        }
        if (_logger.isDebugEnabled())
            _logger.debug("isNullArray Exited isNull: " + isNull);
        return isNull;
    }
}
