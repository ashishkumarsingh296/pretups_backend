
package com.btsl.pretups.channel.transfer.requesthandler;
import java.security.SecureRandom;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.loadcontroller.LoadController;
import com.btsl.loadcontroller.LoadControllerI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.adjustments.businesslogic.AdjustmentsVO;
import com.btsl.pretups.adjustments.businesslogic.DiffCalBL;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupCache;
import com.btsl.pretups.channel.logging.BalanceLogger;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferItemVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayCache;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.grouptype.businesslogic.GroupTypeProfileVO;
import com.btsl.pretups.logging.OneLineTXNLog;
import com.btsl.pretups.logging.SMSChargingLog;
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.network.businesslogic.MSISDNPrefixInterfaceMappingCache;
import com.btsl.pretups.network.businesslogic.MSISDNPrefixInterfaceMappingVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.routing.master.businesslogic.SubscriberRoutingControlCache;
import com.btsl.pretups.routing.master.businesslogic.SubscriberRoutingControlVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.pretups.vastrix.businesslogic.ServiceSelectorInterfaceMappingCache;
import com.btsl.pretups.vastrix.businesslogic.ServiceSelectorInterfaceMappingVO;
import com.btsl.pretups.whitelist.businesslogic.WhiteListVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;


public class C2SBulkReversalController implements ServiceKeywordControllerI,Runnable {

	private static Log _log = LogFactory.getLog(C2SBulkReversalController.class.getName());
	private C2STransferVO _c2sTransferVO=null;
	private C2STransferVO _c2sTransferVORoam=null;
	private TransferItemVO _senderTransferItemVO=null;
	private TransferItemVO _receiverTransferItemVO=null;
	private String _senderMSISDN;
	private String _receiverMSISDN;
	private ChannelUserVO _channelUserVO;
	private ReceiverVO _receiverVO;
	private String _senderSubscriberType;
	private String _senderNetworkCode;
	private Date _currentDate=null;
	private String _requestIDStr;
	private String _transferID;
	private ArrayList _itemList=null;
	private boolean _transferDetailAdded=false;
	private boolean _isCounterDecreased=false;
	private String _type;
	private String _serviceType;
	private boolean _finalTransferStatusUpdate=false;
	private boolean _transferEntryReqd=false;
	private boolean _decreaseTransactionCounts=false;
	private UserBalancesVO _userBalancesVO=null;
	private boolean _creditBackEntryDone=false;
    private Locale _senderLocale=null;
	private RequestVO _requestVO=null;
	private String _imsi=null;

	private String _notAllowedSendMessGatw;
	private String _notAllowedRecSendMessGatw;
	private boolean _receiverInterfaceInfoInDBFound=false;
	private String _receiverAllServiceClassID=PretupsI.ALL;
	private String _externalID=null;
	private String _interfaceStatusType=null;
	private String _receiverSubscriberType=null;
	private Locale _receiverLocale=null;
	
	public static OperatorUtilI _operatorUtil=null;

	private static int _transactionIDCounter=0;
	private static int  _prevMinut=0;
	private static SimpleDateFormat _sdfCompare = new SimpleDateFormat ("mm");

	private String _senderPushMessageMsisdn=null;
	private boolean _oneLog=true;

	

	//Loads operator specific class
	static
	{
		String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
		try
		{
			_operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
		}
		catch(Exception e)
		{
			_log.errorTrace("static",e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2SBulkReversalController[initialize]","","","","Exception while loading the class at the call:"+e.getMessage());
		}
	}	
	public  C2SBulkReversalController()
	{
		_c2sTransferVO=new C2STransferVO();
		_c2sTransferVORoam=new C2STransferVO();
		
		_currentDate=new Date();
		
		_notAllowedSendMessGatw=BTSLUtil.NullToString(Constants.getProperty("PRE_REVERSAL_SEN_MSG_NOT_REQD_GW"));
		_notAllowedRecSendMessGatw = BTSLUtil.NullToString(Constants.getProperty("PRE_REVERSAL_REC_MSG_NOT_REQD_GW"));
		
	}

	/**
	 * Method to process the request of the C2S pre-paid reversal 
	 * @param object of the RequestVO
	 */	
	public void process(RequestVO p_requestVO) 
	{
		final String METHOD_NAME="process";
		StringBuilder loggerValue= new StringBuilder(); 
		if(_log.isDebugEnabled())
			{loggerValue.setLength(0);
			loggerValue.append("Entered for Request ID=");
			loggerValue.append(p_requestVO.getRequestID());
			loggerValue.append(" MSISDN=");
			loggerValue.append(p_requestVO.getFilteredMSISDN());
			_log.debug("process",p_requestVO.getRequestIDStr(),loggerValue);
			}
		Connection con=null;MComConnectionI mcomCon = null;
		try
		{
			_requestVO=p_requestVO;
			_channelUserVO=(ChannelUserVO)p_requestVO.getSenderVO();
			TransactionLog.log("",p_requestVO.getRequestIDStr(),p_requestVO.getFilteredMSISDN(),_channelUserVO.getNetworkID(),PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_TXNSTAGE_RECIVED,"Received Request From Receiver",PretupsI.TXN_LOG_STATUS_SUCCESS,"");
			_senderLocale=p_requestVO.getSenderLocale();
			_senderNetworkCode=_channelUserVO.getNetworkID();

			//Populating C2STransferVO from the request VO
			populateVOFromRequest(p_requestVO);
			_requestIDStr=p_requestVO.getRequestIDStr();
			_type=p_requestVO.getType();
			_serviceType=PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL;
					
					

			//Getting oracle connection
			mcomCon = new MComConnection();con=mcomCon.getConnection();
			String[] p_requestArr = p_requestVO.getDecryptedMessage().replaceAll("\\s+", " ").split(" ");
			String oldTxnId = p_requestArr[1];
			
			 _c2sTransferVO.setOldTxnId(oldTxnId.trim());
			
//			p_requestVO.setReceiverLocale(Integer.parseInt(p_requestArr[4]));
			p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestArr[2]));
			p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestArr[3]));
			_c2sTransferVO.setReceiverAllServiceClassID(_receiverAllServiceClassID);
		    //_operatorUtil.validateC2SPrepaidReversalRequest(con,_c2sTransferVO,p_requestVO);
			
			C2STransferVO oldc2sTransferVO=_operatorUtil.getC2STransferVOFromTxnID(con,_c2sTransferVO,p_requestVO);
			
			_c2sTransferVO.setGifterMSISDN(oldc2sTransferVO.getGifterMSISDN());
			_c2sTransferVO.setIsMNP(oldc2sTransferVO.getIsMNP());
			
			
			if(BTSLUtil.isNullString(p_requestVO.getReceiverMsisdn())){
				ReceiverVO receiverVO=new ReceiverVO();
				receiverVO.setSubscriberType(PretupsI.INTERFACE_CATEGORY_PRE);
				PretupsBL.validateMsisdn(con,receiverVO,_c2sTransferVO.getRequestID(),oldc2sTransferVO.getReceiverMsisdn());
				_c2sTransferVO.setReceiverVO(receiverVO);
				_receiverVO=receiverVO;
				
			}
			_receiverLocale=p_requestVO.getReceiverLocale();
			_senderLocale=p_requestVO.getSenderLocale();
			
			_senderMSISDN=(_channelUserVO.getUserPhoneVO()).getMsisdn();
			_senderPushMessageMsisdn=p_requestVO.getMessageSentMsisdn();
			_receiverMSISDN=((ReceiverVO)_c2sTransferVO.getReceiverVO()).getMsisdn();
			_c2sTransferVO.setReceiverMsisdn(_receiverMSISDN);
			_c2sTransferVO.setReceiverNetworkCode(_receiverVO.getNetworkCode());
			_c2sTransferVO.setTxnType(PretupsI.TXNTYPE_X);
			_c2sTransferVO.setSenderNetworkCode(_senderNetworkCode);
			_c2sTransferVO.setGrphDomainCode(_channelUserVO.getGeographicalCode());
			_c2sTransferVO.setRequestStartTime(p_requestVO.getRequestStartTime());
			_receiverSubscriberType=_receiverVO.getSubscriberType();
			
			if(PretupsI.YES.equalsIgnoreCase(_channelUserVO.getOutSuspened()))
				throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CHNL_ERROR_SENDER_OUT_SUSPEND_PRE_REVERSAL);

			_c2sTransferVO.setUnderProcessCheckReqd(p_requestVO.getMessageGatewayVO().getRequestGatewayVO().getUnderProcessCheckReqd());
			
			  if (!_senderNetworkCode.equals(_c2sTransferVO.getReceiverNetworkCode()) && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ALLOW_ROAM_RECHARGE))).booleanValue()) 
	            {
	            	_c2sTransferVO.setIsRoam(true);
	            }
		
			if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SKEY_REQUIRED)).booleanValue())
			{
				//It is the case of SKey forwarding request to generate the SKEY 
				processSKeyGen(con);
				//Set Sender Message and throw Exception
			}
			else
			{
				//forwarding request to process the transfer request
				processTransfer(con);

				_c2sTransferVO.setModifiedBy(PretupsI.C2S_REV_BATCH);
				
				p_requestVO.setTransactionID(_transferID);
				_receiverVO.setLastTransferID(_transferID);
				TransactionLog.log(_transferID,p_requestVO.getRequestIDStr(),p_requestVO.getFilteredMSISDN(),_channelUserVO.getNetworkID(),PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Generated Transfer ID",PretupsI.TXN_LOG_STATUS_SUCCESS,"Source Type="+_c2sTransferVO.getSourceType()+" Gateway Code="+_c2sTransferVO.getRequestGatewayCode()+" ,Old Txn Id="+_c2sTransferVO.getOldTxnId());
			//	_operatorUtil.validateReversalOldTxnId(_c2sTransferVO,p_requestVO,oldc2sTransferVO);
				 copyOldTxnToNew(_c2sTransferVO, oldc2sTransferVO);
				p_requestVO.setReqSelector(_c2sTransferVO.getSelectorCode());
				
				populateServiceInterfaceDetails(con,PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION);

				_c2sTransferVO.setReceiverAllServiceClassID(_receiverAllServiceClassID);

				//Change is done for ID=SUBTYPVALRECLMT
				//This chenge is done to set the receiver subscriber type in transfer VO
				//This will be used in validate ReceiverLimit method of PretupsBL when receiverTransferItemVO is null
				_c2sTransferVO.setReceiverSubscriberType(_receiverSubscriberType);
				//setting validation status
				_senderTransferItemVO.setValidationStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);

				//commiting transaction and closing the transaction as it is not requred
				try {con.commit();} catch(Exception e){ 
					loggerValue.setLength(0);
					loggerValue.append("Exception:");
					loggerValue.append( e.getMessage());
					_log.error(METHOD_NAME, loggerValue);
					_log.errorTrace(METHOD_NAME, e);
					throw new BTSLBaseException("C2SBulkReversalController","process",PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
				}
				if(mcomCon != null){mcomCon.close("C2SBulkReversalController#process");mcomCon=null;}
				con=null;
				if(!_channelUserVO.isStaffUser())
				{
					(_channelUserVO.getUserPhoneVO()).setLastTransferID(_transferID);
					(_channelUserVO.getUserPhoneVO()).setLastTransferType(PretupsI.TRANSFER_TYPE_C2S);
				}
				else
				{
					(_channelUserVO.getStaffUserDetails().getUserPhoneVO()).setLastTransferID(_transferID);
					(_channelUserVO.getStaffUserDetails().getUserPhoneVO()).setLastTransferType(PretupsI.TRANSFER_TYPE_C2S);
				}
				//Checking the flow type of the transfer request, whether it is common or thread 
				processValidationRequest();					
				run();
				String[] messageArgArray={(_receiverVO.getSid()!=null)?_receiverVO.getSid():_receiverMSISDN,_transferID,PretupsBL.getDisplayAmount(_c2sTransferVO.getTransferValue()),PretupsBL.getDisplayAmount(_senderTransferItemVO.getPostBalance()),PretupsBL.getDisplayAmount(_senderTransferItemVO.getPreviousBalance()),String.valueOf(_receiverTransferItemVO.getValidity()),PretupsBL.getDisplayAmount(_receiverTransferItemVO.getPostBalance()),String.valueOf(_receiverTransferItemVO.getNewGraceDate()),PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverAccessFee()),_c2sTransferVO.getSubService()};
				p_requestVO.setMessageArguments(messageArgArray);

			}
		}
		catch(BTSLBaseException be)
		{	loggerValue.setLength(0);
	      	loggerValue.append("BTSLException:");
		    loggerValue.append(be.getMessage());
			_log.error(METHOD_NAME, loggerValue );
			_log.errorTrace(METHOD_NAME, be);
			p_requestVO.setSuccessTxn(false);
			try
			{
				if(_receiverVO!=null && _receiverVO.isUnmarkRequestStatus())
				{	//getting database connection if it is not already there
					if(mcomCon==null){
						mcomCon = new MComConnection();con=mcomCon.getConnection();
					}
					//Setting users transaction status to completed at the start it was marked underprocess
					PretupsBL.unmarkReceiverLastRequest(con,p_requestVO.getRequestIDStr(),_receiverVO);
				}
			}
			catch(Exception e)
			{      loggerValue.setLength(0);
	      	       loggerValue.append("Exception:");
		           loggerValue.append(e.getMessage());
				_log.error(METHOD_NAME, loggerValue);
				_log.errorTrace(METHOD_NAME, e);
				_c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			}

			//setting transaction status to Fail
			_c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			
				//setting receiver return message
					if(_transferID!=null)
					{
					    _c2sTransferVO.setSenderReturnMessage(getSenderFailMessage());
					}
					
				//getting return message friom the C2StransferVO and setting it to the requestVO
			if(!BTSLUtil.isNullString(_c2sTransferVO.getSenderReturnMessage()))
				p_requestVO.setSenderReturnMessage(_c2sTransferVO.getSenderReturnMessage());		

			if(be.isKey())  //checking if baseexception has key
			{
				if(_c2sTransferVO.getErrorCode()==null)
					_c2sTransferVO.setErrorCode(be.getMessageKey());
				p_requestVO.setMessageCode(be.getMessageKey());
				p_requestVO.setMessageArguments(be.getArgs());
			}
			else //setting default error code if message and key is not found
				_c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);

			//checking whether need to decrease the transaction load, if it is already increased
			if(_transferID!=null && _decreaseTransactionCounts)
			{	
				//decreasing transaction load
				LoadController.decreaseTransactionLoad(_transferID,_senderNetworkCode,LoadControllerI.DEC_LAST_TRANS_COUNT);
				_isCounterDecreased=true;
			}
			//making entry in the transaction log
			TransactionLog.log(_transferID,p_requestVO.getRequestIDStr(),p_requestVO.getFilteredMSISDN(),"",PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,_c2sTransferVO.getSenderReturnMessage(),PretupsI.TXN_LOG_STATUS_FAIL,"Getting Code="+p_requestVO.getMessageCode());
			if(	BTSLUtil.isNullString(_c2sTransferVO.getErrorCode()))
				_c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);

		}
		catch(Exception e)
		{   loggerValue.setLength(0);
	       loggerValue.append("Exception:");
           loggerValue.append(e.getMessage());
			_log.error(METHOD_NAME, loggerValue);
			_log.errorTrace(METHOD_NAME, e);
			//setting success transaction status flag to false
			p_requestVO.setSuccessTxn(false);
			try
			{
				//getting database connection to unmark the users transaction to completed 
				if(_receiverVO!=null && _receiverVO.isUnmarkRequestStatus())
				{
					if(mcomCon==null){
						mcomCon = new MComConnection();con=mcomCon.getConnection();
					}
					//Setting users transaction status to completed at the start it was marked underprocess
					PretupsBL.unmarkReceiverLastRequest(con,p_requestVO.getRequestIDStr(),_receiverVO);
				}
			}
			catch(Exception ex)
			{    loggerValue.setLength(0);
		         loggerValue.append("Exception:");
	             loggerValue.append(e.getMessage());
				_log.error(METHOD_NAME, loggerValue);
				_log.errorTrace(METHOD_NAME, ex);
				_c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			}

			_c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			_c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			_log.error(METHOD_NAME, "Exception:"+ e.getMessage());
			_log.errorTrace(METHOD_NAME, e);

			//decreasing the transaction load count
			if(_transferID!=null && _decreaseTransactionCounts)
			{
				LoadController.decreaseTransactionLoad(_transferID,_senderNetworkCode,LoadControllerI.DEC_LAST_TRANS_COUNT);
				_isCounterDecreased=true;
			}
			//raising alarm
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2SBulkReversalController[process]",_transferID,_senderMSISDN,_senderNetworkCode,"Exception:"+e.getMessage());
			//logging in the transaction log
			TransactionLog.log(_transferID,p_requestVO.getRequestIDStr(),p_requestVO.getFilteredMSISDN(),"",PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,_c2sTransferVO.getSenderReturnMessage(),PretupsI.TXN_LOG_STATUS_FAIL,"Getting Code="+p_requestVO.getMessageCode()+" ,Old Txn Id="+_c2sTransferVO.getOldTxnId());
			if(	BTSLUtil.isNullString(_c2sTransferVO.getErrorCode()))
				_c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);

		} //end of catch
		finally
		{
			try
			{
				//Getting connection if it is null
				if(mcomCon==null){
					mcomCon = new MComConnection();con=mcomCon.getConnection();}
					if(_transferID!=null && !_transferDetailAdded && (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON) ||p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_REQUEST) || (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD) && !p_requestVO.getMessageCode().equals(PretupsI.TXN_STATUS_UNDER_PROCESS))))
					{
					if(!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue())
					{
						final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(_c2sTransferVO
                                .getServiceType());
                            if (serviceSelectorMappingVO != null) {
                                p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                            }
						addEntryInTransfers(con);
					}
				}

				else if(_transferID!=null && p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD))
					_log.info("process",p_requestVO.getRequestIDStr(),"Send the message to MSISDN="+p_requestVO.getFilteredMSISDN()+" Transfer ID="+_transferID+" But not added entry in Transfers yet");
			}
			catch(BTSLBaseException be)
			{    loggerValue.setLength(0);
	             loggerValue.append("BTSLException:");
                  loggerValue.append(be.getMessage());
				_log.error(METHOD_NAME, loggerValue);
				_log.errorTrace(METHOD_NAME, be);
			}
			catch(Exception e)
			{     loggerValue.setLength(0);
                  loggerValue.append("Exception:");
                  loggerValue.append(e.getMessage());
				_log.error(METHOD_NAME, loggerValue);
				_log.errorTrace(METHOD_NAME, e);
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"C2SBulkReversalController[process]",_transferID,_senderMSISDN,_senderNetworkCode,"Exception:"+e.getMessage());
			}

			if(p_requestVO.isSuccessTxn()){
				p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_SENDER_SUCCESS_PRE_REVERSAL);	
			}else if(BTSLUtil.isNullString(p_requestVO.getMessageCode())){
				p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);	
			}
			
			if(_isCounterDecreased)
				p_requestVO.setDecreaseLoadCounters(false);
			if(con!=null)
			{
				//committing transaction and closing connection
				try {con.commit();} catch(Exception e){
					 loggerValue.setLength(0);
	                  loggerValue.append("Exception:");
	                  loggerValue.append(e.getMessage());
					_log.error(METHOD_NAME, loggerValue);
					_log.errorTrace(METHOD_NAME, e);
				};
				if(mcomCon != null){mcomCon.close("C2SBulkReversalController#process");mcomCon=null;}
				con=null;
			}
			//added by nilesh : consolidated for logger
			if(_oneLog)
				OneLineTXNLog.log(_c2sTransferVO,_senderTransferItemVO,_receiverTransferItemVO);
			//making entry in the transaction log	
			TransactionLog.log(_transferID,p_requestVO.getRequestIDStr(),p_requestVO.getFilteredMSISDN(),"",PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Leaving the controller",PretupsI.TXN_LOG_STATUS_SUCCESS,"Getting Code="+p_requestVO.getMessageCode()+" ,Old Txn Id="+_c2sTransferVO.getOldTxnId());
			if(_log.isDebugEnabled()) _log.debug("process","Exiting");
		}//end of finally
	}//end of process

	/**
	 * Method to process the request if SKEY is required for this transaction
	 * @param p_con
	 * @throws BTSLBaseException
	 * @throws Exception
	 */
	private void processSKeyGen(Connection p_con) throws BTSLBaseException,Exception
	{
		final String METHOD_NAME="processSKeyGen";
		if(_log.isDebugEnabled())
			_log.debug(METHOD_NAME,"Entered");
		try
		{
			//validate skey details for generation and generate skey
			PretupsBL.generateSKey(p_con,_c2sTransferVO);
		}
		catch(BTSLBaseException be)
		{
			_log.error(METHOD_NAME, "Exception:"+ be.getMessage());
			_log.errorTrace(METHOD_NAME, be);
			throw be;
		}
		catch(Exception e)
		{
			_log.error(METHOD_NAME, "Exception:"+ e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2SBulkReversalController[processSKeyGen]",_transferID,_senderMSISDN,_senderNetworkCode,"Exception:"+e.getMessage());
			throw new BTSLBaseException("C2SBulkReversalController","processSKeyGen",PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		finally
		{
			if(_log.isDebugEnabled())
				_log.debug("processSKeyGen","Exiting");
		}
	}//end of processSKeyGen


	
	//added for PIN generetion
	synchronized public  String generatePIN(String chars,int passLength) throws BTSLBaseException  
	{
		final String METHOD_NAME="generatePIN";
		StringBuffer temp=null;
		try{
			if (passLength > chars.length()) {
				throw new BTSLBaseException (this, METHOD_NAME, "Random number minimum length should be less than the provided chars list length");
			}
			SecureRandom m_generator = new SecureRandom();
			m_generator.setSeed(System.nanoTime()) ;
			char[] availableChars = chars.toCharArray();
			int availableCharsLeft = availableChars.length;
			temp = new StringBuffer(passLength);
			int pos=0;
			for (int i = 0; i < passLength; ) {
				pos = BTSLUtil.parseDoubleToInt(availableCharsLeft * m_generator.nextDouble());
				if(i==0)
				{
					if(!String.valueOf(availableChars[pos]).equalsIgnoreCase("0")){
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
			_log.error(METHOD_NAME, "Exception:"+ e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			temp = null;
			throw new BTSLBaseException (this, METHOD_NAME, "Exception In generating PIN");
		}
		if (_log.isDebugEnabled()) 
			_log.debug("generatePIN Exiting","");
		return String.valueOf(temp);
	}


	/**
	 * Method to process the request and perform the validation of the request
	 * @param p_con
	 * @throws BTSLBaseException
	 */
	public void processTransfer(Connection p_con) throws BTSLBaseException
	{
		final String METHOD_NAME="processTransfer";
		if(_log.isDebugEnabled()) _log.debug("processTransfer","Entered");
		try
		{
			//Generating the C2S transfer ID
			_c2sTransferVO.setTransferDate(_currentDate);
			_c2sTransferVO.setTransferDateTime(_currentDate);
			//PretupsBL.generateC2STransferID(_c2sTransferVO);
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.COMMON_TRANSFER_ID_APPLICABLE))).booleanValue())
			{
				_transferID=_operatorUtil.generateC2SCommonTransferID(_c2sTransferVO);
				_c2sTransferVO.setTransferID(_transferID);
			}else{
				generateReversalTransferID(_c2sTransferVO);
			}
			
			_transferID=_c2sTransferVO.getTransferID();
			_receiverVO.setLastTransferID(_transferID);

			//Set sender transfer item details
			setSenderTransferItemVO();

			//set receiver transfer item details
			setReceiverTransferItemVO();

			//Get the product Info based on the service type
			//PretupsBL.getProductFromServiceType(p_con,_c2sTransferVO,_serviceType,PretupsI.C2S_MODULE);
			_transferEntryReqd=true;

			//Here logic will come for Commission profile for sale center
			if((_channelUserVO.getCategoryVO()).getDomainTypeCode().equals(PretupsI.DOMAIN_TYPE_SALECENTER))//If domain is of Sales Center then
				_senderTransferItemVO.setTransferValue(_c2sTransferVO.getRequestedAmount());
			else
				_senderTransferItemVO.setTransferValue(_c2sTransferVO.getTransferValue());

		}
		catch(BTSLBaseException be)
		{
			_log.error(METHOD_NAME, "BTSLException:"+ be.getMessage());
			_log.errorTrace(METHOD_NAME, be);
			//setting transfer status to FAIL
			_c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			if(be.isKey())
				_c2sTransferVO.setErrorCode(be.getMessageKey());
			else
				_c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			throw be;
		}
		catch(Exception e)
		{
			_log.error(METHOD_NAME, "Exception:"+ e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			if(_c2sTransferVO.getReceiverReturnMsg()==null || !((BTSLMessages)_c2sTransferVO.getReceiverReturnMsg()).isKey())
			{
				if(_transferID!=null)
					_c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.C2S_RECEIVER_FAIL_REVERSAL,new String[]{PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()),_senderPushMessageMsisdn,String.valueOf(_transferID)}));
				else
					_c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.FAIL_R,new String[]{PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount())}));
			}
			//setting transfer status to FAIL
			_c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			_c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2SBulkReversalController[processTransfer]",_transferID,_senderMSISDN,_senderNetworkCode,"Exception:"+e.getMessage());
			throw new BTSLBaseException("C2SBulkReversalController","processTransfer",PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug("processTransfer","Exited");
		}
	}

	/**
	 * Thread to perform IN related operations
	 */
	public void run() 
	{
		final String METHOD_NAME="run"; 
		if(_log.isDebugEnabled())_log.debug("run",_transferID,"Entered");
		BTSLMessages btslMessages=null; 
		_userBalancesVO=null;
		Connection con=null;MComConnectionI mcomCon = null;
		StringBuilder loggerValue= new StringBuilder(); 
		DiffCalBL diffCalBL=null;
		ArrayList diffList=null;
		ArrayList diffBonusList=null;
		ArrayList diffPenaltyList=null;
		try
		{
			
			_c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
			_c2sTransferVO.setSenderReturnMessage(null);

		
			if(PretupsI.YES.equals(_c2sTransferVO.getDifferentialGiven()))
			{
				if(mcomCon==null) {
					mcomCon = new MComConnection();con=mcomCon.getConnection();	}
				//Caluculate Differential if transaction successful
				try
				{
					diffCalBL=new DiffCalBL();
					diffList=diffCalBL.loadDifferentialCalculationsReversal(con,_c2sTransferVO,PretupsI.C2S_MODULE);
				}
				catch(BTSLBaseException be)
				{    loggerValue.setLength(0);
				     loggerValue.append("Exception:");
				     loggerValue.append( be.getMessage());
					_log.error(METHOD_NAME, loggerValue);
					_log.errorTrace(METHOD_NAME, be);
					_c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
					_c2sTransferVO.setErrorCode(PretupsErrorCodesI.ERROR_EXCEPTION);
					_finalTransferStatusUpdate=false;
					if(_log.isDebugEnabled()) {
						 loggerValue.setLength(0);
					     loggerValue.append("For _transferID=");
					     loggerValue.append(_transferID);
					     loggerValue.append(" Diff applicable=");
					     loggerValue.append(_c2sTransferVO.getDifferentialApplicable());
					     loggerValue.append(" Diff Given=");
					     loggerValue.append(_c2sTransferVO.getDifferentialGiven());
					     loggerValue.append(" Not able to give Diff commission getting BTSL Base Exception=");
					     loggerValue.append(be.getMessage());
					     loggerValue.append(" Leaving transaction status as Under process");
					     loggerValue.append(" ,Old Txn Id=");
					     loggerValue.append(_c2sTransferVO.getOldTxnId());
						_log.debug("C2SBulkReversalController",loggerValue);
					}
					EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"C2SPrepaidReversalController[run]",_c2sTransferVO.getTransferID(),_c2sTransferVO.getSenderMsisdn(),_c2sTransferVO.getNetworkCode(),"Exception:"+be.getMessage());
				}
				catch(Exception e)
				{     loggerValue.setLength(0);
			          loggerValue.append("Exception:");
			          loggerValue.append(e.getMessage());
					_log.error(METHOD_NAME, loggerValue );
					_log.errorTrace(METHOD_NAME, e);
					_c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
					_c2sTransferVO.setErrorCode(PretupsErrorCodesI.ERROR_EXCEPTION);
					_finalTransferStatusUpdate=false;
					if(_log.isDebugEnabled())
						{
						  loggerValue.setLength(0);
				          loggerValue.append("For _transferID=");
				          loggerValue.append(_transferID);
				          loggerValue.append(" Diff applicable=");
				          loggerValue.append(_c2sTransferVO.getDifferentialApplicable());
				          loggerValue.append(" Diff Given=");
				          loggerValue.append(_c2sTransferVO.getDifferentialGiven());
				          loggerValue.append(" Not able to give Diff commission getting Exception=");
				          loggerValue.append(e.getMessage());
				          loggerValue.append(" Leaving transaction status as Under process");
				          loggerValue.append(" ,Old Txn Id=");
				          loggerValue.append(_c2sTransferVO.getOldTxnId());
						_log.debug("C2SBulkReversalController",loggerValue);
						}
				}
				if(mcomCon != null){mcomCon.close("C2SBulkReversalController#run");mcomCon=null;}
				con=null;
				_finalTransferStatusUpdate=true;
			}else{
				_finalTransferStatusUpdate=true;
			}

			_c2sTransferVO.setErrorCode(null);
			//changes done to handle bonus i.e promo commission.
			if(mcomCon==null) {
				mcomCon = new MComConnection();con=mcomCon.getConnection();}	
			//Caluculate Differential if transaction successful
			try
			{
				diffCalBL=new DiffCalBL();
				diffBonusList=diffCalBL.loadDifferentialBonusCalculationsReversal(con, _c2sTransferVO, PretupsI.C2S_MODULE);				
			}
			catch(BTSLBaseException be)
			{
				_log.error(METHOD_NAME, "Exception:"+ be.getMessage());
				_log.errorTrace(METHOD_NAME, be);
				_c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
				_c2sTransferVO.setErrorCode(PretupsErrorCodesI.ERROR_EXCEPTION);
				_finalTransferStatusUpdate=false;
				if(_log.isDebugEnabled()) {
					 loggerValue.setLength(0);
					 loggerValue.append("For _transferID=");
					 loggerValue.append(_transferID);
					 loggerValue.append(" Diff applicable=");
					 loggerValue.append(_c2sTransferVO.getDifferentialApplicable());
					 loggerValue.append(" Diff Given=");
					 loggerValue.append(_c2sTransferVO.getDifferentialGiven());
					 loggerValue.append(" Not able to give Diff commission getting BTSL Base Exception=");
					 loggerValue.append(be.getMessage());
					 loggerValue.append(" Leaving transaction status as Under process");
					 loggerValue.append(" ,Old Txn Id=");
					 loggerValue.append(_c2sTransferVO.getOldTxnId());
					_log.debug("C2SBulkReversalController",loggerValue);
				}
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"C2SBulkReversalController[run]",_c2sTransferVO.getTransferID(),_c2sTransferVO.getSenderMsisdn(),_c2sTransferVO.getNetworkCode(),"Exception:"+be.getMessage());
			}
			catch(Exception e)
			{      loggerValue.setLength(0);
			       loggerValue.append("Exception :");
			       loggerValue.append(e.getMessage());
				_log.error(METHOD_NAME, loggerValue );
				_log.errorTrace(METHOD_NAME, e);
				_c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
				_c2sTransferVO.setErrorCode(PretupsErrorCodesI.ERROR_EXCEPTION);
				_finalTransferStatusUpdate=false;
				if(_log.isDebugEnabled()) {
					   loggerValue.setLength(0);
				       loggerValue.append("For _transferID=");
				       loggerValue.append(_transferID);
				       loggerValue.append(" Diff applicable=");
				       loggerValue.append(_c2sTransferVO.getDifferentialApplicable());
				       loggerValue.append(" Diff Given=");
				       loggerValue.append(_c2sTransferVO.getDifferentialGiven() );
				       loggerValue.append(" Not able to give Diff commission getting Exception=");
				       loggerValue.append(e.getMessage());
				       loggerValue.append(" Leaving transaction status as Under process");
				       loggerValue.append(" ,Old Txn Id=");
				       loggerValue.append(_c2sTransferVO.getOldTxnId());
					_log.debug("C2SBulkReversalController",loggerValue);
				}
			}
			if (mcomCon != null) {
				mcomCon.close("C2SBulkReversalController#run");
				mcomCon = null;
			}
			con=null;
			
			if(mcomCon==null) {
				mcomCon = new MComConnection();con=mcomCon.getConnection();}
			
			 try {
	                diffCalBL = new DiffCalBL();
	                diffPenaltyList = diffCalBL.loadDifferentialCalculationsReversalPenalty(con, _c2sTransferVO, PretupsI.C2S_MODULE);
	            } catch (BTSLBaseException be) {
	                	loggerValue.setLength(0);
				       loggerValue.append("Exception:");
				       loggerValue.append(be.getMessage());
	                _log.error(METHOD_NAME,  loggerValue );
	                _log.errorTrace(METHOD_NAME, be);
	                _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
	                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.ERROR_EXCEPTION);
	                _finalTransferStatusUpdate = false;
	                if (_log.isDebugEnabled()) {
	                	   loggerValue.setLength(0);
					       loggerValue.append( "For _transferID=");
					       loggerValue.append(_transferID);
					       loggerValue.append(" Diff applicable=");
					       loggerValue.append(_c2sTransferVO.getDifferentialApplicable());
					       loggerValue.append(" Diff Given=");
					       loggerValue.append(_c2sTransferVO.getDifferentialGiven());
					       loggerValue.append(" Not able to give Diff commission getting BTSL Base Exception=");
					       loggerValue.append(be.getMessage());
					       loggerValue.append(" Leaving transaction status as Under process");
					       loggerValue.append(" ,Old Txn Id=" );
					       loggerValue.append( _c2sTransferVO.getOldTxnId());
	                    _log.debug("C2SBulkReversalController",loggerValue);
	                }
	                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "C2SPrepaidReversalController[run]", _c2sTransferVO
	                    .getTransferID(), _c2sTransferVO.getSenderMsisdn(), _c2sTransferVO.getNetworkCode(), "Exception:" + be.getMessage());
	            } catch (Exception e) {
	                   loggerValue.setLength(0);
				       loggerValue.append("Exception:");
				       loggerValue.append(e.getMessage());
	                _log.error(METHOD_NAME,  loggerValue);
	                _log.errorTrace(METHOD_NAME, e);
	                _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
	                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.ERROR_EXCEPTION);
	                _finalTransferStatusUpdate = false;
	                if (_log.isDebugEnabled()) {
	                	loggerValue.setLength(0);
	                	loggerValue.append("For _transferID=");
	                	loggerValue.append(_transferID);
	                	loggerValue.append(" Diff applicable=");
	                	loggerValue.append(_c2sTransferVO.getDifferentialApplicable());
	                	loggerValue.append(" Diff Given=");
	                	loggerValue.append(_c2sTransferVO.getDifferentialGiven());
	                	loggerValue.append(" Not able to give Diff commission getting Exception=");
	                	loggerValue.append(e.getMessage());
	                	loggerValue.append(" Leaving transaction status as Under process");
	                	loggerValue.append(" ,Old Txn Id=");
	                	loggerValue.append(_c2sTransferVO.getOldTxnId());
	                    _log.debug(
	                        "C2SBulkReversalController",loggerValue);
	                }
	            }
			 	if(mcomCon != null){mcomCon.close("C2SBulkReversalController#run");mcomCon=null;}
	            con = null;

			//TransactionLog.log(_transferID,_requestIDStr,_senderMSISDN,_senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"After Differential Calculation",PretupsI.TXN_LOG_STATUS_SUCCESS,"Differential Appl="+_c2sTransferVO.getDifferentialApplicable()+" Diff Given="+_c2sTransferVO.getDifferentialGiven());

			if(_log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("For _transferID=");
				loggerValue.append(_transferID);
				loggerValue.append(" Diff applicable=");
				loggerValue.append(_c2sTransferVO.getDifferentialApplicable());
				loggerValue.append(" Diff Given=");
				loggerValue.append(_c2sTransferVO.getDifferentialGiven());
				loggerValue.append(" ,Old Txn Id=");
				loggerValue.append(_c2sTransferVO.getOldTxnId());
				_log.debug("C2SBulkReversalController",loggerValue);
			}
		}//end try
		catch(BTSLBaseException be)
		{	 loggerValue.setLength(0);
	     	 loggerValue.append("BTSLException:");
		     loggerValue.append(be.getMessage());
			_log.error(METHOD_NAME,loggerValue );
			_log.errorTrace(METHOD_NAME, be);
			_requestVO.setSuccessTxn(false);
			if(BTSLUtil.isNullString(_c2sTransferVO.getErrorCode()))
			{
				if(be.isKey())
					_c2sTransferVO.setErrorCode(be.getMessageKey());
				else
					_c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			}//end if 
			if(be.isKey() && _c2sTransferVO.getSenderReturnMessage()==null)
				btslMessages=be.getBtslMessages();
			else if(_c2sTransferVO.getSenderReturnMessage()==null)
				_c2sTransferVO.setSenderReturnMessage(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			if(_log.isDebugEnabled())
				{
				loggerValue.setLength(0);
				loggerValue.append("Error Code:");
				loggerValue.append(_c2sTransferVO.getErrorCode());
				loggerValue.append(" ,Old Txn Id=");
				loggerValue.append(_c2sTransferVO.getOldTxnId());
				_log.debug("run",_transferID,loggerValue);
				}

			if(	BTSLUtil.isNullString(_c2sTransferVO.getErrorCode()))
				_c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);

		}//end catch BTSLBaseException
		catch(Exception e)
		{    loggerValue.setLength(0);
		     loggerValue.append("Exception:");
		     loggerValue.append(e.getMessage());
			_log.error(METHOD_NAME, loggerValue );
			_log.errorTrace(METHOD_NAME, e);
			_requestVO.setSuccessTxn(false);
			//try{if(con!=null) con.rollback() ;}catch(Exception ex){}


			if(BTSLUtil.isNullString(_c2sTransferVO.getErrorCode()))
				_c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			_log.error("run",_transferID,"Exception:"+e.getMessage());
			loggerValue.setLength(0);
		     loggerValue.append("Exception:");
		     loggerValue.append(e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"{C2SPrepaidReversalController[run]",_transferID,_senderMSISDN,_senderNetworkCode,loggerValue.toString());
			btslMessages=new BTSLMessages(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);

			
			if(	BTSLUtil.isNullString(_c2sTransferVO.getErrorCode()))
				_c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);

		}//end catch Exception
		finally
		{
			if(_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL) && (_c2sTransferVO.getReceiverReturnMsg()==null || !((BTSLMessages)_c2sTransferVO.getReceiverReturnMsg()).isKey()))
				_c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.C2S_RECEIVER_FAIL_REVERSAL,new String[]{PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()),_senderPushMessageMsisdn,String.valueOf(_transferID)}));

			//decreasing transaction load count
			LoadController.decreaseTransactionLoad(_transferID,_senderNetworkCode,LoadControllerI.DEC_LAST_TRANS_COUNT);

			int updateCount=0;
			try
			{
				if(mcomCon==null){
					mcomCon = new MComConnection();con=mcomCon.getConnection();}

				//_c2sTransferVO.setReferenceID(_c2sTransferVO.getOldTxnId());

			_c2sTransferVO.setModifiedBy(PretupsI.C2S_REV_BATCH);				_c2sTransferVO.setModifiedOn(_currentDate);

				_c2sTransferVO.setSenderTransferValue(_c2sTransferVO.getTransferValue());

				if(_finalTransferStatusUpdate)
				{
					//Update Previous billpayment Success Request 
					updateCount=ChannelTransferBL.updateOldC2STransferDetailsReversal(con,_c2sTransferVO);

					if(!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue())
					{
						if(updateCount>0)
						{
							updateCount=0;
							//Credit back the credited billpayment amount
							updateCount=ChannelUserBL.creditUserBalanceForProductReversal(con,_transferID,_c2sTransferVO);
						}
						
						if(updateCount>0)
						{
							updateCount=0;
							updateCount=ChannelTransferBL.updateC2STransferDetailsReversal(con,_c2sTransferVO);	
						}
					}

				}
			}
			catch(BTSLBaseException be)
			{     loggerValue.setLength(0);
			      loggerValue.append("BTSLException:");
			      loggerValue.append(be.getMessage());
				_log.error(METHOD_NAME, loggerValue );
				_log.errorTrace(METHOD_NAME, be);
				updateCount=0;
				try{con.rollback();}catch(Exception e){
					loggerValue.setLength(0);
				      loggerValue.append("Exception:");
				      loggerValue.append(e.getMessage());
					_log.error(METHOD_NAME, loggerValue);
					_log.errorTrace(METHOD_NAME, e);
				}
				_log.error("run",_transferID,"BTSLBaseException while updating transfer details in database:"+be.getMessage());
				if(	BTSLUtil.isNullString(_c2sTransferVO.getErrorCode()))
					_c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);

			}
			catch(Exception e)
			{     loggerValue.setLength(0);
		          loggerValue.append("Exception:");
		          loggerValue.append(e.getMessage());
				_log.error(METHOD_NAME, loggerValue);
				_log.errorTrace(METHOD_NAME, e);
				updateCount=0;
				try{con.rollback();}catch(Exception ex){
					_log.errorTrace(METHOD_NAME,ex);
				}
				_log.error("run",_transferID,"Exception while updating transfer details in database:"+e.getMessage());
				  loggerValue.setLength(0);
		          loggerValue.append("Exception while updating transfer details in database , Exception:");
		          loggerValue.append(e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"C2SBulkReversalController[run]",_transferID,_senderMSISDN,_senderNetworkCode,loggerValue.toString());
				if(	BTSLUtil.isNullString(_c2sTransferVO.getErrorCode()))
					_c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			}
			finally
			{
				if(updateCount>0)
				{
					try{con.commit();}catch(Exception e){
						_log.error(METHOD_NAME, "Exception:"+ e.getMessage());
						_log.errorTrace(METHOD_NAME, e);
					}

					if(_finalTransferStatusUpdate && diffList!=null && !diffList.isEmpty())
					{
						try{
							updateCount=0;
							diffCalBL=new DiffCalBL();
							//Credit back the debited billpayment amount
							//AdjustmentsVO adjustmentsVO=(AdjustmentsVO)diffList.get(0);
							((AdjustmentsVO)diffList.get(0)).setSubService(_c2sTransferVO.getSelectorCode());
							//diffList.remove(0);
							//diffList.add(adjustmentsVO);
							updateCount=diffCalBL.differentialAdjustmentForReversal(con,_c2sTransferVO,diffList);
						}catch (Exception e) {
							_log.error(METHOD_NAME, "Exception:"+ e.getMessage());
							_log.errorTrace(METHOD_NAME, e);
							try{con.rollback();}catch(Exception ec){
								loggerValue.setLength(0);
								loggerValue.append("Exception:");
								loggerValue.append(ec.getMessage());
								_log.error(METHOD_NAME, loggerValue);
								_log.errorTrace(METHOD_NAME, ec);
							}
						}finally
						{
							if(updateCount>0)
								try{con.commit();}catch(Exception e){
									loggerValue.setLength(0);
									loggerValue.append("Exception:");
									loggerValue.append(e.getMessage());
									_log.error(METHOD_NAME, loggerValue);
									_log.errorTrace(METHOD_NAME, e);
								}
								else
									try{con.rollback();}catch(Exception ec){
										loggerValue.setLength(0);
										loggerValue.append("Exception:");
										loggerValue.append(ec.getMessage());
										_log.error(METHOD_NAME, loggerValue);
										_log.errorTrace(METHOD_NAME, ec);
									}
						}
					}
					//changes done to handle bonus i.e promo commission.
					if(_finalTransferStatusUpdate && diffBonusList!=null && !diffBonusList.isEmpty())
					{
						try{
							updateCount=0;
							diffCalBL=new DiffCalBL();
							((AdjustmentsVO)diffBonusList.get(0)).setSubService(_c2sTransferVO.getSelectorCode());
							updateCount=diffCalBL.differentialBonusAdjustmentForReversal(con,_c2sTransferVO,diffBonusList);
						}catch (Exception e) {
							loggerValue.setLength(0);
							loggerValue.append("Exception:");
							loggerValue.append(e.getMessage());
							_log.error(METHOD_NAME, loggerValue);
							_log.errorTrace(METHOD_NAME, e);
							try{con.rollback();}catch(Exception ec){
								loggerValue.setLength(0);
								loggerValue.append("Exception:");
								loggerValue.append(ec.getMessage());
								_log.error(METHOD_NAME, loggerValue);
								_log.errorTrace(METHOD_NAME, ec);
							}
						}finally
						{
							if(updateCount>0)
								try{con.commit();}catch(Exception e){
									loggerValue.setLength(0);
									loggerValue.append("Exception:");
									loggerValue.append(e.getMessage());
									_log.error(METHOD_NAME, loggerValue);
									_log.errorTrace(METHOD_NAME, e);
								}
								else
									try{con.rollback();}catch(Exception ec){
										loggerValue.setLength(0);
										loggerValue.append("Exception:");
										loggerValue.append(ec.getMessage());
										_log.error(METHOD_NAME, loggerValue);
										_log.errorTrace(METHOD_NAME, ec);
									}
						}
					}
					
					  if (_finalTransferStatusUpdate && diffPenaltyList != null && !diffPenaltyList.isEmpty()) {
	                        try {
	                            updateCount = 0;
	                            diffCalBL = new DiffCalBL();
	                           // ((AdjustmentsVO) diffPenaltyList.get(0)).setSubService(_c2sTransferVO.getSelectorCode());
	                            updateCount = diffCalBL.differentialAdjustmentForReversalPenalty(con, _c2sTransferVO, diffPenaltyList);
	                        } catch (Exception e) {
	                        	_finalTransferStatusUpdate=false;
	                        	loggerValue.setLength(0);
								loggerValue.append("Exception:");
								loggerValue.append(e.getMessage());
	                            _log.error(METHOD_NAME,loggerValue);
	                            _log.errorTrace(METHOD_NAME, e);
	                            try {
	                                con.rollback();
	                            } catch (Exception ec) {
	                            	loggerValue.setLength(0);
									loggerValue.append("Exception:");
									loggerValue.append(ec.getMessage());
	                                _log.error(METHOD_NAME, loggerValue);
	                                _log.errorTrace(METHOD_NAME, ec);
	                            }
	                        } finally {
	                            if (updateCount > 0) {
	                                try {
	                                    con.commit();
	                                } catch (Exception e) {
	                                	loggerValue.setLength(0);
										loggerValue.append("Exception:");
										loggerValue.append(e.getMessage());
	                                    _log.error(METHOD_NAME, loggerValue);
	                                    _log.errorTrace(METHOD_NAME, e);
	                                }
	                            } else {
	                                try {
	                                    con.rollback();
	                                } catch (Exception ec) {
	                                	loggerValue.setLength(0);
										loggerValue.append("Exception:");
										loggerValue.append(ec.getMessage());
	                                    _log.error(METHOD_NAME, loggerValue);
	                                    _log.errorTrace(METHOD_NAME, ec);
	                                }
	                            }
	                        }

	                    }
				}else{
					try{con.rollback();}catch(Exception e){
						loggerValue.setLength(0);
						loggerValue.append("Exception:");
						loggerValue.append(e.getMessage());
						_log.error(METHOD_NAME, loggerValue);
						_log.errorTrace(METHOD_NAME, e);
					}
				}
				if(mcomCon != null){mcomCon.close("C2SBulkReversalController#run");mcomCon=null;}
				con=null;
			}

			
			
			_senderTransferItemVO.setPostBalance(_c2sTransferVO.getPostBalance());
			
			
			Boolean alloWSenderMessage=((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ALLOW_BULK_C2S_REVERSAL_MESSAGE))).booleanValue();
			
			//If transaction is fail and grouptype counters need to be decrease then decrease the counters
			//This change has been done by ankit on date 14/07/06 for SMS charging
			if(!_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)&& _requestVO.isDecreaseGroupTypeCounter() && ((ChannelUserVO)_requestVO.getSenderVO()).getUserControlGrouptypeCounters()!=null)
				PretupsBL.decreaseGroupTypeCounters(((ChannelUserVO)_requestVO.getSenderVO()).getUserControlGrouptypeCounters());

			if(BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(),_notAllowedSendMessGatw))
			{
				PushMessage pushMessages=null;
				if(btslMessages!=null)
				{
					//push final error message to sender
					pushMessages=(new PushMessage(_senderPushMessageMsisdn,BTSLUtil.getMessage(_senderLocale,btslMessages.getMessageKey(),btslMessages.getArgs()),_transferID,_c2sTransferVO.getRequestGatewayCode(),_senderLocale));
				}
				else
				{
				
					//push Additional Commission success message to sender and final status to sender
					if(!BTSLUtil.isNullString(_c2sTransferVO.getSenderReturnMessage()))
						pushMessages=(new PushMessage(_senderPushMessageMsisdn,_c2sTransferVO.getSenderReturnMessage(),_transferID,_c2sTransferVO.getRequestGatewayCode(),_senderLocale));
					else if(_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS) && alloWSenderMessage )
						pushMessages=new PushMessage(_senderPushMessageMsisdn,getSenderSuccessMessage(),_transferID,_c2sTransferVO.getRequestGatewayCode(),_senderLocale);
					
				}//end if
				//If transaction is successfull then if group type counters reach limit then send message using gateway that is associated with group type profile
				//This change has been done by ankit on date 14/07/06 for SMS charging
				if(_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS) && ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CHRG_ALLOWED))!=null && ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CHRG_ALLOWED)).indexOf(_requestVO.getRequestGatewayType())!=-1 && !PretupsI.NOT_APPLICABLE.equals(_requestVO.getGroupType()))
				{
					try
					{
						GroupTypeProfileVO groupTypeProfileVO=null;
						//load the user running and profile counters
						//Check the counters
						//update the counters
						groupTypeProfileVO=PretupsBL.loadAndCheckC2SGroupTypeCounters(_requestVO,PretupsI.GRPT_TYPE_CHARGING);
						//if group type counters reach limit then send message using gateway that is associated with group type profile
						if(groupTypeProfileVO!=null && groupTypeProfileVO.isGroupTypeCounterReach())
						{
							pushMessages.push(groupTypeProfileVO.getGatewayCode(),groupTypeProfileVO.getAltGatewayCode());//new method will be called here
							SMSChargingLog.log(((ChannelUserVO)_requestVO.getSenderVO()).getUserID(),(((ChannelUserVO)_requestVO.getSenderVO()).getUserChargeGrouptypeCounters()).getCounters(),groupTypeProfileVO.getThresholdValue(),groupTypeProfileVO.getReqGatewayType(),groupTypeProfileVO.getResGatewayType(),groupTypeProfileVO.getNetworkCode(),_requestVO.getGroupType(),_requestVO.getServiceType(),_requestVO.getModule());
						}
						else
							pushMessages.push();
					}
					catch(Exception e){
						loggerValue.setLength(0);
						loggerValue.append("Exception:");
						loggerValue.append(e.getMessage());
						_log.error(METHOD_NAME, loggerValue);
						_log.errorTrace(METHOD_NAME, e);
					}
				}
				else
					pushMessages.push();

			}
			//Log the credit back entry in the balance log
			if(_creditBackEntryDone)
				BalanceLogger.log(_userBalancesVO);		
			//added by nilesh: consolidated for logger
			if(!_oneLog)
				OneLineTXNLog.log(_c2sTransferVO,_senderTransferItemVO,_receiverTransferItemVO);
			TransactionLog.log(_transferID,_requestIDStr,_senderMSISDN,_senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Transaction Ending",PretupsI.TXN_LOG_STATUS_SUCCESS,"Trans Status="+_c2sTransferVO.getTransferStatus()+" Error Code="+_c2sTransferVO.getErrorCode()+" Diff Appl="+_c2sTransferVO.getDifferentialApplicable()+" Diff Given="+_c2sTransferVO.getDifferentialGiven()+" Message="+_c2sTransferVO.getSenderReturnMessage()+" ,Old Txn Id="+_c2sTransferVO.getOldTxnId());

			btslMessages=null; 
			_userBalancesVO=null;
			
			if(_log.isDebugEnabled())_log.debug("run",_transferID,"Exiting");
		}//end of finally
	}

	/**
	 * Method to populate C2S Transfer VO from request VO for further use
	 * @param p_requestVO
	 */
	private void populateVOFromRequest(RequestVO p_requestVO)
	{
		_c2sTransferVO.setSenderVO(_channelUserVO);
		MessageGatewayVO messageGatewayVO=MessageGatewayCache.getObject(PretupsI.GATEWAY_TYPE_WEB);
		p_requestVO.setMessageGatewayVO(messageGatewayVO);
		_c2sTransferVO.setRequestID(p_requestVO.getRequestIDStr());
		_c2sTransferVO.setModule(p_requestVO.getModule());
		_c2sTransferVO.setInstanceID(p_requestVO.getInstanceID());
		_c2sTransferVO.setRequestGatewayCode(messageGatewayVO.getGatewayCode());
		_c2sTransferVO.setRequestGatewayType(messageGatewayVO.getGatewayType());
		_c2sTransferVO.setServiceType(p_requestVO.getServiceType());
		_c2sTransferVO.setSourceType(p_requestVO.getSourceType());
		_c2sTransferVO.setCreatedOn(_currentDate);
		_c2sTransferVO.setCreatedBy(_channelUserVO.getUserID());
		_c2sTransferVO.setModifiedOn(_currentDate);
		_c2sTransferVO.setModifiedBy(PretupsI.C2S_REV_BATCH);
		_c2sTransferVO.setTransferDate(_currentDate);
		_c2sTransferVO.setTransferDateTime(_currentDate);
		_c2sTransferVO.setSenderMsisdn((_channelUserVO.getUserPhoneVO()).getMsisdn());
		_c2sTransferVO.setSenderID(_channelUserVO.getUserID());
		_c2sTransferVO.setNetworkCode(_channelUserVO.getNetworkID());
		_c2sTransferVO.setLocale(_senderLocale);
		_c2sTransferVO.setLanguage(_c2sTransferVO.getLocale().getLanguage());
		_c2sTransferVO.setCountry(_c2sTransferVO.getLocale().getCountry());
		_c2sTransferVO.setMsgGatewayFlowType(p_requestVO.getMessageGatewayVO().getFlowType());
		_c2sTransferVO.setMsgGatewayResponseType(p_requestVO.getMessageGatewayVO().getResponseType());
		_c2sTransferVO.setMsgGatewayTimeOutValue(p_requestVO.getMessageGatewayVO().getTimeoutValue());
		(_channelUserVO.getUserPhoneVO()).setLocale(_senderLocale);
		_c2sTransferVO.setReferenceID(p_requestVO.getExternalReferenceNum());
		_c2sTransferVO.setActiveUserId(_channelUserVO.getActiveUserID());
	}//end populateVOFromRequest


	/**
	 * Method to do the validation of the receiver and perform the steps before the topup stage
	 * @param p_con
	 * @throws BTSLBaseException
	 * @throws Exception
	 */
	private void processValidationRequest() throws BTSLBaseException,Exception
	{
		final String METHOD_NAME="processValidationRequest";
		StringBuilder loggerValue= new StringBuilder(); 
		if(_log.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Entered and performing validations for transfer ID=");
			loggerValue.append(_transferID);
			loggerValue.append(" ");
			loggerValue.append(_c2sTransferVO.getModule());
			loggerValue.append(" ");
			loggerValue.append(_c2sTransferVO.getReceiverNetworkCode());
			loggerValue.append(" ");
			loggerValue.append(_type);
			_log.debug("processValidationRequest",loggerValue);  
		}
		Connection con=null;MComConnectionI mcomCon = null;
		try
		{
			mcomCon = new MComConnection();con=mcomCon.getConnection();
			C2STransferVO c2sVOForBalance = new C2STransferVO();
			C2STransferDAO c2STransferDAO = new C2STransferDAO();
			c2sVOForBalance=c2STransferDAO.loadC2STransferDetails(con,_c2sTransferVO.getOldTxnId());
			if(c2sVOForBalance ==null)
			{
				_log.debug(this,"[processValidationRequest]Transaction No["+_c2sTransferVO.getOldTxnId()+"] Records not found in recharges table");
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2SBulkReversalController[processValidationRequest]","","","","Records not found in transfers table For Transfer ID ="+_c2sTransferVO.getOldTxnId());
				if(mcomCon != null){mcomCon.close("C2SBulkReversalController#processValidationRequest");mcomCon=null;}
				con=null;
				throw new BTSLBaseException("C2SBulkReversalController","processValidationRequest",PretupsErrorCodesI.EVD_TRANSACTION_DETAILS_NOT_FOUND);
			}
			  	
			
			_c2sTransferVO.setReceiverMsisdn(c2sVOForBalance.getReceiverMsisdn());
			_receiverMSISDN=c2sVOForBalance.getReceiverMsisdn();
			((ReceiverVO)_c2sTransferVO.getReceiverVO()).setMsisdn(_receiverMSISDN);
			
			_c2sTransferVO.setCardGroupSetID(c2sVOForBalance.getCardGroupSetID());
			_c2sTransferVO.setServiceClass(c2sVOForBalance.getServiceClass());
			_c2sTransferVO.setServiceClassCode(c2sVOForBalance.getServiceClassCode());
			_c2sTransferVO.setInterfaceReferenceId(c2sVOForBalance.getInterfaceReferenceId()); 
            _c2sTransferVO.setPenalty(c2sVOForBalance.getPenalty());
            _c2sTransferVO.setOwnerPenalty(c2sVOForBalance.getOwnerPenalty());
            _c2sTransferVO.setRoamPenalty(c2sVOForBalance.getPenalty());
            _c2sTransferVO.setRoamPenaltyOwner(c2sVOForBalance.getOwnerPenalty());
            _c2sTransferVO.setPenaltyDetails(c2sVOForBalance.getPenaltyDetails());
			TransactionLog.log(_transferID,_requestIDStr,_receiverMSISDN,_receiverVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"After Card Group Set Id="+_c2sTransferVO.getCardGroupSetID()+" Code"+_c2sTransferVO.getCardGroupCode()+" Card ID="+_c2sTransferVO.getCardGroupID()+" Access fee="+_c2sTransferVO.getReceiverAccessFee()+" Tax1 ="+_c2sTransferVO.getReceiverTax1Value()+" Tax2="+_c2sTransferVO.getReceiverTax1Value()+" Bonus="+_c2sTransferVO.getReceiverBonusValue()+" Val Type="+_c2sTransferVO.getReceiverValPeriodType()+" Validity="+_c2sTransferVO.getReceiverValidity()+" Talk Time="+_c2sTransferVO.getReceiverTransferValue(),PretupsI.TXN_LOG_STATUS_SUCCESS,""+" ,Old Txn Id="+_c2sTransferVO.getOldTxnId());
			_itemList=new ArrayList();
			_itemList.add(_senderTransferItemVO);
			_itemList.add(_receiverTransferItemVO);
			_c2sTransferVO.setTransferItemList(_itemList);

			if(mcomCon==null){
				mcomCon = new MComConnection();con=mcomCon.getConnection();}
			
			//ChannelTransferBL.increaseC2STransferInCounts(con, _c2sTransferVO,false);

			_c2sTransferVO.setReverseTransferID(_c2sTransferVO.getOldTxnId());
			_c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
			_c2sTransferVO.setQuantity(c2sVOForBalance.getQuantity());
			_c2sTransferVO.setSenderTransferValue(c2sVOForBalance.getSenderTransferValue());
			_senderTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
			_receiverTransferItemVO.setServiceClass(c2sVOForBalance.getServiceClass());
            _receiverTransferItemVO.setServiceClassCode(c2sVOForBalance.getServiceClassCode());
            
            
            
            final String cardGroupSetID = c2sVOForBalance.getCardGroupSetID();
            final String cardGroupID = c2sVOForBalance.getCardGroupID();
            final String reversalAllowed = (CardGroupCache.getCardRevPrmttdDetails(cardGroupSetID, cardGroupID)).getReversalPermitted();
            if (reversalAllowed.equals(PretupsI.NO)) {
                _log.error("C2SPrepaidReversalController", "The operator does not allow to reverse this transaction");
                throw new BTSLBaseException("C2SPrepaidReversalController", "processValidationRequest", PretupsErrorCodesI.REVERSAL_NOT_ALLOWED_CARDGROUP);
            }
            
			if(!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue()){
				_c2sTransferVO.setServiceType(PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL);
				if(PretupsI.BCU_USER.equalsIgnoreCase(_requestVO.getRequestorCategoryCode())||PretupsI.CUSTOMER_CARE.equalsIgnoreCase(_requestVO.getRequestorCategoryCode()))
				{
					_c2sTransferVO.setCreatedBy("SYSTEM");
					
					_c2sTransferVO.setModifiedBy(PretupsI.C2S_REV_BATCH);
					ChannelTransferBL.addC2STransferDetails(con, _c2sTransferVO);
				}
				else
				{
					ChannelTransferBL.addC2STransferDetails(con, _c2sTransferVO);
				}
				
			}
			_transferDetailAdded=true;

			//Commit the transaction and relaease the locks
			try { con.commit(); } catch(Exception be) {
				_log.error(METHOD_NAME, "Exception:"+ be.getMessage());
				_log.errorTrace(METHOD_NAME,be);
			}
			if(mcomCon != null){mcomCon.close("C2SBulkReversalController#processValidationRequest");mcomCon=null;}
			con=null;

			TransactionLog.log(_transferID,_requestIDStr,_receiverMSISDN,_receiverVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Marked Under process",PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"" +" ,Old Txn Id="+_c2sTransferVO.getOldTxnId());
       }
		catch(BTSLBaseException be)
		{
			_log.errorTrace(METHOD_NAME,be);
			_requestVO.setSuccessTxn(false);
			if(con!=null) con.rollback();
			_c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			_receiverTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			if(BTSLUtil.isNullString(_c2sTransferVO.getErrorCode()))
			{
				if(be.isKey())
					_c2sTransferVO.setErrorCode(be.getMessageKey());
				else
					_c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			}
			_log.error("C2SBulkReversalController[processValidationRequest]","Getting BTSL Base Exception:"+be.getMessage());
			throw be;
		}
		catch(Exception e)
		{    	loggerValue.setLength(0);
	        	loggerValue.append("Exception:");
	         	loggerValue.append(e.getMessage());
			_log.error(METHOD_NAME, loggerValue );
			_log.errorTrace(METHOD_NAME,e);
			_requestVO.setSuccessTxn(false);
			if(con!=null) con.rollback();
			_c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			
			if(BTSLUtil.isNullString(_c2sTransferVO.getErrorCode()))
				_c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			throw new BTSLBaseException("C2SPrepaidReversalController","processValidationRequest",PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		finally
		{
			if(mcomCon != null){mcomCon.close("C2SBulkReversalController#processValidationRequest");mcomCon=null;}
			con=null;
		}
	}	

	private String getSenderFailMessage(){
		String[] messageArgArray = {_transferID };
		return BTSLUtil.getMessage(_senderLocale, PretupsErrorCodesI.C2S_SENDER_FAIL_KEY_PRE_REVERSAL, messageArgArray);
	}
	

	private String getSenderSuccessMessage()
	{
		
		String[] messageArgArray={(_receiverVO.getSid()!=null)?_receiverVO.getSid():_receiverMSISDN,_transferID,PretupsBL.getDisplayAmount(_c2sTransferVO.getTransferValue()),PretupsBL.getDisplayAmount(_senderTransferItemVO.getPostBalance()),_c2sTransferVO.getSubService()};
		_requestVO.setMessageCode(PretupsErrorCodesI.C2S_SENDER_SUCCESS_PRE_REVERSAL);
		_requestVO.setMessageArguments(messageArgArray);
		return BTSLUtil.getMessage(_senderLocale,PretupsErrorCodesI.C2S_SENDER_SUCCESS_PRE_REVERSAL,messageArgArray);
	}
	


	/**
	 * Sets the sender transfer Items VO for the channel user
	 *
	 */
	private void setSenderTransferItemVO()
	{
		_senderTransferItemVO=new C2STransferItemVO();
		//set sender transfer item details
		_senderTransferItemVO.setSNo(1);
		_senderTransferItemVO.setMsisdn(_senderMSISDN);
		_senderTransferItemVO.setRequestValue(_c2sTransferVO.getRequestedAmount());
		_senderTransferItemVO.setSubscriberType(_senderSubscriberType);
		_senderTransferItemVO.setTransferDate(_currentDate);
		_senderTransferItemVO.setTransferDateTime(_currentDate);
		_senderTransferItemVO.setTransferID(_c2sTransferVO.getTransferID());
		_senderTransferItemVO.setTransferType(PretupsI.TRANSFER_TYPE_TXN);
		_senderTransferItemVO.setUserType(PretupsI.USER_TYPE_SENDER);
		_senderTransferItemVO.setEntryDate(_currentDate);
		_senderTransferItemVO.setEntryDateTime(_currentDate);
		_senderTransferItemVO.setEntryType(PretupsI.DEBIT);
		_senderTransferItemVO.setTransferStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
		_senderTransferItemVO.setPrefixID((_channelUserVO.getUserPhoneVO()).getPrefixID());
	}
	/**
	 * Sets the receiever transfer Items VO for the subscriber
	 *
	 */
	private void setReceiverTransferItemVO()
	{
		_receiverTransferItemVO=new C2STransferItemVO();
		_receiverTransferItemVO.setSNo(2);
		_receiverTransferItemVO.setMsisdn(_receiverMSISDN);
		_receiverTransferItemVO.setRequestValue(_c2sTransferVO.getRequestedAmount());
		_receiverTransferItemVO.setSubscriberType(_receiverVO.getSubscriberType());
		_receiverTransferItemVO.setTransferDate(_currentDate);
		_receiverTransferItemVO.setTransferDateTime(_currentDate);
		_receiverTransferItemVO.setTransferID(_c2sTransferVO.getTransferID());
		_receiverTransferItemVO.setTransferType(PretupsI.TRANSFER_TYPE_TXN);
		_receiverTransferItemVO.setUserType(PretupsI.USER_TYPE_RECEIVER);
		_receiverTransferItemVO.setEntryDate(_currentDate);
		_receiverTransferItemVO.setEntryDateTime(_currentDate);
		_receiverTransferItemVO.setEntryType(PretupsI.CREDIT);
		_receiverTransferItemVO.setPrefixID(_receiverVO.getPrefixID());
		_receiverTransferItemVO.setTransferStatus(PretupsErrorCodesI.RECEIVER_UNDERPROCESS_SUCCESS);
	}


	/**
	 * Method that will add entry in Transfer Table if not added else update the records
	 * @param p_con
	 */
	private void addEntryInTransfers(Connection p_con)
	{
		final String METHOD_NAME = "addEntryInTransfers";
		try
		{
			//METHOD FOR INSERTING AND UPDATION IN C2S Transfer Table
			if(!_transferDetailAdded && _transferEntryReqd)
				if(PretupsI.BCU_USER.equalsIgnoreCase(_requestVO.getRequestorCategoryCode())||PretupsI.CUSTOMER_CARE.equalsIgnoreCase(_requestVO.getRequestorCategoryCode()))
				{
					_c2sTransferVO.setCreatedBy("SYSTEM");
					_c2sTransferVO.setModifiedBy(PretupsI.C2S_REV_BATCH);
					ChannelTransferBL.addC2STransferDetails(p_con, _c2sTransferVO);
				}
				else
				{
					ChannelTransferBL.addC2STransferDetails(p_con, _c2sTransferVO);
				}
				// ChannelTransferBL.addC2STransferDetails(p_con, _c2sTransferVO);// add
				// transfer
				// details
				// in
				// database
			else if (_transferDetailAdded) {
				_c2sTransferVO.setModifiedOn(new Date());
				_c2sTransferVO.setModifiedBy(PretupsI.C2S_REV_BATCH);
				ChannelTransferBL.updateC2STransferDetails(p_con, _c2sTransferVO);// add
			}
			p_con.commit();
		}
		catch(BTSLBaseException be)
		{
			_log.errorTrace(METHOD_NAME,be);
			if(!_isCounterDecreased && _decreaseTransactionCounts)
			{
				LoadController.decreaseTransactionLoad(_transferID,_senderNetworkCode,LoadControllerI.DEC_LAST_TRANS_COUNT);
				_isCounterDecreased=true;
			}
			_log.error("addEntryInTransfers",_transferID,"BTSLBaseException while adding transfer details in database:"+be.getMessage());
		}
		catch(Exception e)
		{
			_log.errorTrace(METHOD_NAME,e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2SBulkReversalController[addEntryInTransfers]",_transferID,_senderMSISDN,_senderNetworkCode,"Exception:"+e.getMessage());
			if(!_isCounterDecreased && _decreaseTransactionCounts)
			{
				LoadController.decreaseTransactionLoad(_transferID,_senderNetworkCode,LoadControllerI.DEC_LAST_TRANS_COUNT);
				_isCounterDecreased=true;
			}
			_log.error("addEntryInTransfers",_transferID,"Exception while adding transfer details in database:"+e.getMessage());
		}
	}

	static synchronized void generateReversalTransferID(TransferVO p_transferVO)
	{		
		final String METHOD_NAME="generateReversalTransferID";
		String transferID=null;
		String minut2Compare=null;
		Date mydate = null;
		try
		{
			mydate = new Date();
			p_transferVO.setCreatedOn(mydate);
			minut2Compare = _sdfCompare.format(mydate);
			int currentMinut=Integer.parseInt(minut2Compare);  		

			if(currentMinut !=_prevMinut)
			{
				_transactionIDCounter=1;
				_prevMinut=currentMinut;	  			 
			}
			else
			{
				_transactionIDCounter++;	  			 
			}
			if(_transactionIDCounter==0)
				throw new BTSLBaseException("C2SBulkReversalController","generateReversalTransferID",PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
			transferID=_operatorUtil.formatTransferID(p_transferVO,_transactionIDCounter,"X");
			if(transferID==null)
				throw new BTSLBaseException("C2SBulkReversalController","generateReversalTransferID",PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
			p_transferVO.setTransferID(transferID);			 
		}
		catch(Exception e)
		{
			_log.errorTrace(METHOD_NAME,e);
		}
	}
	
	private void copyOldTxnToNew(C2STransferVO oldTransferVO, C2STransferVO newTransferVO2) throws BTSLBaseException {
		final String METHOD_NAME = "copyOldTxnToNew";
		StringBuilder loggerValue= new StringBuilder(); 
		if(_log.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Before OptUtil Tax3:Tax4=");
			loggerValue.append(newTransferVO2.getReceiverTax3Rate());
			loggerValue.append(":");
			loggerValue.append(newTransferVO2.getReceiverTax4Rate());
			_log.debug(METHOD_NAME, loggerValue);
		}
			try{

			oldTransferVO.setReceiverMsisdn(newTransferVO2.getReceiverMsisdn());
			oldTransferVO.setRequestedAmount(newTransferVO2.getRequestedAmount());
			oldTransferVO.setTransferValue(newTransferVO2.getTransferValue());
			//oldTransferVO.setErrorCode(newTransferVO2.getErrorCode());
			oldTransferVO.setReceiverValidity(newTransferVO2.getReceiverValidity());
			oldTransferVO.setReceiverTransferValue(newTransferVO2.getReceiverTransferValue());
			oldTransferVO.setReceiverNetworkCode(newTransferVO2.getReceiverNetworkCode());
			oldTransferVO.setCardGroupCode(newTransferVO2.getCardGroupCode());
			oldTransferVO.setCardGroupID(newTransferVO2.getCardGroupID());
			oldTransferVO.setCardGroupSetID(newTransferVO2.getCardGroupID());
			oldTransferVO.setVersion(newTransferVO2.getVersion());
			oldTransferVO.setDifferentialApplicable(newTransferVO2.getDifferentialApplicable());
			oldTransferVO.setDifferentialGiven(newTransferVO2.getDifferentialGiven());
			oldTransferVO.setReceiverTax1Type(newTransferVO2.getReceiverTax1Type());
			oldTransferVO.setReceiverTax1Rate(newTransferVO2.getReceiverTax1Rate());
			oldTransferVO.setReceiverTax1Value(newTransferVO2.getReceiverTax1Value());
			oldTransferVO.setReceiverTax2Type(newTransferVO2.getReceiverTax2Type());
			oldTransferVO.setReceiverTax2Rate(newTransferVO2.getReceiverTax2Rate());
			oldTransferVO.setReceiverTax2Value(newTransferVO2.getReceiverTax2Value());
			if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CGTAX34APP)).booleanValue())
				{
					oldTransferVO.setReceiverTax3Type(newTransferVO2.getReceiverTax3Type());
					oldTransferVO.setReceiverTax3Rate(newTransferVO2.getReceiverTax3Rate());
					oldTransferVO.setReceiverTax3Value(newTransferVO2.getReceiverTax3Value());
					oldTransferVO.setReceiverTax4Type(newTransferVO2.getReceiverTax4Type());
					oldTransferVO.setReceiverTax4Rate(newTransferVO2.getReceiverTax4Rate());
					oldTransferVO.setReceiverTax4Value(newTransferVO2.getReceiverTax4Value());
				}
			oldTransferVO.setReceiverBonusValue(newTransferVO2.getReceiverBonusValue());
			oldTransferVO.setReceiverGracePeriod(newTransferVO2.getReceiverGracePeriod());
			oldTransferVO.setReceiverBonusValidity(newTransferVO2.getReceiverBonusValidity());
			oldTransferVO.setReceiverValPeriodType(newTransferVO2.getReceiverValPeriodType());
			oldTransferVO.setSenderTransferItemVO(newTransferVO2.getSenderTransferItemVO());
			oldTransferVO.setReverseTransferID(newTransferVO2.getReverseTransferID());
			oldTransferVO.setSelectorCode(newTransferVO2.getSelectorCode());
			oldTransferVO.setSubService(newTransferVO2.getSubService());
			oldTransferVO.setProductCode(newTransferVO2.getProductCode());
			oldTransferVO.setProductName(newTransferVO2.getProductName());
			oldTransferVO.setNetworkCode(newTransferVO2.getNetworkCode());
			oldTransferVO.setPenaltyDetails(newTransferVO2.getPenaltyDetails());
			oldTransferVO.setPenalty(newTransferVO2.getPenalty());
			oldTransferVO.setOwnerPenalty(newTransferVO2.getOwnerPenalty());
			oldTransferVO.setRoamPenalty(newTransferVO2.getRoamPenalty());
			oldTransferVO.setRoamPenaltyOwner(newTransferVO2.getRoamPenaltyOwner());
			oldTransferVO.setReceiverAccessFee(newTransferVO2.getReceiverAccessFee());
			
			if(_log.isDebugEnabled())
				{
				loggerValue.setLength(0);
				loggerValue.append("After OptUtil Tax3:Tax4=");
				loggerValue.append(oldTransferVO.getReceiverTax3Rate());
				loggerValue.append(":");
				loggerValue.append(oldTransferVO.getReceiverTax4Rate());
				_log.debug(METHOD_NAME, loggerValue);
				}
			
		}catch (Exception e) {
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorUtil[copyOldTxnToNew]","","","","Exception while Copy Property from db " +" ,getting Exception="+e.getMessage());
			throw new BTSLBaseException(this,METHOD_NAME,PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}

	}
	
	/**
	 * Method to populate the service interface details based on the action and service type
	 * @param action
	 * @throws BTSLBaseException
	 */
	public void populateServiceInterfaceDetails(Connection p_con,String action) throws BTSLBaseException
	{
		String receiverNetworkCode=_receiverVO.getNetworkCode();
		long receiverPrefixID=_receiverVO.getPrefixID();
		boolean isReceiverFound=false;
		//Check if receiver info is already found in DB or not
		//If yes then not go for ineterface routing again because it was getted at the time of validation
		//This block is executed for update and validate both
		if((!_receiverInterfaceInfoInDBFound && action.equals(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION)) || action.equals(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION)) {
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MNP_ALLOWED))).booleanValue())
			{
				isReceiverFound=getInterfaceRoutingDetailsForMNP(p_con,_receiverMSISDN,receiverPrefixID,_receiverVO.getSubscriberType(),receiverNetworkCode,_serviceType,_type,PretupsI.USER_TYPE_RECEIVER,action);
			}
			if(!isReceiverFound)
			isReceiverFound=getInterfaceRoutingDetails(p_con,_receiverMSISDN,receiverPrefixID,_receiverVO.getSubscriberType(),receiverNetworkCode,_serviceType,_type,PretupsI.USER_TYPE_RECEIVER,action);
		} else {
			isReceiverFound=true;
		}

		if(!isReceiverFound)
			throw new BTSLBaseException("C2SPrepaidReversalController","populateServiceInterfaceDetails",PretupsErrorCodesI.C2S_ERROR_NOTFOUND_SERVICEINTERFACEMAPPING);
	}
	
	
	/**
	 * Method to get the interface details based on the parameters
	 * @param p_con
	 * @param p_msisdn
	 * @param p_prefixID
	 * @param p_subscriberType
	 * @param p_networkCode
	 * @param p_serviceType : RC or REG etc
	 * @param p_interfaceCategory: PRE or POST
	 * @param p_userType: RECEIVER ONLY
	 * @param p_action: VALIDATE OR UPDATE
	 * @return
	 */
	private boolean getInterfaceRoutingDetails(Connection p_con,String p_msisdn,long p_prefixID,String p_subscriberType,String p_networkCode,String p_serviceType,String p_interfaceCategory,String p_userType,String p_action)  throws BTSLBaseException
	{
		final String METHOD_NAME="getInterfaceRoutingDetails";
		StringBuilder loggerValue= new StringBuilder(); 
		if(_log.isDebugEnabled()) 
			{
			loggerValue.setLength(0);
			loggerValue.append(" Entered with MSISDN=");
			loggerValue.append(p_msisdn);
			loggerValue.append(" Prefix ID=");
			loggerValue.append(p_prefixID);
			loggerValue.append(" p_subscriberType=");
			loggerValue.append(p_subscriberType);
			loggerValue.append(p_networkCode);
			loggerValue.append(" =");
			loggerValue.append(p_networkCode);
			loggerValue.append(" p_serviceType=");
			loggerValue.append(p_serviceType);
			loggerValue.append(" p_interfaceCategory=");
			loggerValue.append(p_interfaceCategory);
			loggerValue.append(" p_userType=");
			loggerValue.append(p_userType);
			loggerValue.append(" p_action=");
			loggerValue.append(p_action);	_log.debug(METHOD_NAME,loggerValue);
			}
			boolean isSuccess=false;
		/* Get the routing control parameters based on network code , service and interface category
		 * 1. Check if database check is required
		 * 2. If required then check in database whether the number is present
		 * 3. If present then Get the interface ID from the same and send request to interface to validate the same
		 * 4. If not found then Get the interface ID On the Series basis and send request to interface to validate the same
		 */
		SubscriberRoutingControlVO subscriberRoutingControlVO=SubscriberRoutingControlCache.getRoutingControlDetails(p_networkCode+"_"+p_serviceType+"_"+p_interfaceCategory);
		try
		{
			if(_log.isDebugEnabled()) _log.debug(METHOD_NAME," subscriberRoutingControlVO="+subscriberRoutingControlVO);

			if(subscriberRoutingControlVO!=null)
			{
				if(subscriberRoutingControlVO.isDatabaseCheckBool())
				{
					if(p_interfaceCategory.equalsIgnoreCase(PretupsI.INTERFACE_CATEGORY_POST))
					{
						WhiteListVO whiteListVO=PretupsBL.validateNumberInWhiteList(p_con,p_msisdn);
						if(whiteListVO!=null)
						{
							ListValueVO listValueVO=whiteListVO.getListValueVO();
							if(p_action.equals(PretupsI.INTERFACE_VALIDATE_ACTION))
								_receiverInterfaceInfoInDBFound=true;
							_externalID=listValueVO.getIDValue();
							_interfaceStatusType=listValueVO.getStatusType();
							isSuccess=true;
							_c2sTransferVO.setReceiverInterfaceStatusType(_interfaceStatusType);
							_receiverTransferItemVO.setInterfaceID(listValueVO.getValue());
							_receiverTransferItemVO.setInterfaceType(_type);
							_receiverTransferItemVO.setInterfaceHandlerClass(listValueVO.getLabel());
							if(PretupsI.YES.equals(listValueVO.getType()))
								_c2sTransferVO.setUnderProcessMsgReq(true);
							_receiverAllServiceClassID=listValueVO.getTypeName();
							_receiverVO.setPostOfflineInterface(true);
							if(!PretupsI.YES.equals(listValueVO.getStatus()) && PretupsI.INTERFACE_STATUS_TYPE_MANUAL.equals(listValueVO.getStatusType()))
							{
								//ChangeID=LOCALEMASTER
								//Check which language message to be sent from the locale master table for the perticuler locale.
								if(PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(_senderLocale)).getMessage()))								
									_c2sTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo());
								else 
									_c2sTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo2());
								throw new BTSLBaseException(this,METHOD_NAME,PretupsErrorCodesI.INTERFACE_NOT_ACTIVE);
							}
							_receiverTransferItemVO.setAccountStatus(whiteListVO.getAccountStatus());
							_receiverTransferItemVO.setServiceClassCode(whiteListVO.getServiceClassCode());
							_receiverTransferItemVO.setReferenceID(whiteListVO.getAccountID());
							_receiverTransferItemVO.setPreviousBalance(whiteListVO.getCreditLimit());
							_receiverSubscriberType=p_interfaceCategory;
							_imsi=whiteListVO.getImsi();
							//If use interface language flag is Y in service types table then update the receiver locale
							if("Y".equals(_requestVO.getUseInterfaceLanguage()))
							{
								try
								{
									_receiverLocale=new Locale(whiteListVO.getLanguage(),whiteListVO.getCountry());
								}
								catch(Exception e){
									_log.error(METHOD_NAME, "Exception:"+ e.getMessage());
									_log.errorTrace(METHOD_NAME,e);
									EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"C2SPrepaidReversalController[getInterfaceRoutingDetails]",_transferID,_receiverMSISDN,"","Exception: Notification language from white list is not defined in system p_language: "+whiteListVO.getLanguage()+" country="+whiteListVO.getCountry());
								}
							}
						}
						else if(subscriberRoutingControlVO.isSeriesCheckBool())
						{

							isSuccess=performSeriesBasedRouting(p_prefixID,p_subscriberType,p_action,p_networkCode);
						}
						else
						{
							isSuccess=false;
							throw new BTSLBaseException(this,METHOD_NAME,PretupsErrorCodesI.NUMBER_NOT_EXISTS_IN_WHITELIST);
						}
					}
				}
				else if(subscriberRoutingControlVO.isSeriesCheckBool())
				{

					isSuccess=performSeriesBasedRouting(p_prefixID,p_subscriberType,p_action,p_networkCode);
				}
				else
					isSuccess=false;
			}
			else
			{
				//This event is raised by ankit Z on date 3/8/06 for case when entry not found in routing control and considering series based routing
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,"C2SPrepaidReversalController[getInterfaceRoutingDetails]",_transferID,_senderMSISDN,_senderNetworkCode,"Exception:Routing control information not defined so performing series based routing");
				isSuccess=performSeriesBasedRouting(p_prefixID,p_subscriberType,p_action,p_networkCode);
			}
		}
		catch(BTSLBaseException be)
		{
			_log.error(METHOD_NAME, "BTSLException:"+ be.getMessage());
			_log.errorTrace(METHOD_NAME,be);
			throw be;
		}
		catch(Exception e)
		{
			_log.error(METHOD_NAME, "Exception:"+ e.getMessage());
			_log.errorTrace(METHOD_NAME,e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2SPrepaidReversalController[getInterfaceRoutingDetails]",_transferID,_senderMSISDN,_senderNetworkCode,"Exception:"+e.getMessage());
			isSuccess=false;
			throw new BTSLBaseException(this,METHOD_NAME,PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if(_log.isDebugEnabled()) _log.debug(METHOD_NAME," Exiting with isSuccess="+isSuccess);
		return isSuccess;
	}

	
	private boolean getInterfaceRoutingDetailsForMNP(Connection p_con,String p_msisdn,long p_prefixID,String p_subscriberType,String p_networkCode,String p_serviceType,String p_interfaceCategory,String p_userType,String p_action)  throws BTSLBaseException
	{
		final String methodName = "getInterfaceRoutingDetailsForMNP";
		boolean isSuccess=false;
		ListValueVO listValueVO=null;
		String interfaceID=null;
		String interfaceHandlerClass=null;
		String underProcessMsgReqd=null;
		String allServiceClassID=null;
		try
		{
			if(_receiverVO.getListValueVO()!=null && !(_receiverVO.getListValueVO().getValue()).equals(""))
			{
				interfaceID=_receiverVO.getListValueVO().getValue();
				interfaceHandlerClass= _receiverVO.getListValueVO().getLabel();
				underProcessMsgReqd= _receiverVO.getListValueVO().getType();
				allServiceClassID= _receiverVO.getListValueVO().getTypeName();
				if(p_userType.equals(PretupsI.USER_TYPE_RECEIVER) && p_action.equals(PretupsI.INTERFACE_VALIDATE_ACTION))
					_receiverInterfaceInfoInDBFound=true;
				_externalID= _receiverVO.getListValueVO().getIDValue();
				_interfaceStatusType= _receiverVO.getListValueVO().getStatusType();
				isSuccess=true;
				_receiverSubscriberType=p_interfaceCategory;
			}
			else
			{
				if(!_receiverVO.is_mnpChecked())
		listValueVO=PretupsBL.validateNumberInRoutingDatabaseForMNP(p_con,p_msisdn,p_interfaceCategory);
		if(listValueVO!=null)
		{
			interfaceID=listValueVO.getValue();
			interfaceHandlerClass=listValueVO.getLabel();
			underProcessMsgReqd=listValueVO.getType();
			allServiceClassID=listValueVO.getTypeName();
			if(p_userType.equals(PretupsI.USER_TYPE_RECEIVER) && p_action.equals(PretupsI.INTERFACE_VALIDATE_ACTION))
				_receiverInterfaceInfoInDBFound=true;
			_externalID=listValueVO.getIDValue();
			_interfaceStatusType=listValueVO.getStatusType();
			isSuccess=true;
			_receiverSubscriberType=p_interfaceCategory;
		}
			}
		if(isSuccess && p_userType.equals(PretupsI.USER_TYPE_RECEIVER))
		{
			_receiverTransferItemVO.setInterfaceID(interfaceID);
			_receiverTransferItemVO.setInterfaceType(_type);
			_receiverTransferItemVO.setInterfaceHandlerClass(interfaceHandlerClass);
			if(PretupsI.YES.equals(underProcessMsgReqd)) {
				_c2sTransferVO.setUnderProcessMsgReq(true);
			}
			_receiverAllServiceClassID=allServiceClassID;
			_c2sTransferVO.setReceiverInterfaceStatusType(_interfaceStatusType);
		}
		}
	catch(Exception e)
	{
		_log.errorTrace(methodName, e);
		EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2SPrepaidController[getInterfaceRoutingDetails]",_transferID,_senderMSISDN,_senderNetworkCode,"Exception:"+e.getMessage());
		isSuccess=false;
		throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
	}
	if(_log.isDebugEnabled()) {
		_log.debug(methodName," Exiting with isSuccess="+isSuccess);
	}
		return isSuccess;
	}

	

	/**
	 * Method: performSeriesBasedRouting
	 * This method to perform series based interface routing
	 *  
	 * @param p_prefixID long
	 * @param p_subscriberType String
	 * @param p_action String
	 * 
	 * @return boolean
	 */ // VASTRIX CHANGES
	public boolean performSeriesBasedRouting(long p_prefixID,String p_subscriberType,String p_action,String p_networkCode) throws BTSLBaseException
	{
		final String METHOD_NAME="performSeriesBasedRouting";
		StringBuilder loggerValue= new StringBuilder(); 
		boolean isSuccess=false;
		if(_log.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Entered p_prefixID=");
			loggerValue.append(p_prefixID);
			loggerValue.append(" p_subscriberType=");
			loggerValue.append(p_subscriberType);
			loggerValue.append(" p_action=");
			loggerValue.append(p_action);
			loggerValue.append("p_networkCode");
			loggerValue.append(p_networkCode);
			_log.debug("performSeriesBasedRouting",loggerValue);
		}
		try
		{
			ServiceSelectorInterfaceMappingVO interfaceMappingVO1=null;
			// added by rahul.d to check service selector based check load of interface 
			if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue())
			{
				interfaceMappingVO1=(ServiceSelectorInterfaceMappingVO)ServiceSelectorInterfaceMappingCache.getObject(_serviceType+"_"+_c2sTransferVO.getSubService()+"_"+p_action+"_"+p_networkCode+"_"+p_prefixID);
				if(interfaceMappingVO1!=null)
				{	
					_receiverTransferItemVO.setInterfaceID(interfaceMappingVO1.getInterfaceID());
					_receiverTransferItemVO.setInterfaceType(_type);
					_receiverTransferItemVO.setInterfaceHandlerClass(interfaceMappingVO1.getHandlerClass());
					if(PretupsI.YES.equals(interfaceMappingVO1.getUnderProcessMsgRequired()))
						_c2sTransferVO.setUnderProcessMsgReq(true);
					_receiverAllServiceClassID=interfaceMappingVO1.getAllServiceClassID();
					_externalID=interfaceMappingVO1.getExternalID();
					_interfaceStatusType=interfaceMappingVO1.getStatusType();
					isSuccess=true;		
					_receiverSubscriberType=p_subscriberType;
					if(!PretupsI.YES.equals(interfaceMappingVO1.getInterfaceStatus()) && PretupsI.INTERFACE_STATUS_TYPE_MANUAL.equals(interfaceMappingVO1.getStatusType()))
					{
						if(PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(_senderLocale)).getMessage()))								
							_c2sTransferVO.setSenderReturnMessage(interfaceMappingVO1.getLanguage1Message());
						else 
							_c2sTransferVO.setSenderReturnMessage(interfaceMappingVO1.getLanguage2Message());
						throw new BTSLBaseException(this,METHOD_NAME,PretupsErrorCodesI.INTERFACE_NOT_ACTIVE);
					}
				}
			}
			if(interfaceMappingVO1==null)
			{
				MSISDNPrefixInterfaceMappingVO interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_prefixID, PretupsI.SERVICE_TYPE_CHNL_RECHARGE,p_action); 
				_receiverTransferItemVO.setInterfaceID(interfaceMappingVO.getInterfaceID());
				_receiverTransferItemVO.setInterfaceType(_type);
				_receiverTransferItemVO.setInterfaceHandlerClass(interfaceMappingVO.getHandlerClass());
				if(PretupsI.YES.equals(interfaceMappingVO.getUnderProcessMsgRequired()))
					_c2sTransferVO.setUnderProcessMsgReq(true);
				_receiverAllServiceClassID=interfaceMappingVO.getAllServiceClassID();
				_externalID=interfaceMappingVO.getExternalID();
				_interfaceStatusType=interfaceMappingVO.getStatusType();
				_c2sTransferVO.setReceiverInterfaceStatusType(_interfaceStatusType);
				_receiverSubscriberType=p_subscriberType;
				isSuccess=true;

				if(!PretupsI.YES.equals(interfaceMappingVO.getInterfaceStatus()) && PretupsI.INTERFACE_STATUS_TYPE_MANUAL.equals(interfaceMappingVO.getStatusType()))
				{
					//ChangeID=LOCALEMASTER
					//Check which language message to be sent from the locale master table for the perticuler locale.
					if(PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(_senderLocale)).getMessage()))								
						_c2sTransferVO.setSenderReturnMessage(interfaceMappingVO.getLanguage1Message());
					else 
						_c2sTransferVO.setSenderReturnMessage(interfaceMappingVO.getLanguage2Message());
					throw new BTSLBaseException(this,"performSeriesBasedRouting",PretupsErrorCodesI.INTERFACE_NOT_ACTIVE);
				}
			}
		}
		catch(BTSLBaseException be){
			loggerValue.setLength(0);
			loggerValue.append("BTSLException:");
			loggerValue.append(be.getMessage());
			_log.error(METHOD_NAME, loggerValue );
			_log.errorTrace(METHOD_NAME,be);
			throw be;
		}
		catch(Exception e){
			loggerValue.setLength(0);
			loggerValue.append("Exception:");
			loggerValue.append( e.getMessage());
			_log.error(METHOD_NAME, loggerValue);
			_log.errorTrace(METHOD_NAME,e); 
			throw new BTSLBaseException(this, METHOD_NAME, "");
		}
		if(_log.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Exited isSuccess=");
			loggerValue.append( isSuccess);
			_log.debug("performSeriesBasedRouting",loggerValue);
		}
		return isSuccess;
	}
	
	

}
