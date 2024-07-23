package com.inter.alcatelobo452;

/**
 * @(#)AlcatelONI452TestServer.java
 *                                  Copyright(c) 2011, Comviva Technologies Ltd.
 *                                  All Rights Reserved
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  ---------
 *                                  Author Date History
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  ---------
 *                                  Vinay Kumar Singh April 19, 2011 Initial
 *                                  Creation
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  --------
 *                                  This class can be used as an IN Simulator
 *                                  for the testing with integration with whole
 *                                  Pretups system.
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

public class AlcatelOBO452TestServer extends HttpServlet {
    private static final long serialVersionUID = -7830696682972760291L;
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private String requestResponseFilePath = null;
    private String accountInfoResponseStr = null;
    private String creditResponseStr = null;
    private String debitResponseStr = null;
    private File file = null;

    /**
     * Constructor of the object.
     */
    public AlcatelOBO452TestServer() {
        super();
    }

    public void init(ServletConfig conf) throws ServletException {
        if (_log.isDebugEnabled())
            _log.debug("AlcatelOBO452TestServer init()", " Entered ");
        super.init(conf);
        requestResponseFilePath = getServletContext().getRealPath(getInitParameter("alcatelONIxmlfilepath"));
        Properties properties = new Properties();
        file = new File(requestResponseFilePath);
        try {
            properties.load(new FileInputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
        accountInfoResponseStr = properties.getProperty("ACCOUNT_INFO");
        creditResponseStr = properties.getProperty("ACCOUNT_CREDIT");
        debitResponseStr = properties.getProperty("ACCOUNT_DEBIT");
        if (_log.isDebugEnabled())
            _log.debug("init ", "requestResponseFilePath: " + requestResponseFilePath);
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
            _log.debug("AlcatelOCI452SimulatorServlet", "doPost: Entered ");
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
                _log.debug("AlcatelOBO452TestServer", " requestStr: " + message);
            int index = message.indexOf("<action>");
            int intAction;
            String test = message.substring(index + 8, index + 10);
            if (test.substring(1).equals("<"))
                intAction = Integer.parseInt(test.substring(0, 1));
            else
                intAction = Integer.parseInt(test);
            if (_log.isDebugEnabled())
                _log.debug("doPost ", "intAction: " + intAction);
            if (intAction == AlcatelOBO452I.ACTION_GET_ACCOUNT_INFO) {
                // map = parser.parseGetAccountInfoResponse(message);
                responseStr = accountInfoResponseStr;
                if (_log.isDebugEnabled())
                    _log.debug("doPost", "Response String  ::" + responseStr);
                // String string =
                // responseStr.replaceFirst("PRETUPS5.1",(String)map.get("cp_id"));
                index = message.indexOf("<cp_transaction_id>");
                String cp_transaction_id = message.substring(index + "<cp_transaction_id>".length(), message.indexOf("</cp_transaction_id>", index));
                String string2 = responseStr.replaceFirst("YYYYYYYY", cp_transaction_id);
                // Thread .sleep(200);
                if (_log.isDebugEnabled())
                    _log.debug("doPost ", "ACCOUNT_INFO  Response: " + string2);
                out.print(string2);
            } else if (intAction == AlcatelOBO452I.ACTION_IMMEDIATE_DEBIT) {
                // map = parser.parseImmediateDebitRequest(message);
                responseStr = debitResponseStr;
                index = message.indexOf("<cp_transaction_id>");
                String cp_transaction_id = message.substring(index + "<cp_transaction_id>".length(), message.indexOf("</cp_transaction_id>", index));
                String string2 = responseStr.replaceFirst("YYYYYYYY", cp_transaction_id);
                if (_log.isDebugEnabled())
                    _log.debug("doPost ", "DEBIT  Response: " + string2);
                out.print(string2);
            } else if (intAction == AlcatelOBO452I.ACTION_IMMEDIATE_CREDIT) {
                // map = parser.parseRechargeCreditRequest(message);
                responseStr = creditResponseStr;
                index = message.indexOf("<cp_transaction_id>");
                String cp_transaction_id = message.substring(index + "<cp_transaction_id>".length(), message.indexOf("</cp_transaction_id>", index));
                String string2 = responseStr.replaceFirst("YYYYYYYY", cp_transaction_id);
                if (_log.isDebugEnabled())
                    _log.debug("doPost ", "CREDIT  Response: " + string2);
                out.print(string2);
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
