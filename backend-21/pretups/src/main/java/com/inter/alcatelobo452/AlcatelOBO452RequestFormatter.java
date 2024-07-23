package com.inter.alcatelobo452;

/**
 * @(#)AlcatelOBO452RequestFormatter
 *                                   Copyright(c) 2011, Comviva Technologies
 *                                   Ltd.
 *                                   All Rights Reserved
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   -----------
 *                                   Author Date History
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   -----------
 *                                   Vinay Kumar Singh Jan 19, 2011 Initial
 *                                   Creation
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   ----------
 *                                   This class is used to format the request
 *                                   and response based on the action.
 *                                   REQUEST: XML request is generated from the
 *                                   hash map based on key value pairs.
 *                                   RESPONSE: From XML response elements values
 *                                   are stored in HashMap.
 */

import java.util.Arrays;
import java.util.HashMap;
import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.util.BTSLUtil;

public class AlcatelOBO452RequestFormatter {
    // Get the logger object, which is used to write different types of logs.
    public static Log _log = LogFactory.getLog("AlcatelOBO452RequestFormatter".getClass().getName());
    private String _cardGrp = null;
    private boolean _explicitRecharge = false;
    private boolean _combinedRecharge = false;
    private boolean _implicitRecharge = false;
    private String _selectorBundleId = null;
    private String _interfaceId = null;
    private String _serviceType = null;

    private String getINReconTxnID(HashMap<String, String> p_requestMap) {
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
     * Based on the action value, a method is referenced to generate the XML
     * string for corresponding request
     * 
     * @param int p_action
     * @param HashMap
     *            p_map
     * @return String str
     */
    protected String generateRequest(int p_action, HashMap<String, String> p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateRequest: ", "Entered p_action:" + p_action + ", p_map: " + p_map);
        String str = null;
        try {
            p_map.put("action", String.valueOf(p_action));
            _serviceType = (String) p_map.get("REQ_SERVICE");
            _selectorBundleId = FileCache.getValue((String) p_map.get("INTERFACE_ID"), "SELECTOR_" + p_map.get("SELECTOR_BUNDLE_ID"));
            if (!InterfaceUtil.isNullString(_selectorBundleId))
                _selectorBundleId = _selectorBundleId.trim();
            _interfaceId = ((String) p_map.get("INTERFACE_ID")).trim();
            switch (p_action) {
            case AlcatelOBO452I.ACTION_GET_ACCOUNT_INFO: {
                str = generateGetAccountInfoRequest(p_map);
                break;
            }
            case AlcatelOBO452I.ACTION_IMMEDIATE_CREDIT: {
                if ("PRC".equals(_serviceType))
                    str = generateCreditAdjustRequest(p_map);
                else
                    str = generateImmediateCreditRequest(p_map);
                break;
            }
            case AlcatelOBO452I.ACTION_IMMEDIATE_DEBIT: {
                str = generateImmediateDebitRequest(p_map);
                break;
            }
            }
        } catch (Exception e) {
            _log.error("generateRequest: ", "Exception e: " + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateRequest: ", "Exited Str: " + str);
        }
        return str;
    }// end of generateRequest

    /**
     * Based on the action value, a method is referenced to parse the XML string
     * for corresponding response.
     * 
     * @param int p_action
     * @param String
     *            p_responseStr
     * @return HashMap
     */
    protected HashMap<String, String> parseResponse(int p_action, String p_responseStr, String p_interfaceID) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseResponse: ", "Entered p_action: " + p_action + " responseStr: " + p_responseStr);
        HashMap<String, String> map = null;
        try {
            switch (p_action) {
            case AlcatelOBO452I.ACTION_GET_ACCOUNT_INFO: {
                map = parseGetAccountInfoResponse(p_responseStr, p_interfaceID);
                break;
            }
            case AlcatelOBO452I.ACTION_IMMEDIATE_CREDIT: {
                map = parseImmediateCreditResponse(p_responseStr, p_interfaceID);
                break;
            }
            case AlcatelOBO452I.ACTION_IMMEDIATE_DEBIT: {
                map = parseImmediateDebitResponse(p_responseStr, p_interfaceID);
                break;
            }
            }
        } catch (Exception e) {
            _log.error("parseResponse", "Exception e: " + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseResponse", " Exiting map: " + map);
        }
        return map;
    }// end of parseResponse

    /**
     * This Method generate account information request
     * 
     * @param map
     *            HashMap
     * @throws Exception
     * @return requestStr
     */
    private String generateGetAccountInfoRequest(HashMap<String, String> map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateGetAccountInfoRequest", " Entered map=" + map);
        String requestStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1028);

            sbf.append("<?xml version=\"1.0\"?>");
            sbf.append("<!DOCTYPE cp_request SYSTEM \"cp_req_websvr.dtd\">");
            sbf.append("<cp_request>");

            // cp_id: This id identifies the External Entity that interacts with
            // the ICC, and is present in request map under the key cp_id.
            sbf.append("<cp_id>");
            sbf.append(map.get("cp_id"));
            sbf.append("</cp_id>");

            // cp_transaction_id: It is the identifier of the transaction
            // running between the External Entity and the ICC.
            // The getINReconTxnID method generates cp_transaction_id.
            sbf.append("<cp_transaction_id>");
            sbf.append(getINReconTxnID(map));
            sbf.append("</cp_transaction_id>");

            // op_transaction_id: It conveys information related to the session
            // parameters kept at the DDCF
            // application side in case of two-step Debit transactions.
            // op_transaction_id would be blank
            sbf.append("<op_transaction_id></op_transaction_id>");

            // application: It specifies which application is to be triggered on
            // the ICC product platform. This field must be set to ‘1’,
            // that represents the Direct Debit Credit Function (DDCF)
            // application. This parameter MUST be present in all requests sent
            // to ICC.
            // Value of application would be present in requestMap under key as
            // application.
            sbf.append("<application>");
            sbf.append(map.get("application"));
            sbf.append("</application>");

            // action: It characterizes the kind of the request and MUST be
            // present in all requests sent to DDCF
            // application. Value of action would be present in requestMap under
            // key as action
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
        } catch (Exception e) {
            _log.error("generateGetAccountInfoRequest: ", "Exception e: " + e);
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateGetAccountInfoRequest: ", "Exiting requestStr: " + requestStr);
        }
        return requestStr;
    }// end of generateGetAccountInfoRequest

    /**
     * This Method parse GetAccount Info Response
     * 
     * @param String
     *            p_responseStr
     * @throws Exception
     * @return HashMap
     */
    private HashMap<String, String> parseGetAccountInfoResponse(String p_responseStr, String p_interfaceID) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseGetAccountInfoResponse: ", "Entered p_responseStr: " + p_responseStr);
        HashMap<String, String> responseMap = null;
        try {
            responseMap = new HashMap<String, String>();
            int index = p_responseStr.indexOf("<result>");
            String result = p_responseStr.substring(index + "<result>".length(), p_responseStr.indexOf("</result>", index));
            responseMap.put("result", result);

            if (AlcatelOBO452I.RESULT_OK.equals(result)) {
                index = p_responseStr.indexOf("<cp_id>");
                if (index != -1) {
                    String cp_id = p_responseStr.substring(index + "<cp_id>".length(), p_responseStr.indexOf("</cp_id>", index));
                    responseMap.put("cp_id", cp_id);
                }
                // cp_transaction_id
                index = p_responseStr.indexOf("<cp_transaction_id>");
                if (index != -1) {
                    String cp_transaction_id = p_responseStr.substring(index + "<cp_transaction_id>".length(), p_responseStr.indexOf("</cp_transaction_id>", index));
                    responseMap.put("cp_transaction_id", cp_transaction_id);
                }
                // account_id: It identifies the specific account of the user
                // that is to be credited. The identifier can
                // only be of 'type': Login (Account Identifier (Login))
                index = p_responseStr.indexOf("<account_id>");
                if (index != -1) {
                    String account_id = p_responseStr.substring(index + "<account_id>".length(), p_responseStr.indexOf("</account_id>", index));
                    responseMap.put("account_id", account_id);
                }
                // credit_balance
                index = p_responseStr.indexOf("<credit_balance>");
                if (index != -1) {
                    String credit_balance = p_responseStr.substring(index + "<credit_balance>".length(), p_responseStr.indexOf("</credit_balance>", index));
                    responseMap.put("credit_balance", credit_balance);
                }
                // end_val_date
                index = p_responseStr.indexOf("<end_val_date>");
                String end_val_date = p_responseStr.substring(index + "<end_val_date>".length(), p_responseStr.indexOf("</end_val_date>", index));
                responseMap.put("end_val_date", end_val_date);

                // end_inact_date
                index = p_responseStr.indexOf("<end_inact_date>");
                if (index != -1) {
                    String end_inact_date = p_responseStr.substring(index + "<end_inact_date>".length(), p_responseStr.indexOf("</end_inact_date>", index));
                    responseMap.put("end_inact_date", end_inact_date);
                }
                // profile (This is an optional value, confirm whether the Tag
                // will be present or not,in the case when profile has no
                // value?).
                index = p_responseStr.indexOf("<profile>");
                if (index != -1) {
                    String profile = p_responseStr.substring(index + "<profile>".length(), p_responseStr.indexOf("</profile>", index));
                    responseMap.put("profile", profile);
                }
                // prof_lang
                index = p_responseStr.indexOf("<prof_lang>");
                if (index != -1) {
                    String prof_lang = p_responseStr.substring(index + "<prof_lang>".length(), p_responseStr.indexOf("</prof_lang>", index));
                    responseMap.put("prof_lang", prof_lang);
                }
                // acc_status
                index = p_responseStr.indexOf("<acc_status>");
                if (index != -1) {
                    String acc_status = p_responseStr.substring(index + "<acc_status>".length(), p_responseStr.indexOf("</acc_status>", index));
                    responseMap.put("acc_status", acc_status);
                }
                // At the time of pre-integration testing lock_information is
                // made as the mandatory tag
                index = p_responseStr.indexOf("<lock_information>");
                if (index != -1) {
                    String lock_info = p_responseStr.substring(index + "<lock_information>".length(), p_responseStr.indexOf("</lock_information>", index));
                    responseMap.put("lock_info", lock_info);
                }
                // Set the defined bundle names into the response map.
                setBundlesRcvdFromIN(p_responseStr, responseMap);

                // if selector value is not 1, set selector balance in the map
                // in credit_balance
                if (!"1".equals(_selectorBundleId)) {

                }
            }// end of if- result=OK
            else {
                /**
                 * Note: when we get the XML error code 3 or 9 in that case we
                 * do not receive cp_id and transaction id only else we will
                 */
                if (!(AlcatelOBO452I.RESULT_ERROR_MALFORMED_REQUEST.equals(result)) || !(AlcatelOBO452I.RESULT_ERROR_XML_PARSE.equals(result))) {
                    index = p_responseStr.indexOf("<cp_id>");
                    if (index != -1) {
                        String cp_id = p_responseStr.substring(index + "<cp_id>".length(), p_responseStr.indexOf("</cp_id>", index));
                        responseMap.put("cp_id", cp_id);
                    }
                    // cp_transaction_id
                    index = p_responseStr.indexOf("<cp_transaction_id>");
                    if (index != -1) {
                        String cp_transaction_id = p_responseStr.substring(index + "<cp_transaction_id>".length(), p_responseStr.indexOf("</cp_transaction_id>", index));
                        responseMap.put("cp_transaction_id", cp_transaction_id);
                    }
                }
                // Else set cp_id and cp_transaction_id equal to NULL into
                // responseMap
                else {
                    responseMap.put("cp_id", null);
                    responseMap.put("cp_transaction_id", null);
                }
                responseMap.put("result", result);
            }// end of else- result=NOK
            return responseMap;
        }// end of try-block
        catch (Exception e) {
            _log.error("parseGetAccountInfoResponse: ", "Exception e: " + e);
            throw e;
        }// end of catch -Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseGetAccountInfoResponse: ", "Exiting map: " + responseMap);
        }// end of finally
    }// end of parseGetAccountInfoResponse

    /**
     * This method is responsible to generate the XML string for the credit
     * adjust(CP2P) request.
     * 
     * @param HashMap
     *            map
     * @return String requestStr
     * @throws Exception
     */
    private String generateCreditAdjustRequest(HashMap<String, String> p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateCreditAdjustRequest: ", "Entered p_map:" + p_map);
        String requestStr = null;
        StringBuffer sbf = null;
        String interfaceID = null;
        String amtStr = null;
        String validity = null;
        //
        String grace = null;

        try {
            _cardGrp = (String) p_map.get("CARD_GROUP");
            String combined = (String) p_map.get("COMBINED_RECHARGE");
            String implicit = (String) p_map.get("IMPLICIT_RECHARGE");
            if ("N".equals(implicit) && "N".equals(combined))
                _explicitRecharge = true;
            else if ("Y".equals(implicit) && "Y".equals(combined))
                _combinedRecharge = true;
            else if ("Y".equals(implicit) && "N".equals(combined)) {
                _implicitRecharge = true;
                /*
                 * _log.error("generateImmediateCreditRequest: ",
                 * "Implicit Credit recharge method not allowed in the system. Please check card group code "
                 * +_cardGrp);
                 * EventHandler.handle(EventIDI.SYSTEM_ERROR,
                 * EventComponentI.INTERFACES, EventStatusI.RAISED,
                 * EventLevelI.FATAL,
                 * "AlcatelOCI452RequestFormatter[generateImmediateCreditRequest]"
                 * ,"","","",
                 * "Implicit Credit recharge method not allowed in the system. Please check card group code "
                 * +_cardGrp);
                 * throw new
                 * BTSLBaseException(InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                 */
            }

            System.out.println("_explicitRecharge: " + _explicitRecharge + ",_combinedRecharge: " + _combinedRecharge + ",_implicitRecharge : " + _implicitRecharge);
            sbf = new StringBuffer(1024);
            // get the interface id from the map.
            interfaceID = (String) p_map.get("INTERFACE_ID");

            sbf.append("<?xml version=\"1.0\"?>");
            sbf.append("<!DOCTYPE cp_request SYSTEM \"cp_req_websvr.dtd\">");
            sbf.append("<cp_request>");
            // cp_id: This id identifies the External Entity that interacts with
            // the ICC,
            // and is present in request map under the key cp_id.
            sbf.append("<cp_id>");
            sbf.append(p_map.get("cp_id"));
            sbf.append("</cp_id>");

            // cp_transaction_id: It is the identifier of the transaction
            // running between the External Entity and the ICC.
            // The getINReconTxnID method generates cp_transaction_id.
            sbf.append("<cp_transaction_id>");
            sbf.append(getINReconTxnID(p_map));
            sbf.append("</cp_transaction_id>");

            sbf.append("<op_transaction_id></op_transaction_id>");

            // application: It specifies which application is to be triggered on
            // the ICC product platform. This field must be set to ‘1’,
            // that represents the Direct Debit Credit Function (DDCF)
            // application. This parameter MUST be present in all requests sent
            // to ICC.
            // Value of application would be present in requestMap under key as
            // application.
            sbf.append("<application>");
            sbf.append(p_map.get("application"));
            sbf.append("</application>");

            // action: It characterizes the kind of the request and MUST be
            // present in all requests sent to DDCF
            // application. Value of action would be present in requestMap under
            // key as action
            sbf.append("<action>");
            sbf.append(p_map.get("action"));
            sbf.append("</action>");

            // user_id: : It identifies the user whom the direct debit or direct
            // credit operation is to be made for.
            // user-id will be present in request map with key MSISDN, it would
            // be filtered before setting in the request
            sbf.append("<user_id type=\"MSISDN\">");
            sbf.append(InterfaceUtil.getFilterMSISDN((String) p_map.get("INTERFACE_ID"), (String) p_map.get("MSISDN")));
            sbf.append("</user_id>");
            sbf.append("<transaction_currency>");
            sbf.append("1");
            sbf.append("</transaction_currency>");

            /*
             * if(_implicitRecharge)
             * {
             * sbf.append("<recharge_id>");
             * sbf.append(_cardGrp);
             * sbf.append("</recharge_id>");
             * }
             * else
             * {
             * if(!_selectorBundleId.equals("1"))
             * {
             */
            // Get the value of VALIDITY_DAYS from requestMap
            validity = (String) p_map.get("VALIDITY_DAYS");
            // Get the value of GRACE_DAYS from requestMap
            grace = (String) p_map.get("GRACE_DAYS");
            // If VALIDITY_DAYS is NULL set user_validity equal to 0, else set
            // same user_validity.
            if (InterfaceUtil.isNullString(validity))
                validity = "0";
            // If GRACE_DAYS is NULL set user_grace equal to 0, else set same as
            // grace_days.
            if (InterfaceUtil.isNullString(grace))
                grace = "0";

            // If bonus validity is seperate from the main validity, then
            // substract it from the main validity.
            String addMainBnsVal = (String) FileCache.getValue(interfaceID, "ADD_MAIN_AND_BUNUS_VALIDITY").trim();
            String bonusValidity = (String) p_map.get("BONUS_VALIDITY_DAYS");
            if ("N".equals(addMainBnsVal) && !BTSLUtil.isNullString(bonusValidity.trim())) {
                long mainVal = Long.parseLong(validity.trim());
                long bonusVal = Long.parseLong(bonusValidity);
                if (bonusVal > 0)
                    validity = String.valueOf(mainVal - bonusVal);
            }
            // }

            /*
             * if(_selectorBundleId.equals("1"))
             * {
             */
            sbf.append("<transaction_price>");
            sbf.append(p_map.get("transfer_amount"));
            sbf.append("</transaction_price>");

            /*
             * }
             * else
             * {
             * if(!"1".equals(_selectorBundleId)&& _explicitRecharge)
             * sbf.append("<credit_params>");
             * }
             * 
             * if(_combinedRecharge)
             * {
             * if("1".equals(_selectorBundleId))
             * sbf.append("</credit_params>");
             * sbf.append("<recharge_id>");
             * sbf.append(_cardGrp);
             * sbf.append("</recharge_id>");
             * }
             * else if(_explicitRecharge)
             * {
             * amtStr=(String)p_map.get("transfer_amount");
             * String
             * selectorBundlename=FileCache.getValue(interfaceID,_selectorBundleId
             * ).trim();
             * //If SMS bundle is defined at IN, then only set it into the
             * request string.
             * if( !"1".equals(_selectorBundleId))
             * {
             * if(Long.parseLong(validity)>0 && Long.parseLong(amtStr)>0)
             * {
             * sbf.append("<crd_param bdl_name="+ "\""+selectorBundlename+"\"");
             * sbf.append(" bdl_validity="+"\""+validity+"\""+">"+ amtStr
             * +"</crd_param>");
             * }
             * }
             * sbf.append("</credit_params>");
             * }
             * }
             */
            sbf.append("<credit_params main_activity='" + validity + "'");
            sbf.append(" main_inactivity='" + grace + "'>");
            sbf.append("</credit_params>");
            sbf.append("</cp_request>");
            requestStr = sbf.toString();
        } catch (Exception e) {
            _log.error("generateCreditAdjustRequest: ", "Exception e: " + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateCreditAdjustRequest: ", "Exited requestStr: " + requestStr);
        }
        return requestStr;
    }// end of generateCreditAdjustRequest

    /**
     * This method is responsible to generate the XML string for the credit
     * request.
     * 
     * @param HashMap
     *            map
     * @return String requestStr
     * @throws Exception
     */
    private String generateImmediateCreditRequest(HashMap<String, String> p_map) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateImmediateCreditRequest: ", "Entered p_map:" + p_map);
        String requestStr = null;
        StringBuffer sbf = null;
        String interfaceID = null;
        String amtStr = null;

        try {
            _cardGrp = (String) p_map.get("CARD_GROUP");
            String combined = (String) p_map.get("COMBINED_RECHARGE");
            String implicit = (String) p_map.get("IMPLICIT_RECHARGE");
            if ("N".equals(implicit) && "N".equals(combined))
                _explicitRecharge = true;
            else if ("Y".equals(implicit) && "Y".equals(combined))
                _combinedRecharge = true;
            else if ("Y".equals(implicit) && "N".equals(combined))
                _implicitRecharge = true;

            System.out.println("_explicitRecharge: " + _explicitRecharge + ",_combinedRecharge: " + _combinedRecharge + ",_implicitRecharge : " + _implicitRecharge);
            sbf = new StringBuffer(1024);
            // get the interface id from the map.
            interfaceID = (String) p_map.get("INTERFACE_ID");

            sbf.append("<?xml version=\"1.0\"?>");
            sbf.append("<!DOCTYPE cp_request SYSTEM \"cp_req_websvr.dtd\">");
            sbf.append("<cp_request>");
            // cp_id: This id identifies the External Entity that interacts with
            // the ICC,
            // and is present in request map under the key cp_id.
            sbf.append("<cp_id>");
            sbf.append(p_map.get("cp_id"));
            sbf.append("</cp_id>");

            // cp_transaction_id: It is the identifier of the transaction
            // running between the External Entity and the ICC.
            // The getINReconTxnID method generates cp_transaction_id.
            sbf.append("<cp_transaction_id>");
            sbf.append(getINReconTxnID(p_map));
            sbf.append("</cp_transaction_id>");

            // op_transaction_id would be blank
            sbf.append("<op_transaction_id></op_transaction_id>");

            // application: It specifies which application is to be triggered on
            // the ICC product platform. This field must be set to ‘1’,
            // that represents the Direct Debit Credit Function (DDCF)
            // application. This parameter MUST be present in all requests sent
            // to ICC.
            // Value of application would be present in requestMap under key as
            // application.
            sbf.append("<application>");
            sbf.append(p_map.get("application"));
            sbf.append("</application>");

            // action: It characterizes the kind of the request and MUST be
            // present in all requests sent to DDCF
            // application. Value of action would be present in requestMap under
            // key as action
            sbf.append("<action>");
            sbf.append(p_map.get("action"));
            sbf.append("</action>");

            // user_id: : It identifies the user whom the direct debit or direct
            // credit operation is to be made for.
            // user-id will be present in request map with key MSISDN, it would
            // be filtered before setting in the request
            sbf.append("<user_id type=\"MSISDN\">");
            sbf.append(InterfaceUtil.getFilterMSISDN((String) p_map.get("INTERFACE_ID"), (String) p_map.get("MSISDN")));
            sbf.append("</user_id>");

            sbf.append("<transaction_currency>");
            sbf.append("1");
            sbf.append("</transaction_currency>");
            if (_implicitRecharge) {
                sbf.append("<recharge_id>");
                sbf.append(_cardGrp);
                sbf.append("</recharge_id>");
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

                // If bonus validity is separate from the main validity, then
                // subtract it from the main validity.
                String addMainBnsVal = (String) FileCache.getValue(interfaceID, "ADD_MAIN_AND_BUNUS_VALIDITY").trim();
                String bonusValidity = (String) p_map.get("BONUS_VALIDITY_DAYS");
                if ("N".equals(addMainBnsVal) && !BTSLUtil.isNullString(bonusValidity.trim())) {
                    long mainVal = Long.parseLong(validity.trim());
                    long bonusVal = Long.parseLong(bonusValidity);
                    if (bonusVal > 0)
                        validity = String.valueOf(mainVal - bonusVal);
                }

                if (_selectorBundleId.equals("1")) {
                    sbf.append("<transaction_price>");
                    sbf.append(p_map.get("transfer_amount"));
                    sbf.append("</transaction_price>");
                    sbf.append("<credit_params main_activity='" + validity + "'");
                    sbf.append(" main_inactivity='" + grace + "'>");
                } else {
                    Object[] ambList = ((String) p_map.get("BONUS_BUNDLE_IDS")).split("\\|");
                    if (!"1".equals(_selectorBundleId) && _explicitRecharge && (!(Arrays.asList(ambList).contains("1"))))
                        sbf.append("<credit_params>");
                }

                if (_combinedRecharge) {
                    if ("1".equals(_selectorBundleId))
                        sbf.append("</credit_params>");
                    else {
                        Object[] ambList = ((String) p_map.get("BONUS_BUNDLE_IDS")).split("\\|");
                        if (Arrays.asList(ambList).contains("1")) {
                            if ("Y".equals(FileCache.getValue(interfaceID, "BUNDLE_FEATURE_ALLOWED"))) {
                                sbf.append(getBundleRequestString(p_map));
                                if (getBundleRequestString(p_map).contains("<credit_params"))
                                    sbf.append("</credit_params>");
                            }
                        }
                    }
                    sbf.append("<recharge_id>");
                    sbf.append(_cardGrp);
                    sbf.append("</recharge_id>");

                } else if (_explicitRecharge) {

                    amtStr = (String) p_map.get("transfer_amount");
                    String selectorBundlename = FileCache.getValue(interfaceID, _selectorBundleId).trim();
                    // If SMS bundle is defined at IN, then only set it into the
                    // request string.
                    if (!"1".equals(_selectorBundleId)) {
                        Object[] ambList = ((String) p_map.get("BONUS_BUNDLE_IDS")).split("\\|");

                        if (!(Arrays.asList(ambList).contains("1"))) {
                            if (Long.parseLong(validity) > 0 || Long.parseLong(amtStr) > 0) {
                                sbf.append("<crd_param bdl_name=" + "\"" + selectorBundlename + "\"");
                                sbf.append(" bdl_validity=" + "\"" + validity + "\"" + ">" + amtStr + "</crd_param>");
                            }
                            if ("Y".equals(FileCache.getValue(interfaceID, "BUNDLE_FEATURE_ALLOWED")))
                                sbf.append(getBundleRequestString(p_map));
                        } else {
                            if ("Y".equals(FileCache.getValue(interfaceID, "BUNDLE_FEATURE_ALLOWED")))
                                sbf.append(getBundleRequestString(p_map));
                            if (Long.parseLong(validity) > 0 && Long.parseLong(amtStr) > 0) {
                                sbf.append("<crd_param bdl_name=" + "\"" + selectorBundlename + "\"");
                                sbf.append(" bdl_validity=" + "\"" + validity + "\"" + ">" + amtStr + "</crd_param>");
                            }
                        }
                    } else {
                        String bonusBundleAllowed = FileCache.getValue(interfaceID, "BUNDLE_FEATURE_ALLOWED");
                        if ("Y".equals(bonusBundleAllowed))
                            sbf.append(getBundleRequestString(p_map));

                        // This block is only for promotional features
                        String promoBundleAllowed = FileCache.getValue(interfaceID, "PROMO_BUNDLE_ALLOWED");
                        if ("N".equals(bonusBundleAllowed) && !InterfaceUtil.isNullString(promoBundleAllowed) && "Y".equals(promoBundleAllowed)) {
                            sbf.append("<crd_param bdl_name=" + "\"" + FileCache.getValue(interfaceID, "PROMO_BUNDLE_NAME") + "\"");
                            sbf.append(" bdl_validity=" + "\"" + p_map.get("BONUS_VALIDITY_DAYS") + "\"" + ">" + p_map.get("BONUS_AMOUNT") + "</crd_param>");
                        }
                    }
                    sbf.append("</credit_params>");
                }
            }
            sbf.append("</cp_request>");
            requestStr = sbf.toString();
        } catch (Exception e) {
            _log.error("generateImmediateCreditRequest: ", "Exception e: " + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateImmediateCreditRequest: ", "Exited requestStr: " + requestStr);
        }
        return requestStr;
    }// end of generateImmediateCreditRequest

    /*
     * private String getBundleRequestString(HashMap<String,String> p_map)
     * throws Exception
     * {
     * if(_log.isDebugEnabled())
     * _log.debug("getBundleRequestString","Entered  ");
     * String bundleReqStr="";
     * StringBuffer sbfBundle=null;
     * StringBuffer sbfBundleMain=null;
     * String[] bundleIds=null;
     * String[] bundleValidities=null;
     * String[] bundleValues=null;
     * String[] bundleTypes=null;
     * int bonusBundleCount=0;
     * try
     * {
     * if(!InterfaceUtil.isNullString(p_map.get("BONUS_BUNDLE_IDS")))
     * {
     * bundleIds=(p_map.get("BONUS_BUNDLE_IDS")).split("\\|");
     * bundleValidities=((String)p_map.get("BONUS_BUNDLE_VALIDITIES")).split("\\|"
     * );
     * bundleValues=((String)p_map.get("BONUS_BUNDLE_VALUES")).split("\\|");
     * bundleTypes=((String)p_map.get("BONUS_BUNDLE_TYPES")).split("\\|");
     * bonusBundleCount=bundleIds.length;
     * 
     * sbfBundleMain=new StringBuffer(1024);
     * sbfBundle=new StringBuffer(1024);
     * for(int i=0; i<bonusBundleCount;i++)
     * {
     * String bundleCode=FileCache.getValue(_interfaceId,bundleIds[i]);
     * if(InterfaceUtil.isNullString(bundleCode) ||
     * (!bundleIds[i].equalsIgnoreCase("1") && _combinedRecharge))
     * continue;
     * 
     * String bundleValue=bundleValues[i];
     * String bundleValidity=bundleValidities[i];
     * double bundleValueDbl=Double.parseDouble(bundleValue);
     * double inbundleValueDbl=0;
     * long inbundleValueLng=0;
     * if(bundleValueDbl>0 && Long.parseLong(bundleValidity)>0)
     * {
     * if("AMT".equals(bundleTypes[i]))
     * {
     * inbundleValueDbl=InterfaceUtil.getINAmountFromSystemAmountToIN(bundleValueDbl
     * ,Double.parseDouble(FileCache.getValue(_interfaceId,"MULT_FACTOR")));
     * String roundFlag =(String)p_map.get("ROUND_FLAG");
     * inbundleValueLng=Math.round(inbundleValueDbl);
     * if("Y".equals(roundFlag))
     * {
     * inbundleValueLng=Math.round(inbundleValueDbl);
     * }
     * }
     * 
     * if(!"1".equals(bundleIds[i]))
     * {
     * sbfBundle.append("<crd_param bdl_name="+ "\""+bundleCode+"\"");
     * sbfBundle.append(" bdl_validity="+ "\""+bundleValidity+"\">" );
     * if(!"AMT".equals(bundleTypes[i]))
     * sbfBundle.append(bundleValue+"</crd_param>");
     * else
     * sbfBundle.append(String.valueOf(inbundleValueLng)+"</crd_param>");
     * }
     * else
     * {
     * sbfBundleMain.append("<transaction_price>"+String.valueOf(inbundleValueLng
     * )+"</transaction_price>");
     * sbfBundleMain.append("<credit_params main_activity='"+bundleValidity+"'");
     * sbfBundleMain.append(" main_inactivity='0'>");
     * }
     * }
     * else
     * {
     * if(!"1".equals(_selectorBundleId) && "1".equals(bundleIds[i]) &&
     * !_combinedRecharge)
     * sbfBundleMain.append("<credit_params>");
     * }
     * }
     * bundleReqStr=sbfBundleMain.toString()+sbfBundle.toString();
     * }
     * return bundleReqStr;
     * }
     * catch(Exception e)
     * {
     * e.printStackTrace();
     * _log.error("AlcatelOBO452RequestFormatter[getBundleRequestString]",
     * "Exception e::"+e.getMessage());
     * throw e;
     * }
     * finally
     * {
     * if(_log.isDebugEnabled())_log.debug("getBundleRequestString",
     * "Exiting  bundleReqStr="+bundleReqStr);
     * }
     * }
     */
    /**
     * This method is responsible to parse the XML string for the credit
     * response.
     * 
     * @param responseStr
     *            String
     * @throws Exception
     * @return map
     */
    private HashMap<String, String> parseImmediateCreditResponse(String responseStr, String p_interfaceID) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseImmediateCreditResponse: ", "Entered responseStr: " + responseStr);
        HashMap<String, String> map = null;
        String result = null;
        int index = 0;
        try {
            map = new HashMap<String, String>();
            index = responseStr.indexOf("<result>");
            if (index > 0) {
                result = responseStr.substring(index + "<result>".length(), responseStr.indexOf("</result>", index));
                map.put("result", result);
            }
            // Get the value of result and compare this against the success code
            // (0: means success).
            // If the result is equal to 0, parse the XML string and get the
            // value of following elements.
            if (AlcatelOBO452I.RESULT_OK.equals(result)) {
                // cp_id:
                index = responseStr.indexOf("<cp_id>");
                if (index != -1) {
                    String cp_id = responseStr.substring(index + "<cp_id>".length(), responseStr.indexOf("</cp_id>", index));
                    map.put("cp_id", cp_id);
                }
                // cp_transaction_id:
                index = responseStr.indexOf("<cp_transaction_id>");
                if (index != -1) {
                    String cp_transaction_id = responseStr.substring(index + "<cp_transaction_id>".length(), responseStr.indexOf("</cp_transaction_id>", index));
                    map.put("cp_transaction_id", cp_transaction_id);
                }
                // credit_balance
                index = responseStr.indexOf("<credit_balance>");
                if (index != -1) {
                    String credit_balance = responseStr.substring(index + "<credit_balance>".length(), responseStr.indexOf("</credit_balance>", index));
                    map.put("credit_balance", credit_balance);
                }
                // end_val_date
                index = responseStr.indexOf("<end_val_date>");
                if (index != -1) {
                    String end_val_date = responseStr.substring(index + "<end_val_date>".length(), responseStr.indexOf("</end_val_date>", index));
                    map.put("end_val_date", end_val_date);
                }
                // end_inact_date
                index = responseStr.indexOf("<end_inact_date>");
                if (index != -1) {
                    String end_inact_date = responseStr.substring(index + "<end_inact_date>".length(), responseStr.indexOf("</end_inact_date>", index));
                    map.put("end_inact_date", end_inact_date);
                }
            } else {
                // when we get the XML error code 3 or 9 in that case we will
                // not receive cp_id and transaction id only else we will
                // receive both
                if (!(AlcatelOBO452I.RESULT_ERROR_MALFORMED_REQUEST.equals(result) || AlcatelOBO452I.RESULT_ERROR_XML_PARSE.equals(result))) {
                    // cp_id:
                    index = responseStr.indexOf("<cp_id>");
                    if (index != -1) {
                        String cp_id = responseStr.substring(index + "<cp_id>".length(), responseStr.indexOf("</cp_id>", index));
                        map.put("cp_id", cp_id);
                    }
                    // cp_transaction_id:
                    index = responseStr.indexOf("<cp_transaction_id>");
                    if (index != -1) {
                        String cp_transaction_id = responseStr.substring(index + "<cp_transaction_id>".length(), responseStr.indexOf("</cp_transaction_id>", index));
                        map.put("cp_transaction_id", cp_transaction_id);
                    }
                }
                // Else set cp_id and cp_transaction_id equal to NULL into
                // responseMap
                else {
                    map.put("cp_id", null);
                    map.put("cp_transaction_id", null);
                }
            }
        } catch (Exception e) {
            _log.error("parseImmediateCreditResponse: ", "Exception e: " + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseImmediateCreditResponse: ", "Exiting map: " + map);
        }
        return map;
    } // end of parseImmediateCreditResponse

    /**
     * This method is responsible to generate the XML string for the debit
     * request (Only amount is supported).
     * 
     * @param HashMap
     *            p_map
     * @throws Exception
     * @return String
     */
    private String generateImmediateDebitRequest(HashMap<String, String> p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateImmediateDebitRequest: ", "Entered p_map=" + p_map);
        String requestStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);

            sbf.append("<?xml version=\"1.0\"?>");
            sbf.append("<!DOCTYPE cp_request SYSTEM \"cp_req_websvr.dtd\">");
            sbf.append("<cp_request>");
            // cp_id: This id identifies the External Entity that interacts with
            // the ICC,
            // and is present in request map under the key cp_id.
            sbf.append("<cp_id>");
            sbf.append(p_map.get("cp_id"));
            sbf.append("</cp_id>");

            // cp_transaction_id: It is the identifier of the transaction
            // running between the External Entity and the ICC.
            // The getINReconTxnID method generates cp_transaction_id.
            sbf.append("<cp_transaction_id>");
            sbf.append(getINReconTxnID(p_map));
            sbf.append("</cp_transaction_id>");

            // op_transaction_id: It conveys information related to the session
            // parameters kept at the DDCF
            // application side in case of two-step Debit transactions.
            // op_transaction_id would be blank
            sbf.append("<op_transaction_id>");
            sbf.append("</op_transaction_id>");

            // application: It specifies which application is to be triggered on
            // the ICC product platform. This field must be set to ‘1’,
            // that represents the Direct Debit Credit Function (DDCF)
            // application. This parameter MUST be present in all requests sent
            // to ICC.
            // Value of application would be present in requestMap under key as
            // application.
            sbf.append("<application>");
            sbf.append(p_map.get("application"));
            sbf.append("</application>");

            // action: It characterizes the kind of the request and MUST be
            // present in all requests sent to DDCF
            // application. Value of action would be present in requestMap under
            // key as action
            sbf.append("<action>");
            sbf.append(p_map.get("action"));
            sbf.append("</action>");

            sbf.append("<user_id>");
            sbf.append(InterfaceUtil.getFilterMSISDN((String) p_map.get("INTERFACE_ID"), (String) p_map.get("MSISDN")));
            sbf.append("</user_id>");

            // account-id will be sent in the request. Value will be MSISDN
            // only.
            sbf.append("<account_id a_type=\"Login\">");
            sbf.append(InterfaceUtil.getFilterMSISDN((String) p_map.get("INTERFACE_ID"), (String) p_map.get("MSISDN")));
            // sbf.append((String)p_map.get("IN_ACCOUNT_ID"));
            sbf.append("</account_id>");

            sbf.append("<transaction_currency>");
            sbf.append("1");
            sbf.append("</transaction_currency>");

            sbf.append("<transaction_price>");
            sbf.append(p_map.get("transfer_amount"));
            sbf.append("</transaction_price>");

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
    }// end of generateImmediateDebitRequest

    /**
     * This Method parse Immediate Debit Response
     * 
     * @param responseStr
     *            String
     * @throws Exception
     * @return map
     */
    public HashMap<String, String> parseImmediateDebitResponse(String responseStr, String p_interfaceID) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseImmediateDebitResponse: ", "Entered responseStr: " + responseStr);
        HashMap<String, String> map = new HashMap<String, String>();
        String result = null;
        int index = 0;
        try {
            index = responseStr.indexOf("<result>");
            if (index > 0) {
                result = responseStr.substring(index + "<result>".length(), responseStr.indexOf("</result>", index));
                map.put("result", result);
            }
            // if result value is 0, get the parameter in to the map
            if (AlcatelOBO452I.RESULT_OK.equals(result)) {
                // cp_id:
                index = responseStr.indexOf("<cp_id>");
                if (index != -1) {
                    String cp_id = responseStr.substring(index + "<cp_id>".length(), responseStr.indexOf("</cp_id>", index));
                    map.put("cp_id", cp_id);
                }
                // cp_transaction_id:
                index = responseStr.indexOf("<cp_transaction_id>");
                if (index != -1) {
                    String cp_transaction_id = responseStr.substring(index + "<cp_transaction_id>".length(), responseStr.indexOf("</cp_transaction_id>", index));
                    map.put("cp_transaction_id", cp_transaction_id);
                }
                // acc_status
                index = responseStr.indexOf("<account_id>");
                if (index != -1) {
                    String account_id = responseStr.substring(index + "<account_id>".length(), responseStr.indexOf("</account_id>", index));
                    map.put("account_id", account_id);
                }
            } else {
                // when we get the XML error code 3 or 9 in that case we will
                // not receive cp_id and transaction id only else we will
                // receive both
                if (!(AlcatelOBO452I.RESULT_ERROR_MALFORMED_REQUEST.equals(result) || AlcatelOBO452I.RESULT_ERROR_XML_PARSE.equals(result))) {
                    // cp_id:
                    index = responseStr.indexOf("<cp_id>");
                    if (index != -1) {
                        String cp_id = responseStr.substring(index + "<cp_id>".length(), responseStr.indexOf("</cp_id>", index));
                        map.put("cp_id", cp_id);
                    }
                    // cp_transaction_id:
                    index = responseStr.indexOf("<cp_transaction_id>");
                    if (index != -1) {
                        String cp_transaction_id = responseStr.substring(index + "<cp_transaction_id>".length(), responseStr.indexOf("</cp_transaction_id>", index));
                        map.put("cp_transaction_id", cp_transaction_id);
                    }
                }
                // Else set cp_id and cp_transaction_id equal to NULL into
                // responseMap.
                else {
                    map.put("cp_id", null);
                    map.put("cp_transaction_id", null);
                }
                map.put("result", result);
            }
            return map;
        }// end of try block.
        catch (Exception e) {
            _log.error("parseImmediateDebitResponse: ", "Exception e: " + e.getMessage());
            throw e;
        }// end of Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseImmediateDebitResponse: ", "Exiting map: " + map);
        }
    }// end of parseImmediateDebitResponse

    /**
     * This Method will parse the response received from the IN to get the
     * bundles information.
     * It will get the bundle names define at the IN and set it in to the
     * response map.
     * 
     * @param p_responseStr
     *            String
     * @param p_requestMap
     *            HashMap
     * @throws Exception
     * @return HashMap
     */
    private void setBundlesRcvdFromIN(String p_responseStr, HashMap<String, String> p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("setDefinedBundlesInToMap", "Entered ");
        int index = 0;
        int noOfBundles = 0;
        String bdl_name = null;
        String bdlNameStr = null;
        try {
            // Check whether bundle is present is string or not, if present then
            // split it.
            index = p_responseStr.indexOf("</bundle>");
            if (index != -1) {
                String[] bundleArray = p_responseStr.split("</bundle>");
                noOfBundles = bundleArray.length;
                for (int i = 0; i < noOfBundles; i++) {
                    // get the bdl_name if present in the bucket.
                    index = bundleArray[i].indexOf("<bdl_name>");
                    if (index != -1) {
                        bdl_name = bundleArray[i].substring(index + "<bdl_name>".length(), bundleArray[i].indexOf("</bdl_name>", index));
                        System.out.println("bdl_name" + bdl_name);
                        if (!InterfaceUtil.isNullString(bdlNameStr))
                            bdlNameStr = bdlNameStr + "," + bdl_name;
                        else
                            bdlNameStr = bdl_name;
                    }
                }
            }
            if (!InterfaceUtil.isNullString(bdlNameStr))
                p_requestMap.put("received_bundles", bdlNameStr.trim());
            else
                p_requestMap.put("received_bundles", bdlNameStr);
        } catch (Exception e) {
            _log.error("setDefinedBundlesInToMap: ", "Exception e: " + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("setDefinedBundlesInToMap: ", "Exiting Defined bundles at IN: " + (String) p_requestMap.get("received_bundles"));
        }
    }

    private String getBundleRequestString(HashMap<String, String> p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getBundleRequestString", "Entered  ");

        String bundleReqStr = "";
        StringBuffer sbfBundle = null;
        StringBuffer sbfBundleMain = null;
        String[] bundleIds = null;
        String[] bundleValidities = null;
        String[] bundleValues = null;
        String[] bundleTypes = null;
        String[] bundleName = null;
        int bonusBundleCount = 0;
        try {
            if (!InterfaceUtil.isNullString(p_map.get("BONUS_BUNDLE_IDS"))) {
                bundleIds = (p_map.get("BONUS_BUNDLE_IDS")).split("\\|");
                bundleValidities = (p_map.get("BONUS_BUNDLE_VALIDITIES")).split("\\|");
                bundleValues = (p_map.get("BONUS_BUNDLE_VALUES")).split("\\|");
                bundleTypes = (p_map.get("BONUS_BUNDLE_TYPES")).split("\\|");
                bundleName = (p_map.get("BONUS_BUNDLE_NAMES")).split("\\|");
                bonusBundleCount = bundleIds.length;

                sbfBundleMain = new StringBuffer(1024);
                sbfBundle = new StringBuffer(1024);
                for (int i = 0; i < bonusBundleCount; i++) {
                    System.out.println(bundleIds[i] + ", " + bundleName[i] + ", " + bundleValidities[i] + ", " + bundleValues[i] + ", " + bundleTypes[i]);

                    Object[] ambList = (p_map.get("IN_RESP_BUNDLE_CODES")).split("%2C");
                    if (!(Arrays.asList(ambList).contains(bundleName[i])))
                        continue;

                    String bundleValue = bundleValues[i];
                    String bundleValidity = bundleValidities[i];
                    double bundleValueDbl = Double.parseDouble(bundleValue);
                    double inbundleValueDbl = 0;
                    long inbundleValueLng = 0;
                    if (bundleValueDbl > 0 || Long.parseLong(bundleValidity) > 0) {
                        if ("AMT".equals(bundleTypes[i])) {
                            // change the multiplication factor
                            inbundleValueDbl = InterfaceUtil.getINAmountFromSystemAmountToIN(bundleValueDbl, Double.parseDouble(FileCache.getValue(_interfaceId, "AMT_MULT_FACTOR")));// add
                                                                                                                                                                                      // by
                                                                                                                                                                                      // ved
                            String roundFlag = p_map.get("ROUND_FLAG");
                            inbundleValueLng = Math.round(inbundleValueDbl);
                            if ("Y".equals(roundFlag))
                                inbundleValueLng = Math.round(inbundleValueDbl);
                        } else// add by ved
                        {
                            inbundleValueDbl = InterfaceUtil.getINAmountFromSystemAmountToIN(bundleValueDbl, Double.parseDouble(FileCache.getValue(_interfaceId, "UNIT_MULT_FACTOR")));
                            String roundFlag = p_map.get("ROUND_FLAG");
                            inbundleValueLng = Math.round(inbundleValueDbl);
                            if ("Y".equals(roundFlag))
                                inbundleValueLng = Math.round(inbundleValueDbl);
                        }

                        if (!"1".equals(bundleIds[i])) {

                            sbfBundle.append("<crd_param bdl_name=" + "\"" + bundleName[i] + "\"");
                            sbfBundle.append(" bdl_validity=" + "\"" + bundleValidity + "\">");
                            // Comment by ved
                            /*
                             * if(!"AMT".equals(bundleTypes[i]))
                             * sbfBundle.append(bundleValue+"</crd_param>");
                             * else
                             */
                            sbfBundle.append(String.valueOf(inbundleValueLng) + "</crd_param>");
                        } else {
                            sbfBundleMain.append("<transaction_price>" + String.valueOf(inbundleValueLng) + "</transaction_price>");
                            sbfBundleMain.append("<credit_params main_activity='" + bundleValidity + "'");
                            sbfBundleMain.append(" main_inactivity='0'>");
                        }
                    } else {
                        if (!"1".equals(_selectorBundleId) && "1".equals(bundleIds[i]) && !_combinedRecharge)
                            sbfBundleMain.append("<credit_params>");
                    }
                }
                bundleReqStr = sbfBundleMain.toString() + sbfBundle.toString();
            }
            return bundleReqStr;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("AlcatelOBO452RequestFormatter[getBundleRequestString]", "Exception e::" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getBundleRequestString", "Exiting  bundleReqStr=" + bundleReqStr);
        }
    }
}
