/*
 * @(#)BLUSSDStringParser.java
 * Copyright(c) 2009, Comviva technologies
 * All Rights Reserved
 * ------------------------------------------------------------------------------
 * -------------------
 * Author Date History
 * ------------------------------------------------------------------------------
 * -------------------
 * Vipan Kumar 19/06/14 Initial creation
 * ------------------------------------------------------------------------------
 * -------------------
 * Parse the request of USSD system, Request should be in String format
 */
package com.btsl.pretups.gateway.util.clientutil;

import java.net.URLDecoder;
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
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.ParserUtility;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class BLUSSDStringParser {

    private static String chnl_message_sep = null;
    private static String p2p_message_sep = null;
    public static final String TYPE_STR = "TYPE";
    public static final String TXNID_STR = "TXNID";
    public static final String TXNSTATUS_STR = "TXNSTATUS";
    public static final String MSISDN1_STR = "MSISDN1";
    public static final String PIN_STR = "PIN";
    public static final String MSISDN2_STR = "MSISDN2";
    public static final String SELECTOR_STR = "SELECTOR";
    public static final String AMOUNT_STR = "AMOUNT";
    public static final String LANG1_STR = "LANG1";
    public static final String LANG2_STR = "LANG2";
    public static final String CELLID_STR = "CELLID";
    public static final String SWITCHID_STR = "SWITCHID";
    public static final String USSD_SESSION_ID = "USSDSESSIONID";
    public static final String EXTNWCODE_STR = "EXTNWCODE";
    public static final String DATE_STR="DATE";	
	public static final String USERLOGINID_STR="USERLOGINID";
	public static final String USERPASSWORD_STR="USERPASSWORD";
	public static final String EXTREFNUM_STR="EXTREFNUM";
    public static final Log _log = LogFactory.getLog(BLUSSDStringParser.class.getName());
    private final static String _blank = "";
    static {
        try {
            chnl_message_sep = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
            if (BTSLUtil.isNullString(chnl_message_sep)) {
                chnl_message_sep = " ";
            }
            p2p_message_sep = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_PLAIN_SMS_SEPARATOR));
            if (BTSLUtil.isNullString(p2p_message_sep)) {
                p2p_message_sep = " ";
            }
        } catch (Exception e) {
            _log.errorTrace("static", e);
        }
    }

    /**
   	 * ensures no instantiation
   	 */
    private BLUSSDStringParser(){
    	
    }
    
    public static void parseChannelCreditTransferRequest(RequestVO p_requestVO) throws Exception {
        final String methodName = "parseChannelCreditTransferRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        String cellId = null;
        String switchId = null;
        final boolean tagsMandatory = false;
        String externalNetworkCode = null;
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            final HashMap requestMap = BTSLUtil.getStringToHash(requestStr, "&", "=");
            final String type = (String) requestMap.get("TYPE");
            String selector = (String) requestMap.get("SELECTOR");
            final String msisdn2 = (String) requestMap.get("MSISDN2");

            final String msisdn1 = (String) requestMap.get("MSISDN1");
            final String amount = (String) requestMap.get("AMOUNT");
            final String language1 = (String) requestMap.get(LANG1_STR);
            final String language2 = (String) requestMap.get(LANG2_STR);
            final String ussdSessionID = (String) requestMap.get(USSD_SESSION_ID);

            final String pin = (String) requestMap.get("PIN");
            cellId = (String) requestMap.get(CELLID_STR);
            switchId = (String) requestMap.get(SWITCHID_STR);

            if (BTSLUtil.isNullString(type) || BTSLUtil.isNullString(msisdn1) || BTSLUtil.isNullString(pin) || BTSLUtil.isNullString(msisdn2) || BTSLUtil.isNullString(amount) || BTSLUtil
                .isNullString(selector)) {
                throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }
            if (BTSLUtil.isNullString(selector)) {
                final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(PretupsI.SERVICE_TYPE_CHNL_RECHARGE);
                if (serviceSelectorMappingVO != null) {
                    selector = serviceSelectorMappingVO.getSelectorCode();
                }
            }
            // if we have to forcibaly change the value of default selecter in
            // case of ussd request.
            if (!BTSLUtil.isNullString(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEF_FRCXML_SEL_C2S)))) {
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "using default force selecter of XML");
                }
                selector = "" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEF_FRCXML_SEL_C2S));
            }
            if (!BTSLUtil.isNullString(p_requestVO.getMessageGatewayVO().getGatewayCode()) && ("EXTGW".equals(p_requestVO.getMessageGatewayVO().getGatewayCode()))) {
                externalNetworkCode = (String) requestMap.get(EXTNWCODE_STR);
                if (BTSLUtil.isNullString(externalNetworkCode)) {
                    throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
                }
            }

            if (tagsMandatory) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
                    }
                } catch (BTSLBaseException btsle) {
                    throw btsle;
                }
            }

            parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_RECHARGE + chnl_message_sep + msisdn2 + chnl_message_sep + amount + chnl_message_sep + selector + chnl_message_sep + language1 + chnl_message_sep + language2 + chnl_message_sep + pin;

            parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_RECHARGE + chnl_message_sep + msisdn2 + chnl_message_sep + amount + chnl_message_sep + selector + chnl_message_sep + "1" + chnl_message_sep + pin;

            p_requestVO.setCellId(cellId);
            p_requestVO.setSwitchId(switchId);
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setReqSelector(selector);
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    public static void generateChannelCreditTransferResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateChannelCreditTransferResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            if (p_requestVO.isSuccessTxn()) {
                sbf.append(getMessage(p_requestVO.getLocale(), "UNDERPROCESS", p_requestVO.getMessageArguments()));
            } else {
                sbf.append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            }
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error("generateCreditTransferResponse", "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "BLUSSDStringParser[generateChannelCreditTransferResponse]", PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateChannelCreditTransferResponse:" + e
                    .getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    public static void parseChannelChangePinRequest(RequestVO p_requestVO) throws Exception {
        final String methodName = "parseChannelChangePinRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        String cellId = null;
        String switchId = null;
        boolean tagsMandatory;
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            final HashMap requestMap = BTSLUtil.getStringToHash(requestStr, "&", "=");
            final String type = (String) requestMap.get("TYPE");
            final String msisdn1 = (String) requestMap.get("MSISDN1");
            final String language1 = (String) requestMap.get("LANGUAGE1");
            final String pin = (String) requestMap.get("PIN");
            final String newPin = (String) requestMap.get("NEWPIN");
            final String confirmPin = (String) requestMap.get("CONFIRMPIN");

            // added for ussd changes

            cellId = (String) requestMap.get(CELLID_STR);

            switchId = (String) requestMap.get(SWITCHID_STR);

            tagsMandatory = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY))).booleanValue();
            if (tagsMandatory) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
                    }
                } catch (BTSLBaseException btsle) {
                    throw btsle;
                }
            }
            if (BTSLUtil.isNullString(type) || BTSLUtil.isNullString(msisdn1) || BTSLUtil.isNullString(pin) || BTSLUtil.isNullString(newPin) || BTSLUtil
                .isNullString(confirmPin)) {
                throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }

            parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_CHANGEPIN + chnl_message_sep + pin + chnl_message_sep + newPin + chnl_message_sep + confirmPin;
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
            p_requestVO.setCellId(cellId);
            p_requestVO.setSwitchId(switchId);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    public static void generateChannelChangePinResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateChannelChangePinResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "BLUSSDStringParser[generateChannelChangePinResponse]", PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateChannelChangePinResponse:" + e
                    .getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    public static void parseChannelNotificationLanguageRequest(RequestVO p_requestVO) throws Exception {
        final String methodName = "parseChannelNotificationLanguageRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        boolean tagsMandatory;
        String cellId = null;
        String switchId = null;
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            final HashMap requestMap = BTSLUtil.getStringToHash(requestStr, "&", "=");
            final String type = (String) requestMap.get("TYPE");
            final String msisdn1 = (String) requestMap.get("MSISDN1");
            final String language1 = (String) requestMap.get("LANGUAGE1");
            final String pin = (String) requestMap.get("PIN");
            cellId = (String) requestMap.get(CELLID_STR);

            switchId = (String) requestMap.get(SWITCHID_STR);
            tagsMandatory = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY))).booleanValue();
            if (tagsMandatory) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
                    }
                } catch (BTSLBaseException btsle) {
                    throw btsle;
                }
            }

            if (BTSLUtil.isNullString(type) || BTSLUtil.isNullString(msisdn1) || BTSLUtil.isNullString(pin) || BTSLUtil.isNullString(language1)) {
                throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }

            parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_LANG_NOTIFICATION + chnl_message_sep + language1 + chnl_message_sep + pin;
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
            // added for ussd changes
            p_requestVO.setCellId(cellId);
            p_requestVO.setSwitchId(switchId);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    public static void generateChannelNotificationLanguageResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateChannelNotificationLanguageResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestID: " + p_requestVO.getRequestIDStr());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "BLUSSDStringParser[generateChannelNotificationLanguageResponse]", PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "",
                "generateChannelNotificationLanguageResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    public static void generateChannelTransferResponse(RequestVO p_requestVO, int p_action) throws Exception {
        final String methodName = "generateChannelTransferResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "BLUSSDStringParser[generateChannelTransferResponse]", PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateChannelTransferResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * This method generates Balance Enquiry Response
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void generateChannelBalanceEnquiryResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateChannelBalanceEnquiryResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "BLUSSDStringParser[generateChannelBalanceEnquiryResponse]", PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateChannelBalanceEnquiryResponse:" + e
                    .getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * This method generates Daily Status Report Response in XML format from
     * requestVO
     * 
     * @param p_requestVO
     * @throws Exception
     * @author sanjeew.kumar
     * @Date 03/05/07
     */
    public static void generateChannelDailyStatusReportResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateChannelDailyStatusReportResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);

            sbf.append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "BLUSSDStringParser[generateChannelDailyStatusReportResponse]", PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "",
                "generateChannelDailyStatusReportResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * This method generates Last Transfer Status Report Response in XML format
     * from requestVO
     * 
     * @param p_requestVO
     * @throws Exception
     * @author sanjeew.kumar
     * @Date 03/05/07
     */
    public static void generateChannelLastTransferStatusResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateChannelLastTransferStatusResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "BLUSSDStringParser[generateChannelLastTransferStatusResponse]", PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "",
                "generateChannelLastTransferStatusResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * Generate Fail message
     * 
     * @param p_requestVO
     * @throws Exception
     */
    public static void generateFailureResponse(RequestVO p_requestVO) {
        final String methodName = "generateFailureResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered :p_requestVO " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.error(methodName, "Exception e: " + e);
            _log.errorTrace(methodName, e);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    public static void parseChannelTransferRequest(RequestVO p_requestVO, int p_action) throws Exception {
        final String methodName = "parseChannelTransferRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString() + " p_action=" + p_action);
        }
        String parsedRequestStr = null;
        boolean tagsMandatory;
        String cellId = null;
        String switchId = null;
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            final HashMap requestMap = BTSLUtil.getStringToHash(requestStr, "&", "=");
            final String type = (String) requestMap.get("TYPE");
            final String amount = (String) requestMap.get("TOPUPVALUE");
            final String msisdn2 = (String) requestMap.get("MSISDN2");

            final String msisdn1 = (String) requestMap.get("MSISDN1");
            final String language1 = (String) requestMap.get("LANGUAGE1");
            String productCode = (String) requestMap.get("PRODUCTCODE");
            final String pin = (String) requestMap.get("PIN");
            cellId = (String) requestMap.get(CELLID_STR);
            switchId = (String) requestMap.get(SWITCHID_STR);

            if (BTSLUtil.isNullString(type) || BTSLUtil.isNullString(msisdn1) || BTSLUtil.isNullString(pin) || BTSLUtil.isNullString(msisdn2) || BTSLUtil.isNullString(amount)) {
                throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }

            if (BTSLUtil.isNullString(productCode)) {
                productCode = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_PRODUCT));
            }
            // finding the transfer ,retuen and withdraw
            if (p_action == ParserUtility.ACTION_CHNL_TRANSFER_MESSAGE) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_TRANSFER + chnl_message_sep + msisdn2 + chnl_message_sep + amount + chnl_message_sep + productCode + chnl_message_sep + pin;
            } else if (p_action == ParserUtility.ACTION_CHNL_RETURN_MESSAGE) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_RETURN + chnl_message_sep + msisdn2 + chnl_message_sep + amount + chnl_message_sep + productCode + chnl_message_sep + pin;
            } else if (p_action == ParserUtility.ACTION_CHNL_WITHDRAW_MESSAGE) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_WITHDRAW + chnl_message_sep + msisdn2 + chnl_message_sep + amount + chnl_message_sep + productCode + chnl_message_sep + pin;
            }
            tagsMandatory = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY))).booleanValue();
            if (tagsMandatory) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
                    }
                } catch (BTSLBaseException btsle) {
                    throw btsle;
                }
            }
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
            tagsMandatory = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY))).booleanValue();
            p_requestVO.setCellId(cellId);
            p_requestVO.setSwitchId(switchId);

        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO ID: " + p_requestVO.getRequestIDStr());
            }
        }
    }

    /**
     * This method generates Balance Enquiry Request from XML String and
     * formating into white space seperated String
     * 
     * @param p_requestVO
     *            RequestVO
     * @author sanjeew.kumar
     * @Date 03/05/07
     */
    public static void parseChannelBalanceEnquiry(RequestVO p_requestVO) throws Exception {
        final String methodName = "parseChannelBalanceEnquiry";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        boolean tagsMandatory;
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            final HashMap requestMap = BTSLUtil.getStringToHash(requestStr, "&", "=");
            final String type = (String) requestMap.get("TYPE");
            final String msisdn1 = (String) requestMap.get("MSISDN1");
            final String pin = (String) requestMap.get("PIN");
            final String msisdn2 = (String) requestMap.get("MSISDN2");
            final String cellId = (String) requestMap.get(CELLID_STR);

            final String switchId = (String) requestMap.get(SWITCHID_STR);
            tagsMandatory = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY))).booleanValue();
            if (tagsMandatory) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
                    }
                } catch (BTSLBaseException btsle) {
                    throw btsle;
                }
            }

            if (BTSLUtil.isNullString(type) || BTSLUtil.isNullString(msisdn1) || BTSLUtil.isNullString(pin)) {
                throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }

            if (!BTSLUtil.isNullString(msisdn2)) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_BALANCE_ENQUIRY + chnl_message_sep + msisdn2 + chnl_message_sep + pin;
            } else {
                parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_BALANCE_ENQUIRY + chnl_message_sep + pin;
            }
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
            p_requestVO.setCellId(cellId);
            p_requestVO.setSwitchId(switchId);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * @param p_requestVO
     *            RequestVO
     */
    public static void parseChannelDailyStatusReport(RequestVO p_requestVO) throws Exception {
        final String methodName = "parseChannelDailyStatusReport";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        boolean tagsMandatory;
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            final HashMap requestMap = BTSLUtil.getStringToHash(requestStr, "&", "=");
            final String type = (String) requestMap.get("TYPE");
            final String msisdn1 = (String) requestMap.get("MSISDN1");
            final String pin = (String) requestMap.get("PIN");
            final String cellId = (String) requestMap.get(CELLID_STR);

            final String switchId = (String) requestMap.get(SWITCHID_STR);
            tagsMandatory = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY))).booleanValue();
            if (tagsMandatory) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
                    }
                } catch (BTSLBaseException btsle) {
                    throw btsle;
                }
            }
            if (BTSLUtil.isNullString(type) || BTSLUtil.isNullString(msisdn1) || BTSLUtil.isNullString(pin)) {
                throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }

            parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_DAILY_STATUS_REPORT + chnl_message_sep + pin;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
            p_requestVO.setCellId(cellId);
            p_requestVO.setSwitchId(switchId);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method generates Daily Last Transfer Status Request from XML String
     * and formating into white space seperated String
     * 
     * @param p_requestVO
     *            RequestVO
     * @author sanjeew.kumar
     * @Date 03/05/07
     */
    public static void parseChannelLastTransferStatus(RequestVO p_requestVO) throws Exception {
        final String methodName = "parseChannelLastTransferStatus";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        boolean tagsMandatory;
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            final HashMap requestMap = BTSLUtil.getStringToHash(requestStr, "&", "=");
            final String type = (String) requestMap.get("TYPE");
            final String msisdn1 = (String) requestMap.get("MSISDN1");
            final String pin = (String) requestMap.get("PIN");
            final String cellId = (String) requestMap.get(CELLID_STR);

            final String switchId = (String) requestMap.get(SWITCHID_STR);
            tagsMandatory = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY))).booleanValue();
            if (tagsMandatory) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
                    }
                } catch (BTSLBaseException btsle) {
                    throw btsle;
                }
            }

            if (BTSLUtil.isNullString(type) || BTSLUtil.isNullString(msisdn1) || BTSLUtil.isNullString(pin)) {
                throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }

            parsedRequestStr = PretupsI.SERVICE_TYPE_LAST_TRANSFER_STATUS + chnl_message_sep + pin;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
            p_requestVO.setCellId(cellId);
            p_requestVO.setSwitchId(switchId);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * @author vikram.kumar
     *         Added for parse C2S Last N Transfer report.
     * @param p_requestVO
     * @param p_action
     * @throws Exception
     */
    public static void parseChannelLastXTransferStatus(RequestVO p_requestVO) throws Exception {
        final String methodName = "parseChannelLastXTransferStatus";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            final HashMap requestMap = BTSLUtil.getStringToHash(requestStr, "&", "=");
            final String type = (String) requestMap.get("TYPE");
            final String msisdn1 = (String) requestMap.get("MSISDN1");
            final String pin = (String) requestMap.get("PIN");

            if (BTSLUtil.isNullString(type) || BTSLUtil.isNullString(msisdn1) || BTSLUtil.isNullString(pin)) {
                throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }

            parsedRequestStr = PretupsI.SERVICE_TYPE_LASTX_TRANSFER_REPORT + chnl_message_sep + pin;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method generates Last Transfer Status Report Response in XML format
     * from requestVO
     * 
     * @param p_requestVO
     * @throws Exception
     * @author vikram.kumar
     * @Date 03/05/07
     */
    public static void generateChannelLastXTransferStatusResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateChannelLastXTransferStatusResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "XMLStringParser[generateChannelLastTransferStatusResponse]", PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateChannelLastTransferStatusResponse:" + e
                    .getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * This method generates Last Transfer Status Report Response in XML format
     * from requestVO
     * 
     * @param p_requestVO
     * @throws Exception
     * @author vikram.kumar
     * @Date 03/05/07
     */
    public static void generateChannelXEnquiryStatusResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateChannelLastXTransferStatusResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "XMLStringParser[generateChannelLastTransferStatusResponse]", PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateChannelLastTransferStatusResponse:" + e
                    .getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * @author vikram.kumar
     *         Added for parse C2S Last N Transfer report.
     * @param p_requestVO
     * @param p_action
     * @throws Exception
     */
    public static void parseChannelXEnquiryStatus(RequestVO p_requestVO) throws Exception {
        final String methodName = "parseChannelLastXTransferStatus";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            final HashMap requestMap = BTSLUtil.getStringToHash(requestStr, "&", "=");
            final String type = (String) requestMap.get("TYPE");
            final String msisdn1 = (String) requestMap.get("MSISDN1");
            final String pin = (String) requestMap.get("PIN");
            final String msisdn2 = (String) requestMap.get("MSISDN2");

            if (BTSLUtil.isNullString(type) || BTSLUtil.isNullString(msisdn1) || BTSLUtil.isNullString(pin) || BTSLUtil.isNullString(msisdn2)) {
                throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }
            parsedRequestStr = PretupsI.SERVICE_TYPE_CUSTX_ENQUIRY + chnl_message_sep + msisdn2 + chnl_message_sep + pin;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);

        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    private static String getMessage(Locale locale, String key, String[] p_args1) {
        final String methodName = "getMessage";
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
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
        }
        return message;
    }

    /**
     * To parse the MVD voucher download request.
     * 
     * @param p_requestVO
     * @throws Exception
     * @author Ashish T
     */
    public static void parseChannelMVDVoucherDownloadRequest(RequestVO p_requestVO) throws Exception {
        final String methodName = "parseChannelMVDVoucherDownloadRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            final HashMap requestMap = BTSLUtil.getStringToHash(requestStr, "&", "=");
            final String type = (String) requestMap.get("TYPE");
            final String msisdn1 = (String) requestMap.get("MSISDN1");
            final String pin = (String) requestMap.get("PIN");

            String denominationAmount = (String) requestMap.get("AMOUNT");
            if (BTSLUtil.isNullString(denominationAmount)) {
                denominationAmount = "0";
            }

            String requestedQuantity = (String) requestMap.get("QUANTITY");
            if (BTSLUtil.isNullString(requestedQuantity)) {
                requestedQuantity = "2";
            }

            String language1 = (String) requestMap.get("LANGUAGE1");
            if (BTSLUtil.isNullString(language1)) {
                language1 = Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
            }
            if (BTSLUtil.isNullString(language1)) {
                language1 = "0";
            }
            String language2 = (String) requestMap.get("LANGUAGE2");
            if (BTSLUtil.isNullString(language2)) {
                language2 = Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
            }
            if (BTSLUtil.isNullString(language2)) {
                language2 = "0";
            }
            if (BTSLUtil.isNullString(type) || BTSLUtil.isNullString(msisdn1) || BTSLUtil.isNullString(pin) || BTSLUtil.isNullString(denominationAmount) || BTSLUtil
                .isNullString(requestedQuantity)) {
                throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }
            parsedRequestStr = PretupsI.MVD_VOUCHER_DOWNLOAD + chnl_message_sep + msisdn1 + chnl_message_sep + denominationAmount + chnl_message_sep + requestedQuantity + chnl_message_sep + pin;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);

        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseChannelLastXTransferStatus", "Exception e: " + e);
            throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * Parse User Creation request.
     * 
     * @param p_requestVO
     * @throws Exception
     */
    public static void parsePlainUserCreationRequest(RequestVO p_requestVO) throws Exception {
        final String methodName = "parsePlainUserCreationRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            final HashMap requestMap = BTSLUtil.getStringToHash(requestStr, "&", "=");
            final String type = (String) requestMap.get("TYPE");
            final String msisdn1 = (String) requestMap.get("MSISDN1");
            final String userMsisdn = (String) requestMap.get("USERMSISDN");
            String userName = (String) requestMap.get("USERNAME"); // Optinal.
            if (BTSLUtil.isNullString(userName)) {
                userName = userMsisdn;
            }
            String loginId = (String) requestMap.get("LOGINID"); // Optional.
            if (BTSLUtil.isNullString(loginId)) {
                loginId = userMsisdn;
            }
            final String catCode = (String) requestMap.get("CATCODE");
            final String parentMsisdn = (String) requestMap.get("PARENTMSISDN");
            final String pin = (String) requestMap.get("PIN");
            final String empCode = (String) requestMap.get("EMPCODE");// Optional.
            final String ssn = (String) requestMap.get("SSN"); // Optional.
            final String extCode = (String) requestMap.get("EXTCODE"); // Optional.

            if (BTSLUtil.isNullString(type) || BTSLUtil.isNullString(msisdn1) || BTSLUtil.isNullString(userMsisdn) || BTSLUtil.isNullString(catCode) || (BTSLUtil
                .isNullString(parentMsisdn) || (BTSLUtil.isNullString(pin)))) {
                throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }
            parsedRequestStr = PretupsI.ADD_CHNL_USER + chnl_message_sep + userMsisdn + chnl_message_sep + userName + chnl_message_sep + loginId + chnl_message_sep + catCode + chnl_message_sep + parentMsisdn + chnl_message_sep + pin;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);

        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseChannelLastXTransferStatus", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * Method User Creation Response.
     * 
     * @param p_requestVO
     * @throws Exception
     */
    public static void generatePlainUserCreationResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generatePlainUserCreationResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "XMLStringParser[generateChannelLastTransferStatusResponse]", PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateChannelLastTransferStatusResponse:" + e
                    .getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * Parse User Creation request.
     * 
     * @param p_requestVO
     * @throws Exception
     */
    public static void parseChannelExtRechargeStatusRequest(RequestVO p_requestVO) throws Exception {
        final String methodName = "parsePlainUserCreationRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        final String MSG_IDNTFICATION_TXN_ID = "TXN";
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            final HashMap requestMap = BTSLUtil.getStringToHash(requestStr, "&", "=");
            final String type = (String) requestMap.get("TYPE");
            requestMap.put("TYPE", type);
            final String date = (String) requestMap.get("DATE");
            requestMap.put("DATE", date);
            final String extNwCode = (String) requestMap.get("EXTNWCODE");
            requestMap.put("EXTNWCODE", extNwCode);
            final String msisdn = (String) requestMap.get("MSISDN");
            requestMap.put("MSISDN", msisdn);
            final String pin = (String) requestMap.get("PIN");
            requestMap.put("PIN", pin);
            final String loginId = (String) requestMap.get("LOGINID"); // Optinal.
            requestMap.put("LOGINID", loginId);
            final String password = (String) requestMap.get("PASSWORD"); // Optional.
            requestMap.put("PASSWORD", password);
            final String extCode = (String) requestMap.get("EXTCODE");
            requestMap.put("EXTCODE", extCode);
            final String txnID = (String) requestMap.get("TXNID");
            requestMap.put("TXNID", txnID);
            final String language = (String) requestMap.get("LANGUAGE");

            parsedRequestStr = PretupsI.ENQ_TXN_ID + chnl_message_sep + txnID + chnl_message_sep + pin;
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language));
            p_requestVO.setRequestMSISDN(msisdn);
            p_requestVO.setExternalNetworkCode(extNwCode);
            p_requestVO.setSenderExternalCode(extCode);
            p_requestVO.setSenderLoginID(loginId);
            p_requestVO.setRequestMap(requestMap);

            if (!BTSLUtil.isNullString(txnID)) {
                parsedRequestStr = PretupsI.ENQ_TXN_ID + chnl_message_sep + txnID + chnl_message_sep + MSG_IDNTFICATION_TXN_ID + chnl_message_sep + pin;
            }

            p_requestVO.setDecryptedMessage(parsedRequestStr);

        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseChannelExtRechargeStatusRequest", "Exception e: " + e);
            throw new BTSLBaseException("BLUSSDStringParser", "parseChannelExtRechargeStatusRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseChannelExtRechargeStatusRequest", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * Method User Creation Response.
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
        try {
            sbf = new StringBuffer(1024);
            sbf.append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "BLUSSDStringParser[generateChannelExtRechargeStatusResponse]", PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "",
                "generateChannelExtRechargeStatusResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * parseP2PGiveMeBalanceRequest
     * Request of Balance from the external system.
     * 
     * @param p_requestVO
     * @throws Exception
     * @author mukesh.singh
     * @Date 25/02/10
     */

    public static void parseP2PGiveMeBalanceRequest(RequestVO p_requestVO) throws Exception {
        final String methodName = "parseP2PGiveMeBalanceRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        String cellId = null;
        String switchId = null;
        boolean tagsMandatory;
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            final HashMap requestMap = BTSLUtil.getStringToHash(requestStr, "&", "=");

            final String type = (String) requestMap.get(TYPE_STR);
            final String msisdn1 = (String) requestMap.get(MSISDN1_STR);
            String msisdn2 = (String) requestMap.get(MSISDN2_STR);
            if (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.COUNTRY_CODE)) != null) {
                // append the country code before the msisdn for give me balance
                // //rahul
                msisdn2 = BTSLUtil.addCountryCodeToMSISDN(msisdn2);
            }
            final String amount = (String) requestMap.get(AMOUNT_STR);
            // amount field should be mandatory
            if (BTSLUtil.isNullString(amount)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_BLANK_AMOUNT);
                _log.error(methodName, "Amount field is null");
                throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.P2P_ERROR_BLANK_AMOUNT);
            }

            String language1 = (String) requestMap.get(LANG1_STR);
            String language2 = (String) requestMap.get(LANG2_STR);
            final String ussdSessionID = (String) requestMap.get(USSD_SESSION_ID);

            cellId = (String) requestMap.get(CELLID_STR);
            switchId = (String) requestMap.get(SWITCHID_STR);
            tagsMandatory = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY))).booleanValue();
            if (tagsMandatory) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        p_requestVO.setMessageCode(PretupsErrorCodesI.USSD_CELLID_BLANK_ERROR);
                        throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.USSD_CELLID_BLANK_ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        p_requestVO.setMessageCode(PretupsErrorCodesI.USSD_SWITCHID_BLANK_ERROR);
                        throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.USSD_SWITCHID_BLANK_ERROR);
                    }
                } catch (BTSLBaseException btsle) {
                    throw btsle;
                }
            }

            if (BTSLUtil.isNullString(language1)) {
                language1 = Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
            }
            if (BTSLUtil.isNullString(language1)) {
                language1 = "0";
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "language1:" + language1 + ":");
            }

            if (BTSLUtil.isNullString(language2)) {
                language2 = Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
            }
            if (BTSLUtil.isNullString(language2)) {
                language2 = "0";
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "language2:" + language2 + ":");
            }

            p_requestVO.setRequestMap(requestMap);
            if (BTSLUtil.isNullString(type)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.P2PGMB_TYPE_BLANK);
                throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.P2PGMB_TYPE_BLANK);
            }
            if (BTSLUtil.isNullString(msisdn1)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.P2PGMB_MSISDN1_BLANK);
                throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.P2PGMB_MSISDN1_BLANK);
            }
            if (BTSLUtil.isNullString(msisdn2)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.P2PGMB_MSISDN2_BLANK);
                throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.P2PGMB_MSISDN2_BLANK);
            }
            if (!BTSLUtil.isNullString(msisdn1) && !BTSLUtil.isNullString(msisdn2) && msisdn1.equals(msisdn2)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.P2PGMB_MSISDN1_MSISDN2_EQUAL);
                throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.P2PGMB_MSISDN1_MSISDN2_EQUAL);
            }
            parsedRequestStr = PretupsI.SERVICE_TYPE_GIVE_ME_BALANCE + p2p_message_sep + msisdn2 + p2p_message_sep + amount;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
            p_requestVO.setUssdSessionID(ussdSessionID);
            p_requestVO.setCellId(cellId);
            p_requestVO.setSwitchId(switchId);
        } catch (BTSLBaseException be) {
            _log.error(methodName, " BTSL Exception while parsing Request Message :" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2PGMB_INVALID_MESSAGE);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.P2PGMB_INVALID_MESSAGE);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO ID: " + p_requestVO.getRequestIDStr());
            }
        }
    }

    /**
     * Method to generate Give Me Balance response for USSD
     * 
     * @param p_requestVO
     * @throws Exception
     */

    public static void generateP2PGiveMeBalanceResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateP2PGiveMeBalanceResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append(TYPE_STR + "=CGMBALRESP");

            if (BTSLUtil.isNullString(p_requestVO.getTransactionID())) {
                sbf.append("&" + TXNID_STR + "=" + _blank);
            } else {
                sbf.append("&" + TXNID_STR + "=" + p_requestVO.getTransactionID());
            }
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("&" + TXNSTATUS_STR + "=" + PretupsI.TXN_STATUS_SUCCESS);
            } else {
                sbf.append("&" + TXNSTATUS_STR + "=" + p_requestVO.getMessageCode());
            }

            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
            p_requestVO.setSenderMessageRequired(false);

            final String msisdn1 = p_requestVO.getRequestMap().get(MSISDN1_STR).toString();
            p_requestVO.setRequestMSISDN(msisdn1);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "BLUSSDStringParser[generateP2PGiveMeBalanceResponse]", PretupsErrorCodesI.P2P_ERROR_EXCEPTION, "", "", "generateP2PGiveMeBalanceResponse:" + e
                    .getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * this method parse credit transfer request from XML String formating into
     * white space seperated String
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void parseCreditTransferRequest(RequestVO p_requestVO) throws BTSLBaseException, Exception {
        final String methodName = "parseCreditTransferRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        boolean tagsMandatory;
        String cellId = null;
        String switchId = null;
        try {

            final String requestStr = p_requestVO.getRequestMessage();
            final HashMap requestMap = BTSLUtil.getStringToHash(requestStr, "&", "=");

            final String msisdn1 = (String) requestMap.get(MSISDN1_STR);

            String pin = (String) requestMap.get(PIN_STR);

            final String msisdn2 = (String) requestMap.get(MSISDN2_STR);

            final String amount = (String) requestMap.get(AMOUNT_STR);

            String language1 = (String) requestMap.get(LANG1_STR);

            String language2 = (String) requestMap.get(LANG2_STR);

            String selector = (String) requestMap.get(SELECTOR_STR);

            cellId = (String) requestMap.get(CELLID_STR);

            switchId = (String) requestMap.get(SWITCHID_STR);

            final String ussdSessionID = (String) requestMap.get(USSD_SESSION_ID);

            if (BTSLUtil.isNullString(msisdn1) || BTSLUtil.isNullString(msisdn2)) {
                throw new BTSLBaseException("BLUSSDStringParser", "parseGetAccountInfoRequest", PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
            }

            // amount field should be mandatory
            if (BTSLUtil.isNullString(amount)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_BLANK_AMOUNT);
                _log.error(methodName, "Amount field is null");
                throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.P2P_ERROR_BLANK_AMOUNT);
            }
            // added by sanjay to set default pin
            if (BTSLUtil.isNullString(pin)) {
                pin = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN));
            }

            // added by sanjay 10/01/2006 - to set default language code
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "language1:" + language1 + ":");
            }

            if (BTSLUtil.isNullString(language1)) {
                language1 = Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
            }

            if (BTSLUtil.isNullString(language1)) {
                language1 = "0";
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "language1:" + language1 + ":");
                // added by sanjay 10/01/2006 - to set default language code
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "language2:" + language2 + ":");
            }

            if (BTSLUtil.isNullString(language2)) {
                language2 = Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
            }

            if (BTSLUtil.isNullString(language2)) {
                language2 = "0";
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "language2:" + language2 + ":");
            }

            if (BTSLUtil.isNullString(selector)) {
                // selector=""+((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_TRANSFER_DEF_SELECTOR_CODE));
                // Changed on 27/05/07 for Service Type selector Mapping
                final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(PretupsI.SERVICE_TYPE_P2PRECHARGE);
                if (serviceSelectorMappingVO != null) {
                    selector = serviceSelectorMappingVO.getSelectorCode();
                }
            }
            // if we have to forcibaly change the value of selecter in case of
            // USSD request.

            if (!BTSLUtil.isNullString(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEF_FRCXML_SEL_P2P)))) {
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "using default force selecter of XML");
                }
                selector = "" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEF_FRCXML_SEL_P2P));
            }
            tagsMandatory = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY))).booleanValue();
            if (tagsMandatory) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.USSD_CELLID_BLANK_ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.USSD_SWITCHID_BLANK_ERROR);
                    }
                } catch (BTSLBaseException btsle) {
                    throw btsle;
                }
            }
            parsedRequestStr = PretupsI.SERVICE_TYPE_P2PRECHARGE + p2p_message_sep + msisdn2 + p2p_message_sep + amount + (p2p_message_sep + selector) + (p2p_message_sep + language2) + p2p_message_sep + pin;
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(language2));
            p_requestVO.setReqSelector(selector);
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
            p_requestVO.setReceiverMsisdn(msisdn2);
            p_requestVO.setCellId(cellId);
            p_requestVO.setSwitchId(switchId);
            // ---added for amount rahul
            p_requestVO.setReqAmount(amount);
            p_requestVO.setUssdSessionID(ussdSessionID);
        } catch (BTSLBaseException be) {
            _log.error(methodName, " BTSL Exception while parsing Request Message :" + be.getMessage());
            throw be;
        } catch (Exception e) {
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * this method construct credit transfer response in XML format from
     * requestVO
     * 
     * @param p_requestVO
     *            RequestVO
     * @return responseStr java.lang.String
     */
    public static void generateCreditTransferResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateCreditTransferResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);

            sbf.append(TYPE_STR + "=CCTRFRESP");
            if (BTSLUtil.isNullString(p_requestVO.getTransactionID())) {
                sbf.append("&" + TXNID_STR + "=" + _blank);
            } else {
                sbf.append("&" + TXNID_STR + "=" + p_requestVO.getTransactionID());
            }
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("&" + TXNSTATUS_STR + "=" + PretupsI.TXN_STATUS_SUCCESS);
            } else {
                sbf.append("&" + TXNSTATUS_STR + "=" + p_requestVO.getMessageCode());
            }

            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "BLUSSDStringParser[generateCreditTransferResponse]", PretupsErrorCodesI.P2P_ERROR_EXCEPTION, "", "", "generateCreditTransferResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    public static void parseChannelHelpDeskRequest(RequestVO p_requestVO) throws Exception {
        final String methodName = "parseChannelHelpDeskRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            final HashMap requestMap = BTSLUtil.getStringToHash(requestStr, "&", "=");
            final String type = (String) requestMap.get("TYPE");
            final String msisdn1 = (String) requestMap.get("MSISDN1");
            final String pin = (String) requestMap.get("PIN");
            final String langauge = (String) requestMap.get("LANGUAGE1");

            if (BTSLUtil.isNullString(type) || BTSLUtil.isNullString(msisdn1) || BTSLUtil.isNullString(pin)) {
                throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }
            parsedRequestStr = PretupsI.SERVICE_TYPE_HLPDSK_REQUEST + chnl_message_sep + msisdn1 + chnl_message_sep + pin;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);

        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseChannelLastXTransferStatus", "Exception e: " + e);
            throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * @author arvinder.singh
     * @param p_requestVO
     * @throws Exception
     */
    public static void generateChannelHelpDeskResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateChannelHelpDeskResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateChannelHelpDeskResponse]",
                PretupsErrorCodesI.C2S_ERROR_EXCEPTION, "", "", "generateChannelHelpDeskResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * @author arvinder.singh
     * @param p_requestVO
     * @throws BTSLBaseException
     * @throws Exception
     */
    public static void parseChannelPostPaidBillPaymentRequest(RequestVO p_requestVO) throws BTSLBaseException, Exception {
        final String methodName = "parseChannelPostPaidBillPaymentRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;

        try {

            final String requestStr = p_requestVO.getRequestMessage();
            final HashMap requestMap = BTSLUtil.getStringToHash(requestStr, "&", "=");

            final String msisdn1 = (String) requestMap.get(MSISDN1_STR);

            final String pin = (String) requestMap.get(PIN_STR);

            final String msisdn2 = (String) requestMap.get(MSISDN2_STR);

            final String amount = (String) requestMap.get(AMOUNT_STR);

            final String language1 = (String) requestMap.get(LANG1_STR);

            final String language2 = (String) requestMap.get(LANG2_STR);

            String selector = (String) requestMap.get(SELECTOR_STR);

            final String ussdSessionID = (String) requestMap.get(USSD_SESSION_ID);

            if (BTSLUtil.isNullString(selector)) {

                final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(PretupsI.SERVICE_TYPE_CHNL_BILLPAY);
                if (serviceSelectorMappingVO != null) {
                    selector = serviceSelectorMappingVO.getSelectorCode();
                }
            }

            if (!BTSLUtil.isNullString(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEF_FRCXML_SEL_BILLPAY)))) {
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "using default force selecter of XML");
                }
                selector = "" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEF_FRCXML_SEL_BILLPAY));
            }

            parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_BILLPAY + chnl_message_sep + msisdn2 + chnl_message_sep + amount + chnl_message_sep + selector + chnl_message_sep + language2 + chnl_message_sep + pin;

            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setReqSelector(selector);
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
            p_requestVO.setUssdSessionID(ussdSessionID);
        }

        catch (Exception e) {
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.BILLPAY_INVALID_MESSAGE_FORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * @author arvinder.singh
     * @param p_requestVO
     * @throws BTSLBaseException
     * @throws Exception
     */
    public static void generateChannelPostPaidBillPaymentResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateChannelPostPaidBillPaymentResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);

            sbf.append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "XMLStringParser[generateChannelPostPaidBillPaymentResponse]", PretupsErrorCodesI.C2S_ERROR_EXCEPTION, "", "",
                "generateChannelPostPaidBillPaymentResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * @author akanksha.gupta
     *         Added for Collection Enquiry report.
     * @param p_requestVO
     * @param p_action
     * @throws Exception
     */
    public static void parseChannelCollectionEnquiryRequest(RequestVO p_requestVO) throws Exception {
        final String methodName = "parseChannelCollectionEnquiryRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        String cellId = null;
        String switchId = null;
        final boolean tagsMandatory = false;
        String externalNetworkCode = null;
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            final HashMap requestMap = BTSLUtil.getStringToHash(requestStr, "&", "=");

            final String type = (String) requestMap.get("TYPE");
            String selector = (String) requestMap.get("SELECTOR");
            final String msisdn2 = (String) requestMap.get("MSISDN2");

            final String msisdn1 = (String) requestMap.get("MSISDN1");
            final String language1 = (String) requestMap.get(LANG1_STR);
            final String language2 = (String) requestMap.get(LANG2_STR);

            final String ussdSessionID = (String) requestMap.get(USSD_SESSION_ID);
            final String pin = (String) requestMap.get("PIN");
            cellId = (String) requestMap.get(CELLID_STR);
            switchId = (String) requestMap.get(SWITCHID_STR);

            if (BTSLUtil.isNullString(type) || BTSLUtil.isNullString(msisdn1) || BTSLUtil.isNullString(pin) || BTSLUtil.isNullString(msisdn2) || BTSLUtil
                .isNullString(selector)) {
                throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }
            if (BTSLUtil.isNullString(selector)) {
                final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(PretupsI.COLLECTION_ENQUIRY);
                if (serviceSelectorMappingVO != null) {
                    selector = serviceSelectorMappingVO.getSelectorCode();
                }
            }
            if (!BTSLUtil.isNullString(p_requestVO.getMessageGatewayVO().getGatewayCode()) && ("EXTGW".equals(p_requestVO.getMessageGatewayVO().getGatewayCode()))) {
                externalNetworkCode = (String) requestMap.get(EXTNWCODE_STR);
                if (BTSLUtil.isNullString(externalNetworkCode)) {
                    throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
                }
            }

            if (tagsMandatory) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
                    }
                } catch (BTSLBaseException btsle) {
                    throw btsle;
                }
            }

            final int amount = 0;

            parsedRequestStr = PretupsI.COLLECTION_ENQUIRY + chnl_message_sep + msisdn2 + chnl_message_sep + amount + chnl_message_sep + selector + chnl_message_sep + language1 + chnl_message_sep + language2 + chnl_message_sep + pin;
            p_requestVO.setCellId(cellId);
            p_requestVO.setSwitchId(switchId);
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setReqSelector(selector);
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseChannelCreditTransferRequest", "Exception e: " + e);
            throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    public static void generateChannelCollectionEnquiryResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateChannelCollectionEnquiryResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);

            if (p_requestVO.isSuccessTxn()) {
                sbf.append(getMessage(p_requestVO.getLocale(), "UNDERPROCESS", p_requestVO.getMessageArguments()));
            } else {
                sbf.append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            }
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "BLUSSDStringParser[generateChannelCollectionEnquiryResponse]", PretupsErrorCodesI.C2S_ERROR_EXCEPTION, "", "",
                "generateChannelCreditTransferResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * @author akanksha.gupta
     *         Added for Collection BillPayment report.
     * @param p_requestVO
     * @param p_action
     * @throws Exception
     */
    public static void parseChannelCollectionBillPaymentRequest(RequestVO p_requestVO) throws Exception {
        final String methodName = "parseChannelCollectionBillPaymentRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        String cellId = null;
        String switchId = null;
        final boolean tagsMandatory = false;
        String externalNetworkCode = null;
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            final HashMap requestMap = BTSLUtil.getStringToHash(requestStr, "&", "=");
            final String type = (String) requestMap.get("TYPE");
            String selector = (String) requestMap.get("SELECTOR");
            final String msisdn2 = (String) requestMap.get("MSISDN2");
            final String amount = (String) requestMap.get("AMOUNT");
            final String msisdn1 = (String) requestMap.get("MSISDN1");

            final String language1 = (String) requestMap.get(LANG1_STR);
            final String language2 = (String) requestMap.get(LANG2_STR);

            final String ussdSessionID = (String) requestMap.get(USSD_SESSION_ID);
            final String pin = (String) requestMap.get("PIN");
            cellId = (String) requestMap.get(CELLID_STR);
            switchId = (String) requestMap.get(SWITCHID_STR);

            if (BTSLUtil.isNullString(type) || BTSLUtil.isNullString(msisdn1) || BTSLUtil.isNullString(pin) || BTSLUtil.isNullString(msisdn2) || BTSLUtil
                .isNullString(selector)) {
                throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }
            if (BTSLUtil.isNullString(selector)) {
                final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(PretupsI.COLLECTION_BILLPAYMENT);
                if (serviceSelectorMappingVO != null) {
                    selector = serviceSelectorMappingVO.getSelectorCode();
                }
            }
            if (!BTSLUtil.isNullString(p_requestVO.getMessageGatewayVO().getGatewayCode()) && ("EXTGW".equals(p_requestVO.getMessageGatewayVO().getGatewayCode()))) {
                externalNetworkCode = (String) requestMap.get(EXTNWCODE_STR);
                if (BTSLUtil.isNullString(externalNetworkCode)) {
                    throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
                }
            }

            if (tagsMandatory) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
                    }
                } catch (BTSLBaseException btsle) {
                    throw btsle;
                }
            }
            p_requestVO.setCellId(cellId);
            p_requestVO.setSwitchId(switchId);
            parsedRequestStr = PretupsI.COLLECTION_BILLPAYMENT + chnl_message_sep + msisdn2 + chnl_message_sep + amount + chnl_message_sep + selector + chnl_message_sep + language1 + chnl_message_sep + language2 + chnl_message_sep + pin;
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setReqSelector(selector);
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseChannelCreditTransferRequest", "Exception e: " + e);
            throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    public static void generateChannelCollectionBillPaymentResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateChannelCollectionBillPaymentResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);

            if (p_requestVO.isSuccessTxn()) {
                sbf.append(getMessage(p_requestVO.getLocale(), "UNDERPROCESS", p_requestVO.getMessageArguments()));
            } else {
                sbf.append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            }
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "BLUSSDStringParser[generateChannelCollectionEnquiryResponse]", PretupsErrorCodesI.C2S_ERROR_EXCEPTION, "", "",
                "generateChannelCreditTransferResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    public static void parseC2STransferRequest(RequestVO p_requestVO, int actionId) throws Exception {
        final String methodName = "parseC2STransferRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        String cellId = null;
        String switchId = null;
        final boolean tagsMandatory = false;
        String externalNetworkCode = null;
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            final HashMap requestMap = BTSLUtil.getStringToHash(requestStr, "&", "=");
            final String type = (String) requestMap.get("TYPE");
            String selector = (String) requestMap.get("SELECTOR");
            final String msisdn2 = (String) requestMap.get("MSISDN2");

            final String msisdn1 = (String) requestMap.get("MSISDN1");
            final String amount = (String) requestMap.get("AMOUNT");

            final String language1 = (String) requestMap.get(LANG1_STR);
            final String language2 = (String) requestMap.get(LANG2_STR);

            final String ussdSessionID = (String) requestMap.get(USSD_SESSION_ID);
            final String pin = (String) requestMap.get("PIN");
            cellId = (String) requestMap.get(CELLID_STR);
            switchId = (String) requestMap.get(SWITCHID_STR);

            if (BTSLUtil.isNullString(type) || BTSLUtil.isNullString(msisdn1) || BTSLUtil.isNullString(pin) || BTSLUtil.isNullString(msisdn2) || BTSLUtil.isNullString(amount) || BTSLUtil
                .isNullString(selector)) {
                throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }
            if (BTSLUtil.isNullString(selector)) {
                String selectorAction = "";

                if (actionId == ParserUtility.ACTION_DTH) {
                    selectorAction = PretupsI.INTERFACE_CATEGORY_DTH;
                } else if (actionId == ParserUtility.ACTION_CHNL_CREDIT_TRANSFER) {
                    selectorAction = PretupsI.SERVICE_TYPE_CHNL_RECHARGE;
                } else if (actionId == ParserUtility.ACTION_DC) {
                    selectorAction = PretupsI.INTERFACE_CATEGORY_DATACARD;
                } else if (actionId == ParserUtility.ACTION_PIN) {
                    selectorAction = PretupsI.INTERFACE_CATEGORY_PIN;
                } else if (actionId == ParserUtility.ACTION_PMD) {
                    selectorAction = PretupsI.INTERFACE_CATEGORY_PMD;
                } else if (actionId == ParserUtility.ACTION_BPB) {
                    selectorAction = PretupsI.INTERFACE_CATEGORY_BPB;
                } else if (actionId == ParserUtility.ACTION_FLRC) {
                    selectorAction = PretupsI.INTERFACE_CATEGORY_FLRC;
                }
                final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(selectorAction);
                if (serviceSelectorMappingVO != null) {
                    selector = serviceSelectorMappingVO.getSelectorCode();
                }
            }
            // if we have to forcibaly change the value of default selecter in
            // case of ussd request.
            if (!BTSLUtil.isNullString(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEF_FRCXML_SEL_C2S)))) {
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "using default force selecter of XML");
                }
                selector = "" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEF_FRCXML_SEL_C2S));
            }
            if (!BTSLUtil.isNullString(p_requestVO.getMessageGatewayVO().getGatewayCode()) && ("EXTGW".equals(p_requestVO.getMessageGatewayVO().getGatewayCode()))) {
                externalNetworkCode = (String) requestMap.get(EXTNWCODE_STR);
                if (BTSLUtil.isNullString(externalNetworkCode)) {
                    throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
                }
            }

            if (tagsMandatory) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
                    }
                } catch (BTSLBaseException btsle) {
                    throw btsle;
                }
            }

            if (actionId == ParserUtility.ACTION_DTH) {
                parsedRequestStr = PretupsI.INTERFACE_CATEGORY_DTH + chnl_message_sep + msisdn2 + chnl_message_sep + amount + chnl_message_sep + selector + chnl_message_sep + language1 + chnl_message_sep + language2 + chnl_message_sep + pin;
            } else if (actionId == ParserUtility.ACTION_CHNL_CREDIT_TRANSFER) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_RECHARGE + chnl_message_sep + msisdn2 + chnl_message_sep + amount + chnl_message_sep + selector + chnl_message_sep + language1 + chnl_message_sep + language2 + chnl_message_sep + pin;
            } else if (actionId == ParserUtility.ACTION_DC) {
                parsedRequestStr = PretupsI.INTERFACE_CATEGORY_DATACARD + chnl_message_sep + msisdn2 + chnl_message_sep + amount + chnl_message_sep + selector + chnl_message_sep + language1 + chnl_message_sep + language2 + chnl_message_sep + pin;
            } else if (actionId == ParserUtility.ACTION_PIN) {
                parsedRequestStr = PretupsI.INTERFACE_CATEGORY_PIN + chnl_message_sep + msisdn2 + chnl_message_sep + amount + chnl_message_sep + selector + chnl_message_sep + language1 + chnl_message_sep + language2 + chnl_message_sep + pin;
            } else if (actionId == ParserUtility.ACTION_PMD) {
                parsedRequestStr = PretupsI.INTERFACE_CATEGORY_PMD + chnl_message_sep + msisdn2 + chnl_message_sep + amount + chnl_message_sep + selector + chnl_message_sep + language1 + chnl_message_sep + language2 + chnl_message_sep + pin;
            } else if (actionId == ParserUtility.ACTION_BPB) {
                parsedRequestStr = PretupsI.INTERFACE_CATEGORY_BPB + chnl_message_sep + msisdn2 + chnl_message_sep + amount + chnl_message_sep + selector + chnl_message_sep + language1 + chnl_message_sep + language2 + chnl_message_sep + pin;
            } else if (actionId == ParserUtility.ACTION_FLRC) {
                parsedRequestStr = PretupsI.INTERFACE_CATEGORY_FLRC + chnl_message_sep + msisdn2 + chnl_message_sep + amount + chnl_message_sep + selector + chnl_message_sep + language1 + chnl_message_sep + language2 + chnl_message_sep + pin;
            }
            p_requestVO.setCellId(cellId);
            p_requestVO.setSwitchId(switchId);
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setReqSelector(selector);
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    public static void generateC2STransferResponse(RequestVO p_requestVO, int actionId) throws Exception {
        final String methodName = "generateC2STransferResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);

            if (p_requestVO.isSuccessTxn()) {
                sbf.append(getMessage(p_requestVO.getLocale(), "UNDERPROCESS", p_requestVO.getMessageArguments()));
            } else {
                sbf.append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            }
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BLUSSDStringParser[generateC2STransferResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateC2STransferResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * @author akanksha.gupta
     *         Added for Collection BillPayment Reversal report.
     * @param p_requestVO
     * @param p_action
     * @throws Exception
     */
    public static void parseC2SPostPaidReversalRequest(RequestVO p_requestVO) throws Exception {
        final String methodName = "parseC2SPostPaidReversalRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        String cellId = null;
        String switchId = null;
        final boolean tagsMandatory = false;
        String externalNetworkCode = null;
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            final HashMap requestMap = BTSLUtil.getStringToHash(requestStr, "&", "=");
            final String type = (String) requestMap.get("TYPE");
            String selector = (String) requestMap.get("SELECTOR");
            final String msisdn2 = (String) requestMap.get("MSISDN2");
            final String msisdn1 = (String) requestMap.get("MSISDN1");

            final String language1 = (String) requestMap.get(LANG1_STR);
            final String language2 = (String) requestMap.get(LANG2_STR);
            final String ussdSessionID = (String) requestMap.get(USSD_SESSION_ID);
            final String pin = (String) requestMap.get("PIN");
            cellId = (String) requestMap.get(CELLID_STR);
            switchId = (String) requestMap.get(SWITCHID_STR);
            final String txnid = (String) requestMap.get("TRANSACTIONID");

            if (BTSLUtil.isNullString(type) || BTSLUtil.isNullString(msisdn1) || BTSLUtil.isNullString(pin) || BTSLUtil.isNullString(msisdn2) || BTSLUtil
                .isNullString(selector) || BTSLUtil.isNullString(txnid)) {
                throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }
            if (BTSLUtil.isNullString(selector)) {
                final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(PretupsI.COLLECTION_CANCELATION);
                if (serviceSelectorMappingVO != null) {
                    selector = serviceSelectorMappingVO.getSelectorCode();
                }
            }
            if (!BTSLUtil.isNullString(p_requestVO.getMessageGatewayVO().getGatewayCode()) && ("EXTGW".equals(p_requestVO.getMessageGatewayVO().getGatewayCode()))) {
                externalNetworkCode = (String) requestMap.get(EXTNWCODE_STR);
                if (BTSLUtil.isNullString(externalNetworkCode)) {
                    throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
                }
            }

            if (tagsMandatory) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
                    }
                } catch (BTSLBaseException btsle) {
                    throw btsle;
                }
            }
            final int amount = 0;
            parsedRequestStr = PretupsI.COLLECTION_CANCELATION + chnl_message_sep + msisdn2 + chnl_message_sep + amount + chnl_message_sep + selector + chnl_message_sep + language1 + chnl_message_sep + language2 + chnl_message_sep + pin + chnl_message_sep + txnid;
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setReqSelector(selector);
            p_requestVO.setCellId(cellId);
            p_requestVO.setSwitchId(switchId);
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
            p_requestVO.setValidatePin(pin);

        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

/**
     * @author Zeeshan Aleem
     *         Added for IAT Recharge.
     * @param p_requestVO
     * @param p_action
     * @throws Exception
     */
    public static void parseIATRoamRecharge(RequestVO p_requestVO) throws Exception {
        final String methodName = "parseIATRoamRecharge";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        String cellId = null;
        String switchId = null;
        final boolean tagsMandatory = false;
        String externalNetworkCode = null;
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            final HashMap requestMap = BTSLUtil.getStringToHash(requestStr, "&", "=");
            final String type = (String) requestMap.get("TYPE");            
            final String msisdn2 = (String) requestMap.get("MSISDN2");
            final String msisdn1 = (String) requestMap.get("MSISDN1");
            final String amount = (String) requestMap.get("AMOUNT");
            final String language1 = (String) requestMap.get(LANG1_STR);            
            final String ussdSessionID = (String) requestMap.get(USSD_SESSION_ID);
            final String pin = (String) requestMap.get("PIN");
            if (BTSLUtil.isNullString(type) || BTSLUtil.isNullString(msisdn1) || BTSLUtil.isNullString(pin) || BTSLUtil.isNullString(msisdn2)) {
                throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }
            if (!BTSLUtil.isNullString(p_requestVO.getMessageGatewayVO().getGatewayCode()) && ("EXTGW".equals(p_requestVO.getMessageGatewayVO().getGatewayCode()))) {
                externalNetworkCode = (String) requestMap.get(EXTNWCODE_STR);
                if (BTSLUtil.isNullString(externalNetworkCode)) {
                    throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
                }
            }
            if (tagsMandatory) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
                    }
                } catch (BTSLBaseException btsle) {
                    throw btsle;
                }
            }
            parsedRequestStr = PretupsI.IAT_SERVICE_TYPE_ROAM_RECHARGE + chnl_message_sep + msisdn2 + chnl_message_sep + amount + chnl_message_sep + pin + chnl_message_sep ;
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));            
            p_requestVO.setCellId(cellId);
            p_requestVO.setSwitchId(switchId);
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
            p_requestVO.setValidatePin(pin);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }
    public static void generateC2SPostPaidReversalResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateC2SPostPaidReversalResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            if (p_requestVO.isSuccessTxn()) {
                sbf.append(getMessage(p_requestVO.getLocale(), "UNDERPROCESS", p_requestVO.getMessageArguments()));
            } else {
                sbf.append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            }
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "BLUSSDStringParser[generateC2SPostPaidReversalResponse]", PretupsErrorCodesI.C2S_ERROR_EXCEPTION, "", "",
                "generateChannelCollectionCancallationResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * @param p_requestVO
     * @throws Exception
     * @author rahul.dutt
     */
    public static void generateChannelPPBEnqResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateChannelPPBEnqResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("TYPE=PBENQRESP&");

            if (p_requestVO.isSuccessTxn()) {
                sbf.append("TXNSTATUS=" + PretupsI.TXN_STATUS_SUCCESS);
            } else {
                sbf.append("TXNSTATUS=" + p_requestVO.getMessageCode());
            }
            sbf.append("&TOTALAMT=" + p_requestVO.getDueAmt());
            sbf.append("&MINAMT=" + p_requestVO.getMinAmtDue());
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "XMLStringParser[generateChannelLastTransferStatusResponse]", PretupsErrorCodesI.C2S_ERROR_EXCEPTION, "", "", "generateChannelLastTransferStatusResponse:" + e
                    .getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * @param p_requestVO
     * @throws Exception
     * @author rahul.dutt
     */
    public static void parsePPBEnqRequest(RequestVO p_requestVO) throws Exception {
        final String methodName = "parsePPBEnqRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            final HashMap requestMap = BTSLUtil.getStringToHash(requestStr, "&", "=");
            final String type = (String) requestMap.get("TYPE");
            final String msisdn1 = (String) requestMap.get("MSISDN1");
            final String pin = (String) requestMap.get("PIN");
            final String msisdn2 = (String) requestMap.get("MSISDN2");
            final String lang1 = (String) requestMap.get("LANGUAGE1");
            final String lang2 = (String) requestMap.get("LANGUAGE2");
            if (BTSLUtil.isNullString(type) || BTSLUtil.isNullString(msisdn1) || BTSLUtil.isNullString(pin) || BTSLUtil.isNullString(msisdn2)) {
                throw new BTSLBaseException("BLUSSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }
            if (lang1 != null && lang2 != null) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_PPB_ENQUIRY + chnl_message_sep + msisdn2 + chnl_message_sep + lang1 + chnl_message_sep + lang2 + chnl_message_sep + pin;
            } else if (lang1 != null) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_PPB_ENQUIRY + chnl_message_sep + msisdn2 + chnl_message_sep + lang1 + chnl_message_sep + pin;
            } else {
                parsedRequestStr = PretupsI.SERVICE_TYPE_PPB_ENQUIRY + chnl_message_sep + msisdn2 + chnl_message_sep + pin;
            }
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);

        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }
    /**
	 *
	 * @param p_requestVO
	 * @throws Exception
	 */
	public static void parseChannelUserAuthRequest(RequestVO p_requestVO) throws Exception
	{
		if (_log.isDebugEnabled())
			_log.debug("parseChannelUserAuthRequest", "Entered p_requestVO: "
					+ p_requestVO.toString());
		String parsedRequestStr =null;
		try
		{
			String requestStr=p_requestVO.getRequestMessage();
			HashMap requestMap=BTSLUtil.getStringToHash(requestStr,"&","=");
			String type=(String)requestMap.get(TYPE_STR);
			String chnnUserExtCode=(String)requestMap.get(EXTNWCODE_STR);
			String date=(String)requestMap.get(DATE_STR);
			String extRefNum=(String)requestMap.get(EXTREFNUM_STR);
			String userLoginID=(String)requestMap.get(USERLOGINID_STR);
			String userPassword=(String)requestMap.get(USERPASSWORD_STR);
			String msisdn=(String)requestMap.get(MSISDN1_STR);
			String pin=(String)requestMap.get(PIN_STR);
			String ussdSessionID=(String)requestMap.get(USSD_SESSION_ID);
			if(BTSLUtil.isNullString(msisdn)||BTSLUtil.isNullString(pin))
			{
				throw new BTSLBaseException("USSDStringParser","parseChannelUserAuthRequest",PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT);
			}
			parsedRequestStr=PretupsI.SERVICE_TYPE_USER_AUTH;
			p_requestVO.setDecryptedMessage(parsedRequestStr);
			p_requestVO.setRequestMap(requestMap);
			p_requestVO.setRequestMSISDN(msisdn);
			p_requestVO.setExternalReferenceNum(extRefNum);
			p_requestVO.setUssdSessionID(ussdSessionID);
		}
		catch(Exception e)
		{
            _log.errorTrace("parseChannelUserAuthRequest", e);
			p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT);
			p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
			_log.error("parseChannelUserAuthRequest","Exception e: "+e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"USSDStringParser[parseChannelUserAuthRequest]",PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT,"","","parseChannelUserAuthRequest:"+e.getMessage());
			throw new BTSLBaseException("USSDStringParser","parseChannelUserAuthRequest",PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT);
		}
		finally
		{
			if (_log.isDebugEnabled())
				_log.debug("parseChannelUserAuthRequest",
						"Exiting  p_requestVO: " + p_requestVO.toString());
		}
	}
    /**
     * @author zeeshan.aleem
     * @param p_requestVO
     * @throws Exception
     */
    public static void generateUserAuthorizationResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateUserAuthorizationResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateUserAuthorizationResponse]",
                PretupsErrorCodesI.C2S_ERROR_EXCEPTION, "", "", "generateUserAuthorizationResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }	
    
    /**
  	 *
  	 * @param p_requestVO
  	 * @throws Exception
  	 */
  	public static void parseChannelVASRequest(RequestVO p_requestVO) throws Exception
  	{
		if (_log.isDebugEnabled())
			_log.debug("parseChannelUserAuthRequest", "Entered p_requestVO: "
					+ p_requestVO.toString());
  		String parsedRequestStr =null;
  		try
  		{
  			String requestStr=p_requestVO.getRequestMessage();
  			HashMap requestMap=BTSLUtil.getStringToHash(requestStr,"&","=");
  			String type=(String)requestMap.get(TYPE_STR);
  			String chnnUserExtCode=(String)requestMap.get(EXTNWCODE_STR);
  			String date=(String)requestMap.get(DATE_STR);
  			String extRefNum=(String)requestMap.get(EXTREFNUM_STR);
  			String userLoginID=(String)requestMap.get(USERLOGINID_STR);
  			String userPassword=(String)requestMap.get(USERPASSWORD_STR);
  			String msisdn1=(String)requestMap.get(MSISDN1_STR);
  			String msisdn2=(String)requestMap.get(MSISDN2_STR);
  			String amount = (String) requestMap.get(AMOUNT_STR);
  			String selector = (String) requestMap.get(SELECTOR_STR);
  			String language1 = (String) requestMap.get(LANG1_STR);
		        String language2 = (String) requestMap.get(LANG2_STR);
  			String pin=(String)requestMap.get(PIN_STR);
  			String ussdSessionID=(String)requestMap.get(USSD_SESSION_ID);
  			if(BTSLUtil.isNullString(msisdn1) || BTSLUtil.isNullString(msisdn2) || BTSLUtil.isNullString(pin))
  			{
  				throw new BTSLBaseException("USSDStringParser","parseChannelUserAuthRequest",PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT);
  			}
  			 if (!BTSLUtil.isNullString(amount) && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTI_AMOUNT_ENABLED))).booleanValue()) {
                 parsedRequestStr = PretupsI.SERVICE_TYPE_VAS_RECHARGE + chnl_message_sep + msisdn2 + chnl_message_sep + amount + chnl_message_sep + selector + chnl_message_sep + language2 + chnl_message_sep + pin;
             } else if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTI_AMOUNT_ENABLED))).booleanValue()) {
                 parsedRequestStr = PretupsI.SERVICE_TYPE_VAS_RECHARGE + chnl_message_sep + msisdn2 + chnl_message_sep + selector + chnl_message_sep + language2 + chnl_message_sep + pin;
             } else if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTI_AMOUNT_ENABLED))).booleanValue() && BTSLUtil.isNullString(amount)) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_VAS_RECHARGE + chnl_message_sep + msisdn2 + chnl_message_sep + PretupsI.VAS_BLANK_SLCTR_AMNT + chnl_message_sep + selector + chnl_message_sep + language2 + chnl_message_sep + pin;
            }  else {
                 throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelVasTransferRequest", PretupsErrorCodesI.AMOUNT_REQUIRED);
             }
  			p_requestVO.setDecryptedMessage(parsedRequestStr);
  			p_requestVO.setRequestMap(requestMap);
  			p_requestVO.setRequestMSISDN(msisdn1);
  			p_requestVO.setExternalReferenceNum(extRefNum);
  			p_requestVO.setUssdSessionID(ussdSessionID);
  		}
  		catch(Exception e)
  		{
            _log.errorTrace("parseChannelUserAuthRequest", e);
  			p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT);
  			p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
  			_log.error("parseChannelUserAuthRequest","Exception e: "+e);
  			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"USSDStringParser[parseChannelUserAuthRequest]",PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT,"","","parseChannelUserAuthRequest:"+e.getMessage());
  			throw new BTSLBaseException("USSDStringParser","parseChannelUserAuthRequest",PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT);
  		}
  		finally
  		{
			if (_log.isDebugEnabled())
				_log.debug("parseChannelUserAuthRequest",
						"Exiting  p_requestVO: " + p_requestVO.toString());
  		}
  	}
      /**
       * @author zeeshan.aleem
       * @param p_requestVO
       * @throws Exception
       */
      public static void generateVASResponse(RequestVO p_requestVO) throws Exception {
          final String methodName = "generateUserAuthorizationResponse";
          if (_log.isDebugEnabled()) {
              _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
          }
          String responseStr = null;
          StringBuffer sbf = null;
          try {
              sbf = new StringBuffer(1024);
              sbf.append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
              responseStr = sbf.toString();
              p_requestVO.setSenderReturnMessage(responseStr);
          } catch (Exception e) {
              _log.errorTrace(methodName, e);
              _log.error(methodName, "Exception e: " + e);
              p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
              EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateUserAuthorizationResponse]",
                  PretupsErrorCodesI.C2S_ERROR_EXCEPTION, "", "", "generateUserAuthorizationResponse:" + e.getMessage());
          } finally {
              if (_log.isDebugEnabled()) {
                  _log.debug(methodName, "Exiting responseStr: " + responseStr);
              }
          }
      }	
}
