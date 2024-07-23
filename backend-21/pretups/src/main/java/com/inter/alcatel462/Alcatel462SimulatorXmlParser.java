package com.inter.alcatel462;

/**
 * @(#)Alcatel462SimulatorXmlParser.java
 *                                       Copyright(c) 2011, COMVIVA TECHNOLOGIES
 *                                       LIMITED. All rights reserved.
 *                                       COMVIVA PROPRIETARY/CONFIDENTIAL. Use
 *                                       is subject to license terms.
 *                                       All Rights Reserved
 *                                       --------------------------------------
 *                                       --
 *                                       --------------------------------------
 *                                       -------------------
 *                                       Author Date History
 *                                       --------------------------------------
 *                                       --
 *                                       --------------------------------------
 *                                       -------------------
 *                                       Vinay Kumar Singh January 25, 2011
 *                                       Initial Creation
 *                                       --------------------------------------
 *                                       --
 *                                       --------------------------------------
 *                                       ------------------
 *                                       This class is only used by IN(IN
 *                                       Simulator
 *                                       Alcatel462SimulatorServlet.java) for
 *                                       the parsing of response.
 */

import java.util.HashMap;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class Alcatel462SimulatorXmlParser {
    public Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * This Method parse GetAccount Info Response
     * 
     * @param String
     *            p_responseStr
     * @throws Exception
     * @return HashMap
     */
    public HashMap<String, String> parseGetAccountInfoResponse(String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseGetAccountInfoResponse", "Entered requestStr: " + p_responseStr);

        HashMap<String, String> map = new HashMap<String, String>();
        int index = 0;
        try {
            String requestXMLStr = p_responseStr.replaceAll("cp_reply.dtd", "http://202.56.229.157/pretups/xml/cp_req_websvr.dtd");
            index = requestXMLStr.indexOf("<cp_id>");
            String cp_id = requestXMLStr.substring(index + "<cp_id>".length(), requestXMLStr.indexOf("</cp_id>", index));
            map.put("cp_id", cp_id);
            index = requestXMLStr.indexOf("<cp_transaction_id>");
            String cp_transaction_id = requestXMLStr.substring(index + "<cp_transaction_id>".length(), requestXMLStr.indexOf("</cp_transaction_id>", index));
            map.put("cp_transaction_id", cp_transaction_id);
            index = requestXMLStr.indexOf("<application>");
            String application = requestXMLStr.substring(index + "<application>".length(), requestXMLStr.indexOf("</application>", index));
            map.put("application", application);
            index = requestXMLStr.indexOf("<action>");
            String action = requestXMLStr.substring(index + "<action>".length(), requestXMLStr.indexOf("</action>", index));
            map.put("action", action);
            index = requestXMLStr.indexOf("<user_id type=\"MSISDN\">");
            String user_id = requestXMLStr.substring(index + "<user_id type=\"MSISDN\">".length(), requestXMLStr.indexOf("</user_id>", index));
            map.put("user_id", user_id);
            return map;
        }// end of try block
        catch (Exception e) {
            _log.error("parseGetAccountInfoResponse", "Exception e: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseGetAccountInfoResponse test xml", "Exiting map: " + map);
        }// end of finally
    }// end of parseGetAccountInfoResponse

    /**
     * This method is used to parse the XML string(of RechargeCreditRequest) and
     * store the XML elements and value into HashMap
     * 
     * @param String
     *            requestStr
     * @return HashMap
     * @throws Exception
     */
    public HashMap<String, String> parseRechargeCreditRequest(String requestStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseRechargeCreditRequest", "Entered requestStr: " + requestStr);
        HashMap<String, String> map = new HashMap<String, String>();
        int index = 0;
        try {
            String requestXMLStr = requestStr.replaceAll("cp_reply.dtd", "http://202.56.229.157/pretups/xml/cp_req_websvr.dtd");
            index = requestXMLStr.indexOf("<cp_id>");
            String cp_id = requestXMLStr.substring(index + "<cp_id>".length(), requestXMLStr.indexOf("</cp_id>", index));
            map.put("cp_id", cp_id);
            index = requestXMLStr.indexOf("<cp_transaction_id>");
            String cp_transaction_id = requestXMLStr.substring(index + "<cp_transaction_id>".length(), requestXMLStr.indexOf("</cp_transaction_id>", index));
            map.put("cp_transaction_id", cp_transaction_id);
            index = requestXMLStr.indexOf("<credit_balance>");
            String application = requestXMLStr.substring(index + "<application>".length(), requestXMLStr.indexOf("</application>", index));
            map.put("application", application);
            index = requestXMLStr.indexOf("<action>");
            String action = requestXMLStr.substring(index + "<action>".length(), requestXMLStr.indexOf("</action>", index));
            map.put("action", action);
            index = requestXMLStr.indexOf("<user_id type=\"MSISDN\">");
            String user_id = requestXMLStr.substring(index + "<user_id type=\"MSISDN\">".length(), requestXMLStr.indexOf("</user_id>", index));
            map.put("<user_id", user_id);

            return map;
        }// end of try block
        catch (Exception e) {
            _log.error("parseRechargeCreditRequest", "Exception e: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }// end of catch(Exception e)
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseRechargeCreditRequeston test xml", "Exiting map: " + map);
        }// end of finally
    }// end of parseRechargeCreditRequeston

    /**
     * This method is used to parse the XML string(ImmediateDebitRequest)and
     * store the XML elements and value into HashMap
     * 
     * @param String
     *            requestStr
     * @return HashMap
     * @throws Exception
     */
    public HashMap<String, String> parseImmediateDebitRequest(String requestStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseImmediateDebitRequest", "Entered requestStr: " + requestStr);
        HashMap<String, String> map = new HashMap<String, String>();
        int index = 0;
        try {
            String requestXMLStr = requestStr.replaceAll("cp_reply.dtd", "http://202.56.229.157/pretups/xml/cp_req_websvr.dtd");
            index = requestXMLStr.indexOf("<cp_id>");
            String cp_id = requestXMLStr.substring(index + "<cp_id>".length(), requestXMLStr.indexOf("</cp_id>", index));
            map.put("cp_id", cp_id);
            index = requestXMLStr.indexOf("<cp_transaction_id>");
            String cp_transaction_id = requestXMLStr.substring(index + "<cp_transaction_id>".length(), requestXMLStr.indexOf("</cp_transaction_id>", index));
            map.put("cp_transaction_id", cp_transaction_id);
            index = requestXMLStr.indexOf("<credit_balance>");
            String application = requestXMLStr.substring(index + "<application>".length(), requestXMLStr.indexOf("</application>", index));
            map.put("application", application);
            index = requestXMLStr.indexOf("<action>");
            String action = requestXMLStr.substring(index + "<action>".length(), requestXMLStr.indexOf("</action>", index));
            map.put("action", action);
            index = requestXMLStr.indexOf("<user_id type=\"MSISDN\">");
            String user_id = requestXMLStr.substring(index + "<user_id type=\"MSISDN\">".length(), requestXMLStr.indexOf("</user_id>", index));
            map.put("<user_id", user_id);
            index = requestXMLStr.indexOf("<transaction_price>");
            String transaction_price = requestXMLStr.substring(index + "<transaction_price>".length(), requestXMLStr.indexOf("</transaction_price>", index));
            map.put("transaction_price", transaction_price);
            index = requestXMLStr.indexOf("<transaction_currency>");

            return map;
        }// end of try block
        catch (Exception e) {
            _log.error("parseImmediateDebitRequest", "Exception e: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseImmediateDebitRequest on test xml", "Exiting map: " + map);
        }// end of finally
    }// end of parseImmediateDebitRequest
}
