/**
 * @(#)RwandaUtil.java
 *                     Copyright(c) 2009, Comviva Ltd.
 *                     All Rights Reserved
 * 
 *                     <description>
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     Author Date History
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     Manisha Jain Aug 28, 2009 Initital Creation
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 * 
 */

package com.client.pretups.util.clientutils;

import java.util.HashMap;
import java.util.Random;

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

public class RwandaUtil extends OperatorUtil {
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private static Random rn = new Random();

    private static int rand(int lo, int hi) {
        final int n = hi - lo + 1;
        int i = rn.nextInt() % n;
        if (i < 0) {
            i = -i;
        }
        return lo + i;
    }

    /**
     * This method used for LoginId validation.
     * While creating or modifying the user LoginId This method will be used.
     * Method validateLoginId.
     * 
     * @param p_loginID
     *            String
     * @param p_password
     *            String
     * @return HashMap
     */
    public HashMap validateLoginId(String p_loginID) {
        _log.debug("validateLoginId", "Entered, p_userID= ", new String(p_loginID));
        final HashMap messageMap = new HashMap();

        /*
         * if(!BTSLUtil.containsChar(p_loginID))
         * messageMap.put(
         * "operatorutil.validatepassword.error.passwordnotcontainschar",null);
         */
        // for special character not required for Tigo Rwanda client specific
        // changes
        /*
         * String
         * specialChar=Constants.getProperty("SPECIAL_CHARACTER_LOGIN_VALIDATION"
         * );
         * if(!BTSLUtil.isNullString(specialChar))
         * {
         * String[] specialCharArray={specialChar};
         * String[] passwordCharArray=specialChar.split(",");
         * boolean specialCharFlag=false;
         * for(int i=0,j=passwordCharArray.length;i<j;i++)
         * {
         * if(p_loginID.contains(passwordCharArray[i]))
         * {
         * specialCharFlag=true;
         * break;
         * }
         * }
         * 
         * if(!specialCharFlag)
         * messageMap.put("operatorutil.validatepassword.error.passwordspecialchar"
         * ,specialCharArray);
         * 
         * }
         */
        // for number
        final String[] passwordNumberStrArray = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
        boolean numberStrFlag = false;
        for (int i = 0, j = passwordNumberStrArray.length; i < j; i++) {
            if (p_loginID.contains(passwordNumberStrArray[i])) {
                numberStrFlag = true;
                break;
            }
        }
        if (!numberStrFlag) {
            messageMap.put("operatorutil.validatepassword.error.passwordnumberchar", null);
        }

        if (_log.isDebugEnabled()) {
            _log.debug("validateLoginId", "Exiting ");
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
        /*
         * if(p_loginID.equals(p_password))
         * messageMap.put("operatorutil.validatepassword.error.sameusernamepassword"
         * ,null);
         */
        if (_log.isDebugEnabled()) {
            _log.debug("validatePassword", "Exiting messageMap.size()=" + messageMap.size());
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
            _log.debug("generateRandomPassword", "Entered in to RwandaUtil");
        }
        final String METHOD_NAME = "generateRandomPassword";
        String returnStr = null;
        String specialStr = "";
        String numberStr = null;
        String alphaStr = null;
        String finalStr = null;
        String SPECIAL_CHARACTERS = null;
        int decreseCounter = 0;
        try {
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
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorUtil[generateRandomPassword]", "", "", "",
                "Exception generate Random Password=" + e.getMessage());
            returnStr = null;
        }
        if (_log.isDebugEnabled()) {
            _log.debug("generateRandomPassword", "Exiting from RwandaUtil = " + returnStr);
        }
        return returnStr;
    }
}