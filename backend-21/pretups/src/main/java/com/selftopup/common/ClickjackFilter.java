package com.selftopup.common;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

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
