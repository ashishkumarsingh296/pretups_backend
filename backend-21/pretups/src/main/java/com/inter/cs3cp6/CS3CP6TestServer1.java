package com.inter.cs3cp6;

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

public class CS3CP6TestServer1 extends HttpServlet {
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private String cs3cp6lResponseFilePath;

    /**
     * Constructor of the object.
     */
    public CS3CP6TestServer1() {
        super();
    }

    public void init(ServletConfig conf) throws ServletException {
        if (_log.isDebugEnabled())
            _log.debug("init", "Entered");
        super.init(conf);
        cs3cp6lResponseFilePath = getServletContext().getRealPath(getInitParameter("cs3cp6xmlfilepath"));
        if (_log.isDebugEnabled())
            _log.debug("init", "Exiting cs3cp6lResponseFilePath=" + cs3cp6lResponseFilePath);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (_log.isDebugEnabled())
            _log.debug("CS3CP6TestServer1", "Entered...Connected to CS3CP6TestServer1");
        doPost(request, response);
    }

    /**
     * Destruction of the servlet. <br>
     */
    public void destroy() {
        super.destroy();
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (_log.isDebugEnabled())
            _log.debug("CS3CP6TestServer1", "Entered...Connected to CS3CP6TestServer1");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String responseStr = null;
        StringBuffer lineBuff = null;
        int indexStart = 0;
        int indexEnd = 0;
        String methodName = "";
        String lineSep = "";
        try {
            String message = "";
            lineSep = System.getProperty("line.separator");
            lineBuff = new StringBuffer();
            String strReq = "";
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            while ((strReq = bufferedReader.readLine()) != null)
                lineBuff.append(strReq + lineSep);
            message = lineBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("CS3CP6TestServer1", "message = " + message);
            indexStart = message.indexOf("<methodName>");
            indexEnd = message.indexOf("</methodName>", indexStart);
            methodName = message.substring("<methodName>".length() + indexStart, indexEnd);
            if (_log.isDebugEnabled())
                _log.debug("doPost", "methodName::" + methodName);
            Properties properties = new Properties();
            File file = new File(cs3cp6lResponseFilePath);
            properties.load(new FileInputStream(file));

            if ("GetAccountDetails".equalsIgnoreCase(methodName.trim())) {
                responseStr = properties.getProperty("ACCOUNT_DETAILS");

            } else if ("GetBalanceAndDate".equalsIgnoreCase(methodName.trim())) {
                responseStr = properties.getProperty("BALANCE_AND_DATE");

            } else if ("UpdateBalanceAndDate".equalsIgnoreCase(methodName.trim())) {
                if (message.contains("<string>-"))
                    responseStr = properties.getProperty("ACCOUNT_DEBIT");
                else
                    responseStr = properties.getProperty("ACCOUNT_CREDIT_ADJ");

            } else if ("Refill".equalsIgnoreCase(methodName.trim())) {
                responseStr = properties.getProperty("ACCOUNT_CREDIT");

            }
            out.print(responseStr);
        } catch (Exception e) {
            e.printStackTrace();
            // out.print(getErrorResponse(CS3I.UNKNOWN_ERROR));
            _log.error("doPost", "Exception e:" + e.getMessage());
        }// end of catch-Exception
    }// end of dePost

}
