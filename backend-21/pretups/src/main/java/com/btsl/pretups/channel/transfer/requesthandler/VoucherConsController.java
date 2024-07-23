package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

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
import com.btsl.pretups.network.businesslogic.NetworkDAO;
import com.btsl.pretups.network.businesslogic.NetworkInterfaceModuleCache;
import com.btsl.pretups.network.businesslogic.NetworkInterfaceModuleVO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.p2p.subscriber.businesslogic.P2PSubscriberCounterVO;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberDAO;
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
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.voucher.businesslogic.VomsVoucherDAO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;
import com.txn.pretups.sos.businesslogic.SOSTxnDAO;
import com.txn.voms.voucher.businesslogic.VomsVoucherTxnDAO;

public class VoucherConsController implements ServiceKeywordControllerI, Runnable {
	
	private static Log _log = LogFactory.getLog(VoucherConsController.class.getName());
	private P2PTransferVO _p2pTransferVO = null;
	private TransferItemVO _senderTransferItemVO = null;
	private TransferItemVO _receiverTransferItemVO = null;
	private String _senderMSISDN;
	private String _receiverMSISDN;
	private SenderVO _senderVO;
	private ReceiverVO _receiverVO;
	private String _senderSubscriberType;
	private String _senderNetworkCode;
	private Date _currentDate = null;
	private ArrayList _itemList = null;
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
	private Locale _senderLocale = null;
	private Locale _receiverLocale = null;
	private boolean _isCounterDecreased = false;
	private String _type;
	private String _serviceType;
	private boolean _finalTransferStatusUpdate = true;
	private boolean _decreaseTransactionCounts = false;
	private boolean _transferDetailAdded = false;
	private boolean _senderInterfaceInfoInDBFound = false;
	private boolean _receiverInterfaceInfoInDBFound = false;
	private String _senderAllServiceClassID = PretupsI.ALL;
	private String _receiverAllServiceClassID = PretupsI.ALL;
	private String _receiverExternalID = null;
	private RequestVO _requestVO = null;
	private boolean _processedFromQueue = false; 
	private boolean _recValidationFailMessageRequired = false; 
	private boolean _recTopupFailMessageRequired = false;
	private ServiceInterfaceRoutingVO _serviceInterfaceRoutingVO = null;
	private boolean _useAlternateCategory = false; 
	private boolean _performIntfceCatRoutingBeforeVal = false; 
	private boolean _interfaceCatRoutingDone = false; 
	private String _oldInterfaceCategory = null; 
	private String _newInterfaceCategory = null; 
	private boolean _senderDeletionReqFromSubRouting = false; 
	private boolean _receiverDeletionReqFromSubRouting = false; 
	private final int SRC_BEFORE_INRESP_CAT_ROUTING = 1; 
	private final int SRC_AFTER_INRESP_CAT_ROUTING = 2; 
	private NetworkPrefixVO _networkPrefixVO = null;
	private String _oldDefaultSelector = null;
	private String _newDefaultSelector = null;
	private static OperatorUtilI _operatorUtil = null;
	private String _senderInterfaceStatusType = null;
	private String _receiverInterfaceStatusType = null;
	//commented and added by Ashish for VIL
	//private static int _transactionIDCounter = 0;
	private static AtomicInteger _transactionIDCounter=new AtomicInteger();
	//ended by Ashish for VIL
	private static int _prevMinut = 0;
	private static SimpleDateFormat _sdfCompare = new SimpleDateFormat("mm");
	private boolean _oneLog = true;
	private NetworkInterfaceModuleVO _networkInterfaceModuleVO = null;
	private VomsVoucherVO _vomsVO = null;
	private boolean _vomsInterfaceInfoInDBFound = false;
	private String _vomsExternalID = null;
	private SubscriberDAO subscriberDao = null;
	private boolean inValidPinAttempt = false;
	private boolean _voucherMarked = false;
	private boolean barredStatus = false;
	//added by Ashish for VIL
	private  String _receiverNtwrkCode=null;
	private   String _senderNtwrkCode;
	private   String _brand;
	protected String _notAllowedRecSendMessGatw;
	protected String _registrationSMSToBeSent;
	
	//ended by Ashish for VIL
	public VoucherConsController() {
		_p2pTransferVO = new P2PTransferVO();
		_vomsVO =new VomsVoucherVO();
		_currentDate = new Date();
		if ("Y".equals(BTSLUtil.NullToString(Constants.getProperty("P2P_REC_GEN_FAIL_MSG_REQD_V")))) {
			_recValidationFailMessageRequired = true;
		}
		if ("Y".equals(BTSLUtil.NullToString(Constants.getProperty("P2P_REC_GEN_FAIL_MSG_REQD_T")))) {
			_recTopupFailMessageRequired = true;
		}
		_notAllowedRecSendMessGatw = BTSLUtil.NullToString(Constants.getProperty("VMS_REC_MSG_NOT_REQD_GW"));
		_registrationSMSToBeSent = BTSLUtil.NullToString(Constants.getProperty("VMS_REG_MSG_NOT_TO_BE_SENT"));
	}

	static {
		final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
		try {
			_operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
		} catch (Exception e) {
			_log.errorTrace("VoucherConsController", e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherConsController[initialize]", "", "", "",
					"Exception while loading the class at the call:" + e.getMessage());
		}
	}

	public void process(RequestVO p_requestVO) {
		Connection con = null;MComConnectionI mcomCon = null;
		_requestIDStr = p_requestVO.getRequestIDStr();
		boolean receiverMessageSendReq = false;
		final String methodName = "process";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, _requestIDStr, "Entered");
		}
		try {
			_requestVO = p_requestVO;
			mcomCon = new MComConnection();con=mcomCon.getConnection();
			_senderVO = (SenderVO) p_requestVO.getSenderVO();
			if (_senderVO == null) {
				p_requestVO.setInfo10("VMS");
				new RegisterationController().regsiterNewUser(p_requestVO);
				_senderVO = (SenderVO) p_requestVO.getSenderVO();
				//added by Ashish for VIL
				_senderNtwrkCode = _senderVO.getNetworkCode();
				//ended by Ashish for VIL
				_senderVO.setDefUserRegistration(true);
				_senderVO.setStatus(PretupsI.USER_STATUS_ACTIVE);
				_senderVO.setActivateStatusReqd(true);
				p_requestVO.setSenderLocale(new Locale(_senderVO.getLanguage(), _senderVO.getCountry()));
				if (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CTRL_ALLOWED)) != null && ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CTRL_ALLOWED)).indexOf(p_requestVO.getRequestGatewayType()) != -1 && !PretupsI.NOT_APPLICABLE
						.equals(p_requestVO.getGroupType())) {
					final GroupTypeProfileVO groupTypeProfileVO = PretupsBL.loadAndCheckP2PGroupTypeCounters(p_requestVO, PretupsI.GRPT_TYPE_CONTROLLING);
					if (groupTypeProfileVO != null && groupTypeProfileVO.isGroupTypeCounterReach()) {
						p_requestVO.setDecreaseGroupTypeCounter(false);
						final String arr[] = { String.valueOf(groupTypeProfileVO.getThresholdValue()) };
						if (PretupsI.GRPT_TYPE_FREQUENCY_DAILY.equals(groupTypeProfileVO.getFrequency())) {
							throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.P2P_ERROR_GRPT_COUNTERS_REACH_LIMIT_D, arr);
						}
						throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.P2P_ERROR_GRPT_COUNTERS_REACH_LIMIT_M, arr);
					}
				}
				
			}
			if (_senderVO != null){
				
				subscriberDao = new SubscriberDAO();
				P2PSubscriberCounterVO p2PSubscriberCounterVO = new P2PSubscriberCounterVO();
				p2PSubscriberCounterVO = subscriberDao.loadP2PsubscriberCounterBasedOnService(con, _senderVO.getMsisdn(), _requestVO.getServiceType());
				if(p2PSubscriberCounterVO!=null)
				_p2pTransferVO.setInvalidPINcount(p2PSubscriberCounterVO.getInvalidVoucherPinCount());
				else
					_p2pTransferVO.setInvalidPINcount(0);	
				
			}
			

			_senderLocale = p_requestVO.getSenderLocale();
			_receiverLocale = p_requestVO.getReceiverLocale();

			if (_log.isDebugEnabled()) {
				_log.debug(methodName, _requestIDStr, "_senderLocale=" + _senderLocale + " _receiverLocale=" + _receiverLocale);
			}

			TransactionLog.log("", p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), _senderVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ,
					PretupsI.TXN_LOG_TXNSTAGE_RECIVED, "Received Request From Receiver", PretupsI.TXN_LOG_STATUS_SUCCESS, "");

			_type = p_requestVO.getType();

			_serviceType = p_requestVO.getServiceType();

			populateVOFromRequest(p_requestVO);

			
			_senderVO.setModifiedBy(_senderVO.getUserID());
			_senderVO.setModifiedOn(_currentDate);

			_operatorUtil.validateVoucherPin(con, p_requestVO, _p2pTransferVO);

			_receiverLocale = p_requestVO.getReceiverLocale();

			_receiverVO = (ReceiverVO) _p2pTransferVO.getReceiverVO();

			_receiverVO.setMsisdnPrefix(PretupsBL.getMSISDNPrefix(_receiverVO.getMsisdn()));

			_p2pTransferVO.setCellId(_requestVO.getCellId());
			_p2pTransferVO.setSwitchId(_requestVO.getSwitchId());

			final String[] ReqMessageArgArray = p_requestVO.getRequestMessageArray();
			_requestVO.setVoucherCode(ReqMessageArgArray[2]);
			if(ReqMessageArgArray.length == 4) {
				if(!BTSLUtil.isNullString(ReqMessageArgArray[3]) && ReqMessageArgArray[3].length() > 2)//sub-service can not be greater than 2
					_requestVO.setSerialnumber(ReqMessageArgArray[3]);
				else
					p_requestVO.setReqSelector(ReqMessageArgArray[3]);
			} else if(ReqMessageArgArray.length == 5) {
				_requestVO.setSerialnumber(ReqMessageArgArray[3]);
				p_requestVO.setReqSelector(ReqMessageArgArray[4]);
			}
			_p2pTransferVO.setVoucherCode(_requestVO.getVoucherCode());
			_p2pTransferVO.setSerialNumber(_requestVO.getSerialnumber());

			PretupsBL.getSelectorValueFromCode(p_requestVO);

			_serviceInterfaceRoutingVO = ServiceInterfaceRoutingCache
					.getServiceInterfaceRoutingDetails(_receiverVO.getNetworkCode() + "_" + p_requestVO.getServiceType() + "_" + _senderVO.getSubscriberType());
			if (_serviceInterfaceRoutingVO != null) {
				if (_log.isDebugEnabled()) {
					_log.debug(
							methodName,
							_requestIDStr,
							"For =" + _receiverVO.getNetworkCode() + "_" + p_requestVO.getServiceType() + " Got Interface Category=" + _serviceInterfaceRoutingVO
							.getInterfaceType() + " Alternate Check Required=" + _serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool() + " Alternate Interface=" + _serviceInterfaceRoutingVO
							.getAlternateInterfaceType() + " _oldDefaultSelector=" + _serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode() + "_newDefaultSelector= " + _serviceInterfaceRoutingVO
							.getAlternateDefaultSelectortCode());
				}
				if (BTSLUtil.isNullString(p_requestVO.getReqSelector())) {
					_oldDefaultSelector = _serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode();					
				} else {
					_oldDefaultSelector = p_requestVO.getReqSelector();
				}
			} else {
				final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(_p2pTransferVO.getServiceType());
				if (serviceSelectorMappingVO != null) {
					_oldDefaultSelector = serviceSelectorMappingVO.getSelectorCode();
				}
				_log.info(methodName, _requestIDStr, "Service Interface Routing control Not defined, thus using default Selector=" + _oldDefaultSelector);
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherConsController[process]", "", _senderMSISDN,
						_senderNetworkCode, "Service Interface Routing control Not defined, thus using default selector=" + _oldDefaultSelector);
			}
			//added by Ashish for VIL
			String msisdnPrefix=null;
			String receiverMSISDN = _receiverVO.getMsisdn();
			receiverMSISDN = PretupsBL.getFilteredMSISDN(receiverMSISDN);
	        String senderMSISDN = p_requestVO.getRequestMSISDN();
	        senderMSISDN = PretupsBL.getFilteredMSISDN(senderMSISDN);
			//end by Ashish for VIL
	        
			if (!_receiverVO.getSubscriberType().equals(_type)) {
				//added by Ashish for VIL
				if(!BTSLUtil.isNullString(receiverMSISDN) && receiverMSISDN.equalsIgnoreCase(senderMSISDN) && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SNDR_NETWORK_IDENTIFY_ON_IMSI_BASIS))).booleanValue()){
	            	String tmpmsisdnPrefix = new NetworkDAO().getSeriesBasedOnIMSI(p_requestVO.getImsi());
	            	msisdnPrefix = tmpmsisdnPrefix.substring(0,tmpmsisdnPrefix.indexOf("_"));
	            	_brand = tmpmsisdnPrefix.substring(tmpmsisdnPrefix.indexOf("_")+1);
	            }else{
	            	msisdnPrefix = _receiverVO.getMsisdnPrefix();
	            }
				
				//_networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(_receiverVO.getMsisdnPrefix(), _type);
				_networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix, _type);
				//end by Ashish for VIL
				if (_networkPrefixVO != null) {
					PretupsBL.checkNumberPortability(con, _requestIDStr, _receiverVO.getMsisdn(), _networkPrefixVO);
					_receiverVO.setNetworkCode(_networkPrefixVO.getNetworkCode());
					_receiverVO.setPrefixID(_networkPrefixVO.getPrefixID());
					_receiverVO.setSubscriberType(_networkPrefixVO.getSeriesType());
					_senderVO.setNetworkCode(_networkPrefixVO.getNetworkCode());
				} else if (_useAlternateCategory) {
					if (_log.isDebugEnabled()) {
						_log.debug(
								methodName,
								_requestIDStr,
								"Network Prefix Not Found For Series=" + _receiverVO.getMsisdnPrefix() + " and Type=" + _type + " and thus using Type as =" + _newInterfaceCategory + " _useAlternateCategory was true");
					}
					_useAlternateCategory = false;
					_type = _newInterfaceCategory;
					_oldDefaultSelector = _newDefaultSelector;
					_interfaceCatRoutingDone = true;
				} else {
					_log.error(this, "Series =" + _receiverVO.getMsisdnPrefix() + " Not Defined for Series type=" + _type);
					EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherConsController[process]", "", "", "",
							"Series =" + _receiverVO.getMsisdnPrefix() + " Not Defined for Series type=" + _type + " But alternate Category Routing was required on interface");
					throw new BTSLBaseException("", methodName, PretupsErrorCodesI.ERROR_NOTFOUND_SERIES_TYPE, 0, new String[] { _receiverVO.getMsisdn() }, null);
				}
			} else {
				//added by Ashish for VIL
				if(!BTSLUtil.isNullString(receiverMSISDN) && receiverMSISDN.equalsIgnoreCase(senderMSISDN) && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SNDR_NETWORK_IDENTIFY_ON_IMSI_BASIS))).booleanValue()){
	            	String tmpmsisdnPrefix = new NetworkDAO().getSeriesBasedOnIMSI(p_requestVO.getImsi());
	            	msisdnPrefix = tmpmsisdnPrefix.substring(0,tmpmsisdnPrefix.indexOf("_"));
	            	_brand = tmpmsisdnPrefix.substring(tmpmsisdnPrefix.indexOf("_")+1);
	            }else{
	            	msisdnPrefix = _receiverVO.getMsisdnPrefix();
	            }
				
				//_networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(_receiverVO.getMsisdnPrefix(), _type);
				_networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix, _type);
				//end by Ashish for VIL
				PretupsBL.checkNumberPortability(con, _requestIDStr, _receiverVO.getMsisdn(), _networkPrefixVO);
			}
			//added by Ashish for VIL
			_receiverNtwrkCode = _networkPrefixVO.getNetworkCode();
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ADD_INFO_REQUIRED_FOR_VOUCHER))).booleanValue()){
				_p2pTransferVO.setInfo4(p_requestVO.getImsi());
				_p2pTransferVO.setInfo5(_brand);
			}
			//ended by Ashish for VIL
			if (BTSLUtil.isNullString(p_requestVO.getReqSelector())) {
				if (_log.isDebugEnabled()) {
					_log.debug(methodName, _requestIDStr, "Selector Not found in Incoming Message Thus using Selector as  " + _oldDefaultSelector);
				}
				p_requestVO.setReqSelector(_oldDefaultSelector);
			} else {
				_newDefaultSelector = p_requestVO.getReqSelector();
			}

			if (_log.isDebugEnabled()) {
				_log.debug(methodName, _requestIDStr, "_receiverVO:" + _receiverVO);
			}

			_senderSubscriberType = _senderVO.getSubscriberType();
			_p2pTransferVO.setTransferCategory(_senderSubscriberType + "-" + _type);
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, _requestIDStr, "Starting with transfer Category as :" + _p2pTransferVO.getTransferCategory());
			}

			_senderNetworkCode = _senderVO.getNetworkCode();
			_senderMSISDN = ((SubscriberVO) _p2pTransferVO.getSenderVO()).getMsisdn();
			_receiverMSISDN = ((SubscriberVO) _p2pTransferVO.getReceiverVO()).getMsisdn();
			_receiverVO.setModule(_p2pTransferVO.getModule());
			_receiverVO.setCreatedDate(_currentDate);
			_receiverVO.setLastTransferOn(_currentDate);
			_p2pTransferVO.setReceiverMsisdn(_receiverMSISDN);
			_p2pTransferVO.setReceiverNetworkCode(_receiverVO.getNetworkCode());
			_p2pTransferVO.setSubService(p_requestVO.getReqSelector());
			_p2pTransferVO.setRequestStartTime(p_requestVO.getRequestStartTime());
			PretupsBL.validateNetworkService(_p2pTransferVO);
			
			try {
				PretupsBL.checkMSISDNBarred(con, _receiverVO.getMsisdn(), _receiverVO.getNetworkCode(), _p2pTransferVO.getModule(), PretupsI.USER_TYPE_RECEIVER);
				if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CHK_BLK_LST_STAT))).booleanValue()) {
					_operatorUtil.isRestrictedSubscriberAllowed(con, _receiverVO.getMsisdn(), _senderMSISDN);
				}

			} catch (BTSLBaseException be) {
				if (be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.ERROR_RECEIVER_USERBARRED))) {
					_p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.ERROR_USERBARRED_R, new String[] {}));
				}
				throw be;
			}

 			PretupsBL.loadRecieverControlLimits(con, p_requestVO.getRequestIDStr(), _p2pTransferVO);
			_receiverVO.setUnmarkRequestStatus(true);
			try {
				con.commit();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
				throw new BTSLBaseException("VoucherConsController", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			}

			if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SKEY_REQUIRED)).booleanValue() && _senderVO.getSkeyRequired().equals(PretupsI.YES)) {
				processSKeyGen(con);
			} else {
				processTransfer(con);
				p_requestVO.setTransactionID(_transferID);
				_receiverVO.setLastTransferID(_transferID);
				TransactionLog.log(_transferID, p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), _senderVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT,
						PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Generated Transfer ID", PretupsI.TXN_LOG_STATUS_SUCCESS,
						"Source Type=" + _p2pTransferVO.getSourceType() + " Gateway Code=" + _p2pTransferVO.getRequestGatewayCode());

				populateServicePaymentInterfaceDetails(con, PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION);

				_p2pTransferVO.setTransferCategory(_senderSubscriberType + "-" + _type);

				_p2pTransferVO.setSenderAllServiceClassID(_senderAllServiceClassID);
				_p2pTransferVO.setReceiverAllServiceClassID(_receiverAllServiceClassID);

				SubscriberBL.validateSenderLimits(con, _p2pTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL);
				
				_p2pTransferVO.setReceiverSubscriberType(_receiverTransferItemVO.getInterfaceType());

				PretupsBL.validateRecieverLimits(con, _p2pTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL, PretupsI.P2P_MODULE);

				try {
					con.commit();
				} catch (Exception e) {
					_log.errorTrace(methodName, e);
					throw new BTSLBaseException("VoucherConsController", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
				}
				if(mcomCon != null){mcomCon.close("VoucherConsController#process");mcomCon=null;}
				con = null;

				checkTransactionLoad();

				_decreaseTransactionCounts = true;
				
				//Checks If flow type is common then validation will be performed before sending the 
				//response to user and if it is thread based then validation will also be performed in thread 
				//along with topup
				if(p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON))
				{
					//Process validation requests
					processValidationRequest();
					p_requestVO.setSenderMessageRequired(_p2pTransferVO.isUnderProcessMsgReq());
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
					_oneLog = false;
					p_requestVO.setDecreaseLoadCounters(false);
				}
				else if(p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_REQUEST))
				{
					p_requestVO.setMessageCode(PretupsI.TXN_STATUS_UNDER_PROCESS);
					processValidationRequest();		
					processThread();
					String[] messageArgArray={_receiverMSISDN,_transferID,PretupsBL.getDisplayAmount(_p2pTransferVO.getTransferValue()), _p2pTransferVO.getVoucherCode(),_p2pTransferVO.getVoucherSerialNumber()};
					if (_p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
						p_requestVO.setMessageCode(PretupsErrorCodesI.VMS_SENDER_SUCCESS);
						final String[] messageArgArray1 = { PretupsBL.getDisplayAmount(_p2pTransferVO.getTransferValue()),_transferID,_receiverMSISDN, _p2pTransferVO.getVoucherCode() };
						p_requestVO.setMessageArguments(messageArgArray1);
						p_requestVO.setSenderReturnMessage(getSenderSuccessMessage());
					}
				}
				
					
				
				

				/*p_requestVO.setMessageCode(PretupsI.TXN_STATUS_UNDER_PROCESS);
				processValidationRequest();
				_oneLog = false;
				processThread();
				final String[] messageArgArray = { _receiverMSISDN, _transferID,PretupsBL.getDisplayAmount(_p2pTransferVO.getTransferValue()), _p2pTransferVO.getVoucherCode() };
				p_requestVO.setMessageArguments(messageArgArray);
				*/
				
				p_requestVO.setDecreaseLoadCounters(false);
			}
		} catch (BTSLBaseException be) {
			receiverMessageSendReq = true;
			_log.errorTrace(methodName, be);
			_log.error(methodName, "Exception be:" + be.getMessage());
			_requestVO.setSuccessTxn(false);
			if (_senderVO != null) {
				try {
					if (mcomCon == null) {
						mcomCon = new MComConnection();con=mcomCon.getConnection();
					}
					
					if(inValidPinAttempt){
						populateServicePaymentInterfaceDetails(con, PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION);
						
						
						if (_receiverVO != null && _receiverVO.isUnmarkRequestStatus()) {
							_receiverVO = (ReceiverVO)_p2pTransferVO.getReceiverVO();
							PretupsBL.unmarkReceiverLastRequest(con, _transferID, _receiverVO);
						}
						if(BTSLUtil.isNullString(Constants.getProperty("INVALID_PIN_NOTIY_TO_IN")) || PretupsI.YES.equalsIgnoreCase(Constants.getProperty("INVALID_PIN_NOTIY_TO_IN").trim())){
							sendNotification();
						}
						SubscriberBL.barSenderMSISDNforInvalidVoucherPin(con,_p2pTransferVO,_senderVO);
					}
					SubscriberBL.updateSubscriberLastDetails(con, _p2pTransferVO, _senderVO, _currentDate, PretupsErrorCodesI.TXN_STATUS_FAIL);
					if(_voucherMarked){
					_p2pTransferVO.setModifiedOn(_currentDate);
					_p2pTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
					_p2pTransferVO.setTransferStatus(PretupsI.TXN_STATUS_FAIL);
					
					final VomsVoucherDAO vomsVoucherDAO = new VomsVoucherDAO();
                 final VomsVoucherVO vomsVoucherVO = vomsVoucherDAO.loadVomsVoucherVOCons(con, _p2pTransferVO);
                  int updateCount = vomsVoucherDAO.updateVoucherConsumptionStatus(con,  _p2pTransferVO, vomsVoucherVO);
                 if (updateCount <= 0) {
                 	_log.error(methodName, _transferID, "Exception while updating Voucher Status in database:");
     				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherConsController[run]", _transferID,
     						_senderMSISDN, _senderNetworkCode, "Exception while updating Voucher Status in database");
     				throw new BTSLBaseException("VoucherConsController", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
                 } 
				}
				} catch (BTSLBaseException bex) {
					_log.errorTrace(methodName, bex);
					EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherConsController[process]", _transferID,
							_senderMSISDN, _senderNetworkCode, "Base Exception while updating Subscriber Last Details:" + bex.getMessage());
				} catch (Exception e) {
					_log.errorTrace(methodName, e);
					EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherConsController[process]", _transferID,
							_senderMSISDN, _senderNetworkCode, "Exception while updating Subscriber Last Details:" + e.getMessage());
					_p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
				}

				try {
					if (_receiverVO != null && _receiverVO.isUnmarkRequestStatus()) {
						PretupsBL.unmarkReceiverLastRequest(con, p_requestVO.getRequestIDStr(), _receiverVO);
					}
				} catch (BTSLBaseException bex) {
					_log.errorTrace(methodName, bex);
					EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherConsController[process]", _transferID,
							_senderMSISDN, _senderNetworkCode, "Leaving Reciever Unmarked Base Exception:" + bex.getMessage());
				} catch (Exception e) {
					_log.errorTrace(methodName, e);
					EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherConsController[process]", _transferID,
							_senderMSISDN, _senderNetworkCode, "Leaving Reciever Unmarked Exception:" + e.getMessage());
					_p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
				}
			}

			_p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			if (_recValidationFailMessageRequired) {
				if (_p2pTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _p2pTransferVO.getReceiverReturnMsg()).isKey()) {
					if (_transferID != null) {
						_p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.P2P_RECEIVER_FAIL, new String[] { String.valueOf(_transferID), PretupsBL
								.getDisplayAmount(_p2pTransferVO.getRequestedAmount()) }));
					} else {
						_p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.P2P_FAIL_R, new String[] { PretupsBL.getDisplayAmount(_p2pTransferVO
								.getRequestedAmount()) }));
					}
				}
			}

			if (!BTSLUtil.isNullString(_p2pTransferVO.getSenderReturnMessage())) {
				p_requestVO.setSenderReturnMessage(_p2pTransferVO.getSenderReturnMessage());
			}

			if (be.isKey()) {
				if (BTSLUtil.isNullString(_p2pTransferVO.getErrorCode())) {
					_p2pTransferVO.setErrorCode(be.getMessageKey());
				}
				p_requestVO.setMessageCode(be.getMessageKey());
				p_requestVO.setMessageArguments(be.getArgs());
			} else {
				_p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			}
			if (_transferID != null && _decreaseTransactionCounts) {
				LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
				_isCounterDecreased = true;
			}
			TransactionLog.log(_transferID, _requestIDStr, p_requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
					_p2pTransferVO.getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + p_requestVO.getMessageCode());
			P2PRequestDailyLog.log(P2PRequestDailyLog.populateP2PRequestDailyLogVO(_requestVO, _p2pTransferVO));
		} catch (Exception e) {
			receiverMessageSendReq = true;
			_log.error(methodName, "Exception e:" + e.getMessage());
			_log.errorTrace(methodName, e);
			_requestVO.setSuccessTxn(false);
			try {
				if (mcomCon == null) {
					mcomCon = new MComConnection();con=mcomCon.getConnection();
				}
				SubscriberBL.updateSubscriberLastDetails(con, _p2pTransferVO, _senderVO, _currentDate, PretupsErrorCodesI.TXN_STATUS_FAIL);
				
			} catch (BTSLBaseException bex) {
				_log.errorTrace(methodName, bex);
				_p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			} catch (Exception ex) {
				_log.errorTrace(methodName, ex);
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherConsController[process]", _transferID,
						_senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
				_p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			}

			try {
				if (_receiverVO != null && _receiverVO.isUnmarkRequestStatus()) {
					PretupsBL.unmarkReceiverLastRequest(con, p_requestVO.getRequestIDStr(), _receiverVO);
				}
			} catch (BTSLBaseException bex) {
				_log.errorTrace(methodName, bex);
				_p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			} catch (Exception ex1) {
				_log.errorTrace(methodName, ex1);
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherConsController[process]", _transferID,
						_senderMSISDN, _senderNetworkCode, "Exception:" + ex1.getMessage());
				_p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			}

			if (_recValidationFailMessageRequired) {
				if (_p2pTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _p2pTransferVO.getReceiverReturnMsg()).isKey()) {
					if (_transferID != null) {
						_p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.P2P_RECEIVER_FAIL, new String[] { String.valueOf(_transferID), PretupsBL
								.getDisplayAmount(_p2pTransferVO.getRequestedAmount()) }));
					} else {
						_p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.P2P_FAIL_R, new String[] { PretupsBL.getDisplayAmount(_p2pTransferVO
								.getRequestedAmount()) }));
					}
				}
			}

			_p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			_p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			_log.error(methodName, "Exception:" + e.getMessage());
			_log.errorTrace(methodName, e);
			if (_transferID != null && _decreaseTransactionCounts) {
				_isCounterDecreased = true;
				LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
			}
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherConsController[process]", _transferID,
					_senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
			TransactionLog.log(_transferID, _requestIDStr, p_requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
					_p2pTransferVO.getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + p_requestVO.getMessageCode());
			P2PRequestDailyLog.log(P2PRequestDailyLog.populateP2PRequestDailyLogVO(_requestVO, _p2pTransferVO));
		} finally {
			try {
				if (mcomCon == null) {
					mcomCon = new MComConnection();con=mcomCon.getConnection();
				}
				
				/*changes done to make entry in DB in case of fail transactions when pin invalid starts*/
				if (_transferID != null  && !p_requestVO.getMessageCode().equals(PretupsI.TXN_STATUS_UNDER_PROCESS))
				{
					if (!_transferDetailAdded) {
						addEntryInTransfers(con);
					}
				} else if (_transferID != null && p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD)) {
					_log.info(methodName, _requestIDStr,"Send the message to MSISDN=" + p_requestVO.getFilteredMSISDN() + " Transfer ID=" + _transferID + " But not added entry in Transfers yet");
				}
				/*changes done to make entry in DB in case of fail transactions Ends*/
			} catch (BTSLBaseException be) {
				_log.errorTrace(methodName, be);
				_log.error(methodName, "BTSL Base Exception:" + be.getMessage());
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
				_log.error(methodName, "Exception:" + e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherConsController[process]", _transferID,
						_senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
			}
			if (BTSLUtil.isNullString(p_requestVO.getMessageCode())) {
				p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
			}
			if (_isCounterDecreased) {
				p_requestVO.setDecreaseLoadCounters(false);
			}
			if (con != null) {
				try {
					con.commit();
				} catch (Exception e) {
					_log.errorTrace(methodName, e);
				}
				if(mcomCon != null){mcomCon.close("VoucherConsController#process");mcomCon=null;}
				con = null;
			}
			
			if (_receiverTransferItemVO != null && receiverMessageSendReq) {
				if (_p2pTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) _p2pTransferVO.getReceiverReturnMsg()).isKey()) {
					if (InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED_P2P.equals(_receiverTransferItemVO.getValidationStatus())) {
						_p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED_P2P + "_R"));
					}
					final BTSLMessages btslRecMessages = (BTSLMessages) _p2pTransferVO.getReceiverReturnMsg();
					(new PushMessage(_receiverMSISDN, BTSLUtil.getMessage(_receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), _transferID,
							_p2pTransferVO.getRequestGatewayCode(), _receiverLocale)).push();
				} else if (_p2pTransferVO.getReceiverReturnMsg() != null) {
					(new PushMessage(_receiverMSISDN, (String) _p2pTransferVO.getReceiverReturnMsg(), _transferID, _p2pTransferVO.getRequestGatewayCode(), _receiverLocale))
					.push();
				}
			}
			 if (!BTSLUtil.isNullString(_registrationSMSToBeSent) && _registrationSMSToBeSent.equalsIgnoreCase(PretupsI.YES) &&  _senderVO!=null && _senderVO.isActivateStatusReqd()) {
                 (new PushMessage(_senderMSISDN, getSenderRegistrationMessage(), _transferID, _p2pTransferVO.getRequestGatewayCode(), _senderLocale)).push();
            }
			 if(barredStatus){
				 if(!_p2pTransferVO.getSenderMsisdn().equals(_p2pTransferVO.getReceiverMsisdn())){
					 (new PushMessage(_senderMSISDN, getBarredMessageforSender(), null, _p2pTransferVO.getRequestGatewayCode(), _receiverLocale)).push(); 
				 }
				 (new PushMessage(_receiverMSISDN,getBarredMessageforReceiver(), null, _p2pTransferVO.getRequestGatewayCode(), _receiverLocale)).push();
				 
			 }
			if (_oneLog) {
				OneLineTXNLog.log(_p2pTransferVO,_senderTransferItemVO,_receiverTransferItemVO,_vomsVO);
			}
			TransactionLog.log(_transferID, p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES,
					PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Leaving the controller", PretupsI.TXN_LOG_STATUS_SUCCESS, "Getting Code=" + p_requestVO.getMessageCode());
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exiting");
			}
		}
	}

	/**
	 * This method process the S Key based transactions
	 * 
	 * @param p_con
	 * @throws BTSLBaseException
	 * @throws Exception
	 */
	private void processSKeyGen(Connection p_con) throws BTSLBaseException, Exception {
		final String methodName = "processSKeyGen";
		if (_log.isDebugEnabled()) {
			_log.debug("processSKeyGen", "Entered");
		}
		try {
			PretupsBL.generateSKey(p_con, _p2pTransferVO);
		} catch (BTSLBaseException be) {
			throw be;
		} catch (Exception e) {
			_log.error(methodName, "Exception e:" + e.getMessage());
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherConsController[processSKeyGen]", _transferID,
					_senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
			throw new BTSLBaseException("VoucherConsController", "processSKeyGen", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
		} finally {
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exiting");
			}
		}
	}

	/**
	 * Method to perform validation request
	 * 
	 * @throws BTSLBaseException
	 * @throws Exception
	 */
	private void processValidationRequest() throws BTSLBaseException, Exception {
		Connection con = null;MComConnectionI mcomCon = null;
		final String methodName = "processValidationRequest";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered and performing validations for transfer ID=" + _transferID + " " + _p2pTransferVO.getModule() + " " + _p2pTransferVO
					.getReceiverNetworkCode() + " " + _type);
		}

		try {
			_itemList = new ArrayList();
			_itemList.add(_senderTransferItemVO);
			_itemList.add(_receiverTransferItemVO);
			_p2pTransferVO.setTransferItemList(_itemList);
			_networkInterfaceModuleVO = (NetworkInterfaceModuleVO) NetworkInterfaceModuleCache.getObject(_p2pTransferVO.getModule(), _p2pTransferVO.getReceiverNetworkCode(),
					_type);

			_intModCommunicationTypeR = _networkInterfaceModuleVO.getCommunicationType();
			_intModIPR = _networkInterfaceModuleVO.getIP();
			_intModPortR = _networkInterfaceModuleVO.getPort();
			_intModClassNameR = _networkInterfaceModuleVO.getClassName();

			final String requestStr = getReceiverValidateStr();

			LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_VAL);
			try {
			final CommonClient commonClient = new CommonClient();

			TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL,
					requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "");
			final String receiverValResponse = commonClient.process(requestStr, _transferID, _networkInterfaceModuleVO.getCommunicationType(), _networkInterfaceModuleVO
					.getIP(), _networkInterfaceModuleVO.getPort(), _networkInterfaceModuleVO.getClassName());
			
			TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL,
					receiverValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

			if (_log.isDebugEnabled()) {
				_log.debug(methodName, _transferID, "Got the validation response from VOMS Handler receiverValResponse=" + receiverValResponse);
			}
			
			/////  by anubhav start
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			try{
			updateForVOMSValidationResponse(receiverValResponse);
			}
			catch(BTSLBaseException be)
			{
				LoadController.decreaseResponseCounters(_transferID, _receiverVO.getTransactionStatus(),LoadControllerI.SENDER_VAL_RESPONSE);
				if(_log.isDebugEnabled()) {
					_log.debug(methodName,"inside catch of BTSL Base Exception: "+be.getMessage()+" vomsInterfaceInfoInDBFound: "+_vomsInterfaceInfoInDBFound);
				}
				if (_vomsInterfaceInfoInDBFound && _senderTransferItemVO.getValidationStatus().equals(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND)) {
					PretupsBL.deleteSubscriberInterfaceRouting(_receiverMSISDN, PretupsI.INTERFACE_CATEGORY_VOMS);
				}
				PretupsBL.validateRecieverLimits(null,_p2pTransferVO,PretupsI.TRANS_STAGE_AFTER_INVAL,PretupsI.P2P_MODULE);
			throw be;
			}
			catch(Exception e)
			{
				LoadController.decreaseResponseCounters(_transferID, _receiverVO.getTransactionStatus(),LoadControllerI.SENDER_VAL_RESPONSE);
				PretupsBL.validateRecieverLimits(null,_p2pTransferVO,PretupsI.TRANS_STAGE_AFTER_INVAL,PretupsI.P2P_MODULE);
				throw new BTSLBaseException(this, methodName, "");
			}
			
			
			String pDecryptedMessage = null;
			if(_p2pTransferVO.getSerialNumber() != null)
			pDecryptedMessage = VOMSI.SERVICE_TYPE_VOUCHER_CON + " " + _p2pTransferVO.getVoucherCode() + " " +_receiverMSISDN + " " + _p2pTransferVO.getSerialNumber();
			else
		    pDecryptedMessage = VOMSI.SERVICE_TYPE_VOUCHER_CON + " " + _p2pTransferVO.getVoucherCode() + " " +_receiverMSISDN;
			final VomsVoucherTxnDAO vomsVoucherTxnDAO = new VomsVoucherTxnDAO();
            HashMap responseMap = vomsVoucherTxnDAO.loadData(con, pDecryptedMessage, null, VOMSI.SERVICE_TYPE_VOUCHER_CON, _p2pTransferVO.getReceiverNetworkCode());
           
            validateVoucherResponse(responseMap);
            //_voucherMarked = true;
    		updateForVomsEnquiryVO(responseMap);
			
    		//added by Ashish for VIL
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ADD_INFO_REQUIRED_FOR_VOUCHER))).booleanValue() && _p2pTransferVO!=null && _vomsVO != null){
				_p2pTransferVO.setInfo1(_vomsVO.getVoucherSegment());
				_p2pTransferVO.setInfo2(_vomsVO.getProductName());
				_p2pTransferVO.setInfo3(_vomsVO.getVoucherType());
			}			
			//ended by Ashish for VIL
			_itemList = new ArrayList();
			_itemList.add(_senderTransferItemVO);
			_itemList.add(_receiverTransferItemVO);
			_p2pTransferVO.setTransferItemList(_itemList);

			
				
			} catch (BTSLBaseException be) {
				LoadController.decreaseResponseCounters(_transferID, _receiverVO.getTransactionStatus(), LoadControllerI.SENDER_VAL_RESPONSE);
				if (_log.isDebugEnabled()) {
					_log.debug(methodName, "inside catch of BTSL Base Exception: " + be.getMessage() + " _vomsInterfaceInfoInDBFound: " + _vomsInterfaceInfoInDBFound);
				}
				if (_vomsInterfaceInfoInDBFound && _senderTransferItemVO.getValidationStatus().equals(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND)) {
					PretupsBL.deleteSubscriberInterfaceRouting(_receiverMSISDN, PretupsI.INTERFACE_CATEGORY_VOMS);
				}
				PretupsBL.validateRecieverLimits(null, _p2pTransferVO, PretupsI.TRANS_STAGE_AFTER_INVAL, PretupsI.P2P_MODULE);
				throw be;
			} catch (Exception e) {
				LoadController.decreaseResponseCounters(_transferID, _receiverVO.getTransactionStatus(), LoadControllerI.SENDER_VAL_RESPONSE);
				PretupsBL.validateRecieverLimits(null, _p2pTransferVO, PretupsI.TRANS_STAGE_AFTER_INVAL, PretupsI.P2P_MODULE);
				throw new BTSLBaseException(this, methodName, "");
			}

			LoadController.decreaseResponseCounters(_transferID, _receiverVO.getTransactionStatus(), LoadControllerI.SENDER_VAL_RESPONSE);

			InterfaceVO vomsInterfaceVO = (InterfaceVO) NetworkInterfaceModuleCache.getObject(_receiverTransferItemVO.getInterfaceID());
			if ((System.currentTimeMillis() - _p2pTransferVO.getRequestStartTime()) > vomsInterfaceVO.getValExpiryTime()) {
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "EVDController[processValidationRequest]",
						_transferID, _senderMSISDN, _senderNetworkCode, "Exception: System is taking more time till validation of voucher");
				throw new BTSLBaseException("EVDController", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_TKING_TIME_TILL_VAL);
			}
			vomsInterfaceVO = null;
			
			if (_vomsVO != null) {
				calulateTransferValue(_p2pTransferVO, _vomsVO);
			}
			// validate sender receiver service class,validate transfer value
			PretupsBL.validateServiceClassChecks(con, _senderTransferItemVO, _p2pTransferVO, PretupsI.P2P_MODULE, _requestVO.getServiceType());
            _senderVO.setServiceClassCode(_senderTransferItemVO.getServiceClass());
            _senderVO.setUsingAllServiceClass(_senderTransferItemVO.isUsingAllServiceClass());
            
			
            PretupsBL.validateServiceClassChecks(con, _receiverTransferItemVO, _p2pTransferVO, PretupsI.P2P_MODULE, _requestVO.getServiceType());
            _receiverVO.setServiceClassCode(_receiverTransferItemVO.getServiceClass());
            _receiverVO.setUsingAllServiceClass(_receiverTransferItemVO.isUsingAllServiceClass());
            try{
            	PretupsBL.validateTransferRule(con, _p2pTransferVO, PretupsI.P2P_MODULE);
				if (_log.isDebugEnabled()) {
        			_log.debug(methodName, "VomVO Network Code = "+_vomsVO.getUserNetworkCode());
        		}
            	if(!BTSLUtil.isNullString(_vomsVO.getUserNetworkCode()))
            	{
            		_p2pTransferVO.setNetworkCode(_vomsVO.getUserNetworkCode());
            	}
	            CardGroupBL.calculateCardGroupDetails(con, _p2pTransferVO, PretupsI.P2P_MODULE, true);
	             _p2pTransferVO.setSenderAccessFee(0);
	             _p2pTransferVO.setSenderTax1Value(0);
	             _p2pTransferVO.setSenderTax2Value(0);
	             _p2pTransferVO.setSenderTransferValue(0);
	             final TransferItemVO senderTransferItemVO = (TransferItemVO) _p2pTransferVO.getTransferItemList().get(0);
	             senderTransferItemVO.setTransferValue(0);
	 			 senderTransferItemVO.setPostBalance(0);
	             _p2pTransferVO.setTransferValue(_vomsVO.getTalkTime());
	             final TransferItemVO receiverTransferItemVO = (TransferItemVO) _p2pTransferVO.getTransferItemList().get(1);
	             receiverTransferItemVO.setRequestValue(_vomsVO.getTalkTime());
	             TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
	 					"Validity=" + _p2pTransferVO.getReceiverValidity() + " Talk Time=" + _p2pTransferVO.getReceiverTransferValue(), PretupsI.TXN_LOG_STATUS_SUCCESS, "");
            } catch(BTSLBaseException be){
				_voucherMarked = true;
                 throw be;
			}
            
			try{
			PretupsBL.validateRecieverLimits(null, _p2pTransferVO, PretupsI.TRANS_STAGE_AFTER_INVAL, PretupsI.P2P_MODULE);
			}
			catch(BTSLBaseException be){
				
				_voucherMarked = true;
                 throw be;
				
				
			}
			_p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
			_senderTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
			
			
			
			populateServicePaymentInterfaceDetails(con, PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);

			if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue()) {
				PretupsBL.addTransferDetails(con, _p2pTransferVO);
			}
			_transferDetailAdded = true;
			
			try {
				con.commit();
			} catch (Exception be) {
				_log.errorTrace(methodName, be);
			}
			if(mcomCon != null){mcomCon.close("VoucherConsController#processValidationRequest");mcomCon=null;}
			con = null;

			TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
					"Marked Under process,", PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "");

			if (_p2pTransferVO.getMsgGatewayFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON) || _processedFromQueue) {
				final Thread _controllerThread = new Thread(this);
				_controllerThread.start();
				_oneLog = false;
			}
		} catch (BTSLBaseException be) {
			_requestVO.setSuccessTxn(false);
			if (con != null) {
				con.rollback();
			}

			if (BTSLUtil.isNullString(_p2pTransferVO.getErrorCode())) {
				if (be.isKey()) {
					_p2pTransferVO.setErrorCode(be.getMessageKey());
				} else {
					_p2pTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
				}
			}
			if (_recValidationFailMessageRequired) {
				if (_p2pTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _p2pTransferVO.getReceiverReturnMsg()).isKey()) {
					if(!BTSLUtil.isNullString(_p2pTransferVO.getErrorCode())){
						_p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(_p2pTransferVO.getErrorCode(), new String[] { String.valueOf(_transferID), PretupsBL
								.getDisplayAmount(_p2pTransferVO.getRequestedAmount()) }));
					}else{
						_p2pTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.C2S_RECEIVER_FAIL), new String[] { String.valueOf(_transferID), PretupsBL
								.getDisplayAmount(_p2pTransferVO.getRequestedAmount()) }));
					}
				}
			}
			_p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			_receiverTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			if (BTSLUtil.isNullString(_p2pTransferVO.getErrorCode())) {
				if (be.isKey()) {
					_p2pTransferVO.setErrorCode(be.getMessageKey());
				} else {
					_p2pTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
				}
			}
			_log.error("VoucherConsController[processValidationRequest]", "Getting BTSL Base Exception:" + be.getMessage());
			throw be;
		} catch (Exception e) {
			_requestVO.setSuccessTxn(false);
			_log.errorTrace(methodName, e);
			if (con != null) {
				con.rollback();
			}
			_p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			if (_recValidationFailMessageRequired) {
				if (_p2pTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _p2pTransferVO.getReceiverReturnMsg()).isKey()) {
					_p2pTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.C2S_RECEIVER_FAIL_EVD), new String[] { String.valueOf(_transferID), PretupsBL
							.getDisplayAmount(_p2pTransferVO.getRequestedAmount()) }));
				}
			}
			if (BTSLUtil.isNullString(_p2pTransferVO.getErrorCode())) {
				_p2pTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			}

			throw new BTSLBaseException("VoucherConsController", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
		} finally {
			if(mcomCon != null){mcomCon.close("VoucherConsController#processValidationRequest");mcomCon=null;}
			con = null;
		}
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Exiting");
		}
	}

	

	/**
	 * Method :calulateTransferValue
	 * This method will set the values from the VOMS VO into intemsVO and
	 * transferVO
	 * 
	 * @param p_transferVO
	 * @param p_vomsVO
	 * @throws BTSLBaseException 
	 */
	public void calulateTransferValue(P2PTransferVO p_transferVO, VomsVoucherVO p_vomsVO) throws BTSLBaseException {
		p_transferVO.setSenderTransferValue(p_transferVO.getTransferValue());
		final TransferItemVO transferItemVO = (TransferItemVO) p_transferVO.getTransferItemList().get(1);
		final TransferItemVO senderTransferItemVO = (TransferItemVO) p_transferVO.getTransferItemList().get(0);
		p_transferVO.setReceiverGracePeriod(p_vomsVO.getGracePeriod());
		p_transferVO.setReceiverValidity(p_vomsVO.getValidity());
		p_transferVO.setReceiverTransferValue(p_vomsVO.getTalkTime());
		transferItemVO.setTransferValue(p_vomsVO.getTalkTime());
		transferItemVO.setGraceDaysStr(String.valueOf(p_vomsVO.getGracePeriod()));
		transferItemVO.setValidity(p_vomsVO.getValidity());
		senderTransferItemVO.setTransferValue(0);
		senderTransferItemVO.setPostBalance(0);
		p_transferVO.setRequestedAmount(p_vomsVO.getTalkTime());//added for pass value in card group
		p_transferVO.setQuantity(p_vomsVO.getTalkTime());
	}

	/**
	 * Process Transfer Request , Genaerates the Transfer ID and populates the
	 * Transfer Items VO
	 * 
	 * @param p_con
	 * @throws BTSLBaseException
	 */
	public void processTransfer(Connection p_con) throws BTSLBaseException {
		if (_log.isDebugEnabled()) {
			_log.debug("processTransfer", _p2pTransferVO.getRequestID(), "Entered");
		}
		try {
			_p2pTransferVO.setTransferDate(_currentDate);
			_p2pTransferVO.setTransferDateTime(_currentDate);
			generateTransferID(_p2pTransferVO);
			_transferID = _p2pTransferVO.getTransferID();
			setSenderTransferItemVO();
			// set receiver transfer item details
			setReceiverTransferItemVO();
			PretupsBL.getProductFromServiceType(p_con, _p2pTransferVO, _serviceType, PretupsI.P2P_MODULE);

		} catch (BTSLBaseException be) {
			_p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			if (be.isKey()) {
				_p2pTransferVO.setErrorCode(be.getMessageKey());
			} else {
				_p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			}
			throw be;
		} catch (Exception e) {
			if (_recValidationFailMessageRequired) {
				if (_p2pTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _p2pTransferVO.getReceiverReturnMsg()).isKey()) {
					if (_transferID != null) {
						_p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.P2P_RECEIVER_FAIL, new String[] { String.valueOf(_transferID), PretupsBL
								.getDisplayAmount(_p2pTransferVO.getRequestedAmount()) }));
					} else {
						_p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.P2P_FAIL_R, new String[] { PretupsBL.getDisplayAmount(_p2pTransferVO
								.getRequestedAmount()) }));
					}
				}
			}
			_p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			_p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			final String methodName = "processValidationRequest";
			_log.errorTrace(methodName, e);

			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherConsController[processTransfer]", _transferID,
					_senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
			throw new BTSLBaseException("VoucherConsController", "processTransfer", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
		}
	}

	public void run() {
		processThread();
	}

	/**
	 * This method will perform either topup in thread or both validation and
	 * topup on thread based on Flow Type
	 */
	public void processThread() {
		final String methodName = "processThread";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, _transferID, "Entered");
		}
		BTSLMessages btslMessages = null;
		Connection con = null;MComConnectionI mcomCon = null;
		final boolean onlyDecreaseCounters = false;
		int updateCount = 0;
		try {
			// Perform the validation of parties if Flow type is thread

			if (_p2pTransferVO.getMsgGatewayFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD) && !_processedFromQueue) {
				processValidationRequestInThread();
			}

			// send validation request for sender
			final CommonClient commonClient = new CommonClient();
			LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_TOP);

			final String receiverStr = getReceiverCreditStr();

			// send validation request for receiver
			TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INTOP,
					receiverStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

			final String receiverCreditResponse = commonClient.process(receiverStr, _transferID, _intModCommunicationTypeR, _intModIPR, _intModPortR, _intModClassNameR);

			TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INTOP,
					receiverCreditResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

			if (_log.isDebugEnabled()) {
				_log.debug(methodName, _transferID, "receiverCreditResponse From IN Module=" + receiverCreditResponse);
			}

			try {
				updateForReceiverCreditResponse(receiverCreditResponse);
			} catch (BTSLBaseException be) {
				TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
						"Transaction Failed", PretupsI.TXN_LOG_STATUS_FAIL, "Transfer Status=" + _p2pTransferVO.getTransferStatus() + " Getting Code=" + _receiverVO
						.getInterfaceResponseCode());
				LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.RECEIVER_TOP_RESPONSE);

				if (_p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS) && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_SNDR_CREDIT_BK_AMB_STATUS))).booleanValue() || _p2pTransferVO
						.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
					creditBackSenderForFailedTrans();
				}

				PretupsBL.validateRecieverLimits(null, _p2pTransferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.P2P_MODULE);

				throw be;
			} catch (Exception e) {
				TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
						"Transaction Failed", PretupsI.TXN_LOG_STATUS_FAIL, "Transfer Status=" + _p2pTransferVO.getTransferStatus() + " Getting exception=" + e.getMessage());

				LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.RECEIVER_TOP_RESPONSE);
				if (_p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS) && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_SNDR_CREDIT_BK_AMB_STATUS))).booleanValue() || _p2pTransferVO
						.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
					creditBackSenderForFailedTrans();
				}

				PretupsBL.validateRecieverLimits(null, _p2pTransferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.P2P_MODULE);
				throw (BTSLBaseException)e;
			}

			LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.RECEIVER_TOP_RESPONSE);

			_senderVO.setTotalConsecutiveFailCount(0);
			_senderVO.setTotalTransfers(_senderVO.getTotalTransfers() + 1);
			_senderVO.setTotalTransferAmount(_senderVO.getTotalTransferAmount() + _senderTransferItemVO.getRequestValue());
			_senderVO.setLastSuccessTransferDate(_currentDate);
			_p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
			_p2pTransferVO.setErrorCode(null);
			PretupsBL.validateRecieverLimits(null, _p2pTransferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.P2P_MODULE);
            final TransferItemVO senderTransferItemVO = (TransferItemVO) _p2pTransferVO.getTransferItemList().get(0);
            senderTransferItemVO.setTransferValue(0);
			 senderTransferItemVO.setPostBalance(0);
			 _p2pTransferVO.setTransferValue(_vomsVO.getTalkTime());
			ReqNetworkServiceLoadController.increaseRechargeCounters(_requestVO.getInstanceID(), _requestVO.getMessageGatewayVO().getGatewayType(), _senderNetworkCode,
					_serviceType, _transferID, LoadControllerI.COUNTER_SUCCESS_REQUEST, 0, true, _receiverVO.getNetworkCode());
			 /******SOS settlement*********/
			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SOS_ONLINE_ALLOW))).booleanValue()) {
	                if (_p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
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
	                            sosvo.setLmbAmountAtIN(_receiverTransferItemVO.getLmbdebitvalue());
	                            sosvo.setSettlmntServiceType(_requestVO.getServiceType()); // samna
	                            // soin
	                            sosvo.setLocale(_receiverLocale);
	                            final SOSSettlementController sosSettlementController = new SOSSettlementController();
	                            sosSettlementController.processSOSRechargeRequest(sosvo);
	                        } else {
	                            _log.error(this, "PrepaidController", methodName + " No record found in database for this number :" + _receiverMSISDN);
	                        }
	                    } catch (BTSLBaseException be) {
	                        _log.errorTrace(methodName, be);
	                        _log.error(this, "PrepaidController",
	                            methodName + "Transaction ID: " + sosvo.getTransactionID() + "Msisdn" + _receiverMSISDN + "Getting Exception while processing LMB request :" + be);
	                    } finally {
	                        if (con != null) {
	                            try {
	                                con.commit();
	                            } catch (Exception e) {
	                                _log.errorTrace(methodName, e);
	                            }
								if (mcomCon != null) {
									mcomCon.close("PrepaidController#run");
									mcomCon = null;
								}
	                            con = null;
	                        }
	                    }
	                }
	            }
			 /******SOS settlement*********/
		} catch (BTSLBaseException be) {
			_log.errorTrace(methodName, be);
			_requestVO.setSuccessTxn(false);

			if (be.isKey()) {
				if (_p2pTransferVO.getErrorCode() == null) {
					_p2pTransferVO.setErrorCode(be.getMessageKey());
				}

				_requestVO.setMessageCode(be.getMessageKey());
				_requestVO.setMessageArguments(be.getArgs());
			} else {
				_p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
				_requestVO.setMessageCode(be.getMessageKey());
				_requestVO.setMessageArguments(be.getArgs());
			}

			if (be.isKey() && _p2pTransferVO.getSenderReturnMessage() == null) {
				btslMessages = be.getBtslMessages();
			} else if (_p2pTransferVO.getSenderReturnMessage() == null) {
				_p2pTransferVO.setSenderReturnMessage(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, _transferID, "Error Code:" + btslMessages.print());
			}

			ReqNetworkServiceLoadController.increaseRechargeCounters(_requestVO.getInstanceID(), _requestVO.getMessageGatewayVO().getGatewayType(), _senderNetworkCode,
					_serviceType, _transferID, LoadControllerI.COUNTER_FAIL_REQUEST, 0, false, _receiverVO.getNetworkCode());
		} catch (Exception e) {
			_requestVO.setSuccessTxn(false);

			_log.errorTrace(methodName, e);
			if (BTSLUtil.isNullString(_p2pTransferVO.getErrorCode())) {
				_p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			}
			_log.error(methodName, _transferID, "Exception:" + e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherConsController[run]", _transferID,
					_senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
			btslMessages = new BTSLMessages(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);

			ReqNetworkServiceLoadController.increaseRechargeCounters(_requestVO.getInstanceID(), _requestVO.getMessageGatewayVO().getGatewayType(), _senderNetworkCode,
					_serviceType, _transferID, LoadControllerI.COUNTER_FAIL_REQUEST, 0, false, _receiverVO.getNetworkCode());
		} finally {
			try {
				if (_p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL) && (_p2pTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _p2pTransferVO
						.getReceiverReturnMsg()).isKey())) {
					_p2pTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.P2P_RECEIVER_FAIL), new String[] { String.valueOf(_transferID), PretupsBL
							.getDisplayAmount(_p2pTransferVO.getRequestedAmount()) }));
				}

				LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);

				if (mcomCon == null) {
					mcomCon = new MComConnection();con=mcomCon.getConnection();
				}
				try {
					//SubscriberBL.updateSubscriberLastDetails(con, _p2pTransferVO, _senderVO, _currentDate, _p2pTransferVO.getTransferStatus());
					
					if(PretupsErrorCodesI.TXN_STATUS_SUCCESS.equalsIgnoreCase(_p2pTransferVO.getTransferStatus())){
		            SubscriberBL.increaseTransferOutCounts(con, _senderTransferItemVO.getServiceClass(), _p2pTransferVO,_serviceType);
					}
					SubscriberBL.updateSubscriberLastDetails(con, _p2pTransferVO, _senderVO, _currentDate, _p2pTransferVO.getTransferStatus());
					
				} catch (Exception e) {
					_log.errorTrace(methodName, e);
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherConsController[run]", _transferID,
							_senderMSISDN, _senderNetworkCode, "Not able to update Subscriber Last Details Exception:" + e.getMessage());
				}

				try {
					if (_receiverVO != null && _receiverVO.isUnmarkRequestStatus()) {
						PretupsBL.unmarkReceiverLastRequest(con, _transferID, _receiverVO);
					}
				} catch (BTSLBaseException bex) {
					_log.errorTrace(methodName, bex);
				} catch (Exception e) {
					_log.errorTrace(methodName, e);
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherConsController[run]", _transferID,
							_senderMSISDN, _senderNetworkCode, "Not able to unmark Receiver Last Request, Exception:" + e.getMessage());
				}

				if (_finalTransferStatusUpdate) {
					 _p2pTransferVO.setModifiedOn(_currentDate);
					_p2pTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
					PretupsBL.updateVoucherTransferDetails(con, _p2pTransferVO);
					final VomsVoucherDAO vomsVoucherDAO = new VomsVoucherDAO();
                    final VomsVoucherVO vomsVoucherVO = vomsVoucherDAO.loadVomsVoucherVOCons(con, _p2pTransferVO);
                    updateCount = vomsVoucherDAO.updateVoucherConsumptionStatus(con,  _p2pTransferVO, vomsVoucherVO);
                   
                    if (updateCount <= 0) {
                    	_log.error(methodName, _transferID, "Exception while updating Voucher Status in database:");
        				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherConsController[run]", _transferID,
        						_senderMSISDN, _senderNetworkCode, "Exception while updating Voucher Status in database");
        				throw new BTSLBaseException("VoucherConsController", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
                    } 
				}
			} catch (BTSLBaseException bex) {
				_log.errorTrace(methodName, bex);
				try {
					if (con != null) {
						con.rollback();
					}
				} catch (Exception ex) {
					_log.errorTrace(methodName, ex);
				}
				_log.error(methodName, _transferID, "BTSL Base Exception while updating transfer details in database:" + bex.getMessage());
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
				try {
					if (con != null) {
						con.rollback();
					}
				} catch (Exception ex) {
					_log.errorTrace(methodName, ex);
				}
				_log.error(methodName, _transferID, "Exception while updating transfer details in database:" + e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherConsController[run]", _transferID,
						_senderMSISDN, _senderNetworkCode, "Exception while updating transfer details in database , Exception:" + e.getMessage());
			}
			if (con != null) {
				try {
					con.commit();
				} catch (Exception e) {
					_log.errorTrace(methodName, e);
				}
				if(mcomCon != null){mcomCon.close("VoucherConsController#processThread");mcomCon=null;}
				con = null;
			}
			if (_requestVO.getSenderVO() != null && !_p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS) && _requestVO
					.isDecreaseGroupTypeCounter() && ((SenderVO) _requestVO.getSenderVO()).getUserControlGrouptypeCounters() != null) {
				PretupsBL.decreaseGroupTypeCounters(((SenderVO) _requestVO.getSenderVO()).getUserControlGrouptypeCounters());
			}

			final String recAlternetGatewaySMS = BTSLUtil.NullToString(Constants.getProperty("P2P_REC_MSG_REQD_BY_ALT_GW"));
			String reqruestGW = _p2pTransferVO.getRequestGatewayCode();
			if (!BTSLUtil.isNullString(recAlternetGatewaySMS) && (recAlternetGatewaySMS.split(":")).length >= 2) {
				if (reqruestGW.equalsIgnoreCase(recAlternetGatewaySMS.split(":")[0])) {
					reqruestGW = (recAlternetGatewaySMS.split(":")[1]).trim();
					if (_log.isDebugEnabled()) {
						_log.debug("run: Reciver Message push through alternate GW", reqruestGW, "Requested GW was:" + _p2pTransferVO.getRequestGatewayCode());
					}
				}
			}
			if(!BTSLUtil.isStringIn(reqruestGW, _notAllowedRecSendMessGatw) && !"ALL".equals(_notAllowedRecSendMessGatw)){
				if (_p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
					if (_p2pTransferVO.getReceiverReturnMsg() == null) {
						(new PushMessage(_receiverMSISDN, getReceiverSuccessMessage(), _transferID, reqruestGW, _receiverLocale)).push();
					} else if (_p2pTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) _p2pTransferVO.getReceiverReturnMsg()).isKey()) {
						final BTSLMessages btslRecMessages = (BTSLMessages) _p2pTransferVO.getReceiverReturnMsg();
						(new PushMessage(_receiverMSISDN, BTSLUtil.getMessage(_receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), _transferID,
								reqruestGW, _receiverLocale)).push();
					} else {
						(new PushMessage(_receiverMSISDN, (String) _p2pTransferVO.getReceiverReturnMsg(), _transferID, reqruestGW, _receiverLocale)).push();
					}
				}
			}
			if (_recTopupFailMessageRequired && _p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS)) {
				if (_p2pTransferVO.getReceiverReturnMsg() == null) {
					(new PushMessage(_receiverMSISDN, getReceiverAmbigousMessage(), _transferID, reqruestGW, _receiverLocale)).push();
				} else if (_p2pTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) _p2pTransferVO.getReceiverReturnMsg()).isKey()) {
					final BTSLMessages btslRecMessages = (BTSLMessages) _p2pTransferVO.getReceiverReturnMsg();
					(new PushMessage(_receiverMSISDN, BTSLUtil.getMessage(_receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), _transferID,
							reqruestGW, _receiverLocale)).push();
				} else {
					(new PushMessage(_receiverMSISDN, (String) _p2pTransferVO.getReceiverReturnMsg(), _transferID, reqruestGW, _receiverLocale)).push();
				}
			} else if (_recTopupFailMessageRequired && _p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
				if (_p2pTransferVO.getReceiverReturnMsg() == null) {
					(new PushMessage(_receiverMSISDN, getReceiverFailMessage(), _transferID, reqruestGW, _receiverLocale)).push();
				} else if (_p2pTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) _p2pTransferVO.getReceiverReturnMsg()).isKey()) {
					final BTSLMessages btslRecMessages = (BTSLMessages) _p2pTransferVO.getReceiverReturnMsg();
					(new PushMessage(_receiverMSISDN, BTSLUtil.getMessage(_receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), _transferID,
							reqruestGW, _receiverLocale)).push();
				} else {
					(new PushMessage(_receiverMSISDN, (String) _p2pTransferVO.getReceiverReturnMsg(), _transferID, reqruestGW, _receiverLocale)).push();
				}
			}
			PushMessage pushMessages = null;
            //For Voucher recharges of Other Send message to sender also
            if (!_p2pTransferVO.getSenderMsisdn().equals(_p2pTransferVO.getReceiverMsisdn())) {
                if (!BTSLUtil.isNullString(_p2pTransferVO.getSenderReturnMessage())) {
                    pushMessages = (new PushMessage(_senderMSISDN, _p2pTransferVO.getSenderReturnMessage(), _transferID, _p2pTransferVO.getRequestGatewayCode(), _senderLocale));
                } else if (btslMessages != null) {
                    // push error message to sender
                    pushMessages = (new PushMessage(_senderMSISDN, BTSLUtil.getMessage(_senderLocale, btslMessages.getMessageKey(), btslMessages.getArgs(),_serviceType), _transferID,
                        _p2pTransferVO.getRequestGatewayCode(), _senderLocale));
                }else if (_p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
                    pushMessages = (new PushMessage(_senderMSISDN, getSenderSuccessMessage(), _transferID, _p2pTransferVO.getRequestGatewayCode(), _senderLocale));
                   
               }
             }
            else {
                if (btslMessages != null) {
                    // push error message to sender
                    pushMessages = (new PushMessage(_senderMSISDN, BTSLUtil.getMessage(_senderLocale, btslMessages.getMessageKey(), btslMessages.getArgs(),_serviceType), _transferID,
                        _p2pTransferVO.getRequestGatewayCode(), _senderLocale));
                }
            }
            
            if(pushMessages!=null){
                pushMessages.push();
                }
            
			if (!_oneLog) {
				OneLineTXNLog.log(_p2pTransferVO, _senderTransferItemVO, _receiverTransferItemVO,_vomsVO);
			}
			TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
					"Transaction Ending", PretupsI.TXN_LOG_STATUS_SUCCESS, "Transfer Status=" + _p2pTransferVO.getTransferStatus() + " Transfer Category=" + _p2pTransferVO
					.getTransferCategory() + " Error Code=" + _p2pTransferVO.getErrorCode() + " Message=" + _p2pTransferVO.getSenderReturnMessage());

			P2PRequestDailyLog.log(P2PRequestDailyLog.populateP2PRequestDailyLogVO(_requestVO, _p2pTransferVO));

			if (_log.isDebugEnabled()) {
				_log.debug(methodName, _transferID, "Exiting");
			}
		}
	}
/**
 * 
 * @return
 */
	private String getSenderSuccessMessage() {
		final String[] messageArgArray = { _transferID,_receiverMSISDN };
		return BTSLUtil.getMessage(_senderLocale, PretupsErrorCodesI.VMS_SENDER_SUCCESS, messageArgArray);
	}
/**
 * 
 * @return
 */
	private String getSenderRegistrationMessage() {
        if (_senderVO.isPinUpdateReqd()) {
            final String[] messageArgArray = { BTSLUtil.decryptText(_senderVO.getPin()) };
            return BTSLUtil.getMessage(_senderLocale, PretupsErrorCodesI.P2P_SENDER_AUTO_REG_SUCCESS_WITHPIN, messageArgArray);
        }
        return BTSLUtil.getMessage(_senderLocale, PretupsErrorCodesI.P2P_SENDER_AUTO_REG_SUCCESS, null);
    }
	/**
	 * Method to get the Receiver Ambigous Message
	 * 
	 * @return
	 */
	private String getReceiverAmbigousMessage() {
		final String[] messageArgArray = { _senderMSISDN, _transferID, PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()) };
		return BTSLUtil.getMessage(_receiverLocale, PretupsErrorCodesI.P2P_RECEIVER_AMBIGOUS_MESSAGE_KEY, messageArgArray);
	}

	/**
	 * Method to get the Receiver Fail Message
	 * 
	 * @return
	 */
	private String getReceiverFailMessage() {
		final String[] messageArgArray = { _senderMSISDN, _transferID, PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()) };
		return BTSLUtil.getMessage(_receiverLocale, PretupsErrorCodesI.P2P_RECEIVER_FAIL_MESSAGE_KEY, messageArgArray);
	}
/**
 * method to get barred user message
 * @return
 */
	private String getBarredMessageforSender() {
        return BTSLUtil.getMessage(_senderLocale, PretupsErrorCodesI.BARRED_SUBSCRIBER_FOR_INVALID_PIN_SNDR_MSG, null);
    }
	private String getBarredMessageforReceiver() {
        return BTSLUtil.getMessage(_receiverLocale, PretupsErrorCodesI.BARRED_SUBSCRIBER_FOR_INVALID_PIN_RECV_MSG, null);
    }
	/***
	 * 
	 * Method updated for notification message using service class date 15/05/06
	 */
	private String getReceiverSuccessMessage() {
		final String METHOD_NAME = "getReceiverSuccessMessage";
		String[] messageArgArray = null;
		String key = null;

		String dateStr = null;
		try {
			dateStr = BTSLUtil.getDateStringFromDate(_receiverTransferItemVO.getNewExpiry());
		} catch (Exception e) {
			_log.errorTrace(METHOD_NAME, e);
			dateStr = String.valueOf(_receiverTransferItemVO.getNewExpiry());
		}
		messageArgArray = new String[] { _transferID, PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(_receiverTransferItemVO
				.getTransferValue()), Long.toString(_receiverTransferItemVO.getPostBalance()), dateStr, _senderMSISDN, PretupsBL.getDisplayAmount(_p2pTransferVO
						.getReceiverAccessFee()), _p2pTransferVO.getSubService(), String.valueOf(_p2pTransferVO.getReceiverBonus1()), String.valueOf(_p2pTransferVO.getReceiverBonus2()), PretupsBL
				.getDisplayAmount(_p2pTransferVO.getBonusTalkTimeValue()), PretupsBL.getDisplayAmount(_p2pTransferVO.getCalminusBonusvalue()), String.valueOf(_p2pTransferVO
						.getReceiverBonus1Validity()), String.valueOf(_p2pTransferVO.getReceiverBonus2Validity()), String.valueOf(_p2pTransferVO.getReceiverCreditBonusValidity()) };
		if (_p2pTransferVO.getBonusTalkTimeValue() == 0) {
			key = PretupsErrorCodesI.VMS_RECEIVER_SUCCESS;// return
		} else {
			key = PretupsErrorCodesI.VMS_RECEIVER_SUCCESS_WITH_BONUS;
		}

		return BTSLUtil.getMessage(_receiverLocale, key, messageArgArray);
	}

	/**
	 * Populates the Sender Transfer Items VO
	 * 
	 */
	private void setSenderTransferItemVO() {
		_senderTransferItemVO = new TransferItemVO();
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
		_receiverTransferItemVO = new TransferItemVO();
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
	 * Method to populate the Interface Details of the sender and receiver based
	 * on action specified
	 * 
	 * @param action
	 *            Can be Validate / Topup
	 * @throws BTSLBaseException
	 */
	public void populateServicePaymentInterfaceDetails(Connection p_con, String action) throws BTSLBaseException {
		final String senderNetworkCode = _senderVO.getNetworkCode();
		String receiverNetworkCode = _receiverVO.getNetworkCode();
		final long senderPrefixID = _senderVO.getPrefixID();
		long receiverPrefixID = _receiverVO.getPrefixID();
		boolean isSenderFound = false;
		boolean isReceiverFound = false;
		if (_log.isDebugEnabled()) {
			_log.debug(
					this,
					"Getting interface details For Action=" + action + " _senderInterfaceInfoInDBFound=" + _senderInterfaceInfoInDBFound + " _receiverInterfaceInfoInDBFound=" + _receiverInterfaceInfoInDBFound);
		}

		if (((!_senderInterfaceInfoInDBFound && (_p2pTransferVO.getPaymentMethodKeywordVO() == null || !PretupsI.YES.equals(_p2pTransferVO.getPaymentMethodKeywordVO()
				.getUseDefaultInterface()))) && action.equals(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION)) || action
				.equals(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION)) {
			if (_p2pTransferVO.getPaymentMethodKeywordVO() != null && PretupsI.YES.equals(_p2pTransferVO.getPaymentMethodKeywordVO().getUseDefaultInterface())) {
				if (_log.isDebugEnabled()) {
					_log.debug(this, "For Sender using the Payment Method Default Interface as=" + _p2pTransferVO.getPaymentMethodKeywordVO().getDefaultInterfaceID());
				}
				_senderTransferItemVO.setPrefixID(senderPrefixID);
				_senderTransferItemVO.setInterfaceID(_p2pTransferVO.getPaymentMethodKeywordVO().getDefaultInterfaceID());
				_senderTransferItemVO.setInterfaceHandlerClass(_p2pTransferVO.getPaymentMethodKeywordVO().getHandlerClass());
				_senderAllServiceClassID = _p2pTransferVO.getPaymentMethodKeywordVO().getAllServiceClassId();
				_senderInterfaceStatusType = _p2pTransferVO.getPaymentMethodKeywordVO().getStatusType();
				_p2pTransferVO.setSenderAllServiceClassID(_senderAllServiceClassID);
				_senderTransferItemVO.setInterfaceType(_p2pTransferVO.getPaymentMethodType());
				_p2pTransferVO.setSenderInterfaceStatusType(_senderInterfaceStatusType);
				if (!PretupsI.YES.equals(_p2pTransferVO.getPaymentMethodKeywordVO().getStatus()) && PretupsI.INTERFACE_STATUS_TYPE_MANUAL.equals(_senderInterfaceStatusType)) {
					if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(_senderLocale)).getMessage())) {
						_p2pTransferVO.setSenderReturnMessage(_p2pTransferVO.getPaymentMethodKeywordVO().getLang1Message());
					} else {
						_p2pTransferVO.setSenderReturnMessage(_p2pTransferVO.getPaymentMethodKeywordVO().getLang2Message());
					}
					throw new BTSLBaseException(this, "populateServicePaymentInterfaceDetails", PretupsErrorCodesI.INTERFACE_NOT_ACTIVE);
				}
				isSenderFound = true;
			} else {
				isSenderFound = getInterfaceRoutingDetails(p_con, _senderMSISDN, senderPrefixID, _senderVO.getSubscriberType(), senderNetworkCode, _p2pTransferVO
						.getServiceType(), _p2pTransferVO.getPaymentMethodType(), PretupsI.USER_TYPE_SENDER, action);
			}
		} else {
			isSenderFound = true;
		}
		if (!isSenderFound) {
			if (!_senderVO.isDefUserRegistration()) {
				throw new BTSLBaseException("VoucherConsController", "populateServicePaymentInterfaceDetails", PretupsErrorCodesI.P2P_SENDER_ALREADY_REG_NOT_FOUND_IN_VAL, 0,
						new String[] { ((LookupsVO) LookupsCache.getObject(PretupsI.SUBSRICBER_TYPE, _senderVO.getSubscriberType())).getLookupName() }, null);
			}
			throw new BTSLBaseException("VoucherConsController", "populateServicePaymentInterfaceDetails", PretupsErrorCodesI.P2P_NOTFOUND_PAYMENTINTERFACEMAPPING);
		}

		if ((!_receiverInterfaceInfoInDBFound && action.equals(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION)) || action
				.equals(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION)) {
			isReceiverFound = getInterfaceRoutingDetails(p_con, _receiverMSISDN, receiverPrefixID, _type, receiverNetworkCode, _p2pTransferVO.getServiceType(), _type,
					PretupsI.USER_TYPE_RECEIVER, action);
			if (action.equals(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION) && !isReceiverFound && _performIntfceCatRoutingBeforeVal && _useAlternateCategory && !_interfaceCatRoutingDone) {
				_log.info(this,
						"********* Performing ALTERNATE INTERFACE CATEGORY routing for receiver before IN Validations on Interface=" + _newInterfaceCategory + " *********");

				_type = _newInterfaceCategory;
				_interfaceCatRoutingDone = true;

				_requestVO.setReqSelector(_newDefaultSelector);
				_p2pTransferVO.setSubService(_newDefaultSelector);

				_networkPrefixVO = null;
				_networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(_receiverVO.getMsisdnPrefix(), _type);
				if (_networkPrefixVO != null) {
					if (_log.isDebugEnabled()) {
						_log.debug(this, "Getting Reeciver Prefix ID for MSISDN=" + _receiverMSISDN + " as " + _networkPrefixVO.getPrefixID());
					}
					_receiverVO.setNetworkCode(_networkPrefixVO.getNetworkCode());
					_receiverVO.setPrefixID(_networkPrefixVO.getPrefixID());
					_receiverVO.setSubscriberType(_networkPrefixVO.getSeriesType());
					receiverNetworkCode = _receiverVO.getNetworkCode();
					receiverPrefixID = _receiverVO.getPrefixID();
					isReceiverFound = getInterfaceRoutingDetails(p_con, _receiverMSISDN, receiverPrefixID, _type, receiverNetworkCode, _p2pTransferVO.getServiceType(), _type,
							PretupsI.USER_TYPE_RECEIVER, action);
				} else {
					_log.error(this, "Series Not Defined for Alternate Interface =" + _type + " For Series=" + _receiverVO.getMsisdnPrefix());
					EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO,
							"VoucherConsController[populateServicePaymentInterfaceDetails]", "", "", "",
							"Series =" + _receiverVO.getMsisdnPrefix() + " Not Defined for Series type=" + _type + " But alternate Category Routing was required on interface");
					isReceiverFound = false;
				}
			}
		} else {
			isReceiverFound = true;
		}
		if (!isReceiverFound) {
			throw new BTSLBaseException("VoucherConsController", "populateServicePaymentInterfaceDetails", PretupsErrorCodesI.P2P_NOTFOUND_SERVICEINTERFACEMAPPING);
		}
	}

	/**
	 * Get the Receiver Request String to be send to common Client
	 * 
	 * @return
	 */
	private String getReceiverCommonString() {
		final String methodName = "getReceiverCommonString";
		StringBuffer strBuff = null;
		//strBuff = new StringBuffer("MSISDN=" + _receiverMSISDN);
		strBuff = new StringBuffer("TRANSACTION_ID=" + _transferID);
		strBuff.append("&NETWORK_CODE=" + _receiverVO.getNetworkCode());
		strBuff.append("&INTERFACE_ID=" + _receiverTransferItemVO.getInterfaceID());
		strBuff.append("&INTERFACE_HANDLER=" + _receiverTransferItemVO.getInterfaceHandlerClass());
		strBuff.append("&INT_MOD_COMM_TYPE=" + _intModCommunicationTypeR);
		strBuff.append("&INT_MOD_IP=" + _intModIPR);
		strBuff.append("&INT_MOD_PORT=" + _intModPortR);
		strBuff.append("&INT_MOD_CLASSNAME=" + _intModClassNameR);
		strBuff.append("&MODULE=" + PretupsI.P2P_MODULE);
		strBuff.append("&USER_TYPE=R");
		strBuff.append("&CARD_GROUP_SELECTOR=" + _requestVO.getReqSelector());
		strBuff.append("&REQ_SERVICE=" + _serviceType);
		strBuff.append("&INT_ST_TYPE=" + _p2pTransferVO.getReceiverInterfaceStatusType());
		try {
			strBuff.append("&TRANSFER_DATE=" + BTSLUtil.getDateTimeStringFromDate(_p2pTransferVO.getTransferDate(), PretupsI.TIMESTAMP_DATESPACEHHMM));
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		strBuff.append("&REQUEST_GATEWAY_CODE=" + _requestVO.getRequestGatewayCode());
		strBuff.append("&REQUEST_GATEWAY_TYPE=" + _requestVO.getRequestGatewayType());
		strBuff.append("&LOGIN=" + _requestVO.getLogin());
		strBuff.append("&PASSWORD=" + _requestVO.getPassword());
		strBuff.append("&SOURCE_TYPE=" + _requestVO.getSourceType());
		strBuff.append("&SERVICE_PORT=" + _requestVO.getServicePort());

		strBuff.append("&VOUCHER_CODE=" + _p2pTransferVO.getVoucherCode());

		return strBuff.toString();
	}

	/**
	 * Gets the receiver validate Request String
	 * 
	 * @return
	 */
	public String getReceiverValidateStr() {
		StringBuffer strBuff = null;
		strBuff = new StringBuffer(getReceiverCommonString());
		strBuff.append("&MSISDN=" + _receiverMSISDN);
		strBuff.append("&INTERFACE_ACTION=" + PretupsI.INTERFACE_VALIDATE_ACTION);
		strBuff.append("&SERVICE_CLASS=" + _receiverTransferItemVO.getServiceClassCode());
		strBuff.append("&ACCOUNT_ID=" + _receiverTransferItemVO.getReferenceID());
		strBuff.append("&ACCOUNT_STATUS=" + _receiverTransferItemVO.getAccountStatus());
		strBuff.append("&CREDIT_LIMIT=" + _receiverTransferItemVO.getPreviousBalance());
		strBuff.append("&SERVICE_TYPE=" + _senderSubscriberType + "-" + _type);
		strBuff.append("&SENDER_MSISDN=" + _p2pTransferVO.getSenderMsisdn());
		if (_p2pTransferVO.getSerialNumber() != null) {
			strBuff.append("&SERIAL_NUMBER=" + _p2pTransferVO.getSerialNumber());
		} else {
			strBuff.append("&SERIAL_NUMBER=" + "");
		}
		strBuff.append("&SENDER_USER_ID=" + _p2pTransferVO.getSenderID());
		return strBuff.toString();
	}

	/**
	 * Gets the sender Credit Request String
	 * 
	 * @return
	 */
	public String getReceiverCreditStr() {
		StringBuffer strBuff = null;
		strBuff = new StringBuffer(getReceiverCommonString());
		strBuff.append("&MSISDN=" + _receiverMSISDN);
		strBuff.append("&INTERFACE_ACTION=" + PretupsI.INTERFACE_CREDIT_ACTION);
		strBuff.append("&INTERFACE_AMOUNT=" + _receiverTransferItemVO.getTransferValue());
		strBuff.append("&SERVICE_CLASS=" + _receiverTransferItemVO.getServiceClassCode());
		strBuff.append("&ACCOUNT_ID=" + _receiverTransferItemVO.getReferenceID());
		strBuff.append("&ACCOUNT_STATUS=" + _receiverTransferItemVO.getAccountStatus());
		strBuff.append("&SOURCE_TYPE=" + _p2pTransferVO.getSourceType());
		strBuff.append("&SERIAL_NUMBER="+_p2pTransferVO.getSerialNumber());
		strBuff.append("&UPDATE_STATUS=" + VOMSI.VOUCHER_USED);
		strBuff.append("&SENDER_MSISDN=" + _senderMSISDN);
		strBuff.append("&RECEIVER_MSISDN=" + _receiverMSISDN);
		strBuff.append("&SENDER_ID=" + ((SenderVO) _requestVO.getSenderVO()).getUserID());
		strBuff.append("&SERVICE_TYPE=" + _senderSubscriberType + "-" + _type);
		strBuff.append("&TOPUP=" + _receiverTransferItemVO.getTransferValue());
		strBuff.append("&VALIDITY_DAYS=" + _receiverTransferItemVO.getValidity());
		strBuff.append("&VOUCHER_TYPE=" + _vomsVO.getVoucherType());
		//added by Ashish for VIL
		//strBuff.append("&MRP=" + (long)_vomsVO.getMRP());
		strBuff.append("&MRP=" + BTSLUtil.parseDoubleToLong(_vomsVO.getMRP()));
		
		strBuff.append("&VOUCHER_SEGMENT=" + _vomsVO.getVoucherSegment());
		strBuff.append("&PRODUCT_NAME=" + _vomsVO.getProductName());
		strBuff.append("&CARD_GROUP=" + _p2pTransferVO.getCardGroupCode());
		strBuff.append("&ACCESS_FEE=" + _p2pTransferVO.getReceiverAccessFee());
		
		return strBuff.toString();
	}

	/**
	 * Method to handle receiver credit response
	 * 
	 * @param str
	 * @throws BTSLBaseException
	 */
	public void updateForReceiverCreditResponse(String str) throws BTSLBaseException {
		
		if (_log.isDebugEnabled()) {
			_log.debug(
					this,
					"updateForReceiverCreditResponse :: REsponse String" + str);
		}
		final HashMap map = BTSLUtil.getStringToHash(str, "&", "=");
		final String status = (String) map.get("TRANSACTION_STATUS");

 		if (null != map.get("IN_START_TIME")) {
			_requestVO.setTopUPReceiverRequestSent(((Long.valueOf((String) map.get("IN_START_TIME"))).longValue()));
		}
		if (null != map.get("IN_END_TIME")) {
			_requestVO.setTopUPReceiverResponseReceived(((Long.valueOf((String) map.get("IN_END_TIME"))).longValue()));
		}
		final String interfaceStatusType = (String) map.get("INT_SET_STATUS");
		if (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) && _receiverTransferItemVO.getInterfaceID().equals(_senderTransferItemVO.getInterfaceID())) {
			_p2pTransferVO.setSenderInterfaceStatusType(InterfaceCloserI.INTERFACE_AUTO_ACTIVE);
		}
		if (!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME
				.equals(interfaceStatusType))) {
			new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES, _receiverTransferItemVO.getInterfaceID(), interfaceStatusType,
					PretupsErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG, PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
		}

		_p2pTransferVO.setTransferValueStr("0");
		_receiverTransferItemVO.setProtocolStatus((String) map.get("PROTOCOL_STATUS"));
		_receiverTransferItemVO.setInterfaceResponseCode((String) map.get("INTERFACE_STATUS"));
		_senderTransferItemVO.setInterfaceResponseCode((String) map.get("INTERFACE_STATUS"));
		_senderTransferItemVO.setProtocolStatus((String) map.get("PROTOCOL_STATUS"));

		String updateStatus = (String) map.get("UPDATE_STATUS");

		if (BTSLUtil.isNullString(updateStatus)) {
			updateStatus = status;
		}

		_receiverTransferItemVO.setUpdateStatus(status);
		_receiverVO.setInterfaceResponseCode(_receiverTransferItemVO.getInterfaceResponseCode());

		_receiverTransferItemVO.setUpdateStatus1((String) map.get("UPDATE_STATUS1"));
		_receiverTransferItemVO.setUpdateStatus2((String) map.get("UPDATE_STATUS2"));

		if (!BTSLUtil.isNullString((String) map.get("ADJUST_AMOUNT"))) {
			_receiverTransferItemVO.setAdjustValue(Long.parseLong((String) map.get("ADJUST_AMOUNT")));
		}

		final String methodName = "updateForReceiverCreditResponse";
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
		_receiverTransferItemVO.setReferenceID((String) map.get("IN_RECON_ID"));

		String[] strArr = null;
		if (BTSLUtil.isNullString(status) || (!status.equals(InterfaceErrorCodesI.SUCCESS) && !status.equals(InterfaceErrorCodesI.AMBIGOUS))) {
			_p2pTransferVO.setErrorCode(status + "_R");
			_p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			_receiverTransferItemVO.setTransferStatus(status);
			_senderTransferItemVO.setTransferStatus(status);
			_senderTransferItemVO.setUpdateStatus(status);
			strArr = new String[] { _receiverMSISDN, PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()), _transferID };
			throw new BTSLBaseException(this, methodName, _p2pTransferVO.getErrorCode(), 0, strArr, null);
		} else if (status.equals(InterfaceErrorCodesI.AMBIGOUS)) {
			_p2pTransferVO.setErrorCode(status + "_R");
			_p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
			_receiverTransferItemVO.setTransferStatus(status);
			
			_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
			_receiverTransferItemVO.setUpdateStatus(status);
			_senderTransferItemVO.setUpdateStatus(status);
			strArr = new String[] { _transferID, _receiverTransferItemVO.getMsisdn(), PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()) };
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS, 0, strArr, null);
		} else {
			_receiverTransferItemVO.setTransferStatus(status);
			_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
			_receiverTransferItemVO.setUpdateStatus(status);
			_senderTransferItemVO.setUpdateStatus(status);
			_senderTransferItemVO.setTransferValue(Long.parseLong((String) map.get("TOPUP")));
			_p2pTransferVO.setTransferValue(Long.parseLong((String) map.get("TOPUP")));
			_p2pTransferVO.setTransferValueStr((String) map.get("TOPUP"));
			_receiverTransferItemVO.setTransferValue(Long.parseLong((String) map.get("TOPUP")));
			if (!BTSLUtil.isNullString((String) map.get("INTERFACE_POST_BALANCE"))) {
				_receiverTransferItemVO.setPostBalance(Long.parseLong((String) map.get("INTERFACE_POST_BALANCE")));
			}

		}

		try {
			final Date expDate = BTSLUtil.getDateFromDateString((String) map.get("NEW_EXPIRY_DATE"), "yyyyMMdd");
			_receiverTransferItemVO.setNewExpiry(expDate);
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}

	}

	/**
	 * Method to check the various level of loads whether request can be passed
	 * or not
	 * 
	 * @throws BTSLBaseException
	 */
	private void checkTransactionLoad() throws BTSLBaseException {
		final String methodName = "checkTransactionLoad";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Checking load for transfer ID=" + _transferID);
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

			final int senderLoadStatus = LoadController.checkInterfaceLoad(((SenderVO) _p2pTransferVO.getSenderVO()).getNetworkCode(), _senderTransferItemVO.getInterfaceID(),
					_transferID, _p2pTransferVO, true);
			int recieverLoadStatus = 0;
			if (senderLoadStatus == 0) {
				recieverLoadStatus = LoadController.checkInterfaceLoad(((ReceiverVO) _p2pTransferVO.getReceiverVO()).getNetworkCode(), _receiverTransferItemVO
						.getInterfaceID(), _transferID, _p2pTransferVO, true);
				if (recieverLoadStatus == 0) {
					try {
						LoadController.checkTransactionLoad(((SenderVO) _p2pTransferVO.getSenderVO()).getNetworkCode(), _senderTransferItemVO.getInterfaceID(),
								PretupsI.P2P_MODULE, _transferID, true, LoadControllerI.USERTYPE_SENDER);
					} catch (BTSLBaseException e) {
						LoadController.decreaseCurrentInterfaceLoad(_transferID, ((ReceiverVO) _p2pTransferVO.getReceiverVO()).getNetworkCode(), _receiverTransferItemVO
								.getInterfaceID(), LoadControllerI.DEC_LAST_TRANS_COUNT);
						throw e;
					}
					try {
						LoadController.checkTransactionLoad(((ReceiverVO) _p2pTransferVO.getReceiverVO()).getNetworkCode(), _receiverTransferItemVO.getInterfaceID(),
								PretupsI.P2P_MODULE, _transferID, true, LoadControllerI.USERTYPE_RECEIVER);
					} catch (BTSLBaseException e) {
						LoadController.decreaseTransactionInterfaceLoad(_transferID, ((SenderVO) _p2pTransferVO.getSenderVO()).getNetworkCode(),
								LoadControllerI.DEC_LAST_TRANS_COUNT);
						throw e;
					}
					if (_log.isDebugEnabled()) {
						_log.debug("VoucherConsController[checkTransactionLoad]", "_transferID=" + _transferID + " Successfully through load");
					}
				}
				else if (recieverLoadStatus == 1) {
					LoadController.decreaseCurrentInterfaceLoad(_transferID, ((SenderVO) _p2pTransferVO.getSenderVO()).getNetworkCode(), _senderTransferItemVO
							.getInterfaceID(), LoadControllerI.DEC_LAST_TRANS_COUNT);

					final String strArr[] = { _receiverMSISDN, String.valueOf(_p2pTransferVO.getRequestedAmount()) };
					throw new BTSLBaseException("VoucherConsController", "checkTransactionLoad", PretupsErrorCodesI.REQUEST_IN_QUEUE, 0, strArr, null);
				}
				else {
					throw new BTSLBaseException("VoucherConsController", "checkTransactionLoad", PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
				}
			}
			else if (senderLoadStatus == 1) {
				final String strArr[] = { _receiverMSISDN, String.valueOf(_p2pTransferVO.getRequestedAmount()) };
				throw new BTSLBaseException("VoucherConsController", "checkTransactionLoad", PretupsErrorCodesI.REQUEST_IN_QUEUE, 0, strArr, null);
			}
			else {
				throw new BTSLBaseException("EVDP2PController", "checkTransactionLoad", PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
			}
		} catch (BTSLBaseException be) {
			_log.error("VoucherConsController[checkTransactionLoad]", "Refusing request getting Exception:" + be.getMessage());
			throw be;
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
			_log.error("VoucherConsController[checkTransactionLoad]", "Refusing request getting Exception:" + e.getMessage());
			throw new BTSLBaseException("VoucherConsController", "checkTransactionLoad", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
		}
	}

	/**
	 * This method will check the transaction load on the given interface
	 * 
	 * @param p_userType
	 * @param p_interfaceID
	 * @throws BTSLBaseException
	 */
	private void checkTransactionLoad(String p_userType, String p_interfaceID) throws BTSLBaseException {

		final String methodName = "checkTransactionLoad";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Checking load for transfer ID=" + _transferID + " on interface=" + p_interfaceID);
		}
		int recieverLoadStatus = 0;

		try {
			if (PretupsI.USER_TYPE_SENDER.equals(p_userType)) {
				final int senderLoadStatus = LoadController.checkInterfaceLoad(((SenderVO) _p2pTransferVO.getSenderVO()).getNetworkCode(), p_interfaceID, _transferID,
						_p2pTransferVO, true);
				// Further process the request
				if (senderLoadStatus == 0) {
					recieverLoadStatus = LoadController.checkInterfaceLoad(((ReceiverVO) _p2pTransferVO.getReceiverVO()).getNetworkCode(), _receiverTransferItemVO
							.getInterfaceID(), _transferID, _p2pTransferVO, false);
					if (recieverLoadStatus == 0) {
						try {
							LoadController.checkTransactionLoad(((SenderVO) _p2pTransferVO.getSenderVO()).getNetworkCode(), p_interfaceID, PretupsI.P2P_MODULE, _transferID,
									true, LoadControllerI.USERTYPE_SENDER);
						} catch (BTSLBaseException e) {
							LoadController.decreaseCurrentInterfaceLoad(_transferID, ((ReceiverVO) _p2pTransferVO.getReceiverVO()).getNetworkCode(), _receiverTransferItemVO
									.getInterfaceID(), LoadControllerI.DEC_LAST_TRANS_COUNT);
							throw e;
						}
						try {
							LoadController.checkTransactionLoad(((ReceiverVO) _p2pTransferVO.getReceiverVO()).getNetworkCode(), _receiverTransferItemVO.getInterfaceID(),
									PretupsI.P2P_MODULE, _transferID, true, LoadControllerI.USERTYPE_RECEIVER);
						} catch (BTSLBaseException e) {
							LoadController.decreaseTransactionInterfaceLoad(_transferID, ((SenderVO) _p2pTransferVO.getSenderVO()).getNetworkCode(),
									LoadControllerI.DEC_LAST_TRANS_COUNT);
							throw e;
						}

						if (_log.isDebugEnabled()) {
							_log.debug("VoucherConsController[checkTransactionLoad]", "_transferID=" + _transferID + " Successfully through load");
						}
					}
					else {
						throw new BTSLBaseException("VoucherConsController", "checkTransactionLoad", PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
					}
				}
				else if (senderLoadStatus == 1) {
					final String strArr[] = { _receiverMSISDN, String.valueOf(_p2pTransferVO.getRequestedAmount()) };
					throw new BTSLBaseException("VoucherConsController", "checkTransactionLoad", PretupsErrorCodesI.REQUEST_IN_QUEUE, 0, strArr, null);
				}
				else {
					throw new BTSLBaseException("VoucherConsController", "checkTransactionLoad", PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
				}
			} else {
				recieverLoadStatus = LoadController.checkInterfaceLoad(((ReceiverVO) _p2pTransferVO.getReceiverVO()).getNetworkCode(), p_interfaceID, _transferID,
						_p2pTransferVO, false);
				if (recieverLoadStatus == 0) {
					LoadController.checkTransactionLoad(((ReceiverVO) _p2pTransferVO.getReceiverVO()).getNetworkCode(), p_interfaceID, PretupsI.P2P_MODULE, _transferID, true,
							LoadControllerI.USERTYPE_RECEIVER);
					if (_log.isDebugEnabled()) {
						_log.debug("checkTransactionLoad[checkTransactionLoad]", "_transferID=" + _transferID + " Successfully through load");
					}
				}
				else if (recieverLoadStatus == 1) {
					throw new BTSLBaseException("checkTransactionLoad", "checkTransactionLoad", PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
				}
				else {
					throw new BTSLBaseException("checkTransactionLoad", "checkTransactionLoad", PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
				}
			}
		} catch (BTSLBaseException be) {
			_log.error("VoucherConsController[checkTransactionLoad]", "Refusing request getting Exception:" + be.getMessage());
			throw be;
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
			_log.error("VoucherConsController[checkTransactionLoad]", "Refusing request getting Exception:" + e.getMessage());
			throw new BTSLBaseException("VoucherConsController", "checkTransactionLoad", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
		}
	}

	/**
	 * This method will be called to process the request from the queue
	 * 
	 * @param p_transferVO
	 */
	public void processFromQueue(TransferVO p_transferVO) {
		final String methodName = "processFromQueue";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered");
		}
		Connection con = null;MComConnectionI mcomCon = null;
		try {
			_p2pTransferVO = (P2PTransferVO) p_transferVO;
			_requestVO = _p2pTransferVO.getRequestVO();
			_senderVO = (SenderVO) _requestVO.getSenderVO();
			_receiverVO = (ReceiverVO) _p2pTransferVO.getReceiverVO();
			_type = _requestVO.getType();
			if (_type.equals(PretupsI.INTERFACE_CATEGORY_BOTH)) {
				_serviceInterfaceRoutingVO = ServiceInterfaceRoutingCache.getServiceInterfaceRoutingDetails(_receiverVO.getNetworkCode() + "_" + _requestVO.getServiceType());
				if (_serviceInterfaceRoutingVO != null) {
					if (_log.isDebugEnabled()) {
						_log.debug(
								"process",
								_requestIDStr,
								"For =" + _receiverVO.getNetworkCode() + "_" + _requestVO.getServiceType() + " Got Interface Category=" + _serviceInterfaceRoutingVO
								.getInterfaceType() + " Alternate Check Required=" + _serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool() + " Alternate Interface=" + _serviceInterfaceRoutingVO
								.getAlternateInterfaceType() + " _oldDefaultSelector=" + _oldDefaultSelector + "_newDefaultSelector= " + _newDefaultSelector);
					}

					_type = _serviceInterfaceRoutingVO.getInterfaceType();
					_oldInterfaceCategory = _type;
					_oldDefaultSelector = _serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode();
					_useAlternateCategory = _serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool();
					_newInterfaceCategory = _serviceInterfaceRoutingVO.getAlternateInterfaceType();
					_newDefaultSelector = _serviceInterfaceRoutingVO.getAlternateDefaultSelectortCode();
				} else {
					_log.info("process", _requestIDStr,
							"Service Interface Routing control Not defined, thus using default type=" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE)));
					EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherConsController[process]", "",
							_senderMSISDN, _senderNetworkCode,
							"Service Interface Routing control Not defined, thus using default type=" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE)));
					_type = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE));
					final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_transferVO.getServiceType());
					if (serviceSelectorMappingVO != null) {
						_oldDefaultSelector = serviceSelectorMappingVO.getSelectorCode();
					}

				}
			} else {
				_serviceInterfaceRoutingVO = ServiceInterfaceRoutingCache
						.getServiceInterfaceRoutingDetails(_receiverVO.getNetworkCode() + "_" + _requestVO.getServiceType() + "_" + _senderVO.getSubscriberType());
				if (_serviceInterfaceRoutingVO != null) {
					if (_log.isDebugEnabled()) {
						_log.debug(
								"process",
								_requestIDStr,
								"For =" + _receiverVO.getNetworkCode() + "_" + _requestVO.getServiceType() + " Got Interface Category=" + _serviceInterfaceRoutingVO
								.getInterfaceType() + " Alternate Check Required=" + _serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool() + " Alternate Interface=" + _serviceInterfaceRoutingVO
								.getAlternateInterfaceType() + " _oldDefaultSelector=" + _oldDefaultSelector + "_newDefaultSelector= " + _newDefaultSelector);
					}
					_oldDefaultSelector = _serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode();
				} else {
					final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_transferVO.getServiceType());
					if (serviceSelectorMappingVO != null) {
						_oldDefaultSelector = serviceSelectorMappingVO.getSelectorCode();
					}
					_log.info("process", _requestIDStr, "Service Interface Routing control Not defined, thus using default Selector=" + _oldDefaultSelector);
					EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherConsController[process]", "",
							_senderMSISDN, _senderNetworkCode, "Service Interface Routing control Not defined, thus using default selector=" + _oldDefaultSelector);
				}
			}

			if (BTSLUtil.isNullString(_requestVO.getReqSelector())) {
				if (_log.isDebugEnabled()) {
					_log.debug("process", _requestIDStr, "Selector Not found in Incoming Message Thus using Selector as  " + _oldDefaultSelector);
				}
				_requestVO.setReqSelector(_oldDefaultSelector);
			} else {
				_newDefaultSelector = _requestVO.getReqSelector();
			}

			_requestIDStr = _requestVO.getRequestIDStr();
			_receiverLocale = _requestVO.getReceiverLocale();
			_transferID = _p2pTransferVO.getTransferID();
			_senderSubscriberType = _senderVO.getSubscriberType();
			_senderNetworkCode = _senderVO.getNetworkCode();
			_senderMSISDN = ((SubscriberVO) _p2pTransferVO.getSenderVO()).getMsisdn();
			_receiverMSISDN = ((SubscriberVO) _p2pTransferVO.getReceiverVO()).getMsisdn();
			_senderLocale = _requestVO.getSenderLocale();
			_receiverLocale = _requestVO.getReceiverLocale();
			_serviceType = _requestVO.getServiceType();
			_senderTransferItemVO = _p2pTransferVO.getSenderTransferItemVO();
			_receiverTransferItemVO = _p2pTransferVO.getReceiverTransferItemVO();
			_performIntfceCatRoutingBeforeVal = _requestVO.isPerformIntfceCatRoutingBeforeVal();
			_receiverDeletionReqFromSubRouting = _requestVO.isReceiverDeletionReqFromSubRouting();
			_receiverInterfaceInfoInDBFound = _requestVO.isReceiverInterfaceInfoInDBFound();
			_senderDeletionReqFromSubRouting = _requestVO.isSenderDeletionReqFromSubRouting();
			_senderInterfaceInfoInDBFound = _requestVO.isSenderInterfaceInfoInDBFound();
			_interfaceCatRoutingDone = _requestVO.isInterfaceCatRoutingDone();

			try {
				LoadController.checkTransactionLoad(((SubscriberVO) _p2pTransferVO.getSenderVO()).getNetworkCode(), _senderTransferItemVO.getInterfaceID(),
						PretupsI.P2P_MODULE, _transferID, true, LoadControllerI.USERTYPE_SENDER);
			} catch (BTSLBaseException e) {
				// Decreasing interface load of receiver which we had
				// incremented before 27/09/06, sender was decreased in the
				// method
				LoadController.decreaseCurrentInterfaceLoad(_transferID, ((ReceiverVO) _p2pTransferVO.getReceiverVO()).getNetworkCode(), _receiverTransferItemVO
						.getInterfaceID(), LoadControllerI.DEC_LAST_TRANS_COUNT);
				throw e;
			}

			try {
				LoadController.checkTransactionLoad(((SubscriberVO) _p2pTransferVO.getReceiverVO()).getNetworkCode(), _receiverTransferItemVO.getInterfaceID(),
						PretupsI.P2P_MODULE, _transferID, true, LoadControllerI.USERTYPE_RECEIVER);
			} catch (BTSLBaseException e) {
				LoadController.decreaseTransactionInterfaceLoad(_transferID, ((SenderVO) _p2pTransferVO.getSenderVO()).getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);
				throw e;
			}

			mcomCon = new MComConnection();con=mcomCon.getConnection();
			PretupsBL.loadRecieverControlLimits(con, _requestIDStr, _p2pTransferVO);
			_receiverVO.setUnmarkRequestStatus(true);
			try {
				con.commit();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			if(mcomCon != null){mcomCon.close("VoucherConsController#processFromQueue");mcomCon=null;}
			con = null;

			_processedFromQueue = true;

			if (_log.isDebugEnabled()) {
				_log.debug("VoucherConsController[processFromQueue]", "_transferID=" + _transferID + " Successfully through load");
			}
			processValidationRequest();
			p_transferVO.setMessageCode(PretupsErrorCodesI.SENDER_UNDERPROCESS_SUCCESS);
			final String[] messageArgArray = { p_transferVO.getTransferID(), PretupsBL.getDisplayAmount(p_transferVO.getRequestedAmount()) };
			p_transferVO.setMessageArguments(messageArgArray);
		} catch (BTSLBaseException be) {
			_log.errorTrace(methodName, be);
			if(mcomCon != null){mcomCon.close("VoucherConsController#processFromQueue");mcomCon=null;}
			con = null;
			try {
				if (_receiverVO != null && _receiverVO.isUnmarkRequestStatus()) {
					mcomCon = new MComConnection();con=mcomCon.getConnection();
					PretupsBL.unmarkReceiverLastRequest(con, _requestIDStr, _receiverVO);
				}
			} catch (BTSLBaseException bex) {
				_log.errorTrace(methodName, bex);
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherConsController[processFromQueue]",
						_transferID, _senderMSISDN, _senderNetworkCode, "Leaving Reciever Unmarked Exception:" + bex.getMessage());
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherConsController[processFromQueue]",
						_transferID, _senderMSISDN, _senderNetworkCode, "Leaving Reciever Unmarked Exception:" + e.getMessage());
				_p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			}

			_p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);

			if (be.isKey()) {
				if (BTSLUtil.isNullString(_p2pTransferVO.getErrorCode())) {
					_p2pTransferVO.setErrorCode(be.getMessageKey());
				}
				_p2pTransferVO.setMessageCode(be.getMessageKey());
				_p2pTransferVO.setMessageArguments(be.getArgs());
				_requestVO.setMessageCode(be.getMessageKey());
				_requestVO.setMessageArguments(be.getArgs());
			} else {
				_p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			}
			LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
			TransactionLog.log(_transferID, _requestIDStr, _requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, _p2pTransferVO
					.getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + _requestVO.getMessageCode());

		} catch (Exception e) {
			_log.error(methodName, "Exception:" + e.getMessage());
			_log.errorTrace(methodName, e);
			if(mcomCon != null){mcomCon.close("VoucherConsController#processFromQueue");mcomCon=null;}
			con = null;
			try {
				if (_receiverVO != null && _receiverVO.isUnmarkRequestStatus()) {
					mcomCon = new MComConnection();con=mcomCon.getConnection();
					PretupsBL.unmarkReceiverLastRequest(con, _requestIDStr, _receiverVO);
				}
			} catch (BTSLBaseException bex) {
				_log.errorTrace(methodName, bex);
				_p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherConsController[processFromQueue]",
						_transferID, _senderMSISDN, _senderNetworkCode, "Leaving Reciever Unmarked Exception:" + bex.getMessage());
			} catch (Exception ex1) {
				_log.errorTrace(methodName, ex1);
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherConsController[processFromQueue]",
						_transferID, _senderMSISDN, _senderNetworkCode, "Leaving Reciever Unmarked Exception:" + ex1.getMessage());
				_p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			}

			if (_recValidationFailMessageRequired) {
				if (_p2pTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _p2pTransferVO.getReceiverReturnMsg()).isKey()) {
					if (_transferID != null) {
						_p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.P2P_RECEIVER_FAIL, new String[] { String.valueOf(_transferID), PretupsBL
								.getDisplayAmount(_p2pTransferVO.getRequestedAmount()) }));
					} else {
						_p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.P2P_FAIL_R, new String[] { PretupsBL.getDisplayAmount(_p2pTransferVO
								.getRequestedAmount()) }));
					}
				}
			}

			_p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			_p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			_p2pTransferVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);

			LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);

			TransactionLog.log(_transferID, _requestIDStr, _requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, _p2pTransferVO
					.getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + _requestVO.getMessageCode());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherConsController[processFromQueue]", _transferID,
					_senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
		} finally {
			try {
				if (mcomCon == null) {
					mcomCon = new MComConnection();con=mcomCon.getConnection();
				}
				if (_transferID != null && !_transferDetailAdded) {
					addEntryInTransfers(con);
				}
			} catch (BTSLBaseException be) {
				_log.errorTrace(methodName, be);
				_log.error(methodName, "BTSL Base Exception:" + be.getMessage());
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
				_log.error(methodName, "Exception:" + e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherConsController[processFromQueue]",
						_transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
			}
			if (BTSLUtil.isNullString(_p2pTransferVO.getMessageCode())) {
				_p2pTransferVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
			}
			if (con != null) {
				try {
					con.commit();
				} catch (Exception e) {
					_log.errorTrace(methodName, e);
				}
				if(mcomCon != null){mcomCon.close("VoucherConsController#processFromQueue");mcomCon=null;}
				con = null;
			}
			if (_p2pTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) _p2pTransferVO.getReceiverReturnMsg()).isKey()) {
				final BTSLMessages btslRecMessages = (BTSLMessages) _p2pTransferVO.getReceiverReturnMsg();
				(new PushMessage(_receiverMSISDN, BTSLUtil.getMessage(_receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), _transferID,
						_p2pTransferVO.getRequestGatewayCode(), _receiverLocale)).push();
			} else if (_p2pTransferVO.getReceiverReturnMsg() != null) {
				(new PushMessage(_receiverMSISDN, (String) _p2pTransferVO.getReceiverReturnMsg(), _transferID, _p2pTransferVO.getRequestGatewayCode(), _receiverLocale))
				.push();
			}

			TransactionLog.log(_transferID, _requestVO.getRequestIDStr(), _requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
					"Leaving the controller after Queue Processing", PretupsI.TXN_LOG_STATUS_SUCCESS, "Getting Code=" + _requestVO.getMessageCode());
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exiting");
			}
		}
	}

	/**
	 * Method to populate transfer VO from request VO
	 * 
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
		if(p_requestVO.getRequestMap() != null)
		{
		_p2pTransferVO.setCellId(p_requestVO.getRequestMap().get("CELLID") != null ? (String)p_requestVO.getRequestMap().get("CELLID") : "");
		_p2pTransferVO.setSwitchId(p_requestVO.getRequestMap().get("SWITCHID") != null ? (String)p_requestVO.getRequestMap().get("SWITCHID") : "");
		_p2pTransferVO.setInfo1(p_requestVO.getRequestMap().get("INFO1") != null ? (String)p_requestVO.getRequestMap().get("INFO1") : "");
		_p2pTransferVO.setInfo2(p_requestVO.getRequestMap().get("INFO2") != null ? (String)p_requestVO.getRequestMap().get("INFO2") : "");
		_p2pTransferVO.setInfo3(p_requestVO.getRequestMap().get("INFO3") != null ? (String)p_requestVO.getRequestMap().get("INFO3") : "");
		_p2pTransferVO.setInfo4(p_requestVO.getRequestMap().get("INFO4") != null ? (String)p_requestVO.getRequestMap().get("INFO4") : "");
		_p2pTransferVO.setInfo5(p_requestVO.getRequestMap().get("INFO5") != null ? (String)p_requestVO.getRequestMap().get("INFO5") : "");
		}
	}

	/**
	 * Method to perform validation in thread
	 * 
	 * @throws BTSLBaseException
	 * @throws Exception
	 */
	private void processValidationRequestInThread() throws BTSLBaseException, Exception {
		final String methodName = "processValidationRequestInThread";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered and performing validations for transfer ID=" + _transferID);
		}
		try {
			TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
					"Performing Validation in thread", PretupsI.TXN_LOG_STATUS_SUCCESS, "");
			processValidationRequest();
		} catch (BTSLBaseException be) {
			_log.error("VoucherConsController[processValidationRequestInThread]", "Getting BTSL Base Exception:" + be.getMessage());
			TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
					"Base Exception while performing Validation in thread", PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + be.getMessageKey());
			throw be;
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
			if (_recValidationFailMessageRequired) {
				if (_p2pTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _p2pTransferVO.getReceiverReturnMsg()).isKey()) {
					_p2pTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.P2P_RECEIVER_FAIL), new String[] { String.valueOf(_transferID), PretupsBL
							.getDisplayAmount(_p2pTransferVO.getRequestedAmount()) }));
				}
			}

			_p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			if (BTSLUtil.isNullString(_p2pTransferVO.getErrorCode())) {
				_p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			}
			_log.error(this, _transferID, "Exception:" + e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherConsController[run]", _transferID,
					_senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
			TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
					"Exception while performing Validation in thread", PretupsI.TXN_LOG_STATUS_FAIL, "Getting exception =" + e.getMessage());
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
		} finally {
			if (_transferID != null && !_transferDetailAdded) {
				Connection con = null;MComConnectionI mcomCon = null;
				try {
					mcomCon = new MComConnection();con=mcomCon.getConnection();
					addEntryInTransfers(con);
					if (_p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
						_finalTransferStatusUpdate = false; // No need to update
					}

				} catch (BTSLBaseException be) {
					_log.errorTrace(methodName, be);
					_log.error(methodName, "BTSLBaseException:" + be.getMessage());
				} catch (Exception e) {
					_log.errorTrace(methodName, e);
					_log.error(methodName, "Exception:" + e.getMessage());
					EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherConsController[process]",
							_transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
				} finally {
					if(mcomCon != null){mcomCon.close("VoucherConsController#processValidationRequestInThread");mcomCon=null;}
					con = null;
				}
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exiting");
			}
		}
	}

	/**
	 * Method to get the under process message before validation to be sent to
	 * sender
	 * 
	 * @return
	 */
	private String getSndrUPMsgBeforeValidation() {
		final String[] messageArgArray = { _receiverMSISDN, _transferID, PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()) };
		return BTSLUtil.getMessage(_senderLocale, PretupsErrorCodesI.P2P_SENDER_UNDERPROCESS_B4VAL, messageArgArray);
	}

	/**
	 * Method to get the success message to be sent to sender
	 * 
	 * @return
	 */
	private String getSenderUnderProcessMessage() {
		final String[] messageArgArray = { _receiverMSISDN, _transferID, PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()), PretupsBL
				.getDisplayAmount(_p2pTransferVO.getSenderTransferValue()), PretupsBL.getDisplayAmount(_p2pTransferVO.getSenderAccessFee()) };
		return BTSLUtil.getMessage(_senderLocale, PretupsErrorCodesI.P2P_SENDER_UNDERPROCESS, messageArgArray);
	}

	/**
	 * Method that will add entry in Transfer Table if not added else update the
	 * records
	 * 
	 * @param p_con
	 */
	private void addEntryInTransfers(Connection p_con) {
		final String methodName = "addEntryInTransfers";
		try {
			TransferItemVO senderTransferItemVO = (TransferItemVO) _p2pTransferVO.getTransferItemList().get(0);
			senderTransferItemVO.setPostBalance(0);
			if (!_transferDetailAdded) {
				PretupsBL.addTransferDetails(p_con, _p2pTransferVO);// add
			} else if (_transferDetailAdded) {
				_p2pTransferVO.setModifiedOn(new Date());
				_p2pTransferVO.setModifiedBy(_p2pTransferVO.getSenderID());
				PretupsBL.updateTransferDetails(p_con, _p2pTransferVO);// add
			}
			p_con.commit();
		} catch (BTSLBaseException be) {
			_log.errorTrace(methodName, be);
			if (!_isCounterDecreased && _decreaseTransactionCounts) {
				LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
				_isCounterDecreased = true;
			}
			_log.error(methodName, _transferID, "BTSLBaseException while adding transfer details in database:" + be.getMessage());
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherConsController[process]", _transferID,
					_senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
			if (!_isCounterDecreased && _decreaseTransactionCounts) {
				LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
				_isCounterDecreased = true;
			}
			_log.error(methodName, _transferID, "Exception while adding transfer details in database:" + e.getMessage());
		}
	}

	/**
	 * Method to get the interface details based on the parameters
	 * 
	 * @param p_con
	 * @param p_msisdn
	 * @param p_prefixID
	 * @param p_subscriberType
	 * @param p_networkCode
	 * @param p_serviceType
	 *            : RC or REG etc
	 * @param p_interfaceCategory
	 *            : PRE or POST
	 * @param p_userType
	 *            : SENDER or RECEIVER
	 * @param p_action
	 *            : VALIDATE OR UPDATE
	 * @return
	 */
	private boolean getInterfaceRoutingDetails(Connection p_con, String p_msisdn, long p_prefixID, String p_subscriberType, String p_networkCode, String p_serviceType, String p_interfaceCategory, String p_userType, String p_action) throws BTSLBaseException {
		final String methodName = "getInterfaceRoutingDetails";
		if (_log.isDebugEnabled()) {
			_log.debug(
					methodName,
					_requestIDStr,
					" Entered with MSISDN=" + p_msisdn + " Prefix ID=" + p_prefixID + " p_subscriberType=" + p_subscriberType + " p_networkCode=" + p_networkCode + " p_serviceType=" + p_serviceType + " p_interfaceCategory=" + p_interfaceCategory + " p_userType=" + p_userType + " p_action=" + p_action);
		}
		boolean isSuccess = false;
		String interfaceID = null;
		String interfaceHandlerClass = null;
		String underProcessMsgReqd = null;
		String allServiceClassID = null;
		String externalID = null;
		_performIntfceCatRoutingBeforeVal = false; // Set so that receiver flag
		final SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache
				.getRoutingControlDetails(p_networkCode + "_" + p_serviceType + "_" + p_interfaceCategory);
		try {
			if (subscriberRoutingControlVO != null) {
				if (_log.isDebugEnabled()) {
					_log.debug(
							methodName,
							_transferID,
							" p_userType=" + p_userType + " Database Check Required=" + subscriberRoutingControlVO.isDatabaseCheckBool() + " Series Check Required=" + subscriberRoutingControlVO
							.isSeriesCheckBool());
				}

				if (subscriberRoutingControlVO.isDatabaseCheckBool()) {
					if (p_interfaceCategory.equalsIgnoreCase(PretupsI.INTERFACE_CATEGORY_PRE)) {
						final ListValueVO listValueVO = PretupsBL.validateNumberInRoutingDatabase(p_con, p_msisdn, p_interfaceCategory);
						if (listValueVO != null) {
							isSuccess = true;
							setInterfaceDetails(p_prefixID, p_userType, listValueVO, false, null, null);

							if (p_userType.equals(PretupsI.USER_TYPE_SENDER) && p_action.equals(PretupsI.INTERFACE_VALIDATE_ACTION)) {
								_senderInterfaceInfoInDBFound = true;
								_senderDeletionReqFromSubRouting = true;
							} else if (p_userType.equals(PretupsI.USER_TYPE_RECEIVER) && p_action.equals(PretupsI.INTERFACE_VALIDATE_ACTION)) {
								_receiverInterfaceInfoInDBFound = true;
								_receiverDeletionReqFromSubRouting = true;
							}
						} else if (subscriberRoutingControlVO.isSeriesCheckBool()) {
							if (_log.isDebugEnabled()) {
								_log.debug(methodName, _transferID,
										" p_userType=" + p_userType + " MSISDN =" + p_msisdn + " not found in Database , performing Series Check for Prefix ID=" + p_prefixID);
							}
							ServiceSelectorInterfaceMappingVO interfaceMappingVO1 = null;
							MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null;
							if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue()) {
								interfaceMappingVO1 = (ServiceSelectorInterfaceMappingVO) ServiceSelectorInterfaceMappingCache.getObject(_serviceType + "_" + _p2pTransferVO
										.getSubService() + "_" + p_action + "_" + p_networkCode + "_" + p_prefixID);
							}
							if (interfaceMappingVO1 == null) {
								try {
									interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_prefixID, _serviceType, p_action);
									isSuccess = true;
									setInterfaceDetails(p_prefixID, p_userType, null, true, interfaceMappingVO, null);
								} catch (BTSLBaseException be) {
									if (be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.MSISDN_PREFIX_INTERFACE_MAPPING_NOTFOUND))) {
										_performIntfceCatRoutingBeforeVal = true;
									} else {
										throw be;
									}
								}
							} else {
								isSuccess = true;
								setInterfaceDetails(p_prefixID, p_userType, null, true, null, interfaceMappingVO1);
							}
						} else {
							_performIntfceCatRoutingBeforeVal = true;
							isSuccess = false;
						}
					} else if (p_interfaceCategory.equalsIgnoreCase(PretupsI.INTERFACE_CATEGORY_POST)) {
						final WhiteListVO whiteListVO = PretupsBL.validateNumberInWhiteList(p_con, p_msisdn);
						if (whiteListVO != null) {
							isSuccess = true;
							final ListValueVO listValueVO = whiteListVO.getListValueVO();
							interfaceID = listValueVO.getValue();
							interfaceHandlerClass = listValueVO.getLabel();
							underProcessMsgReqd = listValueVO.getType();
							allServiceClassID = listValueVO.getTypeName();
							externalID = listValueVO.getIDValue();

							if (p_userType.equals(PretupsI.USER_TYPE_SENDER)) {
								_senderTransferItemVO.setInterfaceID(interfaceID);
								_senderTransferItemVO.setInterfaceHandlerClass(interfaceHandlerClass);
								_senderAllServiceClassID = allServiceClassID;
								_senderVO.setPostOfflineInterface(true);

								_senderTransferItemVO.setPreviousBalance(whiteListVO.getCreditLimit());
								_senderVO.setCreditLimit(whiteListVO.getCreditLimit());
								_senderTransferItemVO.setReferenceID(whiteListVO.getAccountID());
								_senderTransferItemVO.setAccountStatus(whiteListVO.getAccountStatus());
								_senderTransferItemVO.setPrefixID(p_prefixID);
								_senderTransferItemVO.setServiceClassCode(whiteListVO.getServiceClassCode());
								if (p_action.equals(PretupsI.INTERFACE_VALIDATE_ACTION)) {
									_senderInterfaceInfoInDBFound = true;
								}
							} else {
								_receiverTransferItemVO.setPrefixID(p_prefixID);
								_receiverTransferItemVO.setInterfaceID(interfaceID);
								_receiverTransferItemVO.setInterfaceHandlerClass(interfaceHandlerClass);
								if (PretupsI.YES.equals(underProcessMsgReqd)) {
									_p2pTransferVO.setUnderProcessMsgReq(true);
								}
								_receiverAllServiceClassID = allServiceClassID;
								_receiverExternalID = externalID;
								_receiverVO.setPostOfflineInterface(true);

								_receiverTransferItemVO.setPreviousBalance(whiteListVO.getCreditLimit());
								_receiverTransferItemVO.setServiceClassCode(whiteListVO.getServiceClassCode());
								_receiverTransferItemVO.setReferenceID(whiteListVO.getAccountID());
								_receiverTransferItemVO.setAccountStatus(whiteListVO.getAccountStatus());
								if (p_action.equals(PretupsI.INTERFACE_VALIDATE_ACTION)) {
									_receiverInterfaceInfoInDBFound = true;
								}
							}
							if (!PretupsI.YES.equals(listValueVO.getStatus())) {
								if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(_senderLocale)).getMessage())) {
									_p2pTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo());
								} else {
									_p2pTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo2());
								}
								throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INTERFACE_NOT_ACTIVE);
							}
						} else if (subscriberRoutingControlVO.isSeriesCheckBool()) {
							if (_log.isDebugEnabled()) {
								_log.debug(methodName, _transferID,
										" p_userType=" + p_userType + " MSISDN =" + p_msisdn + " not found in Database , performing Series Check for Prefix ID=" + p_prefixID);
							}

							MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null;
							ServiceSelectorInterfaceMappingVO interfaceMappingVO1 = null;
							if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue()) {
								interfaceMappingVO1 = (ServiceSelectorInterfaceMappingVO) ServiceSelectorInterfaceMappingCache.getObject(_serviceType + "_" + _p2pTransferVO
										.getSubService() + "_" + p_action + "_" + p_networkCode + "_" + p_prefixID);
							}
							if (interfaceMappingVO1 == null) {
								try {
									interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_prefixID, _serviceType, p_action);
									isSuccess = true;
									setInterfaceDetails(p_prefixID, p_userType, null, true, interfaceMappingVO, null);
								} catch (BTSLBaseException be) {
									if (be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.MSISDN_PREFIX_INTERFACE_MAPPING_NOTFOUND))) {
										_performIntfceCatRoutingBeforeVal = true;
									} else {
										throw be;
									}
								}
							} else {
								isSuccess = true;
								setInterfaceDetails(p_prefixID, p_userType, null, true, null, interfaceMappingVO1);
							}
						} else {
							isSuccess = false;
							_performIntfceCatRoutingBeforeVal = true;
						}
					}
				} else if (subscriberRoutingControlVO.isSeriesCheckBool()) {
					if (_log.isDebugEnabled()) {
						_log.debug(methodName, _transferID, " p_userType=" + p_userType + " MSISDN =" + p_msisdn + " performing Series Check for Prefix ID=" + p_prefixID);
					}

					MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null;
					ServiceSelectorInterfaceMappingVO interfaceMappingVO1 = null;
					if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue()) {
						interfaceMappingVO1 = (ServiceSelectorInterfaceMappingVO) ServiceSelectorInterfaceMappingCache.getObject(_serviceType + "_" + _p2pTransferVO
								.getSubService() + "_" + p_action + "_" + p_networkCode + "_" + p_prefixID);
					}
					if (interfaceMappingVO1 == null) {
						try {
							interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_prefixID, _serviceType, p_action);
							isSuccess = true;
							setInterfaceDetails(p_prefixID, p_userType, null, true, interfaceMappingVO, null);
						} catch (BTSLBaseException be) {
							if (be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.MSISDN_PREFIX_INTERFACE_MAPPING_NOTFOUND))) {
								_performIntfceCatRoutingBeforeVal = true;
							} else {
								throw be;
							}
						}
					} else {
						isSuccess = true;
						setInterfaceDetails(p_prefixID, p_userType, null, true, null, interfaceMappingVO1);
					}
				} else {
					isSuccess = false;
				}
			} else {
				if (_log.isDebugEnabled()) {
					_log.debug(
							methodName,
							_transferID,
							" By default carrying out series check as routing control not defined for p_userType=" + p_userType + " MSISDN =" + p_msisdn + " performing Series Check for Prefix ID=" + p_prefixID);
				}
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherConsController[getInterfaceRoutingDetails]",
						_transferID, _senderMSISDN, _senderNetworkCode, "Exception:Routing control information not defined so performing series based routing");

				MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null;
				ServiceSelectorInterfaceMappingVO interfaceMappingVO1 = null;
				if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue()) {
					interfaceMappingVO1 = (ServiceSelectorInterfaceMappingVO) ServiceSelectorInterfaceMappingCache.getObject(_serviceType + "_" + _p2pTransferVO
							.getSubService() + "_" + p_action + "_" + p_networkCode + "_" + p_prefixID);
				}
				if (interfaceMappingVO1 == null) {
					try {
						interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_prefixID, _serviceType, p_action);
						isSuccess = true;
						setInterfaceDetails(p_prefixID, p_userType, null, true, interfaceMappingVO, null);
					} catch (BTSLBaseException be) {
						if (be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.MSISDN_PREFIX_INTERFACE_MAPPING_NOTFOUND))) {
							_performIntfceCatRoutingBeforeVal = true;
						} else {
							throw be;
						}
					}
				} else {
					isSuccess = true;
					setInterfaceDetails(p_prefixID, p_userType, null, true, null, interfaceMappingVO1);
				}
			}

			if (isSuccess && p_userType.equals(PretupsI.USER_TYPE_SENDER)) {
				_senderTransferItemVO.setInterfaceType(_type);
			} else if (isSuccess && p_userType.equals(PretupsI.USER_TYPE_RECEIVER)) {
				_receiverTransferItemVO.setInterfaceType(_type);
				_receiverTransferItemVO.setSubscriberType(_type);
			}
		} catch (BTSLBaseException be) {
			if (isSuccess && p_userType.equals(PretupsI.USER_TYPE_SENDER)) {
				_senderTransferItemVO.setInterfaceType(p_interfaceCategory);
			} else if (isSuccess && p_userType.equals(PretupsI.USER_TYPE_RECEIVER)) {
				_receiverTransferItemVO.setInterfaceType(_type);
				_receiverTransferItemVO.setSubscriberType(_type);
			}
			throw be;
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherConsController[getInterfaceRoutingDetails]",
					_transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
			isSuccess = false;
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
		}
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, _requestIDStr,
					" Exiting with isSuccess=" + isSuccess + "_senderAllServiceClassID=" + _senderAllServiceClassID + " _receiverAllServiceClassID=" + _receiverAllServiceClassID);
		}
		return isSuccess;
	}

	/**
	 * Method: updateReceiverLocale
	 * This method update the receiver locale with the language code returned
	 * from the IN
	 * 
	 * @param p_languageCode
	 *            String
	 * @return void
	 */
	public void updateReceiverLocale(String p_languageCode) {
		final String methodName = "updateReceiverLocale";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered p_languageCode=" + p_languageCode);
		}
		if (!BTSLUtil.isNullString(p_languageCode)) {
			try {
				if (LocaleMasterCache.getLocaleFromCodeDetails(p_languageCode) == null) {
					EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherConsController[updateReceiverLocale]",
							_transferID, _receiverMSISDN, "", "Exception: Notification language returned from IN is not defined in system p_languageCode: " + p_languageCode);
				} else {
					_receiverLocale = (LocaleMasterCache.getLocaleFromCodeDetails(p_languageCode));
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
		}
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Exited _receiverLocale=" + _receiverLocale);
		}
	}

	/**
	 * This method will perform the alternate interface category routing if
	 * there
	 * This method will be called either after validation or after performing
	 * interface routing
	 * 
	 * @throws BTSLBaseException
	 */
	public void performAlternateCategoryRouting() throws BTSLBaseException {
		final String methodName = "performAlternateCategoryRouting";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Performing ALternate interface category routing Entered");
		}
		Connection con = null;MComConnectionI mcomCon = null;
		try {
			String requestStr = null;
			CommonClient commonClient = null;
			String receiverValResponse = null;

			LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.RECEIVER_VAL_RESPONSE);
			LoadController.decreaseReceiverTransactionInterfaceLoad(_transferID, _p2pTransferVO.getReceiverNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);

			mcomCon = new MComConnection();con=mcomCon.getConnection();

			populateAlternateInterfaceDetails(con);

			if (con != null) {
				try {
					con.rollback();
				} catch (Exception be) {
					_log.errorTrace(methodName, be);
				}
				if (mcomCon != null) {
					mcomCon.close("VoucherConsController#performAlternateCategoryRouting");
					mcomCon = null;
				}
				con = null;
			}
			_p2pTransferVO.setTransferCategory(_senderSubscriberType + "-" + _type);
			if (_log.isDebugEnabled()) {
				_log.debug("process", _requestIDStr, "Overriding transfer Category as :" + _p2pTransferVO.getTransferCategory());
			}

			_p2pTransferVO.setReceiverAllServiceClassID(_receiverAllServiceClassID);

			checkTransactionLoad(PretupsI.USER_TYPE_RECEIVER, _receiverTransferItemVO.getInterfaceID());

			PretupsBL.validateRecieverLimits(null, _p2pTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL, PretupsI.P2P_MODULE);

			requestStr = getReceiverValidateStr();
			commonClient = new CommonClient();

			LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.RECEIVER_UNDER_VAL);

			TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL,
					requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "Performing Alternate Category Routing");

			receiverValResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);

			TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL,
					receiverValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

			handleReceiverValidateResponse(receiverValResponse, SRC_AFTER_INRESP_CAT_ROUTING);
			if (InterfaceErrorCodesI.SUCCESS.equals(_receiverTransferItemVO.getValidationStatus())) {
				if (_newInterfaceCategory.equals(PretupsI.INTERFACE_CATEGORY_POST)) {
					if (_receiverDeletionReqFromSubRouting) {
						PretupsBL.deleteSubscriberInterfaceRouting(_receiverMSISDN, _oldInterfaceCategory);
					}
				} else {
					final SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(_p2pTransferVO
							.getReceiverNetworkCode() + "_" + _p2pTransferVO.getServiceType() + "_" + _newInterfaceCategory);
					if (!_receiverDeletionReqFromSubRouting && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
						if (_log.isDebugEnabled()) {
							_log.debug(methodName, "Inserting the MSISDN=" + _receiverMSISDN + " in Subscriber routing database for further usage");
						}

						PretupsBL.insertSubscriberInterfaceRouting(_receiverTransferItemVO.getInterfaceID(), _receiverExternalID, _receiverMSISDN, _newInterfaceCategory,
								_senderVO.getUserID(), _currentDate);
						_receiverInterfaceInfoInDBFound = true;
						_receiverDeletionReqFromSubRouting = true;
					}
				}
			}
		} catch (BTSLBaseException be) {
			throw be;
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"VoucherConsController[performAlternateCategoryRouting]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
		} finally {
			if (con != null) {
				try {
					con.rollback();
				} catch (Exception be) {
					_log.errorTrace(methodName, be);
				}
				if(mcomCon != null){mcomCon.close("VoucherConsController#performAlternateCategoryRouting");mcomCon=null;}
				con = null;
			}
		}
	}

	/**
	 * Method to populate the Alternate Interface Details for the Receiver
	 * against the new interface category
	 * 
	 * @throws BTSLBaseException
	 */
	public void populateAlternateInterfaceDetails(Connection p_con) throws BTSLBaseException {
		if (_log.isDebugEnabled()) {
			_log.debug("populateAlternateInterfaceDetails", "Entered to get the alternate category");
		}

		boolean isReceiverFound = false;

		if (!_interfaceCatRoutingDone) {
			_interfaceCatRoutingDone = true;
			_type = _newInterfaceCategory;
			_networkPrefixVO = null;

			_requestVO.setReqSelector(_newDefaultSelector);
			_p2pTransferVO.setSubService(_newDefaultSelector);

			if (_log.isDebugEnabled()) {
				_log.debug("populateAlternateInterfaceDetails", "Got the alternate category as =" + _type);
			}

			_networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(_receiverVO.getMsisdnPrefix(), _type);
			if (_networkPrefixVO != null) {
				if (_log.isDebugEnabled()) {
					_log.debug("populateAlternateInterfaceDetails", "Got the Prefix ID for MSISDN=" + _receiverMSISDN + "Prefix ID=" + _networkPrefixVO.getPrefixID());
				}

				_receiverVO.setNetworkCode(_networkPrefixVO.getNetworkCode());
				_receiverVO.setPrefixID(_networkPrefixVO.getPrefixID());
				_receiverVO.setSubscriberType(_networkPrefixVO.getSeriesType());
				isReceiverFound = getInterfaceRoutingDetails(p_con, _receiverMSISDN, _receiverVO.getPrefixID(), _receiverVO.getSubscriberType(), _receiverVO.getNetworkCode(),
						_p2pTransferVO.getServiceType(), _type, PretupsI.USER_TYPE_RECEIVER, PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION);
			} else {
				_log.error(this, "Series Not Defined for Alternate Interface =" + _type + " For Series=" + _receiverVO.getMsisdnPrefix());
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO,
						"VoucherConsController[populateAlternateInterfaceDetails]", "", "", "",
						"Series =" + _receiverVO.getMsisdnPrefix() + " Not Defined for Series type=" + _type + " But required for validation");
				isReceiverFound = false;
			}

			if (!isReceiverFound) {
				throw new BTSLBaseException("VoucherConsController", "populateServicePaymentInterfaceDetails", PretupsErrorCodesI.P2P_NOTFOUND_SERVICEINTERFACEMAPPING);
			}
		}
	}

	/**
	 * This method handles the receiver validate response after sending request
	 * to IN
	 * 
	 * @param str
	 * @param p_source
	 * @throws BTSLBaseException
	 */
	public void handleReceiverValidateResponse(String str, int p_source) throws BTSLBaseException {
		final HashMap map = BTSLUtil.getStringToHash(str, "&", "=");
		final String status = (String) map.get("TRANSACTION_STATUS");
		ArrayList altList = null;
		boolean isRequired = false;
		if (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status)) {
			_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			altList = InterfaceRoutingControlCache.getRoutingControlDetails(_receiverTransferItemVO.getInterfaceID());
			if (altList != null && !altList.isEmpty()) {
				performReceiverAlternateRouting(altList, p_source);
			} else {
				isRequired = true;
			}
		}
		if (!InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) || isRequired) {
			populateReceiverItemsDetails(map);
			_receiverTransferItemVO.setServiceProviderName(BTSLUtil.NullToString((String) map.get("SPNAME")));
		}
	}

	/**
	 * Method to perform the Interface routing for the subscriber MSISDN
	 * 
	 * @param altList
	 * @param p_source
	 *            : Determines whether Alternate category needs to be performed
	 *            after this or not
	 * @throws BTSLBaseException
	 */
	private void performReceiverAlternateRouting(ArrayList altList, int p_source) throws BTSLBaseException {
		final String methodName = "performReceiverAlternateRouting";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, _requestIDStr, " Entered p_source=" + p_source);
		}
		try {
			if (altList != null && !altList.isEmpty()) {
				ListValueVO listValueVO = null;
				String requestStr = null;
				CommonClient commonClient = null;
				String receiverValResponse = null;
				switch (altList.size()) {
				case 1:
				{
					LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.RECEIVER_VAL_RESPONSE);
					LoadController
					.decreaseReceiverTransactionInterfaceLoad(_transferID, _p2pTransferVO.getReceiverNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);

					listValueVO = (ListValueVO) altList.get(0);

					setInterfaceDetails(_receiverTransferItemVO.getPrefixID(), PretupsI.USER_TYPE_RECEIVER, listValueVO, false, null, null);

					checkTransactionLoad(PretupsI.USER_TYPE_RECEIVER, _receiverTransferItemVO.getInterfaceID());

					PretupsBL.validateRecieverLimits(null, _p2pTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL, PretupsI.P2P_MODULE);

					requestStr = getReceiverValidateStr();
					commonClient = new CommonClient();

					LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.RECEIVER_UNDER_VAL);

					TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ,
							PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "Performing Interface Routing 1");

					if (_log.isDebugEnabled()) {
						_log.debug(methodName, "Sending Request For MSISDN=" + _receiverMSISDN + " on ALternate Routing 1 to =" + _receiverTransferItemVO
								.getInterfaceID());
					}

					receiverValResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);

					TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES,
							PretupsI.TXN_LOG_TXNSTAGE_INVAL, receiverValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

					try {
						receiverValidateResponse(receiverValResponse, 1, altList.size(), p_source);
						if (p_source == SRC_BEFORE_INRESP_CAT_ROUTING) {
							if (PretupsI.INTERFACE_CATEGORY_PRE.equals(_type) && InterfaceErrorCodesI.SUCCESS.equals(_receiverTransferItemVO.getValidationStatus())) {
								updateSubscriberRoutingDetails(PretupsI.USER_TYPE_RECEIVER, _p2pTransferVO.getReceiverNetworkCode(), _receiverTransferItemVO
										.getInterfaceID(), _receiverExternalID, _receiverMSISDN, _type, _senderVO.getUserID(), _currentDate);
							}
						} else {
							if (InterfaceErrorCodesI.SUCCESS.equals(_receiverTransferItemVO.getValidationStatus())) {
								if (_newInterfaceCategory.equals(PretupsI.INTERFACE_CATEGORY_POST)) {
									if (_receiverDeletionReqFromSubRouting) {
										PretupsBL.deleteSubscriberInterfaceRouting(_receiverMSISDN, _oldInterfaceCategory);
									}
								} else {
									final SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache
											.getRoutingControlDetails(_p2pTransferVO.getReceiverNetworkCode() + "_" + _p2pTransferVO.getServiceType() + "_" + _newInterfaceCategory);
									if (!_receiverDeletionReqFromSubRouting && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
										PretupsBL.insertSubscriberInterfaceRouting(_receiverTransferItemVO.getInterfaceID(), _receiverExternalID, _receiverMSISDN,
												_newInterfaceCategory, _senderVO.getUserID(), _currentDate);
										_receiverInterfaceInfoInDBFound = true;
										_receiverDeletionReqFromSubRouting = true;
									}
								}
							}
						}
					} catch (BTSLBaseException be) {
						throw be;
					} catch (Exception e) {
						throw (BTSLBaseException)e;
					}

					break;
				}
				case 2:
				{
					LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.RECEIVER_VAL_RESPONSE);
					LoadController
					.decreaseReceiverTransactionInterfaceLoad(_transferID, _p2pTransferVO.getReceiverNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);

					listValueVO = (ListValueVO) altList.get(0);

					setInterfaceDetails(_receiverTransferItemVO.getPrefixID(), PretupsI.USER_TYPE_RECEIVER, listValueVO, false, null, null);

					checkTransactionLoad(PretupsI.USER_TYPE_RECEIVER, _receiverTransferItemVO.getInterfaceID());

					PretupsBL.validateRecieverLimits(null, _p2pTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL, PretupsI.P2P_MODULE);

					requestStr = getReceiverValidateStr();
					commonClient = new CommonClient();

					LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.RECEIVER_UNDER_VAL);

					TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ,
							PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "Performing Interface Routing 1");

					if (_log.isDebugEnabled()) {
						_log.debug(methodName, "Sending Request For MSISDN=" + _receiverMSISDN + " on ALternate Routing 1 to =" + _receiverTransferItemVO
								.getInterfaceID());
					}

					receiverValResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);

					TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES,
							PretupsI.TXN_LOG_TXNSTAGE_INVAL, receiverValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

					try {
						receiverValidateResponse(receiverValResponse, 1, altList.size(), p_source);
						if (p_source == SRC_BEFORE_INRESP_CAT_ROUTING) {
							if (PretupsI.INTERFACE_CATEGORY_PRE.equals(_type) && InterfaceErrorCodesI.SUCCESS.equals(_receiverTransferItemVO.getValidationStatus())) {
								// Update in DB for routing interface
								updateSubscriberRoutingDetails(PretupsI.USER_TYPE_RECEIVER, _p2pTransferVO.getReceiverNetworkCode(), _receiverTransferItemVO
										.getInterfaceID(), _receiverExternalID, _receiverMSISDN, _type, _senderVO.getUserID(), _currentDate);
							}
						} else {
							if (InterfaceErrorCodesI.SUCCESS.equals(_receiverTransferItemVO.getValidationStatus())) {
								if (_newInterfaceCategory.equals(PretupsI.INTERFACE_CATEGORY_POST)) {
									if (_receiverDeletionReqFromSubRouting) {
										PretupsBL.deleteSubscriberInterfaceRouting(_receiverMSISDN, _oldInterfaceCategory);
									}
								} else {
									final SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache
											.getRoutingControlDetails(_p2pTransferVO.getReceiverNetworkCode() + "_" + _p2pTransferVO.getServiceType() + "_" + _newInterfaceCategory);
									if (!_receiverDeletionReqFromSubRouting && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
										PretupsBL.insertSubscriberInterfaceRouting(_receiverTransferItemVO.getInterfaceID(), _receiverExternalID, _receiverMSISDN,
												_newInterfaceCategory, _senderVO.getUserID(), _currentDate);
										_receiverInterfaceInfoInDBFound = true;
										_receiverDeletionReqFromSubRouting = true;
									}
								}
							}
						}
					} catch (BTSLBaseException be) {
						if (be.isKey() && InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(be.getMessageKey())) {
							if (_log.isDebugEnabled()) {
								_log.debug(
										methodName,
										"Got Status=" + InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND + " After validation Request For MSISDN=" + _receiverMSISDN + " Performing Alternate Routing to 2");
							}

							LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.RECEIVER_VAL_RESPONSE);
							LoadController.decreaseReceiverTransactionInterfaceLoad(_transferID, _p2pTransferVO.getReceiverNetworkCode(),
									LoadControllerI.DEC_LAST_TRANS_COUNT);

							listValueVO = (ListValueVO) altList.get(1);

							setInterfaceDetails(_receiverTransferItemVO.getPrefixID(), PretupsI.USER_TYPE_RECEIVER, listValueVO, false, null, null);

							checkTransactionLoad(PretupsI.USER_TYPE_RECEIVER, _receiverTransferItemVO.getInterfaceID());

							PretupsBL.validateRecieverLimits(null, _p2pTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL, PretupsI.P2P_MODULE);

							requestStr = getReceiverValidateStr();

							LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.RECEIVER_UNDER_VAL);

							TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ,
									PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "Performing Interface Routing 2");

							if (_log.isDebugEnabled()) {
								_log.debug(methodName, "Sending Request For MSISDN=" + _receiverMSISDN + " on ALternate Routing 2 to =" + _receiverTransferItemVO
										.getInterfaceID());
							}

							receiverValResponse = commonClient
									.process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);

							TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES,
									PretupsI.TXN_LOG_TXNSTAGE_INVAL, receiverValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

							try {
								receiverValidateResponse(receiverValResponse, 2, altList.size(), p_source);

								if (p_source == SRC_BEFORE_INRESP_CAT_ROUTING) {
									if (PretupsI.INTERFACE_CATEGORY_PRE.equals(_type) && InterfaceErrorCodesI.SUCCESS.equals(_receiverTransferItemVO
											.getValidationStatus())) {
										updateSubscriberRoutingDetails(PretupsI.USER_TYPE_RECEIVER, _p2pTransferVO.getReceiverNetworkCode(), _receiverTransferItemVO
												.getInterfaceID(), _receiverExternalID, _receiverMSISDN, _type, _senderVO.getUserID(), _currentDate);
									}
								} else {
									if (InterfaceErrorCodesI.SUCCESS.equals(_receiverTransferItemVO.getValidationStatus())) {
										if (_newInterfaceCategory.equals(PretupsI.INTERFACE_CATEGORY_POST)) {
											if (_receiverDeletionReqFromSubRouting) {
												PretupsBL.deleteSubscriberInterfaceRouting(_receiverMSISDN, _oldInterfaceCategory);
											}
										} else {
											final SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache
													.getRoutingControlDetails(_p2pTransferVO.getReceiverNetworkCode() + "_" + _p2pTransferVO.getServiceType() + "_" + _newInterfaceCategory);
											if (!_receiverDeletionReqFromSubRouting && subscriberRoutingControlVO != null && subscriberRoutingControlVO
													.isDatabaseCheckBool()) {
												PretupsBL.insertSubscriberInterfaceRouting(_receiverTransferItemVO.getInterfaceID(), _receiverExternalID,
														_receiverMSISDN, _newInterfaceCategory, _senderVO.getUserID(), _currentDate);
												_receiverInterfaceInfoInDBFound = true;
												_receiverDeletionReqFromSubRouting = true;
											}
										}
									}
								}
							} catch (BTSLBaseException bex) {
								throw new BTSLBaseException(bex);
							} catch (Exception e) {
								throw (BTSLBaseException)e;
							}
						} else {
							throw be;
						}
					} catch (Exception e) {
						throw (BTSLBaseException)e;
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
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherConsController[performAlternateRouting]",
					_transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
		}
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, _requestIDStr, " Exiting ");
		}
	}

	/**
	 * This method validates the response from Interfaces in interface routing
	 * 
	 * @param str
	 * @param p_attempt
	 * @param p_altSize
	 * @param p_source
	 * @throws BTSLBaseException
	 */
	public void receiverValidateResponse(String str, int p_attempt, int p_altSize, int p_source) throws BTSLBaseException {
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
			throw new BTSLBaseException(this, "receiverValidateResponse", InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
		} else if (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) && p_attempt == p_altSize && p_source == SRC_BEFORE_INRESP_CAT_ROUTING && _useAlternateCategory && !_interfaceCatRoutingDone) {
			if (_log.isDebugEnabled()) {
				_log.debug(this, " Performing Alternate category routing as MSISDN not found on any interfaces after routing for " + _receiverMSISDN);
			}
			performAlternateCategoryRouting();
		} else {
			if ("Y".equals(_requestVO.getUseInterfaceLanguage())) {
				updateReceiverLocale((String) map.get("IN_LANG"));
			}
			_receiverTransferItemVO.setProtocolStatus((String) map.get("PROTOCOL_STATUS"));
			_receiverTransferItemVO.setAccountStatus((String) map.get("ACCOUNT_STATUS"));
			_receiverTransferItemVO.setInterfaceResponseCode((String) map.get("INTERFACE_STATUS"));
			_receiverTransferItemVO.setValidationStatus(status);
			_receiverVO.setInterfaceResponseCode(_receiverTransferItemVO.getInterfaceResponseCode());
			_receiverTransferItemVO.setInterfaceReferenceID((String) map.get("IN_TXN_ID"));
			_receiverTransferItemVO.setReferenceID((String) map.get("IN_RECON_ID"));
			String[] strArr = null;

			if (BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS)) {
				_p2pTransferVO.setErrorCode(status + "_R");
				_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
				_receiverTransferItemVO.setTransferStatus(status);
				_senderTransferItemVO.setValidationStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
				_senderTransferItemVO.setTransferStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
				_senderVO.setTransactionStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
				strArr = new String[] { _receiverMSISDN, PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()), _transferID };
				throw new BTSLBaseException("VoucherConsController", "receiverValidateResponse", PretupsErrorCodesI.P2P_SENDER_FAIL, 0, strArr, null);
			}
			_receiverTransferItemVO.setTransferStatus(status);
			_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
			_receiverTransferItemVO.setSubscriberType(_type);
			_receiverVO.setSubscriberType(_type);

			try {
				_receiverTransferItemVO.setPreviousExpiry(BTSLUtil.getDateFromDateString((String) map.get("OLD_EXPIRY_DATE"), "ddMMyyyy"));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			;
			try {
				_receiverTransferItemVO.setPreviousGraceDate(BTSLUtil.getDateFromDateString((String) map.get("OLD_GRACE_DATE"), "ddMMyyyy"));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			_receiverVO.setInterfaceResponseCode(_receiverTransferItemVO.getInterfaceResponseCode());

			_receiverTransferItemVO.setFirstCall((String) map.get("FIRST_CALL"));
			_receiverTransferItemVO.setGraceDaysStr((String) map.get("GRACE_DAYS"));

			_receiverTransferItemVO.setServiceClassCode((String) map.get("SERVICE_CLASS"));

			_receiverVO.setServiceClassCode(_receiverTransferItemVO.getServiceClass());
			_senderVO.setServiceClassCode(_receiverTransferItemVO.getServiceClass());
			try {
				_receiverTransferItemVO.setPreviousBalance(Long.parseLong((String) map.get("INTERFACE_PREV_BALANCE")));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			if (_receiverVO.isPostOfflineInterface()) {
				final boolean isPeriodChange = BTSLUtil.isPeriodChangeBetweenDates(_receiverVO.getLastSuccessOn(), _currentDate, BTSLUtil.PERIOD_MONTH);
				if (!isPeriodChange) {
					_receiverTransferItemVO.setPreviousBalance(_receiverTransferItemVO.getPreviousBalance() - _receiverVO.getMonthlyTransferAmount());
				}
			}

			if (_receiverTransferItemVO.getPreviousExpiry() == null) {
				_receiverTransferItemVO.setPreviousExpiry(_currentDate);
			}
		}
	}

	/**
	 * This method will populate the receiver Items VO after the response from
	 * interfaces
	 * 
	 * @param p_map
	 * @throws BTSLBaseException
	 */
	public void populateReceiverItemsDetails(HashMap p_map) throws BTSLBaseException {
		final String methodName = "populateReceiverItemsDetails";
		final String status = (String) p_map.get("TRANSACTION_STATUS");
		if ("Y".equals(_requestVO.getUseInterfaceLanguage())) {
			updateReceiverLocale((String) p_map.get("IN_LANG"));
		}
		_receiverTransferItemVO.setProtocolStatus((String) p_map.get("PROTOCOL_STATUS"));
		_receiverTransferItemVO.setAccountStatus((String) p_map.get("ACCOUNT_STATUS"));
		_receiverTransferItemVO.setInterfaceResponseCode((String) p_map.get("INTERFACE_STATUS"));
		_receiverTransferItemVO.setValidationStatus(status);
		_receiverVO.setInterfaceResponseCode(_receiverTransferItemVO.getInterfaceResponseCode());

		if (!BTSLUtil.isNullString((String) p_map.get("IN_TXN_ID"))) {
			try {
				_receiverTransferItemVO.setInterfaceReferenceID((String) p_map.get("IN_TXN_ID"));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
						"VoucherConsController[updateForReceiverValidateResponse]", _transferID, _senderMSISDN, _senderNetworkCode,
						"Exception while parsing for interface txn ID , Exception:" + e.getMessage());
			}
		}
		_receiverTransferItemVO.setReferenceID((String) p_map.get("IN_RECON_ID"));

		String[] strArr = null;

		if (BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS)) {
			_p2pTransferVO.setErrorCode(status + "_R");
			_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			_receiverTransferItemVO.setTransferStatus(status);

			_senderVO.setTransactionStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
			strArr = new String[] { _receiverMSISDN, PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()), _transferID };
			if (InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED_P2P.equals(_receiverTransferItemVO.getValidationStatus())) {
				throw new BTSLBaseException("VoucherConsController", "populateReceiverItemsDetails", InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED_P2P + "_S", 0,
						strArr, null);
			} else {
				throw new BTSLBaseException("VoucherConsController", "populateReceiverItemsDetails", _p2pTransferVO.getErrorCode(), 0, strArr, null);
			}
		}
		_receiverTransferItemVO.setTransferStatus(status);
		_receiverTransferItemVO.setSubscriberType(_type);
		_receiverVO.setSubscriberType(_type);
		_p2pTransferVO.setRequestedAmount(Long.parseLong(String.valueOf(p_map.get("TALK_TIME"))));
		_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
		final SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache
				.getRoutingControlDetails(_p2pTransferVO.getReceiverNetworkCode() + "_" + _p2pTransferVO.getServiceType() + "_" + _type);
		if (PretupsI.INTERFACE_CATEGORY_PRE.equals(_type) && !_receiverDeletionReqFromSubRouting && subscriberRoutingControlVO != null && subscriberRoutingControlVO
				.isDatabaseCheckBool()) {
			PretupsBL.insertSubscriberInterfaceRouting(_receiverTransferItemVO.getInterfaceID(), _receiverExternalID, _receiverMSISDN, _type, _senderVO.getUserID(),
					_currentDate);
			_receiverInterfaceInfoInDBFound = true;
			_receiverDeletionReqFromSubRouting = true;
		}

		try {
			_receiverTransferItemVO.setPreviousExpiry(BTSLUtil.getDateFromDateString((String) p_map.get("OLD_EXPIRY_DATE"), "ddMMyyyy"));
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		;
		try {
			_receiverTransferItemVO.setPreviousGraceDate(BTSLUtil.getDateFromDateString((String) p_map.get("OLD_GRACE_DATE"), "ddMMyyyy"));
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		;
		_receiverTransferItemVO.setFirstCall((String) p_map.get("FIRST_CALL"));
		_receiverTransferItemVO.setGraceDaysStr((String) p_map.get("GRACE_DAYS"));

		_receiverTransferItemVO.setServiceClassCode((String) p_map.get("SERVICE_CLASS"));
		_receiverTransferItemVO.setOldExporyInMillis((String) p_map.get("CAL_OLD_EXPIRY_DATE"));// @nu

		try {
			_receiverTransferItemVO.setPreviousBalance(Long.parseLong((String) p_map.get("INTERFACE_PREV_BALANCE")));
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		;

		_receiverTransferItemVO.setBundleTypes((String) p_map.get("BUNDLE_TYPES"));
		_receiverTransferItemVO.setBonusBundleValidities((String) p_map.get("BONUS_BUNDLE_VALIDITIES"));

		if (_receiverVO.isPostOfflineInterface()) {
			final boolean isPeriodChange = BTSLUtil.isPeriodChangeBetweenDates(_receiverVO.getLastSuccessOn(), _currentDate, BTSLUtil.PERIOD_MONTH);
			if (!isPeriodChange) {
				_receiverTransferItemVO.setPreviousBalance(_receiverTransferItemVO.getPreviousBalance() - _receiverVO.getMonthlyTransferAmount());
			}
		}
		if (_receiverTransferItemVO.getPreviousExpiry() == null) {
			_receiverTransferItemVO.setPreviousExpiry(_currentDate);
		}

		try {
			_receiverTransferItemVO.setLmbdebitvalue((Long.valueOf((String) p_map.get("LMB_ALLOWED_VALUE"))));
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
			_log.error("populateReceiverItemsDetails", "Exception e" + e);
		}
	}

	/**
	 * This method sets the Interface Details based on the VOs values.
	 * If p_useInterfacePrefixVO is True then use
	 * p_MSISDNPrefixInterfaceMappingVO else use p_listValueVO to populate
	 * values
	 * 
	 * @param p_prefixID
	 * @param p_userType
	 * @param p_listValueVO
	 * @param p_useInterfacePrefixVO
	 * @param p_MSISDNPrefixInterfaceMappingVO
	 * @throws BTSLBaseException
	 */
	private void setInterfaceDetails(long p_prefixID, String p_userType, ListValueVO p_listValueVO, boolean p_useInterfacePrefixVO, MSISDNPrefixInterfaceMappingVO p_MSISDNPrefixInterfaceMappingVO, ServiceSelectorInterfaceMappingVO p_serviceSelectorInterfaceMappingVO) throws BTSLBaseException {
		if (_log.isDebugEnabled()) {
			_log.debug(
					"setInterfaceDetails",
					_requestIDStr,
					" Entered p_prefixID=" + p_prefixID + " p_listValueVO=" + p_listValueVO + " p_useInterfacePrefixVO=" + p_useInterfacePrefixVO + " p_MSISDNPrefixInterfaceMappingVO=" + p_MSISDNPrefixInterfaceMappingVO + "p_serviceSelectorInterfaceMappingVO" + p_serviceSelectorInterfaceMappingVO);
		}
		try {
			String interfaceID = null;
			String interfaceHandlerClass = null;
			String underProcessMsgReqd = null;
			String allServiceClassID = null;
			String externalID = null;
			String status = null;
			String message1 = null;
			String message2 = null;
			String interfaceStatusTy = null;
			if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue() && p_serviceSelectorInterfaceMappingVO != null) {
				interfaceID = p_serviceSelectorInterfaceMappingVO.getInterfaceID();
				interfaceHandlerClass = p_serviceSelectorInterfaceMappingVO.getHandlerClass();
				underProcessMsgReqd = p_serviceSelectorInterfaceMappingVO.getUnderProcessMsgRequired();
				allServiceClassID = p_serviceSelectorInterfaceMappingVO.getAllServiceClassID();
				externalID = p_serviceSelectorInterfaceMappingVO.getExternalID();
				status = p_serviceSelectorInterfaceMappingVO.getInterfaceStatus();
				message1 = p_serviceSelectorInterfaceMappingVO.getLanguage1Message();
				message2 = p_serviceSelectorInterfaceMappingVO.getLanguage2Message();
				interfaceStatusTy = p_serviceSelectorInterfaceMappingVO.getStatusType();
			}
			if (p_useInterfacePrefixVO && p_serviceSelectorInterfaceMappingVO == null) {
				interfaceID = p_MSISDNPrefixInterfaceMappingVO.getInterfaceID();
				interfaceHandlerClass = p_MSISDNPrefixInterfaceMappingVO.getHandlerClass();
				underProcessMsgReqd = p_MSISDNPrefixInterfaceMappingVO.getUnderProcessMsgRequired();
				allServiceClassID = p_MSISDNPrefixInterfaceMappingVO.getAllServiceClassID();
				externalID = p_MSISDNPrefixInterfaceMappingVO.getExternalID();
				status = p_MSISDNPrefixInterfaceMappingVO.getInterfaceStatus();
				message1 = p_MSISDNPrefixInterfaceMappingVO.getLanguage1Message();
				message2 = p_MSISDNPrefixInterfaceMappingVO.getLanguage2Message();
				interfaceStatusTy = p_MSISDNPrefixInterfaceMappingVO.getStatusType();
			} else if (p_serviceSelectorInterfaceMappingVO == null) {
				interfaceID = p_listValueVO.getValue();
				interfaceHandlerClass = p_listValueVO.getLabel();
				allServiceClassID = p_listValueVO.getTypeName();
				externalID = p_listValueVO.getIDValue();
				underProcessMsgReqd = p_listValueVO.getType();
				status = p_listValueVO.getStatus();
				message1 = p_listValueVO.getOtherInfo();
				message2 = p_listValueVO.getOtherInfo2();
				interfaceStatusTy = p_listValueVO.getStatusType();
			}

			if (p_userType.equals(PretupsI.USER_TYPE_SENDER)) {
				_senderTransferItemVO.setPrefixID(p_prefixID);
				_senderTransferItemVO.setInterfaceID(interfaceID);
				_senderTransferItemVO.setInterfaceHandlerClass(interfaceHandlerClass);
				_senderAllServiceClassID = allServiceClassID;
				_senderInterfaceStatusType = interfaceStatusTy;
				_p2pTransferVO.setSenderAllServiceClassID(_senderAllServiceClassID);
				_p2pTransferVO.setSenderInterfaceStatusType(_senderInterfaceStatusType);

			} else if (p_userType.equals(PretupsI.USER_TYPE_RECEIVER)) {
				_receiverTransferItemVO.setPrefixID(p_prefixID);
				_receiverTransferItemVO.setInterfaceID(interfaceID);
				_receiverTransferItemVO.setInterfaceHandlerClass(interfaceHandlerClass);
				if (PretupsI.YES.equals(underProcessMsgReqd)) {
					_p2pTransferVO.setUnderProcessMsgReq(true);
				}
				_receiverAllServiceClassID = allServiceClassID;
				_receiverExternalID = externalID;
				_receiverInterfaceStatusType = interfaceStatusTy;
				_p2pTransferVO.setReceiverAllServiceClassID(_receiverAllServiceClassID);
				_p2pTransferVO.setReceiverInterfaceStatusType(_receiverInterfaceStatusType);
			}
			if (!PretupsI.YES.equals(status) && PretupsI.INTERFACE_STATUS_TYPE_MANUAL.equals(interfaceStatusTy)) {
				if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(_senderLocale)).getMessage())) {
					_p2pTransferVO.setSenderReturnMessage(message1);
				} else {
					_p2pTransferVO.setSenderReturnMessage(message2);
				}
				throw new BTSLBaseException(this, "setInterfaceDetails", PretupsErrorCodesI.INTERFACE_NOT_ACTIVE);
			}
		} catch (BTSLBaseException be) {
			_log.error("setInterfaceDetails", "Getting Base Exception =" + be.getMessage());
			throw be;
		} catch (Exception e) {
			final String methodName = "performReceiverAlternateRouting";
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherConsController[setInterfaceDetails]",
					_transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, "setInterfaceDetails", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
		} finally {
			if (_log.isDebugEnabled()) {
				_log.debug("setInterfaceDetails", _requestIDStr,
						" Exiting with Sender Interface ID=" + _senderTransferItemVO.getInterfaceID() + " Receiver Interface=" + _receiverTransferItemVO.getInterfaceID());
			}
		}
	}

	/**
	 * Method that will update the Subscriber Routing Details If interface is
	 * PRE
	 * 
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
	private void updateSubscriberRoutingDetails(String p_userType, String p_networkCode, String p_interfaceID, String p_externalID, String p_msisdn, String p_interfaceCategory, String p_userID, Date p_currentDate) throws BTSLBaseException {
		final String methodName = "updateSubscriberRoutingDetails";
		if (_log.isDebugEnabled()) {
			_log.debug(
					methodName,
					_requestIDStr,
					" Entered p_userType=" + p_userType + " p_networkCode=" + p_networkCode + " p_interfaceID=" + p_interfaceID + " p_externalID=" + p_externalID + " p_msisdn=" + p_msisdn + " p_interfaceCategory=" + p_interfaceCategory + " p_userID=" + p_userID + " p_currentDate=" + p_currentDate);
		}
		try {
			boolean updationReqd = false;
			if (PretupsI.USER_TYPE_SENDER.equals(p_userType)) {
				updationReqd = _senderDeletionReqFromSubRouting;
			} else {
				updationReqd = _receiverDeletionReqFromSubRouting;
			}

			if (updationReqd) {
				PretupsBL.updateSubscriberInterfaceRouting(p_interfaceID, p_externalID, p_msisdn, p_interfaceCategory, p_userID, p_currentDate);
			} else {
				final SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(p_networkCode + "_" + _p2pTransferVO
						.getServiceType() + "_" + p_interfaceCategory);
				if (!updationReqd && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
					PretupsBL.insertSubscriberInterfaceRouting(p_interfaceID, p_externalID, p_msisdn, p_interfaceCategory, p_userID, p_currentDate);
					if (PretupsI.USER_TYPE_SENDER.equals(p_userType)) {
						_senderInterfaceInfoInDBFound = true;
						_senderDeletionReqFromSubRouting = true;
					} else {
						_receiverInterfaceInfoInDBFound = true;
						_receiverDeletionReqFromSubRouting = true;
					}
				}
			}

		} catch (BTSLBaseException be) {
			_log.error(methodName, "Getting Base Exception =" + be.getMessage());
			throw be;
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"VoucherConsController[updateSubscriberRoutingDetails]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
		} finally {
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, _requestIDStr, " Exiting ");
			}
		}
	}

	/**
	 * @param p_transferVO
	 * @throws BTSLBaseException
	 * @author ashish.gupta
	 * Changed for VIL
	 */
	public static synchronized void generateTransferID(TransferVO p_transferVO) throws BTSLBaseException {
		String transferID = null;
		Date mydate = null;
		String minut2Compare = null;
		int trancount=_transactionIDCounter.get();

		final String methodName = "generateTransferID";
		try {
			mydate = new Date();
			p_transferVO.setCreatedOn(mydate);
			minut2Compare = _sdfCompare.format(mydate);
			final int currentMinut = Integer.parseInt(minut2Compare);

			if (currentMinut != _prevMinut) {
				_transactionIDCounter.set(0);
	  			trancount=_transactionIDCounter.incrementAndGet();
	  			_prevMinut=currentMinut;
				
				/*_transactionIDCounter = 1;
				_prevMinut = currentMinut;*/

			} else if(trancount >= 9999)
	  		{
				_transactionIDCounter.set(0);
	  			trancount=_transactionIDCounter.incrementAndGet();	  			 
	  		}else {
	  			trancount=_transactionIDCounter.incrementAndGet();;

			}

			if (trancount == 0) {
				throw new BTSLBaseException("VoucherConsController", methodName, PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
			}
			transferID = _operatorUtil.formatVoucherTransferID(p_transferVO, trancount);
			if (transferID == null) {
				throw new BTSLBaseException("VoucherConsController", methodName, PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
			}
			p_transferVO.setTransferID(transferID);
		} catch (BTSLBaseException be) {
			_log.errorTrace(methodName, be);
			throw be;
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
			throw new BTSLBaseException("VoucherConsController", methodName, PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
		}
	}

	/**
	 * updateForVOMSValidationResponse
	 * Method to process the response of the receiver validation from VOMS
	 * 
	 * @param str
	 * @throws BTSLBaseException
	 */

	public void updateForVOMSValidationResponse(String str) throws BTSLBaseException {
		final String methodName = "updateForVOMSValidationResponse";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered " + str);
		}
		final HashMap map = BTSLUtil.getStringToHash(str, "&", "=");
		final String status = (String) map.get("TRANSACTION_STATUS");
		_senderTransferItemVO.setInterfaceResponseCode((String) map.get("INTERFACE_STATUS"));
		_receiverVO.setInterfaceResponseCode(_senderTransferItemVO.getInterfaceResponseCode());
		//_senderTransferItemVO.setValidationStatus(status);
		_receiverTransferItemVO.setValidationStatus(status);
		
		_senderTransferItemVO.setInterfaceReferenceID((String) map.get("IN_TXN_ID"));

		String[] strArr = null;
		if (BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS)) {
			if(status.equalsIgnoreCase(InterfaceErrorCodesI.INTERFACE_MSISDN_BARRED)){
				barredStatus = true;
			}
			_p2pTransferVO.setErrorCode(status + "_S");
			_senderTransferItemVO.setTransferStatus(status);
			strArr = new String[] { _transferID, PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()) };
			throw new BTSLBaseException("VoucherConsController", methodName, _p2pTransferVO.getErrorCode(), 0, strArr, null);
		}
		_senderTransferItemVO.setTransferStatus(status);
		_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "SERVICE_CLASS=" + (String) map.get("SERVICE_CLASS"));
		}

		_receiverTransferItemVO.setServiceClassCode((String) map.get("SERVICE_CLASS"));
		_senderTransferItemVO.setServiceClassCode((String) map.get("SERVICE_CLASS"));
		_receiverTransferItemVO.setServiceClass((String) map.get("SERVICE_CLASS"));
		_senderTransferItemVO.setServiceClass((String) map.get("SERVICE_CLASS"));
		_senderTransferItemVO.setInterfaceType(_receiverTransferItemVO.getInterfaceType());
		try {
            _receiverTransferItemVO.setLmbdebitvalue((Long.valueOf((String) map.get("LMB_ALLOWED_VALUE"))));
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e" + e);
        }
		
		final SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache
				.getRoutingControlDetails(_p2pTransferVO.getReceiverNetworkCode() + "_" + _p2pTransferVO.getServiceType() + "_" + PretupsI.INTERFACE_CATEGORY_VOMS);
		if (!_vomsInterfaceInfoInDBFound && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
			PretupsBL.insertSubscriberInterfaceRouting(_senderTransferItemVO.getInterfaceID(), _vomsExternalID, _receiverMSISDN, PretupsI.INTERFACE_CATEGORY_VOMS, _senderVO
					.getUserID(), _currentDate);
			_vomsInterfaceInfoInDBFound = true;
		}

		if (!BTSLUtil.isNullString((String) map.get("SUBSCRIBER_CELL_SWITCH_ID_REQ")) && "Y".equalsIgnoreCase((String) map.get("SUBSCRIBER_CELL_SWITCH_ID_REQ"))) {

			if (!BTSLUtil.isNullString((String) map.get("SUBSCRIBER_CELL_ID"))) {
				_p2pTransferVO.setCellId((String) map.get("SUBSCRIBER_CELL_ID"));
			}

			if (!BTSLUtil.isNullString((String) map.get("SUBSCRIBER_SWITCH_ID"))) {
				_p2pTransferVO.setSwitchId((String) map.get("SUBSCRIBER_SWITCH_ID"));
			}
		}
		
	}

	private void updateForVomsEnquiryVO(final HashMap map) throws BTSLBaseException {
		final String methodName = "updateForVomsEnquiryVO";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered " + map);
		}
		if(map.get(VOMSI.TOPUP) != null)
			_receiverTransferItemVO.setTransferValue(Long.parseLong((String) map.get(VOMSI.TOPUP)));
		if(map.get(VOMSI.VOMS_VALIDITY) != null)
			_receiverTransferItemVO.setValidity(Integer.parseInt((String) map.get(VOMSI.VOMS_VALIDITY)));
		if(map.get(VOMSI.SERIAL_NO) != null)
			_p2pTransferVO.setSerialNumber((String) map.get(VOMSI.SERIAL_NO));
		
		if (!BTSLUtil.isNullString((String) map.get(VOMSI.PRODUCT_ID))) {
			_vomsVO.setProductID((String) map.get(VOMSI.PRODUCT_ID));
			_vomsVO.setSerialNo((String) map.get(VOMSI.SERIAL_NO));
			_vomsVO.setTransactionID((String) map.get(VOMSI.VOMS_TXNID));
	        _vomsVO.setUserNetworkCode((String) map.get(VOMSI.REGION));
			if ("null".equals((String) map.get(VOMSI.SERIAL_NO))) {
				throw new BTSLBaseException("VoucherConsController", methodName, PretupsErrorCodesI.VOUCHER_NOT_FOUND);
			}

			try {
				_vomsVO.setTalkTime(Long.parseLong((String) map.get(VOMSI.TOPUP)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			if(map.get(VOMSI.PIN) != null)
				_p2pTransferVO.setVoucherCode((String) map.get(VOMSI.PIN));
			
			_vomsVO.setPinNo(_p2pTransferVO.getVoucherCode());

			_p2pTransferVO.setSerialNumber((String) map.get(VOMSI.SERIAL_NO));

		}
		if(map.get(VOMSI.VOMS_TYPE) != null)
			_p2pTransferVO.setVoucherType((String) map.get(VOMSI.VOMS_TYPE));
		if(map.get(VOMSI.VOUCHER_SEGMENT) != null)
			_p2pTransferVO.setVoucherSegment((String) map.get(VOMSI.VOUCHER_SEGMENT));
		if(map.get(VOMSI.PRODUCT_ID) != null)
			_p2pTransferVO.setProductId((String) map.get(VOMSI.PRODUCT_ID));
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Exiting: "+methodName+" VomsVO::" + _vomsVO);
		}
		
		
	}
	

	private void creditBackSenderForFailedTrans() throws BTSLBaseException {
		final String methodName = "creditBackSenderForFailedTrans";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "", "Entered with p_onlyDecreaseOnly=");
		}
		Connection con = null;MComConnectionI mcomCon = null;
		try {
			mcomCon = new MComConnection();con=mcomCon.getConnection();
			SubscriberBL.decreaseTransferOutCounts(con, _p2pTransferVO,PretupsI.SERVICE_TYPE_VOUCHER_CONSUMPTION);
			_p2pTransferVO.setModifiedOn(_currentDate);
			_p2pTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
			PretupsBL.updateTransferDetails(con, _p2pTransferVO);
			con.commit();
		} catch (BTSLBaseException be) {
			if (con != null) {
				try {
					con.rollback();
				} catch (Exception e) {
					_log.errorTrace(methodName, e);
				}
			}
			throw be;
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
			if (con != null) {
				try {
					con.rollback();
				} catch (Exception ex) {
					_log.errorTrace(methodName, ex);
				}
			}

			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
		} finally {
			if(mcomCon != null){mcomCon.close("VoucherConsController#creditBackSenderForFailedTrans");mcomCon=null;}
		}

	}
/**
 * 	
 * @param responseMap
 * @throws BTSLBaseException
 */
	private void validateVoucherResponse(HashMap responseMap) throws BTSLBaseException{
		final String methodName = "validateVoucherResponse";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, " Entered With Voucher_DB_Status  : "+responseMap.get(VOMSI.MESSAGE) );
		}
		_vomsVO.setSerialNo((String)responseMap.get(VOMSI.SERIAL_NO));
		_vomsVO.setCurrentStatus((String)responseMap.get(VOMSI.VOMS_STATUS));
		_vomsVO.setExpiryDateStr((String)responseMap.get(VOMSI.VOMS_EXPIRY_DATE));
		_vomsVO.setMRP(Double.parseDouble((String)responseMap.get(VOMSI.TOPUP)));
		_vomsVO.setTalkTime(Long.parseLong((String)responseMap.get(VOMSI.TOPUP)));
		_vomsVO.setMessage((String)responseMap.get(VOMSI.MESSAGE));
		_vomsVO.setVoucherType((String)responseMap.get(VOMSI.VOMS_TYPE));
		_vomsVO.setMaxReqQuantity(Long.parseLong((String)responseMap.get(VOMSI.TALKTIME)));
		//added by Ashish to set additional param to IN for VIL
		_vomsVO.setProductName((String)responseMap.get(VOMSI.PRODUCT_NAME));
		_vomsVO.setVoucherSegment((String)responseMap.get(VOMSI.VOUCHER_SEGMENT));
		
		_p2pTransferVO.setSerialNumber((String)responseMap.get(VOMSI.SERIAL_NO));
		_p2pTransferVO.setTransferValue(_vomsVO.getTalkTime());
		_p2pTransferVO.setVoucherSegment((String)responseMap.get(VOMSI.VOUCHER_SEGMENT));
		if(responseMap.get("VOMS_UPDATE_STATUS") != null && !BTSLUtil.isNullString((String)responseMap.get("VOMS_UPDATE_STATUS"))){
			_voucherMarked = Boolean.valueOf((String)responseMap.get("VOMS_UPDATE_STATUS"));
		}
		 //if(PretupsErrorCodesI.ERROR_VOMS_PINNOTFOUNDINDB.equals(responseMap.get(VOMSI.MESSAGE))){
		if(PretupsErrorCodesI.ERROR_VOMS_PINNOTFOUNDINDB.equals(responseMap.get(VOMSI.MESSAGE)) || PretupsErrorCodesI.ERROR_VOMS_DIFF_NETWORK.equals(responseMap.get(VOMSI.MESSAGE))){
			
			 
         	_p2pTransferVO.setInvalidPINcount(_p2pTransferVO.getInvalidPINcount()+1);
         	_receiverVO = (ReceiverVO)_p2pTransferVO.getReceiverVO();
         	_receiverVO.setInvalidPINcount(_receiverVO.getInvalidPINcount()+1);
         	_p2pTransferVO.setReceiverVO(_receiverVO);
         	inValidPinAttempt=true;
         	//throw new BTSLBaseException(this.getClass().getName(), methodName, PretupsErrorCodesI.ERROR_VOMS_PINNOTFOUNDINDB);
         	throw new BTSLBaseException(this.getClass().getName(), methodName, responseMap.get(VOMSI.MESSAGE).toString());
         }
         else if(PretupsErrorCodesI.ERROR_VOMS_PINEMPTY.equals(responseMap.get(VOMSI.MESSAGE))){
         	throw new BTSLBaseException(this.getClass().getName(), methodName, PretupsErrorCodesI.ERROR_VOMS_PINEMPTY);
         }
         else if(PretupsErrorCodesI.ERROR_VOMS_STATUSINVALIDCONSUMP.equals(responseMap.get(VOMSI.MESSAGE))){
         	_p2pTransferVO.setInvalidPINcount(_p2pTransferVO.getInvalidPINcount()+1);
         	_receiverVO = (ReceiverVO)_p2pTransferVO.getReceiverVO();
         	_receiverVO.setInvalidPINcount(_receiverVO.getInvalidPINcount()+1);
         	_p2pTransferVO.setReceiverVO(_receiverVO);
         	inValidPinAttempt=true;
         	throw new BTSLBaseException(this.getClass().getName(), methodName, PretupsErrorCodesI.ERROR_VOMS_STATUSINVALIDCONSUMP);
         }
         else if(PretupsErrorCodesI.ERROR_VOMS_STATUSINVALIDFORCONSUMPTION.equals(responseMap.get(VOMSI.MESSAGE))){
        	 throw new BTSLBaseException(this.getClass().getName(), methodName, PretupsErrorCodesI.ERROR_VOMS_STATUSINVALIDFORCONSUMPTION);
         }
         else if(PretupsErrorCodesI.ERROR_VOMS_VOUCHEREXPIRED.equals(responseMap.get(VOMSI.MESSAGE))){
        	 throw new BTSLBaseException(this.getClass().getName(), methodName, PretupsErrorCodesI.ERROR_VOMS_VOUCHEREXPIRED);
         }
         else if(PretupsErrorCodesI.ERROR_VOMS_GEN.equals(responseMap.get(VOMSI.MESSAGE)))
        	 {
        	 throw new BTSLBaseException(this.getClass().getName(), methodName, PretupsErrorCodesI.ERROR_VOMS_GEN);

        	 }
         else if(PretupsErrorCodesI.ERROR_VOMS_DIFF_NETWORK.equals(responseMap.get(VOMSI.MESSAGE)))
    	 {
    	 throw new BTSLBaseException(this.getClass().getName(), methodName, PretupsErrorCodesI.ERROR_VOMS_DIFF_NETWORK);

    	 }
         else{
        	 _receiverVO.setInvalidPINcount(0);
         }
		
			 
		 if (_log.isDebugEnabled()) {
				_log.debug(methodName, " Exitting With Voucher_DB_Status  : "+responseMap.get(VOMSI.MESSAGE) );
			}
		
	}
/**
 * 	
 */
	public void sendNotification() {
		final String methodName = "sendNotification";
		if (_log.isDebugEnabled())
			_log.debug(methodName, "Entered...");
		String voucherInvalidPinResp = null;
		try {
			
			final NetworkInterfaceModuleVO networkInterfaceModuleVOS = (NetworkInterfaceModuleVO) NetworkInterfaceModuleCache
					.getObject(_p2pTransferVO.getModule(), _p2pTransferVO.getNetworkCode(),
							PretupsI.INTERFACE_CATEGORY_PRE);
			String intModCommunicationTypeS = networkInterfaceModuleVOS.getCommunicationType();
			String intModClassNameS = networkInterfaceModuleVOS.getClassName();
			final CommonClient commonClient = new CommonClient();
			final String requestStr = getInValidAttemptStr();
			TransactionLog.log(null, null, _receiverMSISDN, _p2pTransferVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

			voucherInvalidPinResp = commonClient.process(requestStr, null, intModCommunicationTypeS, null, 0,intModClassNameS);

			TransactionLog.log(null, null, _receiverMSISDN, _p2pTransferVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_INVAL, voucherInvalidPinResp, PretupsI.TXN_LOG_STATUS_SUCCESS, "");
			/*final HashMap map = BTSLUtil.getStringToHash(voucherInvalidPinResp, "&", "=");
			if (!BTSLUtil.isNullString((String) map.get("BARRED_STATUS")) && PretupsI.YES.equalsIgnoreCase((String) map.get("BARRED_STATUS"))) {
					barredStatus = true;
			}*/
			
		} catch (Exception e) {
			_log.error(methodName, "Exception : " + e.getMessage());
			_log.errorTrace(methodName, e);
		} finally {
			if (_log.isDebugEnabled())
				_log.debug(methodName, "Exited...");
		}
	
		

	}
/**
 * 	
 * @return
 */
	public String getInValidAttemptStr() {
		StringBuilder strBuff = null;
		strBuff = new StringBuilder(getReceiverCommonString());
		strBuff.append("&INTERFACE_ACTION=" + PretupsI.INTERFACE_VALIDATE_ACTION);
		strBuff.append("&SENDER_INVALID_PIN_CNT=" + _p2pTransferVO.getInvalidPINcount());
		strBuff.append("&RECEIVER_INVALID_PIN_CNT=" + _receiverVO.getInvalidPINcount());
		strBuff.append("&CRM_NOTIFICATION_MSG=" + PretupsI.YES);
		strBuff.append("&INTERFACE_AMOUNT=" + "0");
		strBuff.append("&VALIDITY_DAYS=" + "0");
		strBuff.append("&MSISDN=" + _receiverMSISDN);
		strBuff.append("&SENDER_MSISDN=" +_senderMSISDN);

		return strBuff.toString();
	}
	
	
}
