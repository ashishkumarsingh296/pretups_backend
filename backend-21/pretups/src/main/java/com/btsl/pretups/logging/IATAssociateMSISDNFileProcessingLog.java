/**
 * @# IATAssociateMSISDNFileProcessingLog.java
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

public class IATAssociateMSISDNFileProcessingLog {

    private static Log _Filelogger = LogFactory.getLog(IATAssociateMSISDNFileProcessingLog.class.getName());

    private IATAssociateMSISDNFileProcessingLog() {
        super();
    }

    public static void log(String p_action, String p_msisdn, String p_message, String p_result, String p_otherInfo) {
        StringBuffer message = new StringBuffer();
        message.append("[ACTION = " + BTSLUtil.NullToString(p_action) + "] ");
        message.append("[MSISDN = " + BTSLUtil.NullToString(p_msisdn) + "] ");
        message.append("[RESULT = " + BTSLUtil.NullToString(p_result) + "] ");
        message.append("[MESSAGE = " + BTSLUtil.NullToString(p_message) + "] ");
        message.append("[OTHER INFO = " + BTSLUtil.NullToString(p_otherInfo) + "] ");
        _Filelogger.info(" ", message);
    }

    /**
     * Prints log for Activation Mapping. It prints NA as value if value is not
     * availbel.
     * 
     * @author babu.kunwar
     * @param userId
     * @param retailerMSISDN
     * @param subcriberMSISDN
     * @param msg_remark
     * @param otherInfo
     */
    public static void activationMappingLog(String userId, String retailerMSISDN, String subcriberMSISDN, String msg_remark, String otherInfo) {
        StringBuffer message = new StringBuffer();
        message.append("[User ID  = " + BTSLUtil.NullToString(userId) + "] ");
        message.append("[Retailer MSISDN = " + BTSLUtil.NullToString(retailerMSISDN) + "] ");
        message.append("[Subscriber MSISDN = " + BTSLUtil.NullToString(subcriberMSISDN) + "] ");
        message.append("[Message = " + BTSLUtil.NullToString(msg_remark) + "] ");
        message.append("[OTHER INFO = " + BTSLUtil.NullToString(otherInfo) + "] ");
        _Filelogger.info(" ", message);
    }

}
