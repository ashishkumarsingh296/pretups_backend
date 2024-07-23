package com.inter.alcatel432;

/**
 * @(#)Alcatel432RequestFormatter
 *                                Copyright(c) 2006, Bharti Telesoft Int. Public
 *                                Ltd.
 *                                All Rights Reserved
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Author Date History
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Ashish Kumar May 03,2006 Initial Creation
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                ----
 *                                This class is used to format the request and
 *                                response based on the action.
 *                                REQUEST: XML request is generated from the
 *                                hash map based on key value pairs.
 *                                RESPONSE: From XML response elements values
 *                                are stored in HashMap.
 */

import java.util.HashMap;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.util.BTSLUtil;

public class Alcatel432RequestFormatter {

    // Defines the type of account to be credited
    private String _mainAccount = "1"; // Represent the MAIN account to be
                                       // credited.
    private String _contentCredit = "5";// Represent the CONTENT account to be
                                        // credited.
    private String _dataCredit = "6"; // Represent the DATA account to be
                                      // credited.
    private String _smsCredit = "8";// Represent the SMS acount to be credited.
    private String _mmsCredit = "7";// Represent the MMS acount to be credited
    public Log _log = LogFactory.getLog(this.getClass().getName());

    public Alcatel432RequestFormatter() {

    }

    /**
     * Get IN Reconciliation Txn ID
     * 
     * @param HashMap
     *            p_requestMap
     * @return String
     * @throws Exception
     */
    private String getINReconTxnID(HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getINReconTxnID", "Enetered p_requestMap =" + p_requestMap);
        String inReconID = null;
        try {
            String userType = (String) p_requestMap.get("USER_TYPE");
            if (userType != null)
                inReconID = ((String) p_requestMap.get("TRANSACTION_ID") + "." + userType);
            else
                inReconID = ((String) p_requestMap.get("TRANSACTION_ID"));
            p_requestMap.put("IN_RECON_ID", inReconID);
        }// end of try block
        catch (Exception e) {
            _log.error("getINReconTxnID", "Exception e=" + e);
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("getINReconTxnID", "Exited inReconID =" + inReconID);
        }// end of finally
        return inReconID;
    }// end of getINReconTxnID

    /**
     * This method is used to decide the request formation for type of request
     * based on the action value.
     * Convert key and value of HashMap into XML String.
     * 
     * @param int action
     * @param HashMap
     *            p_map
     * @return String
     * @throws Exception
     */
    protected String generateRequest(int p_action, HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateRequest", "Entered p_action = " + p_action + " map: " + p_map);
        String str = null;
        p_map.put("action", String.valueOf(p_action));
        try {
            switch (p_action) {
            case Alcatel432I.ACTION_ACCOUNT_INFO: {
                str = generateGetAccountInfoRequest(p_map);
                break;
            }
            case Alcatel432I.ACTION_RECHARGE_CREDIT: {
                str = generateRechargeCreditRequest(p_map);
                break;
            }
            case Alcatel432I.ACTION_IMMEDIATE_DEBIT: {
                str = generateImmediateDebitRequest(p_map);
                break;
            }
            }// end of switch block
        }// end of try block
        catch (Exception e) {
            _log.error("generateRequest", "Exception e = " + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("generateRequest", "Exited Request String: str = " + str);
        }// end of finally
        return str;
    }// end of generateRequest

    /**
     * This method parse the response from XML String into HashMap
     * 
     * @param int action
     * @param String
     *            responseStr
     * @return HashMap map
     * @throws Exception
     */
    protected HashMap parseResponse(int p_action, String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseResponse", "Entered p_action = " + p_action + " p_responseStr = " + p_responseStr);
        HashMap map = null;
        try {
            switch (p_action) {
            case Alcatel432I.ACTION_ACCOUNT_INFO: {
                map = parseGetAccountInfoResponse(p_responseStr);
                break;
            }
            case Alcatel432I.ACTION_RECHARGE_CREDIT: {
                map = parseRechargeCreditResponse(p_responseStr);
                break;
            }
            case Alcatel432I.ACTION_IMMEDIATE_DEBIT: {
                map = parseImmediateDebitResponse(p_responseStr);
                break;
            }
            }// end of switch block
        }// end of try block
        catch (Exception e) {
            _log.error("parseResponse", "Exception e = " + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseResponse", "Exiting map: " + map);
        }// end of finally
        return map;
    }// end of parseResponse

    /**
     * This method is used to generate the request for getting account
     * information.
     * 
     * @param HashMap
     *            p_map
     * @return String
     * @throws Exception
     */
    private String generateGetAccountInfoRequest(HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateGetAccountInfoRequest", "Entered p_map =" + p_map);
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1028);
            sbf.append("<?xml version=\"1.0\"?>");
            sbf.append("<!DOCTYPE cp_request SYSTEM \"cp_req_websvr.dtd\">");
            sbf.append("<cp_request>");
            sbf.append("<cp_id>" + p_map.get("cp_id") + "</cp_id>");
            sbf.append("<cp_transaction_id>" + getINReconTxnID(p_map) + "</cp_transaction_id>");
            sbf.append("<op_transaction_id></op_transaction_id>");
            sbf.append("<application>" + p_map.get("application") + "</application>");
            sbf.append("<action>" + p_map.get("action") + "</action>");
            sbf.append("<user_id type=\"MSISDN\">" + InterfaceUtil.getFilterMSISDN((String) p_map.get("INTERFACE_ID"), (String) p_map.get("MSISDN")) + "</user_id>");
            sbf.append("</cp_request>");
        }// end of try-block
        catch (Exception e) {
            _log.error("generateGetAccountInfoRequest", "Exception e: " + e);
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("generateGetAccountInfoRequest", "Exiting Request String:sbf.toString()=" + sbf.toString());
        }// end of finally
        return sbf.toString();
    }// end of generateGetAccountInfoRequest

    /**
     * This method used to parse the xml string and its element and values are
     * stored as key value pair in HashMap
     * 
     * @param String
     *            responseStr
     * @return HashMap
     * @throws Exception
     */
    public HashMap parseGetAccountInfoResponse(String responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseGetAccountInfoResponse", "Entered responseStr: " + responseStr);
        HashMap map = null;
        try {
            map = new HashMap();
            String str = "0";
            int index = responseStr.indexOf("<result>");
            String result = responseStr.substring(index + "<result>".length(), responseStr.indexOf("</result>", index));
            map.put("result", result);
            if (str.equals(result)) {
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
                // Checks whether the respones contains the Lock information and
                // service type.If yes then parse these.
                if (responseStr.indexOf("<lock_information>") > 0) {
                    index = responseStr.indexOf("<lock_information>");
                    String lock_information = responseStr.substring(index + "<lock_information>".length(), responseStr.indexOf("</lock_information>", index));
                    map.put("lock_information", lock_information);
                    map.put("LOCK_FLAG", lock_information);
                    index = responseStr.indexOf("<service_type>");
                    String service_type = responseStr.substring(index + "<service_type>".length(), responseStr.indexOf("</service_type>", index));
                    map.put("service_type", service_type);
                }
            }// end of if case when the result is OK(its value 0)
            else {
                if (!(Alcatel432I.RESULT_ERROR_MALFORMED_REQUEST.equals(result) || Alcatel432I.RESULT_ERROR_XML_PARSE.equals(result))) {
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
            }// end of else case when the result is not 0
            return map;
        }// end of try block
        catch (Exception e) {
            _log.error("parseGetAccountInfoResponse", "Exception e: " + e);
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseGetAccountInfoResponse", "Exiting map: " + map);
        }// end of finally
    }// end of parseGetAccountInfoResponse

    /**
     * This method is used to generate a predefined XML string from the key
     * value pair of HashMap.
     * Based on the flag SEPERATE_SUB_ACCOUNT it is decided that whether request
     * is generated for combined (main+bonus)
     * or separate account.
     * 
     * @param HashMap
     *            map
     * @return String
     * @throws Exception
     */
    private String generateRechargeCreditRequest(HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateRechargeCreditRequest", "Entered p_map= " + p_map);
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?>");
            sbf.append("<!DOCTYPE cp_request SYSTEM \"cp_req_websvr.dtd\">");
            sbf.append("<cp_request>");
            sbf.append("<cp_id>" + p_map.get("cp_id") + "</cp_id>");
            sbf.append("<cp_transaction_id>" + getINReconTxnID(p_map) + "</cp_transaction_id>");
            sbf.append("<op_transaction_id></op_transaction_id>");
            sbf.append("<application>" + p_map.get("application") + "</application>");
            sbf.append("<action>" + p_map.get("action") + "</action>");
            sbf.append("<user_id type=\"MSISDN\">" + InterfaceUtil.getFilterMSISDN((String) p_map.get("INTERFACE_ID"), (String) p_map.get("MSISDN")) + "</user_id>");
            sbf.append("<transaction_currency>" + p_map.get("transaction_currency") + "</transaction_currency>");
            String adjust = BTSLUtil.NullToString((String) p_map.get("ADJUST"));

            // Check for the creditAdjustment request in this case xml request
            // does not contain the credit params.
            if ("Y".equals(adjust))
                sbf.append("<transaction_price>" + p_map.get("transfer_amount") + "</transaction_price>");
            else {

                // Get the value of validity,grace,bonus amount and bonus
                // validity and checks for null,if it is null then assign 0.
                String validity = (String) p_map.get("VALIDITY_DAYS");
                String grace = (String) p_map.get("GRACE_DAYS");

                // Implement the logic based on card_group_selector
                // From request map we will get the card_group_selector
                // value of card group selector defines the type of account to
                // be credited.(It defines 5 type account)
                // 1.Main account,Contains the transaction price and
                // credit_params contains- Subscriber type(crd_ref0),Main
                // activity(crd_ref1),Main Inactivity(crd_ref2).
                // 2.Content credit,It will not include the transaction price
                // TAG and credit_params contains crd_ref0 and crd_ref5 tag.
                // 3.Data credit,It will not include the transaction price TAG
                // and credit_params contains crd_ref0 and crd_ref6.
                // 4.SMS credit,It will not include the transaction price TAG
                // and credit_params contains crd_ref0, crd_ref8 and crd_ref9.
                // 5.MMS credit,It will not include the transaction price TAG
                // and credit_params contains crd_ref0 and crd_ref7.
                // TRANSACTION PRICE IS ASSOCIATED ONLY WITH THE MAIN ACCOUNT
                // Also consider the case of SEPARATE ACCOUNT or COMBINED

                if (_mainAccount.equals((String) p_map.get("CARD_GROUP_SELECTOR")))
                    sbf.append("<transaction_price>" + p_map.get("transfer_amount") + "</transaction_price>");
                sbf.append("<credit_params>");
                sbf.append("<crd_param crd_ref=\"0\">1</crd_param>");
                // Case when request defines the card_group_selector as Main
                // account.
                if (_mainAccount.equals((String) p_map.get("CARD_GROUP_SELECTOR"))) {
                    // Here we should check the value of SEPERATE_SUB_ACCOUNT.
                    // If its value is Y then Main and Bonus amount will set
                    // separatly.else combined value of validity and grace will
                    // be set.
                    if (!InterfaceUtil.isNullString(validity)) {
                        long val = Long.parseLong(validity);
                        if (val > 0)
                            sbf.append("<crd_param crd_ref=\"1\">" + validity + "</crd_param>");
                    }
                    if (!InterfaceUtil.isNullString(grace)) {
                        long val = Long.parseLong(grace);
                        if (val > 0)
                            sbf.append("<crd_param crd_ref=\"2\">" + grace + "</crd_param>");
                    }
                }
                // Case when request defines the card_group_selector as content
                // credit and its value will be as the transaction price.
                if (_contentCredit.equals((String) p_map.get("CARD_GROUP_SELECTOR"))) {
                    // Content Credit
                    sbf.append("<crd_param crd_ref=\"5\">" + (String) p_map.get("transfer_amount") + "</crd_param>");
                }
                // Case when request defines the card_group_selector as data
                // credit.
                if (_dataCredit.equals((String) p_map.get("CARD_GROUP_SELECTOR"))) {
                    // Data Credit.
                    sbf.append("<crd_param crd_ref=\"6\">" + (String) p_map.get("transfer_amount") + "</crd_param>");
                }
                // Case when request defines the card_group_selector as sms
                // credit.
                // It includes the credit and validity. Consider the case of
                // SEPERATE_SUB_ACCOUNT
                if (_smsCredit.equals((String) p_map.get("CARD_GROUP_SELECTOR"))) {
                    // SMS CREDIT
                    sbf.append("<crd_param crd_ref=\"8\">" + (String) p_map.get("transfer_amount") + "</crd_param>");
                    // SMS Validity

                    if (!InterfaceUtil.isNullString(validity)) {
                        long val = Long.parseLong(validity);
                        if (val > 0)
                            sbf.append("<crd_param crd_ref=\"9\">" + validity + "</crd_param>");
                    }
                }
                // Case when request defines the card_group-selector as MMS
                // credit.
                if (_mmsCredit.equals((String) p_map.get("CARD_GROUP_SELECTOR"))) {
                    // MMS CREDIT
                    sbf.append("<crd_param crd_ref=\"7\">" + (String) p_map.get("transfer_amount") + "</crd_param>");
                }
                // This is the case when Separate Sub Account flag is set to
                // true.
                // In this case the prom_activity and prom_credit will be send.
                if ("Y".equals((String) p_map.get("SEPERATE_SUB_ACCOUNT"))) {
                    String bonusAmount = (String) p_map.get("transfer_bonus_amount");
                    String bonusValidity = (String) p_map.get("BONUS_VALIDITY_DAYS");
                    if (InterfaceUtil.isNullString(bonusAmount))
                        bonusAmount = "0";
                    if (InterfaceUtil.isNullString(bonusValidity))
                        bonusValidity = "0";
                    long bonusAmt = Long.parseLong(bonusAmount);
                    long validityBonus = Long.parseLong(bonusValidity);
                    if (bonusAmt > 0)
                        sbf.append("<crd_param crd_ref=\"3\">" + String.valueOf(bonusAmt) + "</crd_param>");
                    if (validityBonus > 0)
                        sbf.append("<crd_param crd_ref=\"4\">" + String.valueOf(validityBonus) + "</crd_param>");
                }
                sbf.append("</credit_params>");
            }// end of else case of Adjustment request
            sbf.append("</cp_request>");
            return sbf.toString();
        }// end of try-block
        catch (Exception e) {
            _log.error("generateRechargeCreditRequest", "Exception e: " + e);
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("generateRechargeCreditRequest", "Exited Request String:sbf.toString()= " + sbf.toString());
        }// end of finally
    }// end of generateRechargeCreditRequest

    /**
     * This method is used to retrieve the element and value of input xml string
     * into key value pair of hashMap
     * 
     * @param String
     *            responseStr
     * @return HashMap
     * @throws Exception
     */

    private HashMap parseRechargeCreditResponse(String responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseRechargeCreditResponse", "Entered responseStr = " + responseStr);
        HashMap map = null;
        try {
            map = new HashMap();
            String str = "0";
            int index = responseStr.indexOf("<result>");
            String result = responseStr.substring(index + "<result>".length(), responseStr.indexOf("</result>", index));
            map.put("result", result);
            if (str.equals((result))) {
                index = responseStr.indexOf("<cp_id>");
                String cp_id = responseStr.substring(index + "<cp_id>".length(), responseStr.indexOf("</cp_id>", index));
                map.put("cp_id", cp_id);
                index = responseStr.indexOf("<cp_transaction_id>");
                String cp_transaction_id = responseStr.substring(index + "<cp_transaction_id>".length(), responseStr.indexOf("</cp_transaction_id>", index));
                map.put("cp_transaction_id", cp_transaction_id);
            }// end of if case that checks the result of the response.
            else {

                // Note: when we get the xml error code 3 or 9 in that case we
                // will not receive cp_id and transaction id only else we will
                // receive both
                if (!(Alcatel432I.RESULT_ERROR_MALFORMED_REQUEST.equals(result) || Alcatel432I.RESULT_ERROR_XML_PARSE.equals(result))) {
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
        }// end of try block
        catch (Exception e) {
            _log.error("parseRechargeCreditResponse", "Exception e: " + e);
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseRechargeCreditResponse", "Exiting map: " + map);
        }// end of finally
    }// end of parseRechargeCreditResponse

    /**
     * This method is used to generate predefined XML string based on the key
     * value pair of the HashMap
     * 
     * @param HashMap
     *            map
     * @return String
     * @throws Exception
     */
    private String generateImmediateDebitRequest(HashMap map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateImmediateDebitRequest", "Entered map=" + map);
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
            sbf.append("<transaction_price>" + map.get("transfer_amount") + "</transaction_price>");
            sbf.append("<transaction_currency>" + map.get("transaction_currency") + "</transaction_currency>");
            sbf.append("<user_id>" + InterfaceUtil.getFilterMSISDN((String) map.get("INTERFACE_ID"), (String) map.get("MSISDN")) + "</user_id>");
            sbf.append("</cp_request>");
            return sbf.toString();
        }// end of try-Block
        catch (Exception e) {
            _log.error("generateImmediateDebitRequest", "Exception e: " + e);
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("generateImmediateDebitRequest", "Exiting Request String:sbf.toString()= " + sbf.toString());
        }// end of finally
    } // end of generateImmediateDebitRequest

    /**
     * This method is used to retrieve the element and value of input xml string
     * into key value pair of hashMap.
     * 
     * @param String
     *            p_responseStr
     * @throws Exception
     * @return HashMap
     */
    public HashMap parseImmediateDebitResponse(String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseImmediateDebitResponse", "Entered responseStr: " + p_responseStr);
        HashMap map = new HashMap();
        try {
            String str = "0";
            int index = p_responseStr.indexOf("<result>");
            String result = p_responseStr.substring(index + "<result>".length(), p_responseStr.indexOf("</result>", index));
            map.put("result", result);
            if (str.equals(result)) {
                index = p_responseStr.indexOf("<cp_id>");
                String cp_id = p_responseStr.substring(index + "<cp_id>".length(), p_responseStr.indexOf("</cp_id>", index));
                map.put("cp_id", cp_id);
                index = p_responseStr.indexOf("<cp_transaction_id>");
                String cp_transaction_id = p_responseStr.substring(index + "<cp_transaction_id>".length(), p_responseStr.indexOf("</cp_transaction_id>", index));
                map.put("cp_transaction_id", cp_transaction_id);
                // Check whether the service type is present in the response or
                // not.
                if (p_responseStr.indexOf("<service_type>") > 0) {
                    index = p_responseStr.indexOf("<service_type>");
                    String service_type = p_responseStr.substring(index + "<service_type>".length(), p_responseStr.indexOf("</service_type>", index));
                    map.put("service_type", service_type);
                }
            }// end of if that checks the result of response.
            else {
                // Note: when we get the xml error code 3 or 9 in that case we
                // will not receive cp_id and transaction id only else we will
                // receive both
                if (!(Alcatel432I.RESULT_ERROR_MALFORMED_REQUEST.equals(result) || Alcatel432I.RESULT_ERROR_XML_PARSE.equals(result))) {
                    index = p_responseStr.indexOf("<cp_id>");
                    String cp_id = p_responseStr.substring(index + "<cp_id>".length(), p_responseStr.indexOf("</cp_id>", index));
                    map.put("cp_id", cp_id);
                    index = p_responseStr.indexOf("<cp_transaction_id>");
                    String cp_transaction_id = p_responseStr.substring(index + "<cp_transaction_id>".length(), p_responseStr.indexOf("</cp_transaction_id>", index));
                    map.put("cp_transaction_id", cp_transaction_id);
                } else {
                    map.put("cp_id", null);
                    map.put("cp_transaction_id", null);
                }
                map.put("result", result);
            }// end of else case, in which error is processed if any.
            return map;
        }// end of try-block
        catch (Exception e) {
            _log.error("parseImmediateDebitResponse", "Exception e: " + e);
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseImmediateDebitResponse", "Exiting map: " + map);
        }// end of finally
    }// end of parseImmediateDebitResponse
}
