package com.btsl.pretups.gateway.util;

/*
 * @(#)USSDC2SXMLStringParser.java
 * Copyright(c) 2005, Bharti Telesoft Ltd.
 * All Rights Reserved
 * ------------------------------------------------------------------------------
 * -------------------
 * Author Date History
 * ------------------------------------------------------------------------------
 * -------------------
 * Manoj Kumar Jan 25, 2006 Initital Creation
 * Ankit Singhal Dec 13, 2006 Modified
 * Kapil Mehta Feb 03, 2009 Modified
 * Harpreet Kaur OCT 18,2011 Modified
 * Gaurav pandey jan 12, 2013 Modified
 * Vibhu Trehan Jun 27, 2014 Split of file XMLStringParser
 * ------------------------------------------------------------------------------
 * -------------------
 * XML String class
 */
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.XMLTagValueValidation;
import com.btsl.voms.vomscommon.VOMSI;

/**
 * @author sanjeew.kumar
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class USSDC2SXMLStringParser {

    private static final Log _log = LogFactory.getLog(USSDC2SXMLStringParser.class.getName());

    private static String p2p_message_sep = null;
    private static String chnl_message_sep = null;
	private static final String EXCEPTION = "EXCEPTION: ";
    private static final String BTSL_EXCEPTION = "BTSLBASEEXCEPTION: ";
    private static final String EXIT_KEY = "Exiting: p_requestVO:";
    private static final String ENTRY_KEY = "Entered: p_requestVO=";
    private static final String CLASS_NAME = "USSDC2SXMLStringParser";
    private static final String EXIT_KEY_RES = "Exiting: responseStr:";
	private static final String DOC_TYPE = "<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>";


    static {
    	String chnlPlainSmsSeparator = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
        String p2pPlainSmsSeparator = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_PLAIN_SMS_SEPARATOR);
        try {
            chnl_message_sep = chnlPlainSmsSeparator;
            if (BTSLUtil.isNullString(chnl_message_sep)) {
                chnl_message_sep = " ";
            }
            p2p_message_sep = p2pPlainSmsSeparator;
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
    private USSDC2SXMLStringParser(){
    	
    }
    
    /***
     * @ addded by Jasmine kaur For Private Recharge Registration parsing XML
     * message
     * 
     * @param p_requestVO
     * @throws Exception
     * @author Jasmine kaur
     * @Date 2/1/11
     */
    public static void parseChannelRegistrationRequest(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("parseChannelRegistrationRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        String sid = null;
        String newsid = null;
        final String methodName = "parseChannelRegistrationRequest";
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<EXTNWCODE>");
            if(index >= 0)
            {
            	 final String networkCode = requestStr.substring(index + "<EXTNWCODE>".length(), requestStr.indexOf("</EXTNWCODE>", index));
            	 p_requestVO.setExternalNetworkCode(networkCode);
                
            }
            index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<MSISDN>");
            final String msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
            index = requestStr.indexOf("<SID>");
            sid = requestStr.substring(index + "<SID>".length(), requestStr.indexOf("</SID>", index));
            index = requestStr.indexOf("<NEWSID>");
            newsid = requestStr.substring(index + "<NEWSID>".length(), requestStr.indexOf("</NEWSID>", index));
            if ((BTSLUtil.isNullString(sid)) && (BTSLUtil.isNullString(newsid))) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_SIDREG;
            } else if ((!BTSLUtil.isNullString(sid)) && (BTSLUtil.isNullString(newsid))) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_SIDREG + chnl_message_sep + sid;
            } else if ((!BTSLUtil.isNullString(sid)) && (!BTSLUtil.isNullString(newsid))) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_SIDREG + chnl_message_sep + sid + chnl_message_sep + newsid;
            } else {
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelRegistrationRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            }
            if (BTSLUtil.isNullString(msisdn)) {
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelRegistrationRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            }

            // -----for language param in request
            index = requestStr.indexOf("<LANGUAGE1>");
            String language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));

            // Added By Diwakar for OCM on 08-MAY-2014
            if (BTSLUtil.isNullString(language1)) {
                language1 = Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
            }

            if (BTSLUtil.isNullString(language1)) {
                language1 = "1";
            }

            if (!BTSLUtil.isNullString(language1)) {
                if (!("0".equalsIgnoreCase(language1)) || !("1".equalsIgnoreCase(language1))) {
                    if (BTSLUtil.isNullString(language1)) {
                        language1 = Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
                    }

                    if (BTSLUtil.isNullString(language1)) {
                        language1 = "1";
                    }
                }
                p_requestVO.setLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            }

            // ---------------------------------
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseChannelRegistrationRequest", "Exception e: " + e);
            throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelRegistrationRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseChannelRegistrationRequest", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method parses the USSD request for deleting SID and stores it in a
     * VO
     * 
     * @param p_requestVO
     * @throws Exception
     * @author ankuj.arora
     * @Date 24/12/10
     */
    public static void parseDeleteSIDRequest(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("parseDeleteSIDRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<MSISDN>");
            final String msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
            index = requestStr.indexOf("<SID>");
            final String sid = requestStr.substring(index + "<SID>".length(), requestStr.indexOf("</SID>", index));
            
            
        
            
            
            
            if (BTSLUtil.isNullString(msisdn)) {
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseDeleteSIDRequest", PretupsErrorCodesI.MSISDN_NULL);
            }
            
             if (BTSLUtil.isNullString(sid)) {
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseDeleteSIDRequest", PretupsErrorCodesI.SID_INVALID);
            }
            
             parsedRequestStr = PretupsI.SERVICE_TYPE_SID_DELETE + chnl_message_sep + msisdn + chnl_message_sep + sid; 
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn);
        } catch (BTSLBaseException be) {
            _log.error("parseDeleteSIDRequest", " BTSL Exception while parsing Request Message :" + be.getMessage());
            throw be;
        }
          catch (Exception e) {
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseDeleteSIDRequest", "Exception e: " + e);
            throw new BTSLBaseException("USSDC2SXMLStringParser", "parseGetAccountInfoRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseDeleteSIDRequest", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method parses a request for the enquiry SID request in the case of
     * private recharge.
     * 
     * @param p_requestVO
     * @throws Exception
     * @author Ankuj.Arora
     * @Date 29/12/10
     */
    public static void parseEnquirySIDRequest(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("parseEnquirySIDRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<EXTNWCODE>");
            if(index >= 0)
            {
            	 final String networkCode = requestStr.substring(index + "<EXTNWCODE>".length(), requestStr.indexOf("</EXTNWCODE>", index));
            	 p_requestVO.setExternalNetworkCode(networkCode);
                
            }
             index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<MSISDN>");
            final String msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
           
            if (BTSLUtil.isNullString(msisdn)) {
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseEnquirySIDRequest", PretupsErrorCodesI.MSISDN_NULL);
            }
            parsedRequestStr = PretupsI.SERVICE_TYPE_SID_ENQUIRY + chnl_message_sep + msisdn;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn);
           
        }catch (BTSLBaseException be) {
            _log.error("parseDeleteSIDRequest", " BTSL Exception while parsing Request Message :" + be.getMessage());
            throw be;
        }
        
        
        catch (Exception e) {
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseEnquirySIDRequest", "Exception e: " + e);
            throw new BTSLBaseException("USSDC2SXMLStringParser", "parseEnquirySIDRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseEnquirySIDRequest", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method is used to parse the Schedule multiple credit transfer
     * request initiated by External System
     * 
     * @param p_requestVO
     * @throws BTSLBaseException
     * @throws Exception
     * @author vikas.kumar
     */
    public static void parseScheduleCreditTransferRequest(RequestVO p_requestVO) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("parseScheduleCreditTransferRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<MSISDN1>");
            final String msisdn1 = requestStr.substring(index + "<MSISDN1>".length(), requestStr.indexOf("</MSISDN1>", index));
            index = requestStr.indexOf("<PIN>");
            String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            index = requestStr.indexOf("<MSISDN2>");
            final String msisdn2 = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));
            index = requestStr.indexOf("<AMOUNT>");
            final String amount = requestStr.substring(index + "<AMOUNT>".length(), requestStr.indexOf("</AMOUNT>", index));
            index = requestStr.indexOf("<LANGUAGE1>");
            String language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
            index = requestStr.indexOf("<LANGUAGE2>");
            String language2 = requestStr.substring(index + "<LANGUAGE2>".length(), requestStr.indexOf("</LANGUAGE2>", index));
            index = requestStr.indexOf("<SELECTOR>");
            String selector = requestStr.substring(index + "<SELECTOR>".length(), requestStr.indexOf("</SELECTOR>", index));
            index = requestStr.indexOf("<SCTYPE>");
            final String schType = requestStr.substring(index + "<SCTYPE>".length(), requestStr.indexOf("</SCTYPE>", index));
            index = requestStr.indexOf("<SDATE>");
             String schDate = requestStr.substring(index + "<SDATE>".length(), requestStr.indexOf("</SDATE>", index));
            schDate = BTSLDateUtil.getGregorianDateInString(schDate);

            // amount field should be mandatory
            if (BTSLUtil.isNullString(amount)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_BLANK_AMOUNT);
                _log.error("parseCreditTransferRequest", "Amount field is null");
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseCreditTransferRequest", PretupsErrorCodesI.P2P_ERROR_BLANK_AMOUNT);
            }
            String p2pDefaultSmsPin = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN);
            // added by sanjay to set default pin
            if (BTSLUtil.isNullString(pin)) {
                // pin=PretupsI.DEFAULT_P2P_PIN;
                pin = p2pDefaultSmsPin;
            }

            // added by sanjay 10/01/2006 - to set default language code
            if (_log.isDebugEnabled()) {
                _log.debug("parseScheduleCreditTransferRequest", "language1:" + language1 + ":");
            }

            if (BTSLUtil.isNullString(language1)) {
                language1 = Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
            }

            if (BTSLUtil.isNullString(language1)) {
                language1 = "0";
            }

            if (_log.isDebugEnabled()) {
                _log.debug("parseScheduleCreditTransferRequest", "language1:" + language1 + ":");
                // added by sanjay 10/01/2006 - to set default language code
            }

            if (_log.isDebugEnabled()) {
                _log.debug("parseScheduleCreditTransferRequest", "language2:" + language2 + ":");
            }

            if (BTSLUtil.isNullString(language2)) {
                language2 = Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
            }

            if (BTSLUtil.isNullString(language2)) {
                language2 = "0";
            }
            if (_log.isDebugEnabled()) {
                _log.debug("parseScheduleCreditTransferRequest", "language2:" + language2 + ":");
            }

            if (BTSLUtil.isNullString(selector)) {

                final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(PretupsI.SERVICE_TYPE_P2PRECHARGE);
                if (serviceSelectorMappingVO != null) {
                    selector = serviceSelectorMappingVO.getSelectorCode();
                }
            }

            parsedRequestStr = PretupsI.SERVICE_TYPE_SCH_CREDIT_TRANSFER + p2p_message_sep + msisdn2 + p2p_message_sep + amount + (p2p_message_sep + selector) + (p2p_message_sep + language2) + p2p_message_sep + pin + p2p_message_sep + schType + p2p_message_sep + schDate;
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(language2));
            p_requestVO.setReqSelector(selector);
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
        } catch (BTSLBaseException be) {
            _log.error("parseScheduleCreditTransferRequest", " BTSL Exception while parsing Request Message :" + be.getMessage());
            throw be;
        } catch (Exception e) {
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseScheduleCreditTransferRequest", "Exception e: " + e);
            throw new BTSLBaseException("USSDC2SXMLStringParser", "parseScheduleCreditTransferRequest", PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseScheduleCreditTransferRequest", "Exiting p_requestVO: " + p_requestVO.toString());
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
        final String METHOD_NAME = "parseChannelBalanceEnquiry";
        if (_log.isDebugEnabled()) {
            _log.debug("parseChannelBalanceEnquiry", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        // boolean tagsMandatory;
        String cellId = null;
        String switchId = null;
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            index = requestStr.indexOf("<MSISDN1>");
            final String msisdn1 = requestStr.substring(index + "<MSISDN1>".length(), requestStr.indexOf("</MSISDN1>", index));
            if(BTSLUtil.isNullString(msisdn1)){
            	throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelBalanceEnquiry", PretupsErrorCodesI.XML_ERROR_MSISDN_IS_NULL);
            }
            String msisdn2 = "";
            try {
                index = requestStr.indexOf("<MSISDN2>");
                msisdn2 = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            index = requestStr.indexOf("<CELLID>");
            if (index >= 0) {
                cellId = requestStr.substring(index + "<CELLID>".length(), requestStr.indexOf("</CELLID>", index));
            }
            index = requestStr.indexOf("<SWITCHID>");
            if (index >= 0) {
                switchId = requestStr.substring(index + "<SWITCHID>".length(), requestStr.indexOf("</SWITCHID>", index));
            }
            boolean ussdNewTagsMandatory = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY);
            // tagsMandatory=ussdNewTagsMandatory;
            if (ussdNewTagsMandatory) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelBalanceEnquiry", PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelBalanceEnquiry", PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
                    }
                } catch (BTSLBaseException btsle) {
                    throw btsle;
                }
            }
            parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_BALANCE_ENQUIRY + chnl_message_sep + msisdn2 + chnl_message_sep + pin;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
            p_requestVO.setCellId(cellId);
            p_requestVO.setSwitchId(switchId);
        }
        catch (BTSLBaseException be) {
            _log.error("parseChannelBalanceEnquiry", " BTSL Exception while parsing Request Message :" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseChannelBalanceEnquiry", "Exception e: " + e);
            throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelBalanceEnquiry", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseChannelBalanceEnquiry", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method generates Daily Status Report Request from XML String and
     * formating into white space seperated String
     * 
     * @param p_requestVO
     *            RequestVO
     * @author sanjeew.kumar
     * @Date 03/05/07
     */
    public static void parseChannelDailyStatusReport(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("parseChannelDailyStatusReport", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        // boolean tagsMandatory;
        String cellId = null;
        String switchId = null;
        final String methodName = "parseChannelDailyStatusReport";
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            index = requestStr.indexOf("<MSISDN1>");
            final String msisdn1 = requestStr.substring(index + "<MSISDN1>".length(), requestStr.indexOf("</MSISDN1>", index));
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            index = requestStr.indexOf("<CELLID>");
            if (index >= 0) {
                cellId = requestStr.substring(index + "<CELLID>".length(), requestStr.indexOf("</CELLID>", index));
            }
            index = requestStr.indexOf("<SWITCHID>");
            if (index >= 0) {
                switchId = requestStr.substring(index + "<SWITCHID>".length(), requestStr.indexOf("</SWITCHID>", index));
            }
            if (BTSLUtil.isNullString(pin)) {
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelTransferRequest", PretupsErrorCodesI.C2S_PIN_BLANK);
            }
            boolean ussdNewTagsMandatory = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY);
            // tagsMandatory=ussdNewTagsMandatory;
            if (ussdNewTagsMandatory) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelDailyStatusReport", PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelDailyStatusReport", PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
                    }
                } catch (BTSLBaseException btsle) {
                    throw btsle;
                }
            }
            parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_DAILY_STATUS_REPORT + chnl_message_sep + pin;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
            p_requestVO.setCellId(cellId);
            p_requestVO.setSwitchId(switchId);
        } catch (BTSLBaseException Excp) {
            throw Excp;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseChannelDailyStatusReport", "Exception e: " + e);
            throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelDailyStatusReport", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseChannelDailyStatusReport", "Exiting p_requestVO: " + p_requestVO.toString());
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
        if (_log.isDebugEnabled()) {
            _log.debug("parseChannelLastTransferStatus", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        // boolean tagsMandatory;
        String cellId = null;
        String switchId = null;
        final String methodName = "parseChannelLastTransferStatus";
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            index = requestStr.indexOf("<MSISDN1>");
            final String msisdn1 = requestStr.substring(index + "<MSISDN1>".length(), requestStr.indexOf("</MSISDN1>", index));
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));

            index = requestStr.indexOf("<CELLID>");
            if (index >= 0) {
                cellId = requestStr.substring(index + "<CELLID>".length(), requestStr.indexOf("</CELLID>", index));
            }
            index = requestStr.indexOf("<SWITCHID>");
            if (index >= 0) {
                switchId = requestStr.substring(index + "<SWITCHID>".length(), requestStr.indexOf("</SWITCHID>", index));
            }
            boolean ussdNewTagsMandatory = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY);
            // tagsMandatory=ussdNewTagsMandatory;
            if (ussdNewTagsMandatory) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelLastTransferStatus", PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelLastTransferStatus", PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
                    }
                } catch (BTSLBaseException btsle) {
                    throw btsle;
                }
            } 
            if(BTSLUtil.isNullString(pin))
            	{   parsedRequestStr = PretupsI.SERVICE_TYPE_LAST_TRANSFER_STATUS + chnl_message_sep + "BlankPin";}
            else{
            parsedRequestStr = PretupsI.SERVICE_TYPE_LAST_TRANSFER_STATUS + chnl_message_sep + pin;}
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
            p_requestVO.setCellId(cellId);
            p_requestVO.setSwitchId(switchId);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseChannelLastTransferStatus", "Exception e: " + e);
            throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelLastTransferStatus", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseChannelLastTransferStatus", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method generates Channel EVD Request from XML String and formating
     * into white space seperated String
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void parseChannelEVDRequest(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("parseChannelEVDRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        final String methodName = "parseChannelEVDRequest";
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<MSISDN1>");
            final String msisdn1 = requestStr.substring(index + "<MSISDN1>".length(), requestStr.indexOf("</MSISDN1>", index));
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            index = requestStr.indexOf("<MSISDN2>");
            final String msisdn2 = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));
            index = requestStr.indexOf("<AMOUNT>");
            final String amount = requestStr.substring(index + "<AMOUNT>".length(), requestStr.indexOf("</AMOUNT>", index));
            index = requestStr.indexOf("<LANGUAGE1>");
            String language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
            index = requestStr.indexOf("<LANGUAGE2>");
            String language2 = requestStr.substring(index + "<LANGUAGE2>".length(), requestStr.indexOf("</LANGUAGE2>", index));
            index = requestStr.indexOf("<SELECTOR>");
            final String selector = requestStr.substring(index + "<SELECTOR>".length(), requestStr.indexOf("</SELECTOR>", index));
            if (BTSLUtil.isNullString(amount)) {
                _log.error("parseChannelEVDRequest", "Amount field is null ");
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelEVDRequest", PretupsErrorCodesI.C2S_ERROR_BLANK_AMOUNT);
            }
            if (BTSLUtil.isNullString(msisdn2)) {
                _log.error("parseChannelEVDRequest", "Msisdn2 field is null ");
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelEVDRequest", PretupsErrorCodesI.CHNL_ERROR_RECR_MSISDN_BLANK);
            }
            if (BTSLUtil.isNullString(language1)) {
                language1 = Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
            }

            if (BTSLUtil.isNullString(language1)) {
                language1 = "0";
            }

            if (BTSLUtil.isNullString(language2)) {
                language1 = Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
            }

            if (BTSLUtil.isNullString(language2)) {
                language2 = "0";
            }
            if (BTSLUtil.isNullString(selector)) {
                _log.error("parseChannelEVDRequest", "Selector field is null ");
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelEVDRequest", PretupsErrorCodesI.C2S_EXTGW_BLANK_SELECTOR);
            }
            // parsedRequestStr=PretupsI.SERVICE_TYPE_CHNL_EVD+CHNL_MESSAGE_SEP+msisdn2+CHNL_MESSAGE_SEP+amount+CHNL_MESSAGE_SEP+language1+CHNL_MESSAGE_SEP+language2+CHNL_MESSAGE_SEP+selector+CHNL_MESSAGE_SEP+pin;
            parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_EVD + chnl_message_sep + msisdn2 + chnl_message_sep + amount + chnl_message_sep + selector + chnl_message_sep + language1 + chnl_message_sep + language2 + chnl_message_sep + pin;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
        } catch (BTSLBaseException be) {
            _log.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseChannelEVDRequest", "Exception e: " + e);
            throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelEVDRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseChannelEVDRequest", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * Method to parse External Interface Muliple Electronic Voucher
     * Distribution Request
     * 
     * @param p_requestVO
     * @throws Exception
     */
    public static void parseMultipleVoucherDistributionRequest(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("parseMultipleVoucherDistributionRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        final String methodName = "parseMultipleVoucherDistributionRequest";
        try {
            final HashMap requestHashMap = new HashMap();
            final String requestStr = p_requestVO.getRequestMessage();

            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            requestHashMap.put("TYPE", type);
            index = requestStr.indexOf("<MSISDN1>");
            final String msisdn1 = requestStr.substring(index + "<MSISDN1>".length(), requestStr.indexOf("</MSISDN1>", index));
            requestHashMap.put("MSISDN1", msisdn1);
            index = requestStr.indexOf("<MSISDN2>");
            final String msisdn2 = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));
            requestHashMap.put("MSISDN2", msisdn2);
            index = requestStr.indexOf("<AMOUNT>");
            final String amount = requestStr.substring(index + "<AMOUNT>".length(), requestStr.indexOf("</AMOUNT>", index));
            requestHashMap.put("AMOUNT", amount);
            index = requestStr.indexOf("<QTY>");
            final String qty = requestStr.substring(index + "<QTY>".length(), requestStr.indexOf("</QTY>", index));
            requestHashMap.put("QTY", qty);
            index = requestStr.indexOf("<LANGUAGE1>");
            final String language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
            requestHashMap.put("LANGUAGE1", language1);
            index = requestStr.indexOf("<LANGUAGE2>");
            final String language2 = requestStr.substring(index + "<LANGUAGE2>".length(), requestStr.indexOf("</LANGUAGE2>", index));
            requestHashMap.put("LANGUAGE2", language2);
            index = requestStr.indexOf("<SELECTOR>");
            final String selector = requestStr.substring(index + "<SELECTOR>".length(), requestStr.indexOf("</SELECTOR>", index));
            requestHashMap.put("SELECTOR", selector);
            index = requestStr.indexOf("<PIN>");
            String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            requestHashMap.put("PIN", pin);            
            if (BTSLUtil.isNullString(pin)) {
            	String c2sDefaultSmsPin = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_SMSPIN);
                pin = c2sDefaultSmsPin;
            }
            parsedRequestStr = PretupsI.SERVICE_TYPE_MVD + chnl_message_sep + msisdn2 + chnl_message_sep + amount + chnl_message_sep + qty + chnl_message_sep + pin;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(language2));
            p_requestVO.setReqSelector(selector);
            p_requestVO.setRequestMap(requestHashMap);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseMultipleVoucherDistributionRequest", "Exception e: " + e);
            throw new BTSLBaseException("USSDC2SXMLStringParser", "parseMultipleVoucherDistributionRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseMultipleVoucherDistributionRequest", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * @param p_requestVO
     * @throws Exception
     * @author shishupal.singh
     */
    public static void parseUtilityBillPaymentRequest(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("parseUtilityBillPaymentRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        final String methodName = "parseUtilityBillPaymentRequest";
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<MSISDN1>");
            final String msisdn1 = requestStr.substring(index + "<MSISDN1>".length(), requestStr.indexOf("</MSISDN1>", index));
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            index = requestStr.indexOf("<MSISDN2>");
            final String msisdn2 = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));
            index = requestStr.indexOf("<AMOUNT>");
            final String amount = requestStr.substring(index + "<AMOUNT>".length(), requestStr.indexOf("</AMOUNT>", index));
            index = requestStr.indexOf("<LANGUAGE1>");
            final String language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
            index = requestStr.indexOf("<LANGUAGE2>");
            final String language2 = requestStr.substring(index + "<LANGUAGE2>".length(), requestStr.indexOf("</LANGUAGE2>", index));
            index = requestStr.indexOf("<SELECTOR>");
            String selector = requestStr.substring(index + "<SELECTOR>".length(), requestStr.indexOf("</SELECTOR>", index));
            index = requestStr.indexOf("<SUBSERVICE>");
            final String subservice = requestStr.substring(index + "<SUBSERVICE>".length(), requestStr.indexOf("</SUBSERVICE>", index));
            if (BTSLUtil.isNullString(selector)) {
                // selector=""+SystemPreferences.C2S_DEF_SELECTOR_CODE_BILLPAY;
                // Changed on 27/05/07 for Service Type selector Mapping
                final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(PretupsI.SERVICE_TYPE_UTILITY_BILLPAY);
                if (serviceSelectorMappingVO != null) {
                    selector = serviceSelectorMappingVO.getSelectorCode();
                }
            }
            String defFrcXmlSelBillPay = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEF_FRCXML_SEL_BILLPAY);
            // if we have to forcibaly change the value of default selecter in
            // case of ussd request.
            if (!BTSLUtil.isNullString(defFrcXmlSelBillPay)) {
                if (_log.isDebugEnabled()) {
                    _log.debug("parseUtilityBillPaymentRequest", "using default force selecter of XML");
                }
                selector = "" + defFrcXmlSelBillPay;
            }
            if (!BTSLUtil.isNullString(subservice)) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_UTILITY_BILLPAY + chnl_message_sep + subservice + chnl_message_sep + msisdn2 + chnl_message_sep + amount + chnl_message_sep + selector + chnl_message_sep + language1 + chnl_message_sep + msisdn1 + chnl_message_sep + language2 + chnl_message_sep + pin;
            } else {
                parsedRequestStr = PretupsI.SERVICE_TYPE_UTILITY_BILLPAY + chnl_message_sep + msisdn2 + chnl_message_sep + amount + chnl_message_sep + selector + chnl_message_sep + language1 + chnl_message_sep + msisdn1 + chnl_message_sep + language2 + chnl_message_sep + pin;
            }
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setReqSelector(selector);
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseUtilityBillPaymentRequest", "Exception e: " + e);
            throw new BTSLBaseException("USSDC2SXMLStringParser", "parseUtilityBillPaymentRequest", PretupsErrorCodesI.BILLPAY_INVALID_MESSAGE_FORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseUtilityBillPaymentRequest", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * Parse the Gift recharge Request coming through USSD
     * 
     * @param p_requestVO
     * @throws Exception
     */
    public static void parseGiftRechargeRequest(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("parseGiftRechargeRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        final String methodName = "parseGiftRechargeRequest";
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<MSISDN1>");
            final String msisdn1 = requestStr.substring(index + "<MSISDN1>".length(), requestStr.indexOf("</MSISDN1>", index));
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            index = requestStr.indexOf("<MSISDN2>");
            final String msisdn2 = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));
            index = requestStr.indexOf("<AMOUNT>");
            final String amount = requestStr.substring(index + "<AMOUNT>".length(), requestStr.indexOf("</AMOUNT>", index));
            index = requestStr.indexOf("<LANGUAGE1>");
            String language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
            index = requestStr.indexOf("<LANGUAGE2>");
            String language2 = requestStr.substring(index + "<LANGUAGE2>".length(), requestStr.indexOf("</LANGUAGE2>", index));
            index = requestStr.indexOf("<SELECTOR>");
            String selector = requestStr.substring(index + "<SELECTOR>".length(), requestStr.indexOf("</SELECTOR>", index));
            if (BTSLUtil.isNullString(selector)) {
                final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(PretupsI.SERVICE_TYPE_GIFT_RECHARGE);
                if (serviceSelectorMappingVO != null) {
                    selector = serviceSelectorMappingVO.getSelectorCode();
                }
            }
            String defFrcXmlSelC2S = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEF_FRCXML_SEL_C2S);
            // if we have to forcibaly change the value of default selecter in
            // case of ussd request.
            if (!BTSLUtil.isNullString(defFrcXmlSelC2S)) {
                if (_log.isDebugEnabled()) {
                    _log.debug("parseGiftRechargeRequest", "using default force selecter of XML");
                }
                selector = String.valueOf(defFrcXmlSelC2S);
            }
            index = requestStr.indexOf("<GIFTER_MSISDN>");
            final String gifterMsisdn = requestStr.substring(index + "<GIFTER_MSISDN>".length(), requestStr.indexOf("</GIFTER_MSISDN>", index));
            index = requestStr.indexOf("<GIFTER_NAME>");
            final String gifterName = requestStr.substring(index + "<GIFTER_NAME>".length(), requestStr.indexOf("</GIFTER_NAME>", index));
            index = requestStr.indexOf("<GIFTER_LANGUAGE>");
            final String gifterLanguage = requestStr.substring(index + "<GIFTER_LANGUAGE>".length(), requestStr.indexOf("</GIFTER_LANGUAGE>", index));
            LocaleMasterVO localeMasterVO = null;
            String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
            String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
            if (BTSLUtil.isNullString(language1)) {
                p_requestVO.setSenderLocale(new Locale(defaultLanguage, defaultCountry));
                localeMasterVO = LocaleMasterCache.getLocaleDetailsFromlocale(p_requestVO.getSenderLocale());
                language1 = localeMasterVO.getLanguage_code();
            }

            if (BTSLUtil.isNullString(language2)) {
                p_requestVO.setReceiverLocale(new Locale(defaultLanguage, defaultCountry));
                localeMasterVO = LocaleMasterCache.getLocaleDetailsFromlocale(p_requestVO.getReceiverLocale());
                language2 = localeMasterVO.getLanguage_code();
            }
            if (BTSLUtil.isNullString(msisdn2)) {
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseExtGiftRechargeRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            }

            if (BTSLUtil.isNullString(gifterMsisdn)) {
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseExtGiftRechargeRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            }

            if (BTSLUtil.isNullString(gifterName)) {
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseExtGiftRechargeRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            }
            if (BTSLUtil.isNullString(amount)) {
                _log.error("parseCreditRechargeRequest", "Amount field is null ");
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseGiftRechargeRequest", PretupsErrorCodesI.C2S_ERROR_BLANK_AMOUNT);
            }
            if(LocaleMasterCache.getLocaleFromCodeDetails(language1) == null )
            {
          	  throw new BTSLBaseException("USSDC2SXMLStringParser", "parseCreditTransferRequest", PretupsErrorCodesI.LANG_CODE_NOT_EXIST);
            }
            if(LocaleMasterCache.getLocaleFromCodeDetails(language2)==null)
            {
          	  throw new BTSLBaseException("USSDC2SXMLStringParser", "parseCreditTransferRequest", PretupsErrorCodesI.LANG_CODE_NOT_EXIST);
            }
            parsedRequestStr = PretupsI.SERVICE_TYPE_CHANNEL_GIFT_RECHARGE + chnl_message_sep + msisdn2 + chnl_message_sep + amount + chnl_message_sep + selector + chnl_message_sep + language2 + chnl_message_sep + language1 + chnl_message_sep + gifterMsisdn + chnl_message_sep + gifterName + chnl_message_sep + gifterLanguage + chnl_message_sep + pin;
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(language2));
            p_requestVO.setGifterLocale(LocaleMasterCache.getLocaleFromCodeDetails(gifterLanguage));
            p_requestVO.setReqSelector(selector);
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
            p_requestVO.setGifterMSISDN(gifterMsisdn);
            p_requestVO.setGifterName(gifterName);
        } catch (BTSLBaseException be) {
            _log.error("parseGiftRechargeRequest", " BTSL Exception while parsing Request Message :" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseGiftRechargeRequest", "Exception e: " + e);
            throw new BTSLBaseException("USSDC2SXMLStringParser", "parseGiftRechargeRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseGiftRechargeRequest", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * Added for CDMA Recharge via USSD system.
     * 
     * @param p_requestVO
     * @param p_action
     * @throws Exception
     * @author kapil.mehta
     */

    public static void parseChannelCreditTransferCDMARequest(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("parseChannelCreditTransferCDMARequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        final String methodName = "parseChannelCreditTransferCDMARequest";
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<MSISDN1>");
            final String msisdn1 = requestStr.substring(index + "<MSISDN1>".length(), requestStr.indexOf("</MSISDN1>", index));
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            index = requestStr.indexOf("<MSISDN2>");
            final String msisdn2 = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));
            index = requestStr.indexOf("<AMOUNT>");
            final String amount = requestStr.substring(index + "<AMOUNT>".length(), requestStr.indexOf("</AMOUNT>", index));
            index = requestStr.indexOf("<LANGUAGE1>");
            final String language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
            index = requestStr.indexOf("<LANGUAGE2>");
            final String language2 = requestStr.substring(index + "<LANGUAGE2>".length(), requestStr.indexOf("</LANGUAGE2>", index));
            index = requestStr.indexOf("<SELECTOR>");
            String selector = requestStr.substring(index + "<SELECTOR>".length(), requestStr.indexOf("</SELECTOR>", index));
            if (BTSLUtil.isNullString(selector)) {
                // selector=""+SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE;
                // Changed on 27/05/07 for Service Type selector Mapping
                final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(PretupsI.SERVICE_TYPE_CHNL_RECHARGE);
                if (serviceSelectorMappingVO != null) {
                    selector = serviceSelectorMappingVO.getSelectorCode();
                }
            }
            if (BTSLUtil.isNullString(amount)) {
                _log.error("parseChannelCreditTransferCDMARequest", "Amount field is null ");
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelCreditTransferCDMARequest", PretupsErrorCodesI.C2S_ERROR_BLANK_AMOUNT);
            }
            String defFrcXmlSelC2S = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEF_FRCXML_SEL_C2S);
            // if we have to forcibaly change the value of default selecter in
            // case of ussd request.
            if (!BTSLUtil.isNullString(defFrcXmlSelC2S)) {
                if (_log.isDebugEnabled()) {
                    _log.debug("parseChannelCreditTransferCDMARequest", "using default force selecter of XML");
                }
                selector = "" + defFrcXmlSelC2S;
            }

            parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_RECHARGE_CDMA + chnl_message_sep + msisdn2 + chnl_message_sep + amount + chnl_message_sep + selector + chnl_message_sep + language2 + chnl_message_sep + pin;
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setReqSelector(selector);
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseChannelCreditTransferCDMARequest", "Exception e: " + e);
            throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelCreditTransferCDMARequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseChannelCreditTransferCDMARequest", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * Added for PSTN Recharge via USSD system.
     * 
     * @param p_requestVO
     * @param p_action
     * @throws Exception
     * @author kapil.mehta
     */

    public static void parseChannelCreditTransferPSTNRequest(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("parseChannelCreditTransferPSTNRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        final String methodName = "parseChannelCreditTransferPSTNRequest";
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<MSISDN1>");
            final String msisdn1 = requestStr.substring(index + "<MSISDN1>".length(), requestStr.indexOf("</MSISDN1>", index));
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            index = requestStr.indexOf("<MSISDN2>");
            final String msisdn2 = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));
            index = requestStr.indexOf("<AMOUNT>");
            final String amount = requestStr.substring(index + "<AMOUNT>".length(), requestStr.indexOf("</AMOUNT>", index));
            index = requestStr.indexOf("<LANGUAGE1>");
            final String language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
            index = requestStr.indexOf("<LANGUAGE2>");
            final String language2 = requestStr.substring(index + "<LANGUAGE2>".length(), requestStr.indexOf("</LANGUAGE2>", index));
            index = requestStr.indexOf("<SELECTOR>");
            String selector = requestStr.substring(index + "<SELECTOR>".length(), requestStr.indexOf("</SELECTOR>", index));
            if (BTSLUtil.isNullString(selector)) {
                // selector=""+SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE;
                // Changed on 27/05/07 for Service Type selector Mapping
                final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(PretupsI.SERVICE_TYPE_CHNL_RECHARGE);
                if (serviceSelectorMappingVO != null) {
                    selector = serviceSelectorMappingVO.getSelectorCode();
                }
            }
            if (BTSLUtil.isNullString(amount)) {
                _log.error("parseChannelCreditTransferPSTNRequest", "Amount field is null ");
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelCreditTransferPSTNRequest", PretupsErrorCodesI.C2S_ERROR_BLANK_AMOUNT);
            }

            index = requestStr.indexOf("<NOTIFICATION_MSISDN>");
            final String notification_msisdn = requestStr.substring(index + "<NOTIFICATION_MSISDN>".length(), requestStr.indexOf("</NOTIFICATION_MSISDN>", index));
            String defFrcXmlSelC2S = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEF_FRCXML_SEL_C2S);
            // if we have to forcibaly change the value of default selecter in
            // case of ussd request.
            if (!BTSLUtil.isNullString(defFrcXmlSelC2S)) {
                if (_log.isDebugEnabled()) {
                    _log.debug("parseChannelCreditTransferPSTNRequest", "using default force selecter of XML");
                }
                selector = "" + defFrcXmlSelC2S;
            }

            parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_RECHARGE_PSTN + chnl_message_sep + msisdn2 + chnl_message_sep + amount + chnl_message_sep + notification_msisdn + chnl_message_sep + selector + chnl_message_sep + language2 + chnl_message_sep + pin;
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setReqSelector(selector);
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
            p_requestVO.setNotificationMSISDN(notification_msisdn);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseChannelCreditTransferPSTNRequest", "Exception e: " + e);
            throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelCreditTransferPSTNRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseChannelCreditTransferPSTNRequest", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * Added for Broadband Recharge via USSD system.
     * 
     * @param p_requestVO
     * @param p_action
     * @throws Exception
     * @author kapil.mehta
     */

    public static void parseChannelCreditTransferINTRRequest(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("parseChannelCreditTransferINTRRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        final String methodName = "parseChannelCreditTransferINTRRequest";
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<MSISDN1>");
            final String msisdn1 = requestStr.substring(index + "<MSISDN1>".length(), requestStr.indexOf("</MSISDN1>", index));
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            index = requestStr.indexOf("<MSISDN2>");
            final String msisdn2 = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));
            index = requestStr.indexOf("<AMOUNT>");
            final String amount = requestStr.substring(index + "<AMOUNT>".length(), requestStr.indexOf("</AMOUNT>", index));
            index = requestStr.indexOf("<LANGUAGE1>");
            final String language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
            index = requestStr.indexOf("<LANGUAGE2>");
            final String language2 = requestStr.substring(index + "<LANGUAGE2>".length(), requestStr.indexOf("</LANGUAGE2>", index));
            index = requestStr.indexOf("<SELECTOR>");
            String selector = requestStr.substring(index + "<SELECTOR>".length(), requestStr.indexOf("</SELECTOR>", index));
            if (BTSLUtil.isNullString(selector)) {
                // selector=""+SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE;
                // Changed on 27/05/07 for Service Type selector Mapping
                final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(PretupsI.SERVICE_TYPE_CHNL_RECHARGE);
                if (serviceSelectorMappingVO != null) {
                    selector = serviceSelectorMappingVO.getSelectorCode();
                }
            }
            if (BTSLUtil.isNullString(amount)) {
                _log.error("parseChannelCreditTransferINTRRequest", "Amount field is null ");
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelCreditTransferINTRRequest", PretupsErrorCodesI.C2S_ERROR_BLANK_AMOUNT);
            }
           
            index = requestStr.indexOf("<NOTIFICATION_MSISDN>");
            final String notification_msisdn = requestStr.substring(index + "<NOTIFICATION_MSISDN>".length(), requestStr.indexOf("</NOTIFICATION_MSISDN>", index));
            
            if(BTSLUtil.isNullString(notification_msisdn))
            {
            	throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelCreditTransferINTRRequest", PretupsErrorCodesI.C2S_NOTIFICATION_MSISDN_BLANK);
            }
            if(!BTSLUtil.isValidMSISDN(notification_msisdn))
            {
            	throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelCreditTransferINTRRequest", PretupsErrorCodesI.ERROR_INVALID_MSISDN);
            }
            
            String defFrcXmlSelC2S = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEF_FRCXML_SEL_C2S);
            // if we have to forcibaly change the value of default selecter in
            // case of ussd request.
            if (!BTSLUtil.isNullString(defFrcXmlSelC2S)) {
                if (_log.isDebugEnabled()) {
                    _log.debug("parseChannelCreditTransferINTRRequest", "using default force selecter of XML");
                }
                selector = "" + defFrcXmlSelC2S;
            }
            
            parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_RECHARGE_INTR + chnl_message_sep + msisdn2 + chnl_message_sep + amount + chnl_message_sep + notification_msisdn + chnl_message_sep + selector + chnl_message_sep + language2 + chnl_message_sep + pin;
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setReqSelector(selector);
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
            p_requestVO.setNotificationMSISDN(notification_msisdn);

            
        } catch (BTSLBaseException be) {
            _log.error(methodName, " BTSL Exception while parsing Request Message :" + be.getMessage());
            throw be;
        }
        
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseChannelCreditTransferINTRRequest", "Exception e: " + e);
            throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelCreditTransferINTRRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseChannelCreditTransferINTRRequest", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * Added for Order Credit via USSD system.
     * 
     * @param p_requestVO
     * @param p_action
     * @throws Exception
     * @author kapil.mehta
     */

    public static void parseChannelOrderCredit(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("parseChannelOrderCredit", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        final String methodName = "parseChannelOrderCredit";
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<MSISDN>");
            final String msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
            index = requestStr.indexOf("<AMOUNT>");
            final String amount = requestStr.substring(index + "<AMOUNT>".length(), requestStr.indexOf("</AMOUNT>", index));
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));

            if (BTSLUtil.isNullString(amount)) {
                _log.error("parseChannelOrderCredit", "Amount field is null ");
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelOrderCredit", PretupsErrorCodesI.ORDER_LINE_ERROR_BLANK_AMOUNT);
            }

            parsedRequestStr = PretupsI.SERVICE_TYPE_EXT_CHNL_ORDER_CREDIT + chnl_message_sep + amount + chnl_message_sep + pin;
            // p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setRequestMSISDN(msisdn);
            p_requestVO.setMobileLineQty(amount);
            p_requestVO.setDecryptedMessage(parsedRequestStr);

        }

        catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseChannelOrderCredit", "Exception e: " + e);
            throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelOrderCredit", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseChannelOrderCredit", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * Added for Order Line via USSD system.
     * 
     * @param p_requestVO
     * @param p_action
     * @throws Exception
     * @author kapil.mehta
     */

    public static void parseChannelOrderLine(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("parseChannelOrderLine", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        final String methodName = "parseChannelOrderLine";
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<MSISDN>");
            final String msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
            index = requestStr.indexOf("<MOBILEQTY>");
            final String mobileqty = requestStr.substring(index + "<MOBILEQTY>".length(), requestStr.indexOf("</MOBILEQTY>", index));
            index = requestStr.indexOf("<FIXLINEQTY>");
            final String fixlineqty = requestStr.substring(index + "<FIXLINEQTY>".length(), requestStr.indexOf("</FIXLINEQTY>", index));
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));

            if (BTSLUtil.isNullString(mobileqty)) {
                _log.error("parseChannelOrderLine", "Mobile Quantity field is null ");
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelOrderLine", PretupsErrorCodesI.ORDER_LINE_ERROR_BLANK_AMOUNT);
            }
            if (BTSLUtil.isNullString(fixlineqty)) {
                _log.error("parseChannelOrderLine", "FixLine Quantity field is null ");
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelOrderLine", PretupsErrorCodesI.ORDER_LINE_ERROR_BLANK_AMOUNT);
            }

            parsedRequestStr = PretupsI.SERVICE_TYPE_EXT_CHNL_ORDER_LINE + chnl_message_sep + mobileqty + chnl_message_sep + fixlineqty + chnl_message_sep + pin;
            // p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setRequestMSISDN(msisdn);
            p_requestVO.setMobileLineQty(mobileqty);
            p_requestVO.setFixedLineQty(fixlineqty);
            p_requestVO.setDecryptedMessage(parsedRequestStr);

        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseChannelOrderLine", "Exception e: " + e);
            throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelOrderLine", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseChannelOrderLine", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * Added for Barring via USSD system.
     * 
     * @param p_requestVO
     * @param p_action
     * @throws Exception
     * @author kapil.mehta
     */

    public static void parseChannelBarring(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("parseChannelBarring", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        final String methodName = "parseChannelBarring";
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<MSISDN>");
            final String msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));

            parsedRequestStr = PretupsI.SERVICE_TYPE_EXT_CHNL_BARRED + chnl_message_sep + pin;
            // p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setRequestMSISDN(msisdn);
            p_requestVO.setDecryptedMessage(parsedRequestStr);

        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseChannelBarring", "Exception e: " + e);
            throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelBarring", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseChannelBarring", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * Added for IAT Roam recharge via USSD system.
     * 
     * @param p_requestVO
     * @param p_action
     * @throws Exception
     */

    public static void parseIATRoamRecharge(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("parseIATRoamRecharge", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        final String methodName = "parseIATRoamRecharge";
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<MSISDN1>");
            final String retailerMsisdn = requestStr.substring(index + "<MSISDN1>".length(), requestStr.indexOf("</MSISDN1>", index));
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            index = requestStr.indexOf("<MSISDN2>");
            final String receiverMsisdn = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));
            index = requestStr.indexOf("<AMOUNT>");
            final String amount = requestStr.substring(index + "<AMOUNT>".length(), requestStr.indexOf("</AMOUNT>", index));
            index = requestStr.indexOf("<LANGUAGE1>");
            final String retailerLanguage = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
            // RR MSIS amt lang pin
            parsedRequestStr = PretupsI.IAT_SERVICE_TYPE_ROAM_RECHARGE + chnl_message_sep + receiverMsisdn + chnl_message_sep + amount + chnl_message_sep + pin;
            p_requestVO.setRequestMSISDN(retailerMsisdn);
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(retailerLanguage));
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseIATRoamRecharge", "Exception e: " + e);
            throw new BTSLBaseException("USSDC2SXMLStringParser", "parseIATRoamRecharge", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseIATRoamRecharge", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * Added for parseIATInternationalRecharge.
     * 
     * @param p_requestVO
     * @param p_action
     * @throws Exception
     */

    public static void parseIATInternationalRecharge(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("parseIATInternationalRecharge", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        final String methodName = "parseIATInternationalRecharge";
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<MSISDN1>");
            final String retailerMsisdn = requestStr.substring(index + "<MSISDN1>".length(), requestStr.indexOf("</MSISDN1>", index));
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));

            index = requestStr.indexOf("<MSISDN2>");
            final String receiverMsisdn = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));
            index = requestStr.indexOf("<AMOUNT>");
            final String amount = requestStr.substring(index + "<AMOUNT>".length(), requestStr.indexOf("</AMOUNT>", index));

            index = requestStr.indexOf("<MSISDN3>");
            final String notifierMsisdn = requestStr.substring(index + "<MSISDN3>".length(), requestStr.indexOf("</MSISDN3>", index));
            index = requestStr.indexOf("<LANGUAGE1>");
            final String retailerLanguage = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));

            index = requestStr.indexOf("<LANGUAGE2>");
            final String notifierLanguage = requestStr.substring(index + "<LANGUAGE2>".length(), requestStr.indexOf("</LANGUAGE2>", index));

            if (BTSLUtil.isNullString(notifierMsisdn)) {
                parsedRequestStr = PretupsI.IAT_SERVICE_TYPE_INTERNATIONAL_RECHARGE + chnl_message_sep + receiverMsisdn + chnl_message_sep + amount + chnl_message_sep + pin;
            } else if (BTSLUtil.isNullString(notifierLanguage)) {
                parsedRequestStr = PretupsI.IAT_SERVICE_TYPE_INTERNATIONAL_RECHARGE + chnl_message_sep + receiverMsisdn + chnl_message_sep + amount + chnl_message_sep + notifierMsisdn + chnl_message_sep + pin;
            } else {
                parsedRequestStr = PretupsI.IAT_SERVICE_TYPE_INTERNATIONAL_RECHARGE + chnl_message_sep + receiverMsisdn + chnl_message_sep + amount + chnl_message_sep + notifierMsisdn + chnl_message_sep + notifierLanguage + chnl_message_sep + pin;
            }
            p_requestVO.setRequestMSISDN(retailerMsisdn);
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(retailerLanguage));
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseIATInternationalRecharge", "Exception e: " + e);
            throw new BTSLBaseException("USSDC2SXMLStringParser", "parseIATInternationalRecharge", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseIATInternationalRecharge", "Exiting p_requestVO: " + p_requestVO.toString());
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
        if (_log.isDebugEnabled()) {
            _log.debug("parseChannelLastXTransferStatus", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        final String methodName = "parseChannelLastXTransferStatus";
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            index = requestStr.indexOf("<MSISDN1>");
            final String msisdn1 = requestStr.substring(index + "<MSISDN1>".length(), requestStr.indexOf("</MSISDN1>", index));
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            parsedRequestStr = PretupsI.SERVICE_TYPE_LASTX_TRANSFER_REPORT + chnl_message_sep + pin;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseChannelLastXTransferStatus", "Exception e: " + e);
            throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelLastXTransferStatus", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseChannelLastXTransferStatus", "Exiting p_requestVO: " + p_requestVO.toString());
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
        if (_log.isDebugEnabled()) {
            _log.debug("parseChannelLastXTransferStatus", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        final String methodName = "parseChannelXEnquiryStatus";
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            final HashMap requestMap = new HashMap();
            int index = requestStr.indexOf("<TYPE>");
            index = requestStr.indexOf("<MSISDN1>");
            final String msisdn1 = requestStr.substring(index + "<MSISDN1>".length(), requestStr.indexOf("</MSISDN1>", index));
            index = requestStr.indexOf("<MSISDN2>");
            final String msisdn2 = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));
            // index=requestStr.indexOf("<DATE>");
            // String
            // Date=requestStr.substring(index+"<DATE>".length(),requestStr.indexOf("</DATE>",index));
            // java.util.Date txnDate = BTSLUtil.getDateFromDateString(Date);
            // requestMap.put("TXNDATE", txnDate);
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            parsedRequestStr = PretupsI.SERVICE_TYPE_CUSTX_ENQUIRY + chnl_message_sep + msisdn2 + chnl_message_sep + pin;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
            p_requestVO.setRequestMap(requestMap);

        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseChannelLastXTransferStatus", "Exception e: " + e);
            throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelLastXTransferStatus", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseChannelLastXTransferStatus", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * To parse the request for MVD download through USSD .
     * 
     * @param p_requestVO
     * @throws Exception
     * @author ashish Todia
     */
    public static void parseMVDDownloadRequest(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("parseMVDDownloadRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        final String methodName = "parseMVDDownloadRequest";
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            index = requestStr.indexOf("<MSISDN1>");
            final String msisdn1 = requestStr.substring(index + "<MSISDN1>".length(), requestStr.indexOf("</MSISDN1>", index));

            index = requestStr.indexOf("<MSISDN2>");
            final String msisdn2 = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));

            index = requestStr.indexOf("<AMOUNT>");
            String denominationAmount = requestStr.substring(index + "<AMOUNT>".length(), requestStr.indexOf("</AMOUNT>", index));
            if (BTSLUtil.isNullString(denominationAmount)) {
                denominationAmount = "0";
            }
            index = requestStr.indexOf("<QUANTITY>");
            String requestedQuantity = requestStr.substring(index + "<QUANTITY>".length(), requestStr.indexOf("</QUANTITY>", index));
            if (BTSLUtil.isNullString(requestedQuantity)) {
                requestedQuantity = "2";
            }
            index = requestStr.indexOf("<LANGUAGE1>");
            String language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
            if (BTSLUtil.isNullString(language1)) {
                language1 = Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
            }

            if (BTSLUtil.isNullString(language1)) {
                language1 = "0";
            }
            index = requestStr.indexOf("<LANGUAGE2>");
            String language2 = requestStr.substring(index + "<LANGUAGE2>".length(), requestStr.indexOf("</LANGUAGE2>", index));
            if (BTSLUtil.isNullString(language2)) {
                language2 = Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
            }

            if (BTSLUtil.isNullString(language2)) {
                language2 = "0";
            }

            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(language2));

            parsedRequestStr = PretupsI.MVD_VOUCHER_DOWNLOAD + chnl_message_sep + msisdn1 + chnl_message_sep + denominationAmount + chnl_message_sep + requestedQuantity + chnl_message_sep + pin;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);

        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseMVDDownloadRequest", "Exception e: " + e);
            throw new BTSLBaseException("USSDC2SXMLStringParser", "parseMVDDownloadRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseMVDDownloadRequest", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * @param p_requestVO
     * @throws Exception
     * @author rahul.dutt
     *         method is used to parse C2S transaction enquiry request
     */
    public static void parseC2STransferEnqRequest(RequestVO p_requestVO) throws Exception, BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("parseC2STransferEnqRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        final String methodName = "parseC2STransferEnqRequest";
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<MSISDN1>");
            final String msisdn1 = requestStr.substring(index + "<MSISDN1>".length(), requestStr.indexOf("</MSISDN1>", index));
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            index = requestStr.indexOf("<MSISDN2>");
            final String msisdn2 = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));
            index = requestStr.indexOf("<AMOUNT>");
            final String amount = requestStr.substring(index + "<AMOUNT>".length(), requestStr.indexOf("</AMOUNT>", index));
            index = requestStr.indexOf("<LANGUAGE1>");
            String language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
            if (BTSLUtil.isNullString(language1)) {
                language1 = Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
            }

            if (BTSLUtil.isNullString(language1)) {
                language1 = "0";
            }
            /*
             * index=requestStr.indexOf("<LANGUAGE2>");
             * String
             * language2=requestStr.substring(index+"<LANGUAGE2>".length()
             * ,requestStr.indexOf("</LANGUAGE2>",index));
             */
            index = requestStr.indexOf("<TRANSDATE>");
            String transDate = requestStr.substring(index + "<TRANSDATE>".length(), requestStr.indexOf("</TRANSDATE>", index));
            transDate = BTSLDateUtil.getGregorianDateInString(transDate);
            parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_TRANS_ENQ + chnl_message_sep + msisdn2 + chnl_message_sep + transDate + chnl_message_sep + amount + chnl_message_sep + pin;
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseC2STransferEnqRequest", "Exception e: " + e);
            throw new BTSLBaseException("USSDC2SXMLStringParser", "parseC2STransferEnqRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseC2STransferEnqRequest", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * Method to parse the user creation details in the request.
     * To parse the user creation request.
     * 
     * @param p_requestVO
     * @throws Exception
     */
    public static void parseUserCreationRequest(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("parseUserCreationRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        final String methodName = "parseUserCreationRequest";
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            final HashMap requestMap = new HashMap();
            int index = requestStr.indexOf("<TYPE>");
            index = requestStr.indexOf("<MSISDN1>");
            final String msisdn1 = requestStr.substring(index + "<MSISDN1>".length(), requestStr.indexOf("</MSISDN1>", index));
            index = requestStr.indexOf("<USERMSISDN>");
            final String userMsisdn = requestStr.substring(index + "<USERMSISDN>".length(), requestStr.indexOf("</USERMSISDN>", index));
            if (BTSLUtil.isNullString(userMsisdn)) {
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseUserCreationRequest", PretupsErrorCodesI.EXT_USRADD_INVALID_MSISDN);
            }
            index = requestStr.indexOf("<USERNAME>");// optional
            String userName = requestStr.substring(index + "<USERNAME>".length(), requestStr.indexOf("</USERNAME>", index));
            if (userName.contains(" ")) {
                if (_log.isDebugEnabled()) {
                    _log.debug("parseUserCreationRequest", "userName: " + userName);
                }
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseUserCreationRequest", PretupsErrorCodesI.EXT_USRADD_USERNAME_INCORRECT);
            }
            if (BTSLUtil.isNullString(userName)) {
                userName = userMsisdn;
            }
            requestMap.put("USERNAME", userName);
            index = requestStr.indexOf("<LOGINID>");// optional
            String loginId = requestStr.substring(index + "<LOGINID>".length(), requestStr.indexOf("</LOGINID>", index));
            if (loginId.contains(" ")) {
                if (_log.isDebugEnabled()) {
                    _log.debug("parseUserCreationRequest", "loginId: " + loginId);
                }
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseUserCreationRequest", PretupsErrorCodesI.EXT_USRADD_INVALID_LOGINID);
            }
            if (BTSLUtil.isNullString(loginId)) {
                loginId = userMsisdn;
            }
            requestMap.put("LOGINID", loginId);
            index = requestStr.indexOf("<CATCODE>");
            final String categoryCode = requestStr.substring(index + "<CATCODE>".length(), requestStr.indexOf("</CATCODE>", index));
            if (BTSLUtil.isNullString(categoryCode)) {
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseUserCreationRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            }
            index = requestStr.indexOf("<PARENTMSISDN>");
            final String parentMsisdn = requestStr.substring(index + "<PARENTMSISDN>".length(), requestStr.indexOf("</PARENTMSISDN>", index));
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            index = requestStr.indexOf("<EMPCODE>");// optional
            final String empCode = requestStr.substring(index + "<EMPCODE>".length(), requestStr.indexOf("</EMPCODE>", index));
            requestMap.put("EMPCODE", empCode);
            index = requestStr.indexOf("<SSN>");// optional
            final String ssn = requestStr.substring(index + "<SSN>".length(), requestStr.indexOf("</SSN>", index));
            requestMap.put("SSN", ssn);
            index = requestStr.indexOf("<EXTCODE>"); // optional
            final String extCode = requestStr.substring(index + "<EXTCODE>".length(), requestStr.indexOf("</EXTCODE>", index));
            boolean externalCodeMandatoryForUser = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_CODE_MANDATORY_FORUSER);
            if (externalCodeMandatoryForUser) {
                if (BTSLUtil.isNullString(extCode)) {
                    throw new BTSLBaseException("USSDC2SXMLStringParser", "parseUserCreationRequest", PretupsErrorCodesI.ERROR_ERP_CHNL_USER_EXTERNAL_CODE_IS_MANDATORY);
                }
            }
            requestMap.put("EXTCODE", extCode);

            // messageformat:-
            // ADDCHUSR_userMsisdn_username_loginid_categorycode_parentmsisdn
            // keyword usermsisdn username loginId categoryCode parentmsisdn
            parsedRequestStr = PretupsI.ADD_CHNL_USER + chnl_message_sep + userMsisdn + chnl_message_sep + userName + chnl_message_sep + loginId + chnl_message_sep + categoryCode + chnl_message_sep + parentMsisdn + chnl_message_sep + pin;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
            p_requestVO.setRequestMap(requestMap);
            p_requestVO.setExternalReferenceNum(extCode);
            p_requestVO.setEmployeeCode(empCode);
            p_requestVO.setSsn(ssn);

        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseChannelLastXTransferStatus", "Exception e: " + e);
            throw new BTSLBaseException("USSDC2SXMLStringParser", "parseUserCreationRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseUserCreationRequest", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * Added for parse CRBT Registration request.
     * 
     * @param p_requestVO
     * @throws BTSLBaseException
     * @throws Exception
     */
    public static void parseCRBTRegistrationRequest(RequestVO p_requestVO) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("parseCRBTRegistrationRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<MSISDN1>");
            final String msisdn1 = requestStr.substring(index + "<MSISDN1>".length(), requestStr.indexOf("</MSISDN1>", index));
            index = requestStr.indexOf("<PIN>");
            String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            index = requestStr.indexOf("<MSISDN2>");
            final String msisdn2 = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));
            index = requestStr.indexOf("<AMOUNT>");
            final String amount = requestStr.substring(index + "<AMOUNT>".length(), requestStr.indexOf("</AMOUNT>", index));
            index = requestStr.indexOf("<LANGUAGE1>");
            String language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
            index = requestStr.indexOf("<LANGUAGE2>");
            String language2 = requestStr.substring(index + "<LANGUAGE2>".length(), requestStr.indexOf("</LANGUAGE2>", index));
            // amount field should be mandatory
            if (BTSLUtil.isNullString(amount)) {
                // modification needed
                // p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_BLANK_AMOUNT);
                _log.error("parseCreditTransferRequest", "Amount field is null");
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseCRBTRegistrationRequest", PretupsErrorCodesI.P2P_ERROR_BLANK_AMOUNT);
            }
            // set default pin
            if (BTSLUtil.isNullString(pin)) {
            	String c2sDefaultSmsPin = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_SMSPIN);
                // pin=PretupsI.DEFAULT_P2P_PIN;
                pin = c2sDefaultSmsPin;
            }

            // to set default language code
            if (_log.isDebugEnabled()) {
                _log.debug("parseCRBTRegistrationRequest", "language1:" + language1 + ":");
            }

            if (BTSLUtil.isNullString(language1)) {
                language1 = Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
            }

            if (BTSLUtil.isNullString(language1)) {
                language1 = "1";
            }

            if (_log.isDebugEnabled()) {
                _log.debug("parseCRBTRegistrationRequest", "language1:" + language1 + ":");
                // to set default language code
            }

            if (_log.isDebugEnabled()) {
                _log.debug("parseCRBTRegistrationRequest", "language2:" + language2 + ":");
            }

            if (BTSLUtil.isNullString(language2)) {
                language2 = Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
            }

            if (BTSLUtil.isNullString(language2)) {
                language2 = "1";
            }
            if (_log.isDebugEnabled()) {
                _log.debug("parseCRBTRegistrationRequest", "language2:" + language2 + ":");
            }

            parsedRequestStr = PretupsI.SERVICE_TYPE_CRBTREGISTRATION + chnl_message_sep + msisdn2 + chnl_message_sep + amount + chnl_message_sep + language2 + chnl_message_sep + pin;
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(language2));
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
        } catch (BTSLBaseException be) {
            _log.error("parseCRBTRegistrationRequest", " BTSL Exception while parsing Request Message :" + be.getMessage());
            throw be;
        } catch (Exception e) {
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseCRBTRegistrationRequest", "Exception e: " + e);
            throw new BTSLBaseException("USSDC2SXMLStringParser", "parseCRBTRegistrationRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseCRBTRegistrationRequest", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * Added for parse CRBT Song Selection Service request
     * 
     * @param p_requestVO
     * @throws Exception
     */
    public static void parseCRBTSongSelectionRequest(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("parseCRBTSongSelectionRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        final String methodName = "parseCRBTSongSelectionRequest";
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<MSISDN1>");
            final String msisdn1 = requestStr.substring(index + "<MSISDN1>".length(), requestStr.indexOf("</MSISDN1>", index));
            index = requestStr.indexOf("<PIN>");
            String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            index = requestStr.indexOf("<MSISDN2>");
            final String msisdn2 = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));
            index = requestStr.indexOf("<AMOUNT>");
            final String amount = requestStr.substring(index + "<AMOUNT>".length(), requestStr.indexOf("</AMOUNT>", index));
            index = requestStr.indexOf("<LANGUAGE1>");
            String language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
            index = requestStr.indexOf("<LANGUAGE2>");
            String language2 = requestStr.substring(index + "<LANGUAGE2>".length(), requestStr.indexOf("</LANGUAGE2>", index));
            index = requestStr.indexOf("<SONGCODE>");
            final String songcode = requestStr.substring(index + "<SONGCODE>".length(), requestStr.indexOf("</SONGCODE>", index));
            // amount field should be mandatory
            if (BTSLUtil.isNullString(amount)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_BLANK_AMOUNT);
                _log.error("parseCRBTSongSelectionRequest", "Amount field is null");
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseCRBTSongSelectionRequest", PretupsErrorCodesI.C2S_ERROR_BLANK_AMOUNT);
            }
            // set default pin
            if (BTSLUtil.isNullString(pin)) {
            	String c2sDefaultSmsPin = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_SMSPIN);
                // pin=PretupsI.DEFAULT_P2P_PIN;
                pin = c2sDefaultSmsPin;
            }

            // to set default language code
            if (_log.isDebugEnabled()) {
                _log.debug("parseCRBTSongSelectionRequest", "language1:" + language1 + ":");
            }

            if (BTSLUtil.isNullString(language1)) {
                language1 = Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
            }

            if (BTSLUtil.isNullString(language1)) {
                language1 = "1";
            }

            if (_log.isDebugEnabled()) {
                _log.debug("parseCRBTSongSelectionRequest", "language1:" + language1 + ":");
                // to set default language code
            }

            if (_log.isDebugEnabled()) {
                _log.debug("parseCRBTSongSelectionRequest", "language2:" + language2 + ":");
            }

            if (BTSLUtil.isNullString(language2)) {
                language2 = Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
            }

            if (BTSLUtil.isNullString(language2)) {
                language2 = "1";
            }
            if (_log.isDebugEnabled()) {
                _log.debug("parseCRBTSongSelectionRequest", "language2:" + language2 + ":");
            }

            if (BTSLUtil.isNullString(songcode)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.CRBT_ERROR_BLANK_SONGCODE);
                _log.error("parseCRBTSongSelectionRequest", "Song Code field is null");
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseCRBTSongSelectionRequest", PretupsErrorCodesI.CRBT_ERROR_BLANK_SONGCODE);
            }

            parsedRequestStr = PretupsI.SERVICE_TYPE_CRBT_SONGSEL + chnl_message_sep + msisdn2 + chnl_message_sep + amount + chnl_message_sep + language2 + chnl_message_sep + pin + chnl_message_sep + songcode;
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseCRBTSongSelectionRequest", "Exception e: " + e);
            throw new BTSLBaseException("USSDC2SXMLStringParser", "parseCRBTSongSelectionRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseCRBTSongSelectionRequest", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method generates Channel EVR Request from XML String and formating
     * into white space seperated String
     * 
     * @param p_requestVO
     *            RequestVO
     * @param p_requestVO
     * @throws Exception
     * @author harpreet.kaur
     */
    public static void parseElecronicVoucherRechargeRequest(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("parseElecronicVoucherRechargeRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        final String methodName = "parseElecronicVoucherRechargeRequest";
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<MSISDN1>");
            final String msisdn1 = requestStr.substring(index + "<MSISDN1>".length(), requestStr.indexOf("</MSISDN1>", index));
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            index = requestStr.indexOf("<MSISDN2>");
            final String msisdn2 = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));
            index = requestStr.indexOf("<AMOUNT>");
            final String amount = requestStr.substring(index + "<AMOUNT>".length(), requestStr.indexOf("</AMOUNT>", index));
            index = requestStr.indexOf("<LANGUAGE1>");
            final String language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
            index = requestStr.indexOf("<LANGUAGE2>");
            final String language2 = requestStr.substring(index + "<LANGUAGE2>".length(), requestStr.indexOf("</LANGUAGE2>", index));
            index = requestStr.indexOf("<SELECTOR>");
            final String selector = requestStr.substring(index + "<SELECTOR>".length(), requestStr.indexOf("</SELECTOR>", index));
            parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_EVR + chnl_message_sep + msisdn2 + chnl_message_sep + amount + chnl_message_sep + language1 + chnl_message_sep + language2 + chnl_message_sep + selector + chnl_message_sep + pin;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseElecronicVoucherRechargeRequest", "Exception e: " + e);
            throw new BTSLBaseException("USSDC2SXMLStringParser", "parseElecronicVoucherRechargeRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseElecronicVoucherRechargeRequest", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * this method parse Channel User Suspend Resume request from XML String
     * formating into white space seperated String
     * 
     * @param p_requestVO
     *            RequestVO
     *            Added by Sanjeew.kumar
     */
    public static void parseChannelUserSuspendResume(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("parseChannelUserSuspendResume", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        final String methodName = "parseChannelUserSuspendResume";
        try {
            HashMap requestHashMap = p_requestVO.getRequestMap();
            if (requestHashMap == null) {
                requestHashMap = new HashMap();
            }
            final ArrayList arrList = new ArrayList();
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            requestHashMap.put("TYPE", type);

            index = requestStr.indexOf("<MSISDN1>");
            final String msisdn1 = requestStr.substring(index + "<MSISDN1>".length(), requestStr.indexOf("</MSISDN1>", index));
            requestHashMap.put("MSISDN1", msisdn1);

            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            requestHashMap.put("PIN", pin);

            index = requestStr.indexOf("<MSISDN2>");
            final String msisdn2 = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));
            requestHashMap.put("MSISDN", msisdn2);

            index = requestStr.indexOf("<ACTION>");
            final String action = requestStr.substring(index + "<ACTION>".length(), requestStr.indexOf("</ACTION>", index));
            requestHashMap.put("ACTION", action);

            index = requestStr.indexOf("<LANGUAGE1>");
            final String language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
            requestHashMap.put("LANGUAGE1", language1);

            // Channel User Suspend resume

            parsedRequestStr = PretupsI.SERVICE_TYPE_USR_SUSPEND_RESUME + chnl_message_sep + msisdn2 + chnl_message_sep + action;

            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));

            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
            p_requestVO.setType(PretupsI.USER_TYPE_CHANNEL);
            p_requestVO.setRequestMap(requestHashMap);
            p_requestVO.setReceiverMsisdn(msisdn2);

        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseChannelUserSuspendResume", "Exception e: " + e);
            throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelUserSuspendResume", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseChannelUserSuspendResume", "Exiting p_requestVO ID: " + p_requestVO.getRequestIDStr());
            }
        }
    }

    /**
     * @author rahul.d
     *         Added for parse C2S Last X Transfer report USSD
     * @param p_requestVO
     * @param p_action
     * @throws Exception
     *             korek
     */
    public static void parseC2SLastXTransferStatus(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("parseC2SLastXTransferStatus", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        final String methodName = "parseC2SLastXTransferStatus";
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            index = requestStr.indexOf("<MSISDN1>");
            final String msisdn1 = requestStr.substring(index + "<MSISDN1>".length(), requestStr.indexOf("</MSISDN1>", index));
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            parsedRequestStr = PretupsI.C2S_LASTX_TRANSFER_REPORT + chnl_message_sep + pin;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseC2SLastXTransferStatus", "Exception e: " + e);
            throw new BTSLBaseException("USSDC2SXMLStringParser", "parseC2SLastXTransferStatus", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseC2SLastXTransferStatus", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * @param p_requestVO
     * @throws Exception
     * @author rahul.dutt intigrated from tigo by hitesh.ghanghas
     *         This method is used to parse Vas recharge request incoming
     *         through USSDGW
     */
    public static void parseChannelVasTransferRequest(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("parseChannelVasTransferRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        // boolean tagsMandatory;
        String cellId = null;
        String switchId = null;
        String externalNetworkCode = null;
        final String methodName = "parseChannelVasTransferRequest";
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<MSISDN1>");
            final String msisdn1 = requestStr.substring(index + "<MSISDN1>".length(), requestStr.indexOf("</MSISDN1>", index));
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            index = requestStr.indexOf("<MSISDN2>");
            final String msisdn2 = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));
            if (BTSLUtil.isNullString(msisdn2)) {
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelExtVASTransferRequest", PretupsErrorCodesI.CHNL_ERROR_RECR_MSISDN_BLANK);
            }
            index = requestStr.indexOf("<AMOUNT>");
            final String amount = requestStr.substring(index + "<AMOUNT>".length(), requestStr.indexOf("</AMOUNT>", index));
            index = requestStr.indexOf("<LANGUAGE1>");
            final String language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
            index = requestStr.indexOf("<LANGUAGE2>");
            final String language2 = requestStr.substring(index + "<LANGUAGE2>".length(), requestStr.indexOf("</LANGUAGE2>", index));
            index = requestStr.indexOf("<SELECTOR>");
            final String selector = requestStr.substring(index + "<SELECTOR>".length(), requestStr.indexOf("</SELECTOR>", index));
            index = requestStr.indexOf("<DATE>");
            String date = requestStr.substring(index + "<DATE>".length(), requestStr.indexOf("</DATE>", index));
            date = BTSLDateUtil.getGregorianDateInString(date);
            if (!BTSLUtil.isNullString(date) && !BTSLUtil.isValidDatePattern(date)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_DATE);
                String systemDateFormat = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT);
                final String args[] = { systemDateFormat };
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelVasTransferRequest", PretupsErrorCodesI.INVALID_DATE, args);
            }
            index = requestStr.indexOf("<CELLID>");
            if (index >= 0) {
                cellId = requestStr.substring(index + "<CELLID>".length(), requestStr.indexOf("</CELLID>", index));
            }
            index = requestStr.indexOf("<SWITCHID>");
            if (index >= 0) {
                switchId = requestStr.substring(index + "<SWITCHID>".length(), requestStr.indexOf("</SWITCHID>", index));
            }
            // selector is mandatory
            if (BTSLUtil.isNullString(selector)) {
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelVasTransferRequest", PretupsErrorCodesI.VAS_PROMOVAS_REQ_SELECTOR_MISSING);
            }
            /** new added to test c2s using extgw **/
            if (!BTSLUtil.isNullString(p_requestVO.getMessageGatewayVO().getGatewayCode()) && "EXTGW".equals((p_requestVO.getMessageGatewayVO().getGatewayCode()))) {
                index = requestStr.indexOf("<EXTNWCODE>");
                externalNetworkCode = requestStr.substring(index + "<EXTNWCODE>".length(), requestStr.indexOf("</EXTNWCODE>", index));
            }
            boolean ussdNewTagsMandatory = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY);
            // tagsMandatory=ussdNewTagsMandatory;
            if (ussdNewTagsMandatory) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelVasTransferRequest", PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelVasTransferRequest", PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
                    }
                } catch (BTSLBaseException btsle) {
                    throw btsle;
                }
            }
            boolean multiAmountEnabled = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTI_AMOUNT_ENABLED);
            if (!BTSLUtil.isNullString(amount) && multiAmountEnabled) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_VAS_RECHARGE + chnl_message_sep + msisdn2 + chnl_message_sep + amount + chnl_message_sep + selector + chnl_message_sep + language2 + chnl_message_sep + pin;
            } else if (!multiAmountEnabled) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_VAS_RECHARGE + chnl_message_sep + msisdn2 + chnl_message_sep + selector + chnl_message_sep + language2 + chnl_message_sep + pin;
            } else {
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelVasTransferRequest", PretupsErrorCodesI.AMOUNT_REQUIRED);
            }
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setReqSelector(selector);
            p_requestVO.setRequestMSISDN(msisdn1);
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setCellId(cellId);
            p_requestVO.setSwitchId(switchId);
            p_requestVO.setExternalNetworkCode(externalNetworkCode);
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseChannelVasTransferRequest", "Exception e: " + e);
            throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelVasTransferRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseChannelVasTransferRequest", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * @param p_requestVO
     * @throws Exception
     * @author rahul.dutt
     *         This method parses the Promo Vas transfer request
     */
    public static void parseChannelPrVasTransferRequest(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("parseChannelPrVasTransferRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        // boolean tagsMandatory;
        String cellId = null;
        String switchId = null;
        String externalNetworkCode = null;
        final String methodName = "parseChannelPrVasTransferRequest";
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<MSISDN1>");
            final String msisdn1 = requestStr.substring(index + "<MSISDN1>".length(), requestStr.indexOf("</MSISDN1>", index));
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            index = requestStr.indexOf("<MSISDN2>");
            final String msisdn2 = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));
            if (BTSLUtil.isNullString(msisdn2)) {
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelExtVASTransferRequest", PretupsErrorCodesI.CHNL_ERROR_RECR_MSISDN_BLANK);
            }
            index = requestStr.indexOf("<AMOUNT>");
            final String amount = requestStr.substring(index + "<AMOUNT>".length(), requestStr.indexOf("</AMOUNT>", index));
            index = requestStr.indexOf("<LANGUAGE1>");
            final String language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
            index = requestStr.indexOf("<LANGUAGE2>");
            final String language2 = requestStr.substring(index + "<LANGUAGE2>".length(), requestStr.indexOf("</LANGUAGE2>", index));
            index = requestStr.indexOf("<DATE>");
            String date = requestStr.substring(index + "<DATE>".length(), requestStr.indexOf("</DATE>", index));
            date = BTSLDateUtil.getGregorianDateInString(date);
            if (!BTSLUtil.isNullString(date) && !BTSLUtil.isValidDatePattern(date)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_DATE);
                String systemDateFormat = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT);
                final String args[] = { systemDateFormat };
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelPrVasTransferRequest", PretupsErrorCodesI.INVALID_DATE, args);
            }
            index = requestStr.indexOf("<SELECTOR>");
            final String selector = requestStr.substring(index + "<SELECTOR>".length(), requestStr.indexOf("</SELECTOR>", index));
            index = requestStr.indexOf("<CELLID>");
            if (index >= 0) {
                cellId = requestStr.substring(index + "<CELLID>".length(), requestStr.indexOf("</CELLID>", index));
            }
            index = requestStr.indexOf("<SWITCHID>");
            if (index >= 0) {
                switchId = requestStr.substring(index + "<SWITCHID>".length(), requestStr.indexOf("</SWITCHID>", index));
            }

            if (BTSLUtil.isNullString(selector)) {
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelPrVasTransferRequest", PretupsErrorCodesI.VAS_PROMOVAS_REQ_SELECTOR_MISSING);
            }
            // if we have to forcibaly change the value of default selecter in
            // case of ussd request.
            /** new added to test c2s using extgw **/
            if (!BTSLUtil.isNullString(p_requestVO.getMessageGatewayVO().getGatewayCode()) && (p_requestVO.getMessageGatewayVO().getGatewayCode().equals("EXTGW"))) {
                index = requestStr.indexOf("<EXTNWCODE>");
                externalNetworkCode = requestStr.substring(index + "<EXTNWCODE>".length(), requestStr.indexOf("</EXTNWCODE>", index));
            }
            boolean ussdNewTagsMandatory = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY);
            // tagsMandatory=ussdNewTagsMandatory;
            if (ussdNewTagsMandatory) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelPrVasTransferRequest", PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelPrVasTransferRequest", PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
                    }
                } catch (BTSLBaseException btsle) {
                    throw btsle;
                }
            }
            boolean multiAmountEnabled = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTI_AMOUNT_ENABLED);
            if (!BTSLUtil.isNullString(amount) && multiAmountEnabled) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_PVAS_RECHARGE + chnl_message_sep + msisdn2 + chnl_message_sep + amount + chnl_message_sep + selector + chnl_message_sep + language2 + chnl_message_sep + pin;
            } else if (!multiAmountEnabled) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_PVAS_RECHARGE + chnl_message_sep + msisdn2 + chnl_message_sep + selector + chnl_message_sep + language2 + chnl_message_sep + pin;
            } else {
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelPrVasTransferRequest", PretupsErrorCodesI.AMOUNT_REQUIRED);
            }
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setReqSelector(selector);
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
            p_requestVO.setCellId(cellId);
            p_requestVO.setSwitchId(switchId);
            p_requestVO.setExternalNetworkCode(externalNetworkCode);
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseChannelPrVasTransferRequest", "Exception e: " + e);
            throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelPrVasTransferRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseChannelPrVasTransferRequest", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * @param p_requestVO
     * @throws Exception
     * @author
     *         Method parseChannelUserAuthRequest() parse the request of
     *         User Authentication XML API used for USSD System
     */
    public static void parseChannelUserAuthRequest(RequestVO p_requestVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("parseChannelUserAuthRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        final String methodName = "parseChannelUserAuthRequest";
        try {
            final HashMap requestMap = new HashMap();
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            requestMap.put("TYPE", type);

            index = requestStr.indexOf("<EXTNWCODE>");
            final String chnnUserExtCode = requestStr.substring(index + "<EXTNWCODE>".length(), requestStr.indexOf("</EXTNWCODE>", index));
            requestMap.put("EXTNWCODE", chnnUserExtCode);
            if (BTSLUtil.isNullString(chnnUserExtCode)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.EXT_USRADD_ERROR_MISSING_MANDATORY_FIELDS);
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseExtUserRequest", PretupsErrorCodesI.EXT_USRADD_ERROR_MISSING_MANDATORY_FIELDS);
            }
            index = requestStr.indexOf("<DATE>");
            String date = requestStr.substring(index + "<DATE>".length(), requestStr.indexOf("</DATE>", index));
            date = BTSLDateUtil.getGregorianDateInString(date);

            if (!BTSLUtil.isValidDatePattern(date)) {
                _log.error("parseChannelUserAuthRequest", "in valid date  message format  ");
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelUserAuthRequest", PretupsErrorCodesI.INVALID_DATE);
            }
            requestMap.put("DATE", date);

            index = requestStr.indexOf("<EXTREFNUM>");
            final String extRefNum = requestStr.substring(index + "<EXTREFNUM>".length(), requestStr.indexOf("</EXTREFNUM>", index));
            requestMap.put("EXTREFNUM", extRefNum);

            index = requestStr.indexOf("<USERLOGINID>");
            final String userLoginID = requestStr.substring(index + "<USERLOGINID>".length(), requestStr.indexOf("</USERLOGINID>", index));
            requestMap.put("USERLOGINID", userLoginID);

            index = requestStr.indexOf("<USERPASSWORD>");
            final String userPassword = requestStr.substring(index + "<USERPASSWORD>".length(), requestStr.indexOf("</USERPASSWORD>", index));
            requestMap.put("USERPASSWORD", userPassword);

            index = requestStr.indexOf("<MSISDN1>");
            final String msisdn = requestStr.substring(index + "<MSISDN1>".length(), requestStr.indexOf("</MSISDN1>", index));
            requestMap.put("MSISDN", msisdn);

            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            requestMap.put("PIN", pin);
            if ((BTSLUtil.isNullString(userLoginID) && BTSLUtil.isNullString(userPassword)) && (BTSLUtil.isNullString(msisdn) && BTSLUtil.isNullString(pin))) {
                _log.error("parseChannelUserAuthRequest", "in valid message format  ");
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelUserAuthRequest", PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT);
            }
            parsedRequestStr = PretupsI.SERVICE_TYPE_USER_AUTH;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMap(requestMap);
            p_requestVO.setRequestMSISDN(msisdn);
            p_requestVO.setSenderLoginID(userLoginID);
            p_requestVO.setExternalReferenceNum(extRefNum);
        } catch (BTSLBaseException be) {
            _log.error("parseExtUserRequest", "BTSLBaseException e: " + be);
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);

            _log.error("parseChannelUserAuthRequest", "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "USSDC2SXMLStringParser[parseChannelUserAuthRequest]",
                PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT, "", "", "parseChannelUserAuthRequest:" + e.getMessage());
            throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelUserAuthRequest", PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseChannelUserAuthRequest", "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * 
     * @param p_requestVO
     * @throws Exception
     */
    public static void parseSIMActivationRequest(RequestVO p_requestVO) throws Exception {

        if (_log.isDebugEnabled()) {
            _log.debug("parseSIMActivationRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        String cellId = null;
        String switchId = null;
        String amount = null;
        final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DDMMYYYYHHMMSS);
        final String methodName = "parseSIMActivationRequest";
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<MSISDN1>");
            final String msisdn1 = requestStr.substring(index + "<MSISDN1>".length(), requestStr.indexOf("</MSISDN1>", index));
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            index = requestStr.indexOf("<DATE>");
            String date = requestStr.substring(index + "<DATE>".length(), requestStr.indexOf("</DATE>", index));
            date = BTSLDateUtil.getGregorianDateInString(date);

            index = requestStr.indexOf("<AGENTID>");
            final String agentid = requestStr.substring(index + "<AGENTID>".length(), requestStr.indexOf("</AGENTID>", index));
            index = requestStr.indexOf("<AGENTCODE>");
            final String agentcode = requestStr.substring(index + "<AGENTCODE>".length(), requestStr.indexOf("</AGENTCODE>", index));
            index = requestStr.indexOf("<ICCID>");
            final String iccid = requestStr.substring(index + "<ICCID>".length(), requestStr.indexOf("</ICCID>", index));
            index = requestStr.indexOf("<SIMPIN>");
            final String simpin = requestStr.substring(index + "<SIMPIN>".length(), requestStr.indexOf("</SIMPIN>", index));
            boolean debitSenderSimAct = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEBIT_SENDER_SIMACT))).booleanValue();
            if (debitSenderSimAct) {
                index = requestStr.indexOf("<AMOUNT>");
                amount = requestStr.substring(index + "<AMOUNT>".length(), requestStr.indexOf("</AMOUNT>", index));

                if (BTSLUtil.isNullString(amount)) {
                    amount = "0";
                }
            }

            index = requestStr.indexOf("<SUBCRIBERID>");
            final String subscriberid = requestStr.substring(index + "<SUBCRIBERID>".length(), requestStr.indexOf("</SUBCRIBERID>", index));
            index = requestStr.indexOf("<SUBSCRIBERDOB>");
            String subdob = null;
            if (index > 0) {
                subdob = requestStr.substring(index + "<SUBSCRIBERDOB>".length(), requestStr.indexOf("</SUBSCRIBERDOB>", index));
                if (!BTSLUtil.isValidDatePattern(subdob)) {
                    p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_DATE);
                    String systemDateFormat = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT);
                    final String args[] = { systemDateFormat };
                    throw new BTSLBaseException("USSDC2SXMLStringParser", "parseExtGeographyRequest", PretupsErrorCodesI.INVALID_DATE, args);
                }
            }

            index = requestStr.indexOf("<DOCTYPE>");
            final String doctype = requestStr.substring(index + "<DOCTYPE>".length(), requestStr.indexOf("</DOCTYPE>", index));
            index = requestStr.indexOf("<LANGUAGE1>");
            String language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
            index = requestStr.indexOf("<LANGUAGE2>");
            String language2 = requestStr.substring(index + "<LANGUAGE2>".length(), requestStr.indexOf("</LANGUAGE2>", index));
            String selector = null;
            index = requestStr.indexOf("<SELECTOR>");
            if (index >= 0) {
                selector = requestStr.substring(index + "<SELECTOR>".length(), requestStr.indexOf("</SELECTOR>", index));
            }

            index = requestStr.indexOf("<CELLID>");
            cellId = requestStr.substring(index + "<CELLID>".length(), requestStr.indexOf("</CELLID>", index));
            index = requestStr.indexOf("<SWITCHID>");
            switchId = requestStr.substring(index + "<SWITCHID>".length(), requestStr.indexOf("</SWITCHID>", index));

            try {

                sdf.setLenient(false); // this is required else it will convert
                sdf.parse(date);

            } catch (java.text.ParseException e1) {
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseSIMActivationRequest", PretupsErrorCodesI.ERROR_EXT_DATE_NOT_PROPER);
            }

            final String language = Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
            if (BTSLUtil.isNullString(language1)) {
                language1 = language;
            }

            if (BTSLUtil.isNullString(language1)) {
                language1 = "0";
            }

            if (_log.isDebugEnabled()) {
                _log.debug("parseSIMActivationRequest", "language1:" + language1 + ":");
                // added by sanjay 10/01/2006 - to set default language code
            }

            if (_log.isDebugEnabled()) {
                _log.debug("parseSIMActivationRequest", "language2:" + language2 + ":");
            }

            if (BTSLUtil.isNullString(language2)) {
                language2 = Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
            }

            if (BTSLUtil.isNullString(language2)) {
                language2 = "0";
            }
            if (_log.isDebugEnabled()) {
                _log.debug("parseSIMActivationRequest", "language2:" + language2 + ":");
            }
            String simActDefaultSelector = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SIMACT_DEFAULT_SELECTOR);
            if (BTSLUtil.isNullString(selector)) {
                selector = simActDefaultSelector;
            }

            if (BTSLUtil.isNullString(simpin)) {
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseSIMActivationRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            }

            if (BTSLUtil.isNullString(iccid)) {
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseSIMActivationRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            }

            if (BTSLUtil.isNullString(agentid)) {
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseSIMActivationRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            }
            if (BTSLUtil.isNullString(agentcode)) {
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseSIMActivationRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            }
            if (BTSLUtil.isNullString(subscriberid)) {
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseSIMActivationRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            }
            if (BTSLUtil.isNullString(subdob)) {
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseSIMActivationRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            }
            if (BTSLUtil.isNullString(doctype)) {
                throw new BTSLBaseException("USSDC2SXMLStringParser", "parseSIMActivationRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            }
            boolean ussdNewTagsMandatory = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY);
            if (ussdNewTagsMandatory) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        throw new BTSLBaseException("USSDC2SXMLStringParser", "parseSIMActivationRequest", PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        throw new BTSLBaseException("USSDC2SXMLStringParser", "parseSIMActivationRequest", PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
                    }

                    if (!language1.equals(language)) {

                        throw new BTSLBaseException("USSDC2SXMLStringParser", "parseSIMActivationRequest", PretupsErrorCodesI.USSD_LANGUAGE1_BLANK_ERROR);
                    }
                    if (!language2.equals(language)) {
                        throw new BTSLBaseException("USSDC2SXMLStringParser", "parseSIMActivationRequest", PretupsErrorCodesI.USSD_LANGUAGE2_BLANK_ERROR);
                    }

                } catch (BTSLBaseException btsle) {
                    throw btsle;
                }
                if (debitSenderSimAct) {
                    parsedRequestStr = PretupsI.SERVICE_TYPE_SIM_ACT_REQ + chnl_message_sep + subscriberid + chnl_message_sep + amount + chnl_message_sep + iccid + chnl_message_sep + simpin + chnl_message_sep + subdob + chnl_message_sep + doctype + chnl_message_sep + agentid + chnl_message_sep + agentcode + chnl_message_sep + cellId + chnl_message_sep + switchId + chnl_message_sep + pin;

                } else {
                    parsedRequestStr = PretupsI.SERVICE_TYPE_SIM_ACT_REQ + chnl_message_sep + subscriberid + chnl_message_sep + iccid + chnl_message_sep + simpin + chnl_message_sep + subdob + chnl_message_sep + doctype + chnl_message_sep + agentid + chnl_message_sep + agentcode + chnl_message_sep + cellId + chnl_message_sep + switchId + chnl_message_sep + pin;
                }

            } else {
                if (debitSenderSimAct) {
                    parsedRequestStr = PretupsI.SERVICE_TYPE_SIM_ACT_REQ + chnl_message_sep + subscriberid + chnl_message_sep + amount + chnl_message_sep + iccid + chnl_message_sep + simpin + chnl_message_sep + subdob + chnl_message_sep + doctype + chnl_message_sep + agentid + chnl_message_sep + agentcode + chnl_message_sep + pin;

                } else {
                    parsedRequestStr = PretupsI.SERVICE_TYPE_SIM_ACT_REQ + chnl_message_sep + subscriberid + chnl_message_sep + iccid + chnl_message_sep + simpin + chnl_message_sep + subdob + chnl_message_sep + doctype + chnl_message_sep + agentid + chnl_message_sep + agentcode + chnl_message_sep + pin;
                }
            }
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(language2));
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setReqSelector(selector);
            if (debitSenderSimAct) {
                p_requestVO.setReqAmount(amount);
            }
            p_requestVO.setRequestMSISDN(msisdn1);
            p_requestVO.setCellId(cellId);
            p_requestVO.setSwitchId(switchId);
        } catch (BTSLBaseException e) {
            p_requestVO.setMessageCode(e.getMessageKey());
            _log.error("parseSIMActivationRequest", "Exception e: " + e.getMessageKey());
            throw e;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseSIMActivationRequest", "Exception e: " + e);
            throw new BTSLBaseException("USSDC2SXMLStringParser", "parseSIMActivationRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseSIMActivationRequest", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }

    }

    /**
     * This method generates Last Transfer Status Report Response in XML format
     * from requestVO
     * 
     * @param p_requestVO
     * @throws Exception
     * @author arvinder.singh
     * @Date 10/09/2013
     */
    public static void parseChannelHelpDeskRequest(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("parseChannelHelpDeskRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        boolean tagsMandatory;
        String cellId = null;
        String switchId = null;
        final String extCode = null;
        final String externalNetworkCode = null;
        final String methodName = "parseChannelHelpDeskRequest";
        try {
            final HashMap requestHashMap = new HashMap();
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<MSISDN1>");
            final String msisdn1 = requestStr.substring(index + "<MSISDN1>".length(), requestStr.indexOf("</MSISDN1>", index));
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            index = requestStr.indexOf("<LANGUAGE1>");
            final String language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
            index = requestStr.indexOf("<CELLID>");
            if (index >= 0) {
                cellId = requestStr.substring(index + "<CELLID>".length(), requestStr.indexOf("</CELLID>", index));
            }
            index = requestStr.indexOf("<SWITCHID>");
            if (index >= 0) {
                switchId = requestStr.substring(index + "<SWITCHID>".length(), requestStr.indexOf("</SWITCHID>", index));
            }
            boolean ussdNewTagsMandatory = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY);
            tagsMandatory = ussdNewTagsMandatory;
            if (tagsMandatory) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelHelpDeskRequest", PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelHelpDeskRequest", PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
                    }
                } catch (BTSLBaseException btsle) {
                    throw btsle;
                }
            }
            parsedRequestStr = PretupsI.SERVICE_TYPE_HLPDSK_REQUEST + chnl_message_sep + msisdn1 + chnl_message_sep + pin;
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
            p_requestVO.setCellId(cellId);
            p_requestVO.setSwitchId(switchId);
            p_requestVO.setExternalNetworkCode(externalNetworkCode);
            p_requestVO.setSenderExternalCode(extCode);
            p_requestVO.setRequestMap(requestHashMap);

        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseChannelHelpDeskRequest", "Exception e: " + e);
            throw new BTSLBaseException("USSDC2SXMLStringParser", "parseChannelHelpDeskRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseChannelHelpDeskRequest", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    // Added By Brajesh For Loyalts Points Enquiry Through External Gateway
    // this method will recieve the message from in XML format from USSD gateway
    // and Parse that message and extract the msisd, pin, cellid,switchid
    /*
     * public static void parseLMSPointsEnquiryRequest(RequestVO p_requestVO)
     * throws Exception
     * {
     * if(_log.isDebugEnabled()) {
     * _log.debug("parseLMSPointsEnquiryRequest","Entered p_requestVO: "+p_requestVO
     * .toString());
     * }
     * String parsedRequestStr=null;
     * //boolean tagsMandatory;
     * String cellId=null;
     * String switchId=null;
     * try
     * {
     * String requestStr=p_requestVO.getRequestMessage();
     * int index=requestStr.indexOf("<TYPE>");
     * index=requestStr.indexOf("<MSISDN>");
     * String
     * msisdn=requestStr.substring(index+"<MSISDN>".length(),requestStr.indexOf
     * ("</MSISDN>",index));
     * 
     * index=requestStr.indexOf("<PIN>");
     * String
     * pin=requestStr.substring(index+"<PIN>".length(),requestStr.indexOf(
     * "</PIN>",index));
     * index=requestStr.indexOf("<CELLID>");
     * if(index>=0) {
     * cellId=requestStr.substring(index+"<CELLID>".length(),requestStr.indexOf(
     * "</CELLID>",index));
     * }
     * index=requestStr.indexOf("<SWITCHID>");
     * if(index>=0) {
     * switchId=requestStr.substring(index+"<SWITCHID>".length(),requestStr.indexOf
     * ("</SWITCHID>",index));
     * }
     * boolean ussdNewTagsMandatory = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY);
     * //tagsMandatory=ussdNewTagsMandatory;
     * if(ussdNewTagsMandatory)
     * {
     * try{
     * if(BTSLUtil.isNullString(cellId))
     * {
     * throw new
     * BTSLBaseException("USSDC2SXMLStringParser","parseLMSPointsEnquiryRequest"
     * ,PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
     * }
     * if(BTSLUtil.isNullString(switchId))
     * {
     * throw new
     * BTSLBaseException("USSDC2SXMLStringParser","parseLMSPointsEnquiryRequest"
     * ,PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
     * }
     * }catch(BTSLBaseException btsle){
     * throw btsle;
     * }
     * }
     * parsedRequestStr=PretupsI.SERVICE_TYPE_LMS_POINTS_ENQUIRY+CHNL_MESSAGE_SEP
     * +pin;
     * p_requestVO.setDecryptedMessage(parsedRequestStr);
     * p_requestVO.setRequestMSISDN(msisdn);
     * p_requestVO.setCellId(cellId);
     * p_requestVO.setSwitchId(switchId);
     * 
     * }
     * catch(Exception e)
     * {
     * e.printStackTrace();
     * p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT
     * );
     * _log.error("parseChannelBalanceEnquiry","Exception e: "+e);
     * throw new
     * BTSLBaseException("USSDC2SXMLStringParser","parseLMSPointsEnquiryRequest"
     * ,PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
     * }
     * finally
     * {
     * if(_log.isDebugEnabled()) {
     * _log.debug("parseLMSPointsEnquiryRequest","Exiting p_requestVO: "+p_requestVO
     * .toString());
     * }
     * }
     * }
     * //Added By Brajesh For Loyalts Points Redemption Through External Gateway
     * //this method will recieve the message from in XML format from USSD
     * gateway and Parse that message and extract the msisd, pin,
     * cellid,switchid
     * public static void parseLMSPointsRedemptionRequest(RequestVO p_requestVO)
     * throws Exception
     * {
     * if(_log.isDebugEnabled()) {
     * _log.debug("parseLMSPointsRedemptionRequest","Entered p_requestVO: "+
     * p_requestVO.toString());
     * }
     * String parsedRequestStr=null;
     * LoyaltyPointsRedemptionVO lpRedemptionVO =new
     * LoyaltyPointsRedemptionVO();
     * //boolean tagsMandatory;
     * String cellId=null;
     * String switchId=null;
     * try
     * {
     * String requestStr=p_requestVO.getRequestMessage();
     * int index=requestStr.indexOf("<TYPE>");
     * index=requestStr.indexOf("<MSISDN>");
     * String
     * msisdn=requestStr.substring(index+"<MSISDN>".length(),requestStr.indexOf
     * ("</MSISDN>",index));
     * 
     * index=requestStr.indexOf("<PIN>");
     * String
     * pin=requestStr.substring(index+"<PIN>".length(),requestStr.indexOf(
     * "</PIN>",index));
     * index=requestStr.indexOf("<POINTS>");
     * String
     * points=requestStr.substring(index+"<POINTS>".length(),requestStr.indexOf
     * ("</POINTS>",index));
     * index=requestStr.indexOf("<CELLID>");
     * if(index>=0) {
     * cellId=requestStr.substring(index+"<CELLID>".length(),requestStr.indexOf(
     * "</CELLID>",index));
     * }
     * index=requestStr.indexOf("<SWITCHID>");
     * if(index>=0) {
     * switchId=requestStr.substring(index+"<SWITCHID>".length(),requestStr.indexOf
     * ("</SWITCHID>",index));
     * }
     * boolean ussdNewTagsMandatory = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY);
     * //tagsMandatory=ussdNewTagsMandatory;
     * if(ussdNewTagsMandatory)
     * {
     * try{
     * if(BTSLUtil.isNullString(cellId))
     * {
     * throw new
     * BTSLBaseException("USSDC2SXMLStringParser","parseLMSPointsEnquiryRequest"
     * ,PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
     * }
     * if(BTSLUtil.isNullString(switchId))
     * {
     * throw new
     * BTSLBaseException("USSDC2SXMLStringParser","parseLMSPointsEnquiryRequest"
     * ,PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
     * }
     * }catch(BTSLBaseException btsle){
     * throw btsle;
     * }
     * }
     * parsedRequestStr=PretupsI.SERVICE_TYPE_LMS_POINTS_REDEMPTION+CHNL_MESSAGE_SEP
     * +points+CHNL_MESSAGE_SEP+pin;
     * p_requestVO.setDecryptedMessage(parsedRequestStr);
     * p_requestVO.setRequestMSISDN(msisdn);
     * p_requestVO.setCellId(cellId);
     * p_requestVO.setSwitchId(switchId);
     * lpRedemptionVO.setRedempLoyaltyPointString(points);
     * 
     * }
     * catch(Exception e)
     * {
     * e.printStackTrace();
     * p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT
     * );
     * _log.error("parseChannelBalanceEnquiry","Exception e: "+e);
     * throw new
     * BTSLBaseException("USSDC2SXMLStringParser","parseLMSPointsRedemptionRequest"
     * ,PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
     * }
     * finally
     * {
     * if(_log.isDebugEnabled()) {
     * _log.debug("parseLMSPointsRedemptionRequest","Exiting p_requestVO: "+
     * p_requestVO.toString());
     * }
     * }
     * }
     */

    /**
     * @param p_requestVO
     * @throws Exception
     *             to parse voucher consumption request
     */
    public static void parseVoucherRetReq(RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "parseVoucherRetReq";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<SUBID>");
            final String SUBID = requestStr.substring(index + "<SUBID>".length(), requestStr.indexOf("</SUBID>", index));
            index = requestStr.indexOf("<MRP>");
            final String mrp = requestStr.substring(index + "<MRP>".length(), requestStr.indexOf("</MRP>", index));
            index = requestStr.indexOf("<SERVICE>");
            final String service = requestStr.substring(index + "<SERVICE>".length(), requestStr.indexOf("</SERVICE>", index));
            index = requestStr.indexOf("<SELECTOR>");
            final String SELECTOR = requestStr.substring(index + "<SELECTOR>".length(), requestStr.indexOf("</SELECTOR>", index));
            index = requestStr.indexOf("<TXNID>");
            final String TXNID = requestStr.substring(index + "<TXNID>".length(), requestStr.indexOf("</TXNID>", index));

            parsedRequestStr = VOMSI.SERVICE_TYPE_VOUCHER_REC + chnl_message_sep + SUBID + chnl_message_sep + mrp + chnl_message_sep + service + chnl_message_sep + SELECTOR + chnl_message_sep + TXNID;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(SUBID);
            p_requestVO.setVoucherType(type);
            p_requestVO.setRequestMessageArray(PretupsBL.parsePlainMessage(parsedRequestStr));

        } catch (Exception e) {
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
     * @param p_requestVO
     * @throws Exception
     *             to parse voucher rollback request
     */
    public static void parseVoucherRetrievalRollBackReq(RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "parseVoucherRetrievalRollBackReq";
        final String className = "ExtAPIStringParser";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        String voucherType = null;
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<SUBID>");
            String SUBID = requestStr.substring(index + "<SUBID>".length(), requestStr.indexOf("</SUBID>", index));
            index = requestStr.indexOf("<TXNID>");
            String txnId = requestStr.substring(index + "<TXNID>".length(), requestStr.indexOf("</TXNID>", index));
            index = requestStr.indexOf("<VTYPE>");
            if (index > 0) {
                voucherType = requestStr.substring(index + "<VTYPE>".length(), requestStr.indexOf("</VTYPE>", index));
            }

            if (BTSLUtil.isNullString(SUBID)) {
                SUBID = "0";
            }
            if (BTSLUtil.isNullString(txnId)) {
                txnId = "0";
            }

            parsedRequestStr = VOMSI.SERVICE_TYPE_VOUCHER_RETRIEVAL_ROLLBACK + chnl_message_sep + SUBID + chnl_message_sep + txnId;
            p_requestVO.setRequestMSISDN(SUBID);
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setVoucherType(voucherType);
            p_requestVO.setRequestMessageArray(PretupsBL.parsePlainMessage(parsedRequestStr));
        } catch (Exception e) {
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    public static void parseVoucherConsumptionRequest(RequestVO p_requestVO) throws Exception {
        final String methodName = "parseVoucherConsumptionRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        // boolean tagsMandatory;
        String cellId = null;
        String extnwcode = null;
        String switchId = null;
        try {

            final HashMap requestHashMap = new HashMap();
            final String requestStr = p_requestVO.getRequestMessage();
            int index = 0;
            
            index = requestStr.indexOf("<MSISDN>");
            final String msisdn1 = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
            requestHashMap.put("MSISDN", msisdn1);
            
            index = requestStr.indexOf("<PIN>");
            String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            requestHashMap.put("PIN", pin);
            
            /*index = requestStr.indexOf("<LOGINID>");
            final String loginId = requestStr.substring(index + "<LOGINID>".length(), requestStr.indexOf("</LOGINID>", index));
            requestHashMap.put("LOGINID", loginId);
            
            index = requestStr.indexOf("<PASSWORD>");
            final String password = requestStr.substring(index + "<PASSWORD>".length(), requestStr.indexOf("</PASSWORD>", index));
            requestHashMap.put("PASSWORD", password);
            
            index = requestStr.indexOf("<EXTCODE>");
            final String extCode = requestStr.substring(index + "<EXTCODE>".length(), requestStr.indexOf("</EXTCODE>", index));
            p_requestVO.setSenderExternalCode(extCode);
            requestHashMap.put("EXTCODE", extCode);*/
            
            index = requestStr.indexOf("<EXTREFNUM>");
            final String extRefNumber = requestStr.substring(index + "<EXTREFNUM>".length(), requestStr.indexOf("</EXTREFNUM>", index));
            p_requestVO.setExternalReferenceNum(extRefNumber);
            requestHashMap.put("EXTREFNUM", extRefNumber);
            
            index = requestStr.indexOf("<MSISDN2>");
            final String msisdn2 = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));
            requestHashMap.put("MSISDN2", msisdn2);
            
            index = requestStr.indexOf("<AMOUNT>");
            String amount = requestStr.substring(index + "<AMOUNT>".length(), requestStr.indexOf("</AMOUNT>", index));
            requestHashMap.put("AMOUNT", amount);
            
            index = requestStr.indexOf("<LANGUAGE1>");
            final String language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
            requestHashMap.put("LANGUAGE1", language1);
            
            index = requestStr.indexOf("<LANGUAGE2>");
            final String language2 = requestStr.substring(index + "<LANGUAGE2>".length(), requestStr.indexOf("</LANGUAGE2>", index));
            requestHashMap.put("LANGUAGE2", language2);
            
            index = requestStr.indexOf("<SELECTOR>");
            final String selector = requestStr.substring(index + "<SELECTOR>".length(), requestStr.indexOf("</SELECTOR>", index));
            requestHashMap.put("SELECTOR", selector);
            
            index = requestStr.indexOf("<VOUCHERCODE>");
            final String vouchercode = requestStr.substring(index + "<VOUCHERCODE>".length(), requestStr.indexOf("</VOUCHERCODE>", index));
            int vomsDamgPinLengthAllow = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_DAMG_PIN_LNTH_ALLOW))).intValue();
            if (BTSLUtil.isNullString(vouchercode)) {
                throw new BTSLBaseException("USSDC2SXMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_BLANK_VOUCHERCODE);
            }
            else if (vomsDamgPinLengthAllow>vouchercode.length()) {
            	final String[] strArr = new String[] {((Integer)vomsDamgPinLengthAllow).toString()};
                throw new BTSLBaseException("USSDC2SXMLStringParser", methodName, PretupsErrorCodesI.ERROR_VOMS_INVALID_PIN_LENGTH, 0, strArr, null);
               
            }
            index = requestStr.indexOf("<SERIALNUMBER>");
            String serialNo = requestStr.substring(index + "<SERIALNUMBER>".length(), requestStr.indexOf("</SERIALNUMBER>", index));
            
            index = requestStr.indexOf("<CELLID>");
            if (index >= 0) {
                cellId = requestStr.substring(index + "<CELLID>".length(), requestStr.indexOf("</CELLID>", index));
            }
            requestHashMap.put("CELLID", cellId);
            index = requestStr.indexOf("<SWITCHID>");
            if (index >= 0) {
                switchId = requestStr.substring(index + "<SWITCHID>".length(), requestStr.indexOf("</SWITCHID>", index));
            }
            requestHashMap.put("SWITCHID", switchId);
            
            
            
            index = requestStr.indexOf("<INFO1>");
            if(index>0)
            {
            	final String info1 = requestStr.substring(index + "<INFO1>".length(), requestStr.indexOf("</INFO1>", index));
            	requestHashMap.put("INFO1", info1);
            }
            index = requestStr.indexOf("<INFO2>");
            if(index>0)
            {
            	final String info2 = requestStr.substring(index + "<INFO2>".length(), requestStr.indexOf("</INFO2>", index));
            	requestHashMap.put("INFO2", info2);
            }
            index = requestStr.indexOf("<INFO3>");
            if(index>0)
            {
            	final String info3 = requestStr.substring(index + "<INFO3>".length(), requestStr.indexOf("</INFO3>", index));
            	requestHashMap.put("INFO3", info3);
            }
            index = requestStr.indexOf("<INFO4>");
            if(index>0)
            {
            	final String info4 = requestStr.substring(index + "<INFO4>".length(), requestStr.indexOf("</INFO4>", index));
            	requestHashMap.put("INFO4", info4);
            }
            index = requestStr.indexOf("<INFO5>");
            if(index>0)
            {
            	final String info5 = requestStr.substring(index + "<INFO5>".length(), requestStr.indexOf("</INFO5>", index));
            	requestHashMap.put("INFO5", info5);
            }
            
            p_requestVO.setRequestMap(requestHashMap);

           if (BTSLUtil.isNullString(amount)) {
                amount = "0";
            }

            if (BTSLUtil.isNullString(pin)) {
                pin = "0";
            }
            boolean ussdNewTagsMandatory = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY);
            if (ussdNewTagsMandatory) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        throw new BTSLBaseException("ExtAPIXMLStringParser", methodName, PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        throw new BTSLBaseException("ExtAPIXMLStringParser", methodName, PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
                    }
                } catch (BTSLBaseException btsle) {
                    throw btsle;
                }
            }

            p_requestVO.setRequestMSISDN(msisdn1);
            
           	if(msisdn2 != null && !msisdn2.isEmpty() ){
           		parsedRequestStr = PretupsI.SERVICE_TYPE_VOUCHER_CONSUMPTION + chnl_message_sep + msisdn2 + chnl_message_sep + vouchercode;
           	}else{
           	 parsedRequestStr = PretupsI.SERVICE_TYPE_VOUCHER_CONSUMPTION + chnl_message_sep + msisdn1 + chnl_message_sep + vouchercode;
           	}
            
           if(serialNo != null){
        	   parsedRequestStr = parsedRequestStr + chnl_message_sep + serialNo;
           }
           
           if(selector != null){
        	   parsedRequestStr = parsedRequestStr + chnl_message_sep + selector;
           }
          /*  if (language1 != null) {
                parsedRequestStr = parsedRequestStr + chnl_message_sep + language1;
            }
            if (cellId != null) {
                parsedRequestStr = parsedRequestStr + chnl_message_sep + cellId;
            }
            if (switchId != null) {
                parsedRequestStr = parsedRequestStr + chnl_message_sep + switchId;
            }*/

            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setReqSelector(selector);
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
            p_requestVO.setLanguage1(language1);
            p_requestVO.setCellId(cellId);
            p_requestVO.setSwitchId(switchId);
            p_requestVO.setExternalNetworkCode(extnwcode);
            p_requestVO.setVoucherCode(vouchercode);
            

        } catch (BTSLBaseException be) {
            _log.error(methodName, " BTSL Exception while parsing Request Message :" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("ExtAPIXMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    public static void parseVoucherConsReq(RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "parseVoucherConsReq";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        String voucherType = null;
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            index = requestStr.indexOf("<SUBID>");
            final String SUBID = requestStr.substring(index + "<SUBID>".length(), requestStr.indexOf("</SUBID>", index));
            index = requestStr.indexOf("<VTYPE>");
            if (index > 0) {
                voucherType = requestStr.substring(index + "<VTYPE>".length(), requestStr.indexOf("</VTYPE>", index));
            }

            parsedRequestStr = VOMSI.SERVICE_TYPE_VOUCHER_CON + chnl_message_sep + pin + chnl_message_sep + SUBID;
            p_requestVO.setRequestMSISDN(SUBID);
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setVoucherType(voucherType);
            p_requestVO.setRequestMSISDN(SUBID);
            p_requestVO.setRequestMessageArray(PretupsBL.parsePlainMessage(parsedRequestStr));
        } catch (Exception e) {
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    public static void parseVoucherQueryReq(RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "parseVoucherQueryReq";
        final String className = "USSDC2SXMLStringParser";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        String voucherType = null;
    	String action="";
        try {
        	HashMap requestHashMap = p_requestVO.getRequestMap();
            if (requestHashMap == null) {
                requestHashMap = new HashMap();
            }
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<PIN>");
            String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            index = requestStr.indexOf("<VTYPE>");
            if (index > 0) {
                voucherType = requestStr.substring(index + "<VTYPE>".length(), requestStr.indexOf("</VTYPE>", index));
            }
            index = requestStr.indexOf("<SNO>");
            String serialNo = requestStr.substring(index + "<SNO>".length(), requestStr.indexOf("</SNO>", index));
			// Code modified <ACTION> tag is added  by Naveen
			index=requestStr.indexOf("<ACTION>");
			if(index>0)
			 action=requestStr.substring(index+"<ACTION>".length(),requestStr.indexOf("</ACTION>",index));
			if(!BTSLUtil.isNullString(action)){
			if(action.length()<2 && (action.equalsIgnoreCase(PretupsI.VOUCHER_ENQ_ACTION_PIN) || action.equalsIgnoreCase(PretupsI.VOUCHER_ENQ_ACTION_SNO) || action.equalsIgnoreCase(PretupsI.VOUCHER_ENQ_ACTION_BOTH)))
			{}else{
				throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.C2S_ERROR_INVALID_ACTION_MSGFORMAT);
			   }
			}
            if (BTSLUtil.isNullString(pin)) {
                pin = "0";
            }
            if (BTSLUtil.isNullString(serialNo)) {
                serialNo = "0";
            }

            parsedRequestStr = VOMSI.SERVICE_TYPE_VOUCHER_QRY + chnl_message_sep + pin + chnl_message_sep + serialNo;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setVoucherType(voucherType);
            requestHashMap.put("voucherAction", action);
            p_requestVO.setRequestMap(requestHashMap);
			p_requestVO.setRequestMessageArray(PretupsBL.parsePlainMessage(parsedRequestStr));
        } catch (BTSLBaseException be)
		{
			_log.error(methodName,"BTSLBaseException e: "+be);
			if(be.isKey())
			{
				p_requestVO.setVomsMessage(be.getMessageKey());
				p_requestVO.setVomsError(BTSLUtil.getMessage(p_requestVO.getLocale(), be.getMessageKey(), be.getArgs()));
			}
		}
		catch (Exception e) {
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

   

	public static void parseVoucherRollBackReq(RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "parseVoucherRollBackReq";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        String voucherType = null;
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<PIN>");
            String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            index = requestStr.indexOf("<SUBID>");
            String SUBID = requestStr.substring(index + "<SUBID>".length(), requestStr.indexOf("</SUBID>", index));
            index = requestStr.indexOf("<VTYPE>");
            if (index > 0) {
                voucherType = requestStr.substring(index + "<VTYPE>".length(), requestStr.indexOf("</VTYPE>", index));
            }

            if (BTSLUtil.isNullString(pin)) {
                pin = "0";
            }
            if (BTSLUtil.isNullString(SUBID)) {
                SUBID = "0";
            }

            parsedRequestStr = VOMSI.SERVICE_TYPE_VOUCHER_ROLLBACK + chnl_message_sep + pin + chnl_message_sep + SUBID;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setVoucherType(voucherType);
            p_requestVO.setRequestMessageArray(PretupsBL.parsePlainMessage(parsedRequestStr));
        } catch (Exception e) {
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException(methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }
	
	/**
     * This method generates Channel DVD Request from XML String and formating
     * into white space seperated String
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void parseChannelDVDRequest(RequestVO p_requestVO) throws Exception {
    	final String methodName = "parseChannelDVDRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        try {
        	Boolean VOUCHER_PROFLE_IS_OPTIONAL = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.VOUCHER_PROFLE_IS_OPTIONAL);
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<MSISDN1>");
            final String msisdn1 = requestStr.substring(index + "<MSISDN1>".length(), requestStr.indexOf("</MSISDN1>", index));
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            index = requestStr.indexOf("<MSISDN2>");
            final String msisdn2 = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));
            index = requestStr.indexOf("<VOUCHERTYPE>");
            final String voucherType = requestStr.substring(index + "<VOUCHERTYPE>".length(), requestStr.indexOf("</VOUCHERTYPE>", index));
            index = requestStr.indexOf("<VOUCHERSEGMENT>");
            final String voucherSegment = requestStr.substring(index + "<VOUCHERSEGMENT>".length(), requestStr.indexOf("</VOUCHERSEGMENT>", index));
            index = requestStr.indexOf("<AMOUNT>");
            final String amount = requestStr.substring(index + "<AMOUNT>".length(), requestStr.indexOf("</AMOUNT>", index));
            index = requestStr.indexOf("<VOUCHERPROFILE>");
            String voucherProfile = requestStr.substring(index + "<VOUCHERPROFILE>".length(), requestStr.indexOf("</VOUCHERPROFILE>", index));
            if(!BTSLUtil.isNullString(voucherProfile) && "0".equals(voucherProfile)){
				throw new BTSLBaseException("USSDC2SXMLStringParser", methodName, PretupsErrorCodesI.VOUCHER_PRODUCT_INVALID);
			}
            else if (BTSLUtil.isNullString(voucherProfile) && VOUCHER_PROFLE_IS_OPTIONAL) {
            	voucherProfile = "0";
            }
            index = requestStr.indexOf("<QUANTITY>");
            final String quantity = requestStr.substring(index + "<QUANTITY>".length(), requestStr.indexOf("</QUANTITY>", index));
            index = requestStr.indexOf("<LANGUAGE1>");
            String language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
            index = requestStr.indexOf("<LANGUAGE2>");
            String language2 = requestStr.substring(index + "<LANGUAGE2>".length(), requestStr.indexOf("</LANGUAGE2>", index));
            index = requestStr.indexOf("<SELECTOR>");
            final String selector = requestStr.substring(index + "<SELECTOR>".length(), requestStr.indexOf("</SELECTOR>", index));
            if (BTSLUtil.isNullString(amount)) {
                _log.error(methodName, "Amount field is null ");
                throw new BTSLBaseException("USSDC2SXMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_BLANK_AMOUNT);
            }
            if (BTSLUtil.isNullString(msisdn2)) {
                _log.error(methodName, "Msisdn2 field is null ");
                throw new BTSLBaseException("USSDC2SXMLStringParser", methodName, PretupsErrorCodesI.MSISDN2_BLANK);
            }
            if (BTSLUtil.isNullString(quantity)) {
                _log.error(methodName, "Quantity field is null ");
                String [] msgargs = {quantity};
                throw new BTSLBaseException("USSDC2SXMLStringParser", methodName, PretupsErrorCodesI.EXTSYS_BLANK,msgargs);
            }
            if (BTSLUtil.isNullString(language1)) {
                language1 = Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
            }

            if (BTSLUtil.isNullString(language1)) {
                language1 = "0";
            }

            if (BTSLUtil.isNullString(language2)) {
                language1 = Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
            }

            if (BTSLUtil.isNullString(language2)) {
                language2 = "0";
            }
            parsedRequestStr = PretupsI.SERVICE_TYPE_DVD + chnl_message_sep + msisdn2 + chnl_message_sep + voucherType + chnl_message_sep + voucherSegment + chnl_message_sep + amount + chnl_message_sep + voucherProfile + chnl_message_sep + quantity + chnl_message_sep + selector + chnl_message_sep + language2 + chnl_message_sep + pin;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
        } catch (BTSLBaseException be) {
            _log.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("USSDC2SXMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }
    
   
    /**
     * @param p_requestVO
     * @throws Exception
     */
    public static void parseC2CVomsTransferRequest(RequestVO p_requestVO) throws Exception {
    	final String methodName = "parseC2CVomsTransferRequest";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        try {
            HashMap requestHashMap = p_requestVO.getRequestMap();
            if (requestHashMap == null) {
                requestHashMap = new HashMap();
            }
            final ArrayList arrList = new ArrayList();
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            requestHashMap.put("TYPE", type);

            index = requestStr.indexOf("<MSISDN>");
            final String msisdn1 = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
            requestHashMap.put("MSISDN", msisdn1);

            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            requestHashMap.put("PIN", pin);

            index = requestStr.indexOf("<EXTREFNUM>");
            final String extRefNumber = requestStr.substring(index + "<EXTREFNUM>".length(), requestStr.indexOf("</EXTREFNUM>", index));
            requestHashMap.put("EXTREFNUM", extRefNumber);

            index = requestStr.indexOf("<MSISDN2>");
            final String msisdn2 = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));
            requestHashMap.put("MSISDN2", msisdn2);

            index = requestStr.indexOf("<VOUCHERTYPE>");
            final String voucherType = requestStr.substring(index + "<VOUCHERTYPE>".length(), requestStr.indexOf("</VOUCHERTYPE>", index));
            requestHashMap.put("VOUCHERTYPE", voucherType);

            index = requestStr.indexOf("<VOUCHERSEGMENT>");
            final String voucherSegment = requestStr.substring(index + "<VOUCHERSEGMENT>".length(), requestStr.indexOf("</VOUCHERSEGMENT>", index));
            requestHashMap.put("VOUCHERSEGMENT", voucherSegment);
            
           
            StringBuilder voms = new StringBuilder();
            index = requestStr.indexOf("<VOUCHERDETAILS>");
            String voucherDetails = requestStr.substring(index + "<VOUCHERDETAILS>".length(), requestStr.indexOf("</VOUCHERDETAILS>", index));
            String[] voucherDetailsArr = voucherDetails.split("<VOUCHER>");

            for (String voucherDetailsArrObj : voucherDetailsArr) {
                   if (voucherDetailsArrObj != null && voucherDetailsArrObj.trim().length() > 0) {
                	   if(!BTSLUtil.isNullString(voms.toString())){
                		   voms.append(",");
                	   }
                	   index = voucherDetailsArrObj.indexOf("<DENOMINATION>");
                       final String denomination = voucherDetailsArrObj.substring(index + "<DENOMINATION>".length(),
                                     voucherDetailsArrObj.indexOf("</DENOMINATION>", index));
                       
                         index = voucherDetailsArrObj.indexOf("<FROMSERIALNO>");
                         final String fromSerialNo = voucherDetailsArrObj.substring(index + "<FROMSERIALNO>".length(),
                                       voucherDetailsArrObj.indexOf("</FROMSERIALNO>", index));

                         index = voucherDetailsArrObj.indexOf("<TOSERIALNO>");
                         final String toSerialNo = voucherDetailsArrObj.substring(index + "<TOSERIALNO>".length(),
                                       voucherDetailsArrObj.indexOf("</TOSERIALNO>", index));

                         voms.append(denomination+":"+fromSerialNo+":"+toSerialNo);
                   }
            }
            requestHashMap.put("VOUCHERDETAILS", voms);

            index = requestStr.indexOf("<LANGUAGE1>");
            final String language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
            requestHashMap.put("LANGUAGE1", language1);
            boolean channelTransferInfoRequired = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_TRANSFERS_INFO_REQUIRED);
            Boolean isTagReq=false;
            isTagReq= channelTransferInfoRequired;
            if(isTagReq)
            {
            	
                index = requestStr.indexOf("<REMARKS>");
                if(index>0)
                {
                	final String remarks = requestStr.substring(index + "<REMARKS>".length(), requestStr.indexOf("</REMARKS>", index));
                    requestHashMap.put("REMARKS", remarks);
                }
                index = requestStr.indexOf("<INFO1>");
                if(index>0)
                {
    	            final String info1 = requestStr.substring(index + "<INFO1>".length(), requestStr.indexOf("</INFO1>", index));
    	            requestHashMap.put("INFO1", info1);
                }
                index = requestStr.indexOf("<INFO2>");
                if(index>0)
                {
    	            final String info2 = requestStr.substring(index + "<INFO2>".length(), requestStr.indexOf("</INFO2>", index));
    	            requestHashMap.put("INFO2", info2);
                }
            }
            parsedRequestStr = PretupsI.SERVICE_TYPE_C2C_VOMS_TRANSFERS + chnl_message_sep + msisdn2 + chnl_message_sep + pin;
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
            p_requestVO.setExternalReferenceNum(extRefNumber);
            p_requestVO.setRequestMap(requestHashMap);
            p_requestVO.setReceiverMsisdn(msisdn2);
        }
       /* catch (BTSLBaseException be) {
            p_requestVO.setMessageCode(be.getMessageKey());
            p_requestVO.setMessageArguments(be.getArgs());
            throw be;
        }*/
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            loggerValue.setLength(0);
    		loggerValue.append("EXCEPTION: ");
    		loggerValue.append(e.getMessage());
    		_log.error(methodName, loggerValue);
            throw new BTSLBaseException("USSDC2SXMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
       } finally {
        	if (_log.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting p_requestVO : ");
             	loggerValue.append(p_requestVO.toString());
             	_log.debug(methodName, loggerValue);
             }
        }
    }
    
    public static void parseC2CVomsInitiateRequest(RequestVO p_requestVO) throws Exception {
    	final String methodName = "parseC2CVomsInitiateRequest";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        try {
            HashMap requestHashMap = p_requestVO.getRequestMap();
            if (requestHashMap == null) {
                requestHashMap = new HashMap();
            }
            final ArrayList arrList = new ArrayList();
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            requestHashMap.put("TYPE", type);

            index = requestStr.indexOf("<MSISDN>");
            final String msisdn1 = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
            requestHashMap.put("MSISDN", msisdn1);

            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            requestHashMap.put("PIN", pin);

            index = requestStr.indexOf("<EXTREFNUM>");
            final String extRefNumber = requestStr.substring(index + "<EXTREFNUM>".length(), requestStr.indexOf("</EXTREFNUM>", index));
            requestHashMap.put("EXTREFNUM", extRefNumber);

            index = requestStr.indexOf("<MSISDN2>");
            final String msisdn2 = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));
            requestHashMap.put("MSISDN2", msisdn2);

            index = requestStr.indexOf("<VOUCHERTYPE>");
            final String voucherType = requestStr.substring(index + "<VOUCHERTYPE>".length(), requestStr.indexOf("</VOUCHERTYPE>", index));
            requestHashMap.put("VOUCHERTYPE", voucherType);

            index = requestStr.indexOf("<VOUCHERSEGMENT>");
            final String voucherSegment = requestStr.substring(index + "<VOUCHERSEGMENT>".length(), requestStr.indexOf("</VOUCHERSEGMENT>", index));
            requestHashMap.put("VOUCHERSEGMENT", voucherSegment);
            
           
            StringBuilder voms = new StringBuilder();
            index = requestStr.indexOf("<VOUCHERDETAILS>");
            String voucherDetails = requestStr.substring(index + "<VOUCHERDETAILS>".length(), requestStr.indexOf("</VOUCHERDETAILS>", index));
            String[] voucherDetailsArr = voucherDetails.split("<VOUCHER>");

            for (String voucherDetailsArrObj : voucherDetailsArr) {
                   if (voucherDetailsArrObj != null && voucherDetailsArrObj.trim().length() > 0) {
                	   if(!BTSLUtil.isNullString(voms.toString())){
                		   voms.append(",");
                	   }
                	   index = voucherDetailsArrObj.indexOf("<DENOMINATION>");
                       final String denomination = voucherDetailsArrObj.substring(index + "<DENOMINATION>".length(),
                                     voucherDetailsArrObj.indexOf("</DENOMINATION>", index));
                       
                         index = voucherDetailsArrObj.indexOf("<QUANTITY>");
                         final String quantity = voucherDetailsArrObj.substring(index + "<QUANTITY>".length(),
                                       voucherDetailsArrObj.indexOf("</QUANTITY>", index));

                         voms.append(denomination+":"+ quantity);
                   }
            }
            requestHashMap.put("VOUCHERDETAILS", voms);

            index = requestStr.indexOf("<LANGUAGE1>");
            final String language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
            requestHashMap.put("LANGUAGE1", language1);
            boolean channelTransferInfoRequired = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_TRANSFERS_INFO_REQUIRED);
            Boolean isTagReq=false;
            isTagReq= channelTransferInfoRequired;
            if(isTagReq)
            {
            	
                index = requestStr.indexOf("<REMARKS>");
                if(index>0)
                {
                	final String remarks = requestStr.substring(index + "<REMARKS>".length(), requestStr.indexOf("</REMARKS>", index));
                    requestHashMap.put("REMARKS", remarks);
                }
                index = requestStr.indexOf("<INFO1>");
                if(index>0)
                {
    	            final String info1 = requestStr.substring(index + "<INFO1>".length(), requestStr.indexOf("</INFO1>", index));
    	            requestHashMap.put("INFO1", info1);
                }
                index = requestStr.indexOf("<INFO2>");
                if(index>0)
                {
    	            final String info2 = requestStr.substring(index + "<INFO2>".length(), requestStr.indexOf("</INFO2>", index));
    	            requestHashMap.put("INFO2", info2);
                }
            }
            parsedRequestStr = PretupsI.SERVICE_TYPE_C2C_VOMS_INITIIATE + chnl_message_sep + msisdn2 + chnl_message_sep + pin;
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
            p_requestVO.setExternalReferenceNum(extRefNumber);
            p_requestVO.setRequestMap(requestHashMap);
            p_requestVO.setReceiverMsisdn(msisdn2);
        }
       /* catch (BTSLBaseException be) {
            p_requestVO.setMessageCode(be.getMessageKey());
            p_requestVO.setMessageArguments(be.getArgs());
            throw be;
        }*/
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            loggerValue.setLength(0);
    		loggerValue.append("EXCEPTION: ");
    		loggerValue.append(e.getMessage());
    		_log.error(methodName, loggerValue);
            throw new BTSLBaseException("USSDC2SXMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
       } finally {
        	if (_log.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting p_requestVO : ");
             	loggerValue.append(p_requestVO.toString());
             	_log.debug(methodName, loggerValue);
             }
        }
    }
    /**
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public static void parseChannelUserDetailsRequestUssd(RequestVO p_requestVO) throws BTSLBaseException {
    	final String methodName = "parseChannelUserDetailsRequestUssd";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        try {
            HashMap requestHashMap = p_requestVO.getRequestMap();
            if (requestHashMap == null) {
                requestHashMap = new HashMap();
            }
            final String requestStr = p_requestVO.getRequestMessage();
            String type = null;
            int index=requestStr.indexOf("<TYPE>");
            type=requestStr.substring(index+"<TYPE>".length(),requestStr.indexOf("</TYPE>",index));
            requestHashMap.put("TYPE", type);
    		String extnwcode = "";
    		index=requestStr.indexOf("<EXTNWCODE>");
    		extnwcode=requestStr.substring(index+"<EXTNWCODE>".length(),requestStr.indexOf("</EXTNWCODE>",index));
    		requestHashMap.put("EXTNWCODE", extnwcode);
            index = requestStr.indexOf("<MSISDN>");
            final String msisdn1 = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
            requestHashMap.put("MSISDN", msisdn1);
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            requestHashMap.put("PIN", pin);
            index = requestStr.indexOf("<MSISDN2>");
            final String msisdn2 = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));
            requestHashMap.put("MSISDN2", msisdn2);
            final String language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
            requestHashMap.put("LANGUAGE1", msisdn2);
            if(BTSLUtil.isNullString(type)){
         	   throw new BTSLBaseException("USSDC2SXMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);	
            }
           if(BTSLUtil.isNullString(extnwcode)){
        	   throw new BTSLBaseException("USSDC2SXMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);	
            }
           if(BTSLUtil.isNullString(msisdn1)){
        	   throw new BTSLBaseException("USSDC2SXMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
           }
           if(!BTSLUtil.isNullString(msisdn1) &&  BTSLUtil.isNullString(pin) ){
       		throw new BTSLBaseException("USSDC2SXMLStringParser", methodName, PretupsErrorCodesI.PIN_REQUIRED);            	
           }
           if(BTSLUtil.isNullString(msisdn2)){
        	   throw new BTSLBaseException("USSDC2SXMLStringParser", methodName, PretupsErrorCodesI.MSISDN_INVALID_OR_BLANK);
           }
          
            parsedRequestStr = PretupsI.CHANNEL_USER_DETAILS + chnl_message_sep + msisdn1 + chnl_message_sep + pin + chnl_message_sep + msisdn2 + chnl_message_sep + language1;		
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setExternalNetworkCode(extnwcode);
            p_requestVO.setRequestMSISDN(msisdn1);
            p_requestVO.setPin(pin);
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMap(requestHashMap);
        }catch (BTSLBaseException be) {
            p_requestVO.setMessageCode(be.getMessageKey());
            p_requestVO.setMessageArguments(be.getArgs());
            throw be;
        } 
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            loggerValue.setLength(0);
    		loggerValue.append("EXCEPTION: ");
    		loggerValue.append(e.getMessage());
    		_log.error(methodName, loggerValue);
            throw new BTSLBaseException("USSDC2SXMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
        	if (_log.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append(p_requestVO.toString());
             	_log.debug(methodName, loggerValue);
             }
        }
    }
    
    /**
     * @param p_requestVO
     * @throws Exception
     */
    public static void parseTxnCountDetails(RequestVO p_requestVO,int p_action) throws Exception {
    	final String methodName = "parseChannelUserDetailsRequestUssd";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        try {
            HashMap requestHashMap = p_requestVO.getRequestMap();
            if (requestHashMap == null) {
                requestHashMap = new HashMap();
            }
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            requestHashMap.put("TYPE", type);

          
            index = requestStr.indexOf("<EXTNWCODE>");
            final String extNwCode = requestStr.substring(index + "<EXTNWCODE>".length(), requestStr.indexOf("</EXTNWCODE>", index));
            requestHashMap.put("EXTNWCODE", extNwCode);

            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            requestHashMap.put("PIN", pin);
            
            index = requestStr.indexOf("<LANGUAGE1>");
            final String language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
            requestHashMap.put("LANGUAGE1", language1);
            
            index = requestStr.indexOf("<MSISDN>");
            final String msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
            requestHashMap.put("MSISDN", msisdn);

            index = requestStr.indexOf("<FROMDATE>");
            final String fromdate = requestStr.substring(index + "<FROMDATE>".length(), requestStr.indexOf("</FROMDATE>", index));
            requestHashMap.put("FROMDATE", fromdate);
            
            index = requestStr.indexOf("<TODATE>");
            final String todate = requestStr.substring(index + "<TODATE>".length(), requestStr.indexOf("</TODATE>", index));
            requestHashMap.put("TODATE", todate);
            
            index = requestStr.indexOf("<SERVICETYPE>");
            final String serviceType = requestStr.substring(index + "<SERVICETYPE>".length(), requestStr.indexOf("</SERVICETYPE>", index));
            requestHashMap.put("SERVICETYPE", serviceType);
            
            if(BTSLUtil.isNullString(type)){
          	   throw new BTSLBaseException("USSDC2SXMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);	
             }
            if(BTSLUtil.isNullString(extNwCode)){
         	   throw new BTSLBaseException("USSDC2SXMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);	
             }
            if(BTSLUtil.isNullString(msisdn)){
         	   throw new BTSLBaseException("USSDC2SXMLStringParser", methodName, PretupsErrorCodesI.MSISDN_INVALID_OR_BLANK);
            }
            if(!BTSLUtil.isNullString(msisdn) &&  BTSLUtil.isNullString(pin) ){
        		throw new BTSLBaseException("USSDC2SXMLStringParser", methodName, PretupsErrorCodesI.PIN_REQUIRED);            	
            }
            
            if(BTSLUtil.isNullString(fromdate)){
          	   throw new BTSLBaseException("USSDC2SXMLStringParser", methodName, PretupsErrorCodesI.CCE_XML_ERROR_FROM_DATE_REQUIRED);
             }
            
            if(BTSLUtil.isNullString(todate)){
         	   throw new BTSLBaseException("USSDC2SXMLStringParser", methodName, PretupsErrorCodesI.CCE_XML_ERROR_TO_DATE_REQUIRED);
            }
            
            if(BTSLUtil.isNullString(serviceType)){
          	   throw new BTSLBaseException("USSDC2SXMLStringParser", methodName, PretupsErrorCodesI.SERVICE_TYPE_BLANK);
             }
            
            if(p_action == ParserUtility.C2S_N_PROD_TXN_DETAILS){
            	index = requestStr.indexOf("<TOPPRODUCTS>");
                final String topProducts = requestStr.substring(index + "<TOPPRODUCTS>".length(), requestStr.indexOf("</TOPPRODUCTS>", index));
                requestHashMap.put("TOPPRODUCTS", topProducts);
                XMLTagValueValidation.validateTopProductFlag(topProducts, true, "TOPPRODUCTS");
                
                index = requestStr.indexOf("<NUMBEROFPRODORDENO>");
                final String noOfProd = requestStr.substring(index + "<NUMBEROFPRODORDENO>".length(), requestStr.indexOf("</NUMBEROFPRODORDENO>", index));
                requestHashMap.put("NUMBEROFPRODORDENO", noOfProd);
                XMLTagValueValidation.validateNoOfProd(noOfProd, true, "NUMBEROFPRODORDENO");
            }
            
            ChannelUserVO  channelUserVO = ChannelUserVO.getInstance();
            
            parsedRequestStr = type + chnl_message_sep + msisdn + chnl_message_sep + msisdn + chnl_message_sep +pin;
            
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setExternalNetworkCode(extNwCode);
            p_requestVO.setRequestMSISDN(msisdn);
            p_requestVO.setPin(pin);
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMap(requestHashMap);
        }catch (BTSLBaseException be) {
            p_requestVO.setMessageCode(be.getMessageKey());
            p_requestVO.setMessageArguments(be.getArgs());
            throw be;
        } 
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            loggerValue.setLength(0);
    		loggerValue.append("EXCEPTION: ");
    		loggerValue.append(e.getMessage());
    		_log.error(methodName, loggerValue);
            throw new BTSLBaseException("USSDC2SXMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
        	if (_log.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append(p_requestVO.toString());
             	_log.debug(methodName, loggerValue);
             }
        }
    }
    /**
     * @param p_requestVO
     * @throws Exception
     */
    public static void totalTrnxDetailReq(RequestVO p_requestVO) throws Exception {
    	final String methodName = "totalTrnxDetailReq";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	
        	loggerValue.append(p_requestVO.toString());
        	_log.debug(methodName, loggerValue);
        }
        String parsedRequestStr = null;
        try {
            HashMap requestHashMap = p_requestVO.getRequestMap();
            if (requestHashMap == null) {
                requestHashMap = new HashMap();
            }
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            requestHashMap.put("TYPE", type);

            index = requestStr.indexOf("<EXTNWCODE>");
            final String extNwCode = requestStr.substring(index + "<EXTNWCODE>".length(), requestStr.indexOf("</EXTNWCODE>", index));
            requestHashMap.put("EXTNWCODE", extNwCode);

            index = requestStr.indexOf("<MSISDN>");
            final String msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
            requestHashMap.put("MSISDN", msisdn);
            
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            requestHashMap.put("PIN", pin);
            
            index = requestStr.indexOf("<LOGINID>");
            final String loginId = requestStr.substring(index + "<LOGINID>".length(), requestStr.indexOf("</LOGINID>", index));
            requestHashMap.put("LOGINID", loginId);
            
            index = requestStr.indexOf("<PASSWORD>");
            final String password = requestStr.substring(index + "<PASSWORD>".length(), requestStr.indexOf("</PASSWORD>", index));
            requestHashMap.put("PASSWORD", msisdn);
            
            index = requestStr.indexOf("<EXTCODE>");
            final String extcode = requestStr.substring(index + "<EXTCODE>".length(), requestStr.indexOf("</EXTCODE>", index));
            requestHashMap.put("EXTCODE", extcode);
            
            index = requestStr.indexOf("<FROMDATE>");
            final String fromdate = requestStr.substring(index + "<FROMDATE>".length(), requestStr.indexOf("</FROMDATE>", index));
            requestHashMap.put("FROMDATE", fromdate);
            
            index = requestStr.indexOf("<TODATE>");
            final String todate = requestStr.substring(index + "<TODATE>".length(), requestStr.indexOf("</TODATE>", index));
            requestHashMap.put("TODATE", todate);
            
            index = requestStr.indexOf("<FROMROW>");
            final String fromRow = requestStr.substring(index + "<FROMROW>".length(), requestStr.indexOf("</FROMROW>", index));
            requestHashMap.put("FROMROW", fromRow);
            
            index = requestStr.indexOf("<TOROW>");
            final String toRow = requestStr.substring(index + "<TOROW>".length(), requestStr.indexOf("</TOROW>", index));
            requestHashMap.put("TOROW", toRow);
            
            index = requestStr.indexOf("<STATUS>");
            final String status = requestStr.substring(index + "<STATUS>".length(), requestStr.indexOf("</STATUS>", index));
            requestHashMap.put("STATUS", status);
            
            index = requestStr.indexOf("<MSISDN2>");
            final String msisdn2 = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));
            requestHashMap.put("MSISDN2", msisdn2);
            
            index = requestStr.indexOf("<TRANSACTIONID>");
            final String transactionId = requestStr.substring(index + "<TRANSACTIONID>".length(), requestStr.indexOf("</TRANSACTIONID>", index));
            requestHashMap.put("TRANSACTIONID", transactionId);
            XMLStringValidation.validateExtTotalTxnDetailRequest(p_requestVO, extNwCode, msisdn, pin, loginId, password, extcode, fromdate, todate,status,msisdn2);
            
            ChannelUserVO  channelUserVO = ChannelUserVO.getInstance();
            
            parsedRequestStr = PretupsI.TRANSACTION_DETAILED_VIEW + chnl_message_sep + msisdn + chnl_message_sep + msisdn + chnl_message_sep + loginId +chnl_message_sep+pin;
            
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setPin(pin);
            p_requestVO.setRequestMSISDN(msisdn);
            p_requestVO.setExternalNetworkCode(extNwCode);
            p_requestVO.setSenderVO(channelUserVO);
            p_requestVO.setSenderLoginID(loginId);
            p_requestVO.setRequestMap(requestHashMap);
            

        }catch (BTSLBaseException be) {
            p_requestVO.setMessageCode(be.getMessageKey());
            p_requestVO.setMessageArguments(be.getArgs());
            throw be;
        } 
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            loggerValue.setLength(0);
    		loggerValue.append("EXCEPTION: ");
    		loggerValue.append(e.getMessage());
    		_log.error(methodName, loggerValue);
            throw new BTSLBaseException("USSDC2SXMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
        	if (_log.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append(p_requestVO.toString());
             	_log.debug(methodName, loggerValue);
             }
        }
		}
		 /**
	 * To write the Enter/Exit logger for parse request
	 * @param methodName Name of method called for logging
	 * @param loggerValue Buffer object in which logging
	 * @param enterExitKey Enter or Exit value for logging
	 * @param p_requestVO RequestVO for logging
	 */
	private static void printEnterExitLogger(String methodName, StringBuilder loggerValue, String enterExitKey, RequestVO p_requestVO){
		if (_log.isDebugEnabled()) {
	    	loggerValue.setLength(0);
	    	loggerValue.append(enterExitKey);
	    	loggerValue.append(p_requestVO.toString());
	    	_log.debug(methodName, loggerValue);
	    }
	}
   
    /**
     * Method to parse External Interface Vas Recharge Request
     * 
     * @param p_requestVO
     * @throws Exception
     * @author rahul.dutt
     */
    public static void parseChannelExtVASTransferRequest(RequestVO p_requestVO) throws Exception {
        final String methodName = "parseChannelExtVASTransferRequest";
        StringBuilder loggerValue= new StringBuilder();
        printEnterExitLogger(methodName, loggerValue, ENTRY_KEY,  p_requestVO);
        String parsedRequestStr = null;
        try {
            final HashMap requestHashMap = new HashMap();
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            requestHashMap.put("TYPE", type);
            index = requestStr.indexOf("<MSISDN>");
            final String msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
            requestHashMap.put("MSISDN", msisdn);
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            requestHashMap.put("PIN", pin);
            index = requestStr.indexOf("<MSISDN2>");
            final String msisdn2 = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));
            if (BTSLUtil.isNullString(msisdn2)) {
                throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.CHNL_ERROR_RECR_MSISDN_BLANK);
            }
            index = requestStr.indexOf("<AMOUNT>");
            final String amount = requestStr.substring(index + "<AMOUNT>".length(), requestStr.indexOf("</AMOUNT>", index));
            index = requestStr.indexOf("<LANGUAGE1>");
            final String language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
            index = requestStr.indexOf("<LANGUAGE2>");
            final String language2 = requestStr.substring(index + "<LANGUAGE2>".length(), requestStr.indexOf("</LANGUAGE2>", index));
            index = requestStr.indexOf("<SELECTOR>");
            final String selector = requestStr.substring(index + "<SELECTOR>".length(), requestStr.indexOf("</SELECTOR>", index));
			 index = requestStr.indexOf("<PRODUCTCODE>");
		 String productcode=null;
			if(index>0)
            {
            productcode = requestStr.substring(index + "<PRODUCTCODE>".length(), requestStr.indexOf("</PRODUCTCODE>", index));
            requestHashMap.put("PRODUCTCODE", productcode);
			}
            index = requestStr.indexOf("<INFO1>");
            if(index>0)
            {
            	final String info1 = requestStr.substring(index + "<INFO1>".length(), requestStr.indexOf("</INFO1>", index));
            	requestHashMap.put("INFO1", info1);
            }
            index = requestStr.indexOf("<INFO2>");
            if(index>0)
            {
            	final String info2 = requestStr.substring(index + "<INFO2>".length(), requestStr.indexOf("</INFO2>", index));
            	requestHashMap.put("INFO2", info2);
            }
            index = requestStr.indexOf("<INFO3>");
            if(index>0)
            {
            	final String info3 = requestStr.substring(index + "<INFO3>".length(), requestStr.indexOf("</INFO3>", index));
            	requestHashMap.put("INFO3", info3);
            }
            index = requestStr.indexOf("<INFO4>");
            if(index>0)
            {
            	final String info4 = requestStr.substring(index + "<INFO4>".length(), requestStr.indexOf("</INFO4>", index));
            	requestHashMap.put("INFO4", info4);
            }
            index = requestStr.indexOf("<INFO5>");
            if(index>0)
            {
            	final String info5 = requestStr.substring(index + "<INFO5>".length(), requestStr.indexOf("</INFO5>", index));
            	requestHashMap.put("INFO5", info5);
            }

            if (BTSLUtil.isNullString(selector)) {
                throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.VAS_PROMOVAS_REQ_SELECTOR_MISSING);
            }
            boolean multiAmountEnabled = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTI_AMOUNT_ENABLED);
            if (!BTSLUtil.isNullString(amount) && multiAmountEnabled) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_VAS_RECHARGE + chnl_message_sep + msisdn2 + chnl_message_sep + amount + chnl_message_sep + selector + chnl_message_sep + language2;
            } else if (!multiAmountEnabled) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_VAS_RECHARGE + chnl_message_sep + msisdn2 + chnl_message_sep + selector + chnl_message_sep + language2;
            } else if (multiAmountEnabled && BTSLUtil.isNullString(amount)) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_VAS_RECHARGE + chnl_message_sep + msisdn2 + chnl_message_sep + PretupsI.VAS_BLANK_SLCTR_AMNT + chnl_message_sep + selector + chnl_message_sep + language2;
            } else {
                throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.AMOUNT_REQUIRED);
            }
             if (!BTSLUtil.isNullString(productcode)) {
            	parsedRequestStr =productcode+parsedRequestStr;
            }
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setReqSelector(selector);
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn);
            p_requestVO.setRequestMap(requestHashMap);
            p_requestVO.setProductCode(productcode);
            if (BTSLUtil.isNullString(pin) && BTSLUtil.isNullString(msisdn)) {
                p_requestVO.setPinValidationRequired(false);
            }
        } catch (BTSLBaseException be) {
        	loggerValue.setLength(0);
			loggerValue.append(BTSL_EXCEPTION);
			loggerValue.append(be.getMessage());
			_log.error(methodName, loggerValue);
            throw be;
        } catch (Exception e) {
        	_log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
        	printEnterExitLogger(methodName, loggerValue, EXIT_KEY,  p_requestVO);
        }
    }
    

    /**
     * @param p_requestVO
     * @throws Exception
     * @author rahul.dutt
     *         This method generates the response for Vas recharge request
     *         incoming through extgw
     */
    public static void generateVasExtCreditTransferResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateVasExtCreditTransferResponse";
        StringBuilder loggerValue= new StringBuilder();
        printEnterExitLogger(methodName, loggerValue, ENTRY_KEY,  p_requestVO);
        String responseStr = null;
        StringBuilder sbf = null;
        final java.util.Date date = new java.util.Date();
        try {
        	String externalDateSystem = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_DATE_FORMAT);
            final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DDMMYYYYHHMMSS);
            sdf.setLenient(false); // this is required else it will convert
            sbf = new StringBuilder(1024);
            sbf.append("<?xml version=\"1.0\"?><COMMAND>");
            sbf.append("<TYPE>VASEXTRFRESP</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                String message = p_requestVO.getMessageCode();
                if (message.indexOf("_") != -1) {
                    message = message.substring(0, message.indexOf("_"));
                }
                sbf.append("<TXNSTATUS>").append(message).append("</TXNSTATUS>");
            }

            if (BTSLUtil.isNullString(p_requestVO.getTransactionID())) {
                sbf.append("<TXNID></TXNID>");
            } else {
                sbf.append("<TXNID>").append(p_requestVO.getTransactionID()).append("</TXNID>");
            }
            if (!p_requestVO.isSuccessTxn()) {
                String message = null;
            	message=BTSLUtil.getMessage(p_requestVO.getLocale(), p_requestVO.getServiceType()+"_"+p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
            	if(BTSLUtil.isNullString(message))
                    message = BTSLUtil.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
            	sbf.append("<MESSAGE>").append(message).append("</MESSAGE>");
            	
            } else {
               String message = null;
            	message=BTSLUtil.getMessage(p_requestVO.getLocale(), p_requestVO.getServiceType()+"_"+PretupsErrorCodesI.VAST_SENDER_SUCCESS, p_requestVO.getMessageArguments());
            	if(BTSLUtil.isNullString(message))
            	message=BTSLUtil.getMessage(p_requestVO.getLocale(), PretupsErrorCodesI.VAST_SENDER_SUCCESS, p_requestVO.getMessageArguments());
            	sbf.append("<MESSAGE>").append(message).append("</MESSAGE>");
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
        	_log.errorTrace(methodName, e);
            loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ExtAPIXMLStringParser[generateVasExtCreditTransferResponse]", PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateVasExtCreditTransferResponse:" + e
                    .getMessage());
        } finally {
        	if (_log.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: responseStr:");
             	loggerValue.append(responseStr.toString());
             	_log.debug(methodName, loggerValue);
             }
        }
    }   





    public static void parseSelfChannelUserBarRequest(RequestVO p_requestVO) throws Exception {
        String parsedRequestStr = null;
        final String methodName = "parseSelfChannelUserBar";
    	if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }

        try {
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<MSISDN>");
            final String msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            
            index = requestStr.indexOf("<PARENTMSISDN>");
            final String parentMsisdn = requestStr.substring(index + "<PARENTMSISDN>".length(), requestStr.indexOf("</PARENTMSISDN>", index));
            index = requestStr.indexOf("<BALANCE>");
            final String userBalance = requestStr.substring(index + "<BALANCE>".length(), requestStr.indexOf("</BALANCE>", index));
            index = requestStr.indexOf("<PRODUCTCODE>");
            final String productCode = requestStr.substring(index + "<PRODUCTCODE>".length(), requestStr.indexOf("</PRODUCTCODE>", index));
            
        	HashMap requestHashMap = p_requestVO.getRequestMap();
            if (requestHashMap == null) {
                requestHashMap = new HashMap();
            }
            
            requestHashMap.put("parentMsisdn", parentMsisdn);
            requestHashMap.put("userBalance", userBalance);
            requestHashMap.put("productCode", productCode);
            
            p_requestVO.setRequestMap(requestHashMap);
            
            if(ParserUtility.SELF_CUBAR.equals(type)) {
            	parsedRequestStr = ParserUtility.SELF_CUBAR  + chnl_message_sep + pin;
            }
            if(ParserUtility.SELF_CU_UNBAR.equals(type)) {
            	parsedRequestStr = ParserUtility.SELF_CU_UNBAR  + chnl_message_sep + pin;
            }
            
            
            // p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setServiceKeyword(type);
            p_requestVO.setRequestMSISDN(msisdn);
            p_requestVO.setDecryptedMessage(parsedRequestStr);

        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("USSDC2SXMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }












 
}
