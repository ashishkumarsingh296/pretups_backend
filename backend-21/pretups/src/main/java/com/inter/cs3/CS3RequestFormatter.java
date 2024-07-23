package com.inter.cs3;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.module.InterfaceUtil;

/**
 * @(#)CS3RequestFormatter
 *                         Copyright(c) 2006, Bharti Telesoft Int. Public Ltd.
 *                         All Rights Reserved
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Author Date History
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Ashish Kumar Sep 06,2006 Initial Creation
 *                         ----------------------------------------------------
 *                         --------------------------------------------
 *                         This class is used to format the request and response
 *                         based on the action.
 *                         REQUEST: XML request is generated from the hash map
 *                         based on key value pairs.
 *                         RESPONSE: From XML response elements values are
 *                         stored in HashMap.
 */
public class CS3RequestFormatter {
    public static Log _log = LogFactory.getLog(CS3RequestFormatter.class);
    private SimpleDateFormat _sdf = null;
    private TimeZone _timeZone = null;
    private String _transDateFormat = "yyyyMMdd'T'HH:mm:ss";// Defines the Date
                                                            // and time format
                                                            // of CS3IN.
    private DecimalFormat _twoDigits = null;
    private int _offset;
    private String _sign = "+";
    private int _hours;
    private int _minutes;
    private static int _counter = 0;
    private static long _prevReqTime = 0;

    public CS3RequestFormatter() throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("CS3RequestFormatter[constructor]", "Entered");
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
            _log.error("CS3RequestFormatter[constructor]", "Exception e::" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("CS3RequestFormatter[constructor]", "Exited");
        }// end of finally
    }// end of CS3RequestFormatter[constructor]

    /**
     * This method is used to generate the transaction id that is to be send to
     * IN.
     * 
     * @param HashMap
     *            p_requestMap
     * @return String
     * @throws Exception
     */
    private String getINReconTxnID(HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getINReconTxnID", "Enetered p_requestMap::" + p_requestMap);
        String inReconID = null;
        try {
            String userType = (String) p_requestMap.get("USER_TYPE");
            if (userType != null)
                inReconID = ((String) p_requestMap.get("TRANSACTION_ID") + userType);
            else
                inReconID = ((String) p_requestMap.get("TRANSACTION_ID"));
            p_requestMap.put("IN_RECON_ID", inReconID);
        }// end of try block
        catch (Exception e) {
            _log.error("getINReconTxnID", "Exception e=" + e);
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("getINReconTxnID", "Exited inReconID::" + inReconID);
        }// end of finally
        return inReconID;
    }// end of getINReconTxnID

    /**
     * Get IN TransactionID for superRefillT
     * 
     * @return
     * @throws Exception
     */
    private String getINTransactionID(HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getINTransactionID", "Entered");
        String counter = "";

        // Changed it from 7 to 4 (For aircell it is used as of length 7) we can
        // also configure this but it will be parsed each
        // each time hence the default value is taken as 4, Just confirm this
        // that whether any impact would be in the SUPERREFILLT COMMAND in the
        // AircellChainnai.
        int inTxnLength = 4;
        try {
            java.util.Date mydate = new java.util.Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm");
            String dateString = sdf.format(mydate);
            counter = getIncrCounter();
            if (_log.isDebugEnabled())
                _log.debug("getINReconTxnID", "counter value from EricsionValidation is " + counter);
            int length = counter.length();
            int tmpLength = inTxnLength - length;
            if (length < inTxnLength) {
                for (int i = 0; i < tmpLength; i++)
                    counter = "0" + counter;
            }
            counter = dateString + (String) p_requestMap.get("INSTANCE_ID") + counter;
            p_requestMap.put("IN_RECON_ID", counter);
            p_requestMap.put("IN_TXN_ID", counter);
            if (_log.isDebugEnabled())
                _log.debug("getINTransactionID", "Exited  id: " + dateString);
            return counter;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("CS3RequestFormatter[getINTransactionID]", "Exception e::" + e.getMessage());
            throw e;
        }// end of catch-Exception
    }

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
            case CS3I.ACTION_ACCOUNT_INFO: {
                str = generateGetAccountInfoRequest(p_map);
                break;
            }
            case CS3I.ACTION_CREDIT: {
                str = generateRechargeCreditRequest(p_map);
                break;
            }
            case CS3I.ACTION_DEBIT: {
                str = generateImmediateDebitRequest(p_map);
                break;
            }
            case CS3I.ACTION_SUPER_REFILLT_CREDIT: {
                str = generateRechargeCreditRequestSuperRefillT(p_map);
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
            case CS3I.ACTION_ACCOUNT_INFO: {
                map = parseGetAccountInfoResponse(p_responseStr);
                break;
            }
            case CS3I.ACTION_CREDIT: {
                map = parseRechargeCreditResponse(p_responseStr);
                break;
            }
            case CS3I.ACTION_DEBIT: {
                map = parseImmediateDebitResponse(p_responseStr);
                break;
            }
            case CS3I.ACTION_SUPER_REFILLT_CREDIT: {
                map = parseRechargeCreditResponseSuperRRefillT(p_responseStr);
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
    private String generateGetAccountInfoRequest(HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateGetAccountInfoRequest", "Entered p_requestMap::" + p_requestMap);
        String requestStr = null;
        StringBuffer stringBuffer = null;
        String inTransactionID = null;
        try {
            stringBuffer = new StringBuffer(1028);
            stringBuffer.append("<?xml version='1.0'?>");
            stringBuffer.append("<methodCall>");
            stringBuffer.append("<methodName>GetAccountDetailsTRequest</methodName>");
            stringBuffer.append(startRequestTag());
            // Set the originNodeType
            stringBuffer.append("<member>");
            stringBuffer.append("<name>originNodeType</name>");
            stringBuffer.append("<value><string>" + p_requestMap.get("origin_node_type") + "</string></value>");
            stringBuffer.append("</member>");
            // Set the originHostName
            stringBuffer.append("<member>");
            stringBuffer.append("<name>originHostName</name>");
            stringBuffer.append("<value><string>" + p_requestMap.get("origin_host_name") + "</string></value>");
            stringBuffer.append("</member>");
            stringBuffer.append("<member>");
            // Set the originTransactionID
            // Get the transaction id format based on the INFile parameter.
            if ("Y".equals((String) p_requestMap.get("PRETUPS_ID_AS_ORGN_TXN_ID")))
                inTransactionID = getINReconTxnID(p_requestMap);
            else
                inTransactionID = getINTransactionID(p_requestMap);
            stringBuffer.append("<name>originTransactionID</name>");
            stringBuffer.append("<value><string>" + inTransactionID + "</string></value>");
            stringBuffer.append("</member>");
            // Set the originTimeStamp
            stringBuffer.append("<member>");
            stringBuffer.append("<name>originTimeStamp</name>");
            stringBuffer.append("<value><dateTime.iso8601>" + getCS3TransDateTime() + "</dateTime.iso8601></value>");
            stringBuffer.append("</member>");
            // Set the optional parameter subscriberNumberNAI if present.
            if (!InterfaceUtil.isNullString((String) p_requestMap.get("SubscriberNumberNAI"))) {
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
            // Set the optional parameter messageCapabilityFlag,if present.
            if (!InterfaceUtil.isNullString((String) p_requestMap.get("message_capability_flag"))) {
                stringBuffer.append("<member>");
                stringBuffer.append("<name>messageCapabilityFlag</name>");
                stringBuffer.append("<value><string>" + p_requestMap.get("message_capability_flag") + "</string></value>");
                stringBuffer.append("</member>");
            }
            stringBuffer.append(endRequestTag());
            stringBuffer.append("</methodCall>");
            requestStr = stringBuffer.toString();
        }// end of try-block
        catch (Exception e) {
            _log.error("generateGetAccountInfoRequest", "Exception e::" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("generateGetAccountInfoRequest", "Exiting Request String:requestStr::" + requestStr);
        }// end of finally
        return requestStr;
    }// end of generateGetAccountInfoRequest

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
            stringBuffer.append("<methodName>RefillTRequest</methodName>");
            stringBuffer.append(startRequestTag());
            // Set the origin originNodeType
            stringBuffer.append("<member>");
            stringBuffer.append("<name>originNodeType</name>");
            stringBuffer.append("<value><string>" + p_requestMap.get("origin_node_type") + "</string></value>");
            stringBuffer.append("</member>");
            // Set the originHostName
            stringBuffer.append("<member>");
            stringBuffer.append("<name>originHostName</name>");
            stringBuffer.append("<value><string>" + p_requestMap.get("origin_host_name") + "</string></value>");
            stringBuffer.append("</member>");
            stringBuffer.append("<member>");
            // Set the originTransactionID
            if ("Y".equals((String) p_requestMap.get("PRETUPS_ID_AS_ORGN_TXN_ID")))
                inTransactionID = getINReconTxnID(p_requestMap);
            else
                inTransactionID = getINTransactionID(p_requestMap);
            stringBuffer.append("<name>originTransactionID</name>");
            stringBuffer.append("<value><string>" + inTransactionID + "</string></value>");
            stringBuffer.append("</member>");
            // Set the originTimeStamp
            stringBuffer.append("<member>");
            stringBuffer.append("<name>originTimeStamp</name>");
            stringBuffer.append("<value><dateTime.iso8601>" + getCS3TransDateTime() + "</dateTime.iso8601></value>");
            stringBuffer.append("</member>");
            // Check the optional value of SubscriberNumberNAI if it is not null
            // set it.
            if (!InterfaceUtil.isNullString((String) p_requestMap.get("SubscriberNumberNAI"))) {
                stringBuffer.append("<member>");
                stringBuffer.append("<name>subscriberNumberNAI</name>");
                stringBuffer.append("<value><i4>" + p_requestMap.get("SubscriberNumberNAI") + "</i4></value>");
                stringBuffer.append("</member>");
            }
            // Set subscriberNumber and add or remove the msisdn if required.
            stringBuffer.append("<member>");
            stringBuffer.append("<name>subscriberNumber</name>");
            stringBuffer.append("<value><string>" + InterfaceUtil.getFilterMSISDN((String) p_requestMap.get("INTERFACE_ID"), (String) p_requestMap.get("MSISDN")) + "</string></value>");
            stringBuffer.append("</member>");
            // Set the transfer_amount to transactionAmountRefill
            stringBuffer.append("<member>");
            stringBuffer.append("<name>transactionAmountRefill</name>");
            stringBuffer.append("<value><string>" + p_requestMap.get("transfer_amount") + "</string></value>");
            stringBuffer.append("</member>");
            // Set the transaction_currency defined into INFile for
            // transactionCurrency.
            stringBuffer.append("<member>");
            stringBuffer.append("<name>transactionCurrency</name>");
            stringBuffer.append("<value><string>" + p_requestMap.get("transaction_currency") + "</string></value>");
            stringBuffer.append("</member>");
            // Set the card group for paymentProfileID
            stringBuffer.append("<member>");
            stringBuffer.append("<name>paymentProfileID</name>");
            stringBuffer.append("<value><string>" + p_requestMap.get("CARD_GROUP") + "</string></value>");
            stringBuffer.append("</member>");
            // Set the optional value of ExternalData1, if required.
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
     * @param p_map
     * @return
     * @throws Exception
     */
    private String generateRechargeCreditRequestSuperRefillT(HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateRechargeCreditRequestSuperRefillT", "Entered p_requestMap::" + p_requestMap);
        String requestStr = null;
        StringBuffer stringBuffer = null;

        try {
            stringBuffer = new StringBuffer(1028);
            stringBuffer.append("<?xml version='1.0'?>");
            stringBuffer.append("<methodCall>");
            stringBuffer.append("<methodName>SuperRefillT</methodName>");
            stringBuffer.append(startRequestTag());
            // Set the origin originNodeType
            stringBuffer.append("<member>");
            stringBuffer.append("<name>originNodeType</name>");
            stringBuffer.append("<value><string>" + p_requestMap.get("origin_node_type") + "</string></value>");
            stringBuffer.append("</member>");
            // Set the originHostName
            stringBuffer.append("<member>");
            stringBuffer.append("<name>originHostName</name>");
            stringBuffer.append("<value><string>" + p_requestMap.get("origin_host_name") + "</string></value>");
            stringBuffer.append("</member>");
            stringBuffer.append("<member>");
            // Set the originTransactionID
            stringBuffer.append("<name>originTransactionID</name>");
            stringBuffer.append("<value><string>" + getINTransactionID(p_requestMap) + "</string></value>");
            stringBuffer.append("</member>");
            // Set the originTimeStamp
            stringBuffer.append("<member>");
            stringBuffer.append("<name>originTimeStamp</name>");
            stringBuffer.append("<value><dateTime.iso8601>" + getCS3TransDateTime() + "</dateTime.iso8601></value>");
            stringBuffer.append("</member>");
            // Check the optional value of SubscriberNumberNAI if it is not null
            // set it.
            if (!InterfaceUtil.isNullString((String) p_requestMap.get("SubscriberNumberNAI"))) {
                stringBuffer.append("<member>");
                stringBuffer.append("<name>subscriberNumberNAI</name>");
                stringBuffer.append("<value><i4>" + p_requestMap.get("SubscriberNumberNAI") + "</i4></value>");
                stringBuffer.append("</member>");
            }
            // Set subscriberNumber and add or remove the msisdn if required.
            stringBuffer.append("<member>");
            stringBuffer.append("<name>subscriberNumber</name>");
            stringBuffer.append("<value><string>" + InterfaceUtil.getFilterMSISDN((String) p_requestMap.get("INTERFACE_ID"), (String) p_requestMap.get("MSISDN")) + "</string></value>");
            stringBuffer.append("</member>");
            // Set the transfer_amount to transactionAmountRefill
            stringBuffer.append("<member>");
            stringBuffer.append("<name>transactionAmountRefill</name>");
            stringBuffer.append("<value><string>" + p_requestMap.get("transfer_amount") + "</string></value>");
            stringBuffer.append("</member>");
            // Set the transaction_currency defined into INFile for
            // transactionCurrency.
            stringBuffer.append("<member>");
            stringBuffer.append("<name>transactionCurrency</name>");
            stringBuffer.append("<value><string>" + p_requestMap.get("transaction_currency") + "</string></value>");
            stringBuffer.append("</member>");
            // Set the card group for paymentProfileID
            stringBuffer.append("<member>");
            stringBuffer.append("<name>paymentProfileID</name>");
            stringBuffer.append("<value><string>" + p_requestMap.get("CARD_GROUP") + "</string></value>");
            stringBuffer.append("</member>");
            // Set the optional value of ExternalData1, if required.
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
            stringBuffer.append(endRequestTag());
            stringBuffer.append("</methodCall>");
            requestStr = stringBuffer.toString();
        }// end of try-block
        catch (Exception e) {
            _log.error("generateRechargeCreditRequestSuperRefillT", "Exception e: " + e);
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("generateRechargeCreditRequestSuperRefillT", "Exiting Request requestStr::" + requestStr);
        }// end of finally
        return requestStr;
    }// end of generateRechargeCreditRequestSuperRefillT

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
            stringBuffer.append("<methodName>AdjustmentTRequest</methodName>");
            stringBuffer.append(startRequestTag());
            // Set the origin node type
            stringBuffer.append("<member>");
            stringBuffer.append("<name>originNodeType</name>");
            stringBuffer.append("<value><string>" + p_requestMap.get("origin_node_type") + "</string></value>");
            stringBuffer.append("</member>");
            // Set the origin host type
            stringBuffer.append("<member>");
            stringBuffer.append("<name>originHostName</name>");
            stringBuffer.append("<value><string>" + p_requestMap.get("origin_host_name") + "</string></value>");
            stringBuffer.append("</member>");
            // Set the originTransactionID
            if ("Y".equals((String) p_requestMap.get("PRETUPS_ID_AS_ORGN_TXN_ID")))
                inTransactionID = getINReconTxnID(p_requestMap);
            else
                inTransactionID = getINTransactionID(p_requestMap);
            stringBuffer.append("<member>");
            stringBuffer.append("<name>originTransactionID</name>");
            stringBuffer.append("<value><string>" + inTransactionID + "</string></value>");
            stringBuffer.append("</member>");
            // Set the originTimeStamp
            stringBuffer.append("<member>");
            stringBuffer.append("<name>originTimeStamp</name>");
            stringBuffer.append("<value><dateTime.iso8601>" + getCS3TransDateTime() + "</dateTime.iso8601></value>");
            stringBuffer.append("</member>");

            // Set the optional parameter subscriberNumberNAI
            if (!InterfaceUtil.isNullString((String) p_requestMap.get("SubscriberNumberNAI"))) {
                stringBuffer.append("<member>");
                stringBuffer.append("<name>subscriberNumberNAI</name>");
                stringBuffer.append("<value><i4>" + p_requestMap.get("SubscriberNumberNAI") + "</i4></value>");
                stringBuffer.append("</member>");
            }
            // Set the subscriberNumber
            stringBuffer.append("<member>");
            stringBuffer.append("<name>subscriberNumber</name>");
            stringBuffer.append("<value><string>" + InterfaceUtil.getFilterMSISDN((String) p_requestMap.get("INTERFACE_ID"), (String) p_requestMap.get("MSISDN")) + "</string></value>");
            stringBuffer.append("</member>");
            // Set the optional parameter messageCapabilityFlag
            if (!InterfaceUtil.isNullString((String) p_requestMap.get("message_capability_flag"))) {
                stringBuffer.append("<member>");
                stringBuffer.append("<name>messageCapabilityFlag</name>");
                stringBuffer.append("<value><string>" + p_requestMap.get("message_capability_flag") + "</string></value>");
                stringBuffer.append("</member>");
            }
            // Set the transactionCurrency
            stringBuffer.append("<member>");
            stringBuffer.append("<name>transactionCurrency</name>");
            stringBuffer.append("<value><string>" + p_requestMap.get("transaction_currency") + "</string></value>");
            stringBuffer.append("</member>");
            // Set the adjustmentAmount,it may not present for validity or grace
            // adjustment.
            if (!InterfaceUtil.isNullString((String) p_requestMap.get("transfer_amount"))) {
                stringBuffer.append("<member>");
                stringBuffer.append("<name>adjustmentAmount</name>");
                stringBuffer.append("<value><string>" + p_requestMap.get("transfer_amount") + "</string></value>");
                stringBuffer.append("</member>");
            }
            // Set the optional parameter relativeDateAdjustmentSupervision
            if (!InterfaceUtil.isNullString((String) p_requestMap.get("relative_date_adjustment_supervision"))) {
                stringBuffer.append("<member>");
                stringBuffer.append("<name>relativeDateAdjustmentSupervision</name>");
                stringBuffer.append("<value><i4>" + p_requestMap.get("relative_date_adjustment_supervision") + "</i4></value>");
                stringBuffer.append("</member>");
            }
            // Set the optional parameter RelativeDateAdjustmentServiceFee
            if (!InterfaceUtil.isNullString((String) p_requestMap.get("relative_date_adjustment_service_fee"))) {
                stringBuffer.append("<member>");
                stringBuffer.append("<name>relativeDateAdjustmentServiceFee</name>");
                stringBuffer.append("<value><i4>" + p_requestMap.get("relative_date_adjustment_service_fee") + "</i4></value>");
                stringBuffer.append("</member>");
            }
            // Set the optional parameter ExternalData1
            if (!InterfaceUtil.isNullString((String) p_requestMap.get("ExternalData1"))) {
                stringBuffer.append("<member>");
                stringBuffer.append("<name>externalData1</name>");
                stringBuffer.append("<value><string>" + p_requestMap.get("ExternalData1") + "</string></value>");
                stringBuffer.append("</member>");
            }
            // Set the optional parameter ExternalData2
            if (!InterfaceUtil.isNullString((String) p_requestMap.get("ExternalData2"))) {
                stringBuffer.append("<member>");
                stringBuffer.append("<name>externalData2</name>");
                stringBuffer.append("<value><string>" + p_requestMap.get("ExternalData2") + "</string></value>");
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
        if (_log.isDebugEnabled())
            _log.debug("parseGetAccountInfoResponse", "Entered p_responseStr::" + p_responseStr);
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
                if (!"0".equals(responseCode))
                    return responseMap;
            }
            // TBD- AVINASH for parsing the AccountFlags.
            tempIndex = p_responseStr.indexOf("accountFlags", indexStart);
            if (tempIndex > 0) {
                String accountFlags = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex));
                responseMap.put("accountFlags", accountFlags.trim());
            }
            indexEnd = p_responseStr.indexOf("</member>", indexStart);
            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("accountValue1", indexStart);
            if (tempIndex > 0) {
                String accountValue1 = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("accountValue1", accountValue1.trim());
            }
            indexEnd = p_responseStr.indexOf("</member>", indexStart);
            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("creditClearanceDate", indexStart);
            if (tempIndex > 0) {
                String creditClearanceDate = p_responseStr.substring("<dateTime.iso8601>".length() + p_responseStr.indexOf("<dateTime.iso8601>", tempIndex), p_responseStr.indexOf("</dateTime.iso8601>", tempIndex)).trim();
                responseMap.put("creditClearanceDate", getDateString(creditClearanceDate));
            }
            indexEnd = p_responseStr.indexOf("</member>", indexStart);
            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("currency1", indexStart);
            if (tempIndex > 0) {
                String currency1 = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("currency1", currency1.trim());
            }
            indexEnd = p_responseStr.indexOf("</member>", indexStart);
            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("currentLanguageID", indexStart);
            if (tempIndex > 0) {
                String currentLanguageID = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("currentLanguageID", currentLanguageID.trim());
            }
            indexEnd = p_responseStr.indexOf("</member>", indexStart);
            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("firstIVRCallFlag", indexStart);
            if (tempIndex > 0) {
                String firstIVRCallFlag = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("firstIVRCallFlag", firstIVRCallFlag.trim());
            }
            indexEnd = p_responseStr.indexOf("</member>", indexStart);
            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("serviceClassCurrent", indexStart);
            if (tempIndex > 0) {
                String serviceClassCurrent = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("serviceClassCurrent", serviceClassCurrent.trim());
            }
            indexEnd = p_responseStr.indexOf("</member>", indexStart);
            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("serviceFeeDate", indexStart);
            if (tempIndex > 0) {
                String serviceFeeDate = p_responseStr.substring("<dateTime.iso8601>".length() + p_responseStr.indexOf("<dateTime.iso8601>", tempIndex), p_responseStr.indexOf("</dateTime.iso8601>", tempIndex)).trim();
                responseMap.put("serviceFeeDate", getDateString(serviceFeeDate));
            }
            indexEnd = p_responseStr.indexOf("</member>", indexStart);
            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("serviceRemovalDate", indexStart);
            if (tempIndex > 0) {
                String serviceRemovalDate = p_responseStr.substring("<dateTime.iso8601>".length() + p_responseStr.indexOf("<dateTime.iso8601>", tempIndex), p_responseStr.indexOf("</dateTime.iso8601>", tempIndex)).trim();
                responseMap.put("serviceRemovalDate", getDateString(serviceRemovalDate));
            }
            indexEnd = p_responseStr.indexOf("</member>", indexStart);
            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("supervisionDate", indexStart);
            if (tempIndex > 0) {
                String supervisionDate = p_responseStr.substring("<dateTime.iso8601>".length() + p_responseStr.indexOf("<dateTime.iso8601>", tempIndex), p_responseStr.indexOf("</dateTime.iso8601>", tempIndex)).trim();
                responseMap.put("supervisionDate", getDateString(supervisionDate));
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
                String faultCode = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex));
                responseMap.put("faultCode", faultCode.trim());
                tempIndex = p_responseStr.indexOf("faultString", tempIndex);
                String faultString = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex));
                responseMap.put("faultString", faultString.trim());
                return responseMap;
            }
            indexStart = p_responseStr.indexOf("<member>");
            tempIndex = p_responseStr.indexOf("responseCode", indexStart);
            // Check the value of response code if it 0 then continue the
            // parsing.
            if (tempIndex > 0) {
                responseCode = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("responseCode", responseCode.trim());
                if (!"0".equals(responseCode))
                    return responseMap;
            }
            tempIndex = p_responseStr.indexOf("accountValueAfter1", indexStart);
            if (tempIndex > 0) {
                String accountValueAfter1 = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex));
                responseMap.put("accountValueAfter1", accountValueAfter1.trim());
            }
            indexEnd = p_responseStr.indexOf("</member>", indexStart);
            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("currency1", indexStart);
            if (tempIndex > 0) {
                String currency1 = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("currency1", currency1.trim());
            }
            indexEnd = p_responseStr.indexOf("</member>", indexStart);
            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("rechargeAmount1MainTotal", indexStart);
            if (tempIndex > 0) {
                String rechargeAmount1MainTotal = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("rechargeAmount1MainTotal", rechargeAmount1MainTotal.trim());
            }
            indexEnd = p_responseStr.indexOf("</member>", indexStart);
            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("serviceClassCurrent", indexStart);
            if (tempIndex > 0) {
                String serviceClassCurrent = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("serviceClassCurrent", serviceClassCurrent.trim());
            }
            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("serviceFeeDateAfter", indexStart);
            if (tempIndex > 0) {
                String serviceFeeDateAfter = p_responseStr.substring("<dateTime.iso8601>".length() + p_responseStr.indexOf("<dateTime.iso8601>", tempIndex), p_responseStr.indexOf("</dateTime.iso8601>", tempIndex)).trim();
                responseMap.put("serviceFeeDateAfter", getDateString(serviceFeeDateAfter));
            }
            indexEnd = p_responseStr.indexOf("</member>", indexStart);
            indexEnd = p_responseStr.indexOf("</member>", indexStart);
            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("serviceFeeDaysTotalExt", indexStart);
            if (tempIndex > 0) {
                String serviceFeeDaysTotalExt = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("serviceFeeDaysTotalExt", serviceFeeDaysTotalExt.trim());
            }
            indexEnd = p_responseStr.indexOf("</member>", indexStart);
            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("supervisionDateAfter", indexStart);
            if (tempIndex > 0) {
                String supervisionDateAfter = p_responseStr.substring("<dateTime.iso8601>".length() + p_responseStr.indexOf("<dateTime.iso8601>", tempIndex), p_responseStr.indexOf("</dateTime.iso8601>", tempIndex)).trim();
                responseMap.put("supervisionDateAfter", getDateString(supervisionDateAfter));
            }
            indexEnd = p_responseStr.indexOf("</member>", indexStart);
            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("supervisionDays", indexStart);
            if (tempIndex > 0) {
                String supervisionDays = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("supervisionDays", supervisionDays);
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
     * This method is used to parse the credit response of super refillt.
     * 
     * @param p_responseStr
     * @return HashMap
     * @throws Exception
     */
    public HashMap parseRechargeCreditResponseSuperRRefillT(String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseRechargeCreditResponseSuperRRefillT", "Entered p_responseStr::" + p_responseStr);
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
                String faultCode = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex));
                responseMap.put("faultCode", faultCode.trim());
                tempIndex = p_responseStr.indexOf("faultString", tempIndex);
                String faultString = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex));
                responseMap.put("faultString", faultString.trim());
                return responseMap;
            }
            indexStart = p_responseStr.indexOf("<member>");
            tempIndex = p_responseStr.indexOf("responseCode", indexStart);
            // Check the value of response code if it 0 then continue the
            // parsing.
            if (tempIndex > 0) {
                responseCode = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("responseCode", responseCode.trim());
                if (!"0".equals(responseCode))
                    return responseMap;
            }
            tempIndex = p_responseStr.indexOf("accountValueAfter1", indexStart);
            if (tempIndex > 0) {
                String accountValueAfter1 = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex));
                responseMap.put("accountValueAfter1", accountValueAfter1.trim());
            }
            indexEnd = p_responseStr.indexOf("</member>", indexStart);
            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("currency1", indexStart);
            if (tempIndex > 0) {
                String currency1 = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("currency1", currency1.trim());
            }

            // by vikas
            indexEnd = p_responseStr.indexOf("</member>", indexStart);
            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("currentLanguageID", indexStart);
            if (tempIndex > 0) {
                String currentLanguageID = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("currentLanguageID", currentLanguageID.trim());
            }
            indexEnd = p_responseStr.indexOf("</member>", indexStart);
            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("originTransactionID", indexStart);
            if (tempIndex > 0) {
                String originTransactionID = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("originTransactionID", originTransactionID.trim());
            }
            // end vikas

            indexEnd = p_responseStr.indexOf("</member>", indexStart);
            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("rechargeAmount1MainTotal", indexStart);
            if (tempIndex > 0) {
                String rechargeAmount1MainTotal = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("rechargeAmount1MainTotal", rechargeAmount1MainTotal.trim());
            }
            indexEnd = p_responseStr.indexOf("</member>", indexStart);
            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("serviceClassCurrent", indexStart);
            if (tempIndex > 0) {
                String serviceClassCurrent = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("serviceClassCurrent", serviceClassCurrent.trim());
            }
            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("serviceFeeDateAfter", indexStart);
            if (tempIndex > 0) {
                String serviceFeeDateAfter = p_responseStr.substring("<dateTime.iso8601>".length() + p_responseStr.indexOf("<dateTime.iso8601>", tempIndex), p_responseStr.indexOf("</dateTime.iso8601>", tempIndex)).trim();
                responseMap.put("serviceFeeDateAfter", getDateString(serviceFeeDateAfter));
            }
            indexEnd = p_responseStr.indexOf("</member>", indexStart);
            indexEnd = p_responseStr.indexOf("</member>", indexStart);
            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("serviceFeeDaysTotalExt", indexStart);
            if (tempIndex > 0) {
                String serviceFeeDaysTotalExt = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("serviceFeeDaysTotalExt", serviceFeeDaysTotalExt.trim());
            }
            indexEnd = p_responseStr.indexOf("</member>", indexStart);
            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("supervisionDateAfter", indexStart);
            if (tempIndex > 0) {
                String supervisionDateAfter = p_responseStr.substring("<dateTime.iso8601>".length() + p_responseStr.indexOf("<dateTime.iso8601>", tempIndex), p_responseStr.indexOf("</dateTime.iso8601>", tempIndex)).trim();
                responseMap.put("supervisionDateAfter", getDateString(supervisionDateAfter));
            }
            indexEnd = p_responseStr.indexOf("</member>", indexStart);
            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("supervisionDays", indexStart);
            if (tempIndex > 0) {
                String supervisionDays = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("supervisionDays", supervisionDays);
            }

            // Start vikas
            indexEnd = p_responseStr.indexOf("</member>", indexStart);
            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("transactionAmount1Refill", indexStart);
            if (tempIndex > 0) {
                String transactionAmount1Refill = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("transactionAmount1Refill", transactionAmount1Refill.trim());
            }

            indexEnd = p_responseStr.indexOf("</member>", indexStart);
            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("transactionServiceClass", indexStart);
            if (tempIndex > 0) {
                String transactionServiceClass = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("transactionServiceClass", transactionServiceClass.trim());
            }

            indexEnd = p_responseStr.indexOf("</member>", indexStart);
            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("transactionVVPeriodExt", indexStart);
            if (tempIndex > 0) {
                String transactionVVPeriodExt = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("transactionVVPeriodExt", transactionVVPeriodExt.trim());
            }

            indexEnd = p_responseStr.indexOf("</member>", indexStart);
            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("valueVoucherDateAfter", indexStart);
            if (tempIndex > 0) {
                String valueVoucherDateAfter = p_responseStr.substring("<dateTime.iso8601>".length() + p_responseStr.indexOf("<dateTime.iso8601>", tempIndex), p_responseStr.indexOf("</dateTime.iso8601>", tempIndex)).trim();
                responseMap.put("valueVoucherDateAfter", getDateString(valueVoucherDateAfter));
            }

            // end vikas
        } catch (Exception e) {
            _log.error("parseRechargeCreditResponseSuperRRefillT", "Exception e::" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseRechargeCreditResponseSuperRRefillT", "Exited responseMap::" + responseMap);
        }// end of finally
        return responseMap;
    }// end of parseRechargeCreditResponseSuperRRefillT

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
            indexStart = p_responseStr.indexOf("<member>");
            tempIndex = p_responseStr.indexOf("responseCode", indexStart);
            if (tempIndex > 0) {
                responseCode = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("responseCode", responseCode.trim());
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

    public static synchronized String getIncrCounter() throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getIncrCounter", "Entered");
        try {
            long currentReqTime = System.currentTimeMillis();
            if (currentReqTime - _prevReqTime >= (60000))
                _counter = 1;
            else
                _counter++;
            _prevReqTime = currentReqTime;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("getIncrCounter", e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionValidation[validation]", "", "", "", " Error occurs while getting transaction id Exception is " + e.getMessage());
            throw new BTSLBaseException(e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getIncrCounter", "Exiting counter = " + _counter);
        }
        return String.valueOf(_counter);
    }

    public static void main(String[] args) throws Exception {

        HashMap requestMap = new HashMap();
        // String
        // responseStr="<?xml version=\"1.0\" encoding=\"utf-8\"?><methodResponse><params><param><value><struct><member><name>accountFlags</name><value><string>10000000</string></value></member><member><name>accountValue1</name><value><string>3340</string></value></member><member><name>creditClearanceDate</name><value><dateTime.iso8601>20080503T00:00:00+0000</dateTime.iso8601></value></member><member><name>currency1</name><value><string>JOD</string></value></member><member><name>currentLanguageID</name><value><i4>2</i4></value></member><member><name>responseCode</name><value><i4>0</i4></value></member><member><name>serviceClassCurrent</name><value><i4>105</i4></value></member><member><name>serviceFeeDate</name><value><dateTime.iso8601>20080403T00:00:00+0000</dateTime.iso8601></value></member><member><name>serviceRemovalDate</name><value><dateTime.iso8601>20080503T00:00:00+0000</dateTime.iso8601></value></member><member><name>supervisionDate</name><value><dateTime.iso8601>20080203T00:00:00+0000</dateTime.iso8601></value></member></struct></value></param></params></methodResponse>";
        String responseStr = "<?xml version=\"1.0\" encoding=\"utf-8\"?><methodResponse><params><param><value><struct><member><name>accountValueAfter1</name><value><string>3395</string></value></member><member><name>currency1</name><value><string>JOD</string></value></member><member><name>dedicatedAccountInformation</name><value><array><data><value><struct><member><name>dedicatedAccountID</name><value><i4>3</i4></value></member><member><name>rechargeAmount1DedicatedTotal</name><value><string>3</string></value></member></struct></value></data></array></value></member><member><name>rechargeAmount1MainTotal</name><value><string>20</string></value></member><member><name>responseCode</name><value><i4>0</i4></value></member><member><name>serviceClassCurrent</name><value><i4>105</i4></value></member><member><name>serviceFeeDateAfter</name><value><dateTime.iso8601>20080403T00:00:00+0000</dateTime.iso8601></value></member><member><name>supervisionDateAfter</name><value><dateTime.iso8601>20080203T00:00:00+0000</dateTime.iso8601></value></member></struct></value></param></params></methodResponse>";

        CS3RequestFormatter testCS3 = new CS3RequestFormatter();
        requestMap = testCS3.parseRechargeCreditResponse(responseStr);
        System.out.println("Response Map:->" + requestMap);
        /*
         * requestMap.put("origin_node_type","ETUPS");
         * requestMap.put("origin_host_name","DNS");
         * requestMap.put("PRETUPS_ID_AS_ORGN_TXN_ID","N");
         * requestMap.put("message_capability_flag","00000000");
         * requestMap.put("MSISDN","777898894");
         * requestMap.put("transfer_amount","20");
         * requestMap.put("transaction_currency","JOD");
         * requestMap.put("ExternalData1","ZEBRA");
         * requestMap.put("ExternalData2","R070807.1230.0032.0776222242");
         * requestMap.put("INSTANCE_ID","2");
         * requestMap.put("CARD_GROUP","Z0");
         */
        /*
         * requestMap.put("","");
         * requestMap.put("","");
         * requestMap.put("","");
         * requestMap.put("","");
         * requestMap.put("","");
         * requestMap.put("","");
         * requestMap.put("","");
         * requestMap.put("","");
         * requestMap.put("","");
         */

        // System.out.println(" RequestStr: "+testCS3.generateGetAccountInfoRequest(requestMap));
        // System.out.println(" RequestStr: "+testCS3.generateRechargeCreditRequest(requestMap));
        // Prepare the requestMap.

        /*
         * StringBuffer response= new StringBuffer(
         * "<?xml version=\"1.0\" encoding=\"utf-8\"?><methodResponse><params><param><value><struct>"
         * );
         * response.append(
         * "<member><name>accountFlags</name><value><string>10001111</string></value></member>"
         * );
         * response.append(
         * "<member><name>accountValue1</name><value><string>1 </string></value></member>"
         * );
         * response.append(
         * "<member><name>creditClearanceDate</name><value><dateTime.iso8601>20051015T00:00:00+0000</dateTime.iso8601></value></member>"
         * );
         * response.append(
         * "<member><name>currency1</name><value><string>BDT</string></value></member>"
         * );
         * response.append(
         * "<member><name>currentLanguageID</name><value><i4>1</i4></value></member>"
         * );
         * response.append(
         * "<member><name>firstIVRCallFlag</name><value><string>1</string></value></member>"
         * );
         * response.append(
         * "<member><name>responseCode</name><value><i4>10</i4></value></member>"
         * );
         * response.append(
         * "<member><name>serviceClassCurrent</name><value><i4>9999</i4></value></member>"
         * );
         * response.append(
         * "<member><name>serviceFeeDate</name><value><dateTime.iso8601>20050930T00:00:00+0000</dateTime.iso8601></value></member>"
         * );
         * response.append(
         * "<member><name>serviceRemovalDate</name><value><dateTime.iso8601>20051015T00:00:00+0000</dateTime.iso8601></value></member>"
         * );
         * response.append(
         * "<member><name>supervisionDate</name><value><dateTime.iso8601>20050930T00:00:00+0000</dateTime.iso8601></value></member>"
         * );
         * response.append("</struct></value></param></params></methodResponse>")
         * ;
         * String respStr = response.toString();
         */
        /*
         * 2.) REFILL String
         */
        /*
         * StringBuffer response= new StringBuffer(
         * "<?xml version=\"1.0\" encoding=\"utf-8\"?><methodResponse><params><param><value><struct>"
         * );
         * response.append(
         * "<member><name>accountValueAfter1</name><value><string>71050</string></value></member>"
         * );
         * response.append(
         * "<member><name>currency1</name><value><string>BDT</string></value></member>"
         * );
         * response.append(
         * "<member><name>rechargeAmount1MainTotal</name><value><string>50</string></value></member>"
         * );
         * response.append(
         * "<member><name>responseCode</name><value><i4>0</i4></value></member>"
         * );
         * response.append(
         * "<member><name> serviceClassCurrent</name><value><i4>9999</i4></value></member>"
         * );
         * response.append(
         * "<member><name> serviceFeeDateAfter</name><value><dateTime.iso8601>20051007T00:00:00+0000</dateTime.iso8601></value></member>"
         * );
         * response.append(
         * "<member><name>serviceFeeDaysTotalExt</name><value><i4>1</i4></value></member>"
         * );
         * response.append(
         * "<member><name>supervisionDateAfter</name><value><dateTime.iso8601>20051007T00:00:00+0000</dateTime.iso8601></value></member>"
         * );
         * response.append(
         * "<member><name>supervisionDays TotalExt</name><value><i4>1</i4></value></member>"
         * );
         * response.append("</struct></value></param></params></methodResponse>")
         * ;
         * String respStr = response.toString();
         * System.out.println("Request String "+respStr);
         */
        /*
         * 3.) AdjustmentT
         */

        /*
         * StringBuffer response= new StringBuffer(
         * "<?xml version=\"1.0\"?><methodResponse><fault><value><struct>");
         * response.append(
         * "<member><name>faultCode</name><value><int>1001</int></value></member>"
         * );
         * response.append(
         * "<member><name>faultString</name><value><string>Mandatory field missing.</string></value></member>"
         * );
         * response.append("</struct></value></fault></methodResponse>");
         * String respStr = response.toString();
         */

        /*
         * StringBuffer response= new StringBuffer(
         * "<?xml version=\"1.0\" encoding=\"utf-8\"?><methodResponse><params><param><value><struct>"
         * );
         * response.append(
         * "<member><name>responseCode</name><value><i4>30000</i4></value></member>"
         * );
         * response.append("</struct></value></param></params></methodResponse>")
         * ;
         * String respStr = response.toString();
         * 
         * System.out.println("Request String "+respStr);
         */
        // String lineSep=System.getProperty("line.separator");
        // System.out.println("lineSep::");
        /*
         * String lineSep="\n";
         * StringBuffer response= new
         * StringBuffer("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
         * response.append("<methodResponse>");
         * response.append("<params>");
         * response.append("<param>");
         * response.append("<value>");
         * response.append("<struct>");
         * response.append("<member>");
         * response.append("<name>responseCode</name>");
         * response.append("<value><i4>30000</i4></value>");
         * response.append("</member>");
         * response.append("</struct>");
         * response.append("</value>");
         * response.append("</param>");
         * response.append("</params>");
         * response.append("</methodResponse>");
         * String respStr = response.toString();
         * 
         * System.out.println("Request String "+respStr);
         * CS3RequestFormatter cs3Parser = new CS3RequestFormatter();
         * System.out.println("Map :: "+cs3Parser.parseResponse(1,respStr));
         */
        // String f
        // =cs3Parser.getCS3TransDateTime("yyyyMMdd'T'HH:mm:ss+SSSS","UTC");
        // System.out.println("date ::"+f);
        /*
         * SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss");
         * TimeZone timeZone = TimeZone.getDefault();
         * sdf.setTimeZone(timeZone);
         * DecimalFormat twoDigits = new DecimalFormat("00");
         * Date now = new Date();
         * int offset = sdf.getTimeZone().getOffset(now.getTime());
         * System.out.println(offset);
         * String sign = "+";
         * if (offset < 0) {
         * offset = -offset;
         * sign = "-";
         * }
         * int hours = offset / 3600000;
         * int minutes = (offset - hours * 3600000) / 60000;
         * String transDateTime = sdf.format(now) + sign +
         * twoDigits.format(hours) + twoDigits.format(minutes);
         * System.out.println("transDateTime ::"+transDateTime);
         */
        /*
         * Thread th = new Thread(new CS3RequestFormatter());
         * for(int i=0;i<120;i++)
         * th.run();
         */

    }

}
