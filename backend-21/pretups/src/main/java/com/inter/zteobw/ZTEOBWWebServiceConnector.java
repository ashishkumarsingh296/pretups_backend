package com.inter.zteobw;
/**
 * @(#)ZTEOMLWebServiceConnector.java
 * Copyright(c) 2015, Comviva Technologies Ltd.
 * All Rights Reserved
 *-------------------------------------------------------------------------------------------------
 *         Author                                Date                             History
 *-------------------------------------------------------------------------------------------------
 * Sanjeew      June 10, 2015               Initial Creation
 * ------------------------------------------------------------------------------------------------
 * This class will stablished the HTTP connection between IN and IN Module.
 */
import java.net.URL;
import javax.xml.rpc.ServiceException;
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

import zsmart.ztesoft.com.service.ObwWebserviceLocator;
import zsmart.ztesoft.com.service.ObwWebservicePortType;
import zsmart.ztesoft.com.service.ObwWebserviceSoap11BindingStub;
import zsmart.ztesoft.com.service.ObwWebserviceSoap12BindingStub;

public class ZTEOBWWebServiceConnector
{

	private Log log = LogFactory.getLog(ZTEOBWWebServiceConnector.class.getName());
	private static ObwWebserviceLocator service=null;
	private static ObwWebservicePortType port=null;
	public static ObwWebserviceSoap11BindingStub  bindingStub=null;
	public ZTEOBWWebServiceConnector()   
	{
		//Constructor
	}

	public ZTEOBWWebServiceConnector(String pInterfaceID) throws BTSLBaseException
	{
		String metodeName="ZTEOMLWebServiceConnector[ZTEOMLWebServiceConnector()]";
		if(log.isDebugEnabled())
			log.debug(metodeName," Entered p_interfaceID="+pInterfaceID);
		try{

			service=new ObwWebserviceLocator();
			String tabsURL=FileCache.getValue(pInterfaceID,"END_POINT");
			log.debug(metodeName," Entered tabsURL="+tabsURL);
			URL url =new URL(tabsURL);
			port= service.getObwWebserviceHttpSoap11Endpoint(url);
			bindingStub=(ObwWebserviceSoap11BindingStub)port;

		}
		catch (ServiceException e) {
			log.errorTrace(metodeName, e);
		}		
		catch(Exception e)
		{
			log.error(metodeName,"Unable to get Client Stub "+e);
			EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,metodeName,"","","","Unable to get Client Stub");
			throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
		}
		finally
		{
			if(log.isDebugEnabled())
				log.debug(metodeName," Exited service="+service);
		}
	}
	public ObwWebserviceSoap11BindingStub getStubConnection() throws BTSLBaseException
	{	
		if(bindingStub==null){
			throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
		}

		return bindingStub;
	}
}

