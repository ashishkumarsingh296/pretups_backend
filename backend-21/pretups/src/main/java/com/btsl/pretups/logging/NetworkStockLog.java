package com.btsl.pretups.logging;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.restapi.networkadmin.networkStock.NetworkStockTxnVO1;

/*
 * @# NetworkStockLog.java
 * 
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * --
 * Sandeep Goel Dec 26, 2005 Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
public class NetworkStockLog {
    private static Log _Filelogger = LogFactory.getLog(NetworkStockLog.class.getName());

    /**
	 * ensures no instantiation
	 */
    private NetworkStockLog() {
        
    }

    public static void log(NetworkStockTxnVO p_networkStockTxnVO) {
        StringBuilder strBuff = new StringBuilder();
        strBuff.append(" [Transfer ID:" + p_networkStockTxnVO.getTxnNo() + "]");
        strBuff.append(" [Reference ID:" + p_networkStockTxnVO.getReferenceNo() + "]");
        strBuff.append(" [User ID:" + p_networkStockTxnVO.getUserID() + "]");
        strBuff.append(" [Network ID:" + p_networkStockTxnVO.getNetworkCode() + "]");
        strBuff.append(" [Network For:" + p_networkStockTxnVO.getNetworkFor() + "]");
        strBuff.append(" [Product Code:" + p_networkStockTxnVO.getProductCode() + "]");
        strBuff.append(" [Requested Qty:" + p_networkStockTxnVO.getRequestedQuantity() + "]");
        strBuff.append(" [Transfer Qty:" + p_networkStockTxnVO.getApprovedQuantity() + "]");
        strBuff.append(" [Previous Bal:" + p_networkStockTxnVO.getPreviousStock() + "]");
        strBuff.append(" [Post Bal:" + p_networkStockTxnVO.getPostStock() + "]");
        strBuff.append(" [Entry Type:" + p_networkStockTxnVO.getTxnType() + "]");
        strBuff.append(" [Type:" + p_networkStockTxnVO.getStockType() + "]");
        strBuff.append(" [Transfer Category:" + p_networkStockTxnVO.getTxnCategory() + "]");
        strBuff.append(" [Transfer Type:" + p_networkStockTxnVO.getEntryType() + "]");
        strBuff.append(" [Transfer On:" + BTSLDateUtil.getLocaleDateTimeFromDate(p_networkStockTxnVO.getCreatedOn()) + "]");
        strBuff.append(" [Transfer By:" + p_networkStockTxnVO.getCreatedBy() + "]");
        strBuff.append(" [Approved By:" + p_networkStockTxnVO.getModifiedBy() + "]");
        strBuff.append("[OTHER INFO = " + BTSLUtil.NullToString(p_networkStockTxnVO.getOtherInfo()) + "] ");
        _Filelogger.info(" ", strBuff.toString());
    }

    public static void log(NetworkStockTxnVO1 p_networkStockTxnVO) {
        StringBuilder strBuff = new StringBuilder();
        strBuff.append(" [Transfer ID:" + p_networkStockTxnVO.getTxnNo() + "]");
        strBuff.append(" [Reference ID:" + p_networkStockTxnVO.getReferenceNo() + "]");
        strBuff.append(" [User ID:" + p_networkStockTxnVO.getUserID() + "]");
        strBuff.append(" [Network ID:" + p_networkStockTxnVO.getNetworkCode() + "]");
        strBuff.append(" [Network For:" + p_networkStockTxnVO.getNetworkFor() + "]");
        strBuff.append(" [Product Code:" + p_networkStockTxnVO.getProductCode() + "]");
        strBuff.append(" [Requested Qty:" + p_networkStockTxnVO.getRequestedQuantity() + "]");
        strBuff.append(" [Transfer Qty:" + p_networkStockTxnVO.getApprovedQuantity() + "]");
        strBuff.append(" [Previous Bal:" + p_networkStockTxnVO.getPreviousStock() + "]");
        strBuff.append(" [Post Bal:" + p_networkStockTxnVO.getPostStock() + "]");
        strBuff.append(" [Entry Type:" + p_networkStockTxnVO.getTxnType() + "]");
        strBuff.append(" [Type:" + p_networkStockTxnVO.getStockType() + "]");
        strBuff.append(" [Transfer Category:" + p_networkStockTxnVO.getTxnCategory() + "]");
        strBuff.append(" [Transfer Type:" + p_networkStockTxnVO.getEntryType() + "]");
        strBuff.append(" [Transfer On:" + BTSLDateUtil.getLocaleDateTimeFromDate(p_networkStockTxnVO.getCreatedOn()) + "]");
        strBuff.append(" [Transfer By:" + p_networkStockTxnVO.getCreatedBy() + "]");
        strBuff.append(" [Approved By:" + p_networkStockTxnVO.getModifiedBy() + "]");
        strBuff.append("[OTHER INFO = " + BTSLUtil.NullToString(p_networkStockTxnVO.getOtherInfo()) + "] ");
        _Filelogger.info(" ", strBuff.toString());
    }
}
