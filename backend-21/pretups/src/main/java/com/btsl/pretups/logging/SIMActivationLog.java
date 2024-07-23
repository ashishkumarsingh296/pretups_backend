package com.btsl.pretups.logging;

/*
 * @(#)SIMActivationLog.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Sachin Sharma 28/09/2011 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2009 Comviva Technologies Ltd.
 * Class for logging all the Sim Activation log
 */

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class SIMActivationLog {
    private static Log _log = LogFactory.getFactory().getInstance(SIMActivationLog.class.getName());

    /**
   	 * ensures no instantiation
   	 */
    private SIMActivationLog(){
    	
    }
    /**
     * Method to log the info in transaction log
     * 
     * @param p_transferID
     * @param p_referenceID
     * @param p_msisdn
     * @param p_network
     * @param p_time
     * @param p_reqType
     * @param p_transactionStage
     * @param p_message
     * @param p_status
     * @param p_otherInfo
     */
    public static void log(String p_txnID, String p_messagePOS, String p_message, String p_otherInfo) {
        final String METHOD_NAME = "log";
        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append("[TID:" + p_txnID + "]");
            strBuff.append("[POS:" + p_messagePOS + "]");
            strBuff.append("[MSG:" + p_message + "]");
            strBuff.append("[INFO:" + p_otherInfo + "]");
            _log.info("", strBuff.toString());
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("log", p_txnID, " Not able to log info, getting Exception :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SIMActivation[log]", p_txnID, "", "", "Not able to log info for Transfer ID:" + p_txnID + " ,getting Exception=" + e.getMessage());
        }
    }

    public static Log getLogger() {
        return _log;
    }
}
