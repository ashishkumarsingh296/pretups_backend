package com.client.pretups.userinfo.aup.requesthandler;

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
import com.inter.ClaroAUPUserInfo.StealthIntegration_PortType;
import com.inter.ClaroAUPUserInfo.StealthIntegration_ServiceLocator;
import com.inter.claroColUserInfoWS.scheduler.NodeVO;

/**
 * @(#)ClaroColCUInfoWSConnectionManager
 *                Copyright(c) 2016, Comviva TechnoLOGies Ltd.
 *                   All Rights Reserved
 *                 ------------------------------------------------------------
 *                 -------------------------------------
 *                 Author Date History
 *                 ------------------------------------------------------------
 *                 -------------------------------------
 *                 Pankaj Sharma Spt 28,2016 Initial Creation
 *                 ------------------------------------------------------------
 *                 ------------------------------------
 *                 This servlet is responsible for create connection.  
 */
public class ClaroAUPCUInfoWSConnectionManager {

    private static final Log LOG = LogFactory.getLog(ClaroAUPCUInfoWSConnectionManager.class.getName());

    private StealthIntegration_PortType stub = null;

    private static Stub stubSuper = null;

    /**
     * Method ClaroAUPCUInfoWSConnectionManager
     * Constructor of the class
     * 
     * @param pNodevo
     * @param pInterfaceID
     */
    public ClaroAUPCUInfoWSConnectionManager(NodeVO pNodevo, String pInterfaceID) throws Exception {
        final String constructName="ClaroColCUInfoWSConnectionManager";
        LogFactory.printLog(constructName, " Entered pNodevo::" + pNodevo.toString() + " pInterfaceID" + pInterfaceID, LOG);
        try {
            StealthIntegration_ServiceLocator wsConsultasSAPServiceLocator = new StealthIntegration_ServiceLocator();
            stub = wsConsultasSAPServiceLocator.getStealthIntegrationPort(new java.net.URL(pNodevo.getUrl()));
            wsConsultasSAPServiceLocator.setMaintainSession(true);
            stubSuper = (Stub) stub;
            stubSuper.setTimeout(pNodevo.getReadTimeOut());
        } catch (Exception e) {
            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, 
            		constructName, "", "", "", "Unable to get Client Stub");
            LOG.errorTrace("BTSLBaseException be::" , e);
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
        } finally {
            LogFactory.printLog(constructName, " Exited _service " + stubSuper, LOG);
        }
    }
    
    protected StealthIntegration_PortType getService() {
        return (StealthIntegration_PortType) stubSuper;
    }
}
