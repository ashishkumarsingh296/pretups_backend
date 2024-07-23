/*
 * @(#)OSierraLeoneUtil.java
 * Copyright(c) 2010, Comviva Technologies Ltd.
 * All Rights Reserved
 * Description :- 
 * --------------------------------------------------------------------
 *  Author                		Date            History
 * --------------------------------------------------------------------
 * Shishupal.singh              Jun 10, 2011	Initial creation
 * Diwakar						Mar 07, 2019	Modification
 * --------------------------------------------------------------------
 * */
package com.btsl.pretups.util.clientutils;

import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.login.LoginDAO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

public class OSierraLeoneUtil extends OperatorUtil
{
    private Log _log = LogFactory.getLog(this.getClass().getName());

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
    
    public String[] getC2SChangePinMessageArray(String message[])
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
		final  String methodName="validatePassword";
		_log.debug(methodName,"Entered, p_userID= ",new String(p_loginID+", Password= "+p_password));
		HashMap messageMap=new HashMap();
		int count = 0;
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
		boolean isPWDBlocedkExpired=false;
		Connection p_con = null;
		try
		{
			if(p_channelUserVO!=null)
			{
				/*
				 * change done by ashishT for hashing implementation
				 * comparing the password hashvlue from db to the password sent by user.
				 */
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
						throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.XML_ERROR_CHANGE_DEFAULT_PASSWD);
					}
					else if(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_PASSWORD)).equals(p_channelUserVO.getPassword()))//if password value == default Password Value force the user to change the password
					{
						throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.XML_ERROR_CHANGE_DEFAULT_PASSWD);
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
							 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.XML_ERROR_CHANGE_DEFAULT_PASSWD);
						 }
					}
					p_con = OracleUtil.getConnection();
					
					if (p_channelUserVO.getInvalidPasswordCount() == ((Integer)PreferenceCache.getControlPreference(PreferenceI.MAX_PASSWORD_BLOCK_COUNT,p_channelUserVO.getNetworkID(),p_channelUserVO.getCategoryCode())).intValue())
					{
						//If password is blocked throw an exception
						throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.CHNL_ERROR_SENDER_BLOCKED);
					}
					else if(updatePasswordInvalidCount(p_con,p_channelUserVO,p_password, passwordValidation))
					{
						if (p_channelUserVO.getInvalidPasswordCount() == ((Integer)PreferenceCache.getControlPreference(PreferenceI.MAX_PASSWORD_BLOCK_COUNT,p_channelUserVO.getNetworkID(),p_channelUserVO.getCategoryCode())).intValue())
						{
							//If password is blocked throw an exception
							throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHNL_ERROR_SENDER_BLOCKED);
						}
						throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PASSWORD);
					}
					
				}
				
			}
			else
				throw new BTSLBaseException("OSierraLeoneUtil", methodName, PretupsErrorCodesI.XML_ERROR_NO_SUCH_USER);

		}
		catch (BTSLBaseException bex)
		{
			_log.errorTrace(methodName,bex);
			throw bex;
		}
		catch (Exception e)
		{
			_log.error(methodName, "Exception " + e.getMessage());
			_log.errorTrace(methodName,e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OSierraLeoneUtil[validateTransactionPassword]","","","","Exception:"+e.getMessage());  
			throw new BTSLBaseException("OSierraLeoneUtil", methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
		}
		finally
		{
			if (p_con != null) {try{p_con.close();}catch (Exception e) {_log.error(methodName, " Unable to close connection="+e);}}
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
		boolean passwordStatus = false;
		int updateStatus = 0;
		final String methodName="updatePasswordInvalidCount";
		try
		{
			//con = OracleUtil.getConnection(); killed by sanjay
			LoginDAO _loginDAO= new LoginDAO();

			String decryptedPassword = BTSLUtil.decryptText(p_channelUserVO.getPassword());
			Date currentDate = new Date();
			p_channelUserVO.setModifiedOn(currentDate);

			if (_log.isDebugEnabled())
				_log.debug(methodName, "User Login Id:" + p_channelUserVO.getLoginID() + " decrypted Password=" + decryptedPassword + " entered Password=" + p_channelUserVO.getPassword());
			if (_log.isDebugEnabled())
			_log.debug(methodName,"Modified On="+p_channelUserVO.getModifiedOn()+" Updated On="+p_channelUserVO.getPasswordCountUpdatedOn()+" System="+((Long)PreferenceCache.getControlPreference(PreferenceI.PASSWORD_BLK_RST_DURATION,p_channelUserVO.getNetworkID(),p_channelUserVO.getCategoryCode())).longValue());
			//done by ashishT , for controling the both case hashing and AES/DES mode.
			if(!isPWDBlocedkExpired)
			{
				long mintInDay=24*60;
				if(p_channelUserVO.getPasswordCountUpdatedOn()!=null)
				{
					//Check if Password counters needs to be reset after the reset duration
					Calendar cal=Calendar.getInstance();
					cal.setTime(p_channelUserVO.getModifiedOn());
					int d1=cal.get(Calendar.DAY_OF_YEAR);
					cal.setTime(p_channelUserVO.getPasswordCountUpdatedOn());
					int d2=cal.get(Calendar.DAY_OF_YEAR);
					if (_log.isDebugEnabled()) _log.debug(methodName, "Day Of year of Modified On="+d1+" Day Of year of PasswordCountUpdatedOn="+d2);
					//updated by shishupal on 15/03/2007
					//if(d1!=d2 && SystemPreferences.PASSWORD_BLK_RST_DURATION<=mintInDay)
					if(d1!=d2 && ((Long)PreferenceCache.getControlPreference(PreferenceI.PASSWORD_BLK_RST_DURATION,p_channelUserVO.getNetworkID(),p_channelUserVO.getCategoryCode())).longValue()<=mintInDay)
					{
						if (_log.isDebugEnabled())  _log.debug(methodName,"shishu1");
						//reset
						p_channelUserVO.setInvalidPasswordCount(1);
						p_channelUserVO.setPasswordCountUpdatedOn(p_channelUserVO.getModifiedOn());
					}
					else if(d1!=d2 && ((Long)PreferenceCache.getControlPreference(PreferenceI.PASSWORD_BLK_RST_DURATION,p_channelUserVO.getNetworkID(),p_channelUserVO.getCategoryCode())).longValue()>=mintInDay && (d1-d2)>=(((Long)PreferenceCache.getControlPreference(PreferenceI.PASSWORD_BLK_RST_DURATION,p_channelUserVO.getNetworkID(),p_channelUserVO.getCategoryCode())).longValue()/mintInDay))
					{
						if (_log.isDebugEnabled())  _log.debug(methodName,"shishu2");
						//Reset
						p_channelUserVO.setInvalidPasswordCount(1);
						p_channelUserVO.setPasswordCountUpdatedOn(p_channelUserVO.getModifiedOn());
					}
					else if(((p_channelUserVO.getModifiedOn().getTime()-p_channelUserVO.getPasswordCountUpdatedOn().getTime())/(60*1000))<((Long)PreferenceCache.getControlPreference(PreferenceI.PASSWORD_BLK_RST_DURATION,p_channelUserVO.getNetworkID(),p_channelUserVO.getCategoryCode())).longValue())
					{
						if (_log.isDebugEnabled())  _log.debug(methodName,"shishu3");
						//updated by shishupal on 14/03/2007
						//if (p_userVO.getInvalidPasswordCount() - SystemPreferences.MAX_PASSWORD_BLOCK_COUNT== 0)
						if (p_channelUserVO.getInvalidPasswordCount() - ((Integer)PreferenceCache.getControlPreference(PreferenceI.MAX_PASSWORD_BLOCK_COUNT,p_channelUserVO.getNetworkID(),p_channelUserVO.getCategoryCode())).intValue()== 0)
						{
							if (_log.isDebugEnabled())  _log.debug(methodName,"shishu4");
							// password block message
							throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHNL_ERROR_SENDER_BLOCKED);
						}
						p_channelUserVO.setPasswordCountUpdatedOn(p_channelUserVO.getModifiedOn());             
						if(!isPWDBlocedkExpired){
							p_channelUserVO.setInvalidPasswordCount(p_channelUserVO.getInvalidPasswordCount() + 1);
							if (_log.isDebugEnabled())  _log.debug(methodName,"shishu5");
						}else{
							p_channelUserVO.setInvalidPasswordCount(p_channelUserVO.getInvalidPasswordCount());
							if (_log.isDebugEnabled())  _log.debug(methodName,"shishu6");
						}
					}
					else
					{
						if (_log.isDebugEnabled())  _log.debug(methodName,"shishu7");
						p_channelUserVO.setInvalidPasswordCount(1);
						p_channelUserVO.setPasswordCountUpdatedOn(p_channelUserVO.getModifiedOn());
					}
				}
				else
				{
					if (_log.isDebugEnabled())  _log.debug(methodName,"shishu8");
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
				if (_log.isDebugEnabled())  _log.debug(methodName,"shishu9");
				// initilize Password Counters if ifPinCount>0
				if (p_channelUserVO.getInvalidPasswordCount() > 0)
				{
					if (_log.isDebugEnabled())  _log.debug(methodName,"shishu10");
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
		}catch(BTSLBaseException bbe){
			_log.errorTrace(methodName,bbe);
			throw bbe;
		}
		catch (Exception e)
		{
			_log.errorTrace(methodName,e);
			throw e;
		}
		finally
		{
			if (_log.isDebugEnabled())   _log.debug(methodName,"Exiting  ::: passwordStatus:"+passwordStatus);
		}
		return passwordStatus;
	}//end of updatePasswordInvalidCount

}
