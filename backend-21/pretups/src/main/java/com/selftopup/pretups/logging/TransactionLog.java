package com.selftopup.pretups.logging;

/*
 * @(#)TransactionLog.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 05/09/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Class for logging all the transaction log
 */

import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.util.BTSLUtil;

public class TransactionLog {
    private static Log _log = LogFactory.getFactory().getInstance(TransactionLog.class.getName());

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
    public static void log(String p_transferID, String p_referenceID, String p_msisdn, String p_network, String p_reqType, String p_transactionStage, String p_message, String p_status, String p_otherInfo) {
        try {
            StringBuffer strBuff = new StringBuffer();
            int index = -1;
            int index2 = -1;
            strBuff.append("[TID:" + p_transferID + "]");
            if (PretupsI.TXN_LOG_REQTYPE_RES.equals(p_reqType) && (PretupsI.TXN_LOG_TXNSTAGE_INVAL.equals(p_transactionStage) && PretupsI.TXN_LOG_TXNSTAGE_INTOP.equals(p_transactionStage)))
                strBuff.append("[TTIN:" + p_otherInfo + "]");
            strBuff.append("[REFID:" + p_referenceID + "]");
            strBuff.append("[MNO:" + p_msisdn + "]");
            strBuff.append("[NW:" + p_network + "]");
            strBuff.append("[TYPE:" + p_reqType + "]");
            strBuff.append("[STG:" + p_transactionStage + "]");

            if (!BTSLUtil.isNullString(p_message)) {
                index = p_message.indexOf("<cardNumber>");
                index2 = p_message.indexOf("CARD_NUMBER=");
            }
            if (index != -1) {
                strBuff.append("[MSG:" + p_message.substring(0, index + "<cardNumber>".length()) + "************" + p_message.substring(index + "<cardNumber>".length() + 12) + "]");
            } else if (index2 != -1) {
                strBuff.append("[MSG:" + p_message.substring(0, index + "CARD_NUMBER=".length()) + "************" + p_message.substring(index + "CARD_NUMBER=".length() + 12) + "]");
            } else {
                strBuff.append("[MSG:" + p_message + "]");
            }
            strBuff.append("[ST:" + p_status + "]");
            strBuff.append("[INFO:" + p_otherInfo + "]");
            _log.info("", strBuff.toString());
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("log", p_transferID, " Not able to log info, getting Exception :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransactionLog[log]", p_transferID, "", "", "Not able to log info for Transfer ID:" + p_transferID + " ,getting Exception=" + e.getMessage());
        }
    }

    /**
     * 
     * @param p_transferID
     * @param p_referenceID
     * @param p_msisdn
     * @param p_network
     * @param p_reqType
     * @param p_transactionStage
     * @param p_ttlTimeTakn
     * @param p_inStartTime
     * @param p_inEndTime
     * @param p_status
     * @param p_otherInfo
     */
    public static void log(String p_transferID, String p_referenceID, String p_msisdn, String p_network, String p_transactionStage, String p_ttlTimeTakn, String p_inStartTime, String p_inEndTime) {
        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append("[TID:" + p_transferID + "]");
            strBuff.append("[REFID:" + p_referenceID + "]");
            strBuff.append("[MNO:" + p_msisdn + "]");
            strBuff.append("[NW:" + p_network + "]");
            strBuff.append("[STG:" + p_transactionStage + "]");
            strBuff.append("[TTIN:" + p_ttlTimeTakn + "]");
            strBuff.append("[INSTRTIME:" + p_inStartTime + "]");
            strBuff.append("[INENDTIME:" + p_inEndTime + "]");
            _log.info("", strBuff.toString());
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("log", p_transferID, " Not able to log info, getting Exception :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransactionLog[log]", p_transferID, "", "", "Not able to log info for Transfer ID:" + p_transferID + " ,getting Exception=" + e.getMessage());
        }
    }

    public static Log getLogger() {
        return _log;
    }
}
