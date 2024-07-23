/*
 * @(#)USSDStringParser.java
 * Copyright(c) 2009, Comviva technologies
 * All Rights Reserved
 * ------------------------------------------------------------------------------
 * -------------------
 * Author Date History
 * ------------------------------------------------------------------------------
 * -------------------
 * Manisha Jain 24/11/09 Initial creation
 * ------------------------------------------------------------------------------
 * -------------------
 * Parse the request of USSD system, Request should be in String format
 */
package com.btsl.pretups.gateway.util;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;

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
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class USSDStringParser {

    private static String CHNL_MESSAGE_SEP = null;
    private static String P2P_MESSAGE_SEP = null;
    private static String USSD_RESP_SEP=null;
    private static String P2P_USSD_RESP_SEP=null;
    public static final String TYPE_STR = "TYPE";
    public static final String TXNID_STR = "TXNID";
    public static final String TXNSTATUS_STR = "TXNSTATUS";
    public static final String MSISDN1_STR = "MSISDN1";
    public static final String PIN_STR = "PIN";
    public static final String MSISDN2_STR = "MSISDN2";
    public static final String SELECTOR_STR = "SELECTOR";
    public static final String AMOUNT_STR = "AMOUNT";

    public static final String LANG1_STR = "LANGUAGE1";
    public static final String LANG2_STR = "LANGUAGE2";
    public static final String CELLID_STR = "CELLID";
    public static final String SWITCHID_STR = "SWITCHID";
    public static final String USSD_SESSION_ID = "USSDSESSIONID";
    public static final String EXTNWCODE_STR = "EXTNWCODE";
    public static final String CURRENCY_STR = "CURRENCY";
    public static final String DATE_STR="DATE";
	public static final String USERLOGINID_STR="USERLOGINID";
	public static final String USERPASSWORD_STR="USERPASSWORD";
	public static final String EXTREFNUM_STR="EXTREFNUM";
	
	public static String ACCOUNTID_STR = "ACCOUNTID";
	//added by Ashish for VIL
	private static OperatorUtilI _operatorUtil = null;
	/** name of class for writing log*/
	private static final String CLASS_NAME = "USSDStringParser";	
    public static final Log _log = LogFactory.getLog(USSDStringParser.class.getName());
    private final static String _blank = "";
    static {
    	
            final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            try {
                _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
            }catch (InstantiationException e) {
            	_log.errorTrace("USSDStringParser", e);
            } catch (ClassNotFoundException e) {
            	_log.errorTrace("USSDStringParser", e);
            } 
            catch (Exception e) {
                _log.errorTrace("USSDStringParser", e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "USSDStringParser[initialize]", "", "", "",
                    "Exception while loading the class at the call:" + e.getMessage());
            }
            String chnlPlainSmsSeparator = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
            String p2pPlainSmsSeparator = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_PLAIN_SMS_SEPARATOR);
            String ussdRespSeparator = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_RESP_SEPARATOR);
        try {
            CHNL_MESSAGE_SEP = chnlPlainSmsSeparator;
            if (BTSLUtil.isNullString(CHNL_MESSAGE_SEP)) {
                CHNL_MESSAGE_SEP = " ";
            }
            P2P_MESSAGE_SEP = p2pPlainSmsSeparator;
            if (BTSLUtil.isNullString(P2P_MESSAGE_SEP)) {
                P2P_MESSAGE_SEP = " ";
            }
            
           USSD_RESP_SEP=ussdRespSeparator;
	       if(BTSLUtil.isNullString(USSD_RESP_SEP)) {
	    	   USSD_RESP_SEP="&";
	       }
	       P2P_USSD_RESP_SEP=ussdRespSeparator;
	       if(BTSLUtil.isNullString(P2P_USSD_RESP_SEP)) {
	    	   P2P_USSD_RESP_SEP="&";
	       }
        } catch (Exception e) {
            _log.errorTrace("static", e);
        }
    }

    /**
	 * ensures no instantiation
	 */
    private USSDStringParser(){
    	
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
        boolean ussdRcLangParamReq = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_RC_LANG_PARAM_REQ);
        String defFrcXmlSelC2S = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEF_FRCXML_SEL_C2S);
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

            if (BTSLUtil.isNullString(type)) {
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }
            
            if (BTSLUtil.isNullString(msisdn1)) {
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.XML_ERROR_MSISDN_IS_NULL);
            }
            
            if (BTSLUtil.isNullString(pin) ) {
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_BLANK_PIN);
            }
            
            if (BTSLUtil.isNullString(msisdn2)) {
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.CHNL_ERROR_RECR_MSISDN_BLANK);
            }
            
            if (BTSLUtil.isNullString(amount)) {
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_BLANK_AMOUNT);
            }
            
            if (BTSLUtil.isNullString(selector)) {
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.USSD_BLANK_SELECTOR);
            }
            
            if (BTSLUtil.isNullString(selector)) {
                final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(PretupsI.SERVICE_TYPE_CHNL_RECHARGE);
                if (serviceSelectorMappingVO != null) {
                    selector = serviceSelectorMappingVO.getSelectorCode();
                }
            }
            // if we have to forcibaly change the value of default selecter in
            // case of ussd request.
            if (!BTSLUtil.isNullString(defFrcXmlSelC2S)) {
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "using default force selecter of XML");
                }
                selector = "" + defFrcXmlSelC2S;
            }
            if (!BTSLUtil.isNullString(p_requestVO.getMessageGatewayVO().getGatewayCode()) && ("EXTGW".equals(p_requestVO.getMessageGatewayVO().getGatewayCode()))) {
                externalNetworkCode = (String) requestMap.get(EXTNWCODE_STR);
                if (BTSLUtil.isNullString(externalNetworkCode)) {
                    throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
                }
            }

            if (tagsMandatory) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
                    }
                } catch (BTSLBaseException btsle) {
                    throw btsle;
                }
            }

            if (!ussdRcLangParamReq) {
            	parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_RECHARGE + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + amount + CHNL_MESSAGE_SEP + selector + CHNL_MESSAGE_SEP + language1 + CHNL_MESSAGE_SEP + language2 + CHNL_MESSAGE_SEP + pin;
            } else {
            	parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_RECHARGE + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + amount + CHNL_MESSAGE_SEP + selector + CHNL_MESSAGE_SEP + "1" + CHNL_MESSAGE_SEP + pin;
            }

            p_requestVO.setCellId(cellId);
            p_requestVO.setSwitchId(switchId);
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            
            if (!ussdRcLangParamReq) {
            	p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(language2));
            }
            
            p_requestVO.setReqSelector(selector);
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
        } catch (BTSLBaseException e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(e.getMessageKey());
            _log.error(methodName, "Exception e: " + e);
            throw e;          
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
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
            sbf.append("TYPE=RCTRFRESP&");
            if (BTSLUtil.isNullString(p_requestVO.getTransactionID())) {
                sbf.append("TXNID=&");
            } else {
                sbf.append("TXNID>").append(p_requestVO.getTransactionID()).append("&");
            }
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS);
            } else {
                String message = p_requestVO.getMessageCode();
                if (message.indexOf("_") != -1) {
                    message = message.substring(0, message.indexOf("_"));
                }
                sbf.append("TXNSTATUS>").append(message);
            }
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("&MESSAGE>").append(getMessage(p_requestVO.getLocale(), "UNDERPROCESS", p_requestVO.getMessageArguments()));
            } else {
                sbf.append("&MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            }
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error("generateCreditTransferResponse", "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "USSDStringParser[generateChannelCreditTransferResponse]", PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateChannelCreditTransferResponse:" + e
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
        boolean ussdNewTagsMandatory = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY);
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

            tagsMandatory = ussdNewTagsMandatory;
            if (tagsMandatory) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
                    }
                } catch (BTSLBaseException btsle) {
                    throw btsle;
                }
            }
            if (BTSLUtil.isNullString(type) || BTSLUtil.isNullString(msisdn1) || BTSLUtil.isNullString(pin) || BTSLUtil.isNullString(newPin) || BTSLUtil
                .isNullString(confirmPin)) {
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }

            parsedRequestStr = (PretupsI.C2S_MODULE.equals(p_requestVO.getModule()) ? PretupsI.SERVICE_TYPE_CHNL_CHANGEPIN : PretupsI.SERVICE_TYPE_P2PCHANGEPIN) + CHNL_MESSAGE_SEP + pin + CHNL_MESSAGE_SEP + newPin + CHNL_MESSAGE_SEP + confirmPin;
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
            p_requestVO.setCellId(cellId);
            p_requestVO.setSwitchId(switchId);
        } catch (BTSLBaseException e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(e.getMessageKey());
            _log.error(methodName, "Exception e: " + e);
            throw e;          
        }
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
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
            //sbf.append("TYPE=RCPNRESP&");
            sbf.append(PretupsI.C2S_MODULE.equals(p_requestVO.getModule()) ? "TYPE=RCPNRESP&" : "TYPE=CCPNRESP&");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS);
            } else {
                sbf.append("TXNSTATUS>").append(p_requestVO.getMessageCode());
            }
            sbf.append("&MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "USSDStringParser[generateChannelChangePinResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateChannelChangePinResponse:" + e.getMessage());
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
        boolean ussdNewTagsMandatory = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY);
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            final HashMap requestMap = BTSLUtil.getStringToHash(requestStr, "&", "=");
            final String type = (String) requestMap.get("TYPE");
            final String msisdn1 = (String) requestMap.get("MSISDN1");
            final String language1 = (String) requestMap.get("LANGUAGE1");
            final String pin = (String) requestMap.get("PIN");
            cellId = (String) requestMap.get(CELLID_STR);

            switchId = (String) requestMap.get(SWITCHID_STR);
            tagsMandatory = ussdNewTagsMandatory;
            if (tagsMandatory) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
                    }
                } catch (BTSLBaseException btsle) {
                    throw btsle;
                }
            }

            if (BTSLUtil.isNullString(type) || BTSLUtil.isNullString(msisdn1) || BTSLUtil.isNullString(pin) || BTSLUtil.isNullString(language1)) {
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }

            parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_LANG_NOTIFICATION + CHNL_MESSAGE_SEP + language1 + CHNL_MESSAGE_SEP + pin;
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
            throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
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
            sbf.append("TYPE=RCLANGRESP&");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS);
            } else {
                sbf.append("TXNSTATUS>").append(p_requestVO.getMessageCode());
            }
            sbf.append("&MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "USSDStringParser[generateChannelNotificationLanguageResponse]", PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "",
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
            if (p_action == ParserUtility.ACTION_CHNL_TRANSFER_MESSAGE) {
                sbf.append("TYPE=TRFRESP&");
            } else if (p_action == ParserUtility.ACTION_CHNL_RETURN_MESSAGE) {
                sbf.append("TYPE=RETRESP&");
            } else if (p_action == ParserUtility.ACTION_CHNL_WITHDRAW_MESSAGE) {
                sbf.append("TYPE=WDTHRESP&");
            }

            if (p_requestVO.isSuccessTxn()) {
                sbf.append("TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS);
            } else {
                sbf.append("TXNSTATUS>").append(p_requestVO.getMessageCode());
            }
            sbf.append("&MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "USSDStringParser[generateChannelTransferResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateChannelTransferResponse:" + e.getMessage());
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
            sbf.append("TYPE=BALRESP&");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS);
            } else {
                sbf.append("TXNSTATUS>").append(p_requestVO.getMessageCode());
            }
            sbf.append("&MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "USSDStringParser[generateChannelBalanceEnquiryResponse]", PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateChannelBalanceEnquiryResponse:" + e
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
            sbf.append("TYPE=DSRRESP&");

            if (p_requestVO.isSuccessTxn()) {
                sbf.append("TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS);
            } else {
                sbf.append("TXNSTATUS>").append(p_requestVO.getMessageCode());
            }
            sbf.append("&MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "USSDStringParser[generateChannelDailyStatusReportResponse]", PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateChannelDailyStatusReportResponse:" + e
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
            sbf.append("TYPE=LTSRESP&");

            if (p_requestVO.isSuccessTxn()) {
                sbf.append("TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS);
            } else {
                sbf.append("TXNSTATUS>").append(p_requestVO.getMessageCode());
            }
            sbf.append("&MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "USSDStringParser[generateChannelLastTransferStatusResponse]", PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "",
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
    public static void generateFailureResponse(RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "generateFailureResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered :p_requestVO " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("TYPE=&");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS);
            } else {
                sbf.append("TXNSTATUS>").append(p_requestVO.getMessageCode());
            }
            sbf.append("&MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.error(methodName, "Exception e: " + e);
            _log.errorTrace(methodName, e);
            throw new BTSLBaseException("USSDStringParser", methodName, "Exception in generating Failure Response");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    public static void parseChannelTransferRequest(RequestVO p_requestVO, int p_action) throws Exception {
        final String methodName = "parseChannelTransferRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString() + " p_action="+p_action);
        }
        String parsedRequestStr = null;
        boolean tagsMandatory;
        String cellId = null;
        String switchId = null;
        boolean ussdNewTagsMandatory = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY);
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

            if (BTSLUtil.isNullString(type)) {
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }
            
            if (BTSLUtil.isNullString(msisdn1)) {
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.XML_ERROR_MSISDN_IS_NULL);
            }
            
            if (BTSLUtil.isNullString(pin) ) {
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_BLANK_PIN);
            }
            
            if (BTSLUtil.isNullString(msisdn2)) {
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.CHNL_ERROR_RECR_MSISDN_BLANK);
            }
            
            if (BTSLUtil.isNullString(amount)) {
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_BLANK_AMOUNT);
            }

            String defaultProductValue = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_PRODUCT);
            if (BTSLUtil.isNullString(productCode)) {
                productCode = defaultProductValue;
            }
            // finding the transfer ,retuen and withdraw
            if (p_action == ParserUtility.ACTION_CHNL_TRANSFER_MESSAGE) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_TRANSFER + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + amount + CHNL_MESSAGE_SEP + productCode + CHNL_MESSAGE_SEP + pin;
            } else if (p_action == ParserUtility.ACTION_CHNL_RETURN_MESSAGE) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_RETURN + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + amount + CHNL_MESSAGE_SEP + productCode + CHNL_MESSAGE_SEP + pin;
            } else if (p_action == ParserUtility.ACTION_CHNL_WITHDRAW_MESSAGE) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_WITHDRAW + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + amount + CHNL_MESSAGE_SEP + productCode + CHNL_MESSAGE_SEP + pin;
            }
            tagsMandatory = ussdNewTagsMandatory;
            if (tagsMandatory) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
                    }
                } catch (BTSLBaseException btsle) {
                    throw btsle;
                }
            }
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
            tagsMandatory = ussdNewTagsMandatory;
            p_requestVO.setCellId(cellId);
            p_requestVO.setSwitchId(switchId);

        }catch (BTSLBaseException e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(e.getMessageKey());
            _log.error(methodName, "Exception e: " + e);
            throw e;          
        }
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
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
        boolean ussdNewTagsMandatory = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY);
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            final HashMap requestMap = BTSLUtil.getStringToHash(requestStr, "&", "=");
            final String type = (String) requestMap.get("TYPE");
            final String msisdn1 = (String) requestMap.get("MSISDN1");
            final String pin = (String) requestMap.get("PIN");
            final String msisdn2 = (String) requestMap.get("MSISDN2");
            final String cellId = (String) requestMap.get(CELLID_STR);

            final String switchId = (String) requestMap.get(SWITCHID_STR);
            tagsMandatory = ussdNewTagsMandatory;
            if (tagsMandatory) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
                    }
                } catch (BTSLBaseException btsle) {
                    throw btsle;
                }
            }

            if (BTSLUtil.isNullString(type)) {
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }
            
            if (BTSLUtil.isNullString(msisdn1)) {
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.XML_ERROR_MSISDN_IS_NULL);
            }
            
            if (BTSLUtil.isNullString(pin) ) {
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_BLANK_PIN);
            }
            
            if (!BTSLUtil.isNullString(msisdn2)) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_BALANCE_ENQUIRY + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + pin;
            } else {
                parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_BALANCE_ENQUIRY + CHNL_MESSAGE_SEP + pin;
            }
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
            p_requestVO.setCellId(cellId);
            p_requestVO.setSwitchId(switchId);
        } catch (BTSLBaseException e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(e.getMessageKey());
            _log.error(methodName, "Exception e: " + e);
            throw e;          
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
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
        boolean ussdNewTagsMandatory = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY);
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            final HashMap requestMap = BTSLUtil.getStringToHash(requestStr, "&", "=");
            final String type = (String) requestMap.get("TYPE");
            final String msisdn1 = (String) requestMap.get("MSISDN1");
            final String pin = (String) requestMap.get("PIN");
            final String cellId = (String) requestMap.get(CELLID_STR);

            final String switchId = (String) requestMap.get(SWITCHID_STR);
            tagsMandatory = ussdNewTagsMandatory;
            if (tagsMandatory) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
                    }
                } catch (BTSLBaseException btsle) {
                    throw btsle;
                }
            }
            
            if (BTSLUtil.isNullString(type)) {
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }
            
            if (BTSLUtil.isNullString(msisdn1)) {
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.XML_ERROR_MSISDN_IS_NULL);
            }
            
            if (BTSLUtil.isNullString(pin) ) {
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_BLANK_PIN);
            }

            parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_DAILY_STATUS_REPORT + CHNL_MESSAGE_SEP + pin;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
            p_requestVO.setCellId(cellId);
            p_requestVO.setSwitchId(switchId);
        } catch (BTSLBaseException e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(e.getMessageKey());
            _log.error(methodName, "Exception e: " + e);
            throw e;          
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
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
        boolean ussdNewTagsMandatory = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY);
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            final HashMap requestMap = BTSLUtil.getStringToHash(requestStr, "&", "=");
            final String type = (String) requestMap.get("TYPE");
            final String msisdn1 = (String) requestMap.get("MSISDN1");
            final String pin = (String) requestMap.get("PIN");
            final String cellId = (String) requestMap.get(CELLID_STR);

            final String switchId = (String) requestMap.get(SWITCHID_STR);
            tagsMandatory = ussdNewTagsMandatory;
            if (tagsMandatory) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
                    }
                } catch (BTSLBaseException btsle) {
                    throw btsle;
                }
            }

            if (BTSLUtil.isNullString(type) || BTSLUtil.isNullString(msisdn1) || BTSLUtil.isNullString(pin)) {
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }

            parsedRequestStr = PretupsI.SERVICE_TYPE_LAST_TRANSFER_STATUS + CHNL_MESSAGE_SEP + pin;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
            p_requestVO.setCellId(cellId);
            p_requestVO.setSwitchId(switchId);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
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

            if (BTSLUtil.isNullString(type)) {
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }
            
            if (BTSLUtil.isNullString(msisdn1)) {
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.XML_ERROR_MSISDN_IS_NULL);
            }
            
            if (BTSLUtil.isNullString(pin) ) {
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_BLANK_PIN);
            }
            
            parsedRequestStr = PretupsI.SERVICE_TYPE_LASTX_TRANSFER_REPORT + CHNL_MESSAGE_SEP + pin;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
        } catch (BTSLBaseException e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(e.getMessageKey());
            _log.error(methodName, "Exception e: " + e);
            throw e;          
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("USSDXMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
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
            sbf.append("TYPE=L3TSRESP&");

            if (p_requestVO.isSuccessTxn()) {
                sbf.append("TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS);
            } else {
                sbf.append("TXNSTATUS>").append(p_requestVO.getMessageCode());
            }
            sbf.append("&MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "USSDXMLStringParser[generateChannelLastTransferStatusResponse]", PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "",
                "generateChannelLastTransferStatusResponse:" + e.getMessage());
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
            sbf.append("TYPE=CUSTRESP&");

            if (p_requestVO.isSuccessTxn()) {
                sbf.append("TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS);
            } else {
                sbf.append("TXNSTATUS>").append(p_requestVO.getMessageCode());
            }
            sbf.append("&MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "USSDXMLStringParser[generateChannelLastTransferStatusResponse]", PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "",
                "generateChannelLastTransferStatusResponse:" + e.getMessage());
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

            if (BTSLUtil.isNullString(type)) {
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }
            
            if (BTSLUtil.isNullString(msisdn1)) {
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.XML_ERROR_MSISDN_IS_NULL);
            }
            
            if (BTSLUtil.isNullString(pin) ) {
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_BLANK_PIN);
            }
            
            if (BTSLUtil.isNullString(msisdn2)) {
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.CHNL_ERROR_RECR_MSISDN_BLANK);
            }

            parsedRequestStr = PretupsI.SERVICE_TYPE_CUSTX_ENQUIRY + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + pin;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);

        } catch (BTSLBaseException e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(e.getMessageKey());
            _log.error(methodName, "Exception e: " + e);
            throw e;          
        }
        
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("USSDXMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
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
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }
            parsedRequestStr = PretupsI.MVD_VOUCHER_DOWNLOAD + CHNL_MESSAGE_SEP + msisdn1 + CHNL_MESSAGE_SEP + denominationAmount + CHNL_MESSAGE_SEP + requestedQuantity + CHNL_MESSAGE_SEP + pin;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);

        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseChannelLastXTransferStatus", "Exception e: " + e);
            throw new BTSLBaseException("USSDXMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
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
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }
            parsedRequestStr = PretupsI.ADD_CHNL_USER + CHNL_MESSAGE_SEP + userMsisdn + CHNL_MESSAGE_SEP + userName + CHNL_MESSAGE_SEP + loginId + CHNL_MESSAGE_SEP + catCode + CHNL_MESSAGE_SEP + parentMsisdn + CHNL_MESSAGE_SEP + pin;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);

        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("USSDXMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
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
            sbf.append("TYPE=ADDCHUSRRESP&");

            if (p_requestVO.isSuccessTxn()) {
                sbf.append("TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS);
            } else {
                sbf.append("TXNSTATUS>").append(p_requestVO.getMessageCode());
            }
            sbf.append("&MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "USSDXMLStringParser[generateChannelLastTransferStatusResponse]", PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "",
                "generateChannelLastTransferStatusResponse:" + e.getMessage());
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
        final String MSG_IDNTFICATION_BOTH = "BOTH";
        final String MSG_IDNTFICATION_TXN_ID = "TXN";
        final String MSG_IDNTFICATION_EXT_REF = "EXT";
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            final HashMap requestMap = BTSLUtil.getStringToHash(requestStr, "&", "=");
            final String type = (String) requestMap.get("TYPE");
            requestMap.put("TYPE", type);
            String date = (String) requestMap.get("DATE");
            date = BTSLDateUtil.getGregorianDateInString(date);
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

            parsedRequestStr = PretupsI.ENQ_TXN_ID + CHNL_MESSAGE_SEP + txnID + CHNL_MESSAGE_SEP + pin;
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language));
            p_requestVO.setRequestMSISDN(msisdn);
            p_requestVO.setExternalNetworkCode(extNwCode);
            p_requestVO.setSenderExternalCode(extCode);
            p_requestVO.setSenderLoginID(loginId);
            p_requestVO.setRequestMap(requestMap);

            if (!BTSLUtil.isNullString(txnID)) {
                parsedRequestStr = PretupsI.ENQ_TXN_ID + CHNL_MESSAGE_SEP + txnID + CHNL_MESSAGE_SEP + MSG_IDNTFICATION_TXN_ID + CHNL_MESSAGE_SEP + pin;
            }

            p_requestVO.setDecryptedMessage(parsedRequestStr);

        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseChannelExtRechargeStatusRequest", "Exception e: " + e);
            throw new BTSLBaseException("USSDStringParser", "parseChannelExtRechargeStatusRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
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
            sbf.append("TYPE=TXNENQRESP&");

            if (p_requestVO.isSuccessTxn()) {
                sbf.append("TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS);
            } else {
                sbf.append("TXNSTATUS>").append(p_requestVO.getMessageCode());
            }
            sbf.append("&MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "USSDStringParser[generateChannelExtRechargeStatusResponse]", PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateChannelExtRechargeStatusResponse:" + e
                    .getMessage());
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
        boolean ussdNewTagsMandatory = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY);
        String countryCode = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.COUNTRY_CODE);
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            final HashMap requestMap = BTSLUtil.getStringToHash(requestStr, "&", "=");

            final String type = (String) requestMap.get(TYPE_STR);
            final String msisdn1 = (String) requestMap.get(MSISDN1_STR);
            String msisdn2 = (String) requestMap.get(MSISDN2_STR);
            if (countryCode != null) {
                // append the country code before the msisdn for give me balance
                // //rahul
                msisdn2 = BTSLUtil.addCountryCodeToMSISDN(msisdn2);
            }
            final String amount = (String) requestMap.get(AMOUNT_STR);
            // amount field should be mandatory
            if (BTSLUtil.isNullString(amount)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_BLANK_AMOUNT);
                _log.error(methodName, "Amount field is null");
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.P2P_ERROR_BLANK_AMOUNT);
            }

            String language1 = (String) requestMap.get(LANG1_STR);
            String language2 = (String) requestMap.get(LANG2_STR);
            final String ussdSessionID = (String) requestMap.get(USSD_SESSION_ID);

            cellId = (String) requestMap.get(CELLID_STR);
            switchId = (String) requestMap.get(SWITCHID_STR);
            tagsMandatory = ussdNewTagsMandatory;
            if (tagsMandatory) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        p_requestVO.setMessageCode(PretupsErrorCodesI.USSD_CELLID_BLANK_ERROR);
                        throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.USSD_CELLID_BLANK_ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        p_requestVO.setMessageCode(PretupsErrorCodesI.USSD_SWITCHID_BLANK_ERROR);
                        throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.USSD_SWITCHID_BLANK_ERROR);
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
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.P2PGMB_TYPE_BLANK);
            }
            if (BTSLUtil.isNullString(msisdn1)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.P2PGMB_MSISDN1_BLANK);
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.P2PGMB_MSISDN1_BLANK);
            }
            if (BTSLUtil.isNullString(msisdn2)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.P2PGMB_MSISDN2_BLANK);
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.P2PGMB_MSISDN2_BLANK);
            }
            if (!BTSLUtil.isNullString(msisdn1) && !BTSLUtil.isNullString(msisdn2) && msisdn1.equals(msisdn2)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.P2PGMB_MSISDN1_MSISDN2_EQUAL);
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.P2PGMB_MSISDN1_MSISDN2_EQUAL);
            }
            parsedRequestStr = PretupsI.SERVICE_TYPE_GIVE_ME_BALANCE + P2P_MESSAGE_SEP + msisdn2 + P2P_MESSAGE_SEP + amount;
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
            throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.P2PGMB_INVALID_MESSAGE);
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
                sbf.append("&" + TXNID_STR + ">").append(_blank);
            } else {
                sbf.append("&" + TXNID_STR + ">").append(p_requestVO.getTransactionID());
            }
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("&" + TXNSTATUS_STR + ">").append(PretupsI.TXN_STATUS_SUCCESS);
            } else {
                sbf.append("&" + TXNSTATUS_STR + ">").append(p_requestVO.getMessageCode());
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "USSDStringParser[generateP2PGiveMeBalanceResponse]",
                PretupsErrorCodesI.P2P_ERROR_EXCEPTION, "", "", "generateP2PGiveMeBalanceResponse:" + e.getMessage());
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
        boolean ussdNewTagsMandatory = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY);
        String p2pDefaultSmsPin = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN));
        String defFrcXmlSelP2p = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEF_FRCXML_SEL_P2P);
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
                throw new BTSLBaseException("USSDStringParser", "parseGetAccountInfoRequest", PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
            }

            // amount field should be mandatory
            if (BTSLUtil.isNullString(amount)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_BLANK_AMOUNT);
                _log.error(methodName, "Amount field is null");
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.P2P_ERROR_BLANK_AMOUNT);
            }
            // added by sanjay to set default pin
            if (BTSLUtil.isNullString(pin)) {
                // pin=PretupsI.DEFAULT_P2P_PIN;
                pin = p2pDefaultSmsPin;
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

            if (!BTSLUtil.isNullString(defFrcXmlSelP2p)) {
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "using default force selecter of XML");
                }
                selector = "" + defFrcXmlSelP2p;
            }
            tagsMandatory = ussdNewTagsMandatory;
            if (tagsMandatory) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.USSD_CELLID_BLANK_ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.USSD_SWITCHID_BLANK_ERROR);
                    }
                } catch (BTSLBaseException btsle) {
                    throw btsle;
                }
            }
            parsedRequestStr = PretupsI.SERVICE_TYPE_P2PRECHARGE + P2P_MESSAGE_SEP + msisdn2 + P2P_MESSAGE_SEP + amount + (P2P_MESSAGE_SEP + selector) + (P2P_MESSAGE_SEP + language2) + P2P_MESSAGE_SEP + pin;
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
            throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
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
                sbf.append("&" + TXNID_STR + ">").append(_blank);
            } else {
                sbf.append("&" + TXNID_STR + ">").append(p_requestVO.getTransactionID());
            }
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("&" + TXNSTATUS_STR + ">").append(PretupsI.TXN_STATUS_SUCCESS);
            } else {
                sbf.append("&" + TXNSTATUS_STR + ">").append(p_requestVO.getMessageCode());
            }

            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "USSDStringParser[generateCreditTransferResponse]",
                PretupsErrorCodesI.P2P_ERROR_EXCEPTION, "", "", "generateCreditTransferResponse:" + e.getMessage());
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
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }
            parsedRequestStr = PretupsI.SERVICE_TYPE_HLPDSK_REQUEST + CHNL_MESSAGE_SEP + msisdn1 + CHNL_MESSAGE_SEP + pin;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);

        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseChannelLastXTransferStatus", "Exception e: " + e);
            throw new BTSLBaseException("USSDXMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
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
            sbf.append("TYPE=HLPDSKRSP&");

            if (p_requestVO.isSuccessTxn()) {
                sbf.append("TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS);
            } else {
                sbf.append("TXNSTATUS>").append(p_requestVO.getMessageCode());
            }
            sbf.append("&MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "USSDXMLStringParser[generateChannelHelpDeskResponse]",
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
        String defFrcXmlSelBillPay = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEF_FRCXML_SEL_BILLPAY);
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

            if (!BTSLUtil.isNullString(defFrcXmlSelBillPay)) {
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "using default force selecter of XML");
                }
                selector = "" + defFrcXmlSelBillPay;
            }

            parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_BILLPAY + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + amount + CHNL_MESSAGE_SEP + selector + CHNL_MESSAGE_SEP + language2 + CHNL_MESSAGE_SEP + pin;

            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setReqSelector(selector);
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
            p_requestVO.setUssdSessionID(ussdSessionID);
        }

        catch (Exception e) {
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.BILLPAY_INVALID_MESSAGE_FORMAT);
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
            sbf.append("TYPE=PPBTRFRESP");

            if (p_requestVO.isSuccessTxn()) {
                sbf.append("TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS);
            } else {
                sbf.append("TXNSTATUS>").append(p_requestVO.getMessageCode());
            }
            sbf.append("&MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "USSDXMLStringParser[generateChannelPostPaidBillPaymentResponse]", PretupsErrorCodesI.C2S_ERROR_EXCEPTION, "", "",
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
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
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
                    throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
                }
            }

            if (tagsMandatory) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
                    }
                } catch (BTSLBaseException btsle) {
                    throw btsle;
                }
            }

            final int amount = 0;

            parsedRequestStr = PretupsI.COLLECTION_ENQUIRY + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + amount + CHNL_MESSAGE_SEP + selector + CHNL_MESSAGE_SEP + language1 + CHNL_MESSAGE_SEP + language2 + CHNL_MESSAGE_SEP + pin;
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
            throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
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
            sbf.append("TYPE=COLENQRESP&");
            if (BTSLUtil.isNullString(p_requestVO.getTransactionID())) {
                sbf.append("TXNID=&");
            } else {
                sbf.append("TXNID>").append(p_requestVO.getTransactionID()).append("&");
            }
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS);
            } else {
                String message = p_requestVO.getMessageCode();
                if (message.indexOf("_") != -1) {
                    message = message.substring(0, message.indexOf("_"));
                }
                sbf.append("TXNSTATUS>").append(message);
            }
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("&MESSAGE>").append(getMessage(p_requestVO.getLocale(), "UNDERPROCESS", p_requestVO.getMessageArguments()));
            } else {
                sbf.append("&MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            }
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "USSDStringParser[generateChannelCollectionEnquiryResponse]", PretupsErrorCodesI.C2S_ERROR_EXCEPTION, "", "", "generateChannelCreditTransferResponse:" + e
                    .getMessage());
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
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
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
                    throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
                }
            }

            if (tagsMandatory) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
                    }
                } catch (BTSLBaseException btsle) {
                    throw btsle;
                }
            }
            p_requestVO.setCellId(cellId);
            p_requestVO.setSwitchId(switchId);
            parsedRequestStr = PretupsI.COLLECTION_BILLPAYMENT + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + amount + CHNL_MESSAGE_SEP + selector + CHNL_MESSAGE_SEP + language1 + CHNL_MESSAGE_SEP + language2 + CHNL_MESSAGE_SEP + pin;
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setReqSelector(selector);
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseChannelCreditTransferRequest", "Exception e: " + e);
            throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
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
            sbf.append("TYPE=COLBPRESP&");
            if (BTSLUtil.isNullString(p_requestVO.getTransactionID())) {
                sbf.append("TXNID=&");
            } else {
                sbf.append("TXNID>").append(p_requestVO.getTransactionID()).append("&");
            }
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS);
            } else {
                String message = p_requestVO.getMessageCode();
                if (message.indexOf("_") != -1) {
                    message = message.substring(0, message.indexOf("_"));
                }
                sbf.append("TXNSTATUS>").append(message);
            }
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("&MESSAGE>").append(getMessage(p_requestVO.getLocale(), "UNDERPROCESS", p_requestVO.getMessageArguments()));
            } else {
                sbf.append("&MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            }
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "USSDStringParser[generateChannelCollectionEnquiryResponse]", PretupsErrorCodesI.C2S_ERROR_EXCEPTION, "", "", "generateChannelCreditTransferResponse:" + e
                    .getMessage());
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
        String defFrcXmlSelC2S = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEF_FRCXML_SEL_C2S);
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
            final String currencyCode = (String) requestMap.get(CURRENCY_STR);
            final String pin = (String) requestMap.get("PIN");
            cellId = (String) requestMap.get(CELLID_STR);
            switchId = (String) requestMap.get(SWITCHID_STR);

            if (BTSLUtil.isNullString(type) || BTSLUtil.isNullString(msisdn1) || BTSLUtil.isNullString(pin) || BTSLUtil.isNullString(msisdn2) || BTSLUtil.isNullString(amount) || BTSLUtil
                .isNullString(selector)) {
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }
            if (BTSLUtil.isNullString(selector)) {
                String selectorAction = "";

                if (actionId == ParserUtility.ACTION_DTH) {
                    selectorAction = PretupsI.INTERFACE_CATEGORY_DTH;
                } else if (actionId == ParserUtility.ACTION_CHNL_EXT_CREDIT_TRANSFER) {
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
                else if (actionId == ParserUtility.ACTION_MULTICURRENCY_RECHARGE) {
                    selectorAction = PretupsI.MULTI_CURRENCY_SERVICE_TYPE;
                }
                final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(selectorAction);
                if (serviceSelectorMappingVO != null) {
                    selector = serviceSelectorMappingVO.getSelectorCode();
                }
            }
            // if we have to forcibaly change the value of default selecter in
            // case of ussd request.
            if (!BTSLUtil.isNullString(defFrcXmlSelC2S)) {
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "using default force selecter of XML");
                }
                selector = "" + defFrcXmlSelC2S;
            }
            if (!BTSLUtil.isNullString(p_requestVO.getMessageGatewayVO().getGatewayCode()) && ("EXTGW".equals(p_requestVO.getMessageGatewayVO().getGatewayCode()))) {
                externalNetworkCode = (String) requestMap.get(EXTNWCODE_STR);
                if (BTSLUtil.isNullString(externalNetworkCode)) {
                    throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
                }
            }

            if (tagsMandatory) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
                    }
                } catch (BTSLBaseException btsle) {
                    throw btsle;
                }
            }

            if (actionId == ParserUtility.ACTION_DTH) {
                parsedRequestStr = PretupsI.INTERFACE_CATEGORY_DTH + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + amount + CHNL_MESSAGE_SEP + selector + CHNL_MESSAGE_SEP + language1 + CHNL_MESSAGE_SEP + language2 + CHNL_MESSAGE_SEP + pin;
            } else if (actionId == ParserUtility.ACTION_CHNL_EXT_CREDIT_TRANSFER) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_RECHARGE + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + amount + CHNL_MESSAGE_SEP + selector + CHNL_MESSAGE_SEP + language1 + CHNL_MESSAGE_SEP + language2 + CHNL_MESSAGE_SEP + pin;
            } else if (actionId == ParserUtility.ACTION_DC) {
                parsedRequestStr = PretupsI.INTERFACE_CATEGORY_DATACARD + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + amount + CHNL_MESSAGE_SEP + selector + CHNL_MESSAGE_SEP + language1 + CHNL_MESSAGE_SEP + language2 + CHNL_MESSAGE_SEP + pin;
            } else if (actionId == ParserUtility.ACTION_PIN) {
                parsedRequestStr = PretupsI.INTERFACE_CATEGORY_PIN + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + amount + CHNL_MESSAGE_SEP + selector + CHNL_MESSAGE_SEP + language1 + CHNL_MESSAGE_SEP + language2 + CHNL_MESSAGE_SEP + pin;
            } else if (actionId == ParserUtility.ACTION_PMD) {
                parsedRequestStr = PretupsI.INTERFACE_CATEGORY_PMD + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + amount + CHNL_MESSAGE_SEP + selector + CHNL_MESSAGE_SEP + language1 + CHNL_MESSAGE_SEP + language2 + CHNL_MESSAGE_SEP + pin;
            } else if (actionId == ParserUtility.ACTION_BPB) {
                parsedRequestStr = PretupsI.INTERFACE_CATEGORY_BPB + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + amount + CHNL_MESSAGE_SEP + selector + CHNL_MESSAGE_SEP + language1 + CHNL_MESSAGE_SEP + language2 + CHNL_MESSAGE_SEP + pin;
            } else if (actionId == ParserUtility.ACTION_FLRC) {
                parsedRequestStr = PretupsI.INTERFACE_CATEGORY_FLRC + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + amount + CHNL_MESSAGE_SEP + selector + CHNL_MESSAGE_SEP + language1 + CHNL_MESSAGE_SEP + language2 + CHNL_MESSAGE_SEP + pin;
            }
            else if (actionId == ParserUtility.ACTION_MULTICURRENCY_RECHARGE) {
            	 parsedRequestStr = PretupsI.MULTI_CURRENCY_SERVICE_TYPE + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + amount + CHNL_MESSAGE_SEP + selector + CHNL_MESSAGE_SEP + language1 + CHNL_MESSAGE_SEP + language2 + CHNL_MESSAGE_SEP + pin + CHNL_MESSAGE_SEP + currencyCode;
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
            throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
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
            if (actionId == ParserUtility.ACTION_DTH) {
                sbf.append("TYPE=DTHTRFRESP&");
            } else if (actionId == ParserUtility.ACTION_CHNL_EXT_CREDIT_TRANSFER) {
                sbf.append("TYPE=RCTRFRESP&");
            } else if (actionId == ParserUtility.ACTION_DC) {
                sbf.append("TYPE=DCTRFRESP&");
            } else if (actionId == ParserUtility.ACTION_BPB) {
                sbf.append("TYPE=RPBTRFRESP&");
            } else if (actionId == ParserUtility.ACTION_PIN) {
                sbf.append("TYPE=PINTRFRESP&");
            } else if (actionId == ParserUtility.ACTION_FLRC) {
                sbf.append("TYPE=FLRCTRFRESP&");
            } else if (actionId == ParserUtility.ACTION_PMD) {
                sbf.append("TYPE=PMDTRFRESP&");
            }
            else if (actionId == ParserUtility.ACTION_MULTICURRENCY_RECHARGE){
            	sbf.append("TYPE=MCRTRFRESP&");
            }

            if (BTSLUtil.isNullString(p_requestVO.getTransactionID())) {
                sbf.append("TXNID=&");
            } else {
                sbf.append("TXNID>").append(p_requestVO.getTransactionID()).append("&");
            }
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS);
            } else {
                String message = p_requestVO.getMessageCode();
                if (message.indexOf("_") != -1) {
                    message = message.substring(0, message.indexOf("_"));
                }
                sbf.append("TXNSTATUS>").append(message);
            }
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("&MESSAGE>").append(getMessage(p_requestVO.getLocale(), "UNDERPROCESS", p_requestVO.getMessageArguments()));
            } else {
                sbf.append("&MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            }
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "USSDStringParser[generateC2STransferResponse]",
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
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
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
                    throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
                }
            }

            if (tagsMandatory) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
                    }
                } catch (BTSLBaseException btsle) {
                    throw btsle;
                }
            }
            final int amount = 0;
            parsedRequestStr = PretupsI.COLLECTION_CANCELATION + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + amount + CHNL_MESSAGE_SEP + selector + CHNL_MESSAGE_SEP + language1 + CHNL_MESSAGE_SEP + language2 + CHNL_MESSAGE_SEP + pin + CHNL_MESSAGE_SEP + txnid;
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
            throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
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
            sbf.append("TYPE=COLCCNRESP&");
            if (BTSLUtil.isNullString(p_requestVO.getTransactionID())) {
                sbf.append("TXNID=&");
            } else {
                sbf.append("TXNID>").append(p_requestVO.getTransactionID()).append("&");
            }
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS);
            } else {
                String message = p_requestVO.getMessageCode();
                if (message.indexOf("_") != -1) {
                    message = message.substring(0, message.indexOf("_"));
                }
                sbf.append("TXNSTATUS>").append(message);
            }
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("&MESSAGE>").append(getMessage(p_requestVO.getLocale(), "UNDERPROCESS", p_requestVO.getMessageArguments()));
            } else {
                sbf.append("&MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            }
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "USSDStringParser[generateC2SPostPaidReversalResponse]", PretupsErrorCodesI.C2S_ERROR_EXCEPTION, "", "", "generateChannelCollectionCancallationResponse:" + e
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
                sbf.append("TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS);
            } else {
                sbf.append("TXNSTATUS>").append(p_requestVO.getMessageCode());
            }
            sbf.append("&TOTALAMT>").append(p_requestVO.getDueAmt());
            sbf.append("&MINAMT>").append(p_requestVO.getMinAmtDue());
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "USSDXMLStringParser[generateChannelLastTransferStatusResponse]", PretupsErrorCodesI.C2S_ERROR_EXCEPTION, "", "",
                "generateChannelLastTransferStatusResponse:" + e.getMessage());
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
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }
            if (lang1 != null && lang2 != null) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_PPB_ENQUIRY + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + lang1 + CHNL_MESSAGE_SEP + lang2 + CHNL_MESSAGE_SEP + pin;
            } else if (lang1 != null) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_PPB_ENQUIRY + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + lang1 + CHNL_MESSAGE_SEP + pin;
            } else {
                parsedRequestStr = PretupsI.SERVICE_TYPE_PPB_ENQUIRY + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + pin;
            }
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);

        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("USSDXMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }
    
    
    public static void parseChannelPrivateRechargeDelRequest(RequestVO p_requestVO) throws Exception {
        final String methodName = "parseChannelPrivateRechargeDelRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        String cellId = null;
        String switchId = null;
        boolean tagsMandatory;
        boolean ussdNewTagsMandatory = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY);
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            final HashMap requestMap = BTSLUtil.getStringToHash(requestStr, "&", "=");
            final String type = (String) requestMap.get("TYPE");
            final String msisdn1 = (String) requestMap.get("MSISDN1");
            final String sid = (String) requestMap.get("SID");
            final String lang1 = (String) requestMap.get("LANGUAGE1");
            // added for ussd changes

            cellId = (String) requestMap.get(CELLID_STR);

            switchId = (String) requestMap.get(SWITCHID_STR);

            tagsMandatory = ussdNewTagsMandatory;
            if (tagsMandatory) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
                    }
                } catch (BTSLBaseException btsle) {
                    throw btsle;
                }
            }
           if (BTSLUtil.isNullString(type) || BTSLUtil.isNullString(msisdn1) || BTSLUtil.isNullString(sid)) {
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }

            parsedRequestStr = PretupsI.SERVICE_TYPE_SID_DELETE + CHNL_MESSAGE_SEP + msisdn1 + CHNL_MESSAGE_SEP + sid;
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(lang1));
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
            p_requestVO.setCellId(cellId);
            p_requestVO.setSwitchId(switchId);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }
    
    public static void generateChannelPrivateRechargeDelResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateChannelPrivateRechargeDelResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("TYPE=DELSIDRESP"+P2P_USSD_RESP_SEP); 
            if (BTSLUtil.isNullString(p_requestVO.getTransactionID())) {
                sbf.append("TXNID="+P2P_USSD_RESP_SEP);
            } else {
                sbf.append("TXNID>").append(p_requestVO.getTransactionID() + P2P_USSD_RESP_SEP);
            }
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS);
            } else {
                String message = p_requestVO.getMessageCode();
                if (message.indexOf("_") != -1) {
                    message = message.substring(0, message.indexOf("_"));
                }
                sbf.append("TXNSTATUS>").append(message+P2P_USSD_RESP_SEP);
            }
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("MESSAGE>").append(getMessage(p_requestVO.getLocale(), "UNDERPROCESS", p_requestVO.getMessageArguments()));
            } else {
                sbf.append("MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            }
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error("generateCreditTransferResponse", "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "USSDStringParser[generateChannelCreditTransferResponse]", PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateChannelCreditTransferResponse:" + e
                    .getMessage());
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
	public static void parseChannelUserAuthRequest(RequestVO requestVO) throws BTSLBaseException
	{
		final String methodName = "parseChannelUserAuthRequest";
	       if (_log.isDebugEnabled()) {
	           _log.debug(methodName, "Entered requestVO: " + requestVO.toString());
	       }
	       
		String parsedRequestStr =null;
		try
		{
			Boolean PIN_VALIDATATION_IN_USSD = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PIN_VALIDATATION_IN_USSD);
			String requestStr=requestVO.getRequestMessage();
			HashMap requestMap=BTSLUtil.getStringToHash(requestStr,"&","=");
			String extRefNum=(String)requestMap.get(EXTREFNUM_STR);
			String msisdn=(String)requestMap.get(MSISDN1_STR);
			String pin=(String)requestMap.get(PIN_STR);
			String ussdSessionID=(String)requestMap.get(USSD_SESSION_ID);
			if(PIN_VALIDATATION_IN_USSD)
			{
				if(BTSLUtil.isNullString(msisdn)||BTSLUtil.isNullString(pin))
				{
					throw new BTSLBaseException("USSDStringParser",methodName,PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT);
				}
			}
			else if(BTSLUtil.isNullString(msisdn))
				{
					throw new BTSLBaseException("USSDStringParser",methodName,PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT);
				}
			parsedRequestStr=PretupsI.SERVICE_TYPE_USER_AUTH;
			requestVO.setDecryptedMessage(parsedRequestStr);
			requestVO.setRequestMap(requestMap);
			requestVO.setRequestMSISDN(msisdn);
			requestVO.setExternalReferenceNum(extRefNum);
			requestVO.setUssdSessionID(ussdSessionID);
		}
		catch(Exception e)
		{
			_log.errorTrace(methodName, e);
			requestVO.setTxnAuthStatus(PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT);
			requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
			_log.error(methodName,"Exception e: "+e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"USSDStringParser[parseChannelUserAuthRequest]",PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT,"","","parseChannelUserAuthRequest:"+e.getMessage());
			throw new BTSLBaseException("USSDStringParser",methodName,PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT);
		}
		finally
		{
			if (_log.isDebugEnabled()) {
	               _log.debug(methodName, "Exiting requestVO: " + requestVO.toString());
	           }
		}
	}
   /**
    * @author zeeshan.aleem
    * @param p_requestVO
    * @throws Exception
    */
   public static void generateUserAuthorizationResponse(RequestVO requestVO) throws BTSLBaseException {
       final String methodName = "generateUserAuthorizationResponse";
       if (_log.isDebugEnabled()) {
           _log.debug(methodName, "Entered requestVO: " + requestVO.toString());
       }
       String responseStr = null;
       StringBuilder sbf = null;
       try {
           sbf = new StringBuilder(1024);
           sbf.append(getMessage(requestVO.getLocale(), requestVO.getMessageCode(), requestVO.getMessageArguments()));
           responseStr = sbf.toString();
           requestVO.setSenderReturnMessage(responseStr);
       } catch (Exception e) {
           _log.errorTrace(methodName, e);
           _log.error(methodName, "Exception e: " + e);
           requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
           EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateUserAuthorizationResponse]",
               PretupsErrorCodesI.C2S_ERROR_EXCEPTION, "", "", "generateUserAuthorizationResponse:" + e.getMessage());
           throw new BTSLBaseException("USSDStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
       } finally {
           if (_log.isDebugEnabled()) {
               _log.debug(methodName, "Exiting responseStr: " + responseStr);
           }
       }
   }
    
    public static void parseLastXTrfRequest(RequestVO requestVO) throws BTSLBaseException {
        final String methodName = "parseLastXTrfRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + requestVO.toString());
        }
        String parsedRequestStr = null;
        try {
            final String requestStr = requestVO.getRequestMessage();
            final HashMap requestMap = BTSLUtil.getStringToHash(requestStr, "&", "=");
            final String type = (String) requestMap.get("TYPE");
            final String msisdn1 = (String) requestMap.get("MSISDN1");
            final String pin = (String) requestMap.get("PIN");
            final String txnType = (String) requestMap.get("TXNTYPE");
            final String subTxnType = (String) requestMap.get("TXNSUBTYPE");
            String c2cInOut = (String) requestMap.get("C2CINOUT");
            if(BTSLUtil.isNullString(c2cInOut)){
            	c2cInOut="OUT";
            }
            requestVO.setRequestMap(requestMap);
            
            if (BTSLUtil.isNullString(type) || BTSLUtil.isNullString(msisdn1) || BTSLUtil.isNullString(txnType) || BTSLUtil.isNullString(subTxnType)) {
                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }
            if( !(txnType.equals(PretupsI.TRANSFER_TYPE_O2C) || txnType.equals(PretupsI.TRANSFER_TYPE_C2C) || txnType.equals(PretupsI.TRANSFER_TYPE_C2S) || txnType.equals(PretupsI.TRANSFER_TYPE_FOC))){
            	throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.CCE_ERROR_INVALID_TRANSFER_TYPE);
            }
         
            if( !(subTxnType.equals(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER) || subTxnType.equals(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN) || subTxnType.equals(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW)
            		|| subTxnType.equals(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_AUTO_RETURN) || subTxnType.equals(PretupsI.TRANSFER_TYPE_REVERSE_SUB_TYPE) )){
            	throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.CCE_ERROR_INVALID_TRANSFER_SUB_TYPE);
            }
            
            if(!BTSLUtil.isNullString(pin)){
            parsedRequestStr = PretupsI.SERVICE_TYPE_LAST_X_TRF_REQ + CHNL_MESSAGE_SEP + msisdn1 + CHNL_MESSAGE_SEP + pin +CHNL_MESSAGE_SEP +txnType +CHNL_MESSAGE_SEP+ subTxnType+CHNL_MESSAGE_SEP+c2cInOut.trim().toUpperCase();
            }else{
            parsedRequestStr = PretupsI.SERVICE_TYPE_LAST_X_TRF_REQ + CHNL_MESSAGE_SEP + msisdn1 +CHNL_MESSAGE_SEP +txnType +CHNL_MESSAGE_SEP+ subTxnType+CHNL_MESSAGE_SEP+c2cInOut.trim().toUpperCase();
            }
            requestVO.setDecryptedMessage(parsedRequestStr);
            requestVO.setRequestMSISDN(msisdn1);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO ::" + requestVO.toString());
            }
        }
    }
    public static void generateLastXTrfResponse(RequestVO requestVO) throws Exception {
        final String methodName = "generateLastXTrfResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO:: " + requestVO.toString());
        }
        String responseStr = null;
        StringBuilder sbf = null;
        try {
            sbf = new StringBuilder(1024);
            sbf.append("TYPE=LASTXTRFRES&");

            if (requestVO.isSuccessTxn()) {
                sbf.append("TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS);
            } else {
                sbf.append("TXNSTATUS>").append(requestVO.getMessageCode());
            }
            sbf.append("&MESSAGE>").append(getMessage(requestVO.getLocale(), requestVO.getMessageCode(), requestVO.getMessageArguments()));
            responseStr = sbf.toString();
            requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "USSDStringParser[generateLastXTrfResponse]", PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "",
                "generateLastXTrfResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr:: " + responseStr);
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
    		int MIN_MSISDN_LENGTH = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH);
    		final String requestStr = p_requestVO.getRequestMessage();
    		final HashMap requestMap = BTSLUtil.getStringToHash(requestStr, "&", "=");
    		final String type = (String) requestMap.get("TYPE");            
    		final String msisdn2 = (String) requestMap.get("MSISDN2");
    		final String msisdn1 = (String) requestMap.get("MSISDN1");
    		final String amount = (String) requestMap.get("AMOUNT");
    		final String language1 = (String) requestMap.get(LANG1_STR);            
    		final String ussdSessionID = (String) requestMap.get(USSD_SESSION_ID);
    		final String pin = (String) requestMap.get("PIN");
    		final String countrycode = (String) requestMap.get("COUNTRYCODE");
    		cellId = (String) requestMap.get(CELLID_STR);
            switchId = (String) requestMap.get(SWITCHID_STR);
            
    		if (BTSLUtil.isNullString(type) || BTSLUtil.isNullString(msisdn1) || BTSLUtil.isNullString(pin) || BTSLUtil.isNullString(msisdn2)) {
    			throw new BTSLBaseException("USSDStringParser::", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
    		}
    		if (!BTSLUtil.isNullString(p_requestVO.getMessageGatewayVO().getGatewayCode()) && ("EXTGW".equals(p_requestVO.getMessageGatewayVO().getGatewayCode()))) {
    			externalNetworkCode = (String) requestMap.get(EXTNWCODE_STR);
    			if (BTSLUtil.isNullString(externalNetworkCode)) {
    				throw new BTSLBaseException("USSDStringParser:", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
    			}
    		}
    		if (tagsMandatory) {
    			try {
    				if (BTSLUtil.isNullString(cellId)) {
    					throw new BTSLBaseException("USSDStringParser ", methodName, PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
    				}
    				if (BTSLUtil.isNullString(switchId)) {
    					throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
    				}
    			} catch (BTSLBaseException btsle) {
    				throw btsle;
    			}
    		}
    		String tempMSISDN2=msisdn2;
    		if (msisdn2.length() > MIN_MSISDN_LENGTH) {
    			if(!BTSLUtil.isNullString(countrycode)){
    				if (msisdn2.startsWith(countrycode, 0)) {
    					tempMSISDN2 = tempMSISDN2.substring(countrycode.length());
    				}
    			}
    		}
    		if(BTSLUtil.isNullString(countrycode))
    			parsedRequestStr = PretupsI.IAT_SERVICE_TYPE_ROAM_RECHARGE + CHNL_MESSAGE_SEP + tempMSISDN2 + CHNL_MESSAGE_SEP + amount + CHNL_MESSAGE_SEP + pin + CHNL_MESSAGE_SEP ;
    		else
    			parsedRequestStr = PretupsI.IAT_SERVICE_TYPE_ROAM_RECHARGE + CHNL_MESSAGE_SEP + countrycode + tempMSISDN2 + CHNL_MESSAGE_SEP + amount + CHNL_MESSAGE_SEP + pin + CHNL_MESSAGE_SEP ;
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
    		throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
    	} finally {
    		if (_log.isDebugEnabled()) {
    			_log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
    		}
    	}
    }
    public static void generateRoamRechargeResponse(RequestVO p_requestVO) throws Exception {
    	final String methodName = "generateRoamRechargeResponse";
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
    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroUSSDStringParser[generateC2STransferResponse]",
    				PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateC2STransferResponse:" + e.getMessage());
    	} finally {
    		if (_log.isDebugEnabled()) {
    			_log.debug(methodName, "Exiting responseStr:: " + responseStr);
    		}
    	}
    }
    
    /**
	 * @param p_requestVO
	 * @throws Exception
	 * @author sanjay.bind1
	 * This method is used to parse Vas recharge request incoming through USSDGW
	 */
	public static void parseChannelVasTransferRequest(RequestVO prequestVO) throws Exception
	{
	    final String methodName = "parseChannelVasTransferRequest";
	     
		if(_log.isDebugEnabled())
			_log.debug(methodName,"Entered p_requestVO: "+prequestVO.toString());
		String parsedRequestStr=null;
		boolean tagsMandatory;
		String cellId=null;
		String switchId=null;
		String externalNetworkCode=null;
		try
		{
			Boolean USSD_NEW_TAGS_MANDATORY = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY);
			String requestStr=prequestVO.getRequestMessage();
			HashMap requestMap=BTSLUtil.getStringToHash(requestStr,"&","=");

			String msisdn1=(String)requestMap.get(MSISDN1_STR);

			String pin=(String)requestMap.get(PIN_STR);

			String msisdn2=(String)requestMap.get(MSISDN2_STR);

			String amount=(String)requestMap.get(AMOUNT_STR);

			String language1=(String)requestMap.get(LANG1_STR);

			String language2=(String)requestMap.get(LANG2_STR);

			String selector=(String)requestMap.get(SELECTOR_STR);

			String ussdSessionID=(String)requestMap.get(USSD_SESSION_ID);


		
			cellId=(String)requestMap.get(CELLID_STR);

			switchId=(String)requestMap.get(SWITCHID_STR);

			if(BTSLUtil.isNullString(msisdn1)||BTSLUtil.isNullString(pin)||BTSLUtil.isNullString(msisdn2)||BTSLUtil.isNullString(amount))
			{
				throw new BTSLBaseException("USSDStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
			}

			//selector is mandatory 
			if(BTSLUtil.isNullString(selector))
			{
				throw new BTSLBaseException("USSDStringParser",methodName,PretupsErrorCodesI.VAS_PROMOVAS_REQ_SELECTOR_MISSING);
			}
			/** new added to test c2s using extgw**/
			if(!BTSLUtil.isNullString(prequestVO.getMessageGatewayVO().getGatewayCode())&& (prequestVO.getMessageGatewayVO().getGatewayCode().equals("EXTGW")))
			{
				externalNetworkCode=(String)requestMap.get(EXTNWCODE_STR);
				if(BTSLUtil.isNullString(externalNetworkCode))
				{
					throw new BTSLBaseException("USSDStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
				}
			}
			tagsMandatory=USSD_NEW_TAGS_MANDATORY;
			if(tagsMandatory)
			{
				try{
					if(BTSLUtil.isNullString(cellId))
					{
						throw new BTSLBaseException("USSDStringParser",methodName,PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
					}
					if(BTSLUtil.isNullString(switchId))
					{
						throw new BTSLBaseException("USSDStringParser",methodName,PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
					}
				}catch(BTSLBaseException btsle){
					throw btsle;
				}
			}
			parsedRequestStr=PretupsI.SERVICE_TYPE_VAS_RECHARGE+CHNL_MESSAGE_SEP+msisdn2+CHNL_MESSAGE_SEP+amount+CHNL_MESSAGE_SEP+selector+CHNL_MESSAGE_SEP+language2+CHNL_MESSAGE_SEP+pin;
			prequestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
			prequestVO.setReqSelector(selector);
			prequestVO.setRequestMSISDN(msisdn1);
			prequestVO.setDecryptedMessage(parsedRequestStr);
			prequestVO.setCellId(cellId);
			prequestVO.setSwitchId(switchId);
			prequestVO.setExternalNetworkCode(externalNetworkCode);
			prequestVO.setUssdSessionID(ussdSessionID);
		}
		catch(Exception e)
		{
			_log.errorTrace(methodName,e);
			prequestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
			_log.error(methodName,"Exception e: "+e);
			throw new BTSLBaseException("USSDStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
		}
		finally
		{
			if(_log.isDebugEnabled())
				_log.debug(methodName,"Exiting prequestVO: "+prequestVO.toString());
		}
	}
	
	/**
	 * @param p_requestVO
	 * @throws Exception
	 * @author sanjay.bind1
	 * This method parses the Promo Vas transfer request 
	 */
	public static void parseChannelPrVasTransferRequest(RequestVO prequestVO) throws Exception{
		 final String methodName = "parseChannelPrVasTransferRequest";
		 
		if(_log.isDebugEnabled())
			_log.debug(methodName,"Entered p_requestVO: "+prequestVO.toString());
		  
		String parsedRequestStr=null;
		boolean tagsMandatory;
		String cellId=null;
		String switchId=null;
		String externalNetworkCode=null;
		try
		{
			Boolean USSD_NEW_TAGS_MANDATORY = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY);
			String requestStr=prequestVO.getRequestMessage();
			HashMap requestMap=BTSLUtil.getStringToHash(requestStr,"&","=");

			String msisdn1=(String)requestMap.get(MSISDN1_STR);

			String pin=(String)requestMap.get(PIN_STR);

			String msisdn2=(String)requestMap.get(MSISDN2_STR);

			String amount=(String)requestMap.get(AMOUNT_STR);

			String language1=(String)requestMap.get(LANG1_STR);

			String language2=(String)requestMap.get(LANG2_STR);

			String selector=(String)requestMap.get(SELECTOR_STR);

			String ussdSessionID=(String)requestMap.get(USSD_SESSION_ID);

			cellId=(String)requestMap.get(CELLID_STR);

			switchId=(String)requestMap.get(SWITCHID_STR);

			if(BTSLUtil.isNullString(msisdn1)||BTSLUtil.isNullString(pin)||BTSLUtil.isNullString(msisdn2)||BTSLUtil.isNullString(amount))
			{
				throw new BTSLBaseException("USSDStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
			}

			if(BTSLUtil.isNullString(selector))
			{
				throw new BTSLBaseException("USSDStringParser",methodName,PretupsErrorCodesI.VAS_PROMOVAS_REQ_SELECTOR_MISSING);
			}
			//if we have to forcibaly change the value of  default selecter in case of ussd request.
			/** new added to test c2s using extgw**/
			if(!BTSLUtil.isNullString(prequestVO.getMessageGatewayVO().getGatewayCode())&& (prequestVO.getMessageGatewayVO().getGatewayCode().equals("EXTGW")))
			{
				externalNetworkCode=(String)requestMap.get(EXTNWCODE_STR);
				if(BTSLUtil.isNullString(externalNetworkCode))
				{
					throw new BTSLBaseException("USSDStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
				}
			}
			tagsMandatory=USSD_NEW_TAGS_MANDATORY;
			if(tagsMandatory)
			{
				try{
					if(BTSLUtil.isNullString(cellId))
					{
						throw new BTSLBaseException("USSDStringParser",methodName,PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
					}
					if(BTSLUtil.isNullString(switchId))
					{
						throw new BTSLBaseException("USSDStringParser",methodName,PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
					}
				}catch(BTSLBaseException btsle){
					throw btsle;
				}
			}
			parsedRequestStr=PretupsI.SERVICE_TYPE_PVAS_RECHARGE+CHNL_MESSAGE_SEP+msisdn2+CHNL_MESSAGE_SEP+amount+CHNL_MESSAGE_SEP+selector+CHNL_MESSAGE_SEP+language2+CHNL_MESSAGE_SEP+pin;
			prequestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
			prequestVO.setReqSelector(selector);
			prequestVO.setDecryptedMessage(parsedRequestStr);
			prequestVO.setRequestMSISDN(msisdn1);
			prequestVO.setCellId(cellId);
			prequestVO.setSwitchId(switchId);
			prequestVO.setExternalNetworkCode(externalNetworkCode);
			prequestVO.setUssdSessionID(ussdSessionID);
		}
		catch(Exception e)
		{
			_log.errorTrace(methodName, e);
			prequestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
			_log.error(methodName,"Exception e: "+e);
			throw new BTSLBaseException("USSDStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
		}
		finally
		{
			if(_log.isDebugEnabled())
				_log.debug(methodName,"Exiting p_requestVO: "+prequestVO.toString());
		}
	}

	//added for wireless recharge for plain ussd request
	   public static void parseChannelWirelessRCTransferRequest(RequestVO p_requestVO) throws Exception{

		   final String methodName = "parseChannelWirelessRCTransferRequest";
		     
			if(_log.isDebugEnabled())
				_log.debug(methodName,"Entered p_requestVO: "+p_requestVO.toString());
			String parsedRequestStr=null;
			boolean tagsMandatory;
			String cellId=null;
			String switchId=null;
			String externalNetworkCode=null;
			try
			{
				String WIRC_ACCOUNT_MSISDN_OPT = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.WIRC_ACCOUNT_MSISDN_OPT);
				Boolean USSD_NEW_TAGS_MANDATORY = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY);
				String requestStr=p_requestVO.getRequestMessage();
				HashMap requestMap=BTSLUtil.getStringToHash(requestStr,"&","=");

				String msisdn1=(String)requestMap.get("MSISDN");

				String pin=(String)requestMap.get(PIN_STR);

				String amount=(String)requestMap.get(AMOUNT_STR);

				String language1=(String)requestMap.get(LANG1_STR);

				String language2=(String)requestMap.get(LANG2_STR);

				String selector=(String)requestMap.get(SELECTOR_STR);

				String ussdSessionID=(String)requestMap.get(USSD_SESSION_ID);

				cellId=(String)requestMap.get(CELLID_STR);

				switchId=(String)requestMap.get(SWITCHID_STR);
				
				String uniqueSubId = null;
				if(WIRC_ACCOUNT_MSISDN_OPT.equals("MSISDN")){
					uniqueSubId=(String)requestMap.get(MSISDN2_STR);
					if(BTSLUtil.isNullString(uniqueSubId)) {
						throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.CHNL_ERROR_RECR_MSISDN_BLANK);
					}
				}
				else{
					uniqueSubId =(String)requestMap.get(ACCOUNTID_STR);
					if(BTSLUtil.isNullString(uniqueSubId)) {
						throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.CHNL_ERROR_RECR_ACCOUNT_ID_BLANK);
					}
				}
				if(BTSLUtil.isNullString(msisdn1)||BTSLUtil.isNullString(pin) || BTSLUtil.isNullString(amount))
				{
					throw new BTSLBaseException("USSDStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
				}
				
				
			
				
				//selector is mandatory 
				if(BTSLUtil.isNullString(selector))
				{
					throw new BTSLBaseException("USSDStringParser",methodName,PretupsErrorCodesI.WIRC_REQ_SELECTOR_MISSING);
				}
				/** new added to test c2s using extgw**/
				if(!BTSLUtil.isNullString(p_requestVO.getMessageGatewayVO().getGatewayCode())&& (p_requestVO.getMessageGatewayVO().getGatewayCode().equals("EXTGW")))
				{
					externalNetworkCode=(String)requestMap.get(EXTNWCODE_STR);
					if(BTSLUtil.isNullString(externalNetworkCode))
					{
						throw new BTSLBaseException("USSDStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
					}
				}
				tagsMandatory=USSD_NEW_TAGS_MANDATORY;
				if(tagsMandatory)
				{
					try{
						if(BTSLUtil.isNullString(cellId))
						{
							throw new BTSLBaseException("USSDStringParser",methodName,PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
						}
						if(BTSLUtil.isNullString(switchId))
						{
							throw new BTSLBaseException("USSDStringParser",methodName,PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
						}
					}catch(BTSLBaseException btsle){
						throw btsle;
					}
				}
				parsedRequestStr=PretupsI.SERVICE_TYPE_WIRELESS_INTERNET_RECHARGE+CHNL_MESSAGE_SEP+uniqueSubId+CHNL_MESSAGE_SEP+amount+CHNL_MESSAGE_SEP+selector+CHNL_MESSAGE_SEP+language2+CHNL_MESSAGE_SEP+pin;
				p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
				p_requestVO.setReqSelector(selector);
				p_requestVO.setRequestMSISDN(msisdn1);
				p_requestVO.setDecryptedMessage(parsedRequestStr);
				p_requestVO.setCellId(cellId);
				p_requestVO.setSwitchId(switchId);
				p_requestVO.setExternalNetworkCode(externalNetworkCode);
				p_requestVO.setUssdSessionID(ussdSessionID);
			}
			catch(Exception e)
			{
				_log.errorTrace(methodName,e);
				p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
				_log.error(methodName,"Exception e: "+e);
				throw new BTSLBaseException("USSDStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
			}
			finally
			{
				if(_log.isDebugEnabled())
					_log.debug(methodName,"Exiting prequestVO: "+p_requestVO.toString());
			}
	   }
	   /**
	 * @param p_requestVO
	 * @param actionId
	 * @throws Exception
	 * @author ashish.gupta
	 * for VIL
	 */
	public static void parseVoucherConsumptionPlainStringRequest(RequestVO p_requestVO) throws Exception {
	        final String methodName = "parseVoucherConsumptionPlainStringRequest";
	        if (_log.isDebugEnabled()) {
	            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
	        }
	        String parsedRequestStr = null;
	        int vomsPinMinLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PIN_MIN_LENGTH))).intValue();
	        int vomsPinMaxLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PIN_MAX_LENGTH))).intValue();
	        try {
	            final String requestStr = p_requestVO.getRequestMessage();
	            final HashMap requestMap = BTSLUtil.getStringToHash(requestStr, "&", "=");
	            final String type = (String) requestMap.get("TYPE");
	            final String msisdn = (String) requestMap.get("MSISDN");
	            final String msisdn2 = (String) requestMap.get("MSISDN2");
	            final String extRefNumber = (String) requestMap.get("EXTREFNUM");
	            String vouchercode = (String) requestMap.get("VOUCHERCODE");
	            final String serialNumber = (String) requestMap.get("SERIALNUMBER");
	            String selector = (String) requestMap.get("SELECTOR");
	            final String imsi = (String) requestMap.get("IMSI");
	            
	            if (BTSLUtil.isNullString(type)) {
	            	_log.error(methodName, "Request ID : "+p_requestVO.getRequestID()+" , Invalid Request string missing mandatory value TYPE");
	            	p_requestVO.setSenderMessageRequired(false);
	            	throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
	            }
	            if (BTSLUtil.isNullString(msisdn)) {
	            	_log.error(methodName, "Request ID : "+p_requestVO.getRequestID()+" , Invalid Request string missing mandatory value MSISDN");
	            	p_requestVO.setSenderMessageRequired(false);
	                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
	            }
	            p_requestVO.setFilteredMSISDN(_operatorUtil.getSystemFilteredMSISDN(msisdn));
	            if (BTSLUtil.isNullString(extRefNumber)) {
	            	_log.error(methodName, "Request ID : "+p_requestVO.getRequestID()+" , Invalid Request string missing mandatory value EXTREFNUM");
	            	p_requestVO.setSenderMessageRequired(false);
	            	throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
	            }
	            if (BTSLUtil.isNullString(vouchercode)) {
	            	_log.error(methodName, "Request ID : "+p_requestVO.getRequestID()+" , Invalid Request string missing mandatory value VOUCHERCODE");
	            	p_requestVO.setSenderMessageRequired(true);
	                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_BLANK_VOUCHERCODE);
	            }
	            if (BTSLUtil.isNullString(selector)) {
	            	_log.error(methodName, "Request ID : "+p_requestVO.getRequestID()+" , Invalid Request string missing mandatory value SELECTOR");
	            	p_requestVO.setSenderMessageRequired(false);
	                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
	            }
	            if (BTSLUtil.isNullString(imsi)) {
	            	_log.error(methodName, "Request ID : "+p_requestVO.getRequestID()+" , Invalid Request string missing mandatory value IMSI");
	            	p_requestVO.setSenderMessageRequired(false);
	                throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_IMSI_INFORMATION);
	            }
	            vouchercode = _operatorUtil.decryptPINPassword(vouchercode);

	            if((vouchercode.length()<vomsPinMinLength || vouchercode.length()>vomsPinMaxLength)){
	            	p_requestVO.setSenderMessageRequired(true);
	            	throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN);
	            }
	            if(BTSLUtil.isNullString(selector)){
	            	final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(PretupsI.SERVICE_TYPE_VOUCHER_CONSUMPTION);
	                if (serviceSelectorMappingVO != null) {
	                    selector = serviceSelectorMappingVO.getSelectorCode();
	                }
	            }
	            
	            
	            if(msisdn2 != null && !msisdn2.isEmpty() ){
	           		parsedRequestStr = PretupsI.SERVICE_TYPE_VOUCHER_CONSUMPTION + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + vouchercode;
	           	}else{
	           	 parsedRequestStr = PretupsI.SERVICE_TYPE_VOUCHER_CONSUMPTION + CHNL_MESSAGE_SEP + msisdn + CHNL_MESSAGE_SEP + vouchercode;
	           	}
	            p_requestVO.setRequestMap(requestMap);
	            p_requestVO.setExternalReferenceNum(extRefNumber);
	            p_requestVO.setRequestMSISDN(msisdn);
	           	p_requestVO.setFilteredMSISDN(msisdn);
	           	p_requestVO.setDecryptedMessage(parsedRequestStr);
	           	p_requestVO.setReqSelector(selector);
	           	p_requestVO.setImsi(imsi);
	           	
	            p_requestVO.setVoucherCode(vouchercode);
	            p_requestVO.setSerialnumber(serialNumber);
	        } 
	        catch (BTSLBaseException be) {
	        	_log.errorTrace(methodName, be);
				throw be;
	        }
	        catch (Exception e) {
	            _log.errorTrace(methodName, e);
	            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
	            _log.error(methodName, "Request ID : "+p_requestVO.getRequestID()+" , Exception e: " + e);
	            throw new BTSLBaseException("USSDStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
	        } finally {
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Request ID : "+p_requestVO.getRequestID()+" , Exiting p_requestVO: " + p_requestVO.toString());
	            }
	        }
	    }

	/**
	 * @param p_requestVO
	 * @throws Exception
	 * @author ashish.gupta
	 * for VIL
	 */
	public static void generateVoucherConsumptionPlainStringResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateVoucherConsumptionPlainStringResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);

            sbf.append(TYPE_STR + "=VOMSRCRESP");
            if (BTSLUtil.isNullString(p_requestVO.getTransactionID())) {
                sbf.append("&" + TXNID_STR + ">").append(_blank);
            } else {
                sbf.append("&" + TXNID_STR + ">").append(p_requestVO.getTransactionID());
            }
            
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("&" + TXNSTATUS_STR + ">").append(PretupsI.TXN_STATUS_SUCCESS);
            } else {
                String message = p_requestVO.getMessageCode();
                if (message.indexOf("_") != -1) {
                    message = message.substring(0, message.indexOf("_"));
                }
                sbf.append("&" + TXNSTATUS_STR + ">").append(message);
            }
            
            if (!p_requestVO.isSuccessTxn()) {
                sbf.append("&MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            } else {
                sbf.append("&MESSAGE>").append(getMessage(p_requestVO.getLocale(), PretupsErrorCodesI.VOUCHER_CONSUMPTION_SUCCESS, p_requestVO.getMessageArguments()));
            }
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "USSDStringParser[generateCreditTransferResponse]",
                PretupsErrorCodesI.P2P_ERROR_EXCEPTION, "", "", "generateCreditTransferResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }
	
	// Added for OSL
	public static void generateProductRechargeResponse(RequestVO p_requestVO) throws Exception
	{
		final String methodName="generateProductRechargeResponse";
		if(_log.isDebugEnabled())_log.debug(methodName,"Entered p_requestVO: "+p_requestVO.toString());
		String responseStr= null;
		StringBuffer sbf=null;
		try
		{
			sbf=new StringBuffer(1024);
			sbf.append("TYPE=TBRCTRFRESP"+USSD_RESP_SEP);
			if(p_requestVO.isSuccessTxn())
				sbf.append(USSD_RESP_SEP+"TXNSTATUS="+PretupsI.TXN_STATUS_SUCCESS);
			else
			    sbf.append(USSD_RESP_SEP+"TXNSTATUS="+p_requestVO.getMessageCode());
			if(!BTSLUtil.isNullString(p_requestVO.getSenderReturnMessage()) )
			{
				sbf.append(USSD_RESP_SEP+"MESSAGE="+p_requestVO.getSenderReturnMessage());
			}
			else
			{	
				System.out.println("p_requestVO.getSenderReturnMessage()"+p_requestVO.getSenderReturnMessage());
				sbf.append(USSD_RESP_SEP+"MESSAGE="+getMessage(p_requestVO.getLocale(),p_requestVO.getMessageCode(),p_requestVO.getMessageArguments()));
			}
			responseStr = sbf.toString();
		   p_requestVO.setSenderReturnMessage(responseStr);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			_log.error(methodName,"Exception e: "+e);
			p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"USSDStringParser[generateProductRechargeResponse]",PretupsErrorCodesI.C2S_ERROR_EXCEPTION,"","","generateProductRechargeResponse:"+e.getMessage());
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug("USSDStringParser","Exiting responseStr: "+responseStr);
		}
	}
   
   public static void generateMultipleVoucherDistributionResponse(RequestVO p_requestVO) throws Exception
	{
		final String methodName="generateMultipleVoucherDistributionResponse";
		if(_log.isDebugEnabled())_log.debug(methodName,"Entered p_requestVO: "+p_requestVO.toString());
		String responseStr= null;
		StringBuffer sbf=null;
		try
		{
			sbf=new StringBuffer(1024);
			sbf.append("TYPE=MEVDRESP"+USSD_RESP_SEP);
			if(BTSLUtil.isNullString(p_requestVO.getTransactionID()))
			{	sbf.append("TXNID_ARR=");
				sbf.append(USSD_RESP_SEP+"TXNID=");
			}
			else
			{
				sbf.append("TXNID_ARR=");
				for(int i=0,j=((ArrayList)p_requestVO.getValueObject()).size();i<j;i++)
				{
				    	sbf.append(USSD_RESP_SEP+"TXNID="+(String)((ArrayList)p_requestVO.getValueObject()).get(i));
				}
			}
			
			if(p_requestVO.isSuccessTxn())
				sbf.append(USSD_RESP_SEP+"TXNSTATUS="+PretupsI.TXN_STATUS_SUCCESS);
			else
			    sbf.append(USSD_RESP_SEP+"TXNSTATUS="+p_requestVO.getMessageCode());
			if(!BTSLUtil.isNullString(p_requestVO.getSenderReturnMessage()) )
			{
				sbf.append(USSD_RESP_SEP+"MESSAGE="+p_requestVO.getSenderReturnMessage());
			}
			else
			{	
				System.out.println("p_requestVO.getSenderReturnMessage()"+p_requestVO.getSenderReturnMessage());
				sbf.append(USSD_RESP_SEP+"MESSAGE="+getMessage(p_requestVO.getLocale(),p_requestVO.getMessageCode(),p_requestVO.getMessageArguments()));
			}
			responseStr = sbf.toString();
		   p_requestVO.setSenderReturnMessage(responseStr);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			_log.error(methodName,"Exception e: "+e);
			p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"USSDStringParser[generateMultipleVoucherDistributionResponse]",PretupsErrorCodesI.C2S_ERROR_EXCEPTION,"","","generateMultipleVoucherDistributionResponse:"+e.getMessage());
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug("USSDStringParser","Exiting responseStr: "+responseStr);
		}
	}
   public static void generateAddChannelUserResponse(RequestVO p_requestVO) throws Exception
	{
		final String methodName="generateAddChannelUserResponse";
		if(_log.isDebugEnabled())_log.debug(methodName,"Entered p_requestVO: "+p_requestVO.toString());
		String responseStr= null;
		StringBuffer sbf=null;
		try
		{
			
			//TYPE=ADDCHUSRRESP&TXNSTATUS=Transaction_Status&MESSAGE=User_Message
			
			sbf=new StringBuffer(1024);
			sbf.append("TYPE=ADUSRRESP"+USSD_RESP_SEP);
		
			if(p_requestVO.isSuccessTxn())
				sbf.append("TXNSTATUS="+PretupsI.TXN_STATUS_SUCCESS);
			else
			    sbf.append("TXNSTATUS="+p_requestVO.getMessageCode());
			if(!BTSLUtil.isNullString(p_requestVO.getSenderReturnMessage()) )
			{
				sbf.append(USSD_RESP_SEP+"MESSAGE="+p_requestVO.getSenderReturnMessage());
			}else
				sbf.append(USSD_RESP_SEP+"MESSAGE="+getMessage(p_requestVO.getLocale(),p_requestVO.getMessageCode(),p_requestVO.getMessageArguments()));
			responseStr = sbf.toString();
		   p_requestVO.setSenderReturnMessage(responseStr);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			_log.error(methodName,"Exception e: "+e);
			p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"USSDStringParser[generateAddChannelUserResponse]",PretupsErrorCodesI.C2S_ERROR_EXCEPTION,"","","generateChannelMVDResponse:"+e.getMessage());
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug("USSDStringParser","Exiting responseStr: "+responseStr);
		}
	}
   
   public static void generateChannelRegistrationResponse(RequestVO p_requestVO)
	{
		final String methodName="generateChannelRegistrationResponse";
		if(_log.isDebugEnabled())_log.debug(methodName,"Entered p_requestVO: "+p_requestVO.toString());
		String responseStr= null;
		StringBuffer sbf=null;
		try
		{
		    sbf=new StringBuffer(1024);
		    sbf.append("TYPE=REGSIDRESP"+USSD_RESP_SEP);
			if(p_requestVO.isSuccessTxn())
			    sbf.append("TXNSTATUS="+PretupsI.TXN_STATUS_SUCCESS+USSD_RESP_SEP);
			else
			    sbf.append("TXNSTATUS="+p_requestVO.getMessageCode()+USSD_RESP_SEP);
			responseStr= sbf.toString();
			p_requestVO.setSenderReturnMessage(responseStr);
		}
		catch(Exception e)
		{
			_log.error(methodName,"Exception e: "+e);
			p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,null,PretupsErrorCodesI.P2P_ERROR_EXCEPTION,"","","generateSelfBarResponse:"+e.getMessage());
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug(methodName,"Exiting responseStr: "+responseStr);
		}
    }
	
   public static void generateDeleteSIDResponse(RequestVO p_requestVO)
	{
		final String methodName="generateDeleteSIDResponse";
		if(_log.isDebugEnabled())_log.debug(methodName,"Entered p_requestVO: "+p_requestVO.toString());
		String responseStr= null;
		StringBuffer sbf=null;
		try
		{
		    sbf=new StringBuffer(1024);
		    sbf.append("TYPE=DELSIDRESP"+P2P_USSD_RESP_SEP);
			if(p_requestVO.isSuccessTxn())
			    sbf.append("TXNSTATUS="+PretupsI.TXN_STATUS_SUCCESS+P2P_USSD_RESP_SEP);
			else
			    sbf.append("TXNSTATUS="+p_requestVO.getMessageCode()+P2P_USSD_RESP_SEP);
			if (p_requestVO.isSuccessTxn()) {
                sbf.append("MESSAGE>").append(getMessage(p_requestVO.getLocale(), "UNDERPROCESS", p_requestVO.getMessageArguments()));
            } else {
                sbf.append("MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            }
			responseStr= sbf.toString();
			p_requestVO.setSenderReturnMessage(responseStr);
		}
		catch(Exception e)
		{
			_log.error(methodName,"Exception e: "+e);
			p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,null,PretupsErrorCodesI.P2P_ERROR_EXCEPTION,"","","generateSelfBarResponse:"+e.getMessage());
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug(methodName,"Exiting responseStr: "+responseStr);
		}
    }
	
   public static void parseEnquirySIDRequest(RequestVO p_requestVO) throws Exception{
		final String methodName="parseEnquirySIDRequest";
		if(_log.isDebugEnabled()) {
			_log.debug(methodName,"Entered p_requestVO: "+p_requestVO.toString());
		}
		String parsedRequestStr=null;
		HttpServletRequest p_request=(HttpServletRequest) p_requestVO.getRequestMap().get("HTTPREQUEST");
		try
		{
			String msisdn=p_request.getParameter("MSISDN");
			parsedRequestStr=PretupsI.SERVICE_TYPE_SID_ENQUIRY+CHNL_MESSAGE_SEP+msisdn;
			if(BTSLUtil.isNullString(msisdn)) {
				throw new BTSLBaseException("USSDStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
			}
			p_requestVO.setDecryptedMessage(parsedRequestStr);
			p_requestVO.setRequestMSISDN(msisdn);
		}
		catch(Exception e)
		{
			p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
			_log.error(methodName,"Exception e: "+e);
			throw new BTSLBaseException("USSDStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
		}
		finally
		{
			if(_log.isDebugEnabled()) {
				_log.debug(methodName,"Exiting p_requestVO: "+p_requestVO.toString());
			}
		}
	}
   
   public static void generateEnquirySIDResponse(RequestVO p_requestVO)
	{
		final String methodName="generateEnquirySIDResponse";
		if(_log.isDebugEnabled())_log.debug(methodName,"Entered p_requestVO: "+p_requestVO.toString());
		String responseStr= null;
		StringBuffer sbf=null;
		try
		{
		    sbf=new StringBuffer(1024);
		    sbf.append("TYPE=ENQSIDRESP"+P2P_USSD_RESP_SEP);
			if(p_requestVO.isSuccessTxn()) {
			    sbf.append("TXNSTATUS="+PretupsI.TXN_STATUS_SUCCESS+P2P_USSD_RESP_SEP);
				sbf.append("MESSAGE>").append(getMessage(p_requestVO.getLocale(), "UNDERPROCESS", p_requestVO.getMessageArguments()));
			} else {
			    sbf.append("TXNSTATUS="+p_requestVO.getMessageCode()+P2P_USSD_RESP_SEP);
				sbf.append("MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
			}
			responseStr= sbf.toString();
			p_requestVO.setSenderReturnMessage(responseStr);
		}
		catch(Exception e)
		{
			_log.error(methodName,"Exception e: "+e);
			p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,null,PretupsErrorCodesI.P2P_ERROR_EXCEPTION,"","","generateSelfBarResponse:"+e.getMessage());
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug(methodName,"Exiting responseStr: "+responseStr);
		}
    }
	public static void generateLmsPointEnquiryResponse(RequestVO p_requestVO)
	{
		final String methodName="generateLmsPointEnquiryResponse";
		if(_log.isDebugEnabled())_log.debug(methodName,"Entered p_requestVO: "+p_requestVO.toString());
		String responseStr= null;
		StringBuffer sbf=null;
		try
		{
		    sbf=new StringBuffer(1024);
		    sbf.append("TYPE=LMSPTENQ"+USSD_RESP_SEP);
			if(p_requestVO.isSuccessTxn()) {
			    sbf.append("TXNSTATUS="+PretupsI.TXN_STATUS_SUCCESS+USSD_RESP_SEP);
			    sbf.append("MESSAGE>").append(getMessage(p_requestVO.getLocale(), "UNDERPROCESS", p_requestVO.getMessageArguments()));
			} else {
			    sbf.append("TXNSTATUS="+p_requestVO.getMessageCode()+USSD_RESP_SEP);
			    sbf.append("MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
			}
			responseStr= sbf.toString();
			p_requestVO.setSenderReturnMessage(responseStr);
		}
		catch(Exception e)
		{
			_log.error(methodName,"Exception e: "+e);
			p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,null,PretupsErrorCodesI.P2P_ERROR_EXCEPTION,"","",methodName+":"+e.getMessage());
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug(methodName,"Exiting responseStr: "+responseStr);
		}
    }
	public static void generateLmsPointRedemptionResponse(RequestVO p_requestVO)
	{
		final String methodName="generateEnquirySIDResponse";
		if(_log.isDebugEnabled())_log.debug(methodName,"Entered p_requestVO: "+p_requestVO.toString());
		String responseStr= null;
		StringBuffer sbf=null;
		try
		{
		    sbf=new StringBuffer(1024);
		    sbf.append("TYPE=LMSPTRED"+USSD_RESP_SEP);
			if(p_requestVO.isSuccessTxn()) {
			    sbf.append("TXNSTATUS="+PretupsI.TXN_STATUS_SUCCESS+USSD_RESP_SEP);
			    sbf.append("MESSAGE>").append(getMessage(p_requestVO.getLocale(), "UNDERPROCESS", p_requestVO.getMessageArguments()));
			} else {
			    sbf.append("TXNSTATUS="+p_requestVO.getMessageCode()+USSD_RESP_SEP);
			    sbf.append("MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
			}
			responseStr= sbf.toString();
			p_requestVO.setSenderReturnMessage(responseStr);
		}
		catch(Exception e)
		{
			_log.error(methodName,"Exception e: "+e);
			p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,null,PretupsErrorCodesI.P2P_ERROR_EXCEPTION,"","",methodName+":"+e.getMessage());
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug(methodName,"Exiting responseStr: "+responseStr);
		}
    }
	
	public static void generateGetAccountInfoResponse(RequestVO p_requestVO) throws Exception
    {
		final String methodName="generateGetAccountInfoResponse";
	    if(_log.isDebugEnabled())_log.debug(methodName,"Entered p_requestVO: "+p_requestVO.toString());
	    String responseStr= null;
		boolean reqFromP2P=false;
		SenderVO senderVO=null;
		StringBuffer sbf=null;
		try
		{
			if(p_requestVO.getModule().equals(PretupsI.P2P_MODULE))
			{
				senderVO=(SenderVO)p_requestVO.getSenderVO();
				reqFromP2P=true;
			}
			
			sbf=new StringBuffer(1024);
			sbf.append("TYPE=CACINFRESP"+USSD_RESP_SEP);
		
		    if(reqFromP2P)
			{
			    if(senderVO != null)
			    {
			        sbf.append("REGSTATUS="+senderVO.getRegistered()+USSD_RESP_SEP);
			        sbf.append("PINSTATUS="+senderVO.getPin()+USSD_RESP_SEP);
			    }
			   
			    if(p_requestVO.isSuccessTxn())
			        sbf.append("TXNSTATUS="+PretupsI.TXN_STATUS_SUCCESS+USSD_RESP_SEP);
			   else
			       sbf.append("TXNSTATUS="+p_requestVO.getMessageCode()+USSD_RESP_SEP);
			  if(senderVO != null)
			    {
				  	boolean responseInDisplayAmount = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.RESPONSE_IN_DISPLAY_AMT);
			      	if(responseInDisplayAmount)
				  	{
						sbf.append("MINREMBAL="+String.valueOf((senderVO.getMinResidualBalanceAllowed()))+USSD_RESP_SEP);
				        sbf.append("MINAMT="+String.valueOf((senderVO.getMinTxnAmountAllowed()))+USSD_RESP_SEP);
				        sbf.append("MAXAMT="+String.valueOf((senderVO.getMaxTxnAmountAllowed()))+USSD_RESP_SEP);
				  	}
			      	else
					{
						sbf.append("MINREMBAL="+String.valueOf(PretupsBL.getSystemAmount(senderVO.getMinResidualBalanceAllowed()))+USSD_RESP_SEP);
				        sbf.append("MINAMT="+String.valueOf(PretupsBL.getSystemAmount(senderVO.getMinTxnAmountAllowed()))+USSD_RESP_SEP);
				        sbf.append("MAXAMT="+String.valueOf(PretupsBL.getSystemAmount(senderVO.getMaxTxnAmountAllowed()))+USSD_RESP_SEP);
					}
			        sbf.append("MAXPCTBAL="+String.valueOf(BTSLUtil.parseDoubleToLong( senderVO.getMaxPerTransferAllowed()))+USSD_RESP_SEP);
			    }
			   
			}
			else //TO DO Needs to be changed for C2S
			{
			    sbf.append("REGSTATUS="+senderVO.getRegistered()+USSD_RESP_SEP);
		        sbf.append("PINSTATUS="+senderVO.getPin()+USSD_RESP_SEP);
			   
				if(p_requestVO.isSuccessTxn())
				    sbf.append("TXNSTATUS="+PretupsI.TXN_STATUS_SUCCESS+USSD_RESP_SEP);
				else
				    sbf.append("TXNSTATUS="+p_requestVO.getMessageCode()+USSD_RESP_SEP);
				sbf.append("MINREMBAL="+String.valueOf(senderVO.getMinResidualBalanceAllowed())+USSD_RESP_SEP);
				sbf.append("MINAMT="+String.valueOf(senderVO.getMinTxnAmountAllowed())+USSD_RESP_SEP);
				sbf.append("MAXAMT="+String.valueOf(senderVO.getMaxTxnAmountAllowed())+USSD_RESP_SEP);
				sbf.append("MAXPCTBAL="+String.valueOf(senderVO.getMaxPerTransferAllowed())+USSD_RESP_SEP);
			}
		  
		    //just workaround
		    responseStr=sbf.toString();
		 	p_requestVO.setSenderReturnMessage(responseStr);
		}
		catch(Exception e)
		{
			_log.error(methodName,"Exception e: "+e);
			p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"USSDStringParser[generateGetAccountInfoResponse]",PretupsErrorCodesI.P2P_ERROR_EXCEPTION,"","","generateGetAccountInfoResponse:"+e.getMessage());
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug(methodName,"Exiting responseStr: "+responseStr);
		}
    }
	
	public static void generateChangePinResponse(RequestVO p_requestVO) throws Exception
    {
		final String methodName="generateChangePinResponse";
	    if(_log.isDebugEnabled())_log.debug(methodName,"Entered p_requestVO: "+p_requestVO.toString());
	    String responseStr= null;
		StringBuffer sbf=null;
		try
		{
		    sbf=new StringBuffer(1024);
		    sbf.append("TYPE=CCPNRESP"+USSD_RESP_SEP);
			if(p_requestVO.isSuccessTxn())
			    sbf.append("TXNSTATUS="+PretupsI.TXN_STATUS_SUCCESS+USSD_RESP_SEP);
			else
			    sbf.append("TXNSTATUS="+p_requestVO.getMessageCode()+USSD_RESP_SEP);
                     if(!BTSLUtil.isNullString(p_requestVO.getSenderReturnMessage()) )
				sbf.append("MESSAGE="+p_requestVO.getSenderReturnMessage());
			else
				sbf.append("MESSAGE="+getMessage(p_requestVO.getLocale(),p_requestVO.getMessageCode(),p_requestVO.getMessageArguments()));
			responseStr= sbf.toString();
			p_requestVO.setSenderReturnMessage(responseStr);
		}
		catch(Exception e)
		{
			_log.error(methodName,"Exception e: "+e);
			p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"XMLStringParser[generateChangePinResponse]",PretupsErrorCodesI.P2P_ERROR_EXCEPTION,"","","generateChangePinResponse:"+e.getMessage());
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug(methodName,"Exiting responseStr: "+responseStr);
		}
    }
	
	public static void generateNotificationLanguageResponse(RequestVO p_requestVO) throws Exception
    {
		final String methodName="generateNotificationLanguageResponse";
	    if(_log.isDebugEnabled())_log.debug(methodName,"Entered p_requestVO: "+p_requestVO.toString());
	    String responseStr= null;
		StringBuffer sbf=null;
		try
		{
		    sbf=new StringBuffer(1024);
		    sbf.append("TYPE=CCLANGRESP"+USSD_RESP_SEP);
			if(p_requestVO.isSuccessTxn())
			    sbf.append("TXNSTATUS="+PretupsI.TXN_STATUS_SUCCESS+USSD_RESP_SEP);
			else
			    sbf.append("TXNSTATUS="+p_requestVO.getMessageCode()+USSD_RESP_SEP);
			responseStr= sbf.toString();
			p_requestVO.setSenderReturnMessage(responseStr);
		}
		catch(Exception e)
		{
			_log.error(methodName,"Exception e: "+e);
			p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"XMLStringParser[generateNotificationLanguageResponse]",PretupsErrorCodesI.P2P_ERROR_EXCEPTION,"","","generateNotificationLanguageResponse:"+e.getMessage());
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug(methodName,"Exiting responseStr: "+responseStr);
		}
    }
	
	public static void generateHistoryMessageResponse(RequestVO p_requestVO) throws Exception
    {
		final String methodName="generateHistoryMessageResponse";
	    if(_log.isDebugEnabled())_log.debug(methodName,"Entered p_requestVO: "+p_requestVO.toString());
	    String responseStr= null;
		StringBuffer sbf=null;
		try
		{
			sbf=new StringBuffer(1024);
			sbf.append("TYPE=CCHISRESP"+USSD_RESP_SEP);
			if(p_requestVO.isSuccessTxn())
				sbf.append("TXNSTATUS="+PretupsI.TXN_STATUS_SUCCESS+USSD_RESP_SEP);
			else
			    sbf.append("TXNSTATUS="+p_requestVO.getMessageCode()+USSD_RESP_SEP);
			responseStr = sbf.toString();
			p_requestVO.setSenderReturnMessage(responseStr);
		}
		catch(Exception e)
		{
			_log.error(methodName,"Exception e: "+e);
			p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"XMLStringParser[generateHistoryMessageResponse]",PretupsErrorCodesI.P2P_ERROR_EXCEPTION,"","","generateHistoryMessageResponse:"+e.getMessage());
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug(methodName,"Exiting responseStr: "+responseStr);
		}
    }
	
	public static void generateSubscriberRegistrationResponse(RequestVO p_requestVO) throws Exception
    {
		final String methodName="generateSubscriberRegistrationResponse";
	    if(_log.isDebugEnabled())_log.debug(methodName,"Entered p_requestVO: "+p_requestVO.toString());
	    String responseStr= null;
		StringBuffer sbf=null;
		try
		{
		    sbf=new StringBuffer(1024);
			sbf.append("TYPE=REGRESP"+P2P_USSD_RESP_SEP);
			if(p_requestVO.isSuccessTxn())
		        sbf.append("TXNSTATUS="+PretupsI.TXN_STATUS_SUCCESS+P2P_USSD_RESP_SEP);
			else
			    sbf.append("TXNSTATUS="+p_requestVO.getMessageCode()+P2P_USSD_RESP_SEP);
			if (p_requestVO.isSuccessTxn()) {
                sbf.append("MESSAGE>").append(getMessage(p_requestVO.getLocale(), "UNDERPROCESS", p_requestVO.getMessageArguments()));
            } else {
                sbf.append("MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            }
		    responseStr = sbf.toString();
			p_requestVO.setSenderReturnMessage(responseStr);
		}
		catch(Exception e)
		{
			_log.error(methodName,"Exception e: "+e);
			p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"XMLStringParser[generateSubscriberRegistrationResponse]",PretupsErrorCodesI.P2P_ERROR_EXCEPTION,"","","generateSubscriberRegistrationResponse:"+e.getMessage());
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug(methodName,"Exiting responseStr: "+responseStr);
		}
    }
	
	public static void generateCreditRechargeResponse(RequestVO p_requestVO) throws Exception
    {
		final String methodName="generateCreditRechargeResponse";
	    if(_log.isDebugEnabled())_log.debug(methodName,"Entered p_requestVO: "+p_requestVO.toString());
	    String responseStr= null;
		StringBuffer sbf=null;
		try
		{
		    sbf=new StringBuffer(1024);
			sbf.append("TYPE=CCRCRESP"+USSD_RESP_SEP);
			if(BTSLUtil.isNullString(p_requestVO.getTransactionID()))
			    sbf.append("TXNID="+USSD_RESP_SEP);
			else
			    sbf.append("TXNID="+p_requestVO.getTransactionID()+USSD_RESP_SEP);    
		    if(p_requestVO.isSuccessTxn())
		        sbf.append("TXNSTATUS="+PretupsI.TXN_STATUS_SUCCESS+USSD_RESP_SEP);
			else
			    sbf.append("TXNSTATUS="+p_requestVO.getMessageCode()+USSD_RESP_SEP);
		    responseStr = sbf.toString();
			p_requestVO.setSenderReturnMessage(responseStr);
		}
		catch(Exception e)
		{
			_log.error(methodName,"Exception e: "+e);
			p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"XMLStringParser[generateCreditRechargeResponse]",PretupsErrorCodesI.P2P_ERROR_EXCEPTION,"","","generateCreditRechargeResponse:"+e.getMessage());
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug(methodName,"Exiting responseStr: "+responseStr);
		}
    }

	public static void generateSubscriberDeRegistrationResponse(RequestVO p_requestVO) throws Exception
    {
		final String methodName="generateSubscriberDeRegistrationResponse";
	    if(_log.isDebugEnabled())_log.debug(methodName,"Entered p_requestVO: "+p_requestVO.toString());
	    String responseStr= null;
		StringBuffer sbf=null;
		try
		{
		    sbf=new StringBuffer(1024);
			sbf.append("TYPE=DREGRESP"+P2P_USSD_RESP_SEP);
			if(p_requestVO.isSuccessTxn())
		        sbf.append("TXNSTATUS="+PretupsI.TXN_STATUS_SUCCESS+USSD_RESP_SEP);
			else
			    sbf.append("TXNSTATUS="+p_requestVO.getMessageCode()+USSD_RESP_SEP);
		   
		    responseStr = sbf.toString();
			p_requestVO.setSenderReturnMessage(responseStr);
		}
		catch(Exception e)
		{
			_log.error(methodName,"Exception e: "+e);
			p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"XMLStringParser[generateSubscriberDeRegistrationResponse]",PretupsErrorCodesI.P2P_ERROR_EXCEPTION,"","","generateSubscriberDeRegistrationResponse:"+e.getMessage());
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug(methodName,"Exiting responseStr: "+responseStr);
		}
    }
	
	public static void generateP2PServiceSuspendResponse(RequestVO p_requestVO) 
	{
		final String methodName="generateP2PServiceSuspendResponse";
		if(_log.isDebugEnabled())_log.debug(methodName,"Entered p_requestVO: "+p_requestVO.toString());
		StringBuffer sbf=null;
		String responseStr=null;
		try
		{
			sbf=new StringBuffer(1024);
		   
			    sbf.append("TYPE=SUSRESP"+USSD_RESP_SEP);
		   	if(p_requestVO.isSuccessTxn())
		        sbf.append("TXNSTATUS="+PretupsI.TXN_STATUS_SUCCESS+USSD_RESP_SEP);
			else
			    sbf.append("TXNSTATUS="+p_requestVO.getMessageCode()+USSD_RESP_SEP);
		    responseStr = sbf.toString();
			p_requestVO.setSenderReturnMessage(responseStr);
		}
		catch(Exception e) 
		{
		    e.printStackTrace();
			p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
		    _log.error(methodName,"Exception e: "+e);
		    EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"XMLStringParser[generateP2PServiceSuspendResponse]",PretupsErrorCodesI.XML_ERROR_EXCEPTION,"","","generateP2PServiceSuspendResponse:"+e.getMessage());
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug(methodName,"Exiting  p_requestVO: "+p_requestVO.toString());
		}
	}
	
	public static void generateP2PServiceResumeResponse(RequestVO p_requestVO) 
	{
		final String methodName="generateP2PServiceResumeResponse";
		if(_log.isDebugEnabled())_log.debug(methodName,"Entered p_requestVO: "+p_requestVO.toString());
		StringBuffer sbf=null;
		String responseStr=null;
		try
		{
			sbf=new StringBuffer(1024);
			    sbf.append("TYPE=RESRESP"+USSD_RESP_SEP);
		   	if(p_requestVO.isSuccessTxn())
		        sbf.append("TXNSTATUS="+PretupsI.TXN_STATUS_SUCCESS+USSD_RESP_SEP);
			else
			    sbf.append("TXNSTATUS="+p_requestVO.getMessageCode()+USSD_RESP_SEP);
		    responseStr = sbf.toString();
			p_requestVO.setSenderReturnMessage(responseStr);
		}
		catch(Exception e) 
		{
		    e.printStackTrace();
			p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
		    _log.error(methodName,"Exception e: "+e);
		    EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"XMLStringParser[generateP2PServiceResumeResponse]",PretupsErrorCodesI.XML_ERROR_EXCEPTION,"","","generateP2PServiceResumeResponse:"+e.getMessage());
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug(methodName,"Exiting  p_requestVO: "+p_requestVO.toString());
		}
	}
	
	public static void generateLastTransferStatus(RequestVO p_requestVO) throws Exception
	{
		final String methodName="generateLastTransferStatus";
		if(_log.isDebugEnabled())_log.debug(methodName,"Entered p_requestVO: "+p_requestVO.toString());
		String responseStr= null;
		StringBuffer sbf=null;
		try
		{
			sbf=new StringBuffer(1024);
			sbf.append("TYPE=PLTRESP"+USSD_RESP_SEP);					
			if(p_requestVO.isSuccessTxn())
				sbf.append("TXNSTATUS="+PretupsI.TXN_STATUS_SUCCESS+USSD_RESP_SEP);
			else
				sbf.append("TXNSTATUS="+p_requestVO.getMessageCode()+USSD_RESP_SEP);
			responseStr = sbf.toString();
		   p_requestVO.setSenderReturnMessage(responseStr);
		}
		catch(Exception e)
		{
			_log.error(methodName,"Exception e: "+e);
			p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"XMLStringParser[generateLastTransferStatus]",PretupsErrorCodesI.P2P_ERROR_EXCEPTION,"","","generateLastTransferStatus:"+e.getMessage());
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug(methodName,"Exiting responseStr: "+responseStr);
		}
	}
	
	public static void generateAddBuddyResponse(RequestVO p_requestVO)
	{
		if(_log.isDebugEnabled())_log.debug("generateAddBuddyResponse","Entered p_requestVO: "+p_requestVO.toString());
		String responseStr= null;
		StringBuffer sbf=null;
		try
		{
		    sbf=new StringBuffer(1024);
		    sbf.append("TYPE=ADDBUDDYRESP"+USSD_RESP_SEP);
			if(p_requestVO.isSuccessTxn())
			    sbf.append("TXNSTATUS="+PretupsI.TXN_STATUS_SUCCESS+USSD_RESP_SEP);
			else
			    sbf.append("TXNSTATUS="+p_requestVO.getMessageCode()+USSD_RESP_SEP);
			responseStr= sbf.toString();
			p_requestVO.setSenderReturnMessage(responseStr);
		}
		catch(Exception e)
		{
			_log.error("generateAddBuddyResponse","Exception e: "+e);
			p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"XMLStringParser[generateAddBuddyResponse]",PretupsErrorCodesI.P2P_ERROR_EXCEPTION,"","","generateAddBuddyResponse:"+e.getMessage());
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug("generateAddBuddyResponse","Exiting responseStr: "+responseStr);
		}
    }

/** This method generates DeleteBuddy Response in XML format from requestVO
	 *@param p_requestVO RequestVO
	 * @author vikas.kumar
	 */
	public static void generateDeleteBuddyResponse(RequestVO p_requestVO)
	{
		if(_log.isDebugEnabled())_log.debug("generateDeleteBuddyResponse","Entered p_requestVO: "+p_requestVO.toString());
		String responseStr= null;
		StringBuffer sbf=null;
		try
		{
		    sbf=new StringBuffer(1024);
		    sbf.append("TYPE=DELBUDDYRESP"+USSD_RESP_SEP);
			if(p_requestVO.isSuccessTxn())
			    sbf.append("TXNSTATUS"+PretupsI.TXN_STATUS_SUCCESS+USSD_RESP_SEP);
			else
			    sbf.append("TXNSTATUS"+p_requestVO.getMessageCode()+USSD_RESP_SEP);
			responseStr= sbf.toString();
			p_requestVO.setSenderReturnMessage(responseStr);
		}
		catch(Exception e)
		{
			_log.error("generateDeleteBuddyResponse","Exception e: "+e);
			p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"XMLStringParser[generateDeleteBuddyResponse]",PretupsErrorCodesI.P2P_ERROR_EXCEPTION,"","","generateAddBuddyResponse:"+e.getMessage());
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug("generateDeleteBuddyResponse","Exiting responseStr: "+responseStr);
		}
    }




/** This method generates List Buddy Response in XML format from requestVO
	 *@param p_requestVO RequestVO
	 * @author vikas.kumar
	 */
	public static void generateListBuddyResponse(RequestVO p_requestVO)
	{
		if(_log.isDebugEnabled())_log.debug("generateListBuddyResponse","Entered p_requestVO: "+p_requestVO.toString());
		String responseStr= null;
		StringBuffer sbf=null;
		try
		{
		    sbf=new StringBuffer(1024);
		    sbf.append("TYPE=LSTBUDDYRESP"+USSD_RESP_SEP);
			if(p_requestVO.isSuccessTxn())
			    sbf.append("TXNSTATUS="+PretupsI.TXN_STATUS_SUCCESS+USSD_RESP_SEP);
			else
			    sbf.append("TXNSTATUS="+p_requestVO.getMessageCode()+USSD_RESP_SEP);
			responseStr= sbf.toString();
			p_requestVO.setSenderReturnMessage(responseStr);
		}
		catch(Exception e)
		{
			_log.error("generateListBuddyResponse","Exception e: "+e);
			p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"XMLStringParser[generateAddBuddyResponse]",PretupsErrorCodesI.P2P_ERROR_EXCEPTION,"","","generateAddBuddyResponse:"+e.getMessage());
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug("generateListBuddyResponse","Exiting responseStr: "+responseStr);
		}
    }

	public static void generateGMBResponse(RequestVO p_requestVO)
	{
		if(_log.isDebugEnabled())_log.debug("generateGMBResponse","Entered p_requestVO: "+p_requestVO.toString());
		String responseStr= null;
		StringBuffer sbf=null;
		try
		{
		    sbf=new StringBuffer(1024);
		    
		    //TYPE=CGMBALRESP|TXNID=<TXNID>|TXNSTATUS=<TXNSTATUS>|MESSAGE=<MESSAGE>
		    sbf.append("TYPE=CGMBALRESP"+USSD_RESP_SEP);

		    if(p_requestVO.isSuccessTxn())
				sbf.append("TXNSTATUS="+PretupsI.TXN_STATUS_SUCCESS);
			else
			    sbf.append("TXNSTATUS="+p_requestVO.getMessageCode());
				 
			if(!BTSLUtil.isNullString(p_requestVO.getSenderReturnMessage()) )
				sbf.append(USSD_RESP_SEP+"MESSAGE="+p_requestVO.getSenderReturnMessage());
			else
				sbf.append(USSD_RESP_SEP+"MESSAGE="+getMessage(p_requestVO.getLocale(),p_requestVO.getMessageCode(),p_requestVO.getMessageArguments()));

			responseStr= sbf.toString();
			p_requestVO.setSenderReturnMessage(responseStr);
		}
		catch(Exception e)
		{
			_log.error("generateGMBResponse","Exception e: "+e);
			p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"USSDStringParser[generateGMBResponse]",PretupsErrorCodesI.P2P_ERROR_EXCEPTION,"","","generateGMBResponse:"+e.getMessage());
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug("generateGMBResponse","Exiting responseStr: "+responseStr);
		}
    }
	
	/** This method generates SelfBar Response in XML format from requestVO
	 *@param p_requestVO RequestVO
	 * @author vikas.kumar
	 */
	public static void generateSelfBarResponse(RequestVO p_requestVO)
	{
		final String methodName="generateSelfBarResponse";
		if(_log.isDebugEnabled())_log.debug(methodName,"Entered p_requestVO: "+p_requestVO.toString());
		String responseStr= null;
		StringBuffer sbf=null;
		try
		{
		    sbf=new StringBuffer(1024);
		    sbf.append("TYPE=BARRESP"+USSD_RESP_SEP);
			if(p_requestVO.isSuccessTxn())
			    sbf.append("TXNSTATUS"+PretupsI.TXN_STATUS_SUCCESS+USSD_RESP_SEP);
			else
			    sbf.append("TXNSTATUS"+p_requestVO.getMessageCode()+USSD_RESP_SEP);
			responseStr= sbf.toString();
			p_requestVO.setSenderReturnMessage(responseStr);
		}
		catch(Exception e)
		{
			_log.error(methodName,"Exception e: "+e);
			p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,null,PretupsErrorCodesI.P2P_ERROR_EXCEPTION,"","",methodName+":"+e.getMessage());
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug(methodName,"Exiting responseStr: "+responseStr);
		}
    }
	
	public static void parseChannelRegistrationRequest(RequestVO p_requestVO) throws Exception
	{
		final String methodName="parseChannelRegistrationRequest";
		if(_log.isDebugEnabled()) {
			_log.debug(methodName,"Entered p_requestVO: "+p_requestVO.toString());
		}
		String parsedRequestStr=null;
		HttpServletRequest p_request=(HttpServletRequest) p_requestVO.getRequestMap().get("HTTPREQUEST");

		try
		{
			String msisdn=p_request.getParameter("MSISDN");
			String sid=p_request.getParameter("SID");
			String newsid=p_request.getParameter("NEWSID");
			if((BTSLUtil.isNullString(sid))&&(BTSLUtil.isNullString(newsid))) {
				parsedRequestStr=PretupsI.SERVICE_TYPE_SIDREG;
			} else if((!BTSLUtil.isNullString(sid))&&(BTSLUtil.isNullString(newsid))) {
				parsedRequestStr=PretupsI.SERVICE_TYPE_SIDREG+CHNL_MESSAGE_SEP+sid;
			} else if((!BTSLUtil.isNullString(sid))&&(!BTSLUtil.isNullString(newsid))) {
				parsedRequestStr=PretupsI.SERVICE_TYPE_SIDREG+CHNL_MESSAGE_SEP+sid+CHNL_MESSAGE_SEP+newsid;
			} else {
				throw new BTSLBaseException("USSDStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
			}
			if(BTSLUtil.isNullString(msisdn)) {
				throw new BTSLBaseException("USSDStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
			}

			String language1=p_request.getParameter("LANGUAGE1"); 
			if(BTSLUtil.isNullString(language1)) {
				language1=Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
			}
			
			if(BTSLUtil.isNullString(language1)) {
				language1="1";
			}
			
			if(!BTSLUtil.isNullString(language1)){
				   if(!language1.equalsIgnoreCase("0") || !language1.equalsIgnoreCase("1")){
					   if(BTSLUtil.isNullString(language1)) {
						language1=Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
					}
						
					if(BTSLUtil.isNullString(language1)) {
						language1="1";
					}
			   }
			   p_requestVO.setLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
			}
			p_requestVO.setDecryptedMessage(parsedRequestStr);			
		}
		catch(Exception e)
		{
			_log.errorTrace(methodName,e);
			p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
			_log.error(methodName,"Exception e: "+e);
			throw new BTSLBaseException("USSDStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
		}
		finally
		{
			if(_log.isDebugEnabled()) {
				_log.debug(methodName,"Exiting p_requestVO: "+p_requestVO.toString());
			}
		}
	}
	
	public static void parseDeleteSIDRequest(RequestVO p_requestVO) throws Exception{
		final String methodName="parseDeleteSIDRequest";
		if(_log.isDebugEnabled()) {
			_log.debug(methodName,"Entered p_requestVO: "+p_requestVO.toString());
		}
		String parsedRequestStr=null;
		HttpServletRequest p_request=(HttpServletRequest) p_requestVO.getRequestMap().get("HTTPREQUEST");
		try
		{
			String msisdn=p_request.getParameter("MSISDN");
			String sid=p_request.getParameter("SID");
			parsedRequestStr=PretupsI.SERVICE_TYPE_SID_DELETE+CHNL_MESSAGE_SEP+msisdn+CHNL_MESSAGE_SEP+sid;
			if(BTSLUtil.isNullString(msisdn) || BTSLUtil.isNullString(sid)) {
				throw new BTSLBaseException("USSDStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
			}
			p_requestVO.setDecryptedMessage(parsedRequestStr);			
		}
		catch(Exception e)
		{
			p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
			_log.error(methodName,"Exception e: "+e);
			throw new BTSLBaseException("USSDStringParser","parseGetAccountInfoRequest",PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
		}
		finally
		{
			if(_log.isDebugEnabled()) {
				_log.debug(methodName,"Exiting p_requestVO: "+p_requestVO.toString());
			}
		}
	}
	
	public static void generateInitiateSelfTPinResetResponse(RequestVO p_requestVO)
	{
		final String methodName="generateInitiateSelfTPinResetResponse";
		if(_log.isDebugEnabled())_log.debug(methodName,"Entered p_requestVO: "+p_requestVO.toString());
		String responseStr= null;
		StringBuffer sbf=null;
		try
		{
		    sbf=new StringBuffer(1024);
		    sbf.append("TYPE=INPRESETRESP"+USSD_RESP_SEP);
			if(p_requestVO.isSuccessTxn()) {
			    sbf.append("TXNSTATUS="+PretupsI.TXN_STATUS_SUCCESS+USSD_RESP_SEP);
			    sbf.append("MESSAGE>").append(getMessage(p_requestVO.getLocale(), "UNDERPROCESS", p_requestVO.getMessageArguments()));
			} else {
			    sbf.append("TXNSTATUS="+p_requestVO.getMessageCode()+USSD_RESP_SEP);
			    sbf.append("MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
			}
			responseStr= sbf.toString();
			p_requestVO.setSenderReturnMessage(responseStr);
		}
		catch(Exception e)
		{
			_log.error(methodName,"Exception e: "+e);
			p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,null,PretupsErrorCodesI.P2P_ERROR_EXCEPTION,"","",methodName+":"+e.getMessage());
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug(methodName,"Exiting responseStr: "+responseStr);
		}
    }
	
	public static void generateSelfTPinResetResponse(RequestVO p_requestVO)
	{
		final String methodName="generateSelfTPinResetResponse";
		if(_log.isDebugEnabled())_log.debug(methodName,"Entered p_requestVO: "+p_requestVO.toString());
		String responseStr= null;
		StringBuffer sbf=null;
		try
		{
		    sbf=new StringBuffer(1024);
		    sbf.append("TYPE=PRESETRESP"+USSD_RESP_SEP);
			if(p_requestVO.isSuccessTxn()) {
			    sbf.append("TXNSTATUS="+PretupsI.TXN_STATUS_SUCCESS+USSD_RESP_SEP);
			    sbf.append("MESSAGE>").append(getMessage(p_requestVO.getLocale(), "UNDERPROCESS", p_requestVO.getMessageArguments()));
			} else {
			    sbf.append("TXNSTATUS="+p_requestVO.getMessageCode()+USSD_RESP_SEP);
			    sbf.append("MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
			}
			responseStr= sbf.toString();
			p_requestVO.setSenderReturnMessage(responseStr);
		}
		catch(Exception e)
		{
			_log.error(methodName,"Exception e: "+e);
			p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,null,PretupsErrorCodesI.P2P_ERROR_EXCEPTION,"","",methodName+":"+e.getMessage());
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug(methodName,"Exiting responseStr: "+responseStr);
		}
    }
	
	public static void generateSelfTPinDataUpdateResponse(RequestVO p_requestVO)
	{
		final String methodName="generateSelfTPinDataUpdateResponse";
		if(_log.isDebugEnabled())_log.debug(methodName,"Entered p_requestVO: "+p_requestVO.toString());
		String responseStr= null;
		StringBuffer sbf=null;
		try
		{
		    sbf=new StringBuffer(1024);
		    sbf.append("TYPE=DUPDATERESP"+USSD_RESP_SEP);
			if(p_requestVO.isSuccessTxn()) {
			    sbf.append("TXNSTATUS="+PretupsI.TXN_STATUS_SUCCESS+USSD_RESP_SEP);
			    sbf.append("MESSAGE>").append(getMessage(p_requestVO.getLocale(), "UNDERPROCESS", p_requestVO.getMessageArguments()));
			} else {
			    sbf.append("TXNSTATUS="+p_requestVO.getMessageCode()+USSD_RESP_SEP);
			    sbf.append("MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
			}
			responseStr= sbf.toString();
			p_requestVO.setSenderReturnMessage(responseStr);
		}
		catch(Exception e)
		{
			_log.error(methodName,"Exception e: "+e);
			p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,null,PretupsErrorCodesI.P2P_ERROR_EXCEPTION,"","",methodName+":"+e.getMessage());
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug(methodName,"Exiting responseStr: "+responseStr);
		}
    }
	
	public static void generateSOSResponse(RequestVO p_requestVO)
	{
		final String methodName="generateSOSResponse";
		if(_log.isDebugEnabled())_log.debug(methodName,"Entered p_requestVO: "+p_requestVO.toString());
		String responseStr= null;
		StringBuffer sbf=null;
		try
		{
		    sbf=new StringBuffer(1024);
		    sbf.append("TYPE=SOSRES"+USSD_RESP_SEP);
			if(p_requestVO.isSuccessTxn()) {
			    sbf.append("TXNSTATUS="+PretupsI.TXN_STATUS_SUCCESS+USSD_RESP_SEP);
			    sbf.append("MESSAGE>").append(getMessage(p_requestVO.getLocale(), "UNDERPROCESS", p_requestVO.getMessageArguments()));
			} else {
			    sbf.append("TXNSTATUS="+p_requestVO.getMessageCode()+USSD_RESP_SEP);
			    sbf.append("MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
			}
			responseStr= sbf.toString();
			p_requestVO.setSenderReturnMessage(responseStr);
		}
		catch(Exception e)
		{
			_log.error(methodName,"Exception e: "+e);
			p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,null,PretupsErrorCodesI.P2P_ERROR_EXCEPTION,"","",methodName+":"+e.getMessage());
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug(methodName,"Exiting responseStr: "+responseStr);
		}
    }
	
	public static void generateSOSSettlementResponse(RequestVO p_requestVO)
	{
		final String methodName="generateSOSSettlementResponse";
		if(_log.isDebugEnabled())_log.debug(methodName,"Entered p_requestVO: "+p_requestVO.toString());
		String responseStr= null;
		StringBuffer sbf=null;
		try
		{
		    sbf=new StringBuffer(1024);
		    sbf.append("TYPE=SOSSTLRES"+USSD_RESP_SEP);
			if(p_requestVO.isSuccessTxn()) {
			    sbf.append("TXNSTATUS="+PretupsI.TXN_STATUS_SUCCESS+USSD_RESP_SEP);
			    sbf.append("MESSAGE>").append(getMessage(p_requestVO.getLocale(), "UNDERPROCESS", p_requestVO.getMessageArguments()));
			} else {
			    sbf.append("TXNSTATUS="+p_requestVO.getMessageCode()+USSD_RESP_SEP);
			    sbf.append("MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
			}
			responseStr= sbf.toString();
			p_requestVO.setSenderReturnMessage(responseStr);
		}
		catch(Exception e)
		{
			_log.error(methodName,"Exception e: "+e);
			p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,null,PretupsErrorCodesI.P2P_ERROR_EXCEPTION,"","",methodName+":"+e.getMessage());
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug(methodName,"Exiting responseStr: "+responseStr);
		}
    }



    /**
     * this method parse GetAccountInfoRequest from plain String and formating
     * into white space seperated String
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void parseGetAccountInfoRequest(RequestVO p_requestVO) throws Exception {
    	final String methodName = "parseGetAccountInfoRequest";
    	 StringBuilder loggerValue= new StringBuilder();
         if (_log.isDebugEnabled()) {
         	loggerValue.setLength(0);
         	loggerValue.append(PretupsI.ENTERED_VALUE);
         	loggerValue.append(p_requestVO.toString());
         	_log.debug(methodName, loggerValue);
         }
        String parsedRequestStr = null;
        boolean tagsMandatory = false;
        String cellId = null;
        String switchId = null;
        boolean ussdNewTagsMandatory = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY);
        try {   	        	
                final String requestStr = p_requestVO.getRequestMessage();
                final HashMap requestMap = BTSLUtil.getStringToHash(requestStr, "&", "=");

                final String msisdn1 = (String) requestMap.get(MSISDN1_STR);

                String selector = (String) requestMap.get(SELECTOR_STR);

                cellId = (String) requestMap.get(CELLID_STR);

                switchId = (String) requestMap.get(SWITCHID_STR);

                final String ussdSessionID = (String) requestMap.get(USSD_SESSION_ID);

                if (BTSLUtil.isNullString(msisdn1)) {
                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.USSD_BLANK_MSISDN);
                }
                if (BTSLUtil.isNullString(selector)) {
                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.USSD_BLANK_SELECTOR);
                }
             
            // tagsMandatory=ussdNewTagsMandatory
            if (ussdNewTagsMandatory) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
                    }
                } catch (BTSLBaseException btsle) {
                    throw btsle;
                }
            }

            parsedRequestStr = PretupsI.SERVICE_TYPE_ACCOUNTINFO + P2P_MESSAGE_SEP + msisdn1;
            p_requestVO.setReqSelector(selector);
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
            p_requestVO.setCellId(cellId);
            p_requestVO.setSwitchId(switchId);
            p_requestVO.setUssdSessionID(ussdSessionID);
            } catch (BTSLBaseException btsle) {
            	loggerValue.setLength(0);
    			loggerValue.append(PretupsI.BTSLEXCEPTION);
    			loggerValue.append(btsle.getMessage());
    			_log.error(methodName, loggerValue);
                throw btsle;
            }
            catch (Exception e) {
            	loggerValue.setLength(0);
    			loggerValue.append(PretupsI.EXCEPTION);
    			loggerValue.append(e.getMessage());
    			_log.error(methodName, loggerValue);
                throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
            } finally {
            	if (_log.isDebugEnabled()) {
                 	loggerValue.setLength(0);
                 	loggerValue.append(PretupsI.EXITED_VALUE);
                 	loggerValue.append(p_requestVO.toString());
                 	_log.debug(methodName, loggerValue);
                 }
            }
       
        }

	
}
