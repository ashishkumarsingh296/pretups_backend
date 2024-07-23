package com.btsl.pretups.logging;

/*
 * @(#)SDTransactionLog.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Dhiraj Tiwari 21/11/2009 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2009 Bharti Telesoft Ltd.
 * Class for logging all the sd transaction log
 */

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class SDTransactionLog {
    private static Log _log = LogFactory.getFactory().getInstance(SDTransactionLog.class.getName());

    /**
   	 * ensures no instantiation
   	 */
    private SDTransactionLog(){
    	
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
    public static void log(String p_transferID, String p_referenceID, String p_msisdn, String p_network, String p_reqType, String p_transactionStage, String p_message, String p_resp, String p_status, String p_ttsd, String p_otherInfo) {
        final String METHOD_NAME = "log";
        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append("[TID:" + p_transferID + "]");
            strBuff.append("[REFID:" + p_referenceID + "]");
            strBuff.append("[MNO:" + p_msisdn + "]");
            strBuff.append("[NW:" + p_network + "]");
            strBuff.append("[TYPE:" + p_reqType + "]");
            strBuff.append("[STG:" + p_transactionStage + "]");
            strBuff.append("[REQ:" + p_message + "]");
            strBuff.append("[RES:" + p_resp + "]");
            strBuff.append("[ST:" + p_status + "]");
            strBuff.append("[TTSD:" + p_ttsd + "]");
            strBuff.append("[INFO:" + p_otherInfo + "]");
            _log.info("", strBuff.toString());
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("log", p_transferID, " Not able to log info, getting Exception :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransactionLog[log]", p_transferID, "", "", "Not able to log info for Transfer ID:" + p_transferID + " ,getting Exception=" + e.getMessage());
        }
    }
}
