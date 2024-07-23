package com.btsl.pretups.channel.logging;

/*
 * @(#)AutoC2CLogger.java.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 05/09/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Class for logging all the balance related Logs for channel user
 */

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.user.businesslogic.ChannelSoSVO;
import com.btsl.pretups.util.PretupsBL;

public class SOSAlertLogger {

    private static Log log = LogFactory.getFactory().getInstance(SOSAlertLogger.class.getName());

    /**
	 * ensures no instantiation
	 */
    private SOSAlertLogger(){
    	
    }
    
    public static void log(ChannelSoSVO channelSoSVO,long previousbal, long postbal) {
        final String METHODNAME = "log";
        StringBuilder loggerEventValue= new StringBuilder(); 
        final StringBuilder strBuff = new StringBuilder();
        try {
            strBuff.append(" [ UserID :" + channelSoSVO.getUserId()+ "]");
            strBuff.append(" [ MSISDN :" + channelSoSVO.getMsisdn()+ "]");
            strBuff.append(" [ SOS Allowed :" + channelSoSVO.getSosAllowed()+ "]");
            strBuff.append(" [ SOS Allowed Amount :" + PretupsBL.getDisplayAmount(channelSoSVO.getSosAllowedAmount())+ "]");
            strBuff.append(" [ SOS Threshold Amount :" + PretupsBL.getDisplayAmount(channelSoSVO.getSosThresholdLimit())+ "]");
            strBuff.append(" [ User Previous Balance :" + PretupsBL.getDisplayAmount(previousbal)+ "]");
            strBuff.append(" [ User Post Balance :" + PretupsBL.getDisplayAmount(postbal)+ "]");
            log.info("", strBuff.toString());
        } catch (Exception e) {
            log.errorTrace(METHODNAME, e);
            loggerEventValue.setLength(0);
            loggerEventValue.append(" Exception :");
            loggerEventValue.append(e.getMessage());
            log.error("log", channelSoSVO.getMsisdn(),  loggerEventValue);
            loggerEventValue.setLength(0);
            loggerEventValue.append("Not able to log info getting Exception=");
            loggerEventValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSAlertLogger[log]", channelSoSVO.getMsisdn(),
                "", "", loggerEventValue.toString() );
        }
    }
}
