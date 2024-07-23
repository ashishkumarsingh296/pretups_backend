package com.inter.gp.cs5;

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


public class CS5GpTestServer1 extends HttpServlet {
    private Log log = LogFactory.getLog(this.getClass().getName());
    private String cs5lResponseFilePath;
    private String flag="";
    /**
     * Constructor of the object.
     */
    public CS5GpTestServer1() {
        super();
    }
     public void init(ServletConfig conf) throws ServletException
    {
    	 String METHOD_NAME="init";
    	 LogFactory.printLog(METHOD_NAME,"Entered",log);
        super.init(conf);
        cs5lResponseFilePath = getServletContext().getRealPath(getInitParameter("cs5gpaxmlfilepath"));
        LogFactory.printLog(METHOD_NAME,"Exiting cs5lResponseFilePath="+cs5lResponseFilePath,log);
    }
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	String METHOD_NAME="doGet";
    	LogFactory.printLog(METHOD_NAME,"Entered",log);
    	LogFactory.printLog(METHOD_NAME,"Entered...Connected to CS5GpTestServer1",log);
        doPost(request, response);
    }
    /**
     * Destruction of the servlet. <br>
     */
    public void destroy()
    {
        super.destroy(); 
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	 String METHOD_NAME="doPost";
    	LogFactory.printLog(METHOD_NAME,"Entered...Connected to CS5GpTestServer1",log);
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String responseStr = null;
        StringBuffer lineBuff=null;
        int indexStart =0;
        int indexEnd=0;
        String methodName="";
        String lineSep="";
        try
        {
            String message = "";
            lineSep=System.getProperty("line.separator");
            lineBuff= new StringBuffer();
            String strReq="";
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getInputStream())))
            {
            while ((strReq = bufferedReader.readLine()) != null)
                lineBuff.append(strReq+lineSep);
            message = lineBuff.toString();
            LogFactory.printLog(METHOD_NAME,"message = "+message,log);
            indexStart = message.indexOf("<methodName>");
            indexEnd = message.indexOf("</methodName>",indexStart);
            methodName = message.substring("<methodName>".length()+indexStart,indexEnd);
            LogFactory.printLog(METHOD_NAME,"methodName::"+methodName,log);
            Properties properties = new Properties();
            File file = new File(cs5lResponseFilePath);
            properties.load(new FileInputStream(file));
           // System.out.println("ABCM");
           // System.out.println("flag"+flag);
            if(("100").equals(flag)){
            	responseStr = properties.getProperty("ACCOUNT_INFO_AGAIN");
            	//System.out.println("getaccount info"+responseStr);
            	flag="";
            }
            else if ("GetBalanceAndDate".equalsIgnoreCase(methodName.trim()) )
            {
              responseStr = properties.getProperty("ACCOUNT_INFO");
             
            }
            else if ("UpdateBalanceAndDate".equalsIgnoreCase(methodName.trim()))
            {
            	if (message.contains("<string>-"))
                    responseStr = properties.getProperty("ACCOUNT_DEBIT");
            	else
            		responseStr = properties.getProperty("ACCOUNT_CREDIT_ADJ");
           
            }
            else if ("Refill".equalsIgnoreCase(methodName.trim()))
            {
            	
                responseStr = properties.getProperty("ACCOUNT_CREDIT");
                flag="100";
               
            }
          /*  else if ("GetAccountDetails".equalsIgnoreCase(methodName.trim()))
            {
                responseStr = properties.getProperty("ACCOUNT_DETAILS");
               
            }*/
            out.print(responseStr);
        } 
        catch (Exception e)
        {
        	log.errorTrace("doPost",e);
           // out.print(getErrorResponse(CS5I.UNKNOWN_ERROR));
            log.error("doPost","Exception e:"+e.getMessage());
        }//end of catch-Exception
    }//end of dePost
        catch (Exception e)
        {
        	log.errorTrace("doPost",e);
           // out.print(getErrorResponse(CS5I.UNKNOWN_ERROR));
            log.error("doPost","Exception e:"+e.getMessage());
        } 
    
    }
}