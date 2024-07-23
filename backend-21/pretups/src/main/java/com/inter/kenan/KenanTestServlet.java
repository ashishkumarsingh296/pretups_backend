package com.inter.kenan;

import java.io.IOException;
import java.util.HashMap;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

/**
 * @(#)KenanTestServlet
 *                      Copyright(c) 2006, Bharti Telesoft Int. Public Ltd.
 *                      All Rights Reserved
 *                      --------------------------------------------------------
 *                      -----------------------------------------
 *                      Author Date History
 *                      --------------------------------------------------------
 *                      -----------------------------------------
 *                      Ashish Kumar Nov 22, 2006 Initial Creation
 *                      --------------------------------------------------------
 *                      ----------------------------------------
 *                      This servlet is responsible to simulate the
 *                      KenanINHandler class to send the request.
 */

public class KenanTestServlet extends HttpServlet {
    private static Log _log = LogFactory.getLog(KenanTestServlet.class.getName());

    /**
     * Constructor of the object.
     */
    public KenanTestServlet() {
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
        testMethod();
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
        testMethod();
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

    public void testMethod() {
        if (_log.isDebugEnabled())
            _log.debug("testMethod", "Entered");
        HashMap _requestMap = new HashMap();
        KenanINHandler kenanINhandler = null;
        _requestMap.put("IN_TXN_ID", "C586666");
        _requestMap.put("INTERFACE_ID", "INTID00021");
        _requestMap.put("cp_id", "TOPCPID1");
        _requestMap.put("application", "1");
        _requestMap.put("INTERFACE_AMOUNT", "5000");
        _requestMap.put("VALIDITY_DAYS", "5");
        _requestMap.put("GRACE_DAYS", "3");
        // _requestMap.put("transaction_currency","1");
        _requestMap.put("op_transaction_id", "45");
        _requestMap.put("TRANSACTION_ID", "R060810.1350.0001");
        _requestMap.put("SENDER_MSISDN", "9868647394");
        _requestMap.put("OWNER_MSISDN", "1234567890");
        // Adding the Type of account in the request map
        _requestMap.put("CARD_GROUP", "1");
        _requestMap.put("MODULE", "C2S");
        _requestMap.put("NETWORK_CODE", "TS");
        // _requestMap.put("ADJUST","Y");
        // Debit request parameters
        _requestMap.put("INTERFACE_PREV_BALANCE", "1500");

        // Credit request parameters
        _requestMap.put("BONUS_AMOUNT", "100");
        _requestMap.put("BONUS_VALIDITY_DAYS", "1");
        try {
            _requestMap.put("MSISDN", "6999995");
            // String arr[] =
            // {"6999999","6999998","6999997","6999996","6999995","6999994"};
            kenanINhandler = new KenanINHandler();
            kenanINhandler.credit(_requestMap);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("main Exception e=" + e.getMessage());
        }
        if (_log.isDebugEnabled())
            _log.debug("testMethod", "Exited");
    }
}
