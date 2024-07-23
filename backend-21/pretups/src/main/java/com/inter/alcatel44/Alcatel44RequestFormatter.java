package com.inter.alcatel44;

/**
 * @(#)Alcatel44RequestFormatter
 *                               Copyright(c) 2006, Bharti Telesoft Int. Public
 *                               Ltd.
 *                               All Rights Reserved
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Author Date History
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Pankaj K Namdev Dec 05,2006 Initial Creation
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               --
 *                               This class is used to format the request and
 *                               response based on the action.
 *                               REQUEST: XML request is generated from the hash
 *                               map based on key value pairs.
 *                               RESPONSE: From XML response elements values are
 *                               stored in HashMap.
 */

import java.util.HashMap;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.module.InterfaceUtil;

public class Alcatel44RequestFormatter {
    // Get the logger object, which is used to write different types of logs.
    public static Log _log = LogFactory.getLog("Alcatel44RequestFormatter".getClass().getName());

    /**
     * Get IN Reconciliation Txn ID and append the user type and store the it
     * into the request map under key IN_RECON_ID.
     * 
     * @param HashMap
     *            p_requestMap
     * @return String
     */
    private String getINReconTxnID(HashMap p_requestMap) {
        if (_log.isDebugEnabled())
            _log.debug("getINReconTxnID", "Entered p_requestMap:" + p_requestMap);
        // return ((String)p_requestMap.get("IN_TXN_ID"));
        String inReconID = null;
        try {
            // Get the user type from the request map using the key USER_TYPE.
            String userType = (String) p_requestMap.get("USER_TYPE");
            if (_log.isDebugEnabled())
                _log.debug("getINReconTxnID", "userType :" + userType);
            if (userType != null)
                inReconID = ((String) p_requestMap.get("TRANSACTION_ID") + "." + userType.trim());
            else
                inReconID = ((String) p_requestMap.get("TRANSACTION_ID"));
            p_requestMap.put("IN_RECON_ID", inReconID);
        } catch (Exception e) {
            _log.error("getINReconTxnID", "Exception e:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getINReconTxnID", "Exited inReconID:" + inReconID);
        }

        return inReconID;
    }

    /**
     * Based on the action value, a method is referenced to generate the xml
     * string for corresponding request
     * 
     * @param int p_action
     * @param HashMap
     *            p_map
     * @return String str
     */
    protected String generateRequest(int p_action, HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateRequest", "Entered p_action:" + p_action + " p_map: " + p_map);
        String str = null;
        try {
            p_map.put("action", String.valueOf(p_action));
            switch (p_action) {
            case Alcatel44I.ACTION_ACCOUNT_INFO: {
                str = generateGetAccountInfoRequest(p_map);
                break;
            }
            case Alcatel44I.ACTION_RECHARGE_CREDIT: {
                str = generateRechargeCreditRequest(p_map);
                break;
            }
            case Alcatel44I.ACTION_IMMEDIATE_DEBIT: {
                str = generateImmediateDebitRequest(p_map);
                break;
            }
            }
        } catch (Exception e) {
            _log.error("generateRequest", "Exception e::" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateRequest", "Exited str:" + str);
        }
        return str;
    }

    /**
     * Based on the action value, a method is referenced to parse the xml string
     * for corresponding response.
     * 
     * @param int p_action
     * @param String
     *            p_responseStr
     * @return HashMap
     */

    protected HashMap parseResponse(int p_action, String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseResponse", "Entered p_action:" + p_action + " responseStr:  " + p_responseStr);
        HashMap map = null;
        try {
            switch (p_action) {
            case Alcatel44I.ACTION_ACCOUNT_INFO: {
                map = parseGetAccountInfoResponse(p_responseStr);
                break;
            }
            case Alcatel44I.ACTION_RECHARGE_CREDIT: {
                map = parseRechargeCreditResponse(p_responseStr);
                break;
            }
            case Alcatel44I.ACTION_IMMEDIATE_DEBIT: {
                map = parseImmediateDebitResponse(p_responseStr);
                break;
            }
            }
        } catch (Exception e) {
            _log.error("parseResponse", "Exception e:" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseResponse", "Exiting map: " + map);
        }
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

            // cp_id will be present in request map under the key cp_id
            sbf.append("<cp_id>");
            sbf.append(map.get("cp_id"));
            sbf.append("</cp_id>");

            // Calling the getINReconTxnID method generates cp_transaction_id.
            sbf.append("<cp_transaction_id>");
            sbf.append(getINReconTxnID(map));
            sbf.append("</cp_transaction_id>");

            // op_transaction_id would be blank
            sbf.append("<op_transaction_id></op_transaction_id>");

            // Value of application would be present in requestMap under key as
            // application
            sbf.append("<application>");
            sbf.append(map.get("application"));
            sbf.append("</application>");

            // Value of action would be present in requestMap under key as
            // action
            sbf.append("<action>");
            sbf.append(map.get("action"));
            sbf.append("</action>");

            // user-id will be present in request map with key MSISDN, it would
            // be filtered before setting in the request
            sbf.append("<user_id type=\"MSISDN\">");
            sbf.append(InterfaceUtil.getFilterMSISDN((String) map.get("INTERFACE_ID"), (String) map.get("MSISDN")));
            sbf.append("</user_id>");

            sbf.append("</cp_request>");
            requestStr = sbf.toString();
            return requestStr;
        } catch (Exception e) {
            _log.error("generateGetAccountInfoRequest", "Exception e: " + e);
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateGetAccountInfoRequest", "Exiting requestStr: " + requestStr);
        }
    }

    /**
     * This Method parse GetAccount Info Response
     * 
     * @param String
     *            p_responseStr
     * @throws Exception
     * @return HashMap
     */
    public HashMap parseGetAccountInfoResponse(String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseGetAccountInfoResponse", "Entered p_responseStr: " + p_responseStr);
        HashMap map = null;
        try {
            map = new HashMap();
            int index = p_responseStr.indexOf("<result>");
            String result = p_responseStr.substring(index + "<result>".length(), p_responseStr.indexOf("</result>", index));
            map.put("result", result);
            if (Alcatel44I.RESULT_OK.equals(result)) {
                index = p_responseStr.indexOf("<cp_id>");
                String cp_id = p_responseStr.substring(index + "<cp_id>".length(), p_responseStr.indexOf("</cp_id>", index));
                map.put("cp_id", cp_id);
                index = p_responseStr.indexOf("<cp_transaction_id>");
                String cp_transaction_id = p_responseStr.substring(index + "<cp_transaction_id>".length(), p_responseStr.indexOf("</cp_transaction_id>", index));
                map.put("cp_transaction_id", cp_transaction_id);
                // index=p_responseStr.indexOf("<op_transaction_id>");
                // String
                // op_transaction_id=p_responseStr.substring(index+"<op_transaction_id>".length(),p_responseStr.indexOf("</op_transaction_id>",index));
                // map.put("op_transaction_id",op_transaction_id);
                index = p_responseStr.indexOf("<credit_balance>");
                String credit_balance = p_responseStr.substring(index + "<credit_balance>".length(), p_responseStr.indexOf("</credit_balance>", index));
                map.put("credit_balance", credit_balance);
                index = p_responseStr.indexOf("<end_val_date>");
                String end_val_date = p_responseStr.substring(index + "<end_val_date>".length(), p_responseStr.indexOf("</end_val_date>", index));
                map.put("end_val_date", end_val_date);
                index = p_responseStr.indexOf("<end_inact_date>");
                String end_inact_date = p_responseStr.substring(index + "<end_inact_date>".length(), p_responseStr.indexOf("</end_inact_date>", index));
                map.put("end_inact_date", end_inact_date);
                // profile (This is an optional value, confirm whether the Tag
                // will be present or not,in the case when profile has no
                // value?).
                index = p_responseStr.indexOf("<profile>");
                if (index >= 0) {
                    String profile = p_responseStr.substring(index + "<profile>".length(), p_responseStr.indexOf("</profile>", index));
                    map.put("profile", profile);
                }
                index = p_responseStr.indexOf("<prof_lang>");
                String prof_lang = p_responseStr.substring(index + "<prof_lang>".length(), p_responseStr.indexOf("</prof_lang>", index));
                map.put("prof_lang", prof_lang);
                index = p_responseStr.indexOf("<acc_status>");
                String acc_status = p_responseStr.substring(index + "<acc_status>".length(), p_responseStr.indexOf("</acc_status>", index));
                map.put("acc_status", acc_status);
                // At the time of preintegration testing lock_information is
                // made as the mandatory tag
                index = p_responseStr.indexOf("<lock_information>");
                String lock_info = p_responseStr.substring(index + "<lock_information>".length(), p_responseStr.indexOf("</lock_information>", index));
                map.put("lock_info", lock_info);
                // service_type: This is an optional value, first check for its
                // existence of TAG in the
                // response, if present then parse and get the value, else don’t
                // set it to responseMap.
                index = p_responseStr.indexOf("<service_type>");
                if (index >= 0) {
                    String service_type = p_responseStr.substring(index + "<service_type>".length(), p_responseStr.indexOf("</service_type>", index));
                    map.put("service_type", service_type);
                }
            }// end of if- result=OK
            else {
                /**
                 * Note: when we get the xml error code 3 or 9 in that case we
                 * will not receive cp_id and transaction id only else we will
                 * receive both
                 */
                if (!(Alcatel44I.RESULT_ERROR_MALFORMED_REQUEST.equals(result) || Alcatel44I.RESULT_ERROR_XML_PARSE.equals(result))) {
                    index = p_responseStr.indexOf("<cp_id>");
                    String cp_id = p_responseStr.substring(index + "<cp_id>".length(), p_responseStr.indexOf("</cp_id>", index));
                    map.put("cp_id", cp_id);
                    index = p_responseStr.indexOf("<cp_transaction_id>");
                    String cp_transaction_id = p_responseStr.substring(index + "<cp_transaction_id>".length(), p_responseStr.indexOf("</cp_transaction_id>", index));
                    map.put("cp_transaction_id", cp_transaction_id);
                }
                // Else set cp_id and cp_transaction_id equal to NULL into
                // responseMap
                else {
                    map.put("cp_id", null);
                    map.put("cp_transaction_id", null);
                }
                map.put("result", result);
            }// end of else- result=NOK
            return map;
        }// end of try-block
        catch (Exception e) {
            _log.error("parseGetAccountInfoResponse", "Exception e: " + e);
            throw e;
        }// end of catch -Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseGetAccountInfoResponse", "Exiting map: " + map);
        }// end of finally
    }

    /**
     * This method is responsible to generate the xml string for the credit
     * request.
     * 
     * @param HashMap
     *            map
     * @return String requestStr
     * @throws Exception
     */
    private String generateRechargeCreditRequest(HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateRechargeCreditRequest", "Entered p_map:" + p_map);
        String requestStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?>");

            sbf.append("<!DOCTYPE cp_request SYSTEM \"cp_req_websvr.dtd\">");

            sbf.append("<cp_request>");

            // cp_id will be present in request map under the key cp_id
            sbf.append("<cp_id>");
            sbf.append(p_map.get("cp_id"));
            sbf.append("</cp_id>");

            // Calling the getINReconTxnID method generates cp_transaction_id.
            sbf.append("<cp_transaction_id>");
            sbf.append(getINReconTxnID(p_map));
            sbf.append("</cp_transaction_id>");

            // op_transaction_id would be blank
            sbf.append("<op_transaction_id></op_transaction_id>");

            // Value of application would be present in requestMap under key as
            // application
            sbf.append("<application>");
            sbf.append(p_map.get("application"));
            sbf.append("</application>");

            // Value of action would be present in requestMap under key as
            // action
            sbf.append("<action>");
            sbf.append(p_map.get("action"));
            sbf.append("</action>");

            // user-id will be present in request map with key MSISDN, it would
            // be filtered before setting in the request
            sbf.append("<user_id type=\"MSISDN\">");
            sbf.append(InterfaceUtil.getFilterMSISDN((String) p_map.get("INTERFACE_ID"), (String) p_map.get("MSISDN")));
            sbf.append("</user_id>");

            // Value of transaction_price will be get from requestMap using key
            // as transfer_amount.
            sbf.append("<transaction_price>");
            sbf.append(p_map.get("transfer_amount"));
            sbf.append("</transaction_price>");

            // Value of transaction_currency will be getting from requestMap
            // using key as transaction_currency.
            sbf.append("<transaction_currency>");
            sbf.append(p_map.get("transaction_currency"));
            sbf.append("</transaction_currency>");

            // User_validity and user_grace would be set based on value of
            // ADJUST present in the requestMap.
            // If value of ADJUST is Y, set the validity and grace equal to ZERO
            // (0)·

            // <credit_params main_activity="1"
            // main_inactivity="1"></credit_params>

            //
            if ("Y".equals((String) p_map.get("ADJUST"))) {
                // sbf.append("<user_validity>0</user_validity>");
                //
                // sbf.append("<user_grace>0</user_grace>");
                //
                sbf.append("<credit_params main_activity='0' main_inactivity='0'></credit_params>");
            } else {
                // Get the value of VALIDITY_DAYS from requestMap
                String validity = (String) p_map.get("VALIDITY_DAYS");
                // Get the value of GRACE_DAYS from requestMap
                String grace = (String) p_map.get("GRACE_DAYS");
                // If VALIDITY_DAYS is NULL set user_validity equal to 0, else
                // set same user_validity.
                if (InterfaceUtil.isNullString(validity))
                    validity = "0";
                // If GRACE_DAYS is NULL set user_grace equal to 0, else set
                // same as grace_days.
                if (InterfaceUtil.isNullString(grace))
                    grace = "0";

                /*
                 * sbf.append("<user_validity>");
                 * sbf.append(validity);
                 * sbf.append("</user_validity>");//Confirm whether the IN takes
                 * a ZERO (0)value for any element-Ashish
                 * 
                 * sbf.append("<user_grace>");
                 * sbf.append(grace);
                 * sbf.append("</user_grace>");//Confirm whether the IN takes a
                 * ZERO (0)value for any element-Ashish
                 */
                sbf.append("<credit_params main_activity='" + validity + "'");
                sbf.append(" main_inactivity='" + grace + "'>");
                sbf.append("</credit_params>");
            }
            sbf.append("</cp_request>");
            requestStr = sbf.toString();
        } catch (Exception e) {
            _log.error("generateRechargeCreditRequest", "Exception e: " + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateRechargeCreditRequest", "Exited requestStr: " + requestStr);
        }
        return requestStr;
    }

    /**
     * This method is responsible to parse the xml string for the credit
     * response.
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
            int index = responseStr.indexOf("<result>");
            String result = responseStr.substring(index + "<result>".length(), responseStr.indexOf("</result>", index));
            map.put("result", result);
            // Get the value of result and compare this against the success code
            // (0: means success).
            // If the result is equal to 0, parse the xml string and get the
            // value of following elements.
            if (Alcatel44I.RESULT_OK.equals((result))) {
                // cp_id:
                index = responseStr.indexOf("<cp_id>");
                String cp_id = responseStr.substring(index + "<cp_id>".length(), responseStr.indexOf("</cp_id>", index));
                map.put("cp_id", cp_id);
                // cp_transaction_id:
                index = responseStr.indexOf("<cp_transaction_id>");
                String cp_transaction_id = responseStr.substring(index + "<cp_transaction_id>".length(), responseStr.indexOf("</cp_transaction_id>", index));
                map.put("cp_transaction_id", cp_transaction_id);
                // op_transaction_id
                // index=responseStr.indexOf("<op_transaction_id>");
                // String
                // op_transaction_id=responseStr.substring(index+"<op_transaction_id>".length(),responseStr.indexOf("</op_transaction_id>",index));
                // map.put("op_transaction_id",op_transaction_id);
                // credit_balance
                index = responseStr.indexOf("<credit_balance>");
                String credit_balance = responseStr.substring(index + "<credit_balance>".length(), responseStr.indexOf("</credit_balance>", index));
                map.put("credit_balance", credit_balance);
                // end_val_date
                index = responseStr.indexOf("<end_val_date>");
                String end_val_date = responseStr.substring(index + "<end_val_date>".length(), responseStr.indexOf("</end_val_date>", index));
                map.put("end_val_date", end_val_date);
                // end_inact_date
                index = responseStr.indexOf("<end_inact_date>");
                String end_inact_date = responseStr.substring(index + "<end_inact_date>".length(), responseStr.indexOf("</end_inact_date>", index));
                map.put("end_inact_date", end_inact_date);

                // · profile (This is an optional value, confirm whether the Tag
                // will be present or not, in the case when profile has no
                // value?).
                index = responseStr.indexOf("<profile>");
                if (index >= 0) {
                    String profile = responseStr.substring(index + "<profile>".length(), responseStr.indexOf("</profile>", index));
                    map.put("profile", profile);
                }
                // prof_lang
                index = responseStr.indexOf("<prof_lang>");
                String prof_lang = responseStr.substring(index + "<prof_lang>".length(), responseStr.indexOf("</prof_lang>", index));
                map.put("prof_lang", prof_lang);
                // acc_status
                index = responseStr.indexOf("<acc_status>");
                String acc_status = responseStr.substring(index + "<acc_status>".length(), responseStr.indexOf("</acc_status>", index));
                map.put("acc_status", acc_status);
                index = responseStr.indexOf("<service_type>");
                if (index >= 0) {
                    String service_type = responseStr.substring(index + "<service_type>".length(), responseStr.indexOf("</service_type>", index));
                    map.put("service_type", service_type);
                }
            }// end of if- result=OK
            else {
                // when we get the xml error code 3 or 9 in that case we will
                // not receive cp_id and transaction id only else we will
                // receive both
                if (!(Alcatel44I.RESULT_ERROR_MALFORMED_REQUEST.equals(result) || Alcatel44I.RESULT_ERROR_XML_PARSE.equals(result))) {
                    index = responseStr.indexOf("<cp_id>");
                    String cp_id = responseStr.substring(index + "<cp_id>".length(), responseStr.indexOf("</cp_id>", index));
                    map.put("cp_id", cp_id);
                    index = responseStr.indexOf("<cp_transaction_id>");
                    String cp_transaction_id = responseStr.substring(index + "<cp_transaction_id>".length(), responseStr.indexOf("</cp_transaction_id>", index));
                    map.put("cp_transaction_id", cp_transaction_id);
                }
                // Else set cp_id and cp_transaction_id equal to NULL into
                // responseMap
                else {
                    map.put("cp_id", null);
                    map.put("cp_transaction_id", null);
                }
                map.put("result", result);
            }// end of else- result=NOK
            return map;
        } catch (Exception e) {
            _log.error("parseRechargeCreditResponse", "Exception e: " + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseRechargeCreditResponse", "Exiting map: " + map);
        }
    }

    /**
     * This method is responsible to generate the xml string for the debit
     * request (Only amount is supported).
     * 
     * @param HashMap
     *            p_map
     * @throws Exception
     * @return String
     */
    private String generateImmediateDebitRequest(HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateImmediateDebitRequest", "Entered p_map=" + p_map);
        String requestStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?>");

            sbf.append("<!DOCTYPE cp_request SYSTEM \"cp_req_websvr.dtd\">");

            sbf.append("<cp_request>");
            // cp_id will be present in request map under the key cp_id.
            sbf.append("<cp_id>");
            sbf.append(p_map.get("cp_id"));
            sbf.append("</cp_id>");

            // Calling the getINReconTxnID method generates cp_transaction_id
            sbf.append("<cp_transaction_id>");
            sbf.append(getINReconTxnID(p_map));
            sbf.append("</cp_transaction_id>");

            // op_transaction_id would be blank.
            sbf.append("<op_transaction_id></op_transaction_id>");

            // Value of application would be present in requestMap under key as
            // application.
            sbf.append("<application>");
            sbf.append(p_map.get("application"));
            sbf.append("</application>");

            // Value of action would be present in requestMap under key as
            // action.
            sbf.append("<action>");
            sbf.append(p_map.get("action"));
            sbf.append("</action>");

            // user-id will be present in request map with key MSISDN, it would
            // be filtered before setting in the request.
            sbf.append("<user_id>");
            sbf.append(InterfaceUtil.getFilterMSISDN((String) p_map.get("INTERFACE_ID"), (String) p_map.get("MSISDN")));
            sbf.append("</user_id>");

            // Value of transaction_price will be get from requestMap using key
            // as transfer_amount.
            sbf.append("<transaction_price>");
            sbf.append(p_map.get("transfer_amount"));
            sbf.append("</transaction_price>");

            // Value of transaction_currency will be getting from requestMap
            // using key as transaction_currency.
            sbf.append("<transaction_currency>");
            sbf.append(p_map.get("transaction_currency"));
            sbf.append("</transaction_currency>");

            sbf.append("</cp_request>");
            requestStr = sbf.toString();
        } catch (Exception e) {
            _log.error("generateImmediateDebitRequest", "Exception e: " + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateImmediateDebitRequest", "Exiting requestStr: " + requestStr);
        }
        return requestStr;
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
            int index = responseStr.indexOf("<result>");
            String result = responseStr.substring(index + "<result>".length(), responseStr.indexOf("</result>", index));
            map.put("result", result);
            // Get the value of result and compare this against the success code
            // (0: means success).
            // If the result is equal to 0, parse the xml string and get the
            // value of following elements.
            if (Alcatel44I.RESULT_OK.equals(result)) {
                // cp_id:
                index = responseStr.indexOf("<cp_id>");
                String cp_id = responseStr.substring(index + "<cp_id>".length(), responseStr.indexOf("</cp_id>", index));
                map.put("cp_id", cp_id);
                // cp_transaction_id:
                index = responseStr.indexOf("<cp_transaction_id>");
                String cp_transaction_id = responseStr.substring(index + "<cp_transaction_id>".length(), responseStr.indexOf("</cp_transaction_id>", index));
                map.put("cp_transaction_id", cp_transaction_id);
                // acc_status
                // index=responseStr.indexOf("<acc_status>");
                // String
                // acc_status=responseStr.substring(index+"<acc_status>".length(),responseStr.indexOf("</acc_status>",index));
                // map.put("acc_status",acc_status);
            }// end of if- result=OK
            else {
                // when we get the xml error code 3 or 9 in that case we will
                // not receive cp_id and transaction id only else we will
                // receive both
                if (!(Alcatel44I.RESULT_ERROR_MALFORMED_REQUEST.equals(result) || Alcatel44I.RESULT_ERROR_XML_PARSE.equals(result))) {
                    index = responseStr.indexOf("<cp_id>");
                    String cp_id = responseStr.substring(index + "<cp_id>".length(), responseStr.indexOf("</cp_id>", index));
                    map.put("cp_id", cp_id);
                    index = responseStr.indexOf("<cp_transaction_id>");
                    String cp_transaction_id = responseStr.substring(index + "<cp_transaction_id>".length(), responseStr.indexOf("</cp_transaction_id>", index));
                    map.put("cp_transaction_id", cp_transaction_id);
                }
                // Else set cp_id and cp_transaction_id equal to NULL into
                // responseMap.
                else {
                    map.put("cp_id", null);
                    map.put("cp_transaction_id", null);
                }
                map.put("result", result);
            }// end of else case -result=NOK
            return map;
        }// end of try block.
        catch (Exception e) {
            _log.error("parseImmediateDebitResponse", "Exception e: " + e.getMessage());
            throw e;
        }// end of Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseImmediateDebitResponse", "Exiting map: " + map);
        }
    }// end of parseImmediateDebitResponse
}
