/*
 * @# DatabasePurgingLog.java
 * This class is the log file for archiving and purging.
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

public class DatabasePurgingLog {
    private static Log _Filelogger = LogFactory.getLog(DatabasePurgingLog.class.getName());

    /**
	 * 
	 */
    private DatabasePurgingLog() {
        super();
    }

    public static void archiveLog(String p_process, String p_directoyPath, String p_directoryName, String fileName, String tableName1, String p_networkName, String p_action) {
        StringBuffer message = new StringBuffer();

        message.append("[PROCESS = " + BTSLUtil.NullToString(p_process) + "] ");
        message.append("[DIRECTORY PATH = " + BTSLUtil.NullToString(p_directoyPath) + "] ");
        message.append("[DIRECTORY NAME = " + BTSLUtil.NullToString(p_directoryName) + "] ");
        message.append("[FILE NAME = " + BTSLUtil.NullToString(fileName) + "] ");
        message.append("[TABLE NAME = " + BTSLUtil.NullToString(tableName1) + "] ");
        message.append("[NETWORK NAME = " + BTSLUtil.NullToString(p_networkName) + "] ");
        message.append("[ACTION = " + BTSLUtil.NullToString(p_action) + "] ");
        _Filelogger.info(" ", message);
    }

    public static void purgeLog(String p_process, String p_action) {
        StringBuffer message = new StringBuffer();

        message.append("[PROCESS = " + BTSLUtil.NullToString(p_process) + "] ");
        message.append("[ACTION = " + BTSLUtil.NullToString(p_action) + "] ");
        _Filelogger.info(" ", message);
    }

}
