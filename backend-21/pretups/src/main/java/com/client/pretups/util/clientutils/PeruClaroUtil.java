/**
 * @(#)PeruClaroUtil.java
 * Copyright(c) 2009, Bharti Telesoft Ltd.
 * All Rights Reserved
 *
 * <description>
 *-------------------------------------------------------------------------------------------------
 * Author                        Date            History
 *-------------------------------------------------------------------------------------------------
 * Sushma              	     Sep 21, 2009       Initital Creation
 *-------------------------------------------------------------------------------------------------
 *
 */
package com.client.pretups.util.clientutils;

import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDetailsVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferItemVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.privaterecharge.businesslogic.PrivateRchrgDAO;
import com.btsl.pretups.privaterecharge.businesslogic.PrivateRchrgVO;
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
import com.btsl.util.Constants;

public class PeruClaroUtil extends OperatorUtil
{

	private Log _log = LogFactory.getLog(this.getClass().getName());


	/**
	 * This method used for Password validation.
	 * While creating or modifying the user Password This method will be used.
	 * Method validatePassword.
	 * @param p_loginID String
	 * @param p_password String
	 * @return HashMap
	 */
	public HashMap validatePassword(String p_loginID, String p_password)
	{
		_log.debug("validatePassword","Entered, p_userID= ",new String(p_loginID+", Password= "+p_password));
		HashMap messageMap=new HashMap();
        int asciiCode = 0;
        boolean isConSeq = false;
        int previousAsciiCode = 0;
        int numSeqcount = 0;
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
		boolean repeatedCharacter = false;
		 Map<Character, Integer> dupMap = new HashMap<Character, Integer>(); 
	        char[] passCharArr = p_password.toCharArray();
	        for(Character ch:passCharArr){
	            if(dupMap.containsKey(ch)){
	                dupMap.put(ch, dupMap.get(ch)+1);
	            } else {
	                dupMap.put(ch, 1);
	            }
	        }
	
	        Set<Character> keys = dupMap.keySet();
	        for(Character ch:keys){
	            if(dupMap.get(ch) > Integer.parseInt(Constants.getProperty("ALLOWED_CHARACTER_OCCURANCE"))){
	            	repeatedCharacter =true;
	            	break;
	            }
	        }



	        for (int i = 0; i < passCharArr.length; i++) {
	            asciiCode = passCharArr[i];
	            if ((previousAsciiCode + 1) == asciiCode) {
	                numSeqcount++;
	                if (numSeqcount >= 1) {
	                    isConSeq = true;
	                    break;
	                }
	            } else {
	                numSeqcount = 0;
	            }
	            previousAsciiCode = asciiCode;
	        }
	    if(isConSeq)  
			messageMap.put("operatorutil.validatepassword.error.passwordconsecutive",null);
	    if(repeatedCharacter)
	    	messageMap.put("operatorutil.validatepassword.error.passwordsamedigit",null);
		if(!numberStrFlag)
			messageMap.put("operatorutil.validatepassword.error.passwordnumberchar",null);
		if(p_loginID.equals(p_password))
			messageMap.put("operatorutil.validatepassword.error.sameusernamepassword",null);
		if(_log.isDebugEnabled()) _log.debug("validatePassword","Exiting ");
		return messageMap;
	}

	
	
	/**
	 * This method used for Password validation.
	 * While creating or modifying the user Password This method will be used.
	 * Method validatePassword.
	 * @param p_loginID String
	 * @param p_password String
	 * @return HashMap
	 */
	public HashMap validatePassword(String p_password)
	{
		_log.debug("validatePassword","Entered,  Password= "+p_password);
		HashMap messageMap=new HashMap();
        int asciiCode = 0;
        boolean isConSeq = false;
        int previousAsciiCode = 0;
        int numSeqcount = 0;
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
		boolean repeatedCharacter = false;
		 Map<Character, Integer> dupMap = new HashMap<Character, Integer>(); 
	        char[] passCharArr = p_password.toCharArray();
	        for(Character ch:passCharArr){
	            if(dupMap.containsKey(ch)){
	                dupMap.put(ch, dupMap.get(ch)+1);
	            } else {
	                dupMap.put(ch, 1);
	            }
	        }
	
	        Set<Character> keys = dupMap.keySet();
	        for(Character ch:keys){
	            if(dupMap.get(ch) > Integer.parseInt(Constants.getProperty("ALLOWED_CHARACTER_OCCURANCE"))){
	            	repeatedCharacter =true;
	            	break;
	            }
	        }



	        for (int i = 0; i < passCharArr.length; i++) {
	            asciiCode = passCharArr[i];
	            if ((previousAsciiCode + 1) == asciiCode) {
	                numSeqcount++;
	                if (numSeqcount >= 1) {
	                    isConSeq = true;
	                    break;
	                }
	            } else {
	                numSeqcount = 0;
	            }
	            previousAsciiCode = asciiCode;
	        }
	    if(isConSeq)  
			messageMap.put("operatorutil.validatepassword.error.passwordconsecutive",null);
	    if(repeatedCharacter)
	    	messageMap.put("operatorutil.validatepassword.error.passwordsamedigit",null);
		if(!numberStrFlag)
			messageMap.put("operatorutil.validatepassword.error.passwordnumberchar",null);
		if(_log.isDebugEnabled()) _log.debug("validatePassword","Exiting ");
		return messageMap;
	}

	
	/**
	 * Method generateRandomPassword.
	 * @return String
	 */
	public String generateRandomPassword()
	{
		if(_log.isDebugEnabled()) _log.debug("generateRandomPassword","Entered in to PeruClaroUtil");
		String returnStr=null;
		String specialStr="";
		String numberStr=null;
		String alphaStr=null;
		String finalStr=null;
		String SPECIAL_CHARACTERS=null;
		int decreseCounter=0;
		try
		{
			boolean flag=false;
			int count=0;
			do {
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
			if(validatePassword(returnStr).isEmpty())
			{
				flag=true;
			}else{
				Thread.sleep(500);
				if(count>5)
				{
					flag=true;
				}
				count++;
				if(_log.isDebugEnabled()) _log.debug("generateRandomPassword","Regenerating Password ="+returnStr);
			}
			} while (!flag);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorUtil[generateRandomPassword]","","","","Exception generate Random Password="+e.getMessage());
			returnStr=null;
		}
		if(_log.isDebugEnabled()) _log.debug("generateRandomPassword","Exiting from PeruClaroUtil = "+returnStr);
		return returnStr;
	}

	/**
	 * This method validate the LoginId.
	 * 9:47:00 AM
	 * HashMap
	 * sushma.salve
	 */	
	public HashMap validateLoginId(String p_loginID)
	{
		_log.debug("validateLoginId","Entered, p_userID= "+p_loginID);

		HashMap messageMap=new HashMap();
		if(!BTSLUtil.containsChar(p_loginID))
			messageMap.put("operatorutil.validateloginid.error.logindnotcontainschar",null);
		// for special character
		String specialChar=Constants.getProperty("SPECIAL_CHARACTER_LOGIN_VALIDATION");
		if(!BTSLUtil.isNullString(specialChar))
		{
			String[] specialCharArray={specialChar};
			String[] passwordCharArray=specialChar.split(",");
			boolean specialCharFlag=false;
			for(int i=0,j=passwordCharArray.length;i<j;i++)
			{
				if(p_loginID.contains(passwordCharArray[i]))
				{
					specialCharFlag=true;
					break;
				}
			}
			if(!specialCharFlag)
				messageMap.put("operatorutil.validateloginid.error.loginspecialchar",specialCharArray);
		}   	

		// for number
		String[]passwordNumberStrArray={"0","1","2","3","4","5","6","7","8","9"};
		boolean numberStrFlag=false;
		for(int i=0,j=passwordNumberStrArray.length;i<j;i++)
		{
			if(p_loginID.contains(passwordNumberStrArray[i]))
			{
				numberStrFlag=true;
				break;
			}
		}
		if(!numberStrFlag)
			messageMap.put("operatorutil.validatelogind.error.loginidnumberchar",null);

		if(_log.isDebugEnabled()) _log.debug("validateLoginId","Exiting ");
		return messageMap;
	}

	/**
	 * @author vinay.kumar
	 * @param p_INPromo double
	 * @param p_BonusTalkTime long
	 * @return finalPromo double
	 */	
	public double calculateINPromo(double p_INPromo,long p_BonusTalkTime)
	{
		double finalPromo=0;

		if(p_INPromo==0 && p_BonusTalkTime==0)
			finalPromo=0;
		else if(p_INPromo!=0 && p_BonusTalkTime==0)
			finalPromo=0;
		else if(p_INPromo==0 && p_BonusTalkTime!=0)
			finalPromo=p_BonusTalkTime;    
		else if(p_BonusTalkTime>p_INPromo)				
			finalPromo=p_BonusTalkTime-p_INPromo;
		else
			finalPromo=0;

		return finalPromo;
	}

	/**
	 * Method calculateReceiverTransferValue.
	 * @param p_requestedValue long
	 * @param p_calculatedAccessFee long
	 * @param p_calculatedTax1Value long
	 * @param p_calculatedTax2Value long
	 * @param p_calculatedBonusTalkTimeValue long
	 * @return long
	 * @see com.btsl.pretups.util.OperatorUtilI#calculateReceiverTransferValue(long, long, long, long, long)
	 */
	public long calculateReceiverTransferValue(long p_requestedValue,long p_calculatedAccessFee,long p_calculatedTax1Value,long p_calculatedTax2Value,long p_calculatedBonusTalkTimeValue)  
	{
		long transferValue=p_requestedValue-p_calculatedAccessFee-p_calculatedTax1Value-p_calculatedTax2Value+p_calculatedBonusTalkTimeValue;
		return transferValue;
	}

	/**
	 * Method that will validate the user message sent in case of postpaid bill payment
	 * This method is added on 15/05/06 for postpaid bill payment.
	 * 
	 * @param p_con
	 * @param p_c2sTransferVO
	 * @param p_requestVO
	 * @throws BTSLBaseException
	 * @see com.btsl.pretups.util.OperatorUtilI#validateC2SBillPmtRequest(Connection, C2STransferVO, RequestVO)
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
				//set the default local of receiver
				p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
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
				if(BTSLUtil.isNullString(p_requestArr[3]))
					p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
				else
				{
					int langCode=PretupsBL.getLocaleValueFromCode(p_requestVO,p_requestArr[3]);
					if(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode))==null)
						p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
					else
						p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
					//throw new BTSLBaseException(this,"validateC2SBillPmtRequest",PretupsErrorCodesI.BILLPAY_INVALID_PAYEE_NOT_LANG);

				}
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
				if(BTSLUtil.isNullString(p_requestArr[4]))
					p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
				else
				{
					int langCode=PretupsBL.getLocaleValueFromCode(p_requestVO,p_requestArr[4]);
					if(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode))==null)
						throw new BTSLBaseException(this,"validateC2SBillPmtRequest",PretupsErrorCodesI.BILLPAY_INVALID_PAYEE_NOT_LANG);
					p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
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

				if(BTSLUtil.isNullString(p_requestArr[5]))
					p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
				else
				{
					int langCode=PretupsBL.getLocaleValueFromCode(p_requestVO,p_requestArr[5]);
					if(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode))==null)
						throw new BTSLBaseException(this,"validateC2SBillPmtRequest",PretupsErrorCodesI.BILLPAY_INVALID_PAYEE_NOT_LANG);
					p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
				}
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

	/**
	 * @author vinay.kumar
	 * @param p_RequestedAmount long
	 * @param p_BonusTalkTime long
	 * @return rechargeComment String
	 */	
	public String getRechargeComment(long p_RequestedAmount,long p_BonusTalkTime)
	{
		String rechargeComment="";
		int rem=0;
		try
		{
			rem=(int)(p_BonusTalkTime/p_RequestedAmount);
		}
		catch(Exception e)
		{
			rem =0;	
		}
		try
		{
			switch(rem)
			{
			case 0:
				rechargeComment="NORMAL";
				break;
			case 1:
				rechargeComment="DOUBLE";
				break;
			case 2:
				rechargeComment="TRIPPLE";
				break;
			case 3:
				rechargeComment="QUADRUPLE";
				break;
			case 4:
				rechargeComment="PENTUPLE";
				break;
			case 5:
				rechargeComment="HEXTUPLE";
				break;
			case 6:
				rechargeComment="SEPTUPLE";
				break;	
			case 7:
				rechargeComment="OCTUPLE";
				break;
			case 8:
				rechargeComment="NONUPLE";
				break;
			case 9:
				rechargeComment="DECUPLE";
				break;
			default:
				rechargeComment="NORMAL";
			break;
			}
		}
		catch (ArithmeticException e)
		{
			rechargeComment="NORMAL";
		}
		return rechargeComment;
	}

	/**
	 * 
	 * @param p_con
	 * @param p_c2sTransferVO
	 * @param p_requestVO
	 * @throws BTSLBaseException
	 */
	public void validateSIMACTRequest(Connection p_con,C2STransferVO p_c2sTransferVO,RequestVO p_requestVO) throws BTSLBaseException
	{
		if(_log.isDebugEnabled()) _log.debug("validateSIMACTRequest","Entered");
		try
		{
			String[] p_requestArr=p_requestVO.getRequestMessageArray();
			String custMsisdn=null;
			String cellId=null;
			String switchId=null;
			ChannelUserVO channelUserVO=(ChannelUserVO)p_c2sTransferVO.getSenderVO();
			int messageLen=p_requestArr.length;
			if(_log.isDebugEnabled()) _log.debug("validateSIMACTRequest","messageLen: "+messageLen);
			for(int i=0;i<messageLen;i++)
			{
				if(_log.isDebugEnabled()) _log.debug("validateSIMACTRequest","i: "+i+" value: "+p_requestArr[i]);
			}
			switch(messageLen)
			{
			case 9:
			{
				if(!(SystemPreferences.USSD_NEW_TAGS_MANDATORY)&&!(PretupsI.GATEWAY_TYPE_USSD.equals(p_c2sTransferVO.getRequestGatewayType())))// cellid and switch id not mandatory in request
				{
					if((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES))
					{
						try
						{
							ChannelUserBL.validatePIN(p_con,channelUserVO,p_requestArr[8]);
						}
						catch(BTSLBaseException be)
						{
							if(be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) ||  (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK))))
								p_con.commit();
							throw be;
						}								
					}
					ReceiverVO receiverVO=new ReceiverVO();
					custMsisdn=p_requestArr[1];
					PretupsBL.validateMsisdn(p_con,receiverVO,p_c2sTransferVO.getRequestID(),custMsisdn);
					p_c2sTransferVO.setReceiverVO(receiverVO);
					break;
				}
				else// cellid and switch id mandatory in request
				{
					//Do the 000 check Default PIN 
					if((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES))
					{
						try
						{
							ChannelUserBL.validatePIN(p_con,channelUserVO,p_requestArr[8]);
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
					PretupsBL.validateMsisdn(p_con,receiverVO,p_c2sTransferVO.getRequestID(),custMsisdn);
					p_c2sTransferVO.setReceiverVO(receiverVO);
					cellId=p_requestArr[6];
					switchId=p_requestArr[7];
					//validations to be done on cellId switchId to be put here
					p_requestVO.setCellId(cellId);
					p_requestVO.setSwitchId(switchId);						
					break;
				}
			}
			case 11:
			{
				if(!(SystemPreferences.USSD_NEW_TAGS_MANDATORY)&&!(PretupsI.GATEWAY_TYPE_USSD.equals(p_c2sTransferVO.getRequestGatewayType())))// cellid and switch id not mandatory in request
				{
					if((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES))
					{
						try
						{
							ChannelUserBL.validatePIN(p_con,channelUserVO,p_requestArr[10]);
						}
						catch(BTSLBaseException be)
						{
							if(be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) ||  (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK))))
								p_con.commit();
							throw be;
						}								
					}
					ReceiverVO receiverVO=new ReceiverVO();
					custMsisdn=p_requestArr[1];
					PretupsBL.validateMsisdn(p_con,receiverVO,p_c2sTransferVO.getRequestID(),custMsisdn);
					p_c2sTransferVO.setReceiverVO(receiverVO);			
					break;
				}
				else// cellid and switch id mandatory in request
				{
					//Do the 000 check Default PIN 
					if((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES))
					{
						try
						{
							ChannelUserBL.validatePIN(p_con,channelUserVO,p_requestArr[10]);
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
					PretupsBL.validateMsisdn(p_con,receiverVO,p_c2sTransferVO.getRequestID(),custMsisdn);
					p_c2sTransferVO.setReceiverVO(receiverVO);		
					//=======cellID switchID=============
					cellId=p_requestArr[8];
					switchId=p_requestArr[9];
					//validations to be done on cellId switchId to be put here
					p_requestVO.setCellId(cellId);
					p_requestVO.setSwitchId(switchId);
					//=======cellID switchID=============
					break;
				}			
			}
			default:
				throw new BTSLBaseException(this,"validateSIMACTRequest",PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT,0,new String[]{p_requestVO.getActualMessageFormat()},null);
			}
		}
		catch(BTSLBaseException be)
		{
			throw be;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			_log.error("validateSIMACTRequest","  Exception while validating user message :"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PretupsBL[validateSIMACTRequest]","","","","Exception while validating user message" +" ,getting Exception="+e.getMessage());
			throw new BTSLBaseException(this,"validateSIMACTRequest",PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if(_log.isDebugEnabled()) _log.debug("validateSIMACTRequest","Exiting ");
	}


	/**
	 * Method that will validate the user message sent for DTH
	 * @param p_con
	 * @param p_c2sTransferVO
	 * @param p_requestVO
	 * @throws BTSLBaseException
	 * @see com.btsl.pretups.util.OperatorUtilI#validateC2SRechargeRequest(Connection, C2STransferVO, RequestVO)
	 */
	public void validateDTHRechargeRequest(Connection p_con,C2STransferVO p_c2sTransferVO,RequestVO p_requestVO) throws BTSLBaseException
	{
		try
		{
			String[] p_requestArr=p_requestVO.getRequestMessageArray();
			String custMsisdn=null;

			String requestAmtStr=null;
			ChannelUserVO channelUserVO=(ChannelUserVO)p_c2sTransferVO.getSenderVO();
			UserPhoneVO userPhoneVO=null;
			if(!channelUserVO.isStaffUser())
				userPhoneVO=(UserPhoneVO)channelUserVO.getUserPhoneVO();
			else
				userPhoneVO=(UserPhoneVO)channelUserVO.getStaffUserDetails().getUserPhoneVO();

			int messageLen=p_requestArr.length;
			if(_log.isDebugEnabled()) _log.debug("validateDTHRechargeRequest","messageLen: "+messageLen);

			for(int i=0;i<messageLen;i++)
			{
				if(_log.isDebugEnabled()) _log.debug("validateDTHRechargeRequest","i: "+i+" value: "+p_requestArr[i]);
			}
			switch(messageLen)
			{
			case 4:
			{
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

				PretupsBL.validateDTHAccount(p_con,receiverVO,p_c2sTransferVO.getRequestID(),custMsisdn);

				//Recharge amount Validation
				requestAmtStr=p_requestArr[2];
				PretupsBL.validateAmount(p_c2sTransferVO,requestAmtStr);
				p_c2sTransferVO.setReceiverVO(receiverVO);		

				//Changed on 27/05/07 for Service Type selector Mapping
				ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
				if(serviceSelectorMappingVO!=null)
					p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());									
				p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
				break;
			}

			case 5:
			{

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
				PretupsBL.validateDTHAccount(p_con,receiverVO,p_c2sTransferVO.getRequestID(),custMsisdn);

				//Recharge amount Validation
				requestAmtStr=p_requestArr[2];
				PretupsBL.validateAmount(p_c2sTransferVO,requestAmtStr);
				p_c2sTransferVO.setReceiverVO(receiverVO);
				if(BTSLUtil.isNullString(p_requestArr[3]))
					p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
				else
				{
					int langCode=PretupsBL.getLocaleValueFromCode(p_requestVO,p_requestArr[3]);
					if(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode))==null)
						throw new BTSLBaseException(this,"validateDTHRechargeRequest",PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
					p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
				}

				//Changed on 27/05/07 for Service Type selector Mapping
				ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
				if(serviceSelectorMappingVO!=null)
					p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());									
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
				PretupsBL.validateDTHAccount(p_con,receiverVO,p_c2sTransferVO.getRequestID(),custMsisdn);

				//Recharge amount Validation
				requestAmtStr=p_requestArr[2];
				PretupsBL.validateAmount(p_c2sTransferVO,requestAmtStr);
				p_c2sTransferVO.setReceiverVO(receiverVO);
				if(BTSLUtil.isNullString(p_requestArr[3]))
				{
					if("en".equalsIgnoreCase(p_requestVO.getLocale().getLanguage()))
					{
						//p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE));
						//Changed on 27/05/07 for Service Type selector Mapping
						ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
						if(serviceSelectorMappingVO!=null)
							p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());									
					}
					//changed for CRE_INT_CR00029 by ankit Zindal
				}
				else
					p_requestVO.setReqSelector(p_requestArr[3]);

				PretupsBL.getSelectorValueFromCode(p_requestVO);
				//changed for CRE_INT_CR00029 by ankit Zindal
				if(BTSLUtil.isNullString(p_requestVO.getReqSelector()))
				{
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
						throw new BTSLBaseException(this,"validateDTHRechargeRequest",PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
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

				PretupsBL.validateDTHAccount(p_con,receiverVO,p_c2sTransferVO.getRequestID(),custMsisdn);

				//Recharge amount Validation
				requestAmtStr=p_requestArr[2];
				PretupsBL.validateAmount(p_c2sTransferVO,requestAmtStr);
				p_c2sTransferVO.setReceiverVO(receiverVO);
				if(BTSLUtil.isNullString(p_requestArr[3]))
				{
					if("en".equalsIgnoreCase(p_requestVO.getLocale().getLanguage()))
					{
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

					p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
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
						throw new BTSLBaseException(this,"validateDTHRechargeRequest",PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
					p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
				}
				break;
			}
			default:
				throw new BTSLBaseException(this,"validateDTHRechargeRequest",PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT,0,new String[]{p_requestVO.getActualMessageFormat()},null);
			}
		}
		catch(BTSLBaseException be)
		{
			throw be;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			_log.error("validateDTHRechargeRequest","  Exception while validating user message :"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PretupsBL[validateDTHRechargeRequest]","","","","Exception while validating user message" +" ,getting Exception="+e.getMessage());
			throw new BTSLBaseException(this,"validateDTHRechargeRequest",PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if(_log.isDebugEnabled()) _log.debug("validateDTHRechargeRequest","Exiting ");
	}


	//added for OTP generetion
	synchronized public  String generateOTP() throws Exception 
	{
		String chars=null;
		try {
			chars = Constants.getProperty("OTP_PIN_GEN_ARG");
		} catch (Exception e) {
			chars = "1234567890";
		}

		int passLength = 0;
		try {
			passLength = Integer.parseInt(Constants.getProperty("OTP_PIN_LENGTH"));
		} catch (Exception e) {
			passLength = 6;
		}
		StringBuffer temp=null;
		try{
			if (_log.isDebugEnabled()) 
				_log.debug("generateOTP Entered with Chars"+chars+"  And OTP Length",passLength);
			if (passLength > chars.length()) {
				throw new Exception ("Random number minimum length should be less than the provided chars list length");
			}
			Random m_generator = new Random(System.nanoTime());
			char[] availableChars = chars.toCharArray();
			int availableCharsLeft = availableChars.length;
			temp = new StringBuffer(passLength);
			int pos=0;
			for (int i = 0; i < passLength; ) {
				pos = (int) (availableCharsLeft * m_generator.nextDouble());
				if(i==0)
				{
					if(!String.valueOf(availableChars[pos]).equalsIgnoreCase("1") && !String.valueOf(availableChars[pos]).equalsIgnoreCase("0")){
						i++;
						temp.append(availableChars[pos]);
						availableChars[pos] = availableChars[availableCharsLeft - 1];
						--availableCharsLeft;
					}
				}else{
					temp.append(availableChars[pos]);
					i++;
					availableChars[pos] = availableChars[availableCharsLeft - 1];
					--availableCharsLeft;
				}
			}
		}catch (Exception e) {
			temp = null;
			throw new Exception ("Exception In generating OTP");
		}
		if (_log.isDebugEnabled()) 
			_log.debug("generateOTP Exiting","");
		return String.valueOf(temp);
	}

	/**
	 * Method that will validate the user message sent
	 * @param p_con
	 * @param p_c2sTransferVO
	 * @param p_requestVO
	 * @throws BTSLBaseException
	 * @see com.btsl.pretups.util.OperatorUtilI#validateC2SRechargeRequest(Connection, C2STransferVO, RequestVO)
	 */
	public void validateC2SRequestWithoutAmount(Connection p_con,C2STransferVO p_c2sTransferVO,RequestVO p_requestVO) throws BTSLBaseException
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
			if(_log.isDebugEnabled()) _log.debug("validateC2SRequestWithoutAmount","messageLen: "+messageLen);
			for(int i=0;i<messageLen;i++)
			{
				if(_log.isDebugEnabled()) _log.debug("validateC2SRequestWithoutAmount","i: "+i+" value: "+p_requestArr[i]);
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
				try{
					if(!BTSLUtil.isNullString(requestAmtStr))
					{
						if(Integer.parseInt(requestAmtStr.trim())>0)
							PretupsBL.validateAmount(p_c2sTransferVO,requestAmtStr);
					}
				}catch (Exception e) {
				}
				p_c2sTransferVO.setReceiverVO(receiverVO);		
				
				ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
				if(serviceSelectorMappingVO!=null)
					p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());									
				
				p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
				
				break;
			}

			case 5:
			{
//				Do the 000 check Default PIN 
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
//				Change for the SID logic
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
				try{
					if(!BTSLUtil.isNullString(requestAmtStr))
					{
						if(Integer.parseInt(requestAmtStr.trim())>0)
							PretupsBL.validateAmount(p_c2sTransferVO,requestAmtStr);
					}
				}catch (Exception e) {
				}
				p_c2sTransferVO.setReceiverVO(receiverVO);
				
				if(BTSLUtil.isNullString(p_requestArr[3]))
				{
					if("en".equalsIgnoreCase(p_requestVO.getLocale().getLanguage()))
					{
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
					ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
					if(serviceSelectorMappingVO!=null)
						p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());									
				}

				p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));

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
//				Change for the SID logic
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
				try{
					if(!BTSLUtil.isNullString(requestAmtStr))
					{
						if(Integer.parseInt(requestAmtStr.trim())>0)
							PretupsBL.validateAmount(p_c2sTransferVO,requestAmtStr);
					}
				}catch (Exception e) {
				}					p_c2sTransferVO.setReceiverVO(receiverVO);
			
				if(BTSLUtil.isNullString(p_requestArr[3]))
				{
					if("en".equalsIgnoreCase(p_requestVO.getLocale().getLanguage()))
					{
						//p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE));
						//Changed on 27/05/07 for Service Type selector Mapping
						ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
						if(serviceSelectorMappingVO!=null)
							p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());									
					}
				}
				else
					p_requestVO.setReqSelector(p_requestArr[3]);

				PretupsBL.getSelectorValueFromCode(p_requestVO);
//				changed for CRE_INT_CR00029 by ankit Zindal
				if(BTSLUtil.isNullString(p_requestVO.getReqSelector()))
				{
					//p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE));
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
						throw new BTSLBaseException(this,"validateC2SRequestWithoutAmount",PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
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
//				Change for the SID logic
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
				try{
					if(!BTSLUtil.isNullString(requestAmtStr))
					{
						if(Integer.parseInt(requestAmtStr.trim())>0)
							PretupsBL.validateAmount(p_c2sTransferVO,requestAmtStr);
					}
				}catch (Exception e) {
				}
				p_c2sTransferVO.setReceiverVO(receiverVO);
				if(BTSLUtil.isNullString(p_requestArr[3]))
				{
					if("en".equalsIgnoreCase(p_requestVO.getLocale().getLanguage()))
					{
						//p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE));
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
					//p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE));
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
						throw new BTSLBaseException(this,"validateC2SRequestWithoutAmount",PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
					p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
				}
				break;
			}
			
			case 8:

				//if((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES) && !PretupsI.DEFAULT_C2S_PIN.equals(BTSLUtil.decryptText((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getSmsPin())))
				if(userPhoneVO.getPinRequired().equals(PretupsI.YES))
				{
					try
					{
						ChannelUserBL.validatePIN(p_con,channelUserVO,p_requestArr[7]);
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
//				Change for the SID logic
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
				try{
					if(!BTSLUtil.isNullString(requestAmtStr))
					{
						if(Integer.parseInt(requestAmtStr.trim())>0)
							PretupsBL.validateAmount(p_c2sTransferVO,requestAmtStr);
					}
				}catch (Exception e) {
				}
				p_c2sTransferVO.setReceiverVO(receiverVO);
				
				p_requestVO.getRequestMap().put("PLANID",p_requestArr[3]);
				
				if(BTSLUtil.isNullString(p_requestArr[4]))
				{
					if("en".equalsIgnoreCase(p_requestVO.getLocale().getLanguage()))
					{
						//p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE));
						//Changed on 27/05/07 for Service Type selector Mapping
						ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
						if(serviceSelectorMappingVO!=null)
							p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());									
					}
					/*						else
								p_requestVO.setReqSelector((Constants.getProperty("CVG_UNICODE_"+p_requestVO.getLocale().getLanguage().toUpperCase())));
					 */					}
				else
					p_requestVO.setReqSelector(p_requestArr[4]);

				PretupsBL.getSelectorValueFromCode(p_requestVO);
				//changed for CRE_INT_CR00029 by ankit Zindal
				if(BTSLUtil.isNullString(p_requestVO.getReqSelector()))
				{
					//p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE));
					//Changed on 27/05/07 for Service Type selector Mapping
					ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
					if(serviceSelectorMappingVO!=null)
						p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());									
				}
				//For handling of sender locale
				if(BTSLUtil.isNullString(p_requestArr[5]))
					p_requestVO.setSenderLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
				else
				{
					int langCode=PretupsBL.getLocaleValueFromCode(p_requestVO,p_requestArr[5]);
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

				if(BTSLUtil.isNullString(p_requestArr[6]))
					p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
				else
				{
					int langCode=PretupsBL.getLocaleValueFromCode(p_requestVO,p_requestArr[6]);
					if(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode))==null)
						throw new BTSLBaseException(this,"validateC2SRequestWithoutAmount",PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
					p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
				}
				break;
			
			default:
				throw new BTSLBaseException(this,"validateC2SRequestWithoutAmount",PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT,0,new String[]{p_requestVO.getActualMessageFormat()},null);
			}
		}
		catch(BTSLBaseException be)
		{
			throw be;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			_log.error("validateC2SRequestWithoutAmount","  Exception while validating user message :"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PretupsBL[validateC2SRequestWithoutAmount]","","","","Exception while validating user message" +" ,getting Exception="+e.getMessage());
			throw new BTSLBaseException(this,"validateC2SRequestWithoutAmount",PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if(_log.isDebugEnabled()) _log.debug("validateC2SRequestWithoutAmount","Exiting ");
	}

	/**
	 * Method to format Channel User Creation Transfer ID
	 * @param p_transferVO
	 * @param p_tempTransferID
	 * @return String
	 */
	public String formatChnlUserTransferID(TransferVO p_transferVO,long p_tempTransferID)
	{

		String returnStr=null;
		try
		{
			String paddedTransferIDStr=BTSLUtil.padZeroesToLeft(Long.toHexString(p_tempTransferID),CHANEL_TRANSFER_ID_PAD_LENGTH); 
			returnStr="CU"+currentDateTimeFormatString(p_transferVO.getCreatedOn())+"."+currentTimeFormatString(p_transferVO.getCreatedOn())+"."+Constants.getProperty("INSTANCE_ID")+paddedTransferIDStr;
			p_transferVO.setTransferID(returnStr);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PeruClaroUtil[]","","","","Not able to generate Transfer ID:"+e.getMessage());
			returnStr=null;
		}
		return returnStr;

	}

	public String formatTransferID(TransferVO p_transferVO,long p_tempTransferID,String requestedId)
	{
		String returnStr=null;
		try
		{
			String paddedTransferIDStr=BTSLUtil.padZeroesToLeft(Long.toHexString(p_tempTransferID),C2S_TRANSFER_ID_PAD_LENGTH); 
			returnStr=requestedId+currentDateTimeFormatString(p_transferVO.getCreatedOn())+"."+currentTimeFormatString(p_transferVO.getCreatedOn())+"."+Constants.getProperty("INSTANCE_ID")+paddedTransferIDStr;
			p_transferVO.setTransferID(returnStr);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PeruClaroUtil[]","","","","Not able to generate Transfer ID:"+e.getMessage());
			returnStr=null;
		}
		return returnStr;
	}

	/**
	 * Method to validate C2S fix line Recharge Request.
	 * Receiver locale of notification msisdn
	 * C2S Fixline recharge message array will be like:: 
	 * Here we are handling the cases with PIN, if we want the cases without PIN,it will be written in Operator specific util
	 * 
	 * FRC MSISDN AMT N_MSISDN PIN 
	 * FRC MSISDN AMT N_MSISDN SEL PIN 
	 * FRC MSISDN AMT N_MSISDN SEL REC PIN 
	 * FRC MSISDN AMT N_MSISDN SEL SEN REC PIN 
	 * 
	 * @param p_con Connection
	 * @param p_c2sTransferVO C2STransferVO
	 * @param p_requestVO RequestVO
	 */
	public void  validateC2SFixLineRechargeRequest(Connection p_con,C2STransferVO p_c2sTransferVO,RequestVO p_requestVO) throws BTSLBaseException
	{
		if(_log.isDebugEnabled())
			_log.debug("validateC2SFixLineRechargeRequest, p_requestVO"+p_requestVO.toString(),"");
		try
		{
			String receiverMsisdn=null;
			String requestedAmt=null;
			String notificationMsisdn=null;
			String [] msgArray=p_requestVO.getRequestMessageArray();
			int msgLength=msgArray.length;
			for(int i=0;i<msgLength;i++)
				if(_log.isDebugEnabled())_log.debug("validateC2SFixLineRechargeRequest", "i="+i+" ,value="+msgArray[i]);
			if(msgLength<5)
				throw new BTSLBaseException(this,"validateC2SFixLineRechargeRequest",PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT);
			ChannelUserVO channelUserVO=(ChannelUserVO)p_c2sTransferVO.getSenderVO();
			UserPhoneVO userPhoneVO=null;
			if(!channelUserVO.isStaffUser())
				userPhoneVO=(UserPhoneVO)channelUserVO.getUserPhoneVO();
			else
				userPhoneVO=(UserPhoneVO)channelUserVO.getStaffUserDetails().getUserPhoneVO();

			switch(msgLength)
			{
			//message Length 5, then message would be FRC_MSISDN_Amt_NotificationMSISDN_PIN
			case 5:
			{
				//check the sender PIN
				if(userPhoneVO.getPinRequired().equals(PretupsI.YES))
				{
					try
					{
						ChannelUserBL.validatePIN(p_con,channelUserVO,msgArray[4]);
					}
					catch(BTSLBaseException be)
					{
						if(be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) ||  (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK))))
							p_con.commit();
						throw be;
					}	
				}					
				ReceiverVO receiverVO=new ReceiverVO();
				//Receiver MSISDN Validation
				receiverMsisdn=msgArray[1];
				PretupsBL.validateMsisdn(p_con,receiverVO,p_c2sTransferVO.getRequestID(),receiverMsisdn);

				//Recharge amount Validation
				requestedAmt=msgArray[2];
				PretupsBL.validateAmount(p_c2sTransferVO,requestedAmt);
				p_c2sTransferVO.setReceiverVO(receiverVO);		

				if(BTSLUtil.isNullString(msgArray[3]))
				{
					ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
					if(serviceSelectorMappingVO!=null)
						p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());									
				}	
				else
					p_requestVO.setReqSelector(msgArray[3]);

				PretupsBL.getSelectorValueFromCode(p_requestVO);
				
				if(BTSLUtil.isNullString(p_requestVO.getReqSelector()))
				{
					ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
					if(serviceSelectorMappingVO!=null)
						p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());									
				}
				p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
				break;
			}

			//message Length 6, then message would be FRC_MSISDN_Amt_NotificationMSISDN_selector_PIN
			case 6:
			{
				if(userPhoneVO.getPinRequired().equals(PretupsI.YES))
				{
					try
					{
						ChannelUserBL.validatePIN(p_con,channelUserVO,msgArray[5]);
					}
					catch(BTSLBaseException be)
					{
						if(be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) ||  (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK))))
							p_con.commit();
						throw be;
					}	
				}						
				ReceiverVO receiverVO=new ReceiverVO();
				receiverMsisdn=msgArray[1];
				PretupsBL.validateMsisdn(p_con,receiverVO,p_c2sTransferVO.getRequestID(),receiverMsisdn);

				//Recharge amount Validation
				requestedAmt=msgArray[2];
				PretupsBL.validateAmount(p_c2sTransferVO,requestedAmt);
				p_c2sTransferVO.setReceiverVO(receiverVO);

				p_requestVO.setNotificationMSISDN(receiverMsisdn);

				if(BTSLUtil.isNullString(msgArray[3]))
				{
					ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
					if(serviceSelectorMappingVO!=null)
						p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());									
				}	
				else
					p_requestVO.setReqSelector(msgArray[3]);

				PretupsBL.getSelectorValueFromCode(p_requestVO);
				
				if(BTSLUtil.isNullString(p_requestVO.getReqSelector()))
				{
					ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
					if(serviceSelectorMappingVO!=null)
						p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());									
				}

				if(BTSLUtil.isNullString(msgArray[4]))
					p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
				else
				{
					int langCode=PretupsBL.getLocaleValueFromCode(p_requestVO,msgArray[4]);
					if(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode))==null)
						throw new BTSLBaseException(this,"validateC2SFixLineRechargeRequest",PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
					p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
				}
				break;
			}

			case 7:
			{
				//message Length 7, then message would be FRC_MSISDN_Amt_NotificationMSISDN_selector_receiverlocale_PIN
				if(userPhoneVO.getPinRequired().equals(PretupsI.YES))
				{
					try
					{
						ChannelUserBL.validatePIN(p_con,channelUserVO,msgArray[6]);
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
				receiverMsisdn=msgArray[1];
				PretupsBL.validateMsisdn(p_con,receiverVO,p_c2sTransferVO.getRequestID(),receiverMsisdn);

				requestedAmt=msgArray[2];
				PretupsBL.validateAmount(p_c2sTransferVO,requestedAmt);
				p_c2sTransferVO.setReceiverVO(receiverVO);
				//	notificationMsisdn=msgArray[3];
				p_requestVO.setNotificationMSISDN(receiverMsisdn);

				if(BTSLUtil.isNullString(msgArray[3]))
				{
					ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
					if(serviceSelectorMappingVO!=null)
						p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());									
				}	
				else
					p_requestVO.setReqSelector(msgArray[3]);

				PretupsBL.getSelectorValueFromCode(p_requestVO);
				//				changed for CRE_INT_CR00029 by ankit Zindal
				if(BTSLUtil.isNullString(p_requestVO.getReqSelector()))
				{
					ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
					if(serviceSelectorMappingVO!=null)
						p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());									
				}
				if(BTSLUtil.isNullString(msgArray[4]))
					p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
				else
				{
					int langCode=PretupsBL.getLocaleValueFromCode(p_requestVO,msgArray[4]);
					if(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode))==null)
						throw new BTSLBaseException(this,"validateC2SFixLineRechargeRequest",PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
					p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
				}
				if(BTSLUtil.isNullString(msgArray[5]))
					p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
				else
				{
					int langCode=PretupsBL.getLocaleValueFromCode(p_requestVO,msgArray[5]);
					if(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode))==null)
						throw new BTSLBaseException(this,"validateC2SFixLineRechargeRequest",PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
					p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
				}
				break;
			}
			case 8:
			{
				//message Length 8, then message would be FRC_MSISDN_Amt_NotificationMSISDN_selector_receiverlocale_senderlocale_PIN				
				if(userPhoneVO.getPinRequired().equals(PretupsI.YES))
				{
					try
					{
						ChannelUserBL.validatePIN(p_con,channelUserVO,msgArray[7]);
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
				receiverMsisdn=msgArray[1];
				PretupsBL.validateMsisdn(p_con,receiverVO,p_c2sTransferVO.getRequestID(),receiverMsisdn);
				requestedAmt=msgArray[2];
				PretupsBL.validateAmount(p_c2sTransferVO,requestedAmt);
				p_c2sTransferVO.setReceiverVO(receiverVO);

				notificationMsisdn=msgArray[3];
				validateNotificationMsisdn(p_requestVO,p_requestVO.getRequestIDStr(),notificationMsisdn);

				if(BTSLUtil.isNullString(msgArray[4]))
				{
					ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
					if(serviceSelectorMappingVO!=null)
						p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());									
				}
				else
					p_requestVO.setReqSelector(msgArray[4]);

				PretupsBL.getSelectorValueFromCode(p_requestVO);
				//changed for CRE_INT_CR00029 by ankit Zindal
				if(BTSLUtil.isNullString(p_requestVO.getReqSelector()))
				{
					ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
					if(serviceSelectorMappingVO!=null)
						p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());									
				}
				//For handling of sender locale
				if(BTSLUtil.isNullString(msgArray[5]))
					p_requestVO.setSenderLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
				else
				{
					int langCode=PretupsBL.getLocaleValueFromCode(p_requestVO,msgArray[5]);
					p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
					p_c2sTransferVO.setLocale(p_requestVO.getSenderLocale());
					p_c2sTransferVO.setLanguage(p_c2sTransferVO.getLocale().getLanguage());
					p_c2sTransferVO.setCountry(p_c2sTransferVO.getLocale().getCountry());
				}

				if(BTSLUtil.isNullString(msgArray[6]))
					p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
				else
				{
					int langCode=PretupsBL.getLocaleValueFromCode(p_requestVO,msgArray[6]);
					if(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode))==null)
						throw new BTSLBaseException(this,"validateC2SFixLineRechargeRequest",PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
					p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
				}
				break;
			}
			default:
				throw new BTSLBaseException(this,"validateC2SFixLineRechargeRequest",PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT,0,new String[]{p_requestVO.getActualMessageFormat()},null);
			}

		}
		catch(BTSLBaseException be)
		{
			throw be;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			_log.error("validateC2SFixLineRechargeRequest","  Exception while validating user message :"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PretupsBL[validateC2SFixLineRechargeRequest]","","","","Exception while validating user message" +" ,getting Exception="+e.getMessage());
			throw new BTSLBaseException(this,"validateC2SFixLineRechargeRequest",PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if(_log.isDebugEnabled()) _log.debug("validateC2SFixLineRechargeRequest","Exiting ");


	}

	/**
	 * to validate the notification msisdn given by the customer
	 * @param p_RequestVO
	 * @param p_requestID
	 * @param p_gifterMsisdn
	 * @throws BTSLBaseException
	 */
	public void validateNotificationMsisdn(RequestVO p_requestVO,String p_requestID,String p_notificationMsisdn) throws BTSLBaseException
	{
		if(_log.isDebugEnabled()) _log.debug("validateNotificationMsisdn",p_requestID,"Entered for p_notificationMsisdn= "+p_notificationMsisdn);
		String[] strArr=null;
		try
		{
			if (BTSLUtil.isNullString(p_notificationMsisdn))
				throw new BTSLBaseException(this,"validateNotificationMsisdn",PretupsErrorCodesI.C2S_NOTIFICATION_MSISDN_BLANK);
			p_notificationMsisdn=PretupsBL.getFilteredMSISDN(p_notificationMsisdn);
			if((p_notificationMsisdn.length() < ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH))).intValue() || p_notificationMsisdn.length() > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue()))
			{
				if(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH))).intValue()!=((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue())
				{
					strArr=new String[]{p_notificationMsisdn,String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH))).intValue()),String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue())};
					throw new BTSLBaseException(this,"validateNotificationMsisdn",PretupsErrorCodesI.C2S_NOTIFICATION_MSISDN_NOTINRANGE,0,strArr,null);
				}
				else
				{
					strArr=new String[]{p_notificationMsisdn,String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH))).intValue())};
					throw new BTSLBaseException(this,"validateNotificationMsisdn",PretupsErrorCodesI.C2S_NOTIFICATION_MSISDN_LEN_NOTSAME,0,strArr,null);
				}
			}
			try
			{
				long lng=Long.parseLong(p_notificationMsisdn);
			}
			catch(Exception e)
			{
				strArr=new String[]{p_notificationMsisdn,String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue())};
				throw new BTSLBaseException(this,"validateNotificationMsisdn",PretupsErrorCodesI.C2S_NOTIFICATION_MSISDN_NOTNUMERIC,0,strArr,null);
			}
			p_requestVO.setNotificationMSISDN(p_notificationMsisdn);

		}
		catch(BTSLBaseException be)
		{
			throw be;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			_log.error("validateNotificationMsisdn","  Exception while validating after msisdn :"+e.getMessage());
			throw new BTSLBaseException(this,"validateNotificationMsisdn",PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}	
		if(_log.isDebugEnabled()) _log.debug("validateNotificationMsisdn",p_requestID,"Exiting for p_notificationMsisdn= "+p_notificationMsisdn);
	}

	/**
	 * Method that will validate the user message sent
	 * @param p_con
	 * @param p_c2sTransferVO
	 * @param p_requestVO
	 * @throws BTSLBaseException
	 * @see com.btsl.pretups.util.OperatorUtilI#validateCollectionBillpaymentRequest(Connection, C2STransferVO, RequestVO)
	 */
	public void validateCollectionBillpaymentRequest(Connection p_con,C2STransferVO p_c2sTransferVO,RequestVO p_requestVO) throws BTSLBaseException
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
			if(_log.isDebugEnabled()) _log.debug("validateCollectionBillpaymentRequest","messageLen: "+messageLen);
			for(int i=0;i<messageLen;i++)
			{
				if(_log.isDebugEnabled()) _log.debug("validateCollectionBillpaymentRequest","i: "+i+" value: "+p_requestArr[i]);
			}
			switch(messageLen)
			{
			case 5:
			{
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
					catch(Exception e)
					{
						e.printStackTrace();
						p_con.rollback();
						throw new BTSLBaseException(this,"validateC2SReverrsalRequest",PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN);
						
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
				PretupsBL.validateAmount(p_c2sTransferVO,requestAmtStr);
				p_c2sTransferVO.setReceiverVO(receiverVO);
				if(BTSLUtil.isNullString(p_requestArr[3]))
				{
					if("en".equalsIgnoreCase(p_requestVO.getLocale().getLanguage()))
					{
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
					ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
					if(serviceSelectorMappingVO!=null)
						p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());									
				}

				p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
				
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
					catch(Exception e)
					{
						e.printStackTrace();
						p_con.rollback();
						throw new BTSLBaseException(this,"validateC2SReverrsalRequest",PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN);
						
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
				PretupsBL.validateAmount(p_c2sTransferVO,requestAmtStr);
				p_c2sTransferVO.setReceiverVO(receiverVO);
				if(BTSLUtil.isNullString(p_requestArr[3]))
				{
					if("en".equalsIgnoreCase(p_requestVO.getLocale().getLanguage()))
					{
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
						throw new BTSLBaseException(this,"validateCollectionBillpaymentRequest",PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
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
					catch(Exception e)
					{
						e.printStackTrace();
						p_con.rollback();
						throw new BTSLBaseException(this,"validateC2SReverrsalRequest",PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN);
						
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
				PretupsBL.validateAmount(p_c2sTransferVO,requestAmtStr);
				p_c2sTransferVO.setReceiverVO(receiverVO);
				if(BTSLUtil.isNullString(p_requestArr[3]))
				{
					if("en".equalsIgnoreCase(p_requestVO.getLocale().getLanguage()))
					{
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
						throw new BTSLBaseException(this,"validateCollectionBillpaymentRequest",PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
					p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
				}
				break;
			}
			case 8:
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
					catch(Exception e)
					{
						e.printStackTrace();
						p_con.rollback();
						throw new BTSLBaseException(this,"validateC2SReverrsalRequest",PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN);
						
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
				PretupsBL.validateAmount(p_c2sTransferVO,requestAmtStr);
				p_c2sTransferVO.setReceiverVO(receiverVO);
				if(BTSLUtil.isNullString(p_requestArr[3]))
				{
					if("en".equalsIgnoreCase(p_requestVO.getLocale().getLanguage()))
					{
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
						throw new BTSLBaseException(this,"validateCollectionBillpaymentRequest",PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
					p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
				}

				String invoiceno = p_requestArr[7];
				if(BTSLUtil.isNullString(p_requestArr[7]))
					throw new BTSLBaseException(this,"validateCollectionBillpaymentRequest",PretupsErrorCodesI.C2S_BILLPAYMNET_INVOICE_NO_REO);
				else
					p_requestVO.setInvoiceno(invoiceno);
				break;
			}
			default:
				throw new BTSLBaseException(this,"validateCollectionBillpaymentRequest",PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT,0,new String[]{p_requestVO.getActualMessageFormat()},null);
			}


		}
		catch(BTSLBaseException be)
		{
			be.printStackTrace();
			_log.error("validateCollectionBillpaymentRequest","  Exception while validating user message :"+be.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PretupsBL[validateCollectionBillpaymentRequest]","","","","Exception while validating user message" +" ,getting Exception="+be.getMessage());
			throw new BTSLBaseException(this,"validateCollectionBillpaymentRequest",be.getMessage(),be.getArgs());

		}
		catch(Exception e)
		{
			e.printStackTrace();
			_log.error("validateCollectionBillpaymentRequest","  Exception while validating user message :"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PretupsBL[validateCollectionBillpaymentRequest]","","","","Exception while validating user message" +" ,getting Exception="+e.getMessage());
			throw new BTSLBaseException(this,"validateCollectionBillpaymentRequest",PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if(_log.isDebugEnabled()) _log.debug("validateCollectionBillpaymentRequest","Exiting ");
	}

	/**
	 * Method that will validate the user message sent
	 * @param p_con
	 * @param p_c2sTransferVO
	 * @param p_requestVO
	 * @throws BTSLBaseException
	 * @see com.btsl.pretups.util.OperatorUtilI#validateCollectionBillpaymentRequest(Connection, C2STransferVO, RequestVO)
	 */
	public void validateC2SReverrsalRequest(Connection p_con,C2STransferVO p_c2sTransferVO,RequestVO p_requestVO) throws BTSLBaseException
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
			if(_log.isDebugEnabled()) _log.debug("validateC2SReverrsalRequest","messageLen: "+messageLen);
			for(int i=0;i<messageLen;i++)
			{
				if(_log.isDebugEnabled()) _log.debug("validateC2SReverrsalRequest","i: "+i+" value: "+p_requestArr[i]);
			}
			switch(messageLen)
			{
			case 5:
			{
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
				//PretupsBL.validateAmount(p_c2sTransferVO,requestAmtStr);
				p_c2sTransferVO.setReceiverVO(receiverVO);		
				//p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE));
				//Changed on 27/05/07 for Service Type selector Mapping
				ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
				if(serviceSelectorMappingVO!=null)
					p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());									
				p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));

				String oldTxnId = p_requestArr[4];
				if(BTSLUtil.isNullString(oldTxnId)){
					throw new BTSLBaseException(this,"validateC2SReverrsalRequest",PretupsErrorCodesI.C2S_INVALID_TXN_ID_REVERSAL);
				}else{
					p_c2sTransferVO.setOldTxnId(oldTxnId.trim());
				}
				break;
			}

			case 6:
			{
//				Do the 000 check Default PIN 
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
//				Change for the SID logic
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
				//PretupsBL.validateAmount(p_c2sTransferVO,requestAmtStr);
				p_c2sTransferVO.setReceiverVO(receiverVO);
				
				if(BTSLUtil.isNullString(p_requestArr[3]))
				{
					if("en".equalsIgnoreCase(p_requestVO.getLocale().getLanguage()))
					{
						//p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE));
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
//				changed for CRE_INT_CR00029 by ankit Zindal
				if(BTSLUtil.isNullString(p_requestVO.getReqSelector()))
				{
					//p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE));
					//Changed on 27/05/07 for Service Type selector Mapping
					ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
					if(serviceSelectorMappingVO!=null)
						p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());									
				}

				p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
				
				String oldTxnId = p_requestArr[5];
				if(BTSLUtil.isNullString(oldTxnId)){
					throw new BTSLBaseException(this,"validateC2SReverrsalRequest",PretupsErrorCodesI.C2S_INVALID_TXN_ID_REVERSAL);
				}else{
					p_c2sTransferVO.setOldTxnId(oldTxnId.trim());
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
//				Change for the SID logic
				p_requestVO.setSid(custMsisdn);
				receiverVO.setSid(custMsisdn);
				PrivateRchrgVO prvo=null;
				if((prvo=getPrivateRechargeDetails(p_con,custMsisdn))!=null)
				{
					p_c2sTransferVO.setSubscriberSID(custMsisdn);
					custMsisdn=prvo.getMsisdn();						
				}
				PretupsBL.validateMsisdn(p_con,receiverVO,p_c2sTransferVO.getRequestID(),custMsisdn);

				requestAmtStr=p_requestArr[2];
				
				p_c2sTransferVO.setReceiverVO(receiverVO);

				
				//Recharge amount Validation
				//	requestAmtStr=p_requestArr[2];
				//PretupsBL.validateAmount(p_c2sTransferVO,requestAmtStr);
				if(BTSLUtil.isNullString(p_requestArr[3]))
				{
					if("en".equalsIgnoreCase(p_requestVO.getLocale().getLanguage()))
					{
						//p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE));
						//Changed on 27/05/07 for Service Type selector Mapping
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
						throw new BTSLBaseException(this,"validateC2SReverrsalRequest",PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
					p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
				}
			
				String oldTxnId = p_requestArr[6];
				if(BTSLUtil.isNullString(oldTxnId)){
					throw new BTSLBaseException(this,"validateC2SReverrsalRequest",PretupsErrorCodesI.C2S_INVALID_TXN_ID_REVERSAL);
				}else{
					p_c2sTransferVO.setOldTxnId(oldTxnId.trim());
				}
				break;
			}
			case 8:
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
//				Change for the SID logic
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
				//PretupsBL.validateAmount(p_c2sTransferVO,requestAmtStr);
				p_c2sTransferVO.setReceiverVO(receiverVO);
				if(BTSLUtil.isNullString(p_requestArr[3]))
				{
					if("en".equalsIgnoreCase(p_requestVO.getLocale().getLanguage()))
					{
						//p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE));
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
					//p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE));
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
						throw new BTSLBaseException(this,"validateC2SReverrsalRequest",PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
					p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
				}
				String oldTxnId = p_requestArr[7];
				if(BTSLUtil.isNullString(oldTxnId)){
					throw new BTSLBaseException(this,"validateC2SReverrsalRequest",PretupsErrorCodesI.C2S_INVALID_TXN_ID_REVERSAL);
				}else{
					p_c2sTransferVO.setOldTxnId(oldTxnId.trim());
				}
				break;
			}
			default:
				throw new BTSLBaseException(this,"validateC2SReverrsalRequest",PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT,0,new String[]{p_requestVO.getActualMessageFormat()},null);
			}

		}
		catch(BTSLBaseException be)
		{
			be.printStackTrace();
			_log.error("validateC2SReverrsalRequest","  Exception while validating user message :"+be.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PretupsBL[validateC2SReverrsalRequest]","","","","Exception while validating user message" +" ,getting Exception="+be.getMessage());
			throw new BTSLBaseException(this,"validateC2SReverrsalRequest",be.getMessage());

		}
		catch(Exception e)
		{
			e.printStackTrace();
			_log.error("validateC2SReverrsalRequest","  Exception while validating user message :"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PretupsBL[validateC2SReverrsalRequest]","","","","Exception while validating user message" +" ,getting Exception="+e.getMessage());
			throw new BTSLBaseException(this,"validateC2SReverrsalRequest",PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if(_log.isDebugEnabled()) _log.debug("validateC2SReverrsalRequest","Exiting ");
	}



	
	/**
	 * Method that will validate the old txn id
	 * 
	 */
	public void validateReversalOldTxnId(Connection p_con,C2STransferVO p_c2sTransferVO,RequestVO p_requestVO) throws BTSLBaseException
	{
		if(_log.isDebugEnabled()) _log.debug("validateReversalOldTxnId","Entered Old Txn Id=" + p_c2sTransferVO.getOldTxnId());

		try{
			C2STransferDAO channelTransferDAO=new C2STransferDAO();

			ReceiverVO _receiverVO=(ReceiverVO)p_c2sTransferVO.getReceiverVO();

			C2STransferVO transferVO=channelTransferDAO.loadOldTxnIDForReversal(p_con,p_c2sTransferVO,p_requestVO,PretupsI.COLLECTION_BILLPAYMENT);

			if(transferVO==null)
			{
				throw new BTSLBaseException("PeruClaroUtil","validateReversalOldTxnId",PretupsErrorCodesI.C2S_INVALID_TXN_ID_REVERSAL);
			}
			if(!transferVO.getSenderMsisdn().equalsIgnoreCase(p_c2sTransferVO.getSenderMsisdn()))
			{
				throw new BTSLBaseException("PeruClaroUtil","validateReversalOldTxnId",PretupsErrorCodesI.C2S_INVALID_SENDERMSISDN_REVERSAL);
			}
			if(!transferVO.getReceiverMsisdn().equalsIgnoreCase(_receiverVO.getMsisdn()))
			{
				throw new BTSLBaseException("PeruClaroUtil","validateReversalOldTxnId",PretupsErrorCodesI.C2S_INVALID_RECEIVERMSISDN_REVERSAL);
			}
			if(transferVO.getTransferStatus().equalsIgnoreCase(PretupsErrorCodesI.TXN_STATUS_FAIL))
			{
				throw new BTSLBaseException("PeruClaroUtil","validateReversalOldTxnId",PretupsErrorCodesI.C2S_PREVIOUS_FAIL_REVERSAL);
			}
			if(transferVO.getTransferStatus().equalsIgnoreCase(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS))
			{
				throw new BTSLBaseException("PeruClaroUtil","validateReversalOldTxnId",PretupsErrorCodesI.C2S_PREVIOUS_AMBIGUOUS_REVERSAL);
			}
			//Check Whether reversal alreday done or not
			if(!BTSLUtil.isNullString(transferVO.getReverseTransferID()))
			{
				throw new BTSLBaseException("PeruClaroUtil","validateReversalOldTxnId",PretupsErrorCodesI.C2S_REVERSAL_ALREADY_DONE);
			}
			
			//In Minutes
			if(SystemPreferences.RVE_C2S_TRN_EXPIRY < (p_requestVO.getRequestStartTime()- transferVO.getTransferDateTime().getTime())/(1000*60))
			{
				throw new BTSLBaseException("PeruClaroUtil","validateReversalOldTxnId",PretupsErrorCodesI.C2S_WRONG_TRANSACTION_REVERSAL_TIMEOUT);
			}
			
			copyOldTxnToNew(p_c2sTransferVO,transferVO);

		}
		catch(BTSLBaseException be)
		{
			be.printStackTrace();
			_log.error("validateReversalOldTxnId","  Exception while validating user message :"+be.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PretupsBL[validateReversalOldTxnId]","","","","Exception while validating user message" +" ,getting Exception="+be.getMessage());
			throw new BTSLBaseException(this,"validateReversalOldTxnId",be.getMessage());

		}
		catch(Exception e)
		{
			e.printStackTrace();
			_log.error("validateReversalOldTxnId","  Exception while validating user message :"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PretupsBL[validateReversalOldTxnId]","","","","Exception while validating user message" +" ,getting Exception="+e.getMessage());
			throw new BTSLBaseException(this,"validateReversalOldTxnId",PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if(_log.isDebugEnabled()) _log.debug("validateReversalOldTxnId","Exiting ");
	}

	/**
	 * Copy Property from old txn to new txn object
	 * @param oldTransferVO
	 * @param newTransferVO2
	 * @throws BTSLBaseException
	 */
	private void copyOldTxnToNew(C2STransferVO oldTransferVO, C2STransferVO newTransferVO2) throws BTSLBaseException {
		try{

			oldTransferVO.setReceiverMsisdn(newTransferVO2.getReceiverMsisdn());
			oldTransferVO.setRequestedAmount(newTransferVO2.getRequestedAmount());
			oldTransferVO.setQuantity(newTransferVO2.getQuantity());
			oldTransferVO.setTransferValue(newTransferVO2.getTransferValue());
			oldTransferVO.setErrorCode(newTransferVO2.getErrorCode());
			oldTransferVO.setReceiverValidity(newTransferVO2.getReceiverValidity());
			oldTransferVO.setReceiverTransferValue(newTransferVO2.getReceiverTransferValue());
			oldTransferVO.setInfo1(newTransferVO2.getInfo1());
			oldTransferVO.setInfo2(newTransferVO2.getInfo2());
			oldTransferVO.setInfo3(newTransferVO2.getInfo3());
			oldTransferVO.setOldtransferDateTime(newTransferVO2.getTransferDateTime());
			oldTransferVO.setReceiverNetworkCode(newTransferVO2.getReceiverNetworkCode());
			oldTransferVO.setCardGroupCode(newTransferVO2.getCardGroupCode());
			oldTransferVO.setCardGroupID(newTransferVO2.getCardGroupID());
			oldTransferVO.setCardGroupSetID(newTransferVO2.getCardGroupSetID());
			oldTransferVO.setVersion(newTransferVO2.getVersion());
			oldTransferVO.setDifferentialApplicable(newTransferVO2.getDifferentialApplicable());
			oldTransferVO.setDifferentialGiven(newTransferVO2.getDifferentialGiven());
			oldTransferVO.setReceiverTax1Type(newTransferVO2.getReceiverTax1Type());
			oldTransferVO.setReceiverTax1Rate(newTransferVO2.getReceiverTax1Rate());
			oldTransferVO.setReceiverTax1Value(newTransferVO2.getReceiverTax1Value());
			oldTransferVO.setReceiverTax2Type(newTransferVO2.getReceiverTax2Type());
			oldTransferVO.setReceiverTax2Rate(newTransferVO2.getReceiverTax2Rate());
			oldTransferVO.setReceiverTax2Value(newTransferVO2.getReceiverTax2Value());
			oldTransferVO.setReceiverBonusValue(newTransferVO2.getReceiverBonusValue());
			oldTransferVO.setReceiverGracePeriod(newTransferVO2.getReceiverGracePeriod());
			oldTransferVO.setReceiverBonusValidity(newTransferVO2.getReceiverBonusValidity());
			oldTransferVO.setReceiverValPeriodType(newTransferVO2.getReceiverValPeriodType());
			oldTransferVO.setSenderTransferItemVO(newTransferVO2.getSenderTransferItemVO());
			oldTransferVO.setReverseTransferID(newTransferVO2.getReverseTransferID());
			oldTransferVO.setSelectorCode(newTransferVO2.getSelectorCode());
			oldTransferVO.setSubService(newTransferVO2.getSubService());
			oldTransferVO.setReceiverAccessFee(newTransferVO2.getReceiverAccessFee());
			C2STransferItemVO senderVO=new C2STransferItemVO();
			C2STransferItemVO receiverVO=new C2STransferItemVO();
			
			
			 for (int l = 0,m=newTransferVO2.getTransferItemList().size(); l < m; l++){
                 Object obj=newTransferVO2.getTransferItemList().get(l);
                 if(!(obj instanceof C2STransferItemVO))
                       continue;
               
                 C2STransferItemVO c2STransferItemVO=(C2STransferItemVO)obj;
                 if(c2STransferItemVO.getSNo()==1)
                       senderVO=c2STransferItemVO;
                 else if(c2STransferItemVO.getSNo()==2)
                       receiverVO=c2STransferItemVO;
           }
			ArrayList receiverarrayList=new ArrayList();
			receiverarrayList.add(receiverVO);
			
			ArrayList senderarrayList=new ArrayList();
			senderarrayList.add(senderVO);
			 
			oldTransferVO.setTransferItemList(receiverarrayList);
			oldTransferVO.setTransferItemList(senderarrayList);
			
		}catch (Exception e) {
			e.printStackTrace();
			_log.error("copyOldTxnToNew","  Exception while validating user message :"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PeruClaroUtil[copyOldTxnToNew]","","","","Exception while Copy Property from db " +" ,getting Exception="+e.getMessage());
			throw new BTSLBaseException(this,"copyOldTxnToNew",PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}

	}
	
	public void validateCardGroupDetails(String p_startRange,String p_endRange,String p_subService)  throws Exception
	{
		try
		{
			//This implementation is comented because not needed at this time. Will be implemented for any operator.
			if(p_subService.split(":")[1].equals((String.valueOf(PretupsI.CHNL_SELECTOR_VG_VALUE))))
			{
				/*if(Double.parseDouble(p_startRange)!=Double.parseDouble( p_endRange))
					throw new BTSLBaseException(this,"validateCardGroupDetails","cardgroup.addc2scardgroup.error.invalidstartandendrange");
				 */
				if(_log.isDebugEnabled()) _log.debug("validateCardGroupDetails"," Overwright the default implementation ");
			}
		}
		catch(Exception e){throw e;}
	}
	
	public void setCalculatedCardGroupValues(String p_subService,CardGroupDetailsVO p_cardGroupDetailVO,TransferVO p_transferVO) throws Exception
	{

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

			/*
			int amountDeducted;
			if(transferItemVO.isNumberBackAllowed())
			{
			    amountDeducted= transferItemVO.getAmountDeducted();
			    transferValue=transferValue-amountDeducted;
				  if(!(transferValue>0))
				      throw new BTSLBaseException(this,"setCalculatedCardGroupValues",PretupsErrorCodesI.TRANSFER_VALUE_IS_NOT_VALID);
				  p_cardGroupDetailVO.setTransferValue(transferValue);
				  p_transferVO.setReceiverAccessFee(p_transferVO.getReceiverAccessFee() + amountDeducted);
			    }
			}
			 */
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
		catch(Exception e){throw e;}
	}

	public static String getDateTimeStringFromDate(Date date)  throws ParseException
	{
		String format="yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat (format);
		sdf.setLenient(false); // this is required else it will convert
		return sdf.format(date);
	}
	
	public String c2sTransferTDRLog(C2STransferVO p_c2sTransferVO,TransferItemVO p_senderTransferItemVO,TransferItemVO p_receiverTransferItemVO)
	{
		String returnStr=null;
		try
		{
			if((p_c2sTransferVO.getSourceType().equalsIgnoreCase(PretupsI.GATEWAY_TYPE_USSD) || p_c2sTransferVO.getSourceType().equalsIgnoreCase("BROWSER"))&& p_c2sTransferVO.getTransferStatus()!=null && p_c2sTransferVO.getTransferStatus().equalsIgnoreCase(PretupsI.TXN_STATUS_SUCCESS))
			{
			StringBuffer strBuff = new StringBuffer();
				
			strBuff.append(getDateTimeStringFromDate(p_c2sTransferVO.getTransferDateTime()) );
            
			if(BTSLUtil.isNullString(p_c2sTransferVO.getSenderMsisdn()))
				strBuff.append(",");
			else
				strBuff.append(","+getOperatorFilteredMSISDN(getSystemFilteredMSISDN(p_c2sTransferVO.getSenderMsisdn())));
			
			strBuff.append(","+p_c2sTransferVO.getRequestedAmount());
			
			if(BTSLUtil.isNullString(p_c2sTransferVO.getCellId()))
				strBuff.append(",");
			else
				strBuff.append(","+p_c2sTransferVO.getCellId());
			
			if(!BTSLUtil.isNullString(p_c2sTransferVO.getSwitchId()))
				strBuff.append(" - "+p_c2sTransferVO.getSwitchId());
					
			
			if(BTSLUtil.isNullString(p_c2sTransferVO.getProductCode()))
				strBuff.append(",");
			else
				strBuff.append(","+p_c2sTransferVO.getProductCode());
			
			returnStr=strBuff.toString();
			}
		
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PeruClaroUtil[]","","","","Not able to generate c2sTransferTDRLog:"+e.getMessage());
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
			
			PretupsBL.validatePromoBonus(p_c2sTransferVO , p_requestVO.getPromoBonus());
	         
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
				PretupsBL.validateAmount(p_c2sTransferVO,requestAmtStr);
				p_c2sTransferVO.setReceiverVO(receiverVO);		
				//p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE));
				//Changed on 27/05/07 for Service Type selector Mapping
				ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
				if(serviceSelectorMappingVO!=null)
					p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());									
				p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
				break;
			}

			case 5:
			{
//				Do the 000 check Default PIN 
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
//				Change for the SID logic
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
					if("en".equalsIgnoreCase(p_requestVO.getLocale().getLanguage()))
					{
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
					ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
					if(serviceSelectorMappingVO!=null)
						p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());									
				}

				p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
					
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
//				Change for the SID logic
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
					if("en".equalsIgnoreCase(p_requestVO.getLocale().getLanguage()))
					{
						//p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE));
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
//				changed for CRE_INT_CR00029 by ankit Zindal
				if(BTSLUtil.isNullString(p_requestVO.getReqSelector()))
				{
					//p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE));
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
				custMsisdn=p_requestArr[1];
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
					if("en".equalsIgnoreCase(p_requestVO.getLocale().getLanguage()))
					{
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
	 * This method used for pin validation.
	 * While creating or modifying the user PIN This method will be used.
	 * Method validatePIN.
	 * @author akanksha.gupta
	 * @created on 19/07/07
	 * @param p_pin String
	 * @return HashMap
	 */
	public HashMap pinValidate(String p_pin)
	{
		_log.debug("validatePIN","Entered, PIN= "+p_pin);
		HashMap messageMap=new HashMap();
		 int asciiCode = 0;
	        boolean isConSeq = false;
	        int previousAsciiCode = 0;
	        int numSeqcount = 0;
			
		String defaultPin = BTSLUtil.getDefaultPasswordNumeric(p_pin);
		if(defaultPin.equals(p_pin))
			return messageMap;

		defaultPin = BTSLUtil.getDefaultPasswordText(p_pin);
		if(defaultPin.equals(p_pin))
			return messageMap;

		if(!BTSLUtil.isNumeric(p_pin))
			messageMap.put("operatorutil.validatepin.error.pinnotnumeric",null);
		if (p_pin.length() < ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_SMS_PIN_LENGTH))).intValue()  || p_pin.length() > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_SMS_PIN_LENGTH))).intValue())
		{
			String[] args={String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_SMS_PIN_LENGTH))).intValue()),String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_SMS_PIN_LENGTH))).intValue())};
			messageMap.put("operatorutil.validatepin.error.smspinlenerr", args);
		}
		int result=BTSLUtil.isSMSPinValid(p_pin);
		if(result==-1)
			messageMap.put("operatorutil.validatepin.error.pinsamedigit",null); 
		else if(result==1)
			messageMap.put("operatorutil.validatepin.error.pinconsecutive",null);
		boolean repeatedCharacter = false;
		 Map<Character, Integer> dupMap = new HashMap<Character, Integer>(); 
	        char[] pinCharArr = p_pin.toCharArray();
	        for(Character ch:pinCharArr){
	            if(dupMap.containsKey(ch)){
	                dupMap.put(ch, dupMap.get(ch)+1);
	            } else {
	                dupMap.put(ch, 1);
	            }
	        }
	        Set<Character> keys = dupMap.keySet();
	        for(Character ch:keys){
	            if(dupMap.get(ch) > Integer.parseInt(Constants.getProperty("ALLOWED_CHARACTER_OCCURANCE"))){
	            	repeatedCharacter =true;
	            	break;
	            }
	        }
	        for (int i = 0; i < pinCharArr.length; i++) {
	            asciiCode = pinCharArr[i];
	            if ((previousAsciiCode + 1) == asciiCode) {
	                numSeqcount++;
	                if (numSeqcount >= 1) {
	                    isConSeq = true;
	                    break;
	                }
	            } else {
	                numSeqcount = 0;
	            }
	            previousAsciiCode = asciiCode;
	        }
	    if(isConSeq)  
			messageMap.put("operatorutil.validatepin.error.pinconsecutive",null);
	    if(repeatedCharacter)
	    	messageMap.put("operatorutil.validatepin.error.pinsamedigit",null);
		if(_log.isDebugEnabled()) _log.debug("validatePIN","Exiting messageMap.size()="+messageMap.size());
		return messageMap;
	}
	
	
	public String generateRandomPin()
	{
		if(_log.isDebugEnabled()) _log.debug("generateRandomPin ","Entered in to OperatorUtil");
		String returnStr=null;
		try
		{
			boolean flag=false;
			int count=0;
			do {
			final String DIGITS= "0123456789";
			final String PRINTABLE_CHARACTERS = DIGITS;
			int minLength=SystemPreferences.C2S_PIN_MIN_LENGTH;
			while (true){   
				returnStr=BTSLUtil.generateRandomPIN(PRINTABLE_CHARACTERS,minLength);
				int  result=BTSLUtil.isSMSPinValid(returnStr);
				if(result==-1) 
					continue;
				else if(result==1)
					continue;
				else if(!BTSLUtil.validateSMSPinConsecutive(returnStr))
					continue;
				else 
					break;	            	            
			}
			try{
				validatePIN(returnStr);
				flag=true;
			}catch (Exception e) {
				Thread.sleep(500);
				if(_log.isDebugEnabled()) _log.debug("generateRandomPin","Regenerate the PIN OperatorUtil = "+returnStr);
				if(count>5)
				{
					flag=true;
				}
				count++;
			}
			} while (!flag);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorUtil[]","","","","Not able to generate MVD Transfer ID:"+e.getMessage());
			returnStr=null;
		}
		if(_log.isDebugEnabled()) _log.debug("generateRandomPin","Exiting from OperatorUtil = "+returnStr);
		return returnStr;
	}
	
	/**
	 * Method that will validate the user message sent
	 * @param p_con
	 * @param p_c2sTransferVO
	 * @param p_requestVO
	 * @throws BTSLBaseException
	 * @see com.btsl.pretups.util.OperatorUtilI#validateC2SRechargeRequest(Connection, C2STransferVO, RequestVO)
	 */
	public void validateOLORequestWithoutAmount(Connection p_con,C2STransferVO p_c2sTransferVO,RequestVO p_requestVO) throws BTSLBaseException{

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
			if(_log.isDebugEnabled()) _log.debug("validateOLORequestWithoutAmount","messageLen: "+messageLen);
			for(int i=0;i<messageLen;i++)
			{
				if(_log.isDebugEnabled()) _log.debug("validateOLORequestWithoutAmount","i: "+i+" value: "+p_requestArr[i]);
			}
			switch(messageLen)
			{
			case 8:

				//if((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES) && !PretupsI.DEFAULT_C2S_PIN.equals(BTSLUtil.decryptText((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getSmsPin())))
				if(userPhoneVO.getPinRequired().equals(PretupsI.YES))
				{
					try
					{
						ChannelUserBL.validatePIN(p_con,channelUserVO,p_requestArr[7]);
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
//				Change for the SID logic
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
				try{
					if(!BTSLUtil.isNullString(requestAmtStr))
					{
						if(Integer.parseInt(requestAmtStr.trim())>0)
							PretupsBL.validateAmount(p_c2sTransferVO,requestAmtStr);
					}
				}catch (Exception e) {
				}
				p_c2sTransferVO.setReceiverVO(receiverVO);
				
				p_requestVO.getRequestMap().put("PLANID",p_requestArr[3]);
				
				if(BTSLUtil.isNullString(p_requestArr[4]))
				{
					if("en".equalsIgnoreCase(p_requestVO.getLocale().getLanguage()))
					{
						//p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE));
						//Changed on 27/05/07 for Service Type selector Mapping
						ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
						if(serviceSelectorMappingVO!=null)
							p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());									
					}
					/*						else
								p_requestVO.setReqSelector((Constants.getProperty("CVG_UNICODE_"+p_requestVO.getLocale().getLanguage().toUpperCase())));
					 */					}
				else
					p_requestVO.setReqSelector(p_requestArr[4]);

				PretupsBL.getSelectorValueFromCode(p_requestVO);
				//changed for CRE_INT_CR00029 by ankit Zindal
				if(BTSLUtil.isNullString(p_requestVO.getReqSelector()))
				{
					//p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE));
					//Changed on 27/05/07 for Service Type selector Mapping
					ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
					if(serviceSelectorMappingVO!=null)
						p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());									
				}
				//For handling of sender locale
				if(BTSLUtil.isNullString(p_requestArr[5]))
					p_requestVO.setSenderLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
				else
				{
					int langCode=PretupsBL.getLocaleValueFromCode(p_requestVO,p_requestArr[5]);
					/*						if(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode))==null)
								throw new BTSLBaseException(this,"validateOLORequestWithoutAmount",PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
					 */						p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
					 //ChangeID=LOCALEMASTER
					 //Sender locale has to be overwritten in transferVO also.
					 p_c2sTransferVO.setLocale(p_requestVO.getSenderLocale());
					 p_c2sTransferVO.setLanguage(p_c2sTransferVO.getLocale().getLanguage());
					 p_c2sTransferVO.setCountry(p_c2sTransferVO.getLocale().getCountry());
				}
				if (_log.isDebugEnabled()) 
					_log.debug(this,"sender locale: ="+p_requestVO.getSenderLocale());

				if(BTSLUtil.isNullString(p_requestArr[6]))
					p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
				else
				{
					int langCode=PretupsBL.getLocaleValueFromCode(p_requestVO,p_requestArr[6]);
					if(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode))==null)
						throw new BTSLBaseException(this,"validateOLORequestWithoutAmount",PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
					p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
				}
				break;
			
			default:
				throw new BTSLBaseException(this,"validateOLORequestWithoutAmount",PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT,0,new String[]{p_requestVO.getActualMessageFormat()},null);
			}
		}
		catch(BTSLBaseException be)
		{
			throw be;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			_log.error("validateOLORequestWithoutAmount","  Exception while validating user message :"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PretupsBL[validateOLORequestWithoutAmount]","","","","Exception while validating user message" +" ,getting Exception="+e.getMessage());
			throw new BTSLBaseException(this,"validateOLORequestWithoutAmount",PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if(_log.isDebugEnabled()) _log.debug("validateOLORequestWithoutAmount","Exiting ");
	
	}
	
	/**
	 * This method used to validate the SID exist or not in the database
	 * If exist it will return the details
	 */
	public PrivateRchrgVO getPrivateRechargeDetails(Connection p_con,String p_sid) throws BTSLBaseException
	{
		if(_log.isDebugEnabled()) _log.debug("getPrivateRechargeDetails","Entering   p_sid="+p_sid);
		PrivateRchrgVO prvo=null;
		try
		{
			PrivateRchrgDAO prdao= new PrivateRchrgDAO();
			//System.out.println(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_SID_SERVICE_ALLOW)).booleanValue());
			if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_SID_SERVICE_ALLOW)).booleanValue())
			{
				String sidprefixes=SystemPreferences.PRVT_RC_MSISDN_PREFIX_LIST;// comma seperated field values
                  String [] sidprefix=sidprefixes.split(",");
                  for(int i=0;i<sidprefix.length;i++)
                  {
                        if(p_sid.startsWith(sidprefix[i].trim()))
                        {
                              prvo=prdao.loadUserDetailsBySID(p_con,p_sid);
                              break;
                        }
                  }
			}		

		}
		catch(BTSLBaseException be)
		{
			throw be;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			_log.error("getPrivateRechargeDetails","  Exception while validating user message :"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PretupsBL[validateC2SRechargeRequest]","","","","Exception while validating user message" +" ,getting Exception="+e.getMessage());
			throw new BTSLBaseException(this,"getPrivateRechargeDetails",PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}		
		if(_log.isDebugEnabled()) _log.debug("getPrivateRechargeDetails","Exiting ");
		return prvo;
	}
}


