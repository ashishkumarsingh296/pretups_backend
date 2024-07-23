package com.btsl.pretups.p2p.logging;

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.p2p.subscriber.businesslogic.BuddyVO;
import com.btsl.pretups.p2p.subscriber.businesslogic.P2PBatchesVO;
import com.btsl.util.BTSLUtil;

/**
 * @(#) ScheduleCreditTransferLog_Detail.java
 *      Name Date History
 *      ------------------------------------------------------------------------
 *      Harsh Dixit 05/07/2013 Initial Creation
 *      ------------------------------------------------------------------------
 *      Copyright(c) 2013, Comviva Technologies Ltd.
 *      Logger used to track the lists summary that are scheduled for credit
 *      transfer
 */
public class ScheduleCreditTransferLogDetails {
    private static Log _log = LogFactory.getFactory().getInstance(ScheduleCreditTransferLogDetails.class.getName());

    /**
	 * ensures no instantiation
	 */
    private ScheduleCreditTransferLogDetails(){
    	
    }
    
    public static void log(P2PBatchesVO batchBuddyVO, BuddyVO buddyVO, String responseCode) {
        final String METHOD_NAME = "log";
        try {
            final StringBuffer strBuff = new StringBuffer();

            strBuff.append("[Batch ID:");
            strBuff.append(batchBuddyVO.getBatchID());
            strBuff.append("]");

            strBuff.append("[Batch Date:");
            strBuff.append(BTSLUtil.getDateTimeStringFromDate(batchBuddyVO.getCreatedOn()));
            strBuff.append("]");

            strBuff.append("[List Name:");
            strBuff.append(batchBuddyVO.getListName());
            strBuff.append("]");

            strBuff.append("[Schedule Type:");
            strBuff.append(batchBuddyVO.getScheduleType());
            strBuff.append("]");

            strBuff.append("[Schedule Frquency:");
            strBuff.append(batchBuddyVO.getNoOfSchedule());
            strBuff.append("]");

            strBuff.append("[Execution Count:");
            strBuff.append(batchBuddyVO.getExecutionCount());
            strBuff.append("]");

            strBuff.append("[Owner MSISDN:");
            strBuff.append(batchBuddyVO.getSenderMSISDN());
            strBuff.append("]");

            strBuff.append("[Owner ID:");
            strBuff.append(batchBuddyVO.getParentID());
            strBuff.append("]");

            strBuff.append("[Buddy MSISDN:");
            strBuff.append(buddyVO.getBuddyMsisdn());
            strBuff.append("]");

            strBuff.append("[Preferred Amount:");
            strBuff.append(buddyVO.getPreferredAmount());
            strBuff.append("]");

            strBuff.append("[Last Transfer ID:");
            strBuff.append(buddyVO.getLastTransferID());
            strBuff.append("]");

            strBuff.append("[Last Transfer Date:");
            strBuff.append(BTSLUtil.getDateTimeStringFromDate(buddyVO.getLastTransferOn()));
            strBuff.append("]");

            strBuff.append("[Last Transfer Amount:");
            strBuff.append(buddyVO.getLastTransferAmount());
            strBuff.append("]");

            strBuff.append("[Last Transfer Type:");
            strBuff.append(buddyVO.getLastTransferType());
            strBuff.append("]");

            strBuff.append("[Transfer Status:");
            strBuff.append(responseCode);
            strBuff.append("]");

            strBuff.append("[Successive Failures:");
            strBuff.append(buddyVO.getSuccessiveFailCount());
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
