/*
 * Created on Jul 6, 2009
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.btsl.pretups.logging;

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class IATInterfaceLog {

    private static Log _log = LogFactory.getFactory().getInstance(IATInterfaceLog.class.getName());

    /**
     * ensures no instantiation
     */
    private IATInterfaceLog(){
    	
    }
    
    /**
     * Method to log the info in IATInterfaceLog log
     * 
     * @param p_transferID
     * @param p_msisdn
     * @param p_network
     * @param p_time
     * @param p_reqType
     * @param p_transactionStage
     * @param p_message
     * @param p_status
     * @param p_otherInfo
     */
    public static void log(String p_action, String p_sercerZebraTxnID, String p_iatTxnID, String p_msisdn, String p_network, String p_reqType, String p_transactionStage, String p_message, String p_status, String p_otherInfo) {
        final String METHOD_NAME = "log";
        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append("[ACTION :" + p_action + "]");
            strBuff.append("[SENDER ZEBRA TID:" + p_sercerZebraTxnID + "]");
            strBuff.append("[IAT TID:" + p_iatTxnID + "]");
            strBuff.append("[MNO:" + p_msisdn + "]");
            strBuff.append("[NW:" + p_network + "]");
            strBuff.append("[TYPE:" + p_reqType + "]");
            strBuff.append("[STG:" + p_transactionStage + "]");
            strBuff.append("[MSG:" + p_message + "]");
            strBuff.append("[ST:" + p_status + "]");
            strBuff.append("[INFO:" + p_otherInfo + "]");

            _log.info("", strBuff.toString());
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("log", p_sercerZebraTxnID, " Not able to log info, getting Exception :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransactionLog[log]", p_sercerZebraTxnID, "", "", "Not able to log info for Transfer ID:" + p_sercerZebraTxnID + " ,getting Exception=" + e.getMessage());
        }
    }

}
