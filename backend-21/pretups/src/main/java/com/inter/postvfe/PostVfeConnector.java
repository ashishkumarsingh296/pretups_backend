package com.inter.postvfe;

import javax.xml.rpc.Stub;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.inter.postvfe.postvfestub.CMSInvoke;
import com.inter.postvfe.postvfestub.CMSInvokeServiceLocator;

public class PostVfeConnector {
    private static Log _log = LogFactory.getLog(PostVfeConnector.class.getName());
    private CMSInvoke _stub = null;
    private static Stub _stubSuper = null;

    public PostVfeConnector() {
    }

    /**
	 * 
	 */
    public PostVfeConnector(String p_serviceAddress, String p_timeoutStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("PostVfeConnector", "Entered:: p_serviceAddress::=" + p_serviceAddress + " p_timeoutStr::" + p_timeoutStr);
        System.out.println("@@@@" + p_serviceAddress);
        try {
            CMSInvokeServiceLocator test = new CMSInvokeServiceLocator();
            _stub = test.getCMSInvoke();
            _stubSuper = (Stub) _stub;
            _stubSuper._setProperty(Stub.ENDPOINT_ADDRESS_PROPERTY, p_serviceAddress);
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeConnector[PostVfeConnector]", "", "", "", "Unable to get Client Stub");
            _log.error("PostVfeConnector", "Unable to get Client Stub");
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
        }
    }

    protected CMSInvoke getPostVfeClient() {
        return _stub;
    }
}
