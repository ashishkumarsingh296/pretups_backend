package com.btsl.pretups.channel.logging;

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.queue.RequestQueueVO;

public class QueueLogger {
    private static Log _log = LogFactory.getFactory().getInstance(QueueLogger.class.getName());

    /**
	 * ensures no instantiation
	 */
    private QueueLogger(){
    	
    }
    
    public static void log(String message) {
        final String METHOD_NAME = "log";
        final StringBuffer strBuff = new StringBuffer();
        try {
            strBuff.append("Message :" + message);
            _log.info("", strBuff.toString());
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"AutoO2CLogger[log]",p_lowBalanceAlertVO.getMsisdn(),"","","Not able to log info getting Exception="+e.getMessage());
        }
    }

    /**
     * Used to log the information.
     * 
     * @param p_requestQueueVO
     */
    public static void Outlog(RequestQueueVO p_requestQueueVO) {
        final String METHOD_NAME = "outlog";
        StringBuilder loggerValue= new StringBuilder(); 
        try {
            final StringBuffer strBuff = new StringBuffer();
            strBuff.append("[QueueOut:]");
            if (p_requestQueueVO != null) {
                strBuff.append("[INID:");
                strBuff.append(p_requestQueueVO.getRequestVO().getInstanceID());
                strBuff.append("]");

                strBuff.append("[SER:");
                strBuff.append(p_requestQueueVO.getServiceType());
                strBuff.append("]");

                strBuff.append("[RQID:");
                strBuff.append(p_requestQueueVO.getRequestIDMethod());
                strBuff.append("]");

                strBuff.append("[RQRVT:");
                strBuff.append(p_requestQueueVO.getRequestVO().getRequestStartTime());
                strBuff.append("]");

                strBuff.append("[SM:");
                strBuff.append(p_requestQueueVO.getSenderMsisdn());
                strBuff.append("]");

                strBuff.append("[RM:");
                strBuff.append(p_requestQueueVO.getReceiverMsisdn());
                strBuff.append("]");
            }
            _log.info("", strBuff.toString());
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            loggerValue.setLength(0);
            loggerValue.append(p_requestQueueVO.getRequestVO().getInstanceID());
            loggerValue.append("");
            loggerValue.append(p_requestQueueVO.getRequestIDMethod());
            loggerValue.append(" Not able to log info, getting Exception :" );
            loggerValue.append(e.getMessage());
            _log.error("log",  loggerValue);
            
            loggerValue.setLength(0);
            loggerValue.append(p_requestQueueVO.getRequestVO().getInstanceID());
            loggerValue.append("-");
            loggerValue.append(p_requestQueueVO.getRequestIDMethod());
            StringBuilder handleHandle= new StringBuilder(); 
            handleHandle.append("Not able to log info for Transaction ID - Request ID:");
            handleHandle.append(p_requestQueueVO.getRequestVO().getInstanceID());
            handleHandle.append("-");
            handleHandle.append(p_requestQueueVO.getRequestIDMethod());
            handleHandle.append(" ,getting Exception=");
            handleHandle.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "QueueLogger[log]", loggerValue.toString() , "", "",  handleHandle.toString());
        }
    }

    /**
     * Used to log the information.
     * 
     * @param p_requestQueueVO
     */
    public static void INlog(RequestQueueVO p_requestQueueVO) {
        final String METHOD_NAME = "INlog";
        StringBuilder loggerValue= new StringBuilder(); 
        try {
            final StringBuffer strBuff = new StringBuffer();
            strBuff.append("[QueueIN:]");
            if (p_requestQueueVO != null) {
                strBuff.append("[INID:");
                strBuff.append(p_requestQueueVO.getRequestVO().getInstanceID());
                strBuff.append("]");

                strBuff.append("[SER:");
                strBuff.append(p_requestQueueVO.getServiceType());
                strBuff.append("]");

                strBuff.append("[RQID:");
                strBuff.append(p_requestQueueVO.getRequestIDMethod());
                strBuff.append("]");

                strBuff.append("[RQRVT:");
                strBuff.append(p_requestQueueVO.getRequestVO().getRequestStartTime());
                strBuff.append("]");

                strBuff.append("[SM:");
                strBuff.append(p_requestQueueVO.getSenderMsisdn());
                strBuff.append("]");

                strBuff.append("[RM:");
                strBuff.append(p_requestQueueVO.getReceiverMsisdn());
                strBuff.append("]");
            }
            _log.info("", strBuff.toString());
        } catch (Exception e) {
        
            _log.errorTrace(METHOD_NAME, e);
            loggerValue.setLength(0);
            loggerValue.append(p_requestQueueVO.getRequestVO().getInstanceID());
            loggerValue.append(p_requestQueueVO.getRequestIDMethod());
            loggerValue.append( " Not able to log info, getting Exception :");
            loggerValue.append(e.getMessage());
            _log.error("log",  loggerValue);
            
            loggerValue.setLength(0);
            loggerValue.append("Not able to log info for Transaction ID - Request ID:");
            loggerValue.append(p_requestQueueVO.getRequestVO().getInstanceID());
            loggerValue.append("-");
            loggerValue.append(p_requestQueueVO.getRequestIDMethod());
            loggerValue.append(" ,getting Exception=");
            loggerValue.append(e.getMessage());
            StringBuilder handles= new StringBuilder(); 
            handles.append(p_requestQueueVO.getRequestVO().getInstanceID());
            handles.append("-");
            handles.append(p_requestQueueVO.getRequestIDMethod());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "QueueLogger[log]", handles.toString(), "", "",  loggerValue.toString() );
        }
    }

}