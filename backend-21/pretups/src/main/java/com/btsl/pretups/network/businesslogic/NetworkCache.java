/**
 * @(#)NetworkCache.java
 *                       Copyright(c) 2005, Bharti Telesoft Ltd.
 *                       All Rights Reserved
 * 
 *                       <description>
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       Author Date History
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       avinash.kamthan Mar 10, 2005 Initital Creation
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 * 
 */

package com.btsl.pretups.network.businesslogic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListSorterUtil;
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

/**
 * @author avinash.kamthan
 * 
 */
public class NetworkCache {

    private static Log _log = LogFactory.getLog(NetworkCache.class.getName());
    private final static String CLASS_NAME = "NetworkCache";
    private static HashMap<String,NetworkVO> _networkMap = new HashMap<String,NetworkVO>();
    private static String hKey = "NetworkCache";
    private static String redisEnable = BTSLUtil.NullToString(Constants.getProperty("REDIS_ENABLE"));
    private static final int keyTimer = Integer.parseInt(Constants.getProperty("KEYTIMER"));
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss:mss").create();
    /**
	 * ensures no instantiation
	 */
    private NetworkCache(){
    	
    }
    
    private static LoadingCache<String,HashMap<String,NetworkVO>>  networkCacheMap = CacheBuilder.newBuilder()
	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)     
	    .build(new CacheLoader<String, HashMap<String,NetworkVO>>(){
			@Override
			public HashMap<String,NetworkVO> load(String key) throws Exception {
				return getNetworkMapFromRedis(key);
			}
	     });
    
    private static LoadingCache<String,NetworkVO>  networkCacheMapObject = CacheBuilder.newBuilder()
	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)     
	    .build(new CacheLoader<String, NetworkVO>(){
			@Override
			public NetworkVO load(String key) throws Exception {
				return getNetworkVOFromRedis(key);
			}
	     });
    
    
    public static void loadNetworkAtStartup() throws BTSLBaseException {
    	final String methodName = "loadNetworkAtStartup";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        try{
        	  if(PretupsI.REDIS_ENABLE.equals(redisEnable)){	
     	   		RedisActivityLog.log("NetworkCache->loadNetworkAtStartup->Start");
     	   		Jedis jedis = null;
      			 try  {
      				jedis = RedisConnectionPool.getPoolInstance().getResource();
      				Pipeline pipeline = jedis.pipelined();
					if(!jedis.exists(hKey)){
						HashMap<String,NetworkVO> networkCache= loadNetworks();
						for (Entry<String, NetworkVO> entry : networkCache.entrySet())  {
	               	    	 pipeline.hset(hKey,entry.getKey(),gson.toJson(entry.getValue()));
						}
						pipeline.sync();
					 }
      			 	}catch(JedisConnectionException je){
      					_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
      			        _log.errorTrace(methodName, je);
      			        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "NetworkCache[loadNetworkAtStartup]", "", "", "", "JedisConnectionException :" + je.getMessage());
      			        throw new BTSLBaseException(NetworkCache.class.getName(), methodName,je.getMessage());
      				}catch(NoSuchElementException  ex){
      					_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
      			        _log.errorTrace(methodName, ex);
      			        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "NetworkCache[loadNetworkAtStartup]", "", "", "", "NoSuchElementException :" + ex.getMessage());
      			        throw new BTSLBaseException(NetworkCache.class.getName(), methodName,ex.getMessage());
      				}catch (Exception e) {
      					_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
      					_log.errorTrace(methodName, e);
      			        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "NetworkCache[loadNetworkAtStartup]", "", "", "", "Exception :" + e.getMessage());
      					throw new BTSLBaseException(NetworkCache.class.getName(), methodName,e.getMessage());
      		    	 }finally{
						if(jedis != null)
							jedis.close();
					}
	    	   		RedisActivityLog.log("NetworkCache->loadNetworkAtStartup->End");
        	  	}else{
					 _networkMap = loadNetworks();
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
        	throw new BTSLBaseException(CLASS_NAME, methodName, "",e);
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
     * @throws Exception 
     */
    private static HashMap<String,NetworkVO> loadNetworks() throws BTSLBaseException {
    	final String methodName = "loadNetworks";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        NetworkDAO networkDAO = new NetworkDAO();
        HashMap<String,NetworkVO> map = null;
        try {
            map = networkDAO.loadNetworksCache();
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
        	throw new BTSLBaseException(CLASS_NAME, methodName, "",e);
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
     * NetworkCache
     */
    public static void updateNetwork() throws BTSLBaseException {
    	final String methodName = "updateNetwork";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        try {
	        HashMap<String,NetworkVO> currentMap = loadNetworks();
	        if(PretupsI.REDIS_ENABLE.equals(redisEnable)){
	        Jedis jedis = null;
        	 try {
        	   	  RedisActivityLog.log("NetworkCache->updateNetwork->Start");
        		  jedis = RedisConnectionPool.getPoolInstance().getResource();
        		  Pipeline pipeline = jedis.pipelined();
				  /*HashMap<String, Object> cachedMap = RedisUtil.deserialize(jedis.hgetAll(hKeyBytes));
			        if ((cachedMap != null) && (cachedMap.size() > 0)) {
	    	            compareMaps(cachedMap, currentMap);
	    	        }*/
        		    pipeline.del(hKey);
					for (Entry<String, NetworkVO> entry : currentMap.entrySet())  {
               	    	 pipeline.hset(hKey,entry.getKey(),gson.toJson(entry.getValue()));
					}
					pipeline.sync();
		    	   	RedisActivityLog.log("NetworkCache->updateNetwork->End");
	        	 }catch(JedisConnectionException je){
	     			_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	    	        _log.errorTrace(methodName, je);
	    	        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "NetworkCache[updateNetwork]", "", "", "", "JedisConnectionException :" + je.getMessage());
	    	        throw new BTSLBaseException(NetworkCache.class.getName(), methodName,je.getMessage());
	    		}catch(NoSuchElementException  ex){
	    			_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	    	        _log.errorTrace(methodName, ex);
	    	        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "NetworkCache[updateNetwork]", "", "", "", "NoSuchElementException :" + ex.getMessage());
	    	        throw new BTSLBaseException(NetworkCache.class.getName(), methodName,ex.getMessage());
	    		}catch (Exception e) {
	    			_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
	    			_log.errorTrace(methodName, e);
	    	        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "NetworkCache[updateNetwork]", "", "", "", "Exception :" + e.getMessage());
	    			throw new BTSLBaseException(NetworkCache.class.getName(), methodName,e.getMessage());
	        	 }finally{
					if(jedis != null)
						jedis.close();
				}
	        }else {
		        if (_networkMap != null && _networkMap.size() > 0) {
		            compareMaps(_networkMap, currentMap);
		        }
		        _networkMap = currentMap;
	        }
    		_log.debug(methodName+ " _networkMap ", _networkMap);
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
        	throw new BTSLBaseException(CLASS_NAME, methodName, "",e);
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED);
        		_log.debug(methodName, PretupsI.EXITED + _networkMap.size());
        	}
        }
    }

    /**
     * to get the requested objcet from the cache
     * 
     * @param p_networkCode
     * @return
     *         Object
     *         NetworkCache
     */
    public static Object getObject(String p_networkCode){

        NetworkVO networkVO = null;

        if (_log.isDebugEnabled()) {
            _log.debug("getObject()", "Entered  p_networkCode: " + p_networkCode);
        }
        if(PretupsI.REDIS_ENABLE.equals(redisEnable.trim())){
        	 try {
             	networkVO = networkCacheMapObject.get(p_networkCode);
			 }catch(ExecutionException e){
				_log.error("getObject", PretupsI.EXCEPTION + e.getMessage());
   		        _log.errorTrace("getObject", e);
		        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO,  CLASS_NAME + "[getObject]", "", "", "", "ExecutionException :" + e.getMessage());
			 }catch (InvalidCacheLoadException e) { 
   				_log.error("getObject", PretupsI.EXCEPTION + e.getMessage());
 			     _log.errorTrace("getObject", e);
	 	     }
        }
        else{
        	networkVO = (NetworkVO) _networkMap.get(p_networkCode);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("getObject()", "Exited networkVO: " + networkVO);
        }

        return networkVO;
    }

    /**
     * To load the active networks p_active should be true.
     * if we want to load all network then pass p_active should be false
     * 
     * @param p_active
     *            boolean
     * @return ArrayList
     */
    public static ArrayList loadNetworkDropDown(boolean p_active){

        if (_log.isDebugEnabled()) {
            _log.debug("loadNetworkDropDown()", "Entered  " + p_active);
        }
        final String methodName = "loadNetworkDropDown";
        ArrayList list = new ArrayList();
        if(PretupsI.REDIS_ENABLE.equals(redisEnable.trim())){
        	try {
            	_networkMap = networkCacheMap.get(hKey);
			 }catch(ExecutionException e){
				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
   		        _log.errorTrace(methodName, e);
		        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, CLASS_NAME +"[" + methodName + "]", "", "", "", "ExecutionException :" + e.getMessage());
			 }catch (InvalidCacheLoadException e) { 
   				_log.error("getObject", PretupsI.EXCEPTION + e.getMessage());
 			     _log.errorTrace("getObject", e);
		 	     }
        }
        Iterator iterator = _networkMap.keySet().iterator();
        NetworkVO networkVO = null;
        while (iterator.hasNext()) {

            networkVO = (NetworkVO) _networkMap.get(iterator.next());

            /*
             * Note:
             * to load only the active network if p_status is not "All"
             */
            if (p_active && "Y".equals(networkVO.getStatus())) {
                list.add(new ListValueVO(networkVO.getNetworkName(), networkVO.getNetworkCode()));
            } else {
                list.add(new ListValueVO(networkVO.getNetworkName(), networkVO.getNetworkCode()));
            }
        }

        ListSorterUtil listSorterUtil = new ListSorterUtil();
        try {
            list = (ArrayList) listSorterUtil.doSort("label", null, list);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }

        if (_log.isDebugEnabled()) {
            _log.debug("loadNetworkDropDown()", "Exited " + list.size());
        }
        return list;
    }

    private static void compareMaps(HashMap p_previousMap, HashMap p_currentMap) {
        if (_log.isDebugEnabled()) {
            _log.debug("compareMaps()", "Entered p_previousMap: " + p_previousMap + "  p_currentMap: " + p_currentMap);
        }

        compareMaps(p_previousMap, p_currentMap, null);

        if (_log.isDebugEnabled()) {
            _log.debug("compareMaps()", "Exited p_previousMap: " + p_previousMap + "  p_currentMap: " + p_currentMap);
        }
    }

    /**
     * compare two hashmap and check which have changed and log the value which
     * has been changed
     * 
     * @param p_previousMap
     * @param p_currentMap
     *            void
     */
    private static void compareMaps(HashMap p_previousMap, HashMap p_currentMap, ArrayList p_list) {

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
                NetworkVO prevNetworkVO = (NetworkVO) p_previousMap.get(key);
                NetworkVO curNetworkVO = (NetworkVO) p_currentMap.get(key);

                if (prevNetworkVO != null && curNetworkVO == null) {
                    // network status has been changed
                    // less no of rows in current than previous
                    // System.out.println(BTSLUtil.formatMessage("Delete",prevNetworkVO.getNetworkCode(),prevNetworkVO.logInfo()));
                    isNewAdded = true;
                    CacheOperationLog.log("NetworkCache", BTSLUtil.formatMessage(PretupsI.CACHE_ACTION_DELETE, prevNetworkVO.getNetworkCode(), prevNetworkVO.logInfo()));
                } else if (prevNetworkVO == null && curNetworkVO != null) {
                    // new network added
                    // System.out.println(BTSLUtil.formatMessage("Add",curNetworkVO.getNetworkCode(),curNetworkVO.logInfo()));
                    CacheOperationLog.log("NetworkCache", BTSLUtil.formatMessage(PretupsI.CACHE_ACTION_ADD, curNetworkVO.getNetworkCode(), curNetworkVO.logInfo()));
                } else if (prevNetworkVO != null && curNetworkVO != null) {
                    if (!curNetworkVO.equalsNetworkVO(prevNetworkVO)) {
                        // System.out.println(BTSLUtil.formatMessage("Modify",curNetworkVO.getNetworkCode(),curNetworkVO.differnces(prevNetworkVO)));
                        CacheOperationLog.log("NetworkCache", BTSLUtil.formatMessage(PretupsI.CACHE_ACTION_MODIFY, curNetworkVO.getNetworkCode(), curNetworkVO.differnces(prevNetworkVO)));
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
                NetworkVO networkVO = null;
                while (iterator2.hasNext()) {
                    // new network added
                    networkVO = (NetworkVO) p_currentMap.get(iterator2.next());
                    CacheOperationLog.log("NetworkCache", BTSLUtil.formatMessage(PretupsI.CACHE_ACTION_ADD, networkVO.getNetworkCode(), networkVO.logInfo()));
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
     * This method returns the HashMap of networkMap
     * 
     * @author ashishk
     */
    public static HashMap<String,NetworkVO> getNetworkMap() {
    	String methodName="getNetworkMap";
    	  if(PretupsI.REDIS_ENABLE.equals(redisEnable.trim())){
          	try {
              	_networkMap = networkCacheMap.get(hKey);
  			 }catch(ExecutionException e){
  				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
     		        _log.errorTrace(methodName, e);
  		        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, CLASS_NAME +"[" + methodName + "]", "", "", "", "ExecutionException :" + e.getMessage());
  			 }catch (InvalidCacheLoadException e) { 
     				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
   			     _log.errorTrace(methodName, e);
  		 	     }
          }
        return _networkMap;
    }

    /**
     * This method returns the networkVO based on the external network code
     * passed.
     * 
     * @param String
     *            p_extNetworkCode - external network code
     * @return Object
     */
    public static NetworkVO getNetworkByExtNetworkCode(String p_extNetworkCode){
        if (_log.isDebugEnabled()) {
            _log.debug("getNetworkByExtNetworkCode()", "Entered  p_extNetworkCode: " + p_extNetworkCode);
        }
        NetworkVO networkVO = null;
        boolean isFound = false;
        p_extNetworkCode = BTSLUtil.NullToString(p_extNetworkCode).trim();
        for(Entry<String,NetworkVO> entry:getNetworkMap().entrySet()){
            networkVO = entry.getValue();
        	if (p_extNetworkCode.equals(networkVO.getErpNetworkCode())) {
                isFound = true;
                break;
            }	
        }
        if (isFound) {
            if (_log.isDebugEnabled()) {
                _log.debug("getNetworkByExtNetworkCode()", "Exiting....  : networkVO " + networkVO);
            }
            return networkVO;
        }
        if (_log.isDebugEnabled()) {
            _log.debug("getNetworkByExtNetworkCode()", "Exiting....  : networkVO =null");
        }
        return null;
    }

    /**
     * @param key
     * @return
     */
    public static HashMap<String,NetworkVO> getNetworkMapFromRedis(String key) {
        String methodName= "getNetworkMapFromRedis";
    	if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered key: "+key);
        }
        Jedis jedis = null;
 		 HashMap<String, NetworkVO> cachedMap = null;
		 try {
      	     RedisActivityLog.log("NetworkCache->getNetworkMapFromRedis->Start");
      		 jedis = RedisConnectionPool.getPoolInstance().getResource();
			 Map<String, String> jsonMap = jedis.hgetAll(key);
			 NetworkVO networkVO = new NetworkVO();
			 cachedMap = new HashMap<String, NetworkVO>();
			 for (Entry<String, String> entry : jsonMap.entrySet())  {
				  networkVO = gson.fromJson(entry.getValue(), NetworkVO.class);
				  cachedMap.put(entry.getKey(),networkVO );
			 }
      	     RedisActivityLog.log("NetworkCache->getNetworkMapFromRedis->End");
		 }catch(JedisConnectionException je){
			_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	        _log.errorTrace(methodName, je);
	        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "NetworkCache[getNetworkMapFromRedis]", "", "", "", "JedisConnectionException :" + je.getMessage());
		}catch(NoSuchElementException  ex){
			_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	        _log.errorTrace(methodName, ex);
	        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "NetworkCache[getNetworkMapFromRedis]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		}catch (Exception e) {
			_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
			_log.errorTrace(methodName, e);
	        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "NetworkCache[getNetworkMapFromRedis]", "", "", "", "Exception :" + e.getMessage());
    	 }
    	 finally {
    		 if(jedis != null ) {
    			 jedis.close();
    		 }
    		 if (_log.isDebugEnabled()) {
 	            _log.debug(methodName, "Exited cachedMap.size(): " + cachedMap.size());
 	        }
    	   }
		 return cachedMap;
		 }

    /**
     * @param key
     * @return
     */
    public static NetworkVO getNetworkVOFromRedis(String key) {
       String methodName= "getNetworkVOFromRedis";
    	if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered key: "+key);
        }
        NetworkVO networkVO = null;
        Jedis jedis = null;
		 try {
	        RedisActivityLog.log("NetworkCache->getNetworkVOFromRedis->Start");
			jedis = RedisConnectionPool.getPoolInstance().getResource();
			String json = jedis.hget(hKey,key);
			if(json != null)
				networkVO=gson.fromJson(json, NetworkVO.class);
       	   	RedisActivityLog.log("NetworkCache->getNetworkVOFromRedis->End");
		 }catch(JedisConnectionException je){
				_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
		        _log.errorTrace(methodName, je);
		        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "NetworkCache[getNetworkVOFromRedis]", "", "", "", "JedisConnectionException :" + je.getMessage());
			}catch(NoSuchElementException  ex){
				_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
		        _log.errorTrace(methodName, ex);
		        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "NetworkCache[getNetworkVOFromRedis]", "", "", "", "NoSuchElementException :" + ex.getMessage());
			}catch (Exception e) {
				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
				_log.errorTrace(methodName, e);
		        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "NetworkCache[getNetworkVOFromRedis]", "", "", "", "Exception :" + e.getMessage());
	    	 }finally {
    		 if(jedis != null ) {
    			 jedis.close();
    		 }
    		 if (_log.isDebugEnabled()) {
    	            _log.debug(methodName, "Exited networkVO: " + networkVO);
    	        }
    	   }
		 return networkVO;
		 }

}
