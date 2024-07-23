package com.inter.alcatel10;

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

import org.w3c.dom.Text;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class AlcatelTestServer extends HttpServlet {
    private Log _log = LogFactory.getLog(this.getClass().getName());

    private String constantsFilePath;

    public void init(ServletConfig conf) throws ServletException {
        System.out.println("ConfigServlet init() Entered ");
        super.init(conf);
        constantsFilePath = getServletContext().getRealPath(getInitParameter("alcatelxmlfilepath"));
        System.out.println("Alcatel File Path >>>>  " + constantsFilePath);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if (_log.isDebugEnabled())
            _log.debug("AlkatelTestServer mamammamamma", "Entered ");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String responseStr = null;

        try {
            AlcatelTestXml parser = new AlcatelTestXml();
            HashMap map = new HashMap();
            HashMap responseMap = new HashMap();
            int c = 0;
            String message = "";
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            while ((c = bufferedReader.read()) != -1) {

                message += (char) c;
            }

            if (_log.isDebugEnabled())
                _log.debug("AlkatelTestServer mamammamamma-------------------", message);

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

            System.out.print("intAction================" + intAction + "    ");

            Properties properties = new Properties();
            File file = new File(constantsFilePath);
            properties.load(new FileInputStream(file));

            if (intAction == AlcatelI.ACTION_ACCOUNT_INFO) {
                map = parser.parseGetAccountInfoRequest(message);

                responseStr = properties.getProperty("ACCOUNT_INFO");

                String string = responseStr.replaceFirst("BHARTIC2S", (String) map.get("cp_id"));
                String string2 = string.replaceFirst("000000", (String) map.get("cp_transaction_id"));
                Thread.sleep(200);
                System.out.println("AccountInfo  Response " + string2);
                out.print(string2);
            }
            if (intAction == AlcatelI.ACTION_IMMEDIATE_DEBIT) {
                map = parser.parseImmediateDebitRequest(message);
                responseStr = properties.getProperty("ACCOUNT_DEBIT");

                String string = responseStr.replaceFirst("BHARTIC2S", (String) map.get("cp_id"));
                String string2 = string.replaceFirst("000000", (String) map.get("cp_transaction_id"));
                Thread.sleep(250);
                System.out.println("DEBIT  Response " + string2);
                out.print(string2);

            }
            if (intAction == AlcatelI.ACTION_RECHARGE_CREDIT) {
                map = parser.parseRechargeCreditRequest(message);
                responseStr = properties.getProperty("ACCOUNT_CREDIT");
                String string = responseStr.replaceFirst("BHARTIC2S", (String) map.get("cp_id"));
                String string2 = string.replaceFirst("000000", (String) map.get("cp_transaction_id"));
                Thread.sleep(250);
                System.out.println("CREDIT  Response " + string2);
                out.print(string2);

            }
            if (intAction == AlcatelI.ACTION_TXN_CANCEL) {
                map = parser.parseRechargeCreditRequest(message);
                responseStr = properties.getProperty("TXN_CANCEL");
                String string = responseStr.replaceFirst("BHARTIC2S", (String) map.get("cp_id"));
                String string2 = string.replaceFirst("000000", (String) map.get("cp_transaction_id"));
                Thread.sleep(250);
                System.out.println("CANCEL  Response " + string2);
                out.print(string2);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}