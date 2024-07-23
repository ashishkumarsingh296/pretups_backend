package com.btsl.common;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class UtilCookie {
	
	private UtilCookie() {
		
	}
	public static String cookieName = "JSESSIONID";
	/**
	 * 
	 * @author Yogesh dixit
	 */
	public static String getCookieValue(HttpServletRequest request, String name) {
	    Cookie[] cookies = request.getCookies();
	    if (cookies != null) {
	        for (Cookie cookie : cookies) {
	            if (name.equals(cookie.getName())) {
	                return cookie.getValue();
	            }
	        }
	    }
	    return null;
	}
	
}
