package com.selftopup.basext.struts2.interceptor;

import java.util.ArrayList;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import com.selftopup.menu.MenuItem;
import com.selftopup.util.Constants;
import com.selftopup.util.UtilValidate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SecurityInterceptor extends MethodFilterInterceptor {

    private Log log = LogFactory.getLog(SecurityInterceptor.class);
    private String permissionCode = null;
    private String permissionCodeDM = null;

    private String exclude = "N";
    private static java.util.HashSet excludeListFromSessionWhileRemoving = null;

    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }

    public void setExclude(String exclude) {
        this.exclude = exclude;
    }

    public String doIntercept(ActionInvocation invocation) throws Exception {

        if (excludeListFromSessionWhileRemoving == null) {
            excludeListFromSessionWhileRemoving = new java.util.HashSet();
            String sessionExcludeList = Constants.getProperty("SESSION_EXCLUDE_LIST");
            System.out.println("processPreprocess static block sessionExcludeList:" + sessionExcludeList);
            String[] strArray = sessionExcludeList.split(",");
            for (int i = 0; i < strArray.length; i++) {
                excludeListFromSessionWhileRemoving.add(strArray[i].trim());
            }
        }
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        String result = null;
        if (request.getSession().getAttribute("cp2pSubscriberVO") == null && "N".equals(exclude)) {

            result = handleSessionExpired(invocation, response);
        } else if (!isAllowed(request, invocation.getAction())) {
            result = handleRejection(invocation, response);
            log.error("SecurityInterceptor Error");
        } else {
            try {
                result = invocation.invoke();
            } catch (NoSuchMethodException e) {
                result = "securityError";
                log.error("SecurityInterceptor:", e);
            }
        }
        return result;
    }

    /**
     * Determines if the request should be allowed for the action
     * 
     * @param request
     *            The request
     * @param action
     *            The action object
     * @return True if allowed, false otherwise
     */
    protected boolean isAllowed(HttpServletRequest request, Object action) {
        if (permissionCode == null) {
            return true;
        }
        ArrayList menuItemList = (ArrayList) request.getSession().getAttribute("menuItemList");

        for (int loop = 0; loop < menuItemList.size(); loop++) {
            MenuItem menuItem = (MenuItem) menuItemList.get(loop);

            if (permissionCode.equals(menuItem.getPageCode())) {

                if (!permissionCode.endsWith("DM")) {
                    permissionCodeDM = permissionCode + "DM";
                } else {
                    permissionCodeDM = permissionCode.substring(0, permissionCode.length() - 2);
                }

                if (UtilValidate.isNotEmpty(request.getParameter("pageCode"))) {
                    if (!((permissionCode.equals(request.getParameter("pageCode"))) || (permissionCodeDM.equals(request.getParameter("pageCode"))))) {
                    	StringBuilder msg=new StringBuilder("");
                    	msg.append("SecurityInterceptor Error : Found Page Code = ");
                    	msg.append(request.getParameter("pageCode"));
                    	msg.append(" ");
                    	msg.append(", while expecting ");
                    	msg.append(permissionCode);
                        log.error(msg.toString());
                        return false;
                    }
                }
                String newModuleCode = request.getParameter("moduleCode");

                if (UtilValidate.isNotEmpty(newModuleCode) && !menuItem.getModuleCode().equals(newModuleCode)) {
                	StringBuilder msg=new StringBuilder("");
                	msg.append("SecurityInterceptor Error : Found Module Code = ");
                	msg.append(request.getParameter("newModuleCode"));
                	msg.append(" ");
                	msg.append(", while expecting ");
                	msg.append(menuItem.getModuleCode());
                    log.error(msg.toString());
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Handles a rejection by sending a 403 HTTP error
     * 
     * @param invocation
     *            The invocation
     * @return The result code
     * @throws Exception
     */
    protected String handleRejection(ActionInvocation invocation, HttpServletResponse response) throws Exception {
        return "securityError";
    }

    protected String handleSessionExpired(ActionInvocation invocation, HttpServletResponse response) throws Exception {
        return "sessionExpire";
    }

    public static void handleClearSession(HttpServletRequest request) {
        // define a variable to store the key clearSession
        String clearSession = request.getParameter("clearSession") == null ? "" : request.getParameter("clearSession");
        System.out.println("SecurityInterceptor handleClearSession  clearSession:" + clearSession);
        if (clearSession.equals("Y")) {
            // Commented for FT Integrated setup
            // clearSession(request);
        }// end if of clear session=Y
    }

    public static void clearSession(HttpServletRequest request) {

        System.out.println("SecurityInterceptor clearSession FORCING CLEAR SESSION ");
        HttpSession session = request.getSession();
        java.util.Enumeration sessionObjects = session.getAttributeNames();
        String name = null;
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
}