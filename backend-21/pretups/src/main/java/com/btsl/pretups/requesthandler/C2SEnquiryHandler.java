/*
 * Created on Dec 18, 2007
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
/*
 * @(#)C2SEnquiryHandler.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Ranjana Chouhan Dec 19,2007
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Handler class for c2s enquiry
 */
package com.btsl.pretups.requesthandler;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupBL;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDetailsVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.util.InterfaceCloserI;
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.ServiceClassInfoByCodeCache;
import com.btsl.pretups.master.businesslogic.ServiceClassVO;
import com.btsl.pretups.network.businesslogic.MSISDNPrefixInterfaceMappingCache;
import com.btsl.pretups.network.businesslogic.MSISDNPrefixInterfaceMappingVO;
import com.btsl.pretups.network.businesslogic.NetworkInterfaceModuleCache;
import com.btsl.pretups.network.businesslogic.NetworkInterfaceModuleVO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
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
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.transfer.businesslogic.TransferRulesVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.pretups.vastrix.businesslogic.ServiceSelectorInterfaceMappingCache;
import com.btsl.pretups.vastrix.businesslogic.ServiceSelectorInterfaceMappingVO;
import com.btsl.pretups.whitelist.businesslogic.WhiteListVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/**
 * @author ranjana.chouhan
 * 
 *         This controller is designed to fetch the card group slabs according
 *         to the entered amount.
 *         It is used for the both prepaid and postpaid subscribers.
 *         In this controller ,transferID is not generated for each request.
 *         So, we are passing the requestID in place transfe id to decrease the
 *         response counter.
 *         otherwise response counters are not decreases when more than one
 *         request comes to the system.
 * 
 */
public class C2SEnquiryHandler implements ServiceKeywordControllerI {
    private static Log _log = LogFactory.getLog(C2SEnquiryHandler.class.getName());

    private String _slabAmount = "";
    private ArrayList _cardGroupDetailsVOList = null;
    private TransferVO _transferVO = null;

    private CardGroupDetailsVO _cardGroupDetailsVO = null;
    private C2STransferVO _c2sTransferVO = null;
    private String _senderMSISDN;
    private String _receiverMSISDN;
    private ChannelUserVO _channelUserVO;
    private ReceiverVO _receiverVO;

    private String _senderNetworkCode;
    private Date _currentDate = null;
    // private long _requestID;
    // private String _transferID;
    private String _requestIDStr;
    private String _intModCommunicationTypeS;
    private String _intModIPS;
    private int _intModPortS;
    private String _intModClassNameS;
    private boolean _isCounterDecreased = false;
    private String _type;
    private String _serviceType;
    private String _subService;
    private RequestVO _requestVO = null;

    private static OperatorUtilI _operatorUtil = null;
    private String _interfaceStatusType = null;

    private Locale _senderLocale = null;
    private Locale _receiverLocale = null;

    private ServiceInterfaceRoutingVO _serviceInterfaceRoutingVO = null;
    private String _interfaceCategory = null;
    private boolean _useAlternateCategory = false;
    private boolean _senderInterfaceInfoInDBFound = false;
    private String _interfaceID = null;

    private boolean _isRequestRefuse = false;
    private String _senderExternalID = null;
    private boolean _decreaseCountersReqd = false;
    private String _msisdn;
    private String _networkCode;
    private String _txnStatus = null;
    private String _defaultSelector = null;
    private String _accountStatus = null;
    private String _requestID;

    // Loads operator specific class
    static {
        final String METHOD_NAME = "static";
        String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SEnquiryHandler[initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    /**
     * Method to process the request of the C2S enquiry
     * 
     * @param object
     *            of the RequestVO
     */
    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        Connection con = null;MComConnectionI mcomCon = null;
        // String spaceSeperator = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
        String spaceSeperator = " ";
        try {
            spaceSeperator = Constants.getProperty("C2S_ENQUIRY_SEPARATOR");
            if (BTSLUtil.isNullString(spaceSeperator) || "null".equalsIgnoreCase(spaceSeperator)) {
                if (!BTSLUtil.isNullString((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR))) {
                    spaceSeperator = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
                } else {
                    spaceSeperator = " ";
                }
            }
        } catch (RuntimeException e) {
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, " " + e.getMessage());
            }
            _log.errorTrace(METHOD_NAME, e);
            if (!BTSLUtil.isNullString((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR))) {
                spaceSeperator = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
            } else {
                spaceSeperator = " ";
            }
        }

        // 1. Validate the incoming Message
        // 2. Get the newtork Code for customer MSISDN
        // 3. check the network and service type maapping
        // 4. Check whether same sender and reciever msisdn can be same
        // 5. Get the location URLS
        // 6. Send request to IN
        // 7. Get the service class and receiver service class id
        // 8. on the basis of receiver service class id,get the card group set
        // id from transfer rules
        // 9. by using card group set id, fetch slab amount from card group
        // details

        if (_log.isDebugEnabled()) {
            _log.debug("process", p_requestVO.getRequestIDStr(), "Entered for Request ID=" + p_requestVO.getRequestID() + " MSISDN=" + p_requestVO.getFilteredMSISDN());
        }

        try {
            _requestVO = p_requestVO;
            _channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
            _senderLocale = p_requestVO.getSenderLocale();
            _senderNetworkCode = _channelUserVO.getNetworkID();
            _currentDate = new Date();
            _c2sTransferVO = new C2STransferVO();
            _transferVO = new TransferVO();
            _transferVO.setTransferDateTime(_currentDate);

            /*
			 * 
			 */
            _requestID = _requestVO.getRequestIDStr();
            populateVOFromRequest(p_requestVO);

            // _requestID=p_requestVO.getRequestID();
            _requestIDStr = p_requestVO.getRequestIDStr();
            _type = p_requestVO.getType();
            _serviceType = p_requestVO.getServiceType();
            _subService = p_requestVO.getReqSelector();
            // Getting oracle connection
            mcomCon = new MComConnection();con=mcomCon.getConnection();

            // Validating user message incomming in the request
            _operatorUtil.validateC2SEnquiryRequest(con, _c2sTransferVO, p_requestVO);

            _receiverLocale = p_requestVO.getReceiverLocale();
            _senderLocale = p_requestVO.getSenderLocale();

            _receiverVO = (ReceiverVO) _c2sTransferVO.getReceiverVO();

            _receiverVO.setMsisdnPrefix(PretupsBL.getMSISDNPrefix(_receiverVO.getMsisdn()));
            _msisdn = _receiverVO.getMsisdn();
            _networkCode = p_requestVO.getRequestNetworkCode();
            _receiverVO.setModule(_c2sTransferVO.getModule());
            _receiverVO.setCreatedDate(_currentDate);
            _receiverVO.setLastTransferOn(_currentDate);
            _senderMSISDN = (_channelUserVO.getUserPhoneVO()).getMsisdn();
            _receiverMSISDN = ((ReceiverVO) _c2sTransferVO.getReceiverVO()).getMsisdn();
            _c2sTransferVO.setReceiverMsisdn(_receiverMSISDN);
            _c2sTransferVO.setReceiverNetworkCode(_receiverVO.getNetworkCode());
            _c2sTransferVO.setGrphDomainCode(_channelUserVO.getGeographicalCode());
            _c2sTransferVO.setSubService(p_requestVO.getReqSelector());
            if (_log.isDebugEnabled()) {
                _log.debug("process", "p_requestVO.getReqSelector()= " + p_requestVO.getReqSelector()+", _c2sTransferVO.getSubService()="+_c2sTransferVO.getSubService());
            }
            _c2sTransferVO.setRequestStartTime(p_requestVO.getRequestStartTime());

            // Validates the network service status
            PretupsBL.validateNetworkService(_c2sTransferVO);

            // check if receiver barred in PreTUPS or not, user should not be
            // barred.
            try {
                PretupsBL.checkMSISDNBarred(con, _receiverMSISDN, _receiverVO.getNetworkCode(), _c2sTransferVO.getModule(), PretupsI.USER_TYPE_RECEIVER);
            } catch (BTSLBaseException be) {
                _log.errorTrace(METHOD_NAME, be);
                if (be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.ERROR_RECEIVER_USERBARRED))) {
                    _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.ERROR_USERBARRED_R, new String[] {}));
                }
                throw be;
            }
            PretupsBL.getProductFromServiceType(con, _c2sTransferVO, _serviceType, PretupsI.C2S_MODULE);

			if (mcomCon != null) {
				mcomCon.close("C2SEnquiryHandler#process");
				mcomCon = null;
			}

            /*
             * New Changes to handle the request hoping from one IN to other.
             * It is required to handle multiple IN for ony one service type
             */

            boolean isUserExists = false;
            // isUserExists=checkRoutingControlAndSendReq(_receiverVO,PretupsI.SERVICE_TYPE_C2S_ENQUIRY,_receiverVO.getSubscriberType());

            if (p_requestVO.getType().equals(PretupsI.NOT_APPLICABLE)) {
                _serviceInterfaceRoutingVO = ServiceInterfaceRoutingCache.getServiceInterfaceRoutingDetails(_receiverVO.getNetworkCode() + "_" + p_requestVO.getServiceType() + "_" + PretupsI.NOT_APPLICABLE);
                if (_serviceInterfaceRoutingVO != null) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("process", _requestIDStr, "For =" + _receiverVO.getNetworkCode() + "_" + p_requestVO.getServiceType() + " Got Interface Category=" + _serviceInterfaceRoutingVO.getInterfaceType() + " Alternate Check Required=" + _serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool() + " Alternate Interface=" + _serviceInterfaceRoutingVO.getAlternateInterfaceType());
                    }

                    _interfaceCategory = _serviceInterfaceRoutingVO.getInterfaceType();
                    _useAlternateCategory = _serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool();
                    _defaultSelector = _serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode();
                } else {
                    _log.info("process", p_requestVO.getRequestIDStr(), "Service Interface Routing control Not defined, thus using default type=" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE)));
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "C2SEnquiryHandler[process]", "", "", "", "Service Interface Routing control Not defined, thus using default type=" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE)));
                    _interfaceCategory = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE));
                }
            } else {
                _interfaceCategory = p_requestVO.getType();
            }

            NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(_receiverVO.getMsisdnPrefix(), _interfaceCategory);
            if (networkPrefixVO != null) {
                _receiverVO.setPrefixID(networkPrefixVO.getPrefixID());
                _receiverVO.setNetworkCode(networkPrefixVO.getNetworkCode());
                _receiverVO.setSubscriberType(networkPrefixVO.getSeriesType());
            }

            ServiceClassVO serviceClassVO = null;
            isUserExists = checkRoutingControlAndSendReq(_receiverVO, PretupsI.SERVICE_TYPE_C2S_ENQUIRY, _interfaceCategory);

            if (isUserExists) {
                if (PretupsI.INTERFACE_CATEGORY_POST.equalsIgnoreCase(_interfaceCategory)) {
                    _requestVO.setEnquiryServiceType("PPB");

                } else if (PretupsI.INTERFACE_CATEGORY_PRE.equalsIgnoreCase(_interfaceCategory)) {
                    _requestVO.setEnquiryServiceType("RC");
                }
            }

            if (!isUserExists && _useAlternateCategory) {
                // If useAlternateCategory is set then check whether alternate
                // interface
                // Check is Y or not. If Y set Preform the above steps and call
                // checkRoutingControlAndSendReq
                // With alternate interface category
                _interfaceCategory = _serviceInterfaceRoutingVO.getAlternateInterfaceType();
                if (PretupsI.INTERFACE_CATEGORY_POST.equalsIgnoreCase(_interfaceCategory)) {
                    _requestVO.setEnquiryServiceType("PPB");

                } else if (PretupsI.INTERFACE_CATEGORY_PRE.equalsIgnoreCase(_interfaceCategory)) {
                    _requestVO.setEnquiryServiceType("RC");
                }
                _receiverVO.setSubscriberType(_interfaceCategory);
                networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(_receiverVO.getMsisdnPrefix(), _interfaceCategory);
                if (networkPrefixVO != null) {
                    _receiverVO.setPrefixID(networkPrefixVO.getPrefixID());
                    _receiverVO.setNetworkCode(networkPrefixVO.getNetworkCode());
                    _receiverVO.setSubscriberType(networkPrefixVO.getSeriesType());
                }

                _defaultSelector = _serviceInterfaceRoutingVO.getAlternateDefaultSelectortCode();
                _log.debug("process", _requestIDStr, "Performing the alternate interface category validation on :=" + _interfaceCategory);
                isUserExists = checkRoutingControlAndSendReq(_receiverVO, PretupsI.SERVICE_TYPE_C2S_ENQUIRY, _interfaceCategory);
            }

            if (_log.isDebugEnabled()) {
                _log.debug("processValidationRequest", "Entered and performing validations for transfer ID=" + _requestID + " " + _c2sTransferVO.getModule() + " " + _c2sTransferVO.getReceiverNetworkCode() + " " + _type);
            }
            try {

                _requestVO.setReceiverSubscriberType(_interfaceCategory);

                if (isUserExists) {

                    if (!BTSLUtil.isNullString(_receiverVO.getServiceClassCode())) {
                        // ServiceClassDAO serviceClassDAO=new
                        // ServiceClassDAO();
                        // serviceClassVO=serviceClassDAO.loadServiceClassInfoByCode(con,_receiverVO.getServiceClassCode(),_interfaceID);
                        serviceClassVO = ServiceClassInfoByCodeCache.getServiceClassByCode(_receiverVO.getServiceClassCode(), _interfaceID);
                        if (serviceClassVO == null) {
                            // serviceClassVO=serviceClassDAO.loadServiceClassInfoByCode(con,PretupsI.ALL,_interfaceID);
                            serviceClassVO = ServiceClassInfoByCodeCache.getServiceClassByCode(PretupsI.ALL, _interfaceID);
                            if (serviceClassVO != null) {
                                p_requestVO.setReceiverServiceClassId(serviceClassVO.getServiceClassId());
                            }
                        } else {
                            p_requestVO.setReceiverServiceClassId(serviceClassVO.getServiceClassId());
                        }
                    }
                    if (serviceClassVO != null && !BTSLUtil.isNullString(p_requestVO.getReceiverServiceClassId())) {
                        if (PretupsI.SUSPEND.equals(serviceClassVO.getStatus())) {
                            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[validateServiceClass]", "", "", "", "Service Class " + serviceClassVO.getServiceClassId() + " is suspended");
                            throw new BTSLBaseException("PretupsBL", "validateServiceClass", PretupsErrorCodesI.ERROR_INTFCE_SRVCECLSS_SUSPEND);
                        }

                        String allowedAccountStatus = null;
                        if (PretupsI.YES.equals(serviceClassVO.getC2sReceiverSuspend())) {
                            throw new BTSLBaseException("PretupsBL", "validateServiceClass", PretupsErrorCodesI.ERROR_INTFCE_SRVCECLSS_C2S_RECEIVER_SUSPEND);
                        }
                        allowedAccountStatus = serviceClassVO.getC2sReceiverAllowedStatus();

                        if (!BTSLUtil.isNullString(allowedAccountStatus) && !PretupsI.ALL.equals(allowedAccountStatus)) {
                            String[] allowedStatus = allowedAccountStatus.split(",");
                            if (!Arrays.asList(allowedStatus).contains(_accountStatus)) {
                                String[] strArr = new String[] { _msisdn, "", _accountStatus };
                                throw new BTSLBaseException("PretupsBL", "validateServiceClass", PretupsErrorCodesI.ERROR_INTFCE_ACCOUNTSTATUS_NOTALLOWED_REC, 0, strArr, null);
                            }
                        }
                        
                        mcomCon = new MComConnection();con=mcomCon.getConnection();
			    		
			    		_transferVO.setServiceType(_requestVO.getEnquiryServiceType());
			    		_transferVO.setModule(_requestVO.getModule());
			    		_transferVO.setReceiverNetworkCode(_receiverVO.getNetworkCode());
			    		_transferVO.setRequestGatewayCode(_requestVO.getRequestGatewayType());
			    		_transferVO.setSubService(_defaultSelector);
			    		_transferVO.setSenderVO(_requestVO.getSenderVO());
			    		_transferVO.setSenderID(((ChannelUserVO)_requestVO.getSenderVO()).getUserID());
			    		TransferRulesVO transferRulesVO=null;
			    		String serviceClassCode=_receiverVO.getServiceClassCode();
			    		_receiverVO.setServiceClassCode(_requestVO.getReceiverServiceClassId());
			    		
			    		
			    		transferRulesVO=PretupsBL.selectTransferRuleFromCache(con, _transferVO, _receiverVO);
			    		_receiverVO.setServiceClassCode(serviceClassCode);

                        /*TransferRulesVO transferRulesVO = null;
                        transferRulesVO = (TransferRulesVO) TransferRulesCache.getObject(_requestVO.getEnquiryServiceType(), _requestVO.getModule(), _receiverVO.getNetworkCode(), ((ChannelUserVO) _requestVO.getSenderVO()).getDomainID(), _receiverVO.getSubscriberType(), PretupsI.ALL, serviceClassVO.getServiceClassId(), _defaultSelector, PretupsI.NOT_APPLICABLE);*/

                        if (transferRulesVO == null) {
                            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[validateTransferRule]", "", ((ChannelUserVO) _requestVO.getSenderVO()).getMsisdn(), _receiverVO.getNetworkCode(), "Transfer Rule not exist in C2S between Domain " + ((ChannelUserVO) _requestVO.getSenderVO()).getDomainID() + " and " + serviceClassVO.getServiceClassCode() + " Sub Service=" + _defaultSelector + " receiver service class code=" + serviceClassVO.getServiceClassCode());
                            throw new BTSLBaseException("PretupsBL", "validateTransferRule", PretupsErrorCodesI.C2S_ERROR_TRANSFER_RULE_NOTEXIST);
                        } else if (transferRulesVO.getStatus().equals(PretupsI.SUSPEND)) {
                            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[validateTransferRule]", "", ((ChannelUserVO) _requestVO.getSenderVO()).getMsisdn(), _receiverVO.getNetworkCode(), "Transfer Rule is suspended in C2S between Domain" + ((ChannelUserVO) _requestVO.getSenderVO()).getDomainID() + " and " + serviceClassVO.getServiceClassCode() + " Sub Service=" + _defaultSelector + " receiver service class code=" + serviceClassVO.getServiceClassCode());
                            throw new BTSLBaseException("PretupsBL", "validateTransferRule", PretupsErrorCodesI.C2S_ERROR_TRANSFER_RULE_SUSPENDED);
                        }

                       

                        ChannelTransferBL.getCardGroupSetIdFromTransferRule(con, _requestVO.getReceiverServiceClassId(), _requestVO.getEnquiryServiceType(), _transferVO, _requestVO, _channelUserVO);

                        if (_transferVO.getCardGroupSetID() != null) {
                            _cardGroupDetailsVOList = CardGroupBL.getCardGroupDetails(con, _transferVO);
                            Iterator itr = _cardGroupDetailsVOList.iterator();
                            if (_cardGroupDetailsVOList.size() > 0) {
                                String[] arr = new String[1];
                                String temp = "";
                                while (itr.hasNext()) {
                                    _cardGroupDetailsVO = (CardGroupDetailsVO) itr.next();
                                    if (_cardGroupDetailsVO != null) {
                                        temp = Long.toString(_cardGroupDetailsVO.getStartRange() / 100) + spaceSeperator + Long.toString(_cardGroupDetailsVO.getEndRange() / 100);
                                        _slabAmount = _slabAmount + temp + ",";
                                    } else {
                                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_ENQUIRY_FAIL);
                                    }
                                }
                                _slabAmount = _slabAmount.substring(0, _slabAmount.lastIndexOf(","));
                                if (!BTSLUtil.isNullString(_slabAmount)) {
                                    // set the argument which will be send to
                                    // user
                                    p_requestVO.setSlab(true);
                                    p_requestVO.setSlabAmount(_slabAmount);
                                } else {
                                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_ENQUIRY_FAIL);
                                }

                                arr[0] = _slabAmount;
                                p_requestVO.setMessageArguments(arr);
                                p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ENQUIRY_SUCCESS);
                                return;
                            } else {
                                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CARD_GROUP_SLAB_NOT_FOUND);
                            }

                        } else {
                            throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CARD_GROUP_SET_IDNOT_FOUND);
                        }

                    } else {
                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ERROR_INTFCE_SRVCECLSS_NOTFOUND);
                    }
                } else {
                    _requestVO.setReceiverSubscriberType("");
                    throw new BTSLBaseException(this, "receiverValidateResponse", InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
                }

            } catch (BTSLBaseException be) {
                _log.errorTrace(METHOD_NAME, be);
                throw be;
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                throw new BTSLBaseException(this, METHOD_NAME,e.getMessage());
            }

        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                if (be.isKey()) {
                    _c2sTransferVO.setErrorCode(be.getMessageKey());
                } else {
                    _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
                }
            }
            _log.error("C2SEnquiryHandler[process]", "Getting BTSL Base Exception:" + be.getMessage());

            p_requestVO.setSuccessTxn(false);
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);

            if (!BTSLUtil.isNullString(_c2sTransferVO.getSenderReturnMessage())) {
                p_requestVO.setSenderReturnMessage(_c2sTransferVO.getSenderReturnMessage());
            }

            if (be.isKey()) // checking if baseexception has key
            {
                if (_c2sTransferVO.getErrorCode() == null) {
                    _c2sTransferVO.setErrorCode(be.getMessageKey());
                }

                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                // setting default error code if message and key is not found
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }

        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }

            p_requestVO.setSuccessTxn(false);
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            _log.error("process", "Exception:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SEnquiryHandler[process]", _requestID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());

        } finally {

			if (mcomCon != null) {
				mcomCon.close("C2SEnquiryHandler#process");
				mcomCon = null;
			}

            if (BTSLUtil.isNullString(p_requestVO.getMessageCode())) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            }
            if (_isCounterDecreased) {
                p_requestVO.setDecreaseLoadCounters(false);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
        }

    }// end of process

    private void populateVOFromRequest(RequestVO p_requestVO) {
        _c2sTransferVO.setSenderVO(_channelUserVO);

        _c2sTransferVO.setRequestID(p_requestVO.getRequestIDStr());
        _c2sTransferVO.setModule(p_requestVO.getModule());
        // _c2sTransferVO.setInstanceID(p_requestVO.getInstanceID());
        _c2sTransferVO.setRequestGatewayCode(p_requestVO.getRequestGatewayCode());
        _c2sTransferVO.setRequestGatewayType(p_requestVO.getRequestGatewayType());
        _c2sTransferVO.setServiceType(p_requestVO.getServiceType());
        _c2sTransferVO.setSourceType(p_requestVO.getSourceType());
        _c2sTransferVO.setCreatedOn(_currentDate);
        _c2sTransferVO.setCreatedBy(_channelUserVO.getUserID());
        _c2sTransferVO.setModifiedOn(_currentDate);
        _c2sTransferVO.setModifiedBy(_channelUserVO.getUserID());
        // _c2sTransferVO.setTransferDate(_currentDate);
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
    }

    public boolean checkRoutingControlAndSendReq(ReceiverVO p_receiverVO, String p_serviceType, String p_interfaceCatgeory) throws BTSLBaseException {
        final String METHOD_NAME = "checkRoutingControlAndSendReq";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered with p_serviceType" + p_serviceType + " p_interfaceCatgeory=" + p_interfaceCatgeory);
        }
        boolean isSuccess = false;
        Connection con = null;MComConnectionI mcomCon = null;
        try {
            /*
             * Get the routing control parameters based on network code ,
             * service and interface category
             * 1. Check if database check is required
             * 2. If required then check in database whether the number is
             * present
             * 3. If present then Get the interface ID from the same and send
             * request to interface to validate the same
             * 4. If not found then Get the interface ID On the Series basis and
             * send request to interface to validate the same
             */
            SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(p_receiverVO.getNetworkCode() + "_" + p_serviceType + "_" + p_interfaceCatgeory);
            if (subscriberRoutingControlVO != null) {
                // Set intentionally to get a unique transfer ID
                // _transferID=BTSLUtil.currentDateTimeFormatString();
                if (_log.isDebugEnabled()) {
                    _log.debug(METHOD_NAME, "Generated Temp Transfer ID for C2SEnquiry Handler" + _requestID + " Database Check Required=" + subscriberRoutingControlVO.isDatabaseCheckBool() + " Series Check Required=" + subscriberRoutingControlVO.isSeriesCheckBool());
                }
                if (subscriberRoutingControlVO.isDatabaseCheckBool()) {
                	mcomCon = new MComConnection();con=mcomCon.getConnection();
                    if (p_interfaceCatgeory.equalsIgnoreCase(PretupsI.INTERFACE_CATEGORY_PRE)) {
                        ListValueVO listValueVO = PretupsBL.validateNumberInRoutingDatabase(con, p_receiverVO.getMsisdn(), p_interfaceCatgeory);
						if (mcomCon != null) {
							mcomCon.close("C2SEnquiryHandler#checkRoutingControlAndSendReq");
							mcomCon = null;
						}
                        if (listValueVO != null) {
                            _senderInterfaceInfoInDBFound = true;

                            setInterfaceDetails(p_receiverVO, listValueVO, false, null);

                            // Send Request to interface
                            if (_log.isDebugEnabled()) {
                                _log.debug(METHOD_NAME, "Sending Validation Request For MSISDN=" + p_receiverVO.getMsisdn() + " On interface=" + _interfaceID);
                            }

                            isSuccess = processValidationRequest(p_receiverVO, listValueVO.getValue(), listValueVO.getLabel(), p_interfaceCatgeory);
                        } else if (subscriberRoutingControlVO.isSeriesCheckBool()) {
                            if (_log.isDebugEnabled()) {
                                _log.debug(METHOD_NAME, _requestIDStr, " MSISDN=" + p_receiverVO.getMsisdn() + " Not found in Subscriber Routing DB, check for Series");
                            }
                            ServiceSelectorInterfaceMappingVO interfaceMappingVO1 = null;
                            MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null;
                            try {                            	
                            	if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue()) {
                            		interfaceMappingVO1 = (ServiceSelectorInterfaceMappingVO) ServiceSelectorInterfaceMappingCache.getObject(_serviceType + "_" +
                            				_c2sTransferVO.getSubService() + "_" + PretupsI.INTERFACE_VALIDATE_ACTION + "_" + _senderNetworkCode + "_" + p_receiverVO.getPrefixID());
                            		setInterfaceDetails(p_receiverVO, null, true, interfaceMappingVO1);
                                    isSuccess = processValidationRequest(p_receiverVO, _interfaceID, interfaceMappingVO1.getHandlerClass(), p_interfaceCatgeory);
                                } else {
                                	interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_receiverVO.getPrefixID(), p_receiverVO.getSubscriberType(), PretupsI.INTERFACE_VALIDATE_ACTION);
                                	setInterfaceDetails(p_receiverVO, null, true, interfaceMappingVO);
                                    isSuccess = processValidationRequest(p_receiverVO, _interfaceID, interfaceMappingVO.getHandlerClass(), p_interfaceCatgeory);
                                }                                
                            } catch (BTSLBaseException be) {
                                _log.errorTrace(METHOD_NAME, be);
                                if (be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.MSISDN_PREFIX_INTERFACE_MAPPING_NOTFOUND))) {
                                    _log.error(this, "Network Prefix Series Not Defined for p_senderVO.getPrefixID()=" + p_receiverVO.getPrefixID() + " p_senderVO.getSubscriberType()= " + p_receiverVO.getSubscriberType() + " Action=" + PretupsI.INTERFACE_VALIDATE_ACTION);
                                    if (_log.isDebugEnabled()) {
                                        _log.debug(METHOD_NAME, _requestIDStr, " MSISDN=" + p_receiverVO.getMsisdn() + " Not found in Subscriber Routing DB, check for Series");
                                    }
                                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "C2SEnquiryHandler[checkRoutingControlAndSendReq]", "", "", "", "Interface Network Series Mapping Not exist for Series =" + p_receiverVO.getMsisdnPrefix() + " Not Defined for Series type=" + p_receiverVO.getSubscriberType() + " But validation required on that interface");
                                    isSuccess = false;
                                } else {
                                    throw be;
                                }
                            }
                        } else {
                            return isSuccess;
                        }
                    } else if (p_interfaceCatgeory.equalsIgnoreCase(PretupsI.INTERFACE_CATEGORY_POST)) {
                        WhiteListVO whiteListVO = PretupsBL.validateNumberInWhiteList(con, p_receiverVO.getMsisdn());
						if (mcomCon != null) {
							mcomCon.close("C2SEnquiryHandler#checkRoutingControlAndSendReq");
							mcomCon = null;
						}
                        if (whiteListVO != null) {
                            _senderInterfaceInfoInDBFound = true;
                            // Send Request to interface
                            ListValueVO listValueVO = whiteListVO.getListValueVO();

                            setInterfaceDetails(p_receiverVO, listValueVO, false, null);

                            p_receiverVO.setServiceClassCode(whiteListVO.getServiceClassCode());
                            // p_senderVO.setAccountStatus(whiteListVO.getAccountStatus());
                            p_receiverVO.setCurrentBalance(whiteListVO.getCreditLimit());

                            // Since Number was found in White List there is no
                            // need to send validation request to the
                            // interface thereby registering the user after it.
                            isSuccess = true;
                        } else if (subscriberRoutingControlVO.isSeriesCheckBool()) {
                            if (_log.isDebugEnabled()) {
                                _log.debug(METHOD_NAME, _requestIDStr, " MSISDN=" + p_receiverVO.getMsisdn() + " Not found in White List Routing DB, check for Series");
                            }
                            MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null;
                            try {
                                interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_receiverVO.getPrefixID(), p_receiverVO.getSubscriberType(), PretupsI.INTERFACE_VALIDATE_ACTION);
                                setInterfaceDetails(p_receiverVO, null, true, interfaceMappingVO);
                                isSuccess = processValidationRequest(p_receiverVO, _interfaceID, interfaceMappingVO.getHandlerClass(), p_interfaceCatgeory);
                            } catch (BTSLBaseException be) {
                                _log.errorTrace(METHOD_NAME, be);
                                if (be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.MSISDN_PREFIX_INTERFACE_MAPPING_NOTFOUND))) {
                                    _log.error(this, "Network Prefix Series Not Defined for p_senderVO.getPrefixID()=" + p_receiverVO.getPrefixID() + " p_senderVO.getSubscriberType()= " + p_receiverVO.getSubscriberType() + " Action=" + PretupsI.INTERFACE_VALIDATE_ACTION);
                                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "C2SEnquiryHandler[checkRoutingControlAndSendReq]", "", "", "", "Interface Network Series Mapping Not exist for Series =" + p_receiverVO.getMsisdnPrefix() + " Not Defined for Series type=" + p_receiverVO.getSubscriberType() + " But validation required on that interface");
                                    isSuccess = false;
                                } else {
                                    throw be;
                                }
                            }
                        } else {
                            return isSuccess;
                        }
                    }
					if (mcomCon != null) {
						mcomCon.close("C2SEnquiryHandler#checkRoutingControlAndSendReq");
						mcomCon = null;
					}
                } else if (subscriberRoutingControlVO.isSeriesCheckBool()) {
                	ServiceSelectorInterfaceMappingVO interfaceMappingVO1 = null;
                    MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null;
                    try {                	                    	
                    	if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue()) {
                    		interfaceMappingVO1 = (ServiceSelectorInterfaceMappingVO) ServiceSelectorInterfaceMappingCache.getObject(_serviceType + "_" +
                    				_c2sTransferVO.getSubService()  + "_" + PretupsI.INTERFACE_VALIDATE_ACTION + "_" + _senderNetworkCode + "_" + p_receiverVO.getPrefixID());
                    		setInterfaceDetails(p_receiverVO, null, true, interfaceMappingVO1);
                    		if (_log.isDebugEnabled()) {
                                _log.debug(METHOD_NAME, "Sending Validation Request For MSISDN=" + p_receiverVO.getMsisdn() + " On interface=" + _interfaceID);
                            }
                    		isSuccess = processValidationRequest(p_receiverVO, _interfaceID, interfaceMappingVO1.getHandlerClass(), p_interfaceCatgeory);
                        }
	                    else {
	                    	interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_receiverVO.getPrefixID(), _interfaceCategory, PretupsI.INTERFACE_VALIDATE_ACTION);
	                    	setInterfaceDetails(p_receiverVO, null, true, interfaceMappingVO);
	                    	if (_log.isDebugEnabled()) {
	                            _log.debug(METHOD_NAME, "Sending Validation Request For MSISDN=" + p_receiverVO.getMsisdn() + " On interface=" + _interfaceID);
	                        }
	                    	isSuccess = processValidationRequest(p_receiverVO, _interfaceID, interfaceMappingVO.getHandlerClass(), p_interfaceCatgeory);
	                    }
                    } catch (BTSLBaseException be) {
                        _log.errorTrace(METHOD_NAME, be);
                        if (be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.MSISDN_PREFIX_INTERFACE_MAPPING_NOTFOUND))) {
                            _log.error(this, "Network Prefix Series Not Defined for p_senderVO.getPrefixID()=" + p_receiverVO.getPrefixID() + " p_senderVO.getSubscriberType()= " + p_receiverVO.getSubscriberType() + " Action=" + PretupsI.INTERFACE_VALIDATE_ACTION);
                            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "C2SEnquiryHandler[checkRoutingControlAndSendReq]", "", "", "", "Interface Network Series Mapping Not exist for Series =" + p_receiverVO.getMsisdnPrefix() + " Not Defined for Series type=" + p_receiverVO.getSubscriberType() + " But validation required on that interface");
                            isSuccess = false;
                        } else {
                            throw be;
                        }
                    }
                } else {
                    return isSuccess;
                }
            } else {
                _log.error(this, "Routing Controls Not Defined for Key=" + p_receiverVO.getNetworkCode() + "_" + p_serviceType + "_" + p_interfaceCatgeory + " Thus returning false");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "C2SEnquiryHandler[checkRoutingControlAndSendReq]", "", "", "", "Routing Controls Not Defined For Key =" + p_receiverVO.getNetworkCode() + "_" + p_serviceType + "_" + p_interfaceCatgeory);
                return isSuccess;
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            // be.printStackTrace();
            _log.error(METHOD_NAME, "BTSLBaseException " + be.getMessage());
            if (be.isKey() && !_isRequestRefuse) {
                throw be;
            } else if (!_isRequestRefuse) {
                isSuccess = false;
            } else {
                throw be;
            }
        } catch (Exception e) {
            _log.error(METHOD_NAME, "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SEnquiryHandler[checkRoutingControlAndSendReq]", p_receiverVO.getMsisdn(), p_receiverVO.getNetworkCode(), "", "Exception:" + e.getMessage());
            if (!_isRequestRefuse) {
                isSuccess = false;
            } else {
                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }
        } finally {
			if (mcomCon != null) {
				mcomCon.close("C2SEnquiryHandler#checkRoutingControlAndSendReq");
				mcomCon = null;
			}
        }
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Exiting with isSuccess" + isSuccess);
        }
        return isSuccess;
    }

    /**
     * This method sets the Interface Details based on the VOs values.
     * If p_useInterfacePrefixVO is True then use
     * p_MSISDNPrefixInterfaceMappingVO else use p_listValueVO to populate
     * values
     * 
     * @param p_receiverVO
     * @param p_listValueVO
     * @param p_useInterfacePrefixVO
     * @param p_MSISDNPrefixInterfaceMappingVO
     * @throws BTSLBaseException
     */
    private void setInterfaceDetails(ReceiverVO p_receiverVO, ListValueVO p_listValueVO, boolean p_useInterfacePrefixVO, MSISDNPrefixInterfaceMappingVO p_MSISDNPrefixInterfaceMappingVO) throws BTSLBaseException {
        final String METHOD_NAME = "setInterfaceDetails";
        if (_log.isDebugEnabled()) {
            _log.debug("setInterfaceDetails", _requestIDStr, " Entered p_listValueVO=" + p_listValueVO + " p_useInterfacePrefixVO=" + p_useInterfacePrefixVO + " p_MSISDNPrefixInterfaceMappingVO=" + p_MSISDNPrefixInterfaceMappingVO);
        }
        try {
            String status = null;
            String message1 = null;
            String message2 = null;

            if (p_useInterfacePrefixVO) {

                _interfaceID = p_MSISDNPrefixInterfaceMappingVO.getInterfaceID();
                _senderExternalID = p_MSISDNPrefixInterfaceMappingVO.getExternalID();
                status = p_MSISDNPrefixInterfaceMappingVO.getInterfaceStatus();
                message1 = p_MSISDNPrefixInterfaceMappingVO.getLanguage1Message();
                message2 = p_MSISDNPrefixInterfaceMappingVO.getLanguage2Message();
                _interfaceStatusType = p_MSISDNPrefixInterfaceMappingVO.getStatusType();// added
                                                                                        // by
                                                                                        // Dhiraj
                                                                                        // on
                                                                                        // 18/07/07
            } else {
                _interfaceID = p_listValueVO.getValue();
                _senderExternalID = p_listValueVO.getIDValue();
                status = p_listValueVO.getStatus();
                message1 = p_listValueVO.getOtherInfo();
                message2 = p_listValueVO.getOtherInfo2();
                _interfaceStatusType = p_listValueVO.getStatusType();// added by
                                                                     // Dhiraj
                                                                     // on
                                                                     // 18/07/07
            }
            if (!PretupsI.YES.equals(status) && PretupsI.INTERFACE_STATUS_TYPE_MANUAL.equals(_interfaceStatusType)) {
                // ChangeID=LOCALEMASTER
                // Which language message to be set is determined from the
                // locale master cache
                // If language not come from the requestVO the default language
                // will be used otherwise language obtained from the requestVO
                // will be used for locale.
                Locale locale = null;
                locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));

                if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(locale)).getMessage())) {
                    _requestVO.setSenderReturnMessage(message1);
                } else {
                    _requestVO.setSenderReturnMessage(message2);
                }
                throw new BTSLBaseException(this, "setInterfaceDetails", PretupsErrorCodesI.INTERFACE_NOT_ACTIVE);
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("setInterfaceDetails", "Getting Base Exception =" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SEnquiryHandler[setInterfaceDetails]", _requestIDStr, p_receiverVO.getMsisdn(), p_receiverVO.getNetworkCode(), "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "setInterfaceDetails", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        }
    }

    /**
     * Method to send the validation request on the interface and check whether
     * that is success of failed
     * 
     * @param p_interfaceID
     * @param p_handlerClass
     * @param p_interfaceCatgeory
     * @throws BTSLBaseException
     * @throws Exception
     */

    private boolean processValidationRequest(ReceiverVO p_receiverVO, String p_interfaceID, String p_handlerClass, String p_interfaceCatgeory) throws BTSLBaseException, Exception {
        final String METHOD_NAME = "processValidationRequest";
        if (_log.isDebugEnabled()) {
            _log.debug("processValidationRequest", "Entered and performing validations for p_interfaceID" + p_interfaceID + " p_interfaceCatgeory=" + p_interfaceCatgeory + " p_handlerClass=" + p_handlerClass);
        }
        boolean isSuccess = false;
        try {

            checkTransactionLoad(p_receiverVO, p_interfaceID);
            _decreaseCountersReqd = true;

            NetworkInterfaceModuleVO networkInterfaceModuleVOS = (NetworkInterfaceModuleVO) NetworkInterfaceModuleCache.getObject(PretupsI.C2S_MODULE, p_receiverVO.getNetworkCode(), p_interfaceCatgeory);
            _intModCommunicationTypeS = networkInterfaceModuleVOS.getCommunicationType();
            _intModIPS = networkInterfaceModuleVOS.getIP();
            _intModPortS = networkInterfaceModuleVOS.getPort();
            _intModClassNameS = networkInterfaceModuleVOS.getClassName();

            LoadController.incrementTransactionInterCounts(_requestID, LoadControllerI.SENDER_UNDER_VAL);

            CommonClient commonClient = new CommonClient();
            String requestStr = getSenderValidateStr(p_interfaceID, p_handlerClass);

            String senderValResponse = commonClient.process(requestStr, "", _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);
            HashMap map = BTSLUtil.getStringToHash(senderValResponse, "&", "=");
            String status = (String) map.get("TRANSACTION_STATUS");

            // Start: Update the Interface table for the interface ID based on
            // Handler status and update the Cache
            String interfaceStatusType = (String) map.get("INT_SET_STATUS");
            if (!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME.equals(interfaceStatusType))) {
                new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES, p_interfaceID, interfaceStatusType, PretupsErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG, PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
                // :End
            }

            ArrayList altList = null;
            boolean isRequired = false;
            if (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status)) {
                altList = InterfaceRoutingControlCache.getRoutingControlDetails(p_interfaceID);
                if (altList != null && altList.size() > 0) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("processValidationRequest", "Got Status=" + status + " After validation Request For MSISDN=" + p_receiverVO.getMsisdn() + " Performing Alternate Routing");
                    }
                    performSenderAlternateRouting(p_receiverVO, altList, p_interfaceCatgeory);
                    isSuccess = true;
                } else {
                    isRequired = true;
                }
            } else if (!InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) || isRequired) {
                if (BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS)) {
                    isSuccess = false;
                    LoadController.decreaseResponseCounters(_requestID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_VAL_RESPONSE);
                    throw new BTSLBaseException(this, "processValidationRequest", PretupsErrorCodesI.INTERFACE_ERROR_RESPONSE);
                }
                // LoadController.decreaseResponseCounters(_requestID,PretupsErrorCodesI.TXN_STATUS_SUCCESS,LoadControllerI.SENDER_VAL_RESPONSE);

                p_receiverVO.setServiceClassCode((String) map.get("SERVICE_CLASS"));
                String validateRequired = FileCache.getValue(_interfaceID, map.get("REQ_SERVICE") + "_" + map.get("USER_TYPE"));

                if (BTSLUtil.isNullString(_receiverVO.getServiceClassCode()) && ("N".equals(validateRequired))) {
                    p_receiverVO.setServiceClassCode(PretupsI.ALL);
                }
                _accountStatus = (String) map.get("ACCOUNT_STATUS");
                if (BTSLUtil.isNullString(_accountStatus)) {
                    _accountStatus = " ";
                }
                try {
                    p_receiverVO.setCurrentBalance(Long.parseLong((String) map.get("INTERFACE_PREV_BALANCE")));
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
                ;
                isSuccess = true;
                if (PretupsI.INTERFACE_CATEGORY_PRE.equals(p_interfaceCatgeory)) {
                    SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(p_receiverVO.getNetworkCode() + "_" + PretupsI.SERVICE_TYPE_C2S_ENQUIRY + "_" + p_interfaceCatgeory);
                    if (!_senderInterfaceInfoInDBFound && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
                        PretupsBL.insertSubscriberInterfaceRouting(_interfaceID, _senderExternalID, p_receiverVO.getMsisdn(), p_interfaceCatgeory, ((ChannelUserVO) _requestVO.getSenderVO()).getUserID(), _requestVO.getCreatedOn());
                    }
                }
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            isSuccess = false;
            throw be;
        } catch (Exception e) {
            isSuccess = false;
            _log.error("processValidationRequest", "BTSLBaseException " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SEnquiryHandler[process]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "processValidationRequest", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
            if (_decreaseCountersReqd) {
                LoadController.decreaseTransactionInterfaceLoad(_requestID, p_receiverVO.getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);
            }
        }
        if (_log.isDebugEnabled()) {
            _log.debug("processValidationRequest", "Exiting with isSuccess" + isSuccess);
        }

        return isSuccess;
    }

    /**
     * Method to check the loads available in the system
     * 
     * @param p_receiverVO
     * @param p_interfaceID
     * @throws BTSLBaseException
     */

    private void checkTransactionLoad(ReceiverVO p_receiverVO, String p_interfaceID) throws BTSLBaseException {
        final String METHOD_NAME = "checkTransactionLoad";
        if (_log.isDebugEnabled()) {
            _log.debug("checkTransactionLoad", "Checking load for MSISDN =" + p_interfaceID + " p_interfaceID=" + p_interfaceID);
        }
        int recieverLoadStatus = 0;
        try {
            // Do not enter the request in Queue
            recieverLoadStatus = LoadController.checkInterfaceLoad(p_receiverVO.getNetworkCode(), p_interfaceID, _requestID, new C2STransferVO(), false);
            if (recieverLoadStatus == 0) {
                LoadController.checkTransactionLoad(p_receiverVO.getNetworkCode(), p_interfaceID, PretupsI.C2S_MODULE, _requestID, true, LoadControllerI.USERTYPE_SENDER);
                if (_log.isDebugEnabled()) {
                    _log.debug("C2SEnquiryHandler[checkTransactionLoad]", "_requestID=" + _requestID + " Successfully through load");
                }
            }
            // Request in Queue
            else if (recieverLoadStatus == 1) {
                throw new BTSLBaseException("C2SEnquiryHandler", "checkTransactionLoad", PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
            }
            // Refuse the request
            else {
                throw new BTSLBaseException("C2SEnquiryHandler", "checkTransactionLoad", PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("C2SEnquiryHandler[checkTransactionLoad]", "Refusing request getting Exception:" + be.getMessage());
            _isRequestRefuse = true;
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("C2SEnquiryHandler[checkTransactionLoad]", "Refusing request getting Exception:" + e.getMessage());
            throw new BTSLBaseException("C2SEnquiryHandler", "checkTransactionLoad", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        }
    }

    public String getSenderValidateStr(String p_interfaceID, String p_handlerClass) {
        StringBuffer strBuff = null;
        strBuff = new StringBuffer("MSISDN=" + _msisdn);
        strBuff.append("&TRANSACTION_ID=" + _requestID);
        strBuff.append("&NETWORK_CODE=" + _networkCode);
        strBuff.append("&INTERFACE_ID=" + p_interfaceID);
        strBuff.append("&INTERFACE_HANDLER=" + p_handlerClass);
        strBuff.append("&INT_MOD_COMM_TYPE=" + _intModCommunicationTypeS);
        strBuff.append("&INT_MOD_IP=" + _intModIPS);
        strBuff.append("&INT_MOD_PORT=" + _intModPortS);
        strBuff.append("&INT_MOD_CLASSNAME=" + _intModClassNameS);
        strBuff.append("&MODULE=" + PretupsI.C2S_MODULE);
        strBuff.append("&INTERFACE_ACTION=" + PretupsI.INTERFACE_VALIDATE_ACTION);
        strBuff.append("&USER_TYPE=R");
        strBuff.append("&REQ_SERVICE=" + _requestVO.getServiceType());
        strBuff.append("&INT_ST_TYPE=" + _interfaceStatusType);

        return strBuff.toString();
    }

    /**
     * Method to perform the sender alternate intreface routing controls
     * 
     * @param altList
     * @throws BTSLBaseException
     */
    private void performSenderAlternateRouting(ReceiverVO p_receiverVO, ArrayList altList, String p_interfaceCatgeory) throws BTSLBaseException {
        final String METHOD_NAME = "performSenderAlternateRouting";
        if (_log.isDebugEnabled()) {
            _log.debug("performSenderAlternateRouting", _requestIDStr, " Entered with p_interfaceCatgeory=" + p_interfaceCatgeory);
        }
        try {
            if (altList != null && altList.size() > 0) {
                // Check Interface Routing if not exists then continue
                // else decrease counters
                // Validate All service class checks
                // Decrease Counters for transaction and interface
                // Check Interface and transaction load
                // Send request
                // If success then update the subscriber routing table with new
                // interface ID
                // Also store in global veriables
                // If Not Found repeat the iteration for alt 2
                ListValueVO listValueVO = null;
                String requestStr = null;
                CommonClient commonClient = null;
                String senderValResponse = null;
                switch (altList.size()) {
                case 1: {
                    LoadController.decreaseResponseCounters(_requestID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_VAL_RESPONSE);
                    LoadController.decreaseTransactionInterfaceLoad(_requestID, p_receiverVO.getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);

                    listValueVO = (ListValueVO) altList.get(0);

                    setInterfaceDetails(p_receiverVO, listValueVO, false, null);

                    checkTransactionLoad(p_receiverVO, _interfaceID);

                    requestStr = getSenderValidateStr(_interfaceID, listValueVO.getLabel());
                    commonClient = new CommonClient();

                    LoadController.incrementTransactionInterCounts(_requestID, LoadControllerI.SENDER_UNDER_VAL);

                    TransactionLog.log(_requestID, _requestIDStr, p_receiverVO.getMsisdn(), p_receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                    if (_log.isDebugEnabled()) {
                        _log.debug("performSenderAlternateRouting", "Sending Request For MSISDN=" + p_receiverVO.getMsisdn() + " on ALternate Routing 1 to =" + _interfaceID);
                    }

                    senderValResponse = commonClient.process(requestStr, _requestID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);

                    TransactionLog.log(_requestID, _requestIDStr, p_receiverVO.getMsisdn(), p_receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL, senderValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                    try {
                        senderValidateResponse(p_receiverVO, senderValResponse, 1, altList.size());
                        if (PretupsErrorCodesI.TXN_STATUS_SUCCESS.equals(_txnStatus)) {
                            LoadController.decreaseResponseCounters(_requestID, PretupsErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.SENDER_VAL_RESPONSE);
                            if (PretupsI.INTERFACE_CATEGORY_PRE.equals(p_interfaceCatgeory)) {
                                updateSubscriberRoutingDetails(p_receiverVO.getNetworkCode(), _interfaceID, _senderExternalID, p_receiverVO.getMsisdn(), p_interfaceCatgeory, ((ChannelUserVO) _requestVO.getSenderVO()).getUserID(), _requestVO.getCreatedOn());
                            }
                        }
                    } catch (BTSLBaseException be) {
                        _log.errorTrace(METHOD_NAME, be);
                        throw new BTSLBaseException(be);
                    } catch (Exception e) {
                        _log.errorTrace(METHOD_NAME, e);
                        throw new BTSLBaseException(e);
                    }

                    break;
                }
                case 2: {
                    LoadController.decreaseResponseCounters(_requestID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_VAL_RESPONSE);
                    LoadController.decreaseTransactionInterfaceLoad(_requestID, p_receiverVO.getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);

                    listValueVO = (ListValueVO) altList.get(0);

                    setInterfaceDetails(p_receiverVO, listValueVO, false, null);

                    checkTransactionLoad(p_receiverVO, _interfaceID);

                    requestStr = getSenderValidateStr(_interfaceID, listValueVO.getLabel());
                    commonClient = new CommonClient();

                    LoadController.incrementTransactionInterCounts(_requestID, LoadControllerI.SENDER_UNDER_VAL);

                    TransactionLog.log(_requestID, _requestIDStr, p_receiverVO.getMsisdn(), p_receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                    if (_log.isDebugEnabled()) {
                        _log.debug("performSenderAlternateRouting", "Sending Request For MSISDN=" + p_receiverVO.getMsisdn() + " on ALternate Routing 1 to =" + _interfaceID);
                    }

                    senderValResponse = commonClient.process(requestStr, _requestID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);

                    TransactionLog.log(_requestID, _requestIDStr, p_receiverVO.getMsisdn(), p_receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL, senderValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                    try {
                        senderValidateResponse(p_receiverVO, senderValResponse, 1, altList.size());
                        if (PretupsErrorCodesI.TXN_STATUS_SUCCESS.equals(_txnStatus)) {
                            LoadController.decreaseResponseCounters(_requestID, PretupsErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.SENDER_VAL_RESPONSE);

                            if (PretupsI.INTERFACE_CATEGORY_PRE.equals(p_interfaceCatgeory)) {
                                updateSubscriberRoutingDetails(p_receiverVO.getNetworkCode(), _interfaceID, _senderExternalID, p_receiverVO.getMsisdn(), p_interfaceCatgeory, ((ChannelUserVO) _requestVO.getSenderVO()).getUserID(), _requestVO.getCreatedOn());
                            }
                        }
                    } catch (BTSLBaseException be) {
                        _log.errorTrace(METHOD_NAME, be);
                        if (be.isKey() && InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(be.getMessageKey())) {
                            if (_log.isDebugEnabled()) {
                                _log.debug("performSenderAlternateRouting", "Got Status=" + InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND + " After validation Request For MSISDN=" + p_receiverVO.getMsisdn() + " Performing Alternate Routing to 2");
                            }

                            LoadController.decreaseResponseCounters(_requestID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_VAL_RESPONSE);
                            LoadController.decreaseTransactionInterfaceLoad(_requestID, p_receiverVO.getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);

                            listValueVO = (ListValueVO) altList.get(1);

                            setInterfaceDetails(p_receiverVO, listValueVO, false, null);

                            checkTransactionLoad(p_receiverVO, _interfaceID);

                            requestStr = getSenderValidateStr(_interfaceID, listValueVO.getLabel());

                            LoadController.incrementTransactionInterCounts(_requestID, LoadControllerI.SENDER_UNDER_VAL);

                            TransactionLog.log(_requestID, _requestIDStr, p_receiverVO.getMsisdn(), p_receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                            if (_log.isDebugEnabled()) {
                                _log.debug("performSenderAlternateRouting", "Sending Request For MSISDN=" + p_receiverVO.getMsisdn() + " on ALternate Routing 2 to =" + _interfaceID);
                            }

                            senderValResponse = commonClient.process(requestStr, _requestID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);

                            TransactionLog.log(_requestID, _requestIDStr, p_receiverVO.getMsisdn(), p_receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL, senderValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                            try {
                                senderValidateResponse(p_receiverVO, senderValResponse, 1, altList.size());
                                if (PretupsErrorCodesI.TXN_STATUS_SUCCESS.equals(_txnStatus)) {
                                    LoadController.decreaseResponseCounters(_requestID, PretupsErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.SENDER_VAL_RESPONSE);
                                    if (PretupsI.INTERFACE_CATEGORY_PRE.equals(p_interfaceCatgeory)) {
                                        updateSubscriberRoutingDetails(p_receiverVO.getNetworkCode(), _interfaceID, _senderExternalID, p_receiverVO.getMsisdn(), p_interfaceCatgeory, ((ChannelUserVO) _requestVO.getSenderVO()).getUserID(), _requestVO.getCreatedOn());
                                    }
                                }
                            } catch (BTSLBaseException bex) {
                                _log.errorTrace(METHOD_NAME, bex);
                                throw new BTSLBaseException(bex);
                            } catch (Exception e) {
                                _log.errorTrace(METHOD_NAME, e);
                                throw new BTSLBaseException(this, METHOD_NAME, e.getMessage());
                            }
                        } else {
                            throw new BTSLBaseException(be);
                        }
                    } catch (Exception e) {
                        _log.errorTrace(METHOD_NAME, e);
                        throw new BTSLBaseException(this, METHOD_NAME, e.getMessage());
                    }
                    break;
                }
                }

            } else {
                return;
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            LoadController.decreaseResponseCounters(_requestID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_VAL_RESPONSE);
            throw new BTSLBaseException(be);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            LoadController.decreaseResponseCounters(_requestID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_VAL_RESPONSE);

            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SEnquiryHandler[performSenderAlternateRouting]", _requestID, p_receiverVO.getMsisdn(), p_receiverVO.getNetworkCode(), "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "performSenderAlternateRouting", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("performSenderAlternateRouting", _requestIDStr, " Exiting ");
        }
    }

    /**
     * Method to handle sender validation response for interface routing
     * 
     * @param str
     * @param p_attempt
     * @param p_altSize
     * @throws BTSLBaseException
     */
    public void senderValidateResponse(ReceiverVO p_receiverVO, String str, int p_attempt, int p_altSize) throws BTSLBaseException {
        final String METHOD_NAME = "senderValidateResponse";
        HashMap map = BTSLUtil.getStringToHash(str, "&", "=");
        String status = (String) map.get("TRANSACTION_STATUS");
        String interfaceID = (String) map.get("INTERFACE_ID");

        // Start: Update the Interface table for the interface ID based on
        // Handler status and update the Cache
        String interfaceStatusType = (String) map.get("INT_SET_STATUS");
        if (!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME.equals(interfaceStatusType))) {
            new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES, interfaceID, interfaceStatusType, PretupsErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG, PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
            // :End
        }

        if (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) && p_attempt == 1 && p_attempt < p_altSize) {
            throw new BTSLBaseException(this, "senderValidateResponse", InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
        }
        if (BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS)) {
            throw new BTSLBaseException(this, "processValidationRequest", PretupsErrorCodesI.INTERFACE_ERROR_RESPONSE);
        }
        _txnStatus = PretupsErrorCodesI.TXN_STATUS_SUCCESS;
        p_receiverVO.setServiceClassCode((String) map.get("SERVICE_CLASS"));
        _accountStatus = (String) map.get("ACCOUNT_STATUS");
        if (BTSLUtil.isNullString(_accountStatus)) {
            _accountStatus = " ";
        }
        try {
            p_receiverVO.setCurrentBalance(Long.parseLong((String) map.get("INTERFACE_PREV_BALANCE")));
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        ;
    }

    private void updateSubscriberRoutingDetails(String p_networkCode, String p_interfaceID, String p_externalID, String p_msisdn, String p_interfaceCategory, String p_userID, Date p_currentDate) throws BTSLBaseException {
        final String METHOD_NAME = "updateSubscriberRoutingDetails";
        if (_log.isDebugEnabled()) {
            _log.debug("updateSubscriberRoutingDetails", _requestIDStr, " Entered p_networkCode=" + p_networkCode + " p_interfaceID=" + p_interfaceID + " p_externalID=" + p_externalID + " p_msisdn=" + p_msisdn + " p_interfaceCategory=" + p_interfaceCategory + " p_userID=" + p_userID + " p_currentDate=" + p_currentDate);
        }
        try {
            // Update in DB for routing interface
            if (_senderInterfaceInfoInDBFound) {
                PretupsBL.updateSubscriberInterfaceRouting(p_interfaceID, p_externalID, p_msisdn, p_interfaceCategory, p_userID, p_currentDate);
            } else {
                SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(p_networkCode + "_" + PretupsI.SERVICE_TYPE_C2S_ENQUIRY + "_" + p_interfaceCategory);
                if (!_senderInterfaceInfoInDBFound && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
                    PretupsBL.insertSubscriberInterfaceRouting(p_interfaceID, p_externalID, p_msisdn, p_interfaceCategory, p_userID, p_currentDate);
                    _senderInterfaceInfoInDBFound = true;
                }
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("updateSubscriberRoutingDetails", "Getting Base Exception =" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SEnquiryHandler[updateSubscriberRoutingDetails]", _requestIDStr, p_msisdn, p_networkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updateSubscriberRoutingDetails", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        }
    }

}
