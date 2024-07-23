package com.inter.pg;

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
import com.btsl.util.BTSLUtil;

public class PaymentGatewayTestServer2 extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Log _log = LogFactory.getLog(this.getClass().getName());
    private String responseFilePath;

    /**
     * Constructor of the object.
     */
    public PaymentGatewayTestServer2() {
        super();
    }

    public void init(ServletConfig conf) throws ServletException {
        if (_log.isDebugEnabled())
            _log.debug("init", "Entered");
        super.init(conf);
        responseFilePath = getServletContext().getRealPath(getInitParameter("responseFilePath"));
        if (_log.isDebugEnabled())
            _log.debug("init", "Exiting responseFilePath=" + responseFilePath);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (_log.isDebugEnabled())
            _log.debug("PaymentGatewayTestServer2", "Entered...");
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
            _log.debug("PaymentGatewayTestServer2 doPost method", "Entered...");

        String paymentPopUpJsp = null;
        String accountDebitAdj = null;
        PrintWriter out = response.getWriter();
        try {
        	response.setContentType("text/html");

            Properties properties = new Properties();
            if(!BTSLUtil.isNullString(responseFilePath)) {
            	File file = new File(responseFilePath);
                properties.load(new FileInputStream(file));
                paymentPopUpJsp = properties.getProperty("PAYMENT_POPUP_1");
                accountDebitAdj = properties.getProperty("ACCOUNT_DEBIT_ADJ");
                _log.debug("paymentPopUpJsp: ", paymentPopUpJsp);
                request.setAttribute("ACCOUNT_DEBIT_ADJ", (String)accountDebitAdj);
                //out.write(paymentPopUpJsp);
                out.print(paymentPopUpJsp);
                out.flush();
                if(out != null)
                    out.close();
            }
            
        } catch (Exception e) {
            _log.error("doPost", "Exception e:" + e.getMessage());
        }
    }// end of doPost

}
