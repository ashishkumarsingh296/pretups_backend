package com.client.pretups.util.clientutils;

import java.sql.Connection;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDetailsVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.p2p.subscriber.businesslogic.BuddyVO;
import com.btsl.pretups.p2p.subscriber.businesslogic.P2PBuddiesDAO;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.btsl.pretups.payment.businesslogic.PaymentMethodCache;
import com.btsl.pretups.payment.businesslogic.PaymentMethodKeywordVO;
import com.btsl.pretups.payment.businesslogic.ServicePaymentMappingCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;

public class UgandaUtil extends OperatorUtil {

    private Log _log = LogFactory.getLog(this.getClass().getName());
    public static OperatorUtilI _operatorUtil = null;

    /**
     * Method to set the value for transfer.
     * This methos is called from CardGroupBL. At this time this method set
     * various values in the transferVO
     * For any other operator who wants to run system on CVG mode, we can change
     * values setting based on subservice.
     * 
     * @param p_subService
     *            String
     * @param p_cardGroupDetailVO
     *            CardGroupDetailsVO
     * @param p_transferVO
     *            TransferVO
     * @throws Exception
     * @see com.btsl.pretups.util.OperatorUtilI#setCalculatedCardGroupValues(String,
     *      CardGroupDetailsVO, TransferVO)
     */
    public void setCalculatedCardGroupValues(String p_subService, CardGroupDetailsVO p_cardGroupDetailVO, TransferVO p_transferVO) throws Exception {
        try {
            TransferItemVO transferItemVO = null;
            final int bonusValidityValue = Integer.parseInt(String.valueOf(p_cardGroupDetailVO.getBonusValidityValue()));
            final int validityPeriodValue = p_cardGroupDetailVO.getValidityPeriod();
            final long transferValue = p_cardGroupDetailVO.getTransferValue();
            final long bonusValue = p_cardGroupDetailVO.getBonusTalkTimeValue();
            transferItemVO = (TransferItemVO) p_transferVO.getTransferItemList().get(1);

            if ((String.valueOf(PretupsI.CHNL_SELECTOR_CVG_VALUE)).equals(p_subService))// CVG,
            // selector=1.
            {
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
            }
            if ((String.valueOf(PretupsI.CHNL_SELECTOR_C_VALUE)).equals(p_subService))// C,
            // selector=2.
            {
                p_transferVO.setReceiverBonusValidity(0);
                p_transferVO.setReceiverGracePeriod(0);
                p_transferVO.setReceiverValidity(validityPeriodValue);
                p_transferVO.setReceiverTransferValue(transferValue);
                transferItemVO.setTransferValue(transferValue);
                transferItemVO.setGraceDaysStr("0");
                transferItemVO.setValidity(0);
                p_transferVO.setReceiverBonusValue(bonusValue);
            }
            if ((String.valueOf(PretupsI.CHNL_SELECTOR_VG_VALUE)).equals(p_subService))// VG
            {
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
            }
        } catch (Exception e) {
            throw e;
        }
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
        final String METHOD_NAME = "validateIfNotBuddy";
        final String[] requestMessageArray = p_requestVO.getRequestMessageArray();
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
                    // FOr uganda whether PIN validation is required or not.
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
                    // Bu sushma for uganda
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
                    // By sushma for uganda
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
                            // _requestVO.setReqSelector(""+SystemPreferences.P2P_TRANSFER_DEF_SELECTOR_CODE);
                            if (!BTSLUtil.isNullString(requestMessageArray[3])) {

                                p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(requestMessageArray[3]));
                                if (p_requestVO.getReceiverLocale() == null) {
                                    // by ankit
                                    // zindal
                                    // 01/08/06
                                    // discussed
                                    // by AC/GB
                                    throw new BTSLBaseException(this, "validateIfNotBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                    // _requestVO.setReceiverLocale(new
                                    // Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                                }

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
                    // By sushma for uganda
                    if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) {
                        if (actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                            // if(!pin.equals(PretupsI.DEFAULT_P2P_PIN))
                            if (!BTSLUtil.isNullString(pin) && !pin.equals((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                                // BTSLUtil.validatePIN(pin);
                                _operatorUtil.validatePINRules(pin);
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

    /**
     * Check After Payment Method For Buddy
     * 
     * @param p_con
     * @param i
     * @return
     * @throws BTSLBaseException
     * @throws Exception
     */
    public boolean checkAfterPaymentMethodForBuddy(Connection p_con, int i, String[] p_requestMessageArray, StringBuffer incomingSmsStr, TransferVO p_transferVO, RequestVO p_requestVO) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("checkAfterPaymentMethodForBuddy", " i=" + i + " requestMessageArray length:" + p_requestMessageArray.length + " i=" + i);
        }
        final String METHOD_NAME = "checkAfterPaymentMethodForBuddy";
        int incReq = 0;
        if (i == 2) {
            incReq = 1;
        }
        final String receiverMSISDN_NAME = p_requestMessageArray[1 + incReq];
        final BuddyVO buddyVO = new P2PBuddiesDAO().loadBuddyDetails(p_con, ((SenderVO) p_transferVO.getSenderVO()).getUserID(), receiverMSISDN_NAME);
        if (buddyVO == null) {
            return false;
        }
        final String receiverMSISDN = buddyVO.getMsisdn();
        final NetworkPrefixVO networkPrefixVO = PretupsBL.getNetworkDetails(receiverMSISDN, PretupsI.USER_TYPE_RECEIVER);
        if (networkPrefixVO == null) {
            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_NOTFOUND_RECEIVERNETWORK, 0, new String[] { receiverMSISDN }, null);
        }
        buddyVO.setNetworkCode(networkPrefixVO.getNetworkCode());
        buddyVO.setPrefixID(networkPrefixVO.getPrefixID());
        buddyVO.setSubscriberType(networkPrefixVO.getSeriesType());
        p_transferVO.setReceiverVO(buddyVO);
        incomingSmsStr.append(receiverMSISDN_NAME + " ");
        final int messageLength = p_requestMessageArray.length;
        String pin = null;
        long amount = 0;
        final SenderVO senderVO = (SenderVO) p_transferVO.getSenderVO();
        final String actualPin = BTSLUtil.decryptText(senderVO.getPin());

        /*
         * Message format that are supported are as:
         * Message length 2: PRC Name
         * If pin required
         * the PIN should be default
         * Amount will always be default
         * Message length 3:
         * PIN required and pin is not default
         * PRC Name PIN
         * (PIN required and actual pin=default pin) OR PIN not required
         * PRC HDFC Name
         * PRC Name Amount
         * Message length 4:
         * PIN Required
         * actual pin!=default pin
         * PRC Name Amount PIN
         * PRC HDFC NAme PIN
         * actual=default
         * PRC HDFC Name Amount
         * PRC Name Amount langCode
         * PIN not required
         * PRC HDFC name Amount
         * PRC Name Amount langCode
         * Message length 5:
         * PIN Required
         * actual!=default
         * PRC HDFC Name Amount PIN
         * PRC Name Amount langCode PIN
         * Actual=default
         * PRC HDFC Name Amount langCode
         * PRC Name Amount selector lang
         * PIN not required
         * PRC HDFC Name Amount LangCode
         * PRC Name Amount selector LangCode
         * Message length 6:
         * PIN Required
         * Actual!=default
         * PRC HDFC Name Amount langCode PIN
         * PRC Name Amount selector langCode PIN
         * Actual=default
         * PRC HDFC Name Amount selector langCode
         * PRC Name Amount selector langCode PIN(Update with new PIN)
         * PIN not required
         * PRC HDFC Name Amount selector langCode
         * PRC Name Amount Selector langCode PIN( Update with new PIN)
         * Message length 7:
         * PIN Required
         * Actual!=default
         * PRC HDFC Name Amount selector langCode PIN
         * Actual=default
         * PRC HDFC Name Amount selector langCode PIN(Update with new PIN)
         * PIN not required
         * PRC HDFC Name Amount selector langCode PIN (Update with new PIN)
         */
        switch (messageLength) {
            case 2:
                {

                    if (((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.BUDDY_PIN_REQUIRED, networkPrefixVO.getNetworkCode())).booleanValue()) {
                        // by sushma
                        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) {
                            if (!actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_INVALID_MESSAGEFORMAT, 0,
                                    new String[] { p_requestVO.getActualMessageFormat() }, null);
                            }
                        }

                        amount = buddyVO.getPreferredAmount();
                        if (amount < 0) {
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                        }
                        p_transferVO.setTransferValue(amount);
                        p_transferVO.setRequestedAmount(amount);
                        incomingSmsStr.append(amount + " ");
                        break;
                    }
                }
            case 3:
                {
                    // if(SystemPreferences.CP2P_PIN_VALIDAT_REQ)
                    if ((((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) && ((((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.BUDDY_PIN_REQUIRED,
                        networkPrefixVO.getNetworkCode())).booleanValue()) && !actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))) {
                        if (i == 2) {
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_INVALID_MESSAGEFORMAT, 0, new String[] { p_requestVO
                                .getActualMessageFormat() }, null);
                        } else {
                            pin = p_requestMessageArray[2];
                            incomingSmsStr.append("****" + " ");

                            try {
                                SubscriberBL.validatePIN(p_con, senderVO, pin);
                            } catch (BTSLBaseException be) {
                                if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey()
                                    .equals(PretupsErrorCodesI.ERROR_SNDR_PINBLOCK)))) {
                                    p_con.commit();
                                }
                                throw be;
                            }

                            amount = buddyVO.getPreferredAmount();
                            if (amount < 0) {
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                            }
                            p_transferVO.setTransferValue(amount);
                            p_transferVO.setRequestedAmount(amount);
                            incomingSmsStr.append(amount + " ");
                        }
                    } else {
                        if (i == 2) {
                            amount = buddyVO.getPreferredAmount();
                            if (amount < 0) {
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                            }
                        } else {
                            amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                            if (amount < 0) {
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                            }
                        }
                        p_transferVO.setTransferValue(amount);
                        p_transferVO.setRequestedAmount(amount);
                        incomingSmsStr.append(amount + " ");
                    }
                    break;
                }
            case 4:
                {
                    if (((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.BUDDY_PIN_REQUIRED, networkPrefixVO.getNetworkCode())).booleanValue()) {
                        // for Uganda whether PIN validation is required or not.
                        if ((((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) && !(actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))) {
                            pin = p_requestMessageArray[3];
                            incomingSmsStr.append("****" + " ");
                            try {
                                SubscriberBL.validatePIN(p_con, senderVO, pin);
                            } catch (BTSLBaseException be) {
                                if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey()
                                    .equals(PretupsErrorCodesI.ERROR_SNDR_PINBLOCK)))) {
                                    p_con.commit();
                                }
                                throw be;
                            }
                            if (i == 2) {
                                amount = buddyVO.getPreferredAmount();
                                if (amount < 0) {
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                                }
                            } else {
                                amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                                if (amount < 0) {
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                                }

                            }
                        } else {
                            if (i == 2) {
                                amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                                if (amount < 0) {
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                                }
                            } else {
                                amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                                if (amount < 0) {
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                                }
                                try {
                                    if (!BTSLUtil.isNullString(p_requestMessageArray[3])) {

                                        p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[3]));
                                        if (p_requestVO.getReceiverLocale() == null) {
                                            // by
                                            // ankit
                                            // zindal
                                            // 01/08/06
                                            // discussed
                                            // by
                                            // AC/GB
                                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                        }
                                    }
                                } catch (Exception e) {
                                    _log.errorTrace(METHOD_NAME, e);
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                }
                            }
                        }
                        p_transferVO.setTransferValue(amount);
                        p_transferVO.setRequestedAmount(amount);
                        incomingSmsStr.append(amount + " ");
                    } else {
                        if (i == 2) {
                            amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                            if (amount < 0) {
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                            }
                        } else {
                            amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                            if (amount < 0) {
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                            }
                            try {
                                if (!BTSLUtil.isNullString(p_requestMessageArray[3])) {

                                    p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[3]));
                                    if (p_requestVO.getReceiverLocale() == null) {
                                        // by
                                        // ankit
                                        // zindal
                                        // 01/08/06
                                        // discussed
                                        // by
                                        // AC/GB
                                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                    }
                                }
                            } catch (Exception e) {
                                _log.errorTrace(METHOD_NAME, e);
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                            }
                        }
                        p_transferVO.setTransferValue(amount);
                        p_transferVO.setRequestedAmount(amount);
                        incomingSmsStr.append(amount + " ");
                    }
                    break;
                }
            case 5:
                {
                    if (((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.BUDDY_PIN_REQUIRED, networkPrefixVO.getNetworkCode())).booleanValue()) {
                        if ((((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) && !(actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))) {
                            pin = p_requestMessageArray[4];
                            incomingSmsStr.append("****" + " ");
                            try {
                                SubscriberBL.validatePIN(p_con, senderVO, pin);
                            } catch (BTSLBaseException be) {
                                if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey()
                                    .equals(PretupsErrorCodesI.ERROR_SNDR_PINBLOCK)))) {
                                    p_con.commit();
                                }
                                throw be;
                            }
                            if (i == 2) {
                                amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                                if (amount < 0) {
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                                }
                            } else {
                                amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                                if (amount < 0) {
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                                }
                                try {
                                    if (!BTSLUtil.isNullString(p_requestMessageArray[3])) {
                                        final int localeValue = Integer.parseInt(p_requestMessageArray[3]);
                                        p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[3]));
                                        if (p_requestVO.getReceiverLocale() == null) {
                                            // by
                                            // ankit
                                            // zindal
                                            // 01/08/06
                                            // discussed
                                            // by
                                            // AC/GB
                                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                        }
                                    }
                                } catch (Exception e) {
                                    _log.errorTrace(METHOD_NAME, e);
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                }
                            }
                            p_transferVO.setTransferValue(amount);
                            p_transferVO.setRequestedAmount(amount);
                            incomingSmsStr.append(amount + " ");
                        } else {
                            if (i == 2) {
                                amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                                if (amount < 0) {
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                                }
                                try {
                                    if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                        final int localeValue = Integer.parseInt(p_requestMessageArray[4]);
                                        p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[4]));
                                        if (p_requestVO.getReceiverLocale() == null) {
                                            // by
                                            // ankit
                                            // zindal
                                            // 01/08/06
                                            // discussed
                                            // by
                                            // AC/GB
                                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                        }
                                    }
                                } catch (Exception e) {
                                    _log.errorTrace(METHOD_NAME, e);
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                }
                            } else {
                                amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                                if (amount < 0) {
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                                }

                                try {
                                    if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                        final int localeValue = Integer.parseInt(p_requestMessageArray[4]);
                                        p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[4]));
                                        if (p_requestVO.getReceiverLocale() == null) {
                                            // by
                                            // ankit
                                            // zindal
                                            // 01/08/06
                                            // discussed
                                            // by
                                            // AC/GB
                                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                        }
                                    }
                                } catch (Exception e) {
                                    _log.errorTrace(METHOD_NAME, e);
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                }
                                if (!BTSLUtil.isNullString(p_requestMessageArray[3])) {
                                    final int selectorValue = Integer.parseInt(p_requestMessageArray[3]);
                                    p_requestVO.setReqSelector("" + selectorValue);
                                }
                            }
                            p_transferVO.setTransferValue(amount);
                            p_transferVO.setRequestedAmount(amount);
                            incomingSmsStr.append(amount + " ");
                        }
                    } else {
                        if (i == 2) {
                            amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                            if (amount < 0) {
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                            }
                            try {
                                if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                    final int localeValue = Integer.parseInt(p_requestMessageArray[4]);
                                    p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[4]));
                                    if (p_requestVO.getReceiverLocale() == null) {
                                        // by
                                        // ankit
                                        // zindal
                                        // 01/08/06
                                        // discussed
                                        // by
                                        // AC/GB
                                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                    }
                                }
                            } catch (Exception e) {
                                _log.errorTrace(METHOD_NAME, e);
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                            }
                        } else {
                            amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                            if (amount < 0) {
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                            }
                            try {
                                if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                    final int localeValue = Integer.parseInt(p_requestMessageArray[4]);
                                    p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[4]));
                                    if (p_requestVO.getReceiverLocale() == null) {
                                        // by
                                        // ankit
                                        // zindal
                                        // 01/08/06
                                        // discussed
                                        // by
                                        // AC/GB
                                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                    }
                                }
                            } catch (Exception e) {
                                _log.errorTrace(METHOD_NAME, e);
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                            }
                            if (!BTSLUtil.isNullString(p_requestMessageArray[3])) {
                                final int selectorValue = Integer.parseInt(p_requestMessageArray[3]);
                                p_requestVO.setReqSelector("" + selectorValue);
                            }
                        }
                        p_transferVO.setTransferValue(amount);
                        p_transferVO.setRequestedAmount(amount);
                        incomingSmsStr.append(amount + " ");
                    }
                    break;
                }
            case 6:
                {
                    if (((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.BUDDY_PIN_REQUIRED, networkPrefixVO.getNetworkCode())).booleanValue()) {
                        if ((((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) && !(actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))) {
                            pin = p_requestMessageArray[5];
                            incomingSmsStr.append("****" + " ");
                            try {
                                SubscriberBL.validatePIN(p_con, senderVO, pin);
                            } catch (BTSLBaseException be) {
                                if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey()
                                    .equals(PretupsErrorCodesI.ERROR_SNDR_PINBLOCK)))) {
                                    p_con.commit();
                                }
                                throw be;
                            }
                            if (i == 2) {
                                amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                                if (amount < 0) {
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                                }
                                try {
                                    if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                        final int localeValue = Integer.parseInt(p_requestMessageArray[4]);
                                        p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[4]));
                                        if (p_requestVO.getReceiverLocale() == null) {
                                            // by
                                            // ankit
                                            // zindal
                                            // 01/08/06
                                            // discussed
                                            // by
                                            // AC/GB
                                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                        }
                                    }
                                } catch (Exception e) {
                                    _log.errorTrace(METHOD_NAME, e);
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                }
                            } else {
                                amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                                if (amount < 0) {
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                                }
                                try {
                                    if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                        final int localeValue = Integer.parseInt(p_requestMessageArray[4]);
                                        p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[4]));
                                        if (p_requestVO.getReceiverLocale() == null) {
                                            // by
                                            // ankit
                                            // zindal
                                            // 01/08/06
                                            // discussed
                                            // by
                                            // AC/GB
                                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                        }
                                    }
                                } catch (Exception e) {
                                    _log.errorTrace(METHOD_NAME, e);
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                }
                                if (!BTSLUtil.isNullString(p_requestMessageArray[3])) {
                                    final int selectorValue = Integer.parseInt(p_requestMessageArray[3]);
                                    p_requestVO.setReqSelector("" + selectorValue);
                                }
                            }
                        } else {
                            if (i == 2) {
                                amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                                if (amount < 0) {
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                                }
                                try {
                                    if (!BTSLUtil.isNullString(p_requestMessageArray[5])) {
                                        final int localeValue = Integer.parseInt(p_requestMessageArray[5]);
                                        p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[5]));
                                        if (p_requestVO.getReceiverLocale() == null) {
                                            // by
                                            // ankit
                                            // zindal
                                            // 01/08/06
                                            // discussed
                                            // by
                                            // AC/GB
                                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                        }
                                    }
                                } catch (Exception e) {
                                    _log.errorTrace(METHOD_NAME, e);
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                }
                                if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                    final int selectorValue = Integer.parseInt(p_requestMessageArray[4]);
                                    p_requestVO.setReqSelector("" + selectorValue);
                                }
                            } else {
                                pin = p_requestMessageArray[5];
                                incomingSmsStr.append("****" + " ");
                                BTSLUtil.validatePIN(pin);
                                senderVO.setPin(BTSLUtil.encryptText(pin));
                                senderVO.setPinUpdateReqd(true);

                                amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                                if (amount < 0) {
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                                }
                                try {
                                    if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                        final int localeValue = Integer.parseInt(p_requestMessageArray[4]);
                                        p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[4]));
                                        if (p_requestVO.getReceiverLocale() == null) {
                                            // by
                                            // ankit
                                            // zindal
                                            // 01/08/06
                                            // discussed
                                            // by
                                            // AC/GB
                                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                        }
                                    }
                                } catch (Exception e) {
                                    _log.errorTrace(METHOD_NAME, e);
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                }
                                if (!BTSLUtil.isNullString(p_requestMessageArray[3])) {
                                    final int selectorValue = Integer.parseInt(p_requestMessageArray[3]);
                                    p_requestVO.setReqSelector("" + selectorValue);
                                }
                            }
                        }
                        p_transferVO.setTransferValue(amount);
                        p_transferVO.setRequestedAmount(amount);
                        incomingSmsStr.append(amount + " ");
                    }

                    else {
                        if (i == 2) {
                            amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                            if (amount < 0) {
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                            }
                            try {
                                if (!BTSLUtil.isNullString(p_requestMessageArray[5])) {
                                    final int localeValue = Integer.parseInt(p_requestMessageArray[5]);
                                    p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[5]));
                                    if (p_requestVO.getReceiverLocale() == null) {
                                        // by
                                        // ankit
                                        // zindal
                                        // 01/08/06
                                        // discussed
                                        // by
                                        // AC/GB
                                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                    }
                                }
                            } catch (Exception e) {
                                _log.errorTrace(METHOD_NAME, e);
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                            }
                            if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                final int selectorValue = Integer.parseInt(p_requestMessageArray[4]);
                                p_requestVO.setReqSelector("" + selectorValue);
                            }
                        } else {
                            // FOr Uganda to check whether PIN validation is
                            // required or
                            // not.
                            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) {
                                if (actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                                    pin = p_requestMessageArray[5];
                                    incomingSmsStr.append("****" + " ");
                                    BTSLUtil.validatePIN(pin);
                                    senderVO.setPin(BTSLUtil.encryptText(pin));
                                    senderVO.setPinUpdateReqd(true);
                                }
                            }
                            amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                            if (amount < 0) {
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                            }
                            try {
                                if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                    final int localeValue = Integer.parseInt(p_requestMessageArray[4]);
                                    p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[4]));
                                    if (p_requestVO.getReceiverLocale() == null) {
                                        // by
                                        // ankit
                                        // zindal
                                        // 01/08/06
                                        // discussed
                                        // by
                                        // AC/GB
                                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                    }
                                }
                            } catch (Exception e) {
                                _log.errorTrace(METHOD_NAME, e);
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                            }
                            if (!BTSLUtil.isNullString(p_requestMessageArray[3])) {
                                final int selectorValue = Integer.parseInt(p_requestMessageArray[3]);
                                p_requestVO.setReqSelector("" + selectorValue);
                            }
                        }
                        p_transferVO.setTransferValue(amount);
                        p_transferVO.setRequestedAmount(amount);
                        incomingSmsStr.append(amount + " ");
                    }
                    break;
                }
            case 7:
                {
                    /*
                     * Actual!=default
                     * PRC HDFC Name Amount selector langCode PIN
                     * Actual=default
                     * PRC HDFC Name Amount selector langCode PIN(Update with
                     * new
                     * PIN)
                     */
                    if (i == 1) {
                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_INVALID_MESSAGEFORMAT, 0, new String[] { p_requestVO
                            .getActualMessageFormat() }, null);
                    } else {
                        // For uganda whether PIN validation required or not
                        if ((((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) && ((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.BUDDY_PIN_REQUIRED,
                            networkPrefixVO.getNetworkCode())).booleanValue()) {

                            if (!actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                                pin = p_requestMessageArray[6];
                                incomingSmsStr.append("****" + " ");
                                try {
                                    SubscriberBL.validatePIN(p_con, senderVO, pin);
                                } catch (BTSLBaseException be) {
                                    if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey()
                                        .equals(PretupsErrorCodesI.ERROR_SNDR_PINBLOCK)))) {
                                        p_con.commit();
                                    }
                                    throw be;
                                }

                                amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                                if (amount < 0) {
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                                }
                                try {
                                    if (!BTSLUtil.isNullString(p_requestMessageArray[5])) {
                                        final int localeValue = Integer.parseInt(p_requestMessageArray[5]);
                                        p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[5]));
                                        if (p_requestVO.getReceiverLocale() == null) {
                                            // by
                                            // ankit
                                            // zindal
                                            // 01/08/06
                                            // discussed
                                            // by
                                            // AC/GB
                                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                        }
                                    }
                                } catch (Exception e) {
                                    _log.errorTrace(METHOD_NAME, e);
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                }
                                if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                    final int selectorValue = Integer.parseInt(p_requestMessageArray[4]);
                                    p_requestVO.setReqSelector("" + selectorValue);
                                }
                            } else {
                                // To check whether PIN validation is required
                                // or
                                // not.
                                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) {
                                    pin = p_requestMessageArray[6];
                                    incomingSmsStr.append("****" + " ");
                                    BTSLUtil.validatePIN(pin);
                                    senderVO.setPin(BTSLUtil.encryptText(pin));
                                    senderVO.setPinUpdateReqd(true);
                                }

                                amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                                if (amount < 0) {
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                                }
                                try {
                                    if (!BTSLUtil.isNullString(p_requestMessageArray[5])) {
                                        final int localeValue = Integer.parseInt(p_requestMessageArray[5]);
                                        p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[5]));
                                        if (p_requestVO.getReceiverLocale() == null) {
                                            // by
                                            // ankit
                                            // zindal
                                            // 01/08/06
                                            // discussed
                                            // by
                                            // AC/GB
                                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                        }
                                    }
                                } catch (Exception e) {
                                    _log.errorTrace(METHOD_NAME, e);
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                }
                                if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                    final int selectorValue = Integer.parseInt(p_requestMessageArray[4]);
                                    p_requestVO.setReqSelector("" + selectorValue);
                                }
                            }
                            p_transferVO.setTransferValue(amount);
                            p_transferVO.setRequestedAmount(amount);
                            incomingSmsStr.append(amount + " ");

                        } else {
                            // For Uganda PIN validation required or not.
                            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) {
                                if (actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                                    pin = p_requestMessageArray[6];
                                    incomingSmsStr.append("****" + " ");
                                    BTSLUtil.validatePIN(pin);
                                    senderVO.setPin(BTSLUtil.encryptText(pin));
                                    senderVO.setPinUpdateReqd(true);
                                }
                            }
                            amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                            if (amount < 0) {
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                            }
                            try {
                                if (!BTSLUtil.isNullString(p_requestMessageArray[5])) {
                                    final int localeValue = Integer.parseInt(p_requestMessageArray[5]);
                                    p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[5]));
                                    if (p_requestVO.getReceiverLocale() == null) {
                                        // by
                                        // ankit
                                        // zindal
                                        // 01/08/06
                                        // discussed
                                        // by
                                        // AC/GB
                                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                    }
                                }
                            } catch (Exception e) {
                                _log.errorTrace(METHOD_NAME, e);
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                            }
                            if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                final int selectorValue = Integer.parseInt(p_requestMessageArray[4]);
                                p_requestVO.setReqSelector("" + selectorValue);
                            }

                            p_transferVO.setTransferValue(amount);
                            p_transferVO.setRequestedAmount(amount);
                            incomingSmsStr.append(amount + " ");
                        }
                    }
                    break;
                }

            default:
                {
                    throw new BTSLBaseException(this, "checkIfBuddy", PretupsErrorCodesI.P2P_INVALID_MESSAGEFORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() },
                        null);
                }

        }
        return true;

    }

}
