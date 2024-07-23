package com.inter.alcatel44;

/**
 * @(#)AlcatelRequestResponse.java
 *                                 Copyright(c) 2005, Bharti Telesoft Int.
 *                                 Public Ltd.
 *                                 All Rights Reserved
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Author Date History
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Pankaj K Namdev Dec 8,2006 Initial Creation
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 ------
 *                                 Request Response Formatting class for
 *                                 interface
 */

import java.util.HashMap;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class Alcatel44TestXml {
    public Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * This method is used to parse the xml string(Account Information) and
     * store the xml elements and value into HashMap.
     * 
     * @param String
     *            requestStr
     * @return HashMap
     * @throws Exception
     */
    public HashMap parseGetAccountInfoRequest(String requestStr) throws Exception {

        if (_log.isDebugEnabled())
            _log.debug("parseGetAccountInfoRequest", "Entered requestStr: " + requestStr);
        HashMap map = new HashMap();
        int index = 0;
        try {
            String requestXMLStr = requestStr.replaceAll("cp_req_websvr.dtd", "http://172.16.1.109:4301/pretups/xml/cp_req_websvr.dtd");
            index = requestXMLStr.indexOf("<cp_id>");
            String cp_id = requestXMLStr.substring(index + "<cp_id>".length(), requestXMLStr.indexOf("</cp_id>", index));
            map.put("cp_id", cp_id);
            index = requestXMLStr.indexOf("<cp_transaction_id>");
            String cp_transaction_id = requestXMLStr.substring(index + "<cp_transaction_id>".length(), requestXMLStr.indexOf("</cp_transaction_id>", index));
            map.put("cp_transaction_id", cp_transaction_id);
            index = requestXMLStr.indexOf("<op_transaction_id>");
            String op_transaction_id = requestXMLStr.substring(index + "<op_transaction_id>".length(), requestXMLStr.indexOf("</op_transaction_id>", index));
            map.put("op_transaction_id", op_transaction_id);
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
            _log.error("parseGetAccountInfoRequest", "Exception e: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseGetAccountInfoRequest test xml", "Exiting map: " + map);
        }// end of finally
    }// end of parseGetAccountInfoRequest

    /**
     * This method is used to parse the xml string(of RechargeCreditRequest) and
     * store the xml elements and value into HashMap
     * 
     * @param String
     *            requestStr
     * @return HashMap
     * @throws Exception
     */
    public HashMap parseRechargeCreditRequest(String requestStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseRechargeCreditRequest", "Entered requestStr: " + requestStr);
        HashMap map = new HashMap();
        int index = 0;
        try {
            String requestXMLStr = requestStr.replaceAll("cp_req_websvr.dtd", "http://172.16.1.109:5003/pretups/xml/cp_req_websvr.dtd");
            index = requestXMLStr.indexOf("<cp_id>");
            String cp_id = requestXMLStr.substring(index + "<cp_id>".length(), requestXMLStr.indexOf("</cp_id>", index));
            map.put("cp_id", cp_id);
            index = requestXMLStr.indexOf("<cp_transaction_id>");
            String cp_transaction_id = requestXMLStr.substring(index + "<cp_transaction_id>".length(), requestXMLStr.indexOf("</cp_transaction_id>", index));
            map.put("cp_transaction_id", cp_transaction_id);
            index = requestXMLStr.indexOf("<op_transaction_id>");
            String op_transaction_id = requestXMLStr.substring(index + "<op_transaction_id>".length(), requestXMLStr.indexOf("</op_transaction_id>", index));
            map.put("op_transaction_id", op_transaction_id);
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
            // Currency is optional field
            if (index > 0) {
                String transaction_currency = requestXMLStr.substring(index + "<transaction_currency>".length(), requestXMLStr.indexOf("</transaction_currency>", index));
                map.put("transaction_currency", transaction_currency);
            }

            /*
             * index = requestXMLStr.indexOf("<user_validity>");
             * String
             * userValidity=requestXMLStr.substring(index+"<user_validity>"
             * .length(),requestXMLStr.indexOf("</user_validity>",index));
             * map.put("user_validity",userValidity);
             * index=requestXMLStr.indexOf("<user_grace>");
             * String
             * graceDays=requestXMLStr.substring(index+"<user_grace>".length
             * (),requestXMLStr.indexOf("</user_grace>",index));
             * map.put("<user_grace>",graceDays);
             */
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
     * This method is used to parse the xml string(ImmediateDebitRequest)and
     * store the xml elements and value into HashMap
     * 
     * @param String
     *            requestStr
     * @return HashMap
     * @throws Exception
     */
    public HashMap parseImmediateDebitRequest(String requestStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseImmediateDebitRequest", "Entered requestStr: " + requestStr);
        HashMap map = new HashMap();
        int index = 0;
        try {
            String requestXMLStr = requestStr.replaceAll("cp_req_websvr.dtd", "http://172.16.1.109:4301/pretups/xml/cp_req_websvr.dtd");
            index = requestXMLStr.indexOf("<cp_id>");
            String cp_id = requestXMLStr.substring(index + "<cp_id>".length(), requestXMLStr.indexOf("</cp_id>", index));
            map.put("cp_id", cp_id);
            index = requestXMLStr.indexOf("<cp_transaction_id>");
            String cp_transaction_id = requestXMLStr.substring(index + "<cp_transaction_id>".length(), requestXMLStr.indexOf("</cp_transaction_id>", index));
            map.put("cp_transaction_id", cp_transaction_id);
            index = requestXMLStr.indexOf("<op_transaction_id>");
            String op_transaction_id = requestXMLStr.substring(index + "<op_transaction_id>".length(), requestXMLStr.indexOf("</op_transaction_id>", index));
            map.put("op_transaction_id", op_transaction_id);
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
            if (index > 0) {
                String transaction_currency = requestXMLStr.substring(index + "<transaction_currency>".length(), requestXMLStr.indexOf("</transaction_currency>", index));
                map.put("transaction_currency", transaction_currency);
            }
            return map;
        }// end of try block
        catch (Exception e) {
            _log.error("parseImmediateDebitRequest", "Exception e: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseImmediateDebitRequeston test xml", "Exiting map: " + map);
        }// end of finally
    }// end of parseImmediateDebitRequest
}