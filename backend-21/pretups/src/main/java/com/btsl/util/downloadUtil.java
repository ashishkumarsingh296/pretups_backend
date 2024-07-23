/*
 * Created on May 8, 2008
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.btsl.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.user.businesslogic.UserVO;

/**
 * @author Administrator
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class downloadUtil extends HttpServlet {

    /**
     * Initialization of the servlet. <br>
     * 
     * @throws ServletException
     *             if an error occure
     */
    private static final Log _log = LogFactory
			.getLog(downloadUtil.class.getName());
    public void init() throws ServletException {
        // Put your code here
    }

    /**
     * The doGet method of the servlet. <br>
     * 
     * This method is called when a form has its tag value method equals to get.
     * 
     * @param request
     *            the request send by the client to the server
     * @param response
     *            the response send by the server to the client
     * @throws ServletException
     *             if an error occurred
     * @throws IOException
     *             if an error occurred
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final String methodName = "doGet";
        final String fileName = request.getParameter("fileName");
        String filePath = request.getParameter("filePath");
        final int check = filePath.indexOf("/");
        final String METHOD_NAME = "doGet";
        if (check < 0) {
            try {
                // filePath=BTSLUtil.decryptText(filePath);
                filePath = BTSLUtil.decrypt3DesAesText(filePath);
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
        }

        final String randomValue = request.getParameter("randomValue");

        UserVO userVO = null;
        userVO = (UserVO) request.getSession(true).getAttribute("user");

        if (userVO != null && randomValue != null && userVO.getSessionInfoVO().getSessionID().equals(randomValue)) {
            if (!(userVO.isAccessAllowed("RPTSDB001")) && !(userVO.isAccessAllowed("RPTSDC001")) && !(userVO.isAccessAllowed("RPTSDP001")) && !(userVO
                .isAccessAllowed("C2SC2CSM01"))) {
                response.sendError(407, "Un-authorized access");
                return;
            }

            if (fileName == null || filePath == null) {
                _log.debug(methodName, "downloadUtil :: Un-authorized access :: " + request.getRequestURI());
                response.sendError(404, request.getRequestURI());
                return;
            }
            // Security related changes starts here.
            String internalPath = Constants.getProperty("SCHEDULED_REPORT_GENERATED_PATH");
            if (filePath.length() >= internalPath.length()) {
                String temp = filePath.substring(0, internalPath.length());
                if (!temp.equalsIgnoreCase(internalPath)) {
                    internalPath = Constants.getProperty("DOWNLOADRELATIVEPATH");
                    temp = filePath.substring(0, internalPath.length());
                    if (!temp.equalsIgnoreCase(internalPath)) {
                        _log.debug(
                            methodName,
                            "downloadUtil in if :::: Un-authorized access :::: " + request.getRequestURI() + "internalPath=" + internalPath + " temp=" + temp + "filePath=" + filePath);
                        response.sendError(404, request.getRequestURI());
                        return;
                    }
                }
            } else {
                internalPath = Constants.getProperty("DOWNLOADRELATIVEPATH");
                final String temp = filePath.substring(0, internalPath.length());
                if (!temp.equalsIgnoreCase(internalPath)) {
                    _log.debug(
                        methodName,
                        "downloadUtil in else :::: Un-authorized access :::: " + request.getRequestURI() + "internalPath=" + internalPath + " temp=" + temp + "filePath=" + filePath);
                    response.sendError(404, request.getRequestURI());
                    return;
                }
            }

            // This pattern allows for alphanumerics, space, underscore,
            // comma(,) and the period character only.
            // Following sequence is not allowed :- / \ : * ? " < > |
            // Pattern p =
            // Pattern.compile("^[\\w\\., [^\\\\/:\\*\\?\\\"<>\\|]]*$");
            final String allowedChar = "^[\\w\\., [^\\/:*?\"<>|]]*$";
            final Pattern p = Pattern.compile(allowedChar);
            if (!p.matcher(fileName).matches()) {
                _log.debug(methodName, "downloadUtil :: Un-authorized access Bad filename ::" + fileName + " ::" + allowedChar);
                response.sendError(407, "Un-authorized");
                return;
            }

            final File f = new File(filePath + fileName).getCanonicalFile();
            // String ext = fileName.substring(fileName.lastIndexOf(".")+1);
            final String ext = f.getName();
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=\"" + ext + "\"");

            response.setContentType("text/html");
            final ServletOutputStream out = response.getOutputStream();
            // response.getWriter();

            final InputStream in = new FileInputStream(f);
            int bit = 256;
            // int i = 0;
            try {
                while ((bit) >= 0) {
                    bit = in.read();
                    out.write(bit);
                }
            } catch (java.lang.IllegalStateException ste) {
                _log.errorTrace(METHOD_NAME, ste);
                _log.errorTrace(methodName, ste);
                response.sendError(404, request.getRequestURI());
                return;
            } catch (IOException ioe) {
                _log.errorTrace(METHOD_NAME, ioe);
                _log.errorTrace(methodName, ioe);
                response.sendError(404, request.getRequestURI());
                return;
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.errorTrace(methodName, e);
                response.sendError(404, request.getRequestURI());
                return;
            }

            finally {
                if (out != null) {
                    out.flush();
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            }
        } else {
            _log.debug(methodName, "downloadUtil :: Un-authorized access :: " + request.getRequestURI());
            response.sendError(404, request.getRequestURI());
            return;
        }
    }

    /**
     * Destruction of the servlet. <br>
     */
    public void destroy() {
        super.destroy(); // Just puts "destroy" string in log
        // Put your code here
    }

}
