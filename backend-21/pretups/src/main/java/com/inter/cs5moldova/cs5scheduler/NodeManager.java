package com.inter.cs5moldova.cs5scheduler;

/**
 * @(#)NodeManager.java
 * Copyright(c) 2011, Comviva Technologies Ltd.
 * All Rights Reserved
 *-------------------------------------------------------------------------------------------------
 * 	  Author				Date				 History
 *-------------------------------------------------------------------------------------------------
 * Vikas Jauhari        Mar 29, 2012		    Initial Creation
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
	private NodeManager(){
		
	}
	private static Log log = LogFactory.getLog(NodeManager.class.getName());
	private static HashMap<String,NodeScheduler> cs3NodeSchedulerMap=null;//Contains the instance of NodeScheduler with key as interface id.

	/**
	 * This method is responsible to store the instance of NodeScheduler and store this into
	 * a HashMap with interface id as Key.
	 * @param	String p_interfaceIDs
	 */
	public static void initialize(String interfaceIDs) throws BTSLBaseException
	{
		final String methodName = "initialize";
		
		if(log.isDebugEnabled()) log.debug(methodName,"Entered p_interfaceIDs::"+interfaceIDs);
		String strINId = null;
		NodeScheduler cs3NodeController = null;
		String[] inStrArray = null;
		try 
		{
			cs3NodeSchedulerMap = new HashMap<>();
			inStrArray = interfaceIDs.split(",");
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
				cs3NodeSchedulerMap.put(strINId,cs3NodeController);
			}
		}
		catch(BTSLBaseException be)
		{
			log.error(methodName,"BTSLBaseException be:"+be.getMessage());
			throw be;
		}
		catch(Exception e)
		{
			log.errorTrace(methodName, e);
			log.error(methodName,"Exception e::"+e.getMessage());
			//Destroying the NodeScheduler Objects from Hashtable cs3NodeSchedulerMap
			destroy();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NodeManager[initialize]","String of interface ids="+interfaceIDs, "","", "While initializing the instance of NodeScheduler for the INTERFACE_ID ="+strINId+" get Exception=" + e.getMessage());
			throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS3_NODE_INITIALIZATION);
		}
		finally
		{
			if(log.isDebugEnabled()) log.debug(methodName,"Exited _cs3ObjectMap::"+cs3NodeSchedulerMap);
		}
	}

	/**
	 * This method is used to return the Scheduler object based on the Interface Id.
	 * @param p_interfaceID
	 * @return
	 * @throws Exception
	 */
	public static NodeScheduler getScheduler(String interfaceID) throws BTSLBaseException
	{
		final String methodName = "getScheduler";
		if(log.isDebugEnabled()) log.debug(methodName,"Entered p_interfaceID::"+interfaceID);
		NodeScheduler cs3NodeScheduler=null;
		try
		{
			//Getting the NodeScheduler instance for an Interface.  
			cs3NodeScheduler =(NodeScheduler)cs3NodeSchedulerMap.get(interfaceID);
		}
		catch(Exception e)
		{
			log.errorTrace(methodName, e);
			log.error(methodName,"Exception e::"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NodeManager[getScheduler]","INTERFACE_ID="+interfaceID, "","", "While getting the the instance of NodeScheduler for the interfaceID ="+interfaceID+" get Exception=" + e.getMessage());
			throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS3_WHILE_GETTING_SCHEDULER_OBJECT);
		}
		finally
		{
			if(log.isDebugEnabled()) log.debug(methodName,"Exited cs3NodeScheduler::"+cs3NodeScheduler);
		}
		return cs3NodeScheduler;
	}

	/**
	 * This method is used to destoy the NodeScheduler's object stored in 
	 *
	 */
	private static void destroy()
	{
		final String methodName = "destroy";
		if(log.isDebugEnabled()) log.debug(methodName,"Entered ");
		HashMap<String,NodeScheduler> map=null;
		try 
		{
			if(cs3NodeSchedulerMap!=null)
			{
				map =new HashMap<>(cs3NodeSchedulerMap);
				Set ketSetCode=map.keySet();
				Iterator iter=ketSetCode.iterator();
				String key=null;
				NodeScheduler cs3NodeScheduler=null;
				while(iter.hasNext())
				{
					key=(String)iter.next();
					log.info(methodName,"Destroying cs3NodeScheduler object from cs3NodeSchedulerMap for Interface ID="+key);
					try
					{
						cs3NodeScheduler = (NodeScheduler)cs3NodeSchedulerMap.remove(key);
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
			log.errorTrace(methodName, e);
			log.error(methodName,"Exception e:"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"NodeManager[destroy]","","","","While destorying the NodeScheduler objects got the Exception "+e.getMessage());
		}
		finally
		{
			if(log.isDebugEnabled()) log.debug(methodName,"Exiting");
		}
	}
}
