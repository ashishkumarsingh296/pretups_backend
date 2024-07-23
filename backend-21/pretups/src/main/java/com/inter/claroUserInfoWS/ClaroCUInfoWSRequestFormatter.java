/*
 * Created on June 18, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.claroUserInfoWS;

import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.inter.claroUserInfoWS.stub.DistribuidorDataRequestType;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;


/**
 * @author vipan.kumar
 * 
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ClaroCUInfoWSRequestFormatter 
{
	public static Log _log = LogFactory.getLog(ClaroCUInfoWSRequestFormatter.class);
	String lineSep = null;
	String _soapAction="";

	public ClaroCUInfoWSRequestFormatter()
	{
		//lineSep = System.getProperty("line.separator")+"\r";
		lineSep = System.getProperty("line.separator")+"";
	}

	/**
	 * This method is used to parse the response string based on the type of Action.
	 * @param	int p_action
	 * @param	HashMap p_map
	 * @return	String.
	 * @throws	Exception
	 */
	protected Object generateRequest(int p_action, HashMap p_map) throws Exception 
	{
		if(_log.isDebugEnabled())_log.debug("generateRequest","Entered p_action::"+p_action+" map::"+p_map);
		Object object = null;
		p_map.put("action", String.valueOf(p_action));
		try
		{
			switch(p_action)
			{
			case ClaroCUInfoWSI.ACTION_ACCOUNT_DETAILS: 
			{

				DistribuidorDataRequestType recargasRequest=generateCUInfoRequest(p_map);
				object= (Object)recargasRequest;
				break;	
			}
				
			}
		}
		catch(Exception e)
		{
			_log.error("generateRequest","Exception e ::"+e.getMessage());
			throw e;
		} 
		finally
		{
			if(_log.isDebugEnabled())_log.debug("generateRequest","Exited Request String: object::"+object);
		}
		return object;
	}

	private DistribuidorDataRequestType generateCUInfoRequest(HashMap p_map) throws Exception{
		if(_log.isDebugEnabled()) _log.debug("generateCUInfoRequest","Entered p_requestMap::"+p_map);
		DistribuidorDataRequestType distribuidorDataRequestType= null;
		try
		{
			distribuidorDataRequestType=new DistribuidorDataRequestType();
			distribuidorDataRequestType.setCodigo(p_map.get("CODIGO").toString());
		}
		catch(Exception e)
		{
			_log.error("generateRechargeDebitRequest","Exception e: "+e);
			throw new BTSLBaseException(this,"generateCUInfoRequest",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug("generateCUInfoRequest","Exiting Request debitoRequest::"+distribuidorDataRequestType);
		}
		return distribuidorDataRequestType;
	}

	

}
