package com.inter.mobinilvoms;

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class AmbiFailStatusLog {

    private static Log _log = LogFactory.getFactory().getInstance(AmbiFailStatusLog.class.getName());

    /**
     * @param String
     *            p_transactionID
     * @param String
     *            p_serialID
     * @param String
     *            p_retryCount
     * @param String
     *            p_transactionStage
     * @param String
     *            p_otherInfo
     */
    public static void log(String p_transactionID, String p_serialID, String p_retryCount, String p_action, String p_responseCode, String p_otherInfo) {
        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append("[TID=" + p_transactionID + "]");
            strBuff.append("[SERIAL_NUMBER=" + p_serialID + "]");
            strBuff.append("[RETRYCOUNT=" + p_retryCount + "]");
            strBuff.append("[ACTION=" + p_action + "]");
            strBuff.append("[RESPONSE_CODE=" + p_responseCode + "]");
            strBuff.append("[OTHERINFO=" + p_otherInfo + "]");
            _log.info("", strBuff.toString());
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("log", p_transactionID, " Not able to log info, getting Exception :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AmbiguousStatusLog[log]", p_transactionID, "", "", "Not able to log info for Transfer ID:" + p_transactionID + " ,getting Exception=" + e.getMessage());
        }
    }

}
