package com.btsl.pretups.gateway.parsers;

/*
 * @(#)USSDParsers.java
 * Copyright(c) 2005, Bharti Telesoft Ltd.
 * All Rights Reserved
 * ------------------------------------------------------------------------------
 * -------------------
 * Author Date History
 * ------------------------------------------------------------------------------
 * -------------------
 * Gurjeet Singh Nov 04, 2005 Initital Creation
 * Manoj Kumar Jan 27,2006 Modification
 * Kapil Mehta Feb 03,2009 Modification
 * ------------------------------------------------------------------------------
 * -------------------
 * Parser class to handle USSD requests
 */

import java.sql.Connection;
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
import com.btsl.pretups.gateway.util.ExtAPIStringParser;
import com.btsl.pretups.gateway.util.ExtAPIXMLStringParser;
import com.btsl.pretups.gateway.util.ParserUtility;
import com.btsl.pretups.gateway.util.USSDC2SXMLStringParser;
import com.btsl.pretups.gateway.util.USSDP2PXMLStringParser;
import com.btsl.pretups.gateway.util.USSDStringParser;
import com.btsl.pretups.gateway.util.XMLStringParser;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;

public class USSDParsers extends ParserUtility {
	public static Log _log = LogFactory.getLog(USSDParsers.class.getClass());

	public void parseRequestMessage(RequestVO p_requestVO) throws BTSLBaseException {
		String contentType = p_requestVO.getReqContentType();
		if (_log.isDebugEnabled()) {
			_log.debug("parseRequestMessage", "Transfer ID=" + p_requestVO.getRequestID() + " contentType: " + contentType);
		}
		final String METHOD_NAME = "parseRequestMessage";
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
			_log.error("parseRequestMessage", " BTSL Exception while parsing Request Message :" + be.getMessage());
			throw be;
		} catch (Exception e) {
			_log.error("parseRequestMessage", "  Exception while parsing Request Message :" + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "USSDParsers[parseRequestMessage]", p_requestVO
					.getTransactionID(), "", "", "Exception :" + e.getMessage());
			throw new BTSLBaseException("USSDParsers", "parseRequestMessage", PretupsErrorCodesI.ERROR_EXCEPTION);
		}
	}

	public void generateResponseMessage(RequestVO p_requestVO) {
		String contentType = p_requestVO.getReqContentType();
		if (_log.isDebugEnabled()) {
			_log.debug("generateResponseMessage", "Transfer ID=" + p_requestVO.getRequestID() + " contentType: " + contentType);
		}
		final String METHOD_NAME = "generateResponseMessage";
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
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "USSDParsers[parseRequestMessage]", p_requestVO
					.getTransactionID(), "", "", "Exception getting message :" + e.getMessage());
			try {
				// XMLAPIParser.generateFailureResponse(p_requestVO);
				ExtAPIXMLStringParser.generateFailureResponse(p_requestVO);
			} catch (Exception ex) {
				_log.errorTrace(METHOD_NAME, ex);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "USSDParsers[parseRequestMessage]", p_requestVO
						.getTransactionID(), "", "", "Exception getting default message :" + ex.getMessage());
				p_requestVO
				.setSenderReturnMessage("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND><TYPE></TYPE><TXNSTATUS>" + PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT + "</TXNSTATUS></COMMAND>");
			}
		}
	}

	public void parseChannelRequestMessage(RequestVO p_requestVO, Connection pCon) throws BTSLBaseException {
		String contentType = p_requestVO.getReqContentType();
		if (_log.isDebugEnabled()) {
			_log.debug("parseChannelRequestMessage", "Transfer ID=" + p_requestVO.getRequestID() + " contentType: " + contentType);
		}
		final String METHOD_NAME = "parseChannelRequestMessage";
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
			_log.error("parseChannelRequestMessage", " BTSL Exception while parsing Request Message :" + be.getMessage());
			throw be;
		} catch (Exception e) {
			_log.error("parseChannelRequestMessage", "  Exception while parsing Request Message :" + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "USSDParsers[parseChannelRequestMessage]", p_requestVO
					.getTransactionID(), "", "", "Exception :" + e.getMessage());
			throw new BTSLBaseException("USSDParsers", "parseChannelRequestMessage", PretupsErrorCodesI.ERROR_EXCEPTION);
		}
	}

	public void generateChannelResponseMessage(RequestVO p_requestVO) {
		String contentType = p_requestVO.getReqContentType();
		if (_log.isDebugEnabled()) {
			_log.debug("generateChannelResponseMessage", "Transfer ID=" + p_requestVO.getRequestID() + " contentType: " + contentType);
		}
		final String METHOD_NAME = "generateChannelResponseMessage";
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
			//message = BTSLUtil.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
        message = BTSLUtil.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments(), p_requestVO.getRequestGatewayType());
             
				}
				int index = 0;
				index = message.indexOf(":");
				if (index > -1) {
					index = message.indexOf(":", index++);
				}

				if (index > -1) {
					//message = message.substring(index, message.length());
					  message = message.substring(index+1, message.length());
				}

				p_requestVO.setSenderReturnMessage(message);
			}
		} catch (Exception e) {
			_log.error("generateChannelResponseMessage", "  Exception while generating Response Message :" + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "USSDParsers[generateChannelResponseMessage]",
					p_requestVO.getTransactionID(), "", "", "Exception getting message :" + e.getMessage());
			try {
				// XMLAPIParser.generateFailureResponse(p_requestVO);
				ExtAPIXMLStringParser.generateFailureResponse(p_requestVO);
			} catch (Exception ex) {
				_log.errorTrace(METHOD_NAME, ex);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "USSDParsers[generateChannelResponseMessage]",
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
		case ACTION_VOMS_RET:
		{
			USSDC2SXMLStringParser.parseVoucherRetReq(p_requestVO);
			break;
		}
		case ACTION_VOMS_RETRIEVAL_ROLLBACK:
		{
			USSDC2SXMLStringParser.parseVoucherRetrievalRollBackReq(p_requestVO);
			break;
		}
		case ACTION_VOUCHER_CONSUMPTION:
		{
			USSDC2SXMLStringParser.parseVoucherConsumptionRequest(p_requestVO);
			break;
		}
		case ACTION_VOMS_CON: // added for voms
		{
			USSDC2SXMLStringParser.parseVoucherConsReq(p_requestVO);
			break;
		}
		case ACTION_VOMS_ROLLBACK: // added for voms
		{
			USSDC2SXMLStringParser.parseVoucherRollBackReq(p_requestVO);
			break;
		}
		// added for voucher query and rollback request
		case ACTION_VOMS_QRY: // added for voms
		{
			USSDC2SXMLStringParser.parseVoucherQueryReq(p_requestVO);
			break;
		}
		case IAT_P2P_RECHRG:
		{
			XMLStringParser.parseP2PIATCreditRequest(p_requestVO);
			break;
		}
		case ACTION_DATA_CP2P_RECHARGE:
			{
				XMLStringParser.parseChannelCP2PDataTransferRequest(p_requestVO);
				break;
			}
		case ACTION_VOMS_MY_VOUCHR_ENQUIRY_SUBSCRIBER_REQ:
		{
			ExtAPIXMLStringParser.parseMyVoucherEnquirySubscriberRequest(p_requestVO);
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
			_log.debug("parseChannelRequest", "Entered Request ID=" + p_requestVO.getRequestID() + " action=" + action);
		}

		// Set Filtered MSISDN, set _requestMSISDN
		// Set message Format , set in decrypted message

		switch (action) {

	     case TOTAL_USER_INCOME_DETAILS_VIEW:
	    	{
	    		ExtAPIXMLStringParser.parseTotalIncomeDetailsViewRequest(p_requestVO);
	    		break;
	    	}
		case ACTION_EXT_LAST_XTRF_ENQ:{
			ExtAPIXMLStringParser.parseExtLastXTransferEnq(p_requestVO);
			break;
		}
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
		// Added By Brajesh For LMS Points Enquiry
		case ACTION_CHNL_LMS_POINTS_ENQUIRY:
		{
			ExtAPIStringParser.parseExtStringRequest(p_requestVO);
			p_requestVO.setRequestMSISDN((String) p_requestVO.getRequestMap().get("MSISDN"));
			if ((BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("MSISDN"))) || (BTSLUtil.NullToString(
					p_requestVO.getRequestMap().get("PIN").toString()).length() == 0)) {
				p_requestVO.setSuccessTxn(false);
				throw new BTSLBaseException("USSDParsers", "parseChannelRequestMessage", PretupsErrorCodesI.ERROR_INVALID_MESSAGE_FORMAT);
			} else if (BTSLUtil.isNullString(p_requestVO.getRequestMap().get("PIN").toString())) {
				throw new BTSLBaseException("USSDParsers", "parseChannelRequestMessage", PretupsErrorCodesI.ERROR_INVALID_PIN);
			}
			String date = (String) p_requestVO.getRequestMap().get("DATE");
			date=BTSLDateUtil.getGregorianDateInString(date);
			if (!BTSLUtil.isValidDatePattern(date)) {
				p_requestVO.setSuccessTxn(false);
				throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.LMS_INVALID_DATE);
			}
			break;
		}
		// Added By Brajesh For LMS Points Redemption
		case ACTION_CHNL_LMS_POINTS_REDEMPTION:
		{
			ExtAPIStringParser.parseExtStringRequest(p_requestVO);
			p_requestVO.setRequestMSISDN((String) p_requestVO.getRequestMap().get("MSISDN"));
			if (BTSLUtil.isNullString(p_requestVO.getRequestMap().get("PIN").toString())) {
				throw new BTSLBaseException("USSDParsers", "parseChannelRequestMessage", PretupsErrorCodesI.ERROR_INVALID_PIN);
			} else if (BTSLUtil.isNullString(p_requestVO.getRequestMap().get("POINTS").toString())) {
				throw new BTSLBaseException("USSDParsers", "parseChannelRequestMessage", PretupsErrorCodesI.INVALID_REDEMP_LOYALTY_POINTS);
			} else if ((BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("MSISDN"))) || (BTSLUtil.NullToString(
					p_requestVO.getRequestMap().get("PIN").toString()).length() == 0) || (BTSLUtil.NullToString(p_requestVO.getRequestMap().get("POINTS").toString())
							.length() == 0)) {
				p_requestVO.setSuccessTxn(false);
				throw new BTSLBaseException("USSDParsers", "parseChannelRequestMessage", PretupsErrorCodesI.ERROR_INVALID_MESSAGE_FORMAT);
			}
			String date = (String) p_requestVO.getRequestMap().get("DATE");
			date=BTSLDateUtil.getGregorianDateInString(date);
			if (!BTSLUtil.isValidDatePattern(date)) {
				p_requestVO.setSuccessTxn(false);
				throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.LMS_INVALID_DATE);
			}
			break;
		}
		case ACTION_INITIATE_PIN_RESET:
		{
			ExtAPIStringParser.parseExtStringRequest(p_requestVO);
			p_requestVO.setRequestMSISDN((String) p_requestVO.getRequestMap().get("MSISDN"));
			break;
		}
		case ACTION_PIN_RESET:
		{
			ExtAPIStringParser.parseExtStringRequest(p_requestVO);
			p_requestVO.setRequestMSISDN((String) p_requestVO.getRequestMap().get("MSISDN"));
			break;
		}
		case ACTION_DATA_UPDATE:
		{
			ExtAPIStringParser.parseExtStringRequest(p_requestVO);
			p_requestVO.setRequestMSISDN((String) p_requestVO.getRequestMap().get("MSISDN"));
			break;
		}
		case ACTION_CUST_C2S_ENQ_REQ: // change for SIM activation by sachin
			// date 1/06/2011
		{
			XMLStringParser.parseC2STrfEnquiryUSSDRequest(p_requestVO);
			break;
		}

		case ACTION_C2S_PRE_PAID_REVERSAL:
		{
			ExtAPIXMLStringParser.parsePrepaidRCReversalRequest(p_requestVO);
			break;
		}

		case ACTION_CHNL_LITE_RECHARGE:
		{
			ExtAPIStringParser.parseExtStringRequest(p_requestVO);
			p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(((String)p_requestVO.getRequestMap().get("LANGUAGE1"))));
			p_requestVO.setReqSelector((String)p_requestVO.getRequestMap().get("SELECTOR"));
			p_requestVO.setRequestMSISDN((String)p_requestVO.getRequestMap().get("MSISDN1"));
			p_requestVO.setCellId((String)p_requestVO.getRequestMap().get("CELLID"));
			p_requestVO.setSwitchId((String)p_requestVO.getRequestMap().get("SWITCHID"));
			break;
		}
		case ACTION_CHNL_CARDGROUP_ENQUIRY_REQUEST:
		{	
			ExtAPIStringParser.parseExtStringRequest(p_requestVO);

			if((BTSLUtil.isNullString((String)p_requestVO.getRequestMap().get("MSISDN1")))){
				p_requestVO.setSuccessTxn(false);
				throw new BTSLBaseException("USSDParsers","parseChannelRequestMessage",PretupsErrorCodesI.MANDATORY_EMPTY);
			}
			if(BTSLUtil.isNullString(p_requestVO.getRequestMap().get("MSISDN2").toString()))
			{
				p_requestVO.setSuccessTxn(false);
				throw new BTSLBaseException("USSDParsers","parseChannelRequestMessage",PretupsErrorCodesI.CARDGROUP_ENQUIRY_RECR_MSISDN_BLANK);
			}
			if(BTSLUtil.isNullString(p_requestVO.getRequestMap().get("SERVICETYPE").toString()))
			{
				p_requestVO.setSuccessTxn(false);
				throw new BTSLBaseException("USSDParsers","parseChannelRequestMessage",PretupsErrorCodesI.CARDGROUP_ENQUIRY_SERVICETYPE_BLANK);
			}
			if(BTSLUtil.isNullString(p_requestVO.getRequestMap().get("AMOUNT").toString()))
			{
				p_requestVO.setSuccessTxn(false);
				throw new BTSLBaseException("USSDParsers","parseChannelRequestMessage",PretupsErrorCodesI.CARDGROUP_ENQUIRY_AMOUNT_BLANK);
			}
			p_requestVO.setRequestMSISDN((String)p_requestVO.getRequestMap().get("MSISDN1"));
			p_requestVO.setReceiverMsisdn((String)p_requestVO.getRequestMap().get("MSISDN2"));
			p_requestVO.setEnquiryServiceType((String)p_requestVO.getRequestMap().get("SERVICETYPE"));
			p_requestVO.setEnquirySubService((String)p_requestVO.getRequestMap().get("SUBSERVICE"));
			p_requestVO.setEnquiryAmount((String)p_requestVO.getRequestMap().get("AMOUNT"));
			break;
		}
		case ACTION_CHNL_EXT_BULK_RCH_REVERSAL:
		{
			ExtAPIStringParser.parseExtStringRequest(p_requestVO);
			p_requestVO.setRequestMSISDN((String)p_requestVO.getRequestMap().get("MSISDN"));
			p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(((String)p_requestVO.getRequestMap().get("LANGUAGE1"))));
			p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(((String)p_requestVO.getRequestMap().get("LANGUAGE2"))));
			break;
		}
		case ACTION_CHNL_SOS_SETTLEMENT_REQUEST:
		{
			ExtAPIStringParser.parseExtStringRequest(p_requestVO);
			p_requestVO.setRequestMSISDN((String)p_requestVO.getRequestMap().get("MSISDN"));
			 String language1 = (String) p_requestVO.getRequestMap().get("LANGUAGE1");
			 String language2 = (String) p_requestVO.getRequestMap().get("LANGUAGE2");
			 if (BTSLUtil.isNullString(language1)) {
			     p_requestVO.setSenderLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
			 } else {
			       
			       if (LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(language1)) == null) {
			         throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
			     }
			     p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(language1)));
			 }
			 
			 if (BTSLUtil.isNullString(language2)) {
			     p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
			 } else {
			       
			       if (LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(language2)) == null) {
			         throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
			     }

			     p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(language2)));
			 }

			String CHNL_MESSAGE_SEP = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
			if (BTSLUtil.isNullString(CHNL_MESSAGE_SEP)) {
				CHNL_MESSAGE_SEP = " ";
			}
			String message = p_requestVO.getDecryptedMessage();
			if (BTSLUtil.NullToString(p_requestVO.getRequestMap().get("PIN").toString()).length() == 0) {
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.C2S_PIN_BLANK);
			}
			message = message + CHNL_MESSAGE_SEP + p_requestVO.getSenderLocale();
			message = message + CHNL_MESSAGE_SEP + p_requestVO.getReceiverLocale();
			message = message + CHNL_MESSAGE_SEP + (String) p_requestVO.getRequestMap().get("PIN");
			p_requestVO.setDecryptedMessage(message);
			break;
		}
		case ACTION_CHNL_SOS_REQUEST:
		{
			ExtAPIStringParser.parseExtStringRequest(p_requestVO);
			p_requestVO.setRequestMSISDN((String)p_requestVO.getRequestMap().get("MSISDN"));
			p_requestVO.setExternalNetworkCode((String)p_requestVO.getRequestMap().get("EXTNWCODE"));
			String CHNL_MESSAGE_SEP = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
			if (BTSLUtil.isNullString(CHNL_MESSAGE_SEP)) {
				CHNL_MESSAGE_SEP = " ";
			}
			String message = p_requestVO.getDecryptedMessage(); 
			if (BTSLUtil.NullToString(p_requestVO.getRequestMap().get("PIN").toString()).length() == 0) {
				throw new BTSLBaseException(this, "parse", PretupsErrorCodesI.C2S_PIN_BLANK);
			}
			String language1=(String)p_requestVO.getRequestMap().get("LANGUAGE1");
			if (BTSLUtil.isNullString(language1)) {
				p_requestVO.setSenderLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
			} else {
				if (LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(language1)) == null) {
					throw new BTSLBaseException(this, "validateC2SReverrsalRequest", PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
				}
				p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(language1)));
			}
			message = message + CHNL_MESSAGE_SEP + (String) p_requestVO.getRequestMap().get("MSISDN") + CHNL_MESSAGE_SEP + (String) p_requestVO.getRequestMap().get("PIN") + CHNL_MESSAGE_SEP + p_requestVO.getSenderLocale();
			p_requestVO.setDecryptedMessage(message);
			break;
		}
		//Added for Channel user Commission earned enq
		case ACTION_USER_COMM_EARNED:
		{
			XMLStringParser.parseCUCommEarnedEnqRequest(p_requestVO);
			break;
		}
		
		case ACTION_CHNL_VOUCHER_AVAILABILITY_XML:
		{
			ExtAPIXMLStringParser.parseExtDigitalVouchersAvailabilityRequest(p_requestVO);
            break;
		}
		
		case ACTION_CHNL_DVD_XML:
		{
			USSDC2SXMLStringParser.parseChannelDVDRequest(p_requestVO);
			break;
		}
		case ACTION_C2C_REQ_REC:
		{
			ExtAPIXMLStringParser.parseChannelTransferRequest(p_requestVO, ACTION_C2C_REQ_REC);
			break;	
		}
		case ACTION_C2C_APPR:
		{
			ExtAPIXMLStringParser.parseExtC2CApproval(p_requestVO, ACTION_C2C_APPR);
            break;
		}case ACTION_C2C_VOUCHER_APPR:
		{
			ExtAPIXMLStringParser.parseExtC2CVoucherApproval(p_requestVO, ACTION_C2C_VOUCHER_APPR);
            break;
		}case ACTION_UPUSRHRCHY:
		{
			ExtAPIXMLStringParser.parseExtUserHierarchy(p_requestVO, ACTION_UPUSRHRCHY);
            break;
		}
		
		case C2C_BUY_ENQ:
		{
			ExtAPIXMLStringParser.parseC2cUserBuyEnquiry(p_requestVO, C2C_BUY_ENQ);
            break;
		}
		case C2S_SV_DETAILS:
		{
			ExtAPIXMLStringParser.parseC2sServiceDetails(p_requestVO, C2S_SV_DETAILS);
            break;
		}
		case C2S_TOTAL_TRANSACTION_COUNT:
    	{
    		ExtAPIXMLStringParser.parseC2STotalTnxCountReq(p_requestVO);
    		break;
    	}
		case ACTION_C2C_VOMS_TRF:
		{
			USSDC2SXMLStringParser.parseC2CVomsTransferRequest(p_requestVO);
            break;
		}
		case ACTION_C2C_VOMS_INI:
		{
			USSDC2SXMLStringParser.parseC2CVomsInitiateRequest(p_requestVO);
            break;
		}
		case CHANNEL_USER_DETAILS:
		{
			USSDC2SXMLStringParser.parseChannelUserDetailsRequestUssd(p_requestVO);
            break;
		}
		case C2S_PROD_TXN_DETAILS:
    	{
    		USSDC2SXMLStringParser.parseTxnCountDetails(p_requestVO,C2S_PROD_TXN_DETAILS);
    		break;
    	}
		case PASSBOOK_VIEW_DETAILS:
		{
			ExtAPIXMLStringParser.parsePassbookViewDetailsRequest(p_requestVO);
			break;
		}
		case C2S_N_PROD_TXN_DETAILS:
    	{
    		USSDC2SXMLStringParser.parseTxnCountDetails(p_requestVO,C2S_N_PROD_TXN_DETAILS);
    		break;
    	}
		case TOTAL_TRANSACTION_DETAILED_VIEW:
    	{
    		USSDC2SXMLStringParser.totalTrnxDetailReq(p_requestVO);
    		break;
    	}
		case ACTION_EXTVAS_RC_REQUEST:
		{
			USSDC2SXMLStringParser.parseChannelExtVASTransferRequest(p_requestVO);
			break;
		}
		case ACTION_LST_LOAN_ENQ:
		{
			
		   	ExtAPIStringParser.parseExtStringRequest(p_requestVO);
        	p_requestVO.setRequestMSISDN((String)p_requestVO.getRequestMap().get("MSISDN"));
        	System.out.println("INSIDE PIN: " + p_requestVO.getRequestMap().get("PIN"));

			
            break;
			
		}
		
		case ACTION_LOAN_OPTIN_REQ: 
		{
		   	ExtAPIStringParser.parseExtStringRequest(p_requestVO);
        	p_requestVO.setRequestMSISDN((String)p_requestVO.getRequestMap().get("MSISDN"));
		 if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("PIN"))) {
	                 p_requestVO.setPin((String) p_requestVO.getRequestMap().get("PIN"));
	             }
			p_requestVO.setExternalNetworkCode((String)p_requestVO.getRequestMap().get("EXTNWCODE"));
			
			if (p_requestVO.getRequestGatewayType().equalsIgnoreCase(PretupsI.REQUEST_SOURCE_TYPE_EXTGW)) {
                if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("LOGINID"))) {
                    p_requestVO.setSenderLoginID((String) p_requestVO.getRequestMap().get("LOGINID"));
                }
                if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("PASSWORD"))) {
                    p_requestVO.setPassword((String) p_requestVO.getRequestMap().get("PASSWORD"));
                }
                if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("EXTCODE"))) {
                	p_requestVO.setSenderExternalCode((String)p_requestVO.getRequestMap().get("EXTCODE"));
                }
                p_requestVO.setExternalReferenceNum(((String)p_requestVO.getRequestMap().get("EXTREFNUM")));
               
            }
            break;
				
		}
		case ACTION_LOAN_OPTOUT_REQ: 
		{
		   	ExtAPIStringParser.parseExtStringRequest(p_requestVO);
        		p_requestVO.setRequestMSISDN((String)p_requestVO.getRequestMap().get("MSISDN"));
			 if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("PIN"))) {
          			   p_requestVO.setPin((String) p_requestVO.getRequestMap().get("PIN"));
         			}
			p_requestVO.setExternalNetworkCode((String)p_requestVO.getRequestMap().get("EXTNWCODE"));
			
			if (p_requestVO.getRequestGatewayType().equalsIgnoreCase(PretupsI.REQUEST_SOURCE_TYPE_EXTGW)) {
                if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("LOGINID"))) {
                    p_requestVO.setSenderLoginID((String) p_requestVO.getRequestMap().get("LOGINID"));
                }
                if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("PASSWORD"))) {
                    p_requestVO.setPassword((String) p_requestVO.getRequestMap().get("PASSWORD"));
                }
                if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("EXTCODE"))) {
                	p_requestVO.setSenderExternalCode((String)p_requestVO.getRequestMap().get("EXTCODE"));
                }
                p_requestVO.setExternalReferenceNum(((String)p_requestVO.getRequestMap().get("EXTREFNUM")));
               
            }
            break;
				
		}
		
		case ACTION_SELF_CUBAR:
		{
			USSDC2SXMLStringParser.parseSelfChannelUserBarRequest(p_requestVO);
			break;
		}
		
		case ACTION_SELF_CU_UNBAR:
		{
			USSDC2SXMLStringParser.parseSelfChannelUserBarRequest(p_requestVO);
			break;
		}
		
		case ACTION_SELF_PIN_RESET:
	{
		ExtAPIStringParser.parseExtStringRequest(p_requestVO);
		p_requestVO.setRequestMSISDN((String) p_requestVO.getRequestMap().get("MSISDN"));
		break;
	}

		default:
 	     	 if(_log.isDebugEnabled()){
 	     		_log.debug("Default Value " ,action);
 	     	 }
		}
		
	}


	public void parseChannelRequestMessage(Connection p_con, RequestVO p_requestVO) throws BTSLBaseException {
		String contentType = p_requestVO.getReqContentType();
		if (_log.isDebugEnabled()) {
			_log.debug("parseChannelRequestMessage", "Transfer ID=" + p_requestVO.getRequestID() + " contentType: " + contentType);
		}
		final String METHOD_NAME = "parseChannelRequestMessage";
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
			_log.error("parseChannelRequestMessage", " BTSL Exception while parsing Request Message :" + be.getMessage());
			throw be;
		} catch (Exception e) {
			_log.error("parseChannelRequestMessage", "  Exception while parsing Request Message :" + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "USSDParsers[parseChannelRequestMessage]", p_requestVO
					.getTransactionID(), "", "", "Exception :" + e.getMessage());
			throw new BTSLBaseException("USSDParsers", "parseChannelRequestMessage", PretupsErrorCodesI.ERROR_EXCEPTION);
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
			USSDStringParser.parseCreditTransferRequest(p_requestVO);
			break;
		}
		case P2P_GIVE_ME_BALANCE:
		{
			USSDStringParser.parseP2PGiveMeBalanceRequest(p_requestVO);
			break;
		}
		case CHANGE_PIN:
		{
			USSDStringParser.parseChannelChangePinRequest(p_requestVO);
			break;
		}
		//added by Ashish for VIL
		case ACTION_VMS_PIN_CONSUME_REQ:
		{
			USSDStringParser.parseVoucherConsumptionPlainStringRequest(p_requestVO);
			break;
		}
		//ended by Ashish for VIL
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
		if (_log.isDebugEnabled()) {
			_log.debug("generateResponse", "Entered Request ID=" + p_requestVO.getRequestID() + " action=" + action);
		}

		String messageCode = p_requestVO.getMessageCode();
		if ((!BTSLUtil.isNullString(messageCode)) && (!p_requestVO.isSuccessTxn()) && messageCode.indexOf("_") != -1) {
			messageCode = messageCode.substring(0, messageCode.indexOf("_"));
			p_requestVO.setMessageCode(messageCode);
		}
		switch (action) {
		case CREDIT_TRANSFER:
		{
			USSDStringParser.generateCreditTransferResponse(p_requestVO);
			break;
		}
		case P2P_GIVE_ME_BALANCE:
		{
			USSDStringParser.generateP2PGiveMeBalanceResponse(p_requestVO);
			break;
		}
		case CHANGE_PIN:
		{
			USSDStringParser.generateChannelChangePinResponse(p_requestVO);
			break;
		}
		//added by Ashish for VIL
		case ACTION_VMS_PIN_CONSUME_REQ:
		{
			USSDStringParser.generateVoucherConsumptionPlainStringResponse(p_requestVO);
		}
		//ended by Ashish for VIL
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
		if (_log.isDebugEnabled()) {
			_log.debug("parseChannelRequest", "Entered Request ID=" + p_requestVO.getRequestID() + " action=" + action);
		}

		switch (action) {
		case ACTION_CHNL_CREDIT_TRANSFER:
		{
			USSDStringParser.parseChannelCreditTransferRequest(p_requestVO);
			break;
		}
		case ACTION_VAS_RC_REQUEST: // Changes for Tigo-GT
		{
			USSDStringParser.parseChannelVasTransferRequest(p_requestVO);
			break;
		}
		case ACTION_PVAS_RC_REQUEST: //Changes for Tigo-GT
		{
			USSDStringParser.parseChannelPrVasTransferRequest(p_requestVO);
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
		// added for Balance Enquiry 03/05/07
		case ACTION_CHNL_BALANCE_ENQUIRY:
		{
			USSDStringParser.parseChannelBalanceEnquiry(p_requestVO);
			break;
		}
		// added for Daily Status Report 03/05/07
		case ACTION_CHNL_DAILY_STATUS_REPORT:
		{
			USSDStringParser.parseChannelDailyStatusReport(p_requestVO);
			break;
		}
		// added for Last Transfer Status(RP2P) 03/05/07
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
		case ACTION_CHNL_HLPDESK_REQUEST: // added for Help Desk Number
			// Plain
			// Request
		{
			USSDStringParser.parseChannelHelpDeskRequest(p_requestVO);
			break;
		}
		case ACTION_CHNL_POSTPAID_BILLPAYMENT: // addedd for PPB from Flares
		{
			USSDStringParser.parseChannelPostPaidBillPaymentRequest(p_requestVO);
			break;
		}
		case ACTION_COL_ENQ: // added for Collection Enquiry
		{
			USSDStringParser.parseChannelCollectionEnquiryRequest(p_requestVO);
			break;
		}

		case ACTION_COL_BILLPAYMENT: // added for Collection Enquiry
		{
			USSDStringParser.parseChannelCollectionBillPaymentRequest(p_requestVO);
			break;
		}
		case ACTION_DTH: // added for DTH
		{
			USSDStringParser.parseC2STransferRequest(p_requestVO, ACTION_DTH);
			break;
		}
		case ACTION_DC:
		{
			USSDStringParser.parseC2STransferRequest(p_requestVO, ACTION_DC);
			break;
		}
		case ACTION_BPB:
		{
			USSDStringParser.parseC2STransferRequest(p_requestVO, ACTION_BPB);
			break;
		}
		case ACTION_PIN:
		{
			USSDStringParser.parseC2STransferRequest(p_requestVO, ACTION_PIN);
			break;
		}
		case ACTION_PMD:
		{
			USSDStringParser.parseC2STransferRequest(p_requestVO, ACTION_PMD);
			break;
		}
		case ACTION_FLRC:
		{
			USSDStringParser.parseC2STransferRequest(p_requestVO, ACTION_FLRC);
			break;
		}
		case ACTION_C2S_POSTPAID_REVERSAL: // added for Collection Enquiry
		{
			USSDStringParser.parseC2SPostPaidReversalRequest(p_requestVO);
			break;
		}
		// Added By Brajesh For LMS Points Enquiry
		case ACTION_CHNL_LMS_POINTS_ENQUIRY:
		{
			ExtAPIStringParser.parseExtStringRequest(p_requestVO);
			// USSDC2SXMLStringParser.parseLMSPointsEnquiryRequest(p_requestVO);
			break;
		}
		// Added By Brajesh For LMS Points Redemption
		case ACTION_CHNL_LMS_POINTS_REDEMPTION:
		{
			ExtAPIStringParser.parseExtStringRequest(p_requestVO);
			// USSDC2SXMLStringParser.parseLMSPointsRedemptionRequest(p_requestVO);
			break;
		}
		case ACTION_INITIATE_PIN_RESET:
		{
			ExtAPIStringParser.parseExtStringRequest(p_requestVO);
			break;
		}
		case ACTION_PIN_RESET:
		{
			ExtAPIStringParser.parseExtStringRequest(p_requestVO);
			break;
		}
		case ACTION_DATA_UPDATE:
		{
			ExtAPIStringParser.parseExtStringRequest(p_requestVO);
			break;
		}
		case ACTION_CHNL_LITE_RECHARGE:
		{
			ExtAPIStringParser.parseExtStringRequest(p_requestVO);
			p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(((String)p_requestVO.getRequestMap().get("LANGUAGE1"))));
			p_requestVO.setReqSelector((String)p_requestVO.getRequestMap().get("SELECTOR"));
			p_requestVO.setRequestMSISDN((String)p_requestVO.getRequestMap().get("MSISDN1"));
			p_requestVO.setCellId((String)p_requestVO.getRequestMap().get("CELLID"));
			p_requestVO.setSwitchId((String)p_requestVO.getRequestMap().get("SWITCHID"));
			break;
		}

		case ACTION_CHNL_CARDGROUP_ENQUIRY_REQUEST:
		{	ExtAPIStringParser.parseExtStringRequest(p_requestVO);
		if((BTSLUtil.isNullString((String)p_requestVO.getRequestMap().get("MSISDN1")))){
			p_requestVO.setSuccessTxn(false);
			throw new BTSLBaseException("USSDParsers","parseChannelRequestMessage",PretupsErrorCodesI.ERROR_INVALID_MESSAGE_FORMAT);
		}
		if(BTSLUtil.isNullString(p_requestVO.getRequestMap().get("MSISDN2").toString()))
		{
			p_requestVO.setSuccessTxn(false);
			throw new BTSLBaseException("USSDParsers","parseChannelRequestMessage",PretupsErrorCodesI.CARDGROUP_ENQUIRY_RECR_MSISDN_BLANK);
		}
		if(BTSLUtil.isNullString(p_requestVO.getRequestMap().get("SERVICETYPE").toString()))
		{
			p_requestVO.setSuccessTxn(false);
			throw new BTSLBaseException("USSDParsers","parseChannelRequestMessage",PretupsErrorCodesI.CARDGROUP_ENQUIRY_SERVICETYPE_BLANK);
		}
		if(BTSLUtil.isNullString(p_requestVO.getRequestMap().get("AMOUNT").toString()))
		{
			p_requestVO.setSuccessTxn(false);
			throw new BTSLBaseException("USSDParsers","parseChannelRequestMessage",PretupsErrorCodesI.CARDGROUP_ENQUIRY_AMOUNT_BLANK);
		}
		p_requestVO.setRequestMSISDN((String)p_requestVO.getRequestMap().get("MSISDN1"));
		p_requestVO.setReceiverMsisdn((String)p_requestVO.getRequestMap().get("MSISDN2"));
		p_requestVO.setEnquiryServiceType((String)p_requestVO.getRequestMap().get("SERVICETYPE"));
		p_requestVO.setEnquirySubService((String)p_requestVO.getRequestMap().get("SUBSERVICE"));
		p_requestVO.setEnquiryAmount((String)p_requestVO.getRequestMap().get("AMOUNT"));
		break;
		}
		case ACTION_MULTICURRENCY_RECHARGE :
		{
			USSDStringParser.parseC2STransferRequest(p_requestVO, ACTION_MULTICURRENCY_RECHARGE);
			break;
		}
		case ACTION_CHNL_EXT_BULK_RCH_REVERSAL:
		{
			ExtAPIStringParser.parseExtStringRequest(p_requestVO);
			p_requestVO.setRequestMSISDN((String)p_requestVO.getRequestMap().get("MSISDN"));
			p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(((String)p_requestVO.getRequestMap().get("LANGUAGE1"))));
			p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(((String)p_requestVO.getRequestMap().get("LANGUAGE2"))));
			break;
		}
		case ACTION_CRM_USER_AUTH_XML://Changes for Tigo-GT
        {
            	USSDStringParser.parseChannelUserAuthRequest(p_requestVO);
            	break;
        }
		case ACTION_LAST_X_TRF_OPT_CHANL:
        {
        	USSDStringParser.parseLastXTrfRequest(p_requestVO);
        	break;
        	
        }
        
		case ACTION_CHNL_IAT_ROAM_RECHARGE: // added for IAT Recharge
        {
        	USSDStringParser.parseIATRoamRecharge(p_requestVO);
            break;
        }
        
		case TXN_ENQUIRY:
        {
        	ExtAPIStringParser.parseExtStringRequest(p_requestVO);
            break;
        }
        
		case LAST_TXN_STATUS_SUBSCRIBER:
        {
        	ExtAPIStringParser.parseExtStringRequest(p_requestVO);
        	p_requestVO.setRequestMSISDN((String)p_requestVO.getRequestMap().get("MSISDN1"));
            break;
        }
        
		case C2S_SUMMARY_ENQUIRY:
        {
        	ExtAPIStringParser.parseExtStringRequest(p_requestVO);
        	p_requestVO.setRequestMSISDN((String)p_requestVO.getRequestMap().get("MSISDN1"));
            break;
        }
        
		case ACTION_WIRELESS_RC_REQUEST:
	    	USSDStringParser.parseChannelWirelessRCTransferRequest(p_requestVO);

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
		case ACTION_CHNL_EXT_RECH_STATUS:
		{
			USSDStringParser.generateChannelExtRechargeStatusResponse(p_requestVO);
			break;
		}
		case ACTION_CHNL_EXT_CREDIT_TRANSFER:
		{
			USSDStringParser.generateChannelExtRechargeStatusResponse(p_requestVO);
			break;
		}
		// added for Balance Enquiry 03/05/07
		case ACTION_CHNL_BALANCE_ENQUIRY:
		{
			USSDStringParser.generateChannelBalanceEnquiryResponse(p_requestVO);
			break;
		}
		// added for Daily Status Report 03/05/07
		case ACTION_CHNL_DAILY_STATUS_REPORT:
		{
			USSDStringParser.generateChannelDailyStatusReportResponse(p_requestVO);
			break;
		}
		// added for Last Transfer Status(RP2P) 03/05/07
		case ACTION_CHNL_LAST_TRANSFER_STATUS:
		{
			USSDStringParser.generateChannelLastTransferStatusResponse(p_requestVO);
			break;
		}
		case ACTION_CHNL_C2S_LAST_XTRANSFER: // last X C2S transfer
		{
			USSDStringParser.generateChannelLastXTransferStatusResponse(p_requestVO);
			break;
		}
		case ACTION_CHNL_CUST_LAST_XTRANSFER: // last X C2S transfer
		{
			USSDStringParser.generateChannelXEnquiryStatusResponse(p_requestVO);
			break;
		}
		case ACTION_CHNL_HLPDESK_REQUEST: // Help Desk Plain Response
		{
			USSDStringParser.generateChannelHelpDeskResponse(p_requestVO);
			break;
		}

		case ACTION_CHNL_POSTPAID_BILLPAYMENT: // PPB Response Plain
		{
			USSDStringParser.generateChannelPostPaidBillPaymentResponse(p_requestVO);
			break;
		}

		case ACTION_COL_ENQ: // Collection Enquiry
		{
			USSDStringParser.generateChannelCollectionEnquiryResponse(p_requestVO);
			break;
		}
		case ACTION_COL_BILLPAYMENT: // Collection Bill Payment
		{
			USSDStringParser.generateChannelCollectionBillPaymentResponse(p_requestVO);
			break;
		}
		case ACTION_DTH: // added for DTH
		{
			USSDStringParser.generateC2STransferResponse(p_requestVO, ACTION_DTH);
			break;
		}
		case ACTION_DC:
		{
			USSDStringParser.generateC2STransferResponse(p_requestVO, ACTION_DC);
			break;
		}
		case ACTION_BPB:
		{
			USSDStringParser.generateC2STransferResponse(p_requestVO, ACTION_BPB);
			break;
		}
		case ACTION_PIN:
		{
			USSDStringParser.generateC2STransferResponse(p_requestVO, ACTION_PIN);
			break;
		}
		case ACTION_PMD:
		{
			USSDStringParser.generateC2STransferResponse(p_requestVO, ACTION_PMD);
			break;
		}
		case ACTION_FLRC:
		{
			USSDStringParser.generateC2STransferResponse(p_requestVO, ACTION_FLRC);
			break;
		}
		case ACTION_MULTICURRENCY_RECHARGE:
		{
			USSDStringParser.generateC2STransferResponse(p_requestVO, ACTION_MULTICURRENCY_RECHARGE);
			break;
		}
		case ACTION_C2S_POSTPAID_REVERSAL: // Collection Bill Payment
		{
			USSDStringParser.generateC2SPostPaidReversalResponse(p_requestVO);
			break;
		}
		case ACTION_CHNL_LITE_RECHARGE:
		{
			p_requestVO.getResponseMap().put("TYPE","RCTRFSERRESP");
			p_requestVO.getResponseMap().put("TXNID",p_requestVO.getTransactionID());
			ExtAPIStringParser.populateResponseMap(p_requestVO, PretupsErrorCodesI.C2S_SENDER_SUCCESS);
			ExtAPIStringParser.generateExtStringResponse(p_requestVO);
			break;
		}
		case ACTION_CHNL_EXT_BULK_RCH_REVERSAL:
		{
			p_requestVO.getResponseMap().put("TYPE","BRCREVRESP");
			p_requestVO.getResponseMap().put("TXNID",p_requestVO.getTransactionID());
			ExtAPIStringParser.populateResponseMap(p_requestVO, PretupsErrorCodesI.C2S_SENDER_SUCCESS_PRE_REVERSAL);
			ExtAPIStringParser.generateExtStringResponse(p_requestVO);
			break;
		}
		case ACTION_CRM_USER_AUTH_XML: // User Authorization
    	{
    		USSDStringParser.generateUserAuthorizationResponse(p_requestVO);
    		break;
    	}
		 case ACTION_LAST_X_TRF_OPT_CHANL:
         {
         	USSDStringParser.generateLastXTrfResponse(p_requestVO);
         	break;
         }
         
		 case ACTION_CHNL_IAT_ROAM_RECHARGE: //ROAM Recharge
         {
        	 USSDStringParser.generateRoamRechargeResponse(p_requestVO);
         break;
         }

		 case TXN_ENQUIRY: 
         {
            p_requestVO.getResponseMap().put("TYPE","TXNENQRESP");
 			ExtAPIStringParser.populateResponseMap(p_requestVO, p_requestVO.getMessageCode());
 			ExtAPIStringParser.generateExtStringResponse(p_requestVO);
         break;
         }
		 case LAST_TXN_STATUS_SUBSCRIBER:
		 {
			p_requestVO.getResponseMap().put("TYPE","LTSRVRRESP");
	 		ExtAPIStringParser.populateResponseMap(p_requestVO, p_requestVO.getMessageCode());
	 		ExtAPIStringParser.generateExtStringResponse(p_requestVO);
	     break;
	     }
		 case C2S_SUMMARY_ENQUIRY:
	     {
	    	p_requestVO.getResponseMap().put("TYPE","C2SSUMRES");
		 	ExtAPIStringParser.populateResponseMap(p_requestVO, p_requestVO.getMessageCode());
		 	ExtAPIStringParser.generateExtStringResponse(p_requestVO);
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
		String contentType = p_requestVO.getReqContentType();
		if (_log.isDebugEnabled()) {
			_log.debug("parseOperatorRequestMessage", "Transfer ID=" + p_requestVO.getRequestID() + " contentType: " + contentType);
		}
		final String METHOD_NAME = "parseOperatorRequestMessage";
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
			_log.error("parseOperatorRequestMessage", " BTSL Exception while parsing Request Message :" + be.getMessage());
			throw be;
		} catch (Exception e) {
			_log.error("parseChannelRequestMessage", "  Exception while parsing Request Message :" + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "USSDParsers[parseOperatorRequestMessage]", p_requestVO
					.getTransactionID(), "", "", "Exception :" + e.getMessage());
			throw new BTSLBaseException("USSDParsers", "parseOperatorRequestMessage", PretupsErrorCodesI.ERROR_EXCEPTION);
		}
	}
	
	@Override
	public void validateUserIdentification(RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "validateUserIdentification";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered Request ID = " + p_requestVO.getRequestID() + ", RequestMSISDN() = " + p_requestVO.getRequestMSISDN());
        }
        // Validate user on the basis of values provided.
        // If MSISDN is there then validate the same.
        if (!BTSLUtil.isNullString(p_requestVO.getRequestMSISDN())) {
            validateMSISDN(p_requestVO);
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exiting...");
        }
    }
	
	/*@Override
	public ChannelUserVO loadValidateUserDetails(Connection p_con, RequestVO p_requestVO) throws BTSLBaseException {
		final String METHOD_NAME = "loadValidateUserDetails";
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME,
					"Entered Request ID=" + p_requestVO.getRequestID() + "Action= " + p_requestVO.getActionValue());
		}
		ChannelUserVO channelUserVO = null;
		boolean byPassCheck = false;
		if (p_requestVO.getActionValue() == ACTION_VOMS_QRY) {
			byPassCheck = true;
		}
		try {
			if (BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN())
					&& BTSLUtil.isNullString(p_requestVO.getSenderExternalCode())
					&& BTSLUtil.isNullString(p_requestVO.getSenderLoginID()) && byPassCheck == false) {
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_MISSING_SENDER_IDENTIFICATION);
			}

			String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
			OperatorUtilI operatorUtili = null;
			try {
				operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
			} catch (Exception e) {
				_log.errorTrace(METHOD_NAME, e);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED,
						EventLevelI.FATAL, "ExtAPIParsers[loadValidateUserDetails]", "", "", "",
						"Exception while loading the class at the call:" + e.getMessage());
			}

			int st = p_requestVO.getActionValue();

			// Changing for passowrd security
			if (p_requestVO.getRequestMap() != null) {
				String passwordtemp = (String) p_requestVO.getRequestMap().get("PASSWORD");
				try {
					passwordtemp = operatorUtili.decryptPINPassword(passwordtemp);
				} catch (Exception e) {
					_log.errorTrace(METHOD_NAME, e);
				}
				p_requestVO.getRequestMap().put("PASSWORD", passwordtemp);
			}
			switch (p_requestVO.getActionValue()) {

			case ACTION_VOMS_QRY: {
				String extCode = (String) p_requestVO.getRequestMap().get("EXTCODE");
				String extnetworkID = p_requestVO.getExternalNetworkCode();
				String loginID = p_requestVO.getSenderLoginID();
				String password = p_requestVO.getPassword();
				LoginDAO _loginDAO = new LoginDAO();
				byPassCheck = true;
				Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));

				if (!BTSLUtil.isNullString(loginID)) {
					channelUserVO = _loginDAO.loadUserDetails(p_con, loginID, password, locale);
					if (channelUserVO == null) {
						throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
					}
				} else if (!BTSLUtil.isNullString(extCode)) {
					channelUserVO = _channelUserDAO.loadChnlUserDetailsByExtCode(p_con,
							BTSLUtil.NullToString(extCode).trim());
				}
				if (!BTSLUtil.isNullString(extnetworkID)) {
					NetworkVO networkVO = (NetworkVO) NetworkCache.getNetworkByExtNetworkCode(extnetworkID);
					if (networkVO == null) {
						;
						throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_INVALID);
					}
				}

				if (channelUserVO != null) {
					if (!operatorUtili.validateTransactionPassword(channelUserVO, password)) {
						throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
					}
					if (!BTSLUtil.isNullString(extnetworkID)
							&& !extnetworkID.equalsIgnoreCase(channelUserVO.getNetworkID())) {
						throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
					}
					if (!BTSLUtil.isNullString(loginID)) {
						if (!loginID.equalsIgnoreCase(channelUserVO.getLoginID())) {
							throw new BTSLBaseException(this, METHOD_NAME,
									PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
						}
					}
					if (!BTSLUtil.isNullString(extCode)) {
						if (!extCode.equalsIgnoreCase(channelUserVO.getExternalCode())) {
							throw new BTSLBaseException(this, METHOD_NAME,
									PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE);
						}
					}

				}

			}    
			}
		} catch (BTSLBaseException be) {
			_log.errorTrace(METHOD_NAME, be);
			throw be;
		} catch (Exception e) {
			_log.errorTrace(METHOD_NAME, e);
			throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_EXCEPTION);
		} finally {
			if (_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME,
						"Exiting Request ID=" + p_requestVO.getRequestID() + " channelUserVO=" + channelUserVO);
			}
		}

		return channelUserVO;
	}*/
}
	
