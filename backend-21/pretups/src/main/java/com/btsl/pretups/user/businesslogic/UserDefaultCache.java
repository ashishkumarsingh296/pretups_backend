/**
 * @(#)UserDefaultCache.java
 *                           Copyright(c) 2009, Comviva Technologies Ltd.
 *                           All Rights Reserved
 * 
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Author Date History
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Ankur Dhawan 25/08/2011 Initial Creation
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           This class is used for Default User Cache updation
 * 
 */

package com.btsl.pretups.user.businesslogic;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import com.btsl.pretups.network.businesslogic.NetworkCache;
import com.btsl.redis.pool.RedisConnectionPool;
import com.btsl.redis.util.RedisActivityLog;
import com.btsl.user.businesslogic.UserDAO;
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

public class UserDefaultCache implements Runnable {
	public static final String classname = "UserDefaultCache";

    private static Log _log = LogFactory.getLog(UserDefaultCache.class.getName());
    private static HashMap<String, Object> _userDefaultMap = new HashMap<String, Object>();
	private static String redisEnable = BTSLUtil.NullToString(Constants.getProperty("REDIS_ENABLE"));
    private static final String hKeyUserDefaultMap = "UserDefaultCache";
 
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss:mss").create();
	private static final int keyTimer = Integer.parseInt(Constants.getProperty("KEYTIMER"));
	
	private static LoadingCache<String, HashMap<String, Object>> userDefaultMapcache = CacheBuilder.newBuilder()
	 .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)
	 .build(new CacheLoader<String,HashMap<String, Object>>(){
			@Override
			public HashMap<String, Object> load(String key) throws Exception {
				return getUserDefaultMapFromRedis(key);
			}
	  });

    public void run() {
        try {
            Thread.sleep(50);
            loadUserDefaultConfigAtStartup();
        } catch (Exception e) {
        	 _log.error("UserDefaultCache init() Exception ", e);
        }
    }
    public static HashMap<String, Object> getUserDefaultMap() {
    	 try{
    		 if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
         	_userDefaultMap = userDefaultMapcache.get(hKeyUserDefaultMap);
    		 }
         }catch (ExecutionException e) {
    		_log.error("getUserDefaultMap", PretupsI.EXCEPTION + e.getMessage());
    		_log.errorTrace("getUserDefaultMap", e);
   	        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "UserDefaultCache[getUserDefaultMap]", "", "", "", "ExecutionException :" + e.getMessage());
		}catch (InvalidCacheLoadException e) { 
    		_log.info("getUserDefaultMap", e);
		}
  		return _userDefaultMap;
  	}    
    /**
     * @author ankur.dhawan
     *         Description : This method loads the default user config cache at
     *         startup
     *         Method : loadUserDefaultConfigAtStartup
     * @return
     * @throws Exception 
     */

    public static void loadUserDefaultConfigAtStartup() throws BTSLBaseException {
    	final String methodName = "loadUserDefaultConfigAtStartup";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
    	Jedis jedis =null;
       try{
    	   if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
    		   RedisActivityLog.log("UserDefaultCache->loadUserDefaultConfigAtStartup->Start");
    		   HashMap<String, Object> currentMap =loadMapping();
  			   jedis = RedisConnectionPool.getPoolInstance().getResource();
  				//If key is already present in redis db do not reload
   				if(!jedis.exists(hKeyUserDefaultMap)) {
 				   jedis.set(hKeyUserDefaultMap, gson.toJson(_userDefaultMap));
   			   }
  			   RedisActivityLog.log("UserDefaultCache->loadUserDefaultConfigAtStartup->End");
    	   }else{
        	   _userDefaultMap = loadMapping();
    	   }
    	 }
       catch(BTSLBaseException be) {
    		_log.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
    		_log.errorTrace(methodName, be);
    		throw be;
    	}catch(JedisConnectionException je){
    		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
			_log.errorTrace(methodName, je);
           EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "UserDefaultCache[" + methodName +"]", "", "", "", "JedisConnectionException :" + je.getMessage());
		}catch(NoSuchElementException  ex){
			_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
			_log.errorTrace(methodName, ex);
           EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "UserDefaultCache[" + methodName +"]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		}catch (Exception e){
    		_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
    		_log.errorTrace(methodName, e);
    	} finally {
	   		 if(jedis != null ) {
				 jedis.close();
			 }
    		if (_log.isDebugEnabled()) {
    			_log.debug(methodName, PretupsI.EXITED);
    		}
    	}
		
    	
    }

    /**
     * @author ankur.dhawan
     *         Description : This method loads the user default configuration
     *         mapping
     *         Method : loadMapping
     * @return HashMap
     * @throws Exception 
     */

    private static HashMap<String, Object> loadMapping() throws BTSLBaseException {
    	final String methodName = "loadMapping";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}

        HashMap<String, Object> userMap = null;
        UserDAO userDAO = null;
        try {
            userDAO = new UserDAO();
            userMap = userDAO.loadUserDefaultConfigCache();
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
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED);
        	}
        }
        return userMap;
    }

    /**
     * @author ankur.dhawan
     *         Description : This method updates the map that contains the
     *         default user configuration and
     *         also updates the users if this configuration is changed
     *         Method : updateUserDefaultConfig
     * @return
     * @throws Exception 
     */

    public static void updateUserDefaultConfig() throws BTSLBaseException {
    	final String methodName = "updateUserDefaultConfig";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
    	Jedis jedis = null;
        try{
	        final HashMap<String, Object> currentMap = loadMapping();
	        HashMap<String, Object> modifiedMap = null;
	    	if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
	    		RedisActivityLog.log("UserDefaultCache->updateUserDefaultConfig->Start");
	    	    jedis = RedisConnectionPool.getPoolInstance().getResource();
	    	    Pipeline pipeline = jedis.pipelined();
	  	        String jsonObj = pipeline.get(hKeyUserDefaultMap).get();
				Type classType = new TypeToken<HashMap<String,Object>>() {}.getType();
				HashMap<String,Object> cachedMap = gson.fromJson(jsonObj, classType);
	  	        if ((cachedMap != null) && (cachedMap.size() > 0)) {
	  	        	modifiedMap = compareMaps(cachedMap, currentMap);
	  	        }
	  	        pipeline.del(hKeyUserDefaultMap);
	  		    pipeline.set(hKeyUserDefaultMap, gson.toJson(currentMap));
	  			pipeline.sync();
	     		RedisActivityLog.log("UserDefaultCache->updateUserDefaultConfig->End");	 
	  	    }else {
		        if (_userDefaultMap != null) {
		            modifiedMap = compareMaps(_userDefaultMap, currentMap);
		        }
		        _userDefaultMap = currentMap;
		  	}
	    	if (modifiedMap != null && modifiedMap.size() > 0) {
	            updateUsers(modifiedMap);
	        }	
        }
        catch(BTSLBaseException be) {
        	_log.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
        	_log.errorTrace(methodName, be);
        	throw be;
        }catch(JedisConnectionException je){
    		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
			_log.errorTrace(methodName, je);
           EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "UserDefaultCache[" + methodName +"]", "", "", "", "JedisConnectionException :" + je.getMessage());
		}catch(NoSuchElementException  ex){
			_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
			_log.errorTrace(methodName, ex);
           EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "UserDefaultCache[" + methodName +"]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		}
        catch (Exception e)
        {
        	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
        	_log.errorTrace(methodName, e);
        } finally {
        	if(jedis!=null)
        		jedis.close();
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED);
        		_log.debug("updateUserDefaultConfig", PretupsI.EXITED + _userDefaultMap.size());
        	}
        }
    }

    /**
     * @author ankur.dhawan
     *         Description : This method compares the current map and the map
     *         already present in the cache.
     *         Method : compareMaps
     * @return HashMap
     */

    private static HashMap<String, Object> compareMaps(HashMap p_prevMap, HashMap p_currMap) {
        final String METHOD_NAME = "compareMaps";
        if (_log.isDebugEnabled()) {
            _log.debug("compareMaps", "Entered : p_prevMap= " + p_prevMap + " p_currMap= " + p_currMap);
        }

        final HashMap<String, Object> modifiedMap = new HashMap<String, Object>();

        try {
            Iterator<String> iterator = null;
            // Iterator copiedIterator=null;
            final Collection<String> keySet = new HashSet<String>();
            keySet.addAll(p_prevMap.keySet());
            keySet.addAll(p_currMap.keySet());
            iterator = keySet.iterator();
            String key;
            while (iterator.hasNext()) {
                key = (String) (iterator.next());
                final Object prevObj = p_prevMap.get(key);
                final Object currObj = p_currMap.get(key);

                if (prevObj != null && currObj == null) {
                    CacheOperationLog.log("UserDefaultCache", PretupsI.CACHE_ACTION_DELETE + key + " " + prevObj.toString());
                    // modifiedMap.put(key, prevObj);
                } else if (prevObj == null && currObj != null) {
                    CacheOperationLog.log("UserDefaultCache", PretupsI.CACHE_ACTION_ADD + key + " " + currObj.toString());
                    modifiedMap.put(key, currObj);
                } else if (prevObj != null && currObj != null) {
                    if (!prevObj.equals(currObj)) {
                        CacheOperationLog.log("UserDefaultCache", PretupsI.CACHE_ACTION_MODIFY + key + " " + prevObj.toString() + " -> " + currObj.toString());
                        modifiedMap.put(key, currObj);
                    }
                }
            }
        } catch (Exception e) {
            _log.error("compareMaps", "Exception e:" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("compareMaps", "Exited");
        }
        return modifiedMap;
    }

    /**
     * @author ankur.dhawan
     *         Description : This method updates the users based on the control
     *         preference
     *         Method : updateUsers
     * @return
     */

    private static void updateUsers(HashMap<String, Object> p_modifiedMap) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("updateUsers", "Entered : p_modifiedMap= " + p_modifiedMap);
        }

        int updateCount = 0;
        final UserDAO userDAO = new UserDAO();
        final HashMap networkMap = NetworkCache.getNetworkMap();
        final Iterator<String> nwCodeIterator = networkMap.keySet().iterator();
        while (nwCodeIterator.hasNext()) {
            updateCount = userDAO.updateUsersFromCache(p_modifiedMap, nwCodeIterator.next());
        }
        if (_log.isDebugEnabled()) {
            _log.debug("updateUsers", "Exited : updateCount= " + updateCount);
        }

    }

    /**
     * @author ankur.dhawan
     *         Description : This method returns a map containing default user
     *         configuration for a particular category
     *         Method : updateUsersFromCache
     * @throws BTSLBaseException
     * @return
     */

    public static HashMap<String, Object> getCategoryDefaultConfig(String p_categoryCode) {
        if (_log.isDebugEnabled()) {
            _log.debug("getCategoryDefaultConfig", "Entered :p_categoryCode " + p_categoryCode);
        }
     	 HashMap<String, Object> userMap = new HashMap<String, Object>();
     	 if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
            	_userDefaultMap = getUserDefaultMap();
       		 }
         final Iterator<String> iterator = _userDefaultMap.keySet().iterator();
         final String catPattern = p_categoryCode + "_";

         while (iterator.hasNext()) {
             final String key = iterator.next();
             if (key.startsWith(catPattern)) {
                 userMap.put(key, _userDefaultMap.get(key));
             }
         }
         if (userMap.isEmpty()) {
             userMap = null;
         }
        if (_log.isDebugEnabled()) {
            _log.debug("getCategoryDefaultConfig", "Exiting :userMap.size()= " + userMap.size());
        }
       
        return userMap;
    }

    /**
     * @author ankur.dhawan
     *         Description : This method checks whether the user default cache
     *         is updated from DB or not
     *         Method : isCacheUpdated
     * @return
     * @throws BTSLBaseException 
     */

    public static boolean isCacheUpdated() throws BTSLBaseException {
    	final String methodName = "isCacheUpdated";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}

        boolean isUpdated = false;

        try {
        	final HashMap<String, Object> currentMap = loadMapping();
            HashMap<String, Object> modifiedMap = null;
            if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
               	_userDefaultMap = getUserDefaultMap();
          		 }
            
            if (_userDefaultMap != null) {
                modifiedMap = compareMaps(_userDefaultMap, currentMap);
            }

            if ((modifiedMap != null && !(modifiedMap.isEmpty()))) {
                isUpdated = true;
            }
            if (_userDefaultMap != null && _userDefaultMap.size() > currentMap.size()) {
                isUpdated = false;
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
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED + isUpdated);
        	}
        }
        return isUpdated;
    }

    public static HashMap<String, Object> getUserDefaultMapFromRedis(String key){
        if (_log.isDebugEnabled()) {
        	_log.debug("getUserDefaultMapcache()", "Entered key: "+key);
        }
        String methodName ="getUserDefaultMapcache";
        HashMap<String,Object> cachedMap = new HashMap<String,Object>();
        Jedis jedis = null;
		 try {
	        RedisActivityLog.log("UserDefaultCache->getUserDefaultMapcache->Start");
			jedis = RedisConnectionPool.getPoolInstance().getResource();
			String jsonObj = jedis.get(key);
			Type classType = new TypeToken<HashMap<String,Object>>() {}.getType();
			if(jsonObj != null)
				cachedMap = gson.fromJson(jsonObj, classType);
	         RedisActivityLog.log("UserDefaultCache->getUserDefaultMapcache->End");
		 }catch(JedisConnectionException je){
			_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
			_log.errorTrace(methodName, je);
           EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "UserDefaultCache[" + methodName +"]", "", "", "", "JedisConnectionException :" + je.getMessage());
		}catch(NoSuchElementException  ex){
			_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
			_log.errorTrace(methodName, ex);
           EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "UserDefaultCache[" + methodName +"]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		}catch (Exception e) {
			_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
			_log.errorTrace(methodName, e);
           EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "UserDefaultCache[" + methodName +"]", "", "", "", "Exception :" + e.getMessage());
		}
    	 finally {
    		 if(jedis != null ) {
    			 jedis.close();
    		 }
    	   }
		return cachedMap;
	}
    
    
    /**
     * @author ashish.singh
     *         Description : This method returns a map containing default user
     *         configuration for a particular category
     *         Method : getCategoryDefaultConfigFromRest
     * @throws BTSLBaseException
     * @return
     */

    public static HashMap<String, Object> getCategoryDefaultConfigFromRest(String p_categoryCode)  throws BTSLBaseException {
    	final String METHOD_NAME = "getCategoryDefaultConfigFromRest";

    	if (_log.isDebugEnabled()) {
    		_log.debug(METHOD_NAME, "Entered :p_categoryCode " + p_categoryCode);
    	}
    	HashMap<String, Object> userMap = new HashMap<String, Object>();
    	if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
    		_userDefaultMap = getUserDefaultMap();
    	}
    	final Iterator<String> iterator = _userDefaultMap.keySet().iterator();
    	final String catPattern = p_categoryCode + "_";

    	while (iterator.hasNext()) {
    		final String key = iterator.next();
    		if (key.startsWith(catPattern)) {
    			userMap.put(key, _userDefaultMap.get(key));
    		}
    	}
    	if (userMap.isEmpty()) {
    		userMap = null;
    	}
    	if(userMap!=null) {
    		if (_log.isDebugEnabled()) {
    			_log.debug(METHOD_NAME, "Exiting :userMap.size()= " + userMap.size());
    		}
    	}else {
    		throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.USERDEFAULT_CATEGORY_NOT_FOUND);
    	}
    	return userMap;
    }

}