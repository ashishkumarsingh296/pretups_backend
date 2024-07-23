package com.inter.vastrixtg;

import java.rmi.Remote;

import org.apache.axis.client.Stub;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.vastrixtg.vastrixtgstub.SMSServiceHttpBindingStub;
import com.btsl.pretups.inter.vastrixtg.vastrixtgstub.SMSServicePortType;
import com.btsl.pretups.inter.vastrixtg.vastrixtgstub.SMSServiceServiceLocator;
import com.inter.vastrixtg.scheduler.NodeVO;

public class VASTGConnector {
    private Log _log = LogFactory.getLog(VASTGConnector.class.getName());

    private Stub _axisPort = null;
    private SMSServicePortType _service = null;
    public static String InterfaceID = null;

    public VASTGConnector() {
    }

    public VASTGConnector(NodeVO p_nodevo, String p_interfaceID) throws Exception {

        if (_log.isDebugEnabled())
            _log.debug("VASTGConnector", " Entered p_nodevo::" + p_nodevo.toString() + " p_interfaceID" + p_interfaceID);
        try {
            // nodevo=p_nodevo;
            InterfaceID = p_interfaceID;
            SMSServiceServiceLocator locator = new SMSServiceServiceLocator();
            Remote remote = (Remote) locator.getPort(SMSServicePortType.class);
            _axisPort = (SMSServiceHttpBindingStub) remote;
            _axisPort._setProperty(_axisPort.ENDPOINT_ADDRESS_PROPERTY, p_nodevo.getUrl());
            // _axisPort.setTimeout(p_nodevo.getReadTimeOut());
            // _axisPort._setProperty(WSHandlerConstants.ACTION,WSHandlerConstants.USERNAME_TOKEN);
            // _axisPort._setProperty(UsernameToken.PASSWORD_TYPE,WSConstants.PASSWORD_TEXT);
            // _axisPort._setProperty(WSHandlerConstants.USER,
            // p_nodevo.getUserName());
            // _axisPort._createCall().setSOAPActionURI(p_nodevo.getSoapUri());
            // _axisPort._setProperty(WSHandlerConstants.PW_CALLBACK_CLASS,p_nodevo.getPwbackCall());
            _service = (SMSServiceHttpBindingStub) _axisPort;
        } catch (Exception e) {
            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VASTGConnector[VASTGConnector]", "", "", "", "Unable to get Client Stub");
            _log.error("VASTGConnector", "Unable to get Client Stub");
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("VASTGConnector", " Exited _service " + _service);
        }

    }

    public Stub getAxisPort() {
        return _axisPort;
    }

    public void setAxisPort(Stub axisPort) {
        _axisPort = axisPort;
    }

    public SMSServicePortType getService() {
        return _service;
    }

    public void setService(SMSServicePortType service) {
        _service = service;
    }

}
