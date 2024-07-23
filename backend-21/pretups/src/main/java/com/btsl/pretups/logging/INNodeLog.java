package com.btsl.pretups.logging;

/*
 * @(#)INNodeLog.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Dhiraj 09/02/2010 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2010 Bharti Telesoft Ltd.
 * Class for logging all the IN Node log
 */

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class INNodeLog {
    private static final Log _log = LogFactory.getFactory().getInstance(INNodeLog.class.getName());

    /**
     * ensures no instantiation
     */
    private INNodeLog(){
    	
    }
    
    
    /**
     * Method to log the info in transaction log
     * 
     * @param p_referenceID
     * @param p_inTxnId
     * @param p_nodeUrl
     * @param nodeStatus
     * @param p_msisdn
     * @param p_action
     * @param p_network
     * @param p_blockedAt
     * @param p_resumedAt
     * @param p_otherInfo
     */
    public static void log(String p_referenceID, String p_inTxnId, String p_nodeUrl, boolean p_isNodeBlocked, boolean p_isNodeSuspended, String p_msisdn, String p_action, int p_retryCnt, String p_network, int p_conCnt, String p_blockedAt, String p_resumedAt, String p_otherInfo) {
        final String METHOD_NAME = "log";
        try {
            StringBuilder strBuff = new StringBuilder();
            strBuff.append("[REFID:" + p_referenceID + "]");
            strBuff.append("[INTXNID:" + p_inTxnId + "]");
            strBuff.append("[NURL:" + p_nodeUrl + "]");
            strBuff.append("[NBLCK:" + p_isNodeBlocked + "]");
            strBuff.append("[NSUS:" + p_isNodeSuspended + "]");
            strBuff.append("[MNO:" + p_msisdn + "]");
            strBuff.append("[ACT:" + p_action + "]");
            strBuff.append("[RTRY:" + p_retryCnt + "]");
            strBuff.append("[NW:" + p_network + "]");
            strBuff.append("[CONCNT:" + p_conCnt + "]");
            strBuff.append("[SUSAT:" + p_blockedAt + "]");
            strBuff.append("[RESAT:" + p_resumedAt + "]");
            strBuff.append("[INFO:" + p_otherInfo + "]");
            _log.info("", strBuff.toString());
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("log", p_referenceID, " Not able to log info, getting Exception :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "INNodeLog[log]", p_referenceID, "", "", "Not able to log info for IN Transfer ID:" + p_inTxnId + " ,getting Exception=" + e.getMessage());
        }
    }

    /**
     * Method to log the info in transaction log
     * 
     * @param p_referenceID
     * @param p_inTxnId
     * @param p_nodeUrl
     * @param nodeStatus
     * @param p_msisdn
     * @param p_action
     * @param p_network
     * @param p_blockedAt
     * @param p_resumedAt
     * @param p_otherInfo
     */
    public static void log(String p_referenceID, String p_inTxnId, String p_nodeUrl, boolean p_isNodeBlocked, boolean p_isNodeSuspended, String p_msisdn, int p_action, int p_ambRtryCnt, int p_conRtryCnt, String p_network, int p_conCnt, String p_otherInfo) {
        final String METHOD_NAME = "log";
        try {
            StringBuilder strBuff = new StringBuilder();
            strBuff.append("[REFID:" + p_referenceID + "]");
            strBuff.append("[INTXNID:" + p_inTxnId + "]");
            strBuff.append("[NURL:" + p_nodeUrl + "]");
            strBuff.append("[NBLCK:" + p_isNodeBlocked + "]");
            strBuff.append("[NSUS:" + p_isNodeSuspended + "]");
            strBuff.append("[MNO:" + p_msisdn + "]");
            strBuff.append("[ACT:" + p_action + "]");
            strBuff.append("[AMBRTRY:" + p_ambRtryCnt + "]");
            strBuff.append("[CONRTRY:" + p_conRtryCnt + "]");
            strBuff.append("[NW:" + p_network + "]");
            strBuff.append("[CONCNT:" + p_conCnt + "]");
            strBuff.append("[INFO:" + p_otherInfo + "]");
            _log.info("", strBuff.toString());
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("log", p_referenceID, " Not able to log info, getting Exception :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "INNodeLog[log]", p_referenceID, "", "", "Not able to log info for IN Transfer ID:" + p_inTxnId + " ,getting Exception=" + e.getMessage());
        }
    }

    public static void log(String p_referenceID, String p_inTxnId, String p_nodeUrl, boolean p_isNodeBlocked, boolean p_isNodeSuspended, String p_msisdn, int p_action, int p_ambRtryCnt, int p_conRtryCnt, String p_network, int p_conCnt, String p_otherInfo, String p_respCode, long p_processTime) {
        final String METHOD_NAME = "log";
        try {
            StringBuilder strBuff = new StringBuilder();
            strBuff.append("[REFID:" + p_referenceID + "]");
            strBuff.append("[INTXNID:" + p_inTxnId + "]");
            strBuff.append("[NURL:" + p_nodeUrl + "]");
            strBuff.append("[NBLCK:" + p_isNodeBlocked + "]");
            strBuff.append("[NSUS:" + p_isNodeSuspended + "]");
            strBuff.append("[MNO:" + p_msisdn + "]");
            strBuff.append("[ACT:" + p_action + "]");
            strBuff.append("[AMBRTRY:" + p_ambRtryCnt + "]");
            strBuff.append("[CONRTRY:" + p_conRtryCnt + "]");
            strBuff.append("[NW:" + p_network + "]");
            strBuff.append("[CONCNT:" + p_conCnt + "]");
            strBuff.append("[INFO:" + p_otherInfo + "]");
            strBuff.append("[INRESCODE:" + p_respCode + "]");
            strBuff.append("[AIRPRTM:" + p_processTime + "]");
            _log.info("", strBuff.toString());
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("log", p_referenceID, " Not able to log info, getting Exception :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "INNodeLog[log]", p_referenceID, "", "", "Not able to log info for IN Transfer ID:" + p_inTxnId + " ,getting Exception=" + e.getMessage());
        }
    }

}
