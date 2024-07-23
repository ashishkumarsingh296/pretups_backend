package com.selftopup.common;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Servlet Filter implementation class XssFilter
 * 
 * Created/Modified by Created/Modified on History
 * ----------------------------------------------------------------------------
 * ----
 * Deepak Arora Feb, 2009 Initial creation
 * ----------------------------------------------------------------------------
 * ----
 * Copyright(c) 2009 Comviva Tech. Ltd.
 */
public class XssFilter implements Filter {

    /**
     * Default constructor.
     */
    public XssFilter() {
    }

    /**
     * @see Filter#init(FilterConfig)
     */
    public void init(FilterConfig config) throws ServletException {
    }

    /**
     * @see Filter#destroy()
     */
    public void destroy() {
    }

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        XssWrapper wrap = new XssWrapper((HttpServletRequest) request);
        chain.doFilter(wrap, response);
    }

}
