/*
 * Created on Mar 1, 2005
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.selftopup.common;

/**
 * @author abhijit.chauhan
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class BaseException extends Exception {
    private String _errorCode = null;

    public BaseException(String p_errorCode) {
        _errorCode = p_errorCode;
    }

    /**
     * @return Returns the _errorCode.
     */
    public String getErrorCode() {
        return _errorCode;
    }

    /**
     * @param code
     *            The _errorCode to set.
     */
    public void setErrorCode(String code) {
        _errorCode = code;
    }
}