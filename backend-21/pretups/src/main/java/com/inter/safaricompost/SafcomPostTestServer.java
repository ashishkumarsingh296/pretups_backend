
package com.inter.safaricompost;

/**
 * @(#)SafcomPostTestServer.java
 * Copyright(c) 2008, Bharti Telesoft Int. Public Ltd.
 * All Rights Reserved
 *-------------------------------------------------------------------------------------------------
 * Author				Date			History
 *-------------------------------------------------------------------------------------------------
 *Manisha Jain			09 june	2008	Initial creation
 * ------------------------------------------------------------------------------------------------
 * Test server class for the interface Post Paid billing System
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

public class SafcomPostTestServer extends HttpServlet
{
	
    private Log _log = LogFactory.getLog(this.getClass().getName());

    private String constantsFilePath;

    public void init(ServletConfig conf) throws ServletException
    {
        System.out.println("SafcomPostTestServer init() Entered ");
        super.init(conf);
        constantsFilePath = getServletContext().getRealPath(getInitParameter("safaricomsxmlfilepath"));
        System.out.println("Safaricom File Path >>>>  " + constantsFilePath);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        if (_log.isDebugEnabled())
            _log.debug("SafcomPostTestServer  ", "Entered ");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String responseStr = null;

        try
        {
            int c = 0;
            String message = "";
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            while ((c = bufferedReader.read()) != -1)
            {

                message += (char) c;
            }
            String startTag="<ns0:ACTION>";
            String endTag="</ns0:ACTION>";
            int startIndex = message.indexOf(startTag);
            int endIndex = message.indexOf(endTag);
            if (_log.isDebugEnabled())
                _log.debug("SafcomPostTestServer  ", "message: "+message);
      
            int intAction=-1;
            if (_log.isDebugEnabled())
                _log.debug("SafcomPostTestServer  ", "test: "+message.substring(startIndex + startTag.length(), endIndex));
      
            String test =message.substring(startIndex + startTag.length(), endIndex).trim();
            intAction = Integer.parseInt(test);
        
            startTag = "<ns0:TXNID>";
            endTag = "</ns0:TXNID>";
            startIndex = message.indexOf(startTag);
            endIndex = message.indexOf(endTag);
            String transID = message.substring(startIndex + startTag.length(), endIndex).trim();

            System.out.print("intAction================" + intAction + " =========== transID========= "+transID);

            Properties properties = new Properties();
            File file = new File(constantsFilePath);
            properties.load(new FileInputStream(file));

            if (intAction == SafaricomPostI.ACTION_ACCOUNT_INFO)
            {
                responseStr = properties.getProperty("ACCOUNT_INFO");
                String string = responseStr.replaceFirst("xxxx",transID);
                Thread .sleep(200);
                System.out.println("AccountInfo  Response " + string);
                out.print(string);
            }
            if (intAction == SafaricomPostI.ACTION_CREDIT)
            {
                responseStr = properties.getProperty("ACCOUNT_CREDIT");
                String string = responseStr.replaceFirst("xxxx",transID);
				Thread .sleep(250);
                System.out.println("CREDIT  Response " + string);
                out.print(string);
            }
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
