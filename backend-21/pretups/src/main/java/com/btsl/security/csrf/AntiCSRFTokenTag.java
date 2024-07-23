package com.btsl.security.csrf;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.jsp.JspTagException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.BodyTagSupport;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

public class AntiCSRFTokenTag extends BodyTagSupport {

    /**
	 * 
	 */
    private static final long serialVersionUID = 6618685514433195079L;
    private static Log _log = LogFactory.getLog(BTSLUtil.class.getName());

    /**
     * Get Current CSRF Token associated session.
     * Create one if one doesn't exist
     * 
     * @return CSRF token for this session
     * @throws JspTagException
     */
    public String getToken() throws JspTagException {
        if (_log.isDebugEnabled())
            _log.debug("getToken", "Entered");
        try {
            HttpSession session = ((HttpServletRequest) this.pageContext.getRequest()).getSession(false);
            String token = CSRFTokenUtil.getToken(session);
            if (_log.isDebugEnabled())
                _log.debug("Genrated Token is", "=" + token);
            return token;
        } catch (Exception e) {
            throw new JspTagException(e);
        }
    }

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
        JspWriter out;
        if (_log.isDebugEnabled())
            _log.debug("doStartTag", "Entered");

        out = this.pageContext.getOut();
        try {
            out.print("<input type='hidden' name='" + CSRFTokenUtil.SESSION_ATTR_KEY + "' value='" + getToken() + "' />");
        } catch (Exception e) {
            throw new JspTagException("Error writing to body's enclosing JspWriter", e);
        }

        return SKIP_BODY;
    }

}
