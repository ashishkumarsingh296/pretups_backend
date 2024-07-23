package com.client.pretups.util.clientutils;

import java.util.HashMap;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class AUPUtil extends OperatorUtil {
	
    private Log _log = LogFactory.getLog(this.getClass().getName());
	
	/**
     * This method used for Password validation.
     * While creating or modifying the user Password This method will be used.
     * Method validatePassword.
     * 
     * @param loginID
     *            String
     * @param password
     *            String
     * @return HashMap
     */
    public HashMap validatePassword(String loginID, String password) {
        LogFactory.printLog("validatePassword", "Entered, p_userID= "+loginID + ", Password= " + password, _log);
        final HashMap messageMap = new HashMap();

        String defaultPin = BTSLUtil.getDefaultPasswordNumeric(password);
        if (defaultPin.equals(password)) {
            return messageMap;
        }
        defaultPin = BTSLUtil.getDefaultPasswordText(password);
        if (defaultPin.equals(password)) {
            return messageMap;
        }
        if (password.length() < ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_LOGIN_PWD_LENGTH))).intValue() || password.length() > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_LOGIN_PWD_LENGTH))).intValue()) {
            final String[] args = { String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_LOGIN_PWD_LENGTH))).intValue()), String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_LOGIN_PWD_LENGTH))).intValue()) };
            messageMap.put("operatorutil.validatepassword.error.passwordlenerr", args);
        }
        // for consecutive
        final int result = BTSLUtil.isSMSPinValid(password);
        // and
        // same characters
        if (result == -1) {
            messageMap.put("operatorutil.validatepassword.error.passwordsamedigit", null);
        } else if (result == 1) {
            messageMap.put("operatorutil.validatepassword.error.passwordconsecutive", null);
        }
        
        if (password.trim().length() != password.length()) {
            messageMap.put("user.adduser.error.password.space.not.allowed", null);
        }
        
        if (!BTSLUtil.containsChar(password)) {
            messageMap.put("operatorutil.validatepassword.error.passwordnotcontainschar", null);
        }
        // for special character
        final String specialChar = Constants.getProperty("SPECIAL_CHARACTER_PASSWORD_VALIDATION");
        if (!BTSLUtil.isNullString(specialChar)) {
            final String[] specialCharArray = { specialChar };
            final String[] passwordCharArray = specialChar.split(",");
            boolean specialCharFlag = false;
            for (int i = 0, j = passwordCharArray.length; i < j; i++) {
                if (password.contains(passwordCharArray[i])) {
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
            if (password.contains(passwordNumberStrArray[i])) {
                numberStrFlag = true;
                break;
            }
        }
        boolean repeatedCharacter = false;
        final char[] passCharArr = password.toCharArray();
        Character previousCharacter = '\0';
        for (final Character ch : passCharArr) {
            if (ch.equals(previousCharacter)){
                repeatedCharacter=true;
            }
            previousCharacter = ch;     
        }

        if (repeatedCharacter) {
            messageMap.put("operatorutil.validatepassword.error.passwordsamedigit", null);
        }
        if (!numberStrFlag) {
            messageMap.put("operatorutil.validatepassword.error.passwordnumberchar", null);
        }
        if (loginID.equals(password) || password.contains(loginID)) {
            messageMap.put("operatorutil.validatepassword.error.sameusernamepassword", null);
        }
        if(!BTSLUtil.containsCapChar(password)){
            messageMap.put("operatorutil.validatepassword.error.passwordnotcontaincapschar",null);
        }
        if(!BTSLUtil.containsSmallChar(password)){
            messageMap.put("operatorutil.validatepassword.error.passwordnotcontainsmallchar",null);
        }
        LogFactory.printLog("validatePassword", "Exiting ", _log);
        return messageMap;
    }
}
