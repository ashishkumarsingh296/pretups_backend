package com.inter.claroUserInfoWS;
import org.apache.axis.client.Stub;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.inter.claroUserInfoWS.scheduler.NodeVO;
import com.inter.claroUserInfoWS.stub.StreetSellerWSServiceLocator;
import com.inter.claroUserInfoWS.stub.StreetSellerWSSoap_PortType;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;

public class ClaroCUInfoWSConnectionManager {

	private static Log _log = LogFactory.getLog(ClaroCUInfoWSConnectionManager.class.getName());

	private StreetSellerWSSoap_PortType _stub=null;

	private static Stub _stubSuper=null;

	public ClaroCUInfoWSConnectionManager(NodeVO p_nodevo, String p_interfaceID) throws Exception
	{
		if(_log.isDebugEnabled())_log.debug("ClaroCUInfoWSConnectionManager"," Entered p_nodevo::"+p_nodevo.toString()+" p_interfaceID"+p_interfaceID);
		try
		{
			StreetSellerWSServiceLocator streetSellerWSLocator = new StreetSellerWSServiceLocator();
			_stub=streetSellerWSLocator.getStreetSellerWSSoap(new java.net.URL(p_nodevo.getUrl()));		
			streetSellerWSLocator.setMaintainSession(true);
			_stubSuper =(Stub)_stub;
			_stubSuper.setTimeout(p_nodevo.getReadTimeOut());
		//	_stubSuper._setProperty(Stub.USERNAME_PROPERTY,p_nodevo.getUserName());
			//_stubSuper._setProperty(Stub.PASSWORD_PROPERTY,p_nodevo.getPassword());
			//_stubSuper.setUsername(p_nodevo.getUserName());
			//_stubSuper.setPassword(p_nodevo.getPassword());
			//_stubSuper._setProperty(UsernameToken.PASSWORD_TYPE,WSConstants.PASSWORD_TEXT);
			//_stubSuper._setProperty(WSHandlerConstants.ACTION,WSHandlerConstants.USERNAME_TOKEN);
		}		
		catch(Exception e)
		{
			EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ClaroCUInfoWSConnectionManager[ClaroCUInfoWSConnectionManager]","","","","Unable to get Client Stub");
			_log.error("ClaroCUInfoWSConnectionManager","Unable to get Client Stub");
			throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug("ClaroCUInfoWSConnectionManager"," Exited _service "+_stubSuper);
		}
	}

	protected StreetSellerWSSoap_PortType getService()
	{
		return (StreetSellerWSSoap_PortType) _stubSuper;
	}
}



