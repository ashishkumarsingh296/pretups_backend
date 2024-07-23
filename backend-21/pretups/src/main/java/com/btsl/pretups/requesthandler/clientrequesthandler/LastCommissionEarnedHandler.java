/**
 * @(#)LastCommissionEarnedHandler.java 
 * Copyright(c) 2017, Mahindra Comviva 
 * All Rights Reserved
 * 
 * -------------------------------------------------------------------------------------------------
 * Author 				Date 			History
 * -------------------------------------------------------------------------------------------------
 * Anjali Agarwal 	Oct 30, 2017 		Initial Creation
 * This class will be responsible for loading the channel user's commission earned upto last 7 days based on no. of days. 
 * 
 */
package com.btsl.pretups.requesthandler.clientrequesthandler;

import java.sql.Connection;
import java.sql.SQLException;
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
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.clientprocesses.businesslogic.ReverseHirerachyCommisionDAO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;

public class LastCommissionEarnedHandler implements ServiceKeywordControllerI {


	private Log log = LogFactory.getLog(LastCommissionEarnedHandler.class.getName());
	private HashMap requestMap = null;
	private RequestVO requestVO = null;
	private String msisdn = null;
	private static final String XML_TAG_MSISDN="MSISDN";
	private static final String PIN="PIN";

	/**
	 * This method is the entry point into the class.
	 * method process
	 * @param RequestVO
	 */
	@Override
	public void process(RequestVO prequestVO) {

		String methodName="process";
		if(log.isDebugEnabled())
			log.debug(methodName,"Entered....: prequestVO= "+prequestVO.toString());
		requestVO = prequestVO;
		Connection con = null;MComConnectionI mcomCon = null;
		ChannelUserVO channelUserVO =null;
		int maxDays =0;
		boolean messageRequired=false;
		try
		{
			mcomCon = new MComConnection();con=mcomCon.getConnection();
			requestMap = requestVO.getRequestMap();
			msisdn=(String)requestMap.get("MSISDN");
			String noOfDays=(String) requestMap.get("NO_OF_DAYS");
			maxDays=((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_DATEDIFF))).intValue();
			if(BTSLUtil.isNullString(noOfDays))
			{
				noOfDays = String.valueOf(maxDays);
			}
			int days=Integer.parseInt(noOfDays);
			if(days>maxDays || days <0)
			{
				String[] msgargs = new String[2];
				msgargs[0]=noOfDays;
				msgargs[1]=String.valueOf(maxDays);
				throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.LAST_DAYS_ERROR,msgargs);
			}
			//this method validates whether the msisdn, module and action have valid values in the request
			channelUserVO =validate(con);
			ChannelUserVO senderVO = (ChannelUserVO) prequestVO.getSenderVO();
			if(senderVO != null && channelUserVO!=null )
			{
				UserPhoneVO userPhoneVO=null;
				if(!senderVO.isStaffUser())
				{
					userPhoneVO=senderVO.getUserPhoneVO();
				}
				else
				{
					userPhoneVO=senderVO.getStaffUserDetails().getUserPhoneVO();
				}
				validatePin(prequestVO, con, userPhoneVO);
				if(log.isDebugEnabled())
				{
					log.debug(methodName,"Sender UserID()= "+senderVO.getUserID()+", channel User ParentID()="+channelUserVO.getParentID()+", Channel User OwnerID()="+channelUserVO.getOwnerID());
				}
				messageRequired=true;
			}

			//setting the transaction status to true
			requestVO.setSuccessTxn(true);
			long amount = getCommissionDetails(con,channelUserVO,days);
			if(log.isDebugEnabled())
			{
				log.debug(methodName, "Final Amount is:"+amount);
			}
			requestVO.setAmount1(amount);
			String[] msgargs = new String[2];
			msgargs[0]=noOfDays;
			msgargs[1]=PretupsBL.getDisplayAmount(amount);
			requestVO.setMessageArguments(msgargs);
			requestVO.setMessageCode(PretupsErrorCodesI.LAST_COMM_SUCCESS);
			if(messageRequired)
			{
				String key=null;
				UserPhoneVO receiverPhoneVO=channelUserVO.getUserPhoneVO();
				if (receiverPhoneVO==null)
				{
					receiverPhoneVO =new UserDAO().loadUserAnyPhoneVO(con,channelUserVO.getMsisdn());
				}
				PushMessage pushMessage=null;
				key=PretupsErrorCodesI.LAST_COMM_SUCCESS;
				Locale locale1 = new Locale(receiverPhoneVO.getPhoneLanguage(),receiverPhoneVO.getCountry());
				String senderMessage=BTSLUtil.getMessage(locale1,key,msgargs);
				pushMessage=new PushMessage(receiverPhoneVO.getMsisdn(),senderMessage,requestVO.getRequestIDStr(),requestVO.getRequestGatewayCode(),locale1);
				pushMessage.push();
				requestVO.setSenderMessageRequired(false);
			}
		}
		catch(BTSLBaseException be)
		{
			log.error(methodName, "BTSLBaseException " + be);       
			requestVO.setSuccessTxn(false);
			if(be.isKey())
			{
				requestVO.setMessageCode(be.getMessageKey());
				requestVO.setMessageArguments(be.getArgs());
			}
			else
			{
				requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
			}
			try{
				if (con != null)
				{
					mcomCon.finalRollback();
				}
			}
			catch (Exception e){
				log.error(methodName, "Error in closing connection:"+e);
			}     
		}
		catch(Exception ex)
		{
			requestVO.setSuccessTxn( false );
			log.error(methodName, ex );
			//Rollbacking the transaction
			try{
				if (con != null)
				{
					mcomCon.finalRollback();
				}
			} 
			catch ( Exception ee )
			{
				log.error(methodName, "Error in closing connection: "+ee);
			}
			log.error( methodName , "Exception " + ex.getMessage());
			EventHandler.handle( EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LastCommissionEarnedHandler[process]", "", "", "", "Exception:" + ex.getMessage());
			requestVO.setMessageCode( PretupsErrorCodesI.REQ_NOT_PROCESS);
			return;
		}//end of Exception
		finally
		{
			requestVO.setRequestMap(requestMap);
			//clossing database connection
			if (mcomCon != null) {
				mcomCon.close("LastCommissionEarnedHandler#process");
				mcomCon = null;
			}
			if (log.isDebugEnabled())
				log.debug("process", " Exited ");
		}//end of finally
	}

	/*
	 * Description: this method validates if pin is required or not. If required then validate pin.
	 * @param : RequestVO
	 * @param Connection con 
	 * @param UserPhonesVO
	 * throws SQLException
	 * throws BTSLBaseException
	 */
	private void validatePin(RequestVO prequestVO, Connection con, UserPhoneVO userPhoneVO)
			throws SQLException, BTSLBaseException {
		if(userPhoneVO.getPinRequired().equals(PretupsI.YES))
		{
			try
			{
				ChannelUserBL.validatePIN(con,(ChannelUserVO)prequestVO.getSenderVO(),(String)requestMap.get(PIN));
			}
			catch(BTSLBaseException be)
			{
				if(be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) ||  (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK))))
					con.commit();
				throw be;
			}               
		}
	}
	
	/*
	 * Description : To loads the commission amount of user
	 * @param pCon Connection
	 * @return long amount
	 * @throws BTSLBaseException
	 */
	private long getCommissionDetails(Connection con, ChannelUserVO channelUserVO,int noOfDays) throws BTSLBaseException {

		String methodName="getCommissionDetails";
		if(log.isDebugEnabled())
		{
			log.debug(methodName, "Entered whith user: "+channelUserVO.getUserID());
		}
		ReverseHirerachyCommisionDAO rhc=new ReverseHirerachyCommisionDAO();
		long amount = rhc.fetchCommissionOfUserThroughUserId(con,channelUserVO.getUserID(),noOfDays);
		if(log.isDebugEnabled())
		{
			log.debug(methodName, "Exited with amount: "+amount);
		}
		return amount;
	}

	/**
	 * This method is called to validate the values present in the requestMap of the requestVO
	 * The purpose of this method is to validate the values of the msisdn
	 * @param pCon Connection
	 * @return channelUserVO ChannelUserVO
	 * @throws BTSLBaseException
	 */
	private ChannelUserVO validate(Connection pCon) throws BTSLBaseException
	{
		String methodName="validate";
		if(log.isDebugEnabled())
			log.debug(methodName,"Entered.....");

		String[] arr = null;
		ChannelUserVO channelUserVO=null;
		ChannelUserDAO channelUserDAO=null;
		String filteredMsisdn=null;
		try
		{

			channelUserDAO=new ChannelUserDAO();

			if (!BTSLUtil.isNullString(msisdn))
			{
				//filtering the msisdn for country independent dial format
				filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdn);
				if(!BTSLUtil.isValidMSISDN(filteredMsisdn))
				{
					requestMap.put("RES_ERR_KEY",XML_TAG_MSISDN);
					throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.CCE_ERROR_INVALID_MSISDN);
				}
				//load user details by msisdn
				channelUserVO=channelUserDAO.loadChannelUserDetails(pCon,filteredMsisdn);
				if (channelUserVO==null)
				{
					throw new BTSLBaseException(this, methodName , PretupsErrorCodesI.CCE_XML_ERROR_CU_DETAILS_NOT_FOUND4MSISDN);
				}
			}
			else
			{
				requestMap.put("RES_ERR_KEY",XML_TAG_MSISDN);
				throw new BTSLBaseException(this, methodName , PretupsErrorCodesI.CCE_XML_ERROR_MISSING_MANDATORY_VALUE);
			}

			String msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
			requestVO.setFilteredMSISDN(filteredMsisdn);
			//checking whether the msisdn prefix is valid in the network
			NetworkPrefixVO networkPrefixVO =(NetworkPrefixVO)NetworkPrefixCache.getObject(msisdnPrefix);
			if(networkPrefixVO == null)
			{
				arr = new String[]{filteredMsisdn};
				throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.CCE_XML_ERROR_UNSUPPORTED_NETWORK, arr);
			}
			String networkCode=networkPrefixVO.getNetworkCode();
			if(networkCode!=null && !networkCode.equalsIgnoreCase(((UserVO)requestVO.getSenderVO()).getNetworkID()))
			{
				throw new BTSLBaseException(this,methodName, PretupsErrorCodesI.CCE_XML_ERROR_NETWORK_NOT_MATCHING_REQUEST);
			}
			return channelUserVO;
		}
		catch(Exception e)
		{
			log.error(methodName, "Exception " + e); 
			EventHandler.handle( EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LastCommissionEarnedHandler[validate]", "", "", "", "Exception:" + e.getMessage());
			throw new BTSLBaseException("LastCommissionEarnedHandler", methodName, PretupsErrorCodesI.REQ_NOT_PROCESS);
		}	
		finally
		{
			if(log.isDebugEnabled())
				log.debug(methodName,"Exiting ");
		}
	}

}
