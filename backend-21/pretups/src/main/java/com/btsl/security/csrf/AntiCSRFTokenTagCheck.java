package com.btsl.security.csrf;

import java.security.NoSuchAlgorithmException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspTagException;
import jakarta.servlet.jsp.tagext.BodyTagSupport;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

public class AntiCSRFTokenTagCheck extends BodyTagSupport {

    /**
	 * 
	 */
    private static final long serialVersionUID = -1230734645121713360L;
    private static Log _log = LogFactory.getLog(BTSLUtil.class.getName());

    /**
     * After tag body parsing handler.
     * 
     * @return {@link jakarta.servlet.jsp.tagext.Tag#SKIP_BODY}
     * @throws JspTagException
     *             if writing to the bodyContent's
     *             enclosing writer throws an IOException or there's an
     *             exception generating a session.
     */
    public int doStartTag() throws JspTagException {
        if (_log.isDebugEnabled())
            _log.debug("token check", "Entered");
        try {
            if (!CSRFTokenUtil.isValid((HttpServletRequest) (pageContext.getRequest()))) {
                if (_log.isDebugEnabled())
                    _log.debug("CSRF", "ATTACK!");
                throw new CSRFTokenException("CSRF Attempt!");
            }
            // System.out.println("NO CSRF ATTACK..");
        } catch (NoSuchAlgorithmException e) {
            throw new JspTagException(e);
        } catch (ServletException e) {
            throw new JspTagException(e);
        }
        return SKIP_BODY;
    }

}
