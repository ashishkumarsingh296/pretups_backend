/**
 * @(#)OCMOperatorUtil.java
 *                          Copyright(c) 2011, Comviva Techonologies LTD.
 *                          All Rights Reserved
 * 
 *                          <description>
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Author Date History
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Jasmine kaur April 18, 2011 Initital Creation
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 * 
 */

package com.client.pretups.util.clientutils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.btsl.pretups.channel.transfer.businesslogic.BonusTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.btsl.pretups.payment.businesslogic.PaymentMethodCache;
import com.btsl.pretups.payment.businesslogic.PaymentMethodKeywordVO;
import com.btsl.pretups.payment.businesslogic.ServicePaymentMappingCache;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class OCMOperatorUtil extends OperatorUtil {
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.btsl.pretups.util.OperatorUtil#validateEVDRequestFormat(java.sql.
     * Connection,
     * com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO,
     * com.btsl.pretups.receiver.RequestVO)
     */
    public void validateEVDRequestFormat(Connection p_con, C2STransferVO p_c2sTransferVO, RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "validateEVDRequestFormat";
        try {
            final String[] p_requestArr = p_requestVO.getRequestMessageArray();
            String custMsisdn = null;
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
                _log.debug("validateEVDRequestFormat", "messageLen: " + messageLen);
            }
            for (int i = 0; i < messageLen; i++) {
                if (_log.isDebugEnabled()) {
                    _log.debug("validateEVDRequestFormat", "i: " + i + " value: " + p_requestArr[i]);
                }
            }
            switch (messageLen) {
                case 3:// Private recharge for STK (EVD AMT PIN)
                    {
                        // Do the 000 check Default PIN
                        if (userPhoneVO.getPinRequired().equals(PretupsI.YES)) {
                            try {
                                ChannelUserBL.validatePIN(p_con, channelUserVO, p_requestArr[2]);
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
                        custMsisdn = userPhoneVO.getMsisdn();

                        PretupsBL.validateMsisdn(p_con, receiverVO, p_c2sTransferVO.getRequestID(), custMsisdn);

                        // Recharge amount Validation
                        requestAmtStr = p_requestArr[1];
                        PretupsBL.validateAmount(p_c2sTransferVO, requestAmtStr);
                        p_c2sTransferVO.setReceiverVO(receiverVO);
                        // p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE));
                        // Changed on 27/05/07 for Service Type selector Mapping
                        final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO
                            .getServiceType());
                        if (serviceSelectorMappingVO != null) {
                            p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                        }
                        p_requestVO.setReceiverLocale(new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                        break;
                    }
                case 7:
                    {
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

                        if ("WEB".equalsIgnoreCase(p_requestVO.getRequestGatewayType())) {

                            if (BTSLUtil.isNullString(p_requestArr[3])) {
                                if (PretupsI.LOCALE_LANGAUGE_EN.equalsIgnoreCase(p_requestVO.getLocale().getLanguage())) {
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
                                final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO
                                    .getServiceType());
                                if (serviceSelectorMappingVO != null) {
                                    p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                                }
                            }

                            // For handling of sender locale
                            if (BTSLUtil.isNullString(p_requestArr[4])) {
                                p_requestVO.setSenderLocale(new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                            } else {
                                final int langCode = PretupsBL.getLocaleValueFromCode(p_requestVO, p_requestArr[4]);
                                p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
                            }
                            if (_log.isDebugEnabled()) {
                                _log.debug(this, "sender locale: =" + p_requestVO.getSenderLocale());
                            }

                            if (BTSLUtil.isNullString(p_requestArr[5])) {
                                p_requestVO.setReceiverLocale(new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                            } else {
                                final int langCode = PretupsBL.getLocaleValueFromCode(p_requestVO, p_requestArr[5]);
                                if (LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)) == null) {
                                    throw new BTSLBaseException(this, "validateEVDRequestFormat", PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
                                }
                                p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
                            }

                        } else if ("USSD".equalsIgnoreCase(p_requestVO.getRequestGatewayType())) {

                            if (BTSLUtil.isNullString(p_requestArr[5])) {
                                if (PretupsI.LOCALE_LANGAUGE_EN.equalsIgnoreCase(p_requestVO.getLocale().getLanguage())) {
                                    final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO
                                        .getServiceType());
                                    if (serviceSelectorMappingVO != null) {
                                        p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                                    }
                                }
                            } else {
                                p_requestVO.setReqSelector(p_requestArr[5]);
                            }

                            PretupsBL.getSelectorValueFromCode(p_requestVO);
                            if (BTSLUtil.isNullString(p_requestVO.getReqSelector())) {
                                final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO
                                    .getServiceType());
                                if (serviceSelectorMappingVO != null) {
                                    p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                                }
                            }

                            // For handling of sender locale
                            if (BTSLUtil.isNullString(p_requestArr[3])) {
                                p_requestVO.setSenderLocale(new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                            } else {
                                final int langCode = PretupsBL.getLocaleValueFromCode(p_requestVO, p_requestArr[3]);
                                p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
                            }
                            if (_log.isDebugEnabled()) {
                                _log.debug(this, "sender locale: =" + p_requestVO.getSenderLocale());
                            }

                            if (BTSLUtil.isNullString(p_requestArr[4])) {
                                p_requestVO.setReceiverLocale(new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                            } else {
                                final int langCode = PretupsBL.getLocaleValueFromCode(p_requestVO, p_requestArr[4]);
                                if (LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)) == null) {
                                    throw new BTSLBaseException(this, "validateEVDRequestFormat", PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
                                }
                                p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
                            }

                        }

                        break;
                    }
                default:
                    throw new BTSLBaseException(this, "validateEVDRequestFormat", PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT, 0, new String[] { p_requestVO
                        .getActualMessageFormat() }, null);
            }
            // Self EVR Allowed Check
            final String senderMSISDN = (channelUserVO.getUserPhoneVO()).getMsisdn();
            final String receiverMSISDN = ((ReceiverVO) p_c2sTransferVO.getReceiverVO()).getMsisdn();
            if (p_c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_EVR)) {
                if (receiverMSISDN.equals(senderMSISDN) && !SystemPreferences.ALLOW_SELF_EVR) {
                    throw new BTSLBaseException(this, "validateEVDRequestFormat", PretupsErrorCodesI.CHNL_ERROR_SELF_TOPUP_NTALLOWD);
                }
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("validateEVDRequestFormat", "  Exception while validating user message :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OCMOperatorUtil[validateEVDRequestFormat]", "", "",
                "", "Exception while validating user message" + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException(this, "validateEVDRequestFormat", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("validateEVDRequestFormat", "Exiting ");
        }

    }

    /**
     * This method used for Password validation.
     * While creating or modifying the user Password This method will be used.
     * Method validatePassword.
     * 
     * @author ved.sharma
     * @created on 29/04/11
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
        if (p_password.length() < SystemPreferences.MIN_LOGIN_PWD_LENGTH || p_password.length() > SystemPreferences.MAX_LOGIN_PWD_LENGTH) {
            final String[] args = { String.valueOf(SystemPreferences.MIN_LOGIN_PWD_LENGTH), String.valueOf(SystemPreferences.MAX_LOGIN_PWD_LENGTH) };
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

        // For Password Should contains atleast one character
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
            _log.debug("validatePassword", "Exiting messageMap.size()=" + messageMap.size());
        }
        return messageMap;
    }

    // For self toup
    private void validateIfNotBuddy(Connection p_con, RequestVO p_requestVO, TransferVO p_transferVO) throws BTSLBaseException, Exception {
        final String[] requestMessageArray = p_requestVO.getRequestMessageArray();
        if (_log.isDebugEnabled()) {
            _log.debug("validateIfNotBuddy", " requestMessageArray length:" + requestMessageArray);
        }
        if (requestMessageArray.length < 3 || requestMessageArray.length > 4) {
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

        // Self topup >> PRC ICICI 1000 1357
        // Self topup >> PRC ICICI 1000
        switch (messageLength) {
            case 3:
                {
                    // whether PIN validation is required or not.
                    if (SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) {
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
                    if (SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) {
                        if (actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                            if (!BTSLUtil.isNullString(requestMessageArray[3])) {
                                if (BTSLUtil.isNumeric(requestMessageArray[3]) && requestMessageArray[3].length() == 1) {
                                    p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(requestMessageArray[3]));
                                    if (p_requestVO.getReceiverLocale() == null) {
                                        p_requestVO.setReceiverLocale(new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                                    }
                                } else {
                                    if (!BTSLUtil.isNullString(pin) && !pin.equals((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                                        validatePIN(pin);
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
        }
        p_transferVO.setIncomingSmsStr(incomingSmsStr.toString());
    }

    public void checkAfterPaymentMethod(Connection p_con, int i, String[] p_requestMessageArray, StringBuffer incomingSmsStr, TransferVO p_transferVO) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("checkAfterPaymentMethod", " i=" + i + " requestMessageArray length:" + p_requestMessageArray.length);
        }

        final SenderVO senderVO = (SenderVO) p_transferVO.getSenderVO();

        String receiverMSISDN = senderVO.getMsisdn(); // Self topup sender
        // itself receiver
        receiverMSISDN = addRemoveDigitsFromMSISDN(PretupsBL.getFilteredMSISDN(receiverMSISDN));
        if (!BTSLUtil.isValidMSISDN(receiverMSISDN)) {
            throw new BTSLBaseException(this, "checkAfterPaymentMethod", PretupsErrorCodesI.ERROR_INVALID_MSISDN, 0, new String[] { receiverMSISDN }, null);
        }

        incomingSmsStr.append(receiverMSISDN + " ");
        final ReceiverVO _receiverVO = new ReceiverVO();
        _receiverVO.setMsisdn(receiverMSISDN);
        final NetworkPrefixVO networkPrefixVO = PretupsBL.getNetworkDetails(receiverMSISDN, PretupsI.USER_TYPE_RECEIVER);
        if (networkPrefixVO == null) {
            throw new BTSLBaseException(this, "checkAfterPaymentMethod", PretupsErrorCodesI.ERROR_NOTFOUND_RECEIVERNETWORK, 0, new String[] { receiverMSISDN }, null);
        }
        _receiverVO.setNetworkCode(networkPrefixVO.getNetworkCode());
        _receiverVO.setPrefixID(networkPrefixVO.getPrefixID());
        _receiverVO.setSubscriberType(networkPrefixVO.getSeriesType());
        p_transferVO.setReceiverVO(_receiverVO);
        long amount = 0;
        amount = PretupsBL.getSystemAmount(p_requestMessageArray[i + 1]);
        if (amount < 0) {
            throw new BTSLBaseException(this, "checkAfterPaymentMethod", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
        }
        p_transferVO.setTransferValue(amount);
        p_transferVO.setRequestedAmount(amount);
        incomingSmsStr.append(amount + " ");
    }

    public boolean checkIfBuddy(Connection p_con, RequestVO p_requestVO, TransferVO p_transferVO) throws BTSLBaseException, Exception {
        final String[] requestMessageArray = p_requestVO.getRequestMessageArray();
        if (_log.isDebugEnabled()) {
            _log.debug("checkIfBuddy", " requestMessageArray length:" + requestMessageArray.length);
        }
        final String serviceKeyword = requestMessageArray[0];
        final String senderSubscriberType = ((SenderVO) p_transferVO.getSenderVO()).getSubscriberType();
        boolean cBuddy = false;
        final StringBuffer incomingSmsStr = new StringBuffer(serviceKeyword + " ");
        if (requestMessageArray.length < 2 || requestMessageArray.length > 4) {
            throw new BTSLBaseException(this, "checkIfBuddy", PretupsErrorCodesI.P2P_INVALID_MESSAGEFORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() }, null);
        }

        // if receiver buddy
        // Validate 2nd Argument for Payment Method Keyword.
        final String paymentMethodKeyword = requestMessageArray[1];

        // if paymentMethod invalid , Validate 2nd Argument for Receiver
        // No(MSISDN).
        final PaymentMethodKeywordVO paymentMethodKeywordVO = PaymentMethodCache.getObject(paymentMethodKeyword, p_transferVO.getServiceType(), p_transferVO.getNetworkCode());
        String paymentMethodType = null;
        if (paymentMethodKeywordVO == null) {
            paymentMethodType = ServicePaymentMappingCache.getDefaultPaymentMethod(p_transferVO.getServiceType(), senderSubscriberType);
            if (paymentMethodType == null) {
                // return with error message, no default payment method defined
                throw new BTSLBaseException(this, "checkIfBuddy", PretupsErrorCodesI.ERROR_NOTFOUND_DEFAULTPAYMENTMETHOD);
            }
            p_transferVO.setPaymentMethodType(paymentMethodType);
            p_transferVO.setDefaultPaymentMethod("Y");
            incomingSmsStr.append(paymentMethodType + " ");
            cBuddy = checkAfterPaymentMethodForBuddy(p_con, 1, requestMessageArray, incomingSmsStr, p_transferVO, p_requestVO);
        } else {
            paymentMethodType = paymentMethodKeywordVO.getPaymentMethodType();
            p_transferVO.setPaymentMethodType(paymentMethodType);
            p_transferVO.setPaymentMethodKeywordVO(paymentMethodKeywordVO);
            p_transferVO.setDefaultPaymentMethod(PretupsI.NO);
            incomingSmsStr.append(paymentMethodType + " ");
            // if paymentMethod valid , Validate 3rd Argument for Receiver
            // No(MSISDN).
            if (requestMessageArray.length < 3) {
                throw new BTSLBaseException(this, "checkIfBuddy", PretupsErrorCodesI.P2P_INVALID_MESSAGEFORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() }, null);
            }

            cBuddy = checkAfterPaymentMethodForBuddy(p_con, 2, requestMessageArray, incomingSmsStr, p_transferVO, p_requestVO);
        }
        p_transferVO.setIncomingSmsStr(incomingSmsStr.toString());
        if (_log.isDebugEnabled()) {
            _log.debug("checkIfBuddy", " return value:" + cBuddy);
        }
        return cBuddy;
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
                                final double y = Double.parseDouble(balance[x]);
                                bonusTransferVO.setBalance((long) y);
                            }
                            if (!BTSLUtil.isNullArray(validity) && !BTSLUtil.isNullString(validity[x]) && validity[x].length() > 0) {
                                bonusTransferVO.setValidity(Long.parseLong(validity[x]));
                            }
                            if (!BTSLUtil.isNullArray(grace) && !BTSLUtil.isNullString(grace[x]) && grace[x].length() > 0) {
                                bonusTransferVO.setGrace(Long.parseLong(grace[x]));
                            }
                            if (!BTSLUtil.isNullArray(postBalance) && !BTSLUtil.isNullString(postBalance[x]) && postBalance[x].length() > 0) {
                                final double y = Double.parseDouble(postBalance[x]);
                                bonusTransferVO.setPostBalance((long) y);
                            }
                            try {
                                if (!BTSLUtil.isNullArray(postValidity) && !BTSLUtil.isNullString(postValidity[x]) && postValidity[x].length() > 0) {
                                    bonusTransferVO.setPostValidity(BTSLUtil.getDateFromDateString(postValidity[x], PretupsI.DATE_FORMAT_DDMMYYYY));
                                }
                            } catch (Exception e) {
                                _log.errorTrace(METHOD_NAME, e);
                                bonusTransferVO.setPostValidity(null);
                            }
                            try {
                                if (!BTSLUtil.isNullArray(postGrace) && !BTSLUtil.isNullString(postGrace[x]) && postGrace[x].length() > 0) {
                                    bonusTransferVO.setPostGrace(BTSLUtil.getDateFromDateString(postGrace[x], PretupsI.DATE_FORMAT_DDMMYYYY));
                                }
                            } catch (Exception e) {
                                _log.errorTrace(METHOD_NAME, e);
                                bonusTransferVO.setPostGrace(null);
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
            bonusSummaryString = formatBonusSummaryString((ArrayList) p_c2stransferVO.getBonusItems());
            p_c2stransferVO.setBonusSummarySting(bonusSummaryString);
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
                        bonusTransferVO.setPreviousBalance(Long.parseLong(previousBalance[i]));
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
                        _log.errorTrace(METHOD_NAME, e);
                        bonusTransferVO.setPreviousGrace(null);
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

}