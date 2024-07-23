package com.btsl.pretups.channel.logging;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

/**
 * @# WebRechargeLogger.java
 * 
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * --
 * Sandeep Goel Feb 22, 2006 Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
public class WebRechargeLogger {

    private static Log _log = LogFactory.getLog(WebRechargeLogger.class.getName());

    /**
     * ensures no instantiation
     */
    private WebRechargeLogger() {
        
    }

    public static void log(String p_logMessage) {
        if (p_logMessage.indexOf("pretups") != -1) {
            final String l1 = p_logMessage.substring(0, p_logMessage.indexOf("pretups"));
            final String l2 = p_logMessage.substring(p_logMessage.indexOf("pretups") + 7, p_logMessage.indexOf("[SENDER"));
            final String l3 = p_logMessage.substring(p_logMessage.indexOf("[SENDER"));
            if (_log.isDebugEnabled()) {
                _log.debug(" ", p_logMessage);
            }
            if (_log.isErrorEnabled()) {
                _log.error(" ", l1 + "]" + l3);
            }
        }
    }
}
