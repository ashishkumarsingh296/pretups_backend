package com.inter.nokia;

import java.util.HashMap;
import org.omg.CORBA.ByteHolder;
import org.omg.CORBA.ByteHolderHolder;
import org.omg.CORBA.IntHolder;
import org.omg.CORBA.IntHolderHolder;
import org.omg.CORBA.StringHolder;
import org.omg.CORBA.StringHolderHolder;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.UserException;
import CosNaming.NameComponent;
import CosNaming.NamingContext;
import CosNaming.NamingContextHelper;
import FFS.AccessDenied;
import FFS.FactoryFinder;
import FFS.FactoryFinderHelper;
import IE.Iona.OrbixWeb._CORBA;
import IE.Iona.OrbixWeb.CORBA.ORB;
import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.util.ConfigServlet;
import com.inter.pool.ClientMarkerI;
import com.nokia.in.smi.stub.PPAccountFactory;
import com.nokia.in.smi.stub.PPAccountFactoryHelper;
import com.nokia.in.smi.stub.PPSubscriberStatusFactory;
import com.nokia.in.smi.stub.OPCI.AccountManager;

/**
 * @(#)NokiaClientPP
 *                   Copyright(c) 2007, Bharti Telesoft Int. Public Ltd.
 *                   All Rights Reserved
 *                   ----------------------------------------------------------
 *                   ---------------------------------------
 *                   Author Date History
 *                   ----------------------------------------------------------
 *                   ---------------------------------------
 *                   Ashish Kumar Jan 3, 2007 Initial Creation
 *                   ----------------------------------------------------------
 *                   --------------------------------------
 *                   This class is to get the reference of remote objects using
 *                   CORBA.
 */
public class NokiaClientPP implements ClientMarkerI {
    private static Log _log = LogFactory.getLog("NokiaClientPP".getClass().getName());
    private String _interfaceID;// Contains the interface id;
    private ORB _orb;
    private org.omg.CORBA.Object _initRef;
    private org.omg.CORBA.Object _authServerObj;
    private NamingContext _namingContext;
    private NameComponent _namingComponent[];
    private FactoryFinder _invokeClientFactory;
    AccountManager _accountManager;
    PPAccountFactory _ppAccountFactory;
    private org.omg.CORBA.Object OrbRef;
    PPSubscriberStatusFactory _ppSubscriberStatusFactory;
    private String _loginId;
    private String _password;
    private Integer _inPort;

    private int sendAmount = 0;

    // For testing the bellow parameters are required.
    /*
     * private PPAccountFactoryTest _ppAccountFactoryTest;
     * private PPAccountManagerTest _ppAccountManager;
     */
    long _provisionId;
    long _scpId;
    long _providerId;

    public NokiaClientPP() {

    }

    /**
     * This constructor would be responsible to initialize ORB and would
     * get the references of remote object.
     * 
     * @param p_interfaceID
     */
    public NokiaClientPP(String p_interfaceID) throws BTSLBaseException, Exception {
        // Reference a destroy method, that would destroy the Remote object
        // reference.
        this._interfaceID = p_interfaceID;

        _orb = null;
        _initRef = null;
        _authServerObj = null;
        OrbRef = null;
        _namingComponent = new NameComponent[2];
        _invokeClientFactory = null;
        _ppAccountFactory = null;
        _ppSubscriberStatusFactory = null;
        _accountManager = null;

        if (_log.isDebugEnabled())
            _log.debug("NokiaClient[Constructor]", "Entered p_interfaceID:" + p_interfaceID);
        try {
            String ipLocalHostName = FileCache.getValue(_interfaceID, "IT_LOCAL_HOSTNAME");
            if (InterfaceUtil.isNullString(ipLocalHostName)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaClient[Constructor]", "", "INTERFACE_ID:" + _interfaceID, "", "IT_LOCAL_HOSTNAME is not defined in the INFile");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }

            String itNamesServerHost = FileCache.getValue(_interfaceID, "IT_NAMES_SERVER_HOST");
            if (InterfaceUtil.isNullString(itNamesServerHost)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaClient[Constructor]", "", "INTERFACE_ID:" + _interfaceID, "", "IT_NAMES_SERVER_HOST is not defined in the INFile");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }

            String itUseHostNameINIOR = FileCache.getValue(_interfaceID, "IT_USE_HOSTNAME_IN_IOR");
            if (InterfaceUtil.isNullString(itUseHostNameINIOR)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaClient[Constructor]", "", "INTERFACE_ID:" + _interfaceID, "", "IT_USE_HOSTNAME_IN_IOR is not defined in the INFile");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }

            String itBindUsingPort = FileCache.getValue(_interfaceID, "IT_BIND_USING_IIOP");
            if (InterfaceUtil.isNullString(itBindUsingPort)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaClient[Constructor]", "", "INTERFACE_ID:" + _interfaceID, "", "IT_BIND_USING_IIOP is not defined in the INFile");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }

            String itDaemonPort = FileCache.getValue(_interfaceID, "IT_DAEMON_PORT");
            if (InterfaceUtil.isNullString(itDaemonPort)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaClient[Constructor]", "", "INTERFACE_ID:" + _interfaceID, "", "IT_DAEMON_PORT is not defined in the INFile");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }

            String itOrbixIIOPPort = FileCache.getValue(_interfaceID, "IT_ORBIXD_IIOP_PORT");
            if (InterfaceUtil.isNullString(itOrbixIIOPPort)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaClient[Constructor]", "", "INTERFACE_ID:" + _interfaceID, "", "IT_ORBIXD_IIOP_PORT is not defined in the INFile");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }

            String itNamesPort = FileCache.getValue(_interfaceID, "IT_NAMES_PORT");
            if (InterfaceUtil.isNullString(itNamesPort)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaClient[Constructor]", "", "INTERFACE_ID:" + _interfaceID, "", "IT_NAMES_PORT is not defined in the INFile");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }

            String itNSPort = FileCache.getValue(_interfaceID, "IT_NS_PORT");
            if (InterfaceUtil.isNullString(itNSPort)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaClient[Constructor]", "", "INTERFACE_ID:" + _interfaceID, "", "IT_NS_PORT is not defined in the INFile");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }

            String itConnectionTimeOut = FileCache.getValue(_interfaceID, "IT_CONNECTION_TIMEOUT");
            if (InterfaceUtil.isNullString(itConnectionTimeOut)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaClient[Constructor]", "", "INTERFACE_ID:" + _interfaceID, "", "IT_CONNECTION_TIMEOUT is not defined in the INFile");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }

            String itDefaultTimeOut = FileCache.getValue(_interfaceID, "IT_DEFAULT_TIMEOUT");
            if (InterfaceUtil.isNullString(itDefaultTimeOut)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaClient[Constructor]", "", "INTERFACE_ID:" + _interfaceID, "", "IT_DEFAULT_TIMEOUT is not defined in the INFile");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }

            String pingDuringBind = FileCache.getValue(_interfaceID, "PIN_DURING_BIND");
            if (InterfaceUtil.isNullString(pingDuringBind)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaClient[Constructor]", "", "INTERFACE_ID:" + _interfaceID, "", "PIN_DURING_BIND is not defined in the INFile");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }

            String portIIOP = FileCache.getValue(_interfaceID, "IT_IIOP_PORT");
            if (InterfaceUtil.isNullString(portIIOP)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaClient[Constructor]", "", "INTERFACE_ID:" + _interfaceID, "", "portIIOP is not defined in the INFile");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }

            String portIIOPOrbix = FileCache.getValue(_interfaceID, "IT_ORBIXD_IIOP_PORT");
            if (InterfaceUtil.isNullString(portIIOPOrbix)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaClient[Constructor]", "", "INTERFACE_ID:" + _interfaceID, "", "portIIOPOrbix is not defined in the INFile");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }

            String portNames = FileCache.getValue(_interfaceID, "IT_NAMES_PORT");
            if (InterfaceUtil.isNullString(portNames)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaClient[Constructor]", "", "INTERFACE_ID:" + _interfaceID, "", "portNames is not defined in the INFile");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }

            String portNS = FileCache.getValue(_interfaceID, "IT_NS_PORT");
            if (InterfaceUtil.isNullString(portNS)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaClient[Constructor]", "", "INTERFACE_ID:" + _interfaceID, "", "portNS is not defined in the INFile");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }
            _loginId = FileCache.getValue(_interfaceID, "LOGIN_ID");
            if (InterfaceUtil.isNullString(_loginId)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaClient[Constructor]", "", "INTERFACE_ID:" + _interfaceID, "", "LOGIN_ID is not defined in the INFile");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }

            _password = FileCache.getValue(_interfaceID, "PASSWORD");
            if (InterfaceUtil.isNullString(_password)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaClient[Constructor]", "", "INTERFACE_ID:" + _interfaceID, "", "PASSWORD is not defined in the INFile");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }
            if (_log.isDebugEnabled())
                _log.debug("NokiaClient[Constructor]", "ipLocalHostName:" + ipLocalHostName + " itNamesServerHost:" + itNamesServerHost + " itUseHostNameINIOR:" + itUseHostNameINIOR + " itBindUsingPort:" + itBindUsingPort + " itDaemonPort:" + itDaemonPort + " itOrbixIIOPPort:" + itOrbixIIOPPort + " itNamesPort:" + itNamesPort + " itNSPort:" + itNSPort + " itConnectionTimeOut:" + itConnectionTimeOut + " itDefaultTimeOut:" + itDefaultTimeOut + " pingDuringBind:" + pingDuringBind + " _loginId:" + _loginId + " _password:" + _password);
            try {
                if (_orb == null) {
                    /*
                     * myOrb = (ORB)ORB.init(null);
                     * _CORBA.Orbix.setConfigItem("IT_LOCAL_HOSTNAME",
                     * _ipAddress);
                     * _CORBA.Orbix.setConfigItem("IT_NAMES_SERVER_HOST",
                     * itNamesServerHost.trim()));
                     * _CORBA.Orbix.setConfigItem("IT_USE_HOSTNAME_IN_IOR",
                     * itUseHostNameINIOR.trim());
                     * _CORBA.Orbix.setConfigItem("IT_BIND_USING_IIOP",
                     * itBindUsingPort.trim());
                     * _CORBA.Orbix.setConfigItem("IT_DAEMON_PORT",
                     * itDaemonPort.trim());
                     * _CORBA.Orbix.setConfigItem("IT_ORBIXD_PORT",itOrbixIIOPPort
                     * .trim());
                     * _CORBA.Orbix.setConfigItem("IT_IIOP_PORT",
                     * portIIOP.trim());
                     * _CORBA.Orbix.setConfigItem("IT_ORBIXD_IIOP_PORT",
                     * portIIOPOrbix.trim());
                     * _CORBA.Orbix.setConfigItem("IT_NAMES_PORT",
                     * portNames.trim());
                     * _CORBA.Orbix.setConfigItem("IT_NS_PORT", portNS.trim());
                     */
                    _orb = (IE.Iona.OrbixWeb.CORBA.ORB) ORB.init(null);
                    _CORBA.Orbix.setConfigItem("IT_LOCAL_HOSTNAME", "localhost");
                    _CORBA.Orbix.setConfigItem("IT_NAMES_SERVER_HOST", itNamesServerHost.trim());
                    _CORBA.Orbix.setConfigItem("IT_USE_HOSTNAME_IN_IOR", itUseHostNameINIOR.trim());
                    _CORBA.Orbix.setConfigItem("IT_BIND_USING_IIOP", itBindUsingPort.trim());
                    _CORBA.Orbix.setConfigItem("IT_DAEMON_PORT", itDaemonPort.trim());
                    _CORBA.Orbix.setConfigItem("IT_ORBIXD_PORT", itOrbixIIOPPort.trim());
                    _CORBA.Orbix.setConfigItem("IT_IIOP_PORT", portIIOP.trim());
                    _CORBA.Orbix.setConfigItem("IT_ORBIXD_IIOP_PORT", portIIOPOrbix.trim());
                    _CORBA.Orbix.setConfigItem("IT_NAMES_PORT", portNames.trim());
                    _CORBA.Orbix.setConfigItem("IT_NS_PORT", portNS.trim());
                    _CORBA.Orbix.setConfigItem("IT_CONNECTION_TIMEOUT", itConnectionTimeOut.trim());
                    _CORBA.Orbix.setConfigItem("IT_DEFAULT_TIMEOUT", itDefaultTimeOut.trim());
                    _CORBA.Orbix.setConfigItem("pingDuringBind", pingDuringBind.trim());
                }
            } catch (Exception e)// Here CORBA Exception can be caught.
            {
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }
            connectionEstablished(_interfaceID);
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            // //PreTUplogs.errLog("0", "BIND", "NokClient[constructor]", "0",
            // "Error binding due to " + exception.toString(), "500");
            throw new Exception("In Exception:: Unable to initialize ORB object");
        }
    }

    /**
     * This method is used to get the Client Factory based on the NameService
     * 
     * @param _interfaceID
     * @throws Exception
     */
    public void connectionEstablished(String _interfaceID) throws BTSLBaseException, Exception {
        try {
            if (_log.isDebugEnabled())
                _log.debug("connectionEstablished", "Entered");
            getInvokerClientFactoryFromNameService();
            inLogin();
            if (_log.isDebugEnabled())
                _log.debug("connectionEstablished ", "Successfully bound to a Nokia OPCI Server.");
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception exception) {
            _log.error("connectionEstablished", "Couldn't resolve Nokia OPCI: exception" + exception.getMessage());
            throw new Exception(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
        }
    }

    /**
     * This method is used to get the Factory to look up the various services.
     * 
     * @throws BTSLBaseException
     * @throws Exception
     */

    private void getInvokerClientFactoryFromNameService() throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("getInvokerClientFactoryFromNameService", "Entered");
        _namingContext = null;
        try {
            _initRef = _CORBA.Orbix.resolve_initial_references("NameService");
            if (_log.isDebugEnabled())
                _log.debug("getInvokerClientFactoryFromNameService", "initRef:" + _initRef);
        } catch (SystemException sysEx) {
            _log.error("getInvokerClientFactoryFromNameService", "SystemException" + sysEx.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NOT_RESOLVE_NAMESERVICE);
        } catch (UserException userexception) {
            _log.error("getInvokerClientFactoryFromNameService", "org.omg.CORBA.UserException::" + userexception);
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NOT_RESOLVE_NAMESERVICE);
        }
        try {
            _namingContext = NamingContextHelper.narrow(_initRef);
            if (_log.isDebugEnabled())
                _log.debug("getInvokerClientFactoryFromNameService", "NameCtx:" + _namingContext);
        } catch (SystemException sysEx) {
            _log.error("getInvokerClientFactoryFromNameService", "Context narrow failure SystemException:" + sysEx.getMessage());
            throw new Exception(InterfaceErrorCodesI.ERROR_NOT_NARROW_INITREF);
        }
        try {
            _namingComponent[0] = new NameComponent("service", "");
            _namingComponent[1] = new NameComponent("FactoryFinder", "");
            _authServerObj = _namingContext.resolve(_namingComponent);
            if (_log.isDebugEnabled())
                _log.debug("getInvokerClientFactoryFromNameService", "authServerObj:" + _authServerObj);
        } catch (SystemException sysEx1) {
            System.err.println("getInvokerClientFactoryFromNameService" + sysEx1.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NOT_RESOLVE_NAMECOMPONENT);
        } catch (Exception exception) {
            System.err.println("authServerObj :: Exception: " + exception.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NOT_RESOLVE_NAMECOMPONENT);
        }
        try {
            _invokeClientFactory = FactoryFinderHelper.narrow(_authServerObj);
            if (_invokeClientFactory == null) {
                if (_log.isDebugEnabled())
                    _log.debug("getInvokerClientFactoryFromNameService", "_invokeClientFactory is null at this time");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_FACTORY_OBJ_NULL);
            }
        } catch (SystemException systemexception3) {
            _log.error("getInvokerClientFactoryFromNameService", " Context narrow failure SystemException:" + systemexception3.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NOT_NARROW_AUTHSERVICE);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NOT_NARROW_AUTHSERVICE);
        }
    }

    /**
     * 
     * @param s
     * @throws Exception
     */
    private void inLogin() throws Exception {
        try {
            if (_log.isDebugEnabled())
                _log.debug("inLogin", "Login ID and Pass=" + _loginId + "," + _password);
            OrbRef = _invokeClientFactory.getFactory(_loginId, _password, "PPAccountFactory");
        } catch (Exception ex) {
            ex.printStackTrace();
            _log.error("inLogin", "Exception ex:" + ex.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NOT_GET_PPACCOUNTFACTORYREF);
        }
        // Lines Added for getting OrbRefPPSSF object for Multiplatform Change
        /*
         * try
         * {
         * OrbRefPPSSF = _invokeClientFactory.getFactory(_loginId, _password,
         * "PPsubscriberStatusFactory");
         * }
         * catch(Exception exception)
         * {
         * if(_log.isDebugEnabled()) _log.error("inLogin",
         * "Couldn't able to login to IN Server! for PPsubscriberStatusFactory exception:"
         * +exception.getMessage());
         * //PreTUplogs.errLog("0", "BIND",
         * "NokClient[INLogin] PPsubscriberStatusFactory", "0", "Error Due to: "
         * + exception.toString(), "500");
         * exception.printStackTrace();
         * throw new Exception(
         * "OrbRef is null while logging to Server PPsubscriberStatusFactory");
         * }
         */
        // Lines Ended for getting OrbRefPPSSF object for Multiplatform Change

        // Lines Added for getting PPsubscriberStatusFactory object for
        // Multiplatform Change
        try {
            _ppAccountFactory = PPAccountFactoryHelper.narrow(OrbRef);
        } catch (Exception exception1) {
            exception1.printStackTrace();
            System.out.println("Not able to narrow OrbRef through PPAccountFactory Object!");
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NOT_GET_PPACCOUNTFACTORYREF);
        }
        /*
         * try
         * {
         * _ppSubscriberStatusFactory =
         * PPSubscriberStatusFactoryHelper.narrow(OrbRefPPSSF);
         * }
         * catch(Exception e2)
         * {
         * e2.printStackTrace();
         * _log.error("inLogin","While getting PPsubscriberStatusFactory e2:"+e2.
         * getMessage());
         * throw new Exception(
         * "PPsubscriberStatusFactory is null while narrowing the OrbRef object PPsubscriberStatusFactory"
         * );
         * }
         */
    }

    /**
     * This method is invoked from the handler class to send the request for the
     * corresponding action.
     * 
     * @param p_requestMap
     * @param p_action
     * @return String
     * @throws BTSLBaseException
     * @throws Exception
     */
    public String sendRequest(HashMap p_requestMap, int p_action) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("sendRequest", "p_requestMap::" + p_requestMap + " p_action:" + p_action);
        String respStr = null;
        try {
            switch (p_action) {
            case NokiaI.ACTION_ACCOUNT_INFO: {
                respStr = accountInfoRequest(p_requestMap);
                break;
            }
            case NokiaI.ACTION_RECHARGE_CREDIT: {
                respStr = creditRequest(p_requestMap);
                break;
            }
            case NokiaI.ACTION_IMMEDIATE_DEBIT: {
                respStr = debitRequest(p_requestMap);
                break;
            }
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("sendRequest", "Exiting:respStr:" + respStr);
        }
        return respStr;
    }

    private String accountInfoRequest(HashMap p_requestMap) throws Exception// List
                                                                            // all(MAIN)
                                                                            // the
                                                                            // excpetion
                                                                            // thrown
                                                                            // by
                                                                            // CORBA
    {
        if (_log.isDebugEnabled())
            _log.debug("accountInfoRequest", "Entered:p_requestMap:" + p_requestMap);
        StringBuffer respBuff = new StringBuffer();
        String outString = null;
        try {
            if (_ppAccountFactory != null) {
                if (_log.isDebugEnabled())
                    _log.debug("accountInfoRequest", "Connected to the Nokia OPCI Server on _interfaceID:: " + _interfaceID + " and port ::" + _inPort + "::MSISDN::" + p_requestMap.get("MSISDN"));

                ByteHolder byteholder = (new ByteHolderHolder()).value;
                IntHolder intholder = (new IntHolderHolder()).value;
                IntHolder intholder1 = (new IntHolderHolder()).value;
                IntHolder intholder2 = (new IntHolderHolder()).value;
                IntHolder intholder3 = (new IntHolderHolder()).value;
                IntHolder intholder4 = (new IntHolderHolder()).value;
                IntHolder intholder5 = (new IntHolderHolder()).value;
                IntHolder intholder6 = (new IntHolderHolder()).value;
                IntHolder intholder7 = (new IntHolderHolder()).value;
                IntHolder intholder8 = (new IntHolderHolder()).value;
                IntHolder intholder9 = (new IntHolderHolder()).value;
                IntHolder intholder10 = (new IntHolderHolder()).value;
                IntHolder intholder11 = (new IntHolderHolder()).value;
                StringHolder stringholder = (new StringHolderHolder()).value;
                StringHolder stringholder1 = (new StringHolderHolder()).value;
                StringHolder stringholder2 = (new StringHolderHolder()).value;
                StringHolder stringholder3 = (new StringHolderHolder()).value;
                StringHolder stringholder4 = (new StringHolderHolder()).value;
                StringHolder stringholder5 = (new StringHolderHolder()).value;
                StringHolder stringholder6 = (new StringHolderHolder()).value;
                String msisdn = InterfaceUtil.getFilterMSISDN((String) p_requestMap.get("INTERFACE_ID"), (String) p_requestMap.get("MSISDN"));

                _ppAccountFactory.getSubscriptionInfo(msisdn, '\001', byteholder, intholder, stringholder, intholder1, intholder2, stringholder2, stringholder3, stringholder4, stringholder5, intholder3, intholder4, intholder5, intholder6, intholder7, intholder8, intholder9, stringholder1, intholder10, intholder11, stringholder6);
                respBuff.append("&statusInd=");
                respBuff.append(byteholder.value);
                respBuff.append("&balance=");
                respBuff.append(intholder.value);
                System.out.println("\n\n Balance Received from Nokia IN :" + intholder.value);
                respBuff.append("&lastCallDate=");
                respBuff.append(stringholder.value);
                respBuff.append("&lastCallCost=");
                respBuff.append(intholder1.value);
                respBuff.append("&expiryInd=");
                respBuff.append(intholder2.value);
                respBuff.append("&NCEDate=");
                respBuff.append(stringholder2.value);
                respBuff.append("&CreditExpiry=");
                respBuff.append(stringholder3.value);
                respBuff.append("&NSEDate=");
                respBuff.append(stringholder4.value);
                respBuff.append("&ServiceExpiry=");
                respBuff.append(stringholder5.value);
                respBuff.append("&subscriberLanguage=");
                respBuff.append(intholder3.value);
                respBuff.append("&rechargeAllowed=");
                respBuff.append(intholder4.value);
                respBuff.append("&secondBeforeClearing=");
                respBuff.append(intholder5.value);
                respBuff.append("&firstCallIndicator=");
                respBuff.append(intholder6.value);
                respBuff.append("&provisionId=");
                respBuff.append(intholder7.value);
                respBuff.append("&scpId=");
                respBuff.append(intholder8.value);
                respBuff.append("&latestStateChange=");
                respBuff.append(stringholder1.value);
                respBuff.append("&expiryServiceId=");
                respBuff.append(intholder10.value);
                respBuff.append("&profileId=");
                respBuff.append(intholder11.value);
                outString = respBuff.toString();
            } else {
                throw new Exception("Could not connect to the Nokia OPCI Server-XXX");
            }
        } catch (AccessDenied accessdenied) {
            _log.error("accountInfoRequest", "AccessDenied Exception :: " + accessdenied.getMessage());
            accessdenied.printStackTrace();
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NOT_PROCESS_REQ);
        } catch (Exception exception) {
            _log.error("accountInfoRequest", "Exception :: " + exception.getMessage());
            exception.printStackTrace();
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NOT_PROCESS_REQ);
        } finally {
            respBuff = null;
            if (_log.isDebugEnabled())
                _log.debug("accountInfoRequest", "Exited :_outString" + outString);
        }
        return outString;
    }// end of accountInfoRequest

    private String creditRequest(HashMap p_requestMap) throws Exception// List
                                                                       // all(MAIN)
                                                                       // the
                                                                       // excpetion
                                                                       // thrown
                                                                       // by
                                                                       // CORBA
    {
        if (_log.isDebugEnabled())
            _log.debug("creditRequest", "Entered:p_requestMap:" + p_requestMap);

        String responseStr = null;
        try {
            String transferAmount = (String) p_requestMap.get("transfer_amount");
            String balanceQuery = (String) p_requestMap.get("balanceQuery");

            sendAmount = Integer.parseInt(transferAmount);
            ByteHolder byteholder = (new ByteHolderHolder()).value;
            StringHolder stringholder = (new StringHolderHolder()).value;
            StringHolder stringholder1 = (new StringHolderHolder()).value;
            StringHolder stringholder2 = (new StringHolderHolder()).value;
            StringHolder stringholder3 = (new StringHolderHolder()).value;
            ByteHolder byteholder1 = (new ByteHolderHolder()).value;
            IntHolder intholder = (new IntHolderHolder()).value;
            StringHolder stringholder4 = (new StringHolderHolder()).value;
            String msisdn = InterfaceUtil.getFilterMSISDN((String) p_requestMap.get("INTERFACE_ID"), (String) p_requestMap.get("MSISDN"));
            _ppAccountFactory.rechargeAndSetExpiry(msisdn, sendAmount, "", "", "", "", byteholder1, intholder, stringholder4);
            StringBuffer respBuff = new StringBuffer();
            respBuff.append("&statusInd=");
            respBuff.append(byteholder1.value);
            respBuff.append("&balance=");
            respBuff.append(intholder.value);
            responseStr = respBuff.toString();

            HashMap responseMap = new HashMap();
            InterfaceUtil.populateStringToHash(responseMap, respBuff.toString(), "&", "=");
            if (NokiaI.RESULT_OK.equals((String) responseMap.get("statusInd"))) {
                try {
                    if ("Y".equals(balanceQuery))
                        responseStr = accountInfoRequest(p_requestMap);
                    if (InterfaceUtil.isNullString(responseStr)) {
                        respBuff.append("&PBLENQ=N");
                        responseStr = respBuff.toString();
                    }
                } catch (Exception e) {
                    // Handle the exception
                    // Confirm to handle the event
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "NokiaClient[credit]", "", msisdn, (String) p_requestMap.get("NETWORK_CODE"), "Not able to fire the Account Info Request after Credit for balance:" + e.getMessage());
                    respBuff.append("&PBLENQ=N");
                    responseStr = respBuff.toString();
                }
            }
        } catch (Exception exception) {
            _log.error("creditRequest", "Exception :: " + exception.getMessage());
            exception.printStackTrace();
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NOT_PROCESS_REQ);

        }
        return responseStr;
    }// end of creditRequest

    private String debitRequest(HashMap p_requestMap) throws Exception // List
                                                                       // all(MAIN)
                                                                       // the
                                                                       // excpetion
                                                                       // thrown
                                                                       // by
                                                                       // CORBA
    {
        if (_log.isDebugEnabled())
            _log.debug("debitRequest", "Entered:p_requestMap:" + p_requestMap);

        String responseStr = null;
        try {
            String transferAmount = (String) p_requestMap.get("transfer_amount");
            sendAmount = Integer.parseInt(transferAmount);
            String balanceQuery = (String) p_requestMap.get("balanceQuery");
            ByteHolder byteholder = (new ByteHolderHolder()).value;
            StringHolder stringholder = (new StringHolderHolder()).value;
            StringHolder stringholder1 = (new StringHolderHolder()).value;
            StringHolder stringholder2 = (new StringHolderHolder()).value;
            StringHolder stringholder3 = (new StringHolderHolder()).value;
            ByteHolder byteholder1 = (new ByteHolderHolder()).value;
            IntHolder intholder = (new IntHolderHolder()).value;
            StringHolder stringholder4 = (new StringHolderHolder()).value;

            _ppAccountFactory.rechargeAndSetExpiry((String) p_requestMap.get("MSISDN"), -sendAmount, "", "", "", "", byteholder1, intholder, stringholder4);
            StringBuffer respBuff = new StringBuffer();
            respBuff.append("&statusInd=");
            respBuff.append(byteholder1.value);
            respBuff.append("&balance=");
            respBuff.append(intholder.value);
            responseStr = respBuff.toString();
            respBuff = null;
            HashMap responseMap = new HashMap();
            InterfaceUtil.populateStringToHash(responseMap, respBuff.toString(), "&", "=");
            if (NokiaI.RESULT_OK.equals((String) responseMap.get("statusInd"))) {
                try {
                    if ("Y".equals(balanceQuery))
                        responseStr = accountInfoRequest(p_requestMap);
                    if (InterfaceUtil.isNullString(responseStr)) {
                        respBuff.append("&PBLENQ=N");
                        responseStr = respBuff.toString();
                    }
                } catch (Exception e) {
                    // Handle the exception
                    // Confirm to handle the event
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "NokiaClient[credit]", "", (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Not able to fire the Account Info Request after Debit for balance:" + e.getMessage());
                    respBuff.append("&PBLENQ=N");
                    responseStr = respBuff.toString();
                }
            }
        } catch (Exception exception) {
            _log.error("debitRequest", "Exception :: " + exception.getMessage());
            exception.printStackTrace();
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NOT_PROCESS_REQ);
        }
        return responseStr;
    }// end of debitRequest

    public void destroy() {
        if (_log.isDebugEnabled())
            _log.debug("destroy", "Entered");
        try {
            _ppAccountFactory.destroy();
            _orb.shutdown(false);
        } catch (Exception e) {
            _log.error("destroy", "" + e.getMessage());
            _orb.shutdown(false);
            e.printStackTrace();
        }
        try {
            _orb = null;
            _initRef = null;
            _namingContext = null;
            _authServerObj = null;
            _ppAccountFactory = null;
            _accountManager = null;
            _ppSubscriberStatusFactory = null;
            _invokeClientFactory = null;
        } catch (Exception e1) {
            _log.error("destroy", "In Destroy Exception 2::" + e1.getMessage());
            e1.printStackTrace();
        }
    }

    public static void main(String[] args) {
        NokiaClientPP nokiaClientPP = null;
        try {
            ConfigServlet.loadProcessCache(args[0], args[1]);
            FileCache.loadAtStartUp();
            nokiaClientPP = new NokiaClientPP("INTID00014");
            HashMap h = new HashMap();
            h.put("MSISDN", "7829712361");
            h.put("INTERFACE_ID", "INTID00014");
            // h.put("MSISDN","94834893284");
            String resp = nokiaClientPP.sendRequest(h, 0);
            System.out.println("Response of Val=" + resp);
            h.put("transfer_amount", "100");
            h.put("IN_RECON_ID", "0000001");
            h.put("applicationId", "0");

            resp = nokiaClientPP.sendRequest(h, 2);
            System.out.println("Response of Credit=" + resp);
            /*
             * resp=nokiaClientPP.sendRequest(h,0);
             * System.out.println("Response of Val="+resp);
             * /*
             * h.put("transfer_amount","50");
             * 
             * resp=nokiaClientPP.sendRequest(h,1);
             * System.out.println("Response of Debit="+resp);
             */
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Reached here");
            if (nokiaClientPP != null)
                nokiaClientPP.destroy();
            ConfigServlet.destroyProcessCache();
        }
    }
}
