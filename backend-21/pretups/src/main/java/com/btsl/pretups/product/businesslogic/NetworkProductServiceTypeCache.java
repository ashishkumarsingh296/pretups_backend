package com.btsl.pretups.product.businesslogic;

/*
 * NetworkProductServiceTypeCache.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 25/08/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Class to load the Service Type and Product Type mapping
 */

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

public class NetworkProductServiceTypeCache implements Runnable {

    private static Log _log = LogFactory.getLog(NetworkProductServiceTypeCache.class.getName());
    private static HashMap<String,ListValueVO> _productServericeTypeMap = new HashMap<String,ListValueVO>();
    private static HashMap<String,NetworkProductVO> _networkProductMapping = new HashMap<String,NetworkProductVO>();
    private static NetworkProductDAO _networkProductDAO = new NetworkProductDAO();
    private static String redisEnable = BTSLUtil.NullToString(Constants.getProperty("REDIS_ENABLE"));
    private static final String hKeyProductServericeTypeMap = "ProductServericeTypeMap";
    private static final String hKeyNetworkProductMapping ="NetworkProductMapping";
    private static int keyTimer = Integer.parseInt(Constants.getProperty("KEYTIMER"));
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss:mss").create();
    
    private static LoadingCache<String,NetworkProductVO>  networkProductMapCache = CacheBuilder.newBuilder()
    	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)     
    	    .build(new CacheLoader<String, NetworkProductVO>(){
    			@Override
    			public NetworkProductVO load(String key) throws Exception {
    				return getNetworkProductMapFromRedis(key);
    			}
    	     });
    
   
    private static LoadingCache<String,ListValueVO>  productServiceTypeMapCache = CacheBuilder.newBuilder()
    	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)     
    	    .build(new CacheLoader<String, ListValueVO>(){
    			@Override
    			public ListValueVO load(String key) throws Exception {
    				return getProductServiceTypeMapFromRedis(key);
    			}
    	     });
    
    
    
    public void run() {
        try {
            Thread.sleep(50);
            loadProductServiceTypeMappingAtStartup();
            loadNetworkProductMappingAtStartup();
        } catch (Exception e) {
        	 _log.error("NetworkProductServiceTypeCache#refreshProductServiceTypeMapping init() Exception ", e);
        }
    }
   
    public static void refreshProductServiceTypeMapping() throws BTSLBaseException {
    	final String methodName = "refreshProductServiceTypeMapping";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
    	Jedis jedis = null;
        try {
            HashMap<String,ListValueVO> tempMap = null;
            if (_log.isDebugEnabled()) {
                _log.debug("refreshProductServiceTypeMapping", " Before loading:" + _productServericeTypeMap);
            }
            _productServericeTypeMap = _networkProductDAO.loadProductServiceTypeMapping();
            if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
            RedisActivityLog.log("NetworkProductServiceTypeCache->refreshProductServiceTypeMapping->Start");
            jedis = RedisConnectionPool.getPoolInstance().getResource();
            Pipeline pipeline = jedis.pipelined();
     	    pipeline.del(hKeyProductServericeTypeMap);
     			for (Entry<String, ListValueVO> entry : _productServericeTypeMap.entrySet())  {
     		      pipeline.hset(hKeyProductServericeTypeMap,entry.getKey(), gson.toJson(entry.getValue()));
        	 }
     		pipeline.sync();
            RedisActivityLog.log("NetworkProductServiceTypeCache->refreshProductServiceTypeMapping->Stop");
           }
            if (_log.isDebugEnabled()) {
                _log.debug("refreshProductServiceTypeMapping", " After loading:" + _productServericeTypeMap.size());
            }
        }
        catch(BTSLBaseException be) {
        	_log.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
        	_log.errorTrace(methodName, be);
        	throw be;
        }catch(JedisConnectionException je){
        	_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
        	_log.errorTrace(methodName, je);
        	EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "NetworkProductServiceTypeCache[" + methodName + "]", "", "", "", "JedisConnectionException :" + je.getMessage());
        	throw new BTSLBaseException("NetworkProductServiceTypeCache", methodName,je.getMessage());
		}catch(NoSuchElementException  ex){
			_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
			_log.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "NetworkProductServiceTypeCache[" + methodName + "]", "", "", "", "NoSuchElementException :" + ex.getMessage());
	        throw new BTSLBaseException("NetworkProductServiceTypeCache", methodName,ex.getMessage());
		}catch (Exception e){
        	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
        	_log.errorTrace(methodName, e);
        	throw new BTSLBaseException("NetworkProductServiceTypeCache", methodName, e.getMessage());
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED);
        	}
        }
    }

    public static void refreshNetworkProductMapping() throws BTSLBaseException {
    	final String methodName = "refreshNetworkProductMapping";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
    	Jedis jedis =null;
        try {
            HashMap<String,NetworkProductVO> tempMap =_networkProductDAO.loadNetworkProductMapping();

            if (_log.isDebugEnabled()) {
                _log.debug("refreshNetworkProductMapping", " Before loading:" + _productServericeTypeMap);
            }
           
          if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
	            RedisActivityLog.log("NetworkProductServiceTypeCache->refreshNetworkProductMapping->Start");
	            jedis = RedisConnectionPool.getPoolInstance().getResource();
	            Pipeline pipeline = jedis.pipelined();
	    	    jedis.del(hKeyNetworkProductMapping);
	    		for (Entry<String, NetworkProductVO> entry : tempMap.entrySet())  {
	    		      pipeline.hset(hKeyNetworkProductMapping, entry.getKey(), gson.toJson(entry.getValue()));
	    			} 
	    		pipeline.sync();
	            RedisActivityLog.log("NetworkProductServiceTypeCache->refreshNetworkProductMapping->End");
          } else {
              compareNetworkProductMaps(_networkProductMapping, tempMap);
              _networkProductMapping = tempMap;
         }
            if (_log.isDebugEnabled()) {
                _log.debug("refreshNetworkProductMapping", " After loading:" + _productServericeTypeMap.size());
            }
        }
        catch(BTSLBaseException be) {
        	_log.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
        	_log.errorTrace(methodName, be);
        	throw be;
        } catch(JedisConnectionException je){
        	_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
        	_log.errorTrace(methodName, je);
        	EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "NetworkProductServiceTypeCache[" + methodName + "]", "", "", "", "JedisConnectionException :" + je.getMessage());
        	throw new BTSLBaseException("NetworkProductServiceTypeCache", methodName,je.getMessage());
		}catch(NoSuchElementException  ex){
			_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
			_log.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "NetworkProductServiceTypeCache[" + methodName + "]", "", "", "", "NoSuchElementException :" + ex.getMessage());
	        throw new BTSLBaseException("NetworkProductServiceTypeCache", methodName,ex.getMessage());
		}catch (Exception e){
        	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
        	_log.errorTrace(methodName, e);
        	throw new BTSLBaseException("NetworkProductServiceTypeCache", methodName, e.getMessage());
        }finally {
        	if (jedis != null) {
        	jedis.close();
        	}
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED);
        	}
	     }
    }

    /*
     * private static void compareMaps(HashMap p_previousMap, HashMap
     * p_currentMap)
     * {
     * if (_log.isDebugEnabled())
     * _log.debug("compareMaps()","Entered p_previousMap: "
     * +p_previousMap+"  p_currentMap: "+p_currentMap);
     * try
     * {
     * Iterator iterator = null;
     * Iterator copiedIterator = null;
     * if(p_previousMap.size() == p_currentMap.size() )
     * {
     * iterator = p_previousMap.keySet().iterator();
     * copiedIterator = p_previousMap.keySet().iterator();
     * }
     * else
     * if(p_previousMap.size() > p_currentMap.size() )
     * {
     * iterator = p_previousMap.keySet().iterator();
     * copiedIterator = p_previousMap.keySet().iterator();
     * }
     * else
     * if(p_previousMap.size() < p_currentMap.size() )
     * {
     * iterator = p_currentMap.keySet().iterator();
     * copiedIterator = p_previousMap.keySet().iterator();
     * }
     * 
     * boolean isNewAdded = false;
     * while (iterator.hasNext())
     * {
     * String key = (String)iterator.next();
     * String prevValue= (String) p_previousMap.get(key);
     * String currValue= (String) p_currentMap.get(key);
     * 
     * if(prevValue != null && currValue == null)
     * {
     * isNewAdded = true;
     * CacheOperationLog.log("ProductServiceTypeMapping",BTSLUtil.formatMessage(
     * "Delete",key,prevValue));
     * }
     * else if(prevValue == null && currValue != null)
     * CacheOperationLog.log("ProductServiceTypeMapping",BTSLUtil.formatMessage(
     * "Add",key,currValue));
     * else if(prevValue != null && currValue != null)
     * {
     * if( ! currValue.equals(prevValue))
     * CacheOperationLog.log("ProductServiceTypeMapping",BTSLUtil.formatMessage(
     * "Modify",key,"From :"+prevValue+" To:"+currValue));
     * }
     * }
     * 
     * // Note: this case arises when same number of element added and deleted
     * as well
     * if(p_previousMap.size() == p_currentMap.size() && isNewAdded )
     * {
     * HashMap tempMap = new HashMap(p_currentMap);
     * while (copiedIterator.hasNext())
     * {
     * tempMap.remove((String)copiedIterator.next());
     * }
     * 
     * Iterator iterator2 = tempMap.keySet().iterator();
     * while(iterator2.hasNext())
     * {
     * String currValue= (String) p_currentMap.get(iterator2.next());
     * CacheOperationLog.log("ProductServiceTypeMapping",BTSLUtil.formatMessage(
     * "Add",(String)iterator2.next(),currValue));
     * }
     * }
     * }
     * catch(Exception e)
     * {
     * e.printStackTrace();
     * }
     * if (_log.isDebugEnabled()) _log.debug("compareMaps()","Exited");
     * }
     */

    public static ListValueVO getProductServiceValueVO(String p_serviceType, String p_subService) {
        String key = p_serviceType + "_" + p_subService;
        ListValueVO listValueVO = null;
        try{
        	if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        		listValueVO = productServiceTypeMapCache.get(key);
        	} else {
        		listValueVO = (ListValueVO) _productServericeTypeMap.get(key);
        	}
        }catch(ExecutionException e){
      		_log.error("getProductServiceValueVO", PretupsI.EXCEPTION + e.getMessage());
	        _log.errorTrace("getProductServiceValueVO", e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "NetworkProductServiceTypeCache[getProductServiceValueVO]", "", "", "", "ExecutionException :" + e.getMessage());
    	}catch (InvalidCacheLoadException e) { 
			_log.info("getProductServiceValueVO",  e.getMessage());	
 	     }
        
        return listValueVO; 
    }

    public static NetworkProductVO getNetworkProductDetails(String p_networkCode, String p_productCode) {
    	NetworkProductVO networkProductVO = null;
    	String key = p_networkCode + "_" + p_productCode;
        try{
        	if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        		networkProductVO = networkProductMapCache.get(key);
        	} else {
			   networkProductVO = (NetworkProductVO) _networkProductMapping.get(key);
        	}
        }catch(ExecutionException e){
      		_log.error("getNetworkProductDetails", PretupsI.EXCEPTION + e.getMessage());
	        _log.errorTrace("getNetworkProductDetails", e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "NetworkProductServiceTypeCache[getNetworkProductDetails]", "", "", "", "ExecutionException :" + e.getMessage());
    	}catch (InvalidCacheLoadException e) { 
			_log.info("getNetworkProductDetails",  e.getMessage());	

 	     }
        return networkProductVO;
    }

    private static void compareNetworkProductMaps(HashMap p_previousMap, HashMap p_currentMap) {
        if (_log.isDebugEnabled()) {
            _log.debug("compareNetworkProductMaps()", "Entered PreviousMap " + p_previousMap + "  Current Map" + p_currentMap);
        }
        final String METHOD_NAME = "compareNetworkProductMaps";
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
                NetworkProductVO prevVO = (NetworkProductVO) p_previousMap.get(key);
                NetworkProductVO curVO = (NetworkProductVO) p_currentMap.get(key);

                if (prevVO != null && curVO == null) {
                    isNewAdded = true;
                    CacheOperationLog.log("NetworkProductMapping", BTSLUtil.formatMessage("Delete", key, prevVO.logInfo()));
                } else if (prevVO == null && curVO != null) {
                    CacheOperationLog.log("NetworkProductMapping", BTSLUtil.formatMessage("Add", key, curVO.logInfo()));
                } else if (prevVO != null && curVO != null) {
                    if (!curVO.equals(prevVO)) {
                        CacheOperationLog.log("NetworkProductMapping", BTSLUtil.formatMessage("Modify", key, curVO.differences(prevVO)));
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
                    NetworkProductVO mappingVO = (NetworkProductVO) p_currentMap.get(iterator2.next());
                    CacheOperationLog.log("NetworkProductMapping", BTSLUtil.formatMessage("Add", (String) iterator2.next(), mappingVO.logInfo()));
                }
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("compareNetworkProductMaps()", "Exited");
        }
    }
    public static void loadProductServiceTypeMappingAtStartup() throws BTSLBaseException {
    	final String methodName = "loadProductServiceTypeMappingAtStartup";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
    	Jedis jedis =  null;
        try {
            if (_log.isDebugEnabled()) {
                _log.debug("loadProductServiceTypeMappingAtStartup", " Before loading:" + _productServericeTypeMap);
            }
            if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
            	RedisActivityLog.log("NetworkProductServiceTypeCache->loadProductServiceTypeMappingAtStartup->Start");
                jedis = RedisConnectionPool.getPoolInstance().getResource();
            	Pipeline pipeline = jedis.pipelined();
    			HashMap<String, ListValueVO> tempMap = _networkProductDAO.loadProductServiceTypeMapping();
        		if(!jedis.exists(hKeyProductServericeTypeMap)) {
	     			for (Entry<String, ListValueVO> entry : tempMap.entrySet())  {
	     				pipeline.hset(hKeyProductServericeTypeMap, entry.getKey(), gson.toJson(entry.getValue()));
	     			}  
     			pipeline.sync();
        	  }
            RedisActivityLog.log("NetworkProductServiceTypeCache->loadProductServiceTypeMappingAtStartup->End");
           }else{
   			_productServericeTypeMap = _networkProductDAO.loadProductServiceTypeMapping();
           }
            if (_log.isDebugEnabled()) {
                _log.debug("loadProductServiceTypeMappingAtStartup", " After loading:" + _productServericeTypeMap.size());
            }
        }catch(BTSLBaseException be) {
        	_log.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
        	_log.errorTrace(methodName, be);
        	throw be;
        }catch(JedisConnectionException je){
			_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	        _log.errorTrace(methodName, je);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "NetworkProductServiceTypeCache[loadProductServiceTypeMappingAtStartup]", "", "", "", "JedisConnectionException :" + je.getMessage());
        	throw new BTSLBaseException("NetworkProductServiceTypeCache", methodName, je.getMessage());
        }catch(NoSuchElementException  ex){
			_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
		     _log.errorTrace(methodName, ex);
	        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "NetworkProductServiceTypeCache[loadProductServiceTypeMappingAtStartup]", "", "", "", "NoSuchElementException :" + ex.getMessage());
        	throw new BTSLBaseException("NetworkProductServiceTypeCache", methodName, ex.getMessage());
        }catch (Exception e){
        	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
        	_log.errorTrace(methodName, e);
        	throw new BTSLBaseException("NetworkProductServiceTypeCache", methodName, e.getMessage());
        } finally {
        	if(jedis!=null)
        		jedis.close();
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED);
        	}
        }
    }

    public static void loadNetworkProductMappingAtStartup() throws BTSLBaseException {
    	final String methodName = "loadNetworkProductMappingAtStartup";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
    	Jedis jedis = null;
        try {
            if (_log.isDebugEnabled()) {
                _log.debug("loadNetworkProductMappingAtStartup", " Before loading:" + _networkProductMapping);
            }
            if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
            	RedisActivityLog.log("NetworkProductServiceTypeCache->loadNetworkProductMappingAtStartup->Start");
                jedis = RedisConnectionPool.getPoolInstance().getResource();
                Pipeline pipeline = jedis.pipelined();
            	HashMap<String, NetworkProductVO> tempMap = _networkProductDAO.loadNetworkProductMapping();
                if(!jedis.exists(hKeyNetworkProductMapping)) {
				for (Entry<String, NetworkProductVO> entry : tempMap.entrySet())  {
				      pipeline.hset(hKeyNetworkProductMapping, entry.getKey(), gson.toJson(entry.getValue()));
					}  
				pipeline.sync();
	            RedisActivityLog.log("NetworkProductServiceTypeCache->loadNetworkProductMappingAtStartup->Stop");
           }
          }else{
          	  _networkProductMapping = _networkProductDAO.loadNetworkProductMapping();
          }
            if (_log.isDebugEnabled()) {
                _log.debug("loadNetworkProductMappingAtStartup", " After loading:" + _networkProductMapping.size());
            }
        }
        catch(BTSLBaseException be) {
        	_log.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
        	_log.errorTrace(methodName, be);
        	throw be;
        }catch(JedisConnectionException je){
			_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	        _log.errorTrace(methodName, je);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "NetworkProductServiceTypeCache[loadNetworkProductMappingAtStartup]", "", "", "", "JedisConnectionException :" + je.getMessage());
        	throw new BTSLBaseException("NetworkProductServiceTypeCache", methodName, je.getMessage());
        }catch(NoSuchElementException  ex){
			_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
		     _log.errorTrace(methodName, ex);
	        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "NetworkProductServiceTypeCache[loadNetworkProductMappingAtStartup]", "", "", "", "NoSuchElementException :" + ex.getMessage());
        	throw new BTSLBaseException("NetworkProductServiceTypeCache", methodName, ex.getMessage());
		}catch (Exception e){
        	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
        	_log.errorTrace(methodName, e);
        	throw new BTSLBaseException("NetworkProductServiceTypeCache", methodName, e.getMessage());
        } finally {
        	if(jedis!=null)
        		jedis.close();
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED);
        	}
        }
    }  
    
    /**
     * @param key
     * @return
     */
    public static NetworkProductVO getNetworkProductMapFromRedis(String key) {
        String methodName ="getNetworkProductMapFromRedis";
    	if (_log.isDebugEnabled()) {
            _log.debug(methodName,"Entered key: " + key);
        }
        Jedis jedis = null;
        NetworkProductVO networkProductVO = null;
		 try {
         	RedisActivityLog.log("ServiceSelectorMappingCache->getNetworkProductMapFromRedis->Start");
			jedis = RedisConnectionPool.getPoolInstance().getResource();
			String json = jedis.hget(hKeyNetworkProductMapping,key);
			if(!BTSLUtil.isNullString(json)){
				networkProductVO=gson.fromJson(json, NetworkProductVO.class);
			}
        	RedisActivityLog.log("ServiceSelectorMappingCache->getNetworkProductMapFromRedis->End");
		  } catch(JedisConnectionException je){
				_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
   		        _log.errorTrace(methodName, je);
   	           EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ServiceSelectorMappingCache[" + methodName + "]", "", "", "", "JedisConnectionException :" + je.getMessage());
   			}catch(NoSuchElementException  ex){
				_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
   		        _log.errorTrace(methodName, ex);
   	           EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ServiceSelectorMappingCache[" + methodName + "]", "", "", "", "NoSuchElementException :" + ex.getMessage());
   			}catch (Exception e) {
			   _log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
			   _log.errorTrace(methodName, e);
               EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ServiceSelectorMappingCache[" + methodName + "]", "", "", "", "Exception :" + e.getMessage());
   			}finally {
    		 if(jedis != null ) {
    			 jedis.close();
    		 }
    		 if (_log.isDebugEnabled()) {
    	            _log.debug(methodName,"Exited networkProductVO :" + networkProductVO);
    	        }
    	   }
		 return networkProductVO;
		 }
    
    

    /**
     * @param key
     * @return
     */
    public static ListValueVO getProductServiceTypeMapFromRedis(String key) {
        String methodName ="getProductServiceTypeMapFromRedis";
    	if (_log.isDebugEnabled()) {
            _log.debug(methodName,"Entered key: " + key);
        }
        Jedis jedis = null;
        ListValueVO listVo = null;
		 try {
         	RedisActivityLog.log("ServiceSelectorMappingCache->getProductServiceTypeMapFromRedis->Start");
			jedis = RedisConnectionPool.getPoolInstance().getResource();
			String json = jedis.hget(hKeyProductServericeTypeMap,key);
			if(!BTSLUtil.isNullString(json)){
				listVo=gson.fromJson(json, ListValueVO.class);
			}
        	RedisActivityLog.log("ServiceSelectorMappingCache->getProductServiceTypeMapFromRedis->End");
		  } catch(JedisConnectionException je){
				_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
   		        _log.errorTrace(methodName, je);
   	           EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ServiceSelectorMappingCache[" + methodName + "]", "", "", "", "JedisConnectionException :" + je.getMessage());
   			}catch(NoSuchElementException  ex){
				_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
   		        _log.errorTrace(methodName, ex);
   	           EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ServiceSelectorMappingCache[" + methodName + "]", "", "", "", "NoSuchElementException :" + ex.getMessage());
   			}catch (Exception e) {
			   _log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
			   _log.errorTrace(methodName, e);
               EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ServiceSelectorMappingCache[" + methodName + "]", "", "", "", "Exception :" + e.getMessage());
   			}finally {
    		 if(jedis != null ) {
    			 jedis.close();
    		 }
    		 if (_log.isDebugEnabled()) {
    	            _log.debug(methodName,"Exited listVo :" + listVo);
    	        }
    	   }
		 return listVo;
		 }
}
