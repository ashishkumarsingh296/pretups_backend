package com.selftopup.pretups.logging;

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

import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;

public class MessageSentLog {
    private static Log _log = LogFactory.getFactory().getInstance(MessageSentLog.class.getName());

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
        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append(" [MSISDN:" + p_msisdn + "]");
            strBuff.append(" [Response Gateway Code:" + p_gatewayCode + "]");
            strBuff.append(" [Response Gateway Type:" + p_gatewayType + "]");
            strBuff.append(" [Locale:" + p_locale + "]");
            strBuff.append(" [Status:" + p_status + "]");
            if (_log.isDebugEnabled()) {
                strBuff.append(" [Message:" + p_message + "]");
                strBuff.append(" [URL:" + p_url + "]");
            } else
                strBuff.append(" [Message: Message has been sent to user ]");

            strBuff.append(" [Other Info:" + p_otherInfo + "]");
            _log.info("", strBuff.toString());
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("log", "", " Not able to log info in Message Sent Log, getting Exception :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageSentLog[log]", "", p_msisdn, "", "Not able to log info in MessageSentLog for MSISDN:" + p_msisdn + " ,getting Exception=" + e.getMessage());
        }
    }
}
