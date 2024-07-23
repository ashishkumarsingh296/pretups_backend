package com.inter.cs3;

import java.util.HashMap;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

/**
 * @(#)CS3TestXMLParser
 *                      Copyright(c) 2006, Bharti Telesoft Int. Public Ltd.
 *                      All Rights Reserved
 *                      --------------------------------------------------------
 *                      -----------------------------------------
 *                      Author Date History
 *                      --------------------------------------------------------
 *                      -----------------------------------------
 *                      Ashish Kumar Sep 21, 2006 Initial Creation
 *                      --------------------------------------------------------
 *                      ----------------------------------------
 *                      This class is responsible to parse the xml request
 */
public class CS3TestXMLParser {
    public Log _log = LogFactory.getLog(this.getClass().getName());
    private HashMap responseMap = null;

    public CS3TestXMLParser() {

    }

    public CS3TestXMLParser(HashMap p_responseMap) {
        this.responseMap = p_responseMap;
    }

    /**
     * This method implements the logic to parse the request xml string of
     * account info.
     * 
     * @param String
     *            p_requestStr
     * @return HashMap
     * @throws Exception
     */
    public HashMap parseGetAccountInformation(String p_requestStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseGetAccountInformation", "Entered p_requestStr: " + p_requestStr);
        HashMap requestMap = null;
        int indexStart = 0;
        int indexEnd = 0;
        int tempIndex = 0;
        String hostName = null;
        String nodeType = null;
        String faultCode = "";
        String faultString = "";
        try {
            requestMap = new HashMap();
            indexStart = p_requestStr.indexOf("<member>");
            tempIndex = p_requestStr.indexOf("originNodeType", indexStart);
            if (tempIndex > 0) {
                String originNodeType = p_requestStr.substring("<string>".length() + p_requestStr.indexOf("<string>", tempIndex), p_requestStr.indexOf("</string>", tempIndex)).trim();
                nodeType = (String) responseMap.get("NODE_TYPE");
                if (!BTSLUtil.isNullString(nodeType) && !nodeType.trim().equals(originNodeType)) {
                    requestMap.put("responseCode", "1001");
                    return requestMap;
                }
                requestMap.put("originNodeType", originNodeType.trim());
            } else {
                faultCode = CS3I.MANDATORY_FILED_MISSING;
                faultString = "Manadatory parameter originNodeType is Missing in the request";
                requestMap.put("faultCode", faultCode);
                requestMap.put("faultString", faultString);
                return requestMap;
            }
            indexStart = p_requestStr.indexOf("<member>", indexEnd);
            tempIndex = p_requestStr.indexOf("originHostName", indexStart);
            if (tempIndex > 0) {
                String originHostName = p_requestStr.substring("<string>".length() + p_requestStr.indexOf("<string>", tempIndex), p_requestStr.indexOf("</string>", tempIndex));
                hostName = (String) responseMap.get("PreTups");
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
                faultCode = CS3I.MANDATORY_FILED_MISSING;
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
                faultCode = CS3I.MANDATORY_FILED_MISSING;
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
                faultCode = CS3I.MANDATORY_FILED_MISSING;
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
     * This method is used parse the Recharge request.
     * 
     * @param String
     *            p_requestStr
     * @return HashMap
     * @throws Exception
     */
    public HashMap parseRechargeCreditRequest(String p_requestStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseRechargeCreditRequest", "Entered p_requestStr: " + p_requestStr);
        HashMap requestMap = null;
        int indexStart = 0;
        int indexEnd = 0;
        int tempIndex = 0;
        String faultCode = null;
        String faultString = null;
        String hostName = "EXT";
        String nodeType = "PreTups";
        String serviceClass = null;
        try {
            requestMap = new HashMap();
            indexStart = p_requestStr.indexOf("<member>");
            tempIndex = p_requestStr.indexOf("originNodeType", indexStart);
            if (tempIndex > 0) {
                String originNodeType = p_requestStr.substring("<string>".length() + p_requestStr.indexOf("<string>", tempIndex), p_requestStr.indexOf("</string>", tempIndex)).trim();
                nodeType = (String) responseMap.get("NODE_TYPE");
                if (!BTSLUtil.isNullString(nodeType) && !nodeType.trim().equals(originNodeType)) {
                    requestMap.put("responseCode", "1001");
                    return requestMap;
                }
                requestMap.put("originNodeType", originNodeType.trim());
            } else {
                faultCode = CS3I.MANDATORY_FILED_MISSING;
                faultString = "Manadatory parameter originNodeType is Missing in the request";
                requestMap.put("faultCode", faultCode);
                requestMap.put("faultString", faultString);
                return requestMap;
            }
            indexStart = p_requestStr.indexOf("<member>", indexEnd);
            tempIndex = p_requestStr.indexOf("originHostName", indexStart);
            if (tempIndex > 0) {
                String originHostName = p_requestStr.substring("<string>".length() + p_requestStr.indexOf("<string>", tempIndex), p_requestStr.indexOf("</string>", tempIndex));
                hostName = (String) responseMap.get("PreTups");
                if (!BTSLUtil.isNullString(hostName) && !hostName.trim().equals(originHostName)) {
                    requestMap.put("responseCode", "1002");
                    return requestMap;
                }
                requestMap.put("originHostName", originHostName.trim());
            } else {
                faultCode = CS3I.MANDATORY_FILED_MISSING;
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
                faultCode = CS3I.MANDATORY_FILED_MISSING;
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
                faultCode = CS3I.MANDATORY_FILED_MISSING;
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
                faultCode = CS3I.MANDATORY_FILED_MISSING;
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
                faultCode = CS3I.MANDATORY_FILED_MISSING;
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
                faultCode = CS3I.MANDATORY_FILED_MISSING;
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
                serviceClass = (String) responseMap.get("ALLWD_SERVICE_CLASS");
                if (!BTSLUtil.isNullString(serviceClass) && !serviceClass.trim().contains(paymentProfileID)) {
                    requestMap.put("responseCode", "1003");
                    return requestMap;
                }
                requestMap.put("paymentProfileID", paymentProfileID.trim());
            } else {
                faultCode = CS3I.MANDATORY_FILED_MISSING;
                faultString = "Manadatory parameter paymentProfileID is Missing in the request";
                requestMap.put("faultCode", faultCode);
                requestMap.put("faultString", faultString);
                return requestMap;
            }
            indexEnd = p_requestStr.indexOf("</member>", indexStart);
            indexStart = p_requestStr.indexOf("<member>", indexEnd);
            tempIndex = p_requestStr.indexOf("externalData1", indexStart);
            if (tempIndex > 0) {
                String externalData1 = p_requestStr.substring("<i4>".length() + p_requestStr.indexOf("<i4>", tempIndex), p_requestStr.indexOf("</i4>", tempIndex)).trim();
                requestMap.put("externalData1", externalData1.trim());
            }
            indexEnd = p_requestStr.indexOf("</member>", indexStart);
            indexStart = p_requestStr.indexOf("<member>", indexEnd);
            tempIndex = p_requestStr.indexOf("externalData2", indexStart);
            if (tempIndex > 0) {
                String externalData2 = p_requestStr.substring("<i4>".length() + p_requestStr.indexOf("<i4>", tempIndex), p_requestStr.indexOf("</i4>", tempIndex)).trim();
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
    public HashMap parseImmediateDebitRequest(String p_requestStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseImmediateDebitRequest", "Entered p_requestStr " + p_requestStr);
        HashMap requestMap = null;
        int indexStart = 0;
        int indexEnd = 0;
        int tempIndex = 0;
        String faultCode = null;
        String faultString = null;
        String hostName = null;
        String nodeType = null;
        try {

            requestMap = new HashMap();
            indexStart = p_requestStr.indexOf("<member>");
            tempIndex = p_requestStr.indexOf("originNodeType", indexStart);
            if (tempIndex > 0) {
                String originNodeType = p_requestStr.substring("<string>".length() + p_requestStr.indexOf("<string>", tempIndex), p_requestStr.indexOf("</string>", tempIndex)).trim();
                nodeType = (String) responseMap.get("NODE_TYPE");
                if (!BTSLUtil.isNullString(nodeType) && !nodeType.trim().equals(originNodeType)) {
                    requestMap.put("responseCode", "1001");
                    return requestMap;
                }
                requestMap.put("originNodeType", originNodeType.trim());
            } else {
                faultCode = CS3I.MANDATORY_FILED_MISSING;
                faultString = "Manadatory parameter originNodeType is Missing in the request";
                requestMap.put("faultCode", faultCode);
                requestMap.put("faultString", faultString);
                return requestMap;
            }
            indexStart = p_requestStr.indexOf("<member>", indexEnd);
            tempIndex = p_requestStr.indexOf("originHostName", indexStart);
            if (tempIndex > 0) {
                String originHostName = p_requestStr.substring("<string>".length() + p_requestStr.indexOf("<string>", tempIndex), p_requestStr.indexOf("</string>", tempIndex));
                hostName = (String) responseMap.get("HOST_NAME").toString().trim();
                if (!BTSLUtil.isNullString(hostName) && !hostName.trim().equals(originHostName)) {
                    requestMap.put("responseCode", "1002");
                    return requestMap;
                }
                requestMap.put("originHostName", originHostName.trim());
            } else {
                faultCode = CS3I.MANDATORY_FILED_MISSING;
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
                faultCode = CS3I.MANDATORY_FILED_MISSING;
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
                faultCode = CS3I.MANDATORY_FILED_MISSING;
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
                faultCode = CS3I.MANDATORY_FILED_MISSING;
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

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        /*
         * CS3TestXMLParser testReqParser = new CS3TestXMLParser();
         * String requestStr= null;
         * StringBuffer stringBuffer = null;
         * String lineSep = System.getProperty("line.separator");
         * try
         * {
         * stringBuffer=new StringBuffer(1028);
         * 
         * //AdjustRequest
         * stringBuffer.append("<?xml version='1.0'?>"+lineSep);
         * stringBuffer.append("<methodCall>"+lineSep);
         * stringBuffer.append("<methodName>RefillTRequest</methodName>"+lineSep)
         * ;
         * stringBuffer.append("<params>"+lineSep);
         * stringBuffer.append("<param>"+lineSep);
         * stringBuffer.append("<value>"+lineSep);
         * stringBuffer.append("<struct>"+lineSep);
         * //Set the originNodeType
         * stringBuffer.append("<member>"+lineSep);
         * stringBuffer.append("<name>originNodeType</name>"+lineSep);
         * stringBuffer.append("<value><string>EXT</string></value>"+lineSep);
         * stringBuffer.append("</member>"+lineSep);
         * //Set the originHostName
         * stringBuffer.append("<member>"+lineSep);
         * stringBuffer.append("<name>originHostName</name>"+lineSep);
         * stringBuffer.append("<value><string>PreTups</string></value>"+lineSep)
         * ;
         * stringBuffer.append("</member>"+lineSep);
         * stringBuffer.append("<member>"+lineSep);
         * //Set the originTransactionID
         * stringBuffer.append("<name>originTransactionID</name>"+lineSep);
         * stringBuffer.append("<value><string>0123456789</string></value>"+lineSep
         * );
         * stringBuffer.append("</member>"+lineSep);
         * //Set the originTimeStamp
         * stringBuffer.append("<member>"+lineSep);
         * stringBuffer.append("<name>originTimeStamp</name>"+lineSep);
         * stringBuffer.append(
         * "<value><dateTime.iso8601>20050913T10:41:00+0600</dateTime.iso8601></value>"
         * +lineSep);
         * stringBuffer.append("</member>"+lineSep);
         * //Set the optional parameter subscriberNumberNAI if present.
         * 
         * stringBuffer.append("<member>"+lineSep);
         * stringBuffer.append("<name>subscriberNumberNAI</name>"+lineSep);
         * stringBuffer.append("<value><i4>9868647394</i4></value>"+lineSep);
         * stringBuffer.append("</member>"+lineSep);
         * //Set the subscriberNumber after adding or removing the prefix
         * defined in the INFile.
         * stringBuffer.append("<member>"+lineSep);
         * stringBuffer.append("<name>subscriberNumber</name>"+lineSep);
         * stringBuffer.append("<value><string>098687394</string></value>"+lineSep
         * );
         * stringBuffer.append("</member>"+lineSep);
         * stringBuffer.append("<member>"+lineSep);
         * stringBuffer.append("<name>messageCapabilityFlag</name>"+lineSep);
         * stringBuffer.append("<value><string>OPTIONAL</string></value>"+lineSep
         * );
         * stringBuffer.append("</member>"+lineSep);
         * stringBuffer.append("<member>"+lineSep);
         * stringBuffer.append("<name>transactionCurrency</name>"+lineSep);
         * stringBuffer.append("<value><string>DPL</string></value>"+lineSep);
         * stringBuffer.append("</member>"+lineSep);
         * 
         * stringBuffer.append("<member>"+lineSep);
         * stringBuffer.append("<name>adjustmentAmount</name>"+lineSep);
         * stringBuffer.append("<value><string>200</string></value>"+lineSep);
         * stringBuffer.append("</member>"+lineSep);
         * 
         * stringBuffer.append("<member>"+lineSep);
         * stringBuffer.append("<name>relativeDateAdjustmentSupervision</name>"+
         * lineSep);
         * stringBuffer.append("<value><i4>12987</i4></value>"+lineSep);
         * stringBuffer.append("</member>"+lineSep);
         * 
         * stringBuffer.append("<member>"+lineSep);
         * stringBuffer.append("<name>relativeDateAdjustmentServiceFee</name>"+
         * lineSep);
         * stringBuffer.append("<value><i4>12987</i4></value>"+lineSep);
         * stringBuffer.append("</member>"+lineSep);
         * 
         * stringBuffer.append("<member>"+lineSep);
         * stringBuffer.append("<name>externalData1</name>"+lineSep);
         * stringBuffer.append("<value><i4>12987</i4></value>"+lineSep);
         * stringBuffer.append("</member>"+lineSep);
         * 
         * stringBuffer.append("<member>"+lineSep);
         * stringBuffer.append("<name>externalData2</name>"+lineSep);
         * stringBuffer.append("<value><i4>12987</i4></value>"+lineSep);
         * stringBuffer.append("</member>"+lineSep);
         * stringBuffer.append("</struct>"+lineSep);
         * stringBuffer.append("</value>"+lineSep);
         * stringBuffer.append("</param>"+lineSep);
         * stringBuffer.append("</params>"+lineSep);
         * stringBuffer.append("</methodCall>"+lineSep);
         * requestStr = stringBuffer.toString();
         * 
         * 
         * 
         * testReqParser.parseImmediateDebitRequest(requestStr);
         * 
         * //GetAccountDetailsTRequest
         * stringBuffer.append("<?xml version='1.0'?>"+lineSep);
         * stringBuffer.append("<methodCall>"+lineSep);
         * stringBuffer.append("<methodName>GetAccountDetailsTRequest</methodName>"
         * +lineSep);
         * stringBuffer.append("<params>"+lineSep);
         * stringBuffer.append("<param>"+lineSep);
         * stringBuffer.append("<value>"+lineSep);
         * stringBuffer.append("<struct>"+lineSep);
         * //Set the originNodeType
         * stringBuffer.append("<member>"+lineSep);
         * stringBuffer.append("<name>originNodeType</name>"+lineSep);
         * stringBuffer.append("<value><string>EXT</string></value>"+lineSep);
         * stringBuffer.append("</member>"+lineSep);
         * //Set the originHostName
         * stringBuffer.append("<member>"+lineSep);
         * stringBuffer.append("<name>originHostName</name>"+lineSep);
         * stringBuffer.append("<value><string>PreTups</string></value>"+lineSep)
         * ;
         * stringBuffer.append("</member>"+lineSep);
         * stringBuffer.append("<member>"+lineSep);
         * //Set the originTransactionID
         * stringBuffer.append("<name>originTransactionID</name>"+lineSep);
         * stringBuffer.append("<value><string>0123456789</string></value>"+lineSep
         * );
         * stringBuffer.append("</member>"+lineSep);
         * //Set the originTimeStamp
         * stringBuffer.append("<member>"+lineSep);
         * stringBuffer.append("<name>originTimeStamp</name>"+lineSep);
         * stringBuffer.append(
         * "<value><dateTime.iso8601>20050913T10:41:00+0600</dateTime.iso8601></value>"
         * +lineSep);
         * stringBuffer.append("</member>"+lineSep);
         * //Set the optional parameter subscriberNumberNAI if present.
         * 
         * stringBuffer.append("<member>"+lineSep);
         * stringBuffer.append("<name>subscriberNumberNAI</name>"+lineSep);
         * stringBuffer.append("<value><i4>9868647394</i4></value>"+lineSep);
         * stringBuffer.append("</member>"+lineSep);
         * //Set the subscriberNumber after adding or removing the prefix
         * defined in the INFile.
         * stringBuffer.append("<member>"+lineSep);
         * stringBuffer.append("<name>subscriberNumber</name>"+lineSep);
         * stringBuffer.append("<value><string>098687394</string></value>"+lineSep
         * );
         * stringBuffer.append("</member>"+lineSep);
         * //Set the optional parameter messageCapabilityFlag,if present.
         * 
         * stringBuffer.append("<member>"+lineSep);
         * stringBuffer.append("<name>messageCapabilityFlag</name>"+lineSep);
         * stringBuffer.append("<value><string>NOT REQUIRED</string></value>"+
         * lineSep);
         * stringBuffer.append("</member>"+lineSep);
         * stringBuffer.append("</struct>"+lineSep);
         * stringBuffer.append("</value>"+lineSep);
         * stringBuffer.append("</param>"+lineSep);
         * stringBuffer.append("</params>"+lineSep);
         * stringBuffer.append("</methodCall>"+lineSep);
         * requestStr = stringBuffer.toString();
         * 
         * testReqParser.parseGetAccountInformation(requestStr);
         * 
         * 
         * //RefillTRequest
         * stringBuffer.append("<?xml version='1.0'?>"+lineSep);
         * stringBuffer.append("<methodCall>"+lineSep);
         * stringBuffer.append("<methodName>RefillTRequest</methodName>"+lineSep)
         * ;
         * stringBuffer.append("<params>"+lineSep);
         * stringBuffer.append("<param>"+lineSep);
         * stringBuffer.append("<value>"+lineSep);
         * stringBuffer.append("<struct>"+lineSep);
         * //Set the originNodeType
         * stringBuffer.append("<member>"+lineSep);
         * stringBuffer.append("<name>originNodeType</name>"+lineSep);
         * stringBuffer.append("<value><string>EXT</string></value>"+lineSep);
         * stringBuffer.append("</member>"+lineSep);
         * //Set the originHostName
         * stringBuffer.append("<member>"+lineSep);
         * stringBuffer.append("<name>originHostName</name>"+lineSep);
         * stringBuffer.append("<value><string>PreTups</string></value>"+lineSep)
         * ;
         * stringBuffer.append("</member>"+lineSep);
         * stringBuffer.append("<member>"+lineSep);
         * //Set the originTransactionID
         * stringBuffer.append("<name>originTransactionID</name>"+lineSep);
         * stringBuffer.append("<value><string>0123456789</string></value>"+lineSep
         * );
         * stringBuffer.append("</member>"+lineSep);
         * //Set the originTimeStamp
         * stringBuffer.append("<member>"+lineSep);
         * stringBuffer.append("<name>originTimeStamp</name>"+lineSep);
         * stringBuffer.append(
         * "<value><dateTime.iso8601>20050913T10:41:00+0600</dateTime.iso8601></value>"
         * +lineSep);
         * stringBuffer.append("</member>"+lineSep);
         * //Set the optional parameter subscriberNumberNAI if present.
         * 
         * stringBuffer.append("<member>"+lineSep);
         * stringBuffer.append("<name>subscriberNumberNAI</name>"+lineSep);
         * stringBuffer.append("<value><i4>9868647394</i4></value>"+lineSep);
         * stringBuffer.append("</member>"+lineSep);
         * //Set the subscriberNumber after adding or removing the prefix
         * defined in the INFile.
         * stringBuffer.append("<member>"+lineSep);
         * stringBuffer.append("<name>subscriberNumber</name>"+lineSep);
         * stringBuffer.append("<value><string>098687394</string></value>"+lineSep
         * );
         * stringBuffer.append("</member>"+lineSep);
         * 
         * stringBuffer.append("<member>"+lineSep);
         * stringBuffer.append("<name>transactionAmountRefill</name>"+lineSep);
         * stringBuffer.append("<value><string>100</string></value>"+lineSep);
         * stringBuffer.append("</member>"+lineSep);
         * 
         * stringBuffer.append("<member>"+lineSep);
         * stringBuffer.append("<name>transactionCurrency</name>"+lineSep);
         * stringBuffer.append("<value><string>DLP</string></value>"+lineSep);
         * stringBuffer.append("</member>"+lineSep);
         * 
         * 
         * stringBuffer.append("<member>"+lineSep);
         * stringBuffer.append("<name>externalData1</name>"+lineSep);
         * stringBuffer.append("<value><i4>123456</i4></value>"+lineSep);
         * stringBuffer.append("</member>"+lineSep);
         * 
         * stringBuffer.append("<member>"+lineSep);
         * stringBuffer.append("<name>externalData2</name>"+lineSep);
         * stringBuffer.append("<value><i4>123456</i4></value>"+lineSep);
         * stringBuffer.append("</member>"+lineSep);
         * 
         * stringBuffer.append("</struct>"+lineSep);
         * stringBuffer.append("</value>"+lineSep);
         * stringBuffer.append("</param>"+lineSep);
         * stringBuffer.append("</params>"+lineSep);
         * stringBuffer.append("</methodCall>"+lineSep);
         * 
         * requestStr = stringBuffer.toString();
         * 
         * testReqParser.parseRechargeCreditRequest(requestStr);
         * 
         * }
         * catch (Exception e)
         * {
         * e.printStackTrace();
         * System.out.println("Main Exception e::"+e.getMessage());
         * }
         */
    }
}
