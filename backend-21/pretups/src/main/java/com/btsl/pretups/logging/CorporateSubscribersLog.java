package com.btsl.pretups.logging;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

/**
 * @(#)CorporateSubscribersLog.java
 *                                  Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                  All Rights Reserved
 * 
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  ---------
 *                                  Author Date History
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  ---------
 *                                  Amit Singh 03/03/2006 Initial Creation
 * 
 *                                  This class is used to handle the Corporate
 *                                  Single line Logs
 * 
 */
public class CorporateSubscribersLog {
    private static Log _Filelogger = LogFactory.getLog(CorporateSubscribersLog.class.getName());

    /**
	 * 
	 */
    private CorporateSubscribersLog() {
        super();
    }

    public static void log(String p_action, String p_userID, String p_msisdn, String p_message, String p_result, String p_otherInfo) {
        StringBuffer message = new StringBuffer();
        message.append("[ACTION = " + BTSLUtil.NullToString(p_action) + "] ");
        message.append("[LOGGED IN USER'S ID = " + BTSLUtil.NullToString(p_userID) + "] ");
        message.append("[MSISDN = " + BTSLUtil.NullToString(p_msisdn) + "] ");
        message.append("[MESSAGE = " + BTSLUtil.NullToString(p_message) + "] ");
        message.append("[RESULT = " + BTSLUtil.NullToString(p_result) + "] ");
        message.append("[OTHER INFO = " + BTSLUtil.NullToString(p_otherInfo) + "] ");
        _Filelogger.info(" ", message);
    }

    public static void main(String[] args) {
    }
}
