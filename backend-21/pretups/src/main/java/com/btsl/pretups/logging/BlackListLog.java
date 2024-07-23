/**
 * @(#)BlackListLog.java
 *                       Copyright(c) 2006, Bharti Telesoft Ltd.
 *                       All Rights Reserved
 * 
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       Author Date History
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       Amit Ruwali 13/04/2006 Initial Creation
 * 
 *                       This class is used as a single line logger for
 *                       black/unblack listing module
 * 
 */

package com.btsl.pretups.logging;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

public class BlackListLog {
    private static Log _Filelogger = LogFactory.getLog(BlackListLog.class.getName());

    private BlackListLog() {
        super();
    }

    public static void log(String p_moduleName, String p_fileName, String p_msisdn, String p_message, String p_result, String p_otherInfo) {
        StringBuffer message = new StringBuffer();
        message.append("[MODULE = " + BTSLUtil.NullToString(p_moduleName) + "] ");
        message.append("[SOURCE = " + BTSLUtil.NullToString(p_fileName) + "] ");
        message.append("[MSISDN = " + BTSLUtil.NullToString(p_msisdn) + "] ");
        message.append("[MESSAGE = " + BTSLUtil.NullToString(p_message) + "] ");
        message.append("[RESULT = " + BTSLUtil.NullToString(p_result) + "] ");
        message.append("[OTHER INFO = " + BTSLUtil.NullToString(p_otherInfo) + "] ");
        _Filelogger.info(" ", message);
    }
}
