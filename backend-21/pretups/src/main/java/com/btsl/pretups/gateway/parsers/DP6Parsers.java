package com.btsl.pretups.gateway.parsers;

/*
 * @(#)DP6Parsers.java
 * Copyright(c) 2007, Bharti Telesoft Ltd.
 * All Rights Reserved
 * ------------------------------------------------------------------------------
 * -------------------
 * Author Date History
 * ------------------------------------------------------------------------------
 * -------------------
 * vikas yadav Jan 04, 2007 Initital Creation
 * ------------------------------------------------------------------------------
 * -------------------
 * Parser class to handle SMSC requests
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
import com.btsl.ota.services.businesslogic.SimProfileVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.ParserUtility;
import com.btsl.pretups.gateway.util.WMLStringParser;
import com.btsl.pretups.iccidkeymgmt.businesslogic.PosKeyVO;
import com.btsl.pretups.iccidkeymgmt.businesslogic.SimVenderCache;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.stk.DP6CryptoUtil;
import com.btsl.pretups.stk.Exception348;
import com.btsl.pretups.stk.Message348;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;

public class DP6Parsers extends ParserUtility {
    private static final int VENDOR_CODE_START_POSITION = 12;
    private static final int VENDOR_CODE_END_POSITION = 13;

    public static Log _log = LogFactory.getLog(DP6Parsers.class.getName());

    public void parseRequestMessage(RequestVO p_requestVO) throws BTSLBaseException {
        p_requestVO.setDecryptedMessage(p_requestVO.getRequestMessage());
    }

    public void generateResponseMessage(RequestVO p_requestVO) {
        String message = null;
        if (!BTSLUtil.isNullString(p_requestVO.getSenderReturnMessage())) {
            message = p_requestVO.getSenderReturnMessage();
        } else {
            message = BTSLUtil.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
        }

        p_requestVO.setSenderReturnMessage(message);
    }

    public void parseChannelRequestMessage(RequestVO p_requestVO, Connection pCon) throws BTSLBaseException {
        p_requestVO.setDecryptedMessage(p_requestVO.getRequestMessage());
        ChannelUserBL.updateUserInfo(pCon, p_requestVO);
    }

    /**
     * This is used to parse Channel request Message if message is not a plain
     * message.
     * then decrypt it according to encryption level.
     * 
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public void parseChannelMessage(RequestVO p_requestVO, ChannelUserVO p_ChanneluserVO) throws BTSLBaseException {
        final String METHOD_NAME = "parseChannelMessage";
        try {
            String networkID = p_requestVO.getRequestNetworkCode();
            // check message is plain or encrypted
            isPlainMessageAndAllowed(p_requestVO);
            if (!p_requestVO.isPlainMessage()) {
                String iccid = p_requestVO.getParam1();
                if (BTSLUtil.isNullString(iccid)) {
                    _log.error("parseChannelMessage", p_requestVO.getRequestIDStr(), " MSISDN=" + p_requestVO.getFilteredMSISDN() + " ICCID is not found in the request");
                    throw new BTSLBaseException("DP6Parsers", "parseChannelMessage", PretupsErrorCodesI.CHNL_ERROR_SNDR_ICCID_NOTFOUND);
                }
                p_requestVO.setParam1(iccid.trim());

                String venderCode = iccid.substring(VENDOR_CODE_START_POSITION, VENDOR_CODE_END_POSITION);

                // get the cipher parameters (algo,mode,padding)
                SimProfileVO simProfile = SimVenderCache.getCipherparamObject(venderCode, networkID);

                // get the encryption key for decoding the message.
                getEncryptionKeyForUser(p_requestVO, networkID, simProfile, p_ChanneluserVO, venderCode);

                String userMessage = p_requestVO.getDecryptedMessage();
                // userMessage=userMessage.trim();
                // userMessage=URLDecoder.decode(userMessage,"UTF-8");
                byte[] messageArray = userMessage.getBytes();
                if (_log.isDebugEnabled()) {
                    _log.debug("parseChannelRequestMessage", "length of message array=" + messageArray.length + "message from DP6==" + userMessage);
                }
                DP6CryptoUtil dp6CryptoUtil = new DP6CryptoUtil();
                userMessage = dp6CryptoUtil.bytesToBinHex(messageArray);
                // userMessage=dp6CryptoUtil.binaryToHex(userMessage);

                if (_log.isDebugEnabled()) {
                    _log.debug("parseChannelRequestMessage", "Transfer ID=" + p_requestVO.getRequestID() + " Hex message==" + userMessage);
                }
                if (userMessage == null || userMessage.length() < 1) {
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "DP6Parsers[parseChannelMessage]", p_requestVO
                        .getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", " Blank binary message from number :" + p_requestVO.getFilteredMSISDN());
                    throw new BTSLBaseException("DP6Parsers", "parseChannelMessage", PretupsErrorCodesI.CHNL_ERROR_SNDR_BLANK_MESSAGE_DP6);
                }// end if

                UserPhoneVO userPhoneVO = p_ChanneluserVO.getUserPhoneVO();

                // origanally decrypt the data .
                // if (userMessage.startsWith("01"))
                // {
                userMessage = userMessage.substring(2);// remove 01 from
                // message.
                // }
                int messageLength = userMessage.length();
                if (!(messageLength == 48 || messageLength == 32 || messageLength == 64)) {
                    int padLength = 64 - messageLength;
                    for (int i = 0; i < padLength; i++) {
                        userMessage = userMessage + "0";

                    }
                }
                /*
                 * else
                 * {
                 * 
                 * }
                 */

                userMessage = (dp6CryptoUtil.decrypt348Data(userMessage.toLowerCase(), userPhoneVO.getEncryptDecryptKey(), simProfile));

                char ch = ' ';
                // This has been done to check whether the Message has be
                // decrypted properly or not
                // only first 3 chars are checked in this case
                for (int i = 0; i < 3; i++) {
                    ch = userMessage.charAt(i);
                    if (!Character.isLetter(ch)) {
                        if (!Character.isDigit(ch)) {
                            if (!Character.isWhitespace(ch)) {
                                _log.error("parseChannelRequestMessage", p_requestVO.getRequestIDStr(), " SMS can not be properly decrypted for Request ID:" + p_requestVO
                                    .getRequestID() + " MSISDN=" + p_requestVO.getFilteredMSISDN());
                                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
                                    "DP6Parsers[parseChannelRequestMessage]", p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "",
                                    "SMS can not be properly decrypted:" + p_requestVO.getFilteredMSISDN());
                                throw new BTSLBaseException("DP6Parsers", "parseChannelRequestMessage", PretupsErrorCodesI.CHNL_ERROR_SNDR_MSG_NOT_DECRYPT_DP6);
                            }
                        }
                    }
                }
                if (_log.isDebugEnabled()) {
                    _log.debug("parseChannelRequestMessage", "Transfer ID=" + p_requestVO.getRequestID() + " User message After Decrypting=" + userMessage);
                }

                int indexOfPipe = userMessage.indexOf(124);
                userMessage = userMessage.substring(0, indexOfPipe);
                p_requestVO.setDecryptedMessage(userMessage);
                p_requestVO.setDecryptedMessage(BTSLUtil.NullToString(p_requestVO.getDecryptedMessage().trim()));
            } else {
                p_requestVO.setDecryptedMessage(p_requestVO.getDecryptedMessage());
            }

            p_requestVO.setMessageAlreadyParsed(true);
            p_requestVO.setPlainMessage(true);
            String contentType = p_requestVO.getReqContentType();
            if (_log.isDebugEnabled()) {
                _log.debug("parseChannelRequestMessage", "Transfer ID=" + p_requestVO.getRequestID() + " contentType: " + contentType);
            }
            if (contentType != null && (contentType.indexOf("wml") != -1 || contentType.indexOf("WML") != -1)) {
                // Forward to WML parsing
                // and final parsed message will be putted in the requestVO.
                parseWMLRequest(p_requestVO);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("parseChannelRequestMessage", "Message =" + p_requestVO.getDecryptedMessage() + " MSISDN=" + p_requestVO.getRequestMSISDN());
            }
        } catch (java.security.GeneralSecurityException ge) {
            _log.errorTrace(METHOD_NAME, ge);
            _log.error("parseChannelRequestMessage", "  General security Exception for Request ID:" + p_requestVO.getRequestID() + " MSISDN=" + p_requestVO
                .getFilteredMSISDN() + " GeneralSecurityException:" + ge.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "DP6Parsers[parseChannelRequestMessage]", p_requestVO
                .getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", " General security Exception for number:" + p_requestVO.getFilteredMSISDN() + " Exception348=" + ge
                .getMessage());
            throw new BTSLBaseException("DP6Parsers", "parseChannelRequestMessage", PretupsErrorCodesI.CHNL_ERROR_SNDR_GEN_SCRTY_EXC_DP6);
        } catch (Exception348 e348) {
            _log.errorTrace(METHOD_NAME, e348);
            _log.error("parseChannelRequestMessage",
                " Exception348 for Request ID:" + p_requestVO.getRequestID() + " MSISDN=" + p_requestVO.getFilteredMSISDN() + " Exception348:" + e348.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "DP6Parsers[parseChannelRequestMessage]", p_requestVO
                .getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", "Exception348 for number:" + p_requestVO.getFilteredMSISDN() + " Exception348=" + e348.getMessage());
            throw new BTSLBaseException("DP6Parsers", "parseChannelRequestMessage", PretupsErrorCodesI.CHNL_ERROR_SNDR_EXC348_EXC_DP6);
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("parseChannelRequestMessage", " BTSL Exception while parsing Request Message :" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("parseChannelRequestMessage", "  Exception while parsing Request Message :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DP6Parsers[parseChannelRequestMessage]", p_requestVO
                .getTransactionID(), "", "", "Exception :" + e.getMessage());
            throw new BTSLBaseException("DP6Parsers", "parseChannelRequestMessage", PretupsErrorCodesI.ERROR_EXCEPTION);
        }
    }

    // get the encryption key if encryption level is master then make it by
    // iccid and master key
    // and if it is user take it from pos keys.
    /**
     * This method is used fo getting the encryption key.
     * 
     * @param p_requestVO
     * @param p_networkId
     * @param p_simProfileVO
     * @param p_channelUserVO
     * @param p_vendorCode
     * @throws BTSLBaseException
     */

    public static void getEncryptionKeyForUser(RequestVO p_requestVO, String p_networkId, SimProfileVO p_simProfileVO, ChannelUserVO p_channelUserVO, String p_vendorCode) throws BTSLBaseException {
        final String METHOD_NAME = "getEncryptionKeyForUser";
        if (_log.isDebugEnabled()) {
            _log.debug("getEncryptionKeyForUser", p_requestVO.getRequestIDStr(),
                "Request ID=" + p_requestVO.getRequestID() + " MSISDN=" + p_requestVO.getFilteredMSISDN() + " Encryption Level=" + p_requestVO.getMessageGatewayVO()
                    .getRequestGatewayVO().getEncryptionLevel() + " p_vendorCode=" + p_vendorCode);
        }
        try {
            UserPhoneVO userPhoneVO = p_channelUserVO.getUserPhoneVO();

            if (_log.isDebugEnabled()) {
                _log.debug("getEncryptionKeyForUser", p_requestVO.getRequestIDStr(),
                    "p_requestVO.getUDH().getBytes()=" + p_requestVO.getUDH().getBytes() + " Message348.bytesToBinHex(p_requestVO.getUDH().getBytes())=" + Message348
                        .bytesToBinHex(p_requestVO.getUDH().getBytes()));
            }
            if (p_requestVO.getMessageGatewayVO().getRequestGatewayVO().getEncryptionLevel().equals(PretupsI.ENCRYPTION_LEVEL_MASTER_KEY)) {
                // get the master key on the basis of vender code ,network id
                // and sim profile id.
                PosKeyVO posKeyVO = SimVenderCache.getkeyObject(p_vendorCode, p_networkId, p_simProfileVO.getSimID());

                if (posKeyVO == null) {
                    _log.error("getEncryptionKeyForUser", p_requestVO.getRequestIDStr(),
                        " MSISDN=" + p_requestVO.getFilteredMSISDN() + " User Encryption Not found in Database");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "DP6Parsers[getEncryptionKeyForUser]",
                        p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", "User Encryption Not found in Database for venderCode=" + p_vendorCode);
                    throw new BTSLBaseException("DP6Parsers", "getEncryptionKeyForUser", PretupsErrorCodesI.CHNL_ERROR_SNDR_ENCR_KEY_NOTFOUND_DP6);
                }

                String masterKey = posKeyVO.getKey();// this logic will be
                // changed according to
                // operater in formation
                // first Encrypt ICCID with master key
                DP6CryptoUtil dp6CryptoUtil = new DP6CryptoUtil();
                String intermediateKey = dp6CryptoUtil.encryptData(p_requestVO.getParam1(), masterKey, p_simProfileVO);
                if (_log.isDebugEnabled()) {
                    _log.debug("getEncryptionKeyForUser", p_requestVO.getRequestIDStr(), "Request ID=" + p_requestVO.getRequestID() + " intermediate key" + intermediateKey);
                }

                userPhoneVO.setEncryptDecryptKey(intermediateKey);
                userPhoneVO.setSimProfileID(posKeyVO.getSimProfile());
                userPhoneVO.setRegistered(posKeyVO.isRegistered());
                p_requestVO.setUDH(null);

            } else // If UDH is not 27000 then it is the response of the ICC ID
                   // Key getting
            {
                throw new BTSLBaseException("DP6Parsers", "getEncryptionKeyForUser", PretupsErrorCodesI.CHNL_ERROR_SNDR_WRONG_UDH_DP6);
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("getEncryptionKeyForUser", "BTSL Base Exception :" + be);
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("getEncryptionKeyForUser", "Exception for Request ID:" + p_requestVO.getRequestID() + " MSISDN=" + p_requestVO.getFilteredMSISDN() + " =" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DP6Parsers[getEncryptionKeyForUser]", "", p_requestVO
                .getFilteredMSISDN(), "", "Not able to get the encryption for the number: " + p_requestVO.getFilteredMSISDN() + " getting Exception=" + e.getMessage());
            throw new BTSLBaseException("DP6Parsers", "getEncryptionKeyForUser", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("getEncryptionKeyForUser", p_requestVO.getRequestIDStr(), "Exiting");
        }
    }

    /**
     * generateChannelResponseMessage is used to send the reponse.
     * 
     * @param p_requestVO
     */

    public void generateChannelResponseMessage(RequestVO p_requestVO) {
        final String METHOD_NAME = "generateChannelResponseMessage";
        String contentType = p_requestVO.getMessageGatewayVO().getRequestGatewayVO().getContentType();
        if (_log.isDebugEnabled()) {
            _log.debug("generateChannelResponseMessage", "Transfer ID=" + p_requestVO.getRequestID() + " contentType: " + contentType);
        }
        try {
            if (contentType != null && (p_requestVO.getMessageGatewayVO().getRequestGatewayVO().getContentType().indexOf("WML") != -1 || p_requestVO.getMessageGatewayVO()
                .getRequestGatewayVO().getContentType().indexOf("wml") != -1)) {
                // Set the Sender Return Message
                WMLStringParser.generateChannelResponse(p_requestVO);
            } else {
                String message = null;
                if (!BTSLUtil.isNullString(p_requestVO.getSenderReturnMessage())) {
                    message = p_requestVO.getSenderReturnMessage();
                } else {
                    message = BTSLUtil.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
                }

                p_requestVO.setSenderReturnMessage(message);
            }
            p_requestVO.setReqContentType("xml");// for sending the underprocess
            // message so that extra
            // condition check is avoided
            // in receiver.
        } catch (Exception e) {
            _log.error("generateChannelResponseMessage", "  Exception while generating Response Message :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DP6Parsers[generateChannelResponseMessage]",
                p_requestVO.getTransactionID(), "", "", "Exception getting message :" + e.getMessage());
            try {
                WMLStringParser.generateFailureResponse(p_requestVO);
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DP6Parsers[generateChannelResponseMessage]",
                    p_requestVO.getTransactionID(), "", "", "Exception getting default message :" + ex.getMessage());
                p_requestVO
                    .setSenderReturnMessage("<?xml version=\"1.0\"encoding=\"ISO-8859-1\" ?><wml><card id=\"Main\"><p>" + PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT + "</p></card></wml>");
            }
        }
    }

    /**
     * loadValidateUserDetails is used to send the reponse.
     * 
     * @param p_requestVO
     *            RequestVO
     * @param p_con
     *            Connection
     * @return ChannelUserVO
     */
    public ChannelUserVO loadValidateUserDetails(Connection p_con, RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "loadValidateUserDetails";
        if (_log.isDebugEnabled()) {
            _log.debug("loadValidateUserDetails", "Entered Request ID=" + p_requestVO.getRequestID());
        }
        ChannelUserVO channelUserVO = null;
        try {
            ChannelUserVO staffUserVO = null;
            // Load ChannelUser on basis of MSISDN
            channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getFilteredMSISDN());
            // Validate user details
            validateUserDetails(p_requestVO, channelUserVO);
            if (channelUserVO != null) {
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

            // parse the channel message.
            parseChannelMessage(p_requestVO, channelUserVO);

            if (!p_requestVO.getMessageGatewayVO().isUserAuthorizationReqd()) {
                p_requestVO.setPinValidationRequired(false);
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            throw be;
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("loadValidateUserDetails", "Exiting Request ID=" + p_requestVO.getRequestID());
            }
        }
        return channelUserVO;
    }

    /**
     * Method might be used in future so that WML request can be parsed
     * accordingly
     * 
     * @param p_requestVO
     * @throws Exception
     */
    public void parseWMLRequest(RequestVO p_requestVO) throws Exception {

    }

    /**
     * Method to check whether message is Plain Message and whether it is
     * allowed or not
     * 
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public static void isPlainMessageAndAllowed(RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "isPlainMessageAndAllowed";
        if (_log.isDebugEnabled()) {
            _log.debug("isPlainMessageAndAllowed", p_requestVO.getRequestIDStr(), "Entered for Request ID:" + p_requestVO.getRequestID() + " MSISDN=" + p_requestVO
                .getFilteredMSISDN());
        }
        try {
            if (BTSLUtil.isNullString(p_requestVO.getUDH())) {
                p_requestVO.setPlainMessage(true);
                // If plain SMS is not allowed in channel
                if (PretupsI.NO.equals(p_requestVO.getMessageGatewayVO().getPlainMsgAllowed())) {
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.REQUEST_RESPONSE_INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,
                        "DP6Parsers[isPlainMessageAndAllowed]", p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "",
                        "Plain SMS not allowed Gateway =" + p_requestVO.getMessageGatewayVO().getGatewayCode());
                    throw new BTSLBaseException("DP6Parsers", "isPlainMessageAndAllowed", PretupsErrorCodesI.CHNL_ERROR_PLAIN_SMS_NOT_ALLOWED_DP6);
                }
            } else {
                p_requestVO.setPlainMessage(false);
                // p_requestVO.setDecryptedMessage(p_requestVO.getRequestMessage());
                if (PretupsI.NO.equals(p_requestVO.getMessageGatewayVO().getBinaryMsgAllowed())) {
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.REQUEST_RESPONSE_INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,
                        "PretupsBL[isPlainMessageAndAllowed]", p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "",
                        "Binary SMS not allowed Gateway =" + p_requestVO.getMessageGatewayVO().getGatewayCode());
                    throw new BTSLBaseException("DP6Parsers", "isPlainMessageAndAllowed", PretupsErrorCodesI.CHNL_ERROR_BINARY_SMS_NOT_ALLOWED_DP6);
                }
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("isPlainMessageAndAllowed", p_requestVO.getRequestIDStr(), "BTSL Base Exception :" + be);
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("isPlainMessageAndAllowed", "Exception :" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[isPlainMessageAndAllowed]", "", p_requestVO
                .getFilteredMSISDN(), "", "Not able to check the message is plain and allowed for number:" + p_requestVO.getFilteredMSISDN() + " ,getting Exception=" + e
                .getMessage());
            throw new BTSLBaseException("PretupsBL", "isPlainMessageAndAllowed", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("isPlainMessageAndAllowed", p_requestVO.getRequestIDStr(), "Exiting ");
        }
    }

    public void parseChannelRequestMessage(Connection p_con, RequestVO p_requestVO) throws BTSLBaseException {
        p_requestVO.setDecryptedMessage(p_requestVO.getRequestMessage());
        updateUserInfo(p_con, p_requestVO);
    }

    /**
     * Method to parse the Operator request
     * 
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public void parseOperatorRequestMessage(RequestVO p_requestVO) throws BTSLBaseException {
        p_requestVO.setDecryptedMessage(p_requestVO.getRequestMessage());
        if (!BTSLUtil.isNullString(p_requestVO.getRequestMSISDN())) {
            String filteredMsisdn = PretupsBL.getFilteredMSISDN(p_requestVO.getRequestMSISDN());
            p_requestVO.setFilteredMSISDN(filteredMsisdn);
            p_requestVO.setMessageSentMsisdn(filteredMsisdn);
        }
    }
}