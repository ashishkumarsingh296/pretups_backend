package com.btsl.pretups.channel.logging;

/*
 * @(#)AutoO2CLogger.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Vibhu Trehan 22/08/2013 Initial Creation
 * ------------------------------------------------------------------------
 * 
 * Class for logging all the balance related Logs for channel user
 */

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class AutoO2CLogger {

    private static Log _log = LogFactory.getFactory().getInstance(AutoO2CLogger.class.getName());
    private AutoO2CLogger() {
		// TODO Auto-generated constructor stub
	}
    public static void log(String message) {
        final String METHOD_NAME = "log";
        final StringBuffer strBuff = new StringBuffer(message.toString());
        try {
            strBuff.append("Message :" + message);
            _log.info("", strBuff.toString());
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"AutoO2CLogger[log]",p_lowBalanceAlertVO.getMsisdn(),"","","Not able to log info getting Exception="+e.getMessage());
        }
    }
}
