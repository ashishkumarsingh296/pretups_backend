/*
 * @# BarFileProccesingLog.java
 * This class is the log file for the routing process
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Nov 17, 2005 Ankit Zindal Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.logging;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

public class BarFileProccesingLog {
    private static Log _Filelogger = LogFactory.getLog(BarFileProccesingLog.class.getName());

    /**
	 * 
	 */
    private BarFileProccesingLog() {
        super();
    }

    public static void log(String p_action, String p_userID, String p_msisdn, long p_totRecords, String p_message, String p_result, String p_otherInfo) {
        StringBuffer message = new StringBuffer();
        message.append("[ACTION = " + BTSLUtil.NullToString(p_action) + "] ");
        message.append("[USER ID = " + BTSLUtil.NullToString(p_userID) + "] ");
        message.append("[MSISDN = " + BTSLUtil.NullToString(p_msisdn) + "] ");
        message.append("[RECORD NO. = " + p_totRecords + "] ");
        message.append("[RESULT = " + BTSLUtil.NullToString(p_message) + "] ");
        message.append("[MESSAGE = " + BTSLUtil.NullToString(p_result) + "] ");
        message.append("[OTHER INFO = " + BTSLUtil.NullToString(p_otherInfo) + "] ");
        _Filelogger.info(" ", message);
    }

    public static void main(String[] args) {
    }
}
