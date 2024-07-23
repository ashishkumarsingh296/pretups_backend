package com.btsl.pretups.filters;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.google.common.cache.Cache;

/**
 * @author tarun.kumar
 * Used for Cross-site request forgery attacks (CSRF)that validate token salt.
 */
public class ValidateSalt implements Filter  {

	 private final Log _log = LogFactory.getLog(this.getClass().getName());
	
	@Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)throws IOException, ServletException {
       
		 final String methodName = "validateSalt.doFilter()";
		// Assume its HTTP
        HttpServletRequest httpReq = (HttpServletRequest) request;
        String salt = PretupsI.SALT_VALUE;
      if(httpReq.getParameterMap().containsKey("method")){ 
        if( httpReq.getParameter("method").equals("changePassword")){
        // Get the salt sent with the request
         salt =(String) httpReq.getParameter("csrfPreventionSalt");
        }else if(httpReq.getParameter("method").equals("showChangePassword")){

        	 salt =(String) httpReq.getAttribute("csrfPreventionSalt");
        }else{
        	 salt = PretupsI.SALT_VALUE;
        }
       } 
        // Validate that the salt is in the cache

        Cache<String, Boolean> csrfPreventionSaltCache = (Cache<String, Boolean>)

         httpReq.getSession().getAttribute("csrfPreventionSaltCache");
        if (csrfPreventionSaltCache != null && salt != null && csrfPreventionSaltCache.getIfPresent(salt) != null){           
            chain.doFilter(request, response);
            // If the salt is in the cache, we move on
        } else {
            // Otherwise we throw an exception aborting the request flow
            throw new ServletException("Potential CSRF detected!! Inform a scary sysadmin ASAP.");
        	        	
        }
      
        
    }

    @Override
    public void destroy() {

    }
    
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}

		
}
