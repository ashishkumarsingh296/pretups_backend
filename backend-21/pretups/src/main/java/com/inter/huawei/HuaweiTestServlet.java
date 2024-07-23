package com.inter.huawei;

import java.io.IOException;
import java.util.HashMap;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.inter.huawei.HuaweiINHandler;

/**
 * @author ashishk
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class HuaweiTestServlet extends HttpServlet {
    private static Log _log = LogFactory.getLog(HuaweiTestServlet.class.getName());

    /**
     * Constructor of the object.
     */
    public HuaweiTestServlet() {
        super();
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
        if (_log.isDebugEnabled())
            _log.debug("doGet", "Entered");
        String action = request.getParameter("ACTION");
        testMethod(action);
        // loadTestMethod(action);
        if (_log.isDebugEnabled())
            _log.debug("doGet", "Exited");
    }

    /**
     * The doPost method of the servlet. <br>
     * 
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
     * Initialization of the servlet. <br>
     * 
     * @throws ServletException
     *             if an error occure
     */
    public void init() throws ServletException {
        // Put your code here
    }

    private void loadTestMethod(String p_action) {
        if (_log.isDebugEnabled())
            _log.debug("loadTestMethod", "Entered");
        HashMap _requestMap = new HashMap();
        try {
            _requestMap.put("IN_TXN_ID", "C586666");
            _requestMap.put("MSISDN", "1813500018");
            // _requestMap.put("MSISDN","1815050020");
            _requestMap.put("INTERFACE_ID", "INTID00003");
            _requestMap.put("MODULE", "C2S");
            _requestMap.put("TRANSACTION_ID", "1500150015001500");
            _requestMap.put("INTERFACE_PREV_BALANCE", "50000");
            _requestMap.put("INTERFACE_AMOUNT", "5000.879");
            new LoadTest(_requestMap, p_action.trim());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("main Exception e=" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("loadTestMethod", "Exited");
        }
    }

    public void testMethod(String action) {
        if (_log.isDebugEnabled())
            _log.debug("testMethod", "Entered");
        HashMap _requestMap = new HashMap();
        HuaweiINHandler inHandler = new HuaweiINHandler();

        try {
            _requestMap.put("IN_TXN_ID", "C586666");
            _requestMap.put("MSISDN", "1813500018");
            // _requestMap.put("MSISDN","1815050020");
            _requestMap.put("INTERFACE_ID", "INTID00003");
            _requestMap.put("MODULE", "C2S");
            _requestMap.put("TRANSACTION_ID", "1500150015001500");
            _requestMap.put("INTERFACE_PREV_BALANCE", "50000");
            _requestMap.put("INTERFACE_AMOUNT", "5000.879");
            if (action.equals("validate"))
                inHandler.validate(_requestMap);
            else if (action.equals("creditAdjust")) {
                inHandler.creditAdjust(_requestMap);
            } else if (action.equals("credit")) {
                inHandler.credit(_requestMap);
            } else if (action.equals("debitAdjust")) {
                inHandler.debitAdjust(_requestMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("main Exception e=" + e.getMessage());
        }
        if (_log.isDebugEnabled())
            _log.debug("testMethod", "Exited");
    }
}
