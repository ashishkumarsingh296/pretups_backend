package com.btsl.pretups.gateway.parsers;

import java.net.URLDecoder;
import java.sql.Connection;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.TypesI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.ParserUtility;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;

/*
 * @(#)SMSParser.java
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
 * This parser is user when plain request is arrived from External system to
 * pretups system
 * Authenticate user on the basis of loginid and PIN of user
 */
public class SMSPOSParsers extends ParserUtility {
    public static final Log _log = LogFactory.getLog(SMSPOSParsers.class.getName());

    public void parseRequestMessage(RequestVO p_requestVO) throws BTSLBaseException {
    }

    public void generateResponseMessage(RequestVO p_requestVO) {
    }

    /**
     * Method to load and validate user details
     * 
     * @param p_con
     * @param p_requestVO
     * @throws BTSLBaseException
     */

    /*
     * public ChannelUserVO loadValidateUserDetails(Connection p_con,RequestVO
     * p_requestVO) throws BTSLBaseException
     * {
     * if(_log.isDebugEnabled())
     * _log.debug("loadValidateUserDetails","Entered Request ID="
     * +p_requestVO.getRequestID());
     * 
     * ChannelUserVO channelUserVO=null;
     * //Load ChannelUser on basis of MSISDN or User Code depending on Action or
     * some standards need to be checked
     * try
     * {
     * if(BTSLUtil.isNullString(p_requestVO.getSenderLoginID()))
     * {
     * throw new
     * BTSLBaseException(this,"loadValidateUserDetails",PretupsErrorCodesI
     * .ERROR_MISSING_SENDER_IDENTIFICATION);
     * }
     * String networkID=p_requestVO.getRequestNetworkCode();
     * 
     * if(!BTSLUtil.isNullString(p_requestVO.getSenderLoginID()))
     * channelUserVO= _channelUserDAO.loadChnlUserDetailsByLoginID(p_con,
     * p_requestVO.getSenderLoginID());
     * if(channelUserVO!=null)
     * {
     * if (!networkID.equalsIgnoreCase(channelUserVO.getNetworkID()))
     * throw new BTSLBaseException(this, "loadValidateUserDetails",
     * PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
     * }else{
     * throw new BTSLBaseException(this, "loadValidateUserDetails",
     * PretupsErrorCodesI.ERROR_USER_NOT_EXIST);
     * }
     * 
     * if(channelUserVO!=null)
     * {
     * channelUserVO.setActiveUserID(channelUserVO.getUserID());
     * channelUserVO.setActiveUserMsisdn(channelUserVO.getMsisdn());
     * channelUserVO.setActiveUserPin(channelUserVO.getSmsPin());
     * 
     * if(PretupsI.STAFF_USER_TYPE.equals(channelUserVO.getUserType()))
     * {
     * channelUserVO.setStaffUser(true);
     * channelUserVO= _channelUserDAO.loadChannelUserDetailsByUserId(p_con,
     * channelUserVO.getParentID());
     * 
     * channelUserVO.setUserID(channelUserVO.getParentID());
     * ChannelUserVO parentChannelUserVO=new
     * UserDAO().loadUserDetailsFormUserID(p_con, channelUserVO.getParentID());
     * 
     * validateUserDetails(p_requestVO,parentChannelUserVO);
     * channelUserVO.setParentID(parentChannelUserVO.getParentID());
     * channelUserVO.setOwnerID(parentChannelUserVO.getOwnerID());
     * channelUserVO.setStatus(parentChannelUserVO.getStatus());
     * channelUserVO.setUserType(parentChannelUserVO.getUserType());
     * channelUserVO.setStaffUser(true);
     * channelUserVO.setMsisdn(parentChannelUserVO.getMsisdn());
     * channelUserVO.setPinRequired(parentChannelUserVO.getPinRequired());
     * channelUserVO.setSmsPin(parentChannelUserVO.getSmsPin());
     * channelUserVO.setParentLoginID(parentChannelUserVO.getLoginID());
     * channelUserVO.setParentStatus(parentChannelUserVO.getStatus());
     * 
     * }
     * }
     * if(BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN()) &&
     * channelUserVO!=null &&
     * !channelUserVO.getLoginID().equals(channelUserVO.getUserPhoneVO
     * ().getMsisdn()))
     * p_requestVO.setFilteredMSISDN(PretupsBL.getFilteredMSISDN(channelUserVO.
     * getUserPhoneVO().getMsisdn()));
     * else
     * p_requestVO.setFilteredMSISDN(PretupsBL.getFilteredMSISDN(channelUserVO.
     * getMsisdn()));
     * 
     * validateUserDetails(p_requestVO,channelUserVO);
     * }
     * catch(BTSLBaseException be)
     * {
     * throw be;
     * }
     * catch(Exception e)
     * {
     * e.printStackTrace();
     * throw new
     * BTSLBaseException(this,"loadValidateUserDetails",PretupsErrorCodesI
     * .ERROR_EXCEPTION);
     * }
     * if(_log.isDebugEnabled())
     * _log.debug("loadValidateUserDetails","Exiting Request ID="
     * +p_requestVO.getRequestID()+" channelUserVO="+channelUserVO);
     * return channelUserVO;
     * }
     */

    public void generateChannelResponseMessage(RequestVO p_requestVO) {
        String message = null;
        // p_requestVO.setSenderMessageRequired(false);
        if (!BTSLUtil.isNullString(p_requestVO.getSenderReturnMessage())) {
            message = p_requestVO.getSenderReturnMessage();
        } else {
            message = BTSLUtil.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
        }

        p_requestVO.setSenderReturnMessage(message);
    }

    /**
     * Method to validate the syntax of message formate
     * In case of rechage message format will be
     * RC <Subscriber MSISDN> <Amount> <PIN> <Sender Language
     * Code>#<SenderLoginId>
     * All fields are mandator, remove <SenderLoginId> from the message
     * and put it in requestVO.
     * Remove <Sender Language Code> from message and put it in requestVO as
     * senderLocale
     * then final message will remain as: RC <Subscriber MSISDN> <Amount> <PIN>
     * In case of any service <Sender Language Code> will be last parameter
     * after PIN
     * But it should be before #<SenderLoginId>
     * 
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public void parseChannelRequestMessage(RequestVO p_requestVO, Connection pCon) throws BTSLBaseException {
        final String METHOD_NAME = "parseChannelRequestMessage";
        if (_log.isDebugEnabled()) {
            _log.debug("parseChannelRequestMessage", "Entered");
        }
        try {
            // p_requestVO.setMessageSentMsisdn(_operatorUtil.getSystemFilteredMSISDN(p_requestVO.getPosMSISDN()));
            String filteredPOSMsisdn = _operatorUtil.getSystemFilteredMSISDN(p_requestVO.getPosMSISDN());
            p_requestVO.setMessageSentMsisdn(filteredPOSMsisdn);
            if (!BTSLUtil.isValidMSISDN(filteredPOSMsisdn)) {
                throw new BTSLBaseException(this, "parseChannelRequestMessage", PretupsErrorCodesI.INVALID_POS_MSISDN);
            }

            String message = URLDecoder.decode(p_requestVO.getRequestMessage(), "UTF16");
            String loginSep = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPT_LOGINID));
            String[] array = message.split(loginSep);
            if (array.length != 2) {
                throw new BTSLBaseException(this, "parseChannelRequestMessage", PretupsErrorCodesI.SMS_LOGINID_NOT_FOUND);
            }
            p_requestVO.setSenderLoginID(array[1]);
            String requestMessage = "";
            String msgSpt = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
            String[] msg = array[0].split(msgSpt);
            if (msg != null && msg.length < 3) {
                throw new BTSLBaseException("SMSPOSParsers", "parseChannelRequestMessage", PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }

            for (int i = 0; i < msg.length; i++) {
                if (i != msg.length - 1) {
                    requestMessage = requestMessage + msgSpt + msg[i];
                } else {
                    p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(msg[i]));
                }
            }
            requestMessage = requestMessage.substring(1, requestMessage.length());
            // p_requestVO.setDecryptedMessage(array[0]);
            p_requestVO.setDecryptedMessage(requestMessage);
            // Set language of SMSPOS on English, message will only be sent in
            // English irespective of language of channel user
            // OR language1 present in request string..........discussed with
            // Dhiraj sir
            // p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails("0"));

            // if(_log.isDebugEnabled())
            // _log.debug("parseChannelRequestMessage","SERVICE CACHE"+ServiceKeywordCache);

            /*
             * ServiceKeywordCacheVO
             * serviceKeywordCacheVO=ServiceKeywordCache.getServiceKeywordObj
             * (p_requestVO);
             * String serviceType = serviceKeywordCacheVO.getServiceType();
             * if("RC".equalsIgnoreCase(serviceType) ||
             * "PPB".equalsIgnoreCase(serviceType))
             * p_requestVO.setSenderLocale(LocaleMasterCache.
             * getLocaleFromCodeDetails("0"));
             */

            ChannelUserBL.updateUserInfo(pCon, p_requestVO);

        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "parseChannelRequestMessage", PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseChannelRequestMessage", "Exiting");
            }
        }
    }

    /**
     * Method to load and Validate the channel User Details (On MSISDN by
     * Default)
     * 
     * @param p_con
     * @param p_requestVO
     * @throws BTSLBaseException
     *             If channel user is sending request then push message to POS
     *             terminal number.
     *             If staff user is sending request then send message to POS
     *             terminal number and psrent channel user too
     */

    public ChannelUserVO loadValidateUserDetails(Connection p_con, RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "loadValidateUserDetails";
        if (_log.isDebugEnabled()) {
            _log.debug("loadValidateUserDetails", "Entered Request ID=" + p_requestVO.getRequestID());
        }
        ChannelUserVO channelUserVO = null;
        ChannelUserVO staffUserVO = null;
        boolean isAllowedTime = false;
        String filteredPOSMsisdn = null;
        try {
            // Load ChannelUser on basis of MSISDN
            channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getFilteredMSISDN());
            validateUserDetails(p_requestVO, channelUserVO);
            /*
             * String utilClass = (String)
             * PreferenceCache.getSystemPreferenceValue
             * (PreferenceI.OPERATOR_UTIL_CLASS);
             * operatorUtil = (OperatorUtilI)
             * Class.forName(utilClass).newInstance();
             */

            filteredPOSMsisdn = _operatorUtil.getSystemFilteredMSISDN(p_requestVO.getPosMSISDN());
            p_requestVO.setMessageSentMsisdn(filteredPOSMsisdn);
            if (!BTSLUtil.isValidMSISDN(filteredPOSMsisdn)) {
                throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.INVALID_POS_MSISDN);
            }

            NetworkPrefixVO prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(filteredPOSMsisdn));
            if (prefixVO == null || !prefixVO.getNetworkCode().equals(channelUserVO.getNetworkID())) {
                throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.ERROR_NETWORK_NOTFOUND);
            }

            if (channelUserVO != null) {
                p_requestVO.setPosUserMSISDN(channelUserVO.getUserPhoneVO().getMsisdn());
                if (!channelUserVO.getUserID().equals(p_requestVO.getActiverUserId())) {
                    p_requestVO.setParentMsisdnPOS(channelUserVO.getUserPhoneVO().getMsisdn());
                    channelUserVO.setStaffUser(true);
                    staffUserVO = _channelUserDAO.loadChannelUserDetailsByUserId(p_con, p_requestVO.getActiverUserId(), channelUserVO.getUserID());
                    validateUserDetails(p_requestVO, staffUserVO);
                    if (staffUserVO != null && !PretupsI.NOT_AVAILABLE.equals(staffUserVO.getUserPhoneVO().getMsisdn())) {
                        p_requestVO.setPosUserMSISDN(staffUserVO.getUserPhoneVO().getMsisdn());
                    }
                    channelUserVO.setActiveUserID(staffUserVO.getUserID());
                    staffUserVO.setActiveUserID(staffUserVO.getUserID());
                    staffUserVO.setStaffUser(true);
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
                // for pushing message in hexadecimal
                /*
                 * if(!channelUserVO.isStaffUser() &&
                 * "ar".equals(channelUserVO.getUserPhoneVO().getPhoneLanguage())
                 * &&
                 * "EG".equals(channelUserVO.getUserPhoneVO().getCountry()))
                 * p_requestVO.setSenderLocale(new Locale("ar1","EG"));
                 * else if(channelUserVO.isStaffUser()&& staffUserVO!=null &&
                 * "ar".equals(staffUserVO.getUserPhoneVO().getPhoneLanguage())
                 * &&
                 * "EG".equals(staffUserVO.getUserPhoneVO().getCountry()))
                 * p_requestVO.setSenderLocale(new Locale("ar1","EG"));
                 */
            }

            if (!p_requestVO.getMessageGatewayVO().isUserAuthorizationReqd()) {
                p_requestVO.setPinValidationRequired(false);
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("loadValidateUserDetails", "Exiting Request ID=" + p_requestVO.getRequestID());
            }
        }
        if (channelUserVO != null && !channelUserVO.isStaffUser()) {
            return channelUserVO;
        } else {
            return staffUserVO;
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
        if (_log.isDebugEnabled()) {
            _log.debug("checkRequestUnderProcess",
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
            _log.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "validateUserDetails", PretupsErrorCodesI.ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("checkRequestUnderProcess", "Exiting For Request ID=" + p_requestVO.getRequestID());
        }

    }

    public void parseChannelRequestMessage(Connection p_con, RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "parseChannelRequestMessage";
        if (_log.isDebugEnabled()) {
            _log.debug("parseChannelRequestMessage", "Entered");
        }
        try {
            // p_requestVO.setMessageSentMsisdn(_operatorUtil.getSystemFilteredMSISDN(p_requestVO.getPosMSISDN()));
            String filteredPOSMsisdn = _operatorUtil.getSystemFilteredMSISDN(p_requestVO.getPosMSISDN());
            p_requestVO.setMessageSentMsisdn(filteredPOSMsisdn);
            if (!BTSLUtil.isValidMSISDN(filteredPOSMsisdn)) {
                throw new BTSLBaseException(this, "parseChannelRequestMessage", PretupsErrorCodesI.INVALID_POS_MSISDN);
            }

            String message = URLDecoder.decode(p_requestVO.getRequestMessage(), "UTF16");
            String loginSep = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPT_LOGINID));
            String[] array = message.split(loginSep);
            if (array.length != 2) {
                throw new BTSLBaseException(this, "parseChannelRequestMessage", PretupsErrorCodesI.SMS_LOGINID_NOT_FOUND);
            }
            p_requestVO.setSenderLoginID(array[1]);
            String requestMessage = "";
            String msgSpt = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
            String[] msg = array[0].split(msgSpt);
            if (msg != null && msg.length < 3) {
                throw new BTSLBaseException("SMSPOSParsers", "parseChannelRequestMessage", PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }

            for (int i = 0; i < msg.length; i++) {
                if (i != msg.length - 1) {
                    requestMessage = requestMessage + msgSpt + msg[i];
                } else {
                    p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(msg[i]));
                }
            }
            requestMessage = requestMessage.substring(1, requestMessage.length());
            p_requestVO.setDecryptedMessage(requestMessage);
            updateUserInfo(p_con, p_requestVO);
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "parseChannelRequestMessage", PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseChannelRequestMessage", "Exiting");
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
        if (_log.isDebugEnabled()) {
            _log.debug("parseOperatorRequestMessage", "Entered");
        }
        try {
            // p_requestVO.setMessageSentMsisdn(_operatorUtil.getSystemFilteredMSISDN(p_requestVO.getPosMSISDN()));
            String filteredPOSMsisdn = _operatorUtil.getSystemFilteredMSISDN(p_requestVO.getPosMSISDN());
            p_requestVO.setMessageSentMsisdn(filteredPOSMsisdn);
            if (!BTSLUtil.isValidMSISDN(filteredPOSMsisdn)) {
                throw new BTSLBaseException(this, "parseOperatorRequestMessage", PretupsErrorCodesI.INVALID_POS_MSISDN);
            }

            String message = URLDecoder.decode(p_requestVO.getRequestMessage(), "UTF16");
            String loginSep = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPT_LOGINID));
            String[] array = message.split(loginSep);
            if (array.length != 2) {
                throw new BTSLBaseException(this, "parseOperatorRequestMessage", PretupsErrorCodesI.SMS_LOGINID_NOT_FOUND);
            }
            p_requestVO.setSenderLoginID(array[1]);
            String requestMessage = "";
            String msgSpt = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
            String[] msg = array[0].split(msgSpt);
            if (msg != null && msg.length < 3) {
                throw new BTSLBaseException("SMSPOSParsers", "parseOperatorRequestMessage", PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
            }

            for (int i = 0; i < msg.length; i++) {
                if (i != msg.length - 1) {
                    requestMessage = requestMessage + msgSpt + msg[i];
                } else {
                    p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(msg[i]));
                }
            }
            requestMessage = requestMessage.substring(1, requestMessage.length());
            p_requestVO.setDecryptedMessage(requestMessage);
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "parseOperatorRequestMessage", PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("parseOperatorRequestMessage", "Exiting");
            }
        }
    }
}