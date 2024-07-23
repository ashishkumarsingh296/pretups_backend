package com.inter.zteoml;

import java.net.URL;
import javax.xml.rpc.ServiceException;
import zsmart.ztesoft.com.service.OrangeServiceLocator;
import zsmart.ztesoft.com.service.OrangeServicePortType;
import zsmart.ztesoft.com.service.OrangeServiceSoap11BindingStub;
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

/**
* @(#)ZTEOMLWebServiceConnector.java
* Copyright(c) 2015, Mahindra Comviva Technologies Ltd.
* All Rights Reserved
*-------------------------------------------------------------------------------------------------
*         Author                                Date                             History
*-------------------------------------------------------------------------------------------------
* 		Sanjeew      							June 10, 2015               	Initial Creation
* ------------------------------------------------------------------------------------------------
* This class will established the HTTP connection between IN and IN Module.
*/

public class ZTEOMLWebServiceConnector
{

        private Log _log = LogFactory.getLog(ZTEOMLWebServiceConnector.class.getName());
        private OrangeServiceLocator _service=null;
    	private OrangeServicePortType _port=null;
    	public OrangeServiceSoap11BindingStub  _bindingStub=null;
        public ZTEOMLWebServiceConnector()      {}
        
        public ZTEOMLWebServiceConnector(String p_interfaceID) throws Exception
        {
            String metodeName="ZTEOMLWebServiceConnector[ZTEOMLWebServiceConnector()]";
        	if(_log.isDebugEnabled())
				_log.debug(metodeName," Entered p_interfaceID="+p_interfaceID);
        	try{
        	    
        			_service=new OrangeServiceLocator();
        			String tabsURL=FileCache.getValue(p_interfaceID,"END_POINT");
        			//URL url =new URL("http://172.17.112.35:8088/services/OrangeService.OrangeServiceHttpSoap11Endpoint/");
        			_log.debug(metodeName," Entered tabsURL="+tabsURL);
        			
        			try {
        			    if(_bindingStub==null)
        			    {
                			 URL url =new URL(tabsURL);
                             _port= _service.getOrangeServiceHttpSoap11Endpoint(url);
                             _bindingStub=(OrangeServiceSoap11BindingStub)_port;
        			    }
        			} catch (ServiceException e) {
        				_log.errorTrace(metodeName, e);
        			}
        	}
        			catch(Exception e)
        			{
        				EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,metodeName,"","","","Unable to get Client Stub");
        				_log.error(metodeName,"Unable to get Client Stub");
        				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
        			}
        			finally
        			{
        				if(_log.isDebugEnabled())
        					_log.debug(metodeName," Exited _service="+_service);
        			}
        }
        
        public OrangeServiceSoap11BindingStub getStubConnection() throws BTSLBaseException
		{	
			if(_bindingStub==null){
				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
			}
			
			return _bindingStub;
		}
        public void closeStubConnection()
        {
            String metodeName="ZTEOMLWebServiceConnector[closeStubConnection()]";
            if(_log.isDebugEnabled())
                _log.debug(metodeName," Entered _service="+_service+", _port="+_port+", _bindingStub="+_bindingStub);
            try{
                _service=null;
                _port=null;
                if(_bindingStub!=null)
                {
                    _bindingStub.clearAttachments();
                    _bindingStub.clearHeaders();
                    _bindingStub=null;
                }
            }catch(Exception e){}
            if(_log.isDebugEnabled())
                _log.debug(metodeName," Exit _service="+_service+", _port="+_port+", _bindingStub="+_bindingStub);
        }
}
