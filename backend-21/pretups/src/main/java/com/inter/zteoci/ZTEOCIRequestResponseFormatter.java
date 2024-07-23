package com.inter.zteoci;

/**
 * @(#)ZTEOCIRequestResponseFormatter.java
 *                                         Copyright(c) 2009, Comviva
 *                                         Technologies Ltd.
 *                                         All Rights Reserved
 *                                         ------------------------------------
 *                                         --
 *                                         ------------------------------------
 *                                         -----------------------
 *                                         Author Date History
 *                                         ------------------------------------
 *                                         --
 *                                         ------------------------------------
 *                                         -----------------------
 *                                         Vinay Kumar Singh July 09, 2009
 *                                         Initial Creation
 *                                         ------------------------------------
 *                                         --
 *                                         ------------------------------------
 *                                         ----------------------
 *                                         This class can be used as a parser
 *                                         class for both request(before sending
 *                                         the request to IN) and
 *                                         response(after getting the response
 *                                         from the IN).
 */
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.util.Constants;

public class ZTEOCIRequestResponseFormatter {
    private static Log _log = LogFactory.getLog(ZTEOCIRequestResponseFormatter.class.getName());
    private static int _counter = 0;
    private String _cardGrp = null;
    private boolean _explicitRecharge = false;
    private boolean _combinedRecharge = false;
    private boolean _implicitRecharge = false;
    private String _selectorBundleId = null;
    private String _interfaceId = null;
    private String _serviceType = null;

    /**
     * Constructor
     */
    public ZTEOCIRequestResponseFormatter() {
        super();
        // TODO Auto-generated constructor stub
    }

    protected String generateRequest(int action, HashMap<String, String> p_requestMap) throws Exception {
        _log.debug("generateRequest", "Entered with action=" + action);

        String requestStr = null;
        p_requestMap.put("action", String.valueOf(action));
        _selectorBundleId = p_requestMap.get("SELECTOR_BUNDLE_ID");

        if (!InterfaceUtil.isNullString(_selectorBundleId))
            _selectorBundleId = _selectorBundleId.trim();

        _interfaceId = (p_requestMap.get("INTERFACE_ID")).trim();
        _serviceType = p_requestMap.get("REQ_SERVICE");

        switch (action) {
        case ZTEOCII.ACTION_ACCOUNT_INFO: {
            requestStr = generateGetAccountInfoRequest(p_requestMap);
            break;
        }
        case ZTEOCII.ACTION_RECHARGE_CREDIT: {
            if ("PRC".equals(_serviceType))
                requestStr = generateCreditAdjustRequest(p_requestMap);
            else
                requestStr = generateRechargeCreditRequest(p_requestMap);
            break;
        }
        case ZTEOCII.ACTION_IMMEDIATE_DEBIT: {
            requestStr = generateImmediateDebitRequest(p_requestMap);
            break;
        }
        }
        _log.debug("generateRequest", "Exited for action=" + action);

        return requestStr;
    }

    /**
     * This method parse the response from XML String into HashMap
     * 
     * @param action
     *            int
     * @param responseStr
     *            String
     * @return map HashMap<String,String>
     */
    protected HashMap<String, String> parseResponse(int action, String responseStr) throws Exception {
        _log.debug("parseResponse", "Entered for action=" + action);

        HashMap<String, String> responseMap = null;
        switch (action) {
        case ZTEOCII.ACTION_ACCOUNT_INFO: {
            responseMap = parseGetAccountInfoResponse(responseStr);
            break;
        }
        case ZTEOCII.ACTION_RECHARGE_CREDIT: {
            if ("PRC".equals(_serviceType))
                responseMap = parseCreditAdjustResponse(responseStr);
            else
                responseMap = parseRechargeCreditResponse(responseStr);
            break;
        }

        case ZTEOCII.ACTION_IMMEDIATE_DEBIT: {
            responseMap = parseImmediateDebitResponse(responseStr);
            break;
        }
        }
        _log.debug("parseResponse", "Exiting for action=" + action);

        return responseMap;
    }

    /**
     * This Method generate account information request
     * 
     * @param map
     *            HashMap
     * @throws Exception
     * @return requestStr
     */
    private String generateGetAccountInfoRequest(HashMap<String, String> p_map) throws Exception {
        _log.debug("generateGetAccountInfoRequest", "Entered p_map=" + p_map);

        String requestStr = null;
        StringBuffer sbf = null;
        try {
            getINRequestID(p_map);
            sbf = new StringBuffer(1028);
            sbf.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            sbf.append("<zsmart>");
            sbf.append("<Data>");
            sbf.append("<header>");
            sbf.append("<ACTION_ID>QueryProfileAndBal</ACTION_ID>");
            sbf.append("<REQUEST_ID>" + p_map.get("IN_REQ_ID") + "</REQUEST_ID>");
            sbf.append("</header>");
            sbf.append("<body>");
            sbf.append("<MSISDN>" + InterfaceUtil.getFilterMSISDN(p_map.get("INTERFACE_ID"), p_map.get("MSISDN")) + "</MSISDN>");
            sbf.append("<TransactionSN>" + p_map.get("IN_RECON_ID") + "</TransactionSN>");
            sbf.append("<UserPwd></UserPwd>");
            sbf.append("</body>");
            sbf.append("</Data>");
            sbf.append("</zsmart>");
            requestStr = sbf.toString();

            return requestStr;
        } catch (Exception e) {
            _log.error("generateGetAccountInfoRequest", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            _log.debug("generateGetAccountInfoRequest", "Exiting with requestStr=" + requestStr);
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
    public HashMap<String, String> parseGetAccountInfoResponse(String responseStr) throws Exception {
        _log.debug("parseGetAccountInfoResponse", "Entered responseStr=" + responseStr);

        HashMap<String, String> responseMap = null;
        try {
            responseMap = new HashMap<String, String>();
            String _str = "0";
            int index = responseStr.indexOf("<returnCode>");
            String returnCode = responseStr.substring(index + "<returnCode>".length(), responseStr.indexOf("</returnCode>", index));
            responseMap.put("resp_returnCode", returnCode);
            if (_str.equals(returnCode)) {
                index = responseStr.indexOf("<ACTION_ID>");
                if (index != -1) {
                    String actionId = responseStr.substring(index + "<ACTION_ID>".length(), responseStr.indexOf("</ACTION_ID>", index));
                    responseMap.put("resp_action_id", actionId);
                }
                index = responseStr.indexOf("<REQUEST_ID>");
                if (index != -1) {
                    String reqId = responseStr.substring(index + "<REQUEST_ID>".length(), responseStr.indexOf("</REQUEST_ID>", index));
                    responseMap.put("resp_req_id", reqId);
                }
                index = responseStr.indexOf("<MSISDN>");
                if (index != -1) {
                    String msisdn = responseStr.substring(index + "<MSISDN>".length(), responseStr.indexOf("</MSISDN>", index));
                    responseMap.put("resp_msisdn", msisdn);
                }
                index = responseStr.indexOf("<DefLang>");
                if (index != -1) {
                    String defLang = responseStr.substring(index + "<DefLang>".length(), responseStr.indexOf("</DefLang>", index));
                    responseMap.put("resp_defLang", defLang);
                }
                index = responseStr.indexOf("<State>");
                if (index != -1) {
                    String resp_state = responseStr.substring(index + "<State>".length(), responseStr.indexOf("</State>", index));
                    responseMap.put("resp_state", resp_state);
                }
                index = responseStr.indexOf("<StateSet>");
                if (index != -1) {
                    String resp_stateSet = responseStr.substring(index + "<StateSet>".length(), responseStr.indexOf("</StateSet>", index));
                    responseMap.put("resp_stateSet", resp_stateSet);
                }
                index = responseStr.indexOf("<ActiveStopDate>");
                if (index != -1) {
                    String resp_activeStopDate = responseStr.substring(index + "<ActiveStopDate>".length(), responseStr.indexOf("</ActiveStopDate>", index));
                    responseMap.put("resp_activeStopDate", resp_activeStopDate);
                }
                index = responseStr.indexOf("<SuspendStopDate>");
                if (index != -1) {
                    String resp_suspendStopDate = responseStr.substring(index + "<SuspendStopDate>".length(), responseStr.indexOf("</SuspendStopDate>", index));
                    responseMap.put("resp_suspendStopDate", resp_suspendStopDate);
                }
                index = responseStr.indexOf("<DisableStopDate>");
                if (index != -1) {
                    String resp_disableStopDate = responseStr.substring(index + "<DisableStopDate>".length(), responseStr.indexOf("</DisableStopDate>", index));
                    responseMap.put("resp_disableStopDate", resp_disableStopDate);
                }
                index = responseStr.indexOf("<ServiceStopDate>");
                if (index != -1) {
                    String resp_serviceStopDate = responseStr.substring(index + "<ServiceStopDate>".length(), responseStr.indexOf("</ServiceStopDate>", index));
                    responseMap.put("resp_serviceStopDate", resp_serviceStopDate);
                }
                index = responseStr.indexOf("<ServiceClass>");
                if (index != -1) {
                    String resp_serviceClass = responseStr.substring(index + "<ServiceClass>".length(), responseStr.indexOf("</ServiceClass>", index));
                    responseMap.put("resp_serviceClass", resp_serviceClass);
                }
                index = responseStr.indexOf("<TransactionSN>");
                if (index != -1) {
                    String resp_transactionSN = responseStr.substring(index + "<TransactionSN>".length(), responseStr.indexOf("</TransactionSN>", index));
                    responseMap.put("resp_transactionSN", resp_transactionSN);
                }
                // Set the bundle info received from the response in to the
                // response map.
                getBundlesInfoRcvdFromIN(responseStr, responseMap);
            }
            return responseMap;
        } catch (Exception e) {
            _log.error("parseGetAccountInfoResponse", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            _log.debug("parseGetAccountInfoResponse", "Exiting with responseMap=" + responseMap);
        }
    }

    /**
     * This method is to generate re-charge Credit Request
     * 
     * @param p_map
     *            HashMap
     * @return String
     * @throws Exception
     */
    private String generateRechargeCreditRequest(HashMap<String, String> p_map) throws Exception {
        _log.debug("generateRechargeCreditRequest", "Entered with p_map=" + p_map);

        String requestStr = null;
        StringBuffer sbf = null;
        String graceDays = "0";
        try {
            // String module = p_map.get("MODULE");
            String reqService = p_map.get("REQ_SERVICE");
            // Grace days
            graceDays = p_map.get("GRACE_DAYS");
            if (InterfaceUtil.isNullString(graceDays))
                graceDays = "0";
            _cardGrp = p_map.get("CARD_GROUP");
            String combined = p_map.get("COMBINED_RECHARGE");
            String implicit = p_map.get("IMPLICIT_RECHARGE");
            if ("N".equals(implicit) && "N".equals(combined))
                _explicitRecharge = true;
            else if ("Y".equals(implicit) && "Y".equals(combined))
                _combinedRecharge = true;
            else if ("Y".equals(implicit) && "N".equals(combined))
                _implicitRecharge = true;

            // Transfer amount of selector.
            double transAmtDbl = 0;
            if (!InterfaceUtil.isNullString(p_map.get("transfer_amount"))) {
                transAmtDbl = Double.parseDouble(p_map.get("transfer_amount"));
                transAmtDbl = 0 - transAmtDbl;
            }
            // Validity days of selector.
            String validity = p_map.get("VALIDITY_DAYS");
            // If bonus validity is separate from the main validity, then
            // subtract it from the main validity.
            String addMainBnsVal = FileCache.getValue(_interfaceId, "ADD_MAIN_AND_BUNUS_VALIDITY").trim();
            String bonusValidity = p_map.get("BONUS_VALIDITY_DAYS");
            if ("N".equals(addMainBnsVal) && !InterfaceUtil.isNullString(bonusValidity.trim()) && !InterfaceUtil.isNullString(validity)) {
                long mainVal = Long.parseLong(validity.trim());
                long bonusVal = Long.parseLong(bonusValidity);
                if (bonusVal > 0)
                    validity = String.valueOf(mainVal - bonusVal);
            } else if (InterfaceUtil.isNullString(validity))
                validity = "0";

            getINRequestID(p_map);
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            sbf.append("<zsmart>");
            sbf.append("<Data>");
            sbf.append("<header>");
            sbf.append("<ACTION_ID>ModifyAllBalReturnAllBal</ACTION_ID>");
            sbf.append("<REQUEST_ID>" + p_map.get("IN_REQ_ID") + "</REQUEST_ID>");
            sbf.append("</header>");
            sbf.append("<body>");
            sbf.append("<MSISDN>" + InterfaceUtil.getFilterMSISDN(p_map.get("INTERFACE_ID"), p_map.get("MSISDN")) + "</MSISDN>");

            // String txnDesc = FileCache.getValue(
            // p_map.get("INTERFACE_ID"),module+"_TXN_DESC");
            String txnDesc = FileCache.getValue(p_map.get("INTERFACE_ID"), reqService + "_TXN_DESC");
            if (!InterfaceUtil.isNullString(txnDesc))
                sbf.append("<TransactionDescription>" + txnDesc + "</TransactionDescription>");

            sbf.append("<AccountCode></AccountCode>");
            sbf.append("<TransactionSN>" + p_map.get("IN_RECON_ID") + "</TransactionSN>");

            if (_implicitRecharge || _combinedRecharge)
                sbf.append("<RechargingID>" + _cardGrp + "</RechargingID>");
            else
                sbf.append("<RechargingID></RechargingID>");

            sbf.append("<SuspendAddDays>" + graceDays + "</SuspendAddDays>");

            _log.debug("generateRechargeCreditRequest", "_explicitRecharge=" + _explicitRecharge + ", _combinedRecharge=" + _combinedRecharge + ", _implicitRecharge=" + _implicitRecharge);

            // if(_explicitRecharge || _combinedRecharge)
            if (!_implicitRecharge) {
                String cardGrpSelector = p_map.get("CARD_GROUP_SELECTOR");
                // If combined re-charge, and selector value is other than 1
                if (_combinedRecharge && !"1".equals(cardGrpSelector) && !InterfaceUtil.isNullString(p_map.get("BONUS_BUNDLE_IDS"))) {
                    Object[] ambList = (p_map.get("BONUS_BUNDLE_IDS")).split("\\|");
                    if (Arrays.asList(ambList).contains("1"))
                        sbf.append(getBundleRequestString(p_map));
                } else {
                    sbf.append("<BalInputDtoList>");
                    sbf.append("<AcctResCode>" + FileCache.getValue(_interfaceId, _selectorBundleId) + "</AcctResCode>");
                    sbf.append("<AcctResName></AcctResName>");
                    sbf.append("<Balance></Balance>");

                    // If transfer amount is not zero, then set it in to the
                    // request string.
                    if (transAmtDbl != 0)
                        sbf.append("<AddBalance>" + String.valueOf(Math.round(transAmtDbl)) + "</AddBalance>");
                    else
                        sbf.append("<AddBalance></AddBalance>");

                    // If validity period is not null, then set it in to the
                    // request string.
                    if (!InterfaceUtil.isNullString(validity))
                        sbf.append("<AddDays>" + validity + "</AddDays>");
                    else
                        sbf.append("<AddDays></AddDays>");

                    sbf.append("<ExpDate></ExpDate>");
                    sbf.append("</BalInputDtoList>");
                }
            }

            // If explicit re-charge, then set the other bundles in to the
            // request string.
            if (_explicitRecharge)
                sbf.append(getBundleRequestString(p_map));

            sbf.append("</body>");
            sbf.append("</Data>");
            sbf.append("</zsmart>");
            requestStr = sbf.toString();
            _log.debug("generateRechargeCreditRequest", "Got the XML String as " + requestStr);

            return requestStr;
        } catch (Exception e) {
            _log.error("generateRechargeCreditRequest", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            _log.debug("generateRechargeCreditRequest", "Exited requestStr=" + requestStr);
        }
    }

    /**
     * This Method parse re-charge credit response
     * 
     * @param responseStr
     *            String
     * @throws Exception
     * @return map
     */
    private HashMap<String, String> parseRechargeCreditResponse(String responseStr) throws Exception {
        _log.debug("parseRechargeCreditResponse", "Entered responseStr: " + responseStr);

        HashMap<String, String> responseMap = null;
        try {
            responseMap = new HashMap<String, String>();
            String _str = "0";
            int index = responseStr.indexOf("<returnCode>");
            String returnCode = responseStr.substring(index + "<returnCode>".length(), responseStr.indexOf("</returnCode>", index));
            responseMap.put("resp_returnCode", returnCode);

            if (_str.equals((returnCode))) {
                index = responseStr.indexOf("<ACTION_ID>");
                if (index != -1) {
                    String actionId = responseStr.substring(index + "<ACTION_ID>".length(), responseStr.indexOf("</ACTION_ID>", index));
                    responseMap.put("resp_action_id", actionId);
                }

                index = responseStr.indexOf("<REQUEST_ID>");
                if (index != -1) {
                    String reqId = responseStr.substring(index + "<REQUEST_ID>".length(), responseStr.indexOf("</REQUEST_ID>", index));
                    responseMap.put("resp_req_id", reqId);
                }

                index = responseStr.indexOf("<TransactionSN>");
                if (index != -1) {
                    String transactionSN = responseStr.substring(index + "<TransactionSN>".length(), responseStr.indexOf("</TransactionSN>", index));
                    responseMap.put("resp_transactionSN", transactionSN);
                }

                // Set the bundle info received from the response in to the
                // response map.
                getBundlesInfoRcvdFromIN4Credit(responseStr, responseMap);
            }
            return responseMap;
        } catch (Exception e) {
            _log.error("parseRechargeCreditResponse", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            _log.debug("parseRechargeCreditResponse", "Exiting responseMap=" + responseMap);
        }
    }

    /**
     * @param map
     * @return requestStr String
     * @throws Exception
     */
    private String generateCreditAdjustRequest(HashMap<String, String> p_map) throws Exception {
        _log.debug("generateCreditAdjustRequest", "Entered with p_map=" + p_map);

        String requestStr = null;
        StringBuffer sbf = null;
        String validity = "0";
        String graceDays = "0";
        try {
            // String module = p_map.get("MODULE");
            String reqService = p_map.get("REQ_SERVICE");
            // Grace days
            graceDays = p_map.get("GRACE_DAYS");
            if (InterfaceUtil.isNullString(graceDays))
                graceDays = "0";
            _cardGrp = p_map.get("CARD_GROUP");
            String combined = p_map.get("COMBINED_RECHARGE");
            String implicit = p_map.get("IMPLICIT_RECHARGE");

            if ("N".equals(implicit) && "N".equals(combined))
                _explicitRecharge = true;
            else if ("Y".equals(implicit) && "Y".equals(combined))
                _combinedRecharge = true;
            else if ("Y".equals(implicit) && "N".equals(combined))
                _implicitRecharge = true;

            // Re-charge amount of selector.
            double transAmtDbl = 0;
            if (!InterfaceUtil.isNullString(p_map.get("transfer_amount"))) {
                transAmtDbl = Double.parseDouble(p_map.get("transfer_amount"));
                transAmtDbl = 0 - transAmtDbl;
            }

            // Get the IN Request ID
            getINRequestID(p_map);

            // Prepare the request string.
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            sbf.append("<zsmart>");
            sbf.append("<Data>");
            sbf.append("<header>");
            sbf.append("<ACTION_ID>ModifyAllBalReturnAllBal</ACTION_ID>");
            sbf.append("<REQUEST_ID>" + p_map.get("IN_REQ_ID") + "</REQUEST_ID>");
            sbf.append("</header>");
            sbf.append("<body>");
            sbf.append("<MSISDN>" + InterfaceUtil.getFilterMSISDN(p_map.get("INTERFACE_ID"), p_map.get("MSISDN")) + "</MSISDN>");

            // String txnDesc = FileCache.getValue(
            // p_map.get("INTERFACE_ID"),module+"_TXN_DESC");
            String txnDesc = FileCache.getValue(p_map.get("INTERFACE_ID"), reqService + "_TXN_DESC");
            if (!InterfaceUtil.isNullString(txnDesc))
                sbf.append("<TransactionDescription>" + txnDesc + "</TransactionDescription>");

            sbf.append("<AccountCode></AccountCode>");

            if ("S".equals(p_map.get("USER_TYPE")))
                sbf.append("<SuspendAddDays></SuspendAddDays>");
            else
                sbf.append("<SuspendAddDays>" + graceDays + "</SuspendAddDays>");

            if ("S".equals(p_map.get("USER_TYPE"))) {
                // String creditBkTranID=getZTEINTransactionID(p_map);
                String creditBkTranID = p_map.get("IN_RECON_ID");
                creditBkTranID = creditBkTranID.replace(".S", ".B");
                sbf.append("<TransactionSN>" + creditBkTranID + "</TransactionSN>");
                String senderBundleId = p_map.get("SENDER_BUNDLE_ID");
                sbf.append("<BalInputDtoList>");
                sbf.append("<AcctResCode>" + FileCache.getValue(_interfaceId, senderBundleId) + "</AcctResCode>");
                sbf.append("<AcctResName></AcctResName>");
                sbf.append("<Balance></Balance>");
                sbf.append("<AddBalance>" + String.valueOf(Math.round(transAmtDbl)) + "</AddBalance>");
                sbf.append("<AddDays></AddDays>");
                sbf.append("<ExpDate></ExpDate>");
                sbf.append("</BalInputDtoList>");
            } else {
                sbf.append("<TransactionSN>" + p_map.get("IN_RECON_ID") + "</TransactionSN>");

                if (_implicitRecharge || _combinedRecharge)
                    sbf.append("<RechargingID>" + _cardGrp + "</RechargingID>");
                else
                    sbf.append("<RechargingID></RechargingID>");

                if (_explicitRecharge || _combinedRecharge) {
                    // Validity period of selector.
                    validity = p_map.get("VALIDITY_DAYS");

                    // If bonus validity is separate from the main validity,
                    // then subtract it from the main validity.
                    String addMainBnsVal = FileCache.getValue(_interfaceId, "ADD_MAIN_AND_BUNUS_VALIDITY").trim();
                    String bonusValidity = p_map.get("BONUS_VALIDITY_DAYS");
                    if ("N".equals(addMainBnsVal) && !InterfaceUtil.isNullString(bonusValidity.trim()) && !InterfaceUtil.isNullString(validity)) {
                        long mainVal = Long.parseLong(validity.trim());
                        long bonusVal = Long.parseLong(bonusValidity);
                        if (bonusVal > 0)
                            validity = String.valueOf(mainVal - bonusVal);
                    } else if (InterfaceUtil.isNullString(validity))
                        validity = "0";

                    String cardGrpSelector = p_map.get("CARD_GROUP_SELECTOR");

                    // If combined re-charge, and selector value is other than 1
                    if (_combinedRecharge && !"1".equals(cardGrpSelector) && !InterfaceUtil.isNullString(p_map.get("BONUS_BUNDLE_IDS"))) {
                        Object[] ambList = (p_map.get("BONUS_BUNDLE_IDS")).split("\\|");
                        if (Arrays.asList(ambList).contains("1"))
                            sbf.append(getBundleRequestString(p_map));
                    } else {
                        sbf.append("<BalInputDtoList>");
                        sbf.append("<AcctResCode>" + FileCache.getValue(_interfaceId, _selectorBundleId) + "</AcctResCode>");
                        sbf.append("<AcctResName></AcctResName>");
                        sbf.append("<Balance></Balance>");

                        // If transfer amount is not zero, then set it in to the
                        // request string.
                        if (transAmtDbl != 0)
                            sbf.append("<AddBalance>" + String.valueOf(Math.round(transAmtDbl)) + "</AddBalance>");
                        else
                            sbf.append("<AddBalance></AddBalance>");

                        // If validity period is not null, then set it in to the
                        // request string.
                        if (!InterfaceUtil.isNullString(validity))
                            sbf.append("<AddDays>" + validity + "</AddDays>");
                        else
                            sbf.append("<AddDays></AddDays>");

                        sbf.append("<ExpDate></ExpDate>");
                        sbf.append("</BalInputDtoList>");
                    }
                }

                if (_explicitRecharge)
                    sbf.append(getBundleRequestString(p_map));
            }

            sbf.append("</body>");
            sbf.append("</Data>");
            sbf.append("</zsmart>");
            requestStr = sbf.toString();

            return requestStr;
        } catch (Exception e) {
            _log.error("generateCreditAdjustRequest", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            _log.debug("generateCreditAdjustRequest", "Exited requestStr=" + requestStr);
        }
    }

    /**
     * This Method parse re-charge credit response
     * 
     * @param responseStr
     *            String
     * @throws Exception
     * @return map
     */
    private HashMap<String, String> parseCreditAdjustResponse(String responseStr) throws Exception {

        _log.debug("parseCreditAdjustResponse", "Entered responseStr: " + responseStr);

        HashMap<String, String> map = null;
        try {
            map = new HashMap<String, String>();
            String _str = "0";
            int index = responseStr.indexOf("<returnCode>");
            String returnCode = responseStr.substring(index + "<returnCode>".length(), responseStr.indexOf("</returnCode>", index));
            map.put("resp_returnCode", returnCode);

            if (_str.equals((returnCode))) {
                index = responseStr.indexOf("<ACTION_ID>");
                if (index != -1) {
                    String actionId = responseStr.substring(index + "<ACTION_ID>".length(), responseStr.indexOf("</ACTION_ID>", index));
                    map.put("resp_action_id", actionId);
                }

                index = responseStr.indexOf("<REQUEST_ID>");
                if (index != -1) {
                    String reqId = responseStr.substring(index + "<REQUEST_ID>".length(), responseStr.indexOf("</REQUEST_ID>", index));
                    map.put("resp_req_id", reqId);
                }

                index = responseStr.indexOf("<TransactionSN>");
                if (index != -1) {
                    String transactionSN = responseStr.substring(index + "<TransactionSN>".length(), responseStr.indexOf("</TransactionSN>", index));
                    map.put("resp_transactionSN", transactionSN);
                }
                getBundlesInfoRcvdFromIN4Credit(responseStr, map);
            }
            return map;
        } catch (Exception e) {
            _log.error("parseCreditAdjustResponse", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            _log.debug("parseCreditAdjustResponse", "Exiting map: " + map);
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
    private String generateImmediateDebitRequest(HashMap<String, String> p_map) throws Exception {
        _log.debug("generateImmediateDebitRequest", "Entered" + p_map);

        String requestStr = null;
        StringBuffer sbf = null;
        String senderBundleId = null;
        try {
            // String module = p_map.get("MODULE");
            String reqService = p_map.get("REQ_SERVICE");

            String transferAmt = p_map.get("transfer_amount");
            senderBundleId = p_map.get("SENDER_BUNDLE_ID");
            getINRequestID(p_map);
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            sbf.append("<zsmart>");
            sbf.append("<Data>");
            sbf.append("<header>");
            sbf.append("<ACTION_ID>ModifyAllBalReturnAllBal</ACTION_ID>");
            sbf.append("<REQUEST_ID>" + p_map.get("IN_REQ_ID") + "</REQUEST_ID>");
            sbf.append("</header>");
            sbf.append("<body>");
            sbf.append("<MSISDN>" + InterfaceUtil.getFilterMSISDN(p_map.get("INTERFACE_ID"), p_map.get("MSISDN")) + "</MSISDN>");

            // String txnDesc = FileCache.getValue(
            // p_map.get("INTERFACE_ID"),module+"_TXN_DESC");
            String txnDesc = FileCache.getValue(p_map.get("INTERFACE_ID"), reqService + "_TXN_DESC");
            if (!InterfaceUtil.isNullString(txnDesc))
                sbf.append("<TransactionDescription>" + txnDesc + "</TransactionDescription>");

            sbf.append("<AccountCode></AccountCode>");
            sbf.append("<TransactionSN>" + p_map.get("IN_RECON_ID") + "</TransactionSN>");
            sbf.append("<RechargingID></RechargingID>");
            sbf.append("<SuspendAddDays></SuspendAddDays>");

            // Access fee defined for the sender
            String segregAccessTax = FileCache.getValue(_interfaceId, "SEGREGATE_ACCESS_FEE_AND_TAX");
            if (!InterfaceUtil.isNullString(segregAccessTax) && "Y".equals(segregAccessTax))
                sbf.append("<AddBalanceFee>" + p_map.get("access_fee") + "</AddBalanceFee>");

            // Set the balance input list
            sbf.append("<BalInputDtoList>");
            sbf.append("<AcctResCode>" + FileCache.getValue(_interfaceId, senderBundleId) + "</AcctResCode>");
            sbf.append("<AcctResName></AcctResName>");
            sbf.append("<Balance></Balance>");
            sbf.append("<AddBalance>" + transferAmt + "</AddBalance>");
            sbf.append("<ExpDate></ExpDate>");
            sbf.append("<AddDays></AddDays>");
            sbf.append("</BalInputDtoList>");

            sbf.append("</body>");
            sbf.append("</Data>");
            sbf.append("</zsmart>");
            requestStr = sbf.toString();

            return requestStr;
        } catch (Exception e) {
            _log.error("generateImmediateDebitRequest", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            _log.debug("generateRechargeCreditRequest", "Exited requestStr: " + requestStr);
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
    public HashMap<String, String> parseImmediateDebitResponse(String responseStr) throws Exception {
        _log.debug("parseImmediateDebitResponse", "Entered responseStr: " + responseStr);

        HashMap<String, String> map = null;
        try {
            map = new HashMap<String, String>();
            String _str = "0";
            int index = responseStr.indexOf("<returnCode>");
            String returnCode = responseStr.substring(index + "<returnCode>".length(), responseStr.indexOf("</returnCode>", index));
            map.put("resp_returnCode", returnCode);

            if (_str.equals((returnCode))) {
                // _requestMap or INTERFACE_ID not available here so
                // AccountResCode is hard-coded.
                int end = responseStr.indexOf("<AccountResCode>1</AccountResCode>");// 1
                                                                                    // is
                                                                                    // for
                                                                                    // main
                                                                                    // account
                                                                                    // on
                                                                                    // live
                                                                                    // environment.
                end = responseStr.indexOf("</BalDtoList>", end);

                index = responseStr.indexOf("<ACTION_ID>");
                if (index != -1) {
                    String actionId = responseStr.substring(index + "<ACTION_ID>".length(), responseStr.indexOf("</ACTION_ID>", index));
                    map.put("resp_action_id", actionId);
                }

                index = responseStr.indexOf("<REQUEST_ID>");
                if (index != -1) {
                    String reqId = responseStr.substring(index + "<REQUEST_ID>".length(), responseStr.indexOf("</REQUEST_ID>", index));
                    map.put("resp_req_id", reqId);
                }

                index = responseStr.lastIndexOf("<Balance>", end);
                if (index != -1) {
                    String grossBalance = responseStr.substring(index + "<Balance>".length(), responseStr.indexOf("</Balance>", index));
                    // If the main balance amount is less than zero, that means
                    // the subscriber has that much credit amount in his/her
                    // account,
                    // else he/she is in negative by the amount in his/her
                    // account.
                    long mainBalLong = Long.parseLong(grossBalance);
                    mainBalLong = 0 - (mainBalLong);
                    grossBalance = String.valueOf(mainBalLong);
                    // put the gross balance in to the map.
                    map.put("resp_Balance", grossBalance);
                }

                index = responseStr.lastIndexOf("<EffDate>", end);
                if (index != -1) {
                    String effDate = responseStr.substring(index + "<EffDate>".length(), responseStr.indexOf("</EffDate>", index));
                    map.put("resp_effDate", effDate);
                }

                index = responseStr.lastIndexOf("<ExpDate>", end);
                if (index != -1) {
                    String expDate = responseStr.substring(index + "<ExpDate>".length(), responseStr.indexOf("</ExpDate>", index));
                    map.put("resp_expDate", expDate);
                }

                index = responseStr.lastIndexOf("<BalanceID>", end);
                if (index != -1) {
                    String balanceID = responseStr.substring(index + "<BalanceID>".length(), responseStr.indexOf("</BalanceID>", index));
                    map.put("resp_balanceID", balanceID);
                }

                index = responseStr.lastIndexOf("<AccountResCode>", end);
                if (index != -1) {
                    String accountResCode = responseStr.substring(index + "<AccountResCode>".length(), responseStr.indexOf("</AccountResCode>", index));
                    map.put("resp_accountResCode", accountResCode);
                }

                index = responseStr.lastIndexOf("<AccountResName>", end);
                if (index != -1) {
                    String accountResName = responseStr.substring(index + "<AccountResName>".length(), responseStr.indexOf("</AccountResName>", index));
                    map.put("resp_accountResName", accountResName);
                }

                index = responseStr.indexOf("<TransactionSN>");
                if (index != -1) {
                    String transactionSN = responseStr.substring(index + "<TransactionSN>".length(), responseStr.indexOf("</TransactionSN>", index));
                    map.put("resp_transactionSN", transactionSN);
                }
            }
            return map;
        } catch (Exception e) {
            _log.error("parseImmediateDebitResponse", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            _log.debug("parseImmediateDebitResponse", "Exiting map: " + map);
        }
    }

    /**
     * This Method will generate the request string for the bundles.
     * 
     * @param p_map
     *            HashMap
     * @throws Exception
     * @return bundleReqStr String
     */
    private String getBundleRequestString(HashMap<String, String> p_map) throws Exception {
        _log.debug("getBundleRequestString", "Entered  ");

        String bundleReqStr = "";
        StringBuffer sbfBundle = null;
        StringBuffer sbfMainBundle = null;
        String[] bundleCodes = null;
        String[] bundleIds = null;
        String[] bundleValidities = null;
        String[] bundleValues = null;
        String[] bundleTypes = null;
        int bonusBundleCount = 0;
        try {
            if (!InterfaceUtil.isNullString(p_map.get("BONUS_BUNDLE_IDS"))) {
                bundleCodes = (p_map.get("BONUS_BUNDLE_CODES")).split("\\|");
                bundleIds = (p_map.get("BONUS_BUNDLE_IDS")).split("\\|");
                bundleValidities = (p_map.get("BONUS_BUNDLE_VALIDITIES")).split("\\|");
                bundleValues = (p_map.get("BONUS_BUNDLE_VALUES")).split("\\|");
                bundleTypes = (p_map.get("BONUS_BUNDLE_TYPES")).split("\\|");

                bonusBundleCount = bundleIds.length;
                sbfBundle = new StringBuffer(1024);
                sbfMainBundle = new StringBuffer(1024);
                for (int i = 0; i < bonusBundleCount; i++) {
                    String bundleCode = bundleCodes[i];
                    String bundleValue = bundleValues[i];
                    String bundleValidity = bundleValidities[i];
                    double bundleValueDbl = Double.parseDouble(bundleValue);
                    double inbundleValueDbl = 0;

                    if (bundleValueDbl > 0 || Long.parseLong(bundleValidity) > 0) {
                        // Calculate the amount by the help of multiplication
                        // factor.
                        if ("AMT".equals(bundleTypes[i]))
                            inbundleValueDbl = InterfaceUtil.getINAmountFromSystemAmountToIN(bundleValueDbl, Double.parseDouble(FileCache.getValue(_interfaceId, "AMT_MULT_FACTOR")));
                        else if ("UNIT".equals(bundleTypes[i]))
                            inbundleValueDbl = InterfaceUtil.getINAmountFromSystemAmountToIN(bundleValueDbl, Double.parseDouble(FileCache.getValue(_interfaceId, "UNIT_MULT_FACTOR")));

                        // Put -(negative) sign for credit.
                        inbundleValueDbl = 0 - inbundleValueDbl;

                        // set the bundle parameters in to the request string.
                        sbfBundle.append("<BalInputDtoList>");
                        sbfBundle.append("<AcctResCode>" + bundleCode + "</AcctResCode>");
                        sbfBundle.append("<AcctResName></AcctResName>");
                        sbfBundle.append("<Balance></Balance>");
                        sbfBundle.append("<AddBalance>" + String.valueOf(Math.round(inbundleValueDbl)) + "</AddBalance>");
                        sbfBundle.append("<ExpDate></ExpDate>");
                        sbfBundle.append("<AddDays>" + bundleValidity + "</AddDays>");
                        sbfBundle.append("</BalInputDtoList>");
                    }
                }
                if (_combinedRecharge && !"1".equals(p_map.get("CARD_GROUP_SELECTOR")))
                    bundleReqStr = sbfMainBundle.toString();
                else
                    bundleReqStr = sbfMainBundle.toString() + sbfBundle.toString();
            }
            return bundleReqStr;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("OCIZteRequestResponseFormatter[getBundleRequestString]", "Exception e::" + e.getMessage());
            throw e;
        } finally {
            _log.debug("getBundleRequestString", "Exiting  bundleReqStr=" + bundleReqStr);
        }
    }

    /**
     * This Method will parse GetAccount Info Response for received bundles.
     * 
     * @param p_responseStr
     *            String
     * @param p_requestMap
     *            HashMap
     * @throws Exception
     * @return void
     */
    private void getBundlesInfoRcvdFromIN(String p_responseStr, HashMap<String, String> p_requestMap) throws Exception {
        _log.debug("getBundlesInfoRcvdFromIN", "Entered ");
        int index = 0;
        int noOfBundles = 0;
        String bdl_code = null;
        String bdlCodeStr = null;

        try {
            // Check whether bundle is present in string or not, if present then
            // split it.
            index = p_responseStr.indexOf("</BalDto>");

            if (index != -1) {
                String[] bundleArray = p_responseStr.split("</BalDto>");
                noOfBundles = bundleArray.length;

                for (int i = 0; i < noOfBundles; i++) {
                    // get the bdl_name if present in the bucket.
                    index = bundleArray[i].indexOf("<AcctResCode>");

                    if (index != -1) {
                        bdl_code = bundleArray[i].substring(index + "<AcctResCode>".length(), bundleArray[i].indexOf("</AcctResCode>", index));

                        if (bdl_code.equalsIgnoreCase("0")) {
                            int index1 = bundleArray[i].indexOf("<Balance>");
                            String bdl_Balance = bundleArray[i].substring(index1 + "<Balance>".length(), bundleArray[i].indexOf("</Balance>", index1));
                            long bdl_BalanceLng = Long.parseLong(bdl_Balance);
                            bdl_BalanceLng = 0 - (bdl_BalanceLng);
                            bdl_Balance = String.valueOf(bdl_BalanceLng);
                            p_requestMap.put("resp_Balance", bdl_Balance);

                            index1 = bundleArray[i].indexOf("<ExpDate>");
                            String bdl_ExpDate = bundleArray[i].substring(index1 + "<ExpDate>".length(), bundleArray[i].indexOf("</ExpDate>", index1));
                            p_requestMap.put("resp_ExpDate", bdl_ExpDate);

                            index1 = bundleArray[i].indexOf("<EffDate>");
                            String bdl_EffDate = bundleArray[i].substring(index1 + "<EffDate>".length(), bundleArray[i].indexOf("</EffDate>", index1));
                            p_requestMap.put("resp_EffDate", bdl_EffDate);
                        }

                        if (!InterfaceUtil.isNullString(bdlCodeStr))
                            bdlCodeStr = bdlCodeStr + "," + bdl_code;
                        else
                            bdlCodeStr = bdl_code;
                    }
                }
            }

            if (!InterfaceUtil.isNullString(bdlCodeStr))
                p_requestMap.put("received_bundles", bdlCodeStr.trim());
            else
                p_requestMap.put("received_bundles", bdlCodeStr);
        } catch (Exception e) {
            _log.error("getBundlesInfoRcvdFromIN", "Exception e: " + e.getMessage());
            throw e;
        } finally {
            _log.debug("getBundlesInfoRcvdFromIN", "Exiting Defined bundles at IN=" + p_requestMap.get("received_bundles"));
        }
    }

    /**
     * This Method will parse the Credit Response for received bundles.
     * 
     * @param p_responseStr
     *            String
     * @param p_requestMap
     *            HashMap
     * @throws Exception
     * @return void
     */
    private void getBundlesInfoRcvdFromIN4Credit(String p_responseStr, HashMap<String, String> p_requestMap) throws Exception {
        _log.debug("getBundlesInfoRcvdFromIN4Credit", "Entered ");
        int index = 0;
        int noOfBundles = 0;
        String bdl_code = null;
        String bdlCodeStr = null;

        try {
            // Check whether bundle is present in string or not, if present then
            // split it.
            index = p_responseStr.indexOf("</BalDtoList>");
            if (index != -1) {
                String[] bundleArray = p_responseStr.split("</BalDtoList>");
                noOfBundles = bundleArray.length;

                for (int i = 0; i < noOfBundles; i++) {
                    // get the bdl_name if present in the bucket.
                    index = bundleArray[i].indexOf("<AcctResCode>");

                    if (index != -1) {
                        bdl_code = bundleArray[i].substring(index + "<AcctResCode>".length(), bundleArray[i].indexOf("</AcctResCode>", index));
                        if (bdl_code.equalsIgnoreCase("0")) {
                            int index1 = bundleArray[i].indexOf("<Balance>");
                            String bdl_Balance = bundleArray[i].substring(index1 + "<Balance>".length(), bundleArray[i].indexOf("</Balance>", index1));
                            long bdl_BalanceLng = Long.parseLong(bdl_Balance);
                            bdl_BalanceLng = 0 - (bdl_BalanceLng);
                            bdl_Balance = String.valueOf(bdl_BalanceLng);
                            p_requestMap.put("resp_Balance", bdl_Balance);

                            index1 = bundleArray[i].indexOf("<ExpDate>");
                            String bdl_ExpDate = bundleArray[i].substring(index1 + "<ExpDate>".length(), bundleArray[i].indexOf("</ExpDate>", index1));
                            p_requestMap.put("resp_ExpDate", bdl_ExpDate);

                            index1 = bundleArray[i].indexOf("<EffDate>");
                            String bdl_EffDate = bundleArray[i].substring(index1 + "<EffDate>".length(), bundleArray[i].indexOf("</EffDate>", index1));
                            p_requestMap.put("resp_EffDate", bdl_EffDate);
                        }

                        if (!InterfaceUtil.isNullString(bdlCodeStr))
                            bdlCodeStr = bdlCodeStr + "," + bdl_code;
                        else
                            bdlCodeStr = bdl_code;
                    }
                }
            }
            if (!InterfaceUtil.isNullString(bdlCodeStr))
                p_requestMap.put("received_bundles", bdlCodeStr.trim());
            else
                p_requestMap.put("received_bundles", bdlCodeStr);
        } catch (Exception e) {
            _log.error("getBundlesInfoRcvdFromIN4Credit", "Exception e: " + e.getMessage());
            throw e;
        } finally {
            _log.debug("getBundlesInfoRcvdFromIN4Credit", "Exiting Defined bundles at IN=" + p_requestMap.get("received_bundles"));
        }
    }

    /**
     * This Method will generate the IN Request ID(a unique sequence identify a
     * Request, can not be repeated),
     * for each request.
     * The format is: Channel_ID+yyyyMMddHHmmss+8 bit sequence no.
     * 
     * @param p_map
     *            HashMap
     * @throws Exception
     * @return dateStrReqTime String
     */
    private String getINRequestID(HashMap<String, String> p_map) throws Exception {
        _log.debug("getINRequestID", "Entered");

        String reqId = "";
        String counter = "";
        String dateStrReqId = null;
        String dateStrReqTime = null;
        String timeStrReqTime = null;
        SimpleDateFormat sdfReqId = null;
        SimpleDateFormat sdfReqTime = null;
        SimpleDateFormat sdfTimeReqTime = null;

        // 5 bit sequence number is required to generate the IN Request ID.
        int inTxnLength = 5;
        try {
            Date mydate = new Date();
            sdfReqId = new SimpleDateFormat("yyMMdd");
            sdfReqTime = new SimpleDateFormat("yyyyMMddHHmmss");
            sdfTimeReqTime = new SimpleDateFormat("HHmmss");
            dateStrReqId = sdfReqId.format(mydate);
            dateStrReqTime = sdfReqTime.format(mydate);
            timeStrReqTime = sdfTimeReqTime.format(mydate);
            counter = getIncrCounter();

            int length = counter.length();
            int tmpLength = inTxnLength - length;
            if (length < inTxnLength) {
                for (int i = 0; i < tmpLength; i++)
                    counter = "0" + counter;
            }

            reqId = p_map.get("CHANNEL_ID") + dateStrReqId + timeStrReqTime + Constants.getProperty("INSTANCE_ID") + counter;
            p_map.put("IN_REQ_ID", reqId);
            p_map.put("IN_REQ_TIME", dateStrReqTime);

            _log.debug("getINRequestID", "Exited  id: " + counter + ", reqId=" + reqId);

            return reqId;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("OCIZteRequestResponseFormatter[getINRequestID]", "Exception e::" + e.getMessage());
            throw e;
        }// end of catch-Exception
    }

    /**
     * This Method will generate the IN Transaction ID for each request.
     * 
     * @param p_map
     *            HashMap
     * @throws BTSLBaseException
     * @return _counter String
     */
    public static synchronized String getIncrCounter() throws BTSLBaseException {
        _log.debug("getIncrCounter", "Entered");
        try {
            if (_counter == 99999)
                _counter = 0;
            _counter++;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("getIncrCounter", e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEKenyaRequestFormatter[getIncrCounter]", "", "", "", " Error occurs while getting IN request id Exception is " + e.getMessage());
            throw new BTSLBaseException(e.getMessage());
        } finally {
            _log.debug("getIncrCounter", "Exiting counter = " + _counter);
        }
        return String.valueOf(_counter);
    }
}
