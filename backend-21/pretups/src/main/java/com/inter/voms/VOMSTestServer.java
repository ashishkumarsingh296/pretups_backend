package com.inter.voms;

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

public class VOMSTestServer extends HttpServlet {

  
	private static final long serialVersionUID = 1L;
	private static final Log _log = LogFactory.getLog(VOMSTestServer.class.getName());
    private String responseFilePath;
    private long validationSleepTime;
    private long topupSleepTime;
    // private long currentTime;
    // private long wakeupTime;
    Properties properties = new Properties();

    /**
     * Constructor of the object.
     */
    public VOMSTestServer() {
        super();
    }

    public void init(ServletConfig conf) throws ServletException {
    	super.init(conf);
    	responseFilePath = getServletContext().getRealPath(getInitParameter("responseFilePath"));
    	 File file = new File(responseFilePath);
    	try(FileInputStream fileInputStream=new FileInputStream(file)) {
            // if(_log.isDebugEnabled()) _log.debug("init","Entered");
            
            
            validationSleepTime = Long.parseLong(getInitParameter("validationSleepTime"));
            topupSleepTime = Long.parseLong(getInitParameter("topupSleepTime"));
           
            properties.load(fileInputStream);
        } catch (Exception e) {
            e.printStackTrace();
            // _log.error("doPost","Exception e:"+e.getMessage());
        }
        // if(_log.isDebugEnabled())_log.debug("init","Exiting responseFilePath="+
        // responseFilePath);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // if (_log.isDebugEnabled()) _log.debug("VOMSTestServer","Entered...");
        doPost(request, response);
    }

    /**
     * Destruction of the servlet. <br>
     */
    public void destroy() {
        super.destroy();
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // if (_log.isDebugEnabled()) _log.debug("VOMSTestServer","Entered...");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String responseStr = null;
        StringBuffer lineBuff = null;
        int indexStart = 0;
        int indexEnd = 0;
        String methodName = "";
        String lineSep = "";

        long currentTime;
        long wakeupTime;
        File file = new File(responseFilePath);
        try(FileInputStream fileInputStream=new FileInputStream(file)) {
            // out.print("");
            String message = "";
            lineSep = System.getProperty("line.separator");
            lineBuff = new StringBuffer();
            String strReq = "";
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getInputStream()));

            while ((strReq = bufferedReader.readLine()) != null)
                lineBuff.append(strReq + lineSep);

            message = lineBuff.toString();

            // message = "<soap:Body><RetrieveSubscriberLite xmlns=";

            // if (_log.isDebugEnabled())
            // _log.debug("VOMSTestServer","Request= "+message);
            System.out.println("Request Got:" + message);
            indexStart = message.indexOf("<soap:Body>") + "<soap:Body>".length() + 3;
            indexEnd = message.indexOf(" xmlns", indexStart);

            methodName = message.substring(indexStart, indexEnd);

            System.out.println("methodName:" + methodName + ":");
            // if(_log.isDebugEnabled())
            // _log.debug("doPost","methodName::"+methodName);

            
            properties.load(fileInputStream);

            if ("RetrieveSubscriberLite".equalsIgnoreCase(methodName.trim())) {
                currentTime = System.currentTimeMillis();
                wakeupTime = currentTime + validationSleepTime;
                System.out.println("Delay " + validationSleepTime);
                while (wakeupTime >= currentTime) {
                    try {
                        Thread.sleep(wakeupTime - currentTime);
                    } catch (InterruptedException ex) {
                    	_log.errorTrace(methodName, ex);
                    } // CATCH EXCEPTION
                    currentTime = System.currentTimeMillis(); // KEEP UPDATING
                                                              // variable
                                                              // currentTime
                                                              // with ACTUAL
                                                              // CURRENT TIME
                }
                responseStr = properties.getProperty("VOUCHER_INFO");

            }
            out.print(responseStr);
            out.flush();
            System.out.println("responseStr==============" + responseStr);
        } catch (Exception e) {
            e.printStackTrace();
            // _log.error("doPost","Exception e:"+e.getMessage());
        }
    }// end of doPost

}
