package com.btsl.pretups.processes.o2cwithdraw;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

/**
 * @author vikram.kumar
 * 
 */
public class O2CWdhUserDelProcessLog {

    private static Log _Filelogger = LogFactory.getLog(O2CWdhUserDelProcessLog.class.getName());

    /**
	 * to ensure no class instantiation 
	 */
    private O2CWdhUserDelProcessLog() {
        
    }

    public static void log(String p_action, String p_msisdn, long p_totRecords, String p_result, String p_message, String p_otherInfo) {
        StringBuffer message = new StringBuffer();
        message.append("[ACTION = " + BTSLUtil.NullToString(p_action) + "] ");
        message.append("[MSISDN = " + BTSLUtil.NullToString(p_msisdn) + "] ");
        message.append("[RECORD NO. = " + p_totRecords + "] ");
        message.append("[RESULT = " + BTSLUtil.NullToString(p_result) + "] ");
        message.append("[MESSAGE = " + BTSLUtil.NullToString(p_message) + "] ");
        message.append("[OTHER INFO = " + BTSLUtil.NullToString(p_otherInfo) + "] ");
        _Filelogger.info(" ", message);
    }

    public static void main(String[] args) {
    }

}
