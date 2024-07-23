package com.selftopup.logging.impl;

import java.io.Serializable;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import com.selftopup.logging.Log;

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
        if (is12)
            getLogger().log(FQCN, (Priority) Level.DEBUG, message, null);
        else
            getLogger().log(FQCN, Level.DEBUG, message, null);
    }

    public void trace(Object methodName, Object message) {
        trace(methodName + LOG_METHOD_MESSAGE_SEPARATOR + message);
    }

    public void trace(Object message, Throwable t) {
        if (is12)
            getLogger().log(FQCN, (Priority) Level.DEBUG, message, t);
        else
            getLogger().log(FQCN, Level.DEBUG, message, t);
    }

    private void debug(Object message) {
        if (is12)
            getLogger().log(FQCN, (Priority) Level.DEBUG, message, null);
        else
            getLogger().log(FQCN, Level.DEBUG, message, null);
    }

    public void debug(Object methodName, Object message) {
        debug(methodName + LOG_METHOD_MESSAGE_SEPARATOR + message);
    }

    public void debug(Object methodName, Object referenceID, Object message) {
        debug(methodName + LOG_METHOD_MESSAGE_SEPARATOR + referenceID + LOG_METHOD_MESSAGE_SEPARATOR + message);
    }

    public void debug(Object message, Throwable t) {
        if (is12)
            getLogger().log(FQCN, (Priority) Level.DEBUG, message, t);
        else
            getLogger().log(FQCN, Level.DEBUG, message, t);
    }

    private void info(Object message) {
        if (is12)
            getLogger().log(FQCN, (Priority) Level.INFO, message, null);
        else
            getLogger().log(FQCN, Level.INFO, message, null);
    }

    public void info(Object methodName, Object message) {
        info(methodName + LOG_METHOD_MESSAGE_SEPARATOR + message);
    }

    public void info(Object methodName, Object referenceID, Object message) {
        info(methodName + LOG_METHOD_MESSAGE_SEPARATOR + referenceID + LOG_METHOD_MESSAGE_SEPARATOR + message);
    }

    public void info(Object message, Throwable t) {
        if (is12)
            getLogger().log(FQCN, (Priority) Level.INFO, message, t);
        else
            getLogger().log(FQCN, Level.INFO, message, t);
    }

    private void warn(Object message) {
        if (is12)
            getLogger().log(FQCN, (Priority) Level.WARN, message, null);
        else
            getLogger().log(FQCN, Level.WARN, message, null);
    }

    public void warn(Object message, Object referenceID) {
        warn(referenceID + " " + message);
    }

    public void warn(Object methodName, Object referenceID, Object message) {
        warn(methodName + LOG_METHOD_MESSAGE_SEPARATOR + referenceID + LOG_METHOD_MESSAGE_SEPARATOR + message);
    }

    public void warn(Object message, Throwable t) {
        if (is12)
            getLogger().log(FQCN, (Priority) Level.WARN, message, t);
        else
            getLogger().log(FQCN, Level.WARN, message, t);
    }

    private void error(Object message) {
        if (is12)
            getLogger().log(FQCN, (Priority) Level.ERROR, message, null);
        else
            getLogger().log(FQCN, Level.ERROR, message, null);
    }

    public void error(Object methodName, Object message) {
        error(methodName + LOG_METHOD_MESSAGE_SEPARATOR + message);
    }

    public void error(Object methodName, Object referenceID, Object message) {
        error(methodName + LOG_METHOD_MESSAGE_SEPARATOR + referenceID + LOG_METHOD_MESSAGE_SEPARATOR + message);
    }

    public void error(Object message, Throwable t) {
        if (is12)
            getLogger().log(FQCN, (Priority) Level.ERROR, message, t);
        else
            getLogger().log(FQCN, Level.ERROR, message, t);
    }

    private void fatal(Object message) {
        if (is12)
            getLogger().log(FQCN, (Priority) Level.FATAL, message, null);
        else
            getLogger().log(FQCN, Level.FATAL, message, null);
    }

    public void fatal(Object methodName, Object message) {
        fatal(methodName + LOG_METHOD_MESSAGE_SEPARATOR + message);
    }

    public void fatal(Object methodName, Object referenceID, Object message) {
        fatal(methodName + LOG_METHOD_MESSAGE_SEPARATOR + referenceID + LOG_METHOD_MESSAGE_SEPARATOR + message);
    }

    public void fatal(Object message, Throwable t) {
        if (is12)
            getLogger().log(FQCN, (Priority) Level.FATAL, message, t);
        else
            getLogger().log(FQCN, Level.FATAL, message, t);
    }

    public Logger getLogger() {
        if (logger == null)
            logger = Logger.getLogger(name);
        return logger;
    }

    public boolean isDebugEnabled() {
        return getLogger().isDebugEnabled();
    }

    public boolean isErrorEnabled() {
        if (is12)
            return getLogger().isEnabledFor((Priority) Level.ERROR);
        else
            return getLogger().isEnabledFor(Level.ERROR);
    }

    public boolean isFatalEnabled() {
        if (is12)
            return getLogger().isEnabledFor((Priority) Level.FATAL);
        else
            return getLogger().isEnabledFor(Level.FATAL);
    }

    public boolean isInfoEnabled() {
        return getLogger().isInfoEnabled();
    }

    public boolean isTraceEnabled() {
        return getLogger().isDebugEnabled();
    }

    public boolean isWarnEnabled() {
        if (is12)
            return getLogger().isEnabledFor((Priority) Level.WARN);
        else
            return getLogger().isEnabledFor(Level.WARN);
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
        FQCN = (com.selftopup.logging.impl.Log4JLogger.class).getName();
        is12 = (org.apache.log4j.Priority.class).isAssignableFrom(org.apache.log4j.Level.class);
    }

    public void errorTrace(Object obj, Throwable throwable) {
        getLogger().error(obj, throwable);

    }
}
