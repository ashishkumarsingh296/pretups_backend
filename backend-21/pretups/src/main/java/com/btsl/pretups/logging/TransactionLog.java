package com.btsl.pretups.logging;

/*
 * @(#)TransactionLog.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 05/09/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Class for logging all the transaction log
 */

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;

public class TransactionLog {
    private static final Log LOG = LogFactory.getFactory().getInstance(TransactionLog.class.getName());

    /**
     * ensures no instantiation
     */
    private TransactionLog(){
    	
    }
    
    /**
     * 
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
        final String METHOD_NAME = "log";
        try {
        	StringBuilder strBuff = new StringBuilder();
            strBuff.append("[TID:").append( p_transferID).append( "]");
            if (PretupsI.TXN_LOG_REQTYPE_RES.equals(p_reqType) && (PretupsI.TXN_LOG_TXNSTAGE_INVAL.equals(p_transactionStage) && PretupsI.TXN_LOG_TXNSTAGE_INTOP.equals(p_transactionStage))) {
                strBuff.append("[TTIN:").append( p_otherInfo).append( "]");
            }
            strBuff.append("[REFID:").append( p_referenceID).append( "]");
            strBuff.append("[MNO:").append( p_msisdn).append( "]");
            strBuff.append("[NW:").append( p_network).append( "]");
            strBuff.append("[TYPE:").append( p_reqType).append( "]");
            strBuff.append("[STG:").append( p_transactionStage).append( "]");
            strBuff.append("[MSG:").append( p_message).append( "]");
            strBuff.append("[ST:").append( p_status).append( "]");
            strBuff.append("[INFO:").append( p_otherInfo).append( "]");
            LOG.info("", strBuff.toString());
        } catch (Exception e) {
            LOG.errorTrace(METHOD_NAME, e);
            LOG.error("log", p_transferID, " Not able to log info, getting Exception :" + e.getMessage());
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"TransactionLog[log]",p_transferID,"","","Not able to log info for Transfer ID:"+p_transferID+" ,getting Exception="+e.getMessage());
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
        final String METHOD_NAME = "log";
        try {
            StringBuilder strBuild = new StringBuilder();
            strBuild.append("[TID:").append(p_transferID).append("]");
            strBuild.append("[REFID:").append(p_referenceID).append("]");
            strBuild.append("[MNO:").append(p_msisdn).append("]");
            strBuild.append("[NW:").append(p_network).append("]");
            strBuild.append("[STG:").append(p_transactionStage).append("]");
            strBuild.append("[TTIN:").append(p_ttlTimeTakn).append("]");
            strBuild.append("[INSTRTIME:").append(p_inStartTime).append("]");
            strBuild.append("[INENDTIME:").append(p_inEndTime).append("]");
            LOG.info("", strBuild.toString());
        } catch (Exception e) {
            LOG.errorTrace(METHOD_NAME, e);
            LOG.error("log", p_transferID, " Not able to log info, getting Exception :" + e.getMessage());
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"TransactionLog[log]",p_transferID,"","","Not able to log info for Transfer ID:"+p_transferID+" ,getting Exception="+e.getMessage());
        }
    }

    public static Log getLogger() {
        return LOG;
    }
}
