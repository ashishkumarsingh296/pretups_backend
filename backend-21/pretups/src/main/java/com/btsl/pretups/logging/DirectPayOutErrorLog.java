package com.btsl.pretups.logging;

import java.text.ParseException;

/*
 * @#DirectPayOutErrorLog.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Manisha Jain 26/10/09 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2009 Comviva Technologies Ltd.
 */

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchMasterVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;

public class DirectPayOutErrorLog {
    private static Log _Filelogger = LogFactory.getLog(DirectPayOutErrorLog.class.getName());

    private DirectPayOutErrorLog() {
        super();
    }

    /**
     * detailLog
     * 
     * @param p_action
     * @param p_batchMasterVO
     * @param p_batchItemsVO
     * @param p_result
     * @param p_otherInfo
     */
    public static void detailLog(String p_action, FOCBatchMasterVO p_batchMasterVO, FOCBatchItemsVO p_batchItemsVO, String p_result, String p_otherInfo) {
        StringBuffer message = new StringBuffer();
        message.append("[ACTION = " + BTSLUtil.NullToString(p_action) + "] ");
        message.append("[NETWORK_ID = " + BTSLUtil.NullToString(p_batchMasterVO.getNetworkCode()) + "] ");
        message.append("[BATCH_NAME = " + BTSLUtil.NullToString(p_batchMasterVO.getBatchName()) + "] ");
        message.append("[PRODUCT = " + BTSLUtil.NullToString(p_batchMasterVO.getProductCode()) + "] ");
        message.append("[ACTION_BY = " + BTSLUtil.NullToString(p_batchItemsVO.getModifiedBy()) + "] ");
        message.append("[BATCH_DETAIL_ID = " + BTSLUtil.NullToString(p_batchItemsVO.getBatchDetailId()) + "] ");
        message.append("[MSISDN = " + BTSLUtil.NullToString(p_batchItemsVO.getMsisdn()) + "] ");
        message.append("[USER_ID = " + BTSLUtil.NullToString(p_batchItemsVO.getUserId()) + "] ");
        message.append("[EXTERNAL_CODE = " + BTSLUtil.NullToString(p_batchItemsVO.getExternalCode()) + "] ");
        message.append("[QTY = " + p_batchItemsVO.getRequestedQuantity() + "] ");
        message.append("[RESULT = " + BTSLUtil.NullToString(p_result) + "] ");
        message.append("[OTHER INFO = " + BTSLUtil.NullToString(p_otherInfo) + "] ");
        _Filelogger.info("", message);
    }

    /**
     * dpBatchMasterLog
     * 
     * @param p_action
     * @param p_batchMasterVO
     * @param p_result
     * @param p_otherInfo
     */
    public static void dpBatchMasterLog(String p_action, FOCBatchMasterVO p_batchMasterVO, String p_result, String p_otherInfo) {
        StringBuffer message = new StringBuffer();
        message.append("[ACTION = " + BTSLUtil.NullToString(p_action) + "] ");
        if (p_batchMasterVO != null) {
            message.append("[NETWORK_ID = " + BTSLUtil.NullToString(p_batchMasterVO.getNetworkCode()) + "] ");
            message.append("[PRODUCT = " + BTSLUtil.NullToString(p_batchMasterVO.getProductCode()) + "] ");
            message.append("[BATCH_ID = " + BTSLUtil.NullToString(p_batchMasterVO.getBatchId()) + "] ");
            message.append("[BATCH_NAME = " + BTSLUtil.NullToString(p_batchMasterVO.getBatchName()) + "] ");
            message.append("[ACTION_BY = " + BTSLUtil.NullToString(p_batchMasterVO.getModifiedBy()) + "] ");
        }
        message.append("[RESULT = " + BTSLUtil.NullToString(p_result) + "] ");
        message.append("[OTHER INFO = " + BTSLUtil.NullToString(p_otherInfo) + "] ");
        _Filelogger.info("", message);
    }

    /**
     * dpBatchItemLog
     * 
     * @param p_action
     * @param p_batchItemsVO
     * @param p_result
     * @param p_otherInfo
     */
    public static void dpBatchItemLog(String p_action, FOCBatchItemsVO p_batchItemsVO, String p_result, String p_otherInfo) {
        StringBuffer message = new StringBuffer();
        message.append("[ACTION = " + BTSLUtil.NullToString(p_action) + "] ");
        if (p_batchItemsVO != null) {
            message.append("[ACTION_BY = " + BTSLUtil.NullToString(p_batchItemsVO.getModifiedBy()) + "] ");
            message.append("[BATCH_DETAIL_ID = " + BTSLUtil.NullToString(p_batchItemsVO.getBatchDetailId()) + "] ");
            message.append("[MSISDN = " + BTSLUtil.NullToString(p_batchItemsVO.getMsisdn()) + "] ");
            message.append("[USER_ID = " + BTSLUtil.NullToString(p_batchItemsVO.getUserId()) + "] ");
            message.append("[EXTERNAL_CODE = " + BTSLUtil.NullToString(p_batchItemsVO.getExternalCode()) + "] ");
            message.append("[QTY = " + p_batchItemsVO.getRequestedQuantity() + "] ");
        }
        message.append("[RESULT = " + BTSLUtil.NullToString(p_result) + "] ");
        message.append("[OTHER INFO = " + BTSLUtil.NullToString(p_otherInfo) + "] ");
        _Filelogger.info("", message);
    }

    /**
     * log
     * 
     * @param p_msisdn
     * @param p_fileName
     * @param p_message
     * @throws ParseException 
     */
    public static void log(String p_msisdn, String p_fileName, String p_message) {
        StringBuffer str = new StringBuffer();
        str.append("[MSISDN: " + p_msisdn + "] ");
        str.append("[FILE NAME: " + p_fileName + "] ");
        str.append("[MESSAGE: " + p_message + "] ");
        str.append("[DATE: " + BTSLDateUtil.getSystemLocaleDateTime() + "] ");
        _Filelogger.info(" ", str.toString());
    }

}
