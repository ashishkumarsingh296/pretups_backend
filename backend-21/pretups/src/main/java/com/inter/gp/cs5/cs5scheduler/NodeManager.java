package com.inter.gp.cs5.cs5scheduler;

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
 * Copyright(c) 2015, Mahindra Comviva technologies LTD.
 * All Rights Reserved
 *-------------------------------------------------------------------------------------------------
 * Author				Date			History
 *-------------------------------------------------------------------------------------------------
 * Karan Vijay Singh         30-Sep-2015     Initial Creation
 * ------------------------------------------------------------------------------------------------
 * This class is responsible to store the instances of NodeScheduler corresponding to each interface id.
 *  
 */
public class NodeManager {
    private static Log log = LogFactory.getLog(NodeManager.class.getName());
    private static HashMap<String, NodeScheduler> _cs5NodeSchedulerMap=null;//Contains the instance of NodeScheduler with key as interface id.
    /**
     * This method is responsible to store the instance of NodeScheduler and store this into
     * a HashMap with interface id as Key.
     * @param	String p_interfaceIDs
     */
    public static void initialize(String p_interfaceIDs) throws BTSLBaseException
    {
    	String METHOD_NAME="initialize";
    	LogFactory.printLog(METHOD_NAME,"Entered p_interfaceIDs::"+p_interfaceIDs,log);
        String strINId = null;
		NodeScheduler cs5NodeController = null;
		String[] inStrArray = null;
		try 
		{
		    _cs5NodeSchedulerMap = new HashMap<String, NodeScheduler>();
		    inStrArray = p_interfaceIDs.split(",");
		    if(BTSLUtil.isNullArray(inStrArray))
		        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NO_INTERFACEIDS); 
			//Confirm while creating instances,if any errror occurs for an interface,should we stop the process with handling the event and throw exception
			//Or only event should be handled corresponding to that interface and continue to other.
			for(int i=0,size=inStrArray.length;i<size;i++)
			{
			    //Create an instance of NodeScheduler corresponding to each Interface.
				strINId = inStrArray[i].trim();
				cs5NodeController = new NodeScheduler(strINId);
				//Put the instnace of NodeScheduler with key as the interfaceID into a HashMap.
				_cs5NodeSchedulerMap.put(strINId,cs5NodeController);
			}//end of while.
		}
		catch(BTSLBaseException be)
		{
		    log.error("initialize","BTSLBaseException be:"+be.getMessage());
		    throw be;
		}//end of catch-BTSLBaseException
		catch(Exception e)
		{
		    log.errorTrace("Exception",e);
		    log.error("initialize","Exception e::"+e.getMessage());
		    //Destroying the NodeScheduler Objects from Hashtable _cs5NodeSchedulerMap
		    destroy();
		    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NodeManager[initialize]","String of interface ids="+p_interfaceIDs, "","", "While initializing the instance of NodeScheduler for the INTERFACE_ID ="+strINId+" get Exception=" + e.getMessage());
		    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_INITIALIZATION);
		}//end of catch-Exception
		finally
		{
			LogFactory.printLog(METHOD_NAME,"Exited _cs5ObjectMap::"+_cs5NodeSchedulerMap,log);
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
    	String METHOD_NAME="getScheduler";
    	LogFactory.printLog(METHOD_NAME,"Entered p_interfaceIDs::"+p_interfaceID,log);
       
        NodeScheduler cs5NodeScheduler=null;
        try
        {
            //Getting the NodeScheduler instance for an Interface.  
            cs5NodeScheduler =(NodeScheduler)_cs5NodeSchedulerMap.get(p_interfaceID);
        }
        catch(Exception e)
        {
            log.errorTrace("Exception",e);
            log.error("getScheduler","Exception e::"+e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NodeManager[getScheduler]","INTERFACE_ID="+p_interfaceID, "","", "While getting the the instance of NodeScheduler for the interfaceID ="+p_interfaceID+" get Exception=" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_WHILE_GETTING_SCHEDULER_OBJECT);
        }//end of catch
        finally
        {
        	LogFactory.printLog(METHOD_NAME,"Exited cs5NodeScheduler::"+cs5NodeScheduler,log);
        }//end of finally
        return cs5NodeScheduler;
    }//end of getScheduler
    /**
     * This method is used to destoy the NodeScheduler's object stored in 
     *
     */
	private static void destroy()
	{
		String METHOD_NAME="destroy";
    	LogFactory.printLog(METHOD_NAME,"Entered",log);
    	
		
		HashMap<String, NodeScheduler> map=null;
		try 
		{
		    if(_cs5NodeSchedulerMap!=null)
		    {
				map =new HashMap<String, NodeScheduler>(_cs5NodeSchedulerMap);
				Set<String> ketSetCode=map.keySet();
				Iterator<String> iter=ketSetCode.iterator();
				String key=null;
				NodeScheduler cs5NodeScheduler=null;
				 while(iter.hasNext())
				 {
					 key=(String)iter.next();
					 log.info("destroy","Destroying cs5NodeScheduler object from _cs5NodeSchedulerMap for Interface ID="+key);
					try
					{
					    cs5NodeScheduler = (NodeScheduler)_cs5NodeSchedulerMap.remove(key);
						if(cs5NodeScheduler!=null)
						{
						    cs5NodeScheduler = null;
						}
					}
					catch(Exception e)
					{
						log.errorTrace("Exception",e);
						cs5NodeScheduler = null;							
					}
				 }//end of while.
		    }//end of if
		}
		catch(Exception e) 
		{
			log.errorTrace("Exception",e);
			log.error("destroy","Exception e:"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"NodeManager[destroy]","","","","While destorying the NodeScheduler objects got the Exception "+e.getMessage());
		}//end of catch-Exception
		finally
		{
			LogFactory.printLog(METHOD_NAME,"Exiting",log);
		}//end of finally
	}//end of destroy
}
