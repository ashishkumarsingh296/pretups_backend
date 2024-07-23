package com.btsl.pretups.gateway.parsers;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.btsl.blutil.BLConstants;
import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.ObjectProducer;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.login.LoginDAO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.gateway.util.ClientExtAPIXMLStringParserI;
import com.btsl.pretups.gateway.util.ExtAPIStringParser;
import com.btsl.pretups.gateway.util.ExtAPIXMLStringParser;
import com.btsl.pretups.gateway.util.ParserUtility;
import com.btsl.pretups.gateway.util.USSDC2SXMLStringParser;
import com.btsl.pretups.gateway.util.USSDP2PXMLStringParser;
import com.btsl.pretups.gateway.util.XMLStringParser;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.network.businesslogic.NetworkCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.ExtUserDAO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.XmlTagValueConstant;

/**
 * @(#)ExtAPIParsers.java
 *                        Copyright(c) 2006, Bharti Telesoft Ltd.
 *                        All Rights Reserved
 * 
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Author Date History
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Gurjeet Bedi Nov 16, 2006 Initial Creation
 *                        Kapil Mehta Feb 03, 2009 Modification
 *                        Parser class for external API Interfaces
 * 
 */

public class ExtAPIParsers extends ParserUtility {

    public static final Log LOG = LogFactory.getLog(ExtAPIParsers.class.getName());
    public static String CHNL_MESSAGE_SEP = null;
    static {
    	String chnlPlainSmsSeparator = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
        try {
            CHNL_MESSAGE_SEP = chnlPlainSmsSeparator;
            if (BTSLUtil.isNullString(CHNL_MESSAGE_SEP)) {
                CHNL_MESSAGE_SEP = " ";
            }
        } catch (Exception e) {
            LOG.errorTrace("static", e);
        }
    }

   	@Override
	public void parseRequestMessage(RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "parseRequestMessage";
        String contentType = p_requestVO.getReqContentType();
        if (LOG.isDebugEnabled()) {
            LOG.debug("parseRequestMessage", "Transfer ID=" + p_requestVO.getRequestID() + " contentType: " + contentType);
        }
        try {
            if (contentType != null && (contentType.indexOf("xml") != -1 || contentType.indexOf("XML") != -1)) {
                // Forward to XML parsing
            	String requestMessage=p_requestVO.getRequestMessage();
            	requestMessage=_operatorUtil.formatRequestXMLString(requestMessage);
            	p_requestVO.setRequestMessage(requestMessage);
            	
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
            if (LOG.isDebugEnabled()) {
                LOG.debug("parseRequestMessage", "Message =" + p_requestVO.getDecryptedMessage() + " MSISDN=" + p_requestVO.getRequestMSISDN());
            }
        } catch (BTSLBaseException be) {
            LOG.errorTrace(METHOD_NAME, be);
            LOG.error("parseRequestMessage", " BTSL Exception while parsing Request Message :" + be.getMessage());
            throw be;
        } catch (Exception e) {
            LOG.error("parseRequestMessage", "  Exception while parsing Request Message :" + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ExtAPIParsers[parseRequestMessage]", p_requestVO
                .getTransactionID(), "", "", "Exception :" + e.getMessage());
            throw new BTSLBaseException("ExtAPIParsers", "parseRequestMessage", PretupsErrorCodesI.ERROR_EXCEPTION,e);
        }
    }

    
		@Override
		public void generateResponseMessage(RequestVO p_requestVO) {
        final String METHOD_NAME = "generateResponseMessage";
        String contentType = p_requestVO.getReqContentType();
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Transfer ID=" + p_requestVO.getRequestID() + " contentType: " + contentType);
        }
        try {
        	boolean plainResParseRequired = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PLAIN_RES_PARSE_REQUIRED))).booleanValue();
            if (contentType != null && (p_requestVO.getReqContentType().indexOf("xml") != -1 || p_requestVO.getReqContentType().indexOf("XML") != -1)) {
                // Set the Sender Return Message
                if (p_requestVO.getActionValue() == -1) {
                    actionParser(p_requestVO);
                }
                generateResponse(p_requestVO.getActionValue(), p_requestVO);
            } else if (contentType != null && (p_requestVO.getReqContentType().indexOf("plain") != -1 || p_requestVO.getReqContentType().indexOf("PLAIN") != -1) && plainResParseRequired) {
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

                p_requestVO.setSenderReturnMessage(message);
            }
        } catch (Exception e) {
            LOG.error(METHOD_NAME, "  Exception while generating Response Message :" + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ExtAPIParsers[parseRequestMessage]", p_requestVO
                .getTransactionID(), "", "", "Exception getting message :" + e.getMessage());
            try {
                // XMLAPIParser.generateFailureResponse(p_requestVO);
		p_requestVO.setSuccessTxn(false);
            	p_requestVO.setMessageCode(e.getMessage());
                ExtAPIXMLStringParser.generateFailureResponse(p_requestVO);
            } catch (Exception ex) {
                LOG.errorTrace(METHOD_NAME, ex);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ExtAPIParsers[parseRequestMessage]", p_requestVO
                    .getTransactionID(), "", "", "Exception getting default message :" + ex.getMessage());
                p_requestVO
                    .setSenderReturnMessage("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND><TYPE></TYPE><TXNSTATUS>" + PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT + "</TXNSTATUS></COMMAND>");
            }
        }
    }

	@Override
	public void parseChannelRequestMessage(RequestVO p_requestVO, Connection pCon) throws BTSLBaseException {
        final String methodName = "parseChannelRequestMessage";
        String contentType = p_requestVO.getReqContentType();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Transfer ID = " + p_requestVO.getRequestID() + ", contentType : " + contentType);
        }
        try {
            if (contentType != null && (contentType.indexOf("xml") != -1 || contentType.indexOf("XML") != -1)) {
                // Forward to XML parsing
                // Set Filtered MSISDN, set _requestMSISDN
                // Set message Format , set in decrypted message
                // XMLAPIParser.actionChannelParser(p_requestVO);
            	
            	// Forward to XML parsing
            	String requestMessage=p_requestVO.getRequestMessage();
            	requestMessage=_operatorUtil.formatRequestXMLString(requestMessage);
            	p_requestVO.setRequestMessage(requestMessage);
            	
                int action = actionChannelParser(p_requestVO);
                parseChannelRequest(action, p_requestVO);
                ChannelUserBL.updateUserInfo(pCon, p_requestVO);
            } else {
                // Forward to plain message parsing
                // Set message Format , set in decrypted message
                p_requestVO.setDecryptedMessage(p_requestVO.getRequestMessage());
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Message = " + p_requestVO.getDecryptedMessage() + ", MSISDN = " + p_requestVO.getRequestMSISDN());
            }
        } catch (BTSLBaseException be) {
            LOG.errorTrace(methodName, be);
            LOG.error(methodName, " BTSLException while parsing Request Message : " + be.getMessage());
            throw be;
        } catch (Exception e) {
            LOG.error(methodName, "  Exception while parsing Request Message : " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ExtAPIParsers[parseChannelRequestMessage]",
                p_requestVO.getTransactionID(), "", "", "Exception :" + e.getMessage());
            throw new BTSLBaseException("ExtAPIParsers", methodName, PretupsErrorCodesI.ERROR_EXCEPTION,e);
        }
    }

   	@Override
	public void generateChannelResponseMessage(RequestVO p_requestVO) {
        final String METHOD_NAME = "generateChannelResponseMessage";
        String contentType = p_requestVO.getReqContentType();
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Transfer ID=" + p_requestVO.getRequestID() + " contentType: " + contentType);
        }
        try {

            if (contentType != null && (p_requestVO.getReqContentType().indexOf("xml") != -1 || p_requestVO.getReqContentType().indexOf("XML") != -1)) {
                // Set the Sender Return Message
                if (p_requestVO.getActionValue() == -1) {
                    actionChannelParser(p_requestVO);
                }
                generateChannelParserResponse(p_requestVO);
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
            LOG.error(METHOD_NAME, "  Exception while generating Response Message :" + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ExtAPIParsers[generateChannelResponseMessage]",
                p_requestVO.getTransactionID(), "", "", "Exception getting message :" + e.getMessage());
            try {
                // XMLAPIParser.generateFailureResponse(p_requestVO);
		   p_requestVO.setSuccessTxn(false);
            	  p_requestVO.setMessageCode(e.getMessage());
                ExtAPIXMLStringParser.generateFailureResponse(p_requestVO);
            } catch (Exception ex) {
                LOG.errorTrace(METHOD_NAME, ex);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ExtAPIParsers[generateChannelResponseMessage]",
                    p_requestVO.getTransactionID(), "", "", "Exception getting default message :" + ex.getMessage());
                p_requestVO
                    .setSenderReturnMessage("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND><TYPE></TYPE><TXNSTATUS>" + PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT + "</TXNSTATUS></COMMAND>");
            }
        }
    }

    /**
     * Method to validate the User Identifier , if MSISDN is there then only
     * validate
     * 
     * @param p_requestVO
     */
 	@Override
	public void validateUserIdentification(RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "validateUserIdentification";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered Request ID = " + p_requestVO.getRequestID() + ", RequestMSISDN() = " + p_requestVO.getRequestMSISDN());
        }
        // Validate user on the basis of values provided.
        // If MSISDN is there then validate the same.
        if (!BTSLUtil.isNullString(p_requestVO.getRequestMSISDN())) {
            validateMSISDN(p_requestVO);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting...");
        }
    }

    /**
     * Method to parse request of P2P on basis of action
     * 
     * @param action
     * @param p_requestVO
     * @throws Exception
     */
    public void parseRequest(int action, RequestVO p_requestVO) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("parseRequest", "Entered Request ID=" + p_requestVO.getRequestID() + " action=" + action);
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
    		case ADD_BUDDY:
    		{
    			USSDP2PXMLStringParser.parseAddBuddyRequest(p_requestVO);
    			break;
    		}
            case DELETE_BUDDY:	
			{
			    XMLStringParser.generateDeleteBuddyResponse(p_requestVO);
			    break;
			}

            case LIST_BUDDY:
			{
			    XMLStringParser.generateListBuddyResponse(p_requestVO);
			    break;
			}
            case ACTION_C2S_TRANS_ENQ:
                {
                    ExtAPIXMLStringParser.parseEXTC2STransferEnqRequest(p_requestVO);
                    break;
                }
            case P2P_LEND_ME_BALANCE:
                {
                    ExtAPIXMLStringParser.parseLendMeBalanceRequest(p_requestVO);
                    break;
                }
            // added by harsh for Scheduled Credit List (Add/Modify/Delete) API
            // on
            // 23 Apr 13
            case ACTION_MULT_CDT_TXR_SCDLIST_AMD:
                {
                    ExtAPIXMLStringParser.parseP2PSMCDAddModifyDeleteRequest(p_requestVO);
                    break;
                }
            // added by Pradyumn for scheduled multiple credit transfer list
            // delete
            case ACTION_MULT_CDT_TXR_SCDLIST_DLT:
                {
                    ExtAPIXMLStringParser.parseP2PSMCDDeleteListRequest(p_requestVO);
                    break;
                }
            // added by Pradyumn for scheduled multiple credit transfer list
            // view
            case ACTION_MULT_CDT_TXR_SCDLIST_VEW:
                {
                    ExtAPIXMLStringParser.parseP2PSMCDViewRequest(p_requestVO);
                    break;
                }
            case ACTION_VOUCHER_CONSUMPTION:
                {
                    ExtAPIXMLStringParser.parseVoucherConsumptionRequest(p_requestVO);
                    break;
                }
            case ACTION_VOUCHER_CONSUMPTION_O2C:
                {
                    ExtAPIXMLStringParser.parseVoucherConsumptionO2CRequest(p_requestVO);
                    break;
                }
            case ACTION_VOMS_ENQ: // added for voms
                {
                    ExtAPIXMLStringParser.parseVoucherEnqReq(p_requestVO);
                    break;
                }
            case ACTION_VOMS_CON: // added for voms
                {
                    ExtAPIXMLStringParser.parseVoucherConsReq(p_requestVO);
                    break;
                }
            case ACTION_VOMS_RET: // added for voms
                {
                    ExtAPIXMLStringParser.parseVoucherRetReq(p_requestVO);
                    break;
                }

            // added for voucher query and rollback request
            case ACTION_VOMS_QRY: // added for voms
                {
                    ExtAPIXMLStringParser.parseVoucherQueryReq(p_requestVO);
                    break;
                }
            case ACTION_VOMS_ROLLBACK: // added for voms
                {
                    ExtAPIXMLStringParser.parseVoucherRollBackReq(p_requestVO);
                    break;
                }

            case ACTION_VOMS_RETAGAIN: // added for get voucher again on the
                // basis
                // of transaction ID
                {
                    ExtAPIXMLStringParser.parseVoucherRetAgainReq(p_requestVO);
                    break;
                }
            // added for voucher query and rollback request
            case ACTION_VOMS_RETRIEVAL_ROLLBACK: // added for voms
                {
                    ExtAPIXMLStringParser.parseVoucherRetrievalRollBackReq(p_requestVO);
                    break;
                }
			case ACTION_VAS_VOUCHER_CONSUMPTION: 
			{
				ExtAPIXMLStringParser.parseVASVoucherConsumptionRequest(p_requestVO);
				break;	
			}
            case ACTION_VOUCHER_STATUS_CHANGES: 
            {
            	ExtAPIStringParser.parseExtStringRequest(p_requestVO);
            	p_requestVO.setRequestMessageArray(PretupsBL.parsePlainMessage(p_requestVO.getDecryptedMessage())); 
            	break;
            }
            case ACTION_VOUCHER_EXPIRY_CHANGES: 
            {
            	ExtAPIStringParser.parseExtStringRequest(p_requestVO);
            	p_requestVO.setRequestMessageArray(PretupsBL.parsePlainMessage(p_requestVO.getDecryptedMessage())); 
            	break;
            }
            case ACTION_DATA_CP2P_RECHARGE:
			{
				XMLStringParser.parseChannelExtCP2PDataTransferRequest(p_requestVO);
				break;
			}
            case P2P_GIVE_ME_BALANCE:
    		{
    			USSDP2PXMLStringParser.parseGiveMeBalanceRequest(p_requestVO);
    			break;
    		}
            case SUBSCRIBER_DEREGISTRATION:
    		{
    			USSDP2PXMLStringParser.parseSubscriberDeRegistrationRequest(p_requestVO);
    			break;
    		}
            case SELF_BAR:
    		{
    			USSDP2PXMLStringParser.parseSelfBarRequest(p_requestVO);
    			break;
    		}
            case LAST_TRANSFER_STATUS:
    		{
    			USSDP2PXMLStringParser.parseLastTransferStatus(p_requestVO);
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
    		case ACTION_REGISTER_SID: // added for C2S Last N transfer
    		{
    			USSDC2SXMLStringParser.parseChannelRegistrationRequest(p_requestVO);
    			break;
    		}
    		
    		case ACTION_ENQUIRY_SID_REQ: // added for SID enquiry
    		{
    			USSDC2SXMLStringParser.parseEnquirySIDRequest(p_requestVO);
    			break;
    		}
    		case ACTION_VMS_PIN_EXP_EXT_REQ:
			{
				ExtAPIXMLStringParser.parseVmsPinExpiryExt(p_requestVO);
                break;
			}
			
				/////////////////VHA START////////////////////////////////////
    		case ACTION_VOMS_VALIDATE_REQ: 
			{
				ClientExtAPIXMLStringParserI ClientExtAPIXMLStringParserI = (ClientExtAPIXMLStringParserI) ObjectProducer.getObject(BLConstants.Client_ExtAPIXMLS_tringParser_Obj, BLConstants.BL_PRODUCER);
				ClientExtAPIXMLStringParserI.parseVoucherValidateRequest(p_requestVO);
				break;	
			}
    		case ACTION_VOMS_RESERVE_REQ: 
			{
				ClientExtAPIXMLStringParserI ClientExtAPIXMLStringParserI = (ClientExtAPIXMLStringParserI) ObjectProducer.getObject(BLConstants.Client_ExtAPIXMLS_tringParser_Obj, BLConstants.BL_PRODUCER);
				ClientExtAPIXMLStringParserI.parseVoucherReserveRequest(p_requestVO);
				break;	
			}
    		case ACTION_VOMS_DIRECT_CONSUMPTION_REQ: 
			{
				ClientExtAPIXMLStringParserI ClientExtAPIXMLStringParserI = (ClientExtAPIXMLStringParserI) ObjectProducer.getObject(BLConstants.Client_ExtAPIXMLS_tringParser_Obj, BLConstants.BL_PRODUCER);
				ClientExtAPIXMLStringParserI.parseVoucherDirectConsumptionRequest(p_requestVO);
				break;	
			}
    		case ACTION_VOMS_DIRECT_ROLLBACK_REQ: 
			{
				ClientExtAPIXMLStringParserI ClientExtAPIXMLStringParserI = (ClientExtAPIXMLStringParserI) ObjectProducer.getObject(BLConstants.Client_ExtAPIXMLS_tringParser_Obj, BLConstants.BL_PRODUCER);
				ClientExtAPIXMLStringParserI.parseVoucherDirectRollbackRequest(p_requestVO);
				break;	
			}
    		case ACTION_VOMS_MY_VOUCHR_ENQUIRY_SUBSCRIBER_REQ: 
			{
				ExtAPIXMLStringParser.parseMyVoucherEnquirySubscriberRequest(p_requestVO);
				break;
			}
    		case ACTION_SUBS_THR_ENQ_REQUEST:
            {
            	ExtAPIXMLStringParser.parseSubscriberThresholdEnqRequest(p_requestVO);
                break;
            }
    		default:
      	     	 if(LOG.isDebugEnabled()){
      	     		LOG.debug("Default Value " ,action);
      	     	 }
			/////////////////VHA END////////////////////////////////////
        }
    }

    /**
     * Method to load and validate network details
     * 
     * @param p_requestVO
     * @throws BTSLBaseException
     */

  @Override
public void loadValidateNetworkDetails(RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "loadValidateNetworkDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered Request ID = " + p_requestVO.getRequestID());
        }

        try {
            NetworkVO networkVO = (NetworkVO) NetworkCache.getNetworkByExtNetworkCode(p_requestVO.getExternalNetworkCode());
            NetworkPrefixVO phoneNetworkPrefixVO = null;
            NetworkPrefixVO networkPrefixVO = null;
            // Also check if MSISDN is there then get the network Details from
            // it and match with network from external code
            if (networkVO != null && !BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN())) {
                phoneNetworkPrefixVO = PretupsBL.getNetworkDetails(p_requestVO.getFilteredMSISDN(), PretupsI.USER_TYPE_SENDER);
                if (!phoneNetworkPrefixVO.getNetworkCode().equals(networkVO.getNetworkCode())) {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NETWORK_CODE_MSIDN_NETWORK_MISMATCH);
                }
            } else if (networkVO == null) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXT_NETWORK_CODE);
            }
            if (phoneNetworkPrefixVO != null) {
                p_requestVO.setValueObject(phoneNetworkPrefixVO);
                validateNetwork(p_requestVO, phoneNetworkPrefixVO);
            } else {
                networkPrefixVO = new NetworkPrefixVO();
                networkPrefixVO.setNetworkCode(networkVO.getNetworkCode());
                networkPrefixVO.setNetworkName(networkVO.getNetworkName());
                networkPrefixVO.setNetworkShortName(networkVO.getNetworkShortName());
                networkPrefixVO.setCompanyName(networkVO.getCompanyName());
                networkPrefixVO.setErpNetworkCode(networkVO.getErpNetworkCode());
                networkPrefixVO.setStatus(networkVO.getStatus());
                networkPrefixVO.setLanguage1Message(networkVO.getLanguage1Message());
                networkPrefixVO.setLanguage2Message(networkVO.getLanguage2Message());
                networkPrefixVO.setModifiedOn(networkVO.getModifiedOn());
                networkPrefixVO.setModifiedTimeStamp(networkVO.getModifiedTimeStamp());
                p_requestVO.setValueObject(networkPrefixVO);
                validateNetwork(p_requestVO, networkPrefixVO);
            }
        } catch (BTSLBaseException be) {
            LOG.errorTrace(methodName, be);
            throw be;
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting Request ID = " + p_requestVO.getRequestID());
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

  
	@Override
	public ChannelUserVO loadValidateUserDetails(Connection p_con, RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "loadValidateUserDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered Request ID=" + p_requestVO.getRequestID() + "Action= " + p_requestVO.getActionValue());
        }

        ChannelUserVO channelUserVO = null;
        ChannelUserVO staffUserVO = null;
        boolean byPassCheck = false;
        // Load ChannelUser on basis of MSISDN or User Code depending on Action
        // or some standards need to be checked

        if (p_requestVO.getActionValue() == ACTION_VOMS_QRY||p_requestVO.getActionValue() ==ACTION_VOMS_RESERVE_REQ ||p_requestVO.getActionValue() ==ACTION_VOMS_DIRECT_CONSUMPTION_REQ   ||    p_requestVO.getActionValue() ==ACTION_VMS_PIN_EXP_EXT_REQ    ||p_requestVO.getActionValue() ==ACTION_VOMS_DIRECT_ROLLBACK_REQ || p_requestVO.getActionValue() ==ACTION_VOMS_VALIDATE_REQ ||p_requestVO.getActionValue() == ACTION_VOMS_ENQ || p_requestVO.getActionValue() == ACTION_EXT_GEOGRAPHY_REQUEST || p_requestVO.getActionValue() == ACTION_EXT_TRF_RULE_TYPE_REQ || p_requestVO.getActionValue() == ACTION_EXT_USERADD_REQUEST || p_requestVO.getActionValue()==ACTION_CRM_USER_AUTH_XML) {
            byPassCheck = true;
        } else if (p_requestVO.getActionValue() == ADD_USER_ACTION || p_requestVO.getActionValue() == MODIFY_USER_ACTION || p_requestVO.getActionValue() == DELETE_USER_ACTION || p_requestVO
            .getActionValue() == SUSPEND_RESUME_USER_ACTION || p_requestVO.getActionValue() == ADD_DELETE_USER_ROLE_ACTION || p_requestVO.getActionValue() == MNP_ACTION || p_requestVO
            .getActionValue() == ICCID_MSISDN_MAP_ACTION || p_requestVO.getActionValue() == CHANGE_PASSWORD_ACTION  ||p_requestVO.getActionValue()==ACTION_EXTPROMOPVAS_REQUEST || p_requestVO.getActionValue()==ACTION_EXTPROMOINTLTRFREQ_REQUEST ||p_requestVO.getActionValue()==ACTION_VOMS_QRY ||p_requestVO.getActionValue()==ACTION_VOMS_CON ||p_requestVO.getActionValue()==ACTION_VOMS_ROLLBACK ||p_requestVO.getActionValue()==ACTION_VOUCHER_STATUS_CHANGES ||p_requestVO.getActionValue()==ACTION_VOUCHER_EXPIRY_CHANGES ||p_requestVO.getActionValue()==ACTION_CHNL_DAILY_STATUS_REPORT ||p_requestVO.getActionValue()==ACTION_VOMS_RET) {
            byPassCheck = true;
        }
        // Ended Here
        try {
            if (BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN()) && BTSLUtil.isNullString(p_requestVO.getSenderExternalCode()) && BTSLUtil.isNullString(p_requestVO
                .getSenderLoginID()) && byPassCheck == false) {
                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_MISSING_SENDER_IDENTIFICATION);
            }
            // Start Moldova cahnges by Ved 24/07/07
            String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            OperatorUtilI operatorUtili = null;
            try {
                operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ExtAPIParsers[loadValidateUserDetails]", "", "",
                    "", "Exception while loading the class at the call:" + e.getMessage());
            }

            // End Moldova cahnges by Ved 24/07/07
            int st = p_requestVO.getActionValue();

            // Changing for passowrd security
            if(p_requestVO.getRequestMap()!=null){
            String passwordtemp = (String) p_requestVO.getRequestMap().get("PASSWORD");
            try {
                passwordtemp = operatorUtili.decryptPINPassword(passwordtemp);
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            p_requestVO.getRequestMap().put("PASSWORD", passwordtemp);
            }
            String chnlPlainSmsSeparator = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
            switch (p_requestVO.getActionValue()) {
                case ACTION_CHNL_O2C_INITIATE:
                case ACTION_CHNL_O2C_INITIATE_TRFR:
                case ACTION_CHNL_O2C_RETURN:
                case ACTION_O2C_SAP_ENQUIRY:
                case ACTION_O2C_SAP_EXTCODE_UPDATE:
                case ACTION_CHNL_O2C_WITHDRAW:
                case ACTION_VOMS_O2C:
                    {
                        // Load with help of MSISDN and User Code
                        if (!BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN()) && !BTSLUtil.isNullString(p_requestVO.getSenderExternalCode())) {
                            channelUserVO = _channelUserDAO.loadChnlUserDetailsByMsisdnExtCode(p_con, p_requestVO.getFilteredMSISDN(), BTSLUtil.NullToString(
                                p_requestVO.getSenderExternalCode()).trim());
                        } else if (!BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN())) {
                            channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getFilteredMSISDN());
                        } else {
                            channelUserVO = _channelUserDAO.loadChnlUserDetailsByExtCode(p_con, BTSLUtil.NullToString(p_requestVO.getSenderExternalCode()).trim());
                            p_requestVO.setFilteredMSISDN(channelUserVO.getUserPhoneVO().getMsisdn());
                            if (!p_requestVO.getRequestNetworkCode().equals(channelUserVO.getNetworkID())) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.NETWORK_CODE_MSIDN_NETWORK_MISMATCH);
                            }
                        }
                        
                        if(channelUserVO==null)
                            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CHNL_ERROR_NO_SUCH_USER);

                        if (!p_requestVO.getMessageGatewayVO().isUserAuthorizationReqd()) {
                            p_requestVO.setPinValidationRequired(false);
                        } else if (BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN())) {
                            p_requestVO.setPinValidationRequired(false);
                        }

                        // Added By Diwakar on 28-FEB-2014
                        if (BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("MSISDN")) || !BTSLUtil.isNullString(p_requestVO.getSenderLoginID())) {
                            p_requestVO.setPinValidationRequired(false);
                        }
                        Locale locale = new Locale(channelUserVO.getUserPhoneVO().getPhoneLanguage(), channelUserVO.getUserPhoneVO().getCountry());
                        p_requestVO.setSenderLocale(locale);
                        // Ended Here
						
						//Krishna                        
                        if("N".equals(Constants.getProperty("PIN_VALIDATION_FOR_O2C"))) {
                        	p_requestVO.setPinValidationRequired(false);
                        }

                        // Added By Diwakar on 25-FEB-2014
                        if (channelUserVO != null) {
                            if (p_requestVO.getRequestMap() != null && !p_requestVO.getRequestMap().containsKey("PIN") || BTSLUtil.NullToString(
                                p_requestVO.getRequestMap().get("PIN").toString()).length() == 0) {
                                p_requestVO.getRequestMap().put("PIN", BTSLUtil.decryptText(channelUserVO.getUserPhoneVO().getSmsPin()));
                            }
                        }
                        
                        if(channelUserVO!=null && !BTSLUtil.isNullString(channelUserVO.getStatus()) && "S".equals(channelUserVO.getStatus()))
                        	throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CHNL_ERROR_SENDER_SUSPEND);

                        if(p_requestVO.getActionValue()==ACTION_CHNL_O2C_RETURN && channelUserVO!=null && !BTSLUtil.isNullString(channelUserVO.getOutSuspened()) && "Y".equals(channelUserVO.getOutSuspened()))
                        	throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_OUT_SUSPENDED);
                        // Ended Here
                        break;
                    }
                case ACTION_ACCOUNT_INFO:
                case CREDIT_TRANSFER:
                case CHANGE_PIN:
                case NOTIFICATION_LANGUAGE:
                case HISTORY_MESSAGE:
                    {
                        // Load ChannelUser on basis of MSISDN
                        channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getFilteredMSISDN());

                        if (!p_requestVO.getMessageGatewayVO().isUserAuthorizationReqd()) {
                            p_requestVO.setPinValidationRequired(false);
                        }

                        break;
                    }
                case ACTION_CHNL_EXT_POST_RECHARGE_STATUS:
                case ACTION_EXT_C2SCHANGEPIN_XML:
                    {
                        String password = (String) p_requestVO.getRequestMap().get("PASSWORD");
                        String extCode = (String) p_requestVO.getRequestMap().get("EXTCODE");
                        String networkID = (String) p_requestVO.getRequestMap().get("EXTNWCODE");// String
                        // networkID=p_requestVO.getRequestNetworkCode();
                        p_requestVO.setSenderLoginID((String) p_requestVO.getRequestMap().get("LOGINID"));
                        String loginID = p_requestVO.getSenderLoginID();
                        String remarks =(String)p_requestVO.getRequestMap().get("REMARKS");
                        // common parser changes start
                        p_requestVO.setExternalReferenceNum((String) p_requestVO.getRequestMap().get("EXTREFNUM"));
                        p_requestVO.setRequestMSISDN((String) p_requestVO.getRequestMap().get("MSISDN"));
                        p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails((String) p_requestVO.getRequestMap().get("LANGUAGE1")));
                        // common parser changes end
                        if (!BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN())) {
                            channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getFilteredMSISDN());
                        } else if (!BTSLUtil.isNullString(p_requestVO.getSenderLoginID())) {
                            channelUserVO = _channelUserDAO.loadChnlUserDetailsByLoginID(p_con, p_requestVO.getSenderLoginID());
                        } else if (!BTSLUtil.isNullString(p_requestVO.getSenderExternalCode())) {
                            channelUserVO = _channelUserDAO.loadChnlUserDetailsByExtCode(p_con, BTSLUtil.NullToString(p_requestVO.getSenderExternalCode()).trim());
                        }
                         

                        if (channelUserVO != null) {
                            if (!networkID.equalsIgnoreCase(channelUserVO.getNetworkID())) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
                            }
                            if (!BTSLUtil.isNullString(password) && !operatorUtili.validateTransactionPassword(channelUserVO, password)) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                            }
                            if (!BTSLUtil.isNullString(extCode) && !extCode.equalsIgnoreCase(channelUserVO.getExternalCode())) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE);
                            }
                            if (!BTSLUtil.isNullString(loginID)) {
                                if (!operatorUtili.validateTransactionPassword(channelUserVO, password)) {
                                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                                }
                                if (!loginID.equalsIgnoreCase(channelUserVO.getLoginID())) {
                                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
                                }
                            }
                        }
                        //
                        if (!p_requestVO.getMessageGatewayVO().isUserAuthorizationReqd()) {
                            p_requestVO.setPinValidationRequired(false);
                        } else if (BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN())) {
                            p_requestVO.setPinValidationRequired(false);
                        }
                        break;
                    }// added for external transfer bill payment
                case ACTION_CHNL_EXT_ROAM_RECHARGE_REVERSAL:
                case ACTION_C2S_PRE_PAID_REVERSAL:

                    {
                        String password = (String) p_requestVO.getRequestMap().get("PASSWORD");
                        String extCode = (String) p_requestVO.getRequestMap().get("EXTCODE");
                        String networkID = p_requestVO.getRequestNetworkCode();
                        String loginID = p_requestVO.getSenderLoginID();
                        // VFE
                        // if(!BTSLUtil.isNullString(p_requestVO.getSenderExternalCode()))
                        if (!BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN())) {
                            channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getFilteredMSISDN());
                        } else if (!BTSLUtil.isNullString(p_requestVO.getSenderLoginID())) {
                            channelUserVO = _channelUserDAO.loadChnlUserDetailsByLoginID(p_con, p_requestVO.getSenderLoginID());
                        }
                        if (!BTSLUtil.isNullString(p_requestVO.getSenderExternalCode())) {
                            channelUserVO = _channelUserDAO.loadChnlUserDetailsByExtCode(p_con, BTSLUtil.NullToString(p_requestVO.getSenderExternalCode()).trim());
                            if (BTSLUtil.NullToString(p_requestVO.getRequestMap().get("PIN").toString()).length() == 0) {
                                p_requestVO.getRequestMap().put("PIN", BTSLUtil.decryptText(channelUserVO.getUserPhoneVO().getSmsPin()));
                            }
                        }
                        if (channelUserVO == null && !BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN())) {
                            channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getFilteredMSISDN());
                        }
                        if (channelUserVO == null && !BTSLUtil.isNullString(p_requestVO.getSenderLoginID())) {
                            channelUserVO = _channelUserDAO.loadChnlUserDetailsByLoginID(p_con, p_requestVO.getSenderLoginID());
                        }

                        if (channelUserVO != null) {
                            if (!networkID.equalsIgnoreCase(channelUserVO.getNetworkID())) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
                            }
                            // change for moldova by Ved 24/07/07
                            if (!BTSLUtil.isNullString(password) && !operatorUtili.validateTransactionPassword(channelUserVO, password)) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                            }
                            if (!BTSLUtil.isNullString(extCode) && !extCode.equalsIgnoreCase(channelUserVO.getExternalCode())) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE);
                            }
                            if (!BTSLUtil.isNullString(loginID)) {
                                if (BTSLUtil.isNullString(password)) {
                                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                                }
                                // change for moldova by Ved 24/07/07
                                if (!operatorUtili.validateTransactionPassword(channelUserVO, password)) {
                                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                                }
                                if (!loginID.equalsIgnoreCase(channelUserVO.getLoginID())) {
                                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
                                }
                            }
                        } else {
                        	String errorDetails = null;
                            if (!BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN())) {
                                errorDetails = p_requestVO.getFilteredMSISDN();
                            } else if (!BTSLUtil.isNullString(extCode)) {
                                errorDetails = extCode;
                            } else if (!BTSLUtil.isNullString(loginID)) {
                                errorDetails = loginID;
                            }
                            String errArgs[] = { errorDetails };
                            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_USER_NOT_EXIST, 0, errArgs, null);
                        }
                        //
                        if (!p_requestVO.getMessageGatewayVO().isUserAuthorizationReqd()) {
                            p_requestVO.setPinValidationRequired(false);
                        } else if (BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN())) {
                            p_requestVO.setPinValidationRequired(false);
                        }

                        // Added By Diwakar on 26-FEB-2014
                        if (BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("MSISDN")) || !BTSLUtil.isNullString(p_requestVO.getSenderLoginID())) {
                            p_requestVO.setPinValidationRequired(false);
                        }
                        // Ended Here
                        String CHNL_MESSAGE_SEP = chnlPlainSmsSeparator;
                        if (BTSLUtil.isNullString(CHNL_MESSAGE_SEP)) {
                            CHNL_MESSAGE_SEP = " ";
                        }
                        String message = p_requestVO.getDecryptedMessage();
                        if (!p_requestVO.isPinValidationRequired()) {
                            if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("PIN"))) {
                                message = message + CHNL_MESSAGE_SEP + (String) p_requestVO.getRequestMap().get("PIN");
                            } else {
                                message = message + CHNL_MESSAGE_SEP + BTSLUtil.decryptText(channelUserVO.getUserPhoneVO().getSmsPin());
                            }
                        } else {
                            if (BTSLUtil.NullToString(p_requestVO.getRequestMap().get("PIN").toString()).length() == 0) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.C2S_PIN_BLANK);
                            }
                        }
                        p_requestVO.setDecryptedMessage(message);
                        break;
                    }
                case ACTION_CHNL_EXT_ENQUIRY_REQUEST:
                case ACTION_CHNL_EXT_TRANSFER_BILL_PAYMENT:
                case ACTION_CHNL_EXT_COMMON_RECHARGE:
                case ACTION_CHNL_GIFT_RECHARGE_XML:
                case ACTION_CHNL_EVD_XML:
                case ACTION_CHNL_EXT_CREDIT_TRANSFER_CDMA:// For Bank API
                case ACTION_CHNL_EXT_CREDIT_TRANSFER_PSTN:// For Bank API
                case ACTION_CHNL_EXT_CREDIT_TRANSFER_INTR:// For Bank API
                case ACTION_C2C_TRANSFER_EXT_XML:
                case ACTION_C2C_WITHDRAW_EXT_XML:
                case ACTION_C2C_RETURN_EXT_XML:
                case ACTION_EXTVAS_RC_REQUEST: // VASTRIX ADDED BY HITESH
                case ACTION_EXTPVAS_RC_REQUEST: // VASTRIX ADDED BY HITESH
                case ACTION_CHNL_EXT_VAS_SELLING:// for CRBT
                case ACTION_EXT_PRIVATERC_XML: // For Private Recharge
                case ACTION_CHNL_EVR_XML: // For EVR
                case ACTION_CHNL_EVR_UMNIAH_XML:
                case ACTION_EXT_SUBENQ:// added by sonali for external
                    // subscriber
                    // enquiry.
                case ACTION_EXT_HLPDESK_REQUEST: // HelpDesk Services
                case ACTION_CHNL_POSTPAID_BILLPAYMENT: // PPB Services
                case ACTION_EXT_PPBENQ:
                case ACTION_CHNL_EXT_CREDIT_TRANSFER:
                case ACTION_VOU_PRF_MOD_REQ:   
                case ACTION_CHNL_DAILY_STATUS_REPORT:
                case ACTION_EXDATATRFREQ_REQUEST:
                case ACTION_C2C_REQ_REC:
                case ACTION_C2C_APPR:
                case ACTION_CHNL_DVD_XML:
                case ACTION_C2C_VOMS_TRF:
                case ACTION_C2C_VOMS_INI:
                case ACTION_VOUCHER_EXPIRY_CHANGES:
                case TOTAL_USER_INCOME_DETAILS_VIEW:
				case ACTION_CHNL_EXT_WARRANTY_TRANSFER:
				case ACTION_CHNL_EXT_ADVANCE_TRANSFER:
				case ACTION_CHNL_EXT_CAUTION_TRANSFER:
				case ACTION_CRM_USER_AUTH_XML:
				//case ACTION_VOUCHER_STATUS_CHANGES:

                    {
                        String password = (String) p_requestVO.getRequestMap().get("PASSWORD");
                        String extCode = (String) p_requestVO.getRequestMap().get("EXTCODE");
                        String networkID = p_requestVO.getRequestNetworkCode();
                        String loginID = p_requestVO.getSenderLoginID();
                        String errorDetails = null;
                        // VFE
                        // if(!BTSLUtil.isNullString(p_requestVO.getSenderExternalCode()))
                        if (!BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN())) {
                        	errorDetails = p_requestVO.getFilteredMSISDN();
                            channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getFilteredMSISDN());
                        } else if (!BTSLUtil.isNullString(p_requestVO.getSenderLoginID())) {
                        	errorDetails = p_requestVO.getSenderLoginID();
                            channelUserVO = _channelUserDAO.loadChnlUserDetailsByLoginID(p_con, p_requestVO.getSenderLoginID());
                        }
                        if (!BTSLUtil.isNullString(p_requestVO.getSenderExternalCode())) {
                        	errorDetails = p_requestVO.getSenderExternalCode();
                            channelUserVO = _channelUserDAO.loadChnlUserDetailsByExtCode(p_con, BTSLUtil.NullToString(p_requestVO.getSenderExternalCode()).trim());
                            if (BTSLUtil.NullToString(p_requestVO.getRequestMap().get("PIN").toString()).length() == 0) {
                                p_requestVO.getRequestMap().put("PIN", BTSLUtil.decryptText(channelUserVO.getUserPhoneVO().getSmsPin()));
                            }
                        }
                        if (channelUserVO == null && !BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN())) {
                            channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getFilteredMSISDN());
                        }
                        if (channelUserVO == null && !BTSLUtil.isNullString(p_requestVO.getSenderLoginID())) {
                            channelUserVO = _channelUserDAO.loadChnlUserDetailsByLoginID(p_con, p_requestVO.getSenderLoginID());
                        }

                        if (channelUserVO != null) {
                            if (!networkID.equalsIgnoreCase(channelUserVO.getNetworkID())) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
                            }
                            if(p_requestVO.getActiverUserId().equalsIgnoreCase(channelUserVO.getUserID())){
                                if (!BTSLUtil.isNullString(password) && !operatorUtili.validateTransactionPassword(channelUserVO, password)) {
                                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                                }
                                if (!BTSLUtil.isNullString(extCode) && !extCode.equalsIgnoreCase(channelUserVO.getExternalCode())) {
                                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE);
                                }
                                if (!BTSLUtil.isNullString(loginID)) {
                                    if (BTSLUtil.isNullString(password)) {
                                        throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                                    }
                                    // change for moldova by Ved 24/07/07
                                    if (!operatorUtili.validateTransactionPassword(channelUserVO, password)) {
                                        throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                                    }
                                    if (!loginID.equalsIgnoreCase(channelUserVO.getLoginID())) {
                                        throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
                                    }
                                }
                              } else {
                            if (!BTSLUtil.isNullString(password) && !operatorUtili.validateTransactionPassword(channelUserVO, password)) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                            }
                            if (!BTSLUtil.isNullString(extCode) && !extCode.equalsIgnoreCase(channelUserVO.getExternalCode())) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE);
                            }
                            if (!BTSLUtil.isNullString(loginID)) {
                                if (!operatorUtili.validateTransactionPassword(channelUserVO, password)) {
                                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                                }
                                if(BTSLUtil.isNullString(password)){
                                	 throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                                }
                                if (!loginID.equalsIgnoreCase(channelUserVO.getLoginID())) {
                                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
                                }
                            }
                          }
                        } else {
                        	    
                        	 
                        	String errArgs[] = { errorDetails };
                            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_USER_NOT_EXIST, 0, errArgs, null);
                        }
                        //
                        if (!p_requestVO.getMessageGatewayVO().isUserAuthorizationReqd()) {
                            p_requestVO.setPinValidationRequired(false);
                        } else if (BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN())) {
                            p_requestVO.setPinValidationRequired(false);
                        }

                        // Added By Diwakar on 26-FEB-2014
                        if (BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("MSISDN")) || !BTSLUtil.isNullString(p_requestVO.getSenderLoginID())) {
                            p_requestVO.setPinValidationRequired(false);
                        }
                        // Ended Here
                        String CHNL_MESSAGE_SEP = chnlPlainSmsSeparator;
                        if (BTSLUtil.isNullString(CHNL_MESSAGE_SEP)) {
                            CHNL_MESSAGE_SEP = " ";
                        }
                        String message = p_requestVO.getDecryptedMessage();
                        if (!p_requestVO.isPinValidationRequired()) {
                            if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("PIN"))) {
                                message = message + CHNL_MESSAGE_SEP + (String) p_requestVO.getRequestMap().get("PIN");
                            } else {
                                message = message + CHNL_MESSAGE_SEP + BTSLUtil.decryptText(channelUserVO.getUserPhoneVO().getSmsPin());
                            }
                        } else {
                            if (BTSLUtil.NullToString(p_requestVO.getRequestMap().get("PIN").toString()).length() == 0) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.C2S_PIN_BLANK);
                            }
                            message = message + CHNL_MESSAGE_SEP + (String) p_requestVO.getRequestMap().get("PIN");
                        }
                        p_requestVO.setDecryptedMessage(message);
                        break;
                    }
                    
                
                case ACTION_COL_ENQ:
                
                case ACTION_COL_BILLPAYMENT:
                case ACTION_DTH:
                case ACTION_DC:
                case ACTION_BPB:
                case ACTION_PMD:
                case ACTION_FLRC:
                case ACTION_C2S_POSTPAID_REVERSAL:
                case ACTION_CHNL_CARDGROUP_ENQUIRY_REQUEST:
                //case ACTION_CHNL_EXT_WARRANTY_TRANSFER: // Added By Narendra
                //case ACTION_CHNL_EXT_ADVANCE_TRANSFER:
                //case ACTION_CHNL_EXT_CAUTION_TRANSFER:
                case ACTION_VOUCHER_CONSUMPTION:
                case ACTION_VOUCHER_CONSUMPTION_O2C:
                case ACTION_PIN:
                case ACTION_CHNL_LITE_RECHARGE:	/*for lite recharge*/
                case GET_CHNL_USR_INFO:
                    {
                        String password = (String) p_requestVO.getRequestMap().get("PASSWORD");
                        String extCode = (String) p_requestVO.getRequestMap().get("EXTCODE");
                        String networkID = p_requestVO.getRequestNetworkCode();
                        String loginID = p_requestVO.getSenderLoginID();
                        if (!BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN())) {
                            channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getFilteredMSISDN());
                        } else if (!BTSLUtil.isNullString(p_requestVO.getSenderLoginID())) {
                            channelUserVO = _channelUserDAO.loadChnlUserDetailsByLoginID(p_con, p_requestVO.getSenderLoginID());
                        }
                        if (!BTSLUtil.isNullString(p_requestVO.getSenderExternalCode())) {
                            channelUserVO = _channelUserDAO.loadChnlUserDetailsByExtCode(p_con, BTSLUtil.NullToString(p_requestVO.getSenderExternalCode()).trim());
                            if (BTSLUtil.NullToString(p_requestVO.getRequestMap().get("PIN").toString()).length() == 0) {
                                p_requestVO.getRequestMap().put("PIN", BTSLUtil.decryptText(channelUserVO.getUserPhoneVO().getSmsPin()));
                            }
                        }
                        if (channelUserVO == null && !BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN())) {
                            channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getFilteredMSISDN());
                        }
                        if (channelUserVO == null && !BTSLUtil.isNullString(p_requestVO.getSenderLoginID())) {
                            channelUserVO = _channelUserDAO.loadChnlUserDetailsByLoginID(p_con, p_requestVO.getSenderLoginID());
                        }

                        if (channelUserVO != null) {
                            if (!networkID.equalsIgnoreCase(channelUserVO.getNetworkID())) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
                            }
                            // change for moldova by Ved 24/07/07
                            if (!BTSLUtil.isNullString(password) && !operatorUtili.validateTransactionPassword(channelUserVO, password)) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                            }
                            if (!BTSLUtil.isNullString(extCode) && !extCode.equalsIgnoreCase(channelUserVO.getExternalCode())) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE);
                            }
                            if (!BTSLUtil.isNullString(loginID)) {
                                if (BTSLUtil.isNullString(password)) {
                                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                                }
                                // change for moldova by Ved 24/07/07
                                if (!channelUserVO.getUserID().equals(p_requestVO.getActiverUserId())) {
                                    staffUserVO = _channelUserDAO.loadChannelUserDetailsByUserId(p_con, p_requestVO.getActiverUserId(), channelUserVO.getUserID());
                                    if (!operatorUtili.validateTransactionPassword(staffUserVO, password) || BTSLUtil.isNullString(password)) {
                                        throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                                    }
                                    if (!loginID.equalsIgnoreCase(staffUserVO.getLoginID())) {
                                        throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
                                    }
                                } else {
                                    if (!operatorUtili.validateTransactionPassword(channelUserVO, password) || BTSLUtil.isNullString(password)) {
                                        throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                                    }
                                    if (!loginID.equalsIgnoreCase(channelUserVO.getLoginID())) {
                                        throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
                                    }

                                }
                            }
                        } else {
                            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_USER_NOT_EXIST);
                        }
                        //
                        if (!p_requestVO.getMessageGatewayVO().isUserAuthorizationReqd()) {
                            p_requestVO.setPinValidationRequired(false);
                        } else if (BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN())) {
                            p_requestVO.setPinValidationRequired(false);
                        }
                        String CHNL_MESSAGE_SEP = chnlPlainSmsSeparator;
                        if (BTSLUtil.isNullString(CHNL_MESSAGE_SEP)) {
                            CHNL_MESSAGE_SEP = " ";
                        }
                        break;
                    }
                case ACTION_USER_CARDGROUP_ENQUIRY_REQUEST:// For DrCr Transfer Through
                {

                    String password = (String) p_requestVO.getRequestMap().get("PASSWORD");
                    String extCode = (String) p_requestVO.getRequestMap().get("EXTCODE");
                    String networkID = p_requestVO.getRequestNetworkCode();
                    String loginID = p_requestVO.getSenderLoginID();
                    if (!BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN())) {
                        channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getFilteredMSISDN());
                    } else if (!BTSLUtil.isNullString(p_requestVO.getSenderLoginID())) {
                        channelUserVO = _channelUserDAO.loadChnlUserDetailsByLoginID(p_con, p_requestVO.getSenderLoginID());
                    }
                    if (!BTSLUtil.isNullString(p_requestVO.getSenderExternalCode())) {
                        channelUserVO = _channelUserDAO.loadChnlUserDetailsByExtCode(p_con, BTSLUtil.NullToString(p_requestVO.getSenderExternalCode()).trim());
                        if (BTSLUtil.NullToString(p_requestVO.getRequestMap().get("PIN").toString()).length() == 0) {
                            p_requestVO.getRequestMap().put("PIN", BTSLUtil.decryptText(channelUserVO.getUserPhoneVO().getSmsPin()));
                        }
                    }
                    if (channelUserVO == null && !BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN())) {
                        channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getFilteredMSISDN());
                    }
                    if (channelUserVO == null && !BTSLUtil.isNullString(p_requestVO.getSenderLoginID())) {
                        channelUserVO = _channelUserDAO.loadChnlUserDetailsByLoginID(p_con, p_requestVO.getSenderLoginID());
                    }

                    if (channelUserVO != null) {
                        if (!networkID.equalsIgnoreCase(channelUserVO.getNetworkID())) {
                            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
                        }
                        // change for moldova by Ved 24/07/07
                        if (!BTSLUtil.isNullString(password) && !operatorUtili.validateTransactionPassword(channelUserVO, password)) {
                            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                        }
                        if (!BTSLUtil.isNullString(extCode) && !extCode.equalsIgnoreCase(channelUserVO.getExternalCode())) {
                            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE);
                        }
                        if (!BTSLUtil.isNullString(loginID)) {
                            if (BTSLUtil.isNullString(password)) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                            }
                            // change for moldova by Ved 24/07/07
                            if (!channelUserVO.getUserID().equals(p_requestVO.getActiverUserId())) {
                                staffUserVO = _channelUserDAO.loadChannelUserDetailsByUserId(p_con, p_requestVO.getActiverUserId(), channelUserVO.getUserID());
                                if (!operatorUtili.validateTransactionPassword(staffUserVO, password) || BTSLUtil.isNullString(password)) {
                                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                                }
                                if (!loginID.equalsIgnoreCase(staffUserVO.getLoginID())) {
                                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
                                }
                            } else {
                                if (!operatorUtili.validateTransactionPassword(channelUserVO, password) || BTSLUtil.isNullString(password)) {
                                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                                }
                                if (!loginID.equalsIgnoreCase(channelUserVO.getLoginID())) {
                                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
                                }

                            }
                        }
                    } else {
                        throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_USER_NOT_EXIST);
                    }
                    //
                    if (!p_requestVO.getMessageGatewayVO().isUserAuthorizationReqd()) {
                        p_requestVO.setPinValidationRequired(false);
                    } else if (BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN())) {
                        p_requestVO.setPinValidationRequired(false);
                    }
                    String CHNL_MESSAGE_SEP = chnlPlainSmsSeparator;
                    if (BTSLUtil.isNullString(CHNL_MESSAGE_SEP)) {
                        CHNL_MESSAGE_SEP = " ";
                    }
                    break;
                
                }
                case ACTION_EXT_DRCR_C2C_CUSER:// For DrCr Transfer Through
                    // External
                    // Gateway
                    {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(METHOD_NAME, "abcdef p_requestVO.getTxnType()=" + p_requestVO.getTxnType());
                        }
                        String pin = (String) p_requestVO.getRequestMap().get("PIN");
                        String password = (String) p_requestVO.getRequestMap().get("PASSWORD");
                        String extCode = (String) p_requestVO.getRequestMap().get("EXTCODE");
                        String networkID = p_requestVO.getRequestNetworkCode();
                        String loginID = null;
                        String msisdn = null;
                        if (p_requestVO.getTxnType().equalsIgnoreCase(PretupsI.CREDIT)) {
                            loginID = p_requestVO.getSenderLoginID();
                            msisdn = p_requestVO.getFilteredMSISDN();
                            if (!BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN())) {
                                channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getFilteredMSISDN());
                            } else if (!BTSLUtil.isNullString(loginID)) {
                                channelUserVO = _channelUserDAO.loadChnlUserDetailsByLoginID(p_con, loginID);
                            } else if (!BTSLUtil.isNullString(p_requestVO.getSenderExternalCode())) {
                                channelUserVO = _channelUserDAO.loadChnlUserDetailsByExtCode(p_con, BTSLUtil.NullToString(p_requestVO.getSenderExternalCode()).trim());
                            }
                        } else if (p_requestVO.getTxnType().equalsIgnoreCase(PretupsI.DEBIT)) {
                            loginID = p_requestVO.getReceiverLoginID();
                            msisdn = p_requestVO.getReceiverMsisdn();
                            if (!BTSLUtil.isNullString(p_requestVO.getReceiverMsisdn())) {
                                channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getReceiverMsisdn());
                            } else if (!BTSLUtil.isNullString(loginID)) {
                                channelUserVO = _channelUserDAO.loadChnlUserDetailsByLoginID(p_con, loginID);
                            } else if (!BTSLUtil.isNullString(p_requestVO.getReceiverExtCode())) {
                                channelUserVO = _channelUserDAO.loadChnlUserDetailsByExtCode(p_con, BTSLUtil.NullToString(p_requestVO.getReceiverExtCode()).trim());
                            }
                        }

                        if (channelUserVO != null) {
                            if (!networkID.equalsIgnoreCase(channelUserVO.getNetworkID())) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
                            }
                            if (!BTSLUtil.isNullString(msisdn) && !BTSLUtil.isNullString(pin)) {
                                operatorUtili.validatePIN(p_con, channelUserVO, pin);
                            }
                            if (!BTSLUtil.isNullString(password) && !operatorUtili.validateTransactionPassword(channelUserVO, password)) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                            }
                            if (!BTSLUtil.isNullString(extCode) && !extCode.equalsIgnoreCase(channelUserVO.getExternalCode())) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE);
                            }
                            if (!BTSLUtil.isNullString(loginID)) {
                                if (!operatorUtili.validateTransactionPassword(channelUserVO, password)) {
                                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                                }
                                if (!loginID.equalsIgnoreCase(channelUserVO.getLoginID())) {
                                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
                                }
                            }
                        } else {
                            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_USER_NOT_EXIST);
                        }
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(METHOD_NAME, "abcdef channelUserVO1=" + channelUserVO);
                        }
                        if (!p_requestVO.getMessageGatewayVO().isUserAuthorizationReqd()) {
                            p_requestVO.setPinValidationRequired(false);
                        } else if (BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN())) {
                            p_requestVO.setPinValidationRequired(false);
                        }
                        String CHNL_MESSAGE_SEP = chnlPlainSmsSeparator;
                        if (BTSLUtil.isNullString(CHNL_MESSAGE_SEP)) {
                            CHNL_MESSAGE_SEP = " ";
                        }
                        String message = p_requestVO.getDecryptedMessage();
                        if (p_requestVO.getTxnType().equalsIgnoreCase(PretupsI.CREDIT)) {
                            if (!p_requestVO.isPinValidationRequired()) {
                                if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("PIN"))) {
                                    message = message + CHNL_MESSAGE_SEP + (String) p_requestVO.getRequestMap().get("PIN");
                                } else {
                                    message = message + CHNL_MESSAGE_SEP + BTSLUtil.decryptText(channelUserVO.getUserPhoneVO().getSmsPin());
                                }
                            } else {
                                if (BTSLUtil.NullToString(p_requestVO.getRequestMap().get("PIN").toString()).length() == 0) {
                                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.C2S_PIN_BLANK);
                                }
                                message = message + CHNL_MESSAGE_SEP + (String) p_requestVO.getRequestMap().get("PIN");
                            }
                        } else if (p_requestVO.getTxnType().equalsIgnoreCase(PretupsI.DEBIT)) {
                            loginID = p_requestVO.getSenderLoginID();
                            if (!BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN())) {
                                channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getFilteredMSISDN());
                            } else if (!BTSLUtil.isNullString(loginID)) {
                                channelUserVO = _channelUserDAO.loadChnlUserDetailsByLoginID(p_con, loginID);
                            } else if (!BTSLUtil.isNullString(p_requestVO.getSenderExternalCode())) {
                                channelUserVO = _channelUserDAO.loadChnlUserDetailsByExtCode(p_con, BTSLUtil.NullToString(p_requestVO.getSenderExternalCode()).trim());
                            }
                            message = message + CHNL_MESSAGE_SEP + BTSLUtil.decryptText(channelUserVO.getUserPhoneVO().getSmsPin());
                        }
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(METHOD_NAME, "abcdef channelUserVO2=" + channelUserVO);
                        }
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(METHOD_NAME, "abcdef message=" + message);
                        }
                        p_requestVO.setDecryptedMessage(message);
                        break;
                    }
                /*
                 * For Running Through External Gateway By Babu Kunwar
                 */
                case ACTION_EXT_LAST_TRF:
                case ACTION_C2C_O2C_TXN_STATUS:
                case ACTION_CHNL_BAL_ENQ_XML:
                case ACTION_EXT_OTHER_BAL_ENQ:
                case ACTION_EXT_LAST_XTRF_ENQ:
                case ACTION_EXT_CUSTOMER_ENQ_REQ:
                case ACTION_EXT_DAILY_STATUS_REPORT:
                case ACTION_ENQUIRY_TXNIDEXTCODEDATE:
                    {
                        String password = (String) p_requestVO.getRequestMap().get("PASSWORD");
                        String pwdCheckRequired = (String) p_requestVO.getRequestMap().get("PASSWORD_CHECK_REQUIRED");
                        String extCode = (String) p_requestVO.getRequestMap().get("EXTCODE");
                        String networkID = p_requestVO.getRequestNetworkCode();
                        String loginID = p_requestVO.getSenderLoginID();
                        String msisdn = (String) p_requestVO.getRequestMap().get("MSISDN");
                        if (!BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN())) {
                            channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getFilteredMSISDN());
                        } else if (!BTSLUtil.isNullString(p_requestVO.getSenderLoginID())) {
                            channelUserVO = _channelUserDAO.loadChnlUserDetailsByLoginID(p_con, p_requestVO.getSenderLoginID());
                        } else if (!BTSLUtil.isNullString(p_requestVO.getSenderExternalCode())) {
                            channelUserVO = _channelUserDAO.loadChnlUserDetailsByExtCode(p_con, BTSLUtil.NullToString(p_requestVO.getSenderExternalCode()).trim());
                        }

                        if (channelUserVO != null) {
                            // Added By Diwakar on 25-FEB-2014 for C2S related
                            // request
                         //   _log.debug(METHOD_NAME, "channelUserVO: SmsPin=" + p_requestVO.getFilteredMSISDN() + p_requestVO.getSenderLoginID());
                           // if (BTSLUtil.NullToString(p_requestVO.getRequestMap().get("PIN").toString()).length() == 0) {
                             //   p_requestVO.getRequestMap().put("PIN", BTSLUtil.decryptText(channelUserVO.getUserPhoneVO().getSmsPin()));
                            //}
                            // Ended Here
                            if (!networkID.equalsIgnoreCase(channelUserVO.getNetworkID())) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
                            }
                            // change for moldova by Ved 24/07/07
                            if (!BTSLUtil.isNullString(password) && !operatorUtili.validateTransactionPassword(channelUserVO, password)) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                            }
                            if (!BTSLUtil.isNullString(extCode) && !extCode.equalsIgnoreCase(channelUserVO.getExternalCode())) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE);
                            }
                            if (!BTSLUtil.isNullString(loginID)) {
                                // change for moldova by Ved 24/07/07
                                if (((!BTSLUtil.isNullString(pwdCheckRequired) && !("N".equalsIgnoreCase(pwdCheckRequired))) || BTSLUtil.isNullString(pwdCheckRequired)) && !operatorUtili
                                    .validateTransactionPassword(channelUserVO, password)) {
                                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                                }
                                if (!loginID.equalsIgnoreCase(channelUserVO.getLoginID())) {
                                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
                                }
                            }
                        } else {
                            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_USER_NOT_EXIST);
                        }
                        //
                        if (!p_requestVO.getMessageGatewayVO().isUserAuthorizationReqd()) {
                            p_requestVO.setPinValidationRequired(false);
                        } else if (BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN())) {
                            p_requestVO.setPinValidationRequired(false);
                        } else if (BTSLUtil.isNullString(p_requestVO.getRequestMSISDN())) {
                            p_requestVO.setPinValidationRequired(false);
                        } else if (BTSLUtil.isNullString(msisdn)) {
                            p_requestVO.setPinValidationRequired(false);
                        }
                        String CHNL_MESSAGE_SEP = chnlPlainSmsSeparator;
                        if (BTSLUtil.isNullString(CHNL_MESSAGE_SEP)) {
                            CHNL_MESSAGE_SEP = " ";
                        }
                        String message = p_requestVO.getDecryptedMessage();
                        if (!p_requestVO.isPinValidationRequired()) {
                            if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("PIN"))) {
                                message = message + CHNL_MESSAGE_SEP + (String) p_requestVO.getRequestMap().get("PIN");
                            } else {
                                message = message + CHNL_MESSAGE_SEP + BTSLUtil.decryptText(channelUserVO.getUserPhoneVO().getSmsPin());
                            }
                        } else {
                            if (BTSLUtil.NullToString(p_requestVO.getRequestMap().get("PIN").toString()).length() == 0) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.C2S_PIN_BLANK);
                            }
                            message = message + CHNL_MESSAGE_SEP + (String) p_requestVO.getRequestMap().get("PIN");
                        }
                        p_requestVO.setDecryptedMessage(message);
                        break;
                    }
                case ACTION_CHNL_EXT_RECH_STATUS:
					case ACTION_EXT_LAST_XTRF_SRVCWISE_ENQ: //Added for Last X Transfer Service Wise
                    {
                        String extCode = (String) p_requestVO.getRequestMap().get("EXTCODE");
                        // Added By Diwakar on 26-FEB-2014
                        String channelUserMSISDN = null;
                        if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("MSISDN"))) {
                            channelUserMSISDN = PretupsBL.getFilteredMSISDN((String) p_requestVO.getRequestMap().get("MSISDN"));
                        } else {
                            channelUserMSISDN = (String) p_requestVO.getRequestMap().get("MSISDN");
                        }
                        LOG.debug(METHOD_NAME, "channelUserMSISDN = " + channelUserMSISDN + " , p_requestVO.getFilteredMSISDN() = " + p_requestVO.getFilteredMSISDN());
                        // Ended Here

                        String password = (String) p_requestVO.getRequestMap().get("PASSWORD");
                        String networkID = p_requestVO.getRequestNetworkCode();
                        String loginID = p_requestVO.getSenderLoginID();
                        if (!BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN())) {
                            channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getFilteredMSISDN());
                        } else if (!BTSLUtil.isNullString(p_requestVO.getSenderLoginID())) {
                            channelUserVO = _channelUserDAO.loadChnlUserDetailsByLoginID(p_con, p_requestVO.getSenderLoginID());
                        } else if (!BTSLUtil.isNullString(p_requestVO.getSenderExternalCode())) {
                            channelUserVO = _channelUserDAO.loadChnlUserDetailsByExtCode(p_con, BTSLUtil.NullToString(p_requestVO.getSenderExternalCode()).trim());
                        }
                        if (channelUserVO != null &&PretupsI.USER_TYPE_CHANNEL.equalsIgnoreCase(channelUserVO.getUserType())) {
                            p_requestVO.setActiverUserId(channelUserVO.getUserID());
                      }
                        if (channelUserVO != null) {
                            // Added By Diwakar on 26-FEB-2014
                            if (!BTSLUtil.isNullString(channelUserMSISDN)) {
                                if (p_requestVO.getRequestMap() != null && BTSLUtil.NullToString(p_requestVO.getRequestMap().get("PIN").toString()).length() == 0) {
                                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.C2S_PIN_BLANK);
                                }
                            }
                            if (!BTSLUtil.isNullString(extCode)) {
                                if (p_requestVO.getRequestMap() != null && BTSLUtil.NullToString(p_requestVO.getRequestMap().get("PIN").toString()).length() == 0) {
                                    p_requestVO.getRequestMap().put("PIN", BTSLUtil.decryptText(channelUserVO.getUserPhoneVO().getSmsPin()));
                                }
                            }
                            // Ended Here
                            if (!networkID.equalsIgnoreCase(channelUserVO.getNetworkID())) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
                            }
                            if (!channelUserVO.getUserID().equals(p_requestVO.getActiverUserId())) {
                                staffUserVO = _channelUserDAO.loadChannelUserDetailsByUserId(p_con, p_requestVO.getActiverUserId(), channelUserVO.getUserID());
                            }
                            if (!BTSLUtil.isNullString(loginID)) {
                                if (!channelUserVO.getUserID().equals(p_requestVO.getActiverUserId())) {
                                    staffUserVO = _channelUserDAO.loadChannelUserDetailsByUserId(p_con, p_requestVO.getActiverUserId(), channelUserVO.getUserID());
                                    if (!operatorUtili.validateTransactionPassword(staffUserVO, password) || BTSLUtil.isNullString(password)) {
                                        throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                                    }
                                    if (!loginID.equalsIgnoreCase(staffUserVO.getLoginID())) {
                                        throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
                                    }
                                } else {
                                    if (!operatorUtili.validateTransactionPassword(channelUserVO, password) || BTSLUtil.isNullString(password)) {
                                        throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                                    }
                                    if (!loginID.equalsIgnoreCase(channelUserVO.getLoginID())) {
                                        throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
                                    }

                                }
                            }
                            if (!BTSLUtil.isNullString(extCode)) {
                                if (!channelUserVO.getUserID().equals(p_requestVO.getActiverUserId())) {
                                    staffUserVO = _channelUserDAO.loadChannelUserDetailsByUserId(p_con, p_requestVO.getActiverUserId(), channelUserVO.getUserID());
                                    if (!extCode.equalsIgnoreCase(staffUserVO.getExternalCode())) {
                                        throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE);
                                    }
                                } else {
                                    if (!extCode.equalsIgnoreCase(channelUserVO.getExternalCode())) {
                                        throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE);
                                    }
                                }
                            }
                            if (!BTSLUtil.isNullString(channelUserMSISDN)) {
                                if (!channelUserVO.getUserID().equals(p_requestVO.getActiverUserId())) {
                                    staffUserVO = _channelUserDAO.loadChannelUserDetailsByUserId(p_con, p_requestVO.getActiverUserId(), channelUserVO.getUserID());
                                    if (!PretupsBL.getFilteredMSISDN(staffUserVO.getMsisdn()).equalsIgnoreCase(channelUserMSISDN)) {
                                        throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.C2S_ERROR_INVALID_SENDER_MSISDN);
                                    }
                                } else {
                                    if (!p_requestVO.getFilteredMSISDN().equalsIgnoreCase(channelUserMSISDN)) {
                                        throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.C2S_ERROR_INVALID_SENDER_MSISDN);
                                    }
                                }
                                if (!p_requestVO.getMessageGatewayVO().isUserAuthorizationReqd()) {
                                    p_requestVO.setPinValidationRequired(false);
                                } else if (BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN())) {
                                    p_requestVO.setPinValidationRequired(true);
                                }
                                String CHNL_MESSAGE_SEP = chnlPlainSmsSeparator;
                                if (BTSLUtil.isNullString(CHNL_MESSAGE_SEP)) {
                                    CHNL_MESSAGE_SEP = " ";
                                }
                                String message = p_requestVO.getDecryptedMessage();
                                if (p_requestVO.isPinValidationRequired() && !BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("PIN"))) {
                                    if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("PIN"))) {
                                        message = message + CHNL_MESSAGE_SEP + (String) p_requestVO.getRequestMap().get("PIN");
                                    } else {
                                        message = message + CHNL_MESSAGE_SEP + BTSLUtil.decryptText(channelUserVO.getUserPhoneVO().getSmsPin());
                                    }
                                } else {
                                    if (BTSLUtil.NullToString(p_requestVO.getRequestMap().get("PIN").toString()).length() == 0) {
                                        throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.C2S_PIN_BLANK);
                                    }
                                    message = message + CHNL_MESSAGE_SEP + (String) p_requestVO.getRequestMap().get("PIN");
                                }
                                p_requestVO.setDecryptedMessage(message);
                            } else {
                                if (BTSLUtil.isNullString(channelUserMSISDN)) {
                                    String CHNL_MESSAGE_SEP = chnlPlainSmsSeparator;
                                    if (BTSLUtil.isNullString(CHNL_MESSAGE_SEP)) {
                                        CHNL_MESSAGE_SEP = " ";
                                    }
                                    String message = p_requestVO.getDecryptedMessage();
                                    message = message + CHNL_MESSAGE_SEP + BTSLUtil.decryptText(channelUserVO.getUserPhoneVO().getSmsPin());
                                    p_requestVO.setDecryptedMessage(message);
                                }
                            }
                        }
                        // Changes END By Babu Kunwar
                        else {
                        	 String errorDetails = null;
                             if (!BTSLUtil.isNullString(channelUserMSISDN)) {
                                 errorDetails = channelUserMSISDN;
                             } else if (!BTSLUtil.isNullString(extCode)) {
                                 errorDetails = extCode;
                             } else if (!BTSLUtil.isNullString(loginID)) {
                                 errorDetails = loginID;
                             }
                             String[] errString={errorDetails};
                            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_USER_NOT_EXIST, 0, errString, null);
                        }
                        break;
                    }
                case ACTION_C2S_TRANS_ENQ:
                    {
                        // Load ChannelUser on basis of MSISDN
                        channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getFilteredMSISDN());

                        if (!p_requestVO.getMessageGatewayVO().isUserAuthorizationReqd()) {
                            p_requestVO.setPinValidationRequired(false);
                        }

                        break;
                    }
                // added by gaurav pandey for suspend resume channel user
                // through
                // external gateway
                case ACTION_SUSPEND_RESUME_CUSR_EXTGW:
                    {
                        channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getFilteredMSISDN());
                       UserVO userVO= new UserDAO().loadUsersDetails(p_con, p_requestVO.getFilteredMSISDN());
                        if(BTSLUtil.isNullObject(channelUserVO) && userVO!=null)
                        		{
                        	channelUserVO = (ChannelUserVO) userVO;
                        	UserPhoneVO userPhoneVO = new UserDAO().loadUserPhoneVO(p_con, channelUserVO.getUserID());
                        	channelUserVO.setUserPhoneVO(userPhoneVO);
                        		}
                        String msisdn = (String) (p_requestVO.getRequestMap().get("MSISDN"));
                        if (!BTSLUtil.isNullString(msisdn)) {
                            if (p_requestVO.getRequestMap() != null && BTSLUtil.NullToString(p_requestVO.getRequestMap().get("PIN").toString()).length() == 0) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.C2S_PIN_BLANK);
                            }
                        }
                        
                        msisdn = (String) (p_requestVO.getRequestMap().get("MSISDN1"));
                        if (BTSLUtil.isNullString(msisdn)) {                        
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CCE_XML_ERROR_MISSING_MANDATORY_VALUE);                  
                        }
                        
                        String pin =(String) (p_requestVO.getRequestMap().get("PIN"));
                       if(!BTSLUtil.isNullString(pin))
                        {
                      		operatorUtili.validatePIN(p_con, channelUserVO, pin);
                            
                        }
                        
                        String actionSuspendResume = (String) (p_requestVO.getRequestMap().get("ACTION"));
                        
                        if(!BTSLUtil.isNullString(actionSuspendResume) && !actionSuspendResume.equals("S") && !actionSuspendResume.equals("R"))
                        	throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CCE_XML_ERROR_INVALID_ACTION_VALUE);
                        
						break;
                    }
                case ACTION_EXT_GEOGRAPHY_REQUEST:// BY ANUPAM MALVIYA
                    {
                        String extCode = (String) (p_requestVO.getRequestMap().get("EXTCODE"));
                        String msisdn = (String) (p_requestVO.getRequestMap().get("MSISDN"));
                        String loginId = (String) (p_requestVO.getRequestMap().get("LOGINID"));
                        String networkID = p_requestVO.getRequestNetworkCode();
                        String geoCode = (String) (p_requestVO.getRequestMap().get("GEOCODE"));
                        if (!BTSLUtil.isNullString(msisdn)) {
                            channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con, msisdn);
                        } else if (!BTSLUtil.isNullString(loginId)) {
                            channelUserVO = _channelUserDAO.loadChnlUserDetailsByLoginID(p_con, loginId);
                        } else if (!BTSLUtil.isNullString(extCode)) {
                            channelUserVO = _channelUserDAO.loadChnlUserDetailsByExtCode(p_con, extCode);
                        }

                        if (channelUserVO != null) {
                            if (!networkID.equalsIgnoreCase(channelUserVO.getNetworkID())) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
                            }
                            if (!BTSLUtil.isNullString(loginId)) {
                                if (!loginId.equalsIgnoreCase(channelUserVO.getLoginID())) {
                                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
                                }
                            }
                            if (!BTSLUtil.isNullString(extCode)) {
                                if (!extCode.equalsIgnoreCase(channelUserVO.getExternalCode())) {
                                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE);
                                }
                            }
                            if (!BTSLUtil.isNullString(msisdn)) {
                                if (!msisdn.equalsIgnoreCase(channelUserVO.getMsisdn())) {
                                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_MSISDN);
                                }
                            }
                            p_requestVO.getRequestMap().put("MSISDN", channelUserVO.getMsisdn());
                        } else if (channelUserVO == null && (!BTSLUtil.isNullString(msisdn) || !BTSLUtil.isNullString(extCode) || !BTSLUtil.isNullString(loginId))) {
                            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_GRPH_INVALID_PARENT);
                        } else if (BTSLUtil.isNullString(geoCode)) {
                            String errorDetails = null;
                            if (!BTSLUtil.isNullString(msisdn)) {
                                errorDetails = msisdn;
                            } else if (!BTSLUtil.isNullString(extCode)) {
                                errorDetails = extCode;
                            } else if (!BTSLUtil.isNullString(loginId)) {
                                errorDetails = loginId;
                            }
                            String errArgs[] = { errorDetails };
                            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_USER_NOT_EXIST, 0, errArgs, null);
                        }
                        break;
                    }
                case ACTION_EXT_TRF_RULE_TYPE_REQ:// BY ANUPAM MALVIYA
                    {
                        String extCode = (String) p_requestVO.getRequestMap().get("EXTCODE");
                        String channelUserMSISDN = (String) p_requestVO.getRequestMap().get("MSISDN");
                        String networkID = p_requestVO.getRequestNetworkCode();
                        String loginID = p_requestVO.getSenderLoginID();
                        if (!BTSLUtil.isNullString(channelUserMSISDN)) {
                            channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con, channelUserMSISDN);
                        } else if (!BTSLUtil.isNullString(p_requestVO.getSenderLoginID())) {
                            channelUserVO = _channelUserDAO.loadChnlUserDetailsByLoginID(p_con, p_requestVO.getSenderLoginID());
                        } else if (!BTSLUtil.isNullString(extCode)) {
                            channelUserVO = _channelUserDAO.loadChnlUserDetailsByExtCode(p_con, BTSLUtil.NullToString(extCode).trim());
                        }
                        if (channelUserVO != null) {
                            if (!networkID.equalsIgnoreCase(channelUserVO.getNetworkID())) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
                            }
                            if (!BTSLUtil.isNullString(loginID)) {
                                if (!loginID.equalsIgnoreCase(channelUserVO.getLoginID())) {
                                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
                                }
                            }
                            if (!BTSLUtil.isNullString(extCode)) {
                                if (!extCode.equalsIgnoreCase(channelUserVO.getExternalCode())) {
                                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE);
                                }
                            }
                            if (!BTSLUtil.isNullString(channelUserMSISDN)) {
                                if (!channelUserMSISDN.equals(channelUserVO.getMsisdn())) {
                                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.C2S_ERROR_INVALID_SENDER_MSISDN);
                                }
                            }
                        } else if (channelUserVO == null && (!BTSLUtil.isNullString(channelUserMSISDN) || !BTSLUtil.isNullString(extCode) || !BTSLUtil.isNullString(loginID))) {
                            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_GRPH_INVALID_PARENT);
                        }
                        break;
                    }
                case ACTION_EXT_USERADD_REQUEST:// BY ANUPAM MALVIYA
                    {
                        break;
                    }
                // Added By Diwakar
                case CHANGE_PASSWORD_ACTION:
                    {
                        p_requestVO.getMessageGatewayVO().getRequestGatewayVO().setUnderProcessCheckReqd("N");
                        String extNwCode = (String) p_requestVO.getRequestMap().get("EXTNWCODE");
                        String categoryCode = (String) p_requestVO.getRequestMap().get("CATCODE");
                        String empCode = (String) p_requestVO.getRequestMap().get("EMPCODE");

                        String senderMSISDN = (String) p_requestVO.getRequestMap().get("MSISDN");
                        String password = (String) p_requestVO.getRequestMap().get("PASSWORD");
                        String networkID = p_requestVO.getRequestNetworkCode();
                        String loginID = (String) p_requestVO.getRequestMap().get("LOGINID");
                        String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
                        String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
                        Locale locale = new Locale("en", "US");
                        try {
                            locale = new Locale(defaultLanguage, defaultCountry);
                        } catch (RuntimeException e) {
                            // TODO Auto-generated catch block
                            LOG.errorTrace(METHOD_NAME, e);
                            locale = new Locale("en", "en");
                        }
                        if (!(extNwCode == null) || (!(loginID == null) || !(senderMSISDN == null))) {
                            // checked name
                            ExtUserDAO _extUserDao = new ExtUserDAO();
                            channelUserVO = _extUserDao.loadChannelUserDetailsByMsisdnOrLoginIdOrEmpCodeWithExtNwCode(p_con, senderMSISDN, "", loginID, password, empCode,
                                extNwCode, categoryCode, "", locale);
                            if (channelUserVO == null) {
                                throw new BTSLBaseException(this, "loadChannelUserDetailsByMsisdnOrLoginIdOrEmpCodeWithExtNwCode", PretupsErrorCodesI.OPT_ERROR_NO_SUCH_USER);
                            }

                            GeographicalDomainDAO _geographyDAO = new GeographicalDomainDAO();
                            channelUserVO.setGeographicalAreaList(_geographyDAO.loadUserGeographyList(p_con, channelUserVO.getUserID(), channelUserVO.getNetworkID()));
                            // load domains
                            DomainDAO domainDAO = new DomainDAO();
                            channelUserVO.setDomainList(domainDAO.loadDomainListByUserId(p_con, channelUserVO.getUserID()));
                            UserPhoneVO phoneVO = new UserPhoneVO();
                            phoneVO.setPhoneLanguage(defaultLanguage);
                            phoneVO.setCountry(defaultCountry);
                            phoneVO.setMsisdn(channelUserVO.getMsisdn());
                            channelUserVO.setUserPhoneVO(phoneVO);
                            HashMap map = p_requestVO.getRequestMap();
                            map.put("CHNUSERVO", channelUserVO);
                            p_requestVO.setRequestMap(map);
                            // end
                        } else {
                            throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ERP_USER_NONE_PARAMETER);
                        }

                        ParserUtility.validateChannelUserVO(channelUserVO, senderMSISDN, staffUserVO, networkID, p_requestVO, loginID);
                        break;
                    }
                case ADD_USER_ACTION:
                    {
                        
                        channelUserVO= ParserUtility.validateAddUser(p_con, p_requestVO, channelUserVO, staffUserVO);
                        break;
                    }
                case ADD_DELETE_USER_ROLE_ACTION:
                case SUSPEND_RESUME_USER_ACTION:
                case MODIFY_USER_ACTION:
                    {
                        String existingUserMSISDN = (String) p_requestVO.getRequestMap().get("USERMSISDN");
                        UserDAO userDao = new UserDAO();
                        // Added on 21-02-2014
                        String parentCatCode = null;
                        if ((p_requestVO.getActionValue() == SUSPEND_RESUME_USER_ACTION) && !BTSLUtil.isNullString(existingUserMSISDN)) {
                            parentCatCode = userDao.userCategoryFromMSISDN(p_con, existingUserMSISDN);
                            // If parent category is not found that means
                            // whether the
                            // parent user or it's category is in prohibited
                            // state.
                            if (BTSLUtil.isNullString(parentCatCode)) {
                                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXTSYS_REQ_PARENT_CAT_NOT_ALLOWED);
                            }

                        } else if (p_requestVO.getActionValue() == ADD_DELETE_USER_ROLE_ACTION || p_requestVO.getActionValue() == MODIFY_USER_ACTION) {
                        	Boolean isExists = _channelUserDAO.isPhoneExists(p_con, existingUserMSISDN);
                        	if(!isExists)
                        	{
                        		throw new BTSLBaseException(this, "process", PretupsErrorCodesI.USER_MODIFY_INVALID_MSISDN);
                        	}
                            parentCatCode = userDao.userCategoryFromMSISDN(p_con, existingUserMSISDN);
                            
  
                            if (BTSLUtil.isNullString(parentCatCode)) {
                                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXTSYS_REQ_PARENT_CAT_NOT_ALLOWED);
                            }

                        }
                        // Ended Here on 21-02-2014

                        p_requestVO.getMessageGatewayVO().getRequestGatewayVO().setUnderProcessCheckReqd("N");
                        String extCode = (String) p_requestVO.getRequestMap().get("EXTNWCODE");
                        String senderMSISDN = (String) p_requestVO.getRequestMap().get("MSISDN");
                        String password = (String) p_requestVO.getRequestMap().get("PASSWORD");
                        String networkID = p_requestVO.getRequestNetworkCode();
                        String loginID = (String) p_requestVO.getRequestMap().get("LOGINID");
                        String empCode = (String) p_requestVO.getRequestMap().get("EMPCODE");
                        String message = p_requestVO.getDecryptedMessage();

                        String pin = null;
                        if (p_requestVO.getActionValue() == MODIFY_USER_ACTION || p_requestVO.getActionValue() == SUSPEND_RESUME_USER_ACTION) {
                            pin = (String) p_requestVO.getRequestMap().get("PIN"); // 03-MAR-2014
                        }
                        String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
                        String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
                        Locale locale = new Locale("en", "US");
                        try {
                            locale = new Locale(defaultLanguage, defaultCountry);
                        } catch (RuntimeException e) {
                            // TODO Auto-generated catch block
                            LOG.errorTrace(METHOD_NAME, e);
                            locale = new Locale("en", "en");
                        }
                        if (!(extCode == null) || (!(loginID == null) || !(senderMSISDN == null))) {
                            // checked name
                            ExtUserDAO _extUserDao = new ExtUserDAO();
                            if (p_requestVO.getActionValue() == ADD_DELETE_USER_ROLE_ACTION) {
                                String categoryCode = (String) p_requestVO.getRequestMap().get("CATCODE");
                                channelUserVO = _extUserDao.loadChannelUserDetailsByMsisdnOrLoginIdOrEmpCodeWithExtNwCode(p_con, senderMSISDN, pin, loginID, password,
                                    empCode, extCode, categoryCode, "", locale);
                            } else {
                                channelUserVO = _extUserDao.loadChannelUserDetailsByMsisdnOrLoginIdOrEmpCodeWithExtNwCode(p_con, senderMSISDN, pin, loginID, password,
                                    empCode, extCode, "", "", locale);
                            }
                            if (channelUserVO == null) {
                                throw new BTSLBaseException(this, "loadChannelUserDetailsByMsisdnOrLoginIdOrEmpCodeWithExtNwCode", PretupsErrorCodesI.OPT_ERROR_NO_SUCH_USER);
                            }

                            GeographicalDomainDAO _geographyDAO = new GeographicalDomainDAO();
                            channelUserVO.setGeographicalAreaList(_geographyDAO.loadUserGeographyList(p_con, channelUserVO.getUserID(), channelUserVO.getNetworkID()));
                            // load domains
                            DomainDAO domainDAO = new DomainDAO();
                            channelUserVO.setDomainList(domainDAO.loadDomainListByUserId(p_con, channelUserVO.getUserID()));
                            UserPhoneVO phoneVO = new UserPhoneVO();
                            phoneVO.setPhoneLanguage(defaultLanguage);
                            phoneVO.setCountry(defaultCountry);
                            phoneVO.setMsisdn(channelUserVO.getMsisdn());
                            phoneVO.setPinRequired(channelUserVO.getPinRequired());
                            phoneVO.setSmsPin(channelUserVO.getSmsPin());
                            channelUserVO.setUserPhoneVO(phoneVO);
                            // end
                        } else {
                            throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ERP_USER_NONE_PARAMETER);
                        }

                        validateChannelUserVO(channelUserVO, senderMSISDN, staffUserVO, networkID, p_requestVO, loginID);

                        break;
                    }
                case DELETE_USER_ACTION:
                    {
                        String existingUserMSISDN = (String) p_requestVO.getRequestMap().get("USERMSISDN");
                        UserDAO userDao = new UserDAO();
                       
                        p_requestVO.getMessageGatewayVO().getRequestGatewayVO().setUnderProcessCheckReqd("N");
                        String externalCode = (String) p_requestVO.getRequestMap().get("EXTCODE");
                        String categoryCode = (String) p_requestVO.getRequestMap().get("CATCODE");
                        String senderMSISDN = null;
                        String password = (String) p_requestVO.getRequestMap().get("PASSWORD");
                        String networkID = p_requestVO.getRequestNetworkCode();
                        String loginID = (String) p_requestVO.getRequestMap().get("LOGINID");
                        String empCode = (String) p_requestVO.getRequestMap().get("EMPCODE");
                        String pin = null;// 03-MAR-2014
                        String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
                        String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
                        Locale locale = new Locale("en", "US");
                        try {
                            locale = new Locale(defaultLanguage, defaultCountry);
                        } catch (RuntimeException e) {
                            // TODO Auto-generated catch block
                            LOG.errorTrace(METHOD_NAME, e);
                            locale = new Locale("en", "en");
                        }
                        if (!(externalCode == null) || (!(loginID == null) || !(senderMSISDN == null))) {
                            // checked name
                            ExtUserDAO _extUserDao = new ExtUserDAO();
                            channelUserVO = _extUserDao.loadChannelUserDetailsByMsisdnOrLoginIdOrEmpCodeWithExtNwCode(p_con, senderMSISDN, pin, loginID, password, empCode,
                                "", categoryCode, externalCode, locale);
                            if (channelUserVO == null) {
                                throw new BTSLBaseException(this, "loadChannelUserDetailsByMsisdnOrLoginIdOrEmpCodeWithExtNwCode", PretupsErrorCodesI.OPT_ERROR_NO_SUCH_USER);
                            }

                            GeographicalDomainDAO _geographyDAO = new GeographicalDomainDAO();
                            channelUserVO.setGeographicalAreaList(_geographyDAO.loadUserGeographyList(p_con, channelUserVO.getUserID(), channelUserVO.getNetworkID()));
                            // load domains
                            DomainDAO domainDAO = new DomainDAO();
                            channelUserVO.setDomainList(domainDAO.loadDomainListByUserId(p_con, channelUserVO.getUserID()));
                            UserPhoneVO phoneVO = new UserPhoneVO();
                            phoneVO.setPhoneLanguage(defaultLanguage);
                            phoneVO.setCountry(defaultCountry);
                            phoneVO.setMsisdn(channelUserVO.getMsisdn());
                            channelUserVO.setUserPhoneVO(phoneVO);
                            // end
                        } else {
                            throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ERP_USER_NONE_PARAMETER);
                        }

                        validateChannelUserVO(channelUserVO, senderMSISDN, staffUserVO, networkID, p_requestVO, loginID);
                        break;
                    }
                case MNP_ACTION:
                case ICCID_MSISDN_MAP_ACTION:
                    {
                        p_requestVO.getMessageGatewayVO().getRequestGatewayVO().setUnderProcessCheckReqd("N");
                        String extNwCode = (String) p_requestVO.getRequestMap().get("EXTNWCODE");
                        String categoryCode = (String) p_requestVO.getRequestMap().get("CATCODE");
                        String senderMSISDN = (String) p_requestVO.getRequestMap().get("MSISDN");
                        String loginID = (String) p_requestVO.getRequestMap().get("LOGINID");
                        String password = (String) p_requestVO.getRequestMap().get("PASSWORD");
                        String networkID = p_requestVO.getRequestNetworkCode();
                        String empCode = (String) p_requestVO.getRequestMap().get("EMPCODE");
                        String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
                        String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
                        Locale locale = new Locale("en", "en");
                        try {
                            locale = new Locale(defaultLanguage, defaultCountry);
                        } catch (RuntimeException e) {
                            // TODO Auto-generated catch block
                            LOG.errorTrace(METHOD_NAME, e);
                            locale = new Locale("en", "en");
                        }
                        if (!(extNwCode == null) || (!(loginID == null) || !(senderMSISDN == null))) {
                            // checked name
                            ExtUserDAO _extUserDao = new ExtUserDAO();
                            channelUserVO = _extUserDao.loadChannelUserDetailsByMsisdnOrLoginIdOrEmpCodeWithExtNwCode(p_con, senderMSISDN, "", loginID, password, empCode,
                                extNwCode, categoryCode, "", locale);
                            if (channelUserVO == null) {
                                throw new BTSLBaseException(this, "loadChannelUserDetailsByMsisdnOrLoginIdOrEmpCodeWithExtNwCode", PretupsErrorCodesI.OPT_ERROR_NO_SUCH_USER);
                            }

                            GeographicalDomainDAO _geographyDAO = new GeographicalDomainDAO();
                            channelUserVO.setGeographicalAreaList(_geographyDAO.loadUserGeographyList(p_con, channelUserVO.getUserID(), channelUserVO.getNetworkID()));
                            // load domains
                            DomainDAO domainDAO = new DomainDAO();
                            channelUserVO.setDomainList(domainDAO.loadDomainListByUserId(p_con, channelUserVO.getUserID()));
                            UserPhoneVO phoneVO = new UserPhoneVO();
                            phoneVO.setPhoneLanguage(defaultLanguage);
                            phoneVO.setCountry(defaultCountry);
                            phoneVO.setMsisdn(channelUserVO.getMsisdn());
                            channelUserVO.setUserPhoneVO(phoneVO);
                            // end
                        } else {
                            throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ERP_USER_NONE_PARAMETER);
                        }

                        validateChannelUserVO(channelUserVO, senderMSISDN, staffUserVO, networkID, p_requestVO, loginID);
                        break;
                    }
					case ACTION_EXTPROMOPVAS_REQUEST:
					{
						 String password =(String)p_requestVO.getRequestMap().get("PASSWORD");
					     String extCode =(String)p_requestVO.getRequestMap().get("EXTCODE");
					     String networkID=p_requestVO.getRequestNetworkCode();
						 String loginID=p_requestVO.getSenderLoginID();
						
						 if ( !BTSLUtil.isNullString(p_requestVO.getType())   && (p_requestVO.getType().equalsIgnoreCase("EXTPROMOVASTRFREQ") || p_requestVO.getType().equalsIgnoreCase("EXPROMORCTRFREQ")))
						 {
							 if(!BTSLUtil.isNullString(p_requestVO.getRequestMSISDN()))
							 {
								 if(BTSLUtil.NullToString(p_requestVO.getRequestMap().get("PIN").toString()).length()==0)
								 throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.C2S_PIN_BLANK);   
								 		
							}
						 }
						 
						 
						if(!BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN()))
							channelUserVO= _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getFilteredMSISDN());
						else if(!BTSLUtil.isNullString(p_requestVO.getSenderLoginID()))
							channelUserVO= _channelUserDAO.loadChnlUserDetailsByLoginID(p_con, p_requestVO.getSenderLoginID());
						 if(!BTSLUtil.isNullString(p_requestVO.getSenderExternalCode()))
							{
								channelUserVO= _channelUserDAO.loadChnlUserDetailsByExtCode(p_con, BTSLUtil.NullToString(p_requestVO.getSenderExternalCode()).trim());
								if(BTSLUtil.NullToString(p_requestVO.getRequestMap().get("PIN").toString()).length()==0)
								{
									p_requestVO.getRequestMap().put("PIN",BTSLUtil.decryptText(channelUserVO.getUserPhoneVO().getSmsPin()));
								}
							}
							if(channelUserVO==null&&!BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN()))
								channelUserVO= _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getFilteredMSISDN());
							if(channelUserVO==null&&!BTSLUtil.isNullString(p_requestVO.getSenderLoginID()))
								channelUserVO= _channelUserDAO.loadChnlUserDetailsByLoginID(p_con, p_requestVO.getSenderLoginID());

						if(channelUserVO!=null)
						{
							if (!networkID.equalsIgnoreCase(channelUserVO.getNetworkID()))
								throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
							if(!channelUserVO.getUserID().equals(p_requestVO.getActiverUserId()))
							{
								staffUserVO= _channelUserDAO.loadChannelUserDetailsByUserId(p_con, p_requestVO.getActiverUserId(),channelUserVO.getUserID());
							}
	                      //change for moldova by Ved 24/07/07
							/*if(!BTSLUtil.isNullString(password) && !operatorUtili.validateTransactionPassword(channelUserVO, password))
								throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PASSWORD);*/
							if (!BTSLUtil.isNullString(extCode) && !extCode.equalsIgnoreCase(channelUserVO.getExternalCode()))
								throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE);
							if(!BTSLUtil.isNullString(loginID))
							{
								if(BTSLUtil.isNullString(password))
									throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PASSWORD);
	                            //change for moldova by Ved 24/07/07
								if(!channelUserVO.getUserID().equals(p_requestVO.getActiverUserId()))
						    	{
						    		staffUserVO= _channelUserDAO.loadChannelUserDetailsByUserId(p_con, p_requestVO.getActiverUserId(),channelUserVO.getUserID());
							    	if(!operatorUtili.validateTransactionPassword(staffUserVO, password) || BTSLUtil.isNullString(password))
										throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PASSWORD);
							    	if(!loginID.equalsIgnoreCase(staffUserVO.getLoginID()))
										throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
						    	}
						    	else
						    	{
						    		if(!operatorUtili.validateTransactionPassword(channelUserVO, password) || BTSLUtil.isNullString(password))
										throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PASSWORD);
						    		if(!loginID.equalsIgnoreCase(channelUserVO.getLoginID()))
										throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
								
						    	}
							}
							channelUserVO.setGateway(PretupsI.REQUEST_SOURCE_TYPE_EXTGW);
						} else {
							throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.ERROR_USER_NOT_EXIST);    
						}
						//
						if(!p_requestVO.getMessageGatewayVO().isUserAuthorizationReqd())
							p_requestVO.setPinValidationRequired(false);
						else if(BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN()))
							p_requestVO.setPinValidationRequired(false);
						String CHNL_MESSAGE_SEP=chnlPlainSmsSeparator;
						if(BTSLUtil.isNullString(CHNL_MESSAGE_SEP))
							CHNL_MESSAGE_SEP=" ";
						//if(BTSLUtil.isNullString((String)p_requestVO.getRequestMap().get("MSISDN")) && !BTSLUtil.isNullString(p_requestVO.getSenderExternalCode())){
							String message=p_requestVO.getDecryptedMessage();
							String splitMsg[]=BTSLUtil.split(message,CHNL_MESSAGE_SEP);					
							message=message.substring(0,message.length()-(splitMsg[splitMsg.length-1].length()))+(String)p_requestVO.getRequestMap().get("PIN");						
							p_requestVO.setDecryptedMessage(message);
	                //}
						break;
						}
					case ACTION_EXTPROMOINTLTRFREQ_REQUEST:
						{
						 String password =(String)p_requestVO.getRequestMap().get("PASSWORD");
					     String extCode =(String)p_requestVO.getRequestMap().get("EXTCODE");
					     String networkID=p_requestVO.getRequestNetworkCode();
						 String loginID=p_requestVO.getSenderLoginID();
						 if ( !BTSLUtil.isNullString(p_requestVO.getType())   && (p_requestVO.getType().equalsIgnoreCase("EXTPROMOVASTRFREQ") || p_requestVO.getType().equalsIgnoreCase("EXPROMORCTRFREQ")))
						 {
							 if(!BTSLUtil.isNullString(p_requestVO.getRequestMSISDN()))
							 {
								 if(BTSLUtil.NullToString(p_requestVO.getRequestMap().get("PIN").toString()).length()==0)
								 throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.C2S_PIN_BLANK);   
							}
						 }
						if(!BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN()))
							channelUserVO= _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getFilteredMSISDN());
						else if(!BTSLUtil.isNullString(p_requestVO.getSenderLoginID()))
							channelUserVO= _channelUserDAO.loadChnlUserDetailsByLoginID(p_con, p_requestVO.getSenderLoginID());
						 if(!BTSLUtil.isNullString(p_requestVO.getSenderExternalCode()))
							{
								channelUserVO= _channelUserDAO.loadChnlUserDetailsByExtCode(p_con, BTSLUtil.NullToString(p_requestVO.getSenderExternalCode()).trim());
								if(BTSLUtil.NullToString(p_requestVO.getRequestMap().get("PIN").toString()).length()==0)
								{
									p_requestVO.getRequestMap().put("PIN",BTSLUtil.decryptText(channelUserVO.getUserPhoneVO().getSmsPin()));
								}
							}
							if(channelUserVO==null&&!BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN()))
								channelUserVO= _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getFilteredMSISDN());
							if(channelUserVO==null&&!BTSLUtil.isNullString(p_requestVO.getSenderLoginID()))
								channelUserVO= _channelUserDAO.loadChnlUserDetailsByLoginID(p_con, p_requestVO.getSenderLoginID());
						if(channelUserVO!=null)
						{
							if (!networkID.equalsIgnoreCase(channelUserVO.getNetworkID()))
								throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
							if(!channelUserVO.getUserID().equals(p_requestVO.getActiverUserId()))
							{
								staffUserVO= _channelUserDAO.loadChannelUserDetailsByUserId(p_con, p_requestVO.getActiverUserId(),channelUserVO.getUserID());
							}
							if (!BTSLUtil.isNullString(extCode) && !extCode.equalsIgnoreCase(channelUserVO.getExternalCode()))
								throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE);
							if(!BTSLUtil.isNullString(loginID))
							{
								if(BTSLUtil.isNullString(password))
									throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
								if(!channelUserVO.getUserID().equals(p_requestVO.getActiverUserId()))
						    	{
						    		staffUserVO= _channelUserDAO.loadChannelUserDetailsByUserId(p_con, p_requestVO.getActiverUserId(),channelUserVO.getUserID());
							    	if(!operatorUtili.validateTransactionPassword(staffUserVO, password) || BTSLUtil.isNullString(password))
										throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
							    	if(!loginID.equalsIgnoreCase(staffUserVO.getLoginID()))
										throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
						    	}
						    	else
						    	{
						    		if(!operatorUtili.validateTransactionPassword(channelUserVO, password) || BTSLUtil.isNullString(password))
										throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
						    		if(!loginID.equalsIgnoreCase(channelUserVO.getLoginID()))
										throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
						    	}
							}
							channelUserVO.setGateway(PretupsI.REQUEST_SOURCE_TYPE_EXTGW);
						} else {
							throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_USER_NOT_EXIST);    
						}
						if(!p_requestVO.getMessageGatewayVO().isUserAuthorizationReqd())
							p_requestVO.setPinValidationRequired(false);
						else if(BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN()))
							p_requestVO.setPinValidationRequired(false);
						String CHNL_MESSAGE_SEP=chnlPlainSmsSeparator;
						if(BTSLUtil.isNullString(CHNL_MESSAGE_SEP))
							CHNL_MESSAGE_SEP=" ";
						if(BTSLUtil.isNullString((String)p_requestVO.getRequestMap().get("MSISDN")) && !BTSLUtil.isNullString(p_requestVO.getSenderExternalCode())){
							String message=p_requestVO.getDecryptedMessage();
							String splitMsg[]=BTSLUtil.split(message,CHNL_MESSAGE_SEP);					
							message=message.substring(0,message.length()-(splitMsg[splitMsg.length-1].length()))+(String)p_requestVO.getRequestMap().get("PIN");						
							p_requestVO.setDecryptedMessage(message);
	                }
						break;
					}
                case ACTION_CHNL_EXT_ROAM_RECHARGE:
                    {
                        String password = (String) p_requestVO.getRequestMap().get("PASSWORD");
                        String extCode = (String) p_requestVO.getRequestMap().get("EXTCODE");
                        String networkID = p_requestVO.getRequestNetworkCode();
                        String loginID = p_requestVO.getSenderLoginID();
                        if (!BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN())) {
                            channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getFilteredMSISDN());
                        } else if (!BTSLUtil.isNullString(p_requestVO.getSenderLoginID())) {
                            channelUserVO = _channelUserDAO.loadChnlUserDetailsByLoginID(p_con, p_requestVO.getSenderLoginID());
                        } else if (!BTSLUtil.isNullString(p_requestVO.getSenderExternalCode())) {
                            channelUserVO = _channelUserDAO.loadChnlUserDetailsByExtCode(p_con, BTSLUtil.NullToString(p_requestVO.getSenderExternalCode()).trim());
                        }

                        if (channelUserVO != null) {
                            if (!networkID.equalsIgnoreCase(channelUserVO.getNetworkID())) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
                            }
                            // change for moldova by Ved 24/07/07
                            if (!BTSLUtil.isNullString(password) && !operatorUtili.validateTransactionPassword(channelUserVO, password)) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                            }
                            if (!BTSLUtil.isNullString(extCode) && !extCode.equalsIgnoreCase(channelUserVO.getExternalCode())) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE);
                            }
                            if (!BTSLUtil.isNullString(loginID)) {
                                // change for moldova by Ved 24/07/07
                                if (!operatorUtili.validateTransactionPassword(channelUserVO, password)) {
                                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                                }
                                if (!loginID.equalsIgnoreCase(channelUserVO.getLoginID())) {
                                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
                                }
                            }
                        } else {
                            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_USER_NOT_EXIST);
                        }
                        //
                        if (!p_requestVO.getMessageGatewayVO().isUserAuthorizationReqd()) {
                            p_requestVO.setPinValidationRequired(false);
                        } else if (BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN())) {
                            p_requestVO.setPinValidationRequired(false);
	                    }/* else if (BTSLUtil.isNullString(p_requestVO.getMsisdn())) {
	                        p_requestVO.setPinValidationRequired(false);
	                    }*/
                        String CHNL_MESSAGE_SEP = chnlPlainSmsSeparator;
                        if (BTSLUtil.isNullString(CHNL_MESSAGE_SEP)) {
                            CHNL_MESSAGE_SEP = " ";
                        }
                        String message = p_requestVO.getDecryptedMessage();
                        if (!p_requestVO.isPinValidationRequired()) {
                            if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("PIN"))) {
                                message = message + CHNL_MESSAGE_SEP + (String) p_requestVO.getRequestMap().get("PIN");
                            } else {
                                message = message + CHNL_MESSAGE_SEP + BTSLUtil.decryptText(channelUserVO.getUserPhoneVO().getSmsPin());
                            }
                        } else {
                            if (BTSLUtil.NullToString(p_requestVO.getRequestMap().get("PIN").toString()).length() == 0) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_INVALID_PIN);
                            }
                            message = message + CHNL_MESSAGE_SEP + (String) p_requestVO.getRequestMap().get("PIN");
                        }
                        p_requestVO.setDecryptedMessage(message);
                        break;
                    }// Ended Here by Diwakar

                // Added By Brajesh for LMS Points Enquiry Through EXTGW
                case ACTION_CHNL_LMS_POINTS_ENQUIRY:
                    {

                        String password = (String) p_requestVO.getRequestMap().get("PASSWORD");
                        String extCode = (String) p_requestVO.getRequestMap().get("EXTCODE");
                        String msisdn = (String) p_requestVO.getRequestMap().get("MSISDN");
                        String date = (String) p_requestVO.getRequestMap().get("DATE");
                        date=BTSLDateUtil.getGregorianDateInString(date);
                        if (!BTSLUtil.isValidDatePattern(date)) {
                            p_requestVO.setSuccessTxn(false);
                            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.LMS_INVALID_DATE);
                        }
                        String networkID = p_requestVO.getRequestNetworkCode();
                        String loginID = p_requestVO.getSenderLoginID();
                        if (!BTSLUtil.isNullString(msisdn)) {
                            channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getFilteredMSISDN());
                        } else if (!BTSLUtil.isNullString(p_requestVO.getSenderLoginID())) {

                            channelUserVO = _channelUserDAO.loadChnlUserDetailsByLoginID(p_con, p_requestVO.getSenderLoginID());

                        } else if (!BTSLUtil.isNullString(p_requestVO.getSenderExternalCode())) {
                            channelUserVO = _channelUserDAO.loadChnlUserDetailsByExtCode(p_con, BTSLUtil.NullToString(p_requestVO.getSenderExternalCode()).trim());
                        }

                        if (channelUserVO != null) {
                            if (!networkID.equalsIgnoreCase(channelUserVO.getNetworkID())) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
                            }

                            if (!BTSLUtil.isNullString(extCode) && !extCode.equalsIgnoreCase(channelUserVO.getExternalCode())) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE);
                            }
                            if (!BTSLUtil.isNullString(loginID)) {
                                // change for moldova by Ved 24/07/07

                                if (!loginID.equalsIgnoreCase(channelUserVO.getLoginID())) {
                                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
                                }
                                if (!operatorUtili.validateTransactionPassword(channelUserVO, password))
                                	throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_INVALID_PSWD);
                                /*if (!BTSLUtil.encryptText(password).equalsIgnoreCase(BTSLUtil.decryptText(channelUserVO.getPassword()))) {
                                    // if
                                    // the
                                    // pin
                                    // is
                                    // valid
                                    // or
                                    // not
                                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_INVALID_PSWD);
                                }*/
                            }
                        } else {
                            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_USER_NOT_EXIST);
                        }
                        //
                        if (!p_requestVO.getMessageGatewayVO().isUserAuthorizationReqd()) {
                            p_requestVO.setPinValidationRequired(false);
                        } else if (BTSLUtil.isNullString(msisdn)) {
                            p_requestVO.setPinValidationRequired(false);
                        }
                        String CHNL_MESSAGE_SEP = chnlPlainSmsSeparator;
                        if (BTSLUtil.isNullString(CHNL_MESSAGE_SEP)) {
                            CHNL_MESSAGE_SEP = " ";
                        }
                        String message = p_requestVO.getDecryptedMessage();
                        if (!p_requestVO.isPinValidationRequired()) {
                            if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("PIN"))) {
                                message = message + CHNL_MESSAGE_SEP + (String) p_requestVO.getRequestMap().get("PIN");
                            } else {
                                message = message + CHNL_MESSAGE_SEP + BTSLUtil.decryptText(channelUserVO.getUserPhoneVO().getSmsPin());
                            }
                        } else {
                        	if(BTSLUtil.NullToString(p_requestVO.getRequestMap().get("PIN").toString()).length()==0)
    					        throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.ERROR_INVALID_PIN);
    					    else if(BTSLUtil.isNullString(p_requestVO.getRequestMap().get("PIN").toString()))
    					    	throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.ERROR_INVALID_PIN);					    
                        }
                        p_requestVO.setDecryptedMessage(message);
                        break;
                    }
                // Added By Brajesh for LMS Points Enquiry Through EXTGW
                case ACTION_CHNL_LMS_POINTS_REDEMPTION:
                    {
                        String extNwCode = (String) p_requestVO.getRequestMap().get("EXTNWCODE");
                        String categoryCode = (String) p_requestVO.getRequestMap().get("CATCODE");
                        String senderMSISDN = (String) p_requestVO.getRequestMap().get("MSISDN");
                        String loginID = (String) p_requestVO.getRequestMap().get("LOGINID");
                        String password = (String) p_requestVO.getRequestMap().get("PASSWORD");
                        String networkID = p_requestVO.getRequestNetworkCode();
                        String empCode = (String) p_requestVO.getRequestMap().get("EMPCODE");
                        String date = (String) p_requestVO.getRequestMap().get("DATE");
                        date=BTSLDateUtil.getGregorianDateInString(date);
                        if (!BTSLUtil.isValidDatePattern(date)) {
                            p_requestVO.setSuccessTxn(false);
                            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.LMS_INVALID_DATE);
                        }
                        String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
                        String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
                        Locale locale = new Locale("en", "en");
                        try {
                            locale = new Locale(defaultLanguage, defaultCountry);
                        } catch (RuntimeException e) {
                            // TODO Auto-generated catch block
                            LOG.errorTrace(METHOD_NAME, e);
                            locale = new Locale("en", "en");
                        }
                        String pin = (String) p_requestVO.getRequestMap().get("PIN");
                        String extCode = (String) p_requestVO.getRequestMap().get("EXTCODE");
                        String msisdn = (String) p_requestVO.getRequestMap().get("MSISDN");
                        String points = (String) p_requestVO.getRequestMap().get("POINTS");
                        if (BTSLUtil.isNullString(points)) {
                            p_requestVO.setSuccessTxn(false);
                            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.INVALID_REDEMP_LOYALTY_POINTS);
                        }
                        if (!BTSLUtil.isNullString(msisdn)) {
                            channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getFilteredMSISDN());
                        } else if (!BTSLUtil.isNullString(p_requestVO.getSenderLoginID())) {
                            channelUserVO = _channelUserDAO.loadChnlUserDetailsByLoginID(p_con, p_requestVO.getSenderLoginID());

                        } else if (!BTSLUtil.isNullString(p_requestVO.getSenderExternalCode())) {
                            channelUserVO = _channelUserDAO.loadChnlUserDetailsByExtCode(p_con, BTSLUtil.NullToString(p_requestVO.getSenderExternalCode()).trim());
                        }

                        if (channelUserVO != null) {
                            if (!networkID.equalsIgnoreCase(channelUserVO.getNetworkID())) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
                            }

                            if (!BTSLUtil.isNullString(extCode) && !extCode.equalsIgnoreCase(channelUserVO.getExternalCode())) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE);
                            }
                            if (!BTSLUtil.isNullString(loginID)) {
                                // change for moldova by Ved 24/07/07

                                if (!loginID.equalsIgnoreCase(channelUserVO.getLoginID())) {
                                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
                                }
                                if (!operatorUtili.validateTransactionPassword(channelUserVO, password))
                                	throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_INVALID_PSWD);
                                /*if (!BTSLUtil.encryptText(password).equalsIgnoreCase(BTSLUtil.decryptText(channelUserVO.getPassword()))) {
                                    // if
                                    // the
                                    // pin
                                    // is
                                    // valid
                                    // or
                                    // not
                                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_INVALID_PSWD);
                                }*/
                            }
                        } else {
                            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_USER_NOT_EXIST);
                        }
                        //
                        if (!p_requestVO.getMessageGatewayVO().isUserAuthorizationReqd()) {
                            p_requestVO.setPinValidationRequired(false);
                        } else if (BTSLUtil.isNullString(msisdn)) {
                            p_requestVO.setPinValidationRequired(false);
                        }
                        String CHNL_MESSAGE_SEP = chnlPlainSmsSeparator;
                        if (BTSLUtil.isNullString(CHNL_MESSAGE_SEP)) {
                            CHNL_MESSAGE_SEP = " ";
                        }
                        String message = p_requestVO.getDecryptedMessage();
                        if (!p_requestVO.isPinValidationRequired()) {
                            if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("PIN"))) {
                                message = message + CHNL_MESSAGE_SEP + (String) p_requestVO.getRequestMap().get("PIN");
                            } else {
                                message = message + CHNL_MESSAGE_SEP + BTSLUtil.decryptText(channelUserVO.getUserPhoneVO().getSmsPin());
                            }
                        } else {
                        	if(BTSLUtil.NullToString(p_requestVO.getRequestMap().get("PIN").toString()).length()==0)
    					        throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.ERROR_INVALID_PIN);
    					    else if(BTSLUtil.isNullString(p_requestVO.getRequestMap().get("PIN").toString()))
    					    	throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.ERROR_INVALID_PIN);					    
                        }
                        p_requestVO.setDecryptedMessage(message);
                        break;
                    }

                case ACTION_EXT_C2SCHANGEMSISDN:
                    { // for MSISDN Change
                      // Functionality Start
                        String password = (String) p_requestVO.getRequestMap().get("PASSWORD");
                        String extCode = (String) p_requestVO.getRequestMap().get("EXTCODE");
                        String networkID = p_requestVO.getRequestNetworkCode();
                        String loginID = p_requestVO.getSenderLoginID();
                        if (!BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN())) {
                            channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getFilteredMSISDN());
                        } else if (!BTSLUtil.isNullString(p_requestVO.getSenderLoginID())) {
                            channelUserVO = _channelUserDAO.loadChnlUserDetailsByLoginID(p_con, p_requestVO.getSenderLoginID());
                        } else if (!BTSLUtil.isNullString(p_requestVO.getSenderExternalCode())) {
                            channelUserVO = _channelUserDAO.loadChnlUserDetailsByExtCode(p_con, BTSLUtil.NullToString(p_requestVO.getSenderExternalCode()).trim());
                        }

                        if (channelUserVO != null) {
                            if (!networkID.equalsIgnoreCase(channelUserVO.getNetworkID())) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
                            }
                            if (!BTSLUtil.isNullString(password) && !operatorUtili.validateTransactionPassword(channelUserVO, password)) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                            }
                            if (!BTSLUtil.isNullString(extCode) && !extCode.equalsIgnoreCase(channelUserVO.getExternalCode())) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE);
                            }
                            if (!BTSLUtil.isNullString(loginID)) {
                                if (!operatorUtili.validateTransactionPassword(channelUserVO, password)) {
                                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                                }
                                if (!loginID.equalsIgnoreCase(channelUserVO.getLoginID())) {
                                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
                                }
                            }
                        }
                        //
                        if (!p_requestVO.getMessageGatewayVO().isUserAuthorizationReqd()) {
                            p_requestVO.setPinValidationRequired(false);
                        } else if (BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN())) {
                            p_requestVO.setPinValidationRequired(false);
                        }
                        break;
                    } // for MSISDN Change Functionality End
                case ACTION_EXT_C2SRECHARGESTATUS:
                    { // for ETU Change Recharge
                      // Status Functionality Start
                        String password = (String) p_requestVO.getRequestMap().get("PASSWORD");
                        String extCode = (String) p_requestVO.getRequestMap().get("EXTCODE");
                        String networkID = p_requestVO.getRequestNetworkCode();
                        String loginID = p_requestVO.getSenderLoginID();
                        if (!BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN())) {
                            channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getFilteredMSISDN());
                        } else if (!BTSLUtil.isNullString(p_requestVO.getSenderLoginID())) {
                            channelUserVO = _channelUserDAO.loadChnlUserDetailsByLoginID(p_con, p_requestVO.getSenderLoginID());
                        } else if (!BTSLUtil.isNullString(p_requestVO.getSenderExternalCode())) {
                            channelUserVO = _channelUserDAO.loadChnlUserDetailsByExtCode(p_con, BTSLUtil.NullToString(p_requestVO.getSenderExternalCode()).trim());
                        }

                        if (channelUserVO != null) {
                            if (!networkID.equalsIgnoreCase(channelUserVO.getNetworkID())) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
                            }
                            if (!BTSLUtil.isNullString(password) && !operatorUtili.validateTransactionPassword(channelUserVO, password)) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                            }
                            if (!BTSLUtil.isNullString(extCode) && !extCode.equalsIgnoreCase(channelUserVO.getExternalCode())) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE);
                            }
                            if (!BTSLUtil.isNullString(loginID)) {
                                if (!operatorUtili.validateTransactionPassword(channelUserVO, password)) {
                                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                                }
                                if (!loginID.equalsIgnoreCase(channelUserVO.getLoginID())) {
                                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
                                }
                            }
                        }
                        //
                        if (!p_requestVO.getMessageGatewayVO().isUserAuthorizationReqd()) {
                            p_requestVO.setPinValidationRequired(false);
                        } else if (BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN())) {
                            p_requestVO.setPinValidationRequired(false);
                        }
                        break;
                    } // for ETU Change Recharge Status Functionality End
                    
                case ACTION_EXT_DSR_REQUEST:
                {
                    String pin = (String) p_requestVO.getRequestMap().get("PIN");
                    String password = (String) p_requestVO.getRequestMap().get("PASSWORD");
                    String extCode = (String) p_requestVO.getRequestMap().get("EXTCODE");
                    String networkID = (String)p_requestVO.getRequestMap().get("EXTNWCODE");
                    
                    p_requestVO.setExternalNetworkCode((String)p_requestVO.getRequestMap().get("EXTNWCODE"));
                    if (p_requestVO.getRequestGatewayType().equalsIgnoreCase(PretupsI.REQUEST_SOURCE_TYPE_EXTGW)) {
                    	if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("LOGINID"))) {
                    		p_requestVO.setSenderLoginID((String) p_requestVO.getRequestMap().get("LOGINID"));
                    	}
                    	if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("PASSWORD"))) {
                    		p_requestVO.setPassword((String) p_requestVO.getRequestMap().get("PASSWORD"));
                    	}
                    }
                    String msisdn = (String) p_requestVO.getRequestMap().get("MSISDN1");
                    String msisdn2 = (String) p_requestVO.getRequestMap().get("MSISDN2");
                    String loginID = (String) p_requestVO.getRequestMap().get("LOGINID");
                    
                    if (!BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN())) {
                        channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getFilteredMSISDN());
                    } else if (!BTSLUtil.isNullString(p_requestVO.getSenderLoginID())) {
                        channelUserVO = _channelUserDAO.loadChnlUserDetailsByLoginID(p_con, p_requestVO.getSenderLoginID());
                    } else if (!BTSLUtil.isNullString(p_requestVO.getSenderExternalCode())) {
                        channelUserVO = _channelUserDAO.loadChnlUserDetailsByExtCode(p_con, BTSLUtil.NullToString(p_requestVO.getSenderExternalCode()).trim());
                    }
                    
                    if (channelUserVO != null) {
                    	if (!networkID.equalsIgnoreCase(channelUserVO.getNetworkID())) {
                            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
                        }
                        if (!BTSLUtil.isNullString(msisdn) && !BTSLUtil.isNullString(pin)) {
                            operatorUtili.validatePIN(p_con, channelUserVO, pin);
                        }
                        if (!BTSLUtil.isNullString(password) && !operatorUtili.validateTransactionPassword(channelUserVO, password)) {
                            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                        }
                        if (!BTSLUtil.isNullString(extCode) && !extCode.equalsIgnoreCase(channelUserVO.getExternalCode())) {
                            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE);
                        }
                        if (!BTSLUtil.isNullString(loginID)) {
                            if (!operatorUtili.validateTransactionPassword(channelUserVO, password)) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                            }
                            if (!loginID.equalsIgnoreCase(channelUserVO.getLoginID())) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
                            }
                        }
                        String[] arr=null;
                        int childSize=0;
                    	String status = "'"+PretupsI.USER_STATUS_ACTIVE+"','"+PretupsI.USER_STATUS_SUSPEND+"', '"+PretupsI.USER_STATUS_SUSPEND_REQUEST+"'";
        				String statusUsed = PretupsI.STATUS_IN;
                    	arr = new String[1];
    					arr[0]=channelUserVO.getUserID();
    					ArrayList childUserList =_channelUserDAO.loadUserHierarchyListForTransfer(p_con,arr,PretupsI.SINGLE,statusUsed,status,channelUserVO.getCategoryCode());
    					if (childUserList!=null ) {
                        	childSize = childUserList.size()-1;
                        }
                        
                        p_requestVO.getRequestMap().put("TOTAL_CHILD", childSize);
                        
                        if(!BTSLUtil.isNullString(msisdn2)) {
                        	boolean isValidChild = false;
                        	
        					if (childUserList!=null && !childUserList.isEmpty()) {
        						for(int i=0;i<childUserList.size();i++) {
            						ChannelUserVO userListVO = (ChannelUserVO)childUserList.get(i);
            						if(userListVO.getMsisdn().equals(msisdn2)) {
            							isValidChild = true;
            						}
            					}
        					}
        					if (!isValidChild) {
        						throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.DSR_CHILD_MSISDN_INVALID,new String[] {msisdn2});
        					}
                        }
                    } else {
                        throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_USER_NOT_EXIST);
                    }
                    break;
                }
                case ACTION_EXT_STOCK_BALANCE_REQUEST:
                {
                    String pin = (String) p_requestVO.getRequestMap().get("PIN");
                    String password = (String) p_requestVO.getRequestMap().get("PASSWORD");
                    String extCode = (String) p_requestVO.getRequestMap().get("EXTCODE");
                    String networkID = (String)p_requestVO.getRequestMap().get("EXTNWCODE");
                    
                    p_requestVO.setExternalNetworkCode((String)p_requestVO.getRequestMap().get("EXTNWCODE"));
                    if (p_requestVO.getRequestGatewayType().equalsIgnoreCase(PretupsI.REQUEST_SOURCE_TYPE_EXTGW)) {
                    	if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("LOGINID"))) {
                    		p_requestVO.setSenderLoginID((String) p_requestVO.getRequestMap().get("LOGINID"));
                    	}
                    	if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("PASSWORD"))) {
                    		p_requestVO.setPassword((String) p_requestVO.getRequestMap().get("PASSWORD"));
                    	}
                    }
                    String msisdn = (String) p_requestVO.getRequestMap().get("MSISDN1");
                    String msisdn2 = (String) p_requestVO.getRequestMap().get("MSISDN2");
                    String loginID = (String) p_requestVO.getRequestMap().get("LOGINID");
                    
                    if (!BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN())) {
                        channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getFilteredMSISDN());
                    } else if (!BTSLUtil.isNullString(p_requestVO.getSenderLoginID())) {
                        channelUserVO = _channelUserDAO.loadChnlUserDetailsByLoginID(p_con, p_requestVO.getSenderLoginID());
                    } else if (!BTSLUtil.isNullString(p_requestVO.getSenderExternalCode())) {
                        channelUserVO = _channelUserDAO.loadChnlUserDetailsByExtCode(p_con, BTSLUtil.NullToString(p_requestVO.getSenderExternalCode()).trim());
                    }
                    
                    if (channelUserVO != null) {
                    	if (!networkID.equalsIgnoreCase(channelUserVO.getNetworkID())) {
                            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
                        }
                        if (!BTSLUtil.isNullString(msisdn) && !BTSLUtil.isNullString(pin)) {
                            operatorUtili.validatePIN(p_con, channelUserVO, pin);
                        }
                        if (!BTSLUtil.isNullString(password) && !operatorUtili.validateTransactionPassword(channelUserVO, password)) {
                            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                        }
                        if (!BTSLUtil.isNullString(extCode) && !extCode.equalsIgnoreCase(channelUserVO.getExternalCode())) {
                            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE);
                        }
                        if (!BTSLUtil.isNullString(loginID)) {
                            if (!operatorUtili.validateTransactionPassword(channelUserVO, password)) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                            }
                            if (!loginID.equalsIgnoreCase(channelUserVO.getLoginID())) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
                            }
                        }
                        String[] arr=null;
                        int childSize=0;
                    	String status = "'"+PretupsI.USER_STATUS_ACTIVE+"','"+PretupsI.USER_STATUS_SUSPEND+"', '"+PretupsI.USER_STATUS_SUSPEND_REQUEST+"'";
        				String statusUsed = PretupsI.STATUS_IN;
                    	arr = new String[1];
    					arr[0]=channelUserVO.getUserID();
    					ArrayList childUserList =_channelUserDAO.loadUserHierarchyListForTransfer(p_con,arr,PretupsI.SINGLE,statusUsed,status,channelUserVO.getCategoryCode());
    					if (childUserList!=null ) {
                        	childSize = childUserList.size()-1;
                        }
                        p_requestVO.getRequestMap().put("TOTAL_CHILD", childSize);
                        
                        if(!BTSLUtil.isNullString(msisdn2)) {
                        	boolean isValidChild = false;
                        	
        					if (childUserList!=null && !childUserList.isEmpty()) {
        						for(int i=0;i<childUserList.size();i++) {
            						ChannelUserVO userListVO = (ChannelUserVO)childUserList.get(i);
            						if(userListVO.getMsisdn().equals(msisdn2)) {
            							isValidChild = true;
            						}
            					}
        					}
        					if (!isValidChild) {
        						throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.DSR_CHILD_MSISDN_INVALID,new String[] {msisdn2});
        					}
                        }
                    } else {
                        throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_USER_NOT_EXIST);
                    }
                    break;
                }
                
                case ACTION_CHNL_EXT_BULK_RCH_REVERSAL:
				{
					if(!BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN()))
						channelUserVO= _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getFilteredMSISDN());
					if(channelUserVO==null){
						throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.ERROR_USER_NOT_EXIST);    
					}
					p_requestVO.setPinValidationRequired(false);
					break;
				}
				
                case ACTION_LOAN_OPTIN_REQ:
                case ACTION_LOAN_OPTOUT_REQ:
                case ACTION_CHNL_SOS_SETTLEMENT_REQUEST:
                case ACTION_SOS_FLAG_UPDATE_REQ:
                {
                	channelUserVO = validateEXTGWRequestCommon(p_con, p_requestVO, channelUserVO, operatorUtili);
                	
                    String CHNL_MESSAGE_SEP = chnlPlainSmsSeparator;
                    if (BTSLUtil.isNullString(CHNL_MESSAGE_SEP)) {
                        CHNL_MESSAGE_SEP = " ";
                    }
                    String message = p_requestVO.getDecryptedMessage();
                    if(p_requestVO.getSenderLocale()!=null)
                    	message = message + CHNL_MESSAGE_SEP + p_requestVO.getSenderLocale();
                    if(p_requestVO.getReceiverLocale()!=null)
                    	message = message + CHNL_MESSAGE_SEP + p_requestVO.getReceiverLocale();
                    
                    if (!p_requestVO.isPinValidationRequired()) {
                        if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("PIN"))) {
                            message = message + CHNL_MESSAGE_SEP + (String) p_requestVO.getRequestMap().get("PIN");
                        } else {
                            message = message + CHNL_MESSAGE_SEP + BTSLUtil.decryptText(channelUserVO.getUserPhoneVO().getSmsPin());
                        }
                    } else {
                        if (BTSLUtil.NullToString(p_requestVO.getRequestMap().get("PIN").toString()).length() == 0) {
                            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.C2S_PIN_BLANK);
                        }
                        message = message + CHNL_MESSAGE_SEP + (String) p_requestVO.getRequestMap().get("PIN");
                    }
                    p_requestVO.setDecryptedMessage(message);
                    break;
                }
                case ACTION_CHNL_SOS_REQUEST:
                {
                	channelUserVO = validateEXTGWRequestCommon(p_con, p_requestVO, channelUserVO, operatorUtili);
                	
                    String CHNL_MESSAGE_SEP = chnlPlainSmsSeparator;
                    if (BTSLUtil.isNullString(CHNL_MESSAGE_SEP)) {
                        CHNL_MESSAGE_SEP = " ";
                    }
                     
                  
                    String message = p_requestVO.getDecryptedMessage();
                    if( p_requestVO.getRequestMSISDN()!=null)
                    {
                    	message = message + CHNL_MESSAGE_SEP +  p_requestVO.getRequestMSISDN();
                    }
                    if (!p_requestVO.isPinValidationRequired()) {
                        if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("PIN"))) {
                            message = message + CHNL_MESSAGE_SEP + (String) p_requestVO.getRequestMap().get("PIN") + CHNL_MESSAGE_SEP + p_requestVO.getReceiverLocale();
                        } else {
                            message = message + CHNL_MESSAGE_SEP + BTSLUtil.decryptText(channelUserVO.getUserPhoneVO().getSmsPin()) + CHNL_MESSAGE_SEP + p_requestVO.getReceiverLocale();
                        }
                    } else {
                        if (BTSLUtil.NullToString(p_requestVO.getRequestMap().get("PIN").toString()).length() == 0) {
                            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.C2S_PIN_BLANK);
                        }
                        message = message + CHNL_MESSAGE_SEP + (String) p_requestVO.getRequestMap().get("PIN") + CHNL_MESSAGE_SEP + p_requestVO.getReceiverLocale();
                      

                    }
                    p_requestVO.setDecryptedMessage(message);
                
                    break;
                }

            
			case ACTION_VOMS_CON:   
                case ACTION_VOMS_ROLLBACK:
                case ACTION_VOUCHER_STATUS_CHANGES:
                case ACTION_VOMS_QRY:
                case ACTION_VOMS_VALIDATE_REQ:
    			case ACTION_VOMS_RESERVE_REQ:
    			case ACTION_VOMS_DIRECT_CONSUMPTION_REQ:
    			case ACTION_VMS_PIN_EXP_EXT_REQ:
    			case ACTION_VOMS_DIRECT_ROLLBACK_REQ:
    			
                {
                	String extCode = p_requestVO.getSenderExternalCode();
                    
                    String extnetworkID = p_requestVO.getExternalNetworkCode();
                    String loginID = p_requestVO.getSenderLoginID();
                    String password=p_requestVO.getPassword();
                    LoginDAO _loginDAO = new LoginDAO();
                    byPassCheck=true;
                    String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
                    String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
                    Locale locale = new Locale(defaultLanguage, defaultCountry);                    
                     if (!BTSLUtil.isNullString(loginID)) {
                    	 channelUserVO = _loginDAO.loadUserDetails(p_con, loginID, password, locale);
                    	 if (channelUserVO== null)
                    	 {
                    		 throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER); 
                    	 }
                    } else if (!BTSLUtil.isNullString(extCode)) {
                        channelUserVO = _channelUserDAO.loadChnlUserDetailsByExtCode(p_con, BTSLUtil.NullToString(extCode).trim());
                    }
                     if (!BTSLUtil.isNullString(extnetworkID)) {
	                     NetworkVO networkVO = (NetworkVO) NetworkCache.getNetworkByExtNetworkCode(extnetworkID);
	                     if(networkVO==null){
	                     String messageArray[]= {extnetworkID};
	                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_INVALID, messageArray);
	                     }
                     }
                     
                     
                    if (channelUserVO != null) {
                    	 if (!operatorUtili.validateTransactionPassword(channelUserVO, password)) {
                             throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                         }
                        if (!BTSLUtil.isNullString(extnetworkID) && !extnetworkID.equalsIgnoreCase(channelUserVO.getNetworkID())) {
                            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
                        }
                        if (!BTSLUtil.isNullString(loginID)) {
                            if (!loginID.equalsIgnoreCase(channelUserVO.getLoginID())) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
                            }
                        }
                        if (!BTSLUtil.isNullString(extCode)) {
                            if (!extCode.equalsIgnoreCase(channelUserVO.getExternalCode())) {
                                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE);
                            }
                        }
                        
                    }
                	
                    break;
                }
                
    			case ACTION_CHNL_VOUCHER_AVAILABILITY_XML:
    			case CHANNEL_USER_DETAILS:
    			{
    				String extCode = p_requestVO.getRequestMap().get("EXTCODE").toString();
    				String msisdn = p_requestVO.getRequestMap().get("MSISDN").toString();
    				String pin = p_requestVO.getRequestMap().get("PIN").toString();
    				  
                    String extnetworkID = p_requestVO.getRequestMap().get("EXTNWCODE").toString();
                    String loginID = p_requestVO.getRequestMap().get("LOGINID").toString();
                    String password = p_requestVO.getRequestMap().get("PASSWORD").toString();
                    
                    byPassCheck=true;
                    String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
                    String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
                    Locale locale = new Locale(defaultLanguage, defaultCountry);
                    
                     if (!BTSLUtil.isNullString(loginID)) {
                    	 channelUserVO = _channelUserDAO.loadChnlUserDetailsByLoginID(p_con, loginID);
                    	 if (channelUserVO== null)
                    	 {
                    		 throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER); 
                    	 }
                    	 
                    	 if (!operatorUtili.validateTransactionPassword(channelUserVO, password) || BTSLUtil.isNullString(password)) {
                             throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                         }
                         if (!loginID.equalsIgnoreCase(channelUserVO.getLoginID())) {
                             throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
                         }
                         
                    } else if (!BTSLUtil.isNullString(extCode)) {
                        channelUserVO = _channelUserDAO.loadChnlUserDetailsByExtCode(p_con, BTSLUtil.NullToString(extCode).trim());
                    }
                     
                    else if(!BTSLUtil.isNullString(msisdn))
                    {
                    	channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con,msisdn);
                    	if(channelUserVO == null)
                    	{
                    		throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
                    	}
                    	
                    	else
                    	{
                    		operatorUtili.validatePIN(p_con, channelUserVO, pin);
                    	}
                    }
                     if (!BTSLUtil.isNullString(extnetworkID)) {
	                     NetworkVO networkVO = (NetworkVO) NetworkCache.getNetworkByExtNetworkCode(extnetworkID);
	                     if(networkVO==null){
	                     String messageArray[]= {extnetworkID};
	                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_INVALID, messageArray);
	                     }
                     }
                     
                     if (channelUserVO != null) {
                     	if (!extnetworkID.equalsIgnoreCase(channelUserVO.getNetworkID())) {
                             throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
                         }
                     }
                     if (!BTSLUtil.isNullString(extnetworkID) && !extnetworkID.equalsIgnoreCase(channelUserVO.getNetworkID())) {
                         throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
                     }
                     if (!BTSLUtil.isNullString(loginID)) {
                         if (!loginID.equalsIgnoreCase(channelUserVO.getLoginID())) {
                             throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
                         }
                     }
                     if (!BTSLUtil.isNullString(extCode)) {
                         if (!extCode.equalsIgnoreCase(channelUserVO.getExternalCode())) {
                             throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE);
                         }
                     }
                     
                     break;
    			}
    			
    			case ACTION_C2C_VOUCHER_APPR: 
    			 { 
                     // Load ChannelUser on basis of MSISDN 
                     channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getFilteredMSISDN());                    

                     break; 
                 }

			case ACTION_UPUSRHRCHY: {
				// Load ChannelUser on basis of MSISDN
				channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getFilteredMSISDN());

				break;
			}
			case ACTION_PSBKRPT: {
				// Load ChannelUser on basis of MSISDN
				channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getFilteredMSISDN());

				break;
			}
			case C2C_BUY_ENQ: {
				// Load ChannelUser on basis of MSISDN
				channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getFilteredMSISDN());

				break;
			}
			case C2S_SV_DETAILS:
			case C2S_PROD_TXN_DETAILS:
			case PASSBOOK_VIEW_DETAILS:
			case C2S_TOTAL_TRANSACTION_COUNT:
			case C2S_N_PROD_TXN_DETAILS:
			{
				channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getFilteredMSISDN());

				break;
			}
			case TOTAL_TRANSACTION_DETAILED_VIEW:
			{
				channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getFilteredMSISDN());

				break;
			}
			case COMMISSION_CALCULATOR:
			{
				channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getFilteredMSISDN());

				break;
			}
			
            case ACTION_LST_N_EVD_TRF:
            {	
                	channelUserVO = validateEXTGWRequestCommon(p_con, p_requestVO, channelUserVO, operatorUtili);
            }
			
            case ACTION_LST_LOAN_ENQ:
            {
            	channelUserVO = validateEXTGWRequestCommon(p_con, p_requestVO, channelUserVO, operatorUtili);
            }
            
            
            
            case ACTION_SELF_CUBAR: 
            {
            	
            	channelUserVO = validateEXTGWRequestCommon(p_con, p_requestVO, channelUserVO, operatorUtili);
                
            	break;
            }
            
            case ACTION_SELF_CU_UNBAR: 
            {
            	String msisdn = p_requestVO.getFilteredMSISDN();
            	
            	
            	channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con, msisdn);
            	
            	if(channelUserVO == null) {
            		throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_USER_NOT_EXIST);
            	}

            	
            	
                break;
            }
            
            case ACTION_SELF_PIN_RESET: 
            {
				if(!BTSLUtil.isNullString(p_requestVO.getRequestMSISDN()))
					channelUserVO= _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getRequestMSISDN());
				if(channelUserVO==null){
					throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.ERROR_USER_NOT_EXIST);    
				}
				p_requestVO.setPinValidationRequired(false);
				break;
			}


    			default:
          	     	 if(LOG.isDebugEnabled()){
          	     		LOG.debug("Default Value " ,p_requestVO.getActionValue());
          	     	 }  
                
            }
                
            
            if (!byPassCheck) // Added for Ext geography API
            {
                // added by zafar on 22 May'08
                if (BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN()) && channelUserVO != null) {
                    p_requestVO.setFilteredMSISDN(PretupsBL.getFilteredMSISDN(channelUserVO.getUserPhoneVO().getMsisdn()));
                }

                validateUserDetails(p_requestVO, channelUserVO);
                if (channelUserVO != null) {
                    p_requestVO.setMessageSentMsisdn(channelUserVO.getUserPhoneVO().getMsisdn());
                    if(!BTSLUtil.isNullString(p_requestVO.getActiverUserId())){
                    	if (!channelUserVO.getUserID().equals(p_requestVO.getActiverUserId())) {
                    		channelUserVO.setStaffUser(true);
                    		staffUserVO = _channelUserDAO.loadChannelUserDetailsByUserId(p_con, p_requestVO.getActiverUserId(), channelUserVO.getUserID());
                    		if(staffUserVO!=null)
                    		{
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
                    		}
                    	}else{
                    		channelUserVO.setActiveUserID(channelUserVO.getUserID());
                    	}
                    } else {
                        channelUserVO.setActiveUserID(channelUserVO.getUserID());
                    }
                }
            }/*else{
            	channelUserVO = ChannelUserVO.getInstance();
            	channelUserVO.setCategoryCode(PretupsI.SUPER_ADMIN);            	
            }*/
            	p_requestVO.setSenderVO(channelUserVO);
            
        } catch (BTSLBaseException be) {
        	p_requestVO.setSuccessTxn(false);
        	p_requestVO.setMessageCode(be.getMessage());
        	p_requestVO.setMessageArguments(be.getArgs());
            LOG.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_EXCEPTION,e);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting Request ID=" + p_requestVO.getRequestID() + " channelUserVO=" + channelUserVO);
            }
        }
        if (channelUserVO != null && channelUserVO.isStaffUser() && staffUserVO!=null) {
            return staffUserVO;
        } else {
            return channelUserVO;
        }
    }

    /**
     * @author diwakar
     * @param channelUserVO
     * @param senderMSISDN
     * @param staffUserVO
     * @param p_requestVO
     * @param networkID
     * @param loginID
     * @throws BTSLBaseException
     */


    /**
     * Method to validate User Details, check various status
     * 
     * @param p_requestVO
     * @param p_channelUserVO
     * @throws BTSLBaseException
     */
   
	@Override
	public void validateUserDetails(RequestVO p_requestVO, ChannelUserVO p_channelUserVO) throws BTSLBaseException {
        final String METHOD_NAME = "validateUserDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug("validateUserDetails", "Entered Request ID=" + p_requestVO.getRequestID());
        }
        try {
            if (p_channelUserVO == null) {
                throw new BTSLBaseException(this, "validateUserDetails", PretupsErrorCodesI.CHNL_ERROR_NO_SUCH_USER);
            }
            // bypassing validation check for sender in case of o2c initiate
           // if ((p_requestVO.getActionValue() == ACTION_CHNL_O2C_INITIATE_TRFR)) {
            if((p_requestVO.getActionValue()== ACTION_CHNL_O2C_INITIATE_TRFR) || (p_requestVO.getActionValue()== ACTION_SUSPEND_RESUME_CUSR_EXTGW) || (p_requestVO.getActionValue()== ACTION_CHNL_SOS_REQUEST) ) return;
               /* return;
            }*/
            p_requestVO.setLocale(new Locale((p_channelUserVO.getUserPhoneVO()).getPhoneLanguage(), (p_channelUserVO.getUserPhoneVO()).getCountry()));
            boolean statusAllowed = false;
            UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(p_channelUserVO.getNetworkID(), p_channelUserVO.getCategoryCode(), p_channelUserVO
                .getUserType(), p_requestVO.getRequestGatewayType());

            if (((p_requestVO.getActionValue() != ACTION_CHNL_O2C_WITHDRAW) && p_requestVO.getActionValue() != ACTION_CHNL_O2C_RETURN) && p_channelUserVO.getStatus()
                .equalsIgnoreCase(PretupsI.USER_STATUS_SUSPEND)) {
                throw new BTSLBaseException(this, "validateUserDetails", PretupsErrorCodesI.CHNL_ERROR_SENDER_SUSPEND);
            }
            if (userStatusVO == null) {
                throw new BTSLBaseException("validateUserDetails", "process", PretupsErrorCodesI.ERROR_USERSTATUS_NOTCONFIGURED);
            } else {
                String userStatusAllowed = userStatusVO.getUserSenderAllowed();
                String status[] = userStatusAllowed.split(",");
                for (int i = 0; i < status.length; i++) {
                    if (status[i].equals(p_channelUserVO.getStatus())) {
                        statusAllowed = true;
                    }
                }
                if (statusAllowed) {
                    if (!p_channelUserVO.getCategoryVO().getAllowedGatewayTypes().contains(p_requestVO.getMessageGatewayVO().getGatewayType())) {
                        throw new BTSLBaseException(this, "validateUserDetails", PretupsErrorCodesI.CHNL_ERROR_CAT_GATETYPENOTALLOWED);
                    } else if (p_channelUserVO.getGeographicalCodeStatus().equals(PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_SUSPEND)) {
                        throw new BTSLBaseException(this, "validateUserDetails", PretupsErrorCodesI.CHNL_ERROR_GEODOMAIN_SUSPEND);
                    }
                } else {
                    throw new BTSLBaseException(this, "validateUserDetails", PretupsErrorCodesI.CHNL_ERROR_SENDER_NOTALLOWED);
                }

            }
        } catch (BTSLBaseException be) {
            LOG.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "validateUserDetails", PretupsErrorCodesI.ERROR_EXCEPTION,e);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("validateUserDetails", "Exiting Request ID=" + p_requestVO.getRequestID());
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
        final String methodName = "parseChannelRequest";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered Request ID = " + p_requestVO.getRequestID() + ", action = " + action);
        }

        switch (action) {
        
        case TOTAL_USER_INCOME_DETAILS_VIEW:
    	{
    		ExtAPIXMLStringParser.parseTotalIncomeDetailsViewRequest(p_requestVO);
    		break;
    	}
        	case ACTION_UPUSRHRCHY:
        	{
        		ExtAPIXMLStringParser.parseExtUserHierarchy(p_requestVO, ACTION_UPUSRHRCHY);
        		break;
        	}
        	case ACTION_PSBKRPT: {
        		ExtAPIXMLStringParser.parseExtPassbookRpt(p_requestVO, ACTION_UPUSRHRCHY);
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
            case ACTION_CHNL_O2C_INITIATE:
                {
                    ExtAPIXMLStringParser.parseChannelO2CInitiateRequestAPI(p_requestVO);
                    break;
                }
            case ACTION_CHNL_O2C_INITIATE_TRFR:
                {
                    ExtAPIXMLStringParser.parseChannelO2CInitiateTrfrRequestAPI(p_requestVO);
                    break;
                }
            case ACTION_CHNL_O2C_RETURN:
                {
                    ExtAPIXMLStringParser.parseChannelO2CReturnRequestAPI(p_requestVO);
                    break;
                }
            case ACTION_CHNL_O2C_WITHDRAW:
                {
                    ExtAPIXMLStringParser.parseChannelO2CWithdrawRequestAPI(p_requestVO);
                    break;
                }
            case ACTION_CHNL_EXT_RECH_STATUS:
                {
                    ExtAPIXMLStringParser.parseChannelExtRechargeStatusRequest(p_requestVO);
                    break;
                }
            case ACTION_CHNL_EXT_CREDIT_TRANSFER:
                {
                    ExtAPIXMLStringParser.parseC2STransferRequest(p_requestVO, ACTION_CHNL_EXT_CREDIT_TRANSFER);
                    break;
                }
            case ACTION_VOU_PRF_MOD_REQ:
            {
                ExtAPIXMLStringParser.parseVoucherProfileBasedModificationRequest(p_requestVO, ACTION_VOU_PRF_MOD_REQ);
                break;
            }
            case ACTION_CHNL_EXT_TRANSFER_BILL_PAYMENT:
                {
                    ExtAPIXMLStringParser.parseChannelExtTransferBillPayment(p_requestVO);
                    break;
                }
            case ACTION_CHNL_EXT_ENQUIRY_REQUEST:
                {
                	ExtAPIStringParser.parseExtStringRequest(p_requestVO);
    				p_requestVO.setRequestMSISDN((String)p_requestVO.getRequestMap().get("MSISDN"));
    				p_requestVO.setSenderExternalCode((String)p_requestVO.getRequestMap().get("EXTCODE"));
    				p_requestVO.setExternalNetworkCode((String)p_requestVO.getRequestMap().get("EXTNWCODE"));
    				p_requestVO.setEnquiryServiceType((String)p_requestVO.getRequestMap().get("SERVICETYPE"));
    				p_requestVO.setEnquirySubService((String)p_requestVO.getRequestMap().get("SUBSERVICE"));
    				p_requestVO.setEnquiryAmount((String)p_requestVO.getRequestMap().get("AMOUNT"));
    				p_requestVO.setExternalTransactionNum((String)p_requestVO.getRequestMap().get("EXTTXNNUMBER"));
                    break;
                }
            case ACTION_CHNL_EXT_COMMON_RECHARGE:
                {
                    ExtAPIXMLStringParser.parseChannelExtCommonRechargeRequest(p_requestVO);
                    break;
                }
            case ACTION_CHNL_EXT_POST_RECHARGE_STATUS:
                {
                    // ExtAPIXMLStringParser.parseChannelExtPostRechargeStatusRequest(p_requestVO);
                    ExtAPIStringParser.parseExtStringRequest(p_requestVO);
                    String parsedRequestStr = null;
                    if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("EXTREFNUM")) && !BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get(
                        "TXNID"))) {
                        parsedRequestStr = (String) p_requestVO.getRequestMap().get("TYPE") + CHNL_MESSAGE_SEP + (String) p_requestVO.getRequestMap().get("TXNID") + CHNL_MESSAGE_SEP + (String) p_requestVO
                            .getRequestMap().get("EXTREFNUM") + CHNL_MESSAGE_SEP + "BOTH" + CHNL_MESSAGE_SEP + (String) p_requestVO.getRequestMap().get("PIN");
                    }
                    if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("EXTREFNUM"))) {
                        parsedRequestStr = (String) p_requestVO.getRequestMap().get("TYPE") + CHNL_MESSAGE_SEP + (String) p_requestVO.getRequestMap().get("EXTREFNUM") + CHNL_MESSAGE_SEP + "EXT" + CHNL_MESSAGE_SEP + (String) p_requestVO
                            .getRequestMap().get("PIN");
                    }
                    if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("TXNID"))) {
                        parsedRequestStr = (String) p_requestVO.getRequestMap().get("TYPE") + CHNL_MESSAGE_SEP + (String) p_requestVO.getRequestMap().get("TXNID") + CHNL_MESSAGE_SEP + "TXN" + CHNL_MESSAGE_SEP + (String) p_requestVO
                            .getRequestMap().get("PIN");
                    }
                    p_requestVO.setDecryptedMessage(parsedRequestStr);
                    break;
                }
            case ACTION_CHNL_GIFT_RECHARGE_XML:
                {
                    ExtAPIXMLStringParser.parseExtGiftRechargeRequest(p_requestVO);
                    break;
                }
            case ACTION_CHNL_EVD_XML:
                {
                    ExtAPIXMLStringParser.parseExtEVDRequest(p_requestVO);
                    break;
                }
            case ACTION_C2C_TRANSFER_EXT_XML:
                {
                    ExtAPIXMLStringParser.parseExtC2CTransferRequest(p_requestVO, ACTION_C2C_TRANSFER_EXT_XML);
                    break;
                }
            case ACTION_C2C_RETURN_EXT_XML:
                {
                    ExtAPIXMLStringParser.parseExtC2CTransferRequest(p_requestVO, ACTION_C2C_RETURN_EXT_XML);
                    break;
                }
            case ACTION_C2C_WITHDRAW_EXT_XML:
                {
                    ExtAPIXMLStringParser.parseExtC2CTransferRequest(p_requestVO, ACTION_C2C_WITHDRAW_EXT_XML);
                    break;
                }
            case ACTION_CHNL_EXT_CREDIT_TRANSFER_CDMA:
                {
                    ExtAPIXMLStringParser.parseChannelExtCreditTransferRequestCDMA(p_requestVO);
                    break;
                }
            case ACTION_CHNL_EXT_CREDIT_TRANSFER_PSTN:
                {
                    ExtAPIXMLStringParser.parseChannelExtCreditTransferRequestPSTN(p_requestVO);
                    break;
                }
            case ACTION_CHNL_EXT_CREDIT_TRANSFER_INTR:
                {
                    ExtAPIXMLStringParser.parseChannelExtCreditTransferRequestINTR(p_requestVO);
                    break;
                }
            case ACTION_CHNL_EXT_VAS_SELLING: // for vas services CRBT
                {
                    ExtAPIXMLStringParser.parseExtVasSellingRequest(p_requestVO);
                    break;
                }
            case ACTION_C2S_TRANS_ENQ:
                {
                    ExtAPIXMLStringParser.parseEXTC2STransferEnqRequest(p_requestVO);// for
                    // channel
                    // transaction
                    // enquiry
                    // request
                    // date/msisdn
                    // by
                    // RahulD
                    break;
                }
            /*
             * Added By Babu Kunwar For Last Transaction Details
             */
            case ACTION_EXT_LAST_TRF:// Last Transaction Details
                {
                    ExtAPIXMLStringParser.parseExtChannelLastTransferStatusRequest(p_requestVO);
                    break;
                }
            /*
             * /Added By Babu Kunwar For Cahnnel Balance Enquiry
             */
            case ACTION_CHNL_BAL_ENQ_XML:
                {
                    ExtAPIXMLStringParser.parseExtChannelUserBalanceRequest(p_requestVO);
                    break;
                }
            /*
             * /Added By Babu Kunwar For Other User Balance
             */
            case ACTION_EXT_OTHER_BAL_ENQ:
                {
                    ExtAPIXMLStringParser.parseExtUserOtherBalEnqReq(p_requestVO);
                    break;
                }
            /*
             * Added By Babu Kunwar For Change_Pin
             */
            case ACTION_EXT_C2SCHANGEPIN_XML:
                {
                    // ExtAPIXMLStringParser.parseExtChangepinRequest(p_requestVO);
                    ExtAPIStringParser.parseExtStringRequest(p_requestVO);
                    boolean userEventRemark = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_EVENT_REMARKS);
                    if(userEventRemark){
                    	 p_requestVO.setRemarks((String) p_requestVO.getRequestMap().get("REMARKS"));	
                    }
                    p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails((String) p_requestVO.getRequestMap().get("LANGUAGE1")));
                    p_requestVO.setRequestMSISDN((String) p_requestVO.getRequestMap().get("MSISDN"));
                    p_requestVO.setExternalNetworkCode((String) p_requestVO.getRequestMap().get("EXTNWCODE"));
                    p_requestVO.setExternalReferenceNum((String) p_requestVO.getRequestMap().get("EXTREFNUM"));
                    p_requestVO.setSenderLoginID((String) p_requestVO.getRequestMap().get("LOGINID"));
                    p_requestVO.setSenderExternalCode((String) p_requestVO.getRequestMap().get("EXTCODE"));
                    break;
                }
            /*
             * /Added By Babu Kunwar For Last 3 Transaction
             */
            case ACTION_EXT_LAST_XTRF_ENQ:
                {
                    ExtAPIXMLStringParser.parseExtLastXTransferEnq(p_requestVO);
                    break;
                }
            /*
             * /Added By Babu Kunwar For Customer Enquiry
             */
            case ACTION_EXT_CUSTOMER_ENQ_REQ:
                {
                    ExtAPIXMLStringParser.parseExtCustomerEnqReq(p_requestVO);
                    break;
                }
            /*
             * Added By Babu Kunwar For Daily Transfer Reports
             */
            case ACTION_EXT_DAILY_STATUS_REPORT:
                {
                    ExtAPIXMLStringParser.parseExtDailyTransactionRequest(p_requestVO);
                    break;
                }
            case ACTION_ENQUIRY_TXNIDEXTCODEDATE:
                {
                    ExtAPIXMLStringParser.parseEnquiryTxnIDExtCodeDateRequest(p_requestVO);
                    break;
                }
            case ACTION_EXT_PRIVATERC_XML: // For Private Recharge
                {
                    ExtAPIXMLStringParser.parseExtPrivateRechargeRequest(p_requestVO);
                    break;
                }
            /*
             * Added By Harpreet Kaur For EVR
             */
            case ACTION_CHNL_EVR_XML:
                {
                    ExtAPIXMLStringParser.parseExtEVRRequest(p_requestVO);
                    break;
                }
                /*
                 * Added By Abhilasha For UmniahEVR
                 */
                case ACTION_CHNL_EVR_UMNIAH_XML :
                    {
                        ExtAPIXMLStringParser.parseExtEVRRequest(p_requestVO);
                        break;
                    }
            case ACTION_EXT_DRCR_C2C_CUSER:
                {
                    ExtAPIXMLStringParser.parseExtC2CTrfDrCrRequest(p_requestVO, ACTION_EXT_DRCR_C2C_CUSER);// For
                    // DrCr
                    // Transfer
                    // Through
                    // External
                    // Gateway
                    break;
                }

            /*
             * Added By GAURAV PANDEY for suspend resume channel user through
             * External gateway (road map 5.8)
             */
            case ACTION_SUSPEND_RESUME_CUSR_EXTGW:
                {
                    ExtAPIXMLStringParser.parseChannelUserSuspendResumeEx(p_requestVO);
                    break;
                }

            // vastrix ADDED BY HITESH
            case ACTION_EXTVAS_RC_REQUEST:
                {
                    ExtAPIXMLStringParser.parseChannelExtVASTransferRequest(p_requestVO);
                    break;
                }
            case ACTION_EXTPVAS_RC_REQUEST:
                {
                    ExtAPIXMLStringParser.parseChannelExtPrVASTransferRequest(p_requestVO);
                    break;
                }
            case ACTION_EXT_GEOGRAPHY_REQUEST:
                {
                    ExtAPIXMLStringParser.parseExtGeographyRequest(p_requestVO);
                    break;
                }
            case ACTION_EXT_TRF_RULE_TYPE_REQ:
                {
                    ExtAPIXMLStringParser.parseExtTrfRuleTypeRequest(p_requestVO);
                    break;
                }
            case ACTION_EXT_USERADD_REQUEST:
                {
                    ExtAPIXMLStringParser.parseExtUserRequest(p_requestVO);
                    break;
                }
            case ACTION_EXT_SUBENQ:
                {
                    ExtAPIXMLStringParser.parseChannelExtSubscriberEnqRequest(p_requestVO);// added
                    // by
                    // sonali
                    // garg
                    break;
                }
            // added by arvinder through External Gateway/
            case ACTION_EXT_HLPDESK_REQUEST:
                {
                    ExtAPIXMLStringParser.parseChannelExtHelpDeskRequest(p_requestVO);
                    break;
                }
            // added by akanksha gupta for claro
            case ACTION_O2C_SAP_ENQUIRY:
                {
                    ExtAPIXMLStringParser.parseO2CExtSAPEnqRequest(p_requestVO);
                    break;
                }

            case ACTION_O2C_SAP_EXTCODE_UPDATE:
                {
                    ExtAPIXMLStringParser.parseO2CExtCodeUpdateRequest(p_requestVO);
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
            // Added By Diwakar on 20-JAN-2014 for ROBI as all the request will
            // be
            // handled by Operator Receiver only.
            case ADD_USER_ACTION:
                {
                    ExtAPIXMLStringParser.parseUserAddRequest(p_requestVO);
                    break;
                }
            case MODIFY_USER_ACTION:
                {
                    ExtAPIXMLStringParser.parseUserModifyRequest(p_requestVO);
                    break;
                }
            case DELETE_USER_ACTION:
                {
                    ExtAPIXMLStringParser.parseUserDeleteRequest(p_requestVO);
                    break;
                }
            case SUSPEND_RESUME_USER_ACTION:
                {
                    ExtAPIXMLStringParser.parseUserSuspendOrResumeRequest(p_requestVO);
                    break;
                }
            case ADD_DELETE_USER_ROLE_ACTION:
                {
                    ExtAPIXMLStringParser.parseUserRoleAddOrDeleteRequest(p_requestVO);
                    break;
                }
            case CHANGE_PASSWORD_ACTION:
                {
                    ExtAPIXMLStringParser.parseChangePasswordRequest(p_requestVO);
                    break;
                }
            case MNP_ACTION:
                {
                    ExtAPIXMLStringParser.parseMNPUploadRequest(p_requestVO);
                    break;
                }
            case ICCID_MSISDN_MAP_ACTION:
                {
                    ExtAPIXMLStringParser.parseMSISDNAssociationWithICCIDRequest(p_requestVO);
                    break;
                }
            // Ended Here By Diwakar
            // added by Vikas Singh
            case ACTION_C2S_PRE_PAID_REVERSAL:
                {
                    ExtAPIXMLStringParser.parsePrepaidRCReversalRequest(p_requestVO);
                    break;
                }
            case ACTION_CHNL_EXT_ROAM_RECHARGE:
                {
                    XMLStringParser.parseExtRoamRechargeRequest(p_requestVO);
                    break;
                }
            // ended
            // Added By Surabhi
            case GET_MY_MSISDN:
                {
                    ExtAPIXMLStringParser.parseGetMyNumberRequest(p_requestVO);
                    break;
                }
            case ACTION_EXT_C2SCHANGEMSISDN:
                { // for MSISDN Change Functionality
                    ExtAPIXMLStringParser.parseChannelExtChangeMsisdn(p_requestVO);
                    break;
                }
            case ACTION_EXT_C2SRECHARGESTATUS:
                { // for ETU Change Recharge Status
                    ExtAPIXMLStringParser.parseChannelExtETopUpRechargeStatus(p_requestVO);
                    break;
                }
            // ended
            // Added By Narendra
            case ACTION_CHNL_EXT_WARRANTY_TRANSFER:
                {
                    ExtAPIXMLStringParser.parseChannelExtWarrantyRechargeRequest(p_requestVO);
                    break;
                }
            case ACTION_CHNL_EXT_ADVANCE_TRANSFER:
                {
                    ExtAPIXMLStringParser.parseChannelExtAdvanceRechargeRequest(p_requestVO);
                    break;
                }
            case ACTION_CHNL_EXT_CAUTION_TRANSFER:
                {
                    ExtAPIXMLStringParser.parseChannelExtCautionRechargeRequest(p_requestVO);
                    break;
                }
            // ended
            // brajesh
            case ACTION_CHNL_LMS_POINTS_ENQUIRY:
                {
                    // ExtAPIXMLStringParser.parseLMSPointsEnquiryRequestEXTGW(p_requestVO);
                    ExtAPIStringParser.parseExtStringRequest(p_requestVO);
                    p_requestVO.setRequestMSISDN((String) p_requestVO.getRequestMap().get("MSISDN"));
                    if (p_requestVO.getRequestGatewayType().equalsIgnoreCase(PretupsI.REQUEST_SOURCE_TYPE_EXTGW)) {
                        if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("EXTNWCODE"))) {
                            p_requestVO.setExternalNetworkCode((String) p_requestVO.getRequestMap().get("EXTNWCODE"));
                        }
                        if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("EXTCODE"))) {
                            p_requestVO.setSenderExternalCode((String) p_requestVO.getRequestMap().get("EXTCODE"));
                        }
                        if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("LOGINID"))) {
                            p_requestVO.setSenderLoginID((String) p_requestVO.getRequestMap().get("LOGINID"));
                        }
                        if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("PASSWORD"))) {
                            p_requestVO.setPassword((String) p_requestVO.getRequestMap().get("PASSWORD"));
                        }
                    }
                    if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("LANGUAGE1"))) {
                       // p_requestVO.setDecryptedMessage(p_requestVO.getDecryptedMessage() + (String) p_requestVO.getRequestMap().get("LANGUAGE1"));
						p_requestVO.setLocale(LocaleMasterCache.getLocaleFromCodeDetails((String)p_requestVO.getRequestMap().get("LANGUAGE1")));
					
                    }
                    break;
                }
            case ACTION_CHNL_LMS_POINTS_REDEMPTION:
                {
                    // ExtAPIXMLStringParser.parseLMSPointsRedemptionRequestEXTGW(p_requestVO);
                    ExtAPIStringParser.parseExtStringRequest(p_requestVO);
                    p_requestVO.setRequestMSISDN((String) p_requestVO.getRequestMap().get("MSISDN"));
                    if (p_requestVO.getRequestGatewayType().equalsIgnoreCase(PretupsI.REQUEST_SOURCE_TYPE_EXTGW)) {
                        if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("EXTNWCODE"))) {
                            p_requestVO.setExternalNetworkCode((String) p_requestVO.getRequestMap().get("EXTNWCODE"));
                        }
                        if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("EXTCODE"))) {
                            p_requestVO.setSenderExternalCode((String) p_requestVO.getRequestMap().get("EXTCODE"));
                        }
                        if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("LOGINID"))) {
                            p_requestVO.setSenderLoginID((String) p_requestVO.getRequestMap().get("LOGINID"));
                        }
                        if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("PASSWORD"))) {
                            p_requestVO.setPassword((String) p_requestVO.getRequestMap().get("PASSWORD"));
                        }
                    }
                    if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("LANGUAGE1"))) {
                        //p_requestVO.setDecryptedMessage(p_requestVO.getDecryptedMessage() +" "+(String) p_requestVO.getRequestMap().get("LANGUAGE1"));
                    	p_requestVO.setLocale(LocaleMasterCache.getLocaleFromCodeDetails((String)p_requestVO.getRequestMap().get("LANGUAGE1")));
    					
                    }
                    break;
                }
            case ACTION_VOUCHER_CONSUMPTION:
                {
                    ExtAPIXMLStringParser.parseVoucherConsumptionRequest(p_requestVO);
                    break;
                }
            case GET_CHNL_USR_INFO:
                {
                    ExtAPIXMLStringParser.parseChannelInfoRequest(p_requestVO);
                    break;
                }
            case ACTION_VOUCHER_CONSUMPTION_O2C:
                {
                    ExtAPIXMLStringParser.parseVoucherConsumptionO2CRequest(p_requestVO);
                    break;
                }
            case ACTION_CHNL_EXT_ROAM_RECHARGE_REVERSAL:
                {

                    XMLStringParser.parseExtRoamRechargeReversalRequest(p_requestVO);
                    break;
                }
				/*
			 * /Added new request type specific for Last X Transfer Service Wise request
			 */
			case ACTION_EXT_LAST_XTRF_SRVCWISE_ENQ:
			{
				XMLStringParser.parseExtLastXTransferServiceWiseEnq(p_requestVO);
			    break;
			}
			case ACTION_CHNL_LITE_RECHARGE:/*ADDED for lite recharge though EXTGW*/
			{
				ExtAPIStringParser.parseExtStringRequest(p_requestVO);
				if(!BTSLUtil.isNullString(((String)p_requestVO.getRequestMap().get("LANGUAGE1"))) && LocaleMasterCache.getLocaleFromCodeDetails(((String)p_requestVO.getRequestMap().get("LANGUAGE1"))) == null) {
					p_requestVO.setSuccessTxn(false);
					 final String tagName = XmlTagValueConstant.TAG_LANGUAGE1;
					 String[] str  = {tagName};
					throw new BTSLBaseException("ExtAPIParser","parseChannelRequestMessage",PretupsErrorCodesI.INVALID_LANGUAGE_CODE,str);
				}
				p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(((String)p_requestVO.getRequestMap().get("LANGUAGE1"))));
				p_requestVO.setReqSelector((String)p_requestVO.getRequestMap().get("SELECTOR"));
				p_requestVO.setRequestMSISDN((String)p_requestVO.getRequestMap().get("MSISDN1"));
				p_requestVO.setExternalNetworkCode((String)p_requestVO.getRequestMap().get("EXTNWCODE"));
				break;
			}
			case ACTION_USER_CARDGROUP_ENQUIRY_REQUEST: 
			{
				ExtAPIStringParser.parseExtStringRequest(p_requestVO);
				
				if((BTSLUtil.isNullString((String)p_requestVO.getRequestMap().get("MSISDN1")))){
					p_requestVO.setSuccessTxn(false);
					throw new BTSLBaseException("ExtAPIParser","parseChannelRequestMessage",PretupsErrorCodesI.ERROR_INVALID_MESSAGE_FORMAT);
				}
				if(BTSLUtil.isNullString(p_requestVO.getRequestMap().get("SERVICETYPE").toString()))
				{
					p_requestVO.setSuccessTxn(false);
					 throw new BTSLBaseException("ExtAPIParser","parseChannelRequestMessage",PretupsErrorCodesI.CARDGROUP_ENQUIRY_SERVICETYPE_BLANK);
				}
				
				if(BTSLUtil.isNullString(p_requestVO.getRequestMap().get("PIN").toString()))
				{
					p_requestVO.setSuccessTxn(false);
					 throw new BTSLBaseException("ExtAPIParser","parseChannelRequestMessage",PretupsErrorCodesI.PIN_REQUIRED);
				}
				p_requestVO.setRequestMSISDN((String)p_requestVO.getRequestMap().get("MSISDN1"));
				p_requestVO.setPin((String)p_requestVO.getRequestMap().get("PIN"));
				p_requestVO.setExternalNetworkCode((String)p_requestVO.getRequestMap().get("EXTNWCODE"));
				p_requestVO.setReceiverMsisdn((String)p_requestVO.getRequestMap().get("MSISDN1"));
				p_requestVO.setEnquiryServiceType((String)p_requestVO.getRequestMap().get("SERVICETYPE"));
				break;
			}
			case ACTION_CHNL_CARDGROUP_ENQUIRY_REQUEST: 
			{
				ExtAPIStringParser.parseExtStringRequest(p_requestVO);
				
				if(BTSLUtil.isNullString((String)p_requestVO.getRequestMap().get("MSISDN1")) && BTSLUtil.isNullString((String)p_requestVO.getRequestMap().get("LOGINID"))){
					p_requestVO.setSuccessTxn(false);
					throw new BTSLBaseException("USSDParsers","parseChannelRequestMessage",PretupsErrorCodesI.MANDATORY_EMPTY);
				}
				/*if((!BTSLUtil.isNullString((String)p_requestVO.getRequestMap().get("MSISDN1")) && BTSLUtil.isNullString((String)p_requestVO.getRequestMap().get("PIN")))){
					p_requestVO.setSuccessTxn(false);
					throw new BTSLBaseException("USSDParsers","parseChannelRequestMessage",PretupsErrorCodesI.MANDATORY_EMPTY);
				}*/
				if((!BTSLUtil.isNullString((String)p_requestVO.getRequestMap().get("LOGINID")) && BTSLUtil.isNullString((String)p_requestVO.getRequestMap().get("PASSWORD")))){
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
				p_requestVO.setSenderLoginID((String)p_requestVO.getRequestMap().get("LOGINID"));
				p_requestVO.setRequestMSISDN((String)p_requestVO.getRequestMap().get("MSISDN1"));
				p_requestVO.setExternalNetworkCode((String)p_requestVO.getRequestMap().get("EXTNWCODE"));
				p_requestVO.setReceiverMsisdn((String)p_requestVO.getRequestMap().get("MSISDN2"));
				p_requestVO.setEnquiryServiceType((String)p_requestVO.getRequestMap().get("SERVICETYPE"));
				p_requestVO.setEnquirySubService((String)p_requestVO.getRequestMap().get("SUBSERVICE"));
				p_requestVO.setEnquiryAmount((String)p_requestVO.getRequestMap().get("AMOUNT"));
				break;
			}
			case ACTION_EXTPROMOPVAS_REQUEST: 
			{
				XMLStringParser.parsePromoVasTransferRequest(p_requestVO);
				break;	
			}
			case ACTION_EXTPROMOINTLTRFREQ_REQUEST: 
			{
				XMLStringParser.parseIntlTransferRequest(p_requestVO);
				break;	
			}
            case ACTION_CHNL_DAILY_STATUS_REPORT:
                {
                	ExtAPIStringParser.parseExtStringRequest(p_requestVO);
                	p_requestVO.setRequestMSISDN((String)p_requestVO.getRequestMap().get("MSISDN1"));
    				p_requestVO.setExternalNetworkCode((String)p_requestVO.getRequestMap().get("EXTNWCODE"));    				
    				if (p_requestVO.getRequestGatewayType().equalsIgnoreCase(PretupsI.REQUEST_SOURCE_TYPE_EXTGW)) {
                        if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("LOGINID"))) {
                            p_requestVO.setSenderLoginID((String) p_requestVO.getRequestMap().get("LOGINID"));
                        }
                        if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("PASSWORD"))) {
                            p_requestVO.setPassword((String) p_requestVO.getRequestMap().get("PASSWORD"));
                        }
                    }
                    break;
                }
            case ACTION_EXDATATRFREQ_REQUEST: // added for DATA Recharge
            {
                ExtAPIXMLStringParser.parseC2STransferRequest(p_requestVO,ACTION_EXDATATRFREQ_REQUEST);
                    break;
                }
            case ACTION_EXT_DSR_REQUEST: // added for DSR Report for Self and Child
            {
            	ExtAPIStringParser.parseExtStringRequest(p_requestVO);
            	p_requestVO.setRequestMSISDN((String)p_requestVO.getRequestMap().get("MSISDN1"));
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
                    
                    if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("MSISDN2"))) {
                    	p_requestVO.setMsisdn((String)p_requestVO.getRequestMap().get("MSISDN2"));
                    }
                    if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("ENQ_DATE"))) {
                    	p_requestVO.setReqDate((String)p_requestVO.getRequestMap().get("ENQ_DATE"));
                    } else {
                    	throw new BTSLBaseException("ExtAPIParser", methodName, PretupsErrorCodesI.MANDATORY_EMPTY);
                    }
                }
                break;
            }
            case ACTION_EXT_STOCK_BALANCE_REQUEST: 
            {
            	ExtAPIStringParser.parseExtStringRequest(p_requestVO);
            	p_requestVO.setRequestMSISDN((String)p_requestVO.getRequestMap().get("MSISDN1"));
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
                    
                    if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("MSISDN2"))) {
                    	p_requestVO.setMsisdn((String)p_requestVO.getRequestMap().get("MSISDN2"));
                    }
                }
                break;
            }
            case ACTION_CHNL_EXT_BULK_RCH_REVERSAL:/*ADDED for lite bulk recharge reversal though EXTGW*/
			{
			  
				ExtAPIStringParser.parseExtStringRequest(p_requestVO);
				p_requestVO.setRequestMSISDN((String)p_requestVO.getRequestMap().get("MSISDN"));
				p_requestVO.setExternalNetworkCode((String)p_requestVO.getRequestMap().get("EXTNWCODE"));
				p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(((String)p_requestVO.getRequestMap().get("LANGUAGE1"))));
				p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(((String)p_requestVO.getRequestMap().get("LANGUAGE2"))));
				break;
			}
            case ACTION_VOUCHER_STATUS_CHANGES: 
            {
            	ExtAPIStringParser.parseExtStringRequest(p_requestVO);     
            	p_requestVO.setRequestMessageArray(PretupsBL.parsePlainMessage(p_requestVO.getDecryptedMessage()));
            }
            case ACTION_VOUCHER_EXPIRY_CHANGES: 
            {
            	ExtAPIStringParser.parseExtStringRequest(p_requestVO);     
            	p_requestVO.setRequestMessageArray(PretupsBL.parsePlainMessage(p_requestVO.getDecryptedMessage()));
            }
            case ACTION_CHNL_SOS_SETTLEMENT_REQUEST:
            {
            	ExtAPIStringParser.parseExtStringRequest(p_requestVO);
            	p_requestVO.setRequestMSISDN((String)p_requestVO.getRequestMap().get("MSISDN"));
            	p_requestVO.setExternalNetworkCode((String)p_requestVO.getRequestMap().get("EXTNWCODE"));
            	p_requestVO.setSenderLoginID((String)p_requestVO.getRequestMap().get("LOGINID"));
            	p_requestVO.setSenderExternalCode((String)p_requestVO.getRequestMap().get("EXTCODE"));
            	break;
            }
            case ACTION_CHNL_SOS_REQUEST:
            {
            	ExtAPIStringParser.parseExtStringRequest(p_requestVO);
            	p_requestVO.setRequestMSISDN((String)p_requestVO.getRequestMap().get("MSISDN"));
            	p_requestVO.setExternalNetworkCode((String)p_requestVO.getRequestMap().get("EXTNWCODE"));
            	p_requestVO.setSenderLoginID((String)p_requestVO.getRequestMap().get("LOGINID"));
            	p_requestVO.setSenderExternalCode((String)p_requestVO.getRequestMap().get("EXTCODE"));
            	p_requestVO.setProductCode((String)p_requestVO.getRequestMap().get("PRODUCTCODE"));
            	break;
            }
			case ACTION_VAS_VOUCHER_CONSUMPTION: 
			{
				ExtAPIXMLStringParser.parseVASVoucherConsumptionRequest(p_requestVO);
				break;	
			}
			//Added for C2C_O2C Txn Status through EXTGW
			case ACTION_C2C_O2C_TXN_STATUS:
			{
				XMLStringParser.parseExtChannelTxnStatusRequest(p_requestVO);
			  	p_requestVO.setRequestMSISDN((String)p_requestVO.getRequestMap().get("MSISDN"));
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
			case ACTION_TPS_MAX_CALCULATION:
			{
				ExtAPIStringParser.parseExtStringRequest(p_requestVO);
				p_requestVO.setExternalNetworkCode((String)p_requestVO.getRequestMap().get("EXTNWCODE"));
				break;
			}
			case ACTION_SOS_FLAG_UPDATE_REQ: 
			{
			   	ExtAPIStringParser.parseExtStringRequest(p_requestVO);
	        	p_requestVO.setRequestMSISDN((String)p_requestVO.getRequestMap().get("MSISDN"));
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
	                
	                if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("MSISDN2"))) {
	                	p_requestVO.setMsisdn((String)p_requestVO.getRequestMap().get("MSISDN2"));
	                }
	                else {
	                	throw new BTSLBaseException("ExtAPIParser", methodName, PretupsErrorCodesI.MANDATORY_EMPTY);
	                }
	            }
	            break;
					
			}
			
			case ACTION_CHNL_VOUCHER_AVAILABILITY_XML:
            {
                ExtAPIXMLStringParser.parseExtDigitalVouchersAvailabilityRequest(p_requestVO);
                break;
            }
			case ACTION_CHNL_DVD_XML:
            {
                ExtAPIXMLStringParser.parseExtDVDRequest(p_requestVO);
                break;
            }
			case ACTION_VOMS_O2C:
			{
				ExtAPIXMLStringParser.parseVoucherO2CTransferRequest(ACTION_VOMS_O2C,p_requestVO);
			}
			case ACTION_C2C_REQ_REC:
			{
				ExtAPIXMLStringParser.parseExtC2CTransferRequestReceiver(p_requestVO, ACTION_C2C_REQ_REC);
                break;
			}
			case ACTION_C2C_APPR:
			{
				ExtAPIXMLStringParser.parseExtC2CApproval(p_requestVO, ACTION_C2C_APPR);
                break;
			}
			case ACTION_C2C_VOUCHER_APPR:
			{
				ExtAPIXMLStringParser.parseExtC2CVoucherApproval(p_requestVO, ACTION_C2C_VOUCHER_APPR);
                break;
			}
			case ACTION_C2C_VOMS_TRF:
			{
				ExtAPIXMLStringParser.parseExtC2CVomsTransferRequest(p_requestVO);
                break;
			}
			case ACTION_C2C_VOMS_INI:
			{
				ExtAPIXMLStringParser.parseExtC2CVomsInitiateRequest(p_requestVO);
                break;
			}
			case CHANNEL_USER_DETAILS:
            {
                ExtAPIXMLStringParser.parseChannelUserDetailsRequest(p_requestVO);
                break;
            }
        	case C2S_TOTAL_TXN:
        	{
        		ExtAPIXMLStringParser.parseC2STotalTxnNoReq(p_requestVO);
        		break;
        	}
        	
        	case C2S_TOTAL_TRANSACTION_COUNT:
        	{
        		ExtAPIXMLStringParser.parseC2STotalTnxCountReq(p_requestVO);
        		break;
        	}
            
        	case C2S_PROD_TXN_DETAILS:
        	{
        		ExtAPIXMLStringParser.parseTxnCountDetails(p_requestVO,C2S_PROD_TXN_DETAILS);
        		break;
        	}
        	case PASSBOOK_VIEW_DETAILS:
        	{
        		ExtAPIXMLStringParser.parsePassbookViewDetailsRequest(p_requestVO);
        		break;
        	}
        	case C2S_N_PROD_TXN_DETAILS:
        	{
        		ExtAPIXMLStringParser.parseTxnCountDetails(p_requestVO,C2S_N_PROD_TXN_DETAILS);
        		break;
        	}
        	case TOTAL_TRANSACTION_DETAILED_VIEW:
        	{
        		ExtAPIXMLStringParser.totalTrnxDetailReq(p_requestVO);
        		break;
        	}
        	case COMMISSION_CALCULATOR:
        	{
        		ExtAPIXMLStringParser.commissionCalculatorReq(p_requestVO);
        		break;
        	}
        	case ACTION_CRM_USER_AUTH_XML: 
    		{
    			XMLStringParser.parseChannelUserAuthRequest(p_requestVO);
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
    		case ACTION_LST_LOAN_ENQ:
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
                    //p_requestVO.setExternalReferenceNum(((String)p_requestVO.getRequestMap().get("EXTREFNUM")));
                   
                }
                break;
    		}
    		
    		case ACTION_SELF_CUBAR:
    		{
    			ExtAPIXMLStringParser.parseSelfChannelUserBarRequest(p_requestVO);

    		
    			break;
    		}
    		
    		case ACTION_SELF_CU_UNBAR:
    		{
    			ExtAPIXMLStringParser.parseSelfChannelUserBarRequest(p_requestVO);

    			break;
    		}
    		
    		case ACTION_SELF_PIN_RESET:
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
    		case ACTION_LST_N_EVD_TRF: 
			{

			ExtAPIXMLStringParser.parseExtEVDLastNTxnRequest(p_requestVO);
			break;
			}

    		
        	default:
     	     	 if(LOG.isDebugEnabled()){
     	     		LOG.debug("Default Value " ,action);
     	     	 }  
        }
    }

    /**
     * Method to generate Response of Channel requests
     * 
     * @param p_requestVO
     * @throws Exception
     */
    public void generateChannelParserResponse(RequestVO p_requestVO) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("generateChannelParserResponse", "Entered Request ID=" + p_requestVO.getRequestID());
        }
        int action = p_requestVO.getActionValue();

        String messageCode = p_requestVO.getMessageCode();
        if ((!BTSLUtil.isNullString(messageCode)) && (!p_requestVO.isSuccessTxn()) && messageCode.indexOf("_") != -1) {
            messageCode = messageCode.substring(0, messageCode.indexOf("_"));
            p_requestVO.setMessageCode(messageCode);
        }
        switch (action) {
        
        case TOTAL_USER_INCOME_DETAILS_VIEW:
    	{
    		ExtAPIXMLStringParser.generateTotalIcomeDetailsViewResponse(p_requestVO);
    		break;
    	}
        
        	case ACTION_UPUSRHRCHY: {

        		ExtAPIXMLStringParser.generateUserHierarchyResponse(p_requestVO);
        		break;
        	}
        	case C2C_BUY_ENQ: {

        		ExtAPIXMLStringParser.generateC2cBuyUserResponse(p_requestVO);
        		break;
        	}
        	case C2S_SV_DETAILS: {

        		ExtAPIXMLStringParser.generateC2sServiceDetailsResponse(p_requestVO);
        		break;
        	}

            case ACTION_CHNL_CREDIT_TRANSFER:
                {
                    ExtAPIXMLStringParser.generateChannelCreditTransferResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_CHANGE_PIN:
                {
                    ExtAPIXMLStringParser.generateChannelChangeResponse(p_requestVO, "RCPNRESP");
                    break;
                }
            case ACTION_CHNL_NOTIFICATION_LANGUAGE:
                {
                    // ExtAPIXMLStringParser.generateChannelNotificationLanguageResponse(p_requestVO);
                    ExtAPIXMLStringParser.generateChannelChangeResponse(p_requestVO, "RCLANGRESP");
                    break;
                }
            case ACTION_CHNL_TRANSFER_MESSAGE:
                {
                    // ExtAPIXMLStringParser.generateChannelTransferResponse(p_requestVO,ACTION_CHNL_TRANSFER_MESSAGE);
                    ExtAPIXMLStringParser.generateChannelChangeResponse(p_requestVO, "TRFRESP");
                    break;
                }
            case ACTION_CHNL_WITHDRAW_MESSAGE:
                {
                    // ExtAPIXMLStringParser.generateChannelTransferResponse(p_requestVO,ACTION_CHNL_WITHDRAW_MESSAGE);
                    ExtAPIXMLStringParser.generateChannelChangeResponse(p_requestVO, "WDTHRESP");
                    break;
                }
            case ACTION_CHNL_RETURN_MESSAGE:
                {
                    // ExtAPIXMLStringParser.generateChannelTransferResponse(p_requestVO,ACTION_CHNL_RETURN_MESSAGE);
                    ExtAPIXMLStringParser.generateChannelChangeResponse(p_requestVO, "RETRESP");
                    break;
                }

            case ACTION_CHNL_POSTPAID_BILLPAYMENT:
                {
                    ExtAPIXMLStringParser.generateChannelPostPaidBillPaymentResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_O2C_INITIATE:
                {
                    ExtAPIXMLStringParser.generateChannelO2CAPIResponse(p_requestVO, "O2CINRESP");
                    // Set Message Required flag to false as no SMS needs to be
                    // pushed
                    // in these Cases
                    p_requestVO.setSenderMessageRequired(false);
                    break;
                }
            case ACTION_CHNL_O2C_INITIATE_TRFR:
                {
                    ExtAPIXMLStringParser.generateChannelO2CAPIResponse(p_requestVO, "O2CINTRESP");
                    break;
                }
            case ACTION_CHNL_O2C_RETURN:
                {
                    // ExtAPIXMLStringParser.generateChannelO2CReturnAPIResponse(p_requestVO);
                    ExtAPIXMLStringParser.generateChannelO2CAPIResponse(p_requestVO, "OCRETRESP");
                    break;
                }
            case ACTION_CHNL_O2C_WITHDRAW:
                {
                    // ExtAPIXMLStringParser.generateChannelO2CWithdrawAPIResponse(p_requestVO);
                    ExtAPIXMLStringParser.generateChannelO2CAPIResponse(p_requestVO, "O2CWDRESP");
                    break;
                }
            case ACTION_CHNL_EXT_RECH_STATUS:
                {
                    ExtAPIXMLStringParser.generateChannelExtRechargeStatusResponse(p_requestVO);
                    // Set Message Required flag to false as no SMS needs to be
                    // pushed
                    // in these Cases
                    p_requestVO.setSenderMessageRequired(false);
                    break;
                }
            case ACTION_CHNL_EXT_CREDIT_TRANSFER:
                {
                    // ExtAPIXMLStringParser.generateC2STransferResponse(p_requestVO,ACTION_CHNL_EXT_CREDIT_TRANSFER);
                    ExtAPIXMLStringParser.generateC2STransferResponse(p_requestVO, "EXRCTRFRESP");
                    // Set Message Required flag to false as no SMS needs to be
                    // pushed
                    // in these Cases
                    p_requestVO.setSenderMessageRequired(false);
                    break;
                }
            case ACTION_VOU_PRF_MOD_REQ:
            {
                ExtAPIXMLStringParser.generateVoucherProfileModificationResponse(p_requestVO, "EXVOUPRFMODRESP");
                p_requestVO.setSenderMessageRequired(false);
                break;
            }
            case ACTION_CHNL_EXT_TRANSFER_BILL_PAYMENT:
                {
                    // ExtAPIXMLStringParser.generateExtPostpaidBillPaymentResponse(p_requestVO);
                    ExtAPIXMLStringParser.generateC2STransferResponse(p_requestVO, "EXTPPBRESP");
                    p_requestVO.setSenderMessageRequired(false);
                    break;
                }
            case ACTION_CHNL_EXT_ENQUIRY_REQUEST:
                {
                	if(p_requestVO.getResponseMap() != null) {
                	p_requestVO.getResponseMap().put("TYPE","EXTSYSENQRESP");
                	if("MO".equalsIgnoreCase(p_requestVO.getExternalNetworkCode())){
                		p_requestVO.getResponseMap().put("SLABAMT",p_requestVO.getSlabDetails());
                	} else {
                		p_requestVO.getResponseMap().put("SLABAMOUNT",p_requestVO.getSlabDetails());
                	}
    				p_requestVO.getResponseMap().put("EXTTXNNUMBER",p_requestVO.getExternalTransactionNum());
                	}
                	else{
                		HashMap ResponseMap = new HashMap(); 
                		p_requestVO.setResponseMap(ResponseMap);
                		p_requestVO.getResponseMap().put("TYPE","EXTSYSENQRESP");
                		if("MO".equalsIgnoreCase(p_requestVO.getExternalNetworkCode())){
                    		p_requestVO.getResponseMap().put("SLABAMT",p_requestVO.getSlabDetails());
                    	} else {
                    		p_requestVO.getResponseMap().put("SLABAMOUNT",p_requestVO.getSlabDetails());
                    	}
        				p_requestVO.getResponseMap().put("EXTTXNNUMBER",p_requestVO.getExternalTransactionNum());
                	}
    				ExtAPIStringParser.populateResponseMap(p_requestVO,PretupsErrorCodesI.C2S_ENQUIRY_SUCCESS);
    				ExtAPIStringParser.generateExtStringResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_EXT_COMMON_RECHARGE:
                {
                    // ExtAPIXMLStringParser.generateChannelExtCommonRechargeResponse(p_requestVO);
                    ExtAPIXMLStringParser.generateC2STransferResponse(p_requestVO, "CEXRCTRFRESP");
                    p_requestVO.setSenderMessageRequired(false);
                    break;
                }
            case ACTION_CHNL_EXT_POST_RECHARGE_STATUS:
                {
                    ExtAPIXMLStringParser.generateChannelExtPostRechargeStatusResponse(p_requestVO);
                    // Set Message Required flag to false as no SMS needs to be
                    // pushed
                    // in these Cases
                    p_requestVO.setSenderMessageRequired(false);
                    break;
                }
            case ACTION_CHNL_GIFT_RECHARGE_XML:
                {
                    ExtAPIXMLStringParser.generateExtGiftRechargeResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_BAL_ENQ_XML:
                {
                    ExtAPIXMLStringParser.generateExtChannelUserBalanceResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_EVD_XML:
                {
                    ExtAPIXMLStringParser.generateExtEVDResponse(p_requestVO);
                    break;
                }
            case ACTION_C2C_TRANSFER_EXT_XML:
                {
                    ExtAPIXMLStringParser.generateExtC2CTransferResponse(p_requestVO, ACTION_C2C_TRANSFER_EXT_XML);
                    p_requestVO.setSenderMessageRequired(false);
                    break;
                }
            case ACTION_C2C_WITHDRAW_EXT_XML:
                {
                    ExtAPIXMLStringParser.generateExtC2CTransferResponse(p_requestVO, ACTION_C2C_WITHDRAW_EXT_XML);
                    p_requestVO.setSenderMessageRequired(false);
                    break;
                }
            case ACTION_C2C_RETURN_EXT_XML:
                {
                    ExtAPIXMLStringParser.generateExtC2CTransferResponse(p_requestVO, ACTION_C2C_RETURN_EXT_XML);
                    p_requestVO.setSenderMessageRequired(false);
                    break;
                }
            case ACTION_EXT_C2SCHANGEPIN_XML:
                {
                    // ExtAPIXMLStringParser.generateExtChangepinResponse(p_requestVO);
                	if(p_requestVO.getResponseMap() != null) {
                		p_requestVO.getResponseMap().put("TYPE", "EXC2SCPNRESP");
                        p_requestVO.getResponseMap().put("EXTREFNUM", p_requestVO.getExternalReferenceNum());
                    	}
                    	else{
                    		HashMap ResponseMap = new HashMap(); 
                    		p_requestVO.setResponseMap(ResponseMap);
                    		p_requestVO.getResponseMap().put("TYPE", "EXC2SCPNRESP");
                            p_requestVO.getResponseMap().put("EXTREFNUM", p_requestVO.getExternalReferenceNum());
                    	} 
                    ExtAPIStringParser.populateResponseMap(p_requestVO, PretupsErrorCodesI.EXTSYS_CHANGE_PIN_SUCCESS);
                    ExtAPIStringParser.generateExtStringResponse(p_requestVO);
                    p_requestVO.setSenderMessageRequired(true);
                    break;
                }
            case ACTION_CHNL_EXT_CREDIT_TRANSFER_CDMA:
                {
                    // ExtAPIXMLStringParser.generateChannelExtCreditTransferResponseCDMA(p_requestVO);
                    ExtAPIXMLStringParser.generateChannelExtTransferResponse(p_requestVO, "EXCDMARCRESP");
                    p_requestVO.setSenderMessageRequired(false);
                    break;
                }
            case ACTION_CHNL_EXT_CREDIT_TRANSFER_PSTN:
                {
                    // ExtAPIXMLStringParser.generateChannelExtCreditTransferResponsePSTN(p_requestVO);
                    ExtAPIXMLStringParser.generateChannelExtTransferResponse(p_requestVO, "EXPSTNRCRESP");
                    p_requestVO.setSenderMessageRequired(false);
                    break;
                }
            case ACTION_CHNL_EXT_CREDIT_TRANSFER_INTR:
                {
                    // ExtAPIXMLStringParser.generateChannelExtCreditTransferResponseINTR(p_requestVO);
                    ExtAPIXMLStringParser.generateChannelExtTransferResponse(p_requestVO, "EXINTRRCRESP");
                    p_requestVO.setSenderMessageRequired(false);
                    break;
                }
            case ACTION_CHNL_EXT_VAS_SELLING:
                {
                    // ExtAPIXMLStringParser.generateChnlVasSellingResponse(p_requestVO);
                    ExtAPIXMLStringParser.generateChannelExtTransferResponse(p_requestVO, "VASSELLRESP");
                    p_requestVO.setSenderMessageRequired(false);
                    break;
                }
            case ACTION_EXT_LAST_XTRF_ENQ: // last X C2S transfer
                {
                    ExtAPIXMLStringParser.generateExtLastXTrfResponse(p_requestVO);
                    break;
                }
            case ACTION_EXT_DAILY_STATUS_REPORT: // daily report
                {
                    ExtAPIXMLStringParser.generateExtDailyTransactionResponse(p_requestVO);
                    break;
                }
            case ACTION_C2S_TRANS_ENQ:
                {
                    ExtAPIXMLStringParser.generate2STransferEnqExtResp(p_requestVO);// for
                    // channel
                    // transaction
                    // enquiry
                    // request
                    // date/msisdn
                    // by
                    // RahulD
                    break;
                }
            case ACTION_ENQUIRY_TXNIDEXTCODEDATE:
                {
                    ExtAPIXMLStringParser.parseEnquiryTxnIDExtCodeDateResponse(p_requestVO);
                    break;
                }

            case ACTION_CHNL_EVR_XML:
                {
                    // ExtAPIXMLStringParser.generateExtEVRResponse(p_requestVO);
                    ExtAPIXMLStringParser.generateChannelExtTransferResponse(p_requestVO, "EXEVRTRFRESP");
                    break;
                }
            case ACTION_CHNL_EVR_UMNIAH_XML:
            {
                // ExtAPIXMLStringParser.generateExtEVRResponse(p_requestVO);
                ExtAPIXMLStringParser.generateChannelExtTransferResponse(p_requestVO, "EXEVRRESP");
                break;
            }
            case ACTION_EXT_PRIVATERC_XML: // For private Recharge
                {
                    // ExtAPIXMLStringParser.generateExtPrivateRechargeResponse(p_requestVO);
                    ExtAPIXMLStringParser.generateChannelExtTransferResponse(p_requestVO, "EXPVEVDRESP");
                    p_requestVO.setSenderMessageRequired(false);
                    break;
                }
            case ACTION_EXT_DRCR_C2C_CUSER:
                {
                    ExtAPIXMLStringParser.generateExtC2CTrfDrCrResponse(p_requestVO, ACTION_EXT_DRCR_C2C_CUSER);// For
                    // DrCr
                    // Transfer
                    // Through
                    // External
                    // Gateway
                    break;
                }
            case ACTION_SUSPEND_RESUME_CUSR_EXTGW: // added by gaurav pandey for
                // suspend resume channel
                // users
                // (road map 5.8)
                {
                    ExtAPIXMLStringParser.generateChannelUserSuspendResumeResponse(p_requestVO);
                    break;
                }
            // VASTRIX ADDDED BY HITESH
            case ACTION_EXTVAS_RC_REQUEST:
                {
                    ExtAPIXMLStringParser.generateVasExtCreditTransferResponse(p_requestVO);
                    break;
                }
            case ACTION_EXTPVAS_RC_REQUEST:
                {
                    ExtAPIXMLStringParser.generatePrVasExtCreditTransferResponse(p_requestVO);
                    break;
                }
            case ACTION_EXT_GEOGRAPHY_REQUEST:// BY ANUPAM MALVIYA TO GENERATE
                // RESPONSE OF GEOGRAPHY REQUEST
                // BY
                // EXT API
                {
                    ExtAPIXMLStringParser.generateExtGeographyResponse(p_requestVO);// 7/Nov/2012
                    p_requestVO.setSenderMessageRequired(false);
                    break;
                }
            case ACTION_EXT_TRF_RULE_TYPE_REQ: // BY ANUPAM MALVIYA TO GENERATE
                // RESPONSE OF TRANSFER RULE
                // REQUEST
                // BY EXT API
                {
                    ExtAPIXMLStringParser.generateExtTrfRuleTypeResponse(p_requestVO);
                    p_requestVO.setSenderMessageRequired(false);
                    break;
                }
            case ACTION_EXT_USERADD_REQUEST: // BY ANUPAM MALVIYA TO GENERATE
                // RESPONSE OF USER ADD REQUEST BY
                // EXT
                // API
                {
                    ExtAPIXMLStringParser.generateExtUserResponse(p_requestVO);
                    p_requestVO.setSenderMessageRequired(false);
                    break;
                }
            case ACTION_EXT_SUBENQ:
                {
                    ExtAPIXMLStringParser.generateChannelExtSubscriberEnqResponse(p_requestVO);// added
                    // by
                    // sonali
                    // garg
                    break;
                }
            case ACTION_EXT_LAST_TRF: // last X C2S transfer (new added
                // arvinder)
                {
                    ExtAPIXMLStringParser.generateExtChannelLastTransferStatusResponse(p_requestVO);
                    break;
                }

            case ACTION_EXT_HLPDESK_REQUEST:// added for Help Desk Service
                // Through
                // External Gateway
                {
                    ExtAPIXMLStringParser.generateChannelExtHelpDeskResponse(p_requestVO);
                    break;
                }
            case ACTION_EXT_OTHER_BAL_ENQ:
                {
                    ExtAPIXMLStringParser.generateExtChannelUserOtherBalRes(p_requestVO);
                    break;
                }
            case ACTION_O2C_SAP_ENQUIRY:
                {
                    ExtAPIXMLStringParser.generateO2CExtSAPEnqResponse(p_requestVO);// added
                    // by
                    // akanksha
                    // gupta
                    break;
                }
            case ACTION_O2C_SAP_EXTCODE_UPDATE:
                {
                    ExtAPIXMLStringParser.generateO2CExtCodeUpdateResponse(p_requestVO);// added
                    // by
                    // akanksha
                    // gupta
                    break;

                }
            case ACTION_COL_ENQ:
                {
                    ExtAPIXMLStringParser.generateChannelExtColEnqResponse(p_requestVO);
                    break;
                }
            case ACTION_COL_BILLPAYMENT:
                {
                    ExtAPIXMLStringParser.generateChannelCollectionBillPaymentResponse(p_requestVO);
                    break;
                }
            case ACTION_DTH:
                {
                    // ExtAPIXMLStringParser.generateC2STransferResponse(p_requestVO,ACTION_DTH);
                    ExtAPIXMLStringParser.generateC2STransferResponse(p_requestVO, "EXDTHTRFRESP");
                    break;
                }
            case ACTION_DC:
                {
                    // ExtAPIXMLStringParser.generateC2STransferResponse(p_requestVO,ACTION_DC);
                    ExtAPIXMLStringParser.generateC2STransferResponse(p_requestVO, "EXDCTRFRESP");
                    break;
                }
            case ACTION_BPB:
                {
                    // ExtAPIXMLStringParser.generateC2STransferResponse(p_requestVO,ACTION_BPB);
                    ExtAPIXMLStringParser.generateC2STransferResponse(p_requestVO, "EXBPBTRFRESP");
                    break;
                }
            case ACTION_PIN:
                {
                    // ExtAPIXMLStringParser.generateC2STransferResponse(p_requestVO,ACTION_PIN);
                    ExtAPIXMLStringParser.generateC2STransferResponse(p_requestVO, "EXPINTRFRESP");
                    break;
                }
            case ACTION_PMD:
                {
                    // ExtAPIXMLStringParser.generateC2STransferResponse(p_requestVO,ACTION_PMD);
                    ExtAPIXMLStringParser.generateC2STransferResponse(p_requestVO, "EXPMDTRFRESP");
                    break;
                }
            case ACTION_FLRC:
                {
                    // ExtAPIXMLStringParser.generateC2STransferResponse(p_requestVO,ACTION_FLRC);
                    ExtAPIXMLStringParser.generateC2STransferResponse(p_requestVO, "EXFLRCTRFRESP");
                    break;
                }
            case ACTION_C2S_POSTPAID_REVERSAL:
                {
                    ExtAPIXMLStringParser.generateC2SPostPaidReversalResponse(p_requestVO);
                    break;
                }
            // Added By Diwakar on 20-JAN-2014 for ROBI
            case ADD_USER_ACTION:
                {
                    ExtAPIXMLStringParser.generateUserAddResponse(p_requestVO);
                    break;
                }
            case MODIFY_USER_ACTION:
                {
                    ExtAPIXMLStringParser.generateUserModifyResponse(p_requestVO);
                    break;
                }
            case DELETE_USER_ACTION:
                {
                    ExtAPIXMLStringParser.generateUserDeleteResponse(p_requestVO);
                    break;
                }
            case SUSPEND_RESUME_USER_ACTION:
                {
                    ExtAPIXMLStringParser.generateUserSuspendOrResumeResponse(p_requestVO);
                    break;
                }
            case ADD_DELETE_USER_ROLE_ACTION:
                {
                    ExtAPIXMLStringParser.generateUserRoleAddOrDeleteResponse(p_requestVO);
                    break;
                }
            case CHANGE_PASSWORD_ACTION:
                {
                    ExtAPIXMLStringParser.generateChangePasswordResponse(p_requestVO);
                    break;
                }
            case MNP_ACTION:
                {
                    ExtAPIXMLStringParser.generateMNPUploadResponse(p_requestVO);
                    break;
                }
            case ICCID_MSISDN_MAP_ACTION:
                {
                    ExtAPIXMLStringParser.generateMSISDNAssociationWithICCIDResponse(p_requestVO);
                    break;
                }
            // Ended Here By Diwakar
            // added by Vikas Singh
            case ACTION_C2S_PRE_PAID_REVERSAL:
                {
                    // ExtAPIXMLStringParser.genratePrepaidRCReversalResponse(p_requestVO);
                    ExtAPIXMLStringParser.generateC2STransferResponse(p_requestVO, "RCREVRESP");
                    break;
                }
            case ACTION_CHNL_EXT_ROAM_RECHARGE:
                {
                    XMLStringParser.generateChnlRoamRechResponse(p_requestVO);
                    // Set Message Required flag to false as no SMS needs to be
                    // pushed
                    // in these Cases
                    p_requestVO.setSenderMessageRequired(false);
                    break;
                }
            case ACTION_EXT_C2SCHANGEMSISDN:
                { // for MSISDN Change Functionality
                    ExtAPIXMLStringParser.generateExtChangeMsisdnResponse(p_requestVO);
                    p_requestVO.setSenderMessageRequired(false);
                    break;
                }
            case ACTION_EXT_C2SRECHARGESTATUS:
                { // for ETU Change Recharge Status
                    ExtAPIXMLStringParser.generateExtETopUpRechargeStatusResponse(p_requestVO);
                    p_requestVO.setSenderMessageRequired(false);
                    break;
                }
            // ended
            // Added By Narendra
            case ACTION_CHNL_EXT_WARRANTY_TRANSFER:
                {
                    ExtAPIXMLStringParser.generateChannelExtWarrantyRechargeResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_EXT_ADVANCE_TRANSFER:
                {
                    ExtAPIXMLStringParser.generateChannelExtAdvanceRechargeResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_EXT_CAUTION_TRANSFER:
                {
                    ExtAPIXMLStringParser.generateChannelExtCautionRechargeResponse(p_requestVO);
                    break;
                }
            // ended
            // added by brajesh prasad for LMS Points Enquiry
            case ACTION_CHNL_LMS_POINTS_ENQUIRY:
                {
                    // ExtAPIXMLStringParser.generateLMSPointsEnquiryResponseEXTGW(p_requestVO);
                    p_requestVO.getResponseMap().put("TYPE", "LMSPTENQRES");
                    p_requestVO.getResponseMap().put("POINTS", p_requestVO.getCurrentLoyaltyPoints());
				p_requestVO.getResponseMap().put("PRODUCTCODE",p_requestVO.getProductCode());
                    ExtAPIStringParser.populateResponseMap(p_requestVO, PretupsErrorCodesI.TOTAL_LOYALTY_POINTS_FOR_USER);
                    ExtAPIStringParser.generateExtStringResponse(p_requestVO);
                    p_requestVO.setSenderMessageRequired(false);
                    break;
                }

            case ACTION_CHNL_LMS_POINTS_REDEMPTION:
                {
                    // ExtAPIXMLStringParser.generateLMSPointsRedemptionResponseEXTGW(p_requestVO);
                    p_requestVO.getResponseMap().put("TYPE", "LMSPTREDRES");
                    p_requestVO.getResponseMap().put("REDTXNID", p_requestVO.getRedemptionId());
                    p_requestVO.getResponseMap().put("REMPOINTS", p_requestVO.getCurrentLoyaltyPoints());
                    p_requestVO.getResponseMap().put("CREDITEDAMOUNT", p_requestVO.getCreditedAmount());
				p_requestVO.getResponseMap().put("PRODUCTCODE",p_requestVO.getProductCode());
                    ExtAPIStringParser.populateResponseMap(p_requestVO, PretupsErrorCodesI.TOTAL_LOYALTY_REDEMPTION_AND_AMOUNT);
                    ExtAPIStringParser.generateExtStringResponse(p_requestVO);
                    p_requestVO.setSenderMessageRequired(false);
                    break;
                }
            case GET_MY_MSISDN:
                {
                    ExtAPIXMLStringParser.generateGetMyNumberResponse(p_requestVO, "EXMSISDNRSP");
                    break;
                }

            case GET_CHNL_USR_INFO:
                {
                    ExtAPIXMLStringParser.generateChannelInfoResponse(p_requestVO, "EXUSERINFORSP");
                    break;
                }
            case ACTION_VOUCHER_CONSUMPTION_O2C:
                {
                    ExtAPIXMLStringParser.generateVoucherConsumptionO2CResponse(p_requestVO, "VOMSCONSRES");
                    break;
                }

            case ACTION_CHNL_EXT_ROAM_RECHARGE_REVERSAL:
                {

                    XMLStringParser.generateChnlRoamRechResponse(p_requestVO);
                    break;
                }
				/*
			 * Added new response method specific for Last X Transfer Service Wise
			 */
			case ACTION_EXT_LAST_XTRF_SRVCWISE_ENQ: 	
			{
				XMLStringParser.generateExtLastXTrfSrvcWiseResponse(p_requestVO);
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
			case ACTION_CHNL_CARDGROUP_ENQUIRY_REQUEST:
			{
				ExtAPIStringParser.generateResponseCardGroupEnquiryRequest(p_requestVO);
				break;
			}
			case ACTION_EXTPROMOPVAS_REQUEST:
			{
				XMLStringParser.generatePromoVASTransferResponse(p_requestVO);
                p_requestVO.setSenderMessageRequired(false);
				break;	
        	}
	case ACTION_EXTPROMOINTLTRFREQ_REQUEST:
	{
		XMLStringParser.generateIntlTransferResponse(p_requestVO);
        p_requestVO.setSenderMessageRequired(false);
		break;	
	}
			case ACTION_CHNL_DAILY_STATUS_REPORT:
			{
				ExtAPIXMLStringParser.generateChannelDailyStatusReportResponse(p_requestVO);
				p_requestVO.setSenderMessageRequired(false);
				break;
			}
			case ACTION_EXDATATRFREQ_REQUEST:
            {
                ExtAPIXMLStringParser.generateC2STransferResponse(p_requestVO, "EXDATATRFRESP");
                break;
            }
			case ACTION_EXT_DSR_REQUEST:
			{
        		ExtAPIXMLStringParser.generateDSRResponse(p_requestVO, "EXTDCSRRESP");
                break;
        	}
			case ACTION_EXT_STOCK_BALANCE_REQUEST:
            {
                ExtAPIXMLStringParser.generateStockBalanceResponse(p_requestVO, "EXTSTKBALRESP");
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
			case ACTION_VOUCHER_STATUS_CHANGES: 
            {
            	p_requestVO.getResponseMap().put("TYPE","VOMSSTCHGRES");
            	ExtAPIStringParser.parseExtStringRequest(p_requestVO);
				break;
            }
			case ACTION_VOUCHER_EXPIRY_CHANGES: 
            {
            	p_requestVO.getResponseMap().put("TYPE","VOMSEXPCHGRES");
            	ExtAPIStringParser.generateExtStringResponse(p_requestVO);
				break;
            }
			case ACTION_CHNL_SOS_SETTLEMENT_REQUEST:
			{
				ExtAPIXMLStringParser.generateSOSSettlementResponse(p_requestVO);
				break;
			}
			case ACTION_CHNL_SOS_REQUEST:
			{
				ExtAPIXMLStringParser.generateSOSResponse(p_requestVO);
				break;
			}
			//Added by Anjali for O2C_C2C_Txn_Status enquiry
			case ACTION_C2C_O2C_TXN_STATUS:
			{
				XMLStringParser.generateExtChannelTxnStatusResponse(p_requestVO);
				break;
			}
			case ACTION_TPS_MAX_CALCULATION:
			{
				p_requestVO.getResponseMap().put("TYPE","MAXTPSHOURLYRES");
				Map<String,String> tpsMap = new HashMap<>();
				tpsMap = (Map)p_requestVO.getValueObject();
				p_requestVO.getResponseMap().put("MAXTPS",tpsMap.get("MAX_TPS"));
				p_requestVO.getResponseMap().put("COUNT",tpsMap.get("COUNT"));
				p_requestVO.getResponseMap().put("SUM",tpsMap.get("SUM"));
				ExtAPIStringParser.populateResponseMap(p_requestVO, PretupsErrorCodesI.MAXTPS_SUCCESS);
				ExtAPIStringParser.generateExtStringResponse(p_requestVO);
				break;
			}
			case ACTION_REGISTER_SID: // added by rahul for generating a
                // response
                // for SID Deletion
                {
                    XMLStringParser.generatePvtRechargeRegistrationResponse(p_requestVO);
                    break;
                }
            case ACTION_ENQUIRY_SID_REQ: // added by rahul for generating a
                // response
                // for SID Deletion
                {
                    XMLStringParser.generateEnquirySIDResponse(p_requestVO);
                    break;
                }
            case ACTION_USER_CARDGROUP_ENQUIRY_REQUEST:
            			{
            					ExtAPIStringParser.generateChannelCardGroupEnquiryResponse(p_requestVO);
            					break;
            				}
            			
            case ACTION_SOS_FLAG_UPDATE_REQ:
            {
                ExtAPIXMLStringParser.generateChannelExtSOSFlagResponse(p_requestVO, "SOSFLAGUPDATERESP");
                p_requestVO.setSenderMessageRequired(false);
                break;
            }
            case ACTION_CHNL_VOUCHER_AVAILABILITY_XML:
            {
                ExtAPIXMLStringParser.userAvailableVoucherEnquiryResponse(p_requestVO);
                break;
            }
            case ACTION_CHNL_DVD_XML:
            {
                ExtAPIXMLStringParser.generateExtDVDResponse(p_requestVO);
                break;
            }
			case ACTION_VOMS_O2C:
			{
				ExtAPIXMLStringParser.generateVoucherO2CTransferResponse(p_requestVO);
			}
			case ACTION_C2C_REQ_REC:
            {
                ExtAPIXMLStringParser.generateExtC2CTransferRequestResponse(p_requestVO, ACTION_C2C_REQ_REC);
                p_requestVO.setSenderMessageRequired(false);
                break;
            }
			case ACTION_C2C_APPR:
            {
                ExtAPIXMLStringParser.generateExtC2CTransferRequestResponse(p_requestVO, ACTION_C2C_APPR);
                p_requestVO.setSenderMessageRequired(false);
                break;
            }
            case ACTION_C2C_VOUCHER_APPR:
            {
                ExtAPIXMLStringParser.generateExtC2CVoucherTransferRequestResponse(p_requestVO, ACTION_C2C_VOUCHER_APPR);
                p_requestVO.setSenderMessageRequired(false);
                break;
            }
            case ACTION_C2C_VOMS_TRF:
			{
                ExtAPIXMLStringParser.generateExtC2CVomsTransferRequestResponse(p_requestVO, ACTION_C2C_VOMS_TRF);
                break;
			}
            case ACTION_C2C_VOMS_INI:
			{
                ExtAPIXMLStringParser.generateExtC2CVomsInitiateRequestResponse(p_requestVO, ACTION_C2C_VOMS_INI);
                break;
			}
            case CHANNEL_USER_DETAILS:
			{
                ExtAPIXMLStringParser.generateChannelUserDetailsResponse(p_requestVO, CHANNEL_USER_DETAILS);
                break;
			}
            case C2S_TOTAL_TRANSACTION_COUNT:
			{
                ExtAPIXMLStringParser.generateTotalTnxCountResponse(p_requestVO, C2S_TOTAL_TRANSACTION_COUNT);
                break;
			}
            case C2S_PROD_TXN_DETAILS: {
        		ExtAPIXMLStringParser.generateTxnCountDetailsResponse(p_requestVO,C2S_PROD_TXN_DETAILS);
        		break;
        	}
            case PASSBOOK_VIEW_DETAILS: {

        		ExtAPIXMLStringParser.generatePassbookDetailsViewResponse(p_requestVO);;
        		break;
        	}
            case C2S_N_PROD_TXN_DETAILS: {
        		ExtAPIXMLStringParser.generateTxnCountDetailsResponse(p_requestVO,C2S_N_PROD_TXN_DETAILS);
        		break;
        	}
        	case TOTAL_TRANSACTION_DETAILED_VIEW: {

        		ExtAPIXMLStringParser.generateTotalTnxDetailedResponse(p_requestVO, TOTAL_TRANSACTION_DETAILED_VIEW);
        		break;
        	}
        	case COMMISSION_CALCULATOR:
        	{
        		ExtAPIXMLStringParser.generateCommissionCalculatorResponse(p_requestVO,COMMISSION_CALCULATOR);
        		break;
        	}
        	case ACTION_CRM_USER_AUTH_XML: 
    		{
    			XMLStringParser.generateChannelUserAuthResponse(p_requestVO);
    			break;	
    		}
    		
			case ACTION_LST_LOAN_ENQ:
			{
				
				ExtAPIXMLStringParser.generateLastLoanEnqResponse(p_requestVO);
				break;
			}
			case ACTION_LOAN_OPTIN_REQ:
            {
                ExtAPIXMLStringParser.generateLoanOptInOptOutResponse(p_requestVO);
                break;
            }
			case ACTION_LOAN_OPTOUT_REQ:
            {
                ExtAPIXMLStringParser.generateLoanOptInOptOutResponse(p_requestVO);
                break;
            }
            case ACTION_SELF_CUBAR: 
            {
                
                XMLStringParser.generateSelfChannelUserBarResponse(p_requestVO);
            	break;
            }
            
            case ACTION_SELF_CU_UNBAR: 
            {
            	XMLStringParser.generateSelfChannelUserBarResponse(p_requestVO);
                break;
            }
            
            case ACTION_SELF_PIN_RESET:
            {
                  p_requestVO.getResponseMap().put("TYPE", "SPINRESET");
                ExtAPIStringParser.populateResponseMap(p_requestVO, PretupsErrorCodesI.PIN_RESET_SUCCESSFUL);
                ExtAPIStringParser.generateExtStringResponse(p_requestVO);
                p_requestVO.setSenderMessageRequired(false);
                break;
            }
            case ACTION_LST_N_EVD_TRF:
			{
				
				ExtAPIXMLStringParser.generateExtEVDLastNTxnResponse(p_requestVO);
				break;
			}

    		
    		
            default:
      	     	 if(LOG.isDebugEnabled()){
      	     		LOG.debug("Default Value " ,action);
      	     	 }	 
        }
    }

	@Override
	public void parseChannelRequestMessage(Connection p_con, RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "parseChannelRequestMessage";
        String contentType = p_requestVO.getReqContentType();
        if (LOG.isDebugEnabled()) {
            LOG.debug("parseChannelRequestMessage", "Transfer ID = " + p_requestVO.getRequestID() + " contentType: " + contentType);
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ExtAPIParsers[parseChannelRequestMessage]",
                p_requestVO.getTransactionID(), "", "", "Exception :" + e.getMessage());
            throw new BTSLBaseException("ExtAPIParsers", "parseChannelRequestMessage", PretupsErrorCodesI.ERROR_EXCEPTION,e);
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
        if (LOG.isDebugEnabled()) {
            LOG.debug("parseRequest", "Entered Request ID=" + p_requestVO.getRequestID() + " action=" + action);
        }

        switch (action) {
            case LMB_ONLINE_DEBIT:
                {
                    ExtAPIXMLStringParser.parseLMBDebitRequest(p_requestVO);
                    break;
                }
            default:
      	     	 if(LOG.isDebugEnabled()){
      	     		LOG.debug("Default Value " ,action);
      	     	 }
        }
    }

    /**
     * Method to generate Response of P2P requests
     * 
     * @param p_requestVO
     * @throws Exception
     */
    public void generatePlainResponse(int action, RequestVO p_requestVO) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("generateResponse", "Entered Request ID=" + p_requestVO.getRequestID() + " action=" + action);
        }
        String messageCode = p_requestVO.getMessageCode();
        if ((!BTSLUtil.isNullString(messageCode)) && (!p_requestVO.isSuccessTxn()) && messageCode.indexOf("_") != -1) {
            messageCode = messageCode.substring(0, messageCode.indexOf("_"));
            p_requestVO.setMessageCode(messageCode);
        }
        switch (action) {
            case LMB_ONLINE_DEBIT:
                {
                    ExtAPIXMLStringParser.generateLMBDebitResponse(p_requestVO);
                    break;
                }
            default:
      	     	 if(LOG.isDebugEnabled()){
      	     		LOG.debug("Default Value " ,action);
      	     	 }
        }
    }

    /**
     * Method to parse the Operator request
     * 
     * @param p_requestVO
     * @throws BTSLBaseException
     */
 
	@Override
	public void parseOperatorRequestMessage(RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "parseOperatorRequestMessage";
        String contentType = p_requestVO.getReqContentType();
        if (LOG.isDebugEnabled()) {
            LOG.debug("parseOperatorRequestMessage", "Transfer ID=" + p_requestVO.getRequestID() + " contentType: " + contentType);
        }
        try {
            if (contentType != null && (contentType.indexOf("xml") != -1 || contentType.indexOf("XML") != -1)) {
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ExtAPIParsers[parseOperatorRequestMessage]",
                p_requestVO.getTransactionID(), "", "", "Exception :" + e.getMessage());
            throw new BTSLBaseException("ExtAPIParsers", "parseOperatorRequestMessage", PretupsErrorCodesI.ERROR_EXCEPTION,e);
        }
    }

	 /**
	 * @param con
	 * @param requestVO
	 * @param channelUserVO
	 * @param operatorUtili
	 * @return 
	 * @throws BTSLBaseException
	 */
	private ChannelUserVO validateEXTGWRequestCommon(Connection con, RequestVO requestVO, ChannelUserVO channelUserVO, OperatorUtilI operatorUtili) throws BTSLBaseException {
		 
		 final String methodName = "validateEXTGWRequestCommon";
	     if(LOG.isDebugEnabled()) {
					LOG.debug(methodName, " Entered: ");
				}
		String msisdn = (String) requestVO.getRequestMap().get("MSISDN");
     	String pin = (String) requestVO.getRequestMap().get("PIN");
     	String loginID = (String) requestVO.getRequestMap().get("LOGINID");
     	String password = (String) requestVO.getRequestMap().get("PASSWORD");
        String extCode = (String) requestVO.getRequestMap().get("EXTCODE");
        String language1 = (String) requestVO.getRequestMap().get("LANGUAGE1");
        String language2 = (String) requestVO.getRequestMap().get("LANGUAGE2");

         if (!BTSLUtil.isNullString(msisdn) && !BTSLUtil.isNullString(pin)) {
             channelUserVO = _channelUserDAO.loadChannelUserDetails(con, msisdn);
         } else if (!BTSLUtil.isNullString(loginID) && !BTSLUtil.isNullString(password) ) {
             channelUserVO = _channelUserDAO.loadChnlUserDetailsByLoginID(con, loginID);
         } else if (!BTSLUtil.isNullString(extCode)) {
             channelUserVO = _channelUserDAO.loadChnlUserDetailsByExtCode(con, BTSLUtil.NullToString(extCode).trim());
         }

         if (channelUserVO != null) {
        	if ("SHA".equalsIgnoreCase((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE))))  {
	        	if((!BTSLUtil.isNullString(pin)) && !BTSLUtil.encryptText(pin).equals(BTSLUtil.decryptText(channelUserVO.getUserPhoneVO().getSmsPin()))) {
	        		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_INVALID_PIN);
	        	}
        	}
        	else if ((!BTSLUtil.isNullString(pin)) && !pin.equals(BTSLUtil.decrypt3DesAesText(channelUserVO.getUserPhoneVO().getSmsPin()))) {
                 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_INVALID_PIN);
             }
             if (!BTSLUtil.isNullString(password) && !operatorUtili.validateTransactionPassword(channelUserVO, password)) {
                 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
             }
             if (!BTSLUtil.isNullString(extCode) && !extCode.equalsIgnoreCase(channelUserVO.getExternalCode())) {
                 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE);
             }
             
         } else {
             throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_USER_NOT_EXIST);
         }
         String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
         String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
         if (BTSLUtil.isNullString(language1)) {
             requestVO.setSenderLocale(new Locale(defaultLanguage, defaultCountry));
         } else {
        	 
        	 if (LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(language1)) == null) {
                 throw new BTSLBaseException(this, "validateC2SReverrsalRequest", PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
             }
             requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(language1)));
         }
         
         if (BTSLUtil.isNullString(language2)) {
             requestVO.setReceiverLocale(new Locale(defaultLanguage, defaultCountry));
         } else {
        	 
        	 if (LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(language2)) == null) {
                 throw new BTSLBaseException(this, "validateC2SReverrsalRequest", PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
             }
      
             requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(language2)));
         }
         
         if (!requestVO.getMessageGatewayVO().isUserAuthorizationReqd()) {
             requestVO.setPinValidationRequired(false);
         } else if (BTSLUtil.isNullString(requestVO.getFilteredMSISDN())) 
         {
             requestVO.setPinValidationRequired(false);
         } 
         else if (BTSLUtil.isNullString(requestVO.getRequestMSISDN())) 
         {
             requestVO.setPinValidationRequired(false);
         } 
         else if (BTSLUtil.isNullString(msisdn)) 
         {
             requestVO.setPinValidationRequired(false);
         }
         
         return channelUserVO;
		}
	
	
}
