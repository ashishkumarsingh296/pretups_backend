package com.client.pretups.util.clientutils;

/*
 * MoldovaUtil.java
 * Name Date History
 * ------------------------------------------------------------------------
 * ved.sharma Aug 22, 2007 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2007 Bharti Telesoft Ltd.
 */

import java.sql.Connection;
import java.util.Calendar;

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
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.btsl.pretups.payment.businesslogic.PaymentMethodCache;
import com.btsl.pretups.payment.businesslogic.PaymentMethodKeywordVO;
import com.btsl.pretups.payment.businesslogic.ServicePaymentMappingCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;

public class MoldovaUtil extends OperatorUtil {
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
                 * hashvalue of pin sent by user.
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
     * Date : Aug 22, 2007
     * Discription :
     * Method : getReceiverConversionRate
     * 
     * @param p_senderInterfaceID
     * @param p_receiverInterfaceID
     * @throws BTSLBaseException
     * @return double
     * @author ved.sharma
     */
    /*
     * This code was commented on 01/04/08 to eliminate fetch conversion rate
     * step.
     * Now Moldova will support single currency. Previously conversion rate was
     * required to
     * support multiple currency for moldova.
     * 
     * public double getReceiverConversionRate(String p_senderInterfaceID,String
     * p_receiverInterfaceID) throws BTSLBaseException
     * {
     * if (_log.isDebugEnabled()) _log.debug("getReceiverConversionRate",
     * " Entered p_senderInterfaceID=:" +p_senderInterfaceID +
     * " p_receiverInterfaceID=" +p_receiverInterfaceID);
     * double conversionRate=1;
     * double senderConversionRateDouble=0;
     * double receiverConversionRateDouble=0;
     * try
     * {
     * String senderConversionRate=ConversionRateCache
     * .getConversionRate(p_senderInterfaceID);
     * try
     * {
     * senderConversionRateDouble=Double.parseDouble(senderConversionRate);
     * }
     * catch (Exception e)
     * {
     * _log.error("getReceiverConversionRate",
     * "senderConversionRate="+senderConversionRate+" Exception " +
     * e.getMessage());
     * e.printStackTrace();
     * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI
     * .
     * RAISED,EventLevelI.FATAL,"MoldovaUtil[getReceiverConversionRate]","","",""
     * ,"Exception:"+e.getMessage());
     * throw new BTSLBaseException("MoldovaUtil", "getReceiverConversionRate",
     * PretupsErrorCodesI.SENDER_CONVERSION_RATE_NOTFOUND);
     * }
     * String receiverConversionRate=ConversionRateCache
     * .getConversionRate(p_receiverInterfaceID);
     * try
     * {
     * receiverConversionRateDouble=Double.parseDouble(receiverConversionRate);
     * }
     * catch (Exception e)
     * {
     * _log.error("getReceiverConversionRate",
     * "receiverConversionRate="+receiverConversionRate
     * +"receiverConversionRate="+receiverConversionRate+" Exception " +
     * e.getMessage());
     * e.printStackTrace();
     * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI
     * .
     * RAISED,EventLevelI.FATAL,"MoldovaUtil[getReceiverConversionRate]","","",""
     * ,"Exception:"+e.getMessage());
     * throw new BTSLBaseException("MoldovaUtil", "getReceiverConversionRate",
     * PretupsErrorCodesI.RECEIVER_CONVERSION_RATE_NOTFOUND);
     * }
     * conversionRate=senderConversionRateDouble/receiverConversionRateDouble;
     * }
     * catch (BTSLBaseException be)
     * {
     * throw be;
     * }
     * catch (Exception e)
     * {
     * _log.error("getReceiverConversionRate",
     * "senderConversionRateDouble="+senderConversionRateDouble
     * +",receiverConversionRateDouble="
     * +receiverConversionRateDouble+"Exception " + e.getMessage());
     * e.printStackTrace();
     * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI
     * .
     * RAISED,EventLevelI.FATAL,"MoldovaUtil[getReceiverConversionRate]","","",""
     * ,"Exception:"+e.getMessage());
     * throw new BTSLBaseException("MoldovaUtil", "getReceiverConversionRate",
     * PretupsErrorCodesI.ERROR_EXCEPTION);
     * }
     * if (_log.isDebugEnabled()) _log.debug("getReceiverConversionRate",
     * " Exiting p_senderInterfaceID=:" +p_senderInterfaceID +
     * " p_receiverInterfaceID="
     * +p_receiverInterfaceID+" conversionRate="+conversionRate);
     * return conversionRate;
     * }
     */

    /**
     * Date : Aug 23, 2007
     * Discription :
     * Method : getReceiverConversionRate
     * 
     * @param p_receiverInterfaceID
     * @throws BTSLBaseException
     * @return double
     * @author ved.sharma
     */
    /*
     * This code was commented on 01/04/08 to eliminate fetch conversion rate
     * step.
     * Now Moldova will support single currency. Previously conversion rate was
     * required to
     * support multiple currency for moldova.
     * 
     * public double getReceiverConversionRate(String p_receiverInterfaceID)
     * throws BTSLBaseException
     * {
     * if (_log.isDebugEnabled()) _log.debug("getReceiverConversionRate",
     * " Entered  p_receiverInterfaceID=" +p_receiverInterfaceID);
     * double conversionRate=1;
     * double receiverConversionRateDouble=0;
     * try
     * {
     * 
     * String receiverConversionRate=ConversionRateCache
     * .getConversionRate(p_receiverInterfaceID);
     * try
     * {
     * receiverConversionRateDouble=Double.parseDouble(receiverConversionRate);
     * }
     * catch (Exception e)
     * {
     * _log.error("getReceiverConversionRate",
     * "receiverConversionRate="+receiverConversionRate
     * +"receiverConversionRate="+receiverConversionRate+" Exception " +
     * e.getMessage());
     * e.printStackTrace();
     * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI
     * .
     * RAISED,EventLevelI.FATAL,"MoldovaUtil[getReceiverConversionRate]","","",""
     * ,"Exception:"+e.getMessage());
     * throw new BTSLBaseException("MoldovaUtil", "getReceiverConversionRate",
     * PretupsErrorCodesI.RECEIVER_CONVERSION_RATE_NOTFOUND);
     * }
     * conversionRate=receiverConversionRateDouble;
     * }
     * catch (BTSLBaseException be)
     * {
     * throw be;
     * }
     * catch (Exception e)
     * {
     * _log.error("getReceiverConversionRate",
     * "receiverConversionRateDouble="+receiverConversionRateDouble+"Exception "
     * + e.getMessage());
     * e.printStackTrace();
     * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI
     * .
     * RAISED,EventLevelI.FATAL,"MoldovaUtil[getReceiverConversionRate]","","",""
     * ,"Exception:"+e.getMessage());
     * throw new BTSLBaseException("MoldovaUtil", "getReceiverConversionRate",
     * PretupsErrorCodesI.ERROR_EXCEPTION);
     * }
     * if (_log.isDebugEnabled()) _log.debug("getReceiverConversionRate",
     * " Exiting  p_receiverInterfaceID="
     * +p_receiverInterfaceID+" conversionRate="+conversionRate);
     * return conversionRate;
     * }
     */

    /**
     * Date : Sep 28, 2007
     * Discription :Method to validate the request for Non Buddy transfers
     * Method : validateIfNotBuddy
     * 
     * @param p_con
     * @param p_requestVO
     * @param p_transferVO
     * @throws BTSLBaseException
     * @throws Exception
     * @return void
     * @author ved.sharma
     */
    private void validateIfNotBuddy(Connection p_con, RequestVO p_requestVO, TransferVO p_transferVO) throws BTSLBaseException, Exception {
        final String[] requestMessageArray = p_requestVO.getRequestMessageArray();
        final String METHOD_NAME = "validateIfNotBuddy";
        if (_log.isDebugEnabled()) {
            _log.debug("validateIfNotBuddy", " requestMessageArray length:" + requestMessageArray);
        }
        if (requestMessageArray.length < 4 || requestMessageArray.length > 7) {
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
                    if (actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                        throw new BTSLBaseException(this, "validateIfNotBuddy", PretupsErrorCodesI.CHNLUSR_CHANGE_DEFAULT_PIN, 0, null, null);
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
                    // Validate 4nd Argument for PIN.
                    pin = requestMessageArray[3];

                    incomingSmsStr.append("****" + " ");
                    try {
                        BTSLUtil.validatePIN(pin);
                        SubscriberBL.validatePIN(p_con, senderVO, pin);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey()
                            .equals(PretupsErrorCodesI.ERROR_SNDR_PINBLOCK)))) {
                            p_con.commit();
                        }
                        throw be;
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

                    // Validate 5nd Argument for PIN.
                    pin = requestMessageArray[4];
                    incomingSmsStr.append("****" + " ");
                    try {
                        BTSLUtil.validatePIN(pin);
                        SubscriberBL.validatePIN(p_con, senderVO, pin);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey()
                            .equals(PretupsErrorCodesI.ERROR_SNDR_PINBLOCK)))) {
                            p_con.commit();
                        }
                        throw be;
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
                    try {
                        BTSLUtil.validatePIN(pin);
                        SubscriberBL.validatePIN(p_con, senderVO, pin);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey()
                            .equals(PretupsErrorCodesI.ERROR_SNDR_PINBLOCK)))) {
                            p_con.commit();
                        }
                        throw be;
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
                    try {
                        BTSLUtil.validatePIN(pin);
                        SubscriberBL.validatePIN(p_con, senderVO, pin);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey()
                            .equals(PretupsErrorCodesI.ERROR_SNDR_PINBLOCK)))) {
                            p_con.commit();
                        }
                        throw be;
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
}
