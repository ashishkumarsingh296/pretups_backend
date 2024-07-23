package com.btsl.pretups.logging;

/**
 * @(#)AmbiguousStatusLog.java
 *                             Copyright(c) 2009, Comviva Technologies Ltd.
 *                             All Rights Reserved
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Author Date History
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Vinay Kumar Singh 26/11/2009 Initial Creation
 *                             ------------------------------------------------
 *                             ------------------------------------------------
 */
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class AmbiguousStatusLog {
    private static Log _log = LogFactory.getFactory().getInstance(AmbiguousStatusLog.class.getName());

    /**
     * ensures no instantiation
     */
    private AmbiguousStatusLog(){
    	
    }
    
    /**
     * @param String
     *            p_transferID
     * @param String
     *            p_reconID
     * @param String
     *            p_msisdn
     * @param String
     *            p_transactionStage
     * @param String
     *            p_otherInfo
     */
    public static void log(String p_transferID, String p_reconID, String p_retryCount, String p_msisdn, String p_transactionStage, String p_otherInfo) {
        final String METHOD_NAME = "log";
        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append("[TID:" + p_transferID + "]");
            strBuff.append("[RECONID:" + p_reconID + "]");
            strBuff.append("[RETRYCOUNT:" + p_retryCount + "]");
            strBuff.append("[MNO:" + p_msisdn + "]");
            strBuff.append("[STG:" + p_transactionStage + "]");
            strBuff.append("[OTHERINFO:" + p_otherInfo + "]");
            _log.info("", strBuff.toString());
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("log", p_reconID, " Not able to log info, getting Exception :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AmbiguousStatusLog[log]", p_reconID, "", "", "Not able to log info for Transfer ID:" + p_reconID + " ,getting Exception=" + e.getMessage());
        }
    }
}
