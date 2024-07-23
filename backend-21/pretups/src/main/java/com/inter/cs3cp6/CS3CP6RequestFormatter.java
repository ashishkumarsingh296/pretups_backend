/*
 * Created on May 1, 2008
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.cs3cp6;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.module.InterfaceUtil;

/**
 * @author dhirCS3CP6RequestFormatterODO To change the template for this
 *         generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class CS3CP6RequestFormatter {
    public static Log _log = LogFactory.getLog(CS3CP6RequestFormatter.class);
    private SimpleDateFormat _sdf = null;
    private TimeZone _timeZone = null;
    private String _transDateFormat = "yyyyMMdd'T'HH:mm:ss";// Defines the Date
                                                            // and time format
                                                            // of CS3MobinilIN.
    private DecimalFormat _twoDigits = null;
    private int _offset;
    private String _sign = "+";
    private int _hours;
    private int _minutes;
    private static int _counter = 0;
    private static long _prevReqTime = 0;

    public CS3CP6RequestFormatter() throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("CS3CP6RequestFormatter[constructor]", "Entered");
        try {
            _sdf = new SimpleDateFormat(_transDateFormat);
            _timeZone = TimeZone.getDefault();
            _sdf.setTimeZone(_timeZone);
            _twoDigits = new DecimalFormat("00");
            _offset = _sdf.getTimeZone().getOffset(new Date().getTime());
            if (_offset < 0) {
                _offset = -_offset;
                _sign = "-";
            }
            _hours = _offset / 3600000;
            _minutes = (_offset - _hours * 3600000) / 60000;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("CS3CP6RequestFormatter[constructor]", "Exception e::" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("CS3CP6RequestFormatter[constructor]", "Exited");
        }// end of finally
    }// end of CS3CP6RequestFormatter[constructor]

    /**
     * This method is used to parse the response string based on the type of
     * Action.
     * 
     * @param int p_action
     * @param HashMap
     *            p_map
     * @return String.
     * @throws Exception
     */
    protected String generateRequest(int p_action, HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateRequest", "Entered p_action::" + p_action + " map::" + p_map);
        String str = null;
        p_map.put("action", String.valueOf(p_action));
        try {
            switch (p_action) {

            case CS3CP6I.ACTION_ACCOUNT_DETAILS: {
                str = generateGetAccountDetailsRequest(p_map);
                break;
            }
            case CS3CP6I.ACTION_BALANCE_DATE: {
                str = generateGetBalanceAndDateRequest(p_map);
                break;
            }
            case CS3CP6I.ACTION_RECHARGE_CREDIT: {
                str = generateRechargeCreditRequest(p_map);
                break;
            }
            case CS3CP6I.ACTION_IMMEDIATE_DEBIT: {
                str = generateImmediateDebitRequest(p_map);
                break;
            }
            }// end of switch block
        }// end of try block
        catch (Exception e) {
            _log.error("generateRequest", "Exception e ::" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("generateRequest", "Exited Request String: str::" + str);
        }// end of finally
        return str;
    }// end of generateRequest

    /**
     * This method is used to parse the response.
     * 
     * @param int p_action
     * @param String
     *            p_responseStr
     * @return HashMap
     * @throws Exception
     */
    public HashMap parseResponse(int p_action, String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseResponse", "Entered p_action::" + p_action + " p_responseStr:: " + p_responseStr);
        HashMap map = null;
        try {
            switch (p_action) {
            case CS3CP6I.ACTION_BALANCE_DATE: {
                map = parseGetAccountInfoResponse(p_responseStr);
                break;
            }
            case CS3CP6I.ACTION_RECHARGE_CREDIT: {
                map = parseRechargeCreditResponse(p_responseStr);
                break;
            }
            case CS3CP6I.ACTION_IMMEDIATE_DEBIT: {
                map = parseImmediateDebitResponse(p_responseStr);
                break;
            }
            }// end of switch block
        }// end of try block
        catch (Exception e) {
            _log.error("parseResponse", "Exception e::" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseResponse", "Exiting map::" + map);
        }// end of finally
        return map;
    }// end of parseResponse

    /**
     * This method is used to generate the request for getting account
     * information along with balance but no firstIVRCall,AccountStatus details
     * .
     * 
     * @param HashMap
     *            p_requestMap
     * @return String
     * @throws Exception
     */
    private String generateGetBalanceAndDateRequest(HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateGetBalanceAndDateRequest", "Entered p_requestMap::" + p_requestMap);
        String requestStr = null;
        StringBuffer stringBuffer = null;
        String inTransactionID = null;
        try {
            stringBuffer = new StringBuffer(1028);
            stringBuffer.append("<?xml version='1.0'?>");
            stringBuffer.append("<methodCall>");
            stringBuffer.append("<methodName>GetBalanceAndDate</methodName>");
            stringBuffer.append(startRequestTag());
            stringBuffer.append("<member>");
            stringBuffer.append("<name>originNodeType</name>");
            stringBuffer.append("<value><string>" + p_requestMap.get("NODE_TYPE") + "</string></value>");
            stringBuffer.append("</member>");
            // Set the originHostName
            stringBuffer.append("<member>");
            stringBuffer.append("<name>originHostName</name>");
            stringBuffer.append("<value><string>" + p_requestMap.get("HOST_NAME") + "</string></value>");
            stringBuffer.append("</member>");
            stringBuffer.append("<member>");
            stringBuffer.append("<name>originTransactionID</name>");
            stringBuffer.append("<value><string>" + (String) p_requestMap.get("IN_RECON_ID") + "</string></value>");
            stringBuffer.append("</member>");
            // Set the originTimeStamp
            stringBuffer.append("<member>");
            stringBuffer.append("<name>originTimeStamp</name>");
            stringBuffer.append("<value><dateTime.iso8601>" + getCS3CP6TransDateTime() + "</dateTime.iso8601></value>");
            stringBuffer.append("</member>");
            // Set the optional parameter subscriberNumberNAI if present.
            stringBuffer.append("<member>");
            stringBuffer.append("<name>subscriberNumberNAI</name>");
            stringBuffer.append("<value><i4>" + p_requestMap.get("SubscriberNumberNAI") + "</i4></value>");
            stringBuffer.append("</member>");

            // Set the subscriberNumber after adding or removing the prefix
            // defined in the INFile.
            stringBuffer.append("<member>");
            stringBuffer.append("<name>subscriberNumber</name>");
            stringBuffer.append("<value><string>" + InterfaceUtil.getFilterMSISDN((String) p_requestMap.get("INTERFACE_ID"), (String) p_requestMap.get("MSISDN")) + "</string></value>");
            stringBuffer.append("</member>");
            stringBuffer.append(endRequestTag());
            stringBuffer.append("</methodCall>");
            requestStr = stringBuffer.toString();
        }// end of try-block
        catch (Exception e) {
            _log.error("generateGetBalanceAndDateRequest", "Exception e::" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("generateGetBalanceAndDateRequest", "Exiting Request String:requestStr::" + requestStr);
        }// end of finally
        return requestStr;
    }// end of generateGetBalanceAndDateRequest

    /**
     * 
     * @param p_map
     * @return
     * @throws Exception
     */
    private String generateRechargeCreditRequest(HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateRechargeCreditRequest", "Entered p_requestMap::" + p_requestMap);
        String requestStr = null;
        StringBuffer stringBuffer = null;
        String inTransactionID = null;
        try {
            stringBuffer = new StringBuffer(1028);
            stringBuffer.append("<?xml version='1.0'?>");
            stringBuffer.append("<methodCall>");
            stringBuffer.append("<methodName>Refill</methodName>");
            stringBuffer.append(startRequestTag());
            // Set the origin originNodeType
            stringBuffer.append("<member>");
            stringBuffer.append("<name>originNodeType</name>");
            stringBuffer.append("<value><string>" + p_requestMap.get("NODE_TYPE") + "</string></value>");
            stringBuffer.append("</member>");
            // Set the originHostName
            stringBuffer.append("<member>");
            stringBuffer.append("<name>originHostName</name>");
            stringBuffer.append("<value><string>" + p_requestMap.get("HOST_NAME") + "</string></value>");
            stringBuffer.append("</member>");
            stringBuffer.append("<member>");
            stringBuffer.append("<name>originTransactionID</name>");
            stringBuffer.append("<value><string>" + (String) p_requestMap.get("IN_RECON_ID") + "</string></value>");
            stringBuffer.append("</member>");
            // Set the originTimeStamp
            stringBuffer.append("<member>");
            stringBuffer.append("<name>originTimeStamp</name>");
            stringBuffer.append("<value><dateTime.iso8601>" + getCS3CP6TransDateTime() + "</dateTime.iso8601></value>");
            stringBuffer.append("</member>");
            // Check the optional value of SubscriberNumberNAI if it is not null
            // set it.
            stringBuffer.append("<member>");
            stringBuffer.append("<name>subscriberNumberNAI</name>");
            stringBuffer.append("<value><i4>" + p_requestMap.get("SubscriberNumberNAI") + "</i4></value>");
            stringBuffer.append("</member>");
            // Set subscriberNumber and add or remove the msisdn if required.
            stringBuffer.append("<member>");
            stringBuffer.append("<name>subscriberNumber</name>");
            stringBuffer.append("<value><string>" + InterfaceUtil.getFilterMSISDN((String) p_requestMap.get("INTERFACE_ID"), (String) p_requestMap.get("MSISDN")) + "</string></value>");
            stringBuffer.append("</member>");
            if (!InterfaceUtil.isNullString((String) p_requestMap.get("ExternalData1"))) {
                stringBuffer.append("<member>");
                stringBuffer.append("<name>externalData1</name>");
                stringBuffer.append("<value><string>" + p_requestMap.get("ExternalData1") + "</string></value>");
                stringBuffer.append("</member>");
            }
            // Set the optional value of ExternalData2, if required.
            if (!InterfaceUtil.isNullString((String) p_requestMap.get("ExternalData2"))) {
                stringBuffer.append("<member>");
                stringBuffer.append("<name>externalData2</name>");
                stringBuffer.append("<value><string>" + p_requestMap.get("ExternalData2") + "</string></value>");
                stringBuffer.append("</member>");
            }
            stringBuffer.append("<member>");
            stringBuffer.append("<name>requestRefillAccountAfterFlag</name>");
            stringBuffer.append("<value><boolean>" + p_requestMap.get("REFILL_ACNT_AFTER_FLAG") + "</boolean></value>");
            stringBuffer.append("</member>");

            /*
             * stringBuffer.append("<member>");
             * stringBuffer.append("<name>requestRefillAccountBeforeFlag</name>")
             * ;
             * stringBuffer.append("<value><boolean>"+p_requestMap.get(
             * "REFILL_ACNT_B4_FLAG")+"</boolean></value>");
             * stringBuffer.append("</member>");
             */

            // Set the transfer_amount to transactionAmount
            stringBuffer.append("<member>");
            stringBuffer.append("<name>transactionAmount</name>");
            stringBuffer.append("<value><string>" + p_requestMap.get("transfer_amount") + "</string></value>");
            stringBuffer.append("</member>");
            // Set the transaction_currency defined into INFile for
            // transactionCurrency.
            stringBuffer.append("<member>");
            stringBuffer.append("<name>transactionCurrency</name>");
            stringBuffer.append("<value><string>" + p_requestMap.get("CURRENCY") + "</string></value>");
            stringBuffer.append("</member>");
            // Set the card group for paymentProfileID
            stringBuffer.append("<member>");
            stringBuffer.append("<name>refillProfileID</name>");
            stringBuffer.append("<value><string>" + p_requestMap.get("CARD_GROUP") + "</string></value>");
            stringBuffer.append("</member>");
            stringBuffer.append(endRequestTag());
            stringBuffer.append("</methodCall>");
            requestStr = stringBuffer.toString();

        }// end of try-block
        catch (Exception e) {
            _log.error("generateRechargeCreditRequest", "Exception e: " + e);
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("generateRechargeCreditRequest", "Exiting Request requestStr::" + requestStr);
        }// end of finally
        return requestStr;
    }// end of generateRechargeCreditRequest

    /**
     * 
     * @param map
     * @return
     * @throws Exception
     */
    private String generateImmediateDebitRequest(HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateImmediateDebitRequest", "Entered p_requestMap::" + p_requestMap);
        String requestStr = null;
        StringBuffer stringBuffer = null;
        String inTransactionID = null;
        try {
            stringBuffer = new StringBuffer(1028);
            stringBuffer.append("<?xml version='1.0'?>");
            stringBuffer.append("<methodCall>");
            stringBuffer.append("<methodName>UpdateBalanceAndDate</methodName>");
            stringBuffer.append(startRequestTag());
            // Set the origin originNodeType
            stringBuffer.append("<member>");
            stringBuffer.append("<name>originNodeType</name>");
            stringBuffer.append("<value><string>" + p_requestMap.get("NODE_TYPE") + "</string></value>");
            stringBuffer.append("</member>");
            // Set the originHostName
            stringBuffer.append("<member>");
            stringBuffer.append("<name>originHostName</name>");
            stringBuffer.append("<value><string>" + p_requestMap.get("HOST_NAME") + "</string></value>");
            stringBuffer.append("</member>");
            stringBuffer.append("<member>");
            // Set the originTransactionID
            stringBuffer.append("<name>originTransactionID</name>");
            stringBuffer.append("<value><string>" + (String) p_requestMap.get("IN_RECON_ID") + "</string></value>");
            stringBuffer.append("</member>");
            // Set the originTimeStamp
            stringBuffer.append("<member>");
            stringBuffer.append("<name>originTimeStamp</name>");
            stringBuffer.append("<value><dateTime.iso8601>" + getCS3CP6TransDateTime() + "</dateTime.iso8601></value>");
            stringBuffer.append("</member>");
            // Check the optional value of SubscriberNumberNAI if it is not null
            // set it.
            stringBuffer.append("<member>");
            stringBuffer.append("<name>subscriberNumberNAI</name>");
            stringBuffer.append("<value><i4>" + p_requestMap.get("SubscriberNumberNAI") + "</i4></value>");
            stringBuffer.append("</member>");
            // Set subscriberNumber and add or remove the msisdn if required.
            stringBuffer.append("<member>");
            stringBuffer.append("<name>subscriberNumber</name>");
            stringBuffer.append("<value><string>" + InterfaceUtil.getFilterMSISDN((String) p_requestMap.get("INTERFACE_ID"), (String) p_requestMap.get("MSISDN")) + "</string></value>");
            stringBuffer.append("</member>");
            // Set the transfer_amount to transactionAmount
            stringBuffer.append("<member>");
            stringBuffer.append("<name>adjustmentAmountRelative</name>");
            stringBuffer.append("<value><string>" + p_requestMap.get("transfer_amount") + "</string></value>");
            stringBuffer.append("</member>");

            // Set the transaction_currency defined into INFile for
            // transactionCurrency.
            stringBuffer.append("<member>");
            stringBuffer.append("<name>transactionCurrency</name>");
            stringBuffer.append("<value><string>" + p_requestMap.get("CURRENCY") + "</string></value>");
            stringBuffer.append("</member>");

            String validityDays = (String) p_requestMap.get("VALIDITY_DAYS");
            if ((!InterfaceUtil.isNullString(validityDays)) && !"0".equals(validityDays)) {
                stringBuffer.append("<member>");
                stringBuffer.append("<name>supervisionExpiryDateRelative</name>");
                stringBuffer.append("<value><i4>" + validityDays + "</i4></value>");
                stringBuffer.append("</member>");
            }

            String graceDays = (String) p_requestMap.get("GRACE_DAYS");
            if (!InterfaceUtil.isNullString(graceDays) && !"0".equals(graceDays)) {
                stringBuffer.append("<member>");
                stringBuffer.append("<name>serviceFeeExpiryDateRelative</name>");
                stringBuffer.append("<value><i4>" + (String) p_requestMap.get("GRACE_DAYS") + "</i4></value>");
                stringBuffer.append("</member>");
            }

            if (!InterfaceUtil.isNullString((String) p_requestMap.get("ExternalData1"))) {
                stringBuffer.append("<member>");
                stringBuffer.append("<name>externalData1</name>");
                stringBuffer.append("<value><string>" + (String) p_requestMap.get("ExternalData1") + "</string></value>");
                stringBuffer.append("</member>");
            }
            // Set the optional value of ExternalData2, if required.
            if (!InterfaceUtil.isNullString((String) p_requestMap.get("ExternalData2"))) {
                stringBuffer.append("<member>");
                stringBuffer.append("<name>externalData2</name>");
                stringBuffer.append("<value><string>" + (String) p_requestMap.get("ExternalData2") + "</string></value>");
                stringBuffer.append("</member>");
            }

            stringBuffer.append(endRequestTag());
            stringBuffer.append("</methodCall>");
            requestStr = stringBuffer.toString();
        }// end of try-Block
        catch (Exception e) {
            _log.error("generateImmediateDebitRequest", "Exception e: " + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("generateImmediateDebitRequest", "Exiting  requestStr::" + requestStr);
        }// end of finally
        return requestStr;
    }// end of generateImmediateDebitRequest

    /**
     * This method is used to parse the response of GetAccountinformation.
     * 
     * @param String
     *            p_responseStr
     * @return HashMap
     */
    private HashMap parseGetAccountInfoResponse(String p_responseStr) throws Exception {
        // if(_log.isDebugEnabled())
        // _log.debug("parseGetAccountInfoResponse","Entered p_responseStr::"+p_responseStr);
        HashMap responseMap = null;
        int indexStart = 0;
        int indexEnd = 0;
        int tempIndex = 0;
        String responseCode = null;
        try {
            responseMap = new HashMap();
            indexStart = p_responseStr.indexOf("<fault>");
            if (indexStart > 0) {
                tempIndex = p_responseStr.indexOf("faultCode", indexStart);
                String faultCode = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</4>", tempIndex));
                responseMap.put("faultCode", faultCode.trim());
                tempIndex = p_responseStr.indexOf("faultString", tempIndex);
                String faultString = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex));
                responseMap.put("faultString", faultString.trim());
                return responseMap;
            }

            indexStart = p_responseStr.indexOf("<member>");
            tempIndex = p_responseStr.indexOf("responseCode", indexStart);
            if (tempIndex > 0) {
                responseCode = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("responseCode", responseCode.trim());
                Object[] successList = CS3CP6I.RESULT_OK.split(",");
                // if(!CS3MobinilI.RESULT_OK.equals(responseCode))
                if (!Arrays.asList(successList).contains(responseCode))
                    return responseMap;
            }

            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("accountValue1", indexStart);
            if (tempIndex > 0) {
                String accountValue1 = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("accountValue1", accountValue1.trim());
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("originTransactionID", indexStart);
            if (tempIndex > 0) {
                String originTransactionID = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("originTransactionID", originTransactionID.trim());
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("serviceClassCurrent", indexStart);
            if (tempIndex > 0) {
                String serviceClassCurrent = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("serviceClassCurrent", serviceClassCurrent.trim());
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("supervisionExpiryDate", indexStart);
            if (tempIndex > 0) {
                String supervisionExpiryDate = p_responseStr.substring("<dateTime.iso8601>".length() + p_responseStr.indexOf("<dateTime.iso8601>", tempIndex), p_responseStr.indexOf("</dateTime.iso8601>", tempIndex)).trim();
                responseMap.put("supervisionExpiryDate", getDateString(supervisionExpiryDate.trim()));
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("serviceFeeExpiryDate", indexStart);
            if (tempIndex > 0) {
                String serviceFeeExpiryDate = p_responseStr.substring("<dateTime.iso8601>".length() + p_responseStr.indexOf("<dateTime.iso8601>", tempIndex), p_responseStr.indexOf("</dateTime.iso8601>", tempIndex)).trim();
                responseMap.put("serviceFeeExpiryDate", getDateString(serviceFeeExpiryDate.trim()));
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("languageIDCurrent", indexStart);
            if (tempIndex > 0) {
                String languageIDCurrent = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("languageIDCurrent", languageIDCurrent.trim());
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("temporaryBlockedFlag", indexStart);
            if (tempIndex > 0) {
                String temporaryBlockedFlag = p_responseStr.substring("<boolean>".length() + p_responseStr.indexOf("<boolean>", tempIndex), p_responseStr.indexOf("</boolean>", tempIndex)).trim();
                responseMap.put("temporaryBlockedFlag", getDateString(temporaryBlockedFlag));
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }
        } catch (Exception e) {
            _log.error("parseGetAccountInfoResponse", "Exception e::" + e.getMessage());
            throw e;
        }// end catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseGetAccountInfoResponse", "Exited responseMap::" + responseMap);
        }// end of finally
        return responseMap;
    }// end of parseGetAccountInfoResponse

    /**
     * This method is used to parse the credit response.
     * 
     * @param p_responseStr
     * @return HashMap
     * @throws Exception
     */
    public HashMap parseRechargeCreditResponse(String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseRechargeCreditResponse", "Entered p_responseStr::" + p_responseStr);
        HashMap responseMap = null;
        int indexStart = 0;
        int indexEnd = 0;
        int tempIndex = 0;
        String responseCode = null;
        try {
            responseMap = new HashMap();

            indexStart = p_responseStr.indexOf("<fault>");
            if (indexStart > 0) {
                tempIndex = p_responseStr.indexOf("faultCode", indexStart);
                String faultCode = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</4>", tempIndex));
                responseMap.put("faultCode", faultCode.trim());
                tempIndex = p_responseStr.indexOf("faultString", tempIndex);
                String faultString = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex));
                responseMap.put("faultString", faultString.trim());
                return responseMap;
            }

            indexStart = p_responseStr.indexOf("<member>");
            tempIndex = p_responseStr.indexOf("responseCode", indexStart);
            if (tempIndex > 0) {
                responseCode = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("responseCode", responseCode.trim());
                Object[] successList = CS3CP6I.RESULT_OK.split(",");
                // if(!CS3MobinilI.RESULT_OK.equals(responseCode))
                if (!Arrays.asList(successList).contains(responseCode))
                    return responseMap;
            }

            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("originTransactionID", indexStart);
            if (tempIndex > 0) {
                String originTransactionID = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("originTransactionID", originTransactionID.trim());
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("transactionAmount", indexStart);
            if (tempIndex > 0) {
                String transactionAmount = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("transactionAmount", transactionAmount.trim());
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("serviceFeeExpiryDate", indexStart);
            if (tempIndex > 0) {
                String serviceFeeExpiryDate = p_responseStr.substring("<dateTime.iso8601>".length() + p_responseStr.indexOf("<dateTime.iso8601>", tempIndex), p_responseStr.indexOf("</dateTime.iso8601>", tempIndex)).trim();
                responseMap.put("serviceFeeExpiryDate", getDateString(serviceFeeExpiryDate.trim()));
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("supervisionExpiryDate", indexStart);
            if (tempIndex > 0) {
                String supervisionExpiryDate = p_responseStr.substring("<dateTime.iso8601>".length() + p_responseStr.indexOf("<dateTime.iso8601>", tempIndex), p_responseStr.indexOf("</dateTime.iso8601>", tempIndex)).trim();
                responseMap.put("supervisionExpiryDate", getDateString(supervisionExpiryDate.trim()));
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("accountValue1", indexStart);
            if (tempIndex > 0) {
                String accountValue1 = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("accountValue1", accountValue1.trim());
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }
        } catch (Exception e) {
            _log.error("parseGetAccountInfoResponse", "Exception e::" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseRechargeCreditResponse", "Exited responseMap::" + responseMap);
        }// end of finally
        return responseMap;
    }// end of parseRechargeCreditResponse

    /**
     * This method is used to parse the credit response.
     * 
     * @param p_responseStr
     * @return HashMap
     * @throws Exception
     */
    private HashMap parseImmediateDebitResponse(String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseImmediateDebitResponse", "Entered p_responseStr::" + p_responseStr);
        HashMap responseMap = null;
        int indexStart = 0;
        int indexEnd = 0;
        int tempIndex = 0;
        String responseCode = null;
        try {
            responseMap = new HashMap();

            indexStart = p_responseStr.indexOf("<fault>");
            if (indexStart > 0) {
                tempIndex = p_responseStr.indexOf("faultCode", indexStart);
                String faultCode = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</4>", tempIndex));
                responseMap.put("faultCode", faultCode.trim());
                tempIndex = p_responseStr.indexOf("faultString", tempIndex);
                String faultString = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex));
                responseMap.put("faultString", faultString.trim());
                return responseMap;
            }

            indexStart = p_responseStr.indexOf("<member>");
            tempIndex = p_responseStr.indexOf("responseCode", indexStart);
            if (tempIndex > 0) {
                responseCode = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("responseCode", responseCode.trim());
                Object[] successList = CS3CP6I.RESULT_OK.split(",");
                // if(!CS3MobinilI.RESULT_OK.equals(responseCode))
                if (!Arrays.asList(successList).contains(responseCode))
                    return responseMap;
            }

            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("originTransactionID", indexStart);
            if (tempIndex > 0) {
                String originTransactionID = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("originTransactionID", originTransactionID.trim());
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }
        } catch (Exception e) {
            _log.error("parseImmediateDebitResponse", "Exception e::" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseImmediateDebitResponse", "Exited responseMap::" + responseMap);
        }
        return responseMap;
    }// end of parseImmediateDebitResponse

    /**
     * Method to get the Transaction date and time with specified format.
     * 
     * @return String
     * @throws Exception
     */
    private String getCS3CP6TransDateTime() throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getCS3CP6TransDateTime", "Entered");
        String transDateTime = null;
        try {
            Date now = new Date();
            transDateTime = _sdf.format(now) + _sign + _twoDigits.format(_hours) + _twoDigits.format(_minutes);
        }// end of try block
        catch (Exception e) {
            _log.error("getCS3CP6TransDateTime", "Exception e = " + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("getCS3CP6TransDateTime", "Exited transDateTime: " + transDateTime);
        }// end of finally
        return transDateTime;
    }// end of getCS3CP6TransDateTime

    /**
     * This method is used to construct the string that contains the start
     * elements of request xml.
     * 
     * @return String
     */
    private String startRequestTag() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("<params>");
        stringBuffer.append("<param>");
        stringBuffer.append("<value>");
        stringBuffer.append("<struct>");
        return stringBuffer.toString();
    }

    /**
     * This method is used to construct the string that contains the end
     * elements of request xml.
     * 
     * @return String
     */
    private String endRequestTag() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("</struct>");
        stringBuffer.append("</value>");
        stringBuffer.append("</param>");
        stringBuffer.append("</params>");
        return stringBuffer.toString();
    }

    /**
     * This method is used to convert the date string into yyyyMMdd from
     * yyyyMMdd'T'HH:mm:ss
     * 
     * @param String
     *            p_dateStr
     * @return String
     */
    public String getDateString(String p_dateStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getDateString", "Entered p_dateStr::" + p_dateStr);
        String dateStr = "";
        try {
            dateStr = p_dateStr.substring(0, p_dateStr.indexOf("T")).trim();
        } catch (Exception e) {
            _log.error("getDateString", "Exception e::" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getDateString", "Exited dateStr::" + dateStr);
        }
        return dateStr;
    }

    /**
     * This method is used to generate the request for getting account details
     * along with firstIVRCall,AccountStatus but no balance details.
     * 
     * @param HashMap
     *            p_requestMap
     * @return String
     * @throws Exception
     */
    private String generateGetAccountDetailsRequest(HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateGetBalanceAndDateRequest", "Entered p_requestMap::" + p_requestMap);
        String requestStr = null;
        StringBuffer stringBuffer = null;
        String inTransactionID = null;
        try {
            stringBuffer = new StringBuffer(1028);
            stringBuffer.append("<?xml version='1.0'?>");
            stringBuffer.append("<methodCall>");
            stringBuffer.append("<methodName>GetAccountDetails</methodName>");
            stringBuffer.append(startRequestTag());
            stringBuffer.append("<member>");
            stringBuffer.append("<name>originNodeType</name>");
            stringBuffer.append("<value><string>" + p_requestMap.get("NODE_TYPE") + "</string></value>");
            stringBuffer.append("</member>");
            // Set the originHostName
            stringBuffer.append("<member>");
            stringBuffer.append("<name>originHostName</name>");
            stringBuffer.append("<value><string>" + p_requestMap.get("HOST_NAME") + "</string></value>");
            stringBuffer.append("</member>");
            stringBuffer.append("<member>");
            stringBuffer.append("<name>originTransactionID</name>");
            stringBuffer.append("<value><string>" + (String) p_requestMap.get("IN_RECON_ID") + "</string></value>");
            stringBuffer.append("</member>");
            // Set the originTimeStamp
            stringBuffer.append("<member>");
            stringBuffer.append("<name>originTimeStamp</name>");
            stringBuffer.append("<value><dateTime.iso8601>" + getCS3CP6TransDateTime() + "</dateTime.iso8601></value>");
            stringBuffer.append("</member>");
            // Set the optional parameter subscriberNumberNAI if present.
            stringBuffer.append("<member>");
            stringBuffer.append("<name>subscriberNumberNAI</name>");
            stringBuffer.append("<value><i4>" + p_requestMap.get("SubscriberNumberNAI") + "</i4></value>");
            stringBuffer.append("</member>");

            // Set the subscriberNumber after adding or removing the prefix
            // defined in the INFile.
            stringBuffer.append("<member>");
            stringBuffer.append("<name>subscriberNumber</name>");
            stringBuffer.append("<value><string>" + InterfaceUtil.getFilterMSISDN((String) p_requestMap.get("INTERFACE_ID"), (String) p_requestMap.get("MSISDN")) + "</string></value>");
            stringBuffer.append("</member>");
            stringBuffer.append(endRequestTag());
            stringBuffer.append("</methodCall>");
            requestStr = stringBuffer.toString();
        }// end of try-block
        catch (Exception e) {
            _log.error("generateGetAccountDetailsRequest", "Exception e::" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("generateGetAccountDetailsRequest", "Exiting Request String:requestStr::" + requestStr);
        }// end of finally
        return requestStr;
    }// end of generateGetAccountDetailsRequest

}
