package com.btsl.security.csrf;

import jakarta.servlet.jsp.JspTagException;

/**
 * Simple class to represent CSRF token exceptions
 */

public class CSRFTokenException extends JspTagException {

    public CSRFTokenException(String string) {
        super(string);
    }

}
