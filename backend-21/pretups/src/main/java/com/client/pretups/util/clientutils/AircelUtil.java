package com.client.pretups.util.clientutils;

/**
 * @(#)AircelUtil
 *                Copyright(c) 2006, Bharti Telesoft Int. Public Ltd.
 *                All Rights Reserved
 *                This class is an util class to perform operator specific
 *                functions
 *                --------------------------------------------------------------
 *                -----------------------------------
 *                Author Date History
 *                --------------------------------------------------------------
 *                -----------------------------------
 *                Pankaj Namdev 29/12/06 Created
 *                --------------------------------------------------------------
 *                ----------------------------------
 */
import java.sql.Connection;
import java.util.Date;
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
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchMasterVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnVO;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.btsl.pretups.payment.businesslogic.PaymentMethodCache;
import com.btsl.pretups.payment.businesslogic.PaymentMethodKeywordVO;
import com.btsl.pretups.payment.businesslogic.ServicePaymentMappingCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchMasterVO;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/**
 * @author pankaj.namdev
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class AircelUtil extends OperatorUtil {
    private Log _log = LogFactory.getLog(this.getClass().getName());
    public int C2S_TRANSFER_ID_PAD_LENGTH = 5;
    public int P2P_TRANSFER_ID_PAD_LENGTH = 5;

    /**
     * Method formatC2STransferID.
     * 
     * @param p_transferVO
     *            TransferVO
     * @param p_tempTransferID
     *            long
     * @return String
     * @see com.btsl.pretups.util.AircelUtilI#formatC2STransferID(TransferVO,
     *      long)
     * 
     */
    public String formatC2STransferID(TransferVO p_transferVO, long p_tempTransferID) {
        String returnStr = null;
        final String METHOD_NAME = "formatC2STransferID";
        try {
            final String paddedTransferIDStr = BTSLUtil.padZeroesToLeft(String.valueOf(p_tempTransferID), C2S_TRANSFER_ID_PAD_LENGTH);
            returnStr = "R" + currentDateTimeFormatString(p_transferVO.getCreatedOn()) + currentTimeFormatString(p_transferVO.getCreatedOn()) + Constants
                .getProperty("INSTANCE_ID") + paddedTransferIDStr;
            if (!BTSLUtil.isNullString(p_transferVO.getReceiverNetworkCode())) {
                returnStr = p_transferVO.getReceiverNetworkCode() + returnStr;
            }
            p_transferVO.setTransferID(returnStr);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AircelUtil[]", "", "", "",
                "Not able to generate Transfer ID:" + e.getMessage());
            returnStr = null;
        }
        return returnStr;
    }

    /**
     * Method formatPostpaidBillPayTransferID.
     * 
     * @param p_transferVO
     *            TransferVO
     * @param p_tempTransferID
     *            long
     * @return String
     * @see com.btsl.pretups.util.OperatorUtilI#formatPostpaidBillPayTransferID(TransferVO,
     *      long)
     * 
     */
    public String formatPostpaidBillPayTransferID(TransferVO p_transferVO, long p_tempTransferID) {
        String returnStr = null;
        final String METHOD_NAME = "formatPostpaidBillPayTransferID";
        try {
            final String paddedTransferIDStr = BTSLUtil.padZeroesToLeft(String.valueOf(p_tempTransferID), C2S_TRANSFER_ID_PAD_LENGTH);
            returnStr = "P" + currentDateTimeFormatString(p_transferVO.getCreatedOn()) + currentTimeFormatString(p_transferVO.getCreatedOn()) + Constants
                .getProperty("INSTANCE_ID") + paddedTransferIDStr;
            if (!BTSLUtil.isNullString(p_transferVO.getReceiverNetworkCode())) {
                returnStr = p_transferVO.getReceiverNetworkCode() + returnStr;
            }
            p_transferVO.setTransferID(returnStr);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AircelUtil[]", "", "", "",
                "Not able to generate PostpaidBillPay Transfer ID:" + e.getMessage());
            returnStr = null;
        }
        return returnStr;
    }

    /**
     * Method formatBillPayTransferID.
     * 
     * @param p_transferVO
     *            TransferVO
     * @param p_tempTransferID
     *            long
     * @return String
     * @see com.btsl.pretups.util.OperatorUtilI#formatBillPayTransferID(TransferVO,
     *      long)
     * 
     */
    public String formatBillPayTransferID(TransferVO p_transferVO, long p_tempTransferID) {
        String returnStr = null;
        final String METHOD_NAME = "formatBillPayTransferID";
        try {
            final String paddedTransferIDStr = BTSLUtil.padZeroesToLeft(String.valueOf(p_tempTransferID), C2S_TRANSFER_ID_PAD_LENGTH);
            returnStr = "B" + currentDateTimeFormatString(p_transferVO.getCreatedOn()) + currentTimeFormatString(p_transferVO.getCreatedOn()) + Constants
                .getProperty("INSTANCE_ID") + paddedTransferIDStr;
            if (!BTSLUtil.isNullString(p_transferVO.getReceiverNetworkCode())) {
                returnStr = p_transferVO.getReceiverNetworkCode() + returnStr;
            }
            p_transferVO.setTransferID(returnStr);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AircelUtil[]", "", "", "",
                "Not able to generate BillPay Transfer ID:" + e.getMessage());
            returnStr = null;
        }
        return returnStr;
    }

    /**
     * Method formatP2PTransferID.
     * 
     * @param p_transferVO
     *            TransferVO
     * @param p_tempTransferID
     *            long
     * @return String
     * @see com.btsl.pretups.util.AircelUtilI#formatP2PTransferID(TransferVO,
     *      long)
     */
    public String formatP2PTransferID(TransferVO p_transferVO, long p_tempTransferID) {
        String returnStr = null;
        final String METHOD_NAME = "formatP2PTransferID";
        try {
            // ReceiverVO receiverVO=(ReceiverVO)p_transferVO.getReceiverVO();
            // String currentYear=BTSLUtil.getFinancialYearLastDigits(2);
            final String paddedTransferIDStr = BTSLUtil.padZeroesToLeft(String.valueOf(p_tempTransferID), P2P_TRANSFER_ID_PAD_LENGTH);
            // returnStr=receiverVO.getNetworkCode()+"/"+currentYear+"/"+paddedTransferIDStr;
            returnStr = "C" + currentDateTimeFormatString(p_transferVO.getCreatedOn()) + currentTimeFormatString(p_transferVO.getCreatedOn()) + Constants
                .getProperty("INSTANCE_ID") + paddedTransferIDStr;

            if (!BTSLUtil.isNullString(p_transferVO.getReceiverNetworkCode())) {
                returnStr = p_transferVO.getReceiverNetworkCode() + returnStr;
            }
            p_transferVO.setTransferID(returnStr);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AircelUtil[]", "", "", "",
                "Not able to generate Transfer ID:" + e.getMessage());
            returnStr = null;
        }
        return returnStr;
    }

    /**
     * Method formatChannelTransferID.
     * 
     * @param p_channelTransferVO
     *            ChannelTransferVO
     * @param p_tempTransferStr
     *            String
     * @param p_tempTransferID
     *            long
     * @return String
     * @see com.btsl.pretups.util.AircelUtilI#formatChannelTransferID(ChannelTransferVO,
     *      String, long)
     */
    public String formatChannelTransferID(ChannelTransferVO p_channelTransferVO, String p_tempTransferStr, long p_tempTransferID) {
        String returnStr = null;
        final String METHOD_NAME = "formatChannelTransferID";
        try {
            final String paddedTransferIDStr = BTSLUtil.padZeroesToLeft(String.valueOf(p_tempTransferID), CHANEL_TRANSFER_ID_PAD_LENGTH);
            // returnStr=p_tempTransferStr+currentDateTimeFormatString(p_channelTransferVO.getCreatedOn())+"."+currentTimeFormatString(p_channelTransferVO.getCreatedOn())+"."+paddedTransferIDStr;
            returnStr = p_tempTransferStr + currentDateTimeFormatString(p_channelTransferVO.getDBDateTime()) + "." + currentTimeFormatString(p_channelTransferVO
                .getDBDateTime()) + "." + paddedTransferIDStr;

            if (!BTSLUtil.isNullString(p_channelTransferVO.getNetworkCode())) {
                returnStr = p_channelTransferVO.getNetworkCode() + returnStr;
            }
            p_channelTransferVO.setTransferID(returnStr);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AircelUtil[formatChannelTransferID]", "", "", "",
                "Not able to generate Transaction ID:" + e.getMessage());
            returnStr = null;
        }
        return returnStr;
    }

    /**
     * Method formatNetworkStockTxnID.
     * 
     * @param p_networkStockTxnVO
     *            NetworkStockTxnVO
     * @param p_tempTransferID
     *            long
     * @return String
     * @see com.btsl.pretups.util.AircelUtilI#formatNetworkStockTxnID(Date,
     *      long)
     */
    public String formatNetworkStockTxnID(NetworkStockTxnVO p_networkStockTxnVO, long p_tempTransferID) {
        String returnStr = null;
        final String METHOD_NAME = "formatNetworkStockTxnID";
        try {
            final String paddedTransferIDStr = BTSLUtil.padZeroesToLeft(String.valueOf(p_tempTransferID), NETWORK_STOCK_TXN_ID_PAD_LENGTH);
            returnStr = PretupsI.NETWORK_STOCK_TRANSACTION_ID + currentDateTimeFormatString(p_networkStockTxnVO.getCreatedOn()) + "." + currentTimeFormatString(p_networkStockTxnVO
                .getCreatedOn()) + "." + paddedTransferIDStr;
            if (!BTSLUtil.isNullString(p_networkStockTxnVO.getNetworkCode())) {
                returnStr = p_networkStockTxnVO.getNetworkCode() + returnStr;
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AircelUtil[formatNetworkStockTxnID]", "", "", "",
                "Not able to generate Transaction ID:" + e.getMessage());
            returnStr = null;
        }
        return returnStr;
    }

    /**
     * Method formatFOCBatchMasterTxnID.
     * This method generate the batch ID for the FOC batch master transfer.
     * 
     * @param p_batchMasterVO
     *            FOCBatchMasterVO
     * @param p_tempTransferID
     *            long
     * @return String
     * 
     * @see com.btsl.pretups.util.AircelUtilI#formatFOCBatchMasterTxnID(FOCBatchMasterVO,
     *      long)
     */
    public String formatFOCBatchMasterTxnID(FOCBatchMasterVO p_batchMasterVO, long p_tempTransferID) {
        String returnStr = null;
        final String METHOD_NAME = "formatFOCBatchMasterTxnID";
        try {
            final String paddedTransferIDStr = BTSLUtil.padZeroesToLeft(String.valueOf(p_tempTransferID), FOC_BATCH_MASTER_ID_PAD_LENGTH);
            returnStr = PretupsI.FOC_BATCH_TRANSACTION_ID + currentDateTimeFormatString(p_batchMasterVO.getCreatedOn()) + "." + paddedTransferIDStr;
            if (!BTSLUtil.isNullString(p_batchMasterVO.getNetworkCode())) {
                returnStr = p_batchMasterVO.getNetworkCode() + returnStr;
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AircelUtil[formatFOCBatchMasterTxnID]", "", "", "",
                "Not able to generate Transaction ID:" + e.getMessage());
            returnStr = null;
        }
        return returnStr;
    }

    /**
     * Method formatNetworkStockTxnID.
     * 
     * @param p_scheduleMasterVO
     *            ScheduleBatchMasterVO
     * @param p_tempTransferStr
     *            String
     * @param p_tempTransferID
     *            long
     * @return String
     * @see com.btsl.pretups.util.OperatorUtilI#formatNetworkStockTxnID(Date,
     *      long)
     */
    public String formatScheduleBatchID(ScheduleBatchMasterVO p_scheduleMasterVO, String p_tempTransferStr, long p_tempTransferID) {
        String returnStr = null;
        final String METHOD_NAME = "formatScheduleBatchID";
        try {
            final String paddedTransferIDStr = BTSLUtil.padZeroesToLeft(String.valueOf(p_tempTransferID), SCHEDULE_BATCH_ID_PAD_LENGTH);
            // returnStr=p_tempTransferStr+currentDateTimeFormatString(p_scheduleMasterVO.getCreatedOn())+"."+currentTimeFormatString(p_scheduleMasterVO.getCreatedOn())+"."+paddedTransferIDStr;
            returnStr = p_tempTransferStr + currentDateTimeFormatString(p_scheduleMasterVO.getCreatedOn()) + "." + paddedTransferIDStr;
            if (!BTSLUtil.isNullString(p_scheduleMasterVO.getNetworkCode())) {
                returnStr = p_scheduleMasterVO.getNetworkCode() + returnStr;
            }
            p_scheduleMasterVO.setBatchID(returnStr);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorUtil[formatChannelTransferID]", "", "", "",
                "Not able to generate Transaction ID:" + e.getMessage());
            returnStr = null;
        }
        return returnStr;
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
        String[] requestMessageArray = p_requestVO.getRequestMessageArray();
        final String METHOD_NAME = "validateIfNotBuddy";
        if (_log.isDebugEnabled()) {
            _log.debug("validateIfNotBuddy", " requestMessageArray length:" + requestMessageArray);
        }
        if (requestMessageArray.length < 3 || requestMessageArray.length > 7) {
            throw new BTSLBaseException(this, "validateIfNotBuddy", PretupsErrorCodesI.P2P_INVALID_MESSAGEFORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() },
                null);
        }
        final String serviceKeyword = requestMessageArray[0];
        final String senderSubscriberType = ((SenderVO) p_transferVO.getSenderVO()).getSubscriberType();
        final String serviceType = p_requestVO.getServiceType();// AircelChennai::SelfTopUp::ASHISH
        // S
        final StringBuffer incomingSmsStr = new StringBuffer(serviceKeyword + " ");
        int messageLength = requestMessageArray.length;
        final SenderVO senderVO = (SenderVO) p_transferVO.getSenderVO();
        // if pin Invalid return with error(PIN is Mandatory)
        String actualPin = null;
        actualPin = BTSLUtil.decryptText(senderVO.getPin());

        if (_log.isDebugEnabled()) {
            _log.debug("validateIfNotBuddy", " actualPin:" + actualPin);
        }

        String paymentMethodType = null;
        String pin = null;
        String paymentMethodKeyword = requestMessageArray[1];

        // AircelChennai::SelfTopUp::START:ASHISH S

        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_ALLOW_SELF_TOPUP))).booleanValue() && "PCR".equals(serviceType)) {
            if (BTSLUtil.isNumeric(paymentMethodKeyword) || messageLength == 3) {
                throw new BTSLBaseException(this, "validateIfNotBuddy", PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
            }
        }

        boolean p_selfTopUp = false;
        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_ALLOW_SELF_TOPUP))).booleanValue() && (messageLength == 4) && "PCR".equals(serviceType) && (!BTSLUtil.isNumeric(paymentMethodKeyword))) {
            final String[] newRequestMessageArray = { requestMessageArray[0], requestMessageArray[1].toUpperCase(), p_transferVO.getSenderMsisdn(), requestMessageArray[2], requestMessageArray[3] };
            requestMessageArray = newRequestMessageArray;
            p_requestVO.setRequestMessageArray(requestMessageArray);
            messageLength = requestMessageArray.length;
            final String _bankPIN = requestMessageArray[4];
            if (_log.isDebugEnabled()) {
                _log.debug("validateIfNotBuddy", " _bankPIN:" + _bankPIN);
            }
            senderVO.setPin(_bankPIN);
            p_selfTopUp = true;
        }
        // AircelChennai::SelfTopUp::END:ASHISH S

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
                    // if(actualPin.equalsIgnoreCase(PretupsI.DEFAULT_P2P_PIN))
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
                    if (!p_selfTopUp)// AircelChennai::SelfTopUp::modified:ASHISH
                                     // S
                    {
                        // Validate 2nd Argument for PIN.
                        pin = requestMessageArray[4];
                        incomingSmsStr.append("****" + " ");
                        // if pin Invalid return with error(PIN is Mandatory)
                        // if(actualPin.equalsIgnoreCase(PretupsI.DEFAULT_P2P_PIN))
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
     * Method validatePassword.
     * 
     * @author shishupal.singh
     * @created on 21/08/08
     * @param p_loginID
     *            String
     * @param p_password
     *            String
     * @return String
     */
    public HashMap validatePassword(String p_loginID, String p_password) {
        _log.debug("validatePassword", "Entered, p_userID= ", new String(p_loginID + ", Password= " + p_password));
        final HashMap messageMap = new HashMap();
        String defaultPassword = BTSLUtil.getDefaultPasswordNumeric(p_password);
        if (defaultPassword.equals(p_password)) {
            return messageMap;
        }
        defaultPassword = BTSLUtil.getDefaultPasswordText(p_password);
        if (defaultPassword.equals(p_password)) {
            return messageMap;
        }
        if (p_password.length() < ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_LOGIN_PWD_LENGTH))).intValue() || p_password.length() > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_LOGIN_PWD_LENGTH))).intValue()) {
            final String[] args = { String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_LOGIN_PWD_LENGTH))).intValue()), String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_LOGIN_PWD_LENGTH))).intValue()) };
            messageMap.put("operatorutil.validatepassword.error.passwordlenerr", args);
        }

        int numberStrCount = 0;

        // if Space Char exist in password.
        /*
         * String passwordSpace=" ";
         * if(p_password.contains(passwordSpace))
         * {
         * messageMap.put("aircelutil.validatepassword.error.spacecharnotallow",null
         * );
         * }
         */

        // for special character
        final String nonSpecialChar = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        final char[] ch = p_password.toCharArray();
        for (int i = 0, j = ch.length; i < j; i++) {
            if (!nonSpecialChar.contains(Character.toString(ch[i]))) {
                messageMap.put("aircelutil.validatepassword.error.passwordspecialchar", null);
                break;
            }
        }

        // for number
        final String[] passwordNumberStrArray2 = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
        for (int i = 0, j = passwordNumberStrArray2.length; i < j; i++) {
            if (p_password.contains(passwordNumberStrArray2[i])) {
                numberStrCount++;
                break;
            }
        }

        if (numberStrCount > 0) {
            // for uper case alphabet
            final String[] passwordNumberStrArray = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
            for (int i = 0, j = passwordNumberStrArray.length; i < j; i++) {
                if (p_password.contains(passwordNumberStrArray[i]) || p_password.contains(passwordNumberStrArray[i].toLowerCase())) {
                    numberStrCount++;
                    break;
                }
            }
        }

        if (numberStrCount < 2) {
            messageMap.put("aircelutil.validatepassword.error.passwordalphanumeric", null);
        }
        if ((p_loginID.toUpperCase()).equals(p_password.toUpperCase())) {
            messageMap.put("operatorutil.validatepassword.error.sameusernamepassword", null);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("validatePassword", "Exiting ");
        }
        return messageMap;
    }
}
