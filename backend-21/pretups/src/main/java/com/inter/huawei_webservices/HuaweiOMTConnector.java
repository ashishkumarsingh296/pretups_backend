package com.inter.huawei_webservices;

import java.io.IOException;
import java.rmi.Remote;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.Stub;
import org.apache.axis.configuration.FileProvider;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSPasswordCallback;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.apache.ws.security.message.token.UsernameToken;
import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.huawei_webservices.omt_huawei.CBSInterfaceAccountMgr;
import com.btsl.pretups.inter.huawei_webservices.omt_huawei.CBSInterfaceAccountMgrBindingStub;
import com.btsl.pretups.inter.huawei_webservices.omt_huawei.CBSInterfaceAccountMgrServiceLocator;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.util.BTSLUtil;

public class HuaweiOMTConnector implements CallbackHandler {
    private Log _log = LogFactory.getLog(HuaweiOMTConnector.class.getName());

    private Stub _axisPort = null;
    private CBSInterfaceAccountMgr _service = null;
    public static String InterfaceID = null;

    public HuaweiOMTConnector() {
    }

    public HuaweiOMTConnector(String p_interfaceID) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("HuaweiOMTConnector", " Entered p_interfaceID=" + p_interfaceID);
        try {
            InterfaceID = p_interfaceID;
            EngineConfiguration config = new FileProvider(FileCache.getValue(p_interfaceID, "WSDD_LOCATION"));
            CBSInterfaceAccountMgrServiceLocator locator = new CBSInterfaceAccountMgrServiceLocator(config);
            Remote remote = (Remote) locator.getPort(CBSInterfaceAccountMgr.class);
            _axisPort = (CBSInterfaceAccountMgrBindingStub) remote;
            _axisPort._setProperty(_axisPort.ENDPOINT_ADDRESS_PROPERTY, FileCache.getValue(p_interfaceID, "END_URL"));
            _axisPort.setTimeout(Integer.parseInt(FileCache.getValue(p_interfaceID, "READ_TIME_OUT")));
            _axisPort._setProperty(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
            _axisPort._setProperty(UsernameToken.PASSWORD_TYPE, WSConstants.PASSWORD_TEXT);
            _axisPort._setProperty(WSHandlerConstants.USER, FileCache.getValue(p_interfaceID, "USER_NAME"));
            _axisPort._createCall().setSOAPActionURI(FileCache.getValue(p_interfaceID, "SOAP_ACTION_URI"));
            _axisPort._setProperty(WSHandlerConstants.PW_CALLBACK_CLASS, FileCache.getValue(p_interfaceID, "PW_CALLBACK"));
            _service = (CBSInterfaceAccountMgrBindingStub) _axisPort;
        } catch (Exception e) {
            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiOMTConnector[HuaweiOMTConnector]", "", "", "", "Unable to get Client Stub");
            _log.error("HuaweiOMTConnector", "Unable to get Client Stub");
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("HuaweiOMTConnector", " Exited _service=" + _service);
        }
    }

    /**
     * @seejavax.security.auth.callback.CallbackHandler#handle(javax.security.auth.callback.Callback[])
     */
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        String username = "";
        String node = "";
        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof WSPasswordCallback) {
                WSPasswordCallback pc = (WSPasswordCallback) callbacks[i];
                // set the password given a username
                username = pc.getIdentifer().substring(0, pc.getIdentifer().indexOf('_'));
                node = pc.getIdentifer().substring(pc.getIdentifer().indexOf('_') + 1);
                pc.setIdentifier(username);
                pc.setPassword(BTSLUtil.decryptText(FileCache.getValue(InterfaceID, "PASSWORD_" + node)));
            } else
                throw new UnsupportedCallbackException(callbacks[i], "Unrecognized Callback");
        }
    }

    public Stub getAxisPort() {
        return _axisPort;
    }

    public void setAxisPort(Stub axisPort) {
        _axisPort = axisPort;
    }

    public CBSInterfaceAccountMgr getService() {
        return _service;
    }

    public void setService(CBSInterfaceAccountMgr service) {
        _service = service;
    }
}
