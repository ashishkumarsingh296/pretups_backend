package com.btsl.security;

/**
 * @(#)CrossScriptingFilter.java
 *                               Copyright(c) 2010, Comviva Technologies Limited
 *                               All Rights Reserved
 *                               Action class for interaction between front end
 *                               and backend
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Author Date History
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Sanjeev Sharma 09/02/2010 Initial Creation
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               --
 */

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

public class CrossScriptingFilter implements Filter {

    private FilterConfig filterConfig;
    private static boolean no_init = true;

    public CrossScriptingFilter() {
        filterConfig = null;
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.Filter#init(jakarta.servlet.FilterConfig)
     */
    @Override
    public void init(FilterConfig filterconfig) throws ServletException {
        this.filterConfig = filterconfig;
        no_init = false;
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.Filter#doFilter(jakarta.servlet.ServletRequest, jakarta.servlet.ServletResponse, jakarta.servlet.FilterChain)
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterchain) throws IOException, ServletException {
        filterchain.doFilter(new RequestWrapper((HttpServletRequest) request, response), response);
    }

    public FilterConfig getFilterConfig() {
        return filterConfig;
    }

    public void setFilterConfig(FilterConfig config) {
        if (no_init) {
            no_init = false;
            filterConfig = config;
        }
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {
        this.filterConfig = null;
    }
}