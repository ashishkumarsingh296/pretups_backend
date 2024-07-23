package com.btsl.pretups.filters;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class XSSFilter implements Filter {

	 private static final Log logger = LogFactory.getLog(XSSFilter.class);

	@Override
	public void destroy() {
		
		
	}

	@Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
            
            
            HttpServletResponse res = (HttpServletResponse) response;
            XSSRequestWrapper wrapper = new XSSRequestWrapper((HttpServletRequest) request);
           
            res.addHeader("X-FRAME-OPTIONS", "DENY");
         
            
           
            try{
            chain.doFilter(new XSSRequestWrapper((HttpServletRequest) request), response);
            }catch(Exception e){
            	if(logger.isDebugEnabled()){
            		logger.debug(e.getMessage());
            	}
            }
    }

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
		
	}
	
	
}
