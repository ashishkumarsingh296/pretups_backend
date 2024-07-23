/*
 * @(#)PhiysicalPOSParser.java
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
package com.btsl.pretups.gateway.parsers;

import java.sql.Connection;
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
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.POSXMLStringParser;
import com.btsl.pretups.gateway.util.ParserUtility;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;

/**
 * @author manisha.jain
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class PhysicalPOSParser extends ParserUtility {
    public static final Log LOG = LogFactory.getLog(PhysicalPOSParser.class.getName());

    public void parseRequestMessage(RequestVO p_requestVO) throws BTSLBaseException {
    }

    public void generateResponseMessage(RequestVO p_requestVO) {
    }

    public void parseChannelRequestMessage(RequestVO p_requestVO, Connection pCon) throws BTSLBaseException {
        final String METHOD_NAME = "parseChannelRequestMessage";
        String contentType = p_requestVO.getReqContentType();
        if (LOG.isDebugEnabled()) {
            LOG.debug("parseChannelRequestMessage", "Transfer ID=" + p_requestVO.getRequestID() + " contentType: " + contentType);
        }
        try {
            if (contentType != null && (contentType.indexOf("text") != -1 || contentType.indexOf("TEXT") != -1)) {
                int action = actionChannelParser(p_requestVO);
                parseChannelRequest(action, p_requestVO);
                ChannelUserBL.updateUserInfo(pCon, p_requestVO);
            } else {
                // Forward to plain message parsing
                // Set message Format , set in decrypted message
                p_requestVO.setDecryptedMessage(p_requestVO.getRequestMessage());
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("parseChannelRequestMessage", "Message =" + p_requestVO.getDecryptedMessage() + " MSISDN=" + p_requestVO.getRequestMSISDN());
            }
        } catch (BTSLBaseException be) {
            LOG.errorTrace(METHOD_NAME, be);
            LOG.error("parseChannelRequestMessage", " BTSL Exception while parsing Request Message :" + be.getMessage());
            throw be;
        } catch (Exception e) {
            LOG.error("parseChannelRequestMessage", "  Exception while parsing Request Message :" + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "USSDParsers[parseChannelRequestMessage]", p_requestVO
                .getTransactionID(), "", "", "Exception :" + e.getMessage());
            throw new BTSLBaseException("USSDParsers", "parseChannelRequestMessage", PretupsErrorCodesI.ERROR_EXCEPTION,e);
        }
    }

    public void generateChannelResponseMessage(RequestVO p_requestVO) {
        final String METHOD_NAME = "generateChannelResponseMessage";
        String contentType = p_requestVO.getReqContentType();
        if (LOG.isDebugEnabled()) {
            LOG.debug("generateChannelResponseMessage", "Transfer ID=" + p_requestVO.getRequestID() + " contentType: " + contentType);
        }
        try {
            p_requestVO.setSenderMessageRequired(false);
            if (contentType != null && (p_requestVO.getReqContentType().indexOf("xml") != -1 || p_requestVO.getReqContentType().indexOf("XML") != -1)) {
                // Set the Sender Return Message
                if (p_requestVO.getActionValue() == -1) {
                    actionChannelParser(p_requestVO);
                }
                generateChannelResponse(p_requestVO.getActionValue(), p_requestVO);
            } else {
                String message = null;
                if (!BTSLUtil.isNullString(p_requestVO.getSenderReturnMessage())) {
                    message = p_requestVO.getSenderReturnMessage();
                } else {
                    message = BTSLUtil.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
                }

                p_requestVO.setSenderReturnMessage(message);
            }
        } catch (Exception e) {
            LOG.error("generateChannelResponseMessage", "  Exception while generating Response Message :" + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "USSDParsers[generateChannelResponseMessage]",
                p_requestVO.getTransactionID(), "", "", "Exception getting message :" + e.getMessage());
            try {
                POSXMLStringParser.generateFailureResponse(p_requestVO);
            } catch (Exception ex) {
                LOG.errorTrace(METHOD_NAME, ex);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "USSDParsers[generateChannelResponseMessage]",
                    p_requestVO.getTransactionID(), "", "", "Exception getting default message :" + ex.getMessage());
                p_requestVO
                    .setSenderReturnMessage("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND><TYPE></TYPE><TXNSTATUS>" + PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT + "</TXNSTATUS></COMMAND>");
            }
        }
    }

    /**
     * Method to parse channel requests on basis of keyword
     * 
     * @param action
     * @param p_requestVO
     * @throws Exception
     */
    public void parseChannelRequest(int action, RequestVO p_requestVO) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("parseChannelRequest", "Entered Request ID=" + p_requestVO.getRequestID() + " action=" + action);
        }

        switch (action) {
            case ACTION_CHNL_EXT_RECH_STATUS:
                {
                    POSXMLStringParser.parseChannelExtRechargeStatusRequest(p_requestVO);
                    break;
                }
            case ACTION_CHNL_EXT_CREDIT_TRANSFER:
                {
                    POSXMLStringParser.parseChannelExtCreditTransferRequest(p_requestVO);
                    break;
                }
            case ACTION_CHNL_EXT_TRANSFER_BILL_PAYMENT:
                {
                    POSXMLStringParser.parseChannelExtTransferBillPayment(p_requestVO);
                    break;
                }
            case ACTION_CHNL_BAL_ENQ_XML:
                {
                    POSXMLStringParser.parseExtChannelUserBalanceRequest(p_requestVO);
                    break;
                }
            case ACTION_EXT_C2SCHANGEPIN_XML:
                {
                    POSXMLStringParser.parseExtChangepinRequest(p_requestVO);
                    break;
                }
            case ACTION_EXT_LAST_XTRF_ENQ:
                {
                    POSXMLStringParser.parseExtLastXTransferEnq(p_requestVO);
                    break;
                }
            case ACTION_EXT_CUSTOMER_ENQ_REQ:
                {
                    POSXMLStringParser.parseExtCustomerEnqReq(p_requestVO);
                    break;
                }
            case ACTION_EXT_OTHER_BAL_ENQ:
                {
                    POSXMLStringParser.parseExtUserOtherBalEnqReq(p_requestVO);
                    break;
                }
            case ACTION_EXT_DAILY_STATUS_REPORT:
                {
                    POSXMLStringParser.parseExtDailyTransactionRequest(p_requestVO);
                    break;
                }
            case ACTION_EXT_LAST_TRF:
                {
                    POSXMLStringParser.parseExtChannelLastTransferStatusRequest(p_requestVO);
                    break;
                }
            case ACTION_EXT_EVD_RC_POS:
                {
                    POSXMLStringParser.parseChannelExtPrivateRechargeRequest(p_requestVO);
                    break;
                }
            case ACTION_EXT_PPBENQ:
                {
                    POSXMLStringParser.parseChannelExtPPBEnqRequest(p_requestVO);
                    break;
                }
            default:
      	     	 if(LOG.isDebugEnabled()){
      	     		LOG.debug("Default Value " ,action);
      	     	 }
        }
    }

    /**
     * Method to load and validate user details
     * 
     * @param p_con
     * @param p_requestVO
     * @throws BTSLBaseException
     */

    public ChannelUserVO loadValidateUserDetails(Connection p_con, RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "loadValidateUserDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadValidateUserDetails", "Entered Request ID=" + p_requestVO.getRequestID());
        }

        ChannelUserVO channelUserVO = null;
        ChannelUserVO staffUserVO = null;
        boolean isAllowedTime = false;
        try {
            String networkID = p_requestVO.getRequestNetworkCode();
            // Load ChannelUser on basis of MSISDN
            channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getFilteredMSISDN());
            validateUserDetails(p_requestVO, channelUserVO);
            if (channelUserVO != null) {
                if (!networkID.equalsIgnoreCase(channelUserVO.getNetworkID())) {
                    throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
                }

                p_requestVO.setMessageSentMsisdn(channelUserVO.getUserPhoneVO().getMsisdn());
                if (!channelUserVO.getUserID().equals(p_requestVO.getActiverUserId())) {
                    channelUserVO.setStaffUser(true);
                    staffUserVO = _channelUserDAO.loadChannelUserDetailsByUserId(p_con, p_requestVO.getActiverUserId(), channelUserVO.getUserID());
                    validateUserDetails(p_requestVO, staffUserVO);
                    channelUserVO.setActiveUserID(staffUserVO.getUserID());
                    staffUserVO.setActiveUserID(staffUserVO.getUserID());
                    staffUserVO.setStaffUser(true);
                    if (staffUserVO != null && !PretupsI.NOT_AVAILABLE.equals(staffUserVO.getUserPhoneVO().getMsisdn())) {
                        p_requestVO.setMessageSentMsisdn(staffUserVO.getUserPhoneVO().getMsisdn());
                    } else {
                        UserPhoneVO userPhoneVO = channelUserVO.getUserPhoneVO();
                        p_requestVO.setSenderLocale(new Locale(userPhoneVO.getPhoneLanguage(), userPhoneVO.getCountry()));
                    }
                    channelUserVO.setStaffUserDetails(staffUserVO);
                } else {
                    channelUserVO.setActiveUserID(channelUserVO.getUserID());
                }
            }
            p_requestVO.setSenderVO(channelUserVO);

            if (channelUserVO != null && !channelUserVO.isStaffUser()) {
                isAllowedTime = BTSLUtil.isDayTimeValid(channelUserVO.getAllowedDays(), channelUserVO.getFromTime(), channelUserVO.getToTime());
            } else {
                isAllowedTime = BTSLUtil.isDayTimeValid(staffUserVO.getAllowedDays(), staffUserVO.getFromTime(), staffUserVO.getToTime());
            }
            if (!isAllowedTime) {
                throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.INVALID_ACCESS_TIME);
            }

            if (!p_requestVO.getMessageGatewayVO().isUserAuthorizationReqd()) {
                p_requestVO.setPinValidationRequired(false);
            }

            String CHNL_MESSAGE_SEP = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
            if (BTSLUtil.isNullString(CHNL_MESSAGE_SEP)) {
                CHNL_MESSAGE_SEP = " ";
            }
            String message = p_requestVO.getDecryptedMessage();

            if (p_requestVO.getActionValue() != ACTION_EXT_C2SCHANGEPIN_XML) {
                if (!p_requestVO.isPinValidationRequired()) {
                    message = message + CHNL_MESSAGE_SEP + (String) p_requestVO.getRequestMap().get("PIN");
                } else {
                    if (BTSLUtil.NullToString(p_requestVO.getRequestMap().get("PIN").toString()).length() == 0) {
                        throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.ERROR_INVALID_PIN);
                    }
                    message = message + CHNL_MESSAGE_SEP + (String) p_requestVO.getRequestMap().get("PIN");
                }
            }

            p_requestVO.setDecryptedMessage(message);

            // for pussing message in hexadecimal
            /*
             * if(!channelUserVO.isStaffUser() &&
             * "ar".equals(channelUserVO.getUserPhoneVO().getPhoneLanguage()) &&
             * "EG".equals(channelUserVO.getUserPhoneVO().getCountry()))
             * p_requestVO.setSenderLocale(new Locale("ar1","EG"));
             * else if(channelUserVO.isStaffUser()&& staffUserVO!=null &&
             * "ar".equals(staffUserVO.getUserPhoneVO().getPhoneLanguage()) &&
             * "EG".equals(staffUserVO.getUserPhoneVO().getCountry()))
             * p_requestVO.setSenderLocale(new Locale("ar1","EG"));
             */
        } catch (BTSLBaseException be) {
            LOG.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.ERROR_EXCEPTION,e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadValidateUserDetails", "Exiting Request ID=" + p_requestVO.getRequestID());
        }
        if (!channelUserVO.isStaffUser()) {
            return channelUserVO;
        } else {
            return staffUserVO;
        }
    }

    // Same till when responses are same in USSD and ExtAPI , when separated
    // this method will be overridden in respective parsers
    /**
     * Method to generate Response of Channel requests
     * 
     * @param p_requestVO
     * @throws Exception
     */
    public static void generateChannelResponse(int action, RequestVO p_requestVO) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("generateChannelResponse", "Entered Request ID=" + p_requestVO.getRequestID() + " action=" + action);
        }

        String messageCode = p_requestVO.getMessageCode();
        if ((!BTSLUtil.isNullString(messageCode)) && (!p_requestVO.isSuccessTxn()) && messageCode.indexOf("_") != -1) {
            messageCode = messageCode.substring(0, messageCode.indexOf("_"));
            p_requestVO.setMessageCode(messageCode);
        }
        switch (action) {
            case ACTION_CHNL_EXT_RECH_STATUS:
                {
                    POSXMLStringParser.generateChannelExtRechargeStatusResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_EXT_CREDIT_TRANSFER:
                {
                    POSXMLStringParser.generateChannelExtCreditTransferResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_EXT_TRANSFER_BILL_PAYMENT:
                {
                    POSXMLStringParser.generateExtPostpaidBillPaymentResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_BAL_ENQ_XML:
                {
                    POSXMLStringParser.generateExtChannelUserBalanceResponse(p_requestVO);
                    break;
                }
            case ACTION_EXT_C2SCHANGEPIN_XML:
                {
                    POSXMLStringParser.generateExtChangepinResponse(p_requestVO);
                    break;
                }

            case ACTION_EXT_LAST_XTRF_ENQ: // last X C2S transfer
                {
                    POSXMLStringParser.generateExtLastXTrfResponse(p_requestVO);
                    break;
                }
            case ACTION_EXT_CUSTOMER_ENQ_REQ: // last X C2S transfer
                {
                    POSXMLStringParser.generateExtCustomerEnqResponse(p_requestVO);
                    break;
                }
            case ACTION_EXT_OTHER_BAL_ENQ: // last X C2S transfer
                {
                    POSXMLStringParser.generateExtChannelUserOtherBalRes(p_requestVO);
                    break;
                }
            case ACTION_EXT_LAST_TRF: // last X C2S transfer
                {
                    POSXMLStringParser.generateExtChannelLastTransferStatusResponse(p_requestVO);
                    break;
                }

            case ACTION_EXT_DAILY_STATUS_REPORT: // last X C2S transfer
                {
                    POSXMLStringParser.generateExtDailyTransactionResponse(p_requestVO);
                    break;
                }
            case ACTION_EXT_EVD_RC_POS: // added by nilesh: Private recharge
                // through
                // POS
                {
                    POSXMLStringParser.generateChannelExtPrivateRechargeResponse(p_requestVO);
                    break;
                }
            case ACTION_EXT_PPBENQ:
                {
                    POSXMLStringParser.generateChannelExtPPBENQResponse(p_requestVO);
                    break;
                }
            default:
      	     	 if(LOG.isDebugEnabled()){
      	     		LOG.debug("Default Value " ,action);
      	     	 }
        }
    }

    /**
     * Method to mark and unmark the request for subscriber
     * 
     * @param p_con
     * @param p_requestVO
     * @param p_module
     * @param p_mark
     * @throws BTSLBaseException
     */
    public void checkRequestUnderProcess(Connection p_con, RequestVO p_requestVO, String p_module, boolean p_mark, ChannelUserVO channeluserVO) throws BTSLBaseException {
        final String METHOD_NAME = "checkRequestUnderProcess";
        if (LOG.isDebugEnabled()) {
            LOG.debug("checkRequestUnderProcess",
                "Entered Request ID=" + p_requestVO.getRequestID() + " p_module=" + p_module + " p_mark=" + p_mark + " Check Required=" + p_requestVO.getMessageGatewayVO()
                    .getRequestGatewayVO().getUnderProcessCheckReqd());
        }
        try {
            if (TypesI.YES.equals(p_requestVO.getMessageGatewayVO().getRequestGatewayVO().getUnderProcessCheckReqd())) {
                if (PretupsI.C2S_MODULE.equals(p_module)) {
                    ChannelUserBL.checkRequestUnderProcessPOS(p_con, p_requestVO.getRequestIDStr(), channeluserVO.getUserPhoneVO(), p_mark);
                }

            }
        } catch (BTSLBaseException be) {
            LOG.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "validateUserDetails", PretupsErrorCodesI.ERROR_EXCEPTION,e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("checkRequestUnderProcess", "Exiting For Request ID=" + p_requestVO.getRequestID());
        }

    }

    public void parseChannelRequestMessage(Connection p_con, RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "parseChannelRequestMessage";
        String contentType = p_requestVO.getReqContentType();
        if (LOG.isDebugEnabled()) {
            LOG.debug("parseChannelRequestMessage", "Transfer ID=" + p_requestVO.getRequestID() + " contentType: " + contentType);
        }
        try {
            if (contentType != null && (contentType.indexOf("text") != -1 || contentType.indexOf("TEXT") != -1)) {
                int action = actionChannelParser(p_requestVO);
                parseChannelRequest(action, p_requestVO);
                updateUserInfo(p_con, p_requestVO);
            } else {
                // Forward to plain message parsing
                // Set message Format , set in decrypted message
                p_requestVO.setDecryptedMessage(p_requestVO.getRequestMessage());
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("parseChannelRequestMessage", "Message =" + p_requestVO.getDecryptedMessage() + " MSISDN=" + p_requestVO.getRequestMSISDN());
            }
        } catch (BTSLBaseException be) {
            LOG.errorTrace(METHOD_NAME, be);
            LOG.error("parseChannelRequestMessage", " BTSL Exception while parsing Request Message :" + be.getMessage());
            throw be;
        } catch (Exception e) {
            LOG.error("parseChannelRequestMessage", "  Exception while parsing Request Message :" + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "USSDParsers[parseChannelRequestMessage]", p_requestVO
                .getTransactionID(), "", "", "Exception :" + e.getMessage());
            throw new BTSLBaseException("USSDParsers", "parseChannelRequestMessage", PretupsErrorCodesI.ERROR_EXCEPTION,e);
        }
    }

    /**
     * Method to parse the Operator request
     * 
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public void parseOperatorRequestMessage(RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "parseOperatorRequestMessage";
        String contentType = p_requestVO.getReqContentType();
        if (LOG.isDebugEnabled()) {
            LOG.debug("parseOperatorRequestMessage", "Transfer ID=" + p_requestVO.getRequestID() + " contentType: " + contentType);
        }
        try {
            if (contentType != null && (contentType.indexOf("text") != -1 || contentType.indexOf("TEXT") != -1)) {
                int action = actionChannelParser(p_requestVO);
                parseChannelRequest(action, p_requestVO);
            } else {
                p_requestVO.setDecryptedMessage(p_requestVO.getRequestMessage());
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("parseOperatorRequestMessage", "Message =" + p_requestVO.getDecryptedMessage() + " MSISDN=" + p_requestVO.getRequestMSISDN());
            }
        } catch (BTSLBaseException be) {
            LOG.errorTrace(METHOD_NAME, be);
            LOG.error("parseOperatorRequestMessage", " BTSL Exception while parsing Request Message :" + be.getMessage());
            throw be;
        } catch (Exception e) {
            LOG.error("parseOperatorRequestMessage", "  Exception while parsing Request Message :" + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "USSDParsers[parseOperatorRequestMessage]", p_requestVO
                .getTransactionID(), "", "", "Exception :" + e.getMessage());
            throw new BTSLBaseException("USSDParsers", "parseOperatorRequestMessage", PretupsErrorCodesI.ERROR_EXCEPTION,e);
        }
    }
}