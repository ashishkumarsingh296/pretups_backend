/*
 * @(#)BulkUploadVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Samna Soin 24/10/2011 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright(c) 2011, Comviva Technologies Ltd.
 */
package com.btsl.pretups.master.businesslogic;

import java.io.Serializable;

public class BulkUploadVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String _lineNumber = null;
    private String _msisdn = null;
    private String _errorCode = null;

    public String getErrorCode() {
        return _errorCode;
    }

    public void setErrorCode(String errorCode) {
        _errorCode = errorCode;
    }

    public String getLineNumber() {
        return _lineNumber;
    }

    public void setLineNumber(String lineNumber) {
        _lineNumber = lineNumber;
    }

    public String getMsisdn() {
        return _msisdn;
    }

    public void setMsisdn(String msisdn) {
        _msisdn = msisdn;
    }
}