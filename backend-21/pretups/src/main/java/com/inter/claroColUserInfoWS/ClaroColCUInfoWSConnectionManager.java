package com.inter.claroColUserInfoWS;

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
import com.inter.claroColUserInfoWS.stub.WSConsultaSAPServiceLocator;
import com.inter.claroColUserInfoWS.stub.WSConsultaSAP;
import com.inter.claroColUserInfoWS.stub.WSConsultaSAPServiceLocator;
import com.inter.claroColUserInfoWS.scheduler.*;

/**
 * @(#)ClaroColCUInfoWSConnectionManager
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
 *                 This servlet is responsible for create connection.  
 */
public class ClaroColCUInfoWSConnectionManager {

    private static Log log = LogFactory.getLog(ClaroColCUInfoWSConnectionManager.class.getName());

    private WSConsultaSAP stub = null;

    private static Stub stubSuper = null;

    public ClaroColCUInfoWSConnectionManager(NodeVO pNodevo, String pInterfaceID) throws Exception {
    	final String constructName="ClaroColCUInfoWSConnectionManager";
        if (log.isDebugEnabled())
            log.debug(constructName, " Entered pNodevo::" + pNodevo.toString() + " pInterfaceID" + pInterfaceID);
        try {
        	WSConsultaSAPServiceLocator wsConsultasSAPServiceLocator = new WSConsultaSAPServiceLocator();
            stub = wsConsultasSAPServiceLocator.getWSConsultaSAPPort(new java.net.URL(pNodevo.getUrl()));
            wsConsultasSAPServiceLocator.setMaintainSession(true);
            stubSuper = (Stub) stub;
            stubSuper.setTimeout(pNodevo.getReadTimeOut());
        } catch (Exception e) {
            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, constructName, "", "", "", "Unable to get Client Stub");
            log.errorTrace("BTSLBaseException be::" , e);
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
        } finally {
            if (log.isDebugEnabled())
                log.debug(constructName, " Exited _service " + stubSuper);
        }
    }
    
    protected WSConsultaSAP getService() {
        return (WSConsultaSAP) stubSuper;
    }
}
