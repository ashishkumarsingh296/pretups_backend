package com.btsl.pretups.logging;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

public class LMBForceSettleLog {
    private static Log _Filelogger = LogFactory.getLog(LMBForceSettleLog.class.getName());

    /**
	 * 
	 */
    private LMBForceSettleLog() {
        super();
    }

    public static void log(String p_fileName, String p_msisdn, String p_message, String p_result, String p_otherInfo) {
        StringBuffer message = new StringBuffer();
        message.append("[FILE NAME = " + BTSLUtil.NullToString(p_fileName) + "] ");
        message.append("[MSISDN = " + BTSLUtil.NullToString(p_msisdn) + "] ");
        message.append("[MESSAGE = " + BTSLUtil.NullToString(p_message) + "] ");
        message.append("[RESULT = " + BTSLUtil.NullToString(p_result) + "] ");
        message.append("[OTHER INFO = " + BTSLUtil.NullToString(p_otherInfo) + "] ");
        _Filelogger.info(" ", message);
    }

    public static void main(String[] args) {
    }
}
