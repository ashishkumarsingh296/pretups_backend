/*
 * Created on Jun 10, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.safaricom;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.inter.safaricom.safaricomstub.Mediator_Stub;
import com.inter.safaricom.safaricomstub.Request;
import com.inter.safaricom.safaricomstub.Response;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
/**
 * @author dhiraj.tiwari
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SafcomTestServer1 {

	/**
	 * 
	 */
	public SafcomTestServer1() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		
		Mediator_Stub clientStub=null;
		 Response responseObj=null;
		 Request p_requestObj=null;
		WebServiceConnector serviceConnection;
		try {
//		serviceConnection = new WebServiceConnector("http://10.6.98.171:8087/");
		serviceConnection = new WebServiceConnector("http://10.6.255.30:8080/");
			p_requestObj =new Request(1,500,"254740788209","254740788209","pinless","8001","140361d554d525411","SFC_PINLESS");
	
		clientStub =serviceConnection.getClient();

		if(clientStub==null)
		{
			throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
		}
		
		
	System.out.println("p_requestObj:  "+p_requestObj.toString() +"url::"+"http://10.6.255.30:8080/");
//		System.out.println("p_requestObj:  "+p_requestObj.toString() +"url::"+"http://10.6.98.171:8087/");
		responseObj=clientStub.rechargeAmountSFC(p_requestObj, p_requestObj);
		
		System.out.println("responseObj:  "+responseObj.toString());
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
		}
		
	}
}

