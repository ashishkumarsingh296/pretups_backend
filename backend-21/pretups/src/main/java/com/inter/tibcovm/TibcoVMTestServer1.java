package com.inter.tibcovm;

import java.io.*;
import java.util.Properties;
import java.util.Random;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

public class TibcoVMTestServer1 extends HttpServlet
{
    private String responseFilePath;
    private int lowSleepTime;
    private int diffSleepTime;
    private static Properties properties = null;
    private static String ACCOUNT_CREDIT = null;
    private static String LANGUAGE_CODE = null;
    private static String ACCOUNT_INFO = null;

    public TibcoVMTestServer1()
    {
    }

    public void init(ServletConfig conf)
        throws ServletException
    {
        try
        {
            super.init(conf);
            responseFilePath = getServletContext().getRealPath(getInitParameter("responseFilePath"));
            lowSleepTime = Integer.parseInt(getInitParameter("validationSleepTime"));
            diffSleepTime = Integer.parseInt(getInitParameter("topupSleepTime"));
            properties = new Properties();
            File file = new File(responseFilePath);
            properties.load(new FileInputStream(file));
            ACCOUNT_CREDIT = properties.getProperty("ACCOUNT_CREDIT");
            LANGUAGE_CODE = properties.getProperty("LANGUAGE_CODE");
            ACCOUNT_INFO = properties.getProperty("ACCOUNT_INFO");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void destroy()
    {
        super.destroy();
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String responseStr = null;
        StringBuffer lineBuff = null;
        int indexStart = 0;
        int indexEnd = 0;
        String methodName = "";
        String lineSep = "";
        try
        {
            String message = "";
            lineSep = System.getProperty("line.separator");
            lineBuff = new StringBuffer();
            String strReq = "";
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            while((strReq = bufferedReader.readLine()) != null) 
                lineBuff.append((new StringBuilder(String.valueOf(strReq))).append(lineSep).toString());
            message = lineBuff.toString();
            System.out.println((new StringBuilder("Request Got:")).append(message).toString());
            indexStart = message.indexOf("<soap:Body>") + "<soap:Body>".length() + 2;
            indexEnd = message.indexOf(" xmlns", indexStart);
            methodName = message.substring(indexStart, indexEnd);
            System.out.println((new StringBuilder("methodName:")).append(methodName).append(":").toString());
            if("RetrieveSubscriberLite".equalsIgnoreCase(methodName.trim()))
            {
            	Random r = new Random();
            	int result = lowSleepTime + r.nextInt(diffSleepTime) ;
            	Thread.sleep(result);
            	System.out.println((new StringBuilder("Delay ")).append(result).toString());
                responseStr = ACCOUNT_INFO;
            } else
            if("CreditAccount".equalsIgnoreCase(methodName.trim()))
            {
                if(message.contains("<rechValue>-"))
                {
                	Random r = new Random();
                	int result = lowSleepTime + r.nextInt(diffSleepTime) ;
                	Thread.sleep(result);
                	System.out.println((new StringBuilder("Delay ")).append(result).toString());
                    responseStr = properties.getProperty("ACCOUNT_DEBIT");
                } else
                {
                	Random r = new Random();
                	int result = lowSleepTime + r.nextInt(diffSleepTime) ;
                	Thread.sleep(result);
                	System.out.println((new StringBuilder("Delay ")).append(result).toString());
                    responseStr = properties.getProperty("ACCOUNT_CREDIT_ADJ");
                }
            } else
            if("NonVoucherRecharge".equalsIgnoreCase(methodName.trim()))
            {
            	Random r = new Random();
            	int result = lowSleepTime + r.nextInt(diffSleepTime) ;
            	Thread.sleep(result);
            	System.out.println((new StringBuilder("Delay ")).append(result).toString());
                responseStr = ACCOUNT_CREDIT;
            } else
            if("RetrieveSubscriberWithIdentityNoHistory".equalsIgnoreCase(methodName.trim()))
                responseStr = LANGUAGE_CODE;
            out.print(responseStr);
            out.flush();
            if(out != null)
                out.close();
            if(bufferedReader != null)
                bufferedReader.close();
            System.out.println((new StringBuilder("Shishupal responseStr==============")).append(responseStr).toString());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

}