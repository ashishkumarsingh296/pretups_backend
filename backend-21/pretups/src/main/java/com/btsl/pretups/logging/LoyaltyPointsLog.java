package com.btsl.pretups.logging;

/*
 * @(#)LoyaltyPointsLog.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Vibhu Trehan 27/02/2014 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Class for logging all the Messages that are sent from the system
 */

import java.util.Date;

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;

public class LoyaltyPointsLog {
    private static Log _log = LogFactory.getFactory().getInstance(LoyaltyPointsLog.class.getName());

    /**
     * ensures no instantiation
     */
    private LoyaltyPointsLog(){
    	
    }
    /**
     * Method to log the info in the file
     * 
     * @param p_profileType
     * @param p_userID
     * @param p_pointDate
     * @param p_entryType
     * @param p_txnID
     * @param p_txnType
     * @param p_serviceType
     * @param p_points
     */
    public static void log(String p_profileType, String p_userID, String p_setID, Date p_pointDate, String p_entryType, String p_txnID, String p_txnType, String p_serviceType, long p_points, long p_target, long p_sumAmount, String p_txnStatus, String p_errorCode) {
        final String METHOD_NAME = "log";
        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append(" [PrfT:" + p_profileType + "]");
            strBuff.append(" [UId:" + p_userID + "]");
            strBuff.append(" [SId:" + p_setID + "]");
            // strBuff.append(" [Ver:"+p_version +"]");
            strBuff.append(" [Dt:" + p_pointDate + "]");
            strBuff.append(" [ET:" + p_entryType + "]");
            strBuff.append(" [TxnT:" + p_txnType + "]");
            strBuff.append(" [ST:" + p_serviceType + "]");
            if (p_profileType.equals(PretupsI.PROFILE_TRANS)) {
                strBuff.append(" [TxnID:" + p_txnID + "]");
            } else {
                strBuff.append(" [TgtAmt:" + p_target + "]");
                strBuff.append(" [SumAmt:" + p_sumAmount + "]");
            }

            strBuff.append(" [Pt:" + p_points + "]");

            /*
             * if(_log.isDebugEnabled())
             * {
             * strBuff.append(" [Message:"+p_message +"]");
             * strBuff.append(" [URL:"+p_url +"]");
             * }
             * else
             * strBuff.append(" [Message: Message has been sent to user ]");
             */

            strBuff.append(" [TS:" + p_txnStatus + "]");
            _log.info("", strBuff.toString());
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("log", "", " Not able to log info in LoyaltyPointsLog, getting Exception :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LMSTransactionLog[log]", "", p_userID, "", "Not able to log info in MessageSentLog for UserID:" + p_userID + " ,getting Exception=" + e.getMessage());
        }
    }

}
