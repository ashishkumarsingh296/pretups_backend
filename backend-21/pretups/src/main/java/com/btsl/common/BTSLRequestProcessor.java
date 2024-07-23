/*
 * Created on Jun 8, 2005
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.btsl.common;

import java.io.File;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.Locale;
import java.util.NoSuchElementException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/*import org.apache.struts.Globals;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.RequestProcessor;*/

import com.btsl.basext.struts2.interceptor.SecurityInterceptor;
import com.btsl.cp2p.subscriber.businesslogic.CP2PSubscriberVO;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/**
 * @author mohit.goel
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class BTSLRequestProcessor /*extends RequestProcessor */{

    private static java.util.HashSet excludeListFromSessionWhileRemoving = new java.util.HashSet();
    private static final Log log = LogFactory.getLog(BTSLRequestProcessor.class.getName());
    // !!!! DEBUG SWITHCH !!!!

    public static final int BTSL_USER_MISSING = 601;
    public static final int BTSL_UNAUTHORISED_ACCESS = 407;
    private UserVO userVO = null;
    private CP2PSubscriberVO cp2pSubscriberVO = null;

    static {
        String sessionExcludeList = Constants.getProperty("SESSION_EXCLUDE_LIST");
        String[] strArray = sessionExcludeList.split(",");
        for (int i = 0; i < strArray.length; i++) {
            excludeListFromSessionWhileRemoving.add(strArray[i].trim());
        }
    }

  /*  public boolean processPreprocess(HttpServletRequest request, HttpServletResponse response) {
        // the requested action (e.g. "/logon", "/logoff", etc..)
        String path = "";
        ActionMapping mapping = null;
        final String METHOD_NAME = "processPreprocess";
        try {
            request.setCharacterEncoding("UTF-8");
            HttpSession session = request.getSession(false);
            
            
            *//*
             * Added to prevent csrf attack
             
            String host=request.getHeader("host");
            String referer=request.getHeader("referer");
            String[] refrelUrl=referer.split("/");
            if(refrelUrl.length>=3) {
            	 if(!refrelUrl[2].equals(host)) {
               	  String message = "Unauthorised Acccess";
               	response.sendError(BTSL_UNAUTHORISED_ACCESS, message);
               	return false;
               	
               }
            }*//*
            
            
            

            *//*
             * This language parameter comes as a request paremeter from
             * index.jsp
             * at the same time change the locale of the session
             * 
             * while clear the session attributes not clesr the
             * org.apache.struts.action.LOCALE attrubute.
             * This is defined in the constants.props file
             * SESSION_EXCLUDE_LIST=user,urlCode,moduleCode,org.apache.struts.action
             * .LOCALE
             *//*
            // SessionCounter.incrementCounters(request);

            // session.setAttribute("cp2pwebmodule","cp2pwebmodule");
            try {
				String httpLanguage = request.getParameter("language");
				if (!BTSLUtil.isNullString(httpLanguage)) {
				    String[] langArgs = httpLanguage.split("_");
				    if (langArgs != null && langArgs.length == 2) {
				        // Create a Locale for language
				        Locale locale = new Locale(langArgs[0], langArgs[1]);
				        session.setAttribute(Globals.LOCALE_KEY, locale);
				        session.setAttribute("WW_TRANS_I18N_LOCALE", locale);
				        if (log.isDebugEnabled()) {
				            log.debug(METHOD_NAME, "BTSLRequestProcessor: New Locale=" + locale);
				        }
				    }
				}
			} catch (Exception e2) {
				log.errorTrace(METHOD_NAME, e2);
			}

            int requestID = 0;
            int lastID = 0;

            // shashi:To avoid clickjacking attack and cache control
            response.addHeader("X-FRAME-OPTIONS", "SAMEORIGIN");
            response.addHeader("Content-Security-Policy", "frame-ancestors 'self'");

            response.setHeader("Cache-Control", "no-cache, no-store, max-age=0");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");

            response.addHeader("X-XSS-Protection", "1; mode=block");
            response.addHeader("X-Content-Type-Options", "nosniff");
            path = processPath(request, response);
            String sessionLastId = null;
            String requestId = null;
            try {
				sessionLastId = (String)session.getAttribute("lastID");
				lastID = sessionLastId == null ? 0 : Integer.parseInt(sessionLastId);                
			} catch (Exception e1) {
				log.errorTrace(METHOD_NAME, e1);
                lastID = 0;
			}
            try {
            	requestId = (String) request.getParameter("requestID");
            	requestID = requestId == null ? 0 : Integer.parseInt(requestId);                
            } catch (Exception e) {
            	log.errorTrace(METHOD_NAME, e);
                requestID = 0;
            }
            try {
            	if(requestID != lastID && requestID == 0) {
                	lastID = 0;
            	}
                if (log.isDebugEnabled()) {
                    log.debug(METHOD_NAME, "BTSLRequestProcessor: path=" + path);
                    // eject if logging on or off or launching the application
                }

                if (!"cp2pwebmodule".equals((String) session.getAttribute("cp2pwebmodule")) && !("/login".equalsIgnoreCase(path) || "/reloadMessageResource".equalsIgnoreCase(path))) {
                    mapping = processMapping(request, response, path);
                    if (mapping == null) {
                        String message = "No mapping available for the requested URL " + path;
                        if (log.isDebugEnabled()) {
                            log.debug(METHOD_NAME, "message: " + message);
                        }
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
                        return false;
                    }

                    userVO = (UserVO) session.getAttribute("user");

                    String message = "";

                    // have they already logged in
                    if (userVO == null) {

                        message = "Critical component [User] not found in session... re-logon required";
                        if (log.isDebugEnabled()) {
                            log.debug(METHOD_NAME, "message: " + message);
                        }
                        RequestDispatcher requestDispatcher = getServletContext().getRequestDispatcher(getServletContext().getResource(String.valueOf(BTSL_USER_MISSING)).toString());
                        if (requestDispatcher != null) {
                            requestDispatcher.forward(request, response);
                        } else {
                            response.sendError(BTSL_USER_MISSING, message);
                        }
                        return false;
                    }
                    // validating session id on each request.
                    else if (!userVO.getSessionInfoVO().getSessionID().equals(session.getId())) {
                        if (log.isDebugEnabled()) {
                            log.debug(METHOD_NAME, "REDIRECTING");
                        }
                        response.sendError(BTSL_UNAUTHORISED_ACCESS, message);
                        return false;
                    }

                    if (log.isDebugEnabled()) {
                    	StringBuilder sb = new StringBuilder();
                		sb.append("getName [");
                		sb.append(mapping.getName());
                		sb.append("]");
                        log.debug(METHOD_NAME,sb.toString());
                        sb = new StringBuilder();
                		sb.append("getInput [");
                		sb.append(mapping.getInput());
                		sb.append("]");
                        log.debug(METHOD_NAME,sb.toString());
                        sb = new StringBuilder();
                		sb.append("getType [");
                		sb.append(mapping.getType());
                		sb.append("]");
                        log.debug(METHOD_NAME, sb.toString());
                        sb = new StringBuilder();
                		sb.append("getScope [");
                		sb.append(mapping.getScope());
                		sb.append("]");
                        log.debug(METHOD_NAME,sb.toString());
                        log.debug(METHOD_NAME, "====================================================================");
                    }
                } else if ("cp2pwebmodule".equals((String) session.getAttribute("cp2pwebmodule")) && !("/cp2plogin".equalsIgnoreCase(path) || "/reloadMessageResource".equalsIgnoreCase(path))) {

                    mapping = processMapping(request, response, path);
                    if (mapping == null) {
                        String message = "No mapping available for the requested URL " + path;
                        if (log.isDebugEnabled()) {
                            log.debug(METHOD_NAME, "message: " + message);
                        }
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
                        return false;
                    }

                    cp2pSubscriberVO = (CP2PSubscriberVO) session.getAttribute("cp2pSubscriber");

                    String message = "";

                    // have they already logged in
                    if (cp2pSubscriberVO == null) {

                        message = "Critical component [Subscriber] not found in session... re-logon required";
                        if (log.isDebugEnabled()) {
                            log.debug(METHOD_NAME, "message: " + message);
                        }
                        RequestDispatcher requestDispatcher = getServletContext().getRequestDispatcher(getServletContext().getResource(String.valueOf(BTSL_USER_MISSING)).toString());
                        if (requestDispatcher != null) {
                            requestDispatcher.forward(request, response);
                        } else {
                            response.sendError(BTSL_USER_MISSING, message);
                        }
                        return false;
                    }
                    // validating session id on each request.
                    else if (!cp2pSubscriberVO.getSessionInfoVO().getSessionID().equals(session.getId())) {
                        if (log.isDebugEnabled()) {
                            log.debug(METHOD_NAME, "REDIRECTING");
                        }
                        response.sendError(BTSL_UNAUTHORISED_ACCESS, message);
                        return false;
                    }
                    if (log.isDebugEnabled()) {
                        log.debug(METHOD_NAME, "getName [" + mapping.getName() + "]");
                        log.debug(METHOD_NAME, "getInput [" + mapping.getInput() + "]");
                        log.debug(METHOD_NAME, "getType [" + mapping.getType() + "]");
                        log.debug(METHOD_NAME, "getScope [" + mapping.getScope() + "]");
                        log.debug(METHOD_NAME, "====================================================================");
                    }
                }
                
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            printRequestAttributesheaders(request);
            if (log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("lastID: ");
            	msg.append(lastID);
            	msg.append("     requestID: ");
            	msg.append(requestID);
            	msg.append("      ");
            	msg.append(request.getRequestURI());
            	msg.append(" requestPath: ");
            	msg.append((String) request.getAttribute("requestPath"));
            	String message=msg.toString();
                log.debug(METHOD_NAME,message);
            }
            if (lastID != 0 && requestID < lastID && BTSLUtil.isNullString((String) request.getAttribute("requestPath"))) {
                if (log.isDebugEnabled()) {
                    log.debug(METHOD_NAME, "REDIRECTING");
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
				log.errorTrace(METHOD_NAME, e);
				newUrlCode = "";
			}
            try {
				newModuleCode = request.getParameter("moduleCode");
			} catch (Exception e) {
				log.errorTrace(METHOD_NAME, e);
				newModuleCode = "";
			}
            if (log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("newUrlCode:");
            	msg.append(newUrlCode);
            	msg.append("  newModuleCode:");
            	msg.append(newModuleCode);
            	String message=msg.toString();
                log.debug(METHOD_NAME, message);
            }
            String invalidOp = request.getParameter("invalidOp");
            if (log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append(" invalidOp: ");
            	msg.append(invalidOp);
            	msg.append(" request method: ");
            	msg.append(request.getMethod());
            	String message=msg.toString();
                log.debug(METHOD_NAME, message);
            }
            if (newUrlCode != null || newModuleCode != null) {
                clearSession(request);
            }
            *//*
             * else
             * if(mapping!=null&&BTSLUtil.isNullString(invalidOp)&&request.getMethod
             * ().equals("POST"))
             * {
             * String formName=mapping.getName();
             * Object formObject=actionForm;
             * 
             * Object obj=session.getAttribute(formName);
             * if(_log.isDebugEnabled()) _log.debug(METHOD_NAME,
             * "Form Object obj: "
             * +obj+" invalidOp: "+invalidOp+" request method: "
             * +request.getMethod());
             * java.util.Enumeration sessionObjects =
             * session.getAttributeNames();
             * if(obj==null)
             * {
             * loglog.isDebugEnabled())_log.debug(METHOD_NAME,
             * "INVALID OPERATION Form/bean already cleared");
             * response.sendRedirect(request.getContextPath()+File.separator+
             * "/commonAction.do?method=invalidOperation&invalidOp=true");
             * return false;
             * }
             * }
             *//*

            // setting cookies to http only as well as setting cookie value as a
            // random number.
            String randomValue = null;
            try {
                SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
                randomValue = String.valueOf(Math.abs(sr.nextLong()));
            } catch (java.security.NoSuchAlgorithmException nsae) {
                log.errorTrace(METHOD_NAME, nsae);
                randomValue = Long.toString(System.currentTimeMillis());
            }
            String contextPath = request.getContextPath();
            String secure = "";
            if (request.isSecure()) {
                secure = "; Secure";
            }
            response.setHeader("SET-COOKIE", "UNIQUECK=" + randomValue + "; Path=" + contextPath + "; HttpOnly" + secure);

            *//*
             * This code can be use to handle & update cookies for every request
             * with secured random value and validate cookies value.
             * if(!validateCookies(request,response,session))
             * {
             * String message =
             * "You are not Authorised because of security checks failures, Please re-login"
             * +path;
             * ActivityLog.log((String)request.getAttribute("pageCode"), userVO
             * );
             * try{
             * session.invalidate();
             * }
             * catch(Exception exx){}
             * response.sendError(BTSL_UNAUTHORISED_ACCESS,message);
             * return (false);
             * }
             *//*

        } catch (Exception ex) {
            log.errorTrace(METHOD_NAME, ex);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "leaving for path: " + path);
            }
        }
        return true;
    }
*/
    public static void handleClearSession(HttpServletRequest request) {
        // define a variable to store the key clearSession
        String clearSession = BTSLUtil.NullToString(request.getParameter("clearSession"));
        if ("Y".equals(clearSession)) {
          //  clearSession(request);
        }// end if of clear session=Y
    }

   /* public static void clearSession(HttpServletRequest request) {
        SecurityInterceptor.clearSession(request, excludeListFromSessionWhileRemoving);
    }*/

    private void printRequestAttributesheaders(HttpServletRequest request) {
    	final String methodName = "printRequestAttributesheaders";
        Enumeration<String> enume ;
        Enumeration<String> enumh ;

        enume = request.getParameterNames();
        enumh = request.getHeaderNames();
        String reqParamsHeaders = null;
        if (log.isDebugEnabled()) {
            try {
				while (enume.hasMoreElements()) {
					reqParamsHeaders = enume.nextElement();
					log.debug(methodName, "Request parameters :: " + reqParamsHeaders);
				}
			} catch (NoSuchElementException e) {
				reqParamsHeaders = "";
				log.errorTrace(methodName, e);
			}
            try {
				while (enumh.hasMoreElements()) {
					reqParamsHeaders = enumh.nextElement();
				    log.debug(methodName, "Request header parameters :: " + reqParamsHeaders);
				}
			} catch (NoSuchElementException e) {
				reqParamsHeaders = "";
				log.errorTrace(methodName, e);
			}
        }

    }

    /*
     * This code can be use to handle & update cookies for every request with
     * secured random value and validate cookies value.
     * In this we are validating request cookie and updating this value with new
     * secured random value.
     */
    private boolean validateCookies(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        if (log.isDebugEnabled()) {
            log.debug("validateCookies", "Entered with session random value :: " + (String) session.getAttribute("cRandom"));
        }
        // setting cookies to http only as well as setting cookie value as a
        // random number.
        final String METHOD_NAME = "validateCookies";
        String randomValue = null;
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            randomValue = String.valueOf(Math.abs(sr.nextLong()));
            if (log.isDebugEnabled()) {
                log.debug("validateCookies", "New random value :: " + randomValue);
            }
        } catch (java.security.NoSuchAlgorithmException nsae) {
            log.errorTrace(METHOD_NAME, nsae);
            randomValue = Long.toString(System.currentTimeMillis());
        }
        Cookie cookiesArray[] = request.getCookies();
        String cName = "randomValue";
        if (cookiesArray != null) {
        	StringBuffer msg=new StringBuffer("");
            for (int i = 0; i < cookiesArray.length; i++) {
                Cookie reqCookie = cookiesArray[i];
                if ("randomValue".equals(reqCookie.getName())) {
                	String reqCookieName ;
                	String reqCookieValue;
                	try {
						reqCookieName = reqCookie.getName();
					} catch (Exception e) {
						log.errorTrace(METHOD_NAME, e);
						reqCookieName = "";
					}
                	try {
						reqCookieValue = reqCookie.getValue();
					} catch (Exception e) {
						log.errorTrace(METHOD_NAME, e);
						reqCookieValue = "";
					}
                    if (log.isDebugEnabled()) {
                    	msg.setLength(0);
                    	msg.append("reqCookie.getName()::");
                    	msg.append(reqCookieName);
                    	msg.append(", Length::");
                    	msg.append(cookiesArray.length);
                    	msg.append(", reqCookie.getValue()::");
                    	msg.append(reqCookieValue);
                    	msg.append(", session::");
                    	msg.append(session.getAttribute("cRandom"));
                    	String message=msg.toString();
                        log.debug(METHOD_NAME, message);
                    }
                    if (reqCookieValue.equals(session.getAttribute("cRandom"))) {
                        session.setAttribute("cRandom", randomValue);
                        cName = reqCookie.getName();
                        String cValue = reqCookie.getValue();
                        reqCookie.setMaxAge(0);
                        response.addCookie(reqCookie);
                        break;
                    } else {
                        reqCookie.setMaxAge(0);
                        response.addCookie(reqCookie);
                        return false;
                    }
                }
            }
        }
        // setting cookies to http only as well as setting cookie value as a
        // random number.
        String contextPath = request.getContextPath();
        String secure = "";
        if (request.isSecure()) {
            secure = "; Secure";
        }
        
        response.setHeader("SET-COOKIE", "UNIQUECK=" + randomValue + "; Path=" + contextPath + "; HttpOnly" + secure);

        Cookie cookie = new Cookie(cName, randomValue);
        cookie.setMaxAge(-1);
        cookie.setSecure(request.isSecure());
        cookie.setPath(contextPath + "/");
        response.addCookie(cookie);
        return true;
    }

    public static void deleteCookies(HttpServletResponse p_response, HttpServletRequest p_request, String p_cookieName) {
        Cookie[] cookies = p_request.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equalsIgnoreCase(p_cookieName) && cookies[i].getValue() != null) {
                    Cookie cookie = new Cookie(p_cookieName, null);
                    cookie.setPath(p_request.getContextPath() + "/");
                    cookie.setSecure(p_request.isSecure());
                    cookie.setMaxAge(0);// Expire right now
                    p_response.addCookie(cookie);
                }
            }
        }
    }

    private void rewriteCookieToHeader(HttpServletRequest request, HttpServletResponse response) {
        if (response.containsHeader("SET-COOKIE")) {
            String sessionid = request.getSession().getId();
            String contextPath = request.getContextPath();
            String secure = "";
            if (request.isSecure()) {
                secure = "; Secure";
            }
            StringBuilder sb = new StringBuilder();
    		sb.append("JSESSIONID=");
    		sb.append(sessionid);
    		sb.append("; Path=");
    		sb.append(contextPath);
    		sb.append("; HttpOnly");
    		sb.append(secure);
            response.setHeader("SET-COOKIE",sb.toString());
        }
    }
}
