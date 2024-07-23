package com.inter.vodafoneghana.voucherconsumptiongh;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.StringTokenizer;

import netscape.ldap.LDAPAttribute;
import netscape.ldap.LDAPAttributeSet;
import netscape.ldap.LDAPEntry;
import netscape.ldap.LDAPSearchResults;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class VoucherConsumptionGHResponseParser {

    private static Log _log = LogFactory.getLog(VoucherConsumptionGHResponseParser.class.getName());
    VoucherConsumptionGHError err = new VoucherConsumptionGHError();

    /**
     * This method is used to parse the response.
     * 
     * @param int p_action
     * @param String
     *            p_responseStr
     * @return HashMap
     * @throws Exception
     */
    public HashMap parseResponse(int p_action, HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseResponse", "Entered p_action::" + p_action + " p_responseStr:: " + p_map);
        HashMap map = null;
        try {
            switch (p_action) {

            case VoucherConsumptionGHI.ACTION_ACCOUNT_DETAILS: {
                map = parseRechargeAccountDetailsResponse(p_map);
                break;
            }
            case VoucherConsumptionGHI.ACTION_RECHARGE_CREDIT: {

                map = parseRechargeCreditResponse(p_map);
                break;
            }
            case VoucherConsumptionGHI.ACTION_IMMEDIATE_DEBIT: {
                map = parseImmediateDebitResponse(p_map);
                break;
            }
            case VoucherConsumptionGHI.ACTION_RECHARGE_CREDIT_BUNDLE: {
                map = parseRechargeBundleCreditResponse(p_map);

                break;
            }
            case VoucherConsumptionGHI.ACTION_BLOCK_SUBSCRIBER: {
                map = parseSubscriberBlockResponse(p_map);
                break;
            }
            }
        } catch (Exception e) {
            _log.error("parseResponse", "Exception e::" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseResponse", "Exiting map::" + map);
        }
        return map;
    }

    /**
     * This method is used to parse the credit response.
     * 
     * @param p_responseStr
     * @return HashMap
     * @throws Exception
     */
    public HashMap parseSubscriberBlockResponse(HashMap _reqMap) throws Exception {
        final String methodName = "VoucherConsumptionGHResponseParser[parseSubscriberBlockResponse]";

        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered object::" + _reqMap);
        HashMap responseMap = null;
        LDAPSearchResults ldSearchResult = null;
        try {
            responseMap = new HashMap();
            if (!BTSLUtil.isNullString(Constants.getProperty("VFGGAHANA_SIMULATOR")) && "Y".equalsIgnoreCase(Constants.getProperty("VFGGAHANA_SIMULATOR"))) {
                responseMap.put("INTERFACE_STATUS", "00");
                return responseMap;
            }
            Object object = _reqMap.get("RESPONSE_OBJECT");

            if (object != null)
                ldSearchResult = (LDAPSearchResults) object;
            else
                throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);

            LDAPEntry ldEntry = null;

            if (ldSearchResult.hasMoreElements()) {
                ldEntry = ldSearchResult.next();
                LDAPAttributeSet ldAttributeSet = ldEntry.getAttributeSet();
                Enumeration<LDAPAttribute> enumAttributeSet = ldAttributeSet.getAttributes();
                while (enumAttributeSet.hasMoreElements()) {
                    LDAPAttribute ldAttribute = enumAttributeSet.nextElement();
                    String attrName = ldAttribute.getName();
                    String[] aVal = ldAttribute.getStringValueArray();
                    if (attrName.equalsIgnoreCase(VoucherConsumptionGHI.STATUS_KEY)) {
                        for (String val : aVal) {
                            responseMap.put("INTERFACE_STATUS", val);
                        }

                    }
                    if (attrName.equalsIgnoreCase(VoucherConsumptionGHI.VALUE_KEY)) {
                        for (String val : aVal) {
                            responseMap.put("INTERFACE_PARAMTER", val);
                        }

                    }
                }

            }

            if (responseMap.get("INTERFACE_STATUS") != null && !BTSLUtil.isNullString(responseMap.get("INTERFACE_STATUS").toString()) && responseMap.get("INTERFACE_STATUS").toString().equalsIgnoreCase(VoucherConsumptionGHI.RESPONSE_SUCCESS)) {
            } else {

                if (responseMap.get("INTERFACE_STATUS") == null) {
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                } else if (BTSLUtil.isNullString(responseMap.get("INTERFACE_STATUS").toString())) {
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                }
                throw new BTSLBaseException(err.mapErrorCode(Integer.parseInt(responseMap.get("INTERFACE_STATUS").toString())));
            }

        } catch (BTSLBaseException be) {
            _log.error(methodName, "Exception e::" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error(methodName, "Exception e::" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exited responseMap::" + responseMap);
        }
        return responseMap;
    }

    /**
     * This method is used to parse the credit response.
     * 
     * @param p_responseStr
     * @return HashMap
     * @throws Exception
     */
    public HashMap parseRechargeBundleCreditResponse(HashMap _reqMap) throws Exception {
        final String methodName = "VoucherConsumptionGHResponseParser[parseRechargeBundleCreditResponse]";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered object::" + _reqMap);
        HashMap responseMap = null;
        LDAPSearchResults ldSearchResult = null;
        try {
            responseMap = new HashMap();
            Object object = _reqMap.get("RESPONSE_OBJECT");
            if (!BTSLUtil.isNullString(Constants.getProperty("VFGGAHANA_SIMULATOR")) && "Y".equalsIgnoreCase(Constants.getProperty("VFGGAHANA_SIMULATOR"))) {
                responseMap.put("INTERFACE_STATUS", "00");
                return responseMap;
            }
            if (object != null)
                ldSearchResult = (LDAPSearchResults) object;
            else
                throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);

            LDAPEntry ldEntry = null;

            if (ldSearchResult.hasMoreElements()) {
                ldEntry = ldSearchResult.next();
                LDAPAttributeSet ldAttributeSet = ldEntry.getAttributeSet();
                Enumeration<LDAPAttribute> enumAttributeSet = ldAttributeSet.getAttributes();
                while (enumAttributeSet.hasMoreElements()) {
                    LDAPAttribute ldAttribute = enumAttributeSet.nextElement();
                    String attrName = ldAttribute.getName();
                    String[] aVal = ldAttribute.getStringValueArray();
                    if (attrName.equalsIgnoreCase(VoucherConsumptionGHI.STATUS_KEY)) {
                        for (String val : aVal) {
                            responseMap.put("INTERFACE_STATUS", val);
                        }

                    }
                    if (attrName.equalsIgnoreCase(VoucherConsumptionGHI.VALUE_KEY)) {
                        for (String val : aVal) {
                            responseMap.put("INTERFACE_PARAMTER", val);
                        }

                    }
                }

            }

            if (responseMap.get("INTERFACE_STATUS") != null && !BTSLUtil.isNullString(responseMap.get("INTERFACE_STATUS").toString()) && responseMap.get("INTERFACE_STATUS").toString().equalsIgnoreCase(VoucherConsumptionGHI.RESPONSE_SUCCESS)) {
            } else {

                if (responseMap.get("INTERFACE_STATUS") == null) {
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                } else if (BTSLUtil.isNullString(responseMap.get("INTERFACE_STATUS").toString())) {
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                }
                throw new BTSLBaseException(err.mapErrorCode(Integer.parseInt(responseMap.get("INTERFACE_STATUS").toString())));
            }

        } catch (BTSLBaseException be) {
            _log.error(methodName, "Exception e::" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error(methodName, "Exception e::" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exited responseMap::" + responseMap);
        }
        return responseMap;
    }

    /**
     * This method is used to parse the credit response.
     * 
     * @param p_responseStr
     * @return HashMap
     * @throws Exception
     */
    public HashMap parseRechargeCreditResponse(HashMap _reqMap) throws Exception {
        final String methodName = "VoucherConsumptionGHResponseParser[parseRechargeCreditResponse]";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered object::" + _reqMap);
        HashMap responseMap = null;

        LDAPSearchResults ldSearchResult = null;
        try {
            responseMap = new HashMap();

            if (!BTSLUtil.isNullString(Constants.getProperty("VFGGAHANA_SIMULATOR")) && "Y".equalsIgnoreCase(Constants.getProperty("VFGGAHANA_SIMULATOR"))) {
                responseMap.put("INTERFACE_STATUS", "00");
                responseMap.put("POST_BALANCE", "10");
                responseMap.put("NEW_EXPIRY_DATE", BTSLUtil.getDateTimeStringFromDate(new Date(), "yyyyMMdd"));
                return responseMap;
            }
            Object object = _reqMap.get("RESPONSE_OBJECT");

            if (object != null)
                ldSearchResult = (LDAPSearchResults) object;
            else
                throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);

            LDAPEntry ldEntry = null;

            if (ldSearchResult.hasMoreElements()) {
                ldEntry = ldSearchResult.next();
                LDAPAttributeSet ldAttributeSet = ldEntry.getAttributeSet();
                Enumeration<LDAPAttribute> enumAttributeSet = ldAttributeSet.getAttributes();
                while (enumAttributeSet.hasMoreElements()) {
                    LDAPAttribute ldAttribute = enumAttributeSet.nextElement();
                    String attrName = ldAttribute.getName();
                    String[] aVal = ldAttribute.getStringValueArray();
                    if (attrName.equalsIgnoreCase(VoucherConsumptionGHI.STATUS_KEY)) {
                        for (String val : aVal) {
                            responseMap.put("INTERFACE_STATUS", val);
                        }

                    }
                    if (attrName.equalsIgnoreCase(VoucherConsumptionGHI.VALUE_KEY)) {
                        for (String val : aVal) {
                            responseMap.put("INTERFACE_PARAMTER", val);
                        }

                    }
                }

            }

            if (responseMap.get("INTERFACE_STATUS") != null && !BTSLUtil.isNullString(responseMap.get("INTERFACE_STATUS").toString()) && responseMap.get("INTERFACE_STATUS").toString().equalsIgnoreCase(VoucherConsumptionGHI.RESPONSE_SUCCESS)) {
                if (responseMap.get("INTERFACE_PARAMTER") != null && !BTSLUtil.isNullString(responseMap.get("INTERFACE_PARAMTER").toString())) {
                    String resultParam = responseMap.get("INTERFACE_PARAMTER").toString();
                    StringTokenizer resultParamTokens = new StringTokenizer(resultParam, ";");
                    while (resultParamTokens.hasMoreElements()) {
                        String resultParamToken = (String) resultParamTokens.nextElement();
                        resultParamToken = resultParamToken.trim();
                        if (resultParamToken.startsWith("New Balance Is")) {
                            int length = resultParamToken.indexOf("Is");
                            length = length + 2;
                            String postBalance = resultParamToken.substring(length);
                            if (!BTSLUtil.isNullString(postBalance)) {
                                responseMap.put("POST_BALANCE", postBalance.trim());
                            } else {
                                responseMap.put("POST_BALANCE", 0);

                            }

                        }
                        if (resultParamToken.startsWith("REASON")) {
                            int length = resultParamToken.indexOf("New CED Is");
                            length = length + 10;
                            int totalLength = resultParamToken.length();
                            String cedDate = resultParamToken.substring(length, totalLength);
                            if (!BTSLUtil.isNullString(cedDate)) {
                                responseMap.put("NEW_EXPIRY_DATE", cedDate.trim());
                            } else {
                                responseMap.put("NEW_EXPIRY_DATE", BTSLUtil.getDateTimeStringFromDate(new Date(), "yyyyMMdd"));
                            }

                        }
                    }
                }
            } else {

                if (responseMap.get("INTERFACE_STATUS") == null) {
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                } else if (BTSLUtil.isNullString(responseMap.get("INTERFACE_STATUS").toString())) {
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                }
                throw new BTSLBaseException(err.mapErrorCode(Integer.parseInt(responseMap.get("INTERFACE_STATUS").toString())));
            }

        } catch (BTSLBaseException be) {
            _log.error(methodName, "Exception e::" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error(methodName, "Exception e::" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exited responseMap::" + responseMap);
        }
        return responseMap;
    }

    /**
     * This method is used to parse the credit response.
     * 
     * @param p_responseStr
     * @return HashMap
     * @throws Exception
     */
    public HashMap parseRechargeAccountDetailsResponse(HashMap _reqMap) throws Exception {
        final String methodName = "VoucherConsumptionGHResponseParser[parseRechargeAccountDetailsResponse]";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered object::" + _reqMap);
        HashMap responseMap = null;
        LDAPSearchResults ldSearchResult = null;
        try {
            responseMap = new HashMap();

            if (!BTSLUtil.isNullString(Constants.getProperty("VFGGAHANA_SIMULATOR")) && "Y".equalsIgnoreCase(Constants.getProperty("VFGGAHANA_SIMULATOR"))) {
                responseMap.put("INTERFACE_STATUS", "00");
                responseMap.put("RESP_BALANCE", "10");
                responseMap.put("OLD_EXPIRY_DATE", BTSLUtil.getDateTimeStringFromDate(new Date(), "yyyyMMdd"));
                responseMap.put("ACCOUNT_STATUS", "Active");
                responseMap.put("SERVICE_CLASS", "ALL");
                return responseMap;
            }

            Object object = _reqMap.get("RESPONSE_OBJECT");

            if (object != null)
                ldSearchResult = (LDAPSearchResults) object;
            else
                throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);

            LDAPEntry ldEntry = null;

            if (ldSearchResult.hasMoreElements()) {
                ldEntry = ldSearchResult.next();
                LDAPAttributeSet ldAttributeSet = ldEntry.getAttributeSet();
                Enumeration<LDAPAttribute> enumAttributeSet = ldAttributeSet.getAttributes();
                while (enumAttributeSet.hasMoreElements()) {
                    LDAPAttribute ldAttribute = enumAttributeSet.nextElement();
                    String attrName = ldAttribute.getName();
                    String[] aVal = ldAttribute.getStringValueArray();
                    if (attrName.equalsIgnoreCase(VoucherConsumptionGHI.STATUS_KEY)) {
                        for (String val : aVal) {
                            responseMap.put("INTERFACE_STATUS", val);
                        }

                    }
                    if (attrName.equalsIgnoreCase(VoucherConsumptionGHI.VALUE_KEY)) {
                        for (String val : aVal) {
                            responseMap.put("INTERFACE_PARAMTER", val);
                        }

                    }
                }

            }

            if (responseMap.get("INTERFACE_STATUS") != null && !BTSLUtil.isNullString(responseMap.get("INTERFACE_STATUS").toString()) && responseMap.get("INTERFACE_STATUS").toString().equalsIgnoreCase(VoucherConsumptionGHI.RESPONSE_SUCCESS)) {
                if (responseMap.get("INTERFACE_PARAMTER") != null && !BTSLUtil.isNullString(responseMap.get("INTERFACE_PARAMTER").toString())) {

                    String resultParam = responseMap.get("INTERFACE_PARAMTER").toString();
                    StringTokenizer resultParamTokens = new StringTokenizer(resultParam, ";");
                    while (resultParamTokens.hasMoreElements()) {
                        String resultParamToken = (String) resultParamTokens.nextElement();
                        String[] attriPara = resultParamToken.split("=");
                        if (attriPara.length > 1) {
                            responseMap.put(attriPara[0], attriPara[1]);
                        } else {
                            responseMap.put(attriPara[0], "");
                        }
                    }
                }
                if (responseMap.get("SIM_RTDB_Record1.Primary_Balance") != null && !BTSLUtil.isNullString(responseMap.get("SIM_RTDB_Record1.Primary_Balance").toString())) {
                    responseMap.put("RESP_BALANCE", responseMap.get("SIM_RTDB_Record1.Primary_Balance").toString());
                }
                if (responseMap.get("SIM_RTDB_Record1.SIM_Credit_Expiry_Date") != null && !BTSLUtil.isNullString(responseMap.get("SIM_RTDB_Record1.SIM_Credit_Expiry_Date").toString())) {
                    responseMap.put("OLD_EXPIRY_DATE", responseMap.get("SIM_RTDB_Record1.SIM_Credit_Expiry_Date").toString());
                }
                if (responseMap.get("SIM_RTDB_Record1.SIM_State") != null && !BTSLUtil.isNullString(responseMap.get("SIM_RTDB_Record1.SIM_State").toString())) {
                    responseMap.put("ACCOUNT_STATUS", responseMap.get("SIM_RTDB_Record1.SIM_State").toString());
                }
                if (responseMap.get("SIM_RTDB_Record1.COSP_ID") != null && !BTSLUtil.isNullString(responseMap.get("SIM_RTDB_Record1.COSP_ID").toString())) {
                    responseMap.put("SERVICE_CLASS", responseMap.get("SIM_RTDB_Record1.COSP_ID").toString());
                }

            } else {

                if (responseMap.get("INTERFACE_STATUS") == null) {
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                } else if (BTSLUtil.isNullString(responseMap.get("INTERFACE_STATUS").toString())) {
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                }
                throw new BTSLBaseException(err.mapErrorCode(Integer.parseInt(responseMap.get("INTERFACE_STATUS").toString())));
            }

        } catch (BTSLBaseException be) {
            _log.error(methodName, "Exception e::" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error(methodName, "Exception e::" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exited responseMap::" + responseMap);
        }
        return responseMap;
    }

    /**
     * This method is used to parse the credit response.
     * 
     * @param p_responseStr
     * @return HashMap
     * @throws Exception
     */
    public HashMap parseImmediateDebitResponse(HashMap _reqMap) throws Exception {
        final String methodName = "VoucherConsumptionGHResponseParser[parseImmediateDebitResponse]";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered object::" + _reqMap);
        HashMap responseMap = null;
        LDAPSearchResults ldSearchResult = null;
        try {
            responseMap = new HashMap();
            if (!BTSLUtil.isNullString(Constants.getProperty("VFGGAHANA_SIMULATOR")) && "Y".equalsIgnoreCase(Constants.getProperty("VFGGAHANA_SIMULATOR"))) {
                responseMap.put("INTERFACE_STATUS", "00");
                responseMap.put("POST_BALANCE", "10");
                responseMap.put("NEW_EXPIRY_DATE", BTSLUtil.getDateTimeStringFromDate(new Date(), "yyyyMMdd"));
                return responseMap;
            }
            Object object = _reqMap.get("RESPONSE_OBJECT");

            if (object != null)
                ldSearchResult = (LDAPSearchResults) object;
            else
                throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);

            LDAPEntry ldEntry = null;

            if (ldSearchResult.hasMoreElements()) {
                ldEntry = ldSearchResult.next();
                LDAPAttributeSet ldAttributeSet = ldEntry.getAttributeSet();
                Enumeration<LDAPAttribute> enumAttributeSet = ldAttributeSet.getAttributes();
                while (enumAttributeSet.hasMoreElements()) {
                    LDAPAttribute ldAttribute = enumAttributeSet.nextElement();
                    String attrName = ldAttribute.getName();
                    String[] aVal = ldAttribute.getStringValueArray();
                    if (attrName.equalsIgnoreCase(VoucherConsumptionGHI.STATUS_KEY)) {
                        for (String val : aVal) {
                            responseMap.put("INTERFACE_STATUS", val);
                        }

                    }
                    if (attrName.equalsIgnoreCase(VoucherConsumptionGHI.VALUE_KEY)) {
                        for (String val : aVal) {
                            responseMap.put("INTERFACE_PARAMTER", val);
                        }

                    }
                }

            }

            if (responseMap.get("INTERFACE_STATUS") != null && !BTSLUtil.isNullString(responseMap.get("INTERFACE_STATUS").toString()) && responseMap.get("INTERFACE_STATUS").toString().equalsIgnoreCase(VoucherConsumptionGHI.RESPONSE_SUCCESS)) {
                if (responseMap.get("INTERFACE_PARAMTER") != null && !BTSLUtil.isNullString(responseMap.get("INTERFACE_PARAMTER").toString())) {
                    String resultParam = responseMap.get("INTERFACE_PARAMTER").toString();
                    StringTokenizer resultParamTokens = new StringTokenizer(resultParam, ";");
                    while (resultParamTokens.hasMoreElements()) {
                        String resultParamToken = (String) resultParamTokens.nextElement();
                        resultParamToken = resultParamToken.trim();
                        if (resultParamToken.startsWith("New Balance Is")) {
                            int length = resultParamToken.indexOf("Is");
                            length = length + 2;
                            String postBalance = resultParamToken.substring(length);
                            if (!BTSLUtil.isNullString(postBalance)) {
                                responseMap.put("POST_BALANCE", postBalance.trim());
                            } else {
                                responseMap.put("POST_BALANCE", 0);

                            }

                        }
                        if (resultParamToken.startsWith("REASON")) {
                            int length = resultParamToken.indexOf("New CED Is");
                            length = length + 10;
                            int totalLength = resultParamToken.length();
                            String cedDate = resultParamToken.substring(length, totalLength);
                            if (!BTSLUtil.isNullString(cedDate)) {
                                responseMap.put("NEW_EXPIRY_DATE", cedDate.trim());
                            } else {
                                responseMap.put("NEW_EXPIRY_DATE", BTSLUtil.getDateTimeStringFromDate(new Date(), "yyyyMMdd"));
                            }

                        }
                    }
                }
            } else {

                if (responseMap.get("INTERFACE_STATUS") == null) {
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                } else if (BTSLUtil.isNullString(responseMap.get("INTERFACE_STATUS").toString())) {
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                }
                throw new BTSLBaseException(err.mapErrorCode(Integer.parseInt(responseMap.get("INTERFACE_STATUS").toString())));
            }

        } catch (BTSLBaseException be) {
            _log.error(methodName, "Exception e::" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error(methodName, "Exception e::" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exited responseMap::" + responseMap);
        }
        return responseMap;
    }

}
