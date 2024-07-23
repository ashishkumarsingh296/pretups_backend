package com.btsl.ota.util;

/*
 * APDULogger.java
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

/**
 * @author rajat.mishra
 * 
 */
public class APDULogger implements Serializable {

	private static Log apdu_logger = LogFactory.getLog(APDULogger.class.getName());

    public static void logMessage(String logStr) {
        final String METHOD_NAME = "logMessage";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
            apdu_logger.info(" ", sdf.format(new Date()) + " " + logStr);
        } catch (Exception ex) {
            apdu_logger.errorTrace(METHOD_NAME, ex);
        }
    }

    public static void main(String[] args) {
    }
}
