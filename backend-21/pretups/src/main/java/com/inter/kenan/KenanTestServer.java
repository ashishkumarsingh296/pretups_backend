package com.inter.kenan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Properties;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.module.InterfaceUtil;

/**
 * @(#)KenanTestServer
 *                     Copyright(c) 2006, Bharti Telesoft Int. Public Ltd.
 *                     All Rights Reserved
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     Author Date History
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     Ashish Kumar Nov 22, 2006 Initial Creation
 *                     --------------------------------------------------------
 *                     ----------------------------------------
 *                     This class works as the simulator of Kenan Server
 */
public class KenanTestServer extends HttpServlet {
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private String kenanResponseFilePath;

    /**
     * Constructor of the object.
     */
    public KenanTestServer() {
        super();
    }

    public void init(ServletConfig conf) throws ServletException {
        if (_log.isDebugEnabled())
            _log.debug("init", "KenanTestServer Entered");
        super.init(conf);
        kenanResponseFilePath = getServletContext().getRealPath(getInitParameter("kenanxmlfilepath"));
        if (_log.isDebugEnabled())
            _log.debug("init", "Exiting kenanResponseFilePath=" + kenanResponseFilePath);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * Destruction of the servlet. <br>
     */
    public void destroy() {
        super.destroy();
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        KenanTestXMLParser kenanTestXMLParser = null;
        String responseStr = null;
        StringBuffer lineBuff = null;
        HashMap map = null;
        String lineSep = "";
        Properties properties = null;
        try {
            String message = "";
            lineSep = System.getProperty("line.separator");
            lineBuff = new StringBuffer();
            String strReq = "";
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            while ((strReq = bufferedReader.readLine()) != null)
                lineBuff.append(strReq + lineSep);
            message = lineBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("KenanTestServer", "message = " + message);
            properties = new Properties();
            File file = new File(kenanResponseFilePath);
            properties.load(new FileInputStream(file));
            responseStr = properties.getProperty("ACCOUNT_CREDIT");
            kenanTestXMLParser = new KenanTestXMLParser(new HashMap(properties));
            // Thread.sleep(3500);
            map = kenanTestXMLParser.parseRechargeCreditRequest(message);
            // if(!KenanI.RESULT_OK.equals((String)map.get("error-code")))
            // responseStr=getErrorResponse((String)map.get("error-code"),(String)map.get("error-desc"),responseStr);
            responseStr = getResponseString(map, responseStr);
            out.print(responseStr);
        } catch (Exception e) {
            _log.error("doPost", "Exception e:" + e.getMessage());
        }// end of catch-Exception
    }// end of dePost

    /**
     * This method is used to generate the erro response when responseCode would
     * be other than 0
     * 
     * @param String
     *            p_responseStr
     * @param HashMap
     *            p_map
     * @return String
     */
    public String getResponseString(HashMap p_map, String p_responseStr) {

        if (_log.isDebugEnabled())
            _log.debug("getErrorResponse", "Entered p_map:" + p_map + " p_responseStr:" + p_responseStr);
        String responseStr = p_responseStr;
        String errorCode = null;
        String errDesc = null;
        String multFactor = null;
        int intMulFactor = 1;
        long amountD = 0;
        long bonusD = 0;
        long totalAmt = 0;
        try {
            errorCode = (String) p_map.get("error-code");
            errDesc = (String) p_map.get("error-desc");
            multFactor = p_map.get("MULTIPLICATION_FACTOR") == null ? "1" : p_map.get("MULTIPLICATION_FACTOR").toString().trim();
            if (_log.isDebugEnabled())
                _log.debug("getResponseString", "multFactor");

            if (!KenanI.RESULT_OK.equals((String) p_map.get("error-code"))) {
                responseStr = responseStr.replaceAll("<error-desc>succeess</error-desc>", "<error-desc>" + errDesc + "</error-desc>");
                responseStr = responseStr.replaceAll("<status>success</status>", "<status>fail</status>");
                responseStr = responseStr.replaceAll("<error-code>17300</error-code>", "<error-code>" + errorCode + "</error-code>");
            }
            if (!InterfaceUtil.isNullString((String) p_map.get("bonus"))) {
                amountD = Long.parseLong((String) p_map.get("amount"));
                bonusD = Long.parseLong((String) p_map.get("bonus"));
                totalAmt = amountD + bonusD;
            } else
                totalAmt = Long.parseLong((String) p_map.get("amount"));
            try {
                intMulFactor = Integer.parseInt(multFactor);
            } catch (Exception e) {
                intMulFactor = 1;
            }
            if (_log.isDebugEnabled())
                _log.debug("getResponseString", "totalAmt::" + totalAmt);
            long amountLong = InterfaceUtil.getSystemAmount(totalAmt, intMulFactor);
            responseStr = responseStr.replaceAll("xxxxxx", String.valueOf(amountLong));
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("getErrorResponse", "Exception e:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getErrorResponse", "Exited respStr::" + responseStr);
        }
        return responseStr;
    }
}
