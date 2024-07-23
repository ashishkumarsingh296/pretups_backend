package com.client.pretups.util.clientutils;

/**
 * @(#)KenyaUtil.java
 *                    Copyright(c) 2005, Bharti Telesoft Int. Public Ltd.
 *                    All Rights Reserved
 *                    This class is used to store System Preferences for Pretups
 *                    System.
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    Author Date History
 *                    Ranjana Chouhan january 16,2009 Initial Creation
 *                    ----------------------------------------------------------
 *                    --------------------------------------
 */
import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

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
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;

/**
 * @author ranjana.chouhan
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class KenyaUtil extends OperatorUtil {
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private static Random rn = new Random();

    /**
     * Method to generate Random Pin
     * 
     * @return String
     */
    public String randomPinGenerate() {
        if (_log.isDebugEnabled()) {
            _log.debug("randomPinGenerate", " Entered PIN:");
        }

        int result = -1, count = 0;
        String pin = "";
        do {
            pin = PretupsBL.genratePin();
            result = BTSLUtil.isSMSPinValid(pin);
            if (result == 0) {
                count = pinCheck(pin);
            }

        } while (count > 1 || result != 0);

        if (_log.isDebugEnabled()) {
            _log.debug("randomPinGenerate", " Entered PIN:" + pin);
        }
        return pin;
    }

    /**
     * Method to validate Pin
     * 
     * @param String
     * @return HashMap
     */
    public HashMap pinValidate(String p_pin) {
        if (_log.isDebugEnabled()) {
            _log.debug("validatePIN", "Entered, PIN= " + p_pin);
        }
        final HashMap messageMap = new HashMap();

        if (!BTSLUtil.isNumeric(p_pin)) {
            messageMap.put("operatorutil.validatepin.error.pinnotnumeric", null);
        }
        if (p_pin.length() < ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_SMS_PIN_LENGTH))).intValue() || p_pin.length() > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_SMS_PIN_LENGTH))).intValue()) {
            final String[] args = { String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_SMS_PIN_LENGTH))).intValue()), String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_SMS_PIN_LENGTH))).intValue()) };
            messageMap.put("operatorutil.validatepin.error.smspinlenerr", args);
        }
        final int result = BTSLUtil.isSMSPinValid(p_pin);
        if (result == -1) {
            messageMap.put("operatorutil.validatepin.error.pinsamedigit", null);
        } else if (result == 1) {
            messageMap.put("operatorutil.validatepin.error.pinconsecutive", null);
        }
        // to check the more than 2 repeated number i.e. 1333
        final int count = pinCheck(p_pin);
        if (count > 1) {
            messageMap.put("operatorutil.validatepin.error.pinmorethan2repetative", null);
        }

        if (_log.isDebugEnabled()) {
            _log.debug("validatePIN", "Exiting messageMap.size()=" + messageMap.size());
        }
        return messageMap;
    }

    /**
     * Method to Check Pin i.e it contains two repetative digits Only.
     * i.e. 1733 valid, 1333 not valid
     * 
     * @param String
     * @return int
     */
    private int pinCheck(String pin) {
        int pos = 0, count = 0;
        for (int i = 0; i < pin.length(); i++) {
            pos = pin.charAt(i);
            for (int x = i + 1; x < pin.length(); x++) {
                if (pos == pin.charAt(x)) {
                    count++;
                }
            }
        }
        return count;
    }

    private static int rand(int lo, int hi) {
        final int n = hi - lo + 1;
        int i = rn.nextInt() % n;
        if (i < 0) {
            i = -i;
        }
        return lo + i;
    }

    private static String randomstring() {
        final int n = rand(8, 12);
        final byte b[] = new byte[n];
        for (int i = 0; i < n; i++) {
            b[i] = (byte) rand('a', 'z');
        }
        return new String(b);
    }

    /**
     * This method used for Password validation.
     * While creating or modifying the user Password This method will be used.
     * Method validatePassword.
     * 
     * @param p_loginID
     *            String
     * @param p_password
     *            String
     * @return HashMap
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

        // Password Should contains atleast one character
        if (!BTSLUtil.containsChar(p_password) || !containsNum(p_password)) {
            messageMap.put("operatorutil.validatepassword.error.passwordnumberchar", null);
        }

        final char x = p_password.charAt(0);
        if (x >= 'A' && x <= 'Z') {
            messageMap.put("operatorutil.validatepassword.error.firstchariscaps", null);
        }

        if (!BTSLUtil.containsCapChar(p_password)) {
            messageMap.put("operatorutil.validatepassword.error.passwordnotcontaincapschar", null);
        }

        if (_log.isDebugEnabled()) {
            _log.debug("validatePassword", "Exiting messageMap.size()=" + messageMap.size());
        }
        return messageMap;
    }

    /**
     * To check String have at least one integer value or not.
     * 
     * @param str
     * @return boolean if contains integer returns true else false;
     */
    private boolean containsNum(String str) {
        final String[] passwordNumberStrArray = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
        boolean flag = false;

        for (int i = 0, j = passwordNumberStrArray.length; i < j; i++) {
            if (str.contains(passwordNumberStrArray[i])) {
                flag = true;
                break;
            }
        }
        return flag;
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
        if (_log.isDebugEnabled()) {
            _log.debug("validatePIN", "Entered with p_userPhoneVO:" + p_channelUserVO.toString() + " p_requestPin=" + p_requestPin);
        }
        final String METHOD_NAME = "validatePIN";
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
                    "validatePIN",
                    "Modified Time=:" + userPhoneVO.getModifiedOn() + " userPhoneVO.getPinModifiedOn()=" + userPhoneVO.getPinModifiedOn() + "userPhoneVO.getCreatedOn()" + userPhoneVO
                        .getCreatedOn() + ", Service Type=" + p_channelUserVO.getServiceTypes() + ", Reset PIN=" + p_channelUserVO.getPinReset());
            }

            final int daysAfterChngPn = ((Integer) PreferenceCache.getControlPreference(PreferenceI.C2S_DAYS_AFTER_CHANGE_PIN, p_channelUserVO.getNetworkID(), p_channelUserVO
                .getCategoryCode())).intValue();
            if ("Y".equalsIgnoreCase(p_channelUserVO.getPinReset()) && (userPhoneVO.isAccessOn() || ((userPhoneVO.getPinModifiedOn().getTime()) == (userPhoneVO.getCreatedOn()
                .getTime())))) {
                Date pinExpiredTime = null;
                final Calendar cal = Calendar.getInstance();
                cal.setTime(userPhoneVO.getPinModifiedOn());
                final int pinExpiredInHours = ((Integer) PreferenceCache.getControlPreference(PreferenceI.RESET_PIN_EXPIRED_TIME_IN_HOURS, p_channelUserVO.getNetworkID(),
                    p_channelUserVO.getCategoryCode())).intValue();
                cal.add(Calendar.HOUR, pinExpiredInHours);
                pinExpiredTime = cal.getTime();
                final Date curentDate = new Date();

                if (curentDate.after(pinExpiredTime)) {
                    throw new BTSLBaseException("KenyaUtil", "validatePIN", PretupsErrorCodesI.CHNL_ERROR_SNDR_REGISTERED_PIN_EXPIRED);
                }

            }
            // added for OCI changes regarding to change PIN on 1st request
            if (userPhoneVO.isForcePinCheckReqd() && (((userPhoneVO.getPinModifiedOn().getTime()) == (userPhoneVO.getCreatedOn().getTime())) || userPhoneVO.isAccessOn())) {
                throw new BTSLBaseException("KenyaUtil", "validatePIN", PretupsErrorCodesI.CHNL_FIRST_REQUEST_PIN_CHANGE);
            }

            if (userPhoneVO.isForcePinCheckReqd() && userPhoneVO.getPinModifiedOn() != null && ((userPhoneVO.getModifiedOn().getTime() - userPhoneVO.getPinModifiedOn()
                .getTime()) / (24 * 60 * 60 * 1000)) > daysAfterChngPn) {
                // Force the user to change PIN if he has not changed the same
                // in the defined no of days
                if (_log.isDebugEnabled()) {
                    _log.debug("validatePIN",
                        "Modified Time=:" + userPhoneVO.getModifiedOn() + " userPhoneVO.getPinModifiedOn()=" + userPhoneVO.getPinModifiedOn() + " Difference=" + ((userPhoneVO
                            .getModifiedOn().getTime() - userPhoneVO.getPinModifiedOn().getTime()) / (24 * 60 * 60 * 1000)));
                }
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "KenyaUtil[validatePIN]", "",
                    userPhoneVO.getMsisdn(), "", "Force User to change PIN after " + daysAfterChngPn + " days as last changed on " + userPhoneVO.getPinModifiedOn());
                final String strArr[] = { String.valueOf(daysAfterChngPn) };
                throw new BTSLBaseException("KenyaUtil", "validatePIN", PretupsErrorCodesI.CHNL_ERROR_SNDR_FORCE_CHANGEPIN, 0, strArr, null);
            }
            // added to force user to change pin in case pin has been reset for
            // all the request except change pin request
            else if ("Y".equals(p_channelUserVO.getPinReset()) && !PretupsI.CHANGE_PIN_SERVICE_TYPE.contains(p_channelUserVO.getServiceTypes())) {
                if (_log.isDebugEnabled()) {
                    _log.debug("validatePIN",
                        "Modified Time=" + userPhoneVO.getModifiedOn() + ", Pin Modified On=" + userPhoneVO.getPinModifiedOn() + ", Difference=" + ((userPhoneVO
                            .getModifiedOn().getTime() - userPhoneVO.getPinModifiedOn().getTime()) / (24 * 60 * 60 * 1000)));
                }
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "KenyaUtil[validatePIN]", "",
                    userPhoneVO.getMsisdn(), "", "Force User to change new reset PIN.");
                throw new BTSLBaseException("KenyaUtil", "validatePIN", PretupsErrorCodesI.CHNL_ERROR_SNDR_FORCE_CHANGE_RESETPIN, 0, null);
            } else {
                final String decryptedPin = BTSLUtil.decryptText(userPhoneVO.getSmsPin());
                if (_log.isDebugEnabled()) {
                    _log.debug("validatePIN", "Sender MSISDN:" + userPhoneVO.getMsisdn() + " decrypted PIN of database=" + decryptedPin + " p_requestPin =" + p_requestPin);
                }

                /*
                 * change done by ashishT
                 * comparing hashvalue of password set in userphonevo to the
                 * hashvalue of pin sent by user
                 */
                // if (!decryptedPin.equals(p_requestPin))
                boolean checkpin;
                if ("SHA".equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE))) {
                    if (p_requestPin.length() > SystemPreferences.C2S_PIN_MAX_LENGTH) {
                        checkpin = decryptedPin.equals(p_requestPin);
                    } else {
                        checkpin = (!PretupsI.FALSE.equalsIgnoreCase(BTSLUtil.compareHash2String(decryptedPin, p_requestPin)));
                    }
                } else {
                    checkpin = decryptedPin.equals(p_requestPin);
                }
                if (!checkpin) {
                    increaseInvalidPinCount = true;
                    if (userPhoneVO.getFirstInvalidPinTime() != null) {
                        // Check if PIN counters needs to be reset after the
                        // reset duration
                        final long pnBlckRstDuration = ((Long) PreferenceCache.getControlPreference(PreferenceI.C2S_PIN_BLK_RST_DURATION, p_channelUserVO.getNetworkID(),
                            p_channelUserVO.getCategoryCode())).longValue();
                        if (_log.isDebugEnabled()) {
                            _log.debug(
                                "validatePIN",
                                "p_userPhoneVO.getModifiedOn().getTime()=" + userPhoneVO.getModifiedOn().getTime() + " p_userPhoneVO.getFirstInvalidPinTime().getTime()=" + userPhoneVO
                                    .getFirstInvalidPinTime().getTime() + " Diff=" + ((userPhoneVO.getModifiedOn().getTime() - userPhoneVO.getFirstInvalidPinTime().getTime()) / (60 * 1000)) + " Allowed=" + pnBlckRstDuration);
                        }
                        final Calendar cal = Calendar.getInstance();
                        cal.setTime(userPhoneVO.getModifiedOn());
                        final int d1 = cal.get(Calendar.DAY_OF_YEAR);
                        cal.setTime(userPhoneVO.getFirstInvalidPinTime());
                        final int d2 = cal.get(Calendar.DAY_OF_YEAR);
                        if (_log.isDebugEnabled()) {
                            _log.debug("validatePIN", "Day Of year of Modified On=" + d1 + " Day Of year of FirstInvalidPinTime=" + d2);
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
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "KenyaUtil[validatePIN]", "",
                                userPhoneVO.getMsisdn(), "", "Not able to update invalid PIN count for users");
                            throw new BTSLBaseException("KenyaUtil", "validatePIN", PretupsErrorCodesI.ERROR_EXCEPTION);
                        }
                    }
                }
                if (increaseInvalidPinCount) {
                    updateStatus = new ChannelUserDAO().updateSmsPinCounter(p_con, userPhoneVO);
                    if (updateStatus > 0 && !isUserBarred) {
                        throw new BTSLBaseException("KenyaUtil", "validatePIN", PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN);
                    } else if (updateStatus > 0 && isUserBarred) {
                        throw new BTSLBaseException("KenyaUtil", "validatePIN", PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK);
                    } else {
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "KenyaUtil[validatePIN]", "", userPhoneVO
                            .getMsisdn(), "", "Not able to update invalid PIN count for users");
                        throw new BTSLBaseException("KenyaUtil", "validatePIN", PretupsErrorCodesI.ERROR_EXCEPTION);
                    }
                }
            }

        } catch (BTSLBaseException bex) {
            throw bex;
        } catch (Exception e) {
            _log.error("validatePIN", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "KenyaUtil[validatePIN]", "", "", "", "Exception:" + e
                .getMessage());
            throw new BTSLBaseException("KenyaUtil", "validatePIN", PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("validatePIN", "Exiting with increase invalid Pin Count flag=" + increaseInvalidPinCount);
            }
        }
    }

    /**
     * To check the period after which the created password (on first time
     * creation) will be expired.
     */
    public boolean checkPasswordPeriodToResetAfterCreation(Date p_modifiedOn, ChannelUserVO p_channelUserVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("checkPasswordPeriodToResetAfterCreation", "Entered with _categoryCode:" + p_channelUserVO.getCategoryCode() + " , _networkId =" + p_channelUserVO
                .getNetworkID() + " modifiedDate=" + p_modifiedOn + ", _loginTime=" + p_channelUserVO.getLoginTime());
        }
        boolean passwordResetFlag = false;
        Date passwordExpiredTime = null;
        final Calendar cal = Calendar.getInstance();
        cal.setTime(p_modifiedOn);
        final int passwordExpiredInHours = ((Integer) PreferenceCache.getControlPreference(PreferenceI.PSWD_EXP_TIME_IN_HOUR_AFTER_CREATION, p_channelUserVO.getNetworkID(),
            p_channelUserVO.getCategoryCode())).intValue();
        cal.add(Calendar.HOUR, passwordExpiredInHours);
        passwordExpiredTime = cal.getTime();
        if (passwordExpiredInHours == 0) {
            passwordResetFlag = false;
        } else if ("Y".equals(p_channelUserVO.getPasswordReset()) && p_channelUserVO.getLoginTime().after(passwordExpiredTime)) {
            passwordResetFlag = true;
        }

        if (_log.isDebugEnabled()) {
            _log.debug("checkPasswordPeriodToResetAfterCreation", "Exited with passwordResetFlag:" + passwordResetFlag);
        }

        return passwordResetFlag;
    }
}
