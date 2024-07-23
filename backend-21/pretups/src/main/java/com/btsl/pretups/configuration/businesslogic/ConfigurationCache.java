/**
 * @(#)ConfigurationCache.java
 * Copyright(c) 2005, Bharti Telesoft Ltd.
 * All Rights Reserved
 *
 * <description>
 *-------------------------------------------------------------------------------------------------
 * Author                        Date            History
 *-------------------------------------------------------------------------------------------------
 * Sanjay Kumar Bind1            May 7, 2017     Initital Creation
 *-------------------------------------------------------------------------------------------------
 *
 */
package com.btsl.pretups.configuration.businesslogic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.logging.CacheOperationLog;
import com.btsl.util.BTSLUtil;

/**
 * @author Sanjay Kumar Bind1
 */
public class ConfigurationCache {
	private static Log log = LogFactory.getLog(ConfigurationCache.class.getName());
	private static Map preferenceMap = new HashMap();
	
	/**
   	 * ensures no instantiation
   	 */
	private ConfigurationCache(){
		
	}
	
	
	/**
	 * To Load all configurations on startup
	 */
	public static void loadPrefrencesOnStartUp()
	{
		final String methodName = "loadPrefrencesOnStartUp";
		LogFactory.printLog(methodName, "Entered", log);
		preferenceMap = loadPrefrences();
		LogFactory.printLog(methodName, "Exited", log);
	}

	/**
	 * To load the Preferences
	 * 
	 * @return Map ConfigurationCache
	 */
	private static Map loadPrefrences()
	{
		final String methodName = "loadPrefrences";
		LogFactory.printLog(methodName, "Entered", log);
		ConfigurationDAO configurationCacheDAO = new ConfigurationDAO();
		Map map = null;
		try
		{
			map = configurationCacheDAO.loadConfigurations();
		} 
		catch (BTSLBaseException be)
		{
			log.error("loadPrefrences()", "BTSLBaseException be: "+be);
			log.errorTrace(methodName,be);
		}
		catch (Exception e)
		{
			log.error("loadPrefrences()", "Exception e: "+e);
			log.errorTrace(methodName,e);
		}
		if(map != null) {
			LogFactory.printLog(methodName, "Exited " + map.size(), log);
		}
		return map;
	}

	/**
	 * To update the preferences cache void ConfigurationCache
	 */
	public static void updatePrefrences()
	{
		final String methodName = "updatePrefrences";
		LogFactory.printLog(methodName, " Entered", log);
		Map currentMap = loadPrefrences();
		if ((preferenceMap != null) && (preferenceMap.size() > 0))
		{
			compareMaps(preferenceMap, currentMap);
		}
		preferenceMap = currentMap;
		LogFactory.printLog(methodName, "Exited " + preferenceMap.size(), log);
		
	}
	/**
	 * get the value against the prefrenceCode at system level
	 * 
	 * @param pIntidKey
	 * @return String ConfigurationCache
	 */
	public static Object getSystemPreferenceValue(String pIntidKey)
	{
		final String methodName = "getSystemPreferenceValue";
		LogFactory.printLog(methodName, "Entered pIntidKey: " + pIntidKey, log);
		Object value = null;
		ConfigurationCacheVO cacheVO = (ConfigurationCacheVO) preferenceMap.get(pIntidKey);
		if (cacheVO != null)
			value = getCastedObject(cacheVO.getValueType(), cacheVO.getIntidValue());
		LogFactory.printLog(methodName, "Exited value: " + value, log);
		return value;
	}



	/**
	 * compare two Maps and check which have changed and log the value which
	 * has been changed
	 * 
	 * @param pPreviousMap
	 * @param pCurrentMap
	 *            void
	 */
	private static void compareMaps(Map pPreviousMap, Map pCurrentMap)
	{
		final String methodName = "compareMaps";
		LogFactory.printLog(methodName, "Entered pPreviousMap " + pPreviousMap + "  pCurrentMap: " + pCurrentMap, log);
		try
		{
			Iterator iterator = null;
			Iterator copiedIterator = null;
			if (pPreviousMap.size() == pCurrentMap.size())
			{
				iterator = pPreviousMap.keySet().iterator();
				copiedIterator = pPreviousMap.keySet().iterator();
			} 
			else if (pPreviousMap.size() > pCurrentMap.size())
			{
				iterator = pPreviousMap.keySet().iterator();
				copiedIterator = pPreviousMap.keySet().iterator();
			} else if (pPreviousMap.size() < pCurrentMap.size())
			{
				iterator = pCurrentMap.keySet().iterator();
				copiedIterator = pPreviousMap.keySet().iterator();
			}
	
			// to check whether any new network added or not but size of
			boolean isNewAdded = false;
	
			while (iterator!=null && iterator.hasNext())
			{
				String key = (String) iterator.next();
				ConfigurationCacheVO prevPreferenceCacheVO = (ConfigurationCacheVO) pPreviousMap.get(key);
				ConfigurationCacheVO curPreferenceCacheVO = (ConfigurationCacheVO) pCurrentMap.get(key);
	
				if ((prevPreferenceCacheVO != null) && (curPreferenceCacheVO == null))
				{
					isNewAdded = true;
					CacheOperationLog.log(methodName, BTSLUtil.formatMessage("Delete", prevPreferenceCacheVO.getPreferenceLevel(), prevPreferenceCacheVO.logInfo()));
				} else if ((prevPreferenceCacheVO == null) && (curPreferenceCacheVO != null))
				{
					CacheOperationLog.log(methodName, BTSLUtil.formatMessage("Add", curPreferenceCacheVO.getPreferenceLevel(), curPreferenceCacheVO.logInfo()));
				} else if ((prevPreferenceCacheVO != null) && (curPreferenceCacheVO != null) && (!curPreferenceCacheVO.equalsConfigurationCacheVO(prevPreferenceCacheVO)))
				{
						CacheOperationLog.log(methodName, BTSLUtil.formatMessage("Modify", curPreferenceCacheVO.getPreferenceLevel(), curPreferenceCacheVO.differences(prevPreferenceCacheVO)));
				}
			}
	
			/**
			 * Note: this case arises when same number of network added and deleted
			 * as well
			 */
			if ((pPreviousMap.size() == pCurrentMap.size()) && isNewAdded)
			{
				Map tempMap = new HashMap(pCurrentMap);
	
				while (copiedIterator.hasNext())
				{
					tempMap.remove((String) copiedIterator.next());
				}
	
				Iterator iterator2 = tempMap.keySet().iterator();
	
				while (iterator2.hasNext())
				{
					// new network added
					ConfigurationCacheVO preferenceCacheVO = (ConfigurationCacheVO) pCurrentMap.get(iterator2.next());
					CacheOperationLog.log(methodName, BTSLUtil.formatMessage("Add", preferenceCacheVO.getPreferenceLevel(), preferenceCacheVO.logInfo()));
				}
			}
		}
		catch(ConcurrentModificationException cme){
			log.errorTrace(methodName,cme);
		}
		catch(Exception e)
		{
			log.errorTrace(methodName,e);
		}
		LogFactory.printLog(methodName, "Exited", log);
	}

	/**
	 * @param pObjType
	 * @param pObjValue
	 * @return Object it can be diffrent type as Integer,Long,Boolean,Date,
	 *         String ConfigurationCache
	 */
	private static Object getCastedObject(String pObjType, String pObjValue)
	{
		Object obj = null;
		final String methodName = "getCastedObject";
		if (ConfigurationI.TYPE_INTEGER.equals(pObjType))
		{
			obj = new Integer(pObjValue);
		} else if (ConfigurationI.TYPE_LONG.equals(pObjType))
		{
			obj = new Long(pObjValue);
		} 
        else 
        if (ConfigurationI.TYPE_BOOLEAN.equals(pObjType))
		{
			obj = new Boolean(pObjValue);
		} 
        else 
        if (ConfigurationI.TYPE_AMOUNT.equals(pObjType))
        {
            obj = new Long(pObjValue);
        } 
        else 
        if (ConfigurationI.TYPE_DATE.equals(pObjType))
		{
			try {
				obj = BTSLUtil.getDateFromDateString(pObjValue);
			} catch (ParseException e) {				
				log.errorTrace(methodName,e);
			}
		} else if (ConfigurationI.TYPE_STRING.equals(pObjType))
		{
			obj = pObjValue;
		}

		return obj;
	}

	/**
	 * @param dateStr
	 * @return
	 */
	public static Date getSQLDate(String dateStr)
	{
		if ((dateStr == null) || "".equals(dateStr))
		{
			return null;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		Date date = null;
		final String methodName = "getSQLDate";
		try
		{
			date = sdf.parse(dateStr);
		} catch (ParseException e)
		{
			log.errorTrace(methodName,e);
		}
		return new java.sql.Date(date.getTime());
	}
}
