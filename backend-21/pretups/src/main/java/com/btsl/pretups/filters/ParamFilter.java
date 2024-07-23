/**
 * @(#)/**
 * @(#)ParamFilter.java
 *                      Copyright(c) 2014, Mahindra Comviva Technologies Ltd.
 *                      All Rights Reserved
 * 
 *                      <description>
 *                      --------------------------------------------------------
 *                      -----------------------------------------
 *                      Author Date History
 *                      --------------------------------------------------------
 *                      -----------------------------------------
 *                      Diwakar May 05, 2014 Initital Creation
 *                      --------------------------------------------------------
 *                      -----------------------------------------
 * 
 */

package com.btsl.pretups.filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public final class ParamFilter implements Filter {

	private FilterConfig filterConfig = null;

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String regex = this.filterConfig.getInitParameter("excludeParams");
        
        HttpServletRequest req = (HttpServletRequest) request;
        String requestURI = req.getRequestURI();
        
        if(requestURI != null && requestURI.contains("commonAction/") && ( requestURI.contains(".html") || requestURI.contains(".js") ) ) {
        
        	
        	String newURI = "/commonAction.do";
        	request.getRequestDispatcher(newURI).forward(request, response);
        }else {
        	chain.doFilter(new ParamFilteredRequest(request, regex), response);	
        }
        
        
    }

    public void destroy() {
    }

    static class ParamFilteredRequest extends HttpServletRequestWrapper {
    	    	
        private HttpServletRequest originalRequest;
        private String regex;
        private static Log _log = LogFactory.getLog(ParamFilteredRequest.class.getName());

        public ParamFilteredRequest(ServletRequest request, String regex) {
            super((HttpServletRequest) request);
            this.originalRequest = (HttpServletRequest) request;
            this.regex = regex;
        }

        public Enumeration getParameterNames() {
            List<String> requestParameterNames = Collections.list((Enumeration<String>) super.getParameterNames());
            List finalParameterNames = new ArrayList();

            for (String parameterName : requestParameterNames) {
                if (!parameterName.matches(regex)) {
                    finalParameterNames.add(parameterName);
                    if (_log.isDebugEnabled()) {
                        _log.debug("ParamFilteredRequest()", "Param : " + parameterName);
                    }
                }
            }
            return Collections.enumeration(finalParameterNames);        	            
        }
    }
}
