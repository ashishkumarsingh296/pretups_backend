package com.inter.claro.cs5.cs5scheduler;

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
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.util.BTSLUtil;

/**
 * @(#)NodeManager
 * Copyright(c) 2016, Mahindra Comviva Technologies LTD.
 * All Rights Reserved
 *-------------------------------------------------------------------------------------------------
 * Author				Date			History
 *-------------------------------------------------------------------------------------------------
 * Sanjay Kumar Bind1       30-Sep-2016     Initial Creation
 * ------------------------------------------------------------------------------------------------
 * This class is responsible to store the instances of NodeScheduler corresponding to each interface id.
 *  
 */
public class NodeManager {
	
	private static final Log log = LogFactory.getLog(NodeManager.class);
	private static HashMap<String, NodeScheduler> cs5nodeschedulermap;//Contains the instance of NodeScheduler with key as interface id.
	
	private NodeManager(){
		
	}
        
    /**
     * This method is responsible to store the instance of NodeScheduler and store this into
     * a HashMap with interface id as Key.
     * @param	String pInterfaceIDs
     */
    public static void initialize(String pInterfaceIDs) throws BTSLBaseException
    {
    	final String methodName = "initialize";
        if(log.isDebugEnabled())
        	log.debug(methodName,PretupsI.ENTERED+" pInterfaceIDs::"+pInterfaceIDs);
        String strINId = null;
		NodeScheduler cs5NodeController = null;
		String[] inStrArray = null;
		try 
		{
		    cs5nodeschedulermap = new HashMap<>();
		    inStrArray = pInterfaceIDs.split(",");
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
				cs5nodeschedulermap.put(strINId,cs5NodeController);
			}//end of while.
		}
		catch(BTSLBaseException be)
		{
		    log.error(methodName,"BTSLBaseException be:"+be.getMessage());
		    throw be;
		}//end of catch-BTSLBaseException
		catch(Exception e)
		{
			log.errorTrace(PretupsI.EXCEPTION+ " in method ::"+methodName,e);
		    //Destroying the NodeScheduler Objects from Hashtable cs5nodeschedulermap
		    destroy();
		    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NodeManager[initialize]","String of interface ids="+pInterfaceIDs, "","", "While initializing the instance of NodeScheduler for the INTERFACE_ID ="+strINId+" get Exception=" + e.getMessage());
		    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_INITIALIZATION);
		}//end of catch-Exception
		finally
		{
		    if(log.isDebugEnabled())
		    	log.debug(methodName,PretupsI.EXITED+" _cs5ObjectMap::"+cs5nodeschedulermap);
		}//end of finally
    }//end of initialize
    /**
     * This method is used to return the Scheduler object based on the Interface Id.
     * @param pInterfaceID
     * @return
     * @throws Exception
     */
    public static NodeScheduler getScheduler(String pInterfaceID) throws BTSLBaseException
    {
    	final String methodName = "getScheduler";
        if(log.isDebugEnabled())
        	log.debug(methodName,PretupsI.ENTERED+" pInterfaceID::"+pInterfaceID);
        NodeScheduler cs5NodeScheduler=null;
        try
        {
            //Getting the NodeScheduler instance for an Interface.  
            cs5NodeScheduler = cs5nodeschedulermap.get(pInterfaceID);
        }
        catch(Exception e)
        {
        	log.errorTrace(PretupsI.EXCEPTION+" in method ::"+methodName,e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NodeManager[getScheduler]","INTERFACE_ID="+pInterfaceID, "","", "While getting the the instance of NodeScheduler for the interfaceID ="+pInterfaceID+" get Exception=" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_WHILE_GETTING_SCHEDULER_OBJECT);
        }//end of catch
        finally
        {
            if(log.isDebugEnabled())
            	log.debug(methodName,PretupsI.EXITED+" cs5NodeScheduler::"+cs5NodeScheduler);
        }//end of finally
        return cs5NodeScheduler;
    }//end of getScheduler
    /**
     * This method is used to destoy the NodeScheduler's object stored in 
     *
     */
	private static void destroy()
	{
		final String methodName = "destroy"; 
		if(log.isDebugEnabled())
			log.debug(methodName,PretupsI.ENTERED);
		HashMap<String, NodeScheduler> map=null;
		try 
		{
		    if(cs5nodeschedulermap!=null)
		    {
				map =new HashMap<>(cs5nodeschedulermap);
				Set<String> ketSetCode=map.keySet();
				Iterator<String> iter=ketSetCode.iterator();
				String key;
				NodeScheduler cs5NodeScheduler=null;
				 while(iter.hasNext())
				 {
					 key=iter.next();
					 log.info(methodName,"Destroying cs5NodeScheduler object from cs5nodeschedulermap for Interface ID="+key);
					try
					{
					    cs5NodeScheduler = cs5nodeschedulermap.remove(key);
						if(cs5NodeScheduler!=null)
						{
						    cs5NodeScheduler = null;
						}
					}
					catch(Exception e)
					{
						cs5NodeScheduler = null;
						throw e;
					}
				 }//end of while.
		    }//end of if
		}
		catch(Exception e) 
		{
			log.error(methodName,PretupsI.EXCEPTION+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"NodeManager[destroy]","","","","While destorying the NodeScheduler objects got the Exception "+e.getMessage());
			throw e;
		}//end of catch-Exception
		finally
		{
		    if(log.isDebugEnabled())
		    	log.debug(methodName,PretupsI.EXITED);
		}//end of finally
	}//end of destroy
}
