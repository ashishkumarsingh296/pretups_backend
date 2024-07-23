package com.btsl.pretups.p2p.logging;

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.p2p.subscriber.businesslogic.P2PBatchesVO;
import com.btsl.util.BTSLUtil;

/**
 * @(#) ScheduleCreditTransferLog_Summary.java
 *      Name Date History
 *      ------------------------------------------------------------------------
 *      Harsh Dixit 05/07/2013 Initial Creation
 *      ------------------------------------------------------------------------
 *      Copyright(c) 2013, Comviva Technologies Ltd.
 *      Logger used to track the lists summary that are scheduled for credit
 *      transfer
 */
public class ScheduleCreditTransferLogSummary {
    private static Log _log = LogFactory.getFactory().getInstance(ScheduleCreditTransferLogSummary.class.getName());

    /**
	 * ensures no instantiation
	 */
    private ScheduleCreditTransferLogSummary(){
    	
    }
    public static void log(P2PBatchesVO batchBuddyVO, int listCount, int successCount, int failCount, int ambigsCount) {
        final String METHOD_NAME = "log";
        try {
            final StringBuffer strBuff = new StringBuffer();

            strBuff.append("[Batch ID:");
            strBuff.append(batchBuddyVO.getBatchID());
            strBuff.append("]");

            strBuff.append("[Batch Date:");
            strBuff.append(BTSLUtil.getDateTimeStringFromDate(batchBuddyVO.getCreatedOn()));
            strBuff.append("]");

            strBuff.append("[Owner ID:");
            strBuff.append(batchBuddyVO.getParentID());
            strBuff.append("]");

            strBuff.append("[List Name:");
            strBuff.append(batchBuddyVO.getListName());
            strBuff.append("]");

            strBuff.append("[Total Transfers:");
            strBuff.append(listCount);
            strBuff.append("]");

            strBuff.append("[Successful Transfers:");
            strBuff.append(successCount);
            strBuff.append("]");

            strBuff.append("[Fail Transfers:");
            strBuff.append(failCount);
            strBuff.append("]");

            strBuff.append("[Ambiguous Transfers:");
            strBuff.append(ambigsCount);
            strBuff.append("]");

            _log.debug("scheduleRecords", strBuff.toString());
        }

        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("log", batchBuddyVO.getParentID() + "" + batchBuddyVO.getBatchID(), " Not able to log info, getting Exception :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduleCreditTransferLog_Details[log]", batchBuddyVO
                .getParentID() + "-" + batchBuddyVO.getBatchID(), "", "",
                "Not able to log info for Transaction ID - Request ID:" + batchBuddyVO.getParentID() + "-" + batchBuddyVO.getBatchID() + " ,getting Exception=" + e
                    .getMessage());
        }
    }

}
