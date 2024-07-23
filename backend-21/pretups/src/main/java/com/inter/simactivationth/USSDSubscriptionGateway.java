package com.inter.simactivationth;

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

public class USSDSubscriptionGateway extends HttpServlet {
    private String responseFilePath;

    /**
     * Constructor of the object.
     */
    public USSDSubscriptionGateway() {
        super();
    }

    public void init(ServletConfig conf) throws ServletException {
        super.init(conf);
        responseFilePath = getServletContext().getRealPath(getInitParameter("responseFilePath"));
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String responseStr = null;
        StringBuffer lineBuff = null;
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
            System.out.println("Request Got:" + message);
            Properties properties = new Properties();
            File file = new File(responseFilePath);
            properties.load(new FileInputStream(file));
            responseStr = properties.getProperty("DATA");
            out.print(responseStr);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            // _log.error("doPost","Exception e:"+e.getMessage());
        }
    }

    /**
     * Destruction of the servlet. <br>
     */
    public void destroy() {
        super.destroy();
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }// end of doPost

}
