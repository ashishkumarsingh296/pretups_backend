/**
 * @(#)NigerUtil.java
 *                    Copyright(c) 2010, Comviva Technologies Ltd.
 *                    All Rights Reserved
 * 
 *                    Niger Util class
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    Author Date History
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    Lohit Audhkhasi March 08, 2010 Initital Creation
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 * 
 */

package com.btsl.pretups.util.clientutils;

import java.sql.Connection;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDetailsVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;

public class NigerUtil extends OperatorUtil {
    private final Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * validate Card Group Details
     * 
     * @param p_startRange
     *            String
     * @param p_endRange
     *            String
     * @param p_subService
     *            String
     * @throws Exception
     */
    @Override
    public void validateCardGroupDetails(String p_startRange, String p_endRange, String p_subService) throws Exception {
        final String METHOD_NAME = "validateCardGroupDetails";
        try {
            if (p_subService.split(":")[1].equals((String.valueOf(PretupsI.CHNL_SELECTOR_VG_VALUE))))
            // This implementation is comented because not needed at this time.
            // Will be implemented for any operator.
            {
                // if(Double.parseDouble(p_startRange)!=Double.parseDouble(
                // p_endRange))
                // throw new
                // BTSLBaseException(this,"validateCardGroupDetails","cardgroup.addc2scardgroup.error.invalidstartandendrange");
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            throw e;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.btsl.pretups.util.OperatorUtil#validateC2SGiftRechargeRequest(java
     * .sql.Connection,
     * com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO,
     * com.btsl.pretups.receiver.RequestVO)
     */
    @Override
    public void validateC2SGiftRechargeRequest(Connection p_con, C2STransferVO p_c2sTransferVO, RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "validateC2SGiftRechargeRequest";
        try {
            // get message array from requestVO
            String[] p_requestArr = p_requestVO.getRequestMessageArray();
            String custMsisdn = null;
            String gifterMsisdn = null;
            String gifterName = null;
            String requestAmtStr = null;
            ChannelUserVO channelUserVO = (ChannelUserVO) p_c2sTransferVO.getSenderVO();
            UserPhoneVO userPhoneVO = null;
            if (!channelUserVO.isStaffUser()) {
                userPhoneVO = channelUserVO.getUserPhoneVO();
            } else {
                userPhoneVO = channelUserVO.getStaffUserDetails().getUserPhoneVO();
            }
            int messageLen = p_requestArr.length;
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "messageLen: " + messageLen);
            }
            for (int i = 0; i < messageLen; i++) {
                if (_log.isDebugEnabled()) {
                    _log.debug(METHOD_NAME, "i: " + i + " value: " + p_requestArr[i]);
                }
            }
            switch (messageLen) {
            case 6: // SERVICE_KEYWORD(M) RECEIVEAR_MSISDN(M) AMOUNT(M)
                    // GIFTER_MSISDN(M) GIFTER_NAME(M) MPIN
            {
                // GRC <Mobile Number> <Amount> <Language> <Donor Number> <PIN>
                // Do the 000 check Default PIN
                if (userPhoneVO.getPinRequired().equals(PretupsI.YES)) {
                    try {
                        ChannelUserBL.validatePIN(p_con, channelUserVO, p_requestArr[5]);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                            p_con.commit();
                        }
                        throw be;
                    }
                }
                ReceiverVO receiverVO = new ReceiverVO();
                // Customer MSISDN Validation
                custMsisdn = p_requestArr[1];
                // validate customer(receiver) mobile numbers
                PretupsBL.validateMsisdn(p_con, receiverVO, p_c2sTransferVO.getRequestID(), custMsisdn);

                gifterMsisdn = p_requestArr[4];
                gifterName = p_requestArr[3];
                // validate gifter msisdn or not(To be decided)
                // p_reqquestArr[3]0
                validateGifterMsisdn(p_requestVO, p_c2sTransferVO.getRequestID(), gifterMsisdn);

                // validate gifter name or not(To be decided) p_reqquestArr[4]
                if (BTSLUtil.isNullString(gifterName)) {
                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CHNL_ERROR_GIFTER_NAME_NULL_GIFTRECHARGE);
                }
                p_requestVO.setGifterMSISDN(gifterMsisdn);
                p_requestVO.setGifterName(gifterName);

                // Recharge amount Validation
                requestAmtStr = p_requestArr[2];
                PretupsBL.validateAmount(p_c2sTransferVO, requestAmtStr);
                p_c2sTransferVO.setReceiverVO(receiverVO);
                // Changed on 27/05/07 for Service Type selector Mapping
                ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
                if (serviceSelectorMappingVO != null) {
                    p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                }
                p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                p_requestVO.setGifterLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                break;
            }

            case 7: // SERVICE_KEYWORD(M) RECEIVEAR_MSISDN(M) AMOUNT(M)
                    // RECEIVER_LOCALE(O) GIFTER_MSISDN(M) GIFTER_NAME(M) MPIN
            {

                // GRC 90232759 50 French 90232707 **** TX111111797
                // Do the 000 check Default PIN
                // if((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES)
                // &&
                // !PretupsI.DEFAULT_C2S_PIN.equals(BTSLUtil.decryptText((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getSmsPin())))
                if (userPhoneVO.getPinRequired().equals(PretupsI.YES)) {
                    try {
                        ChannelUserBL.validatePIN(p_con, channelUserVO, p_requestArr[6]);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                            p_con.commit();
                        }
                        throw be;
                    }
                }
                ReceiverVO receiverVO = new ReceiverVO();
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
                    int langCode = PretupsBL.getLocaleValueFromCode(p_requestVO, p_requestArr[3]);
                    if (LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)) == null) {
                        throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
                    }
                    p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
                }

                gifterMsisdn = p_requestArr[4];
                gifterName = p_requestArr[5];
                // validate gifter msisdn or not(To be decided)
                // p_reqquestArr[3]0
                validateGifterMsisdn(p_requestVO, p_c2sTransferVO.getRequestID(), gifterMsisdn);

                // validate gifter name or not(To be decided) p_reqquestArr[4]
                if (BTSLUtil.isNullString(gifterName)) {
                    throw new BTSLBaseException(this, "validateC2SRechargeRequest", PretupsErrorCodesI.CHNL_ERROR_GIFTER_NAME_NULL_GIFTRECHARGE);
                }
                p_requestVO.setGifterMSISDN(gifterMsisdn);
                p_requestVO.setGifterName(gifterName);
                p_requestVO.setGifterLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                // p_requestVO.setReqSelector(String.valueOf(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_TRANSFER_DEF_SELECTOR_CODE))));
                // Changed on 27/05/07 for Service Type selector Mapping
                ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
                if (serviceSelectorMappingVO != null) {
                    p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                }
                break;
            }

            case 8: // SERVICE_KEYWORD(M) RECEIVEAR_MSISDN(M) AMOUNT(M)
                    // SELECTOR(O) RECEIVER_LOCALE(O) GIFTER_MSISDN(M)
                    // GIFTER_NAME(M) MPIN
            {
                // validate pin if required
                if (userPhoneVO.getPinRequired().equals(PretupsI.YES)) {
                    try {
                        ChannelUserBL.validatePIN(p_con, channelUserVO, p_requestArr[7]);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                            p_con.commit();
                        }
                        throw be;
                    }
                }

                ReceiverVO receiverVO = new ReceiverVO();
                // Customer MSISDN Validation
                custMsisdn = p_requestArr[1];
                // validate customer(giftee) msisdn
                PretupsBL.validateMsisdn(p_con, receiverVO, p_c2sTransferVO.getRequestID(), custMsisdn);

                // Recharge amount Validation
                requestAmtStr = p_requestArr[2];
                PretupsBL.validateAmount(p_c2sTransferVO, requestAmtStr);
                p_c2sTransferVO.setReceiverVO(receiverVO);
                if (BTSLUtil.isNullString(p_requestArr[3])) {
                    if ("en".equalsIgnoreCase(p_requestVO.getLocale().getLanguage())) {
                        // p_requestVO.setReqSelector(String.valueOf(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_TRANSFER_DEF_SELECTOR_CODE))));
                        // Changed on 27/05/07 for Service Type selector Mapping
                        ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
                        if (serviceSelectorMappingVO != null) {
                            p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                        }
                    }

                } else {
                    p_requestVO.setReqSelector(p_requestArr[3]);
                }

                PretupsBL.getSelectorValueFromCode(p_requestVO);
                // changed for CRE_INT_CR00029 by ankit Zindal
                if (BTSLUtil.isNullString(p_requestVO.getReqSelector())) {
                    // p_requestVO.setReqSelector(String.valueOf(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_TRANSFER_DEF_SELECTOR_CODE))));
                    // Changed on 27/05/07 for Service Type selector Mapping
                    ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
                    if (serviceSelectorMappingVO != null) {
                        p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                    }
                }
                if (BTSLUtil.isNullString(p_requestArr[4])) {
                    p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                } else {
                    int langCode = PretupsBL.getLocaleValueFromCode(p_requestVO, p_requestArr[4]);
                    if (LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)) == null) {
                        throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
                    }
                    p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
                }

                p_requestVO.setGifterLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                gifterMsisdn = p_requestArr[5];
                gifterName = p_requestArr[6];
                // validate gifter msisdn or not(To be decided) p_reqquestArr[5]
                validateGifterMsisdn(p_requestVO, p_c2sTransferVO.getRequestID(), gifterMsisdn);

                // validate gifter name or not(To be decided) p_reqquestArr[6]
                if (BTSLUtil.isNullString(gifterName)) {
                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CHNL_ERROR_GIFTER_NAME_NULL_GIFTRECHARGE);
                }
                p_requestVO.setGifterMSISDN(gifterMsisdn);
                p_requestVO.setGifterName(gifterName);
                break;
            }
            case 9: // SERVICE_KEYWORD(M) RECEIVEAR_MSISDN(M) AMOUNT(M)
                    // SELECTOR(O) RECEIVER_LOCALE(O) SENDER_LOCALE(O)
                    // GIFTER_MSISDN(M) GIFTER_NAME(M) MPIN
            {
                // validate pin if required
                if (userPhoneVO.getPinRequired().equals(PretupsI.YES)) {
                    try {
                        ChannelUserBL.validatePIN(p_con, channelUserVO, p_requestArr[8]);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                            p_con.commit();
                        }
                        throw be;
                    }
                }
                ReceiverVO receiverVO = new ReceiverVO();

                custMsisdn = p_requestArr[1];
                // Customer MSISDN Validation
                PretupsBL.validateMsisdn(p_con, receiverVO, p_c2sTransferVO.getRequestID(), custMsisdn);

                // Recharge amount Validation
                requestAmtStr = p_requestArr[2];
                PretupsBL.validateAmount(p_c2sTransferVO, requestAmtStr);
                p_c2sTransferVO.setReceiverVO(receiverVO);
                if (BTSLUtil.isNullString(p_requestArr[3])) {
                    if ("en".equalsIgnoreCase(p_requestVO.getLocale().getLanguage())) {
                        // p_requestVO.setReqSelector(String.valueOf(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_TRANSFER_DEF_SELECTOR_CODE))));
                        // Changed on 27/05/07 for Service Type selector Mapping
                        ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
                        if (serviceSelectorMappingVO != null) {
                            p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                        }
                    }
                } else {
                    p_requestVO.setReqSelector(p_requestArr[3]);
                }

                PretupsBL.getSelectorValueFromCode(p_requestVO);
                // changed for CRE_INT_CR00029 by ankit Zindal
                if (BTSLUtil.isNullString(p_requestVO.getReqSelector())) {
                    // p_requestVO.setReqSelector(String.valueOf(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_TRANSFER_DEF_SELECTOR_CODE))));
                    // Changed on 27/05/07 for Service Type selector Mapping
                    ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
                    if (serviceSelectorMappingVO != null) {
                        p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                    }
                }
                // For handling of sender locale
                if (BTSLUtil.isNullString(p_requestArr[5])) {
                    p_requestVO.setSenderLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                } else {
                    int langCode = PretupsBL.getLocaleValueFromCode(p_requestVO, p_requestArr[5]);
                    p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
                    // ChangeID=LOCALEMASTER
                    // Sender locale has to be overwritten in transferVO also.
                    p_c2sTransferVO.setLocale(p_requestVO.getSenderLocale());
                    p_c2sTransferVO.setLanguage(p_c2sTransferVO.getLocale().getLanguage());
                    p_c2sTransferVO.setCountry(p_c2sTransferVO.getLocale().getCountry());
                }
                if (_log.isDebugEnabled()) {
                    _log.debug(this, "sender locale: =" + p_requestVO.getSenderLocale());
                }

                if (BTSLUtil.isNullString(p_requestArr[4])) {
                    p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                } else {
                    int langCode = PretupsBL.getLocaleValueFromCode(p_requestVO, p_requestArr[4]);
                    if (LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)) == null) {
                        throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
                    }
                    p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
                }
                gifterMsisdn = p_requestArr[6];
                gifterName = p_requestArr[7];
                // validate gifter msisdn or not(To be decided) p_reqquestArr[7]
                validateGifterMsisdn(p_requestVO, p_c2sTransferVO.getRequestID(), gifterMsisdn);

                // validate gifter name or not(To be decided) p_reqquestArr[8]
                if (BTSLUtil.isNullString(gifterName)) {
                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CHNL_ERROR_GIFTER_NAME_NULL_GIFTRECHARGE);
                }
                p_requestVO.setGifterLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                p_requestVO.setGifterMSISDN(gifterMsisdn);
                p_requestVO.setGifterName(gifterName);
                break;
            }
            case 10: // SERVICE_KEYWORD(M) RECEIVEAR_MSISDN(M) AMOUNT(M)
                     // SELECTOR(O) RECEIVER_LOCALE(O) SENDER_LOCALE(O)
                     // GIFTER_MSISDN(M) GIFTER_NAME(M) GIFTER_LOCALE(O) MPIN
            {
                //
                if (userPhoneVO.getPinRequired().equals(PretupsI.YES)) {
                    try {
                        ChannelUserBL.validatePIN(p_con, channelUserVO, p_requestArr[9]);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                            p_con.commit();
                        }
                        throw be;
                    }
                }

                ReceiverVO receiverVO = new ReceiverVO();
                // Customer MSISDN Validation
                custMsisdn = p_requestArr[1];

                PretupsBL.validateMsisdn(p_con, receiverVO, p_c2sTransferVO.getRequestID(), custMsisdn);

                // Recharge amount Validation
                requestAmtStr = p_requestArr[2];
                PretupsBL.validateAmount(p_c2sTransferVO, requestAmtStr);
                p_c2sTransferVO.setReceiverVO(receiverVO);
                if (BTSLUtil.isNullString(p_requestArr[3])) {
                    if ("en".equalsIgnoreCase(p_requestVO.getLocale().getLanguage())) {
                        // p_requestVO.setReqSelector(String.valueOf(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_TRANSFER_DEF_SELECTOR_CODE))));
                        // Changed on 27/05/07 for Service Type selector Mapping
                        ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
                        if (serviceSelectorMappingVO != null) {
                            p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                        }
                    }
                } else {
                    p_requestVO.setReqSelector(p_requestArr[3]);
                }

                PretupsBL.getSelectorValueFromCode(p_requestVO);
                // changed for CRE_INT_CR00029 by ankit Zindal
                if (BTSLUtil.isNullString(p_requestVO.getReqSelector())) {
                    // p_requestVO.setReqSelector(String.valueOf(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_TRANSFER_DEF_SELECTOR_CODE))));
                    // Changed on 27/05/07 for Service Type selector Mapping
                    ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
                    if (serviceSelectorMappingVO != null) {
                        p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                    }
                }
                // For handling of sender locale
                if (BTSLUtil.isNullString(p_requestArr[5])) {
                    p_requestVO.setSenderLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                } else {
                    int langCode = PretupsBL.getLocaleValueFromCode(p_requestVO, p_requestArr[5]);
                    p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
                    // ChangeID=LOCALEMASTER
                    // Sender locale has to be overwritten in transferVO also.
                    p_c2sTransferVO.setLocale(p_requestVO.getSenderLocale());
                    p_c2sTransferVO.setLanguage(p_c2sTransferVO.getLocale().getLanguage());
                    p_c2sTransferVO.setCountry(p_c2sTransferVO.getLocale().getCountry());
                }
                if (_log.isDebugEnabled()) {
                    _log.debug(this, "sender locale: =" + p_requestVO.getSenderLocale());
                }

                if (BTSLUtil.isNullString(p_requestArr[4])) {
                    p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                } else {
                    int langCode = PretupsBL.getLocaleValueFromCode(p_requestVO, p_requestArr[4]);
                    if (LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)) == null) {
                        throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
                    }
                    p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
                }
                gifterMsisdn = p_requestArr[6];
                gifterName = p_requestArr[7];
                // validate gifter msisdn or not(To be decided) p_reqquestArr[7]
                validateGifterMsisdn(p_requestVO, p_c2sTransferVO.getRequestID(), gifterMsisdn);

                // validate gifter name or not(To be decided) p_reqquestArr[8]
                if (BTSLUtil.isNullString(gifterName)) {
                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CHNL_ERROR_GIFTER_NAME_NULL_GIFTRECHARGE);
                }
                p_requestVO.setGifterMSISDN(gifterMsisdn);
                p_requestVO.setGifterName(gifterName);

                if (BTSLUtil.isNullString(p_requestArr[8])) {
                    p_requestVO.setGifterLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                } else {
                    int langCode = PretupsBL.getLocaleValueFromCode(p_requestVO, p_requestArr[8]);
                    if (LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)) == null) {
                        throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
                    }
                    p_requestVO.setGifterLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
                }
                break;
            }
            default:
                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() }, null);
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(METHOD_NAME, "  Exception while validating user message :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NigerUtil[validateC2SGiftRechargeRequest]", "", "", "", "Exception while validating user message" + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Exiting ");
        }
    }

    /**
     * 
     * @param p_RequestVO
     * @param p_requestID
     * @param p_gifterMsisdn
     * @throws BTSLBaseException
     */
    private void validateGifterMsisdn(RequestVO p_RequestVO, String p_requestID, String p_gifterMsisdn) throws BTSLBaseException {
        final String METHOD_NAME = "validateGifterMsisdn";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, p_requestID, "Entered for p_gifterMsisdn= " + p_gifterMsisdn);
        }
        String[] strArr = null;
        try {
            if (BTSLUtil.isNullString(p_gifterMsisdn)) {
                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CHNL_ERROR_RECR_MSISDN_BLANK);
            }
            p_gifterMsisdn = PretupsBL.getFilteredMSISDN(p_gifterMsisdn);
            if ((p_gifterMsisdn.length() < ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH))).intValue() || p_gifterMsisdn.length() > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue())) {
                if (((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH))).intValue() != ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue()) {
                    strArr = new String[] { p_gifterMsisdn, String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH))).intValue()), String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue()) };
                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CHNL_ERROR_GIFTER_MSISDN_NOTINRANGE_GIFTRECHARGE, 0, strArr, null);
                } else {
                    strArr = new String[] { p_gifterMsisdn, String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH))).intValue()) };
                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CHNL_ERROR_GIFTER_MSISDN_LEN_NOTSAME_GIFTRECHARGE, 0, strArr, null);
                }
            }
            try {
                long lng = Long.parseLong(p_gifterMsisdn);
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.error(METHOD_NAME, "  Exception ::" + e.getMessage());
                strArr = new String[] { p_gifterMsisdn };
                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CHNL_ERROR_GIFTER_MSISDN_NOTNUMERIC_GIFTRECHARGE, 0, strArr, null);
            }
            p_RequestVO.setGifterMSISDN(p_gifterMsisdn);
            if (_log.isDebugEnabled() && p_RequestVO.getGifterMSISDN() != null) {
                _log.debug("", "*********************" + p_RequestVO.getGifterMSISDN());
            }

        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(METHOD_NAME, "  Exception while validating gifter msisdn :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NigerUtil[validateMsisdn]", "", "", "", "Exception while validating gifter msisdn" + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, p_requestID, "Exiting for p_gifterMsisdn= " + p_gifterMsisdn);
        }
    }
	
	/**
    	 * Method to set the value for transfer.
    	 * This methos is called from CardGroupBL. At this time this method set various values in the transferVO
    	 * For any other operator who wants to run system on CVG mode, we can change values setting based on subservice. 
    	 * added for CRE_INT_CR00029 by ankit Zindal
    	 * 
    	 * @param p_subService String
    	 * @param p_cardGroupDetailVO CardGroupDetailsVO
    	 * @param p_transferVO TransferVO 
    	 * 
    	 * @throws Exception
    	 * @see com.btsl.pretups.util.OperatorUtilI#setCalculatedCardGroupValues(String, CardGroupDetailsVO, TransferVO)
    	 */
    	public void setCalculatedCardGroupValues(String p_subService,CardGroupDetailsVO p_cardGroupDetailVO,TransferVO p_transferVO) throws Exception
    	{

    		if(_log.isDebugEnabled()) _log.debug("setCalculatedCardGroupValues","Entered for p_subService= "+p_subService+",p_cardGroupDetailVO="+p_cardGroupDetailVO.toString()+",p_transferVO="+p_transferVO.toString());
    		try
    		{
    			/**
    			 * In case of CVG all values are set as calculated.
    			 * In case of VG transfer value is set to 0.
    			 * In case of C, validity and grace will be set to 0.
    			 * 
    			 */
    			TransferItemVO transferItemVO=null;
    			int bonusValidityValue=Integer.parseInt(String.valueOf(p_cardGroupDetailVO.getBonusValidityValue()));
    			int validityPeriodValue=p_cardGroupDetailVO.getValidityPeriod();
    			long transferValue=p_cardGroupDetailVO.getTransferValue();
    			long bonusValue=p_cardGroupDetailVO.getBonusTalkTimeValue();
    			transferItemVO=(TransferItemVO)p_transferVO.getTransferItemList().get(1);

    			//This feature is specific to the operator
    			//if operator wants, amount is needed to be deducted for Get number back service
    			//so transfer the value to user after amount deducted for number back feature
    			//so net transfer value is transferValue=transferValue-amountDeducted
    			//and accessFee is normalAccessFee+amountDeducted
    			if((String.valueOf(PretupsI.CHNL_SELECTOR_CVG_VALUE)).equals(p_subService))//CVG
    			{
    				p_transferVO.setReceiverBonusValidity(bonusValidityValue);
    				p_transferVO.setReceiverGracePeriod(p_cardGroupDetailVO.getGracePeriod());
    				p_transferVO.setReceiverValidity(validityPeriodValue);
    				//Is Bonus Validity on Requested Value ??
    				calculateValidity(p_transferVO,transferItemVO.getTransferDateTime(),transferItemVO.getPreviousExpiry(),p_cardGroupDetailVO.getValidityPeriodType(),validityPeriodValue,bonusValidityValue);
    				p_transferVO.setReceiverTransferValue(transferValue);
    				transferItemVO.setTransferValue(transferValue);
    				transferItemVO.setGraceDaysStr(String.valueOf(p_cardGroupDetailVO.getGracePeriod()));
    				transferItemVO.setValidity(validityPeriodValue);
    				p_transferVO.setReceiverBonusValue(bonusValue);
    			}
    			else if((String.valueOf(PretupsI.CHNL_SELECTOR_C_VALUE)).equals(p_subService))//C
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
    			else if((String.valueOf(PretupsI.CHNL_SELECTOR_VG_VALUE)).equals(p_subService))//VG
    			{
    				p_transferVO.setReceiverBonusValidity(bonusValidityValue);
    				p_transferVO.setReceiverGracePeriod(p_cardGroupDetailVO.getGracePeriod());
    				p_transferVO.setReceiverValidity(validityPeriodValue);
    				//Is Bonus Validity on Requested Value ??
    				calculateValidity(p_transferVO,transferItemVO.getTransferDateTime(),transferItemVO.getPreviousExpiry(),p_cardGroupDetailVO.getValidityPeriodType(),validityPeriodValue,bonusValidityValue);
    				p_transferVO.setReceiverTransferValue(0);
    				transferItemVO.setTransferValue(0);
    				transferItemVO.setGraceDaysStr(String.valueOf(p_cardGroupDetailVO.getGracePeriod()));
    				transferItemVO.setValidity(validityPeriodValue);
    				p_transferVO.setReceiverBonusValue(0);
    			}
    			else		//PRCMDA
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

    		}
    		catch(Exception e){
    			throw e;
    		} 
    		finally{
    			if(_log.isDebugEnabled()) _log.debug("setCalculatedCardGroupValues","Finally for p_subService= "+p_subService+",p_cardGroupDetailVO="+p_cardGroupDetailVO.toString()+",p_transferVO="+p_transferVO.toString());
    		}
    	}
}
