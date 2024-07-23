/**
 * @(#)MobilecomUtil.java
 * Copyright(c) 2005, Bharti Telesoft Ltd.
 * All Rights Reserved
 *
 * <description>
 *-------------------------------------------------------------------------------------------------
 * Author                        Date            History
 *-------------------------------------------------------------------------------------------------
 * avinash.kamthan               	Aug 5, 2005        Initital Creation
 *-------------------------------------------------------------------------------------------------
 *
 */

package com.btsl.pretups.util.clientutils;
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
import com.btsl.pretups.privaterecharge.businesslogic.PrivateRchrgVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class MobilecomUtil extends OperatorUtil
{
	/**
	 * Field _log.
	 */
	private Log _log = LogFactory.getLog(this.getClass().getName());

	/**
	 * This method will convert operator specific msisdn to system specific msisdn.
	 * @param p_msisdn
	 * @return
	 * @throws BTSLBaseException
	 */
	public String getSystemFilteredMSISDN(String p_msisdn) throws BTSLBaseException
	{
		String msisdn=super.getSystemFilteredMSISDN(p_msisdn);
		if(msisdn.length()<10)
			msisdn="0"+msisdn;
		return msisdn;
	}
	
	/**
	 * This method will convert system specific msisdn to operater specific msisdn
	 * 
	 * @param p_msisdn
	 * @return
	 */
	public String getOperatorFilteredMSISDN(String p_msisdn)
	{
		if(p_msisdn.length()==10&& p_msisdn.startsWith("0"))
			p_msisdn=p_msisdn.substring(1);
		return Constants.getProperty("COUNTRY_CODE")+p_msisdn;
	}

    public HashMap validatePassword(String p_loginID, String p_password)
    {
        _log.debug("validatePassword","Entered, p_userID= ",new String(p_loginID+", Password= "+p_password));
        HashMap messageMap=new HashMap();
        String defaultPin = BTSLUtil.getDefaultPasswordNumeric(p_password);
        if(defaultPin.equals(p_password))
           return messageMap;
        defaultPin = BTSLUtil.getDefaultPasswordText(p_password);
        if(defaultPin.equals(p_password))
           return messageMap;
        if (p_password.length() < ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_LOGIN_PWD_LENGTH))).intValue() || p_password.length() > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_LOGIN_PWD_LENGTH))).intValue())
        {
            String[] args={String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_LOGIN_PWD_LENGTH))).intValue()),String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_LOGIN_PWD_LENGTH))).intValue())};
            messageMap.put("operatorutil.validatepassword.error.passwordlenerr", args);
        }
        int result=BTSLUtil.isSMSPinValid(p_password);//for consecutive and same characters
        if(result==-1)
            messageMap.put("operatorutil.validatepassword.error.passwordsamedigit",null);
        else if(result==1)
            messageMap.put("operatorutil.validatepassword.error.passwordconsecutive",null);
        if(!BTSLUtil.containsChar(p_password))
                messageMap.put("operatorutil.validatepassword.error.passwordnotcontainschar",null);
        // for special character
        String specialChar=Constants.getProperty("SPECIAL_CHARACTER_PASSWORD_VALIDATION");
        if(!BTSLUtil.isNullString(specialChar))
        {
            String[] specialCharArray={specialChar};
            String[] passwordCharArray=specialChar.split(",");
            boolean specialCharFlag=false;
            for(int i=0,j=passwordCharArray.length;i<j;i++)
            {
                if(p_password.contains(passwordCharArray[i]))
                {
                    specialCharFlag=true;
                    break;
                }
            }
            if(!specialCharFlag)
                messageMap.put("operatorutil.validatepassword.error.passwordspecialchar",specialCharArray);
        }
        // for number
        String[]passwordNumberStrArray={"0","1","2","3","4","5","6","7","8","9"};
        boolean numberStrFlag=false;
        for(int i=0,j=passwordNumberStrArray.length;i<j;i++)
        {
            if(p_password.contains(passwordNumberStrArray[i]))
            {
                numberStrFlag=true;
                break;
            }
        }
        if(!numberStrFlag)
            messageMap.put("operatorutil.validatepassword.error.passwordnumberchar",null);
        if(p_loginID.equals(p_password))
            messageMap.put("operatorutil.validatepassword.error.sameusernamepassword",null);
        if(_log.isDebugEnabled()) _log.debug("validatePassword","Exiting ");
        return messageMap;
    }

     /**
     * Date : Jul 23, 2007
     * Discription :
     * Method : validateTransactionPassword
     * @param p_channelUserVO
     * @param p_password
     * @throws BTSLBaseException
     * @return void
     * @author ved.sharma
     */
    public boolean validateTransactionPassword(ChannelUserVO p_channelUserVO, String p_password)throws BTSLBaseException
    {
		if (_log.isDebugEnabled()) _log.debug("validateTransactionPassword", " Entered p_channelUserVO=:" + p_channelUserVO + " p_password=" +p_password+"| p_channelUserVO.getPassword()||"+p_channelUserVO.getPassword());
        boolean passwordValidation=true;
        try
        {
            if(p_channelUserVO!=null)
            {
                if(!BTSLUtil.isNullString(p_channelUserVO.getPassword()) && !BTSLUtil.decryptText(p_channelUserVO.getPassword()).equals(p_password))
                   passwordValidation=false;
                if(!BTSLUtil.isNullString(p_password) && ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_PASSWORD)).equals(p_password))
                    throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.XML_ERROR_CHANGE_DEFAULT_PASSWD);
            }
            else
                throw new BTSLBaseException("OciUtil", "validateTransactionPassword", PretupsErrorCodesI.XML_ERROR_NO_SUCH_USER);

        }
        catch (BTSLBaseException bex)
        {
            throw bex;
        }
        catch (Exception e)
        {
            _log.error("validateTransactionPassword", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OciUtil[validateTransactionPassword]","","","","Exception:"+e.getMessage());
            throw new BTSLBaseException("OciUtil", "validateTransactionPassword", PretupsErrorCodesI.ERROR_EXCEPTION);
        }
        finally
        {
             if (_log.isDebugEnabled()) _log.debug("validateTransactionPassword", " Exiting passwordValidation="+passwordValidation);
        }
        return passwordValidation;
    }
    
    
    /**
	 * Method that will validate the user message sent
	 * @param p_con
	 * @param p_c2sTransferVO
	 * @param p_requestVO
	 * @throws BTSLBaseException
	 * @see com.btsl.pretups.util.OperatorUtilI#validateC2SRechargeRequest(Connection, C2STransferVO, RequestVO)
	 */
	public void validateC2SRechargeRequest(Connection p_con,C2STransferVO p_c2sTransferVO,RequestVO p_requestVO) throws BTSLBaseException
	{
		try
		{
			String[] p_requestArr=p_requestVO.getRequestMessageArray();
			String custMsisdn=null;
			//String [] strArr=null;
			//double requestAmt=0;
			String requestAmtStr=null;
			ChannelUserVO channelUserVO=(ChannelUserVO)p_c2sTransferVO.getSenderVO();
			UserPhoneVO userPhoneVO=null;
			if(!channelUserVO.isStaffUser())
			    userPhoneVO=(UserPhoneVO)channelUserVO.getUserPhoneVO();
			else
			    userPhoneVO=(UserPhoneVO)channelUserVO.getStaffUserDetails().getUserPhoneVO();
			
			int messageLen=p_requestArr.length;
			if(_log.isDebugEnabled()) _log.debug("validateC2SRechargeRequest","messageLen: "+messageLen);
			for(int i=0;i<messageLen;i++)
			{
				if(_log.isDebugEnabled()) _log.debug("validateC2SRechargeRequest","i: "+i+" value: "+p_requestArr[i]);
			}
			switch(messageLen)
			{
				case 4:
				{
					//Do the 000 check Default PIN 
					//if((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES) && !PretupsI.DEFAULT_C2S_PIN.equals(BTSLUtil.decryptText((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getSmsPin())))
					if(userPhoneVO.getPinRequired().equals(PretupsI.YES))
					{
						try
						{
							ChannelUserBL.validatePIN(p_con,channelUserVO,p_requestArr[3]);
						}
						catch(BTSLBaseException be)
						{
							if(be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) ||  (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK))))
								p_con.commit();
							throw be;
						}	
					}					
					ReceiverVO receiverVO=new ReceiverVO();
					//Customer MSISDN Validation
					custMsisdn=p_requestArr[1];
					//Change for the SID logic
					p_requestVO.setSid(custMsisdn);
					receiverVO.setSid(custMsisdn);
					PrivateRchrgVO prvo=null;
					if((prvo=getPrivateRechargeDetails(p_con,custMsisdn))!=null)
					{
						p_c2sTransferVO.setSubscriberSID(custMsisdn);
						custMsisdn=prvo.getMsisdn();						
					}
					PretupsBL.validateMsisdn(p_con,receiverVO,p_c2sTransferVO.getRequestID(),custMsisdn);
					
					//Recharge amount Validation
					requestAmtStr=p_requestArr[2];
					requestAmtStr=addDecimal(requestAmtStr);// Decimal Value Changes
					PretupsBL.validateAmount(p_c2sTransferVO,requestAmtStr);
					p_c2sTransferVO.setReceiverVO(receiverVO);		
					//p_requestVO.setReqSelector(String.valueOf(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_TRANSFER_DEF_SELECTOR_CODE))));
					//Changed on 27/05/07 for Service Type selector Mapping
					ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
					if(serviceSelectorMappingVO!=null)
						p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());									
					p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
					break;
				}
				
				case 5:
				{
//					Do the 000 check Default PIN 
					//if((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES) && !PretupsI.DEFAULT_C2S_PIN.equals(BTSLUtil.decryptText((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getSmsPin())))
				    if(userPhoneVO.getPinRequired().equals(PretupsI.YES))
					{
						try
						{
							ChannelUserBL.validatePIN(p_con,channelUserVO,p_requestArr[4]);
						}
						catch(BTSLBaseException be)
						{
							if(be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) ||  (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK))))
								p_con.commit();
							throw be;
						}	
					}						
					ReceiverVO receiverVO=new ReceiverVO();
					//Customer MSISDN Validation
					custMsisdn=p_requestArr[1];
					//Change for the SID logic
					p_requestVO.setSid(custMsisdn);
					receiverVO.setSid(custMsisdn);
					PrivateRchrgVO prvo=null;
					if((prvo=getPrivateRechargeDetails(p_con,custMsisdn))!=null)
					{
						p_c2sTransferVO.setSubscriberSID(custMsisdn);
						custMsisdn=prvo.getMsisdn();						
					}
					PretupsBL.validateMsisdn(p_con,receiverVO,p_c2sTransferVO.getRequestID(),custMsisdn);
					
					//Recharge amount Validation
					requestAmtStr=p_requestArr[2];
					requestAmtStr=addDecimal(requestAmtStr);// Decimal Value Changes
					PretupsBL.validateAmount(p_c2sTransferVO,requestAmtStr);
					p_c2sTransferVO.setReceiverVO(receiverVO);
					if(BTSLUtil.isNullString(p_requestArr[3]))
						p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
					else
					{
						int langCode=PretupsBL.getLocaleValueFromCode(p_requestVO,p_requestArr[3]);
						if(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode))==null)
							throw new BTSLBaseException(this,"validateC2SRechargeRequest",PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
						p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
					}
					//p_requestVO.setReqSelector(String.valueOf(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_TRANSFER_DEF_SELECTOR_CODE))));
					//Changed on 27/05/07 for Service Type selector Mapping
					ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
					if(serviceSelectorMappingVO!=null)
						p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());									
					break;
				}
				
				case 6:
				{
					//if((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES) && !PretupsI.DEFAULT_C2S_PIN.equals(BTSLUtil.decryptText((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getSmsPin())))
				    if(userPhoneVO.getPinRequired().equals(PretupsI.YES))
					{
						try
						{
							ChannelUserBL.validatePIN(p_con,channelUserVO,p_requestArr[5]);
						}
						catch(BTSLBaseException be)
						{
							if(be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) ||  (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK))))
								p_con.commit();
							throw be;
						}								
					}
					
					ReceiverVO receiverVO=new ReceiverVO();
					//Customer MSISDN Validation
					custMsisdn=p_requestArr[1];
					//Change for the SID logic
					p_requestVO.setSid(custMsisdn);
					receiverVO.setSid(custMsisdn);
					PrivateRchrgVO prvo=null;
					if((prvo=getPrivateRechargeDetails(p_con,custMsisdn))!=null)
					{
						p_c2sTransferVO.setSubscriberSID(custMsisdn);
						custMsisdn=prvo.getMsisdn();						
					}
					PretupsBL.validateMsisdn(p_con,receiverVO,p_c2sTransferVO.getRequestID(),custMsisdn);
					
					//Recharge amount Validation
					requestAmtStr=p_requestArr[2];
					requestAmtStr=addDecimal(requestAmtStr);// Decimal Value Changes
					PretupsBL.validateAmount(p_c2sTransferVO,requestAmtStr);
					p_c2sTransferVO.setReceiverVO(receiverVO);
					if(BTSLUtil.isNullString(p_requestArr[3]))
					{
						if("en".equalsIgnoreCase(p_requestVO.getLocale().getLanguage()))
						{
							//p_requestVO.setReqSelector(String.valueOf(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_TRANSFER_DEF_SELECTOR_CODE))));
							//Changed on 27/05/07 for Service Type selector Mapping
							ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
							if(serviceSelectorMappingVO!=null)
								p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());									
						}
						//changed for CRE_INT_CR00029 by ankit Zindal
						//in case of binary message we will set default value after calling getselectorvaluefromcode method
						/*						else
							p_requestVO.setReqSelector((Constants.getProperty("CVG_UNICODE_"+p_requestVO.getLocale().getLanguage().toUpperCase())));
*/					}
					else
						p_requestVO.setReqSelector(p_requestArr[3]);
					
					PretupsBL.getSelectorValueFromCode(p_requestVO);
//					changed for CRE_INT_CR00029 by ankit Zindal
					if(BTSLUtil.isNullString(p_requestVO.getReqSelector()))
					{
						//p_requestVO.setReqSelector(String.valueOf(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_TRANSFER_DEF_SELECTOR_CODE))));
						//Changed on 27/05/07 for Service Type selector Mapping
						ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
						if(serviceSelectorMappingVO!=null)
							p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());									
					}
					if(BTSLUtil.isNullString(p_requestArr[4]))
						p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
					else
					{
						int langCode=PretupsBL.getLocaleValueFromCode(p_requestVO,p_requestArr[4]);
						if(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode))==null)
							throw new BTSLBaseException(this,"validateC2SRechargeRequest",PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
						p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
					}
					break;
				}
				case 7:
				{
					//if((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES) && !PretupsI.DEFAULT_C2S_PIN.equals(BTSLUtil.decryptText((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getSmsPin())))
				    if(userPhoneVO.getPinRequired().equals(PretupsI.YES))
					{
						try
						{
							ChannelUserBL.validatePIN(p_con,channelUserVO,p_requestArr[6]);
						}
						catch(BTSLBaseException be)
						{
							if(be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) ||  (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK))))
								p_con.commit();
							throw be;
						}								
					}
					
					ReceiverVO receiverVO=new ReceiverVO();
					//Customer MSISDN Validation
					custMsisdn=p_requestArr[1];
					//Change for the SID logic
					p_requestVO.setSid(custMsisdn);
					receiverVO.setSid(custMsisdn);
					PrivateRchrgVO prvo=null;
					if((prvo=getPrivateRechargeDetails(p_con,custMsisdn))!=null)
					{
						p_c2sTransferVO.setSubscriberSID(custMsisdn);
						custMsisdn=prvo.getMsisdn();						
					}
					PretupsBL.validateMsisdn(p_con,receiverVO,p_c2sTransferVO.getRequestID(),custMsisdn);
					
					//Recharge amount Validation
					requestAmtStr=p_requestArr[2];
					requestAmtStr=addDecimal(requestAmtStr);// Decimal Value Changes
					PretupsBL.validateAmount(p_c2sTransferVO,requestAmtStr);
					p_c2sTransferVO.setReceiverVO(receiverVO);
					if(BTSLUtil.isNullString(p_requestArr[3]))
					{
						if("en".equalsIgnoreCase(p_requestVO.getLocale().getLanguage()))
						{
							//p_requestVO.setReqSelector(String.valueOf(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_TRANSFER_DEF_SELECTOR_CODE))));
							//Changed on 27/05/07 for Service Type selector Mapping
							ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
							if(serviceSelectorMappingVO!=null)
								p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());									
						}
/*						else
							p_requestVO.setReqSelector((Constants.getProperty("CVG_UNICODE_"+p_requestVO.getLocale().getLanguage().toUpperCase())));
*/					}
					else
						p_requestVO.setReqSelector(p_requestArr[3]);
					
					PretupsBL.getSelectorValueFromCode(p_requestVO);
					//changed for CRE_INT_CR00029 by ankit Zindal
					if(BTSLUtil.isNullString(p_requestVO.getReqSelector()))
					{
						//p_requestVO.setReqSelector(String.valueOf(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_TRANSFER_DEF_SELECTOR_CODE))));
						//Changed on 27/05/07 for Service Type selector Mapping
						ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
						if(serviceSelectorMappingVO!=null)
							p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());									
					}
					//For handling of sender locale
					if(BTSLUtil.isNullString(p_requestArr[4]))
						p_requestVO.setSenderLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
					else
					{
						int langCode=PretupsBL.getLocaleValueFromCode(p_requestVO,p_requestArr[4]);
/*						if(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode))==null)
							throw new BTSLBaseException(this,"validateC2SRechargeRequest",PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
*/						p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
						//ChangeID=LOCALEMASTER
						//Sender locale has to be overwritten in transferVO also.
						p_c2sTransferVO.setLocale(p_requestVO.getSenderLocale());
						p_c2sTransferVO.setLanguage(p_c2sTransferVO.getLocale().getLanguage());
						p_c2sTransferVO.setCountry(p_c2sTransferVO.getLocale().getCountry());
					}
					if (_log.isDebugEnabled()) 
				        _log.debug(this,"sender locale: ="+p_requestVO.getSenderLocale());
					
					if(BTSLUtil.isNullString(p_requestArr[5]))
						p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
					else
					{
						int langCode=PretupsBL.getLocaleValueFromCode(p_requestVO,p_requestArr[5]);
						if(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode))==null)
							throw new BTSLBaseException(this,"validateC2SRechargeRequest",PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
						p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
					}
					break;
				}
				default:
					throw new BTSLBaseException(this,"validateC2SRechargeRequest",PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT,0,new String[]{p_requestVO.getActualMessageFormat()},null);
			}
			
			/*
			if(p_requestArr.length <5)
				throw new BTSLBaseException(this,"validateC2SRechargeRequest",PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT);

			//Do the 000 check Default PIN 
			if((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES) && !PretupsI.DEFAULT_C2S_PIN.equals(BTSLUtil.decryptText((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getSmsPin())))
				ChannelUserBL.validatePIN(p_con,channelUserVO.getUserPhoneVO(),p_requestArr[4]);
			
			ReceiverVO receiverVO=new ReceiverVO();
			//Customer MSISDN Validation
			custMsisdn=p_requestArr[1];
			
			validateMsisdn(receiverVO,p_c2sTransferVO.getRequestID(),custMsisdn);
			
			//Recharge amount Validation
			requestAmtStr=p_requestArr[2];
			validateAmount(p_c2sTransferVO,requestAmtStr);
			p_c2sTransferVO.setReceiverVO(receiverVO);
			*/
		}
		catch(BTSLBaseException be)
		{
			throw be;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			_log.error("validateC2SRechargeRequest","  Exception while validating user message :"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PretupsBL[validateC2SRechargeRequest]","","","","Exception while validating user message" +" ,getting Exception="+e.getMessage());
			throw new BTSLBaseException(this,"validateC2SRechargeRequest",PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if(_log.isDebugEnabled()) _log.debug("validateC2SRechargeRequest","Exiting ");
	}
	
	/**
	 * Method to Check Convert the requested amount in the decimal Value
	 * @param amount
	 * @return
	 * @throws BTSLBaseException
	 * @throws Exception
	 */
	/*private  String addDecimal(String amount)throws BTSLBaseException,Exception
    {
		if(_log.isDebugEnabled()) 
			_log.debug("addDecimal","Entered  amount "+amount);
		String doubleZero="00";
		String subString = amount.substring(0, 2);
		if(subString.equalsIgnoreCase(doubleZero)) // case like 0078=00.78
			amount=doubleZero+"."+amount.substring(2, amount.length());
		else if (amount.substring(0,1).equalsIgnoreCase("0")) // case like 098=invalid request amount
			throw new BTSLBaseException(this,"addDecimal",PretupsErrorCodesI.ERROR_INVALID_AMOUNT);
		//else return the same amount as it is.
        return amount;
    }*/

	/**
	 * Method to Check Convert the requested amount in the decimal Value
	 * @param amount
	 * @return
	 * @throws BTSLBaseException
	 * @throws Exception
	 */
	public String addDecimal(String p_amount)throws BTSLBaseException,Exception
    {
		if(_log.isDebugEnabled()) 
			_log.debug("addDecimal Entered ","p_amount: "+p_amount);
		String amount=p_amount;
		Character firstIndex=amount.charAt(0);
		String firstCharacter=firstIndex.toString();
		int l=amount.length();
		if(l<=3)
		{
    		if(firstCharacter.equals("0"))
    		{
    		    if(l>1)
    		    {
    		        amount="0."+amount.substring(1, l);
    		    }
    		}
		}
		if(_log.isDebugEnabled()) 
			_log.debug("addDecimal Exited ","p_amount: "+p_amount);
		return amount;
    }
	/*public String DES3Encryption(String p_message, RequestVO p_requestvo)  throws BTSLBaseException,Exception
    {
		if(_log.isDebugEnabled())
            _log.debug("DES3Encryption","Entered p_message="+p_message);
			  final String METHOD_NAME="DES3Encryption";
		      byte[] byteMi = null;    
		      byte[] byteMing = null;    
		      String strMi ="";    
		      BASE64Encoder base64en = new BASE64Encoder();  
		      p_requestvo.setPrivateRechBinMsgAllowed(true);
		      ChannelUserVO channelUserVO=(ChannelUserVO)p_requestvo.getSenderVO();
		      String encrytKey=channelUserVO.getUserPhoneVO().getEncryptDecryptKey();
		      if(encrytKey==null || encrytKey.length()==0)
              {
                  Connection con=null;
                  try
                  {
                      con=OracleUtil.getConnection();
                      //Load the pos key of the msisdn 
                      PosKeyVO posKeyVO=new PosKeyDAO().loadPosKeyByMsisdn(con,p_requestvo.getFilteredMSISDN());
                      if(posKeyVO==null)
                      {
                          _log.error("getEncryptionKeyForUser",p_requestvo.getRequestIDStr()," MSISDN="+p_requestvo.getFilteredMSISDN()+" User Encryption Not found in Database"); 
                          EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,"PretupsBL[isPlainMessageAndAllowed]",p_requestvo.getRequestIDStr(),p_requestvo.getFilteredMSISDN(),"","User Encryption Not found in Database for MSISDN="+p_requestvo.getFilteredMSISDN());
                          throw new BTSLBaseException("PretupsBL","getEncryptionKeyForUser",PretupsErrorCodesI.CHNL_ERROR_SNDR_ENCR_KEY_NOTFOUND);
                      }
                      if(posKeyVO.isRegistered())
                          encrytKey=posKeyVO.getKey();
                      if(encrytKey==null || encrytKey.length()==0)
                          throw new BTSLBaseException("Encryption key not defined for MSISND="+p_requestvo.getFilteredMSISDN());
                  }
                  catch(BTSLBaseException bse)
                  {
                      _log.error("DES3Encryption","Encryption key not defined for MSISND="+p_requestvo.getFilteredMSISDN());
                      throw bse;
                  }
                  catch(Exception e)
                  {
                      _log.error("DES3Encryption","Encryption key not defined for MSISND="+p_requestvo.getFilteredMSISDN());
                      throw e;
                  }
                  finally
                  {
                      try {if (con != null) {con.close();}} catch (Exception e) {
                    	  _log.errorTrace(METHOD_NAME, e);
                      } 
                  }
              }

		      //String encrytKey="2F091607C0268AC9400A444ADFAC6E48";
		      try {   
		    	  	  String sArithmeticname="";
			    	  if (encrytKey.length() ==16)
			          {
			          	sArithmeticname = "DES";
			          	
			          } else{
			          	sArithmeticname = "DESede";
			          	
			          	int m = 0;
			          	while (encrytKey.length()< 48){
			          		encrytKey = encrytKey + encrytKey.substring(m,m+2);
			          		m = m + 2;
			          	}
			          }
			    	  
			    	  byteMing = p_message.getBytes("UTF8");
		        
		        
		        byteMi = this.getEncCode(byteMing,sArithmeticname,encrytKey);
		        
		        strMi = base64en.encode(byteMi);    
		      } catch (Exception e) {    
		        throw new BTSLBaseException(    
		           "DES3Encryption Error initializing SqlMap class. Cause:" + e);    
		      } finally {    
		        base64en = null;    
		        byteMing = null;    
		        byteMi = null;    
		      }  
		      if(_log.isDebugEnabled())
		            _log.debug("DES3Encryption","Exiting strMi="+strMi);
		      return strMi;    
    }*/
	
	 /**   
	   * Get Encryption byte[]
	   * @param byteS   
	   * @return   
	   */   
	  /*private byte[] getEncCode(byte[] byteS,String p_sArithmeticname,String p_encrytKey) {    
	      byte[] byteFina = null;    
	      Cipher cipher;    
	      try {    
	        cipher = Cipher.getInstance(p_sArithmeticname);
	        
	        SecretKey DesKey = new SecretKeySpec(hexStr2Bytes(p_encrytKey),p_sArithmeticname);
	        
	        cipher.init(Cipher.ENCRYPT_MODE, DesKey);    
	        byteFina = cipher.doFinal(byteS);    
	      } catch (Exception e) {    
	        throw new RuntimeException(    
	          "getEncCode Error initializing SqlMap class. Cause:" + e);    
	      } finally {    
	        cipher = null;    
	      }    
	      return byteFina;    
	  } */
	  
	 /* private static byte[] hexStr2Bytes(String src) {

	      if (src == null || src.length() == 0) {
	          return new byte[0];
	      }

	      int m = 0;
	      int n = 0;
	      int l = src.length() / 2;
	      
	      byte[] ret = new byte[l];
	      for (int i = 0; i < l; i++) {
	          m = i * 2 + 1;
	          n = m + 1;
	          ret[i] = uniteBytes(src.substring(i * 2, m), src.substring(m, n));
	      }
	      return ret;
	  }
	 
	  private static byte uniteBytes(String src0, String src1) {
	      byte b0 = Byte.decode("0x" + src0).byteValue();
	      b0 = (byte) (b0 << 4);
	      byte b1 = Byte.decode("0x" + src1).byteValue();
	      byte ret = (byte) (b0 | b1);
	      return ret;
	  }*/
	  
	  public String formatC2STransferID(TransferVO p_transferVO,long p_tempTransferID)
		{
			final String methodName = "formatC2STransferID";
			String returnStr=null;
			 //Added for Multi Product Recharge
		    String SERVICE_TYPE_CHNL_RECHARGE_VOICE="101RC";
		    String SERVICE_TYPE_CHNL_RECHARGE_DATA="102RC";
		    String SERVICE_TYPE_CHNL_RECHARGE_WEINAK="104RC";
		    String SERVICE_TYPE_CHNL_RECHARGE_VAS="105RC";
		    String SERVICE_TYPE_CHNL_RECHARGE_MIX="106RC";
			try
			{
				String paddedTransferIDStr=BTSLUtil.padZeroesToLeft(Long.toHexString(p_tempTransferID),C2S_TRANSFER_ID_PAD_LENGTH); 
				if(SERVICE_TYPE_CHNL_RECHARGE_VOICE.equalsIgnoreCase(p_transferVO.getServiceType())){
					returnStr="O"+currentDateTimeFormatString(p_transferVO.getCreatedOn())+"."+currentTimeFormatString(p_transferVO.getCreatedOn())+"."+Constants.getProperty("INSTANCE_ID")+paddedTransferIDStr;
				} else if(SERVICE_TYPE_CHNL_RECHARGE_DATA.equalsIgnoreCase(p_transferVO.getServiceType())){
					returnStr="D"+currentDateTimeFormatString(p_transferVO.getCreatedOn())+"."+currentTimeFormatString(p_transferVO.getCreatedOn())+"."+Constants.getProperty("INSTANCE_ID")+paddedTransferIDStr;
				} else if(SERVICE_TYPE_CHNL_RECHARGE_WEINAK.equalsIgnoreCase(p_transferVO.getServiceType())){
					returnStr="W"+currentDateTimeFormatString(p_transferVO.getCreatedOn())+"."+currentTimeFormatString(p_transferVO.getCreatedOn())+"."+Constants.getProperty("INSTANCE_ID")+paddedTransferIDStr;
				} else if(SERVICE_TYPE_CHNL_RECHARGE_VAS.equalsIgnoreCase(p_transferVO.getServiceType())){
					returnStr="V"+currentDateTimeFormatString(p_transferVO.getCreatedOn())+"."+currentTimeFormatString(p_transferVO.getCreatedOn())+"."+Constants.getProperty("INSTANCE_ID")+paddedTransferIDStr;
				} else if(SERVICE_TYPE_CHNL_RECHARGE_MIX.equalsIgnoreCase(p_transferVO.getServiceType())){
					returnStr="MI"+currentDateTimeFormatString(p_transferVO.getCreatedOn())+"."+currentTimeFormatString(p_transferVO.getCreatedOn())+"."+Constants.getProperty("INSTANCE_ID")+paddedTransferIDStr;
				} else {
					returnStr="R"+currentDateTimeFormatString(p_transferVO.getCreatedOn())+"."+currentTimeFormatString(p_transferVO.getCreatedOn())+"."+Constants.getProperty("INSTANCE_ID")+paddedTransferIDStr;
				}
				p_transferVO.setTransferID(returnStr);
			}
			catch(Exception e)
			{
				_log.errorTrace(methodName, e);
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorUtil[]","","","","Not able to generate Transfer ID:"+e.getMessage());
				returnStr=null;
			}
			return returnStr;
		}
	  
	  	/**
		 * Method that will validate the user message sent
		 * @param p_con
		 * @param p_c2sTransferVO
		 * @param p_requestVO
		 * @throws BTSLBaseException
		 * @see com.btsl.pretups.util.OperatorUtilI#validateEVDRequestFormat(Connection, C2STransferVO, RequestVO)
		 */
		public void validateEVDRequestFormat(Connection p_con,C2STransferVO p_c2sTransferVO,RequestVO p_requestVO) throws BTSLBaseException
		{
			final String obj = "validateEVDRequestFormat";
			final String methodName = obj;
			try
			{
				String[] p_requestArr=p_requestVO.getRequestMessageArray();
				String custMsisdn=null;
				String requestAmtStr=null;
				ChannelUserVO channelUserVO=(ChannelUserVO)p_c2sTransferVO.getSenderVO();
				UserPhoneVO userPhoneVO=null;
				if(!channelUserVO.isStaffUser()) {
					userPhoneVO=(UserPhoneVO)channelUserVO.getUserPhoneVO();
				} else {
					userPhoneVO=(UserPhoneVO)channelUserVO.getStaffUserDetails().getUserPhoneVO();
				}
				int messageLen=p_requestArr.length;
				if(_log.isDebugEnabled()) {
					_log.debug(methodName,"messageLen: "+messageLen);
				}
				for(int i=0;i<messageLen;i++)
				{
					if(_log.isDebugEnabled()) {
						_log.debug(methodName,"i: "+i+" value: "+p_requestArr[i]);
					}
				}
				switch(messageLen)
				{
				case 4:
				{
					//Do the 000 check Default PIN 
					if(userPhoneVO.getPinRequired().equals(PretupsI.YES))
					{
						try
						{
							ChannelUserBL.validatePIN(p_con,channelUserVO,p_requestArr[3]);
						}
						catch(BTSLBaseException be)
						{
							if(be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) ||  (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
								p_con.commit();
							}
							throw be;
						}	
					}					
					ReceiverVO receiverVO=new ReceiverVO();
					if(PretupsI.GATEWAY_TYPE_EXTGW.equalsIgnoreCase(p_requestVO.getRequestGatewayType()) && ( BTSLUtil.isNullString(p_requestVO.getSenderExternalCode())) && BTSLUtil.isNullString(p_requestVO.getSenderLoginID())){
						//Customer MSISDN Validation
						custMsisdn=p_requestArr[1];
	//					Change for the SID logic
						p_requestVO.setSid(custMsisdn);
						receiverVO.setSid(custMsisdn);
						PrivateRchrgVO prvo=null;
						if((prvo=getPrivateRechargeDetails(p_con,custMsisdn))!=null)
						{
							p_c2sTransferVO.setSubscriberSID(custMsisdn);
							custMsisdn=prvo.getMsisdn();						
						}	
						PretupsBL.validateMsisdn(p_con,receiverVO,p_c2sTransferVO.getRequestID(),custMsisdn);
						//Recharge amount Validation
						requestAmtStr=p_requestArr[2];
						PretupsBL.validateAmount(p_c2sTransferVO,requestAmtStr);
						p_c2sTransferVO.setReceiverVO(receiverVO);		
						
					} else if(PretupsI.GATEWAY_TYPE_EXTGW.equalsIgnoreCase(p_requestVO.getRequestGatewayType())){
						custMsisdn=userPhoneVO.getMsisdn();
						PretupsBL.validateMsisdn(p_con,receiverVO,p_c2sTransferVO.getRequestID(),custMsisdn);
						//Recharge amount Validation
						requestAmtStr=p_requestArr[1];
						PretupsBL.validateAmount(p_c2sTransferVO,requestAmtStr);
						p_c2sTransferVO.setReceiverVO(receiverVO);		
						
					} else {
						//Customer MSISDN Validation
						custMsisdn=p_requestArr[1];
	//					Change for the SID logic
						p_requestVO.setSid(custMsisdn);
						receiverVO.setSid(custMsisdn);
						PrivateRchrgVO prvo=null;
						if((prvo=getPrivateRechargeDetails(p_con,custMsisdn))!=null)
						{
							p_c2sTransferVO.setSubscriberSID(custMsisdn);
							custMsisdn=prvo.getMsisdn();						
						}	
						PretupsBL.validateMsisdn(p_con,receiverVO,p_c2sTransferVO.getRequestID(),custMsisdn);
						//Recharge amount Validation
						requestAmtStr=p_requestArr[2];
						PretupsBL.validateAmount(p_c2sTransferVO,requestAmtStr);
						p_c2sTransferVO.setReceiverVO(receiverVO);	
					}
					//p_requestVO.setReqSelector(String.valueOf(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_TRANSFER_DEF_SELECTOR_CODE))));
					//Changed on 27/05/07 for Service Type selector Mapping
					ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
					if(serviceSelectorMappingVO!=null) {
						p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
					}									
					p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
					break;
				}

				case 5:
				{
					//Do the 000 check Default PIN 
					if(userPhoneVO.getPinRequired().equals(PretupsI.YES))
					{
						try
						{
							ChannelUserBL.validatePIN(p_con,channelUserVO,p_requestArr[4]);
						}
						catch(BTSLBaseException be)
						{
							if(be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) ||  (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
								p_con.commit();
							}
							throw be;
						}	
					}						
					ReceiverVO receiverVO=new ReceiverVO();
					if(PretupsI.GATEWAY_TYPE_EXTGW.equalsIgnoreCase(p_requestVO.getRequestGatewayType()) && ( BTSLUtil.isNullString(p_requestVO.getSenderExternalCode())) && BTSLUtil.isNullString(p_requestVO.getSenderLoginID())){
						//Customer MSISDN Validation
						custMsisdn=p_requestArr[1];
	//					Change for the SID logic
						p_requestVO.setSid(custMsisdn);
						receiverVO.setSid(custMsisdn);
						PrivateRchrgVO prvo=null;
						if((prvo=getPrivateRechargeDetails(p_con,custMsisdn))!=null)
						{
							p_c2sTransferVO.setSubscriberSID(custMsisdn);
							custMsisdn=prvo.getMsisdn();						
						}	
						PretupsBL.validateMsisdn(p_con,receiverVO,p_c2sTransferVO.getRequestID(),custMsisdn);
						//Recharge amount Validation
						requestAmtStr=p_requestArr[2];
						PretupsBL.validateAmount(p_c2sTransferVO,requestAmtStr);
						p_c2sTransferVO.setReceiverVO(receiverVO);		
						
					} else if(PretupsI.GATEWAY_TYPE_EXTGW.equalsIgnoreCase(p_requestVO.getRequestGatewayType())){
						custMsisdn=userPhoneVO.getMsisdn();
						PretupsBL.validateMsisdn(p_con,receiverVO,p_c2sTransferVO.getRequestID(),custMsisdn);
						//Recharge amount Validation
						requestAmtStr=p_requestArr[1];
						PretupsBL.validateAmount(p_c2sTransferVO,requestAmtStr);
						p_c2sTransferVO.setReceiverVO(receiverVO);		
						
					} else {
						//Customer MSISDN Validation
						custMsisdn=p_requestArr[1];
	//					Change for the SID logic
						p_requestVO.setSid(custMsisdn);
						receiverVO.setSid(custMsisdn);
						PrivateRchrgVO prvo=null;
						if((prvo=getPrivateRechargeDetails(p_con,custMsisdn))!=null)
						{
							p_c2sTransferVO.setSubscriberSID(custMsisdn);
							custMsisdn=prvo.getMsisdn();						
						}	
						PretupsBL.validateMsisdn(p_con,receiverVO,p_c2sTransferVO.getRequestID(),custMsisdn);
						//Recharge amount Validation
						requestAmtStr=p_requestArr[2];
						PretupsBL.validateAmount(p_c2sTransferVO,requestAmtStr);
						p_c2sTransferVO.setReceiverVO(receiverVO);	
					}
					if(BTSLUtil.isNullString(p_requestArr[3])) {
						p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
					} else
					{
						int langCode=PretupsBL.getLocaleValueFromCode(p_requestVO,p_requestArr[3]);
						if(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode))==null) {
							throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
						}
						p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
					}
					//p_requestVO.setReqSelector(String.valueOf(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_TRANSFER_DEF_SELECTOR_CODE))));
					//Changed on 27/05/07 for Service Type selector Mapping
					ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
					if(serviceSelectorMappingVO!=null) {
						p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
					}									
					break;
				}

				case 6:
				{
					if(userPhoneVO.getPinRequired().equals(PretupsI.YES))
					{
						try
						{
							ChannelUserBL.validatePIN(p_con,channelUserVO,p_requestArr[5]);
						}
						catch(BTSLBaseException be)
						{
							if(be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) ||  (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
								p_con.commit();
							}
							throw be;
						}								
					}

					ReceiverVO receiverVO=new ReceiverVO();
					//Customer MSISDN Validation
					custMsisdn=p_requestArr[1];
//					Change for the SID logic
					p_requestVO.setSid(custMsisdn);
					receiverVO.setSid(custMsisdn);
					PrivateRchrgVO prvo=null;
					if((prvo=getPrivateRechargeDetails(p_con,custMsisdn))!=null)
					{
						p_c2sTransferVO.setSubscriberSID(custMsisdn);
						custMsisdn=prvo.getMsisdn();						
					}
					PretupsBL.validateMsisdn(p_con,receiverVO,p_c2sTransferVO.getRequestID(),custMsisdn);

					//Recharge amount Validation
					requestAmtStr=p_requestArr[2];
					PretupsBL.validateAmount(p_c2sTransferVO,requestAmtStr);
					p_c2sTransferVO.setReceiverVO(receiverVO);
					if(BTSLUtil.isNullString(p_requestArr[3]))
					{
						if(PretupsI.LOCALE_LANGAUGE_EN.equalsIgnoreCase(p_requestVO.getLocale().getLanguage()))
						{
							//p_requestVO.setReqSelector(String.valueOf(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_TRANSFER_DEF_SELECTOR_CODE))));
							//Changed on 27/05/07 for Service Type selector Mapping
							ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
							if(serviceSelectorMappingVO!=null) {
								p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
							}									
						}
					} else {
						p_requestVO.setReqSelector(p_requestArr[3]);
					}

					PretupsBL.getSelectorValueFromCode(p_requestVO);
					if(BTSLUtil.isNullString(p_requestVO.getReqSelector()))
					{
						//p_requestVO.setReqSelector(String.valueOf(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_TRANSFER_DEF_SELECTOR_CODE))));
						//Changed on 27/05/07 for Service Type selector Mapping
						ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
						if(serviceSelectorMappingVO!=null) {
							p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
						}									
					}
					if(BTSLUtil.isNullString(p_requestArr[4])) {
						p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
					} else
					{
						int langCode=PretupsBL.getLocaleValueFromCode(p_requestVO,p_requestArr[4]);
						if(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode))==null) {
							throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
						}
						p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
					}
					break;
				}
				case 7:
				{
					if(userPhoneVO.getPinRequired().equals(PretupsI.YES))
					{
						try
						{
							ChannelUserBL.validatePIN(p_con,channelUserVO,p_requestArr[6]);
						}
						catch(BTSLBaseException be)
						{
							if(be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) ||  (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
								p_con.commit();
							}
							throw be;
						}								
					}

					ReceiverVO receiverVO=new ReceiverVO();
					//Customer MSISDN Validation
					custMsisdn=p_requestArr[1];
//					Change for the SID logic
					p_requestVO.setSid(custMsisdn);
					receiverVO.setSid(custMsisdn);
					PrivateRchrgVO prvo=null;
					if((prvo=getPrivateRechargeDetails(p_con,custMsisdn))!=null)
					{
						p_c2sTransferVO.setSubscriberSID(custMsisdn);
						custMsisdn=prvo.getMsisdn();						
					}	
					PretupsBL.validateMsisdn(p_con,receiverVO,p_c2sTransferVO.getRequestID(),custMsisdn);

					//Recharge amount Validation
					requestAmtStr=p_requestArr[2];
					PretupsBL.validateAmount(p_c2sTransferVO,requestAmtStr);
					p_c2sTransferVO.setReceiverVO(receiverVO);
					if(BTSLUtil.isNullString(p_requestArr[3]))
					{
						if(PretupsI.LOCALE_LANGAUGE_EN.equalsIgnoreCase(p_requestVO.getLocale().getLanguage()))
						{
							//p_requestVO.setReqSelector(String.valueOf(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_TRANSFER_DEF_SELECTOR_CODE))));
							//Changed on 27/05/07 for Service Type selector Mapping
							ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
							if(serviceSelectorMappingVO!=null) {
								p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
							}									
						}
					} else {
						p_requestVO.setReqSelector(p_requestArr[3]);
					}

					PretupsBL.getSelectorValueFromCode(p_requestVO);
					if(BTSLUtil.isNullString(p_requestVO.getReqSelector()))
					{
						//p_requestVO.setReqSelector(String.valueOf(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_TRANSFER_DEF_SELECTOR_CODE))));
						//Changed on 27/05/07 for Service Type selector Mapping
						ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
						if(serviceSelectorMappingVO!=null) {
							p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
						}									
					}
					//For handling of sender locale
					if(BTSLUtil.isNullString(p_requestArr[4])) {
						p_requestVO.setSenderLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
					} else
					{
						int langCode=PretupsBL.getLocaleValueFromCode(p_requestVO,p_requestArr[4]);
						p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
					}
					if (_log.isDebugEnabled()) {
						_log.debug(this,"sender locale: ="+p_requestVO.getSenderLocale());
					}

					if(BTSLUtil.isNullString(p_requestArr[5])) {
						p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
					} else
					{
						int langCode=PretupsBL.getLocaleValueFromCode(p_requestVO,p_requestArr[5]);
						if(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode))==null) {
							throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
						}
						p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
					}
					break;
				}
				default:
					throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT,0,new String[]{p_requestVO.getActualMessageFormat()},null);
				}
				//Self EVR  Allowed Check
				String senderMSISDN=(channelUserVO.getUserPhoneVO()).getMsisdn();
				String receiverMSISDN=((ReceiverVO)p_c2sTransferVO.getReceiverVO()).getMsisdn();
				if(p_c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_EVR))
				{
					if(receiverMSISDN.equals(senderMSISDN) && !((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ALLOW_SELF_EVR))).booleanValue()) {
						throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.CHNL_ERROR_SELF_TOPUP_NTALLOWD);
					}
				}
			}
			catch(BTSLBaseException be)
			{
				throw be;
			}
			catch(Exception e)
			{
				_log.errorTrace(obj, e);
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorUtil[validateEVDRequestFormat]","","","","Exception while validating user message" +" ,getting Exception="+e.getMessage());
				throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			}
			if(_log.isDebugEnabled()) {
				_log.debug(methodName,"Exiting ");
			}

		}



}
