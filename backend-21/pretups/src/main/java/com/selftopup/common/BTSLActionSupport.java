package com.selftopup.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.opensymphony.xwork2.ActionSupport;

public class BTSLActionSupport extends ActionSupport implements ServletRequestAware, ServletResponseAware {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    public HttpServletRequest _request;
    public HttpServletResponse _response;

    public void setServletRequest(HttpServletRequest arg0) {
        _request = arg0;
    }

    public void setServletResponse(HttpServletResponse arg0) {
        _response = arg0;
    }
}