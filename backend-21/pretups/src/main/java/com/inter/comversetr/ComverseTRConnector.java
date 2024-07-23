package com.inter.comversetr;

import java.rmi.Remote;
import java.util.HashMap;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.Stub;
import org.apache.axis.configuration.FileProvider;
import org.apache.ws.security.WSConstants;
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
import com.btsl.pretups.inter.comversetr.comversetrstub.ServiceLocator;
import com.btsl.pretups.inter.comversetr.comversetrstub.ServiceSoap;
import com.btsl.pretups.inter.comversetr.comversetrstub.ServiceSoapStub;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;

/**
 * @author dhiraj.tiwari
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class ComverseTRConnector {
    private Log _log = LogFactory.getLog(ComverseTRConnector.class.getName());

    private Stub _axisPort = null;
    private ServiceSoap _service = null;

    public ComverseTRConnector() {
    }

    public ComverseTRConnector(HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("ComverseTRConnector", " Entered p_map: " + p_map);
        try {
            EngineConfiguration config = new FileProvider((String) p_map.get("WSDD_LOCATION"));
            ServiceLocator locator = new ServiceLocator(config);
            Remote remote = (Remote) locator.getPort(ServiceSoap.class);
            _axisPort = (ServiceSoapStub) remote;
            _axisPort._setProperty(_axisPort.ENDPOINT_ADDRESS_PROPERTY, (String) p_map.get("END_URL"));
            _axisPort.setTimeout(Integer.parseInt((String) p_map.get("READ_TIME_OUT")));
            _axisPort._setProperty(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
            _axisPort._setProperty(UsernameToken.PASSWORD_TYPE, WSConstants.PASSWORD_TEXT);
            _axisPort._setProperty(WSHandlerConstants.USER, (String) p_map.get("USER_NAME"));
            _axisPort._setProperty(WSHandlerConstants.PW_CALLBACK_CLASS, "com.btsl.pretups.inter.comversetr.PWCallback");
            _axisPort._createCall().setSOAPActionURI((String) p_map.get("SOAP_ACTION_URI"));
            _service = (ServiceSoapStub) _axisPort;
        } catch (Exception e) {
            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTRConnector[ComverseTRConnector]", "", "", "", "Unable to get Client Stub");
            _log.error("ComverseTRConnector", "Unable to get Client Stub");
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("ComverseTRConnector", " Exited p_map: " + p_map + "_service " + _service);
        }
    }

    public Stub getAxisPort() {
        return _axisPort;
    }

    public void setAxisPort(Stub axisPort) {
        _axisPort = axisPort;
    }

    public ServiceSoap getService() {
        return _service;
    }

    public void setService(ServiceSoap service) {
        _service = service;
    }
}
