/*
 * @# ProcessesLog.java
 * This class is the log file for pinPasswordAlert and lowBalanceAlert
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * FEB 24, 2006 Ankit Singhal Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.logging;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

/**
 * 
 */

public class ProcessesLog {
    /**
     * Field _Filelogger.
     */
    private static Log _Filelogger = LogFactory.getLog(ProcessesLog.class.getName());

    /**
     * Constructor for ProcessesLog.
     */
    private ProcessesLog() {
        
    }

    /**
     * Method log.
     * 
     * @param p_action
     *            String
     * @param p_msisdn
     *            String
     * @param p_message
     *            String
     * @param p_otherInfo
     *            StringBuffer
     */
    public static void log(String p_action, String p_msisdn, String p_message, StringBuffer p_otherInfo) {
        StringBuffer message = new StringBuffer();
        message.append("[ACTION = " + BTSLUtil.NullToString(p_action) + "] ");
        message.append("[MSISDN = " + BTSLUtil.NullToString(p_msisdn) + "] ");
        message.append("[MESSAGE = " + BTSLUtil.NullToString(p_message) + "] ");
        message.append("[OTHER INFO = " + p_otherInfo + "] ");
        _Filelogger.info(" ", message);
    }

    /**
     * Method balanceMismatchLog.
     * 
     * @param p_action
     *            String
     * @param p_mismatchBalanceStr
     *            String
     */
    public static void balanceMismatchLog(String p_action, String p_mismatchBalanceStr) {
        StringBuffer message = new StringBuffer();
        message.append("[ACTION = " + BTSLUtil.NullToString(p_action) + "] ");
        message.append("[MISMATCH STRING = " + BTSLUtil.NullToString(p_mismatchBalanceStr) + "] ");
        _Filelogger.info(" ", message);
    }
}
