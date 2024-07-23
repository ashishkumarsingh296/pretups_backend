package com.btsl.pretups.logging;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

/**
 * @# SMSChargingLog.java
 * 
 *    Created by Created on History
 *    --------------------------------------------------------------------------
 *    ------
 *    Ankit Zindal 17/07/06 Initial Creation
 *    --------------------------------------------------------------------------
 *    ------
 *    Copyright(c) 2006 Bharti Telesoft Ltd.
 */
public class SMSChargingLog {
    private static Log _Filelogger = LogFactory.getLog(SMSChargingLog.class.getName());

    /**
   	 * ensures no instantiation
   	 */
    private SMSChargingLog() {
        
    }

    /**
     * log
     * 
     * @param p_userID
     * @param p_currentCount
     * @param p_profileCounts
     * @param p_requestCode
     * @param p_responseCode
     * @param p_networkCode
     * @param p_groupType
     * @param p_serviceType
     * @param p_module
     */
    public static void log(String p_userID, long p_currentCount, long p_profileCounts, String p_requestCode, String p_responseCode, String p_networkCode, String p_groupType, String p_serviceType, String p_module) {
        StringBuffer message = new StringBuffer();
        message.append("[USER ID = " + BTSLUtil.NullToString(p_userID) + "] ");
        message.append("[CURRENT COUNT = " + p_currentCount + "] ");
        message.append("[PROFILE COUNT = " + p_profileCounts + "] ");
        message.append("[REQUEST GATEWAY TYPE = " + BTSLUtil.NullToString(p_requestCode) + "] ");
        message.append("[RESPONSE GATEWAY TYPE = " + BTSLUtil.NullToString(p_responseCode) + "] ");
        message.append("[NETWORK CODE = " + BTSLUtil.NullToString(p_networkCode) + "] ");
        message.append("[GROUP TYPE = " + BTSLUtil.NullToString(p_groupType) + "] ");
        message.append("[SERVICE TYPE = " + BTSLUtil.NullToString(p_serviceType) + "] ");
        message.append("[MODULE = " + BTSLUtil.NullToString(p_module) + "] ");
        _Filelogger.info(" ", message);
    }

}
