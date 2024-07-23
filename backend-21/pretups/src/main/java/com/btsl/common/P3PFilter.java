// Decompiled by DJ v3.5.5.77 Copyright 2003 Atanas Neshkov  Date: 3/21/2017 11:10:41 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   P3PFilter.java

package com.btsl.common;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

public class P3PFilter
    implements Filter
{

    public P3PFilter()
    {
    }

    public void destroy()
    {
    }

    public void doFilter(ServletRequest servletrequest, ServletResponse servletresponse, FilterChain filterchain)
        throws IOException, ServletException
    {
        HttpServletResponse httpservletresponse = (HttpServletResponse)servletresponse;
        httpservletresponse.addHeader("p3p", "CP=\"IDC DSP COR ADM DEVi TAIi PSA PSD IVAi IVDi CONi HIS OUR IND CNT\"");
        filterchain.doFilter(servletrequest, httpservletresponse);
    }

    public void init(FilterConfig filterconfig)
        throws ServletException
    {
    }
}
