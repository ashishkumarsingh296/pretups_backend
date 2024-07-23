package com.selftopup.pretups.p2p.subscriber.requesthandler;

import java.sql.Connection;
import java.util.Date;
import java.util.Locale;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.gateway.businesslogic.PushMessage;
import com.selftopup.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.selftopup.pretups.p2p.subscriber.businesslogic.SubscriberDAO;
import com.selftopup.pretups.preference.businesslogic.PreferenceCache;
import com.selftopup.pretups.preference.businesslogic.PreferenceI;
import com.selftopup.pretups.preference.businesslogic.SystemPreferences;

import com.selftopup.pretups.receiver.RequestVO;
import com.selftopup.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.selftopup.pretups.subscriber.businesslogic.SenderVO;
import com.selftopup.pretups.util.OperatorUtilI;
import com.selftopup.user.businesslogic.UserDAO;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.OracleUtil;

public class SelfTopUpSubscriberInfo implements ServiceKeywordControllerI {

    private static Log _log = LogFactory.getLog(SelfTopUpSubscriberInfo.class.getName());

    String msisdn = null;
    String pin = null;
    // String dpin=null;
    Connection con = null;
    public static OperatorUtilI _operatorUtil = null;

    static {
        String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SelfTopUpChangePinController[initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    // SubscriberDAO subscriberDAO=null;
    public void process(RequestVO p_requestVO) {
        if (_log.isDebugEnabled())
            _log.debug("process", "Entered");
        try {
            SenderVO senderVO = (SenderVO) p_requestVO.getSenderVO();
            if (senderVO == null)
                throw new BTSLBaseException(this, "validateDetails", SelfTopUpErrorCodesI.SUBSCRIBER_NOT_REGISTERED);
            String[] args = p_requestVO.getRequestMessageArray();
            con = OracleUtil.getConnection();
            pin = args[1];
            msisdn = p_requestVO.getRequestMSISDN();
            int argsLen = args.length;
            int count = 0;
            String newPin = null;
            switch (argsLen) {
            case 2: {
                if (!BTSLUtil.isNullString(pin)) {
                    try {
                        SubscriberBL.validatePIN(con, senderVO, pin);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_SNDR_PINBLOCK))))
                            con.commit();
                        throw be;
                    }
                    p_requestVO.setMessageCode(SelfTopUpErrorCodesI.SUBSCRIBER_ACTIVE_SYSTEM);
                    p_requestVO.setSuccessTxn(true);

                } else {
                    throw new BTSLBaseException(this, "processRequest", SelfTopUpErrorCodesI.PIN_INVALID);
                }
                break;
            }
            case 3: {
                // dpin = args[2];
                if (!BTSLUtil.isNullString(pin)) {
                    try {
                        SubscriberBL.validatePIN(con, senderVO, pin);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_SNDR_PINBLOCK))))
                            con.commit();
                        throw be;
                    }
                    checkNewPinEqualOldPin(args[1], args[2]);
                    checkNewPinEqualDefPin(args[2]);
                    _operatorUtil.validatePINRules(args[2]);
                    newPin = args[2];
                    count = updateUserPIN(con, p_requestVO, args[2]);
                    if (count > 0) {
                        con.commit();
                        String arr[] = { newPin };
                        if (senderVO.isDefUserRegistration()) {
                            // if(PretupsI.DEFAULT_P2P_PIN.equals(newPin))
                            if ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN).equals(newPin))
                                // p_requestVO.setMessageArguments(arr);
                                p_requestVO.setMessageCode(SelfTopUpErrorCodesI.PIN_CHANGE_SUCCESS_AND_REG);
                            else {
                                p_requestVO.setMessageArguments(arr);
                                p_requestVO.setMessageCode(SelfTopUpErrorCodesI.PIN_CHANGE_SUCCESS_AND_REGWITHPIN);
                            }
                        } else {
                            if (senderVO.isActivateStatusReqd()) {
                                (new PushMessage(senderVO.getMsisdn(), getSenderRegistrationMessage(newPin, senderVO.getLocale()), "", p_requestVO.getRequestGatewayCode(), senderVO.getLocale())).push();
                            }
                            String arrmsg[] = { newPin };
                            p_requestVO.setMessageArguments(arrmsg);
                            p_requestVO.setMessageCode(SelfTopUpErrorCodesI.PIN_CHANGE_SUCCESS);
                        }
                    } else {
                        con.rollback();
                        throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.PIN_CHANGE_FAILED);
                    }
                    p_requestVO.setMessageCode(SelfTopUpErrorCodesI.SUBSCRIBER_ACTIVE_SYSTEM);
                    p_requestVO.setSuccessTxn(true);

                } else {
                    throw new BTSLBaseException(this, "processRequest", SelfTopUpErrorCodesI.PIN_INVALID);
                }
                break;
            }
            }
        } catch (BTSLBaseException be) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
            }
            p_requestVO.setSuccessTxn(false);
            _log.error("process", "BTSLBaseException " + be.getMessage());
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else
                p_requestVO.setMessageCode(SelfTopUpErrorCodesI.CARD_DETAILS_INSERTION_FAILED);
        } catch (Exception e) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ee) {
            }
            p_requestVO.setSuccessTxn(false);
            _log.error("process", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberCardDetailsControllerSc[processRequest]", "", "", "", "Getting Exception:" + e.getMessage());
            p_requestVO.setMessageCode(SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }

        finally {

            if (_log.isDebugEnabled())
                _log.debug("process", "Exiting");
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {

            }
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
        if (p_pin.equals(p_oldPin))
            throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.PIN_OLDNEWSAME);
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
            String arr[] = { (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN) };
            throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.PIN_SAME_TO_DEFAULT_PIN, 0, arr, null);
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
        if (_log.isDebugEnabled())
            _log.debug("updateUserPIN", " Entered for MSISDN= " + p_requestVO.getFilteredMSISDN());
        int count = 0;
        try {
            SenderVO senderVO = (SenderVO) p_requestVO.getSenderVO();
            String oldPin = BTSLUtil.decryptText(senderVO.getPin());
            String modifificationType = PretupsI.USER_PIN_MANAGEMENT;
            if (oldPin.equals((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)) && senderVO.getStatus().equals(PretupsI.USER_STATUS_NEW)) {
                senderVO.setActivateStatusReqd(true);
                senderVO.setStatus(PretupsI.USER_STATUS_ACTIVE);
            }
            senderVO.setPin(p_pin);
            Date currentDate = new Date();
            senderVO.setModifiedOn(currentDate);
            senderVO.setPinModifiedOn(currentDate);
            senderVO.setModifiedBy(senderVO.getUserID());
            SubscriberDAO subscriberDAO = new SubscriberDAO();
            UserDAO userDAO = new UserDAO();

            // check if pin exist or not in password history table
            boolean pin_status = userDAO.checkPasswordHistory(p_con, modifificationType, senderVO.getUserID(), senderVO.getMsisdn(), BTSLUtil.encryptText(senderVO.getPin()));
            if (pin_status) {
                String lenArr[] = new String[2];
                lenArr[0] = String.valueOf(SystemPreferences.PREV_PIN_NOT_ALLOW);
                throw new BTSLBaseException(this, "updateUserPIN", SelfTopUpErrorCodesI.P2P_PIN_CHECK_HISTORY_EXIST, 0, lenArr, null);
            }
            count = subscriberDAO.changePin(p_con, senderVO);
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SelfTopUpChangePinController[updateUserPIN]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updateUserPIN", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("updateUserPIN", " Exited with count=" + count);
        }
        return count;
    }

    /**
     * Generate the Sender Auto Registration Success Message
     * 
     * @param p_pin
     * @param p_locale
     * @return String
     */
    private String getSenderRegistrationMessage(String p_pin, Locale p_locale) {
        String[] messageArgArray = { p_pin };
        return BTSLUtil.getMessage(p_locale, SelfTopUpErrorCodesI.P2P_SENDER_AUTO_REG_SUCCESS, messageArgArray);
    }
}
