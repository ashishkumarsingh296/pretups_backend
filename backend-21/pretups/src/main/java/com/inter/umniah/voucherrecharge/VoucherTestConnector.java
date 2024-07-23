package com.inter.umniah.voucherrecharge;

import java.rmi.Remote;
import java.util.HashMap;

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
import com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.CardRechargeMgrBindingStub;
import com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.CardRechargeMgr;
import com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.CardRechargeMgrServiceLocator;

public class VoucherTestConnector
{
	private Log _log = LogFactory.getLog(VoucherTestConnector.class.getName());
    private Stub _stub=null;
    private CardRechargeMgr _service=null;
    public static String _interfaceID=null;
	private HashMap _requestMap = null;
  

    public VoucherTestConnector(HashMap _requestMap) throws Exception
	{
    	if(_log.isDebugEnabled())_log.debug("VoucherTestConnector"," Entered _requestMap"+_requestMap);
    	try
		{
    		
    		CardRechargeMgrServiceLocator locator = new CardRechargeMgrServiceLocator();
			Remote remote = (Remote) locator.getPort(CardRechargeMgr.class);
			_stub = (CardRechargeMgrBindingStub)remote;
			
			String url=(String)_requestMap.get("URL");;
			String userName=(String)_requestMap.get("USER_NAME");;
			String password=(String)_requestMap.get("PASSWORD");;
			_stub._setProperty(_stub.ENDPOINT_ADDRESS_PROPERTY,url);	
			_stub.setUsername(userName);
			_stub.setPassword(password);
			_stub.setTimeout(Integer.parseInt((String)_requestMap.get("TIME_OUT")));
			_service = (CardRechargeMgrBindingStub)_stub;	
		}		
    	catch(Exception e)
		{
			EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"VoucherTestConnector[VoucherTestConnector]","","","","Unable to get Client Stub");
			_log.error("VoucherTestConnector","Unable to get Client Stub");
			throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
		}
		finally
		{
			if(_log.isDebugEnabled())
				_log.debug("VoucherTestConnector"," Exited _service "+_service);
		}
	}
	
	public Stub getStub() {
		return _stub;
	}
	public void setStub(Stub _stub) {
		_stub = _stub;
	}
	public CardRechargeMgr getService() {
		return _service;
	}
	public void setService(CardRechargeMgr service) {
		_service = service;
	}
}



