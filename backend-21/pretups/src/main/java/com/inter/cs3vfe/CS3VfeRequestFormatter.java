/*
 * Created on May 1, 2008
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.cs3vfe;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceUtil;

/**
 * @author dhirCS3MobinilRequestFormatterODO To change the template for this
 *         generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class CS3VfeRequestFormatter {
    public static Log _log = LogFactory.getLog(CS3VfeRequestFormatter.class);
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
    private String _interfaceID;

    public CS3VfeRequestFormatter() throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("CS3VfeRequestFormatter[constructor]", "Entered");
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
            _log.error("CS3VfeRequestFormatter[constructor]", "Exception e::" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("CS3VfeRequestFormatter[constructor]", "Exited");
        }// end of finally
    }// end of CS3VfeRequestFormatter[constructor]

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
            case CS3VfeI.ACTION_GET_BAL_DATE: {
                str = generateGetBalAndDateRequest(p_map);
                break;
            }
            case CS3VfeI.ACTION_GET_ACCOUNT_DETAILS: {
                str = generateGetAccountDetailsRequest(p_map);
                break;
            }
            case CS3VfeI.ACTION_RECHARGE_CREDIT: {
                str = generateRechargeCreditRequest(p_map);
                break;
            }
            case CS3VfeI.ACTION_DEBIT_ADJUST: {
                str = generateCreditAdjustRequest(p_map);
                break;
            }
            case CS3VfeI.ACTION_CREDIT_ADJUST: {
                str = generateDebitAdjustRequest(p_map);
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
    public HashMap parseResponse(int p_action, String p_responseStr, String p_interfaceID) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseResponse", "Entered p_action::" + p_action + " p_responseStr:: " + p_responseStr);
        HashMap map = null;
        _interfaceID = p_interfaceID;
        try {
            switch (p_action) {
            case CS3VfeI.ACTION_GET_BAL_DATE: {
                map = parseGetBAalAndDateResponse(p_responseStr);
                break;
            }
            case CS3VfeI.ACTION_GET_ACCOUNT_DETAILS: {
                map = parseGetAccountDetailsResponse(p_responseStr);
                break;
            }
            case CS3VfeI.ACTION_RECHARGE_CREDIT: {
                map = parseRechargeCreditResponse(p_responseStr);
                break;
            }
            case CS3VfeI.ACTION_CREDIT_ADJUST: {
                map = parseCreditAdjustResponse(p_responseStr);
                break;
            }
            case CS3VfeI.ACTION_DEBIT_ADJUST: {
                map = parseDebitAdjustResponse(p_responseStr);
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
     * information.
     * 
     * @param HashMap
     *            p_requestMap
     * @return String
     * @throws Exception
     */
    private String generateGetBalAndDateRequest(HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateGetBalAndDateRequest", "Entered p_requestMap::" + p_requestMap);
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
            stringBuffer.append("<value><dateTime.iso8601>" + getCS3TransDateTime() + "</dateTime.iso8601></value>");
            stringBuffer.append("</member>");
            // Set the optional parameter subscriberNumberNAI if present.

            // if(!PretupsI.SERVICE_TYPE_SUBSCRIBER_ENQUIRY.equals(p_requestMap.get("REQ_SERVICE")))
            {
                stringBuffer.append("<member>");
                stringBuffer.append("<name>subscriberNumberNAI</name>");
                stringBuffer.append("<value><i4>" + p_requestMap.get("SubscriberNumberNAI") + "</i4></value>");
                stringBuffer.append("</member>");
            }

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
            _log.error("generateGetBalAndDateRequest", "Exception e::" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("generateGetBalAndDateRequest", "Exiting Request String:requestStr::" + requestStr);
        }// end of finally
        return requestStr;
    }// end of generateGetBalAndDateRequest

    /**
     * This method is used to generate the request for getting account
     * information.
     * 
     * @param HashMap
     *            p_requestMap
     * @return String
     * @throws Exception
     */
    private String generateGetAccountDetailsRequest(HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateGetAccountDetailsRequest", "Entered p_requestMap::" + p_requestMap);
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
            stringBuffer.append("<value><dateTime.iso8601>" + getCS3TransDateTime() + "</dateTime.iso8601></value>");
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
            stringBuffer.append("<value><dateTime.iso8601>" + getCS3TransDateTime() + "</dateTime.iso8601></value>");
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
            /*
             * if(!InterfaceUtil.isNullString((String)p_requestMap.get(
             * "CR_EXTERNAL_DATA1")))
             * {
             * stringBuffer.append("<member>");
             * stringBuffer.append("<name>externalData1</name>");
             * stringBuffer.append("<value><string>"+p_requestMap.get(
             * "CR_EXTERNAL_DATA1")+"</string></value>");
             * stringBuffer.append("</member>");
             * }
             */
            stringBuffer.append("<member>");
            stringBuffer.append("<name>externalData1</name>");
            stringBuffer.append("<value><string>" + InterfaceUtil.getFilterMSISDN((String) p_requestMap.get("INTERFACE_ID"), (String) p_requestMap.get("SENDER_MSISDN")) + "</string></value>");
            stringBuffer.append("</member>");
            // Set the optional value of ExternalData2, if required.
            if (!InterfaceUtil.isNullString((String) p_requestMap.get("CR_EXTERNAL_DATA2"))) {
                stringBuffer.append("<member>");
                stringBuffer.append("<name>externalData2</name>");
                stringBuffer.append("<value><string>" + p_requestMap.get("CR_EXTERNAL_DATA2") + "</string></value>");
                stringBuffer.append("</member>");
            }
            if (!InterfaceUtil.isNullString((String) p_requestMap.get("CR_EXTERNAL_DATA3"))) {
                stringBuffer.append("<member>");
                stringBuffer.append("<name>externalData3</name>");
                stringBuffer.append("<value><string>" + p_requestMap.get("CR_EXTERNAL_DATA3") + "</string></value>");
                stringBuffer.append("</member>");
            }
            // Set the optional value of ExternalData2, if required.
            if (!InterfaceUtil.isNullString((String) p_requestMap.get("CR_EXTERNAL_DATA4"))) {
                stringBuffer.append("<member>");
                stringBuffer.append("<name>externalData4</name>");
                stringBuffer.append("<value><string>" + p_requestMap.get("CR_EXTERNAL_DATA4") + "</string></value>");
                stringBuffer.append("</member>");
            }
            stringBuffer.append("<member>");
            stringBuffer.append("<name>requestRefillAccountAfterFlag</name>");
            stringBuffer.append("<value><boolean>" + p_requestMap.get("REFILL_ACNT_AFTER_FLAG") + "</boolean></value>");
            stringBuffer.append("</member>");

            stringBuffer.append("<member>");
            stringBuffer.append("<name>requestRefillAccountBeforeFlag</name>");
            stringBuffer.append("<value><boolean>" + p_requestMap.get("REFILL_ACNT_B4_FLAG") + "</boolean></value>");
            stringBuffer.append("</member>");

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
    private String generateCreditAdjustRequest(HashMap p_requestMap) throws Exception {
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
            stringBuffer.append("<value><dateTime.iso8601>" + getCS3TransDateTime() + "</dateTime.iso8601></value>");
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

            if (!InterfaceUtil.isNullString((String) p_requestMap.get("VALIDITY_DAYS"))) {
                stringBuffer.append("<member>");
                stringBuffer.append("<name>supervisionExpiryDateRelative</name>");
                stringBuffer.append("<value><string>" + (String) p_requestMap.get("VALIDITY_DAYS") + "</string></value>");
                stringBuffer.append("</member>");
            }

            if (!InterfaceUtil.isNullString((String) p_requestMap.get("GRACE_DAYS"))) {
                stringBuffer.append("<member>");
                stringBuffer.append("<name>serviceFeeExpiryDateRelative</name>");
                stringBuffer.append("<value><string>" + (String) p_requestMap.get("GRACE_DAYS") + "</string></value>");
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
            _log.error("generateCreditAdjustRequest", "Exception e: " + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("generateCreditAdjustRequest", "Exiting  requestStr::" + requestStr);
        }// end of finally
        return requestStr;
    }// end of generateCreditAdjustRequest

    /**
     * 
     * @param map
     * @return
     * @throws Exception
     */
    private String generateDebitAdjustRequest(HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateDebitAdjustRequest", "Entered p_requestMap::" + p_requestMap);
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
            stringBuffer.append("<value><dateTime.iso8601>" + getCS3TransDateTime() + "</dateTime.iso8601></value>");
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

            if (!InterfaceUtil.isNullString((String) p_requestMap.get("VALIDITY_DAYS"))) {
                stringBuffer.append("<member>");
                stringBuffer.append("<name>supervisionExpiryDateRelative</name>");
                stringBuffer.append("<value><string>" + (String) p_requestMap.get("VALIDITY_DAYS") + "</string></value>");
                stringBuffer.append("</member>");
            }

            if (!InterfaceUtil.isNullString((String) p_requestMap.get("GRACE_DAYS"))) {
                stringBuffer.append("<member>");
                stringBuffer.append("<name>serviceFeeExpiryDateRelative</name>");
                stringBuffer.append("<value><string>" + (String) p_requestMap.get("GRACE_DAYS") + "</string></value>");
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
            _log.error("generateDebitAdjustRequest", "Exception e: " + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("generateDebitAdjustRequest", "Exiting  requestStr::" + requestStr);
        }// end of finally
        return requestStr;
    }// end of generateDebitAdjustRequest

    /**
     * This method is used to parse the response of GetAccountinformation.
     * 
     * @param String
     *            p_responseStr
     * @return HashMap
     */
    private HashMap parseGetBAalAndDateResponse(String p_responseStr) throws Exception {
        // if(_log.isDebugEnabled())
        // _log.debug("parseGetAccountInfoResponse","Entered p_responseStr::"+p_responseStr);
        HashMap responseMap = null;
        int indexStart = 0;
        int tempIndex = 0;
        String responseCode = null;
        try {
            responseMap = new HashMap();
            indexStart = p_responseStr.indexOf("<fault>");
            if (indexStart > 0) {
                tempIndex = p_responseStr.indexOf("faultCode", indexStart);
                String faultCode = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex));
                responseMap.put("faultCode", faultCode.trim());
                tempIndex = p_responseStr.indexOf("faultString", tempIndex);
                String faultString = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex));
                responseMap.put("faultString", faultString.trim());
                return responseMap;
            }

            tempIndex = p_responseStr.indexOf("<name>responseCode");
            if (tempIndex > 0) {
                responseCode = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("responseCode", responseCode.trim());
                if (!"0".equals(responseCode))
                    return responseMap;
            }

            tempIndex = p_responseStr.indexOf("<name>originTransactionID");
            if (tempIndex > 0) {
                String originTransactionID = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("originTransactionID", originTransactionID.trim());
            }

            tempIndex = p_responseStr.indexOf("<name>accountActivatedFlag");
            if (tempIndex > 0) {
                String accountActivatedFlag = p_responseStr.substring("<boolean>".length() + p_responseStr.indexOf("<boolean>", tempIndex), p_responseStr.indexOf("</boolean>", tempIndex)).trim();
                responseMap.put("accountActivatedFlag", accountActivatedFlag.trim());
            }

            /*
             * tempIndex = p_responseStr.indexOf("<member><name>accountFlags");
             * if(tempIndex>0)
             * {
             * String accountFlags =
             * p_responseStr.substring("<string>".length()+
             * p_responseStr.indexOf(
             * "<string>",tempIndex),p_responseStr.indexOf(
             * "</string>",tempIndex)).trim();
             * responseMap.put("accountFlags",accountFlags.trim());
             * }
             */

            tempIndex = p_responseStr.indexOf("<name>accountValue1");
            if (tempIndex > 0) {
                String accountValue1 = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("accountValue1", accountValue1.trim());
            }

            tempIndex = p_responseStr.indexOf("<name>languageIDCurrent");
            if (tempIndex > 0) {
                String languageIDCurrent = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("languageIDCurrent", languageIDCurrent.trim());
            }

            tempIndex = p_responseStr.indexOf("<name>firstIVRCallFlag");
            if (tempIndex > 0) {
                String firstIVRCallFlag = p_responseStr.substring("<boolean>".length() + p_responseStr.indexOf("<boolean>", tempIndex), p_responseStr.indexOf("</boolean>", tempIndex)).trim();
                responseMap.put("firstIVRCallFlag", firstIVRCallFlag.trim());
            }

            tempIndex = p_responseStr.indexOf("<name>serviceClassCurrent");
            if (tempIndex > 0) {
                String serviceClassCurrent = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("serviceClassCurrent", serviceClassCurrent.trim());
            }

            tempIndex = p_responseStr.indexOf("<name>temporaryBlockedFlag");
            if (tempIndex > 0) {
                String temporaryBlockedFlag = p_responseStr.substring("<boolean>".length() + p_responseStr.indexOf("<boolean>", tempIndex), p_responseStr.indexOf("</boolean>", tempIndex)).trim();
                responseMap.put("temporaryBlockedFlag", temporaryBlockedFlag.trim());
            }

            tempIndex = p_responseStr.indexOf("<name>serviceFeeExpiryDate");
            if (tempIndex > 0) {
                String serviceFeeExpiryDate = p_responseStr.substring("<dateTime.iso8601>".length() + p_responseStr.indexOf("<dateTime.iso8601>", tempIndex), p_responseStr.indexOf("</dateTime.iso8601>", tempIndex)).trim();
                responseMap.put("serviceFeeExpiryDate", getDateString(serviceFeeExpiryDate));
            }

            tempIndex = p_responseStr.indexOf("<name>supervisionExpiryDate");
            if (tempIndex > 0) {
                String supervisionExpiryDate = p_responseStr.substring("<dateTime.iso8601>".length() + p_responseStr.indexOf("<dateTime.iso8601>", tempIndex), p_responseStr.indexOf("</dateTime.iso8601>", tempIndex)).trim();
                responseMap.put("supervisionExpiryDate", getDateString(supervisionExpiryDate));
            }

            tempIndex = p_responseStr.indexOf("<name>creditClearanceDate");

            if (tempIndex > 0) {
                String supervisionExpiryDate = p_responseStr.substring("<dateTime.iso8601>".length() + p_responseStr.indexOf("<dateTime.iso8601>", tempIndex), p_responseStr.indexOf("</dateTime.iso8601>", tempIndex)).trim();
                responseMap.put("creditClearanceDate", getDateString(supervisionExpiryDate));
            }
            parseDedicatedAcntRcvdFromIN(p_responseStr, responseMap, "V");
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
     * This method is used to parse the response of GetAccountinformation.
     * 
     * @param String
     *            p_responseStr
     * @return HashMap
     */
    private HashMap parseGetAccountDetailsResponse(String p_responseStr) throws Exception {
        // if(_log.isDebugEnabled())
        // _log.debug("parseGetAccountDetailsResponse","Entered p_responseStr::"+p_responseStr);
        HashMap responseMap = null;
        int indexStart = 0;
        int tempIndex = 0;
        String responseCode = null;
        try {
            responseMap = new HashMap();
            indexStart = p_responseStr.indexOf("<fault>");
            if (indexStart > 0) {
                tempIndex = p_responseStr.indexOf("faultCode", indexStart);
                String faultCode = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex));
                responseMap.put("faultCode", faultCode.trim());
                tempIndex = p_responseStr.indexOf("faultString", tempIndex);
                String faultString = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex));
                responseMap.put("faultString", faultString.trim());
                return responseMap;
            }

            tempIndex = p_responseStr.indexOf("<name>responseCode");
            if (tempIndex > 0) {
                responseCode = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("responseCode", responseCode.trim());
                if (!"0".equals(responseCode))
                    return responseMap;
            }

            tempIndex = p_responseStr.indexOf("<name>originTransactionID");
            if (tempIndex > 0) {
                String originTransactionID = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("originTransactionID", originTransactionID.trim());
            }

            parseServiceOfferings(p_responseStr, responseMap);
            // setServiceOffereing(p_responseStr);
            /*
             * tempIndex = p_responseStr.indexOf("<name>serviceOfferings");
             * if(tempIndex>0)
             * {
             * String originTransactionID =
             * p_responseStr.substring("<string>".length
             * ()+p_responseStr.indexOf(
             * "<string>",tempIndex),p_responseStr.indexOf
             * ("</string>",tempIndex)).trim();
             * responseMap.put("originTransactionID",originTransactionID.trim());
             * }
             */
        } catch (Exception e) {
            _log.error("parseGetAccountDetailsResponse", "Exception e::" + e.getMessage());
            throw e;
        }// end catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseGetAccountDetailsResponse", "Exited responseMap::" + responseMap);
        }// end of finally
        return responseMap;
    }// end of parseGetAccountInfoResponse

    private void parseServiceOfferings(String p_responseStr, HashMap p_responseMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseServiceOfferings: ", "Entered ");
        try {
            String serviceOfferingIds = "";
            String serviceOfferingFlags = "";
            String[] serviceOfferingActiveFlags = p_responseStr.split("serviceOfferingActiveFlag");
            for (int i = 1; i < serviceOfferingActiveFlags.length; i++) {
                String temp = serviceOfferingActiveFlags[i];
                String serviceOfferingId = "";
                String serviceOfferingFlag = "";
                int tempIndex = 0;
                tempIndex = temp.indexOf("<value><boolean>");
                if (tempIndex > 0) {
                    serviceOfferingFlag = temp.substring("<boolean>".length() + temp.indexOf("<boolean>", tempIndex), temp.indexOf("</boolean>", tempIndex)).trim();
                    tempIndex = temp.indexOf("<value><i4>");
                    if (tempIndex > 0)
                        serviceOfferingId = temp.substring("<i4>".length() + temp.indexOf("<i4>", tempIndex), temp.indexOf("</i4>", tempIndex)).trim();

                    serviceOfferingIds = serviceOfferingIds + "," + serviceOfferingId;
                    serviceOfferingFlags = serviceOfferingFlags + "," + serviceOfferingFlag;
                }
            }

            if (serviceOfferingIds.length() > 0 && serviceOfferingFlags.length() > 0) {
                p_responseMap.put("SERVICE_OFFERINGS_IDS", serviceOfferingIds.substring(1));
                p_responseMap.put("SERVICE_OFFERINGS_FLAGS", serviceOfferingFlags.substring(1));
            }
        } catch (Exception e) {
            _log.error("parseServiceOfferings: ", "Exception e: " + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseServiceOfferings: ", "Exiting ");
        }
    }

    public void parseAccountInfoB4AfterCr(String p_responseStr, String accountInfoB4AfterCr, HashMap responseMap, String suffix, boolean dedicatedReq) throws Exception {
        int tempIndex = 0;
        tempIndex = accountInfoB4AfterCr.indexOf("<name>accountValue1");
        if (tempIndex > 0) {
            String accountValue1 = accountInfoB4AfterCr.substring("<string>".length() + accountInfoB4AfterCr.indexOf("<string>", tempIndex), accountInfoB4AfterCr.indexOf("</string>", tempIndex)).trim();
            responseMap.put("accountValue1" + suffix, accountValue1.trim());
        }

        tempIndex = accountInfoB4AfterCr.indexOf("<name>serviceFeeExpiryDate");
        if (tempIndex > 0) {
            String serviceFeeExpiryDate = accountInfoB4AfterCr.substring("<dateTime.iso8601>".length() + accountInfoB4AfterCr.indexOf("<dateTime.iso8601>", tempIndex), accountInfoB4AfterCr.indexOf("</dateTime.iso8601>", tempIndex)).trim();
            responseMap.put("serviceFeeExpiryDate" + suffix, getDateString(serviceFeeExpiryDate.trim()));
        } else {
            if ("AfterCr".equals(suffix))
                // if("1".equals(FileCache.getValue(_interfaceID,"REFILL_ACNT_B4_FLAG")))
                if (true)
                    responseMap.put("serviceFeeExpiryDate" + suffix, (String) responseMap.get("serviceFeeExpiryDateB4Cr"));
        }

        tempIndex = accountInfoB4AfterCr.indexOf("<name>supervisionExpiryDate");
        if (tempIndex > 0) {
            String supervisionExpiryDate = accountInfoB4AfterCr.substring("<dateTime.iso8601>".length() + accountInfoB4AfterCr.indexOf("<dateTime.iso8601>", tempIndex), accountInfoB4AfterCr.indexOf("</dateTime.iso8601>", tempIndex)).trim();
            responseMap.put("supervisionExpiryDate" + suffix, getDateString(supervisionExpiryDate.trim()));
        } else {
            if ("B4Cr".equals(suffix))
                // if("1".equals(FileCache.getValue(_interfaceID,"REFILL_ACNT_B4_FLAG")))
                if (true)
                    responseMap.put("supervisionExpiryDate" + suffix, (String) responseMap.get("supervisionExpiryDateB4Cr"));
        }
        if (dedicatedReq)
            parseDedicatedAcntRcvdFromIN(accountInfoB4AfterCr, responseMap, suffix);
    }

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
        int tempIndexB4 = 0;
        int tempIndexAfter = 0;
        String responseCode = null;
        try {
            responseMap = new HashMap();

            indexStart = p_responseStr.indexOf("<fault>");
            if (indexStart > 0) {
                tempIndex = p_responseStr.indexOf("faultCode", indexStart);
                String faultCode = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex));
                responseMap.put("faultCode", faultCode.trim());
                tempIndex = p_responseStr.indexOf("faultString", tempIndex);
                String faultString = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex));
                responseMap.put("faultString", faultString.trim());
                return responseMap;
            }

            tempIndex = p_responseStr.indexOf("<name>responseCode");
            if (tempIndex > 0) {
                responseCode = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("responseCode", responseCode.trim());
                if (!"0".equals(responseCode))
                    return responseMap;
            }

            tempIndex = p_responseStr.indexOf("<name>originTransactionID");
            if (tempIndex > 0) {
                String originTransactionID = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("originTransactionID", originTransactionID.trim());
            }

            tempIndex = p_responseStr.indexOf("<name>transactionAmount");
            if (tempIndex > 0) {
                String originTransactionID = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("transactionAmount", originTransactionID.trim());
            }
            // assuming that PreTUPS will receive accountBeforeRefill and
            // accountAfterRefill tags in response.
            tempIndexB4 = p_responseStr.indexOf("accountBeforeRefill", 0);
            tempIndexAfter = p_responseStr.indexOf("accountAfterRefill", 0);
            String accountInfoB4Cr = null;
            String accountInfoAfterCr = null;
            if (tempIndexB4 > 0 && tempIndexAfter > 0) {
                String[] refillInfo = p_responseStr.split("accountBeforeRefill");
                if (refillInfo[0].contains("accountAfterRefill")) {
                    accountInfoB4Cr = refillInfo[1];
                    accountInfoAfterCr = refillInfo[0];
                } else if (refillInfo[1].contains("accountAfterRefill")) {
                    accountInfoB4Cr = refillInfo[1].split("accountAfterRefill")[0];
                    accountInfoAfterCr = refillInfo[1].split("accountAfterRefill")[1];
                }

                parseAccountInfoB4AfterCr(p_responseStr, accountInfoB4Cr, responseMap, "B4Cr", true);
                String[] namesB4 = new String[0];
                String[] valuesB4 = new String[0];
                String[] ExpiriesB4 = new String[0];
                String[] namesAfter = new String[0];
                String[] valuesAfter = new String[0];
                String[] ExpiriesAfter = new String[0];
                if (!InterfaceUtil.isNullString((String) responseMap.get("DDCATED_ACNT_CODES_B4Cr"))) {
                    namesB4 = ((String) responseMap.get("DDCATED_ACNT_CODES_B4Cr")).split(",");
                    valuesB4 = ((String) responseMap.get("DDCATED_ACNT_VALUES_B4Cr")).split(",");
                    ExpiriesB4 = ((String) responseMap.get("DDCATED_ACNT_EXPIRIES_B4Cr")).split(",");
                }

                parseAccountInfoB4AfterCr(p_responseStr, accountInfoAfterCr, responseMap, "AfterCr", true);
                if (!InterfaceUtil.isNullString((String) responseMap.get("DDCATED_ACNT_CODES_AfterCr"))) {
                    namesAfter = ((String) responseMap.get("DDCATED_ACNT_CODES_AfterCr")).split(",");
                    valuesAfter = ((String) responseMap.get("DDCATED_ACNT_VALUES_AfterCr")).split(",");
                    ExpiriesAfter = ((String) responseMap.get("DDCATED_ACNT_EXPIRIES_AfterCr")).split(",");
                }

                String changedAcnts = "";
                for (int i = 0; i < namesAfter.length; i++) {
                    boolean afterAcntMatched = false;
                    for (int j = 0; j < namesB4.length; j++) {
                        if (namesAfter[i].equals(namesB4[j])) {
                            afterAcntMatched = true;
                            if (!(valuesAfter[i].equals(valuesB4[j]))) {
                                changedAcnts = changedAcnts + "," + namesAfter[i];
                                break;
                            } else if (!(ExpiriesAfter[i].equals(ExpiriesB4[j]))) {
                                changedAcnts = changedAcnts + "," + namesAfter[i];
                                break;
                            }
                        }
                    }
                    if (!afterAcntMatched)
                        changedAcnts = changedAcnts + "," + namesAfter[i];
                }
                // GET THE AFTER VALUES OF THESE ACNTS WHILE SENDING MESSAGE
                if (changedAcnts.length() > 0 && changedAcnts.charAt(0) == ',')
                    changedAcnts = changedAcnts.substring(1);
                responseMap.put("CHANGED_DDCATED_ACNTS", changedAcnts);
            } else if (tempIndexB4 > 0) {
                accountInfoB4Cr = p_responseStr;
                parseAccountInfoB4AfterCr(p_responseStr, accountInfoB4Cr, responseMap, "B4Cr", false);
            } else if (tempIndexAfter > 0) {
                accountInfoAfterCr = p_responseStr;
                parseAccountInfoB4AfterCr(p_responseStr, accountInfoAfterCr, responseMap, "AfterCr", false);
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
    private HashMap parseCreditAdjustResponse(String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseCreditAdjustResponse", "Entered p_responseStr::" + p_responseStr);
        HashMap responseMap = null;
        int indexStart = 0;
        int tempIndex = 0;
        String responseCode = null;
        try {
            responseMap = new HashMap();
            indexStart = p_responseStr.indexOf("<fault>");
            if (indexStart > 0) {
                tempIndex = p_responseStr.indexOf("faultCode", indexStart);
                String faultCode = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex));
                responseMap.put("faultCode", faultCode.trim());
                tempIndex = p_responseStr.indexOf("faultString", tempIndex);
                String faultString = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex));
                responseMap.put("faultString", faultString.trim());
                return responseMap;
            }

            tempIndex = p_responseStr.indexOf("<name>responseCode");
            if (tempIndex > 0) {
                responseCode = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("responseCode", responseCode.trim());
            }

            tempIndex = p_responseStr.indexOf("<name>originTransactionID");
            if (tempIndex > 0) {
                String originTransactionID = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("originTransactionID", originTransactionID.trim());
            }
        } catch (Exception e) {
            _log.error("parseCreditAdjustResponse", "Exception e::" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseCreditAdjustResponse", "Exited responseMap::" + responseMap);
        }
        return responseMap;
    }// end of parseCreditAdjustResponse

    /**
     * This method is used to parse the credit response.
     * 
     * @param p_responseStr
     * @return HashMap
     * @throws Exception
     */
    private HashMap parseDebitAdjustResponse(String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseDebitAdjustResponse", "Entered p_responseStr::" + p_responseStr);
        HashMap responseMap = null;
        int indexStart = 0;
        int tempIndex = 0;
        String responseCode = null;
        try {
            responseMap = new HashMap();
            indexStart = p_responseStr.indexOf("<fault>");
            if (indexStart > 0) {
                tempIndex = p_responseStr.indexOf("faultCode", indexStart);
                String faultCode = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex));
                responseMap.put("faultCode", faultCode.trim());
                tempIndex = p_responseStr.indexOf("faultString", tempIndex);
                String faultString = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex));
                responseMap.put("faultString", faultString.trim());
                return responseMap;
            }

            tempIndex = p_responseStr.indexOf("<name>responseCode");
            if (tempIndex > 0) {
                responseCode = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("responseCode", responseCode.trim());
            }

            tempIndex = p_responseStr.indexOf("<name>originTransactionID");
            if (tempIndex > 0) {
                String originTransactionID = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("originTransactionID", originTransactionID.trim());
            }
        } catch (Exception e) {
            _log.error("parseDebitAdjustResponse", "Exception e::" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseDebitAdjustResponse", "Exited responseMap::" + responseMap);
        }
        return responseMap;
    }// end of parseDebitAdjustResponse

    /**
     * Method to get the Transaction date and time with specified format.
     * 
     * @return String
     * @throws Exception
     */
    private String getCS3TransDateTime() throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getCS3TransDateTime", "Entered");
        String transDateTime = null;
        try {
            Date now = new Date();
            transDateTime = _sdf.format(now) + _sign + _twoDigits.format(_hours) + _twoDigits.format(_minutes);
        }// end of try block
        catch (Exception e) {
            _log.error("getCS3TransDateTime", "Exception e = " + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("getCS3TransDateTime", "Exited transDateTime: " + transDateTime);
        }// end of finally
        return transDateTime;
    }// end of getCS3TransDateTime

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
        /*
         * if(_log.isDebugEnabled())
         * _log.debug("getDateString","Entered p_dateStr::"+p_dateStr);
         * String dateStr="";
         * try
         * {
         * dateStr = p_dateStr.substring(0,p_dateStr.indexOf("T")).trim();
         * }
         * catch(Exception e)
         * {
         * _log.error("getDateString","Exception e::"+e.getMessage());
         * throw e;
         * }
         * finally
         * {
         * if(_log.isDebugEnabled())
         * _log.debug("getDateString","Exited dateStr::"+dateStr);
         * }
         */return p_dateStr;
    }

    private void parseDedicatedAcntRcvdFromIN(String p_responseStr, HashMap p_responseMap, String p_sufix) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("setDedicatedAcntRcvdFromIN: ", "Entered ");
        try {
            String acntNames = "";
            String acntValues = "";
            String acntExpiries = "";
            String[] dedicatedAccountID = p_responseStr.split("dedicatedAccountID");
            for (int i = 1; i < dedicatedAccountID.length; i++) {
                String temp = dedicatedAccountID[i];
                String acntName = "";
                String acntValue = "";
                String acntExpiry = "";
                int tempIndex = 0;

                tempIndex = temp.indexOf("value", 0);
                if (tempIndex > 0) {
                    acntName = temp.substring("<i4>".length() + temp.indexOf("<i4>", tempIndex), temp.indexOf("</i4>", tempIndex)).trim();
                    tempIndex = temp.indexOf("dedicatedAccountValue1", tempIndex);
                    if (tempIndex > 0)
                        acntValue = temp.substring("<string>".length() + temp.indexOf("<string>", tempIndex), temp.indexOf("</string>", tempIndex)).trim();
                    tempIndex = temp.indexOf("expiryDate", tempIndex);
                    if (tempIndex > 0)
                        acntExpiry = temp.substring("<dateTime.iso8601>".length() + temp.indexOf("<dateTime.iso8601>", tempIndex), temp.indexOf("</dateTime.iso8601>", tempIndex)).trim();
                    acntExpiry = getDateString(acntExpiry);
                    acntNames = acntNames + "," + acntName;
                    acntValues = acntValues + "," + acntValue;
                    acntExpiries = acntExpiries + "," + acntExpiry;
                }
            }
            if (acntNames.length() > 0 && acntValues.length() > 0 && acntExpiries.length() > 0) {
                p_responseMap.put("DDCATED_ACNT_CODES_" + p_sufix, acntNames.substring(1));
                p_responseMap.put("DDCATED_ACNT_VALUES_" + p_sufix, acntValues.substring(1));
                p_responseMap.put("DDCATED_ACNT_EXPIRIES_" + p_sufix, acntExpiries.substring(1));
            }
        } catch (Exception e) {
            _log.error("setDedicatedAcntRcvdFromIN: ", "Exception e: " + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("setDedicatedAcntRcvdFromIN: ", "Exiting Defined bundles at IN: " + p_responseMap.get("DDCATED_ACNT_CODES_" + p_sufix));
        }
    }
}
