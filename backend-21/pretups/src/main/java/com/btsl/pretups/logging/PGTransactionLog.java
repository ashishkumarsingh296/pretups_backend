package com.btsl.pretups.logging;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;

public class PGTransactionLog {
    private static final Log LOG = LogFactory.getFactory().getInstance(PGTransactionLog.class.getName());
    private PGTransactionLog(){
    	
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
    public static void log(String p_transferID, String p_referenceID, String p_msisdn, String p_network, String p_reqType, String p_transactionStage, String p_message1, String p_message2, String p_status, String resonseData) {
        final String METHOD_NAME = "log";
        try {
        	StringBuilder strBuff = new StringBuilder();
            strBuff.append("[TID:").append( p_transferID).append( "]");
            strBuff.append("[REFID:").append( p_referenceID).append( "]");
            strBuff.append("[MNO:").append( p_msisdn).append( "]");
            strBuff.append("[NW:").append( p_network).append( "]");
            strBuff.append("[TYPE:").append( p_reqType).append( "]");
            strBuff.append("[STG:").append( p_transactionStage).append( "]");
            if(p_reqType.equals(PretupsI.TXN_LOG_REQTYPE_REQ)) {
            	strBuff.append("[PGURL:").append( p_message1).append( "]");
            	strBuff.append("[CALLBACKURL:").append( p_message2).append( "]");
            } else if(p_reqType.equals(PretupsI.TXN_LOG_REQTYPE_RES)) {
            	strBuff.append("[DATA:").append( p_message1).append( "]");
            }
            strBuff.append("[ST:").append( p_status).append( "]");
            strBuff.append("[INFO:").append( resonseData).append( "]");
            LOG.info("", strBuff.toString());
        } catch (Exception e) {
            LOG.errorTrace(METHOD_NAME, e);
            LOG.error("log", p_transferID, " Not able to log info, getting Exception :" + e.getMessage());
        }
    }

    public static Log getLogger() {
        return LOG;
    }
}
