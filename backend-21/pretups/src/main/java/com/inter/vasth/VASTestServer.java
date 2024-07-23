package com.inter.vasth;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class VASTestServer extends HttpServlet {
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private String vasResponseFilePath;

    /**
     * Constructor of the object.
     */
    public VASTestServer() {
        super();
    }

    public void init(ServletConfig conf) throws ServletException {
        if (_log.isDebugEnabled())
            _log.debug("init", "Entered");
        super.init(conf);
        vasResponseFilePath = getServletContext().getRealPath(getInitParameter("vasxmlfilepath"));
        if (_log.isDebugEnabled())
            _log.debug("init", "Exiting vasResponseFilePath=" + vasResponseFilePath);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (_log.isDebugEnabled())
            _log.debug("VASTestServer", "Entered...Connected to VASTestServer");
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
            _log.debug("VASTestServer", "Entered...Connected to VASTestServer");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String responseStr = null;
        String productCode = null;
        String transactionID = null;

        try {

            productCode = request.getParameter("code");
            transactionID = request.getParameter("TransactionID");
            if (_log.isDebugEnabled())
                _log.debug("doPost", "methodName::" + productCode);
            Properties properties = new Properties();
            File file = new File(vasResponseFilePath);
            properties.load(new FileInputStream(file));

            if ("1".equalsIgnoreCase(productCode.trim())) {
                responseStr = properties.getProperty("test");

            } else if ("Msg".equalsIgnoreCase(productCode.trim())) {
                responseStr = properties.getProperty("Msg");

            } else if ("5".equalsIgnoreCase(productCode.trim())) {
                responseStr = properties.getProperty("RmFree");

            } else if ("2".equalsIgnoreCase(productCode.trim())) {
                responseStr = properties.getProperty("GPRS");

            } else if("3".equalsIgnoreCase(productCode.trim())) {
                responseStr = properties.getProperty("mEssaGe");
               
            } 
            else if ("4".equalsIgnoreCase(productCode.trim())) {
                responseStr = properties.getProperty("Ct001");

            } else if ("message".equalsIgnoreCase(productCode.trim())) {
                responseStr = properties.getProperty("message");

            }
            /*
             * else if("VAS07".equalsIgnoreCase(productCode.trim()))
             * {
             * responseStr = properties.getProperty("VAS07");
             * 
             * }
             * else if("VAS08".equalsIgnoreCase(productCode.trim()))
             * {
             * responseStr = properties.getProperty("VAS08");
             * 
             * }
             * else if("VAS09".equalsIgnoreCase(productCode.trim()))
             * {
             * responseStr = properties.getProperty("VAS09");
             * 
             * }
             * else if("VAS10".equalsIgnoreCase(productCode.trim()))
             * {
             * responseStr = properties.getProperty("VAS10");
             * 
             * }
             */
            else {
                responseStr = properties.getProperty("INVALID_PRODUCT");

            }
            out.print(responseStr + transactionID);
        } catch (Exception e) {
            e.printStackTrace();

            _log.error("doPost", "Exception e:" + e.getMessage());
        }// end of catch-Exception
    }// end of dePost

}
