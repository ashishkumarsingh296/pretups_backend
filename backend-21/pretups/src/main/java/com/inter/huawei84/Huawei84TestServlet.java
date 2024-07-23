package com.inter.huawei84;

/**
 * @(#)Huawei84TestServlet.java
 *                              Copyright(c) 2007, Bharti Telesoft Int. Public
 *                              Ltd.
 *                              All Rights Reserved
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Author Date History
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Vinay Kumar Singh December 26, 2007 Initial
 *                              Creation
 *                              ------------------------------------------------
 *                              ------------------------------------------------
 *                              This class is to test the Huawei84INhandler.
 */
import java.io.IOException;
import java.util.HashMap;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.util.InterfaceCloserI;
import com.btsl.pretups.processes.ResumeSuspendProcess;
import com.btsl.util.BTSLUtil;

public class Huawei84TestServlet extends HttpServlet {
    private static Log _log = LogFactory.getLog(Huawei84TestServlet.class.getName());
    private String _constantsFilePath;

    /**
     * Constructor of the object.
     */
    public Huawei84TestServlet() {
        super();
    }

    /**
     * Destruction of the servlet. <br>
     */
    public void destroy() {
        super.destroy(); // Just puts "destroy" string in log
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
        if (_log.isDebugEnabled())
            _log.debug("doGet", "Entered");
        String action = request.getParameter("ACTION");
        testMethod(action);
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
        String action = request.getParameter("ACTION");
        // loadTestMethod(action);
        testMethod(action);
    }

    /**
     * Initialization of the servlet.
     * 
     * @throws ServletException
     *             if an error occur
     */
    public void init() throws ServletException {
        // Put your code here
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
        _constantsFilePath = getServletContext().getRealPath(getInitParameter("Huawei84xmlfilepath"));
        System.out.println("Huawei84 File Path >>>>  " + _constantsFilePath);
    }

    /**
     * This method will provide the necessary parameters for the response.
     * 
     */
    public void testMethod(String action) {
        if (_log.isDebugEnabled())
            _log.debug("testMethod", "Entered");
        HashMap _requestMap = new HashMap();
        Huawei84INHandler inHandler = new Huawei84INHandler();
        try {
            _requestMap.put("IN_TXN_ID", "C586666");
            // _requestMap.put("MSISDN","1813500018");
            _requestMap.put("MSISDN", "7000150020");
            _requestMap.put("INTERFACE_ID", "INTID00004");
            _requestMap.put("MODULE", "C2S");
            _requestMap.put("TRANSACTION_ID", "1500150015001500");
            _requestMap.put("INTERFACE_PREV_BALANCE", "50000");
            _requestMap.put("INTERFACE_AMOUNT", "5000.879");
            _requestMap.put("EXT_VALIDITY_DAYS", "10");
            // Added for
            _requestMap.put("INT_ST_TYPE", "A");
            _requestMap.put("USER_TYPE", "S");
            if (action.equals("validate"))
                inHandler.validate(_requestMap);
            else if (action.equals("creditAdjust")) {
                inHandler.creditAdjust(_requestMap);
            } else if (action.equals("credit")) {
                inHandler.credit(_requestMap);
            } else if (action.equals("debitAdjust")) {
                inHandler.debitAdjust(_requestMap);
            } else if (action.equals("validityAdjust")) {
                inHandler.validityAdjust(_requestMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("main Exception e=" + e.getMessage());
            if (_log.isDebugEnabled())
                _log.debug("testMethod", "_requestMap _requestMap _requestMap _requestMap " + _requestMap);
            String interfaceStatusType = (String) _requestMap.get("INT_SET_STATUS");
            if (!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME.equals(interfaceStatusType)))
                new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES, (String) _requestMap.get("INTERFACE_ID"), interfaceStatusType, PretupsErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG, PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
        }
        if (_log.isDebugEnabled())
            _log.debug("testMethod", "Exited");
    }
}
