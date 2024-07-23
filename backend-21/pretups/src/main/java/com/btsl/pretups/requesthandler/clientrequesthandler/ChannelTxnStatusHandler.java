/**
 * @(#)ChannelTxnStatusHandler.java 
 * Copyright(c) 2006, Bharti Telesoft Ltd. 
 * All Rights Reserved
 * 
 * -------------------------------------------------------------------------------------------------
 * Author 				Date 			History
 * -------------------------------------------------------------------------------------------------
 * Anjali Agarwal 	Oct 24, 2017 		Initial Creation
 * This class will be responsible for loading the channel transaction details based on 
 * external reference or transaction id. 
 * 
 */

package com.btsl.pretups.requesthandler.clientrequesthandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
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
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;

public class ChannelTxnStatusHandler implements ServiceKeywordControllerI {

	private static Log log = LogFactory.getLog(ChannelTxnStatusHandler.class.getName());

	@Override
	public void process(RequestVO requestVO) {
		String methodName="process[ChannelTxnStatusHandler]";
		String validateUser="loadValidateUserDetails";
		if (log.isDebugEnabled()) {
			log.debug(methodName, " messageLen=" + requestVO.toString());
		}
		Connection con=null;MComConnectionI mcomCon = null;
		ChannelTransferVO transferVO=null;
		ChannelTransferDAO channelTransferDAO=new ChannelTransferDAO();
		String externalRefNum = requestVO.getExternalReferenceNum();
		String extRefNumORTxnID = null;
		ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
		OperatorUtilI operatorUtili=null;
		try
		{
			operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
		}
		catch(Exception e)
		{
			log.error(methodName, e);
			if(log.isDebugEnabled())
				log.debug(methodName, e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ExtAPIParsers[loadValidateUserDetails]","","","","Exception while loading the class at the call:"+e.getMessage());
		}
		try
		{
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			ChannelUserVO channelUserVO=null;
			String extCode =(String)requestVO.getRequestMap().get("EXTCODE");
			
			String channelUserMSISDN=null;
			channelUserMSISDN = (String)requestVO.getRequestMap().get("MSISDN");
			log.debug(methodName, "channelUserMSISDN = "+channelUserMSISDN+" , requestVO.getFilteredMSISDN() = "+requestVO.getFilteredMSISDN());
			//Ended Here
			String password =(String)requestVO.getRequestMap().get("PASSWORD");
			String networkID=requestVO.getRequestNetworkCode();
			String loginID=(String)requestVO.getRequestMap().get("LOGINID");
			String extNetCode = requestVO.getExternalNetworkCode();
			if(!BTSLUtil.isNullString(requestVO.getFilteredMSISDN()))
				channelUserVO= channelUserDAO.loadChannelUserDetails(con, requestVO.getFilteredMSISDN());
			else if(!BTSLUtil.isNullString(loginID))
				channelUserVO= channelUserDAO.loadChnlUserDetailsByLoginID(con, loginID);
			else if(!BTSLUtil.isNullString(extCode))
				channelUserVO= channelUserDAO.loadChnlUserDetailsByExtCode(con, BTSLUtil.NullToString(extCode).trim());
			if(channelUserVO!=null)
			{
				loadValidateUserDetails(requestVO, operatorUtili, channelUserVO, extCode, password,
						networkID, loginID);
			}
			//Changes END By Babu Kunwar
			else
			{
				Locale locale=new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
				if (!BTSLUtil.isNullString(loginID) && !BTSLUtil.isNullString(password))
				{
					//get user informationon the basis of login id and validate the password
					//if failed throw exception
					LoginDAO loginDAO= new LoginDAO();
					channelUserVO = loginDAO.loadUserDetails(con,loginID ,password,locale);
					if (channelUserVO==null)
						throw new BTSLBaseException(this, validateUser, PretupsErrorCodesI.XML_ERROR_NO_SUCH_USER);
					//block added on request by Vamsidhar to avoid channel users to use this feature on 13-jun-2007
					if (!PretupsI.USER_TYPE_OPERATOR.equalsIgnoreCase(channelUserVO.getUserType()))
						throw new BTSLBaseException(this, validateUser, PretupsErrorCodesI.XML_ERROR_NO_SUCH_USER);
					if (!extNetCode.equalsIgnoreCase(channelUserVO.getNetworkID()))
						throw new BTSLBaseException(this, validateUser, PretupsErrorCodesI.XML_ERROR_NO_SUCH_USER);
					//Moldova cahnges by Ved 24/07/07
					if(!BTSLUtil.isNullString(channelUserVO.getPassword()) && !operatorUtili.validateTransactionPassword(channelUserVO, password))
						throw new BTSLBaseException(this, validateUser, PretupsErrorCodesI.XML_ERROR_INVALID_PSWD);					
				}
				else
				{
					throw new BTSLBaseException(this, validateUser, PretupsErrorCodesI.XML_ERROR_NO_SUCH_USER);
				}
				requestVO.setLocale(locale);
				//validate the user
				if(channelUserVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_SUSPEND))
				{
					throw new BTSLBaseException(this, validateUser, PretupsErrorCodesI.XML_ERROR_SENDER_SUSPEND);
				}
				else if(channelUserVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_BLOCK))
				{
					throw new BTSLBaseException(this, validateUser, PretupsErrorCodesI.XML_ERROR_SENDER_BLOCKED);
				}
				else if(channelUserVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_DELETE_REQUEST))
				{
					throw new BTSLBaseException(this, validateUser, PretupsErrorCodesI.XML_ERROR_SENDER_DELETE_REQUEST);
				}
				else if(channelUserVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_SUSPEND_REQUEST))
				{
					throw new BTSLBaseException(this, validateUser, PretupsErrorCodesI.XML_ERROR_SENDER_SUSPEND_REQUEST);
				}
				//load geographical area
				GeographicalDomainDAO geographyDAO = new GeographicalDomainDAO();
				channelUserVO.setGeographicalAreaList(geographyDAO.loadUserGeographyList(con,channelUserVO.getUserID(),channelUserVO.getNetworkID()));
				//load domains
				DomainDAO domainDAO = new DomainDAO();  
				channelUserVO.setDomainList(domainDAO.loadDomainListByUserId(con,channelUserVO.getUserID()));
				UserPhoneVO phoneVO=new UserPhoneVO();
				phoneVO.setPhoneLanguage((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)));
				phoneVO.setCountry((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
				channelUserVO.setUserPhoneVO(phoneVO);
				requestVO.setSenderVO(channelUserVO);		
			}
			UserPhoneVO userPhoneVO = channelUserVO.getUserPhoneVO();
			final String[] messageArr = requestVO.getRequestMessageArray();
			String transferID = null;
			final int messageLen = messageArr.length;
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();

			if (log.isDebugEnabled()) {
				log.debug(methodName, " messageLen=" + messageLen+" messageArr= "+Arrays.toString(messageArr));
			}

			switch (messageLen) {

			// if message length is 3 then at the 2 index of array the value
			// could be extrefnum or transfer ID
			case PretupsI.C2S_MESSAGE_LENGTH_LAST_RECHARGE: 
			{
				extRefNumORTxnID = messageArr[2];
				if ("EXT".equals(extRefNumORTxnID)) {
					externalRefNum = messageArr[1];
					break;
				} else {
					transferID = messageArr[1];
					break;
				}
			}
			case PretupsI.C2S_MESSAGE_LENGTH_LAST_RECHARGE + 1:
			{
				if ("BOTH".equals(messageArr[3])) {
					transferID = messageArr[1];
					externalRefNum = messageArr[2];
				} else {
					extRefNumORTxnID = messageArr[2];
					if ("EXT".equals(extRefNumORTxnID)) {
						externalRefNum = messageArr[1];
					} else {
						transferID = messageArr[1];
					}
					checkPin(requestVO, methodName, con, channelUserVO, userPhoneVO, messageArr);
				}
				if (log.isDebugEnabled()) {
					log.debug(methodName, "case 4 transferID=" + transferID + " externalRefNum=" + externalRefNum);
				}
				break;
			}
			case PretupsI.C2S_MESSAGE_LENGTH_LAST_RECHARGE + 2: 
			{   checkPin(requestVO, methodName, con, channelUserVO, userPhoneVO, messageArr);
			transferID = messageArr[1];
			externalRefNum = requestVO.getExternalReferenceNum();
			break;
			}
			default:
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHNL_ERROR_LRCH_INVALIDMESSAGEFORMAT, 0,
						new String[] { requestVO.getActualMessageFormat() }, null);
			}

			requestVO.setTransactionID(transferID);
			if (!BTSLUtil.isNullString(externalRefNum)) {
				requestVO.setTransactionID(externalRefNum);
				transferVO = channelTransferDAO.loadChannelTxnDetails(con, externalRefNum, "EXT",channelUserVO.getUserID(),channelUserVO.getNetworkID(),channelUserVO.getUserType());
			} else {
				requestVO.setTransactionID(transferID);
				transferVO = channelTransferDAO.loadChannelTxnDetails(con, transferID, "TXN",channelUserVO.getUserID(),channelUserVO.getNetworkID(),channelUserVO.getUserType());
			}
			if(transferVO != null && channelUserVO.getUserType().equals(PretupsI.OPERATOR_USER_TYPE) )
			{
				GeographicalDomainDAO geographicalDomainDAO =new GeographicalDomainDAO();
				if(!geographicalDomainDAO.isGeoDomainExistInHierarchy(con,transferVO.getGraphicalDomainCode(),channelUserVO.getUserID()))
				{
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.LAST_RECHARGE_STATUS_NOT_FOUND, 0, new String[] { requestVO.getTransactionID() }, null);
				}
				else
					requestVO.setValueObject(transferVO);
			}
			else if(transferVO != null && channelUserVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE))
			{
				requestVO.setValueObject(transferVO);
			}
			else 
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.LAST_RECHARGE_STATUS_NOT_FOUND, 0, new String[] { requestVO.getTransactionID() }, null);
		}
		catch (BTSLBaseException be) {
			requestVO.setSuccessTxn(false);
			try {
				if (con != null) {
				mcomCon.finalRollback();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
			log.error(methodName, "BTSLBaseException " + be.getMessage());
			log.errorTrace(methodName, be);
			if (be.isKey()) {
				requestVO.setMessageCode(be.getMessageKey());
				requestVO.setMessageArguments(be.getArgs());
			} else {
				requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			}
		} catch (Exception e) {
			requestVO.setSuccessTxn(false);
			try {
				if (con != null) {
					mcomCon.finalRollback();
				}
			} catch (Exception ee) {
				log.errorTrace(methodName, ee);
			}
			log.error(methodName, "BTSLBaseException " + e.getMessage());
			log.errorTrace("process", e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "", "", "",
					"Exception:" + e.getMessage());
			requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		finally {
			if (mcomCon != null) {
				mcomCon.close("ChannelTxnStatusHandler#process");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, " Exited ");
			}
		}
	}

	private void checkPin(RequestVO requestVO, String methodName, Connection con, final ChannelUserVO channelUserVO,UserPhoneVO userPhoneVO, final String[] messageArr) throws SQLException, BTSLBaseException {
		if (userPhoneVO.getPinRequired().equals(PretupsI.YES) && requestVO.isPinValidationRequired()) {
			try {
				ChannelUserBL.validatePIN(con, channelUserVO, messageArr[3]);
			} catch (BTSLBaseException be) {
				log.errorTrace(methodName, be);
				if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
						.equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
					con.commit();
				}
				throw be;
			}
		}
	}
	
	private void loadValidateUserDetails(RequestVO requestVO, OperatorUtilI operatorUtili, ChannelUserVO channelUserVO,
			String extCode, String password, String networkID, String loginID)
					throws BTSLBaseException {
		String methodName="loadValidateUserDetails";
		if(log.isDebugEnabled())
			log.debug(methodName, "Entered: ");
		String channelUserMSISDN = (String)requestVO.getRequestMap().get("MSISDN");
		if(!BTSLUtil.isNullString(channelUserMSISDN)){
			if(requestVO.getRequestMap()!=null && BTSLUtil.NullToString(requestVO.getRequestMap().get("PIN").toString()).length()==0){
				requestVO.getRequestMap().put("PIN",BTSLUtil.decryptText(channelUserVO.getUserPhoneVO().getSmsPin()));
			}
		}
		if(!BTSLUtil.isNullString(extCode)){
			if(requestVO.getRequestMap()!=null && BTSLUtil.NullToString(requestVO.getRequestMap().get("PIN").toString()).length()==0){
				requestVO.getRequestMap().put("PIN",BTSLUtil.decryptText(channelUserVO.getUserPhoneVO().getSmsPin()));                             
			}
		}
		//Ended Here
		if (!networkID.equalsIgnoreCase(channelUserVO.getNetworkID()))
		{
			throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
		}
		if(!BTSLUtil.isNullString(loginID))
		{
	    		if(!operatorUtili.validateTransactionPassword(channelUserVO, password) || BTSLUtil.isNullString(password))
				{
					throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PASSWORD);
				}
	    		if(!loginID.equalsIgnoreCase(channelUserVO.getLoginID()))
				{
					throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
				}
		}
		if(!BTSLUtil.isNullString(extCode))
		{
			if(!extCode.equalsIgnoreCase(channelUserVO.getExternalCode()))
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE);
		}
		if(!BTSLUtil.isNullString(channelUserMSISDN))
		{
			if(!requestVO.getMessageGatewayVO().isUserAuthorizationReqd())
				requestVO.setPinValidationRequired(false);
			else if(BTSLUtil.isNullString(requestVO.getFilteredMSISDN()))
				requestVO.setPinValidationRequired(true);

			if(BTSLUtil.NullToString(requestVO.getRequestMap().get("PIN").toString()).length()==0)
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_PIN_BLANK);    

		}
	}
}
