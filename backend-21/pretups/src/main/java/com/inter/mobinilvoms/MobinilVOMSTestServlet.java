package com.inter.mobinilvoms;

/*
 * @(#)MobinilVOMSTestServlet.java
 * ----------------------------------------------------------------------
 * Name Date History
 * ------------------------------------------------------------------------
 * Vinay Kumar Singh 22/11/2011 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2011 Comviva Technologies Ltd.
 * Response simulator class for Voucher Management System.
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Properties;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class MobinilVOMSTestServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private String requestResponseFilePath = null;
    private String getVoucherDetailsResStr = null;
    private String updateVoucherStateResStr = null;
    private File file = null;

    /**
     * Constructor of the object.
     */
    public MobinilVOMSTestServlet() {
        super();
    }

    public void init(ServletConfig conf) throws ServletException {
        if (_log.isDebugEnabled())
            _log.debug("MobinilVOMSTestServlet init()", " Entered ");

        super.init(conf);
        requestResponseFilePath = getServletContext().getRealPath(getInitParameter("mobinilVOMSxmlfilepath"));
        if (_log.isDebugEnabled())
            _log.debug("MobinilVOMSTestServlet init()", "mobinilVOMSxmlfilepath=" + requestResponseFilePath);
        Properties properties = new Properties();
        file = new File(requestResponseFilePath);
        try {
            properties.load(new FileInputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
        getVoucherDetailsResStr = properties.getProperty("GET_VOUCHER_DETAILS");
        updateVoucherStateResStr = properties.getProperty("UPDATE_VOUCHER_STATE");

        if (_log.isDebugEnabled())
            _log.debug("MobinilVOMSTestServlet init()", " Exiting ");
    }

    /**
     * Destruction of the servlet. <br>
     */
    public void destroy() {
        super.destroy(); // Just puts "destroy" string in log
        // Put your code here
    }

    /**
     * The doGet method of the servlet. <br>
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
        doPost(request, response);
    }

    /**
     * The doPost method of the servlet. <br>
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
            _log.debug("MobinilVOMSTestServlet", "doPost: Entered ");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String responseStr = null;
        StringBuffer sbf = null;

        try {
            sbf = new StringBuffer(1028);
            String line = null;
            String message = "";
            // Get the Input Stream from the Connection object.
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            // Read the request string line by line and append it to a buffer
            // string.
            while ((line = bufferedReader.readLine()) != null)
                sbf.append(line);
            message = sbf.toString();
            if (_log.isDebugEnabled())
                _log.debug("MobinilVOMSTestServlet", " requestStr=" + message);
            int index = message.indexOf("<methodName>");
            String methodName = message.substring(index + "<methodName>".length(), message.indexOf("</methodName>", index));

            int intAction = 0;

            if ("GetVoucherDetails".equals(methodName))
                intAction = MobinilVOMSI.ACTION_GET_VOUCHER_DETAILS;
            else if ("UpdateVoucherState".equals(methodName))
                intAction = MobinilVOMSI.ACTION_UPDATE_VOUCHER_STATE;

            if (_log.isDebugEnabled())
                _log.debug("doPost ", "methodName=" + methodName + ", intAction=" + intAction);

            if (intAction == MobinilVOMSI.ACTION_GET_VOUCHER_DETAILS) {
                responseStr = getVoucherDetailsResStr;
                if (_log.isDebugEnabled())
                    _log.debug("doPost", "Response String=" + responseStr);
                if (_log.isDebugEnabled())
                    _log.debug("doPost ", "GET_VOUCHER_DETAILS Response=" + responseStr);
                out.print(responseStr);
            } else if (intAction == MobinilVOMSI.ACTION_UPDATE_VOUCHER_STATE) {
                responseStr = updateVoucherStateResStr;
                if (_log.isDebugEnabled())
                    _log.debug("doPost ", "UPDATE_VOUCHER_STATE Response=" + updateVoucherStateResStr);
                out.print(updateVoucherStateResStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("doPost", "Exception e:" + e.getMessage());
        }// end of catch-Exception
    }

    /**
     * Initialization of the servlet. <br>
     * 
     * @throws ServletException
     *             if an error occure
     */
    public void init() throws ServletException {
        // Put your code here
    }

}