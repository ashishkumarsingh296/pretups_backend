package com.btsl.pretups.logging;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

public class MSISDNBatchPocessingLog {

    private static final Log LOGGER = LogFactory.getFactory().getInstance(MSISDNBatchPocessingLog.class.getName());

    /**
	 * ensures no instantiation
	 */
    private MSISDNBatchPocessingLog() {
        
    }

    public static void log(String p_action, String msisdn, boolean msisdnType) {
        StringBuffer message = new StringBuffer();
        message.append("[ACTION = " + BTSLUtil.NullToString(p_action) + "] ");
        message.append("[MSISDN = " + BTSLUtil.NullToString(msisdn) + "] ");
        if (msisdnType) {
            message.append("[Current MSISDN is not present in system] ");
        } else {
            message.append("[New MSISDN is already present in system for some other user] ");
        }
        LOGGER.info(" ", message);
    }

    public static void main(String[] args) {
    }

}
