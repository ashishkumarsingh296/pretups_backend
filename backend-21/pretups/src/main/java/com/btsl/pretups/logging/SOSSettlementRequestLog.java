/**
 * 
 */
package com.btsl.pretups.logging;

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.sos.businesslogic.SOSVO;

/**
 * @author shamit.jain
 * 
 */
public class SOSSettlementRequestLog {

    private static Log _log = LogFactory.getFactory().getInstance(SOSSettlementRequestLog.class.getName());
    
    /**
   	 * ensures no instantiation
   	 */
    private SOSSettlementRequestLog(){
    	
    }
    
    /**
     * Method log.
     * 
     * @param p_action
     *            String
     * @param p_msisdn
     *            String
     * @param p_message
     *            String
     * @param p_otherInfo
     *            StringBuffer
     */
    public static void log(String p_classname, String p_methodname, String p_info) {
        _log.info(p_classname, "[" + p_methodname + "]" + p_info);
    }

    /**
     * Used to log the information.
     * 
     * @param p_requestDailyLogVO
     */
    public static void log(String info, SOSVO p_sosLogVO) {
        final String METHOD_NAME = "log";
        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append("[" + info + "]");
            strBuff.append("[TransactionID:" + p_sosLogVO.getTransactionID() + "]");
            strBuff.append("[MSISDN:" + p_sosLogVO.getSubscriberMSISDN() + "]");
            strBuff.append("[NetworkCode:" + p_sosLogVO.getNetworkCode() + "]");
            strBuff.append("[TransactionStatus:" + p_sosLogVO.getTransactionStatus() + "]");
            strBuff.append("[ErrorCode:" + p_sosLogVO.getErrorCode() + "]");
            strBuff.append("[Amount:" + p_sosLogVO.getDebitAmount() + "]");
            strBuff.append("[InterfaceResponseCode:" + p_sosLogVO.getInterfaceResponseCode() + "]");
            strBuff.append("[TransferStatus:" + p_sosLogVO.getTransferStatus() + "]");
            _log.info("", strBuff.toString());
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("log", p_sosLogVO.getInstanceID() + "" + p_sosLogVO.getTransactionID(), " Not able to log info, getting Exception :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransactionLog[log]", p_sosLogVO.getInstanceID() + "-" + p_sosLogVO.getTransactionID(), "", "", "Not able to log info for Transaction ID - Request ID: ,getting Exception=" + e.getMessage());
        }
    }

}
