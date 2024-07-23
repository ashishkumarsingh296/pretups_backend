package com.inter.oloapi;
import org.apache.axis.client.Stub;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.inter.oloapi.scheduler.NodeVO;
import com.inter.oloapi.stub.PrepayPortType;
import com.inter.oloapi.stub.PrepayLocator;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import com.btsl.util.Constants;

public class OLORechargeConnectionManager {

	private static Log _log = LogFactory.getLog(OLORechargeConnectionManager.class.getName());
	private PrepayPortType _stub=null;
	private static Stub _stubSuper=null;
	/**
	 * @author vipan.kumar
	 * @date 17 Oct 2013
	 */
	public OLORechargeConnectionManager(NodeVO p_nodevo, String p_interfaceID)throws Exception
	{

		if(_log.isDebugEnabled())_log.debug("OLORechargeConnectionManager"," Entered p_nodevo::"+p_nodevo.toString()+" p_interfaceID"+p_interfaceID);
		
		if((Constants.getProperty("HTTP_CONNECTION")).equals("TRUE")){
			try
			{				
				PrepayLocator ebsRecargaVirtualServiceLocator = new PrepayLocator();
				_stub=ebsRecargaVirtualServiceLocator.getprepayPort(new java.net.URL(p_nodevo.getUrl()));		
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
				EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"OLORechargeConnectionManager[OLORechargeConnectionManager]","","","","Unable to get Client Stub");
				_log.error("OLORechargeConnectionManager","Unable to get Client Stub");
				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
			}
			finally
			{
				if(_log.isDebugEnabled())_log.debug("OLORechargeConnectionManager"," Exited _service "+_stubSuper);
			}
		}
		else{
			try
			{
				

				TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() { 
					public java.security.cert.X509Certificate[] getAcceptedIssuers() { 
						return null; 
					} 

					public void checkClientTrusted( 
							java.security.cert.X509Certificate[] certs, String authType) { 
					} 

					public void checkServerTrusted( 
							java.security.cert.X509Certificate[] certs, String authType) { 
						for (int i = 0; i < certs.length; i++) { 
							if(_log.isDebugEnabled())_log.debug("connectGateway"," [Issuer Principal]"+certs[i].getIssuerX500Principal().getName() +"[Issuer DN]"+certs[i].getIssuerDN().getName());
						} 
					} 
				} }; 

				try { 
					SSLContext sc = SSLContext.getInstance("SSL"); 
					sc.init(null, trustAllCerts, new java.security.SecureRandom()); 
					HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory()); 
				} catch (Exception e) { 
					_log.error("OLORechargeConnectionManager", e);
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
				} 

				HostnameVerifier hv = new HostnameVerifier()
				{	        
					public boolean verify(String urlHostName, SSLSession session) {
						if(_log.isDebugEnabled())_log.debug("connectGateway()"," [URL Host]"+urlHostName +"[vs.]"+session.getPeerHost());
						return true;
					}
				};

					HttpsURLConnection.setDefaultHostnameVerifier(hv);
				
				PrepayLocator ebsRecargaVirtualServiceLocator = new PrepayLocator();
				_stub=ebsRecargaVirtualServiceLocator.getprepayPort(new java.net.URL(p_nodevo.getUrl()));		
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
				EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"OLORechargeConnectionManager[OLORechargeConnectionManager]","","","","Unable to get Client Stub");
				_log.error("OLORechargeConnectionManager","Unable to get Client Stub");
				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
			}
			finally
			{
				if(_log.isDebugEnabled())_log.debug("OLORechargeConnectionManager"," Exited _service "+_stubSuper);
			}		
		}
	}
	protected PrepayPortType getService()
	{
		return (PrepayPortType) _stubSuper;
	}

}
