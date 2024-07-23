/**
 * @(#)OciUtil.java
 *                  Copyright(c) 2007, Bharti Telesoft Ltd.
 *                  All Rights Reserved
 * 
 *                  <description>
 *                  ------------------------------------------------------------
 *                  -------------------------------------
 *                  Author Date History
 *                  ------------------------------------------------------------
 *                  -------------------------------------
 *                  Ved Prakash July 17, 2007 Initital Creation
 *                  ------------------------------------------------------------
 *                  -------------------------------------
 * 
 */

package com.client.pretups.util.clientutils;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
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
import com.btsl.pretups.cardgroup.businesslogic.BonusBundleCache;
import com.btsl.pretups.cardgroup.businesslogic.BonusBundleDetailVO;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDetailsVO;
import com.btsl.pretups.channel.transfer.businesslogic.BonusTransferVO;
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
import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class OciUtil extends OperatorUtil {
    private Log _log = LogFactory.getLog(this.getClass().getName());

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
                        .getCreatedOn());
            }

            // added for OCI changes regarding to change PIN on 1st request
            if (userPhoneVO.isForcePinCheckReqd() && (userPhoneVO.getPinModifiedOn().getTime()) == (userPhoneVO.getCreatedOn().getTime())) {
                throw new BTSLBaseException("OperatorUtil", "validatePIN", PretupsErrorCodesI.CHNL_FIRST_REQUEST_PIN_CHANGE);
            }

            final int daysAfterChngPn = ((Integer) PreferenceCache.getControlPreference(PreferenceI.C2S_DAYS_AFTER_CHANGE_PIN, p_channelUserVO.getNetworkID(), p_channelUserVO
                .getCategoryCode())).intValue();
            if (userPhoneVO.isForcePinCheckReqd() && userPhoneVO.getPinModifiedOn() != null && ((userPhoneVO.getModifiedOn().getTime() - userPhoneVO.getPinModifiedOn()
                .getTime()) / (24 * 60 * 60 * 1000)) > daysAfterChngPn) {
                // Force the user to change PIN if he has not changed the same
                // in the defined no of days
                if (_log.isDebugEnabled()) {
                    _log.debug("validatePIN",
                        "Modified Time=:" + userPhoneVO.getModifiedOn() + " userPhoneVO.getPinModifiedOn()=" + userPhoneVO.getPinModifiedOn() + " Difference=" + ((userPhoneVO
                            .getModifiedOn().getTime() - userPhoneVO.getPinModifiedOn().getTime()) / (24 * 60 * 60 * 1000)));
                }
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "OperatorUtil[validatePIN]", "", userPhoneVO
                    .getMsisdn(), "", "Force User to change PIN after " + daysAfterChngPn + " days as last changed on " + userPhoneVO.getPinModifiedOn());
                final String strArr[] = { String.valueOf(daysAfterChngPn) };
                throw new BTSLBaseException("OperatorUtil", "validatePIN", PretupsErrorCodesI.CHNL_ERROR_SNDR_FORCE_CHANGEPIN, 0, strArr, null);
            } else {
                final String decryptedPin = BTSLUtil.decryptText(userPhoneVO.getSmsPin());
                if (_log.isDebugEnabled()) {
                    _log.debug("validatePIN", "Sender MSISDN:" + userPhoneVO.getMsisdn() + " decrypted PIN of database=" + decryptedPin + " p_requestPin =" + p_requestPin);
                }

                // added for Moldova Change the default PIN
                if (userPhoneVO.isForcePinCheckReqd() && ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_SMSPIN)).equals(decryptedPin)) {
                    throw new BTSLBaseException("OciUtil", "validatePIN", PretupsErrorCodesI.CHNLUSR_CHANGE_DEFAULT_PIN);
                }

                /*
                 * change done by ashishT
                 * comparing the hashvalue of password set in userphonevo to the
                 * hashvalue in pin sent by user.
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
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorUtil[validatePIN]", "",
                                userPhoneVO.getMsisdn(), "", "Not able to update invalid PIN count for users");
                            throw new BTSLBaseException("OperatorUtil", "validatePIN", PretupsErrorCodesI.ERROR_EXCEPTION);
                        }
                    }
                }
                if (increaseInvalidPinCount) {
                    updateStatus = new ChannelUserDAO().updateSmsPinCounter(p_con, userPhoneVO);
                    if (updateStatus > 0 && !isUserBarred) {
                        throw new BTSLBaseException("OperatorUtil", "validatePIN", PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN);
                    } else if (updateStatus > 0 && isUserBarred) {
                        throw new BTSLBaseException("OperatorUtil", "validatePIN", PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK);
                    } else {
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorUtil[validatePIN]", "",
                            userPhoneVO.getMsisdn(), "", "Not able to update invalid PIN count for users");
                        throw new BTSLBaseException("OperatorUtil", "validatePIN", PretupsErrorCodesI.ERROR_EXCEPTION);
                    }
                }
            }

        } catch (BTSLBaseException bex) {
            throw bex;
        } catch (Exception e) {
            _log.error("validatePIN", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorUtil[validatePIN]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("OperatorUtil", "validatePIN", PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("validatePIN", "Exiting with increase invalid Pin Count flag=" + increaseInvalidPinCount);
            }
        }
    }

    /**
     * Method validatePassword.
     * 
     * @author sanjeew.kumar
     * @created on 12/07/07
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
     * Date : Jul 23, 2007
     * Discription :
     * Method : validateTransactionPassword
     * 
     * @param p_channelUserVO
     * @param p_password
     * @throws BTSLBaseException
     * @return void
     * @author ved.sharma
     */
    public boolean validateTransactionPassword(ChannelUserVO p_channelUserVO, String p_password) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("validateTransactionPassword", " Entered p_channelUserVO=:" + p_channelUserVO + " p_password=" + p_password);
        }
        final String METHOD_NAME = "validateTransactionPassword";
        boolean passwordValidation = true;
        try {
            if (p_channelUserVO != null) {
                /*
                 * change done by ashishT
                 * comparing the password hashvalue with the password sent by
                 * user.
                 */
                // if(!BTSLUtil.isNullString(p_channelUserVO.getPassword()) &&
                // !BTSLUtil.decryptText(p_channelUserVO.getPassword()).equals(p_password))

                //
                if ("SHA".equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE))) {
                    boolean checkpassword;
                    if (p_password.length() > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_LOGIN_PWD_LENGTH))).intValue()) {
                        checkpassword = BTSLUtil.decryptText(p_channelUserVO.getPassword()).equals(p_password);
                    } else {
                        checkpassword = (!PretupsI.FALSE.equalsIgnoreCase(BTSLUtil.compareHash2String(p_channelUserVO.getPassword(), p_password)));
                    }

                    if (!BTSLUtil.isNullString(p_channelUserVO.getPassword()) && (!checkpassword)) {
                        passwordValidation = false;
                    }
                } else {
                    if (!BTSLUtil.isNullString(p_channelUserVO.getPassword()) && !BTSLUtil.decryptText(p_channelUserVO.getPassword()).equals(p_password)) {
                        passwordValidation = false;
                    }
                }
                if (!BTSLUtil.isNullString(p_password) && ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_PASSWORD)).equals(p_password)) {
                    throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.XML_ERROR_CHANGE_DEFAULT_PASSWD);
                }
            } else {
                throw new BTSLBaseException("OciUtil", "validateTransactionPassword", PretupsErrorCodesI.XML_ERROR_NO_SUCH_USER);
            }

        } catch (BTSLBaseException bex) {
            throw bex;
        } catch (Exception e) {
            _log.error("validateTransactionPassword", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OciUtil[validateTransactionPassword]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("OciUtil", "validateTransactionPassword", PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("validateTransactionPassword", " Exiting passwordValidation=" + passwordValidation);
            }
        }
        return passwordValidation;
    }

    /**
     * Method that will validate the user message sent
     * 
     * @param p_con
     * @param p_c2sTransferVO
     * @param p_requestVO
     * @throws BTSLBaseException
     * @see com.btsl.pretups.util.OperatorUtilI#validateC2SRechargeRequest(Connection,
     *      C2STransferVO, RequestVO)
     */
    public void validateC2SRechargeRequest(Connection p_con, C2STransferVO p_c2sTransferVO, RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "validateC2SRechargeRequest";
        try {
            final String[] p_requestArr = p_requestVO.getRequestMessageArray();
            String custMsisdn = null;
            // String [] strArr=null;
            // double requestAmt=0;
            String requestAmtStr = null;
            final ChannelUserVO channelUserVO = (ChannelUserVO) p_c2sTransferVO.getSenderVO();
            UserPhoneVO userPhoneVO = null;
            if (!channelUserVO.isStaffUser()) {
                userPhoneVO = (UserPhoneVO) channelUserVO.getUserPhoneVO();
            } else {
                userPhoneVO = (UserPhoneVO) channelUserVO.getStaffUserDetails().getUserPhoneVO();
            }

            final int messageLen = p_requestArr.length;
            if (_log.isDebugEnabled()) {
                _log.debug("validateC2SRechargeRequest", "messageLen: " + messageLen);
            }
            for (int i = 0; i < messageLen; i++) {
                if (_log.isDebugEnabled()) {
                    _log.debug("validateC2SRechargeRequest", "i: " + i + " value: " + p_requestArr[i]);
                }
            }
            switch (messageLen) {
                case 4:
                    {
                        // Do the 000 check Default PIN
                        // if((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES)
                        // &&
                        // !PretupsI.DEFAULT_C2S_PIN.equals(BTSLUtil.decryptText((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getSmsPin())))
                        if (userPhoneVO.getPinRequired().equals(PretupsI.YES)) {
                            try {
                                ChannelUserBL.validatePIN(p_con, channelUserVO, p_requestArr[3]);
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

                        // Recharge amount Validation
                        requestAmtStr = p_requestArr[2];
                        PretupsBL.validateAmount(p_c2sTransferVO, requestAmtStr);
                        p_c2sTransferVO.setReceiverVO(receiverVO);
                        // p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE));
                        // Changed on 27/05/07 for Service Type selector Mapping
                        final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO
                            .getServiceType());
                        if (serviceSelectorMappingVO != null) {
                            p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                        }
                        p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                        break;
                    }

                case 5:
                    {
                        // Do the 000 check Default PIN
                        // if((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES)
                        // &&
                        // !PretupsI.DEFAULT_C2S_PIN.equals(BTSLUtil.decryptText((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getSmsPin())))
                        if (userPhoneVO.getPinRequired().equals(PretupsI.YES)) {
                            try {
                                ChannelUserBL.validatePIN(p_con, channelUserVO, p_requestArr[4]);
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

                        // Recharge amount Validation
                        requestAmtStr = p_requestArr[2];
                        PretupsBL.validateAmount(p_c2sTransferVO, requestAmtStr);
                        p_c2sTransferVO.setReceiverVO(receiverVO);
                        if (BTSLUtil.isNullString(p_requestArr[3])) {
                            p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                        } else {
                            final int langCode = PretupsBL.getLocaleValueFromCode(p_requestVO, p_requestArr[3]);
                            if (LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)) == null) {
                                throw new BTSLBaseException(this, "validateC2SRechargeRequest", PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
                            }
                            p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
                        }
                        // p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE));
                        // Changed on 27/05/07 for Service Type selector Mapping
                        final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO
                            .getServiceType());
                        if (serviceSelectorMappingVO != null) {
                            p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                        }
                        break;
                    }

                case 6:
                    {
                        // if((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES)
                        // &&
                        // !PretupsI.DEFAULT_C2S_PIN.equals(BTSLUtil.decryptText((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getSmsPin())))
                        if (userPhoneVO.getPinRequired().equals(PretupsI.YES)) {
                            try {
                                ChannelUserBL.validatePIN(p_con, channelUserVO, p_requestArr[5]);
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

                        // Recharge amount Validation
                        requestAmtStr = p_requestArr[2];
                        PretupsBL.validateAmount(p_c2sTransferVO, requestAmtStr);
                        p_c2sTransferVO.setReceiverVO(receiverVO);
                        if (BTSLUtil.isNullString(p_requestArr[3])) {
                            if ("en".equalsIgnoreCase(p_requestVO.getLocale().getLanguage())) {
                                // p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE));
                                // Changed on 27/05/07 for Service Type selector
                                // Mapping
                                final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO
                                    .getServiceType());
                                if (serviceSelectorMappingVO != null) {
                                    p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                                }
                            }
                            // changed for CRE_INT_CR00029 by ankit Zindal
                            // in case of binary message we will set default
                            // value
                            // after
                            // calling getselectorvaluefromcode method
                            /*
                             * else
                             * p_requestVO.setReqSelector((Constants.getProperty(
                             * "CVG_UNICODE_"
                             * +p_requestVO.getLocale().getLanguage().toUpperCase
                             * ()))
                             * );
                             */} else {
                            p_requestVO.setReqSelector(p_requestArr[3]);
                        }

                        // PretupsBL.getSelectorValueFromCode(p_requestVO);
                        // changed for CRE_INT_CR00029 by ankit Zindal
                        if (BTSLUtil.isNullString(p_requestVO.getReqSelector())) {
                            // p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE));
                            // Changed on 27/05/07 for Service Type selector
                            // Mapping
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
                                throw new BTSLBaseException(this, "validateC2SRechargeRequest", PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
                            }
                            p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
                        }
                        break;
                    }
                case 7:
                    {
                        // if((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES)
                        // &&
                        // !PretupsI.DEFAULT_C2S_PIN.equals(BTSLUtil.decryptText((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getSmsPin())))
                        if (userPhoneVO.getPinRequired().equals(PretupsI.YES)) {
                            try {
                                ChannelUserBL.validatePIN(p_con, channelUserVO, p_requestArr[6]);
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

                        // Recharge amount Validation
                        requestAmtStr = p_requestArr[2];
                        PretupsBL.validateAmount(p_c2sTransferVO, requestAmtStr);
                        p_c2sTransferVO.setReceiverVO(receiverVO);
                        if (BTSLUtil.isNullString(p_requestArr[3])) {
                            if ("en".equalsIgnoreCase(p_requestVO.getLocale().getLanguage())) {
                                // p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE));
                                // Changed on 27/05/07 for Service Type selector
                                // Mapping
                                final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO
                                    .getServiceType());
                                if (serviceSelectorMappingVO != null) {
                                    p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                                }
                            }
                            /*
                             * else
                             * p_requestVO.setReqSelector((Constants.getProperty(
                             * "CVG_UNICODE_"
                             * +p_requestVO.getLocale().getLanguage().toUpperCase
                             * ()))
                             * );
                             */} else {
                            p_requestVO.setReqSelector(p_requestArr[3]);
                        }

                        // PretupsBL.getSelectorValueFromCode(p_requestVO);
                        // changed for CRE_INT_CR00029 by ankit Zindal
                        if (BTSLUtil.isNullString(p_requestVO.getReqSelector())) {
                            // p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE));
                            // Changed on 27/05/07 for Service Type selector
                            // Mapping
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
                            /*
                             * if(LocaleMasterCache.getLocaleFromCodeDetails(String
                             * .
                             * valueOf
                             * (langCode))==null)
                             * throw new
                             * BTSLBaseException(this,"validateC2SRechargeRequest"
                             * ,PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
                             */p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
                            // ChangeID=LOCALEMASTER
                            // Sender locale has to be overwritten in transferVO
                            // also.
                            p_c2sTransferVO.setLocale(p_requestVO.getSenderLocale());
                            p_c2sTransferVO.setLanguage(p_c2sTransferVO.getLocale().getLanguage());
                            p_c2sTransferVO.setCountry(p_c2sTransferVO.getLocale().getCountry());
                        }
                        if (_log.isDebugEnabled()) {
                            _log.debug(this, "sender locale: =" + p_requestVO.getSenderLocale());
                        }

                        if (BTSLUtil.isNullString(p_requestArr[5])) {
                            p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                        } else {
                            final int langCode = PretupsBL.getLocaleValueFromCode(p_requestVO, p_requestArr[5]);
                            if (LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)) == null) {
                                throw new BTSLBaseException(this, "validateC2SRechargeRequest", PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
                            }
                            p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
                        }
                        break;
                    }
                default:
                    throw new BTSLBaseException(this, "validateC2SRechargeRequest", PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT, 0, new String[] { p_requestVO
                        .getActualMessageFormat() }, null);
            }

            /*
             * if(p_requestArr.length <5)
             * throw new BTSLBaseException(this,"validateC2SRechargeRequest",
             * PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT);
             * 
             * //Do the 000 check Default PIN
             * if((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO(
             * )).getPinRequired().equals(PretupsI.YES) &&
             * !PretupsI.DEFAULT_C2S_PIN
             * .equals(BTSLUtil.decryptText((((ChannelUserVO
             * )p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getSmsPin())))
             * ChannelUserBL.validatePIN(p_con,channelUserVO.getUserPhoneVO(),
             * p_requestArr[4]);
             * 
             * ReceiverVO receiverVO=new ReceiverVO();
             * //Customer MSISDN Validation
             * custMsisdn=p_requestArr[1];
             * 
             * validateMsisdn(receiverVO,p_c2sTransferVO.getRequestID(),custMsisdn
             * );
             * 
             * //Recharge amount Validation
             * requestAmtStr=p_requestArr[2];
             * validateAmount(p_c2sTransferVO,requestAmtStr);
             * p_c2sTransferVO.setReceiverVO(receiverVO);
             */
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("validateC2SRechargeRequest", "  Exception while validating user message :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateC2SRechargeRequest]", "", "", "",
                "Exception while validating user message" + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException(this, "validateC2SRechargeRequest", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("validateC2SRechargeRequest", "Exiting ");
        }
    }

    public void setCalculatedCardGroupValues(String p_subService, CardGroupDetailsVO p_cardGroupDetailVO, TransferVO p_transferVO) {

        try {
            /**
             * In case of CVG all values are set as calculated.
             * In case of VG transfer value is set to 0.
             * In case of C, validity and grace will be set to 0.
             * 
             */
            TransferItemVO transferItemVO = null;
            final int bonusValidityValue = Integer.parseInt(String.valueOf(p_cardGroupDetailVO.getBonusValidityValue()));
            final int validityPeriodValue = p_cardGroupDetailVO.getValidityPeriod();
            final long transferValue = p_cardGroupDetailVO.getTransferValue();
            final long bonusValue = p_cardGroupDetailVO.getBonusTalkTimeValue();
            transferItemVO = (TransferItemVO) p_transferVO.getTransferItemList().get(1);

            // This feature is specific to the operator
            // if operator wants, amount is needed to be deducted for Get number
            // back service
            // so transfer the value to user after amount deducted for number
            // back feature
            // so net transfer value is
            // transferValue=transferValue-amountDeducted
            // and accessFee is normalAccessFee+amountDeducted

            /*
             * int amountDeducted;
             * if(transferItemVO.isNumberBackAllowed())
             * {
             * amountDeducted= transferItemVO.getAmountDeducted();
             * transferValue=transferValue-amountDeducted;
             * if(!(transferValue>0))
             * throw new BTSLBaseException(this,"setCalculatedCardGroupValues",
             * PretupsErrorCodesI.TRANSFER_VALUE_IS_NOT_VALID);
             * p_cardGroupDetailVO.setTransferValue(transferValue);
             * p_transferVO.setReceiverAccessFee(p_transferVO.getReceiverAccessFee
             * () + amountDeducted);
             * }
             * }
             */

            p_transferVO.setReceiverBonusValidity(bonusValidityValue);
            p_transferVO.setReceiverGracePeriod(p_cardGroupDetailVO.getGracePeriod());
            p_transferVO.setReceiverValidity(validityPeriodValue);
            // Is Bonus Validity on Requested Value ??
            calculateValidity(p_transferVO, transferItemVO.getTransferDateTime(), transferItemVO.getPreviousExpiry(), p_cardGroupDetailVO.getValidityPeriodType(),
                validityPeriodValue, bonusValidityValue);
            p_transferVO.setReceiverTransferValue(transferValue);
            transferItemVO.setTransferValue(transferValue);
            transferItemVO.setGraceDaysStr(String.valueOf(p_cardGroupDetailVO.getGracePeriod()));
            transferItemVO.setValidity(validityPeriodValue);
            p_transferVO.setReceiverBonusValue(bonusValue);

            if ((String.valueOf(PretupsI.CHNL_SELECTOR_C_VALUE)).equals(p_subService) && "P2P".equals(p_transferVO.getModule()))// C
            {
                p_transferVO.setReceiverBonusValidity(0);
                p_transferVO.setReceiverGracePeriod(0);
                p_transferVO.setReceiverValidity(0);
                p_transferVO.setReceiverTransferValue(transferValue);
                transferItemVO.setTransferValue(transferValue);
                transferItemVO.setGraceDaysStr("0");
                transferItemVO.setValidity(0);
                p_transferVO.setReceiverBonusValue(bonusValue);
            }
            if ((String.valueOf(PretupsI.CHNL_SELECTOR_VG_VALUE)).equals(p_subService) && "P2P".equals(p_transferVO.getModule()))// VG
            {
                p_transferVO.setReceiverBonusValidity(bonusValidityValue);
                p_transferVO.setReceiverGracePeriod(p_cardGroupDetailVO.getGracePeriod());
                p_transferVO.setReceiverValidity(validityPeriodValue);
                // Is Bonus Validity on Requested Value ??
                calculateValidity(p_transferVO, transferItemVO.getTransferDateTime(), transferItemVO.getPreviousExpiry(), p_cardGroupDetailVO.getValidityPeriodType(),
                    validityPeriodValue, bonusValidityValue);
                p_transferVO.setReceiverTransferValue(0);
                transferItemVO.setTransferValue(0);
                transferItemVO.setGraceDaysStr(String.valueOf(p_cardGroupDetailVO.getGracePeriod()));
                transferItemVO.setValidity(validityPeriodValue);
                p_transferVO.setReceiverBonusValue(0);
            }

        } catch (Exception e) {
        	_log.error("credit", e.getMessage());
        }
    }

    public void validateCardGroupDetails(String p_startRange, String p_endRange, String p_subService) throws Exception {
        try {
            // This implementation is comented because not needed at this time.
            // Will be implemented for any operator.
            if (p_subService.split(":")[1].equals((String.valueOf(PretupsI.CHNL_SELECTOR_VG_VALUE)))) {
                // if(Double.parseDouble(p_startRange)!=Double.parseDouble(
                // p_endRange))
                // throw new
                // BTSLBaseException(this,"validateCardGroupDetails","cardgroup.addc2scardgroup.error.invalidstartandendrange");

            }
        } catch (Exception e) {
            throw e;
        }
    }

    public void populateBonusListAfterValidation(HashMap p_map, C2STransferVO p_c2stransferVO) {
        HashMap map = null;
        final String METHOD_NAME = "populateBonusListAfterValidation";
        try {
            if (_log.isDebugEnabled()) {
                _log.debug("populateBonusListAfterValidation", "_transferID: " + p_c2stransferVO.getTransferID() + " Entered");
            }
            map = new HashMap();
            BonusTransferVO bonusTransferVO = null;
            String accountIds[] = null;
            String accountCodes[] = null;
            String previousBalance[] = null;
            String previousValidity[] = null;
            String previousGrace[] = null;
            String tempString = null;
            final String splitChar = "%2C";

            tempString = (String) p_map.get("IN_RESP_BUNDLE_IDS");
            if (!BTSLUtil.isNullString(tempString) && tempString.length() > 0) {
                accountIds = tempString.split(splitChar);
            }

            tempString = (String) p_map.get("IN_RESP_BUNDLE_CODES");
            if (!BTSLUtil.isNullString(tempString) && tempString.length() > 0) {
                accountCodes = tempString.split(splitChar);
            }

            tempString = (String) p_map.get("IN_RESP_BUNDLE_PREV_BALS");
            if (!BTSLUtil.isNullString(tempString) && tempString.length() > 0) {
                previousBalance = tempString.split(splitChar);
            }

            tempString = (String) p_map.get("IN_RESP_BUNDLE_PREV_VALIDITY");
            if (!BTSLUtil.isNullString(tempString) && tempString.length() > 0) {
                previousValidity = tempString.split(splitChar);
            }

            tempString = (String) p_map.get("IN_RESP_BUNDLE_PREV_GRACE");
            if (!BTSLUtil.isNullString(tempString) && tempString.length() > 0) {
                previousGrace = tempString.split(splitChar);
            }

            if (!BTSLUtil.isNullArray(accountCodes) && accountCodes.length > 0) {
                for (int i = 0, size = accountCodes.length; i < size; i++) {
                    bonusTransferVO = new BonusTransferVO();
                    if (!BTSLUtil.isNullArray(accountIds) && !BTSLUtil.isNullString(accountIds[i]) && accountIds[i].length() > 0) {
                        bonusTransferVO.setAccountId(accountIds[i]);
                    }
                    if (!BTSLUtil.isNullArray(accountCodes) && !BTSLUtil.isNullString(accountCodes[i]) && accountCodes[i].length() > 0) {
                        bonusTransferVO.setAccountCode(accountCodes[i]);
                    }
                    if (!BTSLUtil.isNullArray(previousBalance) && !BTSLUtil.isNullString(previousBalance[i]) && previousBalance[i].length() > 0) {
                        bonusTransferVO.setPreviousBalance(Double.parseDouble(previousBalance[i]));
                    }
                    try {
                        if (!BTSLUtil.isNullArray(previousValidity) && !BTSLUtil.isNullString(previousValidity[i]) && previousValidity[i].length() > 0) {
                            bonusTransferVO.setPreviousValidity(BTSLUtil.getDateFromDateString(previousValidity[i], PretupsI.DATE_FORMAT_DDMMYYYY));
                        }
                    } catch (Exception e) {
                        _log.errorTrace(METHOD_NAME, e);
                        bonusTransferVO.setPreviousValidity(null);
                    }
                    try {
                        if (!BTSLUtil.isNullArray(previousGrace) && !BTSLUtil.isNullString(previousGrace[i]) && previousGrace[i].length() > 0) {
                            bonusTransferVO.setPreviousGrace(BTSLUtil.getDateFromDateString(previousGrace[i], PretupsI.DATE_FORMAT_DDMMYYYY));
                        }
                    } catch (Exception e) {
                        bonusTransferVO.setPreviousGrace(null);
                        _log.errorTrace(METHOD_NAME, e);
                    }
                    bonusTransferVO.setCreatedOn(p_c2stransferVO.getTransferDate());
                    bonusTransferVO.setTransferId(p_c2stransferVO.getTransferID());
                    map.put(accountCodes[i], bonusTransferVO);
                }
            }
            p_c2stransferVO.setBonusItems(map);
        } catch (Exception e) {
            _log.error("populateBonusListAfterValidation", "Exception: " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("populateBonusListAfterValidation", "_transferID: " + p_c2stransferVO.getTransferID() + "bonus map afer validation :" + map + " Exited");
            }
        }
    }

    public void updateBonusListAfterTopup(HashMap p_map, C2STransferVO p_c2stransferVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("updateBonusListAfterTopup", "_transferID: " + p_c2stransferVO.getTransferID() + " Entered");
        }
        final String METHOD_NAME = "updateBonusListAfterTopup";
        ArrayList bonusList = null;
        try {
            bonusList = new ArrayList();
            HashMap bonusMasterMap = null;

            final String combined = (String) p_map.get("COMBINED_RECHARGE");
            final String implicit = (String) p_map.get("IMPLICIT_RECHARGE");
            if (PretupsI.YES.equals(implicit) || PretupsI.YES.equals(combined)) {
                p_c2stransferVO.setBonusItems(bonusList);
                return;
            }
            BonusTransferVO bonusTransferVO = null;
            String recAccountCodes[] = null;
            String accountNames[] = null;
            String accountTypes[] = null;
            String accountRates[] = null;
            String balance[] = null;
            String validity[] = null;
            String grace[] = null;
            String postBalance[] = null;
            String postValidity[] = null;
            String postGrace[] = null;
            String tempString = null;
            final String splitChar = "%7C";
            String accountIds[] = null;
            boolean isReceived = false;
            BonusBundleDetailVO bundleDetailVO = null;
            String sendAccountCodes[] = null;
            HashMap map = null;

            // Only those account details will be added in database that are
            // areceived in credit response
            // If credit response is not obtained, acount send to IN for credit
            // will be considered

            tempString = (String) p_map.get("IN_RESP_BUNDLE_CODES_CR");
            if (!BTSLUtil.isNullString(tempString) && tempString.length() > 0) {
                recAccountCodes = tempString.split(splitChar);
            }

            sendAccountCodes = (p_c2stransferVO.getBonusBundleCode()).split("\\|");

            tempString = (String) p_map.get("BONUS_BUNDLE_IDS");
            if (!BTSLUtil.isNullString(tempString) && tempString.length() > 0) {
                accountIds = tempString.split(splitChar);
            }

            tempString = (String) p_map.get("BONUS_BUNDLE_NAMES");
            if (!BTSLUtil.isNullString(tempString) && tempString.length() > 0) {
                accountNames = tempString.split(splitChar);
            }

            tempString = (String) p_map.get("BONUS_BUNDLE_TYPES");
            if (!BTSLUtil.isNullString(tempString) && tempString.length() > 0) {
                accountTypes = tempString.split(splitChar);
            }

            tempString = (String) p_map.get("BONUS_BUNDLE_RATES");
            if (!BTSLUtil.isNullString(tempString) && tempString.length() > 0) {
                accountRates = tempString.split(splitChar);
            }

            tempString = (String) p_map.get("BONUS_BUNDLE_VALUES");
            if (!BTSLUtil.isNullString(tempString) && tempString.length() > 0) {
                balance = tempString.split(splitChar);
            }

            tempString = (String) p_map.get("BONUS_BUNDLE_VALIDITIES");
            if (!BTSLUtil.isNullString(tempString) && tempString.length() > 0) {
                validity = tempString.split(splitChar);
            }

            tempString = (String) p_map.get("BONUS_BUNDLE_GRACE");
            if (!BTSLUtil.isNullString(tempString) && tempString.length() > 0) {
                grace = tempString.split(splitChar);
            }

            tempString = (String) p_map.get("IN_RESP_BUNDLE_POST_BALS");
            if (!BTSLUtil.isNullString(tempString) && tempString.length() > 0) {
                postBalance = tempString.split(splitChar);
            }

            tempString = (String) p_map.get("IN_RESP_BUNDLE_POST_VALIDITY");
            if (!BTSLUtil.isNullString(tempString) && tempString.length() > 0) {
                postValidity = tempString.split(splitChar);
            }

            tempString = (String) p_map.get("IN_RESP_BUNDLE_POST_GRACE");
            if (!BTSLUtil.isNullString(tempString) && tempString.length() > 0) {
                postGrace = tempString.split(splitChar);
            }

            bonusMasterMap = BonusBundleCache.getBonusBundlesLinkedMap();
            final Set set = bonusMasterMap.entrySet();
            final Iterator itr = set.iterator();
            map = (HashMap) p_c2stransferVO.getBonusItems();

            // loop of iterator for all bundles
            // if the account code in current bundle VO is received, get values
            // from received
            // if the account code in current bundle VO is not received and not
            // selector, take values as zero
            // if the account code in current bundle VO is not received and is
            // selector, get values from c2s_transfer VO

            final ArrayList newCode = new ArrayList();
            ArrayList finalCode = new ArrayList();

            if (BTSLUtil.isNullArray(recAccountCodes) || recAccountCodes.length == 0) {
                recAccountCodes = sendAccountCodes;
            } else {

                for (int i = 0, size = sendAccountCodes.length; i < size; i++) {
                    if (!Arrays.asList(recAccountCodes).contains(sendAccountCodes[i])) {
                        newCode.add(sendAccountCodes[i]);
                    }
                }
                finalCode = (ArrayList) Arrays.asList(recAccountCodes);
                finalCode.addAll(newCode);
            }

            final int x1 = finalCode.size();
            if (x1 > 0) {
                recAccountCodes = new String[x1];
                for (int i = 0, size = finalCode.size(); i < size; i++) {
                    recAccountCodes[i] = (String) finalCode.get(i);
                }
            }

            while (itr.hasNext()) {
                isReceived = false;
                final Map.Entry valEntry = (Map.Entry) itr.next();
                bundleDetailVO = (BonusBundleDetailVO) valEntry.getValue();

                if (!BTSLUtil.isNullArray(recAccountCodes) && recAccountCodes.length > 0) {
                    for (int i = 0, size = recAccountCodes.length; i < size; i++) {
                        if (recAccountCodes[i].equalsIgnoreCase(bundleDetailVO.getBundleCode()) && (!p_c2stransferVO.getReceiverBundleID().equalsIgnoreCase(
                            bundleDetailVO.getBundleCode()))) {
                            isReceived = true;
                            break;
                        }
                    }
                }
                bonusTransferVO = (BonusTransferVO) map.get(bundleDetailVO.getBundleCode());
                if (bonusTransferVO == null) {
                    bonusTransferVO = new BonusTransferVO();
                }
                bonusTransferVO.setTransferId(p_c2stransferVO.getTransferID());
                if (isReceived) {
                    // loop for received account codes
                    for (int x = 0, arrSize = recAccountCodes.length; x < arrSize; x++) {
                        if (recAccountCodes[x].equalsIgnoreCase(bundleDetailVO.getBundleCode())) {
                            bonusTransferVO.setAccountCode(bundleDetailVO.getBundleCode());
                            if (!BTSLUtil.isNullArray(accountIds) && !BTSLUtil.isNullString(accountIds[x]) && accountIds[x].length() > 0) {
                                bonusTransferVO.setAccountId(accountIds[x]);
                            }
                            if (!BTSLUtil.isNullArray(accountNames) && !BTSLUtil.isNullString(accountNames[x]) && accountNames[x].length() > 0) {
                                bonusTransferVO.setAccountName(accountNames[x]);
                            }
                            if (!BTSLUtil.isNullArray(accountTypes) && !BTSLUtil.isNullString(accountTypes[x]) && accountTypes[x].length() > 0) {
                                bonusTransferVO.setAccountType(accountTypes[x]);
                            }
                            if (!BTSLUtil.isNullArray(accountRates) && !BTSLUtil.isNullString(accountRates[x]) && accountRates[x].length() > 0) {
                                bonusTransferVO.setAccountRate(Double.parseDouble(accountRates[x]));
                            }
                            if (!BTSLUtil.isNullArray(balance) && !BTSLUtil.isNullString(balance[x]) && balance[x].length() > 0) {
                                bonusTransferVO.setBalance(Double.parseDouble(balance[x]));
                            }
                            if (!BTSLUtil.isNullArray(validity) && !BTSLUtil.isNullString(validity[x]) && validity[x].length() > 0) {
                                bonusTransferVO.setValidity(Long.parseLong(validity[x]));
                            }
                            if (!BTSLUtil.isNullArray(grace) && !BTSLUtil.isNullString(grace[x]) && grace[x].length() > 0) {
                                bonusTransferVO.setGrace(Long.parseLong(grace[x]));
                            }
                            if (!BTSLUtil.isNullArray(postBalance) && !BTSLUtil.isNullString(postBalance[x]) && postBalance[x].length() > 0) {
                                bonusTransferVO.setPostBalance(Double.parseDouble(postBalance[x]));
                            }
                            try {
                                if (!BTSLUtil.isNullArray(postValidity) && !BTSLUtil.isNullString(postValidity[x]) && postValidity[x].length() > 0) {
                                    bonusTransferVO.setPostValidity(BTSLUtil.getDateFromDateString(postValidity[x], PretupsI.DATE_FORMAT_DDMMYYYY));
                                }
                            } catch (Exception e) {
                                bonusTransferVO.setPostValidity(null);
                                _log.errorTrace(METHOD_NAME, e);
                            }
                            try {
                                if (!BTSLUtil.isNullArray(postGrace) && !BTSLUtil.isNullString(postGrace[x]) && postGrace[x].length() > 0) {
                                    bonusTransferVO.setPostGrace(BTSLUtil.getDateFromDateString(postGrace[x], PretupsI.DATE_FORMAT_DDMMYYYY));
                                }
                            } catch (Exception e) {
                                bonusTransferVO.setPostGrace(null);
                                _log.errorTrace(METHOD_NAME, e);
                            }
                            bonusTransferVO.setCreatedOn(p_c2stransferVO.getTransferDate());
                            bonusList.add(bonusTransferVO);
                            break;
                        }// end if
                    }// end for
                } else if (!isReceived && !p_c2stransferVO.getReceiverBundleID().equalsIgnoreCase(bundleDetailVO.getBundleID())) {
                    bonusTransferVO.setAccountId(bundleDetailVO.getBundleID());
                    bonusTransferVO.setAccountCode(bundleDetailVO.getBundleCode());
                    bonusTransferVO.setAccountName(bundleDetailVO.getBundleName());
                    bonusTransferVO.setAccountType(bundleDetailVO.getBundleType());
                    bonusTransferVO.setAccountRate(1);
                    bonusTransferVO.setBalance(0);
                    bonusTransferVO.setValidity(0);
                    bonusTransferVO.setGrace(0);
                    bonusTransferVO.setPostBalance(0);
                    bonusTransferVO.setPostValidity(null);
                    bonusTransferVO.setPostGrace(null);

                    bonusTransferVO.setCreatedOn(p_c2stransferVO.getTransferDate());
                    bonusList.add(bonusTransferVO);
                } else if (!isReceived && p_c2stransferVO.getReceiverBundleID().equalsIgnoreCase(bundleDetailVO.getBundleID())) {
                    bonusTransferVO.setAccountId(bundleDetailVO.getBundleID());
                    bonusTransferVO.setAccountCode(bundleDetailVO.getBundleCode());
                    bonusTransferVO.setAccountName(bundleDetailVO.getBundleName());
                    bonusTransferVO.setAccountType(bundleDetailVO.getBundleType());
                    bonusTransferVO.setAccountRate(1);
                    bonusTransferVO.setBalance(p_c2stransferVO.getReceiverBonusValue());
                    bonusTransferVO.setValidity(p_c2stransferVO.getReceiverCreditBonusValidity());
                    bonusTransferVO.setGrace(0);
                    bonusTransferVO.setPreviousBalance(p_c2stransferVO.getReceiverTransferItemVO().getPreviousBalance());
                    bonusTransferVO.setPreviousValidity(p_c2stransferVO.getReceiverTransferItemVO().getPreviousExpiry());
                    bonusTransferVO.setPreviousGrace(p_c2stransferVO.getReceiverTransferItemVO().getPreviousGraceDate());
                    bonusTransferVO.setPostBalance(0);
                    bonusTransferVO.setPostValidity(null);
                    bonusTransferVO.setPostGrace(null);
                    bonusTransferVO.setCreatedOn(p_c2stransferVO.getTransferDate());
                    bonusList.add(bonusTransferVO);
                }
            }
            p_c2stransferVO.setBonusItems(bonusList);
            // Method call to generate a bonus summary string
            String bonusSummaryString = null;
            // bonusSummaryString=formatBonusSummaryString((ArrayList)p_c2stransferVO.getBonusItems());
            String bonusSummaryMessagesString = null;
            HashMap<String, String> bonuses = null;
            bonuses = formatBonusSummaryStringMessags((ArrayList) p_c2stransferVO.getBonusItems());
            bonusSummaryString = bonuses.get("bonus");
            bonusSummaryMessagesString = bonuses.get("bonusMessages");
            p_c2stransferVO.setBonusSummarySting(bonusSummaryString);
            p_c2stransferVO.setBonusSummaryMessageSting(bonusSummaryMessagesString);
        } catch (Exception e) {
            p_c2stransferVO.setBonusItems(bonusList);
            _log.error("updateBonusListAfterTopup", "Exception: " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("updateBonusListAfterTopup", "_transferID: " + p_c2stransferVO.getTransferID() + " Exited");
            }
        }
    }

    public boolean prefixServiceMappingExist(String p_serviceType, String p_prxfService) {
        if (p_serviceType.equals(p_prxfService)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Method to validate C2S fix line Recharge Request.
     * Receiver locale of notification msisdn
     * C2S Fixline recharge message array will be like::
     * Here we are handling the cases with PIN, if we want the cases without
     * PIN,it will be written in Operator specific util
     * 
     * FRC MSISDN AMT N_MSISDN PIN
     * FRC MSISDN AMT N_MSISDN SEL PIN
     * FRC MSISDN AMT N_MSISDN SEL REC PIN
     * FRC MSISDN AMT N_MSISDN SEL SEN REC PIN
     * 
     * @param p_con
     *            Connection
     * @param p_c2sTransferVO
     *            C2STransferVO
     * @param p_requestVO
     *            RequestVO
     */
    public void validateC2SFixLineRechargeRequest(Connection p_con, C2STransferVO p_c2sTransferVO, RequestVO p_requestVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("validateC2SFixLineRechargeRequest, p_requestVO" + p_requestVO.toString(), "");
        }
        final String METHOD_NAME = "validateC2SFixLineRechargeRequest";
        try {
            String receiverMsisdn = null;
            String requestedAmt = null;
            String notificationMsisdn = null;
            final String[] msgArray = p_requestVO.getRequestMessageArray();
            final int msgLength = msgArray.length;
            for (int i = 0; i < msgLength; i++) {
                if (_log.isDebugEnabled()) {
                    _log.debug("validateC2SFixLineRechargeRequest", "i=" + i + " ,value=" + msgArray[i]);
                }
            }
            if (msgLength < 5) {
                throw new BTSLBaseException(this, "validateC2SFixLineRechargeRequest", PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT);
            }
            final ChannelUserVO channelUserVO = (ChannelUserVO) p_c2sTransferVO.getSenderVO();
            UserPhoneVO userPhoneVO = null;
            if (!channelUserVO.isStaffUser()) {
                userPhoneVO = (UserPhoneVO) channelUserVO.getUserPhoneVO();
            } else {
                userPhoneVO = (UserPhoneVO) channelUserVO.getStaffUserDetails().getUserPhoneVO();
            }

            switch (msgLength) {
            // message Length 5, then message would be
            // FRC_MSISDN_Amt_NotificationMSISDN_PIN
                case 5:
                    {
                        // check the sender PIN
                        if (userPhoneVO.getPinRequired().equals(PretupsI.YES)) {
                            try {
                                ChannelUserBL.validatePIN(p_con, channelUserVO, msgArray[4]);
                            } catch (BTSLBaseException be) {
                                if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
                                    .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                                    p_con.commit();
                                }
                                throw be;
                            }
                        }
                        final ReceiverVO receiverVO = new ReceiverVO();
                        // Receiver MSISDN Validation
                        receiverMsisdn = msgArray[1];
                        PretupsBL.validateMsisdn(p_con, receiverVO, p_c2sTransferVO.getRequestID(), receiverMsisdn);

                        // Recharge amount Validation
                        requestedAmt = msgArray[2];
                        PretupsBL.validateAmount(p_c2sTransferVO, requestedAmt);
                        p_c2sTransferVO.setReceiverVO(receiverVO);

                        notificationMsisdn = msgArray[3];
                        if (notificationMsisdn.length() <= 2) {
                            if (_log.isDebugEnabled()) {
                                _log.debug("validateC2SFixLineRechargeRequest Replacing Notification MSISDN:", notificationMsisdn, ",By Sender MSISDN:" + channelUserVO
                                    .getMsisdn());
                            }
                            notificationMsisdn = channelUserVO.getMsisdn();
                        }

                        validateNotificationMsisdn(p_requestVO, p_requestVO.getRequestIDStr(), notificationMsisdn);

                        final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO
                            .getServiceType());
                        if (serviceSelectorMappingVO != null) {
                            p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                        }
                        p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                        break;
                    }

                // message Length 6, then message would be
                // FRC_MSISDN_Amt_NotificationMSISDN_selector_PIN
                case 6:
                    {
                        // message Length 7, then message would be
                        // FRC_MSISDN_Amt_selector_receiverlocale_PIN
                        if (userPhoneVO.getPinRequired().equals(PretupsI.YES)) {
                            try {
                                ChannelUserBL.validatePIN(p_con, channelUserVO, msgArray[5]);
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
                        receiverMsisdn = msgArray[1];
                        PretupsBL.validateMsisdn(p_con, receiverVO, p_c2sTransferVO.getRequestID(), receiverMsisdn);

                        requestedAmt = msgArray[2];
                        PretupsBL.validateAmount(p_c2sTransferVO, requestedAmt);
                        p_c2sTransferVO.setReceiverVO(receiverVO);
                        // notificationMsisdn=msgArray[3];
                        // p_requestVO.setNotificationMSISDN(notificationMsisdn);

                        if (BTSLUtil.isNullString(msgArray[3])) {
                            final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO
                                .getServiceType());
                            if (serviceSelectorMappingVO != null) {
                                p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                            }
                        } else {
                            p_requestVO.setReqSelector(msgArray[3]);
                        }

                        // PretupsBL.getSelectorValueFromCode(p_requestVO);
                        // changed for CRE_INT_CR00029 by ankit Zindal
                        if (BTSLUtil.isNullString(p_requestVO.getReqSelector())) {
                            final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO
                                .getServiceType());
                            if (serviceSelectorMappingVO != null) {
                                p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                            }
                        }
                        if (BTSLUtil.isNullString(msgArray[4])) {
                            p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                        } else {
                            final int langCode = PretupsBL.getLocaleValueFromCode(p_requestVO, msgArray[4]);
                            if (LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)) == null) {
                                throw new BTSLBaseException(this, "validateC2SFixLineRechargeRequest", PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
                            }
                            p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
                        }
                        break;
                    }

                case 7:
                    {
                        // message Length 7, then message would be
                        // FRC_MSISDN_Amt_NotificationMSISDN_selector_receiverlocale_PIN
                        if (userPhoneVO.getPinRequired().equals(PretupsI.YES)) {
                            try {
                                ChannelUserBL.validatePIN(p_con, channelUserVO, msgArray[6]);
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
                        receiverMsisdn = msgArray[1];
                        PretupsBL.validateMsisdn(p_con, receiverVO, p_c2sTransferVO.getRequestID(), receiverMsisdn);

                        requestedAmt = msgArray[2];
                        PretupsBL.validateAmount(p_c2sTransferVO, requestedAmt);
                        p_c2sTransferVO.setReceiverVO(receiverVO);
                        notificationMsisdn = msgArray[3];
                        p_requestVO.setNotificationMSISDN(notificationMsisdn);

                        if (BTSLUtil.isNullString(msgArray[4])) {
                            final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO
                                .getServiceType());
                            if (serviceSelectorMappingVO != null) {
                                p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                            }
                        } else {
                            p_requestVO.setReqSelector(msgArray[4]);
                        }

                        PretupsBL.getSelectorValueFromCode(p_requestVO);
                        // changed for CRE_INT_CR00029 by ankit Zindal
                        if (BTSLUtil.isNullString(p_requestVO.getReqSelector())) {
                            final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO
                                .getServiceType());
                            if (serviceSelectorMappingVO != null) {
                                p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                            }
                        }
                        if (BTSLUtil.isNullString(msgArray[5])) {
                            p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                        } else {
                            final int langCode = PretupsBL.getLocaleValueFromCode(p_requestVO, msgArray[5]);
                            if (LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)) == null) {
                                throw new BTSLBaseException(this, "validateC2SFixLineRechargeRequest", PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
                            }
                            p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
                        }
                        break;
                    }
                case 8:
                    {
                        // message Length 8, then message would be
                        // FRC_MSISDN_Amt_NotificationMSISDN_selector_receiverlocale_senderlocale_PIN
                        if (userPhoneVO.getPinRequired().equals(PretupsI.YES)) {
                            try {
                                ChannelUserBL.validatePIN(p_con, channelUserVO, msgArray[7]);
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
                        receiverMsisdn = msgArray[1];
                        PretupsBL.validateMsisdn(p_con, receiverVO, p_c2sTransferVO.getRequestID(), receiverMsisdn);
                        requestedAmt = msgArray[2];
                        PretupsBL.validateAmount(p_c2sTransferVO, requestedAmt);
                        p_c2sTransferVO.setReceiverVO(receiverVO);

                        notificationMsisdn = msgArray[3];
                        validateNotificationMsisdn(p_requestVO, p_requestVO.getRequestIDStr(), notificationMsisdn);

                        if (BTSLUtil.isNullString(msgArray[4])) {
                            final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO
                                .getServiceType());
                            if (serviceSelectorMappingVO != null) {
                                p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                            }
                        } else {
                            p_requestVO.setReqSelector(msgArray[4]);
                        }

                        PretupsBL.getSelectorValueFromCode(p_requestVO);
                        // changed for CRE_INT_CR00029 by ankit Zindal
                        if (BTSLUtil.isNullString(p_requestVO.getReqSelector())) {
                            final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO
                                .getServiceType());
                            if (serviceSelectorMappingVO != null) {
                                p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                            }
                        }
                        // For handling of sender locale
                        if (BTSLUtil.isNullString(msgArray[5])) {
                            p_requestVO.setSenderLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                        } else {
                            final int langCode = PretupsBL.getLocaleValueFromCode(p_requestVO, msgArray[5]);
                            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
                            p_c2sTransferVO.setLocale(p_requestVO.getSenderLocale());
                            p_c2sTransferVO.setLanguage(p_c2sTransferVO.getLocale().getLanguage());
                            p_c2sTransferVO.setCountry(p_c2sTransferVO.getLocale().getCountry());
                        }

                        if (BTSLUtil.isNullString(msgArray[6])) {
                            p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                        } else {
                            final int langCode = PretupsBL.getLocaleValueFromCode(p_requestVO, msgArray[6]);
                            if (LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)) == null) {
                                throw new BTSLBaseException(this, "validateC2SFixLineRechargeRequest", PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
                            }
                            p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
                        }
                        break;
                    }
                default:
                    throw new BTSLBaseException(this, "validateC2SFixLineRechargeRequest", PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT, 0, new String[] { p_requestVO
                        .getActualMessageFormat() }, null);
            }

        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("validateC2SFixLineRechargeRequest", "  Exception while validating user message :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateC2SFixLineRechargeRequest]", "", "",
                "", "Exception while validating user message" + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException(this, "validateC2SFixLineRechargeRequest", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("validateC2SFixLineRechargeRequest", "Exiting ");
        }

    }

    public String formatBonusSummaryString(ArrayList p_bonusVOList) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("formatBonusSummaryString", "p_bonusVOList: " + p_bonusVOList);
        }
        final String METHOD_NAME = "formatBonusSummaryString";
        String bonus = "";
        try {
            BonusTransferVO bonusTransferVO = null;
            if (p_bonusVOList != null && p_bonusVOList.size() > 0) {
                for (int i = 0, j = p_bonusVOList.size(); i < j; i++) {
                    bonusTransferVO = (BonusTransferVO) p_bonusVOList.get(i);
                    if (bonus.length() <= 210 && (bonusTransferVO.getBalance() > 0 || bonusTransferVO.getValidity() > 0)) {
                        bonus += bonusTransferVO.getAccountCode() + ":" + bonusTransferVO.getAccountType() + ":" + getDisplayAmount(bonusTransferVO.getBalance()) + ":" + bonusTransferVO
                            .getValidity() + "|";
                    } else if (bonus.length() >= 250) {
                        break;
                    }
                }
                if (bonus.length() > 0) {
                    bonus = bonus.substring(0, bonus.length() - 1);
                }
            }

        } catch (Exception e) {
            _log.error("formatBonusSummaryString", "Exception: " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("formatBonusSummaryString", "bonus: " + bonus);
            }
        }
        return bonus;
    }

    private HashMap<String, String> formatBonusSummaryStringMessags(ArrayList p_bonusVOList) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("formatBonusSummaryStringMessags", "p_boneusVOList: " + p_bonusVOList);
        }
        final String METHOD_NAME = "formatBonusSummaryStringMessags";
        String bonus = "";
        String bonusMessages = "";
        final HashMap<String, String> bonusMap = new HashMap<String, String>();
        try {
            BonusTransferVO bonusTransferVO = null;
            String DisplayAmount = null;
            if (p_bonusVOList != null && p_bonusVOList.size() > 0) {
                for (int i = 0, j = p_bonusVOList.size(); i < j; i++) {
                    DisplayAmount = getDisplayAmount(bonusTransferVO.getBalance());
                    bonusTransferVO = (BonusTransferVO) p_bonusVOList.get(i);
                    if (bonus.length() <= 210 && (bonusTransferVO.getBalance() > 0 || bonusTransferVO.getValidity() > 0)) {
                        DisplayAmount = bonus += bonusTransferVO.getAccountCode() + ":" + bonusTransferVO.getAccountType() + ":" + DisplayAmount + ":" + bonusTransferVO
                            .getValidity() + "|";
                        bonusMessages += bonusTransferVO.getAccountName() + ": " + DisplayAmount + ", ";
                    } else if (bonus.length() >= 250) {
                        break;
                    }
                }
                if (bonus.length() > 0) {
                    bonus = bonus.substring(0, bonus.length() - 1);
                    bonusMessages = bonusMessages.substring(0, bonusMessages.length() - 1);
                }
            }
        } catch (Exception e) {
            _log.error("formatBonusSummaryStringMessags", "Exception: " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("formatBonusSummaryStringMessags", "bonus: " + bonus + ", bonusMessages: " + bonusMessages);
            }
        }
        bonusMap.put("bonus", bonus);
        bonusMap.put("bonusMessages", bonusMessages);
        return bonusMap;
    }

    /**
     * Get Display Amount
     * 
     * @param p_amount
     *            double
     * @return String
     */
    public String getDisplayAmount(double p_amount) {
        final int multiplicationFactor = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue();
        final String METHOD_NAME = "getDisplayAmount";
        final double amount = (double) p_amount / (double) multiplicationFactor;
        String amountStr = new DecimalFormat("#############.###").format(amount);
        try {
            final long l = Long.parseLong(amountStr);
            amountStr = String.valueOf(l);
        } catch (Exception e) {
            amountStr = new DecimalFormat("############0.00#").format(amount);
            _log.errorTrace(METHOD_NAME, e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("getDisplayAmount", "Exiting with amountStr=" + amountStr);
        }
        return amountStr;
    }
}