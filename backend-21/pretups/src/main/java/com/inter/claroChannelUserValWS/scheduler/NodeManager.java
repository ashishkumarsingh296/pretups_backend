package com.inter.claroChannelUserValWS.scheduler;

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

/**
 * @(#)NodeManager
 * Copyright(c) 2013, Bharti Telesoft Int. Public Ltd.
 * All Rights Reserved
 *-------------------------------------------------------------------------------------------------
 * Author				Date			History
 *-------------------------------------------------------------------------------------------------
 * Vipan Kumar			Oct 11,2013     Initial Creation
 * ------------------------------------------------------------------------------------------------
 * This class is responsible to store the instances of NodeScheduler corresponding to each interface id.
 *  
 */
public class NodeManager {
    private static Log _log = LogFactory.getLog(NodeManager.class.getName());
    private static HashMap _nodeSchedulerMap=null;//Contains the instance of NodeScheduler with key as interface id.
    /**
     * This method is responsible to store the instance of NodeScheduler and store this into
     * a HashMap with interface id as Key.
     * @param	String p_interfaceIDs
     */
    public static void initialize(String p_interfaceIDs) throws BTSLBaseException
    {
        if(_log.isDebugEnabled()) _log.debug("initialize","Entered p_interfaceIDs::"+p_interfaceIDs);
        String strINId = null;
		NodeScheduler nodeController = null;
		String[] inStrArray = null;
		try 
		{
		    _nodeSchedulerMap = new HashMap();
		    inStrArray = p_interfaceIDs.split(",");
		    if(BTSLUtil.isNullArray(inStrArray))
		        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NO_INTERFACEIDS); 
			//Confirm while creating instances,if any errror occurs for an interface,should we stop the process with handling the event and throw exception
			//Or only event should be handled corresponding to that interface and continue to other.
			for(int i=0,size=inStrArray.length;i<size;i++)
			{
			    //Create an instance of NodeScheduler corresponding to each Interface.
				strINId = inStrArray[i].trim();
				nodeController = new NodeScheduler(strINId);
				//Put the instnace of NodeScheduler with key as the interfaceID into a HashMap.
				_nodeSchedulerMap.put(strINId,nodeController);
			}//end of while.
		}
		catch(BTSLBaseException be)
		{
		    _log.error("initialize","BTSLBaseException be:"+be.getMessage());
		    throw be;
		}//end of catch-BTSLBaseException
		catch(Exception e)
		{
		    e.printStackTrace();
		    _log.error("initialize","Exception e::"+e.getMessage());
		    //Destroying the NodeScheduler Objects from Hashtable _nodeSchedulerMap
		    destroy();
		    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NodeManager[initialize]","String of interface ids="+p_interfaceIDs, "","", "While initializing the instance of NodeScheduler for the INTERFACE_ID ="+strINId+" get Exception=" + e.getMessage());
		    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NODE_INITIALIZATION);
		}//end of catch-Exception
		finally
		{
		    if(_log.isDebugEnabled()) _log.debug("initialize","Exited _comObjectMap::"+_nodeSchedulerMap);
		}//end of finally
    }//end of initialize
    /**
     * This method is used to return the Scheduler object based on the Interface Id.
     * @param p_interfaceID
     * @return
     * @throws Exception
     */
    public static NodeScheduler getScheduler(String p_interfaceID) throws BTSLBaseException
    {
        if(_log.isDebugEnabled()) _log.debug("getScheduler","Entered p_interfaceID::"+p_interfaceID);
        NodeScheduler nodeScheduler=null;
        try
        {
            //Getting the NodeScheduler instance for an Interface.  
        	nodeScheduler =(NodeScheduler)_nodeSchedulerMap.get(p_interfaceID);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            _log.error("getScheduler","Exception e::"+e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NodeManager[getScheduler]","INTERFACE_ID="+p_interfaceID, "","", "While getting the the instance of NodeScheduler for the interfaceID ="+p_interfaceID+" get Exception=" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_WHILE_GETTING_SCHEDULER_OBJECT);
        }//end of catch
        finally
        {
            if(_log.isDebugEnabled()) _log.debug("getScheduler","Exited comNodeScheduler::"+nodeScheduler);
        }//end of finally
        return nodeScheduler;
    }//end of getScheduler
    /**
     * This method is used to destoy the NodeScheduler's object stored in 
     *
     */
	private static void destroy()
	{
		if(_log.isDebugEnabled()) _log.debug("destroy","Entered ");
		HashMap map=null;
		try 
		{
		    if(_nodeSchedulerMap!=null)
		    {
				map =new HashMap(_nodeSchedulerMap);
				Set ketSetCode=map.keySet();
				Iterator iter=ketSetCode.iterator();
				String key=null;
				NodeScheduler nodeScheduler=null;
				 while(iter.hasNext())
				 {
					 key=(String)iter.next();
					 _log.info("destroy","Destroying nodeScheduler object from _nodeSchedulerMap for Interface ID="+key);
					try
					{
						nodeScheduler = (NodeScheduler)_nodeSchedulerMap.remove(key);
						if(nodeScheduler!=null)
						{
							nodeScheduler = null;
						}
					}
					catch(Exception e)
					{
						//e.printStackTrace();
						nodeScheduler = null;							
					}
				 }//end of while.
		    }//end of if
		}
		catch(Exception e) 
		{
			e.printStackTrace();
			_log.error("destroy","Exception e:"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"NodeManager[destroy]","","","","While destorying the NodeScheduler objects got the Exception "+e.getMessage());
		}//end of catch-Exception
		finally
		{
		    if(_log.isDebugEnabled()) _log.debug("destroy","Exiting");
		}//end of finally
	}//end of destroy
}
