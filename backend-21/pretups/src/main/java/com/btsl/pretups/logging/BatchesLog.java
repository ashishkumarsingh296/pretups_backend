package com.btsl.pretups.logging;

/*
 * @(#)BatchesLog.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Ved prakash Sharma 26/07/06 Initial Creation
 * Shishupal Singh 21/03/07 Modification
 * ------------------------------------------------------------------------
 * Copyright (c) 2007 Bharti Telesoft Ltd.
 */

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.batch.businesslogic.BatchesVO;
import com.btsl.pretups.transfer.businesslogic.BatchTransferRulesVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.BatchOPTUserVO;
import com.btsl.util.BTSLDateUtil;

public class BatchesLog {
    private static Log _log = LogFactory.getFactory().getInstance(BatchesLog.class.getName());

    /**
     * ensures no instantiation
     */
    private BatchesLog(){
    	
    }
    
    public static void log(String p_action, ChannelUserVO p_channelUserVO, BatchesVO p_batchesVO, String p_otherInfo) {
        final String METHOD_NAME = "log";
        try {
            StringBuffer strBuff = new StringBuffer("[Action: " + p_action);
            if (p_batchesVO != null) {
                strBuff.append(" [ Batch ID:" + p_batchesVO.getBatchID());
                strBuff.append(" # Batch Status:" + p_batchesVO.getStatus());
                strBuff.append(" # Batch Type:" + p_batchesVO.getBatchType());
                strBuff.append(" # Modify By: " + p_batchesVO.getModifiedBy());
                strBuff.append(" # Modify On:" + BTSLDateUtil.getLocaleDateTimeFromDate(p_batchesVO.getModifiedOn()));
                strBuff.append(" # Batch Size:" + p_batchesVO.getBatchSize() + "]");
            }
            if (p_channelUserVO != null) {
                strBuff.append(" [User Status:" + p_channelUserVO.getStatus());
                strBuff.append(" # Batch ID:" + p_channelUserVO.getBatchID());
                strBuff.append(" # User ID:" + p_channelUserVO.getUserID());
                strBuff.append(" # Login id:" + p_channelUserVO.getLoginID());
                strBuff.append(" # User name:" + p_channelUserVO.getUserName());
                strBuff.append(" # Mobile no. :" + p_channelUserVO.getMsisdn());
                strBuff.append(" # Modify By: " + p_channelUserVO.getModifiedBy());
                strBuff.append(" # Modify On:" + BTSLDateUtil.getLocaleDateTimeFromDate(p_channelUserVO.getModifiedOn()));
                strBuff.append(" # User Grade:" + p_channelUserVO.getUserGrade());
                strBuff.append(" # Transfer profile:" + p_channelUserVO.getTransferProfileID());
                strBuff.append(" # Commision profile:" + p_channelUserVO.getCommissionProfileSetID());
                strBuff.append(" # Remarks:" + p_channelUserVO.getRemarks() + "]");
            }
            strBuff.append(" # [Other Info :" + p_otherInfo + "]");
            _log.info("", strBuff.toString());
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("log", "Action : " + p_action + "getting Exception :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchesLog[log]", "", "", "", "Action : " + p_action + " ,getting Exception=" + e.getMessage());
        }
    }

    public static void operatorUserLog(String p_action, BatchOPTUserVO p_batchOPTUserVO, BatchesVO p_batchesVO, String p_otherInfo) {
        final String METHOD_NAME = "operatorUserLog";
        try {
            StringBuffer strBuff = new StringBuffer("[Action: " + p_action);
            if (p_batchesVO != null) {
                strBuff.append(" [ Batch ID:" + p_batchesVO.getBatchID());
                strBuff.append(" # Batch Status:" + p_batchesVO.getStatus());
                strBuff.append(" # Batch Type:" + p_batchesVO.getBatchType());
                strBuff.append(" # Modify By: " + p_batchesVO.getModifiedBy());
                strBuff.append(" # Modify On:" + BTSLDateUtil.getLocaleDateTimeFromDate(p_batchesVO.getModifiedOn()));
                strBuff.append(" # Batch Size:" + p_batchesVO.getBatchSize() + "]");
            }
            if (p_batchOPTUserVO != null) {
                strBuff.append(" [User Status:" + p_batchOPTUserVO.getStatus());
                strBuff.append(" # Batch ID:" + p_batchOPTUserVO.getBatchID());
                strBuff.append(" # User ID:" + p_batchOPTUserVO.getUserID());
                strBuff.append(" # Login id:" + p_batchOPTUserVO.getLoginID());
                strBuff.append(" # User name:" + p_batchOPTUserVO.getUserName());
                strBuff.append(" # Mobile no. :" + p_batchOPTUserVO.getMsisdn());
                strBuff.append(" # Modify By: " + p_batchOPTUserVO.getModifiedBy());
                strBuff.append(" # Modify On:" + BTSLDateUtil.getLocaleDateTimeFromDate(p_batchOPTUserVO.getModifiedOn()));
                strBuff.append(" # Remarks:" + p_batchOPTUserVO.getRemarks() + "]");
            }
            strBuff.append(" # [Other Info :" + p_otherInfo + "]");
            _log.info("", strBuff.toString());
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("log", "Action : " + p_action + "getting Exception :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchesLog[log]", "", "", "", "Action : " + p_action + " ,getting Exception=" + e.getMessage());
        }
    }

    public static void transferRuleLog(String p_action, BatchTransferRulesVO p_batchTransferRulesVO, BatchesVO p_batchesVO, String p_otherInfo) {
        final String METHOD_NAME = "transferRuleLog";
        try {
            StringBuffer strBuff = new StringBuffer("[Action: " + p_action);
            if (p_batchesVO != null) {
                strBuff.append(" [ Batch ID:" + p_batchesVO.getBatchID());
                strBuff.append(" # Batch Status:" + p_batchesVO.getStatus());
                strBuff.append(" # Batch Type:" + p_batchesVO.getBatchType());
                strBuff.append(" # Modify By: " + p_batchesVO.getModifiedBy());
                strBuff.append(" # Modify On:" + BTSLDateUtil.getLocaleDateTimeFromDate(p_batchesVO.getModifiedOn()));
                strBuff.append(" # Batch Size:" + p_batchesVO.getBatchSize() + "]");
            }
            if (p_batchTransferRulesVO != null) {
                strBuff.append(" [User Status:" + p_batchTransferRulesVO.getStatus());
                strBuff.append(" # Modify By: " + p_batchTransferRulesVO.getModifiedBy());
                strBuff.append(" # Modify On:" + BTSLDateUtil.getLocaleDateTimeFromDate(p_batchTransferRulesVO.getModifiedOn()));
            }
            strBuff.append(" # [Other Info :" + p_otherInfo + "]");
            _log.info("", strBuff.toString());
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("log", "Action : " + p_action + "getting Exception :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchesLog[log]", "", "", "", "Action : " + p_action + " ,getting Exception=" + e.getMessage());
        }
    }
}
