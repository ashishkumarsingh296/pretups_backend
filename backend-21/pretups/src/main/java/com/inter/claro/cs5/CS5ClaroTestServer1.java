package com.inter.claro.cs5;

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
import com.btsl.pretups.common.PretupsI;


public class CS5ClaroTestServer1 extends HttpServlet {
    private static final Log log = LogFactory.getLog(CS5ClaroTestServer1.class);
    
    private String cs5lResponseFilePath;
    static final String CLASSNAME = "CS5ClaroTestServer1";
    String flag="";
    /**
     * Constructor of the object.
     */
    public CS5ClaroTestServer1() {
        super();
    }
    @Override
    public void init(ServletConfig conf) throws ServletException
    {
    	final String methodName="init";
        if(log.isDebugEnabled())
        	log.debug(methodName,PretupsI.ENTERED);
        super.init(conf);
        cs5lResponseFilePath = getServletContext().getRealPath(getInitParameter("cs5claroaxmlfilepath"));
        if(log.isDebugEnabled())
        	log.debug(methodName,PretupsI.EXITED+" cs5lResponseFilePath="+cs5lResponseFilePath);
    }
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	final String methodName="doGet";
    	if (log.isDebugEnabled())
    		log.debug(methodName,PretupsI.ENTERED+"...Connected to CS5ClaroTestServer1");
        doPost(request, response);
    }
    /**
     * Destruction of the servlet. <br>
     */
    @Override
    public void destroy()
    {
        super.destroy(); 
    }
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	final String methodName2="doPost";
    	if (log.isDebugEnabled())
    		log.debug(methodName2,PretupsI.ENTERED+"...Connected to CS5ClaroTestServer1");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String responseStr = null;
        StringBuilder lineBuff=null;
        int indexStart =0;
        int indexEnd=0;
        String methodName="";
        String lineSep="";
        try
        {
            String message;
            lineSep=System.getProperty("line.separator");
            lineBuff= new StringBuilder();
            String strReq;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            while ((strReq = bufferedReader.readLine()) != null)
                lineBuff.append(strReq+lineSep);
            message = lineBuff.toString();
            if (log.isDebugEnabled())
            	log.debug(methodName2,"message = "+message);
            indexStart = message.indexOf("<methodName>");
            indexEnd = message.indexOf("</methodName>",indexStart);
            methodName = message.substring("<methodName>".length()+indexStart,indexEnd);
            if(log.isDebugEnabled())
            	log.debug("methodName::",methodName);
            Properties properties = new Properties();
            File file = new File(cs5lResponseFilePath);
            properties.load(new FileInputStream(file));
            if("100".equals(flag)){
            	responseStr = properties.getProperty("ACCOUNT_INFO_AGAIN");
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
            out.print(responseStr);
        } 
        catch (Exception e)
        {
            log.errorTrace(methodName2,e);
            try{
            	throw e;
            }
            catch(Exception ex)
            {
            	log.errorTrace(methodName2,ex);
            }
        }//end of catch-Exception
    }//end of dePost
    
}
