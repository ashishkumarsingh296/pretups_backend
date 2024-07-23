/*
 * Created on Apr 8, 2009
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.btsl.pretups.logging;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

/**
 * @author dhiraj.tiwari
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class HeartBeatSentLog {

    private static Log _log = LogFactory.getFactory().getInstance(HeartBeatSentLog.class.getName());

    /**
     * ensures no instantiation
     */
    private HeartBeatSentLog(){
    	
    }
    
    public static void log(String p_str) {
        final String METHOD_NAME = "log";
        try {
            _log.info("", p_str + "\n");
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            /*
             * _log.error("log",p_transferID,
             * " Not able to log info, getting Exception :"+e.getMessage());
             * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,
             * EventStatusI
             * .RAISED,EventLevelI.FATAL,"TransactionLog[log]",p_transferID
             * ,"","","Not able to log info for Transfer ID:"+p_transferID+
             * " ,getting Exception="+e.getMessage());
             */
        }
    }
}
