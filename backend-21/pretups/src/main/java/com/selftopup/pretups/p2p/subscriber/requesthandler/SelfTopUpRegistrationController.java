package com.selftopup.pretups.p2p.subscriber.requesthandler;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.common.CommonClient;
import com.selftopup.common.ListValueVO;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.loadcontroller.LoadController;
import com.selftopup.loadcontroller.LoadControllerI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.gateway.businesslogic.PushMessage;
import com.selftopup.pretups.inter.module.InterfaceErrorCodesI;
import com.selftopup.pretups.inter.util.InterfaceCloserI;
import com.selftopup.pretups.logging.TransactionLog;
import com.selftopup.pretups.master.businesslogic.LocaleMasterCache;
import com.selftopup.pretups.master.businesslogic.LookupsCache;
import com.selftopup.pretups.master.businesslogic.LookupsVO;
import com.selftopup.pretups.master.businesslogic.ServiceClassInfoByCodeCache;
import com.selftopup.pretups.master.businesslogic.ServiceClassVO;
import com.selftopup.pretups.network.businesslogic.MSISDNPrefixInterfaceMappingCache;
import com.selftopup.pretups.network.businesslogic.MSISDNPrefixInterfaceMappingVO;
import com.selftopup.pretups.network.businesslogic.NetworkInterfaceModuleCache;
import com.selftopup.pretups.network.businesslogic.NetworkInterfaceModuleVO;
import com.selftopup.pretups.network.businesslogic.NetworkPrefixCache;
import com.selftopup.pretups.network.businesslogic.NetworkPrefixVO;
import com.selftopup.pretups.p2p.subscriber.businesslogic.RegistrationControlCache;
import com.selftopup.pretups.p2p.subscriber.businesslogic.RegistrationControlVO;
import com.selftopup.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.selftopup.pretups.p2p.subscriber.businesslogic.SubscriberDAO;
import com.selftopup.pretups.preference.businesslogic.SystemPreferences;
import com.selftopup.pretups.processes.ResumeSuspendProcess;
import com.selftopup.pretups.receiver.RequestVO;
import com.selftopup.pretups.routing.master.businesslogic.InterfaceRoutingControlCache;
import com.selftopup.pretups.routing.master.businesslogic.SubscriberRoutingControlCache;
import com.selftopup.pretups.routing.master.businesslogic.SubscriberRoutingControlVO;
import com.selftopup.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.selftopup.pretups.subscriber.businesslogic.SenderVO;
import com.selftopup.pretups.util.PretupsBL;
import com.selftopup.pretups.whitelist.businesslogic.WhiteListVO;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.OracleUtil;

/**
 * @author sonali.garg
 * 
 */
public class SelfTopUpRegistrationController implements ServiceKeywordControllerI {

    private static Log _log = LogFactory.getLog(SelfTopUpRegistrationController.class.getName());
    private String _intModCommunicationTypeS;
    private String _intModIPS;
    private int _intModPortS;
    private String _intModClassNameS;
    private String _msisdn;
    private String _networkCode;
    private String _requestID = null;
    private String _transferID = null;
    private boolean _isRequestRefuse = false;
    private String _interfaceID = null;
    private boolean _senderInterfaceInfoInDBFound = false;
    private String _senderExternalID = null;
    private RequestVO _requestVO = null;
    private String _txnStatus = null;
    private String _requestIDStr = null;
    private static long _counter = 0L;
    private long _txnIDSuffix = 0L;
    private Locale _senderLocale = null;
    private boolean _decreaseCountersReqd = false;
    private String _interfaceStatusType = null;
    private boolean _registerationAllowed = false;
    private String _loginId = null;
    private String _imei = null;

    /**
	 * 
	 */
    public void process(RequestVO p_requestVO) {
        _requestID = p_requestVO.getRequestIDStr();
        _requestVO = p_requestVO;

        if (_log.isDebugEnabled())
            _log.debug("process", _requestID, "Entered p_requestVO : " + p_requestVO);

        SenderVO senderVO = (SenderVO) p_requestVO.getSenderVO();
        // if user already register in our system as prepaid and now he want to
        // register as post paid.
        // then his registeration request will be refused. and message will be
        // sent that he will first
        // deactivate his previous service then only he can reregister himself
        if (senderVO != null && !senderVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_NEW)) {
            // Try catch is added by ankit z on date 2/8/06 to handel the
            // exception thworn by lookup cache
            // String arr[] =null;
            Connection con = null;
            try {
                con = OracleUtil.getConnection();
                _registerationAllowed = SubscriberBL.validateSusbscriberExpiry(con, senderVO.getMsisdn());
                if (_registerationAllowed) {
                    int i = new SubscriberDAO().deleteSubscriber(con, senderVO);
                    con.commit();

                }

            } catch (Exception e) {
                p_requestVO.setSuccessTxn(false);
                try {
                    if (con != null) {
                        con.rollback();
                    }
                } catch (Exception ee) {
                }
                _log.error("process", _requestID, "BTSLBaseException " + e.getMessage());
                e.printStackTrace();
                p_requestVO.setMessageCode(SelfTopUpErrorCodesI.P2P_REGISTERATION_ERROR);
                return;
            } finally {
                try {
                    if (con != null)
                        con.close();
                } catch (Exception e) {
                }
                if (_log.isDebugEnabled())
                    _log.debug("process", _requestID, "Exited");
            }
            /*
             * if(p_requestVO.getRequestMessageArray().length == 2)
             * {
             * if(p_requestVO.getRequestMessageArray()[1].equals(senderVO.
             * getSubscriberType()))
             * {
             * try
             * {
             * arr=new String[]{
             * senderVO.getMsisdn(),((LookupsVO)LookupsCache.getObject
             * (PretupsI.SUBSRICBER_TYPE
             * ,senderVO.getSubscriberType())).getLookupName()};
             * }
             * catch(Exception e){}
             * p_requestVO.setMessageArguments(arr);
             * p_requestVO.setMessageCode(PretupsErrorCodesI.
             * P2P_REGISTERATION_DUPLICATE);
             * return;
             * }
             * }
             */

        } else if (senderVO != null && senderVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_NEW)) {
            Connection con = null;
            try {
                con = OracleUtil.getConnection();
                // int i=new SubscriberDAO().deleteSubscriber(con, senderVO);
                con.commit();
                p_requestVO.setUnmarkSenderRequired(false);
            } catch (Exception e) {
                p_requestVO.setSuccessTxn(false);
                try {
                    if (con != null) {
                        con.rollback();
                    }
                } catch (Exception ee) {
                }
                _log.error("process", _requestID, "BTSLBaseException " + e.getMessage());
                e.printStackTrace();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RegisterationController[process]", "", "", "", "Exception:" + e.getMessage());
                p_requestVO.setMessageCode(SelfTopUpErrorCodesI.P2P_REGISTERATION_ERROR);
                return;
            } finally {
                try {
                    if (con != null)
                        con.close();
                } catch (Exception e) {
                }
                if (_log.isDebugEnabled())
                    _log.debug("process", _requestID, "Exited");
            }
        }

        senderVO = prepareSenderVO(p_requestVO);

        // to check whether PIN is required in the system after user
        // registeration
        // if required the genrate the PIN to user
        // get the pin for user
        if (SystemPreferences.PIN_REQUIRED)
            senderVO.setPin(PretupsBL.genratePin());
        else
            // senderVO.setPin(PretupsI.DEFAULT_P2P_PIN);
            senderVO.setPin((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN));

        _requestIDStr = p_requestVO.getRequestIDStr();

        Connection con = null;
        try {
            boolean registerP2PUser = false;
            registerP2PUser = isUserRegistrationApplicable(p_requestVO, senderVO, false);
            if (registerP2PUser) {
                if (senderVO.getSubscriberType().equals(PretupsI.SERIES_TYPE_PREPAID)) {
                    senderVO.setActivatedOn(new Date());
                    senderVO.setStatus(PretupsI.USER_STATUS_ACTIVE);
                } else if (SystemPreferences.POSTPAID_REGISTER_AS_ACTIVATED_STATUS) {
                    senderVO.setActivatedOn(new Date());
                    senderVO.setStatus(PretupsI.USER_STATUS_ACTIVE);
                } else
                    senderVO.setStatus(PretupsI.USER_STATUS_NEW);

                con = OracleUtil.getConnection();
                if (!BTSLUtil.isNullString(senderVO.getServiceClassCode())) {
                    // ServiceClassDAO serviceClassDAO=new ServiceClassDAO();
                    // ServiceClassVO
                    // serviceClassVO=serviceClassDAO.loadServiceClassInfoByCode(con,senderVO.getServiceClassCode(),_interfaceID);
                    ServiceClassVO serviceClassVO = ServiceClassInfoByCodeCache.getServiceClassByCode(senderVO.getServiceClassCode(), _interfaceID);
                    if (serviceClassVO == null) {
                        // serviceClassVO=serviceClassDAO.loadServiceClassInfoByCode(con,PretupsI.ALL,_interfaceID);
                        serviceClassVO = ServiceClassInfoByCodeCache.getServiceClassByCode(PretupsI.ALL, _interfaceID);
                        if (serviceClassVO != null)
                            senderVO.setServiceClassID(serviceClassVO.getServiceClassId());
                    } else
                        senderVO.setServiceClassID(serviceClassVO.getServiceClassId());

                    if (serviceClassVO == null) {
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "RegsiterationController[regsiterNewUser]", "", senderVO.getMsisdn(), "", "No Service Class defined for " + senderVO.getServiceClassCode() + " and Interface ID " + _interfaceID);
                        throw new BTSLBaseException("RegsiterationController", "process", SelfTopUpErrorCodesI.REG_KEY_ERROR_INTFCE_SRVCECLSS_NOTFOUND);
                    } else if (PretupsI.SUSPEND.equals(serviceClassVO.getStatus())) {
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "RegsiterationController[regsiterNewUser]", _transferID, senderVO.getMsisdn(), "", "Service Class " + serviceClassVO.getServiceClassId() + " is suspended");
                        throw new BTSLBaseException("RegsiterationController", "process", SelfTopUpErrorCodesI.REG_KEY_ERROR_INTFCE_SRVCECLSS_SUSPEND);
                    } else {
                        String allowedAccountStatus = null;
                        if (PretupsI.YES.equals(serviceClassVO.getP2pSenderSuspend()))
                            throw new BTSLBaseException("RegsiterationController", "process", SelfTopUpErrorCodesI.REG_KEY_ERROR_INTFCE_SRVCECLSS_SENDER_SUSPEND);
                        allowedAccountStatus = serviceClassVO.getP2pSenderAllowedStatus();

                        if (!BTSLUtil.isNullString(allowedAccountStatus) && !PretupsI.ALL.equals(allowedAccountStatus)) {
                            String[] allowedStatus = allowedAccountStatus.split(",");
                            if (!Arrays.asList(allowedStatus).contains(senderVO.getAccountStatus())) {
                                if (_log.isDebugEnabled())
                                    _log.debug("process", _transferID, "Account Status =" + senderVO.getAccountStatus() + " is not allowed in the allowed List for interface ID=" + _interfaceID);
                                throw new BTSLBaseException("RegsiterationController", "process", SelfTopUpErrorCodesI.REG_KEY_ERROR_INTFCE_ACCOUNTSTATUS_NOTALLOWED);
                            }
                        }
                    }
                }

                int status = SubscriberBL.regsiterP2PSelfTopUpsubscriber(con, senderVO);
                p_requestVO.setEncryptionKey(senderVO.getEncryptionKey());
                if (status > 0) {
                    con.commit();
                    if (senderVO.getSubscriberType().equals(PretupsI.SERIES_TYPE_PREPAID)) {
                        if (SystemPreferences.PIN_REQUIRED) {
                            String[] args = null;
                            String message1 = null;
                            if (senderVO.getImei().length() == PretupsI.IMEI_LENGTH && !PretupsI.DEFAULT_P2P_WEB_IMEI.equals(senderVO.getImei())) {
                                args = new String[] { senderVO.getMsisdn(), ((LookupsVO) LookupsCache.getObject(PretupsI.SUBSRICBER_TYPE, senderVO.getSubscriberType())).getLookupName(), senderVO.getPin(), BTSLUtil.decryptText(senderVO.getPassword()), senderVO.getLogin() };
                                p_requestVO.setMessageArguments(args);
                                p_requestVO.setMessageCode(SelfTopUpErrorCodesI.P2P_REGISTERATION_APP_PREPAID_SUCCESS);
                                message1 = BTSLUtil.getMessage(p_requestVO.getLocale(), SelfTopUpErrorCodesI.P2P_REGISTERATION_APP_PREPAID_SUCCESS, p_requestVO.getMessageArguments());
                            } else {
                                args = new String[] { senderVO.getMsisdn(), senderVO.getLogin(), BTSLUtil.decryptText(senderVO.getPassword()), senderVO.getPin() };
                                p_requestVO.setMessageArguments(args);
                                p_requestVO.setMessageCode(SelfTopUpErrorCodesI.STU_REG_SUCCESS_WITH_PIN_WEB);
                                message1 = BTSLUtil.getMessage(p_requestVO.getLocale(), SelfTopUpErrorCodesI.STU_REG_SUCCESS_WITH_PIN_WEB, p_requestVO.getMessageArguments());
                            }
                            PushMessage pushMessage1 = new PushMessage(p_requestVO.getFilteredMSISDN(), message1, p_requestVO.getRequestIDStr(), p_requestVO.getRequestGatewayCode(), p_requestVO.getLocale());
                            pushMessage1.push();

                            return;
                        } else {
                            String[] args = null;
                            String message1 = null;
                            if (senderVO.getImei().length() == PretupsI.IMEI_LENGTH && !PretupsI.DEFAULT_P2P_WEB_IMEI.equals(senderVO.getImei())) {
                                args = new String[] { senderVO.getMsisdn(), ((LookupsVO) LookupsCache.getObject(PretupsI.SUBSRICBER_TYPE, senderVO.getSubscriberType())).getLookupName(), senderVO.getPin(), BTSLUtil.decryptText(senderVO.getPassword()), senderVO.getLogin() };
                                p_requestVO.setMessageArguments(args);
                                p_requestVO.setMessageCode(SelfTopUpErrorCodesI.P2P_REGISTERATION_APP_PREPAID_SUCCESS);
                                message1 = BTSLUtil.getMessage(p_requestVO.getLocale(), SelfTopUpErrorCodesI.P2P_REGISTERATION_APP_PREPAID_SUCCESS, p_requestVO.getMessageArguments());
                            } else {
                                p_requestVO.setMessageCode(SelfTopUpErrorCodesI.STU_REG_SUCCESS_WITH_PIN_WEB);//
                                // String args[] =
                                // {senderVO.getMsisdn(),((LookupsVO)LookupsCache.getObject(PretupsI.SUBSRICBER_TYPE,senderVO.getSubscriberType())).getLookupName(),senderVO.getPassword()};
                                args = new String[] { senderVO.getMsisdn(), senderVO.getLogin(), BTSLUtil.decryptText(senderVO.getPassword()), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN) };
                                p_requestVO.setMessageArguments(args);
                                message1 = BTSLUtil.getMessage(p_requestVO.getLocale(), SelfTopUpErrorCodesI.STU_REG_SUCCESS_WITH_PIN_WEB, p_requestVO.getMessageArguments());
                            }
                            PushMessage pushMessage1 = new PushMessage(p_requestVO.getFilteredMSISDN(), message1, p_requestVO.getRequestIDStr(), p_requestVO.getRequestGatewayCode(), p_requestVO.getLocale());
                            pushMessage1.push();
                            return;

                        }
                    } else {
                        String args[] = { senderVO.getMsisdn(), ((LookupsVO) LookupsCache.getObject(PretupsI.SUBSRICBER_TYPE, senderVO.getSubscriberType())).getLookupName(), senderVO.getPin() };
                        p_requestVO.setMessageCode(SelfTopUpErrorCodesI.P2P_REGISTERATION_POSTPAID_SUCCESS);
                        p_requestVO.setMessageArguments(args);
                        String message1 = BTSLUtil.getMessage(p_requestVO.getLocale(), SelfTopUpErrorCodesI.P2P_REGISTERATION_POSTPAID_SUCCESS, p_requestVO.getMessageArguments());
                        PushMessage pushMessage1 = new PushMessage(p_requestVO.getFilteredMSISDN(), message1, p_requestVO.getRequestIDStr(), p_requestVO.getRequestGatewayCode(), p_requestVO.getLocale());
                        pushMessage1.push();
                        return;
                    }
                } else {
                    // registeraion failed
                    con.rollback();
                    p_requestVO.setMessageCode(SelfTopUpErrorCodesI.P2P_REGISTERATION_ERROR);
                    return;
                }
            } else {
                // registeraion failed
                try {
                    if (con != null) {
                        con.rollback();
                    }
                } catch (Exception e) {
                }
                p_requestVO.setMessageCode(SelfTopUpErrorCodesI.P2P_REGISTERATION_ERROR);
                return;
            }

        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
            }
            _log.error("process", _requestID, "BTSLBaseException " + be.getMessage());
            be.printStackTrace();
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"RegisterationController[process]","","","","BTSL Exception:"+be.getMessage());
            p_requestVO.setMessageCode(SelfTopUpErrorCodesI.P2P_REGISTERATION_ERROR);
            return;
        } catch (Exception e) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ee) {
            }
            _log.error("process", _requestID, "BTSLBaseException " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RegisterationController[process]", "", "", "", "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(SelfTopUpErrorCodesI.P2P_REGISTERATION_ERROR);
            return;
        } finally {
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("process", _requestID, "Exited");
        }
    }

    /**
     * Method that will be called from various controllers to handle auto
     * registration
     * 
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public void regsiterNewUser(RequestVO p_requestVO) throws BTSLBaseException {
        _requestID = p_requestVO.getRequestIDStr();
        _requestIDStr = p_requestVO.getRequestIDStr();
        _requestVO = p_requestVO;
        if (_log.isDebugEnabled())
            _log.debug("regsiterNewUser", _requestID, "Entered");
        SenderVO senderVO = prepareSenderVO(p_requestVO);
        // senderVO.setPin(PretupsI.DEFAULT_P2P_PIN);
        senderVO.setPin((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN));
        senderVO.setStatus(PretupsI.USER_STATUS_NEW);
        Connection con = null;
        ServiceClassVO serviceClassVO = null;
        try {
            boolean registerP2PUser = false;
            registerP2PUser = isUserRegistrationApplicable(p_requestVO, senderVO, true);
            if (registerP2PUser) {
                senderVO.setActivatedOn(new Date());
                con = OracleUtil.getConnection();
                if (!BTSLUtil.isNullString(senderVO.getServiceClassCode())) {
                    // ServiceClassDAO serviceClassDAO=new ServiceClassDAO();
                    // serviceClassVO=serviceClassDAO.loadServiceClassInfoByCode(con,senderVO.getServiceClassCode(),_interfaceID);
                    serviceClassVO = ServiceClassInfoByCodeCache.getServiceClassByCode(senderVO.getServiceClassCode(), _interfaceID);
                    if (serviceClassVO == null) {
                        // serviceClassVO=serviceClassDAO.loadServiceClassInfoByCode(con,PretupsI.ALL,_interfaceID);
                        serviceClassVO = ServiceClassInfoByCodeCache.getServiceClassByCode(PretupsI.ALL, _interfaceID);
                        if (serviceClassVO != null)
                            senderVO.setServiceClassID(serviceClassVO.getServiceClassId());
                    } else
                        senderVO.setServiceClassID(serviceClassVO.getServiceClassId());
                }
                int status = SubscriberBL.regsiterP2PSelfTopUpsubscriber(con, senderVO);

                if (status > 0) {
                    con.commit();
                    senderVO.setPin(BTSLUtil.encryptText(senderVO.getPin()));
                    p_requestVO.setSenderVO(senderVO);
                    if (!BTSLUtil.isNullString(senderVO.getServiceClassCode())) {
                        if (serviceClassVO == null) {
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "RegsiterationController[regsiterNewUser]", "", senderVO.getMsisdn(), "", "No Service Class defined for " + senderVO.getServiceClassCode() + " and Interface ID " + _interfaceID);
                            throw new BTSLBaseException("RegsiterationController", "regsiterNewUser", SelfTopUpErrorCodesI.REG_ERROR_INTFCE_SRVCECLSS_NOTFOUND);
                        } else if (PretupsI.SUSPEND.equals(serviceClassVO.getStatus())) {
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "RegsiterationController[regsiterNewUser]", _transferID, senderVO.getMsisdn(), "", "Service Class " + serviceClassVO.getServiceClassId() + " is suspended");
                            throw new BTSLBaseException("RegsiterationController", "regsiterNewUser", SelfTopUpErrorCodesI.REG_ERROR_INTFCE_SRVCECLSS_SUSPEND);
                        } else {
                            String allowedAccountStatus = null;
                            if (PretupsI.YES.equals(serviceClassVO.getP2pSenderSuspend()))
                                throw new BTSLBaseException("RegsiterationController", "regsiterNewUser", SelfTopUpErrorCodesI.REG_ERROR_INTFCE_SRVCECLSS_SENDER_SUSPEND);
                            allowedAccountStatus = serviceClassVO.getP2pSenderAllowedStatus();

                            if (!BTSLUtil.isNullString(allowedAccountStatus) && !PretupsI.ALL.equals(allowedAccountStatus)) {
                                String[] allowedStatus = allowedAccountStatus.split(",");
                                if (!Arrays.asList(allowedStatus).contains(senderVO.getAccountStatus())) {
                                    if (_log.isDebugEnabled())
                                        _log.debug("regsiterNewUser", _transferID, "Account Status =" + senderVO.getAccountStatus() + " is not allowed in the allowed List for interface ID=" + _interfaceID);
                                    throw new BTSLBaseException("RegsiterationController", "regsiterNewUser", SelfTopUpErrorCodesI.REG_ERROR_INTFCE_ACCOUNTSTATUS_NOTALLOWED);
                                }
                            }
                        }
                    }
                } else {
                    // registeraion failed
                    try {
                        if (con != null) {
                            con.rollback();
                        }
                    } catch (Exception e) {
                    }
                    p_requestVO.setMessageCode(SelfTopUpErrorCodesI.P2P_REGISTERATION_ERROR);
                    throw new BTSLBaseException(this, "regsiterNewUser", SelfTopUpErrorCodesI.P2P_REGISTERATION_ERROR);
                }
            } else {
                // registeraion failed
                try {
                    if (con != null) {
                        con.rollback();
                    }
                } catch (Exception e) {
                }
                p_requestVO.setMessageCode(SelfTopUpErrorCodesI.P2P_REGISTERATION_ERROR);
                throw new BTSLBaseException(this, "regsiterNewUser", SelfTopUpErrorCodesI.P2P_REGISTERATION_ERROR);
            }
        } catch (BTSLBaseException be) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
            }
            _log.error("regsiterNewUser", _requestID, "BTSLBaseException " + be.getMessage());
            be.printStackTrace();
            throw be;
        } catch (Exception e) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ee) {
            }
            _log.error("regsiterNewUser", _requestID, "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RegisterationController[regsiterNewUser]", "", "", "", "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(SelfTopUpErrorCodesI.P2P_REGISTERATION_ERROR);
            throw new BTSLBaseException(this, "regsiterNewUser", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } finally {
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("regsiterNewUser", _requestID, "Exited");
        }
    }

    /**
     * Common Method to prepare the Sender VO to be inserted in DB
     * 
     * @param p_requestVO
     * @return
     */
    public SenderVO prepareSenderVO(RequestVO p_requestVO) {
        if (_log.isDebugEnabled())
            _log.debug("prepareSenderVO", _requestID, "Entered");
        SenderVO senderVO = new SenderVO();
        senderVO.setMsisdnPrefix(PretupsBL.getMSISDNPrefix(p_requestVO.getFilteredMSISDN()));
        senderVO.setMsisdn(p_requestVO.getFilteredMSISDN());
        senderVO.setModule(PretupsI.P2P_MODULE);
        senderVO.setImei(p_requestVO.getImei());
        if (BTSLUtil.isNullString(p_requestVO.getUserLoginId()))
            senderVO.setLogin(p_requestVO.getFilteredMSISDN());
        else
            senderVO.setLogin(p_requestVO.getUserLoginId());
        senderVO.setEmailId(p_requestVO.getEmailId());
        // msisdn is unique for every subscriber. hence this will only be the
        // login ID.
        _msisdn = senderVO.getMsisdn();
        _loginId = _msisdn;
        _imei = senderVO.getImei();

        NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(senderVO.getMsisdnPrefix());
        senderVO.setPrefixID(networkPrefixVO.getPrefixID());
        senderVO.setNetworkCode(networkPrefixVO.getNetworkCode());
        _networkCode = senderVO.getNetworkCode();
        if (_log.isDebugEnabled())
            _log.debug("prepareSenderVO", _requestID, "Exiting with Sender Prefix ID as =" + networkPrefixVO.getPrefixID());
        return senderVO;

    }

    /**
     * Method to check whether Registration needs to be done and also sets other
     * info like Subsctiber Type etc
     * 
     * @param p_requestVO
     * @param p_senderVO
     * @param p_reqFromDiffController
     * @return
     * @throws BTSLBaseException
     */
    public boolean isUserRegistrationApplicable(RequestVO p_requestVO, SenderVO senderVO, boolean p_reqFromDiffController) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("isUserRegistrationApplicable", _requestID, "Entered with p_reqFromDiffController=" + p_reqFromDiffController);

        String messageSubscriberType = null;
        boolean registerP2PUser = false;
        String registrationType = null;
        String validationInterface = null;
        try {
            // Validate the message only if coming from User keyword STUREG
            // to check whether user sends PRE or POST with STUREG as
            // registeration
            // message request
            if (!p_reqFromDiffController) {
                if (p_requestVO.getRequestMessageArray() != null && p_requestVO.getRequestMessageArray().length > 5)
                    throw new BTSLBaseException(this, "isUserRegistrationApplicable", SelfTopUpErrorCodesI.REG_INVALID_MESG_FORMAT);
                else if (p_requestVO.getRequestMessageArray() != null && p_requestVO.getRequestMessageArray().length == 5) {
                    messageSubscriberType = p_requestVO.getRequestMessageArray()[4];
                }
            }

            if (_log.isDebugEnabled())
                _log.debug("isUserRegistrationApplicable", _requestID, "messageSubscriberType" + messageSubscriberType);

            // to check what user sends as registeration request (PRE or POST)
            // and in which way MSISDN PREFIX which user sends
            // is registered in our system means as PREPAID OR POSTPAID

            if (messageSubscriberType != null) {
                registrationType = messageSubscriberType.toUpperCase();
                // on the base of user registeration request type set his
                // subscriber type
                if (messageSubscriberType.equalsIgnoreCase(PretupsI.REGISTERATION_REQUEST_PRE)) {
                    senderVO.setSubscriberType(PretupsI.SERIES_TYPE_PREPAID);
                } else if (messageSubscriberType.equalsIgnoreCase(PretupsI.REGISTERATION_REQUEST_POST)) {
                    senderVO.setSubscriberType(PretupsI.SERIES_TYPE_POSTPAID);
                }
            } else {
                registrationType = PretupsI.ALL;
            }

            if (_log.isDebugEnabled())
                _log.debug("isUserRegistrationApplicable", _requestID, "registrationType: " + registrationType);

            // validate the p2p user on the basis of subscriber type
            // if not found then on the bases of system preferences
            if (p_reqFromDiffController)
                senderVO.setStatus(PretupsI.USER_STATUS_NEW);

            if (SystemPreferences.SKEY_REQUIRED)
                senderVO.setSkeyRequired("Y");
            else
                senderVO.setSkeyRequired("N");

            senderVO.setCountry((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
            senderVO.setLanguage((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE));
            /*
             * Get the Registration Control parameters
             * 1. Check if Validation is Required :
             * If Yes
             * a) Check Routing Control Parameters and send request to the
             * required interface
             * b) If Not Found on this interface , check for the alternate
             * interface
             * c) If alternate is defined
             * d) Check Routing Control Parameters and send request to the
             * required interface
             * e) If Not Found on this interface , Fail the request
             * f) If Found then register the user with this category
             * g) If alternate interface is not defined then check whether
             * registration needs to be done or not
             * h) If Required then register with default category
             * i) If Found then register the user with this category
             * 2. If Validation is not required then check whether registration
             * needs to be done or not
             * a) If Required then register with default category
             */
            RegistrationControlVO registrationControlVO = RegistrationControlCache.getRegistrationControlDetails(senderVO.getNetworkCode() + "_" + registrationType);
            NetworkPrefixVO networkPrefixVO = null;
            if (registrationControlVO != null) {
                if (_log.isDebugEnabled())
                    _log.debug("isUserRegistrationApplicable", _requestID, "Registration Control Info for =" + senderVO.getMsisdnPrefix() + " validation interface =" + registrationControlVO.isValidationReqdBool() + " On interface=" + registrationControlVO.getValidationInterface() + " Alternate Check=" + registrationControlVO.isAltInterfaceCheckBool() + " Alternate interface=" + registrationControlVO.getAlternateInterface() + " Registration to be done=" + registrationControlVO.isRegistrationToBedoneBool() + " On interface=" + registrationControlVO.getDefRegistrationType());

                if (registrationControlVO.isValidationReqdBool()) {
                    validationInterface = registrationControlVO.getValidationInterface();
                    senderVO.setSubscriberType(validationInterface);
                    // This has been done again has before this Prefix ID is
                    // loaded with PRE interface
                    // and if this interface is POST then we need to get the new
                    // Prefix ID
                    networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(senderVO.getMsisdnPrefix(), validationInterface);
                    if (networkPrefixVO != null) {
                        if (_log.isDebugEnabled())
                            _log.debug("isUserRegistrationApplicable", _requestID, "Network Prefix found For Prefix=" + senderVO.getMsisdnPrefix() + " and validation interface =" + validationInterface + " Setting Prefix ID as =" + networkPrefixVO.getPrefixID());

                        senderVO.setPrefixID(networkPrefixVO.getPrefixID());
                        senderVO.setNetworkCode(networkPrefixVO.getNetworkCode());
                        senderVO.setSubscriberType(networkPrefixVO.getSeriesType());

                        boolean isSuccess = false;
                        try {
                            isSuccess = checkRoutingControlAndSendReq(senderVO, PretupsI.SERVICE_TYPE_REGISTERATION, validationInterface);
                        } catch (BTSLBaseException be) {
                            if (registrationControlVO.isAltInterfaceCheckBool() || registrationControlVO.isRegistrationToBedoneBool())
                                isSuccess = false;
                            else
                                throw be;
                            // be.printStackTrace();
                            _log.error("isUserRegistrationApplicable", _requestID, "Msisdn not found on interface and validation interface =" + validationInterface);
                        }
                        if (_log.isDebugEnabled())
                            _log.debug("isUserRegistrationApplicable", _requestID, " Validation Successful on interface=" + validationInterface + " =" + isSuccess + " Alternate Check=" + registrationControlVO.isAltInterfaceCheckBool() + " Alternate interface=" + registrationControlVO.getAlternateInterface() + " Registration to be done=" + registrationControlVO.isRegistrationToBedoneBool() + " On interface=" + registrationControlVO.getDefRegistrationType());

                        if (!isSuccess) {
                            if (registrationControlVO.isAltInterfaceCheckBool()) {
                                _log.info("isUserRegistrationApplicable", _requestID, "Checking for Alternate interface=" + registrationControlVO.getAlternateInterface());

                                senderVO.setSubscriberType(registrationControlVO.getAlternateInterface());
                                networkPrefixVO = null;

                                // We need to get the new Prefix ID for
                                // alternate interface category
                                networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(senderVO.getMsisdnPrefix(), registrationControlVO.getAlternateInterface());
                                if (networkPrefixVO != null) {
                                    if (_log.isDebugEnabled())
                                        _log.debug("isUserRegistrationApplicable", _requestID, "Network Prefix found For Prefix=" + senderVO.getMsisdnPrefix() + " and validation interface =" + registrationControlVO.getAlternateInterface() + " Setting Prefix ID as =" + networkPrefixVO.getPrefixID());

                                    senderVO.setPrefixID(networkPrefixVO.getPrefixID());
                                    senderVO.setNetworkCode(networkPrefixVO.getNetworkCode());
                                    senderVO.setSubscriberType(networkPrefixVO.getSeriesType());

                                    isSuccess = checkRoutingControlAndSendReq(senderVO, PretupsI.SERVICE_TYPE_REGISTERATION, registrationControlVO.getAlternateInterface());
                                    if (_log.isDebugEnabled())
                                        _log.debug("isUserRegistrationApplicable", _requestID, " Validation Successful on interface=" + registrationControlVO.getAlternateInterface() + " =" + isSuccess);

                                    if (!isSuccess)
                                        throw new BTSLBaseException(this, "isUserRegistrationApplicable", SelfTopUpErrorCodesI.P2P_REGISTERATION_ERROR);
                                    senderVO.setSubscriberType(registrationControlVO.getAlternateInterface());
                                    registerP2PUser = true;
                                } else {
                                    _log.error(this, "Series =" + senderVO.getMsisdnPrefix() + " Not Defined for Series type=" + registrationControlVO.getAlternateInterface());
                                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "RegistrationController[isUserRegistrationApplicable]", "", "", "", "Series =" + senderVO.getMsisdnPrefix() + " Not Defined for Series type=" + registrationControlVO.getAlternateInterface());
                                    throw new BTSLBaseException("", "process", SelfTopUpErrorCodesI.ERROR_NOTFOUND_SERIES_TYPE, 0, new String[] { p_requestVO.getFilteredMSISDN() }, null);
                                }
                            } else if (registrationControlVO.isRegistrationToBedoneBool()) {
                                _log.info("isUserRegistrationApplicable", _requestID, " Validation Failed on interfaces But Registration to be done=" + registrationControlVO.isRegistrationToBedoneBool() + " On interface=" + registrationControlVO.getDefRegistrationType());
                                networkPrefixVO = null;
                                networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(senderVO.getMsisdnPrefix(), registrationControlVO.getDefRegistrationType());
                                if (networkPrefixVO != null) {
                                    if (_log.isDebugEnabled())
                                        _log.debug("isUserRegistrationApplicable", _requestID, "Network Prefix found For Prefix=" + senderVO.getMsisdnPrefix() + " and validation interface =" + registrationControlVO.getAlternateInterface() + " Setting Prefix ID as =" + networkPrefixVO.getPrefixID());

                                    senderVO.setPrefixID(networkPrefixVO.getPrefixID());
                                    senderVO.setNetworkCode(networkPrefixVO.getNetworkCode());
                                    senderVO.setSubscriberType(networkPrefixVO.getSeriesType());

                                    senderVO.setSubscriberType(registrationControlVO.getDefRegistrationType());
                                    registerP2PUser = true;
                                } else {
                                    _log.error(this, "Series =" + senderVO.getMsisdnPrefix() + " Not Defined for Series type=" + registrationControlVO.getDefRegistrationType());
                                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "RegistrationController[isUserRegistrationApplicable]", "", "", "", "Series =" + senderVO.getMsisdnPrefix() + " Not Defined for Series type=" + registrationControlVO.getDefRegistrationType());
                                    throw new BTSLBaseException("", "process", SelfTopUpErrorCodesI.ERROR_NOTFOUND_SERIES_TYPE, 0, new String[] { p_requestVO.getFilteredMSISDN() }, null);
                                }
                            }
                        } else {
                            senderVO.setSubscriberType(registrationControlVO.getValidationInterface());
                            registerP2PUser = true;
                        }
                    }
                    // Checks if Prefix ID for that interface is not found then
                    // check for alternate Interface
                    // Flag if defined then validate the request
                    else if (registrationControlVO.isAltInterfaceCheckBool()) {
                        _log.info("isUserRegistrationApplicable", _requestID, "Network Prefix Not found For Prefix=" + senderVO.getMsisdnPrefix() + " and validation interface =" + validationInterface + " Checking for Alternate interface=" + registrationControlVO.getAlternateInterface());

                        networkPrefixVO = null;
                        senderVO.setSubscriberType(registrationControlVO.getAlternateInterface());

                        // We need to get the new Prefix ID for alternate
                        // interface category
                        networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(senderVO.getMsisdnPrefix(), registrationControlVO.getAlternateInterface());
                        if (networkPrefixVO != null) {
                            if (_log.isDebugEnabled())
                                _log.debug("isUserRegistrationApplicable", _requestID, "Network Prefix found For Prefix=" + senderVO.getMsisdnPrefix() + " and validation interface =" + registrationControlVO.getAlternateInterface() + " Setting Prefix ID as =" + networkPrefixVO.getPrefixID());

                            senderVO.setPrefixID(networkPrefixVO.getPrefixID());
                            senderVO.setNetworkCode(networkPrefixVO.getNetworkCode());
                            senderVO.setSubscriberType(networkPrefixVO.getSeriesType());

                            boolean isSuccess = checkRoutingControlAndSendReq(senderVO, PretupsI.SERVICE_TYPE_REGISTERATION, registrationControlVO.getAlternateInterface());
                            if (_log.isDebugEnabled())
                                _log.debug("isUserRegistrationApplicable", _requestID, " Validation Successful on interface=" + registrationControlVO.getAlternateInterface() + " =" + isSuccess);

                            if (!isSuccess)
                                throw new BTSLBaseException(this, "isUserRegistrationApplicable", SelfTopUpErrorCodesI.P2P_REGISTERATION_ERROR);
                            registerP2PUser = true;
                        } else {
                            _log.error(this, "Series =" + senderVO.getMsisdnPrefix() + " Not Defined for Series type=" + registrationControlVO.getAlternateInterface());
                            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "RegistrationController[isUserRegistrationApplicable]", "", "", "", "Series =" + senderVO.getMsisdnPrefix() + " Not Defined for Series type=" + registrationControlVO.getAlternateInterface() + " But validation required on that interface");
                            throw new BTSLBaseException("", "process", SelfTopUpErrorCodesI.ERROR_NOTFOUND_SERIES_TYPE, 0, new String[] { p_requestVO.getFilteredMSISDN() }, null);
                        }

                    } else if (registrationControlVO.isRegistrationToBedoneBool()) {
                        _log.info("isUserRegistrationApplicable", _requestID, " Validation Failed on interfaces But Registration to be done=" + registrationControlVO.isRegistrationToBedoneBool() + " On interface=" + registrationControlVO.getDefRegistrationType());
                        networkPrefixVO = null;
                        networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(senderVO.getMsisdnPrefix(), registrationControlVO.getDefRegistrationType());
                        if (networkPrefixVO != null) {
                            if (_log.isDebugEnabled())
                                _log.debug("isUserRegistrationApplicable", _requestID, "Network Prefix found For Prefix=" + senderVO.getMsisdnPrefix() + " and validation interface =" + registrationControlVO.getDefRegistrationType() + " Setting Prefix ID as =" + networkPrefixVO.getPrefixID());

                            senderVO.setPrefixID(networkPrefixVO.getPrefixID());
                            senderVO.setNetworkCode(networkPrefixVO.getNetworkCode());
                            senderVO.setSubscriberType(networkPrefixVO.getSeriesType());

                            senderVO.setSubscriberType(registrationControlVO.getDefRegistrationType());
                            registerP2PUser = true;
                        } else {
                            _log.error(this, "Series =" + senderVO.getMsisdnPrefix() + " Not Defined for Series type=" + registrationControlVO.getDefRegistrationType());
                            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "RegistrationController[isUserRegistrationApplicable]", "", "", "", "Series =" + senderVO.getMsisdnPrefix() + " Not Defined for Series type=" + registrationControlVO.getDefRegistrationType() + " But validation required on that interface");
                            throw new BTSLBaseException("", "process", SelfTopUpErrorCodesI.ERROR_NOTFOUND_SERIES_TYPE, 0, new String[] { p_requestVO.getFilteredMSISDN() }, null);
                        }
                    }
                } else if (registrationControlVO.isRegistrationToBedoneBool()) {
                    _log.info("isUserRegistrationApplicable", _requestID, " Validation Not Required on interfaces But Registration to be done=" + registrationControlVO.isRegistrationToBedoneBool() + " On interface=" + registrationControlVO.getDefRegistrationType());
                    networkPrefixVO = null;
                    networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(senderVO.getMsisdnPrefix(), registrationControlVO.getDefRegistrationType());
                    if (networkPrefixVO != null) {
                        if (_log.isDebugEnabled())
                            _log.debug("isUserRegistrationApplicable", _requestID, "Network Prefix found For Prefix=" + senderVO.getMsisdnPrefix() + " and validation interface =" + registrationControlVO.getDefRegistrationType() + " Setting Prefix ID as =" + networkPrefixVO.getPrefixID());

                        senderVO.setPrefixID(networkPrefixVO.getPrefixID());
                        senderVO.setNetworkCode(networkPrefixVO.getNetworkCode());
                        senderVO.setSubscriberType(networkPrefixVO.getSeriesType());

                        senderVO.setSubscriberType(registrationControlVO.getDefRegistrationType());
                        registerP2PUser = true;
                    } else {
                        _log.error(this, "Series =" + senderVO.getMsisdnPrefix() + " Not Defined for Series type=" + registrationControlVO.getDefRegistrationType());
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "RegistrationController[isUserRegistrationApplicable]", "", "", "", "Series =" + senderVO.getMsisdnPrefix() + " Not Defined for Series type=" + registrationControlVO.getDefRegistrationType() + " But validation required on that interface");
                        throw new BTSLBaseException("", "process", SelfTopUpErrorCodesI.ERROR_NOTFOUND_SERIES_TYPE, 0, new String[] { p_requestVO.getFilteredMSISDN() }, null);
                    }
                } else
                    throw new BTSLBaseException(this, "isUserRegistrationApplicable", SelfTopUpErrorCodesI.P2P_REGISTERATION_ERROR);
            } else {
                _log.error(this, "Registration routing controls are not defined in system, please check");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RegisterationController[isUserRegistrationApplicable]", "", "", "", "Registration routing controls are not defined in system, please check");
                throw new BTSLBaseException(this, "isUserRegistrationApplicable", SelfTopUpErrorCodesI.P2P_REGISTERATION_ERROR);
            }
        } catch (BTSLBaseException be) {
            _log.error("isUserRegistrationApplicable", "BTSLBaseException " + be.getMessage());
            be.printStackTrace();
            throw be;
        } catch (Exception e) {
            _log.error("isUserRegistrationApplicable", "BTSLBaseException " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RegisterationController[isUserRegistrationApplicable]", "", "", "", "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(SelfTopUpErrorCodesI.P2P_REGISTERATION_ERROR);
            throw new BTSLBaseException(this, "isUserRegistrationApplicable", SelfTopUpErrorCodesI.P2P_REGISTERATION_ERROR);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("isUserRegistrationApplicable", " Exited ");
        }
        return registerP2PUser;
    }

    /**
     * Method to check the routing controls and validate the same on that
     * interface
     * 
     * @param p_senderVO
     * @param p_serviceType
     * @param p_interfaceCatgeory
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean checkRoutingControlAndSendReq(SenderVO p_senderVO, String p_serviceType, String p_interfaceCatgeory) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("checkRoutingControlAndSendReq", "Entered with p_serviceType" + p_serviceType + " p_interfaceCatgeory=" + p_interfaceCatgeory);
        boolean isSuccess = false;
        Connection con = null;
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
            SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(p_senderVO.getNetworkCode() + "_" + p_serviceType + "_" + p_interfaceCatgeory);
            if (subscriberRoutingControlVO != null) {
                // Set intentionally to get a unique transfer ID
                _txnIDSuffix = getCounter();
                _transferID = BTSLUtil.currentDateTimeFormatString() + _txnIDSuffix;
                if (_log.isDebugEnabled())
                    _log.debug("checkRoutingControlAndSendReq", "Generated Temp Transfer ID for Registration Controller" + _transferID + " Database Check Required=" + subscriberRoutingControlVO.isDatabaseCheckBool() + " Series Check Required=" + subscriberRoutingControlVO.isSeriesCheckBool());
                if (subscriberRoutingControlVO.isDatabaseCheckBool()) {
                    con = OracleUtil.getConnection();
                    if (p_interfaceCatgeory.equalsIgnoreCase(PretupsI.INTERFACE_CATEGORY_PRE)) {
                        ListValueVO listValueVO = PretupsBL.validateNumberInRoutingDatabase(con, p_senderVO.getMsisdn(), p_interfaceCatgeory);
                        try {
                            if (con != null)
                                con.close();
                        } catch (Exception e) {
                        }
                        if (listValueVO != null) {
                            _senderInterfaceInfoInDBFound = true;

                            setInterfaceDetails(p_senderVO, listValueVO, false, null);

                            if (_log.isDebugEnabled())
                                _log.debug("checkRoutingControlAndSendReq", "Sending Validation Request For MSISDN=" + p_senderVO.getMsisdn() + " On interface=" + _interfaceID);

                            isSuccess = processValidationRequest(p_senderVO, listValueVO.getValue(), listValueVO.getLabel(), p_interfaceCatgeory);
                        } else if (subscriberRoutingControlVO.isSeriesCheckBool()) {
                            if (_log.isDebugEnabled())
                                _log.debug("checkRoutingControlAndSendReq", _requestIDStr, " MSISDN=" + p_senderVO.getMsisdn() + " Not found in Subscriber Routing DB, check for Series");

                            MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null;
                            try {
                                interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_senderVO.getPrefixID(), p_senderVO.getSubscriberType(), PretupsI.INTERFACE_VALIDATE_ACTION);

                                setInterfaceDetails(p_senderVO, null, true, interfaceMappingVO);

                                if (_log.isDebugEnabled())
                                    _log.debug("checkRoutingControlAndSendReq", "Sending Validation Request For MSISDN=" + p_senderVO.getMsisdn() + " On interface=" + _interfaceID);

                                isSuccess = processValidationRequest(p_senderVO, _interfaceID, interfaceMappingVO.getHandlerClass(), p_interfaceCatgeory);
                            } catch (BTSLBaseException be) {
                                if (be.isKey() && (be.getMessageKey().equals(SelfTopUpErrorCodesI.MSISDN_PREFIX_INTERFACE_MAPPING_NOTFOUND))) {
                                    _log.error(this, "Interface Network Prefix Series Not Defined for p_senderVO.getPrefixID()=" + p_senderVO.getPrefixID() + " p_senderVO.getSubscriberType()= " + p_senderVO.getSubscriberType() + " Action=" + PretupsI.INTERFACE_VALIDATE_ACTION);
                                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "RegistrationController[checkRoutingControlAndSendReq]", "", "", "", "Interface Network Series Mapping Not exist for Series =" + p_senderVO.getMsisdnPrefix() + " Not Defined for Series type=" + p_senderVO.getSubscriberType() + " But validation required on that interface");
                                    isSuccess = false;
                                } else
                                    throw be;
                            }
                        } else
                            return isSuccess;
                    } else if (p_interfaceCatgeory.equalsIgnoreCase(PretupsI.INTERFACE_CATEGORY_POST)) {
                        WhiteListVO whiteListVO = PretupsBL.validateNumberInWhiteList(con, p_senderVO.getMsisdn());
                        try {
                            if (con != null)
                                con.close();
                        } catch (Exception e) {
                        }
                        if (whiteListVO != null) {
                            _senderInterfaceInfoInDBFound = true;
                            // Send Request to interface
                            ListValueVO listValueVO = whiteListVO.getListValueVO();

                            setInterfaceDetails(p_senderVO, listValueVO, false, null);

                            p_senderVO.setServiceClassCode(whiteListVO.getServiceClassCode());
                            p_senderVO.setAccountStatus(whiteListVO.getAccountStatus());

                            // Since Number was found in White List there is no
                            // need to send validation request to the
                            // interface thereby registering the user after it.
                            isSuccess = true;
                        } else if (subscriberRoutingControlVO.isSeriesCheckBool()) {
                            if (_log.isDebugEnabled())
                                _log.debug("checkRoutingControlAndSendReq", _requestIDStr, " MSISDN=" + p_senderVO.getMsisdn() + " Not found in White List Routing DB, check for Series");

                            MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null;
                            try {
                                interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_senderVO.getPrefixID(), p_senderVO.getSubscriberType(), PretupsI.INTERFACE_VALIDATE_ACTION);

                                setInterfaceDetails(p_senderVO, null, true, interfaceMappingVO);

                                if (_log.isDebugEnabled())
                                    _log.debug("checkRoutingControlAndSendReq", "Sending Validation Request For MSISDN=" + p_senderVO.getMsisdn() + " On interface=" + _interfaceID);

                                isSuccess = processValidationRequest(p_senderVO, _interfaceID, interfaceMappingVO.getHandlerClass(), p_interfaceCatgeory);
                            } catch (BTSLBaseException be) {
                                if (be.isKey() && (be.getMessageKey().equals(SelfTopUpErrorCodesI.MSISDN_PREFIX_INTERFACE_MAPPING_NOTFOUND))) {
                                    _log.error(this, "Interface Network Prefix Series Not Defined for p_senderVO.getPrefixID()=" + p_senderVO.getPrefixID() + " p_senderVO.getSubscriberType()= " + p_senderVO.getSubscriberType() + " Action=" + PretupsI.INTERFACE_VALIDATE_ACTION);
                                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "RegistrationController[checkRoutingControlAndSendReq]", "", "", "", "Interface Network Series Mapping Not exist for Series =" + p_senderVO.getMsisdnPrefix() + " Not Defined for Series type=" + p_senderVO.getSubscriberType() + " But validation required on that interface");
                                    isSuccess = false;
                                } else
                                    throw be;
                            }
                        } else
                            return isSuccess;
                    }
                    try {
                        if (con != null)
                            con.close();
                    } catch (Exception e) {
                    }
                } else if (subscriberRoutingControlVO.isSeriesCheckBool()) {
                    MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null;
                    try {
                        interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_senderVO.getPrefixID(), p_senderVO.getSubscriberType(), PretupsI.INTERFACE_VALIDATE_ACTION);

                        setInterfaceDetails(p_senderVO, null, true, interfaceMappingVO);

                        if (_log.isDebugEnabled())
                            _log.debug("checkRoutingControlAndSendReq", "Sending Validation Request For MSISDN=" + p_senderVO.getMsisdn() + " On interface=" + _interfaceID);

                        isSuccess = processValidationRequest(p_senderVO, _interfaceID, interfaceMappingVO.getHandlerClass(), p_interfaceCatgeory);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && (be.getMessageKey().equals(SelfTopUpErrorCodesI.MSISDN_PREFIX_INTERFACE_MAPPING_NOTFOUND))) {
                            _log.error(this, "Interface Network Prefix Series Not Defined for p_senderVO.getPrefixID()=" + p_senderVO.getPrefixID() + " p_senderVO.getSubscriberType()= " + p_senderVO.getSubscriberType() + " Action=" + PretupsI.INTERFACE_VALIDATE_ACTION);
                            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "RegistrationController[checkRoutingControlAndSendReq]", "", "", "", "Interface Network Series Mapping Not exist for Series =" + p_senderVO.getMsisdnPrefix() + " Not Defined for Series type=" + p_senderVO.getSubscriberType() + " But validation required on that interface");
                            isSuccess = false;
                        } else
                            throw be;
                    }
                } else
                    return isSuccess;
            } else {
                return isSuccess;
            }
        } catch (BTSLBaseException be) {
            // be.printStackTrace();
            _log.error("checkRoutingControlAndSendReq", "BTSLBaseException " + be.getMessage());
            if (be.isKey() && !_isRequestRefuse)
                throw be;
            else if (!_isRequestRefuse)
                isSuccess = false;
            else
                throw be;
        } catch (Exception e) {
            _log.error("checkRoutingControlAndSendReq", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RegisterationController[checkRoutingControlAndSendReq]", "", "", "", "Exception:" + e.getMessage());
            if (!_isRequestRefuse)
                isSuccess = false;
            else
                throw new BTSLBaseException(this, "checkRoutingControlAndSendReq", SelfTopUpErrorCodesI.P2P_REGISTERATION_ERROR);
        } finally {
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
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
        if (_log.isDebugEnabled())
            _log.debug("processValidationRequest", "Entered and performing validations for p_interfaceID" + p_interfaceID + " p_interfaceCatgeory=" + p_interfaceCatgeory + " p_handlerClass=" + p_handlerClass);
        boolean isSuccess = false;
        try {
            checkTransactionLoad(p_senderVO, p_interfaceID);
            _decreaseCountersReqd = true;

            NetworkInterfaceModuleVO networkInterfaceModuleVOS = (NetworkInterfaceModuleVO) NetworkInterfaceModuleCache.getObject(PretupsI.P2P_MODULE, _networkCode, p_interfaceCatgeory);
            _intModCommunicationTypeS = networkInterfaceModuleVOS.getCommunicationType();
            _intModIPS = networkInterfaceModuleVOS.getIP();
            _intModPortS = networkInterfaceModuleVOS.getPort();
            _intModClassNameS = networkInterfaceModuleVOS.getClassName();
            CommonClient commonClient = new CommonClient();
            LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_VAL);

            String requestStr = getSenderValidateStr(p_interfaceID, p_handlerClass);

            String senderValResponse = commonClient.process(requestStr, "", _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);
            HashMap map = BTSLUtil.getStringToHash(senderValResponse, "&", "=");
            String status = (String) map.get("TRANSACTION_STATUS");

            // Start: Update the Interface table for the interface ID based on
            // Handler status and update the Cache
            String interfaceStatusType = (String) map.get("INT_SET_STATUS");
            if (!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME.equals(interfaceStatusType)))
                new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES, p_interfaceID, interfaceStatusType, SelfTopUpErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG, PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
            // :End

            ArrayList altList = null;
            boolean isRequired = false;
            if (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status)) {
                altList = InterfaceRoutingControlCache.getRoutingControlDetails(p_interfaceID);
                if (altList != null && altList.size() > 0) {
                    if (_log.isDebugEnabled())
                        _log.debug("processValidationRequest", "Got Status=" + status + " After validation Request For MSISDN=" + p_senderVO.getMsisdn() + " Performing Alternate Routing");
                    performSenderAlternateRouting(p_senderVO, altList, p_interfaceCatgeory);
                    isSuccess = true;
                } else
                    isRequired = true;
            } else if (!InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) || isRequired) {
                if (BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS)) {
                    isSuccess = false;
                    LoadController.decreaseResponseCounters(_transferID, SelfTopUpErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_VAL_RESPONSE);
                    // throw new
                    // BTSLBaseException(this,"processValidationRequest",PretupsErrorCodesI.P2P_REGISTERATION_ERROR);
                    if (InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED.equals(status))
                        throw new BTSLBaseException("ResistrationController", "processValidationRequest", InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED + "_S");
                    else
                        throw new BTSLBaseException(this, "processValidationRequest", SelfTopUpErrorCodesI.P2P_REGISTERATION_ERROR);
                }
                LoadController.decreaseResponseCounters(_transferID, SelfTopUpErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.SENDER_VAL_RESPONSE);
                if ("Y".equals(_requestVO.getUseInterfaceLanguage())) {
                    // update the sender locale if language code returned from
                    // IN is not null
                    updateSenderLocale(p_senderVO, (String) map.get("IN_LANG"));
                }
                p_senderVO.setServiceClassCode((String) map.get("SERVICE_CLASS"));
                p_senderVO.setAccountStatus((String) map.get("ACCOUNT_STATUS"));
                isSuccess = true;
                if (PretupsI.INTERFACE_CATEGORY_PRE.equals(p_interfaceCatgeory)) {
                    SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(p_senderVO.getNetworkCode() + "_" + PretupsI.SERVICE_TYPE_REGISTERATION + "_" + p_interfaceCatgeory);
                    if (!_senderInterfaceInfoInDBFound && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
                        PretupsBL.insertSubscriberInterfaceRouting(_interfaceID, _senderExternalID, p_senderVO.getMsisdn(), p_interfaceCatgeory, p_senderVO.getUserID(), _requestVO.getCreatedOn());
                        _senderInterfaceInfoInDBFound = true;
                    }
                }
            }
        } catch (BTSLBaseException be) {
            isSuccess = false;
            throw be;
        } catch (Exception e) {
            isSuccess = false;
            _log.error("processValidationRequest", "BTSLBaseException " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RegisterationController[process]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "processValidationRequest", SelfTopUpErrorCodesI.P2P_REGISTERATION_ERROR);
        } finally {
            if (_decreaseCountersReqd)
                LoadController.decreaseTransactionInterfaceLoad(_transferID, p_senderVO.getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);
        }
        if (_log.isDebugEnabled())
            _log.debug("processValidationRequest", "Exiting with isSuccess" + isSuccess);

        return isSuccess;
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
     * Method to check the loads available in the system
     * 
     * @param p_senderVO
     * @param p_interfaceID
     * @throws BTSLBaseException
     */
    private void checkTransactionLoad(SenderVO p_senderVO, String p_interfaceID) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("checkTransactionLoad", "Checking load for MSISDN =" + p_senderVO.getMsisdn() + " p_interfaceID=" + p_interfaceID);
        int recieverLoadStatus = 0;
        try {
            // Do not enter the request in Queue
            recieverLoadStatus = LoadController.checkInterfaceLoad(p_senderVO.getNetworkCode(), p_interfaceID, _transferID, new C2STransferVO(), false);
            if (recieverLoadStatus == 0) {
                LoadController.checkTransactionLoad(p_senderVO.getNetworkCode(), p_interfaceID, PretupsI.P2P_MODULE, _transferID, true, LoadControllerI.USERTYPE_SENDER);
                if (_log.isDebugEnabled())
                    _log.debug("RegistrationController[checkTransactionLoad]", "_transferID=" + _transferID + " Successfully through load");
            }
            // Request in Queue
            else if (recieverLoadStatus == 1) {
                throw new BTSLBaseException("RegistrationController", "checkTransactionLoad", SelfTopUpErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
            }
            // Refuse the request
            else
                throw new BTSLBaseException("RegistrationController", "checkTransactionLoad", SelfTopUpErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
        } catch (BTSLBaseException be) {
            _log.error("RegistrationController[checkTransactionLoad]", "Refusing request getting Exception:" + be.getMessage());
            _isRequestRefuse = true;
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("RegistrationController[checkTransactionLoad]", "Refusing request getting Exception:" + e.getMessage());
            throw new BTSLBaseException("RegistrationController", "checkTransactionLoad", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
    }

    /**
     * Method to perform the sender alternate intreface routing controls
     * 
     * @param altList
     * @throws BTSLBaseException
     */
    private void performSenderAlternateRouting(SenderVO p_senderVO, ArrayList altList, String p_interfaceCatgeory) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("performSenderAlternateRouting", _requestIDStr, " Entered with p_interfaceCatgeory=" + p_interfaceCatgeory);
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
                    LoadController.decreaseResponseCounters(_transferID, SelfTopUpErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_VAL_RESPONSE);
                    LoadController.decreaseTransactionInterfaceLoad(_transferID, p_senderVO.getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);

                    listValueVO = (ListValueVO) altList.get(0);

                    setInterfaceDetails(p_senderVO, listValueVO, false, null);

                    checkTransactionLoad(p_senderVO, _interfaceID);

                    requestStr = getSenderValidateStr(_interfaceID, listValueVO.getLabel());
                    commonClient = new CommonClient();

                    LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_VAL);

                    TransactionLog.log(_transferID, _requestIDStr, p_senderVO.getMsisdn(), p_senderVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                    if (_log.isDebugEnabled())
                        _log.debug("performSenderAlternateRouting", "Sending Request For MSISDN=" + p_senderVO.getMsisdn() + " on ALternate Routing 1 to =" + _interfaceID);

                    senderValResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);

                    TransactionLog.log(_transferID, _requestIDStr, p_senderVO.getMsisdn(), p_senderVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL, senderValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                    try {
                        senderValidateResponse(p_senderVO, senderValResponse, 1, altList.size());
                        if (SelfTopUpErrorCodesI.TXN_STATUS_SUCCESS.equals(_txnStatus)) {
                            LoadController.decreaseResponseCounters(_transferID, SelfTopUpErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.SENDER_VAL_RESPONSE);

                            if (PretupsI.INTERFACE_CATEGORY_PRE.equals(p_interfaceCatgeory)) {
                                // Update in DB for routing interface
                                updateSubscriberRoutingDetails(p_senderVO.getNetworkCode(), _interfaceID, _senderExternalID, p_senderVO.getMsisdn(), p_interfaceCatgeory, p_senderVO.getUserID(), _requestVO.getCreatedOn());
                            }
                        }
                    } catch (BTSLBaseException be) {
                        throw be;
                    } catch (Exception e) {
                        throw e;
                    }

                    break;
                }
                case 2: {
                    LoadController.decreaseResponseCounters(_transferID, SelfTopUpErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_VAL_RESPONSE);
                    LoadController.decreaseTransactionInterfaceLoad(_transferID, p_senderVO.getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);

                    listValueVO = (ListValueVO) altList.get(0);

                    setInterfaceDetails(p_senderVO, listValueVO, false, null);

                    checkTransactionLoad(p_senderVO, _interfaceID);

                    requestStr = getSenderValidateStr(_interfaceID, listValueVO.getLabel());
                    commonClient = new CommonClient();

                    LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_VAL);

                    TransactionLog.log(_transferID, _requestIDStr, p_senderVO.getMsisdn(), p_senderVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                    if (_log.isDebugEnabled())
                        _log.debug("performSenderAlternateRouting", "Sending Request For MSISDN=" + p_senderVO.getMsisdn() + " on ALternate Routing 1 to =" + _interfaceID);

                    senderValResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);

                    TransactionLog.log(_transferID, _requestIDStr, p_senderVO.getMsisdn(), p_senderVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL, senderValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                    try {
                        senderValidateResponse(p_senderVO, senderValResponse, 1, altList.size());
                        if (SelfTopUpErrorCodesI.TXN_STATUS_SUCCESS.equals(_txnStatus)) {
                            LoadController.decreaseResponseCounters(_transferID, SelfTopUpErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.SENDER_VAL_RESPONSE);

                            if (PretupsI.INTERFACE_CATEGORY_PRE.equals(p_interfaceCatgeory)) {
                                updateSubscriberRoutingDetails(p_senderVO.getNetworkCode(), _interfaceID, _senderExternalID, p_senderVO.getMsisdn(), p_interfaceCatgeory, p_senderVO.getUserID(), _requestVO.getCreatedOn());
                            }
                        }
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(be.getMessageKey())) {
                            if (_log.isDebugEnabled())
                                _log.debug("performSenderAlternateRouting", "Got Status=" + InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND + " After validation Request For MSISDN=" + p_senderVO.getMsisdn() + " Performing Alternate Routing to 2");

                            LoadController.decreaseResponseCounters(_transferID, SelfTopUpErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_VAL_RESPONSE);
                            LoadController.decreaseTransactionInterfaceLoad(_transferID, p_senderVO.getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);

                            listValueVO = (ListValueVO) altList.get(1);

                            setInterfaceDetails(p_senderVO, listValueVO, false, null);

                            checkTransactionLoad(p_senderVO, _interfaceID);

                            requestStr = getSenderValidateStr(_interfaceID, listValueVO.getLabel());

                            LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_VAL);

                            TransactionLog.log(_transferID, _requestIDStr, p_senderVO.getMsisdn(), p_senderVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                            if (_log.isDebugEnabled())
                                _log.debug("performSenderAlternateRouting", "Sending Request For MSISDN=" + p_senderVO.getMsisdn() + " on ALternate Routing 2 to =" + _interfaceID);

                            senderValResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);

                            TransactionLog.log(_transferID, _requestIDStr, p_senderVO.getMsisdn(), p_senderVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL, senderValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                            try {
                                senderValidateResponse(p_senderVO, senderValResponse, 1, altList.size());
                                if (SelfTopUpErrorCodesI.TXN_STATUS_SUCCESS.equals(_txnStatus)) {
                                    LoadController.decreaseResponseCounters(_transferID, SelfTopUpErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.SENDER_VAL_RESPONSE);

                                    if (PretupsI.INTERFACE_CATEGORY_PRE.equals(p_interfaceCatgeory)) {
                                        updateSubscriberRoutingDetails(p_senderVO.getNetworkCode(), _interfaceID, _senderExternalID, p_senderVO.getMsisdn(), p_interfaceCatgeory, p_senderVO.getUserID(), _requestVO.getCreatedOn());
                                    }
                                }
                            } catch (BTSLBaseException bex) {
                                throw bex;
                            } catch (Exception e) {
                                throw e;
                            }
                        } else {
                            throw be;
                        }
                    } catch (Exception e) {
                        throw e;
                    }
                    break;
                }
                }

            } else
                return;
        } catch (BTSLBaseException be) {
            LoadController.decreaseResponseCounters(_transferID, SelfTopUpErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_VAL_RESPONSE);
            throw be;
        } catch (Exception e) {
            LoadController.decreaseResponseCounters(_transferID, SelfTopUpErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_VAL_RESPONSE);
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RegsiterationController[performSenderAlternateRouting]", _transferID, p_senderVO.getMsisdn(), p_senderVO.getNetworkCode(), "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "performSenderAlternateRouting", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("performSenderAlternateRouting", _requestIDStr, " Exiting ");
    }

    /**
     * Method to handle sender validation response for interface routing
     * 
     * @param p_senderVO
     * @param str
     * @param p_attempt
     * @param p_altSize
     * @throws BTSLBaseException
     */
    public void senderValidateResponse(SenderVO p_senderVO, String str, int p_attempt, int p_altSize) throws BTSLBaseException {
        HashMap map = BTSLUtil.getStringToHash(str, "&", "=");
        String status = (String) map.get("TRANSACTION_STATUS");
        String interfaceID = (String) map.get("INTERFACE_ID");

        // Start: Update the Interface table for the interface ID based on
        // Handler status and update the Cache
        String interfaceStatusType = (String) map.get("INT_SET_STATUS");
        if (!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME.equals(interfaceStatusType)))
            new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES, interfaceID, interfaceStatusType, SelfTopUpErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG, PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
        // :End

        if (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) && p_attempt == 1 && p_attempt < p_altSize) {
            throw new BTSLBaseException(this, "senderValidateResponse", InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
        }
        if (BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS)) {
            throw new BTSLBaseException(this, "processValidationRequest", SelfTopUpErrorCodesI.INTERFACE_ERROR_RESPONSE);
        }
        _txnStatus = SelfTopUpErrorCodesI.TXN_STATUS_SUCCESS;
        p_senderVO.setServiceClassCode((String) map.get("SERVICE_CLASS"));
        p_senderVO.setAccountStatus((String) map.get("ACCOUNT_STATUS"));
    }

    /**
     * Method: updateSenderLocale
     * This method update the sender locale with the language code returned from
     * the IN
     * 
     * @param p_senderVO
     *            SenderVO
     * @param p_languageCode
     *            String
     * @return void
     */
    public void updateSenderLocale(SenderVO p_senderVO, String p_languageCode) {
        if (_log.isDebugEnabled())
            _log.debug("updateSenderLocale", "Entered p_languageCode=" + p_languageCode);
        // check if language is returned fron IN or not.
        // If not then send alarm and not set the locale
        // otherwise set the local corresponding to the code returned from the
        // IN.
        if (!BTSLUtil.isNullString(p_languageCode)) {
            try {
                if (LocaleMasterCache.getLocaleFromCodeDetails(p_languageCode) == null)
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "RegsiterationController[updateSenderLocale]", _transferID, _msisdn, "", "Exception: Notification language returned from IN is not defined in system p_languageCode: " + p_languageCode);
                else {
                    _senderLocale = (LocaleMasterCache.getLocaleFromCodeDetails(p_languageCode));
                    p_senderVO.setCountry(_senderLocale.getCountry());
                    p_senderVO.setLanguage(_senderLocale.getLanguage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (_log.isDebugEnabled())
            _log.debug("updateSenderLocale", "Exited _senderLocale=" + _senderLocale);
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
        if (_log.isDebugEnabled())
            _log.debug("setInterfaceDetails", _requestIDStr, " Entered p_listValueVO=" + p_listValueVO + " p_useInterfacePrefixVO=" + p_useInterfacePrefixVO + " p_MSISDNPrefixInterfaceMappingVO=" + p_MSISDNPrefixInterfaceMappingVO);
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
                // locale master table
                if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(new Locale(p_senderVO.getLanguage(), p_senderVO.getCountry()))).getMessage()))
                    _requestVO.setSenderReturnMessage(message1);
                else
                    _requestVO.setSenderReturnMessage(message2);
                throw new BTSLBaseException(this, "setInterfaceDetails", SelfTopUpErrorCodesI.INTERFACE_NOT_ACTIVE);
            }
        } catch (BTSLBaseException be) {
            _log.error("setInterfaceDetails", "Getting Base Exception =" + be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RegisterationController[setInterfaceDetails]", _requestIDStr, p_senderVO.getMsisdn(), p_senderVO.getNetworkCode(), "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "setInterfaceDetails", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
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
        if (_log.isDebugEnabled())
            _log.debug("updateSubscriberRoutingDetails", _requestIDStr, " Entered p_networkCode=" + p_networkCode + " p_interfaceID=" + p_interfaceID + " p_externalID=" + p_externalID + " p_msisdn=" + p_msisdn + " p_interfaceCategory=" + p_interfaceCategory + " p_userID=" + p_userID + " p_currentDate=" + p_currentDate);
        try {
            // Update in DB for routing interface
            if (_senderInterfaceInfoInDBFound)
                PretupsBL.updateSubscriberInterfaceRouting(p_interfaceID, p_externalID, p_msisdn, p_interfaceCategory, p_userID, p_currentDate);
            else {
                SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(p_networkCode + "_" + PretupsI.SERVICE_TYPE_REGISTERATION + "_" + p_interfaceCategory);
                if (!_senderInterfaceInfoInDBFound && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
                    PretupsBL.insertSubscriberInterfaceRouting(p_interfaceID, p_externalID, p_msisdn, p_interfaceCategory, p_userID, p_currentDate);
                    _senderInterfaceInfoInDBFound = true;
                }
            }
        } catch (BTSLBaseException be) {
            _log.error("updateSubscriberRoutingDetails", "Getting Base Exception =" + be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RegisterationController[updateSubscriberRoutingDetails]", _requestIDStr, p_msisdn, p_networkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updateSubscriberRoutingDetails", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
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
        if (_counter >= 1000)
            _counter = 0;
        return _counter++;
    }
}