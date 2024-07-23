package com.btsl.pretups.master.businesslogic;

import java.util.HashMap;
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

public class ServiceClassInfoByCodeCache implements Runnable{

    private static Log _log = LogFactory.getLog(ServiceClassInfoByCodeCache.class.getName());
    private static HashMap<String, ServiceClassVO> _serviceClassByCodeMap = new HashMap<String, ServiceClassVO>();
    private static final String CLASS_NAME = "ServiceClassInfoByCodeCache";
    private static String redisEnable = BTSLUtil.NullToString(Constants.getProperty("REDIS_ENABLE"));
    private static final String hKeyServiceClassByCodeMap = "ServiceClassByCodeMap";
    private static Gson gson = new GsonBuilder()
	.setDateFormat("yyyy-MM-dd HH:mm:ss:mss")
	.create();
	private static final int keyTimer = Integer.parseInt(Constants.getProperty("KEYTIMER"));
	
	private static LoadingCache<String, ServiceClassVO> serviceClassByCodeCache = CacheBuilder.newBuilder()
	.expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)
	.build(new CacheLoader<String,ServiceClassVO>(){
		@Override
		public ServiceClassVO load(String key) throws Exception {
			return getServiceClassByCodeVOFormRedis(key);
		}
	 });

    /**
     * Description : This method loads the load Service Class By Code Map At
     * Startup
     * Method : loadServiceClassByCodeMapAtStartup
     * 
     * @return
     * @throws Exception 
     */
    public void run() {
        try {
            Thread.sleep(50);
            loadServiceClassByCodeMapAtStartup();
        } catch (Exception e) {
        	 _log.error("ServiceClassInfoByCodeCache init() Exception ", e);
        }
    }
    public static void loadServiceClassByCodeMapAtStartup() throws BTSLBaseException {
    	final String methodName = "loadServiceClassByCodeMapAtStartup";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
      Jedis jedis = null;
      try{
    	  if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
           	RedisActivityLog.log("ServiceClassInfoByCodeCache->loadSubsdcriberRoutingControlAtStartUp->Start");
 	   			  jedis = RedisConnectionPool.getPoolInstance().getResource();
 	   			  Pipeline pipeline = jedis.pipelined();
 					//If key is already present in redis db do not reload
 				  if(!jedis.exists(hKeyServiceClassByCodeMap)) {
 						HashMap<String, ServiceClassVO> _txfrRuleMap1  = loadMapping();
 					 for (Entry<String, ServiceClassVO> entry : _txfrRuleMap1.entrySet())  {
 						 pipeline.hset(hKeyServiceClassByCodeMap, entry.getKey(), gson.toJson(entry.getValue()));
 					 } 
 					 pipeline.sync();
 				 }
 		  RedisActivityLog.log("ServiceClassInfoByCodeCache->loadSubsdcriberRoutingControlAtStartUp->End");
    	  } else {	 
        _serviceClassByCodeMap = loadMapping();
    	}
      }catch(BTSLBaseException be) {
		_log.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
		_log.errorTrace(methodName, be);
		throw be;
      }catch(JedisConnectionException je){
 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
        _log.errorTrace(methodName, je);
        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ServiceClassInfoByCodeCache[loadSubsdcriberRoutingControlAtStartUp]", "", "", "", "JedisConnectionException :" + je.getMessage());
        throw new BTSLBaseException(CLASS_NAME, methodName,je.getMessage());
      }catch(NoSuchElementException  ex){
 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
        _log.errorTrace(methodName, ex);
        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ServiceClassInfoByCodeCache[loadSubsdcriberRoutingControlAtStartUp]", "", "", "", "NoSuchElementException :" + ex.getMessage());
        throw new BTSLBaseException(CLASS_NAME, methodName,ex.getMessage());
	 }catch (Exception e){
		_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
		_log.errorTrace(methodName, e);
		throw new BTSLBaseException(CLASS_NAME, methodName, "Exception in loading the service Class By Code mapping on Startup.",e);
	} finally {
    	if (jedis != null) {
    	jedis.close();
    	}
    	if (_log.isDebugEnabled()) {
			_log.debug(methodName, PretupsI.EXITED);
		}
    }
    }

    /**
     * Description : This method loads the service Class By Code mapping
     * Method : loadMapping
     * 
     * @return HashMap
     * @throws Exception 
     */
    private static HashMap<String, ServiceClassVO> loadMapping() throws BTSLBaseException {
    	final String methodName = "loadMapping";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}

        HashMap<String, ServiceClassVO> serviceClassByCodeMap = null;
        ServiceClassDAO serviceClassDAO = null;
        try {
            serviceClassDAO = new ServiceClassDAO();
            serviceClassByCodeMap = serviceClassDAO.loadServiceClassInfoByCodeWithAll();
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
        	throw new BTSLBaseException(CLASS_NAME, methodName, "Exception in loading the service Class By Code mapping.",e);
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED + ". serviceClassByCodeMap.size()=" + serviceClassByCodeMap.size());
        	}
        }
        return serviceClassByCodeMap;
    }

    /**
     * getServiceClassByCode() method returns the details of service class from
     * cache
     * 
     * @param p_interfaceCode
     * @param p_serviceClassCode
     * @return ServiceClassVO
     */
    public static ServiceClassVO getServiceClassByCode(String p_serviceClassCode, String p_interfaceCode) throws BTSLBaseException {
        final String METHOD_NAME = "getServiceClassByCode";
        if (_log.isDebugEnabled()) {
            _log.debug("getServiceClassByCode()", "entered p_serviceClassCode: " + p_serviceClassCode + ", p_interfaceCode: " + p_interfaceCode);
        }
        ServiceClassVO serviceClassVO = null;
        try {
        	 if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        		 serviceClassVO = serviceClassByCodeCache.get(p_serviceClassCode + "_" + p_interfaceCode);
        	 } else {
    	         serviceClassVO = (ServiceClassVO) _serviceClassByCodeMap.get(p_serviceClassCode + "_" + p_interfaceCode);
    		 }
            /*
             * if(serviceClassVO==null)
             * {
             * throw new BTSLBaseException("ServiceClassInfoByCodeCache",
             * "getServiceClassByCode"
             * ,PretupsErrorCodesI.ERROR_INTFCE_SRVCECLSS_NOTFOUND,0,null);
             * }
             */
        } catch (ExecutionException e) {
			_log.error("getServiceClassByCode", PretupsI.EXCEPTION + e.getMessage());
	        _log.errorTrace("getServiceClassByCode", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassInfoByCodeCache[getServiceClassByCode]", "", "", "", "ExecutionException :" + e.getMessage());
            throw new BTSLBaseException("ServiceClassInfoByCodeCache", "getServiceClassByCode", "ExecutionException",e);
        }catch (InvalidCacheLoadException e) { 
	        _log.info("getServiceClassByCode", e);
		} catch (Exception e) {
            _log.error("getServiceClassByCode", "SQLException " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceClassInfoByCodeCache[getServiceClassByCode]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("ServiceClassInfoByCodeCache", "getServiceClassByCode", "error.general.processing",e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("getServiceClassByCode()", "exited serviceClassVO: " + serviceClassVO);
        }
        return serviceClassVO;
    }

    public static void updateServiceClassByCodeMap() throws BTSLBaseException {
    	final String methodName = "updateServiceClassByCodeMap";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
    	Jedis jedis =null;
      try{
    	  if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
	           	RedisActivityLog.log("ServiceClassInfoByCodeCache->updateServiceClassByCodeMap->Start");
	            jedis = RedisConnectionPool.getPoolInstance().getResource();
	           	Pipeline pipeline = jedis.pipelined();
	           	pipeline.del(hKeyServiceClassByCodeMap);
				HashMap<String, ServiceClassVO> _txfrRuleMap1  = loadMapping();
				for (Entry<String, ServiceClassVO> entry : _txfrRuleMap1.entrySet())  {
			       pipeline.hset(hKeyServiceClassByCodeMap, entry.getKey(), gson.toJson(entry.getValue()));
				 }
				pipeline.sync();
	          	RedisActivityLog.log("ServiceClassInfoByCodeCache->updateServiceClassByCodeMap->End");
    	  } else {	 
    		  _serviceClassByCodeMap = loadMapping();
    	}
      }
      catch(BTSLBaseException be) {
    		_log.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
    		_log.errorTrace(methodName, be);
    		throw be;
    	}catch(JedisConnectionException je){
	 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	        _log.errorTrace(methodName, je);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ServiceClassInfoByCodeCache[updateServiceClassByCodeMap]", "", "", "", "JedisConnectionException :" + je.getMessage());
	        throw new BTSLBaseException(CLASS_NAME, methodName,je.getMessage());
		 }catch(NoSuchElementException  ex){
	 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	        _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ServiceClassInfoByCodeCache[updateServiceClassByCodeMap]", "", "", "", "NoSuchElementException :" + ex.getMessage());
	        throw new BTSLBaseException(CLASS_NAME, methodName,ex.getMessage());
		 }catch (Exception e)
    	{
    		_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
    		_log.errorTrace(methodName, e);
    		throw new BTSLBaseException(CLASS_NAME, methodName, "Exception in loading the service Class By Code mapping on Startup.",e);
    	} finally {
    		if (jedis != null) {
            	jedis.close();
            	}
    		if (_log.isDebugEnabled()) {
    			_log.debug(methodName, PretupsI.EXITED);
    		}
    	}
    }
    
    
    /**
     * @param key
     * @return
     * @throws BTSLBaseException
     */
    public static ServiceClassVO getServiceClassByCodeVOFormRedis(String key) throws BTSLBaseException {
        String methodName = "getServiceClassByCodeVOFormRedis";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:key=" + key);
        }
        Jedis jedis = null;
        ServiceClassVO serviceClassVO = null;
		 try {
		     RedisActivityLog.log("ServiceClassInfoByCodeCache->getServiceClassByCodeVOFormRedis->Start");
			 jedis = RedisConnectionPool.getPoolInstance().getResource();
			 String prefObj = jedis.hget(hKeyServiceClassByCodeMap, key);
			 if(!BTSLUtil.isNullString(prefObj))
				 serviceClassVO = gson.fromJson(prefObj,ServiceClassVO.class);
			 RedisActivityLog.log("ServiceClassInfoByCodeCache->getServiceClassByCodeVOFormRedis->End");
		 }catch(JedisConnectionException je){
	 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	        _log.errorTrace(methodName, je);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ServiceClassInfoByCodeCache[getServiceClassByCodeVOFormRedis]", "", "", "", "JedisConnectionException :" + je.getMessage());
	        throw new BTSLBaseException(ServiceClassInfoByCodeCache.class.getName(), methodName,je.getMessage());
		 }catch(NoSuchElementException  ex){
	 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	        _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ServiceClassInfoByCodeCache[getServiceClassByCodeVOFormRedis]", "", "", "", "NoSuchElementException :" + ex.getMessage());
	        throw new BTSLBaseException(ServiceClassInfoByCodeCache.class.getName(), methodName,ex.getMessage());
		 }catch (Exception e) {
		 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
		 	_log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ServiceClassInfoByCodeCache[getServiceClassByCodeVOFormRedis]", "", "", "", "Exception :" + e.getMessage());
			throw new BTSLBaseException(ServiceClassInfoByCodeCache.class.getName(), methodName,e.getMessage());
		 }
		 finally {
	        	if (jedis != null) {
	        	jedis.close();
	        	}
	        }
        return serviceClassVO;
    }

}
