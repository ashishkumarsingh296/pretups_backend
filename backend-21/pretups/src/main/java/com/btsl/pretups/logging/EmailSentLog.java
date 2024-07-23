package com.btsl.pretups.logging;

/**
 * @(#)EmailSentLog.java
 *                       Copyright(c) 2009, Bharti Telesoft Ltd.
 *                       All Rights Reserved
 * 
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       Author Date History
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       Santanu Mohanty 04 oct 2007 initial Creation
 *                       This class is used for writing log during email to the
 *                       respective user at the time of
 *                       modification/registration/deletion
 * 
 */

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.user.businesslogic.UserVO;

public class EmailSentLog {
    private static Log _log = LogFactory.getFactory().getInstance(EmailSentLog.class.getName());

    /**
     * ensures no instantiation
     */
    private EmailSentLog(){
    	
    }
    public static void log(String p_fromMailServer, String p_fromEmailID, String p_toEmailID, String p_message, UserVO p_channelUserVO, UserVO p_sessionUserVO, String p_otherInfo) {
        final String METHOD_NAME = "log";
        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append(" [From Mail Server:" + p_fromMailServer + "]");
            strBuff.append(" [From ID: " + p_fromEmailID + "]");
            strBuff.append(" [TO ID: " + p_toEmailID + "]");
            strBuff.append(" [Message:" + p_message + "]");
            if (p_channelUserVO != null) {
                strBuff.append(" [User type:" + p_channelUserVO.getUserType() + "]");
                strBuff.append(" [User name:" + p_channelUserVO.getUserName() + "]");
                strBuff.append(" [Mobile no. :" + p_channelUserVO.getMsisdn() + "]");
                if (p_sessionUserVO != null) {
                    strBuff.append(" [Modify By: " + p_sessionUserVO.getModifiedBy() + "]");
                }
                strBuff.append(" [Modify On:" + p_channelUserVO.getModifiedOn() + "]");
                if (p_sessionUserVO != null) {
                    strBuff.append(" [Modify By login id:" + p_sessionUserVO.getLoginID() + "]");
                }
                strBuff.append(" [Status:" + p_channelUserVO.getStatus() + "]");
                strBuff.append(" [User ID:" + p_channelUserVO.getUserID() + "]");
                strBuff.append(" [Login id:" + p_channelUserVO.getLoginID() + "]");

            }
            strBuff.append(" [Other Info:" + p_otherInfo + "]");
            _log.info("", strBuff.toString());
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("log", "", " Not able to log info in Email Sent Log, getting Exception :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EmailSentLog[log]", "", "", "", "Not able to log info in EmailSentLog for user:" + p_channelUserVO.getUserName() + " ,getting Exception=" + e.getMessage());
        }
    }
}
