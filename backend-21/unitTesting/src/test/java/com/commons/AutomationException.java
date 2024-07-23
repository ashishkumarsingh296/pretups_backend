package com.commons;

import org.apache.log4j.spi.ErrorCode;

public class AutomationException extends Exception {

	private static final long serialVersionUID = 1L;
	private final ErrorCode code;
    public AutomationException(String message) {
        super();
        this.code = null;
    }
    public AutomationException(Throwable cause) {
        super();
        this.code = null;
    }
    public AutomationException(String message, Throwable cause) {
        super(message, cause);
        this.code = null;
    }
    public AutomationException(String message, Throwable cause, ErrorCode code) {
        super(message, cause);
        this.code = code;
    }
    public AutomationException(String message, ErrorCode code) {
        super(message);
        this.code = code;
    }
    public AutomationException(Throwable cause, ErrorCode code) {
        super(cause);
        this.code = code;
    }
    public ErrorCode getCode() {
        return this.code;
    }
	
}
