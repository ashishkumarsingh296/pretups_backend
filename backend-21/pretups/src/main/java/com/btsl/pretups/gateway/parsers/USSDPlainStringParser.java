package com.btsl.pretups.gateway.parsers;

/*
 * @(#)USSDPlainStringParser.java
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
 * 
 * Parser class to handle USSD plain requests
 */

import java.sql.Connection;

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
import com.btsl.pretups.gateway.util.ParserUtility;
import com.btsl.pretups.gateway.util.USSDStringParser;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.util.BTSLUtil;

public class USSDPlainStringParser extends ParserUtility {
    public static final Log LOG = LogFactory.getLog(USSDPlainStringParser.class.getName());

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
            throw new BTSLBaseException("USSDParsers", "parseChannelRequestMessage", PretupsErrorCodesI.ERROR_EXCEPTION);
        }
    }

    public void generateChannelResponseMessage(RequestVO p_requestVO) {
        final String METHOD_NAME = "generateChannelResponseMessage";
        String contentType = p_requestVO.getReqContentType();
        if (LOG.isDebugEnabled()) {
            LOG.debug("generateChannelResponseMessage", "Transfer ID=" + p_requestVO.getRequestID() + " contentType: " + contentType);
        }
        try {
            if (contentType != null && (p_requestVO.getReqContentType().indexOf("text") != -1 || p_requestVO.getReqContentType().indexOf("TEXT") != -1)) {
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
                USSDStringParser.generateFailureResponse(p_requestVO);
            } catch (Exception ex) {
                LOG.errorTrace(METHOD_NAME, ex);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "USSDParsers[generateChannelResponseMessage]",
                    p_requestVO.getTransactionID(), "", "", "Exception getting default message :" + ex.getMessage());
                p_requestVO.setSenderReturnMessage("TYPE=&TXNSTATUS=" + PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
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
        if (LOG.isDebugEnabled()) {
            LOG.debug("parseChannelRequest", "Entered Request ID=" + p_requestVO.getRequestID() + " action=" + action);
        }

        switch (action) {
        case ACTION_DELETE_SID_REQ:{
        	 USSDStringParser.parseChannelPrivateRechargeDelRequest(p_requestVO);
             break;
        }
            case ACTION_CHNL_CREDIT_TRANSFER:
                {
                    USSDStringParser.parseChannelCreditTransferRequest(p_requestVO);
                    break;
                }
            case ACTION_CHNL_CHANGE_PIN:
                {
                    USSDStringParser.parseChannelChangePinRequest(p_requestVO);
                    break;
                }
            case ACTION_CHNL_NOTIFICATION_LANGUAGE:
                {
                    USSDStringParser.parseChannelNotificationLanguageRequest(p_requestVO);
                    break;
                }
            case ACTION_CHNL_TRANSFER_MESSAGE:
                {
                    USSDStringParser.parseChannelTransferRequest(p_requestVO, ACTION_CHNL_TRANSFER_MESSAGE);
                    break;
                }
            case ACTION_CHNL_WITHDRAW_MESSAGE:
                {
                    USSDStringParser.parseChannelTransferRequest(p_requestVO, ACTION_CHNL_WITHDRAW_MESSAGE);
                    break;
                }
            case ACTION_CHNL_RETURN_MESSAGE:
                {
                    USSDStringParser.parseChannelTransferRequest(p_requestVO, ACTION_CHNL_RETURN_MESSAGE);
                    break;
                }
            case ACTION_CHNL_BALANCE_ENQUIRY:
                {
                    USSDStringParser.parseChannelBalanceEnquiry(p_requestVO);
                    break;
                }
            case ACTION_CHNL_DAILY_STATUS_REPORT:
                {
                    USSDStringParser.parseChannelDailyStatusReport(p_requestVO);
                    break;
                }
            case ACTION_CHNL_LAST_TRANSFER_STATUS:
                {
                    USSDStringParser.parseChannelLastTransferStatus(p_requestVO);
                    break;
                }
            case ACTION_CHNL_C2S_LAST_XTRANSFER: // added for C2S Last N
                // transfer
                {
                    USSDStringParser.parseChannelLastXTransferStatus(p_requestVO);
                    break;
                }
            case ACTION_CHNL_CUST_LAST_XTRANSFER: // added for C2S Last N
                // transfer
                {
                    USSDStringParser.parseChannelXEnquiryStatus(p_requestVO);
                    break;
                }
            case ACTION_EXT_MVD_DWNLD_RQST: // to download MVD through USSD ..
                // done
                // by ashishT
                {
                    USSDStringParser.parseChannelMVDVoucherDownloadRequest(p_requestVO);
                    break;
                }
            case ACTION_EXT_USER_CREATION: // Ussd User Creation.
                {
                    USSDStringParser.parsePlainUserCreationRequest(p_requestVO);
                    break;
                }
            case ACTION_EXT_PPBENQ:
                {
                    USSDStringParser.parsePPBEnqRequest(p_requestVO);
                    break;
                }
            default:
     	     	 if(LOG.isDebugEnabled()){
     	     		LOG.debug("Default Value " ,action);
     	     	 }
        }
        // load info acc to msisdn set active user id and swap requested msisdn,
        // method in ChannelBL.java
    }

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
	        case ACTION_DELETE_SID_REQ:{
	        	USSDStringParser.generateChannelPrivateRechargeDelResponse(p_requestVO);
	        	break;
	        }
            case ACTION_CHNL_CREDIT_TRANSFER:
                {
                    USSDStringParser.generateChannelCreditTransferResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_CHANGE_PIN:
                {
                    USSDStringParser.generateChannelChangePinResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_NOTIFICATION_LANGUAGE:
                {
                    USSDStringParser.generateChannelNotificationLanguageResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_TRANSFER_MESSAGE:
                {
                    USSDStringParser.generateChannelTransferResponse(p_requestVO, ACTION_CHNL_TRANSFER_MESSAGE);
                    break;
                }
            case ACTION_CHNL_WITHDRAW_MESSAGE:
                {
                    USSDStringParser.generateChannelTransferResponse(p_requestVO, ACTION_CHNL_WITHDRAW_MESSAGE);
                    break;
                }
            case ACTION_CHNL_RETURN_MESSAGE:
                {
                    USSDStringParser.generateChannelTransferResponse(p_requestVO, ACTION_CHNL_RETURN_MESSAGE);
                    break;
                }
            case ACTION_CHNL_BALANCE_ENQUIRY:
                {
                    USSDStringParser.generateChannelBalanceEnquiryResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_DAILY_STATUS_REPORT:
                {
                    USSDStringParser.generateChannelDailyStatusReportResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_LAST_TRANSFER_STATUS:
                {
                    USSDStringParser.generateChannelLastTransferStatusResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_C2S_LAST_XTRANSFER: // last X C2S transfer
                {
                    USSDStringParser.generateChannelLastXTransferStatusResponse(p_requestVO);
                    // manisha
                    break;
                }
            case ACTION_CHNL_CUST_LAST_XTRANSFER: // last X C2S transfer
                {
                    USSDStringParser.generateChannelXEnquiryStatusResponse(p_requestVO);
                    // manisha
                    break;
                }
            case ACTION_EXT_USER_CREATION: // Ussd user creation response.
                {
                    USSDStringParser.generatePlainUserCreationResponse(p_requestVO);
                    break;
                }
            case ACTION_EXT_PPBENQ:
                {
                    USSDStringParser.generateChannelPPBEnqResponse(p_requestVO);

                    // manisha
                    break;
                }
            default:
     	     	 if(LOG.isDebugEnabled()){
     	     		LOG.debug("Default Value " ,action);
     	     	 }
        }
    }

    /**
     * Method to find the action (Keyword) in Channel requests
     * 
     * @param p_requestVO
     * @return
     * @throws BTSLBaseException
     */
    /*
     * public static int actionChannelParser(RequestVO p_requestVO) throws
     * BTSLBaseException
     * {
     * String requestStr=p_requestVO.getRequestMessage();
     * if(_log.isDebugEnabled())_log.debug("actionChannelParser",
     * "Entered p_requestVO="
     * +p_requestVO.toString()+" requestStr: "+requestStr);
     * int action=-1;
     * try
     * {
     * int index=requestStr.indexOf("TYPE=");
     * String
     * type=requestStr.substring(index+"TYPE=".length(),requestStr.indexOf
     * ("&",index));
     * if(type.equals("CACINFREQ"))
     * {
     * action=0;
     * }
     * else if(type.equals("RCTRFREQ"))
     * {
     * action=1;
     * }
     * else if(type.equals("RCPNREQ"))
     * {
     * action=2;
     * }
     * else if(type.equals("RCNLANGREQ"))
     * {
     * action=3;
     * }
     * else if(type.equals("CCHISREQ"))
     * {
     * action=4;
     * }
     * else if(type.equals("TRFREQ"))
     * {
     * action=5;
     * }
     * else if(type.equals("RETREQ"))
     * {
     * action=6;
     * }
     * else if(type.equals("WDTHREQ"))
     * {
     * action=7;
     * }
     * else if(type.equals("PPBTRFREQ"))
     * {
     * action=8;
     * }
     * else if(type.equals("O2CINREQ"))
     * {
     * action=ACTION_CHNL_O2C_INITIATE;
     * }
     * else if(type.equals("O2CINTREQ"))
     * {
     * action=ACTION_CHNL_O2C_INITIATE_TRFR;
     * }
     * else if(type.equals("O2CRETREQ"))
     * {
     * action=ACTION_CHNL_O2C_RETURN;
     * }
     * else if(type.equals("O2CWDREQ"))
     * {
     * action=ACTION_CHNL_O2C_WITHDRAW;
     * }
     * else if(type.equals("EXRCSTATREQ"))
     * {
     * action=ACTION_CHNL_EXT_RECH_STATUS;
     * }
     * else if(type.equals("EXRCTRFREQ"))
     * {
     * action=ACTION_CHNL_EXT_CREDIT_TRANSFER;
     * }
     * else if(type.equals("BALREQ")) //added for Balance Enquiry 03/05/07
     * {
     * action=ACTION_CHNL_BALANCE_ENQUIRY;
     * }
     * else if(type.equals("DSRREQ")) //added for Daily Status Report 03/05/07
     * {
     * action=ACTION_CHNL_DAILY_STATUS_REPORT;
     * }
     * else if(type.equals("LTSREQ")) //added for Last Transfer Status(RP2P)
     * 03/05/07
     * {
     * action=ACTION_CHNL_LAST_TRANSFER_STATUS;
     * }
     * else if(type.equals("EVDREQ"))
     * {
     * action=ACTION_CHNL_EVD_REQUEST;
     * }
     * else if(type.equals("MVDREQ"))
     * {
     * action=ACTION_MULTIPLE_VOUCHER_DISTRIBUTION;
     * }
     * else if(type.equals("UBPREQ"))
     * {
     * action=ACTION_UTILITY_BILL_PAYMENT;
     * }
     * else if(type.equals("EXPPBREQ"))//added for C2S Bill payment
     * {
     * action=ACTION_CHNL_EXT_TRANSFER_BILL_PAYMENT;
     * }
     * else if(type.equals("EXTSYSENQREQ"))//added for c2s Enquiry
     * {
     * action=ACTION_CHNL_EXT_ENQUIRY_REQUEST;
     * }
     * else if(type.equals("EXPPBSTATREQ"))//added for c2s Enquiry
     * {
     * action=ACTION_CHNL_EXT_POST_RECHARGE_STATUS;
     * }
     * else if(type.equals("CEXRCTRFREQ")) //added for common recharge
     * {
     * action=ACTION_CHNL_EXT_COMMON_RECHARGE;
     * }
     * else if(type.equals("GFTRCREQ"))//added for Gift Recharge through USSD
     * {
     * action=ACTION_CHNL_GIFT_RECHARGE_USSD;
     * }
     * else if(type.equals("EXGFTRCREQ"))//added for Gift Recharge through XML
     * API
     * {
     * action=ACTION_CHNL_GIFT_RECHARGE_XML;
     * }
     * else if(type.equals("EXUSRBALREQ"))//added for Gift Recharge through XML
     * API
     * {
     * action=ACTION_CHNL_BAL_ENQ_XML;
     * }
     * else if(type.equals("EXEVDREQ"))//added for Gift Recharge through XML API
     * {
     * action=ACTION_CHNL_EVD_XML;
     * }
     * else if(type.equals("EXC2CTRFREQ"))//C2C transfer through external getway
     * (XML API)
     * {
     * action=ACTION_C2C_TRANSFER_EXT_XML;
     * }
     * else if(type.equals("EXC2CWDREQ"))//C2C withdraw through external getway
     * (XML API)
     * {
     * action=ACTION_C2C_WITHDRAW_EXT_XML;
     * }
     * else if(type.equals("EXC2CRETREQ"))//C2C Return through external getway
     * (XML API)
     * {
     * action=ACTION_C2C_RETURN_EXT_XML;
     * }
     * else if(type.equals("EXC2SCPNREQ"))//Change Pin through external getway
     * (XML API)
     * {
     * action=ACTION_EXT_C2SCHANGEPIN_XML;
     * }
     * else if(type.equals("CDMARCTRFREQ"))//Added for CDMA recharge
     * {
     * action=ACTION_CHNL_CREDIT_TRANSFER_CDMA;
     * }
     * else if(type.equals("PSTNRCTRFREQ"))//Added for PSTN recharge
     * {
     * action=ACTION_CHNL_CREDIT_TRANSFER_PSTN;
     * }
     * else if(type.equals("INTRRCTRFREQ"))//Added for INTERNET recharge
     * {
     * action=ACTION_CHNL_CREDIT_TRANSFER_INTR;
     * }
     * else if(type.equals("EXCDMARCREQ"))//Added for CDMA Bank Recharge
     * {
     * action=ACTION_CHNL_EXT_CREDIT_TRANSFER_CDMA;
     * }
     * else if(type.equals("EXPSTNRCREQ"))//Added for PSTN Bank Recharge
     * {
     * action=ACTION_CHNL_EXT_CREDIT_TRANSFER_PSTN;
     * }
     * else if(type.equals("EXINTRRCREQ"))//Added for INTR Bank Recharge
     * {
     * action=ACTION_CHNL_EXT_CREDIT_TRANSFER_INTR;
     * }
     * else if(type.equals("ORDLREQ"))//Added for Order Line
     * {
     * action=ACTION_CHNL_ORDER_LINE;
     * }
     * else if(type.equals("ORDCREQ"))//Added for Order Credit
     * {
     * action=ACTION_CHNL_ORDER_CREDIT;
     * }
     * else if(type.equals("BARREQ"))//Added for Barring
     * {
     * action=ACTION_CHNL_BARRING;
     * }
     * else if(type.equals("VASSELLREQ"))//Roam Recharge through external getway
     * (XML API)
     * {
     * action=ACTION_CHNL_EXT_VAS_SELLING;
     * }
     * else if(type.equals("ROAMRCREQ"))//Roam Recharge through external getway
     * (XML API)
     * {
     * action=ACTION_CHNL_IAT_ROAM_RECHARGE;
     * }
     * else if(type.equals("INTLRCREQ"))//Roam Recharge through external getway
     * (XML API)
     * {
     * action=ACTION_CHNL_IAT_INTERNATIONAL_RECHARGE;
     * }
     * else if(type.equals("LXTSREQ"))
     * {
     * action=ACTION_CHNL_C2S_LAST_XTRANSFER;
     * }
     * 
     * if(action==-1)
     * throw new
     * BTSLBaseException(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
     * p_requestVO.setActionValue(action);
     * }
     * catch(BTSLBaseException be)
     * {
     * throw be;
     * }
     * catch(Exception e)
     * {
     * _log.error("actionChannelParser","Exception e: "+e);
     * e.printStackTrace();
     * throw new
     * BTSLBaseException(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
     * }
     * finally
     * {
     * if(_log.isDebugEnabled())_log.debug("actionChannelParser","exit action:"+
     * action);
     * }
     * return action;
     * }
     */
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
            throw new BTSLBaseException(this, "validateUserDetails", PretupsErrorCodesI.ERROR_EXCEPTION);
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
            throw new BTSLBaseException("USSDParsers", "parseChannelRequestMessage", PretupsErrorCodesI.ERROR_EXCEPTION);
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
            throw new BTSLBaseException("USSDParsers", "parseOperatorRequestMessage", PretupsErrorCodesI.ERROR_EXCEPTION);
        }
    }
}
