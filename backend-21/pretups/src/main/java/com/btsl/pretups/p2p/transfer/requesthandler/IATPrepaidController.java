package com.btsl.pretups.p2p.transfer.requesthandler;

/**
 * @(#)IATPrepaidController.java
 * Copyright(c) 2005, Bharti Telesoft Int. Public Ltd.
 * All Rights Reserved
 *-------------------------------------------------------------------------------------------------
 * Author				Date			History
 *-------------------
 *------------------------------------------------------------------------------
 * Zeeshan Aleem	   September,2014		Initial Creation
 * ------------------------------------------------------------------------------------------------
 */
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.CommonClient;
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
import com.btsl.loadcontroller.ReqNetworkServiceLoadController;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupBL;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.grouptype.businesslogic.GroupTypeProfileVO;
import com.btsl.pretups.iat.businesslogic.IATDAO;
import com.btsl.pretups.iat.businesslogic.IATNWServiceCache;
import com.btsl.pretups.iat.businesslogic.IATNetworkServiceMappingVO;
import com.btsl.pretups.iat.transfer.businesslogic.IATInterfaceVO;
import com.btsl.pretups.iat.transfer.businesslogic.IATTransferItemVO;
import com.btsl.pretups.inter.module.IATInterfaceHandlerI;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.util.InterfaceCloserI;
import com.btsl.pretups.interfaces.businesslogic.InterfaceVO;
import com.btsl.pretups.logging.P2PRequestDailyLog;
import com.btsl.pretups.logging.SMSChargingLog;
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.network.businesslogic.MSISDNPrefixInterfaceMappingCache;
import com.btsl.pretups.network.businesslogic.MSISDNPrefixInterfaceMappingVO;
import com.btsl.pretups.network.businesslogic.NetworkInterfaceModuleCache;
import com.btsl.pretups.network.businesslogic.NetworkInterfaceModuleVO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.btsl.pretups.p2p.subscriber.requesthandler.RegisterationController;
import com.btsl.pretups.p2p.transfer.businesslogic.P2PTransferVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.ResumeSuspendProcess;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.routing.master.businesslogic.InterfaceRoutingControlCache;
import com.btsl.pretups.routing.master.businesslogic.ServiceInterfaceRoutingCache;
import com.btsl.pretups.routing.master.businesslogic.ServiceInterfaceRoutingVO;
import com.btsl.pretups.routing.master.businesslogic.SubscriberRoutingControlCache;
import com.btsl.pretups.routing.master.businesslogic.SubscriberRoutingControlVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.sos.businesslogic.SOSVO;
import com.btsl.pretups.sos.requesthandler.SOSSettlementController;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.subscriber.businesslogic.SubscriberVO;
import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.pretups.vastrix.businesslogic.ServiceSelectorInterfaceMappingCache;
import com.btsl.pretups.vastrix.businesslogic.ServiceSelectorInterfaceMappingVO;
import com.btsl.pretups.whitelist.businesslogic.WhiteListVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.txn.pretups.sos.businesslogic.SOSTxnDAO;

public class IATPrepaidController implements ServiceKeywordControllerI,Runnable {

	private static Log _log = LogFactory.getLog(IATPrepaidController.class.getName());
	private P2PTransferVO _p2pTransferVO=null;
	private TransferItemVO _senderTransferItemVO=null;
	private TransferItemVO _receiverTransferItemVO=null;
	private TransferItemVO _senderCreditBackStatusVO=null;
	private String _senderMSISDN;
	private String _receiverMSISDN;
	private SenderVO _senderVO;
	private ReceiverVO _receiverVO;
	private String _senderSubscriberType;
	private String _senderNetworkCode;
	private Date _currentDate=null;
	private ArrayList _itemList=null;
	private String _intModCommunicationTypeS;
	private String _intModIPS;
	private int _intModPortS;
	private String _intModClassNameS;
	private String _intModCommunicationTypeR;
	private String _intModIPR;
	private int _intModPortR;
	private String _intModClassNameR;
	private String _transferID;
	private String _requestIDStr;
	private Locale _senderLocale=null;
	private Locale _receiverLocale=null;
	private boolean _isCounterDecreased=false;
	private String _type;
	private String _serviceType;
	private boolean _finalTransferStatusUpdate=true;
	private boolean _decreaseTransactionCounts=false;
	private boolean _transferDetailAdded=false;
	private boolean _senderInterfaceInfoInDBFound=false;
	private boolean _receiverInterfaceInfoInDBFound=false;
	private String _senderAllServiceClassID=PretupsI.ALL;
	private String _receiverAllServiceClassID=PretupsI.ALL;
	private String _senderPostBalanceAvailable;
	private String _receiverPostBalanceAvailable;
	private String _senderCreditPostBalanceAvailable;
	private String _receiverExternalID=null;
	private String _senderExternalID=null;
	private RequestVO _requestVO=null;
	private boolean _processedFromQueue=false; //Flag to indicate that request has been processed from Queue
	private boolean _recValidationFailMessageRequired=false; //Whether Receiver Fail Message is required before validation
	private boolean _recTopupFailMessageRequired=false;//Whether Receiver Fail Message is required before topup
	private ServiceInterfaceRoutingVO _serviceInterfaceRoutingVO=null;
	private boolean _useAlternateCategory=false; //Whether to use alternate interface category
	private boolean _performIntfceCatRoutingBeforeVal=false; //Whether we need to perform alternate interface category routing before sending Receiver Validation Request
	private boolean _interfaceCatRoutingDone=false; //To indicate that interface category routing has been done for the process
	private String _oldInterfaceCategory=null; //The initial interface category that has to be used
	private String _newInterfaceCategory=null; //The alternate interface category that has to be used
	private boolean _senderDeletionReqFromSubRouting=false; //Whether to update in Subscriber Routing for sender MSISDN
	private boolean _receiverDeletionReqFromSubRouting=false; //Whether to update in Subscriber Routing for Reciever MSISDN
	private final int SRC_BEFORE_INRESP_CAT_ROUTING=1; //To denote the process from where interface routing has been called, Before IN Validation of Receiver
	private final int SRC_AFTER_INRESP_CAT_ROUTING=2; //To denote the process from where interface routing has been called, After IN Validation of Receiver
	private String _receiverIMSI=null;
	private String _senderIMSI=null;
	private NetworkPrefixVO _networkPrefixVO=null;
	private String _oldDefaultSelector=null;
	private String _newDefaultSelector=null;
	private static OperatorUtilI _operatorUtil=null;
	private String _senderInterfaceStatusType=null;
	private String _receiverInterfaceStatusType=null;
	private static int _transactionIDCounter=0;
	private static int  _prevMinut=0;
	private static SimpleDateFormat _sdfCompare = new SimpleDateFormat ("mm");
	//to update the P2P_Subscriber if subscriber found on Alternate Interface.
	private boolean _isUpdateRequired=false;
	private boolean isRoutingSecond=false;
	private boolean _isSenderRoutingUpdate=false;
	private IATTransferItemVO _iatTransferItemVO;
	private Connection con=null;
	private MComConnectionI mcomCon = null;

	public  IATPrepaidController() {
		_p2pTransferVO=new P2PTransferVO();
		_currentDate=new Date();
		if(BTSLUtil.NullToString(Constants.getProperty("P2P_REC_GEN_FAIL_MSG_REQD_V")).equals("Y")) {
			_recValidationFailMessageRequired=true;
		}
		if(BTSLUtil.NullToString(Constants.getProperty("P2P_REC_GEN_FAIL_MSG_REQD_T")).equals("Y")) {
			_recTopupFailMessageRequired=true;
		}
	}
	//Loads operator specific class
	static {
		final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
		try {
			_operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
		} catch (Exception e) {
			  _log.errorTrace("IATPrepaidController", e);
			
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"IATPrepaidController[initialize]","","","","Exception while loading the class at the call:"+e.getMessage());
		}
	}	
	/**
	 * This is the main entry method for P2P transactions
	 * It calls all other methods based on the process flow.
	 * 1. If any authorised user is sending the transfer request then register the user on the request with status NEW.
	 * 2. Parse the message send in the request.
	 * 3. If the service type used has its Type as BOTH then based on the receiver network code and service type,
	 *    Get the First interface on which the request will be processed.
	 * 4. Validate whether the Service is launched at the Network
	 * 5. Check whether the Payment method is allowed for the Service against the Sender Subscriber Type
	 * 6. Check whether Receiver MSISDN is barred or not.
	 * 7. Load the Receiver Controlling Limits and mark the request as under process.
	 * 8. Generate the Transfer ID
	 * 9. Populate the interface details for the Series Type and interface Category for VALIDATE action. 
	 * 10. Based on the Routing Control, Database Check and Series Check are performed to get the Interface ID.
	 * 11. Validate the Sender Controlling Limits
	 * 12. Validate the Receiver Controlling Limits
	 * 13. Check the transaction Load Counters.
	 * 14. Based on the Flow Type decide whether Validation needs to be done in Thread along with topup or before that
	 * 15. Perform the Validation and Send Request for Sender on the Interface, If Number was not found on the interface
	 * 	   Then perform the alternate routing of the interfaces to validate the same. 
	 * 	   If Found then check whether if Database Check was Y and Number was initailly not found in DB then insert the
	 *     same. If not found even after routing then delete the number from routing database if initially had been found.
	 * 16. Perform the Validation and Send Request for Receiver on the Interface, If Number was not found on the interface
	 * 	   Then perform the alternate routing of the interfaces to validate the same. 
	 * 	   If Found then check whether if Database Check was Y and Number was initailly not found in DB then insert the
	 *     same. If not found even after routing then Check whether alternate Category Routing was required or not.
	 *     If Yes then get the new category and perform the validation process again on the interface and on alternate interfaces
	 *     as well if not found on previous ones. If still not found then Delete the number from routing database if initially had been found.
	 *     Alternate category routing will be performed only if it has not been performed initially.
	 * 17. Calculate the Card group based on the service class IDs
	 * 18. Increase the sender controlling limits.
	 * 19. Insert the record in transaction table with status as Under process.
	 * 20. Populate the interface details for the Series Type and interface Category for TOPUP action. 
	 *     If Database check was Y then do not fire query for search again in DB, use the earlier loaded interface ID.
	 * 21. Send the Sender Debit request 
	 * 22. If Failed then increase the Sender controlling Limits and fail the transaction.
	 * 23. If Success then Send credit Request for receiver at interface.
	 * 24. If Fail then send credit back request of Sender and increase the controlling limits.
	 * 25. If ambigous then check whether credit back request for sender needs to be send or not. 
	 * 26. If Success make the transaction status as success and send message accordingly.               
	 */
	public void process(RequestVO p_requestVO) {
		final String METHOD_NAME="process";
		_requestIDStr=p_requestVO.getRequestIDStr();		
		if(_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME,_requestIDStr,"Entered");
		}
		try {
			_requestVO=p_requestVO;
			_senderVO=(SenderVO)p_requestVO.getSenderVO();
			//If user is not already registered then register the user with status as NEW and Default PIN
			if(_senderVO==null) {
				new RegisterationController().regsiterNewUser(p_requestVO);
				_senderVO=(SenderVO)p_requestVO.getSenderVO();
				_senderVO.setDefUserRegistration(true);
				p_requestVO.setSenderLocale(new Locale(_senderVO.getLanguage(),_senderVO.getCountry()));	
				//If group type counters are allowed to check for controlling for the request gateway then check them
				//This change has been done by ankit on date 14/07/06 for SMS charging
				if(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CTRL_ALLOWED))!=null && ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CTRL_ALLOWED)).indexOf(p_requestVO.getRequestGatewayType())!=-1 && !PretupsI.NOT_APPLICABLE.equals(p_requestVO.getGroupType())) {
					//load the user running and profile counters
					//Check the counters
					//update the counters
					GroupTypeProfileVO groupTypeProfileVO=PretupsBL.loadAndCheckP2PGroupTypeCounters(p_requestVO,PretupsI.GRPT_TYPE_CONTROLLING);
					//If counters reach the profile limit them throw exception
					if(groupTypeProfileVO!=null && groupTypeProfileVO.isGroupTypeCounterReach()) {
						p_requestVO.setDecreaseGroupTypeCounter(false);
						String arr[]={String.valueOf(groupTypeProfileVO.getThresholdValue())};
						if(PretupsI.GRPT_TYPE_FREQUENCY_DAILY.equals(groupTypeProfileVO.getFrequency())) {
							throw new BTSLBaseException(this,METHOD_NAME,PretupsErrorCodesI.ICP2P_ERROR_GRPT_COUNTERS_REACH_LIMIT_D,arr);
						}
						throw new BTSLBaseException(this,METHOD_NAME,PretupsErrorCodesI.ICP2P_ERROR_GRPT_COUNTERS_REACH_LIMIT_M,arr);
					}
				}
			}
			_senderLocale=p_requestVO.getSenderLocale();
			_receiverLocale=p_requestVO.getReceiverLocale();			
			if(_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME,_requestIDStr,"_senderLocale="+_senderLocale+" _receiverLocale="+_receiverLocale);			
			}
			TransactionLog.log("",p_requestVO.getRequestIDStr(),p_requestVO.getFilteredMSISDN(),_senderVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_TXNSTAGE_RECIVED,"Received Request From Receiver",PretupsI.TXN_LOG_STATUS_SUCCESS,"");		
			_type=p_requestVO.getType();		    
			_serviceType=p_requestVO.getServiceType();			
			populateVOFromRequest(p_requestVO);			
			mcomCon = new MComConnection();con=mcomCon.getConnection();
			_senderVO.setModifiedBy(_senderVO.getUserID());
			_senderVO.setModifiedOn(_currentDate);
			_operatorUtil.validateCP2PIRServiceRequest(con,_p2pTransferVO,p_requestVO);
			//Block added to avoid decimal amount in credit transfer
			if(!BTSLUtil.isStringIn(_serviceType,(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DECIMAL_ALLOW_SERVICES))) {
				try
				{
					String displayAmt=PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount());
					Long.parseLong(displayAmt);
				} catch(Exception e) {
					_log.errorTrace(METHOD_NAME, e);
					throw new BTSLBaseException(this,METHOD_NAME,PretupsErrorCodesI.ERROR_INVALID_AMOUNT);
				}
			}
			_receiverLocale=p_requestVO.getReceiverLocale();
			_receiverVO=(ReceiverVO)_p2pTransferVO.getReceiverVO();
			_receiverVO.setMsisdnPrefix(PretupsBL.getMSISDNPrefix(_receiverVO.getMsisdn()));			
			if(!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_ALLOW_SELF_TOPUP))).booleanValue() && _senderVO.getMsisdn().equals(_receiverVO.getMsisdn())) {
				_log.error(METHOD_NAME,_requestIDStr,"Sender and receiver MSISDN are same, Sender MSISDN="+_senderVO.getMsisdn()+" Receiver MSISDN="+_receiverVO.getMsisdn());
				throw new BTSLBaseException("",METHOD_NAME,PretupsErrorCodesI.ERROR_ICP2P_SAME_MSISDN_TRANSFER_NOTALLWD,0,new String[]{_receiverVO.getMsisdn()},null);
			}			
			PretupsBL.getSelectorValueFromCode(p_requestVO);			
			//Get the Interface Category routing details based on the receiver Network Code and Service type 
			if(_type.equals(PretupsI.INTERFACE_CATEGORY_BOTH)) {
				_serviceInterfaceRoutingVO=ServiceInterfaceRoutingCache.getServiceInterfaceRoutingDetails(_receiverVO.getNetworkCode()+"_"+p_requestVO.getServiceType()+"_"+_senderVO.getSubscriberType());
				if (_serviceInterfaceRoutingVO!=null) {
					if(_log.isDebugEnabled()) {
						_log.debug(METHOD_NAME,_requestIDStr,"For ="+_receiverVO.getNetworkCode()+"_"+p_requestVO.getServiceType()+" Got Interface Category="+_serviceInterfaceRoutingVO.getInterfaceType()+" Alternate Check Required="+_serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool()+" Alternate Interface="+_serviceInterfaceRoutingVO.getAlternateInterfaceType()+" _oldDefaultSelector="+_serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode()+"_newDefaultSelector= "+_serviceInterfaceRoutingVO.getAlternateDefaultSelectortCode());
					}					
					_type=_serviceInterfaceRoutingVO.getInterfaceType();
					_oldInterfaceCategory=_type;
					_oldDefaultSelector=_serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode();
					_useAlternateCategory=_serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool();
					_newInterfaceCategory=_serviceInterfaceRoutingVO.getAlternateInterfaceType();
					_newDefaultSelector=_serviceInterfaceRoutingVO.getAlternateDefaultSelectortCode();
				} else {
					_log.info(METHOD_NAME,_requestIDStr,"Service Interface Routing control Not defined, thus using default type="+((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE)));
					EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"IATPrepaidController[process]","",_senderMSISDN,_senderNetworkCode,"Service Interface Routing control Not defined, thus using default type="+((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE)));
					_type=((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE));
					//Changed on 27/05/07 for Service Type selector Mapping
					ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(_p2pTransferVO.getServiceType());
					if(serviceSelectorMappingVO!=null) {
						_oldDefaultSelector=serviceSelectorMappingVO.getSelectorCode();
					}						
				}
			} else {
				_serviceInterfaceRoutingVO=ServiceInterfaceRoutingCache.getServiceInterfaceRoutingDetails(_receiverVO.getNetworkCode()+"_"+p_requestVO.getServiceType()+"_"+_senderVO.getSubscriberType());
				if (_serviceInterfaceRoutingVO!=null)
				{
					if(_log.isDebugEnabled()) {
						_log.debug(METHOD_NAME,_requestIDStr,"For ="+_receiverVO.getNetworkCode()+"_"+p_requestVO.getServiceType()+" Got Interface Category="+_serviceInterfaceRoutingVO.getInterfaceType()+" Alternate Check Required="+_serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool()+" Alternate Interface="+_serviceInterfaceRoutingVO.getAlternateInterfaceType()+" _oldDefaultSelector="+_serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode()+"_newDefaultSelector= "+_serviceInterfaceRoutingVO.getAlternateDefaultSelectortCode());
					}
					_oldDefaultSelector=_serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode();
				} else {
					//Changed on 27/05/07 for Service Type selector Mapping
					ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(_p2pTransferVO.getServiceType());
					if(serviceSelectorMappingVO!=null) {
						_oldDefaultSelector=serviceSelectorMappingVO.getSelectorCode();
					}						
					_log.info(METHOD_NAME,_requestIDStr,"Service Interface Routing control Not defined, thus using default Selector="+_oldDefaultSelector);
					EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,"IATPrepaidController[process]","",_senderMSISDN,_senderNetworkCode,"Service Interface Routing control Not defined, thus using default selector="+_oldDefaultSelector);
				}
			}
			if(_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME,_requestIDStr,"_receiverVO:"+_receiverVO);
			}
			//check service payment mapping
			_senderSubscriberType=_senderVO.getSubscriberType();
			//By Default Entry, will be overridden later in the file
			_p2pTransferVO.setTransferCategory(_senderSubscriberType+"-"+_type);
			if(_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME,_requestIDStr,"Starting with transfer Category as :"+_p2pTransferVO.getTransferCategory());			
			}
			_senderNetworkCode=_senderVO.getNetworkCode();
			_senderMSISDN=((SubscriberVO)_p2pTransferVO.getSenderVO()).getMsisdn();
			_receiverMSISDN=((SubscriberVO)_p2pTransferVO.getReceiverVO()).getMsisdn();
			_receiverVO.setModule(_p2pTransferVO.getModule());
			_receiverVO.setCreatedDate(_currentDate);
			_receiverVO.setLastTransferOn(_currentDate);
			_p2pTransferVO.setReceiverMsisdn(_receiverMSISDN);
			_p2pTransferVO.setReceiverNetworkCode(_senderVO.getNetworkCode());			
			_p2pTransferVO.setSubService(p_requestVO.getReqSelector());
			_p2pTransferVO.setRequestStartTime(p_requestVO.getRequestStartTime());
			_receiverVO.setSubscriberType("PRE");
			_receiverVO.setNetworkCode(_senderNetworkCode);                        
			_p2pTransferVO.setReceiverAllServiceClassID(_receiverAllServiceClassID);	
			//Validates the network service status
			PretupsBL.validateNetworkService(_p2pTransferVO);			
			_receiverVO.setUnmarkRequestStatus(true);
			try {
				con.commit();
			} catch(Exception e) { 
				_log.errorTrace(METHOD_NAME, e);
				throw new BTSLBaseException("IATPrepaidController",METHOD_NAME,PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION);
			}
			processTransfer(con);
			p_requestVO.setTransactionID(_transferID);
			_receiverVO.setLastTransferID(_transferID);				
			//get IATtransactionItemVO from c2s transferVO. This was set during validation of RR service request format.
			//This VO would be used to make entries in P2P_IAT_TRANSFER_ITEMS table.
			_iatTransferItemVO=_p2pTransferVO.getIatTransferItemVO();
			//set sender System transaction id in IATtransactionItemVO 
			_iatTransferItemVO.setIatSenderTxnId(_transferID);
			_iatTransferItemVO.setSendingNWTimestamp(_currentDate);
			_receiverTransferItemVO.setServiceClassCode(_receiverAllServiceClassID);		
			TransactionLog.log(_transferID,p_requestVO.getRequestIDStr(),p_requestVO.getFilteredMSISDN(),_senderVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Generated Transfer ID",PretupsI.TXN_LOG_STATUS_SUCCESS,"Source Type="+_p2pTransferVO.getSourceType()+" Gateway Code="+_p2pTransferVO.getRequestGatewayCode());
			//populate payment and service interface details for validate action
			populateIATServiceDetails();
			populateServicePaymentInterfaceDetails(con,PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION);
			_p2pTransferVO.setTransferCategory(_senderSubscriberType+"-"+_type);				
			_p2pTransferVO.setSenderAllServiceClassID(_senderAllServiceClassID);
			_p2pTransferVO.setReceiverAllServiceClassID(_receiverAllServiceClassID);				
			//validate sender limits before Interface Validations
			SubscriberBL.validateSenderLimits(con,_p2pTransferVO,PretupsI.TRANS_STAGE_BEFORE_INVAL);
			//Change is done for ID=SUBTYPVALRECLMT
			//This chenge is done to set the receiver subscriber type in transfer VO
			//This will be used in validate ReceiverLimit method of PretupsBL when receiverTransferItemVO is null
			_p2pTransferVO.setReceiverSubscriberType(_receiverTransferItemVO.getInterfaceType());
			try {
				con.commit();
			} catch(Exception e) {
				_log.errorTrace(METHOD_NAME, e);
				throw new BTSLBaseException("IATPrepaidController",METHOD_NAME,PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION);
			}
			if (mcomCon != null) {
				mcomCon.close("IATPrepaidController#process");
				mcomCon = null;
			}
			con=null;
			//Checks the Various loads
			checkTransactionLoad();
			_decreaseTransactionCounts=true;				
			//Checks If flow type is common then validation will be performed before sending the 
			//response to user and if it is thread based then validation will also be performed in thread 
			//along with topup
			if(p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON)) {
				//Process validation requests
				processValidationRequest();
				p_requestVO.setSenderMessageRequired(_p2pTransferVO.isUnderProcessMsgReq());
				p_requestVO.setSenderReturnMessage(getSenderUnderProcessMessage());
				p_requestVO.setMessageCode(PretupsI.TXN_STATUS_UNDER_PROCESS);
				p_requestVO.setDecreaseLoadCounters(false);
			} else if(p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD)) {
				//Check if message needs to be sent in case of Thread implmentation
				p_requestVO.setSenderReturnMessage(getSndrUPMsgBeforeValidation());
				p_requestVO.setMessageCode(PretupsI.TXN_STATUS_UNDER_PROCESS);
				Thread _controllerThread=new Thread(this);
				_controllerThread.start();
				p_requestVO.setDecreaseLoadCounters(false);
			}				
			//Parameter set to indicate that instance counters will not be decreased in receiver for this transaction 
			p_requestVO.setDecreaseLoadCounters(false);
		} catch(BTSLBaseException be) {			
			_log.errorTrace(METHOD_NAME, be);
			p_requestVO.setSuccessTxn(false);
			if(_senderVO!=null) {
				try {
					if(mcomCon==null) {
						mcomCon = new MComConnection();con=mcomCon.getConnection();
					}						
					SubscriberBL.updateSubscriberLastDetails(con,_p2pTransferVO,_senderVO,_currentDate,PretupsErrorCodesI.TXN_STATUS_FAIL);					
				} catch(BTSLBaseException bex) {
					_log.errorTrace(METHOD_NAME, bex);					
					EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"IATPrepaidController[process]",_transferID,_senderMSISDN,_senderNetworkCode,"Base Exception while updating Subscriber Last Details:"+bex.getMessage());
				} catch(Exception e) {
					_log.errorTrace(METHOD_NAME, e);
					EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"IATPrepaidController[process]",_transferID,_senderMSISDN,_senderNetworkCode,"Exception while updating Subscriber Last Details:"+e.getMessage());
					_p2pTransferVO.setErrorCode(PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION);
				}
				//Unmarking Receiver last request status
				try {
					if(_receiverVO!=null && _receiverVO.isUnmarkRequestStatus()) {
						PretupsBL.unmarkReceiverLastRequest(con,p_requestVO.getRequestIDStr(),_receiverVO);
					}
				} catch(BTSLBaseException bex) {
					_log.errorTrace(METHOD_NAME, bex);				
					EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"IATPrepaidController[process]",_transferID,_senderMSISDN,_senderNetworkCode,"Leaving Reciever Unmarked Base Exception:"+bex.getMessage());
				} catch(Exception e) {
					_log.errorTrace(METHOD_NAME, e);
					EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"IATPrepaidController[process]",_transferID,_senderMSISDN,_senderNetworkCode,"Leaving Reciever Unmarked Exception:"+e.getMessage());
					_p2pTransferVO.setErrorCode(PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION);
				}	
			}
			_p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			if(_recValidationFailMessageRequired) {
				if(_p2pTransferVO.getReceiverReturnMsg()==null || !((BTSLMessages)_p2pTransferVO.getReceiverReturnMsg()).isKey()) {
					if(_transferID!=null) {
						_p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.ICP2P_RECEIVER_FAIL,new String[]{String.valueOf(_transferID),PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount())}));
					} else {
						_p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.ICP2P_FAIL_R,new String[]{PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount())}));
					}
				}
			}
			if(!BTSLUtil.isNullString(_p2pTransferVO.getSenderReturnMessage())) {
				p_requestVO.setSenderReturnMessage(_p2pTransferVO.getSenderReturnMessage());		
			}
			if(be.isKey()) {
				if(BTSLUtil.isNullString(_p2pTransferVO.getErrorCode())) {
					_p2pTransferVO.setErrorCode(be.getMessageKey());
				}
				p_requestVO.setMessageCode(be.getMessageKey());
				p_requestVO.setMessageArguments(be.getArgs());
			} else {
				_p2pTransferVO.setErrorCode(PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION);
			}
			if(_transferID!=null && _decreaseTransactionCounts) {
				LoadController.decreaseTransactionLoad(_transferID,_senderNetworkCode,LoadControllerI.DEC_LAST_TRANS_COUNT);
				_isCounterDecreased=true;
			}
			TransactionLog.log(_transferID,_requestIDStr,p_requestVO.getFilteredMSISDN(),"",PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,_p2pTransferVO.getSenderReturnMessage(),PretupsI.TXN_LOG_STATUS_FAIL,"Getting Code="+p_requestVO.getMessageCode());
//			Populate the P2PRequestDailyLog and log
			P2PRequestDailyLog.log(P2PRequestDailyLog.populateP2PRequestDailyLogVO(_requestVO, _p2pTransferVO));		
		} catch(Exception e) {
			_log.errorTrace(METHOD_NAME, e);
			p_requestVO.setSuccessTxn(false);
			try {
				if(mcomCon==null) {
					mcomCon = new MComConnection();con=mcomCon.getConnection();
				}
				SubscriberBL.updateSubscriberLastDetails(con,_p2pTransferVO,_senderVO,_currentDate,PretupsErrorCodesI.TXN_STATUS_FAIL);
			} catch(BTSLBaseException bex) {
				_log.errorTrace(METHOD_NAME, bex);
				_p2pTransferVO.setErrorCode(PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION);				
			} catch(Exception ex) {
				_log.errorTrace(METHOD_NAME, ex);
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"IATPrepaidController[process]",_transferID,_senderMSISDN,_senderNetworkCode,"Exception:"+e.getMessage());
				_p2pTransferVO.setErrorCode(PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION);
			}				
			try {
				if(_receiverVO!=null && _receiverVO.isUnmarkRequestStatus()) {
					PretupsBL.unmarkReceiverLastRequest(con,p_requestVO.getRequestIDStr(),_receiverVO);
				}
			} catch(BTSLBaseException bex) {
				_log.errorTrace(METHOD_NAME, bex);
				_p2pTransferVO.setErrorCode(PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION);				
			} catch(Exception ex1) {
				_log.errorTrace(METHOD_NAME, ex1);
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"IATPrepaidController[process]",_transferID,_senderMSISDN,_senderNetworkCode,"Exception:"+ex1.getMessage());
				_p2pTransferVO.setErrorCode(PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION);
			}	
			if(_recValidationFailMessageRequired) {
				if(_p2pTransferVO.getReceiverReturnMsg()==null || !((BTSLMessages)_p2pTransferVO.getReceiverReturnMsg()).isKey()) {
					if(_transferID!=null) {
						_p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.ICP2P_RECEIVER_FAIL,new String[]{String.valueOf(_transferID),PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount())}));
					} else {
						_p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.ICP2P_FAIL_R,new String[]{PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount())}));
					}
				}
			}
			_p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			_p2pTransferVO.setErrorCode(PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION);
			p_requestVO.setMessageCode(PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION);			
			_log.errorTrace(METHOD_NAME, e);
			if(_transferID!=null && _decreaseTransactionCounts) {
				_isCounterDecreased=true;
				LoadController.decreaseTransactionLoad(_transferID,_senderNetworkCode,LoadControllerI.DEC_LAST_TRANS_COUNT);
			}
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"IATPrepaidController[process]",_transferID,_senderMSISDN,_senderNetworkCode,"Exception:"+e.getMessage());
			TransactionLog.log(_transferID,_requestIDStr,p_requestVO.getFilteredMSISDN(),"",PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,_p2pTransferVO.getSenderReturnMessage(),PretupsI.TXN_LOG_STATUS_FAIL,"Getting Code="+p_requestVO.getMessageCode());
//			Populate the P2PRequestDailyLog and log
			P2PRequestDailyLog.log(P2PRequestDailyLog.populateP2PRequestDailyLogVO(_requestVO, _p2pTransferVO));			
		}
		finally {
			try {
				if(mcomCon==null) {
					mcomCon = new MComConnection();con=mcomCon.getConnection();
				}
				if(_transferID!=null && !_transferDetailAdded && (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON) || (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD) && !p_requestVO.getMessageCode().equals(PretupsI.TXN_STATUS_UNDER_PROCESS)))) {
					addEntryInTransfers(con);
				} else if(_transferID!=null && p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD)) {
					_log.info(METHOD_NAME,_requestIDStr,"Send the message to MSISDN="+p_requestVO.getFilteredMSISDN()+" Transfer ID="+_transferID+" But not added entry in Transfers yet");
				}
			} catch(BTSLBaseException be) {
				_log.errorTrace(METHOD_NAME, be);				
			} catch(Exception e) {
				_log.errorTrace(METHOD_NAME, e);				
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"IATPrepaidController[process]",_transferID,_senderMSISDN,_senderNetworkCode,"Exception:"+e.getMessage());
			}
			if(BTSLUtil.isNullString(p_requestVO.getMessageCode())) {
				p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
			}
			if(_isCounterDecreased) {
				p_requestVO.setDecreaseLoadCounters(false);
			}
			if(con!=null) {
				try {
					con.commit();
				} catch(Exception e) {
					_log.errorTrace(METHOD_NAME, e);
				}
				if (mcomCon != null) {
					mcomCon.close("IATPrepaidController#process");
					mcomCon = null;
				}
				con=null;
			}			
			TransactionLog.log(_transferID,p_requestVO.getRequestIDStr(),p_requestVO.getFilteredMSISDN(),"",PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Leaving the controller",PretupsI.TXN_LOG_STATUS_SUCCESS,"Getting Code="+p_requestVO.getMessageCode());
			if(_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME,"Exiting");
			}
		}
	}
	/**
	 * Method to perform validation request
	 * @throws BTSLBaseException
	 * @throws SQLException 
	 * @throws Exception
	 */
	private void processValidationRequest() throws BTSLBaseException, SQLException
	{
		String methodName = "processValidationRequest";
		if(_log.isDebugEnabled()) {
			_log.debug(methodName,"Entered and performing validations for transfer ID="+_transferID);  
		}
		Connection con=null;
		try
		{		
			NetworkInterfaceModuleVO networkInterfaceModuleVOS=(NetworkInterfaceModuleVO)NetworkInterfaceModuleCache.getObject(_p2pTransferVO.getModule(),_senderNetworkCode,_senderVO.getSubscriberType());
			_intModCommunicationTypeS=networkInterfaceModuleVOS.getCommunicationType();
			_intModIPS=networkInterfaceModuleVOS.getIP();
			_intModPortS=networkInterfaceModuleVOS.getPort();
			_intModClassNameS=networkInterfaceModuleVOS.getClassName();			
			LoadController.incrementTransactionInterCounts(_transferID,LoadControllerI.SENDER_UNDER_VAL);
			CommonClient commonClient=new CommonClient();
			String requestStr=getSenderValidateStr();			
			TransactionLog.log(_transferID,_requestIDStr,_senderMSISDN,_senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_TXNSTAGE_INVAL,requestStr,PretupsI.TXN_LOG_STATUS_SUCCESS,"");
			String senderValResponse=commonClient.process(requestStr,_transferID,_intModCommunicationTypeS,_intModIPS,_intModPortS,_intModClassNameS);		
			TransactionLog.log(_transferID,_requestIDStr,_senderMSISDN,_senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_INVAL,senderValResponse,PretupsI.TXN_LOG_STATUS_SUCCESS,"");			
			if(_log.isDebugEnabled()) {
				_log.debug(methodName,"senderValResponse From IN Module="+senderValResponse);
			}
			_itemList=new ArrayList();			
			_itemList.add(_senderTransferItemVO);
			_itemList.add(_receiverTransferItemVO);
			_p2pTransferVO.setTransferItemList(_itemList);
			try
			{
				//Get the Sender validate response and processes the same
				updateForSenderValidateResponse(senderValResponse);
			} catch(BTSLBaseException be) {
				//TransactionLog.log(_transferID,_requestIDStr,_senderMSISDN,_senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Transaction Failed",PretupsI.TXN_LOG_STATUS_FAIL,"Getting Code="+_senderTransferItemVO.getInterfaceResponseCode());
				LoadController.decreaseResponseCounters(_transferID,PretupsErrorCodesI.TXN_STATUS_FAIL,LoadControllerI.SENDER_VAL_RESPONSE);
				if(_senderDeletionReqFromSubRouting && _senderTransferItemVO.getValidationStatus().equals(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND)) {
					PretupsBL.deleteSubscriberInterfaceRouting(_senderMSISDN,PretupsI.INTERFACE_CATEGORY_PRE);
				}
				mcomCon = new MComConnection();con=mcomCon.getConnection();
				//validate sender limits after Interface Validations
				SubscriberBL.validateSenderLimits(con,_p2pTransferVO,PretupsI.TRANS_STAGE_AFTER_INVAL);
				//This block will send different error code if the user is already registered at a particular interface
				//Category but is not found on that interface while validation request
				if(!_senderVO.isDefUserRegistration() && _senderTransferItemVO.getValidationStatus().equals(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND)) {
					throw new BTSLBaseException("IATPrepaidController", methodName,PretupsErrorCodesI.ICP2P_SENDER_ALREADY_REG_NOT_FOUND_IN_VAL,0,new String[]{((LookupsVO)LookupsCache.getObject(PretupsI.SUBSRICBER_TYPE,_senderVO.getSubscriberType())).getLookupName()},null);
				}
				throw be;
			}
			mcomCon = new MComConnection();con=mcomCon.getConnection();
			LoadController.decreaseResponseCounters(_transferID,PretupsErrorCodesI.TXN_STATUS_SUCCESS,LoadControllerI.SENDER_VAL_RESPONSE);			
			//If request is taking more time till validation of sender than reject the request.
			InterfaceVO interfaceVO=(InterfaceVO)NetworkInterfaceModuleCache.getObject(_senderTransferItemVO.getInterfaceID());
			if((System.currentTimeMillis()-_p2pTransferVO.getRequestStartTime())>interfaceVO.getValExpiryTime()) {
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,"IATPrepaidController[run]",_transferID,_senderMSISDN,_senderNetworkCode,"Exception: System is taking more time till validation of sender");
				throw new BTSLBaseException("IATPrepaidController", methodName,PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION_TKING_TIME_TILL_VAL_S);
			}
			interfaceVO=null;
			//Get the service Class ID based on the code
			PretupsBL.validateServiceClassChecks(con,_senderTransferItemVO,_p2pTransferVO,PretupsI.P2P_MODULE,_requestVO.getServiceType());
			_senderVO.setServiceClassCode(_senderTransferItemVO.getServiceClassCode());
			_senderVO.setUsingAllServiceClass(_senderTransferItemVO.isUsingAllServiceClass());
			//update P2P_SUBSCRIBERS if ldcc and found on ailternate Interface type.
			if(_isUpdateRequired) {
				PretupsBL.updateP2PSubscriberDetail(_senderVO);
			}
			if(_isSenderRoutingUpdate && !_isUpdateRequired) {
				updateSubscriberRoutingDetails(PretupsI.USER_TYPE_SENDER,_p2pTransferVO.getNetworkCode(),_senderTransferItemVO.getInterfaceID(),_senderExternalID,_senderMSISDN,_p2pTransferVO.getPaymentMethodType(),_senderVO.getUserID(),_currentDate);
			}
			else if (_isSenderRoutingUpdate && _isUpdateRequired)
				updateSubscriberAilternateRouting(PretupsI.USER_TYPE_SENDER,_p2pTransferVO.getNetworkCode(),_senderTransferItemVO.getInterfaceID(),_senderExternalID,_senderMSISDN,_p2pTransferVO.getPaymentMethodType(),_senderVO.getUserID(),_currentDate);
			//validate sender limits after Interface Validations
			SubscriberBL.validateSenderLimits(con,_p2pTransferVO,PretupsI.TRANS_STAGE_AFTER_INVAL);			
			//send validation request for receiver			
			PretupsBL.validateServiceClassChecks(con,_receiverTransferItemVO,_p2pTransferVO,PretupsI.P2P_MODULE,_requestVO.getServiceType());
			_receiverVO.setServiceClassCode(_receiverTransferItemVO.getServiceClass());
			_receiverVO.setUsingAllServiceClass(_receiverTransferItemVO.isUsingAllServiceClass());	
			//validate sender receiver service class,validate transfer value
			PretupsBL.validateTransferRule(con,_p2pTransferVO,PretupsI.P2P_MODULE);			
			//calculate card group details
			CardGroupBL.calculateCardGroupDetails(con,_p2pTransferVO,PretupsI.P2P_MODULE,true);			
			//validate sender limits after Card Group Calculations
			SubscriberBL.validateSenderLimits(con,_p2pTransferVO,PretupsI.TRANS_STAGE_AFTER_FIND_CGROUP);
			TransactionLog.log(_transferID,_requestIDStr,_senderMSISDN,_senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"After Card Group Set Id="+_p2pTransferVO.getCardGroupSetID()+" Code"+_p2pTransferVO.getCardGroupCode()+" Card ID="+_p2pTransferVO.getCardGroupID()+" Sender Access fee="+_p2pTransferVO.getSenderAccessFee()+" Tax1 ="+_p2pTransferVO.getSenderTax1Value()+" Tax2="+_p2pTransferVO.getSenderTax1Value()+" Talk Time="+_p2pTransferVO.getSenderTransferValue()+" Receiver Access fee="+_p2pTransferVO.getReceiverAccessFee()+" Tax1 ="+_p2pTransferVO.getReceiverTax1Value()+" Tax2="+_p2pTransferVO.getReceiverTax1Value()+" Bonus="+_p2pTransferVO.getReceiverBonusValue()+" Val Type="+_p2pTransferVO.getReceiverValPeriodType()+" Validity="+_p2pTransferVO.getReceiverValidity()+" Talk Time="+_p2pTransferVO.getReceiverTransferValue(),PretupsI.TXN_LOG_STATUS_SUCCESS,"");	
			//Update Daily Counters for the sender and Buddy if there
			SubscriberBL.increaseTransferOutCounts(con,_senderTransferItemVO.getServiceClass(),_p2pTransferVO,PretupsI.SERVICE_TYPE_P2PRECHARGE_IAT);			
			_p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);			
			//populate payment and service interface details
			populateServicePaymentInterfaceDetails(con,PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);
			//update transfer details in database
			PretupsBL.addTransferDetails(con,_p2pTransferVO);
			_transferDetailAdded=true;
			con.commit();
			if (mcomCon != null) {
				mcomCon.close("IATPrepaidController#processValidationRequest");
				mcomCon = null;
			}
			con=null;
			//Push Under Process Message to Reciever , this might have to be implemented on flag basis whether to send message or not
			if(_p2pTransferVO.isUnderProcessMsgReq()) {
				//In case of Self TopUp,Underprocessrequest message will be given to sender only.
				if(!_p2pTransferVO.getSenderMsisdn().equals(_p2pTransferVO.getReceiverMsisdn())) {	
					(new PushMessage(_receiverMSISDN,getReceiverUnderProcessMessage(),_transferID,_p2pTransferVO.getRequestGatewayCode(),_receiverLocale)).push();
				}
			}			
			//If request is taking more time till credit transfer of subscriber than reject the request.
			interfaceVO=(InterfaceVO)NetworkInterfaceModuleCache.getObject(_senderTransferItemVO.getInterfaceID());
			if((System.currentTimeMillis()-_p2pTransferVO.getRequestStartTime())>interfaceVO.getTopUpExpiryTime()) {
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,"IATPrepaidController[run]",_transferID,_senderMSISDN,_senderNetworkCode,"Exception: System is taking more time till credit transfer");
				throw new BTSLBaseException("IATPrepaidController","run",PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION_TKING_TIME_TILL_TOPUP);
			}
			interfaceVO=null;            
			if(_p2pTransferVO.getMsgGatewayFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON)  || _processedFromQueue) {
				//create new Thread
				Thread _controllerThread=new Thread(this);
				_controllerThread.start();
			}
		} catch(BTSLBaseException be) {
			if(con!=null) {
				con.rollback();
			}
			if(_recValidationFailMessageRequired) {
				if(_p2pTransferVO.getReceiverReturnMsg()==null || !((BTSLMessages)_p2pTransferVO.getReceiverReturnMsg()).isKey()) {
					_p2pTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.ICP2P_RECEIVER_FAIL),new String[]{String.valueOf(_transferID),PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount())}));
				}
			}
			_p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			if(BTSLUtil.isNullString(_p2pTransferVO.getErrorCode())) {
				if(be.isKey()) {
					_p2pTransferVO.setErrorCode(be.getMessageKey());
				} else {
					_p2pTransferVO.setErrorCode(PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION);
				}
			}			
			if(BTSLUtil.isNullString(_receiverTransferItemVO.getTransferStatus()) || _receiverTransferItemVO.getTransferStatus().equals(InterfaceErrorCodesI.SUCCESS)) {
				_receiverTransferItemVO.setTransferStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
				_receiverTransferItemVO.setUpdateStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
				_senderTransferItemVO.setTransferStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
				_senderTransferItemVO.setUpdateStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
			}
			_log.error("IATPrepaidController[processValidationRequest]","Getting BTSL Base Exception:"+be.getMessage());
			throw be;
		} catch(Exception e) {
			if(con!=null) {
				con.rollback();
			}
			if(_recValidationFailMessageRequired) {
				if(_p2pTransferVO.getReceiverReturnMsg()==null || !((BTSLMessages)_p2pTransferVO.getReceiverReturnMsg()).isKey()) {
					_p2pTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.ICP2P_RECEIVER_FAIL),new String[]{String.valueOf(_transferID),PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount())}));
				}
			}
			_p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			if(BTSLUtil.isNullString(_p2pTransferVO.getErrorCode())) {
				_p2pTransferVO.setErrorCode(PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION);
			}
			_log.error("IATPrepaidController[processValidationRequest]","Getting Exception:"+e.getMessage());
			if(BTSLUtil.isNullString(_receiverTransferItemVO.getTransferStatus()) || _receiverTransferItemVO.getTransferStatus().equals(InterfaceErrorCodesI.SUCCESS)) {
				_receiverTransferItemVO.setTransferStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
				_receiverTransferItemVO.setUpdateStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
				_senderTransferItemVO.setTransferStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
				_senderTransferItemVO.setUpdateStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
			}
			throw new BTSLBaseException(this, methodName, "Exception in Processing Validation Request");
		}
		finally {
			if (mcomCon != null) {
				mcomCon.close("IATPrepaidController#processValidationRequest");
				mcomCon = null;
			}
			con=null;
		}
	}	
	/**
	 * Process Transfer Request , Genaerates the Transfer ID and populates the Transfer Items VO
	 * @param p_con
	 * @throws BTSLBaseException
	 */
	public void processTransfer(Connection p_con) throws BTSLBaseException
	{
		if(_log.isDebugEnabled()) {
			_log.debug("processTransfer",_p2pTransferVO.getRequestID(),"Entered");
		}
		try {
			_p2pTransferVO.setTransferDate(_currentDate);
			_p2pTransferVO.setTransferDateTime(_currentDate);
			generateTransferID(_p2pTransferVO);
			_transferID=_p2pTransferVO.getTransferID();
			//set sender transfer item details
			setSenderTransferItemVO();
			//set receiver transfer item details
			setReceiverTransferItemVO();
			PretupsBL.getProductFromServiceType(p_con,_p2pTransferVO,_serviceType,PretupsI.P2P_MODULE);
		} catch(BTSLBaseException be) {
			_p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			if(be.isKey()) {
				_p2pTransferVO.setErrorCode(be.getMessageKey());
			} else {
				_p2pTransferVO.setErrorCode(PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION);
			}
			throw be;
		} catch(Exception e) {
			if(_recValidationFailMessageRequired) {
				if(_p2pTransferVO.getReceiverReturnMsg()==null || !((BTSLMessages)_p2pTransferVO.getReceiverReturnMsg()).isKey()) {
					if(_transferID!=null) {
						_p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.ICP2P_RECEIVER_FAIL,new String[]{String.valueOf(_transferID),PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount())}));
					} else {
						_p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.ICP2P_FAIL_R,new String[]{PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount())}));
					}
				}
			}
			_p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			_p2pTransferVO.setErrorCode(PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION);
			_log.errorTrace("processTransfer", e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"IATPrepaidController[processTransfer]",_transferID,_senderMSISDN,_senderNetworkCode,"Exception:"+e.getMessage());
			throw new BTSLBaseException("IATPrepaidController","processTransfer",PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION);
		}
	}

	/**
	 * This method will perform either topup in thread or both validation and topup on thread based on Flow Type
	 */
	public void run() {
		String methodName = "run";
		if(_log.isDebugEnabled()) {
			_log.debug(methodName,_transferID,"Entered");
		}
		BTSLMessages btslMessages=null; 
		boolean onlyDecreaseCounters=false;
		Connection con=null;
		try {
			//Perform the validation of parties if Flow type is thread
			if(_p2pTransferVO.getMsgGatewayFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD)  && !_processedFromQueue) {
				processValidationRequestInThread();
			}
			//send validation request for sender
			CommonClient commonClient=new CommonClient();
			LoadController.incrementTransactionInterCounts(_transferID,LoadControllerI.SENDER_UNDER_TOP);			
			String requestStr=getSenderDebitAdjustStr();
			TransactionLog.log(_transferID,_requestIDStr,_senderMSISDN,_senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_TXNSTAGE_INTOP,requestStr,PretupsI.TXN_LOG_STATUS_SUCCESS,"");
			String senderDebitResponse=commonClient.process(requestStr,_transferID,_intModCommunicationTypeS,_intModIPS,_intModPortS,_intModClassNameS);
			TransactionLog.log(_transferID,_requestIDStr,_senderMSISDN,_senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_INTOP,senderDebitResponse,PretupsI.TXN_LOG_STATUS_SUCCESS,"");
			if(_log.isDebugEnabled()) {
				_log.debug(methodName,_transferID,"senderDebitResponse From IN Module="+senderDebitResponse);
			}
			try	{
				//Get the Sender Debit response and processes the same				
				updateForSenderDebitResponse(senderDebitResponse);
			} catch(BTSLBaseException be) {
				TransactionLog.log(_transferID,_requestIDStr,_senderMSISDN,_senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Transaction Failed",PretupsI.TXN_LOG_STATUS_FAIL,"Transfer Status="+_p2pTransferVO.getTransferStatus()+" Getting Code="+_senderVO.getInterfaceResponseCode());
				LoadController.decreaseResponseCounters(_transferID,PretupsErrorCodesI.TXN_STATUS_FAIL,LoadControllerI.SENDER_TOP_RESPONSE);
				//If transaction is Ambigous and Preference flag is Set to true (Whether credit back is true in ambigous case)
				//Then credit back the sender
				if(_p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS) && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_SNDR_CREDIT_BK_AMB_STATUS))).booleanValue()  || _p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
					onlyDecreaseCounters=true;
					creditBackSenderForFailedTrans(commonClient,onlyDecreaseCounters);
				}
				//validate sender limits after Interface Updation
				SubscriberBL.validateSenderLimits(null,_p2pTransferVO,PretupsI.TRANS_STAGE_AFTER_INTOP);
				//PretupsBL.unmarkReceiverLastRequest(con,_transferID,_receiverVO);
				throw be;
			}
			LoadController.decreaseResponseCounters(_transferID,PretupsErrorCodesI.TXN_STATUS_SUCCESS,LoadControllerI.SENDER_TOP_RESPONSE);			
			LoadController.incrementTransactionInterCounts(_transferID,LoadControllerI.RECEIVER_UNDER_TOP);
			IATInterfaceVO reqIATInterfaceVO = new IATInterfaceVO();
			setReceiverCreditParams(reqIATInterfaceVO);			
			TransactionLog.log(_transferID,_requestIDStr,_receiverMSISDN,_receiverVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_TXNSTAGE_INTOP,reqIATInterfaceVO.toString(),PretupsI.TXN_LOG_STATUS_SUCCESS,"");			
			IATInterfaceHandlerI handleObj = (IATInterfaceHandlerI)PretupsBL.getIATHandlerObj(_iatTransferItemVO.getIatHandlerClass());
			handleObj.credit(reqIATInterfaceVO);
			TransactionLog.log(_transferID,_requestIDStr,_receiverMSISDN,_receiverVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_INTOP,reqIATInterfaceVO.toString(),PretupsI.TXN_LOG_STATUS_SUCCESS,"");			
			if(_log.isDebugEnabled()) {
				_log.debug(methodName,_transferID,"Got the response from IN Module receiverCreditResponse="+reqIATInterfaceVO.toString());
			}
			//Getting Database connection
			try {
				//updating receiver credit response 
				updateForReceiverCreditResponse(reqIATInterfaceVO);
				//decreasing response counters 
				LoadController.decreaseResponseCounters(_transferID,PretupsErrorCodesI.TXN_STATUS_SUCCESS,LoadControllerI.RECEIVER_TOP_RESPONSE);
			} catch(BTSLBaseException be) {
				TransactionLog.log(_transferID,_requestIDStr,_receiverMSISDN,_receiverVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Transaction Failed",PretupsI.TXN_LOG_STATUS_FAIL,"Transfer Status="+_p2pTransferVO.getTransferStatus()+" Getting Code="+_receiverVO.getInterfaceResponseCode());
				//decreaseing the resposne counters and making 	it success in case of Ambiguous and Fail in case of fail
				if(_p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS)) {
					LoadController.decreaseResponseCounters(_transferID,PretupsErrorCodesI.TXN_STATUS_SUCCESS,LoadControllerI.RECEIVER_TOP_RESPONSE);
				} else {
					LoadController.decreaseResponseCounters(_transferID,PretupsErrorCodesI.TXN_STATUS_FAIL,LoadControllerI.RECEIVER_TOP_RESPONSE);
				}
				//Update the sender back for fail transaction
				//Check Status if Ambigous then credit back preference wise
				if(((_p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS) && ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_SNDR_CREDIT_BK_AMB_STATUS)).booleanValue())) || _p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
					creditBackSenderForFailedTrans(commonClient,onlyDecreaseCounters);
				}
				throw be; 
			} catch(Exception e) {
				TransactionLog.log(_transferID,_requestIDStr,_receiverMSISDN,_receiverVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Transaction Failed",PretupsI.TXN_LOG_STATUS_FAIL,"Transfer Status="+_p2pTransferVO.getTransferStatus()+" Getting Code="+_receiverVO.getInterfaceResponseCode());
				//decreaseing the resposne counters and making 	it success in case of Ambiguous and Fail in case of fail
				if(_p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS)) {
					LoadController.decreaseResponseCounters(_transferID,PretupsErrorCodesI.TXN_STATUS_SUCCESS,LoadControllerI.RECEIVER_TOP_RESPONSE);
				} else { 
					LoadController.decreaseResponseCounters(_transferID,PretupsErrorCodesI.TXN_STATUS_FAIL,LoadControllerI.RECEIVER_TOP_RESPONSE);
				}
				//Update the sender back for fail transaction
				//Check Status if Ambigous then credit back preference wise
				if(((_p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS) && ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_SNDR_CREDIT_BK_AMB_STATUS)).booleanValue())) || _p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
					creditBackSenderForFailedTrans(commonClient,onlyDecreaseCounters);
				}
				throw new BTSLBaseException(this, methodName, "Exception in transaction"); 				
			}//end of catch Exception
			_senderVO.setTotalConsecutiveFailCount(0);
			_senderVO.setTotalTransfers(_senderVO.getTotalTransfers()+1);
			_senderVO.setTotalTransferAmount(_senderVO.getTotalTransferAmount()+_senderTransferItemVO.getRequestValue());
			_senderVO.setLastSuccessTransferDate(_currentDate);
			_p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
			_p2pTransferVO.setErrorCode(null);
			//TransactionLog.log(_transferID,_requestIDStr,_senderMSISDN,_senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Transaction Success",PretupsI.TXN_LOG_STATUS_SUCCESS,"Transfer Status="+_p2pTransferVO.getTransferStatus()+" Transfer Category="+_p2pTransferVO.getTransferCategory());
			//con=OracleUtil.getConnection();
			//validate receiver limits after Interface Updation
			PretupsBL.validateRecieverLimits(null,_p2pTransferVO,PretupsI.TRANS_STAGE_AFTER_INTOP,PretupsI.P2P_MODULE);			
			//For increaseing the counters in network and service type
			ReqNetworkServiceLoadController.increaseRechargeCounters(_requestVO.getInstanceID(),_requestVO.getMessageGatewayVO().getGatewayType(),_senderNetworkCode,_serviceType,_transferID,LoadControllerI.COUNTER_SUCCESS_REQUEST,0,true,_receiverVO.getNetworkCode());
			// real time settlement of LMB on the basis of system preference  //@nu
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SOS_ONLINE_ALLOW))).booleanValue()) {
				if( _p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
					Date currentDate = new Date();
					SOSVO	sosvo=null;
					try {
						if(mcomCon==null) {
							mcomCon = new MComConnection();con=mcomCon.getConnection();
						}
						sosvo =  new SOSTxnDAO().loadSOSDetails(con,currentDate,_receiverMSISDN);
						if (sosvo!=null) {
							sosvo.setCreatedOn(currentDate);
							sosvo.setInterfaceID(_receiverTransferItemVO.getInterfaceID());
							sosvo.setInterfaceHandlerClass(_receiverTransferItemVO.getInterfaceHandlerClass());
							sosvo.setOldExpiryInMillis(_receiverTransferItemVO.getOldExporyInMillis());
							sosvo.setLmbAmountAtIN(_receiverTransferItemVO.getLmbdebitvalue());
							///sosvo.setServiceType(_serviceType);
							sosvo.setSettlmntServiceType(_requestVO.getServiceType()); //samna soin
							sosvo.setLocale(_receiverLocale);
							SOSSettlementController sosSettlementController = new SOSSettlementController();
							sosSettlementController.processSOSRechargeRequest(sosvo);
						} else {
							_log.error(this, "IATPrepaidController", "run" +" No record found in database for this number :"+_receiverMSISDN);
						}
					} catch(BTSLBaseException be) {
						_log.error(this, "IATPrepaidController", "run" +"Transaction ID: "+sosvo.getTransactionID()+"Msisdn"+_receiverMSISDN+"Getting Exception while processing LMB request :"+be);
					}
					finally {
						if(con!=null) {
							try {
								con.commit();
							} catch(Exception e) {
								_log.errorTrace("run", e);
							}
							if (mcomCon != null) {
								mcomCon.close("IATPrepaidController#run");
								mcomCon = null;
							}
							con=null;
						}
					}
				}
			}
		} catch(BTSLBaseException be) {
			_log.errorTrace("run", be);		
			if(BTSLUtil.isNullString(_p2pTransferVO.getErrorCode())) {
				if(be.isKey()) {
					_p2pTransferVO.setErrorCode(be.getMessageKey());
				} else {
					_p2pTransferVO.setErrorCode(PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION);
				}
			}
			if(be.isKey() && _p2pTransferVO.getSenderReturnMessage()==null) {
				btslMessages=be.getBtslMessages();
			} else if(_p2pTransferVO.getSenderReturnMessage()==null) {
				_p2pTransferVO.setSenderReturnMessage(PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION);
			}
			if(_log.isDebugEnabled()) {
				_log.debug("run",_transferID,"Error Code:"+btslMessages.print());
			}
			//For increaseing the counters in network and service type
			ReqNetworkServiceLoadController.increaseRechargeCounters(_requestVO.getInstanceID(),_requestVO.getMessageGatewayVO().getGatewayType(),_senderNetworkCode,_serviceType,_transferID,LoadControllerI.COUNTER_FAIL_REQUEST,0,false,_receiverVO.getNetworkCode());
		} catch(Exception e) {
			_log.errorTrace("run", e);
			if(BTSLUtil.isNullString(_p2pTransferVO.getErrorCode())) {
				_p2pTransferVO.setErrorCode(PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION);
			}
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"IATPrepaidController[run]",_transferID,_senderMSISDN,_senderNetworkCode,"Exception:"+e.getMessage());
			btslMessages=new BTSLMessages(PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION);
			//For increaseing the counters in network and service type
			ReqNetworkServiceLoadController.increaseRechargeCounters(_requestVO.getInstanceID(),_requestVO.getMessageGatewayVO().getGatewayType(),_senderNetworkCode,_serviceType,_transferID,LoadControllerI.COUNTER_FAIL_REQUEST,0,false,_receiverVO.getNetworkCode());
		}
		finally {
			try {
				if(_p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL) && (_p2pTransferVO.getReceiverReturnMsg()==null || !((BTSLMessages)_p2pTransferVO.getReceiverReturnMsg()).isKey())) {
					_p2pTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.ICP2P_RECEIVER_FAIL),new String[]{String.valueOf(_transferID),PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount())}));
				}
				LoadController.decreaseTransactionLoad(_transferID,_senderNetworkCode,LoadControllerI.DEC_LAST_TRANS_COUNT);
				if(mcomCon==null) {
					mcomCon = new MComConnection();con=mcomCon.getConnection();
				}
				try {
					SubscriberBL.updateSubscriberLastDetails(con,_p2pTransferVO,_senderVO,_currentDate,_p2pTransferVO.getTransferStatus());
				} catch(BTSLBaseException bex) {
					_log.errorTrace("run", bex);
				} catch(Exception e) {
					_log.errorTrace("run", e);
					EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"IATPrepaidController[run]",_transferID,_senderMSISDN,_senderNetworkCode,"Not able to update Subscriber Last Details Exception:"+e.getMessage());
				}
				try {
					if(_receiverVO!=null && _receiverVO.isUnmarkRequestStatus()) {
						PretupsBL.unmarkReceiverLastRequest(con,_transferID,_receiverVO);
					}
				} catch(BTSLBaseException bex) {
					_log.errorTrace("run", bex);
				} catch(Exception e) {
					_log.errorTrace("run", e);
					EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"IATPrepaidController[run]",_transferID,_senderMSISDN,_senderNetworkCode,"Not able to unmark Receiver Last Request, Exception:"+e.getMessage());
				}
				if(_finalTransferStatusUpdate) {
					_p2pTransferVO.setModifiedOn(_currentDate);
					_p2pTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
					PretupsBL.updateTransferDetails(con,_p2pTransferVO);
				}
			} catch(BTSLBaseException bex) {
				_log.errorTrace("run", bex);
				try {
					if(con!=null) {
						con.rollback();
					}
				} catch(Exception ex) {
					_log.errorTrace("run", ex);	
				}
				_log.error("run",_transferID,"BTSL Base Exception while updating transfer details in database:"+bex.getMessage());
			} catch(Exception e) {
				_log.errorTrace("run", e);
				try {
					if(con!=null) {
						con.rollback();
					}
				} catch(
						Exception ex) {
					_log.errorTrace("run", ex);
				}				
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"IATPrepaidController[run]",_transferID,_senderMSISDN,_senderNetworkCode,"Exception while updating transfer details in database , Exception:"+e.getMessage());
			}
			if(con!=null) {
				try {
					con.commit();
				} catch(Exception e) {
					_log.errorTrace("run", e);
				}
				if (mcomCon != null) {
					mcomCon.close("IATPrepaidController#run");
					mcomCon = null;
				}
				con=null;
			}
			//If transaction is fail and grouptype counters need to be decrease then decrease the counters
			//This change has been done by ankit on date 14/07/06 for SMS charging
			if(_requestVO.getSenderVO()!=null && !_p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)&& _requestVO.isDecreaseGroupTypeCounter() && ((SenderVO)_requestVO.getSenderVO()).getUserControlGrouptypeCounters()!=null) {
				PretupsBL.decreaseGroupTypeCounters(((SenderVO)_requestVO.getSenderVO()).getUserControlGrouptypeCounters());
			}
			String recAlternetGatewaySMS=BTSLUtil.NullToString(Constants.getProperty("P2P_REC_MSG_REQD_BY_ALT_GW"));
			String reqruestGW=_p2pTransferVO.getRequestGatewayCode();
			if(!BTSLUtil.isNullString(recAlternetGatewaySMS)&& (recAlternetGatewaySMS.split(":")).length>=2) {
				if(reqruestGW.equalsIgnoreCase(recAlternetGatewaySMS.split(":")[0])) {
					reqruestGW=(recAlternetGatewaySMS.split(":")[1]).trim();
					if(_log.isDebugEnabled()) {
						_log.debug("run: Reciver Message push through alternate GW",reqruestGW,"Requested GW was:"+_p2pTransferVO.getRequestGatewayCode());
					}
				}
			}
			PushMessage pushMessages=null;
			//In case of self TopUp,sender and receiver will be same so only one final response message will be given to receiver.
			//Otherwise two final response message.
			if(!_p2pTransferVO.getSenderMsisdn().equals(_p2pTransferVO.getReceiverMsisdn())) {		
				if(!BTSLUtil.isNullString(_p2pTransferVO.getSenderReturnMessage())) {
					pushMessages=(new PushMessage(_senderMSISDN,_p2pTransferVO.getSenderReturnMessage(),_transferID,_p2pTransferVO.getRequestGatewayCode(),_senderLocale));
				} else if(btslMessages!=null) {
					//push error message to sender
					pushMessages=(new PushMessage(_senderMSISDN,BTSLUtil.getMessage(_senderLocale,btslMessages.getMessageKey(),btslMessages.getArgs()),_transferID,_p2pTransferVO.getRequestGatewayCode(),_senderLocale));
				} else if(_p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
					//push success message to sender and receiver
					pushMessages=(new PushMessage(_senderMSISDN,getSenderSuccessMessage(),_transferID,_p2pTransferVO.getRequestGatewayCode(),_senderLocale));
					//(new PushMessage(_receiverMSISDN,getReceiverSuccessMessage(),_transferID,_p2pTransferVO.getRequestGatewayCode(),_receiverLocale)).push();
					if(_senderVO.isActivateStatusReqd()) {
						//TO DO Also update is required if PIN is other then Default PIN 
						(new PushMessage(_senderMSISDN,getSenderRegistrationMessage(),_transferID,reqruestGW,_senderLocale)).push();
					}	
				}
			} else {
				if(btslMessages!=null) {
					//push error message to sender
					pushMessages=(new PushMessage(_senderMSISDN,BTSLUtil.getMessage(_senderLocale,btslMessages.getMessageKey(),btslMessages.getArgs()),_transferID,_p2pTransferVO.getRequestGatewayCode(),_senderLocale));
				}				
			}
			//If transaction is successfull then if group type counters reach limit then send message using gateway that is associated with group type profile
			//This change has been done by ankit on date 14/07/06 for SMS charging
			if(_p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS) && ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CHRG_ALLOWED))!=null && ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CHRG_ALLOWED)).indexOf(_requestVO.getRequestGatewayType())!=-1 && !PretupsI.NOT_APPLICABLE.equals(_requestVO.getGroupType())) {
				try {
					GroupTypeProfileVO groupTypeProfileVO=null;
					//load the user running and profile counters
					//Check the counters
					//update the counters
					groupTypeProfileVO=PretupsBL.loadAndCheckP2PGroupTypeCounters(_requestVO,PretupsI.GRPT_TYPE_CHARGING);
					//if group type counters reach limit then send message using gateway that is associated with group type profile
					if(groupTypeProfileVO!=null && groupTypeProfileVO.isGroupTypeCounterReach()) {
						pushMessages.push(groupTypeProfileVO.getGatewayCode(),groupTypeProfileVO.getAltGatewayCode());//new method will be called here
						SMSChargingLog.log(((SenderVO)_requestVO.getSenderVO()).getUserID(),(((SenderVO)_requestVO.getSenderVO()).getUserChargeGrouptypeCounters()).getCounters(),groupTypeProfileVO.getThresholdValue(),groupTypeProfileVO.getReqGatewayType(),groupTypeProfileVO.getResGatewayType(),groupTypeProfileVO.getNetworkCode(),_requestVO.getGroupType(),_requestVO.getServiceType(),_requestVO.getModule());
					} else
						pushMessages.push();
				} catch(Exception e) {
					_log.errorTrace("run", e);
				}
			} else
				pushMessages.push();
			int messageLength=0;
			String message=getSenderSuccessMessage();
			String messLength=BTSLUtil.NullToString(Constants.getProperty("MSG_LENGTH_GW"));
			if(!BTSLUtil.isNullString(messLength)) {
				messageLength=(new Integer(messLength)).intValue();
			}
			if(((message.length()<messageLength))&&((_p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS))&&(!reqruestGW.equalsIgnoreCase(_p2pTransferVO.getRequestGatewayCode())))) {
				//push success message to sender and receiver
				PushMessage pushMessages1=(new PushMessage(_senderMSISDN,message,_transferID,reqruestGW,_senderLocale));
				pushMessages1.push();
			}
			TransactionLog.log(_transferID,_requestIDStr,_senderMSISDN,_senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Transaction Ending",PretupsI.TXN_LOG_STATUS_SUCCESS,"Transfer Status="+_p2pTransferVO.getTransferStatus()+" Transfer Category="+_p2pTransferVO.getTransferCategory()+" Error Code="+_p2pTransferVO.getErrorCode()+" Message="+_p2pTransferVO.getSenderReturnMessage());
//			Populate the P2PRequestDailyLog and log
			P2PRequestDailyLog.log(P2PRequestDailyLog.populateP2PRequestDailyLogVO(_requestVO, _p2pTransferVO));
			if(_log.isDebugEnabled()) {
				_log.debug("run",_transferID,"Exiting");
			}
		}
	}
	private void setReceiverCreditParams(IATInterfaceVO p_iatInterfaceVO) {
		setReceiverCommonParams(p_iatInterfaceVO);
		p_iatInterfaceVO.setIatAction(PretupsI.INTERFACE_CREDIT_ACTION);
		p_iatInterfaceVO.setIatGraceDays(_receiverTransferItemVO.getGraceDaysStr());
		p_iatInterfaceVO.setIatCardGroupCode(_p2pTransferVO.getCardGroupCode());
		p_iatInterfaceVO.setIatValidityDays(_p2pTransferVO.getReceiverValidity());
		p_iatInterfaceVO.setIatBonusValidityDays(_p2pTransferVO.getReceiverBonusValidity());
		p_iatInterfaceVO.setIatRequestedAmount(_p2pTransferVO.getRequestedAmount());
		p_iatInterfaceVO.setIatSendingNWTimestamp(_iatTransferItemVO.getSendingNWTimestamp());
		p_iatInterfaceVO.setIatInterfaceAmt(_p2pTransferVO.getReceiverTransferValue());
	}	
	private void setReceiverCommonParams(IATInterfaceVO p_iatInterfaceVO) {
		if(_log.isDebugEnabled()) {
			_log.debug("setReceiverCommonParams","Entered");
		}
		p_iatInterfaceVO.setIatReceiverMSISDN(_receiverMSISDN);
		p_iatInterfaceVO.setIatSenderNWTRXID(_transferID);
		p_iatInterfaceVO.setIatSenderNWID(_p2pTransferVO.getNetworkCode());
		p_iatInterfaceVO.setIatInterfaceId(_iatTransferItemVO.getIatCode());
		p_iatInterfaceVO.setIatInterfaceHandlerClass(_iatTransferItemVO.getIatHandlerClass());
		p_iatInterfaceVO.setIatModule(PretupsI.C2S_MODULE);
		p_iatInterfaceVO.setIatCardGrpSelector(_requestVO.getReqSelector());
		p_iatInterfaceVO.setIatINAccessType(PretupsI.CONTROLLER);
		p_iatInterfaceVO.setIatRetailerMsisdn(_senderMSISDN);
		p_iatInterfaceVO.setIatUserType("R");
		p_iatInterfaceVO.setIatServiceType(_serviceType);
		p_iatInterfaceVO.setIatReceiverCountryShortName(_iatTransferItemVO.getIatRecCountryShortName());
		p_iatInterfaceVO.setIatType(_iatTransferItemVO.getIatType());
		p_iatInterfaceVO.setIatSourceType(_p2pTransferVO.getSourceType());
		p_iatInterfaceVO.setIatReceiverCountryCode(_iatTransferItemVO.getIatRcvrCountryCode());
		p_iatInterfaceVO.setIatSenderCountryCode(Integer.parseInt(_iatTransferItemVO.getSenderCountryCode()));
		p_iatInterfaceVO.setSenderId(_senderVO.getUserID());
		p_iatInterfaceVO.setIatRetailerID(_senderMSISDN);
		p_iatInterfaceVO.setIatRcvrNWID(_iatTransferItemVO.getIatRecNWCode());
		p_iatInterfaceVO.setIatNotifyMSISDN(_iatTransferItemVO.getIatNotifyMsisdn());
		if(_log.isDebugEnabled()) {
			_log.debug("setReceiverCommonParams","Exited");
		}
	}

	/**
	 * Method to get the sender regsitration message
	 * @return
	 */
	private String getSenderRegistrationMessage() {
		if(_senderVO.isPinUpdateReqd()) {
			String[] messageArgArray={BTSLUtil.decryptText(_senderVO.getPin())};
			return BTSLUtil.getMessage(_senderLocale,PretupsErrorCodesI.ICP2P_SENDER_AUTO_REG_SUCCESS_WITHPIN,messageArgArray);
		}
		return BTSLUtil.getMessage(_senderLocale,PretupsErrorCodesI.ICP2P_SENDER_AUTO_REG_SUCCESS,null);
	}
	/***
	 * 
	 * Method updated for notification message using service class date 15/05/06
	 */
	private String getSenderSuccessMessage() {
		String key=null;
		String[] messageArgArray=null;
		long remainingMonAmount=0;
		long remainingMonCount=0;
		long remainingDailyAmount=0;
		long remainingDailyCount=0;
		long remainingWeekCount=0;
		long remainingWeekAmount=0;
		//added for updating sender message with remaining threshold amount and count Manisha(01/02/08)
		// messages would be changed in messages.properties per operator's requirement (Monthly/daily/weekly/not applicable). So there would be 4 messages against one key, but only one would be active at a time. Same would be applicable for messages with service class.
		remainingMonAmount=  (_senderVO.getMonthlyMaxTransAmtThreshold()-(_senderVO.getMonthlyTransferAmount()+_p2pTransferVO.getRequestedAmount())); 
		remainingMonCount=  (_senderVO.getMonthlyMaxTransCountThreshold()-(_senderVO.getMonthlyTransferCount()+1));
		remainingDailyAmount=  (_senderVO.getDailyMaxTransAmtThreshold()-(_senderVO.getDailyTransferAmount()+_p2pTransferVO.getRequestedAmount())); 
		remainingDailyCount=  (_senderVO.getDailyMaxTransCountThreshold()-(_senderVO.getDailyTransferCount()+1));
		remainingWeekAmount=  (_senderVO.getWeeklyMaxTransAmtThreshold()-(_senderVO.getWeeklyTransferAmount()+_p2pTransferVO.getRequestedAmount())); 
		remainingWeekCount=  (_senderVO.getWeeklyMaxTransCountThreshold()-(_senderVO.getWeeklyTransferCount()+1));
		if(!"N".equals(_senderPostBalanceAvailable)) {
			messageArgArray=new String[]{_iatTransferItemVO.getIatRcvrCountryCode()+_receiverMSISDN,_transferID,PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()),PretupsBL.getDisplayAmount(_senderTransferItemVO.getTransferValue()),PretupsBL.getDisplayAmount(_receiverTransferItemVO.getTransferValue()),PretupsBL.getDisplayAmount(_senderTransferItemVO.getPostBalance()),PretupsBL.getDisplayAmount(_p2pTransferVO.getSenderAccessFee()),_p2pTransferVO.getSubService(),PretupsBL.getDisplayAmount(remainingDailyAmount),Long.toString(remainingDailyCount),PretupsBL.getDisplayAmount(remainingMonAmount),Long.toString(remainingMonCount),PretupsBL.getDisplayAmount(remainingWeekAmount),Long.toString(remainingWeekCount)};
			if(_p2pTransferVO.getSenderAccessFee()==0) {
				key=PretupsErrorCodesI.ICP2P_SENDER_SUCCESS_WITHOUT_ACCESSFEE;
			} else {
				key=PretupsErrorCodesI.ICP2P_SENDER_SUCCESS;
			}
		} else {
			messageArgArray=new String[]{_iatTransferItemVO.getIatRcvrCountryCode()+_receiverMSISDN,_transferID,PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()),PretupsBL.getDisplayAmount(_senderTransferItemVO.getTransferValue()),PretupsBL.getDisplayAmount(_receiverTransferItemVO.getTransferValue()),PretupsBL.getDisplayAmount(_p2pTransferVO.getSenderAccessFee()),_p2pTransferVO.getSubService(),PretupsBL.getDisplayAmount(remainingDailyAmount),Long.toString(remainingDailyCount),PretupsBL.getDisplayAmount(remainingMonAmount),Long.toString(remainingMonCount),PretupsBL.getDisplayAmount(remainingWeekAmount),Long.toString(remainingWeekCount)};
			key=PretupsErrorCodesI.ICP2P_SENDER_SUCCESS_WITHOUT_POSTBAL;
		}
		if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.NOTIFICATION_SERVICECLASSWISE_SEN))).booleanValue()) {
			String message=null;
			try {
				message= BTSLUtil.getMessage(_senderLocale,key+"_"+_senderTransferItemVO.getServiceClass(),messageArgArray);
				if(BTSLUtil.isNullString(message)) {
					message=BTSLUtil.getMessage(_senderLocale,key,messageArgArray);
				}
				return message;
			} catch(Exception e) {
				_log.errorTrace("getSenderSuccessMessage", e);
				return BTSLUtil.getMessage(_senderLocale,key,messageArgArray);
			}
		}
		return BTSLUtil.getMessage(_senderLocale,key,messageArgArray);
	}
	/**
	 * Method to get the Receiver Ambigous Message
	 * @return
	 */
	private String getReceiverAmbigousMessage() {
		String[] messageArgArray={_senderMSISDN,_transferID,PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount())};
		return BTSLUtil.getMessage(_receiverLocale,PretupsErrorCodesI.ICP2P_RECEIVER_AMBIGOUS_MESSAGE_KEY,messageArgArray);
	}
	/**
	 * Method to get the Receiver Fail Message
	 * @return
	 */
	private String getReceiverFailMessage() {
		String[] messageArgArray={_senderMSISDN,_transferID,PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount())};
		return BTSLUtil.getMessage(_receiverLocale,PretupsErrorCodesI.ICP2P_RECEIVER_FAIL_MESSAGE_KEY,messageArgArray);
	}
	/***
	 * 
	 * Method updated for notification message using service class date 15/05/06
	 */
	private String getReceiverSuccessMessage() {
		String[] messageArgArray=null;
		String key=null;
		//For Get NUMBER BACK Service
		if(_p2pTransferVO.getReceiverTransferItemVO().isNumberBackAllowed()) {
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_SEPARATE_BONUS_REQUIRED))).booleanValue()) {
				_p2pTransferVO.setCalminusBonusvalue(_p2pTransferVO.getReceiverTransferValue()-_p2pTransferVO.getBonusTalkTimeValue());
			}
			//added by vikas kumar for card group updation
			messageArgArray=new String[]{_transferID,PretupsBL.getDisplayAmount(_p2pTransferVO.getReceiverTransferValue()),String.valueOf(_receiverTransferItemVO.getValidity()),_senderMSISDN,PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()),PretupsBL.getDisplayAmount(_p2pTransferVO.getReceiverAccessFee()),_p2pTransferVO.getSubService(),String.valueOf(_p2pTransferVO.getReceiverBonus1()),String.valueOf(_p2pTransferVO.getReceiverBonus2()),PretupsBL.getDisplayAmount(_p2pTransferVO.getBonusTalkTimeValue()),PretupsBL.getDisplayAmount(_p2pTransferVO.getCalminusBonusvalue()),String.valueOf(_p2pTransferVO.getReceiverBonus1Validity()),String.valueOf(_p2pTransferVO.getReceiverBonus2Validity()),String.valueOf(_p2pTransferVO.getReceiverCreditBonusValidity())};
			if(_p2pTransferVO.getBonusTalkTimeValue()==0) {
				key=PretupsErrorCodesI.ICP2P_RECEIVER_GET_NUMBER_BACK_SUCCESS;
			} else {
				key=PretupsErrorCodesI.ICP2P_RECEIVER_GET_NUMBER_BACK_SUCCESS_WITH_BONUS;
			}
		}
		if(!"N".equals(_receiverPostBalanceAvailable)) {
			String dateStr=null;
			try {
				dateStr=BTSLUtil.getDateStringFromDate(_receiverTransferItemVO.getNewExpiry());				
			} catch(Exception e) {
				_log.errorTrace("getReceiverSuccessMessage", e);
				dateStr=String.valueOf(_receiverTransferItemVO.getNewExpiry());
			}
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_SEPARATE_BONUS_REQUIRED))).booleanValue()) {
				_p2pTransferVO.setCalminusBonusvalue(_receiverTransferItemVO.getTransferValue()-_p2pTransferVO.getBonusTalkTimeValue());
			}
			// added by vikas kumar for card group updation sms/mms 
			messageArgArray=new String[]{_transferID,PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()),PretupsBL.getDisplayAmount(_receiverTransferItemVO.getTransferValue()),PretupsBL.getDisplayAmount(_receiverTransferItemVO.getPostBalance()),dateStr,_senderMSISDN,PretupsBL.getDisplayAmount(_p2pTransferVO.getReceiverAccessFee()),_p2pTransferVO.getSubService(),String.valueOf(_p2pTransferVO.getReceiverBonus1()),String.valueOf(_p2pTransferVO.getReceiverBonus2()),PretupsBL.getDisplayAmount(_p2pTransferVO.getBonusTalkTimeValue()),PretupsBL.getDisplayAmount(_p2pTransferVO.getCalminusBonusvalue()),String.valueOf(_p2pTransferVO.getReceiverBonus1Validity()),String.valueOf(_p2pTransferVO.getReceiverBonus2Validity()),String.valueOf(_p2pTransferVO.getReceiverCreditBonusValidity())};
			if(_p2pTransferVO.getBonusTalkTimeValue()==0) {			
				key=PretupsErrorCodesI.ICP2P_RECEIVER_SUCCESS;//return BTSLUtil.getMessage(_receiverLocale,PretupsErrorCodesI.ICP2P_RECEIVER_SUCCESS,messageArgArray);
			} else {
				key=PretupsErrorCodesI.ICP2P_RECEIVER_SUCCESS_WITH_BONUS;
			}
		} else {
//			601:Transaction number {0} to transfer {1} INR from {3} is successful. Transferred value is {2} & access fee is {4} INR. Please check your balance.
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_SEPARATE_BONUS_REQUIRED))).booleanValue()) {
				_p2pTransferVO.setCalminusBonusvalue(_receiverTransferItemVO.getTransferValue()-_p2pTransferVO.getBonusTalkTimeValue());
			}
			// added by vikas kumar fro card group updation 
			messageArgArray=new String[]{_transferID,PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()),PretupsBL.getDisplayAmount(_receiverTransferItemVO.getTransferValue()),_senderMSISDN,PretupsBL.getDisplayAmount(_p2pTransferVO.getReceiverAccessFee()),_p2pTransferVO.getSubService(),String.valueOf(_p2pTransferVO.getReceiverBonus1()),String.valueOf(_p2pTransferVO.getReceiverBonus2()),PretupsBL.getDisplayAmount(_p2pTransferVO.getBonusTalkTimeValue()),PretupsBL.getDisplayAmount(_p2pTransferVO.getCalminusBonusvalue()),String.valueOf(_p2pTransferVO.getReceiverBonus1Validity()),String.valueOf(_p2pTransferVO.getReceiverBonus2Validity()),String.valueOf(_p2pTransferVO.getReceiverCreditBonusValidity())};
			if(_p2pTransferVO.getBonusTalkTimeValue()==0) {
				key=PretupsErrorCodesI.ICP2P_RECEIVER_SUCCESS_WITHOUT_POSTBAL;//return BTSLUtil.getMessage(_receiverLocale,PretupsErrorCodesI.ICP2P_RECEIVER_SUCCESS_WITHOUT_POSTBAL,messageArgArray);
			} else {
				key=PretupsErrorCodesI.ICP2P_RECEIVER_SUCCESS_WITHOUT_POSTBAL_WITH_BONUS;
			}
		}
		if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.NOTIFICATION_SERVICECLASSWISE_REC))).booleanValue()) {
			String message=null; 
			try {
				message= BTSLUtil.getMessage(_receiverLocale,key+"_"+_receiverTransferItemVO.getServiceClass(),messageArgArray);
				if(BTSLUtil.isNullString(message)) {
					message=BTSLUtil.getMessage(_receiverLocale,key,messageArgArray);
				}
				return message;
			} catch(Exception e) {
				_log.errorTrace("getReceiverSuccessMessage", e);
				return BTSLUtil.getMessage(_receiverLocale,key,messageArgArray);
			}
		}
		return BTSLUtil.getMessage(_receiverLocale,key,messageArgArray);
	}

	/**
	 * Populates the Sender Transfer Items VO
	 *
	 */
	private void setSenderTransferItemVO() {
		_senderTransferItemVO=new TransferItemVO();
		//set sender transfer item details
		_senderTransferItemVO.setSNo(1);
		_senderTransferItemVO.setMsisdn(_senderMSISDN);
		_senderTransferItemVO.setRequestValue(_p2pTransferVO.getTransferValue());
		_senderTransferItemVO.setSubscriberType(_senderSubscriberType);
		_senderTransferItemVO.setTransferDate(_currentDate);
		_senderTransferItemVO.setTransferDateTime(_currentDate);
		_senderTransferItemVO.setTransferID(_p2pTransferVO.getTransferID());
		_senderTransferItemVO.setTransferType(PretupsI.TRANSFER_TYPE_TXN);
		_senderTransferItemVO.setUserType(PretupsI.USER_TYPE_SENDER);
		_senderTransferItemVO.setEntryDate(_currentDate);
		_senderTransferItemVO.setEntryDateTime(_currentDate);
		_senderTransferItemVO.setEntryType(PretupsI.DEBIT);
		_senderTransferItemVO.setPrefixID(_senderVO.getPrefixID());
	}
	/**
	 * Populates the Receiver Transfer Items VO
	 *
	 */
	private void setReceiverTransferItemVO() {
		_receiverTransferItemVO=new TransferItemVO();
		_receiverTransferItemVO.setSNo(2);	
		_receiverTransferItemVO.setMsisdn(_receiverMSISDN);
		_receiverTransferItemVO.setRequestValue(_p2pTransferVO.getTransferValue());
		_receiverTransferItemVO.setSubscriberType(_type);
		_receiverTransferItemVO.setTransferDate(_currentDate);
		_receiverTransferItemVO.setTransferDateTime(_currentDate);
		_receiverTransferItemVO.setTransferID(_p2pTransferVO.getTransferID());
		_receiverTransferItemVO.setTransferType(PretupsI.TRANSFER_TYPE_TXN);
		_receiverTransferItemVO.setUserType(PretupsI.USER_TYPE_RECEIVER);
		_receiverTransferItemVO.setEntryDate(_currentDate);
		_receiverTransferItemVO.setEntryType(PretupsI.CREDIT);
		_receiverTransferItemVO.setPrefixID(_receiverVO.getPrefixID());
		_receiverTransferItemVO.setEntryDateTime(_currentDate);
	}

	/**
	 * Method to populate the Interface Details of the sender and receiver based on action specified
	 * @param action Can be Validate / Topup
	 * @throws BTSLBaseException
	 */
	public void populateServicePaymentInterfaceDetails(Connection p_con,String action) throws BTSLBaseException {
		String senderNetworkCode=_senderVO.getNetworkCode();
		String receiverNetworkCode=_receiverVO.getNetworkCode();
		long senderPrefixID=_senderVO.getPrefixID();
		long receiverPrefixID=_receiverVO.getPrefixID();
		boolean isSenderFound=false;
		boolean isReceiverFound=false;
		if(_log.isDebugEnabled()) {
			_log.debug(this,"Getting interface details For Action="+action+" _senderInterfaceInfoInDBFound="+_senderInterfaceInfoInDBFound+" _receiverInterfaceInfoInDBFound="+_receiverInterfaceInfoInDBFound);
		}
		//Avoid searching in the loop again if in validation details was found in database
		//This condition has been changed so that if payment method is not the dafult one then there may be case that default interface will be used for that.
		if(((!_senderInterfaceInfoInDBFound && (_p2pTransferVO.getPaymentMethodKeywordVO()==null||!PretupsI.YES.equals(_p2pTransferVO.getPaymentMethodKeywordVO().getUseDefaultInterface())) )&& action.equals(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION)) || action.equals(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION)) {
			if(_p2pTransferVO.getPaymentMethodKeywordVO()!=null && PretupsI.YES.equals(_p2pTransferVO.getPaymentMethodKeywordVO().getUseDefaultInterface())) {
				if(_log.isDebugEnabled()) {
					_log.debug(this,"For Sender using the Payment Method Default Interface as="+_p2pTransferVO.getPaymentMethodKeywordVO().getDefaultInterfaceID());
				}
				_senderTransferItemVO.setPrefixID(senderPrefixID);
				_senderTransferItemVO.setInterfaceID(_p2pTransferVO.getPaymentMethodKeywordVO().getDefaultInterfaceID());
				_senderTransferItemVO.setInterfaceHandlerClass(_p2pTransferVO.getPaymentMethodKeywordVO().getHandlerClass());
				_senderAllServiceClassID=_p2pTransferVO.getPaymentMethodKeywordVO().getAllServiceClassId();
				_senderExternalID=_p2pTransferVO.getPaymentMethodKeywordVO().getExternalID();
				_senderInterfaceStatusType=_p2pTransferVO.getPaymentMethodKeywordVO().getStatusType();
				_p2pTransferVO.setSenderAllServiceClassID(_senderAllServiceClassID);
				_senderTransferItemVO.setInterfaceType(_p2pTransferVO.getPaymentMethodType());
				_p2pTransferVO.setSenderInterfaceStatusType(_senderInterfaceStatusType);
				if(!PretupsI.YES.equals(_p2pTransferVO.getPaymentMethodKeywordVO().getStatus()) && PretupsI.INTERFACE_STATUS_TYPE_MANUAL.equals(_senderInterfaceStatusType)) {
					if(PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(_senderLocale)).getMessage())) {								
						_p2pTransferVO.setSenderReturnMessage(_p2pTransferVO.getPaymentMethodKeywordVO().getLang1Message());
					} else { 
						_p2pTransferVO.setSenderReturnMessage(_p2pTransferVO.getPaymentMethodKeywordVO().getLang2Message());
					}
					throw new BTSLBaseException(this,"populateServicePaymentInterfaceDetails",PretupsErrorCodesI.INTERFACE_NOT_ACTIVE);
				}
				isSenderFound=true;
			} else {
				isSenderFound=getInterfaceRoutingDetails(p_con,_senderMSISDN,senderPrefixID,_senderVO.getSubscriberType(),senderNetworkCode,_p2pTransferVO.getServiceType(),_p2pTransferVO.getPaymentMethodType(),PretupsI.USER_TYPE_SENDER,action);
			}
		} else
			isSenderFound=true;
		if(!isSenderFound) {
			if(!_senderVO.isDefUserRegistration()) {
				throw new BTSLBaseException("IATPrepaidController","populateServicePaymentInterfaceDetails",PretupsErrorCodesI.ICP2P_SENDER_ALREADY_REG_NOT_FOUND_IN_VAL,0,new String[]{((LookupsVO)LookupsCache.getObject(PretupsI.SUBSRICBER_TYPE,_senderVO.getSubscriberType())).getLookupName()},null);
			}
			throw new BTSLBaseException("IATPrepaidController","populateServicePaymentInterfaceDetails",PretupsErrorCodesI.ICP2P_NOTFOUND_PAYMENTINTERFACEMAPPING);
		}				
	}
	/**
	 * Get the sender String to be send to common Client
	 * @return
	 */
	private String getSenderCommonString() {
		StringBuffer strBuff=null;
		strBuff=new StringBuffer("MSISDN="+_senderMSISDN);
		strBuff.append("&TRANSACTION_ID="+_transferID);
		strBuff.append("&NETWORK_CODE="+_senderVO.getNetworkCode());
		strBuff.append("&INTERFACE_ID="+_senderTransferItemVO.getInterfaceID());
		strBuff.append("&INTERFACE_HANDLER="+_senderTransferItemVO.getInterfaceHandlerClass());
		strBuff.append("&INT_MOD_COMM_TYPE="+_intModCommunicationTypeS);
		strBuff.append("&INT_MOD_IP="+_intModIPS);
		strBuff.append("&INT_MOD_PORT="+_intModPortS);
		strBuff.append("&INT_MOD_CLASSNAME="+_intModClassNameS);
		strBuff.append("&MODULE="+PretupsI.P2P_MODULE);
		strBuff.append("&USER_TYPE=S");
		//added for CRE_INT_CR00029 by ankit Zindal
		strBuff.append("&CARD_GROUP_SELECTOR="+_requestVO.getReqSelector());
		strBuff.append("&REQ_SERVICE="+_serviceType);
		strBuff.append("&INT_ST_TYPE="+_p2pTransferVO.getSenderInterfaceStatusType());
		strBuff.append("&RECEIVER_MSISDN="+_receiverMSISDN);
		strBuff.append("&REQ_AMOUNT="+_p2pTransferVO.getRequestedAmount());
		//Added By Babu Kunwar
		strBuff.append("&SENDER_BUNDLE_ID="+_p2pTransferVO.getSelectorBundleId());
		return strBuff.toString();
	}
	/**
	 * Gets the sender validate Request String
	 * @return
	 */
	public String getSenderValidateStr() {
		StringBuffer strBuff=null;
		strBuff=new StringBuffer(getSenderCommonString());
		strBuff.append("&INTERFACE_ACTION="+PretupsI.INTERFACE_VALIDATE_ACTION);
		strBuff.append("&SERVICE_CLASS="+_senderTransferItemVO.getServiceClassCode());
		strBuff.append("&ACCOUNT_ID="+_senderTransferItemVO.getReferenceID());
		strBuff.append("&ACCOUNT_STATUS="+_senderTransferItemVO.getAccountStatus());
		strBuff.append("&CREDIT_LIMIT="+_senderTransferItemVO.getPreviousBalance());	
		strBuff.append("&SERVICE_TYPE="+_senderSubscriberType+"-"+_type);
		return strBuff.toString();
	}
	/**
	 * Get the sender Debit Request String
	 * @return
	 */
	public String getSenderDebitAdjustStr() {
		StringBuffer strBuff=null;
		strBuff=new StringBuffer(getSenderCommonString());
		strBuff.append("&INTERFACE_ACTION="+PretupsI.INTERFACE_DEBIT_ACTION);
		strBuff.append("&INTERFACE_AMOUNT="+_senderTransferItemVO.getTransferValue());
		strBuff.append("&GRACE_DAYS="+_senderTransferItemVO.getGraceDaysStr());
		strBuff.append("&CARD_GROUP="+_p2pTransferVO.getCardGroupCode());
		strBuff.append("&SERVICE_CLASS="+_senderTransferItemVO.getServiceClassCode());
		strBuff.append("&ACCOUNT_ID="+_senderTransferItemVO.getReferenceID());
		strBuff.append("&ACCOUNT_STATUS="+_senderTransferItemVO.getAccountStatus());
		strBuff.append("&SOURCE_TYPE="+_p2pTransferVO.getSourceType());
		strBuff.append("&PRODUCT_CODE="+_p2pTransferVO.getProductCode());
		strBuff.append("&TAX_AMOUNT="+(_p2pTransferVO.getSenderTax1Value()+_p2pTransferVO.getSenderTax2Value()));
		strBuff.append("&ACCESS_FEE="+_p2pTransferVO.getSenderAccessFee());
		strBuff.append("&SENDER_MSISDN="+_senderMSISDN);
		strBuff.append("&RECEIVER_MSISDN="+_receiverMSISDN);
		strBuff.append("&EXTERNAL_ID="+_senderExternalID);
		strBuff.append("&GATEWAY_CODE="+_requestVO.getRequestGatewayCode());
		strBuff.append("&GATEWAY_TYPE="+_requestVO.getRequestGatewayType());
		strBuff.append("&IMSI="+BTSLUtil.NullToString(_senderIMSI));
		strBuff.append("&SENDER_ID="+((SenderVO)_requestVO.getSenderVO()).getUserID());
		strBuff.append("&SERVICE_TYPE="+_senderSubscriberType+"-"+_type);
		strBuff.append("&ADJUST=Y");
		strBuff.append("&INTERFACE_PREV_BALANCE="+_senderTransferItemVO.getPreviousBalance());		
		// Avinash send the requested amount to IN. to use card group only for reporting purpose.
		strBuff.append("&REQUESTED_AMOUNT="+_p2pTransferVO.getRequestedAmount());
		//Aircel Chennai::SelfTopUp:ASHISH S
		strBuff.append("&BANK_PIN="+((SenderVO)_requestVO.getSenderVO()).getPin());
		strBuff.append("&TAS_ORIGIN_ST_CODE="+_p2pTransferVO.getPaymentMethodType());
		strBuff.append("&CAL_OLD_EXPIRY_DATE="+_senderTransferItemVO.getOldExporyInMillis());//@nu
		if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ENQ_POSTBAL_IN))).booleanValue()) {
			strBuff.append("&ENQ_POSTBAL_IN="+PretupsI.YES);
		} else {
			strBuff.append("&ENQ_POSTBAL_IN="+PretupsI.NO);
		}
		if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ENQ_POSTBAL_ALLOW))).booleanValue()) {
			strBuff.append("&ENQ_POSTBAL_ALLOW="+PretupsI.YES);
		} else {
			strBuff.append("&ENQ_POSTBAL_ALLOW="+PretupsI.NO);
		}
		return strBuff.toString();
	}
	/**
	 * Get the Receiver Request String to be send to common Client
	 * @return
	 */
	private String getReceiverCommonString() {
		StringBuffer strBuff=null;
		strBuff=new StringBuffer("MSISDN="+_receiverMSISDN);
		strBuff.append("&TRANSACTION_ID="+_transferID);
		strBuff.append("&NETWORK_CODE="+_receiverVO.getNetworkCode());
		strBuff.append("&INTERFACE_ID="+_receiverTransferItemVO.getInterfaceID());
		strBuff.append("&INTERFACE_HANDLER="+_receiverTransferItemVO.getInterfaceHandlerClass());
		strBuff.append("&INT_MOD_COMM_TYPE="+_intModCommunicationTypeR);
		strBuff.append("&INT_MOD_IP="+_intModIPR);
		strBuff.append("&INT_MOD_PORT="+_intModPortR);
		strBuff.append("&INT_MOD_CLASSNAME="+_intModClassNameR);
		strBuff.append("&MODULE="+PretupsI.P2P_MODULE);
		strBuff.append("&USER_TYPE=R");
		//added for CRE_INT_CR00029 by ankit Zindal
		strBuff.append("&CARD_GROUP_SELECTOR="+_requestVO.getReqSelector());
		strBuff.append("&REQ_SERVICE="+_serviceType);
		strBuff.append("&INT_ST_TYPE="+_p2pTransferVO.getReceiverInterfaceStatusType());
		//Added By Babu Kunwar
		strBuff.append("&SELECTOR_BUNDLE_ID="+_p2pTransferVO.getSelectorBundleId());
		return strBuff.toString();
	}
	/**
	 * Gets the receiver validate Request String
	 * @return
	 */
	public String getReceiverValidateStr() {
		StringBuffer strBuff=null;
		strBuff=new StringBuffer(getReceiverCommonString());
		strBuff.append("&INTERFACE_ACTION="+PretupsI.INTERFACE_VALIDATE_ACTION);
		strBuff.append("&SERVICE_CLASS="+_receiverTransferItemVO.getServiceClassCode());
		strBuff.append("&ACCOUNT_ID="+_receiverTransferItemVO.getReferenceID());
		strBuff.append("&ACCOUNT_STATUS="+_receiverTransferItemVO.getAccountStatus());
		strBuff.append("&CREDIT_LIMIT="+_receiverTransferItemVO.getPreviousBalance());
		strBuff.append("&SERVICE_TYPE="+_senderSubscriberType+"-"+_type);
		return strBuff.toString();
	}
	/**
	 * Gets the sender Credit Request String
	 * @return
	 */
	public String getReceiverCreditStr() {
		StringBuffer strBuff=null;
		strBuff=new StringBuffer(getReceiverCommonString());
		strBuff.append("&INTERFACE_ACTION="+PretupsI.INTERFACE_CREDIT_ACTION);
		strBuff.append("&INTERFACE_AMOUNT="+_receiverTransferItemVO.getTransferValue());
		strBuff.append("&SERVICE_CLASS="+_receiverTransferItemVO.getServiceClassCode());
		strBuff.append("&ACCOUNT_ID="+_receiverTransferItemVO.getReferenceID());
		strBuff.append("&ACCOUNT_STATUS="+_receiverTransferItemVO.getAccountStatus());
		strBuff.append("&GRACE_DAYS="+_receiverTransferItemVO.getGraceDaysStr());
		strBuff.append("&CARD_GROUP="+_p2pTransferVO.getCardGroupCode());
		strBuff.append("&MIN_CARD_GROUP_AMT="+_p2pTransferVO.getMinCardGroupAmount());
		strBuff.append("&VALIDITY_DAYS="+_receiverTransferItemVO.getValidity());
		strBuff.append("&BONUS_VALIDITY_DAYS="+_p2pTransferVO.getReceiverBonusValidity());
		strBuff.append("&BONUS_AMOUNT="+_p2pTransferVO.getReceiverBonusValue());
		strBuff.append("&SOURCE_TYPE="+_p2pTransferVO.getSourceType());
		strBuff.append("&PRODUCT_CODE="+_p2pTransferVO.getProductCode());
		strBuff.append("&TAX_AMOUNT="+(_p2pTransferVO.getReceiverTax1Value()+_p2pTransferVO.getReceiverTax2Value()));
		strBuff.append("&ACCESS_FEE="+_p2pTransferVO.getReceiverAccessFee());
		strBuff.append("&SENDER_MSISDN="+_senderMSISDN);
		strBuff.append("&RECEIVER_MSISDN="+_receiverMSISDN);
		strBuff.append("&EXTERNAL_ID="+_receiverExternalID);
		strBuff.append("&GATEWAY_CODE="+_requestVO.getRequestGatewayCode());
		strBuff.append("&GATEWAY_TYPE="+_requestVO.getRequestGatewayType());
		strBuff.append("&IMSI="+BTSLUtil.NullToString(_receiverIMSI));
		strBuff.append("&SENDER_ID="+((SenderVO)_requestVO.getSenderVO()).getUserID());
		strBuff.append("&SERVICE_TYPE="+_senderSubscriberType+"-"+_type);
		if(String.valueOf(PretupsI.CHNL_SELECTOR_C_VALUE).equals(_requestVO.getReqSelector())) {
			strBuff.append("&ADJUST=Y");
			strBuff.append("&CAL_OLD_EXPIRY_DATE="+_receiverTransferItemVO.getOldExporyInMillis());///@nu
		}
		try {
			strBuff.append("&OLD_EXPIRY_DATE="+BTSLUtil.getDateStringFromDate(_receiverTransferItemVO.getPreviousExpiry()));
		} catch(Exception e) {
				_log.errorTrace("getReceiverCreditStr", e);
		}
		try {
			strBuff.append("&OLD_GRACE_DATE="+BTSLUtil.getDateStringFromDate(_receiverTransferItemVO.getPreviousGraceDate()));
		} catch(Exception e) {
			_log.errorTrace("getReceiverCreditStr", e);
		}
		strBuff.append("&INTERFACE_PREV_BALANCE="+_receiverTransferItemVO.getPreviousBalance());
		// Avinash send the requested amount to IN. to use card group only for reporting purpose.
		strBuff.append("&REQUESTED_AMOUNT="+_p2pTransferVO.getRequestedAmount());
		//For Get NUMBER BACK Service
		if(_receiverTransferItemVO.isNumberBackAllowed()) {
			String numbck_diff_to_in=_p2pTransferVO.getServiceType()+PreferenceI.NUMBCK_DIFF_REQ_TO_IN;
			Boolean NBR_BK_SEP_REQ=(Boolean)PreferenceCache.getControlPreference(numbck_diff_to_in,_p2pTransferVO.getNetworkCode(),_receiverTransferItemVO.getInterfaceID());
			strBuff.append("&NBR_BK_DIFF_REQ="+NBR_BK_SEP_REQ);
		}
		//Added by Zafar Abbas on 13/02/2008 after adding two new fields for Bonus SMS/MMS in Card group
		strBuff.append("&BONUS1="+_p2pTransferVO.getReceiverBonus1());
		strBuff.append("&BONUS2="+_p2pTransferVO.getReceiverBonus2());
		strBuff.append("&BUNDLE_TYPES="+_receiverTransferItemVO.getBundleTypes());
		strBuff.append("&BONUS_BUNDLE_VALIDITIES="+_receiverTransferItemVO.getBonusBundleValidities());
		//added by vikask for card group updation field		
		strBuff.append("&BONUS1_VAL="+_p2pTransferVO.getReceiverBonus1Validity());
		strBuff.append("&BONUS2_VAL="+_p2pTransferVO.getReceiverBonus2Validity());
		strBuff.append("&CREDIT_BONUS_VAL="+_p2pTransferVO.getReceiverCreditBonusValidity());		
		//added by amit for card group offline field
		strBuff.append("&COMBINED_RECHARGE="+_p2pTransferVO.getBoth());
		strBuff.append("&EXPLICIT_RECHARGE="+_p2pTransferVO.getOnline());
		if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ENQ_POSTBAL_IN))).booleanValue()) {//@nu
			strBuff.append("&ENQ_POSTBAL_IN="+PretupsI.YES);
		} else {
			strBuff.append("&ENQ_POSTBAL_IN="+PretupsI.NO);
		}
		if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ENQ_POSTBAL_ALLOW))).booleanValue()) {
			strBuff.append("&ENQ_POSTBAL_ALLOW="+PretupsI.YES);
		} else {
			strBuff.append("&ENQ_POSTBAL_ALLOW="+PretupsI.NO);
		}
		return strBuff.toString();
	}
	/**
	 * Method to handle sender validation request
	 * This method will perform the Alternate interface routing is mobile is not found on the interface 
	 * If not found on any interface then raise error
	 * @param str
	 * @throws BTSLBaseException
	 */
	public void updateForSenderValidateResponse(String str) throws BTSLBaseException {
		HashMap map=BTSLUtil.getStringToHash(str,"&","=");
		String status=(String)map.get("TRANSACTION_STATUS");
		ArrayList altList=null;
		boolean isRequired=false;
//		added to log the IN validation request sent and request received time. Start 12/02/2008  
		if (null != map.get("IN_START_TIME"))
			_requestVO.setValidationSenderRequestSent(((Long.valueOf((String)map.get("IN_START_TIME"))).longValue()));
		if (null != map.get("IN_END_TIME"))		
			_requestVO.setValidationSenderResponseReceived(((Long.valueOf((String)map.get("IN_END_TIME"))).longValue()));
//		end 12/02/2008
		//Start: Update the Interface table for the interface ID based on Handler status and update the Cache
		String interfaceStatusType=(String)map.get("INT_SET_STATUS");
		if(!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME.equals(interfaceStatusType))) {
			new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES,_senderTransferItemVO.getInterfaceID(),interfaceStatusType,PretupsErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG,PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
		}
		//:End
		boolean isLDCCTest=true;
		boolean ldccHandle=_operatorUtil.handleLDCCRequest();
		//If we get the MSISDN not found on interface error then perform interface routing
		if(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status)) {
			if(!ldccHandle) {
				isRoutingSecond=true;
			}
			_senderVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			altList=InterfaceRoutingControlCache.getRoutingControlDetails(_senderTransferItemVO.getInterfaceID());
			try {
				if(map.get("SERVICE_CLASS").equals(PretupsI.SERVICE_CLASS_LDCC)) {
					throw new BTSLBaseException(this,"updateForSenderValidateResponse",InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
				} else {
					if(altList!=null && !altList.isEmpty()) {
						if(_log.isDebugEnabled()) {
							_log.debug("updateForSenderValidateResponse","Got Status="+status +" After validation Request For MSISDN="+_senderMSISDN+" Performing Alternate Routing");  
						}
						performSenderAlternateRouting(altList); //Method to perform the sender interface routing for validation
					} else {
						isRequired=true;
						throw new BTSLBaseException(this,"updateForSenderValidateResponse",InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
					}
				}
			} catch (BTSLBaseException e) {
				// TODO Auto-generated catch block
				if(!ldccHandle) {
					throw e;
				}
				else {
					status=e.getMessage();
					if(ldccHandle && InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) ) {
						try {
							handleLDCCRequest();
						} catch(BTSLBaseException be) {
							_log.errorTrace("updateForSenderValidateResponse", e); status=be.getMessage();
						}
						if(!BTSLUtil.isNullString(_senderTransferItemVO.getValidationStatus())) {
							status=_senderTransferItemVO.getValidationStatus();
						}
						isRequired=false;
						isLDCCTest=false;
						isRoutingSecond=true;
						if(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status)) {
							_senderVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
							altList=InterfaceRoutingControlCache.getRoutingControlDetails(_senderTransferItemVO.getInterfaceID());
							//routing of Ailternate type IN .
							if(altList!=null && !altList.isEmpty()) {
								if(_log.isDebugEnabled()) {
									_log.debug("updateForSenderValidateResponse","Got Status="+status +" After validation Request For MSISDN="+_senderMSISDN+" Performing Alternate Routing");  
								}
								performSenderAlternateRouting(altList); //Method to perform the sender interface routing for validation
								if(InterfaceErrorCodesI.SUCCESS.equals(_senderTransferItemVO.getValidationStatus())) {
									_senderVO.setSubscriberType(_serviceInterfaceRoutingVO.getAlternateInterfaceType());
									_p2pTransferVO.setTransferCategory(_serviceInterfaceRoutingVO.getAlternateInterfaceType()+"-"+_type);
									_p2pTransferVO.setPaymentMethodType(_serviceInterfaceRoutingVO.getAlternateInterfaceType());
									_isUpdateRequired=true;
								}
							} else {
								isLDCCTest=true;
								isRequired=true;
							}
						}
					}
					if(map.get("SERVICE_CLASS").equals(PretupsI.SERVICE_CLASS_LDCC) ) {
						//if service class is ldcc then subscriber type will not be updated . it will remain as PRE type.
						_isUpdateRequired=false;
						_isSenderRoutingUpdate=false;
						if(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(_senderTransferItemVO.getValidationStatus())) {
							//if not found on other IN but subscriber is basically INACTIVE or balance is not enough.
							_senderTransferItemVO.setValidationStatus(InterfaceErrorCodesI.SUCCESS);
							status=InterfaceErrorCodesI.SUCCESS;
							isRequired=true;
						}
					}
				}
			}
		}
		if((!InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) && isLDCCTest)|| isRequired) {
			_senderTransferItemVO.setProtocolStatus((String)map.get("PROTOCOL_STATUS"));
			_senderTransferItemVO.setAccountStatus((String)map.get("ACCOUNT_STATUS"));
			_senderTransferItemVO.setInterfaceResponseCode((String)map.get("INTERFACE_STATUS"));
			_senderTransferItemVO.setValidationStatus(status);
			_senderVO.setInterfaceResponseCode(_senderTransferItemVO.getInterfaceResponseCode());
			if(!BTSLUtil.isNullString((String)map.get("IN_TXN_ID"))) {
				try {
					_senderTransferItemVO.setInterfaceReferenceID((String)map.get("IN_TXN_ID"));
				} catch(Exception e) {
					_log.errorTrace("updateForSenderValidateResponse", e);
				}
			}
			_senderTransferItemVO.setReferenceID((String)map.get("IN_RECON_ID"));
			String [] strArr=null;
			if(BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS)) {
				_p2pTransferVO.setErrorCode(status+"_S");
				_senderVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
				_receiverVO.setTransactionStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
				_senderTransferItemVO.setTransferStatus(status);
				_receiverTransferItemVO.setValidationStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
				_receiverTransferItemVO.setTransferStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
				strArr=new String[]{_receiverMSISDN,PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()),_transferID};
				//throw new BTSLBaseException("PretupsBL",METHOD_NAME,PretupsErrorCodesI.ICP2P_SENDER_FAIL,0,strArr,null);
				throw new BTSLBaseException("IATPrepaidController","updateForSenderValidateResponse",_p2pTransferVO.getErrorCode(),0,strArr,null);
			}
			_senderTransferItemVO.setTransferStatus(status);
			_senderVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
			SubscriberRoutingControlVO subscriberRoutingControlVO=SubscriberRoutingControlCache.getRoutingControlDetails(_p2pTransferVO.getNetworkCode()+"_"+_p2pTransferVO.getServiceType()+"_"+_p2pTransferVO.getPaymentMethodType());
			if((PretupsI.INTERFACE_CATEGORY_PRE.equals(_senderVO.getSubscriberType()) || ldccHandle ) && !_senderDeletionReqFromSubRouting && subscriberRoutingControlVO!=null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
				PretupsBL.insertSubscriberInterfaceRouting(_senderTransferItemVO.getInterfaceID(),_senderExternalID,_senderMSISDN,_p2pTransferVO.getPaymentMethodType(),_senderVO.getUserID(),_currentDate);
				_senderInterfaceInfoInDBFound=true;
				_senderDeletionReqFromSubRouting=true;
			}
			try {
				_senderTransferItemVO.setPreviousExpiry(BTSLUtil.getDateFromDateString((String)map.get("OLD_EXPIRY_DATE"),"ddMMyyyy"));
			} catch(Exception e) {
				_log.errorTrace("updateForSenderValidateResponse", e);
			}
			try {
				_senderTransferItemVO.setPreviousGraceDate(BTSLUtil.getDateFromDateString((String)map.get("OLD_GRACE_DATE"),"ddMMyyyy"));
			} catch(Exception e) {
				_log.errorTrace("updateForSenderValidateResponse", e);
			}
			_senderTransferItemVO.setOldExporyInMillis((String)map.get("CAL_OLD_EXPIRY_DATE"));//@nu
			_senderTransferItemVO.setServiceClassCode((String)map.get("SERVICE_CLASS"));
			try {
				_senderTransferItemVO.setPreviousBalance(Long.parseLong((String)map.get("INTERFACE_PREV_BALANCE")));
			} catch(Exception e) {
				_log.errorTrace("updateForSenderValidateResponse", e); _senderTransferItemVO.setBalanceCheckReq(false);
			}
			_senderVO.setCreditLimit(_senderTransferItemVO.getPreviousBalance());
			//Update the Previous Balance in case of Post Paid Offline interface with Credit Limit - Monthly Transfer Amount
			if(_senderVO.isPostOfflineInterface()) {
				boolean isPeriodChange=BTSLUtil.isPeriodChangeBetweenDates(_senderVO.getLastSuccessTransferDate(),_currentDate,BTSLUtil.PERIOD_MONTH);
				if(!isPeriodChange) {		
					_senderTransferItemVO.setPreviousBalance(_senderTransferItemVO.getPreviousBalance()-_senderVO.getMonthlyTransferAmount());
				}
			}
			if(PretupsI.INTERFACE_CATEGORY_POST.equals(_senderVO.getSubscriberType())) {
				long balance =Long.parseLong((String)map.get("BILL_AMOUNT_BAL"));
				long credit_limit =Long.parseLong((String)map.get("INTERFACE_PREV_BALANCE"));
				_senderVO.setCreditLimit(credit_limit-balance);	
				_senderTransferItemVO.setPreviousBalance(_senderTransferItemVO.getPreviousBalance()-_senderVO.getMonthlyTransferAmount());
			}
			_senderTransferItemVO.setFirstCall((String)map.get("FIRST_CALL"));
			_senderTransferItemVO.setGraceDaysStr((String)map.get("GRACE_DAYS"));
		}
	}

	/**
	 * Method to handle Sender Debit Response
	 * @param str
	 * @throws BTSLBaseException
	 */
	public void updateForSenderDebitResponse(String str) throws BTSLBaseException {
		HashMap map=BTSLUtil.getStringToHash(str,"&","=");
		String status=(String)map.get("TRANSACTION_STATUS");
//		added to log the IN validation request sent and request received time. Start 12/02/2008  
		if (null != map.get("IN_START_TIME"))
			_requestVO.setTopUPSenderRequestSent(((Long.valueOf((String)map.get("IN_START_TIME"))).longValue()));
		if (null != map.get("IN_END_TIME"))		
			_requestVO.setTopUPSenderResponseReceived(((Long.valueOf((String)map.get("IN_END_TIME"))).longValue()));
//		end 12/02/2008
		//Start: Update the Interface table for the interface ID based on Handler status and update the Cache
		String interfaceStatusType=(String)map.get("INT_SET_STATUS");
		if(!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME.equals(interfaceStatusType))) {
			new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES,_receiverTransferItemVO.getInterfaceID(),interfaceStatusType,PretupsErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG,PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
		}
		//:End
		_senderTransferItemVO.setProtocolStatus((String)map.get("PROTOCOL_STATUS"));
		_senderTransferItemVO.setInterfaceResponseCode((String)map.get("INTERFACE_STATUS"));
		_senderTransferItemVO.setUpdateStatus(status);
		_senderVO.setInterfaceResponseCode(_senderTransferItemVO.getInterfaceResponseCode());
		_senderPostBalanceAvailable=((String)map.get("POST_BALANCE_ENQ_SUCCESS"));
		if(!BTSLUtil.isNullString((String)map.get("IN_TXN_ID"))) {
			try {
				_senderTransferItemVO.setInterfaceReferenceID((String)map.get("IN_TXN_ID"));
			} catch(Exception e) {
				_log.errorTrace("updateForSenderDebitResponse", e);
			}
		}
		_senderTransferItemVO.setReferenceID((String)map.get("IN_RECON_ID"));	
		String [] strArr=null;		
		if(BTSLUtil.isNullString(status) || (!status.equals(InterfaceErrorCodesI.SUCCESS) && !status.equals(InterfaceErrorCodesI.AMBIGOUS))) {
			_p2pTransferVO.setErrorCode(status+"_S");
			_p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			_senderVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			_senderTransferItemVO.setTransferStatus(status);
			_receiverTransferItemVO.setUpdateStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
			_receiverTransferItemVO.setTransferStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
			strArr=new String[]{_receiverMSISDN,PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()),_transferID};
			//throw new BTSLBaseException(this,METHOD_NAME,PretupsErrorCodesI.ICP2P_SENDER_FAIL,0,strArr,null);
			throw new BTSLBaseException(this,"updateForSenderDebitResponse",_p2pTransferVO.getErrorCode(),0,strArr,null);
		} else if (status.equals(InterfaceErrorCodesI.AMBIGOUS)) {
			_p2pTransferVO.setErrorCode(status+"_S");
			_p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
			_senderTransferItemVO.setTransferStatus(status);
			_senderVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
			_senderTransferItemVO.setUpdateStatus(status);
			_receiverTransferItemVO.setUpdateStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
			_receiverTransferItemVO.setTransferStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
			strArr=new String[]{_transferID,_receiverTransferItemVO.getMsisdn(),PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount())};
			throw new BTSLBaseException(this,"updateForSenderDebitResponse",PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS,0,strArr,null);
		} else {
			_senderTransferItemVO.setTransferStatus(status);
			_senderVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
			_senderTransferItemVO.setUpdateStatus(status);
		}
		if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ENQ_POSTBAL_ALLOW))).booleanValue()) {
			try {
				_senderTransferItemVO.setNewExpiry(BTSLUtil.getDateFromDateString((String)map.get("NEW_EXPIRY_DATE"),"ddMMyyyy"));
			} catch(Exception e) {
				_log.errorTrace("updateForSenderDebitResponse", e);
			}			
			try {
				_senderTransferItemVO.setPostBalance(Long.parseLong((String)map.get("INTERFACE_POST_BALANCE")));
			} catch(Exception e) {
				_log.errorTrace("updateForSenderDebitResponse", e);
			}
			try {
				_senderTransferItemVO.setPostValidationStatus((String)map.get("POSTCRE_TRANSACTION_STATUS"));
			} catch(Exception e) {
				_log.errorTrace("updateForSenderDebitResponse", e);
			}			
		}
		try {
			if(!BTSLUtil.isNullString((String)map.get("IN_POSTCREDIT_VAL_TIME"))) {
				_requestVO.setPostValidationTimeTaken(Long.parseLong((String)map.get("IN_POSTCREDIT_VAL_TIME")));
			} else {
				_requestVO.setPostValidationTimeTaken(0L);
			}
		} catch(Exception e) {
			_log.errorTrace("updateForSenderDebitResponse", e);
		}	
	}
	/**
	 * Method to handle receiver validation response
	 * This method will perform the Alternate interface routing is mobile is not found on the interface 
	 * If not found on any interface then perform the alternate category routing if that is not done
	 * Earlier.
	 * @param str
	 * @throws BTSLBaseException
	 */
	public void updateForReceiverValidateResponse(String str) throws BTSLBaseException {
		HashMap map=BTSLUtil.getStringToHash(str,"&","=");
		String status=(String)map.get("TRANSACTION_STATUS");
		ArrayList altList=null;
		boolean isRequired=false;
//		added to log the IN validation request sent and request received time. Start 12/02/2008  
		if (null != map.get("IN_START_TIME"))
			_requestVO.setValidationReceiverRequestSent(((Long.valueOf((String)map.get("IN_START_TIME"))).longValue()));
		if (null != map.get("IN_END_TIME"))		
			_requestVO.setValidationReceiverResponseReceived(((Long.valueOf((String)map.get("IN_END_TIME"))).longValue()));
//		end 12/02/2008
		//Start: Update the Interface table for the interface ID based on Handler status and update the Cache
		String interfaceStatusType=(String)map.get("INT_SET_STATUS");
		if(!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME.equals(interfaceStatusType))) {
			new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES,_receiverTransferItemVO.getInterfaceID(),interfaceStatusType,PretupsErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG,PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
		}
		//:End
		//If we get the MSISDN not found on interface error then perform interface routing
		if(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status)) {
			_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			altList=InterfaceRoutingControlCache.getRoutingControlDetails(_receiverTransferItemVO.getInterfaceID());
			if(altList!=null && !altList.isEmpty()) {
				performReceiverAlternateRouting(altList,SRC_BEFORE_INRESP_CAT_ROUTING);
			} else {
				if(_useAlternateCategory && !_performIntfceCatRoutingBeforeVal && !_interfaceCatRoutingDone) {
					performAlternateCategoryRouting();
				} else {
					isRequired=true;
				}
			}
		}
		if(!InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) || isRequired) {
			populateReceiverItemsDetails(map);
		}
	}

	/**
	 * Method to handle receiver credit response  
	 * @param str
	 * @throws BTSLBaseException
	 */
	public void updateForReceiverCreditResponse(String str) throws BTSLBaseException {
		final String METHOD_NAME="updateForReceiverCreditResponse";
		HashMap map=BTSLUtil.getStringToHash(str,"&","=");
		String status=(String)map.get("TRANSACTION_STATUS");
//		added to log the IN validation request sent and request received time. Start 12/02/2008  
		if (null != map.get("IN_START_TIME"))
			_requestVO.setTopUPReceiverRequestSent(((Long.valueOf((String)map.get("IN_START_TIME"))).longValue()));
		if (null != map.get("IN_END_TIME"))		
			_requestVO.setTopUPReceiverResponseReceived(((Long.valueOf((String)map.get("IN_END_TIME"))).longValue()));
//		end 12/02/2008
		//Start: Update the Interface table for the interface ID based on Handler status and update the Cache
		String interfaceStatusType=(String)map.get("INT_SET_STATUS");
		//Done so that in Credit Back IN module does not activate the IN as else it would receive M from here
		if(InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) && _receiverTransferItemVO.getInterfaceID().equals(_senderTransferItemVO.getInterfaceID())) {
			_p2pTransferVO.setSenderInterfaceStatusType(InterfaceCloserI.INTERFACE_AUTO_ACTIVE);
		}
		if(!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME.equals(interfaceStatusType))) {
			new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES,_receiverTransferItemVO.getInterfaceID(),interfaceStatusType,PretupsErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG,PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
		}
		//:End			
		_receiverTransferItemVO.setProtocolStatus((String)map.get("PROTOCOL_STATUS"));
		_receiverTransferItemVO.setInterfaceResponseCode((String)map.get("INTERFACE_STATUS"));
		String updateStatus=(String)map.get("UPDATE_STATUS");
		if(BTSLUtil.isNullString(updateStatus)) {
			updateStatus=status;
		}
		_receiverTransferItemVO.setUpdateStatus(status);
		_receiverVO.setInterfaceResponseCode(_receiverTransferItemVO.getInterfaceResponseCode());		
		_receiverTransferItemVO.setUpdateStatus1((String)map.get("UPDATE_STATUS1"));
		_receiverTransferItemVO.setUpdateStatus2((String)map.get("UPDATE_STATUS2"));
		if(!BTSLUtil.isNullString((String)map.get("ADJUST_AMOUNT"))) {
			_receiverTransferItemVO.setAdjustValue(Long.parseLong((String)map.get("ADJUST_AMOUNT")));
		}
		_receiverPostBalanceAvailable=((String)map.get("POST_BALANCE_ENQ_SUCCESS"));
		//set from IN Module
		if(!BTSLUtil.isNullString((String)map.get("IN_TXN_ID"))) {
			try {
				_receiverTransferItemVO.setInterfaceReferenceID((String)map.get("IN_TXN_ID"));
			} catch(Exception e) {
				_log.errorTrace(METHOD_NAME, e);
			}
		}
		if(!BTSLUtil.isNullString((String)map.get("IN_TXN_ID1"))) {
			try {
				_receiverTransferItemVO.setInterfaceReferenceID1((String)map.get("IN_TXN_ID1"));
			} catch(Exception e) {
				_log.errorTrace(METHOD_NAME, e);
			}
			_receiverTransferItemVO.setTransferType1(PretupsI.TRANSFER_TYPE_BA_ADJ_CR);
		}
		if(!BTSLUtil.isNullString((String)map.get("IN_TXN_ID2"))) {
			try {
				_receiverTransferItemVO.setInterfaceReferenceID2((String)map.get("IN_TXN_ID2"));
			} catch(Exception e) {
				_log.errorTrace(METHOD_NAME, e);
			}
			_receiverTransferItemVO.setTransferType2(PretupsI.TRANSFER_TYPE_BA_ADJ_DR);
		}
		_receiverTransferItemVO.setReferenceID((String)map.get("IN_RECON_ID"));
		String [] strArr=null;
		if(BTSLUtil.isNullString(status) || (!status.equals(InterfaceErrorCodesI.SUCCESS) && !status.equals(InterfaceErrorCodesI.AMBIGOUS))) {
			_p2pTransferVO.setErrorCode(status+"_R");
			_p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			_receiverTransferItemVO.setTransferStatus(status);
			strArr=new String[]{_receiverMSISDN,PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()),_transferID};
			//throw new BTSLBaseException(this,METHOD_NAME,PretupsErrorCodesI.ICP2P_SENDER_FAIL,0,strArr,null);
			throw new BTSLBaseException(this,METHOD_NAME,_p2pTransferVO.getErrorCode(),0,strArr,null);
		} else if (status.equals(InterfaceErrorCodesI.AMBIGOUS)) {
			_p2pTransferVO.setErrorCode(status+"_R");
			_p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
			_receiverTransferItemVO.setTransferStatus(status);
			_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
			_receiverTransferItemVO.setUpdateStatus(status);
			strArr=new String[]{_transferID,_receiverTransferItemVO.getMsisdn(),PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount())};
			throw new BTSLBaseException(this,METHOD_NAME,PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS,0,strArr,null);
		}	
		else {
			_receiverTransferItemVO.setTransferStatus(status);
			_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
			_receiverTransferItemVO.setUpdateStatus(status);
		}
		if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ENQ_POSTBAL_ALLOW))).booleanValue()) {
			try {
				_receiverTransferItemVO.setNewExpiry(BTSLUtil.getDateFromDateString((String)map.get("NEW_EXPIRY_DATE"),"ddMMyyyy"));
			} catch(Exception e) {
				_log.errorTrace(METHOD_NAME, e);
			}
			try {
				_receiverTransferItemVO.setPostBalance(Long.parseLong((String)map.get("INTERFACE_POST_BALANCE")));
			} catch(Exception e) {
				_log.errorTrace(METHOD_NAME, e);
			}
			try {
				_receiverTransferItemVO.setPostValidationStatus((String)map.get("POSTCRE_TRANSACTION_STATUS"));
			} catch(Exception e) {
				_log.errorTrace(METHOD_NAME, e);
			}
		}
		try {
			if(!BTSLUtil.isNullString((String)map.get("IN_POSTCREDIT_VAL_TIME"))) {
				_requestVO.setPostValidationTimeTaken(Long.parseLong((String)map.get("IN_POSTCREDIT_VAL_TIME")));
			} else {
				_requestVO.setPostValidationTimeTaken(0L);
			}
		} catch(Exception e) {
			_log.errorTrace(METHOD_NAME, e);
		}
	}

	/**
	 * Method to process the response of the receiver top up from IN
	 * @param str
	 * @throws BTSLBaseException
	 */
	public void updateForReceiverCreditResponse(IATInterfaceVO p_iatInterfaceVO) throws BTSLBaseException {
		final String METHOD_NAME="updateForReceiverCreditResponse";
		String status=p_iatInterfaceVO.getIatINTransactionStatus();
		if (null != p_iatInterfaceVO.getIatStartTime())
			_requestVO.setTopUPReceiverRequestSent(((Long.valueOf(p_iatInterfaceVO.getIatStartTime()).longValue())));
		if (null != p_iatInterfaceVO.getIatEndTime())		
			_requestVO.setTopUPReceiverResponseReceived(((Long.valueOf(p_iatInterfaceVO.getIatEndTime()).longValue())));
		_receiverTransferItemVO.setProtocolStatus(p_iatInterfaceVO.getIatProtocolStatus());
		_receiverTransferItemVO.setInterfaceResponseCode(p_iatInterfaceVO.getIatResponseCodeChkStatus());
		_iatTransferItemVO.setIatTimestamp(p_iatInterfaceVO.getIatTimeStamp());
		_iatTransferItemVO.setIatTxnId(p_iatInterfaceVO.getIatTRXID());
		_iatTransferItemVO.setIatExchangeRate(p_iatInterfaceVO.getIatExchangeRate());
		_iatTransferItemVO.setIatProvRatio(p_iatInterfaceVO.getIatProvRatio());
		_iatTransferItemVO.setIatReceiverSystemBonus(p_iatInterfaceVO.getIatReceiverZebraBonus());		
		_iatTransferItemVO.setIatFees(p_iatInterfaceVO.getIatFees());
		_iatTransferItemVO.setIatCreditMessage(p_iatInterfaceVO.getIatResponseMsgCredit());
		_iatTransferItemVO.setIatCreditRespCode(p_iatInterfaceVO.getIatResponseCodeCredit());
		_iatTransferItemVO.setIatCheckStatusRespCode(p_iatInterfaceVO.getIatResponseCodeChkStatus());		
		_iatTransferItemVO.setIatSentAmtByIAT(p_iatInterfaceVO.getIatSentAmtByIAT());
		_iatTransferItemVO.setIatRcvrRcvdAmt(p_iatInterfaceVO.getIatRcvrRcvdAmount());
		_iatTransferItemVO.setIatReceivedAmount(p_iatInterfaceVO.getIatReceivedAmount());		
		if(!PretupsErrorCodesI.TXN_STATUS_SUCCESS.equals(status)) {
			_iatTransferItemVO.setIatErrorCode(p_iatInterfaceVO.getIatReasonCode());
			_iatTransferItemVO.setIatErrorMessage(p_iatInterfaceVO.getIatReasonMessage());		    
			_iatTransferItemVO.setIatFailedAt(p_iatInterfaceVO.getIatFailedAt());
			_iatTransferItemVO.setIatRcvrNWErrorCode(p_iatInterfaceVO.getReceiverNWReasonCode());
			_iatTransferItemVO.setIatRcvrNWErrorMessage(p_iatInterfaceVO.getReceiverNWReasonMessage());
		}	    
		String updateStatus=p_iatInterfaceVO.getIatUpdateStatus();	
		if(BTSLUtil.isNullString(updateStatus)) {
			updateStatus=status;
		}
		_receiverTransferItemVO.setUpdateStatus(updateStatus);
		//set from IN Module
		if(!BTSLUtil.isNullString(p_iatInterfaceVO.getIatTRXID())) {
			try {
				_receiverTransferItemVO.setInterfaceReferenceID(p_iatInterfaceVO.getIatTRXID());
			} catch(Exception e) {
				_log.errorTrace(METHOD_NAME, e);
			}
		}
		_receiverTransferItemVO.setReferenceID(p_iatInterfaceVO.getReconId());
		//If status is other than Success in validation stage mark sender request as Not applicable and
		//Make transaction Fail
		String [] strArr=null;
		if(BTSLUtil.isNullString(status) ||(!status.equals(InterfaceErrorCodesI.SUCCESS) && !status.equals(InterfaceErrorCodesI.AMBIGOUS))) {
			_p2pTransferVO.setErrorCode(status+"_R");
			_p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			_iatTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			_receiverTransferItemVO.setTransferStatus(status);
			strArr=new String[]{_transferID,PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount())};
			throw new BTSLBaseException(this,METHOD_NAME,_p2pTransferVO.getErrorCode(),0,strArr,null);
		} else if (status.equals(InterfaceErrorCodesI.AMBIGOUS)) {
			_p2pTransferVO.setErrorCode(status+"_R");
			_p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
			_iatTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
			_iatTransferItemVO.setTransferValue(_p2pTransferVO.getReceiverTransferValue());
			_receiverTransferItemVO.setTransferStatus(status);
			_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
			_receiverTransferItemVO.setUpdateStatus(status);
			strArr=new String[]{_transferID,_receiverTransferItemVO.getMsisdn(),PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount())};
			throw new BTSLBaseException(this,METHOD_NAME,PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS,0,strArr,null);
		} else {
			_receiverTransferItemVO.setTransferStatus(status);
			_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
			_receiverTransferItemVO.setUpdateStatus(status);
			_iatTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
			_iatTransferItemVO.setTransferValue(_p2pTransferVO.getReceiverTransferValue());
		}
	}

	/**
	 * Method to handle sender credit back response
	 * @param str
	 * @throws BTSLBaseException
	 */
	public void updateForSenderCreditBackResponse(String str) throws BTSLBaseException {
		final String METHOD_NAME="updateForSenderCreditBackResponse";
		HashMap map=BTSLUtil.getStringToHash(str,"&","=");
		_senderCreditBackStatusVO=new TransferItemVO();
		_senderCreditBackStatusVO.setMsisdn(_senderTransferItemVO.getMsisdn());
		_senderCreditBackStatusVO.setRequestValue(_senderTransferItemVO.getRequestValue());
		_senderCreditBackStatusVO.setSubscriberType(_senderTransferItemVO.getSubscriberType());
		_senderCreditBackStatusVO.setTransferDate(_senderTransferItemVO.getTransferDate());
		_senderCreditBackStatusVO.setTransferDateTime(_senderTransferItemVO.getTransferDateTime());
		_senderCreditBackStatusVO.setTransferID(_senderTransferItemVO.getTransferID());
		_senderCreditBackStatusVO.setUserType(_senderTransferItemVO.getUserType());
		_senderCreditBackStatusVO.setEntryDate(_senderTransferItemVO.getEntryDate());
		_senderCreditBackStatusVO.setEntryDateTime(_senderTransferItemVO.getEntryDateTime());
		_senderCreditBackStatusVO.setPrefixID(_senderTransferItemVO.getPrefixID());
		_senderCreditBackStatusVO.setTransferValue(_senderTransferItemVO.getTransferValue());
		_senderCreditBackStatusVO.setInterfaceID(_senderTransferItemVO.getInterfaceID());
		_senderCreditBackStatusVO.setInterfaceType(_senderTransferItemVO.getInterfaceType());
		_senderCreditBackStatusVO.setServiceClass(_senderTransferItemVO.getServiceClass());
		_senderCreditBackStatusVO.setServiceClassCode(_senderTransferItemVO.getServiceClassCode());
		_senderCreditBackStatusVO.setInterfaceHandlerClass(_senderTransferItemVO.getInterfaceHandlerClass());
		_senderCreditBackStatusVO.setSNo(3);
		_senderCreditBackStatusVO.setEntryType(PretupsI.CREDIT);
		_senderCreditBackStatusVO.setTransferType(PretupsI.TRANSFER_TYPE_P2P_CREDITBACK);
		_senderCreditPostBalanceAvailable=(String)map.get("POST_BALANCE_ENQ_SUCCESS");		
		String status=(String)map.get("TRANSACTION_STATUS");
		_senderCreditBackStatusVO.setProtocolStatus((String)map.get("PROTOCOL_STATUS"));
		_senderCreditBackStatusVO.setInterfaceResponseCode((String)map.get("INTERFACE_STATUS"));
		_senderCreditBackStatusVO.setTransferStatus(status);
		_senderCreditBackStatusVO.setUpdateStatus(status);
		_senderCreditBackStatusVO.setValidationStatus(status);
		_p2pTransferVO.setCreditBackStatus(status);		
		if(!BTSLUtil.isNullString((String)map.get("IN_TXN_ID"))) {
			_senderCreditBackStatusVO.setInterfaceReferenceID((String)map.get("IN_TXN_ID"));
		}
		_senderCreditBackStatusVO.setReferenceID((String)map.get("IN_RECON_ID"));
		if(BTSLUtil.isNullString(status) || !status.equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
			//Mark the request as Ambigous if not able to credit back the sender
			_p2pTransferVO.setErrorCode(status+"_S");
			_p2pTransferVO.setCreditBackStatus(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
			_senderCreditBackStatusVO.setTransferStatus(InterfaceErrorCodesI.AMBIGOUS);
			_p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
			throw new BTSLBaseException(status);
		}
		if(!PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS.equals(_p2pTransferVO.getTransferStatus())) {
			_p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
		}
		try {
			_senderCreditBackStatusVO.setPreviousBalance(Long.parseLong((String)map.get("INTERFACE_PREV_BALANCE")));
		} catch(Exception e) {
			_log.errorTrace(METHOD_NAME, e);
		}
		try {
			_senderCreditBackStatusVO.setPreviousExpiry(BTSLUtil.getDateFromDateString((String)map.get("OLD_EXPIRY_DATE"),"ddMMyyyy"));
		} catch(Exception e) {
			_log.errorTrace(METHOD_NAME, e);
		}
		if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ENQ_POSTBAL_ALLOW))).booleanValue()) {
			try {
				_senderCreditBackStatusVO.setPostBalance(Long.parseLong((String)map.get("INTERFACE_POST_BALANCE")));
			} catch(Exception e) {
				_log.errorTrace(METHOD_NAME, e);
			}
			try {
				_senderCreditBackStatusVO.setNewExpiry(BTSLUtil.getDateFromDateString((String)map.get("NEW_EXPIRY_DATE"),"ddMMyyyy"));
			} catch(Exception e) {
				_log.errorTrace(METHOD_NAME, e);
			}
			try {
				_senderCreditBackStatusVO.setPostValidationStatus((String)map.get("POSTCRE_TRANSACTION_STATUS"));
			} catch(Exception e) {
				_log.errorTrace(METHOD_NAME, e);
			}
		}
		try {
			if(!BTSLUtil.isNullString((String)map.get("IN_POSTCREDIT_VAL_TIME"))) {
				_requestVO.setPostValidationTimeTaken(Long.parseLong((String)map.get("IN_POSTCREDIT_VAL_TIME")));
			} else {
				_requestVO.setPostValidationTimeTaken(0L);
			}
		} catch(Exception e) {
			_log.errorTrace(METHOD_NAME, e);
		}
	}

	/**
	 * Method to check the various level of loads whether request can be passed or not
	 * @throws BTSLBaseException
	 */
	private void checkTransactionLoad() throws BTSLBaseException {
		final String METHOD_NAME="checkTransactionLoad";
		if(_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME,"Checking load for transfer ID="+_transferID);  
		}
		try {
			_requestVO.setPerformIntfceCatRoutingBeforeVal(_performIntfceCatRoutingBeforeVal);
			_p2pTransferVO.setRequestVO(_requestVO);
			_p2pTransferVO.setSenderTransferItemVO(_senderTransferItemVO);
			_p2pTransferVO.setReceiverTransferItemVO(_receiverTransferItemVO);
			_requestVO.setReceiverDeletionReqFromSubRouting(_receiverDeletionReqFromSubRouting);
			_requestVO.setReceiverInterfaceInfoInDBFound(_receiverInterfaceInfoInDBFound);
			_requestVO.setSenderDeletionReqFromSubRouting(_senderDeletionReqFromSubRouting);
			_requestVO.setSenderInterfaceInfoInDBFound(_senderInterfaceInfoInDBFound);
			_requestVO.setInterfaceCatRoutingDone(_interfaceCatRoutingDone);			
			int senderLoadStatus=LoadController.checkInterfaceLoad(((SenderVO)_p2pTransferVO.getSenderVO()).getNetworkCode(),_senderTransferItemVO.getInterfaceID(),_transferID,_p2pTransferVO,true);
			int recieverLoadStatus=0;
			//Further process the request
			if(senderLoadStatus==0) {
				recieverLoadStatus=LoadController.checkInterfaceLoad(((ReceiverVO)_p2pTransferVO.getReceiverVO()).getNetworkCode(),_receiverTransferItemVO.getInterfaceID(),_transferID,_p2pTransferVO,true);
				if(recieverLoadStatus==0) {
					try {
						LoadController.checkTransactionLoad(((SenderVO)_p2pTransferVO.getSenderVO()).getNetworkCode(),_senderTransferItemVO.getInterfaceID(),PretupsI.P2P_MODULE,_transferID,true,LoadControllerI.USERTYPE_SENDER);
					} catch(BTSLBaseException e) {
						//Decreasing interface load of receiver which we had incremented before 27/09/06, sender was decreased in the method
						LoadController.decreaseCurrentInterfaceLoad(_transferID,((ReceiverVO)_p2pTransferVO.getReceiverVO()).getNetworkCode(),_receiverTransferItemVO.getInterfaceID(),LoadControllerI.DEC_LAST_TRANS_COUNT);
						throw e;
					}
					try {
						LoadController.checkTransactionLoad(((ReceiverVO)_p2pTransferVO.getReceiverVO()).getNetworkCode(),_receiverTransferItemVO.getInterfaceID(),PretupsI.P2P_MODULE,_transferID,true,LoadControllerI.USERTYPE_RECEIVER);
					} catch(BTSLBaseException e) {
						//Decreasing interface load of sender which we had incremented before 27/09/06, receiver was decreased in the method
						LoadController.decreaseTransactionInterfaceLoad(_transferID,((SenderVO)_p2pTransferVO.getSenderVO()).getNetworkCode(),LoadControllerI.DEC_LAST_TRANS_COUNT);
						throw e;
					}					
					if(_log.isDebugEnabled()) {
						_log.debug("IATPrepaidController[checkTransactionLoad]","_transferID="+_transferID+" Successfully through load");
					}
				}
				//Request in Queue
				else if(recieverLoadStatus==1) {
					//Decrease the interface counter of the sender that was increased
					LoadController.decreaseCurrentInterfaceLoad(_transferID,((SenderVO)_p2pTransferVO.getSenderVO()).getNetworkCode(),_senderTransferItemVO.getInterfaceID(),LoadControllerI.DEC_LAST_TRANS_COUNT);									
					String strArr[]={_receiverMSISDN,String.valueOf(_p2pTransferVO.getRequestedAmount())};
					throw new BTSLBaseException("IATPrepaidController",METHOD_NAME,PretupsErrorCodesI.REQUEST_IN_QUEUE,0,strArr,null);
				}
				//Refuse the request
				else
					throw new BTSLBaseException("IATPrepaidController",METHOD_NAME,PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
			}
			//Request in Queue
			else if(senderLoadStatus==1) {
				String strArr[]={_receiverMSISDN,String.valueOf(_p2pTransferVO.getRequestedAmount())};
				throw new BTSLBaseException("IATPrepaidController",METHOD_NAME,PretupsErrorCodesI.REQUEST_IN_QUEUE,0,strArr,null);
			}
			//Refuse the request
			else
				throw new BTSLBaseException("IATPrepaidController",METHOD_NAME,PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
		} catch(BTSLBaseException be) {
			_log.error("IATPrepaidController[checkTransactionLoad]","Refusing request getting Exception:"+be.getMessage());
			throw be;
		} catch(Exception e) {
			_log.errorTrace(METHOD_NAME, e);			
			throw new BTSLBaseException("IATPrepaidController",METHOD_NAME,PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION);
		}
	}

	/**
	 * This method will check the transaction load on the given interface
	 * @param p_userType
	 * @param p_interfaceID
	 * @throws BTSLBaseException
	 */
	private void checkTransactionLoad(String p_userType,String p_interfaceID) throws BTSLBaseException {
		final String METHOD_NAME="checkTransactionLoad";
		if(_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME,"Checking load for transfer ID="+_transferID +" on interface="+p_interfaceID);  
		}
		int recieverLoadStatus=0;
		try {
			if(PretupsI.USER_TYPE_SENDER.equals(p_userType)) {
				int senderLoadStatus=LoadController.checkInterfaceLoad(((SenderVO)_p2pTransferVO.getSenderVO()).getNetworkCode(),p_interfaceID,_transferID,_p2pTransferVO,true);
				//Further process the request
				if(senderLoadStatus==0) {
					recieverLoadStatus=LoadController.checkInterfaceLoad(((ReceiverVO)_p2pTransferVO.getReceiverVO()).getNetworkCode(),_receiverTransferItemVO.getInterfaceID(),_transferID,_p2pTransferVO,false);
					if(recieverLoadStatus==0) {
						try {
							LoadController.checkTransactionLoad(((SenderVO)_p2pTransferVO.getSenderVO()).getNetworkCode(),p_interfaceID,PretupsI.P2P_MODULE,_transferID,true,LoadControllerI.USERTYPE_SENDER);
						} catch(BTSLBaseException e) {
							//Decreasing interface load of receiver which we had incremented before 27/09/06, sender was decreased in the method
							LoadController.decreaseCurrentInterfaceLoad(_transferID,((ReceiverVO)_p2pTransferVO.getReceiverVO()).getNetworkCode(),_receiverTransferItemVO.getInterfaceID(),LoadControllerI.DEC_LAST_TRANS_COUNT);
							throw e;
						}
						try {
							LoadController.checkTransactionLoad(((ReceiverVO)_p2pTransferVO.getReceiverVO()).getNetworkCode(),_receiverTransferItemVO.getInterfaceID(),PretupsI.P2P_MODULE,_transferID,true,LoadControllerI.USERTYPE_RECEIVER);
						} catch(BTSLBaseException e) {
							//Decreasing interface load of sender which we had incremented before 27/09/06, receiver was decreased in the method
							LoadController.decreaseTransactionInterfaceLoad(_transferID,((SenderVO)_p2pTransferVO.getSenderVO()).getNetworkCode(),LoadControllerI.DEC_LAST_TRANS_COUNT);
							throw e;
						}
						if(_log.isDebugEnabled()) {
							_log.debug("IATPrepaidController[checkTransactionLoad]","_transferID="+_transferID+" Successfully through load");
						}
					}
					//Refuse the request
					else
						throw new BTSLBaseException("IATPrepaidController",METHOD_NAME,PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
				}
				//Request in Queue
				else if(senderLoadStatus==1) {
					String strArr[]={_receiverMSISDN,String.valueOf(_p2pTransferVO.getRequestedAmount())};
					throw new BTSLBaseException("IATPrepaidController",METHOD_NAME,PretupsErrorCodesI.REQUEST_IN_QUEUE,0,strArr,null);
				}
				//Refuse the request
				else
					throw new BTSLBaseException("IATPrepaidController",METHOD_NAME,PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
			} else {
				//Do not enter the request in Queue
				recieverLoadStatus=LoadController.checkInterfaceLoad(((ReceiverVO)_p2pTransferVO.getReceiverVO()).getNetworkCode(),p_interfaceID,_transferID,_p2pTransferVO,false);
				if(recieverLoadStatus==0) {
					LoadController.checkTransactionLoad(((ReceiverVO)_p2pTransferVO.getReceiverVO()).getNetworkCode(),p_interfaceID,PretupsI.P2P_MODULE,_transferID,true,LoadControllerI.USERTYPE_RECEIVER);
					if(_log.isDebugEnabled()) {
						_log.debug("checkTransactionLoad[checkTransactionLoad]","_transferID="+_transferID+" Successfully through load");
					}
				}
				//Request in Queue
				else if(recieverLoadStatus==1) {
					throw new BTSLBaseException(METHOD_NAME,METHOD_NAME,PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
				}
				//Refuse the request
				else
					throw new BTSLBaseException(METHOD_NAME,METHOD_NAME,PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
			}	
		} catch(BTSLBaseException be) {
			_log.error("IATPrepaidController[checkTransactionLoad]","Refusing request getting Exception:"+be.getMessage());
			throw be;
		} catch(Exception e) {
			_log.errorTrace(METHOD_NAME, e);
			throw new BTSLBaseException("IATPrepaidController",METHOD_NAME,PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION);
		}
	}

	/**
	 * This method will be called to process the request from the queue
	 * @param p_transferVO
	 */
	public void processFromQueue(TransferVO p_transferVO) {
		final String METHOD_NAME="processFromQueue";
		if(_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME,"Entered");	
		}		
		Connection con=null;
		try {
			_p2pTransferVO=(P2PTransferVO)p_transferVO;
			_requestVO=_p2pTransferVO.getRequestVO();
			_senderVO=(SenderVO)_requestVO.getSenderVO();
			_receiverVO=(ReceiverVO)_p2pTransferVO.getReceiverVO();
			_type=_requestVO.getType();
			if(_type.equals(PretupsI.INTERFACE_CATEGORY_BOTH)) {
				_serviceInterfaceRoutingVO=ServiceInterfaceRoutingCache.getServiceInterfaceRoutingDetails(_receiverVO.getNetworkCode()+"_"+_requestVO.getServiceType());
				if (_serviceInterfaceRoutingVO!=null) {
					if(_log.isDebugEnabled()) {
						_log.debug(METHOD_NAME,_requestIDStr,"For ="+_receiverVO.getNetworkCode()+"_"+_requestVO.getServiceType()+" Got Interface Category="+_serviceInterfaceRoutingVO.getInterfaceType()+" Alternate Check Required="+_serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool()+" Alternate Interface="+_serviceInterfaceRoutingVO.getAlternateInterfaceType()+" _oldDefaultSelector="+_oldDefaultSelector+"_newDefaultSelector= "+_newDefaultSelector);
					}				
					_type=_serviceInterfaceRoutingVO.getInterfaceType();
					_oldInterfaceCategory=_type;
					_oldDefaultSelector=_serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode();
					_useAlternateCategory=_serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool();
					_newInterfaceCategory=_serviceInterfaceRoutingVO.getAlternateInterfaceType();
					_newDefaultSelector=_serviceInterfaceRoutingVO.getAlternateDefaultSelectortCode();
				} else {
					_log.info(METHOD_NAME,_requestIDStr,"Service Interface Routing control Not defined, thus using default type="+((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE)));
					EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"IATPrepaidController[process]","",_senderMSISDN,_senderNetworkCode,"Service Interface Routing control Not defined, thus using default type="+((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE)));
					_type=((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE));
					//Changed on 27/05/07 for Service Type selector Mapping
					ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_transferVO.getServiceType());
					if(serviceSelectorMappingVO!=null) {
						_oldDefaultSelector=serviceSelectorMappingVO.getSelectorCode();
					}
				}			       
			} else {
				_serviceInterfaceRoutingVO=ServiceInterfaceRoutingCache.getServiceInterfaceRoutingDetails(_receiverVO.getNetworkCode()+"_"+_requestVO.getServiceType()+"_"+_senderVO.getSubscriberType());
				if (_serviceInterfaceRoutingVO!=null) {
					if(_log.isDebugEnabled()) {
						_log.debug(METHOD_NAME,_requestIDStr,"For ="+_receiverVO.getNetworkCode()+"_"+_requestVO.getServiceType()+" Got Interface Category="+_serviceInterfaceRoutingVO.getInterfaceType()+" Alternate Check Required="+_serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool()+" Alternate Interface="+_serviceInterfaceRoutingVO.getAlternateInterfaceType()+" _oldDefaultSelector="+_oldDefaultSelector+"_newDefaultSelector= "+_newDefaultSelector);
					}
					_oldDefaultSelector=_serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode();
				} else {
					//Changed on 27/05/07 for Service Type selector Mapping
					ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_transferVO.getServiceType());
					if(serviceSelectorMappingVO!=null) {
						_oldDefaultSelector=serviceSelectorMappingVO.getSelectorCode();
					}
					_log.info(METHOD_NAME,_requestIDStr,"Service Interface Routing control Not defined, thus using default Selector="+_oldDefaultSelector);
					EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,"IATPrepaidController[process]","",_senderMSISDN,_senderNetworkCode,"Service Interface Routing control Not defined, thus using default selector="+_oldDefaultSelector);
				}
			}			
			if(BTSLUtil.isNullString(_requestVO.getReqSelector())) {
				if(_log.isDebugEnabled()) {
					_log.debug(METHOD_NAME,_requestIDStr,"Selector Not found in Incoming Message Thus using Selector as  "+_oldDefaultSelector);
				}
				_requestVO.setReqSelector(_oldDefaultSelector);
			} else {
				_newDefaultSelector=_requestVO.getReqSelector();
			}
			_requestIDStr=_requestVO.getRequestIDStr();
			_receiverLocale=_requestVO.getReceiverLocale();
			_transferID=_p2pTransferVO.getTransferID();			
			_senderSubscriberType=_senderVO.getSubscriberType();
			_senderNetworkCode=_senderVO.getNetworkCode();
			_senderMSISDN=((SubscriberVO)_p2pTransferVO.getSenderVO()).getMsisdn();
			_receiverMSISDN=((SubscriberVO)_p2pTransferVO.getReceiverVO()).getMsisdn();
			_senderLocale=_requestVO.getSenderLocale();
			_receiverLocale=_requestVO.getReceiverLocale();			
			_serviceType=_requestVO.getServiceType();
			_senderTransferItemVO=_p2pTransferVO.getSenderTransferItemVO();
			_receiverTransferItemVO=_p2pTransferVO.getReceiverTransferItemVO();
			_performIntfceCatRoutingBeforeVal=_requestVO.isPerformIntfceCatRoutingBeforeVal();
			_receiverDeletionReqFromSubRouting=_requestVO.isReceiverDeletionReqFromSubRouting();
			_receiverInterfaceInfoInDBFound=_requestVO.isReceiverInterfaceInfoInDBFound();
			_senderDeletionReqFromSubRouting=_requestVO.isSenderDeletionReqFromSubRouting();
			_senderInterfaceInfoInDBFound=_requestVO.isSenderInterfaceInfoInDBFound();
			_interfaceCatRoutingDone=_requestVO.isInterfaceCatRoutingDone();
			try {
				LoadController.checkTransactionLoad(((SubscriberVO)_p2pTransferVO.getSenderVO()).getNetworkCode(),_senderTransferItemVO.getInterfaceID(),PretupsI.P2P_MODULE,_transferID,true,LoadControllerI.USERTYPE_SENDER);
			} catch(BTSLBaseException e) {
				//Decreasing interface load of receiver which we had incremented before 27/09/06, sender was decreased in the method
				LoadController.decreaseCurrentInterfaceLoad(_transferID,((ReceiverVO)_p2pTransferVO.getReceiverVO()).getNetworkCode(),_receiverTransferItemVO.getInterfaceID(),LoadControllerI.DEC_LAST_TRANS_COUNT);
				throw e;
			}			
			try {
				LoadController.checkTransactionLoad(((SubscriberVO)_p2pTransferVO.getReceiverVO()).getNetworkCode(),_receiverTransferItemVO.getInterfaceID(),PretupsI.P2P_MODULE,_transferID,true,LoadControllerI.USERTYPE_RECEIVER);
			} catch(BTSLBaseException e) {
				//Decreasing interface load of sender which we had incremented before 27/09/06, receiver was decreased in the method
				LoadController.decreaseTransactionInterfaceLoad(_transferID,((SenderVO)_p2pTransferVO.getSenderVO()).getNetworkCode(),LoadControllerI.DEC_LAST_TRANS_COUNT);
				throw e;
			}			

			mcomCon = new MComConnection();con=mcomCon.getConnection();
			//Loading receiver's controll parameters
			PretupsBL.loadRecieverControlLimits(con,_requestIDStr,_p2pTransferVO);
			_receiverVO.setUnmarkRequestStatus(true);
			try {
				con.commit();
			} catch(Exception e) {
				_log.errorTrace(METHOD_NAME, e);
			}
			if (mcomCon != null) {
				mcomCon.close("IATPrepaidController#processFromQueue");
				mcomCon = null;
			}
			con = null;
			_processedFromQueue=true;
			if(_log.isDebugEnabled()) {
				_log.debug("IATPrepaidController[processFromQueue]","_transferID="+_transferID+" Successfully through load");
			}
			processValidationRequest();
			//Set under process message for the sender and reciever
			p_transferVO.setMessageCode(PretupsErrorCodesI.SENDER_UNDERPROCESS_SUCCESS);
			String[] messageArgArray={p_transferVO.getTransferID(),PretupsBL.getDisplayAmount(p_transferVO.getRequestedAmount())};
			p_transferVO.setMessageArguments(messageArgArray);
		} catch(BTSLBaseException be) {
			_log.errorTrace(METHOD_NAME, be);
			if (mcomCon != null) {
				mcomCon.close("IATPrepaidController#processFromQueue");
				mcomCon = null;
			}
			con=null;			
			try {
				if(_receiverVO!=null && _receiverVO.isUnmarkRequestStatus()) {
					mcomCon = new MComConnection();con=mcomCon.getConnection();			
					PretupsBL.unmarkReceiverLastRequest(con,_requestIDStr,_receiverVO);
				}
			} catch(BTSLBaseException bex) {
				_log.errorTrace(METHOD_NAME, bex);		
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"IATPrepaidController[processFromQueue]",_transferID,_senderMSISDN,_senderNetworkCode,"Leaving Reciever Unmarked Exception:"+bex.getMessage());
			} catch(Exception e) {
				_log.errorTrace(METHOD_NAME, e);
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"IATPrepaidController[processFromQueue]",_transferID,_senderMSISDN,_senderNetworkCode,"Leaving Reciever Unmarked Exception:"+e.getMessage());
				_p2pTransferVO.setErrorCode(PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION);
			}	
			_p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);			
			if(be.isKey()) {
				if(BTSLUtil.isNullString(_p2pTransferVO.getErrorCode())) {
					_p2pTransferVO.setErrorCode(be.getMessageKey());
				}
				_p2pTransferVO.setMessageCode(be.getMessageKey());
				_p2pTransferVO.setMessageArguments(be.getArgs());				
				_requestVO.setMessageCode(be.getMessageKey());
				_requestVO.setMessageArguments(be.getArgs());
			} else {
				_p2pTransferVO.setErrorCode(PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION);
			}
			LoadController.decreaseTransactionLoad(_transferID,_senderNetworkCode,LoadControllerI.DEC_LAST_TRANS_COUNT);
			TransactionLog.log(_transferID,_requestIDStr,_requestVO.getFilteredMSISDN(),"",PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,_p2pTransferVO.getSenderReturnMessage(),PretupsI.TXN_LOG_STATUS_FAIL,"Getting Code="+_requestVO.getMessageCode());
		} catch(Exception e) {
			_log.error(METHOD_NAME,"Exception:"+e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			if (mcomCon != null) {
				mcomCon.close("IATPrepaidController#processFromQueue");
				mcomCon = null;
			}
			con=null;
			try {
				if(_receiverVO!=null && _receiverVO.isUnmarkRequestStatus()) {
					mcomCon = new MComConnection();con=mcomCon.getConnection();
					PretupsBL.unmarkReceiverLastRequest(con,_requestIDStr,_receiverVO);
				}
			} catch(BTSLBaseException bex) {
				_log.errorTrace(METHOD_NAME, bex);
				_p2pTransferVO.setErrorCode(PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION);
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"IATPrepaidController[processFromQueue]",_transferID,_senderMSISDN,_senderNetworkCode,"Leaving Reciever Unmarked Exception:"+bex.getMessage());
			} catch(Exception ex1) {
				_log.errorTrace(METHOD_NAME, ex1);
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"IATPrepaidController[processFromQueue]",_transferID,_senderMSISDN,_senderNetworkCode,"Leaving Reciever Unmarked Exception:"+ex1.getMessage());
				_p2pTransferVO.setErrorCode(PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION);
			}
			if(_recValidationFailMessageRequired) {
				if(_p2pTransferVO.getReceiverReturnMsg()==null || !((BTSLMessages)_p2pTransferVO.getReceiverReturnMsg()).isKey()) {
					if(_transferID!=null) {
						_p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.ICP2P_RECEIVER_FAIL,new String[]{String.valueOf(_transferID),PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount())}));
					} else {
						_p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.ICP2P_FAIL_R,new String[]{PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount())}));
					}
				}
			}
			_p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			_p2pTransferVO.setErrorCode(PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION);
			_requestVO.setMessageCode(PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION);
			_p2pTransferVO.setMessageCode(PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION);
			LoadController.decreaseTransactionLoad(_transferID,_senderNetworkCode,LoadControllerI.DEC_LAST_TRANS_COUNT);
			TransactionLog.log(_transferID,_requestIDStr,_requestVO.getFilteredMSISDN(),"",PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,_p2pTransferVO.getSenderReturnMessage(),PretupsI.TXN_LOG_STATUS_FAIL,"Getting Code="+_requestVO.getMessageCode());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"IATPrepaidController[processFromQueue]",_transferID,_senderMSISDN,_senderNetworkCode,"Exception:"+e.getMessage());
		}
		finally {
			try {
				if(mcomCon==null) {
					mcomCon = new MComConnection();con=mcomCon.getConnection();
				}
				if(_transferID!=null && !_transferDetailAdded) {
					addEntryInTransfers(con);
				}
			} catch(BTSLBaseException be) {
				_log.errorTrace(METHOD_NAME, be);
			} catch(Exception e) {
				_log.errorTrace(METHOD_NAME, e);
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"IATPrepaidController[processFromQueue]",_transferID,_senderMSISDN,_senderNetworkCode,"Exception:"+e.getMessage());
			}
			if(BTSLUtil.isNullString(_p2pTransferVO.getMessageCode())) {
				_p2pTransferVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
			}
			if(con!=null) {
				try {
					con.commit();
				} catch(Exception e) {
					_log.errorTrace(METHOD_NAME, e);
				}
				if (mcomCon != null) {
					mcomCon.close("IATPrepaidController#processFromQueue");
					mcomCon = null;
				}
				con=null;
			}
			if(_p2pTransferVO.getReceiverReturnMsg()!=null&&((BTSLMessages)_p2pTransferVO.getReceiverReturnMsg()).isKey()) {
				BTSLMessages btslRecMessages=(BTSLMessages)_p2pTransferVO.getReceiverReturnMsg();
				(new PushMessage(_receiverMSISDN,BTSLUtil.getMessage(_receiverLocale,btslRecMessages.getMessageKey(),btslRecMessages.getArgs()),_transferID,_p2pTransferVO.getRequestGatewayCode(),_receiverLocale)).push();
			} else if(_p2pTransferVO.getReceiverReturnMsg()!=null) {
				(new PushMessage(_receiverMSISDN,(String)_p2pTransferVO.getReceiverReturnMsg(),_transferID,_p2pTransferVO.getRequestGatewayCode(),_receiverLocale)).push();
			}
			TransactionLog.log(_transferID,_requestVO.getRequestIDStr(),_requestVO.getFilteredMSISDN(),"",PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Leaving the controller after Queue Processing",PretupsI.TXN_LOG_STATUS_SUCCESS,"Getting Code="+_requestVO.getMessageCode());
			if(_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME,"Exiting");			
			}
		}
	}

	/**
	 * Method to populate transfer VO from request VO
	 * @param p_requestVO
	 */
	private void populateVOFromRequest(RequestVO p_requestVO) {
		_p2pTransferVO.setSenderVO(_senderVO);
		_p2pTransferVO.setRequestID(p_requestVO.getRequestIDStr());
		_p2pTransferVO.setModule(p_requestVO.getModule());
		_p2pTransferVO.setInstanceID(p_requestVO.getInstanceID());
		_p2pTransferVO.setRequestGatewayCode(p_requestVO.getRequestGatewayCode());
		_p2pTransferVO.setRequestGatewayType(p_requestVO.getRequestGatewayType());
		_p2pTransferVO.setServiceType(p_requestVO.getServiceType());
		_p2pTransferVO.setSourceType(p_requestVO.getSourceType());
		_p2pTransferVO.setCreatedOn(_currentDate);
		_p2pTransferVO.setCreatedBy(_senderVO.getUserID());
		_p2pTransferVO.setModifiedOn(_currentDate);
		_p2pTransferVO.setModifiedBy(_senderVO.getUserID());
		_p2pTransferVO.setTransferDate(_currentDate);
		_p2pTransferVO.setTransferDateTime(_currentDate);
		_p2pTransferVO.setSenderMsisdn(_senderVO.getMsisdn());
		_p2pTransferVO.setSenderID(_senderVO.getUserID());
		_p2pTransferVO.setNetworkCode(_senderVO.getNetworkCode());
		_p2pTransferVO.setLocale(_senderLocale);
		_p2pTransferVO.setLanguage(_p2pTransferVO.getLocale().getLanguage());
		_p2pTransferVO.setCountry(_p2pTransferVO.getLocale().getCountry());
		_p2pTransferVO.setMsgGatewayFlowType(p_requestVO.getMessageGatewayVO().getFlowType());
		_p2pTransferVO.setMsgGatewayResponseType(p_requestVO.getMessageGatewayVO().getResponseType());
		_p2pTransferVO.setMsgGatewayTimeOutValue(p_requestVO.getMessageGatewayVO().getTimeoutValue());		
	}

	/**
	 * Method to credit back the sender for failed or ambigous transaction
	 * @param p_commonClient
	 * @param p_onlyDecreaseOnly
	 * @throws BTSLBaseException
	 */
	private void creditBackSenderForFailedTrans(CommonClient p_commonClient,boolean p_onlyDecreaseOnly) throws BTSLBaseException {
		final String METHOD_NAME="creditBackSenderForFailedTrans";
		if(_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME,_transferID,"Entered with p_onlyDecreaseOnly="+p_onlyDecreaseOnly);
		}
		Connection con=null;
		try {
			TransactionLog.log(_transferID,_requestIDStr,_senderMSISDN,_senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Credit Back Sender",PretupsI.TXN_LOG_STATUS_SUCCESS,"Transfer Status="+_p2pTransferVO.getTransferStatus()+" Credit Back Allowed="+((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_SNDR_CREDIT_BACK_ALLOWED))).booleanValue()+" Credit in Ambigous ="+((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_SNDR_CREDIT_BK_AMB_STATUS))).booleanValue());
			if(!p_onlyDecreaseOnly) {
				if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_SNDR_CREDIT_BACK_ALLOWED))).booleanValue()) {
					if((((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_SNDR_CREDIT_BK_AMB_STATUS))).booleanValue() && _p2pTransferVO.getTransferStatus().equalsIgnoreCase(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS)) || _p2pTransferVO.getTransferStatus().equalsIgnoreCase(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
						String requestStr=getSenderCreditAdjustStr();
						TransactionLog.log(_transferID,_requestIDStr,_senderMSISDN,_senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_TXNSTAGE_CREDITBACK,requestStr,PretupsI.TXN_LOG_STATUS_SUCCESS,"");
						String senderCreditBackResponse=p_commonClient.process(getSenderCreditAdjustStr(),_transferID,_intModCommunicationTypeS,_intModIPS,_intModPortS,_intModClassNameS);
						TransactionLog.log(_transferID,_requestIDStr,_senderMSISDN,_senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_TXNSTAGE_CREDITBACK,senderCreditBackResponse,PretupsI.TXN_LOG_STATUS_SUCCESS,"");
						if(_log.isDebugEnabled()) {
							_log.debug(METHOD_NAME,_transferID,"senderCreditBackResponse From IN Module="+senderCreditBackResponse);
						}
						boolean isCounterToBeDecreased=true;
						try	{
							updateForSenderCreditBackResponse(senderCreditBackResponse);
						} catch(BTSLBaseException be) {
							_log.error(METHOD_NAME, "Exception " + be.getMessage());
		        			_log.errorTrace(METHOD_NAME,be);
							isCounterToBeDecreased=false;
							TransactionLog.log(_transferID,_requestIDStr,_senderMSISDN,_senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Transaction Not Success",PretupsI.TXN_LOG_STATUS_FAIL,"Transfer Status="+_p2pTransferVO.getTransferStatus()+" Getting Code="+_senderVO.getInterfaceResponseCode());
						}
						TransactionLog.log(_transferID,_requestIDStr,_senderMSISDN,_senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Credit Back Success",PretupsI.TXN_LOG_STATUS_SUCCESS,"");
						mcomCon = new MComConnection();con=mcomCon.getConnection();
						if(isCounterToBeDecreased) {
							SubscriberBL.decreaseTransferOutCounts(con,_p2pTransferVO,PretupsI.SERVICE_TYPE_P2PRECHARGE_IAT);
						}
						_p2pTransferVO.setModifiedOn(_currentDate);
						_p2pTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
						PretupsBL.updateTransferDetails(con,_p2pTransferVO);						
						PretupsBL.addTransferCreditBackDetails(con,_p2pTransferVO.getTransferID(),_senderCreditBackStatusVO);
						con.commit();
						_finalTransferStatusUpdate=false;						
						if(PretupsErrorCodesI.TXN_STATUS_SUCCESS.equals(_p2pTransferVO.getCreditBackStatus())) {
							_p2pTransferVO.setSenderReturnMessage(getSenderCreditBackMessage());
						}
					} else {
						EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,"IATPrepaidController[processFromQueue]",_transferID,_senderMSISDN,_senderNetworkCode,"Credit back not required in case of Ambigous cases");
					}
				} else {
					EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,"IATPrepaidController[creditBackSenderForFailedTrans]",_transferID,_senderMSISDN,_senderNetworkCode,"Credit back Not required in case of failed transactions");
				}	
				TransactionLog.log(_transferID,_requestIDStr,_senderMSISDN,_senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Credit Back Done",PretupsI.TXN_LOG_STATUS_SUCCESS,"");
			} else {
			//When Sender Debit fails the decrease the counters only
				mcomCon = new MComConnection();con=mcomCon.getConnection();
				SubscriberBL.decreaseTransferOutCounts(con,_p2pTransferVO,PretupsI.SERVICE_TYPE_P2PRECHARGE_IAT);
				_p2pTransferVO.setModifiedOn(_currentDate);
				_p2pTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
				PretupsBL.updateTransferDetails(con,_p2pTransferVO);
				con.commit();
				_finalTransferStatusUpdate=false;
			}
		} catch(BTSLBaseException be) {
			if(con!=null) {
				try {
					con.rollback();
				} catch(Exception e) {
					_log.errorTrace(METHOD_NAME, e);
				}
			}
			_finalTransferStatusUpdate=false;
			//EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"IATPrepaidController[creditBackSenderForFailedTrans]",_transferID,_senderMSISDN,_senderNetworkCode,"Exception:"+be.getMessage());
			TransactionLog.log(_transferID,_requestIDStr,_senderMSISDN,"",PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"base Exception while crediting back sender",PretupsI.TXN_LOG_STATUS_FAIL,"Exception:"+be.getMessage()+" Getting Code="+be.getMessageKey());
			throw be;
		} catch(Exception e) {
			if(con!=null) {
				try {
					con.rollback();
				} catch(Exception ex) {
					_log.errorTrace(METHOD_NAME, ex);
				}
			}
			_finalTransferStatusUpdate=false;
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"IATPrepaidController[creditBackSenderForFailedTrans]",_transferID,_senderMSISDN,_senderNetworkCode,"Exception:"+e.getMessage());
			TransactionLog.log(_transferID,_requestIDStr,_senderMSISDN,"",PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"base Exception while crediting back sender",PretupsI.TXN_LOG_STATUS_FAIL,"Getting Exception:"+e.getMessage());
			throw new BTSLBaseException(this,METHOD_NAME,PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION);
		}
		finally {
			if (mcomCon != null) {
				mcomCon.close("IATPrepaidController#creditBackSenderForFailedTrans");
				mcomCon = null;
			}
		}
	}

	/**
	 * Method to perform validation in thread
	 * @throws BTSLBaseException
	 * @throws Exception
	 */
	private void processValidationRequestInThread() throws BTSLBaseException,Exception {
		final String METHOD_NAME="processValidationRequestInThread";
		if(_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME,"Entered and performing validations for transfer ID="+_transferID);  
		}
		try {
			TransactionLog.log(_transferID,_requestIDStr,_senderMSISDN,_senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Performing Validation in thread",PretupsI.TXN_LOG_STATUS_SUCCESS,"");
			processValidationRequest();
		} catch(BTSLBaseException be) {
			_log.error("IATPrepaidController[processValidationRequestInThread]","Getting BTSL Base Exception:"+be.getMessage());
			TransactionLog.log(_transferID,_requestIDStr,_senderMSISDN,_senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Base Exception while performing Validation in thread",PretupsI.TXN_LOG_STATUS_FAIL,"Getting Code="+be.getMessageKey());
			throw be;
		} catch(Exception e) {
			_log.errorTrace(METHOD_NAME, e);
			if(_recValidationFailMessageRequired) {
				if(_p2pTransferVO.getReceiverReturnMsg()==null || !((BTSLMessages)_p2pTransferVO.getReceiverReturnMsg()).isKey()) {
					_p2pTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.ICP2P_RECEIVER_FAIL),new String[]{String.valueOf(_transferID),PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount())}));
				}
			}
			_p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			if(BTSLUtil.isNullString(_p2pTransferVO.getErrorCode())) {
				_p2pTransferVO.setErrorCode(PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION);
			}
			_log.error(this,_transferID,"Exception:"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"IATPrepaidController[run]",_transferID,_senderMSISDN,_senderNetworkCode,"Exception:"+e.getMessage());
			TransactionLog.log(_transferID,_requestIDStr,_senderMSISDN,_senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Exception while performing Validation in thread",PretupsI.TXN_LOG_STATUS_FAIL,"Getting exception ="+e.getMessage());
			throw new BTSLBaseException(this,METHOD_NAME,PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION);
		}
		finally {
			//!_transferDetailAdded Condition Added as we think its not require as already done 
			if(_transferID!=null && !_transferDetailAdded) {	
				Connection con=null;
				try {
					mcomCon = new MComConnection();con=mcomCon.getConnection();
					addEntryInTransfers(con);
					if(_p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
						_finalTransferStatusUpdate=false; //No need to update the status of transaction in run method
					}
				} catch(BTSLBaseException be) {
					_log.errorTrace(METHOD_NAME, be);
				} catch(Exception e) {
					_log.errorTrace(METHOD_NAME, e);
					EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"IATPrepaidController[process]",_transferID,_senderMSISDN,_senderNetworkCode,"Exception:"+e.getMessage());
				}
				finally {
					if (mcomCon != null) {
						mcomCon.close("IATPrepaidController#processValidationRequestInThread");
						mcomCon = null;
					}
					con=null;
				}
			}
			if(_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME,"Exiting");
			}
		}		
	}

	/**
	 * Get the sender Credit Back Adjust String
	 * @return
	 */
	public String getSenderCreditAdjustStr() {
		StringBuffer strBuff=null;
		strBuff=new StringBuffer(getSenderCommonString());
		strBuff.append("&INTERFACE_ACTION="+PretupsI.INTERFACE_CREDIT_ACTION);
		strBuff.append("&INTERFACE_AMOUNT="+_senderTransferItemVO.getTransferValue());
		strBuff.append("&CARD_GROUP="+_p2pTransferVO.getCardGroupCode());
		strBuff.append("&SERVICE_CLASS="+_senderTransferItemVO.getServiceClassCode());
		strBuff.append("&ACCOUNT_ID="+_senderTransferItemVO.getReferenceID());
		strBuff.append("&ACCOUNT_STATUS="+_senderTransferItemVO.getAccountStatus());
		strBuff.append("&SOURCE_TYPE="+_p2pTransferVO.getSourceType());
		strBuff.append("&PRODUCT_CODE="+_p2pTransferVO.getProductCode());
		strBuff.append("&TAX_AMOUNT="+(_p2pTransferVO.getSenderTax1Value()+_p2pTransferVO.getSenderTax2Value()));
		strBuff.append("&ACCESS_FEE="+_p2pTransferVO.getSenderAccessFee());
		strBuff.append("&SENDER_MSISDN="+_senderMSISDN);
		strBuff.append("&RECEIVER_MSISDN="+_receiverMSISDN);
		strBuff.append("&EXTERNAL_ID="+_senderExternalID);
		strBuff.append("&GATEWAY_CODE="+_requestVO.getRequestGatewayCode());
		strBuff.append("&GATEWAY_TYPE="+_requestVO.getRequestGatewayType());
		strBuff.append("&IMSI="+BTSLUtil.NullToString(_senderIMSI));
		strBuff.append("&SENDER_ID="+((SenderVO)_requestVO.getSenderVO()).getUserID());
		strBuff.append("&SERVICE_TYPE="+_senderSubscriberType+"-"+_type);
		strBuff.append("&ADJUST=Y");
		strBuff.append("&INTERFACE_PREV_BALANCE="+_senderTransferItemVO.getPostBalance());		
		// Avinash send the requested amount to IN. to use card group only for reporting purpose.
		strBuff.append("&REQUESTED_AMOUNT="+_p2pTransferVO.getRequestedAmount());
		// Added for closing the sender credit back issue.as below parameter was not set 
		strBuff.append("&CAL_OLD_EXPIRY_DATE="+_senderTransferItemVO.getOldExporyInMillis());//@nu
		strBuff.append("&VALIDITY_DAYS="+_senderTransferItemVO.getValidity());
		if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ENQ_POSTBAL_IN))).booleanValue()) {
			strBuff.append("&ENQ_POSTBAL_IN="+PretupsI.YES);
		} else {
			strBuff.append("&ENQ_POSTBAL_IN="+PretupsI.NO);
		}
		if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ENQ_POSTBAL_ALLOW))).booleanValue()) {
			strBuff.append("&ENQ_POSTBAL_ALLOW="+PretupsI.YES);
		} else {
			strBuff.append("&ENQ_POSTBAL_ALLOW="+PretupsI.NO);
		}
		return strBuff.toString();
	}

	/**
	 * Get the receiver Under process message
	 * @return
	 */
	private String getReceiverUnderProcessMessage() {
		String[] messageArgArray={_transferID,PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()),PretupsBL.getDisplayAmount(_p2pTransferVO.getReceiverTransferValue()),_senderMSISDN,PretupsBL.getDisplayAmount(_p2pTransferVO.getReceiverAccessFee())};
		return BTSLUtil.getMessage(_receiverLocale,PretupsErrorCodesI.ICP2P_RECEIVER_UNDERPROCESS,messageArgArray);
	}

	/**
	 *  Method to get the under process message before validation to be sent to sender
	 * @return
	 */
	private String getSndrUPMsgBeforeValidation() {
		String[] messageArgArray={_receiverMSISDN,_transferID,PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount())};
		return BTSLUtil.getMessage(_senderLocale,PretupsErrorCodesI.ICP2P_SENDER_UNDERPROCESS_B4VAL,messageArgArray);
	}

	/**
	 * Method to get the success message to be sent to sender
	 * @return
	 */
	private String getSenderUnderProcessMessage() {
		String[] messageArgArray={_receiverMSISDN,_transferID,PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()),PretupsBL.getDisplayAmount(_p2pTransferVO.getSenderTransferValue()),PretupsBL.getDisplayAmount(_p2pTransferVO.getSenderAccessFee())};
		return BTSLUtil.getMessage(_senderLocale,PretupsErrorCodesI.ICP2P_SENDER_UNDERPROCESS,messageArgArray);
	}
	/**
	 * Method to get the credit back message
	 * @return
	 */
	private String getSenderCreditBackMessage() {
		if(BTSLUtil.isNullString(_senderCreditPostBalanceAvailable) || "Y".equals(_senderCreditPostBalanceAvailable)) {
			String[] messageArgArray={_receiverMSISDN,_transferID,PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()),PretupsBL.getDisplayAmount(_senderTransferItemVO.getPostBalance())};
			return BTSLUtil.getMessage(_senderLocale,PretupsErrorCodesI.ICP2P_SENDER_CREDIT_BACK,messageArgArray);
		}
		String[] messageArgArray={_receiverMSISDN,_transferID,PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount())};
		return BTSLUtil.getMessage(_senderLocale,PretupsErrorCodesI.ICP2P_SENDER_CREDIT_BACK_WITHOUT_POSTBAL,messageArgArray);
	}

	/**
	 * Method that will add entry in Transfer Table if not added else update the records
	 * @param p_con
	 */
	private void addEntryInTransfers(Connection p_con) {
		final String METHOD_NAME="addEntryInTransfers";
		try {
			//METHOD FOR INSERTING AND UPDATION IN P2P Transfer Table
			if(!_transferDetailAdded) {
				PretupsBL.addTransferDetails(p_con,_p2pTransferVO);//add transfer details in database
			} else if(_transferDetailAdded) {
				_p2pTransferVO.setModifiedOn(new Date());
				_p2pTransferVO.setModifiedBy(_p2pTransferVO.getSenderID());
				PretupsBL.updateTransferDetails(p_con,_p2pTransferVO);//add transfer details in database
			}
			p_con.commit();
		} catch(BTSLBaseException be) {
			_log.errorTrace(METHOD_NAME, be);
			if(!_isCounterDecreased  && _decreaseTransactionCounts) {
				LoadController.decreaseTransactionLoad(_transferID,_senderNetworkCode,LoadControllerI.DEC_LAST_TRANS_COUNT);
				_isCounterDecreased=true;
			}			
			_log.error(METHOD_NAME,_transferID,"BTSLBaseException while adding transfer details in database:"+be.getMessage());
		} catch(Exception e) {
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"IATPrepaidController[process]",_transferID,_senderMSISDN,_senderNetworkCode,"Exception:"+e.getMessage());
			if(!_isCounterDecreased  && _decreaseTransactionCounts) {
				LoadController.decreaseTransactionLoad(_transferID,_senderNetworkCode,LoadControllerI.DEC_LAST_TRANS_COUNT);
				_isCounterDecreased=true;
			}
			_log.error(METHOD_NAME,_transferID,"Exception while adding transfer details in database:"+e.getMessage());
		}
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
	 * @param p_userType: SENDER or RECEIVER
	 * @param p_action: VALIDATE OR UPDATE
	 * @return
	 */
	private boolean getInterfaceRoutingDetails(Connection p_con,String p_msisdn,long p_prefixID,String p_subscriberType,String p_networkCode,String p_serviceType,String p_interfaceCategory,String p_userType,String p_action) throws BTSLBaseException {
		final String METHOD_NAME="getInterfaceRoutingDetails";
		if(_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME,_requestIDStr," Entered with MSISDN="+p_msisdn+" Prefix ID="+p_prefixID+" p_subscriberType="+p_subscriberType+" p_networkCode="+p_networkCode+" p_serviceType="+p_serviceType+" p_interfaceCategory="+p_interfaceCategory+" p_userType="+p_userType+" p_action="+p_action);
		}
		boolean isSuccess=false;
		/* Get the routing control parameters based on network code , service and interface category
		 * 1. Check if database check is required
		 * 2. If required then check in database whether the number is present
		 * 3. If present then Get the interface ID from the same and send request to interface to validate the same
		 * 4. If not found then Get the interface ID On the Series basis and send request to interface to validate the same
		 */
		String interfaceID=null;
		String interfaceHandlerClass=null;
		String underProcessMsgReqd=null;
		String allServiceClassID=null;
		String externalID=null;
		_performIntfceCatRoutingBeforeVal=false; //Set so that receiver flag is not overridden by sender flag
		SubscriberRoutingControlVO subscriberRoutingControlVO=SubscriberRoutingControlCache.getRoutingControlDetails(p_networkCode+"_"+p_serviceType+"_"+p_interfaceCategory);
		try {
			if(subscriberRoutingControlVO!=null) {
				if(_log.isDebugEnabled()) {
					_log.debug(METHOD_NAME,_transferID," p_userType="+p_userType+" Database Check Required="+subscriberRoutingControlVO.isDatabaseCheckBool()+" Series Check Required="+subscriberRoutingControlVO.isSeriesCheckBool());
				}
				if(subscriberRoutingControlVO.isDatabaseCheckBool()&&!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue()) {
					if(p_interfaceCategory.equalsIgnoreCase(PretupsI.INTERFACE_CATEGORY_PRE)) {
						ListValueVO listValueVO=PretupsBL.validateNumberInRoutingDatabase(p_con,p_msisdn,p_interfaceCategory);
						if(listValueVO!=null) {
							isSuccess=true;
							setInterfaceDetails(p_prefixID,p_userType,listValueVO,false,null,null);
							if(p_userType.equals(PretupsI.USER_TYPE_SENDER) && p_action.equals(PretupsI.INTERFACE_VALIDATE_ACTION)) {
								_senderInterfaceInfoInDBFound=true;
								_senderDeletionReqFromSubRouting=true;
							} else if(p_userType.equals(PretupsI.USER_TYPE_RECEIVER) && p_action.equals(PretupsI.INTERFACE_VALIDATE_ACTION)) {
								_receiverInterfaceInfoInDBFound=true;
								_receiverDeletionReqFromSubRouting=true;
							}
						} else if(subscriberRoutingControlVO.isSeriesCheckBool()) {
							if(_log.isDebugEnabled()) {
								_log.debug(METHOD_NAME,_transferID," p_userType="+p_userType+" MSISDN ="+p_msisdn+" not found in Database , performing Series Check for Prefix ID="+p_prefixID);
							}
							// service selector based checks added 							
							ServiceSelectorInterfaceMappingVO interfaceMappingVO1=null;
							MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null; 
							if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue()) {
								interfaceMappingVO1=(ServiceSelectorInterfaceMappingVO)ServiceSelectorInterfaceMappingCache.getObject(_serviceType+"_"+_p2pTransferVO.getSubService()+"_"+p_action+"_"+p_networkCode+"_"+p_prefixID);
							}
							if(interfaceMappingVO1==null) {	
								try	{
									interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_prefixID, p_subscriberType,p_action);
									isSuccess=true;
									setInterfaceDetails(p_prefixID,p_userType,null,true,interfaceMappingVO,null);
								} catch(BTSLBaseException be)	{
									if(be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.MSISDN_PREFIX_INTERFACE_MAPPING_NOTFOUND))) {
										_performIntfceCatRoutingBeforeVal=true;
									} else {
										throw be;
									}
								}
							} else {
								isSuccess=true;
								setInterfaceDetails(p_prefixID,p_userType,null,true,null,interfaceMappingVO1);
							}
						} else {
							_performIntfceCatRoutingBeforeVal=true;
							isSuccess=false;
						}
					} else if(p_interfaceCategory.equalsIgnoreCase(PretupsI.INTERFACE_CATEGORY_POST)) {
						WhiteListVO whiteListVO=PretupsBL.validateNumberInWhiteList(p_con,p_msisdn);
						if(whiteListVO!=null) {
							isSuccess=true;
							ListValueVO listValueVO=whiteListVO.getListValueVO();
							interfaceID=listValueVO.getValue();
							interfaceHandlerClass=listValueVO.getLabel();
							underProcessMsgReqd=listValueVO.getType();
							allServiceClassID=listValueVO.getTypeName();
							externalID=listValueVO.getIDValue();
							if(p_userType.equals(PretupsI.USER_TYPE_SENDER)) {
								_senderTransferItemVO.setInterfaceID(interfaceID);
								_senderTransferItemVO.setInterfaceHandlerClass(interfaceHandlerClass);
								_senderAllServiceClassID=allServiceClassID;
								_senderExternalID=externalID;
								//Mark the Post Paid Interface as Online
								_senderVO.setPostOfflineInterface(true);
								_senderTransferItemVO.setPreviousBalance(whiteListVO.getCreditLimit());
								_senderVO.setCreditLimit(whiteListVO.getCreditLimit());
								_senderTransferItemVO.setReferenceID(whiteListVO.getAccountID());
								_senderTransferItemVO.setAccountStatus(whiteListVO.getAccountStatus());
								_senderIMSI=whiteListVO.getImsi();
								_senderTransferItemVO.setPrefixID(p_prefixID);
								_senderTransferItemVO.setServiceClassCode(whiteListVO.getServiceClassCode());
								if(p_action.equals(PretupsI.INTERFACE_VALIDATE_ACTION)) {
									_senderInterfaceInfoInDBFound=true;
								}
							} else {
								_receiverTransferItemVO.setPrefixID(p_prefixID);
								_receiverTransferItemVO.setInterfaceID(interfaceID);
								_receiverTransferItemVO.setInterfaceHandlerClass(interfaceHandlerClass);
								if(PretupsI.YES.equals(underProcessMsgReqd)) {
									_p2pTransferVO.setUnderProcessMsgReq(true);
								}
								_receiverAllServiceClassID=allServiceClassID;
								_receiverExternalID=externalID;
								_receiverVO.setPostOfflineInterface(true);
								_receiverTransferItemVO.setPreviousBalance(whiteListVO.getCreditLimit());
								_receiverTransferItemVO.setServiceClassCode(whiteListVO.getServiceClassCode());
								_receiverTransferItemVO.setReferenceID(whiteListVO.getAccountID());
								_receiverIMSI=whiteListVO.getImsi();
								_receiverTransferItemVO.setAccountStatus(whiteListVO.getAccountStatus());
								if(p_action.equals(PretupsI.INTERFACE_VALIDATE_ACTION)) {
									_receiverInterfaceInfoInDBFound=true;
								}
							}
							if(!PretupsI.YES.equals(listValueVO.getStatus())) {
								//ChangeID=LOCALEMASTER
								//which language message to be set is determined from the locale master table for the requested locale
								if(PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(_senderLocale)).getMessage())) {							
									_p2pTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo());
								} else { 
									_p2pTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo2());
								}
								throw new BTSLBaseException(this,METHOD_NAME,PretupsErrorCodesI.INTERFACE_NOT_ACTIVE);
							}							
						} else if(subscriberRoutingControlVO.isSeriesCheckBool()) {
							if(_log.isDebugEnabled()) {
								_log.debug(METHOD_NAME,_transferID," p_userType="+p_userType+" MSISDN ="+p_msisdn+" not found in Database , performing Series Check for Prefix ID="+p_prefixID);
							}
							MSISDNPrefixInterfaceMappingVO interfaceMappingVO=null;
							//check service selector based check loading of interface 
							ServiceSelectorInterfaceMappingVO interfaceMappingVO1=null;
							if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue()) {
								interfaceMappingVO1=(ServiceSelectorInterfaceMappingVO)ServiceSelectorInterfaceMappingCache.getObject(_serviceType+"_"+_p2pTransferVO.getSubService()+"_"+p_action+"_"+p_networkCode+"_"+p_prefixID);
							}
							if(interfaceMappingVO1==null) {	
								try {
									interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_prefixID, p_subscriberType,p_action);
									isSuccess=true;	
									setInterfaceDetails(p_prefixID,p_userType,null,true,interfaceMappingVO,null);
								} catch(BTSLBaseException be) {
									if(be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.MSISDN_PREFIX_INTERFACE_MAPPING_NOTFOUND))) {
										_performIntfceCatRoutingBeforeVal=true;
									} else {
										throw be;
									}
								}
							} else {
								isSuccess=true;	
								setInterfaceDetails(p_prefixID,p_userType,null,true,null,interfaceMappingVO1);
							}
						} else {
							isSuccess=false;
							_performIntfceCatRoutingBeforeVal=true;
						}
					}
				} else if(subscriberRoutingControlVO.isSeriesCheckBool()) {
					if(_log.isDebugEnabled()) {
						_log.debug(METHOD_NAME,_transferID," p_userType="+p_userType+" MSISDN ="+p_msisdn+" performing Series Check for Prefix ID="+p_prefixID);
					}
					MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null;
					//check service selector based check loading of interface 
					ServiceSelectorInterfaceMappingVO interfaceMappingVO1=null;
					if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue()) {
						interfaceMappingVO1=(ServiceSelectorInterfaceMappingVO)ServiceSelectorInterfaceMappingCache.getObject(_serviceType+"_"+_p2pTransferVO.getSubService()+"_"+p_action+"_"+p_networkCode+"_"+p_prefixID);
					}
					if(interfaceMappingVO1==null) {	
						try {
							interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_prefixID, p_subscriberType,p_action);
							isSuccess=true;
							setInterfaceDetails(p_prefixID,p_userType,null,true,interfaceMappingVO,null);
						} catch(BTSLBaseException be) {
							if(be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.MSISDN_PREFIX_INTERFACE_MAPPING_NOTFOUND))) {
								_performIntfceCatRoutingBeforeVal=true;
							} else {
								throw be;
							}
						}
					} else {
						isSuccess=true;
						setInterfaceDetails(p_prefixID,p_userType,null,true,null,interfaceMappingVO1);
					}
				} else
					isSuccess=false;
			} else {
				if(_log.isDebugEnabled()) {
					_log.debug(METHOD_NAME,_transferID," By default carrying out series check as routing control not defined for p_userType="+p_userType+" MSISDN ="+p_msisdn+" performing Series Check for Prefix ID="+p_prefixID);
				}
				//This event is raised by ankit Z on date 3/8/06 for case when entry not found in routing control and considering series based routing
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,"IATPrepaidController[getInterfaceRoutingDetails]",_transferID,_senderMSISDN,_senderNetworkCode,"Exception:Routing control information not defined so performing series based routing");
				MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null; 
				//check service selector based check loading of interface 
				ServiceSelectorInterfaceMappingVO interfaceMappingVO1=null;
				if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue()) {
					interfaceMappingVO1=(ServiceSelectorInterfaceMappingVO)ServiceSelectorInterfaceMappingCache.getObject(_serviceType+"_"+_p2pTransferVO.getSubService()+"_"+p_action+"_"+p_networkCode+"_"+p_prefixID);
				}
				if(interfaceMappingVO1==null) {	
					try {
						interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_prefixID, p_subscriberType,p_action);
						isSuccess=true;
						setInterfaceDetails(p_prefixID,p_userType,null,true,interfaceMappingVO,null);
					} catch(BTSLBaseException be) {
						if(be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.MSISDN_PREFIX_INTERFACE_MAPPING_NOTFOUND))) {
							_performIntfceCatRoutingBeforeVal=true;
						} else {
							throw be;
						}
					}
				} else {
					isSuccess=true;
					setInterfaceDetails(p_prefixID,p_userType,null,true,null,interfaceMappingVO1);
				}
			}
			if(isSuccess && p_userType.equals(PretupsI.USER_TYPE_SENDER)) {
				_senderTransferItemVO.setInterfaceType(p_interfaceCategory);
			} else if(isSuccess && p_userType.equals(PretupsI.USER_TYPE_RECEIVER)) {
				_receiverTransferItemVO.setInterfaceType(_type);
				_receiverTransferItemVO.setSubscriberType(_type);				
			}
		} catch(BTSLBaseException be) {
			if(isSuccess && p_userType.equals(PretupsI.USER_TYPE_SENDER)) {
				_senderTransferItemVO.setInterfaceType(p_interfaceCategory);
			} else if(isSuccess && p_userType.equals(PretupsI.USER_TYPE_RECEIVER)) {
				_receiverTransferItemVO.setInterfaceType(_type);
				_receiverTransferItemVO.setSubscriberType(_type);				
			}
			throw be;
		} catch(Exception e) {
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"IATPrepaidController[getInterfaceRoutingDetails]",_transferID,_senderMSISDN,_senderNetworkCode,"Exception:"+e.getMessage());
			isSuccess=false;
			throw new BTSLBaseException(this,METHOD_NAME,PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION);
		}
		if(_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME,_requestIDStr," Exiting with isSuccess="+isSuccess+ "_senderAllServiceClassID="+_senderAllServiceClassID+" _receiverAllServiceClassID="+_receiverAllServiceClassID);
		}
		return isSuccess;
	}

	/**
	 * Method to perform the sender alternate intreface routing controls 
	 * @param altList
	 * @throws BTSLBaseException
	 */
	private void performSenderAlternateRouting(ArrayList altList) throws BTSLBaseException {
		final String METHOD_NAME="performSenderAlternateRouting";
		if(_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME,_requestIDStr," Entered ");
		}
		Connection con=null;
		try {
			if(altList!=null && !altList.isEmpty()) {
				//Check Interface Routing if not exists then continue
				//else decrease counters 
				//Validate All service class checks
				//Decrease Counters for transaction and interface
				//Check Interface and transaction load
				//Send request
				//If success then update the subscriber routing table with new interface ID
				//Also store in global veriables
				//If Not Found repeat the iteration for alt 2
				ListValueVO listValueVO=null;
				String requestStr=null;
				CommonClient commonClient=null;
				String senderValResponse=null;
				switch (altList.size()) {
				case 1: {
					LoadController.decreaseResponseCounters(_transferID,PretupsErrorCodesI.TXN_STATUS_FAIL,LoadControllerI.SENDER_VAL_RESPONSE);
					LoadController.decreaseTransactionInterfaceLoad(_transferID,_p2pTransferVO.getNetworkCode(),LoadControllerI.DEC_LAST_TRANS_COUNT);
					listValueVO=(ListValueVO)altList.get(0);
					setInterfaceDetails(_senderTransferItemVO.getPrefixID(),PretupsI.USER_TYPE_SENDER,listValueVO,false,null,null);
					checkTransactionLoad(PretupsI.USER_TYPE_SENDER,_senderTransferItemVO.getInterfaceID());
					//validate sender limits after Interface Validations
					mcomCon = new MComConnection();con=mcomCon.getConnection();
					SubscriberBL.validateSenderLimits(con,_p2pTransferVO,PretupsI.TRANS_STAGE_BEFORE_INVAL);
					if (mcomCon != null) {
						mcomCon.close("IATPrepaidController#performSenderAlternateRouting");
						mcomCon = null;
					}
					con=null;
					requestStr=getSenderValidateStr();
					commonClient=new CommonClient();
					LoadController.incrementTransactionInterCounts(_transferID,LoadControllerI.SENDER_UNDER_VAL);
					TransactionLog.log(_transferID,_requestIDStr,_senderMSISDN,_senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_TXNSTAGE_INVAL,requestStr,PretupsI.TXN_LOG_STATUS_SUCCESS,"");
					if(_log.isDebugEnabled()) {
						_log.debug(METHOD_NAME,"Sending Request For MSISDN="+_senderMSISDN+" on ALternate Routing 1 to ="+_senderTransferItemVO.getInterfaceID());  
					}
					senderValResponse=commonClient.process(requestStr,_transferID,_intModCommunicationTypeS,_intModIPS,_intModPortS,_intModClassNameS);
					TransactionLog.log(_transferID,_requestIDStr,_senderMSISDN,_senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_INVAL,senderValResponse,PretupsI.TXN_LOG_STATUS_SUCCESS,"");
					try {
						senderValidateResponse(senderValResponse,1,altList.size());							
						if(InterfaceErrorCodesI.SUCCESS.equals(_senderTransferItemVO.getValidationStatus())) {
							_isSenderRoutingUpdate=true;
						}
					} catch(BTSLBaseException be) {
						throw be;
					} catch(Exception e) {
						throw new BTSLBaseException(this, METHOD_NAME, "Exception in performing the sender alternate intreface routing controls");
					}
					break;
				}
				case 2: {	
					LoadController.decreaseResponseCounters(_transferID,PretupsErrorCodesI.TXN_STATUS_FAIL,LoadControllerI.SENDER_VAL_RESPONSE);
					LoadController.decreaseTransactionInterfaceLoad(_transferID,_p2pTransferVO.getNetworkCode(),LoadControllerI.DEC_LAST_TRANS_COUNT);
					listValueVO=(ListValueVO)altList.get(0);
					setInterfaceDetails(_senderTransferItemVO.getPrefixID(),PretupsI.USER_TYPE_SENDER,listValueVO,false,null,null);
					checkTransactionLoad(PretupsI.USER_TYPE_SENDER,_senderTransferItemVO.getInterfaceID());
					mcomCon = new MComConnection();con=mcomCon.getConnection();
					SubscriberBL.validateSenderLimits(con,_p2pTransferVO,PretupsI.TRANS_STAGE_BEFORE_INVAL);
					if (mcomCon != null) {
						mcomCon.close("IATPrepaidController#performSenderAlternateRouting");
						mcomCon = null;
					}
					con=null;
					requestStr=getSenderValidateStr();
					commonClient=new CommonClient();
					LoadController.incrementTransactionInterCounts(_transferID,LoadControllerI.SENDER_UNDER_VAL);
					TransactionLog.log(_transferID,_requestIDStr,_senderMSISDN,_senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_TXNSTAGE_INVAL,requestStr,PretupsI.TXN_LOG_STATUS_SUCCESS,"");
					if(_log.isDebugEnabled()) {
						_log.debug(METHOD_NAME,"Sending Request For MSISDN="+_senderMSISDN+" on ALternate Routing 1 to ="+_senderTransferItemVO.getInterfaceID());  						
					}
					senderValResponse=commonClient.process(requestStr,_transferID,_intModCommunicationTypeS,_intModIPS,_intModPortS,_intModClassNameS);
					TransactionLog.log(_transferID,_requestIDStr,_senderMSISDN,_senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_INVAL,senderValResponse,PretupsI.TXN_LOG_STATUS_SUCCESS,"");
					try {
						senderValidateResponse(senderValResponse,1,altList.size());
						if(PretupsI.INTERFACE_CATEGORY_PRE.equals(_type) && InterfaceErrorCodesI.SUCCESS.equals(_senderTransferItemVO.getValidationStatus())) {
							//Update in DB for routing interface 
							updateSubscriberRoutingDetails(PretupsI.USER_TYPE_SENDER,_p2pTransferVO.getNetworkCode(),_senderTransferItemVO.getInterfaceID(),_senderExternalID,_senderMSISDN,_p2pTransferVO.getPaymentMethodType(),_senderVO.getUserID(),_currentDate);
						}
					} catch(BTSLBaseException be) {
						if(be.isKey() && InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(be.getMessageKey())) {	
							if(_log.isDebugEnabled()) {
								_log.debug(METHOD_NAME,"Got Status="+InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND +" After validation Request For MSISDN="+_senderMSISDN+" Performing Alternate Routing to 2");
							}
							if (mcomCon != null) {
								mcomCon.close("IATPrepaidController#performSenderAlternateRouting");
								mcomCon = null;
							}
							con=null;
							LoadController.decreaseResponseCounters(_transferID,PretupsErrorCodesI.TXN_STATUS_FAIL,LoadControllerI.SENDER_VAL_RESPONSE);
							LoadController.decreaseTransactionInterfaceLoad(_transferID,_p2pTransferVO.getNetworkCode(),LoadControllerI.DEC_LAST_TRANS_COUNT);
							listValueVO=(ListValueVO)altList.get(1);
							setInterfaceDetails(_senderTransferItemVO.getPrefixID(),PretupsI.USER_TYPE_SENDER,listValueVO,false,null,null);
							checkTransactionLoad(PretupsI.USER_TYPE_SENDER,_senderTransferItemVO.getInterfaceID());
							//validate sender limits after Interface Validations
							mcomCon = new MComConnection();con=mcomCon.getConnection();
							SubscriberBL.validateSenderLimits(con,_p2pTransferVO,PretupsI.TRANS_STAGE_BEFORE_INVAL);
							if (mcomCon != null) {
								mcomCon.close("IATPrepaidController#performSenderAlternateRouting");
								mcomCon = null;
							}
							con=null;
							requestStr=getSenderValidateStr();
							LoadController.incrementTransactionInterCounts(_transferID,LoadControllerI.SENDER_UNDER_VAL);
							TransactionLog.log(_transferID,_requestIDStr,_senderMSISDN,_senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_TXNSTAGE_INVAL,requestStr,PretupsI.TXN_LOG_STATUS_SUCCESS,"");
							if(_log.isDebugEnabled()) {
								_log.debug(METHOD_NAME,"Sending Request For MSISDN="+_senderMSISDN+" on ALternate Routing 2 to ="+_senderTransferItemVO.getInterfaceID());  
							}
							senderValResponse=commonClient.process(requestStr,_transferID,_intModCommunicationTypeS,_intModIPS,_intModPortS,_intModClassNameS);
							TransactionLog.log(_transferID,_requestIDStr,_senderMSISDN,_senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_INVAL,senderValResponse,PretupsI.TXN_LOG_STATUS_SUCCESS,"");
							try {
								senderValidateResponse(senderValResponse,2,altList.size());
								if(PretupsI.INTERFACE_CATEGORY_PRE.equals(_type) && InterfaceErrorCodesI.SUCCESS.equals(_senderTransferItemVO.getValidationStatus())) {
									//Update in DB for routing interface 
									updateSubscriberRoutingDetails(PretupsI.USER_TYPE_SENDER,_p2pTransferVO.getNetworkCode(),_senderTransferItemVO.getInterfaceID(),_senderExternalID,_senderMSISDN,_p2pTransferVO.getPaymentMethodType(),_senderVO.getUserID(),_currentDate);
								}
							} catch(BTSLBaseException bex) {
								_log.errorTrace(METHOD_NAME, bex);
								throw bex;
							} catch(Exception e) {
								_log.errorTrace(METHOD_NAME, e);
								throw new BTSLBaseException(this, METHOD_NAME, "Exception in performing the sender alternate intreface routing controls");
							}
						} else {
							_log.errorTrace(METHOD_NAME, be);
							throw be;
						}
					} catch(Exception e) {
						_log.errorTrace(METHOD_NAME, e);
						throw new BTSLBaseException(this, METHOD_NAME, "Exception in performing the sender alternate intreface routing controls");
					}
					break;
				}
				}
			} else return;
		} catch(BTSLBaseException be) {
			_log.errorTrace(METHOD_NAME, be);
			throw be;
		} catch(Exception e) {
			_log.errorTrace(METHOD_NAME, e);			
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"IATPrepaidController[performSenderAlternateRouting]",_transferID,_senderMSISDN,_senderNetworkCode,"Exception:"+e.getMessage());
			throw new BTSLBaseException(this,METHOD_NAME,PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION);
		}
		finally {
			if (mcomCon != null) {
				mcomCon.close("IATPrepaidController#performSenderAlternateRouting");
				mcomCon = null;
			}
			con=null;
		}
		if(_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME,_requestIDStr," Exiting ");
		}
	}

	/**
	 * Method to handle sender validation response for interface routing
	 * @param str
	 * @param p_attempt
	 * @param p_altSize
	 * @throws BTSLBaseException
	 */
	public void senderValidateResponse(String str,int p_attempt,int p_altSize) throws BTSLBaseException	{
		final String METHOD_NAME="senderValidateResponse";
		HashMap map=BTSLUtil.getStringToHash(str,"&","=");
		String status=(String)map.get("TRANSACTION_STATUS");
		//Start: Update the Interface table for the interface ID based on Handler status and update the Cache
		String interfaceStatusType=(String)map.get("INT_SET_STATUS");
		if(!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME.equals(interfaceStatusType))) {
			new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES,_senderTransferItemVO.getInterfaceID(),interfaceStatusType,PretupsErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG,PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
		}
		//:End
		//This has been done so that when Alternate routing has to be performed and when we have to get out and throw error 
		if((InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) && p_attempt==1 && p_attempt<p_altSize) || (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) && !isRoutingSecond)) {
			_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
		}
		_senderTransferItemVO.setProtocolStatus((String)map.get("PROTOCOL_STATUS"));
		_senderTransferItemVO.setAccountStatus((String)map.get("ACCOUNT_STATUS"));
		_senderTransferItemVO.setInterfaceResponseCode((String)map.get("INTERFACE_STATUS"));
		_senderTransferItemVO.setValidationStatus(status);
		_senderVO.setInterfaceResponseCode(_senderTransferItemVO.getInterfaceResponseCode());
		_senderTransferItemVO.setInterfaceReferenceID((String)map.get("IN_TXN_ID"));
		_senderTransferItemVO.setReferenceID((String)map.get("IN_RECON_ID"));		
		//If status is other than Success in validation stage mark sender request as Not applicable and
		//Make transaction Fail
		String [] strArr=null;
		if(BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS)) {
			_p2pTransferVO.setErrorCode(status+"_S");
			_senderVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			_receiverVO.setTransactionStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
			_senderTransferItemVO.setTransferStatus(status);
			_receiverTransferItemVO.setValidationStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
			_receiverTransferItemVO.setTransferStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
			strArr=new String[]{_receiverMSISDN,PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()),_transferID};
			throw new BTSLBaseException(this,METHOD_NAME,PretupsErrorCodesI.ICP2P_SENDER_FAIL,0,strArr,null);
		}

		_senderTransferItemVO.setTransferStatus(status);
		_senderVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
		try {
			_senderTransferItemVO.setPreviousExpiry(BTSLUtil.getDateFromDateString((String)map.get("OLD_EXPIRY_DATE"),"ddMMyyyy"));
		} catch(Exception e) {
			_log.errorTrace(METHOD_NAME, e);
		}
		try {
			_senderTransferItemVO.setPreviousGraceDate(BTSLUtil.getDateFromDateString((String)map.get("OLD_GRACE_DATE"),"ddMMyyyy"));
		} catch(Exception e) {
			_log.errorTrace(METHOD_NAME, e);
		}
		_senderTransferItemVO.setServiceClassCode((String)map.get("SERVICE_CLASS"));
		try {
			_senderTransferItemVO.setPreviousBalance(Long.parseLong((String)map.get("INTERFACE_PREV_BALANCE")));
		} catch(Exception e) {
			_log.error(METHOD_NAME, "Exception " + e.getMessage());
			_log.errorTrace(METHOD_NAME,e);
			_senderTransferItemVO.setBalanceCheckReq(false);
		}
		_senderVO.setCreditLimit(_senderTransferItemVO.getPreviousBalance());
		//Update the Previous Balance in case of Post Paid Offline interface with Credit Limit - Monthly Transfer Amount
		if(_senderVO.isPostOfflineInterface()) {
			boolean isPeriodChange=BTSLUtil.isPeriodChangeBetweenDates(_senderVO.getLastSuccessTransferDate(),_currentDate,BTSLUtil.PERIOD_MONTH);
			if(!isPeriodChange) {		
				_senderTransferItemVO.setPreviousBalance(_senderTransferItemVO.getPreviousBalance()-_senderVO.getMonthlyTransferAmount());
			}
		}
		_senderTransferItemVO.setFirstCall((String)map.get("FIRST_CALL"));
		_senderTransferItemVO.setGraceDaysStr((String)map.get("GRACE_DAYS"));
	}

	/**
	 * Method: updateReceiverLocale
	 * This method update the receiver locale with the language code returned from the IN
	 *  
	 * @param p_languageCode String
	 * @return void
	 */
	public void updateReceiverLocale(String p_languageCode) {
		final String METHOD_NAME="updateReceiverLocale";
		if(_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME,"Entered p_languageCode="+p_languageCode);
		}
		//check if language is returned fron IN or not.
		//If not then send alarm and not set the locale
		//otherwise set the local corresponding to the code returned from the IN.
		if(!BTSLUtil.isNullString(p_languageCode)) {
			try	{
				if(LocaleMasterCache.getLocaleFromCodeDetails(p_languageCode)==null) {
					EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"IATPrepaidController[updateReceiverLocale]",_transferID,_receiverMSISDN,"","Exception: Notification language returned from IN is not defined in system p_languageCode: "+p_languageCode);
				} else {
					_receiverLocale=(LocaleMasterCache.getLocaleFromCodeDetails(p_languageCode));
				}
			} catch(Exception e) {
				_log.errorTrace(METHOD_NAME, e);
			}
		}
		if(_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME,"Exited _receiverLocale="+_receiverLocale);
		}
	}

	/**
	 * This method will perform the alternate interface category routing if there 
	 * This method will be called either after validation or after performing interface routing
	 * @throws BTSLBaseException
	 */
	public void performAlternateCategoryRouting() throws BTSLBaseException {
		final String METHOD_NAME="performAlternateCategoryRouting";
		if(_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME,"Performing ALternate interface category routing Entered");  
		}
		Connection con=null;
		try {
			String requestStr=null;
			CommonClient commonClient=null;
			String receiverValResponse=null;
			LoadController.decreaseResponseCounters(_transferID,PretupsErrorCodesI.TXN_STATUS_FAIL,LoadControllerI.RECEIVER_VAL_RESPONSE);
			LoadController.decreaseReceiverTransactionInterfaceLoad(_transferID,_p2pTransferVO.getReceiverNetworkCode(),LoadControllerI.DEC_LAST_TRANS_COUNT);
			mcomCon = new MComConnection();con=mcomCon.getConnection();
			//populates the alternate interface category details
			populateAlternateInterfaceDetails(con);
			if(con!=null) {
				try {
					con.rollback();
				} catch(Exception be) {
					_log.errorTrace(METHOD_NAME, be);
				}
				if (mcomCon != null) {
					mcomCon.close("IATPrepaidController#performAlternateCategoryRouting");
					mcomCon = null;
				}
				con=null;
			}
			_p2pTransferVO.setTransferCategory(_senderSubscriberType+"-"+_type);
			if(_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME,_requestIDStr,"Overriding transfer Category as :"+_p2pTransferVO.getTransferCategory());
			}
			_p2pTransferVO.setReceiverAllServiceClassID(_receiverAllServiceClassID);			
			checkTransactionLoad(PretupsI.USER_TYPE_RECEIVER,_receiverTransferItemVO.getInterfaceID());
			//validate receiver limits before Interface Validations
			PretupsBL.validateRecieverLimits(null,_p2pTransferVO,PretupsI.TRANS_STAGE_BEFORE_INVAL,PretupsI.P2P_MODULE);
			requestStr=getReceiverValidateStr();
			commonClient=new CommonClient();			
			LoadController.incrementTransactionInterCounts(_transferID,LoadControllerI.RECEIVER_UNDER_VAL);			
			TransactionLog.log(_transferID,_requestIDStr,_receiverMSISDN,_receiverVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_TXNSTAGE_INVAL,requestStr,PretupsI.TXN_LOG_STATUS_SUCCESS,"Performing Alternate Category Routing");
			receiverValResponse=commonClient.process(requestStr,_transferID,_intModCommunicationTypeS,_intModIPS,_intModPortS,_intModClassNameS);			
			TransactionLog.log(_transferID,_requestIDStr,_receiverMSISDN,_receiverVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_INVAL,receiverValResponse,PretupsI.TXN_LOG_STATUS_SUCCESS,"");
			handleReceiverValidateResponse(receiverValResponse,SRC_AFTER_INRESP_CAT_ROUTING);
			if(InterfaceErrorCodesI.SUCCESS.equals(_receiverTransferItemVO.getValidationStatus())) {
				//If mobile number found on Post but previously was defined in PRE then delete the number 
				if(_newInterfaceCategory.equals(PretupsI.INTERFACE_CATEGORY_POST)) {
					if(_receiverDeletionReqFromSubRouting) {
						PretupsBL.deleteSubscriberInterfaceRouting(_receiverMSISDN,_oldInterfaceCategory);
					}
				} else {
					//Update in DB for routing interface 
					SubscriberRoutingControlVO subscriberRoutingControlVO=SubscriberRoutingControlCache.getRoutingControlDetails(_p2pTransferVO.getReceiverNetworkCode()+"_"+_p2pTransferVO.getServiceType()+"_"+_newInterfaceCategory);
					if(!_receiverDeletionReqFromSubRouting && subscriberRoutingControlVO!=null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
						if(_log.isDebugEnabled()) {
							_log.debug(METHOD_NAME,"Inserting the MSISDN="+_receiverMSISDN+" in Subscriber routing database for further usage");  
						}
						PretupsBL.insertSubscriberInterfaceRouting(_receiverTransferItemVO.getInterfaceID(),_receiverExternalID,_receiverMSISDN,_newInterfaceCategory,_senderVO.getUserID(),_currentDate);
						_receiverInterfaceInfoInDBFound=true;
						_receiverDeletionReqFromSubRouting=true;
					}
				}
			}
		} catch(BTSLBaseException be) {
			_log.errorTrace(METHOD_NAME, be);
			throw be;
		} catch(Exception e) {
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"IATPrepaidController[performAlternateCategoryRouting]",_transferID,_senderMSISDN,_senderNetworkCode,"Exception:"+e.getMessage());
			throw new BTSLBaseException(this,METHOD_NAME,PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION);
		}
		finally {
			if(con!=null) {
				try { 
					con.rollback();
				} catch(Exception be) {
					_log.errorTrace(METHOD_NAME, be);
				}
				if (mcomCon != null) {
					mcomCon.close("IATPrepaidController#performAlternateCategoryRouting");
					mcomCon = null;
				}
				con=null;
			}			
		}
	}

	/**
	 * Method to populate the Alternate Interface Details for the Receiver against the new interface category
	 * @throws BTSLBaseException
	 */
	public void populateAlternateInterfaceDetails(Connection p_con) throws BTSLBaseException {
		final String METHOD_NAME="populateAlternateInterfaceDetails";
		if(_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME,"Entered to get the alternate category");  
		}
		boolean isReceiverFound=false;
		if(!_interfaceCatRoutingDone) {
			_interfaceCatRoutingDone=true;
			_type=_newInterfaceCategory;
			_networkPrefixVO=null;
			_requestVO.setReqSelector(_newDefaultSelector);
			_p2pTransferVO.setSubService(_newDefaultSelector);
			//Load the new prefix ID against the interface category , If Not required then give the error
			if(_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME,"Got the alternate category as ="+_type);  
			}
			_networkPrefixVO=(NetworkPrefixVO)NetworkPrefixCache.getObject(_receiverVO.getMsisdnPrefix(),_type);
			if(_networkPrefixVO!=null) {
				if(_log.isDebugEnabled()) {
					_log.debug(METHOD_NAME,"Got the Prefix ID for MSISDN="+_receiverMSISDN+ "Prefix ID="+_networkPrefixVO.getPrefixID());  
				}
				_receiverVO.setNetworkCode(_networkPrefixVO.getNetworkCode());
				_receiverVO.setPrefixID(_networkPrefixVO.getPrefixID());
				_receiverVO.setSubscriberType(_networkPrefixVO.getSeriesType());
				isReceiverFound=getInterfaceRoutingDetails(p_con,_receiverMSISDN,_receiverVO.getPrefixID(),_receiverVO.getSubscriberType(),_receiverVO.getNetworkCode(),_p2pTransferVO.getServiceType(),_type,PretupsI.USER_TYPE_RECEIVER,PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION);
			} else {
				_log.error(this,"Series Not Defined for Alternate Interface ="+_type+" For Series="+_receiverVO.getMsisdnPrefix());
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,"IATPrepaidController[populateAlternateInterfaceDetails]","","","","Series ="+_receiverVO.getMsisdnPrefix()+" Not Defined for Series type="+_type+" But required for validation");
				isReceiverFound=false;
			}
			if(!isReceiverFound) {
				throw new BTSLBaseException(METHOD_NAME,"populateServicePaymentInterfaceDetails",PretupsErrorCodesI.ICP2P_NOTFOUND_SERVICEINTERFACEMAPPING);
			}
		}
	}

	/**
	 * This method handles the receiver validate response after sending request to IN
	 * @param str
	 * @param p_source
	 * @throws BTSLBaseException
	 */
	public void handleReceiverValidateResponse(String str,int p_source) throws BTSLBaseException {
		HashMap map=BTSLUtil.getStringToHash(str,"&","=");
		String status=(String)map.get("TRANSACTION_STATUS");
		ArrayList altList=null;
		boolean isRequired=false;		
		//If we get the MSISDN not found on interface error then perform interface routing
		if(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status)) {
			_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			altList=InterfaceRoutingControlCache.getRoutingControlDetails(_receiverTransferItemVO.getInterfaceID());
			if(altList!=null && !altList.isEmpty()) {
				performReceiverAlternateRouting(altList,p_source);
			} else {
				isRequired=true;
			}
		}
		if(!InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) || isRequired) {
			populateReceiverItemsDetails(map);
		}
	}	

	/**
	 * Method to perform the Interface routing for the subscriber MSISDN
	 * @param altList
	 * @param p_source: Determines whether Alternate category needs to be performed after this or not
	 * @throws BTSLBaseException
	 */
	private void performReceiverAlternateRouting(ArrayList altList,int p_source) throws BTSLBaseException {
		final String METHOD_NAME="performReceiverAlternateRouting";
		if(_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME,_requestIDStr," Entered p_source="+p_source);
		}
		try {
			if(altList!=null && !altList.isEmpty()) {
				//Check Interface Routing if not exists then continue
				//else decrease counters 
				//Validate All service class checks
				//Decrease Counters for transaction and interface
				//Check Interface and transaction load
				//Send request
				//If success then update the subscriber routing table with new interface ID
				//Also store in global veriables
				//If Not Found repeat the iteration for alt 2
				ListValueVO listValueVO=null;
				String requestStr=null;
				CommonClient commonClient=null;
				String receiverValResponse=null;
				switch (altList.size()) {
				case 1: {
					LoadController.decreaseResponseCounters(_transferID,PretupsErrorCodesI.TXN_STATUS_FAIL,LoadControllerI.RECEIVER_VAL_RESPONSE);
					LoadController.decreaseReceiverTransactionInterfaceLoad(_transferID,_p2pTransferVO.getReceiverNetworkCode(),LoadControllerI.DEC_LAST_TRANS_COUNT);
					listValueVO=(ListValueVO)altList.get(0);
					setInterfaceDetails(_receiverTransferItemVO.getPrefixID(),PretupsI.USER_TYPE_RECEIVER,listValueVO,false,null,null);
					checkTransactionLoad(PretupsI.USER_TYPE_RECEIVER,_receiverTransferItemVO.getInterfaceID());
					//validate receiver limits before Interface Validations
					PretupsBL.validateRecieverLimits(null,_p2pTransferVO,PretupsI.TRANS_STAGE_BEFORE_INVAL,PretupsI.P2P_MODULE);
					requestStr=getReceiverValidateStr();
					commonClient=new CommonClient();
					LoadController.incrementTransactionInterCounts(_transferID,LoadControllerI.RECEIVER_UNDER_VAL);
					TransactionLog.log(_transferID,_requestIDStr,_receiverMSISDN,_receiverVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_TXNSTAGE_INVAL,requestStr,PretupsI.TXN_LOG_STATUS_SUCCESS,"Performing Interface Routing 1");
					if(_log.isDebugEnabled()) {
						_log.debug(METHOD_NAME,"Sending Request For MSISDN="+_receiverMSISDN+" on ALternate Routing 1 to ="+_receiverTransferItemVO.getInterfaceID());  
					}
					receiverValResponse=commonClient.process(requestStr,_transferID,_intModCommunicationTypeS,_intModIPS,_intModPortS,_intModClassNameS);
					TransactionLog.log(_transferID,_requestIDStr,_receiverMSISDN,_receiverVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_INVAL,receiverValResponse,PretupsI.TXN_LOG_STATUS_SUCCESS,"");
					try {
						receiverValidateResponse(receiverValResponse,1,altList.size(),p_source);
						//If source is before IN validation then if interface is pre then we need to update in subscriber 
						//Routing but after alternate routing if number is found on another interface
						//Then we need to delete the number from subscriber Routing or Vice versa
						if(p_source==SRC_BEFORE_INRESP_CAT_ROUTING) {
							if(PretupsI.INTERFACE_CATEGORY_PRE.equals(_type) && InterfaceErrorCodesI.SUCCESS.equals(_receiverTransferItemVO.getValidationStatus())) {
								//Update in DB for routing interface 
								updateSubscriberRoutingDetails(PretupsI.USER_TYPE_RECEIVER,_p2pTransferVO.getReceiverNetworkCode(),_receiverTransferItemVO.getInterfaceID(),_receiverExternalID,_receiverMSISDN,_type,_senderVO.getUserID(),_currentDate);
							}
						} else {
							if(InterfaceErrorCodesI.SUCCESS.equals(_receiverTransferItemVO.getValidationStatus())) {
								if(_newInterfaceCategory.equals(PretupsI.INTERFACE_CATEGORY_POST)) {
									if(_receiverDeletionReqFromSubRouting) {
										PretupsBL.deleteSubscriberInterfaceRouting(_receiverMSISDN,_oldInterfaceCategory);
									}
								} else {
									//Update in DB for routing interface 
									SubscriberRoutingControlVO subscriberRoutingControlVO=SubscriberRoutingControlCache.getRoutingControlDetails(_p2pTransferVO.getReceiverNetworkCode()+"_"+_p2pTransferVO.getServiceType()+"_"+_newInterfaceCategory);
									if(!_receiverDeletionReqFromSubRouting && subscriberRoutingControlVO!=null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
										PretupsBL.insertSubscriberInterfaceRouting(_receiverTransferItemVO.getInterfaceID(),_receiverExternalID,_receiverMSISDN,_newInterfaceCategory,_senderVO.getUserID(),_currentDate);
										_receiverInterfaceInfoInDBFound=true;
										_receiverDeletionReqFromSubRouting=true;
									}
								}
							}
						}
					} catch(BTSLBaseException be) {
						_log.errorTrace(METHOD_NAME, be);
						throw be;
					} catch(Exception e) {
						_log.errorTrace(METHOD_NAME, e);
						throw new BTSLBaseException(this, METHOD_NAME, "Exception in performing the Interface routing");
					}
					break;
				}
				case 2: {	
					LoadController.decreaseResponseCounters(_transferID,PretupsErrorCodesI.TXN_STATUS_FAIL,LoadControllerI.RECEIVER_VAL_RESPONSE);
					LoadController.decreaseReceiverTransactionInterfaceLoad(_transferID,_p2pTransferVO.getReceiverNetworkCode(),LoadControllerI.DEC_LAST_TRANS_COUNT);
					listValueVO=(ListValueVO)altList.get(0);
					setInterfaceDetails(_receiverTransferItemVO.getPrefixID(),PretupsI.USER_TYPE_RECEIVER,listValueVO,false,null,null);
					checkTransactionLoad(PretupsI.USER_TYPE_RECEIVER,_receiverTransferItemVO.getInterfaceID());
					//validate receiver limits before Interface Validations
					PretupsBL.validateRecieverLimits(null,_p2pTransferVO,PretupsI.TRANS_STAGE_BEFORE_INVAL,PretupsI.P2P_MODULE);
					requestStr=getReceiverValidateStr();
					commonClient=new CommonClient();
					LoadController.incrementTransactionInterCounts(_transferID,LoadControllerI.RECEIVER_UNDER_VAL);
					TransactionLog.log(_transferID,_requestIDStr,_receiverMSISDN,_receiverVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_TXNSTAGE_INVAL,requestStr,PretupsI.TXN_LOG_STATUS_SUCCESS,"Performing Interface Routing 1");
					if(_log.isDebugEnabled()) {
						_log.debug(METHOD_NAME,"Sending Request For MSISDN="+_receiverMSISDN+" on ALternate Routing 1 to ="+_receiverTransferItemVO.getInterfaceID());  
					}
					receiverValResponse=commonClient.process(requestStr,_transferID,_intModCommunicationTypeS,_intModIPS,_intModPortS,_intModClassNameS);
					TransactionLog.log(_transferID,_requestIDStr,_receiverMSISDN,_receiverVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_INVAL,receiverValResponse,PretupsI.TXN_LOG_STATUS_SUCCESS,"");
					try {
						receiverValidateResponse(receiverValResponse,1,altList.size(),p_source);
						//If source is before IN validation then if interface is pre then we need to update in subscriber 
						//Routing but after alternate routing if number is found on another interface
						//Then we need to delete the number from subscriber Routing or Vice versa
						if(p_source==SRC_BEFORE_INRESP_CAT_ROUTING) {
							if(PretupsI.INTERFACE_CATEGORY_PRE.equals(_type) && InterfaceErrorCodesI.SUCCESS.equals(_receiverTransferItemVO.getValidationStatus())) 	{
								//Update in DB for routing interface 
								updateSubscriberRoutingDetails(PretupsI.USER_TYPE_RECEIVER,_p2pTransferVO.getReceiverNetworkCode(),_receiverTransferItemVO.getInterfaceID(),_receiverExternalID,_receiverMSISDN,_type,_senderVO.getUserID(),_currentDate);
							}
						} else {
							if(InterfaceErrorCodesI.SUCCESS.equals(_receiverTransferItemVO.getValidationStatus())) {
								if(_newInterfaceCategory.equals(PretupsI.INTERFACE_CATEGORY_POST)) {
									if(_receiverDeletionReqFromSubRouting) {
										PretupsBL.deleteSubscriberInterfaceRouting(_receiverMSISDN,_oldInterfaceCategory);
									}
								} else {
									//Update in DB for routing interface 
									SubscriberRoutingControlVO subscriberRoutingControlVO=SubscriberRoutingControlCache.getRoutingControlDetails(_p2pTransferVO.getReceiverNetworkCode()+"_"+_p2pTransferVO.getServiceType()+"_"+_newInterfaceCategory);
									if(!_receiverDeletionReqFromSubRouting && subscriberRoutingControlVO!=null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
										PretupsBL.insertSubscriberInterfaceRouting(_receiverTransferItemVO.getInterfaceID(),_receiverExternalID,_receiverMSISDN,_newInterfaceCategory,_senderVO.getUserID(),_currentDate);
										_receiverInterfaceInfoInDBFound=true;
										_receiverDeletionReqFromSubRouting=true;
									}
								}
							}
						}							
					} catch(BTSLBaseException be) {
						if(be.isKey() && InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(be.getMessageKey())) {	
							if(_log.isDebugEnabled()) {
								_log.debug(METHOD_NAME,"Got Status="+InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND +" After validation Request For MSISDN="+_receiverMSISDN+" Performing Alternate Routing to 2");
							}
							LoadController.decreaseResponseCounters(_transferID,PretupsErrorCodesI.TXN_STATUS_FAIL,LoadControllerI.RECEIVER_VAL_RESPONSE);
							LoadController.decreaseReceiverTransactionInterfaceLoad(_transferID,_p2pTransferVO.getReceiverNetworkCode(),LoadControllerI.DEC_LAST_TRANS_COUNT);
							listValueVO=(ListValueVO)altList.get(1);
							setInterfaceDetails(_receiverTransferItemVO.getPrefixID(),PretupsI.USER_TYPE_RECEIVER,listValueVO,false,null,null);
							checkTransactionLoad(PretupsI.USER_TYPE_RECEIVER,_receiverTransferItemVO.getInterfaceID());
							//validate receiver limits before Interface Validations
							PretupsBL.validateRecieverLimits(null,_p2pTransferVO,PretupsI.TRANS_STAGE_BEFORE_INVAL,PretupsI.P2P_MODULE);
							requestStr=getReceiverValidateStr();
							LoadController.incrementTransactionInterCounts(_transferID,LoadControllerI.RECEIVER_UNDER_VAL);
							TransactionLog.log(_transferID,_requestIDStr,_receiverMSISDN,_receiverVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_TXNSTAGE_INVAL,requestStr,PretupsI.TXN_LOG_STATUS_SUCCESS,"Performing Interface Routing 2");
							if(_log.isDebugEnabled()) {
								_log.debug(METHOD_NAME,"Sending Request For MSISDN="+_receiverMSISDN+" on ALternate Routing 2 to ="+_receiverTransferItemVO.getInterfaceID());  
							}
							receiverValResponse=commonClient.process(requestStr,_transferID,_intModCommunicationTypeS,_intModIPS,_intModPortS,_intModClassNameS);							
							TransactionLog.log(_transferID,_requestIDStr,_receiverMSISDN,_receiverVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_INVAL,receiverValResponse,PretupsI.TXN_LOG_STATUS_SUCCESS,"");
							try {
								receiverValidateResponse(receiverValResponse,2,altList.size(),p_source);
								//If source is before IN validation then if interface is pre then we need to update in subscriber 
								//Routing but after alternate routing if number is found on another interface
								//Then we need to delete the number from subscriber Routing or Vice versa
								if(p_source==SRC_BEFORE_INRESP_CAT_ROUTING) {
									if(PretupsI.INTERFACE_CATEGORY_PRE.equals(_type) && InterfaceErrorCodesI.SUCCESS.equals(_receiverTransferItemVO.getValidationStatus())) {
										//Update in DB for routing interface 
										updateSubscriberRoutingDetails(PretupsI.USER_TYPE_RECEIVER,_p2pTransferVO.getReceiverNetworkCode(),_receiverTransferItemVO.getInterfaceID(),_receiverExternalID,_receiverMSISDN,_type,_senderVO.getUserID(),_currentDate);
									}
								} else {
									if(InterfaceErrorCodesI.SUCCESS.equals(_receiverTransferItemVO.getValidationStatus())) {
										if(_newInterfaceCategory.equals(PretupsI.INTERFACE_CATEGORY_POST)) {
											if(_receiverDeletionReqFromSubRouting) {
												PretupsBL.deleteSubscriberInterfaceRouting(_receiverMSISDN,_oldInterfaceCategory);
											}
										} else {
											//Update in DB for routing interface 
											SubscriberRoutingControlVO subscriberRoutingControlVO=SubscriberRoutingControlCache.getRoutingControlDetails(_p2pTransferVO.getReceiverNetworkCode()+"_"+_p2pTransferVO.getServiceType()+"_"+_newInterfaceCategory);
											if(!_receiverDeletionReqFromSubRouting && subscriberRoutingControlVO!=null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
												PretupsBL.insertSubscriberInterfaceRouting(_receiverTransferItemVO.getInterfaceID(),_receiverExternalID,_receiverMSISDN,_newInterfaceCategory,_senderVO.getUserID(),_currentDate);
												_receiverInterfaceInfoInDBFound=true;
												_receiverDeletionReqFromSubRouting=true;
											}
										}
									}
								}									
							} catch(BTSLBaseException bex) {
								_log.errorTrace(METHOD_NAME, bex);
								throw bex;
							} catch(Exception e) {
								_log.errorTrace(METHOD_NAME, e);
								throw new BTSLBaseException(this, METHOD_NAME, "Exception in performing the Interface routing");
							}
						} else {
							_log.errorTrace(METHOD_NAME, be);
							throw be;
						}
					} catch(Exception e) {
						_log.errorTrace(METHOD_NAME, e);
						throw new BTSLBaseException(this, METHOD_NAME, "Exception in performing the Interface routing");
					}
					break;
				}
				}
			} else return;
		} catch(BTSLBaseException be) {
			_log.errorTrace(METHOD_NAME, be);
			throw be;
		} catch(Exception e) {
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"IATPrepaidController[performAlternateRouting]",_transferID,_senderMSISDN,_senderNetworkCode,"Exception:"+e.getMessage());
			throw new BTSLBaseException(this,METHOD_NAME,PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION);
		}
		if(_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME,_requestIDStr," Exiting ");
		}
	}

	/**
	 * This method validates the response from Interfaces in interface routing
	 * @param str
	 * @param p_attempt
	 * @param p_altSize
	 * @param p_source
	 * @throws BTSLBaseException
	 */
	public void receiverValidateResponse(String str,int p_attempt,int p_altSize,int p_source) throws BTSLBaseException {
		final String METHOD_NAME="receiverValidateResponse";
		HashMap map=BTSLUtil.getStringToHash(str,"&","=");
		String status=(String)map.get("TRANSACTION_STATUS");
		//Start: Update the Interface table for the interface ID based on Handler status and update the Cache
		String interfaceStatusType=(String)map.get("INT_SET_STATUS");
		if(!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME.equals(interfaceStatusType))) {
			new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES,_receiverTransferItemVO.getInterfaceID(),interfaceStatusType,PretupsErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG,PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
		}
		//:End
		if(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) && p_attempt==1 && p_attempt<p_altSize) {
			_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			throw new BTSLBaseException(this,"receiverValidateResponse",InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
		} else if(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) && p_attempt==p_altSize && p_source==SRC_BEFORE_INRESP_CAT_ROUTING && _useAlternateCategory && !_interfaceCatRoutingDone) {
			if(_log.isDebugEnabled()) {
				_log.debug(this," Performing Alternate category routing as MSISDN not found on any interfaces after routing for "+_receiverMSISDN);
			}
			performAlternateCategoryRouting();
		} else {
			if("Y".equals(_requestVO.getUseInterfaceLanguage())) {
				//update the receiver locale if language code returned from IN is not null
				updateReceiverLocale((String)map.get("IN_LANG"));
			}
			_receiverTransferItemVO.setProtocolStatus((String)map.get("PROTOCOL_STATUS"));
			_receiverTransferItemVO.setAccountStatus((String)map.get("ACCOUNT_STATUS"));
			_receiverTransferItemVO.setInterfaceResponseCode((String)map.get("INTERFACE_STATUS"));
			_receiverTransferItemVO.setValidationStatus(status);
			_receiverVO.setInterfaceResponseCode(_receiverTransferItemVO.getInterfaceResponseCode());		
			_receiverTransferItemVO.setInterfaceReferenceID((String)map.get("IN_TXN_ID"));
			_receiverTransferItemVO.setReferenceID((String)map.get("IN_RECON_ID"));		
			//If status is other than Success in validation stage mark sender request as Not applicable and
			//Make transaction Fail
			String [] strArr=null;
			if(BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS)) {
				_p2pTransferVO.setErrorCode(status+"_R");
				_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
				_receiverTransferItemVO.setTransferStatus(status);
				_senderTransferItemVO.setValidationStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
				_senderTransferItemVO.setTransferStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
				_senderVO.setTransactionStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
				strArr=new String[]{_receiverMSISDN,PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()),_transferID};
				throw new BTSLBaseException(METHOD_NAME,METHOD_NAME,PretupsErrorCodesI.ICP2P_SENDER_FAIL,0,strArr,null);
			}
			_receiverTransferItemVO.setTransferStatus(status);
			_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
			_receiverTransferItemVO.setSubscriberType(_type);
			_receiverVO.setSubscriberType(_type);
			try {
				_receiverTransferItemVO.setPreviousExpiry(BTSLUtil.getDateFromDateString((String)map.get("OLD_EXPIRY_DATE"),"ddMMyyyy"));
			} catch(Exception e) {
				_log.errorTrace(METHOD_NAME,e);
			}
			try {
				_receiverTransferItemVO.setPreviousGraceDate(BTSLUtil.getDateFromDateString((String)map.get("OLD_GRACE_DATE"),"ddMMyyyy"));
			} catch(Exception e) {
				_log.errorTrace(METHOD_NAME,e);	
			}
			_receiverVO.setInterfaceResponseCode(_receiverTransferItemVO.getInterfaceResponseCode());
			_receiverTransferItemVO.setFirstCall((String)map.get("FIRST_CALL"));
			_receiverTransferItemVO.setGraceDaysStr((String)map.get("GRACE_DAYS"));
			_receiverTransferItemVO.setServiceClassCode((String)map.get("SERVICE_CLASS"));
			//Done so that receiver check can be brough to common
			_receiverVO.setServiceClassCode(_receiverTransferItemVO.getServiceClass());
			try {
				_receiverTransferItemVO.setPreviousBalance(Long.parseLong((String)map.get("INTERFACE_PREV_BALANCE")));
			} catch(Exception e) {
				_log.errorTrace(METHOD_NAME,e);
			}
			//Update the Previous Balance in case of Post Paid Offline interface with Credit Limit - Monthly Transfer Amount
			if(_receiverVO.isPostOfflineInterface()) {
				boolean isPeriodChange=BTSLUtil.isPeriodChangeBetweenDates(_receiverVO.getLastSuccessOn(),_currentDate,BTSLUtil.PERIOD_MONTH);
				if(!isPeriodChange) {		 	
					_receiverTransferItemVO.setPreviousBalance(_receiverTransferItemVO.getPreviousBalance()-_receiverVO.getMonthlyTransferAmount());
				}
			}	
			//TO DO Done for testing purpose should we use it or give exception in this case
			if(_receiverTransferItemVO.getPreviousExpiry()==null) {
				_receiverTransferItemVO.setPreviousExpiry(_currentDate);
			}
		}
	}	

	/**
	 * This method will populate the receiver Items VO after the response from interfaces
	 * @param p_map
	 * @throws BTSLBaseException
	 */
	public void populateReceiverItemsDetails(HashMap p_map) throws BTSLBaseException {
		final String METHOD_NAME="populateReceiverItemsDetails";
		String status=(String)p_map.get("TRANSACTION_STATUS");
		//receiver language has to be taken from IN then the block below will execute
		if("Y".equals(_requestVO.getUseInterfaceLanguage())) {
			//update the receiver locale if language code returned from IN is not null
			updateReceiverLocale((String)p_map.get("IN_LANG"));
		}
		_receiverTransferItemVO.setProtocolStatus((String)p_map.get("PROTOCOL_STATUS"));
		_receiverTransferItemVO.setAccountStatus((String)p_map.get("ACCOUNT_STATUS"));
		_receiverTransferItemVO.setInterfaceResponseCode((String)p_map.get("INTERFACE_STATUS"));
		_receiverTransferItemVO.setValidationStatus(status);
		_receiverVO.setInterfaceResponseCode(_receiverTransferItemVO.getInterfaceResponseCode());		
		if(!BTSLUtil.isNullString((String)p_map.get("IN_TXN_ID"))) {
			try {
				_receiverTransferItemVO.setInterfaceReferenceID((String)p_map.get("IN_TXN_ID"));
			} catch(Exception e) {
				_log.error(METHOD_NAME, "Exception " + e.getMessage());
				_log.errorTrace(METHOD_NAME,e);
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"IATPrepaidController[updateForReceiverValidateResponse]",_transferID,_senderMSISDN,_senderNetworkCode,"Exception while parsing for interface txn ID , Exception:"+e.getMessage());
			}
		}
		_receiverTransferItemVO.setReferenceID((String)p_map.get("IN_RECON_ID"));	
		String [] strArr=null;
		if(BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS)) {
			_p2pTransferVO.setErrorCode(status+"_R");
			_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			_receiverTransferItemVO.setTransferStatus(status);		
			_senderVO.setTransactionStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
			strArr=new String[]{_receiverMSISDN,PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()),_transferID};
			if(InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED_P2P.equals(_receiverTransferItemVO.getValidationStatus())) {
				throw new BTSLBaseException(METHOD_NAME,METHOD_NAME,InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED_P2P+"_S",0,strArr,null);
			} else {
				throw new BTSLBaseException(METHOD_NAME,METHOD_NAME,_p2pTransferVO.getErrorCode(),0,strArr,null);
			}
		}
		_receiverTransferItemVO.setTransferStatus(status);
		_receiverTransferItemVO.setSubscriberType(_type);
		_receiverVO.setSubscriberType(_type);		
		_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
		SubscriberRoutingControlVO subscriberRoutingControlVO=SubscriberRoutingControlCache.getRoutingControlDetails(_p2pTransferVO.getReceiverNetworkCode()+"_"+_p2pTransferVO.getServiceType()+"_"+_type);
		if(PretupsI.INTERFACE_CATEGORY_PRE.equals(_type) && !_receiverDeletionReqFromSubRouting && subscriberRoutingControlVO!=null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
			PretupsBL.insertSubscriberInterfaceRouting(_receiverTransferItemVO.getInterfaceID(),_receiverExternalID,_receiverMSISDN,_type,_senderVO.getUserID(),_currentDate);
			_receiverInterfaceInfoInDBFound=true;
			_receiverDeletionReqFromSubRouting=true;
		}
		try {
			_receiverTransferItemVO.setPreviousExpiry(BTSLUtil.getDateFromDateString((String)p_map.get("OLD_EXPIRY_DATE"),"ddMMyyyy"));
		} catch(Exception e) {
			_log.errorTrace(METHOD_NAME, e);
		}
		try {
			_receiverTransferItemVO.setPreviousGraceDate(BTSLUtil.getDateFromDateString((String)p_map.get("OLD_GRACE_DATE"),"ddMMyyyy"));
		} catch(Exception e) {
			_log.errorTrace(METHOD_NAME, e);
		}
		_receiverTransferItemVO.setFirstCall((String)p_map.get("FIRST_CALL"));
		_receiverTransferItemVO.setGraceDaysStr((String)p_map.get("GRACE_DAYS"));
		_receiverTransferItemVO.setServiceClassCode((String)p_map.get("SERVICE_CLASS"));
		_receiverTransferItemVO.setOldExporyInMillis((String)p_map.get("CAL_OLD_EXPIRY_DATE"));//@nu
		try {
			_receiverTransferItemVO.setPreviousBalance(Long.parseLong((String)p_map.get("INTERFACE_PREV_BALANCE")));
		} catch(Exception e) {
			_log.errorTrace(METHOD_NAME, e);	
		}		
		_receiverTransferItemVO.setBundleTypes((String)p_map.get("BUNDLE_TYPES"));
		_receiverTransferItemVO.setBonusBundleValidities((String)p_map.get("BONUS_BUNDLE_VALIDITIES"));
		//Update the Previous Balance in case of Post Paid Offline interface with Credit Limit - Monthly Transfer Amount
		if(_receiverVO.isPostOfflineInterface()) {
			boolean isPeriodChange=BTSLUtil.isPeriodChangeBetweenDates(_receiverVO.getLastSuccessOn(),_currentDate,BTSLUtil.PERIOD_MONTH);
			if(!isPeriodChange) {		
				_receiverTransferItemVO.setPreviousBalance(_receiverTransferItemVO.getPreviousBalance()-_receiverVO.getMonthlyTransferAmount());
			}
		}		
		//TO DO Done for testing purpose should we use it or give exception in this case
		if(_receiverTransferItemVO.getPreviousExpiry()==null) {
			_receiverTransferItemVO.setPreviousExpiry(_currentDate);
		}

		try {
			_receiverTransferItemVO.setLmbdebitvalue((Long.valueOf((String)p_map.get("LMB_ALLOWED_VALUE"))));
		} catch(Exception e) {
			_log.error(METHOD_NAME, "Exception e"+e);
		};//@nu
	}

	/**
	 * This method sets the Interface Details based on the VOs values.
	 * If p_useInterfacePrefixVO is True then use p_MSISDNPrefixInterfaceMappingVO else use p_listValueVO to populate values
	 * @param p_prefixID
	 * @param p_userType
	 * @param p_listValueVO
	 * @param p_useInterfacePrefixVO
	 * @param p_MSISDNPrefixInterfaceMappingVO
	 * @throws BTSLBaseException
	 */
	private void setInterfaceDetails(long p_prefixID,String p_userType,ListValueVO p_listValueVO,boolean p_useInterfacePrefixVO,MSISDNPrefixInterfaceMappingVO p_MSISDNPrefixInterfaceMappingVO,ServiceSelectorInterfaceMappingVO p_serviceSelectorInterfaceMappingVO) throws BTSLBaseException {
		final String METHOD_NAME="setInterfaceDetails"; 
		if(_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME,_requestIDStr," Entered p_prefixID="+p_prefixID+" p_listValueVO="+p_listValueVO+" p_useInterfacePrefixVO="+p_useInterfacePrefixVO+" p_MSISDNPrefixInterfaceMappingVO="+p_MSISDNPrefixInterfaceMappingVO+"p_serviceSelectorInterfaceMappingVO"+p_serviceSelectorInterfaceMappingVO);
		}
		try {
			String interfaceID=null;
			String interfaceHandlerClass=null;
			String underProcessMsgReqd=null;
			String allServiceClassID=null;
			String externalID=null;
			String status=null;
			String message1=null;
			String message2=null;
			String interfaceStatusTy=null;
			if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue()&&p_serviceSelectorInterfaceMappingVO!=null) {
				interfaceID = p_serviceSelectorInterfaceMappingVO.getInterfaceID();
				interfaceHandlerClass= p_serviceSelectorInterfaceMappingVO.getHandlerClass();
				underProcessMsgReqd=p_serviceSelectorInterfaceMappingVO.getUnderProcessMsgRequired();
				allServiceClassID=p_serviceSelectorInterfaceMappingVO.getAllServiceClassID();
				externalID=p_serviceSelectorInterfaceMappingVO.getExternalID();
				status=p_serviceSelectorInterfaceMappingVO.getInterfaceStatus();
				message1=p_serviceSelectorInterfaceMappingVO.getLanguage1Message();
				message2=p_serviceSelectorInterfaceMappingVO.getLanguage2Message();
				interfaceStatusTy=p_serviceSelectorInterfaceMappingVO.getStatusType();
			}
			if(p_useInterfacePrefixVO&&p_serviceSelectorInterfaceMappingVO==null) {
				interfaceID = p_MSISDNPrefixInterfaceMappingVO.getInterfaceID();
				interfaceHandlerClass= p_MSISDNPrefixInterfaceMappingVO.getHandlerClass();
				underProcessMsgReqd=p_MSISDNPrefixInterfaceMappingVO.getUnderProcessMsgRequired();
				allServiceClassID=p_MSISDNPrefixInterfaceMappingVO.getAllServiceClassID();
				externalID=p_MSISDNPrefixInterfaceMappingVO.getExternalID();
				status=p_MSISDNPrefixInterfaceMappingVO.getInterfaceStatus();
				message1=p_MSISDNPrefixInterfaceMappingVO.getLanguage1Message();
				message2=p_MSISDNPrefixInterfaceMappingVO.getLanguage2Message();
				interfaceStatusTy=p_MSISDNPrefixInterfaceMappingVO.getStatusType();
			} else if(p_serviceSelectorInterfaceMappingVO==null) {
				interfaceID=p_listValueVO.getValue();
				interfaceHandlerClass=p_listValueVO.getLabel();
				allServiceClassID=p_listValueVO.getTypeName();
				externalID=p_listValueVO.getIDValue();
				underProcessMsgReqd=p_listValueVO.getType();
				status=p_listValueVO.getStatus();
				message1=p_listValueVO.getOtherInfo();
				message2=p_listValueVO.getOtherInfo2();
				interfaceStatusTy=p_listValueVO.getStatusType();
			}
			if(p_userType.equals(PretupsI.USER_TYPE_SENDER)) {
				_senderTransferItemVO.setPrefixID(p_prefixID);
				_senderTransferItemVO.setInterfaceID(interfaceID);
				_senderTransferItemVO.setInterfaceHandlerClass(interfaceHandlerClass);
				_senderAllServiceClassID=allServiceClassID;
				_senderExternalID=externalID;
				_senderInterfaceStatusType=interfaceStatusTy;
				_p2pTransferVO.setSenderAllServiceClassID(_senderAllServiceClassID);
				_p2pTransferVO.setSenderInterfaceStatusType(_senderInterfaceStatusType);
			} else if(p_userType.equals(PretupsI.USER_TYPE_RECEIVER)) {
				_receiverTransferItemVO.setPrefixID(p_prefixID);
				_receiverTransferItemVO.setInterfaceID(interfaceID);
				_receiverTransferItemVO.setInterfaceHandlerClass(interfaceHandlerClass);
				if(PretupsI.YES.equals(underProcessMsgReqd)) {
					_p2pTransferVO.setUnderProcessMsgReq(true);
				}
				_receiverAllServiceClassID=allServiceClassID;
				_receiverExternalID=externalID;
				_receiverInterfaceStatusType=interfaceStatusTy;
				_p2pTransferVO.setReceiverAllServiceClassID(_receiverAllServiceClassID);
				_p2pTransferVO.setReceiverInterfaceStatusType(_receiverInterfaceStatusType);
			}
			//Check if interface status is Active or not.
			if(!PretupsI.YES.equals(status)  && PretupsI.INTERFACE_STATUS_TYPE_MANUAL.equals(interfaceStatusTy)) {
				//ChangeID=LOCALEMASTER
				//which language message to be set is determined from the locale master table for the requested locale
				if(PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(_senderLocale)).getMessage())) {							
					_p2pTransferVO.setSenderReturnMessage(message1);
				} else { 
					_p2pTransferVO.setSenderReturnMessage(message2);
				}
				throw new BTSLBaseException(this,METHOD_NAME,PretupsErrorCodesI.INTERFACE_NOT_ACTIVE);
			}
		} catch(BTSLBaseException be) {
			_log.error(METHOD_NAME,"Getting Base Exception ="+be.getMessage());
			throw be;
		} catch(Exception e) {
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"IATPrepaidController[setInterfaceDetails]",_transferID,_senderMSISDN,_senderNetworkCode,"Exception:"+e.getMessage());
			throw new BTSLBaseException(this,METHOD_NAME,PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION);
		}
		finally {
			if(_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME,_requestIDStr," Exiting with Sender Interface ID="+_senderTransferItemVO.getInterfaceID()+" Receiver Interface="+_receiverTransferItemVO.getInterfaceID());
			}
		}
	}

	/**
	 * Method that will update the Subscriber Routing Details If interface is PRE
	 * @param p_userType
	 * @param p_networkCode
	 * @param p_interfaceID
	 * @param p_externalID
	 * @param p_msisdn
	 * @param p_interfaceCategory
	 * @param p_userID
	 * @param p_currentDate
	 * @throws BTSLBaseException
	 */
	private void updateSubscriberRoutingDetails(String p_userType,String p_networkCode,String p_interfaceID,String p_externalID,String p_msisdn,String p_interfaceCategory,String p_userID,Date p_currentDate) throws BTSLBaseException {
		final String METHOD_NAME="updateSubscriberRoutingDetails";
		if(_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME,_requestIDStr," Entered p_userType="+p_userType+" p_networkCode="+p_networkCode+" p_interfaceID="+p_interfaceID+" p_externalID="+p_externalID+" p_msisdn="+p_msisdn+" p_interfaceCategory="+p_interfaceCategory+" p_userID="+p_userID+" p_currentDate="+p_currentDate);
		}
		try {
			boolean updationReqd=false;
			if(PretupsI.USER_TYPE_SENDER.equals(p_userType)) {
				updationReqd=_senderDeletionReqFromSubRouting;
			} else {
				updationReqd=_receiverDeletionReqFromSubRouting;
			}
			if(updationReqd) {
				PretupsBL.updateSubscriberInterfaceRouting(p_interfaceID,p_externalID,p_msisdn,p_interfaceCategory,p_userID,p_currentDate);
			} else {
				SubscriberRoutingControlVO subscriberRoutingControlVO=SubscriberRoutingControlCache.getRoutingControlDetails(p_networkCode+"_"+_p2pTransferVO.getServiceType()+"_"+p_interfaceCategory);
				if(!updationReqd && subscriberRoutingControlVO!=null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
					PretupsBL.insertSubscriberInterfaceRouting(p_interfaceID,p_externalID,p_msisdn,p_interfaceCategory,p_userID,p_currentDate);
					if(PretupsI.USER_TYPE_SENDER.equals(p_userType)) {
						_senderInterfaceInfoInDBFound=true;
						_senderDeletionReqFromSubRouting=true;
					} else {
						_receiverInterfaceInfoInDBFound=true;
						_receiverDeletionReqFromSubRouting=true;						
					}
				}
			}
		} catch(BTSLBaseException be) {
			_log.error(METHOD_NAME,"Getting Base Exception ="+be.getMessage());
			throw be;
		} catch(Exception e) {
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"IATPrepaidController[updateSubscriberRoutingDetails]",_transferID,_senderMSISDN,_senderNetworkCode,"Exception:"+e.getMessage());
			throw new BTSLBaseException(this,METHOD_NAME,PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION);
		}
		finally {
			if(_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME,_requestIDStr," Exiting ");
			}
		}
	}

	/**
	 * Method to generate the Transfer ID 
	 * @param p_transferVO
	 * @throws BTSLBaseException
	 */
	public static synchronized void generateTransferID(TransferVO p_transferVO) throws BTSLBaseException {
		final String METHOD_NAME="generateTransferID";
		String transferID=null;
		Date mydate = null;
		String minut2Compare=null;
		try {
			mydate = new Date();
			p_transferVO.setCreatedOn(mydate);
			minut2Compare = _sdfCompare.format(mydate);
			int currentMinut=Integer.parseInt(minut2Compare);  		
			if(currentMinut !=_prevMinut) {
				_transactionIDCounter=1;
				_prevMinut=currentMinut;
			} else {
				_transactionIDCounter++;
			}
			if(_transactionIDCounter==0) {
				throw new BTSLBaseException(METHOD_NAME,METHOD_NAME,PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
			}
			transferID=_operatorUtil.formatIATP2PTransferID(p_transferVO,_transactionIDCounter);
			if(transferID==null) {
				throw new BTSLBaseException(METHOD_NAME,METHOD_NAME,PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
			}
			p_transferVO.setTransferID(transferID);			
		} catch(BTSLBaseException be) {						
			throw be;
		} catch(Exception e) {
			_log.error(METHOD_NAME, "Exception " + e.getMessage());
			_log.errorTrace(METHOD_NAME,e);
			throw new BTSLBaseException(METHOD_NAME,METHOD_NAME,PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
		}
	}

	public void handleLDCCRequest() throws BTSLBaseException {
		final String METHOD_NAME="handleLDCCRequest";
		Connection con=null;
		if(_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME,"Entered");
		}
		try {
			ListValueVO listValueVO=null;
			String requestStr=null;
			CommonClient commonClient=null;
			String senderValResponse=null;
			mcomCon = new MComConnection();con=mcomCon.getConnection();
			populateServicePaymentInterfaceDetails(con,PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION);
			_serviceInterfaceRoutingVO=ServiceInterfaceRoutingCache.getServiceInterfaceRoutingDetails(_senderVO.getNetworkCode()+"_"+_serviceType+"_"+_senderVO.getSubscriberType());
			_senderVO.setMsisdnPrefix(PretupsBL.getMSISDNPrefix(_senderVO.getMsisdn()));
			NetworkPrefixVO netPreVO=(NetworkPrefixVO)NetworkPrefixCache.getObject(_senderVO.getMsisdnPrefix(),_serviceInterfaceRoutingVO.getAlternateInterfaceType());
			NetworkInterfaceModuleVO networkInterfaceModuleVOS=(NetworkInterfaceModuleVO)NetworkInterfaceModuleCache.getObject(_p2pTransferVO.getModule(),_senderNetworkCode,_p2pTransferVO.getPaymentMethodType());
			LoadController.decreaseResponseCounters(_transferID,PretupsErrorCodesI.TXN_STATUS_FAIL,LoadControllerI.SENDER_VAL_RESPONSE);
			LoadController.decreaseTransactionInterfaceLoad(_transferID,_p2pTransferVO.getNetworkCode(),LoadControllerI.DEC_LAST_TRANS_COUNT);
			_senderTransferItemVO.setPrefixID(netPreVO.getPrefixID());
			_senderVO.setPrefixID(netPreVO.getPrefixID());
			MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null; 
			try {
				interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(_senderTransferItemVO.getPrefixID(), _serviceInterfaceRoutingVO.getAlternateInterfaceType(),PretupsI.INTERFACE_VALIDATE_ACTION);
				setInterfaceDetails(_senderTransferItemVO.getPrefixID(),PretupsI.USER_TYPE_SENDER,listValueVO,true,interfaceMappingVO,null);
			} catch(BTSLBaseException be) {
				throw be;
			}	
			checkTransactionLoad(PretupsI.USER_TYPE_SENDER,_senderTransferItemVO.getInterfaceID());
			//validate sender limits after Interface Validations
			SubscriberBL.validateSenderLimits(con,_p2pTransferVO,PretupsI.TRANS_STAGE_BEFORE_INVAL);
			if (mcomCon != null) {
				mcomCon.close("IATPrepaidController#handleLDCCRequest");
				mcomCon = null;
			}
			con=null;
			requestStr=getSenderValidateStr();
			commonClient=new CommonClient();
			LoadController.incrementTransactionInterCounts(_transferID,LoadControllerI.SENDER_UNDER_VAL);
			TransactionLog.log(_transferID,_requestIDStr,_senderMSISDN,_senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_TXNSTAGE_INVAL,requestStr,PretupsI.TXN_LOG_STATUS_SUCCESS,"");
			if(_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME,"Sending Request For MSISDN="+_senderMSISDN+" on ALternate Routing 1 to ="+_senderTransferItemVO.getInterfaceID());  
			}
			senderValResponse=commonClient.process(requestStr,_transferID,_intModCommunicationTypeS,_intModIPS,_intModPortS,_intModClassNameS);
			TransactionLog.log(_transferID,_requestIDStr,_senderMSISDN,_senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_INVAL,senderValResponse,PretupsI.TXN_LOG_STATUS_SUCCESS,"");
			try {
				senderValidateResponse(senderValResponse,1,0);
				if(InterfaceErrorCodesI.SUCCESS.equals(_senderTransferItemVO.getValidationStatus())) {
					_senderVO.setSubscriberType(_serviceInterfaceRoutingVO.getAlternateInterfaceType());
					_p2pTransferVO.setTransferCategory(_serviceInterfaceRoutingVO.getAlternateInterfaceType()+"-"+_type);
					_p2pTransferVO.setPaymentMethodType(_serviceInterfaceRoutingVO.getAlternateInterfaceType());
					_isUpdateRequired=true;
					_isSenderRoutingUpdate=true;
					_senderSubscriberType=_serviceInterfaceRoutingVO.getAlternateInterfaceType();
				}
				if(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(_senderTransferItemVO.getValidationStatus())) {
					_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
					throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
				}

			} catch(BTSLBaseException be) {
				_log.errorTrace(METHOD_NAME, be);
				throw be;
			} catch(Exception e) {
				_log.errorTrace(METHOD_NAME, e);
				throw new BTSLBaseException(this, METHOD_NAME, "");
			}
		} catch(BTSLBaseException be) {
			_log.errorTrace(METHOD_NAME, be);
			throw be;
		} catch(Exception e) {
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"IATPrepaidController[performSenderAlternateRouting]",_transferID,_senderMSISDN,_senderNetworkCode,"Exception:"+e.getMessage());
			throw new BTSLBaseException(this,METHOD_NAME,PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION);
		}
		finally {
			if (mcomCon != null) {
				mcomCon.close("IATPrepaidController#handleLDCCRequest");
				mcomCon = null;
			}
			con=null;
			if(_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME,"Exit");
			}
		}

	}

	/**
	 * Method that will update the Subscriber Routing Details If interface is PRE
	 * @param p_userType
	 * @param p_networkCode
	 * @param p_interfaceID
	 * @param p_externalID
	 * @param p_msisdn
	 * @param p_interfaceCategory
	 * @param p_userID
	 * @param p_currentDate
	 * @throws BTSLBaseException
	 */
	private void updateSubscriberAilternateRouting(String p_userType,String p_networkCode,String p_interfaceID,String p_externalID,String p_msisdn,String p_interfaceCategory,String p_userID,Date p_currentDate) throws BTSLBaseException {
		final String METHOD_NAME="updateSubscriberAilternateRouting";
		if(_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME,_requestIDStr," Entered p_userType="+p_userType+" p_networkCode="+p_networkCode+" p_interfaceID="+p_interfaceID+" p_externalID="+p_externalID+" p_msisdn="+p_msisdn+" p_interfaceCategory="+p_interfaceCategory+" p_userID="+p_userID+" p_currentDate="+p_currentDate);
		}
		try {
			boolean updationReqd=false;
			if(PretupsI.USER_TYPE_SENDER.equals(p_userType)) {
				updationReqd=_senderDeletionReqFromSubRouting;
			} else {
				updationReqd=_receiverDeletionReqFromSubRouting;
			}
			//if(updationReqd)
			try {
				PretupsBL.updateSubscriberInterfaceAilternateRouting(p_interfaceID,p_externalID,p_msisdn,p_interfaceCategory,p_userID,p_currentDate);
			} catch(BTSLBaseException e) {
				_log.error(METHOD_NAME, "BTSLBaseException " + e.getMessage());
				_log.errorTrace(METHOD_NAME,e);
				if(PretupsErrorCodesI.ERROR_EXCEPTION.equals(e.getMessage())) {
					PretupsBL.insertSubscriberInterfaceRouting(p_interfaceID,p_externalID,p_msisdn,p_interfaceCategory,p_userID,p_currentDate);
				}
			}
			_senderInterfaceInfoInDBFound=true;
			_senderDeletionReqFromSubRouting=true;
		} catch(BTSLBaseException be) {
			_log.error(METHOD_NAME,"Getting Base Exception ="+be.getMessage());
			throw be;
		} catch(Exception e) {
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"IATPrepaidController[updateSubscriberAilternateRouting]",_transferID,_senderMSISDN,_senderNetworkCode,"Exception:"+e.getMessage());
			throw new BTSLBaseException(this,METHOD_NAME,PretupsErrorCodesI.ICP2P_ERROR_EXCEPTION);
		}
		finally {
			if(_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME,_requestIDStr," Exiting ");
			}
		}
	}

	private void populateIATServiceDetails() throws BTSLBaseException {
		final String METHOD_NAME="populateIATServiceDetails";
		if(_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME,"Entered");
		}
		try {
			//get IAT code handler class and other values from cache and set in iat item vo.
			IATNetworkServiceMappingVO iatNetworkServiceMappingVO =(IATNetworkServiceMappingVO)IATNWServiceCache.getNetworkServiceObject(_iatTransferItemVO.getIatRecCountryShortName()+"_"+_iatTransferItemVO.getIatRecNWCode()+"_"+_p2pTransferVO.getServiceType());           
			if(iatNetworkServiceMappingVO==null) {
				IATDAO iatDAO = new IATDAO();
				if(iatNetworkServiceMappingVO!=null) {
					if(PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(_senderLocale)).getMessage())) {								
						_p2pTransferVO.setSenderReturnMessage(iatNetworkServiceMappingVO.getLanguage1Message());
					} else { 
						_p2pTransferVO.setSenderReturnMessage(iatNetworkServiceMappingVO.getLanguage2Message());
					}
					String[] strArr=new String[]{_iatTransferItemVO.getIatRcvrCountryCode()+_iatTransferItemVO.getIatRecMsisdn(),_p2pTransferVO.getServiceName(),_iatTransferItemVO.getIatRecCountryShortName(),_iatTransferItemVO.getIatRcvrCountryName(),_iatTransferItemVO.getIatRecNWCode()};
					throw new BTSLBaseException("IATRoamReachargeController",METHOD_NAME,PretupsErrorCodesI.IAT_NW_CNTRY_SERVICE_SUSPEND,0,strArr,null);
				}
				String[] strArr=new String[]{_iatTransferItemVO.getIatRcvrCountryCode()+_iatTransferItemVO.getIatRecMsisdn(),_p2pTransferVO.getServiceName(),_iatTransferItemVO.getIatRecCountryShortName(),_iatTransferItemVO.getIatRcvrCountryName(),_iatTransferItemVO.getIatRecNWCode()};
				throw new BTSLBaseException("IATRoamReachargeController",METHOD_NAME,PretupsErrorCodesI.IAT_NW_CNTRY_SERVICE_SUSPEND,0,strArr,null);
			}
			_iatTransferItemVO.setIatHandlerClass(iatNetworkServiceMappingVO.getHandlerClass());
			_iatTransferItemVO.setIatCode(iatNetworkServiceMappingVO.getIatCode());
			InterfaceVO interfaceVO =(InterfaceVO)NetworkInterfaceModuleCache.getObject(iatNetworkServiceMappingVO.getIatCode());
			if(!PretupsI.YES.equals(interfaceVO.getStatusCode())) {
				if(PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(_senderLocale)).getMessage())) {								
					_p2pTransferVO.setSenderReturnMessage(interfaceVO.getLanguage1Message());
				} else { 
					_p2pTransferVO.setSenderReturnMessage(interfaceVO.getLanguage2Message());
				}
				throw new BTSLBaseException(this,METHOD_NAME,PretupsErrorCodesI.INTERFACE_NOT_ACTIVE);
			}
			_iatTransferItemVO.setIatInterfaceType(iatNetworkServiceMappingVO.getInterfaceTypeID());
			_iatTransferItemVO.setServiceType(_serviceType);
			_iatTransferItemVO.setSenderId(_senderVO.getUserID());
			_receiverTransferItemVO.setInterfaceID(iatNetworkServiceMappingVO.getIatCode());
			_receiverTransferItemVO.setInterfaceType(iatNetworkServiceMappingVO.getInterfaceTypeID());
			//This is to set if under process message is required or not for sender.
			if("Y".equals(iatNetworkServiceMappingVO.getUnderProcessMsgReq())) {
				_p2pTransferVO.setUnderProcessMsgReq(true);	            
			}
		} catch(BTSLBaseException be) {
			throw be;
		} catch(Exception e) {
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"IATIntlRechargeController[populateIATServiceDetails]","",_receiverMSISDN,"","Exception while populating iat code and handler. Exception: "+e.getMessage());
			throw new BTSLBaseException(PretupsErrorCodesI.IAT_C2S_EXCEPTION);
		}
		finally {
			if(_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME,"Exited _iatTransferItemVO: "+_iatTransferItemVO);
			}
		}	    
	}

	/**
	 * Method to process the response of the receiver validation from IN
	 * @param str
	 * @throws BTSLBaseException
	 */
	public void updateForReceiverValidateResponse(IATInterfaceVO p_iatInterfaceVO) throws BTSLBaseException {
		final String METHOD_NAME="updateForReceiverValidateResponse";
		if(_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME,"Entered");
		}
		String[] strArr=null;
		String status=p_iatInterfaceVO.getIatINTransactionStatus();
		if (null != p_iatInterfaceVO.getIatStartTime())
			_requestVO.setValidationReceiverRequestSent(((Long.valueOf(p_iatInterfaceVO.getIatStartTime()).longValue())));
		if (null != p_iatInterfaceVO.getIatEndTime())		
			_requestVO.setValidationReceiverResponseReceived(((Long.valueOf(p_iatInterfaceVO.getIatEndTime()).longValue())));
		if(!InterfaceErrorCodesI.SUCCESS.equals(status)) {
			_senderTransferItemVO.setValidationStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
			_senderTransferItemVO.setTransferStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
			strArr=new String[]{_transferID,PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount())};
			_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			throw new BTSLBaseException("IATIntlRechargeController",METHOD_NAME,_p2pTransferVO.getErrorCode(),0,strArr,null);
		}
		_receiverTransferItemVO.setValidationStatus(status);
		_receiverTransferItemVO.setTransferStatus(status);
		_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);		
		_receiverTransferItemVO.setServiceClassCode(PretupsI.ALL);
		_receiverVO.setServiceClassCode(PretupsI.ALL);		
		_receiverVO.setInterfaceResponseCode(p_iatInterfaceVO.getIatResponseCodeVal());
		_receiverTransferItemVO.setPreviousExpiry(_currentDate);
		if(_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME,"Exited");
		}
	}
}