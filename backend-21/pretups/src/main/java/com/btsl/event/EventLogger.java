/*
 * Created on Mar 16, 2004
 * 
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.btsl.event;

import org.apache.log4j.Logger;

/**
 * @author Sanjay
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class EventLogger {

    private static Logger _evLogger = Logger.getLogger(EventLogger.class.getName());

    private EventLogger() {
        super();
        // TODO Auto-generated constructor stub
    }

    public static void fatalLog(String logMessage) {
        _evLogger.fatal(logMessage);
    }

    public static void debugLog(String logMessage) {
    	if(_evLogger.isDebugEnabled()){
    		_evLogger.debug(logMessage);
    	}
    }
}