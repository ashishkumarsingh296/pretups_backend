/**
 * @(#)GuineaBissauUtil.java
 *                           Copyright(c) 2007, Bharti Telesoft Ltd.
 *                           All Rights Reserved
 * 
 *                           <description>
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Author Date History
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Sanjeew Kumar March 27, 2009 Initital Creation
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 * 
 */

package com.client.pretups.util.clientutils;

import java.sql.Connection;
import java.util.Calendar;
import java.util.HashMap;
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
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberDAO;
import com.btsl.pretups.payment.businesslogic.PaymentMethodCache;
import com.btsl.pretups.payment.businesslogic.PaymentMethodKeywordVO;
import com.btsl.pretups.payment.businesslogic.ServicePaymentMappingCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class GuineaBissauUtil extends OperatorUtil {
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * Method validatePassword.
     * 
     * @author sanjeew.kumar
     * @created on 05/11/09
     * @param p_loginID
     *            String
     * @param p_password
     *            String
     * @return String
     */
    public HashMap validatePassword(String p_loginID, String p_password) {
        _log.debug("validatePassword", "Entered, p_userID= ", new String(p_loginID + ", Password= " + p_password));
        final HashMap messageMap = new HashMap();
        String defaultPin = BTSLUtil.getDefaultPasswordNumeric(p_password);
        if (defaultPin.equals(p_password)) {
            return messageMap;
        }
        defaultPin = BTSLUtil.getDefaultPasswordText(p_password);
        if (defaultPin.equals(p_password)) {
            return messageMap;
        }
        if (p_password.length() < ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_LOGIN_PWD_LENGTH))).intValue() || p_password.length() > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_LOGIN_PWD_LENGTH))).intValue()) {
            final String[] args = { String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_LOGIN_PWD_LENGTH))).intValue()), String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_LOGIN_PWD_LENGTH))).intValue()) };
            messageMap.put("operatorutil.validatepassword.error.passwordlenerr", args);
        }
        final int result = BTSLUtil.isSMSPinValid(p_password);// for consecutive
        // and
        // same characters
        if (result == -1) {
            messageMap.put("operatorutil.validatepassword.error.passwordsamedigit", null);
        } else if (result == 1) {
            messageMap.put("operatorutil.validatepassword.error.passwordconsecutive", null);
        }
        if (!BTSLUtil.containsChar(p_password)) {
            messageMap.put("operatorutil.validatepassword.error.passwordnotcontainschar", null);
        }
        // for special character
        final String specialChar = Constants.getProperty("SPECIAL_CHARACTER_PASSWORD_VALIDATION");
        if (!BTSLUtil.isNullString(specialChar)) {
            final String[] specialCharArray = { specialChar };
            final String[] passwordCharArray = specialChar.split(",");
            boolean specialCharFlag = false;
            for (int i = 0, j = passwordCharArray.length; i < j; i++) {
                if (p_password.contains(passwordCharArray[i])) {
                    specialCharFlag = true;
                    break;
                }
            }
            if (!specialCharFlag) {
                messageMap.put("operatorutil.validatepassword.error.passwordspecialchar", specialCharArray);
            }
        }
        // for number
        final String[] passwordNumberStrArray = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
        boolean numberStrFlag = false;
        for (int i = 0, j = passwordNumberStrArray.length; i < j; i++) {
            if (p_password.contains(passwordNumberStrArray[i])) {
                numberStrFlag = true;
                break;
            }
        }
        if (!numberStrFlag) {
            messageMap.put("operatorutil.validatepassword.error.passwordnumberchar", null);
        }
        if (p_loginID.equals(p_password)) {
            messageMap.put("operatorutil.validatepassword.error.sameusernamepassword", null);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("validatePassword", "Exiting ");
        }
        return messageMap;
    }

    /**
     * Method to validate the PIN that is sent by user and that stored in
     * database
     * 
     * @param p_con
     * @param p_channelUserVO
     * @param p_requestPin
     * @throws BTSLBaseException
     */
    public void validatePIN(Connection p_con, ChannelUserVO p_channelUserVO, String p_requestPin) throws BTSLBaseException {
        final String methodName = "validatePIN";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered with p_userPhoneVO:@@@@@1" + p_channelUserVO.toString() + " p_requestPin=" + p_requestPin);
        }
        int updateStatus = 0;
        boolean increaseInvalidPinCount = false;
        boolean isUserBarred = false;
        final int mintInDay = 24 * 60;
        try {
            UserPhoneVO userPhoneVO = new UserPhoneVO();
            userPhoneVO = p_channelUserVO.getUserPhoneVO();
            // Force the user to change PIN if he has not changed the same in
            // the defined no of days
            if (_log.isDebugEnabled()) {
                _log.debug(
                    methodName,
                    "Modified Time=:" + userPhoneVO.getModifiedOn() + " userPhoneVO.getPinModifiedOn()=" + userPhoneVO.getPinModifiedOn() + "userPhoneVO.getCreatedOn()" + userPhoneVO
                        .getCreatedOn());
            }

            // added for OCI changes regarding to change PIN on 1st request
            if (userPhoneVO.isForcePinCheckReqd() && (userPhoneVO.getPinModifiedOn().getTime()) == (userPhoneVO.getCreatedOn().getTime())) {
                throw new BTSLBaseException("OperatorUtil", methodName, PretupsErrorCodesI.CHNL_FIRST_REQUEST_PIN_CHANGE);
            }

            final int daysAfterChngPn = ((Integer) PreferenceCache.getControlPreference(PreferenceI.C2S_DAYS_AFTER_CHANGE_PIN, p_channelUserVO.getNetworkID(), p_channelUserVO
                .getCategoryCode())).intValue();
            if (userPhoneVO.isForcePinCheckReqd() && userPhoneVO.getPinModifiedOn() != null && ((userPhoneVO.getModifiedOn().getTime() - userPhoneVO.getPinModifiedOn()
                .getTime()) / (24 * 60 * 60 * 1000)) > daysAfterChngPn) {
                _log.info(methodName, "@@@@FORCEPIN");

                // Force the user to change PIN if he has not changed the same
                // in the defined no of days
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName,
                        "Modified Time=:" + userPhoneVO.getModifiedOn() + " userPhoneVO.getPinModifiedOn()=" + userPhoneVO.getPinModifiedOn() + " Difference=" + ((userPhoneVO
                            .getModifiedOn().getTime() - userPhoneVO.getPinModifiedOn().getTime()) / (24 * 60 * 60 * 1000)));
                }
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "OperatorUtil[validatePIN]", "", userPhoneVO
                    .getMsisdn(), "", "Force User to change PIN after " + daysAfterChngPn + " days as last changed on " + userPhoneVO.getPinModifiedOn());
                final String strArr[] = { String.valueOf(daysAfterChngPn) };
                throw new BTSLBaseException("OperatorUtil", methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_FORCE_CHANGEPIN, 0, strArr, null);
            } else if (p_channelUserVO.getPinReset().equals("Y") && !PretupsI.CHANGE_PIN_SERVICE_TYPE.contains(p_channelUserVO.getServiceTypes())) {
                _log.info(methodName, "@@@@INSIDEELSE");

                if (_log.isDebugEnabled()) {
                    _log.debug(methodName,
                        "Modified Time=" + userPhoneVO.getModifiedOn() + ", Pin Modified On=" + userPhoneVO.getPinModifiedOn() + ", Difference=" + ((userPhoneVO
                            .getModifiedOn().getTime() - userPhoneVO.getPinModifiedOn().getTime()) / (24 * 60 * 60 * 1000)));
                }
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "OperatorUtil[validatePIN]", "", userPhoneVO
                    .getMsisdn(), "", "Force User to change new reset PIN.");
                throw new BTSLBaseException("OperatorUtil", methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_FORCE_CHANGE_RESETPIN, 0, null);
            } else {
                final String decryptedPin = BTSLUtil.decryptText(userPhoneVO.getSmsPin());
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "Sender MSISDN:" + userPhoneVO.getMsisdn() + " decrypted PIN of database=" + decryptedPin + " p_requestPin =" + p_requestPin);
                }

                // added for Moldova Change the default PIN
                if (userPhoneVO.isForcePinCheckReqd() && ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_SMSPIN)).equals(decryptedPin)) {
                    // throw new BTSLBaseException("OciUtil", "validatePIN",
                    // PretupsErrorCodesI.CHNLUSR_CHANGE_DEFAULT_PIN);
                    throw new BTSLBaseException("OperatorUtil", methodName, PretupsErrorCodesI.CHNLUSR_CHANGE_DEFAULT_PIN);
                }
                /*
                 * change done by ashishT
                 * comparing pin password hashvalue set in userphoneVo to the
                 * pin sent by user.
                 */
                // if (!decryptedPin.equals(p_requestPin))
                _log.info(methodName, "@@@@check befire SHA");
                boolean checkpin;
                if ("SHA".equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE))) {
                    if (p_requestPin.length() > SystemPreferences.C2S_PIN_MAX_LENGTH) {
                        checkpin = decryptedPin.equals(p_requestPin);
                    } else {
                        checkpin = (!PretupsI.FALSE.equalsIgnoreCase(BTSLUtil.compareHash2String(decryptedPin, p_requestPin)));
                    }
                    _log.info(methodName, "@@@@check PIN Inside SHA");
                } else {
                    checkpin = decryptedPin.equals(p_requestPin);

                }
                _log.debug(methodName, "@@@@check PIN" + checkpin + decryptedPin);
                if (!checkpin) {
                    increaseInvalidPinCount = true;
                    if (userPhoneVO.getFirstInvalidPinTime() != null) {
                        // Check if PIN counters needs to be reset after the
                        // reset duration
                        final long pnBlckRstDuration = ((Long) PreferenceCache.getControlPreference(PreferenceI.C2S_PIN_BLK_RST_DURATION, p_channelUserVO.getNetworkID(),
                            p_channelUserVO.getCategoryCode())).longValue();
                        if (_log.isDebugEnabled()) {
                            _log.debug(
                                methodName,
                                "p_userPhoneVO.getModifiedOn().getTime()=" + userPhoneVO.getModifiedOn().getTime() + " p_userPhoneVO.getFirstInvalidPinTime().getTime()=" + userPhoneVO
                                    .getFirstInvalidPinTime().getTime() + " Diff=" + ((userPhoneVO.getModifiedOn().getTime() - userPhoneVO.getFirstInvalidPinTime().getTime()) / (60 * 1000)) + " Allowed=" + pnBlckRstDuration);
                        }
                        final Calendar cal = Calendar.getInstance();
                        cal.setTime(userPhoneVO.getModifiedOn());
                        final int d1 = cal.get(Calendar.DAY_OF_YEAR);
                        cal.setTime(userPhoneVO.getFirstInvalidPinTime());
                        final int d2 = cal.get(Calendar.DAY_OF_YEAR);
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodName, "Day Of year of Modified On=" + d1 + " Day Of year of FirstInvalidPinTime=" + d2);
                        }
                        if (d1 != d2 && pnBlckRstDuration <= mintInDay) {
                            // reset
                            userPhoneVO.setInvalidPinCount(1);
                            userPhoneVO.setFirstInvalidPinTime(userPhoneVO.getModifiedOn());
                        } else if (d1 != d2 && pnBlckRstDuration > mintInDay && (d1 - d2) >= (pnBlckRstDuration / mintInDay)) {
                            // Reset
                            userPhoneVO.setInvalidPinCount(1);
                            userPhoneVO.setFirstInvalidPinTime(userPhoneVO.getModifiedOn());
                        } else if (((userPhoneVO.getModifiedOn().getTime() - userPhoneVO.getFirstInvalidPinTime().getTime()) / (60 * 1000)) < pnBlckRstDuration) {
                            final int maxPinBlckCnt = ((Integer) PreferenceCache.getControlPreference(PreferenceI.C2S_MAX_PIN_BLOCK_COUNT_CODE,
                                p_channelUserVO.getNetworkID(), p_channelUserVO.getCategoryCode())).intValue();
                            if (userPhoneVO.getInvalidPinCount() - maxPinBlckCnt == 0) {
                                // Set The flag that indicates that we need to
                                // bar the user because of PIN Change
                                userPhoneVO.setInvalidPinCount(0);
                                userPhoneVO.setFirstInvalidPinTime(null);
                                userPhoneVO.setBarUserForInvalidPin(true);
                                isUserBarred = true;
                            } else {
                                userPhoneVO.setInvalidPinCount(userPhoneVO.getInvalidPinCount() + 1);
                            }

                            if (userPhoneVO.getInvalidPinCount() == 0) {
                                userPhoneVO.setFirstInvalidPinTime(userPhoneVO.getModifiedOn());
                            }
                        } else {
                            userPhoneVO.setInvalidPinCount(1);
                            userPhoneVO.setFirstInvalidPinTime(userPhoneVO.getModifiedOn());
                        }
                    } else {
                        userPhoneVO.setInvalidPinCount(1);
                        userPhoneVO.setFirstInvalidPinTime(userPhoneVO.getModifiedOn());
                    }
                } else {
                    // initilize PIN Counters if ifPinCount>0
                    if (userPhoneVO.getInvalidPinCount() > 0) {
                        userPhoneVO.setInvalidPinCount(0);
                        userPhoneVO.setFirstInvalidPinTime(null);
                        updateStatus = new ChannelUserDAO().updateSmsPinCounter(p_con, userPhoneVO);
                        if (updateStatus < 0) {
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorUtil[validatePIN]", "",
                                userPhoneVO.getMsisdn(), "", "Not able to update invalid PIN count for users");
                            throw new BTSLBaseException("OperatorUtil", methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
                        }
                    }
                }
                if (increaseInvalidPinCount) {
                    updateStatus = new ChannelUserDAO().updateSmsPinCounter(p_con, userPhoneVO);
                    if (updateStatus > 0 && !isUserBarred) {
                        throw new BTSLBaseException("OperatorUtil", methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN);
                    } else if (updateStatus > 0 && isUserBarred) {
                        throw new BTSLBaseException("OperatorUtil", methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK);
                    } else {
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorUtil[validatePIN]", "",
                            userPhoneVO.getMsisdn(), "", "Not able to update invalid PIN count for users");
                        throw new BTSLBaseException("OperatorUtil", methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
                    }
                }
            }

        } catch (BTSLBaseException bex) {
            throw bex;
        } catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorUtil[validatePIN]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("OperatorUtil", methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting with increase invalid Pin Count flag=" + increaseInvalidPinCount);
            }
        }
    }

    /**
     * P2P
     * This method validates the requested PIN with that available in DB, also
     * checks whether to block user or reset the counter or not
     * 
     * @param p_con
     * @param p_senderVO
     * @param p_requestPin
     * @throws BTSLBaseException
     * @author ved.sharma
     */
    public void validatePIN(Connection p_con, SenderVO p_senderVO, String p_requestPin) throws BTSLBaseException {
        final String methodName = "validatePIN";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered with p_senderVO:2222" + p_senderVO.toString() + " p_requestPin=" + p_requestPin);
        }
        int updateStatus = 0;
        boolean updatePinCount = false;
        boolean isUserBarred = false;
        try {
            // added changes regarding to change PIN on 1st request
            // if(p_senderVO.isForcePinCheckReqd() &&
            // (p_senderVO.getPinModifiedOn()==null ||
            // (p_senderVO.getPinModifiedOn().getTime())==(p_senderVO.getCreatedOn().getTime())))
            // throw new BTSLBaseException("OperatorUtil", "validatePIN",
            // PretupsErrorCodesI.CHNL_FIRST_REQUEST_PIN_CHANGE);

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Modified Time=:" + p_senderVO.getModifiedOn() + " p_senderVO.getPinModifiedOn()=" + p_senderVO.getPinModifiedOn());
            }
            if (p_senderVO.isForcePinCheckReqd() && p_senderVO.getPinModifiedOn() != null && ((p_senderVO.getModifiedOn().getTime() - p_senderVO.getPinModifiedOn().getTime()) / (24 * 60 * 60 * 1000)) > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DAYS_AFTER_CHANGE_PIN))).intValue()) {
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName,
                        "Modified Time=:" + p_senderVO.getModifiedOn() + " p_senderVO.getPinModifiedOn()=" + p_senderVO.getPinModifiedOn() + " Difference=" + ((p_senderVO
                            .getModifiedOn().getTime() - p_senderVO.getPinModifiedOn().getTime()) / (24 * 60 * 60 * 1000)));
                }
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "SubscriberBL[validatePIN]", "", p_senderVO
                    .getMsisdn(), "", "Force User to change PIN after " + ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DAYS_AFTER_CHANGE_PIN))).intValue() + " days as last changed on " + p_senderVO
                    .getPinModifiedOn());
                final String strArr[] = { String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DAYS_AFTER_CHANGE_PIN))).intValue()) };
                throw new BTSLBaseException("OperatorUtil", methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_FORCE_CHANGEPIN, 0, strArr, null);
            } else {
                final SubscriberDAO subscriberDAO = new SubscriberDAO();
                final String decryptedPin = BTSLUtil.decryptText(p_senderVO.getPin());

                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "Sender MSISDN:" + p_senderVO.getMsisdn() + " decrypted PIN=" + decryptedPin + " p_requestPin=" + p_requestPin);
                }

                // added for Change the default PIN
                /*
                 * if(p_senderVO.isForcePinCheckReqd() &&
                 * (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN).equals(decryptedPin))
                 * throw new BTSLBaseException("OperatorUtil", "validatePIN",
                 * PretupsErrorCodesI.CHNLUSR_CHANGE_DEFAULT_PIN);
                 */

                /*
                 * change done by ashishT
                 * comparing pin password hashvalue from db to the pin sent by
                 * user.
                 */
                // if (!decryptedPin.equalsIgnoreCase(p_requestPin))
                _log.info(methodName, "@@@@check befire SHA");
                boolean checkpin;
                if ("SHA".equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE))) {
                    if (p_requestPin.length() > SystemPreferences.C2S_PIN_MAX_LENGTH) {
                        checkpin = decryptedPin.equals(p_requestPin);
                    } else {
                        checkpin = (!PretupsI.FALSE.equalsIgnoreCase(BTSLUtil.compareHash2String(decryptedPin, p_requestPin)));
                    }
                    _log.info(methodName, "@@@@check PIN Inside SHA");
                } else {
                    checkpin = decryptedPin.equals(p_requestPin);

                }
                _log.debug(methodName, "@@@@check PIN" + checkpin + decryptedPin);
                if (!checkpin) {
                    updatePinCount = true;
                    final int mintInDay = 24 * 60;
                    if (p_senderVO.getFirstInvalidPinTime() != null) {
                        // Check if PIN counters needs to be reset after the
                        // reset duration
                        if (_log.isDebugEnabled()) {
                            _log.debug(
                                methodName,
                                "p_senderVO.getModifiedOn().getTime()=" + p_senderVO.getModifiedOn().getTime() + " p_senderVO.getFirstInvalidPinTime().getTime()=" + p_senderVO
                                    .getFirstInvalidPinTime().getTime() + " Diff=" + ((p_senderVO.getModifiedOn().getTime() - p_senderVO.getFirstInvalidPinTime().getTime()) / (60 * 1000)) + " Allowed=" + ((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_PIN_BLK_RST_DURATION))).longValue());
                        }
                        final Calendar cal = Calendar.getInstance();
                        cal.setTime(p_senderVO.getModifiedOn());
                        final int d1 = cal.get(Calendar.DAY_OF_YEAR);
                        cal.setTime(p_senderVO.getFirstInvalidPinTime());
                        final int d2 = cal.get(Calendar.DAY_OF_YEAR);
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodName, "Day Of year of Modified On=" + d1 + " Day Of year of FirstInvalidPinTime=" + d2);
                        }
                        if (d1 != d2 && ((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_PIN_BLK_RST_DURATION))).longValue() <= mintInDay) {
                            // reset
                            p_senderVO.setPinBlockCount(1);
                            p_senderVO.setFirstInvalidPinTime(p_senderVO.getModifiedOn());
                        } else if (d1 != d2 && ((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_PIN_BLK_RST_DURATION))).longValue() >= mintInDay && (d1 - d2) >= (((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_PIN_BLK_RST_DURATION))).longValue() / mintInDay)) {
                            // Reset
                            p_senderVO.setPinBlockCount(1);
                            p_senderVO.setFirstInvalidPinTime(p_senderVO.getModifiedOn());
                        } else if (((p_senderVO.getModifiedOn().getTime() - p_senderVO.getFirstInvalidPinTime().getTime()) / (60 * 1000)) < ((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_PIN_BLK_RST_DURATION))).longValue()) {
                            if (p_senderVO.getPinBlockCount() - ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_MAX_PIN_BLOCK_COUNT))).intValue() == 0) {
                                // isStatusUpdate = true;
                                // p_senderVO.setStatus(PretupsI.USER_STATUS_BLOCK);
                                // Set The flag that indicates that we need to
                                // bar the user because of PIN Change
                                p_senderVO.setPinBlockCount(0);
                                // p_senderVO.setFirstInvalidPinTime(null);
                                isUserBarred = true;
                            } else {
                                p_senderVO.setPinBlockCount(p_senderVO.getPinBlockCount() + 1);
                            }

                            if (p_senderVO.getPinBlockCount() == 0) {
                                p_senderVO.setFirstInvalidPinTime(p_senderVO.getModifiedOn());
                            }
                        } else {
                            p_senderVO.setPinBlockCount(1);
                            p_senderVO.setFirstInvalidPinTime(p_senderVO.getModifiedOn());
                        }
                    } else {
                        p_senderVO.setPinBlockCount(1);
                        p_senderVO.setFirstInvalidPinTime(p_senderVO.getModifiedOn());
                    }
                } else {
                    // initilize PIN Counters if ifPinCount>0
                    if (p_senderVO.getPinBlockCount() > 0) {
                        p_senderVO.setPinBlockCount(0);
                        p_senderVO.setFirstInvalidPinTime(null);
                        updateStatus = subscriberDAO.updatePinStatus(p_con, p_senderVO, false);
                        if (updateStatus < 0) {
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorUtil[validatePIN]", "",
                                p_senderVO.getMsisdn(), "", "Not able to update invalid PIN count for users");
                            throw new BTSLBaseException("OperatorUtil", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
                        }
                    }
                }
                if (updatePinCount) {
                    updateStatus = subscriberDAO.updatePinStatus(p_con, p_senderVO, false);
                    if (updateStatus > 0 && !isUserBarred) {
                        throw new BTSLBaseException("OperatorUtil", methodName, PretupsErrorCodesI.ERROR_INVALID_PIN);
                    } else if (updateStatus > 0 && isUserBarred) {
                        p_senderVO.setBarUserForInvalidPin(true);
                        throw new BTSLBaseException("OperatorUtil", methodName, PretupsErrorCodesI.ERROR_SNDR_PINBLOCK, 0, new String[] { String
                            .valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_MAX_PIN_BLOCK_COUNT))).intValue()), String.valueOf(((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_PIN_BLK_RST_DURATION))).longValue()) }, null);
                    } else if (updateStatus < 0) {
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorUtil[validatePIN]", "", p_senderVO
                            .getMsisdn(), "", "Not able to update invalid PIN count for users");
                        throw new BTSLBaseException("OperatorUtil", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
                    }
                }
            }
        } catch (BTSLBaseException bex) {
            throw bex;
        } catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorUtil[validatePIN]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("OperatorUtil", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting with increase Pin Count flag=" + updatePinCount + " Barred Update Flag:" + isUserBarred);
            }
        }
    }

    public void validateCardGroupDetails(String p_startRange, String p_endRange, String p_subService) throws Exception {
    }

    /**
     * Method to validate the request for Non Buddy transfers
     * 
     * @param p_con
     * @param p_senderVO
     * @throws BTSLBaseException
     * @throws Exception
     */
    private void validateIfNotBuddy(Connection p_con, RequestVO p_requestVO, TransferVO p_transferVO) throws BTSLBaseException, Exception {
        final String[] requestMessageArray = p_requestVO.getRequestMessageArray();
        final String METHOD_NAME = "validateIfNotBuddy";
        if (_log.isDebugEnabled()) {
            _log.debug("validateIfNotBuddy", " requestMessageArray length:" + requestMessageArray);
        }
        if (requestMessageArray.length < 3 || requestMessageArray.length > 7) {
            throw new BTSLBaseException(this, "validateIfNotBuddy", PretupsErrorCodesI.P2P_INVALID_MESSAGEFORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() },
                null);
        }
        final String serviceKeyword = requestMessageArray[0];
        final String senderSubscriberType = ((SenderVO) p_transferVO.getSenderVO()).getSubscriberType();
        final StringBuffer incomingSmsStr = new StringBuffer(serviceKeyword + " ");
        final int messageLength = requestMessageArray.length;
        final SenderVO senderVO = (SenderVO) p_transferVO.getSenderVO();
        // if pin Invalid return with error(PIN is Mandatory)
        final String actualPin = BTSLUtil.decryptText(senderVO.getPin());
        if (_log.isDebugEnabled()) {
            _log.debug("validateIfNotBuddy", " actualPin:" + actualPin);
        }

        String paymentMethodType = null;
        String pin = null;
        String paymentMethodKeyword = null;
        switch (messageLength) {
            case 3:
                {
                    // whether PIN validation is required or not.
                    if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) {
                        if (!actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                            throw new BTSLBaseException(this, "validateIfNotBuddy", PretupsErrorCodesI.P2P_INVALID_MESSAGEFORMAT, 0, new String[] { p_requestVO
                                .getActualMessageFormat() }, null);
                        }
                    }
                    paymentMethodType = ServicePaymentMappingCache.getDefaultPaymentMethod(p_transferVO.getServiceType(), senderSubscriberType);
                    if (paymentMethodType == null) {
                        // return with error message, no default payment method
                        // defined
                        throw new BTSLBaseException(this, "validateIfNotBuddy", PretupsErrorCodesI.ERROR_NOTFOUND_DEFAULTPAYMENTMETHOD);
                    }
                    p_transferVO.setPaymentMethodType(paymentMethodType);
                    incomingSmsStr.append(paymentMethodType + " ");
                    checkAfterPaymentMethod(p_con, 1, requestMessageArray, incomingSmsStr, p_transferVO);
                    break;
                }
            case 4:
                {
                    // Validate 2nd Argument for PIN.
                    pin = requestMessageArray[3];

                    incomingSmsStr.append("****" + " ");
                    // if(actualPin.equalsIgnoreCase(PretupsI.DEFAULT_P2P_PIN))
                    // whether PIN validation is required or not.
                    if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) {
                        if (actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                            if (!BTSLUtil.isNullString(requestMessageArray[3])) {
                                if (BTSLUtil.isNumeric(requestMessageArray[3]) && requestMessageArray[3].length() == 1) {
                                    p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(requestMessageArray[3]));
                                    if (p_requestVO.getReceiverLocale() == null) {
                                        p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                                    }
                                } else {
                                    if (!BTSLUtil.isNullString(pin) && !pin.equals((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                                        BTSLUtil.validatePIN(pin);
                                        senderVO.setPin(BTSLUtil.encryptText(pin));
                                        senderVO.setPinUpdateReqd(true);
                                        senderVO.setActivateStatusReqd(true);
                                    }

                                }

                            }
                        } else {
                            try {
                                SubscriberBL.validatePIN(p_con, senderVO, pin);
                            } catch (BTSLBaseException be) {
                                if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey()
                                    .equals(PretupsErrorCodesI.ERROR_SNDR_PINBLOCK)))) {
                                    p_con.commit();
                                }
                                throw be;
                            }
                        }
                    }

                    paymentMethodType = ServicePaymentMappingCache.getDefaultPaymentMethod(p_transferVO.getServiceType(), senderSubscriberType);
                    if (paymentMethodType == null) {
                        // return with error message, no default payment method
                        // defined
                        throw new BTSLBaseException(this, "validateIfNotBuddy", PretupsErrorCodesI.ERROR_NOTFOUND_DEFAULTPAYMENTMETHOD);
                    }
                    p_transferVO.setPaymentMethodType(paymentMethodType);
                    incomingSmsStr.append(paymentMethodType + " ");
                    checkAfterPaymentMethod(p_con, 1, requestMessageArray, incomingSmsStr, p_transferVO);
                    break;
                }
            case 5:
                {

                    // Validate 2nd Argument for PIN.
                    pin = requestMessageArray[4];
                    incomingSmsStr.append("****" + " ");
                    // if pin Invalid return with error(PIN is Mandatory)
                    // if(actualPin.equalsIgnoreCase(PretupsI.DEFAULT_P2P_PIN))
                    // whether PIN validation is required or not.
                    if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) {
                        if (actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                            // if(!pin.equals(PretupsI.DEFAULT_P2P_PIN))
                            if (!BTSLUtil.isNullString(pin) && !pin.equals((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                                BTSLUtil.validatePIN(pin);
                                senderVO.setPin(BTSLUtil.encryptText(pin));
                                senderVO.setPinUpdateReqd(true);
                                senderVO.setActivateStatusReqd(true);
                            }
                        } else {
                            try {
                                SubscriberBL.validatePIN(p_con, senderVO, pin);
                            } catch (BTSLBaseException be) {
                                if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey()
                                    .equals(PretupsErrorCodesI.ERROR_SNDR_PINBLOCK)))) {
                                    p_con.commit();
                                }
                                throw be;
                            }
                        }
                    }

                    // if PIN valid
                    // Validate next Argument for Payment Method.

                    // killed by sanjay as payemnt method table does not exists
                    PaymentMethodKeywordVO paymentMethodKeywordVO = null;
                    paymentMethodKeyword = requestMessageArray[1];
                    // if paymentMethod invalid , Validate next Argument for
                    // Receiver
                    // No(MSISDN).
                    paymentMethodKeywordVO = PaymentMethodCache.getObject(paymentMethodKeyword, p_transferVO.getServiceType(), p_transferVO.getNetworkCode());

                    if (paymentMethodKeywordVO == null) {
                        paymentMethodType = ServicePaymentMappingCache.getDefaultPaymentMethod(p_transferVO.getServiceType(), senderSubscriberType);
                        if (paymentMethodType == null) {
                            // return with error message, no default payment
                            // method
                            // defined
                            throw new BTSLBaseException(this, "validateIfNotBuddy", PretupsErrorCodesI.ERROR_NOTFOUND_DEFAULTPAYMENTMETHOD);
                        }
                        p_transferVO.setPaymentMethodType(paymentMethodType);
                        p_transferVO.setDefaultPaymentMethod("Y");
                        incomingSmsStr.append(paymentMethodType + " ");
                        checkAfterPaymentMethod(p_con, 1, requestMessageArray, incomingSmsStr, p_transferVO);
                        try {
                            if (!BTSLUtil.isNullString(requestMessageArray[3])) {
                                final int selectorValue = Integer.parseInt(requestMessageArray[3]);
                                p_requestVO.setReqSelector("" + selectorValue);
                            }
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
                            throw new BTSLBaseException(this, "validateIfNotBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                        }
                    } else {
                        paymentMethodType = paymentMethodKeywordVO.getPaymentMethodType();
                        p_transferVO.setPaymentMethodType(paymentMethodType);
                        p_transferVO.setPaymentMethodKeywordVO(paymentMethodKeywordVO);
                        p_transferVO.setDefaultPaymentMethod(PretupsI.NO);
                        incomingSmsStr.append(paymentMethodType + " ");
                        checkAfterPaymentMethod(p_con, 2, requestMessageArray, incomingSmsStr, p_transferVO);
                        // _requestVO.setReqSelector(""+SystemPreferences.P2P_TRANSFER_DEF_SELECTOR_CODE);
                    }

                    break;
                }
            case 6:
                {
                    // Validate 2nd Argument for PIN.
                    pin = requestMessageArray[5];
                    incomingSmsStr.append("****" + " ");
                    // if pin Invalid return with error(PIN is Mandatory)
                    // if(actualPin.equalsIgnoreCase(PretupsI.DEFAULT_P2P_PIN))
                    // whether PIN validation is required or not.
                    if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) {
                        if (actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                            // if(!pin.equals(PretupsI.DEFAULT_P2P_PIN))
                            if (!BTSLUtil.isNullString(pin) && !pin.equals((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                                BTSLUtil.validatePIN(pin);
                                senderVO.setPin(BTSLUtil.encryptText(pin));
                                senderVO.setPinUpdateReqd(true);
                                senderVO.setActivateStatusReqd(true);
                            }
                        } else {
                            try {
                                SubscriberBL.validatePIN(p_con, senderVO, pin);
                            } catch (BTSLBaseException be) {
                                if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey()
                                    .equals(PretupsErrorCodesI.ERROR_SNDR_PINBLOCK)))) {
                                    p_con.commit();
                                }
                                throw be;
                            }
                        }
                    }
                    // if PIN valid as
                    // Validate next Argument for Payment Method.
                    paymentMethodType = ServicePaymentMappingCache.getDefaultPaymentMethod(p_transferVO.getServiceType(), senderSubscriberType);
                    if (paymentMethodType == null) {
                        // return with error message, no default payment method
                        // defined
                        throw new BTSLBaseException(this, "validateIfNotBuddy", PretupsErrorCodesI.ERROR_NOTFOUND_DEFAULTPAYMENTMETHOD);
                    }
                    incomingSmsStr.append(paymentMethodType + " ");

                    p_transferVO.setPaymentMethodType(paymentMethodType);
                    p_transferVO.setDefaultPaymentMethod("Y");

                    // if paymentMethod valid , Validate next Argument for
                    // Receiver
                    // No(MSISDN).
                    checkAfterPaymentMethod(p_con, 1, requestMessageArray, incomingSmsStr, p_transferVO);
                    try {
                        if (!BTSLUtil.isNullString(requestMessageArray[3])) {
                            final int selectorValue = Integer.parseInt(requestMessageArray[3]);
                            p_requestVO.setReqSelector("" + selectorValue);
                        }
                    } catch (Exception e) {
                        _log.errorTrace(METHOD_NAME, e);
                        throw new BTSLBaseException(this, "validateIfNotBuddy", PretupsErrorCodesI.ERROR_INVALID_SELECTOR_VALUE);
                    }
                    try {
                        if (!BTSLUtil.isNullString(requestMessageArray[4])) {
                            final int localeValue = Integer.parseInt(requestMessageArray[4]);
                            p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(requestMessageArray[4]));
                            if (p_requestVO.getReceiverLocale() == null) {
                                throw new BTSLBaseException(this, "validateIfNotBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                            }
                        }
                    } catch (Exception e) {
                        _log.errorTrace(METHOD_NAME, e);
                        throw new BTSLBaseException(this, "validateIfNotBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                    }
                    break;
                }
            case 7:
                {
                    // Validate 2nd Argument for PIN.
                    pin = requestMessageArray[6];
                    incomingSmsStr.append("****" + " ");
                    // if pin Invalid return with error(PIN is Mandatory)
                    // if(actualPin.equalsIgnoreCase(PretupsI.DEFAULT_P2P_PIN))
                    // whether PIN validation is required or not.
                    if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) {
                        if (actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                            // if(!pin.equals(PretupsI.DEFAULT_P2P_PIN))
                            if (!BTSLUtil.isNullString(pin) && !pin.equals((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                                BTSLUtil.validatePIN(pin);
                                senderVO.setPin(BTSLUtil.encryptText(pin));
                                senderVO.setPinUpdateReqd(true);
                                senderVO.setActivateStatusReqd(true);
                            }
                        } else {
                            try {
                                SubscriberBL.validatePIN(p_con, senderVO, pin);
                            } catch (BTSLBaseException be) {
                                if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey()
                                    .equals(PretupsErrorCodesI.ERROR_SNDR_PINBLOCK)))) {
                                    p_con.commit();
                                }
                                throw be;
                            }
                        }
                    }
                    // if PIN valid as
                    // Validate next Argument for Payment Method.

                    PaymentMethodKeywordVO paymentMethodKeywordVO = null;
                    paymentMethodKeyword = requestMessageArray[1];
                    // if paymentMethod invalid , Validate next Argument for
                    // Receiver
                    // No(MSISDN).
                    paymentMethodKeywordVO = PaymentMethodCache.getObject(paymentMethodKeyword, p_transferVO.getServiceType(), p_transferVO.getNetworkCode());

                    if (paymentMethodKeywordVO == null) {
                        throw new BTSLBaseException(this, "validateIfNotBuddy", PretupsErrorCodesI.ERROR_NOTFOUND_SERVICEPAYMENTMETHOD);
                    } else {
                        paymentMethodType = paymentMethodKeywordVO.getPaymentMethodType();
                        p_transferVO.setPaymentMethodType(paymentMethodType);
                        p_transferVO.setPaymentMethodKeywordVO(paymentMethodKeywordVO);
                        p_transferVO.setDefaultPaymentMethod(PretupsI.NO);
                        incomingSmsStr.append(paymentMethodType + " ");
                        checkAfterPaymentMethod(p_con, 2, requestMessageArray, incomingSmsStr, p_transferVO);
                        try {
                            if (!BTSLUtil.isNullString(requestMessageArray[4])) {
                                final int selectorValue = Integer.parseInt(requestMessageArray[4]);
                                p_requestVO.setReqSelector("" + selectorValue);
                            }
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
                            throw new BTSLBaseException(this, "validateIfNotBuddy", PretupsErrorCodesI.ERROR_INVALID_SELECTOR_VALUE);
                        }
                        try {
                            if (!BTSLUtil.isNullString(requestMessageArray[5])) {
                                final int localeValue = Integer.parseInt(requestMessageArray[5]);
                                p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(requestMessageArray[5]));
                                if (p_requestVO.getReceiverLocale() == null) {
                                    throw new BTSLBaseException(this, "validateIfNotBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                }
                            }
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
                            throw new BTSLBaseException(this, "validateIfNotBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                        }
                    }
                    break;
                }
        }
        p_transferVO.setIncomingSmsStr(incomingSmsStr.toString());
    }
}