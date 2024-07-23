package com.btsl.pretups.master.businesslogic;

/*
 * NetworkServicesCache.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 18/09/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Class to cache the Network Service List
 */

import java.lang.reflect.Type;
import java.util.ArrayList;
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

public class NetworkServicesCache implements Runnable {

    private static Log _log = LogFactory.getLog(NetworkServicesCache.class.getName());
    private static HashMap _networkServiceMap = new HashMap();
    private static NetworkServiceDAO _networkServiceDAO = new NetworkServiceDAO();
    private static String hKey = "NetworkServicesCache";
    private static String redisEnable = BTSLUtil.NullToString(Constants.getProperty("REDIS_ENABLE"));
    private static final int keyTimer = Integer.parseInt(Constants.getProperty("KEYTIMER"));
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss:mss").create();

    private static LoadingCache<String,HashMap<String, NetworkServiceVO>>  networkServiceCacheMap = CacheBuilder.newBuilder()
	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)     
	    .build(new CacheLoader<String, HashMap<String, NetworkServiceVO>>(){
			@Override
			public HashMap<String, NetworkServiceVO> load(String key) throws Exception {
				return getNetworServiceFromRedis(key);
			}
	     });
    
    public void run() {
 		try {
 			Thread.sleep(50);
 			loadNetworkServicesListAtStartUp();
 		} catch (Exception e) {
 			_log.error("refreshNetworkServicesList init() Exception ", e);
 		}
 	}
    /**
     * Gets the network service list
     * @throws Exception 
     * 
     */
    public static void loadNetworkServicesListAtStartUp() throws BTSLBaseException {
    	final String methodName = "loadNetworkServicesListAtStartUp";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        try {
            if (_log.isDebugEnabled()) {
                _log.debug("loadNetworkServicesListAtStartUp", " Before loading:" + _networkServiceMap);
            }
           
            if(PretupsI.REDIS_ENABLE.equals(redisEnable)){
           	 RedisActivityLog.log("NetworkServicesCache->loadNetworkServicesListAtStartUp->Start");
           	 Jedis jedis = null;
       	   	try {
        	    jedis = RedisConnectionPool.getPoolInstance().getResource();
        		//If key is already present in redis db do not reload
				if(!jedis.exists(hKey)) {
				 HashMap<String, NetworkVO> tempMap = _networkServiceDAO.loadNetworkServicesList();
				 jedis.set(hKey, gson.toJson(tempMap));
			    }
	       	   	RedisActivityLog.log("NetworkServicesCache->loadNetworkServicesListAtStartUp->End");
			}catch(JedisConnectionException je){
				_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
   		        _log.errorTrace(methodName, je);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkServicesCache[loadNetworkServicesListAtStartUp]", "", "", "", "JedisConnectionException :" + je.getMessage());
   		        throw new BTSLBaseException(NetworkServicesCache.class.getName(), methodName,je.getMessage());
			}catch(NoSuchElementException  ex){
				_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
   		        _log.errorTrace(methodName, ex);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkServicesCache[loadNetworkServicesListAtStartUp]", "", "", "", "NoSuchElementException :" + ex.getMessage());
   		        throw new BTSLBaseException(NetworkServicesCache.class.getName(), methodName,ex.getMessage());
			}catch (Exception e){
				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
   		        _log.errorTrace(methodName, e);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkServicesCache[loadNetworkServicesListAtStartUp]", "", "", "", "Exception :" + e.getMessage());
   		        throw new BTSLBaseException(NetworkServicesCache.class.getName(), methodName,e.getMessage());
			}finally{
				if(jedis != null)
					jedis.close();
			}
            }  else {
		            _networkServiceMap = _networkServiceDAO.loadNetworkServicesList();
	        }
	        
            if (_log.isDebugEnabled()) {
                _log.debug("loadNetworkServicesListAtStartUp", " After loading:" + _networkServiceMap.size());
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
        	throw new BTSLBaseException("loadNetworkServicesListAtStartUp", methodName, "Exception in refreshing Network Services List.");
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED);
        	}
        }
    }
    /**
     * Gets the network service list
     * @throws Exception 
     * 
     */
    public static void refreshNetworkServicesList() throws BTSLBaseException {
    	final String methodName = "refreshNetworkServicesList";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        try {
            if (_log.isDebugEnabled()) {
                _log.debug("refreshNetworkServicesList", " Before loading:" + _networkServiceMap);
            }
            HashMap<String, NetworkVO> tempMap = _networkServiceDAO.loadNetworkServicesList();
            if(PretupsI.REDIS_ENABLE.equals(redisEnable)){
            	Jedis jedis = null;
           	   	RedisActivityLog.log("NetworkServicesCache->refreshNetworkServicesList->Start");
	        	 try {
	        		  jedis = RedisConnectionPool.getPoolInstance().getResource();
					  /*HashMap<String, Object> cachedMap = RedisUtil.deserialize(jedis.hgetAll(hKeyBytes));
					  if ((cachedMap != null) && (cachedMap.size() > 0)) {
		    	            compareMaps(cachedMap, tempMap);
		    	        }*/
					 jedis.set(hKey, gson.toJson(tempMap));
	        	}catch(JedisConnectionException je){
					_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	   		        _log.errorTrace(methodName, je);
	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkServicesCache[refreshNetworkServicesList]", "", "", "", "JedisConnectionException :" + je.getMessage());
	   		        throw new BTSLBaseException(NetworkServicesCache.class.getName(), methodName,je.getMessage());
				}catch(NoSuchElementException  ex){
					_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	   		        _log.errorTrace(methodName, ex);
	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkServicesCache[refreshNetworkServicesList]", "", "", "", "NoSuchElementException :" + ex.getMessage());
	   		        throw new BTSLBaseException(NetworkServicesCache.class.getName(), methodName,ex.getMessage());
				}catch (Exception e){
					_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
	   		        _log.errorTrace(methodName, e);
	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkServicesCache[refreshNetworkServicesList]", "", "", "", "Exception :" + e.getMessage());
	   	            throw new BTSLBaseException(NetworkServicesCache.class.getName(), methodName,e.getMessage());
				}finally{
					if(jedis != null)
						jedis.close();
				}
	        RedisActivityLog.log("NetworkServicesCache->refreshNetworkServicesList->End");
	        }else {
	            compareMaps(_networkServiceMap, tempMap);
	            _networkServiceMap = tempMap;
	        }
            if (_log.isDebugEnabled()) {
                _log.debug("refreshNetworkServicesList", " After loading:" + _networkServiceMap.size());
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
        	throw new BTSLBaseException("NetworkServicesCache", methodName, "Exception in refreshing Network Services List.");
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED);
        	}
        }
    }

    /**
     * To compare Maps
     * 
     * @param p_previousMap
     * @param p_currentMap
     */
    private static void compareMaps(HashMap p_previousMap, HashMap p_currentMap) {
        final String METHOD_NAME = "compareMaps";
        if (_log.isDebugEnabled()) {
            _log.debug("compareMaps()", "Entered PreviousMap " + p_previousMap + "  Current Map" + p_currentMap);
        }
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
                NetworkServiceVO prevVO = (NetworkServiceVO) p_previousMap.get(key);
                NetworkServiceVO curVO = (NetworkServiceVO) p_currentMap.get(key);

                if (prevVO != null && curVO == null) {
                    isNewAdded = true;
                    CacheOperationLog.log("NetworkServicesCache", BTSLUtil.formatMessage("Delete", getKey(prevVO.getModuleCode(), prevVO.getSenderNetwork(), prevVO.getReceiverNetwork(), prevVO.getServiceType()), prevVO.logInfo()));
                } else if (prevVO == null && curVO != null) {
                    CacheOperationLog.log("NetworkServicesCache", BTSLUtil.formatMessage("Add", getKey(curVO.getModuleCode(), curVO.getSenderNetwork(), curVO.getReceiverNetwork(), curVO.getServiceType()), curVO.logInfo()));
                } else if (prevVO != null && curVO != null) {
                    if (!curVO.equals(prevVO)) {
                        CacheOperationLog.log("NetworkServicesCache", BTSLUtil.formatMessage("Modify", getKey(curVO.getModuleCode(), curVO.getSenderNetwork(), curVO.getReceiverNetwork(), curVO.getServiceType()), curVO.differences(prevVO)));
                    }
                }
            }

            // Note: this case arises when same number of element added and
            // deleted as well
            if (p_previousMap.size() == p_currentMap.size() && isNewAdded) {
                HashMap tempMap = new HashMap(p_currentMap);
                while (copiedIterator.hasNext()) {
                    tempMap.remove((String) copiedIterator.next());
                }

                Iterator iterator2 = tempMap.keySet().iterator();
                while (iterator2.hasNext()) {
                    NetworkServiceVO mappingVO = (NetworkServiceVO) p_currentMap.get(iterator2.next());
                    CacheOperationLog.log("NetworkServicesCache", BTSLUtil.formatMessage("Add", getKey(mappingVO.getModuleCode(), mappingVO.getSenderNetwork(), mappingVO.getReceiverNetwork(), mappingVO.getServiceType()), mappingVO.logInfo()));
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
     * Get the Network Service Status Info
     * 
     * @param p_moduleCode
     * @param p_senderNetwork
     * @param p_receiverNetwork
     * @param p_serviceType
     * @return NetworkServiceVO
     */
    public static NetworkServiceVO getObject(String p_moduleCode, String p_senderNetwork, String p_receiverNetwork, String p_serviceType) {
        if (_log.isDebugEnabled()) {
            _log.debug("getObject()", "Entered p_moduleCode " + p_moduleCode + " p_senderNetwork=" + p_senderNetwork + " p_receiverNetwork=" + p_receiverNetwork + " p_serviceType=" + p_serviceType);
        }
        NetworkServiceVO networkServiceVO = null;
        if(PretupsI.REDIS_ENABLE.equals(redisEnable.trim())){
        	try{
        		networkServiceVO = networkServiceCacheMap.get(hKey).get(getKey(p_moduleCode, p_senderNetwork, p_receiverNetwork, p_serviceType));
        	}catch(ExecutionException e){
        		_log.error("getObject", PretupsI.EXCEPTION + e.getMessage());
   		        _log.errorTrace("getObject", e);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkServicesCache[getObject]", "", "", "", "ExecutionException :" + e.getMessage());
        	}catch (InvalidCacheLoadException e) { 
   				_log.error("getObject", PretupsI.EXCEPTION + e.getMessage());
			     _log.errorTrace("getObject", e);
	 	     }
        }
       else{
    	   networkServiceVO = (NetworkServiceVO) _networkServiceMap.get(getKey(p_moduleCode, p_senderNetwork, p_receiverNetwork, p_serviceType));
 
       }
        return networkServiceVO; 
      }

    /**
     * Gets the key to be used for map
     * 
     * @param p_moduleCode
     * @param p_senderNetwork
     * @param p_receiverNetwork
     * @param p_serviceType
     * @return String
     */
    public static String getKey(String p_moduleCode, String p_senderNetwork, String p_receiverNetwork, String p_serviceType) {
        return p_moduleCode + "_" + p_senderNetwork + "_" + p_receiverNetwork + "_" + p_serviceType;
    }

    /**
     * @param key
     * @return
     */
    public static HashMap<String, NetworkServiceVO> getNetworServiceFromRedis(String key) {
       String methodName= "getNetworServiceFromRedis";
    	if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered key: "+key);
        }
        NetworkServiceVO networkServiceVO = null;
        Jedis jedis = null;
        HashMap<String, NetworkServiceVO> networkMap = new HashMap<String, NetworkServiceVO>();
		 try {
	        RedisActivityLog.log("NetworkServiceCache->getNetworServiceFromRedis->Start");
			jedis = RedisConnectionPool.getPoolInstance().getResource();
			String json = jedis.get(key);
			Type classType = new TypeToken<HashMap<String, NetworkServiceVO>>() {}.getType();
			if(json != null)
				networkMap=gson.fromJson(json, classType);
       	   	RedisActivityLog.log("NetworkServiceCache->getNetworServiceFromRedis->End");
		 }catch(JedisConnectionException je){
				_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
   		        _log.errorTrace(methodName, je);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkServicesCache[getNetworServiceFromRedis]", "", "", "", "JedisConnectionException :" + je.getMessage());
		 }catch(NoSuchElementException  ex){
				_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
   		        _log.errorTrace(methodName, ex);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkServicesCache[getNetworServiceFromRedis]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		 }catch (Exception e) {
			   _log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
			   _log.errorTrace(methodName, e);
               EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkServicesCache[getNetworServiceFromRedis]", "", "", "", "Exception :" + e.getMessage());
    	 }finally {
    		 if(jedis != null ) {
    			 jedis.close();
    		 }
    		 if (_log.isDebugEnabled()) {
    	            _log.debug(methodName, "Exited networkServiceVO: " + networkServiceVO);
    	        }
    	   }
		 return networkMap;
		 }
}
