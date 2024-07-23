package com.inter.alcatel442;

/**
 * @(#)Alcatel442TestServer.java
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
 *                               Dhiraj Tiwari Oct 16,2007 Initial Creation
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               --
 * 
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Properties;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class Alcatel442TestServer extends HttpServlet {
    private Log _log = LogFactory.getLog(this.getClass().getName());

    private String requestResponseFilePath;

    public void init(ServletConfig conf) throws ServletException {
        if (_log.isDebugEnabled())
            _log.debug("init()", " Entered ");
        super.init(conf);
        requestResponseFilePath = getServletContext().getRealPath(getInitParameter("alcatel442xmlfilepath"));
        if (_log.isDebugEnabled())
            _log.debug("init", "requestResponseFilePath" + requestResponseFilePath);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if (_log.isDebugEnabled())
            _log.debug("Alcatel442TestServer", "Entered ");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String responseStr = null;
        HashMap map = null;
        StringBuffer sbf = null;
        Alcatel442TestXml parser = null;
        File file = null;
        try {
            sbf = new StringBuffer(1028);
            parser = new Alcatel442TestXml();
            map = new HashMap();
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
                _log.debug("Alcatel442TestServer", "requestStr" + message);
            int index = message.indexOf("<action>");
            int intAction;
            String test = message.substring(index + 8, index + 10);
            if (test.substring(1).equals("<"))
                intAction = Integer.parseInt(test.substring(0, 1));
            else
                intAction = Integer.parseInt(test);
            if (_log.isDebugEnabled())
                _log.debug("doPost", "intAction" + intAction);
            Properties properties = new Properties();
            file = new File(requestResponseFilePath);
            properties.load(new FileInputStream(file));
            if (intAction == Alcatel442I.ACTION_ACCOUNT_INFO) {
                map = parser.parseGetAccountInfoRequest(message);

                responseStr = properties.getProperty("ACCOUNT_INFO");
                if (_log.isDebugEnabled())
                    _log.debug("doPost", "Response String  ::" + responseStr);
                String string = responseStr.replaceFirst("BHARTIC2S", (String) map.get("cp_id"));
                String string2 = string.replaceFirst("YYYYYYYY", (String) map.get("cp_transaction_id"));
                Thread.sleep(200);
                if (_log.isDebugEnabled())
                    _log.debug("doPost", "AccountInfo  Response " + string2);
                out.print(string2);
            } else if (intAction == Alcatel442I.ACTION_IMMEDIATE_DEBIT) {
                map = parser.parseImmediateDebitRequest(message);
                responseStr = properties.getProperty("ACCOUNT_DEBIT");

                String string = responseStr.replaceFirst("BHARTIC2S", (String) map.get("cp_id"));
                String string2 = string.replaceFirst("YYYYYYYY", (String) map.get("cp_transaction_id"));
                Thread.sleep(250);
                if (_log.isDebugEnabled())
                    _log.debug("doPost", "DEBIT  Response " + string2);
                out.print(string2);

            } else if (intAction == Alcatel442I.ACTION_RECHARGE_CREDIT) {
                map = parser.parseRechargeCreditRequest(message);
                responseStr = properties.getProperty("ACCOUNT_CREDIT");
                String string = responseStr.replaceFirst("BHARTIC2S", (String) map.get("cp_id"));
                String string2 = string.replaceFirst("YYYYYYYY", (String) map.get("cp_transaction_id"));
                Thread.sleep(250);
                if (_log.isDebugEnabled())
                    _log.debug("doPost", "CREDIT  Response " + string2);
                out.print(string2);
            }
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("doPost", "Exception e:" + e.getMessage());
        }// end of catch-Exception
    }
}