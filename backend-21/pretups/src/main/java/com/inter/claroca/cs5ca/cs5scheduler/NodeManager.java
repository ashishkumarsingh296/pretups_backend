package com.inter.claroca.cs5ca.cs5scheduler;

/**
 * @(#)NodeManager.java
 * Copyright(c) 2015, Comviva Technologies Ltd.
 * All Rights Reserved
 *-------------------------------------------------------------------------------------------------
 * 	  Author				Date				 	History
 *-------------------------------------------------------------------------------------------------
 * 	 Zeeshan Aleem        July 20, 2016		    Initial Creation
 * ------------------------------------------------------------------------------------------------
 */

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.util.BTSLUtil;

public class NodeManager 
{
	private static Log _log = LogFactory.getLog(NodeManager.class.getName());
	private static HashMap<String,NodeScheduler> _cs3NodeSchedulerMap=null;//Contains the instance of NodeScheduler with key as interface id.

	/**
	 * This method is responsible to store the instance of NodeScheduler and store this into
	 * a HashMap with interface id as Key.
	 * @param	String p_interfaceIDs
	 */
	public static void initialize(String p_interfaceIDs) throws BTSLBaseException
	{
		String METHOD_NAME="initialize";
		if(_log.isDebugEnabled()) _log.debug(METHOD_NAME,"Entered p_interfaceIDs::"+p_interfaceIDs);
		String strINId = null;
		NodeScheduler cs3NodeController = null;
		String[] inStrArray = null;
		try 
		{
			_cs3NodeSchedulerMap = new HashMap<String,NodeScheduler>();
			inStrArray = p_interfaceIDs.split(",");
			if(BTSLUtil.isNullArray(inStrArray))
				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS3_NO_INTERFACEIDS); 
			//Confirm while creating instances,if any errror occurs for an interface,should we stop the process with handling the event and throw exception
			//Or only event should be handled corresponding to that interface and continue to other.
			for(int i=0,size=inStrArray.length;i<size;i++)
			{
				//Create an instance of NodeScheduler corresponding to each Interface.
				strINId = inStrArray[i].trim();
				cs3NodeController = new NodeScheduler(strINId);
				//Put the instnace of NodeScheduler with key as the interfaceID into a HashMap.
				_cs3NodeSchedulerMap.put(strINId,cs3NodeController);
			}
		}
		catch(BTSLBaseException be)
		{
			_log.error(METHOD_NAME,"BTSLBaseException be:"+be.getMessage());
			throw be;
		}
		catch(Exception e)
		{
			_log.errorTrace(METHOD_NAME,e);
			_log.error(METHOD_NAME,"Exception e::"+e.getMessage());
			//Destroying the NodeScheduler Objects from Hashtable _cs3NodeSchedulerMap
			destroy();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NodeManager[initialize]","String of interface ids="+p_interfaceIDs, "","", "While initializing the instance of NodeScheduler for the INTERFACE_ID ="+strINId+" get Exception=" + e.getMessage());
			throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS3_NODE_INITIALIZATION);
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug(METHOD_NAME,"Exited _cs3ObjectMap::"+_cs3NodeSchedulerMap);
		}
	}

	/**
	 * This method is used to return the Scheduler object based on the Interface Id.
	 * @param p_interfaceID
	 * @return
	 * @throws Exception
	 */
	public static NodeScheduler getScheduler(String p_interfaceID) throws BTSLBaseException
	{
		String METHOD_NAME="getScheduler";
		if(_log.isDebugEnabled()) _log.debug(METHOD_NAME,"Entered p_interfaceID::"+p_interfaceID);
		NodeScheduler cs3NodeScheduler=null;
		try
		{
			//Getting the NodeScheduler instance for an Interface.  
			cs3NodeScheduler =(NodeScheduler)_cs3NodeSchedulerMap.get(p_interfaceID);
		}
		catch(Exception e)
		{
			_log.errorTrace(METHOD_NAME,e);
			_log.error(METHOD_NAME,"Exception e::"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NodeManager[getScheduler]","INTERFACE_ID="+p_interfaceID, "","", "While getting the the instance of NodeScheduler for the interfaceID ="+p_interfaceID+" get Exception=" + e.getMessage());
			throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS3_WHILE_GETTING_SCHEDULER_OBJECT);
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug(METHOD_NAME,"Exited cs3NodeScheduler::"+cs3NodeScheduler);
		}
		return cs3NodeScheduler;
	}

	/**
	 * This method is used to destoy the NodeScheduler's object stored in 
	 *
	 */
	private static void destroy()
	{
		String METHOD_NAME="destroy";
		if(_log.isDebugEnabled()) _log.debug(METHOD_NAME,"Entered ");
		HashMap<String,NodeScheduler> map=null;
		try 
		{
			if(_cs3NodeSchedulerMap!=null)
			{
				map =new HashMap<String,NodeScheduler>(_cs3NodeSchedulerMap);
				Set ketSetCode=map.keySet();
				Iterator iter=ketSetCode.iterator();
				String key=null;
				NodeScheduler cs3NodeScheduler=null;
				while(iter.hasNext())
				{
					key=(String)iter.next();
					_log.info(METHOD_NAME,"Destroying cs3NodeScheduler object from _cs3NodeSchedulerMap for Interface ID="+key);
					try
					{
						cs3NodeScheduler = (NodeScheduler)_cs3NodeSchedulerMap.remove(key);
						if(cs3NodeScheduler!=null)
						{
							cs3NodeScheduler = null;
						}
					}
					catch(Exception e)
					{
						cs3NodeScheduler = null;							
					}
				}
			}
		}
		catch(Exception e) 
		{
			_log.errorTrace(METHOD_NAME,e);
			_log.error(METHOD_NAME,"Exception e:"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"NodeManager[destroy]","","","","While destorying the NodeScheduler objects got the Exception "+e.getMessage());
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug(METHOD_NAME,"Exiting");
		}
	}
}
