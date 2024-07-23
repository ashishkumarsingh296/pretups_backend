package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupBL;
import com.btsl.pretups.channel.logging.BalanceLogger;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.interfaces.businesslogic.InterfaceVO;
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.pretups.network.businesslogic.NetworkInterfaceModuleCache;
import com.btsl.pretups.network.businesslogic.NetworkInterfaceModuleVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;


public class C2SLiteRechargeController extends C2SPrepaidController{

	private static Log _log = LogFactory.getLog(C2SLiteRechargeController.class.getName());	
	@Override
	public void processValidationRequest() throws BTSLBaseException,SQLException
	{    StringBuilder loggerValue= new StringBuilder(); 
		Connection con=null;
		MComConnectionI mcomCon = null;
		final String methodName = "processValidationRequest";
		if(_log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered and performing validations for transfer ID=");
			loggerValue.append(_transferID);
			loggerValue.append(" ");
			loggerValue.append(_c2sTransferVO.getModule());
			loggerValue.append(" ");
			loggerValue.append(_c2sTransferVO.getReceiverNetworkCode());
			loggerValue.append(" ");
			loggerValue.append(_type);
			_log.debug(methodName,loggerValue);
		}  
		try
		{
			NetworkInterfaceModuleVO networkInterfaceModuleVOS=(NetworkInterfaceModuleVO)NetworkInterfaceModuleCache.getObject(_c2sTransferVO.getModule(),_c2sTransferVO.getReceiverNetworkCode(),_type);
			_intModCommunicationTypeS=networkInterfaceModuleVOS.getCommunicationType();
			_intModIPS=networkInterfaceModuleVOS.getIP();
			_intModPortS=networkInterfaceModuleVOS.getPort();
			_intModClassNameS=networkInterfaceModuleVOS.getClassName();
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();

			_itemList=new ArrayList();
			_itemList.add(_senderTransferItemVO);
			_itemList.add(_receiverTransferItemVO);
			_c2sTransferVO.setTransferItemList(_itemList);
			
			_receiverTransferItemVO.setServiceClassCode((String)_requestVO.getRequestMap().get("SERVICECLASS"));
			_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
			//Get the service Class ID based on the code
			PretupsBL.validateServiceClassChecks(con,_receiverTransferItemVO,_c2sTransferVO,PretupsI.C2S_MODULE,_requestVO.getServiceType());
			_receiverVO.setServiceClassCode(_receiverTransferItemVO.getServiceClass());
			
			//validate sender receiver service class,validate transfer value
			PretupsBL.validateTransferRule(con,_c2sTransferVO,PretupsI.C2S_MODULE);
			
			//validate receiver limits after Interface Validations
			PretupsBL.validateRecieverLimits(con,_c2sTransferVO,PretupsI.TRANS_STAGE_AFTER_INVAL,PretupsI.C2S_MODULE);

			//calculate card group details
			CardGroupBL.calculateCardGroupDetails(con,_c2sTransferVO,PretupsI.C2S_MODULE,true);
			
			TransactionLog.log(_transferID,_requestIDStr,_receiverMSISDN,_receiverVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"After Card Group Set Id="+_c2sTransferVO.getCardGroupSetID()+" Code"+_c2sTransferVO.getCardGroupCode()+" Card ID="+_c2sTransferVO.getCardGroupID()+" Access fee="+_c2sTransferVO.getReceiverAccessFee()+" Tax1 ="+_c2sTransferVO.getReceiverTax1Value()+" Tax2="+_c2sTransferVO.getReceiverTax1Value()+" Bonus="+_c2sTransferVO.getReceiverBonusValue()+" Val Type="+_c2sTransferVO.getReceiverValPeriodType()+" Validity="+_c2sTransferVO.getReceiverValidity()+" Talk Time="+_c2sTransferVO.getReceiverTransferValue(),PretupsI.TXN_LOG_STATUS_SUCCESS,"");
			
			// Here the code for debiting the user account will come
			_userBalancesVO=ChannelUserBL.debitUserBalanceForProduct(con,_transferID,_c2sTransferVO);
			
			//Update Transfer Out Counts for the sender
			ChannelTransferBL.increaseC2STransferOutCounts(con,_c2sTransferVO,true);
			
			/*if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,_c2sTransferVO.getNetworkCode()))
			{
				  ChannelTransferBL.increaseUserOTFCounts(con, _c2sTransferVO, _channelUserVO);
			}*/
			
			_c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
			_senderTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
			
			//populate payment and service interface details
			populateServiceInterfaceDetails(con,PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);
            

			//End of single currency request change
			
			//Method to insert the record in c2s transfer table
			//consolidated for logger
			if(!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue())
			{
				ChannelTransferBL.addC2STransferDetails(con,_c2sTransferVO);
			}
			_transferDetailAdded=true;
			
			//Commit the transaction and relaease the locks
			try { 
				mcomCon.finalCommit();
			} catch(Exception be) 
			{
				_log.errorTrace(methodName, be);
			}
			if(mcomCon != null)
			{
				mcomCon.close("C2SLiteRechargeController#processValidationRequest");
				mcomCon=null;
			}
			con=null;
            
			TransactionLog.log(_transferID,_requestIDStr,_receiverMSISDN,_receiverVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Marked Under process",PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"" );

			//Log the details if the transfer Details were added i.e. if User was debitted
			if(_transferDetailAdded) {
				BalanceLogger.log(_userBalancesVO);
			}

			//Push Under Process Message to Sender and Reciever , this might have to be implemented on flag basis whether to send message or not
			if(_c2sTransferVO.isUnderProcessMsgReq()&& _receiverMessageSendReq && !BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(),_notAllowedRecSendMessGatw)&&!"ALL".equals(_notAllowedRecSendMessGatw)) {
				(new PushMessage(_receiverMSISDN,getReceiverUnderProcessMessage(),_transferID,_c2sTransferVO.getRequestGatewayCode(),_receiverLocale)).push();
			}
			
			//If request is taking more time till topup of subscriber than reject the request.
			InterfaceVO interfaceVO=(InterfaceVO)NetworkInterfaceModuleCache.getObject(_receiverTransferItemVO.getInterfaceID());
            if((System.currentTimeMillis()-_c2sTransferVO.getRequestStartTime())>interfaceVO.getTopUpExpiryTime())
            {
            	EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,"C2SPrepaidController[run]",_transferID,_senderMSISDN,_senderNetworkCode,"Exception: System is taking more time till topup");
            	throw new BTSLBaseException("C2SPrepaidController",methodName,PretupsErrorCodesI.C2S_ERROR_EXCEPTION_TKING_TIME_TILL_TOPUP);
            }
            interfaceVO=null;
            if(!_requestVO.isToBeProcessedFromQueue())
            {
		if(_c2sTransferVO.getMsgGatewayFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON) || _processedFromQueue)
			{
				//create new Thread
				Thread _controllerThread=new Thread(this);
				_controllerThread.start();
				_oneLog = false;
			}
            }
		}
		catch(BTSLBaseException be)
		{
			if(con!=null) {
				mcomCon.finalRollback();
			}
			if(_recValidationFailMessageRequired) 
			{
				if(_c2sTransferVO.getReceiverReturnMsg()==null || !((BTSLMessages)_c2sTransferVO.getReceiverReturnMsg()).isKey()) {
					_c2sTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.C2S_RECEIVER_FAIL),new String[]{String.valueOf(_transferID),PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount())}));
				}
			}
			_c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			_receiverTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			if(BTSLUtil.isNullString(_c2sTransferVO.getErrorCode()))
			{
				if(be.isKey()) {
					_c2sTransferVO.setErrorCode(be.getMessageKey());
				} else {
					_c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
				}
			}
			loggerValue.setLength(0);
			loggerValue.append("Getting BTSL Base Exception:");
			loggerValue.append(be.getMessage());
			_log.error("C2SPrepaidController[processValidationRequest]",loggerValue);
			if(_transferDetailAdded)
			{
				if(mcomCon==null) {
					mcomCon = new MComConnection();
					con=mcomCon.getConnection();
				}
				
				_userBalancesVO=null;
				//Update the sender back for fail transaction
				updateSenderForFailedTransaction(con);
				
				//So that we can update with final status here
				//consolidated for logger
				if(!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue())
				{
					addEntryInTransfers(con);
				}	
				
				mcomCon.finalCommit();
				//Log the details if the transfer Details were added i.e. if User was creditted
				if(_creditBackEntryDone) {
					BalanceLogger.log(_userBalancesVO);
				}		
			}
			throw be;
		}
		catch(Exception e)
		{
			_log.errorTrace(methodName, e);
			if(con!=null) {
				mcomCon.finalRollback();
			}
			_c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			
		if(_recValidationFailMessageRequired) 
			{
				if(_c2sTransferVO.getReceiverReturnMsg()==null || !((BTSLMessages)_c2sTransferVO.getReceiverReturnMsg()).isKey()) {
					_c2sTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.C2S_RECEIVER_FAIL),new String[]{String.valueOf(_transferID),PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount())}));
				}
			}
			if(BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
				_c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			}
			if(_transferDetailAdded)
			{
				if(mcomCon==null) {
					mcomCon = new MComConnection();
					con=mcomCon.getConnection();
				}
				
				_userBalancesVO=null;
				//Update the sender back for fail transaction
				updateSenderForFailedTransaction(con);
				
				//So that we can update with final status here
				//consolidated for logger
				if(!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue())
				{
					
					addEntryInTransfers(con);
				}	
				
				mcomCon.finalCommit();
				//Log the details if the transfer Details were added i.e. if User was creditted
				if(_creditBackEntryDone) {
					BalanceLogger.log(_userBalancesVO);
				}		
			}
			throw new BTSLBaseException("C2SPrepaidController",methodName,PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
 		}
		finally
		{
			if(mcomCon != null)
			{
				mcomCon.close("C2SLiteRechargeController#processValidationRequest");
				mcomCon=null;
			}
			con=null;
		}
	}	
	
	
	
}
