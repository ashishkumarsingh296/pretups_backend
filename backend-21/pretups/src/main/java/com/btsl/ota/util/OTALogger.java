package com.btsl.ota.util;

/*
 * OTALogger.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Amit Ruwali 10/08/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 */
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class OTALogger implements Serializable {
	private static Log ota_logger = LogFactory.getLog(OTALogger.class.getName());

    /**
     * This method logs the messages in the specified file as Class level
     * loggers are maintained in LogConfig file
     * logStr String (string message to be printed in the logger)
     */
    public static void logMessage(String logStr) {
        final String METHOD_NAME = "logMessage";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
            ota_logger.info(" ", sdf.format(new Date()) + " " + logStr);
        } catch (Exception ex) {
            ota_logger.errorTrace(METHOD_NAME, ex);
            ota_logger.info(METHOD_NAME,ex.toString());
        }
    }// end logMessage()
} // end of the class
