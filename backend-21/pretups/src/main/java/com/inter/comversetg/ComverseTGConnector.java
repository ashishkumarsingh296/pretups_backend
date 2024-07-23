package com.inter.comversetg;

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
import com.btsl.pretups.inter.comversetg.comversetgstub.ServiceLocator;
import com.btsl.pretups.inter.comversetg.comversetgstub.ServiceSoap_PortType;
import com.btsl.pretups.inter.comversetg.comversetgstub.ServiceSoap_BindingStub;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.util.BTSLUtil;
import com.inter.comversetg.scheduler.NodeVO;

/**
 * @author shamit.jain
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class ComverseTGConnector implements CallbackHandler {
    private Log _log = LogFactory.getLog(ComverseTGConnector.class.getName());

    private Stub _axisPort = null;
    private ServiceSoap_PortType _service = null;
    public static String InterfaceID = null;

    public ComverseTGConnector() {
    }

    public ComverseTGConnector(NodeVO p_nodevo, String p_interfaceID) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("ComverseTGConnector", " Entered p_nodevo::" + p_nodevo.toString() + " p_interfaceID" + p_interfaceID);
        try {
            // nodevo=p_nodevo;
            InterfaceID = p_interfaceID;
            EngineConfiguration config = new FileProvider(p_nodevo.getWssdFileLoc());
            ServiceLocator locator = new ServiceLocator(config);
            Remote remote = (Remote) locator.getPort(ServiceSoap_PortType.class);
            _axisPort = (ServiceSoap_BindingStub) remote;
            _axisPort._setProperty(_axisPort.ENDPOINT_ADDRESS_PROPERTY, p_nodevo.getUrl());
            _axisPort.setTimeout(p_nodevo.getReadTimeOut());
            _axisPort._setProperty(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
            _axisPort._setProperty(UsernameToken.PASSWORD_TYPE, WSConstants.PASSWORD_TEXT);
            _axisPort._setProperty(WSHandlerConstants.USER, p_nodevo.getUserName());
            _axisPort._createCall().setSOAPActionURI(p_nodevo.getSoapUri());
            _axisPort._setProperty(WSHandlerConstants.PW_CALLBACK_CLASS, p_nodevo.getPwbackCall());
            _service = (ServiceSoap_BindingStub) _axisPort;
        } catch (Exception e) {
            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTGConnector[ComverseTGConnector]", "", "", "", "Unable to get Client Stub");
            _log.error("ComverseTGConnector", "Unable to get Client Stub");
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("ComverseTGConnector", " Exited _service " + _service);
        }
    }

    /***
     * @seejavax.security.auth.callback.CallbackHandler#handle(javax.securi
     *                                                                      ty.
     *                                                                      auth
     *                                                                      .
     *                                                                      callback
     *                                                                      .
     *                                                                      Callback
     *                                                                      [])
     */
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {

        String username = "";
        String node = "";
        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof WSPasswordCallback) {
                WSPasswordCallback pc = (WSPasswordCallback) callbacks[i];
                // set the password given a username
                System.out.println("CHECK ##");
                System.out.println("Interface ID" + InterfaceID);
                username = pc.getIdentifer().substring(0, pc.getIdentifer().indexOf('_'));
                node = pc.getIdentifer().substring(pc.getIdentifer().indexOf('_') + 1);
                System.out.println("CHECK username" + username);
                // pc.setIdentifier(username);
                pc.setIdentifier("COMVIVA_TEST");
                System.out.println("CHECK node" + node + FileCache.getValue(InterfaceID, "PASSWORD_" + node) + BTSLUtil.decryptText(FileCache.getValue(InterfaceID, "PASSWORD_" + node)));

                // pc.setPassword(BTSLUtil.decryptText(FileCache.getValue(InterfaceID,
                // "PASSWORD_"+node)));
                pc.setPassword("Tig0!12345");
                System.out.println("CHECK ###");

            } else {
                throw new UnsupportedCallbackException(callbacks[i], "Unrecognized Callback");
            }
        }
    }

    public Stub getAxisPort() {
        return _axisPort;
    }

    public void setAxisPort(Stub axisPort) {
        _axisPort = axisPort;
    }

    public ServiceSoap_PortType getService() {
        return _service;
    }

    public void setService(ServiceSoap_PortType service) {
        _service = service;
    }
}
