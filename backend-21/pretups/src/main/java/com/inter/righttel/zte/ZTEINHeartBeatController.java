package com.inter.righttel.zte;
/**
 * @ZTEHeartBeatController.java
 * Copyright(c) 2009, Bharti Telesoft Int. Public Ltd.
 *  All Rights Reserved
 *-------------------------------------------------------------------------------------------------
 *     Author				     Date			        History
 *-------------------------------------------------------------------------------------------------
 * Vipan						Sep 13, 2013		Initial Creation
 * -----------------------------------------------------------------------------------------------
 * This class is responsible to control the HeartBeat Thread.
 */

import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.util.BTSLUtil;

public class ZTEINHeartBeatController 
{
	static Log _logger = LogFactory.getLog(ZTEINHeartBeatController.class.getName());

	private HashMap<String,ZTEINHeartBeat> _heartBeatThreadMap = null;
	public ZTEINHeartBeatController(String p_poolIDs)
	{
		String[] inStrArray = null;
		String interfaceID=null;
		if(_logger.isDebugEnabled()) _logger.debug("ZTEINHeartBeatController[constructor]"," Entered p_poolIDs:"+p_poolIDs);		try
		{
			int nodes=0;
		    inStrArray = p_poolIDs.split(",");
		    if(isNullArray(inStrArray))
		        throw new BTSLBaseException("ZTEINHeartBeatController[constructor]"+InterfaceErrorCodesI.HEARTBEAT_ERROR_OBJECT_POOL_INIT);
		    
			//Confirm while creating instances,if any errror occurs for an interface,should we stop the process with handling the event and throw exception
			//Or only event should be handled corresponding to that interface and continue to other.
			_heartBeatThreadMap = new HashMap<String,ZTEINHeartBeat>();//initialize the Map size equal to number of the pool Ids.
			for(int i=0,size=inStrArray.length;i<size;i++)
			{
				interfaceID = inStrArray[i];
				String fileCacheId=interfaceID;
				 nodes = Integer.parseInt(FileCache.getValue(fileCacheId,"MAX_ACTIVE_NODES"));
				 ZTEINHBLogger.logMessage("Start Loop"+nodes); 
				 for(int node=1;node<=nodes;node++)
				  {
					 String interfaceIDNode="";
					  ZTEINHBLogger.logMessage("inside the Loop"+node);
					  interfaceIDNode=interfaceID+"_"+node;
					  _heartBeatThreadMap.put(interfaceIDNode.trim(),new ZTEINHeartBeat(fileCacheId,interfaceIDNode));
					  
				  }
				  ZTEINHBLogger.logMessage("End Loop"+nodes);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			_logger.error("ZTEINHeartBeatController[constructor]"," Exception e:"+e.getMessage());
		}
	}
	//Implement the logic to destroy the heart beat thread based on the INIDs
	//This method 
	public void stopHeartBeat(String p_interfaceID)
	{
		if(_logger.isDebugEnabled()) _logger.debug("stopHeartBeat ","Entered p_interfaceID:"+p_interfaceID);
		String[] inStrArray = null;
		String interfaceID=null;
		ZTEINHeartBeat zteHearBeat = null;
		try
		{
			int nodes=0;
		    inStrArray = p_interfaceID.split(",");
		    if(isNullArray(inStrArray))
		        throw new BTSLBaseException("ZTEINHeartBeatController[stopHeartBeat]"+InterfaceErrorCodesI.HEARTBEAT_ERROR_OBJECT_POOL_DESTROY);
			
			for(int i=0,size=inStrArray.length;i<size;i++)
			{
				interfaceID = inStrArray[i].trim();
				 String fileCacheId=interfaceID;
				 nodes = Integer.parseInt(FileCache.getValue(fileCacheId,"NODES_SIZE_"+interfaceID));
				 for(int node=1;node<=nodes;node++)
				  {
					  String interfaceIDNode="";
					  interfaceIDNode=inStrArray[i]+"_"+node;
					  ZTEINHBLogger.logMessage("inside the Loop"+node+" interfaceID="+interfaceIDNode+" _heartBeatThreadMap"+_heartBeatThreadMap.toString());
					  if(_heartBeatThreadMap!=null && _heartBeatThreadMap.containsKey(interfaceIDNode))
						{
						  
							zteHearBeat = (ZTEINHeartBeat)_heartBeatThreadMap.remove(interfaceIDNode);
							ZTEINHBLogger.logMessage("Stop the Heart BEAT"+zteHearBeat.toString());
							zteHearBeat.stopHearBeat();
							ZTEINHBLogger.logMessage("Succefuly stop the Heart BEAT"+zteHearBeat.toString()+" _heartBeatThreadMap"+_heartBeatThreadMap.toString());
							
						}
				  }
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();//For testing.
		}
		
	}
	/**This method will check the array for null.
	 * If all the entries in array is null then return true otherwise return false
	 * 
	 * @param p_arr
	 * @return
	 */
	public static boolean isNullArray(String[] p_arr)
	{
		if(_logger.isDebugEnabled()) _logger.debug("isNullArray ","Entered p_arr: "+p_arr);
		boolean isNull=true;
		if(p_arr!=null)
		{
			for(int i=0,j=p_arr.length;i<j;i++)
			{
				if(!BTSLUtil.isNullString(p_arr[i]))
				{
					isNull=false;
					break;
				}
			}
		}
		if(_logger.isDebugEnabled()) _logger.debug("isNullArray ","Exited isNull: "+isNull);
		return isNull;
	}
}
