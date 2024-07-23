package com.btsl.pretups.logging;

/**
 * @# BatchO2CProcessLog.java
 * 
 *    Created by Created on History
 *    --------------------------------------------------------------------------
 *    ------
 *    nand.sahu June 02 2011 Initial Creation
 *    --------------------------------------------------------------------------
 *    ------
 *    Copyright(c) 2011 Comviva Technologies Ltd.
 */

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.BatchO2CItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.BatchO2CMasterVO;
import com.btsl.pretups.channel.transfer.businesslogic.O2CBatchItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.O2CBatchMasterVO;
import com.btsl.util.BTSLUtil;

public class BatchO2CProcessLog {
    private static Log _Filelogger = LogFactory.getLog(BatchO2CProcessLog.class.getName());

    private BatchO2CProcessLog() {
        super();
    }

    public static void detailLog(String p_action, BatchO2CMasterVO p_batchMasterVO, BatchO2CItemsVO p_batchItemsVO, String p_result, String p_otherInfo) {
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

    public static void batchO2CMasterLog(String p_action, BatchO2CMasterVO p_batchMasterVO, String p_result, String p_otherInfo) {
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

    public static void batchO2CItemLog(String p_action, BatchO2CItemsVO p_batchItemsVO, String p_result, String p_otherInfo) {
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

    public static void detailLog(String p_action, O2CBatchMasterVO p_batchMasterVO, BatchO2CItemsVO p_batchItemsVO, String p_result, String p_otherInfo) {
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

    public static void o2cBatchMasterLog(String p_action, O2CBatchMasterVO p_batchMasterVO, String p_result, String p_otherInfo) {
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

    public static void batchO2CItemLog(String p_action, O2CBatchItemsVO p_batchItemsVO, String p_result, String p_otherInfo) {
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

    public static void batchO2CMasterLog(String p_action, O2CBatchMasterVO p_batchMasterVO, String p_result, String p_otherInfo) {
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

    public static void o2cBatchItemLog(String p_action, O2CBatchItemsVO p_batchItemsVO, String p_result, String p_otherInfo) {
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

    public static void o2cBatchItemLog(String p_action, BatchO2CItemsVO p_batchItemsVO, String p_result, String p_otherInfo) {
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
}
