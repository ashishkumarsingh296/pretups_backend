package com.btsl.pretups.logging;

/*
 * @(#)MessageSentLog.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 05/09/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Class for logging all the Messages that are sent from the system
 */

import java.util.Locale;

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class MessageSentLog {
    private static Log _log = LogFactory.getFactory().getInstance(MessageSentLog.class.getName());
    private MessageSentLog() {
		// TODO Auto-generated constructor stub
	}

    /**
     * Method to log the info in the file
     * 
     * @param p_msisdn
     * @param p_locale
     * @param p_gatewayType
     * @param p_gatewayCode
     * @param p_message
     * @param p_status
     * @param p_otherInfo
     */
    public static void log(String p_msisdn, Locale p_locale, String p_gatewayType, String p_gatewayCode, String p_message, String p_status, String p_url, String p_otherInfo) {
        final String METHOD_NAME = "log";
        try {
            StringBuilder strBuild = new StringBuilder();
            strBuild.append(" [MSISDN:").append(p_msisdn).append("]");
            strBuild.append(" [Response Gateway Code:").append(p_gatewayCode).append("]");
            strBuild.append(" [Response Gateway Type:").append(p_gatewayType).append("]");
            strBuild.append(" [Locale:").append(p_locale).append("]");
            strBuild.append(" [Status:").append(p_status).append("]");
            if (_log.isDebugEnabled()) {
                strBuild.append(" [Message:").append(p_message).append("]");
                strBuild.append(" [URL:").append(p_url).append("]");
            } else {
                strBuild.append(" [Message: Message has been sent to user ]");
            }

            strBuild.append(" [Other Info:").append(p_otherInfo).append("]");
            _log.info("", strBuild.toString());
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("log", "", " Not able to log info in Message Sent Log, getting Exception :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageSentLog[log]", "", p_msisdn, "", "Not able to log info in MessageSentLog for MSISDN:" + p_msisdn + " ,getting Exception=" + e.getMessage());
        }
    }
}
