package com.btsl.user.businesslogic;

import lombok.Getter;

@Getter
public class VMSBaseException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1346941902309254262L;
    private final String errorCode;

    public VMSBaseException(String errorCode, String msg) {
        super(msg);
        this.errorCode = errorCode;
    }

    
    public VMSBaseException(String errorCode, Throwable cause) {
    	 super(errorCode, cause);
         this.errorCode = errorCode;
    }

}
