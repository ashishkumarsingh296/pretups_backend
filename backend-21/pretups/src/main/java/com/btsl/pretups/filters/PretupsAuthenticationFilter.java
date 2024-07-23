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

public final class PretupsAuthenticationFilter implements Filter {

	private FilterConfig filterConfig = null;

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    
    private boolean validateToken(String token , HttpServletRequest req){
    	
    	String dbToken="xqhqjj1223bndcndcbcebcebcnencccfec";
    	
    	//tokenStore.validateToken i.e. getPrincipal
    	if(token != null && token.equalsIgnoreCase(dbToken)) {
    		//TODO: Add/Update JSON Req Here - In Case Req is not GET

			/*
			 * if (!HttpMethod.GET.toString().equalsIgnoreCase(req.getMethod())) { String
			 * requestString = req.getReader().lines()
			 * .collect(Collectors.joining(System.lineSeparator())); JsonParser jsonParser =
			 * new JsonParser(); JsonElement jsonTree = jsonParser.parse(requestString);
			 * JsonObject jsonObject = jsonTree.getAsJsonObject(); JsonElement f1 =
			 * jsonObject.get(Constants.USERLOGINIDPARAM.getStrValue());
			 * 
			 * if (f1 != null) { this.setUserloginId(f1.getAsString()); }
			 * 
			 * }
			 * 
			 */
    		return true;
    	}else {
    		return false;
    	}
    }
    
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String regex = this.filterConfig.getInitParameter("excludeParams");
        
        HttpServletRequest req = (HttpServletRequest) request;
        String requestURI = req.getRequestURI();
        
        boolean validToken = validateToken(req.getHeader("token"), req);
        
        if(validToken == false) {
        	throw new ServletException("Authentication Failed - missing or nvalid token");
        }
        
        
       
        	chain.doFilter(new ParamFilteredRequest(request, regex), response);	
       
        
        
    }


	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
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
