/**
 * Software published by the Open Web Application Security Project
 * (http://www.owasp.org)
 * This software is licensed under the new BSD license.
 * 
 * @author Jeff Williams <a href="http://www.aspectsecurity.com">Aspect
 *         Security</a>
 * @created February 6, 2009
 */

package com.btsl.common;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
/**
 * 
 * @author ayush.abhijeet
 *
 */
public class ClickjackFilter implements Filter {

    private String mode = "DENY";

    /**
     * Add X-FRAME-OPTIONS response header to tell IE8 (and any other browsers
     * who
     * decide to implement) not to display this content in a frame. For details,
     * please
     * refer to
     * http://blogs.msdn.com/sdl/archive/2009/02/05/clickjacking-defense
     * -in-ie8.aspx.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse res = (HttpServletResponse) response;
        chain.doFilter(request, response);
        res.addHeader("X-FRAME-OPTIONS", mode);
    }

    public void destroy() {
    }

    public void init(FilterConfig filterConfig) {
        String configMode = filterConfig.getInitParameter("mode");
        if (configMode != null) {
            mode = configMode;
        }
    }

}
