package com.inter.claroColUserInfoWS.scheduler;

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
 *                 Copyright(c) 2016, Comviva Technologies Ltd.
 * 				   All Rights Reserved
 *                 ------------------------------------------------------------
 *                 -------------------------------------
 *                 Author Date History
 *                 ------------------------------------------------------------
 *                 -------------------------------------
 *                 Pankaj Sharma Spt 28,2016 Initial Creation
 *                 ------------------------------------------------------------
 *                 ------------------------------------
 *                 This servlet is responsible to initialize the node details at
 *                 server start up.
 */
public class NodeManager {
    private static Log log = LogFactory.getLog(NodeManager.class.getName());
    private static HashMap nodeSchedulerMap = null;// Contains the instance of
                                                    // NodeScheduler with key as
                                                    // interface id.
    private NodeManager() {
		//  Auto-generated constructor stub
	}
    
    /**
     * This method is responsible to store the instance of NodeScheduler and
     * store this into
     * a HashMap with interface id as Key.
     * 
     * @param String pInterfaceIDs
     * @return void
     * @throws BTSLBaseException
     */
    public static void initialize(String pInterfaceIDs) throws BTSLBaseException {
    	final String methodName="initialize";
        if (log.isDebugEnabled())
            log.debug(methodName, "Entered p_interfaceIDs::" + pInterfaceIDs);
        String strINId = null;
        NodeScheduler nodeController = null;
        String[] inStrArray = null;
        try {
            nodeSchedulerMap = new HashMap();
            inStrArray = pInterfaceIDs.split(",");
            if (BTSLUtil.isNullArray(inStrArray))
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NO_INTERFACEIDS);
            // Confirm while creating instances,if any errror occurs for an
            // interface,should we stop the process with handling the event and
            // throw exception
            // Or only event should be handled corresponding to that interface
            // and continue to other.
            for (int i = 0, size = inStrArray.length; i < size; i++) {
                // Create an instance of NodeScheduler corresponding to each
                // Interface.
                strINId = inStrArray[i].trim();
                nodeController = new NodeScheduler(strINId);
                // Put the instnace of NodeScheduler with key as the interfaceID
                // into a HashMap.
                nodeSchedulerMap.put(strINId, nodeController);
                if (log.isDebugEnabled())
                    log.debug("---------nodeSchedulerMap------------", nodeSchedulerMap);
            }// end of while.
        } catch (BTSLBaseException be) {
            log.error(methodName, "BTSLBaseException be:" + be.getMessage());
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
        	log.errorTrace("BTSLBaseException be::"+methodName , e);
        	// Destroying the NodeScheduler Objects from Hashtable
            // nodeSchedulerMap
            destroy();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NodeManager[initialize]", "String of interface ids=" + pInterfaceIDs, "", "", "While initializing the instance of NodeScheduler for the INTERFACE_ID =" + strINId + " get Exception=" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NODE_INITIALIZATION);
        }// end of catch-Exception
        finally {
            if (log.isDebugEnabled())
                log.debug(methodName, "Exited _comObjectMap::" + nodeSchedulerMap);
        }// end of finally
    }// end of initialize

    /**
     * This method is used to return the Scheduler object based on the Interface
     * Id.
     * 
     * @param pInterfaceID
     * @return NodeScheduler
     * @throws BTSLBaseException
     */
    public static NodeScheduler getScheduler(String pInterfaceID) throws BTSLBaseException {
    	final String methodName="getScheduler";
        if (log.isDebugEnabled())
            log.debug(methodName, "Entered pInterfaceID::" + pInterfaceID);
        NodeScheduler nodeScheduler = null;
        try {
            // Getting the NodeScheduler instance for an Interface.
            nodeScheduler = (NodeScheduler) nodeSchedulerMap.get(pInterfaceID);
        } catch (Exception e) {
        	log.errorTrace("BTSLBaseException be::"+methodName , e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NodeManager[getScheduler]", "INTERFACE_ID=" + pInterfaceID, "", "", "While getting the the instance of NodeScheduler for the interfaceID =" + pInterfaceID + " get Exception=" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_WHILE_GETTING_SCHEDULER_OBJECT);
        }// end of catch
        finally {
            if (log.isDebugEnabled())
                log.debug(methodName, "Exited comNodeScheduler::" + nodeScheduler);
        }// end of finally
        return nodeScheduler;
    }// end of getScheduler

    /**
     * This method is used to destoy the NodeScheduler's object stored in
     */
    private static void destroy() {
    	final String methodName="destroy";
        if (log.isDebugEnabled())
            log.debug(methodName, "Entered ");
        HashMap map = null;
        try {
            if (nodeSchedulerMap != null) {
                map = new HashMap(nodeSchedulerMap);
                Set ketSetCode = map.keySet();
                Iterator iter = ketSetCode.iterator();
                String key = null;
                NodeScheduler nodeScheduler = null;
                while (iter.hasNext()) {
                    key = (String) iter.next();
                    log.info(methodName, "Destroying nodeScheduler object from nodeSchedulerMap for Interface ID=" + key);
                    try {
                        nodeScheduler = (NodeScheduler) nodeSchedulerMap.remove(key);
                        if (nodeScheduler != null) {
                            nodeScheduler = null;
                        }
                    } catch (Exception e) {
                        nodeScheduler = null;
                        log.errorTrace("BTSLBaseException be ::"+methodName , e);
                    }
                }// end of while.
            }// end of if
        } catch (Exception e) {
        	log.errorTrace("BTSLBaseException be ::"+methodName , e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NodeManager[destroy]", "", "", "", "While destorying the NodeScheduler objects got the Exception " + e.getMessage());
        }// end of catch-Exception
        finally {
            if (log.isDebugEnabled())
                log.debug(methodName, "Exiting");
        }// end of finally
    }// end of destroy
}