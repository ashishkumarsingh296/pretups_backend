package com.inter.cs3;

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
import com.btsl.util.BTSLUtil;

/**
 * @(#)CS3TestServer
 *                   Copyright(c) 2006, Bharti Telesoft Int. Public Ltd.
 *                   All Rights Reserved
 *                   ----------------------------------------------------------
 *                   ---------------------------------------
 *                   Author Date History
 *                   ----------------------------------------------------------
 *                   ---------------------------------------
 *                   Ashish Kumar Sep 06,2006 Initial Creation
 *                   ----------------------------------------------------------
 *                   --------------------------------------
 *                   This class works as the simulator of CS3 Server
 */
public class CS3TestServer extends HttpServlet {
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private String cs3lResponseFilePath;

    /**
     * Constructor of the object.
     */
    public CS3TestServer() {
        super();
    }

    public void init(ServletConfig conf) throws ServletException {
        if (_log.isDebugEnabled())
            _log.debug("init", "Entered");
        super.init(conf);
        cs3lResponseFilePath = getServletContext().getRealPath(getInitParameter("cs3xmlfilepath"));
        if (_log.isDebugEnabled())
            _log.debug("init", "Exiting cs3lResponseFilePath=" + cs3lResponseFilePath);
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
        CS3TestXMLParser cs3TestXMLParser = null;
        String responseStr = null;
        StringBuffer lineBuff = null;
        int indexStart = 0;
        int indexEnd = 0;
        String methodName = "";
        String responseCode = "";
        HashMap map = null;
        String lineSep = "";
        try {
            map = new HashMap();
            String message = "";
            lineSep = System.getProperty("line.separator");
            lineBuff = new StringBuffer();
            String strReq = "";
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            while ((strReq = bufferedReader.readLine()) != null)
                lineBuff.append(strReq + lineSep);
            message = lineBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("CS3TestServer", "message = " + message);
            indexStart = message.indexOf("<methodName>");
            indexEnd = message.indexOf("</methodName>", indexStart);
            methodName = message.substring("<methodName>".length() + indexStart, indexEnd);
            if (_log.isDebugEnabled())
                _log.debug("doPost", "methodName::" + methodName);
            Properties properties = new Properties();
            File file = new File(cs3lResponseFilePath);
            properties.load(new FileInputStream(file));
            cs3TestXMLParser = new CS3TestXMLParser(new HashMap(properties));
            if ("GetAccountDetailsTRequest".equalsIgnoreCase(methodName.trim())) {
                if (_log.isDebugEnabled())
                    _log.debug("doPost", "ACTION_ACCOUNT_INTFO");
                map = cs3TestXMLParser.parseGetAccountInformation(message);
                responseCode = (String) map.get("responseCode");
                if (map.get("faultCode") != null)
                    responseStr = getFaultResponse(map);
                else if (!BTSLUtil.isNullString(responseCode) && !"0".equals(responseCode.trim()))
                    responseStr = getErrorResponse(responseCode);
                else
                    responseStr = properties.getProperty("ACCOUNT_INFO");
                out.print(responseStr);
            }
            if ("AdjustmentTRequest".equalsIgnoreCase(methodName.trim())) {
                map = cs3TestXMLParser.parseImmediateDebitRequest(message);
                responseCode = (String) map.get("responseCode");
                if (map.get("faultCode") != null)
                    responseStr = getFaultResponse(map);
                else if (!BTSLUtil.isNullString(responseCode) && !"0".equals(responseCode.trim()))
                    responseStr = getErrorResponse(responseCode);
                else
                    responseStr = properties.getProperty("ACCOUNT_DEBIT");
                out.print(responseStr);
            }
            if ("RefillTRequest".equalsIgnoreCase(methodName.trim())) {
                map = cs3TestXMLParser.parseRechargeCreditRequest(message);
                responseCode = (String) map.get("responseCode");
                if (map.get("faultCode") != null)
                    responseStr = getFaultResponse(map);
                else if (!BTSLUtil.isNullString(responseCode) && !"0".equals(responseCode.trim()))
                    responseStr = getErrorResponse(responseCode);
                else
                    responseStr = properties.getProperty("ACCOUNT_CREDIT");
                out.print(responseStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.print(getErrorResponse(CS3I.UNKNOWN_ERROR));
            _log.error("doPost", "Exception e:" + e.getMessage());
        }// end of catch-Exception
    }// end of dePost

    /**
     * This method is used to construct a fault xml string, contains the
     * faultCode with faultString
     * 
     * @param HashMap
     *            p_faultMap
     * @return String
     */
    private String getFaultResponse(HashMap p_faultMap) {
        if (_log.isDebugEnabled())
            _log.debug("getFaultResponse", "Entered p_faultMap::" + p_faultMap);
        String faultResponse = null;
        String lineSep = "";
        StringBuffer faultBuffer = null;
        try {
            lineSep = System.getProperty("line.separator");
            faultBuffer = new StringBuffer("<?xml version=\"1.0\"?>" + lineSep);
            faultBuffer.append("<methodResponse>" + lineSep);
            faultBuffer.append("<fault>" + lineSep);
            faultBuffer.append("<value>" + lineSep);
            faultBuffer.append("<struct>" + lineSep);
            faultBuffer.append("<member>" + lineSep);
            faultBuffer.append("<name>faultCode</name>" + lineSep);
            faultBuffer.append("<value><i4>" + p_faultMap.get("faultCode") + "</i4></value>" + lineSep);
            faultBuffer.append("</member>" + lineSep);
            faultBuffer.append("<member>" + lineSep);
            faultBuffer.append("<name>faultString</name>" + lineSep);
            faultBuffer.append("<value><string>" + p_faultMap.get("faultString") + "</string></value>" + lineSep);
            faultBuffer.append("</member>" + lineSep);
            faultBuffer.append("</struct>" + lineSep);
            faultBuffer.append("</value>" + lineSep);
            faultBuffer.append("</fault>" + lineSep);
            faultBuffer.append("</methodResponse>");
            faultResponse = faultBuffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("getFaultResponse", "Exception e=" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getFaultResponse", "Exited faultResponse::" + faultResponse);
        }
        return faultResponse;
    }// end of getFaultResponse

    /**
     * This method is used to generate the erro response when responseCode would
     * be other than 0
     * 
     * @param p_responseCode
     * @return String
     */
    private String getErrorResponse(String p_responseCode) {
        if (_log.isDebugEnabled())
            _log.debug("getErrorResponse", "Entered");
        String lineSep = "";
        String respStr = "";
        try {
            lineSep = System.getProperty("line.separator");
            StringBuffer response = new StringBuffer("<?xml version=\"1.0\" encoding=\"utf-8\"?>" + lineSep);
            response.append("<methodResponse>" + lineSep);
            response.append("<params>" + lineSep);
            response.append("<param>" + lineSep);
            response.append("<value>" + lineSep);
            response.append("<struct>" + lineSep);
            response.append("<member>" + lineSep);
            response.append("<name>responseCode</name>" + lineSep);
            response.append("<value><i4>" + p_responseCode + "</i4></value>" + lineSep);
            response.append("</member>" + lineSep);
            response.append("</struct>" + lineSep);
            response.append("</value>" + lineSep);
            response.append("</param>" + lineSep);
            response.append("</params>" + lineSep);
            response.append("</methodResponse>" + lineSep);
            respStr = response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("getErrorResponse", "Exception e:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getErrorResponse", "Exited respStr::" + respStr);
        }
        return respStr;
    }
}
