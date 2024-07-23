/**
 * @(#)NetworkInterfaceModuleCache.java
 *                                      Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                      All Rights Reserved
 * 
 *                                      <description>
 *                                      ----------------------------------------
 *                                      ----------------------------------------
 *                                      -----------------
 *                                      Author Date History
 *                                      ----------------------------------------
 *                                      ----------------------------------------
 *                                      -----------------
 *                                      avinash.kamthan June 28, 2005 Initital
 *                                      Creation
 *                                      ----------------------------------------
 *                                      ----------------------------------------
 *                                      -----------------
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
import com.btsl.pretups.interfaces.businesslogic.InterfaceDAO;
import com.btsl.pretups.interfaces.businesslogic.InterfaceVO;
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
public class NetworkInterfaceModuleCache implements Runnable {

    private static Log _log = LogFactory.getLog(NetworkInterfaceModuleCache.class.getName());
    private final static String CLASS_NAME = "NetworkInterfaceModuleCache";

    private static HashMap<String, NetworkInterfaceModuleVO> _interfaceModuleMap = new HashMap<String, NetworkInterfaceModuleVO>();
    private static HashMap<String, InterfaceVO> _interfaceDetailMap = new HashMap<String, InterfaceVO>();

    private static final String hKeyInterfaceCache = "NetworkInterfaceModuleCache";
    private static final String hKeyInterfaceDetailsCache = "NetworkInterfaceDetailsCache";
    private static String redisEnable = BTSLUtil.NullToString(Constants.getProperty("REDIS_ENABLE"));
    private static Gson gson = new GsonBuilder()
    		.setDateFormat("yyyy-MM-dd HH:mm:ss:mss")
    		.create();
    private static final int keyTimer = Integer.parseInt(Constants.getProperty("KEYTIMER"));
    
    private static LoadingCache<String, NetworkInterfaceModuleVO> interfaceModuleMemo = CacheBuilder.newBuilder()
    	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)
    	    .build(new CacheLoader<String, NetworkInterfaceModuleVO>(){
    			@Override
    			public NetworkInterfaceModuleVO load(String key) throws Exception {
    				return getObjectFromRedis(key);
    			}
    	     });
    private static LoadingCache<String, InterfaceVO> interfaceDetailMemo  = CacheBuilder.newBuilder()
    	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)
    	    .build(new CacheLoader<String, InterfaceVO>(){
    			@Override
    			public InterfaceVO load(String key) throws Exception {
    				return getInterfaceDetailsObjectFromRedis(key);
    			}
    	     });
    public void run() {
		try {
			Thread.sleep(50);
			loadNetworkInterfaceModuleAtStartup();
		} catch (Exception e) {
			_log.error("loadNetworkInterfaceModuleAtStartup init() Exception ", e);
		}
	}

    /**
     * Load the Network Interface Module on start up
     * @throws Exception 
     * 
     */
    public static void loadNetworkInterfaceModuleAtStartup() throws BTSLBaseException {
    	final String methodName = "loadNetworkInterfaceModuleAtStartup";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        try{
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
    	   RedisActivityLog.log("NetworkInterfaceModuleCache->loadNetworkInterfaceModuleAtStartup->Start");
    	   Jedis jedis = null;
			 try {
				 jedis = RedisConnectionPool.getPoolInstance().getResource();
				 Pipeline pipeline = jedis.pipelined();
				//If key is already present in redis db do not reload
				if(!jedis.exists(hKeyInterfaceCache)) {
					HashMap<String, NetworkInterfaceModuleVO> interfaceModule = loadMapping();
					 for (Entry<String, NetworkInterfaceModuleVO> entry : interfaceModule.entrySet())  {
						 pipeline.hset(hKeyInterfaceCache, entry.getKey(), gson.toJson(entry.getValue()));
					 }
					 pipeline.sync();
				   }
				if(!jedis.exists(hKeyInterfaceDetailsCache)) {
					HashMap<String, InterfaceVO> interfaceDetails = loadInterfaceDetail();
					 for (Entry<String, InterfaceVO> entry : interfaceDetails.entrySet())  {
						 pipeline.hset(hKeyInterfaceDetailsCache, entry.getKey(), gson.toJson(entry.getValue()));
						 }
					 pipeline.sync();
				   }
				 }catch(JedisConnectionException je){
	  			 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	 	   		        _log.errorTrace(methodName, je);
	 	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkInterfaceModuleCache[loadNetworkInterfaceModuleAtStartup]", "", "", "", "JedisConnectionException :" + je.getMessage());
	 	   		        throw new BTSLBaseException(NetworkInterfaceModuleCache.class.getName(), methodName,je.getMessage());
	 			 }catch(NoSuchElementException  ex){
	 			 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	 	   		        _log.errorTrace(methodName, ex);
	 	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkInterfaceModuleCache[loadNetworkInterfaceModuleAtStartup]", "", "", "", "NoSuchElementException :" + ex.getMessage());
	 	   		        throw new BTSLBaseException(NetworkInterfaceModuleCache.class.getName(), methodName,ex.getMessage());
	 			 }catch (Exception e) {
	 			 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
	 			 	_log.errorTrace(methodName, e);
	 	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkInterfaceModuleCache[loadNetworkInterfaceModuleAtStartup]", "", "", "", "Exception :" + e.getMessage());
	 				throw new BTSLBaseException(NetworkInterfaceModuleCache.class.getName(), methodName,e.getMessage());
	 			 }finally {
	   	        	if (jedis != null) {
	   	        	jedis.close();
	   	        	}
	   	        }
	  	   	  RedisActivityLog.log("NetworkInterfaceModuleCache->loadNetworkInterfaceModuleAtStartup->End");
		   	} else {
		   	 _interfaceModuleMap = loadMapping();
	         _interfaceDetailMap = loadInterfaceDetail();
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
        	throw new BTSLBaseException(CLASS_NAME, methodName, "");
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED);
        	}
        }
    }

    /**
     * To load the Network Interface Module details
     * 
     * @return
     *         HashMap
     *         NetworkInterfaceModuleCache
     * @throws Exception 
     */
    private static HashMap loadMapping() throws BTSLBaseException {
    	final String methodName = "loadMapping";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        NetworkDAO networkDAO = new NetworkDAO();
        HashMap map = null;
        try {
            map = networkDAO.loadNetworkInterfaceModuleCache();

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
        	throw new BTSLBaseException(CLASS_NAME, methodName, "");
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED + " MapSize " + map.size());
        	}
        }

        return map;
    }

    /**
     * To load the Network Interface Module details
     * 
     * @return
     *         HashMap
     *         NetworkInterfaceModuleCache
     * @throws Exception 
     */
    private static HashMap loadInterfaceDetail() throws BTSLBaseException {
    	final String methodName = "loadInterfaceDetail";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        InterfaceDAO intDAO = new InterfaceDAO();
        HashMap map = null;
        try {
            map = intDAO.loadInterfaceByID();

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
        	throw new BTSLBaseException(CLASS_NAME, methodName, "");
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug("loadInterfaceDetail()", PretupsI.EXITED + " MapSize " + map.size());
        	}
        }
        return map;
    }

    /**
     * to update the cache
     * 
     * void
     * @throws Exception 
     * 
     */
    public static void updateNetworkInterfaceModule() throws BTSLBaseException {
    	final String methodName = "updateNetworkInterfaceModule";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, PretupsI.ENTERED);
        }
        try{
        HashMap<String,NetworkInterfaceModuleVO> currentMap = loadMapping();
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        	Jedis jedis = null;
     	   RedisActivityLog.log("NetworkInterfaceModuleCache->updateNetworkInterfaceModule->Start");
			 try {
				 jedis = RedisConnectionPool.getPoolInstance().getResource();
				 Pipeline pipeline = jedis.pipelined();
				 HashMap<String, InterfaceVO> interfaceDetails = loadInterfaceDetail();
				 pipeline.del(hKeyInterfaceDetailsCache);
				 for (Entry<String, InterfaceVO> entry : interfaceDetails.entrySet())  {
					 pipeline.hset(hKeyInterfaceDetailsCache, entry.getKey(), gson.toJson(entry.getValue()));
					 }
				 
				 /*HashMap<String, Object> interfaceMap = RedisUtil.deserialize(jedis.hgetAll(hKeyBytes));
			        if ((interfaceMap != null) && (interfaceMap.size() > 0)) {
	    	            compareMaps(interfaceMap, currentMap);
	    	        }
	    	        */		
				 pipeline.del(hKeyInterfaceCache);
				 for (Entry<String, NetworkInterfaceModuleVO> entry : currentMap.entrySet())  {
					 pipeline.hset(hKeyInterfaceCache, entry.getKey(), gson.toJson(entry.getValue()));
					 }
				 pipeline.sync();
			 }catch(JedisConnectionException je){
			 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
 	   		        _log.errorTrace(methodName, je);
 	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkInterfaceModuleCache[updateNetworkInterfaceModule]", "", "", "", "JedisConnectionException :" + je.getMessage());
 	   		        throw new BTSLBaseException(NetworkInterfaceModuleCache.class.getName(), methodName,je.getMessage());
 			 }catch(NoSuchElementException  ex){
 			 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
 	   		        _log.errorTrace(methodName, ex);
 	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkInterfaceModuleCache[updateNetworkInterfaceModule]", "", "", "", "NoSuchElementException :" + ex.getMessage());
 	   		        throw new BTSLBaseException(NetworkInterfaceModuleCache.class.getName(), methodName,ex.getMessage());
 			 }catch (Exception e) {
 			 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
 			 	_log.errorTrace(methodName, e);
 	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkInterfaceModuleCache[updateNetworkInterfaceModule]", "", "", "", "Exception :" + e.getMessage());
 				throw new BTSLBaseException(NetworkInterfaceModuleCache.class.getName(), methodName,e.getMessage());
 			 }finally {
   	        	if (jedis != null) {
   	        	jedis.close();
   	        	}
   	        }
			 
	    RedisActivityLog.log("NetworkInterfaceModuleCache->updateNetworkInterfaceModule->End");

	   	} else {
	   		_interfaceDetailMap = loadInterfaceDetail();
	        if (_interfaceModuleMap != null && _interfaceModuleMap.size() > 0) {
	            compareMaps(_interfaceModuleMap, currentMap);
	        }
	        _interfaceModuleMap = currentMap;
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
        	throw new BTSLBaseException(CLASS_NAME, methodName, "");
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug("updateNetworkInterfaceModule()", PretupsI.EXITED + _interfaceModuleMap.size());
        	}
        }
    }

    /**
     * To get the details information of Network Interface Module
     * 
     * @param p_module
     * @param p_networkCode
     * @param p_methodType
     * @return NetworkInterfaceModuleVO
     */
    public static Object getObject(String p_module, String p_networkCode, String p_methodType) {
        if (_log.isDebugEnabled()) {
            _log.debug("getObject()", " Entered : p_module: " + p_module + " p_networkCode: " + p_networkCode + " p_methodType: " + p_methodType);
        }
        String methodName = "getObject";
        NetworkInterfaceModuleVO interfaceModuleVO = null;
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        	try {
				interfaceModuleVO = (NetworkInterfaceModuleVO)interfaceModuleMemo.get(p_module + "_" + p_networkCode + "_" + p_methodType);
			}catch (ExecutionException e) {
				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
   		        _log.errorTrace(methodName, e);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkInterfaceModuleCache[getObject]", "", "", "", "ExecutionException :" + e.getMessage());
			}
        	catch (InvalidCacheLoadException e) { 
				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
   		        _log.errorTrace(methodName, e);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkInterfaceModuleCache[getObject]", "", "", "", "ExecutionException :" + e.getMessage());	
			}
        } else {
        	interfaceModuleVO = (NetworkInterfaceModuleVO) _interfaceModuleMap.get(p_module + "_" + p_networkCode + "_" + p_methodType);
       }
        if (_log.isDebugEnabled()) {
            _log.debug("getObject()", " Exited " + interfaceModuleVO);
        }
        return interfaceModuleVO;
    }

    /**
     * To get the details information of Network Interface Module
     * 
     * @param p_interfaceID
     * @return NetworkInterfaceModuleVO
     */

    public static Object getObject(String p_interfaceID) {
        if (_log.isDebugEnabled()) {
            _log.debug("getObject()", " Entered : p_interfaceID: " + p_interfaceID);
        }
        InterfaceVO interfaceVO = null;
        String methodName ="getObject";
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        	try {
        		interfaceVO = (InterfaceVO) interfaceDetailMemo.get(p_interfaceID);
			}catch (ExecutionException e) {
				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
   		        _log.errorTrace(methodName, e);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkInterfaceModuleCache[getObject]", "", "", "", "ExecutionException :" + e.getMessage());
			}
        	catch (InvalidCacheLoadException e) { 
				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
   		        _log.errorTrace(methodName, e);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkInterfaceModuleCache[getObject]", "", "", "", "ExecutionException :" + e.getMessage());	
			}
        } else {
    	   interfaceVO = (InterfaceVO) _interfaceDetailMap.get(p_interfaceID);
       }
        if (_log.isDebugEnabled()) {
            _log.debug("getObject()", " Exited " + interfaceVO);
        }
        return interfaceVO;
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

            boolean isNewAdded = false;
            while (iterator != null && iterator.hasNext()) {
                String key = (String) iterator.next();
                NetworkInterfaceModuleVO prevMappingVO = (NetworkInterfaceModuleVO) p_previousMap.get(key);
                NetworkInterfaceModuleVO curMappingVO = (NetworkInterfaceModuleVO) p_currentMap.get(key);

                if (prevMappingVO != null && curMappingVO == null) {
                    isNewAdded = true;
                    CacheOperationLog.log("NetworkInterfaceModuleCache", BTSLUtil.formatMessage("Delete", prevMappingVO.getNetworkCode(), prevMappingVO.logInfo()));
                } else if (prevMappingVO == null && curMappingVO != null) {
                    CacheOperationLog.log("NetworkInterfaceModuleCache", BTSLUtil.formatMessage("Add", curMappingVO.getNetworkCode(), curMappingVO.logInfo()));
                }
            }

            /**
             * Note: this case arises when same number of element added and
             * deleted as well
             */
            if (p_previousMap.size() == p_currentMap.size() && isNewAdded) {
                HashMap tempMap = new HashMap(p_currentMap);
                while (copiedIterator.hasNext()) {
                    tempMap.remove((String) copiedIterator.next());
                }

                Iterator iterator2 = tempMap.keySet().iterator();
                while (iterator2.hasNext()) {
                    NetworkInterfaceModuleVO mappingVO = (NetworkInterfaceModuleVO) p_currentMap.get(iterator2.next());
                    CacheOperationLog.log("NetworkInterfaceModuleCache", BTSLUtil.formatMessage("Add", mappingVO.getNetworkCode(), mappingVO.logInfo()));
                }
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("compareMaps()", "Exited");
        }
    }
    public static NetworkInterfaceModuleVO getObjectFromRedis(String key) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("getObjectFromRedis()", " Entered : key: " + key);
        }
        NetworkInterfaceModuleVO interfaceModuleVO = null;
     	RedisActivityLog.log("NetworkInterfaceModuleCache->getObjectFromRedis->Start");
     	String methodName = "getObjectFromRedis";
     	Jedis jedis = null;
		 try {
			 jedis = RedisConnectionPool.getPoolInstance().getResource();
				// byte[] hKeyBytes = RedisUtil.serialize(hKeyInterfaceCache);
				String jasonObj =  jedis.hget(hKeyInterfaceCache, key);
				if(!BTSLUtil.isNullString(jasonObj))
				interfaceModuleVO = (NetworkInterfaceModuleVO)gson.fromJson(jasonObj,NetworkInterfaceModuleVO.class);
	      }catch(JedisConnectionException je){
	 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	        _log.errorTrace(methodName, je);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkInterfaceModuleCache[getServiceTypeObjectFromRedis]", "", "", "", "JedisConnectionException :" + je.getMessage());
	        throw new BTSLBaseException(NetworkInterfaceModuleCache.class.getName(), methodName,je.getMessage());
	     }catch(NoSuchElementException  ex){
	 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	        _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkInterfaceModuleCache[getServiceTypeObjectFromRedis]", "", "", "", "NoSuchElementException :" + ex.getMessage());
	        throw new BTSLBaseException(NetworkInterfaceModuleCache.class.getName(), methodName,ex.getMessage());
	     }catch (Exception e) {
	  	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
	 	_log.errorTrace(methodName, e);
         EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkInterfaceModuleCache[getServiceTypeObjectFromRedis]", "", "", "", "Exception :" + e.getMessage());
		throw new BTSLBaseException(NetworkInterfaceModuleCache.class.getName(), methodName,e.getMessage());
	     } finally {
		     	if (jedis != null) {
		     	jedis.close();
		     	}
		     }
	    	   RedisActivityLog.log("NetworkInterfaceModuleCache->getObject->End");
       
        if (_log.isDebugEnabled()) {
            _log.debug("getObject()", " Exited " + interfaceModuleVO);
        }
        return interfaceModuleVO;
    }
    
    public static InterfaceVO getInterfaceDetailsObjectFromRedis(String key) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("getInterfaceDetailsObjectFromRedis()", " Entered : key: " + key);
        }
     	RedisActivityLog.log("NetworkInterfaceModuleCache->getInterfaceDetailsObjectFromRedis->Start");
     	String methodName = "getInterfaceDetailsObjectFromRedis";
     	Jedis jedis = null;
     	InterfaceVO interfaceVO = null;
		 try {
			 jedis = RedisConnectionPool.getPoolInstance().getResource();
				String jasonObj =  jedis.hget(hKeyInterfaceDetailsCache, key);
				if(!BTSLUtil.isNullString(jasonObj))
					interfaceVO = (InterfaceVO)gson.fromJson(jasonObj,InterfaceVO.class);
	      }catch(JedisConnectionException je){
	 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	        _log.errorTrace(methodName, je);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkInterfaceModuleCache[getInterfaceDetailsObjectFromRedis]", "", "", "", "JedisConnectionException :" + je.getMessage());
	        throw new BTSLBaseException(NetworkInterfaceModuleCache.class.getName(), methodName,je.getMessage());
	     }catch(NoSuchElementException  ex){
	 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	        _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkInterfaceModuleCache[getInterfaceDetailsObjectFromRedis]", "", "", "", "NoSuchElementException :" + ex.getMessage());
	        throw new BTSLBaseException(NetworkInterfaceModuleCache.class.getName(), methodName,ex.getMessage());
	     }catch (Exception e) {
	  	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
	 	_log.errorTrace(methodName, e);
         EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkInterfaceModuleCache[getInterfaceDetailsObjectFromRedis]", "", "", "", "Exception :" + e.getMessage());
		throw new BTSLBaseException(NetworkInterfaceModuleCache.class.getName(), methodName,e.getMessage());
	     } finally {
		     	if (jedis != null) {
		     	jedis.close();
		     	}
		     }
	    	   RedisActivityLog.log("NetworkInterfaceModuleCache->getInterfaceDetailsObjectFromRedis->End");
       
        if (_log.isDebugEnabled()) {
            _log.debug("getInterfaceDetailsObjectFromRedis()", " Exited " + interfaceVO);
        }
        return interfaceVO;
    }
    
}
