package com.btsl.logging.impl;

import java.io.Serializable;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.btsl.logging.Log;
import com.btsl.util.BTSLUtil;
import org.apache.commons.lang3.StringUtils;


public class Log4JLogger implements Log, Serializable {
    private final String LOG_METHOD_MESSAGE_SEPARATOR = " :: ";

    public Log4JLogger() {
        logger = null;
        name = null;
    }

    public Log4JLogger(String name) {
        logger = null;
        this.name = null;
        this.name = name;
        logger = getLogger();
    }

    public Log4JLogger(Logger logger) {
        this.logger = null;
        name = null;
        name = logger.getName();
        this.logger = logger;
    }

    private void trace(Object message) {
        /*
         * if(is12)
         * getLogger().log(FQCN, (Priority)Level.DEBUG, message, null);
         * else
         */
      //  getLogger().log(FQCN, (Priority) Level.DEBUG, message, null);
        getLogger().log(Level.TRACE, message, null);
        
    }

    public void trace(Object methodName, Object message) {
        trace(methodName + LOG_METHOD_MESSAGE_SEPARATOR + message);
    }

    public void trace(Object message, Throwable t) {
        /*
         * if(is12)
         * getLogger().log(FQCN, (Priority)Level.DEBUG, message, t);
         * else
         */
    	getLogger().log(Level.TRACE, message, t);
        
    }

    private void debug(Object message) {
        /*
         * if(is12)
         * getLogger().log(FQCN, (Priority)Level.DEBUG, message, null);
         * else
         */
    	if(toSkipLog(message)) {
    		return;
    	}
        getLogger().log(Level.DEBUG, message, null);
    }

    public void debug(Object methodName, Object message) {
        debug(methodName + LOG_METHOD_MESSAGE_SEPARATOR + message);
    }

    public void debug(Object methodName, Object referenceID, Object message) {
        debug(methodName + LOG_METHOD_MESSAGE_SEPARATOR + referenceID + LOG_METHOD_MESSAGE_SEPARATOR + message);
    }

    public void debug(Object message, Throwable t) {
        /*
         * if(is12)
         * getLogger().log(FQCN, (Priority)Level.DEBUG, message, t);
         * else
         */
       // getLogger().log(FQCN, (Priority) Level.DEBUG, message, t);
        getLogger().log(Level.DEBUG, message, null);
    }

    private void info(Object message) {
        /*
         * if(is12)
         * getLogger().log(FQCN, (Priority)Level.INFO, message, null);
         * else
         */
        //getLogger().log(FQCN, (Priority) Level.INFO, message, null);
        getLogger().log(Level.INFO, message, null);
    }

    public void info(Object methodName, Object message) {
        info(methodName + LOG_METHOD_MESSAGE_SEPARATOR + message);
    }

    public void info(Object methodName, Object referenceID, Object message) {
        info(methodName + LOG_METHOD_MESSAGE_SEPARATOR + referenceID + LOG_METHOD_MESSAGE_SEPARATOR + message);
    }

    public void info(Object message, Throwable t) {
        /*
         * if(is12)
         * getLogger().log(FQCN, (Priority)Level.INFO, message, t);
         * else
         */
        //getLogger().log(FQCN, (Priority) Level.INFO, message, t);
        getLogger().log(Level.INFO, message, t);
    }

    private void warn(Object message) {
        /*
         * if(is12)
         * getLogger().log(FQCN, (Priority)Level.WARN, message, null);
         * else
         */
        //getLogger().log(FQCN, (Priority) Level.WARN, message, null);
        getLogger().log(Level.WARN, message, null);
    }

    public void warn(Object message, Object referenceID) {
        warn(referenceID + " " + message);
    }

    public void warn(Object methodName, Object referenceID, Object message) {
        warn(methodName + LOG_METHOD_MESSAGE_SEPARATOR + referenceID + LOG_METHOD_MESSAGE_SEPARATOR + message);
    }

    public void warn(Object message, Throwable t) {
        /*
         * if(is12)
         * getLogger().log(FQCN, (Priority)Level.WARN, message, t);
         * else
         */
       // getLogger().log(FQCN, (Priority) Level.WARN, message, t);
        getLogger().log(Level.WARN, message, t);
    }

    private void error(Object message) {
        /*
         * if(is12)
         * getLogger().log(FQCN, (Priority)Level.ERROR, message, null);
         * else
         */
       // getLogger().log(FQCN, (Priority) Level.ERROR, message, null);
        getLogger().log(Level.ERROR, message, null);
    }

    public void error(Object methodName, Object message) {
        error(methodName + LOG_METHOD_MESSAGE_SEPARATOR + message);
    }

    public void error(Object methodName, Object referenceID, Object message) {
        error(methodName + LOG_METHOD_MESSAGE_SEPARATOR + referenceID + LOG_METHOD_MESSAGE_SEPARATOR + message);
    }

    public void error(Object message, Throwable t) {
        /*
         * if(is12)
         * getLogger().log(FQCN, (Priority)Level.ERROR, message, t);
         * else
         */
        //getLogger().log(FQCN, (Priority) Level.ERROR, message, t);
        getLogger().log(Level.ERROR, message, t);
    }

    private void fatal(Object message) {
        /*
         * if(is12)
         * getLogger().log(FQCN, (Priority)Level.FATAL, message, null);
         * else
         */
        //getLogger().log(FQCN, (Priority) Level.FATAL, message, null);
    	if(toSkipLog(message)) {
    		return;
    	}
        getLogger().log(Level.FATAL, message, null);
    }

    public void fatal(Object methodName, Object message) {
        fatal(methodName + LOG_METHOD_MESSAGE_SEPARATOR + message);
    }

    public void fatal(Object methodName, Object referenceID, Object message) {
        fatal(methodName + LOG_METHOD_MESSAGE_SEPARATOR + referenceID + LOG_METHOD_MESSAGE_SEPARATOR + message);
    }

    public void fatal(Object message, Throwable t) {
        /*
         * if(is12)
         * getLogger().log(FQCN, (Priority)Level.FATAL, message, t);
         * else
         */
    
        getLogger().log(Level.FATAL, message, t);
    }

    public Logger getLogger() {
        if (logger == null)
            logger = LogManager.getLogger();
        return logger;
    }

    public boolean isDebugEnabled() {
        return getLogger().isDebugEnabled();
    }

    public boolean isErrorEnabled() {
        /*
         * if(is12)
         * return getLogger().isEnabledFor((Priority)Level.ERROR);
         * else
         */
        //return getLogger().isEnabledFor((Priority) Level.ERROR);
        return  getLogger().isEnabled( Level.ERROR);
    }

    public boolean isFatalEnabled() {
        /*
         * if(is12)
         * return getLogger().isEnabledFor((Priority)Level.FATAL);
         * else
         */
       // return getLogger().isEnabledFor((Priority) Level.FATAL);
        return  getLogger().isEnabled( Level.FATAL);
    }

    public boolean isInfoEnabled() {
        return getLogger().isInfoEnabled();
    }

    public boolean isTraceEnabled() {
        return getLogger().isTraceEnabled();
    }

    public boolean isWarnEnabled() {
        /*
         * if(is12)
         * return getLogger().isEnabledFor((Priority)Level.WARN);
         * else
         */
       // return getLogger().isEnabledFor((Priority) Level.WARN);
        return  getLogger().isEnabled( Level.WARN);
    }

    static Class _mthclass$(String x0) {
        try {
            return Class.forName(x0);
        } catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    private static final String FQCN;
    private static final boolean is12;
    private transient Logger logger;
    private String name;

    static {
        FQCN = (com.btsl.logging.impl.Log4JLogger.class).getName();
        is12 = (org.apache.log4j.Priority.class).isAssignableFrom(org.apache.log4j.Level.class);
    }

    public void errorTrace(Object obj, Throwable throwable) {
        getLogger().error(obj, throwable);

    }
    
    private boolean toSkipLog(Object message) {
    	String message1 = null;
    	if(message!= null) {
    		message1 = (String)message.toString();
    		}
    	String toSkipLogs = com.btsl.util.Constants.getProperty("LOGS_TO_SKIP");
    	if(!BTSLUtil.isNullString(toSkipLogs)) {
    		String toSkipLogsArr[] = toSkipLogs.split(("\\,"));
    		if(message1 != null && toSkipLogsArr.length>0){
        		for(String testStr : toSkipLogsArr) {
        			if(StringUtils.containsIgnoreCase(message1, testStr)) {
        				return true;
        			}
        		}
        	}
    	}
    	return false;
    }

}
