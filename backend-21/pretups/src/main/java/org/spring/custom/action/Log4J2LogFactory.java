package org.spring.custom.action;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.owasp.esapi.LogFactory;

public class Log4J2LogFactory implements LogFactory {

    @Override
    public org.owasp.esapi.Logger getLogger(String moduleName) {
        return new Log4J2Logger(LogManager.getLogger(moduleName));
    }

    @Override
    public org.owasp.esapi.Logger getLogger(Class aClass) {
        return new Log4J2Logger(LogManager.getLogger(aClass));

    }
}

class Log4J2Logger implements org.owasp.esapi.Logger {

    private final Logger logger;

    Log4J2Logger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void setLevel(int i) {

    }

    @Override
    public int getESAPILevel() {
        return 0;
    }

    @Override
    public void fatal(EventType eventType, String s) {

    }

    @Override
    public void fatal(EventType eventType, String s, Throwable throwable) {

    }

    @Override
    public boolean isFatalEnabled() {
        return false;
    }

    @Override
    public void error(EventType eventType, String message) {
        logger.error(message);
    }

    @Override
    public void error(EventType eventType, String s, Throwable throwable) {

    }

    @Override
    public boolean isErrorEnabled() {
        return false;
    }

    @Override
    public void warning(EventType eventType, String message) {
        logger.warn(message);
    }

    @Override
    public void warning(EventType eventType, String s, Throwable throwable) {

    }

    @Override
    public boolean isWarningEnabled() {
        return false;
    }

    @Override
    public void info(EventType eventType, String s) {

    }

    @Override
    public void info(EventType eventType, String s, Throwable throwable) {

    }

    @Override
    public boolean isInfoEnabled() {
        return false;
    }

    @Override
    public void debug(EventType eventType, String s) {

    }

    @Override
    public void debug(EventType eventType, String s, Throwable throwable) {

    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public void trace(EventType eventType, String s) {

    }

    @Override
    public void trace(EventType eventType, String s, Throwable throwable) {

    }

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public void always(EventType eventType, String s) {

    }

    @Override
    public void always(EventType eventType, String s, Throwable throwable) {

    }

    // Implement other Logger interface methods as needed, forwarding to Log4j2 logger
}
