package com.inter.cs3mobilecom;

/**
 * @(#)CS3Air22TestXMLParser
 *                           Copyright(c) 2011, COMVIVA TECHNOLOGIES LIMITED.
 *                           All rights reserved.
 *                           COMVIVA PROPRIETARY/CONFIDENTIAL. Use is subject to
 *                           license terms.
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Author Date History
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Vinay Kumar Singh January 25, 2011 Initial Creation
 *                           --------------------------------------------------
 *                           ----------------------------------------------
 *                           This class is responsible to parse the XML request
 */
import java.util.HashMap;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

public class CS3Air22TestXMLParser {
    public Log _log = LogFactory.getLog(this.getClass().getName());
    private HashMap<String, String> _responseMap = null;

    public CS3Air22TestXMLParser() {

    }

    public CS3Air22TestXMLParser(HashMap<String, String> p_responseMap) {
        this._responseMap = p_responseMap;
    }

    /**
     * This method implements the logic to parse the request XML string of
     * account info.
     * 
     * @param String
     *            p_requestStr
     * @return HashMap
     * @throws Exception
     */
    public HashMap<String, String> parseGetAccountInformation(String p_requestStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseGetAccountInformation", "Entered p_requestStr: " + p_requestStr);
        HashMap<String, String> requestMap = null;
        int indexStart = 0;
        int indexEnd = 0;
        int tempIndex = 0;
        String hostName = null;
        String nodeType = null;
        String faultCode = "";
        String faultString = "";
        try {
            requestMap = new HashMap<String, String>();
            indexStart = p_requestStr.indexOf("<member>");
            tempIndex = p_requestStr.indexOf("originNodeType", indexStart);
            if (tempIndex > 0) {
                String originNodeType = p_requestStr.substring("<string>".length() + p_requestStr.indexOf("<string>", tempIndex), p_requestStr.indexOf("</string>", tempIndex)).trim();
                nodeType = (String) _responseMap.get("NODE_TYPE");
                if (!BTSLUtil.isNullString(nodeType) && !nodeType.trim().equals(originNodeType)) {
                    requestMap.put("responseCode", "1001");
                    return requestMap;
                }
                requestMap.put("originNodeType", originNodeType.trim());
            } else {
                faultCode = CS3Air22I.MANDATORY_PARAMETER_MISSING;
                faultString = "Manadatory parameter originNodeType is Missing in the request";
                requestMap.put("faultCode", faultCode);
                requestMap.put("faultString", faultString);
                return requestMap;
            }
            indexStart = p_requestStr.indexOf("<member>", indexEnd);
            tempIndex = p_requestStr.indexOf("originHostName", indexStart);
            if (tempIndex > 0) {
                String originHostName = p_requestStr.substring("<string>".length() + p_requestStr.indexOf("<string>", tempIndex), p_requestStr.indexOf("</string>", tempIndex));
                hostName = (String) _responseMap.get("PreTups");
                if (!BTSLUtil.isNullString(hostName) && !hostName.trim().equals(originHostName)) {
                    requestMap.put("responseCode", "1002");
                    return requestMap;
                }
                requestMap.put("originHostName", originHostName.trim());
            }
            indexEnd = p_requestStr.indexOf("</member>", indexStart);
            indexStart = p_requestStr.indexOf("<member>", indexEnd);
            tempIndex = p_requestStr.indexOf("originTransactionID", indexStart);
            if (tempIndex > 0) {
                String originTransactionID = p_requestStr.substring("<string>".length() + p_requestStr.indexOf("<string>", tempIndex), p_requestStr.indexOf("</string>", tempIndex)).trim();
                requestMap.put("originTransactionID", originTransactionID.trim());
            } else {
                faultCode = CS3Air22I.MANDATORY_PARAMETER_MISSING;
                faultString = "Manadatory parameter originTransactionID is Missing in the request";
                requestMap.put("faultCode", faultCode);
                requestMap.put("faultString", faultString);
                return requestMap;
            }
            indexEnd = p_requestStr.indexOf("</member>", indexStart);
            indexStart = p_requestStr.indexOf("<member>", indexEnd);
            tempIndex = p_requestStr.indexOf("originTimeStamp", indexStart);
            if (tempIndex > 0) {
                String originTimeStamp = p_requestStr.substring("<dateTime.iso8601>".length() + p_requestStr.indexOf("<dateTime.iso8601>", tempIndex), p_requestStr.indexOf("</dateTime.iso8601>", tempIndex)).trim();
                requestMap.put("originTimeStamp", originTimeStamp.trim());
            } else {
                faultCode = CS3Air22I.MANDATORY_PARAMETER_MISSING;
                faultString = "Manadatory parameter originTimeStamp is Missing in the request";
                requestMap.put("faultCode", faultCode);
                requestMap.put("faultString", faultString);
                return requestMap;
            }
            indexEnd = p_requestStr.indexOf("</member>", indexStart);
            indexStart = p_requestStr.indexOf("<member>", indexEnd);
            tempIndex = p_requestStr.indexOf("subscriberNumberNAI", indexStart);
            if (tempIndex > 0) {
                String subscriberNumberNAI = p_requestStr.substring("<i4>".length() + p_requestStr.indexOf("<i4>", tempIndex), p_requestStr.indexOf("</i4>", tempIndex)).trim();
                requestMap.put("subscriberNumberNAI", subscriberNumberNAI.trim());
            }
            indexEnd = p_requestStr.indexOf("</member>", indexStart);
            indexStart = p_requestStr.indexOf("<member>", indexEnd);
            tempIndex = p_requestStr.indexOf("subscriberNumber", indexStart);
            if (tempIndex > 0) {
                String subscriberNumber = p_requestStr.substring("<string>".length() + p_requestStr.indexOf("<string>", tempIndex), p_requestStr.indexOf("</string>", tempIndex)).trim();
                requestMap.put("subscriberNumber", subscriberNumber.trim());
            } else {
                faultCode = CS3Air22I.MANDATORY_PARAMETER_MISSING;
                faultString = "Manadatory parameter subscriberNumber is Missing in the request";
                requestMap.put("faultCode", faultCode);
                requestMap.put("faultString", faultString);
                return requestMap;
            }
            indexEnd = p_requestStr.indexOf("</member>", indexStart);
            indexStart = p_requestStr.indexOf("<member>", indexEnd);
            tempIndex = p_requestStr.indexOf("messageCapabilityFlag", indexStart);
            if (tempIndex > 0) {
                String messageCapabilityFlag = p_requestStr.substring("<string>".length() + p_requestStr.indexOf("<string>", tempIndex), p_requestStr.indexOf("</string>", tempIndex)).trim();
                requestMap.put("messageCapabilityFlag", messageCapabilityFlag.trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("parseGetAccountInformation", "Exception e:" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseGetAccountInformation", "Exited requestMap :" + requestMap);
        }// end of finally
        return requestMap;
    }// end of parseGetAccountInformation

    /**
     * This method is used parse the Re-charge request.
     * 
     * @param String
     *            p_requestStr
     * @return HashMap
     * @throws Exception
     */
    public HashMap<String, String> parseRechargeCreditRequest(String p_requestStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseRechargeCreditRequest", "Entered p_requestStr: " + p_requestStr);
        HashMap<String, String> requestMap = null;
        int indexStart = 0;
        int indexEnd = 0;
        int tempIndex = 0;
        String faultCode = null;
        String faultString = null;
        String hostName = "ETUPS";
        String nodeType = "Zebra";
        String serviceClass = null;
        try {
            requestMap = new HashMap<String, String>();
            indexStart = p_requestStr.indexOf("<member>");
            tempIndex = p_requestStr.indexOf("originNodeType", indexStart);
            if (tempIndex > 0) {
                String originNodeType = p_requestStr.substring("<string>".length() + p_requestStr.indexOf("<string>", tempIndex), p_requestStr.indexOf("</string>", tempIndex)).trim();
                nodeType = (String) _responseMap.get("NODE_TYPE");
                if (!BTSLUtil.isNullString(nodeType) && !nodeType.trim().equals(originNodeType)) {
                    requestMap.put("responseCode", "1001");
                    return requestMap;
                }
                requestMap.put("originNodeType", originNodeType.trim());
            } else {
                faultCode = CS3Air22I.MANDATORY_PARAMETER_MISSING;
                faultString = "Manadatory parameter originNodeType is Missing in the request";
                requestMap.put("faultCode", faultCode);
                requestMap.put("faultString", faultString);
                return requestMap;
            }
            indexStart = p_requestStr.indexOf("<member>", indexEnd);
            tempIndex = p_requestStr.indexOf("originHostName", indexStart);
            if (tempIndex > 0) {
                String originHostName = p_requestStr.substring("<string>".length() + p_requestStr.indexOf("<string>", tempIndex), p_requestStr.indexOf("</string>", tempIndex));
                hostName = (String) _responseMap.get("PreTups");
                if (!BTSLUtil.isNullString(hostName) && !hostName.trim().equals(originHostName)) {
                    requestMap.put("responseCode", "1002");
                    return requestMap;
                }
                requestMap.put("originHostName", originHostName.trim());
            } else {
                faultCode = CS3Air22I.MANDATORY_PARAMETER_MISSING;
                faultString = "Manadatory parameter originHostName is Missing in the request";
                requestMap.put("faultCode", faultCode);
                requestMap.put("faultString", faultString);
                return requestMap;
            }
            indexEnd = p_requestStr.indexOf("</member>", indexStart);
            indexStart = p_requestStr.indexOf("<member>", indexEnd);
            tempIndex = p_requestStr.indexOf("originTransactionID", indexStart);
            if (tempIndex > 0) {
                String originTransactionID = p_requestStr.substring("<string>".length() + p_requestStr.indexOf("<string>", tempIndex), p_requestStr.indexOf("</string>", tempIndex)).trim();
                requestMap.put("originTransactionID", originTransactionID.trim());
            } else {
                faultCode = CS3Air22I.MANDATORY_PARAMETER_MISSING;
                faultString = "Manadatory parameter originTransactionID is Missing in the request";
                requestMap.put("faultCode", faultCode);
                requestMap.put("faultString", faultString);
                return requestMap;
            }
            indexEnd = p_requestStr.indexOf("</member>", indexStart);
            indexStart = p_requestStr.indexOf("<member>", indexEnd);
            tempIndex = p_requestStr.indexOf("originTimeStamp", indexStart);
            if (tempIndex > 0) {
                String originTimeStamp = p_requestStr.substring("<dateTime.iso8601>".length() + p_requestStr.indexOf("<dateTime.iso8601>", tempIndex), p_requestStr.indexOf("</dateTime.iso8601>", tempIndex)).trim();
                requestMap.put("originTimeStamp", originTimeStamp.trim());
            } else {
                faultCode = CS3Air22I.MANDATORY_PARAMETER_MISSING;
                faultString = "Manadatory parameter originTimeStamp is Missing in the request";
                requestMap.put("faultCode", faultCode);
                requestMap.put("faultString", faultString);
                return requestMap;
            }
            indexEnd = p_requestStr.indexOf("</member>", indexStart);
            indexStart = p_requestStr.indexOf("<member>", indexEnd);
            tempIndex = p_requestStr.indexOf("subscriberNumberNAI", indexStart);
            if (tempIndex > 0) {
                String subscriberNumberNAI = p_requestStr.substring("<i4>".length() + p_requestStr.indexOf("<i4>", tempIndex), p_requestStr.indexOf("</i4>", tempIndex)).trim();
                requestMap.put("subscriberNumberNAI", subscriberNumberNAI.trim());
            }
            indexEnd = p_requestStr.indexOf("</member>", indexStart);
            indexStart = p_requestStr.indexOf("<member>", indexEnd);
            tempIndex = p_requestStr.indexOf("subscriberNumber", indexStart);
            if (tempIndex > 0) {
                String subscriberNumber = p_requestStr.substring("<string>".length() + p_requestStr.indexOf("<string>", tempIndex), p_requestStr.indexOf("</string>", tempIndex)).trim();
                requestMap.put("subscriberNumber", subscriberNumber.trim());
            } else {
                faultCode = CS3Air22I.MANDATORY_PARAMETER_MISSING;
                faultString = "Manadatory parameter subscriberNumber is Missing in the request";
                requestMap.put("faultCode", faultCode);
                requestMap.put("faultString", faultString);
                return requestMap;
            }
            indexEnd = p_requestStr.indexOf("</member>", indexStart);
            indexStart = p_requestStr.indexOf("<member>", indexEnd);
            tempIndex = p_requestStr.indexOf("transactionAmountRefill", indexStart);
            if (tempIndex > 0) {
                String transactionAmountRefill = p_requestStr.substring("<string>".length() + p_requestStr.indexOf("<string>", tempIndex), p_requestStr.indexOf("</string>", tempIndex)).trim();
                requestMap.put("transactionAmountRefill", transactionAmountRefill.trim());
            } else {
                faultCode = CS3Air22I.MANDATORY_PARAMETER_MISSING;
                faultString = "Manadatory parameter transactionAmountRefill is Missing in the request";
                requestMap.put("faultCode", faultCode);
                requestMap.put("faultString", faultString);
                return requestMap;
            }
            indexEnd = p_requestStr.indexOf("</member>", indexStart);
            indexStart = p_requestStr.indexOf("<member>", indexEnd);
            tempIndex = p_requestStr.indexOf("transactionCurrency", indexStart);
            if (tempIndex > 0) {
                String transactionCurrency = p_requestStr.substring("<string>".length() + p_requestStr.indexOf("<string>", tempIndex), p_requestStr.indexOf("</string>", tempIndex)).trim();
                requestMap.put("transactionCurrency", transactionCurrency.trim());
            } else {
                faultCode = CS3Air22I.MANDATORY_PARAMETER_MISSING;
                faultString = "Manadatory parameter transactionCurrency is Missing in the request";
                requestMap.put("faultCode", faultCode);
                requestMap.put("faultString", faultString);
                return requestMap;
            }
            indexEnd = p_requestStr.indexOf("</member>", indexStart);
            indexStart = p_requestStr.indexOf("<member>", indexEnd);
            tempIndex = p_requestStr.indexOf("paymentProfileID", indexStart);
            if (tempIndex > 0) {
                String paymentProfileID = p_requestStr.substring("<string>".length() + p_requestStr.indexOf("<string>", tempIndex), p_requestStr.indexOf("</string>", tempIndex)).trim();
                serviceClass = (String) _responseMap.get("ALLWD_SERVICE_CLASS");
                if (!BTSLUtil.isNullString(serviceClass) && !serviceClass.trim().contains(paymentProfileID)) {
                    requestMap.put("responseCode", "1003");
                    return requestMap;
                }
                requestMap.put("paymentProfileID", paymentProfileID.trim());
            } else {
                faultCode = CS3Air22I.MANDATORY_PARAMETER_MISSING;
                faultString = "Manadatory parameter paymentProfileID is Missing in the request";
                requestMap.put("faultCode", faultCode);
                requestMap.put("faultString", faultString);
                return requestMap;
            }
            indexEnd = p_requestStr.indexOf("</member>", indexStart);
            indexStart = p_requestStr.indexOf("<member>", indexEnd);
            tempIndex = p_requestStr.indexOf("externalData1", indexStart);
            if (tempIndex > 0) {
                // String externalData1 =
                // p_requestStr.substring("<i4>".length()+p_requestStr.indexOf("<i4>",tempIndex),p_requestStr.indexOf("</i4>",tempIndex)).trim();
                String externalData1 = p_requestStr.substring("<string>".length() + p_requestStr.indexOf("<string>", tempIndex), p_requestStr.indexOf("</string>", tempIndex)).trim();
                requestMap.put("externalData1", externalData1.trim());
            }
            indexEnd = p_requestStr.indexOf("</member>", indexStart);
            indexStart = p_requestStr.indexOf("<member>", indexEnd);
            tempIndex = p_requestStr.indexOf("externalData2", indexStart);
            if (tempIndex > 0) {
                // String externalData2 =
                // p_requestStr.substring("<i4>".length()+p_requestStr.indexOf("<i4>",tempIndex),p_requestStr.indexOf("</i4>",tempIndex)).trim();
                String externalData2 = p_requestStr.substring("<string>".length() + p_requestStr.indexOf("<string>", tempIndex), p_requestStr.indexOf("</string>", tempIndex)).trim();
                requestMap.put("externalData2", externalData2.trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("parseRechargeCreditRequest", "Exception e:" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            _log.error("parseRechargeCreditRequest", "Exited requestMap: " + requestMap);
        }// end of finally
        return requestMap;
    }// end of parseRechargeCreditRequest

    /**
     * This method is used to parse the debit request.
     * 
     * @param String
     *            p_requestStr
     * @return HashMap
     * @throws Exception
     */
    public HashMap<String, String> parseImmediateDebitRequest(String p_requestStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseImmediateDebitRequest", "Entered p_requestStr " + p_requestStr);
        HashMap<String, String> requestMap = null;
        int indexStart = 0;
        int indexEnd = 0;
        int tempIndex = 0;
        String faultCode = null;
        String faultString = null;
        String hostName = null;
        String nodeType = null;
        try {
            requestMap = new HashMap<String, String>();
            indexStart = p_requestStr.indexOf("<member>");
            tempIndex = p_requestStr.indexOf("originNodeType", indexStart);
            if (tempIndex > 0) {
                String originNodeType = p_requestStr.substring("<string>".length() + p_requestStr.indexOf("<string>", tempIndex), p_requestStr.indexOf("</string>", tempIndex)).trim();
                nodeType = (String) _responseMap.get("NODE_TYPE");
                if (!BTSLUtil.isNullString(nodeType) && !nodeType.trim().equals(originNodeType)) {
                    requestMap.put("responseCode", "1001");
                    return requestMap;
                }
                requestMap.put("originNodeType", originNodeType.trim());
            } else {
                faultCode = CS3Air22I.MANDATORY_PARAMETER_MISSING;
                faultString = "Manadatory parameter originNodeType is Missing in the request";
                requestMap.put("faultCode", faultCode);
                requestMap.put("faultString", faultString);
                return requestMap;
            }
            indexStart = p_requestStr.indexOf("<member>", indexEnd);
            tempIndex = p_requestStr.indexOf("originHostName", indexStart);
            if (tempIndex > 0) {
                String originHostName = p_requestStr.substring("<string>".length() + p_requestStr.indexOf("<string>", tempIndex), p_requestStr.indexOf("</string>", tempIndex));
                hostName = (String) _responseMap.get("HOST_NAME").toString().trim();
                if (!BTSLUtil.isNullString(hostName) && !hostName.trim().equals(originHostName)) {
                    requestMap.put("responseCode", "1002");
                    return requestMap;
                }
                requestMap.put("originHostName", originHostName.trim());
            } else {
                faultCode = CS3Air22I.MANDATORY_PARAMETER_MISSING;
                faultString = "Manadatory parameter originHostName is Missing in the request";
                requestMap.put("faultCode", faultCode);
                requestMap.put("faultString", faultString);
                return requestMap;
            }
            indexEnd = p_requestStr.indexOf("</member>", indexStart);
            indexStart = p_requestStr.indexOf("<member>", indexEnd);
            tempIndex = p_requestStr.indexOf("originTransactionID", indexStart);
            if (tempIndex > 0) {
                String originTransactionID = p_requestStr.substring("<string>".length() + p_requestStr.indexOf("<string>", tempIndex), p_requestStr.indexOf("</string>", tempIndex)).trim();
                requestMap.put("originTransactionID", originTransactionID.trim());
            }
            indexEnd = p_requestStr.indexOf("</member>", indexStart);
            indexStart = p_requestStr.indexOf("<member>", indexEnd);
            tempIndex = p_requestStr.indexOf("originTimeStamp", indexStart);
            if (tempIndex > 0) {
                String originTimeStamp = p_requestStr.substring("<dateTime.iso8601>".length() + p_requestStr.indexOf("<dateTime.iso8601>", tempIndex), p_requestStr.indexOf("</dateTime.iso8601>", tempIndex)).trim();
                requestMap.put("originTimeStamp", originTimeStamp.trim());
            } else {
                faultCode = CS3Air22I.MANDATORY_PARAMETER_MISSING;
                faultString = "Manadatory parameter originTimeStamp is Missing in the request";
                requestMap.put("faultCode", faultCode);
                requestMap.put("faultString", faultString);
                return requestMap;
            }
            indexEnd = p_requestStr.indexOf("</member>", indexStart);
            indexStart = p_requestStr.indexOf("<member>", indexEnd);
            /*
             * tempIndex =
             * p_requestStr.indexOf("subscriberNumberNAI",indexStart);
             * if(tempIndex>0)
             * {
             * String subscriberNumberNAI =
             * p_requestStr.substring("<i4>".length(
             * )+p_requestStr.indexOf("<i4>"
             * ,tempIndex),p_requestStr.indexOf("</i4>",tempIndex)).trim();
             * requestMap.put("subscriberNumberNAI",subscriberNumberNAI.trim());
             * }
             * indexEnd = p_requestStr.indexOf("</member>",indexStart);
             * indexStart= p_requestStr.indexOf("<member>",indexEnd);
             */
            tempIndex = p_requestStr.indexOf("subscriberNumber", indexStart);
            if (tempIndex > 0) {
                String subscriberNumber = p_requestStr.substring("<string>".length() + p_requestStr.indexOf("<string>", tempIndex), p_requestStr.indexOf("</string>", tempIndex)).trim();
                requestMap.put("subscriberNumber", subscriberNumber.trim());
            } else {
                faultCode = CS3Air22I.MANDATORY_PARAMETER_MISSING;
                faultString = "Manadatory parameter subscriberNumber is Missing in the request";
                requestMap.put("faultCode", faultCode);
                requestMap.put("faultString", faultString);
                return requestMap;
            }
            indexEnd = p_requestStr.indexOf("</member>", indexStart);
            indexStart = p_requestStr.indexOf("<member>", indexEnd);
            tempIndex = p_requestStr.indexOf("messageCapabilityFlag", indexStart);
            if (tempIndex > 0) {
                String messageCapabilityFlag = p_requestStr.substring("<string>".length() + p_requestStr.indexOf("<string>", tempIndex), p_requestStr.indexOf("</string>", tempIndex)).trim();
                requestMap.put("transactionAmountRefill", messageCapabilityFlag.trim());
            }
            indexEnd = p_requestStr.indexOf("</member>", indexStart);
            indexStart = p_requestStr.indexOf("<member>", indexEnd);
            tempIndex = p_requestStr.indexOf("transactionCurrency", indexStart);
            if (tempIndex > 0) {
                String transactionCurrency = p_requestStr.substring("<string>".length() + p_requestStr.indexOf("<string>", tempIndex), p_requestStr.indexOf("</string>", tempIndex)).trim();
                requestMap.put("transactionCurrency", transactionCurrency.trim());
            } else {
                faultCode = CS3Air22I.MANDATORY_PARAMETER_MISSING;
                faultString = "Manadatory parameter transactionCurrency is Missing in the request";
                requestMap.put("faultCode", faultCode);
                requestMap.put("faultString", faultString);
                return requestMap;
            }
            indexEnd = p_requestStr.indexOf("</member>", indexStart);
            indexStart = p_requestStr.indexOf("<member>", indexEnd);
            tempIndex = p_requestStr.indexOf("adjustmentAmount", indexStart);
            if (tempIndex > 0) {
                String adjustmentAmount = p_requestStr.substring("<string>".length() + p_requestStr.indexOf("<string>", tempIndex), p_requestStr.indexOf("</string>", tempIndex)).trim();
                requestMap.put("adjustmentAmount", adjustmentAmount.trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("parseImmediateDebitRequest", "Exception e:" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseImmediateDebitRequest", "Exited requestMap: " + requestMap);
        }// end of finally
        return requestMap;
    }// end of parseImmediateDebitRequest
}
