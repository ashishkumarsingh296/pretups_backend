package com.inter.sos;

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

public class SOSTestServer extends HttpServlet{

    private static final Log log = LogFactory.getLog(SOSTestServer.class);
    
    private String yabxResponseFilePath;
    static final String CLASSNAME = "SOSTestServer";
    String flag="";
    /**
     * Constructor of the object.
     */
    public SOSTestServer() {
        super();
    }
    public void init(ServletConfig conf) throws ServletException
    {
    	final String methodName="init";
        if(log.isDebugEnabled())
        	log.debug(methodName,PretupsI.ENTERED);
        super.init(conf);
        yabxResponseFilePath = getServletContext().getRealPath(getInitParameter("yabxxmlfilepath"));
        if(log.isDebugEnabled())
        	log.debug(methodName,PretupsI.EXITED+" yabxResponseFilePath="+yabxResponseFilePath);
    }
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	final String methodName="doGet";
    	if (log.isDebugEnabled())
    		log.debug(methodName,PretupsI.ENTERED+"...Connected to SOSTestServer");
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
    		log.debug(methodName2,PretupsI.ENTERED+"...Connected to SOSTestServer");
        response.setContentType("text/xml");
        PrintWriter out = response.getWriter();
        String responseStr = null;
        StringBuilder lineBuff=null;
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
            Properties properties = new Properties();
            File file = new File(yabxResponseFilePath);
            properties.load(new FileInputStream(file));
            responseStr = properties.getProperty("YABX_RESPONSE");
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
