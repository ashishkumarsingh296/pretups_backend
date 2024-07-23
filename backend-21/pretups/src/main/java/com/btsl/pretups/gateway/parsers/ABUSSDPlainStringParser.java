package com.btsl.pretups.gateway.parsers;

/* @(#)USSDPlainStringParser.java
 * Copyright(c) 2009, Comviva technologies
 * All Rights Reserved
 *-------------------------------------------------------------------------------------------------
 * Author                        Date            History
 *-------------------------------------------------------------------------------------------------
 * Manisha Jain					24/11/09		Initial creation
 *-------------------------------------------------------------------------------------------------
 *
 *Parser class to handle USSD plain requests
 */

import java.sql.Connection;

import jakarta.servlet.http.HttpServletRequest;

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
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.util.BTSLUtil;

public class ABUSSDPlainStringParser extends ParserUtility {
	private static String chnl_message_sep = null;
	private static final String  METHOD_NAME="ABUSSDPlainStringParser";
	private static final Log LOG = LogFactory.getLog(ABUSSDPlainStringParser.class.getName());
	static {
		try {
			chnl_message_sep = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
			if (BTSLUtil.isNullString(chnl_message_sep))
				chnl_message_sep = " ";
		} catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
		}
	}

	public void parseRequestMessage(RequestVO p_requestVO)
			throws BTSLBaseException {
	}

	public void generateResponseMessage(RequestVO p_requestVO) {

	}

	public void parseChannelRequestMessage(RequestVO p_requestVO, Connection pCon) throws BTSLBaseException {
		HttpServletRequest p_request=(HttpServletRequest)p_requestVO.getRequestMap().get("HTTPREQUEST");
		String requestType = p_request.getParameter("TYPE");

		if (LOG.isDebugEnabled())
			LOG.debug("parseChannelRequestMessage ABUSSDPlainStringParser.java", "Entered TYPE="
					+ requestType);
		String parsedRequestStr = null;
		String msisdn1 = null;
		String pin = null;
		String msisdn2 = null;
		String selector = null;
		String amount = null;
		try {
			if ("PROMORCTRFREQ".equals(requestType)
					|| "PRRCTRFREQ".equals(requestType)) {
				msisdn1 = p_request.getParameter("MSISDN");
				pin = p_request.getParameter("PIN");
				msisdn2 = p_request.getParameter("MSISDN2");
				amount = p_request.getParameter("AMOUNT");
				selector = p_request.getParameter("SELECTOR");
				if ((BTSLUtil.isNullString(msisdn1))
						|| (BTSLUtil.isNullString(msisdn2))
						|| (BTSLUtil.isNullString(pin))
						|| (BTSLUtil.isNullString(selector))) {
					throw new BTSLBaseException("USSDPlainStringParser",
							"parseChannelRequestMessage", "4323");
				}
				parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_PROMO_RECHARGE
						+ chnl_message_sep + msisdn2 + chnl_message_sep
						+ amount + chnl_message_sep + selector
						+ chnl_message_sep + pin;
				p_requestVO
						.setSenderLocale(LocaleMasterCache
								.getLocaleFromCodeDetails((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE))));
				p_requestVO.setReqSelector(selector);
				p_requestVO.setDecryptedMessage(parsedRequestStr);
				p_requestVO.setRequestMSISDN(msisdn1);
			}
			ChannelUserBL.updateUserInfo(pCon, p_requestVO);
		} catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			p_requestVO
					.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
			LOG.error("parseChannelRequestMessage", "Exception e: " + e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"USSDPlainStringParser[parseChannelRequestMessage]",
					p_requestVO.getTransactionID(), "", "", "Exception :"
							+ e.getMessage());
			throw new BTSLBaseException("USSDPlainStringParser",
					"parseChannelRequestMessage",
					PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT,e);
		} finally {
			if (LOG.isDebugEnabled())
				LOG.debug("parseChannelRequestMessage",
						"Exiting p_requestVO: " + p_requestVO.toString());
		}

	}
	public void generateChannelResponseMessage(RequestVO p_requestVO) {
		String contentType = p_requestVO.getReqContentType();
		if (LOG.isDebugEnabled())
			LOG.debug("generateChannelResponseMessage", "Transfer ID="
					+ p_requestVO.getRequestID() + " contentType: "
					+ contentType);
		try {
			if (contentType != null
					&& (p_requestVO.getReqContentType().indexOf("text") != -1 || p_requestVO
							.getReqContentType().indexOf("TEXT") != -1)) {
				// Set the Sender Return Message
				if (p_requestVO.getActionValue() == -1)
					actionChannelParser(p_requestVO);

				generateChannelResponse(p_requestVO.getActionValue(),
						p_requestVO);
			} else {
				String message = null;
				if (!BTSLUtil
						.isNullString(p_requestVO.getSenderReturnMessage()))
					message = p_requestVO.getSenderReturnMessage();
				else
					message = BTSLUtil.getMessage(p_requestVO.getLocale(),
							p_requestVO.getMessageCode(), p_requestVO
									.getMessageArguments());

				p_requestVO.setSenderReturnMessage(message);
			}
		} catch (Exception e) {
			LOG.error(METHOD_NAME, "generateChannelResponseMessage",
					" Exception while generating Response Message :" + e.getMessage());
			LOG.errorTrace(METHOD_NAME, e);
			
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"USSDParsers[generateChannelResponseMessage]", p_requestVO
							.getTransactionID(), "", "",
					"Exception getting message :" + e.getMessage());
			try {
				USSDStringParser.generateFailureResponse(p_requestVO);
			} catch (Exception ex) {
				LOG.error(METHOD_NAME, "Exception " + e);
			    LOG.errorTrace(METHOD_NAME,e);
				EventHandler
						.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
								EventStatusI.RAISED, EventLevelI.FATAL,
								"USSDParsers[generateChannelResponseMessage]",
								p_requestVO.getTransactionID(), "", "",
								"Exception getting default message :"
										+ ex.getMessage());
				p_requestVO.setSenderReturnMessage("TYPE=&TXNSTATUS="
						+ PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
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
	public void parseChannelRequest(int action, RequestVO p_requestVO)
			throws Exception {
		if (LOG.isDebugEnabled())
			LOG.debug("parseChannelRequest", "Entered Request ID="
					+ p_requestVO.getRequestID() + " action=" + action);

		switch (action) {
		case ACTION_CHNL_CREDIT_TRANSFER: {
			USSDStringParser.parseChannelCreditTransferRequest(p_requestVO);
			break;
		}
		case ACTION_CHNL_CHANGE_PIN: {
			USSDStringParser.parseChannelChangePinRequest(p_requestVO);
			break;
		}
		case ACTION_CHNL_NOTIFICATION_LANGUAGE: {
			USSDStringParser
					.parseChannelNotificationLanguageRequest(p_requestVO);
			break;
		}
		case ACTION_CHNL_TRANSFER_MESSAGE: {
			USSDStringParser.parseChannelTransferRequest(p_requestVO,
					ACTION_CHNL_TRANSFER_MESSAGE);
			break;
		}
		case ACTION_CHNL_WITHDRAW_MESSAGE: {
			USSDStringParser.parseChannelTransferRequest(p_requestVO,
					ACTION_CHNL_WITHDRAW_MESSAGE);
			break;
		}
		case ACTION_CHNL_RETURN_MESSAGE: {
			USSDStringParser.parseChannelTransferRequest(p_requestVO,
					ACTION_CHNL_RETURN_MESSAGE);
			break;
		}
		case ACTION_CHNL_BALANCE_ENQUIRY: {
			USSDStringParser.parseChannelBalanceEnquiry(p_requestVO);
			break;
		}
		case ACTION_CHNL_DAILY_STATUS_REPORT: {
			USSDStringParser.parseChannelDailyStatusReport(p_requestVO);
			break;
		}
		case ACTION_CHNL_LAST_TRANSFER_STATUS: {
			USSDStringParser.parseChannelLastTransferStatus(p_requestVO);
			break;
		}
		case ACTION_CHNL_C2S_LAST_XTRANSFER: // added for C2S Last N transfer
		{
			USSDStringParser.parseChannelLastXTransferStatus(p_requestVO);
			break;
		}
		case ACTION_CHNL_CUST_LAST_XTRANSFER: // added for C2S Last N transfer
		{
			USSDStringParser.parseChannelXEnquiryStatus(p_requestVO);
			break;
		}
		case ACTION_EXT_MVD_DWNLD_RQST: // to download MVD through USSD .. done
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

		case ACTION_CHNL_EXT_RECH_STATUS: // adde by ankuj for txn id based
											// enquiry.
		{
			USSDStringParser.parseChannelExtRechargeStatusRequest(p_requestVO);
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

	public static void generateChannelResponse(int action, RequestVO p_requestVO)
			throws Exception {
		if (LOG.isDebugEnabled())
			LOG.debug("generateChannelResponse", "Entered Request ID="
					+ p_requestVO.getRequestID() + " action=" + action);

		String messageCode = p_requestVO.getMessageCode();
		if ((!BTSLUtil.isNullString(messageCode))
				&& (!p_requestVO.isSuccessTxn())
				&& messageCode.indexOf("_") != -1) {
			messageCode = messageCode.substring(0, messageCode.indexOf("_"));
			p_requestVO.setMessageCode(messageCode);
		}
		switch (action) {
		case ACTION_CHNL_CREDIT_TRANSFER: {
			USSDStringParser.generateChannelCreditTransferResponse(p_requestVO);
			break;
		}
		case ACTION_CHNL_CHANGE_PIN: {
			USSDStringParser.generateChannelChangePinResponse(p_requestVO);
			break;
		}
		case ACTION_CHNL_NOTIFICATION_LANGUAGE: {
			USSDStringParser
					.generateChannelNotificationLanguageResponse(p_requestVO);
			break;
		}
		case ACTION_CHNL_TRANSFER_MESSAGE: {
			USSDStringParser.generateChannelTransferResponse(p_requestVO,
					ACTION_CHNL_TRANSFER_MESSAGE);
			break;
		}
		case ACTION_CHNL_WITHDRAW_MESSAGE: {
			USSDStringParser.generateChannelTransferResponse(p_requestVO,
					ACTION_CHNL_WITHDRAW_MESSAGE);
			break;
		}
		case ACTION_CHNL_RETURN_MESSAGE: {
			USSDStringParser.generateChannelTransferResponse(p_requestVO,
					ACTION_CHNL_RETURN_MESSAGE);
			break;
		}
		case ACTION_CHNL_BALANCE_ENQUIRY: {
			USSDStringParser.generateChannelBalanceEnquiryResponse(p_requestVO);
			break;
		}
		case ACTION_CHNL_DAILY_STATUS_REPORT: {
			USSDStringParser
					.generateChannelDailyStatusReportResponse(p_requestVO);
			break;
		}
		case ACTION_CHNL_LAST_TRANSFER_STATUS: {
			USSDStringParser
					.generateChannelLastTransferStatusResponse(p_requestVO);
			break;
		}
		case ACTION_CHNL_C2S_LAST_XTRANSFER: // last X C2S transfer
		{
			USSDStringParser
					.generateChannelLastXTransferStatusResponse(p_requestVO);
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

		case ACTION_CHNL_EXT_RECH_STATUS: {
			USSDStringParser
					.generateChannelExtRechargeStatusResponse(p_requestVO);
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
	public void checkRequestUnderProcess(Connection p_con,
			RequestVO p_requestVO, String p_module, boolean p_mark,
			ChannelUserVO channeluserVO) throws BTSLBaseException {
		if (LOG.isDebugEnabled())
			LOG.debug("checkRequestUnderProcess", "Entered Request ID="
					+ p_requestVO.getRequestID()
					+ " p_module="
					+ p_module
					+ " p_mark="
					+ p_mark
					+ " Check Required="
					+ p_requestVO.getMessageGatewayVO().getRequestGatewayVO()
							.getUnderProcessCheckReqd());
		try {
			if (TypesI.YES.equals(p_requestVO.getMessageGatewayVO()
					.getRequestGatewayVO().getUnderProcessCheckReqd())) {
				if (PretupsI.C2S_MODULE.equals(p_module)) {
					ChannelUserBL.checkRequestUnderProcessPOS(p_con,
							p_requestVO.getRequestIDStr(), channeluserVO
									.getUserPhoneVO(), p_mark);
				}

			}
		} catch (BTSLBaseException be) {
			throw be;
		} catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception" + e.getMessage());
			LOG.errorTrace(METHOD_NAME, e);
			throw new BTSLBaseException(this, "validateUserDetails",
					PretupsErrorCodesI.ERROR_EXCEPTION);
		}
		if (LOG.isDebugEnabled())
			LOG.debug("checkRequestUnderProcess", "Exiting For Request ID="
					+ p_requestVO.getRequestID());

	}

	public void parseChannelRequestMessage(Connection p_con,
			RequestVO p_requestVO) throws BTSLBaseException {
		String contentType = p_requestVO.getReqContentType();
		if (LOG.isDebugEnabled())
			LOG.debug("parseChannelRequestMessage", "Transfer ID="
					+ p_requestVO.getRequestID() + " contentType: "
					+ contentType);
		try {
			if (contentType != null
					&& (contentType.indexOf("text") != -1 || contentType
							.indexOf("TEXT") != -1)) {
				int action = actionChannelParser(p_requestVO);
				parseChannelRequest(action, p_requestVO);
				updateUserInfo(p_con, p_requestVO);
			} else {
				// Forward to plain message parsing
				// Set message Format , set in decrypted message
				p_requestVO
						.setDecryptedMessage(p_requestVO.getRequestMessage());
			}
			if (LOG.isDebugEnabled())
				LOG.debug("parseChannelRequestMessage", "Message ="
						+ p_requestVO.getDecryptedMessage() + " MSISDN="
						+ p_requestVO.getRequestMSISDN());
		} catch (BTSLBaseException be) {
			LOG.error("parseChannelRequestMessage",
					" BTSL Exception while parsing Request Message :"
							+ be.getMessage());
			throw be;
		} catch (Exception e) {
			LOG.error(METHOD_NAME, "parseChannelRequestMessage",
					"  Exception while parsing Request Message :" + e.getMessage());
			LOG.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"USSDParsers[parseChannelRequestMessage]", p_requestVO
							.getTransactionID(), "", "", "Exception :"
							+ e.getMessage());
			throw new BTSLBaseException("USSDParsers",
					"parseChannelRequestMessage",
					PretupsErrorCodesI.ERROR_EXCEPTION,e);
		}
	}

	/**
	 * Method to parse the Operator request
	 * 
	 * @param p_requestVO
	 * @throws BTSLBaseException
	 */
	public void parseOperatorRequestMessage(RequestVO p_requestVO)
			throws BTSLBaseException {
	}

}
