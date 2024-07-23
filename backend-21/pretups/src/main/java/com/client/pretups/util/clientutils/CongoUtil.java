/*
 * @(#)NigeriaUtil.java
 * Copyright(c) 2010, Comviva Technologies Ltd.
 * All Rights Reserved
 * Description :-
 * --------------------------------------------------------------------
 * Author Date History
 * --------------------------------------------------------------------
 * shishupal.singh Dec 22, 2010 Initial creation
 * --------------------------------------------------------------------
 */
package com.client.pretups.util.clientutils;

import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.util.BTSLUtil;

public class CongoUtil extends OperatorUtil {
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * Method getP2PChangePinMessageArray.
     * 
     * @param message
     *            String[]
     * @return String[]
     */
    public String[] getP2PChangePinMessageArray(String message[]) {
        if (message.length > 2) {
            final String message1[] = new String[message.length + 1];
            message1[0] = message[0];
            message1[1] = message[1];
            message1[2] = message[2];
            message1[3] = message[2];
            return message1;
        } else {
            return message;
        }

    }

    /**
     * Method getC2SChangePinMessageArray.
     * 
     * @param message
     *            String[]
     * @return String[]
     */
    public String[] getC2SChangePinMessageArray(String message[]) {
        if (message.length > 2) {
            final String message1[] = new String[message.length + 1];
            message1[0] = message[0];
            message1[1] = message[1];
            message1[2] = message[2];
            message1[3] = message[2];
            return message1;
        } else {
            return message;
        }

    }

    public void validatePIN(String p_pin) throws BTSLBaseException {
        if (BTSLUtil.isNullString(p_pin)) {
            throw new BTSLBaseException("CongoUtil", "validatePIN", PretupsErrorCodesI.PIN_INVALID);
        } else if (!BTSLUtil.isNumeric(p_pin)) {
            throw new BTSLBaseException("CongoUtil", "validatePIN", PretupsErrorCodesI.NEWPIN_NOTNUMERIC);
        } else if (p_pin.length() < ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_SMS_PIN_LENGTH))).intValue() || p_pin.length() > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_SMS_PIN_LENGTH))).intValue()) {
            final String msg[] = { String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_SMS_PIN_LENGTH))).intValue()), String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_SMS_PIN_LENGTH))).intValue()) };
            throw new BTSLBaseException("CongoUtil", "validatePIN", PretupsErrorCodesI.PIN_LENGTHINVALID, 0, msg, null);
        }
    }

    public HashMap pinValidate(String p_pin) {
        _log.debug("pinValidate", "Entered, PIN= " + p_pin);
        final HashMap messageMap = new HashMap();

        String defaultPin = BTSLUtil.getDefaultPasswordNumeric(p_pin);
        if (defaultPin.equals(p_pin)) {
            return messageMap;
        }

        defaultPin = BTSLUtil.getDefaultPasswordText(p_pin);
        if (defaultPin.equals(p_pin)) {
            return messageMap;
        }

        if (!BTSLUtil.isNumeric(p_pin)) {
            messageMap.put("operatorutil.validatepin.error.pinnotnumeric", null);
        }
        if (p_pin.length() < ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_SMS_PIN_LENGTH))).intValue() || p_pin.length() > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_SMS_PIN_LENGTH))).intValue()) {
            final String[] args = { String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_SMS_PIN_LENGTH))).intValue()), String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_SMS_PIN_LENGTH))).intValue()) };
            messageMap.put("operatorutil.validatepin.error.smspinlenerr", args);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("pinValidate", "Exiting messageMap.size()=" + messageMap.size());
        }
        return messageMap;
    }

}
