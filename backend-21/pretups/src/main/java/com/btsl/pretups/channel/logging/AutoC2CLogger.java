package com.btsl.pretups.channel.logging;

/*
 * @(#)AutoC2CLogger.java.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 05/09/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Class for logging all the balance related Logs for channel user
 */

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.processes.businesslogic.LowBalanceAlertVO;

public class AutoC2CLogger {

    private static Log _log = LogFactory.getFactory().getInstance(AutoC2CLogger.class.getName());

    private AutoC2CLogger() {
		// TODO Auto-generated constructor stub
	}
    public static void log(LowBalanceAlertVO p_lowBalanceAlertVO, String p_status, String p_remark) {
        final String METHOD_NAME = "log";
        final StringBuffer strBuff = new StringBuffer(p_lowBalanceAlertVO.toString());
        try {
            strBuff.append("Status :" + p_status);
            strBuff.append("Reason :" + p_remark);
            _log.info("", strBuff.toString());
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("log", p_lowBalanceAlertVO.getMsisdn(), " Exception :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AutoC2CLogger[log]", p_lowBalanceAlertVO.getMsisdn(),
                "", "", "Not able to log info getting Exception=" + e.getMessage());
        }
    }
}
