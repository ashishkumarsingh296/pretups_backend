/*
 * Created on Dec 21, 2009
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.client.pretups.util.clientutils;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Locale;
import java.util.StringTokenizer;

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
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.privaterecharge.businesslogic.PrivateRchrgVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/**
 * @author dhiraj.tiwari
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class VFEUtil extends OperatorUtil implements OperatorUtilI {
    private Log _log = LogFactory.getLog(this.getClass().getName());

    public void populateBonusListAfterValidation(HashMap p_map, C2STransferVO p_c2stransferVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("populateBonusListAfterValidation", "Entered");
        }
        final String METHOD_NAME = "populateBonusListAfterValidation";

        try {
            p_c2stransferVO.getReceiverTransferItemVO().setPrevBundleBals((String) p_map.get("IN_RESP_BUNDLE_PREV_BALS"));
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        try {
            p_c2stransferVO.getReceiverTransferItemVO().setPrevBundleExpiries((String) p_map.get("IN_RESP_BUNDLE_PREV_VALIDITY"));
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        try {
            p_c2stransferVO.getReceiverTransferItemVO().setBundleTypes((String) p_map.get("IN_RESP_BUNDLE_CODES"));
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }

        if (_log.isDebugEnabled()) {
            _log.debug("populateBonusListAfterValidation", "Exited");
        }
    }

    /*
     * public void updateBonusListAfterTopup(HashMap p_map,C2STransferVO
     * p_c2stransferVO)
     * {
     * if (_log.isDebugEnabled())
     * _log.debug("updateBonusListAfterTopup","Entered ");
     * String finalMsg="";
     * try
     * {
     * String postBalance[]=null;
     * String postValidity[]=null;
     * String postGrace[]=null;
     * String recAccountCodes[]=null;
     * String tempString=null;
     * String splitChar="%2C";
     * 
     * if(BTSLUtil.isNullString(p_c2stransferVO.getErrorCode()))
     * {
     * String changedBundleCodes = (String)p_map.get("CHANGED_BUNDLE_CODES");
     * p_c2stransferVO.getReceiverTransferItemVO().setChangedBundleCodes(
     * changedBundleCodes);
     * 
     * if(!BTSLUtil.isNullString(changedBundleCodes) &&
     * changedBundleCodes.length()>0)
     * {
     * String[] changedBundleCodesArr=changedBundleCodes.split(splitChar);
     * tempString=(String)p_map.get("IN_RESP_BUNDLE_POST_BALS");
     * if (!BTSLUtil.isNullString(tempString) && tempString.length()>0)
     * postBalance=tempString.split(splitChar);
     * 
     * tempString=(String)p_map.get("IN_RESP_BUNDLE_POST_VALIDITY");
     * if (!BTSLUtil.isNullString(tempString) && tempString.length()>0)
     * postValidity=tempString.split(splitChar);
     * 
     * tempString=(String)p_map.get("IN_RESP_BUNDLE_CODES_CR");
     * if (!BTSLUtil.isNullString(tempString) && tempString.length()>0)
     * recAccountCodes=tempString.split(splitChar);
     * 
     * for(int i=0,j=changedBundleCodesArr.length ;i<j;i++)
     * {
     * for(int k=0,l=recAccountCodes.length ;k<l;k++)
     * {
     * if(recAccountCodes[k].equals(changedBundleCodesArr[i]))
     * {
     * finalMsg=finalMsg+recAccountCodes[k]+": (Amt)"+postBalance[k]+",(Val)"+
     * BTSLUtil
     * .getDateStringFromDate(InterfaceUtil.getDateFromDateString(postValidity
     * [k],"yyyyMMdd"))+",";
     * break;
     * }
     * }
     * }
     * finalMsg =
     * "Main: (Amt)"+p_c2stransferVO.getReceiverTransferItemVO().getPostBalance
     * ()+",(Val)"+BTSLUtil.getDateStringFromDate(p_c2stransferVO.
     * getReceiverTransferItemVO().getNewExpiry())+","+finalMsg.substring(0);
     * p_c2stransferVO.getReceiverTransferItemVO().setChangedBundleCodes(finalMsg
     * );
     * }
     * }
     * }
     * catch(Exception e)
     * {
     * finalMsg =
     * "Main: (Amt)"+p_c2stransferVO.getReceiverTransferItemVO().getPostBalance
     * ();
     * e.printStackTrace();
     * }
     * finally
     * {
     * if (_log.isDebugEnabled())
     * _log.debug("updateBonusListAfterTopup","Exited ");
     * 
     * }
     * }
     */

    public void updateBonusListAfterTopup(HashMap p_map, C2STransferVO p_c2stransferVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("updateBonusListAfterTopup", "Entered ");
        }
        final String METHOD_NAME = "updateBonusListAfterTopup";
        String finalMsg = "";
        try {
            String postBalance[] = null;
            String postValidity[] = null;
            final String postGrace[] = null;
            String recAccountCodes[] = null;
            String tempString = null;
            final String splitChar = "%2C";

            if (BTSLUtil.isNullString(p_c2stransferVO.getErrorCode())) {
                final String changedBundleCodes = (String) p_map.get("CHANGED_BUNDLE_CODES");
                p_c2stransferVO.getReceiverTransferItemVO().setChangedBundleCodes(changedBundleCodes);

                if (!BTSLUtil.isNullString(changedBundleCodes) && changedBundleCodes.length() > 0) {
                    final String[] changedBundleCodesArr = changedBundleCodes.split(splitChar);
                    tempString = (String) p_map.get("IN_RESP_BUNDLE_POST_BALS");
                    if (!BTSLUtil.isNullString(tempString) && tempString.length() > 0) {
                        postBalance = tempString.split(splitChar);
                    }

                    tempString = (String) p_map.get("IN_RESP_BUNDLE_POST_VALIDITY");
                    if (!BTSLUtil.isNullString(tempString) && tempString.length() > 0) {
                        postValidity = tempString.split(splitChar);
                    }

                    tempString = (String) p_map.get("IN_RESP_BUNDLE_CODES_CR");
                    if (!BTSLUtil.isNullString(tempString) && tempString.length() > 0) {
                        recAccountCodes = tempString.split(splitChar);
                    }

                    for (int i = 0, j = changedBundleCodesArr.length; i < j; i++) {
                        for (int k = 0, l = recAccountCodes.length; k < l; k++) {
                            if (recAccountCodes[k].equals(changedBundleCodesArr[i])) {
                                // finalMsg=finalMsg+recAccountCodes[k]+" - "+postBalance[k]+" - "+BTSLUtil.getDateStringFromDate(InterfaceUtil.getDateFromDateString(postValidity[k],"yyyyMMdd"))+",";
                                finalMsg = finalMsg + recAccountCodes[k] + " " + PretupsBL.getDisplayAmount(Long.valueOf(postBalance[k])) + " " + BTSLUtil
                                    .getDateStringFromDate(InterfaceUtil.getDateFromDateString(postValidity[k], "yyyyMMdd")) + ",";
                                break;
                            }
                        }
                    }
                    finalMsg = finalMsg.substring(0);
                    p_c2stransferVO.getReceiverTransferItemVO().setChangedBundleCodes(finalMsg);
                }
            }
        } catch (Exception e) {
            finalMsg = "Main: (Amt)" + p_c2stransferVO.getReceiverTransferItemVO().getPostBalance();
            _log.errorTrace(METHOD_NAME, e);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("updateBonusListAfterTopup", "Exited ");
            }

        }
    }

    /**
     * Method validatePassword.
     * 
     * @author vikram.kumar
     * @created on 13/01/10
     * @param p_loginID
     *            String
     * @param p_password
     *            String
     * @return String
     */
    public HashMap validatePassword(String p_loginID, String p_password) {
        _log.debug("validatePassword", "Entered, p_userID= ", new String(p_loginID + ", Password= " + p_password));
        final HashMap messageMap = new HashMap();
        if (!BTSLUtil.isNullString(p_password)) {
            String defaultPin = BTSLUtil.getDefaultPasswordNumeric(p_password);
            if (defaultPin.equals(p_password)) {
                return messageMap;
            }
            defaultPin = BTSLUtil.getDefaultPasswordText(p_password);
            if (defaultPin.equals(p_password)) {
                return messageMap;
            }
        }
        if (p_password.length() < ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_LOGIN_PWD_LENGTH))).intValue() || p_password.length() > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_LOGIN_PWD_LENGTH))).intValue()) {
            final String[] args = { String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_LOGIN_PWD_LENGTH))).intValue()), String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_LOGIN_PWD_LENGTH))).intValue()) };
            messageMap.put("operatorutil.validatepassword.error.passwordlenerr", args);
        }
        // for consecutive and same characters
        final int result = BTSLUtil.isSMSPinValid(p_password);
        if (result == -1) {
            messageMap.put("operatorutil.validatepassword.error.passwordsamedigit", null);
        } else if (result == 1) {
            messageMap.put("operatorutil.validatepassword.error.passwordconsecutive", null);
        }
        if (!BTSLUtil.containsChar(p_password)) {
            messageMap.put("operatorutil.validatepassword.error.passwordnotcontainschar", null);
        }
        // for checkin that password doesn't contains more than 2 succesive
        // identical character.
        boolean sucIdenticalChar = false;
        char pos1 = 0, pos = 0;
        int count = 0;
        for (int k = 0; k < p_password.length(); k++) {
            pos = p_password.charAt(k);
            if (k < p_password.length() - 1) {
                pos1 = p_password.charAt(k + 1);
            }

            if (pos == pos1) {
                count++;
            } else if (count > 1) {
                sucIdenticalChar = true;
                break;
            } else {
                count = 0;
            }
        }
        if (sucIdenticalChar) {
            messageMap.put("operatorutil.validatepassword.error.passwordconsecutiveidentical", null);
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
        // password is same as login id.
        if (p_loginID.equals(p_password)) {
            messageMap.put("operatorutil.validatepassword.error.sameusernamepassword", null);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("validatePassword", "Exiting ");
        }
        return messageMap;
    }

    /**
     * Method validatePIN.
     * 
     * @author vikram.kumar
     * @created on 14/01/10
     * @param p_pin
     *            String
     * @return HashMap
     */
    /*
     * public HashMap pinValidate(String p_pin)
     * {
     * _log.debug("validatePIN","Entered, PIN= "+p_pin);
     * HashMap messageMap=new HashMap();
     * 
     * if(!BTSLUtil.isNumeric(p_pin))
     * messageMap.put("operatorutil.validatepin.error.pinnotnumeric",null);
     * if (p_pin.length() < ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_SMS_PIN_LENGTH))).intValue() ||
     * p_pin.length() > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_SMS_PIN_LENGTH))).intValue())
     * {
     * String[]
     * args={String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_SMS_PIN_LENGTH))).intValue()),String
     * .valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_SMS_PIN_LENGTH))).intValue())};
     * messageMap.put("operatorutil.validatepin.error.smspinlenerr", args);
     * }
     * int result=BTSLUtil.isSMSPinValid(p_pin);
     * if(result==-1)
     * messageMap.put("operatorutil.validatepin.error.pinsamedigit",null);
     * else if(result==1)
     * messageMap.put("operatorutil.validatepin.error.pinconsecutive",null);
     * if(_log.isDebugEnabled())
     * _log.debug("validatePIN","Exiting messageMap.size()="+messageMap.size());
     * return messageMap;
     * }
     */
    /**
     * Method isPinUserId
     * 
     * @author vikram.kumar
     * @created on 21/01/2010
     * @param p_pin
     *            String
     * @param p_userId
     *            String
     * @return HashMap
     */
    public boolean isPinUserId(String p_pin, String p_userId) {
        _log.debug("isPinUserId", "Entered, p_pin= " + p_pin + " p_userId=" + p_userId);
        if (p_pin != null && p_pin.equals(p_userId)) {
            return true;
        } else {
            return false;
        }
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
                channelUserVO.getStaffUserDetails().setServiceTypes(p_requestVO.getServiceType());
                channelUserVO.setServiceTypes(p_requestVO.getServiceType());
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
                        // Change for the SID logic
                        p_requestVO.setSid(custMsisdn);
                        receiverVO.setSid(custMsisdn);
                        PrivateRchrgVO prvo = null;
                        if ((prvo = getPrivateRechargeDetails(p_con, custMsisdn)) != null) {
                            p_c2sTransferVO.setSubscriberSID(custMsisdn);
                            custMsisdn = prvo.getMsisdn();
                        }
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
                        // Change for the SID logic
                        p_requestVO.setSid(custMsisdn);
                        receiverVO.setSid(custMsisdn);
                        PrivateRchrgVO prvo = null;
                        if ((prvo = getPrivateRechargeDetails(p_con, custMsisdn)) != null) {
                            p_c2sTransferVO.setSubscriberSID(custMsisdn);
                            custMsisdn = prvo.getMsisdn();
                        }
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
                        // Change for the SID logic
                        p_requestVO.setSid(custMsisdn);
                        receiverVO.setSid(custMsisdn);
                        PrivateRchrgVO prvo = null;
                        if ((prvo = getPrivateRechargeDetails(p_con, custMsisdn)) != null) {
                            p_c2sTransferVO.setSubscriberSID(custMsisdn);
                            custMsisdn = prvo.getMsisdn();
                        }
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

                        PretupsBL.getSelectorValueFromCode(p_requestVO);
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
                        // Change for the SID logic
                        p_requestVO.setSid(custMsisdn);
                        receiverVO.setSid(custMsisdn);
                        PrivateRchrgVO prvo = null;
                        if ((prvo = getPrivateRechargeDetails(p_con, custMsisdn)) != null) {
                            p_c2sTransferVO.setSubscriberSID(custMsisdn);
                            custMsisdn = prvo.getMsisdn();
                        }
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

                        PretupsBL.getSelectorValueFromCode(p_requestVO);
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

    // Added by Amit Raheja for NNP
    public String getSystemFilteredMSISDN(String p_msisdn) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("getSystemFilteredMSISDN", "Entered p_msisdn:" + p_msisdn);
        }
        final String METHOD_NAME = "getSystemFilteredMSISDN";
        String msisdn = null;
        boolean prefixFound = false;
        String prefix = null;
        String old_prefix = null;
        String new_prefix = null;
        String new_old_mapping = null;
        try {
            if (p_msisdn.length() >= ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH))).intValue()) {
                if (_log.isDebugEnabled()) {
                    _log.debug("getSystemFilteredMSISDN", "((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.MSISDN_PREFIX_LIST_CODE)):" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.MSISDN_PREFIX_LIST_CODE)));
                }

                final StringTokenizer strTok = new StringTokenizer(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.MSISDN_PREFIX_LIST_CODE)), ",");
                while (strTok.hasMoreTokens()) {
                    prefix = strTok.nextToken();
                    if (p_msisdn.startsWith(prefix, 0)) {
                        prefixFound = true;
                        break;
                    } else {
                        continue;
                    }
                }
                if (prefixFound) {
                    msisdn = p_msisdn.substring(prefix.length());
                } else {
                    msisdn = p_msisdn;
                }

                final StringTokenizer strToken = new StringTokenizer(SystemPreferences.MSISDN_MIGRATION_LIST, ",");
                while (strToken.hasMoreTokens()) {
                    new_old_mapping = strToken.nextToken();
                    old_prefix = new_old_mapping.substring(0, new_old_mapping.indexOf(':'));
                    new_prefix = new_old_mapping.substring(new_old_mapping.indexOf(':') + 1);
                    // if(msisdn.startsWith(old_prefix,0)&&
                    // (old_prefix.length()==new_prefix.length() ||
                    // msisdn.length()<((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue() ))
                    if (msisdn.startsWith(old_prefix, 0) && ((old_prefix.length() == new_prefix.length() && msisdn.length() == ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue()) || (old_prefix
                        .length() < new_prefix.length() && msisdn.length() < ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue()))) {
                        msisdn = msisdn.replaceFirst(old_prefix, new_prefix);
                        break;
                    }
                }

            } else {
                msisdn = p_msisdn;
            }
        } catch (Exception e) {
            _log.error("getSystemFilteredMSISDN", "Exception while getting the mobile no from passed no=" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorUtil[getSystemFilteredMSISDN]", "", p_msisdn,
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "getSystemFilteredMSISDN", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("getSystemFilteredMSISDN", "Exiting Filtered msisdn=" + msisdn);
            }
        }
        return msisdn;
    }

    // Addition ends
    /**
     * Method formatC2STransferID.
     * 
     * @param p_transferVO
     *            TransferVO
     * @param p_tempTransferID
     *            long
     * @return String
     * @see com.btsl.pretups.util.OperatorUtilI#formatC2STransferID(TransferVO,
     *      long)
     */
    public String formatC2STransferID(TransferVO p_transferVO, long p_tempTransferID) {
        String returnStr = null;
        final String METHOD_NAME = "formatC2STransferID";
        try {
            // ReceiverVO receiverVO=(ReceiverVO)p_transferVO.getReceiverVO();
            // String currentYear=BTSLUtil.getFinancialYearLastDigits(2);
            final String paddedTransferIDStr = BTSLUtil.padZeroesToLeft(String.valueOf(p_tempTransferID), C2S_TRANSFER_ID_PAD_LENGTH);
            // String
            // paddedTransferIDStr=BTSLUtil.padZeroesToLeft(Long.toHexString(p_tempTransferID),C2S_TRANSFER_ID_PAD_LENGTH);
            // returnStr=receiverVO.getNetworkCode()+"/"+currentYear+"/"+paddedTransferIDStr;
            // returnStr="R"+currentDateTimeFormatString(p_transferVO.getCreatedOn())+"."+currentTimeFormatString(p_transferVO.getCreatedOn())+"."+paddedTransferIDStr;
            returnStr = "R" + currentDateTimeFormatString(p_transferVO.getCreatedOn()) + "." + currentTimeFormatString(p_transferVO.getCreatedOn()) + "." + Constants
                .getProperty("INSTANCE_ID") + paddedTransferIDStr;
            p_transferVO.setTransferID(returnStr);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorUtil[]", "", "", "",
                "Not able to generate Transfer ID:" + e.getMessage());
            returnStr = null;
        }
        return returnStr;
    }

}
