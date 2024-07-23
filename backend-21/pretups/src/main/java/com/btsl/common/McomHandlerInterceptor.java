package com.btsl.common;

import java.io.File;
import java.util.Enumeration;
import java.util.HashSet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
//import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.btsl.cp2p.subscriber.businesslogic.CP2PSubscriberVO;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;

/**
 * @author akanksha
 *Interceptor for spring module
 */
public class McomHandlerInterceptor /*extends HandlerInterceptorAdapter*/ {

    private static HashSet excludeListFromSessionWhileRemoving = new HashSet();
    private final Log log = LogFactory.getLog(this.getClass().getName());
    // !!!! DEBUG SWITHCH !!!!
    private final boolean debug = true;
    public static final int BTSL_USER_MISSING = 601;
    public static final int BTSL_UNAUTHORISED_ACCESS = 407;
    private static String maintenanceMapping;
    private UserVO userVO = null;
    private CP2PSubscriberVO cp2pSubscriberVO = null;


    // akchanges
    public static void setMaintenanceMapping(String maintenanceMapping1) {
        maintenanceMapping = maintenanceMapping1;
    }

    //@Override
 /*   public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){

        // the requested action (e.g. "/logon", "/logoff", etc..)
        String path = "";
        final String methodName = "preHandle";
        path = request.getServletPath();
        try {
        	request.setCharacterEncoding("UTF-8");
            HttpSession session = request.getSession(false);
            String newLocale = (String) session.getAttribute("locale");
            if (newLocale != null) {
                LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
                if (localeResolver == null) {
                    throw new IllegalStateException("No LocaleResolver found: not in a DispatcherServlet request?");
                }
                localeResolver.setLocale(request, response, StringUtils.parseLocaleString(newLocale));
            }
            int requestID = 0;
            int lastID = 0;
            // akanksha:To avoid clickjacking attack and cache control
            response.addHeader("X-FRAME-OPTIONS", "SAMEORIGIN");
            response.addHeader("Content-Security-Policy", "frame-ancestors 'self'");

            response.setHeader("Cache-Control", "no-cache, no-store, max-age=0");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");

            response.addHeader("X-XSS-Protection", "1; mode=block");
            response.addHeader("X-Content-Type-Options", "nosniff");
            String sessionLastId = null;
            String requestId = null;
            try {
				sessionLastId = (String)session.getAttribute("lastID");
				lastID = sessionLastId == null ? 0 : Integer.parseInt(sessionLastId);                
			} catch (Exception e1) {
				log.errorTrace(methodName, e1);
                lastID = 0;
			}
            try {
            	requestId = (String) request.getParameter("requestID");
            	requestID = requestId == null ? 0 : Integer.parseInt(requestId);                
            } catch (Exception e) {
            	log.errorTrace(methodName, e);
                requestID = 0;
            }
            try {
                if (log.isDebugEnabled()) {
                    log.debug(methodName, "McomHandlerInterceptor: path=" + path);
                    // eject if logging on or off or launching the application
                }

                if (!"cp2pwebmodule".equals(session.getAttribute("cp2pwebmodule")) && !(path.equalsIgnoreCase("/login") || path.equalsIgnoreCase("/reloadMessageResource") || path.equalsIgnoreCase("/ForgotPassword/getPasswordPage.form") || path.equalsIgnoreCase("/ForgotPassword/processForgotPassword.form") || path.equalsIgnoreCase("/ForgotPassword/resetPassword.form") || path.equalsIgnoreCase("/ForgotPassword/confirmResetPassword.form")))

                {
                    userVO = (UserVO) session.getAttribute("user");
                    String message = "";
                    if (userVO == null) {

                        message = "Critical component [User] not found in session... re-logon required";
                        if (log.isDebugEnabled()) {
                            log.debug(methodName, "message: " + message);
                        }

                        response.sendRedirect(request.getContextPath() + maintenanceMapping);// akchanges

                        return false;
                    }
                    // validating session id on each request.
                    else if (!userVO.getSessionInfoVO().getSessionID().equals(session.getId())) {
                        if (log.isDebugEnabled()) {
                            log.debug(methodName, "REDIRECTING ");
                        }
                        response.sendError(BTSL_UNAUTHORISED_ACCESS, message);

                        return false;
                    }

                    if (log.isDebugEnabled()) {
                        log.debug(methodName, "ServletPath [" + request.getServletPath() + "]");

                        log.debug(methodName, "====================================================================");
                    }
                } else if ("cp2pwebmodule".equals(session.getAttribute("cp2pwebmodule")) && !(path.equalsIgnoreCase("/cp2plogin") || path.equalsIgnoreCase("/reloadMessageResource") || path.equalsIgnoreCase("/ForgotPassword/getPasswordPage.form") || path.equalsIgnoreCase("/ForgotPassword/processForgotPassword.form") || path.equalsIgnoreCase("/ForgotPassword/resetPassword.form") || path.equalsIgnoreCase("/ForgotPassword/confirmResetPassword.form")))

                {

                    cp2pSubscriberVO = (CP2PSubscriberVO) session.getAttribute("cp2pSubscriber");
                    String message = "";

                    // have they already logged in
                    if (cp2pSubscriberVO == null) {

                        message = "Critical component [Subscriber] not found in session... re-logon required";
                        if (log.isDebugEnabled()) {
                            log.debug(methodName, "message: " + message);
                        }

                        return false;
                    }
                    // validating session id on each request.
                    else if (!cp2pSubscriberVO.getSessionInfoVO().getSessionID().equals(session.getId())) {
                        if (log.isDebugEnabled()) {
                            log.debug(methodName, " REDIRECTING");
                        }
                        response.sendError(BTSL_UNAUTHORISED_ACCESS, message);
                        return false;
                    }
                    if (log.isDebugEnabled()) {
                        log.debug(methodName, "ServletPath [" + request.getServletPath() + "]");

                        log.debug(methodName, "====================================================================");
                    }
                }

            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            printRequestAttributesheaders(request);
            if (log.isDebugEnabled()) {
                log.debug(methodName, "lastID: " + lastID + "     requestID: " + requestID + "      " + request.getRequestURI() + " requestPath: " + (String) request.getAttribute("requestPath"));
            }
            if (lastID != 0 && requestID!=0 && requestID < lastID && BTSLUtil.isNullString((String) request.getAttribute("requestPath"))) {
                if (log.isDebugEnabled()) {
                    log.debug(methodName, "REDIRECTING");
                }
                response.sendRedirect(request.getContextPath() + File.separator + "/commonAction.do?method=refreshNotAllowed");
                return false;
            } else {
                request.setAttribute("requestPath", "true");
                session.setAttribute("lastID", String.valueOf(requestID + 1));
            }
            String newUrlCode ;
            String newModuleCode ;
            try {
				newUrlCode = request.getParameter("urlCode");
			} catch (Exception e) {
				log.errorTrace(methodName, e);
				newUrlCode = "";
			}
            try {
				newModuleCode = request.getParameter("moduleCode");
			} catch (Exception e) {
				log.errorTrace(methodName, e);
				newModuleCode = "";
			}
            if (log.isDebugEnabled()) {
                log.debug(methodName, "newUrlCode:" + newUrlCode + "  newModuleCode:" + newModuleCode);
            }
            String invalidOp ;
            try {
            	invalidOp = request.getParameter("invalidOp");
			} catch (Exception e) {
				log.errorTrace(methodName, e);
				invalidOp = "";
			}
            if (log.isDebugEnabled()) {
                log.debug(methodName, " invalidOp: " + invalidOp + " request method: " + request.getMethod());
            }

        } catch (Exception ex) {
            log.errorTrace(methodName, ex);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug(methodName, "leaving for path: " + path);
            }
        }

        return true;
    }
*/
    private void printRequestAttributesheaders(HttpServletRequest request) {
        Enumeration enume = request.getParameterNames();
        Enumeration enumh = request.getHeaderNames();

        if (log.isDebugEnabled()) {
            while (enume.hasMoreElements()) {
                log.debug("printRequestAttributesheaders", "Request parameters :: " + (String) enume.nextElement());
            }
            while (enumh.hasMoreElements()) {
                log.debug("printRequestAttributesheaders", "Request header parameters :: " + (String) enumh.nextElement());
            }
        }

    }

    /**
     * @param request
     */
    public static void clearSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        java.util.Enumeration sessionObjects = session.getAttributeNames();
        String name;
        if (sessionObjects != null) {
            while (sessionObjects.hasMoreElements()) {
                name = (String) sessionObjects.nextElement();
                if (excludeListFromSessionWhileRemoving.contains(name)) {
                    continue;
                }
                session.removeAttribute(name);
                sessionObjects = session.getAttributeNames();
            }// end of while
        }
    }

    /*@Override
    public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3) throws Exception {

    	 if (log.isDebugEnabled()) {
             log.debug("postHandle", "handle ");
         }

    }*/

    /*@Override
    public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3) throws Exception {

    	 if (log.isDebugEnabled()) {
             log.debug("afterCompletion", "handle ");
         }
    }*/
}
