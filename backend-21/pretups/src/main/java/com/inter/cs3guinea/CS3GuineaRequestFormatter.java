package com.inter.cs3guinea;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.util.BTSLUtil;

/**
 * @CS3GuineaRequestFormatter.java
 *                                 Copyright(c) 2009, Bharti Telesoft Int.
 *                                 Public Ltd.
 *                                 All Rights Reserved
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Author Date History
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Chetan Kothari March 13,2009 Initial Creation
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 ------
 *                                 This class will format all the request and
 *                                 parse all responses used in CS3CP5 IN
 *                                 handling.
 */
public class CS3GuineaRequestFormatter {
    public static Log _log = LogFactory.getLog(CS3GuineaRequestFormatter.class);
    private SimpleDateFormat _sdf = null;
    private TimeZone _timeZone = null;
    private String _transDateFormat = "yyyyMMdd'T'HH:mm:ss";// Defines the Date
                                                            // and time format
                                                            // of CS3GuineaIN.
    private DecimalFormat _twoDigits = null;
    private int _offset;
    private String _sign = "+";
    private int _hours;
    private int _minutes;

    public CS3GuineaRequestFormatter() throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("CS3GuineaRequestFormatter[constructor]", "Entered");
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
            _log.error("CS3GuineaRequestFormatter[constructor]", "Exception e::" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("CS3GuineaRequestFormatter[constructor]", "Exited");
        }// end of finally
    }// end of CS3GuineaRequestFormatter[constructor]

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
            case CS3GuineaI.ACTION_ACCOUNT_INFO: {
                str = generateGetAccountInfoRequest(p_map);
                break;
            }
            case CS3GuineaI.ACTION_RECHARGE_CREDIT: {
                str = generateRechargeCreditRequest(p_map);
                break;
            }
            case CS3GuineaI.ACTION_IMMEDIATE_DEBIT: {
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
    public HashMap parseResponse(int p_action, String p_responseStr, String p_cardGrpSel, String p_serviceType, String p_interfaceID) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseResponse", "Entered p_action::" + p_action + " p_responseStr:: " + p_responseStr);
        HashMap map = null;
        try {
            switch (p_action) {
            case CS3GuineaI.ACTION_ACCOUNT_INFO: {
                map = parseGetAccountInfoResponse(p_responseStr, p_cardGrpSel, p_serviceType, p_interfaceID);
                break;
            }
            case CS3GuineaI.ACTION_RECHARGE_CREDIT: {
                map = parseRechargeCreditResponse(p_responseStr, p_cardGrpSel, p_serviceType, p_interfaceID);
                break;
            }
            case CS3GuineaI.ACTION_IMMEDIATE_DEBIT: {
                map = parseImmediateDebitResponse(p_responseStr, p_cardGrpSel, p_serviceType, p_interfaceID);
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

    private String generateGetAccountInfoRequest(HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateGetAccountInfoRequest", "Entered p_requestMap::" + p_requestMap);
        String requestStr = null;
        StringBuffer stringBuffer = null;
        try {
            stringBuffer = new StringBuffer(1028);
            stringBuffer.append("<?xml version='1.0'?>");
            stringBuffer.append("<methodCall>");
            stringBuffer.append("<methodName>BalanceEnquiryTRequest</methodName>");
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
        try {
            stringBuffer = new StringBuffer(1028);
            stringBuffer.append("<?xml version='1.0'?>");
            stringBuffer.append("<methodCall>");
            stringBuffer.append("<methodName>RefillTRequest</methodName>");
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
            // Set the transfer_amount to transactionAmount
            stringBuffer.append("<member>");
            stringBuffer.append("<name>transactionAmountRefill</name>");
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
            stringBuffer.append("<name>paymentProfileID</name>");
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

    private HashMap parseGetAccountInfoResponse(String p_responseStr, String p_cardGrpSel, String p_serviceType, String p_interfaceID) throws Exception {
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

            indexStart = p_responseStr.indexOf("<member><name>responseCode");
            tempIndex = p_responseStr.indexOf("responseCode", indexStart);
            if (tempIndex > 0) {
                responseCode = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("responseCode", responseCode.trim());
                Object[] successList = CS3GuineaI.RESULT_OK.split(",");
                // if(!CS3GuineaI.RESULT_OK.equals(responseCode))
                if (!Arrays.asList(successList).contains(responseCode))
                    return responseMap;
            }

            indexStart = p_responseStr.indexOf("<member><name>accountValue1", indexEnd);
            tempIndex = p_responseStr.indexOf("accountValue1", indexStart);
            if (tempIndex > 0) {
                String accountValue1 = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("accountValue1", accountValue1.trim());
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member><name>serviceClassCurrent", indexEnd);
            tempIndex = p_responseStr.indexOf("serviceClassCurrent", indexStart);
            if (tempIndex > 0) {
                String serviceClassCurrent = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("serviceClassCurrent", serviceClassCurrent.trim());
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member><name>supervisionDate", indexEnd);
            tempIndex = p_responseStr.indexOf("supervisionDate", indexStart);
            if (tempIndex > 0) {
                String supervisionExpiryDate = p_responseStr.substring("<dateTime.iso8601>".length() + p_responseStr.indexOf("<dateTime.iso8601>", tempIndex), p_responseStr.indexOf("</dateTime.iso8601>", tempIndex)).trim();
                responseMap.put("supervisionDate", getDateString(supervisionExpiryDate.trim()));
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member><name>serviceFeeDate", indexEnd);
            tempIndex = p_responseStr.indexOf("serviceFeeDate", indexStart);
            if (tempIndex > 0) {
                String serviceFeeExpiryDate = p_responseStr.substring("<dateTime.iso8601>".length() + p_responseStr.indexOf("<dateTime.iso8601>", tempIndex), p_responseStr.indexOf("</dateTime.iso8601>", tempIndex)).trim();
                responseMap.put("serviceFeeDate", getDateString(serviceFeeExpiryDate.trim()));
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member><name>serviceRemovalDate", indexEnd);
            tempIndex = p_responseStr.indexOf("serviceRemovalDate", indexStart);
            if (tempIndex > 0) {
                String serviceRemovalDate = p_responseStr.substring("<dateTime.iso8601>".length() + p_responseStr.indexOf("<dateTime.iso8601>", tempIndex), p_responseStr.indexOf("</dateTime.iso8601>", tempIndex)).trim();
                responseMap.put("serviceRemovalDate", getDateString(serviceRemovalDate.trim()));
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member><name>currentLanguageID", indexEnd);
            tempIndex = p_responseStr.indexOf("currentLanguageID", indexStart);
            if (tempIndex > 0) {
                String languageIDCurrent = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("languageIDCurrent", languageIDCurrent.trim());
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member><name>temporaryBlockedFlag", indexEnd);
            tempIndex = p_responseStr.indexOf("temporaryBlockedFlag", indexStart);
            if (tempIndex > 0) {
                String temporaryBlockedFlag = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("temporaryBlockedFlag", getDateString(temporaryBlockedFlag));
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            if (!InterfaceUtil.isNullString(p_cardGrpSel) && !"1".equals(p_cardGrpSel) && "PRC".equals(p_serviceType)) {
                String dedicatedArr[] = FileCache.getValue(p_interfaceID, "DEDICATED_ACC_IDS").split(",");

                HashMap dedicatedMap = new HashMap();
                String cardSelector = null;
                String dedicatedAccoutId = null;
                for (int k = 0; k < dedicatedArr.length; k++) {
                    cardSelector = dedicatedArr[k].split(":")[0];
                    dedicatedAccoutId = dedicatedArr[k].split(":")[1];
                    dedicatedMap.put(cardSelector, dedicatedAccoutId);
                }

                setDedicatedAccountInfoInToMap(p_responseStr, responseMap);
                String dedicatedAnct = (String) dedicatedMap.get(p_cardGrpSel);
                String dedAcntValue = (String) responseMap.get(dedicatedAnct);
                System.out.println("dedicatedAnct :" + dedicatedAnct + "dedAcntValue :" + dedAcntValue);
                if (InterfaceUtil.isNullString(dedAcntValue)) {
                    _log.error("parseGetAccountInfoResponse", "Dedicated Account " + dedicatedAnct + " not defined at IN");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3GuienaINHandler[creditAdjust]", "", "", "", "Dedicated Account " + dedicatedAnct + " not defined at IN");
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_PARAMETER_MISSING);
                }
                responseMap.put("accountValue1", dedAcntValue);
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

    private static void setDedicatedAccountInfoInToMap(String p_requestStr, HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("setBundlesInfoInToMap ", "Entered p_requestStr: " + p_requestStr);
        int index = 0;
        int noOfAcnts = 0;

        try {
            if (!BTSLUtil.isNullString(p_requestStr)) {
                String[] acntArray = p_requestStr.split("dedicatedAccountID");
                noOfAcnts = acntArray.length;
                System.out.println("noOfAcnts :" + noOfAcnts);
                for (int i = 1; i < noOfAcnts; i++) {
                    System.out.println(acntArray[i]);
                    String dedicatedAcntId = null;
                    String dedicatedAcntValue = null;
                    System.out.println("acntArray[i] :" + acntArray[i]);
                    // get the bdl_type if present in the bucket.
                    index = acntArray[i].indexOf("</name><value><i4>");
                    System.out.println("index :" + index);

                    if (index != -1)
                        dedicatedAcntId = acntArray[i].substring(index + "</name><value><i4>".length(), acntArray[i].indexOf("</i4></value>", index));

                    System.out.println("dedicatedAcntId :" + dedicatedAcntId);
                    // get the bdl_name if present in the bucket.
                    index = acntArray[i].indexOf("dedicatedAccountValue1</name><value><string>");
                    if (index != -1)
                        dedicatedAcntValue = acntArray[i].substring(index + "dedicatedAccountValue1</name><value><string>".length(), acntArray[i].indexOf("</string></value>", index));
                    System.out.println("dedicatedAcntValue :" + dedicatedAcntValue);
                    p_requestMap.put(dedicatedAcntId.trim(), dedicatedAcntValue.trim());
                }
            }
            System.out.println("p_requestMap :" + p_requestMap);
        } catch (Exception e) {
            // _log.error("setDedicatedAccountInfoInToMap","Exception e: "+e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("setDedicatedAccountInfoInToMap ", "Exiting noOfAcnts: " + noOfAcnts);
        }
    }

    /**
     * This method is used to parse the credit response.
     * 
     * @param p_responseStr
     * @return HashMap
     * @throws Exception
     */
    public HashMap parseRechargeCreditResponse(String p_responseStr, String p_cardGrpSel, String p_serviceType, String p_interfaceID) throws Exception {
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

            indexStart = p_responseStr.indexOf("<member><name>responseCode");
            tempIndex = p_responseStr.indexOf("responseCode", indexStart);
            if (tempIndex > 0) {
                responseCode = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("responseCode", responseCode.trim());
                Object[] successList = CS3GuineaI.RESULT_OK.split(",");
                // if(!CS3GuineaI.RESULT_OK.equals(responseCode))
                if (!Arrays.asList(successList).contains(responseCode))
                    return responseMap;
            }

            indexStart = p_responseStr.indexOf("<member><name>originTransactionID", indexEnd);
            tempIndex = p_responseStr.indexOf("originTransactionID", indexStart);
            if (tempIndex > 0) {
                String originTransactionID = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("originTransactionID", originTransactionID.trim());
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexEnd = p_responseStr.indexOf("</member>", indexStart);
            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("rechargeAmount1MainTotal", indexStart);
            if (tempIndex > 0) {
                String rechargeAmount1MainTotal = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("rechargeAmount1MainTotal", rechargeAmount1MainTotal.trim());
            }
            indexStart = p_responseStr.indexOf("<member><name>serviceFeeDateAfter", indexEnd);
            tempIndex = p_responseStr.indexOf("serviceFeeDateAfter", indexStart);
            if (tempIndex > 0) {
                String serviceFeeExpiryDate = p_responseStr.substring("<dateTime.iso8601>".length() + p_responseStr.indexOf("<dateTime.iso8601>", tempIndex), p_responseStr.indexOf("</dateTime.iso8601>", tempIndex)).trim();
                responseMap.put("serviceFeeDateAfter", getDateString(serviceFeeExpiryDate.trim()));
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member><name>supervisionDateAfter", indexEnd);
            tempIndex = p_responseStr.indexOf("supervisionDateAfter", indexStart);
            if (tempIndex > 0) {
                String supervisionExpiryDate = p_responseStr.substring("<dateTime.iso8601>".length() + p_responseStr.indexOf("<dateTime.iso8601>", tempIndex), p_responseStr.indexOf("</dateTime.iso8601>", tempIndex)).trim();
                responseMap.put("supervisionDateAfter", getDateString(supervisionExpiryDate.trim()));
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member><name>accountValueAfter1", indexEnd);
            tempIndex = p_responseStr.indexOf("accountValueAfter1", indexStart);
            if (tempIndex > 0) {
                String accountValue1 = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("accountValueAfter1", accountValue1.trim());
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }
        } catch (Exception e) {
            _log.error("parseRechargeCreditResponse", "Exception e::" + e.getMessage());
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
    private HashMap parseImmediateDebitResponse(String p_responseStr, String p_cardGrpSel, String p_serviceType, String p_interfaceID) throws Exception {
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
                String faultCode = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</4>", tempIndex));
                responseMap.put("faultCode", faultCode.trim());
                tempIndex = p_responseStr.indexOf("faultString", tempIndex);
                String faultString = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex));
                responseMap.put("faultString", faultString.trim());
                return responseMap;
            }

            indexStart = p_responseStr.indexOf("<member><name>responseCode");
            tempIndex = p_responseStr.indexOf("responseCode", indexStart);
            if (tempIndex > 0) {
                responseCode = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("responseCode", responseCode.trim());
                Object[] successList = CS3GuineaI.RESULT_OK.split(",");
                if (!Arrays.asList(successList).contains(responseCode))
                    return responseMap;
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

    private String generateImmediateDebitRequest(HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateImmediateDebitRequest", "Entered p_requestMap::" + p_requestMap);
        String requestStr = null;
        StringBuffer stringBuffer = null;
        try {
            stringBuffer = new StringBuffer(1028);
            stringBuffer.append("<?xml version='1.0'?>");
            stringBuffer.append("<methodCall>");
            stringBuffer.append("<methodName>AdjustmentTRequest</methodName>");
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

            // Set messageCapabilityFlag
            stringBuffer.append("<member>");
            stringBuffer.append("<name>messageCapabilityFlag</name>");
            stringBuffer.append("<value><string>00000000</string></value>");
            stringBuffer.append("</member>");

            // Set the transaction_currency defined into INFile for
            // transactionCurrency.
            stringBuffer.append("<member>");
            stringBuffer.append("<name>transactionCurrency</name>");
            stringBuffer.append("<value><string>" + p_requestMap.get("CURRENCY") + "</string></value>");
            stringBuffer.append("</member>");
            // Set the transfer_amount to transactionAmount
            if (((String) p_requestMap.get("CARD_GROUP_SELECTOR")).equals(String.valueOf(PretupsI.CHNL_SELECTOR_CVG_VALUE))) {
                stringBuffer.append("<member>");
                stringBuffer.append("<name>adjustmentAmount</name>");
                stringBuffer.append("<value><string>" + p_requestMap.get("transfer_amount") + "</string></value>");
                stringBuffer.append("</member>");

                String validityDays = (String) p_requestMap.get("supervisionExpiryDateRelative");
                if ((!InterfaceUtil.isNullString(validityDays)) && !"0".equals(validityDays)) {
                    stringBuffer.append("<member>");
                    stringBuffer.append("<name>relativeDateAdjustmentSupervision</name>");
                    stringBuffer.append("<value><i4>" + validityDays + "</i4></value>");
                    stringBuffer.append("</member>");
                }

                String graceDays = (String) p_requestMap.get("serviceFeeExpiryDateRelative");
                if (!InterfaceUtil.isNullString(graceDays) && !"0".equals(graceDays)) {
                    stringBuffer.append("<member>");
                    stringBuffer.append("<name>relativeDateAdjustmentServiceFee</name>");
                    stringBuffer.append("<value><i4>" + graceDays + "</i4></value>");
                    stringBuffer.append("</member>");
                }
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
            String dedicatedArr[] = ((String) p_requestMap.get("DEDICATED_ACC_INFO")).split(",");
            HashMap dedicatedMap = new HashMap();
            String cardSelector = null;
            String dedicatedAccoutId = null;
            for (int k = 0; k < dedicatedArr.length; k++) {
                cardSelector = dedicatedArr[k].split(":")[0];
                dedicatedAccoutId = dedicatedArr[k].split(":")[1];
                dedicatedMap.put(cardSelector, dedicatedAccoutId);
            }
            if (dedicatedMap.get((String) p_requestMap.get("CARD_GROUP_SELECTOR")) != null) {
                stringBuffer.append("<member>");
                stringBuffer.append("<name>dedicatedAccountInformation</name>");
                stringBuffer.append("<value><array><data><value><struct>");
                stringBuffer.append("<member>");
                stringBuffer.append("<name>dedicatedAccountID</name>");
                stringBuffer.append("<value><string>" + dedicatedMap.get((String) p_requestMap.get("CARD_GROUP_SELECTOR")) + "</string></value>");
                stringBuffer.append("</member>");
                stringBuffer.append("<member>");
                stringBuffer.append("<name>adjustmentAmount</name>");
                stringBuffer.append("<value><string>" + p_requestMap.get("transfer_amount") + "</string></value>");
                stringBuffer.append("</member>");
                stringBuffer.append("</struct></value></data></array></value>");
                stringBuffer.append("</member>");

                // stringBuffer.append("</methodCall>");
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

}
