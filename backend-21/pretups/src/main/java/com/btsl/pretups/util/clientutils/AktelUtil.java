/**
 * @(#)AktelUtil.java
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.routing.subscribermgmt.businesslogic.RoutingVO;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.txn.pretups.routing.subscribermgmt.businesslogic.RoutingTxnDAO;

public class AktelUtil extends OperatorUtil
{
	/**
	 * Field _log.
	 */
	private Log _log = LogFactory.getLog(this.getClass().getName());
	Connection con = null;
	/**
	 * This method will convert operator specific msisdn to system specific msisdn.
	 * @param p_msisdn
	 * @return
	 * @throws BTSLBaseException
	 */
	public String getSystemFilteredMSISDN(String p_msisdn) throws BTSLBaseException
	{
		if(_log.isDebugEnabled()) _log.debug(" Aktel Util getSystemFilteredMSISDN","Entered filteredMSISDN:"+p_msisdn);
		String msisdn=super.getSystemFilteredMSISDN(p_msisdn);
		
		// added for handling 016 Prefix from Airtel in C2c
			msisdn=this.addRemoveDigitsFromMSISDN(msisdn);
			
		/* added for Bug Fixes ITR2 */
		if(!BTSLUtil.isNumeric(p_msisdn))
		{         
			throw new BTSLBaseException("AktelUtil","getSystemFilteredMSISDN",PretupsErrorCodesI.C2S_USER_CODE_NOT_NUMERIC);
        }
		if(_log.isDebugEnabled()) _log.debug(" Aktel Util getSystemFilteredMSISDN","Exited  filteredMSISDN:"+p_msisdn);
			return msisdn;
		
	}
	
	public String addRemoveDigitsFromMSISDN(String msisdn)
    {
        //this block is for Operator specific
		if(_log.isDebugEnabled()) _log.debug("Aktel Util addRemoveDigitsFromMSISDN","Entered filteredMSISDN:"+msisdn);
		
	    if(msisdn.length()>((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH))).intValue())
	    {
	        if((msisdn.substring(0,1)).equals("0"))
                msisdn=msisdn.substring(1,msisdn.length());
	    }
	    if(_log.isDebugEnabled()) _log.debug(" Aktel Util addRemoveDigitsFromMSISDN","Exited filteredMSISDN:"+msisdn);
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

	/**
	 * Method processPostBillPayment.
	 * @param p_requestedAmt long
	 * @param p_prevBal long
	 * @return boolean
	 */
	// This is commented during migration from Pretups5002 to Pretups521, 
	// because in Aktel OmaUtil was running and this method was not implemented in that. 
	// Now AktelUtil is running.

	/*public boolean processPostBillPayment(long p_requestedAmt,long p_prevBal)
	{
		boolean processBillPay=true;
		if(p_requestedAmt > p_prevBal)
			processBillPay=false;
		return processBillPay;
	}*/

	/**
	 * Method validatePassword.
	 * @author sanjeew.kumar
	 * @created on 12/07/07
	 * @param p_loginID String
	 * @param p_password String
	 * @return String
	 */
	public HashMap validatePassword(String p_loginID, String p_password) 
	{
		_log.debug("validatePassword","Entered, p_userID= ",new String(p_loginID+", Password= "+p_password));
		
		boolean passwordExist=false;
		HashMap messageMap=new HashMap();
		boolean specialCharFlag=false;
		boolean numberStrFlag=false;
		boolean capitalLettersFlag=false;
		boolean smallLettersFlag=false;
		boolean validPassword=false;
		String defaultPasswd = BTSLUtil.getDefaultPasswordNumeric(p_password);
		if(defaultPasswd.equals(p_password))
			return messageMap;
		defaultPasswd = BTSLUtil.getDefaultPasswordText(p_password);
		if(defaultPasswd.equals(p_password))
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

			for(int i=0,j=passwordCharArray.length;i<j;i++)
			{
				if(p_password.contains(passwordCharArray[i]))
				{
					specialCharFlag=true;
					break;
				}
			}
		}
		// for number
		String[]passwordNumberStrArray={"0","1","2","3","4","5","6","7","8","9"};

		for(int i=0,j=passwordNumberStrArray.length;i<j;i++)
		{
			if(p_password.contains(passwordNumberStrArray[i]))
			{
				numberStrFlag=true;
				break;
			}
		}

		//for Capital Letters
		if(p_password.matches(".*[A-Z].*"))
			capitalLettersFlag=true;
		System.out.println("capitalLettersFlag::"+capitalLettersFlag);
		//for  Small Letters
		if(p_password.matches(".*[a-z].*"))
			smallLettersFlag=true;
		System.out.println("smallLettersFlag::"+smallLettersFlag);
		//S+D+C+L
		if(specialCharFlag&&numberStrFlag&&capitalLettersFlag&&smallLettersFlag)
			validPassword=true;
		//S+D+C
		else if(specialCharFlag&&numberStrFlag&&capitalLettersFlag)
			validPassword=true;
		//D+C+L
		else if(numberStrFlag&&capitalLettersFlag&&smallLettersFlag)
			validPassword=true;
		//C+L+S
		else if(capitalLettersFlag&&smallLettersFlag&&specialCharFlag)
			validPassword=true;
		//L+S+D
		else if(smallLettersFlag&&specialCharFlag&&numberStrFlag)
			validPassword=true;

		if(!validPassword)
			messageMap.put("operatorutil.validatepassword.error.passwordmusthaverequiredchar",null);

		//01-MAR-2014 By Diwakar
		Connection con=null;
		if(!BTSLUtil.isNullString(p_loginID)) {
			if(p_password.contains(p_loginID))
				messageMap.put("operatorutil.validatepassword.error.sameusernamepassword",null);

			try{
				con = OracleUtil.getConnection();
				passwordExist=this.checkPasswordHistory(con,PretupsI.USER_PASSWORD_MANAGEMENT, p_loginID, p_password);
				if(passwordExist)
					messageMap.put("user.modifypwd.error.newpasswordexistcheck",((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PREV_PASS_NOT_ALLOW))).intValue());
			}
			catch(Exception e)
			{
				_log.error("validatePassword","Exception:"+e.getMessage());
				e.printStackTrace();
			}
			//Diwakar
			finally{
				try {
					if (con != null) {
						con.close();
					}
				} catch (Exception e) {
					_log.error("validatePassword","Exception:"+e.getMessage());
				}
			}
		}
		//Ended
		if (_log.isDebugEnabled())
			_log.debug("validatePassword", "Exiting ");
			return messageMap;

	}

	/**
	 * Method for checking Pasword or  already exist in Pin_Password_history table or not.
	 * @param p_con java.sql.Connection
	 * @param p_modificationType String
	 * @param p_loginId String
	 * @param p_newPassword String
	 * @return flag boolean 
	 * @throws  BTSLBaseException
	 */
	private boolean checkPasswordHistory(Connection p_con,String p_modificationType ,String p_loginId,String p_newPassword) throws BTSLBaseException
	{
		final String methodName = "checkPasswordHistory";
		if (_log.isDebugEnabled()){
			_log.debug(methodName, "Entered: p_modification_type=" +p_modificationType+"p_loginId=" + p_loginId+ " p_newPassword= "+p_newPassword);
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean existFlag = false;
		StringBuffer strBuff = new StringBuffer();
		strBuff.append(" SELECT pin_or_password,modified_on FROM (SELECT pin_or_password,modified_on,  row_number()  over (ORDER BY modified_on DESC) rn  ");
		strBuff.append(" FROM pin_password_history WHERE modification_type= ? AND msisdn_or_loginid=? )  WHERE rn <= ? ");
		strBuff.append(" ORDER BY modified_on DESC ");
		String sqlSelect = strBuff.toString();
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "QUERY sqlSelect=" + sqlSelect); 
		}    
		try
		{

			pstmt = p_con.prepareStatement(sqlSelect);
			pstmt.setString(1, p_modificationType);
			pstmt.setString(2, p_loginId);	
			pstmt.setInt(3,((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PREV_PASS_NOT_ALLOW))).intValue());
			rs = pstmt.executeQuery();
			while(rs.next()){				 
				if(rs.getString("Pin_or_Password").equals(p_newPassword)){
					existFlag = true;
					break;
				}
			}
			return existFlag;
		}
		catch (SQLException sqe)
		{
			_log.error(methodName, "SQLException : " + sqe);
			_log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"UserDAO[checkPasswordHistory]","","","","SQL Exception:"+sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} 
		catch (Exception ex)
		{
			_log.error(methodName, "Exception : " + ex);
			ex.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"UserDAO[checkPasswordHistory]","","","","Exception:"+ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally
		{
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				_log.error(methodName, "Exception : " + e);
				
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "QUERY pstmt=   " + pstmt); 
			} 
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				_log.error(methodName, "Exception : " + e);
			}
			if (_log.isDebugEnabled()){
				_log.debug(methodName, "Exiting: existFlag=" + existFlag);
			}
		}
	}

	/**
	 * 
	 */
	public void updateBonusListAfterTopup(HashMap p_map,C2STransferVO p_c2stransferVO)
	{
		if(_log.isDebugEnabled())_log.debug("updateBonusListAfterTopup","_transferID: "+p_c2stransferVO.getTransferID()+" Entered");
//		Nothing to do as there is no bundle managment in the Robi.
		if(_log.isDebugEnabled())_log.debug("updateBonusListAfterTopup","Exited");
	}

	/* Added for the Airtel Merger */

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
		String requiredDomain=Constants.getProperty("REQUIRED_DOMAIN");
		if (_log.isDebugEnabled())
			_log.debug("validateCreditRequestSms", "requiredDomain: "
					+ requiredDomain);
		ChannelUserVO senderVo=(ChannelUserVO)p_requestVO.getSenderVO();
		if((!BTSLUtil.isNullString(requiredDomain))&& ((!BTSLUtil.isNullString(senderVo.getDomainID()))&&requiredDomain.contains(senderVo.getDomainID())))
		{
			validateAirtelEVDRequestFormat(p_con,p_c2sTransferVO,p_requestVO);
			
		}	
		else
		{
			super.validateEVDRequestFormat(p_con,p_c2sTransferVO,p_requestVO);
		}
	}
	public void validateAirtelEVDRequestFormat(Connection p_con,C2STransferVO p_c2sTransferVO,RequestVO p_requestVO) throws BTSLBaseException
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
			if (_log.isDebugEnabled())
				_log.debug("validateAirtelEVDRequestFormat", "messageLen: "	+ messageLen);
			for(int i=0;i<messageLen;i++)
			{
				if (_log.isDebugEnabled())
					_log.debug("validateAirtelEVDRequestFormat", "i: " + i	+ " value: " + p_requestArr[i]);
			}
			switch(messageLen)
			{
			case 3:
			{
				//Do the 000 check Default PIN 
				System.out.println("p_requestVO.getRequestGatewayType()@@@@@@"+p_requestVO.getRequestGatewayType());
				if( p_requestVO.getRequestGatewayType().equalsIgnoreCase(PretupsI.GATEWAY_TYPE_SMSC))
				{
					if(userPhoneVO.getPinRequired().equals(PretupsI.YES))
					{
						try
						{
							ChannelUserBL.validatePIN(p_con,channelUserVO,p_requestArr[2]);
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
					custMsisdn=p_requestVO.getFilteredMSISDN();

					PretupsBL.validateMsisdn(p_con,receiverVO,p_c2sTransferVO.getRequestID(),custMsisdn);

					//Recharge amount Validation
					requestAmtStr=p_requestArr[1];
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

			}
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
						if(be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) ||  (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK))))
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
				//Do the 000 check Default PIN 
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
						throw new BTSLBaseException(this,"validateEVDRequestFormat",PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
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
						if(serviceSelectorMappingVO!=null)
							p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());									
					}
				}
				else
					p_requestVO.setReqSelector(p_requestArr[3]);

				PretupsBL.getSelectorValueFromCode(p_requestVO);
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
						throw new BTSLBaseException(this,"validateEVDRequestFormat",PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
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
						if(serviceSelectorMappingVO!=null)
							p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());									
					}
				}
				else
					p_requestVO.setReqSelector(p_requestArr[3]);

				PretupsBL.getSelectorValueFromCode(p_requestVO);
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
				}
				if (_log.isDebugEnabled()) 
					_log.debug(this,"sender locale: ="+p_requestVO.getSenderLocale());

				if(BTSLUtil.isNullString(p_requestArr[5]))
					p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
				else
				{
					int langCode=PretupsBL.getLocaleValueFromCode(p_requestVO,p_requestArr[5]);
					if(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode))==null)
						throw new BTSLBaseException(this,"validateEVDRequestFormat",PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
					p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
				}
				break;
			}
			default:
				throw new BTSLBaseException(this,"validateEVDRequestFormat",PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT,0,new String[]{p_requestVO.getActualMessageFormat()},null);
			}
			//Self EVR  Allowed Check
			String senderMSISDN=(channelUserVO.getUserPhoneVO()).getMsisdn();
			String receiverMSISDN=((ReceiverVO)p_c2sTransferVO.getReceiverVO()).getMsisdn();
			if(p_c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_EVR))
			{
				if(receiverMSISDN.equals(senderMSISDN) && !((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ALLOW_SELF_EVR))).booleanValue())
					throw new BTSLBaseException(this,"validateEVDRequestFormat",PretupsErrorCodesI.CHNL_ERROR_SELF_TOPUP_NTALLOWD);
			}
		}
		catch(BTSLBaseException be)
		{
			throw be;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			_log.error("validateEVDRequestFormat","  Exception while validating user message :"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorUtil[validateEVDRequestFormat]","","","","Exception while validating user message" +" ,getting Exception="+e.getMessage());
			throw new BTSLBaseException(this,"validateEVDRequestFormat",PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if (_log.isDebugEnabled())
			_log.debug("validateEVDRequestFormat", "Exiting ");
	}
	
	public boolean isSubscriberPrefixMappingExist(Connection con,String receiverMsisdn,String senderMsisdn,String subscriberType){
		final String methodName = "isSubscriberPrefixMappingExist";
		if(_log.isDebugEnabled()) 
			_log.debug(methodName,"Entered with receiverMsisdn "+receiverMsisdn+" senderMsisdn "+senderMsisdn+" subscriberType "+subscriberType);
		RoutingTxnDAO routingDAO = new RoutingTxnDAO();
		RoutingVO routingVO = null;
		boolean isAllowed = false;
		try{
			if(!(PretupsBL.getMSISDNPrefix(receiverMsisdn).equals(PretupsBL.getMSISDNPrefix(senderMsisdn))))
			{
				ArrayList routingList = routingDAO.loadSubscriberRoutingList(con,receiverMsisdn);
				if(routingList == null || routingList.size() == 0)
					return isAllowed;
				for(int count = 0;count<routingList.size();count++){
					routingVO = (RoutingVO) routingList.get(count);
					if(routingVO.getSubscriberType().equalsIgnoreCase(((LookupsVO)LookupsCache.getObject(PretupsI.SUBSRICBER_TYPE ,subscriberType)).getLookupName()) && PretupsBL.getMSISDNPrefix(senderMsisdn).startsWith(routingVO.getText1())){
						isAllowed = true;
						break;
					}
				}
			}
			else
				isAllowed = true;
		}
		catch(BTSLBaseException be){
			_log.errorTrace(methodName, be);
		}
		if(_log.isDebugEnabled()) 
			_log.debug(methodName,"Exiting isAllowed : "+isAllowed);
		return isAllowed;
	}
}
