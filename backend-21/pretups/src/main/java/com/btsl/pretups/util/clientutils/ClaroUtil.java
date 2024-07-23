/**
 * @(#)ClaroUtil.java
 *                    Copyright(c) 2010, Comviva Technologies Ltd.
 *                    All Rights Reserved
 * 
 *                    Claro Util class
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    Author Date History
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    Zeeshan Aleem July 27, 2016 Initital Creation
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 * 
 */

package com.btsl.pretups.util.clientutils;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.Locale;
import java.util.Random;

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
import com.btsl.pretups.currencyconversion.businesslogic.CurrencyConversionCache;
import com.btsl.pretups.currencyconversion.businesslogic.CurrencyConversionVO;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.privaterecharge.businesslogic.PrivateRchrgVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

public class ClaroUtil extends OperatorUtil {
    private  final Log _log = LogFactory.getLog(this.getClass().getName());
    @Override
    public String c2sTransferTDRLog(C2STransferVO p_c2sTransferVO, TransferItemVO p_senderTransferItemVO, TransferItemVO p_receiverTransferItemVO) {
    	final String METHOD_NAME = "c2sTransferTDRLog";    	
    	String returnStr = null;
    	CallableStatement cstmt = null;
    	Connection con = null;
    	int res=0;
    	try{    
    		con=OracleUtil.getExternalDBConnection();    		
    		String getDBUSERByUserIdSql = "{call TTMAF(?,?,?,?,?)}";
    		cstmt = con.prepareCall(getDBUSERByUserIdSql);
    		cstmt.setString(1, p_c2sTransferVO.getSenderMsisdn());
    		cstmt.setString(2,p_c2sTransferVO.getReceiverMsisdn());
    		cstmt.setString(3,p_c2sTransferVO.getServiceType());
    		cstmt.setLong(4,p_c2sTransferVO.getTransferValue());   
    		System.out.println(((ChannelUserVO)p_senderTransferItemVO.getTransferVO()).getParentID());
    		System.out.println(((ChannelUserVO)p_senderTransferItemVO.getTransferVO()).getOwnerID());
    		cstmt.registerOutParameter(5,Types.INTEGER);    		
    		cstmt.executeUpdate();
    		res = cstmt.getInt(5);   
    		con.commit();
    	}
    	catch(Exception e)
    	{    	try{	
    		con.rollback();
    	}
    	catch(Exception ex)
    	{
    		_log.errorTrace(METHOD_NAME,ex );
    	}
    		_log.errorTrace(METHOD_NAME,e);
    	}
    	finally{
    		OracleUtil.closeQuietly(cstmt);
    	}
    	if(res == 0){
    		try {
    			final StringBuffer strBuff = new StringBuffer();
    			strBuff.append(getDateTimeStringFromDate(p_c2sTransferVO.getTransferDateTime()));
    			strBuff.append("," + p_c2sTransferVO.getReceiverMsisdn());
    			strBuff.append("," + p_c2sTransferVO.getRequestedAmount());
    			strBuff.append("," + p_c2sTransferVO.getCellId());
    			strBuff.append("," + p_c2sTransferVO.getProductCode());
    			returnStr = strBuff.toString();

    		} catch (Exception e) {
    			_log.errorTrace(METHOD_NAME, e);
    			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroUtil[]", "", "", "",
    					"Not able to generate c2sTransferTDRLog:" + e.getMessage());
    			returnStr = null;
    		}
    	}
    	return returnStr;        
    }


    @Override
    public void validateMultiCurrencyRechargeRequest(Connection p_con, C2STransferVO p_c2sTransferVO, RequestVO p_requestVO) throws BTSLBaseException {
        final String obj = "validateMultiCurrencyRechargeRequest";
        try {
            final String[] p_requestArr = p_requestVO.getRequestMessageArray();
            String custMsisdn = null;
            // String [] strArr=null
            // double requestAmt=0
            String requestAmtStr = null;
            String currency = null;
	           
            final ChannelUserVO channelUserVO = (ChannelUserVO) p_c2sTransferVO.getSenderVO();
            UserPhoneVO userPhoneVO = null;
            if (!channelUserVO.isStaffUser()) {
                userPhoneVO = channelUserVO.getUserPhoneVO();
            } else {
                userPhoneVO = channelUserVO.getStaffUserDetails().getUserPhoneVO();
            }

            final int messageLen = p_requestArr.length;
            if (_log.isDebugEnabled()) {
                _log.debug(obj, "messageLen: " + messageLen);
            }
            for (int i = 0; i < messageLen; i++) {
                if (_log.isDebugEnabled()) {
                    _log.debug(obj, "i: " + i + " value: " + p_requestArr[i]);
                }
            }
            switch (messageLen) {
                case 5:
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
                        
                        // Recharge amount Validation
                        currency = p_requestArr[4];
                        CurrencyConversionVO  currencyVO = (CurrencyConversionVO) CurrencyConversionCache.getObject(currency, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_CURRENCY)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                        if(currencyVO!= null)
                        {                        
                          p_c2sTransferVO.setMultiCurrencyDetailVO(requestAmtStr+","+String.valueOf(currencyVO.getDisplayAmount())+","+currency);
                          requestAmtStr = String.valueOf(getConvertedAmount(requestAmtStr,currencyVO.getDisplayAmount()));
                          p_requestVO.setReqAmount(requestAmtStr);
                        }
                        else
                      	     throw new BTSLBaseException(this, obj, PretupsErrorCodesI.C2S_INVALID_CURRENCY_CODE);
                        
                        PretupsBL.validateAmount(p_c2sTransferVO, requestAmtStr);
                        p_c2sTransferVO.setReceiverVO(receiverVO);
                        // p_requestVO.setReqSelector(String.valueOf(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_TRANSFER_DEF_SELECTOR_CODE))))
                        // Changed on 27/05/07 for Service Type selector Mapping
                        final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO
                            .getServiceType());
                        if (serviceSelectorMappingVO != null) {
                            p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                        }
                        p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                        break;
                    }

                case 6:
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
                        currency = p_requestArr[5];
                        CurrencyConversionVO  currencyVO = (CurrencyConversionVO) CurrencyConversionCache.getObject(currency, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_CURRENCY)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                      if(currencyVO!= null)
                      {                        
                        p_c2sTransferVO.setMultiCurrencyDetailVO(requestAmtStr+","+String.valueOf(currencyVO.getDisplayAmount())+","+currency);
                        requestAmtStr = String.valueOf(getConvertedAmount(requestAmtStr,currencyVO.getDisplayAmount()));
                        p_requestVO.setReqAmount(requestAmtStr);
                      }
                      else
                    	     throw new BTSLBaseException(this, obj, PretupsErrorCodesI.C2S_INVALID_CURRENCY_CODE);
                      
                        PretupsBL.validateAmount(p_c2sTransferVO, requestAmtStr);
                        p_c2sTransferVO.setReceiverVO(receiverVO);

                        if (BTSLUtil.isNullString(p_requestArr[3])) {
                            p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                        } else {
                            final int langCode = PretupsBL.getLocaleValueFromCode(p_requestVO, p_requestArr[3]);
                            if (LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)) == null) {
                                throw new BTSLBaseException(this, obj, PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
                            }
                            p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
                        }
                        // p_requestVO.setReqSelector(String.valueOf(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_TRANSFER_DEF_SELECTOR_CODE))))
                        // Changed on 27/05/07 for Service Type selector Mapping
                        final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO
                            .getServiceType());
                        if (serviceSelectorMappingVO != null) {
                            p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
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
                        currency = p_requestArr[6];
                        CurrencyConversionVO  currencyVO = (CurrencyConversionVO) CurrencyConversionCache.getObject(currency, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_CURRENCY)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                        if(currencyVO!= null)
                        {                        
                          p_c2sTransferVO.setMultiCurrencyDetailVO(requestAmtStr+","+String.valueOf(currencyVO.getDisplayAmount())+","+currency);
                          requestAmtStr = String.valueOf(getConvertedAmount(requestAmtStr,currencyVO.getDisplayAmount()));
                          p_requestVO.setReqAmount(requestAmtStr);
                        }
                        else
                      	     throw new BTSLBaseException(this, obj, PretupsErrorCodesI.C2S_INVALID_CURRENCY_CODE);
                     
                        PretupsBL.validateAmount(p_c2sTransferVO, requestAmtStr);
                        p_c2sTransferVO.setReceiverVO(receiverVO);
                        if (BTSLUtil.isNullString(p_requestArr[3])) {
                            if ("en".equalsIgnoreCase(p_requestVO.getLocale().getLanguage())) {
                                // p_requestVO.setReqSelector(String.valueOf(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_TRANSFER_DEF_SELECTOR_CODE))))
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
                            // value after
                            // calling getselectorvaluefromcode method
                            /*
                             * else
                             * p_requestVO.setReqSelector((Constants.getProperty
                             * (
                             * "CVG_UNICODE_"
                             * +p_requestVO.getLocale().getLanguage().toUpperCase
                             * ())));
                             */} else {
                            p_requestVO.setReqSelector(p_requestArr[3]);
                        }

                        PretupsBL.getSelectorValueFromCode(p_requestVO);
                        // changed for CRE_INT_CR00029 by ankit Zindal
                        if (BTSLUtil.isNullString(p_requestVO.getReqSelector())) {
                            // p_requestVO.setReqSelector(String.valueOf(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_TRANSFER_DEF_SELECTOR_CODE))))
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
                                throw new BTSLBaseException(this, obj, PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
                            }
                            p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
                        }
                        break;
                    }
                case 8:
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
                        custMsisdn = p_requestArr[1];
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
                        currency = p_requestArr[7];
                        CurrencyConversionVO  currencyVO = (CurrencyConversionVO) CurrencyConversionCache.getObject(currency, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_CURRENCY)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                        if(currencyVO!= null)
                        {                        
                          p_c2sTransferVO.setMultiCurrencyDetailVO(requestAmtStr+","+String.valueOf(currencyVO.getDisplayAmount())+","+currency);
                          requestAmtStr = String.valueOf(getConvertedAmount(requestAmtStr,currencyVO.getDisplayAmount()));
                          p_requestVO.setReqAmount(requestAmtStr);
                        }
                        else
                      	     throw new BTSLBaseException(this, obj, PretupsErrorCodesI.C2S_INVALID_CURRENCY_CODE);
                    
                        PretupsBL.validateAmount(p_c2sTransferVO, requestAmtStr);
                        p_c2sTransferVO.setReceiverVO(receiverVO);
                        if (BTSLUtil.isNullString(p_requestArr[3])) {
                            if ("en".equalsIgnoreCase(p_requestVO.getLocale().getLanguage())) {
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
                            p_requestVO.setSenderLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                        } else {
                            final int langCode = PretupsBL.getLocaleValueFromCode(p_requestVO, p_requestArr[4]);
                            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
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
                                throw new BTSLBaseException(this, obj, PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
                            }
                            p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
                        }
                        break;
                    }
                default:
                    throw new BTSLBaseException(this, obj, PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() }, null);
            }

        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.errorTrace(obj, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateC2SRechargeRequest]", "", "", "",
                "Exception while validating user message" + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException(this, obj, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled()) {
            _log.debug(obj, "Exiting ");
        }
    }

    /**
     * getConvertedAmount
     * @param requestAmt
      * @param ConversionRate
     * @throws BTSLBaseException
     */
	 public double getConvertedAmount(String requestAmtStr, double ConversionRate) throws BTSLBaseException {


	        final String methodName = "getCovertedAmount";
	        if (_log.isDebugEnabled()) {
	        	_log.debug(methodName, "Entered requestAmtStr:" + requestAmtStr+" ConversionRate:"+ConversionRate);
	        }	        
	       double finalCalcAmnt;	        
	      finalCalcAmnt = (Double.parseDouble(requestAmtStr) * ConversionRate);
	       if(_log.isDebugEnabled())_log.debug("getDisplayAmount","Exiting display amount:"+finalCalcAmnt);    
         	  return finalCalcAmnt;
	        
	 }

	 
	 /**
	     * Method to Generate OTP
	     * 
	     * @return String
	     */
	    // added for OTP generetion
	    @Override
	    synchronized public String generateOTP() throws BTSLBaseException {
	        final String METHOD_NAME = "generateOTP";
	        String chars = null;
	        try {
	            chars = Constants.getProperty("OTP_PIN_GEN_ARG");
	        } catch (Exception e) {
	            _log.errorTrace(METHOD_NAME, e);
	            chars = "1234567890";
	        }

	        int passLength = 0;
	        try {
	            passLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OTP_ALLOWED_LENGTH))).intValue();
	        } catch (Exception e) {
	            _log.errorTrace(METHOD_NAME, e);
	            passLength = 6;
	        }
	        StringBuffer temp = null;
	        try {
	            if (_log.isDebugEnabled()) {
	                _log.debug("generateOTP Entered with Chars" + chars + "  And OTP Length", passLength);
	            }
	            if (passLength > chars.length()) {
	                throw new Exception("Random number minimum length should be less than the provided chars list length");
	            }
	            final Random m_generator = new Random(System.nanoTime());
	            final char[] availableChars = chars.toCharArray();
	            int availableCharsLeft = availableChars.length;
	            temp = new StringBuffer(passLength);
	            int pos = 0;
	            for (int i = 0; i < passLength;) {
	                pos = BTSLUtil.parseDoubleToInt((availableCharsLeft * m_generator.nextDouble()));
	                if (i == 0) {
	                    if (!"1".equalsIgnoreCase(String.valueOf(availableChars[pos])) && !"0".equalsIgnoreCase(String.valueOf(availableChars[pos]))) {
	                        i++;
	                        temp.append(availableChars[pos]);
	                        availableChars[pos] = availableChars[availableCharsLeft - 1];
	                        --availableCharsLeft;
	                    }
	                } else {
	                    temp.append(availableChars[pos]);
	                    i++;
	                    availableChars[pos] = availableChars[availableCharsLeft - 1];
	                    --availableCharsLeft;
	                }
	            }
	        } catch (Exception e) {
	            _log.errorTrace(METHOD_NAME, e);
	            temp = null;
	            throw new BTSLBaseException("Exception In generating OTP");
	        }
	        if (_log.isDebugEnabled()) {
	            _log.debug("generateOTP Exiting", "");
	        }
	        return String.valueOf(temp);
	    }
	 
	 
    
}
