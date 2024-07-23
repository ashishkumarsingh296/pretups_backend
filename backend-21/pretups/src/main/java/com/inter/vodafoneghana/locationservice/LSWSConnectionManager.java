package com.inter.vodafoneghana.locationservice;

import gh.com.vodafone.locationsvc.svcintfc.LocationService;
import gh.com.vodafone.locationsvc.svcintfc.LocationService_Service;

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
import com.inter.vodafoneghana.locationservice.scheduler.NodeVO;

public class LSWSConnectionManager {

    private static Log _log = LogFactory.getLog(LSWSConnectionManager.class.getName());

    private LocationService_Service _stub = null;

    private static Stub _stubSuper = null;

    public LSWSConnectionManager(NodeVO p_nodevo, String p_interfaceID) throws Exception {
        final String methodName = "LSWSConnectionManager[LSWSConnectionManager]";

        if (_log.isDebugEnabled())
            _log.debug(methodName, " Entered p_nodevo::" + p_nodevo.toString() + " p_interfaceID" + p_interfaceID);
        try {
            _stub = new LocationService_Service(new java.net.URL(p_nodevo.getUrl()));

        } catch (Exception e) {
            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "", "", "", "Unable to get Client Stub");
            _log.error(methodName, "Unable to get Client Stub");
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug(methodName, " Exited _service " + _stubSuper);
        }
    }

    protected LocationService getService() {
        return _stub.getLocationServicePort();
    }
}
