/*
 * Created on May 4, 2004
 * 
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.btsl.pretups.logging;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

/**
 * @author gurjeet.bedi
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class IccFileProcessLog {
    private static Log _Filelogger = LogFactory.getLog(IccFileProcessLog.class.getName());

    /**
	 * 
	 */
    private IccFileProcessLog() {
        super();
    }

    public static void log(String p_file, String p_userID, String p_locationCode, String p_simId, long p_totRecords, boolean p_status, String p_message) {
        StringBuffer message = new StringBuffer();
        message.append("[FILE NAME = " + BTSLUtil.NullToString(p_file) + "] ");
        message.append("[NETWORK CODE = " + BTSLUtil.NullToString(p_locationCode) + "] ");
        message.append("[USER ID = " + BTSLUtil.NullToString(p_userID) + "] ");
        message.append("[SIM ID = " + BTSLUtil.NullToString(p_simId) + "] ");
        message.append("[TOTAL RECORDS = " + p_totRecords + "] ");
        message.append("[STATUS = " + p_status + "] ");
        message.append("[ERROR MESSAGE = " + BTSLUtil.NullToString(p_message) + "]");
        _Filelogger.info(" ", message);
    }

    public static void main(String[] args) {
    }
}
