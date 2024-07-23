/*
 * @# SubscriberRoutingControlCache.java
 * 
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * --
 * Sandeep Goel Oct 30, 2005 Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */

package com.btsl.pretups.routing.master.businesslogic;

import java.util.HashMap;
import java.util.Iterator;
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
 * 
 */
public class SubscriberRoutingControlCache implements Runnable {

    /**
     * Field _routingMap.
     */
    private static HashMap<String,SubscriberRoutingControlVO> _routingMap = new HashMap<String,SubscriberRoutingControlVO>();
    private static String redisEnable = BTSLUtil.NullToString(Constants.getProperty("REDIS_ENABLE"));
    private static final String hKeySubscriberRoutingControlMap = "SubscriberRoutingControlCache";
    private static  int keyTimer = Integer.parseInt(Constants.getProperty("KEYTIMER"));
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss:mss").create();

    private static LoadingCache<String,SubscriberRoutingControlVO>  routingControlCache = CacheBuilder.newBuilder()
	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)     
	    .build(new CacheLoader<String,SubscriberRoutingControlVO>(){
			@Override
			public SubscriberRoutingControlVO load(String key) throws Exception {
				return getRoutingControlObjectFromRedis(key);
			}
	     });


    public void run() {
        try {
            Thread.sleep(50);
            loadSubsdcriberRoutingControlAtStartUp();
        } catch (Exception e) {
        	 _log.error("SubscriberRoutingControlCache init() Exception ", e);
        }
    }
    /**
     * Field _log.
     */
    private static Log _log = LogFactory.getLog(SubscriberRoutingControlCache.class.getName());
    /**
     * Field _routingControlDAO.
     */
    private static RoutingControlDAO _routingControlDAO = new RoutingControlDAO();

    /**
     * Method loadSubsdcriberRoutingControlAtStartUp.
     * @throws Exception 
     */
    public static void loadSubsdcriberRoutingControlAtStartUp() throws Exception {
    	final String methodName = "loadSubsdcriberRoutingControlAtStartUp";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        try {
            if (_log.isDebugEnabled()) {
                _log.debug("loadSubsdcriberRoutingControlAtStartUp", " Before loading:" + _routingMap);
            }
            if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        	 Jedis jedis = null;
    	   	 try {
	             RedisActivityLog.log("SubscriberRoutingControlCache->loadSubsdcriberRoutingControlAtStartUp->Start");
	   			 jedis = RedisConnectionPool.getPoolInstance().getResource();
	   			 Pipeline pipeline = jedis.pipelined();
				//If key is already present in redis db do not reload
				if(!jedis.exists(hKeySubscriberRoutingControlMap)) {
					HashMap<String,SubscriberRoutingControlVO>routingControlMap = _routingControlDAO.loadRoutingControlDetails();
				    for (Entry<String, SubscriberRoutingControlVO> entry : routingControlMap.entrySet())  {
					 pipeline.hset(hKeySubscriberRoutingControlMap, entry.getKey(), gson.toJson(entry.getValue()));
			         }
				 pipeline.sync();
		         }
		    	RedisActivityLog.log("SubscriberRoutingControlCache->loadSubsdcriberRoutingControlAtStartUp->End");
    	   	   }catch(JedisConnectionException je){
				_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
   		        _log.errorTrace(methodName, je);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberRoutingControlCache[loadSubsdcriberRoutingControlAtStartUp]", "", "", "", "JedisConnectionException :" + je.getMessage());
			}catch(NoSuchElementException  ex){
				_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
   		        _log.errorTrace(methodName, ex);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberRoutingControlCache[loadSubsdcriberRoutingControlAtStartUp]", "", "", "", "NoSuchElementException :" + ex.getMessage());
			}catch (Exception e){
				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
   		        _log.errorTrace(methodName, e);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberRoutingControlCache[loadSubsdcriberRoutingControlAtStartUp]", "", "", "", "Exception :" + e.getMessage());
			}finally{
				if(jedis != null)
					jedis.close();
			}
       	  } else {	 
       		_routingMap = _routingControlDAO.loadRoutingControlDetails();
       	}
            if (_log.isDebugEnabled()) {
                _log.debug("loadSubsdcriberRoutingControlAtStartUp", " After loading:" + _routingMap.size());
            }
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
    }
    
    
    /**
     * Method refreshSubscriberRoutingControl.
     * @throws Exception 
     */
    public static void refreshSubscriberRoutingControl() throws Exception {
    	final String methodName = "refreshSubscriberRoutingControl";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        try {
            HashMap<String,SubscriberRoutingControlVO> tempMap = null;
            if (_log.isDebugEnabled()) {
                _log.debug("refreshSubscriberRoutingControl", " Before loading:" + _routingMap);
            }
            tempMap = _routingControlDAO.loadRoutingControlDetails();
            if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
    	    Jedis jedis = null;
    	    try {
   	   		 	 RedisActivityLog.log("SubscriberRoutingControlCache->refreshSubscriberRoutingControl->Start");
	   			 jedis = RedisConnectionPool.getPoolInstance().getResource();
	   			 Pipeline pipeline = jedis.pipelined();
	   			 /*HashMap<String, Object> cachedMap = RedisUtil.deserialize(jedis.hgetAll(hKeySubscriberRoutingControlMap));
					if ((cachedMap != null) && (cachedMap.size() > 0)) {
			            compareMaps(cachedMap, tempMap);
			       }*/
	   			 jedis.del(hKeySubscriberRoutingControlMap);
				 for (Entry<String, SubscriberRoutingControlVO> entry : tempMap.entrySet())  {
					 pipeline.hset(hKeySubscriberRoutingControlMap, entry.getKey(), gson.toJson(entry.getValue()));
			         }
				 pipeline.sync();
	    	    RedisActivityLog.log("SubscriberRoutingControlCache->refreshSubscriberRoutingControl->End");
   	   	   }catch(JedisConnectionException je){
				_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
  		        _log.errorTrace(methodName, je);
  	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberRoutingControlCache[refreshSubscriberRoutingControl]", "", "", "", "JedisConnectionException :" + je.getMessage());
			}catch(NoSuchElementException  ex){
				_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
  		        _log.errorTrace(methodName, ex);
  	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberRoutingControlCache[refreshSubscriberRoutingControl]", "", "", "", "NoSuchElementException :" + ex.getMessage());
			}catch (Exception e){
				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
  		        _log.errorTrace(methodName, e);
  	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberRoutingControlCache[refreshSubscriberRoutingControl]", "", "", "", "Exception :" + e.getMessage());
			}finally{
				if(jedis != null)
					jedis.close();
			}
   	   	 } else {	 
       		 compareMaps(_routingMap, tempMap);
             _routingMap = tempMap;
       	}
            if (_log.isDebugEnabled()) {
                _log.debug("refreshSubscriberRoutingControl", " After loading:" + _routingMap.size());
            }
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
                SubscriberRoutingControlVO prevVO = (SubscriberRoutingControlVO) p_previousMap.get(key);
                SubscriberRoutingControlVO curVO = (SubscriberRoutingControlVO) p_currentMap.get(key);

                if (prevVO != null && curVO == null) {
                    isNewAdded = true;
                    _log.info("compareMaps()", BTSLUtil.formatMessage("Delete", prevVO.getInterfaceCategory(), prevVO.logInfo()));
                } else if (prevVO == null && curVO != null) {
                    _log.info("compareMaps()", BTSLUtil.formatMessage("Add", curVO.getInterfaceCategory(), curVO.logInfo()));
                } else if (prevVO != null && curVO != null) {
                    if (!curVO.equals(prevVO)) {
                        _log.info("compareMaps()", BTSLUtil.formatMessage("Modify", curVO.getInterfaceCategory(), curVO.differences(prevVO)));
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
                    SubscriberRoutingControlVO mappingVO = (SubscriberRoutingControlVO) p_currentMap.get(iterator2.next());
                    _log.info("compareMaps()", BTSLUtil.formatMessage("Add", mappingVO.getInterfaceCategory(), mappingVO.logInfo()));
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
     * Method getRoutingControlDetails.
     * 
     * @param p_key
     *            String
     * @return SubscriberRoutingControlVO
     */
    public static SubscriberRoutingControlVO getRoutingControlDetails(String p_key) {
        if (_log.isDebugEnabled()) {
            _log.debug("getRoutingControlDetails()", "Entered p_key: " + p_key);
        }
        SubscriberRoutingControlVO subscriberRoutingControlVO = null;
        try{
        	if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        	subscriberRoutingControlVO = routingControlCache.get(p_key);
        } else {
    		subscriberRoutingControlVO = (SubscriberRoutingControlVO) _routingMap.get(p_key);
        }
        }catch (InvalidCacheLoadException e) { 
			_log.error("getRoutingControlDetails", PretupsI.EXCEPTION + e.getMessage());
		     _log.errorTrace("getRoutingControlDetails", e);
       }catch(ExecutionException ex){
     		_log.error("getRoutingControlDetails", PretupsI.EXCEPTION + ex.getMessage());
	        _log.errorTrace("getRoutingControlDetails", ex);
           EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberRoutingControlCache[getRoutingControlDetails]", "", "", "", "ExecutionException :" + ex.getMessage());
       } catch (Exception e) {
           _log.errorTrace("Exception in getRoutingControlDetails() ", e);
           EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberRoutingControlCache[getRoutingControlDetails]", "", "", "", "Exception:" + e.getMessage());
       }
       
        return subscriberRoutingControlVO;
    }

    /**
     * @param key
     * @return
     */
    public static SubscriberRoutingControlVO getRoutingControlObjectFromRedis(String key) {
    	String methodName = "getRoutingControlObjectFromRedis";
    	if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered key: " + key);
        }
        RedisActivityLog.log("SubscriberRoutingControlCache->getRoutingControlObjectFromRedis->Start");
    	Jedis jedis = null;
        SubscriberRoutingControlVO subscriberRoutingControlVO = null;
		 try {
			 jedis = RedisConnectionPool.getPoolInstance().getResource();
			 String json = jedis.hget(hKeySubscriberRoutingControlMap, key);
			if(!BTSLUtil.isNullString(json))
				subscriberRoutingControlVO = gson.fromJson(json, SubscriberRoutingControlVO.class); 
		 }catch(JedisConnectionException je){
		 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
   		        _log.errorTrace(methodName, je);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberRoutingControlCache[getRoutingControlObjectFromRedis]", "", "", "", "JedisConnectionException :" + je.getMessage());
		 }catch(NoSuchElementException  ex){
		 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
   		        _log.errorTrace(methodName, ex);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberRoutingControlCache[getRoutingControlObjectFromRedis]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		 }catch (Exception e) {
		 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
		 	_log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberRoutingControlCache[getRoutingControlObjectFromRedis]", "", "", "", "Exception :" + e.getMessage());
		 }
		 finally {
	        	if (jedis != null) {
	        	jedis.close();
	        	}
	        }
		 RedisActivityLog.log("SubscriberRoutingControlCache->getRoutingControlObjectFromRedis->End");
      return subscriberRoutingControlVO;
    }

}
