package com.inter.claro.vas;

import org.apache.axis.client.Stub;
import java.rmi.Remote;
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
import com.inter.claro.vas.stub.EjecutarTramaSOAPStub;
import com.inter.claro.vas.stub.EjecutarTrama_PortType;
import com.inter.claro.vas.stub.EjecutarTrama_ServiceLocator;
/**
 * @(#)VASClaroConnector
 *                Copyright(c) 2016, Comviva Technologies Ltd.
 * 				  All Rights Reserved
 *                 ------------------------------------------------------------
 *                 -------------------------------------
 *                 Author Date History
 *                 ------------------------------------------------------------
 *                 -------------------------------------
 *                 Pankaj Sharma Spt 28,2016 Initial Creation
 *                 ------------------------------------------------------------
 *                 ------------------------------------
 */
public class VASClaroConnector {
    private Log log = LogFactory.getLog(VASClaroConnector.class.getName());

    private Stub axisPort = null;
    private EjecutarTrama_PortType service = null;
    static String interfaceID = null;

    public VASClaroConnector() {
    	//Auto-generated
    }

    public VASClaroConnector(String pInterfaceID) throws BTSLBaseException {
    	final String methodName="VASClaroConnector";
        if (log.isDebugEnabled())
            log.debug("VASClaroConnector p_interfaceID", pInterfaceID+"url"+ FileCache.getValue(pInterfaceID,"URL_1"));
        try {
        	interfaceID = pInterfaceID;
            EjecutarTrama_ServiceLocator locator = new EjecutarTrama_ServiceLocator();
            Remote remote =  locator.getPort(EjecutarTrama_PortType.class);
            axisPort = (EjecutarTramaSOAPStub) remote;
            axisPort._setProperty(Stub.ENDPOINT_ADDRESS_PROPERTY, FileCache.getValue(pInterfaceID,"URL_1"));
            service = (EjecutarTramaSOAPStub) axisPort;
        } catch (Exception e) {
            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VASClaroConnector[VASClaroConnector]", "", "", "", "Unable to get Client Stub");
            log.error(methodName, "Unable to get Client Stub");
            log.errorTrace("Exception in method :: "+methodName,e);
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
        } finally {
            if (log.isDebugEnabled())
                log.debug(methodName, " Exited service " + service);
        }

    }

    public Stub getAxisPort() {
        return axisPort;
    }

    public void setAxisPort(Stub axisPort) {
        this.axisPort = axisPort;
    }

    public EjecutarTrama_PortType getService() {
        return service;
    }

    public void setService(EjecutarTrama_PortType service) {
        this.service = service;
    }

}
