package com.btsl.pretups.vastrix.businesslogic;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
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
 * @author rahul.dutt
 */
public class ServiceSelectorInterfaceMappingCache implements Runnable {

    private static Log _log = LogFactory.getLog(ServiceSelectorInterfaceMappingCache.class.getName());

    private static HashMap<String,ServiceSelectorInterfaceMappingVO> _serviceSelInterfaceMappingMap = new HashMap<String,ServiceSelectorInterfaceMappingVO>();
    private static final String hKeyServiceSelectorInterfaceMappingCache= "serviceSelectorInterfaceMappingCache";
    private static String redisEnable = BTSLUtil.NullToString(Constants.getProperty("REDIS_ENABLE"));
    private static final int keyTimer = Integer.parseInt(Constants.getProperty("KEYTIMER"));
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss:mss").create();

    private static LoadingCache<String,ServiceSelectorInterfaceMappingVO>  svcSltInterfaceCacheMap = CacheBuilder.newBuilder()
	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)     
	    .build(new CacheLoader<String, ServiceSelectorInterfaceMappingVO>(){
			@Override
			public ServiceSelectorInterfaceMappingVO load(String key) throws Exception {
				return getSvcSltIntfObjectFromRedis(key);
			}
	     });

    public void run() {
        try {
            Thread.sleep(50);
            loadServSelInterfMappingOnStartup();
        } catch (Exception e) {
        	 _log.error("ServiceSelectorInterfaceMappingCache init() Exception ", e);
        }
    }
    
    public static void loadServSelInterfMappingOnStartup() {
        String methodName = "loadServSelInterfMappingOnStartup";
    	if (_log.isDebugEnabled()) {
            _log.debug("loadServSelInterfMappOnStartup", "entered");
        }

    	if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
    	Jedis jedis = null;
   	    RedisActivityLog.log("ServiceSelectorInterfaceMappingCache->loadServSelInterfMappingOnStartup->Start");
   		 try {
   			    jedis = RedisConnectionPool.getPoolInstance().getResource();
   			 	Pipeline pipeline = jedis.pipelined();
				//If key is already present in redis db do not reload
				if(!jedis.exists(hKeyServiceSelectorInterfaceMappingCache)) {
					HashMap<String,ServiceSelectorInterfaceMappingVO> serviceSelInterfaceMappingMap = loadServSelInterfMapping();
    				 for (Entry<String, ServiceSelectorInterfaceMappingVO> entry : serviceSelInterfaceMappingMap.entrySet())  {
    			     pipeline.hset(hKeyServiceSelectorInterfaceMappingCache, entry.getKey(), gson.toJson(entry.getValue()));
    			   } 
    			pipeline.sync();
				}
   		      }catch(JedisConnectionException je){
				_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
   		        _log.errorTrace(methodName, je);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingCache[loadServSelInterfMappingOnStartup]", "", "", "", "JedisConnectionException :" + je.getMessage());
			}catch(NoSuchElementException  ex){
				_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
   		        _log.errorTrace(methodName, ex);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingCache[loadServSelInterfMappingOnStartup]", "", "", "", "NoSuchElementException :" + ex.getMessage());
			}catch (Exception e){
				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
   		        _log.errorTrace(methodName, e);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingCache[loadServSelInterfMappingOnStartup]", "", "", "", "Exception :" + e.getMessage());
			}finally{
				if(jedis != null)
					jedis.close();
			}
	        RedisActivityLog.log("ServiceSelectorInterfaceMappingCache->loadServSelInterfMappingOnStartup->End");
   		   } else {
   			  _serviceSelInterfaceMappingMap = loadServSelInterfMapping();
   		}
      
        if (_log.isDebugEnabled()) {
            _log.debug("loadServSelInterfMappOnStartup", "exited");
        }
    }

    /**
     * @return
     */
    private static HashMap<String,ServiceSelectorInterfaceMappingVO> loadServSelInterfMapping() {
        if (_log.isDebugEnabled()) {
            _log.debug("loadServSelInterfMapping", "entered");
        }
        HashMap<String,ServiceSelectorInterfaceMappingVO> serviceSelInterMap = null;
        ServiceSelectorInterfaceMappingDAO serviceSelectorInterfaceMappingDAO = new ServiceSelectorInterfaceMappingDAO();
        try {
            serviceSelInterMap = serviceSelectorInterfaceMappingDAO.loadServSelInterfMappingCache();
        } catch (Exception e) {
            _log.error("loadServSelInterfMapping", "Exeception e" + e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("loadServSelInterfMappOnStartup", "exiting serviceSelInterMap size" + serviceSelInterMap.size());
        }
        return serviceSelInterMap;

    }

    public static void updateServSelInterfMapping() {
    	String methodName = "updateServSelInterfMapping";
        if (_log.isDebugEnabled()) {
            _log.debug("updateServSelInterfMapping()", " Entered");
        }
        HashMap<String,ServiceSelectorInterfaceMappingVO> currentMap = loadServSelInterfMapping();
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        	Jedis jedis = null;
   	        RedisActivityLog.log("ServiceSelectorInterfaceMappingCache->updateServSelInterfMapping->Start");
      		 try {
      		    jedis = RedisConnectionPool.getPoolInstance().getResource();
      		    Pipeline pipeline = jedis.pipelined();
      			jedis.del(hKeyServiceSelectorInterfaceMappingCache);
      			for (Entry<String,ServiceSelectorInterfaceMappingVO> entry : currentMap.entrySet())  {
      			      pipeline.hset(hKeyServiceSelectorInterfaceMappingCache, entry.getKey(), gson.toJson(entry.getValue()));
      			} 
      			pipeline.sync();
   	        }catch(JedisConnectionException je){
				_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
   		        _log.errorTrace(methodName, je);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingCache[updateServSelInterfMapping]", "", "", "", "JedisConnectionException :" + je.getMessage());
			}catch(NoSuchElementException  ex){
				_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
   		        _log.errorTrace(methodName, ex);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingCache[updateServSelInterfMapping]", "", "", "", "NoSuchElementException :" + ex.getMessage());
			}catch (Exception e){
				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
   		        _log.errorTrace(methodName, e);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingCache[updateServSelInterfMapping]", "", "", "", "Exception :" + e.getMessage());
			}
	   	   RedisActivityLog.log("ServiceSelectorInterfaceMappingCache->updateServSelInterfMapping->End");
       } else {
        _serviceSelInterfaceMappingMap = currentMap;
       }
        if (_log.isDebugEnabled()) {
            _log.debug("updateServSelInterfMapping()", "exited " + _serviceSelInterfaceMappingMap.size());
        }
    }

    /**
     * @param p_servSelInterCode
     * @return
     */
    public static ServiceSelectorInterfaceMappingVO getObject(String p_servSelInterCode) {

        if (_log.isDebugEnabled()) {
            _log.debug("getObject()", "entered " + p_servSelInterCode);
        }
        ServiceSelectorInterfaceMappingVO mappingVO = null;
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
          try{
        	  mappingVO = svcSltInterfaceCacheMap.get(p_servSelInterCode);
          }catch(ExecutionException e){
      		_log.error("getObject", PretupsI.EXCEPTION + e.getMessage());
	        _log.errorTrace("getObject", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingCache[getObject]", "", "", "", "ExecutionException :" + e.getMessage());
    	}catch (InvalidCacheLoadException e) { 
				_log.error("getObject", PretupsI.EXCEPTION + e.getMessage());
			    _log.errorTrace("getObject", e);
		        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingCache[getObject]", "", "", "", "InvalidCacheLoadException :" + e.getMessage());	
	 	     }
        } else {
    	  mappingVO = (ServiceSelectorInterfaceMappingVO) _serviceSelInterfaceMappingMap.get(p_servSelInterCode);
      }
        if (_log.isDebugEnabled()) {
            _log.debug("getObject()", "exited " + mappingVO);
        }
        return mappingVO;
    }
    

    public static ServiceSelectorInterfaceMappingVO getSvcSltIntfObjectFromRedis(String p_servSelInterCode) {
    	String methodName = "getSvcSltIntfObjectFromRedis";
    	if (_log.isDebugEnabled()) {
            _log.debug("getSvcSltIntfObjectFromRedis()", "Entered p_servSelInterCode: " + p_servSelInterCode);
        }
        RedisActivityLog.log("ServiceSelectorInterfaceMappingCache->getSvcSltIntfObjectFromRedis->Start");
    	Jedis jedis = null;
    	ServiceSelectorInterfaceMappingVO mappingVO = null;
		 try {
			 jedis = RedisConnectionPool.getPoolInstance().getResource();
			 String prefObj = jedis.hget(hKeyServiceSelectorInterfaceMappingCache, p_servSelInterCode);
			if(!BTSLUtil.isNullString(prefObj))
				mappingVO = gson.fromJson(prefObj,ServiceSelectorInterfaceMappingVO.class);
		 }catch(JedisConnectionException je){
		 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
   		        _log.errorTrace(methodName, je);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingCache[getSvcSltIntfObjectFromRedis]", "", "", "", "JedisConnectionException :" + je.getMessage());
		 }catch(NoSuchElementException  ex){
		 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
   		        _log.errorTrace(methodName, ex);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingCache[getSvcSltIntfObjectFromRedis]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		 }catch (Exception e) {
		 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
		 	_log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingCache[getSvcSltIntfObjectFromRedis]", "", "", "", "Exception :" + e.getMessage());
		 }
		 finally {
	        	if (jedis != null) {
	        	jedis.close();
	        	}
	        }
		 RedisActivityLog.log("ServiceSelectorInterfaceMappingCache->getSvcSltIntfObjectFromRedis->End");
      return mappingVO;
    }
}
