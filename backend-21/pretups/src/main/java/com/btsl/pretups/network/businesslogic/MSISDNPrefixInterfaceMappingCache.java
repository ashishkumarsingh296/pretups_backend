/**
 * @(#)MSISDNPrefixInterfaceMappingCache.java
 *                                            Copyright(c) 2005, Bharti Telesoft
 *                                            Ltd.
 *                                            All Rights Reserved
 * 
 *                                            <description>
 *                                            ----------------------------------
 *                                            ----------------------------------
 *                                            -----------------------------
 *                                            Author Date History
 *                                            ----------------------------------
 *                                            ----------------------------------
 *                                            -----------------------------
 *                                            avinash.kamthan June 22, 2005
 *                                            Initital Creation
 *                                            ----------------------------------
 *                                            ----------------------------------
 *                                            -----------------------------
 * 
 */

package com.btsl.pretups.network.businesslogic;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
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
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.logging.CacheOperationLog;
import com.btsl.redis.pool.RedisConnectionPool;
import com.btsl.redis.util.RedisActivityLog;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheLoader.InvalidCacheLoadException;
import com.google.common.cache.LoadingCache;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * @author avinash.kamthan
 * 
 */
public class MSISDNPrefixInterfaceMappingCache implements Runnable {

    private static Log _log = LogFactory.getLog(MSISDNPrefixInterfaceMappingCache.class.getName());
    private static final String CLASS_NAME = "MSISDNPrefixInterfaceMappingCache";
    private static HashMap<String,MSISDNPrefixInterfaceMappingVO> _prefixIfMap = new HashMap<String,MSISDNPrefixInterfaceMappingVO>();
    private static HashMap<String,MSISDNPrefixInterfaceMappingVO> _interfaceIDMap = new HashMap<String,MSISDNPrefixInterfaceMappingVO>();
    private static final String hKeyPrefixIDMap = "PrefixIDMap";
    private static final String hKeyInterfaceIDMap = "InterfaceIDMap";
    private static String redisEnable = BTSLUtil.NullToString(Constants.getProperty("REDIS_ENABLE"));
    private static int keyTimer = Integer.parseInt(Constants.getProperty("KEYTIMER"));
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss:mss").create();
    
    public void run() {
		try {
			Thread.sleep(50);
			loadPrefixInterfaceMappingAtStartup();
		} catch (Exception e) {
			_log.error("loadPrefixInterfaceMappingAtStartup init() Exception ", e);
		}
	}
    
    private static LoadingCache<String,MSISDNPrefixInterfaceMappingVO>  prefixIdCacheMap = CacheBuilder.newBuilder()
    	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)     
    	    .build(new CacheLoader<String, MSISDNPrefixInterfaceMappingVO>(){
    			@Override
    			public MSISDNPrefixInterfaceMappingVO load(String key) throws Exception {
    				return getPrefixIfMapFromRedis(key);
    			}
    	     });
    
    private static LoadingCache<String,MSISDNPrefixInterfaceMappingVO>  interfaceIdCacheMap = CacheBuilder.newBuilder()
    	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)      
    	    .build(new CacheLoader<String, MSISDNPrefixInterfaceMappingVO>(){
    			@Override
    			public MSISDNPrefixInterfaceMappingVO load(String key) throws Exception {
    				return getInterfaceIdMapFromRedis(key);
    			}
    	     });
    
    /**
     * Load the Interface and network refixes mapping on start up
     * @throws Exception 
     * 
     */
    public static void loadPrefixInterfaceMappingAtStartup() throws BTSLBaseException {
    	final String methodName = "loadPrefixInterfaceMappingAtStartup";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        try{
        	if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        	Jedis jedis =null;
            RedisActivityLog.log("MSISDNPrefixInterfaceMappingCache->loadPrefixInterfaceMappingAtStartup->Start");
   			 try {
   				jedis = RedisConnectionPool.getPoolInstance().getResource();
   				//If key is already present in redis db do not reload
				if(!jedis.exists(hKeyPrefixIDMap)) {
	   				Pipeline pipeline = jedis.pipelined();
   					HashMap<String,MSISDNPrefixInterfaceMappingVO> prefixIdMap = loadMapping();
   					 for (Entry<String, MSISDNPrefixInterfaceMappingVO> entry : prefixIdMap.entrySet())  {
   						pipeline.hset(hKeyPrefixIDMap,entry.getKey(),gson.toJson(entry.getValue()));
	   				    if (_log.isDebugEnabled()) {
		   	                _log.debug("loadPrefixInterfaceMappingAtStartup", "MSISDNPrefixInterfaceMappingCache :"+entry.getKey()+" is loaded into Redis Cache ");
		   	             }
   					 }
   					pipeline.sync();
				}
    				
				//If key is already present in redis db do not reload
				if(!jedis.exists(hKeyInterfaceIDMap)) {
	   				Pipeline pipeline = jedis.pipelined();
					HashMap<String,MSISDNPrefixInterfaceMappingVO> interfaceIDMap = loadInterfaceIDMapping();
   					for (Entry<String, MSISDNPrefixInterfaceMappingVO> entry : interfaceIDMap.entrySet())  {
   					pipeline.hset(hKeyInterfaceIDMap,entry.getKey(),gson.toJson(entry.getValue()));
	   	   				if (_log.isDebugEnabled()) {
		   	                _log.debug("loadPrefixInterfaceMappingAtStartup", "MSISDNPrefixInterfaceMappingCache :"+entry.getKey()+" is loaded into Redis Cache");
		   	             }
				 	}
   					pipeline.sync();
				}
    		    }catch(JedisConnectionException je){
					_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	   		        _log.errorTrace(methodName, je);
	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MSISDNPrefixInterfaceMappingCache[loadPrefixInterfaceMappingAtStartup]", "", "", "", "JedisConnectionException :" + je.getMessage());
	   	            throw new BTSLBaseException(MSISDNPrefixInterfaceMappingCache.class.getName(), methodName,je.getMessage());
				 }catch(NoSuchElementException  ex){
						_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
		   		        _log.errorTrace(methodName, ex);
		   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MSISDNPrefixInterfaceMappingCache[loadPrefixInterfaceMappingAtStartup]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		   		        throw new BTSLBaseException(MSISDNPrefixInterfaceMappingCache.class.getName(), methodName,ex.getMessage());
				 }catch (Exception e) {
					   _log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
					   _log.errorTrace(methodName, e);
		               EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MSISDNPrefixInterfaceMappingCache[loadPrefixInterfaceMappingAtStartup]", "", "", "", "Exception :" + e.getMessage());
					throw new BTSLBaseException(MSISDNPrefixInterfaceMappingCache.class.getName(), methodName,e.getMessage());
		    	 }finally {
    	        	if (jedis != null) {
    	        	jedis.close();
    	        	}
    	        }
             RedisActivityLog.log("MSISDNPrefixInterfaceMappingCache->loadPrefixInterfaceMappingAtStartup->End");
   			 } else { 
       		_prefixIfMap = loadMapping();
            _interfaceIDMap = loadInterfaceIDMapping();
       	  }
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
        	throw new BTSLBaseException(CLASS_NAME, methodName, "Exception in loading the MSISDN Prefixes and Interfaces Mapping details on Startup.",e);
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED);
        	}
        }
    }

    /**
     * To load the MSISDN Prefixes and Interfaces Mapping details
     * 
     * @return
     *         HashMap
     *         NetworkCache
     * @throws Exception 
     */
    private static HashMap<String,MSISDNPrefixInterfaceMappingVO> loadMapping() throws BTSLBaseException {
    	final String methodName = "loadMapping";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        NetworkDAO networkDAO = new NetworkDAO();
        HashMap<String,MSISDNPrefixInterfaceMappingVO> map = null;
        try {
            map = networkDAO.loadMSISDNInterfaceMappingCache();
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
        	throw new BTSLBaseException(CLASS_NAME, methodName, "Exception in loading the MSISDN Prefixes and Interfaces Mapping details.",e);
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED);
        	}
        }
        return map;
    }

    /**
     * to update the cache
     * 
     * void
     * NetworkCache
     * @throws Exception 
     */
    public static void updatePrefixInterfaceMapping() throws BTSLBaseException {
    	final String methodName = "updatePrefixInterfaceMapping";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        try{
        HashMap<String,MSISDNPrefixInterfaceMappingVO> currentMap = loadMapping();
        HashMap<String,MSISDNPrefixInterfaceMappingVO> currentMap2 = loadInterfaceIDMapping();
	    	if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
            	RedisActivityLog.log("MSISDNPrefixInterfaceMappingCache->updatePrefixInterfaceMapping->Start");
            	Jedis jedis = null; 
            	try {
					jedis = RedisConnectionPool.getPoolInstance().getResource();
					Pipeline pipeline = jedis.pipelined();
					pipeline.del(hKeyPrefixIDMap);//deleting the old data 
					for (Entry<String,MSISDNPrefixInterfaceMappingVO> entry : currentMap.entrySet())  {
						pipeline.hset(hKeyPrefixIDMap,entry.getKey(),gson.toJson(entry.getValue()));
				    }
					pipeline.del(hKeyInterfaceIDMap);//deleting the old data
					for (Entry<String,MSISDNPrefixInterfaceMappingVO> entry : currentMap2.entrySet())  {
					      pipeline.hset(hKeyInterfaceIDMap,entry.getKey(),gson.toJson(entry.getValue()));
						}
				  pipeline.sync();
			      RedisActivityLog.log("MSISDNPrefixInterfaceMappingCache->updatePrefixInterfaceMapping->End");
            	}catch(JedisConnectionException je){
						_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
		   		        _log.errorTrace(methodName, je);
		   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MSISDNPrefixInterfaceMappingCache[updatePrefixInterfaceMapping]", "", "", "", "JedisConnectionException :" + je.getMessage());
		   	            throw new BTSLBaseException(MSISDNPrefixInterfaceMappingCache.class.getName(), methodName,je.getMessage());
				  }catch(NoSuchElementException  ex){
						_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
		   		        _log.errorTrace(methodName, ex);
		   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MSISDNPrefixInterfaceMappingCache[updatePrefixInterfaceMapping]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		   		        throw new BTSLBaseException(MSISDNPrefixInterfaceMappingCache.class.getName(), methodName,ex.getMessage());
				   }catch (Exception e) {
					   _log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
					   _log.errorTrace(methodName, e);
		                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MSISDNPrefixInterfaceMappingCache[updatePrefixInterfaceMapping]", "", "", "", "Exception :" + e.getMessage());
					    throw new BTSLBaseException(MSISDNPrefixInterfaceMappingCache.class.getName(), methodName,e.getMessage());
			      }finally {
	    	        	if (jedis != null) {
	    	        	jedis.close();
	    	        	}
	    	        }
		    } else {
	    		 if (_prefixIfMap != null && _prefixIfMap.size() > 0) {
	    	            compareMaps(_prefixIfMap, currentMap);
	    	        }
	
	    	        _prefixIfMap = currentMap;
	    	        _interfaceIDMap = currentMap2;
	    	}
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
        	throw new BTSLBaseException(CLASS_NAME, methodName, "Exception in updating the MSISDN Prefixes and Interfaces Mapping details.",e);
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED + _prefixIfMap.size());
        	}
        }
    }

    /**
     * Returns the MSISDNPrefixInterfaceMappingVO which have interface ID
     * 
     * @param p_prefixId
     * @param p_interfaceType
     * @param p_action
     * @return MSISDNPrefixInterfaceMappingVO
     */
    public static Object getObject(long p_prefixId, String p_interfaceType, String p_action) throws BTSLBaseException {
        MSISDNPrefixInterfaceMappingVO mappingVO = null;

        if (_log.isDebugEnabled()) {
            _log.debug("getObject()", "Entered   p_prefixId: " + p_prefixId + " p_interfaceType: " + p_interfaceType + " p_action: " + p_action);
        }
        String key = p_prefixId + "_" + p_interfaceType + "_" + p_action;
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        	try {
              	mappingVO = prefixIdCacheMap.get(key);
  			} catch (ExecutionException e) {
  				_log.error("getObject", PretupsI.EXCEPTION + e.getMessage());
   		        _log.errorTrace("getObject", e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MSISDNPrefixInterfaceMappingCache[getObject]", "", "", "", "Exception :" + e.getMessage());
  			}catch (InvalidCacheLoadException e) { 
  				_log.error("getObject", PretupsI.EXCEPTION + e.getMessage());
  			     _log.errorTrace("getObject", e);
  		        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MSISDNPrefixInterfaceMappingCache[getObject]", "", "", "", "InvalidCacheLoadException :" + e.getMessage());	
  	        }
        } else {
            mappingVO = (MSISDNPrefixInterfaceMappingVO) _prefixIfMap.get(key);
        }
        if (mappingVO == null) {
            throw new BTSLBaseException(MSISDNPrefixInterfaceMappingCache.class.getName(), "getObject", PretupsErrorCodesI.MSISDN_PREFIX_INTERFACE_MAPPING_NOTFOUND);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("getObject()", "Exited " + mappingVO);
        }
        return mappingVO;
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
            _log.debug("compareMaps()", "Entered PreviousMap " + p_previousMap + "  Current Map" + p_currentMap);
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

            boolean isNewAdded = false;
            while (iterator != null && iterator.hasNext()) {
                String key = (String) iterator.next();
                MSISDNPrefixInterfaceMappingVO prevMappingVO = (MSISDNPrefixInterfaceMappingVO) p_previousMap.get(key);
                MSISDNPrefixInterfaceMappingVO curMappingVO = (MSISDNPrefixInterfaceMappingVO) p_currentMap.get(key);

                if (prevMappingVO != null && curMappingVO == null) {
                    isNewAdded = true;
                    CacheOperationLog.log("MSISDNPrefixInterfaceMappingCache", BTSLUtil.formatMessage("Delete", prevMappingVO.getNetworkCode(), prevMappingVO.logInfo()));
                } else if (prevMappingVO == null && curMappingVO != null) {
                    CacheOperationLog.log("MSISDNPrefixInterfaceMappingCache", BTSLUtil.formatMessage("Add", curMappingVO.getNetworkCode(), curMappingVO.logInfo()));
                }
            }

            /**
             * Note: this case arises when same number of element added and
             * deleted as well
             */
            if (p_previousMap.size() == p_currentMap.size() && isNewAdded) {
                HashMap tempMap = new HashMap(p_currentMap);
                while (copiedIterator.hasNext()) {
                    tempMap.remove((String) copiedIterator.next());
                }

                Iterator iterator2 = tempMap.keySet().iterator();
                while (iterator2.hasNext()) {
                    MSISDNPrefixInterfaceMappingVO mappingVO = (MSISDNPrefixInterfaceMappingVO) p_currentMap.get(iterator2.next());
                    CacheOperationLog.log("MSISDNPrefixInterfaceMappingCache", BTSLUtil.formatMessage("Add", mappingVO.getNetworkCode(), mappingVO.logInfo()));
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
     * To load the MSISDN Prefixes and Interfaces Mapping details
     * 
     * @return
     *         HashMap
     *         NetworkCache
     * @throws Exception 
     */
    private static HashMap<String,MSISDNPrefixInterfaceMappingVO> loadInterfaceIDMapping() throws BTSLBaseException {
    	final String methodName = "loadInterfaceIDMapping";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        NetworkDAO networkDAO = new NetworkDAO();
        HashMap<String,MSISDNPrefixInterfaceMappingVO> map = null;
        try {
            map = networkDAO.loadMSISDNInterfaceMappingCacheWithInterfaceID();
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
        	throw new BTSLBaseException(CLASS_NAME, methodName, "Exception in loading the MSISDN Prefixes and Interfaces Mapping details.",e);
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED);
        	}
        }
        return map;
    }

    public static Object getObject(String p_interfaceID) throws BTSLBaseException {
        MSISDNPrefixInterfaceMappingVO mappingVO = null;
        if (_log.isDebugEnabled()) {
            _log.debug("getObject()", "Entered   p_interfaceID: " + p_interfaceID);
        }

        String key = p_interfaceID;
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
			 try {
		    	   mappingVO = interfaceIdCacheMap.get(key);
			 }catch(ExecutionException e){
				 _log.error("getObject", PretupsI.EXCEPTION + e.getMessage());
	   		     _log.errorTrace("getObject", e);
	              EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MSISDNPrefixInterfaceMappingCache[getObject]", "", "", "", "Exception :" + e.getMessage());
			 }catch (InvalidCacheLoadException e) { 
	  				_log.error("getObject", PretupsI.EXCEPTION + e.getMessage());
	  			     _log.errorTrace("getObject", e);
	  		        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MSISDNPrefixInterfaceMappingCache[getObject]", "", "", "", "InvalidCacheLoadException :" + e.getMessage());	
	  	        }
       } else {
    	   mappingVO = (MSISDNPrefixInterfaceMappingVO) _interfaceIDMap.get(key);
       }
        if (mappingVO == null) {
            throw new BTSLBaseException(MSISDNPrefixInterfaceMappingCache.class.getName(), "getObject", PretupsErrorCodesI.MSISDN_PREFIX_INTERFACE_MAPPING_NOTFOUND);
        }

        if (_log.isDebugEnabled()) {
            _log.debug("getObject()", "Exited " + mappingVO);
        }
        return mappingVO;
    }
    
    /**
     * @param key
     * @return
     */
    public static MSISDNPrefixInterfaceMappingVO getPrefixIfMapFromRedis(String key){
        String methodName="getPrefixIfMapFromRedis";
    	if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered key: "+key);
        }
        MSISDNPrefixInterfaceMappingVO mappingVO = null;
        Jedis jedis = null;
		 try {
	        RedisActivityLog.log("MSISDNPrefixInterfaceMappingCache->getPrefixIfMapFromRedis->Start");
			jedis = RedisConnectionPool.getPoolInstance().getResource();
			String json = jedis.hget(hKeyPrefixIDMap,key);
			if(json != null)
			  mappingVO=gson.fromJson(json, MSISDNPrefixInterfaceMappingVO.class);
	        RedisActivityLog.log("MSISDNPrefixInterfaceMappingCache->getPrefixIfMapFromRedis->End");
		 }catch(JedisConnectionException je){
				_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
   		        _log.errorTrace(methodName, je);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MSISDNPrefixInterfaceMappingCache[getPrefixIfMapFromRedis]", "", "", "", "JedisConnectionException :" + je.getMessage());
		  }catch(NoSuchElementException  ex){
				_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
   		        _log.errorTrace(methodName, ex);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MSISDNPrefixInterfaceMappingCache[getPrefixIfMapFromRedis]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		   }catch (Exception e) {
			   _log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
			   _log.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MSISDNPrefixInterfaceMappingCache[getPrefixIfMapFromRedis]", "", "", "", "Exception :" + e.getMessage());
	      }finally {
    		 if(jedis != null ) {
    			 jedis.close();
    		 }
    	   }
		 return mappingVO;
		 }

    public static MSISDNPrefixInterfaceMappingVO getInterfaceIdMapFromRedis(String key) {
        if (_log.isDebugEnabled()) {
            _log.debug("getInterfaceIdMapFromRedis()", "Entered p_serviceTypes: "+key);
        }
        String methodName="getInterfaceIdMapFromRedis";
        MSISDNPrefixInterfaceMappingVO mappingVO = null;
        Jedis jedis = null;
		 try {
		        RedisActivityLog.log("MSISDNPrefixInterfaceMappingCache->getInterfaceIdMapFromRedis->Start");
			jedis = RedisConnectionPool.getPoolInstance().getResource();
			String json = jedis.hget(hKeyInterfaceIDMap,key);
			if(json != null)
			  mappingVO=gson.fromJson(json, MSISDNPrefixInterfaceMappingVO.class);
	        RedisActivityLog.log("MSISDNPrefixInterfaceMappingCache->getInterfaceIdMapFromRedis->End");
		 } catch(JedisConnectionException je){
				_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
   		        _log.errorTrace(methodName, je);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MSISDNPrefixInterfaceMappingCache[getInterfaceIdMapFromRedis]", "", "", "", "JedisConnectionException :" + je.getMessage());
		  }catch(NoSuchElementException  ex){
				_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
   		        _log.errorTrace(methodName, ex);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MSISDNPrefixInterfaceMappingCache[getInterfaceIdMapFromRedis]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		   }catch (Exception e) {
			   _log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
			   _log.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MSISDNPrefixInterfaceMappingCache[getInterfaceIdMapFromRedis]", "", "", "", "Exception :" + e.getMessage());
	      }finally {
    		 if(jedis != null ) {
    			 jedis.close();
    		 }
    	   }
		 return mappingVO;
		 }
}
