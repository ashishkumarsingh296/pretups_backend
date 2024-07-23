package com.btsl.pretups.gateway.util;

/*
 * @(#)XMLStringParser.java
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
 * Vibhu Trehan Jun 27, 2014 Modified
 * ------------------------------------------------------------------------------
 * -------------------
 * XML String class
 */
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.TypesI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferItemVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.iccidkeymgmt.businesslogic.PosKeyVO;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.routing.subscribermgmt.businesslogic.RoutingVO;
import com.btsl.pretups.subscriber.businesslogic.BarredUserVO;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.pretups.whitelist.businesslogic.WhiteListVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.voms.vomscommon.VOMSI;
/**
 * @author sanjeew.kumar
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class XMLStringParser {

    public static String CHNL_MESSAGE_SEP = null;
    public static String P2P_MESSAGE_SEP = null;
    public static final  Log _log = LogFactory.getLog(XMLStringParser.class.getName());
    private final static String _blank = "";
    public static String MULT_CRE_TRA_DED_ACC_SEP = null;
    // Added By Diwakar for ROBI
    // Response for request Type

    public final static String MNP_RES = "UPLOADMNPFILERESP";
    public final static String ICCID_MSISDN_MAP_RES = "ICCIDMSISDNMAPRESP";

    public final static String ADD_OPERATOR_USER_RES = "OPTUSRADDRES";// 29-08-2014

    public final static String MOD_OPERATOR_USER_RES = "OPTUSRMODRES";// 29-08-2014

    public final static String SRD_OPERATOR_USER_RES = "OPTUSRSRDRES";// 29-08-2014
	public static final String C2C_REV_RES = "C2CREVRES";
    public static final String O2C_REV_RES = "O2CREVRES";    
    public static String defaultLanguage = null;
    public static String defaultCountry = null;
    public static int minMsisdnLength = 5;
    public static int maxMsisdnLength = 10;
    public static int xmlMaxRcdSumResp = 10;
    public static String externalDateFormat = null;
    public static Boolean isChannelTransferInfoRequired = false;
    public static String p2pDefaultSmsPin = "0000";
    public static Boolean isUssdNewTagsMandatory = false;
    public static String systemDateFormat = null;
    // Ended By Diwakar for ROBI
    
    static {
    	String chnlPlainSmsSeparator = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
    	String p2pPlainSmsSeparator = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_PLAIN_SMS_SEPARATOR);
    	String multCreTraDedAccSep = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULT_CRE_TRA_DED_ACC_SEP);
        try {
            CHNL_MESSAGE_SEP = chnlPlainSmsSeparator;
            if (BTSLUtil.isNullString(CHNL_MESSAGE_SEP)) {
                CHNL_MESSAGE_SEP = " ";
            }
            P2P_MESSAGE_SEP = p2pPlainSmsSeparator;
            if (BTSLUtil.isNullString(P2P_MESSAGE_SEP)) {
                P2P_MESSAGE_SEP = " ";
            }

            MULT_CRE_TRA_DED_ACC_SEP = multCreTraDedAccSep;
            if (BTSLUtil.isNullString(MULT_CRE_TRA_DED_ACC_SEP)) {
                MULT_CRE_TRA_DED_ACC_SEP = ",";
            }
        } catch (Exception e) {
            _log.errorTrace("static", e);
        }
        defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
        minMsisdnLength = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH_CODE);
        maxMsisdnLength = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE);
        xmlMaxRcdSumResp = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.XML_MAX_RCD_SUM_RESP);
        externalDateFormat = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_DATE_FORMAT);
        isChannelTransferInfoRequired = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_TRANSFERS_INFO_REQUIRED);
        p2pDefaultSmsPin = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN);
        isUssdNewTagsMandatory = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY);
        systemDateFormat = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT);
    }

    /**
   	 * ensures no instantiation
   	 */
    private XMLStringParser(){
    	
    }
    /**
     * this method connstruct getAccountInfoResponse in XML format from
     * requestVO
     * 
     * @param p_requestVO
     *            RequestVO
     * @return responseStr java.lang.String
     */
    public static void generateGetAccountInfoResponse(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("generateGetAccountInfoResponse", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        boolean reqFromP2P = false;
        SenderVO senderVO = null;
        StringBuffer sbf = null;
        try {
            if (p_requestVO.getModule().equals(PretupsI.P2P_MODULE)) {
                senderVO = (SenderVO) p_requestVO.getSenderVO();
                reqFromP2P = true;
            }
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>CACINFRESP</TYPE>");
            if (reqFromP2P) {
                if (senderVO != null) {
                    sbf.append("<REGSTATUS>").append(senderVO.getRegistered()).append("</REGSTATUS>");
                    String pinStatus = PretupsI.NO;
                    if(PretupsI.YES.equalsIgnoreCase(senderVO.getRegistered())) {
                          pinStatus = PretupsI.YES;
                    }
                    sbf.append("<PINSTATUS>").append(pinStatus).append("</PINSTATUS>");

                } else {
                    sbf.append("<REGSTATUS>").append(_blank).append("</REGSTATUS>");
                    sbf.append("<PINSTATUS>").append(_blank).append("</PINSTATUS>");
                }
                if (p_requestVO.isSuccessTxn()) {
                    sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
                    
                } else {
                    sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
                    
                }
                Boolean isResponseInDisplayAmt = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.RESPONSE_IN_DISPLAY_AMT);
                if (senderVO != null) {
                    if (isResponseInDisplayAmt) {
                        sbf.append("<MINREMBAL>").append(PretupsBL.convertExponential((senderVO.getMinResidualBalanceAllowed()))).append("</MINREMBAL>");
                        sbf.append("<MINAMT>").append(PretupsBL.convertExponential(senderVO.getMinTxnAmountAllowed())).append("</MINAMT>");
                        sbf.append("<MAXAMT>").append(PretupsBL.convertExponential(senderVO.getMaxTxnAmountAllowed())).append("</MAXAMT>");
                    } else {
                        sbf.append("<MINREMBAL>").append(String.valueOf(PretupsBL.getSystemAmount(senderVO.getMinResidualBalanceAllowed()))).append("</MINREMBAL>");
                        sbf.append("<MINAMT>").append(String.valueOf(PretupsBL.getSystemAmount(senderVO.getMinTxnAmountAllowed()))).append("</MINAMT>");
                        sbf.append("<MAXAMT>").append(String.valueOf(PretupsBL.getSystemAmount(senderVO.getMaxTxnAmountAllowed()))).append("</MAXAMT>");
                    }
                    sbf.append("<MAXPCTBAL>").append(String.valueOf(BTSLUtil.parseDoubleToLong( senderVO.getMaxPerTransferAllowed()))).append("</MAXPCTBAL>");
                } else {
                    sbf.append("<MINREMBAL>").append(_blank).append("</MINREMBAL>");
                    sbf.append("<MINAMT>").append(_blank).append("</MINAMT>");
                    sbf.append("<MAXAMT>").append(_blank).append("</MAXAMT>");
                    sbf.append("<MAXPCTBAL>").append(_blank).append("</MAXPCTBAL>");
                }
                if (senderVO != null && !BTSLUtil.isNullString(senderVO.getSubscriptions())) {
                    // sbf.append("<SUBSCRIPTIONS>"+senderVO.getSubscriptions()+"</SUBSCRIPTIONS>");
                    sbf.append("<SUBSCRIPTIONS>").append(URLDecoder.decode(senderVO.getSubscriptions())).append("</SUBSCRIPTIONS>");
                }
                
            } else // TO DO Needs to be changed for C2S
            {
                if (senderVO != null) {
                    sbf.append("<REGSTATUS>").append(senderVO.getRegistered()).append("</REGSTATUS>");
                    String pinStatus = PretupsI.NO;
                    if(PretupsI.YES.equalsIgnoreCase(senderVO.getRegistered())) {
                          pinStatus = PretupsI.YES;
                    }
                    sbf.append("<PINSTATUS>").append(pinStatus).append("</PINSTATUS>");

                }
                if (p_requestVO.isSuccessTxn()) {
                    sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
                    
                } else {
                    sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
                    
                }
                if (senderVO != null) {
                    sbf.append("<MINREMBAL>").append(PretupsBL.convertExponential(senderVO.getMinResidualBalanceAllowed())).append("</MINREMBAL>");
                    sbf.append("<MINAMT>").append(PretupsBL.convertExponential(senderVO.getMinTxnAmountAllowed())).append("</MINAMT>");
                    sbf.append("<MAXAMT>").append(PretupsBL.convertExponential(senderVO.getMaxTxnAmountAllowed())).append("</MAXAMT>");
                    sbf.append("<MAXPCTBAL>").append(PretupsBL.convertExponential(senderVO.getMaxPerTransferAllowed())).append("</MAXPCTBAL>");
                }
                
            }
            if(senderVO!= null)
            {
            	if(senderVO.getAccountBalance() != null){
            		sbf.append("<ACCNTBAL>").append(senderVO.getAccountBalance()).append("</ACCNTBAL>");
            	}else{
            		sbf.append("<ACCNTBAL>").append(0).append("</ACCNTBAL>");
            	}
            	
            } else {
            	sbf.append("<ACCNTBAL>").append(_blank +"</ACCNTBAL>");
            }
            if(PretupsI.REQUEST_SOURCE_TYPE_EXTGW.equals(p_requestVO.getRequestGatewayType())){
            	sbf.append("<MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(),p_requestVO.getMessageArguments())).append("</MESSAGE>");	
            }
            sbf.append("</COMMAND>");
            // just workaround
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.error("generateGetAccountInfoResponse", "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateGetAccountInfoResponse]",
                PretupsErrorCodesI.P2P_ERROR_EXCEPTION, "", "", "generateGetAccountInfoResponse:" + e.getMessage());
        } finally {
        	final String senderMessage = BTSLUtil.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
            PushMessage pushMessage = null;
            pushMessage = new PushMessage(p_requestVO.getFilteredMSISDN(), senderMessage, p_requestVO.getRequestIDStr(), p_requestVO.getRequestGatewayCode(), p_requestVO
            .getLocale());
			if(p_requestVO.getMessageCode() != null){
			pushMessage.push();
			}
            if (_log.isDebugEnabled()) {
                _log.debug("generateGetAccountInfoResponse", "Exiting responseStr: " + responseStr);
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
        if (_log.isDebugEnabled()) {
            _log.debug("generateCreditTransferResponse", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>CCTRFRESP</TYPE>");
            if (BTSLUtil.isNullString(p_requestVO.getTransactionID())) {
                sbf.append("<TXNID></TXNID>");
            } else {
                sbf.append("<TXNID>").append(p_requestVO.getTransactionID()).append("</TXNID>");
            }
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
                
            } else {
            	if( p_requestVO.getMessageCode().equalsIgnoreCase(PretupsI.TXN_STATUS_UNDER_PROCESS))
            	{
            		sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            	}else{
            		sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");	
            	}

            }
            
            if(PretupsI.REQUEST_SOURCE_TYPE_EXTGW.equals(p_requestVO.getRequestGatewayType())){
            	sbf.append("<MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(),p_requestVO.getMessageArguments())).append("</MESSAGE>");	
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.error("generateCreditTransferResponse", "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateCreditTransferResponse]",
                PretupsErrorCodesI.P2P_ERROR_EXCEPTION, "", "", "generateCreditTransferResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("generateCreditTransferResponse", "Exiting responseStr: " + responseStr);
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
    public static void generateCreditRechargeResponse(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("generateCreditRechargeResponse", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>CCRCRESP</TYPE>");
            if (BTSLUtil.isNullString(p_requestVO.getTransactionID())) {
                sbf.append("<TXNID></TXNID>");
            } else {
                sbf.append("<TXNID>").append(p_requestVO.getTransactionID()).append("</TXNID>");
            }
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.error("generateCreditRechargeResponse", "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateCreditRechargeResponse]",
                PretupsErrorCodesI.P2P_ERROR_EXCEPTION, "", "", "generateCreditRechargeResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("generateCreditRechargeResponse", "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * this method construct subscriber registration response in XML format from
     * requestVO
     * 
     * @param p_requestVO
     *            RequestVO
     * @return responseStr java.lang.String
     */
    public static void generateSubscriberRegistrationResponse(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("generateSubscriberRegistrationResponse", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>REGRESP</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.error("generateSubscriberRegistrationResponse", "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "XMLStringParser[generateSubscriberRegistrationResponse]", PretupsErrorCodesI.P2P_ERROR_EXCEPTION, "", "", "generateSubscriberRegistrationResponse:" + e
                    .getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("generateSubscriberRegistrationResponse", "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * this method construct subscriber de-registration response in XML format
     * from requestVO
     * 
     * @param p_requestVO
     *            RequestVO
     * @return responseStr java.lang.String
     */
    public static void generateSubscriberDeRegistrationResponse(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("generateSubscriberDeRegistrationResponse", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>DREGRESP</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
                
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
                
            }
            if(PretupsI.REQUEST_SOURCE_TYPE_EXTGW.equals(p_requestVO.getRequestGatewayType())){
            	sbf.append("<MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(),p_requestVO.getMessageArguments())).append("</MESSAGE>");	
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.error("generateSubscriberDeRegistrationResponse", "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "XMLStringParser[generateSubscriberDeRegistrationResponse]", PretupsErrorCodesI.P2P_ERROR_EXCEPTION, "", "", "generateSubscriberDeRegistrationResponse:" + e
                    .getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("generateSubscriberDeRegistrationResponse", "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * this method connstruct credit transfer response in XML format from
     * requestVO
     * 
     * @param p_requestVO
     *            RequestVO
     * @return responseStr java.lang.String
     */
    public static void generateChangePinResponse(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("generateChangePinResponse", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>CCPNRESP</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
                
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
                
            }
            
            if(PretupsI.REQUEST_SOURCE_TYPE_EXTGW.equals(p_requestVO.getRequestGatewayType())){
            	sbf.append("<MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(),p_requestVO.getMessageArguments())).append("</MESSAGE>");	
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.error("generateChangePinResponse", "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateChangePinResponse]",
                PretupsErrorCodesI.P2P_ERROR_EXCEPTION, "", "", "generateChangePinResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("generateChangePinResponse", "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * this method connstruct registration response in XML format from requestVO
     * 
     * @param p_requestVO
     *            RequestVO
     * @return responseStr java.lang.String
     */
    public static void generateNotificationLanguageResponse(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("generateNotificationLanguageResponse", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>CCLANGRESP</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
                
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
                
            }
            if(PretupsI.REQUEST_SOURCE_TYPE_EXTGW.equals(p_requestVO.getRequestGatewayType())){
            	sbf.append("<MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(),p_requestVO.getMessageArguments())).append("</MESSAGE>");	
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.error("generateNotificationLanguageResponse", "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "XMLStringParser[generateNotificationLanguageResponse]", PretupsErrorCodesI.P2P_ERROR_EXCEPTION, "", "", "generateNotificationLanguageResponse:" + e
                    .getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("generateNotificationLanguageResponse", "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * this method connstruct registration response in XML format from requestVO
     * 
     * @param p_requestVO
     *            RequestVO
     * @return responseStr java.lang.String
     */
    public static void generateHistoryMessageResponse(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("generateHistoryMessageResponse", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>CCHISRESP</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
                
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
                
            }
            if(PretupsI.REQUEST_SOURCE_TYPE_EXTGW.equals(p_requestVO.getRequestGatewayType())){
            	sbf.append("<MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(),p_requestVO.getMessageArguments())).append("</MESSAGE>");	
            }
            	sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.error("generateHistoryMessageResponse", "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateHistoryMessageResponse]",
                PretupsErrorCodesI.P2P_ERROR_EXCEPTION, "", "", "generateHistoryMessageResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("generateHistoryMessageResponse", "Exiting responseStr: " + responseStr);
            }
        }
    }

    // This method replaces reserved characters with entities.
    protected static String fixup(String s) {
        final StringBuffer sb = new StringBuffer();
        final int len = s.length();
        for (int i = 0; i < len; i++) {
            final char c = s.charAt(i);
            switch (c) {
                default:
                    sb.append(c);
                    break;
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    sb.append("&apos;");
                    break;
            }
        }
        return sb.toString();
    }

    public static void main(String args[]) {
        final String methodName = "main";
        final RequestVO requestVO = new RequestVO();
        // requestVO.setRequestMessage("<?xml version=\"1.0\"?><COMMAND><TYPE>O2CINREQ</TYPE><EXTNWCODE><Network External Code></EXTNWCODE><MSISDN><Retailer MSISDN></MSISDN><EXTCODE><Channel user unique External code></EXTCODE><PIN>11</PIN><EXTTXNNUMBER><Unique transaction number></EXTTXNNUMBER><EXTTXNDATE><DD-MM-YYYY></EXTTXNDATE><PRODUCTS><PRODUCTCODE>101</PRODUCTCODE><QTY>Qty</QTY><PRODUCTCODE>101</PRODUCTCODE><QTY>Qty</QTY></PRODUCTS><TRFCATEGORY><SALE or FOC></TRFCATEGORY><REFNUMBER><Reference number></REFNUMBER><PAYMENTDETAILS><PAYMENTTYPE><DD></PAYMENTTYPE><PAYMENTINSTNUMBER><1234></PAYMENTINSTNUMBER><PAYMENTDATE><DD-MM-YYYY></PAYMENTDATE></PAYMENTDETAILS><REMARKS><Any free text></REMARKS></COMMAND>");
        requestVO
            .setRequestMessage("<?xml version=\"1.0\"?><COMMAND><TYPE>C2STRFANSFERENQ</TYPE><"
            		+ ">14-12-2006 12:00</DATE><EXTNWCODE>MA</EXTNWCODE><CATCODE></CATCODE><EMPCODE></EMPCODE><LOGINID>btcce</LOGINID><PASSWORD>1357</PASSWORD><EXTREFNUM>134350021</EXTREFNUM><DATA><SRVTYPE></SRVTYPE><FROMDATE>15-12-2006</FROMDATE><TODATE>20-12-2006</TODATE><TRANSACTIONID>R061216.1742.0001</TRANSACTIONID><SENDERMSISDN></SENDERMSISDN><MSISDN2></SISDN2></DATA></COMMAND>");

        try {
            parseC2STrfEnquiryRequest(requestVO);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
    }

    /**
     * This method parse common part of request from XML String to hashmap
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void parseCommonXMLRequestForCCE(RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "parseCommonXMLRequestForCCE";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        try {
            final HashMap requestHashMap = new HashMap();
            final String requestStr = p_requestVO.getRequestMessage();

            if (!(requestStr.indexOf("<COMMAND>") != -1)) {
                throw new BTSLBaseException(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
            }
            if (!(requestStr.endsWith("</COMMAND>"))) {
                throw new BTSLBaseException(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
            }

            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            requestHashMap.put("TYPE", type);
            index = requestStr.indexOf("<DATE>");
            String date = requestStr.substring(index + "<DATE>".length(), requestStr.indexOf("</DATE>", index));
            date = BTSLDateUtil.getGregorianDateInString(date);
            requestHashMap.put("DATE", date);
            index = requestStr.indexOf("<EXTNWCODE>");
            final String extNwCode = requestStr.substring(index + "<EXTNWCODE>".length(), requestStr.indexOf("</EXTNWCODE>", index));
            requestHashMap.put("EXTNWCODE", extNwCode);
            index = requestStr.indexOf("<CATCODE>");
            final String catCode = requestStr.substring(index + "<CATCODE>".length(), requestStr.indexOf("</CATCODE>", index));
            requestHashMap.put("CATCODE", catCode);
            index = requestStr.indexOf("<EMPCODE>");
            final String empCode = requestStr.substring(index + "<EMPCODE>".length(), requestStr.indexOf("</EMPCODE>", index));
            requestHashMap.put("EMPCODE", empCode);
            index = requestStr.indexOf("<LOGINID>");
            final String loginId = requestStr.substring(index + "<LOGINID>".length(), requestStr.indexOf("</LOGINID>", index));
            requestHashMap.put("LOGINID", loginId);
            index = requestStr.indexOf("<PASSWORD>");
            final String password = requestStr.substring(index + "<PASSWORD>".length(), requestStr.indexOf("</PASSWORD>", index));
            requestHashMap.put("PASSWORD", password);
            index = requestStr.indexOf("<EXTREFNUM>");
            final String extRefNumber = requestStr.substring(index + "<EXTREFNUM>".length(), requestStr.indexOf("</EXTREFNUM>", index));
            requestHashMap.put("EXTREFNUM", extRefNumber);
            index = requestStr.indexOf("<DATA>");
            final String requestData = requestStr.substring(index + "<DATA>".length(), requestStr.indexOf("</DATA>", index));
            requestHashMap.put("DATA", requestData);
            p_requestVO.setDecryptedMessage(type);
            p_requestVO.setRequestMap(requestHashMap);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[parseCommonXMLRequestForCCE]",
                PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT, "", "", "parseCommonXMLRequestForCCE:" + e.getMessage());
            throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method parse barring request from XML String to hashmap
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void parseBarChannelUserRequest(RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "parseBarChannelUserRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        try {
            parseCommonXMLRequestForCCE(p_requestVO);
            HashMap requestMap = null;
            requestMap = p_requestVO.getRequestMap();
            final String requestStr = (String) p_requestVO.getRequestMap().get("DATA");

            int index = requestStr.indexOf("<MODULE>");
            final String module = requestStr.substring(index + "<MODULE>".length(), requestStr.indexOf("</MODULE>", index));
            requestMap.put("MODULE", module);
            index = requestStr.indexOf("<MSISDN>");
            final String msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
            requestMap.put("MSISDN", msisdn);
            index = requestStr.indexOf("<USERTYPE>");
            final String userType = requestStr.substring(index + "<USERTYPE>".length(), requestStr.indexOf("</USERTYPE>", index));
            requestMap.put("USERTYPE", userType);
            index = requestStr.indexOf("<BARTYPE>");
            final String barType = requestStr.substring(index + "<BARTYPE>".length(), requestStr.indexOf("</BARTYPE>", index));
            requestMap.put("BARTYPE", barType);
            index = requestStr.indexOf("<REMARKS>");
            final String remarks = requestStr.substring(index + "<REMARKS>".length(), requestStr.indexOf("</REMARKS>", index));
            requestMap.put("REMARKS", remarks);
            p_requestVO.setRequestMap(requestMap);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
            _log.error("parseCommonXMLRequestForCCE", "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[parseBarChannelUserRequest]",
                PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT, "", "", "parseBarChannelUserRequest:" + e.getMessage());
            throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method generates barring response from hashmap to XML string
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void generateBarChannelUserResponse(RequestVO p_requestVO) {
        final String methodName = "generateBarChannelUserResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            generateCommonXMLResponseForCCE(p_requestVO);
            sbf.append(p_requestVO.getSenderReturnMessage());
            sbf.append("<DATA></DATA></COMMAND>");
            p_requestVO.setSenderReturnMessage(sbf.toString());
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateBarChannelUserResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateBarChannelUserResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method generates common response from hashmap to XML string
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void generateCommonXMLResponseForCCE(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateCommonXMLResponseForCCE";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        final java.util.Date date = new java.util.Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DDMMYYYYHHMMSS);
        sdf.setLenient(false); // this is required else it will convert
        try {
        	String EXTERNAL_DATE_FORMAT = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_DATE_FORMAT);
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
			String requestType = null;
            if (p_requestVO.getRequestMap() != null && !BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("TYPE")))
            // sbf.append("<TYPE>"+p_requestVO.getRequestMap().get("TYPE")+"RESP</TYPE>");
            // Added By Diwakar on 11-MAR-2014
            {
                requestType = (String) p_requestVO.getRequestMap().get("TYPE");
                if (requestType.equalsIgnoreCase(ParserUtility.MNP_REQ)) {
                    sbf.append("<TYPE>").append(MNP_RES).append("</TYPE>");
                } else if (requestType.equalsIgnoreCase(ParserUtility.C2C_REV_REQ)) {
                    sbf.append("<TYPE>").append(C2C_REV_RES).append("</TYPE>");
                } else if (requestType.equalsIgnoreCase(ParserUtility.O2C_REV_REQ)) {
                    sbf.append("<TYPE>").append(O2C_REV_RES).append("</TYPE>");
                }   
                else {
                    sbf.append("<TYPE>").append(p_requestVO.getRequestMap().get("TYPE") + "RESP</TYPE>");
                }
            }
            // Ended Here
            else {
                sbf.append("<TYPE>RESP</TYPE>");
            }
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsErrorCodesI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
            }

            if (p_requestVO.getRequestMap() != null && !BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("RES_ERR_KEY"))) {
                sbf.append("<ERROR_KEY>").append((String) p_requestVO.getRequestMap().get("RES_ERR_KEY")).append("</ERROR_KEY>");
            } else if (p_requestVO.getRequestMap() != null) {
                sbf.append("<ERROR_KEY></ERROR_KEY>");
            }

            sbf.append("<DATE>").append(BTSLDateUtil.getSystemLocaleDate(sdf.format(date), EXTERNAL_DATE_FORMAT)).append("</DATE>");
            if (p_requestVO.getRequestMap() != null && !BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("EXTREFNUM"))) {
                sbf.append("<EXTREFNUM>").append((String) p_requestVO.getRequestMap().get("EXTREFNUM")).append("</EXTREFNUM>");
            } else {
                sbf.append("<EXTREFNUM></EXTREFNUM>");
            }
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateCommonXMLResponseForCCE]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateCommonXMLResponseForCCE:" + e.getMessage());
            // throw new
            // BTSLBaseException("XMLStringParser","generateCommonXMLResponseForCCE",PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    public static void generateFailureResponseForCCE(RequestVO p_requestVO) {
        final String methodName = "generateFailureResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            generateCommonXMLResponseForCCE(p_requestVO);
            sbf.append(p_requestVO.getSenderReturnMessage());
            // Added By Diwakar on 11-MAR-2014
            if (p_requestVO.getRequestMap() != null && !BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("TYPE"))) {
                final String requestType = (String) p_requestVO.getRequestMap().get("TYPE");
                if (requestType.equalsIgnoreCase(ParserUtility.MNP_REQ)) {
                    sbf.substring(sbf.indexOf("<TYPE>"), sbf.indexOf("</TYPE>"));
                    sbf.append("<TYPE>").append(MNP_RES).append("</TYPE>");
                }
            }
            // Ended Here
            sbf.append("<DATA></DATA></COMMAND>");
            p_requestVO.setSenderReturnMessage(sbf.toString());
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateFailureResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateFailureResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method parse unbarring request from XML String to hashmap
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void parseUnbarChannelUserRequest(RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "parseUnbarChannelUserRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        try {
            parseCommonXMLRequestForCCE(p_requestVO);
            HashMap requestMap = null;
            requestMap = p_requestVO.getRequestMap();
            final String requestStr = (String) p_requestVO.getRequestMap().get("DATA");

            int index = requestStr.indexOf("<MODULE>");
            final String module = requestStr.substring(index + "<MODULE>".length(), requestStr.indexOf("</MODULE>", index));
            requestMap.put("MODULE", module);
            index = requestStr.indexOf("<MSISDN>");
            final String msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
            requestMap.put("MSISDN", msisdn);
            index = requestStr.indexOf("<USERTYPE>");
            final String userType = requestStr.substring(index + "<USERTYPE>".length(), requestStr.indexOf("</USERTYPE>", index));
            requestMap.put("USERTYPE", userType);
            index = requestStr.indexOf("<BARTYPE>");
            final String barType = requestStr.substring(index + "<BARTYPE>".length(), requestStr.indexOf("</BARTYPE>", index));
            requestMap.put("BARTYPE", barType);
            p_requestVO.setRequestMap(requestMap);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[parseUnbarChannelUserRequest]",
                PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT, "", "", "parseUnbarChannelUserRequest:" + e.getMessage());
            throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method generates unbarring response from hashmap to XML string
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void generateUnbarChannelUserResponse(RequestVO p_requestVO) {
        final String methodName = "generateUnbarChannelUserResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            generateCommonXMLResponseForCCE(p_requestVO);
            sbf.append(p_requestVO.getSenderReturnMessage());
            sbf.append("<DATA></DATA></COMMAND>");
            p_requestVO.setSenderReturnMessage(sbf.toString());
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateUnbarChannelUserResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateUnbarChannelUserResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method parse view barring request from XML String to hashmap
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void parseViewBarChannelUserRequest(RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "parseViewBarChannelUserRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        try {
            parseCommonXMLRequestForCCE(p_requestVO);
            HashMap requestMap = null;
            requestMap = p_requestVO.getRequestMap();
            final String requestStr = (String) p_requestVO.getRequestMap().get("DATA");

            int index = requestStr.indexOf("<MODULE>");
            final String module = requestStr.substring(index + "<MODULE>".length(), requestStr.indexOf("</MODULE>", index));
            requestMap.put("MODULE", module);
            index = requestStr.indexOf("<MSISDN>");
            final String msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
            requestMap.put("MSISDN", msisdn);
            index = requestStr.indexOf("<USERTYPE>");
            final String userType = requestStr.substring(index + "<USERTYPE>".length(), requestStr.indexOf("</USERTYPE>", index));
            requestMap.put("USERTYPE", userType);
            index = requestStr.indexOf("<BARTYPE>");
            final String barType = requestStr.substring(index + "<BARTYPE>".length(), requestStr.indexOf("</BARTYPE>", index));
            requestMap.put("BARTYPE", barType);
            index = requestStr.indexOf("<FROMDATE>");
            String fromDate = requestStr.substring(index + "<FROMDATE>".length(), requestStr.indexOf("</FROMDATE>", index));
            fromDate = BTSLDateUtil.getGregorianDateInString(fromDate);
            requestMap.put("FROMDATE", fromDate);
            index = requestStr.indexOf("<TODATE>");
            String toDate = requestStr.substring(index + "<TODATE>".length(), requestStr.indexOf("</TODATE>", index));
            toDate = BTSLDateUtil.getGregorianDateInString(toDate);
            requestMap.put("TODATE", toDate);
            p_requestVO.setRequestMap(requestMap);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[parseViewBarChannelUserRequest]",
                PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT, "", "", "parseViewBarChannelUserRequest:" + e.getMessage());
            throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method generates view barring response from hashmap to XML string
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void generateViewBarChannelUserResponse(RequestVO p_requestVO) {
        final String methodName = "generateViewBarChannelUserResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        StringBuffer sbf = null;
        try {
            BarredUserVO barredUserVO = null;
            sbf = new StringBuffer(1024);
            generateCommonXMLResponseForCCE(p_requestVO);
            sbf.append(p_requestVO.getSenderReturnMessage());
            sbf.append("<DATA>");
            if (p_requestVO.isSuccessTxn()) {
                final ArrayList arrayList = (ArrayList) p_requestVO.getValueObject();
                final int listSize = arrayList.size() > (int)xmlMaxRcdSumResp ? (int)xmlMaxRcdSumResp : arrayList.size();
                for (int i = 0; i < listSize; i++) {
                    barredUserVO = (BarredUserVO) arrayList.get(i);
                    sbf.append("<RECORD>");
                    sbf.append("<MODULE>").append(barredUserVO.getModule()).append("</MODULE>");
                    sbf.append("<MSISDN>").append(barredUserVO.getMsisdn()).append("</MSISDN>");
                    sbf.append("<NETWORK>").append(barredUserVO.getNetworkName()).append("</NETWORK>");
                    sbf.append("<NAME>").append(barredUserVO.getName()).append("</NAME>");
                    sbf.append("<USERTYPE>").append(barredUserVO.getUserType()).append("</USERTYPE>");
                    sbf.append("<BARTYPE>").append(barredUserVO.getBarredType()).append("</BARTYPE>");
                    sbf.append("<BARREDBY>").append(barredUserVO.getCreatedBy()).append("</BARREDBY>");
                    sbf.append("<BARDATE>").append(BTSLDateUtil.getSystemLocaleDate(barredUserVO.getBarredDate())).append("</BARDATE>");
                    sbf.append("<REASON>").append(barredUserVO.getBarredReason()).append("</REASON>");
                    sbf.append("</RECORD>");
                }
            }
            sbf.append("</DATA></COMMAND>");
            p_requestVO.setSenderReturnMessage(sbf.toString());
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateViewBarChannelUserResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateViewBarChannelUserResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method parse c2s enquiry request from XML String to hashmap
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void parseC2STrfEnquiryRequest(RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "parseC2STrfEnquiryRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        try {
            parseCommonXMLRequestForCCE(p_requestVO);
            HashMap requestMap = null;
            requestMap = p_requestVO.getRequestMap();
            final String requestStr = (String) p_requestVO.getRequestMap().get("DATA");

            int index = requestStr.indexOf("<SRVTYPE>");
            final String serviceType = requestStr.substring(index + "<SRVTYPE>".length(), requestStr.indexOf("</SRVTYPE>", index));
            requestMap.put("SRVTYPE", serviceType);
            index = requestStr.indexOf("<FROMDATE>");
            String fromDate = requestStr.substring(index + "<FROMDATE>".length(), requestStr.indexOf("</FROMDATE>", index));
            fromDate = BTSLDateUtil.getGregorianDateInString(fromDate);
            requestMap.put("FROMDATE", fromDate);
            index = requestStr.indexOf("<TODATE>");
            String toDate = requestStr.substring(index + "<TODATE>".length(), requestStr.indexOf("</TODATE>", index));
            toDate = BTSLDateUtil.getGregorianDateInString(toDate);
            requestMap.put("TODATE", toDate);
            index = requestStr.indexOf("<TRANSACTIONID>");
            final String txnId = requestStr.substring(index + "<TRANSACTIONID>".length(), requestStr.indexOf("</TRANSACTIONID>", index));
            requestMap.put("TRANSACTIONID", txnId);
            index = requestStr.indexOf("<SENDERMSISDN>");
            final String senderMsisdn = requestStr.substring(index + "<SENDERMSISDN>".length(), requestStr.indexOf("</SENDERMSISDN>", index));
            requestMap.put("SENDERMSISDN", senderMsisdn);
            index = requestStr.indexOf("<MSISDN2>");

            final String msisdn2 = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));
            requestMap.put("MSISDN2", msisdn2);
            p_requestVO.setRequestMap(requestMap);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[parseC2STrfEnquiryRequest]",
                PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT, "", "", "parseC2STrfEnquiryRequest:" + e.getMessage());
            throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method generates c2s enquiry response from hashmap to XML string
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void generateC2STrfEnquiryResponse(RequestVO p_requestVO) {
        final String methodName = "generateC2STrfEnquiryResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        StringBuffer sbf = null;
        try {
            HashMap hashMap = null;
            ArrayList arrayList = null;
            ArrayList list = null;
            C2STransferVO transferVO = null;
            C2STransferItemVO transferItemVO = null;

            sbf = new StringBuffer(1024);
            generateCommonXMLResponseForCCE(p_requestVO);
            sbf.append(p_requestVO.getSenderReturnMessage());

            sbf.append("<DATA>");
            if (p_requestVO.isSuccessTxn()) {
                final String resType = (String) p_requestVO.getRequestMap().get("RES_TYPE");
                sbf.append("<RECORDTYPE>").append(resType).append("</RECORDTYPE>");
                hashMap = (HashMap) p_requestVO.getValueObject();
                if (!hashMap.isEmpty()) {
                    if ("SUMMARY".equalsIgnoreCase(resType)) {
                        arrayList = (ArrayList) hashMap.get("SUMMARY");
                        final int listSize = arrayList.size() > (int)xmlMaxRcdSumResp ? (int)xmlMaxRcdSumResp : arrayList.size();
                        for (int i = 0; i < listSize; i++) {
                            transferVO = (C2STransferVO) arrayList.get(i);
                            sbf.append("<RECORD>");
                            sbf.append("<TRANSACTIONID>").append(transferVO.getTransferID()).append("</TRANSACTIONID>");
                            sbf.append("<NETWORK>").append(transferVO.getNetworkCode()).append("</NETWORK>");
                            sbf.append("<TXNDATE>").append(BTSLDateUtil.getSystemLocaleDate(transferVO.getTransferDateStr())).append("</TXNDATE>");
                            sbf.append("<SENDERMSISDN>").append(transferVO.getSenderMsisdn()).append("</SENDERMSISDN>");
                            sbf.append("<MSISDN2>").append(transferVO.getReceiverMsisdn()).append("</MSISDN2>");
                            sbf.append("<SUBSERVICE>").append(transferVO.getSubService()).append("</SUBSERVICE>");
                            // not being set in DAO, discussed with Bobba sir on
                            // 22-12-2006
                            // sbf.append("<REQVALUE>"+transferVO.getRequestedAmount()+"</REQVALUE>");
                            sbf.append("<TRFVALUE>").append(transferVO.getTransferValueStr()).append("</TRFVALUE>");
                            sbf.append("<PRODUCTCODE>").append(transferVO.getProductCode()).append("</PRODUCTCODE>");
                            sbf.append("<REQSOURCE>").append(transferVO.getSourceType()).append("</REQSOURCE>");
                            sbf.append("<STATUS>").append(transferVO.getTransferStatus()).append("</STATUS>");
                            sbf.append("<ERRORCODE>").append(transferVO.getErrorCode()).append("</ERRORCODE>");
                            if(isChannelTransferInfoRequired)
                            {
                            	sbf.append("<REMARKS>").append((transferVO.getInfo1()!=null?transferVO.getInfo1():"")).append("</REMARKS>");
                            	sbf.append("<INFO1>").append((transferVO.getInfo2()!=null?transferVO.getInfo2():"")).append("</INFO1>");
                            	sbf.append("<INFO2>").append((transferVO.getInfo3()!=null?transferVO.getInfo3():"")).append("</INFO2>");
                            }
                            sbf.append("<ERRORCODE>").append((transferVO.getErrorCode()!=null?transferVO.getErrorCode():"")).append("</ERRORCODE>");
                            sbf.append("</RECORD>");
                        }
                    } else if ("DETAILS".equalsIgnoreCase(resType)) {
                        arrayList = (ArrayList) hashMap.get("SUMMARY");
                        for (int i = 0, listSize = arrayList.size(); i < listSize; i++) {
                            transferVO = (C2STransferVO) arrayList.get(i);
                            sbf.append("<TRANSACTIONID>").append(transferVO.getTransferID()).append("</TRANSACTIONID>");
                            sbf.append("<NETWORK>").append(transferVO.getNetworkCode()).append("</NETWORK>");
                            sbf.append("<TXNDATE>").append(BTSLDateUtil.getSystemLocaleDate(transferVO.getTransferDateStr())).append("</TXNDATE>");
                            sbf.append("<SENDERMSISDN>").append(transferVO.getSenderMsisdn()).append("</SENDERMSISDN>");
                            sbf.append("<MSISDN2>").append(transferVO.getReceiverMsisdn()).append("</MSISDN2>");
                            sbf.append("<SUBSERVICE>").append(transferVO.getSubService()).append("</SUBSERVICE>");
                            // not being set in DAO
                            // sbf.append("<REQVALUE>"+transferVO.getRequestedAmount()+"</REQVALUE>");
                            sbf.append("<TRFVALUE>").append(transferVO.getTransferValueStr()).append("</TRFVALUE>");
                            sbf.append("<PRODUCTCODE>").append(transferVO.getProductCode()).append("</PRODUCTCODE>");
                            sbf.append("<SACCFEE>").append(transferVO.getSenderAccessFeeAsString()).append("</SACCFEE>");
                            sbf.append("<RACCFEE>").append(transferVO.getReceiverAccessFeeAsString()).append("</RACCFEE>");
                            sbf.append("<REQSOURCE>").append(transferVO.getSourceType()).append("</REQSOURCE>");
                            sbf.append("<STATUS>").append(transferVO.getTransferStatus()).append("</STATUS>");
							sbf.append("<ERRORCODE>").append(transferVO.getErrorCode()).append("</ERRORCODE>");
                            if(isChannelTransferInfoRequired)
                            {
                            	sbf.append("<REMARKS>").append((transferVO.getInfo1()!=null?transferVO.getInfo1():"")).append("</REMARKS>");
                            	sbf.append("<INFO1>").append((transferVO.getInfo2()!=null?transferVO.getInfo2():"")).append("</INFO1>");
                            	sbf.append("<INFO2>").append((transferVO.getInfo3()!=null?transferVO.getInfo3():"")).append("</INFO2>");
                            }
                            sbf.append("<ERRORCODE>").append((transferVO.getErrorCode()!=null?transferVO.getErrorCode():"")).append("</ERRORCODE>");
                        }
                        list = (ArrayList) hashMap.get("DETAILS");
                        for (int i = 0, listSize = list.size(); i < listSize; i++) {
                            transferItemVO = (C2STransferItemVO) list.get(i);
                            sbf.append("<RECORD>");
                            sbf.append("<MSISDN>").append(transferItemVO.getMsisdn()).append("</MSISDN>");
                            sbf.append("<USRTYPE>").append(transferItemVO.getUserType()).append("</USRTYPE>");
                            sbf.append("<ENTRYTYPE>").append(transferItemVO.getEntryType()).append("</ENTRYTYPE>");
                            sbf.append("<TRANSVALUE>").append(transferItemVO.getTransferValueStr()).append("</TRANSVALUE>");
                            sbf.append("<TRFDATE>").append(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(transferItemVO.getTransferDate()))).append("</TRFDATE>");
                            sbf.append("<PRVBAL>").append(PretupsBL.getDisplayAmount(transferItemVO.getPreviousBalance())).append("</PRVBAL>");
                            sbf.append("<POSTBAL>").append(PretupsBL.getDisplayAmount(transferItemVO.getPostBalance())).append("</POSTBAL>");
                            sbf.append("<SUBTYPE>").append((transferItemVO.getSubscriberType()==null?"":transferItemVO.getSubscriberType())).append("</SUBTYPE>");
                            sbf.append("<ENTRYDATE>").append(BTSLUtil.getDateTimeStringFromDate(transferItemVO.getEntryDateTime())).append("</ENTRYDATE>");
                            sbf.append("<SRVCLCODE>").append((transferItemVO.getServiceClassCode()!=null?transferItemVO.getServiceClassCode():"")).append("</SRVCLCODE>");
                            sbf.append("<TRFSTATUS>").append(transferItemVO.getTransferStatus()).append("</TRFSTATUS>");
                            sbf.append("<ACCSTATUS>").append((transferItemVO.getAccountStatus()!=null?transferItemVO.getAccountStatus():"")).append("</ACCSTATUS>");
                            sbf.append("<REFID>").append((transferItemVO.getReferenceID()!=null?transferItemVO.getReferenceID():"")).append("</REFID>");
                            sbf.append("</RECORD>");
                        }
                    }
                }
            }
            sbf.append("</DATA></COMMAND>");
            p_requestVO.setSenderReturnMessage(sbf.toString());
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateC2STrfEnquiryResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateC2STrfEnquiryResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method parse c2c enquiry request from XML String to hashmap
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void parseC2CTrfEnquiryRequest(RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "parseC2CTrfEnquiryRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        try {
            parseCommonXMLRequestForCCE(p_requestVO);
            HashMap requestMap = null;
            requestMap = p_requestVO.getRequestMap();
            final String requestStr = (String) p_requestVO.getRequestMap().get("DATA");

            int index = requestStr.indexOf("<MSISDN>");
            final String msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
            requestMap.put("MSISDN", msisdn);
            index = requestStr.indexOf("<TRFSTYPE>");
            final String trfType = requestStr.substring(index + "<TRFSTYPE>".length(), requestStr.indexOf("</TRFSTYPE>", index));
            requestMap.put("TRFSTYPE", trfType);
            index = requestStr.indexOf("<FROMDATE>");
            String fromDate = requestStr.substring(index + "<FROMDATE>".length(), requestStr.indexOf("</FROMDATE>", index));
            fromDate= BTSLDateUtil.getGregorianDateInString(fromDate);

            requestMap.put("FROMDATE", fromDate);
            index = requestStr.indexOf("<TODATE>");
            String toDate = requestStr.substring(index + "<TODATE>".length(), requestStr.indexOf("</TODATE>", index));
            toDate= BTSLDateUtil.getGregorianDateInString(toDate);
            requestMap.put("TODATE", toDate);
            index = requestStr.indexOf("<TRANSACTIONID>");
            final String txnId = requestStr.substring(index + "<TRANSACTIONID>".length(), requestStr.indexOf("</TRANSACTIONID>", index));
            requestMap.put("TRANSACTIONID", txnId);
            p_requestVO.setRequestMap(requestMap);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[parseC2CTrfEnquiryRequest]",
                PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT, "", "", "parseC2CTrfEnquiryRequest:" + e.getMessage());
            throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method generates c2c enquiry response from hashmap to XML string
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void generateC2CTrfEnquiryResponse(RequestVO p_requestVO) {
        final String methodName = "generateC2CTrfEnquiryResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        StringBuffer sbf = null;
        try {
            HashMap hashMap = null;
            ArrayList arrayList = null;
            ArrayList list = null;
            ChannelTransferItemsVO transferItemVO = null;
            ChannelTransferVO transferVO = null;

            sbf = new StringBuffer(1024);
            generateCommonXMLResponseForCCE(p_requestVO);
            sbf.append(p_requestVO.getSenderReturnMessage());

            sbf.append("<DATA>");
            if (p_requestVO.isSuccessTxn()) {
                hashMap = (HashMap) p_requestVO.getValueObject();
                final String resType = (String) p_requestVO.getRequestMap().get("RES_TYPE");
                sbf.append("<RECORDTYPE>").append(resType).append("</RECORDTYPE>");
                if (!hashMap.isEmpty()) {
                    if ("SUMMARY".equalsIgnoreCase(resType)) {
                        arrayList = (ArrayList) hashMap.get("SUMMARY");
                        final int listSize = arrayList.size() > (int)xmlMaxRcdSumResp ? (int)xmlMaxRcdSumResp : arrayList.size();
                        for (int i = 0; i < listSize; i++) {
                            transferVO = (ChannelTransferVO) arrayList.get(i);
                            sbf.append("<RECORD>");
                            sbf.append("<TRANSACTIONID>").append(transferVO.getTransferID()).append("</TRANSACTIONID>");
                            sbf.append("<TXNDATE>").append(BTSLDateUtil.getSystemLocaleDate(transferVO.getTransferDateAsString())).append("</TXNDATE>");
                            sbf.append("<NETWORK>").append(transferVO.getNetworkCode()).append("</NETWORK>");
                            sbf.append("<TRFSTYPE>").append(transferVO.getTransferSubType()).append("</TRFSTYPE>");
                            sbf.append("<REQVALUE>").append(PretupsBL.getDisplayAmount(transferVO.getRequestedQuantity())).append("</REQVALUE>");
                            sbf.append("<AMOUNT>").append(PretupsBL.getDisplayAmount(transferVO.getPayableAmount())).append("</AMOUNT>");
                            sbf.append("<MSISDN1>").append(transferVO.getFromUserCode()).append("</MSISDN1>");
                            sbf.append("<MSISDN2>").append(transferVO.getToUserCode()).append("</MSISDN2>");
                            sbf.append("<TRFCATEGORY>").append(transferVO.getTransferCategoryCode()).append("</TRFCATEGORY>");
                            sbf.append("<REQSOURCE>").append(transferVO.getSource()).append("</REQSOURCE>");
                            
                            if(isChannelTransferInfoRequired)
                            {
                            	sbf.append("<REMARKS>").append((transferVO.getChannelRemarks()!=null?transferVO.getChannelRemarks():"")).append("</REMARKS>");
                            	sbf.append("<INFO1>").append((transferVO.getInfo1()!=null?transferVO.getInfo1():"")).append("</INFO1>");
                            	sbf.append("<INFO2>").append((transferVO.getInfo2()!=null?transferVO.getInfo2():"")).append("</INFO2>");
                            }
                            if (PretupsI.YES.equalsIgnoreCase(transferVO.getControlTransfer())) {
                                sbf.append("<CONTROLLED>").append(PretupsI.YES).append("</CONTROLLED>");
                            } else {
                                sbf.append("<CONTROLLED>").append(PretupsI.NO).append("</CONTROLLED>");
                            }
                            sbf.append("</RECORD>");
                        }
                    } else if ("DETAILS".equalsIgnoreCase(resType)) {
                        arrayList = (ArrayList) hashMap.get("SUMMARY");
                        for (int i = 0, listSize = arrayList.size(); i < listSize; i++) {
                            transferVO = (ChannelTransferVO) arrayList.get(i);
                            sbf.append("<TRANSACTIONID>").append(transferVO.getTransferID()).append("</TRANSACTIONID>");
                            sbf.append("<TXNDATE>").append(transferVO.getTransferDateAsString()).append("</TXNDATE>");
                            sbf.append("<MSISDN1>").append(transferVO.getFromUserCode()).append("</MSISDN1>");
                            sbf.append("<MSISDN2>").append(transferVO.getToUserCode()).append("</MSISDN2>");
                            sbf.append("<USERNAME1>").append(transferVO.getFromUserName()).append("</USERNAME1>");
                            sbf.append("<USERNAME2>").append(transferVO.getToUserName()).append("</USERNAME2>");
                            sbf.append("<NETWORK>").append(transferVO.getNetworkCode()).append("</NETWORK>");
                            sbf.append("<TRFCATEGORY>").append(transferVO.getTransferCategoryCode()).append("</TRFCATEGORY>");
                            sbf.append("<TRFSTYPE>").append(transferVO.getTransferSubType()).append("</TRFSTYPE>");
                            sbf.append("<GEONAME1>").append(transferVO.getGraphicalDomainCode()).append("</GEONAME1>");
                            sbf.append("<GEONAME2>").append(transferVO.getReceiverGgraphicalDomainCode()).append("</GEONAME2>");
                            sbf.append("<DOMNAME1>").append(transferVO.getDomainCode()).append("</DOMNAME1>");
                            sbf.append("<DOMNAME2>").append(transferVO.getReceiverDomainCode()).append("</DOMNAME2>");
                            sbf.append("<CATEGORY1>").append(transferVO.getSenderCatName()).append("</CATEGORY1>");
                            sbf.append("<CATEGORY2>").append(transferVO.getReceiverCategoryDesc()).append("</CATEGORY2>");
                            sbf.append("<GRADE1>").append(transferVO.getSenderGradeCode()).append("</GRADE1>");
                            sbf.append("<GRADE2>").append(transferVO.getReceiverGradeCode()).append("</GRADE2>");
                            sbf.append("<USERID1>").append(transferVO.getFromUserID()).append("</USERID1>");
                            sbf.append("<USERID2>").append(transferVO.getToUserID()).append("</USERID2>");
                            sbf.append("<REQSOURCE>").append(transferVO.getSource()).append("</REQSOURCE>");
                            if(isChannelTransferInfoRequired)
                            {
                            	sbf.append("<REMARKS>").append((transferVO.getChannelRemarks()!=null?transferVO.getChannelRemarks():"")).append("</REMARKS>");
                            	sbf.append("<INFO1>").append((transferVO.getInfo1()!=null?transferVO.getInfo1():"")).append("</INFO1>");
                            	sbf.append("<INFO2>").append((transferVO.getInfo2()!=null?transferVO.getInfo2():"")).append("</INFO2>");
                            }
                            if (PretupsI.YES.equalsIgnoreCase(transferVO.getControlTransfer())) {
                                sbf.append("<CONTROLLED>").append(PretupsI.YES).append("</CONTROLLED>");
                            } else {
                                sbf.append("<CONTROLLED>").append(PretupsI.NO).append("</CONTROLLED>");
                            }
                        }
                        list = (ArrayList) hashMap.get("DETAILS");
                        for (int i = 0, itemslistSize = list.size(); i < itemslistSize; i++) {
                            transferItemVO = (ChannelTransferItemsVO) list.get(i);
                            sbf.append("<RECORD>");
                            sbf.append("<PRODCODE>").append(transferItemVO.getProductCode()).append("</PRODCODE>");
                            sbf.append("<REQVALUE>").append(transferItemVO.getRequestedQuantity()).append("</REQVALUE>");
                            sbf.append("<TAX1R>").append(transferItemVO.getTax1Rate()).append("</TAX1R>");
                            sbf.append("<TAX1A>").append(PretupsBL.getDisplayAmount(transferItemVO.getTax1Value())).append("</TAX1A>");
                            sbf.append("<TAX2R>").append(transferItemVO.getTax2Rate()).append("</TAX2R>");
                            sbf.append("<TAX2A>").append(PretupsBL.getDisplayAmount(transferItemVO.getTax2Value())).append("</TAX2A>");
                            sbf.append("<TAX3R>").append(transferItemVO.getTax3Rate()).append("</TAX3R>");
                            sbf.append("<TAX3A>").append(PretupsBL.getDisplayAmount(transferItemVO.getTax3Value())).append("</TAX3A>");
                            sbf.append("<COMMR>").append(transferItemVO.getCommRate()).append("</COMMR>");
                            sbf.append("<COMMA>").append(PretupsBL.getDisplayAmount(transferItemVO.getCommValue())).append("</COMMA>");
                            sbf.append("<AMOUNT>").append(PretupsBL.getDisplayAmount(transferItemVO.getPayableAmount())).append("</AMOUNT>");
                            sbf.append("<NETAMT>").append(PretupsBL.getDisplayAmount(transferItemVO.getNetPayableAmount())).append("</NETAMT>");
                            sbf.append("</RECORD>");
                        }
                    }
                }
            }
            sbf.append("</DATA></COMMAND>");
            p_requestVO.setSenderReturnMessage(sbf.toString());
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateC2CTrfEnquiryResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateC2CTrfEnquiryResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method parse o2c enquiry request from XML String to hashmap
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void parseO2CTrfEnquiryRequest(RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "parseO2CTrfEnquiryRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        try {
            parseCommonXMLRequestForCCE(p_requestVO);
            HashMap requestMap = null;
            requestMap = p_requestVO.getRequestMap();
            final String requestStr = (String) p_requestVO.getRequestMap().get("DATA");

            int index = requestStr.indexOf("<MSISDN>");
            final String msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
            requestMap.put("MSISDN", msisdn);
            index = requestStr.indexOf("<TRFCATEGORY>");
            final String trfCategory = requestStr.substring(index + "<TRFCATEGORY>".length(), requestStr.indexOf("</TRFCATEGORY>", index));
            requestMap.put("TRFCATEGORY", trfCategory);
            index = requestStr.indexOf("<FROMDATE>");
            String fromDate = requestStr.substring(index + "<FROMDATE>".length(), requestStr.indexOf("</FROMDATE>", index));
            fromDate = BTSLDateUtil.getGregorianDateInString(fromDate);
            requestMap.put("FROMDATE", fromDate);
            index = requestStr.indexOf("<TODATE>");
            String toDate = requestStr.substring(index + "<TODATE>".length(), requestStr.indexOf("</TODATE>", index));
            toDate = BTSLDateUtil.getGregorianDateInString(toDate);
            requestMap.put("TODATE", toDate);
            index = requestStr.indexOf("<TRANSACTIONID>");
            final String txnId = requestStr.substring(index + "<TRANSACTIONID>".length(), requestStr.indexOf("</TRANSACTIONID>", index));
            requestMap.put("TRANSACTIONID", txnId);
            p_requestVO.setRequestMap(requestMap);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[parseO2CTrfEnquiryRequest]",
                PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT, "", "", "parseO2CTrfEnquiryRequest:" + e.getMessage());
            throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method generates o2c enquiry response from hashmap to XML string
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void generateO2CTrfEnquiryResponse(RequestVO p_requestVO) {
        final String methodName = "generateO2CTrfEnquiryResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        StringBuffer sbf = null;
        try {
            HashMap hashMap = null;
            ArrayList arrayList = null;
            ArrayList list = null;
            ChannelTransferVO transferVO = null;
            ChannelTransferItemsVO transferItemVO = null;

            sbf = new StringBuffer(1024);
            generateCommonXMLResponseForCCE(p_requestVO);
            sbf.append(p_requestVO.getSenderReturnMessage());
            sbf.append("<DATA>");
            if (p_requestVO.isSuccessTxn()) {
                hashMap = (HashMap) p_requestVO.getValueObject();
                final String resType = (String) p_requestVO.getRequestMap().get("RES_TYPE");
                sbf.append("<RECORDTYPE>").append(resType).append("</RECORDTYPE>");
                if (!hashMap.isEmpty()) {
                    if ("SUMMARY".equalsIgnoreCase(resType)) {
                        arrayList = (ArrayList) hashMap.get("SUMMARY");
                        final int listSize = arrayList.size() > (int)xmlMaxRcdSumResp ? (int)xmlMaxRcdSumResp : arrayList.size();
                        for (int i = 0; i < listSize; i++) {
                            transferVO = (ChannelTransferVO) arrayList.get(i);
                            sbf.append("<RECORD>");
                            sbf.append("<TRANSACTIONID>").append(transferVO.getTransferID()).append("</TRANSACTIONID>");
                            sbf.append("<TXNDATE>").append(BTSLDateUtil.getSystemLocaleDate(transferVO.getTransferDateAsString())).append("</TXNDATE>");
                            sbf.append("<NETWORK>").append(transferVO.getNetworkCode()).append("</NETWORK>");
                            sbf.append("<MSISDN>").append(transferVO.getUserMsisdn()).append("</MSISDN>");
                            sbf.append("<TRFCATEGORY>").append(transferVO.getTransferCategory()).append("</TRFCATEGORY>");
                            sbf.append("<APPROVEDBY>").append(transferVO.getFinalApprovedBy()).append("</APPROVEDBY>");
                            sbf.append("<APPROVEDON>").append(transferVO.getFinalApprovedDateAsString()).append("</APPROVEDON>");
                            sbf.append("<TRFTYPE>").append(transferVO.getTransferType()).append("</TRFTYPE>");
                            sbf.append("<REQVALUE>").append(PretupsBL.getDisplayAmount(transferVO.getRequestedQuantity())).append("</REQVALUE>");
                            sbf.append("<AMOUNT>").append(PretupsBL.getDisplayAmount(transferVO.getPayableAmount())).append("</AMOUNT>");
                            sbf.append("<STATUS>").append(transferVO.getStatus()).append("</STATUS>");
                            if(isChannelTransferInfoRequired)
                            {
                            	sbf.append("<REMARKS>").append((transferVO.getChannelRemarks()!=null?transferVO.getChannelRemarks():"")).append("</REMARKS>");
                            	sbf.append("<INFO1>").append((transferVO.getInfo1()!=null?transferVO.getInfo1():"")).append("</INFO1>");
                            	sbf.append("<INFO2>").append((transferVO.getInfo2()!=null?transferVO.getInfo2():"")).append("</INFO2>");
                            }
                            sbf.append("</RECORD>");
                        }
                    } else if ("DETAILS".equalsIgnoreCase(resType)) {
                        arrayList = (ArrayList) hashMap.get("SUMMARY");
                        for (int i = 0, listSize = arrayList.size(); i < listSize; i++) {
                            transferVO = (ChannelTransferVO) arrayList.get(i);
                            sbf.append("<TRANSACTIONID>").append(transferVO.getTransferID()).append("</TRANSACTIONID>");
                            sbf.append("<TXNDATE>").append(BTSLDateUtil.getSystemLocaleDate(transferVO.getTransferDateAsString())).append("</TXNDATE>");
                            sbf.append("<NETWORK>").append(transferVO.getNetworkCode()).append("</NETWORK>");
                            sbf.append("<DOMNAME>").append(transferVO.getDomainCode()).append("</DOMNAME>");
                            sbf.append("<CATEGORY>").append((transferVO.getCategoryCode()!=null?transferVO.getCategoryCode():"")).append("</CATEGORY>");
                            sbf.append("<GEONAME>").append(transferVO.getGraphicalDomainCode()).append("</GEONAME>");
                            sbf.append("<TRFCATEGORY>").append(transferVO.getTransferCategory()).append("</TRFCATEGORY>");
                            sbf.append("<TRFTYPE>").append(transferVO.getTransferType()).append("</TRFTYPE>");
                            sbf.append("<MSISDN>").append(transferVO.getUserMsisdn()).append("</MSISDN>");
                            sbf.append("<EXTTXNNUMBER>").append(transferVO.getExternalTxnNum()).append("</EXTTXNNUMBER>");
                            sbf.append("<EXTTXNDATE>").append(BTSLDateUtil.getSystemLocaleDate(transferVO.getExternalTxnDateAsString())).append("</EXTTXNDATE>");
                            if(isChannelTransferInfoRequired)
                            {
                            	sbf.append("<REMARKS>").append((transferVO.getChannelRemarks()!=null?transferVO.getChannelRemarks():"")).append("</REMARKS>");
                            	sbf.append("<INFO1>").append((transferVO.getInfo1()!=null?transferVO.getInfo1():"")).append("</INFO1>");
                            	sbf.append("<INFO2>").append((transferVO.getInfo2()!=null?transferVO.getInfo2():"")).append("</INFO2>");
                            }
                            sbf.append("<COMMPRF>").append((transferVO.getCommProfileName()!=null?transferVO.getCommProfileName():"")).append("</COMMPRF>");
                            sbf.append("<STATUS>").append(transferVO.getStatus()).append("</STATUS>");
                            sbf.append("<PAYMENTINSTTYPE>").append(transferVO.getPayInstrumentType()).append("</PAYMENTINSTTYPE>");
                            sbf.append("<PAYMENTINSTNUMBER>").append(transferVO.getPayInstrumentNum()).append("</PAYMENTINSTNUMBER>");
                            sbf.append("<PAYMENTINSTDATE>").append(BTSLDateUtil.getLocaleDateTimeFromDate((transferVO.getPayInstrumentDate()))).append("</PAYMENTINSTDATE>");
                            sbf.append("<PAYMENTINSTAMT>").append(PretupsBL.getDisplayAmount(transferVO.getPayInstrumentAmt())).append("</PAYMENTINSTAMT>");
                            sbf.append("<FIRSTAPPREMARKS>").append((transferVO.getFirstApprovalRemark()!=null?transferVO.getFirstApprovalRemark():"")).append("</FIRSTAPPREMARKS>");
                            sbf.append("<SECONDAPPREMARKS>").append((transferVO.getSecondApprovalRemark()!=null?transferVO.getSecondApprovalRemark():"")).append("</SECONDAPPREMARKS>");
                            sbf.append("<THIRDAPPREMARKS>").append((transferVO.getThirdApprovalRemark()!=null?transferVO.getThirdApprovalRemark():"")).append("</THIRDAPPREMARKS>");
                            sbf.append("<REQSOURCE>").append(transferVO.getSource()).append("</REQSOURCE>");
                        }
                        list = (ArrayList) hashMap.get("DETAILS");
                        for (int i = 0, itemsListSize = list.size(); i < itemsListSize; i++) {
                            transferItemVO = (ChannelTransferItemsVO) list.get(i);
                            sbf.append("<RECORD>");
                            sbf.append("<PRODCODE>").append(transferItemVO.getProductCode()).append("</PRODCODE>");
                            sbf.append("<MRP>").append(transferItemVO.getProductMrpStr()).append("</MRP>");
                            sbf.append("<REQVALUE>").append(PretupsBL.getDisplayAmount(Double.parseDouble(transferItemVO.getRequestedQuantity()))).append("</REQVALUE>");
                            sbf.append("<TAX1R>").append(transferItemVO.getTax1Rate()).append("</TAX1R>");
                            sbf.append("<TAX1A>").append(PretupsBL.getDisplayAmount(transferItemVO.getTax1Value())).append("</TAX1A>");
                            sbf.append("<TAX2R>").append(transferItemVO.getTax2Rate()).append("</TAX2R>");
                            sbf.append("<TAX2A>").append(PretupsBL.getDisplayAmount(transferItemVO.getTax2Value())).append("</TAX2A>");
                            sbf.append("<TAX3R>").append(transferItemVO.getTax3Rate()).append("</TAX3R>");
                            sbf.append("<TAX3A>").append(PretupsBL.getDisplayAmount(transferItemVO.getTax3Value())).append("</TAX3A>");
                            sbf.append("<COMMR>").append(transferItemVO.getCommRate()).append("</COMMR>");
                            sbf.append("<COMMA>").append(PretupsBL.getDisplayAmount(transferItemVO.getCommValue())).append("</COMMA>");
                            sbf.append("<AMOUNT>").append(PretupsBL.getDisplayAmount(transferItemVO.getPayableAmount())).append("</AMOUNT>");
                            sbf.append("<NETAMT>").append(PretupsBL.getDisplayAmount(transferItemVO.getNetPayableAmount())).append("</NETAMT>");
                            sbf.append("</RECORD>");
                        }
                    }
                }
            }
            sbf.append("</DATA></COMMAND>");
            p_requestVO.setSenderReturnMessage(sbf.toString());
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateO2CTrfEnquiryResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateO2CTrfEnquiryResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method parse p2p enquiry request from XML String to hashmap
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void parseP2PTrfEnquiryRequest(RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "parseP2PTrfEnquiryRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        try {
            parseCommonXMLRequestForCCE(p_requestVO);
            HashMap requestMap = null;
            requestMap = p_requestVO.getRequestMap();
            final String requestStr = (String) p_requestVO.getRequestMap().get("DATA");
            int index = requestStr.indexOf("<SENDERMSISDN>");
            final String senderMsisdn = requestStr.substring(index + "<SENDERMSISDN>".length(), requestStr.indexOf("</SENDERMSISDN>", index));
            requestMap.put("SENDERMSISDN", senderMsisdn);
            index = requestStr.indexOf("<FROMDATE>");
            String fromDate = requestStr.substring(index + "<FROMDATE>".length(), requestStr.indexOf("</FROMDATE>", index));
            fromDate= BTSLDateUtil.getGregorianDateInString(fromDate);

            requestMap.put("FROMDATE", fromDate);
            index = requestStr.indexOf("<TODATE>");
            String toDate = requestStr.substring(index + "<TODATE>".length(), requestStr.indexOf("</TODATE>", index));
            toDate= BTSLDateUtil.getGregorianDateInString(toDate);
            requestMap.put("TODATE", toDate);
            index = requestStr.indexOf("<TRANSACTIONID>");
            final String txnId = requestStr.substring(index + "<TRANSACTIONID>".length(), requestStr.indexOf("</TRANSACTIONID>", index));
            requestMap.put("TRANSACTIONID", txnId);
            p_requestVO.setRequestMap(requestMap);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[parseP2PTrfEnquiryRequest]",
                PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT, "", "", "parseP2PTrfEnquiryRequest:" + e.getMessage());
            throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method generates P2P enquiry response from hashmap to XML string
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void generateP2PTrfEnquiryResponse(RequestVO p_requestVO) {
        final String methodName = "generateP2PTrfEnquiryResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        StringBuffer sbf = null;
        try {
            HashMap hashMap = null;
            ArrayList arrayList = null;
            ArrayList list = null;
            TransferVO transferVO = null;
            TransferItemVO transferItemVO = null;

            sbf = new StringBuffer(1024);
            generateCommonXMLResponseForCCE(p_requestVO);
            sbf.append(p_requestVO.getSenderReturnMessage());

            sbf.append("<DATA>");
            if (p_requestVO.isSuccessTxn()) {
                hashMap = (HashMap) p_requestVO.getValueObject();
                final String resType = (String) p_requestVO.getRequestMap().get("RES_TYPE");
                sbf.append("<RECORDTYPE>").append(resType).append("</RECORDTYPE>");
                if (!hashMap.isEmpty()) {
                    if ("SUMMARY".equalsIgnoreCase(resType)) {
                        arrayList = (ArrayList) hashMap.get("SUMMARY");
                        final int listSize = arrayList.size() > (int)xmlMaxRcdSumResp ? (int)xmlMaxRcdSumResp : arrayList.size();
                        for (int i = 0; i < listSize; i++) {
                            transferVO = (TransferVO) arrayList.get(i);
                            sbf.append("<RECORD>");
                            sbf.append("<TRANSACTIONID>").append(transferVO.getTransferID()).append("</TRANSACTIONID>");
                            sbf.append("<TXNDATE>").append(BTSLDateUtil.getLocaleDateTimeFromDate(transferVO.getTransferDateTime())).append("</TXNDATE>");
                            sbf.append("<PRODUCTCODE>").append(transferVO.getProductName()).append("</PRODUCTCODE>");
                            sbf.append("<SENDERMSISDN>").append(p_requestVO.getFilteredMSISDN()).append("</SENDERMSISDN>");
                            sbf.append("<MSISDN2>").append(((ReceiverVO) transferVO.getReceiverVO()).getMsisdn()).append("</MSISDN2>");
                            sbf.append("<REQVALUE>").append(transferVO.getQuantity()).append("</REQVALUE>");
                            sbf.append("<TRFVALUE>").append(transferVO.getTransferValueStr()).append("</TRFVALUE>");
                            sbf.append("<STATUS>").append(transferVO.getTransferStatus()).append("</STATUS>");
                            sbf.append("<ERRCODE>").append(transferVO.getErrorCode()).append("</ERRCODE>");
                            sbf.append("</RECORD>");
                        }
                    } else if ("DETAILS".equalsIgnoreCase(resType)) {
                        arrayList = (ArrayList) hashMap.get("SUMMARY");
                        for (int i = 0, listSize = arrayList.size(); i < listSize; i++) {
                            transferVO = (TransferVO) arrayList.get(i);
                            sbf.append("<TRANSACTIONID>").append(transferVO.getTransferID()).append("</TRANSACTIONID>");
                            sbf.append("<TXNDATE>").append(transferVO.getTransferDisplayDateTime()).append("</TXNDATE>");
                            sbf.append("<SENDERMSISDN>").append(transferVO.getSenderMsisdn()).append("</SENDERMSISDN>");
                            sbf.append("<MSISDN2>").append(transferVO.getReceiverMsisdn()).append("</MSISDN2>");
                            sbf.append("<PRODCODE>").append(transferVO.getProductName()).append("</PRODCODE>");
                            sbf.append("<TXNCATEGORY>").append(transferVO.getTransferCategory()).append("</TXNCATEGORY>");
                            sbf.append("<STATUS>").append(transferVO.getTransferStatus()).append("</STATUS>");
                            sbf.append("<ERRCODE>").append(transferVO.getErrorCode()).append("</ERRCODE>");
                            sbf.append("<REQVALUE>").append(PretupsBL.getDisplayAmount(transferVO.getRequestedAmount())).append("</REQVALUE>");
                            sbf.append("<CARDGRPCODE>").append(transferVO.getCardGroupSetName()).append("</CARDGRPCODE>");
                            sbf.append("<SRVTYPE>").append(transferVO.getServiceType()).append("</SRVTYPE>");
                            sbf.append("<STAX1R>").append(transferVO.getSenderTax1Rate()).append("</STAX1R>");
                            sbf.append("<STAX1V>").append(PretupsBL.getDisplayAmount(transferVO.getSenderTax1Value())).append("</STAX1V>");
                            sbf.append("<STAX2R>").append(transferVO.getSenderTax2Rate()).append("</STAX2R>");
                            sbf.append("<STAX2V>").append(PretupsBL.getDisplayAmount(transferVO.getSenderTax2Value())).append("</STAX2V>");
                            sbf.append("<SACCFEE>").append(PretupsBL.getDisplayAmount(transferVO.getSenderAccessFee())).append("</SACCFEE>");
                            sbf.append("<RTAX1R>").append(transferVO.getReceiverTax1Rate()).append("</RTAX1R>");
                            sbf.append("<RTAX1V>").append(PretupsBL.getDisplayAmount(transferVO.getReceiverTax1Value())).append("</RTAX1V>");
                            sbf.append("<RTAX2R>").append(transferVO.getReceiverTax2Rate()).append("</RTAX2R>");
                            sbf.append("<RTAX2V>").append(PretupsBL.getDisplayAmount(transferVO.getReceiverTax2Value())).append("</RTAX2V>");
                            sbf.append("<RACCFEE>").append(PretupsBL.getDisplayAmount(transferVO.getReceiverAccessFee())).append("</RACCFEE>");
                            sbf.append("<RVALIDITY>").append(transferVO.getReceiverValidity()).append("</RVALIDITY>");
                            sbf.append("<RGRACE>").append(transferVO.getReceiverGracePeriod()).append("</RGRACE>");
                            sbf.append("<RBONUSVALUE>").append(PretupsBL.getDisplayAmount(transferVO.getReceiverBonusValue())).append("</RBONUSVALUE>");
                            sbf.append("<RBONUSVALIDITY>").append(transferVO.getReceiverBonusValidity()).append("</RBONUSVALIDITY>");
                            sbf.append("<PRVEXPIRY>").append(transferVO.getMsisdnPreviousExpiryStr()).append("</PRVEXPIRY>");
                            sbf.append("<POSTEXPIRY>").append(transferVO.getMsisdnNewExpiryStr()).append("</POSTEXPIRY>");
                        }
                        list = (ArrayList) hashMap.get("DETAILS");
                        for (int i = 0, itemsListSize = list.size(); i < itemsListSize; i++) {
                            transferItemVO = (TransferItemVO) list.get(i);
                            sbf.append("<RECORD>");
                            sbf.append("<SUBTYPE>").append(transferItemVO.getSubscriberType()).append("</SUBTYPE>");
                            sbf.append("<SRVCLCODE>").append(transferItemVO.getServiceClassCode()).append("</SRVCLCODE>");
                            sbf.append("<TXNTYPE>").append(transferItemVO.getTransferType()).append("</TXNTYPE>");
                            sbf.append("<ENTRYTYPE>").append(transferItemVO.getEntryType()).append("</ENTRYTYPE>");
                            sbf.append("<ENTRYDATE>").append(BTSLDateUtil.getLocaleTimeStamp(transferItemVO.getEntryDisplayDateTime())).append("</ENTRYDATE>");
                            sbf.append("<TRFVALUE>").append(transferItemVO.getTransferValue()).append("</TRFVALUE>");
                            sbf.append("<INTID>").append(transferItemVO.getInterfaceID()).append("</INTID>");
                            sbf.append("<INTREFID>").append(transferItemVO.getInterfaceReferenceID()).append("</INTREFID>");
                            sbf.append("<PRVBAL>").append(PretupsBL.getDisplayAmount(transferItemVO.getPreviousBalance())).append("</PRVBAL>");
                            sbf.append("<POSTBAL>").append(PretupsBL.getDisplayAmount(transferItemVO.getPostBalance())).append("</POSTBAL>");
                            sbf.append("<TRFSTATUS>").append(transferItemVO.getTransferStatus()).append("</TRFSTATUS>");
                            sbf.append("<ACCSTATUS>").append(transferItemVO.getAccountStatus()).append("</ACCSTATUS>");
                            sbf.append("<REFID>").append(transferItemVO.getReferenceID()).append("</REFID>");
                            sbf.append("</RECORD>");
                        }
                    }
                }
            }
            sbf.append("</DATA></COMMAND>");
            p_requestVO.setSenderReturnMessage(sbf.toString());
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateP2PTrfEnquiryResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateP2PTrfEnquiryResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method parse p2p receiver enquiry request from XML String to hashmap
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void parseP2PReceiverTrfEnquiryRequest(RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "parseP2PReceiverTrfEnquiryRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        try {
            parseCommonXMLRequestForCCE(p_requestVO);
            HashMap requestMap = null;
            requestMap = p_requestVO.getRequestMap();
            final String requestStr = (String) p_requestVO.getRequestMap().get("DATA");
            int index = requestStr.indexOf("<MSISDN2>");
            final String msisdn2 = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));
            requestMap.put("MSISDN2", msisdn2);
            index = requestStr.indexOf("<FROMDATE>");
            String fromDate = requestStr.substring(index + "<FROMDATE>".length(), requestStr.indexOf("</FROMDATE>", index));
            fromDate= BTSLDateUtil.getGregorianDateInString(fromDate);

            requestMap.put("FROMDATE", fromDate);
            index = requestStr.indexOf("<TODATE>");
            String toDate = requestStr.substring(index + "<TODATE>".length(), requestStr.indexOf("</TODATE>", index));
            toDate= BTSLDateUtil.getGregorianDateInString(toDate);
            requestMap.put("TODATE", toDate);
            index = requestStr.indexOf("<TRANSACTIONID>");
            final String txnId = requestStr.substring(index + "<TRANSACTIONID>".length(), requestStr.indexOf("</TRANSACTIONID>", index));
            requestMap.put("TRANSACTIONID", txnId);
            p_requestVO.setRequestMap(requestMap);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[parseP2PReceiverTrfEnquiryRequest]",
                PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT, "", "", "parseP2PReceiverTrfEnquiryRequest:" + e.getMessage());
            throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method generates P2P Receiver enquiry response from hashmap to XML
     * string
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void generateP2PRecTrfEnquiryResponse(RequestVO p_requestVO) {
        final String methodName = "generateP2PRecTrfEnquiryResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        StringBuffer sbf = null;
        try {
            HashMap hashMap = null;
            ArrayList arrayList = null;
            ArrayList list = null;
            TransferVO transferVO = null;
            TransferItemVO transferItemVO = null;

            sbf = new StringBuffer(1024);
            generateCommonXMLResponseForCCE(p_requestVO);
            sbf.append(p_requestVO.getSenderReturnMessage());

            sbf.append("<DATA>");
            if (p_requestVO.isSuccessTxn()) {
                hashMap = (HashMap) p_requestVO.getValueObject();
                final String resType = (String) p_requestVO.getRequestMap().get("RES_TYPE");
                sbf.append("<RECORDTYPE>").append(resType).append("</RECORDTYPE>");
                if (!hashMap.isEmpty()) {
                    if ("SUMMARY".equalsIgnoreCase(resType)) {
                        arrayList = (ArrayList) hashMap.get("SUMMARY");
                        final int listSize = arrayList.size() > (int)xmlMaxRcdSumResp ? (int)xmlMaxRcdSumResp : arrayList.size();
                        for (int i = 0; i < listSize; i++) {
                            transferVO = (TransferVO) arrayList.get(i);
                            sbf.append("<RECORD>");
                            sbf.append("<TRANSACTIONID>").append(transferVO.getTransferID()).append("</TRANSACTIONID>");
                            sbf.append("<TXNDATE>").append(transferVO.getTransferDateTime()).append("</TXNDATE>");
                            sbf.append("<PRODUCTCODE>").append(transferVO.getProductCode()).append("</PRODUCTCODE>");
                            /********* Modified **************/
                            sbf.append("<SENDERMSISDN>").append(((SenderVO) transferVO.getSenderVO()).getMsisdn()).append("</SENDERMSISDN>");
                            sbf.append("<MSISDN2>").append(((ReceiverVO) transferVO.getReceiverVO()).getMsisdn()).append("</MSISDN2>");

                            sbf.append("<REQVALUE>").append(transferVO.getQuantity()).append("</REQVALUE>");
                            sbf.append("<TRFVALUE>").append(transferVO.getTransferValueStr()).append("</TRFVALUE>");
                            sbf.append("<STATUS>").append(transferVO.getTransferStatus()).append("</STATUS>");
                            sbf.append("<ERRCODE>").append(transferVO.getErrorCode()).append("</ERRCODE>");
                            sbf.append("</RECORD>");
                        }
                    } else if ("DETAILS".equalsIgnoreCase(resType)) {
                        arrayList = (ArrayList) hashMap.get("SUMMARY");
                        for (int i = 0, listSize = arrayList.size(); i < listSize; i++) {
                            transferVO = (TransferVO) arrayList.get(i);
                            sbf.append("<TRANSACTIONID>").append(transferVO.getTransferID()).append("</TRANSACTIONID>");
                            sbf.append("<TXNDATE>").append(transferVO.getTransferDateStr()).append("</TXNDATE>");
                            /********* Modified **************/
                            sbf.append("<SENDERMSISDN>").append(((SenderVO) transferVO.getSenderVO()).getMsisdn()).append("</SENDERMSISDN>");
                            sbf.append("<MSISDN2>").append(((ReceiverVO) transferVO.getReceiverVO()).getMsisdn()).append("</MSISDN2>");

                            sbf.append("<PRODCODE>").append(transferVO.getProductShortCode()).append("</PRODCODE>");
                            sbf.append("<TXNCATEGORY>").append(transferVO.getTransferCategory()).append("</TXNCATEGORY>");
                            sbf.append("<STATUS>").append(transferVO.getTransferStatus()).append("</STATUS>");
                            sbf.append("<ERRCODE>").append(transferVO.getErrorCode()).append("</ERRCODE>");
                            sbf.append("<REQVALUE>").append(PretupsBL.getDisplayAmount(transferVO.getRequestedAmount())).append("</REQVALUE>");
                            sbf.append("<CARDGRPCODE>").append(transferVO.getCardGroupCode()).append("</CARDGRPCODE>");
                            sbf.append("<SRVTYPE>").append(transferVO.getServiceType()).append("</SRVTYPE>");
                            sbf.append("<STAX1R>").append(transferVO.getSenderTax1Rate()).append("</STAX1R>");
                            sbf.append("<STAX1V>").append(PretupsBL.getDisplayAmount(transferVO.getSenderTax1Value())).append("</STAX1V>");
                            sbf.append("<STAX2R>").append(transferVO.getSenderTax2Rate()).append("</STAX2R>");
                            sbf.append("<STAX2V>").append(PretupsBL.getDisplayAmount(transferVO.getSenderTax2Value())).append("</STAX2V>");
                            sbf.append("<SACCFEE>").append(PretupsBL.getDisplayAmount(transferVO.getSenderAccessFee())).append("</SACCFEE>");
                            sbf.append("<RTAX1R>").append(transferVO.getReceiverTax1Rate()).append("</RTAX1R>");
                            sbf.append("<RTAX1V>").append(PretupsBL.getDisplayAmount(transferVO.getReceiverTax1Value())).append("</RTAX1V>");
                            sbf.append("<RTAX2R>").append(transferVO.getReceiverTax2Rate()).append("</RTAX2R>");
                            sbf.append("<RTAX2V>").append(PretupsBL.getDisplayAmount(transferVO.getReceiverTax2Value())).append("</RTAX2V>");
                            sbf.append("<RACCFEE>").append(PretupsBL.getDisplayAmount(transferVO.getReceiverAccessFee())).append("</RACCFEE>");
                            sbf.append("<RVALIDITY>").append(transferVO.getReceiverValidity()).append("</RVALIDITY>");
                            sbf.append("<RGRACE>").append(transferVO.getReceiverGracePeriod()).append("</RGRACE>");
                            sbf.append("<RBONUSVALUE>").append(PretupsBL.getDisplayAmount(transferVO.getReceiverBonusValue())).append("</RBONUSVALUE>");
                            sbf.append("<RBONUSVALIDITY>").append(transferVO.getReceiverBonusValidity()).append("</RBONUSVALIDITY>");
                            sbf.append("<REQSOURCE>").append(transferVO.getSourceType()).append("</REQSOURCE>");
                        }
                        list = (ArrayList) hashMap.get("DETAILS");
                        for (int i = 0, itemsListSize = list.size(); i < itemsListSize; i++) {
                            transferItemVO = (TransferItemVO) list.get(i);
                            sbf.append("<RECORD>");
                            sbf.append("<SUBTYPE>").append(transferItemVO.getSubscriberType()).append("</SUBTYPE>");
                            sbf.append("<SRVCLCODE>").append(transferItemVO.getServiceClassCode()).append("</SRVCLCODE>");
                            sbf.append("<TXNTYPE>").append(transferItemVO.getTransferType()).append("</TXNTYPE>");
                            sbf.append("<ENTRYTYPE>").append(transferItemVO.getEntryType()).append("</ENTRYTYPE>");
                            sbf.append("<TRFDATE>").append(BTSLDateUtil.getLocaleDateTimeFromDate(transferItemVO.getTransferDate())).append("</TRFDATE>");
                            sbf.append("<TRFVALUE>").append(transferItemVO.getTransferValue()).append("</TRFVALUE>");
                            sbf.append("<INTID>").append(transferItemVO.getInterfaceID()).append("</INTID>");
                            sbf.append("<INTREFID>").append(transferItemVO.getInterfaceReferenceID()).append("</INTREFID>");
                            sbf.append("<PRVBAL>").append(PretupsBL.getDisplayAmount(transferItemVO.getPreviousBalance())).append("</PRVBAL>");
                            sbf.append("<POSTBAL>").append(PretupsBL.getDisplayAmount(transferItemVO.getPostBalance())).append("</POSTBAL>");
                            sbf.append("<PRVEXPIRY>").append(transferItemVO.getPreviousExpiry()).append("</PRVEXPIRY>");
                            sbf.append("<POSTEXPIRY>").append(transferItemVO.getNewExpiry()).append("</POSTEXPIRY>");
                            sbf.append("<TRFSTATUS>").append(transferItemVO.getTransferStatus()).append("</TRFSTATUS>");
                            sbf.append("<ACCSTATUS>").append(transferItemVO.getAccountStatus()).append("</ACCSTATUS>");
                            sbf.append("<REFID>").append(transferItemVO.getReferenceID()).append("</REFID>");
                            sbf.append("</RECORD>");
                        }
                    }
                }
            }
            sbf.append("</DATA></COMMAND>");
            p_requestVO.setSenderReturnMessage(sbf.toString());
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateP2PRecTrfEnquiryResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateP2PRecTrfEnquiryResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method parse change language request from XML String to hashmap
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void parseChangeLanguageRequest(RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "parseChangeLanguageRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        try {
            parseCommonXMLRequestForCCE(p_requestVO);
            HashMap requestMap = null;
            requestMap = p_requestVO.getRequestMap();
            final String requestStr = (String) p_requestVO.getRequestMap().get("DATA");
            int index = requestStr.indexOf("<MODULE>");
            final String module = requestStr.substring(index + "<MODULE>".length(), requestStr.indexOf("</MODULE>", index));
            requestMap.put("MODULE", module);
            index = requestStr.indexOf("<MSISDN>");
            final String msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
            requestMap.put("MSISDN", msisdn);
            index = requestStr.indexOf("<LANGCODE>");
            final String langCode = requestStr.substring(index + "<LANGCODE>".length(), requestStr.indexOf("</LANGCODE>", index));
            requestMap.put("LANGCODE", langCode);
            p_requestVO.setRequestMap(requestMap);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[parseChangeLanguageRequest]",
                PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT, "", "", "parseChangeLanguageRequest:" + e.getMessage());
            throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method generates change language response from hashmap to XML string
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void generateChangeLanguageResponse(RequestVO p_requestVO) {
        final String methodName = "generateChangeLanguageResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            generateCommonXMLResponseForCCE(p_requestVO);
            sbf.append(p_requestVO.getSenderReturnMessage());
            sbf.append("<DATA></DATA></COMMAND>");
            p_requestVO.setSenderReturnMessage(sbf.toString());
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateChangeLanguageResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateChangeLanguageResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method parse iccid msisdn enquiry request from XML String to hashmap
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void parseIccidMsisdnEnquiryRequest(RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "parseIccidMsisdnEnquiryRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        try {
            parseCommonXMLRequestForCCE(p_requestVO);
            HashMap requestMap = null;
            requestMap = p_requestVO.getRequestMap();
            final String requestStr = (String) p_requestVO.getRequestMap().get("DATA");
            int index = requestStr.indexOf("<ICCID>");
            final String iccid = requestStr.substring(index + "<ICCID>".length(), requestStr.indexOf("</ICCID>", index));
            requestMap.put("ICCID", iccid);
            index = requestStr.indexOf("<MSISDN>");
            final String msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
            requestMap.put("MSISDN", msisdn);
            p_requestVO.setRequestMap(requestMap);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[parseIccidMsisdnEnquiryRequest]",
                PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT, "", "", "parseIccidMsisdnEnquiryRequest:" + e.getMessage());
            throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method generates issid msisdn enquiry response from hashmap to XML
     * string
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void generateIccidMsisdnEnquiryResponse(RequestVO p_requestVO) {
        final String methodName = "generateIccidMsisdnEnquiryResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            PosKeyVO posKeyVO = null;
            generateCommonXMLResponseForCCE(p_requestVO);
            sbf.append(p_requestVO.getSenderReturnMessage());
            sbf.append("<DATA>");
            if (p_requestVO.isSuccessTxn()) {
                posKeyVO = (PosKeyVO) p_requestVO.getValueObject();
                if (posKeyVO != null) {
                    sbf.append("<ICCID>").append(posKeyVO.getIccId()).append("</ICCID>");
                    sbf.append("<MSISDN>").append(posKeyVO.getMsisdn()).append("</MSISDN>");
                    if (posKeyVO.isRegistered()) {
                        sbf.append("<REGISTERED>").append(TypesI.YES).append("</REGISTERED>");
                    } else {
                        sbf.append("<REGISTERED>").append(TypesI.NO).append("</REGISTERED>");
                    }
                    sbf.append("<CREATEDBY>").append(posKeyVO.getCreatedBy()).append("</CREATEDBY>");
                    sbf.append("<CREATEDON>").append(BTSLDateUtil.getSystemLocaleDate(posKeyVO.getCreatedOnStr())).append("</CREATEDON>");
                    sbf.append("<NEWICCID>").append(posKeyVO.getNewIccId()).append("</NEWICCID>");
                    sbf.append("<MODIFIEDBY>").append(posKeyVO.getModifiedBy()).append("</MODIFIEDBY>");
                    sbf.append("<MODIFIEDON>").append(BTSLDateUtil.getSystemLocaleDate(posKeyVO.getModifedOnStr())).append("</MODIFIEDON>");
                    sbf.append("<SIMPRFID>").append(posKeyVO.getSimProfile()).append("</SIMPRFID>");
                }
            }
            sbf.append("</DATA></COMMAND>");
            p_requestVO.setSenderReturnMessage(sbf.toString());
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateIccidMsisdnEnquiryResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateIccidMsisdnEnquiryResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method parse routing enquiry request from XML String to hashmap
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void parseRoutingEnquiryRequest(RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "parseRoutingEnquiryRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        try {
            parseCommonXMLRequestForCCE(p_requestVO);
            HashMap requestMap = null;
            requestMap = p_requestVO.getRequestMap();
            final String requestStr = (String) p_requestVO.getRequestMap().get("DATA");
            final int index = requestStr.indexOf("<MSISDN>");
            final String msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
            requestMap.put("MSISDN", msisdn);
            p_requestVO.setRequestMap(requestMap);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[parseRoutingEnquiryRequest]",
                PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT, "", "", "parseRoutingEnquiryRequest:" + e.getMessage());
            throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method generates Routing enquiry response from hashmap to XML string
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void generateRoutingEnquiryResponse(RequestVO p_requestVO) {
        final String methodName = "generateRoutingEnquiryResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            ArrayList arrayList = null;
            RoutingVO routingVO = null;
            generateCommonXMLResponseForCCE(p_requestVO);
            sbf.append(p_requestVO.getSenderReturnMessage());
            sbf.append("<DATA>");
            if (p_requestVO.isSuccessTxn()) {
                arrayList = (ArrayList) p_requestVO.getValueObject();
                for (int i = 0, listSize = arrayList.size(); i < listSize; i++) {
                    routingVO = (RoutingVO) arrayList.get(i);
                    sbf.append("<MSISDN>").append(routingVO.getMsisdn()).append("</MSISDN>");
                    sbf.append("<INTID>").append(routingVO.getInterfaceID()).append("</INTID>");
                    sbf.append("<SUBTYPE>").append(routingVO.getSubscriberType()).append("</SUBTYPE>");
                    sbf.append("<EXTINTID>").append(routingVO.getExternalInterfaceID()).append("</EXTINTID>");
                }
            }
            sbf.append("</DATA></COMMAND>");
            p_requestVO.setSenderReturnMessage(sbf.toString());
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateRoutingEnquiryResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateRoutingEnquiryResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method parse white list enquiry request from XML String to hashmap
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void parseWhiteListEnquiryRequest(RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "parseWhiteListEnquiryRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        try {
            parseCommonXMLRequestForCCE(p_requestVO);
            HashMap requestMap = null;
            requestMap = p_requestVO.getRequestMap();
            final String requestStr = (String) p_requestVO.getRequestMap().get("DATA");
            final int index = requestStr.indexOf("<MSISDN>");
            final String msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
            requestMap.put("MSISDN", msisdn);
            p_requestVO.setRequestMap(requestMap);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[parseWhiteListEnquiryRequest]",
                PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT, "", "", "parseWhiteListEnquiryRequest:" + e.getMessage());
            throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method generates WhiteList enquiry response from hashmap to XML
     * string
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void generateWhiteListEnquiryResponse(RequestVO p_requestVO) {
        final String methodName = "generateWhiteListEnquiryResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            WhiteListVO whiteListVO = null;
            generateCommonXMLResponseForCCE(p_requestVO);
            sbf.append(p_requestVO.getSenderReturnMessage());
            sbf.append("<DATA>");
            if (p_requestVO.isSuccessTxn()) {
                whiteListVO = (WhiteListVO) p_requestVO.getValueObject();
                if (whiteListVO != null) {
                    sbf.append("<NETWORK>").append(whiteListVO.getNetworkCode()).append("</NETWORK>");
                    sbf.append("<MSISDN>").append(whiteListVO.getMsisdn()).append("</MSISDN>");
                    sbf.append("<IMSI>").append(whiteListVO.getImsi()).append("</IMSI>");
                    sbf.append("<ACCOUNTID>").append(whiteListVO.getAccountID()).append("</ACCOUNTID>");
                    sbf.append("<ACCOUNTSTATUS>").append(whiteListVO.getAccountStatus()).append("</ACCOUNTSTATUS>");
                    sbf.append("<SERVICECLASS>").append(whiteListVO.getServiceClassCode()).append("</SERVICECLASS>");
                    sbf.append("<CREDITLIMIT>").append(whiteListVO.getCreditLimitStr()).append("</CREDITLIMIT>");
                    sbf.append("<INTERFACEID>").append(whiteListVO.getInterfaceID()).append("</INTERFACEID>");
                    sbf.append("<INTERFACENAME>").append(whiteListVO.getInterfaceName()).append("</INTERFACENAME>");
                    sbf.append("<STATUS>").append(whiteListVO.getStatusStr()).append("</STATUS>");
                    sbf.append("<ACTIVATEDON>").append(BTSLDateUtil.getSystemLocaleDate(whiteListVO.getActivatedOnStr())).append("</ACTIVATEDON>");
                    sbf.append("<DATE>").append(BTSLDateUtil.getSystemLocaleDate(whiteListVO.getEntryDateStr())).append("</DATE>");
                }
            }
            sbf.append("</DATA></COMMAND>");
            p_requestVO.setSenderReturnMessage(sbf.toString());
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateWhiteListEnquiryResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateWhiteListEnquiryResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method parse pin management enquiry request from XML String to
     * hashmap
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void parsePinMgtRequest(RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "parsePinMgtRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        try {
            parseCommonXMLRequestForCCE(p_requestVO);
            HashMap requestMap = null;
            requestMap = p_requestVO.getRequestMap();
            final String requestStr = (String) p_requestVO.getRequestMap().get("DATA");
            int index = requestStr.indexOf("<MODULE>");
            final String module = requestStr.substring(index + "<MODULE>".length(), requestStr.indexOf("</MODULE>", index));
            requestMap.put("MODULE", module);
            index = requestStr.indexOf("<ACTION>");
            final String action = requestStr.substring(index + "<ACTION>".length(), requestStr.indexOf("</ACTION>", index));
            requestMap.put("ACTION", action);
            index = requestStr.indexOf("<MSISDN>");
            final String msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
            requestMap.put("MSISDN", msisdn);
            p_requestVO.setRequestMap(requestMap);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[parsePinMgtRequest]",
                PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT, "", "", "parsePinMgtRequest:" + e.getMessage());
            throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method generates pin management response from hashmap to XML string
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void generatePinMgtResponse(RequestVO p_requestVO) {
        final String methodName = "generatePinMgtResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            generateCommonXMLResponseForCCE(p_requestVO);
            sbf.append(p_requestVO.getSenderReturnMessage());
            sbf.append("<DATA>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<ACTION>").append(p_requestVO.getRequestMap().get("ACTION")).append("</ACTION>");
            }
            sbf.append("</DATA></COMMAND>");
            p_requestVO.setSenderReturnMessage(sbf.toString());
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generatePinMgtResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generatePinMgtResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method parse suspend p2p request from XML String to hashmap
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void parseSuspendP2PRequest(RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "parseSuspendP2PRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        try {
            parseCommonXMLRequestForCCE(p_requestVO);
            HashMap requestMap = null;
            requestMap = p_requestVO.getRequestMap();
            final String requestStr = (String) p_requestVO.getRequestMap().get("DATA");
            final int index = requestStr.indexOf("<MSISDN>");
            final String msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
            requestMap.put("MSISDN", msisdn);
            p_requestVO.setRequestMap(requestMap);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[parseSuspendP2PRequest]",
                PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT, "", "", "parseSuspendP2PRequest:" + e.getMessage());
            throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method generates suspend p2p response from hashmap to XML string
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void generateSuspendP2PResponse(RequestVO p_requestVO) {
        final String methodName = "generateSuspendP2PResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            generateCommonXMLResponseForCCE(p_requestVO);
            sbf.append(p_requestVO.getSenderReturnMessage());
            sbf.append("<DATA></DATA></COMMAND>");
            p_requestVO.setSenderReturnMessage(sbf.toString());
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateSuspendP2PResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateSuspendP2PResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method parse resume p2p request from XML String to hashmap
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void parseResumeP2PRequest(RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "parseResumeP2PRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        try {
            parseCommonXMLRequestForCCE(p_requestVO);
            HashMap requestMap = null;
            requestMap = p_requestVO.getRequestMap();
            final String requestStr = (String) p_requestVO.getRequestMap().get("DATA");
            final int index = requestStr.indexOf("<MSISDN>");
            final String msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
            requestMap.put("MSISDN", msisdn);
            p_requestVO.setRequestMap(requestMap);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[parseResumeP2PRequest]",
                PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT, "", "", "parseResumeP2PRequest:" + e.getMessage());
            throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method generates resume p2p response from hashmap to XML string
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void generateResumeP2PResponse(RequestVO p_requestVO) {
        final String methodName = "generateResumeP2PResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            generateCommonXMLResponseForCCE(p_requestVO);
            sbf.append(p_requestVO.getSenderReturnMessage());
            sbf.append("<DATA></DATA></COMMAND>");
            p_requestVO.setSenderReturnMessage(sbf.toString());
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateResumeP2PResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateResumeP2PResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method parse register p2p request from XML String to hashmap
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void parseRegisterP2PRequest(RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "parseRegisterP2PRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        try {
            parseCommonXMLRequestForCCE(p_requestVO);
            HashMap requestMap = null;
            requestMap = p_requestVO.getRequestMap();
            final String requestStr = (String) p_requestVO.getRequestMap().get("DATA");
            final int index = requestStr.indexOf("<MSISDN>");
            final String msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
            requestMap.put("MSISDN", msisdn);
            p_requestVO.setRequestMap(requestMap);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[parseRegisterP2PRequest]",
                PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT, "", "", "parseRegisterP2PRequest:" + e.getMessage());
            throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method generates register p2p response from hashmap to XML string
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void generateRegisterP2PResponse(RequestVO p_requestVO) {
        final String methodName = "generateRegisterP2PResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            ArrayList arrayList = null;
            SenderVO senderVO = null;
            generateCommonXMLResponseForCCE(p_requestVO);
            sbf.append(p_requestVO.getSenderReturnMessage());
            sbf.append("<DATA>");
            if (p_requestVO.isSuccessTxn()) {
                arrayList = (ArrayList) p_requestVO.getValueObject();
                for (int i = 0, listSize = arrayList.size(); i < listSize; i++) {
                    senderVO = (SenderVO) arrayList.get(i);
                    sbf.append("<MSISDN>").append(senderVO.getMsisdn()).append("</MSISDN>");
                    sbf.append("<REGISTEREDON>").append(BTSLDateUtil.getSystemLocaleDate(senderVO.getRegisteredOnAsString())).append("</REGISTEREDON>");
                    sbf.append("<ACTIVATEDON>").append(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.NullToString(senderVO.getActivatedOnAsString()))).append("</ACTIVATEDON>");
                    sbf.append("<SUBTYPE>").append(senderVO.getSubscriberType()).append("</SUBTYPE>");
                    sbf.append("<SUBSTATUS>").append(senderVO.getStatus()).append("</SUBSTATUS>");
                    sbf.append("<CREDITLIMIT>").append(BTSLUtil.NullToString(senderVO.getCreditLimitStr())).append("</CREDITLIMIT>");
                    sbf.append("<SERVICECLASSCODE>").append(BTSLUtil.NullToString(senderVO.getServiceClassCode())).append("</SERVICECLASSCODE>");
                    sbf.append("<TOTALTRFAMOUNT>").append(PretupsBL.getDisplayAmount(senderVO.getTotalTransferAmount())).append("</TOTALTRFAMOUNT>");
                    sbf.append("<TOTALTRFCOUNT>").append(senderVO.getTotalTransfers()).append("</TOTALTRFCOUNT>");
                    sbf.append("<LASTSUCCESSTRFDATE>").append(BTSLUtil.NullToString(senderVO.getLastSuccessTransferDateStr())).append("</LASTSUCCESSTRFDATE>");
                    sbf.append("<LASTTRFID>").append(BTSLUtil.NullToString(senderVO.getLastTransferID())).append("</LASTTRFID>");
                    sbf.append("<LASTTXNMSISDN>").append(BTSLUtil.NullToString(senderVO.getLastTransferMSISDN())).append("</LASTTXNMSISDN>");
                    sbf.append("<LASTTXNAMOUNT>").append(BTSLUtil.NullToString(PretupsBL.getDisplayAmount(senderVO.getLastTransferAmount()))).append("</LASTTXNAMOUNT>");
                    sbf.append("<LASTTXNTYPE>").append(BTSLUtil.NullToString(senderVO.getLastTransferType())).append("</LASTTXNTYPE>");
                    sbf.append("<LASTTXNON>").append(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.NullToString(BTSLUtil.getDateStringFromDate(senderVO.getLastTransferOn())))).append("</LASTTXNON>");
                    sbf.append("<LASTTRFSTATUS>").append(BTSLUtil.NullToString(senderVO.getLastTransferStatus())).append("</LASTTRFSTATUS>");
                    sbf.append("<DAILYTRFAMOUNT>").append(BTSLUtil.NullToString(PretupsBL.getDisplayAmount(senderVO.getDailyTransferAmount()))).append("</DAILYTRFAMOUNT>");
                    sbf.append("<DAILYTRFCOUNT>").append(senderVO.getDailyTransferCount()).append("</DAILYTRFCOUNT>");
                    sbf.append("<WEEKLYTRFAMOUNT>").append(BTSLUtil.NullToString(PretupsBL.getDisplayAmount(senderVO.getWeeklyTransferAmount()))).append("</WEEKLYTRFAMOUNT>");
                    sbf.append("<WEEKLYTRFCOUNT>").append(senderVO.getWeeklyTransferCount()).append("</WEEKLYTRFCOUNT>");
                    sbf.append("<MONTHLYTRFAMOUNT>").append(BTSLUtil.NullToString(PretupsBL.getDisplayAmount(senderVO.getMonthlyTransferAmount()))).append("</MONTHLYTRFAMOUNT>");
                    sbf.append("<MONTHLYTRFCOUNT>").append(senderVO.getMonthlyTransferCount()).append("</MONTHLYTRFCOUNT>");
                    sbf.append("<BUDDYLISTSIZE>").append(senderVO.getBuddySeqNumber()).append("</BUDDYLISTSIZE>");
                }
            }
            sbf.append("</DATA></COMMAND>");
            p_requestVO.setSenderReturnMessage(sbf.toString());
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateRegisterP2PResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateRegisterP2PResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method parse de register p2p request from XML String to hashmap
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void parseDeregisterP2PRequest(RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "parseDeregisterP2PRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        try {
            parseCommonXMLRequestForCCE(p_requestVO);
            HashMap requestMap = null;
            requestMap = p_requestVO.getRequestMap();
            final String requestStr = (String) p_requestVO.getRequestMap().get("DATA");
            final int index = requestStr.indexOf("<MSISDN>");
            final String msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
            requestMap.put("MSISDN", msisdn);
            p_requestVO.setRequestMap(requestMap);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[parseDeregisterP2PRequest]",
                PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT, "", "", "parseDeregisterP2PRequest:" + e.getMessage());
            throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method generates de register p2p response from hashmap to XML string
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void generateDeregisterP2PResponse(RequestVO p_requestVO) {
        final String methodName = "generateDeregisterP2PResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            generateCommonXMLResponseForCCE(p_requestVO);
            sbf.append(p_requestVO.getSenderReturnMessage());
            sbf.append("<DATA></DATA></COMMAND>");
            p_requestVO.setSenderReturnMessage(sbf.toString());
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateDeregisterP2PResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateDeregisterP2PResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method parse access control management enquiry request from XML
     * String to hashmap
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void parseAccCtrlMgtRequest(RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "parseAccCtrlMgtRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        try {
            parseCommonXMLRequestForCCE(p_requestVO);
            HashMap requestMap = null;
            requestMap = p_requestVO.getRequestMap();
            final String requestStr = (String) p_requestVO.getRequestMap().get("DATA");
            int index = requestStr.indexOf("<USERLOGINID>");
            final String loginId = requestStr.substring(index + "<USERLOGINID>".length(), requestStr.indexOf("</USERLOGINID>", index));
            requestMap.put("USERLOGINID", loginId);
            index = requestStr.indexOf("<ACTION>");
            final String action = requestStr.substring(index + "<ACTION>".length(), requestStr.indexOf("</ACTION>", index));
            requestMap.put("ACTION", action);
            p_requestVO.setRequestMap(requestMap);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[parseAccCtrlMgtRequest]",
                PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT, "", "", "parseAccCtrlMgtRequest:" + e.getMessage());
            throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method access control management response from hashmap to XML string
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void generateAccCtrlMgtResponse(RequestVO p_requestVO) {
        final String methodName = "generateAccCtrlMgtResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            generateCommonXMLResponseForCCE(p_requestVO);
            sbf.append(p_requestVO.getSenderReturnMessage());
            sbf.append("<DATA>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<ACTION>").append(p_requestVO.getRequestMap().get("ACTION")).append("</ACTION>");
            }
            sbf.append("</DATA></COMMAND>");
            p_requestVO.setSenderReturnMessage(sbf.toString());
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateAccCtrlMgtResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateAccCtrlMgtResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method parse view channel user request from XML String to hashmap
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void parseViewChnlUserRequest(RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "parseViewChnlUserRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        try {
            parseCommonXMLRequestForCCE(p_requestVO);
            HashMap requestMap = null;
            requestMap = p_requestVO.getRequestMap();
            final String requestStr = (String) p_requestVO.getRequestMap().get("DATA");
            int index = requestStr.indexOf("<USERLOGINID>");
            final String loginId = requestStr.substring(index + "<USERLOGINID>".length(), requestStr.indexOf("</USERLOGINID>", index));
            requestMap.put("USERLOGINID", loginId);
            index = requestStr.indexOf("<MSISDN>");
            final String msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
            requestMap.put("MSISDN", msisdn);
            p_requestVO.setRequestMap(requestMap);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[parseViewChnlUserRequest]",
                PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT, "", "", "parseViewChnlUserRequest:" + e.getMessage());
            throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method view channel user response from hashmap to XML string
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void generateViewChnlUserResponse(RequestVO p_requestVO) {
        final String methodName = "generateViewChnlUserResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        StringBuffer sbf = null;
        try {
            ChannelUserVO channelUserVO = null;
            sbf = new StringBuffer(1024);
            generateCommonXMLResponseForCCE(p_requestVO);
            sbf.append(p_requestVO.getSenderReturnMessage());
            sbf.append("<DATA>");
            if (p_requestVO.isSuccessTxn()) {
                channelUserVO = (ChannelUserVO) p_requestVO.getValueObject();
                if (channelUserVO != null) {
                    sbf.append("<STATUS>").append(channelUserVO.getStatusDesc() == null ? "" : channelUserVO.getStatusDesc()).append("</STATUS>");
                    sbf.append("<USRNAME>").append(channelUserVO.getUserName() == null ? "" : channelUserVO.getUserName()).append("</USRNAME>");
                    sbf.append("<MSISDN>").append(channelUserVO.getMsisdn() == null ? "" : channelUserVO.getMsisdn()).append("</MSISDN>");
                    sbf.append("<USERLOGINID>").append(channelUserVO.getLoginID() == null ? "" : channelUserVO.getLoginID()).append("</USERLOGINID>");
                    sbf.append("<SHTNAME>").append(channelUserVO.getShortName() == null ? "" : channelUserVO.getShortName()).append("</SHTNAME>");
                    sbf.append("<SUBCODE>").append(channelUserVO.getEmpCode() == null ? "" : channelUserVO.getEmpCode()).append("</SUBCODE>");
                    sbf.append("<EXTCODE>").append(channelUserVO.getExternalCode() == null ? "" : channelUserVO.getExternalCode()).append("</EXTCODE>");
                    sbf.append("<INSUSPEND>").append(channelUserVO.getInSuspend() == null ? "" : channelUserVO.getInSuspend()).append("</INSUSPEND>");
                    sbf.append("<OUTSUSPEND>").append(channelUserVO.getOutSuspened() == null ? "" : channelUserVO.getOutSuspened()).append("</OUTSUSPEND>");
                    sbf.append("<ADDRESS>");
                    if (!BTSLUtil.isNullString(channelUserVO.getAddress1()) && channelUserVO.getAddress1() != null) {
                        sbf.append(channelUserVO.getAddress1());
                    }
                    if (!BTSLUtil.isNullString(channelUserVO.getAddress1()) && channelUserVO.getAddress1() != null && !BTSLUtil.isNullString(channelUserVO.getAddress2()) && channelUserVO
                        .getAddress2() != null) {
                        sbf.append(", " + channelUserVO.getAddress2());
                    }
                    if (!BTSLUtil.isNullString(channelUserVO.getAddress2()) && channelUserVO.getAddress2() != null) {
                        sbf.append(channelUserVO.getAddress2());
                    }
                    sbf.append("</ADDRESS>");
                    sbf.append("<USRGRADE>").append(channelUserVO.getUserGradeName() == null ? "" : channelUserVO.getUserGradeName()).append("</USRGRADE>");
                    sbf.append("<COMMPRF>").append(channelUserVO.getCommissionProfileSetName() == null ? "" : channelUserVO.getCommissionProfileSetName()).append("</COMMPRF>");
                    sbf.append("<TRFPRF>").append(channelUserVO.getTransferProfileName() == null ? "" : channelUserVO.getTransferProfileName()).append("</TRFPRF>");
                    // Roles not required as discussed with gurjeet on
                    // 21-12-2006
                    /*
                     * sbf.append("<ROLES>");
                     * ArrayList arrayList=channelUserVO.getMenuItemList();
                     * String moduleAndRolesStr=null;
                     * String moduleAndRolesArray[]=null;
                     * if (!arrayList.isEmpty() && arrayList!=null)
                     * {
                     * for(int i=0, listSize=arrayList.size();i<listSize;i++)
                     * {
                     * moduleAndRolesStr=(String)arrayList.get(i);
                     * moduleAndRolesArray=moduleAndRolesStr.split(":");
                     * sbf.append("<RECORD>");
                     * sbf.append("<TYPE>"+moduleAndRolesArray[0]+"</TYPE>");
                     * sbf.append("<CONTENTS>"+moduleAndRolesArray[1]+"</CONTENTS>"
                     * );
                     * sbf.append("</RECORD>");
                     * }
                     * }
                     * sbf.append("</ROLES>");
                     */// discussed with ved, service type will contauin comma
                       // separated values
                    sbf.append("<SERVICES>").append(channelUserVO.getServiceTypes() == null ? "" : channelUserVO.getServiceTypes()).append("</SERVICES>");
                    sbf.append("<PARENTNAME>").append(channelUserVO.getParentName() == null ? "" : channelUserVO.getParentName()).append("</PARENTNAME>");
                    sbf.append("<PARENTMSISDN>").append(channelUserVO.getParentMsisdn() == null ? "" : channelUserVO.getParentMsisdn()).append("</PARENTMSISDN>");
                    sbf.append("<PARENTCATEGORY>").append(channelUserVO.getParentCategoryName() == null ? "" : channelUserVO.getParentCategoryName()).append("</PARENTCATEGORY>");
                    sbf.append("<OWNERNAME>").append(channelUserVO.getOwnerName() == null ? "" : channelUserVO.getOwnerName()).append("</OWNERNAME>");
                    sbf.append("<OWNERMSISDN>").append(channelUserVO.getOwnerMsisdn() == null ? "" : channelUserVO.getOwnerMsisdn()).append("</OWNERMSISDN>");
                    sbf.append("<OWNERCATEGORY>").append(channelUserVO.getOwnerCategoryName() == null ? "" : channelUserVO.getOwnerCategoryName()).append("</OWNERCATEGORY>");
                }
            }
            sbf.append("</DATA></COMMAND>");
            p_requestVO.setSenderReturnMessage(sbf.toString());
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateViewChnlUserResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateViewChnlUserResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * Method to parse External Interface Recharge Request
     * 
     * @param p_requestVO
     * @throws Exception
     */
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
            String date = requestStr.substring(index + "<DATE>".length(), requestStr.indexOf("</DATE>", index));
            date = BTSLDateUtil.getGregorianDateInString(date);

            requestHashMap.put("DATE", date);
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
            requestHashMap.put("PASSWORD", password);
            index = requestStr.indexOf("<EXTCODE>");
            final String extCode = requestStr.substring(index + "<EXTCODE>".length(), requestStr.indexOf("</EXTCODE>", index));
            requestHashMap.put("EXTCODE", extCode);
            index = requestStr.indexOf("<EXTREFNUM>");
            final String extRefNumber = requestStr.substring(index + "<EXTREFNUM>".length(), requestStr.indexOf("</EXTREFNUM>", index));
            requestHashMap.put("EXTREFNUM", extRefNumber);
            index = requestStr.indexOf("<MSISDN2>");
            final String msisdn2 = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));
            index = requestStr.indexOf("<AMOUNT>");
            final String amount = requestStr.substring(index + "<AMOUNT>".length(), requestStr.indexOf("</AMOUNT>", index));
            index = requestStr.indexOf("<LANGUAGE1>");
            final String language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
            index = requestStr.indexOf("<LANGUAGE2>");
            String language2 = requestStr.substring(index + "<LANGUAGE2>".length(), requestStr.indexOf("</LANGUAGE2>", index));
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
            // Added By Diwakar on 01-MAR-2014
            XMLStringValidation.validateChannelExtCreditTransferRequest(p_requestVO, type, date, extNwCode, msisdn, pin, loginId, password, extCode, extRefNumber, msisdn2,
                amount, language1, language2, selector);
            // Ended Here
            if (BTSLUtil.isNullString(selector)) {
                // selector=""+SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE;
                // Changed on 27/05/07 for Service Type selector Mapping
                final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(PretupsI.SERVICE_TYPE_CHNL_RECHARGE);
                if (serviceSelectorMappingVO != null) {
                    selector = serviceSelectorMappingVO.getSelectorCode();
                }
            }
            if (BTSLUtil.isNullString(amount)) {
                _log.error("parseCreditRechargeRequest", "Amount field is null ");
                throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_BLANK_AMOUNT);
            }
            // Added By Diwakar on 20-02-2014 in case language1 is not coming
            // into request
            if (!BTSLUtil.isNullString(language1)) {
                p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            } else {
                p_requestVO.setSenderLocale(new Locale(defaultLanguage, defaultCountry));
            }

            // in case language2 is not coming into request
            if (BTSLUtil.isNullString(language2)) {
                language2 = String.valueOf(PretupsI.CHNL_LOCALE_LANG1_VALUE); // for
                // default
                // value
                // 0
            }
            // Ended Here by Diwakar
            if (BTSLUtil.isNullString(msisdn2)) {
                _log.error("parseCreditRechargeRequest", "msisdn2 field is null ");
                final String[] strArr = new String[] { msisdn2, String.valueOf(minMsisdnLength), String.valueOf(maxMsisdnLength) };
                throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.CHNL_ERROR_RECR_MSISDN_NOTINRANGE, 0, strArr, null);
            }
            if (!(BTSLUtil.isNullString(info1))) {
                final int length = info1.length();
                if (length > 100) {
                    _log.debug(methodName, "length is " + length);
                    _log.error("parseCreditRechargeRequest", "Characters are more than 100 ");
                    throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MAX_100_INFO1);
                }
            }
            if (!(BTSLUtil.isNullString(info2))) {
                final int length = info2.length();
                if (length > 100) {
                    _log.debug(methodName, "length is " + length);
                    _log.error("parseCreditRechargeRequest", "Characters are more than 100 ");
                    throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MAX_100_INFO2);
                }
            }
            if (!(BTSLUtil.isNullString(info3))) {
                final int length = info3.length();
                if (length > 100) {
                    _log.debug(methodName, "length is " + length);
                    _log.error("parseCreditRechargeRequest", "Characters are more than 100 ");
                    throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MAX_100_INFO3);
                }
            }

            if (!(BTSLUtil.isNullString(info4))) {
                final int length = info4.length();
                if (length > 100) {
                    _log.debug(methodName, "length is " + length);

                    _log.error("parseCreditRechargeRequest", "Characters are more than 100 ");
                    throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MAX_100_INFO4);
                }
            }
            if (!(BTSLUtil.isNullString(info5))) {
                final int length = info5.length();
                if (length > 100) {
                    _log.debug(methodName, "length is " + length);
                    _log.error("parseCreditRechargeRequest", "Characters are more than 100 ");
                    throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MAX_100_INFO5);
                }
            }
            if (!(BTSLUtil.isNullString(info6))) {
                final int length = info6.length();
                if (length > 100) {
                    _log.debug(methodName, "length is " + length);
                    _log.error("parseCreditRechargeRequest", "Characters are more than 100 ");
                    throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MAX_100_INFO6);
                }
            }
            if (!(BTSLUtil.isNullString(info7))) {
                final int length = info7.length();
                if (length > 100) {
                    _log.debug(methodName, "length is " + length);
                    _log.error("parseCreditRechargeRequest", "Characters are more than 100 ");
                    throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MAX_100_INFO7);
                }
            }
            if (!(BTSLUtil.isNullString(info8))) {
                final int length = info8.length();
                if (length > 100) {
                    _log.debug(methodName, "length is " + length);
                    _log.error("parseCreditRechargeRequest", "Characters are more than 100 ");
                    throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MAX_100_INFO8);
                }
            }
            if (!(BTSLUtil.isNullString(info9))) {
                final int length = info9.length();
                if (length > 100) {
                    _log.debug(methodName, "length is " + length);
                    _log.error("parseCreditRechargeRequest", "Characters are more than 100 ");

                    throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MAX_100_INFO9);
                }
            }
            if (!(BTSLUtil.isNullString(info10))) {
                final int length = info10.length();
                if (length > 100) {
                    _log.debug(methodName, "length is " + length);
                    _log.error("parseCreditRechargeRequest", "Characters are more than 100 ");
                    throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_MAX_100_INFO10);
                }
            }
            parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_RECHARGE + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + amount + CHNL_MESSAGE_SEP + selector + CHNL_MESSAGE_SEP + language2;

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
            // VFE 6 CR
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
            // 03-MAR-2014
            p_requestVO.setMessageCode(be.getMessageKey());
            p_requestVO.setMessageArguments(be.getArgs());
            // 03-MAR-2014
            throw be;
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
        	String EXTERNAL_DATE_FORMAT = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_DATE_FORMAT);
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

            sbf.append("<DATE>" +BTSLDateUtil.getSystemLocaleDate(sdf.format(date), EXTERNAL_DATE_FORMAT)).append("</DATE>");

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
                "XMLStringParser[generateChannelExtCreditTransferResponse]", PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateChannelExtCreditTransferResponse:" + e
                    .getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    private static String getMessage(Locale locale, String key, String[] p_args) {
        final String methodName = "getMessage";
        String message = BTSLUtil.getMessage(locale, key, p_args);
        try {
			if(locale==null)
            {
                _log.error(methodName,"Locale not defined considering default locale "+defaultLanguage+" "+defaultCountry+"    key: "+key);
                locale=new Locale(defaultLanguage,defaultCountry);
            }
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
                        message = message.substring(endIndexForMessageCode);
                    }
                } else {
                    endIndexForMessageCode = message.indexOf(":");
                    if (endIndexForMessageCode != -1) {
                        messageCode = message.substring(0, endIndexForMessageCode);
                        message = message.substring(endIndexForMessageCode + 1);
                    }
                }
                /*
                 * Code killed by Avinash: to remove encoding in case of XML
                 * response.
                 */
                /*
                 * if("ar".equals(locale.getLanguage()) &&
                 * !message.startsWith("%"))
                 * message=BTSLUtil.encodeSpecial(message,true,localeMasterVO.
                 * getEncoding());
                 * else if(!"ar".equals(locale.getLanguage()))
                 * message=URLEncoder.encode(message,localeMasterVO.getEncoding()
                 * );
                 */
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
        }
        return message;
    }

    /**
     * This method parse suspend and resume user request from XML String to
     * hashmap
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void parseSuspendResumeUserRequest(RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "parseSuspendResumeUserRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        try {
            parseCommonXMLRequestForCCE(p_requestVO);
            HashMap requestMap = null;
            requestMap = p_requestVO.getRequestMap();
            final String requestStr = (String) p_requestVO.getRequestMap().get("DATA");

            int index = requestStr.indexOf("<MSISDN>");
            final String msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
            requestMap.put("MSISDN", msisdn);
            index = requestStr.indexOf("<USERLOGINID>");
            final String userLoginId = requestStr.substring(index + "<USERLOGINID>".length(), requestStr.indexOf("</USERLOGINID>", index));
            requestMap.put("USERLOGINID", userLoginId);
            index = requestStr.indexOf("<ACTION>");
            final String action = requestStr.substring(index + "<ACTION>".length(), requestStr.indexOf("</ACTION>", index));
            requestMap.put("ACTION", action);
            p_requestVO.setRequestMap(requestMap);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[parseSuspendResumeUserRequest]",
                PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT, "", "", "parseSuspendResumeUserRequest:" + e.getMessage());
            throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method generates suspend and resume response from hashmap to XML
     * string
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void generateSuspendResumeUserResponse(RequestVO p_requestVO) {
        final String methodName = "generateSuspendResumeUserResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            generateCommonXMLResponseForCCE(p_requestVO);
            sbf.append(p_requestVO.getSenderReturnMessage());
            sbf.append("<DATA></DATA></COMMAND>");
            p_requestVO.setSenderReturnMessage(sbf.toString());
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateSuspendResumeUserResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateSuspendResumeUserResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method parse user balance request from XML String to hashmap
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void parseUserBalanceRequest(RequestVO p_requestVO) throws BTSLBaseException {

        final String methodName = "parseUserBalanceRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        try {
            parseCommonXMLRequestForCCE(p_requestVO);
            HashMap requestMap = null;
            requestMap = p_requestVO.getRequestMap();
            final String requestStr = (String) p_requestVO.getRequestMap().get("DATA");

            int index = requestStr.indexOf("<MSISDN>");
            final String msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
            requestMap.put("MSISDN", msisdn);
            index = requestStr.indexOf("<USERLOGINID>");
            final String userLoginId = requestStr.substring(index + "<USERLOGINID>".length(), requestStr.indexOf("</USERLOGINID>", index));
            requestMap.put("USERLOGINID", userLoginId);
            if (requestStr.indexOf("<USEREXTCODE>") != -1) {
                index = requestStr.indexOf("<USEREXTCODE>");
                final String userExtCode = requestStr.substring(index + "<USEREXTCODE>".length(), requestStr.indexOf("</USEREXTCODE>", index));
                requestMap.put("USEREXTCODE", userExtCode);
            }
            p_requestVO.setRequestMap(requestMap);

        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[parseUserBalanceRequest]",
                PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT, "", "", "parseUserBalanceRequest:" + e.getMessage());
            throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method generates user balance response from hashmap to XML string
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void generateUserBalanceResponse(RequestVO p_requestVO) {
        final String methodName = "generateUserBalanceResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            UserBalancesVO userBalancesVO = null;
            generateCommonXMLResponseForCCE(p_requestVO);
            sbf.append(p_requestVO.getSenderReturnMessage());
            final HashMap hashMap = p_requestVO.getRequestMap();
            sbf.append("<DATA>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<MSISDN>").append(hashMap.get("MSISDN")).append("</MSISDN>");

                sbf.append("<USERLOGINID>").append(hashMap.get("USERLOGINID")).append("</USERLOGINID>");
                final ArrayList arrayList = (ArrayList) p_requestVO.getValueObject();
                final int listSize = arrayList.size();
                for (int i = 0; i < listSize; i++) {
                    userBalancesVO = (UserBalancesVO) arrayList.get(i);
                    sbf.append("<RECORD>");
                    sbf.append("<PRODUCTCODE>").append(userBalancesVO.getProductCode()).append("</PRODUCTCODE>");
                    sbf.append("<PRODUCTSHORTNAME>").append(userBalancesVO.getProductShortName()).append("</PRODUCTSHORTNAME>");
                    sbf.append("<BALANCE>").append(userBalancesVO.getBalanceStr()).append("</BALANCE>");
                    if (!BTSLUtil.isNullString(userBalancesVO.getAgentBalanceStr())) {
                        sbf.append("<AGENTBALANCE>").append(userBalancesVO.getAgentBalanceStr()).append("</AGENTBALANCE>");
                    }
                    sbf.append("</RECORD>");
                }
            }
            sbf.append("</DATA></COMMAND>");
            p_requestVO.setSenderReturnMessage(sbf.toString());
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateUserBalanceResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateUserBalanceResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method generates Service Suspend Response from hashmap to XML string
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void generateP2PServiceSuspendResponse(RequestVO p_requestVO) {
        final String methodName = "generateP2PServiceSuspendResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        StringBuffer sbf = null;
        String responseStr = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");

            /*
             * if
             * (!BTSLUtil.isNullString((String)p_requestVO.getRequestMap().get
             * ("TYPE")))
             * sbf.append("<TYPE>"+p_requestVO.getRequestMap().get("TYPE")+
             * "RESP</TYPE>");
             * else
             */
            sbf.append("<TYPE>SUSRESP</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateP2PServiceSuspendResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateP2PServiceSuspendResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method generates Service Resume Response from hashmap to XML string
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void generateP2PServiceResumeResponse(RequestVO p_requestVO) {
        final String methodName = "generateP2PServiceResumeResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        StringBuffer sbf = null;
        String responseStr = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");

            /*
             * if
             * (!BTSLUtil.isNullString((String)p_requestVO.getRequestMap().get
             * ("TYPE")))
             * sbf.append("<TYPE>"+p_requestVO.getRequestMap().get("TYPE")+
             * "RESP</TYPE>");
             * else
             */
            sbf.append("<TYPE>RESRESP</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
            }
            if(PretupsI.REQUEST_SOURCE_TYPE_EXTGW.equals(p_requestVO.getRequestGatewayType())){
            	sbf.append("<MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(),p_requestVO.getMessageArguments())).append("</MESSAGE>");	
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateP2PServiceResumeResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateP2PServiceResumeResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("generateServiceResumeResponse", "Exiting  p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * This method generates AddBuddy Response in XML format from requestVO
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void generateAddBuddyResponse(RequestVO p_requestVO) {
        final String methodName = "generateAddBuddyResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>ADDBUDDYRESP</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateAddBuddyResponse]",
                PretupsErrorCodesI.P2P_ERROR_EXCEPTION, "", "", "generateAddBuddyResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * This method generates DeleteBuddy Response in XML format from requestVO
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void generateDeleteBuddyResponse(RequestVO p_requestVO) {
        final String methodName = "generateDeleteBuddyResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>DELBUDDYRESP</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateDeleteBuddyResponse]",
                PretupsErrorCodesI.P2P_ERROR_EXCEPTION, "", "", "generateAddBuddyResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * This method generates DeleteSubscriberList Response in XML format from
     * requestVO
     * 
     * @param p_requestVO
     *            RequestVO
     * @author Harsh Dixit
     * @date 09 Aug 12
     */
    public static void generateDelMultCreditListResponse(RequestVO p_requestVO) {
        final String methodName = "generateDelMultCreditListResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>SCLTRFRESP</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateDelMultCreditListResponse]",
                PretupsErrorCodesI.P2P_ERROR_EXCEPTION, "", "", "generateDelMultCreditListResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * This method generates List Buddy Response in XML format from requestVO
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void generateListBuddyResponse(RequestVO p_requestVO) {
        final String methodName = "generateListBuddyResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>LSTBUDDYRESP</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateAddBuddyResponse]",
                PretupsErrorCodesI.P2P_ERROR_EXCEPTION, "", "", "generateAddBuddyResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * This method generates Balance Enquiry Response in XML format from
     * requestVO
     * 
     * @param p_requestVO
     *            RequestVO
     ** @author sanjeew.kumar
     * @Date 03/05/07
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
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>BALRESP</TYPE>");
            
           /* if (BTSLUtil.isNullString(p_requestVO.getTransactionID())) {
                sbf.append("<TXNID></TXNID>");
            } else {
                sbf.append("<TXNID>").append(p_requestVO.getTransactionID()).append("</TXNID>");
            }*/
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "XMLStringParser[generateChannelBalanceEnquiryResponse]", PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateChannelBalanceEnquiryResponse:" + e
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
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>DSRRESP</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "XMLStringParser[generateChannelDailyStatusReportResponse]", PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateChannelDailyStatusReportResponse:" + e
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
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>LTSRESP</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
            }
            sbf.append("</COMMAND>");
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
     * This method generates Last Transfer Status Report Response(P2P) in XML
     * format from requestVO
     * 
     * @param p_requestVO
     * @throws Exception
     * @author sanjeew.kumar
     * @Date 03/05/07
     */
    public static void generateLastTransferStatus(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateLastTransferStatus";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>PLTRESP</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
                
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
                
            }
            if(PretupsI.REQUEST_SOURCE_TYPE_EXTGW.equals(p_requestVO.getRequestGatewayType())){
            	sbf.append("<MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(),p_requestVO.getMessageArguments())).append("</MESSAGE>");	
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateLastTransferStatus]",
                PretupsErrorCodesI.P2P_ERROR_EXCEPTION, "", "", "generateLastTransferStatus:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * This method generates SelfBar Response in XML format from requestVO
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public static void generateSelfBarResponse(RequestVO p_requestVO) {
        final String methodName = "generateSelfBarResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>BARRESP</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
                
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
                }
            if(PretupsI.REQUEST_SOURCE_TYPE_EXTGW.equals(p_requestVO.getRequestGatewayType())){
            	sbf.append("<MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(),p_requestVO.getMessageArguments())).append("</MESSAGE>");	
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateSelfBarResponse]",
                PretupsErrorCodesI.P2P_ERROR_EXCEPTION, "", "", "generateSelfBarResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * This method generates generate EVD Response in XML format from requestVO
     * 
     * @param p_requestVO
     * @throws Exception
     */
    public static void generateEVDResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateEVDResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>EVDRESP</TYPE>");
            sbf.append("<TXNID>").append(p_requestVO.getTransactionID()).append("</TXNID>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
            }
            sbf.append("<MESSAGE>").append(XMLStringParser.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments())).append("</MESSAGE>");
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateEVDResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateEVDResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * @param p_requestVO
     * @throws Exception
     * @author shishupal.singh
     */
    public static void generateMultipleVoucherDistributionResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateMultipleVoucherDistributionResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        final java.util.Date date = new java.util.Date();
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>MEVDRESP</TYPE>");
            if (BTSLUtil.isNullString(p_requestVO.getTransactionID())) {
                sbf.append("<TXNID_ARR><TXNID></TXNID></TXNID_ARR>");
            } else {
                sbf.append("<TXNID_ARR>");
                for (int i = 0, j = ((ArrayList) p_requestVO.getValueObject()).size(); i < j; i++) {
                    sbf.append("<TXNID>").append((String) ((ArrayList) p_requestVO.getValueObject()).get(i)).append("</TXNID>");
                }
                sbf.append("</TXNID_ARR>");
            }
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "XMLStringParser[generateChannelExtRechargeStatusResponse]", PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateChannelExtRechargeStatusResponse:" + e
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
     * @author shishupal.singh
     */
    public static void generateUtilityBillPaymentResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateUtilityBillPaymentResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>PPBTRFRESP</TYPE>");
            if (BTSLUtil.isNullString(p_requestVO.getTransactionID())) {
                sbf.append("<TXNID></TXNID>");
            } else {
                sbf.append("<TXNID>").append(p_requestVO.getTransactionID()).append("</TXNID>");
            }
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "XMLStringParser[generateChannelPostPaidBillPaymentResponse]", PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "",
                "generateChannelPostPaidBillPaymentResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * This method parse ICCID MSISDN Map Request from XML String to hashmap
     * 
     * @param p_requestVO
     *            RequestVO
     * @author zafar.abbas
     */
    public static void parseIccidMsisdnMapRequest(RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "parseIccidMsisdnMapRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        try {
            parseCommonXMLRequestForCCE(p_requestVO);
            final HashMap requestMap = p_requestVO.getRequestMap();
            final String requestStr = (String) requestMap.get("DATA");
            int index = requestStr.indexOf("<MSISDN>");
            final String msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
            requestMap.put("MSISDN", msisdn);
            index = requestStr.indexOf("<ICCID>");
            final String iccid = requestStr.substring(index + "<ICCID>".length(), requestStr.indexOf("</ICCID>", index));
            requestMap.put("ICCID", iccid);
            p_requestVO.setExternalReferenceNum(requestMap.get("EXTREFNUM").toString());
            p_requestVO.setRequestMap(requestMap);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[parseIccidMsisdnMapRequest]",
                PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT, "", "", "parseIccidMsisdnMapRequest:" + e.getMessage());
            throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exiting  p_requestVO: " + p_requestVO.toString());
        }
    }

    /**
     * This method generate response for the ICCID MSISDN Map request
     * 
     * @param p_requestVO
     * @throws Exception
     * @author zafar.abbas
     */
    public static void generateIccidMsisdnMapResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateIccidMsisdnMapResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        try {
            final StringBuffer sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>ICCIDMSISDNMAPRESP</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
            }
            final String date = BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(new Date()));
            sbf.append("<DATE>").append(date).append("</DATE>");
            if (!"".equals(BTSLUtil.NullToString(p_requestVO.getExternalReferenceNum())) || (BTSLUtil.NullToString(p_requestVO.getExternalReferenceNum())).length() > 0) {
                sbf.append("<EXTREFNUM>").append(p_requestVO.getExternalReferenceNum().toString()).append("</EXTREFNUM>");
            }
            sbf.append("<MESSAGE>"+getMessage(p_requestVO.getLocale(),p_requestVO.getMessageCode(),p_requestVO.getMessageArguments())+"</MESSAGE>");
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateIccidMsisdnMapResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateIccidMsisdnMapResponse:" + e.getMessage());
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exiting responseStr: " + responseStr);
        }
    }

    /**
     * Generate the responsse for Gift recharge, in the case of USSD
     * 
     * @param p_requestVO
     * @throws Exception
     */
    public static void generateGiftRechargeResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateGiftRechargeResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>GFTRCRESP</TYPE>");
            if (BTSLUtil.isNullString(p_requestVO.getTransactionID())) {
                sbf.append("<TXNID></TXNID>");
            } else {
                sbf.append("<TXNID>").append(p_requestVO.getTransactionID()).append("</TXNID>");
            }
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                String message = p_requestVO.getMessageCode();
                if (message.indexOf("_") != -1) {
                    message = message.substring(0, message.indexOf("_"));
                }
                sbf.append("<TXNSTATUS>").append(message).append("</TXNSTATUS>");
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_GIFTRECHARGE);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateGiftRechargeResponse]",
                PretupsErrorCodesI.C2S_ERROR_EXCEPTION_GIFTRECHARGE, "", "", "generateGiftRechargeResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * @param receiverLocale
     * @return
     */

    /**
     * Added for CDMA Recharge via USSD system.
     * 
     * @param p_requestVO
     * @param p_action
     * @throws Exception
     * @author kapil.mehta
     */

    public static void generateChannelCreditTransferCDMAResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateChannelCreditTransferCDMAResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>CDMARCTRFRESP</TYPE>");
            if (BTSLUtil.isNullString(p_requestVO.getTransactionID())) {
                sbf.append("<TXNID></TXNID>");
            } else {
                sbf.append("<TXNID>").append(p_requestVO.getTransactionID()).append("</TXNID>");
            }
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                String message = p_requestVO.getMessageCode();
                if (message.indexOf("_") != -1) {
                    message = message.substring(0, message.indexOf("_"));
                }
                sbf.append("<TXNSTATUS>").append(message).append("</TXNSTATUS>");
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error("generateCreditTransferCDMAResponse", "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "XMLStringParser[generateChannelCreditTransferCDMAResponse]", PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateChannelCreditTransferCDMAResponse:" + e
                    .getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
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

    public static void generateChannelCreditTransferPSTNResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateChannelCreditTransferPSTNResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>PSTNRCTRFRESP</TYPE>");
            if (BTSLUtil.isNullString(p_requestVO.getTransactionID())) {
                sbf.append("<TXNID></TXNID>");
            } else {
                sbf.append("<TXNID>").append(p_requestVO.getTransactionID()).append("</TXNID>");
            }
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                String message = p_requestVO.getMessageCode();
                if (message.indexOf("_") != -1) {
                    message = message.substring(0, message.indexOf("_"));
                }
                sbf.append("<TXNSTATUS>").append(message).append("</TXNSTATUS>");
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "XMLStringParser[generateChannelCreditTransferPSTNResponse]", PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateChannelCreditTransferPSTNResponse:" + e
                    .getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
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

    public static void generateChannelCreditTransferINTRResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateChannelCreditTransferINTRResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>INTRRCTRFRESP</TYPE>");
            if (BTSLUtil.isNullString(p_requestVO.getTransactionID())) {
                sbf.append("<TXNID></TXNID>");
            } else {
                sbf.append("<TXNID>").append(p_requestVO.getTransactionID()).append("</TXNID>");
            }
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                String message = p_requestVO.getMessageCode();
                if (message.indexOf("_") != -1) {
                    message = message.substring(0, message.indexOf("_"));
                }
                sbf.append("<TXNSTATUS>").append(message).append("</TXNSTATUS>");
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "XMLStringParser[generateChannelCreditTransferINTRResponse]", PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateChannelCreditTransferINTRResponse:" + e
                    .getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
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

    public static void generateChannelOrderLineResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateChannelOrderLineResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>ORDL</TYPE>");
            if (BTSLUtil.isNullString(p_requestVO.getTransactionID())) {
                sbf.append("<TXNID></TXNID>");
            } else {
                sbf.append("<TXNID>").append(p_requestVO.getTransactionID()).append("</TXNID>");
            }
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                String message = p_requestVO.getMessageCode();
                if (message.indexOf("_") != -1) {
                    message = message.substring(0, message.indexOf("_"));
                }
                sbf.append("<TXNSTATUS>").append(message).append("</TXNSTATUS>");
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateChannelOrderLineResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateChannelOrderLineResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
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

    public static void generateChannelOrderCreditResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateChannelOrderCreditResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>ORDC</TYPE>");
            if (BTSLUtil.isNullString(p_requestVO.getTransactionID())) {
                sbf.append("<TXNID></TXNID>");
            } else {
                sbf.append("<TXNID>").append(p_requestVO.getTransactionID()).append("</TXNID>");
            }
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                String message = p_requestVO.getMessageCode();
                if (message.indexOf("_") != -1) {
                    message = message.substring(0, message.indexOf("_"));
                }
                sbf.append("<TXNSTATUS>").append(message).append("</TXNSTATUS>");
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateChannelOrderCreditResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateChannelOrderCreditResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
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

    public static void generateChannelBarringResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateChannelBarringResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>BAR</TYPE>");
            if (BTSLUtil.isNullString(p_requestVO.getTransactionID())) {
                sbf.append("<TXNID></TXNID>");
            } else {
                sbf.append("<TXNID>").append(p_requestVO.getTransactionID()).append("</TXNID>");
            }
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                String message = p_requestVO.getMessageCode();
                if (message.indexOf("_") != -1) {
                    message = message.substring(0, message.indexOf("_"));
                }
                sbf.append("<TXNSTATUS>").append(message).append("</TXNSTATUS>");
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateChannelBarringResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateChannelBarringResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * Added for generateIATRoamRechargeResponse.
     * 
     * @param p_requestVO
     * @throws Exception
     */
    public static void generateIATRoamRechargeResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateIATRoamRechargeResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>ROAMRCRESP</TYPE>");
            if (BTSLUtil.isNullString(p_requestVO.getTransactionID())) {
                sbf.append("<TXNID></TXNID>");
            } else {
                sbf.append("<TXNID>").append(p_requestVO.getTransactionID()).append("</TXNID>");
            }
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                String message = p_requestVO.getMessageCode();
                if (message.indexOf("_") != -1) {
                    message = message.substring(0, message.indexOf("_"));
                }
                sbf.append("<TXNSTATUS>").append(message).append("</TXNSTATUS>");
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateIATRoamRechargeResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateIATRoamRechargeResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    public static void generateIATInternationalRechargeResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateIATInternationalRechargeResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>INTLRCREQ</TYPE>");
            if (BTSLUtil.isNullString(p_requestVO.getTransactionID())) {
                sbf.append("<TXNID></TXNID>");
            } else {
                sbf.append("<TXNID>").append(p_requestVO.getTransactionID()).append("</TXNID>");
            }
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                String message = p_requestVO.getMessageCode();
                if (message.indexOf("_") != -1) {
                    message = message.substring(0, message.indexOf("_"));
                }
                sbf.append("<TXNSTATUS>").append(message).append("</TXNSTATUS>");
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "XMLStringParser[generateIATInternationalRechargeResponse]", PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateIATInternationalRechargeResponse:" + e
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
     * @Date 27/11/09
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
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>LXTSRESP</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
            }
            sbf.append("</COMMAND>");
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
     * @Date 27/11/09
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
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>CUSTRESP</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
            }
            sbf.append("</COMMAND>");
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
     * Generate the response of the MVD Download request.
     * 
     * @param p_requestVO
     * @throws Exception
     * @author Ashish Todia
     */
    public static void generateMVDDownloadResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateMVDDownloadResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>EXTMVDRESP</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateMVDDownloadResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateChannelLastTransferStatusResponse:" + e.getMessage());
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
     *         used to generate C2S transfer enquiry response
     */
    public static void generate2STransferEnqResp(RequestVO p_requestVO) throws Exception {
        final String methodName = "generate2STransferEnqResp";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>RCETRANRES</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generate2STransferEnqResp]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generate2STransferEnqResp:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    // response

    /**
     * This method generatePvtRechargeRegistrationResponsee in XML for private
     * recharge
     * 
     * @param p_requestVO
     * @throws Exception
     * @author Jasmine kaur
     * @Date 2/1/11
     */
    public static void generatePvtRechargeRegistrationResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generatePvtRechargeRegistrationResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>SIDRESP</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
                sbf.append("<MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(),p_requestVO.getMessageArguments())).append("</MESSAGE>");
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
                sbf.append("<MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments())).append("</MESSAGE>");
            }

            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error("generateChannelRegistrationResponse", "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "XMLStringParser[generatePvtRechargeRegistrationResponse]", PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generatePvtRechargeRegistrationResponse:" + e
                    .getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * Method to generate the response for user creation.
     * 
     * @param p_requestVO
     * @throws Exception
     */
    public static void generateChannelUserCreationResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateChannelUserCreationResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>ADDCHUSRRESP</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
            }
            sbf.append("</COMMAND>");
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
     * This method generates a response for the delete SID request in the case
     * of private recharge.
     * 
     * @param p_requestVO
     * @throws Exception
     * @author Ankuj.Arora
     * @Date 29/12/10
     */
    public static void generateDeleteSIDResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateDeleteSIDResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>SIDDELRESP</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
                sbf.append("<MESSAGE>").append( getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(),p_requestVO.getMessageArguments())).append("</MESSAGE>");
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
                sbf.append("<MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments())).append("</MESSAGE>");
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateDeleteSIDResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateDeleteSIDResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * This method generates a response for the enquiry SID request in the case
     * of private recharge.
     * 
     * @param p_requestVO
     * @throws Exception
     * @author Ankuj.Arora
     * @Date 29/12/10
     */
    public static void generateEnquirySIDResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateEnquirySIDResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        Boolean isSidEncryptionAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SID_ENCRYPTION_ALLOWED);
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>SIDENQRESP</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
                if(isSidEncryptionAllowed)
                	sbf.append("<SID>").append(BTSLUtil.decrypt3DesAesText(p_requestVO.getSid())).append("</SID>");
                else
                	sbf.append("<SID>").append(p_requestVO.getSid()).append("</SID>");
                sbf.append("<MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(),p_requestVO.getMessageArguments())).append("</MESSAGE>");
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
                sbf.append("<MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments())).append("</MESSAGE>");
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateEnquirySIDResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateEnquirySIDResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /*
     * External Gateway Parser Added By Babu Kunwar
     */
    /**
     * getDateTime
     * 
     * @param p_date
     * @param p_format
     * @return string
     */
    private static String getDateTime(Date p_date, String p_format) {
    	String systemDateTimeFormat = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATETIME_FORMAT);
        if (p_format == null || p_format.length() <= 0) {
            p_format = systemDateTimeFormat;// PretupsI.TIMESTAMP_DATESPACEHHMMSS;
        }
        final SimpleDateFormat sdf = new SimpleDateFormat(p_format);
        sdf.setLenient(false);
        if (p_date == null) {
            p_date = new Date();
        }
        return sdf.format(p_date);
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
        final String methodName = "generateExtLastXTrfResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
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
            sbf.append("<MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments())).append("</MESSAGE>");
            sbf.append("<TXNDETAILS>");
            final HashMap requestHashMap = p_requestVO.getRequestMap();
            if (requestHashMap != null) {
                final ArrayList lastTransferList = (ArrayList) requestHashMap.get("TRANSFERLIST");
                if (lastTransferList != null && !lastTransferList.isEmpty()) {
                    for (int i = 0; i < lastTransferList.size(); i++) {
                        final C2STransferVO c2sTransferVO = (C2STransferVO) lastTransferList.get(i);
                        if (c2sTransferVO != null) {
                            sbf.append("<TXNDETAIL>");
                            sbf.append("<TXNID>").append(c2sTransferVO.getTransferID()).append("</TXNID>");
                            sbf.append("<TXNDATETIME>").append(BTSLDateUtil.getLocaleTimeStamp(getDateTime(c2sTransferVO.getTransferDateTime(), null))).append("</TXNDATETIME>");
                            sbf.append("<TRFTYPE>").append(c2sTransferVO.getType()).append("</TRFTYPE>");
                            sbf.append("<TXNSTATUS>").append(c2sTransferVO.getStatus()).append("</TXNSTATUS>");
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
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateExtLastXTrfResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateExtLastXTrfResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
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
        final String methodName = "generateExtCustomerEnqResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
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
            // Locale(defaultLanguage,defaultCountry);
            sbf.append("<MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments())).append("</MESSAGE>");

            sbf.append("<TXNDETAILS>");
            final HashMap requestHashMap = p_requestVO.getRequestMap();
            if (requestHashMap != null) {
                final ArrayList lastTransferList = (ArrayList) requestHashMap.get("TRANSFERLIST");
                if (lastTransferList != null && !lastTransferList.isEmpty()) {
                    for (int i = 0; i < lastTransferList.size(); i++) {
                        final C2STransferVO c2sTransferVO = (C2STransferVO) lastTransferList.get(i);
                        if (c2sTransferVO != null) {
                            sbf.append("<TXNDETAIL>");
                            sbf.append("<TXNID>").append(c2sTransferVO.getTransferID()).append("</TXNID>");
                            sbf.append("<TXNDATETIME>").append(BTSLDateUtil.getLocaleTimeStamp(getDateTime(c2sTransferVO.getTransferDateTime(), null))).append("</TXNDATETIME>");
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
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateExtCustomerEnqResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateExtCustomerEnqResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * This method generates CRBT Registration Response in XML format from
     * requestVO.
     * 
     * @param p_requestVO
     * @throws Exception
     */
    public static void generateCRBTRegistrationResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateCRBTRegistrationResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>CRBTACRESP</TYPE>");
            if (BTSLUtil.isNullString(p_requestVO.getTransactionID())) {
                sbf.append("<TXNID></TXNID>");
            } else {
                sbf.append("<TXNID>").append(p_requestVO.getTransactionID()).append("</TXNID>");
            }
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                String message = p_requestVO.getMessageCode();
                if (message.indexOf("_") != -1) {
                    message = message.substring(0, message.indexOf("_"));
                }
                sbf.append("<TXNSTATUS>").append(message).append("</TXNSTATUS>");

            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateCRBTRegistrationResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateChannelCreditTransferResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * This method generates CRBT Song Selection Response in XML format from
     * requestVO.
     * 
     * @param p_requestVO
     * @throws Exception
     */

    public static void generateCRBTSongSelectionResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateCRBTSongSelectionResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>CRBTSGRESP</TYPE>");
            if (BTSLUtil.isNullString(p_requestVO.getTransactionID())) {
                sbf.append("<TXNID></TXNID>");
            } else {
                sbf.append("<TXNID>").append(p_requestVO.getTransactionID()).append("</TXNID>");
            }
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                String message = p_requestVO.getMessageCode();
                if (message.indexOf("_") != -1) {
                    message = message.substring(0, message.indexOf("_"));
                }
                sbf.append("<TXNSTATUS>").append(message).append("</TXNSTATUS>");
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateCRBTSongSelectionResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateCRBTSongSelectionResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * This method generates Multiple dedicated account credit transfer Response
     * in XML format from requestVO
     * 
     * @param p_requestVO
     * @throws Exception
     * @Date 27/11/09
     */
    public static void generateP2PCRITResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateP2PCRITResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>CCMULTRFRES</TYPE>");
            // As per discussion with Karim, response should be 200
            sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateP2PCRITResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateP2PCRITResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * This method generates EVR Response in XML format from requestVO.
     * 
     * @param p_requestVO
     * @throws Exception
     * @author harpreet.kaur
     */
    public static void generateElectronicVoucherRechargeResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateElectronicVoucherRechargeResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>EVRTRFRESP</TYPE>");
            sbf.append("<TXNID>").append(p_requestVO.getTransactionID()).append("</TXNID>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateEVDResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateEVDResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * Method to generate Give Me Balance response for USSD
     * 
     * @param p_requestVO
     * @throws Exception
     */
    public static void generateGiveMeBalanceResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateGiveMeBalanceResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        OperatorUtilI operatorUtil = null;
        try {
            operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[initialize]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
        try {
            final String msisdn = operatorUtil.getOperatorFilteredMSISDN(PretupsBL.getFilteredMSISDN((String) p_requestVO.getRequestMap().get("MSISDN2")));
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>CGMBALRESP</TYPE>");
           
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
                
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
                
            }
            if(PretupsI.REQUEST_SOURCE_TYPE_EXTGW.equals(p_requestVO.getRequestGatewayType())){
            	sbf.append("<MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(),p_requestVO.getMessageArguments())).append("</MESSAGE>");	
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
         

        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateGiveMeBalanceResponse]",
                PretupsErrorCodesI.P2P_ERROR_EXCEPTION, "", "", "generateGiveMeBalanceResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * Method to generate lend me balance response
     * 
     * @param p_requestVO
     * @throws Exception
     * @author amit.singh
     */
    public static void generateLendMeBalanceResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateLendMeBalanceResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }

        String responseStr = null;
        StringBuffer sbf = null;
        HashMap requestMap = null;
        try {
            sbf = new StringBuffer(1024);
            requestMap = p_requestVO.getRequestMap();
            sbf.append("<?xml version=\"1.0\"?><COMMAND>");
            sbf.append("<TYPE>LMBRESP</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                String message = p_requestVO.getMessageCode();
                if (message.indexOf("_") != -1) {
                    message = message.substring(0, message.indexOf("_"));
                }
                sbf.append("<TXNSTATUS>").append(message).append("</TXNSTATUS>");
            }
            if (requestMap.get("NOTIFLANG") != null) {
                sbf.append("<NOTIFLANG>").append(requestMap.get("NOTIFLANG")).append("</NOTIFLANG>");
            } else {
                sbf.append("<NOTIFLANG></NOTIFLANG>");
            }
            if(PretupsI.REQUEST_SOURCE_TYPE_EXTGW.equals(p_requestVO.getRequestGatewayType())){
            	sbf.append("<MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(),p_requestVO.getMessageArguments())).append("</MESSAGE>");	
            } 
            
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateLendMeBalanceResponse]",
                PretupsErrorCodesI.P2P_ERROR_EXCEPTION, "", "", "generateLendMeBalanceResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }

        }
    }

    /**
     * @param p_requestVO
     * @throws Exception
     *             parse the voucher enquiry request
     */
    // to check for external network codes
    public static void parseVoucherEnqReq(RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "parseVoucherEnqReq";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        String extNwCode = null;
        String loginId = null;
        String extRefNumber = null;
        String extCode = null;
        String voucherType = null;
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<SUBID>");
            final String subsId = requestStr.substring(index + "<SUBID>".length(), requestStr.indexOf("</SUBID>", index));
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            index = requestStr.indexOf("<VTYPE>");
            if (index > 0) {
                voucherType = requestStr.substring(index + "<VTYPE>".length(), requestStr.indexOf("</VTYPE>", index));
            }

            try {
                index = requestStr.indexOf("<LOGINID>");
                loginId = requestStr.substring(index + "<LOGINID>".length(), requestStr.indexOf("</LOGINID>", index));
                index = requestStr.indexOf("<PASSWORD>");
                final String password = requestStr.substring(index + "<PASSWORD>".length(), requestStr.indexOf("</PASSWORD>", index));
                index = requestStr.indexOf("<EXTREFNUM>");
                extRefNumber = requestStr.substring(index + "<EXTREFNUM>".length(), requestStr.indexOf("</EXTREFNUM>", index));
                index = requestStr.indexOf("<EXTNWCODE>");
                extNwCode = requestStr.substring(index + "<EXTNWCODE>".length(), requestStr.indexOf("</EXTNWCODE>", index));
                index = requestStr.indexOf("<LANGUAGE1>");
                final String language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
                index = requestStr.indexOf("<LANGUAGE2>");
                final String language2 = requestStr.substring(index + "<LANGUAGE2>".length(), requestStr.indexOf("</LANGUAGE2>", index));
                index = requestStr.indexOf("<EXTCODE>");
                extCode = requestStr.substring(index + "<EXTCODE>".length(), requestStr.indexOf("</EXTCODE>", index));

            } catch (Exception e) {
                _log.error(methodName, "Exception e: " + e);
            }

            parsedRequestStr = VOMSI.SERVICE_TYPE_VOUCHER_ENQ + CHNL_MESSAGE_SEP + subsId + CHNL_MESSAGE_SEP + pin;

            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setExternalNetworkCode(extNwCode);
            p_requestVO.setSenderExternalCode(extCode);
            p_requestVO.setSenderLoginID(loginId);
            p_requestVO.setExternalReferenceNum(extRefNumber);
            p_requestVO.setReceiverMsisdn(subsId);
            p_requestVO.setVoucherType(voucherType);
        } catch (Exception e) {
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("XMLStringParser", "parseGetAccountInfoRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * this is used to construct response mesage for voucher enq
     * 
     * @param p_requestVO
     *            RequestVO
     * @return responseStr java.lang.String
     */
    public static void generateVoucherEnqResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateVoucherEnqResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>VOUENQRESP</TYPE>");
            sbf.append("<TXNSTATUS>").append(p_requestVO.getVomsMessage()).append("</TXNSTATUS>");
            sbf.append("<SNO>").append(p_requestVO.getSerialNo()).append("</SNO>");
            sbf.append("<TOPUP>").append(p_requestVO.getVoucherAmount()).append("</TOPUP>");
            sbf.append("<SUBID>").append(p_requestVO.getReceiverMsisdn()).append("</SUBID>");
            sbf.append("<REGION>").append(p_requestVO.getVomsRegion()).append("</REGION>");
            sbf.append("<VALID>").append(p_requestVO.getVomsValid()).append("</VALID>");
            // sbf.append("<MESSAGE>"+p_requestVO.getVomsMessage()+"</MESSAGE>");
            sbf.append("<ERROR>").append(p_requestVO.getVomsError()).append("</ERROR>");
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateVoucherEnqResponse]",
                PretupsErrorCodesI.P2P_ERROR_EXCEPTION, "", "", "generateVoucherEnqResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * @param p_requestVO
     * @throws Exception
     *             to parse voucher consumption request
     */
    public static void parseVoucherConsReq(RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "parseVoucherConsReq";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        String extNwCode = null;
        String loginId = null;
        String extRefNumber = null;
        String extCode = null;
        String voucherType = null;
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            index = requestStr.indexOf("<VTYPE>");
            if (index > 0) {
                voucherType = requestStr.substring(index + "<VTYPE>".length(), requestStr.indexOf("</VTYPE>", index));
            }

            try {
                index = requestStr.indexOf("<SUBID>");
                final String subsId = requestStr.substring(index + "<SUBID>".length(), requestStr.indexOf("</SUBID>", index));
                index = requestStr.indexOf("<LOGINID>");
                loginId = requestStr.substring(index + "<LOGINID>".length(), requestStr.indexOf("</LOGINID>", index));
                index = requestStr.indexOf("<PASSWORD>");
                final String password = requestStr.substring(index + "<PASSWORD>".length(), requestStr.indexOf("</PASSWORD>", index));
                index = requestStr.indexOf("<EXTREFNUM>");
                extRefNumber = requestStr.substring(index + "<EXTREFNUM>".length(), requestStr.indexOf("</EXTREFNUM>", index));
                index = requestStr.indexOf("<EXTNWCODE>");
                extNwCode = requestStr.substring(index + "<EXTNWCODE>".length(), requestStr.indexOf("</EXTNWCODE>", index));
                index = requestStr.indexOf("<LANGUAGE1>");
                final String language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
                index = requestStr.indexOf("<LANGUAGE2>");
                final String language2 = requestStr.substring(index + "<LANGUAGE2>".length(), requestStr.indexOf("</LANGUAGE2>", index));
                index = requestStr.indexOf("<EXTCODE>");
                extCode = requestStr.substring(index + "<EXTCODE>".length(), requestStr.indexOf("</EXTCODE>", index));
            } catch (Exception e) {
                _log.error(methodName, "Exception e: " + e);
            }
            parsedRequestStr = VOMSI.SERVICE_TYPE_VOUCHER_CON + CHNL_MESSAGE_SEP + pin;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setExternalNetworkCode(extNwCode);
            p_requestVO.setSenderExternalCode(extCode);
            p_requestVO.setSenderLoginID(loginId);
            p_requestVO.setExternalReferenceNum(extRefNumber);
            p_requestVO.setVoucherType(voucherType);
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
     * this is used to construct response mesage for voucher enq
     * 
     * @param p_requestVO
     *            RequestVO
     * @return responseStr java.lang.String
     */
    public static void generateVoucherConsResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateVoucherEnqResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>VOUCONSRESP</TYPE>");
            sbf.append("<TXNSTATUS>").append(p_requestVO.getVomsMessage()).append("</TXNSTATUS>");
            sbf.append("<SNO>").append(p_requestVO.getSerialNo()).append("</SNO>");
            sbf.append("<TOPUP>").append(p_requestVO.getVoucherAmount()).append("</TOPUP>");
            sbf.append("<COMSUMED>").append(p_requestVO.getConsumed()).append("</COMSUMED>");
            // sbf.append("<MESSAGE>"+p_requestVO.getVomsMessage()+"</MESSAGE>");
            sbf.append("<ERROR>").append(p_requestVO.getVomsError()).append("</ERROR>");
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateVoucherEnqResponse]",
                PretupsErrorCodesI.P2P_ERROR_EXCEPTION, "", "", "generateVoucherEnqResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    public static void generateResponseP2PMCDAddModifyDeleteRequest(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateResponseP2PMCDAddModifyDeleteRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>SCLAMRESP</TYPE>");
            // sbf.append("<TXNSTATUS>"+PretupsI.TXN_STATUS_SUCCESS+"</TXNSTATUS>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "XMLStringParser[generateResponseP2PMCDAddModifyDeleteRequest]", PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "",
                "generateResponseP2PMCDAddModifyDeleteRequest:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    public static void generateResponseP2PMCDListViewRequest(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateResponseP2PMCDListViewRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        String responseMessage = null;
        String[] responseMessageArray = null;
        try {
            // if(!BTSLUtil.isNullString(p_requestVO.getSenderReturnMessage()))
            if (p_requestVO.isSuccessTxn()) {
                sbf = new StringBuffer(1024);
                sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
                sbf.append("<TYPE>SCLVRESP</TYPE>");
                sbf.append("<LISTRECORDS>");
                sbf.append("<LISTNAME>").append(p_requestVO.getMcdListName()).append("</LISTNAME>");
                responseMessage = p_requestVO.getSenderReturnMessage();
                responseMessageArray = responseMessage.split(",");
                for (int i = 0; i < responseMessageArray.length; i++) {
                    final String responseSubMessage = responseMessageArray[i];
                    final String[] responseSubMessageArray = responseSubMessage.split(":");
                    final int selecAmountCount = (responseSubMessageArray.length - 1) / 2;
                    /*
                     * if(responseSubMessageArray.length==5)
                     * {
                     * sbf.append("<LISTRECORD>");
                     * sbf.append("<MSISDN1>"+responseSubMessageArray[0]+
                     * "</MSISDN1>");
                     * sbf.append("<SELECTOR1>"+responseSubMessageArray[1]+
                     * "</SELECTOR1>");
                     * sbf.append("<AMOUNT1>"+responseSubMessageArray[2]+
                     * "</AMOUNT1>");
                     * sbf.append("<SELECTOR2>"+responseSubMessageArray[3]+
                     * "</SELECTOR2>");
                     * sbf.append("<AMOUNT2>"+responseSubMessageArray[4]+
                     * "</AMOUNT2>");
                     * sbf.append("</LISTRECORD>");
                     * }
                     */

                    sbf.append("<LISTRECORD>");
                    sbf.append("<MSISDN1>").append(responseSubMessageArray[0]).append("</MSISDN1>");
                    for (int j = 1, k = 1; j <= selecAmountCount; j++) {
                        sbf.append("<SELECTOR" + j + ">").append(responseSubMessageArray[k]).append("</SELECTOR" + j + ">");
                        sbf.append("<AMOUNT" + j + ">").append(responseSubMessageArray[k + 1]).append("</AMOUNT" + j + ">");
                        k = k + 2;
                    }
                    sbf.append("</LISTRECORD>");

                }
                sbf.append("</LISTRECORDS>");
                sbf.append("</COMMAND>");
                responseStr = sbf.toString();
                p_requestVO.setSenderReturnMessage(responseStr);
            } else {
                sbf = new StringBuffer(1024);
                sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
                sbf.append("<TYPE>SCLVRESP</TYPE>");
                // sbf.append("<TXNSTATUS>"+PretupsI.TXN_STATUS_SUCCESS+"</TXNSTATUS>");
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
                sbf.append("</COMMAND>");
                responseStr = sbf.toString();
                p_requestVO.setSenderReturnMessage(responseStr);
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "XMLStringParser[generateResponseP2PMCDListViewRequest]", PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateResponseP2PMCDListViewRequest:" + e
                    .getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    public static void generateResponseP2PMCDListCreditRequest(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateResponseP2PMCDListCreditRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>SCLTRFRESP</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "XMLStringParser[generateResponseP2PMCDListCreditRequest]", PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateResponseP2PMCDListCreditRequest:" + e
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
    public static void generateChannelVasTransferResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateChannelVasTransferResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>VASRFRESP</TYPE>");
            if (BTSLUtil.isNullString(p_requestVO.getTransactionID())) {
                sbf.append("<TXNID></TXNID>");
            } else {
                sbf.append("<TXNID>").append(p_requestVO.getTransactionID()).append("</TXNID>");
            }
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                String message = p_requestVO.getMessageCode();
                if (message.indexOf("_") != -1) {
                    message = message.substring(0, message.indexOf("_"));
                }
                sbf.append("<TXNSTATUS>").append(message).append("</TXNSTATUS>");
            }
            if (!p_requestVO.isSuccessTxn()) {
                sbf.append("<MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments())).append("</MESSAGE>");
            } else {
                sbf.append("<MESSAGE>").append(getMessage(p_requestVO.getLocale(), PretupsErrorCodesI.VAST_SENDER_SUCCESS, p_requestVO.getMessageArguments())).append("</MESSAGE>");
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateChannelVasTransferResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", ":" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    public static void generateChannelPrVasTransferResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateChannelPrVasTransferResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>PRVASRFRESP</TYPE>");
            if (BTSLUtil.isNullString(p_requestVO.getTransactionID())) {
                sbf.append("<TXNID></TXNID>");
            } else {
                sbf.append("<TXNID>").append(p_requestVO.getTransactionID()).append("</TXNID>");
            }
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                String message = p_requestVO.getMessageCode();
                if (message.indexOf("_") != -1) {
                    message = message.substring(0, message.indexOf("_"));
                }
                sbf.append("<TXNSTATUS>").append(message).append("</TXNSTATUS>");
            }
            if (!p_requestVO.isSuccessTxn()) {
                sbf.append("<MESSAGE>").append(getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments())).append("</MESSAGE>");
            } else {
                sbf.append("<MESSAGE>").append(getMessage(p_requestVO.getLocale(), PretupsErrorCodesI.VAST_SENDER_SUCCESS, p_requestVO.getMessageArguments())).append("</MESSAGE>");
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", ":" + e.getMessage());
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
     * @author rahul.d
     * @Date 22/6/12
     */
    public static void generateC2SLastXTransferStatusResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateC2SLastXTransferStatusResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>LXC2STSRESP</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
            }
            sbf.append("</COMMAND>");
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

    // Changes end for Ext geography API

    /**
     * 
     */
    public static void parseExtTrfRuleTypeRequest(RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "parseExtTrfRuleTypeRequest";
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
            String extNwCode = null;
            String date = null;
            String loginId = null;
            String extCode = null;

            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            requestHashMap.put("TYPE", type);
            index = requestStr.indexOf("<DATE>");
            if (index > 0) {
                date = requestStr.substring(index + "<DATE>".length(), requestStr.indexOf("</DATE>", index));
                date = BTSLDateUtil.getGregorianDateInString(date);
                if (!BTSLUtil.isValidDatePattern(date)) {
                    p_requestVO.setMessageCode(PretupsErrorCodesI.EXT_TRF_RULE_TYPE_INVALID_DATE);
                    final String args[] = { systemDateFormat };
                    throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.EXT_TRF_RULE_TYPE_INVALID_DATE, args);
                }
                requestHashMap.put("DATE", date);
            }

            index = requestStr.indexOf("<EXTNWCODE>");
            extNwCode = requestStr.substring(index + "<EXTNWCODE>".length(), requestStr.indexOf("</EXTNWCODE>", index));
            requestHashMap.put("EXTNWCODE", extNwCode);

            index = requestStr.indexOf("<EXTREFNUM>");
            if (index > 0) {
                extRefNumber = requestStr.substring(index + "<EXTREFNUM>".length(), requestStr.indexOf("</EXTREFNUM>", index));
                requestHashMap.put("EXTREFNUM", extRefNumber);
            }
            index = requestStr.indexOf("<MSISDN>");
            if (index > 0) {
                msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
                requestHashMap.put("MSISDN", msisdn);
            }
            index = requestStr.indexOf("<LOGINID>");
            if (index > 0) {
                loginId = requestStr.substring(index + "<LOGINID>".length(), requestStr.indexOf("</LOGINID>", index));
                requestHashMap.put("LOGINID", loginId);
            }

            index = requestStr.indexOf("<CATCODE>");
            final String catCode = requestStr.substring(index + "<CATCODE>".length(), requestStr.indexOf("</CATCODE>", index));
            requestHashMap.put("CATCODE", catCode);

            index = requestStr.indexOf("<EXTCODE>");
            if (index > 0) {
                extCode = requestStr.substring(index + "<EXTCODE>".length(), requestStr.indexOf("</EXTCODE>", index));
                requestHashMap.put("EXTCODE", extCode);
            }
            /*
             * if(BTSLUtil.isNullString(msisdn) &&
             * BTSLUtil.isNullString(loginId) && BTSLUtil.isNullString(extCode))
             * {
             * throw new
             * BTSLBaseException("XMLStringParser","parseExtTrfRuleTypeRequest"
             * ,PretupsErrorCodesI
             * .EXT_XML_ERROR_MSISDN_LOGINID_EXTCODE_ALL_MISSING);
             * }
             */
            if (BTSLUtil.isNullString(type) || BTSLUtil.isNullString(extNwCode) || BTSLUtil.isNullString(catCode)) {
                throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.EXT_XML_ERROR_MISSING_MANDATORY_VALUE);
            }
            parsedRequestStr = PretupsI.SERVICE_TYPE_TRF_RULE_TYPE + CHNL_MESSAGE_SEP + loginId;
            p_requestVO.setExternalNetworkCode(extNwCode);
            p_requestVO.setSenderLoginID(loginId);
            p_requestVO.setRequestMap(requestHashMap);
            p_requestVO.setExternalReferenceNum(extRefNumber);
            p_requestVO.setDecryptedMessage(parsedRequestStr);
        } catch (BTSLBaseException be) {
            _log.error(methodName, "BTSLBaseException e: " + be);
            throw be;
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
     * parseExtUserRequest
     * Parse the response for User Creation request from external system
     * 
     * @param RequestVO
     * @throws Exception
     * @author ankur.dhawan
     */

    public static void parseExtUserRequest(RequestVO p_requestVO) throws BTSLBaseException, Exception {
        final String methodName = "parseExtUserRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        try {
            String parsedRequestStr = null;
            HashMap requestHashMap = p_requestVO.getRequestMap();
            if (requestHashMap == null) {
                requestHashMap = new HashMap();
            }

            final String requestStr = p_requestVO.getRequestMessage();
            String type = null;
            String date = null;
            String extNwCode = null;
            String extRefNumber = null;
            String catCode = null;
            String msisdn = null;
            String loginId = null;
            String extCode = null;
            String geoCode = null;
            String optLoginId = null;
            String rsa = null;
            String ssn = null;
            String userName = null;
            String ownerMsisdn = null;
            String parentMsisdn = null;
            String empCode = null;
            String ruleType = null;

            int index = requestStr.indexOf("<TYPE>");
            type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            requestHashMap.put("TYPE", type);

            index = requestStr.indexOf("<DATE>");
            if (index > 0) {
                date = requestStr.substring(index + "<DATE>".length(), requestStr.indexOf("</DATE>", index));
                date = BTSLDateUtil.getGregorianDateInString(date);
                if (!BTSLUtil.isValidDatePattern(date)) {
                    p_requestVO.setMessageCode(PretupsErrorCodesI.EXT_USRADD_INVALID_DATE);
                    final String args[] = { systemDateFormat };
                    throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.EXT_USRADD_INVALID_DATE, args);
                }
                requestHashMap.put("DATE", date);
            }

            index = requestStr.indexOf("<EXTNWCODE>");
            extNwCode = requestStr.substring(index + "<EXTNWCODE>".length(), requestStr.indexOf("</EXTNWCODE>", index));
            requestHashMap.put("EXTNWCODE", extNwCode);

            index = requestStr.indexOf("<EXTREFNUM>");
            if (index > 0) {
                extRefNumber = requestStr.substring(index + "<EXTREFNUM>".length(), requestStr.indexOf("</EXTREFNUM>", index));
                requestHashMap.put("EXTREFNUM", extRefNumber);
            }
            p_requestVO.setExternalReferenceNum(extRefNumber);

            index = requestStr.indexOf("<OPTLOGINID>");
            optLoginId = requestStr.substring(index + "<OPTLOGINID>".length(), requestStr.indexOf("</OPTLOGINID>", index));
            requestHashMap.put("OPTLOGINID", optLoginId.trim());

            index = requestStr.indexOf("<CATCODE>");
            catCode = requestStr.substring(index + "<CATCODE>".length(), requestStr.indexOf("</CATCODE>", index));
            requestHashMap.put("CATCODE", catCode.trim());

            index = requestStr.indexOf("<OWNERMSISDN>");
            if (index > 0) {
                ownerMsisdn = requestStr.substring(index + "<OWNERMSISDN>".length(), requestStr.indexOf("</OWNERMSISDN>", index));
                requestHashMap.put("OWNERMSISDN", ownerMsisdn.trim());
            }

            index = requestStr.indexOf("<PARENTMSISDN>");
            if (index > 0) {
                parentMsisdn = requestStr.substring(index + "<PARENTMSISDN>".length(), requestStr.indexOf("</PARENTMSISDN>", index));
                requestHashMap.put("PARENTMSISDN", parentMsisdn.trim());
            }

            index = requestStr.indexOf("<MSISDN>");
            msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
            requestHashMap.put("MSISDN", msisdn.trim());

            index = requestStr.indexOf("<USERNAME>");
            userName = requestStr.substring(index + "<USERNAME>".length(), requestStr.indexOf("</USERNAME>", index));
            requestHashMap.put("USERNAME", userName.trim());

            index = requestStr.indexOf("<LOGINID>");
            if (index > 0) {
                loginId = requestStr.substring(index + "<LOGINID>".length(), requestStr.indexOf("</LOGINID>", index));
                requestHashMap.put("LOGINID", loginId.trim());
            }

            index = requestStr.indexOf("<EMPCODE>");
            if (index > 0) {
                empCode = requestStr.substring(index + "<EMPCODE>".length(), requestStr.indexOf("</EMPCODE>", index));
                requestHashMap.put("EMPCODE", empCode.trim());
            }

            index = requestStr.indexOf("<RSA>");
            if (index > 0) {
                rsa = requestStr.substring(index + "<RSA>".length(), requestStr.indexOf("</RSA>", index));
                requestHashMap.put("RSA", rsa.trim());
            }

            index = requestStr.indexOf("<SSN>");
            if (index > 0) {
                ssn = requestStr.substring(index + "<SSN>".length(), requestStr.indexOf("</SSN>", index));
                requestHashMap.put("SSN", ssn.trim());
            }

            index = requestStr.indexOf("<EXTCODE>");
            if (index > 0) {
                extCode = requestStr.substring(index + "<EXTCODE>".length(), requestStr.indexOf("</EXTCODE>", index));
                requestHashMap.put("EXTCODE", extCode.trim());
            }

            index = requestStr.indexOf("<GEOGRAPHY>");
            geoCode = requestStr.substring(index + "<GEOGRAPHY>".length(), requestStr.indexOf("</GEOGRAPHY>", index));
            requestHashMap.put("GEOGRAPHY", geoCode.trim());

            index = requestStr.indexOf("<RULETYPE>");
            ruleType = requestStr.substring(index + "<RULETYPE>".length(), requestStr.indexOf("</RULETYPE>", index));
            requestHashMap.put("RULETYPE", ruleType.trim());

            if (BTSLUtil.isNullString(type) || BTSLUtil.isNullString(optLoginId) || BTSLUtil.isNullString(extNwCode)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.EXT_USRADD_ERROR_MISSING_MANDATORY_FIELDS);
                throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.EXT_USRADD_ERROR_MISSING_MANDATORY_FIELDS);
            }
            if (BTSLUtil.isNullString(catCode) || BTSLUtil.isNullString(msisdn) || BTSLUtil.isNullString(userName) || BTSLUtil.isNullString(geoCode)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.EXT_USRADD_ERROR_MISSING_USER_DETAILS);
                throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.EXT_USRADD_ERROR_MISSING_USER_DETAILS);
            }
            Boolean isTrfRuleUserlevelAllow = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW);
            if (isTrfRuleUserlevelAllow && BTSLUtil.isNullString(ruleType)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.EXT_USRADD_RULETYPE_MANADATORY);
                throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.EXT_USRADD_RULETYPE_MANADATORY);
            }
            parsedRequestStr = PretupsI.SERVICE_TYPE_EXT_USR_ADD + CHNL_MESSAGE_SEP + optLoginId;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMap(requestHashMap);
            p_requestVO.setExternalNetworkCode(extNwCode);
            // p_requestVO.setSenderLoginID(optLoginId);
        } catch (BTSLBaseException be) {
            _log.error(methodName, "BTSLBaseException e: " + be);
            throw be;
        } catch (StringIndexOutOfBoundsException sbe) {
            _log.errorTrace(methodName, sbe);
            p_requestVO.setMessageCode(PretupsErrorCodesI.EXT_USRADD_INVALID_MESSAGE_FORMAT);
            _log.error(methodName, "Exception e: " + sbe);
            throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.EXT_USRADD_INVALID_MESSAGE_FORMAT);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.EXT_USRADD_INVALID_MESSAGE_FORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.EXT_USRADD_INVALID_MESSAGE_FORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO ID: " + p_requestVO.toString());
            }
        }
    }

    /**
     * @param p_requestVO
     * @throws Exception
     * @author priyanka.goel
     *         Method generateChannelUserAuthResponse() generate the response of
     *         User Authentication XML API used for USSD System
     */
    public static void generateChannelUserAuthResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateChannelUserAuthResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        final java.util.Date date = new java.util.Date();
        try {
        	String EXTERNAL_DATE_FORMAT = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_DATE_FORMAT);
            final SimpleDateFormat sdf = new SimpleDateFormat(Constants.getProperty("EXTERNAL_USER_DATETIME_FORMAT"));
            sdf.setLenient(false);
            sbf = new StringBuffer(1024);
            //sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<?xml version=\"1.0\"?><COMMAND>");
            sbf.append("<TYPE>").append(PretupsI.USER_AUTH_XML_RESP).append("</TYPE>");
            sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
            final String senderMessage = getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
            sbf.append("<MESSAGE>").append(senderMessage).append("</MESSAGE>");
            sbf.append("<DATE>").append(BTSLDateUtil.getSystemLocaleDate(sdf.format(date),EXTERNAL_DATE_FORMAT)).append("</DATE>");
            if (!BTSLUtil.isNullString(p_requestVO.getExternalReferenceNum())) {
                sbf.append("<EXTREFNUM>").append(p_requestVO.getExternalReferenceNum()).append("</EXTREFNUM>");
            } else {
                sbf.append("<EXTREFNUM>NOT APPLICABLE</EXTREFNUM>");
            }

            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateChannelUserAuthResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateChannelUserAuthResponse:" + e.getMessage());
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
    public static void generateSIMACTResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateSIMACTResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        final java.util.Date date = new java.util.Date();
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DDMMYYYYHHMMSS);
            sdf.setLenient(false);
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>").append(PretupsI.SIM_ACTIVATE_RESP).append("</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
            }
            final String senderMessage = getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
            sbf.append("<MESSAGE>").append(senderMessage).append("</MESSAGE>");
            if (BTSLUtil.isNullString(p_requestVO.getTransactionID())) {
                sbf.append("<TXNID></TXNID>");
            } else {
                sbf.append("<TXNID>").append(p_requestVO.getTransactionID()).append("</TXNID>");
            }
            sbf.append("<DATE>").append(BTSLDateUtil.getSystemLocaleDate(sdf.format(date), externalDateFormat)).append("</DATE>");
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateSIMACTResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateSIMACTResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * @param p_requestVO
     * @throws Exception
     * @author sonali.garg
     */
    public static void parseChannelExtSubscriberEnqRequest(RequestVO p_requestVO) throws Exception {
        final String methodName = "parseChannelExtSubscriberEnqRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;

        try {
            final HashMap requestHashMap = new HashMap();
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            requestHashMap.put("TYPE", type);

            index = requestStr.indexOf("<EXTNWCODE>");
            final String extNwCode = requestStr.substring(index + "<EXTNWCODE>".length(), requestStr.indexOf("</EXTNWCODE>", index));
            requestHashMap.put("EXTNWCODE", extNwCode);

            index = requestStr.indexOf("<MSISDN1>");
            final String msisdn1 = requestStr.substring(index + "<MSISDN1>".length(), requestStr.indexOf("</MSISDN1>", index));
            requestHashMap.put("MSISDN1", msisdn1);

            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            requestHashMap.put("PIN", pin);

            index = requestStr.indexOf("<EXTCODE>");
            final String extCode = requestStr.substring(index + "<EXTCODE>".length(), requestStr.indexOf("</EXTCODE>", index));
            requestHashMap.put("EXTCODE", extCode);

            index = requestStr.indexOf("<EXTTXNNUMBER>");
            final String extTxnNumber = requestStr.substring(index + "<EXTTXNNUMBER>".length(), requestStr.indexOf("</EXTTXNNUMBER>", index));
            requestHashMap.put("EXTTXNNUMBER", extTxnNumber);

            index = requestStr.indexOf("<EXTTXNDATE>");
            String extDate = requestStr.substring(index + "<EXTTXNDATE>".length(), requestStr.indexOf("</EXTTXNDATE>", index));
            extDate = BTSLDateUtil.getGregorianDateInString(extDate);
            requestHashMap.put("EXTTXNDATE", extDate);

            index = requestStr.indexOf("<MSISDN2>");
            final String msisdn2 = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));
            requestHashMap.put("MSISDN2", msisdn2);

            index = requestStr.indexOf("<SERVICETYPE>");
            final String enquiryServiceType = requestStr.substring(index + "<SERVICETYPE>".length(), requestStr.indexOf("</SERVICETYPE>", index));
            requestHashMap.put("SERVICETYPE", enquiryServiceType);

            index = requestStr.indexOf("<REMARKS>");
            final String remarks = requestStr.substring(index + "<REMARKS>".length(), requestStr.indexOf("</REMARKS>", index));
            requestHashMap.put("REMARKS", remarks);

            index = requestStr.indexOf("<SELECTOR>");
            String selector = requestStr.substring(index + "<SELECTOR>".length(), requestStr.indexOf("</SELECTOR>", index));
            parsedRequestStr = PretupsI.SERVICE_TYPE_SUBSCRIBER_ENQUIRY + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + enquiryServiceType;

            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setReceiverMsisdn(msisdn2);
            p_requestVO.setExternalNetworkCode(extNwCode);
            p_requestVO.setSenderExternalCode(extCode);
            p_requestVO.setExternalTransactionNum(extTxnNumber);
            p_requestVO.setRemarks(remarks);
            p_requestVO.setRequestMSISDN(msisdn1);
            p_requestVO.setEnquiryServiceType(enquiryServiceType);
            // here requestType is used to differentiate whether it's for
            // prepaid or postpaid
            // if requestType=RC =>Prepaid else if type=PPB => Postpaid
            if (enquiryServiceType.equals(PretupsI.SERVICE_TYPE_CHNL_RECHARGE)) {
                p_requestVO.setEnquiryServiceType("RC");
                if (BTSLUtil.isNullString(selector)) {
                    final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache
                        .getDefaultSelectorForServiceType(PretupsI.SERVICE_TYPE_CHNL_RECHARGE);
                    if (serviceSelectorMappingVO != null) {
                        selector = serviceSelectorMappingVO.getSelectorCode();
                    }
                }
            } else if (enquiryServiceType.equals(PretupsI.SERVICE_TYPE_CHNL_BILLPAY)) {
                p_requestVO.setEnquiryServiceType("PPB");
                if (BTSLUtil.isNullString(selector)) {
                    final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(PretupsI.SERVICE_TYPE_CHNL_BILLPAY);
                    if (serviceSelectorMappingVO != null) {
                        selector = serviceSelectorMappingVO.getSelectorCode();
                    }
                }
            }

            p_requestVO.setReqSelector(selector);
            p_requestVO.setRequestMap(requestHashMap);

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
     * This method generates response for Schedulded Credit Transfer List
     * (Add/Modify/Delete) Request
     * 
     * @param p_requestVO
     * @throws Exception
     * @author Harsh Dixit
     * @date 22/04/2013
     */

    public static void generateResponseP2PSMCDAddModifyDeleteRequest(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateResponseP2PSMCDAddModifyDeleteRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>PSCTAMRESP</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<LISTNAME>").append(p_requestVO.getMcdListName()).append("</LISTNAME>");
                sbf.append("<SCTYPE>").append(p_requestVO.getMcdScheduleType()).append("</SCTYPE>");
                if (!"FA".equals(p_requestVO.getMcdListStatus()) && !"D".equals(p_requestVO.getAction())) {
                    sbf.append("<SCDATE>").append(BTSLDateUtil.getSystemLocaleDate(p_requestVO.getMcdNextScheduleDate())).append("</SCDATE>");
                    sbf.append("<NOSC>").append(p_requestVO.getMcdNoOfSchedules()).append("</NOSC>");
                }
                sbf.append("<LISTSTATUS>").append(p_requestVO.getMcdListStatus()).append("</LISTSTATUS>");
                sbf.append("<FAILREC>").append(p_requestVO.getMcdFailRecords()).append("</FAILREC>");
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "XMLStringParser[generateResponseP2PMCDAddModifyDeleteRequest]", PretupsErrorCodesI.C2S_ERROR_EXCEPTION, "", "",
                "generateResponseP2PMCDAddModifyDeleteRequest:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
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
     * @author vikas.kumar
     */
    public static void generateScheduleCreditTransferResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateScheduleCreditTransferResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>SHCCTRFRESP</TYPE>");
            if (BTSLUtil.isNullString(p_requestVO.getTransactionID())) {
                sbf.append("<TXNID></TXNID>");
            } else {
                sbf.append("<TXNID>").append(p_requestVO.getTransactionID()).append("</TXNID>");
            }
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "XMLStringParser[generateScheduleCreditTransferResponse]", PretupsErrorCodesI.P2P_ERROR_EXCEPTION, "", "", "generateScheduleCreditTransferResponse:" + e
                    .getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * this method construct Scheduled Credit Transfer Delete response in XML
     * format from requestVO
     * 
     * @param p_requestVO
     *            RequestVO
     * @return responseStr java.lang.String
     * @author Pradyumn.Mishra
     */
    public static void generateResponseP2PSMCDDeleteListRequest(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateResponseP2PSMCDDeleteListRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            sbf.append("<TYPE>PSCTDRESP</TYPE>");
            sbf.append("<LISTNAME>").append(p_requestVO.getMcdListName()).append("</LISTNAME>");
            if (p_requestVO.isSuccessTxn()) {

                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error("generateResponseP2PMCDAddModifyDeleteRequest", "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "XMLStringParser[generateResponseP2PMCDAddModifyDeleteRequest]", PretupsErrorCodesI.C2S_ERROR_EXCEPTION, "", "",
                "generateResponseP2PMCDAddModifyDeleteRequest:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("generateResponseP2PMCDAddModifyDeleteRequest", "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * this method construct Scheduled Credit Transfer List View response in XML
     * format from requestVO
     * 
     * @param p_requestVO
     *            RequestVO
     * @return responseStr java.lang.String
     * @author Pradyumn.Mishra
     */
    public static void generateResponseP2PSMCDViewRequest(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateResponseP2PSMCDViewRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        String responseMessage = null;
        String[] responseMessageArray = null;
        final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.DATE_FORMAT_DDMMYYYY);
        try {
            // if(!BTSLUtil.isNullString(p_requestVO.getSenderReturnMessage()))
            if (p_requestVO.isSuccessTxn()) {
                sbf = new StringBuffer(1024);
                sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
                sbf.append("<TYPE>PSCTVRESP</TYPE>");
                sbf.append("<LISTRECORDS>");
                sbf.append("<LISTNAME>").append(p_requestVO.getMcdListName()).append("</LISTNAME>");
                sbf.append("<SCTYPE>").append(p_requestVO.getMcdScheduleType()).append("</SCTYPE>");
                sbf.append("<SCDATE>").append(BTSLDateUtil.getSystemLocaleDate(sdf.format(p_requestVO.getExecutedUpto()), externalDateFormat)).append("</SCDATE>");
                sbf.append("<NOSC>").append(p_requestVO.getMcdNoOfSchedules()).append("</NOSC>");
                responseMessage = p_requestVO.getSenderReturnMessage();
                responseMessageArray = responseMessage.split(",");
                for (int i = 0; i < responseMessageArray.length; i++) {
                    final String responseSubMessage = responseMessageArray[i];
                    final String[] responseSubMessageArray = responseSubMessage.split(":");
                    sbf.append("<LISTRECORD>");
                    sbf.append("<MSISDN1>").append(responseSubMessageArray[0]).append("</MSISDN1>");
                    sbf.append("<AMOUNT>").append(responseSubMessageArray[1]).append("</AMOUNT>");
                    sbf.append("</LISTRECORD>");
                }
                sbf.append("</LISTRECORDS>");
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
                sbf.append("</COMMAND>");
                responseStr = sbf.toString();
                p_requestVO.setSenderReturnMessage(responseStr);
            } else {
                sbf = new StringBuffer(1024);
                sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
                sbf.append("<TYPE>PSCTVRESP</TYPE>");
                sbf.append("<LISTRECORDS>");
                sbf.append("<LISTNAME>NA</LISTNAME>");
                sbf.append("<SCTYPE>NA</SCTYPE>");
                sbf.append("<SCDATE>NA</SCDATE>");
                sbf.append("<NOSC>NA</NOSC>");
                sbf.append("</LISTRECORDS>");
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
                sbf.append("</COMMAND>");
                responseStr = sbf.toString();
                p_requestVO.setSenderReturnMessage(responseStr);
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error("generateResponseP2PSMCDListViewRequest", "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "XMLStringParser[generateResponseP2PMCDListViewRequest]", PretupsErrorCodesI.C2S_ERROR_EXCEPTION, "", "", "generateResponseP2PMCDListViewRequest:" + e
                    .getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("generateResponseP2PSMCDListViewRequest", "Exiting responseStr: " + responseStr);
            }
        }
    }

    public static void generateChannelHelpDeskResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateChannelHelpDeskResponse";
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
            sbf.append("<TYPE>HLPDSKRSP</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                String message = p_requestVO.getMessageCode();
                if (message.indexOf("_") != -1) {
                    message = message.substring(0, message.indexOf("_"));
                }
                sbf.append("<TXNSTATUS>").append(message).append("</TXNSTATUS>");
            }

            /*
             * sbf.append("<DATE>"+sdf.format(date)+"</DATE>");
             * HashMap requestHashMap=p_requestVO.getRequestMap();
             * if(!p_requestVO.isSuccessTxn())
             * sbf.append("<MESSAGE>"+getMessage(p_requestVO.getLocale(),p_requestVO
             * .
             * getMessageCode(),p_requestVO.getMessageArguments())+"</MESSAGE>")
             * ;
             * else
             * sbf.append("<MESSAGE>"+getMessage(p_requestVO.getLocale(),p_requestVO
             * .
             * getMessageCode(),p_requestVO.getMessageArguments())+"</MESSAGE>")
             * ;
             * //sbf.append("<MESSAGE></MESSAGE>");
             */sbf.append("</COMMAND>");
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

    // VFE PPB Enquiry
    public static void parseChannelExtPPBEnqRequest(RequestVO p_requestVO) throws Exception {
        final String methodName = "parseChannelExtPPBEnqRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        try {
            final HashMap requestHashMap = new HashMap();
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
            requestHashMap.put("PASSWORD", password);
            index = requestStr.indexOf("<EXTCODE>");
            final String extCode = requestStr.substring(index + "<EXTCODE>".length(), requestStr.indexOf("</EXTCODE>", index));
            requestHashMap.put("EXTCODE", extCode);
            index = requestStr.indexOf("<EXTREFNUM>");
            final String extRefNumber = requestStr.substring(index + "<EXTREFNUM>".length(), requestStr.indexOf("</EXTREFNUM>", index));
            requestHashMap.put("EXTREFNUM", extRefNumber);
            index = requestStr.indexOf("<MSISDN2>");
            final String msisdn2 = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));
            index = requestStr.indexOf("<LANGUAGE1>");
            final String language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
            index = requestStr.indexOf("<LANGUAGE2>");
            final String language2 = requestStr.substring(index + "<LANGUAGE2>".length(), requestStr.indexOf("</LANGUAGE2>", index));
            if (language1 != null && language1 != null) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_PPB_ENQUIRY + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + language1 + CHNL_MESSAGE_SEP + language2;
            } else if (language1 != null) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_PPB_ENQUIRY + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + language1;
            } else {
                parsedRequestStr = PretupsI.SERVICE_TYPE_PPB_ENQUIRY + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP;
            }

            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
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

    public static void generateChannelExtPPBEnqResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateChannelExtCommonRechargeResponse";
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
            if (p_requestVO.getRequestMap() != null) {
                sbf.append("<EXTREFNUM>").append((String) p_requestVO.getRequestMap().get("EXTREFNUM")).append("</EXTREFNUM>");
            } else {
                sbf.append("<EXTREFNUM></EXTREFNUM>");
            }

            sbf.append("<DETAILS>");
            if (BTSLUtil.isNullString(p_requestVO.getDueDate())) {
                sbf.append("<INVDATE></INVDATE>");
            } else {
                sbf.append("<INVDATE>").append(BTSLDateUtil.getSystemLocaleDate(p_requestVO.getDueDate())).append("</INVDATE>");
            }
            if (BTSLUtil.isNullString(p_requestVO.getMinAmtDue())) {
                sbf.append("<INVAMT></INVAMT>");
            } else {
                sbf.append("<INVAMT>").append(p_requestVO.getMinAmtDue()).append("</INVAMT>");
            }
            sbf.append("</DETAILS>");
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "XMLStringParser[generateChannelExtCommonRechargeResponse]", PretupsErrorCodesI.C2S_ERROR_EXCEPTION, "", "", "generateChannelExtCommonRechargeResponse:" + e
                    .getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    public static void parseOperatorUserAddRequest(RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "parseUserAddRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            String reqDate = null;
            String externalNetworkCode = null;
            final String categoryCode = null;
            String requestLoginId = null;
            String password = null;
            String extRefNum = null;
            final String userLoginId = null;
            final String userMsisdn = null;
            String userCategory = null;
            String userName = null;
            String shortName = null;
            String userNamePrefix = null;
            String subscriberCode = null;
            String externalCode = null;
            String contactPerson = null;
            String contactNumber = null;
            String ssn = null;
            String address1 = null;
            String address2 = null;
            String city = null;
            String state = null;
            String country = null;
            String emailId = null;
            String webloginId = null;
            String webPassword = null;
            String designation = null;
            String division = null;
            String department = null;
            String catagoryCode = null;
            String mobileNumber = null;

            final HashMap elementMap = new HashMap();
            try {
                final String[] validateTag = { "<COMMAND>", "</COMMAND>", "<TYPE>", "</TYPE>", "<DATE>", "</DATE>", "<CATCODE>", "</CATCODE>", "<EXTNWCODE>", "</EXTNWCODE>", "<LOGINID>", "</LOGINID>", "<PASSWORD>", "</PASSWORD>", "<EXTREFNUM>", "</EXTREFNUM>", "<DATA>", "</DATA>" };
                XMLStringValidation.validateTags(requestStr, validateTag);
                elementMap.put("TYPE", type);

                index = requestStr.indexOf("<DATE>");
                reqDate = requestStr.substring(index + "<DATE>".length(), requestStr.indexOf("</DATE>", index));
                reqDate = BTSLDateUtil.getGregorianDateInString(reqDate);
                elementMap.put("DATE", reqDate);
                index = requestStr.indexOf("<EXTNWCODE>");
                externalNetworkCode = requestStr.substring(index + "<EXTNWCODE>".length(), requestStr.indexOf("</EXTNWCODE>", index));
                elementMap.put("EXTNWCODE", externalNetworkCode);
                index = requestStr.indexOf("<CATCODE>");
                catagoryCode = requestStr.substring(index + "<CATCODE>".length(), requestStr.indexOf("</CATCODE>", index));
                elementMap.put("CATCODE", catagoryCode);

                index = requestStr.indexOf("<LOGINID>");
                requestLoginId = requestStr.substring(index + "<LOGINID>".length(), requestStr.indexOf("</LOGINID>", index));
                elementMap.put("LOGINID", requestLoginId);
                index = requestStr.indexOf("<PASSWORD>");
                password = requestStr.substring(index + "<PASSWORD>".length(), requestStr.indexOf("</PASSWORD>", index));
                elementMap.put("PASSWORD", password);
                // Ended Here
                index = requestStr.indexOf("<EXTREFNUM>");
                extRefNum = requestStr.substring(index + "<EXTREFNUM>".length(), requestStr.indexOf("</EXTREFNUM>", index));
                index = requestStr.indexOf("<DATA>");
                final String requestData = requestStr.substring(index + "<DATA>".length(), requestStr.indexOf("</DATA>", index));

                final String[] validateTag4Data = { "<USERCATCODE>", "</USERCATCODE>", "<USERNAME>", "</USERNAME>", "<SHORTNAME>", "</SHORTNAME>", "<USERNAMEPREFIX>", "</USERNAMEPREFIX>", "<SUBSCRIBERCODE>", "</SUBSCRIBERCODE>", "<EXTERNALCODE>", "</EXTERNALCODE>", "<DESIGNATION>", "</DESIGNATION>", "<DIVISION>", "</DIVISION>", "<DEPARTMENT>", "</DEPARTMENT>", "<CONTACTPERSON>", "</CONTACTPERSON>", "<CONTACTNUMBER>", "</CONTACTNUMBER>", "<MOBILENUMBER>", "</MOBILENUMBER>", "<SSN>", "</SSN>", "<ADDRESS1>", "</ADDRESS1>", "<ADDRESS2>", "</ADDRESS2>", "<CITY>", "</CITY>", "<STATE>", "</STATE>", "<COUNTRY>", "</COUNTRY>", "<EMAILID>", "</EMAILID>", "<WEBLOGINID>", "</WEBLOGINID>", "<WEBPASSWORD>", "</WEBPASSWORD>" };
                XMLStringValidation.validateTags(requestData, validateTag4Data);

                index = requestData.indexOf("<USERCATCODE>");
                userCategory = requestData.substring(index + "<USERCATCODE>".length(), requestData.indexOf("</USERCATCODE>", index));
                elementMap.put("USERCATCODE", userCategory);
                index = requestData.indexOf("<USERNAME>");
                userName = requestData.substring(index + "<USERNAME>".length(), requestData.indexOf("</USERNAME>", index));
                elementMap.put("USERNAME", userName);
                index = requestData.indexOf("<SHORTNAME>");
                shortName = requestData.substring(index + "<SHORTNAME>".length(), requestData.indexOf("</SHORTNAME>", index));
                elementMap.put("SHORTNAME", shortName);
                index = requestData.indexOf("<USERNAMEPREFIX>");
                userNamePrefix = requestData.substring(index + "<USERNAMEPREFIX>".length(), requestData.indexOf("</USERNAMEPREFIX>", index));
                elementMap.put("USERNAMEPREFIX", userNamePrefix);
                index = requestData.indexOf("<SUBSCRIBERCODE>");
                subscriberCode = requestData.substring(index + "<SUBSCRIBERCODE>".length(), requestData.indexOf("</SUBSCRIBERCODE>", index));
                elementMap.put("SUBSCRIBERCODE", subscriberCode);
                index = requestData.indexOf("<EXTERNALCODE>");
                externalCode = requestData.substring(index + "<EXTERNALCODE>".length(), requestData.indexOf("</EXTERNALCODE>", index));
                elementMap.put("EXTERNALCODE", externalCode);

                index = requestData.indexOf("<DESIGNATION>");
                designation = requestData.substring(index + "<DESIGNATION>".length(), requestData.indexOf("</DESIGNATION>", index));
                elementMap.put("DESIGNATION", designation);

                index = requestData.indexOf("<DIVISION>");
                division = requestData.substring(index + "<DIVISION>".length(), requestData.indexOf("</DIVISION>", index));
                elementMap.put("DIVISION", division);

                index = requestData.indexOf("<DEPARTMENT>");
                department = requestData.substring(index + "<DEPARTMENT>".length(), requestData.indexOf("</DEPARTMENT>", index));
                elementMap.put("DEPARTMENT", department);

                index = requestData.indexOf("<CONTACTPERSON>");
                contactPerson = requestData.substring(index + "<CONTACTPERSON>".length(), requestData.indexOf("</CONTACTPERSON>", index));
                elementMap.put("CONTACTPERSON", contactPerson);
                index = requestData.indexOf("<CONTACTNUMBER>");
                contactNumber = requestData.substring(index + "<CONTACTNUMBER>".length(), requestData.indexOf("</CONTACTNUMBER>", index));
                elementMap.put("CONTACTNUMBER", contactNumber);

                index = requestData.indexOf("<MOBILENUMBER>");
                mobileNumber = requestData.substring(index + "<MOBILENUMBER>".length(), requestData.indexOf("</MOBILENUMBER>", index));
                elementMap.put("MOBILENUMBER", mobileNumber);

                index = requestData.indexOf("<SSN>");
                ssn = requestData.substring(index + "<SSN>".length(), requestData.indexOf("</SSN>", index));
                elementMap.put("SSN", ssn);
                index = requestData.indexOf("<ADDRESS1>");
                address1 = requestData.substring(index + "<ADDRESS1>".length(), requestData.indexOf("</ADDRESS1>", index));
                elementMap.put("ADDRESS1", address1);
                index = requestData.indexOf("<ADDRESS2>");
                address2 = requestData.substring(index + "<ADDRESS2>".length(), requestData.indexOf("</ADDRESS2>", index));
                elementMap.put("ADDRESS2", address2);
                index = requestData.indexOf("<CITY>");
                city = requestData.substring(index + "<CITY>".length(), requestData.indexOf("</CITY>", index));
                elementMap.put("CITY", city);
                index = requestData.indexOf("<STATE>");
                state = requestData.substring(index + "<STATE>".length(), requestData.indexOf("</STATE>", index));
                elementMap.put("STATE", state);
                index = requestData.indexOf("<COUNTRY>");
                country = requestData.substring(index + "<COUNTRY>".length(), requestData.indexOf("</COUNTRY>", index));
                elementMap.put("COUNTRY", country);
                index = requestData.indexOf("<EMAILID>");
                emailId = requestData.substring(index + "<EMAILID>".length(), requestData.indexOf("</EMAILID>", index));
                elementMap.put("EMAILID", emailId);
                index = requestData.indexOf("<WEBLOGINID>");
                webloginId = requestData.substring(index + "<WEBLOGINID>".length(), requestData.indexOf("</WEBLOGINID>", index));
                elementMap.put("WEBLOGINID", webloginId);
                index = requestData.indexOf("<WEBPASSWORD>");
                webPassword = requestData.substring(index + "<WEBPASSWORD>".length(), requestData.indexOf("</WEBPASSWORD>", index));
                elementMap.put("WEBPASSWORD", webPassword);
            } catch (BTSLBaseException be) {
                _log.errorTrace(methodName, be);
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
                throw be;
            } catch (RuntimeException e) {
                // TODO Auto-generated catch block
                _log.errorTrace(methodName, e);
                p_requestVO.setMessageCode(PretupsErrorCodesI.EXTSYS_REQ_INVALID_FORMAT);
                throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.EXTSYS_REQ_INVALID_FORMAT);
            }

            p_requestVO.setLocale(new Locale(defaultLanguage, defaultCountry));
            p_requestVO.setRequestIDStr(type);
            p_requestVO.setType(type);
            p_requestVO.setReqDate(reqDate);
            p_requestVO.setExternalNetworkCode(externalNetworkCode);
            p_requestVO.setCategoryCode(categoryCode);
            p_requestVO.setSenderLoginID(requestLoginId);
            p_requestVO.setPassword(password);
            p_requestVO.setExternalReferenceNum(extRefNum);
            p_requestVO.setRequestLoginId(userLoginId);
            p_requestVO.setRequestMSISDN(userMsisdn);
            p_requestVO.setUserCategory(userCategory);
            p_requestVO.setRequestMap(elementMap);

            XMLStringValidation.validateOperatorUserAddRequest(p_requestVO, type, reqDate, externalNetworkCode, requestLoginId, password, extRefNum, userCategory, userName,
                userNamePrefix, externalCode, mobileNumber, division, department, shortName, userNamePrefix, subscriberCode, contactPerson, contactNumber, ssn, address1,
                address2, city, state, country, emailId, webloginId, webPassword);
            parsedRequestStr = PretupsI.SERVICE_TYPE_OPT_USER_ADD + CHNL_MESSAGE_SEP + type + CHNL_MESSAGE_SEP + reqDate + CHNL_MESSAGE_SEP + externalNetworkCode + CHNL_MESSAGE_SEP + requestLoginId + CHNL_MESSAGE_SEP + password + CHNL_MESSAGE_SEP + extRefNum + CHNL_MESSAGE_SEP + userCategory + CHNL_MESSAGE_SEP + userName + CHNL_MESSAGE_SEP + shortName + CHNL_MESSAGE_SEP + userNamePrefix + CHNL_MESSAGE_SEP + subscriberCode + CHNL_MESSAGE_SEP + externalCode + CHNL_MESSAGE_SEP + designation + CHNL_MESSAGE_SEP + department + CHNL_MESSAGE_SEP + division + CHNL_MESSAGE_SEP + contactPerson + CHNL_MESSAGE_SEP + contactNumber + CHNL_MESSAGE_SEP + ssn + CHNL_MESSAGE_SEP + address1 + CHNL_MESSAGE_SEP + address2 + CHNL_MESSAGE_SEP + city + CHNL_MESSAGE_SEP + state + CHNL_MESSAGE_SEP + country + CHNL_MESSAGE_SEP + emailId + CHNL_MESSAGE_SEP + webloginId + CHNL_MESSAGE_SEP + webPassword;

            p_requestVO.setDecryptedMessage(parsedRequestStr);
        } catch (BTSLBaseException be) {
            p_requestVO.setMessageCode(be.getMessageKey());
            p_requestVO.setMessageArguments(be.getArgs());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.EXTSYS_REQ_INVALID_FORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.EXTSYS_REQ_INVALID_FORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    public static void generateOperatorUserAddResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateOperatorUserAddResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        final java.util.Date date = new java.util.Date();
        ChannelUserVO channelUserVO = null;
        try {
            if (p_requestVO.getRequestMap() != null) {
                channelUserVO = (ChannelUserVO) p_requestVO.getRequestMap().get("CHNUSERVO");
            }
            final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DDMMYYYYHHMMSS);
            sdf.setLenient(false);
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?>");
            sbf.append("<COMMAND>");
            sbf.append("<TYPE>").append(ADD_OPERATOR_USER_RES).append("</TYPE>");
            if (channelUserVO != null && !BTSLUtil.isNullString(channelUserVO.getUserID())) {
                sbf.append("<USERID>").append(channelUserVO.getUserID()).append("</USERID>");
            } else {
                sbf.append("<USERID></USERID>");
            }

            if (channelUserVO != null && !BTSLUtil.isNullString(channelUserVO.getExternalCode())) {
                sbf.append("<EXTERNALCODE>").append(channelUserVO.getExternalCode()).append("</EXTERNALCODE>");
            } else {
                sbf.append("<EXTERNALCODE></EXTERNALCODE>");
            }
            if (p_requestVO.getRequestMap() != null) {
                sbf.append("<LOGINID>").append(p_requestVO.getRequestMap().get("WEBLOGINID").toString()).append("</LOGINID>");
            } else {
                sbf.append("<LOGINID></LOGINID>");
            }

            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
            }
            Locale locale;
            if (p_requestVO.getLocale() != null) {
                locale = p_requestVO.getLocale();
            } else {
                locale = new Locale(defaultLanguage, defaultCountry);
            }
            final String senderMessage = getMessage(locale, p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
            sbf.append("<MESSAGE>").append(senderMessage).append("</MESSAGE>");
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateOperatorUserAddResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateOperatorUserAddResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * @author vipan.kumar
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public static void parseOperatorUserModRequest(RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "parseUserAddRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            String reqDate = null;
            String externalNetworkCode = null;
            final String categoryCode = null;
            String requestLoginId = null;
            String password = null;
            String extRefNum = null;
            final String userLoginId = null;
            final String userMsisdn = null;
            final String userCategory = null;
            String userName = null;
            String shortName = null;
            String userNamePrefix = null;
            String subscriberCode = null;
            String externalCode = null;
            String contactPerson = null;
            String contactNumber = null;
            String ssn = null;
            String address1 = null;
            String address2 = null;
            String city = null;
            String state = null;
            String country = null;
            String emailId = null;
            String webloginId = null;
            String webPassword = null;
            String designation = null;
            String division = null;
            String department = null;
            String catagoryCode = null;
            String mobileNumber = null;
            String userCurrentLoginId = null;

            final HashMap elementMap = new HashMap();
            try {
                final String[] validateTag = { "<COMMAND>", "</COMMAND>", "<TYPE>", "</TYPE>", "<DATE>", "</DATE>", "<CATCODE>", "</CATCODE>", "<EXTNWCODE>", "</EXTNWCODE>", "<LOGINID>", "</LOGINID>", "<PASSWORD>", "</PASSWORD>", "<EXTREFNUM>", "</EXTREFNUM>", "<DATA>", "</DATA>" };
                XMLStringValidation.validateTags(requestStr, validateTag);
                elementMap.put("TYPE", type);

                index = requestStr.indexOf("<DATE>");
                reqDate = requestStr.substring(index + "<DATE>".length(), requestStr.indexOf("</DATE>", index));
                reqDate = BTSLDateUtil.getGregorianDateInString(reqDate);
                elementMap.put("DATE", reqDate);
                index = requestStr.indexOf("<EXTNWCODE>");
                externalNetworkCode = requestStr.substring(index + "<EXTNWCODE>".length(), requestStr.indexOf("</EXTNWCODE>", index));
                elementMap.put("EXTNWCODE", externalNetworkCode);
                index = requestStr.indexOf("<CATCODE>");
                catagoryCode = requestStr.substring(index + "<CATCODE>".length(), requestStr.indexOf("</CATCODE>", index));
                elementMap.put("CATCODE", catagoryCode);

                index = requestStr.indexOf("<LOGINID>");
                requestLoginId = requestStr.substring(index + "<LOGINID>".length(), requestStr.indexOf("</LOGINID>", index));
                elementMap.put("LOGINID", requestLoginId);
                index = requestStr.indexOf("<PASSWORD>");
                password = requestStr.substring(index + "<PASSWORD>".length(), requestStr.indexOf("</PASSWORD>", index));
                elementMap.put("PASSWORD", password);
                // Ended Here
                index = requestStr.indexOf("<EXTREFNUM>");
                extRefNum = requestStr.substring(index + "<EXTREFNUM>".length(), requestStr.indexOf("</EXTREFNUM>", index));
                index = requestStr.indexOf("<DATA>");
                final String requestData = requestStr.substring(index + "<DATA>".length(), requestStr.indexOf("</DATA>", index));

                final String[] validateTag4Data = { "<USRLOGINID>", "</USRLOGINID>", "<USERNAME>", "</USERNAME>", "<SHORTNAME>", "</SHORTNAME>", "<USERNAMEPREFIX>", "</USERNAMEPREFIX>", "<SUBSCRIBERCODE>", "</SUBSCRIBERCODE>", "<EXTERNALCODE>", "</EXTERNALCODE>", "<DESIGNATION>", "</DESIGNATION>", "<DIVISION>", "</DIVISION>", "<DEPARTMENT>", "</DEPARTMENT>", "<CONTACTPERSON>", "</CONTACTPERSON>", "<CONTACTNUMBER>", "</CONTACTNUMBER>", "<MOBILENUMBER>", "</MOBILENUMBER>", "<SSN>", "</SSN>", "<ADDRESS1>", "</ADDRESS1>", "<ADDRESS2>", "</ADDRESS2>", "<CITY>", "</CITY>", "<STATE>", "</STATE>", "<COUNTRY>", "</COUNTRY>", "<EMAILID>", "</EMAILID>", "<WEBLOGINID>", "</WEBLOGINID>", "<WEBPASSWORD>", "</WEBPASSWORD>" };
                XMLStringValidation.validateTags(requestData, validateTag4Data);

                index = requestData.indexOf("<USRLOGINID>");
                userCurrentLoginId = requestData.substring(index + "<USRLOGINID>".length(), requestData.indexOf("</USRLOGINID>", index));
                elementMap.put("USRLOGINID", userCurrentLoginId);

                index = requestData.indexOf("<USERNAME>");
                userName = requestData.substring(index + "<USERNAME>".length(), requestData.indexOf("</USERNAME>", index));
                elementMap.put("USERNAME", userName);
                index = requestData.indexOf("<SHORTNAME>");
                shortName = requestData.substring(index + "<SHORTNAME>".length(), requestData.indexOf("</SHORTNAME>", index));
                elementMap.put("SHORTNAME", shortName);
                index = requestData.indexOf("<USERNAMEPREFIX>");
                userNamePrefix = requestData.substring(index + "<USERNAMEPREFIX>".length(), requestData.indexOf("</USERNAMEPREFIX>", index));
                elementMap.put("USERNAMEPREFIX", userNamePrefix);
                index = requestData.indexOf("<SUBSCRIBERCODE>");
                subscriberCode = requestData.substring(index + "<SUBSCRIBERCODE>".length(), requestData.indexOf("</SUBSCRIBERCODE>", index));
                elementMap.put("SUBSCRIBERCODE", subscriberCode);
                index = requestData.indexOf("<EXTERNALCODE>");
                externalCode = requestData.substring(index + "<EXTERNALCODE>".length(), requestData.indexOf("</EXTERNALCODE>", index));
                elementMap.put("EXTERNALCODE", externalCode);

                index = requestData.indexOf("<DESIGNATION>");
                designation = requestData.substring(index + "<DESIGNATION>".length(), requestData.indexOf("</DESIGNATION>", index));
                elementMap.put("DESIGNATION", designation);

                index = requestData.indexOf("<DIVISION>");
                division = requestData.substring(index + "<DIVISION>".length(), requestData.indexOf("</DIVISION>", index));
                elementMap.put("DIVISION", division);

                index = requestData.indexOf("<DEPARTMENT>");
                department = requestData.substring(index + "<DEPARTMENT>".length(), requestData.indexOf("</DEPARTMENT>", index));
                elementMap.put("DEPARTMENT", department);

                index = requestData.indexOf("<CONTACTPERSON>");
                contactPerson = requestData.substring(index + "<CONTACTPERSON>".length(), requestData.indexOf("</CONTACTPERSON>", index));
                elementMap.put("CONTACTPERSON", contactPerson);
                index = requestData.indexOf("<CONTACTNUMBER>");
                contactNumber = requestData.substring(index + "<CONTACTNUMBER>".length(), requestData.indexOf("</CONTACTNUMBER>", index));
                elementMap.put("CONTACTNUMBER", contactNumber);

                index = requestData.indexOf("<MOBILENUMBER>");
                mobileNumber = requestData.substring(index + "<MOBILENUMBER>".length(), requestData.indexOf("</MOBILENUMBER>", index));
                elementMap.put("MOBILENUMBER", mobileNumber);

                index = requestData.indexOf("<SSN>");
                ssn = requestData.substring(index + "<SSN>".length(), requestData.indexOf("</SSN>", index));
                elementMap.put("SSN", ssn);
                index = requestData.indexOf("<ADDRESS1>");
                address1 = requestData.substring(index + "<ADDRESS1>".length(), requestData.indexOf("</ADDRESS1>", index));
                elementMap.put("ADDRESS1", address1);
                index = requestData.indexOf("<ADDRESS2>");
                address2 = requestData.substring(index + "<ADDRESS2>".length(), requestData.indexOf("</ADDRESS2>", index));
                elementMap.put("ADDRESS2", address2);
                index = requestData.indexOf("<CITY>");
                city = requestData.substring(index + "<CITY>".length(), requestData.indexOf("</CITY>", index));
                elementMap.put("CITY", city);
                index = requestData.indexOf("<STATE>");
                state = requestData.substring(index + "<STATE>".length(), requestData.indexOf("</STATE>", index));
                elementMap.put("STATE", state);
                index = requestData.indexOf("<COUNTRY>");
                country = requestData.substring(index + "<COUNTRY>".length(), requestData.indexOf("</COUNTRY>", index));
                elementMap.put("COUNTRY", country);
                index = requestData.indexOf("<EMAILID>");
                emailId = requestData.substring(index + "<EMAILID>".length(), requestData.indexOf("</EMAILID>", index));
                elementMap.put("EMAILID", emailId);
                index = requestData.indexOf("<WEBLOGINID>");
                webloginId = requestData.substring(index + "<WEBLOGINID>".length(), requestData.indexOf("</WEBLOGINID>", index));
                elementMap.put("WEBLOGINID", webloginId);
                index = requestData.indexOf("<WEBPASSWORD>");
                webPassword = requestData.substring(index + "<WEBPASSWORD>".length(), requestData.indexOf("</WEBPASSWORD>", index));
                elementMap.put("WEBPASSWORD", webPassword);
            } catch (BTSLBaseException be) {
                _log.errorTrace(methodName, be);
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
                throw be;
            } catch (RuntimeException e) {
                // TODO Auto-generated catch block
                _log.errorTrace(methodName, e);
                p_requestVO.setMessageCode(PretupsErrorCodesI.EXTSYS_REQ_INVALID_FORMAT);
                throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.EXTSYS_REQ_INVALID_FORMAT);
            }

            p_requestVO.setLocale(new Locale(defaultLanguage, defaultCountry));
            p_requestVO.setRequestIDStr(type);
            p_requestVO.setType(type);
            p_requestVO.setReqDate(reqDate);
            p_requestVO.setExternalNetworkCode(externalNetworkCode);
            p_requestVO.setCategoryCode(categoryCode);
            p_requestVO.setSenderLoginID(requestLoginId);
            p_requestVO.setPassword(password);
            p_requestVO.setExternalReferenceNum(extRefNum);
            p_requestVO.setRequestLoginId(userLoginId);
            p_requestVO.setRequestMSISDN(userMsisdn);
            p_requestVO.setUserCategory(userCategory);
            p_requestVO.setRequestMap(elementMap);

            XMLStringValidation.validateOperatorUserModRequest(p_requestVO, type, reqDate, externalNetworkCode, requestLoginId, password, extRefNum, userCategory, userName,
                userNamePrefix, externalCode, mobileNumber, division, department, shortName, userNamePrefix, subscriberCode, contactPerson, contactNumber, ssn, address1,
                address2, city, state, country, emailId, webloginId, webPassword, userCurrentLoginId);
            parsedRequestStr = PretupsI.SERVICE_TYPE_OPT_USER_MOD + CHNL_MESSAGE_SEP + type + CHNL_MESSAGE_SEP + reqDate + CHNL_MESSAGE_SEP + externalNetworkCode + CHNL_MESSAGE_SEP + requestLoginId + CHNL_MESSAGE_SEP + password + CHNL_MESSAGE_SEP + extRefNum + CHNL_MESSAGE_SEP + userCategory + CHNL_MESSAGE_SEP + userName + CHNL_MESSAGE_SEP + shortName + CHNL_MESSAGE_SEP + userNamePrefix + CHNL_MESSAGE_SEP + subscriberCode + CHNL_MESSAGE_SEP + externalCode + CHNL_MESSAGE_SEP + designation + CHNL_MESSAGE_SEP + department + CHNL_MESSAGE_SEP + division + CHNL_MESSAGE_SEP + contactPerson + CHNL_MESSAGE_SEP + contactNumber + CHNL_MESSAGE_SEP + ssn + CHNL_MESSAGE_SEP + address1 + CHNL_MESSAGE_SEP + address2 + CHNL_MESSAGE_SEP + city + CHNL_MESSAGE_SEP + state + CHNL_MESSAGE_SEP + country + CHNL_MESSAGE_SEP + emailId + CHNL_MESSAGE_SEP + webloginId + CHNL_MESSAGE_SEP + webPassword + CHNL_MESSAGE_SEP + userCurrentLoginId;

            p_requestVO.setDecryptedMessage(parsedRequestStr);
        } catch (BTSLBaseException be) {
            p_requestVO.setMessageCode(be.getMessageKey());
            p_requestVO.setMessageArguments(be.getArgs());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.EXTSYS_REQ_INVALID_FORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.EXTSYS_REQ_INVALID_FORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * @author vipan.kumar
     * @param p_requestVO
     * @throws Exception
     */
    public static void generateOperatorUserModResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateOperatorUserAddResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        final java.util.Date date = new java.util.Date();
        ChannelUserVO channelUserVO = null;
        try {
            if (p_requestVO.getRequestMap() != null) {
                channelUserVO = (ChannelUserVO) p_requestVO.getRequestMap().get("CHNUSERVO");
            }
            final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DDMMYYYYHHMMSS);
            sdf.setLenient(false);
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?>");
            sbf.append("<COMMAND>");
            sbf.append("<TYPE>").append(MOD_OPERATOR_USER_RES).append("</TYPE>");
            if (channelUserVO != null && !BTSLUtil.isNullString(channelUserVO.getUserID())) {
                sbf.append("<USERID>").append(channelUserVO.getUserID()).append("</USERID>");
            } else {
                sbf.append("<USERID></USERID>");
            }
            if (channelUserVO != null && !BTSLUtil.isNullString(channelUserVO.getExternalCode())) {
                sbf.append("<EXTERNALCODE>").append(channelUserVO.getExternalCode()).append("</EXTERNALCODE>");
            } else {
                sbf.append("<EXTERNALCODE></EXTERNALCODE>");
            }
            if (p_requestVO.getRequestMap() != null) {
                sbf.append("<LOGINID>").append(p_requestVO.getRequestMap().get("USRLOGINID").toString()).append("</LOGINID>");
            } else {
                sbf.append("<LOGINID></LOGINID>");
            }

            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
            }
            Locale locale;
            if (p_requestVO.getLocale() != null) {
                locale = p_requestVO.getLocale();
            } else {
                locale = new Locale(defaultLanguage, defaultCountry);
            }
            final String senderMessage = getMessage(locale, p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
            sbf.append("<MESSAGE>").append(senderMessage).append("</MESSAGE>");
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateOperatorUserAddResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateOperatorUserAddResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    /**
     * @author vipan.kumar
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public static void parseOperatorUserSRDRequest(RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "parseOperatorUserSRDRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            String reqDate = null;
            String externalNetworkCode = null;
            final String categoryCode = null;
            String requestLoginId = null;
            String password = null;
            String extRefNum = null;
            String action = null;
            String remarks = null;
            String userCurrentLoginId = null;
            String catagoryCode = null;

            final HashMap elementMap = new HashMap();
            try {
                final String[] validateTag = { "<COMMAND>", "</COMMAND>", "<TYPE>", "</TYPE>", "<DATE>", "</DATE>", "<CATCODE>", "</CATCODE>", "<EXTNWCODE>", "</EXTNWCODE>", "<LOGINID>", "</LOGINID>", "<PASSWORD>", "</PASSWORD>", "<EXTREFNUM>", "</EXTREFNUM>", "<DATA>", "</DATA>" };
                XMLStringValidation.validateTags(requestStr, validateTag);
                elementMap.put("TYPE", type);

                index = requestStr.indexOf("<DATE>");
                reqDate = requestStr.substring(index + "<DATE>".length(), requestStr.indexOf("</DATE>", index));
                reqDate = BTSLDateUtil.getGregorianDateInString(reqDate);
                elementMap.put("DATE", reqDate);
                index = requestStr.indexOf("<EXTNWCODE>");
                externalNetworkCode = requestStr.substring(index + "<EXTNWCODE>".length(), requestStr.indexOf("</EXTNWCODE>", index));
                elementMap.put("EXTNWCODE", externalNetworkCode);
                index = requestStr.indexOf("<CATCODE>");
                catagoryCode = requestStr.substring(index + "<CATCODE>".length(), requestStr.indexOf("</CATCODE>", index));
                elementMap.put("CATCODE", catagoryCode);

                index = requestStr.indexOf("<LOGINID>");
                requestLoginId = requestStr.substring(index + "<LOGINID>".length(), requestStr.indexOf("</LOGINID>", index));
                elementMap.put("LOGINID", requestLoginId);
                index = requestStr.indexOf("<PASSWORD>");
                password = requestStr.substring(index + "<PASSWORD>".length(), requestStr.indexOf("</PASSWORD>", index));
                elementMap.put("PASSWORD", password);
                // Ended Here
                index = requestStr.indexOf("<EXTREFNUM>");
                extRefNum = requestStr.substring(index + "<EXTREFNUM>".length(), requestStr.indexOf("</EXTREFNUM>", index));
                index = requestStr.indexOf("<DATA>");
                final String requestData = requestStr.substring(index + "<DATA>".length(), requestStr.indexOf("</DATA>", index));

                final String[] validateTag4Data = { "<USRLOGINID>", "</USRLOGINID>", "<ACTION>", "</ACTION>", "<REMARKS>", "</REMARKS>" };
                XMLStringValidation.validateTags(requestData, validateTag4Data);

                index = requestData.indexOf("<USRLOGINID>");
                userCurrentLoginId = requestData.substring(index + "<USRLOGINID>".length(), requestData.indexOf("</USRLOGINID>", index));
                elementMap.put("USRLOGINID", userCurrentLoginId);

                index = requestData.indexOf("<ACTION>");
                action = requestData.substring(index + "<ACTION>".length(), requestData.indexOf("</ACTION>", index));
                elementMap.put("ACTION", action);

                index = requestData.indexOf("<REMARKS>");
                remarks = requestData.substring(index + "<REMARKS>".length(), requestData.indexOf("</REMARKS>", index));
                elementMap.put("REMARKS", remarks);

            } catch (BTSLBaseException be) {
                _log.errorTrace(methodName, be);
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
                throw be;
            } catch (RuntimeException e) {
                _log.errorTrace(methodName, e);
                p_requestVO.setMessageCode(PretupsErrorCodesI.EXTSYS_REQ_INVALID_FORMAT);
                throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.EXTSYS_REQ_INVALID_FORMAT);
            }

            p_requestVO.setLocale(new Locale(defaultLanguage, defaultCountry));
            p_requestVO.setRequestIDStr(type);
            p_requestVO.setType(type);
            p_requestVO.setReqDate(reqDate);
            p_requestVO.setExternalNetworkCode(externalNetworkCode);
            p_requestVO.setCategoryCode(categoryCode);
            p_requestVO.setSenderLoginID(requestLoginId);
            p_requestVO.setPassword(password);
            p_requestVO.setExternalReferenceNum(extRefNum);
            p_requestVO.setRequestMap(elementMap);

            parsedRequestStr = PretupsI.SERVICE_TYPE_OPT_USER_SRD + CHNL_MESSAGE_SEP + type + CHNL_MESSAGE_SEP + reqDate + CHNL_MESSAGE_SEP + externalNetworkCode + CHNL_MESSAGE_SEP + requestLoginId + CHNL_MESSAGE_SEP + password + CHNL_MESSAGE_SEP + extRefNum + CHNL_MESSAGE_SEP + userCurrentLoginId + CHNL_MESSAGE_SEP + action + CHNL_MESSAGE_SEP + remarks;
            p_requestVO.setDecryptedMessage(parsedRequestStr);
        } catch (BTSLBaseException be) {
            p_requestVO.setMessageCode(be.getMessageKey());
            p_requestVO.setMessageArguments(be.getArgs());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.EXTSYS_REQ_INVALID_FORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.EXTSYS_REQ_INVALID_FORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    public static void generateOperatorUserSRDResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateOperatorUserSRDResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        final java.util.Date date = new java.util.Date();
        ChannelUserVO channelUserVO = null;
        try {
            if (p_requestVO.getRequestMap() != null) {
                channelUserVO = (ChannelUserVO) p_requestVO.getRequestMap().get("CHNUSERVO");
            }
            final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DDMMYYYYHHMMSS);
            sdf.setLenient(false);
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?>");
            sbf.append("<COMMAND>");
            sbf.append("<TYPE>").append(SRD_OPERATOR_USER_RES).append("</TYPE>");

            if (channelUserVO != null && !BTSLUtil.isNullString(channelUserVO.getUserID())) {
                sbf.append("<USERID>").append(channelUserVO.getUserID()).append("</USERID>");
            } else {
                sbf.append("<USERID></USERID>");
            }

            if (channelUserVO != null && !BTSLUtil.isNullString(channelUserVO.getExternalCode())) {
                sbf.append("<EXTERNALCODE>").append(channelUserVO.getExternalCode()).append("</EXTERNALCODE>");
            } else {
                sbf.append("<EXTERNALCODE></EXTERNALCODE>");
            }

            if (p_requestVO.getRequestMap() != null) {
                sbf.append("<LOGINID>").append(p_requestVO.getRequestMap().get("USRLOGINID").toString()).append("</LOGINID>");
            } else {
                sbf.append("<LOGINID></LOGINID>");
            }

            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
            }
            Locale locale;
            if (p_requestVO.getLocale() != null) {
                locale = p_requestVO.getLocale();
            } else {
                locale = new Locale(defaultLanguage, defaultCountry);
            }
            final String senderMessage = getMessage(locale, p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
            sbf.append("<MESSAGE>").append(senderMessage).append("</MESSAGE>");
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateOperatorUserSRDResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateOperatorUserAddResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    // ENded Here By Diwakar
    public static void parseExtRoamRechargeRequest(RequestVO p_requestVO) throws Exception {
        final String methodName = "parseExtRoamRechargeRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            final HashMap requestHashMap = new HashMap();

            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            requestHashMap.put("TYPE", type);

            index = requestStr.indexOf("<DATE>");
            String date = requestStr.substring(index + "<DATE>".length(), requestStr.indexOf("</DATE>", index));
            date = BTSLDateUtil.getGregorianDateInString(date);
            requestHashMap.put("DATE", date);

            index = requestStr.indexOf("<EXTNWCODE>");
            final String extnwcode = requestStr.substring(index + "<EXTNWCODE>".length(), requestStr.indexOf("</EXTNWCODE>", index));
            requestHashMap.put("EXTNWCODE", extnwcode);

            index = requestStr.indexOf("<MSISDN>");
            final String msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
            requestHashMap.put("MSISDN", msisdn);

            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            requestHashMap.put("PIN", pin);

            index = requestStr.indexOf("<EXTREFNUM>");
            final String extrefnum = requestStr.substring(index + "<EXTREFNUM>".length(), requestStr.indexOf("</EXTREFNUM>", index));
            requestHashMap.put("EXTREFNUM", extrefnum);

            index = requestStr.indexOf("<MSISDN2>");
            final String msisdn2 = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));
            requestHashMap.put("MSISDN2", msisdn2);

            index = requestStr.indexOf("<RECHAMT>");
            final String rechamount = requestStr.substring(index + "<RECHAMT>".length(), requestStr.indexOf("</RECHAMT>", index));
            requestHashMap.put("RECHAMT", rechamount);

            index = requestStr.indexOf("<MULTFACTOR>");
            final String multfactor = requestStr.substring(index + "<MULTFACTOR>".length(), requestStr.indexOf("</MULTFACTOR>", index));
            requestHashMap.put("MULTFACTOR", multfactor);

            index = requestStr.indexOf("<SELECTOR>");
            String selector = requestStr.substring(index + "<SELECTOR>".length(), requestStr.indexOf("</SELECTOR>", index));
            requestHashMap.put("SELECTOR", selector);
            if (BTSLUtil.isNullString(selector)) {
                final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(PretupsI.SERVICE_TYPE_CHNL_RECHARGE);
                if (serviceSelectorMappingVO != null) {
                    selector = serviceSelectorMappingVO.getSelectorCode();
                }
            }

            index = requestStr.indexOf("<LANGUAGE>");
            String language = requestStr.substring(index + "<LANGUAGE>".length(), requestStr.indexOf("</LANGUAGE>", index));
            requestHashMap.put("LANGUAGE", language);

            index = requestStr.indexOf("<VALIDATIONDONE>");
            final String validationdone = requestStr.substring(index + "<VALIDATIONDONE>".length(), requestStr.indexOf("</VALIDATIONDONE>", index));

            // If validation has already been done then only IN parameters are
            // required
            if (!BTSLUtil.isNullString(validationdone) && validationdone.equalsIgnoreCase("Y")) {
                requestHashMap.put("VALIDATIONDONE", true);

                index = requestStr.indexOf("<INPARAMS>");
                final String inparams = requestStr.substring(index + "<INPARAMS>".length(), requestStr.indexOf("</INPARAMS>", index));
                index = inparams.indexOf("<ACCOUNTSTATUS>");
                final String accountstatus = inparams.substring(index + "<ACCOUNTSTATUS>".length(), inparams.indexOf("</ACCOUNTSTATUS>", index));
                requestHashMap.put("ACCOUNTSTATUS", accountstatus);

                index = inparams.indexOf("<SERVICECLASS>");
                final String serviceclass = inparams.substring(index + "<SERVICECLASS>".length(), inparams.indexOf("</SERVICECLASS>", index));
                requestHashMap.put("SERVICECLASS", index);

                index = inparams.indexOf("<VALIDITYDATE>");
                String validitydate = inparams.substring(index + "<VALIDITYDATE>".length(), inparams.indexOf("</VALIDITYDATE>", index));
                validitydate = BTSLDateUtil.getGregorianDateInString(validitydate);

                requestHashMap.put("VALIDITYDATE", validitydate);

                index = inparams.indexOf("<GRACEDATE>");
                String gracedate = inparams.substring(index + "<GRACEDATE>".length(), inparams.indexOf("</GRACEDATE>", index));
                gracedate = BTSLDateUtil.getGregorianDateInString(gracedate);

                requestHashMap.put("GRACEDATE", gracedate);

                index = inparams.indexOf("<BALANCE>");
                final String balance = inparams.substring(index + "<BALANCE>".length(), inparams.indexOf("</BALANCE>", index));
                requestHashMap.put("BALANCE", balance);

                index = inparams.indexOf("<REQCURR>");
                final String requestedcurrency = inparams.substring(index + "<REQCURR>".length(), inparams.indexOf("</REQCURR>", index));
                requestHashMap.put("REQCURR", requestedcurrency);

                index = inparams.indexOf("<LANGUAGE>");
                language = inparams.substring(index + "<LANGUAGE>".length(), inparams.indexOf("</LANGUAGE>", index));
                requestHashMap.put("INLANGUAGE", language);

                index = inparams.indexOf("<NOTIFICATIONNUMBER>");
                final String notificationnumberin = inparams.substring(index + "<NOTIFICATIONNUMBER>".length(), inparams.indexOf("</NOTIFICATIONNUMBER>", index));
                requestHashMap.put("NOTIFICATIONNUMBER", notificationnumberin);
            } else {
                requestHashMap.put("VALIDATIONDONE", false);
            }

            index = requestStr.indexOf("<OTHERINFO>");
            final String otherinfo = requestStr.substring(index + "<OTHERINFO>".length(), requestStr.indexOf("</OTHERINFO>", index));
            index = otherinfo.indexOf("<INITMSISDN>");
            final String initiatemsisdn = otherinfo.substring(index + "<INITMSISDN>".length(), otherinfo.indexOf("</INITMSISDN>", index));
            requestHashMap.put("INITMSISDN", initiatemsisdn);

            index = otherinfo.indexOf("<RECHCURR>");
            final String rechargecurrency = otherinfo.substring(index + "<RECHCURR>".length(), otherinfo.indexOf("</RECHCURR>", index));
            requestHashMap.put("RECHCURR", rechargecurrency);

            index = otherinfo.indexOf("<REQAMT>");
            final String requestedamount = otherinfo.substring(index + "<REQAMT>".length(), otherinfo.indexOf("</REQAMT>", index));
            requestHashMap.put("REQAMT", requestedamount);

            index = otherinfo.indexOf("<CURRCONVFACT>");
            final String currencyconversionfactor = otherinfo.substring(index + "<CURRCONVFACT>".length(), otherinfo.indexOf("</CURRCONVFACT>", index));
            requestHashMap.put("CURRCONVFACT", currencyconversionfactor);

            if (BTSLUtil.isNullString(selector)) {
                // selector=""+SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE;
                // Changed on 27/05/07 for Service Type selector Mapping
                final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(PretupsI.SERVICE_TYPE_CHNL_RECHARGE);
                if (serviceSelectorMappingVO != null) {
                    selector = serviceSelectorMappingVO.getSelectorCode();
                }
            }

            if (BTSLUtil.isNullString(type) || BTSLUtil.isNullString(extnwcode) || BTSLUtil.isNullString(msisdn2) || BTSLUtil.isNullString(rechamount) || BTSLUtil
                .isNullString(selector)) {
                throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            }

            parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_ROAM_RECHARGE + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + rechamount + CHNL_MESSAGE_SEP + selector + CHNL_MESSAGE_SEP + language;

            p_requestVO.setReqSelector(selector);
            p_requestVO.setExternalNetworkCode(extnwcode);
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn);
            p_requestVO.setRequestMap(requestHashMap);
        }

        catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO ID: " + p_requestVO.getRequestIDStr());
            }
        }
    }

    /**
     * Method to generate Roam Recharge response for External interface
     * 
     * @param p_requestVO
     * @throws Exception
     */
    public static void generateChnlRoamRechResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateChannelRoamRechargeResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        final java.util.Date date = new java.util.Date();
        try {
        	String EXTERNAL_DATE_FORMAT = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_DATE_FORMAT);
            final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DDMMYYYYHHMMSS);
            sdf.setLenient(false); // this is required else it will convert
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><COMMAND>");
            sbf.append("<TYPE>EXROAMRCRES</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                String message = p_requestVO.getMessageCode();
                if (message.indexOf("_") != -1) {
                    message = message.substring(0, message.indexOf("_"));
                }
                sbf.append("<TXNSTATUS>").append(message).append("</TXNSTATUS>");
            }

            sbf.append("<DATE>").append(BTSLDateUtil.getSystemLocaleDate(sdf.format(date), EXTERNAL_DATE_FORMAT) ).append("</DATE>");

            if (p_requestVO.getRequestMap() != null) {
                sbf.append("<EXTREFNUM>").append((String) p_requestVO.getRequestMap().get("EXTREFNUM")).append("</EXTREFNUM>");
            } else {
                sbf.append("<EXTREFNUM></EXTREFNUM>");
            }

            sbf.append("<DETAILS>");
            if (BTSLUtil.isNullString(p_requestVO.getDueDate())) {
                sbf.append("<INVDATE></INVDATE>");
            } else {
                sbf.append("<INVDATE>").append(BTSLDateUtil.getSystemLocaleDate(p_requestVO.getDueDate())).append("</INVDATE>");
            }
            if (BTSLUtil.isNullString(p_requestVO.getMinAmtDue())) {
                sbf.append("<INVAMT></INVAMT>");
            } else {
                sbf.append("<INVAMT>").append(p_requestVO.getMinAmtDue()).append("</INVAMT>");
            }
            sbf.append("</DETAILS>");
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
            if (p_requestVO.getAdditionalInfoVO() != null) {
                sbf.append("<TOPUPAMOUNT>").append(p_requestVO.getAdditionalInfoVO().getTopupAmount()).append("</TOPUPAMOUNT>");
                sbf.append("<CURRENCY>").append(BTSLUtil.NullToString(p_requestVO.getAdditionalInfoVO().getCurrency())).append("</CURRENCY>");
                sbf.append("<BALANCE>").append(BTSLUtil.NullToString(p_requestVO.getAdditionalInfoVO().getBalance())).append("</BALANCE>");
                sbf.append("<VALIDITYDATE>").append(BTSLDateUtil.getLocaleDateTimeFromDate((p_requestVO.getAdditionalInfoVO().getValidityDate()))).append("</VALIDITYDATE>");
                sbf.append("<GRACEDATE>").append(BTSLDateUtil.getLocaleDateTimeFromDate(p_requestVO.getAdditionalInfoVO().getGraceDate())).append("</GRACEDATE>");
            }

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
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateChannelRoamRechargeResponse]",
                PretupsErrorCodesI.C2S_ERROR_EXCEPTION, "", "", "generateChannelRoamRechargeResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

    public static void parseC2STrfEnquiryUSSDRequest(RequestVO p_requestVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("parseC2STrfEnquiryUSSDRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        try {
            final String requestStr = p_requestVO.getRequestMessage();

            int index = requestStr.indexOf("<MSISDN1>");
            final String msisdn1 = requestStr.substring(index + "<MSISDN1>".length(), requestStr.indexOf("</MSISDN1>", index));
            index = requestStr.indexOf("<MSISDN2>");
            final String msisdn2 = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));
            index = requestStr.indexOf("<ENQDATE>");
            String enqDate = requestStr.substring(index + "<ENQDATE>".length(), requestStr.indexOf("</ENQDATE>", index));
            enqDate = BTSLDateUtil.getGregorianDateInString(enqDate);
            index = requestStr.indexOf("<AMOUNT>");
            final String amount = requestStr.substring(index + "<AMOUNT>".length(), requestStr.indexOf("</AMOUNT>", index));
            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            index = requestStr.indexOf("<LANGUAGE1>");
            String language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
            if (BTSLUtil.isNullString(language1)) {
                language1 = Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
            }

            if (BTSLUtil.isNullString(language1)) {
                language1 = "0";
            }
            if (BTSLUtil.isNullString(enqDate) && BTSLUtil.isNullString(amount)) {
                parsedRequestStr = PretupsI.CUSTOMER_TXN_C2S_ENQ_USSD + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + pin;
            } else if (!BTSLUtil.isNullString(enqDate) && BTSLUtil.isNullString(amount)) {
                parsedRequestStr = PretupsI.CUSTOMER_TXN_C2S_ENQ_USSD + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + enqDate + CHNL_MESSAGE_SEP + pin;
            } else if (BTSLUtil.isNullString(enqDate) && !BTSLUtil.isNullString(amount)) {
                parsedRequestStr = PretupsI.CUSTOMER_TXN_C2S_ENQ_USSD + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + amount + CHNL_MESSAGE_SEP + pin;
            } else {
                parsedRequestStr = PretupsI.CUSTOMER_TXN_C2S_ENQ_USSD + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + enqDate + CHNL_MESSAGE_SEP + amount + CHNL_MESSAGE_SEP + pin;
            }

            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
        } catch (Exception e) {
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);

            _log.error("parseC2STrfEnquiryUSSDRequest", "Exception e: " + e);
            throw new BTSLBaseException("XMLStringParser", "parseC2STrfEnquiryUSSDRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseC2STrfEnquiryUSSDRequest", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    public static void generateC2STrfEnquiryUSSDResponse(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("generateC2STrfEnquiryUSSDResponse", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?>");
            sbf.append("<TYPE>C2STXNENQRESP</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
            }
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.error("generateC2STrfEnquiryUSSDResponse", "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateC2STrfEnquiryUSSDResponse]",
                PretupsErrorCodesI.C2S_ERROR_EXCEPTION, "", "", "generateC2STrfEnquiryUSSDResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("generateC2STrfEnquiryUSSDResponse", "Exiting responseStr: " + responseStr);
            }
        }
    }

    public static void parseExtRoamRechargeReversalRequest(RequestVO p_requestVO) throws Exception {
        final String methodName = "parseExtRoamRechargeReversalRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        try {
            final String requestStr = p_requestVO.getRequestMessage();
            final HashMap requestHashMap = new HashMap();

            int index = requestStr.indexOf("<TYPE>");
            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            requestHashMap.put("TYPE", type);

            index = requestStr.indexOf("<EXTNWCODE>");
            final String extnwcode = requestStr.substring(index + "<EXTNWCODE>".length(), requestStr.indexOf("</EXTNWCODE>", index));
            requestHashMap.put("EXTNWCODE", extnwcode);

            index = requestStr.indexOf("<DATE>");
            String date = requestStr.substring(index + "<DATE>".length(), requestStr.indexOf("</DATE>", index));
            date = BTSLDateUtil.getGregorianDateInString(date);
            requestHashMap.put("DATE", date);

            index = requestStr.indexOf("<MSISDN>");
            final String msisdn = requestStr.substring(index + "<MSISDN>".length(), requestStr.indexOf("</MSISDN>", index));
            requestHashMap.put("MSISDN", msisdn);

            index = requestStr.indexOf("<PIN>");
            final String pin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
            requestHashMap.put("PIN", pin);

            index = requestStr.indexOf("<LOGINID>");
            final String loginid = requestStr.substring(index + "<LOGINID>".length(), requestStr.indexOf("</LOGINID>", index));
            requestHashMap.put("LOGINID", loginid);

            index = requestStr.indexOf("<PASSWORD>");
            final String password = requestStr.substring(index + "<PASSWORD>".length(), requestStr.indexOf("</PASSWORD>", index));
            requestHashMap.put("PASSWORD", password);

            index = requestStr.indexOf("<EXTCODE>");
            final String extcode = requestStr.substring(index + "<EXTCODE>".length(), requestStr.indexOf("</EXTCODE>", index));
            requestHashMap.put("EXTCODE", extcode);

            index = requestStr.indexOf("<EXTREFNUM>");
            final String extrefnum = requestStr.substring(index + "<EXTREFNUM>".length(), requestStr.indexOf("</EXTREFNUM>", index));
            requestHashMap.put("EXTREFNUM", extrefnum);

            index = requestStr.indexOf("<MSISDN2>");
            final String msisdn2 = requestStr.substring(index + "<MSISDN2>".length(), requestStr.indexOf("</MSISDN2>", index));
            requestHashMap.put("MSISDN2", msisdn2);

            index = requestStr.indexOf("<TXNID>");
            final String txnid = requestStr.substring(index + "<TXNID>".length(), requestStr.indexOf("</TXNID>", index));
            requestHashMap.put("TXNID", txnid);

            index = requestStr.indexOf("<LANGUAGE1>");
            final String language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
            requestHashMap.put("LANGUAGE1", language1);

            index = requestStr.indexOf("<LANGUAGE2>");
            final String language2 = requestStr.substring(index + "<LANGUAGE2>".length(), requestStr.indexOf("</LANGUAGE2>", index));
            requestHashMap.put("LANGUAGE2", language2);

            if (BTSLUtil.isNullString(language1) && BTSLUtil.isNullString(language2)) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_ROAM_RECHARGE_REVERSAL + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + txnid + CHNL_MESSAGE_SEP + pin;
            } else if (!BTSLUtil.isNullString(language1) && BTSLUtil.isNullString(language2)) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_ROAM_RECHARGE_REVERSAL + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + txnid + CHNL_MESSAGE_SEP + language1 + CHNL_MESSAGE_SEP + pin;
                p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            } else if (BTSLUtil.isNullString(language1) && !BTSLUtil.isNullString(language2)) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_ROAM_RECHARGE_REVERSAL + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + txnid + CHNL_MESSAGE_SEP + language2 + CHNL_MESSAGE_SEP + pin;
                p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(language2));
            } else if (!BTSLUtil.isNullString(language1) && !BTSLUtil.isNullString(language2)) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_ROAM_RECHARGE_REVERSAL + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + txnid + CHNL_MESSAGE_SEP + language1 + CHNL_MESSAGE_SEP + language2 + CHNL_MESSAGE_SEP + pin;
                p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
                p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(language2));
            }

            p_requestVO.setExternalNetworkCode(extnwcode);
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn);
            p_requestVO.setRequestMap(requestHashMap);
        }

        catch (Exception e) {
            _log.errorTrace(methodName, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO ID: " + p_requestVO.getRequestIDStr());
            }
        }
    }

    public static void generateChnlRoamReversalResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateChnlRoamReversalResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        final java.util.Date date = new java.util.Date();
        try {
        	String EXTERNAL_DATE_FORMAT = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_DATE_FORMAT);
            final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DDMMYYYYHHMMSS);
            sdf.setLenient(false); // this is required else it will convert
            sbf = new StringBuffer(1024);
            sbf.append("<?xml version=\"1.0\"?><COMMAND>");
            sbf.append("<TYPE>EXROAMRCREVRES</TYPE>");
            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
            } else {
                String message = p_requestVO.getMessageCode();
                if (message.indexOf("_") != -1) {
                    message = message.substring(0, message.indexOf("_"));
                }
                sbf.append("<TXNSTATUS>").append(message).append("</TXNSTATUS>");
            }

            sbf.append("<DATE>").append(BTSLDateUtil.getSystemLocaleDate(sdf.format(date), EXTERNAL_DATE_FORMAT) 
).append("</DATE>");

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
                sbf.append("<MESSAGE>").append(getMessage(p_requestVO.getLocale(), PretupsErrorCodesI.C2S_SENDER_SUCCESS, p_requestVO.getMessageArguments())).append("</MESSAGE>");
            }
            // sbf.append("<MESSAGE></MESSAGE>");
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateChnlRoamReversalResponse]",
                PretupsErrorCodesI.C2S_ERROR_EXCEPTION, "", "", "generateChnlRoamReversalResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }

/**
	 * Method to parse External Interface Last X Transfer Service Wise Request
	 * @param p_requestVO
	 * @throws Exception
	 * @author harsh.dixit
	 */
	public static void parseExtLastXTransferServiceWiseEnq(RequestVO p_requestVO) throws BTSLBaseException{
		final String methodName="parseExtLastXTransferServiceWiseEnq";
		if(_log.isDebugEnabled())_log.debug(methodName,"Entered p_requestVO: "+p_requestVO.toString());
		String parsedRequestStr=null;
		try{
			HashMap<String,String> requestHashMap =new HashMap<String,String>();
			p_requestVO.setRequestMap(requestHashMap);
		    String requestStr=p_requestVO.getRequestMessage();
		    String msisdn=null;
		    String extRefNumber=null;
		    String password=null;
		    String extCode=null;
		    String extNwCode=null;
		    String date=null;
		    String language1=null;
		    String txnType=null;
		    String serviceType=null;
		    String c2cInOut=null;
		    String msisdn1=null;
		    String noOfLastNTxn=null;
			String maxNoOfAllowedTxns=null;
		    maxNoOfAllowedTxns=Constants.getProperty("MAX_NO_OF_ALLOWED_TXNS");
		    if(BTSLUtil.isNullString(maxNoOfAllowedTxns))
		    	maxNoOfAllowedTxns="10";
		    int index=requestStr.indexOf("<TYPE>");
		    String type=requestStr.substring(index+"<TYPE>".length(),requestStr.indexOf("</TYPE>",index));
		    requestHashMap.put("TYPE",type);
		    index=requestStr.indexOf("<DATE>");
		    if(index>0){
		        date=requestStr.substring(index+"<DATE>".length(),requestStr.indexOf("</DATE>",index));
		        date= BTSLDateUtil.getGregorianDateInString(date);
			    requestHashMap.put("DATE",date);
		    }
		    index=requestStr.indexOf("<EXTNWCODE>");
	        extNwCode=requestStr.substring(index+"<EXTNWCODE>".length(),requestStr.indexOf("</EXTNWCODE>",index));
		    requestHashMap.put("EXTNWCODE",extNwCode);
		    index=requestStr.indexOf("<MSISDN>");
		    if(index>0){
		        msisdn=requestStr.substring(index+"<MSISDN>".length(),requestStr.indexOf("</MSISDN>",index));
			    requestHashMap.put("MSISDN",msisdn);
		    }
		    index=requestStr.indexOf("<PIN>");
		    String pin=requestStr.substring(index+"<PIN>".length(),requestStr.indexOf("</PIN>",index));
		    requestHashMap.put("PIN",pin);
		    index=requestStr.indexOf("<LOGINID>");
		    String loginId=requestStr.substring(index+"<LOGINID>".length(),requestStr.indexOf("</LOGINID>",index));
		    requestHashMap.put("LOGINID",loginId);
		    index=requestStr.indexOf("<PASSWORD>");
		    if(index>0){
		        password=requestStr.substring(index+"<PASSWORD>".length(),requestStr.indexOf("</PASSWORD>",index));
			    requestHashMap.put("PASSWORD",password);
		    }
			index=requestStr.indexOf("<EXTCODE>");
			if(index>0){
			    extCode=requestStr.substring(index+"<EXTCODE>".length(),requestStr.indexOf("</EXTCODE>",index));
				requestHashMap.put("EXTCODE",extCode);
			}
		    index=requestStr.indexOf("<EXTREFNUM>");
		    if(index>0){
		        extRefNumber=requestStr.substring(index+"<EXTREFNUM>".length(),requestStr.indexOf("</EXTREFNUM>",index));
			    requestHashMap.put("EXTREFNUM",extRefNumber);
		    }
		    index=requestStr.indexOf("<TRANSACTION_TYPE>");
		    if(index>0){
		        txnType=requestStr.substring(index+"<TRANSACTION_TYPE>".length(),requestStr.indexOf("</TRANSACTION_TYPE>",index));
			    requestHashMap.put("TRANSACTION_TYPE",txnType);
		    }
		    index=requestStr.indexOf("<SERVICE_TYPE>");
		    if(index>0){
		        serviceType=requestStr.substring(index+"<SERVICE_TYPE>".length(),requestStr.indexOf("</SERVICE_TYPE>",index));
		        if(PretupsI.C2S_MODULE.equals(txnType)&&BTSLUtil.isNullString(serviceType))
		        	throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.LASTX_TRANSFER_SERVICE_TYPE_BLANK);
			    requestHashMap.put("SERVICE_TYPE",serviceType);
		    }
		    index=requestStr.indexOf("<C2C_INOUT>");
		    if(index>0){
		        c2cInOut=requestStr.substring(index+"<C2C_INOUT>".length(),requestStr.indexOf("</C2C_INOUT>",index));
		        if(PretupsI.C2C_MODULE.equals(txnType)&& BTSLUtil.isNullString(c2cInOut))
		        	throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.LASTX_TRANSFER_C2C_INOUT_BLANK);
			    requestHashMap.put("C2C_INOUT",c2cInOut);
		    }
		    index=requestStr.indexOf("<MSISDN1>");
		    if(index>0){
		        msisdn1=requestStr.substring(index+"<MSISDN1>".length(),requestStr.indexOf("</MSISDN1>",index));
				if(BTSLUtil.isNullString(msisdn1))
		        	throw new BTSLBaseException("XMLStringParser", methodName, PretupsErrorCodesI.LASTX_TRANSFER_MSISDN1_BLANK);
			    requestHashMap.put("MSISDN1",msisdn1);
		    }
		    index=requestStr.indexOf("<NUMBER_OF_LAST_N_TXN>");
		    if(index>0){
		        noOfLastNTxn=requestStr.substring(index+"<NUMBER_OF_LAST_N_TXN>".length(),requestStr.indexOf("</NUMBER_OF_LAST_N_TXN>",index));
		        if(!BTSLUtil.isNullString(noOfLastNTxn))
		        {
		        	if(!BTSLUtil.isNumeric(noOfLastNTxn))
		        		throw new BTSLBaseException("XMLStringParser",methodName,PretupsErrorCodesI.LASTX_TRANSFER_NOOFTXN_NOTNUMERIC);
		        	if(Integer.parseInt(noOfLastNTxn)>Integer.parseInt(maxNoOfAllowedTxns))
		        		throw new BTSLBaseException("XMLStringParser",methodName,PretupsErrorCodesI.LASTX_TRANSFER_NOOFTXN_NOTALLOWED,0,new String[]{maxNoOfAllowedTxns},null);
		        }
			    requestHashMap.put("NUMBER_OF_LAST_N_TXN",noOfLastNTxn);
		    }
		    index=requestStr.indexOf("<LANGUAGE1>");
		    if(index>0){
			    language1=requestStr.substring(index+"<LANGUAGE1>".length(),requestStr.indexOf("</LANGUAGE1>",index));
				requestHashMap.put("LANGUAGE1",language1);
				if(!BTSLUtil.isNullString(language1))
				    p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
			}
		    if(BTSLUtil.isNullString(type) || BTSLUtil.isNullString(extNwCode) ||BTSLUtil.isNullString(language1)||BTSLUtil.isNullString(txnType)||BTSLUtil.isNullString(noOfLastNTxn) ){
		        throw new BTSLBaseException("XMLStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
		    }
            parsedRequestStr=PretupsI.SERVICE_TYPE_LASTX_TRANSFER_SERVICEWISE_REPORT;
			p_requestVO.setExternalNetworkCode(extNwCode);
			p_requestVO.setSenderExternalCode(extCode);
			p_requestVO.setSenderLoginID(loginId);
			p_requestVO.setRequestMSISDN(msisdn);
			p_requestVO.setRequestMap(requestHashMap);
			p_requestVO.setExternalReferenceNum(extRefNumber);
		    p_requestVO.setDecryptedMessage(parsedRequestStr);
		    p_requestVO.setRequestMap(requestHashMap);
		}
		catch(BTSLBaseException be){
			_log.errorTrace(methodName, be);
			if(be.isKey()){
			    p_requestVO.setMessageCode(be.getMessageKey());
			    p_requestVO.setMessageArguments(be.getArgs());
			}
			throw be;
		}
		catch(Exception e){
			_log.errorTrace(methodName, e);
			p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
		    throw new BTSLBaseException("XMLStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
		}
		finally{
			if(_log.isDebugEnabled())_log.debug(methodName,"Exiting p_requestVO: "+p_requestVO.toString());
		}
	}
	
	/**
	 * generateExtLastXTrfSrvcWiseResponse
	 * Response of Last X Transfer Service Wise from the external system.
	 * @param p_requestVO
	 * @throws Exception
	 * @author harsh.dixit
	 */
	public static void generateExtLastXTrfSrvcWiseResponse(RequestVO p_requestVO) throws Exception{
		final String methodName="generateExtLastXTrfSrvcWiseResponse";
	    if(_log.isDebugEnabled())_log.debug(methodName,"Entered p_requestVO: "+p_requestVO.toString());
	    String responseStr= null;
		StringBuffer sbf=null;
		try{
		    sbf=new StringBuffer(1024);
		    sbf.append("<?xml version=\"1.0\"?><COMMAND>");
			sbf.append("<TYPE>EXLASTXSERENQRESP</TYPE>");
			if(p_requestVO.isSuccessTxn())
				sbf.append("<REQSTATUS>"+PretupsI.TXN_STATUS_SUCCESS+"</REQSTATUS>");
			else
			    sbf.append("<REQSTATUS>"+p_requestVO.getMessageCode()+"</REQSTATUS>");
			sbf.append("<DATE>"+BTSLDateUtil.getLocaleTimeStamp(getDateTime(null, null))+"</DATE>");
			sbf.append("<EXTREFNUM>"+p_requestVO.getExternalReferenceNum()+"</EXTREFNUM>");
			sbf.append("<MESSAGE>"+getMessage(p_requestVO.getLocale(),p_requestVO.getMessageCode(),p_requestVO.getMessageArguments())+"</MESSAGE>");
			sbf.append("<TXNDETAILS>");
			HashMap requestHashMap=p_requestVO.getRequestMap();
			if(requestHashMap!=null){
			    List lastTransferList=(List)requestHashMap.get("TRANSFERLIST");
			    if(lastTransferList!=null && !lastTransferList.isEmpty()){
			        for(int i=0;i<lastTransferList.size();i++){
				        C2STransferVO c2sTransferVO=(C2STransferVO)lastTransferList.get(i);{
				        if(c2sTransferVO!=null)
				            sbf.append("<TXNDETAIL>");
					        sbf.append("<TXNID>"+c2sTransferVO.getTransferID()+"</TXNID>");
					        sbf.append("<MSISDN>"+c2sTransferVO.getReceiverMsisdn()+"</MSISDN>");
							sbf.append("<TXNDATETIME>"+BTSLDateUtil.getLocaleTimeStamp(getDateTime(c2sTransferVO.getTransferDateTime(),null))+"</TXNDATETIME>");
							sbf.append("<TRFTYPE>"+c2sTransferVO.getType()+"</TRFTYPE>");
							sbf.append("<TXNSTATUS>"+c2sTransferVO.getStatus()+"</TXNSTATUS>");
							sbf.append("<TXNAMOUNT>"+PretupsBL.getDisplayAmount(c2sTransferVO.getTransferValue())+"</TXNAMOUNT>");
					        sbf.append("<ERRORCODE>"+c2sTransferVO.getErrorCode()+"</ERRORCODE>");
							sbf.append("</TXNDETAIL>");
				        }
				    }
			    }
			    else{
			        sbf.append("<TXNDETAIL>");
			    	sbf.append("<TXNID></TXNID>");
			    	sbf.append("<MSISDN></MSISDN>");
					sbf.append("<TXNDATETIME></TXNDATETIME>");
					sbf.append("<TRFTYPE></TRFTYPE>");
					sbf.append("<TXNSTATUS></TXNSTATUS>");
					sbf.append("<TXNAMOUNT></TXNAMOUNT>");
					sbf.append("<ERRORCODE></ERRORCODE>");
					sbf.append("</TXNDETAIL>");
			    }
			}
			sbf.append("</TXNDETAILS></COMMAND>");
			responseStr = sbf.toString();
			p_requestVO.setSenderReturnMessage(responseStr);
		}
		catch(Exception e){
			_log.errorTrace(methodName, e);
			p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"XMLStringParser[+"+methodName+"]",PretupsErrorCodesI.XML_ERROR_EXCEPTION,"","",methodName+":"+e.getMessage());
		}
		finally{
			if(_log.isDebugEnabled())_log.debug(methodName,"Exiting responseStr: "+responseStr);
		}
	}
	public static void parseP2PIATCreditRequest(RequestVO p_requestVO) throws BTSLBaseException,Exception{
		if(_log.isDebugEnabled())_log.debug("parseP2PIATCreditRequest","Entered p_requestVO: "+p_requestVO.toString());
		String parsedRequestStr=null;
		try
		{
		    String requestStr=p_requestVO.getRequestMessage();
		    int index=requestStr.indexOf("<MSISDN1>");
		    String msisdn1=requestStr.substring(index+"<MSISDN1>".length(),requestStr.indexOf("</MSISDN1>",index));
		    index=requestStr.indexOf("<PIN>");
		    String pin=requestStr.substring(index+"<PIN>".length(),requestStr.indexOf("</PIN>",index));
		    index=requestStr.indexOf("<MSISDN2>");
		    String msisdn2=requestStr.substring(index+"<MSISDN2>".length(),requestStr.indexOf("</MSISDN2>",index));
		    index=requestStr.indexOf("<AMOUNT>");
		    String amount=requestStr.substring(index+"<AMOUNT>".length(),requestStr.indexOf("</AMOUNT>",index));
		    index=requestStr.indexOf("<LANGUAGE1>");
		    String language1=requestStr.substring(index+"<LANGUAGE1>".length(),requestStr.indexOf("</LANGUAGE1>",index));
		    index=requestStr.indexOf("<LANGUAGE2>");
		    String language2=requestStr.substring(index+"<LANGUAGE2>".length(),requestStr.indexOf("</LANGUAGE2>",index));
		    index=requestStr.indexOf("<SELECTOR>");
		    String selector=requestStr.substring(index+"<SELECTOR>".length(),requestStr.indexOf("</SELECTOR>",index));
		    if(BTSLUtil.isNullString(amount))
		    {
		    	 	p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_BLANK_AMOUNT);
				    _log.error("parseP2PIATCreditRequest","Amount field is null");
				    throw new BTSLBaseException("XMLStringParser","parseP2PIATCreditRequest",PretupsErrorCodesI.P2P_ERROR_BLANK_AMOUNT);	
		    }
			if(BTSLUtil.isNullString(pin))
				pin= p2pDefaultSmsPin;
			if(_log.isDebugEnabled())_log.debug("parseP2PIATCreditRequest","language1:"+language1+":");
			if(BTSLUtil.isNullString(language1))
				language1=Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
			if(BTSLUtil.isNullString(language1))
				language1="0";
			if(_log.isDebugEnabled())_log.debug("parseP2PIATCreditRequest","language1:"+language1+":");
			if(_log.isDebugEnabled())_log.debug("parseP2PIATCreditRequest","language2:"+language2+":");
			if(BTSLUtil.isNullString(language2))
				language2=Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
			if(BTSLUtil.isNullString(language2))
				language2="0";
			if(_log.isDebugEnabled())_log.debug("parseP2PIATCreditRequest","language2:"+language2+":");
		    if(BTSLUtil.isNullString(selector))
			{
				ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(PretupsI.SERVICE_TYPE_P2PRECHARGE);
				if(serviceSelectorMappingVO!=null)
					selector=serviceSelectorMappingVO.getSelectorCode();
			}
		    parsedRequestStr=PretupsI.SERVICE_TYPE_P2PRECHARGE_IAT+P2P_MESSAGE_SEP+msisdn2+P2P_MESSAGE_SEP+amount+P2P_MESSAGE_SEP+selector+(P2P_MESSAGE_SEP+language2)+P2P_MESSAGE_SEP+pin;
			p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
			p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(language2));
			p_requestVO.setDecryptedMessage(parsedRequestStr);
		    p_requestVO.setRequestMSISDN(msisdn1);
		}
		catch(BTSLBaseException be)
		{
			_log.error("parseP2PIATCreditRequest"," BTSL Exception while parsing Request Message :"+be.getMessage());
			throw be;
		}
		catch(Exception e)
		{
		    p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
		    _log.error("parseP2PIATCreditRequest","Exception e: "+e);
		    throw new BTSLBaseException("XMLStringParser","parseP2PIATCreditRequest",PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug("parseP2PIATCreditRequest","Exiting p_requestVO: "+p_requestVO.toString());
		}
	}
	public static void generateP2PIATCreditResponse(RequestVO p_requestVO) throws Exception
    {
	    if(_log.isDebugEnabled())_log.debug("generateP2PIATCreditResponse","Entered p_requestVO: "+p_requestVO.toString());
	    String responseStr= null;
		StringBuffer sbf=null;
		try
		{
		    sbf=new StringBuffer(1024);
		    sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
			sbf.append("<TYPE>IATPRCRESP</TYPE>");
			if(BTSLUtil.isNullString(p_requestVO.getTransactionID()))
			    sbf.append("<TXNID></TXNID>");
			else
			    sbf.append("<TXNID>"+p_requestVO.getTransactionID()+"</TXNID>");    
		    if(p_requestVO.isSuccessTxn())
		        sbf.append("<TXNSTATUS>"+PretupsI.TXN_STATUS_SUCCESS+"</TXNSTATUS>");
			else
			    sbf.append("<TXNSTATUS>"+p_requestVO.getMessageCode()+"</TXNSTATUS>");
		    sbf.append("</COMMAND>");
		    responseStr = sbf.toString();
			p_requestVO.setSenderReturnMessage(responseStr);
		}
		catch(Exception e)
		{
			_log.error("generateP2PIATCreditResponse","Exception e: "+e);
			p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"XMLStringParser[generateP2PIATCreditResponse]",PretupsErrorCodesI.P2P_ERROR_EXCEPTION,"","","generateP2PIATCreditResponse:"+e.getMessage());
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug("generateP2PIATCreditResponse","Exiting responseStr: "+responseStr);
		}
    }
	public static void parsePromoVasTransferRequest(RequestVO p_requestVO) throws Exception{
		final String methodName = "parsePromoVasTransferRequest";
		if(_log.isDebugEnabled()) {
			_log.debug(methodName,"Entered p_requestVO: "+p_requestVO.toString());
		}
		String parsedRequestStr=null;
		String cellId=null;
		String extnwcode=null;
		String switchId=null;
		String bonus="0";
		String type="";
		int out_of_bound=-1;
		try
		{
			String info1=null;
		    String info2=null;
		    String info3=null;
		    String info4=null;
		    String info5=null;
		    String info6=null;
		    String info7=null;
		    String info8=null;
		    String info9=null;
		    String info10=null;
			HashMap requestHashMap =new HashMap();
			String requestStr=p_requestVO.getRequestMessage();
			int index=0;
			 index=requestStr.indexOf("<TYPE>");
			if(index>=0) {
				type=requestStr.substring(index+"<TYPE>".length(),requestStr.indexOf("</TYPE>",index));
			}
			index=requestStr.indexOf("<DATE>");
			String date=requestStr.substring(index+"<DATE>".length(),requestStr.indexOf("</DATE>",index));
			date = BTSLDateUtil.getGregorianDateInString(date);
			requestHashMap.put("DATE",date);
			if(p_requestVO.getRequestGatewayType().equalsIgnoreCase(PretupsI.REQUEST_SOURCE_TYPE_EXTGW))
			{
				index=requestStr.indexOf("<EXTNWCODE>");
				extnwcode=requestStr.substring(index+"<EXTNWCODE>".length(),requestStr.indexOf("</EXTNWCODE>",index));
				requestHashMap.put("EXTNWCODE",extnwcode);
			}
			index=requestStr.indexOf("<MSISDN>");
			String msisdn1=requestStr.substring(index+"<MSISDN>".length(),requestStr.indexOf("</MSISDN>",index));
			requestHashMap.put("MSISDN",msisdn1);
			index=requestStr.indexOf("<PIN>");
			String pin=requestStr.substring(index+"<PIN>".length(),requestStr.indexOf("</PIN>",index));
			requestHashMap.put("PIN",pin);
			index=requestStr.indexOf("<LOGINID>");
			String loginId=requestStr.substring(index+"<LOGINID>".length(),requestStr.indexOf("</LOGINID>",index));
			p_requestVO.setLogin(loginId);
			p_requestVO.setSenderLoginID(loginId);
			requestHashMap.put("LOGINID",loginId);
			index=requestStr.indexOf("<PASSWORD>");
			String password=requestStr.substring(index+"<PASSWORD>".length(),requestStr.indexOf("</PASSWORD>",index));
			p_requestVO.setPassword(password);
			requestHashMap.put("PASSWORD",password);
			index=requestStr.indexOf("<EXTCODE>");
			String extCode=requestStr.substring(index+"<EXTCODE>".length(),requestStr.indexOf("</EXTCODE>",index));
			p_requestVO.setSenderExternalCode(extCode);
			requestHashMap.put("EXTCODE",extCode);
			index=requestStr.indexOf("<EXTREFNUM>");
			String extRefNumber=requestStr.substring(index+"<EXTREFNUM>".length(),requestStr.indexOf("</EXTREFNUM>",index));
			p_requestVO.setExternalReferenceNum(extRefNumber);
			requestHashMap.put("EXTREFNUM",extRefNumber);
			index=requestStr.indexOf("<MSISDN2>");
			String msisdn2=requestStr.substring(index+"<MSISDN2>".length(),requestStr.indexOf("</MSISDN2>",index));
			requestHashMap.put("MSISDN2",msisdn2);
			index=requestStr.indexOf("<AMOUNT>");
			String amount=requestStr.substring(index+"<AMOUNT>".length(),requestStr.indexOf("</AMOUNT>",index));
			requestHashMap.put("AMOUNT",amount);
			p_requestVO.setAmount1String(amount);
			index=requestStr.indexOf("<LANGUAGE1>");
			String language1=requestStr.substring(index+"<LANGUAGE1>".length(),requestStr.indexOf("</LANGUAGE1>",index));
			requestHashMap.put("LANGUAGE1",language1);
			index=requestStr.indexOf("<LANGUAGE2>");
			String language2=requestStr.substring(index+"<LANGUAGE2>".length(),requestStr.indexOf("</LANGUAGE2>",index));
			requestHashMap.put("LANGUAGE2",language2);
			index=requestStr.indexOf("<SELECTOR>");
			String selector=requestStr.substring(index+"<SELECTOR>".length(),requestStr.indexOf("</SELECTOR>",index));
			requestHashMap.put("SELECTOR",selector);
			index=requestStr.indexOf("<BONUS>");
			bonus=requestStr.substring(index+"<BONUS>".length(),requestStr.indexOf("</BONUS>",index));
			requestHashMap.put("BONUS",bonus);
			index=requestStr.indexOf("<EXTERNALDATA1>");
		    if(index!=out_of_bound) {
					info1=requestStr.substring(index+"<EXTERNALDATA1>".length(),requestStr.indexOf("</EXTERNALDATA1>",index));
			}
		    index=requestStr.indexOf("<EXTERNALDATA2>");
		    if(index!=out_of_bound) {
				info2=requestStr.substring(index+"<EXTERNALDATA2>".length(),requestStr.indexOf("</EXTERNALDATA2>",index));
			}
		    index=requestStr.indexOf("<EXTERNALDATA3>");
		    if(index!=out_of_bound) {
				info3=requestStr.substring(index+"<EXTERNALDATA3>".length(),requestStr.indexOf("</EXTERNALDATA3>",index));
			}
		    index=requestStr.indexOf("<EXTERNALDATA4>");
		    if(index!=out_of_bound) {
				info4=requestStr.substring(index+"<EXTERNALDATA4>".length(),requestStr.indexOf("</EXTERNALDATA4>",index));
			}
		    index=requestStr.indexOf("<EXTERNALDATA5>");
		    if(index!=out_of_bound) {
				info5=requestStr.substring(index+"<EXTERNALDATA5>".length(),requestStr.indexOf("</EXTERNALDATA5>",index));
			}
		    index=requestStr.indexOf("<EXTERNALDATA6>");
		    if(index!=out_of_bound) {
				info6=requestStr.substring(index+"<EXTERNALDATA6>".length(),requestStr.indexOf("</EXTERNALDATA6>",index));
			}
		    index=requestStr.indexOf("<EXTERNALDATA7>");
		    if(index!=out_of_bound) {
				info7=requestStr.substring(index+"<EXTERNALDATA7>".length(),requestStr.indexOf("</EXTERNALDATA7>",index));
			}
		    index=requestStr.indexOf("<EXTERNALDATA8>");
		    if(index!=out_of_bound) {
				info8=requestStr.substring(index+"<EXTERNALDATA8>".length(),requestStr.indexOf("</EXTERNALDATA8>",index));
			}
		    index=requestStr.indexOf("<EXTERNALDATA9>");
		    if(index!=out_of_bound) {
				info9=requestStr.substring(index+"<EXTERNALDATA9>".length(),requestStr.indexOf("</EXTERNALDATA9>",index));
			}
		    index=requestStr.indexOf("<EXTERNALDATA10>");
		    if(index!=out_of_bound) {
				info10=requestStr.substring(index+"<EXTERNALDATA10>".length(),requestStr.indexOf("</EXTERNALDATA10>",index));
			}
			index=requestStr.indexOf("<CELLID>");
			if(index>=0) {
				cellId=requestStr.substring(index+"<CELLID>".length(),requestStr.indexOf("</CELLID>",index));
			}
			index=requestStr.indexOf("<SWITCHID>");
			if(index>=0) {
				switchId=requestStr.substring(index+"<SWITCHID>".length(),requestStr.indexOf("</SWITCHID>",index));
			}
			requestHashMap.put("CELLID",cellId);
			requestHashMap.put("SWITCHID",switchId);
			p_requestVO.setRequestMap(requestHashMap);
			if (type != null && type.length()>0 && type.equals(PretupsI.PROMOVAS_EXTGW_TYPE))
			{
			//	XMLStringValidation.validateChannelExtRecharge(p_requestVO,type,date,extnwcode,msisdn1,pin,loginId,password,extCode,extRefNumber,msisdn2,amount,language1,language2,selector,bonus);
				XMLStringValidation.validateChannelExtRecharge(p_requestVO,type,date,extnwcode,msisdn1,pin,loginId,password,extCode,extRefNumber,msisdn2,amount,language1,language2,selector,bonus,info1,info2,info3,info4,info5,info6,info7,info8,info9,info10,switchId,cellId);
				
			}
			if (BTSLUtil.isNullString(info4))
				{
						info4="PRE";
				}
			else if(!info4.equalsIgnoreCase("pre") && !info4.equalsIgnoreCase("post"))
				{
					info4="PRE";
				}
			if(BTSLUtil.isNullString(amount))
			{
				amount="0";
			}
			if(BTSLUtil.isNullString(pin))
			{
			    pin="0";	
			}
			if(BTSLUtil.isNullString(msisdn2))
			{
					_log.error(methodName,"msisdn2 field is null ");
					String[] strArr=new String[]{msisdn2,String.valueOf(minMsisdnLength),String.valueOf(maxMsisdnLength)};
					throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.CHNL_ERROR_RECR_MSISDN_NOTINRANGE,0,strArr,null);
			}
			 if(!(BTSLUtil.isNullString(info1)))
			    {
	            	int length = info1.length();
	            	if(length>100)
	            	{
	            		_log.debug(methodName, "length is "+length);
			    	 	 _log.error(methodName,"Characters are more than 100 ");
					    throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_MAX_100_EXTERNALDATA1);	
	            	}
	            }		
	            if(!(BTSLUtil.isNullString(info2)))
			    {
	            	int length = info2.length();
	            	if(length>100)
	            	{
	            		_log.debug(methodName, "length is "+length);
			    	 	 _log.error(methodName,"Characters are more than 100 ");
					    throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_MAX_100_EXTERNALDATA2);	
	            	}
	            }		
	            if(!(BTSLUtil.isNullString(info3)))
			    {
	            	int length = info3.length();
	            	if(length>100)
	            	{
	            		_log.debug(methodName, "length is "+length);
			    	 	 _log.error(methodName,"Characters are more than 100 ");
					    throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_MAX_100_EXTERNALDATA3);	
	            	}
	            }		
	            if(!(BTSLUtil.isNullString(info4)))
			    {
	            	int length = info4.length();
	            	if(length>100)
	            	{
	            		_log.debug(methodName, "length is "+length);
			    	 	 _log.error(methodName,"Characters are more than 100 ");
					    throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_MAX_100_EXTERNALDATA4);	
	            	}
	            }		
	            if(!(BTSLUtil.isNullString(info5)))
			    {
	            	int length = info5.length();
	            	if(length>100)
	            	{
	            		_log.debug(methodName, "length is "+length);
			    	 	 _log.error(methodName,"Characters are more than 100 ");
					    throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_MAX_100_EXTERNALDATA5);	
	            	}
	            }		
	            if(!(BTSLUtil.isNullString(info6)))
			    {
	            	int length = info6.length();
	            	if(length>100)
	            	{
	            		_log.debug(methodName, "length is "+length);
			    	 	 _log.error(methodName,"Characters are more than 100 ");
					    throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_MAX_100_EXTERNALDATA6);	
	            	}
	            }		
	            if(!(BTSLUtil.isNullString(info7)))
			    {
	            	int length = info7.length();
	            	if(length>100)
	            	{
	            		_log.debug(methodName, "length is "+length);
			    	 	 _log.error(methodName,"Characters are more than 100 ");
					    throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_MAX_100_EXTERNALDATA7);	
	            	}
	            }	
	            if(!(BTSLUtil.isNullString(info8)))
			    {
	            	int length = info8.length();
	            	if(length>100)
	            	{
	            		_log.debug(methodName, "length is "+length);
			    	 	 _log.error(methodName,"Characters are more than 100 ");
					    throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_MAX_100_EXTERNALDATA8);	
	            	}
	            }		
	            if(!(BTSLUtil.isNullString(info9)))
			    {
	            	int length = info9.length();
	            	if(length>100)
	            	{
	            		_log.debug(methodName, "length is "+length);
			    	 	 _log.error(methodName,"Characters are more than 100 ");
					    throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_MAX_100_EXTERNALDATA9);	
	            	}
	            }		
	            if(!(BTSLUtil.isNullString(info10)))
			    {
	            	int length = info10.length();
	            	if(length>100)
	            	{
	            		_log.debug(methodName, "length is "+length);
			    	 	 _log.error(methodName,"Characters are more than 100 ");
					    throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_MAX_100_EXTERNALDATA10);	
	            	}
	            }
			if(BTSLUtil.isNullString(selector))
			{
				ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(PretupsI.SERVICE_TYPE_PVAS_RECHARGE);
				if(serviceSelectorMappingVO!=null) {
					selector=serviceSelectorMappingVO.getSelectorCode();
				}
			}
			if(isUssdNewTagsMandatory) {
				try{
					if(BTSLUtil.isNullString(cellId))
					{
						throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
					}
					if(BTSLUtil.isNullString(switchId))
					{
						throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
					}
				}catch(BTSLBaseException btsle){
					throw btsle;
				}
			}
			parsedRequestStr=PretupsI.SERVICE_TYPE_PVAS_RECHARGE+CHNL_MESSAGE_SEP+msisdn2+CHNL_MESSAGE_SEP+amount+CHNL_MESSAGE_SEP+selector+CHNL_MESSAGE_SEP+language2+CHNL_MESSAGE_SEP+pin;
			p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
			p_requestVO.setReqSelector(selector);
			p_requestVO.setDecryptedMessage(parsedRequestStr);
			p_requestVO.setRequestMSISDN(msisdn1);
			p_requestVO.setCellId(cellId);
			p_requestVO.setSwitchId(switchId);
			p_requestVO.setExternalNetworkCode(extnwcode);
			p_requestVO.setPromoBonus(bonus);
			if(p_requestVO.getRequestGatewayType().equalsIgnoreCase(PretupsI.REQUEST_SOURCE_TYPE_EXTGW)) {
				p_requestVO.setExternalNetworkCode(extnwcode);
				p_requestVO.setSenderExternalCode(extCode);
				p_requestVO.setSenderLoginID(loginId);
				p_requestVO.setPassword(password);
				p_requestVO.setExternalReferenceNum(extRefNumber);
				p_requestVO.setType(type);
			}
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
			try {
				p_requestVO.setCommissionType(PretupsI.OTF_COMMISSION);
				if(Double.parseDouble(bonus)> 0){
					p_requestVO.setCommission(Long.toString(PretupsBL.getSystemAmount(Double.parseDouble(bonus))));
					p_requestVO.setCommissionApplicable(PretupsI.YES);
				}else{
					p_requestVO.setCommissionApplicable(PretupsI.NO);
					p_requestVO.setCommission(bonus);
				}
			}catch (Exception e) {
				 _log.error(methodName, "Exception " + e);
				 _log.errorTrace(methodName,e);
				bonus="0";
				p_requestVO.setCommissionApplicable(PretupsI.NO);
				p_requestVO.setCommission(bonus);
			}
		} catch(BTSLBaseException be) {
			_log.error(methodName," BTSL Exception while parsing Request Message :"+be.getMessage());
			throw be;
		} catch(Exception e) {
			_log.errorTrace(methodName,e);
			p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
			_log.error(methodName,"Exception e: "+e);
			throw new BTSLBaseException(methodName,methodName,PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
		} finally {
			if(_log.isDebugEnabled()) {
				_log.debug(methodName,"Exiting p_requestVO: "+p_requestVO.toString());
			}
		}
	}
	public static void generatePromoVASTransferResponse(RequestVO p_requestVO) throws Exception
	{
		final String methodName = "generatePromoVASTransferResponse";
		if(_log.isDebugEnabled()) {
			_log.debug(methodName,"Entered p_requestVO: "+p_requestVO.toString());
		}
		String responseStr= null;
		StringBuffer sbf=null;
		java.util.Date date=new java.util.Date();
		try {
			String EXTERNAL_DATE_FORMAT = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_DATE_FORMAT);
			SimpleDateFormat sdf = new SimpleDateFormat ("dd-MM-yyyy HH:mm:ss");
			sdf.setLenient(false); // this is required else it will convert
			sbf=new StringBuffer(1024);
			sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
			sbf.append("<TYPE>EXTPROMOVASTRFRESP</TYPE>");
			if(p_requestVO.isSuccessTxn()) {
				sbf.append("<TXNSTATUS>"+PretupsI.TXN_STATUS_SUCCESS+"</TXNSTATUS>");
			} else {
				String message = p_requestVO.getMessageCode();
				if(message.indexOf("_") != -1)
				{
					message = message.substring(0,message.indexOf("_"));
				}
				sbf.append("<TXNSTATUS>"+message+"</TXNSTATUS>");
			}
			sbf.append("<DATE>"+BTSLDateUtil.getSystemLocaleDate(sdf.format(date), EXTERNAL_DATE_FORMAT)+"</DATE>");
			if(p_requestVO.getRequestMap() != null) {
				sbf.append("<EXTREFNUM>"+(String)p_requestVO.getRequestMap().get("EXTREFNUM")+"</EXTREFNUM>");
			} else {
				sbf.append("<EXTREFNUM></EXTREFNUM>");
			} 
			if(BTSLUtil.isNullString(p_requestVO.getTransactionID())) {
				sbf.append("<TXNID></TXNID>");
			} else {
				sbf.append("<TXNID>"+p_requestVO.getTransactionID()+"</TXNID>");
			}
		//	String[] mesgArg= p_requestVO.getMessageArguments();
			if(!p_requestVO.isSuccessTxn()) { 
					sbf.append("<AMOUNT></AMOUNT>");
			} else {
					sbf.append("<AMOUNT>"+p_requestVO.getAmount1String()+"</AMOUNT>");
			}
			if(p_requestVO.isSuccessTxn()) { 
					sbf.append("<TXNDATE>"+BTSLDateUtil.getSystemLocaleDate(sdf.format(p_requestVO.getRequestStartTime()), EXTERNAL_DATE_FORMAT)+"</TXNDATE>"); 
			} else {
					sbf.append("<TXNDATE></TXNDATE>"); 
			}	
			if(BTSLUtil.isNullString(p_requestVO.getServiceType())) { 
					sbf.append("<SERVICETYPE></SERVICETYPE>");
			} else { 
					sbf.append("<SERVICETYPE>"+p_requestVO.getServiceType()+"</SERVICETYPE>");
			}	
			if(isChannelTransferInfoRequired){
			sbf.append("<PREBAL>"+(p_requestVO.getRequestMap().get("PREBAL")!=null?p_requestVO.getRequestMap().get("PREBAL"):"")+"</PREBAL>");
			sbf.append("<POSTBAL>"+(p_requestVO.getRequestMap().get("POSTBAL")!=null?p_requestVO.getRequestMap().get("POSTBAL"):"")+"</POSTBAL>");
			sbf.append("<USERID>"+(p_requestVO.getSenderVO()!=null?((ChannelUserVO)p_requestVO.getSenderVO()).getUserID():"")+"</USERID>");					
			
			}
			if(!p_requestVO.isSuccessTxn()) {
				sbf.append("<MESSAGE>"+getMessage(p_requestVO.getLocale(),p_requestVO.getMessageCode(),p_requestVO.getMessageArguments())+"</MESSAGE>");
			} else {
				//Changed as per IRIS team required through EXTGW
				String message=getMessage(p_requestVO.getLocale(),PretupsErrorCodesI.PROMO_VAS_SUCCESS+"_"+p_requestVO.getRequestGatewayType(),p_requestVO.getMessageArguments());
				if(BTSLUtil.isNullString(message)) {
					// message=getMessage(p_requestVO.getLocale(),PretupsErrorCodesI.PROMO_VAS_SUCCESS,p_requestVO.getDecryptedMessage().split(" "));
					 message=getMessage(p_requestVO.getLocale(),PretupsErrorCodesI.PROMO_VAS_SUCCESS,p_requestVO.getMessageArguments());
				}
				sbf.append("<MESSAGE>"+message+"</MESSAGE>");
			}
			sbf.append("</COMMAND>");
			responseStr = sbf.toString();
			p_requestVO.setSenderReturnMessage(responseStr);
		} catch(Exception e) {
			_log.errorTrace(methodName,e);
			p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ExtAPIXMLStringParser["+methodName+"]",PretupsErrorCodesI.XML_ERROR_EXCEPTION,"","",""+methodName+":"+e.getMessage());
		} finally {
			if(_log.isDebugEnabled()) {
				_log.debug(methodName,"Exiting responseStr: "+responseStr);
			}
		}
	}
	public static void parsePrepaidRCReversalRequest(RequestVO p_requestVO) throws Exception{
		final String methodName = "parsePrepaidRCReversalRequest";
		if(_log.isDebugEnabled()) {
			_log.debug(methodName,"Entered p_requestVO: "+p_requestVO.toString());
		}
		String parsedRequestStr=null;
		String cellId=null;
		String extnwcode=null;
		String switchId=null;
		String type="";
		try
		{
			HashMap requestHashMap =new HashMap();
			String requestStr=p_requestVO.getRequestMessage();
			int index=0;
			String loginId=null;
			String password=null;
			String extCode=null;
			String extRefNumber=null;
			index=requestStr.indexOf("<TYPE>");
			if(index>=0) {
				type=requestStr.substring(index+"<TYPE>".length(),requestStr.indexOf("</TYPE>",index));
			}
			if(p_requestVO.getRequestGatewayType().equalsIgnoreCase(PretupsI.REQUEST_SOURCE_TYPE_EXTGW))
			{
				index=requestStr.indexOf("<EXTNWCODE>");
				extnwcode=requestStr.substring(index+"<EXTNWCODE>".length(),requestStr.indexOf("</EXTNWCODE>",index));
				requestHashMap.put("EXTNWCODE",extnwcode);
			}
			index=requestStr.indexOf("<DATE>");
			String date=requestStr.substring(index+"<DATE>".length(),requestStr.indexOf("</DATE>",index));
			date = BTSLDateUtil.getGregorianDateInString(date);
			requestHashMap.put("DATE",date);
			index=requestStr.indexOf("<MSISDN>");
			String msisdn1=requestStr.substring(index+"<MSISDN>".length(),requestStr.indexOf("</MSISDN>",index));
			requestHashMap.put("MSISDN",msisdn1);
			index=requestStr.indexOf("<PIN>");
			String pin=requestStr.substring(index+"<PIN>".length(),requestStr.indexOf("</PIN>",index));
			requestHashMap.put("PIN",pin);
			index=requestStr.indexOf("<LOGINID>");
			if(index>=0){
			 loginId=requestStr.substring(index+"<LOGINID>".length(),requestStr.indexOf("</LOGINID>",index));
			p_requestVO.setLogin(loginId);
			requestHashMap.put("LOGINID",loginId);
			}
			index=requestStr.indexOf("<PASSWORD>");
			if(index>=0){
			 password=requestStr.substring(index+"<PASSWORD>".length(),requestStr.indexOf("</PASSWORD>",index));
			p_requestVO.setPassword(password);
			requestHashMap.put("PASSWORD",password);
			}
			index=requestStr.indexOf("<EXTCODE>");
			if(index>=0){
			 extCode=requestStr.substring(index+"<EXTCODE>".length(),requestStr.indexOf("</EXTCODE>",index));
			p_requestVO.setSenderExternalCode(extCode);
			requestHashMap.put("EXTCODE",extCode);
			}
			index=requestStr.indexOf("<EXTREFNUM>");
			if(index>=0){
			 extRefNumber=requestStr.substring(index+"<EXTREFNUM>".length(),requestStr.indexOf("</EXTREFNUM>",index));
			p_requestVO.setExternalReferenceNum(extRefNumber);
			requestHashMap.put("EXTREFNUM",extRefNumber);
			}
			index=requestStr.indexOf("<MSISDN2>");
			String msisdn2=requestStr.substring(index+"<MSISDN2>".length(),requestStr.indexOf("</MSISDN2>",index));
			requestHashMap.put("MSISDN2",msisdn2);
			index=requestStr.indexOf("<TXNID>");
			String txnId=requestStr.substring(index+"<TXNID>".length(),requestStr.indexOf("</TXNID>",index));
			requestHashMap.put("TXNID",txnId);
			index=requestStr.indexOf("<LANGUAGE1>");
			String language1=requestStr.substring(index+"<LANGUAGE1>".length(),requestStr.indexOf("</LANGUAGE1>",index));
			requestHashMap.put("LANGUAGE1",language1);
			index=requestStr.indexOf("<LANGUAGE2>");
			String language2=requestStr.substring(index+"<LANGUAGE2>".length(),requestStr.indexOf("</LANGUAGE2>",index));
			requestHashMap.put("LANGUAGE2",language2);
			index=requestStr.indexOf("<CELLID>");
			p_requestVO.setRequestMap(requestHashMap);
			if (type != null && type.length()>0 && type.equals(PretupsI.REVERSAL_EXTGW_TYPE))
			{
				XMLStringValidation.validateChannelExtRechargeReversal(p_requestVO,type,date,extnwcode,msisdn1,pin,loginId,password,extCode,extRefNumber,msisdn2,language1,language2);
			}
			if(BTSLUtil.isNullString(txnId))
			{
				txnId="0";
			}
			if(BTSLUtil.isNullString(msisdn2))
			{
					_log.error(methodName,"msisdn2 field is null ");
					String[] strArr=new String[]{msisdn2,String.valueOf(minMsisdnLength),String.valueOf(maxMsisdnLength)};
					throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.CHNL_ERROR_RECR_MSISDN_NOTINRANGE,0,strArr,null);
			}
			if(isUssdNewTagsMandatory)
			{
				try{
					if(BTSLUtil.isNullString(cellId))
					{
						throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
					}
					if(BTSLUtil.isNullString(switchId))
					{
						throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
					}
				}catch(BTSLBaseException btsle){
					throw btsle;
				}
			}
			if(BTSLUtil.isNullString(language1) && BTSLUtil.isNullString(language2) ){
				parsedRequestStr=PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL+CHNL_MESSAGE_SEP+msisdn2+CHNL_MESSAGE_SEP+txnId;
			}else if(!BTSLUtil.isNullString(language1) && BTSLUtil.isNullString(language2))
			{
				parsedRequestStr=PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL+CHNL_MESSAGE_SEP+msisdn2+CHNL_MESSAGE_SEP+txnId+CHNL_MESSAGE_SEP+language1;
				p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
			}else if(BTSLUtil.isNullString(language1) && !BTSLUtil.isNullString(language2))
			{
				parsedRequestStr=PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL+CHNL_MESSAGE_SEP+msisdn2+CHNL_MESSAGE_SEP+txnId+CHNL_MESSAGE_SEP+language2;
				p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(language2));
			}else if(!BTSLUtil.isNullString(language1) && !BTSLUtil.isNullString(language2)){
				parsedRequestStr=PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL+CHNL_MESSAGE_SEP+msisdn2+CHNL_MESSAGE_SEP+txnId+CHNL_MESSAGE_SEP+language1+CHNL_MESSAGE_SEP+language2;
				p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
				p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(language2));
			}
			p_requestVO.setDecryptedMessage(parsedRequestStr);
			p_requestVO.setRequestMSISDN(msisdn1);
			p_requestVO.setCellId(cellId);
			p_requestVO.setSwitchId(switchId);
			p_requestVO.setReceiverMsisdn(msisdn2); // Added by Naveen for making Reviever msisdn Non-mandatory
			if(p_requestVO.getRequestGatewayType().equalsIgnoreCase(PretupsI.REQUEST_SOURCE_TYPE_EXTGW))
			{
				if(!BTSLUtil.isNullString(extnwcode)) {
					p_requestVO.setExternalNetworkCode(extnwcode);
				}
				if(!BTSLUtil.isNullString(extCode)) {
					p_requestVO.setSenderExternalCode(extCode);
				}
				if(!BTSLUtil.isNullString(loginId)) {
					p_requestVO.setSenderLoginID(loginId);
				}
				if(!BTSLUtil.isNullString(password)) {
					p_requestVO.setPassword(password);
				}
				if(!BTSLUtil.isNullString(extRefNumber)) {
					p_requestVO.setExternalReferenceNum(extRefNumber);
				}
			}
		}
		catch(BTSLBaseException be)
		{
			_log.error(methodName," BTSL Exception while parsing Request Message :"+be.getMessage());
			throw be;
		}
		catch(Exception e)
		{
			_log.errorTrace(methodName,e);
			p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
			_log.error(methodName,"Exception e: "+e);
			throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
		}
		finally
		{
			if(_log.isDebugEnabled()) {
				_log.debug(methodName,"Exiting parsedRequestStr="+parsedRequestStr+"p_requestVO: "+p_requestVO.toString());
			}
		}
	}
public static void generateC2STransferResponse(RequestVO p_requestVO,String p_type) throws Exception
	{
		final String methodName = "generateC2STransferResponse";
		if(_log.isDebugEnabled()) {
			_log.debug(methodName,"Entered p_requestVO: "+p_requestVO.toString());
		}
		String responseStr= null;
		StringBuffer sbf=null;
		java.util.Date date=new java.util.Date();
		try
		{
			String ADDITIONAL_IN_FIELDS_ALLOWED = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.ADDITIONAL_IN_FIELDS_ALLOWED);
			String EXTERNAL_DATE_FORMAT = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_DATE_FORMAT);
			Boolean C2S_SEQID_ALWD = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_SEQID_ALWD);
			String C2S_SEQID_APPL_SER = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_SEQID_APPL_SER);
			String C2S_SEQID_FOR_GWC = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_SEQID_FOR_GWC);
			
			SimpleDateFormat sdf = new SimpleDateFormat ("dd-MM-yyyy HH:mm:ss");
			sdf.setLenient(false); // this is required else it will convert
			sbf=new StringBuffer(1024);
			sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
			sbf.append("<TYPE>"+p_type+"</TYPE>");
			if(p_requestVO.isSuccessTxn()) {
				sbf.append("<TXNSTATUS>"+PretupsI.TXN_STATUS_SUCCESS+"</TXNSTATUS>");
			} else
			{
				String message = p_requestVO.getMessageCode();
				if(message.indexOf("_") != -1)
				{
					message = message.substring(0,message.indexOf("_"));
				}
				sbf.append("<TXNSTATUS>"+message+"</TXNSTATUS>");
			}
			if(!p_requestVO.getRequestGatewayType().equalsIgnoreCase(PretupsI.GATEWAY_TYPE_USSD))
			{
			sbf.append("<DATE>"+sdf.format(date)+"</DATE>");
			
			String additionalINFieldsAllowed = ADDITIONAL_IN_FIELDS_ALLOWED;
			String[] allowedService=additionalINFieldsAllowed.split(",");

			if(p_requestVO.isSuccessTxn() && Arrays.asList(allowedService).contains(p_requestVO.getServiceType())){
				sbf.append("<EXPIRATIONDATE>"+p_requestVO.getNewExpiryDate()+"</EXPIRATIONDATE>");
			}
			
			
			if(p_requestVO.getRequestMap() != null) {
				sbf.append("<EXTREFNUM>"+(String)p_requestVO.getRequestMap().get("EXTREFNUM")+"</EXTREFNUM>");
			} else {
				sbf.append("<EXTREFNUM></EXTREFNUM>");
			} 
				String[] mesgArg= p_requestVO.getMessageArguments();
				if(!p_requestVO.isSuccessTxn()) 
						sbf.append("<AMOUNT></AMOUNT>");
					else
						sbf.append("<AMOUNT>"+mesgArg[2]+"</AMOUNT>");
				if(p_requestVO.isSuccessTxn()) 
						sbf.append("<TXNDATE>"+BTSLDateUtil.getSystemLocaleDate(sdf.format(p_requestVO.getRequestStartTime()), EXTERNAL_DATE_FORMAT)+"</TXNDATE>"); 
				else
						sbf.append("<TXNDATE></TXNDATE>"); 
				if(BTSLUtil.isNullString(p_requestVO.getServiceType())) 
						sbf.append("<SERVICETYPE></SERVICETYPE>");
					 else 
						sbf.append("<SERVICETYPE>"+p_requestVO.getServiceType()+"</SERVICETYPE>");
			}
			
			
			/** START: Birendra: CLARO
			 * If SystemPreferences.C2S_SEQID_ALWD is true AND if the current service type is listed in the SystemPreferences.C2S_SEQID_APPL_SER 
			 * AND if the current Gateway Code is listed in the required list of gateway codes 
			 * i.e. SystemPreferences.C2S_SEQID_FOR_GWC, then the response should contain the sequence id within <EXTREFNUM></EXTREFNUM> tag. 
			 * */
			if(C2S_SEQID_ALWD && BTSLUtil.isStringIn(p_requestVO.getServiceType(), C2S_SEQID_APPL_SER) && BTSLUtil.isStringIn(p_requestVO.getRequestGatewayCode(),C2S_SEQID_FOR_GWC)){
				sbf.append("<SEQID>"+((ChannelUserVO)p_requestVO.getSenderVO()).getUserPhoneVO().getOwnerTempTransferId()+"</SEQID>");
			}
			/** STOP: Birendra: CLARO */

			
			if(BTSLUtil.isNullString(p_requestVO.getTransactionID())) {
				sbf.append("<TXNID></TXNID>");
			} else {
				sbf.append("<TXNID>"+p_requestVO.getTransactionID()+"</TXNID>");
			}
			if(!p_requestVO.isSuccessTxn()) {
				sbf.append("<MESSAGE>"+getMessage(p_requestVO.getLocale(),p_requestVO.getMessageCode(),p_requestVO.getMessageArguments())+"</MESSAGE>");
			} else {
				if("RCREVRESP".equals(p_type))
				sbf.append("<MESSAGE>"+getMessage(p_requestVO.getLocale(),PretupsErrorCodesI.C2S_SENDER_SUCCESS_PRE_REVERSAL,p_requestVO.getMessageArguments())+"</MESSAGE>");
				else
					sbf.append("<MESSAGE>"+getMessage(p_requestVO.getLocale(),PretupsErrorCodesI.C2S_SENDER_SUCCESS,p_requestVO.getMessageArguments())+"</MESSAGE>");
			}	
			sbf.append("</COMMAND>");
			responseStr = sbf.toString();
			p_requestVO.setSenderReturnMessage(responseStr);
		}
		catch(Exception e)
		{
			_log.errorTrace(methodName,e);
			_log.error(methodName,"Exception e: "+e);
			p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ExtAPIXMLStringParser[generateC2STransferResponse]",PretupsErrorCodesI.XML_ERROR_EXCEPTION,"","","generateC2STransferResponse:"+e.getMessage());
		}
		finally
		{
			if(_log.isDebugEnabled()) {
				_log.debug(methodName,"Exiting responseStr: "+responseStr);
			}
		}
	}
public static void parseIntlTransferRequest(RequestVO p_requestVO) throws Exception{
	final String methodName = "parseIntTransferRequest";
	if(_log.isDebugEnabled()) {
		_log.debug(methodName,"Entered p_requestVO: "+p_requestVO.toString());
	}
	String parsedRequestStr=null;
	String cellId=null;
	String extnwcode=null;
	String switchId=null;
	String bonus="0";
	String type="";
	int out_of_bound=-1;
	try
	{
		String info1=null;
	    String info2=null;
	    String info3=null;
	    String info4=null;
	    String info5=null;
	    String info6=null;
	    String info7=null;
	    String info8=null;
	    String info9=null;
	    String info10=null;
		HashMap requestHashMap =new HashMap();
		String requestStr=p_requestVO.getRequestMessage();
		int index=0;
		 index=requestStr.indexOf("<TYPE>");
		if(index>=0) {
			type=requestStr.substring(index+"<TYPE>".length(),requestStr.indexOf("</TYPE>",index));
		}
		index=requestStr.indexOf("<DATE>");
		String date=requestStr.substring(index+"<DATE>".length(),requestStr.indexOf("</DATE>",index));
		date = BTSLDateUtil.getGregorianDateInString(date);
		requestHashMap.put("DATE",date);
		if(p_requestVO.getRequestGatewayType().equalsIgnoreCase(PretupsI.REQUEST_SOURCE_TYPE_EXTGW))
		{
			index=requestStr.indexOf("<EXTNWCODE>");
			extnwcode=requestStr.substring(index+"<EXTNWCODE>".length(),requestStr.indexOf("</EXTNWCODE>",index));
			requestHashMap.put("EXTNWCODE",extnwcode);
		}
		index=requestStr.indexOf("<MSISDN>");
		String msisdn1=requestStr.substring(index+"<MSISDN>".length(),requestStr.indexOf("</MSISDN>",index));
		requestHashMap.put("MSISDN",msisdn1);
		index=requestStr.indexOf("<PIN>");
		String pin=requestStr.substring(index+"<PIN>".length(),requestStr.indexOf("</PIN>",index));
		requestHashMap.put("PIN",pin);
		index=requestStr.indexOf("<LOGINID>");
		String loginId=requestStr.substring(index+"<LOGINID>".length(),requestStr.indexOf("</LOGINID>",index));
		p_requestVO.setLogin(loginId);
		requestHashMap.put("LOGINID",loginId);
		index=requestStr.indexOf("<PASSWORD>");
		String password=requestStr.substring(index+"<PASSWORD>".length(),requestStr.indexOf("</PASSWORD>",index));
		p_requestVO.setPassword(password);
		requestHashMap.put("PASSWORD",password);
		index=requestStr.indexOf("<EXTCODE>");
		String extCode=requestStr.substring(index+"<EXTCODE>".length(),requestStr.indexOf("</EXTCODE>",index));
		p_requestVO.setSenderExternalCode(extCode);
		requestHashMap.put("EXTCODE",extCode);
		index=requestStr.indexOf("<EXTREFNUM>");
		String extRefNumber=requestStr.substring(index+"<EXTREFNUM>".length(),requestStr.indexOf("</EXTREFNUM>",index));
		p_requestVO.setExternalReferenceNum(extRefNumber);
		requestHashMap.put("EXTREFNUM",extRefNumber);
		index=requestStr.indexOf("<MSISDN2>");
		String msisdn2=requestStr.substring(index+"<MSISDN2>".length(),requestStr.indexOf("</MSISDN2>",index));
		requestHashMap.put("MSISDN2",msisdn2);
		index=requestStr.indexOf("<AMOUNT>");
		String amount=requestStr.substring(index+"<AMOUNT>".length(),requestStr.indexOf("</AMOUNT>",index));
		requestHashMap.put("AMOUNT",amount);
		index=requestStr.indexOf("<LANGUAGE1>");
		String language1=requestStr.substring(index+"<LANGUAGE1>".length(),requestStr.indexOf("</LANGUAGE1>",index));
		requestHashMap.put("LANGUAGE1",language1);
		index=requestStr.indexOf("<LANGUAGE2>");
		String language2=requestStr.substring(index+"<LANGUAGE2>".length(),requestStr.indexOf("</LANGUAGE2>",index));
		requestHashMap.put("LANGUAGE2",language2);
		index=requestStr.indexOf("<SELECTOR>");
		String selector=requestStr.substring(index+"<SELECTOR>".length(),requestStr.indexOf("</SELECTOR>",index));
		requestHashMap.put("SELECTOR",selector);
		index=requestStr.indexOf("<BONUS>");
		bonus=requestStr.substring(index+"<BONUS>".length(),requestStr.indexOf("</BONUS>",index));
		requestHashMap.put("BONUS",bonus);
		index=requestStr.indexOf("<EXTERNALDATA1>");
	    if(index!=out_of_bound) {
				info1=requestStr.substring(index+"<EXTERNALDATA1>".length(),requestStr.indexOf("</EXTERNALDATA1>",index));
		}
	    index=requestStr.indexOf("<EXTERNALDATA2>");
	    if(index!=out_of_bound) {
			info2=requestStr.substring(index+"<EXTERNALDATA2>".length(),requestStr.indexOf("</EXTERNALDATA2>",index));
		}
	    index=requestStr.indexOf("<EXTERNALDATA3>");
	    if(index!=out_of_bound) {
			info3=requestStr.substring(index+"<EXTERNALDATA3>".length(),requestStr.indexOf("</EXTERNALDATA3>",index));
		}
	    index=requestStr.indexOf("<EXTERNALDATA4>");
	    if(index!=out_of_bound) {
			info4=requestStr.substring(index+"<EXTERNALDATA4>".length(),requestStr.indexOf("</EXTERNALDATA4>",index));
		}
	    index=requestStr.indexOf("<EXTERNALDATA5>");
	    if(index!=out_of_bound) {
			info5=requestStr.substring(index+"<EXTERNALDATA5>".length(),requestStr.indexOf("</EXTERNALDATA5>",index));
		}
	    index=requestStr.indexOf("<EXTERNALDATA6>");
	    if(index!=out_of_bound) {
			info6=requestStr.substring(index+"<EXTERNALDATA6>".length(),requestStr.indexOf("</EXTERNALDATA6>",index));
		}
	    index=requestStr.indexOf("<EXTERNALDATA7>");
	    if(index!=out_of_bound) {
			info7=requestStr.substring(index+"<EXTERNALDATA7>".length(),requestStr.indexOf("</EXTERNALDATA7>",index));
		}
	    index=requestStr.indexOf("<EXTERNALDATA8>");
	    if(index!=out_of_bound) {
			info8=requestStr.substring(index+"<EXTERNALDATA8>".length(),requestStr.indexOf("</EXTERNALDATA8>",index));
		}
	    index=requestStr.indexOf("<EXTERNALDATA9>");
	    if(index!=out_of_bound) {
			info9=requestStr.substring(index+"<EXTERNALDATA9>".length(),requestStr.indexOf("</EXTERNALDATA9>",index));
		}
	    index=requestStr.indexOf("<EXTERNALDATA10>");
	    if(index!=out_of_bound) {
			info10=requestStr.substring(index+"<EXTERNALDATA10>".length(),requestStr.indexOf("</EXTERNALDATA10>",index));
		}
		index=requestStr.indexOf("<CELLID>");
		if(index>=0) {
			cellId=requestStr.substring(index+"<CELLID>".length(),requestStr.indexOf("</CELLID>",index));
		}
		index=requestStr.indexOf("<SWITCHID>");
		if(index>=0) {
			switchId=requestStr.substring(index+"<SWITCHID>".length(),requestStr.indexOf("</SWITCHID>",index));
		}
		requestHashMap.put("CELLID",cellId);
		requestHashMap.put("SWITCHID",switchId);
		p_requestVO.setRequestMap(requestHashMap);
		if (type != null && type.length()>0 && type.equals(PretupsI.PROMOVAS_EXTGW_TYPE))
		{
			//XMLStringValidation.validateChannelExtRecharge(p_requestVO,type,date,extnwcode,msisdn1,pin,loginId,password,extCode,extRefNumber,msisdn2,amount,language1,language2,selector,bonus);
			XMLStringValidation.validateChannelExtRecharge(p_requestVO,type,date,extnwcode,msisdn1,pin,loginId,password,extCode,extRefNumber,msisdn2,amount,language1,language2,selector,bonus,info1,info2,info3,info4,info5,info6,info7,info8,info9,info10,switchId,cellId);
		}
		if (BTSLUtil.isNullString(info4))
			{
					info4="PRE";
			}
		else if(!info4.equalsIgnoreCase("pre") && !info4.equalsIgnoreCase("post"))
			{
				info4="PRE";
			}
		if(BTSLUtil.isNullString(amount))
		{
			amount="0";
		}
		if(BTSLUtil.isNullString(pin))
		{
		    pin="0";	
		}
		if(BTSLUtil.isNullString(msisdn2))
		{
				_log.error(methodName,"msisdn2 field is null ");
				String[] strArr=new String[]{msisdn2,String.valueOf(minMsisdnLength),String.valueOf(maxMsisdnLength)};
				throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.CHNL_ERROR_RECR_MSISDN_NOTINRANGE,0,strArr,null);
		}
		 if(!(BTSLUtil.isNullString(info1)))
		    {
            	int length = info1.length();
            	if(length>100)
            	{
            		_log.debug(methodName, "length is "+length);
		    	 	 _log.error(methodName,"Characters are more than 100 ");
				    throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_MAX_100_EXTERNALDATA1);	
            	}
            }		
            if(!(BTSLUtil.isNullString(info2)))
		    {
            	int length = info2.length();
            	if(length>100)
            	{
            		_log.debug(methodName, "length is "+length);
		    	 	 _log.error(methodName,"Characters are more than 100 ");
				    throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_MAX_100_EXTERNALDATA2);	
            	}
            }		
            if(!(BTSLUtil.isNullString(info3)))
		    {
            	int length = info3.length();
            	if(length>100)
            	{
            		_log.debug(methodName, "length is "+length);
		    	 	 _log.error(methodName,"Characters are more than 100 ");
				    throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_MAX_100_EXTERNALDATA3);	
            	}
            }		
            if(!(BTSLUtil.isNullString(info4)))
		    {
            	int length = info4.length();
            	if(length>100)
            	{
            		_log.debug(methodName, "length is "+length);
		    	 	 _log.error(methodName,"Characters are more than 100 ");
				    throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_MAX_100_EXTERNALDATA4);	
            	}
            }		
            if(!(BTSLUtil.isNullString(info5)))
		    {
            	int length = info5.length();
            	if(length>100)
            	{
            		_log.debug(methodName, "length is "+length);
		    	 	 _log.error(methodName,"Characters are more than 100 ");
				    throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_MAX_100_EXTERNALDATA5);	
            	}
            }		
            if(!(BTSLUtil.isNullString(info6)))
		    {
            	int length = info6.length();
            	if(length>100)
            	{
            		_log.debug(methodName, "length is "+length);
		    	 	 _log.error(methodName,"Characters are more than 100 ");
				    throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_MAX_100_EXTERNALDATA6);	
            	}
            }		
            if(!(BTSLUtil.isNullString(info7)))
		    {
            	int length = info7.length();
            	if(length>100)
            	{
            		_log.debug(methodName, "length is "+length);
		    	 	 _log.error(methodName,"Characters are more than 100 ");
				    throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_MAX_100_EXTERNALDATA7);	
            	}
            }	
            if(!(BTSLUtil.isNullString(info8)))
		    {
            	int length = info8.length();
            	if(length>100)
            	{
            		_log.debug(methodName, "length is "+length);
		    	 	 _log.error(methodName,"Characters are more than 100 ");
				    throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_MAX_100_EXTERNALDATA8);	
            	}
            }		
            if(!(BTSLUtil.isNullString(info9)))
		    {
            	int length = info9.length();
            	if(length>100)
            	{
            		_log.debug(methodName, "length is "+length);
		    	 	 _log.error(methodName,"Characters are more than 100 ");
				    throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_MAX_100_EXTERNALDATA9);	
            	}
            }		
            if(!(BTSLUtil.isNullString(info10)))
		    {
            	int length = info10.length();
            	if(length>100)
            	{
            		_log.debug(methodName, "length is "+length);
		    	 	 _log.error(methodName,"Characters are more than 100 ");
				    throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_MAX_100_EXTERNALDATA10);	
            	}
            }
		if(BTSLUtil.isNullString(selector))
		{
			ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(PretupsI.SERVICE_TYPE_INTL_RECHARGE);
			if(serviceSelectorMappingVO!=null) {
				selector=serviceSelectorMappingVO.getSelectorCode();
			}
		}
		if(isUssdNewTagsMandatory) {
			try{
				if(BTSLUtil.isNullString(cellId))
				{
					throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
				}
				if(BTSLUtil.isNullString(switchId))
				{
					throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
				}
			}catch(BTSLBaseException btsle){
				throw btsle;
			}
		}
		parsedRequestStr=PretupsI.SERVICE_TYPE_INTL_RECHARGE+CHNL_MESSAGE_SEP+msisdn2+CHNL_MESSAGE_SEP+amount+CHNL_MESSAGE_SEP+selector+CHNL_MESSAGE_SEP+language2+CHNL_MESSAGE_SEP+pin;
		p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
		p_requestVO.setReqSelector(selector);
		p_requestVO.setDecryptedMessage(parsedRequestStr);
		p_requestVO.setRequestMSISDN(msisdn1);
		p_requestVO.setCellId(cellId);
		p_requestVO.setSwitchId(switchId);
		p_requestVO.setExternalNetworkCode(extnwcode);
		if(p_requestVO.getRequestGatewayType().equalsIgnoreCase(PretupsI.REQUEST_SOURCE_TYPE_EXTGW)) {
			p_requestVO.setExternalNetworkCode(extnwcode);
			p_requestVO.setSenderExternalCode(extCode);
			p_requestVO.setSenderLoginID(loginId);
			p_requestVO.setPassword(password);
			p_requestVO.setExternalReferenceNum(extRefNumber);
			p_requestVO.setType(type);
		}
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
		try {
			p_requestVO.setCommissionType(PretupsI.OTF_COMMISSION);
			if(Double.parseDouble(bonus)> 0){
				p_requestVO.setCommission(Long.toString(PretupsBL.getSystemAmount(Double.parseDouble(bonus))));
				p_requestVO.setCommissionApplicable(PretupsI.YES);
			}else{
				p_requestVO.setCommissionApplicable(PretupsI.NO);
				p_requestVO.setCommission(bonus);
			}
		}catch (Exception e) {
			 _log.error(methodName, "Exception " + e);
			 _log.errorTrace(methodName,e);
			bonus="0";
			p_requestVO.setCommissionApplicable(PretupsI.NO);
			p_requestVO.setCommission(bonus);
		}
	} catch(BTSLBaseException be) {
		_log.error(methodName," BTSL Exception while parsing Request Message :"+be.getMessage());
		throw be;
	} catch(Exception e) {
		_log.errorTrace(methodName,e);
		p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
		_log.error(methodName,"Exception e: "+e);
		throw new BTSLBaseException(methodName,methodName,PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
	} finally {
		if(_log.isDebugEnabled()) {
			_log.debug(methodName,"Exiting p_requestVO: "+p_requestVO.toString());
		}
	}
}
public static void generateIntlTransferResponse(RequestVO p_requestVO) throws Exception
{
	final String methodName = "generatePromoVASTransferResponse";
	if(_log.isDebugEnabled()) {
		_log.debug(methodName,"Entered p_requestVO: "+p_requestVO.toString());
	}
	String responseStr= null;
	StringBuffer sbf=null;
	java.util.Date date=new java.util.Date();
	try {
		String EXTERNAL_DATE_FORMAT = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_DATE_FORMAT);
		SimpleDateFormat sdf = new SimpleDateFormat ("dd-MM-yyyy HH:mm:ss");
		sdf.setLenient(false); // this is required else it will convert
		sbf=new StringBuffer(1024);
		sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
		sbf.append("<TYPE>EXTPROMOVASTRFRESP</TYPE>");
		if(p_requestVO.isSuccessTxn()) {
			sbf.append("<TXNSTATUS>"+PretupsI.TXN_STATUS_SUCCESS+"</TXNSTATUS>");
		} else {
			String message = p_requestVO.getMessageCode();
			if(message.indexOf("_") != -1)
			{
				message = message.substring(0,message.indexOf("_"));
			}
			sbf.append("<TXNSTATUS>"+message+"</TXNSTATUS>");
		}
		sbf.append("<DATE>"+BTSLDateUtil.getSystemLocaleDate(sdf.format(date), EXTERNAL_DATE_FORMAT)+"</DATE>");
		if(p_requestVO.getRequestMap() != null) {
			sbf.append("<EXTREFNUM>"+(String)p_requestVO.getRequestMap().get("EXTREFNUM")+"</EXTREFNUM>");
		} else {
			sbf.append("<EXTREFNUM></EXTREFNUM>");
		} 
		if(BTSLUtil.isNullString(p_requestVO.getTransactionID())) {
			sbf.append("<TXNID></TXNID>");
		} else {
			sbf.append("<TXNID>"+p_requestVO.getTransactionID()+"</TXNID>");
		}
		String[] mesgArg= p_requestVO.getMessageArguments();
		if(!p_requestVO.isSuccessTxn()) { 
				sbf.append("<AMOUNT></AMOUNT>");
		} else {
				sbf.append("<AMOUNT>"+mesgArg[2]+"</AMOUNT>");
		}
		if(p_requestVO.isSuccessTxn()) { 
				sbf.append("<TXNDATE>"+BTSLDateUtil.getSystemLocaleDate(sdf.format(p_requestVO.getRequestStartTime()), EXTERNAL_DATE_FORMAT)+"</TXNDATE>"); 
		} else {
				sbf.append("<TXNDATE></TXNDATE>"); 
		}	
		if(BTSLUtil.isNullString(p_requestVO.getServiceType())) { 
				sbf.append("<SERVICETYPE></SERVICETYPE>");
		} else { 
				sbf.append("<SERVICETYPE>"+p_requestVO.getServiceType()+"</SERVICETYPE>");
		}			
		if(!p_requestVO.isSuccessTxn()) {
			sbf.append("<MESSAGE>"+getMessage(p_requestVO.getLocale(),p_requestVO.getMessageCode(),p_requestVO.getMessageArguments())+"</MESSAGE>");
		} else {
			sbf.append("<MESSAGE>"+getMessage(p_requestVO.getLocale(),PretupsErrorCodesI.C2S_SENDER_SUCCESS,p_requestVO.getMessageArguments())+"</MESSAGE>");
		}
		sbf.append("</COMMAND>");
		responseStr = sbf.toString();
		p_requestVO.setSenderReturnMessage(responseStr);
	} catch(Exception e) {
		_log.errorTrace(methodName,e);
		p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
		EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ExtAPIXMLStringParser["+methodName+"]",PretupsErrorCodesI.XML_ERROR_EXCEPTION,"","",""+methodName+":"+e.getMessage());
	} finally {
		if(_log.isDebugEnabled()) {
			_log.debug(methodName,"Exiting responseStr: "+responseStr);
		}
	}
}
// added for channel user transfer
public static void parseChannelUserTrfRequest(RequestVO p_requestVO) throws BTSLBaseException{
	final String methodName="parseChannelUserTrfRequest";
	_log.debug(methodName,"Entered p_requestVO: "+p_requestVO.toString());

String parsedRequestStr=null;
try
{
	HashMap requestHashMap =new HashMap();
	p_requestVO.setRequestMap(requestHashMap);
	String requestStr=p_requestVO.getRequestMessage();
	String type=null;
	String extNetworkCode=null;
	String date=null;
	String empCode=null;
	String loginId=null;
	String passwd=null;
	String extCode=null;
	String extRefNo=null;
	String fromUserMsisdn=null;
	String newfromUserMsisdn=null;
	String fromUserOriginId=null;
	String fromUserExtCode=null;
	String toParentMsisdn=null;
	String newtoParentMsisdn=null;
	String toParentOriginId=null;
	String toParentExtCode=null;
	String toUserGeoCode=null;
	String toUserCatCode=null;
	String networkCode=null;
	String fromUserLoginId=null;
	String toParentLoginId=null;
	try {
	
		
		String[] tagToParse= {"<TYPE/>:<TYPE></TYPE>","<DATE/>:<DATE></DATE>","<EXTNWCODE/>:<EXTNWCODE></EXTNWCODE>","<EMPCODE/>:<EMPCODE></EMPCODE>","<LOGINID/>:<LOGINID></LOGINID>","<PASSWORD/>:<PASSWORD></PASSWORD>","<EXTCODE/>:<EXTCODE></EXTCODE>","<EXTREFNUM/>:<EXTREFNUM></EXTREFNUM>","<NETWORKCODE/>:<NETWORKCODE></NETWORKCODE>","<FROM_USER_MSISDN/>:<FROM_USER_MSISDN></FROM_USER_MSISDN>","<FROM_USER_LOGINID/>:<FROM_USER_LOGINID></FROM_USER_LOGINID>","<FROM_USER_EXTCODE/>:<FROM_USER_EXTCODE></FROM_USER_EXTCODE>","<TO_PARENT_MSISDN/>:<TO_PARENT_MSISDN></TO_PARENT_MSISDN>","<TO_PARENT_LOGINID/>:<TO_PARENT_LOGINID></TO_PARENT_LOGINID>","<TO_PARENT_EXTCODE/>:<TO_PARENT_EXTCODE></TO_PARENT_EXTCODE>","<TO_USER_GEOGRAPHICAL_CODE/>:<TO_USER_GEOGRAPHICAL_CODE></TO_USER_GEOGRAPHICAL_CODE>","<TO_USER_CATEGORY_CODE/>:<TO_USER_CATEGORY_CODE></TO_USER_CATEGORY_CODE>"};
		for(int i=0;i<tagToParse.length;i++)
		{
			String []kv=tagToParse[i].split(":");
			requestStr=requestStr.replace(kv[0], kv[1]);
		}
		
		
		_log.debug(methodName,"parseChannelUserTrfRequestrequeststr---------------------: "+requestStr);
		
	String[] validateTag= {"<TYPE>","</TYPE>","<DATE>","</DATE>","<EXTNWCODE>","</EXTNWCODE>","<EMPCODE>","</EMPCODE>","<LOGINID>","</LOGINID>","<PASSWORD>","</PASSWORD>","<EXTCODE>","</EXTCODE>","<EXTREFNUM>","</EXTREFNUM>","<NETWORKCODE>","</NETWORKCODE>","<FROM_USER_MSISDN>","</FROM_USER_MSISDN>","<FROM_USER_LOGINID>","</FROM_USER_LOGINID>","<FROM_USER_EXTCODE>","</FROM_USER_EXTCODE>","<TO_PARENT_MSISDN>","</TO_PARENT_MSISDN>","<TO_PARENT_LOGINID>","</TO_PARENT_LOGINID>","<TO_PARENT_EXTCODE>","</TO_PARENT_EXTCODE>","<TO_USER_GEOGRAPHICAL_CODE>","</TO_USER_GEOGRAPHICAL_CODE>","<TO_USER_CATEGORY_CODE>","</TO_USER_CATEGORY_CODE>"};
	XMLStringValidation.validateTags(requestStr,validateTag);
	
	int index=requestStr.indexOf("<TYPE>");
	 type=requestStr.substring(index+"<TYPE>".length(),requestStr.indexOf("</TYPE>",index));
	requestHashMap.put("TYPE",PretupsI.REQUEST_TYPE);
	index=requestStr.indexOf("<DATE>");
	if(index>0)
	{
		date=requestStr.substring(index+"<DATE>".length(),requestStr.indexOf("</DATE>",index));
		date = BTSLDateUtil.getGregorianDateInString(date);
		requestHashMap.put("DATE",date);
	}
	index=requestStr.indexOf("<EXTNWCODE>");
	extNetworkCode=requestStr.substring(index+"<EXTNWCODE>".length(),requestStr.indexOf("</EXTNWCODE>",index));
	requestHashMap.put("EXTNWCODE",extNetworkCode);
	
	index=requestStr.indexOf("<EMPCODE>");
	empCode=requestStr.substring(index+"<EMPCODE>".length(),requestStr.indexOf("</EMPCODE>",index));
	requestHashMap.put("EMPCODE",empCode);
	
	index=requestStr.indexOf("<LOGINID>");
	loginId=requestStr.substring(index+"<LOGINID>".length(),requestStr.indexOf("</LOGINID>",index));
	requestHashMap.put("LOGINID",loginId);
	
	index=requestStr.indexOf("<PASSWORD>");
	passwd=requestStr.substring(index+"<PASSWORD>".length(),requestStr.indexOf("</PASSWORD>",index));
	requestHashMap.put("PASSWORD",passwd);
	
	index=requestStr.indexOf("<EXTCODE>");
	extCode=requestStr.substring(index+"<EXTCODE>".length(),requestStr.indexOf("</EXTCODE>",index));
	requestHashMap.put("<EXTCODE>",extCode);
	
	index=requestStr.indexOf("<EXTREFNUM>");
	extRefNo=requestStr.substring(index+"<EXTREFNUM>".length(),requestStr.indexOf("</EXTREFNUM>",index));
	requestHashMap.put("EXTREFNUM",extRefNo);
	
	index=requestStr.indexOf("<NETWORKCODE>");
	networkCode=requestStr.substring(index+"<NETWORKCODE>".length(),requestStr.indexOf("</NETWORKCODE>",index));
	requestHashMap.put("NETWORKCODE",networkCode);
	
	index=requestStr.indexOf("<FROM_USER_MSISDN>");
	fromUserMsisdn=requestStr.substring(index+"<FROM_USER_MSISDN>".length(),requestStr.indexOf("</FROM_USER_MSISDN>",index));
	requestHashMap.put("FROM_USER_MSISDN",fromUserMsisdn);
	
	index=requestStr.indexOf("<FROM_USER_LOGINID>");
	fromUserLoginId=requestStr.substring(index+"<FROM_USER_LOGINID>".length(),requestStr.indexOf("</FROM_USER_LOGINID>",index));
	requestHashMap.put("FROM_USER_LOGINID",fromUserLoginId);
	
	/*if(SystemPreferences.ORIGIN_ID_ALLOW)  // added to check Origin ID allowed
	{
	index=requestStr.indexOf("<FROM_USER_ORIGIN_ID>");
	if(index == -1){
		throw new BTSLBaseException("EXTAPIXMLStringParser","parseChannelUserTrfRequest",PretupsErrorCodesI.ORIGINID_TAG_MISSING);
	}
	fromUserOriginId=requestStr.substring(index+"<FROM_USER_ORIGIN_ID>".length(),requestStr.indexOf("</FROM_USER_ORIGIN_ID>",index));
	requestHashMap.put("FROM_USER_ORIGIN_ID",fromUserOriginId);
	}*/
	index=requestStr.indexOf("<FROM_USER_EXTCODE>");
	fromUserExtCode=requestStr.substring(index+"<FROM_USER_EXTCODE>".length(),requestStr.indexOf("</FROM_USER_EXTCODE>",index));
	requestHashMap.put("FROM_USER_EXTCODE",fromUserExtCode);
	
	index=requestStr.indexOf("<TO_PARENT_MSISDN>");
	toParentMsisdn=requestStr.substring(index+"<TO_PARENT_MSISDN>".length(),requestStr.indexOf("</TO_PARENT_MSISDN>",index));
	requestHashMap.put("TO_PARENT_MSISDN",toParentMsisdn);
	
	index=requestStr.indexOf("<TO_PARENT_LOGINID>");
	toParentLoginId=requestStr.substring(index+"<TO_PARENT_LOGINID>".length(),requestStr.indexOf("</TO_PARENT_LOGINID>",index));
	requestHashMap.put("TO_PARENT_LOGINID",toParentLoginId);
	
	/*if(SystemPreferences.ORIGIN_ID_ALLOW)  // added to check Origin ID allowed
	{
	index=requestStr.indexOf("<TO_PARENT_ORIGIN_ID>");
	if(index == -1){
		throw new BTSLBaseException("EXTAPIXMLStringParser","parseChannelUserTrfRequest",PretupsErrorCodesI.PARENT_ORIGINID_TAG_MISSING);
	}
	toParentOriginId=requestStr.substring(index+"<TO_PARENT_ORIGIN_ID>".length(),requestStr.indexOf("</TO_PARENT_ORIGIN_ID>",index));
	requestHashMap.put("TO_PARENT_ORIGIN_ID",toParentOriginId);
	}*/
	index=requestStr.indexOf("<TO_PARENT_EXTCODE>");
	toParentExtCode=requestStr.substring(index+"<TO_PARENT_EXTCODE>".length(),requestStr.indexOf("</TO_PARENT_EXTCODE>",index));
	requestHashMap.put("TO_PARENT_EXTCODE",toParentExtCode);
	
	index=requestStr.indexOf("<TO_USER_GEOGRAPHICAL_CODE>");
	toUserGeoCode=requestStr.substring(index+"<TO_USER_GEOGRAPHICAL_CODE>".length(),requestStr.indexOf("</TO_USER_GEOGRAPHICAL_CODE>",index));
	requestHashMap.put("TO_USER_GEOGRAPHICAL_CODE",toUserGeoCode);
	
	index=requestStr.indexOf("<TO_USER_CATEGORY_CODE>");
	toUserCatCode=requestStr.substring(index+"<TO_USER_CATEGORY_CODE>".length(),requestStr.indexOf("</TO_USER_CATEGORY_CODE>",index));
	requestHashMap.put("TO_USER_CATEGORY_CODE",toUserCatCode);

	
	//if(BTSLUtil.isNullString(type)|| BTSLUtil.isNullString(toUserGeoCode)|| BTSLUtil.isNullString(toUserCatCode) || BTSLUtil.isNullString(networkCode))
	if(BTSLUtil.isNullString(type)|| BTSLUtil.isNullString(toUserCatCode) || BTSLUtil.isNullString(networkCode))
	{
		throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
	}
	
	if(BTSLUtil.isNullString(empCode))
	{
		if(BTSLUtil.isNullString(loginId) || BTSLUtil.isNullString(passwd))
			throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
	}
	
	if(BTSLUtil.isNullString(fromUserMsisdn) && BTSLUtil.isNullString(fromUserLoginId) ){
		throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.EITHER_FROMUSERMSISDN_OR_FROMLOGINID_REQUIRED);
	}
	
	if(BTSLUtil.isNullString(toParentMsisdn) && BTSLUtil.isNullString(toParentLoginId) ){
		throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.EITHER_TOUSERMSISDN_OR_TOLOGINID_REQUIRED);
	}	
	/*if(SystemPreferences.ORIGIN_ID_ALLOW)  // added to check Origin ID allowed
	{
		 //LoginDAO _loginDAO= null;
		 UserDAO _userdao=null;
		 Connection pcon=null;
		 try{
			 pcon = OracleUtil.getConnection();
			 _userdao= new UserDAO();

			 if(BTSLUtil.isNullString(fromUserOriginId)){
				 throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.INVALID_ORIGINID);
			 }
			 if(BTSLUtil.isNullString(fromUserExtCode)){
				 throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.INVALID_EXTCODE);
			 }

			 newfromUserMsisdn=_userdao.fetchMSISDNbyOriginId(pcon,fromUserOriginId,fromUserExtCode);

			 if(BTSLUtil.isNullString(newfromUserMsisdn)){
				 p_requestVO.setMessageCode(PretupsErrorCodesI.USER_INVALID_ORIGINID);
				 throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.USER_INVALID_ORIGINID);
			 }		    

			 if(!BTSLUtil.isNullString(fromUserMsisdn) && !fromUserMsisdn.equals(newfromUserMsisdn)){
				 p_requestVO.setMessageCode(PretupsErrorCodesI.USER_INVALID_MSISDN);
				 throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.USER_INVALID_MSISDN);
			 }   

			 fromUserMsisdn=newfromUserMsisdn;


			 if(BTSLUtil.isNullString(toParentOriginId)){
				 throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.INVALID_PARENT_ORIGINID);
			 }
			 if(BTSLUtil.isNullString(toParentExtCode)){
				 throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.INVALID_PARENT_EXTCODE);
			 }
			 newtoParentMsisdn=_userdao.fetchMSISDNbyOriginId(pcon,toParentOriginId,toParentExtCode);

			 if(BTSLUtil.isNullString(newtoParentMsisdn)){
				 p_requestVO.setMessageCode(PretupsErrorCodesI.USER_INVALID_MSISDN);
				 throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.USER_INVALID_ORIGINID);
			 }

			 if(!BTSLUtil.isNullString(toParentMsisdn) && !toParentMsisdn.equals(newtoParentMsisdn)){
				 p_requestVO.setMessageCode(PretupsErrorCodesI.USER_INVALID_MSISDN);
				 throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.USER_INVALID_MSISDN);
			 }
			 toParentMsisdn=newtoParentMsisdn;
		 }
		 catch (Exception e) {
			 p_requestVO.setMessageCode(PretupsErrorCodesI.USER_INVALID_ORIGINID);
			 try {
				 if (pcon != null)
					 pcon.rollback();
			 } catch (Exception e1) {
				 _log.errorTrace(methodName, e1);
			 }
			 if (_log.isDebugEnabled())
				 _log.error(methodName,"Exception " + e.getMessage());
			 p_requestVO.setMessageCode(PretupsErrorCodesI.USER_INVALID_ORIGINID);
			 throw e;
		 }
		 finally
		 {
			 if (_log.isDebugEnabled())
				 _log.debug(methodName," Exiting");
			 try {
				 if (pcon != null)
					 try {
						 pcon.close();
					 } catch (SQLException e1) {
						 _log.errorTrace(methodName,e1);
					 }
			 } catch (Exception e) {
				 _log.errorTrace(methodName,e);
			 }
		 }
	}*/
	}
	catch(BTSLBaseException be)
	{
		p_requestVO.setMessageCode(be.getMessageKey());
		p_requestVO.setMessageArguments(be.getArgs());
		throw be;
	}
	catch (RuntimeException e) {
		_log.errorTrace(methodName,e);					
		p_requestVO.setMessageCode(PretupsErrorCodesI.EXTSYS_REQ_INVALID_FORMAT);
	    _log.error(methodName,"Exception e: "+e);
	    throw new BTSLBaseException("XMLStringParser",methodName,PretupsErrorCodesI.EXTSYS_REQ_INVALID_FORMAT);
	}
		
	parsedRequestStr=PretupsI.SERVICE_TYPE_USERMOVEMENT+CHNL_MESSAGE_SEP+networkCode+CHNL_MESSAGE_SEP+loginId+CHNL_MESSAGE_SEP+passwd+CHNL_MESSAGE_SEP+empCode+CHNL_MESSAGE_SEP+fromUserMsisdn+CHNL_MESSAGE_SEP+fromUserOriginId+CHNL_MESSAGE_SEP+fromUserExtCode+CHNL_MESSAGE_SEP+toParentMsisdn+CHNL_MESSAGE_SEP+toParentOriginId+CHNL_MESSAGE_SEP+toParentExtCode+CHNL_MESSAGE_SEP+toUserGeoCode+CHNL_MESSAGE_SEP+toUserCatCode;
	
	p_requestVO.setRequestIDStr(p_requestVO.getRequestIDStr());	
	p_requestVO.setExternalNetworkCode(extNetworkCode);
	p_requestVO.setEmployeeCode(empCode);
	p_requestVO.setLogin(loginId);
	p_requestVO.setPassword(passwd);
	//p_requestVO.setFromUserMsisdn(fromUserMsisdn);
	//p_requestVO.setFromUserUniqId(fromUserUniqId);
	//p_requestVO.setFromUserExtCode(fromUserExtCode);
	//p_requestVO.setToParentMsisdn(toParentMsisdn);
	//p_requestVO.setToParentUniqId(toParentUniqId);
	//p_requestVO.setToParentExtCode(toParentExtCode);
	//p_requestVO.setToUserGeoCode(toUserGeoCode);
	//p_requestVO.setToUserCatCode(toUserCatCode);
	
	
	
	
	p_requestVO.setExternalReferenceNum(extRefNo);
	p_requestVO.setLocale(new Locale(defaultLanguage,defaultCountry));
	p_requestVO.setRequestMap(requestHashMap);
	p_requestVO.setDecryptedMessage(parsedRequestStr);
	p_requestVO.setNetworkCode(networkCode);
	//p_requestVO.setType("CHNLUSERTRF");
	XMLStringValidation.validateExtChannelUserTransferRequest(p_requestVO,date,extNetworkCode,empCode,loginId,passwd,extCode,extRefNo,networkCode,fromUserMsisdn,fromUserOriginId, fromUserExtCode,toParentMsisdn,toParentOriginId,toParentExtCode,toUserGeoCode,toUserCatCode,fromUserLoginId,toParentLoginId);

}
catch(BTSLBaseException be)
{
	_log.errorTrace(methodName,be);
	_log.error(methodName,"Exception be: "+be);
	throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,be.getMessageKey(),be.getArgs());
}
catch(Exception e)
{
	_log.errorTrace(methodName,e);
	p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
	_log.error(methodName,"Exception e: "+e);
	throw new BTSLBaseException("ExtAPIXMLStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
}
finally
{
	if(_log.isDebugEnabled()) {
		_log.debug(methodName,"Exiting p_requestVO: "+p_requestVO.toString());
	}
}

}

// added for channel user transfer

public static void generateUserTransferResponse(RequestVO p_requestVO) throws Exception
{
	final String methodName = "generateUserTransferResponse";
	if(_log.isDebugEnabled()) {
		_log.debug(methodName,"Entered p_requestVO: "+p_requestVO.toString());
	}
	String responseStr= null;
	StringBuffer sbf=null;
	java.util.Date date=new java.util.Date();
	try
	{
		sbf=new StringBuffer(1024);
		SimpleDateFormat sdf = new SimpleDateFormat ("dd/MM/yyyy HH:mm:ss a");
		HashMap hashMap = p_requestVO.getRequestMap();
		Boolean isAccrossDmain = (Boolean)p_requestVO.getRequestMap().get("ACROSS_DOMAIN");
		String errormsg = (String)p_requestVO.getRequestMap().get("ERROR_MSG");
		
		sdf.setLenient(false); 
	    sbf=new StringBuffer(1024);
		sbf.append("<?xml version=\"1.0\"?><COMMAND>");
		sbf.append("<TYPE>USERMOVEMENTRESP</TYPE>");
		if(p_requestVO.isSuccessTxn()) {
			sbf.append("<TXNSTATUS>"+PretupsI.TXN_STATUS_SUCCESS+"</TXNSTATUS>");
		} else {
			sbf.append("<TXNSTATUS>"+p_requestVO.getMessageCode()+"</TXNSTATUS>");
		}
		sbf.append("<DATE>"+BTSLDateUtil.getSystemLocaleDate(sdf.format(date), externalDateFormat)+"</DATE>");
		if(p_requestVO.isSuccessTxn()) {
		sbf.append("<EXTREFNUM>"+p_requestVO.getExternalReferenceNum()+"</EXTREFNUM>");
		if(!isAccrossDmain)
		sbf.append("<MESSAGE>"+getMessage(p_requestVO.getLocale(),p_requestVO.getMessageCode(),p_requestVO.getMessageArguments())+"</MESSAGE>");
		else
			sbf.append("<MESSAGE>"+errormsg+"</MESSAGE>");	
		sbf.append("<REMARKS>"+PretupsI.TXN_STATUS_SUCCESS_MESSAGE+"</REMARKS>");
		}else{
			sbf.append("<EXTREFNUM>"+""+"</EXTREFNUM>");
			sbf.append("<MESSAGE>"+getMessage(p_requestVO.getLocale(),p_requestVO.getMessageCode(),p_requestVO.getMessageArguments())+"</MESSAGE>");
			sbf.append("<REMARKS>"+PretupsI.TXN_STATUS_FAILURE_MESSAGE+"</REMARKS>");
		}
			
		sbf.append("</COMMAND>");
		responseStr = sbf.toString();
		p_requestVO.setSenderReturnMessage(responseStr);
	}
	catch(Exception e)
	{
		_log.errorTrace(methodName, e);
		_log.error(methodName,"Exception e: "+e);
		p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
		EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"XMLStringParser[generateUserTransferResponse]",PretupsErrorCodesI.XML_ERROR_EXCEPTION,"","","generateUserTransferResponse:"+e.getMessage());
	}
	finally
	{
		if(_log.isDebugEnabled()) {
			_log.debug(methodName,"Exiting responseStr: "+responseStr);
		}
	}
}


// Data CP2P changes started
	
	/** this method parse credit transfer request from XML String formating into white space seperated String
	 *@param p_requestVO RequestVO
	  
	 */
	public static void 	parseChannelCP2PDataTransferRequest(RequestVO p_requestVO) throws BTSLBaseException,Exception{
		final String methodName = "generateChannelCP2PDataTransferResponse";
		if(_log.isDebugEnabled()){
			_log.debug(methodName,"Entered p_requestVO: "+p_requestVO.toString());
		}
		String parsedRequestStr=null;
		//boolean tagsMandatory;
		String cellId=null;
		String switchId=null;
		try
		{
		    String requestStr=p_requestVO.getRequestMessage();
		    /*int index=requestStr.indexOf("<TYPE>");
		    String type=requestStr.substring(index+"<TYPE>".length(),requestStr.indexOf("</TYPE>",index));*/
		    int index=requestStr.indexOf("<MSISDN1>");
		    String msisdn1=requestStr.substring(index+"<MSISDN1>".length(),requestStr.indexOf("</MSISDN1>",index));
		    index=requestStr.indexOf("<PIN>");
		    String pin=requestStr.substring(index+"<PIN>".length(),requestStr.indexOf("</PIN>",index));
		    index=requestStr.indexOf("<MSISDN2>");
		    String msisdn2=requestStr.substring(index+"<MSISDN2>".length(),requestStr.indexOf("</MSISDN2>",index));
		    index=requestStr.indexOf("<AMOUNT>");
		    String amount=requestStr.substring(index+"<AMOUNT>".length(),requestStr.indexOf("</AMOUNT>",index));
		    index=requestStr.indexOf("<LANGUAGE1>");
		    String language1=requestStr.substring(index+"<LANGUAGE1>".length(),requestStr.indexOf("</LANGUAGE1>",index));
		    index=requestStr.indexOf("<LANGUAGE2>");
		    String language2=requestStr.substring(index+"<LANGUAGE2>".length(),requestStr.indexOf("</LANGUAGE2>",index));
		    index=requestStr.indexOf("<SELECTOR>");
		    String selector=requestStr.substring(index+"<SELECTOR>".length(),requestStr.indexOf("</SELECTOR>",index));
           //added for cell_id and switch_id
		    index=requestStr.indexOf("<CELLID>");
		    if(index>=0)
		    cellId=requestStr.substring(index+"<CELLID>".length(),requestStr.indexOf("</CELLID>",index));
		    index=requestStr.indexOf("<SWITCHID>");
		    if(index>=0)
		    switchId=requestStr.substring(index+"<SWITCHID>".length(),requestStr.indexOf("</SWITCHID>",index));
		    //amount field should be mandatory
		    if(BTSLUtil.isNullString(amount))
		    {
		    	 	p_requestVO.setMessageCode(PretupsErrorCodesI.CP2P_DATA_ERROR_BLANK_AMOUNT);
				    _log.error(methodName,"Amount field is null");
				    throw new BTSLBaseException("XMLStringParser",methodName,PretupsErrorCodesI.CP2P_DATA_ERROR_BLANK_AMOUNT);	
		    }
		   //added by sanjay to set default pin
			if(BTSLUtil.isNullString(pin))
				//pin=PretupsI.DEFAULT_P2P_PIN;
				pin= p2pDefaultSmsPin;
			
			//added by sanjay 10/01/2006 - to set default language code
			if(_log.isDebugEnabled()){
				_log.debug(methodName,"language1:"+language1+":");
			}
			
			if(BTSLUtil.isNullString(language1))
				language1=Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
			
			if(BTSLUtil.isNullString(language1))
				language1="0";
			
			if(_log.isDebugEnabled()){
				_log.debug(methodName,"language1:"+language1+":");
			}
			//added by sanjay 10/01/2006 - to set default language code
			
			if(_log.isDebugEnabled()){
				_log.debug(methodName,"language2:"+language2+":");
			}
			
			if(BTSLUtil.isNullString(language2))
				language2=Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
			
			if(BTSLUtil.isNullString(language2))
				language2="0";
			if(_log.isDebugEnabled()){
				_log.debug(methodName,"language2:"+language2+":");
			}
			
		    if(BTSLUtil.isNullString(selector))
			{
				//selector=""+SystemPreferences.P2P_TRANSFER_DEF_SELECTOR_CODE;
				//Changed on 27/05/07 for Service Type selector Mapping
				ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(PretupsI.SERVICE_TYPE_DATA_CP2P);
				if(serviceSelectorMappingVO!=null)
					selector=serviceSelectorMappingVO.getSelectorCode();
			}
			//if we have to forcibaly change the value of selecter in case of USSD request.
			
			
			//tagsMandatory=isUssdNewTagsMandatory;
			if(isUssdNewTagsMandatory)
        	{
        		try{
        			if(BTSLUtil.isNullString(cellId))
        			{
        				throw new BTSLBaseException("XMLStringParser",methodName,PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
        			}
        			if(BTSLUtil.isNullString(switchId))
        			{
        				throw new BTSLBaseException("XMLStringParser",methodName,PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
        			}
        		}catch(BTSLBaseException btsle){
        			throw btsle;
        		}
        	}
		    parsedRequestStr=PretupsI.SERVICE_TYPE_DATA_CP2P+P2P_MESSAGE_SEP+msisdn2+P2P_MESSAGE_SEP+amount+(P2P_MESSAGE_SEP+selector)+(P2P_MESSAGE_SEP+language2)+P2P_MESSAGE_SEP+pin;
			p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
			p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(language2));
			p_requestVO.setReqSelector(selector);
			p_requestVO.setDecryptedMessage(parsedRequestStr);
		    p_requestVO.setRequestMSISDN(msisdn1);
		    p_requestVO.setCellId(cellId);
		    p_requestVO.setSwitchId(switchId);
		}
		catch(BTSLBaseException be)
		{
			_log.error(methodName," BTSL Exception while parsing Request Message :"+be.getMessage());
			throw be;
		}
		catch(Exception e)
		{
		    p_requestVO.setMessageCode(PretupsErrorCodesI.CP2P_DATA_ERROR_INVALIDMESSAGEFORMAT);
		    _log.error(methodName,"Exception e: "+e);
		    throw new BTSLBaseException("XMLStringParser",methodName,PretupsErrorCodesI.CP2P_DATA_ERROR_INVALIDMESSAGEFORMAT);
		}
		finally
		{
			if(_log.isDebugEnabled()){
				_log.debug(methodName,"Exiting p_requestVO: "+p_requestVO.toString());
			}
		}
	}
	/** this method construct credit transfer response in XML format from requestVO
	 *@param p_requestVO RequestVO 
	 *@return responseStr java.lang.String
	 */
	public static void 	generateChannelCP2PDataTransferResponse(RequestVO pRequestVO) throws BTSLBaseException
    {
		final String methodName = "generateChannelCP2PDataTransferResponse";
	    if(_log.isDebugEnabled()){
	    	_log.debug(methodName,"Entered p_requestVO: "+pRequestVO.toString());
	    }
	    String responseStr= null;
		StringBuilder sbf=null;
		try
		{
		    sbf=new StringBuilder(1024);
		    sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
			sbf.append("<TYPE>CCDATATRFRESP</TYPE>");
			if(BTSLUtil.isNullString(pRequestVO.getTransactionID())){
			    sbf.append("<TXNID></TXNID>");
			}
			else{
			    sbf.append("<TXNID>"+pRequestVO.getTransactionID()+"</TXNID>");
			}
		    if(pRequestVO.isSuccessTxn()){
		        sbf.append("<TXNSTATUS>"+PretupsI.TXN_STATUS_SUCCESS+"</TXNSTATUS>");
		    }
			else{
			    sbf.append("<TXNSTATUS>"+pRequestVO.getMessageCode()+"</TXNSTATUS>");
			}
			String responseMessage = null;
			responseMessage = getMessage(pRequestVO.getLocale(),pRequestVO.getMessageCode()+"_"+PretupsI.SERVICE_TYPE_DATA_CP2P,pRequestVO.getMessageArguments());
			if(responseMessage==null)
				responseMessage = getMessage(pRequestVO.getLocale(),pRequestVO.getMessageCode(),pRequestVO.getMessageArguments());
			if(BTSLUtil.isNullString(responseMessage))
				sbf.append("<MESSAGE>"+pRequestVO.getMessageCode()+"</MESSAGE>");
			else
				sbf.append("<MESSAGE>"+responseMessage+"</MESSAGE>");
		    sbf.append("</COMMAND>");
		    responseStr = sbf.toString();
		    pRequestVO.setSenderReturnMessage(responseStr);
		}
		catch(Exception e)
		{
			_log.error(methodName,"Exception e: "+e);
			pRequestVO.setMessageCode(PretupsErrorCodesI.CP2P_DATA_ERROR_EXCEPTION);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"XMLStringParser[generateChannelCP2PDataTransferResponse]",PretupsErrorCodesI.CP2P_DATA_ERROR_EXCEPTION,"","","generateChannelCP2PDataTransferResponse:"+e.getMessage());
			 throw new BTSLBaseException("XMLStringParser", methodName,PretupsErrorCodesI.CP2P_DATA_ERROR_INVALIDMESSAGEFORMAT);
				
		}
		finally
		{
			if(_log.isDebugEnabled()){
				_log.debug(methodName,"Exiting responseStr: "+responseStr);
			}
		}
    }
	
	// Data CP2P changes ended
	

	
	// Data CP2P changes ended
	
	
	/**
	 * Method to parse External Interface DATA CP2P Request
	 * @param p_requestVO
	 * @throws Exception
	 */
	public static void parseChannelExtCP2PDataTransferRequest(RequestVO pRequestVO) throws BTSLBaseException{
		final String methodName = "parseChannelExtCP2PDataTransferRequest";
		if(_log.isDebugEnabled()){
			_log.debug(methodName,"Entered p_requestVO: "+pRequestVO.toString());
		}
		String parsedRequestStr=null;
		String cellId=null;
		String switchId=null;
		try
		{
		    String requestStr=pRequestVO.getRequestMessage();
		    
		
		    int index=requestStr.indexOf("<MSISDN1>");
		    String msisdn1=requestStr.substring(index+"<MSISDN1>".length(),requestStr.indexOf("</MSISDN1>",index));
		    index=requestStr.indexOf("<PIN>");
		    String pin=requestStr.substring(index+"<PIN>".length(),requestStr.indexOf("</PIN>",index));
		    index=requestStr.indexOf("<MSISDN2>");
		    String msisdn2=requestStr.substring(index+"<MSISDN2>".length(),requestStr.indexOf("</MSISDN2>",index));
		    index=requestStr.indexOf("<AMOUNT>");
		    String amount=requestStr.substring(index+"<AMOUNT>".length(),requestStr.indexOf("</AMOUNT>",index));
		    index=requestStr.indexOf("<LANGUAGE1>");
		    String language1=requestStr.substring(index+"<LANGUAGE1>".length(),requestStr.indexOf("</LANGUAGE1>",index));
		    index=requestStr.indexOf("<LANGUAGE2>");
		    String language2=requestStr.substring(index+"<LANGUAGE2>".length(),requestStr.indexOf("</LANGUAGE2>",index));
		    index=requestStr.indexOf("<SELECTOR>");
		    String selector=requestStr.substring(index+"<SELECTOR>".length(),requestStr.indexOf("</SELECTOR>",index));
		    index=requestStr.indexOf("<CELLID>");
		    
		    if(index>=0)
		    	cellId=requestStr.substring(index+"<CELLID>".length(),requestStr.indexOf("</CELLID>",index));
		    
		    index=requestStr.indexOf("<SWITCHID>");
		    
		    if(index>=0)
		    	switchId=requestStr.substring(index+"<SWITCHID>".length(),requestStr.indexOf("</SWITCHID>",index));
		    
		    if(BTSLUtil.isNullString(amount))
		    {
		    	pRequestVO.setMessageCode(PretupsErrorCodesI.CP2P_DATA_ERROR_BLANK_AMOUNT);
				    _log.error(methodName,"Amount field is null");
				    throw new BTSLBaseException("XMLStringParser",methodName,PretupsErrorCodesI.CP2P_DATA_ERROR_BLANK_AMOUNT);	
		    }
		    
		   //added by sanjay to set default pin
			if(BTSLUtil.isNullString(pin))
				
				pin= p2pDefaultSmsPin;
			
			//added by sanjay 10/01/2006 - to set default language code
			if(_log.isDebugEnabled()){
				_log.debug(methodName,"language1:"+language1+":");
			}
			
			if(BTSLUtil.isNullString(language1))
				language1=Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
			
			if(BTSLUtil.isNullString(language1))
				language1="0";
			
			if(_log.isDebugEnabled()){
				_log.debug(methodName,"language1:"+language1+":");
			}
			//added by sanjay 10/01/2006 - to set default language code
			
			if(_log.isDebugEnabled()){
				_log.debug(methodName,"language2:"+language2+":");
			}
			
			if(BTSLUtil.isNullString(language2))
				language2=Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
			
			if(BTSLUtil.isNullString(language2))
				language2="0";
			if(_log.isDebugEnabled()){
				_log.debug(methodName,"language2:"+language2+":");
			}
			
		    if(BTSLUtil.isNullString(selector))
			{
				
				//Changed on 27/05/07 for Service Type selector Mapping
				ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(PretupsI.SERVICE_TYPE_DATA_CP2P);
				if(serviceSelectorMappingVO!=null)
					selector=serviceSelectorMappingVO.getSelectorCode();
			}
			//if we have to forcibaly change the value of selecter in case of USSD request.
			
			if(isUssdNewTagsMandatory)
        	{
        		
        			if(BTSLUtil.isNullString(cellId))
        			{
        				throw new BTSLBaseException("XMLStringParser",methodName,PretupsErrorCodesI.USSD_CELLID_BLANK__ERROR);
        			}
        			if(BTSLUtil.isNullString(switchId))
        			{
        				throw new BTSLBaseException("XMLStringParser",methodName,PretupsErrorCodesI.USSD_SWITCHID_BLANK__ERROR);
        			}
        	}
		    parsedRequestStr=PretupsI.SERVICE_TYPE_DATA_CP2P+P2P_MESSAGE_SEP+msisdn2+P2P_MESSAGE_SEP+amount+(P2P_MESSAGE_SEP+selector)+(P2P_MESSAGE_SEP+language2)+P2P_MESSAGE_SEP+pin;
		    pRequestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
		    pRequestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(language2));
		    pRequestVO.setReqSelector(selector);
		    pRequestVO.setDecryptedMessage(parsedRequestStr);
		    pRequestVO.setRequestMSISDN(msisdn1);
		    pRequestVO.setCellId(cellId);
		    pRequestVO.setSwitchId(switchId);
		}
		catch(BTSLBaseException be)
		{
			_log.error(methodName," BTSL Exception while parsing Request Message :"+be.getMessage());
			throw be;
		}
		catch(Exception e)
		{
			pRequestVO.setMessageCode(PretupsErrorCodesI.CP2P_DATA_ERROR_INVALIDMESSAGEFORMAT);
		    _log.error(methodName,"Exception e: "+e);
		    throw new BTSLBaseException("XMLStringParser", methodName,PretupsErrorCodesI.CP2P_DATA_ERROR_INVALIDMESSAGEFORMAT);
		}
		finally
		{
			if(_log.isDebugEnabled()){
				_log.debug(methodName,"Exiting p_requestVO: "+pRequestVO.toString());
			}
		}
	}
	
	/** this method construct credit transfer response in XML format from requestVO
	 *@param p_requestVO RequestVO 
	 *@return responseStr java.lang.String
	 */
	public static void 	generateChannelExtCP2PDataTransferResponse(RequestVO pRequestVO) throws BTSLBaseException
    {
		final String methodName = "generateChannelExtCP2PDataTransferResponse";
	    if(_log.isDebugEnabled()){
	    	_log.debug(methodName,"Entered p_requestVO: "+pRequestVO.toString());
	    }
	    String responseStr= null;
		StringBuilder sbf=null;
		java.util.Date date=new java.util.Date();
		try
		{
			String EXTERNAL_DATE_FORMAT = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_DATE_FORMAT);
			SimpleDateFormat sdf = new SimpleDateFormat ("DD-MM-YYYY HH:mm:ss");
			sdf.setLenient(false); // this is required else it will convert
		    sbf=new StringBuilder(1024);
		    sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
			sbf.append("<TYPE>CCDATATRFRESP</TYPE>");
  
		    if(pRequestVO.isSuccessTxn()){
		        sbf.append("<TXNSTATUS>"+PretupsI.TXN_STATUS_SUCCESS+"</TXNSTATUS>");
		    }
			else{
			    sbf.append("<TXNSTATUS>"+pRequestVO.getMessageCode()+"</TXNSTATUS>");
			}
		    sbf.append("<DATE>"+BTSLDateUtil.getSystemLocaleDate(sdf.format(date), EXTERNAL_DATE_FORMAT)+"</DATE>");
		    if(pRequestVO.getExternalReferenceNum()==null){
				sbf.append("<EXTREFNUM></EXTREFNUM>");
			}
			else{
				sbf.append("<EXTREFNUM>"+pRequestVO.getExternalReferenceNum()+"</EXTREFNUM>");
			}
			if(BTSLUtil.isNullString(pRequestVO.getTransactionID())){
			    sbf.append("<TXNID></TXNID>");
			}
			else{
			    sbf.append("<TXNID>"+pRequestVO.getTransactionID()+"</TXNID>");
			}
					String responseMessage = null;
			responseMessage = getMessage(pRequestVO.getLocale(),pRequestVO.getMessageCode()+"_"+PretupsI.SERVICE_TYPE_DATA_CP2P,pRequestVO.getMessageArguments());
			if(responseMessage==null)
				responseMessage = getMessage(pRequestVO.getLocale(),pRequestVO.getMessageCode(),pRequestVO.getMessageArguments());
			if(BTSLUtil.isNullString(responseMessage))
				sbf.append("<MESSAGE>"+pRequestVO.getMessageCode()+"</MESSAGE>");
			else
				sbf.append("<MESSAGE>"+responseMessage+"</MESSAGE>");
	

		    sbf.append("</COMMAND>");
		    responseStr = sbf.toString();
		    pRequestVO.setSenderReturnMessage(responseStr);
		}
		catch(Exception e)
		{
			_log.error(methodName,"Exception e: "+e);
			pRequestVO.setMessageCode(PretupsErrorCodesI.CP2P_DATA_ERROR_EXCEPTION);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"XMLStringParser[generateChannelCP2PDataTransferResponse]",PretupsErrorCodesI.CP2P_DATA_ERROR_EXCEPTION,"","","generateChannelCP2PDataTransferResponse:"+e.getMessage());
			throw new BTSLBaseException("XMLStringParser",methodName,PretupsErrorCodesI.CP2P_DATA_ERROR_EXCEPTION);
		}
		finally
		{
			if(_log.isDebugEnabled()){
				_log.debug(methodName,"Exiting responseStr: "+responseStr);
			}
		}
    }
	
	//Added for C2C_O2C_TxnStatus Enquiry
			public static void parseExtChannelTxnStatusRequest(RequestVO pRequestVO) throws BTSLBaseException
			{
				String methodName="parseExtChannelTxnStatusRequest";
				if(_log.isDebugEnabled())
					_log.debug(methodName,"Entered p_requestVO: "+pRequestVO.toString());
				try
				{
					String parsedRequestStr=null;
					final String msgIdentificationBoth = "BOTH";
			        final String msgIdentificationTxnId = "TXN";
			        final String msgIdentificationExtRef = "EXT";
					HashMap requestMap=new HashMap();
					String requestStr=pRequestVO.getRequestMessage();
					int index=requestStr.indexOf("<TYPE>");
					String type=requestStr.substring(index+"<TYPE>".length(),requestStr.indexOf("</TYPE>",index));
					requestMap.put("TYPE", type.trim());
					index=requestStr.indexOf("<DATE>");
					String date=requestStr.substring(index+"<DATE>".length(),requestStr.indexOf("</DATE>",index));
					date = BTSLDateUtil.getGregorianDateInString(date);
					requestMap.put("DATE", date);
					index=requestStr.indexOf("<EXTNWCODE>");
					String extNwCode=requestStr.substring(index+"<EXTNWCODE>".length(),requestStr.indexOf("</EXTNWCODE>",index));
					requestMap.put("EXTNWCODE", extNwCode.trim());
					index=requestStr.indexOf("<MSISDN>");
					String msisdn=requestStr.substring(index+"<MSISDN>".length(),requestStr.indexOf("</MSISDN>",index));
					requestMap.put("MSISDN", msisdn.trim());
					index=requestStr.indexOf("<PIN>");
					String pin=requestStr.substring(index+"<PIN>".length(),requestStr.indexOf("</PIN>",index));
					requestMap.put("PIN", pin.trim());
					index=requestStr.indexOf("<LOGINID>");
					String loginId=requestStr.substring(index+"<LOGINID>".length(),requestStr.indexOf("</LOGINID>",index));
					requestMap.put("LOGINID", loginId.trim());
					index=requestStr.indexOf("<PASSWORD>");
					String password=requestStr.substring(index+"<PASSWORD>".length(),requestStr.indexOf("</PASSWORD>",index));
					requestMap.put("PASSWORD", password.trim());
					index=requestStr.indexOf("<EXTCODE>");
					String extCode=requestStr.substring(index+"<EXTCODE>".length(),requestStr.indexOf("</EXTCODE>",index));
					requestMap.put("EXTCODE", extCode.trim());
					index=requestStr.indexOf("<EXTREFNUM>");
					String extRefNum=requestStr.substring(index+"<EXTREFNUM>".length(),requestStr.indexOf("</EXTREFNUM>",index));
					requestMap.put("EXTREFNUM", extRefNum.trim());
					index=requestStr.indexOf("<TXNID>");
					String txnId=requestStr.substring(index+"<TXNID>".length(),requestStr.indexOf("</TXNID>",index));
					requestMap.put("TXNID", txnId.trim());
					index=requestStr.indexOf("<LANGUAGE1>");
					String language1=requestStr.substring(index+"<LANGUAGE1>".length(),requestStr.indexOf("</LANGUAGE1>",index));
					requestMap.put("LANGUAGE1", language1.trim());
					XMLStringValidation.validateChannelExtRechargeStatusRequest(pRequestVO, type, date, extNwCode, msisdn, pin, loginId, password, extCode, extRefNum, txnId,
			                language1);
					parsedRequestStr = PretupsI.SERVICE_TYPE_CHNLTXNSTATUS + CHNL_MESSAGE_SEP + txnId + CHNL_MESSAGE_SEP + pin;
		            if (!BTSLUtil.isNullString(extRefNum) && !BTSLUtil.isNullString(txnId))
		            {
		                parsedRequestStr = PretupsI.SERVICE_TYPE_CHNLTXNSTATUS + CHNL_MESSAGE_SEP + txnId + CHNL_MESSAGE_SEP + extRefNum + CHNL_MESSAGE_SEP + msgIdentificationBoth + CHNL_MESSAGE_SEP + pin;
		            }
		            else if (!BTSLUtil.isNullString(extRefNum))
		            {
		                parsedRequestStr = PretupsI.SERVICE_TYPE_CHNLTXNSTATUS + CHNL_MESSAGE_SEP + extRefNum + CHNL_MESSAGE_SEP + msgIdentificationExtRef + CHNL_MESSAGE_SEP + pin;
		            }
		            else if (!BTSLUtil.isNullString(txnId)) {
		                parsedRequestStr = PretupsI.SERVICE_TYPE_CHNLTXNSTATUS + CHNL_MESSAGE_SEP + txnId + CHNL_MESSAGE_SEP + msgIdentificationTxnId + CHNL_MESSAGE_SEP + pin;
		            }
		            else 
		            {
		            	  throw new BTSLBaseException(PretupsErrorCodesI.CHNL_ERROR_LRCH_INVALIDMESSAGEFORMAT,new String[] { pRequestVO.getActualMessageFormat() });
		            }
					 pRequestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
		             pRequestVO.setDecryptedMessage(parsedRequestStr);
		             pRequestVO.setExternalNetworkCode(extNwCode);
		             pRequestVO.setRequestMap(requestMap);
			         pRequestVO.setExternalReferenceNum(extRefNum);
			         pRequestVO.setFilteredMSISDN(msisdn);
					}
					catch (BTSLBaseException be)
					{
			            pRequestVO.setMessageCode(be.getMessageKey());
			            pRequestVO.setMessageArguments(be.getArgs());
			            throw be;
					}
					catch (Exception e)
					{
			            _log.errorTrace(methodName, e);
			            pRequestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
			            _log.error(methodName, "Exception e: " + e);
			            throw new BTSLBaseException("ExtAPIXMLStringParser", methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
					} finally
					{
			            if (_log.isDebugEnabled()) {
			                _log.debug(methodName, "Exiting p_requestVO: " + pRequestVO.toString());
		            }
		        }
			}
			
			//Added for O2C C2C Txn Status Enquiry Response 
			public static void generateExtChannelTxnStatusResponse(RequestVO pRequestVO)
			{
				
				String methodName="generateExtChannelTxnStatusResponse";
				if(_log.isDebugEnabled())
					_log.debug(methodName,"Entered p_requestVO: "+pRequestVO.toString());
				StringBuilder sbf=null;
				String responseStr=null;
				try
				{
					
					 String reqStatus = "";
			         String transactionId = "";
			         Date txnDate = null;
			         String trfType ="";
			         String prodCode = "";
			         String txnStatus = "";
			         Long txnAmt = null;
			         String recMsisdn = "";
			         String trfSubtype = "";
			         String wallet = "";
			         if (pRequestVO.isSuccessTxn() && pRequestVO.getValueObject() != null)
			         {
			            reqStatus = ((ChannelTransferVO) pRequestVO.getValueObject()).getStatus();
			            transactionId = ((ChannelTransferVO) pRequestVO.getValueObject()).getTransferID();
			            txnDate = (((ChannelTransferVO) pRequestVO.getValueObject()).getCloseDate());
			            trfType = ((ChannelTransferVO) pRequestVO.getValueObject()).getType();
			            trfSubtype = ((ChannelTransferVO) pRequestVO.getValueObject()).getTransferSubType();
			            txnStatus = ((ChannelTransferVO) pRequestVO.getValueObject()).getStatus();
			            prodCode = ((ChannelTransferVO) pRequestVO.getValueObject()).getProductCode();
			            txnAmt = ((ChannelTransferVO) pRequestVO.getValueObject()).getTransferMRP();
			            recMsisdn = ((ChannelTransferVO) pRequestVO.getValueObject()).getToUserMsisdn();
			            wallet = ((ChannelTransferVO) pRequestVO.getValueObject()).getWalletType();
			         }
			         sbf=new StringBuilder();
					 sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
					 sbf.append("<TYPE>EXCHNLSTATRESP</TYPE>");
					 if(pRequestVO.isSuccessTxn())
							sbf.append("<REQSTATUS>"+PretupsI.TXN_STATUS_SUCCESS+"</REQSTATUS>");
						else
							sbf.append("<REQSTATUS>"+pRequestVO.getMessageCode()+"</REQSTATUS>");
						sbf.append("<DATE>"+BTSLDateUtil.getLocaleTimeStamp(getDateTime(null, null))+"</DATE>");
						sbf.append("<EXTREFNUM>"+(BTSLUtil.isNullString(pRequestVO.getExternalReferenceNum())?"":pRequestVO.getExternalReferenceNum())+"</EXTREFNUM>");
						if (pRequestVO.getMessageCode() == null) {
			                sbf.append("<MESSAGE></MESSAGE>");
			            } else {
			                sbf.append("<MESSAGE>").append(getMessage(pRequestVO.getLocale(), pRequestVO.getMessageCode(), pRequestVO.getMessageArguments())).append("</MESSAGE>");
			            }
						if (BTSLUtil.isNullString(transactionId)) {
			                sbf.append("<TXNID></TXNID>");
			            } else {
			                sbf.append("<TXNID>").append(transactionId).append("</TXNID>");
			            }
						if(txnDate==null)
							sbf.append("<TXNDATETIME></TXNDATETIME>");
						else
							sbf.append("<TXNDATETIME>"+BTSLDateUtil.getLocaleDateTimeFromDate(txnDate)+"</TXNDATETIME>");
						sbf.append("<TRFTYPE>"+(BTSLUtil.isNullString(trfType)?"":trfType)+"</TRFTYPE>");
						sbf.append("<PRODUCTDETAILS>");
						sbf.append("<RECORD>");
						sbf.append("<PRODUCTCODE>"+(BTSLUtil.isNullString(prodCode)?"":prodCode)+"</PRODUCTCODE>");
						sbf.append("<TXNSTATUS>"+(BTSLUtil.isNullString(txnStatus)?"":txnStatus)+"</TXNSTATUS>");
						if(txnAmt==null)
							sbf.append("<TXNAMOUNT></TXNAMOUNT>");
						else
							sbf.append("<TXNAMOUNT>"+PretupsBL.getDisplayAmount(txnAmt)+"</TXNAMOUNT>");
						sbf.append("<RECEIVERMSISDN>"+(BTSLUtil.isNullString(recMsisdn)?"":recMsisdn)+"</RECEIVERMSISDN>");
						sbf.append("<TRFSUBTYPE>"+(BTSLUtil.isNullString(trfSubtype)?"":trfSubtype)+"</TRFSUBTYPE>");
						sbf.append("<WALLET>"+(BTSLUtil.isNullString(wallet)?"":wallet)+"</WALLET>");
						sbf.append("</RECORD>");
						sbf.append("</PRODUCTDETAILS>");
					sbf.append("</COMMAND>");
					responseStr = sbf.toString();
					pRequestVO.setSenderReturnMessage(responseStr);
				}
			catch (Exception e) {
		        _log.errorTrace(methodName, e);
		        _log.error(methodName, "Exception e: " + e);
		        pRequestVO.setMessageCode(PretupsErrorCodesI.CHNL_TXN_ENQ_ERROR);
		        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
		            "ExtAPIXMLStringParser[generateExtChannelTxnStatusResponse]", PretupsErrorCodesI.CHNL_TXN_ENQ_ERROR, "", "",
		            "generateExtChannelTxnStatusResponse:" + e.getMessage());
		    } finally {
		        if (_log.isDebugEnabled()) {
		            _log.debug(methodName, "Exiting responseStr: " + responseStr);
		        }
		    }
		}
			
			
			/*
			 * Description: Added for Channel User Commission Earned Enq Request
			 * @author : Anjali
			 * @param : RequestVO
			 */
			public static void parseCUCommEarnedEnqRequest(RequestVO pRequestVO)
			{
				String methodName="parseCUCommEarnedEnqRequest";
				if(_log.isDebugEnabled())
					_log.debug(methodName,"Entered p_requestVO: "+pRequestVO.toString());
				try
				{
					String parsedRequestStr=null;
					HashMap requestMap=new HashMap();
					String requestStr=pRequestVO.getRequestMessage();
					int index=requestStr.indexOf("<TYPE>");
					String type=requestStr.substring(index+"<TYPE>".length(),requestStr.indexOf("</TYPE>",index));
					requestMap.put("TYPE", type.trim());
					index=requestStr.indexOf("<DATE>");
					String date=requestStr.substring(index+"<DATE>".length(),requestStr.indexOf("</DATE>",index));
					date = BTSLDateUtil.getGregorianDateInString(date);

					requestMap.put("DATE", date);
					index=requestStr.indexOf("<NO_OF_DAYS>");
					String noOfDays=requestStr.substring(index+"<NO_OF_DAYS>".length(),requestStr.indexOf("</NO_OF_DAYS>",index));
					requestMap.put("NO_OF_DAYS", noOfDays);
					index=requestStr.indexOf("<MSISDN1>");
					String msisdn=requestStr.substring(index+"<MSISDN1>".length(),requestStr.indexOf("</MSISDN1>",index));
					requestMap.put("MSISDN", msisdn);
					index=requestStr.indexOf("<PIN>");
					String pin=requestStr.substring(index+"<PIN>".length(),requestStr.indexOf("</PIN>",index));
					requestMap.put("PIN", pin);
					parsedRequestStr = PretupsI.CU_COMM_EARNED + CHNL_MESSAGE_SEP + msisdn + CHNL_MESSAGE_SEP + pin;
					pRequestVO.setDecryptedMessage(parsedRequestStr);
					pRequestVO.setRequestMSISDN(msisdn);
					pRequestVO.setRequestMap(requestMap);
				}
				catch (Exception e)
				{
					_log.errorTrace(methodName, e);
					pRequestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
					_log.error(methodName, "Exception e: " + e.getMessage());
				} finally
				{
					if (_log.isDebugEnabled()) {
						_log.debug(methodName, "Exiting p_requestVO: " + pRequestVO.toString());
					}
				}
			}


			/*
			 * Description: Added for Channel User Commission Earned Enq Response
			 * @author : Anjali
			 * @param RequestVO
			 */
			public static void generateCUCommEarnedEnqResponse(RequestVO pRequestVO)
			{
				String methodName="generateCUCommEarnedEnqResponse";
				if(_log.isDebugEnabled())
					_log.debug(methodName,"Entered p_requestVO: "+pRequestVO.toString());
				StringBuilder sbf=null;
				String responseStr=null;
				try
				{
					sbf=new StringBuilder();
					sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
					sbf.append("<TYPE>LTCOMRESP</TYPE>");
					if(pRequestVO.isSuccessTxn())
					{
						sbf.append("<TXNSTATUS>"+PretupsI.TXN_STATUS_SUCCESS+"</TXNSTATUS>");
					}
					else
					{
						sbf.append("<TXNSTATUS>"+pRequestVO.getMessageCode()+"</TXNSTATUS>");
					}
					if (pRequestVO.getMessageCode() == null) {
						sbf.append("<MESSAGE></MESSAGE>");
					} else {
						sbf.append("<MESSAGE>").append(getMessage(pRequestVO.getLocale(), pRequestVO.getMessageCode(), pRequestVO.getMessageArguments())).append("</MESSAGE>");
					}
					sbf.append("</COMMAND>");
					responseStr = sbf.toString();
					pRequestVO.setSenderReturnMessage(responseStr);
				}
				catch (Exception e) {
					_log.errorTrace(methodName, e);
					_log.error(methodName, "Exception e: " + e.getMessage());
					pRequestVO.setMessageCode(PretupsErrorCodesI.CHNL_TXN_ENQ_ERROR);
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
							"XMLStringParser[generateCUCommEarnedEnqResponse]", PretupsErrorCodesI.CHNL_TXN_ENQ_ERROR, "", "",
							"generateCUCommEarnedEnqResponse:" + e.getMessage());
				} finally {
					if (_log.isDebugEnabled()) {
						_log.debug(methodName, "Exiting responseStr: " + responseStr);
					}
				}
			}
			
			public static void generateDVDResponse(RequestVO p_requestVO) throws Exception {
		        final String methodName = "generateDVDResponse";
		        if (_log.isDebugEnabled()) {
		            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
		        }
		        String responseStr = null;
		        StringBuffer sbf = null;
		        try {
		        	//Added for EXTGW response in English language always
					Locale  locale = new Locale("en","US");
		            sbf = new StringBuffer(1024);
		            sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
		            sbf.append("<TYPE>DVDRESP</TYPE>");
		            if (p_requestVO.isSuccessTxn()) {
		                sbf.append("<TXNSTATUS>").append(PretupsI.TXN_STATUS_SUCCESS).append("</TXNSTATUS>");
		                sbf.append("<MESSAGE>").append(p_requestVO.getSenderReturnMessage()).append("</MESSAGE>");
		            } else {
		                sbf.append("<TXNSTATUS>").append(p_requestVO.getMessageCode()).append("</TXNSTATUS>");
			            sbf.append("<MESSAGE>").append(getMessage(locale, p_requestVO.getMessageCode(), p_requestVO.getMessageArguments())).append("</MESSAGE>");
		            }
		            sbf.append("</COMMAND>");
		            responseStr = sbf.toString();
		            p_requestVO.setSenderReturnMessage(responseStr);
		        } catch (Exception e) {
		            _log.errorTrace(methodName, e);
		            _log.error(methodName, "Exception e: " + e);
		            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
		            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateEVDResponse]",
		                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateEVDResponse:" + e.getMessage());
		        } finally {
		            if (_log.isDebugEnabled()) {
		                _log.debug(methodName, "Exiting responseStr: " + responseStr);
		            }
		        }
		    }
			
			/**
			 * Method to parse External Interface Vas Recharge Request
			 * @param p_requestVO
			 * @throws Exception
			 * @author rahul.dutt
			 */
			public static void parseChannelExtVASTransferRequest(RequestVO p_requestVO) throws Exception
			{
				final String methodName = "parseChannelExtVASTransferRequest";
				if(_log.isDebugEnabled())
					_log.debug(methodName,"Entered p_requestVO: "+p_requestVO.toString());
				String parsedRequestStr=null;
				try
				{
					HashMap requestHashMap =new HashMap();
					String requestStr=p_requestVO.getRequestMessage();
					int index=requestStr.indexOf("<TYPE>");
					String type=requestStr.substring(index+"<TYPE>".length(),requestStr.indexOf("</TYPE>",index));
					requestHashMap.put("TYPE",type);
					index=requestStr.indexOf("<DATE>");
					String date=requestStr.substring(index+"<DATE>".length(),requestStr.indexOf("</DATE>",index));
					if(!BTSLUtil.isNullString(date)&&!BTSLUtil.isValidDatePattern(date))
					{
						p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_DATE);
						String args [] = {(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT))};
						throw new BTSLBaseException("XMLStringParser",methodName,PretupsErrorCodesI.INVALID_DATE,args);
					}
					requestHashMap.put("DATE",date);
					index=requestStr.indexOf("<EXTNWCODE>");
					String extNwCode=requestStr.substring(index+"<EXTNWCODE>".length(),requestStr.indexOf("</EXTNWCODE>",index));
					requestHashMap.put("EXTNWCODE",extNwCode);
					index=requestStr.indexOf("<MSISDN>");
					String msisdn=requestStr.substring(index+"<MSISDN>".length(),requestStr.indexOf("</MSISDN>",index));
					requestHashMap.put("MSISDN",msisdn);
					index=requestStr.indexOf("<PIN>");
					String pin=requestStr.substring(index+"<PIN>".length(),requestStr.indexOf("</PIN>",index));
					requestHashMap.put("PIN",pin);
					index=requestStr.indexOf("<LOGINID>");
					String loginId=requestStr.substring(index+"<LOGINID>".length(),requestStr.indexOf("</LOGINID>",index));
					requestHashMap.put("LOGINID",loginId);
					index=requestStr.indexOf("<PASSWORD>");
					String password=requestStr.substring(index+"<PASSWORD>".length(),requestStr.indexOf("</PASSWORD>",index));
					requestHashMap.put("PASSWORD",password);
					if((!BTSLUtil.isNullString(msisdn) && BTSLUtil.isNullString(pin))|| (!BTSLUtil.isNullString(loginId) && BTSLUtil.isNullString(password)))
						throw new BTSLBaseException("XMLStringParser",methodName,PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
					index=requestStr.indexOf("<EXTCODE>");
					String extCode=requestStr.substring(index+"<EXTCODE>".length(),requestStr.indexOf("</EXTCODE>",index));
					requestHashMap.put("EXTCODE",extCode);
					index=requestStr.indexOf("<EXTREFNUM>");
					String extRefNumber=requestStr.substring(index+"<EXTREFNUM>".length(),requestStr.indexOf("</EXTREFNUM>",index));
					requestHashMap.put("EXTREFNUM",extRefNumber);
					index=requestStr.indexOf("<MSISDN2>");
					String msisdn2=requestStr.substring(index+"<MSISDN2>".length(),requestStr.indexOf("</MSISDN2>",index));
					if(BTSLUtil.isNullString(msisdn2))
						throw new BTSLBaseException("XMLStringParser",methodName,PretupsErrorCodesI.CHNL_ERROR_RECR_MSISDN_BLANK);
					index=requestStr.indexOf("<AMOUNT>");
					String amount=requestStr.substring(index+"<AMOUNT>".length(),requestStr.indexOf("</AMOUNT>",index));
					index=requestStr.indexOf("<LANGUAGE1>");
					String language1=requestStr.substring(index+"<LANGUAGE1>".length(),requestStr.indexOf("</LANGUAGE1>",index));
					index=requestStr.indexOf("<LANGUAGE2>");
					String language2=requestStr.substring(index+"<LANGUAGE2>".length(),requestStr.indexOf("</LANGUAGE2>",index));
					index=requestStr.indexOf("<SELECTOR>");
					String selector=requestStr.substring(index+"<SELECTOR>".length(),requestStr.indexOf("</SELECTOR>",index));
					if(BTSLUtil.isNullString(selector))
					{
						throw new BTSLBaseException("XMLStringParser",methodName,PretupsErrorCodesI.VAS_PROMOVAS_REQ_SELECTOR_MISSING);
					}
					boolean multiAmountEnabled = (Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTI_AMOUNT_ENABLED));
					if(!BTSLUtil.isNullString(amount) && multiAmountEnabled)
						parsedRequestStr=PretupsI.SERVICE_TYPE_VAS_RECHARGE+CHNL_MESSAGE_SEP+msisdn2+CHNL_MESSAGE_SEP+amount+CHNL_MESSAGE_SEP+selector+CHNL_MESSAGE_SEP+language2;
					else if(!multiAmountEnabled)
						parsedRequestStr=PretupsI.SERVICE_TYPE_VAS_RECHARGE+CHNL_MESSAGE_SEP+msisdn2+CHNL_MESSAGE_SEP+selector+CHNL_MESSAGE_SEP+language2;
					else
						throw new BTSLBaseException("XMLStringParser",methodName,PretupsErrorCodesI.AMOUNT_REQUIRED);
					if(BTSLUtil.isNullString(language1) || BTSLUtil.isNullString(language2))
						throw new BTSLBaseException("XMLStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_INVALID_LANGUAGECODE);
					
					p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
					p_requestVO.setReqSelector(selector);
					p_requestVO.setDecryptedMessage(parsedRequestStr);
					p_requestVO.setRequestMSISDN(msisdn);
					p_requestVO.setExternalNetworkCode(extNwCode);
					p_requestVO.setSenderExternalCode(extCode);
					p_requestVO.setSenderLoginID(loginId);
					p_requestVO.setRequestMap(requestHashMap);
					p_requestVO.setExternalReferenceNum(extRefNumber);//To enquire Transaction Status with External Refrence Number
					if(BTSLUtil.isNullString(pin)&& BTSLUtil.isNullString(msisdn)&& !BTSLUtil.isNullString(loginId) && !BTSLUtil.isNullString(password))
						p_requestVO.setPinValidationRequired(false);
				}
				catch(BTSLBaseException be)
				{
					_log.error(methodName," BTSL Exception while parsing Request Message :"+be.getMessage());
					throw be;
				}
				catch(Exception e)
				{
					p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
					_log.error(methodName,"Exception e: "+e);
					throw new BTSLBaseException("XMLStringParser",methodName,PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
				}
				finally
				{
					if(_log.isDebugEnabled())_log.debug(methodName,"Exiting p_requestVO: "+p_requestVO.toString());
				}
			}

	public static void parseChannelUserAuthRequest(RequestVO p_requestVO)throws BTSLBaseException
	{
		if(_log.isDebugEnabled())_log.debug("parseChannelUserAuthRequest","Entered p_requestVO: "+p_requestVO.toString());
		String parsedRequestStr =null;
		try
		{
			HashMap requestMap=new HashMap();
			String requestStr=p_requestVO.getRequestMessage();
			int index=requestStr.indexOf("<TYPE>");
			String type=requestStr.substring(index+"<TYPE>".length(),requestStr.indexOf("</TYPE>",index));
			requestMap.put("TYPE",type);

			index=requestStr.indexOf("<EXTNWCODE>");
			String chnnUserExtCode=requestStr.substring(index+"<EXTNWCODE>".length(),requestStr.indexOf("</EXTNWCODE>",index));
			requestMap.put("EXTNWCODE",chnnUserExtCode);
			if(BTSLUtil.isNullString(chnnUserExtCode) && PretupsI.REQUEST_SOURCE_TYPE_EXTGW.equalsIgnoreCase(p_requestVO.getRequestGatewayCode()))
			{
				p_requestVO.setMessageCode(PretupsErrorCodesI.EXT_USRADD_ERROR_MISSING_MANDATORY_FIELDS);
				throw new BTSLBaseException("XMLStringParser","parseExtUserRequest",PretupsErrorCodesI.EXT_USRADD_ERROR_MISSING_MANDATORY_FIELDS);
			}
			index=requestStr.indexOf("<DATE>");
			String date=requestStr.substring(index+"<DATE>".length(),requestStr.indexOf("</DATE>",index));
			if (!BTSLUtil.isValidDatePattern(date))
			{
				_log.error("parseChannelUserAuthRequest","in valid date  message format  ");
				throw new BTSLBaseException("XMLStringParser","parseChannelUserAuthRequest",PretupsErrorCodesI.INVALID_DATE);
			}
			requestMap.put("DATE",date);

			index=requestStr.indexOf("<EXTREFNUM>");
			String extRefNum=requestStr.substring(index+"<EXTREFNUM>".length(),requestStr.indexOf("</EXTREFNUM>",index));
			requestMap.put("EXTREFNUM",extRefNum);

		    index=requestStr.indexOf("<EXTCODE>");
		    String extCode=requestStr.substring(index+"<EXTCODE>".length(),requestStr.indexOf("</EXTCODE>",index));
		    requestMap.put("EXTCODE",extCode);
			index=requestStr.indexOf("<USERLOGINID>");
			String userLoginID=requestStr.substring(index+"<USERLOGINID>".length(),requestStr.indexOf("</USERLOGINID>",index));
			requestMap.put("USERLOGINID",userLoginID);

			index=requestStr.indexOf("<USERPASSWORD>");
			String userPassword=requestStr.substring(index+"<USERPASSWORD>".length(),requestStr.indexOf("</USERPASSWORD>",index));
			requestMap.put("USERPASSWORD",userPassword);
		    requestMap.put("PASSWORD", userPassword);
			index=requestStr.indexOf("<MSISDN1>");
			String msisdn=requestStr.substring(index+"<MSISDN1>".length(),requestStr.indexOf("</MSISDN1>",index));
			requestMap.put("MSISDN",msisdn);

			index=requestStr.indexOf("<PIN>");
			String pin=requestStr.substring(index+"<PIN>".length(),requestStr.indexOf("</PIN>",index));
			requestMap.put("PIN",pin);
			if((BTSLUtil.isNullString(userLoginID) && BTSLUtil.isNullString(userPassword)) && (BTSLUtil.isNullString(msisdn) && BTSLUtil.isNullString(pin)) )
			{
				_log.error("parseChannelUserAuthRequest","in valid message format  ");
				throw new BTSLBaseException("XMLStringParser","parseChannelUserAuthRequest",PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT);
			}
			parsedRequestStr=PretupsI.SERVICE_TYPE_USER_AUTH;
			p_requestVO.setDecryptedMessage(parsedRequestStr);
			p_requestVO.setRequestMap(requestMap);
			p_requestVO.setRequestMSISDN(msisdn);
			p_requestVO.setSenderLoginID(userLoginID);
			p_requestVO.setExternalReferenceNum(extRefNum);
		    p_requestVO.setExternalNetworkCode(chnnUserExtCode);
		    p_requestVO.setSenderExternalCode(extCode);
		}
		catch(BTSLBaseException be)
		{
			_log.error("parseExtUserRequest","BTSLBaseException e: "+be);
			throw be;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT);
			p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);

			_log.error("parseChannelUserAuthRequest","Exception e: "+e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"XMLStringParser[parseChannelUserAuthRequest]",PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT,"","","parseChannelUserAuthRequest:"+e.getMessage());
			throw new BTSLBaseException("XMLStringParser","parseChannelUserAuthRequest",PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT);
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug("parseChannelUserAuthRequest","Exiting  p_requestVO: "+p_requestVO.toString());
		}
	}
	
	
    public static void generateSelfChannelUserBarResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateSelfChannelUserBarResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
//            if(PretupsI.YES.equalsIgnoreCase(DOCTYPE_COMMAND_IN_RESPONSE_REQUIRED))
//            	sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
//            else 
            sbf.append("<?xml version=\"1.0\"?><COMMAND>");
            sbf.append("<TYPE>BARRESP</TYPE>");

            if (p_requestVO.isSuccessTxn()) {
                sbf.append("<TXNSTATUS>" + PretupsI.TXN_STATUS_SUCCESS + "</TXNSTATUS>");
            } else {
                String message = p_requestVO.getMessageCode();
                if (message.indexOf("_") != -1) {
                    message = message.substring(0, message.indexOf("_"));
                }
                sbf.append("<TXNSTATUS>" + message + "</TXNSTATUS>");
            }
            sbf.append("<MESSAGE>" + getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()) + "</MESSAGE>");
            sbf.append("</COMMAND>");
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateSelfChannelUserBarResponse]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateSelfChannelUserBarResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting responseStr: " + responseStr);
            }
        }
    }


}
