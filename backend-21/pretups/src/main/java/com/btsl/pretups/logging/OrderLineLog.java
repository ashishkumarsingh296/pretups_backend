package com.btsl.pretups.logging;

/*
 * @(#)OrderLineLog.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Kapil Mehta 13/02/09 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2009 Bharti Telesoft Ltd.
 * Class for maintain logs
 */

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class OrderLineLog {
    private static Log _log = LogFactory.getFactory().getInstance(OrderLineLog.class.getName());

    /**
   	 * ensures no instantiation
   	 */
    private OrderLineLog(){
    	
    }
    /**
     * Used to log the information.
     * 
     * @param p_requestVO
     */

    public static void log(String req_msisdn, String rec_msisdn, String mobileLine, String fixLine) {
        final String METHOD_NAME = "log";
        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append("[Requester MSISDN:" + req_msisdn + "]");
            strBuff.append("[Parent MSISDN:" + rec_msisdn + "]");
            strBuff.append("[Requested MobileLine:" + mobileLine + "]");
            strBuff.append("[Requested FixLine:" + fixLine + "]");

            _log.info("", strBuff.toString());
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("log", "", " Not able to log info in OrderLineLog, getting Exception :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OrderLineLog[log]", "", req_msisdn, "", "Not able to log info in OrderLineLog for MSISDN:" + req_msisdn + " ,getting Exception=" + e.getMessage());
        }
    }
}