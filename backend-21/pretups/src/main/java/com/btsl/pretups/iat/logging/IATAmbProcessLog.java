package com.btsl.pretups.iat.logging;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

/**
 * @(#)IATAmbProcessLog
 *                      Copyright(c) 2009, Bharti Telesoft Ltd.
 *                      All Rights Reserved
 * 
 *                      --------------------------------------------------------
 *                      -----------------------------------------
 *                      Author Date History
 *                      --------------------------------------------------------
 *                      -----------------------------------------
 *                      vikascyadav july 06, 2009 Initial Creation
 * 
 */
public class IATAmbProcessLog {
    private static Log _logger = LogFactory.getLog(IATAmbProcessLog.class.getName());

    private IATAmbProcessLog() {
        super();
    }

    public static void log(String p_stage, String p_txnID, String p_IATFinalStatus, int p_successTxns, int p_failedTxns, int p_totalAbgTxns, int p_stillAbg, String p_serviceType, String p_iatType) {
        if (_logger.isDebugEnabled()) {
            _logger.debug("IATAmbProcessLog", " Entered");
        }

        StringBuffer message = new StringBuffer();
        message.append("[STAGE = " + BTSLUtil.NullToString(p_stage) + "] ");
        message.append("[CURRENT IATXNID = " + BTSLUtil.NullToString(p_txnID) + "] ");
        message.append("[IATSTATUS = " + BTSLUtil.NullToString(p_IATFinalStatus) + "] ");
        message.append("[SUCCESS TXN TILL NOW= " + p_successTxns + "] ");
        message.append("[FAILEDTXN TILL NOW= " + p_failedTxns + "] ");
        message.append("[TOTALABGTXNS = " + p_totalAbgTxns + "] ");
        message.append("[REMAINSABG = " + p_stillAbg + "] ");
        message.append("[SERVICETYPE = " + p_serviceType + "] ");
        if (BTSLUtil.isNullString(p_iatType)) {
            message.append("[ZEBRATYPE= Receiver]");
        } else {
            message.append("[ZEBRATYPE= Sender]");
        }

        _logger.info(" ", message);

        if (_logger.isDebugEnabled()) {
            _logger.debug("IATAmbProcessLog", "Exited");
        }
    }
}
