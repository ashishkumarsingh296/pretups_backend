package com.client.pretups.p2p.subscriber.requesthandler;

import java.sql.Connection;
import java.util.Date;
import java.util.Locale;

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
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberDAO;
import com.btsl.pretups.p2p.subscriber.requesthandler.RegisterationController;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLUtil;

public class ChangePinControllerAirtelBD implements ServiceKeywordControllerI {

    private static Log _log = LogFactory.getLog(ChangePinControllerAirtelBD.class.getName());
    private String _requestID = null;
    private static OperatorUtilI _operatorUtil = null;

    // Loads operator specific class
    static {
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChangePinController[initialize]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    public void process(RequestVO p_requestVO) {
        _requestID = p_requestVO.getRequestIDStr();
        if (_log.isDebugEnabled()) {
            _log.debug("process", _requestID, " Entered " + p_requestVO);
        }
        final String methodName = "process";
        Connection con = null;MComConnectionI mcomCon = null;
        try {
            SenderVO senderVO = (SenderVO) p_requestVO.getSenderVO();
            if (senderVO == null) {
                if (_log.isDebugEnabled()) {
                    _log.debug("process", _requestID, " Subscriber not registered, Registering .....");
                }
                new RegisterationController().regsiterNewUser(p_requestVO);
                senderVO = (SenderVO) p_requestVO.getSenderVO();
                senderVO.setDefUserRegistration(true);
            }

            // <Key Word> <OLD_PIN> <NEW_PIN> <CONFIRM_NEW_PIN>

            // String messageArr[] = p_requestVO.getRequestMessageArray();
            final String messageArr[] = _operatorUtil.getP2PChangePinMessageArray(p_requestVO.getRequestMessageArray());

            /**
             * Note: checks
             * 1.) message should be in the mentioned format
             * <KeyWord> <NEW_PIN> <CONFIRM_NEW_PIN> <OLD_PIN>
             * 2.) old pin and previously registered PIN both should be same
             * 3.) old pin and new pin should not same
             * 4.) new pin should be numeric
             * 5.) pin length should be same as defined in the system
             * 6.) new pin and confirm pin should
             * be same
             */

            // if pin Invalid return with error(PIN is Mandatory)
            final String actualPin = senderVO.getPin();
            String newPin = null;
            if (messageArr.length > 4) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.P2P_ERROR_CPIN_INVALIDMESSAGEFORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() },
                    null);
            }

            final int messageLength = messageArr.length;
            mcomCon = new MComConnection();con=mcomCon.getConnection();
            int count = 0;
            if (_log.isDebugEnabled()) {
                _log.debug("process", "Message Length=" + messageLength);
            }
            switch (messageLength) {
            case 1: {
                if (!senderVO.isDefUserRegistration()) {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.P2P_ERROR_CPIN_INVALIDMESSAGEFORMAT, 0, new String[] { p_requestVO
                        .getActualMessageFormat() }, null);
                } else {
                    // newPin=PretupsI.DEFAULT_P2P_PIN;
                    newPin = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN);
                    count = updateUserPIN(con, p_requestVO, actualPin);
                }
                break;
            }
            case 2: {
                // if(PretupsI.DEFAULT_P2P_PIN.equals(BTSLUtil.decryptText(actualPin)))
                if (BTSLUtil.encryptText((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)).equals(actualPin)) {
                    checkNewPinEqualDefPin(messageArr[3]);
                    // BTSLUtil.validatePIN(messageArr[1]);
                    // modify by santanu for doing operator specification
                    _operatorUtil.validatePINRules(messageArr[3]);
                    count = updateUserPIN(con, p_requestVO, BTSLUtil.encryptText(messageArr[3]));
                    newPin = messageArr[3];
                } else {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.P2P_ERROR_CPIN_INVALIDMESSAGEFORMAT, 0, new String[] { p_requestVO
                        .getActualMessageFormat() }, null);
                }
                break;
            }
            case 3: {
                // if(PretupsI.DEFAULT_P2P_PIN.equals(BTSLUtil.decryptText(actualPin)))
                if (BTSLUtil.encryptText((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)).equals(actualPin)) {
                    checkNewPinEqualDefPin(messageArr[3]);
                    checkNewPinEqualConfirmPin(messageArr[3], messageArr[1]);
                    // BTSLUtil.validatePIN(messageArr[1]);
                    _operatorUtil.validatePINRules(messageArr[3]);
                    newPin = messageArr[3];
                    count = updateUserPIN(con, p_requestVO, BTSLUtil.encryptText(messageArr[3]));
                } else {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.P2P_ERROR_CPIN_INVALIDMESSAGEFORMAT, 0, new String[] { p_requestVO
                        .getActualMessageFormat() }, null);
                }
                break;
            }
            case 4: {
                // Setting the Pin Change Force check to false so that it can be
                // bypassed
                senderVO.setForcePinCheckReqd(false);
                // if(_log.isDebugEnabled())
                // _log.debug("process","PretupsI.DEFAULT_P2P_PIN="+PretupsI.DEFAULT_P2P_PIN+" messageArr[1]="+messageArr[1]+" actual PIN="+BTSLUtil.decryptText(actualPin));
                if (_log.isDebugEnabled()) {
                    _log.debug("process", "SystemPreferences.DEFAULT_P2P_PIN=" + (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN) + " messageArr[3]=" + messageArr[3] + " actual PIN=****");
                }

                // if(!PretupsI.DEFAULT_P2P_PIN.equals(BTSLUtil.decryptText(actualPin)))
                if (!BTSLUtil.encryptText((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)).equals(actualPin)) {
                    try {
                        SubscriberBL.validatePIN(con, senderVO, BTSLUtil.encryptText(messageArr[3]));
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey()
                            .equals(PretupsErrorCodesI.ERROR_SNDR_PINBLOCK)))) {
                        	mcomCon.finalCommit();
                        }
                        throw be;
                    }
                }
                checkNewPinEqualOldPin(messageArr[3], messageArr[1]);
                checkNewPinEqualDefPin(messageArr[1]);
                checkNewPinEqualConfirmPin(messageArr[1], messageArr[2]);
                // BTSLUtil.validatePIN(messageArr[2]);
                _operatorUtil.validatePINRules(messageArr[1]);
                newPin = messageArr[1];
                count = updateUserPIN(con, p_requestVO, BTSLUtil.encryptText(messageArr[1]));
                break;
            }
            }
            if (count > 0) {
            	mcomCon.finalCommit();
                final String arr[] = { newPin };
                if (senderVO.isDefUserRegistration()) {
                    // if(PretupsI.DEFAULT_P2P_PIN.equals(newPin))
                    if ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN).equals(newPin)) {
                        // p_requestVO.setMessageArguments(arr);
                        p_requestVO.setMessageCode(PretupsErrorCodesI.PIN_CHANGE_SUCCESS_AND_REG);
                    } else {
                        p_requestVO.setMessageArguments(arr);
                        p_requestVO.setMessageCode(PretupsErrorCodesI.PIN_CHANGE_SUCCESS_AND_REGWITHPIN);
                    }
                } else {
                    if (senderVO.isActivateStatusReqd()) {
                        (new PushMessage(senderVO.getMsisdn(), getSenderRegistrationMessage(newPin, senderVO.getLocale()), "", p_requestVO.getRequestGatewayCode(), senderVO
                            .getLocale())).push();
                    }
                    final String arrmsg[] = { newPin };
                    p_requestVO.setMessageArguments(arrmsg);
                    p_requestVO.setMessageCode(PretupsErrorCodesI.PIN_CHANGE_SUCCESS);
                }
            } else {
            	mcomCon.finalRollback();
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.PIN_CHANGE_FAILED);
            }
        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            }
            try {
                if (con != null) {
                	mcomCon.finalRollback();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            p_requestVO.setMessageArguments(be.getArgs());
            _log.errorTrace(methodName, be);
            _log.error("process", _requestID, "BTSLBaseException " + be.getMessage());
        } catch (Exception e) {
            p_requestVO.setSuccessTxn(false);
            _log.errorTrace(methodName, e);
            try {
                if (con != null) {
                	mcomCon.finalRollback();
                }
            } catch (Exception e1) {
                _log.errorTrace(methodName, e1);
            }

            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            _log.error("process", _requestID, "BTSLBaseException " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChangePinController[process]", "", "", "",
                "Exception:" + e.getMessage());
		} finally {
			if (mcomCon != null) {
				mcomCon.close("ChangePinControllerAirtelBD#process");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug("process", _requestID, " Exited ");
            }
        }
    }

    /**
     * Generate the Sender Auto Registration Success Message
     * 
     * @param p_pin
     * @param p_locale
     * @return String
     */
    private String getSenderRegistrationMessage(String p_pin, Locale p_locale) {
        final String[] messageArgArray = { p_pin };
        return BTSLUtil.getMessage(p_locale, PretupsErrorCodesI.P2P_SENDER_AUTO_REG_SUCCESS, messageArgArray);
    }

    /**
     * Method to check whether the New PIN is same as Default PIN
     * 
     * @param p_pin
     * @throws BTSLBaseException
     */
    private void checkNewPinEqualDefPin(String p_pin) throws BTSLBaseException {
        // if(PretupsI.DEFAULT_P2P_PIN.equals(p_pin))
        if ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN).equals(p_pin)) {
            // String arr[] = { PretupsI.DEFAULT_P2P_PIN};
            final String arr[] = { (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN) };
            throw new BTSLBaseException(this, "process", PretupsErrorCodesI.PIN_SAME_TO_DEFAULT_PIN, 0, arr, null);
        }
    }

    /**
     * Method to check whether the New PIN is same as Confirm PIN
     * 
     * @param p_pin
     * @param p_confirmPin
     * @throws BTSLBaseException
     */
    private void checkNewPinEqualConfirmPin(String p_pin, String p_confirmPin) throws BTSLBaseException {
        if (!p_pin.equals(p_confirmPin)) {
            throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_PIN_NEWCONFIRMNOTSAME);
        }
    }

    /**
     * Method to check whether the New PIN is same as Old PIN
     * 
     * @param p_oldPin
     * @param p_pin
     * @throws BTSLBaseException
     */
    private void checkNewPinEqualOldPin(String p_oldPin, String p_pin) throws BTSLBaseException {
        if (p_pin.equals(p_oldPin)) {
            throw new BTSLBaseException(this, "process", PretupsErrorCodesI.PIN_OLDNEWSAME);
        }
    }

    /**
     * Method to update the User PIN and Status if necessary
     * 
     * @param p_requestVO
     * @param p_pin
     * @return
     * @throws BTSLBaseException
     */
    private int updateUserPIN(Connection p_con, RequestVO p_requestVO, String p_pin) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("updateUserPIN", _requestID, " Entered for MSISDN= " + p_requestVO.getFilteredMSISDN());
        }
        final String METHOD_NAME = "updateUserPIN";
        int count = 0;
        try {
            final SenderVO senderVO = (SenderVO) p_requestVO.getSenderVO();
            final String oldPin = senderVO.getPin();
            final String modifificationType = PretupsI.USER_PIN_MANAGEMENT;
            // if(oldPin.equals(PretupsI.DEFAULT_P2P_PIN) &&
            // senderVO.getStatus().equals(PretupsI.USER_STATUS_NEW))
            if (oldPin.equals(BTSLUtil.encryptText((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) && senderVO.getStatus().equals(PretupsI.USER_STATUS_NEW)) {
                senderVO.setActivateStatusReqd(true);
                senderVO.setStatus(PretupsI.USER_STATUS_ACTIVE);
            }
            senderVO.setPin(p_pin);
            final Date currentDate = new Date();
            senderVO.setModifiedOn(currentDate);
            // Setting the pinModifiedOn to the senderVO Added by Ashish
            // 27-10-06
            senderVO.setPinModifiedOn(currentDate);
            senderVO.setModifiedBy(senderVO.getUserID());
            final SubscriberDAO subscriberDAO = new SubscriberDAO();
            final UserDAO userDAO = new UserDAO();

            // check if pin exist or not in password history table
            final boolean pin_status = userDAO.checkPasswordHistory(p_con, modifificationType, senderVO.getUserID(), senderVO.getMsisdn(), senderVO.getPin());
            if (pin_status) {
                final String lenArr[] = new String[2];
                lenArr[0] = String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PREV_PIN_NOT_ALLOW))).intValue());
                throw new BTSLBaseException(this, "updateUserPIN", PretupsErrorCodesI.P2P_PIN_CHECK_HISTORY_EXIST, 0, lenArr, null);
            }
            count = subscriberDAO.changePin(p_con, senderVO);
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChangePinController[updateUserPIN]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updateUserPIN", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("updateUserPIN", _requestID, " Exited with count=" + count);
            }
        }
        return count;
    }

}