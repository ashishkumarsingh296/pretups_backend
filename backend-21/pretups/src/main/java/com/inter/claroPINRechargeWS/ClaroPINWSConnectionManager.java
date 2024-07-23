package com.inter.claroPINRechargeWS;
import org.apache.axis.client.Stub;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.inter.claroPINRechargeWS.scheduler.NodeVO;
import com.inter.claroPINRechargeWS.stub.EbsPinVirtual;
import com.inter.claroPINRechargeWS.stub.EbsPinVirtualServiceLocator;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;

public class ClaroPINWSConnectionManager {

	private static Log _log = LogFactory.getLog(ClaroPINWSConnectionManager.class.getName());

	private EbsPinVirtual _stub=null;

	private static Stub _stubSuper=null;

	public ClaroPINWSConnectionManager(NodeVO p_nodevo, String p_interfaceID) throws Exception
	{
		if(_log.isDebugEnabled())_log.debug("ClaroPINWSConnectionManager"," Entered p_nodevo::"+p_nodevo.toString()+" p_interfaceID"+p_interfaceID);
		try
		{
			EbsPinVirtualServiceLocator ebsPinVirtualServiceLocator = new EbsPinVirtualServiceLocator();
			_stub=ebsPinVirtualServiceLocator.getEbsPinVirtualSoapPort(new java.net.URL(p_nodevo.getUrl()));		
			ebsPinVirtualServiceLocator.setMaintainSession(true);
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
			EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ClaroPINWSConnectionManager[ClaroPINWSConnectionManager]","","","","Unable to get Client Stub");
			_log.error("ClaroPINWSConnectionManager","Unable to get Client Stub");
			throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug("ClaroPINWSConnectionManager"," Exited _service "+_stubSuper);
		}
	}

	protected EbsPinVirtual getService()
	{
		return (EbsPinVirtual) _stubSuper;
	}
}



