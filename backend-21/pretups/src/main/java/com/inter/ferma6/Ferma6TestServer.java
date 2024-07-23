package com.inter.ferma6;

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

public class Ferma6TestServer extends HttpServlet {

    private Log _log = LogFactory.getLog(this.getClass().getName());
    private String constantsFilePath;
    int counter = 0;
    static String txnID = "";

    public void init(ServletConfig conf) throws ServletException {
        System.out.println("ConfigServlet init() Entered ");
        super.init(conf);
        constantsFilePath = getServletContext().getRealPath(getInitParameter("fermaxmlfilepath"));
        System.out.println("Ferma File PAth >>>>  " + constantsFilePath);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (_log.isDebugEnabled())
            _log.debug("Ferma6TestServer ", "Entered ");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String responseStr = null;
        String message = null;
        try {
            Ferma6TestXmlParsere parser = new Ferma6TestXmlParsere();
            HashMap map = new HashMap();
            HashMap responseMap = new HashMap();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            message = bufferedReader.readLine();
            if (_log.isDebugEnabled())
                _log.debug("Ferma6TestServer", " message======== " + message);
            int intAction = parser.requestParser(message);
            if (_log.isDebugEnabled())
                _log.debug("Ferma6TestServer", " intAction======== " + intAction);
            Properties properties = new Properties();
            File file = new File(constantsFilePath);
            properties.load(new FileInputStream(file));
            if (intAction == 0) {
                // login request
                map = parser.constructMapFromStr(intAction, message);
                if (map.get("UserName").equals("user1") && map.get("Password").equals("user1") && map.get("ProtocolVersion").equals("1.0")) {
                    responseMap.put("Status", "0");
                    responseMap.put("TransactionId", map.get("TransactionId"));
                    responseMap.put("InterfaceId", String.valueOf(++counter));
                    responseStr = parser.constructStrFromMapRequest(intAction, responseMap);
                    System.out.println("loginresponse>>>>>>>>>>>>>>>>>" + responseStr);
                    out.print(responseStr);
                } else {
                    responseMap.put("Status", "4");
                    responseStr = parser.constructStrFromMapRequest(intAction, responseMap);
                    out.println(responseStr);
                }
            } else if (intAction == 1) {
                // get account information
                map = parser.constructMapFromStr(intAction, message);
                responseStr = properties.getProperty("ACCOUNT_INFO1");
                String toReplace = "0123456000";
                String replacedString = (String) map.get("AccessIdentifier");
                if (_log.isDebugEnabled())
                    _log.debug("toReplace = " + toReplace, " replacedString = " + replacedString + "responseStr = " + responseStr);
                String newString2 = responseStr.replaceFirst(toReplace, replacedString);
                String newString = newString2.replaceFirst("1111111A", ((String) map.get("TransactionId")));
                if (_log.isDebugEnabled())
                    _log.debug("AccountInfo1  Response = ", newString);
                out.print(newString);
                // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            } else if (intAction == 2) {
                // Balance Re-charge
                map = parser.constructMapFromStr(intAction, message);
                responseStr = properties.getProperty("RECHARGE_BALANCE");
                System.out.println("BalanceRecharge  Response " + " isdisdf :" + (String) map.get("TransactionId") + " txnID " + txnID);
                if (((String) map.get("TransactionId")).equals(txnID)) {
                    if (_log.isDebugEnabled())
                        _log.debug("Ferma6TestServer", "Action Balance Recharge. This is retry Request");
                    responseStr = responseStr.replaceFirst("<ser:Status>XXX</ser:Status>", properties.getProperty("FERMA_RETRY_RESPONSE_CODE"));
                } else {
                    if (_log.isDebugEnabled())
                        _log.debug("Ferma6TestServer", "Action Balance Recharge. sd This is not retry Request");
                    responseStr = responseStr.replaceFirst("<ser:Status>XXX</ser:Status>", properties.getProperty("FERMA_RESPONSE_CODE"));
                }
                System.out.println("responseStr responseStrresponseStr" + responseStr);
                txnID = (String) map.get("TransactionId");
                String toReplace = "0123456001";
                String replacedString = (String) map.get("AccessIdentifier");
                String newString2 = responseStr.replaceFirst(toReplace, replacedString);
                String newString = newString2.replaceFirst("1111111A", ((String) map.get("TransactionId")));
                System.out.println("BalanceRecharge  Response " + newString);
                // Thread.sleep(120000);
                response.setStatus(Integer.parseInt(properties.getProperty("HTTP_RESPONSE_CODE")));
                out.print(newString);
            } else if (intAction == 3) {
                // get account information
                map = parser.constructMapFromStr(intAction, message);
                responseStr = responseStr = responseStr = properties.getProperty("BALANCE_ADJUSTMENT");
                String toReplace = "0123456001";
                String replacedString = (String) map.get("AccessIdentifier");
                String newString2 = responseStr.replaceFirst(toReplace, replacedString);
                String newString = newString2.replaceFirst("1111111A", ((String) map.get("TransactionId")));
                System.out.println("AccountInfo  Response " + newString);
                // Thread.sleep(120000);
                out.print(newString);
            } else if (intAction == 4) {
                // get account information
                map = parser.constructMapFromStr(intAction, message);
                if (map != null) {
                    responseMap.put("Status", "0");
                    responseMap.put("TransactionId", String.valueOf(++counter));
                    responseStr = parser.constructStrFromMapRequest(intAction, responseMap);
                    System.out.println("responseStr=" + responseStr);
                    out.print(responseStr);
                }
            } else if (intAction == 5) {
                // get account info2
                map = parser.constructMapFromStr(intAction, message);
                responseStr = properties.getProperty("ACCOUNT_INFO2");
                String toReplace = "0123456000";
                String replacedString = (String) map.get("AccessIdentifier");
                String newString2 = responseStr.replaceFirst(toReplace, replacedString);
                String newString = newString2.replaceFirst("1111111A", ((String) map.get("TransactionId")));
                if (_log.isDebugEnabled())
                    _log.debug("AccountInfo2  Response = ", newString);
                // System.out.println("AccountInfo2  Response " + newString);
                out.print(newString);
                // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
