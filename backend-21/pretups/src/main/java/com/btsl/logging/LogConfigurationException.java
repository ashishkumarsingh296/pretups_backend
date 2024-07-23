package com.btsl.logging;

public class LogConfigurationException extends RuntimeException {

    public LogConfigurationException() {
        cause = null;
    }

    public LogConfigurationException(String message) {
        super(message);
        cause = null;
    }

    public LogConfigurationException(Throwable cause) {
        this(cause != null ? cause.toString() : null, cause);
    }

    public LogConfigurationException(String message, Throwable cause) {
        super(message + " (Caused by " + cause + ")");
        this.cause = null;
        this.cause = cause;
    }

    public Throwable getCause() {
        return cause;
    }

    protected Throwable cause;
}
