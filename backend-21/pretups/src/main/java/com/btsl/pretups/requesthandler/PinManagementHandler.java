package com.btsl.pretups.requesthandler;

/**
 * * @(#)PinManagementHandler.java
 * Copyright(c) 2005-2006, Bharti Telesoft Ltd.
 * All Rights Reserved
 * 
 * ----------------------------------------------------------------------------
 * ---------------------
 * Author Date History
 * ----------------------------------------------------------------------------
 * ---------------------
 * Siddhartha Srivastava Dec 5, 2006 Initial Creation
 * 
 * This class parses the request received on the basis of format for the PMREQ.
 * After the request is parsed, all the transaction are performed for the pin
 * management handling
 * which are relevant
 * 
 */

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberDAO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;

public class PinManagementHandler implements ServiceKeywordControllerI {

    private Log log = LogFactory.getLog(PinManagementHandler.class.getName());
    private HashMap _requestMap = null;
    private RequestVO _requestVO = null;
    private String _module = null;
    private String _action = null;

    /**
     * This method is the entry point into the class.This method decides
     * whether the request is for 'ResetPin', 'SendPin'
     * depending on the value of the parameter action in the request.
     * 
     * @param RequestVO
     */

    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (log.isDebugEnabled()) {
            log.debug("process", "Entered....: p_requestVO= " + p_requestVO);
        }
        _requestVO = p_requestVO;
        Connection con = null;MComConnectionI mcomCon = null;
        try {
            // retreiving the Map which has all the values in the request.These
            // values can be retreived by
            // using the tag name
            _requestMap = _requestVO.getRequestMap();
            _action = (String) _requestMap.get("ACTION");
            _module = (String) _requestMap.get("MODULE");

            // this method validates whether the msisdn, module and action have
            // valid values in the request
            validate();

            mcomCon = new MComConnection();con=mcomCon.getConnection();

            // depending on the subType(action) resendPin or sendPin are
            // called.Inside these methods, user details
            // are loaded.Also the steps taken will depend whether the request
            // is from C2S or P2P module(module value)
            if ("SPREQ".equals(_action)) {
                sendPin(con);
            } else if ("RPREQ".equals(_action)) {
                resetPin(con);
            }

            // setting the transaction status to true
            _requestVO.setSuccessTxn(true);

        } catch (BTSLBaseException be) {
            log.error("process", "BTSLBaseException " + be.getMessage());
            _requestVO.setSuccessTxn(false);
            if (be.isKey()) {
                _requestVO.setMessageCode(be.getMessageKey());
                _requestVO.setMessageArguments(be.getArgs());
            } else {
                _requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            }
            try {
                if (con != null) {
                    mcomCon.finalRollback();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            log.errorTrace(METHOD_NAME, be);
        } catch (Exception ex) {
            _requestVO.setSuccessTxn(false);

            // Rollbacking the transaction
            try {
                if (con != null) {
                    mcomCon.finalRollback();
                }
            } catch (Exception ee) {
                log.errorTrace(METHOD_NAME, ee);
            }
            log.error("process", "Exception " + ex.getMessage());
            log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PinManagementHandler[process]", "", "", "", "Exception:" + ex.getMessage());
            _requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        }// end of Exception
        finally {
            _requestVO.setRequestMap(_requestMap);
            p_requestVO = _requestVO;
            // clossing database connection
			if (mcomCon != null) {
				mcomCon.close("PinManagementHandler#process");
				mcomCon = null;
			}
            if (log.isDebugEnabled()) {
                log.debug("process", " Exited ");
            }
        }// end of finally
    }

    /**
     * This method is called to validate the values present in the requestMap of
     * the requestVO
     * The purpose of this method is to validate the values of the 'msisdn' ,
     * 'action' and 'module'
     * 
     * @return
     * @throws BTSLBaseException
     */
    private void validate() throws BTSLBaseException {
        final String METHOD_NAME = "validate";
        if (log.isDebugEnabled()) {
            log.debug("validate", "Entered.....");
        }

        String msisdn = null;
        String msisdnPrefix = null;
        String arr[] = null;
        String filteredMsisdn = null;
        try {
            // getting the module ie whether the request is from C2S or P2P
            // module.the request will be handled accordingly
            if (BTSLUtil.isNullString(_module)) {
                _requestMap.put("RES_ERR_KEY", "MODULE");
                throw new BTSLBaseException("PinManagementHandler", "validate", PretupsErrorCodesI.CCE_XML_ERROR_MISSING_MANDATORY_VALUE);
            }

            // the module value should be either C2S or P2P else throw error
            ArrayList moduleList = LookupsCache.loadLookupDropDown(PretupsI.MODULE_TYPE, true);
            if (BTSLUtil.isNullString(BTSLUtil.getOptionDesc(_module, moduleList).getValue())) {
                _requestMap.put("RES_ERR_KEY", "MODULE");
                throw new BTSLBaseException("PinManagementHandler", "validate", PretupsErrorCodesI.CCE_XML_ERROR_INVALID_MODULE_VALUE);
            }
            // getting the action to decide whether the request is for ResendPin
            // or SendPin
            if (BTSLUtil.isNullString(_action)) {
                _requestMap.put("RES_ERR_KEY", "ACTION");
                throw new BTSLBaseException("PinManagementHandler", "validate", PretupsErrorCodesI.CCE_XML_ERROR_MISSING_MANDATORY_VALUE);
            }
            // the action value should be either 'SPREQ' or 'SPREQ' else throw
            // an error
            if (!("SPREQ".equals(_action) || "RPREQ".equals(_action))) {
                _requestMap.put("RES_ERR_KEY", "ACTION");
                throw new BTSLBaseException("PinManagementHandler", "validate", PretupsErrorCodesI.CCE_XML_ERROR_INVALID_ACTION_VALUE);
            }
            // getting the msisdn of the retailer
            msisdn = (String) _requestMap.get("MSISDN");
            if (BTSLUtil.isNullString(msisdn)) {
                _requestMap.put("RES_ERR_KEY", "MSISDN");
                throw new BTSLBaseException("PinManagementHandler", "validate", PretupsErrorCodesI.CCE_XML_ERROR_MISSING_MANDATORY_VALUE);
            }

            // filtering the msisdn for country independent dial format
            filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdn);
            if (!BTSLUtil.isValidMSISDN(filteredMsisdn)) {
                _requestMap.put("RES_ERR_KEY", "MSISDN");
                throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_ERROR_INVALID_MSISDN);
            }
            msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
            _requestVO.setFilteredMSISDN(filteredMsisdn);
            // checking whether the msisdn prefix is valid in the network
            NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
            if (networkPrefixVO == null) {
                arr = new String[] { filteredMsisdn };
                throw new BTSLBaseException("PinManagementHandler", "validate", PretupsErrorCodesI.CCE_XML_ERROR_UNSUPPORTED_NETWORK, arr);
            }
            String networkCode = networkPrefixVO.getNetworkCode();
            if (networkCode != null && !networkCode.equals(((UserVO) _requestVO.getSenderVO()).getNetworkID())) {
                throw new BTSLBaseException("PinManagementHandler", "validate", PretupsErrorCodesI.CCE_XML_ERROR_NETWORK_NOT_MATCHING_REQUEST);
            }
        } catch (BTSLBaseException be) {
            log.errorTrace(METHOD_NAME, be);
            log.error("validate", "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            log.error("validate", "Exception " + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PinManagementHandler[validate]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("PinManagementHandler", "validate", PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        if (log.isDebugEnabled()) {
            log.debug("validate", "Exiting ");
        }
    }

    /**
     * This method is called when a request is received for sending of the pin.
     * As the original pin is resend
     * to the user, no resetting for the value of invalid pin connt takes place
     * 
     * @param p_con
     */
    private void sendPin(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "sendPin";
        if (log.isDebugEnabled()) {
            log.debug("sendPin", "Entered .....");
        }
        // retreiving the module
        BTSLMessages btslMessage = null;
        String[] arr = null;
        try {
            // sending the pin for the C2S module ie the request is from C2S
            if (_module.equals(PretupsI.C2S_MODULE)) {
                ChannelUserDAO channelUserDAO = new ChannelUserDAO();
                ChannelUserVO channelUserVO = channelUserDAO.loadChannelUserDetails(p_con, _requestVO.getFilteredMSISDN());
                if (channelUserVO == null) {
                    throw new BTSLBaseException("PinManagementHandler", "sendPin", PretupsErrorCodesI.CCE_ERROR_USER_DETAIL_NOT_FOUND);
                }
                // preparing the locale to be used while pushing the message
                Locale locale = new Locale(channelUserVO.getUserPhoneVO().getPhoneLanguage(), channelUserVO.getUserPhoneVO().getCountry());

                arr = new String[1];
                arr[0] = BTSLUtil.decryptText(channelUserVO.getUserPhoneVO().getSmsPin());
                ;
                btslMessage = new BTSLMessages(PretupsErrorCodesI.C2SSUBSCRIBER_SENDPIN_MSG, arr);

                // pushing the message to the user who sent the request
                // PushMessage pushMessage = new
                // PushMessage(channelUserVO.getMsisdn(), btslMessage, null,
                // null, locale, channelUserVO.getNetworkID());
                PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(), btslMessage, null, null, locale, channelUserVO.getNetworkID(), "Related SMS will be delivered shortly");
                pushMessage.push();
            }
            // sending the pin for the P2P module ie the request is from P2P
            // module
            else if (_module.equals(PretupsI.P2P_MODULE)) {
                SubscriberDAO subscriberDAO = new SubscriberDAO();
                ArrayList subscriberList = subscriberDAO.loadSubscriberDetails(p_con, _requestVO.getFilteredMSISDN(), null, null, PretupsI.ALL,null);
                if (subscriberList == null || subscriberList.size() <= 0) {
                    throw new BTSLBaseException("PinManagementHandler", "sendPin", PretupsErrorCodesI.CCE_ERROR_SUBSCRIBER_DETAIL_NOT_FOUND);
                }

                SenderVO senderVO = (SenderVO) subscriberList.get(0);
                // preparing the locale to be used while pushing the message
                Locale locale = new Locale(senderVO.getLanguage(), senderVO.getCountry());

                arr = new String[1];
                arr[0] = senderVO.getPin();
                btslMessage = new BTSLMessages(PretupsErrorCodesI.P2PSUBSCRIBER_SENDPIN_MSG, arr);

                // pushing the message to the user who sent the request
                // PushMessage pushMessage=new PushMessage(senderVO.getMsisdn(),
                // btslMessage, null, null, locale, senderVO.getNetworkCode());
                PushMessage pushMessage = new PushMessage(senderVO.getMsisdn(), btslMessage, null, null, locale, senderVO.getNetworkCode(), "Sms will be delivered shortly");
                pushMessage.push();
            }
        } catch (BTSLBaseException be) {
            log.errorTrace(METHOD_NAME, be);
            log.error("sendPin", "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            log.error("sendPin", "Exception " + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PinManagementHandler[sendPin]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("PinManagementHandler", "sendPin", PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        if (log.isDebugEnabled()) {
            log.debug("sendPin", "Exiting  module=" + _module);
        }
    }

    /**
     * This method is called when any request is received for resending of pin.
     * In this case the default pin
     * is send to the user and the invalid pin count is reset to '0'
     * 
     * @param p_con
     */
    private void resetPin(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "resetPin";
        if (log.isDebugEnabled()) {
            log.debug("resetPin", "Entered .....");
        }
        try {
            BTSLMessages btslMessage = null;
            String[] arr = null;
            int resetCount = 0;
            int updateCount = 0;
            Date currentDate = new Date();
            // the request is from the C2S module
            if (_module.equals(PretupsI.C2S_MODULE)) {
                ChannelUserDAO channelUserDAO = new ChannelUserDAO();
                // this method loads the channel user details on the basis of
                // the filtered msisdn
                ChannelUserVO channelUserVO = channelUserDAO.loadChannelUserDetails(p_con, _requestVO.getFilteredMSISDN());
                if (channelUserVO == null) {
                    throw new BTSLBaseException("PinManagementHandler", "resetPin", PretupsErrorCodesI.CCE_ERROR_USER_DETAIL_NOT_FOUND);
                }

                channelUserVO.setModifiedBy(((UserVO) _requestVO.getSenderVO()).getUserID());
                channelUserVO.setModifiedOn(currentDate);

                // preparing the locale to be used while pushing the message
                Locale locale = new Locale(channelUserVO.getUserPhoneVO().getPhoneLanguage(), channelUserVO.getUserPhoneVO().getCountry());

                UserPhoneVO userPhoneVO = channelUserVO.getUserPhoneVO();
                channelUserDAO = new ChannelUserDAO();
                resetCount = channelUserDAO.changePin(p_con, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_SMSPIN)), channelUserVO);
                if (resetCount > 0) {
                    userPhoneVO.setModifiedBy(((UserVO) _requestVO.getSenderVO()).getUserID());
                    userPhoneVO.setModifiedOn(currentDate);
                    userPhoneVO.setInvalidPinCount(0);
                    updateCount = channelUserDAO.updateSmsPinCounter(p_con, userPhoneVO);
                }
                if (resetCount > 0 && updateCount > 0) {
                    p_con.commit();
                    arr = new String[1];
                    arr[0] = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_SMSPIN));
                    btslMessage = new BTSLMessages(PretupsErrorCodesI.C2SSUBSCRIBER_RESETPIN_MSG, arr);
                } else {
                    throw new BTSLBaseException("PinManagementHandler", "resetPin", PretupsErrorCodesI.REQ_NOT_PROCESS);
                }

                // PushMessage pushMessage = new
                // PushMessage(channelUserVO.getMsisdn(), btslMessage, null,
                // null, locale, channelUserVO.getNetworkID());
                PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(), btslMessage, null, null, locale, channelUserVO.getNetworkID(), "Sms will be delivered Shortly...");
                pushMessage.push();
            }
            // the request is from the P2P module
            else if (_module.equals(PretupsI.P2P_MODULE)) {
                SubscriberDAO subscriberDAO = new SubscriberDAO();
                ArrayList subscriberList = subscriberDAO.loadSubscriberDetails(p_con, _requestVO.getFilteredMSISDN(), null, null, PretupsI.ALL,null);

                if (subscriberList == null || subscriberList.size() <= 0) {
                    throw new BTSLBaseException("PinManagementHandler", "resetPin", PretupsErrorCodesI.CCE_ERROR_SUBSCRIBER_DETAIL_NOT_FOUND);
                }

                SenderVO senderVO = (SenderVO) subscriberList.get(0);

                // preparing the locale to be used while pushing the message
                Locale locale = new Locale(senderVO.getLanguage(), senderVO.getCountry());

                senderVO.setModifiedBy(((UserVO) _requestVO.getSenderVO()).getUserID());
                senderVO.setModifiedOn(currentDate);
                senderVO.setPinModifiedOn(currentDate);
                senderVO.setPinBlockCount(0);
                senderVO.setFirstInvalidPinTime(null);
                senderVO.setStatus(PretupsI.USER_STATUS_ACTIVE);
                senderVO.setPin(BTSLUtil.encryptText((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)));
                updateCount = subscriberDAO.updatePinStatus(p_con, senderVO, true);
                if (updateCount > 0) {
                    resetCount = subscriberDAO.changePin(p_con, senderVO);
                }
                if (resetCount > 0 && updateCount > 0) {
                    p_con.commit();
                    arr = new String[1];
                    arr[0] = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN);
                    btslMessage = new BTSLMessages(PretupsErrorCodesI.P2PSUBSCRIBER_RESETPIN_MSG, arr);
                } else {
                    throw new BTSLBaseException("PinManagementHandler", "resetPin", PretupsErrorCodesI.REQ_NOT_PROCESS);
                }

                // PushMessage pushMessage = new
                // PushMessage(senderVO.getMsisdn(), btslMessage, null, null,
                // locale, senderVO.getNetworkCode());
                PushMessage pushMessage = new PushMessage(senderVO.getMsisdn(), btslMessage, null, null, locale, senderVO.getNetworkCode(), "Sms will be delivered Shortly..");
                pushMessage.push();
            }
        } catch (BTSLBaseException be) {
            log.errorTrace(METHOD_NAME, be);
            log.error("resetPin", "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            log.error("resetPin", "Exception " + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PinManagementHandler[resetPin]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("PinManagementHandler", "resetPin", PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        if (log.isDebugEnabled()) {
            log.debug("resetPin", "exiting with module " + _module);
        }
    }
}
