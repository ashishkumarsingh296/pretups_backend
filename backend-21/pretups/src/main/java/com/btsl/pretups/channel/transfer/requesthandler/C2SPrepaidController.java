package com.btsl.pretups.channel.transfer.requesthandler;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
import com.btsl.pretups.adjustments.businesslogic.AdjustmentsVO;
import com.btsl.pretups.adjustments.businesslogic.DiffCalBL;
import com.btsl.pretups.adjustments.businesslogic.PromoBonusCalBL;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupBL;
import com.btsl.pretups.channel.logging.BalanceLogger;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferItemVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.currencyconversion.businesslogic.CurrencyConversionCache;
import com.btsl.pretups.currencyconversion.businesslogic.CurrencyConversionVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.grouptype.businesslogic.GroupTypeProfileVO;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.util.InterfaceCloserI;
import com.btsl.pretups.interfaces.businesslogic.InterfaceVO;
import com.btsl.pretups.logging.ChannelRequestDailyLog;
import com.btsl.pretups.logging.OneLineTXNLog;
import com.btsl.pretups.logging.SMSChargingLog;
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyBL;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyVO;
import com.btsl.pretups.loyaltymgmt.businesslogic.PromotionDetailsVO;
import com.btsl.pretups.loyaltymgmt.transfer.requesthandler.LoyaltyController;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.NetworkServiceVO;
import com.btsl.pretups.master.businesslogic.NetworkServicesCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.network.businesslogic.MSISDNPrefixInterfaceMappingCache;
import com.btsl.pretups.network.businesslogic.MSISDNPrefixInterfaceMappingVO;
import com.btsl.pretups.network.businesslogic.NetworkInterfaceModuleCache;
import com.btsl.pretups.network.businesslogic.NetworkInterfaceModuleVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.privaterecharge.businesslogic.PrivateRchrgDAO;
import com.btsl.pretups.privaterecharge.businesslogic.PrivateRchrgVO;
import com.btsl.pretups.processes.ResumeSuspendProcess;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberBL;
import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberVO;
import com.btsl.pretups.routing.master.businesslogic.InterfaceRoutingControlCache;
import com.btsl.pretups.routing.master.businesslogic.SubscriberRoutingControlCache;
import com.btsl.pretups.routing.master.businesslogic.SubscriberRoutingControlVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.sos.businesslogic.SOSVO;
import com.btsl.pretups.sos.requesthandler.SOSSettlementController;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.user.businesslogic.ChannelSoSVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.pretups.vastrix.businesslogic.ServiceSelectorInterfaceMappingCache;
import com.btsl.pretups.vastrix.businesslogic.ServiceSelectorInterfaceMappingVO;
import com.btsl.redis.util.RedisUtil;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.XmlTagValueConstant;
import com.client.pretups.gateway.businesslogic.USSDPushMessage;
import com.txn.pretups.sos.businesslogic.SOSTxnDAO;
import com.btsl.user.businesslogic.UserLoanVO;

public class C2SPrepaidController extends C2SBaseController implements ServiceKeywordControllerI, Runnable {
	private static Log _log = LogFactory.getLog(C2SPrepaidController.class.getName());
	protected C2STransferVO _c2sTransferVO=null;
	protected TransferItemVO _senderTransferItemVO=null;
	protected TransferItemVO _receiverTransferItemVO=null;
	protected String _senderMSISDN;
	protected String _receiverMSISDN;
	protected ChannelUserVO _channelUserVO;
	protected ReceiverVO _receiverVO;
	private String _senderSubscriberType;
	protected String _senderNetworkCode;
	private Date _currentDate = null;
	protected long _requestID;
	protected String _requestIDStr;
	protected String _transferID;
	protected ArrayList _itemList=null;
	protected String _intModCommunicationTypeS;
	protected String _intModIPS;
	protected int _intModPortS;
	protected String _intModClassNameS;
	protected boolean _transferDetailAdded=false;
	protected boolean _BalanceDetailAdded=false;
	protected boolean _BalanceDetailAddedPenalty=false;
	protected boolean _BalanceDetailAddedPenaltyRoam=false;
	protected boolean _penaltyAdded=false;
	protected boolean _roampenaltyAdded=false;
	private boolean _isCounterDecreased = false;
	protected String _type;
	private String _serviceType;
	private boolean _finalTransferStatusUpdate = true;
	private boolean _transferEntryReqd = false;
	private boolean _decreaseTransactionCounts = false;
	protected UserBalancesVO _userBalancesVO = null;
	protected boolean _creditBackEntryDone = false;
	private boolean _receiverInterfaceInfoInDBFound = false;
	private String _receiverAllServiceClassID = PretupsI.ALL;
	private String _receiverPostBalanceAvailable;
	private Locale _senderLocale = null;
	protected Locale _receiverLocale = null;
	private String _externalID = null;
	protected RequestVO _requestVO=null;
	protected boolean _processedFromQueue=false;
	protected boolean _recValidationFailMessageRequired=false;
	private boolean _recTopupFailMessageRequired = false;
	private final String _notAllowedSendMessGatw;
	protected String _notAllowedRecSendMessGatw;
	private String _receiverSubscriberType = null;
	private static OperatorUtilI _operatorUtil = null;
	private String _interfaceStatusType = null;
	private static int _transactionIDCounter = 0;
	private static int _prevMinut = 0;
	
	protected boolean _receiverMessageSendReq=false;
	private boolean _ussdReceiverMessageSendReq=false;
	private String _receiverBundleID = null;
	private String _selectorName = "";
	private String _senderPushMessageMsisdn = null;
	private final String _RecAlternetGatewaySMS;
	private String _receiverNetworkCode = null;
	protected boolean _oneLog=true;
	private boolean penaltyApplicable = false;
	private boolean penaltyApplicableOwner = false;
	private AdjustmentsVO _penaltyVODebit = null;
	private AdjustmentsVO _penaltyVOCredit = null;
	private AdjustmentsVO _penaltyVODebitOwner = null;
	private AdjustmentsVO _penaltyVOCreditOwner = null;
	private C2STransferVO c2STransferOwnerVO = null;
	private UserBalancesVO userBalanceOwnerVO = null;
	private DiffCalBL _diffCalBL = null;
	private boolean stopAdditionalCommission = false;
	private boolean _subValRequired=true;
	private static ExecutorService executorZB = null;
	private static ExecutorService executorLB = null;
	private static boolean fnfAllowed=false;
	private static boolean lowBasedBallowed=false;
	private String senderReturnMessage= null;
	
	static {
		final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
		try {
			_operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
			fnfAllowed=(Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.FNF_ZB_ALLOWED);
			lowBasedBallowed=(Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LOW_BASED_ALLOWED);
			if(fnfAllowed)
			{
				try {
					executorZB = Executors.newFixedThreadPool((new Integer(Constants.getProperty("THREADPOOLEXE_POLLSIZE_ZB")).intValue()));
				} catch (Exception e) {
					executorZB = Executors.newFixedThreadPool(30);
					_log.errorTrace("static", e);
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SPrepaidController[thread pool initialize]", "", "", "", "Exception while initilizing the thread pool :" + e.getMessage());
				}	
			}
			if(lowBasedBallowed)
			{
				try {
					executorLB = Executors.newFixedThreadPool((new Integer(Constants.getProperty("THREADPOOLEXE_POLLSIZE_LB")).intValue()));
				} catch (Exception e) {
					executorLB = Executors.newFixedThreadPool(30);
					_log.errorTrace("static", e);
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SPrepaidController[thread pool initialize]", "", "", "", "Exception while initilizing the thread pool :" + e.getMessage());
				}	

			}
		} catch (Exception e) {
			_log.errorTrace("static", e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SPrepaidController[initialize]", "", "", "",
					"Exception while loading the class at the call:" + e.getMessage());
		}
	}

	public C2SPrepaidController() {
		_c2sTransferVO = new C2STransferVO();
		_currentDate = new Date();
		if ("Y".equals(BTSLUtil.NullToString(Constants.getProperty("C2S_REC_GEN_FAIL_MSG_REQD_V")))) {
			_recValidationFailMessageRequired = true;
		}
		if ("Y".equals(BTSLUtil.NullToString(Constants.getProperty("C2S_REC_GEN_FAIL_MSG_REQD_T")))) {
			_recTopupFailMessageRequired = true;
		}
		_notAllowedSendMessGatw = BTSLUtil.NullToString(Constants.getProperty("C2S_SEN_MSG_NOT_REQD_GW"));
		_notAllowedRecSendMessGatw = BTSLUtil.NullToString(Constants.getProperty("C2S_REC_MSG_NOT_REQD_GW"));
		_RecAlternetGatewaySMS = BTSLUtil.NullToString(Constants.getProperty("C2S_REC_MSG_REQD_BY_ALT_GW"));
	}

	@Override
	public void process(RequestVO p_requestVO) {
		Connection con = null;
		MComConnectionI mcomCon = null;

		// 1. Validate the incoming Message
		// 2. Get the newtork Code for customer MSISDN
		// a) check the network and service type maapping
		// 3. Check whether same sender and reciever msisdn can be same
		// 4. Get the location URLS
		// 5. Generate the Transfer ID
		// 6. Check the min and max range stored in preferences updated
		// internally
		// 7. Format the amt in system format
		// a) Sender related checks if any
		// 8. Check the sender has balance
		// 9. Send request to IN
		// 10. Get the service class
		// 11. Get the cardgroup
		// 12. Calculate amount
		// 13. Debit the sender also check for -ve differential
		// 14. Spawn the thread and do the topup

		final String methodName = "process";
		StringBuilder loggerValue= new StringBuilder(); 
		if (_log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered for Request ID=");
			loggerValue.append(p_requestVO.getRequestID());
			loggerValue.append(" MSISDN=");
			loggerValue.append(p_requestVO.getFilteredMSISDN());
			loggerValue.append(" _recValidationFailMessageRequired: ");
			loggerValue.append(_recValidationFailMessageRequired);
			loggerValue.append(" _recTopupFailMessageRequired");
			loggerValue.append(_recTopupFailMessageRequired);
			loggerValue.append(" _notAllowedSendMessGatw: ");
			loggerValue.append(_notAllowedSendMessGatw);
			loggerValue.append(" ");
			_log.debug(methodName,p_requestVO.getRequestIDStr(), loggerValue);
		}
		boolean isCellIDRequiredFromIN = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_CELLID_REQUIRED_FROM_IN);
		boolean dbEntryNotAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED);
		boolean allowRoamRecharge = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.ALLOW_ROAM_RECHARGE);
		String decenterRoamLocation = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DECENTER_ROAM_LOCATION);
		boolean subscriberPrefixRoutingAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SUBSCRIBER_PREFIX_ROUTING_ALLOWED);
		boolean c2sAllowSelfTopup = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_ALLOW_SELF_TOPUP);
		String defaultCurrency = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_CURRENCY);
		int amountMultFactor = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue();
		
		CurrencyConversionVO currencyVO;
		try {
			_c2sTransferVO.setRequestStartTime(p_requestVO.getRequestStartTime());
			NetworkServiceVO networkServiceVO = NetworkServicesCache.getObject(PretupsI.C2S_MODULE, _senderNetworkCode, _receiverNetworkCode, _c2sTransferVO.getServiceType());
			_requestVO = p_requestVO;
			_channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();

			TransactionLog.log("", p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), _channelUserVO.getNetworkID(), PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_TXNSTAGE_RECIVED, "Received Request From Receiver", PretupsI.TXN_LOG_STATUS_SUCCESS, "");
			if (!isCellIDRequiredFromIN ) {
				
				boolean isGeoFenced = PretupsBL.checkGeoFencing(_requestVO, _channelUserVO); 
				if(isGeoFenced)
				{
					_c2sTransferVO.setInGeoFencing(true);
				}
				else {
					_c2sTransferVO.setInGeoFencing(false);
				}
			  }
			_senderLocale = p_requestVO.getSenderLocale();
			_senderNetworkCode = _channelUserVO.getNetworkID();
			populateVOFromRequest(p_requestVO);
			_requestID = p_requestVO.getRequestID();
			_requestIDStr = p_requestVO.getRequestIDStr();
			_type = p_requestVO.getType();
			_serviceType = p_requestVO.getServiceType();
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			_channelUserVO.setCellID(p_requestVO.getCellId());
			_c2sTransferVO.setCellId(p_requestVO.getCellId());
			_c2sTransferVO.setSwitchId(p_requestVO.getSwitchId());
			_c2sTransferVO.setRequestGatewayCode(p_requestVO.getRequestGatewayCode());
			if(PretupsI.MULTI_CURRENCY_SERVICE_TYPE.equals(_serviceType ))
				_operatorUtil.validateMultiCurrencyRechargeRequest(con, _c2sTransferVO, p_requestVO);
			else	
				_operatorUtil.validateC2SRechargeRequest(con, _c2sTransferVO, p_requestVO);


			if (p_requestVO.getRequestMap() != null && p_requestVO.getRequestMap().get("CURRENCY") != null && ((String)p_requestVO.getRequestMap().get("CURRENCY")).trim().length()!=0)
			{
				long mult;
				double temp;
				double finalValue;
				currencyVO = (CurrencyConversionVO)CurrencyConversionCache.getObject((String)p_requestVO.getRequestMap().get("CURRENCY"), defaultCurrency, _senderNetworkCode);
				 mult = amountMultFactor;
				 temp = (Double.parseDouble(BigDecimal.valueOf(currencyVO.getConversion()).toPlainString())/currencyVO.getMultFactor()) * Double.parseDouble(PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount())) ;
				 finalValue = Math.round(Double.parseDouble(BigDecimal.valueOf(temp * mult).toPlainString()))/(double)mult;
				 _c2sTransferVO.setMultiCurrencyDetailVO((String)p_requestVO.getRequestMap().get("CURRENCY")+":"+Double.parseDouble(BigDecimal.valueOf(currencyVO.getConversion()).toPlainString())/currencyVO.getMultFactor()+":"+PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()));
				 _c2sTransferVO.setRequestedAmount(PretupsBL.getSystemAmount(finalValue));
			}

			if(!BTSLUtil.isNullString(_requestVO.getSid())){
				_c2sTransferVO.setSID(_requestVO.getSid());
			}
			if(_log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("_c2sTransferVO.getSID()=");
				loggerValue.append(_c2sTransferVO.getSID());
				loggerValue.append(",_requestVO.getSid()=");
				loggerValue.append(_requestVO.getSid());
				_log.debug(methodName,loggerValue);
			}
			String decimalAllowServices = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DECIMAL_ALLOW_SERVICES);
			if (!BTSLUtil.isStringIn(_serviceType, decimalAllowServices)) {
				try {
					final String displayAmt = PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount());
					Long.parseLong(displayAmt);
				} catch (Exception e) {
					_log.errorTrace(methodName, e);
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_INVALID_AMOUNT);
				}
			}

			_receiverLocale = p_requestVO.getReceiverLocale();
			_senderLocale = p_requestVO.getSenderLocale();
			_receiverVO = (ReceiverVO) _c2sTransferVO.getReceiverVO();			
	if(_log.isDebugEnabled()) 
		{_log.debug("process","Prefixes :: Receiver prefix : ",PretupsBL.getMSISDNPrefix(_receiverVO.getMsisdn()) +"  Sender prefix : "+PretupsBL.getMSISDNPrefix(_channelUserVO.getMsisdn()) );}
				if(subscriberPrefixRoutingAllowed){		
					if(!_operatorUtil.isSubscriberPrefixMappingExist(con, _receiverVO.getMsisdn(), _channelUserVO.getMsisdn(), PretupsI.SERVICE_TYPE_PRE))	
						throw new BTSLBaseException(this,"process",PretupsErrorCodesI.RECHARGE_ERROR_DIFFERENT_NETWORK);
				}
			_receiverVO.setMsisdnPrefix(PretupsBL.getMSISDNPrefix(_receiverVO.getMsisdn()));
			_receiverNetworkCode = _receiverVO.getNetworkCode();
			networkServiceVO = NetworkServicesCache.getObject(PretupsI.C2S_MODULE, _senderNetworkCode, _receiverNetworkCode, _c2sTransferVO.getServiceType());
			if (!_senderNetworkCode.equals(_receiverNetworkCode) && allowRoamRecharge && networkServiceVO != null 
					&& "Y".equalsIgnoreCase(networkServiceVO.getStatus())  && 
					BTSLUtil.isStringContain(decenterRoamLocation,_receiverNetworkCode)){
				final C2SPrepaidRoamHelper c2sPrepaidRoamHelper = new C2SPrepaidRoamHelper();
				c2sPrepaidRoamHelper.process(p_requestVO, _c2sTransferVO);
				return;
			}
			_c2sTransferVO.setSelectorCode(p_requestVO.getReqSelector());
			final ServiceSelectorMappingVO serviceSelectorMappingVO = (ServiceSelectorMappingVO) ServiceSelectorMappingCache.getServiceSelectorMap().get(
					p_requestVO.getServiceType() + "_" + p_requestVO.getReqSelector());
			if (serviceSelectorMappingVO != null) {
				_receiverBundleID = serviceSelectorMappingVO.getReceiverBundleID();
				_c2sTransferVO.setReceiverBundleID(_receiverBundleID);
				_selectorName = serviceSelectorMappingVO.getSelectorName();
			}
			else{
				throw new BTSLBaseException("", methodName, PretupsErrorCodesI.ERROR_INVALID_SELECTOR_VALUE,0, null,null);
			}

			if (!_receiverVO.getSubscriberType().equals(_type)) {
			 loggerValue.setLength(0);
			 loggerValue.append("Series =" );
		 	 loggerValue.append(_receiverVO.getMsisdnPrefix());
		 	 loggerValue.append(" Not Defined for Series type=");
		     loggerValue.append(_type);
				_log.error(this, loggerValue );
				loggerValue.setLength(0);
				 loggerValue.append("Series =" );
			 	 loggerValue.append(_receiverVO.getMsisdnPrefix());
			 	 loggerValue.append(" Not Defined for Series type=");
			     loggerValue.append(_type);
			     loggerValue.append(" But request initiated for the same");
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "C2SPrepaidController[process]", "", "", "",  loggerValue.toString() );
				throw new BTSLBaseException("", methodName, PretupsErrorCodesI.ERROR_NOTFOUND_SERIES_TYPE, 0, new String[] { _receiverVO.getMsisdn() }, null);
			}

			_receiverVO.setModule(_c2sTransferVO.getModule());
			_receiverVO.setCreatedDate(_currentDate);
			_receiverVO.setLastTransferOn(_currentDate);
			_receiverNetworkCode = _receiverVO.getNetworkCode();
			_senderMSISDN = (_channelUserVO.getUserPhoneVO()).getMsisdn();
			_senderPushMessageMsisdn = p_requestVO.getMessageSentMsisdn();
			_receiverMSISDN = ((ReceiverVO) _c2sTransferVO.getReceiverVO()).getMsisdn();
			_c2sTransferVO.setReceiverMsisdn(_receiverMSISDN);
			_c2sTransferVO.setReceiverNetworkCode(_receiverVO.getNetworkCode());
			_c2sTransferVO.setSenderNetworkCode(_senderNetworkCode);
			_c2sTransferVO.setGrphDomainCode(_channelUserVO.getGeographicalCode());
			_c2sTransferVO.setSubService(p_requestVO.getReqSelector());
			_receiverSubscriberType = _receiverVO.getSubscriberType();
			_c2sTransferVO.setLRallowed(_channelUserVO.getLrAllowed());
			_c2sTransferVO.setLRMaxAmount(_channelUserVO.getLrMaxAmount());

			RestrictedSubscriberBL.isRestrictedMsisdnExistForC2S(con, _c2sTransferVO, _channelUserVO, _receiverVO.getMsisdn(), _c2sTransferVO.getRequestedAmount());
			PretupsBL.validateNetworkService(_c2sTransferVO);
			_receiverMessageSendReq = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.REC_MSG_SEND_ALLOW, _receiverVO.getNetworkCode(), _serviceType)).booleanValue();
			_ussdReceiverMessageSendReq=((Boolean)PreferenceCache.getControlPreference(PreferenceI.USSD_REC_MSG_SEND_ALLOW,_receiverVO.getNetworkCode(),_serviceType)).booleanValue();
			
			if (_senderMSISDN.equals(_receiverMSISDN) && !c2sAllowSelfTopup) {
				throw new BTSLBaseException(this, "processTransfer", PretupsErrorCodesI.CHNL_ERROR_SELF_TOPUP_NTALLOWD);
			}
			if (PretupsI.SUSPEND.equals(_channelUserVO.getTransferProfileStatus())) {
				throw new BTSLBaseException(this, "processTransfer", PretupsErrorCodesI.CHNL_ERROR_SNDR_TRANPROFILE_SUSPEND);
			}
			if (PretupsI.SUSPEND.equals(_channelUserVO.getCommissionProfileStatus())) {
				throw new BTSLBaseException(this, "processTransfer", PretupsErrorCodesI.CHNL_ERROR_SNDR_COMMPROFILE_SUSPEND);
			} else if (PretupsI.YES.equalsIgnoreCase(_channelUserVO.getOutSuspened())) {
				throw new BTSLBaseException(this, "processTransfer", PretupsErrorCodesI.CHNL_ERROR_SENDER_OUT_SUSPEND);
			}

			try {
				if(p_requestVO.getRequestGatewayCode().equals(PretupsI.GATEWAY_TYPE_EXTGW))
				PretupsBL.checkMSISDNBarred(con, _senderMSISDN  , _receiverVO.getNetworkCode(), _c2sTransferVO.getModule(), PretupsI.USER_TYPE_SENDER);
				PretupsBL.checkMSISDNBarred(con, _receiverMSISDN, _receiverVO.getNetworkCode(), _c2sTransferVO.getModule(), PretupsI.USER_TYPE_RECEIVER);
			} catch (BTSLBaseException be) {
				if (be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.ERROR_RECEIVER_USERBARRED))) {
					_c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.ERROR_USERBARRED_R, new String[] {}));
				}
				throw be;
			}

			_c2sTransferVO.setUnderProcessCheckReqd(p_requestVO.getMessageGatewayVO().getRequestGatewayVO().getUnderProcessCheckReqd());
			PretupsBL.loadRecieverControlLimits(con, p_requestVO.getRequestIDStr(), _c2sTransferVO);
			_receiverVO.setUnmarkRequestStatus(true);

			try {
				mcomCon.partialCommit();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
				throw new BTSLBaseException("C2SPrepaidController", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			}
			boolean skeyRequired = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SKEY_REQUIRED);
			if (skeyRequired) {
				processSKeyGen(con);
			} else {
				processTransfer(con);
				p_requestVO.setTransactionID(_transferID);
				_receiverVO.setLastTransferID(_transferID);
				TransactionLog.log(_transferID, p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), _channelUserVO.getNetworkID(), PretupsI.TXN_LOG_REQTYPE_INT,
						PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Generated Transfer ID", PretupsI.TXN_LOG_STATUS_SUCCESS,"Source Type=" + _c2sTransferVO.getSourceType() + " Gateway Code=" + _c2sTransferVO.getRequestGatewayCode());

				populateServiceInterfaceDetails(con, PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION);

				_c2sTransferVO.setReceiverAllServiceClassID(_receiverAllServiceClassID);
				_c2sTransferVO.setReceiverSubscriberType(_receiverSubscriberType);
				PretupsBL.validateRecieverLimits(con, _c2sTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL, PretupsI.C2S_MODULE);

				if (!_senderNetworkCode.equals(_receiverNetworkCode) && allowRoamRecharge && networkServiceVO != null && "Y"
						.equalsIgnoreCase(networkServiceVO.getStatus())) {
					_c2sTransferVO.setIsRoam(true);
					getRoamPenalty(con);
				}

				_itemList = new ArrayList();
				_itemList.add(_senderTransferItemVO);
				_itemList.add(_receiverTransferItemVO);
				_c2sTransferVO.setTransferItemList(_itemList);

				ChannelUserBL.validateSenderAvailableControls(con, _transferID, _c2sTransferVO);
				_senderTransferItemVO.setValidationStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
				try {
					mcomCon.finalCommit();
				} catch (Exception e) {
					_log.errorTrace(methodName, e);
					throw new BTSLBaseException("C2SPrepaidController", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
				}
				if (mcomCon != null)
					mcomCon.close("C2SPrepaidController#process");
				mcomCon = null;
				con = null;

				checkTransactionLoad();
				_decreaseTransactionCounts = true;

				if (!_channelUserVO.isStaffUser()) {
					(_channelUserVO.getUserPhoneVO()).setLastTransferID(_transferID);
					(_channelUserVO.getUserPhoneVO()).setLastTransferType(PretupsI.TRANSFER_TYPE_C2S);
				} else {
					(_channelUserVO.getStaffUserDetails().getUserPhoneVO()).setLastTransferID(_transferID);
					(_channelUserVO.getStaffUserDetails().getUserPhoneVO()).setLastTransferType(PretupsI.TRANSFER_TYPE_C2S);
				}
				if (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON) && !(p_requestVO.isToBeProcessedFromQueue())) {
					processValidationRequest();
					p_requestVO.setMessageCode(PretupsI.TXN_STATUS_UNDER_PROCESS);
					p_requestVO.setSenderMessageRequired(_c2sTransferVO.isUnderProcessMsgReq());
					p_requestVO.setSenderReturnMessage(getSenderUnderProcessMessage());
					p_requestVO.setDecreaseLoadCounters(false);
				}
				else if (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD) && !(p_requestVO.isToBeProcessedFromQueue())) {
					p_requestVO.setSenderReturnMessage(getSndrUPMsgBeforeValidation());
					p_requestVO.setMessageCode(PretupsI.TXN_STATUS_UNDER_PROCESS);
					final Thread _controllerThread = new Thread(this);
					_controllerThread.start();
					_oneLog = false;
					p_requestVO.setDecreaseLoadCounters(false);
				}

				else if (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_REQUEST) || (p_requestVO.isToBeProcessedFromQueue())) {
					p_requestVO.setMessageCode(PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
					processValidationRequest();
					run();
					if (!PretupsErrorCodesI.TXN_STATUS_SUCCESS.equals(_receiverVO.getTransactionStatus())) {
						p_requestVO.setMessageCode(_c2sTransferVO.getErrorCode());
					}
					String[] messageArgArray={(_receiverVO.getSid()!=null)?_receiverVO.getSid():_receiverMSISDN,_transferID,PretupsBL.getDisplayAmount(_c2sTransferVO.getTransferValue()),PretupsBL.getDisplayAmount(_senderTransferItemVO.getPostBalance()),PretupsBL.getDisplayAmount(_senderTransferItemVO.getPreviousBalance()),String.valueOf(_receiverTransferItemVO.getValidity()),PretupsBL.getDisplayAmount(_receiverTransferItemVO.getPostBalance()),String.valueOf(_receiverTransferItemVO.getNewGraceDate()),PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverAccessFee()),_c2sTransferVO.getSubService(),_c2sTransferVO.getProductName()};
					p_requestVO.setMessageArguments(messageArgArray);
				}
			}
		} catch (BTSLBaseException be) {
			p_requestVO.setSuccessTxn(false);
			try {
				if (_receiverVO != null && _receiverVO.isUnmarkRequestStatus()) { 
					if (mcomCon == null) {
						mcomCon = new MComConnection();
						con = mcomCon.getConnection();
					}
					PretupsBL.unmarkReceiverLastRequest(con, p_requestVO.getRequestIDStr(), _receiverVO);
				}
			} catch (BTSLBaseException bex) {
				_log.errorTrace(methodName, bex);
				loggerValue.setLength(0);
				loggerValue.append("Leaving Reciever Unmarked Base Exception:");
				loggerValue.append(bex.getMessage());
				
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "C2SPrepaidController[process]", _transferID,_senderMSISDN, _senderNetworkCode,  loggerValue.toString());
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
				_c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
				loggerValue.setLength(0);
				loggerValue.append("Leaving Reciever Unmarked Base Exception:");
				loggerValue.append(e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "C2SPrepaidController[process]", _transferID,_senderMSISDN, _senderNetworkCode,  loggerValue.toString());
			}
			_c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			if (_recValidationFailMessageRequired) {
				if (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
					if (_transferID != null) {
						_c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.C2S_RECEIVER_FAIL, new String[] { String.valueOf(_transferID), PretupsBL
								.getDisplayAmount(_c2sTransferVO.getRequestedAmount()) }));
					} else {
						_c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.FAIL_R, new String[] { PretupsBL.getDisplayAmount(_c2sTransferVO
								.getRequestedAmount()) }));
					}
				}
			}
			if (!BTSLUtil.isNullString(_c2sTransferVO.getSenderReturnMessage())) {
				p_requestVO.setSenderReturnMessage(_c2sTransferVO.getSenderReturnMessage());
			}
			if (be.isKey()) 
			{
				if (_c2sTransferVO.getErrorCode() == null) {
					_c2sTransferVO.setErrorCode(be.getMessageKey());
				}
				p_requestVO.setMessageCode(be.getMessageKey());
				p_requestVO.setMessageArguments(be.getArgs());
			} else {
				_c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			}

			if (_transferID != null && _decreaseTransactionCounts) {
				LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
				_isCounterDecreased = true;
			}
			TransactionLog.log(_transferID, p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, _c2sTransferVO.getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + p_requestVO.getMessageCode());
			ChannelRequestDailyLog.log(ChannelRequestDailyLog.populateChannelRequestDailyLogVO(_requestVO, _c2sTransferVO));
			_log.errorTrace(methodName, be);
			if(penaltyApplicable&&_c2sTransferVO.isPenaltyInsufficientBalance()){
				BTSLMessages message= new BTSLMessages(PretupsErrorCodesI.CHNL_ERROR_SNDR_BAL_LESS_ROAM );
				PushMessage pushPenaltymessage1 = (new PushMessage(_senderMSISDN, message, _transferID, _c2sTransferVO.getRequestGatewayCode(), _senderLocale, _senderNetworkCode, null, _serviceType));
				pushPenaltymessage1.push();
			}
			if(penaltyApplicableOwner &&c2STransferOwnerVO.isPenaltyInsufficientBalanceOwner()){
				BTSLMessages message= new BTSLMessages(PretupsErrorCodesI.CHNL_ERROR_OWNR_BAL_LESS_ROAM );
				PushMessage pushPenaltymessage2 = (new PushMessage(_senderMSISDN, message, _transferID, _c2sTransferVO.getRequestGatewayCode(), _senderLocale, _senderNetworkCode, null, _serviceType));
				pushPenaltymessage2.push();
			}
		} catch (Exception e) {
			p_requestVO.setSuccessTxn(false);
			try {
				if (_receiverVO != null && _receiverVO.isUnmarkRequestStatus()) {
					if (mcomCon == null) {
						mcomCon = new MComConnection();
						con = mcomCon.getConnection();
					}
					PretupsBL.unmarkReceiverLastRequest(con, p_requestVO.getRequestIDStr(), _receiverVO);
				}
			} catch (BTSLBaseException bex) {
				_log.errorTrace(methodName, bex);
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "C2SPrepaidController[process]", _transferID, _senderMSISDN, _senderNetworkCode, "Leaving Reciever Unmarked Base Exception:" + bex.getMessage());
			} catch (Exception ex) {
				_log.errorTrace(methodName, ex);
				_c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "C2SPrepaidController[process]", _transferID, _senderMSISDN, _senderNetworkCode, "Leaving Reciever Unmarked Base Exception:" + ex.getMessage());
			}
			if (_recValidationFailMessageRequired) {
				if (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
					if (_transferID != null) {
						_c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.C2S_RECEIVER_FAIL, new String[] { String.valueOf(_transferID), PretupsBL
								.getDisplayAmount(_c2sTransferVO.getRequestedAmount()) }));
					} else {
						_c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.FAIL_R, new String[] { PretupsBL.getDisplayAmount(_c2sTransferVO
								.getRequestedAmount()) }));
					}
				}
			}

			_c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			_c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			_log.errorTrace(methodName, e);
			if (_transferID != null && _decreaseTransactionCounts) {
				LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
				_isCounterDecreased = true;
			}
			loggerValue.setLength(0);
			loggerValue.append("Exception:");
			loggerValue.append(e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SPrepaidController[process]", _transferID, _senderMSISDN, _senderNetworkCode,  loggerValue.toString());
			TransactionLog.log(_transferID, p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, _c2sTransferVO.getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + p_requestVO.getMessageCode());
			ChannelRequestDailyLog.log(ChannelRequestDailyLog.populateChannelRequestDailyLogVO(_requestVO, _c2sTransferVO));
		}
		finally {
			try {
				if (mcomCon == null) {
					mcomCon = new MComConnection();
					con = mcomCon.getConnection();
				}
				if (_transferID != null && !_transferDetailAdded && (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON) || p_requestVO
						.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_REQUEST) || (p_requestVO.getMessageGatewayVO().getFlowType().equals(
								PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD) && !p_requestVO.getMessageCode().equals(PretupsI.TXN_STATUS_UNDER_PROCESS)))) {
					if (!dbEntryNotAllowed) {
						addEntryInTransfers(con);
					}
				} else if (_transferID != null && p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD)) {
					if (_log.isInfoEnabled()) {
						_log.info(methodName, p_requestVO.getRequestIDStr(),"Send the message to MSISDN=" + p_requestVO.getFilteredMSISDN() + " Transfer ID=" + _transferID + " But not added entry in Transfers yet");
					}
				}

				mcomCon.finalCommit();
			} catch (BTSLBaseException be) {
				try {
					mcomCon.finalRollback();
				} catch (SQLException sqle) {
					_log.errorTrace(methodName, sqle);
				}
				_log.errorTrace(methodName, be);
			} catch (Exception e) {
				try {
					mcomCon.finalRollback();
				} catch (SQLException sqle) {
					_log.errorTrace(methodName, sqle);
				}
				_log.errorTrace(methodName, e);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SPrepaidController[process]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
			}finally{
				if (mcomCon != null)
					mcomCon.close("C2SPrepaidController#process");
				mcomCon = null;
				con = null;
			}
			if (BTSLUtil.isNullString(p_requestVO.getMessageCode())) {
				p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
			}
			if (_receiverTransferItemVO != null) {
				if (!BTSLUtil.isNullString(_receiverTransferItemVO.getValidationStatus())) {
					if (_receiverTransferItemVO.getValidationStatus().equals(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND)) {
						p_requestVO.setIntMsisdnNotFound(_c2sTransferVO.getErrorCode());
					} else {
						p_requestVO.setIntMsisdnNotFound(null);
					}
				} else {
					p_requestVO.setIntMsisdnNotFound(null);
				}
			}
			if (_isCounterDecreased) {
				p_requestVO.setDecreaseLoadCounters(false);
			}
			if (_receiverMessageSendReq && _recValidationFailMessageRequired && !BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(), _notAllowedRecSendMessGatw) && !"ALL"
					.equals(_notAllowedRecSendMessGatw)) {
				String requestGW = _c2sTransferVO.getRequestGatewayCode();
				if (!BTSLUtil.isNullString(_RecAlternetGatewaySMS) && (_RecAlternetGatewaySMS.split(":")).length >= 2) {
					if (requestGW.equalsIgnoreCase(_RecAlternetGatewaySMS.split(":")[0])) {
						requestGW = (_RecAlternetGatewaySMS.split(":")[1]).trim();
						if (_log.isDebugEnabled())  _log.debug("process: Reciver Message push through alternate GW", requestGW, "Requested GW was:" + _c2sTransferVO.getRequestGatewayCode());
					}
				}
				if (_c2sTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
					if (_receiverTransferItemVO != null && InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED.equals(_receiverTransferItemVO.getValidationStatus())) {
						_c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED + "_R"));
					}
					final BTSLMessages btslRecMessages = (BTSLMessages) _c2sTransferVO.getReceiverReturnMsg();
					(new PushMessage(_receiverMSISDN, BTSLUtil.getMessage(_receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), _transferID,
							requestGW, _receiverLocale, _senderNetworkCode, null, _serviceType)).push();
				} else if (_c2sTransferVO.getReceiverReturnMsg() != null) {
					(new PushMessage(_receiverMSISDN, (String) _c2sTransferVO.getReceiverReturnMsg(), _transferID, requestGW, _receiverLocale, _senderNetworkCode, null, _serviceType)).push();
				}
			}
			
			if(_ussdReceiverMessageSendReq &&_recValidationFailMessageRequired&&!BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(),_notAllowedRecSendMessGatw)&&!"ALL".equals(_notAllowedRecSendMessGatw))
			{
			    String requestGW=_c2sTransferVO.getRequestGatewayCode();
                if(!BTSLUtil.isNullString(_RecAlternetGatewaySMS)&& (_RecAlternetGatewaySMS.split(":")).length>=2)
                {
                    if(requestGW.equalsIgnoreCase(_RecAlternetGatewaySMS.split(":")[0]))
                    {
                        requestGW=(_RecAlternetGatewaySMS.split(":")[1]).trim();
                        if(_log.isDebugEnabled()) _log.debug("process: Reciver Message push through alternate GW",requestGW,"Requested GW was:"+_c2sTransferVO.getRequestGatewayCode());
                    }
                }
				//checking if receiver message is not null and receiver return message is key
				if(_c2sTransferVO.getReceiverReturnMsg()!=null&&((BTSLMessages)_c2sTransferVO.getReceiverReturnMsg()).isKey())
				{
				    //check _receiverTransferItemVO!=null because if any exception occure before setting _receiverTransferItemVO
				    //generating message and pushing it to receiver
					if(_receiverTransferItemVO!=null && InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED.equals(_receiverTransferItemVO.getValidationStatus()))
					{
						_c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED+"_R"));
					}
					BTSLMessages btslRecMessages=(BTSLMessages)_c2sTransferVO.getReceiverReturnMsg();
					(new USSDPushMessage(_receiverMSISDN,BTSLUtil.getMessage(_receiverLocale,btslRecMessages.getMessageKey(),btslRecMessages.getArgs()),_transferID,requestGW,_receiverLocale)).push();
				}
				else if(_c2sTransferVO.getReceiverReturnMsg()!=null) //pushing message to receiver
					(new USSDPushMessage(_receiverMSISDN,(String)_c2sTransferVO.getReceiverReturnMsg(),_transferID,requestGW,_receiverLocale)).push();
			}
			if (_oneLog) {
				_senderTransferItemVO.setTransferVO(_channelUserVO);
				OneLineTXNLog.log(_c2sTransferVO, _senderTransferItemVO, _receiverTransferItemVO);
			}
			TransactionLog.log(_transferID, p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES,
					PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Leaving the controller", PretupsI.TXN_LOG_STATUS_SUCCESS, "Getting Code=" + p_requestVO.getMessageCode());
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exiting");
			}
		}
	}

	private void processSKeyGen(Connection p_con) throws BTSLBaseException, Exception {
		final String methodName = "processSKeyGen";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered");
		}
		try {
			PretupsBL.generateSKey(p_con, _c2sTransferVO);
		} catch (BTSLBaseException be) {
			throw be;
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SPrepaidController[processSKeyGen]", _transferID,
					_senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
			throw new BTSLBaseException("C2SPrepaidController", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		} finally {
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exiting");
			}
		}
	}

	public void processTransfer(Connection p_con) throws BTSLBaseException {
		final String methodName = "processTransfer";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered");
		}
		try {
			_c2sTransferVO.setTransferDate(_currentDate);
			_c2sTransferVO.setTransferDateTime(_currentDate);
			generateC2STransferID(_c2sTransferVO);
			_transferID = _c2sTransferVO.getTransferID();
			_receiverVO.setLastTransferID(_transferID);
			setSenderTransferItemVO();
			setReceiverTransferItemVO();
			PretupsBL.getProductFromServiceType(p_con, _c2sTransferVO, _serviceType, PretupsI.C2S_MODULE);
			_transferEntryReqd = true;
			if ((_channelUserVO.getCategoryVO()).getDomainTypeCode().equals(PretupsI.DOMAIN_TYPE_SALECENTER)) {
				_senderTransferItemVO.setTransferValue(_c2sTransferVO.getRequestedAmount());
			} else {
				_senderTransferItemVO.setTransferValue(_c2sTransferVO.getTransferValue());
			}
			_requestVO.setValueObject(_c2sTransferVO);
		} catch (BTSLBaseException be) {
			_c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			if (be.isKey()) {
				_c2sTransferVO.setErrorCode(be.getMessageKey());
			} else {
				_c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			}
			throw be;
		} catch (Exception e) {
			if (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
				if (_transferID != null) {
					_c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.C2S_RECEIVER_FAIL, new String[] { String.valueOf(_transferID), PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()) }));
				} else {
					_c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.FAIL_R, new String[] { PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()) }));
				}
			}
			_c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			_c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SPrepaidController[processTransfer]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
			throw new BTSLBaseException("C2SPrepaidController", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
	}

	@Override
	public void run() {
		final String methodName = "run";
		StringBuilder loggerValue= new StringBuilder(); 
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, _transferID, "Entered");
		}
		BTSLMessages btslMessages = null;
		_userBalancesVO = null;
		CommonClient commonClient = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		boolean lmsAppl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);
		boolean optInOutAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW);
		boolean sosOnlineAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SOS_ONLINE_ALLOW);
		boolean dbEntryNotAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED);
		boolean c2sSndrCreditBkAmbStatus = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_SNDR_CREDIT_BK_AMB_STATUS);
		boolean allowRoamAddComm = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.ALLOW_ROAM_ADDCOMM);
		boolean activationFirstRecApp = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.ACTIVATION_FIRST_REC_APP);		
		try {
			if (_c2sTransferVO.getMsgGatewayFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD) && !_processedFromQueue) {
				processValidationRequestInThread();
			}
			LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_TOP);
			commonClient = new CommonClient();
			final String requestStr = getReceiverCreditStr();
			TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INTOP,requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "");
			final String receiverCreditResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);
			TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INTOP,receiverCreditResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");
			if (_log.isDebugEnabled()) {
				loggerValue.setLength(0);
            	loggerValue.append("Got the response from IN Module receiverCreditResponse=");
            	loggerValue.append(receiverCreditResponse);
				_log.debug(methodName, _transferID,  loggerValue );
			}
			try {
				updateForReceiverCreditResponse(receiverCreditResponse);
				LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.SENDER_TOP_RESPONSE);
			} catch (BTSLBaseException be) {
				TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Transaction Failed", PretupsI.TXN_LOG_STATUS_FAIL, "Transfer Status=" + _c2sTransferVO.getTransferStatus() + " Getting Code=" + _receiverVO.getInterfaceResponseCode());
				if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS)) {
					LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.SENDER_TOP_RESPONSE);
				} else {
					LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_TOP_RESPONSE);
				}
				if (((_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS) && c2sSndrCreditBkAmbStatus)) || _c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
					if(mcomCon == null){	
						mcomCon = new MComConnection();
						con = mcomCon.getConnection();
					}
					updateSenderForFailedTransaction(con);
				}
				PretupsBL.validateRecieverLimits(con, _c2sTransferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.C2S_MODULE);
				throw be;
			} catch (Exception e) {
				TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Transaction Failed", PretupsI.TXN_LOG_STATUS_FAIL, "Transfer Status=" + _c2sTransferVO.getTransferStatus() + " Getting Code=" + _receiverVO.getInterfaceResponseCode());
				if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS)) {
					LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.SENDER_TOP_RESPONSE);
				} else {
					LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_TOP_RESPONSE);
				}
				if (((_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS) && c2sSndrCreditBkAmbStatus)) || _c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
					if(mcomCon == null){	
						mcomCon = new MComConnection();
						con = mcomCon.getConnection();
					}
					updateSenderForFailedTransaction(con);
				}
				PretupsBL.validateRecieverLimits(con, _c2sTransferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.C2S_MODULE);
				
			}
			
			_c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
			_c2sTransferVO.setErrorCode(null);
			if (_c2sTransferVO.getMsgGatewayFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD) || _c2sTransferVO.getMsgGatewayFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON)) {
				ReqNetworkServiceLoadController.increaseRechargeCounters(_requestVO.getInstanceID(), _requestVO.getMessageGatewayVO().getGatewayType(), _senderNetworkCode,_serviceType, _transferID, LoadControllerI.COUNTER_SUCCESS_REQUEST, 0, true, _receiverVO.getNetworkCode());
				ReqNetworkServiceLoadController.decrementUnderProcessCounters(_requestVO.getInstanceID(), _requestVO.getMessageGatewayVO().getGatewayType(),_senderNetworkCode, _serviceType, _transferID, LoadControllerI.COUNTER_UNDERPROCESS_REQUEST, 0, false, _receiverVO.getNetworkCode());
			}
			PretupsBL.validateRecieverLimits(con, _c2sTransferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.C2S_MODULE);
			_c2sTransferVO.setSenderReturnMessage(null);
			
			// added for the intermediate balance for the transaction notification to sender in case of additional commission
			senderReturnMessage = getSenderSuccessMessage();
			
			if ((!_senderNetworkCode.equals(_receiverNetworkCode)) && (allowRoamAddComm) && !stopAdditionalCommission) {
				try {
					new DiffCalBL().differentialCalculations(_c2sTransferVO, PretupsI.C2S_MODULE);
				} catch (BTSLBaseException be) {
					_finalTransferStatusUpdate = false;
					EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "C2SPrepaidController[run]", _c2sTransferVO.getTransferID(), _c2sTransferVO.getSenderMsisdn(), _c2sTransferVO.getNetworkCode(), "Exception:" + be.getMessage());
					_log.errorTrace(methodName, be);
				} catch (Exception e) {
					_finalTransferStatusUpdate = false;
					_log.errorTrace(methodName, e);
				}
			}
			if ((_senderNetworkCode.equals(_receiverNetworkCode)) || ((!_senderNetworkCode.equals(_receiverNetworkCode)) && (!allowRoamAddComm)) && !stopAdditionalCommission) {
				if (PretupsI.YES.equals(_c2sTransferVO.getDifferentialAllowedForService())) {
					boolean giveBonus = true;
					if (activationFirstRecApp) {
						if (!"Y".equals(_c2sTransferVO.getActiveBonusProvided())) {
							giveBonus = false;
						}
					}
					if (giveBonus) {
						try {
							new DiffCalBL().differentialCalculations(_c2sTransferVO, PretupsI.C2S_MODULE);
						} catch (BTSLBaseException be) {
							_finalTransferStatusUpdate = false;
							_log.errorTrace(methodName, be);
							EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "C2SPrepaidController[run]",_c2sTransferVO.getTransferID(), _c2sTransferVO.getSenderMsisdn(), _c2sTransferVO.getNetworkCode(), "Exception:" + be.getMessage());
						} catch (Exception e) {
							_finalTransferStatusUpdate = false;
							_log.errorTrace(methodName, e);
						}
					}
				}
			}

			try {
				if(_c2sTransferVO.getPromoBonus()>0)
				{
					if(mcomCon == null){	
						mcomCon = new MComConnection();
						con = mcomCon.getConnection();
					}
                    new PromoBonusCalBL(con).promoBonusAdjustment(_c2sTransferVO, PretupsI.C2S_MODULE);
                }
			} catch (BTSLBaseException be) {
                    _finalTransferStatusUpdate = false;
                    if (_log.isDebugEnabled()) {
                    	loggerValue.setLength(0);
                    	loggerValue.append("For _transferID=");
                    	loggerValue.append(_transferID);
                    	loggerValue.append(_transferID);
                    	loggerValue.append(" roam commission applicable=" );
                    	loggerValue.append(_c2sTransferVO.getDifferentialApplicable());
                    	loggerValue.append(" roam commission Given=" );
                    	loggerValue.append(_c2sTransferVO.getDifferentialGiven());
                    	loggerValue.append(" Not able to give roam commission getting BTSL Base Exception=");
                    	loggerValue.append(be.getMessage());
                    	loggerValue.append(" Leaving transaction status as Under process");
                        _log.debug( "C2SPrepaidController",loggerValue);
                    }
                    loggerValue.setLength(0);
                	loggerValue.append("Exception:");
                	loggerValue.append(be.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "C2SPrepaidController[run]", _c2sTransferVO
                        .getTransferID(), _c2sTransferVO.getSenderMsisdn(), _c2sTransferVO.getNetworkCode(),  loggerValue.toString() );
                    _log.errorTrace(methodName, be);
                } catch (Exception e) {
                    _finalTransferStatusUpdate = false;
                    if (_log.isDebugEnabled()) {
                    	loggerValue.setLength(0);
                    	loggerValue.append( "For _transferID=");
                    	loggerValue.append(_transferID);
                    	loggerValue.append( " roam commission applicable=");
                    	loggerValue.append(_c2sTransferVO.getDifferentialApplicable());
                    	loggerValue.append( " roam commission Given=");
                    	loggerValue.append(_c2sTransferVO.getDifferentialGiven());
                    	loggerValue.append(" Not able to give roam commission getting Exception=");
                    	loggerValue.append(e.getMessage());
                    	loggerValue.append(" Leaving transaction status as Under process");
                        _log.debug("C2SPrepaidController",loggerValue);
                    }
                    _log.errorTrace(methodName, e);
                }
				
				
			try {
				DiffCalBL diffcalBL = new DiffCalBL();
				if (penaltyApplicable) {
					if(mcomCon == null){	
						mcomCon = new MComConnection();
						con = mcomCon.getConnection();
					}
					diffcalBL.insertRoamPenaltyAdjustments(con, _penaltyVODebit, _penaltyVOCredit);
					_c2sTransferVO.setDifferentialGiven(PretupsI.YES);
					_c2sTransferVO.setDifferentialApplicable(PretupsI.YES);
				}
				if (penaltyApplicableOwner) {
					if(mcomCon == null){
						mcomCon = new MComConnection();
						con = mcomCon.getConnection();
					}
					diffcalBL.insertRoamPenaltyAdjustments(con, _penaltyVODebitOwner, _penaltyVOCreditOwner);
				}
				if(penaltyApplicable || penaltyApplicableOwner)
					mcomCon.partialCommit();
			} catch (BTSLBaseException be) {
				try {mcomCon.partialRollback();} catch (SQLException sqle) { _log.errorTrace(methodName, sqle);}
				_finalTransferStatusUpdate = false;
				_log.errorTrace(methodName, be);
				loggerValue.setLength(0);
            	loggerValue.append("Exception:");
            	loggerValue.append( be.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO,
						"C2SPrepaidRoamHelper[run]", _c2sTransferVO.getTransferID(), _c2sTransferVO.getSenderMsisdn(),
						_c2sTransferVO.getNetworkCode(), loggerValue.toString());
			} catch (Exception e) {
				try {
					mcomCon.partialRollback();
					} 
				catch (SQLException sqle) {
					_log.errorTrace(methodName, sqle);
					}
				_finalTransferStatusUpdate = false;
				_log.errorTrace(methodName, e);
			}finally {
				_log.info(methodName, "inside finally");
			}
			if (sosOnlineAllow) {
				if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
					final Date currentDate = new Date();
					SOSVO sosvo = null;
					try {
						if (mcomCon == null) {
							mcomCon = new MComConnection();
							con = mcomCon.getConnection();
						}
						sosvo = new SOSTxnDAO().loadSOSDetails(con, currentDate, _receiverMSISDN);
						if (sosvo != null) {
							sosvo.setCreatedOn(currentDate);
							sosvo.setInterfaceID(_receiverTransferItemVO.getInterfaceID());
							sosvo.setInterfaceHandlerClass(_receiverTransferItemVO.getInterfaceHandlerClass());
							sosvo.setOldExpiryInMillis(_receiverTransferItemVO.getOldExporyInMillis());
							sosvo.setLocale(_receiverLocale);
							sosvo.setLmbAmountAtIN(_receiverTransferItemVO.getLmbdebitvalue());
							sosvo.setSettlmntServiceType(_requestVO.getServiceType());
							final SOSSettlementController sosSettlementController = new SOSSettlementController();
							sosSettlementController.processSOSRechargeRequest(sosvo);
						} else {
							_log.error(this, "C2SPrepaidController", methodName + " No record found in database for this number :" + _receiverTransferItemVO.getMsisdn());
						}
					} catch (BTSLBaseException be) {
						_log.errorTrace(methodName, be);
						loggerValue.setLength(0);
		            	loggerValue.append("run BTSLBaseException:" );
		            	loggerValue.append("Transaction ID: ");
		            	loggerValue.append(sosvo.getTransactionID());
		            	loggerValue.append("Msisdn");
		            	loggerValue.append(_receiverTransferItemVO.getMsisdn());
		            	loggerValue.append("Getting Exception while processing LMB request :");
		            	loggerValue.append(be.getMessage());
						_log.error(this, "C2SPrepaidController", loggerValue);
					} catch (Exception e) {
						_log.errorTrace(methodName, e);
						loggerValue.setLength(0);
		            	loggerValue.append("run Exception:");
		            	loggerValue.append("Transaction ID: ");
		            	loggerValue.append( sosvo.getTransactionID());
		            	loggerValue.append("Msisdn" );
		            	loggerValue.append(_receiverTransferItemVO.getMsisdn() );
		            	loggerValue.append("Getting Exception while processing LMB request :");
		            	loggerValue.append(e.getMessage());
						_log.error(this, "C2SPrepaidController", loggerValue);
					} finally {
						_log.info(methodName, "inside finally");
					}
				}
			}
			if (lmsAppl && _c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
				try {
					if(_log.isDebugEnabled()) {
						loggerValue.setLength(0);
		            	loggerValue.append("getControlGroup=");
		            	loggerValue.append(_channelUserVO.getControlGroup());
		            	loggerValue.append(",getOptInOutStatus=");
		            	loggerValue.append(_channelUserVO.getOptInOutStatus());
		            	loggerValue.append(",OPT_IN_OUT_ALLOW=");
		            	loggerValue.append(optInOutAllow);
		            	
						_log.debug("process",loggerValue);
					}
					if (optInOutAllow) {
						if (!BTSLUtil.isNullString(_channelUserVO.getLmsProfile()) && !BTSLUtil.isNullString(_channelUserVO.getOptInOutStatus()) && !BTSLUtil
								.isNullString(_channelUserVO.getControlGroup()) && PretupsI.NO.equalsIgnoreCase(_channelUserVO.getControlGroup()) && (PretupsI.OPT_IN
										.equalsIgnoreCase(_channelUserVO.getOptInOutStatus()) || PretupsI.NORMAL.equalsIgnoreCase(_channelUserVO.getOptInOutStatus()))) {
							final LoyaltyBL _loyaltyBL = new LoyaltyBL();
							final LoyaltyVO loyaltyVO = new LoyaltyVO();
							loyaltyVO.setServiceType(_serviceType);
							loyaltyVO.setModuleType(PretupsI.C2S_MODULE);
							loyaltyVO.setTransferamt(_c2sTransferVO.getTransferValue());
							loyaltyVO.setCategory(_c2sTransferVO.getCategoryCode());
							loyaltyVO.setUserid(_c2sTransferVO.getActiveUserId());
							loyaltyVO.setNetworkCode(_c2sTransferVO.getNetworkCode());
							loyaltyVO.setSenderMsisdn(_c2sTransferVO.getSenderMsisdn());
							loyaltyVO.setTxnId(_c2sTransferVO.getTransferID());
							loyaltyVO.setCreatedOn(_c2sTransferVO.getCreatedOn());
							loyaltyVO.setProductCode(_c2sTransferVO.getProductCode());
							loyaltyVO.setSetId(_channelUserVO.getLmsProfile());
							try {
								final LoyaltyController _loyaltyController = new LoyaltyController();
								if (mcomCon == null) {
									mcomCon = new MComConnection();
									con = mcomCon.getConnection();
								}
								final PromotionDetailsVO promotionDetailsVO = _loyaltyController.loadProfile(con, loyaltyVO.getSetId());
								if (promotionDetailsVO != null) {
									if (PretupsI.YES.equalsIgnoreCase(promotionDetailsVO.getOptInOutEnabled())) {
										if (PretupsI.NORMAL.equalsIgnoreCase(_channelUserVO.getOptInOutStatus())) {
											if (_log.isDebugEnabled()) {
												_log.debug("process", " No Bonus will be provided to user because of No response received from user end.");
											}
										} else if (PretupsI.OPT_IN.equalsIgnoreCase(_channelUserVO.getOptInOutStatus())) {
											_loyaltyBL.distributeLoyaltyPoints(PretupsI.C2S_MODULE, _c2sTransferVO.getTransferID(), loyaltyVO);
										}
									} else {
										_loyaltyBL.distributeLoyaltyPoints(PretupsI.C2S_MODULE, _c2sTransferVO.getTransferID(), loyaltyVO);
									}
								}
							} catch (RuntimeException e) {
								_log.errorTrace(methodName, e);

							} finally {
								_log.info(methodName, "inside finally");
							}
						} else {
							_log.info("process", "Exception during LMS Module.SetId not found");
						}

					} else {
						if (!BTSLUtil.isNullString(_channelUserVO.getLmsProfile())) {
							final LoyaltyBL _loyaltyBL = new LoyaltyBL();
							final LoyaltyVO loyaltyVO = new LoyaltyVO();
							loyaltyVO.setServiceType(_serviceType);
							loyaltyVO.setModuleType(PretupsI.C2S_MODULE);
							loyaltyVO.setTransferamt(_c2sTransferVO.getTransferValue());
							loyaltyVO.setCategory(_c2sTransferVO.getCategoryCode());
							loyaltyVO.setUserid(_c2sTransferVO.getActiveUserId());
							loyaltyVO.setNetworkCode(_c2sTransferVO.getNetworkCode());
							loyaltyVO.setSenderMsisdn(_c2sTransferVO.getSenderMsisdn());
							loyaltyVO.setTxnId(_c2sTransferVO.getTransferID());
							loyaltyVO.setCreatedOn(_c2sTransferVO.getCreatedOn());
							loyaltyVO.setProductCode(_c2sTransferVO.getProductCode());
							loyaltyVO.setSetId(_channelUserVO.getLmsProfile());
							_loyaltyBL.distributeLoyaltyPoints(PretupsI.C2S_MODULE, _c2sTransferVO.getTransferID(), loyaltyVO);
						} else {
							_log.info("process", "Exception during LMS Module.SetId not found");
						}
					}
				} catch (Exception ex) {
					loggerValue.setLength(0);
	            	loggerValue.append("Exception durign LMS Module ");
	            	loggerValue.append(ex.getMessage());
					_log.error("process", loggerValue );
					_log.errorTrace(methodName, ex);
				}
			}
			if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
				try {
					if (mcomCon == null) {
						mcomCon = new MComConnection();
						con = mcomCon.getConnection();
					}
					boolean statusAllowed = false;
					final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(_channelUserVO.getNetworkID(), _channelUserVO.getCategoryCode(), _channelUserVO
							.getUserType(), _requestVO.getRequestGatewayType());
					if (userStatusVO == null) {
						throw new BTSLBaseException("C2SPrepaidController", "process", PretupsErrorCodesI.ERROR_USERSTATUS_NOTCONFIGURED);
					} else {
						final String userStatusAllowed = userStatusVO.getUserSenderAllowed();
						final String status[] = userStatusAllowed.split(",");
						for (int i = 0; i < status.length; i++) {
							if (status[i].equals(_channelUserVO.getStatus())) {
								statusAllowed = true;
							}
						}
						String txnSenderUserStatusChang = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_SENDER_USER_STATUS_CHANG));
						PretupsBL.chkAllwdStatusToBecomeActive(con, txnSenderUserStatusChang, _channelUserVO.getUserID(), _channelUserVO.getStatus());
						mcomCon.partialCommit();
					}
				} catch (Exception ex) {
					try {mcomCon.partialRollback();} catch (SQLException sqle) { _log.errorTrace(methodName, sqle);}
					loggerValue.setLength(0);
	            	loggerValue.append("Exception while changing user state to active  ");
	            	loggerValue.append(ex.getMessage());
					_log.error("process",  loggerValue);
					_log.errorTrace(methodName, ex);
				} finally {
					_log.info(methodName, "inside finally");
				}
			}
			boolean lowBasedAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LOW_BASED_ALLOWED);
			boolean fnfZbAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.FNF_ZB_ALLOWED);
			if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
				try {
					if(lowBasedAllowed){
						if(!BTSLUtil.isNullString(_requestVO.getSid()))
							_c2sTransferVO.setSID(_requestVO.getSid());	
						_c2sTransferVO.setReceiverMsisdn(_receiverMSISDN);
						ProcessLowBaseThread processLowBaseThread=new ProcessLowBaseThread(_c2sTransferVO,_senderLocale);
						executorLB.submit(processLowBaseThread);
					}
					if(fnfZbAllowed){
						_c2sTransferVO.setReceiverMsisdn(_receiverMSISDN);
						ProcessZBAndFNFThread processZBAndFNFThread=new ProcessZBAndFNFThread(_c2sTransferVO,_receiverLocale);
						executorZB.submit(processZBAndFNFThread);
					}
				}catch (Exception ex) {
					loggerValue.setLength(0);
	            	loggerValue.append("Exception while processing ZB and FNF  ");
	            	loggerValue.append(ex.getMessage());
					_log.error("process",  loggerValue);
					_log.errorTrace(methodName, ex);
				} 
			}
			if (_log.isDebugEnabled()) {
				loggerValue.setLength(0);
            	loggerValue.append("For _transferID=");
            	loggerValue.append(_transferID);
            	loggerValue.append(" Diff applicable=");
            	loggerValue.append(_c2sTransferVO.getDifferentialApplicable());
            	loggerValue.append(" Diff Given=" );
            	loggerValue.append(_c2sTransferVO.getDifferentialGiven());
				_log.debug("C2SPrepaidController", loggerValue);
			}
		} catch (BTSLBaseException be) {
			_requestVO.setSuccessTxn(false);
			if (be.isKey()) {
				if (_c2sTransferVO.getErrorCode() == null) {
					_c2sTransferVO.setErrorCode(be.getMessageKey());
				}
				_requestVO.setMessageCode(be.getMessageKey());
				_requestVO.setMessageArguments(be.getArgs());
			} else {
				_c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			}
			if (be.isKey() && _c2sTransferVO.getSenderReturnMessage() == null) {
				btslMessages = be.getBtslMessages();
			} else if (_c2sTransferVO.getSenderReturnMessage() == null) {
				_c2sTransferVO.setSenderReturnMessage(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, _transferID, "Error Code:" + _c2sTransferVO.getErrorCode());
			}
			if (_c2sTransferVO.getMsgGatewayFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD) || _c2sTransferVO.getMsgGatewayFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON)) {
				ReqNetworkServiceLoadController.increaseRechargeCounters(_requestVO.getInstanceID(), _requestVO.getMessageGatewayVO().getGatewayType(), _senderNetworkCode,_serviceType, _transferID, LoadControllerI.COUNTER_FAIL_REQUEST, 0, false, _receiverVO.getNetworkCode());
				ReqNetworkServiceLoadController.decrementUnderProcessCounters(_requestVO.getInstanceID(), _requestVO.getMessageGatewayVO().getGatewayType(),_senderNetworkCode, _serviceType, _transferID, LoadControllerI.COUNTER_UNDERPROCESS_REQUEST, 0, false, _receiverVO.getNetworkCode());
			}
			_log.errorTrace(methodName, be);
		} catch (Exception e) {
			_requestVO.setSuccessTxn(false);
			_log.errorTrace(methodName, e);
			if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
				_c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			}
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SPrepaidController[run]", _transferID,_senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
			btslMessages = new BTSLMessages(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			if (_c2sTransferVO.getMsgGatewayFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD) || _c2sTransferVO.getMsgGatewayFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON)) {
				ReqNetworkServiceLoadController.increaseRechargeCounters(_requestVO.getInstanceID(), _requestVO.getMessageGatewayVO().getGatewayType(), _senderNetworkCode,_serviceType, _transferID, LoadControllerI.COUNTER_FAIL_REQUEST, 0, false, _receiverVO.getNetworkCode());
				ReqNetworkServiceLoadController.decrementUnderProcessCounters(_requestVO.getInstanceID(), _requestVO.getMessageGatewayVO().getGatewayType(),_senderNetworkCode, _serviceType, _transferID, LoadControllerI.COUNTER_UNDERPROCESS_REQUEST, 0, false, _receiverVO.getNetworkCode());
			}
		}
		finally {
			try {
				if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL) && (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey())) {
					_c2sTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.C2S_RECEIVER_FAIL), new String[] { String.valueOf(_transferID), PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()) }));
				}
				LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
				if (mcomCon == null) {
					mcomCon = new MComConnection();
					con = mcomCon.getConnection();
				}
				if (_receiverVO != null && _receiverVO.isUnmarkRequestStatus()) {
					try {
						PretupsBL.unmarkReceiverLastRequest(con, _transferID, _receiverVO);
					}
					catch (BTSLBaseException be) {
						_log.errorTrace(methodName, be);
					} catch (Exception e) {
						_log.errorTrace(methodName, e);
						loggerValue.setLength(0);
		            	loggerValue.append("Exception while updating Receiver last request status in database , Exception:");
		            	loggerValue.append(e.getMessage());
						EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED,
								EventLevelI.MAJOR, "C2SPrepaidController[run]",
								_transferID,_senderMSISDN, _senderNetworkCode,  loggerValue.toString() );
					}
				}
				if (_finalTransferStatusUpdate) {
					_c2sTransferVO.setModifiedOn(new Date());
					_c2sTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
					if (!dbEntryNotAllowed) {
						try {
							ChannelTransferBL.updateC2STransferDetails(con, _c2sTransferVO);
						} catch (BTSLBaseException be) {
							_log.errorTrace(methodName, be);
						} catch (Exception e) {
							_log.errorTrace(methodName, e);
							loggerValue.setLength(0);
			            	loggerValue.append("Exception while updating Receiver last request status in database , ");
			            	loggerValue.append("Exception:");
			            	loggerValue.append(e.getMessage());
							EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED,
									EventLevelI.MAJOR, "C2SPrepaidController[run]", _transferID,_senderMSISDN, 
									_senderNetworkCode, loggerValue.toString() );
						}
					}
				}
				mcomCon.finalCommit();
			} catch (BTSLBaseException be) {
				try {
					mcomCon.finalRollback();
				} catch (SQLException sqle) {
					_log.errorTrace(methodName, sqle);
				}
				_log.errorTrace(methodName, be);
			} catch (Exception e) {
				try {
					mcomCon.finalRollback();
				} catch (SQLException sqle) {
					_log.errorTrace(methodName, sqle);
				}
				_log.errorTrace(methodName, e);
				loggerValue.setLength(0);
            	loggerValue.append("Exception while updating transfer details in database , Exception:");
            	loggerValue.append(e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "C2SPrepaidController[run]", _transferID,_senderMSISDN, _senderNetworkCode,  loggerValue.toString() );
			}finally{
				if(mcomCon != null )mcomCon.close("C2SPrepaidController#run");
				mcomCon = null;
				con = null;
			}
			
			String requestGW = _c2sTransferVO.getRequestGatewayCode();
			if (!BTSLUtil.isNullString(_RecAlternetGatewaySMS) && (_RecAlternetGatewaySMS.split(":")).length >= 2) {
				if (requestGW.equalsIgnoreCase(_RecAlternetGatewaySMS.split(":")[0])) {
					requestGW = (_RecAlternetGatewaySMS.split(":")[1]).trim();
					if (_log.isDebugEnabled()) {
						loggerValue.setLength(0);
		            	loggerValue.append("Requested GW was:" );
		            	loggerValue.append(_c2sTransferVO.getRequestGatewayCode());
						_log.debug("process: Reciver Message push through alternate GW", requestGW, loggerValue);
					}
				}
			}
			if (!_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS) && _requestVO.isDecreaseGroupTypeCounter() && ((ChannelUserVO) _requestVO
					.getSenderVO()).getUserControlGrouptypeCounters() != null) {
				PretupsBL.decreaseGroupTypeCounters(((ChannelUserVO) _requestVO.getSenderVO()).getUserControlGrouptypeCounters());
			}
			if (_receiverMessageSendReq && !BTSLUtil.isStringIn(requestGW, _notAllowedRecSendMessGatw) && !"ALL".equals(_notAllowedRecSendMessGatw)) {
				if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS) && !("N".equals(_requestVO.getRecmsg()))) {
					if (_c2sTransferVO.getReceiverReturnMsg() == null) {
						(new PushMessage(_receiverMSISDN, getReceiverSuccessMessage(), _transferID, requestGW, _receiverLocale, _senderNetworkCode, null, _serviceType)).push();
					} else if (_c2sTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
						final BTSLMessages btslRecMessages = (BTSLMessages) _c2sTransferVO.getReceiverReturnMsg();
						(new PushMessage(_receiverMSISDN, BTSLUtil.getMessage(_receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), _transferID,
								requestGW, _receiverLocale)).push();
					} else {
						(new PushMessage(_receiverMSISDN, (String) _c2sTransferVO.getReceiverReturnMsg(), _transferID, requestGW, _receiverLocale, _senderNetworkCode, null, _serviceType)).push();
					}
				} else if (_recTopupFailMessageRequired && _c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS)) {
					if (_c2sTransferVO.getReceiverReturnMsg() == null) {
						(new PushMessage(_receiverMSISDN, getReceiverAmbigousMessage(), _transferID, requestGW, _receiverLocale, _senderNetworkCode, null, _serviceType)).push();
					} else if (_c2sTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
						final BTSLMessages btslRecMessages = (BTSLMessages) _c2sTransferVO.getReceiverReturnMsg();
						(new PushMessage(_receiverMSISDN, BTSLUtil.getMessage(_receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), _transferID,
								_c2sTransferVO.getRequestGatewayCode(), _receiverLocale, _senderNetworkCode, null, _serviceType)).push();
					} else {
						(new PushMessage(_receiverMSISDN, (String) _c2sTransferVO.getReceiverReturnMsg(), _transferID, requestGW, _receiverLocale, _senderNetworkCode, null, _serviceType)).push();
					}
				} else if (_recTopupFailMessageRequired && _c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
					if (_c2sTransferVO.getReceiverReturnMsg() == null) {
						final String message=getReceiverFailMessage( _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()), _senderPushMessageMsisdn, _channelUserVO
								.getUserName(), _requestVO.getPosUserMSISDN(),_receiverLocale, PretupsErrorCodesI.C2S_RECEIVER_FAIL_KEY,_requestVO.getRequestGatewayType());
						(new PushMessage(_receiverMSISDN, message, _transferID, requestGW, _receiverLocale, _senderNetworkCode, null, _serviceType)).push();
					} else if (_c2sTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
						final BTSLMessages btslRecMessages = (BTSLMessages) _c2sTransferVO.getReceiverReturnMsg();
						(new PushMessage(_receiverMSISDN, BTSLUtil.getMessage(_receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), _transferID,
								_c2sTransferVO.getRequestGatewayCode(), _receiverLocale, _senderNetworkCode, null, _serviceType)).push();
					} else {
						(new PushMessage(_receiverMSISDN, (String) _c2sTransferVO.getReceiverReturnMsg(), _transferID, requestGW, _receiverLocale, _senderNetworkCode, null, _serviceType)).push();
					}
				}
			}

			if(_ussdReceiverMessageSendReq &&!BTSLUtil.isStringIn(requestGW,_notAllowedRecSendMessGatw)&&!"ALL".equals(_notAllowedRecSendMessGatw))
			{
				if(_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS))
				{
					
					if(_c2sTransferVO.getReceiverReturnMsg()==null)
						(new USSDPushMessage(_receiverMSISDN,getReceiverSuccessMessage(),_transferID,requestGW,_receiverLocale)).push();
					else if(_c2sTransferVO.getReceiverReturnMsg()!=null&&((BTSLMessages)_c2sTransferVO.getReceiverReturnMsg()).isKey())
					{
						BTSLMessages btslRecMessages=(BTSLMessages)_c2sTransferVO.getReceiverReturnMsg();
						(new USSDPushMessage(_receiverMSISDN,BTSLUtil.getMessage(_receiverLocale,btslRecMessages.getMessageKey(),btslRecMessages.getArgs()),_transferID,requestGW,_receiverLocale)).push();
					}
					else 
						(new USSDPushMessage(_receiverMSISDN,(String)_c2sTransferVO.getReceiverReturnMsg(),_transferID,requestGW,_receiverLocale)).push();
				}
				else if(_recTopupFailMessageRequired&&_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS))
				{
					if(_c2sTransferVO.getReceiverReturnMsg()==null)
						(new USSDPushMessage(_receiverMSISDN,getReceiverAmbigousMessage(),_transferID,requestGW,_receiverLocale)).push();
					else if(_c2sTransferVO.getReceiverReturnMsg()!=null&&((BTSLMessages)_c2sTransferVO.getReceiverReturnMsg()).isKey())
					{
						BTSLMessages btslRecMessages=(BTSLMessages)_c2sTransferVO.getReceiverReturnMsg();
						(new USSDPushMessage(_receiverMSISDN,BTSLUtil.getMessage(_receiverLocale,btslRecMessages.getMessageKey(),btslRecMessages.getArgs()),_transferID,_c2sTransferVO.getRequestGatewayCode(),_receiverLocale)).push();
					}
					else 
						(new USSDPushMessage(_receiverMSISDN,(String)_c2sTransferVO.getReceiverReturnMsg(),_transferID,requestGW,_receiverLocale)).push();
				}
				else if(_recTopupFailMessageRequired && _c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL))
				{
					if(_c2sTransferVO.getReceiverReturnMsg()==null) {
						final String message=getReceiverFailMessage( _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()), _senderPushMessageMsisdn, _channelUserVO
								.getUserName(), _requestVO.getPosUserMSISDN(),_receiverLocale, PretupsErrorCodesI.C2S_RECEIVER_FAIL_KEY,_requestVO.getRequestGatewayType());
					
						(new USSDPushMessage(_receiverMSISDN,message,_transferID,requestGW,_receiverLocale)).push();
					}
					else if(_c2sTransferVO.getReceiverReturnMsg()!=null&&((BTSLMessages)_c2sTransferVO.getReceiverReturnMsg()).isKey())
					{
						BTSLMessages btslRecMessages=(BTSLMessages)_c2sTransferVO.getReceiverReturnMsg();
						(new USSDPushMessage(_receiverMSISDN,BTSLUtil.getMessage(_receiverLocale,btslRecMessages.getMessageKey(),btslRecMessages.getArgs()),_transferID,_c2sTransferVO.getRequestGatewayCode(),_receiverLocale)).push();
					}
					else
						(new USSDPushMessage(_receiverMSISDN,(String)_c2sTransferVO.getReceiverReturnMsg(),_transferID,requestGW,_receiverLocale)).push();
				}
			}
			
			int messageLength = 0;
			final String messLength = BTSLUtil.NullToString(Constants.getProperty("MSG_LENGTH_GW"));
			if (!BTSLUtil.isNullString(messLength)) {
				messageLength = (new Integer(messLength)).intValue();
			}
			boolean addCommSeparateMsg = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.ADD_COMM_SEPARATE_MSG);
			if (!BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(), _notAllowedSendMessGatw)) {
				PushMessage pushMessages = null;
				PushMessage pushRoamPenalty = null;
				PushMessage pushMessages2=null;
				if (btslMessages != null) {
					pushMessages = (new PushMessage(_senderPushMessageMsisdn, BTSLUtil.getMessage(_senderLocale, btslMessages.getMessageKey(), btslMessages.getArgs()),
							_transferID, _c2sTransferVO.getRequestGatewayCode(), _senderLocale, _senderNetworkCode, null, _serviceType));
				} else {
					if(_log.isDebugEnabled()) {
						_log.debug(methodName,"addCommSeparateMsg="+addCommSeparateMsg+",is additional comminion flag ="+!BTSLUtil.isNullString(_c2sTransferVO.getSenderReturnMessage()));
					}
					if (!BTSLUtil.isNullString(_c2sTransferVO.getSenderReturnMessage())) {
						if(addCommSeparateMsg) {
							if(_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)){
								pushMessages = (new PushMessage(_senderPushMessageMsisdn, senderReturnMessage, _transferID, _c2sTransferVO.getRequestGatewayCode(), _senderLocale));
								pushMessages2 = (new PushMessage(_senderPushMessageMsisdn, _c2sTransferVO.getSenderReturnMessage(), _transferID, _c2sTransferVO.getRequestGatewayCode(), _senderLocale));
							} else {
								pushMessages = new PushMessage(_senderPushMessageMsisdn, _c2sTransferVO.getSenderReturnMessage(), _transferID, _c2sTransferVO.getRequestGatewayCode(), _senderLocale);
							}
						} else {
							if(_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)){
								pushMessages=new PushMessage(_senderPushMessageMsisdn,getSenderSuccessMessage(),_transferID,_c2sTransferVO.getRequestGatewayCode(),_senderLocale);
							} else {
								pushMessages=new PushMessage(_senderPushMessageMsisdn,_c2sTransferVO.getSenderReturnMessage(),_transferID,_c2sTransferVO.getRequestGatewayCode(),_senderLocale);
							}
						}
					} else if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
						pushMessages = (new PushMessage(_senderPushMessageMsisdn, senderReturnMessage, _transferID, _c2sTransferVO.getRequestGatewayCode(),
								_senderLocale, _senderNetworkCode, null, _serviceType));
					}
					
					 if (!BTSLUtil.isNullString(_c2sTransferVO.getSenderReturnPromoMessage())) {
                        if(_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS))
                        {
                        new PushMessage(_senderPushMessageMsisdn, _c2sTransferVO.getSenderReturnPromoMessage(), _transferID,
                            _c2sTransferVO.getRequestGatewayCode(), _senderLocale, _senderNetworkCode, null, _serviceType).push();
                        }
                    }
					
					if ((!requestGW.equals(_c2sTransferVO.getRequestGatewayCode())) && (!BTSLUtil.isNullString(_c2sTransferVO.getSenderReturnMessage()))) {
						pushMessages = (new PushMessage(_senderPushMessageMsisdn, getSenderSuccessMessage(), _transferID, _c2sTransferVO.getRequestGatewayCode(),
								_senderLocale, _senderNetworkCode, null, _serviceType));
					}
				}
				String grptChrgAllowed = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CHRG_ALLOWED));
				if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS) && grptChrgAllowed != null && grptChrgAllowed
						.indexOf(_requestVO.getRequestGatewayType()) != -1 && !PretupsI.NOT_APPLICABLE.equals(_requestVO.getGroupType())) {
					try {
						GroupTypeProfileVO groupTypeProfileVO = null;
						groupTypeProfileVO = PretupsBL.loadAndCheckC2SGroupTypeCounters(_requestVO, PretupsI.GRPT_TYPE_CHARGING);
						if (groupTypeProfileVO != null && groupTypeProfileVO.isGroupTypeCounterReach()) {
							pushMessages.push(groupTypeProfileVO.getGatewayCode(), groupTypeProfileVO.getAltGatewayCode());// new
							SMSChargingLog.log(((ChannelUserVO) _requestVO.getSenderVO()).getUserID(), (((ChannelUserVO) _requestVO.getSenderVO())
									.getUserChargeGrouptypeCounters()).getCounters(), groupTypeProfileVO.getThresholdValue(), groupTypeProfileVO.getReqGatewayType(),
									groupTypeProfileVO.getResGatewayType(), groupTypeProfileVO.getNetworkCode(), _requestVO.getGroupType(), _requestVO.getServiceType(),
									_requestVO.getModule());
						} else {
							if(!BTSLUtil.isNullString(_c2sTransferVO.getSenderReturnMessage())) {
								if(addCommSeparateMsg) {
									try {
										pushMessages.push();
									} catch (RuntimeException e) {
										_log.errorTrace(methodName,e);
									}
									try {
										pushMessages2.push();
									} catch (RuntimeException e) {
										_log.errorTrace(methodName,e);
									}
								}else {
									pushMessages.push();
								}
							} else {
								pushMessages.push();
							}
						}

					} catch (Exception e) {
						_log.errorTrace(methodName, e);
					}
				} else {
					if(!BTSLUtil.isNullString(_c2sTransferVO.getSenderReturnMessage())) {
						if(addCommSeparateMsg) {
							try {
								pushMessages.push();
							} catch (RuntimeException e) {
								_log.errorTrace(methodName,e);
							}
							try {
								pushMessages2.push();
							} catch (RuntimeException e) {
								_log.errorTrace(methodName,e);
							}
						}else {
							pushMessages.push();
						}
					} else {
						pushMessages.push();
					}
				}
				final String message = getSenderSuccessMessage();
				if ((!requestGW.equalsIgnoreCase(_c2sTransferVO.getRequestGatewayCode())) && (message.length() < messageLength)) {
					PushMessage pushMessages1 = null;
					if (!BTSLUtil.isNullString(_c2sTransferVO.getSenderReturnMessage())) {
						pushMessages1 = (new PushMessage(_senderMSISDN, _c2sTransferVO.getSenderReturnMessage(), _transferID, requestGW, _senderLocale, _senderNetworkCode, null, _serviceType));
					} else {
						pushMessages1 = (new PushMessage(_senderMSISDN, message, _transferID, requestGW, _senderLocale, _senderNetworkCode, null, _serviceType));
					}
					pushMessages1.push();
				}
				if (!BTSLUtil.isNullString(_requestVO.getParentMsisdnPOS()) && !("".equals(_requestVO.getParentMsisdnPOS())) && _channelUserVO.isStaffUser()) {
					final PushMessage pushParentMessages = (new PushMessage(_requestVO.getParentMsisdnPOS(), getSenderParentSuccessMessage(), _transferID, requestGW,
							_senderLocale, _senderNetworkCode, null, _serviceType));
					pushParentMessages.push();
				}
				if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS) && penaltyApplicable) {
					final BTSLMessages roamPenaltymessage = new BTSLMessages(PretupsErrorCodesI.CHNL_ROAM_PENALTY_MSG, new String[] { PretupsBL
							.getDisplayAmount(_c2sTransferVO.getRoamPenalty()), _c2sTransferVO.getTransferID(), PretupsBL
							.getDisplayAmount(c2STransferOwnerVO!=null ? c2STransferOwnerVO.getRoamPenalty():0),PretupsBL.getDisplayAmount(_c2sTransferVO.getRoamPenalty()+(c2STransferOwnerVO!=null ? c2STransferOwnerVO.getRoamPenalty():0)) });
					pushRoamPenalty = (new PushMessage(_senderMSISDN, roamPenaltymessage, _transferID, _c2sTransferVO.getRequestGatewayCode(), _senderLocale,
							_senderNetworkCode, null, _serviceType));
					pushRoamPenalty.push();
				}
				if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS) && penaltyApplicableOwner) {
					final BTSLMessages roamPenaltymessageOwner = new BTSLMessages(PretupsErrorCodesI.CHNL_OWNR_ROAM_PENALTY_MSG, new String[] { PretupsBL
							.getDisplayAmount(c2STransferOwnerVO.getRoamPenalty()), _c2sTransferVO.getTransferID(), ((ChannelUserVO) _c2sTransferVO.getSenderVO()).getUserName(),
							PretupsBL.getDisplayAmount(_c2sTransferVO.getRoamPenalty()+c2STransferOwnerVO.getRoamPenalty())});
					final Locale ownerLocale = new Locale(c2STransferOwnerVO.getOwnerUserVO().getLanguage(), c2STransferOwnerVO.getOwnerUserVO().getCountryCode());
					final PushMessage pushRoamPenaltyOwner = (new PushMessage(c2STransferOwnerVO.getOwnerUserVO().getMsisdn(), roamPenaltymessageOwner, _transferID,
							_c2sTransferVO.getRequestGatewayCode(), ownerLocale, _senderNetworkCode, null, _serviceType));
					pushRoamPenaltyOwner.push();
				}
			}
			if (_creditBackEntryDone) {
				BalanceLogger.log(_userBalancesVO);
			}
			if (!_oneLog) {
				OneLineTXNLog.log(_c2sTransferVO, _senderTransferItemVO, _receiverTransferItemVO);
			}
			TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
					"Transaction Ending", PretupsI.TXN_LOG_STATUS_SUCCESS,
					"Trans Status=" + _c2sTransferVO.getTransferStatus() + " Error Code=" + _c2sTransferVO.getErrorCode() + " Diff Appl=" + _c2sTransferVO
					.getDifferentialApplicable() + " Diff Given=" + _c2sTransferVO.getDifferentialGiven() + " Message=" + _c2sTransferVO.getSenderReturnMessage());
			ChannelRequestDailyLog.log(ChannelRequestDailyLog.populateChannelRequestDailyLogVO(_requestVO, _c2sTransferVO));
			btslMessages = null;
			_userBalancesVO = null;
			commonClient = null;
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, _transferID, "Exiting");
			}
		}
	}

	private void populateVOFromRequest(RequestVO p_requestVO) {
		_c2sTransferVO.setSenderVO(_channelUserVO);
		_c2sTransferVO.setRequestID(p_requestVO.getRequestIDStr());
		_c2sTransferVO.setModule(p_requestVO.getModule());
		_c2sTransferVO.setInstanceID(p_requestVO.getInstanceID());
		_c2sTransferVO.setRequestGatewayCode(p_requestVO.getRequestGatewayCode());
		_c2sTransferVO.setRequestGatewayType(p_requestVO.getRequestGatewayType());
		_c2sTransferVO.setServiceType(p_requestVO.getServiceType());
		_c2sTransferVO.setSourceType(p_requestVO.getSourceType());
		_c2sTransferVO.setCreatedOn(_currentDate);
		_c2sTransferVO.setCreatedBy(_channelUserVO.getUserID());
		_c2sTransferVO.setModifiedOn(_currentDate);
		_c2sTransferVO.setModifiedBy(_channelUserVO.getUserID());
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
		_c2sTransferVO.setCategoryCode(_channelUserVO.getCategoryCode());
		_c2sTransferVO.setInfo1(p_requestVO.getInfo1());
		_c2sTransferVO.setInfo2(p_requestVO.getInfo2());
		_c2sTransferVO.setInfo3(p_requestVO.getInfo3());
		_c2sTransferVO.setInfo4(p_requestVO.getInfo4());
		_c2sTransferVO.setInfo5(p_requestVO.getInfo5());
		_c2sTransferVO.setInfo6(p_requestVO.getInfo6());
		_c2sTransferVO.setInfo7(p_requestVO.getInfo7());
		_c2sTransferVO.setInfo8(p_requestVO.getInfo8());
		_c2sTransferVO.setInfo9(p_requestVO.getInfo9());
		_c2sTransferVO.setInfo10(p_requestVO.getInfo10());
		 if((boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USERWISE_LOAN_ENABLE)  ) {
				_c2sTransferVO.setUserLoanVOList(_channelUserVO.getUserLoanVOList());
			
		}
		else if((boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)){
			ArrayList<ChannelSoSVO>  chnlSoSVOList = new ArrayList<> ();
			chnlSoSVOList.add(new ChannelSoSVO(_channelUserVO.getUserID(),_channelUserVO.getMsisdn(),_channelUserVO.getSosAllowed(),_channelUserVO.getSosAllowedAmount(),_channelUserVO.getSosThresholdLimit()));
			_c2sTransferVO.setChannelSoSVOList(chnlSoSVOList);
		}
	}

	public void updateForReceiverValidateResponse(String str) throws BTSLBaseException {
		final String methodName = "updateForReceiverValidateResponse";
		final HashMap map = BTSLUtil.getStringToHash(str, "&", "=");
		StringBuilder loggerValue= new StringBuilder(); 
		final String status = (String) map.get("TRANSACTION_STATUS");
		ArrayList altList = null;
		boolean isRequired = false;
		boolean serviceProviderPromoAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SERVICE_PROVIDER_PROMO_ALLOW);
		_receiverTransferItemVO.setInterfaceResponseCode((String)map.get("INTERFACE_STATUS"));
		_receiverVO.setInterfaceResponseCode(_receiverTransferItemVO.getInterfaceResponseCode());
		try{_requestVO.setInValidateURL((String)map.get("IP")); _c2sTransferVO.setInfo10(_requestVO.getInValidateURL());}catch(Exception e){_log.errorTrace(methodName, e);}
		try{_requestVO.setValINRespCode(_receiverVO.getInterfaceResponseCode());_c2sTransferVO.setInfo6(_requestVO.getValINRespCode());}catch(Exception ex){_log.errorTrace(methodName, ex);}
		try{_c2sTransferVO.setInfo9((String)map.get("SERVICE_CLASS"));}catch(Exception ex1){_log.errorTrace(methodName, ex1);}
		if (null != map.get("IN_START_TIME")) {
			_requestVO.setValidationReceiverRequestSent(((Long.valueOf((String) map.get("IN_START_TIME"))).longValue()));
		}
		if (null != map.get("IN_END_TIME")) {
			_requestVO.setValidationReceiverResponseReceived(((Long.valueOf((String) map.get("IN_END_TIME"))).longValue()));
		}

		final String interfaceStatusType = (String) map.get("INT_SET_STATUS");
		if (!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME.equals(interfaceStatusType))) {
			new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES, _receiverTransferItemVO.getInterfaceID(), interfaceStatusType,PretupsErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG, PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
		}
		if (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status)) {
			_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			altList = InterfaceRoutingControlCache.getRoutingControlDetails(_receiverTransferItemVO.getInterfaceID());
			if (altList != null && !altList.isEmpty()) {
				performAlternateRouting(altList); // Performing alternate
			} else {
				isRequired = true;
			}
		}
		if (!InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) || isRequired) {
			if ("Y".equals(_requestVO.getUseInterfaceLanguage())) {
				updateReceiverLocale((String) map.get("IN_LANG"));
			}
			_receiverTransferItemVO.setProtocolStatus((String) map.get("PROTOCOL_STATUS"));
			try {
				_receiverTransferItemVO.setAccountStatus(URLDecoder.decode((String) map.get("ACCOUNT_STATUS")));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			if (BTSLUtil.isNullString(_receiverTransferItemVO.getAccountStatus())) {
				_receiverTransferItemVO.setAccountStatus(" ");
			}
			_receiverTransferItemVO.setInterfaceResponseCode((String) map.get("INTERFACE_STATUS"));
			_receiverTransferItemVO.setValidationStatus(status);
			_receiverVO.setInterfaceResponseCode(_receiverTransferItemVO.getInterfaceResponseCode());
			_receiverTransferItemVO.setInterfaceReferenceID((String) map.get("IN_TXN_ID"));
			_receiverTransferItemVO.setReferenceID((String) map.get("IN_RECON_ID"));
			_receiverTransferItemVO.setSubscriberType(_receiverSubscriberType);
			String[] strArr ;
			if (BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS)) {
				_c2sTransferVO.setErrorCode(status + "_R");
				_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
				_receiverTransferItemVO.setTransferStatus(status);
				_senderTransferItemVO.setValidationStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
				_senderTransferItemVO.setTransferStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
				strArr = new String[] { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()) };
				if (InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED.equals(_receiverTransferItemVO.getValidationStatus())) {
					throw new BTSLBaseException("C2SPrepaidController", methodName, InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED + "_S", 0, strArr, null);
				} else {
					throw new BTSLBaseException("C2SPrepaidController", methodName, _c2sTransferVO.getErrorCode(), 0, strArr, null);
				}
			}
			_receiverTransferItemVO.setTransferStatus(status);
			_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
			final SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache
					.getRoutingControlDetails(_c2sTransferVO.getReceiverNetworkCode() + "_" + _c2sTransferVO.getServiceType() + "_" + _type);
			if (!_receiverInterfaceInfoInDBFound && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
				PretupsBL.insertSubscriberInterfaceRouting(_receiverTransferItemVO.getInterfaceID(), _externalID, _receiverMSISDN, _type, _channelUserVO.getUserID(),
						_currentDate);
				_receiverInterfaceInfoInDBFound = true;
			}
			try {
				_receiverTransferItemVO.setPreviousExpiry(BTSLUtil.getDateFromDateString((String) map.get("OLD_EXPIRY_DATE"), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
			} catch (Exception e) {
				loggerValue.setLength(0);
            	loggerValue.append("Exception ");
            	loggerValue.append(e);
				_log.error(methodName, loggerValue );
				_log.errorTrace(methodName, e);
			}
			try {
				_receiverTransferItemVO.setPreviousGraceDate(BTSLUtil.getDateFromDateString((String) map.get("OLD_GRACE_DATE"), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
			} catch (Exception e) {
				loggerValue.setLength(0);
            	loggerValue.append("Exception ");
            	loggerValue.append(e);
				_log.errorTrace(methodName, e);
				
			}
			if(_subValRequired){  
				_receiverTransferItemVO.setServiceClassCode((String) map.get("SERVICE_CLASS"));
				_receiverVO.setServiceClassCode(_receiverTransferItemVO.getServiceClass());
			}
			_receiverVO.setInterfaceResponseCode(_receiverTransferItemVO.getInterfaceResponseCode());
			_receiverTransferItemVO.setOldExporyInMillis((String) map.get("CAL_OLD_EXPIRY_DATE"));// @nu
			if(!BTSLUtil.isNullString((String) map.get("INTERFACE_PREV_BALANCE")))
            {
			try {
				_receiverTransferItemVO.setPreviousBalance(Long.parseLong((String) map.get("INTERFACE_PREV_BALANCE")));
			} catch (Exception e) {
				loggerValue.setLength(0);
            	loggerValue.append("Exception ");
            	loggerValue.append(e);
				_log.error(methodName, loggerValue);
				_log.errorTrace(methodName, e);
			}
 }
			_receiverTransferItemVO.setFirstCall((String) map.get("FIRST_CALL"));
			_receiverTransferItemVO.setGraceDaysStr((String) map.get("GRACE_DAYS"));
			_receiverTransferItemVO.setBundleTypes((String) map.get("IN_RESP_BUNDLE_CODES"));
			_receiverTransferItemVO.setInAccountId((String) map.get("IN_ACCOUNT_ID"));
			_receiverTransferItemVO.setSelectorName(_selectorName);
			if (_receiverTransferItemVO.getPreviousExpiry() == null) {
				_receiverTransferItemVO.setPreviousExpiry(_currentDate);
			}
			_operatorUtil.populateBonusListAfterValidation(map, _c2sTransferVO);
			try {
				final String lmbAllowedValue = (String) map.get("LMB_ALLOWED_VALUE");
				if (!BTSLUtil.isNullString(lmbAllowedValue)) {
					_receiverTransferItemVO.setLmbdebitvalue((Long.valueOf(lmbAllowedValue)));
				}
			} catch (Exception e) {
				loggerValue.setLength(0);
            	loggerValue.append("Exception ");
            	loggerValue.append(e);
				_log.errorTrace(methodName, e);
			}
			_c2sTransferVO.setPreviousPromoExpiry((String) map.get("PROMO_OLD_EXPIRY_DATE"));
			_c2sTransferVO.setPreviousPromoBalance((String) map.get("INTERFACE_PROMO_PREV_BALANCE"));
			_receiverTransferItemVO.setPreviousExpiryInCal((String) map.get("CAL_OLD_EXPIRY_DATE"));
			_receiverTransferItemVO.setPreviousPromoExpiryInCal((String) map.get("PROMO_CAL_OLD_EXPIRY_DATE"));
			if (serviceProviderPromoAllow) {
				_receiverTransferItemVO.setServiceProviderName(BTSLUtil.NullToString((String) map.get("SPNAME")));
				_c2sTransferVO.setServiceProviderName(BTSLUtil.NullToString((String) map.get("SPNAME")));
			}
			try{_requestVO.setInValidateURL((String)map.get("IP"));	_c2sTransferVO.setInfo10(_requestVO.getInValidateURL());}catch(Exception e){
				loggerValue.setLength(0);
            	loggerValue.append("Exception ");
            	loggerValue.append(e);
				_log.error(methodName, loggerValue);
				_log.errorTrace(methodName, e);}
			try{_requestVO.setValINRespCode(_receiverVO.getInterfaceResponseCode());_c2sTransferVO.setInfo6(_requestVO.getValINRespCode());}catch(Exception ex){
				loggerValue.setLength(0);
            	loggerValue.append("Exception ");
            	loggerValue.append(ex);
				_log.error(methodName, loggerValue);
				_log.errorTrace(methodName, ex);}
			try{_c2sTransferVO.setInfo9((String)map.get("SERVICE_CLASS"));}catch(Exception ex1){
				loggerValue.setLength(0);
            	loggerValue.append("Exception ");
            	loggerValue.append(ex1);
				_log.error(methodName, loggerValue);
				_log.errorTrace(methodName, ex1);}
		}
		
		Boolean isCellIDRequiredFromIN = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_CELLID_REQUIRED_FROM_IN);
		if (isCellIDRequiredFromIN ) {
			String cellId= (String) map.get(XmlTagValueConstant.TAG_CELLID);

			if(!BTSLUtil.isNullString(cellId)) {
				_requestVO.setCellId(cellId);
				_c2sTransferVO.setCellId(cellId);
				try {
					final String geoFencingAlertReq = (String) map.get(PretupsI.TAG_GEO_FENCING_ALERT_REQ);
					if (!BTSLUtil.isNullString(geoFencingAlertReq)) {
						if(PretupsI.AGENT_ALLOWED_YES.equals(geoFencingAlertReq)){
							_c2sTransferVO.setInGeoFencing(false);
							final String senderMessage = BTSLUtil.getMessage(_requestVO.getLocale(), PretupsErrorCodesI.SEND_ALERT,
									new String[] { _channelUserVO.getMsisdn(), cellId });// requestVO.getMessageCode(),requestVO.getMessageArguments()
							final PushMessage pushMessage = new PushMessage(Constants.getProperty("adminmobile"), senderMessage, _requestVO.getRequestIDStr(), _requestVO
									.getRequestGatewayCode(), _requestVO.getLocale());
							pushMessage.push();
						}
					}
					final String geoFencingAlertReqUser = (String) map.get(PretupsI.TAG_GEO_FENCING_ALERT_REQ_USER);
					if (!BTSLUtil.isNullString(geoFencingAlertReqUser)) {
						if(PretupsI.AGENT_ALLOWED_YES.equals(geoFencingAlertReqUser)){
							_c2sTransferVO.setInGeoFencing(false);
							final String senderUserMessage = BTSLUtil.getMessage(_requestVO.getLocale(), PretupsErrorCodesI.SEND_ALERT_USER,
									new String[] { _channelUserVO.getMsisdn(), cellId });// requestVO.getMessageCode(),requestVO.getMessageArguments()
							final PushMessage pushMessage1 = new PushMessage(_channelUserVO.getMsisdn(), senderUserMessage, _requestVO.getRequestIDStr(), _requestVO
									.getRequestGatewayCode(), _requestVO.getLocale());
							pushMessage1.push();	
						}
					}
				} catch (Exception e) {
					loggerValue.setLength(0);
					loggerValue.append("Exception ");
					loggerValue.append(e);
					_log.errorTrace(methodName, e);
				}
			}
		}		
	}

	public void updateForReceiverCreditResponse(String str) throws BTSLBaseException {
		final String methodName = "updateForReceiverCreditResponse";
		final HashMap map = BTSLUtil.getStringToHash(str, "&", "=");
		StringBuilder loggerValue= new StringBuilder(); 
	    final String status = (String) map.get("TRANSACTION_STATUS");
		//final String status = PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS;
		_receiverTransferItemVO.setInterfaceResponseCode((String)map.get("INTERFACE_STATUS"));
		try{_requestVO.setInCreditURL((String)map.get("IP"));_c2sTransferVO.setInfo7(_requestVO.getInCreditURL());      }catch(Exception e){_log.errorTrace(methodName, e);}
		try{_requestVO.setCreditINRespCode(_receiverVO.getInterfaceResponseCode()); _c2sTransferVO.setInfo8(_requestVO.getCreditINRespCode());}catch(Exception ex){_log.errorTrace(methodName, ex);}
		if (null != map.get("IN_START_TIME")) {
			_requestVO.setTopUPReceiverRequestSent(((Long.valueOf((String) map.get("IN_START_TIME"))).longValue()));
		}
		if (null != map.get("IN_END_TIME")) {
			_requestVO.setTopUPReceiverResponseReceived(((Long.valueOf((String) map.get("IN_END_TIME"))).longValue()));
		}
		try {
			if (!BTSLUtil.isNullString((String) map.get("IN_POSTCREDIT_VAL_TIME"))) {
				_requestVO.setPostValidationTimeTaken(Long.parseLong((String) map.get("IN_POSTCREDIT_VAL_TIME")));
			}
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		try {
			if (!BTSLUtil.isNullString((String) map.get("IN_RECHARGE_TIME"))) {
				_requestVO.setCreditTime(Long.parseLong((String) map.get("IN_RECHARGE_TIME")));
			}
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		try {
			if (!BTSLUtil.isNullString((String) map.get("IN_PROMO_TIME"))) {
				_requestVO.setPromoTime(Long.parseLong((String) map.get("IN_PROMO_TIME")));
			}
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		try {
			if (!BTSLUtil.isNullString((String) map.get("IN_COS_TIME"))) {
				_requestVO.setCosTime(Long.parseLong((String) map.get("IN_COS_TIME")));
			}
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		try {
			if (!BTSLUtil.isNullString((String) map.get("IN_CREDIT_VAL_TIME"))) {
				_requestVO.setCreditValTime(Long.parseLong((String) map.get("IN_CREDIT_VAL_TIME")));
			}
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		try {
			if (!BTSLUtil.isNullString((String) map.get("IN_PROMO_VAL_TIME"))) {
				_requestVO.setPromoValTime(Long.parseLong((String) map.get("IN_PROMO_VAL_TIME")));
			}
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		try {
			if (!BTSLUtil.isNullString((String) map.get("IN_COS_VAL_TIME"))) {
				_requestVO.setCosValTime(Long.parseLong((String) map.get("IN_COS_VAL_TIME")));
			}
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		final String interfaceStatusType = (String) map.get("INT_SET_STATUS");
		if (_log.isDebugEnabled()) {
			loggerValue.setLength(0);
        	loggerValue.append("Mape from response=");
        	loggerValue.append(map);
        	loggerValue.append(" status=");
        	loggerValue.append(status);
        	loggerValue.append(" interface Status=");
        	loggerValue.append(interfaceStatusType);
			_log.debug(methodName, loggerValue );
		}
		if (!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME
				.equals(interfaceStatusType))) {
			new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES, _receiverTransferItemVO.getInterfaceID(), interfaceStatusType,
					PretupsErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG, PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
		}
		if (PretupsI.STATUS_ACTIVE.equals((_channelUserVO.getCategoryVO()).getRestrictedMsisdns())) {
			if (PretupsI.STATUS_ACTIVE.equals((_channelUserVO.getCategoryVO()).getTransferToListOnly())) {
				((RestrictedSubscriberVO) ((ReceiverVO) _c2sTransferVO.getReceiverVO()).getRestrictedSubscriberVO()).setTempStatus(status);
			}
		}
		_receiverTransferItemVO.setProtocolStatus((String) map.get("PROTOCOL_STATUS"));
		_receiverTransferItemVO.setInterfaceResponseCode((String) map.get("INTERFACE_STATUS"));
		String updateStatus = (String) map.get("UPDATE_STATUS");

		if (BTSLUtil.isNullString(updateStatus)) {
			updateStatus = status;
		}
		_receiverTransferItemVO.setUpdateStatus(updateStatus);
		_receiverVO.setInterfaceResponseCode(_receiverTransferItemVO.getInterfaceResponseCode());
		_receiverTransferItemVO.setUpdateStatus1((String) map.get("UPDATE_STATUS1"));
		_receiverTransferItemVO.setUpdateStatus2((String) map.get("UPDATE_STATUS2"));

		if (!BTSLUtil.isNullString((String) map.get("ADJUST_AMOUNT"))) {
			_receiverTransferItemVO.setAdjustValue(Long.parseLong((String) map.get("ADJUST_AMOUNT")));
		}
		_receiverPostBalanceAvailable = (String) map.get("POST_BALANCE_ENQ_SUCCESS");
		if (!BTSLUtil.isNullString((String) map.get("IN_TXN_ID"))) {
			try {
				_receiverTransferItemVO.setInterfaceReferenceID((String) map.get("IN_TXN_ID"));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
		}
		if (!BTSLUtil.isNullString((String) map.get("IN_TXN_ID1"))) {
			try {
				_receiverTransferItemVO.setInterfaceReferenceID1((String) map.get("IN_TXN_ID1"));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			_receiverTransferItemVO.setTransferType1(PretupsI.TRANSFER_TYPE_BA_ADJ_CR);
		}
		if (!BTSLUtil.isNullString((String) map.get("IN_TXN_ID2"))) {
			try {
				_receiverTransferItemVO.setInterfaceReferenceID2((String) map.get("IN_TXN_ID2"));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			_receiverTransferItemVO.setTransferType2(PretupsI.TRANSFER_TYPE_BA_ADJ_DR);
		}
		
        if ("Y".equals(_requestVO.getUseInterfaceLanguage())) {
            // update the receiver locale if language code returned from IN
            // is not null
            updateReceiverLocale((String) map.get("IN_LANG"));
        }
		
		
		
		_receiverTransferItemVO.setReferenceID((String) map.get("IN_RECON_ID"));
		try{_requestVO.setInCreditURL((String)map.get("IP"));_c2sTransferVO.setInfo7(_requestVO.getInCreditURL());	}catch(Exception e){_log.errorTrace(methodName, e);}
		try{_requestVO.setCreditINRespCode(_receiverVO.getInterfaceResponseCode()); _c2sTransferVO.setInfo8(_requestVO.getCreditINRespCode());}catch(Exception ex){_log.errorTrace(methodName, ex);}
		String[] strArr ;
		if (BTSLUtil.isNullString(status) || (!status.equals(InterfaceErrorCodesI.SUCCESS) && !status.equals(InterfaceErrorCodesI.AMBIGOUS))) {
			_c2sTransferVO.setErrorCode(status + "_R");
			_c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			_receiverTransferItemVO.setTransferStatus(status);
			strArr = new String[] { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()) };
			throw new BTSLBaseException(this, "updateForReceiverValidateResponse", _c2sTransferVO.getErrorCode(), 0, strArr, null);
		} else if (status.equals(InterfaceErrorCodesI.AMBIGOUS)) {
			_c2sTransferVO.setErrorCode(status + "_R");
			_c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
			_receiverTransferItemVO.setTransferStatus(status);
			_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
			_receiverTransferItemVO.setUpdateStatus(status);
			if(_subValRequired) 
				_operatorUtil.updateBonusListAfterTopup(map, _c2sTransferVO);
			boolean c2sSndrCreditBkAmbStatus = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_SNDR_CREDIT_BK_AMB_STATUS);
			if(penaltyApplicableOwner && "Y".equals(Constants.getProperty("ALLOW_C2S_RECHARGE_OWNER_PENALTY_MESSAGE")) && !c2sSndrCreditBkAmbStatus)
			{	
				BTSLMessages roamPenaltymessageOwner= new BTSLMessages(PretupsErrorCodesI.TXN_STATUS_RC_OWNER_AMBIGUOUS ,new String[]{PretupsBL.getDisplayAmount(_c2sTransferVO.getRoamPenaltyOwner()),_transferID,((ChannelUserVO)_c2sTransferVO.getSenderVO()).getUserName(),PretupsBL.getDisplayAmount(_penaltyVOCreditOwner.getPostBalance())});
				Locale ownerLocale = new Locale(c2STransferOwnerVO.getOwnerUserVO().getLanguage(), c2STransferOwnerVO.getOwnerUserVO().getCountryCode());
				PushMessage pushRoamPenaltyOwner = (new PushMessage(c2STransferOwnerVO.getOwnerUserVO().getMsisdn(), roamPenaltymessageOwner, _transferID, _c2sTransferVO.getRequestGatewayCode(),ownerLocale,_senderNetworkCode, null, _serviceType));
				pushRoamPenaltyOwner.push();
			}
			if(penaltyApplicableOwner && "Y".equals(Constants.getProperty("ALLOW_C2S_RECHARGE_OWNER_PENALTY_MESSAGE")) && c2sSndrCreditBkAmbStatus)
			{	
				BTSLMessages roamPenaltymessageOwner= new BTSLMessages(PretupsErrorCodesI.TXN_STATUS_RC_OWNER_CREDITBACK_AMBIGUOUS ,new String[]{PretupsBL.getDisplayAmount(_c2sTransferVO.getRoamPenaltyOwner()),_transferID,((ChannelUserVO)_c2sTransferVO.getSenderVO()).getUserName(),PretupsBL.getDisplayAmount(_penaltyVOCreditOwner.getPostBalance()+_c2sTransferVO.getRoamPenaltyOwner())});
				Locale ownerLocale = new Locale(c2STransferOwnerVO.getOwnerUserVO().getLanguage(), c2STransferOwnerVO.getOwnerUserVO().getCountryCode());
				PushMessage pushRoamPenaltyOwner = (new PushMessage(c2STransferOwnerVO.getOwnerUserVO().getMsisdn(), roamPenaltymessageOwner, _transferID, _c2sTransferVO.getRequestGatewayCode(),ownerLocale,_senderNetworkCode, null, _serviceType));
				pushRoamPenaltyOwner.push();
			}
			if(penaltyApplicable){	
				strArr=new String[]{_transferID,PretupsBL.getDisplayAmount(((C2STransferItemVO)(_c2sTransferVO.getTransferItemList().get(0))).getPostBalance()),PretupsBL.getDisplayAmount(_c2sTransferVO.getRoamPenalty())};
				throw new BTSLBaseException(this,"updateForReceiverValidateResponse",PretupsErrorCodesI.TXN_STATUS_RC_AMBIGUOUS1,0,strArr,null);
			}
			else{
				strArr=new String[]{_transferID,PretupsBL.getDisplayAmount(((C2STransferItemVO)(_c2sTransferVO.getTransferItemList().get(0))).getPostBalance())};
				throw new BTSLBaseException(this,"updateForReceiverValidateResponse",PretupsErrorCodesI.TXN_STATUS_RC_AMBIGUOUS,0,strArr,null);
			}

		} else {
			_receiverTransferItemVO.setTransferStatus(status);
			_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
			_receiverTransferItemVO.setUpdateStatus(status);
			if (!BTSLUtil.isNullString((String) map.get("activationBonusProvided"))) {
				_c2sTransferVO.setActiveBonusProvided((String) map.get("activationBonusProvided"));
			} else {
				_c2sTransferVO.setActiveBonusProvided(PretupsI.YES);
			}
			boolean enqPostbalAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.ENQ_POSTBAL_ALLOW);
			if (enqPostbalAllow)
			{
				try {
					_receiverTransferItemVO.setNewExpiry(BTSLUtil.getDateFromDateString((String) map.get("NEW_EXPIRY_DATE"), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
				} catch (Exception e) {
					_log.errorTrace(methodName, e);
				}
				try {
					_receiverTransferItemVO.setPostBalance(Long.parseLong((String) map.get("INTERFACE_POST_BALANCE")));
				} catch (Exception e) {
					_log.errorTrace(methodName, e);
				}
				try {
					_receiverTransferItemVO.setPreviousBalance(Long.parseLong((String) map.get("INTERFACE_PRE_BALANCE")));
				} catch (Exception e) {
					_log.errorTrace(methodName, e);
				}
				try {
					_receiverTransferItemVO.setPostValidationStatus((String) map.get("POST_BALANCE_ENQ_SUCCESS"));
				} catch (Exception e) {
					_log.errorTrace(methodName, e);
				}
			}
			try {
				if (!BTSLUtil.isNullString((String) map.get("IN_POSTCREDIT_VAL_TIME"))) {
					_requestVO.setPostValidationTimeTaken(Long.parseLong((String) map.get("IN_POSTCREDIT_VAL_TIME")));
				} else {
					_requestVO.setPostValidationTimeTaken(0L);
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			;
			if(_subValRequired) 
				_operatorUtil.updateBonusListAfterTopup(map, _c2sTransferVO);
			try {
				final String newExpDateStr = (String) map.get("NEW_EXPIRY_DATE");
				if (!BTSLUtil.isNullString(newExpDateStr)) {
					_c2sTransferVO.setNewExpiry(BTSLUtil.getDateFromDateString(newExpDateStr, (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				final String newGraceDateStr = (String) map.get("NEW_GRACE_DATE");
				if (!BTSLUtil.isNullString(newGraceDateStr)) {
					_c2sTransferVO.setNewGraceDate(BTSLUtil.getDateFromDateString(newGraceDateStr, (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				final String interfacePostBalanceStr = (String) map.get("INTERFACE_POST_BALANCE");
				if (!BTSLUtil.isNullString(interfacePostBalanceStr)) {
					_c2sTransferVO.setPostBalance(Long.parseLong(interfacePostBalanceStr));
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
			try{_requestVO.setNewExpiryDate(BTSLUtil.formatDateFromOnetoAnother((String)map.get("NEW_EXPIRY_DATE"),(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));}
			catch(Exception e) {
				_log.error(methodName, "Exception:e=" +e);
				_log.errorTrace(methodName, e);
			};
			
			String new_promoBalance = null;
			String new_promoExpiry = null;
			if (BTSLUtil.isNullString(_c2sTransferVO.getNewPromoBalance())) {
				new_promoBalance = (String) map.get("INTERFACE_PROMO_POST_BALANCE");
				if (!BTSLUtil.isNullString(new_promoBalance)) {
					new_promoBalance = PretupsI.PROMO_BALANCE_PREFIX + ":" + new_promoBalance;
				}
			} else {
				new_promoBalance = (String) map.get("INTERFACE_PROMO_POST_BALANCE");
				if (!BTSLUtil.isNullString(new_promoBalance)) {
					new_promoBalance = _c2sTransferVO.getNewPromoBalance() + "|" + PretupsI.PROMO_BALANCE_PREFIX + ":" + new_promoBalance;
				} else {
					new_promoBalance = _c2sTransferVO.getNewPromoBalance();
				}

			}
			_c2sTransferVO.setNewPromoBalance(new_promoBalance);
			try {
				if (BTSLUtil.isNullString(_c2sTransferVO.getNewPromoExpiry())) {

					new_promoExpiry = (String) map.get("NEW_PROMO_EXPIRY_DATE");
					if (!BTSLUtil.isNullString(new_promoExpiry)) {

						new_promoExpiry = PretupsI.PROMO_BALANCE_PREFIX + ":" + new_promoExpiry;
					}
				} else {
					new_promoExpiry = (String) map.get("NEW_PROMO_EXPIRY_DATE");
					if (!BTSLUtil.isNullString(new_promoExpiry)) {

						new_promoExpiry = _c2sTransferVO.getNewPromoExpiry() + "|" + PretupsI.PROMO_BALANCE_PREFIX + ":" + new_promoExpiry;

					} else {
						new_promoExpiry = _c2sTransferVO.getNewPromoExpiry();
					}

				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			_c2sTransferVO.setNewPromoExpiry(new_promoExpiry);
			try {
				final String promoStatus = (String) map.get("PROMO_STATUS");
				if (InterfaceErrorCodesI.ERROR_RESPONSE.equals(promoStatus)) {
					_c2sTransferVO.setPromoStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
				} else {
					_c2sTransferVO.setPromoStatus(promoStatus);
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			_c2sTransferVO.setInterfacePromoStatus((String) map.get("PROMO_INTERFACE_STATUS"));
			try {
				final String cosStatus = (String) map.get("COS_STATUS");
				if (InterfaceErrorCodesI.ERROR_RESPONSE.equals(cosStatus)) {
					_c2sTransferVO.setCosStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
				} else {
					_c2sTransferVO.setCosStatus(cosStatus);
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			_c2sTransferVO.setInterfaceCosStatus((String) map.get("COS_INTERFACE_STATUS"));
			_c2sTransferVO.setNewServiceClssCode((String) map.get("INTERFACE_POST_COS"));
			try {
				_c2sTransferVO.setPostCreditCoreValidity(BTSLUtil.getDateFromDateString((String) map.get("POSTCRE_NEW_EXPIRY_DATE"), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
			} catch (Exception e) {
				loggerValue.setLength(0);
            	loggerValue.append("Exception ");
            	loggerValue.append(e);
				_log.error(methodName, loggerValue );
				_log.errorTrace(methodName, e);
			}
			try {
				_c2sTransferVO.setNewGraceDate(BTSLUtil.getDateFromDateString((String) map.get("NEW_GRACE_DATE"), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
			} catch (Exception e) {
				loggerValue.setLength(0);
            	loggerValue.append("Exception ");
            	loggerValue.append(e);
				_log.error(methodName, loggerValue );
				_log.errorTrace(methodName, e);
			}
		if(!BTSLUtil.isNullString((String) map.get("POSTCRE_INTERFACE_POST_BALANCE")))
        {
			try {
				_c2sTransferVO.setPostCreditCoreBalance(Long.parseLong((String) map.get("POSTCRE_INTERFACE_POST_BALANCE")));
			} catch (Exception e) {
				loggerValue.setLength(0);
            	loggerValue.append("Exception ");
            	loggerValue.append(e);
				_log.error(methodName, loggerValue );
				_log.errorTrace(methodName, e);
			}
		}
		if(!BTSLUtil.isNullString((String) map.get("POSTCRE_INTERFACE_PROMO_POST_BALANCE")))
        {
			try {
				_c2sTransferVO.setPostCreditPromoBalance(Long.parseLong((String) map.get("POSTCRE_INTERFACE_PROMO_POST_BALANCE")));
			} catch (Exception e) {
				loggerValue.setLength(0);
            	loggerValue.append("Exception ");
            	loggerValue.append(e);
				_log.error(methodName, loggerValue );
				_log.errorTrace(methodName, e);
			}
		}
			try {
				_c2sTransferVO.setPostCreditPromoValidity(BTSLUtil.getDateFromDateString((String) map.get("POSTCRE_NEW_PROMO_EXPIRY_DATE"), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
			} catch (Exception e) {
				loggerValue.setLength(0);
            	loggerValue.append("Exception ");
            	loggerValue.append(e);
				_log.error(methodName, loggerValue );
				_log.errorTrace(methodName, e);
			}
			try {
				_c2sTransferVO.setPostValidationStatus((String) map.get("POSTCRE_TRANSACTION_STATUS"));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try{_requestVO.setInCreditURL((String)map.get("IP"));_c2sTransferVO.setInfo7(_requestVO.getInCreditURL());	}catch(Exception e){_log.errorTrace(methodName, e);}
			try{_requestVO.setCreditINRespCode(_receiverVO.getInterfaceResponseCode()); _c2sTransferVO.setInfo8(_requestVO.getCreditINRespCode());}catch(Exception ex){_log.errorTrace(methodName, ex);}
		}
		try {
        _c2sTransferVO.setReceiverSubscriberType((String) map.get("SUBSCIBER_TYPE"));
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
	}

	}
	public void populateServiceInterfaceDetails(Connection p_con, String action) throws BTSLBaseException {
		final String receiverNetworkCode = _receiverVO.getNetworkCode();
		final long receiverPrefixID = _receiverVO.getPrefixID();
		boolean isReceiverFound = false;
		if ((!_receiverInterfaceInfoInDBFound && action.equals(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION)) || action
				.equals(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION)) {
			isReceiverFound = getInterfaceRoutingDetails(p_con, _receiverMSISDN, receiverPrefixID, _receiverVO.getSubscriberType(), receiverNetworkCode, _c2sTransferVO
					.getServiceType(), _type, PretupsI.USER_TYPE_RECEIVER, action);
		} else {
			isReceiverFound = true;
		}
		if (!isReceiverFound) {
			throw new BTSLBaseException("C2SPrepaidController", "populateServiceInterfaceDetails", PretupsErrorCodesI.C2S_ERROR_NOTFOUND_SERVICEINTERFACEMAPPING);
		}

	}

	private void checkTransactionLoad() throws BTSLBaseException {
		final String methodName = "checkTransactionLoad";
		LogFactory.printLog(methodName, "Checking load for transfer ID=" + _transferID, _log);
		int recieverLoadStatus = 0;
		try {
			_c2sTransferVO.setRequestVO(_requestVO);
			_c2sTransferVO.setSenderTransferItemVO(_senderTransferItemVO);
			_c2sTransferVO.setReceiverTransferItemVO(_receiverTransferItemVO);
			recieverLoadStatus = LoadController.checkInterfaceLoad(_c2sTransferVO.getReceiverNetworkCode(), _receiverTransferItemVO.getInterfaceID(), _transferID,_c2sTransferVO, true);
			if (recieverLoadStatus == 0) {
				LoadController.checkTransactionLoad(((ReceiverVO) _c2sTransferVO.getReceiverVO()).getNetworkCode(), _receiverTransferItemVO.getInterfaceID(), PretupsI.C2S_MODULE, _transferID, true, LoadControllerI.USERTYPE_SENDER);
				if (_log.isDebugEnabled()) {
					_log.debug("C2SPrepaidController[checkTransactionLoad]", "_transferID=" + _transferID + " Successfully through load");
				}
			}
			else if (recieverLoadStatus == 1) {
				final String strArr[] = { _receiverMSISDN, String.valueOf(PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount())) };
				throw new BTSLBaseException("C2SPrepaidController", methodName, PretupsErrorCodesI.REQUEST_IN_QUEUE, 0, strArr, null);
			}
			else {
				throw new BTSLBaseException("C2SPrepaidController", methodName, PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
			}
		} catch (BTSLBaseException be) {
			_log.error("C2SPrepaidController[checkTransactionLoad]", "Refusing request getting Exception:" + be.getMessage());
			throw be;
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
			throw new BTSLBaseException("C2SPrepaidController", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
	}

	protected void processValidationRequest() throws BTSLBaseException,SQLException {
		Connection con = null;
		MComConnectionI mcomCon = null;
		StringBuilder loggerValue= new StringBuilder(); 
		final String methodName = "processValidationRequest";
		if (_log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered and performing validations for transfer ID=");
			loggerValue.append(_transferID);
			loggerValue.append( " " );
			loggerValue.append(_c2sTransferVO.getModule());
			loggerValue.append( " " );
			loggerValue.append(_c2sTransferVO.getReceiverNetworkCode());
			loggerValue.append( " " );
			loggerValue.append(_type);
			_log.debug(methodName, loggerValue);
		}
		boolean dbEntryNotAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED);
		try {
			final NetworkInterfaceModuleVO networkInterfaceModuleVOS = (NetworkInterfaceModuleVO) NetworkInterfaceModuleCache.getObject(_c2sTransferVO.getModule(), _c2sTransferVO.getReceiverNetworkCode(), _type);
			_intModCommunicationTypeS = networkInterfaceModuleVOS.getCommunicationType();
			_intModIPS = networkInterfaceModuleVOS.getIP();
			_intModPortS = networkInterfaceModuleVOS.getPort();
			_intModClassNameS = networkInterfaceModuleVOS.getClassName();
			if(_requestVO.getRequestMap()!=null && _requestVO.getRequestMap().get("SERVICECLASS") != null){
				_subValRequired=false;
				_receiverTransferItemVO.setServiceClassCode((String)_requestVO.getRequestMap().get("SERVICECLASS")); 
				_receiverTransferItemVO.setServiceClass(_receiverTransferItemVO.getServiceClassCode());
			}
			String requestStr = getReceiverValidateStr();
			LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_VAL);
			final CommonClient commonClient = new CommonClient();
			TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "");
			final String receiverValResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);
			TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL, receiverValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");
			if (_log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Got the validation response from IN Module receiverValResponse=");
				loggerValue.append(receiverValResponse);
				_log.debug(methodName, _transferID, loggerValue );
			}
			try {
				updateForReceiverValidateResponse(receiverValResponse);
			} catch (BTSLBaseException be) {
				LoadController.decreaseResponseCounters(_transferID, _receiverVO.getTransactionStatus(), LoadControllerI.SENDER_VAL_RESPONSE);
				if (_log.isDebugEnabled()) {
					loggerValue.setLength(0);
					loggerValue.append("inside catch of BTSL Base Exception: ");
					loggerValue.append(be.getMessage() );
					loggerValue.append(" _receiverInterfaceInfoInDBFound: ");
					loggerValue.append(_receiverInterfaceInfoInDBFound);
					_log.debug(methodName, loggerValue);
				}
				if (_receiverInterfaceInfoInDBFound && _receiverTransferItemVO.getValidationStatus().equals(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND)) {
					PretupsBL.deleteSubscriberInterfaceRouting(_receiverMSISDN, _type);
				}
				PretupsBL.validateRecieverLimits(con, _c2sTransferVO, PretupsI.TRANS_STAGE_AFTER_INVAL, PretupsI.C2S_MODULE);
				throw be;
			} catch (Exception e) {
				LoadController.decreaseResponseCounters(_transferID, _receiverVO.getTransactionStatus(), LoadControllerI.SENDER_VAL_RESPONSE);
				PretupsBL.validateRecieverLimits(con, _c2sTransferVO, PretupsI.TRANS_STAGE_AFTER_INVAL, PretupsI.C2S_MODULE);
				throw new BTSLBaseException(this, methodName, "");
			}
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			LoadController.decreaseResponseCounters(_transferID, _receiverVO.getTransactionStatus(), LoadControllerI.SENDER_VAL_RESPONSE);
			InterfaceVO interfaceVO = (InterfaceVO) NetworkInterfaceModuleCache.getObject(_receiverTransferItemVO.getInterfaceID());
			if (_log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("System.currentTimeMillis(): ");
				loggerValue.append(System.currentTimeMillis() );
				loggerValue.append("_c2sTransferVO.getRequestStartTime(): ");
				loggerValue.append(_c2sTransferVO.getRequestStartTime() );
				loggerValue.append(" interfaceVO.getValExpiryTime(): ");
				loggerValue.append(interfaceVO.getValExpiryTime());
				_log.debug(methodName, loggerValue);
			}
			if ((System.currentTimeMillis() - _c2sTransferVO.getRequestStartTime()) > interfaceVO.getValExpiryTime()) {
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "C2SPrepaidController[processValidationRequest]",
						_transferID, _senderMSISDN, _senderNetworkCode, "Exception: System is taking more time till validation");
				throw new BTSLBaseException("C2SPrepaidController", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_TKING_TIME_TILL_VAL);
			}
			PretupsBL.validateServiceClassChecks(con, _receiverTransferItemVO, _c2sTransferVO, PretupsI.C2S_MODULE, _requestVO.getServiceType());
			_receiverVO.setServiceClassCode(_receiverTransferItemVO.getServiceClass());
			PretupsBL.validateTransferRule(con, _c2sTransferVO, PretupsI.C2S_MODULE);
			PretupsBL.validateRecieverLimits(con, _c2sTransferVO, PretupsI.TRANS_STAGE_AFTER_INVAL, PretupsI.C2S_MODULE);
			CardGroupBL.calculateCardGroupDetails(con, _c2sTransferVO, PretupsI.C2S_MODULE, true);

			TransactionLog
			.log(
					_transferID,
					_requestIDStr,
					_receiverMSISDN,
					_receiverVO.getNetworkCode(),
					PretupsI.TXN_LOG_REQTYPE_INT,
					PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
					"After Card Group Set Id=" + _c2sTransferVO.getCardGroupSetID() + " Code" + _c2sTransferVO.getCardGroupCode() + " Card ID=" + _c2sTransferVO
					.getCardGroupID() + " Access fee=" + _c2sTransferVO.getReceiverAccessFee() + " Tax1 =" + _c2sTransferVO.getReceiverTax1Value() + " Tax2=" + _c2sTransferVO
					.getReceiverTax1Value() + " Bonus=" + _c2sTransferVO.getReceiverBonusValue() + " Val Type=" + _c2sTransferVO.getReceiverValPeriodType() + " Validity=" + _c2sTransferVO
					.getReceiverValidity() + " Talk Time=" + _c2sTransferVO.getReceiverTransferValue(), PretupsI.TXN_LOG_STATUS_SUCCESS, "");

			String C2S_MODIFICATION_ALLOWED = BTSLUtil.NullToString(Constants.getProperty("C2S_MODIFICATION_ALLOWED"));
			if(C2S_MODIFICATION_ALLOWED.equals("") || C2S_MODIFICATION_ALLOWED.equals("N") ) {
			_userBalancesVO = ChannelUserBL.debitUserBalanceForProduct(con, _transferID, _c2sTransferVO);
			if(penaltyApplicable){
				_c2sTransferVO.setIsDebitPenalty(true);
				long previousBalance=((C2STransferItemVO)(_c2sTransferVO.getTransferItemList().get(0))).getPreviousBalance();
				((C2STransferItemVO)(_c2sTransferVO.getTransferItemList().get(0))).setTransferValue(_c2sTransferVO.getRoamPenalty());
				_userBalancesVO=ChannelUserBL.debitUserBalanceForProduct(con,_transferID,_c2sTransferVO);
				_penaltyVOCredit.setPreviousBalance(_userBalancesVO.getPreviousBalance());
				_penaltyVOCredit.setPostBalance(_userBalancesVO.getBalance());
				((C2STransferItemVO)(_c2sTransferVO.getTransferItemList().get(0))).setTransferValue(_c2sTransferVO.getTransferValue());
				((C2STransferItemVO)(_c2sTransferVO.getTransferItemList().get(0))).setPreviousBalance(previousBalance);
			}
			if(penaltyApplicableOwner){
				_c2sTransferVO.setIsDebitPenalty(true);
				userBalanceOwnerVO=ChannelUserBL.debitUserBalanceForProductRoamOwner(con, _transferID, c2STransferOwnerVO);
				_penaltyVOCreditOwner.setPreviousBalance(userBalanceOwnerVO.getPreviousBalance());
				_penaltyVOCreditOwner.setPostBalance(userBalanceOwnerVO.getBalance());
				}
				
			}else {
			_userBalancesVO = ChannelUserBL.debitUserBalanceForProductModified(con, _transferID, _c2sTransferVO);
			_BalanceDetailAdded = true;
			 con.commit();
			if(_userBalancesVO.getDailyBalanceUpdateCountList() != null && _userBalancesVO.getDailyBalanceUpdateCountList().size() > 0 )
			{
				UserBalancesDAO _userBalancesDAO = new UserBalancesDAO();
				final int dailyBalanceUpdateCount = _userBalancesDAO.updateUserDailyBalances(con, _c2sTransferVO.getTransferDate(), _userBalancesVO,_userBalancesVO.getDailyBalanceUpdateCountList());
				 if(dailyBalanceUpdateCount > 0)
				 {
					 con.commit();
				 }
				_log.debug(methodName, "No of Rows updated in table : user_daily_balance is = " + dailyBalanceUpdateCount);
			}
			 
			if(penaltyApplicable){
				_c2sTransferVO.setIsDebitPenalty(true);
				long previousBalance=((C2STransferItemVO)(_c2sTransferVO.getTransferItemList().get(0))).getPreviousBalance();
				((C2STransferItemVO)(_c2sTransferVO.getTransferItemList().get(0))).setTransferValue(_c2sTransferVO.getRoamPenalty());
				_userBalancesVO=ChannelUserBL.debitUserBalanceForProductModified(con,_transferID,_c2sTransferVO);
				_BalanceDetailAddedPenalty = true;
				 con.commit();
				if(_userBalancesVO.getDailyBalanceUpdateCountList() != null && _userBalancesVO.getDailyBalanceUpdateCountList().size() > 0 )
				{
					UserBalancesDAO _userBalancesDAO = new UserBalancesDAO();
					final int dailyBalanceUpdateCount = _userBalancesDAO.updateUserDailyBalances(con, _c2sTransferVO.getTransferDate(), _userBalancesVO,_userBalancesVO.getDailyBalanceUpdateCountList());
					 if(dailyBalanceUpdateCount > 0)
					 {
						 con.commit();
					 }
					_log.debug(methodName, "No of Rows updated in table : user_daily_balance is = " + dailyBalanceUpdateCount);
				}
				_penaltyVOCredit.setPreviousBalance(_userBalancesVO.getPreviousBalance());
				_penaltyVOCredit.setPostBalance(_userBalancesVO.getBalance());
				((C2STransferItemVO)(_c2sTransferVO.getTransferItemList().get(0))).setTransferValue(_c2sTransferVO.getTransferValue());
				((C2STransferItemVO)(_c2sTransferVO.getTransferItemList().get(0))).setPreviousBalance(previousBalance);
				
			}
			if(penaltyApplicableOwner){
				_c2sTransferVO.setIsDebitPenalty(true);
				userBalanceOwnerVO=ChannelUserBL.debitUserBalanceForProductRoamOwnerModified(con, _transferID, c2STransferOwnerVO);
				_BalanceDetailAddedPenaltyRoam = true;
				 con.commit();
				if(_userBalancesVO.getDailyBalanceUpdateCountList() != null && _userBalancesVO.getDailyBalanceUpdateCountList().size() > 0 )
				{
						UserBalancesDAO _userBalancesDAO = new UserBalancesDAO();
						final int dailyBalanceUpdateCount = _userBalancesDAO.updateUserDailyBalances(con, _c2sTransferVO.getTransferDate(), _userBalancesVO,_userBalancesVO.getDailyBalanceUpdateCountList());
						 if(dailyBalanceUpdateCount > 0)
						 {
							 con.commit();
						 }
						_log.debug(methodName, "No of Rows updated in table : user_daily_balance is = " + dailyBalanceUpdateCount);
				}
				_penaltyVOCreditOwner.setPreviousBalance(userBalanceOwnerVO.getPreviousBalance());
				_penaltyVOCreditOwner.setPostBalance(userBalanceOwnerVO.getBalance());
			}
		}
			/*if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,_c2sTransferVO.getNetworkCode()))
			{
				ChannelTransferBL.increaseUserOTFCounts(con, _c2sTransferVO, _channelUserVO);
			}*/
			ChannelTransferBL.increaseC2STransferOutCounts(con, _c2sTransferVO, true);
			
			_c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
			_senderTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
			populateServiceInterfaceDetails(con, PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);
			if (!dbEntryNotAllowed) {
				ChannelTransferBL.addC2STransferDetails(con, _c2sTransferVO);
			}
			_transferDetailAdded = true;
			if(!_BalanceDetailAdded )
				_BalanceDetailAdded = true;
			if(penaltyApplicable) {
				_BalanceDetailAddedPenalty = true;
			}
			if(penaltyApplicableOwner) {
				_BalanceDetailAddedPenaltyRoam = true;
			}
			mcomCon.finalCommit();
			TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Marked Under process", PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "");

			if (_transferDetailAdded) {
				BalanceLogger.log(_userBalancesVO);
			}
			if (_c2sTransferVO.isUnderProcessMsgReq() && _receiverMessageSendReq && !BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(), _notAllowedRecSendMessGatw) && !"ALL"
					.equals(_notAllowedRecSendMessGatw)) {
				(new PushMessage(_receiverMSISDN, getReceiverUnderProcessMessage(), _transferID, _c2sTransferVO.getRequestGatewayCode(), _receiverLocale, _senderNetworkCode, null, _serviceType)).push();
			}
			if (_log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("System.currentTimeMillis(): ");
				loggerValue.append(System.currentTimeMillis() );
				loggerValue.append("_c2sTransferVO.getRequestStartTime(): ");
				loggerValue.append(_c2sTransferVO.getRequestStartTime() );
				loggerValue.append(" interfaceVO.getTopUpExpiryTime(): ");
				loggerValue.append(interfaceVO.getTopUpExpiryTime());
				_log.debug(methodName, loggerValue);
			}
			if(_c2sTransferVO.isUnderProcessMsgReq()&& _ussdReceiverMessageSendReq && !BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(),_notAllowedRecSendMessGatw)&&!"ALL".equals(_notAllowedRecSendMessGatw))
				(new USSDPushMessage(_receiverMSISDN,getReceiverUnderProcessMessage(),_transferID,_c2sTransferVO.getRequestGatewayCode(),_receiverLocale)).push();
			
			if ((System.currentTimeMillis() - _c2sTransferVO.getRequestStartTime()) > interfaceVO.getTopUpExpiryTime()) {
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "C2SPrepaidController[run]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception: System is taking more time till topup");
				throw new BTSLBaseException("C2SPrepaidController", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_TKING_TIME_TILL_TOPUP);
			}
			interfaceVO = null;
			if (!_requestVO.isToBeProcessedFromQueue()) {
				if (_c2sTransferVO.getMsgGatewayFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON) || _processedFromQueue) {
					final Thread _controllerThread = new Thread(this);
					_controllerThread.start();
					_oneLog = false;
				}
			}
		} catch (BTSLBaseException be) {
			try {
				if(mcomCon!= null)
				mcomCon.partialRollback();
				} catch (SQLException sqle)
			{ 
					_log.errorTrace(methodName, sqle);
					}
			if (_recValidationFailMessageRequired) {
				if (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
					_c2sTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.C2S_RECEIVER_FAIL), new String[] { String.valueOf(_transferID), PretupsBL
							.getDisplayAmount(_c2sTransferVO.getRequestedAmount()) }));
				}
			}
			_c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			_receiverTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
				if (be.isKey()) {
					_c2sTransferVO.setErrorCode(be.getMessageKey());
				} else {
					_c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
				}
			}
			_log.error("C2SPrepaidController[processValidationRequest]", "Getting BTSL Base Exception:" + be.getMessage());
			if (_transferDetailAdded || _BalanceDetailAdded) {
				if (mcomCon == null) {
					mcomCon = new MComConnection();
					con = mcomCon.getConnection();
				}
				_userBalancesVO = null;
				updateSenderForFailedTransaction(con);
				if (!dbEntryNotAllowed) {
					addEntryInTransfers(con);
				}
				mcomCon.finalCommit();
				if (_creditBackEntryDone) {
					BalanceLogger.log(_userBalancesVO);
				}
			}
			if(penaltyApplicableOwner &&c2STransferOwnerVO.isPenaltyInsufficientBalanceOwner()){
				BTSLMessages message= new BTSLMessages(PretupsErrorCodesI.CHNL_ERROR_OWNR_BAL_LESS_ROAM );
				PushMessage pushPenaltymessage = (new PushMessage(_senderMSISDN, message, _transferID, _c2sTransferVO.getRequestGatewayCode(), _senderLocale, _senderNetworkCode, null, _serviceType));
				pushPenaltymessage.push();
			}
			throw be;
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
			try {
				mcomCon.partialRollback();
				} 
			catch (SQLException sqle) 
			{ 
				_log.errorTrace(methodName, sqle);
				}
			_c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			if (_recValidationFailMessageRequired) {
				if (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
					_c2sTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.C2S_RECEIVER_FAIL), new String[] { String.valueOf(_transferID), PretupsBL
							.getDisplayAmount(_c2sTransferVO.getRequestedAmount()) }));
				}
			}
			if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
				_c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			}
			if (_transferDetailAdded || _BalanceDetailAdded) {
				if (mcomCon == null) {
					mcomCon = new MComConnection();
					con = mcomCon.getConnection();
				}
				_userBalancesVO = null;
				updateSenderForFailedTransaction(con);
				if (!dbEntryNotAllowed) {
					addEntryInTransfers(con);
				}
				mcomCon.finalCommit();
				if (_creditBackEntryDone) {
					BalanceLogger.log(_userBalancesVO);
				}
			}
			throw new BTSLBaseException("C2SPrepaidController", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		} finally {
			if(mcomCon != null )
				mcomCon.close("C2SPrepaidController#processValidationRequest");
			mcomCon = null;
			con = null;
		}
	}

	public String getReceiverValidateStr() {
		StringBuffer strBuff ;
		strBuff = new StringBuffer(getReceiverCommonString());
		strBuff.append("&INTERFACE_ACTION=" + PretupsI.INTERFACE_VALIDATE_ACTION);
		if(!_subValRequired)
			strBuff.append("&SERVICECLASS=TRUE");
		Boolean isCellIDRequiredFromIN = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_CELLID_REQUIRED_FROM_IN);
		if (isCellIDRequiredFromIN ) {

			strBuff.append(PretupsI.TAG_PARAMATER_SEPARATOR);
			strBuff.append(PretupsI.TAG_GATEWAY_TYPE);
			strBuff.append(PretupsI.TAG_VALUE_SEPARATOR);
			strBuff.append(_requestVO.getMessageGatewayVO().getGatewayType());
			
			strBuff.append(PretupsI.TAG_PARAMATER_SEPARATOR);
			strBuff.append(PretupsI.TAG_GEOGRAPHY_CODE);
			strBuff.append(PretupsI.TAG_VALUE_SEPARATOR);
			strBuff.append(_channelUserVO.getGeographicalCode());
		}
		return strBuff.toString();
	}

	public String getReceiverCreditStr() {
		final String methodName = "getReceiverCreditStr";
		StringBuilder loggerValue= new StringBuilder(); 
		Long previous_balance = 0L;
		Date _previousPromoExpiry = null;
		StringBuffer strBuff ;
		strBuff = new StringBuffer(getReceiverCommonString());
		strBuff.append("&INTERFACE_ACTION=" + PretupsI.INTERFACE_CREDIT_ACTION);
		strBuff.append("&INTERFACE_AMOUNT=" + _c2sTransferVO.getReceiverTransferValue());
		strBuff.append("&GRACE_DAYS=" + _receiverTransferItemVO.getGraceDaysStr());
		strBuff.append("&CARD_GROUP=" + _c2sTransferVO.getCardGroupCode());
		strBuff.append("&MIN_CARD_GROUP_AMT=" + _c2sTransferVO.getMinCardGroupAmount());
		strBuff.append("&SENDER_MSISDN=" + _senderMSISDN);
		strBuff.append("&SENDER_ID=" + _channelUserVO.getUserID());
		strBuff.append("&SENDER_EXTERNAL_CODE=" + _channelUserVO.getExternalCode());
		strBuff.append("&PRODUCT_CODE=" + _c2sTransferVO.getProductCode());
		strBuff.append("&VALIDITY_DAYS=" + _c2sTransferVO.getReceiverValidity());
		strBuff.append("&BONUS_VALIDITY_DAYS=" + _c2sTransferVO.getReceiverBonusValidity());
		strBuff.append("&BONUS_AMOUNT=" + _c2sTransferVO.getReceiverBonusValue());
		try {
			strBuff.append("&OLD_EXPIRY_DATE=" + BTSLUtil.getDateStringFromDate(_receiverTransferItemVO.getPreviousExpiry(), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append("Exception " );
			loggerValue.append(e);
			_log.error(methodName, loggerValue);
			_log.errorTrace(methodName, e);
		}
		try {
			strBuff.append("&OLD_GRACE_DATE=" + BTSLUtil.getDateStringFromDate(_receiverTransferItemVO.getPreviousGraceDate(), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append("Exception ");
			loggerValue.append(e);
			_log.error(methodName,  loggerValue);
			_log.errorTrace(methodName, e);
		}
		strBuff.append("&INTERFACE_PREV_BALANCE=" + _receiverTransferItemVO.getPreviousBalance());
		strBuff.append("&REQUESTED_AMOUNT=" + _c2sTransferVO.getRequestedAmount());
		strBuff.append("&SERVICE_CLASS=" + _receiverTransferItemVO.getServiceClassCode());
		strBuff.append("&SOURCE_TYPE=" + _c2sTransferVO.getSourceType());
		strBuff.append("&CREDIT_BONUS_VAL=" + _c2sTransferVO.getReceiverCreditBonusValidity());
		strBuff.append("&COMBINED_RECHARGE=" + _c2sTransferVO.getBoth());
		strBuff.append("&IMPLICIT_RECHARGE=" + _c2sTransferVO.getOnline());
		strBuff.append("&IN_ACCOUNT_ID=" + _receiverTransferItemVO.getInAccountId());
		strBuff.append("&CAL_OLD_EXPIRY_DATE=" + _receiverTransferItemVO.getOldExporyInMillis());// @nu
		if (_receiverTransferItemVO.isNumberBackAllowed()) {
			final String numbck_diff_to_in = _c2sTransferVO.getServiceType() + PreferenceI.NUMBCK_DIFF_REQ_TO_IN;
			final Boolean NBR_BK_SEP_REQ = (Boolean) PreferenceCache.getControlPreference(numbck_diff_to_in, _c2sTransferVO.getNetworkCode(), _receiverTransferItemVO.getInterfaceID());
			strBuff.append("&NBR_BK_DIFF_REQ=" + NBR_BK_SEP_REQ);
		}
		boolean enqPostbalIn = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.ENQ_POSTBAL_IN);
		boolean enqPostbalAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.ENQ_POSTBAL_ALLOW);
		if (enqPostbalIn) {
			strBuff.append("&ENQ_POSTBAL_IN=" + PretupsI.YES);
		} else {
			strBuff.append("&ENQ_POSTBAL_IN=" + PretupsI.NO);
		}
		if (enqPostbalAllow) {
			strBuff.append("&ENQ_POSTBAL_ALLOW=" + PretupsI.YES);
		} else {
			strBuff.append("&ENQ_POSTBAL_ALLOW=" + PretupsI.NO);
		}
		strBuff.append("&SELECTOR_BUNDLE_ID=" + _receiverBundleID);
		strBuff.append("&SELECTOR_BUNDLE_TYPE=" + _c2sTransferVO.getSelectorBundleType());
		strBuff.append("&BONUS_BUNDLE_IDS=" + _c2sTransferVO.getBonusBundleIdS());
		strBuff.append("&BONUS_BUNDLE_TYPES=" + _c2sTransferVO.getBonusBundleTypes());
		strBuff.append("&BONUS_BUNDLE_VALUES=" + _c2sTransferVO.getBonusBundleValues());
		strBuff.append("&BONUS_BUNDLE_VALIDITIES=" + _c2sTransferVO.getBonusBundleValidities());
		strBuff.append("&IN_RESP_BUNDLE_CODES=" + _receiverTransferItemVO.getBundleTypes());
		strBuff.append("&BONUS_BUNDLE_NAMES=" + _c2sTransferVO.getBonusBundleNames());
		strBuff.append("&BONUS_BUNDLE_RATES=" + _c2sTransferVO.getBonusBundleRate());
		strBuff.append("&BONUS_BUNDLE_CODES=" + _c2sTransferVO.getBonusBundleCode());
		strBuff.append("&IN_RESP_BUNDLE_PREV_BALS=" + _receiverTransferItemVO.getPrevBundleBals());
		strBuff.append("&IN_RESP_BUNDLE_PREV_VALIDITY=" + _receiverTransferItemVO.getPrevBundleExpiries());
		strBuff.append("&PROMOTION_AMOUNT=" + _c2sTransferVO.getInPromo());
		strBuff.append("&COS_FLAG=" + _c2sTransferVO.getCosRequired());
		strBuff.append("&NEW_COS_SERVICE_CLASS=" + _c2sTransferVO.getNewCos());
		try {
			final String prevPromoBalStr = _c2sTransferVO.getPreviousPromoBalance();
			if (!BTSLUtil.isNullString(prevPromoBalStr)) {
				previous_balance = (Long.parseLong(prevPromoBalStr));
			}
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		try {
			final String prevPromoExpDateStr = _c2sTransferVO.getPreviousPromoExpiry();
			if (!BTSLUtil.isNullString(prevPromoExpDateStr)) {
				_previousPromoExpiry = (BTSLUtil.getDateFromDateString(prevPromoExpDateStr, (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
			}
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		strBuff.append("&INTERFACE_PROMO_PREV_BALANCE=" + previous_balance);
		strBuff.append("&CAL_OLD_EXPIRY_DATE=" + _c2sTransferVO.getPreviousExpiryInCal());
		strBuff.append("&PROMO_CAL_OLD_EXPIRY_DATE=" + _c2sTransferVO.getPreviousPromoExpiryInCal());
		strBuff.append("&PROMO_OLD_EXPIRY_DATE=" + _previousPromoExpiry);
		strBuff.append("&RC_COMMENT=" + _c2sTransferVO.getRechargeComment());
		strBuff.append("&REQUEST_GATEWAY_CODE="+_c2sTransferVO.getRequestGatewayCode());
		return strBuff.toString();

	}

	private String getReceiverCommonString() {
		StringBuffer strBuff ;
		strBuff = new StringBuffer("MSISDN=" + _receiverMSISDN);
		strBuff.append("&TRANSACTION_ID=" + _transferID);
		strBuff.append("&NETWORK_CODE=" + _receiverVO.getNetworkCode());
		strBuff.append("&INTERFACE_ID=" + _receiverTransferItemVO.getInterfaceID());
		strBuff.append("&INTERFACE_HANDLER=" + _receiverTransferItemVO.getInterfaceHandlerClass());
		strBuff.append("&INT_MOD_COMM_TYPE=" + _intModCommunicationTypeS);
		strBuff.append("&INT_MOD_IP=" + _intModIPS);
		strBuff.append("&INT_MOD_PORT=" + _intModPortS);
		strBuff.append("&INT_MOD_CLASSNAME=" + _intModClassNameS);
		strBuff.append("&MODULE=" + PretupsI.C2S_MODULE);
		strBuff.append("&CARD_GROUP_SELECTOR=" + _requestVO.getReqSelector());
		strBuff.append("&USER_TYPE=R");
		strBuff.append("&REQ_SERVICE=" + _serviceType);
		strBuff.append("&INT_ST_TYPE=" + _c2sTransferVO.getReceiverInterfaceStatusType());
		strBuff.append("&SELECTOR_BUNDLE_ID=" + _receiverBundleID);
		strBuff.append("&DOMAIN_CODE=" + _channelUserVO.getDomainID());
		strBuff.append("&SENDER_MSISDN=" + _senderMSISDN);
		strBuff.append("&CATEGORY_CODE=" + _channelUserVO.getCategoryCode());
		strBuff.append("&REQUEST_GATEWAY_CODE="+_c2sTransferVO.getRequestGatewayCode());
		return strBuff.toString();
	}

	private String getReceiverSuccessMessage() {
		final String methodName = "getReceiverSuccessMessage";
		String[] messageArgArray ;
		String key = null;
		boolean isSeparateBonusRequired = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_SEPARATE_BONUS_REQUIRED);
		boolean notificationServiceClassWise = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.NOTIFICATION_SERVICECLASSWISE_REC_C2S);
		if (!("N".equals(_c2sTransferVO.getBoth()) && "N".equals(_c2sTransferVO.getOnline()))) {
			messageArgArray = new String[] { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverTransferValue()), String.valueOf(_receiverTransferItemVO
					.getValidity()), _senderMSISDN, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(_c2sTransferVO
							.getReceiverAccessFee()), _c2sTransferVO.getSubService(), _channelUserVO.getUserName(), String.valueOf(BTSLUtil.parseDoubleToLong(_c2sTransferVO.getReceiverBonus1())), String
					.valueOf(BTSLUtil.parseDoubleToLong(_c2sTransferVO.getReceiverBonus2())), PretupsBL.getDisplayAmount(_c2sTransferVO.getBonusTalkTimeValue()), PretupsBL
					.getDisplayAmount(_c2sTransferVO.getCalminusBonusvalue()), String.valueOf(_c2sTransferVO.getReceiverBonus1Validity()), String.valueOf(_c2sTransferVO
							.getReceiverBonus2Validity()), String.valueOf(_c2sTransferVO.getReceiverCreditBonusValidity()), _receiverTransferItemVO.getBonus1Name(), _receiverTransferItemVO
					.getBonus2Name(), _receiverTransferItemVO.getSelectorName(), _requestVO.getPosUserMSISDN(), PretupsBL.getDisplayAmount(_receiverTransferItemVO
							.getPostBalance()),_c2sTransferVO.getProductName() };
			key = PretupsErrorCodesI.IMPLICIT_MSG;
		} else {
			if (_c2sTransferVO.getReceiverTransferItemVO().isNumberBackAllowed()) {
				if (isSeparateBonusRequired) {
					_c2sTransferVO.setCalminusBonusvalue(_c2sTransferVO.getReceiverTransferValue() - _c2sTransferVO.getBonusTalkTimeValue());
				}
				messageArgArray = new String[] { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverTransferValue()), String.valueOf(_receiverTransferItemVO
						.getValidity()), _senderPushMessageMsisdn, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(_c2sTransferVO
								.getReceiverAccessFee()), _c2sTransferVO.getSubService(), _channelUserVO.getUserName(), PretupsBL.getDisplayAmount(_c2sTransferVO.getBonusTalkTimeValue()), PretupsBL
						.getDisplayAmount(_c2sTransferVO.getCalminusBonusvalue()), String.valueOf(_c2sTransferVO.getReceiverCreditBonusValidity()), _receiverTransferItemVO
						.getSelectorName(), _requestVO.getPosUserMSISDN(),_c2sTransferVO.getProductName() };
				if (!_receiverTransferItemVO.getPostValidationStatus().equals(InterfaceErrorCodesI.SUCCESS)) {
					if ((_c2sTransferVO.getBonusTalkTimeValue() == 0)) {
						key = PretupsErrorCodesI.C2S_RECEIVER_SUCCESS_WITHOUT_POSTBAL;
					} else {
						key = PretupsErrorCodesI.C2S_RECEIVER_SUCCESS_WITHOUT_POSTBAL_WITH_BONUS;
					}
				} else {
					if ((_c2sTransferVO.getBonusTalkTimeValue() == 0)) {
						key = PretupsErrorCodesI.C2S_RECEIVER_SUCCESS;// return
					} else {
						key = PretupsErrorCodesI.C2S_RECEIVER_SUCCESS_WITH_BONUS;
					}
				}
			}
			if (!PretupsI.NO.equals(_receiverPostBalanceAvailable)) {
				String dateStrGrace = null;
				String dateStrValidity = null;
				try {
					dateStrGrace = (_receiverTransferItemVO.getNewGraceDate() == null) ? "0" : BTSLUtil.getDateStringFromDate(_receiverTransferItemVO.getNewGraceDate());
				} catch (Exception e) {
					_log.errorTrace(methodName, e);
					dateStrGrace = String.valueOf(_receiverTransferItemVO.getNewGraceDate());
				}
				try {
					dateStrValidity = (_receiverTransferItemVO.getNewExpiry() == null) ? "0" : BTSLUtil.getDateStringFromDate(_receiverTransferItemVO.getNewExpiry());
				} catch (Exception e) {
					_log.errorTrace(methodName, e);
					dateStrValidity = String.valueOf(_receiverTransferItemVO.getNewExpiry());
				}
				if (isSeparateBonusRequired) {
					_c2sTransferVO.setCalminusBonusvalue(_c2sTransferVO.getReceiverTransferValue() - _c2sTransferVO.getBonusTalkTimeValue());
				}
				messageArgArray = new String[] { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverTransferValue()), String.valueOf(_receiverTransferItemVO
						.getValidity()), PretupsBL.getDisplayAmount(_receiverTransferItemVO.getPostBalance()), _senderPushMessageMsisdn, dateStrGrace, dateStrValidity, PretupsBL
						.getDisplayAmount(_c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverAccessFee()), _c2sTransferVO.getSubService(), _channelUserVO
						.getUserName(), String.valueOf( BTSLUtil.parseDoubleToLong(_c2sTransferVO.getReceiverBonus1()) ), String.valueOf(BTSLUtil.parseDoubleToLong(_c2sTransferVO.getReceiverBonus2())), PretupsBL
						.getDisplayAmount(_c2sTransferVO.getBonusTalkTimeValue()), PretupsBL.getDisplayAmount(_c2sTransferVO.getCalminusBonusvalue()), String
						.valueOf(_c2sTransferVO.getReceiverBonus1Validity()), String.valueOf(_c2sTransferVO.getReceiverBonus2Validity()), String.valueOf(_c2sTransferVO
								.getReceiverCreditBonusValidity()), _receiverTransferItemVO.getBonus1Name(), _receiverTransferItemVO.getBonus2Name(), _receiverTransferItemVO
						.getSelectorName(), _requestVO.getPosUserMSISDN(), _c2sTransferVO.getBonusSummaryMessageSting(),_c2sTransferVO.getProductName() };
				if (_c2sTransferVO.getBonusTalkTimeValue() == 0) {
					key = PretupsErrorCodesI.C2S_RECEIVER_SUCCESS;// return
				} else {
					key = PretupsErrorCodesI.C2S_RECEIVER_SUCCESS_WITH_BONUS;
				}
			} else {
				if (isSeparateBonusRequired) {
					_c2sTransferVO.setCalminusBonusvalue(_c2sTransferVO.getReceiverTransferValue() - _c2sTransferVO.getBonusTalkTimeValue());
				}
				messageArgArray = new String[] { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverTransferValue()), _senderPushMessageMsisdn, PretupsBL
						.getDisplayAmount(_c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverAccessFee()), _c2sTransferVO.getSubService(), _channelUserVO
						.getUserName(), PretupsBL.getDisplayAmount(_c2sTransferVO.getBonusTalkTimeValue()), PretupsBL.getDisplayAmount(_c2sTransferVO.getCalminusBonusvalue()), String
						.valueOf(_c2sTransferVO.getReceiverCreditBonusValidity()), _receiverTransferItemVO.getSelectorName(), _requestVO.getPosUserMSISDN(), _c2sTransferVO
						.getBonusSummaryMessageSting(),_c2sTransferVO.getProductName() };
				if (_c2sTransferVO.getBonusTalkTimeValue() == 0) {
					key = PretupsErrorCodesI.C2S_RECEIVER_SUCCESS_WITHOUT_POSTBAL;// return
				} else {
					key = PretupsErrorCodesI.C2S_RECEIVER_SUCCESS_WITHOUT_POSTBAL_WITH_BONUS;
				}
			}
		}
		if (!BTSLUtil.isNullString(_receiverTransferItemVO.getChangedBundleCodes()) && _receiverTransferItemVO.getChangedBundleCodes().length() > 0) {
			String dateStrGrace = null;
			String dateStrValidity = null;
			try {
				dateStrGrace = (_receiverTransferItemVO.getNewGraceDate() == null) ? "0" : BTSLUtil.getDateStringFromDate(_receiverTransferItemVO.getNewGraceDate());
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
				dateStrGrace = String.valueOf(_receiverTransferItemVO.getNewGraceDate());
			}
			try {
				dateStrValidity = (_receiverTransferItemVO.getNewExpiry() == null) ? "0" : BTSLUtil.getDateStringFromDate(_receiverTransferItemVO.getNewExpiry());
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
				dateStrValidity = String.valueOf(_receiverTransferItemVO.getNewExpiry());
			}
			messageArgArray = new String[] { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverTransferValue()), String.valueOf(_receiverTransferItemVO
					.getValidity()), PretupsBL.getDisplayAmount(_receiverTransferItemVO.getPostBalance()), _senderPushMessageMsisdn, dateStrGrace, dateStrValidity, PretupsBL
					.getDisplayAmount(_c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverAccessFee()), _c2sTransferVO.getSubService(), _channelUserVO
					.getUserName(), PretupsBL.getDisplayAmount(_c2sTransferVO.getBonusTalkTimeValue()), PretupsBL.getDisplayAmount(_c2sTransferVO.getCalminusBonusvalue()), String
					.valueOf(_c2sTransferVO.getReceiverCreditBonusValidity()), _receiverTransferItemVO.getSelectorName(), _receiverTransferItemVO.getChangedBundleCodes() };
			key = PretupsErrorCodesI.C2S_RECEIVER_SUCCESS_ALL_BALANCES;
		}
		if (notificationServiceClassWise) {
			String message = null;
			try {
				message = BTSLUtil.getMessage(_receiverLocale, key + "_" + _receiverTransferItemVO.getServiceClass(), messageArgArray, _requestVO.getRequestGatewayType());
				if (!BTSLUtil.isNullString(message)) {
					return message;
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
		}

		return BTSLUtil.getMessage(_receiverLocale, key, messageArgArray, _requestVO.getRequestGatewayType());
	}

	private String getSenderSuccessMessage() {
		String[] messageArgArray ;
		if(!BTSLUtil.isNullString(_requestVO.getSid())) {
			messageArgArray= new String[]{ _requestVO.getSid(), _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getTransferValue()), PretupsBL
					.getDisplayAmount(_senderTransferItemVO.getPostBalance()), String.valueOf(_receiverTransferItemVO.getValidity()), PretupsBL
					.getDisplayAmount(_receiverTransferItemVO.getPostBalance()), String.valueOf(_receiverTransferItemVO.getNewGraceDate()), PretupsBL.getDisplayAmount(_c2sTransferVO
							.getReceiverAccessFee()), _c2sTransferVO.getSubService(),_c2sTransferVO.getProductName() };
		} else {
			messageArgArray= new String[]{ _requestVO.getMsisdn(), _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getTransferValue()), PretupsBL
					.getDisplayAmount(_senderTransferItemVO.getPostBalance()), String.valueOf(_receiverTransferItemVO.getValidity()), PretupsBL
					.getDisplayAmount(_receiverTransferItemVO.getPostBalance()), String.valueOf(_receiverTransferItemVO.getNewGraceDate()), PretupsBL.getDisplayAmount(_c2sTransferVO
							.getReceiverAccessFee()), _c2sTransferVO.getSubService(),_c2sTransferVO.getProductName() };
		}
		return BTSLUtil.getMessage(_senderLocale, PretupsErrorCodesI.C2S_SENDER_SUCCESS, messageArgArray);
	}

	protected String getReceiverUnderProcessMessage() {
		final String[] messageArgArray = { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverTransferValue()), String.valueOf(_receiverTransferItemVO
				.getValidity()), PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()), _senderPushMessageMsisdn, PretupsBL.getDisplayAmount(_c2sTransferVO
						.getReceiverAccessFee()), _channelUserVO.getUserName(), _requestVO.getPosUserMSISDN(),_c2sTransferVO.getProductName() };
		return BTSLUtil.getMessage(_receiverLocale, PretupsErrorCodesI.C2S_RECEIVER_UNDERPROCESS, messageArgArray, _requestVO.getRequestGatewayType());
	}

	private String getSenderUnderProcessMessage() {
		String[] messageArgArray ;
		if(!BTSLUtil.isNullString(_requestVO.getSid())) {
			messageArgArray = new String[]{ _requestVO.getSid(), _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()), String
					.valueOf(_receiverTransferItemVO.getValidity()), PretupsBL.getDisplayAmount(_senderTransferItemVO.getPostBalance()),_c2sTransferVO.getProductName() };
		} else {
			messageArgArray = new String[]{ _requestVO.getMsisdn(), _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()), String
					.valueOf(_receiverTransferItemVO.getValidity()), PretupsBL.getDisplayAmount(_senderTransferItemVO.getPostBalance()),_c2sTransferVO.getProductName() };
		}
		return BTSLUtil.getMessage(_senderLocale, PretupsErrorCodesI.C2S_SENDER_UNDERPROCESS, messageArgArray);
	}

	private String getSndrUPMsgBeforeValidation() {
		String[] messageArgArray;
		if(!BTSLUtil.isNullString(_requestVO.getSid())){
			messageArgArray=new String[] {_requestVO.getSid(),_transferID,PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()),_c2sTransferVO.getProductName()};
		} else {
			messageArgArray=new String[] {_receiverMSISDN,_transferID,PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()),_c2sTransferVO.getProductName()};
		}
		return BTSLUtil.getMessage(_senderLocale, PretupsErrorCodesI.C2S_SENDER_UNDERPROCESS_B4VAL, messageArgArray);
	}

	private String getReceiverAmbigousMessage() {
		String[] messageArgArray;
		messageArgArray=new String[]{_transferID,PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()),_senderPushMessageMsisdn,_channelUserVO.getUserName(),_requestVO.getPosUserMSISDN(),_c2sTransferVO.getProductName()};
		return BTSLUtil.getMessage(_receiverLocale, PretupsErrorCodesI.C2S_RECEIVER_AMBIGOUS_KEY, messageArgArray, _requestVO.getRequestGatewayType());
	}

	private void setSenderTransferItemVO() {
		_senderTransferItemVO = new C2STransferItemVO();
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

	private void setReceiverTransferItemVO() {
		_receiverTransferItemVO = new C2STransferItemVO();
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

	protected void updateSenderForFailedTransaction(Connection p_con) throws BTSLBaseException {
		final String methodName = "updateSenderForFailedTransaction";
		boolean privateSidServiceAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_SID_SERVICE_ALLOW);
		boolean sidEncryptionAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SID_ENCRYPTION_ALLOWED);
		try {
			PrivateRchrgVO prvo=null;
			if(privateSidServiceAllow)
			{
				PrivateRchrgDAO prdao= new PrivateRchrgDAO();
				prvo=prdao.loadSubscriberSIDDetails(p_con,_c2sTransferVO.getReceiverMsisdn());				
			}

			_userBalancesVO = ChannelUserBL.creditUserBalanceForProduct(p_con, _c2sTransferVO.getTransferID(), _c2sTransferVO);
			if(penaltyApplicable){
				((C2STransferItemVO)(_c2sTransferVO.getTransferItemList().get(0))).setTransferValue(_c2sTransferVO.getRoamPenalty());
				_userBalancesVO=ChannelUserBL.creditUserBalanceForProductRoam(p_con,_c2sTransferVO.getTransferID(),_c2sTransferVO);
			}
			if(penaltyApplicableOwner){
				userBalanceOwnerVO=ChannelUserBL.creditUserBalanceForProductRoamOwner(p_con,c2STransferOwnerVO.getTransferID(),c2STransferOwnerVO);
			}
			ChannelTransferBL.decreaseC2STransferOutCounts(p_con, _c2sTransferVO);

			/*if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,_c2sTransferVO.getNetworkCode()) && _c2sTransferVO.isOtfCountsIncreased() )
			{
				ChannelTransferBL.decreaseUserOTFCounts(p_con, _c2sTransferVO, _channelUserVO);
			}*/ 
			_creditBackEntryDone = true;
			if (_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_REQUEST)) {
				_requestVO.setSuccessTxn(false);
				final String[] messageArgArray = { prvo!=null ? (sidEncryptionAllow ? BTSLUtil.decrypt3DesAesText(prvo.getUserSID()):prvo.getUserSID()): _c2sTransferVO
						.getReceiverMsisdn(), PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()), _c2sTransferVO
						.getTransferID(), PretupsBL.getDisplayAmount(_userBalancesVO.getBalance()) };
				_requestVO.setMessageArguments(messageArgArray);
				_requestVO.setMessageCode(PretupsErrorCodesI.C2S_SENDER_CREDIT_SUCCESS);
			}
			TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
					"Credit Back Done", PretupsI.TXN_LOG_STATUS_SUCCESS, "");
		} catch (BTSLBaseException be) {
			_finalTransferStatusUpdate = false;
			_c2sTransferVO.setSenderReturnMessage(null);
			PretupsBL.validateRecieverLimits(p_con, _c2sTransferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.C2S_MODULE);
			TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
					"base Exception while crediting back sender", PretupsI.TXN_LOG_STATUS_FAIL, "Exception:" + be.getMessage() + " Getting Code=" + be.getMessageKey());
			_log.errorTrace(methodName, be);
			throw be;
		}
	}

	private void processValidationRequestInThread() throws BTSLBaseException, Exception {
		final String methodName = "processValidationRequestInThread";
		StringBuilder loggerValue= new StringBuilder(); 
		
		if (_log.isDebugEnabled()) {
			loggerValue.setLength(0);
        	loggerValue.append("Entered and performing validations for transfer ID=" );
        	loggerValue.append(_transferID);
			_log.debug(methodName, loggerValue );
		}
		boolean dbEntryNotAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED);
		try {
			TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Performing Validation in thread", PretupsI.TXN_LOG_STATUS_SUCCESS, "");
			processValidationRequest();
		} catch (BTSLBaseException be) {
			
			        loggerValue.setLength(0);
	            	loggerValue.append("Getting BTSL Base Exception:");
	            	loggerValue.append(be.getMessage());
			_log.error("C2SPrepaidController[processValidationRequestInThread]",  loggerValue);
			TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Base Exception while performing Validation in thread", PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + be.getMessageKey());
			throw be;
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
			if (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
				_c2sTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.C2S_RECEIVER_FAIL), new String[] { String.valueOf(_transferID), PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()) }));
			}

			_c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
				_c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			}
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SPrepaidController[processValidationRequestInThread]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
			TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Exception while performing Validation in thread", PretupsI.TXN_LOG_STATUS_FAIL, "Getting exception =" + e.getMessage());
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		} finally {
			if (_transferID != null && !_transferDetailAdded) {
				Connection con = null;
				MComConnectionI mcomCon = null;
				try {
					mcomCon = new MComConnection();
					con = mcomCon.getConnection();
					if (!dbEntryNotAllowed) {
						addEntryInTransfers(con);
					}
					mcomCon.finalCommit();
					if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
						_finalTransferStatusUpdate = false; 
					}
				} catch (BTSLBaseException be) {
					try {
						mcomCon.finalRollback();
					} catch (SQLException sqle) {
						_log.errorTrace(methodName, sqle);
					}
					_log.errorTrace(methodName, be);
				} catch (Exception e) {
					try {
						if(mcomCon!=null)
						{
						mcomCon.finalRollback();
						}
					} catch (SQLException sqle) {
						_log.errorTrace(methodName, sqle);
					}
					_log.errorTrace(methodName, e);
					EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SPrepaidController[processValidationRequestInThread]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
				} finally {
					if(mcomCon != null )
						mcomCon.close("C2SPrepaidController#processValidationRequestInThread");
					mcomCon = null;
					con = null;
				}
			}
			if (_log.isDebugEnabled()) {
				_log.debug("process", "Exiting");
			}
		}
	}

	protected void addEntryInTransfers(Connection p_con) {
		final String methodName = "addEntryInTransfers";
		try {
			if (!_transferDetailAdded && _transferEntryReqd) {
				ChannelTransferBL.addC2STransferDetails(p_con, _c2sTransferVO);
			} else if (_transferDetailAdded) {
				_c2sTransferVO.setModifiedOn(new Date());
				_c2sTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
				ChannelTransferBL.updateC2STransferDetails(p_con, _c2sTransferVO);
			}
		} catch (BTSLBaseException be) {
			_log.errorTrace(methodName, be);
			if (!_isCounterDecreased && _decreaseTransactionCounts) {
				LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
				_isCounterDecreased = true;
			}
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SPrepaidController[process]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
			if (!_isCounterDecreased && _decreaseTransactionCounts) {
				LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
				_isCounterDecreased = true;
			}
		}
	}

	private boolean getInterfaceRoutingDetails(Connection p_con, String p_msisdn, long p_prefixID, String p_subscriberType, String p_networkCode, String p_serviceType, String p_interfaceCategory, String p_userType, String p_action) throws BTSLBaseException {
		final String methodName = "getInterfaceRoutingDetails";
		StringBuilder loggerValue= new StringBuilder(); 
		if (_log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append(" Entered with MSISDN=");
			loggerValue.append(p_msisdn);
			loggerValue.append(" Prefix ID=");
			loggerValue.append(p_prefixID);
			loggerValue.append(" p_subscriberType=");
			loggerValue.append(p_subscriberType);
			loggerValue.append(" p_networkCode=");
            loggerValue.append(p_networkCode);
            loggerValue.append(" p_serviceType=");
            loggerValue.append(p_serviceType);
            loggerValue.append(" p_interfaceCategory=");
            loggerValue.append(p_interfaceCategory);
            loggerValue.append(" p_userType=");
            loggerValue.append(p_userType);
            loggerValue.append(" p_action=");
            loggerValue.append(p_action);
			_log.debug(methodName,loggerValue );
		}
		boolean isSuccess = false;

		String interfaceID = null;
		String interfaceHandlerClass = null;
		String underProcessMsgReqd = null;
		String allServiceClassID = null;
		final SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache
				.getRoutingControlDetails(p_networkCode + "_" + p_serviceType + "_" + p_interfaceCategory);
		boolean selectorInterfaceMapping = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING);
		try {
			if (_log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append(" subscriberRoutingControlVO=");
				loggerValue.append(subscriberRoutingControlVO);
				_log.debug(methodName,  loggerValue);
			}

			if (subscriberRoutingControlVO != null) {
				if (subscriberRoutingControlVO.isDatabaseCheckBool()) {
					if (p_interfaceCategory.equalsIgnoreCase(PretupsI.INTERFACE_CATEGORY_PRE)) {
						final ListValueVO listValueVO = PretupsBL.validateNumberInRoutingDatabase(p_con, p_msisdn, p_interfaceCategory);
						if (listValueVO != null) {
							interfaceID = listValueVO.getValue();
							interfaceHandlerClass = listValueVO.getLabel();
							underProcessMsgReqd = listValueVO.getType();
							allServiceClassID = listValueVO.getTypeName();
							if (p_userType.equals(PretupsI.USER_TYPE_RECEIVER) && p_action.equals(PretupsI.INTERFACE_VALIDATE_ACTION)) {
								_receiverInterfaceInfoInDBFound = true;
							}
							_externalID = listValueVO.getIDValue();
							_interfaceStatusType = listValueVO.getStatusType();
							isSuccess = true;
							_receiverSubscriberType = p_interfaceCategory;

							if (!PretupsI.YES.equals(listValueVO.getStatus()) && PretupsI.INTERFACE_STATUS_TYPE_MANUAL.equals(listValueVO.getStatusType())) {
								if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(_senderLocale)).getMessage())) {
									_c2sTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo());
								} else {
									_c2sTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo2());
								}
								throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INTERFACE_NOT_ACTIVE);
							}
						} else if (subscriberRoutingControlVO.isSeriesCheckBool()) {
							ServiceSelectorInterfaceMappingVO interfaceMappingVO1 = null;
							if (selectorInterfaceMapping) {
								interfaceMappingVO1 = ServiceSelectorInterfaceMappingCache
										.getObject(_serviceType + "_" + _c2sTransferVO.getSubService() + "_" + p_action + "_" + p_networkCode + "_" + p_prefixID);
								if (interfaceMappingVO1 != null) {
									interfaceID = interfaceMappingVO1.getInterfaceID();
									interfaceHandlerClass = interfaceMappingVO1.getHandlerClass();
									underProcessMsgReqd = interfaceMappingVO1.getUnderProcessMsgRequired();
									allServiceClassID = interfaceMappingVO1.getAllServiceClassID();
									_externalID = interfaceMappingVO1.getExternalID();
									_interfaceStatusType = interfaceMappingVO1.getStatusType();
									isSuccess = true;
									_receiverSubscriberType = p_subscriberType;
									if (!PretupsI.YES.equals(interfaceMappingVO1.getInterfaceStatus()) && PretupsI.INTERFACE_STATUS_TYPE_MANUAL.equals(interfaceMappingVO1
											.getStatusType())) {
										if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(_senderLocale)).getMessage())) {
											_c2sTransferVO.setSenderReturnMessage(interfaceMappingVO1.getLanguage1Message());
										} else {
											_c2sTransferVO.setSenderReturnMessage(interfaceMappingVO1.getLanguage2Message());
										}
										throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INTERFACE_NOT_ACTIVE);
									}
								}
							} else {
								if (interfaceMappingVO1 == null) {
									final MSISDNPrefixInterfaceMappingVO interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(
											p_prefixID, _serviceType, p_action);
									interfaceID = interfaceMappingVO.getInterfaceID();
									interfaceHandlerClass = interfaceMappingVO.getHandlerClass();
									underProcessMsgReqd = interfaceMappingVO.getUnderProcessMsgRequired();
									allServiceClassID = interfaceMappingVO.getAllServiceClassID();
									_externalID = interfaceMappingVO.getExternalID();
									_interfaceStatusType = interfaceMappingVO.getStatusType();
									isSuccess = true;
									_receiverSubscriberType = p_subscriberType;

									if (!PretupsI.YES.equals(interfaceMappingVO.getInterfaceStatus()) && PretupsI.INTERFACE_STATUS_TYPE_MANUAL.equals(interfaceMappingVO
											.getStatusType())) {
										if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(_senderLocale)).getMessage())) {
											_c2sTransferVO.setSenderReturnMessage(interfaceMappingVO.getLanguage1Message());
										} else {
											_c2sTransferVO.setSenderReturnMessage(interfaceMappingVO.getLanguage2Message());
										}
										throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INTERFACE_NOT_ACTIVE);
									}
								}
							}
						} else {
							isSuccess = false;
						}
					}
					else if (p_interfaceCategory.equalsIgnoreCase(PretupsI.INTERFACE_CATEGORY_POST)) {
					}
				} else if (subscriberRoutingControlVO.isSeriesCheckBool()) {
					ServiceSelectorInterfaceMappingVO interfaceMappingVO1 = null;
					if (selectorInterfaceMapping) {
						interfaceMappingVO1 = ServiceSelectorInterfaceMappingCache
								.getObject(_serviceType + "_" + _c2sTransferVO.getSubService() + "_" + p_action + "_" + p_networkCode + "_" + p_prefixID);
						if (interfaceMappingVO1 != null) {
							interfaceID = interfaceMappingVO1.getInterfaceID();
							interfaceHandlerClass = interfaceMappingVO1.getHandlerClass();
							underProcessMsgReqd = interfaceMappingVO1.getUnderProcessMsgRequired();
							allServiceClassID = interfaceMappingVO1.getAllServiceClassID();
							_externalID = interfaceMappingVO1.getExternalID();
							_interfaceStatusType = interfaceMappingVO1.getStatusType();
							isSuccess = true;
							_receiverSubscriberType = p_subscriberType;
							if (!PretupsI.YES.equals(interfaceMappingVO1.getInterfaceStatus()) && PretupsI.INTERFACE_STATUS_TYPE_MANUAL.equals(interfaceMappingVO1
									.getStatusType())) {
								if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(_senderLocale)).getMessage())) {
									_c2sTransferVO.setSenderReturnMessage(interfaceMappingVO1.getLanguage1Message());
								} else {
									_c2sTransferVO.setSenderReturnMessage(interfaceMappingVO1.getLanguage2Message());
								}
								throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INTERFACE_NOT_ACTIVE);
							}
						}
					} else {
						if (interfaceMappingVO1 == null) {
							final MSISDNPrefixInterfaceMappingVO interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_prefixID,
									_serviceType, p_action);
							interfaceID = interfaceMappingVO.getInterfaceID();
							interfaceHandlerClass = interfaceMappingVO.getHandlerClass();
							underProcessMsgReqd = interfaceMappingVO.getUnderProcessMsgRequired();
							allServiceClassID = interfaceMappingVO.getAllServiceClassID();
							_externalID = interfaceMappingVO.getExternalID();
							_interfaceStatusType = interfaceMappingVO.getStatusType();
							isSuccess = true;
							_receiverSubscriberType = p_subscriberType;
							if (!PretupsI.YES.equals(interfaceMappingVO.getInterfaceStatus()) && PretupsI.INTERFACE_STATUS_TYPE_MANUAL.equals(interfaceMappingVO
									.getStatusType())) {
								if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(_senderLocale)).getMessage())) {
									_c2sTransferVO.setSenderReturnMessage(interfaceMappingVO.getLanguage1Message());
								} else {
									_c2sTransferVO.setSenderReturnMessage(interfaceMappingVO.getLanguage2Message());
								}
								throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INTERFACE_NOT_ACTIVE);
							}
						}
					}
				} else {
					isSuccess = false;
				}
			} else {
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "C2SPrepaidController[getInterfaceRoutingDetails]",
						_transferID, _senderMSISDN, _senderNetworkCode, "Exception:Routing control information not defined so performing series based routing");
				ServiceSelectorInterfaceMappingVO interfaceMappingVO1 = null;
				if (selectorInterfaceMapping) {
					interfaceMappingVO1 = ServiceSelectorInterfaceMappingCache
							.getObject(_serviceType + "_" + _c2sTransferVO.getSubService() + "_" + p_action + "_" + p_networkCode + "_" + p_prefixID);
					if (interfaceMappingVO1 != null) {
						interfaceID = interfaceMappingVO1.getInterfaceID();
						interfaceHandlerClass = interfaceMappingVO1.getHandlerClass();
						underProcessMsgReqd = interfaceMappingVO1.getUnderProcessMsgRequired();
						allServiceClassID = interfaceMappingVO1.getAllServiceClassID();
						_externalID = interfaceMappingVO1.getExternalID();
						_interfaceStatusType = interfaceMappingVO1.getStatusType();
						isSuccess = true;
						_receiverSubscriberType = p_subscriberType;
						if (!PretupsI.YES.equals(interfaceMappingVO1.getInterfaceStatus()) && PretupsI.INTERFACE_STATUS_TYPE_MANUAL
								.equals(interfaceMappingVO1.getStatusType())) {
							if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(_senderLocale)).getMessage())) {
								_c2sTransferVO.setSenderReturnMessage(interfaceMappingVO1.getLanguage1Message());
							} else {
								_c2sTransferVO.setSenderReturnMessage(interfaceMappingVO1.getLanguage2Message());
							}
							throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INTERFACE_NOT_ACTIVE);
						}
					}
				} else {
					if (interfaceMappingVO1 == null) {
						final MSISDNPrefixInterfaceMappingVO interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_prefixID,
								_serviceType, p_action);
						interfaceID = interfaceMappingVO.getInterfaceID();
						interfaceHandlerClass = interfaceMappingVO.getHandlerClass();
						underProcessMsgReqd = interfaceMappingVO.getUnderProcessMsgRequired();
						allServiceClassID = interfaceMappingVO.getAllServiceClassID();
						_externalID = interfaceMappingVO.getExternalID();
						_interfaceStatusType = interfaceMappingVO.getStatusType();
						isSuccess = true;
						_receiverSubscriberType = p_subscriberType;
						if (!PretupsI.YES.equals(interfaceMappingVO.getInterfaceStatus()) && PretupsI.INTERFACE_STATUS_TYPE_MANUAL.equals(interfaceMappingVO.getStatusType())) {
							if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(_senderLocale)).getMessage())) {
								_c2sTransferVO.setSenderReturnMessage(interfaceMappingVO.getLanguage1Message());
							} else {
								_c2sTransferVO.setSenderReturnMessage(interfaceMappingVO.getLanguage2Message());
							}
							throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INTERFACE_NOT_ACTIVE);
						}
					}
				}
			}

			if (isSuccess && p_userType.equals(PretupsI.USER_TYPE_RECEIVER)) {
				_receiverTransferItemVO.setInterfaceID(interfaceID);
				_receiverTransferItemVO.setInterfaceType(_type);
				_receiverTransferItemVO.setInterfaceHandlerClass(interfaceHandlerClass);
				if (PretupsI.YES.equals(underProcessMsgReqd)) {
					_c2sTransferVO.setUnderProcessMsgReq(true);
				}
				_receiverAllServiceClassID = allServiceClassID;
				_c2sTransferVO.setReceiverInterfaceStatusType(_interfaceStatusType);
			}
		} catch (BTSLBaseException be) {
			if (isSuccess && p_userType.equals(PretupsI.USER_TYPE_RECEIVER)) {
				_receiverTransferItemVO.setInterfaceID(interfaceID);
				_receiverTransferItemVO.setInterfaceType(_type);
				_receiverTransferItemVO.setInterfaceHandlerClass(interfaceHandlerClass);
				if (PretupsI.YES.equals(underProcessMsgReqd)) {
					_c2sTransferVO.setUnderProcessMsgReq(true);
				}
				_receiverAllServiceClassID = allServiceClassID;
				_c2sTransferVO.setReceiverInterfaceStatusType(_interfaceStatusType);
			}
			throw be;
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
			loggerValue.setLength(0);
			loggerValue.append("Exception:");
			loggerValue.append(e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SPrepaidController[getInterfaceRoutingDetails]",
					_transferID, _senderMSISDN, _senderNetworkCode,  loggerValue.toString() );
			isSuccess = false;
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if (_log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append(" Exiting with isSuccess=" );
			loggerValue.append(isSuccess);
			_log.debug(methodName, loggerValue );
		}
		return isSuccess;
	}

	private void performAlternateRouting(ArrayList altList) throws BTSLBaseException {
		StringBuilder loggerValue= new StringBuilder(); 
		try {
			if (altList != null && !altList.isEmpty()) {
				ListValueVO listValueVO = null;
				String requestStr ;
				CommonClient commonClient = null;
				String receiverValResponse ;
				switch (altList.size()) {
				case 1:
				{
					LoadController.decreaseResponseCounters(_transferID, _receiverVO.getTransactionStatus(), LoadControllerI.SENDER_VAL_RESPONSE);
					LoadController.decreaseTransactionInterfaceLoad(_transferID, _c2sTransferVO.getReceiverNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);

					listValueVO = (ListValueVO) altList.get(0);
					_receiverTransferItemVO.setInterfaceID(listValueVO.getValue());
					_receiverTransferItemVO.setInterfaceHandlerClass(listValueVO.getLabel());
					if (PretupsI.YES.equals(listValueVO.getType())) {
						_c2sTransferVO.setUnderProcessMsgReq(true);
					}
					_receiverAllServiceClassID = listValueVO.getTypeName();
					_externalID = listValueVO.getIDValue();
					_interfaceStatusType = listValueVO.getStatusType();
					_c2sTransferVO.setReceiverAllServiceClassID(_receiverAllServiceClassID);
					_c2sTransferVO.setReceiverInterfaceStatusType(_interfaceStatusType);

					if (!PretupsI.YES.equals(listValueVO.getStatus()) && PretupsI.INTERFACE_STATUS_TYPE_MANUAL.equals(listValueVO.getStatusType())) {
						if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(_senderLocale)).getMessage())) {
							_c2sTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo());
						} else {
							_c2sTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo2());
						}
						throw new BTSLBaseException(this, "performAlternateRouting", PretupsErrorCodesI.INTERFACE_NOT_ACTIVE);
					}
					checkTransactionLoad();
					PretupsBL.validateRecieverLimits(null, _c2sTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL, PretupsI.C2S_MODULE);
					requestStr = getReceiverValidateStr();
					commonClient = new CommonClient();
					LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_VAL);
					TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ,
							PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "Performing Interface Routing 1");
					receiverValResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);
					TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES,
							PretupsI.TXN_LOG_TXNSTAGE_INVAL, receiverValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");
					try {
						receiverValidateResponse(receiverValResponse, 1, altList.size());
						if (InterfaceErrorCodesI.SUCCESS.equals(_receiverTransferItemVO.getValidationStatus())) {
							if (_receiverInterfaceInfoInDBFound) {
								PretupsBL.updateSubscriberInterfaceRouting(_receiverTransferItemVO.getInterfaceID(), _externalID, _receiverMSISDN, _type,
										_channelUserVO.getUserID(), _currentDate);
							} else {
								final SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(_c2sTransferVO
										.getReceiverNetworkCode() + "_" + _c2sTransferVO.getServiceType() + "_" + _type);
								if (!_receiverInterfaceInfoInDBFound && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
									PretupsBL.insertSubscriberInterfaceRouting(_receiverTransferItemVO.getInterfaceID(), _externalID, _receiverMSISDN, _type,
											_channelUserVO.getUserID(), _currentDate);
									_receiverInterfaceInfoInDBFound = true;
								}
							}
						}
					} catch (BTSLBaseException be) {
						throw be;
					} catch (Exception e) {
						throw new BTSLBaseException(this, "performAlternateRouting", "");
					}
					break;
				}
				case 2:
				{
					LoadController.decreaseResponseCounters(_transferID, _receiverVO.getTransactionStatus(), LoadControllerI.SENDER_VAL_RESPONSE);
					LoadController.decreaseTransactionInterfaceLoad(_transferID, _c2sTransferVO.getReceiverNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);
					listValueVO = (ListValueVO) altList.get(0);
					_receiverTransferItemVO.setInterfaceID(listValueVO.getValue());
					_receiverTransferItemVO.setInterfaceHandlerClass(listValueVO.getLabel());
					if (PretupsI.YES.equals(listValueVO.getType())) {
						_c2sTransferVO.setUnderProcessMsgReq(true);
					}
					_receiverAllServiceClassID = listValueVO.getTypeName();
					_externalID = listValueVO.getIDValue();
					_interfaceStatusType = listValueVO.getStatusType();
					_c2sTransferVO.setReceiverInterfaceStatusType(_interfaceStatusType);
					_c2sTransferVO.setReceiverAllServiceClassID(_receiverAllServiceClassID);
					if (!PretupsI.YES.equals(listValueVO.getStatus()) && PretupsI.INTERFACE_STATUS_TYPE_MANUAL.equals(listValueVO.getStatusType())) {
						if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(_senderLocale)).getMessage())) {
							_c2sTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo());
						} else {
							_c2sTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo2());
						}
						throw new BTSLBaseException(this, "performAlternateRouting", PretupsErrorCodesI.INTERFACE_NOT_ACTIVE);
					}
					checkTransactionLoad();
					PretupsBL.validateRecieverLimits(null, _c2sTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL, PretupsI.C2S_MODULE);
					requestStr = getReceiverValidateStr();
					commonClient = new CommonClient();
					LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_VAL);
					TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ,
							PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "Performing Interface Routing 1");
					receiverValResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);
					TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES,
							PretupsI.TXN_LOG_TXNSTAGE_INVAL, receiverValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");
					try {
						receiverValidateResponse(receiverValResponse, 1, altList.size());
						if (InterfaceErrorCodesI.SUCCESS.equals(_receiverTransferItemVO.getValidationStatus())) {
							if (_receiverInterfaceInfoInDBFound) {
								PretupsBL.updateSubscriberInterfaceRouting(_receiverTransferItemVO.getInterfaceID(), _externalID, _receiverMSISDN, _type,
										_channelUserVO.getUserID(), _currentDate);
							} else {
								final SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(_c2sTransferVO
										.getReceiverNetworkCode() + "_" + _c2sTransferVO.getServiceType() + "_" + _type);
								if (!_receiverInterfaceInfoInDBFound && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
									PretupsBL.insertSubscriberInterfaceRouting(_receiverTransferItemVO.getInterfaceID(), _externalID, _receiverMSISDN, _type,
											_channelUserVO.getUserID(), _currentDate);
									_receiverInterfaceInfoInDBFound = true;
								}
							}
						}
					} catch (BTSLBaseException be) {
						if (be.isKey() && InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(be.getMessageKey())) {
							LoadController.decreaseResponseCounters(_transferID, _receiverVO.getTransactionStatus(), LoadControllerI.SENDER_VAL_RESPONSE);
							LoadController
							.decreaseTransactionInterfaceLoad(_transferID, _c2sTransferVO.getReceiverNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);
							listValueVO = (ListValueVO) altList.get(1);
							_receiverTransferItemVO.setInterfaceID(listValueVO.getValue());
							_receiverTransferItemVO.setInterfaceHandlerClass(listValueVO.getLabel());
							if (PretupsI.YES.equals(listValueVO.getType())) {
								_c2sTransferVO.setUnderProcessMsgReq(true);
							}
							_receiverAllServiceClassID = listValueVO.getTypeName();
							_externalID = listValueVO.getIDValue();
							_interfaceStatusType = listValueVO.getStatusType();
							_c2sTransferVO.setReceiverInterfaceStatusType(_interfaceStatusType);
							_c2sTransferVO.setReceiverAllServiceClassID(_receiverAllServiceClassID);
							if (!PretupsI.YES.equals(listValueVO.getStatus()) && PretupsI.INTERFACE_STATUS_TYPE_MANUAL.equals(listValueVO.getStatusType())) {
								if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(_senderLocale)).getMessage())) {
									_c2sTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo());
								} else {
									_c2sTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo2());
								}
								throw new BTSLBaseException(this, "performAlternateRouting", PretupsErrorCodesI.INTERFACE_NOT_ACTIVE);
							}
							checkTransactionLoad();
							PretupsBL.validateRecieverLimits(null, _c2sTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL, PretupsI.C2S_MODULE);
							requestStr = getReceiverValidateStr();
							LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_VAL);
							TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ,
									PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "Performing Interface Routing 2");
							receiverValResponse = commonClient
									.process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);
							TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES,
									PretupsI.TXN_LOG_TXNSTAGE_INVAL, receiverValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");
							try {
								receiverValidateResponse(receiverValResponse, 2, altList.size());
								if (InterfaceErrorCodesI.SUCCESS.equals(_receiverTransferItemVO.getValidationStatus())) {
									if (_receiverInterfaceInfoInDBFound) {
										PretupsBL.updateSubscriberInterfaceRouting(_receiverTransferItemVO.getInterfaceID(), _externalID, _receiverMSISDN, _type,
												_channelUserVO.getUserID(), _currentDate);
									} else {
										final SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache
												.getRoutingControlDetails(_c2sTransferVO.getReceiverNetworkCode() + "_" + _c2sTransferVO.getServiceType() + "_" + _type);
										if (!_receiverInterfaceInfoInDBFound && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
											PretupsBL.insertSubscriberInterfaceRouting(_receiverTransferItemVO.getInterfaceID(), _externalID, _receiverMSISDN, _type,
													_channelUserVO.getUserID(), _currentDate);
											_receiverInterfaceInfoInDBFound = true;
										}
									}
								}
							} catch (BTSLBaseException bex) {
								throw new BTSLBaseException(bex);
							} catch (Exception e) {
								throw new BTSLBaseException(this, "performAlternateRouting", "");
							}
						} else {
							throw be;
						}
					} catch (Exception e) {
						throw new BTSLBaseException(this, "performAlternateRouting", "");
					}
					break;
				}
				default:
                  	 if(_log.isDebugEnabled()){
                  		_log.debug("Default Value " , altList.size());
                  	 }
				}
			} else {
				return;
			}
		} catch (BTSLBaseException be) {
			throw be;
		} catch (Exception e) {
			_log.errorTrace("performAlternateRouting", e);
			loggerValue.setLength(0);
			loggerValue.append("Exception:");
			loggerValue.append(e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SPrepaidController[performAlternateRouting]",
					_transferID, _senderMSISDN, _senderNetworkCode, loggerValue.toString());
			throw new BTSLBaseException(this, "updateSubscriberInterfaceRouting", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
	}

	public void receiverValidateResponse(String str, int p_attempt, int p_altSize) throws BTSLBaseException {
		final String methodName = "receiverValidateResponse";
		final HashMap map = BTSLUtil.getStringToHash(str, "&", "=");
		final String status = (String) map.get("TRANSACTION_STATUS");
		final String interfaceStatusType = (String) map.get("INT_SET_STATUS");
		if (!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME
				.equals(interfaceStatusType))) {
			new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES, _receiverTransferItemVO.getInterfaceID(), interfaceStatusType,
					PretupsErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG, PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
		}

		if (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) && p_attempt == 1 && p_attempt < p_altSize) {
			_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
		}
		if ("Y".equals(_requestVO.getUseInterfaceLanguage())) {
			updateReceiverLocale((String) map.get("IN_LANG"));
		}
		_receiverTransferItemVO.setProtocolStatus((String) map.get("PROTOCOL_STATUS"));
		_receiverTransferItemVO.setAccountStatus((String) map.get("ACCOUNT_STATUS"));
		if (BTSLUtil.isNullString(_receiverTransferItemVO.getAccountStatus())) {
			_receiverTransferItemVO.setAccountStatus(" ");
		}
		_receiverTransferItemVO.setInterfaceResponseCode((String) map.get("INTERFACE_STATUS"));
		_receiverTransferItemVO.setValidationStatus(status);
		_receiverVO.setInterfaceResponseCode(_receiverTransferItemVO.getInterfaceResponseCode());
		_receiverTransferItemVO.setInterfaceReferenceID((String) map.get("IN_TXN_ID"));
		_receiverTransferItemVO.setReferenceID((String) map.get("IN_RECON_ID"));
		_receiverTransferItemVO.setSubscriberType(_receiverSubscriberType);
		String[] strArr ;
		if (BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS)) {
			_c2sTransferVO.setErrorCode(status + "_R");
			_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			_receiverTransferItemVO.setTransferStatus(status);
			_senderTransferItemVO.setValidationStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
			_senderTransferItemVO.setTransferStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
			strArr = new String[] { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()) };
			throw new BTSLBaseException("C2SPrepaidController", "updateForReceiverValidateResponse", PretupsErrorCodesI.C2S_RECEIVER_FAIL, 0, strArr, null);
		}
		_receiverTransferItemVO.setTransferStatus(status);
		_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);

		try {
			_receiverTransferItemVO.setPreviousExpiry(BTSLUtil.getDateFromDateString((String) map.get("OLD_EXPIRY_DATE"), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		try {
			_receiverTransferItemVO.setPreviousGraceDate(BTSLUtil.getDateFromDateString((String) map.get("OLD_GRACE_DATE"), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		_receiverTransferItemVO.setServiceClassCode((String) map.get("SERVICE_CLASS"));
		_receiverVO.setServiceClassCode(_receiverTransferItemVO.getServiceClass());
		_receiverVO.setInterfaceResponseCode(_receiverTransferItemVO.getInterfaceResponseCode());
		try {
			_receiverTransferItemVO.setPreviousBalance(Long.parseLong((String) map.get("INTERFACE_PREV_BALANCE")));
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		_receiverTransferItemVO.setFirstCall((String) map.get("FIRST_CALL"));
		_receiverTransferItemVO.setGraceDaysStr((String) map.get("GRACE_DAYS"));
		_receiverTransferItemVO.setBundleTypes((String) map.get("IN_RESP_BUNDLE_CODES"));
		_receiverTransferItemVO.setInAccountId((String) map.get("IN_ACCOUNT_ID"));
		_receiverTransferItemVO.setSelectorName(_selectorName);
		if (_receiverTransferItemVO.getPreviousExpiry() == null) {
			_receiverTransferItemVO.setPreviousExpiry(_currentDate);
		}
		_operatorUtil.populateBonusListAfterValidation(map, _c2sTransferVO);
	}

	public void processFromQueue(TransferVO p_transferVO) {
		final String methodName = "processFromQueue";
		StringBuilder loggerValue= new StringBuilder(); 
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		boolean dbEntryNotAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED);
		try {
			_c2sTransferVO = (C2STransferVO) p_transferVO;
			_requestVO = _c2sTransferVO.getRequestVO();
			_channelUserVO = (ChannelUserVO) _requestVO.getSenderVO();
			_type = _requestVO.getType();
			_requestID = _requestVO.getRequestID();
			_requestIDStr = _requestVO.getRequestIDStr();
			_receiverLocale = _requestVO.getReceiverLocale();
			_transferID = _c2sTransferVO.getTransferID();
			_receiverVO = (ReceiverVO) _c2sTransferVO.getReceiverVO();
			_senderMSISDN = (_channelUserVO.getUserPhoneVO()).getMsisdn();
			_receiverMSISDN = ((ReceiverVO) _c2sTransferVO.getReceiverVO()).getMsisdn();
			_senderLocale = _requestVO.getSenderLocale();
			_senderNetworkCode = _channelUserVO.getNetworkID();
			_serviceType = _requestVO.getServiceType();
			_senderTransferItemVO = _c2sTransferVO.getSenderTransferItemVO();
			_receiverTransferItemVO = _c2sTransferVO.getReceiverTransferItemVO();
			_transferEntryReqd = true;
			_receiverSubscriberType = _c2sTransferVO.getReceiverSubscriberType();
			LoadController.checkTransactionLoad(((ReceiverVO) _c2sTransferVO.getReceiverVO()).getNetworkCode(), _receiverTransferItemVO.getInterfaceID(), PretupsI.C2S_MODULE,
					_transferID, true, LoadControllerI.USERTYPE_SENDER);
			try {
				mcomCon = new MComConnection();
				con = mcomCon.getConnection();
				_c2sTransferVO.setUnderProcessCheckReqd(_requestVO.getMessageGatewayVO().getRequestGatewayVO().getUnderProcessCheckReqd());
				PretupsBL.loadRecieverControlLimits(con, _requestIDStr, _c2sTransferVO);
				_receiverVO.setUnmarkRequestStatus(true);
				mcomCon.finalCommit();
			} catch (Exception e) {
				try {
					mcomCon.finalRollback();
				} catch (SQLException sqle) {
					_log.errorTrace(methodName, sqle);
				}
				_log.errorTrace(methodName, e);
				throw new BTSLBaseException("C2SPrepaidController", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			}finally{
				if(mcomCon != null )
					mcomCon.close("C2SPrepaidController#processFromQueue");
				mcomCon = null;
				con = null;
			}
			if (_log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("_transferID=");
				loggerValue.append(_transferID);
				loggerValue.append(" Successfully through load");
				_log.debug("C2SPrepaidController[processFromQueue]",  loggerValue);
			}
			_processedFromQueue = true;
			processValidationRequest();
			p_transferVO.setMessageCode(PretupsErrorCodesI.SENDER_UNDERPROCESS_SUCCESS);
			final String[] messageArgArray = { p_transferVO.getTransferID(), PretupsBL.getDisplayAmount(p_transferVO.getRequestedAmount()) };
			p_transferVO.setMessageArguments(messageArgArray);
		} catch (BTSLBaseException be) {
			_log.errorTrace(methodName, be);
			try {
				if (_receiverVO != null && _receiverVO.isUnmarkRequestStatus()) { 
					if (mcomCon == null){	
						mcomCon = new MComConnection();
						con = mcomCon.getConnection();
					}
					PretupsBL.unmarkReceiverLastRequest(con, _requestIDStr, _receiverVO);
				}
			} catch (BTSLBaseException bex) {
				try {
					mcomCon.finalRollback();
				} catch (SQLException sqle) {
					_log.errorTrace(methodName, sqle);
				}
				_log.errorTrace(methodName, bex);
				loggerValue.setLength(0);
				loggerValue.append("Leaving Reciever Unmarked Base Exception:");
				loggerValue.append(bex.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "C2SPrepaidController[processFromQueue]",
						_transferID, _senderMSISDN, _senderNetworkCode,  loggerValue.toString());
			} catch (Exception e) {
				try {
					mcomCon.finalRollback();
				} catch (SQLException sqle) {
					_log.errorTrace(methodName, sqle);
				}
				_log.errorTrace(methodName, e);
				loggerValue.setLength(0);
				loggerValue.append("Leaving Reciever Unmarked Base Exception:");
				loggerValue.append(e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED,
						EventLevelI.MAJOR, "C2SPrepaidController[processFromQueue]", _transferID, _senderMSISDN,
						_senderNetworkCode, loggerValue.toString());
				_c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			}
			_c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			if (be.isKey()) 
			{
				if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
					_c2sTransferVO.setErrorCode(be.getMessageKey());
				}
				_c2sTransferVO.setMessageCode(be.getMessageKey());
				_c2sTransferVO.setMessageArguments(be.getArgs());
				_requestVO.setMessageCode(be.getMessageKey());
				_requestVO.setMessageArguments(be.getArgs());
			} else {
				_c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			}
			LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
			_isCounterDecreased = true;
			TransactionLog.log(_transferID, _requestVO.getRequestIDStr(), _requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, _c2sTransferVO.getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + _requestVO.getMessageCode());
		} catch (Exception e) {
			try {
				if (_receiverVO != null && _receiverVO.isUnmarkRequestStatus()) {
					if (mcomCon == null){	
						mcomCon = new MComConnection();
						con = mcomCon.getConnection();
					}
					PretupsBL.unmarkReceiverLastRequest(con, _requestIDStr, _receiverVO);
				}
			} catch (BTSLBaseException bex) {
				try {
					mcomCon.finalRollback();
				} catch (SQLException sqle) {
					_log.errorTrace(methodName, sqle);
				}
				_log.errorTrace(methodName, bex);
				loggerValue.setLength(0);
				loggerValue.append("Leaving Reciever Unmarked Base Exception:");
				loggerValue.append(bex.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
						"C2SPrepaidController[processFromQueue]", _transferID, _senderMSISDN, _senderNetworkCode, 
						loggerValue.toString());
			} catch (Exception ex) {
				try {
					mcomCon.finalRollback();
				} catch (SQLException sqle) {
					_log.errorTrace(methodName, sqle);
				}
				_log.errorTrace(methodName, ex);
				loggerValue.setLength(0);
				loggerValue.append("Leaving Reciever Unmarked Exception:");
				loggerValue.append(ex.getMessage());
				_c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, 
						EventLevelI.MAJOR, "C2SPrepaidController[processFromQueue]", _transferID, _senderMSISDN, 
						_senderNetworkCode, loggerValue.toString() );
			}
			if (_recValidationFailMessageRequired) {
				if (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
					if (_transferID != null) {
						_c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.C2S_RECEIVER_FAIL, new String[] { String.valueOf(_transferID), PretupsBL
								.getDisplayAmount(_c2sTransferVO.getRequestedAmount()) }));
					} else {
						_c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.FAIL_R, new String[] { PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()) }));
					}
				}
			}
			_c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			_c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			_c2sTransferVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			_log.errorTrace(methodName, e);
			LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
			_isCounterDecreased = true;
			loggerValue.setLength(0);
			loggerValue.append("Exception:");
			loggerValue.append(e.getMessage());
			
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED,
					EventLevelI.FATAL, "C2SPrepaidController[processFromQueue]", _transferID, _senderMSISDN, 
					_senderNetworkCode, loggerValue.toString() );
			TransactionLog.log(_transferID, _requestVO.getRequestIDStr(), _requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, _c2sTransferVO.getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + _requestVO.getMessageCode());
		} finally {
			try {
				if (mcomCon == null) {
					mcomCon = new MComConnection();
					con = mcomCon.getConnection();
				}
				if (_transferID != null && !_transferDetailAdded) {
					if (!dbEntryNotAllowed) {
						addEntryInTransfers(con);
					}
				}
				mcomCon.finalCommit();
			} catch (BTSLBaseException be) {
				try {
					
					mcomCon.finalRollback();
					
				} catch (SQLException sqle) {
					_log.errorTrace(methodName, sqle);
				}
				_log.errorTrace(methodName, be);
			} catch (Exception e) {
				try {
					mcomCon.finalRollback();
				} catch (SQLException sqle) {
					_log.errorTrace(methodName, sqle);
				}
				_log.errorTrace(methodName, e);
				loggerValue.setLength(0);
				loggerValue.append( "Exception:");
				loggerValue.append( e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, 
						EventLevelI.FATAL, "C2SPrepaidController[processFromQueue]",_transferID, _senderMSISDN,
						_senderNetworkCode, loggerValue.toString());
			}finally{
				if(mcomCon != null )
					mcomCon.close("C2SPrepaidController#processFromQueue");
				mcomCon = null;
				con = null;
			}
			if (BTSLUtil.isNullString(_c2sTransferVO.getMessageCode())) {
				_c2sTransferVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
			}
			if (_receiverMessageSendReq && !BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(), _notAllowedRecSendMessGatw) && !"ALL"
					.equals(_notAllowedRecSendMessGatw)) {
				if (_c2sTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
					final BTSLMessages btslRecMessages = (BTSLMessages) _c2sTransferVO.getReceiverReturnMsg();
					(new PushMessage(_receiverMSISDN, BTSLUtil.getMessage(_receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), _transferID,
							_c2sTransferVO.getRequestGatewayCode(), _receiverLocale, _senderNetworkCode, null, _serviceType)).push();
				} else if (_c2sTransferVO.getReceiverReturnMsg() != null) {
					(new PushMessage(_receiverMSISDN, (String) _c2sTransferVO.getReceiverReturnMsg(), _transferID, _c2sTransferVO.getRequestGatewayCode(), _receiverLocale, _senderNetworkCode, null, _serviceType))
					.push();
				}
			}
			if(_ussdReceiverMessageSendReq && !BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(),_notAllowedRecSendMessGatw)&&!"ALL".equals(_notAllowedRecSendMessGatw))
			{
				//checking if receiver message is not null and receiver return message is key
				if(_c2sTransferVO.getReceiverReturnMsg()!=null&&((BTSLMessages)_c2sTransferVO.getReceiverReturnMsg()).isKey())
				{
					//generating message and pushing it to receiver
					BTSLMessages btslRecMessages=(BTSLMessages)_c2sTransferVO.getReceiverReturnMsg();
					(new USSDPushMessage(_receiverMSISDN,BTSLUtil.getMessage(_receiverLocale,btslRecMessages.getMessageKey(),btslRecMessages.getArgs()),_transferID,_c2sTransferVO.getRequestGatewayCode(),_receiverLocale)).push();
				}
				else if(_c2sTransferVO.getReceiverReturnMsg()!=null) //pushing message to receiver
					(new USSDPushMessage(_receiverMSISDN,(String)_c2sTransferVO.getReceiverReturnMsg(),_transferID,_c2sTransferVO.getRequestGatewayCode(),_receiverLocale)).push();
			}
			TransactionLog.log(_transferID, _requestVO.getRequestIDStr(), _requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Leaving the controller", PretupsI.TXN_LOG_STATUS_SUCCESS, "Getting Code=" + _requestVO.getMessageCode());
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exiting");
			}
		}
	}

	public void updateReceiverLocale(String p_languageCode) {
		if (_log.isDebugEnabled()) {
			_log.debug("updateReceiverLocale", "Entered p_languageCode=" + p_languageCode);
		}
		if (!BTSLUtil.isNullString(p_languageCode)) {
			try {
				if (LocaleMasterCache.getLocaleFromCodeDetails(p_languageCode) == null) {
					EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "C2SPrepaidController[updateReceiverLocale]",
							_transferID, _receiverMSISDN, "", "Exception: Notification language returned from IN is not defined in system p_languageCode: " + p_languageCode);
				} else {
					_receiverLocale = (LocaleMasterCache.getLocaleFromCodeDetails(p_languageCode));
				}
			} catch (Exception e) {
				_log.errorTrace("updateReceiverLocale", e);
			}
		}
		if (_log.isDebugEnabled()) {
			_log.debug("updateReceiverLocale", "Exited _receiverLocale=" + _receiverLocale);
		}
	}

	static synchronized void generateC2STransferID(TransferVO p_transferVO) {
		SimpleDateFormat _sdfCompare = new SimpleDateFormat("mm");
		String transferID = null;
		String minut2Compare = null;
		Date mydate = null;
		String redisEnable = BTSLUtil.NullToString(Constants.getProperty("REDIS_ENABLE"));
		final String methodName = "generateC2STransferID";
		try {
			mydate = new Date();
			p_transferVO.setCreatedOn(mydate);
			p_transferVO.setTransferDate(mydate);
			minut2Compare = _sdfCompare.format(mydate);
			final int currentMinut = Integer.parseInt(minut2Compare);
	    
			 if(!PretupsI.REDIS_ENABLE.equals(redisEnable)) {
					if (currentMinut != _prevMinut) {
						_transactionIDCounter = 1;
						_prevMinut = currentMinut;
					} else if (_transactionIDCounter >= 65535) {
						_transactionIDCounter = 1;
					} else {
						_transactionIDCounter++;
					}
					if (_transactionIDCounter == 0) {
						throw new BTSLBaseException("C2SPrepaidController", methodName, PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
					}
					transferID = _operatorUtil.formatC2STransferID(p_transferVO, _transactionIDCounter);
			 } else {
				 RedisUtil _redisUtil = new RedisUtil();
				 transferID = _redisUtil.formatC2STransferID(p_transferVO);
			 }
			
			if (transferID == null) {
				throw new BTSLBaseException("C2SPrepaidController", methodName, PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
			}
			p_transferVO.setTransferID(transferID);
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
	}

	private String getSenderParentSuccessMessage() {
		final String[] messageArgArray = { _requestVO.getSid(), _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getTransferValue()), PretupsBL
				.getDisplayAmount(_senderTransferItemVO.getPostBalance()), String.valueOf(_receiverTransferItemVO.getValidity()), PretupsBL
				.getDisplayAmount(_receiverTransferItemVO.getPostBalance()), String.valueOf(_receiverTransferItemVO.getNewGraceDate()), PretupsBL.getDisplayAmount(_c2sTransferVO
						.getReceiverAccessFee()), _c2sTransferVO.getSubService(), _requestVO.getSenderLoginID(),_c2sTransferVO.getProductName() };
		return BTSLUtil.getMessage(_senderLocale, PretupsErrorCodesI.C2S_PARENT_SUCCESS, messageArgArray);
	}

	public void copyRetailerVOtoOwnerVO(C2STransferVO p_ownerVO, C2STransferVO p_retailerVO) {
		p_ownerVO.setTransferValue(p_retailerVO.getTransferValue());
		p_ownerVO.setNetworkCode(p_retailerVO.getNetworkCode());
		p_ownerVO.setReceiverNetworkCode(p_retailerVO.getReceiverNetworkCode());
		p_ownerVO.setProductCode(p_retailerVO.getProductCode());
		p_ownerVO.setSourceType(p_retailerVO.getSourceType());
		p_ownerVO.setTransferDate(p_retailerVO.getTransferDate());
		p_ownerVO.setCreatedOn(p_retailerVO.getCreatedOn());
		p_ownerVO.setTransferID(p_retailerVO.getTransferID());
		p_ownerVO.setRequestedAmount(p_retailerVO.getRequestedAmount());
		p_ownerVO.setProductName(p_retailerVO.getProductName());
		p_ownerVO.setModule(p_retailerVO.getModule());
		p_ownerVO.setServiceType(p_retailerVO.getServiceType());
		p_ownerVO.setSubService(p_retailerVO.getSubService());
		p_ownerVO.setSenderMsisdn(p_ownerVO.getOwnerUserVO().getMsisdn());
		if((boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USERWISE_LOAN_ENABLE)){
			p_ownerVO.setUserLoanVOList(p_retailerVO.getUserLoanVOList());

		}
		else if((boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)){
			p_ownerVO.setChannelSoSVOList(p_retailerVO.getChannelSoSVOList());

		}
	}

	public void getRoamPenalty(Connection pCon) throws BTSLBaseException {
		final String methodName="getRoamPenalty";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName,"Entered");
		}
		final long roamDailyThreshold = ((Long) PreferenceCache.getControlPreference(PreferenceI.ROAM_RECHARGE_DAILY_THRESHOLD, _senderNetworkCode, _channelUserVO
				.getCategoryCode())).longValue();
		long differentialAmount = 0;
		if (roamDailyThreshold > 0) {
			final long roamDailyAmount = ChannelTransferBL.checkC2SRoamCount(pCon, _c2sTransferVO);

			if (roamDailyAmount > roamDailyThreshold) {
				differentialAmount = _c2sTransferVO.getRequestedAmount();
			} else {
				differentialAmount = roamDailyAmount + _c2sTransferVO.getRequestedAmount() - roamDailyThreshold;
			}
			_c2sTransferVO.setRoamDiffAmount(String.valueOf(differentialAmount));
			if (differentialAmount > 0) {
				stopAdditionalCommission = true;
				_c2sTransferVO.setStopAddnCommission(true);
				_penaltyVODebit = new AdjustmentsVO();
				_penaltyVOCredit = new AdjustmentsVO();
				_diffCalBL = new DiffCalBL();
				_diffCalBL.calculateRoamPenalty(_c2sTransferVO, _penaltyVODebit, _penaltyVOCredit, differentialAmount, false,((ChannelUserVO) _c2sTransferVO.getSenderVO()).getCategoryCode());
				if (_c2sTransferVO.getRoamPenalty() > 0) {
					penaltyApplicable = true;
				}
				ChannelUserVO ownerUserVO = null;
				if (!(_channelUserVO.getOwnerID().equalsIgnoreCase(_channelUserVO.getUserID()) || _channelUserVO.getParentID().equalsIgnoreCase(PretupsI.ROOT_PARENT_ID))) {
					final UserDAO userDAO = new UserDAO();
					ownerUserVO = userDAO.loadUserDetailsFormUserID(pCon, _channelUserVO.getOwnerID());
					_penaltyVODebitOwner = new AdjustmentsVO();
					_penaltyVOCreditOwner = new AdjustmentsVO();
					c2STransferOwnerVO = new C2STransferVO();
					c2STransferOwnerVO.setOwnerUserVO(ownerUserVO);
					copyRetailerVOtoOwnerVO(c2STransferOwnerVO, _c2sTransferVO);
					_diffCalBL.calculateRoamPenalty(c2STransferOwnerVO, _penaltyVODebitOwner, _penaltyVOCreditOwner, differentialAmount, true,((ChannelUserVO) _c2sTransferVO.getSenderVO()).getCategoryCode());
					_c2sTransferVO.setRoamPenaltyOwner(c2STransferOwnerVO.getRoamPenalty());
					_c2sTransferVO.setRoamPenaltyPercentageOwner(c2STransferOwnerVO.getRoamPenaltyPercentageOwner());
					_c2sTransferVO.setOwnerCommProfile(ownerUserVO.getCommissionProfileSetID());
					if (c2STransferOwnerVO.getRoamPenalty() > 0) {
						penaltyApplicableOwner = true;
						ChannelUserBL.validateOwnerAvailableControlsRoam(pCon, _transferID, c2STransferOwnerVO);
					}

				}
			}
		}
		if (_log.isDebugEnabled()) {
			_log.debug(methodName,"Exited");
		}
	}
}
