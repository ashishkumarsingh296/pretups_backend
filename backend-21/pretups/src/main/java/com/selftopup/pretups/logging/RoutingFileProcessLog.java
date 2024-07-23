/*
 * @# RoutingDAO.java
 * This class is the log file for the routing process
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Oct 27, 2005 Sandeep goel Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.selftopup.pretups.logging;

import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.util.BTSLUtil;

public class RoutingFileProcessLog {
    private static Log _Filelogger = LogFactory.getLog(RoutingFileProcessLog.class.getName());

    /**
	 * 
	 */
    public RoutingFileProcessLog() {
        super();
    }

    public static void log(String p_action, String p_userID, String p_msisdn, long p_totRecords, String p_message, String p_result, String p_otherInfo) {
        StringBuffer message = new StringBuffer();
        message.append("[ACTION = " + BTSLUtil.NullToString(p_action) + "] ");
        message.append("[USER ID = " + BTSLUtil.NullToString(p_userID) + "] ");
        message.append("[MSISDN = " + BTSLUtil.NullToString(p_msisdn) + "] ");
        message.append("[RECORD NO. = " + p_totRecords + "] ");
        message.append("[RESULT = " + BTSLUtil.NullToString(p_result) + "] ");
        message.append("[MESSAGE = " + BTSLUtil.NullToString(p_message) + "] ");
        message.append("[OTHER INFO = " + BTSLUtil.NullToString(p_otherInfo) + "] ");
        _Filelogger.info(" ", message);
    }

    public static void log(String p_module, String p_msisdn, String p_userTypeDesc, String p_serviceType, String p_serviceClassCode, String p_status, String p_message, String p_result, String p_otherInfo) {
        StringBuffer message = new StringBuffer();
        message.append("[MODULE = " + BTSLUtil.NullToString(p_module) + "] ");
        message.append("[MSISDN = " + BTSLUtil.NullToString(p_msisdn) + "] ");
        message.append("[USER TYPE = " + BTSLUtil.NullToString(p_userTypeDesc) + "] ");
        message.append("[SERVICE TYPE = " + BTSLUtil.NullToString(p_serviceType) + "] ");
        message.append("[SERVICE CLASS ID = " + BTSLUtil.NullToString(p_serviceClassCode) + "] ");
        message.append("[STATUS = " + BTSLUtil.NullToString(p_status) + "] ");
        message.append("[RESULT = " + BTSLUtil.NullToString(p_result) + "] ");
        message.append("[MESSAGE = " + BTSLUtil.NullToString(p_message) + "] ");
        message.append("[OTHER INFO - NetworkID = " + BTSLUtil.NullToString(p_otherInfo) + "] ");
        _Filelogger.info(" ", message);
    }

}
