package com.btsl.pretups.logging;

/**
 * @# BatchC2cFileProcessLog.java
 * 
 *    Created by Created on History
 *    --------------------------------------------------------------------------
 *    ------
 *    Ankit Zindal 29/06/06 Initial Creation
 *    Sandeep Goel 24/07/06 Modification
 *    --------------------------------------------------------------------------
 *    ------
 *    Copyright(c) 2006 Bharti Telesoft Ltd.
 */

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2CBatchItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2CBatchMasterVO;
import com.btsl.util.BTSLUtil;

public class BatchC2CFileProcessLog {
    private static Log _Filelogger = LogFactory.getLog(BatchC2CFileProcessLog.class.getName());

    private BatchC2CFileProcessLog() {
        super();
    }

    public static void detailLog(String p_action, C2CBatchMasterVO p_batchMasterVO, C2CBatchItemsVO p_batchItemsVO, String p_result, String p_otherInfo) {
        StringBuffer message = new StringBuffer();
        message.append("[ACTION = ").append(BTSLUtil.NullToString(p_action));
        message.append("] [NETWORK_ID = ").append(BTSLUtil.NullToString(p_batchMasterVO.getNetworkCode()));
        message.append("] [BATCH_NAME = ").append(BTSLUtil.NullToString(p_batchMasterVO.getBatchName()));
        message.append("] [PRODUCT = ").append(BTSLUtil.NullToString(p_batchMasterVO.getProductCode()));
        message.append("] [ACTION_BY = ").append(BTSLUtil.NullToString(p_batchItemsVO.getModifiedBy()));
        message.append("] [BATCH_DETAIL_ID = ").append(BTSLUtil.NullToString(p_batchItemsVO.getBatchDetailId()));
        message.append("] [MSISDN = ").append(BTSLUtil.NullToString(p_batchItemsVO.getMsisdn()));
        message.append("] [USER_ID = ").append(BTSLUtil.NullToString(p_batchItemsVO.getUserId()));
        message.append("] [EXTERNAL_CODE = ").append(BTSLUtil.NullToString(p_batchItemsVO.getExternalCode()));
        message.append("] [QTY = ").append(p_batchItemsVO.getRequestedQuantity());
        message.append("] [RESULT = ").append(BTSLUtil.NullToString(p_result));
        message.append("] [OTHER INFO = ").append(BTSLUtil.NullToString(p_otherInfo));
        message.append("] ");
        _Filelogger.info("", message);
    }

    public static void c2cBatchMasterLog(String p_action, C2CBatchMasterVO p_batchMasterVO, String p_result, String p_otherInfo) {
        StringBuffer message = new StringBuffer();
        message.append("[ACTION = ").append(BTSLUtil.NullToString(p_action));
        message.append("] ");
        if (p_batchMasterVO != null) {
            message.append("[NETWORK_ID = ").append(BTSLUtil.NullToString(p_batchMasterVO.getNetworkCode()));
            message.append("] [PRODUCT = ").append(BTSLUtil.NullToString(p_batchMasterVO.getProductCode()));
            message.append("] [BATCH_ID = ").append(BTSLUtil.NullToString(p_batchMasterVO.getBatchId()));
            message.append("] [BATCH_NAME = ").append(BTSLUtil.NullToString(p_batchMasterVO.getBatchName()));
            message.append("] [ACTION_BY = ").append(BTSLUtil.NullToString(p_batchMasterVO.getModifiedBy()));
            message.append("] ");
        }
        message.append("[RESULT = ").append(BTSLUtil.NullToString(p_result));
        message.append("] [OTHER INFO = ").append(BTSLUtil.NullToString(p_otherInfo));
        message.append("] ");
        _Filelogger.info("", message);
    }

    public static void c2cBatchItemLog(String p_action, C2CBatchItemsVO p_batchItemsVO, String p_result, String p_otherInfo) {
        StringBuffer message = new StringBuffer();
        message.append("[ACTION = ").append(BTSLUtil.NullToString(p_action));
        if (p_batchItemsVO != null) {
            message.append("] [ACTION_BY = ").append(BTSLUtil.NullToString(p_batchItemsVO.getModifiedBy()));
            message.append("] [BATCH_DETAIL_ID = ").append(BTSLUtil.NullToString(p_batchItemsVO.getBatchDetailId()));
            message.append("] [MSISDN = ").append(BTSLUtil.NullToString(p_batchItemsVO.getMsisdn()));
            message.append("] [USER_ID = ").append(BTSLUtil.NullToString(p_batchItemsVO.getUserId()));
            message.append("] [EXTERNAL_CODE = ").append(BTSLUtil.NullToString(p_batchItemsVO.getExternalCode()));
            message.append("] [QTY = ").append(p_batchItemsVO.getRequestedQuantity());
        }
        message.append("] [RESULT = ").append(BTSLUtil.NullToString(p_result));
        message.append("] [OTHER INFO = ").append(BTSLUtil.NullToString(p_otherInfo));
        message.append("] ");
        _Filelogger.info("", message);
    }
	
	public static void operatorDetailLog(String p_action,C2CBatchMasterVO p_batchMasterVO,C2CBatchItemsVO p_batchItemsVO,String p_result,String p_otherInfo)
	{
		StringBuffer message=new StringBuffer();
		message.append("[ACTION = ").append(BTSLUtil.NullToString(p_action));
		message.append("] [NETWORK_ID = ").append(BTSLUtil.NullToString(p_batchMasterVO.getNetworkCode()));
		message.append("] [BATCH_NAME = ").append(BTSLUtil.NullToString(p_batchMasterVO.getBatchName()));
		message.append("] [PRODUCT = ").append(BTSLUtil.NullToString(p_batchMasterVO.getProductCode()));
		message.append("] [ACTION_BY = ").append(BTSLUtil.NullToString(p_batchItemsVO.getModifiedBy()));
		message.append("] [OPT_BATCH_ID = ").append(BTSLUtil.NullToString(p_batchMasterVO.getOptBatchId()));
		message.append("] [BATCH_ID = ").append(BTSLUtil.NullToString(p_batchMasterVO.getBatchId()));
		message.append("] [BATCH_DETAIL_ID = ").append(BTSLUtil.NullToString(p_batchItemsVO.getBatchDetailId()));
		message.append("] [MSISDN = ").append(BTSLUtil.NullToString(p_batchItemsVO.getMsisdn()));
		message.append("] [USER_ID = ").append(BTSLUtil.NullToString(p_batchItemsVO.getUserId()));
		message.append("] [EXTERNAL_CODE = ").append(BTSLUtil.NullToString(p_batchItemsVO.getExternalCode()));
		message.append("] [QTY = ").append(p_batchItemsVO.getRequestedQuantity());
		message.append("] [RESULT = ").append(BTSLUtil.NullToString(p_result));
		message.append("] [OTHER INFO = ").append(BTSLUtil.NullToString(p_otherInfo));
		message.append("] ");
		_Filelogger.info("",message);
	}

}
