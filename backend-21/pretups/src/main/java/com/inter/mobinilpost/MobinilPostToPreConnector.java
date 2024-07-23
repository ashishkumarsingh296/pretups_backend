/** MobinilPostpaidINHandler.java
* @(#)
* Copyright(c) 2006, Bharti Telesoft Int. Public Ltd.
* All Rights Reserved
*-------------------------------------------------------------------------------------------------
* Author				Date			History
*-------------------------------------------------------------------------------------------------
* Ranjana Chouhan    Jun 10,2009		Initial Creation
* ------------------------------------------------------------------------------------------------
* Interface class for the PostPaid Online Interface
*/
package com.inter.mobinilpost;

import com.btsl.pretups.inter.mobinilpost.mobinilpoststub.MEAI_OnlineServices_webServices_PostToPreCreditTransferPortType;
import com.btsl.pretups.inter.mobinilpost.mobinilpoststub.MEAI_OnlineServices_webServices_PostToPreCreditTransferPortType_Stub;
import com.btsl.pretups.inter.mobinilpost.mobinilpoststub.MEAI_OnlineServices_webServices_PostToPreCreditTransferService_Impl;
import com.sun.xml.rpc.client.StubBase;


/**
 * @author ranjana.chouhan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MobinilPostToPreConnector {

	private static final Class MEAI_OnlineServices_webServices_PostToPreCreditTransferPortType_PortClass = com.btsl.pretups.inter.mobinilpost.mobinilpoststub.MEAI_OnlineServices_webServices_PostToPreCreditTransferPortType.class;
	private MEAI_OnlineServices_webServices_PostToPreCreditTransferPortType_Stub _clientstub=null;
	private MEAI_OnlineServices_webServices_PostToPreCreditTransferPortType _stub=null;
	
	/**
	 * Default constructor
	 *
	 */
	public MobinilPostToPreConnector(){
	}
	
	public MobinilPostToPreConnector(String p_serviceAddress, String p_timeOut){
	try
	{
	  
        _stub=(new MEAI_OnlineServices_webServices_PostToPreCreditTransferService_Impl().getMEAI_OnlineServices_webServices_PostToPreCreditTransferPort0());
        _clientstub=(MEAI_OnlineServices_webServices_PostToPreCreditTransferPortType_Stub)_stub;
         StubBase _stubSuper=(StubBase)_clientstub;
        _stubSuper._setProperty(javax.xml.rpc.Stub.ENDPOINT_ADDRESS_PROPERTY,p_serviceAddress);
    	
	         /*MEAI_OnlineServices_webServices_PostToPreCreditTransferPortType _stub;
	    	 _stub =  (new MEAI_OnlineServices_webServices_PostToPreCreditTransferService_Impl().getMEAI_OnlineServices_webServices_PostToPreCreditTransferPort0());
	    	 _clientstub = (MEAI_OnlineServices_webServices_PostToPreCreditTransferPortType_Stub)_stub;
	    	StubBase _stubSuper=(StubBase)_clientstub;
	    	_stubSuper._setProperty(javax.xml.rpc.Stub.ENDPOINT_ADDRESS_PROPERTY,"http://10.11.113.24:3333/soap/rpc");
	    	*/
	        
	}catch(Exception se)
	{
	        
	}
}       	        
	protected MEAI_OnlineServices_webServices_PostToPreCreditTransferPortType_Stub getClientStub()
	{
		return _clientstub;
	}      
	  
 }


	

