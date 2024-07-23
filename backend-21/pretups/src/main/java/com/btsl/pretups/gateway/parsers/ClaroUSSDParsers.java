package com.btsl.pretups.gateway.parsers;

/*
 * @(#)ClaroUSSDParsers.java
 * Copyright(c) 2005, Bharti Telesoft Ltd.
 * All Rights Reserved
 * ------------------------------------------------------------------------------
 * -------------------
 * Author Date History
 * ------------------------------------------------------------------------------
 * -------------------
 * Vipan Kumar June 19, 2014 Initital Creation
 * ------------------------------------------------------------------------------
 * -------------------
 * Parser class to handle Claro USSD requests
 */

import java.sql.Connection;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.gateway.util.ClaroUSSDStringParser;
import com.btsl.pretups.gateway.util.ExtAPIXMLStringParser;
import com.btsl.pretups.gateway.util.ParserUtility;
import com.btsl.pretups.gateway.util.USSDC2SXMLStringParser;
import com.btsl.pretups.gateway.util.USSDP2PXMLStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.util.BTSLUtil;

public class ClaroUSSDParsers extends ParserUtility {
    public static final Log _log = LogFactory.getLog(ClaroUSSDParsers.class.getClass());

    public void parseRequestMessage(RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "parseRequestMessage";
        String contentType = p_requestVO.getReqContentType();
        if (_log.isDebugEnabled()) {
            _log.debug("parseRequestMessage", "Transfer ID=" + p_requestVO.getRequestID() + " contentType: " + contentType);
        }
        try {
            if (contentType != null && (contentType.indexOf("xml") != -1 || contentType.indexOf("XML") != -1)) {
                // Forward to XML parsing
                // Set Filtered MSISDN, set _requestMSISDN
                // Set message Format , set in decrypted message
                int action = actionParser(p_requestVO);
                parseRequest(action, p_requestVO);
            } else if (contentType != null && (contentType.indexOf("plain") != -1 || contentType.indexOf("PLAIN") != -1)) {
                // Forward to XML parsing
                // Set Filtered MSISDN, set _requestMSISDN
                // Set message Format , set in decrypted message
                p_requestVO.setReqContentType(contentType);
                int action = actionParser(p_requestVO);
                parsePlainRequest(action, p_requestVO);
            } else {
                // Forward to plain message parsing
                // Set message Format , set in decrypted message
                p_requestVO.setDecryptedMessage(p_requestVO.getRequestMessage());
            }
            if (_log.isDebugEnabled()) {
                _log.debug("parseRequestMessage", "Message =" + p_requestVO.getDecryptedMessage() + " MSISDN=" + p_requestVO.getRequestMSISDN());
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("parseRequestMessage", " BTSL Exception while parsing Request Message :" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("parseRequestMessage", "  Exception while parsing Request Message :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroUSSDParsers[parseRequestMessage]", p_requestVO
                .getTransactionID(), "", "", "Exception :" + e.getMessage());
            throw new BTSLBaseException("ClaroUSSDParsers", "parseRequestMessage", PretupsErrorCodesI.ERROR_EXCEPTION,e);
        }
    }

    public void generateResponseMessage(RequestVO p_requestVO) {
        final String METHOD_NAME = "generateResponseMessage";
        String contentType = p_requestVO.getReqContentType();
        if (_log.isDebugEnabled()) {
            _log.debug("generateResponseMessage", "Transfer ID=" + p_requestVO.getRequestID() + " contentType: " + contentType);
        }
        try {
            if (contentType != null && (p_requestVO.getReqContentType().indexOf("xml") != -1 || p_requestVO.getReqContentType().indexOf("XML") != -1)) {
                // Set the Sender Return Message
                if (p_requestVO.getActionValue() == -1) {
                    actionParser(p_requestVO);
                }
                generateResponse(p_requestVO.getActionValue(), p_requestVO);
            } else if (contentType != null && (p_requestVO.getReqContentType().indexOf("plain") != -1 || p_requestVO.getReqContentType().indexOf("PLAIN") != -1) && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PLAIN_RES_PARSE_REQUIRED))).booleanValue()) {
                // Set the Sender Return Message
                if (p_requestVO.getActionValue() == -1) {
                    actionParser(p_requestVO);
                }
                generatePlainResponse(p_requestVO.getActionValue(), p_requestVO);
            } else {
                String message = null;
                if (!BTSLUtil.isNullString(p_requestVO.getSenderReturnMessage())) {
                    message = p_requestVO.getSenderReturnMessage();
                } else {
                    message = BTSLUtil.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
                }
                int index = 0;
                index = message.indexOf(":");
                if (index > -1) {
                    index = message.indexOf(":", index++);
                }

                if (index > -1) {
                    message = message.substring(index, message.length());
                }

                p_requestVO.setSenderReturnMessage(message);
            }
        } catch (Exception e) {
            _log.error("generateResponseMessage", "  Exception while generating Response Message :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroUSSDParsers[parseRequestMessage]", p_requestVO
                .getTransactionID(), "", "", "Exception getting message :" + e.getMessage());
            try {
                // XMLAPIParser.generateFailureResponse(p_requestVO);
                ExtAPIXMLStringParser.generateFailureResponse(p_requestVO);
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroUSSDParsers[parseRequestMessage]",
                    p_requestVO.getTransactionID(), "", "", "Exception getting default message :" + ex.getMessage());
                p_requestVO
                    .setSenderReturnMessage("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND><TYPE></TYPE><TXNSTATUS>" + PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT + "</TXNSTATUS></COMMAND>");
            }
        }
    }

    public void parseChannelRequestMessage(RequestVO p_requestVO, Connection pCon) throws BTSLBaseException {
        final String METHOD_NAME = "parseChannelRequestMessage";
        String contentType = p_requestVO.getReqContentType();
        if (_log.isDebugEnabled()) {
            _log.debug("parseChannelRequestMessage", "Transfer ID=" + p_requestVO.getRequestID() + " contentType: " + contentType);
        }
        try {
            if (contentType != null && (contentType.indexOf("xml") != -1 || contentType.indexOf("XML") != -1)) {
                // Forward to XML parsing
                // Set Filtered MSISDN, set _requestMSISDN
                // Set message Format , set in decrypted message
                // XMLAPIParser.actionChannelParser(p_requestVO);
                int action = actionChannelParser(p_requestVO);
                parseChannelRequest(action, p_requestVO);
                ChannelUserBL.updateUserInfo(pCon, p_requestVO);
            } else if (contentType != null && (contentType.indexOf("plain") != -1 || contentType.indexOf("PLAIN") != -1)) {
                // Forward for parsing
                // Set Filtered MSISDN, set _requestMSISDN
                // Set message Format , set in decrypted message
                // XMLAPIParser.actionChannelParser(p_requestVO);
                int action = actionChannelParser(p_requestVO);
                parseChannelPlainRequest(action, p_requestVO);
                ChannelUserBL.updateUserInfo(pCon, p_requestVO);
            } else {
                // Forward to plain message parsing
                // Set message Format , set in decrypted message
                p_requestVO.setDecryptedMessage(p_requestVO.getRequestMessage());
            }
            if (_log.isDebugEnabled()) {
                _log.debug("parseChannelRequestMessage", "Message =" + p_requestVO.getDecryptedMessage() + " MSISDN=" + p_requestVO.getRequestMSISDN());
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("parseChannelRequestMessage", " BTSL Exception while parsing Request Message :" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("parseChannelRequestMessage", "  Exception while parsing Request Message :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroUSSDParsers[parseChannelRequestMessage]",
                p_requestVO.getTransactionID(), "", "", "Exception :" + e.getMessage());
            throw new BTSLBaseException("ClaroUSSDParsers", "parseChannelRequestMessage", PretupsErrorCodesI.ERROR_EXCEPTION,e);
        }
    }

    public void generateChannelResponseMessage(RequestVO p_requestVO) {
        final String METHOD_NAME = "generateChannelResponseMessage";
        String contentType = p_requestVO.getReqContentType();
        if (_log.isDebugEnabled()) {
            _log.debug("generateChannelResponseMessage", "Transfer ID=" + p_requestVO.getRequestID() + " contentType: " + contentType);
        }
        try {
            if (contentType != null && (p_requestVO.getReqContentType().indexOf("xml") != -1 || p_requestVO.getReqContentType().indexOf("XML") != -1)) {
                // Set the Sender Return Message
                if (p_requestVO.getActionValue() == -1) {
                    actionChannelParser(p_requestVO);
                }
                generateChannelResponse(p_requestVO.getActionValue(), p_requestVO);
            } else if (contentType != null && (p_requestVO.getReqContentType().indexOf("plain") != -1 || p_requestVO.getReqContentType().indexOf("PLAIN") != -1) && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PLAIN_RES_PARSE_REQUIRED))).booleanValue()) {
                // Set the Sender Return Message
                if (p_requestVO.getActionValue() == -1) {
                    actionChannelParser(p_requestVO);
                }
                generateChannelPlainResponse(p_requestVO.getActionValue(), p_requestVO);

            } else {
                String message = null;
                if (!BTSLUtil.isNullString(p_requestVO.getSenderReturnMessage())) {
                    message = p_requestVO.getSenderReturnMessage();
                } else {
                    message = BTSLUtil.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
                }
                int index = 0;
                index = message.indexOf(":");
                if (index > -1) {
                    index = message.indexOf(":", index++);
                }

                if (index > -1) {
                    message = message.substring(index, message.length());
                }

                p_requestVO.setSenderReturnMessage(message);
            }
        } catch (Exception e) {
            _log.error("generateChannelResponseMessage", "  Exception while generating Response Message :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroUSSDParsers[generateChannelResponseMessage]",
                p_requestVO.getTransactionID(), "", "", "Exception getting message :" + e.getMessage());
            try {
                // XMLAPIParser.generateFailureResponse(p_requestVO);
                ExtAPIXMLStringParser.generateFailureResponse(p_requestVO);
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroUSSDParsers[generateChannelResponseMessage]",
                    p_requestVO.getTransactionID(), "", "", "Exception getting default message :" + ex.getMessage());
                p_requestVO
                    .setSenderReturnMessage("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND><TYPE></TYPE><TXNSTATUS>" + PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT + "</TXNSTATUS></COMMAND>");
            }
        }
    }

    /**
     * Method to parse the resquest based on action (Keyword)
     * 
     * @param action
     * @param p_requestVO
     * @throws Exception
     */
    public void parseRequest(int action, RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("parseRequest", "Entered Request ID=" + p_requestVO.getRequestID() + " action=" + action);
        }
        switch (action) {
            case ACTION_ACCOUNT_INFO:
                {
                    ExtAPIXMLStringParser.parseGetAccountInfoRequest(p_requestVO);
                    break;
                }
            case CREDIT_TRANSFER:
                {
                    ExtAPIXMLStringParser.parseCreditTransferRequest(p_requestVO);
                    break;
                }
            case CHANGE_PIN:
                {
                    ExtAPIXMLStringParser.parseChangePinRequest(p_requestVO);
                    break;
                }
            case NOTIFICATION_LANGUAGE:
                {
                    ExtAPIXMLStringParser.parseNotificationLanguageRequest(p_requestVO);
                    break;
                }
            case HISTORY_MESSAGE:
                {
                    ExtAPIXMLStringParser.parseHistoryMessageRequest(p_requestVO);
                    break;
                }
            case CREDIT_RECHARGE:
                {
                    ExtAPIXMLStringParser.parseCreditRechargeRequest(p_requestVO);
                    break;
                }
            case SUBSCRIBER_REGISTRATION:
                {
                    USSDP2PXMLStringParser.parseSubscriberRegistrationRequest(p_requestVO);
                    break;
                }
            case SUBSCRIBER_DEREGISTRATION:
                {
                    USSDP2PXMLStringParser.parseSubscriberDeRegistrationRequest(p_requestVO);
                    break;
                }
            case P2P_SERVICE_SUSPEND:
                {
                    USSDP2PXMLStringParser.parseP2PServiceSuspendRequest(p_requestVO);
                    break;
                }
            case P2P_SERVICE_RESUME:
                {
                    USSDP2PXMLStringParser.parseP2PServiceResumeRequest(p_requestVO);
                    break;
                }
            case ADD_BUDDY:
                {
                    USSDP2PXMLStringParser.parseAddBuddyRequest(p_requestVO);
                    break;
                }

            case DELETE_BUDDY:
                {
                    USSDP2PXMLStringParser.parseDeleteBuddyRequest(p_requestVO);
                    break;
                }
            // added by harsh on 09Aug12
            case DELETE_MULTLIST:
                {
                    USSDP2PXMLStringParser.parseDelMultCreditListRequest(p_requestVO);
                    break;
                }
            case LIST_BUDDY:
                {
                    USSDP2PXMLStringParser.parseListBuddyRequest(p_requestVO);
                    break;
                }
            // added for Last Transfer Status(P2P) 03/05/07
            case LAST_TRANSFER_STATUS:
                {
                    USSDP2PXMLStringParser.parseLastTransferStatus(p_requestVO);
                    break;
                }
            case SELF_BAR:
                {
                    USSDP2PXMLStringParser.parseSelfBarRequest(p_requestVO);
                    break;
                }
            case ACTION_REGISTER_SID: // added for C2S Last N transfer
                {
                    USSDC2SXMLStringParser.parseChannelRegistrationRequest(p_requestVO);
                    break;
                }
            case ACTION_DELETE_SID_REQ: // added for SID deletion
                {
                    USSDC2SXMLStringParser.parseDeleteSIDRequest(p_requestVO);
                    break;
                }
            case ACTION_ENQUIRY_SID_REQ: // added for SID enquiry
                {
                    USSDC2SXMLStringParser.parseEnquirySIDRequest(p_requestVO);
                    break;
                }
            case ACTION_P2P_CRIT_TRANS:
                {
                    USSDP2PXMLStringParser.parseP2PCRITRequest(p_requestVO);
                    break;
                }
            case P2P_GIVE_ME_BALANCE:
                {
                    USSDP2PXMLStringParser.parseGiveMeBalanceRequest(p_requestVO);
                    break;
                }
            case P2P_LEND_ME_BALANCE:
                {
                    ExtAPIXMLStringParser.parseLendMeBalanceRequest(p_requestVO);
                    break;
                }
            case ACTION_MULT_CDT_TXR_LIST_AMD:
                {
                    USSDP2PXMLStringParser.parseP2PMCDAddModifyDeleteRequest(p_requestVO);
                    break;
                }
            case ACTION_MULT_CDT_TXR_LIST_VIEW:
                {
                    USSDP2PXMLStringParser.parseP2PMCDListViewRequest(p_requestVO);
                    break;
                }
            case ACTION_MULT_CDT_TXR_LIST_REQUEST:
                {
                    USSDP2PXMLStringParser.parseP2PMCDListCreditRequest(p_requestVO);
                    break;
                }
            case SCHEDULE_CREDIT_TRANSFER:
                {
                    USSDC2SXMLStringParser.parseScheduleCreditTransferRequest(p_requestVO);
                    break;
                }
            default:
      	     	 if(_log.isDebugEnabled()){
      	     		_log.debug("Default Value " ,action);
      	     	 }
        }
    }

    /**
     * Method to parse the channel resquest based on action (Keyword)
     * 
     * @param action
     * @param p_requestVO
     * @throws Exception
     */
    public void parseChannelRequest(int action, RequestVO p_requestVO) throws Exception {
    	final String METHOD_NAME = "parseChannelRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered Request ID=" + p_requestVO.getRequestID() + " action=" + action);
        }

        // Set Filtered MSISDN, set _requestMSISDN
        // Set message Format , set in decrypted message

        switch (action) {
            case ACTION_CHNL_CREDIT_TRANSFER:
                {
                    ExtAPIXMLStringParser.parseChannelCreditTransferRequest(p_requestVO);
                    break;
                }
            case ACTION_CHNL_CHANGE_PIN:
                {
                    ExtAPIXMLStringParser.parseChannelChangePinRequest(p_requestVO);
                    break;
                }
            case ACTION_CHNL_NOTIFICATION_LANGUAGE:
                {
                    ExtAPIXMLStringParser.parseChannelNotificationLanguageRequest(p_requestVO);
                    break;
                }
            case ACTION_CHNL_TRANSFER_MESSAGE:
                {
                    ExtAPIXMLStringParser.parseChannelTransferRequest(p_requestVO, ACTION_CHNL_TRANSFER_MESSAGE);
                    break;
                }
            case ACTION_CHNL_WITHDRAW_MESSAGE:
                {
                    ExtAPIXMLStringParser.parseChannelTransferRequest(p_requestVO, ACTION_CHNL_WITHDRAW_MESSAGE);
                    break;
                }
            case ACTION_CHNL_RETURN_MESSAGE:
                {
                    ExtAPIXMLStringParser.parseChannelTransferRequest(p_requestVO, ACTION_CHNL_RETURN_MESSAGE);
                    break;
                }

            case ACTION_CHNL_POSTPAID_BILLPAYMENT:
                {
                    ExtAPIXMLStringParser.parseChannelPostPaidBillPaymentRequest(p_requestVO);
                    break;
                }
            // added for Balance Enquiry 03/05/07
            case ACTION_CHNL_BALANCE_ENQUIRY:
                {
                    USSDC2SXMLStringParser.parseChannelBalanceEnquiry(p_requestVO);
                    break;
                }
            // added for Daily Status Report 03/05/07
            case ACTION_CHNL_DAILY_STATUS_REPORT:
                {
                    USSDC2SXMLStringParser.parseChannelDailyStatusReport(p_requestVO);
                    break;
                }
            // added for Last Transfer Status(RP2P) 03/05/07
            case ACTION_CHNL_LAST_TRANSFER_STATUS:
                {
                    USSDC2SXMLStringParser.parseChannelLastTransferStatus(p_requestVO);
                    break;
                }
            case ACTION_CHNL_EVD_REQUEST:
                {
                    USSDC2SXMLStringParser.parseChannelEVDRequest(p_requestVO);
                    break;
                }
            case ACTION_MULTIPLE_VOUCHER_DISTRIBUTION:
                {
                    USSDC2SXMLStringParser.parseMultipleVoucherDistributionRequest(p_requestVO);
                    break;
                }

            case ACTION_UTILITY_BILL_PAYMENT:
                {
                    USSDC2SXMLStringParser.parseUtilityBillPaymentRequest(p_requestVO);
                    break;
                }
            case ACTION_CHNL_GIFT_RECHARGE_USSD:
                {
                    USSDC2SXMLStringParser.parseGiftRechargeRequest(p_requestVO);
                    break;
                }
            case ACTION_CHNL_CREDIT_TRANSFER_CDMA: // added for CDMA recharge
                {
                    USSDC2SXMLStringParser.parseChannelCreditTransferCDMARequest(p_requestVO);
                    break;
                }
            case ACTION_CHNL_CREDIT_TRANSFER_PSTN: // added for PSTN recharge
                {
                    USSDC2SXMLStringParser.parseChannelCreditTransferPSTNRequest(p_requestVO);
                    break;
                }
            case ACTION_CHNL_CREDIT_TRANSFER_INTR: // added for INTERNET
                // recharge
                {
                    USSDC2SXMLStringParser.parseChannelCreditTransferINTRRequest(p_requestVO);
                    break;
                }
            case ACTION_CHNL_ORDER_CREDIT: // added for Order Credit
                {
                    USSDC2SXMLStringParser.parseChannelOrderCredit(p_requestVO);
                    break;
                }
            case ACTION_CHNL_ORDER_LINE: // added for Order Line
                {
                    USSDC2SXMLStringParser.parseChannelOrderLine(p_requestVO);
                    break;
                }
            case ACTION_CHNL_BARRING: // added for Barring
                {
                    USSDC2SXMLStringParser.parseChannelBarring(p_requestVO);
                    break;
                }
            case ACTION_CHNL_IAT_ROAM_RECHARGE: // added for Roam recharge
                {
                    USSDC2SXMLStringParser.parseIATRoamRecharge(p_requestVO);
                    break;
                }
            case ACTION_CHNL_IAT_INTERNATIONAL_RECHARGE: // added for
                // international
                // recharge
                {
                    USSDC2SXMLStringParser.parseIATInternationalRecharge(p_requestVO);
                    break;
                }
            case ACTION_CHNL_C2S_LAST_XTRANSFER: // added for C2S Last N
                // transfer
                {
                    USSDC2SXMLStringParser.parseChannelLastXTransferStatus(p_requestVO);
                    break;
                }
            case ACTION_CHNL_CUST_LAST_XTRANSFER: // added for C2S Last N
                // transfer
                {
                    USSDC2SXMLStringParser.parseChannelXEnquiryStatus(p_requestVO);
                    break;
                }
            case ACTION_EXT_MVD_DWNLD_RQST: // to download MVD through USSD ..
                // done
                // by ashishT
                {
                    USSDC2SXMLStringParser.parseMVDDownloadRequest(p_requestVO);
                    break;
                }
            case ACTION_C2S_TRANS_ENQ:
                {
                    USSDC2SXMLStringParser.parseC2STransferEnqRequest(p_requestVO);// for
                    // channel
                    // transaction
                    // enquiry
                    // request
                    // date/msisdn
                    // by
                    // RahulD
                    break;
                }
            case ACTION_EXT_USER_CREATION: // change done for ussd user creation
                {
                    USSDC2SXMLStringParser.parseUserCreationRequest(p_requestVO);
                    break;
                }
            case ACTION_ENQUIRY_TXNIDEXTCODEDATE:
                {
                    ExtAPIXMLStringParser.parseEnquiryTxnIDExtCodeDateRequest(p_requestVO);
                    break;
                }
            case ACTION_REGISTER_SID: // added for SID registration and
                // modification
                {
                    USSDC2SXMLStringParser.parseChannelRegistrationRequest(p_requestVO);
                    break;
                }
            case ACTION_DELETE_SID_REQ: // added for SID deletion
                {
                    USSDC2SXMLStringParser.parseDeleteSIDRequest(p_requestVO);
                    break;
                }
            case ACTION_ENQUIRY_SID_REQ: // added for SID deletion
                {
                    USSDC2SXMLStringParser.parseEnquirySIDRequest(p_requestVO);
                    break;
                }

            case ACTION_CRBT_REGISTRATION: // added for CRBT Registration by
                // shashank
                {
                    USSDC2SXMLStringParser.parseCRBTRegistrationRequest(p_requestVO);
                    break;
                }

            case ACTION_CRBT_SONG_SELECTION: // added for CRBT Song Selection by
                // shashank
                {
                    USSDC2SXMLStringParser.parseCRBTSongSelectionRequest(p_requestVO);
                    break;
                }
            case ACTION_ELECTRONIC_VOUCHER_RECHARGE:// added for EVR by Harpreet
                {
                    USSDC2SXMLStringParser.parseElecronicVoucherRechargeRequest(p_requestVO);
                    break;
                }
            case SUSPEND_RESUME_CUSR: // Suspend Resume User through USSD
                {
                    USSDC2SXMLStringParser.parseChannelUserSuspendResume(p_requestVO);
                    break;
                }
            case ACTION_C2S_RPT_LAST_XTRANSFER:
                {
                    USSDC2SXMLStringParser.parseC2SLastXTransferStatus(p_requestVO);
                    break;
                }
            // vastrix added by hitesh
            case ACTION_VAS_RC_REQUEST:
                {
                    USSDC2SXMLStringParser.parseChannelVasTransferRequest(p_requestVO);
                    break;
                }
            case ACTION_PVAS_RC_REQUEST:
                {
                    USSDC2SXMLStringParser.parseChannelPrVasTransferRequest(p_requestVO);
                    break;
                }
            case ACTION_CRM_USER_AUTH_XML:// added by shashank for channel user
                // authentication
                {
                    USSDC2SXMLStringParser.parseChannelUserAuthRequest(p_requestVO);
                    break;
                }
            case ACTION_USSD_SIM_ACT_REQ: // change for SIM activation by sachin
                // date 1/06/2011
                {
                    USSDC2SXMLStringParser.parseSIMActivationRequest(p_requestVO);
                    break;
                }

            // added by arvinder/
            case ACTION_CHNL_HLPDESK_REQUEST:
                {
                    USSDC2SXMLStringParser.parseChannelHelpDeskRequest(p_requestVO);
                    break;
                }
            case ACTION_COL_ENQ:
                {
                    ExtAPIXMLStringParser.parseChannelExtCollectionEnquiryRequest(p_requestVO);
                    break;
                }
            case ACTION_COL_BILLPAYMENT:
                {
                    ExtAPIXMLStringParser.parseChannelExtCollectionBillPaymentRequest(p_requestVO);
                    break;
                }
            case ACTION_DTH:
                {
                    ExtAPIXMLStringParser.parseC2STransferRequest(p_requestVO, ACTION_DTH);
                    break;
                }
            case ACTION_DC:
                {
                    ExtAPIXMLStringParser.parseC2STransferRequest(p_requestVO, ACTION_DC);
                    break;
                }
            case ACTION_BPB:
                {
                    ExtAPIXMLStringParser.parseC2STransferRequest(p_requestVO, ACTION_BPB);
                    break;
                }
            case ACTION_PIN:
                {
                    ExtAPIXMLStringParser.parseC2STransferRequest(p_requestVO, ACTION_PIN);
                    break;
                }
            case ACTION_PMD:
                {
                    ExtAPIXMLStringParser.parseC2STransferRequest(p_requestVO, ACTION_PMD);
                    break;
                }
            case ACTION_FLRC:
                {
                    ExtAPIXMLStringParser.parseC2STransferRequest(p_requestVO, ACTION_FLRC);
                    break;
                }
            case ACTION_C2S_POSTPAID_REVERSAL:
                {
                    ExtAPIXMLStringParser.parseC2SPostPaidReversalRequest(p_requestVO);
                    break;
                }
            default:
      	     	 if(_log.isDebugEnabled()){
      	     		_log.debug("Default Value " ,action);
      	     	 }

        }
    }

    public void parseChannelRequestMessage(Connection p_con, RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "parseChannelRequestMessage";
        String contentType = p_requestVO.getReqContentType();
        if (_log.isDebugEnabled()) {
            _log.debug("parseChannelRequestMessage", "Transfer ID=" + p_requestVO.getRequestID() + " contentType: " + contentType);
        }
        try {
            if (contentType != null && (contentType.indexOf("xml") != -1 || contentType.indexOf("XML") != -1)) {
                // Forward to XML parsing
                // Set Filtered MSISDN, set _requestMSISDN
                // Set message Format , set in decrypted message
                // XMLAPIParser.actionChannelParser(p_requestVO);
                int action = actionChannelParser(p_requestVO);
                parseChannelRequest(action, p_requestVO);
                updateUserInfo(p_con, p_requestVO);
            } else {
                // Forward to plain message parsing
                // Set message Format , set in decrypted message
                p_requestVO.setDecryptedMessage(p_requestVO.getRequestMessage());
            }
            if (_log.isDebugEnabled()) {
                _log.debug("parseChannelRequestMessage", "Message =" + p_requestVO.getDecryptedMessage() + " MSISDN=" + p_requestVO.getRequestMSISDN());
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("parseChannelRequestMessage", " BTSL Exception while parsing Request Message :" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("parseChannelRequestMessage", "  Exception while parsing Request Message :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroUSSDParsers[parseChannelRequestMessage]",
                p_requestVO.getTransactionID(), "", "", "Exception :" + e.getMessage());
            throw new BTSLBaseException("ClaroUSSDParsers", "parseChannelRequestMessage", PretupsErrorCodesI.ERROR_EXCEPTION,e);
        }
    }

    /**
     * Method to parse the resquest based on action (Keyword)
     * 
     * @param action
     * @param p_requestVO
     * @throws Exception
     */
    public void parsePlainRequest(int action, RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("parseRequest", "Entered Request ID=" + p_requestVO.getRequestID() + " action=" + action);
        }

        switch (action) {
            case CREDIT_TRANSFER:
                {
                    ClaroUSSDStringParser.parseCreditTransferRequest(p_requestVO);
                    break;
                }
            case P2P_GIVE_ME_BALANCE:
                {
                    ClaroUSSDStringParser.parseP2PGiveMeBalanceRequest(p_requestVO);
                    break;
                }
            default:
      	     	 if(_log.isDebugEnabled()){
      	     		_log.debug("Default Value " ,action);
      	     	 }

        }
    }

    /**
     * Method to generate Response of P2P requests
     * 
     * @param p_requestVO
     * @throws Exception
     */
    public static void generatePlainResponse(int action, RequestVO p_requestVO) throws Exception {
    	final String METHOD_NAME = "generatePlainResponse";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered Request ID=" + p_requestVO.getRequestID() + " action=" + action);
        }
        String messageCode = p_requestVO.getMessageCode();
        if ((!BTSLUtil.isNullString(messageCode)) && (!p_requestVO.isSuccessTxn()) && messageCode.indexOf("_") != -1) {
            messageCode = messageCode.substring(0, messageCode.indexOf("_"));
            p_requestVO.setMessageCode(messageCode);
        }
        switch (action) {
            case CREDIT_TRANSFER:
                {
                    ClaroUSSDStringParser.generateCreditTransferResponse(p_requestVO);
                    break;
                }
            case P2P_GIVE_ME_BALANCE:
                {
                    ClaroUSSDStringParser.generateP2PGiveMeBalanceResponse(p_requestVO);
                    break;
                }
            default:
      	     	 if(_log.isDebugEnabled()){
      	     		_log.debug("Default Value " ,action);
      	     	 }
        }
    }

    /**
     * Method to parse the channel resquest based on action (Keyword)
     * 
     * @param action
     * @param p_requestVO
     * @throws Exception
     */
    public void parseChannelPlainRequest(int action, RequestVO p_requestVO) throws Exception {
    	final String METHOD_NAME = "parseChannelPlainRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered Request ID=" + p_requestVO.getRequestID() + " action=" + action);
        }

        switch (action) {
            case ACTION_CHNL_CREDIT_TRANSFER:
                {
                    ClaroUSSDStringParser.parseC2STransferRequest(p_requestVO, ACTION_CHNL_CREDIT_TRANSFER);
                    break;
                }
            case ACTION_CHNL_CHANGE_PIN:
                {
                    ClaroUSSDStringParser.parseChannelChangePinRequest(p_requestVO);
                    break;
                }
            case ACTION_CHNL_NOTIFICATION_LANGUAGE:
                {
                    ClaroUSSDStringParser.parseChannelNotificationLanguageRequest(p_requestVO);
                    break;
                }
            case ACTION_CHNL_TRANSFER_MESSAGE:
                {
                    ClaroUSSDStringParser.parseChannelTransferRequest(p_requestVO, ACTION_CHNL_TRANSFER_MESSAGE);
                    break;
                }
            case ACTION_CHNL_WITHDRAW_MESSAGE:
                {
                    ClaroUSSDStringParser.parseChannelTransferRequest(p_requestVO, ACTION_CHNL_WITHDRAW_MESSAGE);
                    break;
                }
            case ACTION_CHNL_RETURN_MESSAGE:
                {
                    ClaroUSSDStringParser.parseChannelTransferRequest(p_requestVO, ACTION_CHNL_RETURN_MESSAGE);
                    break;
                }
            // added for Balance Enquiry 03/05/07
            case ACTION_CHNL_BALANCE_ENQUIRY:
                {
                    ClaroUSSDStringParser.parseChannelBalanceEnquiry(p_requestVO);
                    break;
                }
            // added for Daily Status Report 03/05/07
            case ACTION_CHNL_DAILY_STATUS_REPORT:
                {
                    ClaroUSSDStringParser.parseChannelDailyStatusReport(p_requestVO);
                    break;
                }
            // added for Last Transfer Status(RP2P) 03/05/07
            case ACTION_CHNL_LAST_TRANSFER_STATUS:
                {
                    ClaroUSSDStringParser.parseChannelLastTransferStatus(p_requestVO);
                    break;
                }
            case ACTION_CHNL_C2S_LAST_XTRANSFER: // added for C2S Last N
                // transfer
                {
                    ClaroUSSDStringParser.parseChannelLastXTransferStatus(p_requestVO);
                    break;
                }
            case ACTION_CHNL_CUST_LAST_XTRANSFER: // added for C2S Last N
                // transfer
                {
                    ClaroUSSDStringParser.parseChannelXEnquiryStatus(p_requestVO);
                    break;
                }
            case ACTION_CHNL_HLPDESK_REQUEST: // added for Help Desk Number
                // Plain
                // Request
                {
                    ClaroUSSDStringParser.parseChannelHelpDeskRequest(p_requestVO);
                    break;
                }
            case ACTION_CHNL_POSTPAID_BILLPAYMENT: // addedd for PPB from Flares
                {
                    ClaroUSSDStringParser.parseChannelPostPaidBillPaymentRequest(p_requestVO);
                    break;
                }
            case ACTION_COL_ENQ: // added for Collection Enquiry
                {
                    ClaroUSSDStringParser.parseChannelCollectionEnquiryRequest(p_requestVO);
                    break;
                }

            case ACTION_COL_BILLPAYMENT: // added for Collection Enquiry
                {
                    ClaroUSSDStringParser.parseChannelCollectionBillPaymentRequest(p_requestVO);
                    break;
                }
            case ACTION_DTH: // added for DTH
                {
                    ClaroUSSDStringParser.parseC2STransferRequest(p_requestVO, ACTION_DTH);
                    break;
                }
            case ACTION_DC:
                {
                    ClaroUSSDStringParser.parseC2STransferRequest(p_requestVO, ACTION_DC);
                    break;
                }
            case ACTION_BPB:
                {
                    ClaroUSSDStringParser.parseC2STransferRequest(p_requestVO, ACTION_BPB);
                    break;
                }
            case ACTION_PIN:
                {
                    ClaroUSSDStringParser.parseC2STransferRequest(p_requestVO, ACTION_PIN);
                    break;
                }
            case ACTION_PMD:
                {
                    ClaroUSSDStringParser.parseC2STransferRequest(p_requestVO, ACTION_PMD);
                    break;
                }
            case ACTION_FLRC:
                {
                    ClaroUSSDStringParser.parseC2STransferRequest(p_requestVO, ACTION_FLRC);
                    break;
                }
            case ACTION_C2S_POSTPAID_REVERSAL: // added for Collection Enquiry
                {
                    ClaroUSSDStringParser.parseC2SPostPaidReversalRequest(p_requestVO);
                    break;
                }
            case ACTION_CHNL_IAT_ROAM_RECHARGE: // added for IAT Recharge
            {
                ClaroUSSDStringParser.parseIATRoamRecharge(p_requestVO);
                break;
            }
        case ACTION_CRM_USER_AUTH_XML://Changes for Tigo-GT
        {
        	ClaroUSSDStringParser.parseChannelUserAuthRequest(p_requestVO);
        	break;
        }
        case ACTION_C2S_TXNID_STAT:
        {
                ClaroUSSDStringParser.parseC2SStatusRequest(p_requestVO);
                break;
        }
        case ACTION_LIST_RET:
                {
                        ClaroUSSDStringParser.parseRetListRequest(p_requestVO);
                        break;
                }
       
                
        case ACTION_OLORECHARGE_REQUEST: // added for Collection Enquiry
    	{
    		ClaroUSSDStringParser.parseOLOTransferRequest(p_requestVO);
    		break;	
    	}
        default:
  	     	 if(_log.isDebugEnabled()){
  	     		_log.debug("Default Value " ,action);
  	     	 }
        }
        
    	
        // load info acc to msisdn set active user id and swap requested msisdn,
        // method in ChannelBL.java
    }

    public static void generateChannelPlainResponse(int action, RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("generateChannelResponse", "Entered Request ID=" + p_requestVO.getRequestID() + " action=" + action);
        }

        String messageCode = p_requestVO.getMessageCode();
        if ((!BTSLUtil.isNullString(messageCode)) && (!p_requestVO.isSuccessTxn()) && messageCode.indexOf("_") != -1) {
            messageCode = messageCode.substring(0, messageCode.indexOf("_"));
            p_requestVO.setMessageCode(messageCode);
        }
        switch (action) {
            case ACTION_CHNL_CREDIT_TRANSFER:
                {
                    ClaroUSSDStringParser.generateC2STransferResponse(p_requestVO, ACTION_CHNL_CREDIT_TRANSFER);
                    break;
                }
            case ACTION_CHNL_CHANGE_PIN:
                {
                    ClaroUSSDStringParser.generateChannelChangePinResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_NOTIFICATION_LANGUAGE:
                {
                    ClaroUSSDStringParser.generateChannelNotificationLanguageResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_TRANSFER_MESSAGE:
                {
                    ClaroUSSDStringParser.generateChannelTransferResponse(p_requestVO, ACTION_CHNL_TRANSFER_MESSAGE);
                    break;
                }
            case ACTION_CHNL_WITHDRAW_MESSAGE:
                {
                    ClaroUSSDStringParser.generateChannelTransferResponse(p_requestVO, ACTION_CHNL_WITHDRAW_MESSAGE);
                    break;
                }
            case ACTION_CHNL_RETURN_MESSAGE:
                {
                    ClaroUSSDStringParser.generateChannelTransferResponse(p_requestVO, ACTION_CHNL_RETURN_MESSAGE);
                    break;
                }
            case ACTION_CHNL_EXT_RECH_STATUS:
                {
                    ClaroUSSDStringParser.generateChannelExtRechargeStatusResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_EXT_CREDIT_TRANSFER:
                {
                    ClaroUSSDStringParser.generateChannelExtRechargeStatusResponse(p_requestVO);
                    break;
                }
            // added for Balance Enquiry 03/05/07
            case ACTION_CHNL_BALANCE_ENQUIRY:
                {
                    ClaroUSSDStringParser.generateChannelBalanceEnquiryResponse(p_requestVO);
                    break;
                }
            // added for Daily Status Report 03/05/07
            case ACTION_CHNL_DAILY_STATUS_REPORT:
                {
                    ClaroUSSDStringParser.generateChannelDailyStatusReportResponse(p_requestVO);
                    break;
                }
            // added for Last Transfer Status(RP2P) 03/05/07
            case ACTION_CHNL_LAST_TRANSFER_STATUS:
                {
                    ClaroUSSDStringParser.generateChannelLastTransferStatusResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_C2S_LAST_XTRANSFER: // last X C2S transfer
                {
                    ClaroUSSDStringParser.generateChannelLastXTransferStatusResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_CUST_LAST_XTRANSFER: // last X C2S transfer
                {
                    ClaroUSSDStringParser.generateChannelXEnquiryStatusResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_HLPDESK_REQUEST: // Help Desk Plain Response
                {
                    ClaroUSSDStringParser.generateChannelHelpDeskResponse(p_requestVO);
                    break;
                }

            case ACTION_CHNL_POSTPAID_BILLPAYMENT: // PPB Response Plain
                {
                    ClaroUSSDStringParser.generateChannelPostPaidBillPaymentResponse(p_requestVO);
                    break;
                }

            case ACTION_COL_ENQ: // Collection Enquiry
                {
                    ClaroUSSDStringParser.generateChannelCollectionEnquiryResponse(p_requestVO);
                    break;
                }
            case ACTION_COL_BILLPAYMENT: // Collection Bill Payment
                {
                    ClaroUSSDStringParser.generateChannelCollectionBillPaymentResponse(p_requestVO);
                    break;
                }
            case ACTION_DTH: // added for DTH
                {
                    ClaroUSSDStringParser.generateC2STransferResponse(p_requestVO, ACTION_DTH);
                    break;
                }
            case ACTION_DC:
                {
                    ClaroUSSDStringParser.generateC2STransferResponse(p_requestVO, ACTION_DC);
                    break;
                }
            case ACTION_BPB:
                {
                    ClaroUSSDStringParser.generateC2STransferResponse(p_requestVO, ACTION_BPB);
                    break;
                }
            case ACTION_PIN:
                {
                    ClaroUSSDStringParser.generateC2STransferResponse(p_requestVO, ACTION_PIN);
                    break;
                }
            case ACTION_PMD:
                {
                    ClaroUSSDStringParser.generateC2STransferResponse(p_requestVO, ACTION_PMD);
                    break;
                }
            case ACTION_FLRC:
                {
                    ClaroUSSDStringParser.generateC2STransferResponse(p_requestVO, ACTION_FLRC);
                    break;
                }
            case ACTION_CRM_USER_AUTH_XML: // User Authorization
                {
                    ClaroUSSDStringParser.generateUserAuthorizationResponse(p_requestVO);
                    break;
                }
            case ACTION_C2S_POSTPAID_REVERSAL: // Collection Bill Payment
                {
                    ClaroUSSDStringParser.generateC2SPostPaidReversalResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_IAT_ROAM_RECHARGE: //ROAM Recharge
                {
                ClaroUSSDStringParser.generateRoamRechargeResponse(p_requestVO);
                break;
                }
            case ACTION_C2S_TXNID_STAT:
                {
                 ClaroUSSDStringParser.generateC2SStatusResponse(p_requestVO);
                 break;
                }
                case ACTION_LIST_RET:
                {
                        ClaroUSSDStringParser.generateRetListResponse(p_requestVO);
                    break;
                }
                case ACTION_OLORECHARGE_REQUEST: 
        		{
        			ClaroUSSDStringParser.generateOLOTransferResponse(p_requestVO);
        			break;	
        		}
                default:
       	     	 if(_log.isDebugEnabled()){
       	     		_log.debug("Default Value " ,action);
       	     	 }
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
        if (_log.isDebugEnabled()) {
            _log.debug("parseOperatorRequestMessage", "Transfer ID=" + p_requestVO.getRequestID() + " contentType: " + contentType);
        }
        try {
            if (contentType != null && (contentType.indexOf("xml") != -1 || contentType.indexOf("XML") != -1)) {
                int action = actionChannelParser(p_requestVO);
                parseChannelRequest(action, p_requestVO);
            } else {
                p_requestVO.setDecryptedMessage(p_requestVO.getRequestMessage());
            }
            if (_log.isDebugEnabled()) {
                _log.debug("parseOperatorRequestMessage", "Message =" + p_requestVO.getDecryptedMessage() + " MSISDN=" + p_requestVO.getRequestMSISDN());
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("parseOperatorRequestMessage", " BTSL Exception while parsing Request Message :" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("parseChannelRequestMessage", "  Exception while parsing Request Message :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroUSSDParsers[parseOperatorRequestMessage]",
                p_requestVO.getTransactionID(), "", "", "Exception :" + e.getMessage());
            throw new BTSLBaseException("ClaroUSSDParsers", "parseOperatorRequestMessage", PretupsErrorCodesI.ERROR_EXCEPTION,e);
        }
    }
}
