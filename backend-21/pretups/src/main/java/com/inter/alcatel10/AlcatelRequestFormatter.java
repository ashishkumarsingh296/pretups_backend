package com.inter.alcatel10;

/**
 * @(#)AlcatelRequestFormatter.java
 *                                  Copyright(c) 2005, Bharti Telesoft Int.
 *                                  Public Ltd.
 *                                  All Rights Reserved
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  ---------
 *                                  Author Date History
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  ---------
 *                                  Manoj kumar Jan 21,2006 Initial Creation
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  --------
 *                                  Request Response Formatting class for
 *                                  interface
 */
import java.util.HashMap;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.util.BTSLUtil;

public class AlcatelRequestFormatter {
    public Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * Get IN Reconciliation Txn ID
     * 
     * @param p_requestMap
     * @return
     */
    private String getINReconTxnID(HashMap p_requestMap) {
        // return ((String)p_requestMap.get("IN_TXN_ID"));
        String inReconID = null;
        String userType = (String) p_requestMap.get("USER_TYPE");
        if (userType != null)
            inReconID = ((String) p_requestMap.get("TRANSACTION_ID") + "." + userType);
        else
            inReconID = ((String) p_requestMap.get("TRANSACTION_ID"));
        p_requestMap.put("IN_RECON_ID", inReconID);
        return inReconID;
    }

    /**
     * this method construct the request in XML String from HashMap
     * 
     * @param action
     *            int
     * @param map
     *            java.util.HashMap
     * @return str java.lang.String
     */
    protected String generateRequest(int action, HashMap map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateRequest", "Entered map: " + map);
        String str = null;
        map.put("action", String.valueOf(action));
        switch (action) {
        case AlcatelI.ACTION_ACCOUNT_INFO: {
            str = generateGetAccountInfoRequest(map);
            break;
        }
        case AlcatelI.ACTION_RECHARGE_CREDIT: {
            str = generateRechargeCreditRequest(map);
            break;
        }
        case AlcatelI.ACTION_IMMEDIATE_DEBIT: {
            str = generateImmediateDebitRequest(map);
            break;
        }
        case AlcatelI.ACTION_TXN_CANCEL: {
            str = generateCanceTxnRequest(map);
            break;
        }
        }
        if (_log.isDebugEnabled())
            _log.debug("generateRequest", "Exited Request String:  " + str);
        return str;
    }

    /**
     * this method parse the response from XML String into HashMap
     * 
     * @param action
     *            int
     * @param responseStr
     *            java.lang.String
     * @return map java.util.HashMap
     */

    protected HashMap parseResponse(int action, String responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseResponse", "Entered Response String:  " + responseStr);
        HashMap map = null;
        switch (action) {
        case AlcatelI.ACTION_ACCOUNT_INFO: {
            map = parseGetAccountInfoResponse(responseStr);
            break;
        }
        case AlcatelI.ACTION_RECHARGE_CREDIT: {
            map = parseRechargeCreditResponse(responseStr);
            break;
        }
        case AlcatelI.ACTION_IMMEDIATE_DEBIT: {
            map = parseImmediateDebitResponse(responseStr);
            break;
        }
        case AlcatelI.ACTION_TXN_CANCEL: {
            map = parseCancelTxnResponse(responseStr);
            break;
        }
        }
        if (_log.isDebugEnabled())
            _log.debug("parseResponse", "Exiting map: " + map);
        return map;
    }

    /**
     * This Method generate account information request
     * 
     * @param map
     *            HashMap
     * @throws Exception
     * @return requestStr
     */
    private String generateGetAccountInfoRequest(HashMap map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateGetAccountInfoRequest", "Entered map=" + map);
        String requestStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1028);
            sbf.append("<?xml version=\"1.0\"?>");
            sbf.append("<!DOCTYPE cp_request SYSTEM \"cp_req_websvr.dtd\">");
            sbf.append("<cp_request>");
            sbf.append("<cp_id>" + map.get("cp_id") + "</cp_id>");
            sbf.append("<cp_transaction_id>" + getINReconTxnID(map) + "</cp_transaction_id>");
            sbf.append("<op_transaction_id></op_transaction_id>");
            sbf.append("<application>" + map.get("application") + "</application>");
            sbf.append("<action>" + map.get("action") + "</action>");
            sbf.append("<user_id type=\"MSISDN\">" + InterfaceUtil.getFilterMSISDN((String) map.get("INTERFACE_ID"), (String) map.get("MSISDN")) + "</user_id>");
            sbf.append("</cp_request>");
            requestStr = sbf.toString();
            if (_log.isDebugEnabled())
                _log.debug("generateGetAccountInfoRequest", "Got the XML String as " + requestStr);
            return requestStr;
        } catch (Exception e) {
            _log.error("generateGetAccountInfoRequest", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateGetAccountInfoRequest", "Exiting requestStr: " + requestStr);
        }
    }

    /**
     * This Method parse GetAccount Info Response
     * 
     * @param responseStr
     *            String
     * @throws Exception
     * @return map
     */
    public HashMap parseGetAccountInfoResponse(String responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseGetAccountInfoResponse", "Entered responseStr: " + responseStr);
        HashMap map = null;
        try {
            map = new HashMap();
            String _str = "0";
            int index = responseStr.indexOf("<result>");
            String result = responseStr.substring(index + "<result>".length(), responseStr.indexOf("</result>", index));
            map.put("result", result);
            if (_str.equals(result)) {
                index = responseStr.indexOf("<cp_id>");
                String cp_id = responseStr.substring(index + "<cp_id>".length(), responseStr.indexOf("</cp_id>", index));
                map.put("cp_id", cp_id);
                index = responseStr.indexOf("<cp_transaction_id>");
                String cp_transaction_id = responseStr.substring(index + "<cp_transaction_id>".length(), responseStr.indexOf("</cp_transaction_id>", index));
                map.put("cp_transaction_id", cp_transaction_id);
                index = responseStr.indexOf("<op_transaction_id>");
                String op_transaction_id = responseStr.substring(index + "<op_transaction_id>".length(), responseStr.indexOf("</op_transaction_id>", index));
                map.put("op_transaction_id", op_transaction_id);
                index = responseStr.indexOf("<credit_balance>");
                String credit_balance = responseStr.substring(index + "<credit_balance>".length(), responseStr.indexOf("</credit_balance>", index));
                map.put("credit_balance", credit_balance);
                index = responseStr.indexOf("<end_val_date>");
                String end_val_date = responseStr.substring(index + "<end_val_date>".length(), responseStr.indexOf("</end_val_date>", index));
                map.put("end_val_date", end_val_date);
                index = responseStr.indexOf("<end_inact_date>");
                String end_inact_date = responseStr.substring(index + "<end_inact_date>".length(), responseStr.indexOf("</end_inact_date>", index));
                map.put("end_inact_date", end_inact_date);
                index = responseStr.indexOf("<profile>");
                String profile = responseStr.substring(index + "<profile>".length(), responseStr.indexOf("</profile>", index));
                map.put("profile", profile);
                index = responseStr.indexOf("<prof_lang>");
                String prof_lang = responseStr.substring(index + "<prof_lang>".length(), responseStr.indexOf("</prof_lang>", index));
                map.put("prof_lang", prof_lang);
                index = responseStr.indexOf("<acc_status>");
                String acc_status = responseStr.substring(index + "<acc_status>".length(), responseStr.indexOf("</acc_status>", index));
                map.put("acc_status", acc_status);
                index = responseStr.indexOf("<lock_information>");
                String lock_information = responseStr.substring(index + "<lock_information>".length(), responseStr.indexOf("</lock_information>", index));
                map.put("lock_information", lock_information);
                map.put("LOCK_FLAG", lock_information);
                index = responseStr.indexOf("<service_type>");
                String service_type = responseStr.substring(index + "<service_type>".length(), responseStr.indexOf("</service_type>", index));
                map.put("service_type", service_type);
            } else {
                /**
                 * Note: when we get the xml error code 3 or 9 in that case we
                 * will not receive cp_id and transaction id only else we will
                 * receive both
                 */
                if (!(AlcatelI.RESULT_ERROR_MALFORMED_REQUEST.equals(result) || AlcatelI.RESULT_ERROR_XML_PARSE.equals(result))) {
                    index = responseStr.indexOf("<cp_id>");
                    String cp_id = responseStr.substring(index + "<cp_id>".length(), responseStr.indexOf("</cp_id>", index));
                    map.put("cp_id", cp_id);
                    index = responseStr.indexOf("<cp_transaction_id>");
                    String cp_transaction_id = responseStr.substring(index + "<cp_transaction_id>".length(), responseStr.indexOf("</cp_transaction_id>", index));
                    map.put("cp_transaction_id", cp_transaction_id);
                } else {
                    map.put("cp_id", null);
                    map.put("cp_transaction_id", null);
                }
                map.put("result", result);
            }
            return map;
        } catch (Exception e) {
            _log.error("parseGetAccountInfoResponse", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseGetAccountInfoResponse", "Exiting map: " + map);
        }
    }

    /**
     * @param map
     * @return requestStr java.lang.String
     * @throws Exception
     */
    private String generateRechargeCreditRequest(HashMap map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateRechargeCreditRequest", "Entered MSISDN=" + map.get("MSISDN") + "transactionId: " + map.get("IN_TXN_ID") + " Content Provider ID:" + map.get("cp_id") + " application: " + map.get("application") + " action: " + map.get("action") + " transaction price: " + map.get("INTERFACE_AMOUNT") + "user validity:" + map.get("VALIDITY_DAYS") + "user grace:" + map.get("GRACE_DAYS"));
        String requestStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?>");
            sbf.append("<!DOCTYPE cp_request SYSTEM \"cp_req_websvr.dtd\">");
            sbf.append("<cp_request>");
            sbf.append("<cp_id>" + map.get("cp_id") + "</cp_id>");
            sbf.append("<cp_transaction_id>" + getINReconTxnID(map) + "</cp_transaction_id>");
            sbf.append("<op_transaction_id></op_transaction_id>");
            sbf.append("<application>" + map.get("application") + "</application>");
            sbf.append("<action>" + map.get("action") + "</action>");
            sbf.append("<user_id type=\"MSISDN\">" + InterfaceUtil.getFilterMSISDN((String) map.get("INTERFACE_ID"), (String) map.get("MSISDN")) + "</user_id>");
            sbf.append("<transaction_price>" + map.get("INTERFACE_AMOUNT") + "</transaction_price>");
            sbf.append("<transaction_currency>" + map.get("transaction_currency") + "</transaction_currency>");
            String adjust = BTSLUtil.NullToString((String) map.get("ADJUST"));
            if (adjust.equals("Y")) {
                sbf.append("<user_validity>0</user_validity>");
                sbf.append("<user_grace>0</user_grace>");
            } else {
                String validity = (String) map.get("VALIDITY_DAYS");
                String grace = (String) map.get("GRACE_DAYS");
                if (InterfaceUtil.isNullString(validity))
                    validity = "0";
                if (InterfaceUtil.isNullString(grace))
                    grace = "0";
                sbf.append("<user_validity>" + validity + "</user_validity>");
                sbf.append("<user_grace>" + grace + "</user_grace>");
            }
            sbf.append("</cp_request>");
            requestStr = sbf.toString();
            if (_log.isDebugEnabled())
                _log.debug("generateRechargeCreditRequest", "Got the XML String as " + requestStr);
            return requestStr;
        } catch (Exception e) {
            _log.error("generateRechargeCreditRequest", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateRechargeCreditRequest", "Exited requestStr: " + requestStr);
        }
    }

    /**
     * This Method parse recharge credit response
     * 
     * @param responseStr
     *            String
     * @throws Exception
     * @return map
     */
    private HashMap parseRechargeCreditResponse(String responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseRechargeCreditResponse", "Entered responseStr: " + responseStr);
        HashMap map = null;
        try {
            map = new HashMap();
            String _str = "0";
            int index = responseStr.indexOf("<result>");
            String result = responseStr.substring(index + "<result>".length(), responseStr.indexOf("</result>", index));
            map.put("result", result);
            if (_str.equals((result))) {
                index = responseStr.indexOf("<cp_id>");
                String cp_id = responseStr.substring(index + "<cp_id>".length(), responseStr.indexOf("</cp_id>", index));
                map.put("cp_id", cp_id);
                index = responseStr.indexOf("<cp_transaction_id>");
                String cp_transaction_id = responseStr.substring(index + "<cp_transaction_id>".length(), responseStr.indexOf("</cp_transaction_id>", index));
                map.put("cp_transaction_id", cp_transaction_id);
                index = responseStr.indexOf("<op_transaction_id>");
                String op_transaction_id = responseStr.substring(index + "<op_transaction_id>".length(), responseStr.indexOf("</op_transaction_id>", index));
                map.put("op_transaction_id", op_transaction_id);
                index = responseStr.indexOf("<credit_balance>");
                String credit_balance = responseStr.substring(index + "<credit_balance>".length(), responseStr.indexOf("</credit_balance>", index));
                map.put("credit_balance", credit_balance);
                index = responseStr.indexOf("<end_val_date>");
                String end_val_date = responseStr.substring(index + "<end_val_date>".length(), responseStr.indexOf("</end_val_date>", index));
                map.put("end_val_date", end_val_date);
                index = responseStr.indexOf("<end_inact_date>");
                String end_inact_date = responseStr.substring(index + "<end_inact_date>".length(), responseStr.indexOf("</end_inact_date>", index));
                map.put("end_inact_date", end_inact_date);
                index = responseStr.indexOf("<profile>");
                String profile = responseStr.substring(index + "<profile>".length(), responseStr.indexOf("</profile>", index));
                map.put("profile", profile);
                index = responseStr.indexOf("<prof_lang>");
                String prof_lang = responseStr.substring(index + "<prof_lang>".length(), responseStr.indexOf("</prof_lang>", index));
                map.put("prof_lang", prof_lang);
                index = responseStr.indexOf("<acc_status>");
                String acc_status = responseStr.substring(index + "<acc_status>".length(), responseStr.indexOf("</acc_status>", index));
                map.put("acc_status", acc_status);
                index = responseStr.indexOf("<service_type>");
                String service_type = responseStr.substring(index + "<service_type>".length(), responseStr.indexOf("</service_type>", index));
                map.put("service_type", service_type);
            } else {
                /**
                 * Note: when we get the xml error code 3 or 9 in that case we
                 * will not receive cp_id and transaction id only else we will
                 * receive both
                 */
                if (!(AlcatelI.RESULT_ERROR_MALFORMED_REQUEST.equals(result) || AlcatelI.RESULT_ERROR_XML_PARSE.equals(result))) {
                    index = responseStr.indexOf("<cp_id>");
                    String cp_id = responseStr.substring(index + "<cp_id>".length(), responseStr.indexOf("</cp_id>", index));
                    map.put("cp_id", cp_id);
                    index = responseStr.indexOf("<cp_transaction_id>");
                    String cp_transaction_id = responseStr.substring(index + "<cp_transaction_id>".length(), responseStr.indexOf("</cp_transaction_id>", index));
                    map.put("cp_transaction_id", cp_transaction_id);
                } else {
                    map.put("cp_id", null);
                    map.put("cp_transaction_id", null);
                }
                map.put("result", result);
            }
            return map;
        } catch (Exception e) {
            _log.error("parseRechargeCreditResponse", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseRechargeCreditResponse", "Exiting map: " + map);
        }
    }

    /**
     * This Method Generate immediate Debit Request
     * 
     * @param map
     *            HashMap
     * @throws Exception
     * @return requestStr
     */
    private String generateImmediateDebitRequest(HashMap map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateImmediateDebitRequest", "Entered map=" + map);
        String requestStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?>");
            sbf.append("<!DOCTYPE cp_request SYSTEM \"cp_req_websvr.dtd\">");
            sbf.append("<cp_request>");
            sbf.append("<cp_id>" + map.get("cp_id") + "</cp_id>");
            sbf.append("<cp_transaction_id>" + getINReconTxnID(map) + "</cp_transaction_id>");
            sbf.append("<op_transaction_id></op_transaction_id>");
            sbf.append("<application>" + map.get("application") + "</application>");
            sbf.append("<action>" + map.get("action") + "</action>");
            sbf.append("<transaction_price>" + map.get("INTERFACE_AMOUNT") + "</transaction_price>");
            sbf.append("<transaction_currency>" + map.get("transaction_currency") + "</transaction_currency>");
            sbf.append("<user_id>" + InterfaceUtil.getFilterMSISDN((String) map.get("INTERFACE_ID"), (String) map.get("MSISDN")) + "</user_id>");
            sbf.append("</cp_request>");
            requestStr = sbf.toString();
            if (_log.isDebugEnabled())
                _log.debug("generateImmediateDebitRequest", "Got the XML String as " + requestStr);
            return requestStr;
        } catch (Exception e) {
            _log.error("generateImmediateDebitRequest", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateImmediateDebitRequest", "Exiting requestStr: " + requestStr);
        }
    }

    /**
     * This Method parse Immediate Debit Response
     * 
     * @param responseStr
     *            String
     * @throws Exception
     * @return map
     */
    public HashMap parseImmediateDebitResponse(String responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseImmediateDebitResponse", "Entered responseStr: " + responseStr);
        HashMap map = new HashMap();
        try {
            String _str = "0";
            int index = responseStr.indexOf("<result>");
            String result = responseStr.substring(index + "<result>".length(), responseStr.indexOf("</result>", index));
            map.put("result", result);
            if (_str.equals(result)) {
                index = responseStr.indexOf("<cp_id>");
                String cp_id = responseStr.substring(index + "<cp_id>".length(), responseStr.indexOf("</cp_id>", index));
                map.put("cp_id", cp_id);
                index = responseStr.indexOf("<cp_transaction_id>");
                String cp_transaction_id = responseStr.substring(index + "<cp_transaction_id>".length(), responseStr.indexOf("</cp_transaction_id>", index));
                map.put("cp_transaction_id", cp_transaction_id);
                index = responseStr.indexOf("<service_type>");
                String service_type = responseStr.substring(index + "<service_type>".length(), responseStr.indexOf("</service_type>", index));
                map.put("service_type", service_type);
            } else {
                /*
                 *  * Note: when we get the xml error code 3 or 9 in that case we
                 * will not receive cp_id and transaction id only else we will
                 * receive both
                 */
                if (!(AlcatelI.RESULT_ERROR_MALFORMED_REQUEST.equals(result) || AlcatelI.RESULT_ERROR_XML_PARSE.equals(result))) {
                    index = responseStr.indexOf("<cp_id>");
                    String cp_id = responseStr.substring(index + "<cp_id>".length(), responseStr.indexOf("</cp_id>", index));
                    map.put("cp_id", cp_id);
                    index = responseStr.indexOf("<cp_transaction_id>");
                    String cp_transaction_id = responseStr.substring(index + "<cp_transaction_id>".length(), responseStr.indexOf("</cp_transaction_id>", index));
                    map.put("cp_transaction_id", cp_transaction_id);
                } else {
                    map.put("cp_id", null);
                    map.put("cp_transaction_id", null);
                }
                map.put("result", result);
            }
            return map;
        } catch (Exception e) {
            _log.error("parseImmediateDebitResponse", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseImmediateDebitResponse", "Exiting map: " + map);
        }
    }

    /**
     * This method will return request message for Cancel action.
     * 
     * @param HashMap
     *            p_map
     * @return String
     * @throws Exception
     */
    private String generateCanceTxnRequest(HashMap map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateCanceTxnRequest", "Entered MSISDN=" + map.get("MSISDN") + "transactionId: " + map.get("IN_TXN_ID") + " Content Provider ID:" + map.get("cp_id") + " application: " + map.get("application") + " action: " + map.get("action") + " transaction price: " + map.get("INTERFACE_AMOUNT") + "user validity:" + map.get("VALIDITY_DAYS") + "user grace:" + map.get("GRACE_DAYS"));
        String requestStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?>");
            sbf.append("<!DOCTYPE cp_request SYSTEM \"cp_req_websvr.dtd\">");
            sbf.append("<cp_request>");
            sbf.append("<cp_id>" + map.get("cp_id") + "</cp_id>");
            sbf.append("<cp_transaction_id>" + getINReconTxnID(map) + "</cp_transaction_id>");
            sbf.append("<op_transaction_id></op_transaction_id>");
            sbf.append("<application>" + map.get("application") + "</application>");
            sbf.append("<action>" + map.get("action") + "</action>");
            sbf.append("<user_id type=\"MSISDN\">" + InterfaceUtil.getFilterMSISDN((String) map.get("INTERFACE_ID"), (String) map.get("MSISDN")) + "</user_id>");
            sbf.append("<transaction_price>" + map.get("INTERFACE_AMOUNT") + "</transaction_price>");
            sbf.append("<transaction_currency>" + map.get("transaction_currency") + "</transaction_currency>");
            String adjust = BTSLUtil.NullToString((String) map.get("ADJUST"));
            if (adjust.equals("Y")) {
                sbf.append("<user_validity>0</user_validity>");
                sbf.append("<user_grace>0</user_grace>");
            } else {
                String validity = (String) map.get("VALIDITY_DAYS");
                String grace = (String) map.get("GRACE_DAYS");
                if (InterfaceUtil.isNullString(validity))
                    validity = "0";
                if (InterfaceUtil.isNullString(grace))
                    grace = "0";
                sbf.append("<user_validity>" + validity + "</user_validity>");
                sbf.append("<user_grace>" + grace + "</user_grace>");
            }
            sbf.append("</cp_request>");
            requestStr = sbf.toString();
            if (_log.isDebugEnabled())
                _log.debug("generateCanceTxnRequest", "Got the XML String as " + requestStr);
            return requestStr;
        } catch (Exception e) {
            _log.error("generateCanceTxnRequest", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateCanceTxnRequest", "Exited requestStr: " + requestStr);
        }
    }

    /**
     * This method will return hashMap(containing cancel response details) after
     * parsing response string.
     * 
     * @param HashMap
     *            p_responseStr
     * @return String
     * @throws Exception
     */

    private HashMap parseCancelTxnResponse(String responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseCancelTxnResponse", "Entered responseStr: " + responseStr);
        HashMap map = null;
        try {
            map = new HashMap();
            String _str = "0";
            int index = responseStr.indexOf("<result>");
            String result = responseStr.substring(index + "<result>".length(), responseStr.indexOf("</result>", index));
            map.put("result", result);
            if (_str.equals((result))) {
                index = responseStr.indexOf("<cp_id>");
                String cp_id = responseStr.substring(index + "<cp_id>".length(), responseStr.indexOf("</cp_id>", index));
                map.put("cp_id", cp_id);
                index = responseStr.indexOf("<cp_transaction_id>");
                String cp_transaction_id = responseStr.substring(index + "<cp_transaction_id>".length(), responseStr.indexOf("</cp_transaction_id>", index));
                map.put("cp_transaction_id", cp_transaction_id);
                index = responseStr.indexOf("<op_transaction_id>");
                /*
                 * String op_transaction_id=responseStr.substring(index+
                 * "<op_transaction_id>"
                 * .length(),responseStr.indexOf("</op_transaction_id>",index));
                 * map.put("op_transaction_id",op_transaction_id);
                 * index=responseStr.indexOf("<credit_balance>");
                 * String
                 * credit_balance=responseStr.substring(index+"<credit_balance>"
                 * .length(),responseStr.indexOf("</credit_balance>",index));
                 * map.put("credit_balance",credit_balance);
                 * index=responseStr.indexOf("<end_val_date>");
                 * String
                 * end_val_date=responseStr.substring(index+"<end_val_date>"
                 * .length(),responseStr.indexOf("</end_val_date>",index));
                 * map.put("end_val_date",end_val_date);
                 * index=responseStr.indexOf("<end_inact_date>");
                 * String
                 * end_inact_date=responseStr.substring(index+"<end_inact_date>"
                 * .length(),responseStr.indexOf("</end_inact_date>",index));
                 * map.put("end_inact_date",end_inact_date);
                 * index=responseStr.indexOf("<profile>");
                 * String
                 * profile=responseStr.substring(index+"<profile>".length(
                 * ),responseStr.indexOf("</profile>",index));
                 * map.put("profile",profile);
                 * index=responseStr.indexOf("<prof_lang>");
                 * String
                 * prof_lang=responseStr.substring(index+"<prof_lang>".length
                 * (),responseStr.indexOf("</prof_lang>",index));
                 * map.put("prof_lang",prof_lang);
                 * index=responseStr.indexOf("<acc_status>");
                 * String
                 * acc_status=responseStr.substring(index+"<acc_status>".length
                 * (),responseStr.indexOf("</acc_status>",index));
                 * map.put("acc_status",acc_status);
                 * index=responseStr.indexOf("<service_type>");
                 * String
                 * service_type=responseStr.substring(index+"<service_type>"
                 * .length(),responseStr.indexOf("</service_type>",index));
                 * map.put("service_type",service_type);
                 */
            } else {
                /**
                 * Note: when we get the xml error code 3 or 9 in that case we
                 * will not receive cp_id and transaction id only else we will
                 * receive both
                 */
                if (!(AlcatelI.RESULT_ERROR_MALFORMED_REQUEST.equals(result) || AlcatelI.RESULT_ERROR_XML_PARSE.equals(result))) {
                    index = responseStr.indexOf("<cp_id>");
                    String cp_id = responseStr.substring(index + "<cp_id>".length(), responseStr.indexOf("</cp_id>", index));
                    map.put("cp_id", cp_id);
                    index = responseStr.indexOf("<cp_transaction_id>");
                    String cp_transaction_id = responseStr.substring(index + "<cp_transaction_id>".length(), responseStr.indexOf("</cp_transaction_id>", index));
                    map.put("cp_transaction_id", cp_transaction_id);
                } else {
                    map.put("cp_id", null);
                    map.put("cp_transaction_id", null);
                }
                map.put("result", result);
            }
            return map;
        } catch (Exception e) {
            _log.error("parseCancelTxnResponse", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseCancelTxnResponse", "Exiting map: " + map);
        }
    }

}
