package com.inter.claroCollectionEnqWS;
import org.apache.axis.client.Stub;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.inter.claroCollectionEnqWS.scheduler.NodeVO;
import com.inter.claroCollectionEnqWS.stub.ConsultaPagos_PortType;
import com.inter.claroCollectionEnqWS.stub.ConsultaPagos_ServiceLocator;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;

public class ClaroCollEnqWSConnectionManager {

	private static Log _log = LogFactory.getLog(ClaroCollEnqWSConnectionManager.class.getName());

	private ConsultaPagos_PortType _stub=null;

	private static Stub _stubSuper=null;

	public ClaroCollEnqWSConnectionManager(NodeVO p_nodevo, String p_interfaceID) throws Exception
	{
		if(_log.isDebugEnabled())_log.debug("ClaroCollEnqWSConnectionManager"," Entered p_nodevo::"+p_nodevo.toString()+" p_interfaceID"+p_interfaceID);
		try
		{
			ConsultaPagos_ServiceLocator consultaPagos_ServiceLocator = new ConsultaPagos_ServiceLocator();
			_stub=consultaPagos_ServiceLocator.getConsultaPagosSOAP(new java.net.URL(p_nodevo.getUrl()));		
			consultaPagos_ServiceLocator.setMaintainSession(true);
			_stubSuper =(Stub)_stub;
			_stubSuper.setTimeout(p_nodevo.getReadTimeOut());
			//_stubSuper._setProperty(Stub.USERNAME_PROPERTY,p_nodevo.getUserName());
			//_stubSuper._setProperty(Stub.PASSWORD_PROPERTY,p_nodevo.getPassword());
			//_stubSuper.setUsername(p_nodevo.getUserName());
			//_stubSuper.setPassword(p_nodevo.getPassword());
			//_stubSuper._setProperty(UsernameToken.PASSWORD_TYPE,WSConstants.PASSWORD_TEXT);
			//_stubSuper._setProperty(WSHandlerConstants.ACTION,WSHandlerConstants.USERNAME_TOKEN);
		}		
		catch(Exception e)
		{
			EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ClaroCollEnqWSConnectionManager[ClaroCollEnqWSConnectionManager]","","","","Unable to get Client Stub");
			_log.error("ClaroCollEnqWSConnectionManager","Unable to get Client Stub");
			throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug("ClaroCollEnqWSConnectionManager"," Exited _service "+_stubSuper);
		}
	}

	protected ConsultaPagos_PortType getService()
	{
		return (ConsultaPagos_PortType) _stubSuper;
	}
}



