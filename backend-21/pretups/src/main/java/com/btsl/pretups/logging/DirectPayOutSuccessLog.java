package com.btsl.pretups.logging;

import java.util.Date;

/*
 * DirectPayOutSuccessLog.java
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
import com.btsl.util.BTSLUtil;

public class DirectPayOutSuccessLog {
    private static Log _Filelogger = LogFactory.getFactory().getInstance(DirectPayOutSuccessLog.class.getName());

    private DirectPayOutSuccessLog() {
        super();
    }

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

    public static void log(String msisdn, String fileName, String message, String txnID, FOCBatchItemsVO p_focBatchItemVO, String p_networkCode, String p_productCode) {
        StringBuffer str = new StringBuffer();
        Date currentDate = new Date();
        str.append("[MSISDN: " + msisdn + "] ");
        str.append("[USER ID: " + p_focBatchItemVO.getUserId() + "] ");
        str.append("[NETWORK CODE: " + p_networkCode + "] ");
        str.append("[FILE NAME: " + fileName + "] ");
        str.append("[MESSAGE: " + message + "] ");
        str.append("[TRANSFER ID: " + txnID + "] ");
        str.append("[BATCH ID: " + p_focBatchItemVO.getBatchId() + "] ");
        str.append("[BATCH DETAIL ID: " + p_focBatchItemVO.getBatchDetailId() + "] ");
        str.append("[PRODUCT CODE: " + p_productCode + "] ");
        str.append("[DATE: " + currentDate + "] ");
        _Filelogger.info(" ", str.toString());
    }

}
