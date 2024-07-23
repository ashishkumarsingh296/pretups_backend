package com.inter.righttel.paymentgateway;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.axis.client.Stub;

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
import com.btsl.util.BTSLUtil;
import com.inter.righttel.paymentgateway.scheduler.NodeVO;
import com.inter.righttel.paymentgateway.stub.PaymentIFBindingLocator;
import com.inter.righttel.paymentgateway.stub.PaymentIFBindingSoap_PortType;

public class PaymentGatewayUrlConnection {

	private static Log _log = LogFactory.getLog(PaymentGatewayUrlConnection.class.getName());
	private PaymentIFBindingSoap_PortType _stub=null;
	private static Stub _stubSuper=null;
	/**
	 * @author vipan.kumar
	 * @date 17 Oct 2013
	 */
	public PaymentGatewayUrlConnection(NodeVO p_nodevo, String p_interfaceID, String p_txnid)throws Exception
	{

		if(_log.isDebugEnabled())_log.debug("CRMWebServiceConnectionManager"," Entered p_nodevo::"+p_nodevo.toString()+" p_interfaceID"+p_interfaceID);


		String https="";
		if(!BTSLUtil.isNullString(FileCache.getValue(p_interfaceID,"HTTPS_ALLOWED"))){
			https=FileCache.getValue(p_interfaceID,"HTTPS_ALLOWED");
		}else{
			https="false";
		}

		if((https.equalsIgnoreCase("false"))){
			try
			{

				PaymentIFBindingLocator topupServiceLocator = new PaymentIFBindingLocator();
				_stub=topupServiceLocator.getPaymentIFBindingSoap(new java.net.URL(p_nodevo.getUrl()));		
				_stubSuper =(Stub)_stub;
				//_stubSuper.setHeader(soapHeader);
				_stubSuper.setTimeout(p_nodevo.getConnectionTimeOut());
			}		
			catch(Exception e)
			{
				EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"CRMWebServiceConnectionManager[CRMWebServiceConnectionManager]","","","","Unable to get Client Stub");
				_log.error("CRMWebServiceConnectionManager","Unable to get Client Stub");
				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
			}
			finally
			{
				if(_log.isDebugEnabled())_log.debug("CRMWebServiceConnectionManager"," Exited _service "+_stubSuper);
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

				PaymentIFBindingLocator topupServiceLocator = new PaymentIFBindingLocator();
				String url=p_nodevo.getUrl();
				url = url.replace("http:", "https:");
				_stub=topupServiceLocator.getPaymentIFBindingSoap(new java.net.URL(p_nodevo.getUrl()));
				_stubSuper =(Stub)_stub;
				_stubSuper.setTimeout(p_nodevo.getConnectionTimeOut());

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
	protected PaymentIFBindingSoap_PortType getService()
	{
		return (PaymentIFBindingSoap_PortType) _stubSuper;
	}

}
