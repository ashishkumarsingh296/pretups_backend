package com.inter.huaweievr;

/**
 * @(#)HuaweiEVRTestServlet.java
 *                               Copyright(c) 2007, Bharti Telesoft Int. Public
 *                               Ltd.
 *                               All Rights Reserved
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Author Date History
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Vinay Kumar Singh December 10, 2007 Initial
 *                               Creation
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               --
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class HuaweiEVRTestServlet extends HttpServlet {
    private static Log _log = LogFactory.getLog(HuaweiEVRTestServlet.class.getName());
    private String _constantsFilePath;

    // Constructor
    public HuaweiEVRTestServlet() {
        super();
    }

    /**
     * Initialization of the servlet.
     * 
     * @throws ServletException
     *             if an error occur
     */
    public void init(ServletConfig conf) throws ServletException {
        System.out.println("ConfigServlet init() Entered ");
        super.init(conf);
        _constantsFilePath = getServletContext().getRealPath(getInitParameter("HuaweiEVRxmlfilepath"));
        System.out.println("HuaweiEVR File Path >>>>  " + _constantsFilePath);
    }

    /**
     * Destroy method.
     */
    public void destroy() {
        super.destroy();
    }

    /**
     * The doGet method of the servlet.
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
        if (_log.isDebugEnabled())
            _log.debug("doGet", "Entered");
        String action = request.getParameter("ACTION");
        if (_log.isDebugEnabled())
            _log.debug("doGet", "Exited");
    }

    /**
     * The doPost method of the servlet.
     * This method is called when a form has its tag value method equals to
     * post.
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
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (_log.isDebugEnabled())
            _log.debug("doPost", "Entered");

        Properties properties = new Properties();
        File file = new File(_constantsFilePath);
        properties.load(new FileInputStream(file));
        String responseStr = null;
        responseStr = properties.getProperty("ACCOUNT_INFO");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        if (_log.isDebugEnabled())
            _log.debug("doPost", "AccountInfo Response: " + responseStr);
        out.print(responseStr);
        if (_log.isDebugEnabled())
            _log.debug("doPost", "Exited");
    }
}
