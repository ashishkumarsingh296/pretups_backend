package com.inter.claroPromoWS;
import org.apache.axis.client.Stub;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.inter.claroPromoWS.scheduler.NodeVO;
import com.inter.claroPromoWS.stub.EbsEntregaPromocionLocator;
import com.inter.claroPromoWS.stub.EbsEntregaPromocionPortType;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;

public class ClaroPromoWSConnectionManager {

	private static Log _log = LogFactory.getLog(ClaroPromoWSConnectionManager.class.getName());

	private EbsEntregaPromocionPortType _stub=null;

	private static Stub _stubSuper=null;

	public ClaroPromoWSConnectionManager(NodeVO p_nodevo, String p_interfaceID) throws Exception
	{
		if(_log.isDebugEnabled())_log.debug("ClaroPromoWSConnectionManager"," Entered p_nodevo::"+p_nodevo.toString()+" p_interfaceID"+p_interfaceID);
		try
		{
			EbsEntregaPromocionLocator ebsEntregaPromocionesPrePostServiceLocator = new EbsEntregaPromocionLocator();
			_stub=ebsEntregaPromocionesPrePostServiceLocator.getebsEntregaPromocionSB11(new java.net.URL(p_nodevo.getUrl()));		
			ebsEntregaPromocionesPrePostServiceLocator.setMaintainSession(true);
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
			EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ClaroPromoWSConnectionManager[ClaroPromoWSConnectionManager]","","","","Unable to get Client Stub");
			_log.error("ClaroPromoWSConnectionManager","Unable to get Client Stub");
			throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug("ClaroPromoWSConnectionManager"," Exited _service "+_stubSuper);
		}
	}

	protected EbsEntregaPromocionPortType getService()
	{
		return (EbsEntregaPromocionPortType) _stubSuper;
	}
}



