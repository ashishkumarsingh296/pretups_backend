package com.btsl.pretups.logging;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

/**
 * @# ScheduleFileProcessLog.java
 * 
 *    Created by Created on History
 *    --------------------------------------------------------------------------
 *    ------
 *    Sandeep Goel Apr 5, 2006 Initial creation
 *    --------------------------------------------------------------------------
 *    ------
 *    Copyright(c) 2005 Bharti Telesoft Ltd.
 */
public class ScheduleFileProcessLog {
    private static Log _Filelogger = LogFactory.getLog(ScheduleFileProcessLog.class.getName());

    /**
     * ensures no instantiation
     */
    private ScheduleFileProcessLog() {
        
    }

    public static void log(String p_action, String p_userID, String p_msisdn, String p_batchID, String p_message, String p_result, String p_otherInfo) {
        StringBuffer message = new StringBuffer();
        message.append("[ACTION = " + BTSLUtil.NullToString(p_action) + "] ");
        message.append("[BATCH ID = " + BTSLUtil.NullToString(p_batchID) + "] ");
        message.append("[USER ID = " + BTSLUtil.NullToString(p_userID) + "] ");
        message.append("[MSISDN = " + BTSLUtil.NullToString(p_msisdn) + "] ");
        message.append("[RESULT = " + BTSLUtil.NullToString(p_result) + "] ");
        message.append("[MESSAGE = " + BTSLUtil.NullToString(p_message) + "] ");
        message.append("[OTHER INFO = " + BTSLUtil.NullToString(p_otherInfo) + "] ");
        _Filelogger.info(" ", message);
    }

}
