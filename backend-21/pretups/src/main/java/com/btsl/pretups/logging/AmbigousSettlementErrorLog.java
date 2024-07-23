package com.btsl.pretups.logging;

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

public class AmbigousSettlementErrorLog {

	 private static Log _log = LogFactory.getFactory().getInstance(AmbigousSettlementErrorLog.class.getName());
	 
	/**
	 * ensures no instantiation
	 */
	 private AmbigousSettlementErrorLog(){
		 
	 }
	 
	 public static void log(String p_transferID, String p_status, int counter, String p_otherInfo) {
	        final String METHOD_NAME = "log";
	        try {
	            StringBuffer strBuff = new StringBuffer();
	            strBuff.append("[Line No.:" + counter + "]");
	            strBuff.append("[TID:" + p_transferID + "]");
	            strBuff.append("[STATUS:" + p_status + "]");	           
	            strBuff.append("[OTHERINFO:" + p_otherInfo + "]");
	            _log.info(METHOD_NAME, BTSLUtil.logForgingReqParam(strBuff.toString()));
	        } catch (Exception e) {
	            _log.errorTrace(METHOD_NAME, e);
	            _log.error("log", p_transferID, " Not able to log info, getting Exception :" + e.getMessage());
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AmbigousSettlementErrorLog[log]", p_transferID, "", "", "Not able to log info for Transfer ID:" + p_transferID + " ,getting Exception=" + e.getMessage());
	        }
	    }
	 
	 public static void logFormat(String p_input, String p_separtor , String p_otherInfo, int counter)
	 
	 {
	        final String METHOD_NAME = "log";
	        try {
	            StringBuffer strBuff = new StringBuffer();
	            strBuff.append("[Line No.:" + counter + "]");
	            strBuff.append("[INPUT:" + p_input + "]");
	            strBuff.append("[SEPARATOR:" + p_separtor + "]");	           
	            strBuff.append("[OTHERINFO:" + p_otherInfo + "]");
	            _log.info("", strBuff.toString());
	        } catch (Exception e) {
	            _log.errorTrace(METHOD_NAME, e);
	            _log.error("log", p_input, " Not able to log info, getting Exception :" + e.getMessage());
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AmbigousSettlementErrorLog[log]", p_input, "", "", "Not able to log info for input:" + p_input + " ,getting Exception=" + e.getMessage());
	        }
	    }
}
