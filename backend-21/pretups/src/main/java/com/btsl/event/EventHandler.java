/*
 * Created on Jan 6, 2004
 * 
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.btsl.event;

import java.util.Vector;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/**
 * @author abhijit.chauhan
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class EventHandler {

    private static Log _log = LogFactory.getLog(EventHandler.class.getName());
    public final static String FATAL = "FATAL";
    public final static String MAJOR = "MAJOR";
    public final static String MINOR = "MINOR";
    public final static String TRIVIAL = "TRIVIAL";
    public final static String INFO = "INFO";
    public static final Vector _eventList = new Vector();
    
    /**
     * to ensure no class instantiation 
     */
    private EventHandler(){
    	
    }

    /**
     * 
     * @param p_eventID
     *            Alarm ID in case of O & M
     * @param p_componentName
     * @param p_eventStatus
     *            RAISED,CLEARED,CLEARED BY USER
     * @param p_level
     *            CRITICALITY MINOR/MAJOR/FATAL
     * @param p_processName
     *            Name of the process where event is originated
     * @param p_requestID
     *            Request id or transaction no.
     * @param p_msisdn
     *            MSISDN for which event is generated
     * @param p_networkCode
     *            Network Code for which event is generated
     * @param p_message
     */
    public static void handle(int p_eventID, String p_componentName, String p_eventStatus, int p_level, String p_processName, String p_requestID, String p_msisdn, String p_networkCode, String p_message) {
        if (_log.isDebugEnabled())
//				Following line resolved using ResolveCastAppendIssue Utility
			{
				StringBuilder loggerValue1= new StringBuilder();
				loggerValue1.setLength(0);
				loggerValue1.append( "Entered " );
				loggerValue1.append( p_eventID );
				loggerValue1.append( " " );
				loggerValue1.append( p_componentName );
				loggerValue1.append( " " );
				loggerValue1.append( p_eventStatus );
				loggerValue1.append( " " );
				loggerValue1.append( p_level );
				loggerValue1.append( " " );
				loggerValue1.append( p_processName );
				loggerValue1.append( " " );
				loggerValue1.append( p_requestID );
				loggerValue1.append( " " );
				loggerValue1.append( p_msisdn );
				loggerValue1.append( " " );
				loggerValue1.append( p_networkCode );
				loggerValue1.append( " " );
				loggerValue1.append( p_message);
				_log.debug("handle", loggerValue1);
			}
        final String METHOD_NAME = "handle";
        try {
            int index = p_message.indexOf("\n");
            String levelInfo = "";
            switch (p_level) {
            case EventLevelI.FATAL:
                levelInfo = FATAL;
                break;
            case EventLevelI.MAJOR:
                levelInfo = MAJOR;
                break;
            case EventLevelI.MINOR:
                levelInfo = MINOR;
                break;
            case EventLevelI.TRIVIAL:
                levelInfo = TRIVIAL;
                break;
            case EventLevelI.INFO:
                levelInfo = INFO;
                break;
            default:
                levelInfo = INFO;
            }
            if (index != -1)
                p_message = p_message.substring(0, index);
            
            StringBuffer msg = new StringBuffer("");
            msg.append(p_msisdn);
            msg.append("--");
            msg.append(p_processName);
            msg.append("--");
            msg.append(p_requestID);
            msg.append("--");
            msg.append(p_message);
            msg.append("--");
            msg.append(levelInfo);
            msg.append("--");
            msg.append(p_networkCode);
            
            String message=msg.toString();
            // sending alarm
            logEntry(message);
            String levels = Constants.getProperty("SNMP_EVENT_LEVELS");
            if (levels == null || BTSLUtil.isStringIn(String.valueOf(p_level), levels)) {
                _eventList.add(new EventVO(p_eventID, p_componentName, p_eventStatus, p_level, p_processName, p_requestID, p_msisdn, p_networkCode, message));
                if (_log.isDebugEnabled())
                    _log.debug("handle", "_eventList size: " + _eventList.size());
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
    }// end of handle

    /**
     * handle method
     * 
     * @param p_eventID
     * @param p_eventSourceID
     *            (used for constructing event by combining event id with
     *            interface id og interfaces)
     * @param p_componentName
     * @param p_eventStatus
     * @param p_level
     * @param p_processName
     * @param p_requestID
     * @param p_msisdn
     * @param p_networkCode
     * @param p_message
     */
    public static void handle(int p_eventID, String p_eventSourceID, String p_componentName, String p_eventStatus, int p_level, String p_processName, String p_requestID, String p_msisdn, String p_networkCode, String p_message) {

        handle(p_eventID, p_componentName, p_eventStatus, p_level, p_processName, p_requestID, p_msisdn, p_networkCode, p_message + " " + p_eventSourceID);
        // int eventID=p_eventID;
        // following has been comented for time being since event source id has
        // not been used in alarm conf file
        /*
         * try
         * {
         * //parsing event id
         * eventID=Integer.parseInt(String.valueOf(p_eventID)+p_eventSourceID);
         * }
         * catch(Exception e)
         * {
         * eventID=p_eventID;
         * }
         */
        // sending alarm

    }// end of handle

    /**
     * Method to log event related logs
     * 
     * @param p_message
     */
    public static void logEntry(String p_message) {
        final String METHOD_NAME = "logEntry";
        try {
            EventLogger.debugLog(BTSLUtil.NullToString(p_message));
        } catch (Exception ex1) {
            _log.errorTrace(METHOD_NAME, ex1);
            _log.error("logEntry", " Exception :" + ex1.getMessage());
        }
    }// end of eventHandlerEntry

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        if (args.length != 9) {
            if (_log.isDebugEnabled())
                _log.debug("main", "Usage: EventHandler [eventID][componentName][eventStatus][level][processName][requestID][msisdn][networkCode][message]");
            return;
        }
        int alarmID = Integer.parseInt(args[0]);
        int level = Integer.parseInt(args[3]);
        handle(alarmID, args[1], args[2], level, args[4], args[5], args[6], args[7], args[8]);
    }
}