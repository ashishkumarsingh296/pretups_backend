package com.btsl.pretups.filters;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author tarun.kumar
 *
 */
public class RequestWrappingFilter implements Filter {

	public void doFilter(ServletRequest req, ServletResponse res,FilterChain chain) throws IOException, ServletException {

		chain.doFilter(new OwnHttpRequestWrapper((HttpServletRequest) req), res);

	}

	public void init(FilterConfig config) throws ServletException {

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
