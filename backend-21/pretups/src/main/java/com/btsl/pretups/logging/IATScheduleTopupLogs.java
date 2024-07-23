/**
 * @# IATScheduleTopupLogs.java
 * 
 *    Created By Created on History
 *    --------------------------------------------------------------------------
 *    ------
 *    Babu Kunwar 09-OCT-2011 Initial creation
 *    --------------------------------------------------------------------------
 *    ------
 *    Copyright(c) 2011 Comviva Technologies Ltd.
 */

package com.btsl.pretups.logging;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

public class IATScheduleTopupLogs {

    private static Log _Filelogger = LogFactory.getLog(IATScheduleTopupLogs.class.getName());

    private IATScheduleTopupLogs() {
        super();
    }

    /**
     * Method log is used for logging the log for the shedule topup
     * in case if IAT recharge.
     * 
     * @author babu.kunwar
     * @param p_action
     * @param p_userID
     * @param p_msisdn
     * @param p_batchID
     * @param p_message
     * @param p_result
     * @param p_otherInfo
     */
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
