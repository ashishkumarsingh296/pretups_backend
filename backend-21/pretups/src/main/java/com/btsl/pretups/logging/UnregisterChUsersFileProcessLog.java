/*
 * @# UnregisterChUsersFileProcessLog.java
 * 
 * 
 * Created on Created by History
 * -------------------------------------------------------------------------
 * Nov 25, 2005 Amit Singh Initial creation
 * -------------------------------------------------------------------------
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */

package com.btsl.pretups.logging;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

public class UnregisterChUsersFileProcessLog {
    private static Log _Filelogger = LogFactory.getLog(UnregisterChUsersFileProcessLog.class.getName());

    /**
	 * ensures no instantiation
	 */
    private UnregisterChUsersFileProcessLog() {
        
    }

    public static void log(String p_action, String p_userID, String p_msisdn, long p_mobileCount, String p_message, String p_result, String p_otherInfo) {
        StringBuffer message = new StringBuffer();
        message.append("[ACTION = " + BTSLUtil.NullToString(p_action) + "] ");
        message.append("[LOGGED IN USER'S ID = " + BTSLUtil.NullToString(p_userID) + "] ");
        message.append("[PROCESSED STRING = " + BTSLUtil.NullToString(p_msisdn) + "] ");
        message.append("[PROCESSED STRING POSITION = " + p_mobileCount + "] ");
        message.append("[MESSAGE = " + BTSLUtil.NullToString(p_message) + "] ");
        message.append("[RESULT = " + BTSLUtil.NullToString(p_result) + "] ");
        message.append("[OTHER INFO = " + BTSLUtil.NullToString(p_otherInfo) + "] ");
        _Filelogger.info(" ", message);
    }

    public static void main(String[] args) {
    }

}
