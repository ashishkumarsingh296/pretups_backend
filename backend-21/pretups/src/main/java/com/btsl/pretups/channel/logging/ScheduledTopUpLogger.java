/*
 * Created on Apr 1, 2006
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.btsl.pretups.channel.logging;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

/**
 * SchedulerListValueVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Ashish Kumar 01/04/2006 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Class used to create the log file when the request is in process.
 */
public class ScheduledTopUpLogger {
    private static Log _log = LogFactory.getLog(ScheduledTopUpLogger.class.getName());

    private ScheduledTopUpLogger() {
        super();
    }

    public static void log(String p_logMessage) {
        _log.info(" ", p_logMessage);
    }

}
