package com.inter.alcatel432;

/**
 * @(#)Alcatel432RequestFormatter
 *                                Copyright(c) 2006, Bharti Telesoft Int. Public
 *                                Ltd.
 *                                All Rights Reserved
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Author Date History
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Ashish Kumar May 03,2006 Initial Creation
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                ----
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

public class Alcatel432TestServer extends HttpServlet {
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private String alcatelResponseFilePath;

    public void init(ServletConfig conf) throws ServletException {
        System.out.println("ConfigServlet init() 1`3131231231 Entered ");
        super.init(conf);
        alcatelResponseFilePath = getServletContext().getRealPath(getInitParameter("alcatel43xmlfilepath"));
        System.out.println("Alcatel Response File Path=" + alcatelResponseFilePath);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("INSIDE TEST SERVER-----------------------");
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (_log.isDebugEnabled())
            _log.debug("Alcatel432TestServer", "Entered ");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String responseStr = null;
        try {
            Alcatel432TestXML parser = new Alcatel432TestXML();
            HashMap map = new HashMap();
            HashMap responseMap = new HashMap();
            int c = 0;
            String message = "";
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            while ((c = bufferedReader.read()) != -1) {
                message += (char) c;
            }
            if (_log.isDebugEnabled())
                _log.debug("Alcatel432TestServer", "message = " + message);
            int index = message.indexOf("<action>");
            int intAction;
            String test = message.substring(index + 8, index + 10);
            if (test.substring(1).equals("<"))
                intAction = Integer.parseInt(test.substring(0, 1));
            else
                intAction = Integer.parseInt(test);
            int strIndex = message.indexOf("<cp_transaction_id>");
            int endIndex = message.indexOf("</cp_transaction_id>");
            String transID = message.substring(strIndex + 19, endIndex);
            int strIndexCpid = message.indexOf("<cp_id>");
            int endIndexCpid = message.indexOf("</cp_id>");
            String CpID = message.substring(strIndexCpid + 7, endIndexCpid);
            System.out.print("intAction= " + intAction);
            Properties properties = new Properties();
            File file = new File(alcatelResponseFilePath);
            properties.load(new FileInputStream(file));
            if (intAction == Alcatel432I.ACTION_ACCOUNT_INFO) {
                if (_log.isDebugEnabled())
                    _log.debug("doPost", "ACTION_COUNT_INTFO");
                map = parser.parseGetAccountInfoRequest(message);
                responseStr = properties.getProperty("ACCOUNT_INFO");
                String string = responseStr.replaceFirst("BHARTIC2S", (String) map.get("cp_id"));
                String string2 = string.replaceFirst("000000", (String) map.get("cp_transaction_id"));
                // Thread .sleep(200);
                System.out.println("AccountInfo  Response " + string2);
                out.print(string2);
            }
            if (intAction == Alcatel432I.ACTION_IMMEDIATE_DEBIT) {
                map = parser.parseImmediateDebitRequest(message);
                responseStr = properties.getProperty("ACCOUNT_DEBIT");
                String string = responseStr.replaceFirst("BHARTIC2S", (String) map.get("cp_id"));
                String string2 = string.replaceFirst("000000", (String) map.get("cp_transaction_id"));
                // Thread .sleep(250);
                System.out.println("DEBIT  Response " + string2);
                out.print(string2);
            }
            if (intAction == Alcatel432I.ACTION_RECHARGE_CREDIT) {
                map = parser.parseRechargeCreditRequest(message);
                responseStr = properties.getProperty("ACCOUNT_CREDIT");
                String string = responseStr.replaceFirst("BHARTIC2S", (String) map.get("cp_id"));
                String string2 = string.replaceFirst("000000", (String) map.get("cp_transaction_id"));
                // Thread .sleep(250);
                System.out.println("CREDIT  Response " + string2);
                out.print(string2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
