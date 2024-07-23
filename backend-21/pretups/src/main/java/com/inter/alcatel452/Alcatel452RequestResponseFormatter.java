package com.inter.alcatel452;

/**
 * @(#)Alcatel452RequestResponseFormatter.java
 *                                             Copyright(c) 2008, Bharti
 *                                             Telesoft Int. Public Ltd.
 *                                             All Rights Reserved
 *                                             --------------------------------
 *                                             --
 *                                             --------------------------------
 *                                             -------------------------------
 *                                             Author Date History
 *                                             --------------------------------
 *                                             --
 *                                             --------------------------------
 *                                             -------------------------------
 *                                             Vinay Kumar Singh Aug 04, 2008
 *                                             Initial Creation
 *                                             --------------------------------
 *                                             --
 *                                             --------------------------------
 *                                             ------------------------------
 *                                             This class can be used as a
 *                                             parser class for both
 *                                             request(before sending the
 *                                             request to IN) and
 *                                             response(after getting the
 *                                             response from the IN).
 */
import java.util.HashMap;
import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.util.BTSLUtil;

public class Alcatel452RequestResponseFormatter {
    // Get the logger object, which is used to write different types of logs.
    public static Log _log = LogFactory.getLog("Alcatel452RequestResponseFormatter".getClass().getName());

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
            _log.debug("generateRequest: ", "Entered p_action:" + p_action + " p_map: " + p_map);
        String str = null;
        try {
            p_map.put("action", String.valueOf(p_action));
            switch (p_action) {
            case Alcatel452I.ACTION_GET_ACCOUNT_INFO: {
                str = generateGetAccountInfoRequest(p_map);
                break;
            }
            case Alcatel452I.ACTION_IMMEDIATE_CREDIT: {
                if ("Y".equals((String) p_map.get("ADJUST")))
                    str = generateCreditAdjustRequest(p_map);
                else
                    str = generateImmediateCreditRequest(p_map);
                break;
            }
            case Alcatel452I.ACTION_IMMEDIATE_DEBIT: {
                str = generateImmediateDebitRequest(p_map);
                break;
            }
            }
        } catch (Exception e) {
            _log.error("generateRequest: ", "Exception e::" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateRequest: ", "Exited Str:" + str);
        }
        return str;
    }// end of generateRequest

    /**
     * Based on the action value, a method is referenced to parse the xml string
     * for corresponding response.
     * 
     * @param int p_action
     * @param String
     *            p_responseStr
     * @return HashMap
     */
    protected HashMap parseResponse(int p_action, String p_responseStr, String p_interfaceID) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseResponse: ", "Entered p_action:" + p_action + " responseStr:  " + p_responseStr);
        HashMap map = null;
        try {
            switch (p_action) {
            case Alcatel452I.ACTION_GET_ACCOUNT_INFO: {
                map = parseGetAccountInfoResponse(p_responseStr, p_interfaceID);
                break;
            }
            case Alcatel452I.ACTION_IMMEDIATE_CREDIT: {
                map = parseImmediateCreditResponse(p_responseStr, p_interfaceID);
                break;
            }
            case Alcatel452I.ACTION_IMMEDIATE_DEBIT: {
                map = parseImmediateDebitResponse(p_responseStr, p_interfaceID);
                break;
            }
            }
        } catch (Exception e) {
            _log.error("parseResponse: ", "Exception e:" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseResponse: ", "Exiting map: " + map);
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
    private String generateGetAccountInfoRequest(HashMap map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateGetAccountInfoRequest: ", "Entered map=" + map);
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
            sbf.append(map.get("IN_RECON_ID"));
            sbf.append("</cp_transaction_id>");

            // op_transaction_id: It conveys information related to the session
            // parameters kept at the DDCF
            // application side in case of two-step Debit transactions.
            // op_transaction_id would be blank
            sbf.append("<op_transaction_id></op_transaction_id>");

            // application: It specifies which application is to be triggered on
            // the ICC product platform. This field must be set to .1.,
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

            // user_id: : It identifies the user whom the direct debit or direct
            // credit operation is to be made for.
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
    private HashMap parseGetAccountInfoResponse(String p_responseStr, String p_interfaceID) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseGetAccountInfoResponse: ", "Entered p_responseStr: " + p_responseStr);
        HashMap responseMap = null;
        String interfaceID = null;
        try {
            responseMap = new HashMap();
            interfaceID = p_interfaceID;
            int index = p_responseStr.indexOf("<result>");
            String result = p_responseStr.substring(index + "<result>".length(), p_responseStr.indexOf("</result>", index));
            responseMap.put("result", result);

            if (Alcatel452I.RESULT_OK.equals(result)) {
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
                // At the time of preintegration testing lock_information is
                // made as the mandatory tag
                index = p_responseStr.indexOf("<lock_information>");
                if (index != -1) {
                    String lock_info = p_responseStr.substring(index + "<lock_information>".length(), p_responseStr.indexOf("</lock_information>", index));
                    responseMap.put("lock_info", lock_info);
                }
                // Set the defined bundle names into the response map.
                setDefinedBundlesInToMap(p_responseStr, responseMap);
            }// end of if- result=OK
            else {
                /**
                 * Note: when we get the xml error code 3 or 9 in that case we
                 * do not receive cp_id and transaction id only else we will
                 */
                if (!(Alcatel452I.RESULT_ERROR_MALFORMED_REQUEST.equals(result)) || !(Alcatel452I.RESULT_ERROR_XML_PARSE.equals(result))) {
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
     * This method is responsible to generate the xml string for the credit
     * adjust(CP2P) request.
     * 
     * @param HashMap
     *            map
     * @return String requestStr
     * @throws Exception
     */
    private String generateCreditAdjustRequest(HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateCreditAdjustRequest: ", "Entered p_map:" + p_map);
        String requestStr = null;
        StringBuffer sbf = null;
        String interfaceID = null;
        String sepBonusAllowed = null;
        String mulFac = null;
        String amtStr = null;
        long mainBonusAmt = 0;
        long bonus1 = 0;
        long bonus2 = 0;
        try {
            sbf = new StringBuffer(1024);

            // get the interface id from the map.
            interfaceID = (String) p_map.get("INTERFACE_ID");
            // Calculate the main bonus amount.
            if (!BTSLUtil.isNullString((String) p_map.get("IN_BONUS_AMOUNT")))
                mainBonusAmt = Math.round(Double.parseDouble((String) p_map.get("IN_BONUS_AMOUNT")));
            // Calculate the SMS bonus amount.
            if (!BTSLUtil.isNullString((String) p_map.get("BONUS1")))
                bonus1 = Math.round(Double.parseDouble((String) p_map.get("BONUS1")));
            // Calculate the MMS bonus amount.
            if (!BTSLUtil.isNullString((String) p_map.get("BONUS1")))
                bonus1 = Math.round(Double.parseDouble((String) p_map.get("BONUS1")));

            sbf.append("<?xml version=\"1.0\"?>");
            sbf.append("<!DOCTYPE cp_request SYSTEM \"cp_req_websvr.dtd\">");
            sbf.append("<cp_request>");
            // cp_id: This id identifies the External Entity that interacts with
            // the ICC,
            // and is present in request map under the key cp_id.
            sbf.append("<cp_id>");
            sbf.append(p_map.get("cp_id"));
            sbf.append("</cp_id>");
            // op_transaction_id: It conveys information related to the session
            // parameters kept at the DDCF
            // application side in case of two-step Debit transactions.
            // op_transaction_id would be blank
            // sbf.append("<op_transaction_id></op_transaction_id>");

            // cp_transaction_id: It is the identifier of the transaction
            // running between the External Entity and the ICC.
            // The getINReconTxnID method generates cp_transaction_id.
            sbf.append("<cp_transaction_id>");
            sbf.append(p_map.get("IN_RECON_ID"));
            sbf.append("</cp_transaction_id>");

            // application: It specifies which application is to be triggered on
            // the ICC product platform. This field must be set to .1.,
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

            // transaction_price: Withholds an amount of money.Within a credit
            // request, this is the amount of money to be credited on the main
            // balance.
            // The transaction_price and recharge_id parameters are exclusive.
            // Value of transaction_price will be get from requestMap using key
            // as transfer_amount.
            sbf.append("<transaction_price>");
            sbf.append(p_map.get("transfer_amount"));
            sbf.append("</transaction_price>");
            // Set the validity and grace equal to ZERO (0)·
            sbf.append("<credit_params main_activity='0' main_inactivity='0'>");

            // If bonus is defined for the Main, SMS and MMS, then set the bonus
            // amounts.
            // If bonus is defined for the Main, SMS and MMS, then set the bonus
            // amounts.
            sepBonusAllowed = (String) p_map.get("SEPERATE_BONUS_ALLOWED");
            // If seperate bonus is not allowed, then set the main bonus amouunt
            // zero.
            if ("N".equals(sepBonusAllowed))
                mainBonusAmt = 0;
            // If main bonus amount is greater than zero(0) then set it in to
            // the request string.
            if (mainBonusAmt > 0) {
                // Get the Voice bundle name from the file cache.
                String voiceBundle = (String) FileCache.getValue(interfaceID, "VOICE_BUNDLE_NAME").trim();
                if (InterfaceUtil.isNullString(voiceBundle)) {
                    _log.error("generateCreditAdjustRequest: ", "VOICE_BUNDLE_NAME is not defined in the INFile. ");
                    throw new BTSLBaseException(this, "generateCreditAdjustRequest", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                }
                // If Voice bundle is defined at the IN, then set it in to the
                // request string.
                if (isBundleDefinedAtIN(voiceBundle, p_map)) {
                    String mainBonusAmtStr = (String) p_map.get("IN_BONUS_AMOUNT");
                    sbf.append("<crd_param bdl_name=" + "\"" + voiceBundle + "\"");
                    sbf.append(" bdl_validity=" + "\"" + p_map.get("CREDIT_BONUS_VAL") + "\">");
                    sbf.append(mainBonusAmtStr + ".0</crd_param>");
                }
            }
            // Set the SMS bonus in to the request string.
            if (bonus1 > 0) {
                // Get the SMS_Bonus bundle name from the file cache.
                String smsBonusBundle = (String) FileCache.getValue(interfaceID, "SMS_BONUS_BUNDLE_NAME").trim();
                if (InterfaceUtil.isNullString(smsBonusBundle)) {
                    _log.error("generateCreditAdjustRequest: ", "SMS_BONUS_BUNDLE_NAME is not defined in the INFile, smsBonusBundle: " + smsBonusBundle);
                    throw new BTSLBaseException(this, "generateImmediateCreditRequest", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                }
                // If SMS Bonus bundle is defined at the IN, then set it in to
                // the request string.
                if (isBundleDefinedAtIN(smsBonusBundle, p_map)) {
                    // get the multiplication factor for the SMS Bonus bundle.
                    mulFac = (String) FileCache.getValue(interfaceID, "SMS_MULT_FACTOR").trim();
                    String smsAmtStr = (String) p_map.get("BONUS1");
                    smsAmtStr = getINAmtValueStr(p_map, mulFac, smsAmtStr);
                    sbf.append("<crd_param bdl_name=" + "\"" + smsBonusBundle + "\"");
                    sbf.append(" bdl_validity=" + "\"" + (String) p_map.get("BONUS1_VAL") + "\">");
                    sbf.append(smsAmtStr + "</crd_param>");
                }
            }
            // Set the MMS bonus in to the request string.
            if (bonus2 > 0) {
                // Get the MMS_Bonus bundle name from the file cache.
                String mmsBonusBundle = (String) FileCache.getValue(interfaceID, "MMS_BONUS_BUNDLE_NAME").trim();
                if (InterfaceUtil.isNullString(mmsBonusBundle)) {
                    _log.error("generateCreditAdjustRequest: ", "MMS_BONUS_BUNDLE_NAME is not defined in the INFile, smsBonusBundle: " + mmsBonusBundle);
                    throw new BTSLBaseException(this, "generateImmediateCreditRequest", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                }
                // If MMS Bonus bundle is defined at the IN, then set it in to
                // the request string.
                if (isBundleDefinedAtIN(mmsBonusBundle, p_map)) {
                    // get the multiplication factor for the MMS Bonus bundle.
                    mulFac = FileCache.getValue(interfaceID, "MMS_MULT_FACTOR");
                    String mmsAmtStr = (String) p_map.get("BONUS2");
                    mmsAmtStr = getINAmtValueStr(p_map, mulFac, mmsAmtStr);
                    sbf.append("<crd_param bdl_name=" + "\"" + mmsBonusBundle + "\"");
                    sbf.append(" bdl_validity=" + "\"" + (String) p_map.get("BONUS2_VAL") + "\">");
                    sbf.append(mmsAmtStr + "</crd_param>");
                }
            }
            sbf.append("</credit_params>");
            // close the cp_request tag.
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
     * This method is responsible to generate the xml string for the credit
     * request.
     * 
     * @param HashMap
     *            map
     * @return String requestStr
     * @throws Exception
     */
    private String generateImmediateCreditRequest(HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateImmediateCreditRequest: ", "Entered p_map:" + p_map);
        String requestStr = null;
        StringBuffer sbf = null;
        String interfaceID = null;
        String cardGroupSelector = null;
        String sepBonusAllowed = null;
        String roundUnitValue = null;
        String mulFac = null;
        String amtStr = null;
        long mainBonusAmt = 0;
        long bonus1 = 0;
        long bonus2 = 0;
        String mainBonusWithSmsMms = null;
        try {
            sbf = new StringBuffer(1024);
            // Get the card group selector value from the map.
            cardGroupSelector = (String) p_map.get("CARD_GROUP_SELECTOR");
            // Calculate the main bonus amount.
            if (!BTSLUtil.isNullString((String) p_map.get("IN_BONUS_AMOUNT")))
                mainBonusAmt = Math.round(Double.parseDouble((String) p_map.get("IN_BONUS_AMOUNT")));
            // Calculate the SMS bonus amount.
            if (!BTSLUtil.isNullString((String) p_map.get("BONUS1")))
                bonus1 = Math.round(Double.parseDouble((String) p_map.get("BONUS1")));
            // Calculate the MMS bonus amount.
            if (!BTSLUtil.isNullString((String) p_map.get("BONUS2")))
                bonus2 = Math.round(Double.parseDouble((String) p_map.get("BONUS2")));
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
            sbf.append(p_map.get("IN_RECON_ID"));
            sbf.append("</cp_transaction_id>");

            // application: It specifies which application is to be triggered on
            // the ICC product platform. This field must be set to .1.,
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
            // Get the value of VALIDITY_DAYS from requestMap
            String validity = (String) p_map.get("VALIDITY_DAYS");
            // Get the value of GRACE_DAYS from requestMap
            String grace = (String) p_map.get("GRACE_DAYS");
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
            // If bonus is defined for the Main, SMS and MMS, then set the bonus
            // amounts.
            sepBonusAllowed = (String) p_map.get("SEPERATE_BONUS_ALLOWED");
            // transaction_price: Withholds an amount of money.Within a credit
            // request, this is the amount of money to be credited on the main
            // balance.
            // The transaction_price and recharge_id parameters are exclusive.
            // Value of transaction_price will be get from requestMap using key
            // as transfer_amount.
            if (cardGroupSelector.equals("1")) {
                sbf.append("<transaction_price>");
                sbf.append(p_map.get("transfer_amount"));
                sbf.append("</transaction_price>");
                sbf.append("<credit_params main_activity='" + validity + "'");
                sbf.append(" main_inactivity='" + grace + "'>");
                // If seperate bonus is not allowed, then set the main bonus
                // amouunt zero.
                if ("N".equals(sepBonusAllowed))
                    mainBonusAmt = 0;
            }
            // If SMS or MMS is to be credited.
            else {
                double amtStrDouble = Double.parseDouble((String) p_map.get("transfer_amount"));
                amtStr = String.valueOf(Math.round(amtStrDouble));
                sbf.append("<credit_params>");
                // If SMS is to be credited.
                roundUnitValue = FileCache.getValue(interfaceID, "ROUND_FLAG_FOR_UNIT").trim();
                if (cardGroupSelector.equals("2")) {
                    // Get the SMS bundle name
                    String smsBundleName = FileCache.getValue(interfaceID, "SMS_BUNDLE_NAME").trim();
                    // If SMS bundle is defined at IN, then only set it into the
                    // request string.
                    if (isBundleDefinedAtIN(smsBundleName, p_map)) {
                        // If seperate bonus is not allowed then club the
                        // amounts in transfer_amount.
                        if ("N".equals(sepBonusAllowed)) {
                            // Get the system out multiplication factor from the
                            // FileCache with the help of interface id.
                            String sysOutMultFactor = FileCache.getValue(interfaceID, "SYSTEM_OUT_MULT_FACTOR");
                            double requestedAmtDouble = Double.parseDouble((String) p_map.get("INTERFACE_AMOUNT"));
                            double inAmount = InterfaceUtil.getINAmountFromSystemAmountToIN(requestedAmtDouble, Double.parseDouble(sysOutMultFactor));
                            String bonusAmt = (String) p_map.get("BONUS_AMOUNT");
                            if (!BTSLUtil.isNullString(bonusAmt)) {
                                double requestedBonus = Double.parseDouble(bonusAmt);
                                requestedBonus = InterfaceUtil.getINAmountFromSystemAmountToIN(requestedBonus, Double.parseDouble(sysOutMultFactor));
                                inAmount = inAmount - requestedBonus;
                            }
                            mulFac = FileCache.getValue(interfaceID, "SMS_MULT_FACTOR");
                            String bonusVal = (String) p_map.get("BONUS1");
                            bonusVal = getINAmtValueStr(p_map, mulFac, bonusVal);
                            double totalAmt = Math.round(inAmount) + Math.round(Double.parseDouble(bonusVal));
                            amtStr = String.valueOf(Math.round(totalAmt));
                            // Set the SMS Bonus amount zero so that it can not
                            // be send seperately.
                            bonus1 = 0;
                        }
                        if ("N".equals(roundUnitValue))
                            amtStr = amtStr + ".0";
                        sbf.append("<crd_param bdl_name=" + "\"" + smsBundleName + "\"");
                        sbf.append(" bdl_validity=" + "\"" + validity + "\"" + ">" + amtStr + "</crd_param>");
                    }
                }
                // If MMS is to be credited.
                else if (cardGroupSelector.equals("3")) {
                    // Get the MMS bundle name
                    String mmsBundleName = FileCache.getValue(interfaceID, "MMS_BUNDLE_NAME").trim();
                    // If MMS bundle is defined at IN, then only set it into the
                    // request string.
                    if (isBundleDefinedAtIN(mmsBundleName, p_map)) {
                        // If seperate bonus is not allowed then club the
                        // amounts in transfer_amount.
                        if ("N".equals(sepBonusAllowed)) {
                            // Get the system out multiplication factor from the
                            // FileCache with the help of interface id.
                            String sysOutMultFactor = FileCache.getValue(interfaceID, "SYSTEM_OUT_MULT_FACTOR");
                            double requestedAmtDouble = Double.parseDouble((String) p_map.get("INTERFACE_AMOUNT"));
                            double inAmount = InterfaceUtil.getINAmountFromSystemAmountToIN(requestedAmtDouble, Double.parseDouble(sysOutMultFactor));
                            String bonusAmt = (String) p_map.get("BONUS_AMOUNT");
                            if (!BTSLUtil.isNullString(bonusAmt)) {
                                double requestedBonus = Double.parseDouble(bonusAmt);
                                requestedBonus = InterfaceUtil.getINAmountFromSystemAmountToIN(requestedBonus, Double.parseDouble(sysOutMultFactor));
                                inAmount = inAmount - requestedBonus;
                            }
                            mulFac = FileCache.getValue(interfaceID, "MMS_MULT_FACTOR");
                            String bonusVal = (String) p_map.get("BONUS2");
                            bonusVal = getINAmtValueStr(p_map, mulFac, bonusVal);
                            double totalAmt = Math.round(inAmount) + Math.round(Double.parseDouble(bonusVal));
                            amtStr = String.valueOf(Math.round(totalAmt));
                            // Set the MMS Bonus amount zero so that it can not
                            // be send seperately.
                            bonus2 = 0;
                        }
                        if ("N".equals(roundUnitValue))
                            amtStr = amtStr + ".0";
                        sbf.append("<crd_param bdl_name=" + "\"" + mmsBundleName + "\"");
                        sbf.append(" bdl_validity=" + "\"" + validity + "\"" + ">" + amtStr + "</crd_param>");
                    }
                }
            }
            // If bonus is defined for the Main, SMS and MMS, then set the bonus
            // amounts.
            // If main bonus amount is greater than zero(0) then set it in to
            // the request string.
            // For selector value other than 1 i.e. Main account recharge, check
            // whether Main bonus is allowed
            // for other selector values or not i.e. for SMS or MMS(Data).
            mainBonusWithSmsMms = FileCache.getValue(interfaceID, "VOICE_BUNDLE_WITH_SMS_MMS");
            if (cardGroupSelector.equals("1") || "Y".equals(mainBonusWithSmsMms)) {
                if (mainBonusAmt > 0) {
                    String voiceBundle = FileCache.getValue(interfaceID, "VOICE_BUNDLE_NAME").trim();
                    if (InterfaceUtil.isNullString(voiceBundle)) {
                        _log.error("generateImmediateCreditRequest ", "VOICE_BUNDLE_NAME is not defined in the INFile. ");
                        throw new BTSLBaseException(this, "generateImmediateCreditRequest", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                    }
                    // If Voice bundle is defined at IN, then only set it into
                    // the request string.
                    if (isBundleDefinedAtIN(voiceBundle, p_map)) {
                        String mainBonusAmtStr = (String) p_map.get("IN_BONUS_AMOUNT");
                        sbf.append("<crd_param bdl_name=" + "\"" + voiceBundle + "\"");
                        sbf.append(" bdl_validity=" + "\"" + (String) p_map.get("CREDIT_BONUS_VAL") + "\">");
                        sbf.append(mainBonusAmtStr + ".0</crd_param>");
                    }
                }
            }
            // If bonus for SMS is greater than zero, set it in to the request
            // string.
            if (bonus1 > 0) {
                // Get the SMS_Bonus bundle name from the file cache.
                String smsBonusBundle = (String) FileCache.getValue(interfaceID, "SMS_BONUS_BUNDLE_NAME").trim();
                if (InterfaceUtil.isNullString(smsBonusBundle)) {
                    _log.error("generateImmediateCreditRequest: ", "SMS_BONUS_BUNDLE_NAME is not defined in the INFile, smsBonusBundle: " + smsBonusBundle);
                    throw new BTSLBaseException(this, "generateImmediateCreditRequest", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                }
                // If SMS bundle is defined at IN, then only set it into the
                // request string.
                if (isBundleDefinedAtIN(smsBonusBundle, p_map)) {
                    // Get the multiplication factor for the SMS bonus.
                    mulFac = FileCache.getValue(interfaceID, "SMS_MULT_FACTOR").trim();
                    String smsAmtStr = (String) p_map.get("BONUS1");
                    smsAmtStr = getINAmtValueStr(p_map, mulFac, smsAmtStr);
                    sbf.append("<crd_param bdl_name=" + "\"" + smsBonusBundle + "\"");
                    sbf.append(" bdl_validity=" + "\"" + (String) p_map.get("BONUS1_VAL") + "\">");
                    sbf.append(smsAmtStr + "</crd_param>");
                }
            }
            // If bonus for MMS is greater than zero, set it in to the request
            // string.
            if (bonus2 > 0) {
                // Get the MMS_Bonus bundle name from the file cache.
                String mmsBonusBundle = (String) FileCache.getValue(interfaceID, "MMS_BONUS_BUNDLE_NAME").trim();
                if (InterfaceUtil.isNullString(mmsBonusBundle)) {
                    _log.error("generateImmediateCreditRequest: ", "MMS_BONUS_BUNDLE_NAME is not defined in the INFile, smsBonusBundle: " + mmsBonusBundle);
                    throw new BTSLBaseException(this, "generateImmediateCreditRequest", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                }
                // If MMS bundle is defined at IN, then only set it into the
                // request string.
                if (isBundleDefinedAtIN(mmsBonusBundle, p_map)) {
                    // Get the multiplication factor for the MMS bonus.
                    mulFac = FileCache.getValue(interfaceID, "MMS_MULT_FACTOR");
                    String mmsAmtStr = (String) p_map.get("BONUS2");
                    mmsAmtStr = getINAmtValueStr(p_map, mulFac, mmsAmtStr);
                    sbf.append("<crd_param bdl_name=" + "\"" + mmsBonusBundle + "\"");
                    sbf.append(" bdl_validity=" + "\"" + (String) p_map.get("BONUS2_VAL") + "\">");
                    sbf.append(mmsAmtStr + "</crd_param>");
                }
            }
            sbf.append("</credit_params>");
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

    /**
     * This method is responsible to parse the xml string for the credit
     * response.
     * 
     * @param responseStr
     *            String
     * @throws Exception
     * @return map
     */
    private HashMap parseImmediateCreditResponse(String responseStr, String p_interfaceID) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseImmediateCreditResponse: ", "Entered responseStr: " + responseStr);
        HashMap map = null;
        String result = null;
        int index = 0;
        try {
            map = new HashMap();
            index = responseStr.indexOf("<result>");
            if (index > 0) {
                result = responseStr.substring(index + "<result>".length(), responseStr.indexOf("</result>", index));
                map.put("result", result);
            }
            // Get the value of result and compare this against the success code
            // (0: means success).
            // If the result is equal to 0, parse the xml string and get the
            // value of following elements.
            if (Alcatel452I.RESULT_OK.equals(result)) {
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
                // when we get the xml error code 3 or 9 in that case we will
                // not receive cp_id and transaction id only else we will
                // receive both
                if (!(Alcatel452I.RESULT_ERROR_MALFORMED_REQUEST.equals(result) || Alcatel452I.RESULT_ERROR_XML_PARSE.equals(result))) {
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
            sbf.append(p_map.get("IN_RECON_ID"));
            sbf.append("</cp_transaction_id>");

            // op_transaction_id: It conveys information related to the session
            // parameters kept at the DDCF
            // application side in case of two-step Debit transactions.
            // op_transaction_id would be blank
            sbf.append("<op_transaction_id>");
            sbf.append("</op_transaction_id>");

            // application: It specifies which application is to be triggered on
            // the ICC product platform. This field must be set to .1.,
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
    public HashMap parseImmediateDebitResponse(String responseStr, String p_interfaceID) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseImmediateDebitResponse: ", "Entered responseStr: " + responseStr);
        HashMap map = new HashMap();
        String result = null;
        int index = 0;
        try {
            index = responseStr.indexOf("<result>");
            if (index > 0) {
                result = responseStr.substring(index + "<result>".length(), responseStr.indexOf("</result>", index));
                map.put("result", result);
            }
            // if result value is 0, get the parameter in to the map
            if (Alcatel452I.RESULT_OK.equals(result)) {
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
                // when we get the xml error code 3 or 9 in that case we will
                // not receive cp_id and transaction id only else we will
                // receive both
                if (!(Alcatel452I.RESULT_ERROR_MALFORMED_REQUEST.equals(result) || Alcatel452I.RESULT_ERROR_XML_PARSE.equals(result))) {
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
     * This Method is responsible to do the multiplication in Bonus amounts
     * before sending it to IN and after getting it.
     * 
     * @param p_map
     *            HashMap
     * @param p_multiFactor
     *            String
     * @param p_amtStr
     *            String
     * @throws Exception
     * @return String
     */
    private String getINAmtValueStr(HashMap p_map, String p_multiFactor, String p_amtStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getINAmtValueStr: ", "Entered p_multiFactor: " + p_multiFactor + " p_amtStr: " + p_amtStr);
        double inAmount = 0;
        double reqAmtDouble = 0;
        String amtStr = null;
        String roundUnitValue = null;
        String interfaceID = null;
        try {
            interfaceID = (String) p_map.get("INTERFACE_ID");
            // Get the double value from the p_amtStr.
            reqAmtDouble = Double.parseDouble(p_amtStr);
            inAmount = reqAmtDouble / Double.parseDouble(p_multiFactor);
            // Check whether unit amount should be rounded or not e.g. if round
            // flag value is Y, round the amount, else put it as it is.
            roundUnitValue = (String) FileCache.getValue(interfaceID, "ROUND_FLAG_FOR_UNIT").trim();
            if (InterfaceUtil.isNullString(roundUnitValue)) {
                roundUnitValue = "N";
                _log.error("getINAmtValueStr: ", "Value of ROUND_FLAG_FOR_UNIT is not defined in the INFile, hence taking the default value N. ");
            }
            amtStr = String.valueOf(Math.round(inAmount));
            // If roung flag for unit is N, then append .0 with the amount.
            if ("N".equals(roundUnitValue))
                amtStr = amtStr + ".0";
        } catch (Exception e) {
            _log.error("getINAmtValueStr: ", "Exception e: " + e.getMessage());
            throw e;
        }
        return amtStr;
    }

    /**
     * This Method will parse the response received from the IN to get the
     * bundles information.
     * It will get the bundle names define at the IN and set it in to the
     * response map.
     * 
     * @param p_requestStr
     *            String
     * @param p_requestMap
     *            HashMap
     * @throws Exception
     * @return HashMap
     */
    private HashMap setDefinedBundlesInToMap(String p_requestStr, HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("setDefinedBundlesInToMap: ", "Entered ");
        int index = 0;
        int noOfBundles = 0;
        HashMap requestMap = p_requestMap;
        String bdl_name = null;
        String bdlNameStr = null;
        try {
            if (!BTSLUtil.isNullString(p_requestStr)) {
                // Check whether bundle is present is string or not, if present
                // then split it.
                index = p_requestStr.indexOf("</bundle>");
                if (index != -1) {
                    String[] bundleArray = p_requestStr.split("</bundle>");
                    noOfBundles = bundleArray.length;
                    for (int i = 0; i < noOfBundles; i++) {
                        // get the bdl_name if present in the bucket.
                        index = bundleArray[i].indexOf("<bdl_name>");
                        if (index != -1) {
                            bdl_name = bundleArray[i].substring(index + "<bdl_name>".length(), bundleArray[i].indexOf("</bdl_name>", index));
                            if (!InterfaceUtil.isNullString(bdlNameStr))
                                bdlNameStr = bdlNameStr + "," + bdl_name;
                            else
                                bdlNameStr = bdl_name;
                        }
                    }
                }
                requestMap.put("defined_bundles", bdlNameStr);
            }
            return requestMap;
        } catch (Exception e) {
            _log.error("setDefinedBundlesInToMap: ", "Exception e: " + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("setDefinedBundlesInToMap: ", "Exiting Defined bundles at IN: " + (String) requestMap.get("defined_bundles"));
        }
    }

    /**
     * This Method is responsible to do the parsing of buckets to get bundle
     * names defined at IN.
     * 
     * @param p_bundleName
     *            String
     * @param p_requestMap
     *            HashMap
     * @throws Exception
     * @return void
     */
    private boolean isBundleDefinedAtIN(String p_bundleName, HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("isBundleDefinedAtIN: ", "Entered for bundle name: " + p_bundleName);
        int index = 0;
        boolean isBundleDefined = false;
        String definedBundles = null;
        definedBundles = (String) p_requestMap.get("BUNDLE_NAMES");
        try {
            if (!InterfaceUtil.isNullString(definedBundles)) {
                // get the interface id from the request map.
                String interfaceID = (String) p_requestMap.get("INTERFACE_ID");
                // get the seperator of bundle names from the IN File
                String sepValue = FileCache.getValue(interfaceID, "BUNDLE_NAMES_SEPERATOR");
                // get whether single bundle or more than one bundles are
                // defined.
                index = definedBundles.indexOf(sepValue);
                if (index != -1) {
                    String[] bdlNames = definedBundles.split(sepValue);
                    int totalDefinedBdles = bdlNames.length;
                    for (int i = 0; i < totalDefinedBdles; i++) {
                        if (p_bundleName.equals(bdlNames[i])) {
                            _log.debug("isBundleDefinedAtIN: ", "Bundle " + p_bundleName + " is defined at the IN. ");
                            isBundleDefined = true;
                            break;
                        }
                    }
                } else if (p_bundleName.equals(definedBundles))
                    isBundleDefined = true;
            }
            // Check whether the bundle is barred or not from the IN File.
            if (isBundleDefined) {
                String interfaceId = (String) p_requestMap.get("INTERFACE_ID");
                String barredBundlesName = FileCache.getValue(interfaceId, "BARRED_BUNDLE_NAMES");
                if (!InterfaceUtil.isNullString(barredBundlesName)) {
                    // get whether single bundle or more than one bundles are
                    // defined.
                    index = barredBundlesName.indexOf(",");
                    if (index != -1) {
                        String[] barredBdlNames = barredBundlesName.split(",");
                        int totalBarredBdles = barredBdlNames.length;
                        for (int i = 0; i < totalBarredBdles; i++) {
                            if (p_bundleName.equals(barredBdlNames[i])) {
                                _log.debug("isBundleDefinedAtIN: ", "Bundle " + p_bundleName + " is barred in the INFile. ");
                                isBundleDefined = false;
                                break;
                            }
                        }
                    } else if (p_bundleName.equals(barredBundlesName))
                        isBundleDefined = false;
                }
            }
            return isBundleDefined;
        } catch (Exception e) {
            _log.error("isBundleDefinedAtIN: ", "Exception e: " + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("isBundleDefinedAtIN: ", "Exiting: Is " + p_bundleName + " sent to the IN: " + isBundleDefined);
        }
    }
}
