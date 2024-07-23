package com.btsl.pretups.servicekeyword.businesslogic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
import com.btsl.pretups.gateway.businesslogic.MessageGatewayCache;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayMappingCacheVO;
import com.btsl.redis.pool.RedisConnectionPool;
import com.btsl.redis.util.RedisActivityLog;
import com.btsl.redis.util.RedisUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.CacheLoader.InvalidCacheLoadException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class ServiceInstancePriorityCache implements Runnable {

    private static Log _log = LogFactory.getLog(ServiceInstancePriorityCache.class.getName());

    private static HashMap<String, String> _serviceInstancePriorityMap = new HashMap<String, String>();
    private static String redisEnable = BTSLUtil.NullToString(Constants.getProperty("REDIS_ENABLE"));
    private static final String hKeyServiceInstancePriorityMap = "ServiceInstancePriorityCache";
   
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss:mss").create();
    private static final int keyTimer = Integer.parseInt(Constants.getProperty("KEYTIMER"));
    
    private static LoadingCache<String, String> _serviceInstancePriorityCache = CacheBuilder.newBuilder()
    	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)
    	    .build(new CacheLoader<String, String>(){
    			@Override
    			public String load(String key) throws Exception {
    				return getserviceInstancePriorityMapObjFromRedis(key);
    			}
    	     });
  
    public static String getServiceInstancePriority(String p_serviceInstanceKey) {
    	String serviceInstancePriority = null;
    	try{
    		if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
    			serviceInstancePriority = _serviceInstancePriorityCache.get(p_serviceInstanceKey);
	    	} else {
	    		serviceInstancePriority =  _serviceInstancePriorityMap.get(p_serviceInstanceKey);
	    	}
    	}catch (ExecutionException e) {
			_log.error("getServiceInstancePriority", PretupsI.EXCEPTION + e.getMessage());
	        _log.errorTrace("getServiceInstancePriority", e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ServiceInstancePriorityCache[getServiceInstancePriority]", "", "", "", "Exception :" + e.getMessage());
        }catch(InvalidCacheLoadException  ex){
 			_log.error("getServiceInstancePriority", PretupsI.EXCEPTION + ex.getMessage());
	        _log.errorTrace("getServiceInstancePriority", ex);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ServiceInstancePriorityCache[getServiceInstancePriority]", "", "", "", "InvalidCacheLoadException :" + ex.getMessage());
        }
    	return serviceInstancePriority;
    }
    
    public void run() {
        try {
            Thread.sleep(50);
			loadServiceInstancePriorityCacheOnStartUp();
        } catch (Exception e) {
        	 _log.error("ServiceInstancePriorityCache init() Exception ", e);
        }
    }
    /**
     * To Load all prefrences on startup
     * Preferences like System level,Network Level,Zone Level,Service Level
     * 
     * void
     * PreferenceCache0
     */
    public static void loadServiceInstancePriorityCacheOnStartUp() {
    	String methodName = "loadServiceInstancePriorityCacheOnStartUp";
        if (_log.isDebugEnabled()) {
            _log.debug("loadServiceInstancePriorityCacheOnStartUp()", "Entered");
        }
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        	Jedis jedis =null;
        	 RedisActivityLog.log("ServiceInstancePriorityCache->loadServiceInstancePriorityCacheOnStartUp->Start");
			 try {
				 jedis = RedisConnectionPool.getPoolInstance().getResource();
				//If key is already present in redis db do not reload
				 if(!jedis.exists(hKeyServiceInstancePriorityMap)){
					Pipeline pipeline = jedis.pipelined();
	 				Map<String, String> serviceInstancePriorityMap = loadServiceInstancePriority();
					for (Entry<String, String> entry : serviceInstancePriorityMap.entrySet())  {
					  pipeline.hset(hKeyServiceInstancePriorityMap, entry.getKey(), entry.getValue());
					}
					pipeline.sync();
				 }
			 }catch(JedisConnectionException je){
		        	_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
			        _log.errorTrace(methodName, je);
		            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceInstancePriorityCache[loadServiceInstancePriorityCacheOnStartUp]", "", "", "", "JedisConnectionException :" + je.getMessage());
				 }catch(NoSuchElementException  ex){
			 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
			        _log.errorTrace(methodName, ex);
		            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceInstancePriorityCache[loadServiceInstancePriorityCacheOnStartUp]", "", "", "", "NoSuchElementException :" + ex.getMessage());
				 }catch (Exception e) {
					_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
			    	_log.errorTrace(methodName, e);
				 }finally {
			    	if (jedis != null) {
			    	jedis.close();
			    	}
			    	if (_log.isDebugEnabled()) {
		    		_log.debug(methodName, PretupsI.EXITED);
		    	}
		      }	
			 RedisActivityLog.log("ServiceInstancePriorityCache->loadServiceInstancePriorityCacheOnStartUp->Stop");
		 } else {	 
			 _serviceInstancePriorityMap = loadServiceInstancePriority();
	     }
        if (_log.isDebugEnabled()) {
            _log.debug("loadServiceInstancePriorityCacheOnStartUp()", "Exited");
        }
    }

    /**
     * To load the Preferences
     * 
     * @return
     *         HashMap
     *         PreferenceCache
     */
    private static HashMap<String, String> loadServiceInstancePriority() {
        if (_log.isDebugEnabled()) {
            _log.debug("loadServiceInstancePriority()", "Entered");
        }
        final String METHOD_NAME = "loadServiceInstancePriority";
        ServiceInstancePriorityDAO serviceDAO = new ServiceInstancePriorityDAO();
        HashMap<String, String> map = null;

        try {
            map = serviceDAO.loadServiceInstancePriority();
        } catch (Exception e) {
            _log.error("loadServiceInstancePriority() ", "Exception e: " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
        }

        if (_log.isDebugEnabled()) {
            _log.debug("loadServiceInstancePriority()", "Exited " + map.size());
        }

        return map;
    }

    /**
     * @param key
     * @return
     * @throws BTSLBaseException
     */
    public static String getserviceInstancePriorityMapObjFromRedis(String key) throws BTSLBaseException {
        String methodName = "getserviceInstancePriorityMapObjFromRedis";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:key=" + key);
        }
        Jedis jedis = null;
        String obj= null;
		 try {
		     RedisActivityLog.log("ServiceInstancePriorityCache->getserviceInstancePriorityMapObjFromRedis->Start");
			 jedis = RedisConnectionPool.getPoolInstance().getResource();
			 obj = jedis.hget(hKeyServiceInstancePriorityMap, key);
			 RedisActivityLog.log("ServiceInstancePriorityCache->getserviceInstancePriorityMapObjFromRedis->End");
		 }catch(JedisConnectionException je){
	 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	        _log.errorTrace(methodName, je);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ServiceInstancePriorityCache[getserviceInstancePriorityMapObjFromRedis]", "", "", "", "JedisConnectionException :" + je.getMessage());
	        throw new BTSLBaseException(MessageGatewayCache.class.getName(), methodName,je.getMessage());
		 }catch(NoSuchElementException  ex){
	 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	        _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ServiceInstancePriorityCache[getserviceInstancePriorityMapObjFromRedis]", "", "", "", "NoSuchElementException :" + ex.getMessage());
	        throw new BTSLBaseException(MessageGatewayCache.class.getName(), methodName,ex.getMessage());
		 }catch (Exception e) {
		 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
		 	_log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ServiceInstancePriorityCache[getserviceInstancePriorityMapObjFromRedis]", "", "", "", "Exception :" + e.getMessage());
			throw new BTSLBaseException(MessageGatewayCache.class.getName(), methodName,e.getMessage());
		 }
		 finally {
	        	if (jedis != null) {
	        	jedis.close();
	        	}
	        }
        return obj;
    }

}
