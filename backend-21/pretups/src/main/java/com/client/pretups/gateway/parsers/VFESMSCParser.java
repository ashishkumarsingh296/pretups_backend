package com.client.pretups.gateway.parsers;

/*
 * @(#)VFESMSCParser.java
 * All Rights Reserved
 * ------------------------------------------------------------------------------
 * -------------------
 * Author Date History
 * ------------------------------------------------------------------------------
 * -------------------
 * Manisha Jain Apr 28, 2010 Initital Creation
 * ------------------------------------------------------------------------------
 * -------------------
 * Parser class to handle SMSC requests
 */

import java.sql.Connection;
import java.util.Locale;
import java.util.StringTokenizer;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.ota.services.businesslogic.SimProfileCache;
import com.btsl.ota.services.businesslogic.SimProfileVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.ParserUtility;
import com.btsl.pretups.iccidkeymgmt.businesslogic.PosKeyDAO;
import com.btsl.pretups.iccidkeymgmt.businesslogic.PosKeyVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.stk.Exception348;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.client.pretups.stk.VFECyptoUtil;

public class VFESMSCParser extends ParserUtility {

    public static final Log _log = LogFactory.getLog(VFESMSCParser.class.getName());
    public static PosKeyDAO _posKeyDAO = new PosKeyDAO();
    private Connection con = null;
   private  MComConnectionI mcomCon = null;

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
        	mcomCon = new MComConnection();
        	con=mcomCon.getConnection();
            SimProfileVO simProfileVO = null;
            // check message is plain or encrypted
            isPlainMessageAndAllowed(p_requestVO);
            if (!p_requestVO.isPlainMessage()) {
                // get the encryption key for decoding the message.
                getEncryptionKeyForUser(con, p_requestVO, p_ChanneluserVO);

                String userMessage = p_requestVO.getDecryptedMessage();
                VFECyptoUtil vfeCyptoUtil = new VFECyptoUtil();
                byte[] messageArray = userMessage.getBytes();
                if (_log.isDebugEnabled()) {
                    _log.debug("parseChannelMessage", "length of message array=" + messageArray.length + "message from AircelSMSC==" + userMessage);
                }

                userMessage = vfeCyptoUtil.bytesToBinHex(messageArray);
                if (_log.isDebugEnabled()) {
                    _log.debug("parseChannelMessage", "Transfer ID=" + p_requestVO.getRequestID() + " Hex message==" + userMessage);
                }
                if (userMessage == null || userMessage.length() < 1) {
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VFESMSCParser[parseChannelMessage]", p_requestVO
                        .getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", " Blank binary message from number :" + p_requestVO.getFilteredMSISDN());
                    throw new BTSLBaseException("VFESMSCParser", "parseChannelMessage", PretupsErrorCodesI.CHNL_ERROR_SNDR_BLANK_MESSAGE_DP6);
                }// end if

                UserPhoneVO userPhoneVO = p_ChanneluserVO.getUserPhoneVO();
                // userMessage=removeSpaces(userMessage);
                userMessage = userMessage.substring(2);// remove 01 from
                // message.

                simProfileVO = (SimProfileVO) SimProfileCache.getSimProfileDetails(userPhoneVO.getSimProfileID());
                userMessage = (vfeCyptoUtil.decrypt348Data(userMessage.toLowerCase(), userPhoneVO.getEncryptDecryptKey(), simProfileVO));
                char ch = ' ';
                // This has been done to check whether the Message has be
                // decrypted properly or not
                // only first 3 chars are checked in this case
                for (int i = 0; i < 3; i++) {
                    ch = userMessage.charAt(i);
                    if (!Character.isLetter(ch)) {
                        if (!Character.isDigit(ch)) {
                            if (!Character.isWhitespace(ch)) {
                                _log.error("parseChannelMessage", p_requestVO.getRequestIDStr(), " SMS can not be properly decrypted for Request ID:" + p_requestVO
                                    .getRequestID() + " MSISDN=" + p_requestVO.getFilteredMSISDN());
                                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
                                    "VFESMSCParser[parseChannelMessage]", p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "",
                                    "SMS can not be properly decrypted:" + p_requestVO.getFilteredMSISDN());
                                throw new BTSLBaseException("VFESMSCParser", "parseChannelMessage", PretupsErrorCodesI.CHNL_ERROR_SNDR_MSG_NOT_DECRYPT_DP6);
                            }
                        }
                    }
                }
                if (_log.isDebugEnabled()) {
                    _log.debug("parseChannelMessage", "Transfer ID=" + p_requestVO.getRequestID() + " User message After Decrypting=" + userMessage);
                }

                /*
                 * int indexOfPipe=userMessage.indexOf(124);
                 * userMessage=userMessage.substring(0,indexOfPipe);
                 */
                p_requestVO.setDecryptedMessage(userMessage);
                p_requestVO.setDecryptedMessage(BTSLUtil.NullToString(p_requestVO.getDecryptedMessage().trim()));
            } else {
                p_requestVO.setDecryptedMessage(p_requestVO.getDecryptedMessage());
            }

            p_requestVO.setMessageAlreadyParsed(true);
            p_requestVO.setPlainMessage(true);
            String contentType = p_requestVO.getReqContentType();
            if (_log.isDebugEnabled()) {
                _log.debug("parseChannelMessage", "Transfer ID=" + p_requestVO.getRequestID() + " contentType: " + contentType);
            }
            /*
             * if(contentType!=null&&(contentType.indexOf("wml")!=-1 ||
             * contentType.indexOf("WML")!=-1))
             * {
             * //Forward to WML parsing
             * //and final parsed message will be putted in the requestVO.
             * parseWMLRequest(p_requestVO);
             * }
             */
            if (_log.isDebugEnabled()) {
                _log.debug("parseChannelMessage", "Message =" + p_requestVO.getDecryptedMessage() + " MSISDN=" + p_requestVO.getRequestMSISDN());
            }
        } catch (java.security.GeneralSecurityException ge) {
            _log.errorTrace(METHOD_NAME, ge);
            _log.error("parseChannelMessage",
                "  General security Exception for Request ID:" + p_requestVO.getRequestID() + " MSISDN=" + p_requestVO.getFilteredMSISDN() + " GeneralSecurityException:" + ge
                    .getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VFESMSCParser[parseChannelMessage]", p_requestVO
                .getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", " General security Exception for number:" + p_requestVO.getFilteredMSISDN() + " Exception348=" + ge
                .getMessage());
            throw new BTSLBaseException("VFESMSCParser", "parseChannelMessage", PretupsErrorCodesI.CHNL_ERROR_SNDR_GEN_SCRTY_EXC_DP6);
        } catch (Exception348 e348) {
            _log.errorTrace(METHOD_NAME, e348);
            _log.error("parseChannelMessage",
                " Exception348 for Request ID:" + p_requestVO.getRequestID() + " MSISDN=" + p_requestVO.getFilteredMSISDN() + " Exception348:" + e348.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VFESMSCParser[parseChannelMessage]", p_requestVO
                .getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", "Exception348 for number:" + p_requestVO.getFilteredMSISDN() + " Exception348=" + e348.getMessage());
            throw new BTSLBaseException("VFESMSCParser", "parseChannelMessage", PretupsErrorCodesI.CHNL_ERROR_SNDR_EXC348_EXC_DP6);
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("parseChannelMessage", " BTSL Exception while parsing Request Message :" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("parseChannelMessage", "  Exception while parsing Request Message :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VFESMSCParser[parseChannelMessage]", p_requestVO
                .getTransactionID(), "", "", "Exception :" + e.getMessage());
            throw new BTSLBaseException("VFESMSCParser", "parseChannelMessage", PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
        	if(mcomCon != null){mcomCon.close("VFESMSCParser#parseChannelMessage");mcomCon=null;}
        	con=null;
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

    public static void getEncryptionKeyForUser(Connection p_con, RequestVO p_requestVO, ChannelUserVO p_channelUserVO) throws BTSLBaseException {
        final String METHOD_NAME = "getEncryptionKeyForUser";
        if (_log.isDebugEnabled()) {
            _log.debug("getEncryptionKeyForUser", p_requestVO.getRequestIDStr(),
                "Request ID=" + p_requestVO.getRequestID() + " MSISDN=" + p_requestVO.getFilteredMSISDN() + " Encryption Level=" + p_requestVO.getMessageGatewayVO()
                    .getRequestGatewayVO().getEncryptionLevel());
        }

        try {
            UserPhoneVO userPhoneVO = p_channelUserVO.getUserPhoneVO();
            if (p_requestVO.getMessageGatewayVO().getRequestGatewayVO().getEncryptionLevel().equals(PretupsI.ENCRYPTION_LEVEL_USER_CODE)) {

                // Load the pos key of the msisdn
                PosKeyVO posKeyVO = _posKeyDAO.loadPosKeyByMsisdn(p_con, p_requestVO.getFilteredMSISDN());
                if (posKeyVO == null) {
                    _log.error("getEncryptionKeyForUser", p_requestVO.getRequestIDStr(),
                        " MSISDN=" + p_requestVO.getFilteredMSISDN() + " User Encryption Not found in Database");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[isPlainMessageAndAllowed]",
                        p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", "User Encryption Not found in Database for MSISDN=" + p_requestVO
                            .getFilteredMSISDN());
                    throw new BTSLBaseException("PretupsBL", "getEncryptionKeyForUser", PretupsErrorCodesI.CHNL_ERROR_SNDR_ENCR_KEY_NOTFOUND);
                } else {
                    userPhoneVO.setEncryptDecryptKey(posKeyVO.getKey());
                    userPhoneVO.setSimProfileID(posKeyVO.getSimProfile());
                    userPhoneVO.setRegistered(posKeyVO.isRegistered());
                    p_requestVO.setUDH(null);
                }
            } else {
                userPhoneVO.setEncryptDecryptKey(p_requestVO.getMessageGatewayVO().getRequestGatewayVO().getEncryptionKey());
            }

        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("getEncryptionKeyForUser", "BTSL Base Exception :" + be);
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("getEncryptionKeyForUser", "Exception for Request ID:" + p_requestVO.getRequestID() + " MSISDN=" + p_requestVO.getFilteredMSISDN() + " =" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VFESMSCParser[getEncryptionKeyForUser]", "",
                p_requestVO.getFilteredMSISDN(), "", "Not able to get the encryption for the number: " + p_requestVO.getFilteredMSISDN() + " getting Exception=" + e
                    .getMessage());
            throw new BTSLBaseException("VFESMSCParser", "getEncryptionKeyForUser", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
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
        String message = null;
        if (!BTSLUtil.isNullString(p_requestVO.getSenderReturnMessage())) {
            message = p_requestVO.getSenderReturnMessage();
        } else {
            message = BTSLUtil.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
        }

        p_requestVO.setSenderReturnMessage(message);
    }

    /*	*//**
     * loadValidateUserDetails is used to send the reponse.
     * 
     * @param p_requestVO
     *            RequestVO
     * @param p_con
     *            Connection
     * @return ChannelUserVO
     */
    /*
     * public ChannelUserVO loadValidateUserDetails(Connection p_con,RequestVO
     * p_requestVO) throws BTSLBaseException
     * {
     * if(_log.isDebugEnabled())
     * _log.debug("loadValidateUserDetails","Entered Request ID="
     * +p_requestVO.getRequestID());
     * ChannelUserVO channelUserVO=null;
     * try
     * {
     * //Load ChannelUser on basis of MSISDN
     * channelUserVO= _channelUserDAO.loadChannelUserDetails(p_con,
     * p_requestVO.getFilteredMSISDN());
     * //Validate user details
     * validateUserDetails(p_requestVO,channelUserVO);
     * //parse the channel message.
     * parseChannelMessage(p_requestVO,channelUserVO);
     * 
     * if(!p_requestVO.getMessageGatewayVO().isUserAuthorizationReqd())
     * p_requestVO.setPinValidationRequired(false);
     * }
     * catch(BTSLBaseException be)
     * {
     * throw be;
     * }
     * finally
     * {
     * if(_log.isDebugEnabled())
     * _log.debug("loadValidateUserDetails","Exiting Request ID="
     * +p_requestVO.getRequestID());
     * }
     * return channelUserVO;
     * }
     */

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
            if (channelUserVO != null && !channelUserVO.isStaffUser()) {
                parseChannelMessage(p_requestVO, channelUserVO);
            } else {
                parseChannelMessage(p_requestVO, staffUserVO);
            }

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

    /*	*//**
     * Method to check whether message is Plain Message and whether it is
     * allowed or not
     * 
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    /*
     * public static void isPlainMessageAndAllowed(RequestVO p_requestVO) throws
     * BTSLBaseException
     * {
     * if(_log.isDebugEnabled())
     * _log.debug("isPlainMessageAndAllowed",p_requestVO
     * .getRequestIDStr(),"Entered for Request ID:"
     * +p_requestVO.getRequestID()+" MSISDN="+p_requestVO.getFilteredMSISDN());
     * try
     * {
     * p_requestVO.setPlainMessage(false);
     * }
     * catch(Exception e)
     * {
     * e.printStackTrace();
     * _log.error("isPlainMessageAndAllowed","Exception :"+e);
     * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI
     * .
     * RAISED,EventLevelI.FATAL,"PretupsBL[isPlainMessageAndAllowed]","",p_requestVO
     * .getFilteredMSISDN(),"",
     * "Not able to check the message is plain and allowed for number:"
     * +p_requestVO.getFilteredMSISDN()+" ,getting Exception="+e.getMessage());
     * throw new
     * BTSLBaseException("PretupsBL","isPlainMessageAndAllowed",PretupsErrorCodesI
     * .C2S_ERROR_EXCEPTION);
     * }
     * if(_log.isDebugEnabled())
     * _log.debug("isPlainMessageAndAllowed",p_requestVO
     * .getRequestIDStr(),"Exiting ");
     * }
     */

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
                        "VFESMSCParsers[isPlainMessageAndAllowed]", p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "",
                        "Plain SMS not allowed Gateway =" + p_requestVO.getMessageGatewayVO().getGatewayCode());
                    throw new BTSLBaseException("VFESMSCParsers", "isPlainMessageAndAllowed", PretupsErrorCodesI.CHNL_ERROR_PLAIN_SMS_NOT_ALLOWED_DP6);
                }
            } else {
                p_requestVO.setPlainMessage(false);
                // p_requestVO.setDecryptedMessage(p_requestVO.getRequestMessage());
                if (PretupsI.NO.equals(p_requestVO.getMessageGatewayVO().getBinaryMsgAllowed())) {
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.REQUEST_RESPONSE_INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,
                        "PretupsBL[isPlainMessageAndAllowed]", p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "",
                        "Binary SMS not allowed Gateway =" + p_requestVO.getMessageGatewayVO().getGatewayCode());
                    throw new BTSLBaseException("VFESMSCParsers", "isPlainMessageAndAllowed", PretupsErrorCodesI.CHNL_ERROR_BINARY_SMS_NOT_ALLOWED_DP6);
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

    public String removeSpaces(String s) {
        StringTokenizer st = new StringTokenizer(s, " ", false);
        String t = "";
        while (st.hasMoreElements()) {
            t += st.nextElement();
        }
        return t;
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
