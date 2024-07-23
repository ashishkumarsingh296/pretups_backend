/**
 * @(#)HondurasUtil.java
 *                       Copyright(c) 2010, Comviva Ltd.
 *                       All Rights Reserved
 * 
 * 
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       Author Date History
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       Lohit July 5, 2010 Initital Creation
 * 
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       Util file for Honduras.
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
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/**
 * @author avinash.kamthan
 *         This class must be extended if Operator specific implementation would
 *         be required
 *         Tax 1 Rate (Service Tax)= x %
 *         Tax2 Rate (Withholding tax) = y %
 *         Distributor Margin Rate= z%
 * 
 * 
 *         Tax1 Value=(x/(100+x))*MRP (tax in inclusive in MRP)
 *         Distributor Margin Value = (z/1000)*Transfer MRP
 *         Tax 2 Value = (y/100)*Distributor Margin Value
 *         Distributor Amount Payable = MRP – Distributor Margin Value –Tax2
 * 
 */
public class HondurasUtil extends OperatorUtil {

    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * For the Reversal
     * 
     * @param p_transferVO
     * @param p_tempTransferID
     * @return
     */
    public String formatC2SReversalTransferID(TransferVO p_transferVO, long p_tempTransferID) {
        if (_log.isDebugEnabled()) {
            _log.debug("formatC2SReversalTransferID", "entered:p_tempTransferID=" + p_tempTransferID + " p_transferVO: " + p_transferVO.toString());
        }
        final String METHOD_NAME = "formatC2SReversalTransferID";
        String returnStr = null;
        try {
            final String paddedTransferIDStr = BTSLUtil.padZeroesToLeft(String.valueOf(p_tempTransferID), C2S_TRANSFER_ID_PAD_LENGTH);
            returnStr = "X" + currentDateTimeFormatString(p_transferVO.getCreatedOn()) + "." + currentTimeFormatString(p_transferVO.getCreatedOn()) + "." + Constants
                .getProperty("INSTANCE_ID") + paddedTransferIDStr;

            p_transferVO.setTransferID(returnStr);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AircelUtil[]", "", "", "",
                "Not able to generate Transfer ID:" + e.getMessage());
            returnStr = null;
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("formatC2SReversalTransferID", "entered:returnStr=" + returnStr);
            }
        }
        return returnStr;
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
     * This method validate the LoginId.
     * 9:47:00 AM
     * HashMap
     * sushma.salve
     */
    public HashMap validateLoginId(String p_loginID) {
        _log.debug("validateLoginId", "Entered, p_userID= ", new String(p_loginID));
        final HashMap messageMap = new HashMap();
        return messageMap;
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
            String requestAmtStr = null;
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
                        receiverVO.setMsisdn(custMsisdn);
                        // receiverVO.setNetworkCode("TH");
                        p_c2sTransferVO.setReceiverVO(receiverVO);
                        if (SystemPreferences.USSD_NEW_TAGS_MANDATORY && PretupsI.GATEWAY_TYPE_USSD.equals(p_c2sTransferVO.getRequestGatewayType()))// cellid
                        // and
                        // switch
                        // id
                        // not
                        // mandatory
                        // in
                        // request
                        {
                            cellId = p_requestArr[6];
                            switchId = p_requestArr[7];
                            // validations to be done on cellId switchId to be
                            // put
                            // here
                            p_requestVO.setCellId(cellId);
                            p_requestVO.setSwitchId(switchId);
                        }
                        break;

                    }

                case 10:
                    {

                        if ((((ChannelUserVO) p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES)) {
                            try {
                                ChannelUserBL.validatePIN(p_con, channelUserVO, p_requestArr[9]);
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
                        receiverVO.setMsisdn(custMsisdn);
                        requestAmtStr = p_requestArr[2];
                        PretupsBL.validateAmount(p_c2sTransferVO, requestAmtStr);
                        // receiverVO.setNetworkCode("TH");
                        p_c2sTransferVO.setReceiverVO(receiverVO);
                        if (SystemPreferences.USSD_NEW_TAGS_MANDATORY && PretupsI.GATEWAY_TYPE_USSD.equals(p_c2sTransferVO.getRequestGatewayType()))// cellid
                        // and
                        // switch
                        // id
                        // not
                        // mandatory
                        // in
                        // request
                        {
                            cellId = p_requestArr[7];
                            switchId = p_requestArr[8];
                            // validations to be done on cellId switchId to be
                            // put
                            // here
                            p_requestVO.setCellId(cellId);
                            p_requestVO.setSwitchId(switchId);
                        }
                        break;

                    }
                case 11:
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
                        // Customer MSISDN Validation
                        custMsisdn = p_requestArr[1];
                        receiverVO.setMsisdn(custMsisdn);
                        // receiverVO.setNetworkCode("TH");
                        p_c2sTransferVO.setReceiverVO(receiverVO);
                        if (SystemPreferences.USSD_NEW_TAGS_MANDATORY && PretupsI.GATEWAY_TYPE_USSD.equals(p_c2sTransferVO.getRequestGatewayType()))// cellid
                        // and
                        // switch
                        // id
                        // not
                        // mandatory
                        // in
                        // request
                        {
                            // =======cellID switchID=============
                            cellId = p_requestArr[8];
                            switchId = p_requestArr[9];
                            // validations to be done on cellId switchId to be
                            // put
                            // here
                            p_requestVO.setCellId(cellId);
                            p_requestVO.setSwitchId(switchId);
                        }
                        // =======cellID switchID=============
                        break;

                    }

                case 12:
                    {

                        if ((((ChannelUserVO) p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES)) {
                            try {
                                ChannelUserBL.validatePIN(p_con, channelUserVO, p_requestArr[11]);
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
                        receiverVO.setMsisdn(custMsisdn);
                        requestAmtStr = p_requestArr[2];
                        PretupsBL.validateAmount(p_c2sTransferVO, requestAmtStr);
                        // receiverVO.setNetworkCode("TH");
                        p_c2sTransferVO.setReceiverVO(receiverVO);
                        if (SystemPreferences.USSD_NEW_TAGS_MANDATORY && PretupsI.GATEWAY_TYPE_USSD.equals(p_c2sTransferVO.getRequestGatewayType()))// cellid
                        // and
                        // switch
                        // id
                        // not
                        // mandatory
                        // in
                        // request
                        {
                            cellId = p_requestArr[9];
                            switchId = p_requestArr[10];
                            // validations to be done on cellId switchId to be
                            // put
                            // here
                            p_requestVO.setCellId(cellId);
                            p_requestVO.setSwitchId(switchId);
                            // =======cellID switchID=============
                        }
                        break;

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
