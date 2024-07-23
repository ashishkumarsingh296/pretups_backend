package com.btsl.pretups.util.clientutils;

/* SafaricomUtil.java
 * Name                        Date            History
 *------------------------------------------------------------------------
 * ved.sharma                 Aug 22, 2007         Initial Creation
 *------------------------------------------------------------------------
 * Copyright (c) 2007 Bharti Telesoft Ltd.
 */

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
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class SafaricomUtil extends OperatorUtil
{
    private Log _log = LogFactory.getLog(this.getClass().getName());
	
	/**
     * This method used for Password validation.
     * While creating or modifying the user Password This method will be used.
	 * Method validatePassword.
	 * @author ved.sharma
	 * @created on 18/06/08
	 * @param p_loginID String
	 * @param p_password String
	 * @return HashMap
	 */
    
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
	    /*if(p_loginID.equals(p_password))
	        messageMap.put("operatorutil.validatepassword.error.sameusernamepassword",null);*/
	    if(!BTSLUtil.containsCapChar(p_password))
		messageMap.put("operatorutil.validatepassword.error.passwordnotcontaincapschar",null);
	    if(_log.isDebugEnabled()) _log.debug("validatePassword","Exiting messageMap.size()="+messageMap.size());
	    return messageMap;
	}
    
	/**
	 * Method generateRandomPassword.
	 * @return String
	 * @author santanu.mohanty
	 */
	public String generateRandomPassword()
	{
		 if(_log.isDebugEnabled()) _log.debug("generateRandomPassword","Entered in to SafaricomUtil");
		String returnStr=null;
		String specialStr="";
		String numberStr=null;
		String alphaStr=null;
		String finalStr=null;
		String SPECIAL_CHARACTERS=null;
		int decreseCounter=0;
		try
		{
			String specialChar=Constants.getProperty("SPECIAL_CHARACTER_PASSWORD_VALIDATION");
			if(!BTSLUtil.isNullString(specialChar))
			{
				decreseCounter=1;
				specialChar=specialChar.replace(",","");
				SPECIAL_CHARACTERS= specialChar;//"~!@#$%^&";
	 			specialStr=BTSLUtil.generateRandomPIN(SPECIAL_CHARACTERS,decreseCounter);
			}
 			final String DIGITS= "0123456789";
 			numberStr=BTSLUtil.generateRandomPIN(DIGITS,1);
 			decreseCounter++;
 			final String LOCASE_CHARACTERS    = "abcdefghijklmnopqrstuvwxyz";
 			final String UPCASE_CHARACTERS    = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; 			
 			final String PRINTABLE_CHARACTERS =LOCASE_CHARACTERS+UPCASE_CHARACTERS;
 			int minLength=((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_LOGIN_PWD_LENGTH))).intValue();
			 while (true){   
			 	alphaStr=BTSLUtil.generateRandomPIN(PRINTABLE_CHARACTERS,minLength-decreseCounter);
	           int  result=BTSLUtil.isSMSPinValid(alphaStr);
	            if(result==-1) 
	                continue;
	            else if(result==1)
	                continue;
	            else 
	            	break;	            	            
	        }
			 finalStr=specialStr+alphaStr+numberStr;
			 returnStr=BTSLUtil.generateRandomPIN(finalStr,minLength);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorUtil[generateRandomPassword]","","","","Exception generate Random Password="+e.getMessage());
			returnStr=null;
		}
		if(_log.isDebugEnabled()) _log.debug("generateRandomPassword","Exiting from SafaricomUtil = "+returnStr);
		return returnStr;
	}
	/* (non-Javadoc)
	 * @see com.btsl.pretups.util.OperatorUtilI#validateC2SBillPmtRequest(java.sql.Connection, com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO, com.btsl.pretups.receiver.RequestVO)
	 */
	public void validateC2SBillPmtRequest(Connection p_con,C2STransferVO p_c2sTransferVO,RequestVO p_requestVO) throws BTSLBaseException
	{
		if(_log.isDebugEnabled()) _log.debug("validateC2SBillPmtRequest","entered:p_c2sTransferVO="+p_c2sTransferVO+" p_requestVO: "+p_requestVO);
		try
		{
			String[] p_requestArr=p_requestVO.getRequestMessageArray();
			String custMsisdn=null;
			String requestAmtStr=null;
			ChannelUserVO channelUserVO=(ChannelUserVO)p_c2sTransferVO.getSenderVO();
			//get the message length
			int messageLen=p_requestArr.length;
			if(_log.isDebugEnabled()) _log.debug("validateC2SBillPmtRequest","messageLen: "+messageLen);
			for(int i=0;i<messageLen;i++)
			{
				if(_log.isDebugEnabled()) _log.debug("validateC2SBillPmtRequest","i: "+i+" value: "+p_requestArr[i]);
			}
			switch(messageLen)
			{
				case 4://message format expected: keyword#receivernumber#amount#pin
				{
					//If pin required flag is Y for the sender in user phones table then validate the user pin
					if((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES))
					{
						try
						{
							ChannelUserBL.validatePIN(p_con,channelUserVO,p_requestArr[3]);
						}
						catch(BTSLBaseException be)
						{
							if(be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN_BILLPAY)) ||  (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK_BILLPAY))))
								p_con.commit();
							throw be;
						}	
					}					
					ReceiverVO receiverVO=new ReceiverVO();
//					set the default local of receiver
					p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
					//Customer MSISDN Validation
					custMsisdn=p_requestArr[1];
					//validate msisdn
					PretupsBL.validateMsisdn(p_con,receiverVO,p_c2sTransferVO.getRequestID(),custMsisdn);
					
					//Recharge amount Validation
					requestAmtStr=p_requestArr[2];
					//validate the amount
					PretupsBL.validateAmount(p_c2sTransferVO,requestAmtStr);
					p_c2sTransferVO.setReceiverVO(receiverVO);
					//p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_DEF_SELECTOR_CODE_BILLPAY));
					ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
					if(serviceSelectorMappingVO!=null)
						p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());									
					
					break;
				}
				
				case 5://message format expected: keyword#receivernumber#amount#receiverNotificationlang#pin
				{
					if((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES))
					{
						try
						{
							//validate pin
							ChannelUserBL.validatePIN(p_con,channelUserVO,p_requestArr[4]);
						}
						catch(BTSLBaseException be)
						{
							if(be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN_BILLPAY)) ||  (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK_BILLPAY))))
								p_con.commit();
							throw be;
						}	
					}					
					ReceiverVO receiverVO=new ReceiverVO();
					if(BTSLUtil.isNullString(p_requestArr[3]))
						p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
					else
					{
						int langCode=PretupsBL.getLocaleValueFromCode(p_requestVO,p_requestArr[3]);
						if(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode))==null)
							throw new BTSLBaseException(this,"validateC2SBillPmtRequest",PretupsErrorCodesI.BILLPAY_INVALID_PAYEE_NOT_LANG);
						p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
					}
					//Customer MSISDN Validation
					custMsisdn=p_requestArr[1];
					
					PretupsBL.validateMsisdn(p_con,receiverVO,p_c2sTransferVO.getRequestID(),custMsisdn);
					
					//Recharge amount Validation
					requestAmtStr=p_requestArr[2];
					PretupsBL.validateAmount(p_c2sTransferVO,requestAmtStr);
					p_c2sTransferVO.setReceiverVO(receiverVO);
					//p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_DEF_SELECTOR_CODE_BILLPAY));
					ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
					if(serviceSelectorMappingVO!=null)
						p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());									
					//if requested local is not null then set that locale otherwise set default
					
					break;
				}
				case 6://message format expected: keyword#receivernumber#amount#selector#receiverNotificationlang#pin
				{
					if((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES))
					{
						try
						{
							ChannelUserBL.validatePIN(p_con,channelUserVO,p_requestArr[5]);
						}
						catch(BTSLBaseException be)
						{
							if(be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN_BILLPAY)) ||  (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK_BILLPAY))))
								p_con.commit();
							throw be;
						}								
					}
					
					ReceiverVO receiverVO=new ReceiverVO();
					if(BTSLUtil.isNullString(p_requestArr[4]))
						p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
					else
					{
						int langCode=PretupsBL.getLocaleValueFromCode(p_requestVO,p_requestArr[4]);
						if(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode))==null)
							throw new BTSLBaseException(this,"validateC2SBillPmtRequest",PretupsErrorCodesI.BILLPAY_INVALID_PAYEE_NOT_LANG);
						p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
					}
					//Customer MSISDN Validation
					custMsisdn=p_requestArr[1];
					
					PretupsBL.validateMsisdn(p_con,receiverVO,p_c2sTransferVO.getRequestID(),custMsisdn);
					
					//Recharge amount Validation
					requestAmtStr=p_requestArr[2];
					PretupsBL.validateAmount(p_c2sTransferVO,requestAmtStr);
					p_c2sTransferVO.setReceiverVO(receiverVO);
					if(BTSLUtil.isNullString(p_requestArr[3]))
					{
						if(PretupsI.LOCALE_LANGAUGE_EN.equalsIgnoreCase(p_requestVO.getLocale().getLanguage()))
						{
							//p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_DEF_SELECTOR_CODE_BILLPAY));
							ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
							if(serviceSelectorMappingVO!=null)
								p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());									
						}
					}
					else
						p_requestVO.setReqSelector(p_requestArr[3]);
					
					PretupsBL.getSelectorValueFromCode(p_requestVO);
					if(BTSLUtil.isNullString(p_requestVO.getReqSelector()))
					{
						//p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_DEF_SELECTOR_CODE_BILLPAY));
						ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
						if(serviceSelectorMappingVO!=null)
							p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());									
					}
					
					break;
				}
				case 7://message format expected: keyword#receivernumber#amount#selector#senderlang#receiverNotificationlang#pin
				{
					if((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES))
					{
						try
						{
							ChannelUserBL.validatePIN(p_con,channelUserVO,p_requestArr[6]);
						}
						catch(BTSLBaseException be)
						{
							if(be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN_BILLPAY)) ||  (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK_BILLPAY))))
								p_con.commit();
							throw be;
						}								
					}
										
					ReceiverVO receiverVO=new ReceiverVO();
					if(BTSLUtil.isNullString(p_requestArr[5]))
						p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
					else
					{
						int langCode=PretupsBL.getLocaleValueFromCode(p_requestVO,p_requestArr[5]);
						if(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode))==null)
							throw new BTSLBaseException(this,"validateC2SBillPmtRequest",PretupsErrorCodesI.BILLPAY_INVALID_PAYEE_NOT_LANG);
						p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
					}
					//Customer MSISDN Validation
					custMsisdn=p_requestArr[1];
					
					PretupsBL.validateMsisdn(p_con,receiverVO,p_c2sTransferVO.getRequestID(),custMsisdn);
					
					//Recharge amount Validation
					requestAmtStr=p_requestArr[2];
					PretupsBL.validateAmount(p_c2sTransferVO,requestAmtStr);
					p_c2sTransferVO.setReceiverVO(receiverVO);
					if(BTSLUtil.isNullString(p_requestArr[3]))
					{
						if(PretupsI.LOCALE_LANGAUGE_EN.equalsIgnoreCase(p_requestVO.getLocale().getLanguage()))
						{
							//p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_DEF_SELECTOR_CODE_BILLPAY));
							ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
							if(serviceSelectorMappingVO!=null)
								p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());									
						}
					}
					else
						p_requestVO.setReqSelector(p_requestArr[3]);
					
					PretupsBL.getSelectorValueFromCode(p_requestVO);
					if(BTSLUtil.isNullString(p_requestVO.getReqSelector()))
					{
						//p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_DEF_SELECTOR_CODE_BILLPAY));
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
						p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
						//ChangeID=LOCALEMASTER
						//Sender locale has to be overwritten in transferVO also.
						p_c2sTransferVO.setLocale(p_requestVO.getSenderLocale());
						p_c2sTransferVO.setLanguage(p_c2sTransferVO.getLocale().getLanguage());
						p_c2sTransferVO.setCountry(p_c2sTransferVO.getLocale().getCountry());
					}
					if (_log.isDebugEnabled()) 
				        _log.debug("validateC2SBillPmtRequest","sender locale: ="+p_requestVO.getSenderLocale());
					
					
					break;
				}
				default:
					throw new BTSLBaseException(this,"validateC2SBillPmtRequest",PretupsErrorCodesI.BILLPAY_INVALID_MESSAGE_FORMAT,0,new String[]{p_requestVO.getActualMessageFormat()},null);
			}
		}
		catch(BTSLBaseException be)
		{
			throw be;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			_log.error("validateC2SBillPmtRequest","  Exception while validating user message :"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorUtil[validateC2SBillPmtRequest]","","","","Exception while validating user message" +" ,getting Exception="+e.getMessage());
			throw new BTSLBaseException(this,"validateC2SBillPmtRequest",PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if(_log.isDebugEnabled()) _log.debug("validateC2SBillPmtRequest","Exiting ");
	}
}
