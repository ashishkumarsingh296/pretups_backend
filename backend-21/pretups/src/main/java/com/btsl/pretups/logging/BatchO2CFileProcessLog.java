package com.btsl.pretups.logging;

/**
 * @# BatchO2CFileProcessLog.java
 * 
 *    Created by Created on History
 *    --------------------------------------------------------------------------
 *    ------
 *    Chhaya sikheria 24/10/11 Initial Creation
 *    --------------------------------------------------------------------------
 *    ------
 *    Copyright(c) 2011 Comviva.
 */

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchMasterVO;
import com.btsl.pretups.channel.transfer.businesslogic.O2CBatchItemsVO;
import com.btsl.util.BTSLUtil;

public class BatchO2CFileProcessLog {

    private static Log _Filelogger = LogFactory.getLog(BatchO2CFileProcessLog.class.getName());

    /**
     * ensures no instantiation
     */
    private BatchO2CFileProcessLog(){
    	
    }
    
    /**
     * 
     * @param p_action
     * @param p_batchMasterVO
     * @param p_batchItemsVO
     * @param p_result
     * @param p_otherInfo
     */
    public static void detailLog(String p_action, FOCBatchMasterVO p_batchMasterVO, O2CBatchItemsVO p_batchItemsVO, String p_result, String p_otherInfo) {
        StringBuffer message = new StringBuffer();
        message.append("[ACTION = " + BTSLUtil.NullToString(p_action) + "] ");
        message.append("[NETWORK_ID = " + BTSLUtil.NullToString(p_batchMasterVO.getNetworkCode()) + "] ");
        message.append("[BATCH_NAME = " + BTSLUtil.NullToString(p_batchMasterVO.getBatchName()) + "] ");
        message.append("[PRODUCT = " + BTSLUtil.NullToString(p_batchMasterVO.getProductCode()) + "] ");
        message.append("[ACTION_BY = " + BTSLUtil.NullToString(p_batchItemsVO.getModifiedBy()) + "] ");
        message.append("[BATCH_DETAIL_ID = " + BTSLUtil.NullToString(p_batchItemsVO.getBatchDetailId()) + "] ");
        message.append("[MSISDN = " + BTSLUtil.NullToString(p_batchItemsVO.getMsisdn()) + "] ");
        // message.append("[USER_ID = "+BTSLUtil.NullToString(p_batchItemsVO.getUserId())+"] ");
        message.append("[EXTERNAL_CODE = " + BTSLUtil.NullToString(p_batchItemsVO.getExternalCode()) + "] ");
        message.append("[QTY = " + p_batchItemsVO.getRequestedQuantity() + "] ");
        message.append("[RESULT = " + BTSLUtil.NullToString(p_result) + "] ");
        message.append("[OTHER INFO = " + BTSLUtil.NullToString(p_otherInfo) + "] ");
        _Filelogger.info("", message);
    }

    /**
     * 
     * @param p_action
     * @param p_batchMasterVO
     * @param p_result
     * @param p_otherInfo
     */
    public static void o2cBatchMasterLog(String p_action, FOCBatchMasterVO p_batchMasterVO, String p_result, String p_otherInfo) {
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
     * 
     * @param p_action
     * @param p_batchItemsVO
     * @param p_result
     * @param p_otherInfo
     */
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
}
