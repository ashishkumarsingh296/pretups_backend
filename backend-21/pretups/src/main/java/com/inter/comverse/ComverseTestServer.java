package com.inter.comverse;

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

public class ComverseTestServer extends HttpServlet {
    // private Log _log = LogFactory.getLog(this.getClass().getName());
    private String responseFilePath;
    private long validationSleepTime;
    private long topupSleepTime;
    // private long currentTime;
    // private long wakeupTime;
    Properties properties = new Properties();

    /**
     * Constructor of the object.
     */
    public ComverseTestServer() {
        super();
    }

    public void init(ServletConfig conf) throws ServletException {
        try {
            // if(_log.isDebugEnabled()) _log.debug("init","Entered");
            super.init(conf);
            responseFilePath = getServletContext().getRealPath(getInitParameter("responseFilePath"));
            validationSleepTime = Long.parseLong(getInitParameter("validationSleepTime"));
            topupSleepTime = Long.parseLong(getInitParameter("topupSleepTime"));
            File file = new File(responseFilePath);
            properties.load(new FileInputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
            // _log.error("doPost","Exception e:"+e.getMessage());
        }
        // if(_log.isDebugEnabled())_log.debug("init","Exiting responseFilePath="+
        // responseFilePath);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // if (_log.isDebugEnabled())
        // _log.debug("ComverseTestServer","Entered...");
        doPost(request, response);
    }

    /**
     * Destruction of the servlet. <br>
     */
    public void destroy() {
        super.destroy();
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // if (_log.isDebugEnabled())
        // _log.debug("ComverseTestServer","Entered...");
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

        try {
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
            // _log.debug("ComverseTestServer","Request= "+message);
            System.out.println("Request Got:" + message);
            indexStart = message.indexOf("<soap:Body>") + "<soap:Body>".length() + lineSep.length() + 1;
            indexEnd = message.indexOf(" xmlns", indexStart);

            methodName = message.substring(indexStart, indexEnd);

            System.out.println("methodName:" + methodName + ":");
            // if(_log.isDebugEnabled())
            // _log.debug("doPost","methodName::"+methodName);

            File file = new File(responseFilePath);
            properties.load(new FileInputStream(file));

            if ("RetrieveSubscriberLite".equalsIgnoreCase(methodName.trim())) {
                currentTime = System.currentTimeMillis();
                wakeupTime = currentTime + validationSleepTime;
                System.out.println("Delay " + validationSleepTime);
                while (wakeupTime >= currentTime) {
                    try {
                        Thread.sleep(wakeupTime - currentTime);
                    } catch (InterruptedException ex) {
                    } // CATCH EXCEPTION
                    currentTime = System.currentTimeMillis(); // KEEP UPDATING
                                                              // variable
                                                              // currentTime
                                                              // with ACTUAL
                                                              // CURRENT TIME
                }
                responseStr = properties.getProperty("ACCOUNT_INFO");

            } else if ("CreditAccount".equalsIgnoreCase(methodName.trim())) {

                // if (message.contains("<CreditValue>-")) {
                if (message.contains("<rechValue>-")) {
                    currentTime = System.currentTimeMillis();
                    wakeupTime = currentTime + validationSleepTime;
                    System.out.println("Delay " + validationSleepTime);
                    while (wakeupTime >= currentTime) {
                        try {
                            Thread.sleep(wakeupTime - currentTime);
                        } catch (InterruptedException ex) {
                        } // CATCH EXCEPTION
                        currentTime = System.currentTimeMillis(); // KEEP
                                                                  // UPDATING
                                                                  // variable
                                                                  // currentTime
                                                                  // with ACTUAL
                                                                  // CURRENT
                                                                  // TIME
                    }
                    responseStr = properties.getProperty("ACCOUNT_DEBIT");
                	Thread.sleep(Long.parseLong(properties.getProperty("DEBIT_SLEEP").toString()));
                } else {
                    currentTime = System.currentTimeMillis();
                    wakeupTime = currentTime + validationSleepTime;
                    System.out.println("Delay " + validationSleepTime);
                    while (wakeupTime >= currentTime) {
                        try {
                            Thread.sleep(wakeupTime - currentTime);
                        } catch (InterruptedException ex) {
                        } // CATCH EXCEPTION
                        currentTime = System.currentTimeMillis(); // KEEP
                                                                  // UPDATING
                                                                  // variable
                                                                  // currentTime
                                                                  // with ACTUAL
                                                                  // CURRENT
                                                                  // TIME
                    }
                    responseStr = properties.getProperty("ACCOUNT_CREDIT_ADJ");
                	Thread.sleep(Long.parseLong(properties.getProperty("DEBIT_SLEEP").toString()));

                }

            } else if ("NonVoucherRecharge".equalsIgnoreCase(methodName.trim())) {
                currentTime = System.currentTimeMillis();
                wakeupTime = currentTime + validationSleepTime;
                System.out.println("Delay " + validationSleepTime);
                while (wakeupTime >= currentTime) {
                    try {
                        Thread.sleep(wakeupTime - currentTime);
                    } catch (InterruptedException ex) {
                    } // CATCH EXCEPTION
                    currentTime = System.currentTimeMillis(); // KEEP UPDATING
                                                              // variable
                                                              // currentTime
                                                              // with ACTUAL
                                                              // CURRENT TIME
                }
                responseStr = properties.getProperty("ACCOUNT_CREDIT");
            	Thread.sleep(Long.parseLong(properties.getProperty("REFILL_SLEEP").toString()));

            } else if ("RetrieveSubscriberWithIdentityNoHistory".equalsIgnoreCase(methodName.trim())) {
                currentTime = System.currentTimeMillis();
                wakeupTime = currentTime + validationSleepTime;
                System.out.println("Delay " + validationSleepTime);
                while (wakeupTime >= currentTime) {
                    try {
                        Thread.sleep(wakeupTime - currentTime);
                    } catch (InterruptedException ex) {
                    } // CATCH EXCEPTION
                    currentTime = System.currentTimeMillis(); // KEEP UPDATING
                                                              // variable
                                                              // currentTime
                                                              // with ACTUAL
                                                              // CURRENT TIME
                }
                responseStr = properties.getProperty("LANGUAGE_CODE");
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
