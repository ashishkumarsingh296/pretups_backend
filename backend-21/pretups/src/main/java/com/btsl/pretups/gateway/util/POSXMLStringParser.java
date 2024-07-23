/*
s * @(#)POSPOSXMLStringParser.java
 * Copyright(c) 2009, Comviva Ltd.
 * All Rights Reserved
 * ------------------------------------------------------------------------------
 * -------------------
 * Author Date History
 * ------------------------------------------------------------------------------
 * -------------------
 * Manisha Jain 08/12/09 Initial Creation
 * ------------------------------------------------------------------------------
 * -------------------
 * This parser is user when xml request is arrived from External system to
 * pretups system
 * Authenticate user on the basis of loginid and PIN of user
 */
package com.btsl.pretups.gateway.util;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.product.businesslogic.ChannelTransfrsReturnsVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.voms.util.DESEVDEncryption;

public class POSXMLStringParser {

    public static String CHNL_MESSAGE_SEP = null;
    public static final Log _log = LogFactory.getLog(POSXMLStringParser.class.getName());
    private static final String CLASS_NAME = "POSXMLStringParser";
    
    /**
   	 * ensures no instantiation
   	 */
    private POSXMLStringParser(){
    	
    }
    
    static {
        try {
            CHNL_MESSAGE_SEP = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
            if (BTSLUtil.isNullString(CHNL_MESSAGE_SEP)) {
                CHNL_MESSAGE_SEP = " ";
            }
        } catch (Exception e) {
            _log.errorTrace("static", e);
        }
    }

    public static void parseChannelExtCreditTransferRequest(RequestVO p_requestVO) throws Exception {
        final String methodName = "parseChannelExtCreditTransferRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        final int out_of_bound = -1;
        try {
            final HashMap requestHashMap = new HashMap();
            final String requestStr = p_requestVO.getRequestMessage();
            String msisdn = null;
            String language1 = null;
            String language2 = null;
            String extRefNumber = null;
            String password = null;
            String extCode = null;
            String date = null;
            String info1 = null;
            String info2 = null;
            String info3 = null;
            String info4 = null;
            String info5 = null;
            String info6 = null;
            String info7 = null;
            String info8 = null;
            String info9 = null;
            String info10 = null;
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            requestHashMap.put("TYPE", type);
            index = requestStr.indexOf("<DATE>");
            if (index > 0) {
                date = requestStr.substring(index + "<DATE>".length(), requestStr.indexOf("</DATE>", index));
                requestHashMap.put("DATE", date);
            }
            index = requestStr.indexOf("<EXTNWCODE>");
            final String extNwCode = requestStr.substring(index + "<EXTNWCODE>".length(), requestStr.indexOf("</EXTNWCODE>", index));
            requestHashMap.put("EXTNWCODE", extNwCode);
            index = requestStr.indexOf("<MSISDN>");
            if (index > 0) {
                msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
                requestHashMap.put("MSISDN", msisdn);
            }
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            requestHashMap.put("PIN", pin);
            index = requestStr.indexOf("<LOGINID>");
            final String loginId = requestStr.substring(index + "<LOGINID>".length(), requestStr.indexOf("</LOGINID>", index));
            requestHashMap.put("LOGINID", loginId);

            index = requestStr.indexOf("<PASSWORD>");
            if (index > 0) {
                password = requestStr.substring(index + "<PASSWORD>".length(), requestStr.indexOf("</PASSWORD>", index));
                requestHashMap.put("PASSWORD", password);
            }
            index = requestStr.indexOf("<EXTCODE>");
            if (index > 0) {
                extCode = requestStr.substring(index + "<EXTCODE>".length(), requestStr.indexOf("</EXTCODE>", index));
                requestHashMap.put("EXTCODE", extCode);
            }
            index = requestStr.indexOf("<EXTREFNUM>");
            if (index > 0) {
                extRefNumber = requestStr.substring(index + "<EXTREFNUM>".length(), requestStr.indexOf("</EXTREFNUM>", index));
                requestHashMap.put("EXTREFNUM", extRefNumber);
            }
            index = requestStr.indexOf("<MSISDN2>");
            final String msisdn2 = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));
            index = requestStr.indexOf("<AMOUNT>");
            final String amount = requestStr.substring(index + "<AMOUNT>".length(), requestStr.indexOf("</AMOUNT>", index));
            index = requestStr.indexOf("<LANGUAGE1>");
            if (index > 0) {
                language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
            }
            index = requestStr.indexOf("<LANGUAGE2>");
            if (index > 0) {
                language2 = requestStr.substring(index + "<LANGUAGE2>".length(), requestStr.indexOf("</LANGUAGE2>", index));
            }
            index = requestStr.indexOf("<SELECTOR>");
            String selector = requestStr.substring(index + "<SELECTOR>".length(), requestStr.indexOf("</SELECTOR>", index));
            index = requestStr.indexOf("<INFO1>");
            if (index != -1) {
                info1 = requestStr.substring(index + "<INFO1>".length(), requestStr.indexOf("</INFO1>", index));
            }
            index = requestStr.indexOf("<INFO2>");
            if (index != -1) {
                info2 = requestStr.substring(index + "<INFO2>".length(), requestStr.indexOf("</INFO2>", index));
            }
            index = requestStr.indexOf("<INFO3>");
            if (index != -1) {
                info3 = requestStr.substring(index + "<INFO3>".length(), requestStr.indexOf("</INFO3>", index));
            }
            index = requestStr.indexOf("<INFO4>");
            if (index != -1) {
                info4 = requestStr.substring(index + "<INFO4>".length(), requestStr.indexOf("</INFO4>", index));
            }
            index = requestStr.indexOf("<INFO5>");
            if (index != -1) {
                info5 = requestStr.substring(index + "<INFO5>".length(), requestStr.indexOf("</INFO5>", index));
            }
            index = requestStr.indexOf("<INFO6>");
            if (index != -1) {
                info6 = requestStr.substring(index + "<INFO6>".length(), requestStr.indexOf("</INFO6>", index));
            }
            index = requestStr.indexOf("<INFO7>");
            if (index != -1) {
                info7 = requestStr.substring(index + "<INFO7>".length(), requestStr.indexOf("</INFO7>", index));
            }
            index = requestStr.indexOf("<INFO8>");
            if (index != -1) {
                info8 = requestStr.substring(index + "<INFO8>".length(), requestStr.indexOf("</INFO8>", index));
            }
            index = requestStr.indexOf("<INFO9>");
            if (index != -1) {
                info9 = requestStr.substring(index + "<INFO9>".length(), requestStr.indexOf("</INFO9>", index));
            }
            index = requestStr.indexOf("<INFO10>");
            if (index != -1) {
                info10 = requestStr.substring(index + "<INFO10>".length(), requestStr.indexOf("</INFO10>", index));
            }
            index = requestStr.indexOf("<RECMSG>");
            String recmsg = "Y";// if there is no tag value setting default
            // value to Y for sending SMS
            if (index != out_of_bound) {
                recmsg = requestStr.substring(index + "<RECMSG>".length(), requestStr.indexOf("</RECMSG>", index));
            }

            if (BTSLUtil.isNullString(selector)) {
                final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(PretupsI.SERVICE_TYPE_CHNL_RECHARGE);
                if (serviceSelectorMappingVO != null) {
                    selector = serviceSelectorMappingVO.getSelectorCode();
                }
            }
            if (BTSLUtil.isNullString(type) || BTSLUtil.isNullString(extNwCode) || BTSLUtil.isNullString(pin) || BTSLUtil.isNullString(msisdn2) || BTSLUtil
                .isNullString(amount) || BTSLUtil.isNullString(loginId)) {
                throw new BTSLBaseException("POSXMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }
            if (BTSLUtil.isNullString(amount)) {
                _log.error(methodName, "Amount field is null ");
                throw new BTSLBaseException("POSXMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_BLANK_AMOUNT);
            }
            if (!(BTSLUtil.isNullString(info1))) {
                final int length = info1.length();
                if (length > 100) {
                    _log.debug(methodName, "length is " + length);
                    _log.error(methodName, "Characters are more than 100 ");
                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.C2S_ERROR_MAX_100_INFO1);
                }
            }
            if (!(BTSLUtil.isNullString(info2))) {
                final int length = info2.length();
                if (length > 100) {
                    _log.debug(methodName, "length is " + length);
                    _log.error(methodName, "Characters are more than 100 ");
                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.C2S_ERROR_MAX_100_INFO2);
                }
            }
            if (!(BTSLUtil.isNullString(info3))) {
                final int length = info3.length();
                if (length > 100) {
                    _log.debug(methodName, "length is " + length);
                    _log.error(methodName, "Characters are more than 100 ");
                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.C2S_ERROR_MAX_100_INFO3);
                }
            }
            if (!(BTSLUtil.isNullString(info4))) {
                final int length = info4.length();
                if (length > 100) {
                    _log.debug(methodName, "length is " + length);
                    _log.error(methodName, "Characters are more than 100 ");
                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.C2S_ERROR_MAX_100_INFO4);
                }
            }
            if (!(BTSLUtil.isNullString(info5))) {
                final int length = info5.length();
                if (length > 100) {
                    _log.debug(methodName, "length is " + length);
                    _log.error(methodName, "Characters are more than 100 ");
                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.C2S_ERROR_MAX_100_INFO5);
                }
            }
            if (!(BTSLUtil.isNullString(info6))) {
                final int length = info6.length();
                if (length > 100) {
                    _log.debug(methodName, "length is " + length);
                    _log.error(methodName, "Characters are more than 100 ");
                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.C2S_ERROR_MAX_100_INFO6);
                }
            }
            if (!(BTSLUtil.isNullString(info7))) {
                final int length = info7.length();
                if (length > 100) {
                    _log.debug(methodName, "length is " + length);
                    _log.error(methodName, "Characters are more than 100 ");
                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.C2S_ERROR_MAX_100_INFO7);
                }
            }
            if (!(BTSLUtil.isNullString(info8))) {
                final int length = info8.length();
                if (length > 100) {
                    _log.debug(methodName, "length is " + length);
                    _log.error(methodName, "Characters are more than 100 ");
                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.C2S_ERROR_MAX_100_INFO8);
                }
            }
            if (!(BTSLUtil.isNullString(info9))) {
                final int length = info9.length();
                if (length > 100) {
                    _log.debug(methodName, "length is " + length);
                    _log.error(methodName, "Characters are more than 100 ");
                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.C2S_ERROR_MAX_100_INFO9);
                }
            }
            if (!(BTSLUtil.isNullString(info10))) {
                final int length = info10.length();
                if (length > 100) {
                    _log.debug(methodName, "length is " + length);
                    _log.error(methodName, "Characters are more than 100 ");
                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.C2S_ERROR_MAX_100_INFO10);
                }
            }
            parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_RECHARGE + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + amount + CHNL_MESSAGE_SEP + selector + CHNL_MESSAGE_SEP + language2;

            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setReqSelector(selector);
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            // p_requestVO.setRequestMSISDN(msisdn);
            p_requestVO.setExternalNetworkCode(extNwCode);
            p_requestVO.setSenderExternalCode(extCode);
            p_requestVO.setSenderLoginID(loginId);
            p_requestVO.setRequestMap(requestHashMap);
            p_requestVO.setExternalReferenceNum(extRefNumber);// To enquire
            // Transaction
            // Status with
            // External
            // Refrence Number
            p_requestVO.setInfo1(info1);
            p_requestVO.setInfo2(info2);
            p_requestVO.setInfo3(info3);
            p_requestVO.setInfo4(info4);
            p_requestVO.setInfo5(info5);
            p_requestVO.setInfo6(info6);
            p_requestVO.setInfo7(info7);
            p_requestVO.setInfo8(info8);
            p_requestVO.setInfo9(info9);
            p_requestVO.setInfo10(info10);
            p_requestVO.setRecmsg(recmsg);
        } catch (BTSLBaseException be) {
            _log.error(methodName, " BTSL Exception while parsing Request Message :" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("POSXMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * Method to generate Credit transfer response for External interface
     * 
     * @param p_requestVO
     * @throws Exception
     */
    public static void generateChannelExtCreditTransferResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateChannelExtCreditTransferResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        final java.util.Date date = new java.util.Date();
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DDMMYYYYHHMMSS);
            sdf.setLenient(false); // this is required else it will convert
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><COMMAND>");
            sbf.append("<TYPE>EXRCTRFRESP</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                String message = p_requestVO.getMessageCode();
                if (message.indexOf("_") != -1) {
                    message = message.substring(0, message.indexOf("_"));
                }
                sbf.append("<TXNSTATUS>").append(message).append("</TXNSTATUS>");
            }

            sbf.append("<DATE>").append(sdf.format(date)).append("</DATE>");

            if (p_requestVO.getRequestMap() != null) {
                sbf.append("<EXTREFNUM>").append((String) p_requestVO.getRequestMap().get("EXTREFNUM")).append("</EXTREFNUM>");
            } else {
                sbf.append("<EXTREFNUM></EXTREFNUM>");
            }

            if (BTSLUtil.isNullString(p_requestVO.getTransactionID())) {
                sbf.append("<TXNID></TXNID>");
            } else {
                sbf.append("<TXNID>").append(p_requestVO.getTransactionID()).append("</TXNID>");
            }
            /*
             * if(p_requestVO.getMessageArguments()==null &&
             * !p_requestVO.isSuccessTxn())
             * sbf.append("<MESSAGE>"+p_requestVO.getMessageCode()+"</MESSAGE>");
             */
            if (!p_requestVO.isSuccessTxn()) {
                sbf.append("<MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments())).append("</MESSAGE>");
            } else {
                sbf.append("<MESSAGE>").append(getMessage(p_requestVO.getLocale(), PretupsErrorCodesI.C2S_SENDER_SUCCESS, p_requestVO.getMessageArguments())).append("</MESSAGE>");
            }
            // sbf.append("<MESSAGE></MESSAGE>");
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "POSXMLStringParser[generateChannelExtCreditTransferResponse]", PretupsErrorCodesI.C2S_ERROR_EXCEPTION, "", "",
                "generateChannelExtCreditTransferResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * To generate Recharge Status Response for External interface
     * 
     * @param p_requestVO
     * @throws Exception
     */
    public static void generateChannelExtRechargeStatusResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateChannelExtRechargeStatusResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        final java.util.Date date = new java.util.Date();
        try {
            String reqStatus = "";
            String transactionId = "";
            if (p_requestVO.isSuccessTxn() && p_requestVO.getValueObject() != null) {
                _log.debug(methodName, "p_requestVO.getValueObject()::::" + p_requestVO.getValueObject());
                reqStatus = ((C2STransferVO) p_requestVO.getValueObject()).getTransferStatus();
                transactionId = ((C2STransferVO) p_requestVO.getValueObject()).getTransferID();
            }
            final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DDMMYYYYHHMMSS);
            sdf.setLenient(false); // this is required else it will convert
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><COMMAND>");
            sbf.append("<TYPE>EXRCSTATRESP</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                String message = p_requestVO.getMessageCode();
                if (message.indexOf("_") != -1) {
                    message = message.substring(0, message.indexOf("_"));
                }
                sbf.append("<TXNSTATUS>").append(message).append("</TXNSTATUS>");
            }

            sbf.append("<DATE>").append(sdf.format(date)).append("</DATE>");

            if (p_requestVO.getRequestMap() != null) {
                sbf.append("<EXTREFNUM>").append((String) p_requestVO.getRequestMap().get("EXTREFNUM")).append("</EXTREFNUM>");
            } else {
                sbf.append("<EXTREFNUM></EXTREFNUM>");
            }

            if (BTSLUtil.isNullString(transactionId)) {
                sbf.append("<TXNID></TXNID>");
            } else {
                sbf.append("<TXNID>").append(transactionId).append("</TXNID>");
            }

            sbf.append("<REQSTATUS>").append(reqStatus).append("</REQSTATUS>");
            if (p_requestVO.getMessageArguments() == null) {
                sbf.append("<MESSAGE>").append(p_requestVO.getMessageCode()).append("</MESSAGE>");
            } else {
                sbf.append("<MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments())).append("</MESSAGE>");
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "POSXMLStringParser[generateChannelExtRechargeStatusResponse]", PretupsErrorCodesI.C2S_ERROR_EXCEPTION, "", "",
                "generateChannelExtRechargeStatusResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /*
     * private static String getMessage(Locale locale,String key,String[]
     * p_args)
     * {
     * String message=BTSLUtil.getMessage(locale,key,p_args);
     * try
     * {
     * LocaleMasterVO
     * localeMasterVO=LocaleMasterCache.getLocaleDetailsFromlocale(locale);
     * if(message.indexOf("mclass^")==0)
     * {
     * int colonIndex=message.indexOf(":");
     * String messageClassPID=message.substring(0,colonIndex);
     * String[] messageClassPIDArray=messageClassPID.split("&");
     * String messageClass=messageClassPIDArray[0].split("\\^")[1];
     * String pid=messageClassPIDArray[1].split("\\^")[1];
     * message=message.substring(colonIndex+1);
     * int endIndexForMessageCode;
     * String messageCode=null;
     * if("ar".equals(localeMasterVO.getLanguage()))
     * {
     * endIndexForMessageCode=message.indexOf("%00%3A");
     * if(endIndexForMessageCode!=-1)
     * {
     * messageCode=URLDecoder.decode(message.substring(0,endIndexForMessageCode),
     * "UTF16");
     * message=message.substring(endIndexForMessageCode+1);
     * }
     * }
     * else
     * {
     * endIndexForMessageCode=message.indexOf(":");
     * if(endIndexForMessageCode!=-1)
     * {
     * messageCode=message.substring(0,endIndexForMessageCode);
     * message=message.substring(endIndexForMessageCode+1);
     * }
     * }
     * }
     * }
     * catch(Exception e)
     * {
     * e.printStackTrace();
     * _log.error("getMessage","Exception e: "+e);
     * }
     * return message;
     * }
     */

    /**
     * Method to parse External Interface Recharge Request
     * 
     * @param p_requestVO
     * @throws Exception
     */
    public static void parseChannelExtTransferBillPayment(RequestVO p_requestVO) throws Exception {
        final String methodName = "parseChannelExtTransferBillPayment";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        final int out_of_bound = -1;
        try {
            final HashMap requestHashMap = new HashMap();
            final String requestStr = p_requestVO.getRequestMessage();
            String msisdn = null;
            String language1 = null;
            String language2 = null;
            String extRefNumber = null;
            String password = null;
            String extCode = null;
            String selector = null;
            String extNwCode = null;
            String date = null;
            String info1 = null;
            String info2 = null;
            String info3 = null;
            String info4 = null;
            String info5 = null;
            String info6 = null;
            String info7 = null;
            String info8 = null;
            String info9 = null;
            String info10 = null;

            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            requestHashMap.put("TYPE", type);

            index = requestStr.indexOf("<DATE>");
            if (index > 0) {
                date = requestStr.substring(index + "<DATE>".length(), requestStr.indexOf("</DATE>", index));
                requestHashMap.put("DATE", date);
            }

            index = requestStr.indexOf("<EXTNWCODE>");
            if (index > 0) {
                extNwCode = requestStr.substring(index + "<EXTNWCODE>".length(), requestStr.indexOf("</EXTNWCODE>", index));
                requestHashMap.put("EXTNWCODE", extNwCode);
            }
            index = requestStr.indexOf("<MSISDN>");
            if (index > 0) {
                msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
                requestHashMap.put("MSISDN", msisdn);
            }

            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            requestHashMap.put("PIN", pin);

            index = requestStr.indexOf("<LOGINID>");
            final String loginId = requestStr.substring(index + "<LOGINID>".length(), requestStr.indexOf("</LOGINID>", index));
            requestHashMap.put("LOGINID", loginId);

            index = requestStr.indexOf("<PASSWORD>");
            if (index > 0) {
                password = requestStr.substring(index + "<PASSWORD>".length(), requestStr.indexOf("</PASSWORD>", index));
                requestHashMap.put("PASSWORD", password);
            }

            index = requestStr.indexOf("<EXTCODE>");
            if (index > 0) {
                extCode = requestStr.substring(index + "<EXTCODE>".length(), requestStr.indexOf("</EXTCODE>", index));
                requestHashMap.put("EXTCODE", extCode);
            }

            index = requestStr.indexOf("<EXTREFNUM>");
            if (index > 0) {
                extRefNumber = requestStr.substring(index + "<EXTREFNUM>".length(), requestStr.indexOf("</EXTREFNUM>", index));
                requestHashMap.put("EXTREFNUM", extRefNumber);
            }

            index = requestStr.indexOf("<MSISDN2>");
            final String msisdn2 = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));
            index = requestStr.indexOf("<AMOUNT>");
            final String amount = requestStr.substring(index + "<AMOUNT>".length(), requestStr.indexOf("</AMOUNT>", index));

            index = requestStr.indexOf("<LANGUAGE1>");
            if (index > 0) {
                language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
            }

            index = requestStr.indexOf("<LANGUAGE2>");
            if (index > 0) {
                language2 = requestStr.substring(index + "<LANGUAGE2>".length(), requestStr.indexOf("</LANGUAGE2>", index));
            }
            if (BTSLUtil.isNullString(language2)) {
                language2 = "1";
            }

            index = requestStr.indexOf("<SELECTOR>");
            selector = requestStr.substring(index + "<SELECTOR>".length(), requestStr.indexOf("</SELECTOR>", index));
            index = requestStr.indexOf("<INFO1>");
            if (index != -1) {
                info1 = requestStr.substring(index + "<INFO1>".length(), requestStr.indexOf("</INFO1>", index));
            }
            index = requestStr.indexOf("<INFO2>");
            if (index != -1) {
                info2 = requestStr.substring(index + "<INFO2>".length(), requestStr.indexOf("</INFO2>", index));
            }
            index = requestStr.indexOf("<INFO3>");
            if (index != -1) {
                info3 = requestStr.substring(index + "<INFO3>".length(), requestStr.indexOf("</INFO3>", index));
            }
            index = requestStr.indexOf("<INFO4>");
            if (index != -1) {
                info4 = requestStr.substring(index + "<INFO4>".length(), requestStr.indexOf("</INFO4>", index));
            }
            index = requestStr.indexOf("<INFO5>");
            if (index != -1) {
                info5 = requestStr.substring(index + "<INFO5>".length(), requestStr.indexOf("</INFO5>", index));
            }
            index = requestStr.indexOf("<INFO6>");
            if (index != -1) {
                info6 = requestStr.substring(index + "<INFO6>".length(), requestStr.indexOf("</INFO6>", index));
            }
            index = requestStr.indexOf("<INFO7>");
            if (index != -1) {
                info7 = requestStr.substring(index + "<INFO7>".length(), requestStr.indexOf("</INFO7>", index));
            }
            index = requestStr.indexOf("<INFO8>");
            if (index != -1) {
                info8 = requestStr.substring(index + "<INFO8>".length(), requestStr.indexOf("</INFO8>", index));
            }
            index = requestStr.indexOf("<INFO9>");
            if (index != -1) {
                info9 = requestStr.substring(index + "<INFO9>".length(), requestStr.indexOf("</INFO9>", index));
            }
            index = requestStr.indexOf("<INFO10>");
            if (index != -1) {
                info10 = requestStr.substring(index + "<INFO10>".length(), requestStr.indexOf("</INFO10>", index));
            }

            // tag to on/off sending msg at request level
            index = requestStr.indexOf("<RECMSG>");
            String recmsg = "Y";// if there is no tag value setting default
            // value to Y for sending SMS
            if (index != out_of_bound) {
                recmsg = requestStr.substring(index + "<RECMSG>".length(), requestStr.indexOf("</RECMSG>", index));
            }
            if (BTSLUtil.isNullString(selector)) {
                // selector=""+((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_TRANSFER_DEF_SELECTOR_CODE));
                // Changed on 27/05/07 for Service Type selector Mapping
                final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache
                    .getDefaultSelectorForServiceType(PretupsI.SERVICE_TYPE_POSTPAID_BILL_PAYMENT);
                if (serviceSelectorMappingVO != null) {
                    selector = serviceSelectorMappingVO.getSelectorCode();
                }
            }
            if (BTSLUtil.isNullString(type) || BTSLUtil.isNullString(extNwCode) || BTSLUtil.isNullString(pin) || BTSLUtil.isNullString(msisdn2) || BTSLUtil
                .isNullString(amount) || BTSLUtil.isNullString(loginId)) {
                throw new BTSLBaseException("POSPOSXMLStringParser", "parseChannelExtTransferBillPayment", PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }
            if (BTSLUtil.isNullString(amount)) {
                _log.error(methodName, "Amount field is null ");
                throw new BTSLBaseException("POSXMLStringParser", "parseChannelExtTransferBillPayment", PretupsErrorCodesI.C2S_ERROR_BLANK_AMOUNT);
            }
            if (!(BTSLUtil.isNullString(info1))) {
                final int length = info1.length();
                if (length > 100) {
                    _log.debug(methodName, "length is " + length);
                    _log.error(methodName, "Characters are more than 100 ");
                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.C2S_ERROR_MAX_100_INFO1);
                }
            }
            if (!(BTSLUtil.isNullString(info2))) {
                final int length = info2.length();
                if (length > 100) {
                    _log.debug(methodName, "length is " + length);
                    _log.error(methodName, "Characters are more than 100 ");
                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.C2S_ERROR_MAX_100_INFO2);
                }
            }
            if (!(BTSLUtil.isNullString(info3))) {
                final int length = info3.length();
                if (length > 100) {
                    _log.debug(methodName, "length is " + length);
                    _log.error(methodName, "Characters are more than 100 ");
                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.C2S_ERROR_MAX_100_INFO3);
                }
            }
            if (!(BTSLUtil.isNullString(info4))) {
                final int length = info4.length();
                if (length > 100) {
                    _log.debug(methodName, "length is " + length);
                    _log.error(methodName, "Characters are more than 100 ");
                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.C2S_ERROR_MAX_100_INFO4);
                }
            }
            if (!(BTSLUtil.isNullString(info5))) {
                final int length = info5.length();
                if (length > 100) {
                    _log.debug(methodName, "length is " + length);
                    _log.error(methodName, "Characters are more than 100 ");
                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.C2S_ERROR_MAX_100_INFO5);
                }
            }
            if (!(BTSLUtil.isNullString(info6))) {
                final int length = info6.length();
                if (length > 100) {
                    _log.debug(methodName, "length is " + length);
                    _log.error(methodName, "Characters are more than 100 ");
                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.C2S_ERROR_MAX_100_INFO6);
                }
            }
            if (!(BTSLUtil.isNullString(info7))) {
                final int length = info7.length();
                if (length > 100) {
                    _log.debug(methodName, "length is " + length);
                    _log.error(methodName, "Characters are more than 100 ");
                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.C2S_ERROR_MAX_100_INFO7);
                }
            }
            if (!(BTSLUtil.isNullString(info8))) {
                final int length = info8.length();
                if (length > 100) {
                    _log.debug(methodName, "length is " + length);
                    _log.error(methodName, "Characters are more than 100 ");
                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.C2S_ERROR_MAX_100_INFO8);
                }
            }
            if (!(BTSLUtil.isNullString(info9))) {
                final int length = info9.length();
                if (length > 100) {
                    _log.debug(methodName, "length is " + length);
                    _log.error(methodName, "Characters are more than 100 ");
                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.C2S_ERROR_MAX_100_INFO9);
                }
            }
            if (!(BTSLUtil.isNullString(info10))) {
                final int length = info10.length();
                if (length > 100) {
                    _log.debug(methodName, "length is " + length);
                    _log.error(methodName, "Characters are more than 100 ");
                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.C2S_ERROR_MAX_100_INFO10);
                }
            }
            parsedRequestStr = PretupsI.SERVICE_TYPE_POSTPAID_BILL_PAYMENT + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + amount;
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setReqSelector(selector);
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            // p_requestVO.setRequestMSISDN(msisdn);
            p_requestVO.setExternalNetworkCode(extNwCode);
            p_requestVO.setSenderExternalCode(extCode);
            p_requestVO.setSenderLoginID(loginId);
            p_requestVO.setRequestMap(requestHashMap);
            p_requestVO.setExternalReferenceNum(extRefNumber);
            p_requestVO.setInfo1(info1);
            p_requestVO.setInfo2(info2);
            p_requestVO.setInfo3(info3);
            p_requestVO.setInfo4(info4);
            p_requestVO.setInfo5(info5);
            p_requestVO.setInfo6(info6);
            p_requestVO.setInfo7(info7);
            p_requestVO.setInfo8(info8);
            p_requestVO.setInfo9(info9);
            p_requestVO.setInfo10(info10);
            p_requestVO.setRecmsg(recmsg);
        } catch (BTSLBaseException be) {
            _log.error(methodName, " BTSL Exception while parsing Request Message :" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("POSXMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * Method to parse External Interface Recharge Status Request
     * 
     * @param p_requestVO
     * @throws Exception
     */
    public static void parseChannelExtRechargeStatusRequest(RequestVO p_requestVO) throws Exception {
        final String methodName = "parseChannelExtRechargeStatusRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        final String MSG_IDNTFICATION_BOTH = "BOTH";
        final String MSG_IDNTFICATION_TXN_ID = "TXN";
        final String MSG_IDNTFICATION_EXT_REF = "EXT";
        try {
            final HashMap requestHashMap = new HashMap();
            p_requestVO.setRequestMap(requestHashMap);
            final String requestStr = p_requestVO.getRequestMessage();
            String msisdn = null;
            String language1 = null;
            String extRefNumber = null;
            String password = null;
            String extCode = null;
            String extNwCode = null;
            String date = null;

            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            requestHashMap.put("TYPE", type);
            index = requestStr.indexOf("<DATE>");
            if (index > 0) {
                date = requestStr.substring(index + "<DATE>".length(), requestStr.indexOf("</DATE>", index));
                requestHashMap.put("DATE", date);
            }

            index = requestStr.indexOf("<EXTNWCODE>");
            extNwCode = requestStr.substring(index + "<EXTNWCODE>".length(), requestStr.indexOf("</EXTNWCODE>", index));
            requestHashMap.put("EXTNWCODE", extNwCode);

            index = requestStr.indexOf("<MSISDN>");
            if (index > 0) {
                msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
                requestHashMap.put("MSISDN", msisdn);
            }

            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            requestHashMap.put("PIN", pin);

            index = requestStr.indexOf("<LOGINID>");
            final String loginId = requestStr.substring(index + "<LOGINID>".length(), requestStr.indexOf("</LOGINID>", index));
            requestHashMap.put("LOGINID", loginId);

            index = requestStr.indexOf("<PASSWORD>");
            if (index > 0) {
                password = requestStr.substring(index + "<PASSWORD>".length(), requestStr.indexOf("</PASSWORD>", index));
                requestHashMap.put("PASSWORD", password);
            }

            index = requestStr.indexOf("<EXTCODE>");
            if (index > 0) {
                extCode = requestStr.substring(index + "<EXTCODE>".length(), requestStr.indexOf("</EXTCODE>", index));
                requestHashMap.put("EXTCODE", extCode);
            }

            index = requestStr.indexOf("<EXTREFNUM>");
            if (index > 0) {
                extRefNumber = requestStr.substring(index + "<EXTREFNUM>".length(), requestStr.indexOf("</EXTREFNUM>", index));
                requestHashMap.put("EXTREFNUM", extRefNumber);
            }
            index = requestStr.indexOf("<TXNID>");
            final String txnID = requestStr.substring(index + "<TXNID>".length(), requestStr.indexOf("</TXNID>", index));
            requestHashMap.put("TXNID", txnID);
            index = requestStr.indexOf("<LANGUAGE1>");
            if (index > 0) {
                language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
            }

            if (BTSLUtil.isNullString(type) || BTSLUtil.isNullString(extNwCode) || BTSLUtil.isNullString(pin) || BTSLUtil.isNullString(loginId)) {
                throw new BTSLBaseException("POSPOSXMLStringParser", "parseChannelExtRechargeStatusRequest", PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }

            parsedRequestStr = PretupsI.SERVICE_TYPE_RECHARGE_STATUS + CHNL_MESSAGE_SEP + txnID;
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            // p_requestVO.setDecryptedMessage(parsedRequestStr);
            // p_requestVO.setRequestMSISDN(msisdn);
            p_requestVO.setExternalNetworkCode(extNwCode);
            p_requestVO.setSenderExternalCode(extCode);
            p_requestVO.setSenderLoginID(loginId);
            p_requestVO.setRequestMap(requestHashMap);

            if (!BTSLUtil.isNullString(extRefNumber) && !BTSLUtil.isNullString(txnID)) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_RECHARGE_STATUS + CHNL_MESSAGE_SEP + txnID + CHNL_MESSAGE_SEP + extRefNumber + CHNL_MESSAGE_SEP + MSG_IDNTFICATION_BOTH;
            }
            if (!BTSLUtil.isNullString(extRefNumber)) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_RECHARGE_STATUS + CHNL_MESSAGE_SEP + extRefNumber + CHNL_MESSAGE_SEP + MSG_IDNTFICATION_EXT_REF;
            }
            if (!BTSLUtil.isNullString(txnID)) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_RECHARGE_STATUS + CHNL_MESSAGE_SEP + txnID + CHNL_MESSAGE_SEP + MSG_IDNTFICATION_TXN_ID;
            }

            p_requestVO.setExternalReferenceNum(extRefNumber);
            p_requestVO.setDecryptedMessage(parsedRequestStr);

        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("POSXMLStringParser", "parseChannelExtRechargeStatusRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * Method to parse External Interface Recharge Status Request
     * 
     * @param p_requestVO
     * @throws Exception
     */
    public static void parseExtLastXTransferEnq(RequestVO p_requestVO) throws Exception {
        final String methodName = "parseExtLastXTransferEnq";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        try {
            final HashMap requestHashMap = new HashMap();
            p_requestVO.setRequestMap(requestHashMap);
            final String requestStr = p_requestVO.getRequestMessage();
            String msisdn = null;
            String extRefNumber = null;
            String password = null;
            String extCode = null;
            String extNwCode = null;
            String date = null;
            String language1 = null;

            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            requestHashMap.put("TYPE", type);
            index = requestStr.indexOf("<DATE>");
            if (index > 0) {
                date = requestStr.substring(index + "<DATE>".length(), requestStr.indexOf("</DATE>", index));
                requestHashMap.put("DATE", date);
            }

            index = requestStr.indexOf("<EXTNWCODE>");
            extNwCode = requestStr.substring(index + "<EXTNWCODE>".length(), requestStr.indexOf("</EXTNWCODE>", index));
            requestHashMap.put("EXTNWCODE", extNwCode);

            index = requestStr.indexOf("<MSISDN>");
            if (index > 0) {
                msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
                requestHashMap.put("MSISDN", msisdn);
            }

            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            requestHashMap.put("PIN", pin);

            index = requestStr.indexOf("<LOGINID>");
            final String loginId = requestStr.substring(index + "<LOGINID>".length(), requestStr.indexOf("</LOGINID>", index));
            requestHashMap.put("LOGINID", loginId);

            index = requestStr.indexOf("<PASSWORD>");
            if (index > 0) {
                password = requestStr.substring(index + "<PASSWORD>".length(), requestStr.indexOf("</PASSWORD>", index));
                requestHashMap.put("PASSWORD", password);
            }

            index = requestStr.indexOf("<EXTCODE>");
            if (index > 0) {
                extCode = requestStr.substring(index + "<EXTCODE>".length(), requestStr.indexOf("</EXTCODE>", index));
                requestHashMap.put("EXTCODE", extCode);
            }

            index = requestStr.indexOf("<EXTREFNUM>");
            if (index > 0) {
                extRefNumber = requestStr.substring(index + "<EXTREFNUM>".length(), requestStr.indexOf("</EXTREFNUM>", index));
                requestHashMap.put("EXTREFNUM", extRefNumber);
            }
            index = requestStr.indexOf("<LANGUAGE1>");
            if (index > 0) {
                language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
                requestHashMap.put("LANGUAGE1", language1);
                if (!BTSLUtil.isNullString(language1)) {
                    p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
                }
            }
            if (BTSLUtil.isNullString(type) || BTSLUtil.isNullString(extNwCode) || BTSLUtil.isNullString(pin) || BTSLUtil.isNullString(loginId)) {
                throw new BTSLBaseException("POSPOSXMLStringParser", "parseExtLastXTransferEnq", PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }

            parsedRequestStr = PretupsI.SERVICE_TYPE_LASTX_TRANSFER_REPORT;
            // p_requestVO.setDecryptedMessage(parsedRequestStr);
            // p_requestVO.setRequestMSISDN(msisdn);
            p_requestVO.setExternalNetworkCode(extNwCode);
            p_requestVO.setSenderExternalCode(extCode);
            p_requestVO.setSenderLoginID(loginId);
            p_requestVO.setRequestMap(requestHashMap);

            p_requestVO.setExternalReferenceNum(extRefNumber);
            p_requestVO.setDecryptedMessage(parsedRequestStr);

        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("POSXMLStringParser", "parseExtLastXTransferEnq", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * Method to parse External Interface Recharge Status Request
     * 
     * @param p_requestVO
     * @throws Exception
     */
    public static void parseExtCustomerEnqReq(RequestVO p_requestVO) throws Exception {
        final String methodName = "parseExtCustomerEnqReq";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        try {
            final HashMap requestHashMap = new HashMap();
            p_requestVO.setRequestMap(requestHashMap);
            final String requestStr = p_requestVO.getRequestMessage();
            String msisdn = null;
            String extRefNumber = null;
            String password = null;
            String extCode = null;
            String extNwCode = null;
            String date = null;
            // String date=null;
            String language1 = null;

            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            requestHashMap.put("TYPE", type);
            index = requestStr.indexOf("<DATE>");
            if (index > 0) {
                date = requestStr.substring(index + "<DATE>".length(), requestStr.indexOf("</DATE>", index));
                requestHashMap.put("DATE", date);
            }

            index = requestStr.indexOf("<EXTNWCODE>");
            extNwCode = requestStr.substring(index + "<EXTNWCODE>".length(), requestStr.indexOf("</EXTNWCODE>", index));
            requestHashMap.put("EXTNWCODE", extNwCode);
            index = requestStr.indexOf("<MSISDN>");
            if (index > 0) {
                msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
                requestHashMap.put("MSISDN", msisdn);
            }

            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            requestHashMap.put("PIN", pin);

            index = requestStr.indexOf("<LOGINID>");
            final String loginId = requestStr.substring(index + "<LOGINID>".length(), requestStr.indexOf("</LOGINID>", index));
            requestHashMap.put("LOGINID", loginId);

            index = requestStr.indexOf("<PASSWORD>");
            if (index > 0) {
                password = requestStr.substring(index + "<PASSWORD>".length(), requestStr.indexOf("</PASSWORD>", index));
                requestHashMap.put("PASSWORD", password);
            }

            index = requestStr.indexOf("<EXTCODE>");
            if (index > 0) {
                extCode = requestStr.substring(index + "<EXTCODE>".length(), requestStr.indexOf("</EXTCODE>", index));
                requestHashMap.put("EXTCODE", extCode);
            }

            index = requestStr.indexOf("<EXTREFNUM>");
            if (index > 0) {
                extRefNumber = requestStr.substring(index + "<EXTREFNUM>".length(), requestStr.indexOf("</EXTREFNUM>", index));
                requestHashMap.put("EXTREFNUM", extRefNumber);
            }
            index = requestStr.indexOf("<MSISDN2>");
            final String msisdn2 = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));

            index = requestStr.indexOf("<LANGUAGE1>");
            if (index > 0) {
                language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
                requestHashMap.put("LANGUAGE1", language1);
                if (!BTSLUtil.isNullString(language1)) {
                    p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
                }
            }

            if (BTSLUtil.isNullString(type) || BTSLUtil.isNullString(extNwCode) || BTSLUtil.isNullString(pin) || BTSLUtil.isNullString(loginId) || BTSLUtil
                .isNullString(msisdn2)) {
                throw new BTSLBaseException("POSPOSXMLStringParser", "parseExtCustomerEnqReq", PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }
            // parsedRequestStr=PretupsI.SERVICE_TYPE_CUSTX_ENQUIRY+CHNL_MESSAGE_SEP+pin;
            parsedRequestStr = PretupsI.SERVICE_TYPE_CUSTX_ENQUIRY + CHNL_MESSAGE_SEP + msisdn2;
            // p_requestVO.setRequestMSISDN(msisdn);
            p_requestVO.setExternalNetworkCode(extNwCode);
            p_requestVO.setSenderExternalCode(extCode);
            p_requestVO.setSenderLoginID(loginId);
            p_requestVO.setRequestMap(requestHashMap);

            p_requestVO.setExternalReferenceNum(extRefNumber);
            p_requestVO.setDecryptedMessage(parsedRequestStr);

        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("POSXMLStringParser", "parseExtCustomerEnqReq", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * Parsing request to check the balance of External System user
     * 
     * @param p_requestVO
     * @throws Exception
     */
    public static void parseExtChannelUserBalanceRequest(RequestVO p_requestVO) throws Exception {
        final String methodName = "parseExtChannelUserBalanceRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        try {
            final HashMap requestHashMap = new HashMap();
            p_requestVO.setRequestMap(requestHashMap);
            final String requestStr = p_requestVO.getRequestMessage();
            String msisdn = null;
            String extRefNumber = null;
            String password = null;
            String extCode = null;
            String extNwCode = null;
            String date = null;
            String language1 = null;

            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            requestHashMap.put("TYPE", type);
            index = requestStr.indexOf("<DATE>");
            if (index > 0) {
                date = requestStr.substring(index + "<DATE>".length(), requestStr.indexOf("</DATE>", index));
                requestHashMap.put("DATE", date);
            }

            index = requestStr.indexOf("<EXTNWCODE>");
            extNwCode = requestStr.substring(index + "<EXTNWCODE>".length(), requestStr.indexOf("</EXTNWCODE>", index));
            requestHashMap.put("EXTNWCODE", extNwCode);
            index = requestStr.indexOf("<MSISDN>");
            if (index > 0) {
                msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
                requestHashMap.put("MSISDN", msisdn);
            }

            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            requestHashMap.put("PIN", pin);

            index = requestStr.indexOf("<LOGINID>");
            final String loginId = requestStr.substring(index + "<LOGINID>".length(), requestStr.indexOf("</LOGINID>", index));
            requestHashMap.put("LOGINID", loginId);

            index = requestStr.indexOf("<PASSWORD>");
            if (index > 0) {
                password = requestStr.substring(index + "<PASSWORD>".length(), requestStr.indexOf("</PASSWORD>", index));
                requestHashMap.put("PASSWORD", password);
            }

            index = requestStr.indexOf("<EXTCODE>");
            if (index > 0) {
                extCode = requestStr.substring(index + "<EXTCODE>".length(), requestStr.indexOf("</EXTCODE>", index));
                requestHashMap.put("EXTCODE", extCode);
            }

            index = requestStr.indexOf("<EXTREFNUM>");
            if (index > 0) {
                extRefNumber = requestStr.substring(index + "<EXTREFNUM>".length(), requestStr.indexOf("</EXTREFNUM>", index));
                requestHashMap.put("EXTREFNUM", extRefNumber);
            }
            index = requestStr.indexOf("<LANGUAGE1>");
            if (index > 0) {
                language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
                requestHashMap.put("LANGUAGE1", language1);
                if (!BTSLUtil.isNullString(language1)) {
                    p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
                }
            }
            if (BTSLUtil.isNullString(type) || BTSLUtil.isNullString(extNwCode) || BTSLUtil.isNullString(pin) || BTSLUtil.isNullString(loginId)) {
                throw new BTSLBaseException("POSPOSXMLStringParser", "parseExtChannelUserBalanceRequest", PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }
            parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_BALANCE_ENQUIRY;

            p_requestVO.setDecryptedMessage(parsedRequestStr);
            // p_requestVO.setRequestMSISDN(msisdn);
            p_requestVO.setExternalNetworkCode(extNwCode);
            p_requestVO.setSenderExternalCode(extCode);
            p_requestVO.setSenderLoginID(loginId);
            p_requestVO.setRequestMap(requestHashMap);
            p_requestVO.setExternalReferenceNum(extRefNumber);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("POSXMLStringParser", "parseExtChannelUserBalanceRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * Parsing request to check the balance of External System user
     * 
     * @param p_requestVO
     * @throws Exception
     */
    public static void parseExtUserOtherBalEnqReq(RequestVO p_requestVO) throws Exception {
        final String methodName = "parseExtUserOtherBalEnqReq";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        try {
            final HashMap requestHashMap = new HashMap();
            p_requestVO.setRequestMap(requestHashMap);
            final String requestStr = p_requestVO.getRequestMessage();
            String msisdn = null;
            String extRefNumber = null;
            String password = null;
            String extCode = null;
            String extNwCode = null;
            String date = null;
            String language1 = null;

            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            requestHashMap.put("TYPE", type);
            index = requestStr.indexOf("<DATE>");
            if (index > 0) {
                date = requestStr.substring(index + "<DATE>".length(), requestStr.indexOf("</DATE>", index));
                requestHashMap.put("DATE", date);
            }

            index = requestStr.indexOf("<EXTNWCODE>");
            extNwCode = requestStr.substring(index + "<EXTNWCODE>".length(), requestStr.indexOf("</EXTNWCODE>", index));
            requestHashMap.put("EXTNWCODE", extNwCode);
            index = requestStr.indexOf("<MSISDN>");
            if (index > 0) {
                msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
                requestHashMap.put("MSISDN", msisdn);
            }

            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            requestHashMap.put("PIN", pin);

            index = requestStr.indexOf("<LOGINID>");
            final String loginId = requestStr.substring(index + "<LOGINID>".length(), requestStr.indexOf("</LOGINID>", index));
            requestHashMap.put("LOGINID", loginId);

            index = requestStr.indexOf("<PASSWORD>");
            if (index > 0) {
                password = requestStr.substring(index + "<PASSWORD>".length(), requestStr.indexOf("</PASSWORD>", index));
                requestHashMap.put("PASSWORD", password);
            }

            index = requestStr.indexOf("<EXTCODE>");
            if (index > 0) {
                extCode = requestStr.substring(index + "<EXTCODE>".length(), requestStr.indexOf("</EXTCODE>", index));
                requestHashMap.put("EXTCODE", extCode);
            }

            index = requestStr.indexOf("<EXTREFNUM>");
            if (index > 0) {
                extRefNumber = requestStr.substring(index + "<EXTREFNUM>".length(), requestStr.indexOf("</EXTREFNUM>", index));
                requestHashMap.put("EXTREFNUM", extRefNumber);
            }
            index = requestStr.indexOf("<MSISDN2>");
            final String msisdn2 = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));
            requestHashMap.put("MSISDN2", msisdn2);

            index = requestStr.indexOf("<LANGUAGE1>");
            if (index > 0) {
                language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
                requestHashMap.put("LANGUAGE1", language1);
                if (!BTSLUtil.isNullString(language1)) {
                    p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
                }
            }

            if (BTSLUtil.isNullString(type) || BTSLUtil.isNullString(extNwCode) || BTSLUtil.isNullString(pin) || BTSLUtil.isNullString(loginId) || BTSLUtil
                .isNullString(msisdn2)) {
                throw new BTSLBaseException("POSPOSXMLStringParser", "parseExtUserOtherBalEnqReq", PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }

            parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_BALANCE_ENQUIRY + CHNL_MESSAGE_SEP + msisdn2;

            p_requestVO.setDecryptedMessage(parsedRequestStr);
            // p_requestVO.setRequestMSISDN(msisdn);
            p_requestVO.setExternalNetworkCode(extNwCode);
            p_requestVO.setSenderExternalCode(extCode);
            p_requestVO.setSenderLoginID(loginId);
            p_requestVO.setRequestMap(requestHashMap);
            p_requestVO.setExternalReferenceNum(extRefNumber);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("POSXMLStringParser", "parseExtUserOtherBalEnqReq", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * parseExtChangepinResponse
     * Request of Change Pin request from the external system.
     * 
     * @param p_requestVO
     * @param p_action
     * @throws Exception
     * @author bobba
     */

    public static void parseExtChangepinRequest(RequestVO p_requestVO) throws Exception {
        final String methodName = "parseExtChangepinRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        try {
            final HashMap requestHashMap = new HashMap();
            final String requestStr = p_requestVO.getRequestMessage();
            String msisdn = null;
            String extRefNumber = null;
            String password = null;
            String extNwCode = null;
            String date = null;
            String language1 = null;

            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            requestHashMap.put("TYPE", type);

            index = requestStr.indexOf("<DATE>");
            if (index > 0) {
                date = requestStr.substring(index + "<DATE>".length(), requestStr.indexOf("</DATE>", index));
                requestHashMap.put("DATE", date);
            }

            index = requestStr.indexOf("<EXTNWCODE>");
            extNwCode = requestStr.substring(index + "<EXTNWCODE>".length(), requestStr.indexOf("</EXTNWCODE>", index));
            requestHashMap.put("EXTNWCODE", extNwCode);
            index = requestStr.indexOf("<MSISDN>");
            if (index > 0) {
                msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
                requestHashMap.put("MSISDN", msisdn);
            }

            index = requestStr.indexOf("<LOGINID>");
            final String loginId = requestStr.substring(index + "<LOGINID>".length(), requestStr.indexOf("</LOGINID>", index));
            requestHashMap.put("LOGINID", loginId);

            index = requestStr.indexOf("<PASSWORD>");
            if (index > 0) {
                password = requestStr.substring(index + "<PASSWORD>".length(), requestStr.indexOf("</PASSWORD>", index));
                requestHashMap.put("PASSWORD", password);
            }

            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            requestHashMap.put("PIN", pin);
            index = requestStr.indexOf("<NEWPIN>");
            final String newPin = requestStr.substring(index + "<NEWPIN>".length(), requestStr.indexOf("</NEWPIN>", index));
            requestHashMap.put("NEWPIN", newPin);
            index = requestStr.indexOf("<CONFIRMPIN>");
            final String confirmPin = requestStr.substring(index + "<CONFIRMPIN>".length(), requestStr.indexOf("</CONFIRMPIN>", index));
            requestHashMap.put("CONFIRMPIN", confirmPin);
            index = requestStr.indexOf("<LANGUAGE1>");
            if (index > 0) {
                language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
                requestHashMap.put("LANGUAGE1", language1);
                if (!BTSLUtil.isNullString(language1)) {
                    p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
                }
            }

            index = requestStr.indexOf("<EXTREFNUM>");
            if (index > 0) {
                extRefNumber = requestStr.substring(index + "<EXTREFNUM>".length(), requestStr.indexOf("</EXTREFNUM>", index));
                requestHashMap.put("EXTREFNUM", extRefNumber);
            }
            if (BTSLUtil.isNullString(type) || BTSLUtil.isNullString(loginId) || BTSLUtil.isNullString(pin) || BTSLUtil.isNullString(newPin) || BTSLUtil
                .isNullString(confirmPin)) {
                throw new BTSLBaseException("POSPOSXMLStringParser", "parseChannelChangePinRequest", PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }
            parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_CHANGEPIN + CHNL_MESSAGE_SEP + pin + CHNL_MESSAGE_SEP + newPin + CHNL_MESSAGE_SEP + confirmPin;

            p_requestVO.setDecryptedMessage(parsedRequestStr);
            // p_requestVO.setRequestMSISDN(msisdn);
            p_requestVO.setExternalNetworkCode(extNwCode);
            p_requestVO.setExternalReferenceNum(extRefNumber);
            p_requestVO.setRequestMap(requestHashMap);
            p_requestVO.setSenderLoginID(loginId);

        }

        catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("POSXMLStringParser", "parseExtChangepinRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO ID: " + p_requestVO.getRequestIDStr());
            }
        }
    }

    /**
     * generateExtDailyTransactionResponse
     * Response of daily transfer reports request from the external system.
     * 
     * @param p_requestVO
     * @throws Exception
     * @author ved.sharma
     */
    public static void generateExtDailyTransactionResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateExtDailyTransactionResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><COMMAND>");
            sbf.append("<TYPE>EXDLYRESP</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
            }
            sbf.append("<DATE>").append(getDateTime(null, null)).append("</DATE>");
            sbf.append("<EXTREFNUM>").append(p_requestVO.getExternalReferenceNum()).append("</EXTREFNUM>");
            // Locale locale=new
            // Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            sbf.append("<MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments())).append("</MESSAGE>");

            final HashMap requestHashMap = p_requestVO.getRequestMap();
            if (requestHashMap != null) {
                final ArrayList channelIntransferList = (ArrayList) requestHashMap.get("CHANNEL_IN_TRANSFER");
                final ArrayList channelOuttransferList = (ArrayList) requestHashMap.get("CHANNEL_OUT_TRANSFER");
                final ArrayList subscriberOutTransferList = (ArrayList) requestHashMap.get("SUBSCRIBER_OUT_TRANSFER");
                if (channelIntransferList != null && !channelIntransferList.isEmpty()) {
                    for (int i = 0, j = channelIntransferList.size(); i < j; i++) {
                        final ChannelTransfrsReturnsVO channelTransfrsInVO = (ChannelTransfrsReturnsVO) channelIntransferList.get(i);
                        sbf.append("<IN_TRANSACTION>");
                        sbf.append("<PRODSHORTNAME>").append(channelTransfrsInVO.getShortName()).append("</PRODSHORTNAME>");
                        sbf.append("<TRANSFER>").append(PretupsBL.getDisplayAmount(channelTransfrsInVO.getTransfes())).append("</TRANSFER>");
                        sbf.append("<WITHDRAW>").append(PretupsBL.getDisplayAmount(channelTransfrsInVO.getReturns())).append("</WITHDRAW>");
                        sbf.append("</IN_TRANSACTION>");
                    }
                } else {
                    sbf.append("<IN_TRANSACTION>");
                    sbf.append("<PRODSHORTNAME></PRODSHORTNAME>");
                    sbf.append("<TRANSFER></TRANSFER>");
                    sbf.append("<WITHDRAW></WITHDRAW>");
                    sbf.append("</IN_TRANSACTION>");
                }
                if (channelOuttransferList != null && !channelOuttransferList.isEmpty()) {
                    for (int i = 0, j = channelOuttransferList.size(); i < j; i++) {
                        final ChannelTransfrsReturnsVO channelTransfrsOutVO = (ChannelTransfrsReturnsVO) channelOuttransferList.get(i);
                        sbf.append("<OUT_TRANSACTION>");
                        sbf.append("<PRODSHORTNAME>").append(channelTransfrsOutVO.getShortName()).append("</PRODSHORTNAME>");
                        sbf.append("<TRANSFER>").append(PretupsBL.getDisplayAmount(channelTransfrsOutVO.getTransfes())).append("</TRANSFER>");
                        sbf.append("<WITHDRAW>").append(PretupsBL.getDisplayAmount(channelTransfrsOutVO.getReturns())).append("</WITHDRAW>");
                        sbf.append("</OUT_TRANSACTION>");
                    }
                } else {
                    sbf.append("<OUT_TRANSACTION>");
                    sbf.append("<PRODSHORTNAME></PRODSHORTNAME>");
                    sbf.append("<TRANSFER></TRANSFER>");
                    sbf.append("<WITHDRAW></WITHDRAW>");
                    sbf.append("</OUT_TRANSACTION>");
                }
                if (subscriberOutTransferList != null && !subscriberOutTransferList.isEmpty()) {
                    for (int i = 0, j = subscriberOutTransferList.size(); i < j; i++) {
                        final ChannelTransfrsReturnsVO subscriberlTransfrsOutVO = (ChannelTransfrsReturnsVO) subscriberOutTransferList.get(i);
                        sbf.append("<SUBS_OUT_TRANSACTION>");
                        sbf.append("<SERVICE_NAME>").append(subscriberlTransfrsOutVO.getServiceName()).append("</SERVICE_NAME>");
                        sbf.append("<PRODSHORTNAME>").append(subscriberlTransfrsOutVO.getShortName()).append("</PRODSHORTNAME>");
                        sbf.append("<AMOUNT>").append(PretupsBL.getDisplayAmount(subscriberlTransfrsOutVO.getTransfes())).append("</AMOUNT>");
                        sbf.append("</SUBS_OUT_TRANSACTION>");
                    }
                } else {
                    sbf.append("<SUBS_OUT_TRANSACTION>");
                    sbf.append("<SERVICE_NAME></SERVICE_NAME>");
                    sbf.append("<PRODSHORTNAME></PRODSHORTNAME>");
                    sbf.append("<AMOUNT></AMOUNT>");
                    sbf.append("</SUBS_OUT_TRANSACTION>");
                }
            } else {
                sbf.append("<IN_TRANSACTION>");
                sbf.append("<PRODSHORTNAME></PRODSHORTNAME>");
                sbf.append("<TRANSFER></TRANSFER>");
                sbf.append("<WITHDRAW></WITHDRAW>");
                sbf.append("</IN_TRANSACTION>");

                sbf.append("<OUT_TRANSACTION>");
                sbf.append("<PRODSHORTNAME></PRODSHORTNAME>");
                sbf.append("<TRANSFER></TRANSFER>");
                sbf.append("<WITHDRAW></WITHDRAW>");
                sbf.append("</OUT_TRANSACTION>");

                sbf.append("<SUBS_OUT_TRANSACTION>");
                sbf.append("<SERVICE_NAME></SERVICE_NAME>");
                sbf.append("<PRODSHORTNAME></PRODSHORTNAME>");
                sbf.append("<AMOUNT></AMOUNT>");
                sbf.append("</SUBS_OUT_TRANSACTION>");
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error("generateExtDailyTransactionResponse", "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "POSPOSXMLStringParser[generateExtDailyTransactionResponse]", PretupsErrorCodesI.C2S_ERROR_EXCEPTION, "", "", "generateExtDailyTransactionResponse:" + e
                    .getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("generateExtDailyTransactionResponse", "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * getDateTime
     * 
     * @param p_date
     * @param p_format
     * @return string
     */
    private static String getDateTime(Date p_date, String p_format) {
        if (p_format == null || p_format.length() <= 0) {
            p_format = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATETIME_FORMAT));// PretupsI.TIMESTAMP_DATESPACEHHMMSS;
        }
        final SimpleDateFormat sdf = new SimpleDateFormat(p_format);
        sdf.setLenient(false);
        if (p_date == null) {
            p_date = new Date();
        }
        return sdf.format(p_date);
    }

    /**
     * generateExtChangepinResponse
     * Response of Change Pin request from the external system.
     * 
     * @param p_requestVO
     * @param p_action
     * @throws Exception
     * @author bobba
     */
    public static void generateExtChangepinResponse(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("generateExtChangepinResponse", "Entered p_requestVO: " + p_requestVO.toString());
        }
        final String METHOD_NAME = "generateExtChangepinResponse";
        String responseStr = null;
        StringBuffer sbf = null;
        final java.util.Date date = new java.util.Date();
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><COMMAND>");
            sbf.append("<TYPE>EXC2SCPNRESP</TYPE>");
            final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DDMMYYYYHHMMSS);
            sdf.setLenient(false);
            sbf.append("<DATE>").append(sdf.format(date)).append("</DATE>");

            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                String message = p_requestVO.getMessageCode();
                if (message.indexOf("_") != -1) {
                    message = message.substring(0, message.indexOf("_"));
                }
                sbf.append("<TXNSTATUS>").append(message).append("</TXNSTATUS>");
            }
            sbf.append("<EXTREFNUM>").append(p_requestVO.getExternalReferenceNum()).append("</EXTREFNUM>");
            /*
             * if(!p_requestVO.isSuccessTxn())
             * sbf.append("<MESSAGE>"+getMessage(p_requestVO.getLocale(),p_requestVO
             * .
             * getMessageCode(),p_requestVO.getMessageArguments())+"</MESSAGE>")
             * ;
             * else
             */// sbf.append("<MESSAGE></MESSAGE>");
            sbf.append("<MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments())).append("</MESSAGE>");
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("generateExtChangepinResponse", "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "POSXMLStringParser[generateExtChangepinResponse]",
                PretupsErrorCodesI.C2S_ERROR_EXCEPTION, "", "", "generateExtChangepinResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("generateExtC2CTransferResponse", "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * Response sent to External system for the channel user balance enquiry
     * request
     * 
     * @param p_requestVO
     * @throws Exception
     */
    public static void generateExtChannelUserBalanceResponse(RequestVO p_requestVO) throws Exception {
    	final String METHOD_NAME = "generateExtChannelUserBalanceResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        final java.util.Date date = new java.util.Date();
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><COMMAND>");
            sbf.append("<TYPE>EXUSRBALRESP</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                String message = p_requestVO.getMessageCode();
                if (message.indexOf("_") != -1) {
                    message = message.substring(0, message.indexOf("_"));
                }
                sbf.append("<TXNSTATUS>").append(message).append("</TXNSTATUS>");
            }

            final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DDMMYYYYHHMMSS);
            sdf.setLenient(false);
            sbf.append("<DATE>").append(sdf.format(date)).append("</DATE>");

            if (p_requestVO.getRequestMap() != null) {
                sbf.append("<EXTREFNUM>").append((String) p_requestVO.getRequestMap().get("EXTREFNUM")).append("</EXTREFNUM>");
            } else {
                sbf.append("<EXTREFNUM></EXTREFNUM>");
            }

            if (p_requestVO.isSuccessTxn()) {
                final ArrayList arrayList = (ArrayList) p_requestVO.getValueObject();
                if (arrayList != null) {
                    UserBalancesVO userBalancesVO = null;
                    final int listSize = arrayList.size();
                    for (int i = 0; i < listSize; i++) {
                        userBalancesVO = (UserBalancesVO) arrayList.get(i);
                        sbf.append("<RECORD>");
                        sbf.append("<PRODUCTCODE>").append(userBalancesVO.getProductShortCode()).append("</PRODUCTCODE>");
                        sbf.append("<PRODUCTSHORTNAME>").append(userBalancesVO.getProductShortName()).append("</PRODUCTSHORTNAME>");
                        sbf.append("<BALANCE>").append(userBalancesVO.getBalanceStr()).append("</BALANCE>");
                        sbf.append("</RECORD>");
                    }
                }
                // else
                // sbf.append("<MESSAGE>"+p_requestVO.getMessageCode()+"</MESSAGE>");
            }
            // else
            // sbf.append("<MESSAGE>"+p_requestVO.getMessageCode()+"</MESSAGE>");

            sbf.append("<MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments())).append("</MESSAGE>");

            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(METHOD_NAME, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "POSXMLStringParser[generateExtChannelUserBalanceResponse]", PretupsErrorCodesI.C2S_ERROR_EXCEPTION, "", "", "generateExtChannelUserBalanceResponse:" + e
                    .getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * Response sent to External system for the channel user other balance enquiry
     * request
     * 
     * @param p_requestVO
     * @throws Exception
     */
    public static void generateExtChannelUserOtherBalRes(RequestVO p_requestVO) throws Exception {
    	final String METHOD_NAME = "generateExtChannelUserOtherBalRes";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        final java.util.Date date = new java.util.Date();
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><COMMAND>");
            sbf.append("<TYPE>EXOTHUSRBALRESP</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                String message = p_requestVO.getMessageCode();
                if (message.indexOf("_") != -1) {
                    message = message.substring(0, message.indexOf("_"));
                }
                sbf.append("<TXNSTATUS>").append(message).append("</TXNSTATUS>");
            }

            final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DDMMYYYYHHMMSS);
            sdf.setLenient(false);
            sbf.append("<DATE>").append(sdf.format(date)).append("</DATE>");

            if (p_requestVO.getRequestMap() != null) {
                sbf.append("<EXTREFNUM>").append((String) p_requestVO.getRequestMap().get("EXTREFNUM")).append("</EXTREFNUM>");
            } else {
                sbf.append("<EXTREFNUM></EXTREFNUM>");
            }

            if (p_requestVO.isSuccessTxn()) {
                final ArrayList arrayList = (ArrayList) p_requestVO.getValueObject();
                if (arrayList != null) {
                    UserBalancesVO userBalancesVO = null;
                    final int listSize = arrayList.size();
                    for (int i = 0; i < listSize; i++) {
                        userBalancesVO = (UserBalancesVO) arrayList.get(i);
                        sbf.append("<RECORD>");
                        sbf.append("<PRODUCTCODE>").append(userBalancesVO.getProductShortCode()).append("</PRODUCTCODE>");
                        sbf.append("<PRODUCTSHORTNAME>").append(userBalancesVO.getProductShortName()).append("</PRODUCTSHORTNAME>");
                        sbf.append("<BALANCE>").append(userBalancesVO.getBalanceStr()).append("</BALANCE>");
                        sbf.append("</RECORD>");
                    }
                }
                // else
                // sbf.append("<MESSAGE>"+p_requestVO.getMessageCode()+"</MESSAGE>");
            }
            // else
            // sbf.append("<MESSAGE>"+p_requestVO.getMessageCode()+"</MESSAGE>");
            sbf.append("<MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments())).append("</MESSAGE>");
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(METHOD_NAME, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "POSXMLStringParser[generateExtChannelUserBalanceResponse]", PretupsErrorCodesI.C2S_ERROR_EXCEPTION, "", "", "generateExtChannelUserBalanceResponse:" + e
                    .getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /* *//**
     * Response sent to External system for the channel user balace enquiry
     * request
     * 
     * @param p_requestVO
     * @throws Exception
     */
    /*
     * public static void generateExtCustomerEnqRes(RequestVO p_requestVO)
     * throws Exception
     * {
     * if(_log.isDebugEnabled())_log.debug("generateExtCustomerEnqRes",
     * "Entered p_requestVO: "+p_requestVO.toString());
     * String responseStr= null;
     * StringBuffer sbf=null;
     * java.util.Date date=new java.util.Date();
     * try
     * {
     * sbf=new StringBuffer(1024);
     * sbf.append("<?xml version=\"1.0\"?><COMMAND>");
     * sbf.append("<TYPE>EXOTHUSRBALRESP</TYPE>");
     * if(p_requestVO.isSuccessTxn())
     * sbf.append("<TXNSTATUS>"+PretupsI.TXN_STATUS_SUCCESS+"</TXNSTATUS>");
     * else
     * {
     * String message = p_requestVO.getMessageCode();
     * if(message.indexOf("_") != -1)
     * {
     * message = message.substring(0,message.indexOf("_"));
     * }
     * sbf.append("<TXNSTATUS>"+message+"</TXNSTATUS>");
     * }
     * 
     * SimpleDateFormat sdf = new SimpleDateFormat (PretupsI.TIMESTAMP_DDMMYYYYHHMMSS);
     * sdf.setLenient(false);
     * sbf.append("<DATE>"+sdf.format(date)+"</DATE>");
     * 
     * if(p_requestVO.getRequestMap() != null)
     * sbf.append("<EXTREFNUM>"+(String)p_requestVO.getRequestMap().get("EXTREFNUM"
     * )+"</EXTREFNUM>");
     * else
     * sbf.append("<EXTREFNUM></EXTREFNUM>");
     * 
     * if (p_requestVO.isSuccessTxn())
     * {
     * ArrayList arrayList = (ArrayList)p_requestVO.getValueObject();
     * if(arrayList!=null)
     * {
     * UserBalancesVO userBalancesVO=null;
     * int listSize=arrayList.size();
     * for (int i=0;i<listSize;i++)
     * {
     * userBalancesVO=(UserBalancesVO)arrayList.get(i);
     * sbf.append("<RECORD>");
     * sbf.append("<PRODUCTCODE>"+userBalancesVO.getProductShortCode()+
     * "</PRODUCTCODE>");
     * sbf.append("<PRODUCTSHORTNAME>"+userBalancesVO.getProductShortName()+
     * "</PRODUCTSHORTNAME>");
     * sbf.append("<BALANCE>"+userBalancesVO.getBalanceStr()+"</BALANCE>");
     * sbf.append("</RECORD>");
     * }
     * }
     * else
     * sbf.append("<MESSAGE>"+p_requestVO.getMessageCode()+"</MESSAGE>");
     * }
     * else
     * sbf.append("<MESSAGE>"+p_requestVO.getMessageCode()+"</MESSAGE>");
     * sbf.append("</COMMAND>");
     * responseStr = sbf.toString();
     * p_requestVO.setSenderReturnMessage(responseStr);
     * }
     * catch(Exception e)
     * {
     * e.printStackTrace();
     * _log.error("generateExtCustomerEnqRes","Exception e: "+e);
     * p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
     * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI
     * .
     * RAISED,EventLevelI.FATAL,"POSXMLStringParser[generateExtCustomerEnqRes]",
     * PretupsErrorCodesI
     * .C2S_ERROR_EXCEPTION,"","","generateExtCustomerEnqRes:"+e.getMessage());
     * }
     * finally
     * {
     * if(_log.isDebugEnabled())_log.debug("generateExtCustomerEnqRes",
     * "Exiting responseStr: "+responseStr);
     * }
     * }
     */

    /**
     * generateExtC2CTransferResponse
     * Response of C2C transfer request from the external system.
     * 
     * @param p_requestVO
     * @throws Exception
     * @author ved.sharma
     */
    public static void generateExtChannelLastTransferStatusResponse(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("generateExtChannelLastTransferStatusResponse", "Entered p_requestVO: " + p_requestVO.toString());
        }
        final String METHOD_NAME = "generateExtChannelLastTransferStatusResponse";
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><COMMAND>");
            sbf.append("<TYPE>EXLSTTRFRESP</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<REQSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</REQSTATUS>");
            } else {
                sbf.append("<REQSTATUS>").append(p_requestVO.getMessageCode()).append("</REQSTATUS>");
            }
            sbf.append("<DATE>").append(getDateTime(null, null)).append("</DATE>");
            sbf.append("<EXTREFNUM>").append(p_requestVO.getExternalReferenceNum()).append("</EXTREFNUM>");
            // Locale locale=new
            // Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            sbf.append("<MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments())).append("</MESSAGE>");

            final HashMap requestHashMap = p_requestVO.getRequestMap();
            if (requestHashMap != null) {
                final String lastTransferType = (String) requestHashMap.get("LASTTRFTYPE");
                if (PretupsI.LAST_TRANSACTION_C2S_TYPE.equalsIgnoreCase(lastTransferType)) {
                    final C2STransferVO c2sTransferVO = (C2STransferVO) requestHashMap.get(PretupsI.LAST_TRANSACTION_C2S_TYPE);
                    if (c2sTransferVO != null) {
                        sbf.append("<TXNID>").append(c2sTransferVO.getTransferID()).append("</TXNID>");
                        sbf.append("<TXNDATETIME>").append(getDateTime(c2sTransferVO.getTransferDateTime(), null)).append("</TXNDATETIME>");
                        sbf.append("<TRFTYPE>").append(PretupsI.LAST_TRANSACTION_C2S_TYPE).append("</TRFTYPE>");
                        sbf.append("<TXNSTATUS>").append(c2sTransferVO.getValue()).append("</TXNSTATUS>");
                        sbf.append("<RECORD>");
                        sbf.append("<PRODUCTCODE>").append(c2sTransferVO.getProductShortCode()).append("</PRODUCTCODE>");
                        sbf.append("<PRODUCTSHORTNAME>").append(c2sTransferVO.getProductName()).append("</PRODUCTSHORTNAME>");
                        sbf.append("<BALANCE>").append(PretupsBL.getDisplayAmount(c2sTransferVO.getTransferValue())).append("</BALANCE>");
                        sbf.append("</RECORD>");
                    } else {
                        sbf.append("<TXNID></TXNID>");
                        sbf.append("<TXNDATETIME></TXNDATETIME>");
                        sbf.append("<TRFTYPE></TRFTYPE>");
                        sbf.append("<TXNSTATUS></TXNSTATUS>");
                        sbf.append("<RECORD>");
                        sbf.append("<PRODUCTCODE></PRODUCTCODE>");
                        sbf.append("<PRODUCTSHORTNAME></PRODUCTSHORTNAME>");
                        sbf.append("<BALANCE></BALANCE>");
                        sbf.append("</RECORD>");
                    }

                } else if (PretupsI.LAST_TRANSACTION_C2C_TYPE.equalsIgnoreCase(lastTransferType) || PretupsI.LAST_TRANSACTION_O2C_TYPE.equalsIgnoreCase(lastTransferType)) {
                    final ChannelTransferVO channelTransferVO = (ChannelTransferVO) requestHashMap.get(lastTransferType);
                    if (channelTransferVO != null) {
                        sbf.append("<TXNID>").append(channelTransferVO.getTransferID()).append("</TXNID>");
                        sbf.append("<TXNDATETIME>").append(getDateTime(channelTransferVO.getCreatedOn(), null)).append("</TXNDATETIME>");
                        sbf.append("<TRFTYPE>").append(channelTransferVO.getTransferType()).append("</TRFTYPE>");
                        sbf.append("<TXNSTATUS>").append(channelTransferVO.getStatus()).append("</TXNSTATUS>");
                        final ArrayList list = channelTransferVO.getChannelTransferitemsVOList();
                        ChannelTransferItemsVO channelTransferItemsVO = null;
                        for (int i = 0, j = list.size(); i < j; i++) {
                            channelTransferItemsVO = (ChannelTransferItemsVO) list.get(i);
                            sbf.append("<RECORD>");
                            sbf.append("<PRODUCTCODE>").append(channelTransferItemsVO.getProductShortCode()).append("</PRODUCTCODE>");
                            sbf.append("<PRODUCTSHORTNAME>").append(channelTransferItemsVO.getShortName()).append("</PRODUCTSHORTNAME>");
                            sbf.append("<BALANCE>").append(PretupsBL.getDisplayAmount(channelTransferItemsVO.getApprovedQuantity())).append("</BALANCE>");
                            sbf.append("</RECORD>");
                        }
                    } else {
                        sbf.append("<TXNID></TXNID>");
                        sbf.append("<TXNDATETIME></TXNDATETIME>");
                        sbf.append("<TRFTYPE></TRFTYPE>");
                        sbf.append("<TXNSTATUS></TXNSTATUS>");
                        sbf.append("<RECORD>");
                        sbf.append("<PRODUCTCODE></PRODUCTCODE>");
                        sbf.append("<PRODUCTSHORTNAME></PRODUCTSHORTNAME>");
                        sbf.append("<BALANCE></BALANCE>");
                        sbf.append("</RECORD>");
                    }
                } else {
                    sbf.append("<TXNID></TXNID>");
                    sbf.append("<TXNDATETIME></TXNDATETIME>");
                    sbf.append("<TRFTYPE></TRFTYPE>");
                    sbf.append("<TXNSTATUS></TXNSTATUS>");
                    sbf.append("<RECORD>");
                    sbf.append("<PRODUCTCODE></PRODUCTCODE>");
                    sbf.append("<PRODUCTSHORTNAME></PRODUCTSHORTNAME>");
                    sbf.append("<BALANCE></BALANCE>");
                    sbf.append("</RECORD>");
                }
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("generateExtChannelLastTransferStatusResponse", "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "POSPOSXMLStringParser[generateExtChannelLastTransferStatusResponse]", PretupsErrorCodesI.C2S_ERROR_EXCEPTION, "", "",
                "generateExtChannelLastTransferStatusResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("generateExtChannelLastTransferStatusResponse", "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * generateExtC2CTransferResponse
     * Response of C2C transfer request from the external system.
     * 
     * @param p_requestVO
     * @throws Exception
     * @author ved.sharma
     */
    public static void generateExtLastXTrfResponse(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("generateExtLastXTrfResponse", "Entered p_requestVO: " + p_requestVO.toString());
        }
        final String METHOD_NAME = "generateExtLastXTrfResponse";
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><COMMAND>");
            sbf.append("<TYPE>EXLST3TRFRESP</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<REQSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</REQSTATUS>");
            } else {
                sbf.append("<REQSTATUS>").append(p_requestVO.getMessageCode()).append("</REQSTATUS>");
            }
            sbf.append("<DATE>").append(getDateTime(null, null)).append("</DATE>");
            sbf.append("<EXTREFNUM>").append(p_requestVO.getExternalReferenceNum()).append("</EXTREFNUM>");
            // Locale locale=new
            // Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            sbf.append("<MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments())).append("</MESSAGE>");
            sbf.append("<TXNDETAILS>");
            final HashMap requestHashMap = p_requestVO.getRequestMap();
            if (requestHashMap != null) {
                final ArrayList lastTransferList = (ArrayList) requestHashMap.get("TRANSFERLIST");
                if (lastTransferList != null && !lastTransferList.isEmpty()) {
                	int lastTransfersList=lastTransferList.size();
                    for (int i = 0; i <lastTransfersList ; i++) {
                        final C2STransferVO c2sTransferVO = (C2STransferVO) lastTransferList.get(i);
                        if (c2sTransferVO != null) {
                            sbf.append("<TXNDETAIL>");
                            sbf.append("<TXNID>").append(c2sTransferVO.getTransferID()).append("</TXNID>");
                            sbf.append("<TXNDATETIME>").append(getDateTime(c2sTransferVO.getTransferDateTime(), null)).append("</TXNDATETIME>");
                            sbf.append("<TRFTYPE>").append(c2sTransferVO.getType()).append("</TRFTYPE>");
                            sbf.append("<TXNSTATUS>").append(c2sTransferVO.getStatus()).append("</TXNSTATUS>");
                            // manisha
                            sbf.append("<TXNAMOUNT>").append(PretupsBL.getDisplayAmount(c2sTransferVO.getTransferValue())).append("</TXNAMOUNT>");
                            sbf.append("</TXNDETAIL>");
                        }
                    }
                } else {
                    sbf.append("<TXNDETAIL>");
                    sbf.append("<TXNID></TXNID>");
                    sbf.append("<TXNDATETIME></TXNDATETIME>");
                    sbf.append("<TRFTYPE></TRFTYPE>");
                    sbf.append("<TXNSTATUS></TXNSTATUS>");
                    sbf.append("<TXNAMOUNT></TXNAMOUNT>");
                    sbf.append("</TXNDETAIL>");
                }

            }
            sbf.append("</TXNDETAILS></COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("generateExtLastXTrfResponse", "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "POSPOSXMLStringParser[generateExtLastXTrfResponse]",
                PretupsErrorCodesI.C2S_ERROR_EXCEPTION, "", "", "generateExtLastXTrfResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("generateExtLastXTrfResponse", "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * generateExtC2CTransferResponse
     * Response of C2C transfer request from the external system.
     * 
     * @param p_requestVO
     * @throws Exception
     * @author ved.sharma
     */
    public static void generateExtCustomerEnqResponse(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("generateExtCustomerEnqResponse", "Entered p_requestVO: " + p_requestVO.toString());
        }
        final String METHOD_NAME = "generateExtCustomerEnqResponse";
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><COMMAND>");
            sbf.append("<TYPE>EXCUSTRESP</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<REQSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</REQSTATUS>");
            } else {
                sbf.append("<REQSTATUS>").append(p_requestVO.getMessageCode()).append("</REQSTATUS>");
            }
            sbf.append("<DATE>").append(getDateTime(null, null)).append("</DATE>");
            sbf.append("<EXTREFNUM>").append(p_requestVO.getExternalReferenceNum()).append("</EXTREFNUM>");
            // Locale locale=new
            // Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            sbf.append("<MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments())).append("</MESSAGE>");

            sbf.append("<TXNDETAILS>");
            final HashMap requestHashMap = p_requestVO.getRequestMap();
            if (requestHashMap != null) {
                final ArrayList lastTransferList = (ArrayList) requestHashMap.get("TRANSFERLIST");
                if (lastTransferList != null && !lastTransferList.isEmpty()) {
                	int lastTranList=lastTransferList.size();
                    for (int i = 0; i < lastTranList; i++) {
                        final C2STransferVO c2sTransferVO = (C2STransferVO) lastTransferList.get(i);
                        if (c2sTransferVO != null) {
                            sbf.append("<TXNDETAIL>");
                            sbf.append("<TXNID>").append(c2sTransferVO.getTransferID()).append("</TXNID>");
                            sbf.append("<TXNDATETIME>").append(getDateTime(c2sTransferVO.getTransferDateTime(), null)).append("</TXNDATETIME>");
                            sbf.append("<TRFTYPE>").append(PretupsI.LAST_TRANSACTION_C2S_TYPE).append("</TRFTYPE>");
                            sbf.append("<TXNSTATUS>").append(c2sTransferVO.getStatus()).append("</TXNSTATUS>");
                            // manisha
                            sbf.append("<TXNAMOUNT>").append(PretupsBL.getDisplayAmount(c2sTransferVO.getTransferValue())).append("</TXNAMOUNT>");
                            sbf.append("</TXNDETAIL>");
                        }
                    }
                } else {
                    sbf.append("<TXNDETAIL>");
                    sbf.append("<TXNID></TXNID>");
                    sbf.append("<TXNDATETIME></TXNDATETIME>");
                    sbf.append("<TRFTYPE></TRFTYPE>");
                    sbf.append("<TXNSTATUS></TXNSTATUS>");
                    // manisha
                    sbf.append("<TXNAMOUNT></TXNAMOUNT>");
                    sbf.append("</TXNDETAIL>");
                }

            }
            sbf.append("</TXNDETAILS></COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("generateExtCustomerEnqResponse", "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "POSPOSXMLStringParser[generateExtCustomerEnqResponse]", PretupsErrorCodesI.C2S_ERROR_EXCEPTION, "", "", "generateExtCustomerEnqResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("generateExtCustomerEnqResponse", "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * parseExtChannelLastTransferStatusRequest
     * Parse the last transfer status request from the external system.
     * 
     * @param p_requestVO
     * @throws Exception
     * @author ved.sharma
     */
    public static void parseExtChannelLastTransferStatusRequest(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("parseExtChannelLastTransferStatusRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        final String METHOD_NAME = "parseExtChannelLastTransferStatusRequest";
        try {
            String parsedRequestStr = null;

            HashMap requestHashMap = p_requestVO.getRequestMap();
            if (requestHashMap == null) {
                requestHashMap = new HashMap();
            }
            String requestStr = p_requestVO.getRequestMessage();
            String msisdn = null;
            String extRefNumber = null;
            String password = null;
            String extCode = null;
            String extNwCode = null;
            String date = null;
            String language1 = null;
            requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            requestHashMap.put("TYPE", type);
            index = requestStr.indexOf("<DATE>");
            if (index > 0) {
                date = requestStr.substring(index + "<DATE>".length(), requestStr.indexOf("</DATE>", index));
                requestHashMap.put("DATE", date);
            }

            index = requestStr.indexOf("<EXTNWCODE>");
            extNwCode = requestStr.substring(index + "<EXTNWCODE>".length(), requestStr.indexOf("</EXTNWCODE>", index));
            requestHashMap.put("EXTNWCODE", extNwCode);

            index = requestStr.indexOf("<MSISDN>");
            if (index > 0) {
                msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
                requestHashMap.put("MSISDN", msisdn);
            }

            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            requestHashMap.put("PIN", pin);

            index = requestStr.indexOf("<LOGINID>");
            final String loginId = requestStr.substring(index + "<LOGINID>".length(), requestStr.indexOf("</LOGINID>", index));
            requestHashMap.put("LOGINID", loginId);

            index = requestStr.indexOf("<PASSWORD>");
            if (index > 0) {
                password = requestStr.substring(index + "<PASSWORD>".length(), requestStr.indexOf("</PASSWORD>", index));
                requestHashMap.put("PASSWORD", password);
            }

            index = requestStr.indexOf("<EXTCODE>");
            if (index > 0) {
                extCode = requestStr.substring(index + "<EXTCODE>".length(), requestStr.indexOf("</EXTCODE>", index));
                requestHashMap.put("EXTCODE", extCode);
            }

            index = requestStr.indexOf("<EXTREFNUM>");
            if (index > 0) {
                extRefNumber = requestStr.substring(index + "<EXTREFNUM>".length(), requestStr.indexOf("</EXTREFNUM>", index));
                requestHashMap.put("EXTREFNUM", extRefNumber);
            }
            index = requestStr.indexOf("<LANGUAGE1>");
            if (index > 0) {
                language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
                requestHashMap.put("LANGUAGE1", language1);
                if (!BTSLUtil.isNullString(language1)) {
                    p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
                }
            }

            if (BTSLUtil.isNullString(type) || BTSLUtil.isNullString(loginId) || BTSLUtil.isNullString(pin) || BTSLUtil.isNullString(extNwCode)) {
                throw new BTSLBaseException("POSPOSXMLStringParser", "parseExtChannelLastTransferStatusRequest", PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }
            parsedRequestStr = PretupsI.SERVICE_TYPE_LAST_TRANSFER_STATUS;

            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn);
            p_requestVO.setExternalNetworkCode(extNwCode);
            p_requestVO.setExternalReferenceNum(extRefNumber);// To enquire
            // Transaction
            // Status with
            // External
            // Refrence Number
            p_requestVO.setSenderExternalCode(extCode);
            p_requestVO.setSenderLoginID(loginId);
            p_requestVO.setRequestMap(requestHashMap);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseExtChannelLastTransferStatusRequest", "Exception e: " + e);
            throw new BTSLBaseException("POSPOSXMLStringParser", "parseExtChannelLastTransferStatusRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseExtChannelLastTransferStatusRequest", "Exiting p_requestVO ID: " + p_requestVO.getRequestIDStr());
            }
        }
    }

    /**
     * @param p_requestVO
     * @throws Exception
     * @author ranjana.chouhan
     */
    public static void generateExtPostpaidBillPaymentResponse(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("generateExtPostpaidBillPaymentResponse", "Entered p_requestVO: " + p_requestVO.toString());
        }
        final String METHOD_NAME = "generateExtPostpaidBillPaymentResponse";
        String responseStr = null;
        StringBuffer sbf = null;
        final java.util.Date date = new java.util.Date();
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DDMMYYYYHHMMSS);
            sdf.setLenient(false); // this is required else it will convert
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><COMMAND>");
            sbf.append("<TYPE>EXTPPBRESP</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                String message = p_requestVO.getMessageCode();
                if (message.indexOf("_") != -1) {
                    message = message.substring(0, message.indexOf("_"));
                }
                sbf.append("<TXNSTATUS>").append(message).append("</TXNSTATUS>");
            }

            sbf.append("<DATE>").append(sdf.format(date)).append("</DATE>");

            if (p_requestVO.getRequestMap() != null) {
                sbf.append("<EXTREFNUM>").append((String) p_requestVO.getRequestMap().get("EXTREFNUM")).append("</EXTREFNUM>");
            } else {
                sbf.append("<EXTREFNUM></EXTREFNUM>");
            }

            if (BTSLUtil.isNullString(p_requestVO.getTransactionID())) {
                sbf.append("<TXNID></TXNID>");
            } else {
                sbf.append("<TXNID>").append(p_requestVO.getTransactionID()).append("</TXNID>");
            }
            /*
             * if(p_requestVO.getMessageArguments()==null)
             * sbf.append("<MESSAGE>"+p_requestVO.getMessageCode()+"</MESSAGE>");
             */
            if (!p_requestVO.isSuccessTxn()) {
                sbf.append("<MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments())).append("</MESSAGE>");
            } else {
                sbf.append("<MESSAGE>").append(getMessage(p_requestVO.getLocale(), PretupsErrorCodesI.C2S_SENDER_SUCCESS_BILLPAY, p_requestVO.getMessageArguments())).append("</MESSAGE>");
            }
            // sbf.append("<MESSAGE></MESSAGE>");
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("generateExtPostpaidBillPaymentResponse", "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "POSXMLStringParser[generateExtPostpaidBillPaymentResponse]", PretupsErrorCodesI.C2S_ERROR_EXCEPTION, "", "", "generateExtPostpaidBillPaymentResponse:" + e
                    .getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("generateExtPostpaidBillPaymentResponse", "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * parseExtDailyTransactionRequest
     * Parse the daily Transaction Report request from the external system.
     * 
     * @param p_requestVO
     * @throws Exception
     * @author ved.sharma
     */
    public static void parseExtDailyTransactionRequest(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("parseExtDailyTransactionRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        final String METHOD_NAME = "parseExtDailyTransactionRequest";
        try {
            String parsedRequestStr = null;
            String language1 = null;
            HashMap requestHashMap = p_requestVO.getRequestMap();
            if (requestHashMap == null) {
                requestHashMap = new HashMap();
            }

            final String requestStr = p_requestVO.getRequestMessage();
            String msisdn = null;
            String extRefNumber = null;
            String password = null;
            String extCode = null;
            String extNwCode = null;
            String date = null;

            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            requestHashMap.put("TYPE", type);
            index = requestStr.indexOf("<DATE>");
            if (index > 0) {
                date = requestStr.substring(index + "<DATE>".length(), requestStr.indexOf("</DATE>", index));
                requestHashMap.put("DATE", date);
            }

            index = requestStr.indexOf("<EXTNWCODE>");
            extNwCode = requestStr.substring(index + "<EXTNWCODE>".length(), requestStr.indexOf("</EXTNWCODE>", index));
            requestHashMap.put("EXTNWCODE", extNwCode);

            index = requestStr.indexOf("<MSISDN>");
            if (index > 0) {
                msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
                requestHashMap.put("MSISDN", msisdn);
            }

            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            requestHashMap.put("PIN", pin);

            index = requestStr.indexOf("<LOGINID>");
            final String loginId = requestStr.substring(index + "<LOGINID>".length(), requestStr.indexOf("</LOGINID>", index));
            requestHashMap.put("LOGINID", loginId);

            index = requestStr.indexOf("<PASSWORD>");
            if (index > 0) {
                password = requestStr.substring(index + "<PASSWORD>".length(), requestStr.indexOf("</PASSWORD>", index));
                requestHashMap.put("PASSWORD", password);
            }

            index = requestStr.indexOf("<EXTCODE>");
            if (index > 0) {
                extCode = requestStr.substring(index + "<EXTCODE>".length(), requestStr.indexOf("</EXTCODE>", index));
                requestHashMap.put("EXTCODE", extCode);
            }

            index = requestStr.indexOf("<EXTREFNUM>");
            if (index > 0) {
                extRefNumber = requestStr.substring(index + "<EXTREFNUM>".length(), requestStr.indexOf("</EXTREFNUM>", index));
                requestHashMap.put("EXTREFNUM", extRefNumber);
            }

            index = requestStr.indexOf("<LANGUAGE1>");
            if (index > 0) {
                language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
                requestHashMap.put("LANGUAGE1", language1);
                if (!BTSLUtil.isNullString(language1)) {
                    p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
                }
            }

            if (BTSLUtil.isNullString(type) || BTSLUtil.isNullString(loginId) || BTSLUtil.isNullString(pin) || BTSLUtil.isNullString(extNwCode)) {
                throw new BTSLBaseException("POSPOSXMLStringParser", "parseExtDailyTransactionRequest", PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }

            parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_DAILY_STATUS_REPORT;

            p_requestVO.setDecryptedMessage(parsedRequestStr);
            // p_requestVO.setRequestMSISDN(msisdn);
            p_requestVO.setExternalNetworkCode(extNwCode);
            p_requestVO.setExternalReferenceNum(extRefNumber);// To enquire
            // Transaction
            // Status with
            // External
            // Refrence Number
            p_requestVO.setSenderExternalCode(extCode);
            p_requestVO.setSenderLoginID(loginId);
            p_requestVO.setRequestMap(requestHashMap);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseExtDailyTransactionRequest", "Exception e: " + e);
            throw new BTSLBaseException("POSPOSXMLStringParser", "parseExtDailyTransactionRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseExtDailyTransactionRequest", "Exiting p_requestVO ID: " + p_requestVO.getRequestIDStr());
            }
        }
    }

    /**
     * Generate Fail message
     * 
     * @param p_requestVO
     * @throws Exception
     */
    public static void generateFailureResponse(RequestVO p_requestVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("generateFailureResponse", "Entered :p_requestVO " + p_requestVO.toString());
        }
        final String METHOD_NAME = "generateFailureResponse";
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><COMMAND>");
            sbf.append("<TYPE></TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.error("generateFailureResponse", "Exception e: " + e);
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("POSXMLStringParser", METHOD_NAME, "Exception in generating Failure Response");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("generateFailureResponse", "Exiting responseStr: " + responseStr);
            }
        }
    }

    private static String getMessage(Locale locale, String key, String[] p_args1) {
        final String METHOD_NAME = "getMessage";
        String[] p_args = null;
        if (p_args1 == null || p_args1.length == 0) {
            p_args = p_args1;
        } else {
            p_args = new String[p_args1.length];
            for (int i = 0; i < p_args.length; i++) {
                p_args[i] = p_args1[i];
            }
        }
        String message = BTSLUtil.getMessage(locale, key, p_args);
        try {
            final LocaleMasterVO localeMasterVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
            if (message.indexOf("mclass^") == 0) {
                final int colonIndex = message.indexOf(":");
                final String messageClassPID = message.substring(0, colonIndex);
                final String[] messageClassPIDArray = messageClassPID.split("&");
                final String messageClass = messageClassPIDArray[0].split("\\^")[1];
                final String pid = messageClassPIDArray[1].split("\\^")[1];
                message = message.substring(colonIndex + 1);
                int endIndexForMessageCode;
                String messageCode = null;
                if ("ar".equals(localeMasterVO.getLanguage())) {
                    endIndexForMessageCode = message.indexOf("%00%3A");
                    if (endIndexForMessageCode != -1) {
                        messageCode = URLDecoder.decode(message.substring(0, endIndexForMessageCode), "UTF16");
                        // message=message.substring(endIndexForMessageCode+1);
                        message = message.substring(endIndexForMessageCode + "%00%3A".length());
                        if (message.startsWith("%00%20")) {
                            message = message.substring("%00%20".length());
                        }
                    }
                } else {
                    endIndexForMessageCode = message.indexOf(":");
                    if (endIndexForMessageCode != -1) {
                        messageCode = message.substring(0, endIndexForMessageCode);
                        message = message.substring(endIndexForMessageCode + 1);
                    }
                }
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("getMessage", "Exception e: " + e);
        }
        return message;
    }

    // added by nilesh: for private recharge
    /**
     * Request method for private Recharge through POS
     * 
     * @param p_requestVO
     * @throws Exception
     * @author nilesh.kumar
     */

    public static void parseChannelExtPrivateRechargeRequest(RequestVO p_requestVO) throws Exception {
        final String methodName = "parseChannelExtPrivateRechargeRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        try {
            final HashMap requestHashMap = new HashMap();
            final String[] newMessageArray = null;
            final String requestStr = p_requestVO.getRequestMessage();
            String msisdn = null;
            String language1 = null;
            String extRefNumber = null;
            String password = null;
            String extCode = null;
            String date = null;

            // Narendra VFE 6 CR
            String info1 = null;
            String info2 = null;
            String info3 = null;
            String info4 = null;
            String info5 = null;
            String info6 = null;
            String info7 = null;
            String info8 = null;
            String info9 = null;
            String info10 = null;
            // END

            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            requestHashMap.put("TYPE", type);
            index = requestStr.indexOf("<DATE>");
            if (index > 0) {
                date = requestStr.substring(index + "<DATE>".length(), requestStr.indexOf("</DATE>", index));
                requestHashMap.put("DATE", date);
            }
            index = requestStr.indexOf("<EXTNWCODE>");
            final String extNwCode = requestStr.substring(index + "<EXTNWCODE>".length(), requestStr.indexOf("</EXTNWCODE>", index));
            requestHashMap.put("EXTNWCODE", extNwCode);
            index = requestStr.indexOf("<MSISDN>");
            if (index > 0) {
                msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
                requestHashMap.put("MSISDN", msisdn);
            }
            index = requestStr.indexOf("<PIN>");
            String pin = null;
            if (index > 0) {
                pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
                requestHashMap.put("PIN", pin);
            }
            index = requestStr.indexOf("<LOGINID>");
            String loginId = null;
            if (index > 0) {
                loginId = requestStr.substring(index + "<LOGINID>".length(), requestStr.indexOf("</LOGINID>", index));
                requestHashMap.put("LOGINID", loginId);
            }
            index = requestStr.indexOf("<PASSWORD>");
            if (index > 0) {
                password = requestStr.substring(index + "<PASSWORD>".length(), requestStr.indexOf("</PASSWORD>", index));
                requestHashMap.put("PASSWORD", password);
            }
            index = requestStr.indexOf("<EXTCODE>");
            if (index > 0) {
                extCode = requestStr.substring(index + "<EXTCODE>".length(), requestStr.indexOf("</EXTCODE>", index));
                requestHashMap.put("EXTCODE", extCode);
            }
            index = requestStr.indexOf("<EXTREFNUM>");
            if (index > 0) {
                extRefNumber = requestStr.substring(index + "<EXTREFNUM>".length(), requestStr.indexOf("</EXTREFNUM>", index));
                requestHashMap.put("EXTREFNUM", extRefNumber);
            }
            index = requestStr.indexOf("<AMOUNT>");
            String amount = requestStr.substring(index + "<AMOUNT>".length(), requestStr.indexOf("</AMOUNT>", index));
            if (index > 0) {
                amount = requestStr.substring(index + "<AMOUNT>".length(), requestStr.indexOf("</AMOUNT>", index));
                requestHashMap.put("AMOUNT", amount);
            }
            index = requestStr.indexOf("<LANGUAGE1>");
            if (index > 0) {
                language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
                requestHashMap.put("LANGUAGE1", language1);
            }
            index = requestStr.indexOf("<SELECTOR>");
            String selector = null;
            if (index > 0) {
                selector = requestStr.substring(index + "<SELECTOR>".length(), requestStr.indexOf("</SELECTOR>", index));
                requestHashMap.put("SELECTOR", selector);
            }

            // Start added by Narendra

            index = requestStr.indexOf("<INFO1>");
            if (index != -1) {
                info1 = requestStr.substring(index + "<INFO1>".length(), requestStr.indexOf("</INFO1>", index));
            }
            index = requestStr.indexOf("<INFO2>");
            if (index != -1) {
                info2 = requestStr.substring(index + "<INFO2>".length(), requestStr.indexOf("</INFO2>", index));
            }
            index = requestStr.indexOf("<INFO3>");
            if (index != -1) {
                info3 = requestStr.substring(index + "<INFO3>".length(), requestStr.indexOf("</INFO3>", index));
            }
            index = requestStr.indexOf("<INFO4>");
            if (index != -1) {
                info4 = requestStr.substring(index + "<INFO4>".length(), requestStr.indexOf("</INFO4>", index));
            }
            index = requestStr.indexOf("<INFO5>");
            if (index != -1) {
                info5 = requestStr.substring(index + "<INFO5>".length(), requestStr.indexOf("</INFO5>", index));
            }
            index = requestStr.indexOf("<INFO6>");
            if (index != -1) {
                info6 = requestStr.substring(index + "<INFO6>".length(), requestStr.indexOf("</INFO6>", index));
            }
            index = requestStr.indexOf("<INFO7>");
            if (index != -1) {
                info7 = requestStr.substring(index + "<INFO7>".length(), requestStr.indexOf("</INFO7>", index));
            }
            index = requestStr.indexOf("<INFO8>");
            if (index != -1) {
                info8 = requestStr.substring(index + "<INFO8>".length(), requestStr.indexOf("</INFO8>", index));
            }
            index = requestStr.indexOf("<INFO9>");
            if (index != -1) {
                info9 = requestStr.substring(index + "<INFO9>".length(), requestStr.indexOf("</INFO9>", index));
            }
            index = requestStr.indexOf("<INFO10>");
            if (index != -1) {
                info10 = requestStr.substring(index + "<INFO10>".length(), requestStr.indexOf("</INFO10>", index));
            }
            // END narendra
            if (BTSLUtil.isNullString(selector)) {
                final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(PretupsI.SERVICE_TYPE_EVD);
                if (serviceSelectorMappingVO != null) {
                    selector = serviceSelectorMappingVO.getSelectorCode();
                }
            }
            // if(BTSLUtil.isNullString(type) ||
            // BTSLUtil.isNullString(extNwCode) || BTSLUtil.isNullString(amount)
            // ||BTSLUtil.isNullString(loginId) ||
            // BTSLUtil.isNullString(password) )
            if (BTSLUtil.isNullString(type) || BTSLUtil.isNullString(extNwCode) || BTSLUtil.isNullString(loginId) || BTSLUtil.isNullString(pin) || BTSLUtil
                .isNullString(amount) || BTSLUtil.isNullString(selector)) {
                throw new BTSLBaseException("POSXMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }

            // Start added by Narendra

            if (!(BTSLUtil.isNullString(info1))) {
                final int length = info1.length();
                if (length > 100) {
                    _log.debug(methodName, "length is " + length);
                    _log.error(methodName, "Characters are more than 100 ");
                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.C2S_ERROR_MAX_100_INFO1);
                }
            }
            if (!(BTSLUtil.isNullString(info2))) {
                final int length = info2.length();
                if (length > 100) {
                    _log.debug(methodName, "length is " + length);
                    _log.error(methodName, "Characters are more than 100 ");
                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.C2S_ERROR_MAX_100_INFO2);
                }
            }
            if (!(BTSLUtil.isNullString(info3))) {
                final int length = info3.length();
                if (length > 100) {
                    _log.debug(methodName, "length is " + length);
                    _log.error(methodName, "Characters are more than 100 ");
                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.C2S_ERROR_MAX_100_INFO3);
                }
            }
            if (!(BTSLUtil.isNullString(info4))) {
                final int length = info4.length();
                if (length > 100) {
                    _log.debug(methodName, "length is " + length);
                    _log.error(methodName, "Characters are more than 100 ");
                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.C2S_ERROR_MAX_100_INFO4);
                }
            }
            if (!(BTSLUtil.isNullString(info5))) {
                final int length = info5.length();
                if (length > 100) {
                    _log.debug(methodName, "length is " + length);
                    _log.error(methodName, "Characters are more than 100 ");
                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.C2S_ERROR_MAX_100_INFO5);
                }
            }
            if (!(BTSLUtil.isNullString(info6))) {
                final int length = info6.length();
                if (length > 100) {
                    _log.debug(methodName, "length is " + length);
                    _log.error(methodName, "Characters are more than 100 ");
                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.C2S_ERROR_MAX_100_INFO6);
                }
            }
            if (!(BTSLUtil.isNullString(info7))) {
                final int length = info7.length();
                if (length > 100) {
                    _log.debug(methodName, "length is " + length);
                    _log.error(methodName, "Characters are more than 100 ");
                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.C2S_ERROR_MAX_100_INFO7);
                }
            }
            if (!(BTSLUtil.isNullString(info8))) {
                final int length = info8.length();
                if (length > 100) {
                    _log.debug(methodName, "length is " + length);
                    _log.error(methodName, "Characters are more than 100 ");
                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.C2S_ERROR_MAX_100_INFO8);
                }
            }
            if (!(BTSLUtil.isNullString(info9))) {
                final int length = info9.length();
                if (length > 100) {
                    _log.debug(methodName, "length is " + length);
                    _log.error(methodName, "Characters are more than 100 ");
                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.C2S_ERROR_MAX_100_INFO9);
                }
            }
            if (!(BTSLUtil.isNullString(info10))) {
                final int length = info10.length();
                if (length > 100) {
                    _log.debug(methodName, "length is " + length);
                    _log.error(methodName, "Characters are more than 100 ");
                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.C2S_ERROR_MAX_100_INFO10);
                }
            }

            // END NARENDRA

            /*
             * if(BTSLUtil.isNullString(amount))
             * {
             * _log.error("parseChannelExtPrivateRechargeRequest",
             * "Amount field is null ");
             * throw new BTSLBaseException("POSXMLStringParser",
             * "parseChannelExtPrivateRechargeRequest"
             * ,PretupsErrorCodesI.C2S_ERROR_BLANK_AMOUNT);
             * }
             */

            // EVD EXTNGW LOGINID PASSWORD AMOUNT SELECTOR (6)
            if (BTSLUtil.isNullString(password) && BTSLUtil.isNullString(msisdn)) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_EVD + CHNL_MESSAGE_SEP + extNwCode + CHNL_MESSAGE_SEP + loginId + CHNL_MESSAGE_SEP + amount + CHNL_MESSAGE_SEP + selector + CHNL_MESSAGE_SEP + language1; // /Case
            } else if (BTSLUtil.isNullString(msisdn)) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_EVD + CHNL_MESSAGE_SEP + extNwCode + CHNL_MESSAGE_SEP + loginId + CHNL_MESSAGE_SEP + password + CHNL_MESSAGE_SEP + amount + CHNL_MESSAGE_SEP + selector + CHNL_MESSAGE_SEP + language1; // Case
            } else if (BTSLUtil.isNullString(password)) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_EVD + CHNL_MESSAGE_SEP + extNwCode + CHNL_MESSAGE_SEP + loginId + CHNL_MESSAGE_SEP + msisdn + CHNL_MESSAGE_SEP + amount + CHNL_MESSAGE_SEP + selector + CHNL_MESSAGE_SEP + language1; // Case
                // 8
            } else {
                parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_EVD + CHNL_MESSAGE_SEP + extNwCode + CHNL_MESSAGE_SEP + msisdn + CHNL_MESSAGE_SEP + loginId + CHNL_MESSAGE_SEP + password + CHNL_MESSAGE_SEP + amount + CHNL_MESSAGE_SEP + selector + CHNL_MESSAGE_SEP + language1; // Case
                // 9
            }

            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setReqSelector(selector);
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn);
            p_requestVO.setExternalNetworkCode(extNwCode);
            p_requestVO.setSenderExternalCode(extCode);
            p_requestVO.setSenderLoginID(loginId);
            p_requestVO.setRequestMap(requestHashMap);
            p_requestVO.setExternalReferenceNum(extRefNumber);// To enquire
            // Transaction
            // Status with
            // External
            // Refrence Number

            // Start added by Narendra
            p_requestVO.setInfo1(info1);
            p_requestVO.setInfo2(info2);
            p_requestVO.setInfo3(info3);
            p_requestVO.setInfo4(info4);
            p_requestVO.setInfo5(info5);
            p_requestVO.setInfo6(info6);
            p_requestVO.setInfo7(info7);
            p_requestVO.setInfo8(info8);
            p_requestVO.setInfo9(info9);
            p_requestVO.setInfo10(info10);

            // END NARENDRA

        } catch (BTSLBaseException be) {
            _log.error(methodName, " BTSL Exception while parsing Request Message :" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("POSXMLStringParser", "parseChannelExtPrivateRechargeRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    public static void parseChannelExtPPBEnqRequest(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("parseChannelExtPPBEnqRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        final String METHOD_NAME = "parseChannelExtPPBEnqRequest";
        try {
            final HashMap requestHashMap = new HashMap();
            final String[] newMessageArray = null;
            final String requestStr = p_requestVO.getRequestMessage();
            String msisdn = null, msisdn2 = null;
            String lang1 = null;
            String extRefNumber = null;
            String password = null;
            String extCode = null;
            final String date = null;
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            requestHashMap.put("TYPE", type);
            index = requestStr.indexOf("<EXTNWCODE>");
            final String extNwCode = requestStr.substring(index + "<EXTNWCODE>".length(), requestStr.indexOf("</EXTNWCODE>", index));
            requestHashMap.put("EXTNWCODE", extNwCode);
            index = requestStr.indexOf("<MSISDN>");

            msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
            requestHashMap.put("MSISDN", msisdn);

            index = requestStr.indexOf("<MSISDN2>");

            msisdn2 = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));
            requestHashMap.put("MSISDN2", msisdn2);

            index = requestStr.indexOf("<PIN>");
            String pin = null;

            pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            requestHashMap.put("PIN", pin);

            index = requestStr.indexOf("<LOGINID>");
            String loginId = null;

            loginId = requestStr.substring(index + "<LOGINID>".length(), requestStr.indexOf("</LOGINID>", index));
            requestHashMap.put("LOGINID", loginId);

            index = requestStr.indexOf("<PASSWORD>");

            password = requestStr.substring(index + "<PASSWORD>".length(), requestStr.indexOf("</PASSWORD>", index));
            requestHashMap.put("PASSWORD", password);

            index = requestStr.indexOf("<EXTCODE>");

            extCode = requestStr.substring(index + "<EXTCODE>".length(), requestStr.indexOf("</EXTCODE>", index));
            requestHashMap.put("EXTCODE", extCode);

            index = requestStr.indexOf("<EXTREFNUM>");

            extRefNumber = requestStr.substring(index + "<EXTREFNUM>".length(), requestStr.indexOf("</EXTREFNUM>", index));
            requestHashMap.put("EXTREFNUM", extRefNumber);

            index = requestStr.indexOf("<LANGUAGE1>");

            lang1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
            requestHashMap.put("LANGUAGE1", lang1);

            index = requestStr.indexOf("<LANGUAGE2>");
            String lang2 = null;

            lang2 = requestStr.substring(index + "<LANGUAGE2>".length(), requestStr.indexOf("</LANGUAGE2>", index));
            requestHashMap.put("LANGUAGE2", lang2);

            if (BTSLUtil.isNullString(type) || BTSLUtil.isNullString(extNwCode) || BTSLUtil.isNullString(loginId) || BTSLUtil.isNullString(pin)) {
                throw new BTSLBaseException("POSXMLStringParser", "parseChannelExtPPBEnqRequest", PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }
            if (lang1 != null && lang2 != null) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_PPB_ENQUIRY + CHNL_MESSAGE_SEP + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + lang1 + CHNL_MESSAGE_SEP + lang2; // Case
            } else if (lang1 != null) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_PPB_ENQUIRY + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + lang1;
            } else {
                parsedRequestStr = PretupsI.SERVICE_TYPE_PPB_ENQUIRY + CHNL_MESSAGE_SEP + msisdn2;
            }

            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(lang1));
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            // p_requestVO.setRequestMSISDN(msisdn);
            p_requestVO.setExternalNetworkCode(extNwCode);
            p_requestVO.setSenderExternalCode(extCode);
            p_requestVO.setSenderLoginID(loginId);
            p_requestVO.setRequestMap(requestHashMap);
            p_requestVO.setExternalReferenceNum(extRefNumber);// To enquire
            // Transaction
            // Status with
            // External
            // Refrence Number
        } catch (BTSLBaseException be) {
            _log.error("parseChannelExtPPBEnqRequest", " BTSL Exception while parsing Request Message :" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseChannelExtPPBEnqRequest", "Exception e: " + e);
            throw new BTSLBaseException("POSXMLStringParser", "parseChannelExtPPBEnqRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseChannelExtPPBEnqRequest", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * @param p_requestVO
     * @throws Exception
     * @author rahul.dutt
     */
    public static void generateChannelExtPPBENQResponse(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("generateChannelExtPPBENQResponse", "Entered p_requestVO: " + p_requestVO.toString());
        }
        final String METHOD_NAME = "generateChannelExtPPBENQResponse";
        String responseStr = null;
        StringBuffer sbf = null;
        final java.util.Date date = new java.util.Date();
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DDMMYYYYHHMMSS);
            sdf.setLenient(false); // this is required else it will convert
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><COMMAND>");
            sbf.append("<TYPE>EXPBENQRESP</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                String message = p_requestVO.getMessageCode();
                if (message.indexOf("_") != -1) {
                    message = message.substring(0, message.indexOf("_"));
                }
                sbf.append("<TXNSTATUS>").append(message).append("</TXNSTATUS>");
            }

            sbf.append("<DATE>").append(sdf.format(date)).append("</DATE>");
            if (p_requestVO.getRequestMap() != null) {
                sbf.append("<EXTREFNUM>").append((String) p_requestVO.getRequestMap().get("EXTREFNUM")).append("</EXTREFNUM>");
            } else {
                sbf.append("<EXTREFNUM></EXTREFNUM>");
            }

            if (BTSLUtil.isNullString(p_requestVO.getDueAmt())) {
                sbf.append("<TOTALAMT></TOTALAMT>");
            } else {
                sbf.append("<TOTALAMT>").append(p_requestVO.getDueAmt()).append("</TOTALAMT>");
            }
            if (BTSLUtil.isNullString(p_requestVO.getMinAmtDue())) {
                sbf.append("<MINAMT></MINAMT>");
            } else {
                sbf.append("<MINAMT>").append(p_requestVO.getMinAmtDue()).append("</MINAMT>");
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("generateChannelExtPPBENQResponse", "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "POSXMLStringParser[generateChannelExtPPBENQResponse]",
                PretupsErrorCodesI.C2S_ERROR_EXCEPTION, "", "", "generateChannelExtPPBENQResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("generateChannelExtPPBENQResponse", "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * Response method for private Recharge through POS
     * 
     * @param p_requestVO
     * @throws Exception
     * @author nilesh.kumar
     */
    public static void generateChannelExtPrivateRechargeResponse(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("generateChannelExtPrivateRechargeResponse", "Entered p_requestVO: " + p_requestVO.toString());
        }
        final String METHOD_NAME = "generateChannelExtPrivateRechargeResponse";
        String responseStr = null;
        StringBuffer sbf = null;
        final java.util.Date date = new java.util.Date();
        final DESEVDEncryption en = new DESEVDEncryption();
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DDMMYYYYHHMMSS);
            sdf.setLenient(false); // this is required else it will convert
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><COMMAND>");
            sbf.append("<TYPE>EXPVEVDRESP</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                String message = p_requestVO.getMessageCode();
                if (message.indexOf("_") != -1) {
                    message = message.substring(0, message.indexOf("_"));
                }
                sbf.append("<TXNSTATUS>").append(message).append("</TXNSTATUS>");
            }

            sbf.append("<DATE>").append(sdf.format(date)).append("</DATE>");
            if (p_requestVO.getRequestMap() != null) {
                sbf.append("<EXTREFNUM>").append((String) p_requestVO.getRequestMap().get("EXTREFNUM")).append("</EXTREFNUM>");
            } else {
                sbf.append("<EXTREFNUM></EXTREFNUM>");
            }

            if (BTSLUtil.isNullString(p_requestVO.getTransactionID())) {
                sbf.append("<TXNID></TXNID>");
            } else {
                sbf.append("<TXNID>").append(p_requestVO.getTransactionID()).append("</TXNID>");
            }
            if (!p_requestVO.isSuccessTxn()) {
                sbf.append("<MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments())).append("</MESSAGE>");
            } else {
                // To encrypt the response message for EVD through POS : added
                // by harsh
                // sbf.append("<MESSAGE>"+en.encrypt(getMessage(p_requestVO.getLocale(),PretupsErrorCodesI.C2S_SENDER_SUCCESS_EVD,p_requestVO.getMessageArguments()))+"</MESSAGE>");
                sbf.append("<MESSAGE>").append(getMessage(p_requestVO.getLocale(), PretupsErrorCodesI.C2S_SENDER_SUCCESS_EVD, p_requestVO.getMessageArguments())).append("</MESSAGE>");
                // sbf.append("<MESSAGE>"+getMessage(p_requestVO.getLocale(),PretupsErrorCodesI.C2S_EVD_SUCCESS,p_requestVO.getMessageArguments())+"</MESSAGE>");
            }

            // sbf.append("<MESSAGE></MESSAGE>");
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("generateChannelExtPrivateRechargeResponse", "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "POSXMLStringParser[generateChannelExtPrivateRechargeResponse]", PretupsErrorCodesI.C2S_ERROR_EXCEPTION, "", "",
                "generateChannelExtPrivateRechargeResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("generateChannelExtPrivateRechargeResponse", "Exiting responseStr: " + responseStr);
            }
        }
    }
}
