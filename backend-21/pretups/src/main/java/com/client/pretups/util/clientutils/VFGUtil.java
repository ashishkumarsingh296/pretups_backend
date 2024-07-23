/**
 * @(#)VFGUtil.java
 *                  Copyright(c) 2009, Bharti Telesoft Ltd.
 *                  All Rights Reserved
 * 
 *                  <description>
 *                  ------------------------------------------------------------
 *                  -------------------------------------
 *                  Author Date History
 *                  ------------------------------------------------------------
 *                  -------------------------------------
 *                  Mahindra Comviva March 02, 2015 Initital Creation
 *                  ------------------------------------------------------------
 *                  -------------------------------------
 * 
 */
package com.client.pretups.util.clientutils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class VFGUtil extends OperatorUtil {

    private Log _log = LogFactory.getLog(this.getClass().getName());

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
        int asciiCode = 0;
        boolean isConSeq = false;
        int previousAsciiCode = 0;
        int numSeqcount = 0;
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
        boolean repeatedCharacter = false;
        final Map<Character, Integer> dupMap = new HashMap<Character, Integer>();
        final char[] passCharArr = p_password.toCharArray();
        for (final Character ch : passCharArr) {
            if (dupMap.containsKey(ch)) {
                dupMap.put(ch, dupMap.get(ch) + 1);
            } else {
                dupMap.put(ch, 1);
            }
        }

        final Set<Character> keys = dupMap.keySet();
        for (final Character ch : keys) {
            if (dupMap.get(ch) > Integer.parseInt(Constants.getProperty("ALLOWED_CHARACTER_OCCURANCE"))) {
                repeatedCharacter = true;
                break;
            }
        }

        for (int i = 0; i < passCharArr.length; i++) {
            asciiCode = passCharArr[i];
            if ((previousAsciiCode + 1) == asciiCode) {
                numSeqcount++;
                if (numSeqcount >= 1) {
                    isConSeq = true;
                    break;
                }
            } else {
                numSeqcount = 0;
            }
            previousAsciiCode = asciiCode;
        }
        if (isConSeq) {
            messageMap.put("operatorutil.validatepassword.error.passwordconsecutive", null);
        }
        if (repeatedCharacter) {
            messageMap.put("operatorutil.validatepassword.error.passwordsamedigit", null);
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
    public HashMap validatePassword(String p_password) {
        _log.debug("validatePassword", "Entered,  Password= " + p_password);
        final HashMap messageMap = new HashMap();
        int asciiCode = 0;
        boolean isConSeq = false;
        int previousAsciiCode = 0;
        int numSeqcount = 0;
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
        boolean repeatedCharacter = false;
        final Map<Character, Integer> dupMap = new HashMap<Character, Integer>();
        final char[] passCharArr = p_password.toCharArray();
        for (final Character ch : passCharArr) {
            if (dupMap.containsKey(ch)) {
                dupMap.put(ch, dupMap.get(ch) + 1);
            } else {
                dupMap.put(ch, 1);
            }
        }

        final Set<Character> keys = dupMap.keySet();
        for (final Character ch : keys) {
            if (dupMap.get(ch) > Integer.parseInt(Constants.getProperty("ALLOWED_CHARACTER_OCCURANCE"))) {
                repeatedCharacter = true;
                break;
            }
        }

        for (int i = 0; i < passCharArr.length; i++) {
            asciiCode = passCharArr[i];
            if ((previousAsciiCode + 1) == asciiCode) {
                numSeqcount++;
                if (numSeqcount >= 1) {
                    isConSeq = true;
                    break;
                }
            } else {
                numSeqcount = 0;
            }
            previousAsciiCode = asciiCode;
        }
        if (isConSeq) {
            messageMap.put("operatorutil.validatepassword.error.passwordconsecutive", null);
        }
        if (repeatedCharacter) {
            messageMap.put("operatorutil.validatepassword.error.passwordsamedigit", null);
        }
        if (!numberStrFlag) {
            messageMap.put("operatorutil.validatepassword.error.passwordnumberchar", null);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("validatePassword", "Exiting ");
        }
        return messageMap;
    }

    /**
     * Method generateRandomPassword.
     * 
     * @return String
     */
    public String generateRandomPassword() {
        if (_log.isDebugEnabled()) {
            _log.debug("generateRandomPassword", "Entered in to PeruClaroUtil");
        }
        String returnStr = null;
        String specialStr = "";
        String numberStr = null;
        String alphaStr = null;
        String finalStr = null;
        String SPECIAL_CHARACTERS = null;
        int decreseCounter = 0;
        try {
            boolean flag = false;
            int count = 0;
            do {
                String specialChar = Constants.getProperty("SPECIAL_CHARACTER_PASSWORD_VALIDATION");
                if (!BTSLUtil.isNullString(specialChar)) {
                    decreseCounter = 1;
                    specialChar = specialChar.replace(",", "");
                    SPECIAL_CHARACTERS = specialChar;// "~!@#$%^&";
                    specialStr = BTSLUtil.generateRandomPIN(SPECIAL_CHARACTERS, decreseCounter);
                }
                final String DIGITS = "0123456789";
                numberStr = BTSLUtil.generateRandomPIN(DIGITS, 1);
                decreseCounter++;
                final String LOCASE_CHARACTERS = "abcdefghijklmnopqrstuvwxyz";
                final String UPCASE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                final String PRINTABLE_CHARACTERS = LOCASE_CHARACTERS + UPCASE_CHARACTERS;
                final int minLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_LOGIN_PWD_LENGTH))).intValue();
                while (true) {
                    alphaStr = BTSLUtil.generateRandomPIN(PRINTABLE_CHARACTERS, minLength - decreseCounter);
                    final int result = BTSLUtil.isSMSPinValid(alphaStr);
                    if (result == -1) {
                        continue;
                    } else if (result == 1) {
                        continue;
                    } else {
                        break;
                    }
                }
                finalStr = specialStr + alphaStr + numberStr;
                returnStr = BTSLUtil.generateRandomPIN(finalStr, minLength);
                if (validatePassword(returnStr).isEmpty()) {
                    flag = true;
                } else {
                    Thread.sleep(500);
                    if (count > 5) {
                        flag = true;
                    }
                    count++;
                    if (_log.isDebugEnabled()) {
                        _log.debug("generateRandomPassword", "Regenerating Password =" + returnStr);
                    }
                }
            } while (!flag);
        } catch (Exception e) {
            _log.errorTrace("Ëxception in generateRandomPassword() ", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorUtil[generateRandomPassword]", "", "", "",
                "Exception generate Random Password=" + e.getMessage());
            returnStr = null;
        }
        if (_log.isDebugEnabled()) {
            _log.debug("generateRandomPassword", "Exiting from PeruClaroUtil = " + returnStr);
        }
        return returnStr;
    }

    /**
     * Method decryptPassword.
     * 
     * @param p_password
     *            String
     * @throws BTSLBaseException
     * @see com.btsl.pretups.util.OperatorUtilI#decryptPassword(String
     *      p_password) t
     */
    public String decryptPINPassword(String p_pinpassword) throws BTSLBaseException {
        try {
            p_pinpassword = BTSLUtil.decryptText(p_pinpassword);
        } catch (Exception e) {
            _log.errorTrace("decryptPassword", e);
            p_pinpassword = "";
        }
        return p_pinpassword;
    }

    /**
     * Method decryptPassword.
     * 
     * @param p_password
     *            String
     * @throws BTSLBaseException
     * @see com.btsl.pretups.util.OperatorUtilI#decryptPassword(String
     *      p_password) t
     */
    public String encryptPINPassword(String p_pinpassword) throws BTSLBaseException {
        try {
            p_pinpassword = BTSLUtil.encryptText(p_pinpassword);
        } catch (Exception e) {
            _log.errorTrace("encryptPINPassword", e);
            p_pinpassword = "";
        }
        return p_pinpassword;
    }
}