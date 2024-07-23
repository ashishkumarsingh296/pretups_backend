package com.selftopup.pretups.logging;

/*
 * @(#)UnauthorizedAccessLog.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 05/09/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Class for logging all the Unauthorized Access log
 */

import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.receiver.RequestVO;

public class UnauthorizedAccessLog {
    private static Log _log = LogFactory.getFactory().getInstance(UnauthorizedAccessLog.class.getName());

    public static void log(RequestVO p_requestVO, String p_errorCode, String p_otherInfo) {
        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append(" [Request ID:" + p_requestVO.getRequestID() + "]");
            strBuff.append(" [Request Gateway Code:" + p_requestVO.getRequestGatewayCode() + "]");
            strBuff.append(" [Request Gateway Type:" + p_requestVO.getRequestGatewayType() + "]");
            strBuff.append(" [MSISDN:" + p_requestVO.getRequestMSISDN() + "]");
            strBuff.append(" [Service port:" + p_requestVO.getServicePort() + "]");
            strBuff.append(" [Login:" + p_requestVO.getLogin() + "]");
            int passwdLength = p_requestVO.getPassword().length();
            String password = "";
            for (int i = 0; i < passwdLength; i++) {
                password = password + "*";
            }
            strBuff.append(" [Password:" + password + "]");

            // strBuff.append(" [Password:"+p_requestVO.getPassword() +"]");
            strBuff.append(" [Source Type:" + p_requestVO.getSourceType() + "]");
            strBuff.append(" [Error Code:" + p_errorCode + "]");
            strBuff.append(" [Other Info:" + p_otherInfo + "]");
            _log.info("", strBuff.toString());
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("log", p_requestVO.getRequestIDStr(), " Not able to log info, getting Exception :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UnauthorizedAccessLog[log]", p_requestVO.getRequestIDStr(), "", "", "Not able to log info in UnauthorizedAccessLog for Request ID:" + p_requestVO.getRequestID() + " ,getting Exception=" + e.getMessage());
        }
    }
}
