/**
 * @(#)NetworkPrefixCache.java
 *                             Copyright(c) 2005, Bharti Telesoft Ltd.
 *                             All Rights Reserved
 * 
 *                             <description>
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Author Date History
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             avinash.kamthan June 21, 2005 Initital Creation
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 * 
 */

package com.btsl.pretups.network.businesslogic;

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
 * 
 */
public class NetworkPrefixCache  implements Runnable{

    private static Log _log = LogFactory.getLog(NetworkPrefixCache.class.getName());
    private static final String CLASS_NAME = "NetworkPrefixCache";
    private static HashMap<String,NetworkPrefixVO>  _networkPrefixesMap = new HashMap<String,NetworkPrefixVO> ();
    private static String redisEnable = BTSLUtil.NullToString(Constants.getProperty("REDIS_ENABLE"));
    private static final String hKeyNetworkPrefixesMap = "NetworkPrefixCache";
    private static final int keyTimer = Integer.parseInt(Constants.getProperty("KEYTIMER"));
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss:mss").create();

    private static LoadingCache<String,NetworkPrefixVO>  networkPrefixCacheMap = CacheBuilder.newBuilder()
    	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)     
    	    .build(new CacheLoader<String, NetworkPrefixVO>(){
    			@Override
    			public NetworkPrefixVO load(String key) throws Exception {
    				return getNetworkPrefixFromRedis(key);
    			}
    	     });
    
    public void run() {
        try {
            Thread.sleep(50);
            loadNetworkPrefixesAtStartup();
        } catch (Exception e) {
        	 _log.error("NetworkPrefixCache init() Exception ", e);
        }
    }
    public static void loadNetworkPrefixesAtStartup() throws BTSLBaseException {
    	final String methodName = "loadNetworkPrefixesAtStartup";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        try{
        	if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        		Jedis jedis =null;
	   	        RedisActivityLog.log("NetworkPrefixCache->loadNetworkPrefixesAtStartup->Start");
	   			 try {
	   				 jedis = RedisConnectionPool.getPoolInstance().getResource();
	   				 Pipeline pipeline = jedis.pipelined();
    				 if(!jedis.exists(hKeyNetworkPrefixesMap)) {
    					HashMap<String,NetworkPrefixVO> currencyConversionDetailMap = (HashMap<String,NetworkPrefixVO>) loadNetworksPrefixes();
    					for (Entry<String,NetworkPrefixVO> entry : currencyConversionDetailMap.entrySet())  {
	    					pipeline.hset(hKeyNetworkPrefixesMap,entry.getKey(), gson.toJson(entry.getValue()));
		   				    if (_log.isDebugEnabled()) {
		   				    	_log.debug(methodName, CLASS_NAME+ " :"+entry.getKey()+" is loaded into Redis Cache");
			   	             }
	   					 }
	    			 pipeline.sync();
	    			}
	   			 }catch(JedisConnectionException je){
						_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
		   		        _log.errorTrace(methodName, je);
		   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkPrefixCache[loadNetworkPrefixesAtStartup]", "", "", "", "JedisConnectionException :" + je.getMessage());
		   	            throw new BTSLBaseException(NetworkPrefixCache.class.getName(), methodName,je.getMessage());
				  }catch(NoSuchElementException  ex){
						_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
		   		        _log.errorTrace(methodName, ex);
		   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkPrefixCache[loadNetworkPrefixesAtStartup]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		   		        throw new BTSLBaseException(NetworkPrefixCache.class.getName(), methodName,ex.getMessage());
				   }catch (Exception e) {
					   _log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
					   _log.errorTrace(methodName, e);
		                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkPrefixCache[loadNetworkPrefixesAtStartup]", "", "", "", "Exception :" + e.getMessage());
					    throw new BTSLBaseException(NetworkPrefixCache.class.getName(), methodName,e.getMessage());
			      }finally{
	   				 if(jedis != null)
	   					jedis.close();
	   			 }
		   	 RedisActivityLog.log("NetworkPrefixCache->loadNetworkPrefixesAtStartup->End");
			} else {	 
				_networkPrefixesMap = loadNetworksPrefixes();
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
        	throw new BTSLBaseException(CLASS_NAME, methodName, "Exception in loading network prefixes at startup.");
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED);
        	}
        }
    }

    /**
     * To load the networks details
     * 
     * @return
     *         HashMap
     *         NetworkCache
     */
    private static HashMap<String,NetworkPrefixVO>  loadNetworksPrefixes() throws BTSLBaseException {
    	final String methodName = "loadNetworksPrefixes";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        NetworkDAO networkDAO = new NetworkDAO();
        HashMap<String,NetworkPrefixVO>  map = null;
        try {
            map = networkDAO.loadNetworkPrefixCache();

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
        	throw new BTSLBaseException(CLASS_NAME, methodName, "Exception in loading network prefixes.");
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED);
        	}
        }
        return map;
    }

    /**
     * to update the cache
     * 
     * void
     * NetworkPrefixesCache
     */
    public static void updateNetworkPrefixes() throws BTSLBaseException {
    	final String methodName = "updateNetworkPrefixes";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}

        HashMap<String,NetworkPrefixVO> currentMap = null;
        try{
        	currentMap = loadNetworksPrefixes();
 		       if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
 		    	  Jedis jedis = null;
 		   	      RedisActivityLog.log("NetworkPrefixCache->updateNetworkPrefixes->Start");
 		   	      try{
				   jedis = RedisConnectionPool.getPoolInstance().getResource();
				   Pipeline pipeline = jedis.pipelined();
					//Get the byte array for the hash key
					jedis.del(hKeyNetworkPrefixesMap);
					 for (Entry<String,NetworkPrefixVO> entry : currentMap.entrySet())  {
				      pipeline.hset(hKeyNetworkPrefixesMap, entry.getKey(), gson.toJson(entry.getValue()));
					 }
					 pipeline.sync();
				  }catch(JedisConnectionException je){
						_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
		   		        _log.errorTrace(methodName, je);
		   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkPrefixCache[updateNetworkPrefixes]", "", "", "", "JedisConnectionException :" + je.getMessage());
		   	            throw new BTSLBaseException(NetworkPrefixCache.class.getName(), methodName,je.getMessage());
				  }catch(NoSuchElementException  ex){
						_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
		   		        _log.errorTrace(methodName, ex);
		   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkPrefixCache[updateNetworkPrefixes]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		   		        throw new BTSLBaseException(NetworkPrefixCache.class.getName(), methodName,ex.getMessage());
				   }catch (Exception e) {
					   _log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
					   _log.errorTrace(methodName, e);
		                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkPrefixCache[updateNetworkPrefixes]", "", "", "", "Exception :" + e.getMessage());
					    throw new BTSLBaseException(NetworkPrefixCache.class.getName(), methodName,e.getMessage());
			      }finally{
					  if(jedis != null)
						  jedis.close();
				  }
 		   	     RedisActivityLog.log("NetworkPrefixCache->updateNetworkPrefixes->End");
 		   	  } else {
 		   	  if (_networkPrefixesMap != null && _networkPrefixesMap.size() > 0) {
 		            compareMaps(_networkPrefixesMap, currentMap);
 		        }
 		        _networkPrefixesMap = currentMap;
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
        	throw new BTSLBaseException(CLASS_NAME, methodName, "Exception in updating network prefixes.");
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug("updateNetworkPrefixes()", PretupsI.EXITED + _networkPrefixesMap.size());
        	}
        }
    }

    /**
     * to get the requested objcet from the cache
     * 
     * @param p_msisdnPrefix
     * @param p_seriesType
     * @return
     *         Object
     *         NetworkCache
     */
    public static Object getObject(String p_msisdnPrefix, String p_seriesType){

        NetworkPrefixVO networkPrefixVO = null;
        String key = null;
        if (_log.isDebugEnabled()) {
            _log.debug("getObject()", "Entered:msisdnPrefix=" + p_msisdnPrefix + " Series Type" + p_seriesType);
        }
        key = p_msisdnPrefix + "_" + p_seriesType;
		 if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
			 try{
				 networkPrefixVO = networkPrefixCacheMap.get(key);
			 }catch(ExecutionException e){
	 	        _log.error("getObject", PretupsI.EXCEPTION + e.getMessage());
  		        _log.errorTrace("getObject", e);
               EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, CLASS_NAME + "[getObject]", "", "", "", "Exception :" + e.getMessage());
 			 }catch (InvalidCacheLoadException e) { 
   				_log.error("getObject", PretupsI.EXCEPTION + e.getMessage());
 			     _log.errorTrace("getObject", e);
 		        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, CLASS_NAME + "[getObject]", "", "", "", "InvalidCacheLoadException :" + e.getMessage());	
 	        }
		 } else {
    	   networkPrefixVO = (NetworkPrefixVO) _networkPrefixesMap.get(key);
       }
        return networkPrefixVO;
    }

    /**
     * 
     * @param p_msisdnPrefix
     * @return NetworkPrefixVO
     */
    public static Object getObject(String p_msisdnPrefix) {

        Object networkPrefixObj = null;
        if (_log.isDebugEnabled()) {
            _log.debug("getObject()", "Entered:msisdnPrefix=" + p_msisdnPrefix);
        }

        networkPrefixObj = getObject(p_msisdnPrefix, PretupsI.SERIES_TYPE_PREPAID);
        if (networkPrefixObj == null) {
            networkPrefixObj = getObject(p_msisdnPrefix, PretupsI.SERIES_TYPE_POSTPAID);
            ;
        }

        if (_log.isDebugEnabled()) {
            _log.debug("getObject()", "Exited " + networkPrefixObj);
        }

        return networkPrefixObj;
    }

    /**
     * Method to get the Network Prefix Object of only Operator Based Series
     * 
     * @param p_msisdnPrefix
     * @param p_checkOtherSeries
     *            Pass False: If only Opertaor based series are allowed
     * @return
     */
    public static Object getObject(String p_msisdnPrefix, boolean p_checkOtherSeries) {
        if (_log.isDebugEnabled()) {
            _log.debug("getObject()", "Entered:msisdnPrefix=" + p_msisdnPrefix + "p_checkOtherSeries=" + p_checkOtherSeries);
        }
        Object networkPrefixObj = null;
        if (p_checkOtherSeries) {
            networkPrefixObj = getObject(p_msisdnPrefix);
        } else {
            networkPrefixObj = getObject(p_msisdnPrefix, PretupsI.SERIES_TYPE_PREPAID);
            if (networkPrefixObj == null || PretupsI.OPERATOR_TYPE_OTH.equals(((NetworkPrefixVO) networkPrefixObj).getOperator())) {
                networkPrefixObj = getObject(p_msisdnPrefix, PretupsI.SERIES_TYPE_POSTPAID);
                ;
            }
            if (networkPrefixObj == null || PretupsI.OPERATOR_TYPE_OTH.equals(((NetworkPrefixVO) networkPrefixObj).getOperator())) {
                networkPrefixObj = null;
            }
            if (_log.isDebugEnabled()) {
                _log.debug("getObject()", "Exited " + networkPrefixObj);
            }
        }
        return networkPrefixObj;
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

        if (_log.isDebugEnabled()) {
            _log.debug("compareMaps()", "Entered p_previousMap: " + p_previousMap + "  p_currentMap: " + p_currentMap);
        }
        final String METHOD_NAME = "compareMaps";
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

            // to check whether any new network added or not but size of
            boolean isNewAdded = false;
            while (iterator != null && iterator.hasNext()) {
                String key = (String) iterator.next();
                NetworkPrefixVO prevNetworkPrefixVO = (NetworkPrefixVO) p_previousMap.get(key);
                NetworkPrefixVO curNetworkPrefixVO = (NetworkPrefixVO) p_currentMap.get(key);

                if (prevNetworkPrefixVO != null && curNetworkPrefixVO == null) {
                    // network status has been changed
                    // less no of rows in current than previous
                    // System.out.println("Network "+prevNetworkVO.getNetworkCode()+" Previous Status  "+prevNetworkVO.getStatus()+" current Status Removed");
                    isNewAdded = true;
                    CacheOperationLog.log("NetworkPrefixCache", BTSLUtil.formatMessage("Delete", prevNetworkPrefixVO.getNetworkCode(), prevNetworkPrefixVO.logInfo()));
                } else if (prevNetworkPrefixVO == null && curNetworkPrefixVO != null) {
                    // new network added
                    // System.out.println("Network "+curNetworkVO.getNetworkCode()+"  Added has Status  "+curNetworkVO.getStatus());
                    CacheOperationLog.log("NetworkPrefixCache", BTSLUtil.formatMessage("Add", curNetworkPrefixVO.getNetworkCode(), curNetworkPrefixVO.logInfo()));
                } else if (prevNetworkPrefixVO != null && curNetworkPrefixVO != null) {
                    if (!curNetworkPrefixVO.equalsNetworkPrefixVO(prevNetworkPrefixVO)) {
                        CacheOperationLog.log("NetworkPrefixCache", BTSLUtil.formatMessage("Modify", curNetworkPrefixVO.getNetworkCode(), curNetworkPrefixVO.diffrences(prevNetworkPrefixVO)));

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
                    NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) p_currentMap.get(iterator2.next());
                    CacheOperationLog.log("NetworkPrefixCache", BTSLUtil.formatMessage("Add", networkPrefixVO.getNetworkCode(), networkPrefixVO.logInfo()));
                }
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("compareMaps()", "Exited");
        }
    }
    
    public static NetworkPrefixVO getNetworkPrefixFromRedis(String key){
        String methodName= "getNetworkPrefixFromRedis";
     	if (_log.isDebugEnabled()) {
             _log.debug(methodName, "Entered key: "+key);
         }
     	NetworkPrefixVO networkPrefixVO = null;
         Jedis jedis = null;
 		 try {
 	        RedisActivityLog.log("NetworkCache->getNetworkPrefixFromRedis->Start");
 			jedis = RedisConnectionPool.getPoolInstance().getResource();
 			String json = jedis.hget(hKeyNetworkPrefixesMap,key);
 			if(json != null)
 				networkPrefixVO=gson.fromJson(json, NetworkPrefixVO.class);
        	RedisActivityLog.log("NetworkCache->getNetworkPrefixFromRedis->End");
 		 }catch(JedisConnectionException je){
				_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
   		        _log.errorTrace(methodName, je);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkPrefixCache[getNetworkPrefixFromRedis]", "", "", "", "JedisConnectionException :" + je.getMessage());
		  }catch(NoSuchElementException  ex){
				_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
   		        _log.errorTrace(methodName, ex);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkPrefixCache[getNetworkPrefixFromRedis]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		   }catch (Exception e) {
			   _log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
			   _log.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkPrefixCache[getNetworkPrefixFromRedis]", "", "", "", "Exception :" + e.getMessage());
	      }finally {
     		 if(jedis != null ) {
     			 jedis.close();
     		 }
     		 if (_log.isDebugEnabled()) {
     	            _log.debug(methodName, "Exited networkPrefixVO: " + networkPrefixVO);
     	        }
     	   }
 		 return networkPrefixVO;
 		 }

}
