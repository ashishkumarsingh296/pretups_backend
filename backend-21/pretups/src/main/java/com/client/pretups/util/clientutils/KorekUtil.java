package com.client.pretups.util.clientutils;

import java.util.HashMap;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class KorekUtil extends OperatorUtil {
    private Log _log = LogFactory.getLog(this.getClass().getName());

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

        // For OCI Password Should contains atleast one character
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
        if (!BTSLUtil.containsCapChar(p_password.substring(1))) {
            messageMap.put("operatorutil.validatepassword.error.passwordnotcontaincapschar", null);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("validatePassword", "Exiting messageMap.size()=" + messageMap.size());
        }
        return messageMap;
    }

}
