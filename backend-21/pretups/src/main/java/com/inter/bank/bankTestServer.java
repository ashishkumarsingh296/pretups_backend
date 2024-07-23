package com.inter.bank;

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

public class bankTestServer extends HttpServlet {
    private Log _log = LogFactory.getLog("bankTestServer".getClass().getName());
    private String requestResponseFilePath;

    public void init(ServletConfig conf) throws ServletException {
        if (_log.isDebugEnabled())
            _log.debug("init()", " Entered ");
        super.init(conf);
        requestResponseFilePath = getServletContext().getRealPath(getInitParameter("bankxmlfilepath"));
        if (_log.isDebugEnabled())
            _log.debug("init", "requestResponseFilePath" + requestResponseFilePath);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (_log.isDebugEnabled())
            _log.debug("doPost", "Entered");
        String responseStr = null;
        StringBuffer strBuff = null;
        String line = null;
        String message = null;
        HashMap map = null;
        File file = null;
        BankINRequestFormatter bankINRequestFormatter = null;
        try {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()));
            strBuff = new StringBuffer(1024);
            while ((line = in.readLine()) != null) {
                strBuff.append(line);
            }
            message = strBuff.toString();
            bankINRequestFormatter = new BankINRequestFormatter();
            map = bankINRequestFormatter.parseImmediateDebitRequest(message);
            if (_log.isDebugEnabled())
                _log.debug("request map", map);
            file = new File(requestResponseFilePath);
            Properties properties = new Properties();
            properties.load(new FileInputStream(file));
            responseStr = properties.getProperty("DEBIT_RESPONSE");
            out.print(responseStr);
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("bankTestServer", "Exited with Exception");
        }

    }

}
