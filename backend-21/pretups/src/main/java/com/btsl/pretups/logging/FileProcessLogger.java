package com.btsl.pretups.logging;

import java.text.ParseException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.whitelist.businesslogic.WhiteListVO;
import com.btsl.util.BTSLDateUtil;

/**
 * @(#)FileProcessLogger.java
 *                            Copyright(c) 2006, Bharti Telesoft Int. Public
 *                            Ltd.
 *                            All Rights Reserved
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Author Date History
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Ashish Kumar May 18, 2006 Initial Creation
 * 
 */
public class FileProcessLogger {

    private static Log _log = LogFactory.getLog(FileProcessLogger.class.getName());

    private FileProcessLogger() {
        super();
    }

    public static void log(boolean p_flagStart, WhiteListVO p_whiteListVO) throws ParseException {
        StringBuffer message = new StringBuffer();
        if (p_flagStart) {
            message.append("[START-DATE-TIME = " + BTSLDateUtil.getLocaleDateTimeFromDate(p_whiteListVO.getStartDate()) + "] ");
        }

        if (p_whiteListVO.getErrorCode() != null) {
            message.append("[ERROR = " + p_whiteListVO.getErrorCode() + " ]");
            message.append(p_whiteListVO.getMsisdn() != null ? "[MSISDN =" + p_whiteListVO.getMsisdn() + "] " : "");
            message.append(p_whiteListVO.getAccountStatus() != null ? "[ACCOUNT-STATUS = " + p_whiteListVO.getAccountStatus() + "] " : "");
            message.append(p_whiteListVO.getCreditLimitStr() != null ? "[CREDIT-LIMIT = " + p_whiteListVO.getCreditLimitStr() + " ]" : "");
            message.append(p_whiteListVO.getAccountID() != null ? "[ACCOUNT-ID = " + p_whiteListVO.getAccountID() + "] " : "");
            message.append(p_whiteListVO.getServiceClassCode() != null ? "[SERVICE-CLASS =" + p_whiteListVO.getServiceClassCode() + "] " : "");
            message.append(p_whiteListVO.getMovementCode() != null ? "[MOVEMENT-CODE =" + p_whiteListVO.getMovementCode() + "] " : "");
        }
        if (p_whiteListVO.getTotalRecords() != 0) {
            message = new StringBuffer();
            message.append("[TOTAL-RECORDS = " + p_whiteListVO.getTotalRecords() + "] ");
            message.append("[PROCESSED-RECORDS = " + p_whiteListVO.getProcessedRecords() + "] ");
            message.append("[UNPROCESSED-RECORDS = " + p_whiteListVO.getUnProccessedRecords() + "] ");
            message.append("[END-DATE-TIME = " + BTSLDateUtil.getLocaleDateTimeFromDate(p_whiteListVO.getEndDate()) + "] ");
        }
        _log.info(" ", message);
    }
}
