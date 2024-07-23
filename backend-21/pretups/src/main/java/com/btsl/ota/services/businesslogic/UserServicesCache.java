/*
 * @#UserServicesCache.java
 * * This class is the cache class of the user services.
 * 
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * --
 * Aankur Dhawan August, 2011 Initial creation*
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2010 Comviva Ltd.
 */
package com.btsl.ota.services.businesslogic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
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
import com.google.gson.reflect.TypeToken;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class UserServicesCache implements Runnable{
    private static Log _log = LogFactory.getLog(UserServicesCache.class.getName());
    private static HashMap<String, Object> _servicesMap = new HashMap<String, Object>();
    private static String redisEnable = BTSLUtil.NullToString(Constants.getProperty("REDIS_ENABLE"));
    private static final String hKeyServicesMap = "UserServicesCache";
    private static Gson gson = new GsonBuilder()
    		.setDateFormat("yyyy-MM-dd HH:mm:ss:mss")
    		.create();
    private static final int keyTimer = Integer.parseInt(Constants.getProperty("KEYTIMER"));
    private static LoadingCache<String, Object> servicesMemo = CacheBuilder.newBuilder()
    	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)
    	    .build(new CacheLoader<String, Object>(){
    			@Override
    			public Object load(String key) throws Exception {
    				return getObjectFromRedis(key);
    			}
    	     });
   
    public void run() {
        try {
            Thread.sleep(50);
            loadServicesAtStartup();
        } catch (Exception e) {
        	 _log.error("UserServicesCache init() Exception ", e);
        }
    }
    /**
     * @author ankur.dhawan
     *         Description : This method loads the user services cache at
     *         startup
     *         Method : loadServicesAtStartup
     * @return
     * @throws Exception 
     */

    public static void loadServicesAtStartup() throws BTSLBaseException {
    	final String methodName = "loadServicesAtStartup";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
     try{
    	 if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
    		 RedisActivityLog.log("UserServicesCache->loadServicesAtStartup->Start");
    		 Jedis jedis = null;
      		 try {
      			jedis = RedisConnectionPool.getPoolInstance().getResource();
				//If key is already present in redis db do not reload
 				if(!jedis.exists(hKeyServicesMap)) {
 					 Pipeline pipeline = jedis.pipelined();
 					Map<String, Object> servicesMap = loadMapping();
					 for (Entry<String, Object> entry : servicesMap.entrySet())  {
						 pipeline.hset(hKeyServicesMap, entry.getKey(), gson.toJson(entry.getValue()));
					 }
					 pipeline.sync();
 			     }
			 }catch(JedisConnectionException je){
				 _log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
				 _log.errorTrace(methodName, je);
 	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserServicesCache[loadServicesAtStartup]", "", "", "", "JedisConnectionException :" + je.getMessage());
 	   	           throw new BTSLBaseException(UserServicesCache.class.getName(), methodName,je.getMessage());
			 }catch(NoSuchElementException  ex){
 				_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
 				_log.errorTrace(methodName, ex);
 	   	       EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserServicesCache[loadServicesAtStartup]", "", "", "", "NoSuchElementException :" + ex.getMessage());
 	   	       throw new BTSLBaseException(UserServicesCache.class.getName(), methodName,ex.getMessage());
			 }catch (Exception e) {
 				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
 				_log.errorTrace(methodName, e);
 	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserServicesCache[loadServicesAtStartup]", "", "", "", "Exception :" + e.getMessage());
 	           throw new BTSLBaseException(UserServicesCache.class.getName(), methodName,e.getMessage());
			 }
 			 finally {
 		        	if (jedis != null) {
 		        	jedis.close();
 		        }	
			 RedisActivityLog.log("UserServicesCache->loadServicesAtStartup->Stop");
		 }
      	} else {	 
			  _servicesMap = loadMapping();
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
    		throw new BTSLBaseException("UserServicesCache", methodName, "");
    	} finally {
    		if (_log.isDebugEnabled()) {
    			_log.debug(methodName, PretupsI.EXITED);
    		}
    	}
    }

    /**
     * @author ankur.dhawan
     *         Description : This method loads the services mapping
     *         Method : loadMapping
     * @return HashMap
     * @throws Exception 
     */

    private static HashMap<String, Object> loadMapping() throws BTSLBaseException {
    	final String methodName = "loadMapping";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        HashMap<String, Object> serviceMap = null;
        ServicesDAO servicesDAO = null;
        try {
            servicesDAO = new ServicesDAO();
            serviceMap = servicesDAO.loadServicesCache();
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
        	throw new BTSLBaseException("UserServicesCache", methodName, "");
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED);
        	}
        }
        return serviceMap;
    }

    /**
     * @author ankur.dhawan
     *         Description : This method updates the user services map
     *         Method : updateServicesMap
     * @return
     * @throws Exception 
     */

    public static void updateServicesMap() throws BTSLBaseException {
    	final String methodName = "updateServicesMap";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
   try{
	   _servicesMap = loadMapping();
		if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
			RedisActivityLog.log("UserServicesCache->updateServicesMap->Start");
 			Jedis jedis = null;
  			 try {
  				 jedis = RedisConnectionPool.getPoolInstance().getResource();
  				 Pipeline pipeline = jedis.pipelined();
  				 pipeline.del(hKeyServicesMap);
						 for (Entry<String, Object> entry : _servicesMap.entrySet())  {
					       pipeline.hset(hKeyServicesMap, entry.getKey(), gson.toJson(entry.getValue()));
						 }
				  pipeline.sync();	 
				  }catch(JedisConnectionException je){
					  _log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
					  _log.errorTrace(methodName, je);
		 	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserServicesCache[updateServicesMap]", "", "", "", "JedisConnectionException :" + je.getMessage());
		 	   	        throw new BTSLBaseException(UserServicesCache.class.getName(), methodName,je.getMessage());
				  }catch(NoSuchElementException  ex){
		 				_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
		 				_log.errorTrace(methodName, ex);
		 	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserServicesCache[updateServicesMap]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		 	   	        throw new BTSLBaseException(UserServicesCache.class.getName(), methodName,ex.getMessage());
				  }catch (Exception e) {
		 				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
		 				_log.errorTrace(methodName, e);
		 	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserServicesCache[updateServicesMap]", "", "", "", "Exception :" + e.getMessage());
		 	           throw new BTSLBaseException(UserServicesCache.class.getName(), methodName,e.getMessage());
				  }
		 			 finally {
		 		        	if (jedis != null) {
		 		        	jedis.close();
		 		        }
 			
    	 }
    		RedisActivityLog.log("UserServicesCache->updateServicesMap->Stop");
	   }
       Set<Entry<String, Object>> mapIterator = _servicesMap.entrySet();

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "The Services map comtains the values :");
            
            for(Entry<String, Object>  entry : mapIterator ){

                String key = entry.getKey();
                ArrayList<ServiceTypeVO> servicesList = (ArrayList<ServiceTypeVO>) entry.getValue();
                Iterator iter = servicesList.iterator();
                while (iter.hasNext()) {
                    ServiceTypeVO serviceVO = (ServiceTypeVO) iter.next();
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, key.substring(0, key.indexOf('_')) + " " + serviceVO.getServiceType());
                    }
                }
            }
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
			throw new BTSLBaseException(methodName, methodName, "");
		} finally {
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, PretupsI.EXITED + _servicesMap.size());
			}
		}
    }

    /**
     * @author ankur.dhawan
     *         Description : This method return the object of service
     *         Method : getObject
     * @return
     */

    @SuppressWarnings("unchecked")
	public static ArrayList<ServiceTypeVO> getObject(String p_key) {
        if (_log.isDebugEnabled()) {
            _log.debug("getObject", "Entered :p_key " + p_key);
        }
        String methodName ="getObject";
        ArrayList<ServiceTypeVO> userServices = null;
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        	RedisActivityLog.log("UserServicesCache->getObject->Start");
    		 try (Jedis jedis = RedisConnectionPool.getPoolInstance().getResource()) {
    			 try {
					userServices = (ArrayList<ServiceTypeVO>) servicesMemo.get(p_key);
				} catch (ExecutionException e) {
					_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
					_log.errorTrace(methodName, e);
	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserServicesCache[getObject]", "", "", "", "ExecutionException :" + e.getMessage());
				}
	        	catch (InvalidCacheLoadException e) { 
	        		_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
	        		_log.errorTrace(methodName, e);
	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserServicesCache[getObject]", "", "", "", "InvalidCacheLoadException :" + e.getMessage());	
				}
    		 }
    		 RedisActivityLog.log("UserServicesCache->getObject->Stop");
        }
        Set<Entry<String, Object >> iterator =  _servicesMap.entrySet();
        for(Entry<String, Object>  entry: iterator){
            String key = entry.getKey();
            if (key.equals(p_key)) {
                userServices = (ArrayList<ServiceTypeVO>) entry.getValue();
                break;
            }
       }
        if (_log.isDebugEnabled()) {
            _log.debug("getObject", "Exiting :userServices.size()= " + userServices.size());
        }
        if (userServices != null && userServices.isEmpty()) {
            userServices = null;
        }
        return userServices;
    }
    
    public static ArrayList<ServiceTypeVO> getObjectFromRedis(String p_key) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("getObject", "Entered :p_key " + p_key);
        }

        ArrayList<ServiceTypeVO> userServices = null;
        String methodName ="getObjectFromRedis";
        	RedisActivityLog.log("UserServicesCache->getObjectFromRedis->Start");
        	Jedis jedis = null;
    		 try  {
    			 jedis = RedisConnectionPool.getPoolInstance().getResource();
    			 String jasonServiceTypeobj = jedis.hget(hKeyServicesMap, p_key);
    			 if(!BTSLUtil.isNullString(jasonServiceTypeobj))
    			 userServices = (ArrayList<ServiceTypeVO>)gson.fromJson(jasonServiceTypeobj,new TypeToken< ArrayList<ServiceTypeVO>>() {}.getType());
    		 }catch(JedisConnectionException je){
				  _log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
				  _log.errorTrace(methodName, je);
	 	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserServicesCache[getObjectFromRedis]", "", "", "", "JedisConnectionException :" + je.getMessage());
	 	   	        throw new BTSLBaseException(UserServicesCache.class.getName(), methodName,je.getMessage());
    		 }catch(NoSuchElementException  ex){
	 				_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	 				_log.errorTrace(methodName, ex);
	 	   	      EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserServicesCache[getObjectFromRedis]", "", "", "", "NoSuchElementException :" + ex.getMessage());
	 	   	     throw new BTSLBaseException(UserServicesCache.class.getName(), methodName,ex.getMessage()); 	 
    		 }catch (Exception e) {
	 				 
	 				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
	 				_log.errorTrace(methodName, e);
	 	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserServicesCache[getObjectFromRedis]", "", "", "", "Exception :" + e.getMessage());
	 	           throw new BTSLBaseException(UserServicesCache.class.getName(), methodName,e.getMessage());
    		 }
	 			 finally {
	 		        	if (jedis != null) {
	 		        	jedis.close();
	 		        }
		
    		 RedisActivityLog.log("UserServicesCache->getObjectFromRedis->End");
        }
        return userServices;
    }
   
}
