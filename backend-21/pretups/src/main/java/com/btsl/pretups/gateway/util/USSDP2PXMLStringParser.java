package com.btsl.pretups.gateway.util;

/*
 * @(#)USSDP2PXMLStringParser.java
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
 * Vibhu Trehan Jun, 27, 2014 Split of file XMLStringParser
 * ------------------------------------------------------------------------------
 * -------------------
 * XML String class
 */
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/**
 * @author sanjeew.kumar
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class USSDP2PXMLStringParser {

    private static final Log _log = LogFactory.getLog(USSDP2PXMLStringParser.class.getName());
    private static String p2p_message_sep = null;
    private static String chnl_message_sep = null;
    private static String MULT_CRE_TRA_DED_ACC_SEP = null;

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

            MULT_CRE_TRA_DED_ACC_SEP = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULT_CRE_TRA_DED_ACC_SEP));
            if (BTSLUtil.isNullString(MULT_CRE_TRA_DED_ACC_SEP)) {
                MULT_CRE_TRA_DED_ACC_SEP = ",";
            }
        } catch (Exception e) {
            _log.errorTrace("static", e);
        }
    }

    /**
	 * ensures no instantiation
	 */
    private USSDP2PXMLStringParser(){
    	
    }
    
    /**
     * this method parse subscriber registration request from XML String and
     * fromating into white space seperated String
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void parseSubscriberRegistrationRequest(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("parseSubscriberRegistrationRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        String cellId = null;
        String switchId = null;
        // boolean tagsMandatory;
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            /*
             * int index=requestStr.indexOf("<TYPE>");
             * String
             * type=requestStr.substring(index+"<TYPE>".length(),requestStr
             * .indexOf("</TYPE>",index));
             */
            int index = requestStr.indexOf("<MSISDN1>");
            final String msisdn1 = requestStr.substring(index + "<MSISDN1>".length(), requestStr.indexOf("</MSISDN1>", index));
            index = requestStr.indexOf("<SUB_TYPE>");
            final String subtype = requestStr.substring(index + "<SUB_TYPE>".length(), requestStr.indexOf("</SUB_TYPE>", index));
            index = requestStr.indexOf("<CELLID>");
            if (index >= 0) {
                cellId = requestStr.substring(index + "<CELLID>".length(), requestStr.indexOf("</CELLID>", index));
            }
            index = requestStr.indexOf("<SWITCHID>");
            if (index >= 0) {
                switchId = requestStr.substring(index + "<SWITCHID>".length(), requestStr.indexOf("</SWITCHID>", index));
            }

            if (!BTSLUtil.isNullString(subtype)) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_REGISTERATION + p2p_message_sep + subtype;
            } else {
                parsedRequestStr = PretupsI.SERVICE_TYPE_REGISTERATION;
            }

            // tagsMandatory=((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY))).booleanValue();
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY))).booleanValue()) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        throw new BTSLBaseException("USSDP2PXMLStringParser", "parseSubscriberRegistrationRequest", PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        throw new BTSLBaseException("USSDP2PXMLStringParser", "parseSubscriberRegistrationRequest", PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
                    }
                } catch (BTSLBaseException btsle) {
                    throw btsle;
                }
            }
            p_requestVO.setCellId(cellId);
            p_requestVO.setSwitchId(switchId);
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
        } catch (Exception e) {
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseSubscriberRegistrationRequest", "Exception e: " + e);
            throw new BTSLBaseException("USSDP2PXMLStringParser", "parseSubscriberRegistrationRequest", PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseSubscriberRegistrationRequest", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * this method parse subscriber de-registration request from XML String and
     * fromating into white space seperated String
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void parseSubscriberDeRegistrationRequest(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("parseSubscriberDeRegistrationRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        try {
        	Boolean PIN_REQUIRED_P2P = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PIN_REQUIRED_P2P);
            final String requestStr = p_requestVO.getRequestMessage();
            /*
             * int index=requestStr.indexOf("<TYPE>");
             * String
             * type=requestStr.substring(index+"<TYPE>".length(),requestStr
             * .indexOf("</TYPE>",index));
             */
            int index = requestStr.indexOf("<MSISDN1>");
            final String msisdn1 = requestStr.substring(index + "<MSISDN1>".length(), requestStr.indexOf("</MSISDN1>", index));
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            try
            {
            	if(PIN_REQUIRED_P2P && BTSLUtil.isNullString(pin))
            
            {           
            	throw new BTSLBaseException("USSDP2PXMLStringParser", "parseSubscriberDeRegistrationRequest", PretupsErrorCodesI.P2P_MCDL_PIN_REQUIRED);          
            }
            }
            catch (BTSLBaseException btsle) {
                throw btsle;
            }
            if (!BTSLUtil.isNullString(pin)) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_DEREGISTERATION + p2p_message_sep + pin;
            } else {
                parsedRequestStr = PretupsI.SERVICE_TYPE_DEREGISTERATION;
            }
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
        } 
        catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        }catch (Exception e) {
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseSubscriberDeRegistrationRequest", "Exception e: " + e);
            throw new BTSLBaseException("USSDP2PXMLStringParser", "parseSubscriberDeRegistrationRequest", PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseSubscriberDeRegistrationRequest", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method parse Service Suspend Request from XML String to hashmap
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void parseP2PServiceSuspendRequest(RequestVO p_requestVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("parseP2PServiceSuspendRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        // boolean tagsMandatory;
        final String METHOD_NAME = "parseP2PServiceSuspendRequest";
        String cellId = null;
        String switchId = null;
        try {
            String parsedRequestStr = null;
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<MSISDN1>");
            final String msisdn1 = requestStr.substring(index + "<MSISDN1>".length(), requestStr.indexOf("</MSISDN1>", index));
            index = requestStr.indexOf("<PIN>");
            String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            cellId = requestStr.substring(index + "<CELLID>".length(), requestStr.indexOf("</CELLID>", index));
            index = requestStr.indexOf("<SWITCHID>");
            if (index >= 0) {
                switchId = requestStr.substring(index + "<SWITCHID>".length(), requestStr.indexOf("</SWITCHID>", index));
            }
            // tagsMandatory=((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY))).booleanValue();
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY))).booleanValue()) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        throw new BTSLBaseException("USSDP2PXMLStringParser", "parseP2PServiceSuspendRequest", PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        throw new BTSLBaseException("USSDP2PXMLStringParser", "parseP2PServiceSuspendRequest", PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
                    }
                } catch (BTSLBaseException btsle) {
                    throw btsle;
                }
            }
            if (BTSLUtil.isNullString(pin)) {
                // pin=PretupsI.DEFAULT_P2P_PIN;
                pin = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN));
            }
            parsedRequestStr = PretupsI.SERVICE_TYPE_P2PSUSPEND + p2p_message_sep + pin;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
            p_requestVO.setCellId(cellId);
            p_requestVO.setSwitchId(switchId);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseP2PServiceSuspendRequest", "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "USSDP2PXMLStringParser[parseP2PServiceSuspendRequest]", PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT, "", "", "parseP2PServiceSuspendRequest:" + e
                    .getMessage());
            throw new BTSLBaseException("USSDP2PXMLStringParser", "parseP2PServiceSuspendRequest", PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseServiceSuspendRequest", "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method parse Service Resume Request from XML String to hashmap
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void parseP2PServiceResumeRequest(RequestVO p_requestVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("parseP2PServiceResumeRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        // boolean tagsMandatory;
        final String METHOD_NAME = "parseP2PServiceResumeRequest";
        String cellId = null;
        String switchId = null;
        try {
            String parsedRequestStr = null;
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<MSISDN1>");
            final String msisdn1 = requestStr.substring(index + "<MSISDN1>".length(), requestStr.indexOf("</MSISDN1>", index));
            index = requestStr.indexOf("<PIN>");
            String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            index = requestStr.indexOf("<CELLID>");
            if (index >= 0) {
                cellId = requestStr.substring(index + "<CELLID>".length(), requestStr.indexOf("</CELLID>", index));
            }
            index = requestStr.indexOf("<SWITCHID>");
            if (index >= 0) {
                switchId = requestStr.substring(index + "<SWITCHID>".length(), requestStr.indexOf("</SWITCHID>", index));
            }
            // tagsMandatory=((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY))).booleanValue();
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY))).booleanValue()) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        throw new BTSLBaseException("USSDP2PXMLStringParser", "parseP2PServiceResumeRequest", PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        throw new BTSLBaseException("USSDP2PXMLStringParser", "parseP2PServiceResumeRequest", PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
                    }
                } catch (BTSLBaseException btsle) {
                    throw btsle;
                }
            }
            if (BTSLUtil.isNullString(pin)) {
                // pin=PretupsI.DEFAULT_P2P_PIN;
                pin = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN));
            }
            parsedRequestStr = PretupsI.SERVICE_TYPE_P2PRESUME + p2p_message_sep + pin;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
            p_requestVO.setCellId(cellId);
            p_requestVO.setSwitchId(switchId);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseP2PServiceResumeRequest", "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "USSDP2PXMLStringParser[parseP2PServiceResumeRequest]",
                PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT, "", "", "parseP2PServiceResumeRequest:" + e.getMessage());
            throw new BTSLBaseException("USSDP2PXMLStringParser", "parseP2PServiceResumeRequest", PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseP2PServiceResumeRequest", "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * this method parse AddBuddyRequest from XML String and formating into
     * white space seperated String
     * 
     * @param p_requestVO
     *            RequestVO
     */

    public static void parseAddBuddyRequest(RequestVO p_requestVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("parseAddBuddyRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        final String METHOD_NAME = "parseAddBuddyRequest";
        try {
            String parsedRequestStr = null;
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<MSISDN1>");
            final String msisdn1 = requestStr.substring(index + "<MSISDN1>".length(), requestStr.indexOf("</MSISDN1>", index));
            index = requestStr.indexOf("<BUDDYNAME>");
            final String buddyname = requestStr.substring(index + "<BUDDYNAME>".length(), requestStr.indexOf("</BUDDYNAME>", index));
            index = requestStr.indexOf("<MSISDN2>");
            final String msisdn2 = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));
            index = requestStr.indexOf("<PRFAMT>");
            final String prfamt = requestStr.substring(index + "<PRFAMT>".length(), requestStr.indexOf("</PRFAMT>", index));
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            if (BTSLUtil.isNullString(type)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.P2PGMB_TYPE_BLANK);
                throw new BTSLBaseException("USSDP2PXMLStringParser", "parseP2PGiveMeBalanceRequest", PretupsErrorCodesI.P2PGMB_TYPE_BLANK);
            }
            if (BTSLUtil.isNullString(msisdn1)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.USSD_ADDBUDDY_MSISDNBLNK);
                throw new BTSLBaseException("USSDP2PXMLStringParser", "parseAddBuddyRequest", PretupsErrorCodesI.USSD_ADDBUDDY_MSISDNBLNK);
            }
            if (BTSLUtil.isNullString(buddyname)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.USSD_ADDBUDDY_BLKBUDDYNAME);
                throw new BTSLBaseException("USSDP2PXMLStringParser", "parseAddBuddyRequest", PretupsErrorCodesI.USSD_ADDBUDDY_BLKBUDDYNAME);
            }
            
            if (BTSLUtil.isNullString(msisdn2)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.USSD_ADDBUDDY_MSISDNBLNK);
                throw new BTSLBaseException("USSDP2PXMLStringParser", "parseAddBuddyRequest", PretupsErrorCodesI.USSD_ADDBUDDY_MSISDNBLNK);
            }
            
            if (BTSLUtil.isNullString(pin)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.USSD_ADDBUDDY_PINBLANK);
                throw new BTSLBaseException("USSDP2PXMLStringParser", "parseAddBuddyRequest", PretupsErrorCodesI.USSD_ADDBUDDY_PINBLANK);
            }
            parsedRequestStr = PretupsI.SERVICE_TYPE_ADD_BUDDY + p2p_message_sep + buddyname + p2p_message_sep + msisdn2 + p2p_message_sep + prfamt + p2p_message_sep + pin;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
            
            
            
        } catch (BTSLBaseException be) {
            _log.error("parseAddBuddyRequest", " BTSL Exception while parsing Request Message :" + be.getMessage());
            throw be;
        } 
        
        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseAddBuddyRequest", "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "USSDP2PXMLStringParser[parseAddBuddyRequest]",
                PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT, "", "", "parseAddBuddyRequest:" + e.getMessage());
            throw new BTSLBaseException("USSDP2PXMLStringParser", "parseAddBuddyRequest", PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseAddBuddyRequest", "Exiting  p_requestVO: " + p_requestVO.toString());
            }
            
        }
    }

    /**
     * this method parse DeleteBuddy Request from XML String and formating into
     * white space seperated String
     * 
     * @param p_requestVO
     *            RequestVO
     */

    public static void parseDeleteBuddyRequest(RequestVO p_requestVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("parseDeleteBuddyRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        final String METHOD_NAME = "parseDeleteBuddyRequest";
        try {
            String parsedRequestStr = null;
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<MSISDN1>");
            final String msisdn1 = requestStr.substring(index + "<MSISDN1>".length(), requestStr.indexOf("</MSISDN1>", index));
            index = requestStr.indexOf("<MSISDN_NAME>");
            final String name = requestStr.substring(index + "<MSISDN_NAME>".length(), requestStr.indexOf("</MSISDN_NAME>", index));
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            parsedRequestStr = PretupsI.SERVICE_TYPE_DELETE_BUDDY + p2p_message_sep + name + p2p_message_sep + pin;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseDeleteBuddyRequest", "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "USSDP2PXMLStringParser[parseDeleteBuddyRequest]",
                PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT, "", "", "parseDeleteBuddyRequest:" + e.getMessage());
            throw new BTSLBaseException("USSDP2PXMLStringParser", "parseDeleteBuddyRequest", PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseDeleteBuddyRequest", "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * this method parse DeleteSubscriberList Request from XML String and
     * formating into asterisk (*) seperated String
     * 
     * @param p_requestVO
     *            RequestVO
     * @author Harsh Dixit
     * @date 09 Aug 12
     */

    public static void parseDelMultCreditListRequest(RequestVO p_requestVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("parseDelMultCreditListRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        try {
            String parsedRequestStr = null;
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<MSISDN>");
            final String msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            index = requestStr.indexOf("<LISTNAME>");
            final String name = requestStr.substring(index + "<LISTNAME>".length(), requestStr.indexOf("</LISTNAME>", index));
            parsedRequestStr = PretupsI.SERVICE_TYPE_DELETE_BUDDY_LIST + p2p_message_sep + pin + p2p_message_sep + name;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn);
        } catch (Exception e) {

            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseDelMultCreditListRequest", "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "USSDP2PXMLStringParser[parseDelMultCreditListRequest]", PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT, "", "", "parseDelMultCreditListRequest:" + e
                    .getMessage());
            throw new BTSLBaseException("USSDP2PXMLStringParser", "parseDelMultCreditListRequest", PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseDelMultCreditListRequest", "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * this method parse ListBuddy Request from XML String and formating into
     * white space seperated String
     * 
     * @param p_requestVO
     *            RequestVO
     */

    public static void parseListBuddyRequest(RequestVO p_requestVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("parseListBuddyRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        final String METHOD_NAME = "parseListBuddyRequest";
        try {
            String parsedRequestStr = null;
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<MSISDN1>");
            final String msisdn1 = requestStr.substring(index + "<MSISDN1>".length(), requestStr.indexOf("</MSISDN1>", index));
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            parsedRequestStr = PretupsI.SERVICE_TYPE_LIST_BUDDY + p2p_message_sep + pin;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseListBuddyRequest", "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "USSDP2PXMLStringParser[parseAddBuddyRequest]",
                PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT, "", "", "parseAddBuddyRequest:" + e.getMessage());
            throw new BTSLBaseException("USSDP2PXMLStringParser", "parseListBuddyRequest", PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseListBuddyRequest", "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method generates Daily Last Transfer Status Request(P2P) from XML
     * String and formating into white space seperated String
     * 
     * @param p_requestVO
     *            RequestVO
     * @author sanjeew.kumar
     * @Date 03/05/07
     */
    public static void parseLastTransferStatus(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("parseLastTransferStatus", "Entered p_requestVO: " + p_requestVO.toString());
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
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            try
            {
            	if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PIN_REQUIRED_P2P))).booleanValue() && BTSLUtil.isNullString(pin))
            
            {           
            	throw new BTSLBaseException("USSDP2PXMLStringParser", "parseLastTransferStatus", PretupsErrorCodesI.P2P_MCDL_PIN_REQUIRED);          
            }
            }
            catch (BTSLBaseException btsle) {
                throw btsle;
            }
            index = requestStr.indexOf("<CELLID>");
            if (index >= 0) {
                cellId = requestStr.substring(index + "<CELLID>".length(), requestStr.indexOf("</CELLID>", index));
            }
            index = requestStr.indexOf("<SWITCHID>");
            if (index >= 0) {
                switchId = requestStr.substring(index + "<SWITCHID>".length(), requestStr.indexOf("</SWITCHID>", index));
            }
            // tagsMandatory=((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY))).booleanValue();
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY))).booleanValue()) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        throw new BTSLBaseException("USSDP2PXMLStringParser", "parseLastTransferStatus", PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        throw new BTSLBaseException("USSDP2PXMLStringParser", "parseLastTransferStatus", PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
                    }
                } catch (BTSLBaseException btsle) {
                    throw btsle;
                }
            }
            parsedRequestStr = PretupsI.SERVICE_TYPE_P2P_LAST_TRANSFER_STATUS + chnl_message_sep + pin;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
            p_requestVO.setCellId(cellId);
            p_requestVO.setSwitchId(switchId);
        } 
        catch (BTSLBaseException btsle) {
            throw btsle;
        }
        catch (Exception e) {
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseLastTransferStatus", "Exception e: " + e);
            throw new BTSLBaseException("USSDP2PXMLStringParser", "parseLastTransferStatus", PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseLastTransferStatus", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * this method parse Self Bar Request from XML String and formating into
     * white space seperated String
     * 
     * @param p_requestVO
     *            RequestVO
     */

    public static void parseSelfBarRequest(RequestVO p_requestVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("parseSelfBarRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        final String METHOD_NAME = "parseSelfBarRequest";
   
        try {
            String parsedRequestStr = null;
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<MSISDN1>");
            final String msisdn1 = requestStr.substring(index + "<MSISDN1>".length(), requestStr.indexOf("</MSISDN1>", index));
            parsedRequestStr = PretupsI.SERVICE_TYPE_SELF_BAR+ p2p_message_sep;
            if(PretupsI.SERVICE_TYPE_BAR_GIVE_ME_BALANCE.equals(type)){
            index = requestStr.indexOf("<MSISDN2>");
            final String msisdn2 = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));
            parsedRequestStr = PretupsI.SERVICE_TYPE_BAR_GIVE_ME_BALANCE+ p2p_message_sep + msisdn2;
            
            if (BTSLUtil.isNullString(msisdn2)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.P2PGMB_MSISDN2_BLANK);
                throw new BTSLBaseException("USSDP2PXMLStringParser", "parseSelfBarRequest", PretupsErrorCodesI.P2PGMB_MSISDN2_BLANK);
            }
           
             if(!(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH))).intValue()<msisdn2.length()&&((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue()>msisdn2.length())){
            	 p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_INVALID_RECEIVER_MSISDN);
                 throw new BTSLBaseException("USSDP2PXMLStringParser", "parseSelfBarRequest", PretupsErrorCodesI.P2P_ERROR_INVALID_RECEIVER_MSISDN); 
             }
             Pattern p = Pattern.compile("[^0-9 ]", Pattern.CASE_INSENSITIVE);
             Matcher m = p.matcher(msisdn2);
            if( m.find()){
            	p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_INVALID_RECEIVER_MSISDN);
                throw new BTSLBaseException("process", "GMBBAR", PretupsErrorCodesI.P2P_ERROR_INVALID_RECEIVER_MSISDN); 
            }
            m = p.matcher(msisdn1);
            		if( m.find()){
            			p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_INVALID_SENDER_MSISDN);
                        throw new BTSLBaseException("process", "GMBBAR", PretupsErrorCodesI.P2P_ERROR_INVALID_SENDER_MSISDN); 
            		}
        	
            }
            p_requestVO.setServiceType(type);     
            p_requestVO.setRequestMSISDN(msisdn1);
            p_requestVO.setDecryptedMessage(parsedRequestStr);
        }catch(BTSLBaseException be){
        	_log.errorTrace(METHOD_NAME, be);
            throw be;
        }
        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseSelfBarRequest", "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "USSDP2PXMLStringParser[parseSelfBarRequest]",
                PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT, "", "", "parseSelfBarRequest:" + e.getMessage());
            throw new BTSLBaseException("USSDP2PXMLStringParser", "parseSelfBarRequest", PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseSelfBarRequest", "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method generates multiple recipient credit transfer request in XML
     * format from requestVO
     * 
     * @param p_requestVO
     * @throws Exception
     * @author jasmine kaur
     */
    public static void parseP2PCRITRequest(RequestVO p_requestVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("parseP2PCRITRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        String amount = null;
        String msisdnr = null;
        int totrece = 0;
        StringBuffer sbf = null;
        int msisdnlen = 0;
        int amountlen = 0, index1 = 0, index2 = 0;
        String[] arrmsisdn = null;
        int arrlen = 0;
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<MSISDN1>");
            final String msisdn1 = requestStr.substring(index + "<MSISDN1>".length(), requestStr.indexOf("</MSISDN1>", index));
            index = requestStr.indexOf("<PIN>");
            String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            index = requestStr.indexOf("<LANGUAGE1>");
            String language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
            index = requestStr.indexOf("<LANGUAGE2>");
            String language2 = requestStr.substring(index + "<LANGUAGE2>".length(), requestStr.indexOf("</LANGUAGE2>", index));
            index = requestStr.indexOf("<SELECTOR>");
            String selector = requestStr.substring(index + "<SELECTOR>".length(), requestStr.indexOf("</SELECTOR>", index));
            index = requestStr.indexOf("<TOTRECEIVER>");
            final String totreceiver = requestStr.substring(index + "<TOTRECEIVER>".length(), requestStr.indexOf("</TOTRECEIVER>", index));
            index1 = requestStr.indexOf("<RECEIPENTS>");
            index2 = requestStr.indexOf("</RECEIPENTS>");

            if (BTSLUtil.isNullString(pin)) {
                pin = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN));
            }

            if (_log.isDebugEnabled()) {
                _log.debug("parseP2PCRITRequest", "language1:" + language1 + ":");
            }

            if (BTSLUtil.isNullString(language1)) {
                language1 = Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
            }

            if (BTSLUtil.isNullString(language1)) {
                language1 = "0";
            }

            if (_log.isDebugEnabled()) {
                _log.debug("parseP2PCRITRequest", "language1:" + language1 + ":");
            }

            if (_log.isDebugEnabled()) {
                _log.debug("parseP2PCRITRequest", "language2:" + language2 + ":");
            }

            if (BTSLUtil.isNullString(language2)) {
                language2 = Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
            }

            if (BTSLUtil.isNullString(language2)) {
                language2 = "0";
            }
            if (_log.isDebugEnabled()) {
                _log.debug("parseP2PCRITRequest", "language2:" + language2 + ":");
            }

            if (BTSLUtil.isNullString(selector)) {
                final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(PretupsI.SERVICE_TYPE_P2PRECHARGE);
                if (serviceSelectorMappingVO != null) {
                    selector = serviceSelectorMappingVO.getSelectorCode();
                }
            }

            if ((!(index1 != -1)) && (!(index2 != -1))) {
                throw new BTSLBaseException(PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
            }

            totrece = Integer.parseInt(totreceiver);
            msisdnlen = "<MSISDNR>".length();
            amountlen = "<AMOUNT>".length();
            if (totrece > 1) {
                sbf = new StringBuffer();
                arrmsisdn = requestStr.split("<MSISDNR>");
                arrlen = arrmsisdn.length;
                for (int i = 1; i < arrlen; i++) {
                    index = requestStr.indexOf("<RECORD>", index);
                    index = requestStr.indexOf("<MSISDNR>", index);
                    msisdnr = requestStr.substring(index + msisdnlen, requestStr.indexOf("</MSISDNR>", index));
                    index = requestStr.indexOf("<AMOUNT>", index);
                    amount = requestStr.substring(index + amountlen, requestStr.indexOf("</AMOUNT>", index));

                    index = requestStr.indexOf("</RECORD>", index);

                    parsedRequestStr = PretupsI.MULT_CRE_TRA_DED_ACC + p2p_message_sep + msisdnr + p2p_message_sep + amount + (p2p_message_sep + selector) + (p2p_message_sep + language2) + p2p_message_sep + pin;

                    sbf = sbf.append(parsedRequestStr + MULT_CRE_TRA_DED_ACC_SEP);
                }
                parsedRequestStr = sbf.toString();
            } else {
                index = requestStr.indexOf("<RECORD>");
                index = requestStr.indexOf("<MSISDNR>", index);
                msisdnr = requestStr.substring(index + msisdnlen, requestStr.indexOf("</MSISDNR>", index));
                index = requestStr.indexOf("<AMOUNT>", index);
                amount = requestStr.substring(index + amountlen, requestStr.indexOf("</AMOUNT>", index));
                index = requestStr.indexOf("</RECORD>");

                parsedRequestStr = PretupsI.MULT_CRE_TRA_DED_ACC + p2p_message_sep + msisdnr + p2p_message_sep + amount + (p2p_message_sep + selector) + (p2p_message_sep + language2) + p2p_message_sep + pin;
            }

            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(language2));
            p_requestVO.setReqSelector(selector);
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
        } catch (BTSLBaseException be) {
            _log.error("parseP2PCRITRequest", " BTSL Exception while parsing Request Message :" + be.getMessage());
            throw be;
        } catch (Exception e) {
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseP2PCRITRequest", "Exception e: " + e);
            throw new BTSLBaseException("USSDP2PXMLStringParser", "parseP2PCRITRequest", PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseP2PCRITRequest", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * parseGiveMeBalanceRequest
     * Request of Balance from the external system.
     * 
     * @param p_requestVO
     * @throws Exception
     * @author amit
     */
    public static void parseGiveMeBalanceRequest(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("parseGiveMeBalanceRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        final String METHOD_NAME = "parseGiveMeBalanceRequest";
        String parsedRequestStr = null;
        // boolean tagsMandatory;
        String cellId = null;
        String switchId = null;
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            final HashMap requestHashMap = new HashMap();

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

            index = requestStr.indexOf("<LANGUAGE1>");
            String language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
            requestHashMap.put("LANGUAGE1", language1);

            index = requestStr.indexOf("<LANGUAGE2>");
            String language2 = requestStr.substring(index + "<LANGUAGE2>".length(), requestStr.indexOf("</LANGUAGE2>", index));
            requestHashMap.put("LANGUAGE2", language2);

            index = requestStr.indexOf("<CELLID>");
            if (index >= 0) {
                cellId = requestStr.substring(index + "<CELLID>".length(), requestStr.indexOf("</CELLID>", index));
            }
            index = requestStr.indexOf("<SWITCHID>");
            if (index >= 0) {
                switchId = requestStr.substring(index + "<SWITCHID>".length(), requestStr.indexOf("</SWITCHID>", index));
            }

            p_requestVO.setRequestMap(requestHashMap);

            // amount field should be mandatory
            if (BTSLUtil.isNullString(amount)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_BLANK_AMOUNT);
                _log.error("parseGiveMeBalanceRequest", "Amount field is null");
                throw new BTSLBaseException("USSDP2PXMLStringParser", "parseGiveMeBalanceRequest", PretupsErrorCodesI.P2P_ERROR_BLANK_AMOUNT);
            }

            if (BTSLUtil.isNullString(language1)) {
                language1 = Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
            }
            if (BTSLUtil.isNullString(language1)) {
                language1 = "0";
            }

            if (_log.isDebugEnabled()) {
                _log.debug("parseGiveMeBalanceRequest", "language1:" + language1 + ":");
            }

            if (BTSLUtil.isNullString(language2)) {
                language2 = Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
            }
            if (BTSLUtil.isNullString(language2)) {
                language2 = "0";
            }

            if (_log.isDebugEnabled()) {
                _log.debug("parseGiveMeBalanceRequest", "language2:" + language2 + ":");
            }

            if (BTSLUtil.isNullString(type)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.P2PGMB_TYPE_BLANK);
                throw new BTSLBaseException("USSDP2PXMLStringParser", "parseP2PGiveMeBalanceRequest", PretupsErrorCodesI.P2PGMB_TYPE_BLANK);
            }
            if (BTSLUtil.isNullString(msisdn1)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.P2PGMB_MSISDN1_BLANK);
                throw new BTSLBaseException("USSDP2PXMLStringParser", "parseP2PGiveMeBalanceRequest", PretupsErrorCodesI.P2PGMB_MSISDN1_BLANK);
            }
            if (BTSLUtil.isNullString(msisdn2)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.P2PGMB_MSISDN2_BLANK);
                throw new BTSLBaseException("USSDP2PXMLStringParser", "parseP2PGiveMeBalanceRequest", PretupsErrorCodesI.P2PGMB_MSISDN2_BLANK);
            }
            if (!BTSLUtil.isNullString(msisdn1) && !BTSLUtil.isNullString(msisdn2) && msisdn1.equals(msisdn2)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.P2PGMB_MSISDN1_MSISDN2_EQUAL);
                throw new BTSLBaseException("USSDP2PXMLStringParser", "parseP2PGiveMeBalanceRequest", PretupsErrorCodesI.P2PGMB_MSISDN1_MSISDN2_EQUAL);
            }
            Pattern p = Pattern.compile("[^0-9 ]", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(amount);
           if( m.find()){
           	p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_AMOUNT);
           	throw new BTSLBaseException("GiveMeBalanceHandler", METHOD_NAME, PretupsErrorCodesI.INVALID_AMOUNT);
           }
            
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY))).booleanValue()) {
                try {
                    if (BTSLUtil.isNullString(cellId)) {
                        p_requestVO.setMessageCode(PretupsErrorCodesI.USSD_CELLID_BLANK_ERROR);
                        throw new BTSLBaseException("USSDP2PXMLStringParser", "parseGiveMeBalanceRequest", PretupsErrorCodesI.USSD_CELLID_BLANK_ERROR);
                    }
                    if (BTSLUtil.isNullString(switchId)) {
                        p_requestVO.setMessageCode(PretupsErrorCodesI.USSD_SWITCHID_BLANK_ERROR);
                        throw new BTSLBaseException("USSDP2PXMLStringParser", "parseGiveMeBalanceRequest", PretupsErrorCodesI.USSD_SWITCHID_BLANK_ERROR);
                    }
                } catch (BTSLBaseException btsle) {
                    throw btsle;
                }
            }
            parsedRequestStr = PretupsI.SERVICE_TYPE_GIVE_ME_BALANCE + p2p_message_sep + msisdn2 + p2p_message_sep + amount;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(language2));
            p_requestVO.setRequestMSISDN(msisdn1);
            p_requestVO.setCellId(cellId);
            p_requestVO.setSwitchId(switchId);
        } catch (BTSLBaseException be) {
            _log.error("parseGiveMeBalanceRequest", " BTSL Exception while parsing Request Message :" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2PGMB_INVALID_MESSAGE);
            _log.error("parseGiveMeBalanceRequest", "Exception e: " + e);
            throw new BTSLBaseException("USSDP2PXMLStringParser", "parseGiveMeBalanceRequest", PretupsErrorCodesI.P2PGMB_INVALID_MESSAGE);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseGiveMeBalanceRequest", "Exiting p_requestVO ID: " + p_requestVO.getRequestIDStr());
            }
        }
    }

    public static void parseP2PMCDAddModifyDeleteRequest(RequestVO p_requestVO) throws Exception {

        if (_log.isDebugEnabled()) {
            _log.debug("parseP2PMCDAddModifyDeleteRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        String[] arrRecord = null;
        StringBuffer sbf = new StringBuffer();
        int addrequest = 0;

        try {
            final HashMap requestHashMap = new HashMap();
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<MSISDN>");
            final String msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
            requestHashMap.put("MSISDN", msisdn);
            index = requestStr.indexOf("<PIN>");
            String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            requestHashMap.put("PIN", pin);
            p_requestVO.setMcdPIn(pin);
            index = requestStr.indexOf("<LISTNAME>");
            final String listName = requestStr.substring(index + "<LISTNAME>".length(), requestStr.indexOf("</LISTNAME>", index));
            requestHashMap.put("LISTNAME", listName);
            if (BTSLUtil.isNullString(pin)) {
                pin = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN));
            }
            index = requestStr.indexOf("<LISTRECORDS>");
            arrRecord = requestStr.split("<MSISDN1>");
            for (int i = 1; i < arrRecord.length; i++) {
                index = requestStr.indexOf("<LISTRECORD>", index);
                index = requestStr.indexOf("<MSISDN1>", index);
                final String msisdn1 = requestStr.substring(index + "<MSISDN1>".length(), requestStr.indexOf("</MSISDN1>", index));
                index = requestStr.indexOf("<SELECTOR1>", index);
                final String selector1 = requestStr.substring(index + "<SELECTOR1>".length(), requestStr.indexOf("</SELECTOR1>", index));
                index = requestStr.indexOf("<AMOUNT1>", index);
                final String amount1 = requestStr.substring(index + "<AMOUNT1>".length(), requestStr.indexOf("</AMOUNT1>", index));
                index = requestStr.indexOf("<SELECTOR2>", index);
                final String selector2 = requestStr.substring(index + "<SELECTOR2>".length(), requestStr.indexOf("</SELECTOR2>", index));
                index = requestStr.indexOf("<AMOUNT2>", index);
                final String amount2 = requestStr.substring(index + "<AMOUNT2>".length(), requestStr.indexOf("</AMOUNT2>", index));
                index = requestStr.indexOf("<ACTION>", index);
                final String action = requestStr.substring(index + "<ACTION>".length(), requestStr.indexOf("</ACTION>", index));
                index = requestStr.indexOf("</LISTRECORD>", index);
                if ((!BTSLUtil.isNullString(selector1)) && (!BTSLUtil.isNullString(selector2))) {
                    if ((!BTSLUtil.isNullString(amount1)) && (!BTSLUtil.isNullString(amount2))) {
                        parsedRequestStr = PretupsI.P2P_MCD_LIST_SERVICE_TYPE + p2p_message_sep + msisdn1 + p2p_message_sep + pin + p2p_message_sep + selector1 + p2p_message_sep + amount1 + p2p_message_sep + selector2 + p2p_message_sep + amount2 + p2p_message_sep + action;
                    } else if (!BTSLUtil.isNullString(amount1)) {
                        parsedRequestStr = PretupsI.P2P_MCD_LIST_SERVICE_TYPE + p2p_message_sep + msisdn1 + p2p_message_sep + pin + p2p_message_sep + selector1 + p2p_message_sep + amount1 + p2p_message_sep + selector2 + p2p_message_sep + ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_MCDL_DEFAULT_AMOUNT))).intValue() + p2p_message_sep + action;
                    } else if (!BTSLUtil.isNullString(amount2)) {
                        parsedRequestStr = PretupsI.P2P_MCD_LIST_SERVICE_TYPE + p2p_message_sep + msisdn1 + p2p_message_sep + pin + p2p_message_sep + selector1 + p2p_message_sep + ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_MCDL_DEFAULT_AMOUNT))).intValue() + p2p_message_sep + selector2 + p2p_message_sep + amount2 + p2p_message_sep + action;
                    } else {
                        parsedRequestStr = PretupsI.P2P_MCD_LIST_SERVICE_TYPE + p2p_message_sep + msisdn1 + p2p_message_sep + pin + p2p_message_sep + selector1 + p2p_message_sep + ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_MCDL_DEFAULT_AMOUNT))).intValue() + p2p_message_sep + selector2 + p2p_message_sep + ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_MCDL_DEFAULT_AMOUNT))).intValue() + p2p_message_sep + action;
                    }
                    if (PretupsI.P2P_MCD_LIST_ACTION_ADD.equals(action)) {
                        addrequest = addrequest + 2;
                    }
                } else if ((!BTSLUtil.isNullString(selector1))) {
                    if (!BTSLUtil.isNullString(amount1)) {
                        parsedRequestStr = PretupsI.P2P_MCD_LIST_SERVICE_TYPE + p2p_message_sep + msisdn1 + p2p_message_sep + pin + p2p_message_sep + selector1 + p2p_message_sep + amount1 + p2p_message_sep + action;
                    } else {
                        parsedRequestStr = PretupsI.P2P_MCD_LIST_SERVICE_TYPE + p2p_message_sep + msisdn1 + p2p_message_sep + pin + p2p_message_sep + selector1 + p2p_message_sep + ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_MCDL_DEFAULT_AMOUNT))).intValue() + p2p_message_sep + action;
                    }
                    if (PretupsI.P2P_MCD_LIST_ACTION_ADD.equals(action)) {
                        addrequest++;
                    }
                } else if ((!BTSLUtil.isNullString(selector2))) {
                    if (!BTSLUtil.isNullString(amount2)) {
                        parsedRequestStr = PretupsI.P2P_MCD_LIST_SERVICE_TYPE + p2p_message_sep + msisdn1 + p2p_message_sep + pin + p2p_message_sep + selector2 + p2p_message_sep + amount2 + p2p_message_sep + action;
                    } else {
                        parsedRequestStr = PretupsI.P2P_MCD_LIST_SERVICE_TYPE + p2p_message_sep + msisdn1 + p2p_message_sep + pin + p2p_message_sep + selector2 + p2p_message_sep + ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_MCDL_DEFAULT_AMOUNT))).intValue() + p2p_message_sep + action;
                    }
                    if (PretupsI.P2P_MCD_LIST_ACTION_ADD.equals(action)) {
                        addrequest++;
                    }
                } else {
                    throw new BTSLBaseException("USSDP2PXMLStringParser", "parseP2PMCDAddModifyDeleteRequest", PretupsErrorCodesI.P2P_ERROR_MCD_LIST_SELECTOR_REQUIRED);
                }
                if (i == arrRecord.length - 1) {
                    sbf = sbf.append(parsedRequestStr);
                } else {
                    sbf = sbf.append(parsedRequestStr + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.MCDL_DIFFERENT_REQUEST_SEPERATOR)));
                }
            }
            parsedRequestStr = sbf.toString();
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn);
            p_requestVO.setMcdListName(listName);
            p_requestVO.setMcdListAddCount(addrequest);
        } catch (Exception e) {
            // e.printStackTrace();
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseP2PMCDAddModifyDeleteRequest", "Exception e: " + e);
            throw new BTSLBaseException("USSDP2PXMLStringParser", "parseP2PMCDAddModifyDeleteRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseP2PMCDAddModifyDeleteRequest", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    public static void parseP2PMCDListViewRequest(RequestVO p_requestVO) throws Exception {

        if (_log.isDebugEnabled()) {
            _log.debug("parseP2PMCDListViewRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        final String METHOD_NAME = "parseP2PMCDListViewRequest";
        String parsedRequestStr = null;
        final StringBuffer sbf = new StringBuffer();
        // String MESSAGE_SEP=((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_PLAIN_SMS_SEPARATOR));
        try {
            final HashMap requestHashMap = new HashMap();
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<MSISDN>");
            final String msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
            requestHashMap.put("MSISDN", msisdn);
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            requestHashMap.put("PIN", pin);
            p_requestVO.setMcdPIn(pin);
            index = requestStr.indexOf("<LISTNAME>");
            final String listName = requestStr.substring(index + "<LISTNAME>".length(), requestStr.indexOf("</LISTNAME>", index));
            requestHashMap.put("LISTNAME", listName);

            parsedRequestStr = PretupsI.P2P_MCD_LIST_VIEW + p2p_message_sep + listName;

            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn);
            p_requestVO.setMcdListName(listName);

        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseP2PMCDListViewRequest", "Exception e: " + e);
            throw new BTSLBaseException("USSDP2PXMLStringParser", "parseP2PMCDListViewRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseP2PMCDListViewRequest", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }

    }

    public static void parseP2PMCDListCreditRequest(RequestVO p_requestVO) throws Exception {
    	final String METHOD_NAME = "parseP2PMCDListCreditRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        final StringBuffer sbf = new StringBuffer();
        final String MESSAGE_SEP = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_PLAIN_SMS_SEPARATOR));
        try {
            final HashMap requestHashMap = new HashMap();
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<MSISDN>");
            final String msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
            requestHashMap.put("MSISDN", msisdn);
            index = requestStr.indexOf("<PIN>");
            String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            requestHashMap.put("PIN", pin);
            p_requestVO.setMcdPIn(pin);
            if (BTSLUtil.isNullString(pin)) {
                pin = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN));
            }
            index = requestStr.indexOf("<LISTNAME>");
            final String listName = requestStr.substring(index + "<LISTNAME>".length(), requestStr.indexOf("</LISTNAME>", index));
            requestHashMap.put("LISTNAME", listName);

            index = requestStr.indexOf("<SELECTOR>");
            final String selector = requestStr.substring(index + "<SELECTOR>".length(), requestStr.indexOf("</SELECTOR>", index));
            requestHashMap.put("SELECTOR", listName);
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "selector: " + selector);
            }
            parsedRequestStr = PretupsI.P2P_MCD_LIST_REQUEST + MESSAGE_SEP + listName + MESSAGE_SEP + selector + MESSAGE_SEP + pin;

            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn);
            p_requestVO.setMcdListName(listName);
            p_requestVO.setReqSelector(selector);
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "p_requestVO.getReqSelector()= " + p_requestVO.getReqSelector());
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(METHOD_NAME, "Exception e: " + e);
            throw new BTSLBaseException("USSDP2PXMLStringParser", METHOD_NAME, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }

    }

}