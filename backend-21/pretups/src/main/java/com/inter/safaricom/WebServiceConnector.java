/*
 * Created on Jun 17, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.safaricom;

import javax.xml.rpc.Stub;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.inter.safaricom.safaricomstub.MediatorService_Impl;
import com.inter.safaricom.safaricomstub.Mediator_Stub;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
/**
 * @author dhiraj.tiwari
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WebServiceConnector
{
	private Log _log = LogFactory.getLog(WebServiceConnector.class.getName());
	private Mediator_Stub _mediatorStub = null;
	private Stub _stub=null;
	public WebServiceConnector() {
			
	}
	
	public WebServiceConnector(String p_serviceUrl) throws Exception
	{
		try
		{
			_stub = (Stub) (new MediatorService_Impl().getMediator());
			_stub._setProperty(Stub.ENDPOINT_ADDRESS_PROPERTY,p_serviceUrl);
			_mediatorStub = (Mediator_Stub) _stub;
		}
		catch(Exception e)
		{
			EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"SafcomINHandler[sendRequestToIN]","","","","Unable to get Client Object");
			_log.error("sendRequestToIN","Unable to get Client Object");
			throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
		}
	}

	public Mediator_Stub getClient()
	{
		return _mediatorStub;
	}
	public static void main(String[] args) {
	}
}
