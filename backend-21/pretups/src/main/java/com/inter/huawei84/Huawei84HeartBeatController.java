package com.inter.huawei84;

/**
 * @Huawei84HeartBeatController.java
 *                                   Copyright(c) 2007, Bharti Telesoft Int.
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
 *                                   Vinay Kumar Singh December 26, 2007 Initial
 *                                   Creation
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   ---------
 *                                   This class is responsible to control the
 *                                   HeartBeat Thread.
 */

import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceUtil;

public class Huawei84HeartBeatController {
    private Log _log = LogFactory.getLog(Huawei84HeartBeatController.class.getName());
    private HashMap _heartBeatThreadMap = null;

    public Huawei84HeartBeatController(String p_poolIDs) {
        String[] inStrArray = null;
        String interfaceID = null;
        if (_log.isDebugEnabled())
            _log.debug("Huawei84HeartBeatController[constructor]", "Entered p_poolIDs:" + p_poolIDs);
        try {
            inStrArray = p_poolIDs.split(",");
            if (InterfaceUtil.isNullArray(inStrArray))
                throw new BTSLBaseException(this, "Huawei84HeartBeatController[constructor]", InterfaceErrorCodesI.HEARTBEAT_ERROR_OBJECT_POOL_INIT);

            // Confirm while creating instances,if any errror occurs for an
            // interface,should we stop the process with handling the event and
            // throw exception
            // Or only event should be handled corresponding to that interface
            // and continue to other.
            _heartBeatThreadMap = new HashMap(inStrArray.length);// initialize
                                                                 // the Map size
                                                                 // equal to
                                                                 // number of
                                                                 // the pool
                                                                 // Ids.
            for (int i = 0, size = inStrArray.length; i < size; i++) {
                interfaceID = inStrArray[i];
                _heartBeatThreadMap.put(interfaceID.trim(), new Huawei84HeartBeat(interfaceID));
            }
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("Huawei84HeartBeatController[constructor]", "Exception e:" + e.getMessage());
        }
    }

    // Implement the logic to destroy the heart beat thread based on the INIDs
    // This method
    public void stopHeartBeat(String p_interfaceID) {
        if (_log.isDebugEnabled())
            _log.debug("stopHeartBeat", "Entered p_interfaceID:" + p_interfaceID);
        String[] inStrArray = null;
        String interfaceID = null;
        Huawei84HeartBeat huaweiHearBeat = null;
        try {
            inStrArray = p_interfaceID.split(",");
            if (InterfaceUtil.isNullArray(inStrArray))
                throw new BTSLBaseException(this, "Huawei84HeartBeatController[stopHeartBeat]", InterfaceErrorCodesI.HEARTBEAT_ERROR_OBJECT_POOL_DESTROY);

            for (int i = 0, size = inStrArray.length; i < size; i++) {
                interfaceID = inStrArray[i].trim();
                if (_heartBeatThreadMap != null && _heartBeatThreadMap.containsKey(interfaceID)) {
                    huaweiHearBeat = (Huawei84HeartBeat) _heartBeatThreadMap.remove(interfaceID);
                    huaweiHearBeat.stopHearBeat();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();// For testing.
        }

    }
}
