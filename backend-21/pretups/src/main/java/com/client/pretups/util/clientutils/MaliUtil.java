/*
 * Created on Feb 28, 2008
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.client.pretups.util.clientutils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDetailsVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.btsl.pretups.payment.businesslogic.PaymentMethodCache;
import com.btsl.pretups.payment.businesslogic.PaymentMethodKeywordVO;
import com.btsl.pretups.payment.businesslogic.ServicePaymentMappingCache;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/**
 * @author santanu.mohanty
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class MaliUtil extends OperatorUtil {
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * This method used for pin validation.
     * While creating or modifying the user PIN This method will be used.
     * Method validatePIN.
     * 
     * @author santanu.kumar
     * @created on 28/02/08
     * @param p_pin
     *            String
     * @return HashMap
     */
    /*
     * public HashMap pinValidate(String p_pin)
     * {
     * if(_log.isDebugEnabled())_log.debug("validatePIN","Entered, PIN= "+p_pin);
     * HashMap messageMap=new HashMap();
     * 
     * String defaultPin = BTSLUtil.getDefaultPasswordNumeric(p_pin);
     * if(defaultPin.equals(p_pin))
     * return messageMap;
     * 
     * defaultPin = BTSLUtil.getDefaultPasswordText(p_pin);
     * if(defaultPin.equals(p_pin))
     * return messageMap;
     * 
     * if(!BTSLUtil.isNumeric(p_pin))
     * messageMap.put("operatorutil.validatepin.error.pinnotnumeric",null);
     * if (p_pin.length() < SystemPreferences.MIN_SMS_PIN_LENGTH ||
     * p_pin.length() > SystemPreferences.MAX_SMS_PIN_LENGTH)
     * {
     * String[]
     * args={String.valueOf(SystemPreferences.MIN_SMS_PIN_LENGTH),String
     * .valueOf(SystemPreferences.MAX_SMS_PIN_LENGTH)};
     * messageMap.put("operatorutil.validatepin.error.smspinlenerr", args);
     * }
     * if(_log.isDebugEnabled())
     * _log.debug("validatePIN","Exiting messageMap.size()="+messageMap.size());
     * return messageMap;
     * }
     */

    /**
     * 
     * This method validatePINRules the requested PIN business rules
     * 
     * @param p_requestPin
     * @throws BTSLBaseException
     * @author santanu.mohanty
     */
    public void validatePINRules(String p_requestPin) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("validatePINRules", " MaliUtill Entered p_transferVO:" + p_requestPin);
        }

        if (BTSLUtil.isNullString(p_requestPin)) {
            throw new BTSLBaseException("BTSLUtil", "validatePIN", PretupsErrorCodesI.PIN_INVALID);
        } else if (!BTSLUtil.isNumeric(p_requestPin)) {
            throw new BTSLBaseException("BTSLUtil", "validatePIN", PretupsErrorCodesI.NEWPIN_NOTNUMERIC);
        } else if (p_requestPin.length() < SystemPreferences.MIN_SMS_PIN_LENGTH || p_requestPin.length() > SystemPreferences.MAX_SMS_PIN_LENGTH) {
            final String msg[] = { String.valueOf(SystemPreferences.MIN_SMS_PIN_LENGTH), String.valueOf(SystemPreferences.MAX_SMS_PIN_LENGTH) };
            throw new BTSLBaseException("BTSLUtil", "validatePIN", PretupsErrorCodesI.PIN_LENGTHINVALID, 0, msg, null);
        }

        if (_log.isDebugEnabled()) {
            _log.debug("validatePINRules", " MaliUtill Exiting :::");
        }
    }

    public void handleConfirmTransferMessageFormat(RequestVO p_requestVO, TransferVO p_transferVO) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("handleConfirmTransferMessageFormat", "Entered p_requestVO=" + p_requestVO + " p_transferVO=" + p_transferVO);// requestMessageArray
        }

        try {

            final String[] requestMessageArray = p_requestVO.getRequestMessageArray();
            if (_log.isDebugEnabled()) {
                _log.debug("handleConfirmTransferMessageFormat", " requestMessageArray length:" + requestMessageArray);
            }
            if (requestMessageArray.length < 3 || requestMessageArray.length > 7) {
                throw new BTSLBaseException(this, "handleConfirmTransferMessageFormat", PretupsErrorCodesI.P2P_INVALID_MESSAGEFORMAT, 0, new String[] { p_requestVO
                    .getActualMessageFormat() }, null);
            }

            final int messageLength = requestMessageArray.length;
            String pin = null;
            long amount = 0;
            String receiverMSISDN = null;

            switch (messageLength) {
                case 3:
                    {
                        receiverMSISDN = requestMessageArray[1];
                        amount = PretupsBL.getSystemAmount(requestMessageArray[2]);
                        break;
                    }
                case 4:
                    {
                        pin = requestMessageArray[3];
                        receiverMSISDN = requestMessageArray[1];
                        amount = PretupsBL.getSystemAmount(requestMessageArray[2]);
                        break;
                    }
                case 5:
                    {
                        // Validate 2nd Argument for PIN.
                        pin = requestMessageArray[4];
                        receiverMSISDN = requestMessageArray[2];
                        amount = PretupsBL.getSystemAmount(requestMessageArray[3]);
                        break;
                    }
                case 6:
                    {
                        // Validate 2nd Argument for PIN.
                        pin = requestMessageArray[5];
                        receiverMSISDN = requestMessageArray[2];
                        amount = PretupsBL.getSystemAmount(requestMessageArray[3]);
                        break;
                    }
                case 7:
                    {
                        // Validate 2nd Argument for PIN.
                        pin = requestMessageArray[6];
                        receiverMSISDN = requestMessageArray[2];
                        amount = PretupsBL.getSystemAmount(requestMessageArray[3]);
                        break;
                    }
            }

            if (messageLength != 3) {

               
                this.validatePINRules(pin);
            }
            receiverMSISDN = PretupsBL.getFilteredMSISDN(receiverMSISDN);
            if (!BTSLUtil.isValidMSISDN(receiverMSISDN)) {
                throw new BTSLBaseException(this, "handleConfirmTransferMessageFormat", PretupsErrorCodesI.ERROR_INVALID_MSISDN, 0, new String[] { receiverMSISDN }, null);
            }
            final ReceiverVO _receiverVO = new ReceiverVO();
            _receiverVO.setMsisdn(receiverMSISDN);
            final NetworkPrefixVO networkPrefixVO = PretupsBL.getNetworkDetails(receiverMSISDN, PretupsI.USER_TYPE_RECEIVER);
            if (networkPrefixVO == null) {
                throw new BTSLBaseException(this, "handleConfirmTransferMessageFormat", PretupsErrorCodesI.ERROR_NOTFOUND_RECEIVERNETWORK, 0, new String[] { receiverMSISDN },
                    null);
            }
            _receiverVO.setNetworkCode(networkPrefixVO.getNetworkCode());
            _receiverVO.setPrefixID(networkPrefixVO.getPrefixID());
            _receiverVO.setSubscriberType(networkPrefixVO.getSeriesType());
            p_transferVO.setReceiverVO(_receiverVO);

            if (amount < 0) {
                throw new BTSLBaseException(this, "handleConfirmTransferMessageFormat", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
            }
            p_transferVO.setTransferValue(amount);
            p_transferVO.setRequestedAmount(amount);

        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            throw e;
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("handleConfirmTransferMessageFormat", "Exiting...");
            }
        }
    }

    /**
     * Method validatePassword.
     * 
     * @author santanu.mohanty
     * @created on 12/07/07
     * @param p_loginID
     *            String
     * @param p_password
     *            String
     * @return String
     */
    // public HashMap validatePassword(String p_loginID, String p_password)
    // {
    // _log.debug("validatePassword","Entered, p_userID= ",new
    // String(p_loginID+", Password= "+p_password));
    // HashMap messageMap=new HashMap();
    // String defaultPin = BTSLUtil.getDefaultPasswordNumeric(p_password);
    // if(defaultPin.equals(p_password))
    // return messageMap;
    // defaultPin = BTSLUtil.getDefaultPasswordText(p_password);
    // if(defaultPin.equals(p_password))
    // return messageMap;
    // if (p_password.length() < SystemPreferences.MIN_LOGIN_PWD_LENGTH ||
    // p_password.length() > SystemPreferences.MAX_LOGIN_PWD_LENGTH)
    // {
    // String[]
    // args={String.valueOf(SystemPreferences.MIN_LOGIN_PWD_LENGTH),String.valueOf(SystemPreferences.MAX_LOGIN_PWD_LENGTH)};
    // messageMap.put("operatorutil.validatepassword.error.passwordlenerr",
    // args);
    // }
    /*
     * int result=BTSLUtil.isSMSPinValid(p_password);//for consecutive and same
     * characters
     * if(result==-1)
     * messageMap.put("operatorutil.validatepassword.error.passwordsamedigit",null
     * );
     * else if(result==1)
     * messageMap.put("operatorutil.validatepassword.error.passwordconsecutive",null
     * );
     */
    /*
     * if(!BTSLUtil.containsChar(p_password))
     * messageMap.put("operatorutil.validatepassword.error.passwordnotcontainschar"
     * ,null);
     * // for special character
     * String
     * specialChar=Constants.getProperty("SPECIAL_CHARACTER_PASSWORD_VALIDATION"
     * );
     * if(!BTSLUtil.isNullString(specialChar))
     * {
     * String[] specialCharArray={specialChar};
     * String[] passwordCharArray=specialChar.split(",");
     * boolean specialCharFlag=false;
     * for(int i=0,j=passwordCharArray.length;i<j;i++)
     * {
     * if(p_password.contains(passwordCharArray[i]))
     * {
     * specialCharFlag=true;
     * break;
     * }
     * }
     * //if(!specialCharFlag)
     * //messageMap.put("operatorutil.validatepassword.error.passwordspecialchar"
     * ,specialCharArray);
     * if(specialCharFlag)
     * messageMap.put(
     * "operatorutil.validatepassword.error.passwordspecialchar.notallowed"
     * ,specialCharArray);
     * }
     * // for number
     * String[]passwordNumberStrArray={"0","1","2","3","4","5","6","7","8","9"};
     * boolean numberStrFlag=false;
     * for(int i=0,j=passwordNumberStrArray.length;i<j;i++)
     * {
     * if(p_password.contains(passwordNumberStrArray[i]))
     * {
     * numberStrFlag=true;
     * break;
     * }
     * }
     * if(!numberStrFlag)
     * messageMap.put("operatorutil.validatepassword.error.passwordnumberchar",null
     * );
     */
    // if(p_loginID.equals(p_password))
    // messageMap.put("operatorutil.validatepassword.error.sameusernamepassword",null);
    // if(_log.isDebugEnabled()) _log.debug("validatePassword","Exiting ");
    // return messageMap;
    // }

    /**
     * Method to validate the request for Non Buddy transfers
     * 
     * @param p_con
     * @param p_senderVO
     * @throws BTSLBaseException
     * @throws Exception
     */
    private void validateIfNotBuddy(Connection p_con, RequestVO p_requestVO, TransferVO p_transferVO) throws BTSLBaseException, Exception {
        final String methodName = "validateIfNotBuddy";
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
                    if (!actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                        throw new BTSLBaseException(this, "validateIfNotBuddy", PretupsErrorCodesI.P2P_INVALID_MESSAGEFORMAT, 0, new String[] { p_requestVO
                            .getActualMessageFormat() }, null);
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

                    if (actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                        if (!BTSLUtil.isNullString(requestMessageArray[3])) {
                            if (BTSLUtil.isNumeric(requestMessageArray[3]) && requestMessageArray[3].length() == 1) {
                                p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(requestMessageArray[3]));
                                if (p_requestVO.getReceiverLocale() == null) {
                                    p_requestVO.setReceiverLocale(new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                                }
                            } else {
                                if (!BTSLUtil.isNullString(pin) && !pin.equals((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {

                                    this.validatePINRules(pin);
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
                    

                    if (actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {

                        if (!BTSLUtil.isNullString(pin) && !pin.equals((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {

                            this.validatePINRules(pin);
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

                            if (!BTSLUtil.isNullString(requestMessageArray[3])) {

                                p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(requestMessageArray[3]));
                                if (p_requestVO.getReceiverLocale() == null) {
                                    // by ankit
                                    // zindal
                                    // 01/08/06
                                    // discussed
                                    // by AC/GB
                                    throw new BTSLBaseException(this, "validateIfNotBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);


                                }

                            }
                        } catch (Exception e) {
                            _log.errorTrace(methodName, e);
                            throw new BTSLBaseException(this, "validateIfNotBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                        }
                    } else {
                        paymentMethodType = paymentMethodKeywordVO.getPaymentMethodType();
                        p_transferVO.setPaymentMethodType(paymentMethodType);
                        p_transferVO.setPaymentMethodKeywordVO(paymentMethodKeywordVO);
                        p_transferVO.setDefaultPaymentMethod(PretupsI.NO);
                        incomingSmsStr.append(paymentMethodType + " ");
                        checkAfterPaymentMethod(p_con, 2, requestMessageArray, incomingSmsStr, p_transferVO);

                    }

                    break;
                }
            case 6:
                {
                    // Validate 2nd Argument for PIN.
                    pin = requestMessageArray[5];
                    incomingSmsStr.append("****" + " ");
                    // if pin Invalid return with error(PIN is Mandatory)

                    if (actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {

                        if (!BTSLUtil.isNullString(pin) && !pin.equals((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {

                            this.validatePINRules(pin);
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
                        _log.errorTrace(methodName, e);
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
                        _log.errorTrace(methodName, e);
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
                   
                    if (actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {

                        if (!BTSLUtil.isNullString(pin) && !pin.equals((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {

                            this.validatePINRules(pin);
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
                            _log.errorTrace(methodName, e);
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
                            _log.errorTrace(methodName, e);
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
    /*
     * private boolean checkAfterPaymentMethodForBuddy(Connection p_con,int i,
     * String[] p_requestMessageArray, StringBuffer incomingSmsStr, TransferVO
     * p_transferVO, RequestVO p_requestVO) throws BTSLBaseException,Exception
     * {
     * if(_log.isDebugEnabled())
     * _log.debug("checkAfterPaymentMethodForBuddy"," i="+i+
     * " requestMessageArray length:"+p_requestMessageArray.length+" i="+i);
     * int incReq=0;
     * if(i==2)
     * incReq=1;
     * String receiverMSISDN_NAME=p_requestMessageArray[1+incReq];
     * BuddyVO buddyVO=new
     * SubscriberDAO().loadBuddyDetails(p_con,((SenderVO)p_transferVO
     * .getSenderVO()).getUserID(),receiverMSISDN_NAME);
     * if(buddyVO==null)
     * {
     * return false;
     * }
     * String receiverMSISDN=buddyVO.getMsisdn();
     * NetworkPrefixVO
     * networkPrefixVO=PretupsBL.getNetworkDetails(receiverMSISDN
     * ,PretupsI.USER_TYPE_RECEIVER);
     * if(networkPrefixVO==null)
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .ERROR_NOTFOUND_RECEIVERNETWORK,0,new String[]{receiverMSISDN},null);
     * buddyVO.setNetworkCode(networkPrefixVO.getNetworkCode());
     * buddyVO.setPrefixID(networkPrefixVO.getPrefixID());
     * buddyVO.setSubscriberType(networkPrefixVO.getSeriesType());
     * p_transferVO.setReceiverVO(buddyVO);
     * incomingSmsStr.append(receiverMSISDN_NAME+" ");
     * int messageLength=p_requestMessageArray.length;
     * String pin=null;
     * long amount=0;
     * SenderVO senderVO=(SenderVO)p_transferVO.getSenderVO();
     * String actualPin=BTSLUtil.decryptText(senderVO.getPin());
     * 
     * /*Message format that are supported are as:
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
    /*
     * switch(messageLength)
     * {
     * case 2:
     * {
     * if(((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.
     * BUDDY_PIN_REQUIRED,networkPrefixVO.getNetworkCode())).booleanValue())
     * {
     * if(!actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .P2P_INVALID_MESSAGEFORMAT,0,new
     * String[]{p_requestVO.getActualMessageFormat()},null);
     * }
     * amount=buddyVO.getPreferredAmount();
     * if(amount<0)
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .P2P_ERROR_AMOUNT_LESSZERO);
     * p_transferVO.setTransferValue(amount);
     * p_transferVO.setRequestedAmount(amount);
     * incomingSmsStr.append(amount+" ");
     * break;
     * }
     * case 3:
     * {
     * if((((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.
     * BUDDY_PIN_REQUIRED
     * ,networkPrefixVO.getNetworkCode())).booleanValue())&&!actualPin
     * .equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))
     * {
     * if(i==2)
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .P2P_INVALID_MESSAGEFORMAT,0,new
     * String[]{p_requestVO.getActualMessageFormat()},null);
     * else
     * {
     * pin=p_requestMessageArray[2];
     * incomingSmsStr.append("****"+" ");
     * try
     * {
     * SubscriberBL.validatePIN(p_con,senderVO,pin);
     * }
     * catch(BTSLBaseException be)
     * {
     * if(be.isKey() &&
     * ((be.getMessageKey().equals(PretupsErrorCodesI.ERROR_INVALID_PIN)) ||
     * (be.getMessageKey().equals(PretupsErrorCodesI.ERROR_SNDR_PINBLOCK))))
     * p_con.commit();
     * throw be;
     * }
     * amount=buddyVO.getPreferredAmount();
     * if(amount<0)
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .P2P_ERROR_AMOUNT_LESSZERO);
     * p_transferVO.setTransferValue(amount);
     * p_transferVO.setRequestedAmount(amount);
     * incomingSmsStr.append(amount+" ");
     * }
     * }
     * else
     * {
     * if(i==2)
     * {
     * amount=buddyVO.getPreferredAmount();
     * if(amount<0)
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .P2P_ERROR_AMOUNT_LESSZERO);
     * }
     * else
     * {
     * amount=PretupsBL.getSystemAmount(p_requestMessageArray[2]);
     * if(amount<0)
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .P2P_ERROR_AMOUNT_LESSZERO);
     * }
     * p_transferVO.setTransferValue(amount);
     * p_transferVO.setRequestedAmount(amount);
     * incomingSmsStr.append(amount+" ");
     * }
     * break;
     * }
     * case 4:
     * {
     * if(((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.
     * BUDDY_PIN_REQUIRED,networkPrefixVO.getNetworkCode())).booleanValue())
     * {
     * if(!actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))
     * {
     * pin=p_requestMessageArray[3];
     * incomingSmsStr.append("****"+" ");
     * try
     * {
     * SubscriberBL.validatePIN(p_con,senderVO,pin);
     * }
     * catch(BTSLBaseException be)
     * {
     * if(be.isKey() &&
     * ((be.getMessageKey().equals(PretupsErrorCodesI.ERROR_INVALID_PIN)) ||
     * (be.getMessageKey().equals(PretupsErrorCodesI.ERROR_SNDR_PINBLOCK))))
     * p_con.commit();
     * throw be;
     * }
     * if(i==2)
     * {
     * amount=buddyVO.getPreferredAmount();
     * if(amount<0)
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .P2P_ERROR_AMOUNT_LESSZERO);
     * }
     * else
     * {
     * amount=PretupsBL.getSystemAmount(p_requestMessageArray[2]);
     * if(amount<0)
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .P2P_ERROR_AMOUNT_LESSZERO);
     * 
     * }
     * }
     * else
     * {
     * if(i==2)
     * {
     * amount=PretupsBL.getSystemAmount(p_requestMessageArray[3]);
     * if(amount<0)
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .P2P_ERROR_AMOUNT_LESSZERO);
     * }
     * else
     * {
     * amount=PretupsBL.getSystemAmount(p_requestMessageArray[2]);
     * if(amount<0)
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .P2P_ERROR_AMOUNT_LESSZERO);
     * try
     * {
     * if(!BTSLUtil.isNullString(p_requestMessageArray[3]))
     * {
     * int localeValue=Integer.parseInt(p_requestMessageArray[3]);
     * p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(
     * p_requestMessageArray[3]));
     * if(p_requestVO.getReceiverLocale()==null)//changed by ankit zindal
     * 01/08/06 discussed by AC/GB
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .ERROR_INVALID_LANGUAGE_SEL_VALUE);
     * }
     * }
     * catch(Exception e)
     * {
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .ERROR_INVALID_LANGUAGE_SEL_VALUE);
     * }
     * }
     * }
     * p_transferVO.setTransferValue(amount);
     * p_transferVO.setRequestedAmount(amount);
     * incomingSmsStr.append(amount+" ");
     * }
     * else
     * {
     * if(i==2)
     * {
     * amount=PretupsBL.getSystemAmount(p_requestMessageArray[3]);
     * if(amount<0)
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .P2P_ERROR_AMOUNT_LESSZERO);
     * }
     * else
     * {
     * amount=PretupsBL.getSystemAmount(p_requestMessageArray[2]);
     * if(amount<0)
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .P2P_ERROR_AMOUNT_LESSZERO);
     * try
     * {
     * if(!BTSLUtil.isNullString(p_requestMessageArray[3]))
     * {
     * int localeValue=Integer.parseInt(p_requestMessageArray[3]);
     * p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(
     * p_requestMessageArray[3]));
     * if(p_requestVO.getReceiverLocale()==null)//changed by ankit zindal
     * 01/08/06 discussed by AC/GB
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .ERROR_INVALID_LANGUAGE_SEL_VALUE);
     * }
     * }
     * catch(Exception e)
     * {
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .ERROR_INVALID_LANGUAGE_SEL_VALUE);
     * }
     * }
     * p_transferVO.setTransferValue(amount);
     * p_transferVO.setRequestedAmount(amount);
     * incomingSmsStr.append(amount+" ");
     * }
     * break;
     * }
     * case 5:
     * {
     * if(((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.
     * BUDDY_PIN_REQUIRED,networkPrefixVO.getNetworkCode())).booleanValue())
     * {
     * if(!actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))
     * {
     * pin=p_requestMessageArray[4];
     * incomingSmsStr.append("****"+" ");
     * try
     * {
     * SubscriberBL.validatePIN(p_con,senderVO,pin);
     * }
     * catch(BTSLBaseException be)
     * {
     * if(be.isKey() &&
     * ((be.getMessageKey().equals(PretupsErrorCodesI.ERROR_INVALID_PIN)) ||
     * (be.getMessageKey().equals(PretupsErrorCodesI.ERROR_SNDR_PINBLOCK))))
     * p_con.commit();
     * throw be;
     * }
     * if(i==2)
     * {
     * amount=PretupsBL.getSystemAmount(p_requestMessageArray[3]);
     * if(amount<0)
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .P2P_ERROR_AMOUNT_LESSZERO);
     * }
     * else
     * {
     * amount=PretupsBL.getSystemAmount(p_requestMessageArray[2]);
     * if(amount<0)
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .P2P_ERROR_AMOUNT_LESSZERO);
     * try
     * {
     * if(!BTSLUtil.isNullString(p_requestMessageArray[3]))
     * {
     * int localeValue=Integer.parseInt(p_requestMessageArray[3]);
     * p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(
     * p_requestMessageArray[3]));
     * if(p_requestVO.getReceiverLocale()==null)//changed by ankit zindal
     * 01/08/06 discussed by AC/GB
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .ERROR_INVALID_LANGUAGE_SEL_VALUE);
     * }
     * }
     * catch(Exception e)
     * {
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .ERROR_INVALID_LANGUAGE_SEL_VALUE);
     * }
     * }
     * p_transferVO.setTransferValue(amount);
     * p_transferVO.setRequestedAmount(amount);
     * incomingSmsStr.append(amount+" ");
     * }
     * else
     * {
     * if(i==2)
     * {
     * amount=PretupsBL.getSystemAmount(p_requestMessageArray[3]);
     * if(amount<0)
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .P2P_ERROR_AMOUNT_LESSZERO);
     * try
     * {
     * if(!BTSLUtil.isNullString(p_requestMessageArray[4]))
     * {
     * int localeValue=Integer.parseInt(p_requestMessageArray[4]);
     * p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(
     * p_requestMessageArray[4]));
     * if(p_requestVO.getReceiverLocale()==null)//changed by ankit zindal
     * 01/08/06 discussed by AC/GB
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .ERROR_INVALID_LANGUAGE_SEL_VALUE);
     * }
     * }
     * catch(Exception e)
     * {
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .ERROR_INVALID_LANGUAGE_SEL_VALUE);
     * }
     * }
     * else
     * {
     * amount=PretupsBL.getSystemAmount(p_requestMessageArray[2]);
     * if(amount<0)
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .P2P_ERROR_AMOUNT_LESSZERO);
     * 
     * 
     * try
     * {
     * if(!BTSLUtil.isNullString(p_requestMessageArray[4]))
     * {
     * int localeValue=Integer.parseInt(p_requestMessageArray[4]);
     * p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(
     * p_requestMessageArray[4]));
     * if(p_requestVO.getReceiverLocale()==null)//changed by ankit zindal
     * 01/08/06 discussed by AC/GB
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .ERROR_INVALID_LANGUAGE_SEL_VALUE);
     * }
     * }
     * catch(Exception e)
     * {
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .ERROR_INVALID_LANGUAGE_SEL_VALUE);
     * }
     * if(!BTSLUtil.isNullString(p_requestMessageArray[3]))
     * {
     * int selectorValue=Integer.parseInt(p_requestMessageArray[3]);
     * p_requestVO.setReqSelector(""+selectorValue);
     * }
     * }
     * p_transferVO.setTransferValue(amount);
     * p_transferVO.setRequestedAmount(amount);
     * incomingSmsStr.append(amount+" ");
     * }
     * }
     * else
     * {
     * if(i==2)
     * {
     * amount=PretupsBL.getSystemAmount(p_requestMessageArray[3]);
     * if(amount<0)
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .P2P_ERROR_AMOUNT_LESSZERO);
     * try
     * {
     * if(!BTSLUtil.isNullString(p_requestMessageArray[4]))
     * {
     * int localeValue=Integer.parseInt(p_requestMessageArray[4]);
     * p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(
     * p_requestMessageArray[4]));
     * if(p_requestVO.getReceiverLocale()==null)//changed by ankit zindal
     * 01/08/06 discussed by AC/GB
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .ERROR_INVALID_LANGUAGE_SEL_VALUE);
     * }
     * }
     * catch(Exception e)
     * {
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .ERROR_INVALID_LANGUAGE_SEL_VALUE);
     * }
     * }
     * else
     * {
     * amount=PretupsBL.getSystemAmount(p_requestMessageArray[2]);
     * if(amount<0)
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .P2P_ERROR_AMOUNT_LESSZERO);
     * try
     * {
     * if(!BTSLUtil.isNullString(p_requestMessageArray[4]))
     * {
     * int localeValue=Integer.parseInt(p_requestMessageArray[4]);
     * p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(
     * p_requestMessageArray[4]));
     * if(p_requestVO.getReceiverLocale()==null)//changed by ankit zindal
     * 01/08/06 discussed by AC/GB
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .ERROR_INVALID_LANGUAGE_SEL_VALUE);
     * }
     * }
     * catch(Exception e)
     * {
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .ERROR_INVALID_LANGUAGE_SEL_VALUE);
     * }
     * if(!BTSLUtil.isNullString(p_requestMessageArray[3]))
     * {
     * int selectorValue=Integer.parseInt(p_requestMessageArray[3]);
     * p_requestVO.setReqSelector(""+selectorValue);
     * }
     * }
     * p_transferVO.setTransferValue(amount);
     * p_transferVO.setRequestedAmount(amount);
     * incomingSmsStr.append(amount+" ");
     * }
     * break;
     * }
     * case 6:
     * {
     * if(((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.
     * BUDDY_PIN_REQUIRED,networkPrefixVO.getNetworkCode())).booleanValue())
     * {
     * if(!actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))
     * {
     * pin=p_requestMessageArray[5];
     * incomingSmsStr.append("****"+" ");
     * try
     * {
     * SubscriberBL.validatePIN(p_con,senderVO,pin);
     * }
     * catch(BTSLBaseException be)
     * {
     * if(be.isKey() &&
     * ((be.getMessageKey().equals(PretupsErrorCodesI.ERROR_INVALID_PIN)) ||
     * (be.getMessageKey().equals(PretupsErrorCodesI.ERROR_SNDR_PINBLOCK))))
     * p_con.commit();
     * throw be;
     * }
     * if(i==2)
     * {
     * amount=PretupsBL.getSystemAmount(p_requestMessageArray[3]);
     * if(amount<0)
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .P2P_ERROR_AMOUNT_LESSZERO);
     * try
     * {
     * if(!BTSLUtil.isNullString(p_requestMessageArray[4]))
     * {
     * int localeValue=Integer.parseInt(p_requestMessageArray[4]);
     * p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(
     * p_requestMessageArray[4]));
     * if(p_requestVO.getReceiverLocale()==null)//changed by ankit zindal
     * 01/08/06 discussed by AC/GB
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .ERROR_INVALID_LANGUAGE_SEL_VALUE);
     * }
     * }
     * catch(Exception e)
     * {
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .ERROR_INVALID_LANGUAGE_SEL_VALUE);
     * }
     * }
     * else
     * {
     * amount=PretupsBL.getSystemAmount(p_requestMessageArray[2]);
     * if(amount<0)
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .P2P_ERROR_AMOUNT_LESSZERO);
     * try
     * {
     * if(!BTSLUtil.isNullString(p_requestMessageArray[4]))
     * {
     * int localeValue=Integer.parseInt(p_requestMessageArray[4]);
     * p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(
     * p_requestMessageArray[4]));
     * if(p_requestVO.getReceiverLocale()==null)//changed by ankit zindal
     * 01/08/06 discussed by AC/GB
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .ERROR_INVALID_LANGUAGE_SEL_VALUE);
     * }
     * }
     * catch(Exception e)
     * {
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .ERROR_INVALID_LANGUAGE_SEL_VALUE);
     * }
     * if(!BTSLUtil.isNullString(p_requestMessageArray[3]))
     * {
     * int selectorValue=Integer.parseInt(p_requestMessageArray[3]);
     * p_requestVO.setReqSelector(""+selectorValue);
     * }
     * }
     * }
     * else
     * {
     * if(i==2)
     * {
     * amount=PretupsBL.getSystemAmount(p_requestMessageArray[3]);
     * if(amount<0)
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .P2P_ERROR_AMOUNT_LESSZERO);
     * try
     * {
     * if(!BTSLUtil.isNullString(p_requestMessageArray[5]))
     * {
     * int localeValue=Integer.parseInt(p_requestMessageArray[5]);
     * p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(
     * p_requestMessageArray[5]));
     * if(p_requestVO.getReceiverLocale()==null)//changed by ankit zindal
     * 01/08/06 discussed by AC/GB
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .ERROR_INVALID_LANGUAGE_SEL_VALUE);
     * }
     * }
     * catch(Exception e)
     * {
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .ERROR_INVALID_LANGUAGE_SEL_VALUE);
     * }
     * if(!BTSLUtil.isNullString(p_requestMessageArray[4]))
     * {
     * int selectorValue=Integer.parseInt(p_requestMessageArray[4]);
     * p_requestVO.setReqSelector(""+selectorValue);
     * }
     * }
     * else
     * {
     * pin=p_requestMessageArray[5];
     * incomingSmsStr.append("****"+" ");
     * //BTSLUtil.validatePIN(pin);
     * this.validatePINRules(pin);
     * senderVO.setPin(BTSLUtil.encryptText(pin));
     * senderVO.setPinUpdateReqd(true);
     * 
     * amount=PretupsBL.getSystemAmount(p_requestMessageArray[2]);
     * if(amount<0)
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .P2P_ERROR_AMOUNT_LESSZERO);
     * try
     * {
     * if(!BTSLUtil.isNullString(p_requestMessageArray[4]))
     * {
     * int localeValue=Integer.parseInt(p_requestMessageArray[4]);
     * p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(
     * p_requestMessageArray[4]));
     * if(p_requestVO.getReceiverLocale()==null)//changed by ankit zindal
     * 01/08/06 discussed by AC/GB
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .ERROR_INVALID_LANGUAGE_SEL_VALUE);
     * }
     * }
     * catch(Exception e)
     * {
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .ERROR_INVALID_LANGUAGE_SEL_VALUE);
     * }
     * if(!BTSLUtil.isNullString(p_requestMessageArray[3]))
     * {
     * int selectorValue=Integer.parseInt(p_requestMessageArray[3]);
     * p_requestVO.setReqSelector(""+selectorValue);
     * }
     * }
     * }
     * p_transferVO.setTransferValue(amount);
     * p_transferVO.setRequestedAmount(amount);
     * incomingSmsStr.append(amount+" ");
     * }
     * else
     * {
     * if(i==2)
     * {
     * amount=PretupsBL.getSystemAmount(p_requestMessageArray[3]);
     * if(amount<0)
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .P2P_ERROR_AMOUNT_LESSZERO);
     * try
     * {
     * if(!BTSLUtil.isNullString(p_requestMessageArray[5]))
     * {
     * int localeValue=Integer.parseInt(p_requestMessageArray[5]);
     * p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(
     * p_requestMessageArray[5]));
     * if(p_requestVO.getReceiverLocale()==null)//changed by ankit zindal
     * 01/08/06 discussed by AC/GB
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .ERROR_INVALID_LANGUAGE_SEL_VALUE);
     * }
     * }
     * catch(Exception e)
     * {
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .ERROR_INVALID_LANGUAGE_SEL_VALUE);
     * }
     * if(!BTSLUtil.isNullString(p_requestMessageArray[4]))
     * {
     * int selectorValue=Integer.parseInt(p_requestMessageArray[4]);
     * p_requestVO.setReqSelector(""+selectorValue);
     * }
     * }
     * else
     * {
     * if(actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))
     * {
     * pin=p_requestMessageArray[5];
     * incomingSmsStr.append("****"+" ");
     * //BTSLUtil.validatePIN(pin);
     * this.validatePINRules(pin);
     * senderVO.setPin(BTSLUtil.encryptText(pin));
     * senderVO.setPinUpdateReqd(true);
     * }
     * 
     * amount=PretupsBL.getSystemAmount(p_requestMessageArray[2]);
     * if(amount<0)
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .P2P_ERROR_AMOUNT_LESSZERO);
     * try
     * {
     * if(!BTSLUtil.isNullString(p_requestMessageArray[4]))
     * {
     * int localeValue=Integer.parseInt(p_requestMessageArray[4]);
     * p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(
     * p_requestMessageArray[4]));
     * if(p_requestVO.getReceiverLocale()==null)//changed by ankit zindal
     * 01/08/06 discussed by AC/GB
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .ERROR_INVALID_LANGUAGE_SEL_VALUE);
     * }
     * }
     * catch(Exception e)
     * {
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .ERROR_INVALID_LANGUAGE_SEL_VALUE);
     * }
     * if(!BTSLUtil.isNullString(p_requestMessageArray[3]))
     * {
     * int selectorValue=Integer.parseInt(p_requestMessageArray[3]);
     * p_requestVO.setReqSelector(""+selectorValue);
     * }
     * }
     * p_transferVO.setTransferValue(amount);
     * p_transferVO.setRequestedAmount(amount);
     * incomingSmsStr.append(amount+" ");
     * }
     * break;
     * }
     * case 7:
     * {
     * /*Actual!=default
     * PRC HDFC Name Amount selector langCode PIN
     * Actual=default
     * PRC HDFC Name Amount selector langCode PIN(Update with new PIN)
     */
    /*
     * if(i==1)
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .P2P_INVALID_MESSAGEFORMAT,0,new
     * String[]{p_requestVO.getActualMessageFormat()},null);
     * else
     * {
     * if(((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.
     * BUDDY_PIN_REQUIRED,networkPrefixVO.getNetworkCode())).booleanValue())
     * {
     * if(!actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))
     * {
     * pin=p_requestMessageArray[6];
     * incomingSmsStr.append("****"+" ");
     * try
     * {
     * SubscriberBL.validatePIN(p_con,senderVO,pin);
     * }
     * catch(BTSLBaseException be)
     * {
     * if(be.isKey() &&
     * ((be.getMessageKey().equals(PretupsErrorCodesI.ERROR_INVALID_PIN)) ||
     * (be.getMessageKey().equals(PretupsErrorCodesI.ERROR_SNDR_PINBLOCK))))
     * p_con.commit();
     * throw be;
     * }
     * 
     * amount=PretupsBL.getSystemAmount(p_requestMessageArray[3]);
     * if(amount<0)
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .P2P_ERROR_AMOUNT_LESSZERO);
     * try
     * {
     * if(!BTSLUtil.isNullString(p_requestMessageArray[5]))
     * {
     * int localeValue=Integer.parseInt(p_requestMessageArray[5]);
     * p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(
     * p_requestMessageArray[5]));
     * if(p_requestVO.getReceiverLocale()==null)//changed by ankit zindal
     * 01/08/06 discussed by AC/GB
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .ERROR_INVALID_LANGUAGE_SEL_VALUE);
     * }
     * }
     * catch(Exception e)
     * {
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .ERROR_INVALID_LANGUAGE_SEL_VALUE);
     * }
     * if(!BTSLUtil.isNullString(p_requestMessageArray[4]))
     * {
     * int selectorValue=Integer.parseInt(p_requestMessageArray[4]);
     * p_requestVO.setReqSelector(""+selectorValue);
     * }
     * }
     * else
     * {
     * pin=p_requestMessageArray[6];
     * incomingSmsStr.append("****"+" ");
     * //BTSLUtil.validatePIN(pin);
     * this.validatePINRules(pin);
     * senderVO.setPin(BTSLUtil.encryptText(pin));
     * senderVO.setPinUpdateReqd(true);
     * 
     * amount=PretupsBL.getSystemAmount(p_requestMessageArray[3]);
     * if(amount<0)
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .P2P_ERROR_AMOUNT_LESSZERO);
     * try
     * {
     * if(!BTSLUtil.isNullString(p_requestMessageArray[5]))
     * {
     * int localeValue=Integer.parseInt(p_requestMessageArray[5]);
     * p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(
     * p_requestMessageArray[5]));
     * if(p_requestVO.getReceiverLocale()==null)//changed by ankit zindal
     * 01/08/06 discussed by AC/GB
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .ERROR_INVALID_LANGUAGE_SEL_VALUE);
     * }
     * }
     * catch(Exception e)
     * {
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .ERROR_INVALID_LANGUAGE_SEL_VALUE);
     * }
     * if(!BTSLUtil.isNullString(p_requestMessageArray[4]))
     * {
     * int selectorValue=Integer.parseInt(p_requestMessageArray[4]);
     * p_requestVO.setReqSelector(""+selectorValue);
     * }
     * }
     * p_transferVO.setTransferValue(amount);
     * p_transferVO.setRequestedAmount(amount);
     * incomingSmsStr.append(amount+" ");
     * }
     * else
     * {
     * if(actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))
     * {
     * pin=p_requestMessageArray[6];
     * incomingSmsStr.append("****"+" ");
     * //BTSLUtil.validatePIN(pin);
     * this.validatePINRules(pin);
     * senderVO.setPin(BTSLUtil.encryptText(pin));
     * senderVO.setPinUpdateReqd(true);
     * }
     * 
     * amount=PretupsBL.getSystemAmount(p_requestMessageArray[3]);
     * if(amount<0)
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .P2P_ERROR_AMOUNT_LESSZERO);
     * try
     * {
     * if(!BTSLUtil.isNullString(p_requestMessageArray[5]))
     * {
     * int localeValue=Integer.parseInt(p_requestMessageArray[5]);
     * p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(
     * p_requestMessageArray[5]));
     * if(p_requestVO.getReceiverLocale()==null)//changed by ankit zindal
     * 01/08/06 discussed by AC/GB
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .ERROR_INVALID_LANGUAGE_SEL_VALUE);
     * }
     * }
     * catch(Exception e)
     * {
     * throw new
     * BTSLBaseException(this,"checkAfterPaymentMethodForBuddy",PretupsErrorCodesI
     * .ERROR_INVALID_LANGUAGE_SEL_VALUE);
     * }
     * if(!BTSLUtil.isNullString(p_requestMessageArray[4]))
     * {
     * int selectorValue=Integer.parseInt(p_requestMessageArray[4]);
     * p_requestVO.setReqSelector(""+selectorValue);
     * }
     * 
     * p_transferVO.setTransferValue(amount);
     * p_transferVO.setRequestedAmount(amount);
     * incomingSmsStr.append(amount+" ");
     * }
     * break;
     * }
     * }
     * default :
     * {
     * throw new BTSLBaseException(this,"checkIfBuddy",PretupsErrorCodesI.
     * P2P_INVALID_MESSAGEFORMAT,0,new
     * String[]{p_requestVO.getActualMessageFormat()},null);
     * }
     * 
     * }
     * return true;
     * }
     */
    /**
     * This method will be used to handle transfer message format
     * 
     * @param p_con
     * @param requestMessageArray
     * @param p_transferVO
     * @throws BTSLBaseException
     * @throws Exception
     */
    public void handleTransferMessageFormat(Connection p_con, RequestVO p_requestVO, TransferVO p_transferVO) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("handleTransferMessageFormat", "Entered p_requestVO=" + p_requestVO + " p_transferVO=" + p_transferVO);// requestMessageArray
        }

        try {
            if (!checkIfBuddy(p_con, p_requestVO, p_transferVO)) {
                validateIfNotBuddy(p_con, p_requestVO, p_transferVO);
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            throw e;
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("handleTransferMessageFormat", "Exiting :::" + p_transferVO.getIncomingSmsStr());
            }
        }
    }

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
        _log.debug("setCalculatedCardGroupValues ", " Entered");
        try {
            TransferItemVO transferItemVO = null;
            final int bonusValidityValue = Integer.parseInt(String.valueOf(p_cardGroupDetailVO.getBonusValidityValue()));
            final int validityPeriodValue = p_cardGroupDetailVO.getValidityPeriod();
            final long transferValue = p_cardGroupDetailVO.getTransferValue();
            final long bonusValue = p_cardGroupDetailVO.getBonusTalkTimeValue();
            transferItemVO = (TransferItemVO) p_transferVO.getTransferItemList().get(1);
            // Calculate the validities when CARD_GROUP_SELECTOR=1 i.e. for Main
            // account.
            if ((String.valueOf(PretupsI.CHNL_SELECTOR_CVG_VALUE)).equals(p_subService))// CVG
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
            // Calculate the validities when CARD_GROUP_SELECTOR=2 i.e. for SMS
            // account.
            if ((String.valueOf(PretupsI.CHNL_SELECTOR_C_VALUE)).equals(p_subService))// C
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
            // Calculate the validities when CARD_GROUP_SELECTOR=3 i.e. for MMS
            // account.
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
        _log.debug("setCalculatedCardGroupValues ", " Exited");
    }

    /**
     * Method for checking Pasword or already exist in Pin_Password_history
     * table or not.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_modificationType
     *            String
     * @param p_loginId
     *            String
     * @param p_newPassword
     *            String
     * @return flag boolean
     * @throws BTSLBaseException
     */
    private boolean checkPasswordHistory(Connection p_con, String p_modificationType, String p_loginId, String p_newPassword) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("checkPasswordHistory", "Entered: p_modification_type=" + p_modificationType + "p_loginId=" + p_loginId + " p_newPassword= " + p_newPassword);
        }
        final String methodName = "checkPasswordHistory";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean existFlag = false;
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT pin_or_password,modified_on FROM (SELECT pin_or_password,modified_on,  row_number()  over (ORDER BY modified_on DESC) rn  ");
        strBuff.append(" FROM pin_password_history WHERE modification_type= ? AND msisdn_or_loginid=? )x  WHERE rn <= ? ");
        strBuff.append(" ORDER BY modified_on DESC ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("checkPasswordHistory", "QUERY sqlSelect=" + sqlSelect);
        }
        try {

            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_modificationType);
            pstmt.setString(2, p_loginId);
            pstmt.setInt(3, SystemPreferences.PREV_PASS_NOT_ALLOW);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                if (rs.getString("Pin_or_Password").equals(p_newPassword)) {
                    existFlag = true;
                    break;
                }
            }
            return existFlag;
        } catch (SQLException sqe) {
            _log.error("checkPasswordHistory", "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[checkPasswordHistory]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "checkPasswordHistory", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("checkPasswordHistory", "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[checkPasswordHistory]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "checkPasswordHistory", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("checkPasswordHistory", "QUERY pstmt=   " + pstmt);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("checkPasswordHistory", "Exiting: existFlag=" + existFlag);
            }
        }
    }

    /**
     * @author diwakar
     * @date: 09-JUNE-2014
     */
    public HashMap validatePassword(String p_loginID, String p_password) {
        _log.debug("validatePassword", "Entered, p_userID= ", new String(p_loginID + ", Password= " + p_password));
        final String methodName = "validatePassword";
        boolean passwordExist = false;
        final HashMap messageMap = new HashMap();
        boolean specialCharFlag = false;
        boolean numberStrFlag = false;
        boolean capitalLettersFlag = false;
        boolean smallLettersFlag = false;
        boolean validPassword = false;
        boolean firstcapitalLettersFlag = false;

        String defaultPasswd = BTSLUtil.getDefaultPasswordNumeric(p_password);
        if (defaultPasswd.equals(p_password)) {
            return messageMap;
        }
        defaultPasswd = BTSLUtil.getDefaultPasswordText(p_password);
        if (defaultPasswd.equals(p_password)) {
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
        if (!BTSLUtil.containsChar(p_password)) {
            messageMap.put("operatorutil.validatepassword.error.passwordnotcontainschar", null);
        }

        // Should contains Special character
        final String specialChar = Constants.getProperty("SPECIAL_CHARACTER_PASSWORD_VALIDATION");
        if (!BTSLUtil.isNullString(specialChar)) {
            final String[] specialCharArray = { specialChar };
            final String[] passwordCharArray = specialChar.split(",");

            for (int i = 0, j = passwordCharArray.length; i < j; i++) {
                if (p_password.contains(passwordCharArray[i])) {
                    specialCharFlag = true;
                    break;
                }
            }
        }

        // Should contains Number
        final String[] passwordNumberStrArray = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };

        for (int i = 0, j = passwordNumberStrArray.length; i < j; i++) {
            if (p_password.contains(passwordNumberStrArray[i])) {
                numberStrFlag = true;
                break;
            }
        }
        // Should not contains 1'st character Capital
        final char[] firstLetter = p_password.toCharArray();
        if (!String.valueOf(firstLetter[0]).matches("[A-Z]")) {
            firstcapitalLettersFlag = true;
        }
        _log.debug("validatePassword", "firstcapitalLettersFlag::" + firstcapitalLettersFlag);

        // Should contains Capital Letters
        if (p_password.matches(".*[A-Z].*")) {
            capitalLettersFlag = true;
        }
        _log.debug("validatePassword", "capitalLettersFlag::" + capitalLettersFlag);

        // Should contains Small Letters
        if (p_password.matches(".*[a-z].*")) {
            smallLettersFlag = true;
        }
        _log.debug("validatePassword", "smallLettersFlag::" + smallLettersFlag);

        // D+C+S
        if (specialCharFlag && firstcapitalLettersFlag && numberStrFlag && capitalLettersFlag && smallLettersFlag) {
            validPassword = true;
        } else if (specialCharFlag && firstcapitalLettersFlag && numberStrFlag && capitalLettersFlag) {
            validPassword = true;
        } else if (specialCharFlag && firstcapitalLettersFlag && numberStrFlag && smallLettersFlag) {
            validPassword = true;
        } else if (specialCharFlag && firstcapitalLettersFlag && capitalLettersFlag && smallLettersFlag) {
            validPassword = true;
        } else if (specialCharFlag && firstcapitalLettersFlag && numberStrFlag) {
            validPassword = true;
        } else if (specialCharFlag && firstcapitalLettersFlag && capitalLettersFlag) {
            validPassword = true;
        } else if (specialCharFlag && firstcapitalLettersFlag && smallLettersFlag) {
            validPassword = true;
        }

        // extra validation as per MALI requirement

        // Check Sequential
        final boolean isSequential = findSequential(p_password);
        if (isSequential) {
            if (_log.isDebugEnabled()) {
                _log.debug("validatePassword", "Sequential Found");
            }
            if (validPassword) {
                validPassword = false;
            }
        } else {
            if (_log.isDebugEnabled()) {
                _log.debug("validatePassword", "Sequential Not Found");
            }

            // Check Repetation
            final boolean isRepetation = findRepetation(p_password, specialChar);
            if (isRepetation) {
                if (_log.isDebugEnabled()) {
                    _log.debug("validatePassword", "Repetation Found");
                }
                if (validPassword) {
                    validPassword = false;
                }
            } else {
                if (_log.isDebugEnabled()) {
                    _log.debug("validatePassword", "Repetation Not Found");
                }

            }
        }
        // Ended Here

        if (!validPassword) {
            messageMap.put("operatorutil.validatepassword.error.passwordmusthaverequiredchar4m", null);
        }

        Connection con = null;
        MComConnectionI mcomCon = null;
        if (!BTSLUtil.isNullString(p_loginID)) {
            if (p_password.contains(p_loginID)) {
                messageMap.put("operatorutil.validatepassword.error.sameusernamepassword", null);
            }

            try {
                mcomCon = new MComConnection();
                con=mcomCon.getConnection();
                passwordExist = this.checkPasswordHistory(con, PretupsI.USER_PASSWORD_MANAGEMENT, p_loginID, p_password);
                if (passwordExist) {
                    messageMap.put("user.modifypwd.error.newpasswordexistcheck", SystemPreferences.PREV_PASS_NOT_ALLOW);
                }

            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            finally {
            	if(mcomCon != null)
            	{
            		mcomCon.close("MaliUtil#validatePassword");
            		mcomCon=null;
            		}
            }
        }
        if (_log.isDebugEnabled()) {
            _log.debug("validatePassword", "Exiting ");
        }
        return messageMap;
    }

    public HashMap pinValidate(String p_pin) {
        final String methodName = "pinValidate";
        _log.debug("validatePIN", "Entered, PIN= " + p_pin);
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
        if (p_pin.length() < SystemPreferences.MIN_SMS_PIN_LENGTH || p_pin.length() > SystemPreferences.MAX_SMS_PIN_LENGTH) {
            final String[] args = { String.valueOf(SystemPreferences.MIN_SMS_PIN_LENGTH), String.valueOf(SystemPreferences.MAX_SMS_PIN_LENGTH) };
            messageMap.put("operatorutil.validatepin.error.smspinlenerr", args);
        }
        final int result = BTSLUtil.isSMSPinValid(p_pin);
        if (result == -1) {
            messageMap.put("operatorutil.validatepin.error.pinsamedigit", null);
        } else if (result == 1) {
            messageMap.put("operatorutil.validatepin.error.pinconsecutive", null);
        }

        // Should contains Maximum 2 repetation
        if (!isValidRepetation(p_pin)) {
            String msg[] = { "2" };
            try {
                msg = new String[] { Constants.getProperty("NO_OF_REPETATION_ALLOWDED_IN_PIN_VALIDATION") };
            } catch (RuntimeException e) {
                msg = new String[] { "2" };
                _log.errorTrace(methodName, e);
            }
            messageMap.put("operatorutil.validatepin.error.pinrepetative", msg);

        }

        if (_log.isDebugEnabled()) {
            _log.debug("validatePIN", "Exiting messageMap.size()=" + messageMap.size());
        }
        return messageMap;
    }

    public void validatePIN(String p_pin) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("validatePIN", "Entered, p_pin= " + p_pin);
        }
        final String methodName = "validatePIN";
        if (BTSLUtil.isNullString(p_pin)) {
            throw new BTSLBaseException("BTSLUtil", "validatePIN", PretupsErrorCodesI.PIN_INVALID);
        } else if (!BTSLUtil.isNumeric(p_pin)) {
            throw new BTSLBaseException("BTSLUtil", "validatePIN", PretupsErrorCodesI.NEWPIN_NOTNUMERIC);
        } else if (p_pin.length() < SystemPreferences.MIN_SMS_PIN_LENGTH || p_pin.length() > SystemPreferences.MAX_SMS_PIN_LENGTH) {
            final String msg[] = { String.valueOf(SystemPreferences.MIN_SMS_PIN_LENGTH), String.valueOf(SystemPreferences.MAX_SMS_PIN_LENGTH) };
            throw new BTSLBaseException("BTSLUtil", "validatePIN", PretupsErrorCodesI.PIN_LENGTHINVALID, 0, msg, null);
        }
        // Should contains Maximum 2 repetation
        else if (!isValidRepetation(p_pin)) {
            String msg[] = { "2" };
            try {
                msg = new String[] { Constants.getProperty("NO_OF_REPETATION_ALLOWDED_IN_PIN_VALIDATION") };
            } catch (RuntimeException e) {
                msg = new String[] { "2" };
                _log.errorTrace(methodName, e);
            }
            throw new BTSLBaseException("BTSLUtil", "validatePIN", PretupsErrorCodesI.PIN_REPETATION_INVALID, 0, msg, null);
        } else {
            // Commented the below line as Mali is maintaing the old PIN policy

            final int result = isSMSPinValid(p_pin);
            if (result == -1) {
                throw new BTSLBaseException("BTSLUtil", "validatePIN", PretupsErrorCodesI.PIN_SAMEDIGIT);
            } else if (result == 1) {
                throw new BTSLBaseException("BTSLUtil", "validatePIN", PretupsErrorCodesI.PIN_CONSECUTIVE);
            }
        }
    }

    /**
     * Validate the string of repetation not more than configured
     * 
     * @param p_string
     * @author diwakar
     * @date : 09-06-2014
     * @return
     */
    public boolean isValidRepetation(String p_string) {

        if (_log.isDebugEnabled()) {
            _log.debug("isValidRepetation", "p_string= " + p_string);
        }
        final String methodName = "isValidRepetation";
        boolean result = true;
        int noOfConfiguredRepetation = 2;
        final HashMap<String, Integer> hashMap = new HashMap<String, Integer>();

        try {
            noOfConfiguredRepetation = Integer.parseInt(Constants.getProperty("NO_OF_REPETATION_ALLOWDED_IN_PIN_VALIDATION"));

        } catch (RuntimeException e) {
            noOfConfiguredRepetation = 2;
            _log.errorTrace(methodName, e);
        }

        for (int i = 0; i < p_string.length(); i++) {

            if (hashMap.containsKey(p_string.substring(i, i + 1))) {
                hashMap.put(p_string.substring(i, i + 1), hashMap.get(p_string.substring(i, i + 1)) + 1);
            } else {
                hashMap.put(p_string.substring(i, i + 1), 1);
            }
        }

        final Set<String> keySet = hashMap.keySet();
        final Iterator i = keySet.iterator();
        int repetaionCount = 0;
        int foundCount = 0;
        while (i.hasNext()) {
            final String repetaionKey = (String) i.next();
            repetaionCount = hashMap.get(repetaionKey);
            if (repetaionCount > noOfConfiguredRepetation) {
                if (_log.isDebugEnabled()) {
                    _log.debug("isValidRepetation", "repetaionKey= " + repetaionKey + " Ocurred " + repetaionCount + " times");
                }
                result = false;
                break;
            } else if (repetaionCount == noOfConfiguredRepetation) {
                foundCount++;
            }
            if (foundCount > noOfConfiguredRepetation) {
                if (_log.isDebugEnabled()) {
                    _log.debug("isValidRepetation", " Repetation Ocurred " + foundCount + " times");
                }
                result = false;
            }

        }
        return result;
    }

    /**
     * Validate the sequential of the string
     * 
     * @param p_string
     * @author diwakar
     * @date : 12-06-2014
     * @return
     */
    private boolean findSequential(String p_password) {

        if (_log.isDebugEnabled()) {
            _log.debug("findSequential", "Entered p_password = " + p_password);
        }
        final char[] password = p_password.toCharArray();
        final int passwordLength = password.length;
        int currentCounter = 0;
        int matchFoundAlpha = 1;
        int matchFoundNumber = 1;
        boolean isSequential = false;

        while ((currentCounter + 1) < passwordLength) {
            final String str = String.valueOf(password[currentCounter]);

            
            if (str.matches("[a-z]$")) {
                
                final int ascciValFirst = (int) password[currentCounter];
                final int ascciValSecond = (int) password[currentCounter + 1];
                if (((ascciValFirst + 1) == ascciValSecond) || ((ascciValFirst + 1) == (ascciValSecond + 32))) {
                    matchFoundAlpha++;
                    isSequential = true;
                    return isSequential;
                }
            } else if (str.matches("[A-Z]$")) {

                
                final int ascciValFirst = (int) password[currentCounter];
                final int ascciValSecond = (int) password[currentCounter + 1];
                if (((ascciValFirst + 1) == ascciValSecond) || ((ascciValFirst + 1) == (ascciValSecond - 32))) {
                    matchFoundAlpha++;
                    isSequential = true;
                    return isSequential;
                }
            } else if (str.matches("[0-9]$")) {

                final int ascciValFirst = (int) password[currentCounter];
                final int ascciValSecond = (int) password[currentCounter + 1];
                if ((ascciValFirst + 1) == ascciValSecond) {
                    matchFoundNumber++;
                    isSequential = true;
                    return isSequential;
                }
            }
            currentCounter++;

        }
        if (matchFoundAlpha > 2 || matchFoundNumber > 2) {
            isSequential = true;
        } else {
            isSequential = false;
        }

        return isSequential;
    }

    /**
     * Validate the repetation of the string
     * 
     * @param p_string
     * @author diwakar
     * @date : 12-06-2014
     * @return
     */
    private boolean findRepetation(String p_password, String allowdedSpecialChars) {

        if (_log.isDebugEnabled()) {
            _log.debug("findRepetation", "Entered p_password = " + p_password + " allowdedSpecialChars = " + allowdedSpecialChars);
        }
        final char[] password = p_password.toCharArray();
        final int passwordLength = password.length;
        int currentCounter = 0;
        boolean isRepetation = false;

        final String[] allowdedSpecialCharArray = allowdedSpecialChars.split(",");

        while ((currentCounter + 1) < passwordLength) {
            final String str = String.valueOf(password[currentCounter]);

           
            if (str.matches("[a-z]$")) {
                
                final int ascciValFirst = (int) password[currentCounter];
                final int ascciValSecond = (int) password[currentCounter + 1];
                if ((ascciValFirst == ascciValSecond) || ((ascciValFirst) == (ascciValSecond + 32))) {
                    isRepetation = true;
                }
            } else if (str.matches("[A-Z]$")) {
                
                final int ascciValFirst = (int) password[currentCounter];
                final int ascciValSecond = (int) password[currentCounter + 1];
                if ((ascciValFirst == ascciValSecond) || (ascciValFirst == (ascciValSecond - 32))) {
                    isRepetation = true;
                }
            } else if (str.matches("[0-9]$")) {
                
                final int ascciValFirst = (int) password[currentCounter];
                final int ascciValSecond = (int) password[currentCounter + 1];
                if (ascciValFirst == ascciValSecond) {
                    isRepetation = true;
                }
            } else {
                
                for (int i = 0, j = allowdedSpecialCharArray.length; i < j; i++) {
                    if (str.contains(allowdedSpecialCharArray[i])) {
                        final int ascciValFirst = (int) password[currentCounter];
                        final int ascciValSecond = (int) password[currentCounter + 1];
                        if (ascciValFirst == ascciValSecond) {
                            isRepetation = true;
                        }
                        break;
                    }
                }

            }
            currentCounter++;
            if (isRepetation) {
                return isRepetation;
            }

        }
        return isRepetation;
    }

    /**
     * Validate the isSMSPinValid of the string
     * 
     * @param p_string
     * @author diwakar
     * @date : 20-10-2014
     * @return
     */
    public static int isSMSPinValid(String s) {
        int i = 0;
        int j = 0;
        final boolean flag = false;
        char c1 = '\0';
        final boolean flag1 = false;
        byte byte0 = 0;
        for (int k = 0; k < s.length(); k++) {
            final char c2 = s.charAt(k);
            if (k < s.length() - 1) {
                c1 = s.charAt(k + 1);
            }
            final char c = c1;
            if (c2 == c1) {
                i++;
                continue;
            }
            if (c == c2 + 1 || c == c2 - 1) {
                j++;
            }
        }

        if (i == s.length()) {
            return byte0 = -1;
        }
        if (j == s.length() - 1) {
            return byte0 = 1;
        } else {
            return byte0;
        }
    }

}
