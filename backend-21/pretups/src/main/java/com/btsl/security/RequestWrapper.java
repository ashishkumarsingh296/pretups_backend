package com.btsl.security;

import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.AESEncryptionUtil;
import com.btsl.util.Constants;
/**
 * @(#)RequestWrapper.java
 *                         Copyright(c) 2010, Comviva Technologies Limited
 *                         All Rights Reserved
 *                         Action class for interaction between front end and
 *                         backend
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Author Date History
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Sanjeev Sharma 09/02/2010 Initial Creation
 *                         ----------------------------------------------------
 *                         This class wraps the request parameter on the time of using parameter 
 *                         Security validation logic has been moved to EncodingFilter
 *                         --------------------------------------------
 */

public final class RequestWrapper extends HttpServletRequestWrapper {
    /**
     * Commons Logging instance.
     */
    private ServletResponse response = null;
    private HttpServletRequest request = null;
    private Log log = LogFactory.getLog(this.getClass().getName());

    public RequestWrapper(HttpServletRequest servletRequest, ServletResponse servletResponse) {
        super(servletRequest);
        this.request = servletRequest;
        this.response = servletResponse;
    }

    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        
        try{ values[0] = AESEncryptionUtil.aesDecryptor(values[0], Constants.A_KEY) ; }catch(Exception e) {}
        
        return values;
    }

    public String getParameter(String parameter) {
    	String value = super.getParameter(parameter);
       return value;
    }

    public String getHeader(String name) {
        String value = super.getHeader(name);
        return value;
    }


}