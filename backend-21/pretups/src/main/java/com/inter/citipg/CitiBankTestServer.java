package com.inter.citipg;

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

public class CitiBankTestServer extends HttpServlet {
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private String responseFilePath;

    /**
     * Constructor of the object.
     */
    public CitiBankTestServer() {
        super();
    }

    public void init(ServletConfig conf) throws ServletException {
        if (_log.isDebugEnabled())
            _log.debug("init", "Entered");
        super.init(conf);
        responseFilePath = getServletContext().getRealPath(getInitParameter("responseFilePath"));
        if (_log.isDebugEnabled())
            _log.debug("init", "Exiting responseFilePath=" + responseFilePath);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (_log.isDebugEnabled())
            _log.debug("CitiBankTestServer", "Entered...");
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
            _log.debug("CitiBankTestServer", "Entered...");
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
                _log.debug("CitiBankTestServer", "Request= " + message);
            indexStart = message.indexOf("<soap:Body>") + "<soap:Body>".length() + 3;
            indexEnd = message.indexOf(" xmlns", indexStart);

            methodName = message.substring(indexStart, indexEnd);

            if (_log.isDebugEnabled())
                _log.debug("doPost", "methodName::" + methodName);

            Properties properties = new Properties();
            File file = new File(responseFilePath);
            properties.load(new FileInputStream(file));

            if ("RetrieveSubscriberLite".equalsIgnoreCase(methodName.trim())) {
                responseStr = properties.getProperty("ACCOUNT_INFO");

            } else if ("CreditAccount".equalsIgnoreCase(methodName.trim())) {
                if (message.contains("<CreditValue>-"))
                    responseStr = properties.getProperty("ACCOUNT_DEBIT");
                else
                    responseStr = properties.getProperty("ACCOUNT_CREDIT_ADJ");

            } else if ("NonVoucherRecharge".equalsIgnoreCase(methodName.trim())) {
                responseStr = properties.getProperty("ACCOUNT_DEBIT");

            } else if ("RetrieveSubscriberWithIdentityNoHistory".equalsIgnoreCase(methodName.trim())) {
                responseStr = properties.getProperty("LANGUAGE_CODE");

            }
            out.print(responseStr);
            out.flush();
            System.out.println("responseStr==============" + responseStr);
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("doPost", "Exception e:" + e.getMessage());
        }
    }// end of doPost

}