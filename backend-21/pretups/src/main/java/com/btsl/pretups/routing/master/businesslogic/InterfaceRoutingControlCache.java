package com.btsl.pretups.routing.master.businesslogic;

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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
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

/**
 * 
 */
public class InterfaceRoutingControlCache implements Runnable  {

    /**
     * Field _routingMap.
     */
    private static HashMap<String, ArrayList<ListValueVO>> _routingMap = new HashMap<String, ArrayList<ListValueVO>>();

    private static final String hKeyInterfaceRoutingControlCacheMap = "InterfaceRoutingControlCache";
    private static String redisEnable = BTSLUtil.NullToString(Constants.getProperty("REDIS_ENABLE"));
    private static Gson gson = new GsonBuilder()
    		.setDateFormat("yyyy-MM-dd HH:mm:ss:mss")
    		.create();
    private static final int keyTimer = Integer.parseInt(Constants.getProperty("KEYTIMER"));
    private static LoadingCache<String, ArrayList<ListValueVO>> routingMemo = CacheBuilder.newBuilder()
    	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)
    	    .build(new CacheLoader<String, ArrayList<ListValueVO>>(){
    			@Override
    			public ArrayList<ListValueVO> load(String key) throws Exception {
    				return getRoutingControlDetailsListFromRedis(key);
    			}
    	     });
    public void run() {
        try {
            Thread.sleep(50);
            loadInterfaceRoutingControlStartUp();
        } catch (Exception e) {
        	 _log.error("InterfaceRoutingControlCache init() Exception ", e);
        }
    }
    /**
     * Field _log.
     */
    private static Log _log = LogFactory.getLog(InterfaceRoutingControlCache.class.getName());
    /**
     * Field _routingControlDAO.
     */
    private static RoutingControlDAO _routingControlDAO = new RoutingControlDAO();

    /**
     * Method loadInterfaceRoutingControlStartUp.
     * @throws SQLException 
     * @throws Exception 
     */
    public static void loadInterfaceRoutingControlStartUp() throws BTSLBaseException, SQLException {
    	final String methodName = "loadInterfaceRoutingControlStartUp";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        try {
            HashMap<String,ArrayList<ListValueVO>> tempMap = null;
            if (_log.isDebugEnabled()) {
                _log.debug("loadInterfaceRoutingControlStartUp", " Before loading:" + _routingMap);
            }
            if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
            	RedisActivityLog.log("InterfaceRoutingControlCache->loadInterfaceRoutingControlStartUp->Start");
            	Jedis jedis  = null;
       		    try{
       			 jedis  = RedisConnectionPool.getPoolInstance().getResource();
   					if(!jedis.exists(hKeyInterfaceRoutingControlCacheMap)) {
   					  Pipeline pipeline = jedis.pipelined();
   					  tempMap = _routingControlDAO.loadInterfaceRoutingControlDetails();
   					 for (Entry<String, ArrayList<ListValueVO>> entry : tempMap.entrySet())  {
   						pipeline.hset(hKeyInterfaceRoutingControlCacheMap, entry.getKey(), gson.toJson(entry.getValue()));
   					 } 
   					pipeline.sync();
   				 }
   				
   	   		 }catch(JedisConnectionException je){
				  _log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
				  _log.errorTrace(methodName, je);
	 	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserServicesCache[updateServicesMap]", "", "", "", "JedisConnectionException :" + je.getMessage());
	 	   	        throw new BTSLBaseException(InterfaceRoutingControlCache.class.getName(), methodName,je.getMessage());
			  }catch(NoSuchElementException  ex){
	 				_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	 				_log.errorTrace(methodName, ex);
	 	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserServicesCache[updateServicesMap]", "", "", "", "NoSuchElementException :" + ex.getMessage());
	 	   	        throw new BTSLBaseException(InterfaceRoutingControlCache.class.getName(), methodName,ex.getMessage());
			  }catch (Exception e) {
	 				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
	 				_log.errorTrace(methodName, e);
	 	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserServicesCache[updateServicesMap]", "", "", "", "Exception :" + e.getMessage());
	 	           throw new BTSLBaseException(InterfaceRoutingControlCache.class.getName(), methodName,e.getMessage());
			  }
	 			 finally {
	 		        	if (jedis != null) {
	 		        	jedis.close();
	 		        }
   	   	RedisActivityLog.log("InterfaceRoutingControlCache->loadInterfaceRoutingControlStartUp->Stop");
      	  }
       } else {	 
      		  tempMap = _routingControlDAO.loadInterfaceRoutingControlDetails();
            _routingMap = tempMap;
      	}
            if (_log.isDebugEnabled()) {
                _log.debug("loadInterfaceRoutingControlStartUp", " After loading:" + _routingMap.size());
            }
        }
        catch (BTSLBaseException | SQLException e)
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
     * @throws SQLException 
     * @throws Exception 
     */
    public static void refreshInterfaceRoutingControl() throws BTSLBaseException, SQLException {
    	final String methodName = "refreshInterfaceRoutingControl";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        try {
            HashMap<String,ArrayList<ListValueVO>> tempMap = null;
            if (_log.isDebugEnabled()) {
                _log.debug("refreshInterfaceRoutingControl", " Before loading:" + _routingMap);
            }
            tempMap = _routingControlDAO.loadInterfaceRoutingControlDetails();
            
            if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
            	RedisActivityLog.log("InterfaceRoutingControlCache->refreshInterfaceRoutingControl->Start");
            	Jedis jedis = null;
          		 try {
          			jedis =   RedisConnectionPool.getPoolInstance().getResource();
          			Pipeline pipeline = jedis.pipelined();
   					//   HashMap<String, Object> cachedMap = RedisUtil.deserialize(jedis.hgetAll(hKeyBytes));
          			pipeline.del(hKeyInterfaceRoutingControlCacheMap);
   				       /* if ((cachedMap != null) && (cachedMap.size() > 0)) {
   				            compareMaps(cachedMap, tempMap);
   				        }*/
   						for (Entry<String, ArrayList<ListValueVO>> entry : tempMap.entrySet())  {
   					       pipeline.hset(hKeyInterfaceRoutingControlCacheMap,entry.getKey(),gson.toJson(entry.getValue()));
   					 }
   					pipeline.sync();	
   	   		 }catch(JedisConnectionException je){
				  _log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
				  _log.errorTrace(methodName, je);
	 	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserServicesCache[updateServicesMap]", "", "", "", "JedisConnectionException :" + je.getMessage());
	 	   	        throw new BTSLBaseException(InterfaceRoutingControlCache.class.getName(), methodName,je.getMessage());
			  }catch(NoSuchElementException  ex){
	 				_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	 				_log.errorTrace(methodName, ex);
	 	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserServicesCache[updateServicesMap]", "", "", "", "NoSuchElementException :" + ex.getMessage());
	 	   	        throw new BTSLBaseException(InterfaceRoutingControlCache.class.getName(), methodName,ex.getMessage());
			  }catch (Exception e) {
	 				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
	 				_log.errorTrace(methodName, e);
	 	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserServicesCache[updateServicesMap]", "", "", "", "Exception :" + e.getMessage());
	 	           throw new BTSLBaseException(InterfaceRoutingControlCache.class.getName(), methodName,e.getMessage());
			  }
	 			 finally {
	 		        	if (jedis != null) {
	 		        	jedis.close();
	 		        }
   	   	RedisActivityLog.log("InterfaceRoutingControlCache->refreshInterfaceRoutingControl->Stop");
      	  } 
        } else {	 
      		 compareMaps(_routingMap, tempMap);
            _routingMap = tempMap;
      	}
            if (_log.isDebugEnabled()) {
                _log.debug("refreshInterfaceRoutingControl", " After loading:" + _routingMap.size());
            }
        }
        catch (BTSLBaseException | SQLException e)
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
                ListValueVO prevVOAlt1 = null;
                ListValueVO prevVOAlt2 = null;
                ArrayList routingControlVOListPrev = (ArrayList) p_previousMap.get(key);
                if (routingControlVOListPrev != null && !routingControlVOListPrev.isEmpty()) {
                    prevVOAlt1 = (ListValueVO) routingControlVOListPrev.get(0);
                    if (routingControlVOListPrev.size() > 1) {
                        prevVOAlt2 = (ListValueVO) routingControlVOListPrev.get(1);
                    }
                }

                ListValueVO curVOAlt1 = null;
                ListValueVO curVOAlt2 = null;
                ArrayList routingControlVOListCur = (ArrayList) p_currentMap.get(key);
                if (routingControlVOListCur != null && !routingControlVOListCur.isEmpty()) {
                    curVOAlt1 = (ListValueVO) routingControlVOListCur.get(0);
                    if (routingControlVOListCur.size() > 1) {
                        curVOAlt2 = (ListValueVO) routingControlVOListCur.get(1);
                    }
                }

                if (prevVOAlt1 != null && curVOAlt1 == null) {
                    isNewAdded = true;
                    _log.info("compareMaps()", BTSLUtil.formatMessage("Delete", prevVOAlt1.getCodeName(), ""));
                } else if (prevVOAlt1 == null && curVOAlt1 != null) {
                    _log.info("compareMaps()", BTSLUtil.formatMessage("Add", curVOAlt1.getCodeName(), ""));
                } else if (prevVOAlt1 != null && curVOAlt1 != null) {
                    if (!curVOAlt1.equalsListValueVO(prevVOAlt1)) {
                        _log.info("compareMaps()", BTSLUtil.formatMessage("Modify", curVOAlt1.getCodeName(), ""));
                    }
                }
                if (prevVOAlt2 != null && curVOAlt2 == null) {
                    isNewAdded = true;
                    _log.info("compareMaps()", BTSLUtil.formatMessage("Delete", prevVOAlt2.getCodeName(), ""));
                } else if (prevVOAlt2 == null && curVOAlt2 != null) {
                    _log.info("compareMaps()", BTSLUtil.formatMessage("Add", curVOAlt2.getCodeName(), ""));
                } else if (prevVOAlt2 != null && curVOAlt2 != null) {
                    if (!curVOAlt2.equalsListValueVO(prevVOAlt2)) {
                        _log.info("compareMaps()", BTSLUtil.formatMessage("Modify", curVOAlt2.getCodeName(), ""));
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
    public static ArrayList<ListValueVO> getRoutingControlDetails(String p_key) {
        if (_log.isDebugEnabled()) {
            _log.debug("getRoutingControlDetails()", "Entered p_key: " + p_key);
        }
        ArrayList<ListValueVO> list = null;
        String methodName = "getRoutingControlDetails";
         if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        	try {
        		list =  routingMemo.get(p_key);
			}catch (ExecutionException e) {
				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
   		        _log.errorTrace(methodName, e);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceRoutingControlCache[getRoutingControlDetails]", "", "", "", "ExecutionException :" + e.getMessage());
			}
        	catch (InvalidCacheLoadException e) { 
				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
   		        _log.errorTrace(methodName, e);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceRoutingControlCache[getRoutingControlDetails]", "", "", "", "InvalidCacheLoadException :" + e.getMessage());	
			}
        } else {
    		list = (ArrayList) _routingMap.get(p_key);
        }
        return list;
    }
    
    public static ArrayList<ListValueVO> getRoutingControlDetailsListFromRedis(String key) throws BTSLBaseException{
      	 RedisActivityLog.log("InterfaceRoutingControlCache->getRoutingControlDetailsListFromRedis->Start");
      	 Jedis jedis = null;
      	 String methodName ="getRoutingControlDetailsListFromRedis";
      	ArrayList<ListValueVO> list = null;
   		 try {
   			 jedis = RedisConnectionPool.getPoolInstance().getResource();
   			 String routingControlDetailsList = jedis.hget(hKeyInterfaceRoutingControlCacheMap, key);
   			if(!BTSLUtil.isNullString(routingControlDetailsList))
   				list = (ArrayList<ListValueVO>)gson.fromJson(routingControlDetailsList,new TypeToken<ArrayList<ListValueVO>>(){}.getType());
   		 }catch(JedisConnectionException je){
   			 _log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
   			 _log.errorTrace(methodName, je);
     	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceRoutingControlCache[getRoutingControlDetailsListFromRedis]", "", "", "", "JedisConnectionException :" + je.getMessage());
     		        throw new BTSLBaseException(InterfaceRoutingControlCache.class.getName(), methodName,je.getMessage());
   		 }catch(NoSuchElementException  ex){
   			 _log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
   			 _log.errorTrace(methodName, ex);
     	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceRoutingControlCache[getRoutingControlDetailsListFromRedis]", "", "", "", "NoSuchElementException :" + ex.getMessage());
     		        throw new BTSLBaseException(InterfaceRoutingControlCache.class.getName(), methodName,ex.getMessage());
   		 }catch (Exception e) {
   			 _log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
   			 _log.errorTrace(methodName, e);
              EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InterfaceRoutingControlCache[getRoutingControlDetailsListFromRedis]", "", "", "", "Exception :" + e.getMessage());
   			throw new BTSLBaseException(InterfaceRoutingControlCache.class.getName(), methodName,e.getMessage());
   		 }
   		 finally {
   	        	if (jedis != null) {
   	        	jedis.close();
   	        	}
   	        }
     		 RedisActivityLog.log("InterfaceRoutingControlCache->getRoutingControlDetailsListFromRedis->End");
     		 return list;
      }
      

}
