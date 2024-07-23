/**
 * @# IATRestrictedMSISDNLog.java
 * 
 *    Created By Created on History
 *    --------------------------------------------------------------------------
 *    ------
 *    Babu Kunwar 09-OCT-2011 Initial creation
 *    --------------------------------------------------------------------------
 *    ------
 *    Copyright(c) 2011 Comviva Technologies Ltd.
 */
package com.btsl.pretups.logging;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

public class IATRestrictedMSISDNLog {

    private static Log _Filelogger = LogFactory.getLog(IATRestrictedMSISDNLog.class.getName());

    /**
	 * 
	 */
    private IATRestrictedMSISDNLog() {
        super();
    }

    public static void log(String p_fileName, String p_msisdn, String p_message, String p_result) {
        StringBuffer message = new StringBuffer();
        message.append("[FILE NAME = " + BTSLUtil.NullToString(p_fileName) + "] ");
        message.append("[MSISDN = " + BTSLUtil.NullToString(p_msisdn) + "] ");
        message.append("[MESSAGE = " + BTSLUtil.NullToString(p_message) + "] ");
        message.append("[RESULT = " + BTSLUtil.NullToString(p_result) + "] ");
        _Filelogger.info(" ", message);
    }

    public static void main(String[] args) {

    }

}
