package com.btsl.pretups.p2p.subscriber.requesthandler;

/**
 * @(#)AccountInformationController.java
 *                                       Copyright(c) 2005, Bharti Telesoft Int.
 *                                       Public Ltd.
 *                                       All Rights Reserved
 *                                       --------------------------------------
 *                                       --
 *                                       --------------------------------------
 *                                       -------------------
 *                                       Author Date History
 *                                       --------------------------------------
 *                                       --
 *                                       --------------------------------------
 *                                       -------------------
 *                                       Gurjeet Singh Bedi 08/11/05 Initial
 *                                       Creation
 *                                       Ankit Zindal 20/11/06
 *                                       ChangeID=LOCALEMASTER
 *                                       --------------------------------------
 *                                       --
 *                                       --------------------------------------
 *                                       ------------------
 */

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
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
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.util.InterfaceCloserI;
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.LookupsVO;
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
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.pretups.vastrix.businesslogic.ServiceSelectorInterfaceMappingCache;
import com.btsl.pretups.vastrix.businesslogic.ServiceSelectorInterfaceMappingVO;
import com.btsl.pretups.whitelist.businesslogic.WhiteListVO;
import com.btsl.util.BTSLUtil;

public class AccountInformationController implements ServiceKeywordControllerI {
    private Log _log = LogFactory.getLog(AccountInformationController.class.getName());
    private String _intModCommunicationTypeS;
    private String _intModIPS;
    private int _intModPortS;
    private String _intModClassNameS;
    private String _msisdn;
    private String _networkCode;
    private String _requestIDStr = null;
    private String _transferID = null;
    private boolean _isRequestRefuse = false;
    private String _interfaceID = null;
    private String _senderExternalID = null;
    private RequestVO _requestVO = null;
    private boolean _senderInterfaceInfoInDBFound = false;
    private String _txnStatus = null;
    private String _interfaceCategory = null;
    private static long _counter = 0L;
    private long _txnIDSuffix = 0L;
    private boolean _useAlternateCategory = false;
    private boolean _postPaidOfflineInterface = false;
    private ServiceInterfaceRoutingVO _serviceInterfaceRoutingVO = null;
    private boolean _decreaseCountersReqd = false;
    private String _interfaceStatusType = null;

    /*
     * public AccountInformationController()
     * {
     * _txnIDSuffix=_counter++;
     * }
     */

    public void process(RequestVO p_requestVO) {
        // Connection con = null;
        _requestVO = p_requestVO;
        // This field will be used to decide whether message has to be pushed to
        // sender or not
        _requestVO.setPushMessage(false);
        SenderVO senderVO = (SenderVO) p_requestVO.getSenderVO();
        final String METHOD_NAME = "process";
        boolean useRegisteredUserInterfaceCategory = false;
        try {
            _txnIDSuffix = getCounter();
            _requestIDStr = p_requestVO.getRequestIDStr();
            if (senderVO == null) {
                senderVO = prepareSenderVO(p_requestVO);
                if (p_requestVO.getType().equals(PretupsI.INTERFACE_CATEGORY_BOTH)) {
                    _serviceInterfaceRoutingVO = ServiceInterfaceRoutingCache
                        .getServiceInterfaceRoutingDetails(senderVO.getNetworkCode() + "_" + p_requestVO.getServiceType() + "_" + PretupsI.NOT_APPLICABLE);
                    if (_serviceInterfaceRoutingVO != null) {
                        if (_log.isDebugEnabled()) {
                            _log.debug(
                                "process",
                                _requestIDStr,
                                "For =" + senderVO.getNetworkCode() + "_" + p_requestVO.getServiceType() + " Got Interface Category=" + _serviceInterfaceRoutingVO
                                    .getInterfaceType() + " Alternate Check Required=" + _serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool() + " Alternate Interface=" + _serviceInterfaceRoutingVO
                                    .getAlternateInterfaceType());
                        }

                        _interfaceCategory = _serviceInterfaceRoutingVO.getInterfaceType();
                        _useAlternateCategory = _serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool();
                    } else {
                        _log.info("process", p_requestVO.getRequestIDStr(),
                            "Service Interface Routing control Not defined, thus using default type=" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE)));
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "PrepaidController[process]", "", "", "",
                            "Service Interface Routing control Not defined, thus using default type=" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE)));
                        _interfaceCategory = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE));

                    }
                } else {
                    _interfaceCategory = p_requestVO.getType();
                }
                senderVO = prepareSenderVO(p_requestVO, _interfaceCategory, _useAlternateCategory, _serviceInterfaceRoutingVO);
                senderVO.setRegistered(PretupsI.NO);
                p_requestVO.setSenderVO(senderVO);
                senderVO.setSubscriberType(_interfaceCategory);
            } else if (senderVO.getStatus().equals(PretupsI.USER_STATUS_NEW)) {
                _networkCode = senderVO.getNetworkCode();
                _msisdn = senderVO.getMsisdn();
                senderVO.setRegistered(PretupsI.NO);
                _interfaceCategory = senderVO.getSubscriberType();
                useRegisteredUserInterfaceCategory = true;
            } else {
                senderVO.setRegistered(PretupsI.YES);
                _msisdn = senderVO.getMsisdn();
                _networkCode = senderVO.getNetworkCode();
                _interfaceCategory = senderVO.getSubscriberType();
                useRegisteredUserInterfaceCategory = true;
            }
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) {
                if (BTSLUtil.encryptText(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))).equals(senderVO.getPin())) {
                    senderVO.setPin(PretupsI.NO);
                } else {
                    senderVO.setPin(PretupsI.YES);
                }
            } else {
                senderVO.setPin(PretupsI.NO);
            }

            _transferID = BTSLUtil.currentDateTimeFormatString() + _txnIDSuffix;

            boolean isUserExists = false;
            ServiceClassVO serviceClassVO = null;
            _log.debug("process", _requestIDStr, "Performing the interface category validation on :=" + _interfaceCategory);
            try {
                isUserExists = checkRoutingControlAndSendReq(senderVO, PretupsI.SERVICE_TYPE_ACCOUNTINFO, _interfaceCategory);
            } catch (BTSLBaseException be) {
                if (_useAlternateCategory) {
                    isUserExists = false;
                    if (_log.isDebugEnabled()) {
                        _log.debug("process", " Validation not  Successful on interface category =" + _interfaceCategory);
                    }
                } else {
                    throw be;
                }
                _log.error("process", "Msisdn not found on interface and validation interface =" + _interfaceCategory);
            }
            if (!isUserExists && _useAlternateCategory) {
                NetworkPrefixVO networkPrefixVO = null;
                // We need to get the new Prefix ID for alternate interface
                // category
                networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(senderVO.getMsisdnPrefix(), _serviceInterfaceRoutingVO.getAlternateInterfaceType());
                if (networkPrefixVO != null) {
                    senderVO.setPrefixID(networkPrefixVO.getPrefixID());
                    senderVO.setNetworkCode(networkPrefixVO.getNetworkCode());
                    senderVO.setSubscriberType(networkPrefixVO.getSeriesType());
                    isUserExists = checkRoutingControlAndSendReq(senderVO, PretupsI.SERVICE_TYPE_ACCOUNTINFO, _serviceInterfaceRoutingVO.getAlternateInterfaceType());
                    if (!isUserExists) {
                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.INTERFACE_ERROR_RESPONSE);
                    }
                    senderVO.setSubscriberType(_serviceInterfaceRoutingVO.getAlternateInterfaceType());
                } else {
                    _log.error(this, "Series =" + senderVO.getMsisdnPrefix() + " Not Defined for Series type=" + _serviceInterfaceRoutingVO.getAlternateInterfaceType());
                    throw new BTSLBaseException("", "process", PretupsErrorCodesI.ERROR_NOTFOUND_SERIES_TYPE, 0, new String[] { p_requestVO.getFilteredMSISDN() }, null);
                }
            }
            if (isUserExists) {
                // con=OracleUtil.getConnection();
                if (!BTSLUtil.isNullString(senderVO.getServiceClassCode())) {
                    // ServiceClassDAO serviceClassDAO=new ServiceClassDAO();
                    // serviceClassVO=serviceClassDAO.loadServiceClassInfoByCode(con,senderVO.getServiceClassCode(),_interfaceID);
                    serviceClassVO = ServiceClassInfoByCodeCache.getServiceClassByCode(senderVO.getServiceClassCode(), _interfaceID);
                    if (serviceClassVO == null) {
                        // serviceClassVO=serviceClassDAO.loadServiceClassInfoByCode(con,PretupsI.ALL,_interfaceID);
                        serviceClassVO = ServiceClassInfoByCodeCache.getServiceClassByCode(PretupsI.ALL, _interfaceID);
                        if (serviceClassVO != null) {
                            senderVO.setServiceClassID(serviceClassVO.getServiceClassId());
                        }
                    } else {
                        senderVO.setServiceClassID(serviceClassVO.getServiceClassId());
                    }
                }
                if (serviceClassVO != null && !BTSLUtil.isNullString(senderVO.getServiceClassID())) {
                    loadSenderLimits(senderVO);
                } else {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ERROR_INTFCE_SRVCECLSS_NOTFOUND);
                }
            } else {
                // try{if (con != null){con.rollback();}} catch (Exception e){}
                // Setting Different error codes in case user was registered on
                // one interface category but now
                // while getting account information it was not found on the
                // interface category
                if (useRegisteredUserInterfaceCategory) {
                    p_requestVO.setMessageCode(PretupsErrorCodesI.REG_USER_NOT_FOUND_ON_INTERFACE);
                    p_requestVO.setMessageArguments(new String[] { ((LookupsVO) LookupsCache.getObject(PretupsI.SUBSRICBER_TYPE, _interfaceCategory)).getLookupName() });
                } else {
                    p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
                }
                return;
            }
            p_requestVO.setMessageCode(PretupsI.TXN_STATUS_SUCCESS);

        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            // try{if (con != null){con.rollback();}} catch (Exception e){}
            _log.error("process", _requestIDStr, "BTSLBaseException " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            }
        } catch (Exception e) {
            p_requestVO.setSuccessTxn(false);
            // try{if (con != null){con.rollback();}} catch (Exception ee){}
            _log.error("process", _requestIDStr, "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AccountInformationController[process]", "", "", "",
                "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
        } finally {
            // try {if (con != null)con.close();} catch (Exception e){}
            if (_log.isDebugEnabled()) {
                _log.debug("process", _requestIDStr, "Exited");
            }
        }
    }

    /**
     * To get the Sender Basic Network Details
     * 
     * @param p_requestVO
     * @return
     */
    public SenderVO prepareSenderVO(RequestVO p_requestVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("prepareSenderVO", _requestIDStr, "Entered");
        }
        final SenderVO senderVO = new SenderVO();
        senderVO.setMsisdnPrefix(PretupsBL.getMSISDNPrefix(p_requestVO.getFilteredMSISDN()));
        senderVO.setMsisdn(p_requestVO.getFilteredMSISDN());
        senderVO.setModule(PretupsI.P2P_MODULE);
        _msisdn = senderVO.getMsisdn();

        final NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(senderVO.getMsisdnPrefix());
        senderVO.setPrefixID(networkPrefixVO.getPrefixID());
        senderVO.setNetworkCode(networkPrefixVO.getNetworkCode());
        _networkCode = senderVO.getNetworkCode();
        if (_log.isDebugEnabled()) {
            _log.debug("prepareSenderVO", _requestIDStr, "Exiting with Sender Prefix ID as =" + networkPrefixVO.getPrefixID());
        }
        return senderVO;

    }

    /**
     * Common Method to prepare the Sender VO to be inserted in DB
     * This method will get the Prefix ID on the basis of the interface
     * category,
     * If Not found and alternate category is true then get the prefix ID of the
     * alternate interface category
     * If that is not found then give the error , but if found then set the
     * _useAlternateCategory flag as
     * false to indicate that interface level routing is already done.
     * 
     * @param p_requestVO
     * @param p_interfaceCategory
     * @param p_useAlternate
     * @param p_serviceInterfaceRoutingVO
     * @return
     * @throws BTSLBaseException
     */
    public SenderVO prepareSenderVO(RequestVO p_requestVO, String p_interfaceCategory, boolean p_useAlternate, ServiceInterfaceRoutingVO p_serviceInterfaceRoutingVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("prepareSenderVO", _requestIDStr, "Entered");
        }
        final SenderVO senderVO = new SenderVO();
        senderVO.setMsisdnPrefix(PretupsBL.getMSISDNPrefix(p_requestVO.getFilteredMSISDN()));
        senderVO.setMsisdn(p_requestVO.getFilteredMSISDN());
        _msisdn = senderVO.getMsisdn();

        NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(senderVO.getMsisdnPrefix(), p_interfaceCategory);
        if (networkPrefixVO != null) {
            senderVO.setPrefixID(networkPrefixVO.getPrefixID());
            senderVO.setNetworkCode(networkPrefixVO.getNetworkCode());
            senderVO.setSubscriberType(networkPrefixVO.getSeriesType());
        } else if (p_useAlternate) {
            if (_log.isDebugEnabled()) {
                _log.debug(
                    "prepareSenderVO",
                    p_requestVO.getRequestIDStr(),
                    "Network Prefix Not Found For Series=" + senderVO.getMsisdnPrefix() + " and Type=" + p_interfaceCategory + " and thus using Type as =" + p_serviceInterfaceRoutingVO
                        .getAlternateInterfaceType() + " _useAlternateCategory was true");
            }

            _useAlternateCategory = false;
            _interfaceCategory = p_serviceInterfaceRoutingVO.getAlternateInterfaceType();
            networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(senderVO.getMsisdnPrefix(), _interfaceCategory);
            if (networkPrefixVO != null) {
                senderVO.setPrefixID(networkPrefixVO.getPrefixID());
                senderVO.setNetworkCode(networkPrefixVO.getNetworkCode());
                senderVO.setSubscriberType(networkPrefixVO.getSeriesType());
            } else {
                _log.error(this, "Series =" + senderVO.getMsisdnPrefix() + " Not Defined for Series type=" + _interfaceCategory);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "AccountInformation[prepareSenderVO]", "", "", "",
                    "Series =" + senderVO.getMsisdnPrefix() + " Not Defined for Series type=" + _interfaceCategory + " But required for validation ");
                throw new BTSLBaseException("", "process", PretupsErrorCodesI.ERROR_NOTFOUND_SERIES_TYPE, 0, new String[] { p_requestVO.getFilteredMSISDN() }, null);
            }
        } else {
            _log.error(this, "Series =" + senderVO.getMsisdnPrefix() + " Not Defined for Series type=" + _interfaceCategory);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "AccountInformation[prepareSenderVO]", "", "", "",
                "Series =" + senderVO.getMsisdnPrefix() + " Not Defined for  Series type=" + _interfaceCategory + " But required for validation ");
            throw new BTSLBaseException("", "process", PretupsErrorCodesI.ERROR_NOTFOUND_SERIES_TYPE, 0, new String[] { p_requestVO.getFilteredMSISDN() }, null);
        }
        _networkCode = senderVO.getNetworkCode();
        if (_log.isDebugEnabled()) {
            _log.debug("prepareSenderVO", _requestIDStr, "Exiting");
        }
        return senderVO;

    }

    /**
     * Method to check routing controls i.e. whether to get the interface
     * details from database or series based
     * 
     * @param p_senderVO
     * @param p_serviceType
     * @param p_interfaceCatgeory
     * @return
     * @throws BTSLBaseException
     */
    public boolean checkRoutingControlAndSendReq(SenderVO p_senderVO, String p_serviceType, String p_interfaceCatgeory) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("checkRoutingControlAndSendReq", "Entered with p_serviceType" + p_serviceType + " p_interfaceCatgeory=" + p_interfaceCatgeory);
        }
        boolean isSuccess = false;
        final String METHOD_NAME = "checkRoutingControlAndSendReq";
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
            final SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache
                .getRoutingControlDetails(p_senderVO.getNetworkCode() + "_" + p_serviceType + "_" + p_interfaceCatgeory);
            if (subscriberRoutingControlVO != null) {
                // Set intentionally to get a unique transfer ID
                _transferID = BTSLUtil.currentDateTimeFormatString() + _txnIDSuffix;
                if (_log.isDebugEnabled()) {
                    _log.debug("checkRoutingControlAndSendReq",
                        "Generated Temp Transfer ID for Registration Controller" + _transferID + " Database Check Required=" + subscriberRoutingControlVO
                            .isDatabaseCheckBool() + " Series Check Required=" + subscriberRoutingControlVO.isSeriesCheckBool());
                }
                if (subscriberRoutingControlVO.isDatabaseCheckBool()) {
                    mcomCon = new MComConnection();con=mcomCon.getConnection();
                    if (p_interfaceCatgeory.equalsIgnoreCase(PretupsI.INTERFACE_CATEGORY_PRE)) {
                        final ListValueVO listValueVO = PretupsBL.validateNumberInRoutingDatabase(con, p_senderVO.getMsisdn(), p_interfaceCatgeory);
						if (mcomCon != null) {
							mcomCon.close("AccountInformationController#checkRoutingControlAndSendReq");
							mcomCon = null;
						}
                        if (listValueVO != null) {
                            _senderInterfaceInfoInDBFound = true;

                            setInterfaceDetails(p_senderVO, listValueVO, false, null);

                            // Send Request to interface
                            if (_log.isDebugEnabled()) {
                                _log.debug("checkRoutingControlAndSendReq",
                                    "Sending Validation Request For MSISDN=" + p_senderVO.getMsisdn() + " On interface=" + _interfaceID);
                            }

                            isSuccess = processValidationRequest(p_senderVO, listValueVO.getValue(), listValueVO.getLabel(), p_interfaceCatgeory);
                        } else if (subscriberRoutingControlVO.isSeriesCheckBool()) {
                            if (_log.isDebugEnabled()) {
                                _log.debug("checkRoutingControlAndSendReq", _requestIDStr,
                                    " MSISDN=" + p_senderVO.getMsisdn() + " Not found in Subscriber Routing DB, check for Series");
                            }
                            ServiceSelectorInterfaceMappingVO interfaceMappingVO1 = null;
                            MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null;
                            try {
                                	
                                	 if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue()) {
                                         interfaceMappingVO1 = (ServiceSelectorInterfaceMappingVO) ServiceSelectorInterfaceMappingCache.getObject(p_serviceType+ "_" +  PretupsI.SERVICE_DEFAULT_SELECTOR + "_" + PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION + "_" + p_senderVO.getNetworkCode() + "_" + p_senderVO.getPrefixID());
                                         if (interfaceMappingVO1 != null) {
                                            // setInterfaceDetails(p_prefixID, p_userType, null, true, interfaceMappingVO1);
                                             setInterfaceDetails(p_senderVO, null, true, interfaceMappingVO1);
                                             isSuccess = processValidationRequest(p_senderVO, _interfaceID, interfaceMappingVO1.getHandlerClass(), p_interfaceCatgeory);

                                         }
                                     }
                                     if (interfaceMappingVO1 == null) {
                                         
                                    	    interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_senderVO.getPrefixID(), p_senderVO
                                                    .getSubscriberType(), PretupsI.INTERFACE_VALIDATE_ACTION);

                                                setInterfaceDetails(p_senderVO, null, true, interfaceMappingVO);

                                                isSuccess = processValidationRequest(p_senderVO, _interfaceID, interfaceMappingVO.getHandlerClass(), p_interfaceCatgeory);
                                    }

                   
                            } catch (BTSLBaseException be) {
                                if (be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.MSISDN_PREFIX_INTERFACE_MAPPING_NOTFOUND))) {
                                    _log.error(
                                        this,
                                        "Network Prefix Series Not Defined for p_senderVO.getPrefixID()=" + p_senderVO.getPrefixID() + " p_senderVO.getSubscriberType()= " + p_senderVO
                                            .getSubscriberType() + " Action=" + PretupsI.INTERFACE_VALIDATE_ACTION);
                                    if (_log.isDebugEnabled()) {
                                        _log.debug("checkRoutingControlAndSendReq", _requestIDStr,
                                            " MSISDN=" + p_senderVO.getMsisdn() + " Not found in Subscriber Routing DB, check for Series");
                                    }
                                    EventHandler
                                        .handle(
                                            EventIDI.SYSTEM_INFO,
                                            EventComponentI.SYSTEM,
                                            EventStatusI.RAISED,
                                            EventLevelI.INFO,
                                            "RegistrationController[checkRoutingControlAndSendReq]",
                                            "",
                                            "",
                                            "",
                                            "Interface Network Series Mapping Not exist for Series =" + p_senderVO.getMsisdnPrefix() + " Not Defined for Series type=" + p_senderVO
                                                .getSubscriberType() + " But validation required on that interface");
                                    isSuccess = false;
                                } else {
                                    throw be;
                                }
                            }
                        } else {
                            return isSuccess;
                        }
                    } else if (p_interfaceCatgeory.equalsIgnoreCase(PretupsI.INTERFACE_CATEGORY_POST)) {
                        final WhiteListVO whiteListVO = PretupsBL.validateNumberInWhiteList(con, p_senderVO.getMsisdn());
						if (mcomCon != null) {
							mcomCon.close("AccountInformationController#checkRoutingControlAndSendReq");
							mcomCon = null;
						}
                        if (whiteListVO != null) {
                            _senderInterfaceInfoInDBFound = true;
                            // Send Request to interface
                            final ListValueVO listValueVO = whiteListVO.getListValueVO();

                            _postPaidOfflineInterface = true;

                            setInterfaceDetails(p_senderVO, listValueVO, false, null);

                            p_senderVO.setServiceClassCode(whiteListVO.getServiceClassCode());
                            p_senderVO.setAccountStatus(whiteListVO.getAccountStatus());
                            p_senderVO.setCurrentBalance(whiteListVO.getCreditLimit());

                            // Since Number was found in White List there is no
                            // need to send validation request to the
                            // interface thereby registering the user after it.
                            isSuccess = true;
                        } else if (subscriberRoutingControlVO.isSeriesCheckBool()) {
                            if (_log.isDebugEnabled()) {
                                _log.debug("checkRoutingControlAndSendReq", _requestIDStr,
                                    " MSISDN=" + p_senderVO.getMsisdn() + " Not found in White List Routing DB, check for Series");
                            }

                            ServiceSelectorInterfaceMappingVO interfaceMappingVO1 = null;
                            MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null;
                            try {
                                	
                                	 if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue()) {
                                         interfaceMappingVO1 = (ServiceSelectorInterfaceMappingVO) ServiceSelectorInterfaceMappingCache.getObject(p_serviceType + "_" +  PretupsI.SERVICE_DEFAULT_SELECTOR + "_" + PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION + "_" + p_senderVO.getNetworkCode() + "_" + p_senderVO.getPrefixID());
                                         if (interfaceMappingVO1 != null) {
                                            // setInterfaceDetails(p_prefixID, p_userType, null, true, interfaceMappingVO1);
                                             setInterfaceDetails(p_senderVO, null, true, interfaceMappingVO1);
                                             isSuccess = processValidationRequest(p_senderVO, _interfaceID, interfaceMappingVO1.getHandlerClass(), p_interfaceCatgeory);

                                         }
                                     }
                                     if (interfaceMappingVO1 == null) {
                                         
                                    	    interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_senderVO.getPrefixID(), p_senderVO
                                                    .getSubscriberType(), PretupsI.INTERFACE_VALIDATE_ACTION);

                                                setInterfaceDetails(p_senderVO, null, true, interfaceMappingVO);

                                                isSuccess = processValidationRequest(p_senderVO, _interfaceID, interfaceMappingVO.getHandlerClass(), p_interfaceCatgeory);
                                    }
                            } catch (BTSLBaseException be) {
                                if (be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.MSISDN_PREFIX_INTERFACE_MAPPING_NOTFOUND))) {
                                    _log.error(
                                        this,
                                        "Network Prefix Series Not Defined for p_senderVO.getPrefixID()=" + p_senderVO.getPrefixID() + " p_senderVO.getSubscriberType()= " + p_senderVO
                                            .getSubscriberType() + " Action=" + PretupsI.INTERFACE_VALIDATE_ACTION);
                                    EventHandler
                                        .handle(
                                            EventIDI.SYSTEM_INFO,
                                            EventComponentI.SYSTEM,
                                            EventStatusI.RAISED,
                                            EventLevelI.INFO,
                                            "RegistrationController[checkRoutingControlAndSendReq]",
                                            "",
                                            "",
                                            "",
                                            "Interface Network Series Mapping Not exist for Series =" + p_senderVO.getMsisdnPrefix() + " Not Defined for Series type=" + p_senderVO
                                                .getSubscriberType() + " But validation required on that interface");
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
						mcomCon.close("AccountInformationController#checkRoutingControlAndSendReq");
						mcomCon = null;
					}
                } else if (subscriberRoutingControlVO.isSeriesCheckBool()) {
                    ServiceSelectorInterfaceMappingVO interfaceMappingVO1 = null;
                    MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null;
                    try {
                        	
                        	 if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue()) {
                                 interfaceMappingVO1 = (ServiceSelectorInterfaceMappingVO) ServiceSelectorInterfaceMappingCache.getObject(p_serviceType + "_" +  PretupsI.SERVICE_DEFAULT_SELECTOR + "_" + PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION + "_" + p_senderVO.getNetworkCode() + "_" + p_senderVO.getPrefixID());
                                 if (interfaceMappingVO1 != null) {
                                    // setInterfaceDetails(p_prefixID, p_userType, null, true, interfaceMappingVO1);
                                     setInterfaceDetails(p_senderVO, null, true, interfaceMappingVO1);
                                     isSuccess = processValidationRequest(p_senderVO, _interfaceID, interfaceMappingVO1.getHandlerClass(), p_interfaceCatgeory);

                                 }
                             }
                             if (interfaceMappingVO1 == null) {
                                 
                            	    interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_senderVO.getPrefixID(), p_senderVO
                                            .getSubscriberType(), PretupsI.INTERFACE_VALIDATE_ACTION);

                                        setInterfaceDetails(p_senderVO, null, true, interfaceMappingVO);

                                        isSuccess = processValidationRequest(p_senderVO, _interfaceID, interfaceMappingVO.getHandlerClass(), p_interfaceCatgeory);
                            }
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.MSISDN_PREFIX_INTERFACE_MAPPING_NOTFOUND))) {
                            _log.error(
                                this,
                                "Network Prefix Series Not Defined for p_senderVO.getPrefixID()=" + p_senderVO.getPrefixID() + " p_senderVO.getSubscriberType()= " + p_senderVO
                                    .getSubscriberType() + " Action=" + PretupsI.INTERFACE_VALIDATE_ACTION);
                            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO,
                                "RegistrationController[checkRoutingControlAndSendReq]", "", "", "", "Interface Network Series Mapping Not exist for Series =" + p_senderVO
                                    .getMsisdnPrefix() + " Not Defined for Series type=" + p_senderVO.getSubscriberType() + " But validation required on that interface");
                            isSuccess = false;
                        } else {
                            throw be;
                        }
                    }
                } else {
                    return isSuccess;
                }
            } else {
                _log.error(this,
                    "Routing Controls Not Defined for Key=" + p_senderVO.getNetworkCode() + "_" + p_serviceType + "_" + p_interfaceCatgeory + " Thus returning false");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO,
                    "RegistrationController[checkRoutingControlAndSendReq]", "", "", "",
                    "Routing Controls Not Defined For Key =" + p_senderVO.getNetworkCode() + "_" + p_serviceType + "_" + p_interfaceCatgeory);
                return isSuccess;
            }
        } catch (BTSLBaseException be) {
            // be.printStackTrace();
            _log.error("checkRoutingControlAndSendReq", "BTSLBaseException " + be.getMessage());
            if (be.isKey() && !_isRequestRefuse) {
                throw be;
            } else if (!_isRequestRefuse) {
                isSuccess = false;
            } else {
                throw be;
            }
        } catch (Exception e) {
            _log.error("checkRoutingControlAndSendReq", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "AccountInformationController[checkRoutingControlAndSendReq]", p_senderVO.getMsisdn(), p_senderVO.getNetworkCode(), "", "Exception:" + e.getMessage());
            if (!_isRequestRefuse) {
                isSuccess = false;
            } else {
                throw new BTSLBaseException(this, "checkRoutingControlAndSendReq", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            }
        } finally {
			if (mcomCon != null) {
				mcomCon.close("AccountInformationController#checkRoutingControlAndSendReq");
				mcomCon = null;
			}
        }
        if (_log.isDebugEnabled()) {
            _log.debug("checkRoutingControlAndSendReq", "Exiting with isSuccess" + isSuccess);
        }
        return isSuccess;
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

    private boolean processValidationRequest(SenderVO p_senderVO, String p_interfaceID, String p_handlerClass, String p_interfaceCatgeory) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("processValidationRequest",
                "Entered and performing validations for p_interfaceID" + p_interfaceID + " p_interfaceCatgeory=" + p_interfaceCatgeory + " p_handlerClass=" + p_handlerClass);
        }
        boolean isSuccess = false;
        final String METHOD_NAME = "processValidationRequest";
        try {
            checkTransactionLoad(p_senderVO, p_interfaceID);
            _decreaseCountersReqd = true;

            final NetworkInterfaceModuleVO networkInterfaceModuleVOS = (NetworkInterfaceModuleVO) NetworkInterfaceModuleCache.getObject(PretupsI.P2P_MODULE, p_senderVO
                .getNetworkCode(), p_interfaceCatgeory);
            _intModCommunicationTypeS = networkInterfaceModuleVOS.getCommunicationType();
            _intModIPS = networkInterfaceModuleVOS.getIP();
            _intModPortS = networkInterfaceModuleVOS.getPort();
            _intModClassNameS = networkInterfaceModuleVOS.getClassName();

            LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_VAL);

            final CommonClient commonClient = new CommonClient();
            final String requestStr = getSenderValidateStr(p_interfaceID, p_handlerClass);

            final String senderValResponse = commonClient.process(requestStr, "", _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);
            final HashMap map = BTSLUtil.getStringToHash(senderValResponse, "&", "=");
            final String status = (String) map.get("TRANSACTION_STATUS");
            final String subscriptions = (String) map.get("SUBSCRIPTIONS");
            final String accountBalance = (String) map.get("INTERFACE_PREV_BALANCE");
            p_senderVO.setSubscriptions(subscriptions);
            p_senderVO.setAccountBalance(accountBalance);
            // Start: Update the Interface table for the interface ID based on
            // Handler status and update the Cache
            final String interfaceStatusType = (String) map.get("INT_SET_STATUS");
            if (!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME
                .equals(interfaceStatusType))) {
                new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES, p_interfaceID, interfaceStatusType, PretupsErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG,
                    PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
                // :End
            }

            ArrayList altList = null;
            boolean isRequired = false;
            if (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status)) {
                altList = InterfaceRoutingControlCache.getRoutingControlDetails(p_interfaceID);
                if (altList != null && !altList.isEmpty()) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("processValidationRequest",
                            "Got Status=" + status + " After validation Request For MSISDN=" + p_senderVO.getMsisdn() + " Performing Alternate Routing");
                    }
                    performSenderAlternateRouting(p_senderVO, altList, p_interfaceCatgeory);
                    isSuccess = true;
                } else {
                    isRequired = true;
                }
            } else if (!InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) || isRequired) {
                if (BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS)) {
                    isSuccess = false;
                    LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_VAL_RESPONSE);
                    throw new BTSLBaseException(this, "processValidationRequest", PretupsErrorCodesI.INTERFACE_ERROR_RESPONSE);
                }
                LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.SENDER_VAL_RESPONSE);

                p_senderVO.setServiceClassCode((String) map.get("SERVICE_CLASS"));
                p_senderVO.setAccountStatus((String) map.get("ACCOUNT_STATUS"));
                try {
                    p_senderVO.setCurrentBalance(Long.parseLong((String) map.get("INTERFACE_PREV_BALANCE")));
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
                ;
                isSuccess = true;
                if (PretupsI.INTERFACE_CATEGORY_PRE.equals(p_interfaceCatgeory)) {
                    final SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache
                        .getRoutingControlDetails(p_senderVO.getNetworkCode() + "_" + PretupsI.SERVICE_TYPE_ACCOUNTINFO + "_" + p_interfaceCatgeory);
                    if (!_senderInterfaceInfoInDBFound && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
                        PretupsBL.insertSubscriberInterfaceRouting(_interfaceID, _senderExternalID, p_senderVO.getMsisdn(), p_interfaceCatgeory, p_senderVO.getUserID(),
                            _requestVO.getCreatedOn());
                    }
                }
            }
        } catch (BTSLBaseException be) {
            isSuccess = false;
            throw be;
        } catch (Exception e) {
            isSuccess = false;
            _log.error("processValidationRequest", "BTSLBaseException " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AccountInformationController[process]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "processValidationRequest", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (_decreaseCountersReqd) {
                LoadController.decreaseTransactionInterfaceLoad(_transferID, p_senderVO.getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);
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
     * @param p_senderVO
     * @param p_interfaceID
     * @throws BTSLBaseException
     */

    private void checkTransactionLoad(SenderVO p_senderVO, String p_interfaceID) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("checkTransactionLoad", "Checking load for MSISDN =" + p_interfaceID + " p_interfaceID=" + p_interfaceID);
        }
        final String METHOD_NAME = "checkTransactionLoad";
        int recieverLoadStatus = 0;
        try {
            // Do not enter the request in Queue
            recieverLoadStatus = LoadController.checkInterfaceLoad(p_senderVO.getNetworkCode(), p_interfaceID, _transferID, new C2STransferVO(), false);
            if (recieverLoadStatus == 0) {
                LoadController.checkTransactionLoad(p_senderVO.getNetworkCode(), p_interfaceID, PretupsI.P2P_MODULE, _transferID, true, LoadControllerI.USERTYPE_SENDER);
                if (_log.isDebugEnabled()) {
                    _log.debug("AccountInformationController[checkTransactionLoad]", "_transferID=" + _transferID + " Successfully through load");
                }
            }
            // Request in Queue
            else if (recieverLoadStatus == 1) {
                throw new BTSLBaseException("AccountInformationController", "checkTransactionLoad", PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
            }
            // Refuse the request
            else {
                throw new BTSLBaseException("AccountInformationController", "checkTransactionLoad", PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
            }
        } catch (BTSLBaseException be) {
            _log.error("AccountInformationController[checkTransactionLoad]", "Refusing request getting Exception:" + be.getMessage());
            _isRequestRefuse = true;
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("AccountInformationController[checkTransactionLoad]", "Refusing request getting Exception:" + e.getMessage());
            throw new BTSLBaseException("AccountInformationController", "checkTransactionLoad", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }
    }

    /**
     * Method to generate the Validate Request to the Interface
     * 
     * @param p_interfaceID
     * @param p_handlerClass
     * @return String
     */
    public String getSenderValidateStr(String p_interfaceID, String p_handlerClass) {
        StringBuffer strBuff = null;
        strBuff = new StringBuffer("MSISDN=" + _msisdn);
        strBuff.append("&TRANSACTION_ID=" + _transferID);
        strBuff.append("&NETWORK_CODE=" + _networkCode);
        strBuff.append("&INTERFACE_ID=" + p_interfaceID);
        strBuff.append("&INTERFACE_HANDLER=" + p_handlerClass);
        strBuff.append("&INT_MOD_COMM_TYPE=" + _intModCommunicationTypeS);
        strBuff.append("&INT_MOD_IP=" + _intModIPS);
        strBuff.append("&INT_MOD_PORT=" + _intModPortS);
        strBuff.append("&INT_MOD_CLASSNAME=" + _intModClassNameS);
        strBuff.append("&MODULE=" + PretupsI.P2P_MODULE);
        strBuff.append("&INTERFACE_ACTION=" + PretupsI.INTERFACE_VALIDATE_ACTION);
        strBuff.append("&USER_TYPE=S");
        strBuff.append("&REQ_SERVICE=" + _requestVO.getServiceType());
        strBuff.append("&INT_ST_TYPE=" + _interfaceStatusType);

        return strBuff.toString();
    }

    /**
     * Method to store the values of the sender based on service class
     * 
     * @param p_senderVO
     * @throws BTSLBaseException
     */
    private void loadSenderLimits(SenderVO p_senderVO) throws BTSLBaseException {
        Object serviceObjVal = null;
        final String METHOD_NAME = "loadSenderLimits";
        try {
            serviceObjVal = PretupsBL.getServiceClassObject(p_senderVO.getServiceClassID(), PreferenceI.P2P_MAX_PTAGE_TRANSFER_CODE, p_senderVO.getNetworkCode(),
                PretupsI.P2P_MODULE, false, PretupsI.ALL);
            if (serviceObjVal != null) {
                final int perTransfer = ((Integer) serviceObjVal).intValue();
                p_senderVO.setMaxPerTransferAllowed(perTransfer);
                /*
                 * long maxSenderAmt =
                 * (long)((p_senderVO.getCurrentBalance()*((double
                 * )(perTransfer)/(double)100)));
                 * p_senderVO.setMaxPerTransferAllowed(Double.parseDouble(PretupsBL
                 * .getDisplayAmount(maxSenderAmt)));
                 */
            }
            serviceObjVal = null;
            serviceObjVal = PretupsBL.getServiceClassObject(p_senderVO.getServiceClassID(), PreferenceI.P2P_MINTRNSFR_AMOUNT, p_senderVO.getNetworkCode(),
                PretupsI.P2P_MODULE, false, PretupsI.ALL);
            if (serviceObjVal != null) {
                final long minTransfer = ((Long) serviceObjVal).longValue();
                p_senderVO.setMinTxnAmountAllowed(Double.parseDouble(PretupsBL.getDisplayAmount(minTransfer)));
            }
            serviceObjVal = null;
            serviceObjVal = PretupsBL.getServiceClassObject(p_senderVO.getServiceClassID(), PreferenceI.P2P_MAXTRNSFR_AMOUNT, p_senderVO.getNetworkCode(),
                PretupsI.P2P_MODULE, false, PretupsI.ALL);
            if (serviceObjVal != null) {
                final long maxTransfer = ((Long) serviceObjVal).longValue();
                p_senderVO.setMaxTxnAmountAllowed(Double.parseDouble(PretupsBL.getDisplayAmount(maxTransfer)));
            }

            serviceObjVal = null;
            serviceObjVal = PretupsBL.getServiceClassObject(p_senderVO.getServiceClassID(), PreferenceI.MIN_RESIDUAL_BAL_CODE, p_senderVO.getNetworkCode(),
                PretupsI.P2P_MODULE, false, PretupsI.ALL);
            if (serviceObjVal != null) {
                final long minResidualValue = ((Long) serviceObjVal).longValue();
                // long
                // minResidualValue=((Long)PreferenceCache.getServicePreference(PreferenceI.MIN_RESIDUAL_BAL_CODE,senderVO.getNetworkCode(),PretupsI.P2P_MODULE,service_class)).longValue();
                if (minResidualValue != 0) {
                    if (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_RESIDUAL_BAL_TYPE_CODE)).equalsIgnoreCase(PretupsI.SYSTEM_AMOUNT)) {
                        p_senderVO.setMinResidualBalanceAllowed(Double.parseDouble(PretupsBL.getDisplayAmount(minResidualValue)));
                    } else // if percentage
                    {
                        if (PretupsI.INTERFACE_CATEGORY_PRE.equals(p_senderVO.getSubscriberType())) {
                            // final long minResidualAmt = (long) ((p_senderVO.getCurrentBalance() * ((double) (minResidualValue) / (double) 100)));
                            final long minResidualAmt = BTSLUtil.parseDoubleToLong((p_senderVO.getCurrentBalance() * ( (minResidualValue) / 100D)));
                            p_senderVO.setMinResidualBalanceAllowed(Double.parseDouble(PretupsBL.getDisplayAmount(minResidualAmt)));
                        } else {
                            // If User is PostPaid Subscriber then curent
                            // Balance will be credit Limit - the total monthly
                            // Transfer Amount Done (Only if OffLine interface
                            // this there)
                            long currentBalance = p_senderVO.getCurrentBalance();
                            if (_postPaidOfflineInterface) {
                                final boolean isPeriodChange = BTSLUtil.isPeriodChangeBetweenDates(p_senderVO.getLastSuccessTransferDate(), new Date(), BTSLUtil.PERIOD_MONTH);
                                if (!isPeriodChange) {
                                    currentBalance = p_senderVO.getCurrentBalance() - p_senderVO.getMonthlyTransferAmount();
                                }
                            }

                            final long minResidualAmt = BTSLUtil.parseDoubleToLong((currentBalance * ((minResidualValue) /  100D)));
                            p_senderVO.setMinResidualBalanceAllowed(Double.parseDouble(PretupsBL.getDisplayAmount(minResidualAmt)));
                        }
                    }
                }
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("loadSenderLimits", "  Exception while getting the Service class object from cache :" + be.getMessage());
            throw new BTSLBaseException("AccountInformationController", "loadSenderLimits", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("loadSenderLimits", "  Exception while getting the Service class object from cache :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AccountInformationController[getServiceClassObject]",
                "", "", "", "Exception while getting the Service class object from cache" + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException("AccountInformationController", "loadSenderLimits", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }

    }

    /**
     * Method to perform the sender alternate intreface routing controls
     * 
     * @param altList
     * @throws BTSLBaseException
     */
    private void performSenderAlternateRouting(SenderVO p_senderVO, ArrayList altList, String p_interfaceCatgeory) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("performSenderAlternateRouting", _requestIDStr, " Entered with p_interfaceCatgeory=" + p_interfaceCatgeory);
        }
        final String METHOD_NAME = "performSenderAlternateRouting";
        try {
            if (altList != null && !altList.isEmpty()) {
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
                    LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_VAL_RESPONSE);
                    LoadController.decreaseTransactionInterfaceLoad(_transferID, p_senderVO.getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);

                    listValueVO = (ListValueVO) altList.get(0);

                    setInterfaceDetails(p_senderVO, listValueVO, false, null);

                    checkTransactionLoad(p_senderVO, _interfaceID);

                    requestStr = getSenderValidateStr(_interfaceID, listValueVO.getLabel());
                    commonClient = new CommonClient();

                    LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_VAL);

                    TransactionLog.log(_transferID, _requestIDStr, p_senderVO.getMsisdn(), p_senderVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ,
                        PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                    if (_log.isDebugEnabled()) {
                        _log.debug("performSenderAlternateRouting", "Sending Request For MSISDN=" + p_senderVO.getMsisdn() + " on ALternate Routing 1 to =" + _interfaceID);
                    }

                    senderValResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);

                    TransactionLog.log(_transferID, _requestIDStr, p_senderVO.getMsisdn(), p_senderVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES,
                        PretupsI.TXN_LOG_TXNSTAGE_INVAL, senderValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                    try {
                        senderValidateResponse(p_senderVO, senderValResponse, 1, altList.size());
                        if (PretupsErrorCodesI.TXN_STATUS_SUCCESS.equals(_txnStatus)) {
                            LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.SENDER_VAL_RESPONSE);
                            if (PretupsI.INTERFACE_CATEGORY_PRE.equals(p_interfaceCatgeory)) {
                                updateSubscriberRoutingDetails(p_senderVO.getNetworkCode(), _interfaceID, _senderExternalID, p_senderVO.getMsisdn(), p_interfaceCatgeory,
                                    p_senderVO.getUserID(), _requestVO.getCreatedOn());
                            }
                        }
                    } catch (BTSLBaseException be) {
                        throw be;
                    } catch (Exception e) {
                    	throw new BTSLBaseException(this, METHOD_NAME, "Exception in performing the sender alternate intreface routing controls case 1");
                    }

                    break;
                }
                case 2: {
                    LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_VAL_RESPONSE);
                    LoadController.decreaseTransactionInterfaceLoad(_transferID, p_senderVO.getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);

                    listValueVO = (ListValueVO) altList.get(0);

                    setInterfaceDetails(p_senderVO, listValueVO, false, null);

                    checkTransactionLoad(p_senderVO, _interfaceID);

                    requestStr = getSenderValidateStr(_interfaceID, listValueVO.getLabel());
                    commonClient = new CommonClient();

                    LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_VAL);

                    TransactionLog.log(_transferID, _requestIDStr, p_senderVO.getMsisdn(), p_senderVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ,
                        PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                    if (_log.isDebugEnabled()) {
                        _log.debug("performSenderAlternateRouting", "Sending Request For MSISDN=" + p_senderVO.getMsisdn() + " on ALternate Routing 1 to =" + _interfaceID);
                    }

                    senderValResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);

                    TransactionLog.log(_transferID, _requestIDStr, p_senderVO.getMsisdn(), p_senderVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES,
                        PretupsI.TXN_LOG_TXNSTAGE_INVAL, senderValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                    try {
                        senderValidateResponse(p_senderVO, senderValResponse, 1, altList.size());
                        if (PretupsErrorCodesI.TXN_STATUS_SUCCESS.equals(_txnStatus)) {
                            LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.SENDER_VAL_RESPONSE);

                            if (PretupsI.INTERFACE_CATEGORY_PRE.equals(p_interfaceCatgeory)) {
                                updateSubscriberRoutingDetails(p_senderVO.getNetworkCode(), _interfaceID, _senderExternalID, p_senderVO.getMsisdn(), p_interfaceCatgeory,
                                    p_senderVO.getUserID(), _requestVO.getCreatedOn());
                            }
                        }
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(be.getMessageKey())) {
                            if (_log.isDebugEnabled()) {
                                _log.debug(
                                    "performSenderAlternateRouting",
                                    "Got Status=" + InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND + " After validation Request For MSISDN=" + p_senderVO.getMsisdn() + " Performing Alternate Routing to 2");
                            }

                            LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_VAL_RESPONSE);
                            LoadController.decreaseTransactionInterfaceLoad(_transferID, p_senderVO.getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);

                            listValueVO = (ListValueVO) altList.get(1);

                            setInterfaceDetails(p_senderVO, listValueVO, false, null);

                            checkTransactionLoad(p_senderVO, _interfaceID);

                            requestStr = getSenderValidateStr(_interfaceID, listValueVO.getLabel());

                            LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_VAL);

                            TransactionLog.log(_transferID, _requestIDStr, p_senderVO.getMsisdn(), p_senderVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ,
                                PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                            if (_log.isDebugEnabled()) {
                                _log.debug("performSenderAlternateRouting",
                                    "Sending Request For MSISDN=" + p_senderVO.getMsisdn() + " on ALternate Routing 2 to =" + _interfaceID);
                            }

                            senderValResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);

                            TransactionLog.log(_transferID, _requestIDStr, p_senderVO.getMsisdn(), p_senderVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES,
                                PretupsI.TXN_LOG_TXNSTAGE_INVAL, senderValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                            try {
                                senderValidateResponse(p_senderVO, senderValResponse, 1, altList.size());
                                if (PretupsErrorCodesI.TXN_STATUS_SUCCESS.equals(_txnStatus)) {
                                    LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.SENDER_VAL_RESPONSE);
                                    if (PretupsI.INTERFACE_CATEGORY_PRE.equals(p_interfaceCatgeory)) {
                                        updateSubscriberRoutingDetails(p_senderVO.getNetworkCode(), _interfaceID, _senderExternalID, p_senderVO.getMsisdn(),
                                            p_interfaceCatgeory, p_senderVO.getUserID(), _requestVO.getCreatedOn());
                                    }
                                }
                            } catch (BTSLBaseException bex) {
                                throw bex;
                            } catch (Exception e) {
                            	throw new BTSLBaseException(this, METHOD_NAME, "Exception in performing the sender alternate intreface routing controls");
                            }
                        } else {
                            throw be;
                        }
                    } catch (Exception e) {
                    	throw new BTSLBaseException(this, METHOD_NAME, "Exception in performing the sender alternate intreface routing controls case 2");
                    }
                    break;
                }
                }

            } else {
                return;
            }
        } catch (BTSLBaseException be) {
            LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_VAL_RESPONSE);
            throw be;
        } catch (Exception e) {
            LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_VAL_RESPONSE);
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PrepaidController[performSenderAlternateRouting]",
                _transferID, p_senderVO.getMsisdn(), p_senderVO.getNetworkCode(), "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "performSenderAlternateRouting", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
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
    public void senderValidateResponse(SenderVO p_senderVO, String str, int p_attempt, int p_altSize) throws BTSLBaseException {
        final String METHOD_NAME = "senderValidateResponse";
        final HashMap map = BTSLUtil.getStringToHash(str, "&", "=");
        final String status = (String) map.get("TRANSACTION_STATUS");
        final String interfaceID = (String) map.get("INTERFACE_ID");

        // Start: Update the Interface table for the interface ID based on
        // Handler status and update the Cache
        final String interfaceStatusType = (String) map.get("INT_SET_STATUS");
        if (!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME
            .equals(interfaceStatusType))) {
            new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES, interfaceID, interfaceStatusType, PretupsErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG,
                PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
            // :End
        }

        if (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) && p_attempt == 1 && p_attempt < p_altSize) {
            throw new BTSLBaseException(this, "senderValidateResponse", InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
        }
        if (BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS)) {
            throw new BTSLBaseException(this, "processValidationRequest", PretupsErrorCodesI.INTERFACE_ERROR_RESPONSE);
        }
        _txnStatus = PretupsErrorCodesI.TXN_STATUS_SUCCESS;
        p_senderVO.setServiceClassCode((String) map.get("SERVICE_CLASS"));
        p_senderVO.setAccountStatus((String) map.get("ACCOUNT_STATUS"));
        try {
            p_senderVO.setCurrentBalance(Long.parseLong((String) map.get("INTERFACE_PREV_BALANCE")));
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        ;
    }

    /**
     * This method sets the Interface Details based on the VOs values.
     * If p_useInterfacePrefixVO is True then use
     * p_MSISDNPrefixInterfaceMappingVO else use p_listValueVO to populate
     * values
     * 
     * @param p_senderVO
     * @param p_listValueVO
     * @param p_useInterfacePrefixVO
     * @param p_MSISDNPrefixInterfaceMappingVO
     * @throws BTSLBaseException
     */
    private void setInterfaceDetails(SenderVO p_senderVO, ListValueVO p_listValueVO, boolean p_useInterfacePrefixVO, MSISDNPrefixInterfaceMappingVO p_MSISDNPrefixInterfaceMappingVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug(
                "setInterfaceDetails",
                _requestIDStr,
                " Entered p_listValueVO=" + p_listValueVO + " p_useInterfacePrefixVO=" + p_useInterfacePrefixVO + " p_MSISDNPrefixInterfaceMappingVO=" + p_MSISDNPrefixInterfaceMappingVO);
        }
        final String METHOD_NAME = "setInterfaceDetails";
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
                if (BTSLUtil.isNullString(p_senderVO.getLanguage()) || BTSLUtil.isNullString(p_senderVO.getCountry())) {
                    locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                } else {
                    locale = new Locale(p_senderVO.getLanguage(), p_senderVO.getCountry());
                }
                if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(locale)).getMessage())) {
                    _requestVO.setSenderReturnMessage(message1);
                } else {
                    _requestVO.setSenderReturnMessage(message2);
                }
                throw new BTSLBaseException(this, "setInterfaceDetails", PretupsErrorCodesI.INTERFACE_NOT_ACTIVE);
            }
        } catch (BTSLBaseException be) {
            _log.error("setInterfaceDetails", "Getting Base Exception =" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AccountInformationController[setInterfaceDetails]",
                _requestIDStr, p_senderVO.getMsisdn(), p_senderVO.getNetworkCode(), "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "setInterfaceDetails", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }
    }

    /**
     * Method that will update the Subscriber Routing Details If interface is
     * PRE
     * 
     * @param p_networkCode
     * @param p_interfaceID
     * @param p_externalID
     * @param p_msisdn
     * @param p_interfaceCategory
     * @param p_userID
     * @param p_currentDate
     * @throws BTSLBaseException
     */
    private void updateSubscriberRoutingDetails(String p_networkCode, String p_interfaceID, String p_externalID, String p_msisdn, String p_interfaceCategory, String p_userID, Date p_currentDate) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug(
                "updateSubscriberRoutingDetails",
                _requestIDStr,
                " Entered p_networkCode=" + p_networkCode + " p_interfaceID=" + p_interfaceID + " p_externalID=" + p_externalID + " p_msisdn=" + p_msisdn + " p_interfaceCategory=" + p_interfaceCategory + " p_userID=" + p_userID + " p_currentDate=" + p_currentDate);
        }
        final String METHOD_NAME = "updateSubscriberRoutingDetails";
        try {
            // Update in DB for routing interface
            if (_senderInterfaceInfoInDBFound) {
                PretupsBL.updateSubscriberInterfaceRouting(p_interfaceID, p_externalID, p_msisdn, p_interfaceCategory, p_userID, p_currentDate);
            } else {
                final SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache
                    .getRoutingControlDetails(p_networkCode + "_" + PretupsI.SERVICE_TYPE_ACCOUNTINFO + "_" + p_interfaceCategory);
                if (!_senderInterfaceInfoInDBFound && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
                    PretupsBL.insertSubscriberInterfaceRouting(p_interfaceID, p_externalID, p_msisdn, p_interfaceCategory, p_userID, p_currentDate);
                    _senderInterfaceInfoInDBFound = true;
                }
            }
        } catch (BTSLBaseException be) {
            _log.error("updateSubscriberRoutingDetails", "Getting Base Exception =" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "AccountInformationController[updateSubscriberRoutingDetails]", _requestIDStr, p_msisdn, p_networkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updateSubscriberRoutingDetails", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }
    }

    /**
     * Method to get the counter that will be appended in the Transfer ID
     * formation.
     * It will be initialized after every 1000 hits
     * 
     * @return
     */
    public synchronized static long getCounter() {
        if (_counter >= 1000) {
            _counter = 0;
        }
        return _counter++;
    }

}
