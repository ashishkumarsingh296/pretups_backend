/**
 * @(#)PreferenceCache.java
 *                          Copyright(c) 2005, Bharti Telesoft Ltd.
 *                          All Rights Reserved
 * 
 *                          <description>
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Author Date History
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          avinash.kamthan Mar 16, 2005 Initital Creation
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 * 
 */
package com.btsl.pretups.preference.businesslogic;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.logging.CacheOperationLog;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.redis.pool.RedisConnectionPool;
import com.btsl.redis.util.RedisActivityLog;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheLoader.InvalidCacheLoadException;
import com.google.common.cache.LoadingCache;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * @author avinash.kamthan
 */
public class PreferenceCache implements Runnable{
    private static Log _log = LogFactory.getLog(PreferenceCache.class.getName());
    private static HashMap _preferenceMap = new HashMap();
    private static HashMap _preferenceMapRedisBackup = new HashMap();
    
	public static HashMap get_preferenceMapRedisBackup() {
		return _preferenceMapRedisBackup;
	}
	private static final String CLASS_NAME = "PreferenceCache";
    private static final String hKeyPrefrenceCache = "prefrenceMapCache";
    private static final String hKeyPrefrenceCacheClient = "prefrenceMapCacheClient";
    private static String redisEnable = BTSLUtil.NullToString(Constants.getProperty("REDIS_ENABLE"));
    private static Gson gson = new GsonBuilder()
    		.setDateFormat("yyyy-MM-dd HH:mm:ss:mss")
    		.create();
    private static final int keyTimer = Integer.parseInt(Constants.getProperty("KEYTIMER"));
    public void run() {
        try {
            Thread.sleep(50);
        	loadPrefrencesOnStartUp();
        } catch (Exception e) {
        	 _log.error("PreferenceCache init() Exception ", e);
        }
    }
    private static LoadingCache<String,HashMap<String,PreferenceCacheVO>> prefrenceMemo = CacheBuilder.newBuilder()
    	    .expireAfterWrite(2, TimeUnit.MINUTES)
    	    .build(new CacheLoader<String, HashMap<String,PreferenceCacheVO>>(){
    			@Override
    			public HashMap<String,PreferenceCacheVO> load(String key) throws Exception {
    				return getPreferenceObjectFromRedis(key);
    			}
    	     });
    
    public static void loadPrefrencesOnStartUp() throws BTSLBaseException {

		System.out.println("Inside loadPrefrencesOnStartUp");
    	final String methodName = "loadPrefrencesOnStartUp";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        try{


			System.out.println("Inside loadPrefrencesOnStartUp "+redisEnable);
    	if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
    		   RedisActivityLog.log("PreferenceCache->loadPrefrencesOnStartUp->Start");
    		   Jedis jedis = null;
			 try {
				 jedis = RedisConnectionPool.getPoolInstance().getResource();
 				if(!jedis.exists(hKeyPrefrenceCache)) {
 					HashMap<String, PreferenceCacheVO> prefrencemap = loadPrefrences();
 					_preferenceMap = prefrencemap;
				     jedis.set(hKeyPrefrenceCache, gson.toJson(prefrencemap));
				     Pipeline pipeline = jedis.pipelined();
						if(!jedis.exists(hKeyPrefrenceCacheClient)){
							for (Entry<String, PreferenceCacheVO> entry : prefrencemap.entrySet())  {
								 if(entry.getValue().getValue() == null) {
									 pipeline.hset(hKeyPrefrenceCacheClient,entry.getKey(),"null");
								 }else {
									 pipeline.hset(hKeyPrefrenceCacheClient,entry.getKey(),entry.getValue().getValue());
								 }
		               	    	
							}
							pipeline.sync();
						 }
				 }
 				
			 }catch(JedisConnectionException je){
			 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	   		        _log.errorTrace(methodName, je);
	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceCache[loadPrefrencesOnStartUp]", "", "", "", "JedisConnectionException :" + je.getMessage());
	   		        throw new BTSLBaseException(PreferenceCache.class.getName(), methodName,je.getMessage());
			 }catch(NoSuchElementException  ex){
			 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	   		        _log.errorTrace(methodName, ex);
	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceCache[loadPrefrencesOnStartUp]", "", "", "", "NoSuchElementException :" + ex.getMessage());
	   		        throw new BTSLBaseException(PreferenceCache.class.getName(), methodName,ex.getMessage());
			 }catch (Exception e) {
			 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
			 	_log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceCache[loadPrefrencesOnStartUp]", "", "", "", "Exception :" + e.getMessage());
				throw new BTSLBaseException(PreferenceCache.class.getName(), methodName,e.getMessage());
			 }finally {
  	        	if (jedis != null) {
  	        	jedis.close();
  	        	}
  	        }
			   RedisActivityLog.log("PreferenceCache->loadPrefrencesOnStartUp->End");
    	} else {

			System.out.println("loadPrefrences");
    	      _preferenceMap = loadPrefrences();
    	  }
    	/**
    	 * Making key value pair as per frontend requirement
    	 */
    	filterCacheData();
        }
        catch(BTSLBaseException be) {
        	_log.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
        	_log.errorTrace(methodName, be);
        	throw be;
        }
        catch (Exception e)
        {
        	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
        	_log.errorTrace(methodName, e);
        	throw new BTSLBaseException(CLASS_NAME, methodName, "Exception in loading Prefrences cache on Startup.");
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED);
        	}
        }
    }

    /**
     * To load the Preferences
     * 
     * @return HashMap PreferenceCache
     * @throws Exception 
     */
    private static HashMap<String,PreferenceCacheVO> loadPrefrences() throws BTSLBaseException {
    	final String methodName = "loadPrefrences";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        PreferenceDAO preferenceCacheDAO = new PreferenceDAO();
        HashMap<String,PreferenceCacheVO> map = null;
        try {
			System.out.println("loadPrefrences");
        	  map = preferenceCacheDAO.loadPrefrences();
        }
        catch(BTSLBaseException be) {
        	_log.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
        	_log.errorTrace(methodName, be);
        	throw be;
        }
        catch (Exception e)
        {
        	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
        	_log.errorTrace(methodName, e);
        	throw new BTSLBaseException(CLASS_NAME, methodName, "Exception in loading Prefrences cache.");
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug("loadPrefrences()", PretupsI.EXITED + map.size());
        	}
        }
        return map;
    }

    /**
     * To update the preferences cache void PreferenceCache
     * @throws Exception 
     */
    public static void updatePrefrences() throws BTSLBaseException {
    	final String methodName = "updatePrefrences";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        HashMap<String,PreferenceCacheVO> currentMap;
		try {
			currentMap = loadPrefrences();
		    	if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
		    		 RedisActivityLog.log("PreferenceCache->updatePrefrences->Start");
		    		 Jedis jedis = null;
						 try{
							 jedis  = RedisConnectionPool.getPoolInstance().getResource();
							 Pipeline pipeline = jedis.pipelined();
							 pipeline.del(hKeyPrefrenceCache);
						     pipeline.set(hKeyPrefrenceCache, gson.toJson(currentMap));
						     pipeline.del(hKeyPrefrenceCacheClient);
						     for (Entry<String, PreferenceCacheVO> entry : currentMap.entrySet())  {
								 if(entry.getValue().getValue() == null) {
									 pipeline.hset(hKeyPrefrenceCacheClient,entry.getKey(),"null");
								 }else {
									 pipeline.hset(hKeyPrefrenceCacheClient,entry.getKey(),entry.getValue().getValue());
								 }
		               	    	
							}
							 pipeline.sync();
						 }catch(JedisConnectionException je){
						 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
				   		        _log.errorTrace(methodName, je);
				   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceCache[updatePrefrences]", "", "", "", "JedisConnectionException :" + je.getMessage());
				   		        throw new BTSLBaseException(PreferenceCache.class.getName(), methodName,je.getMessage());
						 }catch(NoSuchElementException  ex){
						 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
				   		        _log.errorTrace(methodName, ex);
				   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceCache[updatePrefrences]", "", "", "", "NoSuchElementException :" + ex.getMessage());
				   		        throw new BTSLBaseException(PreferenceCache.class.getName(), methodName,ex.getMessage());
						 }catch (Exception e) {
						 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
						 	_log.errorTrace(methodName, e);
				            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceCache[updatePrefrences]", "", "", "", "Exception :" + e.getMessage());
							throw new BTSLBaseException(PreferenceCache.class.getName(), methodName,e.getMessage());
						 }finally {
			    	        	if (jedis != null) {
			    	        	jedis.close();
			    	        	}
			    	        }
				     RedisActivityLog.log("PreferenceCache->updatePrefrences->End");
			    	} else {
			    		   if ((_preferenceMap != null) && (_preferenceMap.size() > 0)) {
			    	            compareMaps(_preferenceMap, currentMap);
			    	        }
		    	         _preferenceMap = currentMap;
						}
						// for redis N
						filterCacheData();
					}
		catch(BTSLBaseException be) {
			_log.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
			_log.errorTrace(methodName, be);
			throw be;
		}
		catch (Exception e)
		{
			_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
			_log.errorTrace(methodName, e);
			throw new BTSLBaseException(CLASS_NAME, methodName, "Exception in updating Prefrences cache.");
		} finally {
			if (_log.isDebugEnabled()) {
				_log.debug("updatePrefrences()", PretupsI.EXITED + _preferenceMap.size());  
			}
		}
    }

    /**
     * get the value against the prefrenceCode at system level
     * 
     * @param p_preferenceCode
     * @return String PreferenceCache
     */
    public static String getSystemPreferenceValueAsString(String p_preferenceCode) {
        if (_log.isDebugEnabled()) {
            _log.debug("getSystemPreferenceValue()", "Entered p_preferenceCode: " + p_preferenceCode);
        }
        String methodName = "getSystemPreferenceValue";
        Object value = null;
        PreferenceCacheVO cacheVO = null;
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        	try {
				cacheVO = prefrenceMemo.get(hKeyPrefrenceCache).get(p_preferenceCode);
			} catch (ExecutionException e) {
				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
   		        _log.errorTrace(methodName, e);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceCache[getSystemPreferenceValue]", "", "", "", "ExecutionException :" + e.getMessage());
			}
        	catch (InvalidCacheLoadException e) { 
        		_log.info(methodName,  e.getMessage());	
			}
    	} else {
    		cacheVO = (PreferenceCacheVO) _preferenceMap.get(p_preferenceCode);
        }
       
        if (cacheVO != null) {
            value = getCastedObject(cacheVO.getValueType(), cacheVO.getValue());
        }
        if (_log.isDebugEnabled()) {
            _log.debug("getSystemPreferenceValue()", "Exited value: " + value);
        }
        return String.valueOf(value);
    }

    
    /**
     * get the value against the prefrenceCode at system level
     * 
     * @param p_preferenceCode
     * @return String PreferenceCache
     */
    public static Object getSystemPreferenceValue(String p_preferenceCode) {
        if (_log.isDebugEnabled()) {
            _log.debug("getSystemPreferenceValue()", "Entered p_preferenceCode: " + p_preferenceCode);
        }
        String methodName = "getSystemPreferenceValue";
        Object value = null;
        PreferenceCacheVO cacheVO = null;
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        	try {
				cacheVO = prefrenceMemo.get(hKeyPrefrenceCache).get(p_preferenceCode);
			} catch (ExecutionException e) {
				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
   		        _log.errorTrace(methodName, e);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceCache[getSystemPreferenceValue]", "", "", "", "ExecutionException :" + e.getMessage());
			}
        	catch (InvalidCacheLoadException e) { 
        		_log.info(methodName,  e.getMessage());	
			}
    	} else {
    		cacheVO = (PreferenceCacheVO) _preferenceMap.get(p_preferenceCode);
        }
       
        if (cacheVO != null) {
            value = getCastedObject(cacheVO.getValueType(), cacheVO.getValue());
        }
        if (_log.isDebugEnabled()) {
            _log.debug("getSystemPreferenceValue()", "Exited value: " + value);
        }
        return value;
    }

    /**
     * get the value against the prefrenceCode at Network level. if value is not
     * found at network level then search it at System level
     * 
     * @param p_preferenceCode
     * @param p_networkCode
     * @return String PreferenceCache
     */
    public static Object getNetworkPrefrencesValue(String p_preferenceCode, String p_networkCode) {
        if (_log.isDebugEnabled()) {
            _log.debug("getNetworkPrefrencesValue()", "Entered p_preferenceCode: " + p_preferenceCode + " networkCode " + p_networkCode);
        }
        String methodName = "getNetworkPrefrencesValue";
        PreferenceCacheVO cacheVO = null;
        Object value = null;
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        	try {
				cacheVO =prefrenceMemo.get(hKeyPrefrenceCache).get(p_preferenceCode + ":" + p_networkCode);
			}catch (ExecutionException e) {
				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
   		        _log.errorTrace(methodName, e);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceCache[getNetworkPrefrencesValue]", "", "", "", "ExecutionException :" + e.getMessage());
			}
        	catch (InvalidCacheLoadException e) {
        		_log.info(methodName,  e.getMessage());	
        	}
    	} else {
    		cacheVO = (PreferenceCacheVO) _preferenceMap.get(p_preferenceCode + ":" + p_networkCode);
        }

        // if prefrence is not found at network level then try to find it at
        // system level
        if (cacheVO == null) {
            value = getSystemPreferenceValue(p_preferenceCode);
        } else {
            value = getCastedObject(cacheVO.getValueType(), cacheVO.getValue());
        }
        if (_log.isDebugEnabled()) {
            _log.debug("getNetworkPrefrencesValue()", "Exited value: " + value);
        }
        return value;
    }

    /**
     * get the value against the prefrenceCode at Zone level. if value is not
     * found at network level then search it at System level
     * 
     * @param p_preferenceCode
     * @param p_networkCode
     * @param p_controlCode
     * @return String PreferenceCache
     */
    public static Object getControlPreference(String p_preferenceCode, String p_networkCode, String p_controlCode) {
        if (_log.isDebugEnabled()) {
            _log.debug("getControlPreference()", "Entered p_preferenceCode: " + p_preferenceCode + " networkCode " + p_networkCode + " p_controlCode: " + p_controlCode);
        }
        String methodName = "getControlPreference";
        PreferenceCacheVO cacheVO = null;
        Object value = null;
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        	try {
				cacheVO =prefrenceMemo.get(hKeyPrefrenceCache).get(p_preferenceCode + ":" + p_networkCode + ":" + p_controlCode);
			} catch (ExecutionException e) {
				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
   		        _log.errorTrace(methodName, e);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceCache[getControlPreference]", "", "", "", "ExecutionException :" + e.getMessage());
			}catch (InvalidCacheLoadException e) {
				_log.info(methodName,  e.getMessage());	
			}
        	
    	} else {
    	     cacheVO = (PreferenceCacheVO) _preferenceMap.get(p_preferenceCode + ":" + p_networkCode + ":" + p_controlCode);
        }
        if (cacheVO == null) {
            value = getSystemPreferenceValue(p_preferenceCode);
        } else {
            value = getCastedObject(cacheVO.getValueType(), cacheVO.getValue());
        }
        if (_log.isDebugEnabled()) {
            _log.debug("getControlPreference()", "Exited value=" + value);
        }
        return value;
    }

    /**
     * get the value against the prefrenceCode at Service level. if value is not
     * found at network level then search it at System level
     * 
     * @param p_preferenceCode
     * @param p_networkCode
     * @param p_zoneCode
     * @param p_module
     * @param p_serviceCode
     * @return String PreferenceCache
     */
    public static Object getServicePreference(String p_preferenceCode, String p_networkCode, String p_module, String p_serviceCode) {
        if (_log.isDebugEnabled()) {
            _log.debug("getServicePreference()", "Entered p_preferenceCode : " + p_preferenceCode + " p_networkCode: " + p_networkCode + "  p_serviceCode: " + p_serviceCode + " p_module: " + p_module);
        }
        Object value = null;
        value = getServicePreference(p_preferenceCode, p_networkCode, p_module, p_serviceCode, true);
        if (_log.isDebugEnabled()) {
            _log.debug("getServicePreference()", "Exited value: " + value);
        }
        return value;
    }

    /**
     * 
     * @param p_preferenceCode
     * @param p_networkCode
     * @param p_module
     * @param p_serviceCode
     * @param systemDefaultValueRequired
     *            true then find the vlaue at System level other wise just check
     *            the value at service level
     * @return
     */
    public static Object getServicePreference(String p_preferenceCode, String p_networkCode, String p_module, String p_serviceCode, boolean systemDefaultValueRequired) {
        if (_log.isDebugEnabled()) {
            _log.debug("getServicePreference()", "Entered p_preferenceCode: " + p_preferenceCode + " p_networkCode: " + p_networkCode + "  p_serviceCode: " + p_serviceCode + " p_module: " + p_module + " systemDefaultValueRequired: " + systemDefaultValueRequired);
        }
        String methodName = "getServicePreference";
        PreferenceCacheVO cacheVO = null;
        Object value = null;
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        	try {
				cacheVO =prefrenceMemo.get(hKeyPrefrenceCache).get(p_preferenceCode + ":" + p_networkCode + ":" + p_module + ":" + p_serviceCode);

			}  catch (ExecutionException e) {
				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
   		        _log.errorTrace(methodName, e);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceCache[getServicePreference]", "", "", "", "ExecutionException :" + e.getMessage());
			}catch (InvalidCacheLoadException e) {
				_log.info(methodName,  e.getMessage());	
			}
    	} else {
            cacheVO = (PreferenceCacheVO) _preferenceMap.get(p_preferenceCode + ":" + p_networkCode + ":" + p_module + ":" + p_serviceCode);
        }
        
        
        if (cacheVO == null) {
            if (systemDefaultValueRequired) {
                value = getSystemPreferenceValue(p_preferenceCode);
            }
        } else {
            value = getCastedObject(cacheVO.getValueType(), cacheVO.getValue());
        }

        if (_log.isDebugEnabled()) {
            _log.debug("getServicePreference()", "Exited value: " + value);
        }
        return value;
    }

    /**
     * compare two hashmap and check which have changed and log the value which
     * has been changed
     * 
     * @param p_previousMap
     * @param p_currentMap
     *            void
     */
    private static void compareMaps(HashMap p_previousMap, HashMap p_currentMap) {
        if (_log.isDebugEnabled()) {
            _log.debug("compareMaps()", "Entered p_previousMap " + p_previousMap + "  p_currentMap: " + p_currentMap);
        }
        final String METHOD_NAME = "compareMaps";
        try {
            Iterator iterator = null;
            Iterator copiedIterator = null;
            if (p_previousMap.size() == p_currentMap.size()) {
                iterator = p_previousMap.keySet().iterator();
                copiedIterator = p_previousMap.keySet().iterator();
            } else if (p_previousMap.size() > p_currentMap.size()) {
                iterator = p_previousMap.keySet().iterator();
                copiedIterator = p_previousMap.keySet().iterator();
            } else if (p_previousMap.size() < p_currentMap.size()) {
                iterator = p_currentMap.keySet().iterator();
                copiedIterator = p_previousMap.keySet().iterator();
            }

            // to check whether any new network added or not but size of
            boolean isNewAdded = false;

            while (iterator != null && iterator.hasNext()) {
                String key = (String) iterator.next();
                PreferenceCacheVO prevPreferenceCacheVO = (PreferenceCacheVO) p_previousMap.get(key);
                PreferenceCacheVO curPreferenceCacheVO = (PreferenceCacheVO) p_currentMap.get(key);

                if ((prevPreferenceCacheVO != null) && (curPreferenceCacheVO == null)) {
                    isNewAdded = true;
                    CacheOperationLog.log("PreferenceCache", BTSLUtil.formatMessage("Delete", prevPreferenceCacheVO.getPreferenceLevel(), prevPreferenceCacheVO.logInfo()));
                } else if ((prevPreferenceCacheVO == null) && (curPreferenceCacheVO != null)) {
                    CacheOperationLog.log("PreferenceCache", BTSLUtil.formatMessage("Add", curPreferenceCacheVO.getPreferenceLevel(), curPreferenceCacheVO.logInfo()));
                } else if ((prevPreferenceCacheVO != null) && (curPreferenceCacheVO != null)) {
                    if (!curPreferenceCacheVO.equalsPreferenceCacheVO(prevPreferenceCacheVO)) {
                        CacheOperationLog.log("PreferenceCache", BTSLUtil.formatMessage("Modify", curPreferenceCacheVO.getPreferenceLevel(), curPreferenceCacheVO.differences(prevPreferenceCacheVO)));
                    }
                }
            }

            /**
             * Note: this case arises when same number of network added and
             * deleted
             * as well
             */
            if ((p_previousMap.size() == p_currentMap.size()) && isNewAdded) {
                HashMap tempMap = new HashMap(p_currentMap);

                while (copiedIterator.hasNext()) {
                    tempMap.remove((String) copiedIterator.next());
                }

                Iterator iterator2 = tempMap.keySet().iterator();

                while (iterator2.hasNext()) {
                    // new network added
                    PreferenceCacheVO preferenceCacheVO = (PreferenceCacheVO) p_currentMap.get(iterator2.next());
                    CacheOperationLog.log("PreferenceCache", BTSLUtil.formatMessage("Add", preferenceCacheVO.getPreferenceLevel(), preferenceCacheVO.logInfo()));
                }
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("compareMaps()", "Exited");
        }
    }

    /**
     * @param p_objType
     * @param p_objValue
     * @return Object it can be diffrent type as Integer,Long,Boolean,Date,
     *         String PreferenceCache
     */
    private static Object getCastedObject(String p_objType, String p_objValue) {
        Object obj = null;
        final String METHOD_NAME = "getCastedObject";
        if (PreferenceI.TYPE_INTEGER.equals(p_objType)) {
            obj = new Integer(p_objValue);
        } else if (PreferenceI.TYPE_LONG.equals(p_objType)) {
            obj = new Long(p_objValue);
        } else if (PreferenceI.TYPE_BOOLEAN.equals(p_objType)) {
            obj = new Boolean(p_objValue);
        } else if (PreferenceI.TYPE_AMOUNT.equals(p_objType)) {
            obj = new Long(p_objValue);
        }
        else if (PreferenceI.TYPE_DOUBLE.equals(p_objType)) {
        	obj = new Double(p_objValue);
        }
        else if (PreferenceI.TYPE_DATE.equals(p_objType)) {
            try {
                obj = BTSLUtil.getDateFromDateString(p_objValue);
            } catch (ParseException e) {
                _log.errorTrace(METHOD_NAME, e);
            }
        } else if (PreferenceI.TYPE_STRING.equals(p_objType)) {
            obj = p_objValue;
        }

        return obj;
    }

    public static Date getSQLDate(String dateStr) {
        if ((dateStr == null) || dateStr.equals("")) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date date = null;
        final String METHOD_NAME = "getSQLDate";
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        return new java.sql.Date(date.getTime());
    }

    public static Object getServicePreferenceObject(String p_preferenceCode, String p_networkCode, String p_module, String p_serviceCode, boolean systemDefaultValueRequired) {
        if (_log.isDebugEnabled()) {
            _log.debug("getServicePreferenceObject()", "Entered p_preferenceCode: " + p_preferenceCode + " p_networkCode: " + p_networkCode + "  p_serviceCode: " + p_serviceCode + " p_module: " + p_module + " systemDefaultValueRequired " + systemDefaultValueRequired);
        }
        String methodName ="getServicePreferenceObject";
        PreferenceCacheVO cacheVO = null;
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        	try {
				cacheVO =prefrenceMemo.get(hKeyPrefrenceCache).get(p_preferenceCode + ":" + p_networkCode + ":" + p_module + ":" + p_serviceCode);
			}catch (ExecutionException e) {
				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
   		        _log.errorTrace(methodName, e);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceCache[getServicePreferenceObject]", "", "", "", "ExecutionException :" + e.getMessage());
			}catch (InvalidCacheLoadException e) {
				_log.info(methodName,  e.getMessage());	
			}
        } else {
           cacheVO = (PreferenceCacheVO) _preferenceMap.get(p_preferenceCode + ":" + p_networkCode + ":" + p_module + ":" + p_serviceCode);
   	   }
        return cacheVO;
    }
    
    public static HashMap<String,PreferenceCacheVO> getPreferenceObjectFromRedis(String key) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("getPreferenceObjectFromRedis()", "Entered p_preferenceCode: " + key);
        }
        String methodName = "getPreferenceObjectFromRedis";
    	Jedis jedis = null;
    	HashMap<String,PreferenceCacheVO> prefCache=new HashMap<String,PreferenceCacheVO>();
    	PreferenceCacheVO cacheVO = null;
		 try {
		     RedisActivityLog.log("PreferenceCache->getPreferenceObjectFromRedis->Start");
			 jedis = RedisConnectionPool.getPoolInstance().getResource();
			 String json = jedis.get(key);
			 Type classType = new TypeToken<HashMap<String,PreferenceCacheVO>>() {}.getType();
			 if(json != null)
				 prefCache=gson.fromJson(json, classType);
			 RedisActivityLog.log("PreferenceCache->getPreferenceObjectFromRedis->End");
		 }catch(JedisConnectionException je){
		 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
   		        _log.errorTrace(methodName, je);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceCache[getPreferenceObjectFromRedis]", "", "", "", "JedisConnectionException :" + je.getMessage());
   		        throw new BTSLBaseException(PreferenceCache.class.getName(), methodName,je.getMessage());
		 }catch(NoSuchElementException  ex){
		 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
   		        _log.errorTrace(methodName, ex);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceCache[getPreferenceObjectFromRedis]", "", "", "", "NoSuchElementException :" + ex.getMessage());
   		        throw new BTSLBaseException(PreferenceCache.class.getName(), methodName,ex.getMessage());
		 }catch (Exception e) {
		 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
		 	_log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceCache[getPreferenceObjectFromRedis]", "", "", "", "Exception :" + e.getMessage());
			throw new BTSLBaseException(PreferenceCache.class.getName(), methodName,e.getMessage());
		 }
		 finally {
	        	if (jedis != null) {
	        	jedis.close();
	        	}
	        }
      return prefCache;
    }
    
    private static void filterCacheData() {
    	_preferenceMap.forEach((key,value) -> {
    		_preferenceMapRedisBackup.put(key, ((PreferenceCacheVO)value).getValue());
    	});
    }
   
}
