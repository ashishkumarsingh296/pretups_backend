/**
 * @(#)GuatemalaUtil.java
 *                        Copyright(c) 2009, Bharti Telesoft Ltd.
 *                        All Rights Reserved
 * 
 *                        <description>
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Author Date History
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Sushma Sep 21, 2009 Initital Creation
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 * 
 */
package com.client.pretups.util.clientutils;

import java.sql.Connection;
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
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class GuatemalaUtil extends OperatorUtil {

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
     * Method generateRandomPassword.
     * 
     * @return String
     */
    public String generateRandomPassword() {
        if (_log.isDebugEnabled()) {
            _log.debug("generateRandomPassword", "Entered in to GuatemalaUtil");
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
            _log.debug("generateRandomPassword", "Exiting from GuatemalaUtil = " + returnStr);
        }
        return returnStr;
    }

    /**
     * This method validate the LoginId.
     * 9:47:00 AM
     * HashMap
     * sushma.salve
     */
    public HashMap validateLoginId(String p_loginID) {
        _log.debug("validateLoginId", "Entered, p_userID= " + p_loginID);

        final HashMap messageMap = new HashMap();
        if (!BTSLUtil.containsChar(p_loginID)) {
            messageMap.put("operatorutil.validateloginid.error.logindnotcontainschar", null);
        }
        // for special character
        final String specialChar = Constants.getProperty("SPECIAL_CHARACTER_LOGIN_VALIDATION");
        if (!BTSLUtil.isNullString(specialChar)) {
            final String[] specialCharArray = { specialChar };
            final String[] passwordCharArray = specialChar.split(",");
            boolean specialCharFlag = false;
            for (int i = 0, j = passwordCharArray.length; i < j; i++) {
                if (p_loginID.contains(passwordCharArray[i])) {
                    specialCharFlag = true;
                    break;
                }
            }
            if (!specialCharFlag) {
                messageMap.put("operatorutil.validateloginid.error.loginspecialchar", specialCharArray);
            }
        }

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
            messageMap.put("operatorutil.validatelogind.error.loginidnumberchar", null);
        }

        if (_log.isDebugEnabled()) {
            _log.debug("validateLoginId", "Exiting ");
        }
        return messageMap;
    }

    /**
     * @author vinay.kumar
     * @param p_INPromo
     *            double
     * @param p_BonusTalkTime
     *            long
     * @return finalPromo double
     */
    public double calculateINPromo(double p_INPromo, long p_BonusTalkTime) {
        double finalPromo = 0;

        if (p_INPromo == 0 && p_BonusTalkTime == 0) {
            finalPromo = 0;
        } else if (p_INPromo != 0 && p_BonusTalkTime == 0) {
            finalPromo = 0;
        } else if (p_INPromo == 0 && p_BonusTalkTime != 0) {
            finalPromo = p_BonusTalkTime;
        } else if (p_BonusTalkTime > p_INPromo) {
            finalPromo = p_BonusTalkTime - p_INPromo;
        } else {
            finalPromo = 0;
        }

        return finalPromo;
    }

    /**
     * Method calculateReceiverTransferValue.
     * 
     * @param p_requestedValue
     *            long
     * @param p_calculatedAccessFee
     *            long
     * @param p_calculatedTax1Value
     *            long
     * @param p_calculatedTax2Value
     *            long
     * @param p_calculatedBonusTalkTimeValue
     *            long
     * @return long
     * @see com.btsl.pretups.util.OperatorUtilI#calculateReceiverTransferValue(long,
     *      long, long, long, long)
     */
    public long calculateReceiverTransferValue(long p_requestedValue, long p_calculatedAccessFee, long p_calculatedTax1Value, long p_calculatedTax2Value, long p_calculatedBonusTalkTimeValue) {
        final long transferValue = p_requestedValue - p_calculatedAccessFee - p_calculatedTax1Value - p_calculatedTax2Value;
        return transferValue;
    }

    /**
     * Method that will validate the user message sent in case of postpaid bill
     * payment
     * This method is added on 15/05/06 for postpaid bill payment.
     * 
     * @param p_con
     * @param p_c2sTransferVO
     * @param p_requestVO
     * @throws BTSLBaseException
     * @see com.btsl.pretups.util.OperatorUtilI#validateC2SBillPmtRequest(Connection,
     *      C2STransferVO, RequestVO)
     */
    public void validateC2SBillPmtRequest(Connection p_con, C2STransferVO p_c2sTransferVO, RequestVO p_requestVO) throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("validateC2SBillPmtRequest", "entered:p_c2sTransferVO=" + p_c2sTransferVO + " p_requestVO: " + p_requestVO);
        }
        final String METHOD_NAME = "validateC2SBillPmtRequest";
        try {
            final String[] p_requestArr = p_requestVO.getRequestMessageArray();
            String custMsisdn = null;
            String requestAmtStr = null;
            final ChannelUserVO channelUserVO = (ChannelUserVO) p_c2sTransferVO.getSenderVO();
            // get the message length
            final int messageLen = p_requestArr.length;
            if (_log.isDebugEnabled()) {
                _log.debug("validateC2SBillPmtRequest", "messageLen: " + messageLen);
            }
            for (int i = 0; i < messageLen; i++) {
                if (_log.isDebugEnabled()) {
                    _log.debug("validateC2SBillPmtRequest", "i: " + i + " value: " + p_requestArr[i]);
                }
            }
            switch (messageLen) {
                case 4:// message format expected:
                       // keyword#receivernumber#amount#pin
                    {
                        // If pin required flag is Y for the sender in user
                        // phones
                        // table
                        // then validate the user pin
                        if ((((ChannelUserVO) p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES)) {
                            try {
                                ChannelUserBL.validatePIN(p_con, channelUserVO, p_requestArr[3]);
                            } catch (BTSLBaseException be) {
                                if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN_BILLPAY)) || (be.getMessageKey()
                                    .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK_BILLPAY)))) {
                                    p_con.commit();
                                }
                                throw be;
                            }
                        }
                        final ReceiverVO receiverVO = new ReceiverVO();
                        // Customer MSISDN Validation
                        custMsisdn = p_requestArr[1];
                        // validate msisdn
                        PretupsBL.validateMsisdn(p_con, receiverVO, p_c2sTransferVO.getRequestID(), custMsisdn);

                        // Recharge amount Validation
                        requestAmtStr = p_requestArr[2];
                        // validate the amount
                        PretupsBL.validateAmount(p_c2sTransferVO, requestAmtStr);
                        p_c2sTransferVO.setReceiverVO(receiverVO);
                        // p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_DEF_SELECTOR_CODE_BILLPAY));
                        final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO
                            .getServiceType());
                        if (serviceSelectorMappingVO != null) {
                            p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                        }
                        // set the default local of receiver
                        p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                        break;
                    }

                case 5:// message format expected:
                       // keyword#receivernumber#amount#receiverNotificationlang#pin
                    {
                        if ((((ChannelUserVO) p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES)) {
                            try {
                                // validate pin
                                ChannelUserBL.validatePIN(p_con, channelUserVO, p_requestArr[4]);
                            } catch (BTSLBaseException be) {
                                if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN_BILLPAY)) || (be.getMessageKey()
                                    .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK_BILLPAY)))) {
                                    p_con.commit();
                                }
                                throw be;
                            }
                        }
                        final ReceiverVO receiverVO = new ReceiverVO();
                        // Customer MSISDN Validation
                        custMsisdn = p_requestArr[1];

                        PretupsBL.validateMsisdn(p_con, receiverVO, p_c2sTransferVO.getRequestID(), custMsisdn);

                        // Recharge amount Validation
                        requestAmtStr = p_requestArr[2];
                        PretupsBL.validateAmount(p_c2sTransferVO, requestAmtStr);
                        p_c2sTransferVO.setReceiverVO(receiverVO);
                        // p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_DEF_SELECTOR_CODE_BILLPAY));
                        final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO
                            .getServiceType());
                        if (serviceSelectorMappingVO != null) {
                            p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                        }
                        // if requested local is not null then set that locale
                        // otherwise
                        // set default
                        if (BTSLUtil.isNullString(p_requestArr[3])) {
                            p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                        } else {
                            final int langCode = PretupsBL.getLocaleValueFromCode(p_requestVO, p_requestArr[3]);
                            if (LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)) == null) {
                                p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                            } else {
                                p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
                                // throw new
                                // BTSLBaseException(this,"validateC2SBillPmtRequest",PretupsErrorCodesI.BILLPAY_INVALID_PAYEE_NOT_LANG);
                            }

                        }
                        break;
                    }
                case 6:// message format expected:
                       // keyword#receivernumber#amount#selector#receiverNotificationlang#pin
                    {
                        if ((((ChannelUserVO) p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES)) {
                            try {
                                ChannelUserBL.validatePIN(p_con, channelUserVO, p_requestArr[5]);
                            } catch (BTSLBaseException be) {
                                if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN_BILLPAY)) || (be.getMessageKey()
                                    .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK_BILLPAY)))) {
                                    p_con.commit();
                                }
                                throw be;
                            }
                        }

                        final ReceiverVO receiverVO = new ReceiverVO();
                        // Customer MSISDN Validation
                        custMsisdn = p_requestArr[1];

                        PretupsBL.validateMsisdn(p_con, receiverVO, p_c2sTransferVO.getRequestID(), custMsisdn);

                        // Recharge amount Validation
                        requestAmtStr = p_requestArr[2];
                        PretupsBL.validateAmount(p_c2sTransferVO, requestAmtStr);
                        p_c2sTransferVO.setReceiverVO(receiverVO);
                        if (BTSLUtil.isNullString(p_requestArr[3])) {
                            if (PretupsI.LOCALE_LANGAUGE_EN.equalsIgnoreCase(p_requestVO.getLocale().getLanguage())) {
                                // p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_DEF_SELECTOR_CODE_BILLPAY));
                                final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO
                                    .getServiceType());
                                if (serviceSelectorMappingVO != null) {
                                    p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                                }
                            }
                        } else {
                            p_requestVO.setReqSelector(p_requestArr[3]);
                        }

                        PretupsBL.getSelectorValueFromCode(p_requestVO);
                        if (BTSLUtil.isNullString(p_requestVO.getReqSelector())) {
                            // p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_DEF_SELECTOR_CODE_BILLPAY));
                            final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO
                                .getServiceType());
                            if (serviceSelectorMappingVO != null) {
                                p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                            }
                        }
                        if (BTSLUtil.isNullString(p_requestArr[4])) {
                            p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                        } else {
                            final int langCode = PretupsBL.getLocaleValueFromCode(p_requestVO, p_requestArr[4]);
                            if (LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)) == null) {
                                throw new BTSLBaseException(this, "validateC2SBillPmtRequest", PretupsErrorCodesI.BILLPAY_INVALID_PAYEE_NOT_LANG);
                            }
                            p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
                        }
                        break;
                    }
                case 7:// message format expected:
                       // keyword#receivernumber#amount#selector#senderlang#receiverNotificationlang#pin
                    {
                        if ((((ChannelUserVO) p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES)) {
                            try {
                                ChannelUserBL.validatePIN(p_con, channelUserVO, p_requestArr[6]);
                            } catch (BTSLBaseException be) {
                                if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN_BILLPAY)) || (be.getMessageKey()
                                    .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK_BILLPAY)))) {
                                    p_con.commit();
                                }
                                throw be;
                            }
                        }

                        final ReceiverVO receiverVO = new ReceiverVO();
                        // Customer MSISDN Validation
                        custMsisdn = p_requestArr[1];

                        PretupsBL.validateMsisdn(p_con, receiverVO, p_c2sTransferVO.getRequestID(), custMsisdn);

                        // Recharge amount Validation
                        requestAmtStr = p_requestArr[2];
                        PretupsBL.validateAmount(p_c2sTransferVO, requestAmtStr);
                        p_c2sTransferVO.setReceiverVO(receiverVO);
                        if (BTSLUtil.isNullString(p_requestArr[3])) {
                            if (PretupsI.LOCALE_LANGAUGE_EN.equalsIgnoreCase(p_requestVO.getLocale().getLanguage())) {
                                // p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_DEF_SELECTOR_CODE_BILLPAY));
                                final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO
                                    .getServiceType());
                                if (serviceSelectorMappingVO != null) {
                                    p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                                }
                            }
                        } else {
                            p_requestVO.setReqSelector(p_requestArr[3]);
                        }

                        PretupsBL.getSelectorValueFromCode(p_requestVO);
                        if (BTSLUtil.isNullString(p_requestVO.getReqSelector())) {
                            // p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_DEF_SELECTOR_CODE_BILLPAY));
                            final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO
                                .getServiceType());
                            if (serviceSelectorMappingVO != null) {
                                p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                            }
                        }

                        // For handling of sender locale
                        if (BTSLUtil.isNullString(p_requestArr[4])) {
                            p_requestVO.setSenderLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                        } else {
                            final int langCode = PretupsBL.getLocaleValueFromCode(p_requestVO, p_requestArr[4]);
                            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
                            // ChangeID=LOCALEMASTER
                            // Sender locale has to be overwritten in transferVO
                            // also.
                            p_c2sTransferVO.setLocale(p_requestVO.getSenderLocale());
                            p_c2sTransferVO.setLanguage(p_c2sTransferVO.getLocale().getLanguage());
                            p_c2sTransferVO.setCountry(p_c2sTransferVO.getLocale().getCountry());
                        }
                        if (_log.isDebugEnabled()) {
                            _log.debug("validateC2SBillPmtRequest", "sender locale: =" + p_requestVO.getSenderLocale());
                        }

                        if (BTSLUtil.isNullString(p_requestArr[5])) {
                            p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                        } else {
                            final int langCode = PretupsBL.getLocaleValueFromCode(p_requestVO, p_requestArr[5]);
                            if (LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)) == null) {
                                throw new BTSLBaseException(this, "validateC2SBillPmtRequest", PretupsErrorCodesI.BILLPAY_INVALID_PAYEE_NOT_LANG);
                            }
                            p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
                        }
                        break;
                    }
                default:
                    throw new BTSLBaseException(this, "validateC2SBillPmtRequest", PretupsErrorCodesI.BILLPAY_INVALID_MESSAGE_FORMAT, 0, new String[] { p_requestVO
                        .getActualMessageFormat() }, null);
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("validateC2SBillPmtRequest", "  Exception while validating user message :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorUtil[validateC2SBillPmtRequest]", "", "", "",
                "Exception while validating user message" + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException(this, "validateC2SBillPmtRequest", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("validateC2SBillPmtRequest", "Exiting ");
        }

    }

    /**
     * @author vinay.kumar
     * @param p_RequestedAmount
     *            long
     * @param p_BonusTalkTime
     *            long
     * @return rechargeComment String
     */
    public String getRechargeComment(long p_RequestedAmount, long p_BonusTalkTime) {
        String rechargeComment = "";
        final String METHOD_NAME = "getRechargeComment";
        final int rem = (int) (p_BonusTalkTime / p_RequestedAmount);
        try {
            switch (rem) {
                case 0:
                    rechargeComment = "NORMAL";
                    break;
                case 1:
                    rechargeComment = "DOUBLE";
                    break;
                case 2:
                    rechargeComment = "TRIPPLE";
                    break;
                case 3:
                    rechargeComment = "QUADRUPLE";
                    break;
                case 4:
                    rechargeComment = "PENTUPLE";
                    break;
                case 5:
                    rechargeComment = "HEXTUPLE";
                    break;
                case 6:
                    rechargeComment = "SEPTUPLE";
                    break;
                case 7:
                    rechargeComment = "OCTUPLE";
                    break;
                case 8:
                    rechargeComment = "NONUPLE";
                    break;
                case 9:
                    rechargeComment = "DECUPLE";
                    break;
                default:
                    rechargeComment = "NORMAL";
                    break;
            }
        } catch (ArithmeticException e) {
            rechargeComment = "NORMAL";
            _log.errorTrace(METHOD_NAME, e);
        }
        return rechargeComment;
    }

    /**
     * 
     * @param p_con
     * @param p_c2sTransferVO
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public void validateSIMACTRequest(Connection p_con, C2STransferVO p_c2sTransferVO, RequestVO p_requestVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("validateSIMACTRequest", "Entered");
        }
        final String METHOD_NAME = "validateSIMACTRequest";
        try {
            final String[] p_requestArr = p_requestVO.getRequestMessageArray();
            String custMsisdn = null;
            String cellId = null;
            String switchId = null;
            final ChannelUserVO channelUserVO = (ChannelUserVO) p_c2sTransferVO.getSenderVO();
            final int messageLen = p_requestArr.length;
            if (_log.isDebugEnabled()) {
                _log.debug("validateSIMACTRequest", "messageLen: " + messageLen);
            }
            for (int i = 0; i < messageLen; i++) {
                if (_log.isDebugEnabled()) {
                    _log.debug("validateSIMACTRequest", "i: " + i + " value: " + p_requestArr[i]);
                }
            }
            switch (messageLen) {
                case 9:
                    {
                        if (!(SystemPreferences.USSD_NEW_TAGS_MANDATORY) && !(PretupsI.GATEWAY_TYPE_USSD.equals(p_c2sTransferVO.getRequestGatewayType())))// cellid
                        // and
                        // switch
                        // id
                        // not
                        // mandatory
                        // in
                        // request
                        {
                            if ((((ChannelUserVO) p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES)) {
                                try {
                                    ChannelUserBL.validatePIN(p_con, channelUserVO, p_requestArr[8]);
                                } catch (BTSLBaseException be) {
                                    if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
                                        .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                                        p_con.commit();
                                    }
                                    throw be;
                                }
                            }
                            final ReceiverVO receiverVO = new ReceiverVO();
                            custMsisdn = p_requestArr[1];
                            PretupsBL.validateMsisdn(p_con, receiverVO, p_c2sTransferVO.getRequestID(), custMsisdn);
                            p_c2sTransferVO.setReceiverVO(receiverVO);
                            break;
                        } else// cellid and switch id mandatory in request
                        {
                            // Do the 000 check Default PIN
                            if ((((ChannelUserVO) p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES)) {
                                try {
                                    ChannelUserBL.validatePIN(p_con, channelUserVO, p_requestArr[8]);
                                } catch (BTSLBaseException be) {
                                    if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
                                        .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                                        p_con.commit();
                                    }
                                    throw be;
                                }
                            }
                            final ReceiverVO receiverVO = new ReceiverVO();
                            // Customer MSISDN Validation
                            custMsisdn = p_requestArr[1];
                            PretupsBL.validateMsisdn(p_con, receiverVO, p_c2sTransferVO.getRequestID(), custMsisdn);
                            p_c2sTransferVO.setReceiverVO(receiverVO);
                            cellId = p_requestArr[6];
                            switchId = p_requestArr[7];
                            // validations to be done on cellId switchId to be
                            // put
                            // here
                            p_requestVO.setCellId(cellId);
                            p_requestVO.setSwitchId(switchId);
                            break;
                        }
                    }
                case 11:
                    {
                        if (!(SystemPreferences.USSD_NEW_TAGS_MANDATORY) && !(PretupsI.GATEWAY_TYPE_USSD.equals(p_c2sTransferVO.getRequestGatewayType())))// cellid
                        // and
                        // switch
                        // id
                        // not
                        // mandatory
                        // in
                        // request
                        {
                            if ((((ChannelUserVO) p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES)) {
                                try {
                                    ChannelUserBL.validatePIN(p_con, channelUserVO, p_requestArr[10]);
                                } catch (BTSLBaseException be) {
                                    if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
                                        .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                                        p_con.commit();
                                    }
                                    throw be;
                                }
                            }
                            final ReceiverVO receiverVO = new ReceiverVO();
                            custMsisdn = p_requestArr[1];
                            PretupsBL.validateMsisdn(p_con, receiverVO, p_c2sTransferVO.getRequestID(), custMsisdn);
                            p_c2sTransferVO.setReceiverVO(receiverVO);
                            break;
                        } else// cellid and switch id mandatory in request
                        {
                            // Do the 000 check Default PIN
                            if ((((ChannelUserVO) p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES)) {
                                try {
                                    ChannelUserBL.validatePIN(p_con, channelUserVO, p_requestArr[10]);
                                } catch (BTSLBaseException be) {
                                    if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
                                        .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                                        p_con.commit();
                                    }
                                    throw be;
                                }
                            }
                            final ReceiverVO receiverVO = new ReceiverVO();
                            // Customer MSISDN Validation
                            custMsisdn = p_requestArr[1];
                            PretupsBL.validateMsisdn(p_con, receiverVO, p_c2sTransferVO.getRequestID(), custMsisdn);
                            p_c2sTransferVO.setReceiverVO(receiverVO);
                            // =======cellID switchID=============
                            cellId = p_requestArr[8];
                            switchId = p_requestArr[9];
                            // validations to be done on cellId switchId to be
                            // put
                            // here
                            p_requestVO.setCellId(cellId);
                            p_requestVO.setSwitchId(switchId);
                            // =======cellID switchID=============
                            break;
                        }
                    }
                default:
                    throw new BTSLBaseException(this, "validateSIMACTRequest", PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT, 0, new String[] { p_requestVO
                        .getActualMessageFormat() }, null);
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("validateSIMACTRequest", "  Exception while validating user message :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateSIMACTRequest]", "", "", "",
                "Exception while validating user message" + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException(this, "validateSIMACTRequest", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("validateSIMACTRequest", "Exiting ");
        }
    }
}
