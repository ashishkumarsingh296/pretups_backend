package com.client.pretups.user.requesthandler;

/**
 * @(#)LastXTransferServiceWiseController.java
 * Copyright(c) 2015, MComviva tech. ltd.
 * All Rights Reserved
 *
 * <this class is used to get last 'X' number of Transfers details of C2C, C2S & O2C type of a user service wise>
 *-------------------------------------------------------------------------------------------------
 * Author                        Date            History
 *-------------------------------------------------------------------------------------------------
 *  Harsh Dixit            	Jul,22,2014        Initital Creation
 *-------------------------------------------------------------------------------------------------
 *	
 */
 
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCacheVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.KeyArgumentVO;

public class LastXTransferServiceWiseController implements ServiceKeywordControllerI {
	private  Log _log = LogFactory.getLog(LastXTransferServiceWiseController.class.getName());
	
	
	// this method will first validate the user details(PIN).
	//Load the transfer details depending upon the transaction type,service type,c2c_inout.
	//sorts them in the descending order with createdON time.
	//prepares the message to be pushed to user. 
	
	public void process(RequestVO p_requestVO)
	{
		final String methodName="process";
		if (_log.isDebugEnabled())
			_log.debug(methodName, " Entered " + p_requestVO);
		ChannelTransferDAO channelTransferDAO=null;
		ChannelUserDAO _channelUserDAO = null;
		ArrayList<C2STransferVO> transfersList =null;
		UserPhoneVO userPhoneVO=null;
		HashMap map=null;
		
		String txnType=null;
		String txnSubType=null;
		int lastNoOfTxn =0;
		int lastNoOfDays=0;
		String c2cInOut=null;
		String msisdn1=null;
		Connection con  = null;MComConnectionI mcomCon = null;
		ChannelUserVO channelUserVO =null;
		try
		{
			mcomCon = new MComConnection();con=mcomCon.getConnection();
			map=p_requestVO.getRequestMap();

			_channelUserDAO = new ChannelUserDAO();
			msisdn1=p_requestVO.getFilteredMSISDN();
			channelUserVO=_channelUserDAO.loadChannelUserDetails(con, msisdn1);
			channelUserVO.setActiveUserID(channelUserVO.getUserID());
			userPhoneVO=channelUserVO.getUserPhoneVO();
			if(channelUserVO.isStaffUser()){
				userPhoneVO=channelUserVO.getStaffUserDetails().getUserPhoneVO();
			}
			String []messageArr = p_requestVO.getRequestMessageArray();
			lastNoOfTxn=((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LAST_X_TRF_DETAILS_NO))).intValue();
			lastNoOfDays=((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LAST_X_TRF_DETAILS_DAYS))).intValue();
			//need to load this from System preference 
			//[LSTXTXNSW, 720322222, 1357, O2C, TRANSFER_SUBTYPE,CINOUT]

			if(messageArr.length==5 && BTSLUtil.isStringIn(p_requestVO.getRequestGatewayType(),((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SMS_PIN_BYPASS_GATEWAY_TYPE))))
			{
				txnType=messageArr[2];
				txnSubType=messageArr[3];
				c2cInOut=messageArr[4];

			}
			else if(messageArr.length==6)
			{
				txnType=messageArr[3];
				txnSubType=messageArr[4];
				c2cInOut=messageArr[5];
				this.validateSenderPin(con, p_requestVO, userPhoneVO);

			}
			else
			{
				throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.C2S_ERROR_LAST_TRSFER_INVALIDMESSAGEFORMAT,0,new String[]{p_requestVO.getActualMessageFormat()},null);
			}
			
			_log.debug(methodName, "userId:"+ channelUserVO.getActiveUserID() + "lastNoOfTxn: " + lastNoOfTxn + " txnType: "+txnType + "lastNoOfDays: "+ lastNoOfDays + " txnSubType: " + txnSubType+"c2cInOut: "+c2cInOut);
			channelTransferDAO=new ChannelTransferDAO();
			transfersList=channelTransferDAO.loadLastXTransfersDetails(con,  channelUserVO.getActiveUserID(), lastNoOfTxn,  lastNoOfDays,  txnType ,  txnSubType ,  c2cInOut);
			if(transfersList!=null && !transfersList.isEmpty())
			{
				this.formatLastXTransferServiceWiseForSMS(transfersList,p_requestVO,lastNoOfTxn);
			}
			else if(lastNoOfDays==0)
			{
				p_requestVO.setMessageCode(PretupsErrorCodesI.LAST_TRANSFER_NO_TRANSACTION_DONE);

			}
			else
			{
				String []arg = new String[]{Integer.toString(lastNoOfDays)};
				p_requestVO.setMessageArguments(arg);
				p_requestVO.setMessageCode(PretupsErrorCodesI.LASTX_TRANSFER_SERVICEWISE_NO_TRANSACTION_DONE);
			}
			map.put("TRANSFERLIST",transfersList);
		} 
		catch (BTSLBaseException be)
		{
			p_requestVO.setSuccessTxn(false);
			_log.error(methodName, "BTSLBaseException " + be.getMessage());
			_log.errorTrace(methodName, be);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"LastXTransferServiceWiseController[+"+methodName+"]","","","","BTSL Exception:"+be.getMessage());
			if(be.isKey())
			{
			    p_requestVO.setMessageCode(be.getMessageKey());
			    p_requestVO.setMessageArguments(be.getArgs());
			}
			else
			{
			    p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
			    return;
			}
		}
		catch (Exception e)
		{
			p_requestVO.setSuccessTxn(false);
			_log.error(methodName, "Exception ::" + e.getMessage());
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"LastXTransferServiceWiseController [+"+methodName+"]","","","","Exception:"+e.getMessage());
			p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
			return;
		}finally{
			if(mcomCon != null)
			{
				mcomCon.close("LastXTransferServiceWiseController#process");
				mcomCon=null;
				}
		}
		_log.debug(methodName, " Exited ");
	}
	
	/**
	 * This  method basically prepares the message in the format to be pushed to the user.
	 * @author harsh.dixit
	 * @param p_transferList ArrayList
	 * @param p_requestVO RequestVO
	 * @param int xLastTxn no. of details to be pushed in message.
	 * @throws BTSLBaseException
	 */
	private void formatLastXTransferServiceWiseForSMS(ArrayList p_transferList,RequestVO p_requestVO, int xLastTxn) throws BTSLBaseException
	{
		final String methodName="formatLastXTransferServiceWiseForSMS";
		if (_log.isDebugEnabled())
			_log.debug(methodName, "Entered: p_transferList size:" + p_transferList.size() ,"xLastTxn:" + xLastTxn);
		try
		{
		    String[] arr= new String[1];
		    ArrayList argList= new ArrayList();
		    KeyArgumentVO argumentVO = null;
		    String[] messageArray=null;	
		    String sumMsgNo=null;
		    int count=1;
		    boolean multipleSms=((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LAST_TRF_MULTIPLE_SMS))).booleanValue();
		    if(xLastTxn>p_transferList.size())
		    	xLastTxn=p_transferList.size();
		    //if multipleSms=false then send info of all transactions in one sms otherwise send 1 sms for each transaction info.
		    if(!multipleSms)
		    {
		        for (int i = 0;i < xLastTxn;i++)
				{
		        	C2STransferVO c2sTransferVO = (C2STransferVO) p_transferList.get(i);
		            sumMsgNo=Integer.toString(count);
		            if(PretupsI.LOOKUP_CHANNEL_TRANSFER_TYPE_WITHDRAW.equals(c2sTransferVO.getServiceName()))
		            {
		            	c2sTransferVO.setReceiverMsisdn(c2sTransferVO.getSenderMsisdn());			//set the sender msisdn in the sender msisdn
		            }
		            //message format: 1- ID:[0], MSISDN:[1], Status:[2], Type:[3], Amount:[4], Post Balance [5] 
					messageArray=new String[]{sumMsgNo,c2sTransferVO.getTransferID(),c2sTransferVO.getReceiverMsisdn(),c2sTransferVO.getStatus(),c2sTransferVO.getType(),PretupsBL.getDisplayAmount(c2sTransferVO.getTransferValue()),PretupsBL.getDisplayAmount(c2sTransferVO.getSenderPostBalance()),c2sTransferVO.getTransferDateTime().toString()};
					argumentVO=new KeyArgumentVO();
					argumentVO.setKey(PretupsErrorCodesI.LAST_XTRF_SUBKEY);
					argumentVO.setArguments(messageArray);
					argList.add(argumentVO); 
					count++;
				}
			    arr[0]=BTSLUtil.getMessage(p_requestVO.getSenderLocale(),argList);
			    p_requestVO.setMessageArguments(arr);
			    p_requestVO.setMessageCode(PretupsErrorCodesI.LAST_XTRF_MAIN_KEY);
		    }
		    else
		    {
		        for (int i = 0;i < xLastTxn;i++)
				{
			        C2STransferVO c2sTransferVO = (C2STransferVO) p_transferList.get(i);
			        sumMsgNo=Integer.toString(count);
		            if(PretupsI.LOOKUP_CHANNEL_TRANSFER_TYPE_WITHDRAW.equals(c2sTransferVO.getServiceName()))
		            {
		            	c2sTransferVO.setReceiverMsisdn(c2sTransferVO.getSenderMsisdn());			//set the sender msisdn in the sender msisdn
		            }

		            //message format: 1- ID:[0], MSISDN:[1], Status:[2], Type:[3], Amount:[4], Post Balance [5] 
		            messageArray=new String[]{sumMsgNo,c2sTransferVO.getTransferID(),c2sTransferVO.getReceiverMsisdn(),c2sTransferVO.getStatus(),c2sTransferVO.getType(),PretupsBL.getDisplayAmount(c2sTransferVO.getTransferValue()),PretupsBL.getDisplayAmount(c2sTransferVO.getSenderPostBalance()),c2sTransferVO.getTransferDateTime().toString()};
				    String senderMessage=BTSLUtil.getMessage(p_requestVO.getSenderLocale(),PretupsErrorCodesI.LAST_TRF_DETAILS,messageArray);
				    //changed because in the case of stk request content type is null
				    if(i<xLastTxn-1 &&  p_requestVO.isSuccessTxn()  && (  BTSLUtil.isNullString(p_requestVO.getReqContentType())|| p_requestVO.getReqContentType().indexOf("xml")!=-1 || p_requestVO.getReqContentType().indexOf("XML")!=-1 || p_requestVO.getReqContentType().indexOf("plain")!=-1 || p_requestVO.getReqContentType().indexOf("PLAIN")!=-1) && p_requestVO.isSenderMessageRequired())
				    {
				    	ServiceKeywordCacheVO serviceKeywordCacheVO=ServiceKeywordCache.getServiceKeywordObj(p_requestVO);
				    	if(!PretupsI.YES.equals(serviceKeywordCacheVO.getExternalInterface()))
				    	{
				    		PushMessage pushMessage=new PushMessage(p_requestVO.getFilteredMSISDN(),senderMessage,p_requestVO.getRequestIDStr(),p_requestVO.getRequestGatewayCode(),p_requestVO.getSenderLocale());
				    		pushMessage.push();
				    	}
				    }
				    else
				    {
					    p_requestVO.setMessageArguments(messageArray);
					    p_requestVO.setMessageCode(PretupsErrorCodesI.LAST_TRF_DETAILS);
				    }
				}
		    }
		}
		catch (Exception e)
		{
			_log.error(methodName, "Exception " + e.getMessage());
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"LastXTransferServiceWiseController[+"+methodName+"]","","","","Exception:"+e.getMessage());
			throw new BTSLBaseException("LastXTransferServiceWiseRequestHandler", methodName, PretupsErrorCodesI.REQ_NOT_PROCESS);
		}
		finally
		{
			if (_log.isDebugEnabled())
				_log.debug(methodName, "Exited:");
		}
	}
	public void validateSenderPin(Connection con,RequestVO requestVO,UserPhoneVO userPhoneVO) throws BTSLBaseException{
		String []messageArr = requestVO.getRequestMessageArray();
		if(userPhoneVO.getPinRequired().equals(TypesI.YES) && !BTSLUtil.isStringIn(requestVO.getRequestGatewayType(),((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SMS_PIN_BYPASS_GATEWAY_TYPE))))
		{
			try
			{
				ChannelUserBL.validatePIN(con,((ChannelUserVO)requestVO.getSenderVO()),messageArr[2]);
			}
			catch(BTSLBaseException be)
			{
				if(be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) ||  (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))){
					try{con.commit();}catch (Exception e) {_log.error("validateSenderPin", "Exception " + e.getMessage());}
				}
				throw be;
			}				
		}
	}
	
}
