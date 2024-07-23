package com.client.pretups.channel.transfer.requesthandler;
/**
 * @(#)VoucherConsController.java
 * Copyright(c) 2005, Bharti Telesoft Int. Public Ltd.
 * All Rights Reserved
 *-------------------------------------------------------------------------------------------------
 * Author				Date			History
 *-------------------------------------------------------------------------------------------------
 * Vipan Kumar	   Feb 	12,2015		Initial Creation
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
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.util.InterfaceCloserI;
import com.btsl.pretups.interfaces.businesslogic.InterfaceVO;
import com.btsl.pretups.logging.OneLineTXNLog;
import com.btsl.pretups.logging.P2PRequestDailyLog;
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
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.processes.ResumeSuspendProcess;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.routing.master.businesslogic.InterfaceRoutingControlCache;
import com.btsl.pretups.routing.master.businesslogic.ServiceInterfaceRoutingCache;
import com.btsl.pretups.routing.master.businesslogic.ServiceInterfaceRoutingVO;
import com.btsl.pretups.routing.master.businesslogic.SubscriberRoutingControlCache;
import com.btsl.pretups.routing.master.businesslogic.SubscriberRoutingControlVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
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
import com.btsl.util.OracleUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;


public class VASVoucherConsController implements ServiceKeywordControllerI,Runnable{

	private static Log log = LogFactory.getLog(VASVoucherConsController.class.getName());
	private P2PTransferVO p2pTransferVO=null;
	private TransferItemVO senderTransferItemVO=null;
	private TransferItemVO receiverTransferItemVO=null;
	private String senderMSISDN;
	private String receiverMSISDN;
	private SenderVO senderVO;
	private ReceiverVO receiverVO;
	private String senderSubscriberType;
	private String senderNetworkCode;
	private Date currentDate=null;
	private String intModCommunicationTypeS;
	private String intModIPS;
	private int intModPortS;
	private String intModClassNameS;
	private String intModCommunicationTypeR;
	private String intModIPR;
	private int intModPortR;
	private String intModClassNameR;
	private String transferID;
	private String requestIDStr;
	private Locale senderLocale=null;
	private Locale receiverLocale=null;
	private boolean isCounterDecreased=false;
	private String type;
	private String serviceType;
	private boolean finalTransferStatusUpdate=true;
	private boolean decreaseTransactionCounts=false;
	private boolean transferDetailAdded=false;
	private boolean senderInterfaceInfoInDBFound=false;
	private boolean receiverInterfaceInfoInDBFound=false;
	private String senderAllServiceClassID=PretupsI.ALL;
	private String receiverAllServiceClassID=PretupsI.ALL;
	private String receiverExternalID=null;
	private RequestVO requestVO=null;
	private boolean processedFromQueue=false; //Flag to indicate that request has been processed from Queue
	private boolean recValidationFailMessageRequired=false; //Whether Receiver Fail Message is required before validation
	private boolean recTopupFailMessageRequired=false;//Whether Receiver Fail Message is required before topup
	private boolean recTopupSuccMessageRequired=false;//Whether Receiver Fail Message is required before topup
	private ServiceInterfaceRoutingVO serviceInterfaceRoutingVO=null;
	private boolean useAlternateCategory=false; //Whether to use alternate interface category
	private boolean performIntfceCatRoutingBeforeVal=false; //Whether we need to perform alternate interface category routing before sending Receiver Validation Request
	private boolean interfaceCatRoutingDone=false; //To indicate that interface category routing has been done for the process
	private String oldInterfaceCategory=null; //The initial interface category that has to be used
	private String newInterfaceCategory=null; //The alternate interface category that has to be used
	private boolean senderDeletionReqFromSubRouting=false; //Whether to update in Subscriber Routing for sender MSISDN
	private boolean receiverDeletionReqFromSubRouting=false; //Whether to update in Subscriber Routing for Reciever MSISDN
	private final static int SRC_BEFORE_INRESP_CAT_ROUTING=1; //To denote the process from where interface routing has been called, Before IN Validation of Receiver
	private final static int SRC_AFTER_INRESP_CAT_ROUTING=2; //To denote the process from where interface routing has been called, After IN Validation of Receiver
	private NetworkPrefixVO networkPrefixVO=null;
	private String oldDefaultSelector=null;
	private String newDefaultSelector=null;
	private static OperatorUtilI operatorUtil=null;
	private String senderInterfaceStatusType=null;
	private static int transactionIDCounter=0;
	private static int  prevMinut=0;
	
	//to update the P2P_Subscriber if subscriber found on Alternate Interface.
	private boolean oneLog=true;
	private VomsVoucherVO vomsVO=null;
	private boolean vomsInterfaceInfoInDBFound=false;
	private String vomsExternalID=null;
	private HashMap _requestMap = null;
    public  VASVoucherConsController()
	{
		p2pTransferVO=new P2PTransferVO();
		currentDate=new Date();
		if(BTSLUtil.NullToString(Constants.getProperty("VMS_REC_GEN_FAIL_MSG_REQD_V")).equals("Y"))
		{
			recValidationFailMessageRequired=true;
		}
		if(BTSLUtil.NullToString(Constants.getProperty("VMS_REC_GEN_FAIL_MSG_REQD_T")).equals("Y"))
		{
			recTopupFailMessageRequired=true;
		}
		if(BTSLUtil.NullToString(Constants.getProperty("VMS_REC_GEN_SUCC_MSG_REQD_T")).equals("Y"))
		{
			recTopupSuccMessageRequired=true;
		}
	}
	//Loads operator specific class
	static
	{
		String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
		try
		{
			operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
		}
		catch(Exception e)
		{
			log.errorTrace("VASVoucherConsController", e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VASVoucherConsController[initialize]","","","","Exception while loading the class at the call:"+e.getMessage());
		}
	}	
	
	public void process(RequestVO p_requestVO)
	{
		Connection con=null;MComConnectionI mcomCon = null;
		requestIDStr=p_requestVO.getRequestIDStr();
		boolean receiverMessageSendReq=false;
		final String methodName = "process";
		if(log.isDebugEnabled()) {
			log.debug(methodName,requestIDStr,"Entered");
		}
		try
		{
			requestVO=p_requestVO;
			senderVO=(SenderVO)p_requestVO.getSenderVO();
			 _requestMap = requestVO.getRequestMap();
			//If user is not already registered then register the user with status as NEW and Default PIN
			if(senderVO==null)
			{
				p_requestVO.setInfo10("VMS");
				new RegisterationController().regsiterNewUser(p_requestVO);
				senderVO=(SenderVO)p_requestVO.getSenderVO();
				senderVO.setDefUserRegistration(true);
				senderVO.setStatus(PretupsI.USER_STATUS_ACTIVE);
				p_requestVO.setSenderLocale(new Locale(senderVO.getLanguage(),senderVO.getCountry()));	
			}

			senderLocale=p_requestVO.getSenderLocale();
			receiverLocale=p_requestVO.getReceiverLocale();

			if(log.isDebugEnabled()) {
				log.debug(methodName,requestIDStr,"senderLocale="+senderLocale+" receiverLocale="+receiverLocale);
			}

			TransactionLog.log("",p_requestVO.getRequestIDStr(),p_requestVO.getFilteredMSISDN(),senderVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_TXNSTAGE_RECIVED,"Received Request From Receiver",PretupsI.TXN_LOG_STATUS_SUCCESS,"");

			
			type=p_requestVO.getType();

			serviceType=p_requestVO.getServiceType();

			populateVOFromRequest(p_requestVO);

			mcomCon = new MComConnection();con=mcomCon.getConnection();
			senderVO.setModifiedBy(senderVO.getUserID());
			senderVO.setModifiedOn(currentDate);

			operatorUtil.validateVoucherPin(con,p_requestVO,p2pTransferVO);

			receiverLocale=p_requestVO.getReceiverLocale();

			receiverVO=(ReceiverVO)p2pTransferVO.getReceiverVO();

			receiverVO.setMsisdnPrefix(PretupsBL.getMSISDNPrefix(receiverVO.getMsisdn()));

			// for ussd
			p2pTransferVO.setCellId(requestVO.getCellId());
			p2pTransferVO.setSwitchId(requestVO.getSwitchId());

			p2pTransferVO.setVoucherCode(requestVO.getVoucherCode());
			p2pTransferVO.setSerialNumber(requestVO.getSerialNo());


			//Get the Interface Category routing details based on the receiver Network Code and Service type 

			serviceInterfaceRoutingVO=ServiceInterfaceRoutingCache.getServiceInterfaceRoutingDetails(receiverVO.getNetworkCode()+"_"+p_requestVO.getServiceType()+"_"+senderVO.getSubscriberType());
			if (serviceInterfaceRoutingVO!=null)
			{
				if(log.isDebugEnabled()) {
					log.debug(methodName,requestIDStr,"For ="+receiverVO.getNetworkCode()+"_"+p_requestVO.getServiceType()+" Got Interface Category="+serviceInterfaceRoutingVO.getInterfaceType()+" Alternate Check Required="+serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool()+" Alternate Interface="+serviceInterfaceRoutingVO.getAlternateInterfaceType()+" oldDefaultSelector="+serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode()+"newDefaultSelector= "+serviceInterfaceRoutingVO.getAlternateDefaultSelectortCode());
				}
				oldDefaultSelector=serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode();
			}
			else
			{
				//oldDefaultSelector=String.valueOf(SystemPreferences.P2P_TRANSFER_DEF_SELECTOR_CODE);
				//Changed on 27/05/07 for Service Type selector Mapping
				ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p2pTransferVO.getServiceType());
				if(serviceSelectorMappingVO!=null) {
					oldDefaultSelector=serviceSelectorMappingVO.getSelectorCode();
				}
				log.info(methodName,requestIDStr,"Service Interface Routing control Not defined, thus using default Selector="+oldDefaultSelector);
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,"VASVoucherConsController[process]","",senderMSISDN,senderNetworkCode,"Service Interface Routing control Not defined, thus using default selector="+oldDefaultSelector);
			}

			//If the interface category does not match with the Receiver subscriber type got from validation from
			//network prefixes then load the new prefix ID against the interface category
			//If not found then check whether Alternate has to be used or not , if yes then use the old prefix ID
			//already loaded and set the useAlternateCategory=false denoting that do not perform alternate interface
			//category routing again, If Not required then give the error
			if(!receiverVO.getSubscriberType().equals(type))
			{
				networkPrefixVO=(NetworkPrefixVO)NetworkPrefixCache.getObject(receiverVO.getMsisdnPrefix(),type);
				if(networkPrefixVO!=null)
				{
					PretupsBL.checkNumberPortability(con,requestIDStr,receiverVO.getMsisdn(),networkPrefixVO);
					receiverVO.setNetworkCode(networkPrefixVO.getNetworkCode());
					receiverVO.setPrefixID(networkPrefixVO.getPrefixID());
					receiverVO.setSubscriberType(networkPrefixVO.getSeriesType());
					//In P2P both sender and receiver are from the same network
					senderVO.setNetworkCode(networkPrefixVO.getNetworkCode());
				}
				else if(useAlternateCategory)
				{
					if(log.isDebugEnabled()) {
						log.debug(methodName,requestIDStr,"Network Prefix Not Found For Series="+receiverVO.getMsisdnPrefix()+" and Type="+type+" and thus using Type as ="+newInterfaceCategory+" useAlternateCategory was true");
					}
					useAlternateCategory=false;
					type=newInterfaceCategory;
					oldDefaultSelector=newDefaultSelector;
					interfaceCatRoutingDone=true;					
				}
				else
				{
					//Refuse the Request
					log.error(this,"Series ="+receiverVO.getMsisdnPrefix()+" Not Defined for Series type="+type);
					EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,"VASVoucherConsController[process]","","","","Series ="+receiverVO.getMsisdnPrefix()+" Not Defined for Series type="+type+" But alternate Category Routing was required on interface" );
					throw new BTSLBaseException("",methodName,PretupsErrorCodesI.ERROR_NOTFOUND_SERIES_TYPE,0,new String[]{receiverVO.getMsisdn()},null);
				}
			}
			else
			{
				networkPrefixVO=(NetworkPrefixVO)NetworkPrefixCache.getObject(receiverVO.getMsisdnPrefix(),type);
				PretupsBL.checkNumberPortability(con,requestIDStr,receiverVO.getMsisdn(),networkPrefixVO);
			}

			//changed for CRE_INT_CR00029 by ankit Zindal
			if(BTSLUtil.isNullString(p_requestVO.getReqSelector()))
			{
				if(log.isDebugEnabled()) {
					log.debug(methodName,requestIDStr,"Selector Not found in Incoming Message Thus using Selector as  "+oldDefaultSelector);
				}
				p_requestVO.setReqSelector(oldDefaultSelector);
			}
			else
			{
				newDefaultSelector=p_requestVO.getReqSelector();
			}

			if(log.isDebugEnabled()) {
				log.debug(methodName,requestIDStr,"receiverVO:"+receiverVO);
			}

			//check service payment mapping
			senderSubscriberType=senderVO.getSubscriberType();
			//By Default Entry, will be overridden later in the file
			p2pTransferVO.setTransferCategory(senderSubscriberType+"-"+type);
			 if(log.isDebugEnabled()) {
			    	log.debug(methodName,requestIDStr,"Starting with transfer Category as :"+p2pTransferVO.getTransferCategory());
			}

			senderNetworkCode=senderVO.getNetworkCode();
			senderMSISDN=((SubscriberVO)p2pTransferVO.getSenderVO()).getMsisdn();
			receiverMSISDN=((SubscriberVO)p2pTransferVO.getReceiverVO()).getMsisdn();
			receiverVO.setModule(p2pTransferVO.getModule());
			receiverVO.setCreatedDate(currentDate);
			receiverVO.setLastTransferOn(currentDate);
			p2pTransferVO.setReceiverMsisdn(receiverMSISDN);
			p2pTransferVO.setReceiverNetworkCode(receiverVO.getNetworkCode());			
			p2pTransferVO.setSubService(p_requestVO.getReqSelector());
			p2pTransferVO.setRequestStartTime(p_requestVO.getRequestStartTime());
			//Validates the network service status
			PretupsBL.validateNetworkService(p2pTransferVO);
			
			//chect  barred
			try
			{
				PretupsBL.checkMSISDNBarred(con,receiverVO.getMsisdn(),receiverVO.getNetworkCode(),p2pTransferVO.getModule(),PretupsI.USER_TYPE_RECEIVER);
				//check Black list restricted subscribers not allowed for recharge or for CP2P services.
				if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CHK_BLK_LST_STAT))).booleanValue()) {
					operatorUtil.isRestrictedSubscriberAllowed(con,receiverVO.getMsisdn(),senderMSISDN);
				}

			}
			catch(BTSLBaseException be)
			{
				if(be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.ERROR_RECEIVER_USERBARRED))) {
					p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.ERROR_USERBARRED_R,new String[]{}));
				}
				throw be;
			}	


			PretupsBL.loadRecieverControlLimits(con,p_requestVO.getRequestIDStr(),p2pTransferVO);
			receiverVO.setUnmarkRequestStatus(true);
			try {con.commit();} catch(Exception e){ log.errorTrace(methodName,e);throw new BTSLBaseException("VASVoucherConsController",methodName,PretupsErrorCodesI.P2P_ERROR_EXCEPTION);}

			//check subscriber details for skey requirement
			if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SKEY_REQUIRED)).booleanValue()&&senderVO.getSkeyRequired().equals(PretupsI.YES))
			{
				//Call the method to handle SKey related transfers
				processSKeyGen(con);
			}
			else
			{
				processTransfer(con);
				p_requestVO.setTransactionID(transferID);
				receiverVO.setLastTransferID(transferID);
				TransactionLog.log(transferID,p_requestVO.getRequestIDStr(),p_requestVO.getFilteredMSISDN(),senderVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Generated Transfer ID",PretupsI.TXN_LOG_STATUS_SUCCESS,"Source Type="+p2pTransferVO.getSourceType()+" Gateway Code="+p2pTransferVO.getRequestGatewayCode());

				//populate payment and service interface details for validate action
				populateServicePaymentInterfaceDetails(con,PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION);

				p2pTransferVO.setTransferCategory(senderSubscriberType+"-"+type);

				p2pTransferVO.setSenderAllServiceClassID(senderAllServiceClassID);
				p2pTransferVO.setReceiverAllServiceClassID(receiverAllServiceClassID);

				//validate sender limits before Interface Validations
				SubscriberBL.validateSenderLimits(con,p2pTransferVO,PretupsI.TRANS_STAGE_BEFORE_INVAL);
				//Change is done for ID=SUBTYPVALRECLMT
				//This chenge is done to set the receiver subscriber type in transfer VO
				//This will be used in validate ReceiverLimit method of PretupsBL when receiverTransferItemVO is null
				p2pTransferVO.setReceiverSubscriberType(receiverTransferItemVO.getInterfaceType());

				//validate receiver limits before Interface Validations
				PretupsBL.validateRecieverLimits(con,p2pTransferVO,PretupsI.TRANS_STAGE_BEFORE_INVAL,PretupsI.P2P_MODULE);

				try {con.commit();} catch(Exception e){ log.errorTrace(methodName,e);throw new BTSLBaseException("VASVoucherConsController",methodName,PretupsErrorCodesI.P2P_ERROR_EXCEPTION);}
				try {con.close();} catch(Exception e) { log.errorTrace(methodName,e);throw new BTSLBaseException("VASVoucherConsController",methodName,PretupsErrorCodesI.P2P_ERROR_EXCEPTION);}
				con=null;

				//Checks the Various loads
				checkTransactionLoad();

				decreaseTransactionCounts=true;

				//Checks If flow type is common then validation will be performed before sending the 
				//response to user and if it is thread based then validation will also be performed in thread 
				//along with topup
				if(p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON))
				{
					//Process validation requests
					processValidationRequest();
					p_requestVO.setSenderMessageRequired(p2pTransferVO.isUnderProcessMsgReq());
					p_requestVO.setSenderReturnMessage(getSenderUnderProcessMessage());
					p_requestVO.setMessageCode(PretupsI.TXN_STATUS_UNDER_PROCESS);
					p_requestVO.setDecreaseLoadCounters(false);
				}
				else if(p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD))
				{
					//Check if message needs to be sent in case of Thread implmentation
					p_requestVO.setSenderReturnMessage(getSndrUPMsgBeforeValidation());
					p_requestVO.setMessageCode(PretupsI.TXN_STATUS_UNDER_PROCESS);
					Thread _controllerThread=new Thread(this);
					_controllerThread.start();
					oneLog = false;
					p_requestVO.setDecreaseLoadCounters(false);
				}
				else if(p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_REQUEST))
				{
					p_requestVO.setMessageCode(PretupsI.TXN_STATUS_UNDER_PROCESS);
					processValidationRequest();		
					oneLog = false;
					processThread();
					String[] messageArgArray={receiverMSISDN,transferID,Long.toString(p2pTransferVO.getTransferValue()),p2pTransferVO.getVoucherCode(),p2pTransferVO.getVasServiceName()};
					p_requestVO.setMessageArguments(messageArgArray);
				}

				//Parameter set to indicate that instance counters will not be decreased in receiver for this transaction 
				p_requestVO.setDecreaseLoadCounters(false);
			}
		}
		catch(BTSLBaseException be)
		{	
			receiverMessageSendReq=true;
			log.errorTrace(methodName,be);
			log.error(methodName,"Exception be:"+be.getMessage());
			//be.printStackTrace();
			requestVO.setSuccessTxn(false);
			//Unmarking Receiver last request status
				try
				{
				if(mcomCon==null) {
					mcomCon = new MComConnection();con=mcomCon.getConnection();
				}	
					if(receiverVO!=null && receiverVO.isUnmarkRequestStatus())
					{
						PretupsBL.unmarkReceiverLastRequest(con,p_requestVO.getRequestIDStr(),receiverVO);
					}
				}
				catch(BTSLBaseException bex)
				{
					log.errorTrace(methodName, bex);
					//p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);				
					EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"VASVoucherConsController[process]",transferID,senderMSISDN,senderNetworkCode,"Leaving Reciever Unmarked Base Exception:"+bex.getMessage());
				}
				catch(Exception e)
				{
					log.errorTrace(methodName, e);
					EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"VASVoucherConsController[process]",transferID,senderMSISDN,senderNetworkCode,"Leaving Reciever Unmarked Exception:"+e.getMessage());
					p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
				}	
		
			p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			if(recValidationFailMessageRequired)
			{
				if(p2pTransferVO.getReceiverReturnMsg()==null || !((BTSLMessages)p2pTransferVO.getReceiverReturnMsg()).isKey())
				{
					if(transferID!=null) {
						p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.P2P_RECEIVER_FAIL,new String[]{String.valueOf(transferID),PretupsBL.getDisplayAmount(p2pTransferVO.getRequestedAmount())}));
					} else {
						p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.P2P_FAIL_R,new String[]{PretupsBL.getDisplayAmount(p2pTransferVO.getRequestedAmount())}));
					}
				}
			}

			if(!BTSLUtil.isNullString(p2pTransferVO.getSenderReturnMessage())) {
				p_requestVO.setSenderReturnMessage(p2pTransferVO.getSenderReturnMessage());
			}		

			if(be.isKey())
			{
				if(BTSLUtil.isNullString(p2pTransferVO.getErrorCode())) {
					p2pTransferVO.setErrorCode(be.getMessageKey());
				}
				p_requestVO.setMessageCode(be.getMessageKey());
				p_requestVO.setMessageArguments(be.getArgs());
			} else {
				p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			}
			if(transferID!=null && decreaseTransactionCounts)
			{
				LoadController.decreaseTransactionLoad(transferID,senderNetworkCode,LoadControllerI.DEC_LAST_TRANS_COUNT);
				isCounterDecreased=true;
			}
			TransactionLog.log(transferID,requestIDStr,p_requestVO.getFilteredMSISDN(),"",PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,p2pTransferVO.getSenderReturnMessage(),PretupsI.TXN_LOG_STATUS_FAIL,"Getting Code="+p_requestVO.getMessageCode());
//			Populate the P2PRequestDailyLog and log
			P2PRequestDailyLog.log(P2PRequestDailyLog.populateP2PRequestDailyLogVO(requestVO, p2pTransferVO));		
		}
		catch(Exception e)
		{
			receiverMessageSendReq=true;
			log.error(methodName,"Exception e:"+e.getMessage());
			log.errorTrace(methodName,e);
			requestVO.setSuccessTxn(false);
			
			try
			{
				if(mcomCon==null) {
					mcomCon = new MComConnection();con=mcomCon.getConnection();
				}
				if(receiverVO!=null && receiverVO.isUnmarkRequestStatus())
				{
					PretupsBL.unmarkReceiverLastRequest(con,p_requestVO.getRequestIDStr(),receiverVO);
				}
			}
			catch(BTSLBaseException bex)
			{
				log.errorTrace(methodName, bex);
				p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);				
			}
			catch(Exception ex1)
			{
				log.errorTrace(methodName, ex1);
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"VASVoucherConsController[process]",transferID,senderMSISDN,senderNetworkCode,"Exception:"+ex1.getMessage());
				p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			}	

			if(recValidationFailMessageRequired)
			{
				if(p2pTransferVO.getReceiverReturnMsg()==null || !((BTSLMessages)p2pTransferVO.getReceiverReturnMsg()).isKey())
				{
					if(transferID!=null) {
						p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.P2P_RECEIVER_FAIL,new String[]{String.valueOf(transferID),PretupsBL.getDisplayAmount(p2pTransferVO.getRequestedAmount())}));
					} else {
						p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.P2P_FAIL_R,new String[]{PretupsBL.getDisplayAmount(p2pTransferVO.getRequestedAmount())}));
					}
				}
			}

			p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			log.error(methodName,"Exception:"+e.getMessage());
			log.errorTrace(methodName,e);
			if(transferID!=null && decreaseTransactionCounts)
			{
				isCounterDecreased=true;
				LoadController.decreaseTransactionLoad(transferID,senderNetworkCode,LoadControllerI.DEC_LAST_TRANS_COUNT);
			}
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VASVoucherConsController[process]",transferID,senderMSISDN,senderNetworkCode,"Exception:"+e.getMessage());
			TransactionLog.log(transferID,requestIDStr,p_requestVO.getFilteredMSISDN(),"",PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,p2pTransferVO.getSenderReturnMessage(),PretupsI.TXN_LOG_STATUS_FAIL,"Getting Code="+p_requestVO.getMessageCode());
//			Populate the P2PRequestDailyLog and log
			P2PRequestDailyLog.log(P2PRequestDailyLog.populateP2PRequestDailyLogVO(requestVO, p2pTransferVO));			
		}
		finally
		{
			requestVO.setRequestMap(_requestMap);
			p_requestVO = requestVO;
			try
			{
				if(mcomCon==null) {
					mcomCon = new MComConnection();con=mcomCon.getConnection();
				}
				if(transferID!=null && !transferDetailAdded && (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON) || (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD) && !p_requestVO.getMessageCode().equals(PretupsI.TXN_STATUS_UNDER_PROCESS)))) {
					addEntryInTransfers(con);
				} else if(transferID!=null && p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD)) {
					log.info(methodName,requestIDStr,"Send the message to MSISDN="+p_requestVO.getFilteredMSISDN()+" Transfer ID="+transferID+" But not added entry in Transfers yet");
				}
			}
			catch(BTSLBaseException be)
			{
				log.errorTrace(methodName,be);
				log.error(methodName,"BTSL Base Exception:"+be.getMessage());
			}
			catch(Exception e)
			{
				log.errorTrace(methodName,e);
				log.error(methodName,"Exception:"+e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"VASVoucherConsController[process]",transferID,senderMSISDN,senderNetworkCode,"Exception:"+e.getMessage());
			}
			if(BTSLUtil.isNullString(p_requestVO.getMessageCode())) {
				p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
			}
			if(isCounterDecreased) {
				p_requestVO.setDecreaseLoadCounters(false);
			}
			if(con!=null)
			{
				try {con.commit();} catch(Exception e){log.errorTrace(methodName,e);};
				try{con.close();}catch(Exception e){log.errorTrace(methodName,e);}
				con=null;
			}
			if(receiverMessageSendReq)
			{
				if(p2pTransferVO.getReceiverReturnMsg()!=null&&((BTSLMessages)p2pTransferVO.getReceiverReturnMsg()).isKey())
				{
					BTSLMessages btslRecMessages=(BTSLMessages)p2pTransferVO.getReceiverReturnMsg();
					(new PushMessage(receiverMSISDN,BTSLUtil.getMessage(receiverLocale,btslRecMessages.getMessageKey(),btslRecMessages.getArgs()),transferID,p2pTransferVO.getRequestGatewayCode(),receiverLocale)).push();
				}
				else if(p2pTransferVO.getReceiverReturnMsg()!=null) {
					(new PushMessage(receiverMSISDN,(String)p2pTransferVO.getReceiverReturnMsg(),transferID,p2pTransferVO.getRequestGatewayCode(),receiverLocale)).push();
				}
			}
			if(oneLog) {
				///		OneLineTXNLog.log(p2pTransferVO,senderTransferItemVO,receiverTransferItemVO);
			}
			TransactionLog.log(transferID,p_requestVO.getRequestIDStr(),p_requestVO.getFilteredMSISDN(),"",PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Leaving the controller",PretupsI.TXN_LOG_STATUS_SUCCESS,"Getting Code="+p_requestVO.getMessageCode());
			if(log.isDebugEnabled()) {
				log.debug(methodName,"Exiting");
			}
		}
	}

	/**
	 * This method process the S Key based transactions
	 * @param p_con
	 * @throws BTSLBaseException
	 * @throws Exception
	 */
	private void processSKeyGen(Connection p_con) throws BTSLBaseException,Exception
	{
		String methodName="processSKeyGen";
		if(log.isDebugEnabled()) {
			log.debug("processSKeyGen","Entered");
		}
		try
		{
			//validate skey details for generation
			//generate skey
			PretupsBL.generateSKey(p_con,p2pTransferVO);
		}
		catch(BTSLBaseException be)
		{
			throw be;
		}
		catch(Exception e)
		{
			log.error(methodName,"Exception e:"+e.getMessage());
			log.errorTrace(methodName,e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VASVoucherConsController[processSKeyGen]",transferID,senderMSISDN,senderNetworkCode,"Exception:"+e.getMessage());
			throw new BTSLBaseException("VASVoucherConsController","processSKeyGen",PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
		}
		finally
		{
			if(log.isDebugEnabled()) {
				log.debug(methodName,"Exiting");
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
		Connection con=null;MComConnectionI mcomCon = null;
		final String methodName = "processValidationRequest";
		if(log.isDebugEnabled()) {
			log.debug(methodName,"Entered and performing validations for transfer ID="+transferID+ " "+p2pTransferVO.getModule()+" "+p2pTransferVO.getReceiverNetworkCode()+" "+type);
		}  
		ArrayList itemList=null;
		NetworkInterfaceModuleVO networkInterfaceModuleVO=null;
		try
		{
			itemList=new ArrayList();
			itemList.add(senderTransferItemVO);
			itemList.add(receiverTransferItemVO);
			p2pTransferVO.setTransferItemList(itemList);			
			networkInterfaceModuleVO=(NetworkInterfaceModuleVO)NetworkInterfaceModuleCache.getObject(p2pTransferVO.getModule(),p2pTransferVO.getReceiverNetworkCode(),type);
			
			intModCommunicationTypeR=networkInterfaceModuleVO.getCommunicationType();
			intModIPR=networkInterfaceModuleVO.getIP();
			intModPortR=networkInterfaceModuleVO.getPort();
			intModClassNameR=networkInterfaceModuleVO.getClassName();
			
			String requestStr=getReceiverValidateStr();
			
			LoadController.incrementTransactionInterCounts(transferID,LoadControllerI.SENDER_UNDER_VAL);
			
			CommonClient commonClient=new CommonClient();

			TransactionLog.log(transferID,requestIDStr,receiverMSISDN,receiverVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_TXNSTAGE_INVAL,requestStr,PretupsI.TXN_LOG_STATUS_SUCCESS,"");
			String receiverValResponse=commonClient.process(requestStr,transferID,networkInterfaceModuleVO.getCommunicationType(),networkInterfaceModuleVO.getIP(),networkInterfaceModuleVO.getPort(),networkInterfaceModuleVO.getClassName());
			TransactionLog.log(transferID,requestIDStr,receiverMSISDN,receiverVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_INVAL,receiverValResponse,PretupsI.TXN_LOG_STATUS_SUCCESS,"");

			if(log.isDebugEnabled()) {
				log.debug(methodName,transferID,"Got the validation response from VOMS Handler receiverValResponse="+receiverValResponse);
			}
			itemList=new ArrayList();
			itemList.add(senderTransferItemVO);
			itemList.add(receiverTransferItemVO);
			p2pTransferVO.setTransferItemList(itemList);

			try
			{
				updateForVOMSValidationResponse(receiverValResponse);
			}
			catch(BTSLBaseException be)
			{
				LoadController.decreaseResponseCounters(transferID,receiverVO.getTransactionStatus(),LoadControllerI.SENDER_VAL_RESPONSE);
				if(log.isDebugEnabled()) {
					log.debug(methodName,"inside catch of BTSL Base Exception: "+be.getMessage()+" vomsInterfaceInfoInDBFound: "+vomsInterfaceInfoInDBFound);
				}
				if(vomsInterfaceInfoInDBFound && senderTransferItemVO.getValidationStatus().equals(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND)) {
					PretupsBL.deleteSubscriberInterfaceRouting(receiverMSISDN,PretupsI.INTERFACE_CATEGORY_VOMS);
				}
				PretupsBL.validateRecieverLimits(null,p2pTransferVO,PretupsI.TRANS_STAGE_AFTER_INVAL,PretupsI.P2P_MODULE);
			throw new BTSLBaseException(be);
			}
			catch(Exception e)
			{
				LoadController.decreaseResponseCounters(transferID,receiverVO.getTransactionStatus(),LoadControllerI.SENDER_VAL_RESPONSE);
				PretupsBL.validateRecieverLimits(null,p2pTransferVO,PretupsI.TRANS_STAGE_AFTER_INVAL,PretupsI.P2P_MODULE);
				throw new BTSLBaseException(e);
			}
		
			LoadController.decreaseResponseCounters(transferID,receiverVO.getTransactionStatus(),LoadControllerI.SENDER_VAL_RESPONSE);

			//If request is taking more time till validation of subscriber than reject the request.
			InterfaceVO vomsInterfaceVO=(InterfaceVO)NetworkInterfaceModuleCache.getObject(receiverTransferItemVO.getInterfaceID());
			if((System.currentTimeMillis()-p2pTransferVO.getRequestStartTime())>vomsInterfaceVO.getValExpiryTime())
			{
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,"EVDController[processValidationRequest]",transferID,senderMSISDN,senderNetworkCode,"Exception: System is taking more time till validation of voucher");
				throw new BTSLBaseException("EVDController",methodName,PretupsErrorCodesI.C2S_ERROR_EXCEPTION_TKING_TIME_TILL_VAL);
			}
			vomsInterfaceVO=null;

			//This method will set various values into items and transferVO
			if(vomsVO!=null)
			calulateTransferValue(p2pTransferVO,vomsVO);

			TransactionLog.log(transferID,requestIDStr,receiverMSISDN,receiverVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Validity="+p2pTransferVO.getReceiverValidity()+" Talk Time="+p2pTransferVO.getReceiverTransferValue(),PretupsI.TXN_LOG_STATUS_SUCCESS,"");

			PretupsBL.validateRecieverLimits(null,p2pTransferVO,PretupsI.TRANS_STAGE_AFTER_INVAL,PretupsI.P2P_MODULE);
	
			
			p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
			senderTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);

			mcomCon = new MComConnection();con=mcomCon.getConnection();
			populateServicePaymentInterfaceDetails(con,PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);

			//senderTransferItemVO.setServiceClass(_vomsAllServiceClassID);
			//Method to insert the record in c2s transfer table
			//added by nilesh: consolidated for logger
			if(!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue())
			{
				PretupsBL.addVoucherTransferDetails(con,p2pTransferVO);
			}
			
			transferDetailAdded=true;
			//Commit the transaction and relaease the locks
			try { con.commit(); } catch(Exception be) {log.errorTrace(methodName, be);}
			try { con.close(); } catch(Exception be) {log.errorTrace(methodName, be);}
			con=null;

			TransactionLog.log(transferID,requestIDStr,receiverMSISDN,receiverVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Marked Under process,",PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"" );

			if(p2pTransferVO.getMsgGatewayFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON) || processedFromQueue)
			{
				//create new Thread
				Thread _controllerThread=new Thread(this);
				_controllerThread.start();
				oneLog = false;
			}
		}
		catch(BTSLBaseException be)
		{
			requestVO.setSuccessTxn(false);
			if(con!=null) {
				con.rollback();
			}con=null;

			if(recValidationFailMessageRequired)
			{
				if(p2pTransferVO.getReceiverReturnMsg()==null || !((BTSLMessages)p2pTransferVO.getReceiverReturnMsg()).isKey()) {
					p2pTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.C2S_RECEIVER_FAIL),new String[]{String.valueOf(transferID),PretupsBL.getDisplayAmount(p2pTransferVO.getRequestedAmount())}));
				}
			}
			p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			receiverTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			if(BTSLUtil.isNullString(p2pTransferVO.getErrorCode()))
			{
				if(be.isKey()) {
					p2pTransferVO.setErrorCode(be.getMessageKey());
				} else {
					p2pTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
				}
			}
			log.error("EVDController[processValidationRequest]","Getting BTSL Base Exception:"+be.getMessage());
			//voucherUpdateSenderCreditBack(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION);
			throw new BTSLBaseException(be);
		}
		catch(Exception e)
		{
			requestVO.setSuccessTxn(false);
			log.errorTrace(methodName, e);
			if(con!=null) {
				con.rollback();
			}con=null;
			p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			if(recValidationFailMessageRequired)
			{
							if(p2pTransferVO.getReceiverReturnMsg()==null || !((BTSLMessages)p2pTransferVO.getReceiverReturnMsg()).isKey()) {
					p2pTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.C2S_RECEIVER_FAIL_EVD),new String[]{String.valueOf(transferID),PretupsBL.getDisplayAmount(p2pTransferVO.getRequestedAmount())}));
				}
}
			if(BTSLUtil.isNullString(p2pTransferVO.getErrorCode())) {
				p2pTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			}

			throw new BTSLBaseException("EVDController",methodName,PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
		}
		finally
		{
			if(con!=null) {
				try {con.close(); } catch(Exception e) {log.errorTrace(methodName, e);}
			}
			con=null;
		}

	}	

	
	/**Method :calulateTransferValue
	 * This method will set the values from the VOMS VO into intemsVO and transferVO
	 * 
	 * @param p_transferVO
	 * @param p_vomsVO
	 */
	public void calulateTransferValue(P2PTransferVO p_transferVO,VomsVoucherVO p_vomsVO)
	{
		p_transferVO.setSenderTransferValue(p_transferVO.getTransferValue());
		TransferItemVO transferItemVO=(TransferItemVO)p_transferVO.getTransferItemList().get(1);
		TransferItemVO sender_TransferItemVO=(TransferItemVO)p_transferVO.getTransferItemList().get(0);
		p_transferVO.setReceiverGracePeriod(p_vomsVO.getGracePeriod());
		p_transferVO.setReceiverValidity(p_vomsVO.getValidity());
		p_transferVO.setReceiverTransferValue(p_vomsVO.getTalkTime());
		transferItemVO.setTransferValue(p_vomsVO.getTalkTime());
		transferItemVO.setGraceDaysStr(String.valueOf(p_vomsVO.getGracePeriod()));
		transferItemVO.setValidity(p_vomsVO.getValidity());
		sender_TransferItemVO.setTransferValue(p_transferVO.getTransferValue());

	}
	
	/**
	 * Process Transfer Request , Genaerates the Transfer ID and populates the Transfer Items VO
	 * @param p_con
	 * @throws BTSLBaseException
	 */
	public void processTransfer(Connection p_con) throws BTSLBaseException
	{
		if(log.isDebugEnabled()) {
			log.debug("processTransfer",p2pTransferVO.getRequestID(),"Entered");
		}
		try
		{
			p2pTransferVO.setTransferDate(currentDate);
			p2pTransferVO.setTransferDateTime(currentDate);
			//PretupsBL.generateTransferID(p2pTransferVO);
			generateTransferID(p2pTransferVO);
			transferID=p2pTransferVO.getTransferID();
			//set sender transfer item details
			setSenderTransferItemVO();
			//set receiver transfer item details
			setReceiverTransferItemVO();
			PretupsBL.getProductFromServiceType(p_con,p2pTransferVO,serviceType,PretupsI.P2P_MODULE);

		}
		catch(BTSLBaseException be)
		{
			p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			if(be.isKey()) {
				p2pTransferVO.setErrorCode(be.getMessageKey());
			} else {
				p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			}
			throw be;
		}
		catch(Exception e)
		{
			if(recValidationFailMessageRequired)
			{
				if(p2pTransferVO.getReceiverReturnMsg()==null || !((BTSLMessages)p2pTransferVO.getReceiverReturnMsg()).isKey())
				{
					if(transferID!=null) {
						p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.P2P_RECEIVER_FAIL,new String[]{String.valueOf(transferID),PretupsBL.getDisplayAmount(p2pTransferVO.getRequestedAmount())}));
					} else {
						p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.P2P_FAIL_R,new String[]{PretupsBL.getDisplayAmount(p2pTransferVO.getRequestedAmount())}));
					}
				}
			}
			p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			final String methodName = "processValidationRequest";
			log.errorTrace(methodName, e);

			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VASVoucherConsController[processTransfer]",transferID,senderMSISDN,senderNetworkCode,"Exception:"+e.getMessage());
			throw new BTSLBaseException("VASVoucherConsController","processTransfer",PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
		}
	}

	public void run() {
		processThread();
	}
	/**
	 * This method will perform either topup in thread or both validation and topup on thread based on Flow Type
	 */
	public void processThread()
	{
		final String methodName = "processThread";
		if(log.isDebugEnabled()) {
			log.debug(methodName,transferID,"Entered");
		}
		BTSLMessages btslMessages=null; 
		Connection con=null;MComConnectionI mcomCon = null;
		boolean onlyDecreaseCounters=false;
		try
		{
			//Perform the validation of parties if Flow type is thread

			if(p2pTransferVO.getMsgGatewayFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD)  && !processedFromQueue)
			{
				processValidationRequestInThread();
			}

			//send validation request for sender
			CommonClient commonClient=new CommonClient();
			LoadController.incrementTransactionInterCounts(transferID,LoadControllerI.SENDER_UNDER_TOP);

			String receiverStr=getReceiverCreditStr();
			
			//send validation request for receiver
			TransactionLog.log(transferID,requestIDStr,receiverMSISDN,receiverVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_TXNSTAGE_INTOP,receiverStr,PretupsI.TXN_LOG_STATUS_SUCCESS,"");
			
			String receiverCreditResponse=commonClient.process(receiverStr,transferID,intModCommunicationTypeR,intModIPR,intModPortR,intModClassNameR);
			
			TransactionLog.log(transferID,requestIDStr,receiverMSISDN,receiverVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_INTOP,receiverCreditResponse,PretupsI.TXN_LOG_STATUS_SUCCESS,"");

			if(log.isDebugEnabled()) {
				log.debug(methodName,transferID,"receiverCreditResponse From IN Module="+receiverCreditResponse);
			}

			try
			{
				//Get the Receiver Credit response and processes the same	
				updateForReceiverCreditResponse(receiverCreditResponse);
			}
			catch(BTSLBaseException be)
			{
				TransactionLog.log(transferID,requestIDStr,senderMSISDN,senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Transaction Failed",PretupsI.TXN_LOG_STATUS_FAIL,"Transfer Status="+p2pTransferVO.getTransferStatus()+" Getting Code="+receiverVO.getInterfaceResponseCode());
				//No need to check for sender limits as the receiver created the problem
				LoadController.decreaseResponseCounters(transferID,PretupsErrorCodesI.TXN_STATUS_FAIL,LoadControllerI.RECEIVER_TOP_RESPONSE);
				//validate receiver limits after Interface Updation
				PretupsBL.validateRecieverLimits(null,p2pTransferVO,PretupsI.TRANS_STAGE_AFTER_INTOP,PretupsI.P2P_MODULE);

				throw be;
			}
			catch(Exception e)
			{
				TransactionLog.log(transferID,requestIDStr,senderMSISDN,senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Transaction Failed",PretupsI.TXN_LOG_STATUS_FAIL,"Transfer Status="+p2pTransferVO.getTransferStatus()+" Getting exception="+e.getMessage());

				LoadController.decreaseResponseCounters(transferID,PretupsErrorCodesI.TXN_STATUS_FAIL,LoadControllerI.RECEIVER_TOP_RESPONSE);
			
			
				//validate receiver limits after Interface Updation
				PretupsBL.validateRecieverLimits(null,p2pTransferVO,PretupsI.TRANS_STAGE_AFTER_INTOP,PretupsI.P2P_MODULE);
				//No need to check for sender limits as the receiver created the problem
	
				throw new BTSLBaseException(this, methodName, "");
			}

			LoadController.decreaseResponseCounters(transferID,PretupsErrorCodesI.TXN_STATUS_SUCCESS,LoadControllerI.RECEIVER_TOP_RESPONSE);

			senderVO.setTotalConsecutiveFailCount(0);
			senderVO.setTotalTransfers(senderVO.getTotalTransfers()+1);
			senderVO.setTotalTransferAmount(senderVO.getTotalTransferAmount()+senderTransferItemVO.getRequestValue());
			senderVO.setLastSuccessTransferDate(currentDate);
			p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
			p2pTransferVO.setErrorCode(null);
			senderVO.setConsecutiveFailures(0);
			senderVO.setTotalConsecutiveFailCount(0);
		
			//validate receiver limits after Interface Updation
			PretupsBL.validateRecieverLimits(null,p2pTransferVO,PretupsI.TRANS_STAGE_AFTER_INTOP,PretupsI.P2P_MODULE);

			//For increaseing the counters in network and service type
			ReqNetworkServiceLoadController.increaseRechargeCounters(requestVO.getInstanceID(),requestVO.getMessageGatewayVO().getGatewayType(),senderNetworkCode,serviceType,transferID,LoadControllerI.COUNTER_SUCCESS_REQUEST,0,true,receiverVO.getNetworkCode());
			ReqNetworkServiceLoadController.decrementUnderProcessCounters(requestVO.getInstanceID(),requestVO.getMessageGatewayVO().getGatewayType(),senderNetworkCode,serviceType,transferID,LoadControllerI.COUNTER_UNDERPROCESS_REQUEST,0,false,receiverVO.getNetworkCode());

			
		}
		catch(BTSLBaseException be)
		{	
			log.error(methodName, "BTSLBaseException " + be);
			log.errorTrace(methodName, be);
			requestVO.setSuccessTxn(false);
			
			
			if(be.isKey())
			{
				if(p2pTransferVO.getErrorCode()==null) {
					p2pTransferVO.setErrorCode(be.getMessageKey());
				}

				requestVO.setMessageCode(be.getMessageKey());
				requestVO.setMessageArguments(be.getArgs());
			} else {
				p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
				requestVO.setMessageCode(be.getMessageKey());
				requestVO.setMessageArguments(be.getArgs());
			}
			
			
			senderVO.setConsecutiveFailures(senderVO.getConsecutiveFailures()+1);
			senderVO.setTotalConsecutiveFailCount(senderVO.getConsecutiveFailures());
			if(be.isKey() && p2pTransferVO.getSenderReturnMessage()==null) {
			btslMessages=be.getBtslMessages();
		} else if(p2pTransferVO.getSenderReturnMessage()==null) {
			p2pTransferVO.setSenderReturnMessage(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
		}
		if(log.isDebugEnabled()) {
			log.debug(methodName,transferID,"Error Code:"+btslMessages.print());
		}

		//For increaseing the counters in network and service type
		ReqNetworkServiceLoadController.increaseRechargeCounters(requestVO.getInstanceID(),requestVO.getMessageGatewayVO().getGatewayType(),senderNetworkCode,serviceType,transferID,LoadControllerI.COUNTER_FAIL_REQUEST,0,false,receiverVO.getNetworkCode());
		ReqNetworkServiceLoadController.decrementUnderProcessCounters(requestVO.getInstanceID(),requestVO.getMessageGatewayVO().getGatewayType(),senderNetworkCode,serviceType,transferID,LoadControllerI.COUNTER_UNDERPROCESS_REQUEST,0,false,receiverVO.getNetworkCode());

		}
		catch(Exception e)
		{
			requestVO.setSuccessTxn(false);
			
			log.errorTrace(methodName,e);
			if(BTSLUtil.isNullString(p2pTransferVO.getErrorCode())) {
				p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			}
			log.error(methodName,transferID,"Exception:"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VASVoucherConsController[run]",transferID,senderMSISDN,senderNetworkCode,"Exception:"+e.getMessage());
			btslMessages=new BTSLMessages(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			senderVO.setConsecutiveFailures(senderVO.getConsecutiveFailures()+1);
			senderVO.setTotalConsecutiveFailCount(senderVO.getConsecutiveFailures());
			//For increaseing the counters in network and service type
			ReqNetworkServiceLoadController.increaseRechargeCounters(requestVO.getInstanceID(),requestVO.getMessageGatewayVO().getGatewayType(),senderNetworkCode,serviceType,transferID,LoadControllerI.COUNTER_FAIL_REQUEST,0,false,receiverVO.getNetworkCode());
			ReqNetworkServiceLoadController.decrementUnderProcessCounters(requestVO.getInstanceID(),requestVO.getMessageGatewayVO().getGatewayType(),senderNetworkCode,serviceType,transferID,LoadControllerI.COUNTER_UNDERPROCESS_REQUEST,0,false,receiverVO.getNetworkCode());
			}
		finally
		{
			try
			{
				if(p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL) && (p2pTransferVO.getReceiverReturnMsg()==null || !((BTSLMessages)p2pTransferVO.getReceiverReturnMsg()).isKey())) {
							p2pTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.P2P_RECEIVER_FAIL),new String[]{String.valueOf(transferID),PretupsBL.getDisplayAmount(p2pTransferVO.getRequestedAmount())}));
		}

				LoadController.decreaseTransactionLoad(transferID,senderNetworkCode,LoadControllerI.DEC_LAST_TRANS_COUNT);

				if(mcomCon==null) {
					mcomCon = new MComConnection();con=mcomCon.getConnection();
				}
				try
				{
					SubscriberBL.updateVoucherSubscriberLastDetails(con,p2pTransferVO,senderVO,currentDate,p2pTransferVO.getTransferStatus());
				}
				catch(BTSLBaseException bex)
				{
					log.errorTrace(methodName, bex);
				}
				catch(Exception e)
				{
					log.errorTrace(methodName, e);
					EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VASVoucherConsController[run]",transferID,senderMSISDN,senderNetworkCode,"Not able to update Subscriber Last Details Exception:"+e.getMessage());
				}

				try
				{
					if(receiverVO!=null && receiverVO.isUnmarkRequestStatus()) {
						PretupsBL.unmarkReceiverLastRequest(con,transferID,receiverVO);
					}
				}
				catch(BTSLBaseException bex)
				{
					log.errorTrace(methodName, bex);
				}
				catch(Exception e)
				{
					log.errorTrace(methodName, e);
					EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VASVoucherConsController[run]",transferID,senderMSISDN,senderNetworkCode,"Not able to unmark Receiver Last Request, Exception:"+e.getMessage());
				}

				if(finalTransferStatusUpdate)
				{
					//update transfer details in database
					//update transfer details in database
					p2pTransferVO.setModifiedOn(currentDate);
					p2pTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
					PretupsBL.updateVoucherTransferDetails(con,p2pTransferVO,vomsVO);
			}
			}
			catch(BTSLBaseException bex)
			{
				log.errorTrace(methodName, bex);
				try{if(con!=null) {
					con.rollback();
				}}catch(Exception ex){log.errorTrace(methodName,ex);}
				log.error(methodName,transferID,"BTSL Base Exception while updating transfer details in database:"+bex.getMessage());
			}
			catch(Exception e)
			{
				log.errorTrace(methodName, e);
				try{if(con!=null) {
					con.rollback();
				}}catch(Exception ex){log.errorTrace(methodName,ex);}
				log.error(methodName,transferID,"Exception while updating transfer details in database:"+e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VASVoucherConsController[run]",transferID,senderMSISDN,senderNetworkCode,"Exception while updating transfer details in database , Exception:"+e.getMessage());
			}
			if(con!=null) 
			{
				try{con.commit();}catch(Exception e){log.errorTrace(methodName,e);}
				try{con.close();}catch(Exception e){log.errorTrace(methodName,e);}
				con=null;
			}
	
			String recAlternetGatewaySMS=BTSLUtil.NullToString(Constants.getProperty("P2P_REC_MSG_REQD_BY_ALT_GW"));
			String reqruestGW=p2pTransferVO.getRequestGatewayCode();
			if(!BTSLUtil.isNullString(recAlternetGatewaySMS)&& (recAlternetGatewaySMS.split(":")).length>=2)
			{
				if(reqruestGW.equalsIgnoreCase(recAlternetGatewaySMS.split(":")[0]))
				{
					reqruestGW=(recAlternetGatewaySMS.split(":")[1]).trim();
					if(log.isDebugEnabled()) {
						log.debug("run: Reciver Message push through alternate GW",reqruestGW,"Requested GW was:"+p2pTransferVO.getRequestGatewayCode());
					}
				}
			}

			if(recTopupSuccMessageRequired &&p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS))
			{
				if(p2pTransferVO.getReceiverReturnMsg()==null) {
					(new PushMessage(receiverMSISDN,getReceiverSuccessMessage(),transferID,reqruestGW,receiverLocale)).push();
				}else if(p2pTransferVO.getReceiverReturnMsg()!=null &&((BTSLMessages)p2pTransferVO.getReceiverReturnMsg()).getMessageKey().equals(PretupsErrorCodesI.REC_LAST_SUCCESS_REQ_BLOCK_R_PRE))
				{
					(new PushMessage(receiverMSISDN,getReceiverSuccessMessage(),transferID,reqruestGW,receiverLocale)).push();
				}
				 else if(p2pTransferVO.getReceiverReturnMsg()!=null&&((BTSLMessages)p2pTransferVO.getReceiverReturnMsg()).isKey())
				{
					BTSLMessages btslRecMessages=(BTSLMessages)p2pTransferVO.getReceiverReturnMsg();
					(new PushMessage(receiverMSISDN,BTSLUtil.getMessage(receiverLocale,btslRecMessages.getMessageKey(),btslRecMessages.getArgs()),transferID,reqruestGW,receiverLocale)).push();
				} else {
					(new PushMessage(receiverMSISDN,(String)p2pTransferVO.getReceiverReturnMsg(),transferID,reqruestGW,receiverLocale)).push();
				}
			}
			else if(recTopupFailMessageRequired&&p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS))
			{
				if(p2pTransferVO.getReceiverReturnMsg()==null) {
					(new PushMessage(receiverMSISDN,getReceiverAmbigousMessage(),transferID,reqruestGW,receiverLocale)).push();
				} else if(p2pTransferVO.getReceiverReturnMsg()!=null&&((BTSLMessages)p2pTransferVO.getReceiverReturnMsg()).isKey())
				{
					BTSLMessages btslRecMessages=(BTSLMessages)p2pTransferVO.getReceiverReturnMsg();
					(new PushMessage(receiverMSISDN,BTSLUtil.getMessage(receiverLocale,btslRecMessages.getMessageKey(),btslRecMessages.getArgs()),transferID,reqruestGW,receiverLocale)).push();
				} else {
					(new PushMessage(receiverMSISDN,(String)p2pTransferVO.getReceiverReturnMsg(),transferID,reqruestGW,receiverLocale)).push();
				}
			}
			else if(recTopupFailMessageRequired && p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL))
			{
				if(p2pTransferVO.getReceiverReturnMsg()==null) {
					(new PushMessage(receiverMSISDN,getReceiverFailMessage(),transferID,reqruestGW,receiverLocale)).push();
				} else if(p2pTransferVO.getReceiverReturnMsg()!=null&&((BTSLMessages)p2pTransferVO.getReceiverReturnMsg()).isKey())
				{
					BTSLMessages btslRecMessages=(BTSLMessages)p2pTransferVO.getReceiverReturnMsg();
					(new PushMessage(receiverMSISDN,BTSLUtil.getMessage(receiverLocale,btslRecMessages.getMessageKey(),btslRecMessages.getArgs()),transferID,reqruestGW,receiverLocale)).push();
				} else {
					(new PushMessage(receiverMSISDN,(String)p2pTransferVO.getReceiverReturnMsg(),transferID,reqruestGW,receiverLocale)).push();
				}
			}
			
			if(!oneLog) {
				OneLineTXNLog.log(p2pTransferVO,senderTransferItemVO,receiverTransferItemVO,vomsVO);
			}
			TransactionLog.log(transferID,requestIDStr,senderMSISDN,senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Transaction Ending",PretupsI.TXN_LOG_STATUS_SUCCESS,"Transfer Status="+p2pTransferVO.getTransferStatus()+" Transfer Category="+p2pTransferVO.getTransferCategory()+" Error Code="+p2pTransferVO.getErrorCode()+" Message="+p2pTransferVO.getSenderReturnMessage());

			P2PRequestDailyLog.log(P2PRequestDailyLog.populateP2PRequestDailyLogVO(requestVO, p2pTransferVO));

			if(log.isDebugEnabled()) {
				log.debug(methodName,transferID,"Exiting");
			}
		}
	}
	
		/**
	 * Method to get the Receiver Ambigous Message
	 * @return
	 */
	private String getReceiverAmbigousMessage()
	{
		String[] messageArgArray={senderMSISDN,transferID,PretupsBL.getDisplayAmount(p2pTransferVO.getRequestedAmount())};
		return BTSLUtil.getMessage(receiverLocale,PretupsErrorCodesI.P2P_RECEIVER_AMBIGOUS_MESSAGE_KEY,messageArgArray);
	}

	/**
	 * Method to get the Receiver Fail Message
	 * @return
	 */
	private String getReceiverFailMessage()
	{
		String[] messageArgArray={senderMSISDN,transferID,PretupsBL.getDisplayAmount(p2pTransferVO.getRequestedAmount())};
		return BTSLUtil.getMessage(receiverLocale,PretupsErrorCodesI.P2P_RECEIVER_FAIL_MESSAGE_KEY,messageArgArray);
	}
	/***
	 * 
	 * Method updated for notification message using service class date 15/05/06
	 */
	private String getReceiverSuccessMessage()
	{
		String[] messageArgArray=null;
		String key=null;

			messageArgArray=new String[]{transferID,Long.toString(p2pTransferVO.getRequestedAmount()),senderMSISDN,p2pTransferVO.getVasServiceName()};
			if(p2pTransferVO.getBonusTalkTimeValue()==0) {
				key=PretupsErrorCodesI.VMS_VAS_RECEIVER_SUCCESS;//return BTSLUtil.getMessage(receiverLocale,PretupsErrorCodesI.P2P_RECEIVER_SUCCESS,messageArgArray);
			} else {
				key=PretupsErrorCodesI.VMS_VAS_RECEIVER_SUCCESS_WITH_BONUS;
			}

		return BTSLUtil.getMessage(receiverLocale,key,messageArgArray);
	}

	/**
	 * Populates the Sender Transfer Items VO
	 *
	 */
	private void setSenderTransferItemVO()
	{
		senderTransferItemVO=new TransferItemVO();
		//set sender transfer item details
		senderTransferItemVO.setSNo(1);
		senderTransferItemVO.setMsisdn(senderMSISDN);
		senderTransferItemVO.setRequestValue(p2pTransferVO.getTransferValue());
		senderTransferItemVO.setSubscriberType(senderSubscriberType);
		senderTransferItemVO.setTransferDate(currentDate);
		senderTransferItemVO.setTransferDateTime(currentDate);
		senderTransferItemVO.setTransferID(p2pTransferVO.getTransferID());
		senderTransferItemVO.setTransferType(PretupsI.TRANSFER_TYPE_TXN);
		senderTransferItemVO.setUserType(PretupsI.USER_TYPE_SENDER);
		senderTransferItemVO.setEntryDate(currentDate);
		senderTransferItemVO.setEntryDateTime(currentDate);
		senderTransferItemVO.setEntryType(PretupsI.DEBIT);
		senderTransferItemVO.setPrefixID(senderVO.getPrefixID());
	}

	/**
	 * Populates the Receiver Transfer Items VO
	 *
	 */
	private void setReceiverTransferItemVO()
	{
		receiverTransferItemVO=new TransferItemVO();
		receiverTransferItemVO.setSNo(2);	
		receiverTransferItemVO.setMsisdn(receiverMSISDN);
		receiverTransferItemVO.setRequestValue(p2pTransferVO.getTransferValue());
		receiverTransferItemVO.setSubscriberType(type);
		receiverTransferItemVO.setTransferDate(currentDate);
		receiverTransferItemVO.setTransferDateTime(currentDate);
		receiverTransferItemVO.setTransferID(p2pTransferVO.getTransferID());
		receiverTransferItemVO.setTransferType(PretupsI.TRANSFER_TYPE_TXN);
		receiverTransferItemVO.setUserType(PretupsI.USER_TYPE_RECEIVER);
		receiverTransferItemVO.setEntryDate(currentDate);
		receiverTransferItemVO.setEntryType(PretupsI.CREDIT);
		receiverTransferItemVO.setPrefixID(receiverVO.getPrefixID());
		receiverTransferItemVO.setEntryDateTime(currentDate);
	}

	/**
	 * Method to populate the Interface Details of the sender and receiver based on action specified
	 * @param action Can be Validate / Topup
	 * @throws BTSLBaseException
	 */
	public void populateServicePaymentInterfaceDetails(Connection p_con,String action) throws BTSLBaseException
	{
		String senderNetwork_Code=senderVO.getNetworkCode();
		String receiverNetworkCode=receiverVO.getNetworkCode();
		long senderPrefixID=senderVO.getPrefixID();
		long receiverPrefixID=receiverVO.getPrefixID();
		boolean isSenderFound=false;
		boolean isReceiverFound=false;
		if(log.isDebugEnabled()) {
			log.debug(this,"Getting interface details For Action="+action+" senderInterfaceInfoInDBFound="+senderInterfaceInfoInDBFound+" receiverInterfaceInfoInDBFound="+receiverInterfaceInfoInDBFound);
			//Avoid searching in the loop again if in validation details was found in database
			//This condition has been changed so that if payment method is not the dafult one then there may be case that default interface will be used for that.
		}

		if(((!senderInterfaceInfoInDBFound && (p2pTransferVO.getPaymentMethodKeywordVO()==null||!PretupsI.YES.equals(p2pTransferVO.getPaymentMethodKeywordVO().getUseDefaultInterface())) )&& action.equals(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION)) || action.equals(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION))
		{
			if(p2pTransferVO.getPaymentMethodKeywordVO()!=null && PretupsI.YES.equals(p2pTransferVO.getPaymentMethodKeywordVO().getUseDefaultInterface()))
			{
				if(log.isDebugEnabled()) {
					log.debug(this,"For Sender using the Payment Method Default Interface as="+p2pTransferVO.getPaymentMethodKeywordVO().getDefaultInterfaceID());
				}
				senderTransferItemVO.setPrefixID(senderPrefixID);
				senderTransferItemVO.setInterfaceID(p2pTransferVO.getPaymentMethodKeywordVO().getDefaultInterfaceID());
				senderTransferItemVO.setInterfaceHandlerClass(p2pTransferVO.getPaymentMethodKeywordVO().getHandlerClass());
				senderAllServiceClassID=p2pTransferVO.getPaymentMethodKeywordVO().getAllServiceClassId();
			
				senderInterfaceStatusType=p2pTransferVO.getPaymentMethodKeywordVO().getStatusType();
				p2pTransferVO.setSenderAllServiceClassID(senderAllServiceClassID);
				senderTransferItemVO.setInterfaceType(p2pTransferVO.getPaymentMethodType());
				p2pTransferVO.setSenderInterfaceStatusType(senderInterfaceStatusType);
				if(!PretupsI.YES.equals(p2pTransferVO.getPaymentMethodKeywordVO().getStatus()) && PretupsI.INTERFACE_STATUS_TYPE_MANUAL.equals(senderInterfaceStatusType))
				{
					if(PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(senderLocale)).getMessage())) {
						p2pTransferVO.setSenderReturnMessage(p2pTransferVO.getPaymentMethodKeywordVO().getLang1Message());
					} else {
						p2pTransferVO.setSenderReturnMessage(p2pTransferVO.getPaymentMethodKeywordVO().getLang2Message());
					}
					throw new BTSLBaseException(this,"populateServicePaymentInterfaceDetails",PretupsErrorCodesI.INTERFACE_NOT_ACTIVE);
				}
				isSenderFound=true;
			} else {
				isSenderFound=getInterfaceRoutingDetails(p_con,senderMSISDN,senderPrefixID,senderVO.getSubscriberType(),senderNetwork_Code,p2pTransferVO.getServiceType(),p2pTransferVO.getPaymentMethodType(),PretupsI.USER_TYPE_SENDER,action);
			}
		} else {
			isSenderFound=true;
		}
		if(!isSenderFound)
		{
			if(!senderVO.isDefUserRegistration()) {
				throw new BTSLBaseException("VASVoucherConsController","populateServicePaymentInterfaceDetails",PretupsErrorCodesI.P2P_SENDER_ALREADY_REG_NOT_FOUND_IN_VAL,0,new String[]{((LookupsVO)LookupsCache.getObject(PretupsI.SUBSRICBER_TYPE,senderVO.getSubscriberType())).getLookupName()},null);
			}
			throw new BTSLBaseException("VASVoucherConsController","populateServicePaymentInterfaceDetails",PretupsErrorCodesI.P2P_NOTFOUND_PAYMENTINTERFACEMAPPING);
		}				

		//Avoid searching in the loop again if in validation details was found in database
		if((!receiverInterfaceInfoInDBFound && action.equals(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION)) || action.equals(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION))
		{
			isReceiverFound=getInterfaceRoutingDetails(p_con,receiverMSISDN,receiverPrefixID,type,receiverNetworkCode,p2pTransferVO.getServiceType(),type,PretupsI.USER_TYPE_RECEIVER,action);
			//If receiver Not found and we need to perform the alternate category routing before IN Validation and it has not been performed before then do Category Routing
			if(action.equals(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION) && !isReceiverFound && performIntfceCatRoutingBeforeVal && useAlternateCategory &&!interfaceCatRoutingDone)
			{
				//Get the alternate interface category and check whether it is valid in that category.
				log.info(this,"********* Performing ALTERNATE INTERFACE CATEGORY routing for receiver before IN Validations on Interface="+newInterfaceCategory+" *********");

				type=newInterfaceCategory;
				interfaceCatRoutingDone=true;

				requestVO.setReqSelector(newDefaultSelector);
				p2pTransferVO.setSubService(newDefaultSelector);

				//Load the new prefix ID against the interface category , If Not required then give the error

				networkPrefixVO=null;
				networkPrefixVO=(NetworkPrefixVO)NetworkPrefixCache.getObject(receiverVO.getMsisdnPrefix(),type);
				if(networkPrefixVO!=null)
				{
					if(log.isDebugEnabled()) {
						log.debug(this,"Getting Reeciver Prefix ID for MSISDN="+receiverMSISDN+" as "+networkPrefixVO.getPrefixID());
					}
					receiverVO.setNetworkCode(networkPrefixVO.getNetworkCode());
					receiverVO.setPrefixID(networkPrefixVO.getPrefixID());
					receiverVO.setSubscriberType(networkPrefixVO.getSeriesType());
					receiverNetworkCode=receiverVO.getNetworkCode();
					receiverPrefixID=receiverVO.getPrefixID();
					isReceiverFound=getInterfaceRoutingDetails(p_con,receiverMSISDN,receiverPrefixID,type,receiverNetworkCode,p2pTransferVO.getServiceType(),type,PretupsI.USER_TYPE_RECEIVER,action);
				}
				else
				{
					log.error(this,"Series Not Defined for Alternate Interface ="+type+" For Series="+receiverVO.getMsisdnPrefix());
					EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,"VASVoucherConsController[populateServicePaymentInterfaceDetails]","","","","Series ="+receiverVO.getMsisdnPrefix()+" Not Defined for Series type="+type+" But alternate Category Routing was required on interface");
					isReceiverFound=false;
				}
			}
		} else {
			isReceiverFound=true;
		}
		if(!isReceiverFound) {
			throw new BTSLBaseException("VASVoucherConsController","populateServicePaymentInterfaceDetails",PretupsErrorCodesI.P2P_NOTFOUND_SERVICEINTERFACEMAPPING);
		}
	}
	/**
	 * Get the Receiver Request String to be send to common Client
	 * @return
	 */
	private String getReceiverCommonString()
	{
		final String methodName="getReceiverCommonString";
		StringBuffer strBuff=null;
		strBuff=new StringBuffer("MSISDN="+receiverMSISDN);
		strBuff.append("&TRANSACTION_ID="+transferID);
		strBuff.append("&NETWORK_CODE="+receiverVO.getNetworkCode());
		strBuff.append("&INTERFACE_ID="+receiverTransferItemVO.getInterfaceID());
		strBuff.append("&INTERFACE_HANDLER="+receiverTransferItemVO.getInterfaceHandlerClass());
		strBuff.append("&INT_MOD_COMM_TYPE="+intModCommunicationTypeR);
		strBuff.append("&INT_MOD_IP="+intModIPR);
		strBuff.append("&INT_MOD_PORT="+intModPortR);
		strBuff.append("&INT_MOD_CLASSNAME="+intModClassNameR);
		strBuff.append("&MODULE="+PretupsI.P2P_MODULE);
		strBuff.append("&USER_TYPE=R");
		//added for CRE_INT_CR00029 by ankit Zindal
		strBuff.append("&CARD_GROUP_SELECTOR="+requestVO.getReqSelector());
		strBuff.append("&REQ_SERVICE="+serviceType);
		strBuff.append("&INT_ST_TYPE="+p2pTransferVO.getReceiverInterfaceStatusType());
		try
		{
			strBuff.append("&TRANSFER_DATE="+ BTSLUtil.getDateTimeStringFromDate(p2pTransferVO.getTransferDate(),PretupsI.TIMESTAMP_DATESPACEHHMM));
		}
		catch(Exception e)
		{
			log.errorTrace(methodName,e);			
		}
		strBuff.append("&REQUEST_GATEWAY_CODE="+requestVO.getRequestGatewayCode());
		strBuff.append("&REQUEST_GATEWAY_TYPE="+requestVO.getRequestGatewayType());
		strBuff.append("&LOGIN="+requestVO.getLogin());
		strBuff.append("&PASSWORD="+requestVO.getPassword());
		strBuff.append("&SOURCE_TYPE="+requestVO.getSourceType());
		strBuff.append("&SERVICE_PORT="+requestVO.getServicePort());
		strBuff.append("&VOUCHER_CODE="+p2pTransferVO.getVoucherCode());
		strBuff.append("&SERAIL_NO="+p2pTransferVO.getSerialNumber());
		if(p2pTransferVO.getSerialNumber()!=null)
			strBuff.append("&SERAIL_NO="+p2pTransferVO.getSerialNumber());
		else
			strBuff.append("&SERAIL_NO="+"");

		return strBuff.toString();
	}

	/**
	 * Gets the receiver validate Request String
	 * @return
	 */
	public String getReceiverValidateStr()
	{
		StringBuffer strBuff=null;
		strBuff=new StringBuffer(getReceiverCommonString());
		strBuff.append("&INTERFACE_ACTION="+PretupsI.INTERFACE_VALIDATE_ACTION);
		strBuff.append("&SERVICE_CLASS="+receiverTransferItemVO.getServiceClassCode());
		strBuff.append("&ACCOUNT_ID="+receiverTransferItemVO.getReferenceID());
		strBuff.append("&ACCOUNT_STATUS="+receiverTransferItemVO.getAccountStatus());
		strBuff.append("&CREDIT_LIMIT="+receiverTransferItemVO.getPreviousBalance());
		strBuff.append("&SERVICE_TYPE="+senderSubscriberType+"-"+type);
		strBuff.append("&SENDER_MSISDN="+p2pTransferVO.getSenderMsisdn());
		strBuff.append("&SENDER_USER_ID="+p2pTransferVO.getSenderID());
		return strBuff.toString();
	}
	/**
	 * Gets the sender Credit Request String
	 * @return
	 */
	public String getReceiverCreditStr()
	{
		StringBuffer strBuff=null;
		strBuff=new StringBuffer(getReceiverCommonString());
		strBuff.append("&INTERFACE_ACTION="+PretupsI.INTERFACE_CREDIT_ACTION);
		strBuff.append("&INTERFACE_AMOUNT="+receiverTransferItemVO.getTransferValue());
		strBuff.append("&SERVICE_CLASS="+receiverTransferItemVO.getServiceClassCode());
		strBuff.append("&ACCOUNT_ID="+receiverTransferItemVO.getReferenceID());
		strBuff.append("&ACCOUNT_STATUS="+receiverTransferItemVO.getAccountStatus());
		strBuff.append("&SOURCE_TYPE="+p2pTransferVO.getSourceType());
		//strBuff.append("&SERIAL_NUMBER="+vomsVO.getSerialNo());
		strBuff.append("&UPDATE_STATUS="+VOMSI.VOUCHER_USED);
		strBuff.append("&SENDER_MSISDN="+senderMSISDN);
		strBuff.append("&RECEIVER_MSISDN="+receiverMSISDN);
		strBuff.append("&SENDER_ID="+((SenderVO)requestVO.getSenderVO()).getUserID());
		strBuff.append("&SERVICE_TYPE="+senderSubscriberType+"-"+type);
		if(senderVO.getLastTransferOn()==null){
			senderVO.setLastTransferOn(currentDate);
		}
		if(getDifferenceInUtilDates(senderVO.getLastTransferOn(), currentDate)> SystemPreferences.SUBS_UNBLK_AFT_X_TIME){
			
			senderVO.setConsecutiveFailures(0);
			senderVO.setTotalConsecutiveFailCount(0);
			
			strBuff.append("&CONSECUTIVE_FAILURES="+0);
		}else{
			strBuff.append("&CONSECUTIVE_FAILURES="+senderVO.getConsecutiveFailures());
		}
		
		
		return strBuff.toString();
	}

	
	public  int getDifferenceInUtilDates(java.util.Date date1,java.util.Date date2)
	{
		long dt1=date1.getTime();
		long dt2=date2.getTime();
		int nodays=(int)((dt2-dt1)/(1000*60));
		return nodays;
	}

	/**
	 * Method to handle receiver credit response  
	 * @param str
	 * @throws BTSLBaseException
	 */
	public void updateForReceiverCreditResponse(String str) throws BTSLBaseException
	{
		HashMap map=BTSLUtil.getStringToHash(str,"&","=");
		String status=(String)map.get("TRANSACTION_STATUS");
		
		_requestMap.put("vasVoucherStatus", "");
		
		if (null != map.get("IN_START_TIME")) {
			requestVO.setTopUPReceiverRequestSent(((Long.valueOf((String)map.get("IN_START_TIME"))).longValue()));
		}
		if (null != map.get("IN_END_TIME")) {
			requestVO.setTopUPReceiverResponseReceived(((Long.valueOf((String)map.get("IN_END_TIME"))).longValue()));
		}

		//Start: Update the Interface table for the interface ID based on Handler status and update the Cache
		String interfaceStatusType=(String)map.get("INT_SET_STATUS");
		//Done so that in Credit Back IN module does not activate the IN as else it would receive M from here
		if(InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) && receiverTransferItemVO.getInterfaceID().equals(senderTransferItemVO.getInterfaceID())) {
			p2pTransferVO.setSenderInterfaceStatusType(InterfaceCloserI.INTERFACE_AUTO_ACTIVE);
		}
		if(!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME.equals(interfaceStatusType))) {
			new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES,receiverTransferItemVO.getInterfaceID(),interfaceStatusType,PretupsErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG,PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
			//:End
		}

		p2pTransferVO.setTransferValueStr("0");
		receiverTransferItemVO.setProtocolStatus((String)map.get("PROTOCOL_STATUS"));
		receiverTransferItemVO.setInterfaceResponseCode((String)map.get("INTERFACE_STATUS"));
		senderTransferItemVO.setInterfaceResponseCode((String)map.get("INTERFACE_STATUS"));
		senderTransferItemVO.setProtocolStatus((String)map.get("PROTOCOL_STATUS"));
		
		String updateStatus=(String)map.get("UPDATE_STATUS");

		if(BTSLUtil.isNullString(updateStatus)) {
			updateStatus=status;
		}

		receiverTransferItemVO.setUpdateStatus(status);
		receiverVO.setInterfaceResponseCode(receiverTransferItemVO.getInterfaceResponseCode());		

		receiverTransferItemVO.setUpdateStatus1((String)map.get("UPDATE_STATUS1"));
		receiverTransferItemVO.setUpdateStatus2((String)map.get("UPDATE_STATUS2"));

		if(!BTSLUtil.isNullString((String)map.get("ADJUST_AMOUNT"))) {
			receiverTransferItemVO.setAdjustValue(Long.parseLong((String)map.get("ADJUST_AMOUNT")));
		}

		
		
		//set from IN Module
		final String methodName = "updateForReceiverCreditResponse";
		if(!BTSLUtil.isNullString((String)map.get("IN_TXN_ID")))
		{
			try{receiverTransferItemVO.setInterfaceReferenceID((String)map.get("IN_TXN_ID"));}
			catch(Exception e){log.errorTrace(methodName, e);}
		}
		if(!BTSLUtil.isNullString((String)map.get("IN_TXN_ID1")))
		{
			try{receiverTransferItemVO.setInterfaceReferenceID1((String)map.get("IN_TXN_ID1"));}
			catch(Exception e){log.errorTrace(methodName, e);}
			receiverTransferItemVO.setTransferType1(PretupsI.TRANSFER_TYPE_BA_ADJ_CR);
		}
		if(!BTSLUtil.isNullString((String)map.get("IN_TXN_ID2")))
		{
			try{receiverTransferItemVO.setInterfaceReferenceID2((String)map.get("IN_TXN_ID2"));}
			catch(Exception e){log.errorTrace(methodName, e);}
			receiverTransferItemVO.setTransferType2(PretupsI.TRANSFER_TYPE_BA_ADJ_DR);
		}
		receiverTransferItemVO.setReferenceID((String)map.get("IN_RECON_ID"));


		if(!BTSLUtil.isNullString((String)map.get("VINTERFACE_STATUS")))
		{
			vomsVO=new VomsVoucherVO();
			try{
				vomsVO.setSerialNo((String)map.get("SNO"));
			}catch (Exception e) {
				log.error(methodName, "Exception " + e);
				log.errorTrace(methodName, e);
				vomsVO.setSerialNo(null);
			}
			try{
				vomsVO.setCurrentStatus((String)map.get("VSTATUS"));
			}catch (Exception e) {
				log.error(methodName, "Exception " + e);
				log.errorTrace(methodName, e);
				vomsVO.setCurrentStatus(null);
			}
			try{
				vomsVO.setMessage((String)map.get("VERROR"));
			}catch (Exception e) {
				log.error(methodName, "Exception " + e);
				log.errorTrace(methodName, e);
				vomsVO.setMessage(null);
			}
			try{
				vomsVO.setLastErrorMessage((String)map.get("VINTERFACE_STATUS"));
			}catch (Exception e) {
				log.error(methodName, "Exception " + e);
				log.errorTrace(methodName, e);
				vomsVO.setLastErrorMessage(null);
			}	
			try{
				vomsVO.setTalkTime(Long.parseLong((String)map.get("TOPUP")));
			}catch (Exception e) {
				log.error(methodName, "Exception " + e);
				log.errorTrace(methodName, e);
				vomsVO.setTalkTime(Long.parseLong("0"));
			}

			try{
				vomsVO.setMRP(Double.parseDouble((String)map.get("VMRP")));
			}catch (Exception e) {
				log.error(methodName, "Exception " + e);
				log.errorTrace(methodName, e);
				vomsVO.setMRP(Double.parseDouble("0"));
			}
			try
			{
				vomsVO.setExpiryDateStr((String)map.get("VEXPIRYDATE"));
				Date expDate=BTSLUtil.getDateFromDateString((String)map.get("VEXPIRYDATE"),"ddMMyy");
				vomsVO.setExpiryDate(expDate);
			}
			catch(Exception e){
				log.error(methodName, "Exception " + e);
				log.errorTrace(methodName, e);
				vomsVO.setExpiryDate(null);
			}

		}

		String [] strArr=null;
		if(BTSLUtil.isNullString(status) || (!status.equals(InterfaceErrorCodesI.SUCCESS) && !status.equals(InterfaceErrorCodesI.AMBIGOUS)))
		{
			p2pTransferVO.setErrorCode(status+"_R");
			p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			receiverTransferItemVO.setTransferStatus(status);
			senderTransferItemVO.setTransferStatus(status);
			senderTransferItemVO.setUpdateStatus(status);
			try{
			senderTransferItemVO.setTransferValue(Long.parseLong((String)map.get("TOPUP")));
			p2pTransferVO.setTransferValue(Long.parseLong((String)map.get("TOPUP")));
			p2pTransferVO.setTransferValueStr((String)map.get("TOPUP"));
			receiverTransferItemVO.setTransferValue(Long.parseLong((String)map.get("TOPUP")));
			}catch (Exception e) {
				log.errorTrace(methodName, e);
			}
			strArr=new String[]{receiverMSISDN,Long.toString(p2pTransferVO.getRequestedAmount()),transferID};
			//throw new BTSLBaseException(this,"updateForReceiverCreditResponse",PretupsErrorCodesI.P2P_SENDER_FAIL,0,strArr,null);
			throw new BTSLBaseException(this,methodName,p2pTransferVO.getErrorCode(),0,strArr,null);
		}
		else if (status.equals(InterfaceErrorCodesI.AMBIGOUS))
		{
			p2pTransferVO.setErrorCode(status+"_R");
			p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
			receiverTransferItemVO.setTransferStatus(status);
			senderTransferItemVO.setTransferStatus(status);
			receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
			receiverTransferItemVO.setUpdateStatus(status);
			senderTransferItemVO.setUpdateStatus(status);
			try{
				senderTransferItemVO.setTransferValue(Long.parseLong((String)map.get("TOPUP")));
				p2pTransferVO.setTransferValue(Long.parseLong((String)map.get("TOPUP")));
				p2pTransferVO.setTransferValueStr((String)map.get("TOPUP"));
				receiverTransferItemVO.setTransferValue(Long.parseLong((String)map.get("TOPUP")));
				}catch (Exception e) {
					log.errorTrace(methodName, e);
				}
			strArr=new String[]{transferID,receiverTransferItemVO.getMsisdn(),PretupsBL.getDisplayAmount(p2pTransferVO.getRequestedAmount())};
			throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS,0,strArr,null);
		}	
		else
		{
			receiverTransferItemVO.setTransferStatus(status);
			receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
			receiverTransferItemVO.setUpdateStatus(status);
			senderTransferItemVO.setUpdateStatus(status);
			try{
				senderTransferItemVO.setTransferValue(Long.parseLong((String)map.get("TOPUP")));
				p2pTransferVO.setTransferValue(Long.parseLong((String)map.get("TOPUP")));
				p2pTransferVO.setTransferValueStr((String)map.get("TOPUP"));
				receiverTransferItemVO.setTransferValue(Long.parseLong((String)map.get("TOPUP")));
				}catch (Exception e) {
					log.errorTrace(methodName, e);
				}
			_requestMap.put("vasVoucherStatus",(String)map.get("BUNDLE_TXN_STATUS"));
			requestVO.setRequestMap(_requestMap);
			receiverTransferItemVO.setPostBalance(Long.parseLong((String)map.get("INTERFACE_POST_BALANCE")));
			p2pTransferVO.setVasServiceName((String)map.get("SELECTOR_NAME"));
			p2pTransferVO.setSubService((String)map.get("SELECTOR_CODE"));
			p2pTransferVO.setRequestedAmount(Long.parseLong((String)map.get("TOPUP")));
		}
		
		try
		{
			Date expDate=BTSLUtil.getDateFromDateString((String)map.get("NEW_EXPIRY_DATE"),"ddMMyyyy");
			receiverTransferItemVO.setNewExpiry(expDate);
		}
		catch(Exception e){
			log.errorTrace(methodName, e);
		}
		
		
		
		
	}

		/**
	 * Method to check the various level of loads whether request can be passed or not
	 * @throws BTSLBaseException
	 */
	private void checkTransactionLoad() throws BTSLBaseException
	{
		String methodName="checkTransactionLoad";
		if(log.isDebugEnabled()) {
			log.debug(methodName,"Checking load for transfer ID="+transferID);
		}  
		try
		{
			requestVO.setPerformIntfceCatRoutingBeforeVal(performIntfceCatRoutingBeforeVal);
			p2pTransferVO.setRequestVO(requestVO);
			p2pTransferVO.setSenderTransferItemVO(senderTransferItemVO);
			p2pTransferVO.setReceiverTransferItemVO(receiverTransferItemVO);
			requestVO.setReceiverDeletionReqFromSubRouting(receiverDeletionReqFromSubRouting);
			requestVO.setReceiverInterfaceInfoInDBFound(receiverInterfaceInfoInDBFound);
			requestVO.setSenderDeletionReqFromSubRouting(senderDeletionReqFromSubRouting);
			requestVO.setSenderInterfaceInfoInDBFound(senderInterfaceInfoInDBFound);
			requestVO.setInterfaceCatRoutingDone(interfaceCatRoutingDone);

			int senderLoadStatus=LoadController.checkInterfaceLoad(((SenderVO)p2pTransferVO.getSenderVO()).getNetworkCode(),senderTransferItemVO.getInterfaceID(),transferID,p2pTransferVO,true);
			int recieverLoadStatus=0;
			//Further process the request
			if(senderLoadStatus==0)
			{
				recieverLoadStatus=LoadController.checkInterfaceLoad(((ReceiverVO)p2pTransferVO.getReceiverVO()).getNetworkCode(),receiverTransferItemVO.getInterfaceID(),transferID,p2pTransferVO,true);
				if(recieverLoadStatus==0)
				{
					try
					{
						LoadController.checkTransactionLoad(((SenderVO)p2pTransferVO.getSenderVO()).getNetworkCode(),senderTransferItemVO.getInterfaceID(),PretupsI.P2P_MODULE,transferID,true,LoadControllerI.USERTYPE_SENDER);
					}
					catch(BTSLBaseException e)
					{
						//Decreasing interface load of receiver which we had incremented before 27/09/06, sender was decreased in the method
						LoadController.decreaseCurrentInterfaceLoad(transferID,((ReceiverVO)p2pTransferVO.getReceiverVO()).getNetworkCode(),receiverTransferItemVO.getInterfaceID(),LoadControllerI.DEC_LAST_TRANS_COUNT);
						throw e;
					}
					try
					{
						LoadController.checkTransactionLoad(((ReceiverVO)p2pTransferVO.getReceiverVO()).getNetworkCode(),receiverTransferItemVO.getInterfaceID(),PretupsI.P2P_MODULE,transferID,true,LoadControllerI.USERTYPE_RECEIVER);
					}
					catch(BTSLBaseException e)
					{
						//Decreasing interface load of sender which we had incremented before 27/09/06, receiver was decreased in the method
						LoadController.decreaseTransactionInterfaceLoad(transferID,((SenderVO)p2pTransferVO.getSenderVO()).getNetworkCode(),LoadControllerI.DEC_LAST_TRANS_COUNT);
						throw e;
					}					
					if(log.isDebugEnabled()) {
						log.debug("VASVoucherConsController[checkTransactionLoad]","transferID="+transferID+" Successfully through load");
					}
				}
				//Request in Queue
				else if(recieverLoadStatus==1)
				{
					//Decrease the interface counter of the sender that was increased
					LoadController.decreaseCurrentInterfaceLoad(transferID,((SenderVO)p2pTransferVO.getSenderVO()).getNetworkCode(),senderTransferItemVO.getInterfaceID(),LoadControllerI.DEC_LAST_TRANS_COUNT);

					String strArr[]={receiverMSISDN,String.valueOf(p2pTransferVO.getRequestedAmount())};
					throw new BTSLBaseException("VASVoucherConsController","checkTransactionLoad",PretupsErrorCodesI.REQUEST_IN_QUEUE,0,strArr,null);
				}
				//Refuse the request
				else {
					throw new BTSLBaseException("VASVoucherConsController","checkTransactionLoad",PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
				}
			}
			//Request in Queue
			else if(senderLoadStatus==1)
			{
				String strArr[]={receiverMSISDN,String.valueOf(p2pTransferVO.getRequestedAmount())};
				throw new BTSLBaseException("VASVoucherConsController","checkTransactionLoad",PretupsErrorCodesI.REQUEST_IN_QUEUE,0,strArr,null);
			}
			//Refuse the request
			else {
				throw new BTSLBaseException("EVDP2PController","checkTransactionLoad",PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
			}
		}
		catch(BTSLBaseException be)
		{
			log.error("VASVoucherConsController[checkTransactionLoad]","Refusing request getting Exception:"+be.getMessage());
			throw be;
		}
		catch(Exception e)
		{
			log.errorTrace(methodName,e);
			log.error("VASVoucherConsController[checkTransactionLoad]","Refusing request getting Exception:"+e.getMessage());
			throw new BTSLBaseException("VASVoucherConsController","checkTransactionLoad",PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
		}
	}

	/**
	 * This method will check the transaction load on the given interface
	 * @param p_userType
	 * @param p_interfaceID
	 * @throws BTSLBaseException
	 */
	private void checkTransactionLoad(String p_userType,String p_interfaceID) throws BTSLBaseException
	{

		String methodName="checkTransactionLoad";
		if(log.isDebugEnabled()) {
			log.debug(methodName,"Checking load for transfer ID="+transferID +" on interface="+p_interfaceID);
		}  
		int recieverLoadStatus=0;

		try
		{
			if(PretupsI.USER_TYPE_SENDER.equals(p_userType))
			{
				int senderLoadStatus=LoadController.checkInterfaceLoad(((SenderVO)p2pTransferVO.getSenderVO()).getNetworkCode(),p_interfaceID,transferID,p2pTransferVO,true);
				//Further process the request
				if(senderLoadStatus==0)
				{
					recieverLoadStatus=LoadController.checkInterfaceLoad(((ReceiverVO)p2pTransferVO.getReceiverVO()).getNetworkCode(),receiverTransferItemVO.getInterfaceID(),transferID,p2pTransferVO,false);
					if(recieverLoadStatus==0)
					{
						try
						{
							LoadController.checkTransactionLoad(((SenderVO)p2pTransferVO.getSenderVO()).getNetworkCode(),p_interfaceID,PretupsI.P2P_MODULE,transferID,true,LoadControllerI.USERTYPE_SENDER);
						}
						catch(BTSLBaseException e)
						{
							//Decreasing interface load of receiver which we had incremented before 27/09/06, sender was decreased in the method
							LoadController.decreaseCurrentInterfaceLoad(transferID,((ReceiverVO)p2pTransferVO.getReceiverVO()).getNetworkCode(),receiverTransferItemVO.getInterfaceID(),LoadControllerI.DEC_LAST_TRANS_COUNT);
							throw e;
						}
						try
						{
							LoadController.checkTransactionLoad(((ReceiverVO)p2pTransferVO.getReceiverVO()).getNetworkCode(),receiverTransferItemVO.getInterfaceID(),PretupsI.P2P_MODULE,transferID,true,LoadControllerI.USERTYPE_RECEIVER);
						}
						catch(BTSLBaseException e)
						{
							//Decreasing interface load of sender which we had incremented before 27/09/06, receiver was decreased in the method
							LoadController.decreaseTransactionInterfaceLoad(transferID,((SenderVO)p2pTransferVO.getSenderVO()).getNetworkCode(),LoadControllerI.DEC_LAST_TRANS_COUNT);
							throw e;
						}

						if(log.isDebugEnabled()) {
							log.debug("VASVoucherConsController[checkTransactionLoad]","transferID="+transferID+" Successfully through load");
						}
					}
					//Refuse the request
					else {
						throw new BTSLBaseException("VASVoucherConsController","checkTransactionLoad",PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
					}
				}
				//Request in Queue
				else if(senderLoadStatus==1)
				{
					String strArr[]={receiverMSISDN,String.valueOf(p2pTransferVO.getRequestedAmount())};
					throw new BTSLBaseException("VASVoucherConsController","checkTransactionLoad",PretupsErrorCodesI.REQUEST_IN_QUEUE,0,strArr,null);
				}
				//Refuse the request
				else {
					throw new BTSLBaseException("VASVoucherConsController","checkTransactionLoad",PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
				}
			}
			else
			{
				//Do not enter the request in Queue
				recieverLoadStatus=LoadController.checkInterfaceLoad(((ReceiverVO)p2pTransferVO.getReceiverVO()).getNetworkCode(),p_interfaceID,transferID,p2pTransferVO,false);
				if(recieverLoadStatus==0)
				{
					LoadController.checkTransactionLoad(((ReceiverVO)p2pTransferVO.getReceiverVO()).getNetworkCode(),p_interfaceID,PretupsI.P2P_MODULE,transferID,true,LoadControllerI.USERTYPE_RECEIVER);
					if(log.isDebugEnabled()) {
						log.debug("checkTransactionLoad[checkTransactionLoad]","transferID="+transferID+" Successfully through load");
					}
				}
				//Request in Queue
				else if(recieverLoadStatus==1)
				{
					throw new BTSLBaseException("checkTransactionLoad","checkTransactionLoad",PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
				}
				//Refuse the request
				else {
					throw new BTSLBaseException("checkTransactionLoad","checkTransactionLoad",PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
				}
			}	
		}
		catch(BTSLBaseException be)
		{
			log.error("VASVoucherConsController[checkTransactionLoad]","Refusing request getting Exception:"+be.getMessage());
			throw be;
		}
		catch(Exception e)
		{
			log.errorTrace(methodName,e);
			log.error("VASVoucherConsController[checkTransactionLoad]","Refusing request getting Exception:"+e.getMessage());
			throw new BTSLBaseException("VASVoucherConsController","checkTransactionLoad",PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
		}
	}

	/**
	 * This method will be called to process the request from the queue
	 * @param p_transferVO
	 */
	public void processFromQueue(TransferVO p_transferVO)
	{
		final String methodName = "processFromQueue";
		if(log.isDebugEnabled()) {
			log.debug(methodName,"Entered");
		}
		Connection con=null;MComConnectionI mcomCon = null;
		try
		{
			p2pTransferVO=(P2PTransferVO)p_transferVO;
			requestVO=p2pTransferVO.getRequestVO();
			senderVO=(SenderVO)requestVO.getSenderVO();
			receiverVO=(ReceiverVO)p2pTransferVO.getReceiverVO();
			type=requestVO.getType();
			if(type.equals(PretupsI.INTERFACE_CATEGORY_BOTH))
			{
				serviceInterfaceRoutingVO=ServiceInterfaceRoutingCache.getServiceInterfaceRoutingDetails(receiverVO.getNetworkCode()+"_"+requestVO.getServiceType());
				if (serviceInterfaceRoutingVO!=null)
				{
					if(log.isDebugEnabled()) {
						log.debug("process",requestIDStr,"For ="+receiverVO.getNetworkCode()+"_"+requestVO.getServiceType()+" Got Interface Category="+serviceInterfaceRoutingVO.getInterfaceType()+" Alternate Check Required="+serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool()+" Alternate Interface="+serviceInterfaceRoutingVO.getAlternateInterfaceType()+" oldDefaultSelector="+oldDefaultSelector+"newDefaultSelector= "+newDefaultSelector);
					}

					type=serviceInterfaceRoutingVO.getInterfaceType();
					oldInterfaceCategory=type;
					oldDefaultSelector=serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode();
					useAlternateCategory=serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool();
					newInterfaceCategory=serviceInterfaceRoutingVO.getAlternateInterfaceType();
					newDefaultSelector=serviceInterfaceRoutingVO.getAlternateDefaultSelectortCode();
				}
				else
				{
					log.info("process",requestIDStr,"Service Interface Routing control Not defined, thus using default type="+((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE)));
					EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"VASVoucherConsController[process]","",senderMSISDN,senderNetworkCode,"Service Interface Routing control Not defined, thus using default type="+((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE)));
					type=((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE));
					//oldDefaultSelector=String.valueOf(SystemPreferences.P2P_TRANSFER_DEF_SELECTOR_CODE);
					//Changed on 27/05/07 for Service Type selector Mapping
					ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_transferVO.getServiceType());
					if(serviceSelectorMappingVO!=null) {
						oldDefaultSelector=serviceSelectorMappingVO.getSelectorCode();
					}

				}			       
			}
			else
			{
				serviceInterfaceRoutingVO=ServiceInterfaceRoutingCache.getServiceInterfaceRoutingDetails(receiverVO.getNetworkCode()+"_"+requestVO.getServiceType()+"_"+senderVO.getSubscriberType());
				if (serviceInterfaceRoutingVO!=null)
				{
					if(log.isDebugEnabled()) {
						log.debug("process",requestIDStr,"For ="+receiverVO.getNetworkCode()+"_"+requestVO.getServiceType()+" Got Interface Category="+serviceInterfaceRoutingVO.getInterfaceType()+" Alternate Check Required="+serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool()+" Alternate Interface="+serviceInterfaceRoutingVO.getAlternateInterfaceType()+" oldDefaultSelector="+oldDefaultSelector+"newDefaultSelector= "+newDefaultSelector);
					}
					oldDefaultSelector=serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode();
				}
				else
				{
					//oldDefaultSelector=String.valueOf(SystemPreferences.P2P_TRANSFER_DEF_SELECTOR_CODE);
					//Changed on 27/05/07 for Service Type selector Mapping
					ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_transferVO.getServiceType());
					if(serviceSelectorMappingVO!=null) {
						oldDefaultSelector=serviceSelectorMappingVO.getSelectorCode();
					}
					log.info("process",requestIDStr,"Service Interface Routing control Not defined, thus using default Selector="+oldDefaultSelector);
					EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,"VASVoucherConsController[process]","",senderMSISDN,senderNetworkCode,"Service Interface Routing control Not defined, thus using default selector="+oldDefaultSelector);
				}
			}			

			if(BTSLUtil.isNullString(requestVO.getReqSelector()))
			{
				if(log.isDebugEnabled()) {
					log.debug("process",requestIDStr,"Selector Not found in Incoming Message Thus using Selector as  "+oldDefaultSelector);
				}
				requestVO.setReqSelector(oldDefaultSelector);
			}
			else
			{
				newDefaultSelector=requestVO.getReqSelector();
			}

		
			requestIDStr=requestVO.getRequestIDStr();
			receiverLocale=requestVO.getReceiverLocale();
			transferID=p2pTransferVO.getTransferID();			
			senderSubscriberType=senderVO.getSubscriberType();
			senderNetworkCode=senderVO.getNetworkCode();
			senderMSISDN=((SubscriberVO)p2pTransferVO.getSenderVO()).getMsisdn();
			receiverMSISDN=((SubscriberVO)p2pTransferVO.getReceiverVO()).getMsisdn();
			senderLocale=requestVO.getSenderLocale();
			receiverLocale=requestVO.getReceiverLocale();			
			serviceType=requestVO.getServiceType();
			senderTransferItemVO=p2pTransferVO.getSenderTransferItemVO();
			receiverTransferItemVO=p2pTransferVO.getReceiverTransferItemVO();
			performIntfceCatRoutingBeforeVal=requestVO.isPerformIntfceCatRoutingBeforeVal();
			receiverDeletionReqFromSubRouting=requestVO.isReceiverDeletionReqFromSubRouting();
			receiverInterfaceInfoInDBFound=requestVO.isReceiverInterfaceInfoInDBFound();
			senderDeletionReqFromSubRouting=requestVO.isSenderDeletionReqFromSubRouting();
			senderInterfaceInfoInDBFound=requestVO.isSenderInterfaceInfoInDBFound();
			interfaceCatRoutingDone=requestVO.isInterfaceCatRoutingDone();

			try
			{
				LoadController.checkTransactionLoad(((SubscriberVO)p2pTransferVO.getSenderVO()).getNetworkCode(),senderTransferItemVO.getInterfaceID(),PretupsI.P2P_MODULE,transferID,true,LoadControllerI.USERTYPE_SENDER);
			}
			catch(BTSLBaseException e)
			{
				//Decreasing interface load of receiver which we had incremented before 27/09/06, sender was decreased in the method
				LoadController.decreaseCurrentInterfaceLoad(transferID,((ReceiverVO)p2pTransferVO.getReceiverVO()).getNetworkCode(),receiverTransferItemVO.getInterfaceID(),LoadControllerI.DEC_LAST_TRANS_COUNT);
				throw e;
			}			

			try
			{
				LoadController.checkTransactionLoad(((SubscriberVO)p2pTransferVO.getReceiverVO()).getNetworkCode(),receiverTransferItemVO.getInterfaceID(),PretupsI.P2P_MODULE,transferID,true,LoadControllerI.USERTYPE_RECEIVER);
			}
			catch(BTSLBaseException e)
			{
				//Decreasing interface load of sender which we had incremented before 27/09/06, receiver was decreased in the method
				LoadController.decreaseTransactionInterfaceLoad(transferID,((SenderVO)p2pTransferVO.getSenderVO()).getNetworkCode(),LoadControllerI.DEC_LAST_TRANS_COUNT);
				throw e;
			}			

			mcomCon = new MComConnection();con=mcomCon.getConnection();
			//Loading receiver's controll parameters
			PretupsBL.loadRecieverControlLimits(con,requestIDStr,p2pTransferVO);
			receiverVO.setUnmarkRequestStatus(true);
			try {con.commit();} catch(Exception e){log.errorTrace(methodName,e);}
			OracleUtil.closeQuietly(con);
			con=null;

			processedFromQueue=true;

			if(log.isDebugEnabled()) {
				log.debug("VASVoucherConsController[processFromQueue]","transferID="+transferID+" Successfully through load");
			}
			processValidationRequest();
			//Set under process message for the sender and reciever
			p_transferVO.setMessageCode(PretupsErrorCodesI.SENDER_UNDERPROCESS_SUCCESS);
			String[] messageArgArray={p_transferVO.getTransferID(),PretupsBL.getDisplayAmount(p_transferVO.getRequestedAmount())};
			p_transferVO.setMessageArguments(messageArgArray);
		}
		catch(BTSLBaseException be)
		{
			log.errorTrace(methodName,be);
			if(con!=null) {
				try {con.close();} catch(Exception e) {log.errorTrace(methodName, e);}
			}
			con=null;			
			try
			{
				if(receiverVO!=null && receiverVO.isUnmarkRequestStatus())
				{
					mcomCon = new MComConnection();con=mcomCon.getConnection();				
					PretupsBL.unmarkReceiverLastRequest(con,requestIDStr,receiverVO);
				}
			}
			catch(BTSLBaseException bex)
			{
				log.errorTrace(methodName, bex);
				//p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);		
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"VASVoucherConsController[processFromQueue]",transferID,senderMSISDN,senderNetworkCode,"Leaving Reciever Unmarked Exception:"+bex.getMessage());
			}
			catch(Exception e)
			{
				log.errorTrace(methodName, e);
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"VASVoucherConsController[processFromQueue]",transferID,senderMSISDN,senderNetworkCode,"Leaving Reciever Unmarked Exception:"+e.getMessage());
				p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			}	

			p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);

			if(be.isKey())
			{
				if(BTSLUtil.isNullString(p2pTransferVO.getErrorCode())) {
					p2pTransferVO.setErrorCode(be.getMessageKey());
				}
				p2pTransferVO.setMessageCode(be.getMessageKey());
				p2pTransferVO.setMessageArguments(be.getArgs());				
				requestVO.setMessageCode(be.getMessageKey());
				requestVO.setMessageArguments(be.getArgs());
			} else {
				p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			}
			LoadController.decreaseTransactionLoad(transferID,senderNetworkCode,LoadControllerI.DEC_LAST_TRANS_COUNT);
			TransactionLog.log(transferID,requestIDStr,requestVO.getFilteredMSISDN(),"",PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,p2pTransferVO.getSenderReturnMessage(),PretupsI.TXN_LOG_STATUS_FAIL,"Getting Code="+requestVO.getMessageCode());

		}
		catch(Exception e)
		{
			log.error(methodName,"Exception:"+e.getMessage());
			log.errorTrace(methodName,e);
			if(con!=null) {
				try {con.close();} catch(Exception ex) {log.errorTrace(methodName, ex);}
			}
			con=null;
			try
			{
				if(receiverVO!=null && receiverVO.isUnmarkRequestStatus())
				{
					mcomCon = new MComConnection();con=mcomCon.getConnection();
					PretupsBL.unmarkReceiverLastRequest(con,requestIDStr,receiverVO);
				}
			}
			catch(BTSLBaseException bex)
			{
				log.errorTrace(methodName, bex);
				p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"VASVoucherConsController[processFromQueue]",transferID,senderMSISDN,senderNetworkCode,"Leaving Reciever Unmarked Exception:"+bex.getMessage());
			}
			catch(Exception ex1)
			{
				log.errorTrace(methodName, ex1);
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"VASVoucherConsController[processFromQueue]",transferID,senderMSISDN,senderNetworkCode,"Leaving Reciever Unmarked Exception:"+ex1.getMessage());
				p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			}	

			if(recValidationFailMessageRequired)
			{
				if(p2pTransferVO.getReceiverReturnMsg()==null || !((BTSLMessages)p2pTransferVO.getReceiverReturnMsg()).isKey())
				{
					if(transferID!=null) {
						p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.P2P_RECEIVER_FAIL,new String[]{String.valueOf(transferID),PretupsBL.getDisplayAmount(p2pTransferVO.getRequestedAmount())}));
					} else {
						p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.P2P_FAIL_R,new String[]{PretupsBL.getDisplayAmount(p2pTransferVO.getRequestedAmount())}));
					}
				}
			}

			p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			p2pTransferVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);

			LoadController.decreaseTransactionLoad(transferID,senderNetworkCode,LoadControllerI.DEC_LAST_TRANS_COUNT);

			TransactionLog.log(transferID,requestIDStr,requestVO.getFilteredMSISDN(),"",PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,p2pTransferVO.getSenderReturnMessage(),PretupsI.TXN_LOG_STATUS_FAIL,"Getting Code="+requestVO.getMessageCode());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VASVoucherConsController[processFromQueue]",transferID,senderMSISDN,senderNetworkCode,"Exception:"+e.getMessage());
		}
		finally
		{
			try
			{
				if(mcomCon==null) {
					mcomCon = new MComConnection();con=mcomCon.getConnection();
				}
				if(transferID!=null && !transferDetailAdded) {
					addEntryInTransfers(con);
				}
			}
			catch(BTSLBaseException be)
			{
				log.errorTrace(methodName,be);
				log.error(methodName,"BTSL Base Exception:"+be.getMessage());
			}
			catch(Exception e)
			{
				log.errorTrace(methodName,e);
				log.error(methodName,"Exception:"+e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"VASVoucherConsController[processFromQueue]",transferID,senderMSISDN,senderNetworkCode,"Exception:"+e.getMessage());
			}
			if(BTSLUtil.isNullString(p2pTransferVO.getMessageCode())) {
				p2pTransferVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
			}
			if(con!=null)
			{
				try {con.commit();} catch(Exception e){log.errorTrace(methodName,e);}
				OracleUtil.closeQuietly(con);
				con=null;
			}
			if(p2pTransferVO.getReceiverReturnMsg()!=null&&((BTSLMessages)p2pTransferVO.getReceiverReturnMsg()).isKey())
			{
				BTSLMessages btslRecMessages=(BTSLMessages)p2pTransferVO.getReceiverReturnMsg();
				(new PushMessage(receiverMSISDN,BTSLUtil.getMessage(receiverLocale,btslRecMessages.getMessageKey(),btslRecMessages.getArgs()),transferID,p2pTransferVO.getRequestGatewayCode(),receiverLocale)).push();
			}
			else if(p2pTransferVO.getReceiverReturnMsg()!=null) {
				(new PushMessage(receiverMSISDN,(String)p2pTransferVO.getReceiverReturnMsg(),transferID,p2pTransferVO.getRequestGatewayCode(),receiverLocale)).push();
			}

			TransactionLog.log(transferID,requestVO.getRequestIDStr(),requestVO.getFilteredMSISDN(),"",PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Leaving the controller after Queue Processing",PretupsI.TXN_LOG_STATUS_SUCCESS,"Getting Code="+requestVO.getMessageCode());
			if(log.isDebugEnabled()) {
				log.debug(methodName,"Exiting");
			}			
		}
	}

	/**
	 * Method to populate transfer VO from request VO
	 * @param p_requestVO
	 */
	private void populateVOFromRequest(RequestVO p_requestVO)
	{
		p2pTransferVO.setSenderVO(senderVO);
		p2pTransferVO.setRequestID(p_requestVO.getRequestIDStr());
		p2pTransferVO.setModule(p_requestVO.getModule());
		p2pTransferVO.setInstanceID(p_requestVO.getInstanceID());
		p2pTransferVO.setRequestGatewayCode(p_requestVO.getRequestGatewayCode());
		p2pTransferVO.setRequestGatewayType(p_requestVO.getRequestGatewayType());
		p2pTransferVO.setServiceType(p_requestVO.getServiceType());
		p2pTransferVO.setSourceType(p_requestVO.getSourceType());
		p2pTransferVO.setCreatedOn(currentDate);
		p2pTransferVO.setCreatedBy(senderVO.getUserID());
		p2pTransferVO.setModifiedOn(currentDate);
		p2pTransferVO.setModifiedBy(senderVO.getUserID());
		p2pTransferVO.setTransferDate(currentDate);
		p2pTransferVO.setTransferDateTime(currentDate);
		p2pTransferVO.setSenderMsisdn(senderVO.getMsisdn());
		p2pTransferVO.setSenderID(senderVO.getUserID());
		p2pTransferVO.setNetworkCode(senderVO.getNetworkCode());
		p2pTransferVO.setLocale(senderLocale);
		p2pTransferVO.setLanguage(p2pTransferVO.getLocale().getLanguage());
		p2pTransferVO.setCountry(p2pTransferVO.getLocale().getCountry());
		p2pTransferVO.setMsgGatewayFlowType(p_requestVO.getMessageGatewayVO().getFlowType());
		p2pTransferVO.setMsgGatewayResponseType(p_requestVO.getMessageGatewayVO().getResponseType());
		p2pTransferVO.setMsgGatewayTimeOutValue(p_requestVO.getMessageGatewayVO().getTimeoutValue());		
	}

	/**
	 * Method to perform validation in thread
	 * @throws BTSLBaseException
	 * @throws Exception
	 */
	private void processValidationRequestInThread() throws BTSLBaseException,Exception
	{
		final String methodName = "processValidationRequestInThread";
		if(log.isDebugEnabled()) {
			log.debug(methodName,"Entered and performing validations for transfer ID="+transferID);
		}  
		try
		{
			TransactionLog.log(transferID,requestIDStr,senderMSISDN,senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Performing Validation in thread",PretupsI.TXN_LOG_STATUS_SUCCESS,"");
			processValidationRequest();
		}
		catch(BTSLBaseException be)
		{
			log.error("VASVoucherConsController[processValidationRequestInThread]","Getting BTSL Base Exception:"+be.getMessage());
			TransactionLog.log(transferID,requestIDStr,senderMSISDN,senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Base Exception while performing Validation in thread",PretupsI.TXN_LOG_STATUS_FAIL,"Getting Code="+be.getMessageKey());
			throw be;
		}
		catch(Exception e)
		{
			log.errorTrace(methodName,e);
			if(recValidationFailMessageRequired)
			{
				if(p2pTransferVO.getReceiverReturnMsg()==null || !((BTSLMessages)p2pTransferVO.getReceiverReturnMsg()).isKey()) {
			p2pTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.P2P_RECEIVER_FAIL),new String[]{String.valueOf(transferID),PretupsBL.getDisplayAmount(p2pTransferVO.getRequestedAmount())}));
				}
			}

			p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			if(BTSLUtil.isNullString(p2pTransferVO.getErrorCode())) {
				p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			}
			log.error(this,transferID,"Exception:"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VASVoucherConsController[run]",transferID,senderMSISDN,senderNetworkCode,"Exception:"+e.getMessage());
			TransactionLog.log(transferID,requestIDStr,senderMSISDN,senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Exception while performing Validation in thread",PretupsI.TXN_LOG_STATUS_FAIL,"Getting exception ="+e.getMessage());
			throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
		}
		finally
		{
			//!transferDetailAdded Condition Added as we think its not require as already done 
			if(transferID!=null && !transferDetailAdded)
			{	
				Connection con=null;MComConnectionI mcomCon = null;
				try
				{
					mcomCon = new MComConnection();con=mcomCon.getConnection();
					addEntryInTransfers(con);
					if(p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
						finalTransferStatusUpdate=false; //No need to update the status of transaction in run method
					}

				}
				catch(BTSLBaseException be)
				{
					log.errorTrace(methodName,be);
					log.error(methodName,"BTSLBaseException:"+be.getMessage());
				}
				catch(Exception e)
				{
					log.errorTrace(methodName,e);
					log.error(methodName,"Exception:"+e.getMessage());
					EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VASVoucherConsController[process]",transferID,senderMSISDN,senderNetworkCode,"Exception:"+e.getMessage());
				}
				finally
				{
					if(con!=null) {
						try{con.close();}catch(Exception e){log.errorTrace(methodName, e);}
					}
					con=null;
				}
			}
			if(log.isDebugEnabled()) {
				log.debug(methodName,"Exiting");
			}
		}		
	}


	
	/**
	 *  Method to get the under process message before validation to be sent to sender
	 * @return
	 */
	private String getSndrUPMsgBeforeValidation()
	{
		String[] messageArgArray={receiverMSISDN,transferID,PretupsBL.getDisplayAmount(p2pTransferVO.getRequestedAmount())};
		return BTSLUtil.getMessage(senderLocale,PretupsErrorCodesI.P2P_SENDER_UNDERPROCESS_B4VAL,messageArgArray);
	}

	/**
	 * Method to get the success message to be sent to sender
	 * @return
	 */
	private String getSenderUnderProcessMessage()
	{
		String[] messageArgArray={receiverMSISDN,transferID,PretupsBL.getDisplayAmount(p2pTransferVO.getRequestedAmount()),PretupsBL.getDisplayAmount(p2pTransferVO.getSenderTransferValue()),PretupsBL.getDisplayAmount(p2pTransferVO.getSenderAccessFee())};
		return BTSLUtil.getMessage(senderLocale,PretupsErrorCodesI.P2P_SENDER_UNDERPROCESS,messageArgArray);
	}
	/**
	 * Method that will add entry in Transfer Table if not added else update the records
	 * @param p_con
	 */
	private void addEntryInTransfers(Connection p_con)
	{
		String methodName="addEntryInTransfers";
		try
		{
			//METHOD FOR INSERTING AND UPDATION IN P2P Transfer Table
			if(!transferDetailAdded) {
				PretupsBL.addVoucherTransferDetails(p_con,p2pTransferVO);//add transfer details in database
			} else if(transferDetailAdded)
			{
				p2pTransferVO.setModifiedOn(new Date());
				p2pTransferVO.setModifiedBy(p2pTransferVO.getSenderID());
				PretupsBL.updateTransferDetails(p_con,p2pTransferVO);//add transfer details in database
			}
			p_con.commit();
		}
		catch(BTSLBaseException be)
		{
			log.errorTrace(methodName,be);
			if(!isCounterDecreased  && decreaseTransactionCounts)
			{
				LoadController.decreaseTransactionLoad(transferID,senderNetworkCode,LoadControllerI.DEC_LAST_TRANS_COUNT);
				isCounterDecreased=true;
			}			
			log.error("addEntryInTransfers",transferID,"BTSLBaseException while adding transfer details in database:"+be.getMessage());
		}
		catch(Exception e)
		{
			log.errorTrace(methodName,e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VASVoucherConsController[process]",transferID,senderMSISDN,senderNetworkCode,"Exception:"+e.getMessage());
			if(!isCounterDecreased  && decreaseTransactionCounts)
			{
				LoadController.decreaseTransactionLoad(transferID,senderNetworkCode,LoadControllerI.DEC_LAST_TRANS_COUNT);
				isCounterDecreased=true;
			}
			log.error(methodName,transferID,"Exception while adding transfer details in database:"+e.getMessage());
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
	private boolean getInterfaceRoutingDetails(Connection p_con,String p_msisdn,long p_prefixID,String p_subscriberType,String p_networkCode,String p_serviceType,String p_interfaceCategory,String p_userType,String p_action) throws BTSLBaseException
	{
		final String methodName = "getInterfaceRoutingDetails";
		if(log.isDebugEnabled()) {
			log.debug(methodName,requestIDStr," Entered with MSISDN="+p_msisdn+" Prefix ID="+p_prefixID+" p_subscriberType="+p_subscriberType+" p_networkCode="+p_networkCode+" p_serviceType="+p_serviceType+" p_interfaceCategory="+p_interfaceCategory+" p_userType="+p_userType+" p_action="+p_action);
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
		performIntfceCatRoutingBeforeVal=false; //Set so that receiver flag is not overridden by sender flag
		SubscriberRoutingControlVO subscriberRoutingControlVO=SubscriberRoutingControlCache.getRoutingControlDetails(p_networkCode+"_"+p_serviceType+"_"+p_interfaceCategory);
		try
		{
			if(subscriberRoutingControlVO!=null)
			{
				if(log.isDebugEnabled()) {
					log.debug(methodName,transferID," p_userType="+p_userType+" Database Check Required="+subscriberRoutingControlVO.isDatabaseCheckBool()+" Series Check Required="+subscriberRoutingControlVO.isSeriesCheckBool());
				}

				if(subscriberRoutingControlVO.isDatabaseCheckBool())
				{
					if(p_interfaceCategory.equalsIgnoreCase(PretupsI.INTERFACE_CATEGORY_PRE))
					{
						ListValueVO listValueVO=PretupsBL.validateNumberInRoutingDatabase(p_con,p_msisdn,p_interfaceCategory);
						if(listValueVO!=null)
						{
							isSuccess=true;							
							setInterfaceDetails(p_prefixID,p_userType,listValueVO,false,null,null);

							if(p_userType.equals(PretupsI.USER_TYPE_SENDER) && p_action.equals(PretupsI.INTERFACE_VALIDATE_ACTION))
							{
								senderInterfaceInfoInDBFound=true;
								senderDeletionReqFromSubRouting=true;
							}
							else if(p_userType.equals(PretupsI.USER_TYPE_RECEIVER) && p_action.equals(PretupsI.INTERFACE_VALIDATE_ACTION))
							{
								receiverInterfaceInfoInDBFound=true;
								receiverDeletionReqFromSubRouting=true;
							}
						}
						else if(subscriberRoutingControlVO.isSeriesCheckBool())
						{
							if(log.isDebugEnabled()) {
								log.debug(methodName,transferID," p_userType="+p_userType+" MSISDN ="+p_msisdn+" not found in Database , performing Series Check for Prefix ID="+p_prefixID);
							}
							// service selector based checks added 							
							ServiceSelectorInterfaceMappingVO interfaceMappingVO1=null;
							MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null; 
							if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue())
							{
								interfaceMappingVO1=(ServiceSelectorInterfaceMappingVO)ServiceSelectorInterfaceMappingCache.getObject(serviceType+"_"+p2pTransferVO.getSubService()+"_"+p_action+"_"+p_networkCode+"_"+p_prefixID);
							}
							if(interfaceMappingVO1==null)
							{	
								try
								{
									interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_prefixID, serviceType,p_action);
									isSuccess=true;
									setInterfaceDetails(p_prefixID,p_userType,null,true,interfaceMappingVO,null);
								}
								catch(BTSLBaseException be)
								{
									if(be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.MSISDN_PREFIX_INTERFACE_MAPPING_NOTFOUND))) {
										performIntfceCatRoutingBeforeVal=true;
									} else {
										throw be;
									}
								}
							}	
							else
							{
								isSuccess=true;
								setInterfaceDetails(p_prefixID,p_userType,null,true,null,interfaceMappingVO1);
							}
						}
						else
						{
							performIntfceCatRoutingBeforeVal=true;
							isSuccess=false;
						}
					}
					else if(p_interfaceCategory.equalsIgnoreCase(PretupsI.INTERFACE_CATEGORY_POST))
					{
						WhiteListVO whiteListVO=PretupsBL.validateNumberInWhiteList(p_con,p_msisdn);
						if(whiteListVO!=null)
						{
							isSuccess=true;
							ListValueVO listValueVO=whiteListVO.getListValueVO();
							interfaceID=listValueVO.getValue();
							interfaceHandlerClass=listValueVO.getLabel();
							underProcessMsgReqd=listValueVO.getType();
							allServiceClassID=listValueVO.getTypeName();
							externalID=listValueVO.getIDValue();

							if(p_userType.equals(PretupsI.USER_TYPE_SENDER))
							{
								senderTransferItemVO.setInterfaceID(interfaceID);
								senderTransferItemVO.setInterfaceHandlerClass(interfaceHandlerClass);
								senderAllServiceClassID=allServiceClassID;
							
								//Mark the Post Paid Interface as Online
								senderVO.setPostOfflineInterface(true);

								senderTransferItemVO.setPreviousBalance(whiteListVO.getCreditLimit());
								senderVO.setCreditLimit(whiteListVO.getCreditLimit());
								senderTransferItemVO.setReferenceID(whiteListVO.getAccountID());
								senderTransferItemVO.setAccountStatus(whiteListVO.getAccountStatus());
								senderTransferItemVO.setPrefixID(p_prefixID);
								senderTransferItemVO.setServiceClassCode(whiteListVO.getServiceClassCode());
								if(p_action.equals(PretupsI.INTERFACE_VALIDATE_ACTION)) {
									senderInterfaceInfoInDBFound=true;
								}
							}
							else
							{
								receiverTransferItemVO.setPrefixID(p_prefixID);
								receiverTransferItemVO.setInterfaceID(interfaceID);
								receiverTransferItemVO.setInterfaceHandlerClass(interfaceHandlerClass);
								if(PretupsI.YES.equals(underProcessMsgReqd)) {
									p2pTransferVO.setUnderProcessMsgReq(true);
								}
								receiverAllServiceClassID=allServiceClassID;
								receiverExternalID=externalID;
								receiverVO.setPostOfflineInterface(true);

								receiverTransferItemVO.setPreviousBalance(whiteListVO.getCreditLimit());
								receiverTransferItemVO.setServiceClassCode(whiteListVO.getServiceClassCode());
								receiverTransferItemVO.setReferenceID(whiteListVO.getAccountID());
								receiverTransferItemVO.setAccountStatus(whiteListVO.getAccountStatus());
								if(p_action.equals(PretupsI.INTERFACE_VALIDATE_ACTION)) {
									receiverInterfaceInfoInDBFound=true;
								}
							}
							if(!PretupsI.YES.equals(listValueVO.getStatus()))
							{
								//ChangeID=LOCALEMASTER
								//which language message to be set is determined from the locale master table for the requested locale
								if(PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(senderLocale)).getMessage())) {
									p2pTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo());
								} else {
									p2pTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo2());
								}
								throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.INTERFACE_NOT_ACTIVE);
							}							
						}
						else if(subscriberRoutingControlVO.isSeriesCheckBool())
						{
							if(log.isDebugEnabled()) {
								log.debug(methodName,transferID," p_userType="+p_userType+" MSISDN ="+p_msisdn+" not found in Database , performing Series Check for Prefix ID="+p_prefixID);
							}

							MSISDNPrefixInterfaceMappingVO interfaceMappingVO=null;
							//check service selector based check loading of interface 
							ServiceSelectorInterfaceMappingVO interfaceMappingVO1=null;
							if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue())
							{
								interfaceMappingVO1=(ServiceSelectorInterfaceMappingVO)ServiceSelectorInterfaceMappingCache.getObject(serviceType+"_"+p2pTransferVO.getSubService()+"_"+p_action+"_"+p_networkCode+"_"+p_prefixID);
							}
							if(interfaceMappingVO1==null)
							{	
								try
								{
									interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_prefixID, serviceType,p_action);
									isSuccess=true;	
									setInterfaceDetails(p_prefixID,p_userType,null,true,interfaceMappingVO,null);
								}
								catch(BTSLBaseException be)
								{
									if(be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.MSISDN_PREFIX_INTERFACE_MAPPING_NOTFOUND))) {
										performIntfceCatRoutingBeforeVal=true;
									} else {
										throw be;
									}
								}
							}
							else
							{
								isSuccess=true;	
								setInterfaceDetails(p_prefixID,p_userType,null,true,null,interfaceMappingVO1);
							}
						}
						else
						{
							isSuccess=false;
							performIntfceCatRoutingBeforeVal=true;
						}
					}
				}
				else if(subscriberRoutingControlVO.isSeriesCheckBool())
				{
					if(log.isDebugEnabled()) {
						log.debug(methodName,transferID," p_userType="+p_userType+" MSISDN ="+p_msisdn+" performing Series Check for Prefix ID="+p_prefixID);
					}

					MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null;
					//check service selector based check loading of interface 
					ServiceSelectorInterfaceMappingVO interfaceMappingVO1=null;
					if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue())
					{
						interfaceMappingVO1=(ServiceSelectorInterfaceMappingVO)ServiceSelectorInterfaceMappingCache.getObject(serviceType+"_"+p2pTransferVO.getSubService()+"_"+p_action+"_"+p_networkCode+"_"+p_prefixID);
					}
					if(interfaceMappingVO1==null)
					{	
						try
						{
							interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_prefixID, serviceType,p_action);
							isSuccess=true;
							setInterfaceDetails(p_prefixID,p_userType,null,true,interfaceMappingVO,null);
						}
						catch(BTSLBaseException be)
						{
							if(be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.MSISDN_PREFIX_INTERFACE_MAPPING_NOTFOUND))) {
								performIntfceCatRoutingBeforeVal=true;
							} else {
								throw be;
							}
						}
					}
					else
					{	isSuccess=true;
					setInterfaceDetails(p_prefixID,p_userType,null,true,null,interfaceMappingVO1);
					}
				} else {
					isSuccess=false;
				}
			}
			else
			{
				if(log.isDebugEnabled()) {
					log.debug(methodName,transferID," By default carrying out series check as routing control not defined for p_userType="+p_userType+" MSISDN ="+p_msisdn+" performing Series Check for Prefix ID="+p_prefixID);
				}
				//This event is raised by ankit Z on date 3/8/06 for case when entry not found in routing control and considering series based routing
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,"VASVoucherConsController[getInterfaceRoutingDetails]",transferID,senderMSISDN,senderNetworkCode,"Exception:Routing control information not defined so performing series based routing");

				MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null; 
				//check service selector based check loading of interface 
				ServiceSelectorInterfaceMappingVO interfaceMappingVO1=null;
				if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue())
				{
					interfaceMappingVO1=(ServiceSelectorInterfaceMappingVO)ServiceSelectorInterfaceMappingCache.getObject(serviceType+"_"+p2pTransferVO.getSubService()+"_"+p_action+"_"+p_networkCode+"_"+p_prefixID);
				}
				if(interfaceMappingVO1==null)
				{	
					try
					{	interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_prefixID, serviceType,p_action);
					isSuccess=true;
					setInterfaceDetails(p_prefixID,p_userType,null,true,interfaceMappingVO,null);
					}
					catch(BTSLBaseException be)
					{
						if(be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.MSISDN_PREFIX_INTERFACE_MAPPING_NOTFOUND))) {
							performIntfceCatRoutingBeforeVal=true;
						} else {
							throw be;
						}
					}
				}
				else
				{
					isSuccess=true;
					setInterfaceDetails(p_prefixID,p_userType,null,true,null,interfaceMappingVO1);
				}
			}

			if(isSuccess && p_userType.equals(PretupsI.USER_TYPE_SENDER)) {
				senderTransferItemVO.setInterfaceType(type);
			} else if(isSuccess && p_userType.equals(PretupsI.USER_TYPE_RECEIVER))
			{
				receiverTransferItemVO.setInterfaceType(type);
				receiverTransferItemVO.setSubscriberType(type);				
			}
		}
		catch(BTSLBaseException be)
		{
			if(isSuccess && p_userType.equals(PretupsI.USER_TYPE_SENDER)) {
				senderTransferItemVO.setInterfaceType(p_interfaceCategory);
			} else if(isSuccess && p_userType.equals(PretupsI.USER_TYPE_RECEIVER))
			{
				receiverTransferItemVO.setInterfaceType(type);
				receiverTransferItemVO.setSubscriberType(type);				
			}
			throw be;
		}
		catch(Exception e)
		{
			log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VASVoucherConsController[getInterfaceRoutingDetails]",transferID,senderMSISDN,senderNetworkCode,"Exception:"+e.getMessage());
			isSuccess=false;
			throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
		}
		if(log.isDebugEnabled()) {
			log.debug(methodName,requestIDStr," Exiting with isSuccess="+isSuccess+ "senderAllServiceClassID="+senderAllServiceClassID+" receiverAllServiceClassID="+receiverAllServiceClassID);
		}
		return isSuccess;
	}

	/**
	 * Method: updateReceiverLocale
	 * This method update the receiver locale with the language code returned from the IN
	 *  
	 * @param p_languageCode String
	 * @return void
	 */
	public void updateReceiverLocale(String p_languageCode)
	{
		final String methodName = "updateReceiverLocale";
		if(log.isDebugEnabled()) {
			log.debug(methodName,"Entered p_languageCode="+p_languageCode);
		}
		//check if language is returned fron IN or not.
		//If not then send alarm and not set the locale
		//otherwise set the local corresponding to the code returned from the IN.
		if(!BTSLUtil.isNullString(p_languageCode))
		{
			try
			{
				if(LocaleMasterCache.getLocaleFromCodeDetails(p_languageCode)==null) {
					EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"VASVoucherConsController[updateReceiverLocale]",transferID,receiverMSISDN,"","Exception: Notification language returned from IN is not defined in system p_languageCode: "+p_languageCode);
				} else {
					receiverLocale=(LocaleMasterCache.getLocaleFromCodeDetails(p_languageCode));
				}
			}
			catch(Exception e){log.errorTrace(methodName, e);}
		}
		if(log.isDebugEnabled()) {
			log.debug(methodName,"Exited receiverLocale="+receiverLocale);
		}
	}

	/**
	 * This method will perform the alternate interface category routing if there 
	 * This method will be called either after validation or after performing interface routing
	 * @throws BTSLBaseException
	 */
	public void performAlternateCategoryRouting() throws BTSLBaseException
	{
		final String methodName = "performAlternateCategoryRouting";
		if(log.isDebugEnabled()) {
			log.debug(methodName,"Performing ALternate interface category routing Entered");
		}  
		Connection con=null;MComConnectionI mcomCon = null;
		try
		{
			String requestStr=null;
			CommonClient commonClient=null;
			String receiverValResponse=null;

			LoadController.decreaseResponseCounters(transferID,PretupsErrorCodesI.TXN_STATUS_FAIL,LoadControllerI.RECEIVER_VAL_RESPONSE);
			LoadController.decreaseReceiverTransactionInterfaceLoad(transferID,p2pTransferVO.getReceiverNetworkCode(),LoadControllerI.DEC_LAST_TRANS_COUNT);

			mcomCon = new MComConnection();con=mcomCon.getConnection();

			//populates the alternate interface category details
			populateAlternateInterfaceDetails(con);

			if(con!=null)
			{
				try { con.rollback(); } catch(Exception be) {log.errorTrace(methodName,be);}
				try { con.close(); } catch(Exception be) {log.errorTrace(methodName,be);}
				con=null;
			}
			p2pTransferVO.setTransferCategory(senderSubscriberType+"-"+type);
			if(log.isDebugEnabled()) {
				log.debug("process",requestIDStr,"Overriding transfer Category as :"+p2pTransferVO.getTransferCategory());
			}

			p2pTransferVO.setReceiverAllServiceClassID(receiverAllServiceClassID);

			checkTransactionLoad(PretupsI.USER_TYPE_RECEIVER,receiverTransferItemVO.getInterfaceID());

			//validate receiver limits before Interface Validations
			PretupsBL.validateRecieverLimits(null,p2pTransferVO,PretupsI.TRANS_STAGE_BEFORE_INVAL,PretupsI.P2P_MODULE);

			requestStr=getReceiverValidateStr();
			commonClient=new CommonClient();

			LoadController.incrementTransactionInterCounts(transferID,LoadControllerI.RECEIVER_UNDER_VAL);

			TransactionLog.log(transferID,requestIDStr,receiverMSISDN,receiverVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_TXNSTAGE_INVAL,requestStr,PretupsI.TXN_LOG_STATUS_SUCCESS,"Performing Alternate Category Routing");

			receiverValResponse=commonClient.process(requestStr,transferID,intModCommunicationTypeS,intModIPS,intModPortS,intModClassNameS);

			TransactionLog.log(transferID,requestIDStr,receiverMSISDN,receiverVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_INVAL,receiverValResponse,PretupsI.TXN_LOG_STATUS_SUCCESS,"");

			handleReceiverValidateResponse(receiverValResponse,SRC_AFTER_INRESP_CAT_ROUTING);
			if(InterfaceErrorCodesI.SUCCESS.equals(receiverTransferItemVO.getValidationStatus()))
			{
				//If mobile number found on Post but previously was defined in PRE then delete the number 
				if(newInterfaceCategory.equals(PretupsI.INTERFACE_CATEGORY_POST))
				{
					if(receiverDeletionReqFromSubRouting) {
						PretupsBL.deleteSubscriberInterfaceRouting(receiverMSISDN,oldInterfaceCategory);
					}
				}
				else
				{
					//Update in DB for routing interface 
					SubscriberRoutingControlVO subscriberRoutingControlVO=SubscriberRoutingControlCache.getRoutingControlDetails(p2pTransferVO.getReceiverNetworkCode()+"_"+p2pTransferVO.getServiceType()+"_"+newInterfaceCategory);
					if(!receiverDeletionReqFromSubRouting && subscriberRoutingControlVO!=null && subscriberRoutingControlVO.isDatabaseCheckBool())
					{
						if(log.isDebugEnabled()) {
							log.debug(methodName,"Inserting the MSISDN="+receiverMSISDN+" in Subscriber routing database for further usage");
						}  

						PretupsBL.insertSubscriberInterfaceRouting(receiverTransferItemVO.getInterfaceID(),receiverExternalID,receiverMSISDN,newInterfaceCategory,senderVO.getUserID(),currentDate);
						receiverInterfaceInfoInDBFound=true;
						receiverDeletionReqFromSubRouting=true;
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
			log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VASVoucherConsController[performAlternateCategoryRouting]",transferID,senderMSISDN,senderNetworkCode,"Exception:"+e.getMessage());
			throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
		}
		finally
		{
			if(con!=null)
			{
				try { con.rollback(); } catch(Exception be) {log.errorTrace(methodName,be);}
				try { con.close(); } catch(Exception be) {log.errorTrace(methodName,be);}
				con=null;
			}			
		}
	}

	/**
	 * Method to populate the Alternate Interface Details for the Receiver against the new interface category
	 * @throws BTSLBaseException
	 */
	public void populateAlternateInterfaceDetails(Connection p_con) throws BTSLBaseException
	{
		if(log.isDebugEnabled()) {
			log.debug("populateAlternateInterfaceDetails","Entered to get the alternate category");
		}  

		boolean isReceiverFound=false;

		if(!interfaceCatRoutingDone)
		{
			interfaceCatRoutingDone=true;
			type=newInterfaceCategory;
			networkPrefixVO=null;

			requestVO.setReqSelector(newDefaultSelector);
			p2pTransferVO.setSubService(newDefaultSelector);

			//Load the new prefix ID against the interface category , If Not required then give the error

			if(log.isDebugEnabled()) {
				log.debug("populateAlternateInterfaceDetails","Got the alternate category as ="+type);
			}  

			networkPrefixVO=(NetworkPrefixVO)NetworkPrefixCache.getObject(receiverVO.getMsisdnPrefix(),type);
			if(networkPrefixVO!=null)
			{
				if(log.isDebugEnabled()) {
					log.debug("populateAlternateInterfaceDetails","Got the Prefix ID for MSISDN="+receiverMSISDN+ "Prefix ID="+networkPrefixVO.getPrefixID());
				}  

				receiverVO.setNetworkCode(networkPrefixVO.getNetworkCode());
				receiverVO.setPrefixID(networkPrefixVO.getPrefixID());
				receiverVO.setSubscriberType(networkPrefixVO.getSeriesType());
				isReceiverFound=getInterfaceRoutingDetails(p_con,receiverMSISDN,receiverVO.getPrefixID(),receiverVO.getSubscriberType(),receiverVO.getNetworkCode(),p2pTransferVO.getServiceType(),type,PretupsI.USER_TYPE_RECEIVER,PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION);
			}
			else
			{
				log.error(this,"Series Not Defined for Alternate Interface ="+type+" For Series="+receiverVO.getMsisdnPrefix());
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,"VASVoucherConsController[populateAlternateInterfaceDetails]","","","","Series ="+receiverVO.getMsisdnPrefix()+" Not Defined for Series type="+type+" But required for validation");
				isReceiverFound=false;
			}

			if(!isReceiverFound) {
				throw new BTSLBaseException("VASVoucherConsController","populateServicePaymentInterfaceDetails",PretupsErrorCodesI.P2P_NOTFOUND_SERVICEINTERFACEMAPPING);
			}
		}
	}
	/**
	 * This method handles the receiver validate response after sending request to IN
	 * @param str
	 * @param p_source
	 * @throws BTSLBaseException
	 */
	public void handleReceiverValidateResponse(String str,int p_source) throws BTSLBaseException
	{
		HashMap map=BTSLUtil.getStringToHash(str,"&","=");
		String status=(String)map.get("TRANSACTION_STATUS");
		ArrayList altList=null;
		boolean isRequired=false;

		//If we get the MSISDN not found on interface error then perform interface routing
		if(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status))
		{
			receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			altList=InterfaceRoutingControlCache.getRoutingControlDetails(receiverTransferItemVO.getInterfaceID());
			if(altList!=null && !altList.isEmpty()) {
				performReceiverAlternateRouting(altList,p_source);
			} else {
				isRequired=true;
			}
		}
		if(!InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) || isRequired)
		{
			populateReceiverItemsDetails(map);
			//For Service Provider Information
			receiverTransferItemVO.setServiceProviderName(BTSLUtil.NullToString((String)map.get("SPNAME")));
		}
	}	

	/**
	 * Method to perform the Interface routing for the subscriber MSISDN
	 * @param altList
	 * @param p_source: Determines whether Alternate category needs to be performed after this or not
	 * @throws BTSLBaseException
	 */
	private void performReceiverAlternateRouting(ArrayList altList,int p_source) throws BTSLBaseException
	{
		final String methodName = "performReceiverAlternateRouting";
		if(log.isDebugEnabled()) {
			log.debug(methodName,requestIDStr," Entered p_source="+p_source);
		}
		try
		{
			if(altList!=null && !altList.isEmpty())
			{
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
				switch (altList.size())
				{
				case 1: 
				{
					LoadController.decreaseResponseCounters(transferID,PretupsErrorCodesI.TXN_STATUS_FAIL,LoadControllerI.RECEIVER_VAL_RESPONSE);
					LoadController.decreaseReceiverTransactionInterfaceLoad(transferID,p2pTransferVO.getReceiverNetworkCode(),LoadControllerI.DEC_LAST_TRANS_COUNT);

					listValueVO=(ListValueVO)altList.get(0);

					setInterfaceDetails(receiverTransferItemVO.getPrefixID(),PretupsI.USER_TYPE_RECEIVER,listValueVO,false,null,null);

					checkTransactionLoad(PretupsI.USER_TYPE_RECEIVER,receiverTransferItemVO.getInterfaceID());

					//validate receiver limits before Interface Validations
					PretupsBL.validateRecieverLimits(null,p2pTransferVO,PretupsI.TRANS_STAGE_BEFORE_INVAL,PretupsI.P2P_MODULE);

					requestStr=getReceiverValidateStr();
					commonClient=new CommonClient();

					LoadController.incrementTransactionInterCounts(transferID,LoadControllerI.RECEIVER_UNDER_VAL);

					TransactionLog.log(transferID,requestIDStr,receiverMSISDN,receiverVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_TXNSTAGE_INVAL,requestStr,PretupsI.TXN_LOG_STATUS_SUCCESS,"Performing Interface Routing 1");

					if(log.isDebugEnabled()) {
						log.debug(methodName,"Sending Request For MSISDN="+receiverMSISDN+" on ALternate Routing 1 to ="+receiverTransferItemVO.getInterfaceID());
					}  

					receiverValResponse=commonClient.process(requestStr,transferID,intModCommunicationTypeS,intModIPS,intModPortS,intModClassNameS);

					TransactionLog.log(transferID,requestIDStr,receiverMSISDN,receiverVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_INVAL,receiverValResponse,PretupsI.TXN_LOG_STATUS_SUCCESS,"");

					try
					{
						receiverValidateResponse(receiverValResponse,1,altList.size(),p_source);
						//If source is before IN validation then if interface is pre then we need to update in subscriber 
						//Routing but after alternate routing if number is found on another interface
						//Then we need to delete the number from subscriber Routing or Vice versa
						if(p_source==SRC_BEFORE_INRESP_CAT_ROUTING)
						{
							if(PretupsI.INTERFACE_CATEGORY_PRE.equals(type) && InterfaceErrorCodesI.SUCCESS.equals(receiverTransferItemVO.getValidationStatus()))
							{
								//Update in DB for routing interface 
								updateSubscriberRoutingDetails(PretupsI.USER_TYPE_RECEIVER,p2pTransferVO.getReceiverNetworkCode(),receiverTransferItemVO.getInterfaceID(),receiverExternalID,receiverMSISDN,type,senderVO.getUserID(),currentDate);
							}
						}
						else
						{
							if(InterfaceErrorCodesI.SUCCESS.equals(receiverTransferItemVO.getValidationStatus()))
							{
								if(newInterfaceCategory.equals(PretupsI.INTERFACE_CATEGORY_POST))
								{
									if(receiverDeletionReqFromSubRouting) {
										PretupsBL.deleteSubscriberInterfaceRouting(receiverMSISDN,oldInterfaceCategory);
									}
								}
								else
								{
									//Update in DB for routing interface 
									SubscriberRoutingControlVO subscriberRoutingControlVO=SubscriberRoutingControlCache.getRoutingControlDetails(p2pTransferVO.getReceiverNetworkCode()+"_"+p2pTransferVO.getServiceType()+"_"+newInterfaceCategory);
									if(!receiverDeletionReqFromSubRouting && subscriberRoutingControlVO!=null && subscriberRoutingControlVO.isDatabaseCheckBool())
									{
										PretupsBL.insertSubscriberInterfaceRouting(receiverTransferItemVO.getInterfaceID(),receiverExternalID,receiverMSISDN,newInterfaceCategory,senderVO.getUserID(),currentDate);
										receiverInterfaceInfoInDBFound=true;
										receiverDeletionReqFromSubRouting=true;
									}
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
						throw new BTSLBaseException(this, methodName, "");
					}

					break;
				}
				case 2:
				{	
					LoadController.decreaseResponseCounters(transferID,PretupsErrorCodesI.TXN_STATUS_FAIL,LoadControllerI.RECEIVER_VAL_RESPONSE);
					LoadController.decreaseReceiverTransactionInterfaceLoad(transferID,p2pTransferVO.getReceiverNetworkCode(),LoadControllerI.DEC_LAST_TRANS_COUNT);

					listValueVO=(ListValueVO)altList.get(0);

					setInterfaceDetails(receiverTransferItemVO.getPrefixID(),PretupsI.USER_TYPE_RECEIVER,listValueVO,false,null,null);

					checkTransactionLoad(PretupsI.USER_TYPE_RECEIVER,receiverTransferItemVO.getInterfaceID());

					//validate receiver limits before Interface Validations
					PretupsBL.validateRecieverLimits(null,p2pTransferVO,PretupsI.TRANS_STAGE_BEFORE_INVAL,PretupsI.P2P_MODULE);

					requestStr=getReceiverValidateStr();
					commonClient=new CommonClient();

					LoadController.incrementTransactionInterCounts(transferID,LoadControllerI.RECEIVER_UNDER_VAL);

					TransactionLog.log(transferID,requestIDStr,receiverMSISDN,receiverVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_TXNSTAGE_INVAL,requestStr,PretupsI.TXN_LOG_STATUS_SUCCESS,"Performing Interface Routing 1");

					if(log.isDebugEnabled()) {
						log.debug(methodName,"Sending Request For MSISDN="+receiverMSISDN+" on ALternate Routing 1 to ="+receiverTransferItemVO.getInterfaceID());
					}  

					receiverValResponse=commonClient.process(requestStr,transferID,intModCommunicationTypeS,intModIPS,intModPortS,intModClassNameS);

					TransactionLog.log(transferID,requestIDStr,receiverMSISDN,receiverVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_INVAL,receiverValResponse,PretupsI.TXN_LOG_STATUS_SUCCESS,"");

					try
					{
						receiverValidateResponse(receiverValResponse,1,altList.size(),p_source);
						//If source is before IN validation then if interface is pre then we need to update in subscriber 
						//Routing but after alternate routing if number is found on another interface
						//Then we need to delete the number from subscriber Routing or Vice versa

						if(p_source==SRC_BEFORE_INRESP_CAT_ROUTING)
						{
							if(PretupsI.INTERFACE_CATEGORY_PRE.equals(type) && InterfaceErrorCodesI.SUCCESS.equals(receiverTransferItemVO.getValidationStatus()))
							{
								//Update in DB for routing interface 
								updateSubscriberRoutingDetails(PretupsI.USER_TYPE_RECEIVER,p2pTransferVO.getReceiverNetworkCode(),receiverTransferItemVO.getInterfaceID(),receiverExternalID,receiverMSISDN,type,senderVO.getUserID(),currentDate);
							}
						}
						else
						{
							if(InterfaceErrorCodesI.SUCCESS.equals(receiverTransferItemVO.getValidationStatus()))
							{
								if(newInterfaceCategory.equals(PretupsI.INTERFACE_CATEGORY_POST))
								{
									if(receiverDeletionReqFromSubRouting) {
										PretupsBL.deleteSubscriberInterfaceRouting(receiverMSISDN,oldInterfaceCategory);
									}
								}
								else
								{
									//Update in DB for routing interface 
									SubscriberRoutingControlVO subscriberRoutingControlVO=SubscriberRoutingControlCache.getRoutingControlDetails(p2pTransferVO.getReceiverNetworkCode()+"_"+p2pTransferVO.getServiceType()+"_"+newInterfaceCategory);
									if(!receiverDeletionReqFromSubRouting && subscriberRoutingControlVO!=null && subscriberRoutingControlVO.isDatabaseCheckBool())
									{
										PretupsBL.insertSubscriberInterfaceRouting(receiverTransferItemVO.getInterfaceID(),receiverExternalID,receiverMSISDN,newInterfaceCategory,senderVO.getUserID(),currentDate);
										receiverInterfaceInfoInDBFound=true;
										receiverDeletionReqFromSubRouting=true;
									}
								}
							}
						}							
					}
					catch(BTSLBaseException be)
					{
						if(be.isKey() && InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(be.getMessageKey()))
						{	
							if(log.isDebugEnabled()) {
								log.debug(methodName,"Got Status="+InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND +" After validation Request For MSISDN="+receiverMSISDN+" Performing Alternate Routing to 2");
							}

							LoadController.decreaseResponseCounters(transferID,PretupsErrorCodesI.TXN_STATUS_FAIL,LoadControllerI.RECEIVER_VAL_RESPONSE);
							LoadController.decreaseReceiverTransactionInterfaceLoad(transferID,p2pTransferVO.getReceiverNetworkCode(),LoadControllerI.DEC_LAST_TRANS_COUNT);

							listValueVO=(ListValueVO)altList.get(1);

							setInterfaceDetails(receiverTransferItemVO.getPrefixID(),PretupsI.USER_TYPE_RECEIVER,listValueVO,false,null,null);

							checkTransactionLoad(PretupsI.USER_TYPE_RECEIVER,receiverTransferItemVO.getInterfaceID());

							//validate receiver limits before Interface Validations
							PretupsBL.validateRecieverLimits(null,p2pTransferVO,PretupsI.TRANS_STAGE_BEFORE_INVAL,PretupsI.P2P_MODULE);

							requestStr=getReceiverValidateStr();

							LoadController.incrementTransactionInterCounts(transferID,LoadControllerI.RECEIVER_UNDER_VAL);

							TransactionLog.log(transferID,requestIDStr,receiverMSISDN,receiverVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_TXNSTAGE_INVAL,requestStr,PretupsI.TXN_LOG_STATUS_SUCCESS,"Performing Interface Routing 2");

							if(log.isDebugEnabled()) {
								log.debug(methodName,"Sending Request For MSISDN="+receiverMSISDN+" on ALternate Routing 2 to ="+receiverTransferItemVO.getInterfaceID());
							}  

							receiverValResponse=commonClient.process(requestStr,transferID,intModCommunicationTypeS,intModIPS,intModPortS,intModClassNameS);

							TransactionLog.log(transferID,requestIDStr,receiverMSISDN,receiverVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_INVAL,receiverValResponse,PretupsI.TXN_LOG_STATUS_SUCCESS,"");

							try
							{
								receiverValidateResponse(receiverValResponse,2,altList.size(),p_source);
								//If source is before IN validation then if interface is pre then we need to update in subscriber 
								//Routing but after alternate routing if number is found on another interface
								//Then we need to delete the number from subscriber Routing or Vice versa

								if(p_source==SRC_BEFORE_INRESP_CAT_ROUTING)
								{
									if(PretupsI.INTERFACE_CATEGORY_PRE.equals(type) && InterfaceErrorCodesI.SUCCESS.equals(receiverTransferItemVO.getValidationStatus()))
									{
										//Update in DB for routing interface 
										updateSubscriberRoutingDetails(PretupsI.USER_TYPE_RECEIVER,p2pTransferVO.getReceiverNetworkCode(),receiverTransferItemVO.getInterfaceID(),receiverExternalID,receiverMSISDN,type,senderVO.getUserID(),currentDate);
									}
								}
								else
								{
									if(InterfaceErrorCodesI.SUCCESS.equals(receiverTransferItemVO.getValidationStatus()))
									{
										if(newInterfaceCategory.equals(PretupsI.INTERFACE_CATEGORY_POST))
										{
											if(receiverDeletionReqFromSubRouting) {
												PretupsBL.deleteSubscriberInterfaceRouting(receiverMSISDN,oldInterfaceCategory);
											}
										}
										else
										{
											//Update in DB for routing interface 
											SubscriberRoutingControlVO subscriberRoutingControlVO=SubscriberRoutingControlCache.getRoutingControlDetails(p2pTransferVO.getReceiverNetworkCode()+"_"+p2pTransferVO.getServiceType()+"_"+newInterfaceCategory);
											if(!receiverDeletionReqFromSubRouting && subscriberRoutingControlVO!=null && subscriberRoutingControlVO.isDatabaseCheckBool())
											{
												PretupsBL.insertSubscriberInterfaceRouting(receiverTransferItemVO.getInterfaceID(),receiverExternalID,receiverMSISDN,newInterfaceCategory,senderVO.getUserID(),currentDate);
												receiverInterfaceInfoInDBFound=true;
												receiverDeletionReqFromSubRouting=true;
											}
										}
									}
								}									
							}
							catch(BTSLBaseException bex)
							{
								throw bex;
							}
							catch(Exception e)
							{
								throw new BTSLBaseException(this, methodName, "");
							}
						}
						else 
						{
							throw be;
						}
					}
					catch(Exception e)
					{
						throw new BTSLBaseException(this, methodName, "");
					}
					break;
				}
				}

			} else {
				return;
			}
		}
		catch(BTSLBaseException be)
		{
			throw be;
		}
		catch(Exception e)
		{
			log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VASVoucherConsController[performAlternateRouting]",transferID,senderMSISDN,senderNetworkCode,"Exception:"+e.getMessage());
			throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
		}
		if(log.isDebugEnabled()) {
			log.debug(methodName,requestIDStr," Exiting ");
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
	public void receiverValidateResponse(String str,int p_attempt,int p_altSize,int p_source) throws BTSLBaseException
	{
		final String methodName="receiverValidateResponse";
		HashMap map=BTSLUtil.getStringToHash(str,"&","=");
		String status=(String)map.get("TRANSACTION_STATUS");

		//Start: Update the Interface table for the interface ID based on Handler status and update the Cache
		String interfaceStatusType=(String)map.get("INT_SET_STATUS");
		if(!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME.equals(interfaceStatusType))) {
			new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES,receiverTransferItemVO.getInterfaceID(),interfaceStatusType,PretupsErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG,PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
			//:End
		}

		if(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) && p_attempt==1 && p_attempt<p_altSize)
		{
			receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			throw new BTSLBaseException(this,"receiverValidateResponse",InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
		}
		else if(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) && p_attempt==p_altSize && p_source==SRC_BEFORE_INRESP_CAT_ROUTING && useAlternateCategory && !interfaceCatRoutingDone)
		{
			if(log.isDebugEnabled()) {
				log.debug(this," Performing Alternate category routing as MSISDN not found on any interfaces after routing for "+receiverMSISDN);
			}
			performAlternateCategoryRouting();
		}
		else
		{
			if("Y".equals(requestVO.getUseInterfaceLanguage()))
			{
				//update the receiver locale if language code returned from IN is not null
				updateReceiverLocale((String)map.get("IN_LANG"));
			}
			receiverTransferItemVO.setProtocolStatus((String)map.get("PROTOCOL_STATUS"));
			receiverTransferItemVO.setAccountStatus((String)map.get("ACCOUNT_STATUS"));
			receiverTransferItemVO.setInterfaceResponseCode((String)map.get("INTERFACE_STATUS"));
			receiverTransferItemVO.setValidationStatus(status);
			receiverVO.setInterfaceResponseCode(receiverTransferItemVO.getInterfaceResponseCode());		
			receiverTransferItemVO.setInterfaceReferenceID((String)map.get("IN_TXN_ID"));
			receiverTransferItemVO.setReferenceID((String)map.get("IN_RECON_ID"));		

			//If status is other than Success in validation stage mark sender request as Not applicable and
			//Make transaction Fail
			String [] strArr=null;

			if(BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS))
			{
				p2pTransferVO.setErrorCode(status+"_R");
				receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
				receiverTransferItemVO.setTransferStatus(status);
				senderTransferItemVO.setValidationStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
				senderTransferItemVO.setTransferStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
				senderVO.setTransactionStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
				strArr=new String[]{receiverMSISDN,PretupsBL.getDisplayAmount(p2pTransferVO.getRequestedAmount()),transferID};
				throw new BTSLBaseException("VASVoucherConsController","receiverValidateResponse",PretupsErrorCodesI.P2P_SENDER_FAIL,0,strArr,null);
			}
			receiverTransferItemVO.setTransferStatus(status);
			receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
			receiverTransferItemVO.setSubscriberType(type);
			receiverVO.setSubscriberType(type);

			try{receiverTransferItemVO.setPreviousExpiry(BTSLUtil.getDateFromDateString((String)map.get("OLD_EXPIRY_DATE"),"ddMMyyyy"));}catch(Exception e){log.errorTrace(methodName,e);}
			try{receiverTransferItemVO.setPreviousGraceDate(BTSLUtil.getDateFromDateString((String)map.get("OLD_GRACE_DATE"),"ddMMyyyy"));}catch(Exception e){log.errorTrace(methodName,e);}
			receiverVO.setInterfaceResponseCode(receiverTransferItemVO.getInterfaceResponseCode());

			receiverTransferItemVO.setFirstCall((String)map.get("FIRST_CALL"));
			receiverTransferItemVO.setGraceDaysStr((String)map.get("GRACE_DAYS"));

			receiverTransferItemVO.setServiceClassCode((String)map.get("SERVICE_CLASS"));

			//Done so that receiver check can be brough to common
			receiverVO.setServiceClassCode(receiverTransferItemVO.getServiceClass());

			try{receiverTransferItemVO.setPreviousBalance(Long.parseLong((String)map.get("INTERFACE_PREV_BALANCE")));}catch(Exception e){log.errorTrace(methodName,e);}

			//Update the Previous Balance in case of Post Paid Offline interface with Credit Limit - Monthly Transfer Amount
			if(receiverVO.isPostOfflineInterface())
			{
				boolean isPeriodChange=BTSLUtil.isPeriodChangeBetweenDates(receiverVO.getLastSuccessOn(),currentDate,BTSLUtil.PERIOD_MONTH);
				if(!isPeriodChange) {
					receiverTransferItemVO.setPreviousBalance(receiverTransferItemVO.getPreviousBalance()-receiverVO.getMonthlyTransferAmount());
				}
			}	

			//TO DO Done for testing purpose should we use it or give exception in this case
			if(receiverTransferItemVO.getPreviousExpiry()==null) {
				receiverTransferItemVO.setPreviousExpiry(currentDate);
			}
		}
	}	

	/**
	 * This method will populate the receiver Items VO after the response from interfaces
	 * @param p_map
	 * @throws BTSLBaseException
	 */
	public void populateReceiverItemsDetails(HashMap p_map) throws BTSLBaseException
	{
		final String methodName="populateReceiverItemsDetails";
		String status=(String)p_map.get("TRANSACTION_STATUS");
		//receiver language has to be taken from IN then the block below will execute
		if("Y".equals(requestVO.getUseInterfaceLanguage()))
		{
			//update the receiver locale if language code returned from IN is not null
			updateReceiverLocale((String)p_map.get("IN_LANG"));
		}
		receiverTransferItemVO.setProtocolStatus((String)p_map.get("PROTOCOL_STATUS"));
		receiverTransferItemVO.setAccountStatus((String)p_map.get("ACCOUNT_STATUS"));
		receiverTransferItemVO.setInterfaceResponseCode((String)p_map.get("INTERFACE_STATUS"));
		receiverTransferItemVO.setValidationStatus(status);
		receiverVO.setInterfaceResponseCode(receiverTransferItemVO.getInterfaceResponseCode());		

		if(!BTSLUtil.isNullString((String)p_map.get("IN_TXN_ID")))
		{
			try{receiverTransferItemVO.setInterfaceReferenceID((String)p_map.get("IN_TXN_ID"));}
			catch(Exception e){log.errorTrace(methodName,e);EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"VASVoucherConsController[updateForReceiverValidateResponse]",transferID,senderMSISDN,senderNetworkCode,"Exception while parsing for interface txn ID , Exception:"+e.getMessage());}
		}
		receiverTransferItemVO.setReferenceID((String)p_map.get("IN_RECON_ID"));

		String [] strArr=null;

		if(BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS))
		{
			p2pTransferVO.setErrorCode(status+"_R");
			receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			receiverTransferItemVO.setTransferStatus(status);

			senderVO.setTransactionStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
			strArr=new String[]{receiverMSISDN,PretupsBL.getDisplayAmount(p2pTransferVO.getRequestedAmount()),transferID};
			//throw new BTSLBaseException("VASVoucherConsController","updateForReceiverValidateResponse",PretupsErrorCodesI.P2P_SENDER_FAIL,0,strArr,null);
			//throw new BTSLBaseException("VASVoucherConsController","populateReceiverItemsDetails",p2pTransferVO.getErrorCode(),0,strArr,null);
			if(InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED_P2P.equals(receiverTransferItemVO.getValidationStatus())) {
				throw new BTSLBaseException("VASVoucherConsController","populateReceiverItemsDetails",InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED_P2P+"_S",0,strArr,null);
			} else {
				throw new BTSLBaseException("VASVoucherConsController","populateReceiverItemsDetails",p2pTransferVO.getErrorCode(),0,strArr,null);
			}
		}
		receiverTransferItemVO.setTransferStatus(status);
		receiverTransferItemVO.setSubscriberType(type);
		receiverVO.setSubscriberType(type);
		p2pTransferVO.setRequestedAmount(Long.parseLong(String.valueOf(p_map.get("TALK_TIME"))));
		receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
		SubscriberRoutingControlVO subscriberRoutingControlVO=SubscriberRoutingControlCache.getRoutingControlDetails(p2pTransferVO.getReceiverNetworkCode()+"_"+p2pTransferVO.getServiceType()+"_"+type);
		if(PretupsI.INTERFACE_CATEGORY_PRE.equals(type) && !receiverDeletionReqFromSubRouting && subscriberRoutingControlVO!=null && subscriberRoutingControlVO.isDatabaseCheckBool())
		{
			PretupsBL.insertSubscriberInterfaceRouting(receiverTransferItemVO.getInterfaceID(),receiverExternalID,receiverMSISDN,type,senderVO.getUserID(),currentDate);
			receiverInterfaceInfoInDBFound=true;
			receiverDeletionReqFromSubRouting=true;
		}

		try{receiverTransferItemVO.setPreviousExpiry(BTSLUtil.getDateFromDateString((String)p_map.get("OLD_EXPIRY_DATE"),"ddMMyyyy"));}catch(Exception e){log.errorTrace(methodName,e);}
		try{receiverTransferItemVO.setPreviousGraceDate(BTSLUtil.getDateFromDateString((String)p_map.get("OLD_GRACE_DATE"),"ddMMyyyy"));}catch(Exception e){log.errorTrace(methodName,e);}
		receiverTransferItemVO.setFirstCall((String)p_map.get("FIRST_CALL"));
		receiverTransferItemVO.setGraceDaysStr((String)p_map.get("GRACE_DAYS"));

		receiverTransferItemVO.setServiceClassCode((String)p_map.get("SERVICE_CLASS"));
		receiverTransferItemVO.setOldExporyInMillis((String)p_map.get("CAL_OLD_EXPIRY_DATE"));//@nu

		try{receiverTransferItemVO.setPreviousBalance(Long.parseLong((String)p_map.get("INTERFACE_PREV_BALANCE")));}catch(Exception e){log.errorTrace(methodName,e);}

		receiverTransferItemVO.setBundleTypes((String)p_map.get("BUNDLE_TYPES"));
		receiverTransferItemVO.setBonusBundleValidities((String)p_map.get("BONUS_BUNDLE_VALIDITIES"));

		//Update the Previous Balance in case of Post Paid Offline interface with Credit Limit - Monthly Transfer Amount
		if(receiverVO.isPostOfflineInterface())
		{
			boolean isPeriodChange=BTSLUtil.isPeriodChangeBetweenDates(receiverVO.getLastSuccessOn(),currentDate,BTSLUtil.PERIOD_MONTH);
			if(!isPeriodChange) {
				receiverTransferItemVO.setPreviousBalance(receiverTransferItemVO.getPreviousBalance()-receiverVO.getMonthlyTransferAmount());
			}
		}		
		//TO DO Done for testing purpose should we use it or give exception in this case
		if(receiverTransferItemVO.getPreviousExpiry()==null) {
			receiverTransferItemVO.setPreviousExpiry(currentDate);
		}

		try{receiverTransferItemVO.setLmbdebitvalue((Long.valueOf((String)p_map.get("LMB_ALLOWED_VALUE"))));}catch(Exception e){log.errorTrace(methodName,e);log.error("populateReceiverItemsDetails", "Exception e"+e);}
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
	private void setInterfaceDetails(long p_prefixID,String p_userType,ListValueVO p_listValueVO,boolean p_useInterfacePrefixVO,MSISDNPrefixInterfaceMappingVO p_MSISDNPrefixInterfaceMappingVO,ServiceSelectorInterfaceMappingVO p_serviceSelectorInterfaceMappingVO) throws BTSLBaseException
	{
		if(log.isDebugEnabled()) {
			log.debug("setInterfaceDetails",requestIDStr," Entered p_prefixID="+p_prefixID+" p_listValueVO="+p_listValueVO+" p_useInterfacePrefixVO="+p_useInterfacePrefixVO+" p_MSISDNPrefixInterfaceMappingVO="+p_MSISDNPrefixInterfaceMappingVO+"p_serviceSelectorInterfaceMappingVO"+p_serviceSelectorInterfaceMappingVO);
		}
		try
		{
			String interfaceID=null;
			String interfaceHandlerClass=null;
			String underProcessMsgReqd=null;
			String allServiceClassID=null;
			String externalID=null;
			String status=null;
			String message1=null;
			String message2=null;
			String interfaceStatusTy=null;
			String receiverInterfaceStatusType=null;
			if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue()&&p_serviceSelectorInterfaceMappingVO!=null)
			{
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
			if(p_useInterfacePrefixVO&&p_serviceSelectorInterfaceMappingVO==null)
			{
				interfaceID = p_MSISDNPrefixInterfaceMappingVO.getInterfaceID();
				interfaceHandlerClass= p_MSISDNPrefixInterfaceMappingVO.getHandlerClass();
				underProcessMsgReqd=p_MSISDNPrefixInterfaceMappingVO.getUnderProcessMsgRequired();
				allServiceClassID=p_MSISDNPrefixInterfaceMappingVO.getAllServiceClassID();
				externalID=p_MSISDNPrefixInterfaceMappingVO.getExternalID();
				status=p_MSISDNPrefixInterfaceMappingVO.getInterfaceStatus();
				message1=p_MSISDNPrefixInterfaceMappingVO.getLanguage1Message();
				message2=p_MSISDNPrefixInterfaceMappingVO.getLanguage2Message();
				interfaceStatusTy=p_MSISDNPrefixInterfaceMappingVO.getStatusType();
			}
			else if(p_serviceSelectorInterfaceMappingVO==null)
			{
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


			if(p_userType.equals(PretupsI.USER_TYPE_SENDER))
			{
				senderTransferItemVO.setPrefixID(p_prefixID);
				senderTransferItemVO.setInterfaceID(interfaceID);
				senderTransferItemVO.setInterfaceHandlerClass(interfaceHandlerClass);
				senderAllServiceClassID=allServiceClassID;
				
				senderInterfaceStatusType=interfaceStatusTy;
				p2pTransferVO.setSenderAllServiceClassID(senderAllServiceClassID);
				p2pTransferVO.setSenderInterfaceStatusType(senderInterfaceStatusType);

			}
			else if(p_userType.equals(PretupsI.USER_TYPE_RECEIVER))
			{
				receiverTransferItemVO.setPrefixID(p_prefixID);
				receiverTransferItemVO.setInterfaceID(interfaceID);
				receiverTransferItemVO.setInterfaceHandlerClass(interfaceHandlerClass);
				if(PretupsI.YES.equals(underProcessMsgReqd)) {
					p2pTransferVO.setUnderProcessMsgReq(true);
				}
				receiverAllServiceClassID=allServiceClassID;
				receiverExternalID=externalID;
				receiverInterfaceStatusType=interfaceStatusTy;
				p2pTransferVO.setReceiverAllServiceClassID(receiverAllServiceClassID);
				p2pTransferVO.setReceiverInterfaceStatusType(receiverInterfaceStatusType);
			}
			//Check if interface status is Active or not.

			if(!PretupsI.YES.equals(status)  && PretupsI.INTERFACE_STATUS_TYPE_MANUAL.equals(interfaceStatusTy))
			{
				//ChangeID=LOCALEMASTER
				//which language message to be set is determined from the locale master table for the requested locale

				if(PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(senderLocale)).getMessage())) {
					p2pTransferVO.setSenderReturnMessage(message1);
				} else {
					p2pTransferVO.setSenderReturnMessage(message2);
				}
				throw new BTSLBaseException(this,"setInterfaceDetails",PretupsErrorCodesI.INTERFACE_NOT_ACTIVE);
			}
		}
		catch(BTSLBaseException be)
		{
			log.error("setInterfaceDetails","Getting Base Exception ="+be.getMessage());
			throw be;
		}
		catch(Exception e)
		{
			final String methodName = "performReceiverAlternateRouting";
			log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VASVoucherConsController[setInterfaceDetails]",transferID,senderMSISDN,senderNetworkCode,"Exception:"+e.getMessage());
			throw new BTSLBaseException(this,"setInterfaceDetails",PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
		}
		finally
		{
			if(log.isDebugEnabled()) {
				log.debug("setInterfaceDetails",requestIDStr," Exiting with Sender Interface ID="+senderTransferItemVO.getInterfaceID()+" Receiver Interface="+receiverTransferItemVO.getInterfaceID());
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
	private void updateSubscriberRoutingDetails(String p_userType,String p_networkCode,String p_interfaceID,String p_externalID,String p_msisdn,String p_interfaceCategory,String p_userID,Date p_currentDate) throws BTSLBaseException
	{
		final String methodName = "updateSubscriberRoutingDetails";
		if(log.isDebugEnabled()) {
			log.debug(methodName,requestIDStr," Entered p_userType="+p_userType+" p_networkCode="+p_networkCode+" p_interfaceID="+p_interfaceID+" p_externalID="+p_externalID+" p_msisdn="+p_msisdn+" p_interfaceCategory="+p_interfaceCategory+" p_userID="+p_userID+" p_currentDate="+p_currentDate);
		}
		try
		{
			boolean updationReqd=false;
			if(PretupsI.USER_TYPE_SENDER.equals(p_userType)) {
				updationReqd=senderDeletionReqFromSubRouting;
			} else {
				updationReqd=receiverDeletionReqFromSubRouting;
			}

			if(updationReqd) {
				PretupsBL.updateSubscriberInterfaceRouting(p_interfaceID,p_externalID,p_msisdn,p_interfaceCategory,p_userID,p_currentDate);
			} else 
			{
				SubscriberRoutingControlVO subscriberRoutingControlVO=SubscriberRoutingControlCache.getRoutingControlDetails(p_networkCode+"_"+p2pTransferVO.getServiceType()+"_"+p_interfaceCategory);
				if(!updationReqd && subscriberRoutingControlVO!=null && subscriberRoutingControlVO.isDatabaseCheckBool())
				{
					PretupsBL.insertSubscriberInterfaceRouting(p_interfaceID,p_externalID,p_msisdn,p_interfaceCategory,p_userID,p_currentDate);
					if(PretupsI.USER_TYPE_SENDER.equals(p_userType))
					{
						senderInterfaceInfoInDBFound=true;
						senderDeletionReqFromSubRouting=true;
					}
					else
					{
						receiverInterfaceInfoInDBFound=true;
						receiverDeletionReqFromSubRouting=true;						
					}
				}
			}

		}
		catch(BTSLBaseException be)
		{
			log.error(methodName,"Getting Base Exception ="+be.getMessage());
			throw be;
		}
		catch(Exception e)
		{
			log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VASVoucherConsController[updateSubscriberRoutingDetails]",transferID,senderMSISDN,senderNetworkCode,"Exception:"+e.getMessage());
			throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
		}
		finally
		{
			if(log.isDebugEnabled()) {
				log.debug(methodName,requestIDStr," Exiting ");
			}
		}
	}


	public static synchronized void generateTransferID(TransferVO p_transferVO) throws BTSLBaseException
	{
		//if(log.isDebugEnabled()) log.debug("generateTransferID","Entered ");
		String transferID=null;
		Date mydate = null;
		String minut2Compare=null;
		final SimpleDateFormat sdfCompare = new SimpleDateFormat ("mm");
		final String methodName = "generateTransferID";
		try
		{
			//ReceiverVO receiverVO=(ReceiverVO)p_transferVO.getReceiverVO();
			//newTransferID=IDGenerator.getNextID(PretupsI.ID_GEN_P2P_TRANSFER_NO,BTSLUtil.getFinancialYearLastDigits(4),receiverVO.getNetworkCode(),p_transferVO.getCreatedOn());
			mydate = new Date();
			p_transferVO.setCreatedOn(mydate);
			minut2Compare = sdfCompare.format(mydate);
			int currentMinut=Integer.parseInt(minut2Compare);  		

			if(currentMinut !=prevMinut)
			{
				transactionIDCounter=1;
				prevMinut=currentMinut;

			}
			else
			{
				transactionIDCounter++;

			}

			if(transactionIDCounter==0) {
				throw new BTSLBaseException("VASVoucherConsController",methodName,PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
			}
			transferID=operatorUtil.formatVoucherTransferID(p_transferVO,transactionIDCounter,"V");
			if(transferID==null) {
				throw new BTSLBaseException("VASVoucherConsController",methodName,PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
			}
			p_transferVO.setTransferID(transferID);			
		}
		catch(BTSLBaseException be)
		{
			log.errorTrace(methodName, be);
			throw be;
		}
		catch(Exception e)
		{
			log.errorTrace(methodName, e);
			throw new BTSLBaseException("VASVoucherConsController",methodName,PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
		}
	}

	/**updateForVOMSValidationResponse
	 * Method to process the response of the receiver validation from VOMS
	 * @param str
	 * @throws BTSLBaseException
	 */

	public void updateForVOMSValidationResponse(String str) throws BTSLBaseException
	{
		final String methodName="updateForVOMSValidationResponse";
		if(log.isDebugEnabled()) {
			log.debug(methodName,"Entered");
		}
		HashMap map=BTSLUtil.getStringToHash(str,"&","=");
		String status=(String)map.get("TRANSACTION_STATUS");
		senderTransferItemVO.setInterfaceResponseCode((String)map.get("INTERFACE_STATUS"));
		receiverVO.setInterfaceResponseCode(senderTransferItemVO.getInterfaceResponseCode());		
		senderTransferItemVO.setValidationStatus(status);
		senderTransferItemVO.setInterfaceReferenceID((String)map.get("IN_TXN_ID"));
		
		
		//If status is other than Success in validation stage mark sender request as Not applicable and
		//Make transaction Fail
		String [] strArr=null;
		if(BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS))
		{
			p2pTransferVO.setErrorCode(status+"_S");
			senderTransferItemVO.setTransferStatus(status);
			strArr=new String[]{transferID,Long.toString(p2pTransferVO.getRequestedAmount())};
			throw new BTSLBaseException("EVDController",methodName,p2pTransferVO.getErrorCode(),0,strArr,null);
		}
		senderTransferItemVO.setTransferStatus(status);
		receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
	
		
		receiverTransferItemVO.setServiceClassCode((String)map.get("SERVICE_CLASS"));
		senderTransferItemVO.setServiceClassCode((String)map.get("SERVICE_CLASS"));
		receiverTransferItemVO.setServiceClass((String)map.get("SERVICE_CLASS"));
		senderTransferItemVO.setServiceClass((String)map.get("SERVICE_CLASS"));
		senderTransferItemVO.setInterfaceType(receiverTransferItemVO.getInterfaceType());
		
		
		SubscriberRoutingControlVO subscriberRoutingControlVO=SubscriberRoutingControlCache.getRoutingControlDetails(p2pTransferVO.getReceiverNetworkCode()+"_"+p2pTransferVO.getServiceType()+"_"+PretupsI.INTERFACE_CATEGORY_VOMS);
		if(!vomsInterfaceInfoInDBFound && subscriberRoutingControlVO!=null && subscriberRoutingControlVO.isDatabaseCheckBool())
		{
			PretupsBL.insertSubscriberInterfaceRouting(senderTransferItemVO.getInterfaceID(),vomsExternalID,receiverMSISDN,PretupsI.INTERFACE_CATEGORY_VOMS,senderVO.getUserID(),currentDate);
			vomsInterfaceInfoInDBFound=true;
		}
		
		if(!BTSLUtil.isNullString((String)map.get("SUBSCRIBER_CELL_SWITCH_ID_REQ")) && "Y".equalsIgnoreCase((String)map.get("SUBSCRIBER_CELL_SWITCH_ID_REQ"))){
			
			if(!BTSLUtil.isNullString((String)map.get("SUBSCRIBER_CELL_ID")))
			p2pTransferVO.setCellId((String)map.get("SUBSCRIBER_CELL_ID"));
		
			if(!BTSLUtil.isNullString((String)map.get("SUBSCRIBER_SWITCH_ID")))
			p2pTransferVO.setSwitchId((String)map.get("SUBSCRIBER_SWITCH_ID"));
		}
	}

	private void creditBackSenderForFailedTrans() throws BTSLBaseException
	{
		final String methodName = "creditBackSenderForFailedTrans";
		if(log.isDebugEnabled()) {
			log.debug(methodName,"","Entered with p_onlyDecreaseOnly=");
		}
		Connection con=null;MComConnectionI mcomCon = null;
		try
		{
			//TransactionLog.log(transferID,requestIDStr,senderMSISDN,senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Credit Back Sender",PretupsI.TXN_LOG_STATUS_SUCCESS,"Transfer Status="+p2pTransferVO.getTransferStatus()+" Credit Back Allowed="+SystemPreferences.P2P_SNDR_CREDIT_BACK_ALLOWED+" Credit in Ambigous ="+((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_SNDR_CREDIT_BK_AMB_STATUS))).booleanValue());
			mcomCon = new MComConnection();con=mcomCon.getConnection();
				SubscriberBL.decreaseTransferOutCounts(con,p2pTransferVO);
				p2pTransferVO.setModifiedOn(currentDate);
				p2pTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
				PretupsBL.updateTransferDetails(con,p2pTransferVO);
				con.commit();
		}
		catch(BTSLBaseException be)
		{
			if(con!=null){try{con.rollback();}catch(Exception e){log.errorTrace(methodName,e);}}
			//EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VchrConsController[creditBackSenderForFailedTrans]",transferID,senderMSISDN,senderNetworkCode,"Exception:"+be.getMessage());
			throw be;
		}
		catch(Exception e)
		{
			if(con!=null){try{con.rollback();}catch(Exception ex){log.errorTrace(methodName,ex);}}
			
			//EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VchrConsController[creditBackSenderForFailedTrans]",transferID,senderMSISDN,senderNetworkCode,"Exception:"+e.getMessage());
			throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
		}
		finally
		{
			if(con!=null){try{con.close();}catch(Exception e){log.errorTrace(methodName, e);}}
		}
		
	}

}
