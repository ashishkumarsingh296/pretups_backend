package com.inter.alcateloca;

/**
 * @(#)AlcatelOCARequestResponseFormatter.java
 *                                             Copyright(c) 2009, Comviva
 *                                             Technologies Ltd.
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
import java.util.Arrays;
import java.util.HashMap;
import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.cardgroup.businesslogic.BonusBundleCache;
import com.btsl.pretups.cardgroup.businesslogic.BonusBundleDetailVO;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.util.BTSLUtil;

public class AlcatelOCARequestResponseFormatter {
    // Get the logger object, which is used to write different types of logs.
    public static Log _log = LogFactory.getLog("AlcatelOCARequestResponseFormatter".getClass().getName());
    private String _cardGrp = null;
    private boolean _explicitRecharge = false;
    private boolean _combinedRecharge = false;
    private boolean _implicitRecharge = false;
    private String _selectorBundleId = null;
    private String _interfaceId = null;
    private String _serviceType = null;
    private String _userType = null;

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
            _log.debug("generateRequest: ", "Entered p_action:" + p_action + " p_map: " + p_map);
        String str = null;
        try {
            p_map.put("action", String.valueOf(p_action));
            _serviceType = (String) p_map.get("REQ_SERVICE");
            _userType = (String) p_map.get("USER_TYPE");
            _selectorBundleId = (String) p_map.get("SELECTOR_BUNDLE_ID");
            if (!InterfaceUtil.isNullString(_selectorBundleId))
                _selectorBundleId = _selectorBundleId.trim();
            _interfaceId = ((String) p_map.get("INTERFACE_ID")).trim();
            switch (p_action) {
            case AlcatelOCAI.ACTION_GET_ACCOUNT_INFO: {
                str = generateGetAccountInfoRequest(p_map);
                break;
            }
            case AlcatelOCAI.ACTION_IMMEDIATE_CREDIT: {
                if ("PRC".equals(_serviceType))
                    str = generateCreditAdjustRequest(p_map);
                else
                    str = generateImmediateCreditRequest(p_map);
                break;
            }
            case AlcatelOCAI.ACTION_IMMEDIATE_DEBIT: {
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
            case AlcatelOCAI.ACTION_GET_ACCOUNT_INFO: {
                map = parseGetAccountInfoResponse(p_responseStr, p_interfaceID);
                break;
            }
            case AlcatelOCAI.ACTION_IMMEDIATE_CREDIT: {
                map = parseImmediateCreditResponse(p_responseStr, p_interfaceID);
                break;
            }
            case AlcatelOCAI.ACTION_IMMEDIATE_DEBIT: {
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
            sbf.append(getINReconTxnID(map));
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

            // TODO msisdn will changed to login
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

            if (AlcatelOCAI.RESULT_OK.equals(result)) {
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
                setBundlesRcvdFromIN(p_responseStr, responseMap);

                // if selector value is not 1, set selector balance in the map
                // in credit_balance
                if (!"1".equals(_selectorBundleId)) {

                }
            }// end of if- result=OK
            else {
                // Note: when we get the xml error code 3 or 9 in that case we
                // do not receive cp_id and transaction id only else we will
                if (!(AlcatelOCAI.RESULT_ERROR_MALFORMED_REQUEST.equals(result)) || !(AlcatelOCAI.RESULT_ERROR_XML_PARSE.equals(result))) {
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
            _log.debug("generateCreditAdjustRequest", "Entered p_map:" + p_map);
        String requestStr = null;
        StringBuffer sbf = null;
        String interfaceID = null;
        String amtStr = null;
        String validity = null;
        String grace = null;
        BonusBundleDetailVO bonusBundleDetailVO = null;
        String selectorBundlename = null;

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
            sbf.append("<op_transaction_id></op_transaction_id>");
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
            // Get the transaction description from the INFile.
            String txnDesc = (String) FileCache.getValue((String) p_map.get("INTERFACE_ID"), _serviceType + "_TXN_DESC");
            if (!InterfaceUtil.isNullString(txnDesc))
                sbf.append("<transaction_description>" + txnDesc + "</transaction_description>");
            sbf.append("<transaction_price>");
            sbf.append(p_map.get("transfer_amount"));
            sbf.append("</transaction_price>");
            sbf.append("<credit_params main_activity='0' main_inactivity='0'>");
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
     * This method is responsible to generate the xml string for the credit
     * request.
     * 
     * @param HashMap
     *            map
     * @return String requestStr
     * @throws Exception
     */
    private String generateImmediateCreditRequest(HashMap p_map) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateImmediateCreditRequest", "Entered p_map:" + p_map);
        String requestStr = null;
        StringBuffer sbf = null;
        String interfaceID = null;
        String amtStr = null;
        BonusBundleDetailVO bonusBundleDetailVO = null;
        String selectorBundlename = null;

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
            // Value of transaction_currency will be getting from requestMap
            // using key as transaction_currency.
            sbf.append("<transaction_currency>");
            sbf.append(p_map.get("transaction_currency"));
            sbf.append("</transaction_currency>");
            // Get the transaction description from the INFile.
            String txnDesc = (String) FileCache.getValue((String) p_map.get("INTERFACE_ID"), _serviceType + "_TXN_DESC");
            if (!InterfaceUtil.isNullString(txnDesc))
                sbf.append("<transaction_description>" + txnDesc + "</transaction_description>");

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
                // If selector bundle id is 1 i.e. Main recharge, then prepare
                // the request string.
                if (_selectorBundleId.equals("1")) {
                    sbf.append("<transaction_price>");
                    sbf.append(p_map.get("transfer_amount"));
                    sbf.append("</transaction_price>");
                    sbf.append("<credit_params main_activity='" + validity + "'");
                    sbf.append(" main_inactivity='" + grace + "'>");
                } else {
                    if (!InterfaceUtil.isNullString((String) p_map.get("BONUS_BUNDLE_IDS"))) {
                        Object[] ambList = ((String) p_map.get("BONUS_BUNDLE_IDS")).split("\\|");
                        if (_explicitRecharge && (!(Arrays.asList(ambList).contains("1"))))
                            sbf.append("<credit_params>");
                    } else
                        sbf.append("<credit_params>");
                }
                // Prepare the request string for combined recharge.
                if (_combinedRecharge) {
                    if ("1".equals(_selectorBundleId))
                        sbf.append("</credit_params>");
                    else {
                        if (!InterfaceUtil.isNullString((String) p_map.get("BONUS_BUNDLE_IDS"))) {
                            Object[] ambList = ((String) p_map.get("BONUS_BUNDLE_IDS")).split("\\|");
                            if (Arrays.asList(ambList).contains("1")) {
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
                    // String
                    // selectorBundlename=FileCache.getValue(interfaceID,_selectorBundleId).trim();
                    // Get the bundle info from the BonusBundleCache
                    bonusBundleDetailVO = (BonusBundleDetailVO) BonusBundleCache.getBonusBundlesMap().get(_selectorBundleId);
                    if (_log.isDebugEnabled())
                        _log.debug("getBundleRequestString", "bonusBundleDetailVO=" + bonusBundleDetailVO);
                    if (bonusBundleDetailVO != null)
                        selectorBundlename = bonusBundleDetailVO.getBundleCode().trim();

                    // If other than Main recharge, then prepare the request
                    // string accordingly.
                    if (!"1".equals(_selectorBundleId)) {
                        if (!InterfaceUtil.isNullString((String) p_map.get("BONUS_BUNDLE_IDS"))) {
                            Object[] ambList = ((String) p_map.get("BONUS_BUNDLE_IDS")).split("\\|");

                            if (!(Arrays.asList(ambList).contains("1"))) {
                                if (!InterfaceUtil.isNullString(selectorBundlename) && isBundleAllowed(selectorBundlename, p_map) && (Long.parseLong(validity) >= 0 || Long.parseLong(amtStr) > 0)) {
                                    sbf.append("<crd_param bdl_name=" + "\"" + selectorBundlename + "\"");
                                    sbf.append(" bdl_validity=" + "\"" + validity + "\"" + ">" + amtStr + "</crd_param>");
                                }
                                sbf.append(getBundleRequestString(p_map));
                            } else {
                                sbf.append(getBundleRequestString(p_map));
                                if (!InterfaceUtil.isNullString(selectorBundlename) && isBundleAllowed(selectorBundlename, p_map) && (Long.parseLong(validity) >= 0 || Long.parseLong(amtStr) > 0)) {
                                    sbf.append("<crd_param bdl_name=" + "\"" + selectorBundlename + "\"");
                                    sbf.append(" bdl_validity=" + "\"" + validity + "\"" + ">" + amtStr + "</crd_param>");
                                }
                            }
                        } else {
                            if (!InterfaceUtil.isNullString(selectorBundlename) && isBundleAllowed(selectorBundlename, p_map) && (Long.parseLong(validity) >= 0 || Long.parseLong(amtStr) > 0)) {
                                sbf.append("<crd_param bdl_name=" + "\"" + selectorBundlename + "\"");
                                sbf.append(" bdl_validity=" + "\"" + validity + "\"" + ">" + amtStr + "</crd_param>");
                            }
                        }
                    } else
                        sbf.append(getBundleRequestString(p_map));

                    // Close the credit parameter tag in the request string.
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

    /**
     * This method is responsible to generate the xml string for the defined
     * bundles.
     * 
     * @param HashMap
     *            p_map
     * @return String bundleReqStr
     * @throws Exception
     */
    private String getBundleRequestString(HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getBundleRequestString", "Entered");
        String bundleReqStr = "";
        StringBuffer sbfBundle = null;
        StringBuffer sbfBundleMain = null;
        String[] bundleIds = null;
        String[] bundleValidities = null;
        String[] bundleValues = null;
        String[] bundleTypes = null;
        int bonusBundleCount = 0;
        String bundleValue = "0";
        String bundleValidity = "0";
        BonusBundleDetailVO bonusBundleDetailVO = null;
        String bundleName = null;
        try {
            if (!InterfaceUtil.isNullString((String) p_map.get("BONUS_BUNDLE_IDS"))) {
                bundleIds = ((String) p_map.get("BONUS_BUNDLE_IDS")).split("\\|");
                bundleValidities = ((String) p_map.get("BONUS_BUNDLE_VALIDITIES")).split("\\|");
                bundleValues = ((String) p_map.get("BONUS_BUNDLE_VALUES")).split("\\|");
                bundleTypes = ((String) p_map.get("BONUS_BUNDLE_TYPES")).split("\\|");
                bonusBundleCount = bundleIds.length;

                sbfBundleMain = new StringBuffer(1024);
                sbfBundle = new StringBuffer(1024);
                for (int i = 0; i < bonusBundleCount; i++) {
                    bonusBundleDetailVO = (BonusBundleDetailVO) BonusBundleCache.getBonusBundlesMap().get(bundleIds[i]);
                    if (_log.isDebugEnabled())
                        _log.debug("getBundleRequestString", "bonusBundleDetailVO=" + bonusBundleDetailVO);

                    if (bonusBundleDetailVO == null || (!bundleIds[i].equalsIgnoreCase("1") && _combinedRecharge))
                        continue;

                    bundleName = bonusBundleDetailVO.getBundleCode();
                    if (_log.isDebugEnabled())
                        _log.debug("getBundleRequestString", "bundleIds[" + i + "]=" + bundleIds[i] + ", bundleName=" + bundleName);

                    bundleValue = bundleValues[i];
                    bundleValidity = bundleValidities[i];
                    double bundleValueDbl = Double.parseDouble(bundleValue);
                    double inbundleValueDbl = 0;
                    long inbundleValueLng = 0;
                    if (bundleValueDbl > 0 || Long.parseLong(bundleValidity) > 0) {
                        if ("AMT".equals(bundleTypes[i])) {
                            inbundleValueDbl = InterfaceUtil.getINAmountFromSystemAmountToIN(bundleValueDbl, Double.parseDouble(FileCache.getValue(_interfaceId, "AMT_MULT_FACTOR")));
                            inbundleValueLng = Math.round(inbundleValueDbl);
                        } else if ("UNIT".equals(bundleTypes[i])) {
                            inbundleValueDbl = InterfaceUtil.getINAmountFromSystemAmountToIN(bundleValueDbl, Double.parseDouble(FileCache.getValue(_interfaceId, "UNIT_MULT_FACTOR")));
                            inbundleValueLng = Math.round(inbundleValueDbl);
                        }

                        if (!"1".equals(bundleIds[i]) && isBundleAllowed(bundleName, p_map)) {
                            sbfBundle.append("<crd_param bdl_name=" + "\"" + bundleName + "\"");
                            sbfBundle.append(" bdl_validity=" + "\"" + bundleValidity + "\">");
                            sbfBundle.append(String.valueOf(inbundleValueLng) + "</crd_param>");
                        } else if ("1".equals(bundleIds[i])) {
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
            _log.error("OCIZteRequestResponseFormatter[getBundleRequestString]", "Exception e::" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getBundleRequestString", "Exiting  bundleReqStr=" + bundleReqStr);
        }
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
            if (AlcatelOCAI.RESULT_OK.equals(result)) {
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
                if (!(AlcatelOCAI.RESULT_ERROR_MALFORMED_REQUEST.equals(result) || AlcatelOCAI.RESULT_ERROR_XML_PARSE.equals(result))) {
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
            sbf.append(getINReconTxnID(p_map));
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
            // Get the transaction description from the INFile.
            String txnDesc = (String) FileCache.getValue((String) p_map.get("INTERFACE_ID"), _serviceType + "_TXN_DESC");
            if (!InterfaceUtil.isNullString(txnDesc))
                sbf.append("<transaction_description>" + txnDesc + "</transaction_description>");
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
            if (AlcatelOCAI.RESULT_OK.equals(result)) {
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
                if (!(AlcatelOCAI.RESULT_ERROR_MALFORMED_REQUEST.equals(result) || AlcatelOCAI.RESULT_ERROR_XML_PARSE.equals(result))) {
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
            if ("Y".equals(roundUnitValue))
                amtStr = String.valueOf(Math.round(inAmount));
            else
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
     * @param p_responseStr
     *            String
     * @param p_requestMap
     *            HashMap
     * @throws Exception
     * @return HashMap
     */
    private void setBundlesRcvdFromIN(String p_responseStr, HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("setBundlesRcvdFromIN", "Entered ");
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
                        System.out.println("bdl_name: " + bdl_name);
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
            _log.error("setBundlesRcvdFromIN", "Exception e: " + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("setBundlesRcvdFromIN", "Exiting Defined bundles at IN: " + (String) p_requestMap.get("received_bundles"));
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
     * @throws BTSLBaseException
     *             ,Exception
     * @return void
     */
    private boolean isBundleAllowed(String p_bundleName, HashMap p_requestMap) throws BTSLBaseException, Exception {

        if (_log.isDebugEnabled())
            _log.debug("isBundleAllowed", "Entered for bundle name: " + p_bundleName);
        boolean isAllowed = true;
        String interfaceID = null;
        String splitParameter = null;
        try {
            // Get the interface id from the request map.
            interfaceID = (String) p_requestMap.get("INTERFACE_ID");
            splitParameter = (String) FileCache.getValue(interfaceID, "BUNDLE_SPLIT_PARAMETER").trim();
            if (InterfaceUtil.isNullString(splitParameter)) {
                splitParameter = ",";
                _log.error("isBundleAllowed", "BUNDLE_SPLIT_PARAMETER parameter is not defined in the INFile.");
            }
            // Get the bundles(received in Get Acc Info response from the IN)
            // from the request map.
            String bundleFromIN = (String) p_requestMap.get("IN_RESP_BUNDLE_CODES");
            if (!InterfaceUtil.isNullString(bundleFromIN)) {
                Object[] bundleList = bundleFromIN.split(splitParameter);
                if (!(Arrays.asList(bundleList).contains(p_bundleName))) {
                    isAllowed = false;
                    _log.error("isBundleAllowed", "Bundle " + p_bundleName + " is not defined for the subscriber on the Alcatel IN.");
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_RECHARGE_BUNDLE_NOT_DEFINED);
                }
            }
            return isAllowed;
        } catch (BTSLBaseException be) {
            _log.error("isBundleAllowed", "Exception e: " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("isBundleAllowed", "Exception e: " + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("isBundleAllowed", "Exiting: Is " + p_bundleName + " is allow on the IN: " + isAllowed);
        }
    }
}
