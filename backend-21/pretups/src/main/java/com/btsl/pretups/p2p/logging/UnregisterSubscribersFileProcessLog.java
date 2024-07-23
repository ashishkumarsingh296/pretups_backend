/*
 * @# UnregisterSubscribersAction.java
 * This class is the log file for the routing process
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Nov 18, 2005 Amit Singh Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.p2p.logging;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

public class UnregisterSubscribersFileProcessLog {
    private static Log _Filelogger = LogFactory.getLog(UnregisterSubscribersFileProcessLog.class.getName());

    /**
     * ensures no instantiation
     */
    private UnregisterSubscribersFileProcessLog() {
        
    }

    public static void log(String p_action, String p_userID, String p_msisdn, long p_msisdnPosition, String p_message, String p_result, String p_otherInfo) {
        final StringBuffer message = new StringBuffer();
        message.append("[ACTION = " + BTSLUtil.NullToString(p_action) + "] ");
        message.append("[LOGGED IN USER'S ID = " + BTSLUtil.NullToString(p_userID) + "] ");
        message.append("[MSISDN = " + BTSLUtil.NullToString(p_msisdn) + "] ");
        message.append("[MSISDN POSITION = " + p_msisdnPosition + "] ");
        message.append("[MESSAGE = " + BTSLUtil.NullToString(p_message) + "] ");
        message.append("[RESULT = " + BTSLUtil.NullToString(p_result) + "] ");
        message.append("[OTHER INFO = " + BTSLUtil.NullToString(p_otherInfo) + "] ");
        _Filelogger.info(" ", message);
    }

    public static void main(String[] args) {
    }
}
