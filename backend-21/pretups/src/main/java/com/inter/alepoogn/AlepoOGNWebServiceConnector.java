/*
 * Created on Aug 08, 2011
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.alepoogn;

import javax.xml.rpc.Stub;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.inter.alepoogn.alepoognstub.AlepoSoapServiceLocator;
import com.inter.alepoogn.alepoognstub.Radius;

/**
 * @author shashank.shukla
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class AlepoOGNWebServiceConnector {
    private Log _log = LogFactory.getLog(AlepoOGNWebServiceConnector.class.getName());
    private Radius _stub = null;
    private static Stub _stubSuper = null;

    public AlepoOGNWebServiceConnector() {
    }

    /**
     * Parametric Constructor of AlepoOGNWebServiceConnector class
     */
    public AlepoOGNWebServiceConnector(String p_serviceAddress, String p_timeoutStr) throws Exception {
        long p_timeout = Long.parseLong(p_timeoutStr);
        try {
            AlepoSoapServiceLocator test = new AlepoSoapServiceLocator();
            _stub = test.getAlepo();
            _stubSuper = (Stub) _stub;
            _stubSuper._setProperty(Stub.ENDPOINT_ADDRESS_PROPERTY, p_serviceAddress);
        } catch (Exception e) {
            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "AlepoOGNWebServiceConnector[AlepoOGNWebServiceConnector]", "", "", "", "Unable to get Client Stub");
            _log.error("AlepoOGNWebServiceConnector", "Unable to get Client Stub");
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
        }
    }

    protected Radius getAlepoOGNClient() {
        return _stub;
    }

}
