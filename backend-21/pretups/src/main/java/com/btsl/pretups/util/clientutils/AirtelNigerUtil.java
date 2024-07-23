package com.btsl.pretups.util.clientutils;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

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
import com.btsl.login.LoginDAO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberDAO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.privaterecharge.businesslogic.PrivateRchrgVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.ibm.icu.util.Calendar;

public class AirtelNigerUtil extends OperatorUtil{
	
	
	  private Log _log = LogFactory.getLog(this.getClass().getName());

	    /* (non-Javadoc)
	     * @see com.btsl.pretups.util.OperatorUtil#getP2PChangePinMessageArray(java.lang.String[])
	     */
	    public String[] getP2PChangePinMessageArray(String message[])
	    {
	    	if(message.length ==3)
	    		{
			String message1[]= new String[message.length+1];
			message1[0]=message[0];
			message1[1]=message[1];
			message1[2]=message[2];
			message1[3]=message[2];
			return message1;
			}
	    	else
	    	return message;
	    		                   
	    }
	    
	    /* (non-Javadoc)
	     * @see com.btsl.pretups.util.OperatorUtil#getC2SChangePinMessageArray(java.lang.String[])
	     */
	    public String[] getC2SChangePinMessageArray(String message[])
	    {
	    	final String methodName="getC2SChangePinMessageArray";
	    	if(_log.isDebugEnabled()) _log.debug(methodName,"messageLen: "+message.length);
	    	if(message.length ==3)
	    		{
			String message1[]= new String[message.length+1];
			message1[0]=message[0];
			message1[1]=message[1];
			message1[2]=message[2];
			message1[3]=message[2];
			return message1;
			}
	    	else
	    	return message;
	    		                   
	    }
	    
	  
	    /**
		 * This method used for Password validation.
		 * While creating or modifying the user Password This method will be used.
		 * Method validatePassword.
		 * @author 
		 * @created on 
		 * @param p_loginID String
		 * @param p_password String
		 * @return HashMap
		 */
		public HashMap validatePassword(String p_loginID, String p_password)
		{
			final String methodName="validatePassword";
			_log.debug(methodName,"Entered, p_userID= ",new String(p_loginID+", Password= "+p_password));
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

			//check for small and capital letters
			String alphabets="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
			char[] ch = p_password.toCharArray();
			boolean isAlphabet = false; 
			for(int i=0,j=ch.length;i<j;i++)
			{
				if(alphabets.contains(Character.toString(ch[i])))
				{
					isAlphabet = true;
					break;
				}
			}
			if(!isAlphabet)
			{
				messageMap.put("nigeriautil.validatepassword.error.passwordalphabetchar",null);
			}
			else{
				String alphabets1="abcdefghijklmnopqrstuvwxyz";
				char[] ch1 = p_password.toCharArray();
				boolean isAlphabet1 = false; 
				for(int i=0,j=ch.length;i<j;i++)
				{
					if(alphabets.contains(Character.toString(ch[i])))
					{
						isAlphabet1 = true;
						break;
					}
				}
				if( !isAlphabet1)
				{
					messageMap.put("nigeriautil.validatepassword.error.passwordalphabetchar",null);
				}
			}
			
			// check for atleast two non-alphabet characters
			String specialChar=Constants.getProperty("SPECIAL_CHARACTER_PASSWORD_VALIDATION");
			isAlphabet = false; 
			if(!BTSLUtil.isNullString(specialChar))
			{
				for(int i=0;i<ch.length;i++)
				{
					if(specialChar.contains(Character.toString(ch[i])))
					{
						isAlphabet = true;
						break;
					}
				}  
			}
			if(!isAlphabet)
			{
				messageMap.put("nigeriautil.validatepassword.error.passwordnonalphabetchar",null);
			}
			else{	
				String passwordNumberStrArray="0123456789";
				boolean isAlphabet1 = false; 
				for(int i=0;i<ch.length;i++)
				{
					if(passwordNumberStrArray.contains(Character.toString(ch[i])))
					{
						isAlphabet1=true;
						break;
					}
				}
				if( !isAlphabet1)
				{
					messageMap.put("nigeriautil.validatepassword.error.passwordnonalphabetchar",null);
				}
			}
			//check for same consecutive 3 characters of password with LoginId
			String compare = null;
			String tempPassword = p_password;
			boolean match = false;
			for(int i=0; i<(p_loginID.length()-2); i++)
			{
				compare = p_loginID.substring(i, i+3);
				tempPassword = tempPassword.replace(compare,"");
				if(tempPassword.length()<p_password.length())
				{
					match = true;
					break;
				}
			}
			if(match)
			{
				messageMap.put("nigeriautil.validatepassword.error.passwordloginidmatch",null);
			}

			if(_log.isDebugEnabled()) _log.debug(methodName,"Exiting messageMap.size()="+messageMap.size());
			return messageMap;
		}
		
		public boolean validateTransactionPassword(ChannelUserVO p_channelUserVO, String p_password)throws BTSLBaseException
		{
			final String methodName="validateTransactionPassword";
			if (_log.isDebugEnabled()) _log.debug(methodName, " Entered p_channelUserVO=:" + p_channelUserVO + " p_password=" +p_password);
			boolean passwordValidation=true;
			Connection con = null;
			MComConnectionI mcomCon = null;
			try
			{
				if(p_channelUserVO!=null)
				{
					/*
					 * change done by ashishT for hashing implementation
					 * comparing the password hashvlue from db to the password sent by user.
					 */
					// 	if(!BTSLUtil.isNullString(p_channelUserVO.getPassword()) && (PretupsI.FALSE.equalsIgnoreCase(BTSLUtil.compareHash2String(p_channelUserVO.getPassword(), p_password))))
					if("SHA".equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE)))
					{
						boolean checkpassword;
						if(p_password.length()>((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_LOGIN_PWD_LENGTH))).intValue())
							checkpassword=BTSLUtil.decryptText(p_channelUserVO.getPassword()).equals(p_password);
						else
							checkpassword=(!PretupsI.FALSE.equalsIgnoreCase(BTSLUtil.compareHash2String(p_channelUserVO.getPassword(), p_password)));		
						if(!BTSLUtil.isNullString(p_channelUserVO.getPassword()) && (!checkpassword))
							passwordValidation=false;
					}
					else
					{
						if(!BTSLUtil.isNullString(p_channelUserVO.getPassword()) && (PretupsI.FALSE.equalsIgnoreCase(BTSLUtil.compareHash2String(p_channelUserVO.getPassword(), p_password))))
						{	
							passwordValidation=false;
						}

						if(p_channelUserVO.getPasswordModifiedOn()==null)
						{
							throw new BTSLBaseException(this, "updatePasswordInvalidCount", PretupsErrorCodesI.XML_ERROR_CHANGE_DEFAULT_PASSWD);
						}
						else if(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_PASSWORD)).equals(p_channelUserVO.getPassword()))//if password value == default Password Value force the user to change the password
						{
							throw new BTSLBaseException(this, "updatePasswordInvalidCount", PretupsErrorCodesI.XML_ERROR_CHANGE_DEFAULT_PASSWD);
						}
						else
						{
							java.util.Date date1=p_channelUserVO.getPasswordModifiedOn();
							java.util.Date date2=new java.util.Date();

							long dt1=date1.getTime();
							long dt2=date2.getTime();
							long nodays=(long)((dt2-dt1)/(1000*60*60*24));
							long noPasswordTimeOutDays=0;
							try
							{
								noPasswordTimeOutDays=((Integer)PreferenceCache.getControlPreference(PreferenceI.DAYS_AFTER_CHANGE_PASSWORD,p_channelUserVO.getNetworkID(),p_channelUserVO.getCategoryCode())).intValue();

							}
							catch(Exception e)
							{
								System.out.println("loginController.jsp noPasswordTimeOutDays not found in system preferences");
							}
							/*
							 * Here we are checking whether the password change is required or not 
							 *            a)category check(In constants file we define those categories whom password change not required)
							 *  b)No of days check 
							 */
							 if(nodays > noPasswordTimeOutDays)
							 {
								 throw new BTSLBaseException(this, "updatePasswordInvalidCount", PretupsErrorCodesI.XML_ERROR_CHANGE_DEFAULT_PASSWD);
							 }
						}
						mcomCon = new MComConnection();
						con=mcomCon.getConnection();
						
						if (p_channelUserVO.getInvalidPasswordCount() == ((Integer)PreferenceCache.getControlPreference(PreferenceI.MAX_PASSWORD_BLOCK_COUNT,p_channelUserVO.getNetworkID(),p_channelUserVO.getCategoryCode())).intValue())
						{
							//If password is blocked throw an exception
							throw new BTSLBaseException(this, "updatePasswordInvalidCount",PretupsErrorCodesI.CHNL_ERROR_SENDER_BLOCKED);
						}
						else if(updatePasswordInvalidCount(con,p_channelUserVO,p_password, passwordValidation))
						{
							if (p_channelUserVO.getInvalidPasswordCount() == ((Integer)PreferenceCache.getControlPreference(PreferenceI.MAX_PASSWORD_BLOCK_COUNT,p_channelUserVO.getNetworkID(),p_channelUserVO.getCategoryCode())).intValue())
							{
								//If password is blocked throw an exception
								throw new BTSLBaseException(this, "updatePasswordInvalidCount", PretupsErrorCodesI.CHNL_ERROR_SENDER_BLOCKED);
							}
							throw new BTSLBaseException(this, "updatePasswordInvalidCount",PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PASSWORD);
						}
						
					}

				}
				else
					throw new BTSLBaseException("KenyaAirtelUtil", methodName, PretupsErrorCodesI.XML_ERROR_NO_SUCH_USER);

			}
			catch (BTSLBaseException bex)
			{
				throw bex;
			}
			catch (Exception e)
			{
				_log.error(methodName, "Exception " + e.getMessage());
				e.printStackTrace();
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"KenyaAirtelUtil[validateTransactionPassword]","","","","Exception:"+e.getMessage());  
				throw new BTSLBaseException("KenyaAirtelUtil", methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
			}
			finally
			{
				if(mcomCon != null){mcomCon.close("AirtelNigerUtil#validateTransactionPassword");mcomCon=null;}
				if (_log.isDebugEnabled()) 
					_log.debug(methodName, " Exiting passwordValidation="+passwordValidation);
			}
			return passwordValidation;
		}
		

		/**
		 * Method to update the Password Invalid Count in the data base
		 * @param p_channelUserVO ChannelUserVO
		 * @param p_password String
		 * @param isPWDBlocedkExpired boolean
		 * @throws Exception
		 */
		private boolean updatePasswordInvalidCount(Connection p_con,ChannelUserVO p_channelUserVO,String p_password,boolean isPWDBlocedkExpired)throws Exception
		{
			final String methodName="updatePasswordInvalidCount";
			boolean passwordStatus = false;
			int updateStatus = 0;
			if (_log.isDebugEnabled()) _log.debug(methodName, " Entered p_channelUserVO=:" + p_channelUserVO + " p_password=" +p_password);
			try
			{
				LoginDAO _loginDAO= new LoginDAO();

				String decryptedPassword = BTSLUtil.decryptText(p_channelUserVO.getPassword());
				Date currentDate = new Date();
				p_channelUserVO.setModifiedOn(currentDate);

				if (_log.isDebugEnabled())
					_log.debug(methodName, "User Login Id:" + p_channelUserVO.getLoginID() + " decrypted Password=" + decryptedPassword + " entered Password=" + p_channelUserVO.getPassword());
				if (_log.isDebugEnabled())
					_log.debug(methodName,"Modified On="+p_channelUserVO.getModifiedOn()+" Updated On="+p_channelUserVO.getPasswordCountUpdatedOn()+" System="+((Long)PreferenceCache.getControlPreference(PreferenceI.PASSWORD_BLK_RST_DURATION,p_channelUserVO.getNetworkID(),p_channelUserVO.getCategoryCode())).longValue());
				//done by ashishT , for controling the both case hashing and AES/DES mode.
				//boolean passwordFlag=isPWDBlocedkExpired;
				if(!isPWDBlocedkExpired)
				{
					long mintInDay=24*60;
					if(p_channelUserVO.getPasswordCountUpdatedOn()!=null)
					{
						//Check if Password counters needs to be reset after the reset duration
						Calendar cal=BTSLDateUtil.getInstance();
						cal.setTime(p_channelUserVO.getModifiedOn());
						int d1=cal.get(Calendar.DAY_OF_YEAR);
						cal.setTime(p_channelUserVO.getPasswordCountUpdatedOn());
						int d2=cal.get(Calendar.DAY_OF_YEAR);
						if (_log.isDebugEnabled()) _log.debug(methodName, "Day Of year of Modified On="+d1+" Day Of year of PasswordCountUpdatedOn="+d2);
						//updated by shishupal on 15/03/2007
						//if(d1!=d2 && SystemPreferences.PASSWORD_BLK_RST_DURATION<=mintInDay)
						if(d1!=d2 && ((Long)PreferenceCache.getControlPreference(PreferenceI.PASSWORD_BLK_RST_DURATION,p_channelUserVO.getNetworkID(),p_channelUserVO.getCategoryCode())).longValue()<=mintInDay)
						{
							p_channelUserVO.setInvalidPasswordCount(1);
							p_channelUserVO.setPasswordCountUpdatedOn(p_channelUserVO.getModifiedOn());
						}
						else if(d1!=d2 && ((Long)PreferenceCache.getControlPreference(PreferenceI.PASSWORD_BLK_RST_DURATION,p_channelUserVO.getNetworkID(),p_channelUserVO.getCategoryCode())).longValue()>=mintInDay && (d1-d2)>=(((Long)PreferenceCache.getControlPreference(PreferenceI.PASSWORD_BLK_RST_DURATION,p_channelUserVO.getNetworkID(),p_channelUserVO.getCategoryCode())).longValue()/mintInDay))
						{
							p_channelUserVO.setInvalidPasswordCount(1);
							p_channelUserVO.setPasswordCountUpdatedOn(p_channelUserVO.getModifiedOn());
						}
						else if(((p_channelUserVO.getModifiedOn().getTime()-p_channelUserVO.getPasswordCountUpdatedOn().getTime())/(60*1000))<((Long)PreferenceCache.getControlPreference(PreferenceI.PASSWORD_BLK_RST_DURATION,p_channelUserVO.getNetworkID(),p_channelUserVO.getCategoryCode())).longValue())
						{
							if (p_channelUserVO.getInvalidPasswordCount() - ((Integer)PreferenceCache.getControlPreference(PreferenceI.MAX_PASSWORD_BLOCK_COUNT,p_channelUserVO.getNetworkID(),p_channelUserVO.getCategoryCode())).intValue()== 0)
								throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHNL_ERROR_SENDER_BLOCKED);
							p_channelUserVO.setPasswordCountUpdatedOn(p_channelUserVO.getModifiedOn());             
							if(!isPWDBlocedkExpired)
								p_channelUserVO.setInvalidPasswordCount(p_channelUserVO.getInvalidPasswordCount() + 1);
							else
								p_channelUserVO.setInvalidPasswordCount(p_channelUserVO.getInvalidPasswordCount());
						}
						else
						{
							p_channelUserVO.setInvalidPasswordCount(1);
							p_channelUserVO.setPasswordCountUpdatedOn(p_channelUserVO.getModifiedOn());
						}
					}
					else
					{
						p_channelUserVO.setInvalidPasswordCount(1);
						p_channelUserVO.setPasswordCountUpdatedOn(p_channelUserVO.getModifiedOn());
					}

					updateStatus = _loginDAO.updatePasswordCounter(p_con, p_channelUserVO);
					if(updateStatus>0)
						p_con.commit();
					else
					{
						p_con.rollback();
						throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.XML_ERROR_NO_SUCH_USER);
					}
					passwordStatus = true;
				} 
				else
				{
					// initilize Password Counters if ifPinCount>0
					if (p_channelUserVO.getInvalidPasswordCount() > 0)
					{
						p_channelUserVO.setInvalidPasswordCount(0);
						p_channelUserVO.setPasswordCountUpdatedOn(null);
						updateStatus = _loginDAO.updatePasswordCounter(p_con, p_channelUserVO);
						if(updateStatus>0)
							p_con.commit();
						else
						{
							p_con.rollback();
							throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.XML_ERROR_NO_SUCH_USER);
						}
					}
				}
			}
			catch(BTSLBaseException bbe){
				throw bbe;
			}
			catch (Exception e)
			{
				throw e;
			}
			finally
			{
				if (_log.isDebugEnabled())   _log.debug(methodName,"Exiting  ::: passwordStatus:"+passwordStatus);
			}
			return passwordStatus;
		}//end of updatePasswordInvalidCount
		
		
		 /**
	     * Method to validate the SMS PIn sent in the request
	     * @param p_pin
	     * @throws BTSLBaseException
	     */
	    public void validatePIN(String p_pin) throws BTSLBaseException
	    {
	    	final String methodName="validatePIN";
	        if(BTSLUtil.isNullString(p_pin))
	            throw new BTSLBaseException("BTSLUtil",methodName,PretupsErrorCodesI.PIN_INVALID);
	        else if(!BTSLUtil.isNumeric(p_pin))
	            throw new BTSLBaseException("BTSLUtil",methodName,PretupsErrorCodesI.NEWPIN_NOTNUMERIC);
	        else if (p_pin.length() < ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_SMS_PIN_LENGTH))).intValue()  || p_pin.length() > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_SMS_PIN_LENGTH))).intValue())    
	        {
	            String msg[] = {String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_SMS_PIN_LENGTH))).intValue()),String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_SMS_PIN_LENGTH))).intValue())};
	            throw new BTSLBaseException("BTSLUtil",methodName,PretupsErrorCodesI.PIN_LENGTHINVALID,0,msg,null);
	        }
	    }
	    
	    
	    /**
	     * P2P
	     * This method validates the requested PIN with that available in DB, also
	     * checks whether to block user or reset the counter or not
	     * 
	     * @param p_con
	     * @param p_senderVO
	     * @param p_requestPin
	     * @throws BTSLBaseException
	     * @author ved.sharma
	     */
	    public void validatePIN(Connection p_con, SenderVO p_senderVO, String p_requestPin) throws BTSLBaseException
	    {
	    	final String methodName="validatePIN";
	        if (_log.isDebugEnabled())  _log.debug(methodName, "Entered with p_senderVO:" + p_senderVO.toString() + " p_requestPin=" + p_requestPin);
	        int updateStatus = 0;
	        boolean updatePinCount = false;
	        boolean isUserBarred = false;
	        try
	        {
	            if (_log.isDebugEnabled()) _log.debug(methodName, "Modified Time=:" + p_senderVO.getModifiedOn() + " p_senderVO.getPinModifiedOn()=" +p_senderVO.getPinModifiedOn());
	            if(p_senderVO.isForcePinCheckReqd() && p_senderVO.getPinModifiedOn()!=null && ((p_senderVO.getModifiedOn().getTime()-p_senderVO.getPinModifiedOn().getTime())/(24*60*60*1000))>((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DAYS_AFTER_CHANGE_PIN))).intValue())
	            {
	                if (_log.isDebugEnabled()) _log.debug(methodName, "Modified Time=:" + p_senderVO.getModifiedOn() + " p_senderVO.getPinModifiedOn()=" +p_senderVO.getPinModifiedOn()+" Difference="+((p_senderVO.getModifiedOn().getTime()-p_senderVO.getPinModifiedOn().getTime())/(24*60*60*1000)));
	                EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,"SubscriberBL[validatePIN]","",p_senderVO.getMsisdn(),"","Force User to change PIN after "+((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DAYS_AFTER_CHANGE_PIN))).intValue()+" days as last changed on "+p_senderVO.getPinModifiedOn());    
	                String strArr[]={String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DAYS_AFTER_CHANGE_PIN))).intValue())};                      
	                throw new BTSLBaseException("OperatorUtil", methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_FORCE_CHANGEPIN,0,strArr,null);
	            }
	            else
	            {
	                SubscriberDAO subscriberDAO = new SubscriberDAO();
	                String decryptedPin = BTSLUtil.decryptText(p_senderVO.getPin());
	                
	                if (_log.isDebugEnabled()) _log.debug(methodName, "Sender MSISDN:" + p_senderVO.getMsisdn() + " decrypted PIN=" + decryptedPin + " p_requestPin=" + p_requestPin);
	                
	                //added for Change the default PIN
	                if(p_senderVO.isForcePinCheckReqd() && ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)).equals(decryptedPin))
	                    throw new BTSLBaseException("OperatorUtil", methodName, PretupsErrorCodesI.CHNLUSR_CHANGE_DEFAULT_PIN);
	                //if (!decryptedPin.equalsIgnoreCase(p_requestPin))
	                /*
	                 * done by ashishT for checking the value in p_requestPin is hashvalue or actual value.
	                 */
	                boolean checkpin;
	                if("SHA".equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE)))
	                {
		                if(p_requestPin.length()>((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_PIN_MAX_LENGTH))).intValue())
		                	checkpin=decryptedPin.equals(p_requestPin);
		                else
		                	checkpin=(!PretupsI.FALSE.equalsIgnoreCase(BTSLUtil.compareHash2String(decryptedPin, p_requestPin)));
	                }
	                else
	                {
	                	checkpin=decryptedPin.equals(p_requestPin);
	                }
	                if (!checkpin)
	                {
	                    updatePinCount = true;
	                    int mintInDay=24*60;
	                    if(p_senderVO.getFirstInvalidPinTime()!=null)
	                    {
	                        //Check if PIN counters needs to be reset after the reset duration
	                        if (_log.isDebugEnabled()) _log.debug(methodName, "p_senderVO.getModifiedOn().getTime()="+p_senderVO.getModifiedOn().getTime()+" p_senderVO.getFirstInvalidPinTime().getTime()="+p_senderVO.getFirstInvalidPinTime().getTime()+" Diff="+((p_senderVO.getModifiedOn().getTime()-p_senderVO.getFirstInvalidPinTime().getTime())/(60*1000))+" Allowed="+((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_PIN_BLK_RST_DURATION))).longValue());
	                        Calendar cal=BTSLDateUtil.getInstance();
	                        cal.setTime(p_senderVO.getModifiedOn());
	                        int d1=cal.get(Calendar.DAY_OF_YEAR);
	                        cal.setTime(p_senderVO.getFirstInvalidPinTime());
	                        int d2=cal.get(Calendar.DAY_OF_YEAR);
	                        if (_log.isDebugEnabled()) _log.debug(methodName, "Day Of year of Modified On="+d1+" Day Of year of FirstInvalidPinTime="+d2);
	                        if(d1!=d2 && ((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_PIN_BLK_RST_DURATION))).longValue()<=mintInDay)
	                        {
	                            //reset
	                            p_senderVO.setPinBlockCount(1);
	                            p_senderVO.setFirstInvalidPinTime(p_senderVO.getModifiedOn());
	                        }
	                        else if(d1!=d2 && ((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_PIN_BLK_RST_DURATION))).longValue()>=mintInDay && (d1-d2)>=(((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_PIN_BLK_RST_DURATION))).longValue()/mintInDay))
	                        {
	                            //Reset
	                            p_senderVO.setPinBlockCount(1);
	                            p_senderVO.setFirstInvalidPinTime(p_senderVO.getModifiedOn());
	                        }
	                        else if(((p_senderVO.getModifiedOn().getTime()-p_senderVO.getFirstInvalidPinTime().getTime())/(60*1000))<((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_PIN_BLK_RST_DURATION))).longValue())
	                        {
	                            if (p_senderVO.getPinBlockCount() - ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_MAX_PIN_BLOCK_COUNT))).intValue() == 0)
	                            {
	                                //isStatusUpdate = true;
	                                //p_senderVO.setStatus(PretupsI.USER_STATUS_BLOCK);
	                                //Set The flag that indicates that we need to bar the user because of PIN Change
	                                p_senderVO.setPinBlockCount(0);
	                                //p_senderVO.setFirstInvalidPinTime(null);
	                                isUserBarred=true;
	                            }
	                            else
	                                p_senderVO.setPinBlockCount(p_senderVO.getPinBlockCount() + 1);
	                                
	                            if(p_senderVO.getPinBlockCount()==0)
	                                p_senderVO.setFirstInvalidPinTime(p_senderVO.getModifiedOn());
	                        }
	                        else
	                        {
	                            p_senderVO.setPinBlockCount(1);
	                            p_senderVO.setFirstInvalidPinTime(p_senderVO.getModifiedOn());
	                        }
	                    }
	                    else
	                    {
	                        p_senderVO.setPinBlockCount(1);
	                        p_senderVO.setFirstInvalidPinTime(p_senderVO.getModifiedOn());
	                    }
	                } 
	                else
	                {
	                    // initilize PIN Counters if ifPinCount>0
	                    if (p_senderVO.getPinBlockCount() > 0)
	                    {
	                        p_senderVO.setPinBlockCount(0);
	                        p_senderVO.setFirstInvalidPinTime(null);
	                        updateStatus = subscriberDAO.updatePinStatus(p_con, p_senderVO, false);
	                        if (updateStatus < 0)
	                        {
	                            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorUtil[validatePIN]","",p_senderVO.getMsisdn(),"","Not able to update invalid PIN count for users");  
	                            throw new BTSLBaseException("OperatorUtil", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
	                        }
	                    }
	                }
	                if (updatePinCount)
	                {
	                    updateStatus = subscriberDAO.updatePinStatus(p_con, p_senderVO, false);
	                    if (updateStatus > 0 && !isUserBarred)
	                        throw new BTSLBaseException("OperatorUtil", methodName, PretupsErrorCodesI.ERROR_INVALID_PIN);
	                    else if (updateStatus > 0 && isUserBarred)
	                    {
	                        p_senderVO.setBarUserForInvalidPin(true);
	                        throw new BTSLBaseException("OperatorUtil", methodName, PretupsErrorCodesI.ERROR_SNDR_PINBLOCK,0,new String[]{String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_MAX_PIN_BLOCK_COUNT))).intValue()),String.valueOf(((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_PIN_BLK_RST_DURATION))).longValue())},null);
	                    }
	                    else if (updateStatus < 0)
	                    {
	                        EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorUtil[validatePIN]","",p_senderVO.getMsisdn(),"","Not able to update invalid PIN count for users");  
	                        throw new BTSLBaseException("OperatorUtil", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
	                    }
	                }
	            }
	        } 
	        catch (BTSLBaseException bex)
	        {
	            throw bex;
	        } 
	        catch (Exception e)
	        {
	            _log.error(methodName, "Exception " + e.getMessage());
	            e.printStackTrace();
	            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorUtil[validatePIN]","","","","Exception:"+e.getMessage());   
	            throw new BTSLBaseException("OperatorUtil", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
	        }
	        finally{
	            if (_log.isDebugEnabled())
	                _log.debug(methodName, "Exiting with increase Pin Count flag=" + updatePinCount + " Barred Update Flag:" + isUserBarred);
	        }
	    }
	    
	    /**
	     *  
	     * This method validates the requested PIN business rules  
	     * @param p_requestPin
	     * @throws BTSLBaseException
	     * @author santanu.sharma
	     */
	    public void validatePINRules(String p_requestPin) throws BTSLBaseException
	    {
	    	final String methodName="validatePINRules";
	     if(_log.isDebugEnabled()) _log.debug(methodName,"Entered with p_requestPin="+p_requestPin);
	    	validatePIN(p_requestPin);
	     if(_log.isDebugEnabled()) _log.debug(methodName,"Exiting from OperatorUtil ");
	    }
	    
	    /**
	     * This method used for pin validation.
	     * While creating or modifying the user PIN This method will be used.
		 * Method validatePIN.
		 * @author vikas.kumar
		 * @param p_pin String
		 * @return HashMap
		 */
		public HashMap pinValidate(String p_pin)
		{
			final String methodName="pinValidate";
		    _log.debug(methodName,"Entered, PIN= "+p_pin);
		    HashMap messageMap=new HashMap();
	        
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
		  
		    if(_log.isDebugEnabled()) _log.debug(methodName,"Exiting messageMap.size()="+messageMap.size());
		    return messageMap;
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
			final String methodName="validateC2SRechargeRequest"; 
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
				if(_log.isDebugEnabled()) _log.debug(methodName,"messageLen: "+messageLen);
				for(int i=0;i<messageLen;i++)
				{
					if(_log.isDebugEnabled()) _log.debug(methodName,"i: "+i+" value: "+p_requestArr[i]);
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
//						Do the 000 check Default PIN 
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
						PretupsBL.validateAmount(p_c2sTransferVO,requestAmtStr);
						p_c2sTransferVO.setReceiverVO(receiverVO);
						if(BTSLUtil.isNullString(p_requestArr[3]))
							p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
						else
						{
							int langCode=PretupsBL.getLocaleValueFromCode(p_requestVO,p_requestArr[3]);
							if(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode))==null)
								throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
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
						}
						else
							p_requestVO.setReqSelector(p_requestArr[3]);
						
						PretupsBL.getSelectorValueFromCode(p_requestVO);
//						changed for CRE_INT_CR00029 by ankit Zindal
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
								throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
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
						}
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
							p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
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
								throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
							p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
						}
						break;
					}
					default:
						throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT,0,new String[]{p_requestVO.getActualMessageFormat()},null);
				}
								
			}
			catch(BTSLBaseException be)
			{
				throw be;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				_log.error(methodName,"  Exception while validating user message :"+e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PretupsBL[validateC2SRechargeRequest]","","","","Exception while validating user message" +" ,getting Exception="+e.getMessage());
				throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			}
			if(_log.isDebugEnabled()) _log.debug(methodName,"Exiting ");
		}
		
		//Added for Product Recharge
	    /**
		 * Method formatDBRCTransferID.
		 * @param p_transferVO TransferVO
		 * @param p_tempTransferID long
		 * @return String
		 * @see com.btsl.pretups.util.OperatorUtilI#formatDBRCTransferID(TransferVO, long)
		 */
		public String formatDBRCTransferID(TransferVO p_transferVO,long p_tempTransferID)
		{
			String returnStr=null;
			//Added for Product Recharge
			int DBRC_TRANSFER_ID_PAD_LENGTH=4;
			try
			{
				String paddedTransferIDStr=BTSLUtil.padZeroesToLeft(String.valueOf(p_tempTransferID),DBRC_TRANSFER_ID_PAD_LENGTH);
				returnStr="D"+currentDateTimeFormatString(p_transferVO.getCreatedOn())+"."+currentTimeFormatString(p_transferVO.getCreatedOn())+"."+Constants.getProperty("INSTANCE_ID")+paddedTransferIDStr;
				p_transferVO.setTransferID(returnStr);
			}
			catch(Exception e)
			{
				e.printStackTrace();
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
		 * @see com.btsl.pretups.util.OperatorUtilI#validateProductRechargeRequest(Connection, C2STransferVO, RequestVO)
		 */
		public void validateProductRechargeRequest(Connection p_con,C2STransferVO p_c2sTransferVO,RequestVO p_requestVO) throws BTSLBaseException
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
				if(_log.isDebugEnabled()) _log.debug("validateProductRechargeRequest","messageLen: "+messageLen);
				for(int i=0;i<messageLen;i++)
				{
					if(_log.isDebugEnabled()) _log.debug("validateProductRechargeRequest","i: "+i+" value: "+p_requestArr[i]);
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
//						Do the 000 check Default PIN 
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
//						changed for CRE_INT_CR00029 by ankit Zindal
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
								throw new BTSLBaseException(this,"validateProductRechargeRequest",PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
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
								throw new BTSLBaseException(this,"validateProductRechargeRequest",PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
							p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
						}
						break;
					}
					default:
						throw new BTSLBaseException(this,"validateProductRechargeRequest",PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT,0,new String[]{p_requestVO.getActualMessageFormat()},null);
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
}
