package com.btsl.pretups.filters;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.RandomStringUtils;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * @author tarun.kumar
 * Used for Cross-site request forgery attacks (CSRF)for generate salt token
 */
public class LoadSalt implements Filter {

	 private final Log _log = LogFactory.getLog(this.getClass().getName());
	
	 @Override

	    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)

	        throws IOException, ServletException {
	        // Assume its HTTP
		 
	        HttpServletRequest httpReq = (HttpServletRequest) request;
	        // Check the user session for the salt cache, if none is present we create one
	        Cache<String, Boolean> csrfPreventionSaltCache = (Cache<String, Boolean>)
	            httpReq.getSession().getAttribute("csrfPreventionSaltCache");
	        if (csrfPreventionSaltCache == null){
	            csrfPreventionSaltCache = CacheBuilder.newBuilder().maximumSize(5000).expireAfterWrite(20, TimeUnit.MINUTES).build();
	            httpReq.getSession().setAttribute("csrfPreventionSaltCache", csrfPreventionSaltCache);
	        }

	        // Generate the salt and store it in the users cache
	        String salt = PretupsI.SALT_VALUE;
	       
	      if(httpReq.getParameterMap().containsKey("method")){
	        
	        if( httpReq.getParameter("method").equals("changePassword")){
	        	  salt = RandomStringUtils.random(20, 0, 0, true, true, null, new SecureRandom());
	        }else if(httpReq.getParameter("method").equals("showChangePassword")){
	        	 salt = RandomStringUtils.random(20, 0, 0, true, true, null, new SecureRandom());
	        }else{
	        	 salt =PretupsI.SALT_VALUE;
	        }
	      }  
	        csrfPreventionSaltCache.put(salt, Boolean.TRUE);
	        httpReq.setAttribute("csrfPreventionSalt", salt);	 	      
	        chain.doFilter(request, response);	       	        
	    }

	    @Override
	    public void init(FilterConfig filterConfig) throws ServletException {

	    }
	 
	    @Override

	    public void destroy() {

	    }
	
}
