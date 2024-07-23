package com.btsl.user.businesslogic;

import java.util.HashMap;
import java.util.Map;
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

public class UserStatusCache implements Runnable {

    public UserStatusCache() {
    }

    private static final Log LOG = LogFactory.getLog(UserStatusCache.class.getName());
    private static Map<String, UserStatusVO> _userStatusDetailMap = new HashMap<String, UserStatusVO>();
    private static String redisEnable = BTSLUtil.NullToString(Constants.getProperty("REDIS_ENABLE"));
    private static final String hKeyUserStatusDetailMap = "UserStatusCache";
    private static Gson gson = new GsonBuilder()
    		.setDateFormat("yyyy-MM-dd HH:mm:ss:mss")
    		.create();
    private static final int keyTimer = Integer.parseInt(Constants.getProperty("KEYTIMER"));
    private static LoadingCache<String, UserStatusVO> userStatusDetailMemo = CacheBuilder.newBuilder()
    	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)
    	    .build(new CacheLoader<String, UserStatusVO>(){
    			@Override
    			public UserStatusVO load(String key) throws Exception {
    				return getObjectFromRedis(key);
    			}
    	     });
    public void run() {
    	LOG.info("ConfigServlet", "ConfigServlet Start UserStatusCache loading ................... ");
        try {
            Thread.sleep(50);
            loadUserStatusDetailsAtStartUp();
        } catch (Exception e) {
        	 LOG.error("UserStatusCache init() Exception ", e);
        }
        LOG.info("ConfigServlet", "ConfigServlet End UserStatusCache loading........................... ");
    }
    
    public static void loadUserStatusDetailsAtStartUp() throws BTSLBaseException {
    	final String methodName = "loadUserStatusDetailsAtStartUp";
    	if (LOG.isDebugEnabled()) {
    		LOG.debug(methodName, PretupsI.ENTERED);
    	}
        try{
        	if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        		RedisActivityLog.log("UserStatusCache->loadUserStatusDetailsAtStartUp->Start");
        		Jedis jedis = null;
   			 try {
   				 jedis = RedisConnectionPool.getPoolInstance().getResource();
	   				//If key is already present in redis db do not reload
	    				if(!jedis.exists(hKeyUserStatusDetailMap)) {
	    					 Pipeline pipeline = jedis.pipelined();
	    					HashMap<String, UserStatusVO> currencyConversionDetailMap = (HashMap<String, UserStatusVO>) loadMapping();
	   					 for (Entry<String, UserStatusVO> entry : currencyConversionDetailMap.entrySet())  {
	   						pipeline.hset(hKeyUserStatusDetailMap, entry.getKey(),gson.toJson(entry.getValue()));
	   					 }
	   					pipeline.sync();
	    			}
	   			 }catch(JedisConnectionException je){
	 				 LOG.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	 				 LOG.errorTrace(methodName, je);
	 	   	         EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserStatusCache[loadUserStatusDetailsAtStartUp]", "", "", "", "JedisConnectionException :" + je.getMessage());
	 	   	 	throw new BTSLBaseException(UserStatusCache.class.getName(), methodName,je.getMessage());
	 			 }catch(NoSuchElementException  ex){
	 				 LOG.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	 				 LOG.errorTrace(methodName, ex);
	 	   	         EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserStatusCache[loadUserStatusDetailsAtStartUp]", "", "", "", "NoSuchElementException :" + ex.getMessage());
	 	   	 	throw new BTSLBaseException(UserStatusCache.class.getName(), methodName,ex.getMessage());
	 			 }catch (Exception e) {
	 				 LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
	 				 LOG.errorTrace(methodName, e);
	 	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserStatusCache[loadUserStatusDetailsAtStartUp]", "", "", "", "Exception :" + e.getMessage());
	 	       	throw new BTSLBaseException(UserStatusCache.class.getName(), methodName,e.getMessage());
	 			 }
	 			 finally {
	 		        	if (jedis != null) {
	 		        	jedis.close();
	 		        }
	 		     RedisActivityLog.log("UserStatusCache->loadUserStatusDetailsAtStartUp->End");
	 			}
   			} else {	 
				   _userStatusDetailMap = loadMapping();
		   }	
        }
        catch(BTSLBaseException be) {
        	LOG.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
        	LOG.errorTrace(methodName, be);
        	throw be;
        }
        catch (Exception e)
        {
        	LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
        	LOG.errorTrace(methodName, e);
        } finally {
        	if (LOG.isDebugEnabled()) {
        		LOG.debug(methodName, PretupsI.EXITED);
        	}
        }

    }

    public static Map<String, UserStatusVO> loadMapping() throws BTSLBaseException {
    	final String methodName = "loadMapping";
    	if (LOG.isDebugEnabled()) {
    		LOG.debug(methodName, PretupsI.ENTERED);
    	}

        UserDAO userDAO = null;
        try {
            userDAO = new UserDAO();
            _userStatusDetailMap = userDAO.loadUserStatusDetails();
        }
        catch(BTSLBaseException be) {
        	LOG.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
        	LOG.errorTrace(methodName, be);
        	throw be;
        }
        catch (Exception e)
        {
        	LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
        	LOG.errorTrace(methodName, e);
        } finally {
        	if (LOG.isDebugEnabled()) {
        		LOG.debug(methodName, PretupsI.EXITED);
        	}
        }

        return _userStatusDetailMap;
    }

    public static Object getObject(String p_networkCode, String p_categoryCode, String p_userType, String p_gatewayType) {

        UserStatusVO userStatusVO = null;
        String key = null;
        String methodName = "getObject";
        if (LOG.isDebugEnabled()) {
            LOG.debug("getObject()", "Entered  p_networkCode=" + p_networkCode + " p_categoryCode: " + p_categoryCode + " p_userType: " + p_userType + " p_gatewayType " + p_gatewayType);
        }
        key = p_networkCode + "_" + p_categoryCode + "_" + p_userType + "_" + p_gatewayType;
      	if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
					 try {
						userStatusVO = (UserStatusVO) userStatusDetailMemo.get(key);
					} catch (ExecutionException e) {
						LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
						LOG.errorTrace(methodName, e);
		   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserStatusCache[getObject]", "", "", "", "ExecutionException :" + e.getMessage());
					}
		        	catch (InvalidCacheLoadException e) { 
		        		LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
		        		LOG.errorTrace(methodName, e);
		   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserStatusCache[getObject]", "", "", "", "InvalidCacheLoadException :" + e.getMessage());	
					}
				        if (userStatusVO == null) {
				            key = p_networkCode + "_" + PretupsI.ALL + "_" + p_userType + "_" + p_gatewayType;
				            try {
								userStatusVO = (UserStatusVO) userStatusDetailMemo.get(key);
							} catch (ExecutionException e) {
								LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
								LOG.errorTrace(methodName, e);
				   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserStatusCache[getObject]", "", "", "", "ExecutionException :" + e.getMessage());
							}
				        	catch (InvalidCacheLoadException e) { 
				        		LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
				        		LOG.errorTrace(methodName, e);
				   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserStatusCache[getObject]", "", "", "", "InvalidCacheLoadException :" + e.getMessage());	
							}
				        }
				        if (userStatusVO == null) {
				            key = p_networkCode + "_" + p_categoryCode + "_" + p_userType + "_" + PretupsI.ALL;
				            try {
								userStatusVO = (UserStatusVO) userStatusDetailMemo.get(key);
							} catch (ExecutionException e) {
								LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
								LOG.errorTrace(methodName, e);
				   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserStatusCache[getObject]", "", "", "", "ExecutionException :" + e.getMessage());
							}
				        	catch (InvalidCacheLoadException e) { 
				        		LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
				        		LOG.errorTrace(methodName, e);
				   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserStatusCache[getObject]", "", "", "", "InvalidCacheLoadException :" + e.getMessage());	
							}
				        }
				        if (userStatusVO == null) {
				            key = p_networkCode + "_" + p_categoryCode + "_" + PretupsI.ALL + "_" + p_gatewayType;
				            try {
								userStatusVO = (UserStatusVO) userStatusDetailMemo.get(key);
							}catch (ExecutionException e) {
								LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
								LOG.errorTrace(methodName, e);
				   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserStatusCache[getObject]", "", "", "", "ExecutionException :" + e.getMessage());
							}
				        	catch (InvalidCacheLoadException e) { 
				        		LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
				        		LOG.errorTrace(methodName, e);
				   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserStatusCache[getObject]", "", "", "", "InvalidCacheLoadException :" + e.getMessage());	
							}
				        }
				        if (userStatusVO == null) {
				            key = p_networkCode + "_" + PretupsI.ALL + "_" + p_userType + "_" + PretupsI.ALL;
				            try {
								userStatusVO = (UserStatusVO) userStatusDetailMemo.get(key);
							} catch (ExecutionException e) {
								LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
								LOG.errorTrace(methodName, e);
				   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserStatusCache[getObject]", "", "", "", "ExecutionException :" + e.getMessage());
							}
				        	catch (InvalidCacheLoadException e) { 
				        		LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
				        		LOG.errorTrace(methodName, e);
				   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserStatusCache[getObject]", "", "", "", "InvalidCacheLoadException :" + e.getMessage());	
							}
				        }
				        if (userStatusVO == null) {
				            key = p_networkCode + "_" + PretupsI.ALL + "_" + PretupsI.ALL + "_" + p_gatewayType;
				            try {
								userStatusVO = (UserStatusVO) userStatusDetailMemo.get(key);
							}catch (ExecutionException e) {
								LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
								LOG.errorTrace(methodName, e);
				   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserStatusCache[getObject]", "", "", "", "ExecutionException :" + e.getMessage());
							}
				        	catch (InvalidCacheLoadException e) { 
				        		LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
				        		LOG.errorTrace(methodName, e);
				   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserStatusCache[getObject]", "", "", "", "InvalidCacheLoadException :" + e.getMessage());	
							}
				        }
				        if (userStatusVO == null) {
				            key = p_networkCode + "_" + p_categoryCode + "_" + PretupsI.ALL + "_" + PretupsI.ALL;
				            try {
								userStatusVO = (UserStatusVO) userStatusDetailMemo.get(key);
							} catch (ExecutionException e) {
								LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
								LOG.errorTrace(methodName, e);
				   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserStatusCache[getObject]", "", "", "", "ExecutionException :" + e.getMessage());
							}
				        	catch (InvalidCacheLoadException e) { 
				        		LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
				        		LOG.errorTrace(methodName, e);
				   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserStatusCache[getObject]", "", "", "", "InvalidCacheLoadException :" + e.getMessage());	
							}
				        }
				        if (userStatusVO == null) {
				            key = p_networkCode + "_" + PretupsI.ALL + "_" + PretupsI.ALL + "_" + PretupsI.ALL;
				            try {
								userStatusVO = (UserStatusVO) userStatusDetailMemo.get(key);
							}catch (ExecutionException e) {
								LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
								LOG.errorTrace(methodName, e);
				   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserStatusCache[getObject]", "", "", "", "ExecutionException :" + e.getMessage());
							}
				        	catch (InvalidCacheLoadException e) { 
				        		LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
				        		LOG.errorTrace(methodName, e);
				   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserStatusCache[getObject]", "", "", "", "InvalidCacheLoadException :" + e.getMessage());	
							}
					}
				}
	    	   else { 
	        userStatusVO = _userStatusDetailMap.get(key);
	        if (userStatusVO == null) {
	            key = p_networkCode + "_" + PretupsI.ALL + "_" + p_userType + "_" + p_gatewayType;
	            userStatusVO = _userStatusDetailMap.get(key);
	        }
	        if (userStatusVO == null) {
	            key = p_networkCode + "_" + p_categoryCode + "_" + p_userType + "_" + PretupsI.ALL;
	            userStatusVO = _userStatusDetailMap.get(key);
	        }
	        if (userStatusVO == null) {
	            key = p_networkCode + "_" + p_categoryCode + "_" + PretupsI.ALL + "_" + p_gatewayType;
	            userStatusVO = _userStatusDetailMap.get(key);
	        }
	        if (userStatusVO == null) {
	            key = p_networkCode + "_" + PretupsI.ALL + "_" + p_userType + "_" + PretupsI.ALL;
	            userStatusVO = _userStatusDetailMap.get(key);
	        }
	        if (userStatusVO == null) {
	            key = p_networkCode + "_" + PretupsI.ALL + "_" + PretupsI.ALL + "_" + p_gatewayType;
	            userStatusVO = _userStatusDetailMap.get(key);
	        }
	        if (userStatusVO == null) {
	            key = p_networkCode + "_" + p_categoryCode + "_" + PretupsI.ALL + "_" + PretupsI.ALL;
	            userStatusVO = _userStatusDetailMap.get(key);
	        }
	        if (userStatusVO == null) {
	            key = p_networkCode + "_" + PretupsI.ALL + "_" + PretupsI.ALL + "_" + PretupsI.ALL;
	            userStatusVO = _userStatusDetailMap.get(key);
	        }
		}
        if (LOG.isDebugEnabled()) {
            LOG.debug("getObject()", "Exited " + userStatusVO);
        }
        return userStatusVO;
    }

    public static void updateUserStatusMapping() throws BTSLBaseException {

    	final String methodName = "updateUserStatusMapping";
    	if (LOG.isDebugEnabled()) {
    		LOG.debug(methodName, PretupsI.ENTERED);
    	}
	   try{
	       Map<String, UserStatusVO> currentMap = loadMapping();
		        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
	   	         	RedisActivityLog.log("UserStatusCache->updateUserStatusMapping->Start");
	   	     	Jedis jedis = null;
	   			 try {
	   				 jedis = RedisConnectionPool.getPoolInstance().getResource();
	   				 Pipeline pipeline = jedis.pipelined();
	   				 pipeline.del(hKeyUserStatusDetailMap);
							 for (Entry<String, UserStatusVO> entry : currentMap.entrySet())  {
						       pipeline.hset(hKeyUserStatusDetailMap, entry.getKey(), gson.toJson(entry.getValue()));
							 }
					  pipeline.sync();	 
					  }catch(JedisConnectionException je){
			 				 LOG.error(methodName, PretupsI.EXCEPTION + je.getMessage());
			 				 LOG.errorTrace(methodName, je);
			 	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserStatusCache[updateUserStatusMapping]", "", "", "", "JedisConnectionException :" + je.getMessage());
			 	   			throw new BTSLBaseException(UserStatusCache.class.getName(), methodName,je.getMessage());
					  }catch(NoSuchElementException  ex){
			 				 LOG.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
			 				 LOG.errorTrace(methodName, ex);
			 	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserStatusCache[updateUserStatusMapping]", "", "", "", "NoSuchElementException :" + ex.getMessage());
			 	   			throw new BTSLBaseException(UserStatusCache.class.getName(), methodName,ex.getMessage());
					  }catch (Exception e) {
			 				 LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
			 				 LOG.errorTrace(methodName, e);
			 	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserStatusCache[updateUserStatusMapping]", "", "", "", "Exception :" + e.getMessage());
			 				throw new BTSLBaseException(UserStatusCache.class.getName(), methodName,e.getMessage());
					  }
			 			 finally {
			 		        	if (jedis != null) {
			 		        	jedis.close();
			 		        }
		   	        RedisActivityLog.log("UserStatusCache->updateUserStatusMapping->End");
		   	 } 
	   			 }else {
	         _userStatusDetailMap = currentMap;
		 }
	   } catch(BTSLBaseException be) {
        	LOG.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
        	LOG.errorTrace(methodName, be);
        	throw be;
        }
        catch (Exception e)
        {
        	LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
        	LOG.errorTrace(methodName, e);
        } finally {
        	if (LOG.isDebugEnabled()) {
        		LOG.debug(methodName, PretupsI.EXITED);
        		LOG.debug("updateCommissionProfileMapping", PretupsI.EXITED + _userStatusDetailMap.size());
        	}
        }

    }
    
    public static UserStatusVO getObjectFromRedis(String key) throws BTSLBaseException{
        if (LOG.isDebugEnabled()) {
        	LOG.debug("getObjectFromRedis()", "Entered Key: " + key);
        }
        String methodName = "getObjectFromRedis";
        RedisActivityLog.log("UserStatusCache->getObjectFromRedis->Start");
    	Jedis jedis = null;
    	UserStatusVO  userStatusVO = null;
		 try {
			 jedis = RedisConnectionPool.getPoolInstance().getResource();
			 String userStatusObj = jedis.hget(hKeyUserStatusDetailMap, key);
			 if(!BTSLUtil.isNullString(userStatusObj))
				 userStatusVO = (UserStatusVO) gson.fromJson(userStatusObj,UserStatusVO.class);
		 }catch(JedisConnectionException je){
			 LOG.error(methodName, PretupsI.EXCEPTION + je.getMessage());
			 LOG.errorTrace(methodName, je);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserStatusCache[getObjectFromRedis]", "", "", "", "JedisConnectionException :" + je.getMessage());
   		        throw new BTSLBaseException(UserStatusCache.class.getName(), methodName,je.getMessage());
		 }catch(NoSuchElementException  ex){
			 LOG.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
			 LOG.errorTrace(methodName, ex);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserStatusCache[getObjectFromRedis]", "", "", "", "NoSuchElementException :" + ex.getMessage());
   		        throw new BTSLBaseException(UserStatusCache.class.getName(), methodName,ex.getMessage());
		 }catch (Exception e) {
			 LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
		 	LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserStatusCache[getObjectFromRedis]", "", "", "", "Exception :" + e.getMessage());
			throw new BTSLBaseException(UserStatusCache.class.getName(), methodName,e.getMessage());
		 }
		 finally {
	        	if (jedis != null) {
	        	jedis.close();
	        	}
	        }
		 RedisActivityLog.log("UserStatusCache->getObjectFromRedis->End");
      return userStatusVO;
    }
    

}
