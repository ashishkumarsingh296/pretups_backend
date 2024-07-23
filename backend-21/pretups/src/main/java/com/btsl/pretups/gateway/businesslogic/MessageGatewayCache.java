/**
 * @(#)MessageGatewayCache.java
 *                              Copyright(c) 2005, Bharti Telesoft Ltd.
 *                              All Rights Reserved
 * 
 *                              <description>
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Author Date History
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              avinash.kamthan Jul 11, 2005 Initital Creation
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 * 
 */

package com.btsl.pretups.gateway.businesslogic;

import java.util.HashMap;
import java.util.Iterator;
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
 */
public class MessageGatewayCache implements Runnable{

	public void run() {
        try {
            Thread.sleep(50);
            loadMessageGatewayAtStartup();
        } catch (Exception e) {
        	 _log.error("MessageGatewayCache init() Exception ", e);
        }
    }

    private static Log _log = LogFactory.getLog(MessageGatewayCache.class.getName());

    private static HashMap<String,MessageGatewayVO> _messageGatewayMap = new HashMap<String,MessageGatewayVO>();

    private static HashMap<String,MessageGatewayMappingCacheVO> _messageGatewayMappingMap = new HashMap<String,MessageGatewayMappingCacheVO>(); 
    private static String redisEnable = BTSLUtil.NullToString(Constants.getProperty("REDIS_ENABLE"));
    private static final String hKeyMessageGatewayMap = "MessageGatewayMap";
    private static final String hKeyMessageGatewayMappingMap = "MessageGatewayMappingMap";
    private static Gson gson = new GsonBuilder()
    		.setDateFormat("yyyy-MM-dd HH:mm:ss:mss")
    		.create();
    private static final int keyTimer = Integer.parseInt(Constants.getProperty("KEYTIMER"));
    
    private static LoadingCache<String, MessageGatewayVO> messageGatewayMapCache = CacheBuilder.newBuilder()
	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)
	    .build(new CacheLoader<String,MessageGatewayVO>(){
			@Override
			public MessageGatewayVO load(String key) throws Exception {
				return getMessageGatewayVOFormRedis(key);
			}
	     });
  
    private static LoadingCache<String, MessageGatewayMappingCacheVO> messageGatewayMappingMapCache = CacheBuilder.newBuilder()
    	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)
    	    .build(new CacheLoader<String,MessageGatewayMappingCacheVO>(){
    			@Override
    			public MessageGatewayMappingCacheVO load(String key) throws Exception {
    				return getMessageGatewayMappingVOFormRedis(key);
    			}
    	     });
    
    
    public static void loadMessageGatewayAtStartup() {
        String methodName = "loadMessageGatewayAtStartup";
    	if (_log.isDebugEnabled()) {
            _log.debug(methodName, "entered");
        }
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        	RedisActivityLog.log("MessageGatewayCache->loadMessageGatewayAtStartup->Start");
        	Jedis jedis =null;
   		 	try {
   			   jedis = RedisConnectionPool.getPoolInstance().getResource();
				//If key is already present in redis db do not reload
				if(!jedis.exists(hKeyMessageGatewayMap)) {
					 Pipeline pipeline = jedis.pipelined();
					 HashMap<String,MessageGatewayVO> _messageGatewayMap1 = loadMessageGateway();
					 for (Entry<String, MessageGatewayVO> entry : _messageGatewayMap1.entrySet())  {
				       pipeline.hset(hKeyMessageGatewayMap, entry.getKey(), gson.toJson(entry.getValue()));
					 } 
					 pipeline.sync();
			      }
				//If key is already present in redis db do not reload
				if(!jedis.exists(hKeyMessageGatewayMappingMap)) {
					 Pipeline pipeline = jedis.pipelined();
					HashMap<String, MessageGatewayMappingCacheVO> _messageGatewayMappingMap1 = loadMessageGatewayMapping();
					 for (Entry<String, MessageGatewayMappingCacheVO> entry : _messageGatewayMappingMap1.entrySet())  {
				       pipeline.hset(hKeyMessageGatewayMappingMap, entry.getKey(), gson.toJson(entry.getValue()));
					  } 
					 pipeline.sync();
			     }	
				RedisActivityLog.log("MessageGatewayCache->loadMessageGatewayAtStartup->End");
			}catch(JedisConnectionException je){
 			 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
 	   		        _log.errorTrace(methodName, je);
 	   	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MessageGatewayCache[loadMessageGatewayAtStartup]", "", "", "", "JedisConnectionException :" + je.getMessage());
 			 }catch(NoSuchElementException  ex){
 			 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
 	   		        _log.errorTrace(methodName, ex);
 	   	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MessageGatewayCache[loadMessageGatewayAtStartup]", "", "", "", "NoSuchElementException :" + ex.getMessage());
 			 }catch (Exception e) {
 			 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
 			 	_log.errorTrace(methodName, e);
 	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MessageGatewayCache[loadMessageGatewayAtStartup]", "", "", "", "Exception :" + e.getMessage());
 			 }finally {
   	        	if (jedis != null) {
   	        	jedis.close();
   	        	}
 			 }
   		 } else {
   		    _messageGatewayMap = loadMessageGateway();
   	        _messageGatewayMappingMap = loadMessageGatewayMapping();	
		}
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "exited");
        }
    }

    /**
     * To load the gateway details
     * 
     * @return HashMap
     */
    private static HashMap<String,MessageGatewayVO> loadMessageGateway() {
        if (_log.isDebugEnabled()) {
            _log.debug("loadMessageGateway()", "entered");
        }
        final String METHOD_NAME = "loadMessageGateway";
        MessageGatewayDAO gatewayDAO = new MessageGatewayDAO();
        HashMap<String,MessageGatewayVO>  map = null;
        try {
            map = gatewayDAO.loadMessageGatewayCache();
        } catch (Exception e) {
            _log.error("loadMessageGateway()", "Exception: " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("loadMessageGateway()", "exited");
        }

        return map;
    }

    /**
     * To load the mapping details
     * 
     * @return HashMap
     */
    private static HashMap<String,MessageGatewayMappingCacheVO> loadMessageGatewayMapping() {
        if (_log.isDebugEnabled()) {
            _log.debug("loadMessageGatewayMapping()", "entered");
        }
        final String METHOD_NAME = "loadMessageGatewayMapping";
        MessageGatewayDAO gatewayDAO = new MessageGatewayDAO();
        HashMap<String,MessageGatewayMappingCacheVO> map = null;
        try {
            map = gatewayDAO.loadMessageGatewayMappingCache();
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("loadMessageGatewayMapping()", "Exception e:" + e.getMessage());
        }
        if (_log.isDebugEnabled()) {
            _log.debug("loadMessageGatewayMapping()", "exited");
        }

        return map;
    }

    /**
     * to update the cache
     */
    public static void updateMessageGateway() {
    	String methodName="updateMessageGateway";
        if (_log.isDebugEnabled()) {
            _log.debug("updateMessageGateway()", " Entered");
        }
        HashMap<String,MessageGatewayVO> currentMap = loadMessageGateway();
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        	RedisActivityLog.log("MessageGatewayCache->updateMessageGateway->Start");
        	Jedis jedis = null;
      		 try {
      		    jedis = RedisConnectionPool.getPoolInstance().getResource();
   	            Pipeline pipeline = jedis.pipelined();
   	            pipeline.del(hKeyMessageGatewayMap);
	   	        /*HashMap<String, Object> cachedMap = RedisUtil.deserialize(jedis.hgetAll(hKeyMessageGatewayMap));
	   	        if ((cachedMap != null) && (cachedMap.size() > 0)) {
	   	            compareMaps(cachedMap, currentMap);
	   	        }*/
	   			for (Entry<String, MessageGatewayVO> entry : currentMap.entrySet())  {
	   		      pipeline.hset(hKeyMessageGatewayMap, entry.getKey(), gson.toJson(entry.getValue()));
	   			}
	   			pipeline.sync();
	   			RedisActivityLog.log("MessageGatewayCache->updateMessageGateway->End");
      		 }catch(JedisConnectionException je){
		 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
   		        _log.errorTrace(methodName, je);
   	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MessageGatewayCache[updateMessageGateway]", "", "", "", "JedisConnectionException :" + je.getMessage());
			 }catch(NoSuchElementException  ex){
		 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
   		        _log.errorTrace(methodName, ex);
   	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MessageGatewayCache[updateMessageGateway]", "", "", "", "NoSuchElementException :" + ex.getMessage());
			 }catch (Exception e) {
			 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
			 	_log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MessageGatewayCache[updateMessageGateway]", "", "", "", "Exception :" + e.getMessage());
			 }finally {
	        	if (jedis != null) {
	        		jedis.close();
	        	}
			 }
   	   } else {
	        if (_messageGatewayMap != null && _messageGatewayMap.size() > 0) {
	            compareMaps(_messageGatewayMap, currentMap);
	        }
	        _messageGatewayMap = currentMap;
   	   }
        if (_log.isDebugEnabled()) {
            _log.debug("updateMessageGateway()", "exited " + _messageGatewayMap.size());
        }
    }

    /**
     * to update the cache
     */
    public static void updateMessageGatewayMapping() {
    	String methodName = "updateMessageGatewayMapping";
        if (_log.isDebugEnabled()) {
            _log.debug("updateMessageGatewayMapping()", " Entered");
        }
        HashMap<String,MessageGatewayMappingCacheVO> currentMap = loadMessageGatewayMapping();
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        	  RedisActivityLog.log("MessageGatewayCache->updateMessageGatewayMapping->Start");
        	  Jedis jedis = null;
        	  try {
        		jedis = RedisConnectionPool.getPoolInstance().getResource();
        		Pipeline pipeline = jedis.pipelined();
	  	        pipeline.del(hKeyMessageGatewayMappingMap);
	  	        /*HashMap<String, Object> cachedMap = RedisUtil.deserialize(jedis.hgetAll(hKeyBytes));
	  	        if ((cachedMap != null) && (cachedMap.size() > 0)) {
	  	        	compareMappingMaps(cachedMap, currentMap);
	  	        }*/
	  			for (Entry<String, MessageGatewayMappingCacheVO> entry : currentMap.entrySet())  {
	  				pipeline.hset(hKeyMessageGatewayMappingMap, entry.getKey(), gson.toJson(entry.getValue()));
	  			}
	  			pipeline.sync();
	     		RedisActivityLog.log("MessageGatewayCache->updateMessageGatewayMapping->End");	 
        	  }catch(JedisConnectionException je){
		 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
   		        _log.errorTrace(methodName, je);
   	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MessageGatewayCache[updateMessageGateway]", "", "", "", "JedisConnectionException :" + je.getMessage());
			 }catch(NoSuchElementException  ex){
		 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
   		        _log.errorTrace(methodName, ex);
   	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MessageGatewayCache[updateMessageGateway]", "", "", "", "NoSuchElementException :" + ex.getMessage());
			 }catch (Exception e) {
			 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
			 	_log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MessageGatewayCache[updateMessageGateway]", "", "", "", "Exception :" + e.getMessage());
			 }finally {
	        	if (jedis != null) {
	        		jedis.close();
	        	}
			 }
        } else {
		    if (_messageGatewayMappingMap != null && _messageGatewayMappingMap.size() > 0) {
		        compareMappingMaps(_messageGatewayMappingMap, currentMap);
		    }
		
		    _messageGatewayMappingMap = currentMap;
       }
        if (_log.isDebugEnabled()) {
            _log.debug("updateMessageGatewayMapping()", "exited " + _messageGatewayMappingMap.size());
        }
    }

    /**
     * get the massagegateway vo from cache
     * 
     * @param p_messageGatewayCode
     * @return MessageGatewayVO
     */
    public static MessageGatewayVO getObject(String p_messageGatewayCode) {
    	String methodName = "getObject";
        if (_log.isDebugEnabled()) {
            _log.debug("getObject()", "entered " + p_messageGatewayCode);
        }
        MessageGatewayVO messageGatewayVO = null;
        try{
        	if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        	messageGatewayVO = messageGatewayMapCache.get(p_messageGatewayCode);
        } else {
	        messageGatewayVO = (MessageGatewayVO) _messageGatewayMap.get(p_messageGatewayCode);
		}
        }catch (ExecutionException e) {
        	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
        	_log.errorTrace(methodName, e);
   	        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MessageGatewayCache[getObject]", "", "", "", "ExecutionException :" + e.getMessage());
			}
    	catch (InvalidCacheLoadException e) { 
    		_log.info(methodName, e);
		}
        if (_log.isDebugEnabled()) {
            _log.debug("getObject()", "exited " + messageGatewayVO);
        }
        return messageGatewayVO;
    }

    /**
     * @param p_requestCode
     * @return MessageGatewayMappingCacheVO
     */
    public static MessageGatewayMappingCacheVO getMappingObject(String p_requestCode) {
    	String methodName = "getMappingObject";
    	if (_log.isDebugEnabled()) {
            _log.debug("getMappingObject()", "entered " + p_requestCode);
        }
        MessageGatewayMappingCacheVO messageMappingCacheVO = null;
        try{
	        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
	        	messageMappingCacheVO = messageGatewayMappingMapCache.get(p_requestCode);
	        } else {
	        	messageMappingCacheVO = (MessageGatewayMappingCacheVO) _messageGatewayMappingMap.get(p_requestCode);
			  }
        }catch (ExecutionException e) {
        	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
        	_log.errorTrace(methodName, e);
   	        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MessageGatewayCache[getMappingObject]", "", "", "", "ExecutionException :" + e.getMessage());
			}
    	catch (InvalidCacheLoadException e) { 
    		_log.info(methodName, e);
		}
        if (_log.isDebugEnabled()) {
            _log.debug("getMappingObject()", "exited " + messageMappingCacheVO.logInfo());
        }

        return messageMappingCacheVO;
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
        final String METHOD_NAME = "compareMaps";
        try {
            if (_log.isDebugEnabled()) {
                _log.debug("compareMaps()", "Entered PreviousMap " + p_previousMap + "  Current Map" + p_currentMap);
            }

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

            // to check whether any new Message Cache added or not but size of
            boolean isNewAdded = false;
            while (iterator != null && iterator.hasNext()) {
                String key = (String) iterator.next();
                MessageGatewayVO prevMessageGatewayVO = (MessageGatewayVO) p_previousMap.get(key);
                MessageGatewayVO curMessageGatewayVO = (MessageGatewayVO) p_currentMap.get(key);

                if (prevMessageGatewayVO != null && curMessageGatewayVO == null) {
                    isNewAdded = true;
                    CacheOperationLog.log("MessageGatewayCache", BTSLUtil.formatMessage("Delete", prevMessageGatewayVO.getGatewayCode(), prevMessageGatewayVO.logInfo()));
                    if (prevMessageGatewayVO.getRequestGatewayVO() != null) {
                        CacheOperationLog.log("MessageGatewayCache", BTSLUtil.formatMessage("Delete Request Info ", prevMessageGatewayVO.getRequestGatewayVO().getGatewayCode(), prevMessageGatewayVO.getRequestGatewayVO().logInfo()));
                    }
                    if (prevMessageGatewayVO.getResponseGatewayVO() != null) {
                        CacheOperationLog.log("MessageGatewayCache", BTSLUtil.formatMessage("Delete Response Info ", prevMessageGatewayVO.getResponseGatewayVO().getGatewayCode(), prevMessageGatewayVO.getResponseGatewayVO().logInfo()));
                    }

                } else if (prevMessageGatewayVO == null && curMessageGatewayVO != null) {
                    CacheOperationLog.log("MessageGatewayCache", BTSLUtil.formatMessage("Add", curMessageGatewayVO.getGatewayCode(), curMessageGatewayVO.logInfo()));

                    if (curMessageGatewayVO.getRequestGatewayVO() != null) {
                        CacheOperationLog.log("MessageGatewayCache", BTSLUtil.formatMessage("Add Request Info ", curMessageGatewayVO.getRequestGatewayVO().getGatewayCode(), curMessageGatewayVO.getRequestGatewayVO().logInfo()));
                    }
                    if (curMessageGatewayVO.getResponseGatewayVO() != null) {
                        CacheOperationLog.log("MessageGatewayCache", BTSLUtil.formatMessage("Add Response Info ", curMessageGatewayVO.getResponseGatewayVO().getGatewayCode(), curMessageGatewayVO.getResponseGatewayVO().logInfo()));
                    }
                } else if (prevMessageGatewayVO != null && curMessageGatewayVO != null) {
                    if (!curMessageGatewayVO.equalsMessageGatewayVO(prevMessageGatewayVO)) {
                        CacheOperationLog.log("MessageGatewayCache", BTSLUtil.formatMessage("Modify", curMessageGatewayVO.getGatewayCode(), curMessageGatewayVO.differences(prevMessageGatewayVO)));

                        // log the request vo modification
                        if (curMessageGatewayVO.getRequestGatewayVO() != null && prevMessageGatewayVO.getRequestGatewayVO() == null) {
                            CacheOperationLog.log("MessageGatewayCache", BTSLUtil.formatMessage("Add Request Info ", curMessageGatewayVO.getRequestGatewayVO().getGatewayCode(), curMessageGatewayVO.getRequestGatewayVO().logInfo()));
                        } else if (curMessageGatewayVO.getRequestGatewayVO() == null && prevMessageGatewayVO.getRequestGatewayVO() != null) {
                            CacheOperationLog.log("MessageGatewayCache", BTSLUtil.formatMessage("Delete Request Info ", prevMessageGatewayVO.getRequestGatewayVO().getGatewayCode(), prevMessageGatewayVO.getRequestGatewayVO().logInfo()));
                        } else if (curMessageGatewayVO.getRequestGatewayVO() != null && prevMessageGatewayVO.getRequestGatewayVO() != null && !curMessageGatewayVO.getRequestGatewayVO().equals(prevMessageGatewayVO.getRequestGatewayVO())) {
                            CacheOperationLog.log("MessageGatewayCache", BTSLUtil.formatMessage("Modify Request Info ", curMessageGatewayVO.getRequestGatewayVO().getGatewayCode(), curMessageGatewayVO.getRequestGatewayVO().differences(prevMessageGatewayVO.getRequestGatewayVO())));
                        }

                        // log the response vo modification
                        if (curMessageGatewayVO.getResponseGatewayVO() != null && prevMessageGatewayVO.getResponseGatewayVO() == null) {
                            CacheOperationLog.log("MessageGatewayCache", BTSLUtil.formatMessage("Add Response Info ", curMessageGatewayVO.getResponseGatewayVO().getGatewayCode(), curMessageGatewayVO.getResponseGatewayVO().logInfo()));
                        } else if (curMessageGatewayVO.getResponseGatewayVO() == null && prevMessageGatewayVO.getResponseGatewayVO() != null) {
                            CacheOperationLog.log("MessageGatewayCache", BTSLUtil.formatMessage("Delete Response Info ", prevMessageGatewayVO.getResponseGatewayVO().getGatewayCode(), prevMessageGatewayVO.getResponseGatewayVO().logInfo()));
                        } else if (curMessageGatewayVO.getResponseGatewayVO() != null && prevMessageGatewayVO.getResponseGatewayVO() != null && !curMessageGatewayVO.getResponseGatewayVO().equalsResponseGatewayVO(prevMessageGatewayVO.getResponseGatewayVO())) {
                            CacheOperationLog.log("MessageGatewayCache", BTSLUtil.formatMessage("Modify Response Info ", curMessageGatewayVO.getResponseGatewayVO().getGatewayCode(), curMessageGatewayVO.getResponseGatewayVO().differences(prevMessageGatewayVO.getResponseGatewayVO())));
                        }
                    }
                }
            }

            /**
             * Note: this case arises when same number of messagegateway added
             * and
             * deleted as well
             */
            if (p_previousMap.size() == p_currentMap.size() && isNewAdded) {
                HashMap tempMap = new HashMap(p_currentMap);
                while (copiedIterator.hasNext()) {
                    tempMap.remove((String) copiedIterator.next());
                }

                Iterator iterator2 = tempMap.keySet().iterator();
                while (iterator2.hasNext()) {
                    // new network added
                    MessageGatewayVO messageGatewayVO = (MessageGatewayVO) p_currentMap.get(iterator2.next());
                    CacheOperationLog.log("MessageGatewayCache", BTSLUtil.formatMessage("Add", messageGatewayVO.getNetworkCode(), messageGatewayVO.logInfo()));

                    if (messageGatewayVO.getRequestGatewayVO() != null) {
                        CacheOperationLog.log("MessageGatewayCache", BTSLUtil.formatMessage("Add Request Info ", messageGatewayVO.getRequestGatewayVO().getGatewayCode(), messageGatewayVO.getRequestGatewayVO().logInfo()));
                    }
                    if (messageGatewayVO.getResponseGatewayVO() != null) {
                        CacheOperationLog.log("MessageGatewayCache", BTSLUtil.formatMessage("Add Response Info ", messageGatewayVO.getResponseGatewayVO().getGatewayCode(), messageGatewayVO.getResponseGatewayVO().logInfo()));
                    }
                }
            }

            if (_log.isDebugEnabled()) {
                _log.debug("compareMaps()", "Exited");
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }

    }

    private static void compareMappingMaps(HashMap p_previousMap, HashMap p_currentMap) {
        final String METHOD_NAME = "compareMappingMaps";
        try {
            if (_log.isDebugEnabled()) {
                _log.debug("compareMappingMaps()", "Entered PreviousMap " + p_previousMap + "  Current Map" + p_currentMap);
            }
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
                MessageGatewayMappingCacheVO prevMappingVO = (MessageGatewayMappingCacheVO) p_previousMap.get(key);
                MessageGatewayMappingCacheVO curMappingVO = (MessageGatewayMappingCacheVO) p_currentMap.get(key);

                if (prevMappingVO != null && curMappingVO == null) {
                    // network status has been changed
                    // less no of rows in current than previous
                    isNewAdded = true;
                    CacheOperationLog.log("MessageGatewaymappingCache", BTSLUtil.formatMessage("Delete", prevMappingVO.getRequestCode(), prevMappingVO.logInfo()));
                } else if (prevMappingVO == null && curMappingVO != null) {
                    // new network added
                    CacheOperationLog.log("MessageGatewaymappingCache", BTSLUtil.formatMessage("Add", curMappingVO.getRequestCode(), curMappingVO.logInfo()));
                } else if (prevMappingVO != null && curMappingVO != null) {
                    if (!curMappingVO.equalsMsgGatewayMappingCacheVO(prevMappingVO)) {
                        CacheOperationLog.log("MessageGatewaymappingCache", BTSLUtil.formatMessage("Modify", curMappingVO.getRequestCode(), curMappingVO.differences(prevMappingVO)));

                    }
                }
            }

            /**
             * Note: this case arises when same number of network added and
             * deleted as well
             */
            if (p_previousMap.size() == p_currentMap.size() && isNewAdded) {
                HashMap tempMap = new HashMap(p_currentMap);
                while (copiedIterator.hasNext()) {
                    tempMap.remove((String) copiedIterator.next());
                }

                Iterator iterator2 = tempMap.keySet().iterator();
                while (iterator2.hasNext()) {
                    // new network added
                    MessageGatewayMappingCacheVO mappingVO = (MessageGatewayMappingCacheVO) p_currentMap.get(iterator2.next());
                    CacheOperationLog.log("MessageGatewaymappingCache", BTSLUtil.formatMessage("Add", mappingVO.getRequestCode(), mappingVO.logInfo()));
                }
            }
            if (_log.isDebugEnabled()) {
                _log.debug("compareMappingMaps()", "Exited");
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
    }

    public static HashMap getMessageGatewayMap() {
    	String methodName = "getMessageGatewayMap";
    	if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
    		RedisActivityLog.log("MessageGatewayCache->getMessageGatewayMap->Start"); 
    		Jedis jedis = null;
    		try {
    		   jedis = RedisConnectionPool.getPoolInstance().getResource();
    		   Map<String,String> jsonMap = jedis.hgetAll(hKeyMessageGatewayMap);
    		   for (Entry<String, String> entry:jsonMap.entrySet()) {
    			  _messageGatewayMap.put(entry.getKey(), gson.fromJson(entry.getValue(), MessageGatewayVO.class));
    		  	}
	   		   RedisActivityLog.log("MessageGatewayCache->getMessageGatewayMap->End"); 
	        }catch(JedisConnectionException je){
		 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
		        _log.errorTrace(methodName, je);
	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MessageGatewayCache[getMessageGatewayMap]", "", "", "", "JedisConnectionException :" + je.getMessage());
			 }catch(NoSuchElementException  ex){
		 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
		        _log.errorTrace(methodName, ex);
	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MessageGatewayCache[getMessageGatewayMap]", "", "", "", "NoSuchElementException :" + ex.getMessage());
			 }catch (Exception e) {
			 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
			 	_log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MessageGatewayCache[getMessageGatewayMap]", "", "", "", "Exception :" + e.getMessage());
			 }finally{
			 if(jedis!=null)
				 jedis.close();
			}
    	}
        return _messageGatewayMap;
    }

    
    /**
     * @param key
     * @return
     * @throws BTSLBaseException
     */
    public static MessageGatewayVO getMessageGatewayVOFormRedis(String key) throws BTSLBaseException {
        String methodName = "getMessageGatewayVOFormRedis";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:key=" + key);
        }
        Jedis jedis = null;
    	MessageGatewayVO messageGatewayVO = null;
		 try {
		     RedisActivityLog.log("MessageGatewayCache->getMessageGatewayVOFormRedis->Start");
			 jedis = RedisConnectionPool.getPoolInstance().getResource();
			 String prefObj = jedis.hget(hKeyMessageGatewayMap, key);
			 if(!BTSLUtil.isNullString(prefObj))
				messageGatewayVO = gson.fromJson(prefObj,MessageGatewayVO.class);
			 RedisActivityLog.log("MessageGatewayCache->getMessageGatewayVOFormRedis->End");
		 }catch(JedisConnectionException je){
	 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	        _log.errorTrace(methodName, je);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MessageGatewayCache[getMessageGatewayVOFormRedis]", "", "", "", "JedisConnectionException :" + je.getMessage());
	        throw new BTSLBaseException(MessageGatewayCache.class.getName(), methodName,je.getMessage());
		 }catch(NoSuchElementException  ex){
	 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	        _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MessageGatewayCache[getMessageGatewayVOFormRedis]", "", "", "", "NoSuchElementException :" + ex.getMessage());
	        throw new BTSLBaseException(MessageGatewayCache.class.getName(), methodName,ex.getMessage());
		 }catch (Exception e) {
		 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
		 	_log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MessageGatewayCache[getMessageGatewayVOFormRedis]", "", "", "", "Exception :" + e.getMessage());
			throw new BTSLBaseException(MessageGatewayCache.class.getName(), methodName,e.getMessage());
		 }
		 finally {
	        	if (jedis != null) {
	        	jedis.close();
	        	}
	        }
        return messageGatewayVO;
    }

    /**
     * @param key
     * @return
     * @throws BTSLBaseException
     */
    public static MessageGatewayMappingCacheVO getMessageGatewayMappingVOFormRedis(String key) throws BTSLBaseException {
        String methodName = "getMessageGatewayMappingVOFormRedis";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:key=" + key);
        }
        Jedis jedis = null;
        MessageGatewayMappingCacheVO messageGatewayVO = null;
		 try {
		     RedisActivityLog.log("MessageGatewayCache->getMessageGatewayMappingVOFormRedis->Start");
			 jedis = RedisConnectionPool.getPoolInstance().getResource();
			 String prefObj = jedis.hget(hKeyMessageGatewayMappingMap, key);
			 if(!BTSLUtil.isNullString(prefObj))
				messageGatewayVO = gson.fromJson(prefObj,MessageGatewayMappingCacheVO.class);
			 RedisActivityLog.log("MessageGatewayCache->getMessageGatewayMappingVOFormRedis->End");
		 }catch(JedisConnectionException je){
	 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	        _log.errorTrace(methodName, je);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MessageGatewayCache[getMessageGatewayMappingVOFormRedis]", "", "", "", "JedisConnectionException :" + je.getMessage());
	        throw new BTSLBaseException(MessageGatewayCache.class.getName(), methodName,je.getMessage());
		 }catch(NoSuchElementException  ex){
	 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	        _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MessageGatewayCache[getMessageGatewayMappingVOFormRedis]", "", "", "", "NoSuchElementException :" + ex.getMessage());
	        throw new BTSLBaseException(MessageGatewayCache.class.getName(), methodName,ex.getMessage());
		 }catch (Exception e) {
		 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
		 	_log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MessageGatewayCache[getMessageGatewayMappingVOFormRedis]", "", "", "", "Exception :" + e.getMessage());
			throw new BTSLBaseException(MessageGatewayCache.class.getName(), methodName,e.getMessage());
		 }
		 finally {
	        	if (jedis != null) {
	        	jedis.close();
	        	}
	        }
        return messageGatewayVO;
    }

}
