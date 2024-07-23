/**
 * SchedulerListValueVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Ashish Kumar 01/04/2006 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2006 Bharti Telesoft Ltd.
 * Class used to create the log file for the Batch Master
 */
package com.btsl.pretups.channel.logging;

import java.text.ParseException;
import java.util.ArrayList;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchDetailVO;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchMasterVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;

public class ScheduledBatchLogger {

    private static Log _log = LogFactory.getLog(ScheduledBatchLogger.class.getName());

    private ScheduledBatchLogger() {
        super();
    }

    public static void log(ScheduleBatchMasterVO p_scheduleBatchMasterVO) throws ParseException {
        final ArrayList scheduleDetailList = p_scheduleBatchMasterVO.getList();
        ScheduleBatchDetailVO scheduleBatchDetailVO = null;
        final StringBuffer strBuff = new StringBuffer("[ START DATE-TIME= " + BTSLDateUtil.getLocaleDateTimeFromDate(p_scheduleBatchMasterVO.getStartDateOfBatch()) + " ] ");
        strBuff.append(" [BATCH-ID= " + p_scheduleBatchMasterVO.getBatchID() + " ] ");
        if (scheduleDetailList != null && !scheduleDetailList.isEmpty()) {
            for (int index = 0, listSize = scheduleDetailList.size(); index < listSize; index++) {
                scheduleBatchDetailVO = (ScheduleBatchDetailVO) scheduleDetailList.get(index);
                if (scheduleBatchDetailVO.getErrorCode() != null) {
                    strBuff
                        .append("[MSISDN= " + scheduleBatchDetailVO.getMsisdn() + " ] " + "[NAME= " + BTSLUtil.NullToString(scheduleBatchDetailVO.getEmployeeName()) + " ] " + "[ AMT= " + scheduleBatchDetailVO
                            .getAmountForDisp() + " ] " + "[REASON OF FAILURE= " + scheduleBatchDetailVO.getErrorCode() + " ]");
                }
            }
        }
        strBuff
            .append(" [TOTAL-RECORDS= " + p_scheduleBatchMasterVO.getTotalRecords() + "] " + "[ PROCESSED RECORDS= " + p_scheduleBatchMasterVO.getProccessedRecords() + "] " + "[ UNPROCESSED RECORDS= " + p_scheduleBatchMasterVO
                .getUnproccessedRecords() + "] ");
        strBuff.append(" [END DATE-TIME= " + BTSLDateUtil.getLocaleDateTimeFromDate(p_scheduleBatchMasterVO.getEndDateOfBatch()) + " ]");
        _log.info(" ", strBuff.toString());
    }
}
