package com.btsl.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.EncodingException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

/**
 * Servlet Filter implementation class XssWrapper
 * 
 * Created/Modified by Created/Modified on History
 * ----------------------------------------------------------------------------
 * ----
 * Deepak Arora Feb, 2009 Initial creation
 * ----------------------------------------------------------------------------
 * ----
 * Copyright(c) 2009 Comviva Tech. Ltd.
 */

public class XssWrapper extends HttpServletRequestWrapper {
    private static final Log _log = LogFactory.getLog(XssWrapper.class.getName());

    public XssWrapper(HttpServletRequest request) {
        super(request);
    }

    /**
     * Wrapper for ESAPI.encoder().canonicalize()
     */
    private String canonicalize(String value) {
        final String METHOD_NAME = "canonicalize";
        try {
            if (value == null) {
                return null;
            } else if (value.contains("<") || (value.contains(">"))) {
                return ESAPI.encoder().encodeForURL(value);
            } else {
                return value;
            }
        } catch (EncodingException error) {
            _log.errorTrace(METHOD_NAME, error);
            return null;
        }
    }

    /**
     * Wrapper for ESAPI.encoder().encodeForURL()
     */
    private String encodeForURL(String value) {
        final String METHOD_NAME = "encodeForURL";
        try {
            if (value == null) {
                return null;
            } else if (value.contains("<") || (value.contains(">"))) {
                return ESAPI.encoder().encodeForURL(value);
            } else {
                return value;
            }
        } catch (EncodingException error) {
            _log.errorTrace(METHOD_NAME, error);
            return null;
        }
    }

    @Override
    public String getQueryString() {
        String dirtyQuery = super.getQueryString();
        return encodeForURL(canonicalize(dirtyQuery));
    }
}
