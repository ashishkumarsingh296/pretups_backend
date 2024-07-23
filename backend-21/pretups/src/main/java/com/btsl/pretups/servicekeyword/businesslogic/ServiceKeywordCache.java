/**
 * @(#)ServiceKeywordCache.java
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
 *                              avinash.kamthan Mar 16, 2005 Initital Creation
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 * 
 */
package com.btsl.pretups.servicekeyword.businesslogic;

import java.util.ArrayList;
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
import com.btsl.pretups.receiver.RequestVO;
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
public class ServiceKeywordCache implements Runnable  {
    private static Log _log = LogFactory.getLog(ServiceKeywordCache.class.getName());

    private static HashMap<String,ServiceKeywordCacheVO> _serviceKeywordMap = new HashMap<String,ServiceKeywordCacheVO>();
    private static HashMap<String,ServiceKeywordCacheVO> _serviceTypeMap = new HashMap<String,ServiceKeywordCacheVO>();
    private static HashMap<String,WebServiceKeywordCacheVO> _webServiceTypeMap= new HashMap<String,WebServiceKeywordCacheVO>();
    private static String redisEnable = BTSLUtil.NullToString(Constants.getProperty("REDIS_ENABLE"));
    private static final String hKeyServiceKeyWord = "ServiceKeywordCache";
    private static final String hKeyWebServiceType = "WebServiceTypeCache";
    private static final String hKeyServiceType = "ServiceTypeCache";
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss:mss").create();
    private static final int keyTimer = Integer.parseInt(Constants.getProperty("KEYTIMER"));
    
    private static LoadingCache<String,ServiceKeywordCacheVO> serviceKeywordMemo = CacheBuilder.newBuilder()
	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)
	    .build(new CacheLoader<String,ServiceKeywordCacheVO>(){
			@Override
			public ServiceKeywordCacheVO load(String key) throws Exception {
				return getServiceKeywordObjFromRedis(key);
			}
	     });
    private static LoadingCache<String,ServiceKeywordCacheVO> serviceTypeMemo = CacheBuilder.newBuilder()
	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)
	    .build(new CacheLoader<String,ServiceKeywordCacheVO>(){
			@Override
			public ServiceKeywordCacheVO load(String key) throws Exception {
				return getServiceTypeObjectFromRedis(key);
			}
	     });
    private static LoadingCache<String,WebServiceKeywordCacheVO> webServiceTypeMemo = CacheBuilder.newBuilder()
	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)
	    .build(new CacheLoader<String,WebServiceKeywordCacheVO>(){
			@Override
			public WebServiceKeywordCacheVO load(String key) throws Exception {
				return getWebServiceTypeObjectFromRedis(key);
			}
	     });
    private static LoadingCache<String,HashMap<String,ServiceKeywordCacheVO>> serviceKeywordMemoMap = CacheBuilder.newBuilder()
	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)
	    .build(new CacheLoader<String,HashMap<String,ServiceKeywordCacheVO>>(){
			@Override
			public HashMap<String,ServiceKeywordCacheVO> load(String key) throws Exception {
				return getServiceKeywordMapFromRedis(key);
			}
	     });

    public void run() {
        try {
            Thread.sleep(50);
            loadServiceKeywordCacheOnStartUp();
        } catch (Exception e) {
        	 _log.error("ServiceKeywordCache init() Exception ", e);
        }
    }
    /**
     * To Load all prefrences on startup
     * Preferences like System level,Network Level,Zone Level,Service Level
     * 
     * void
     * ServiceKeywordCache
     * @throws Exception 
     */
    public static void loadServiceKeywordCacheOnStartUp() throws BTSLBaseException {
    	final String methodName = "loadServiceKeywordCacheOnStartUp";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
	        try{
	        	if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
	          	   	 RedisActivityLog.log("ServiceKeywordCache->loadServiceKeywordCacheOnStartUp->Start");
	          	   Jedis jedis  = null;
	        		 try{
	        			 jedis  = RedisConnectionPool.getPoolInstance().getResource();
	        			 Pipeline pipeline = jedis.pipelined();
	     				if(!jedis.exists(hKeyServiceKeyWord)) {
	     				 for (Entry<String,ServiceKeywordCacheVO> entry : loadServiceKeyword().entrySet())  {
	     					pipeline.hset(hKeyServiceKeyWord, entry.getKey(), gson.toJson(entry.getValue()));
	     				  } 
	     				pipeline.sync();
	     				}
	     				
	        				 for (Entry<String,WebServiceKeywordCacheVO> entry : loadWebServiceTypes().entrySet())  {
	        					 pipeline.hset(hKeyWebServiceType, entry.getKey(), gson.toJson(entry.getValue()));
	        				  } 
	        				 pipeline.sync();
	     				
	     				if(!jedis.exists(hKeyServiceType)) {
	        				 for (Entry<String,ServiceKeywordCacheVO> entry : loadServiceTypes().entrySet())  {
	        					 pipeline.hset(hKeyServiceType, entry.getKey(), gson.toJson(entry.getValue()));
	        				  } 
	        				 pipeline.sync();
	        				}
	        		 }catch(JedisConnectionException je){
	  			 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	 	   		        _log.errorTrace(methodName, je);
	 	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordCache[loadServiceKeywordCacheOnStartUp]", "", "", "", "JedisConnectionException :" + je.getMessage());
	 	   		        throw new BTSLBaseException(ServiceKeywordCache.class.getName(), methodName,je.getMessage());
		 			 }catch(NoSuchElementException  ex){
		 			 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
		 	   		        _log.errorTrace(methodName, ex);
		 	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordCache[loadServiceKeywordCacheOnStartUp]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		 	   		        throw new BTSLBaseException(ServiceKeywordCache.class.getName(), methodName,ex.getMessage());
		 			 }catch (Exception e) {
		 			 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
		 			 	_log.errorTrace(methodName, e);
		 	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordCache[loadServiceKeywordCacheOnStartUp]", "", "", "", "Exception :" + e.getMessage());
		 				throw new BTSLBaseException(ServiceKeywordCache.class.getName(), methodName,e.getMessage());
		 			 }finally {
		   	        	if (jedis != null) {
		   	        	jedis.close();
		   	        	}
		   	        }
		           	   	 RedisActivityLog.log("ServiceKeywordCache->loadServiceKeywordCacheOnStartUp->End");
		       } else {
				        _serviceKeywordMap = loadServiceKeyword();
				        _serviceTypeMap = loadServiceTypes();
				        _webServiceTypeMap=loadWebServiceTypes();
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
        		_log.debug(methodName, PretupsI.EXITED);
        	}
        }
    }

    /**
     * To load the Preferences
     * 
     * @return
     *         HashMap
     *         ServiceKeywordCache
     * @throws Exception 
     */
    private static HashMap<String,ServiceKeywordCacheVO> loadServiceKeyword() throws BTSLBaseException {
    	final String methodName = "loadServiceKeyword";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        ServiceKeywordDAO serviceDAO = new ServiceKeywordDAO();
        HashMap<String,ServiceKeywordCacheVO> map = null;

        try {
            map = serviceDAO.loadServiceCache();
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
        		_log.debug(methodName, PretupsI.EXITED + map.size());
        	}
        }

        return map;
    }
	private static HashMap<String,WebServiceKeywordCacheVO> loadWebServiceTypes()
    {
    	 if (_log.isDebugEnabled())_log.debug("loadWebServiceTypes()", "Entered");
         final String METHOD_NAME = "loadWebServiceTypes";
         ServiceKeywordDAO serviceDAO = new ServiceKeywordDAO();
         HashMap<String,WebServiceKeywordCacheVO> map = null;

         try
         {
             map = serviceDAO.loadWebServiceTypeCache();
         }
         catch (Exception e)
         {
             _log.error("loadWebServiceTypes() ", "Exception e: "+e.getMessage());
             _log.errorTrace(METHOD_NAME, e);
         }

         if (_log.isDebugEnabled()) _log.debug("loadWebServiceTypes()", "Exited " + map.size());
         
         return map;
    }

    /**
     * To load the Preferences
     * 
     * @return
     *         HashMap
     *         ServiceKeywordCache
     */
    private static HashMap<String,ServiceKeywordCacheVO> loadServiceTypes() {
        if (_log.isDebugEnabled()) {
            _log.debug("loadServiceTypes()", "Entered");
        }
        final String METHOD_NAME = "loadServiceTypes";
        ServiceKeywordDAO serviceDAO = new ServiceKeywordDAO();
        HashMap<String,ServiceKeywordCacheVO> map = null;

        try {
            map = serviceDAO.loadServiceTypeCache();
        } catch (Exception e) {
            _log.error("loadServiceTypes() ", "Exception e: " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
        }

        if (_log.isDebugEnabled()) {
            _log.debug("loadServiceTypes()", "Exited " + map.size());
        }

        return map;
    }

    /**
     * To update the service keyword cache
     * 
     * void
     * ServiceKeywordCache
     * @throws Exception 
     */
    public static void updateServiceKeywords() throws BTSLBaseException {
    	final String methodName = "updateServiceKeywords";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        try {
        HashMap<String,ServiceKeywordCacheVO> currentMap = loadServiceKeyword();
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
      	RedisActivityLog.log("ServiceKeywordCache->updateServiceKeywords->Start");
      	Jedis jedis = null;
   		 try {
   			 jedis =   RedisConnectionPool.getPoolInstance().getResource();
   			Pipeline pipeline = jedis.pipelined();
   			/*
	        HashMap<String, Object> cachedMap = RedisUtil.deserialize(jedis.hgetAll(hKeyBytes));
	        if ((cachedMap != null) && (cachedMap.size() > 0)) {
	            compareMaps(cachedMap, currentMap);
	        }*/
   			pipeline.del(hKeyServiceKeyWord);
			for (Entry<String, ServiceKeywordCacheVO> entry : currentMap.entrySet())  {
		      pipeline.hset(hKeyServiceKeyWord, entry.getKey(), gson.toJson(entry.getValue()));
			} 
			pipeline.del(hKeyServiceType);
			HashMap<String, ServiceKeywordCacheVO> currentServiceTypeMap = loadServiceTypes();
			for (Entry<String, ServiceKeywordCacheVO> entry : currentServiceTypeMap.entrySet())  {
			    pipeline.hset(hKeyServiceType, entry.getKey(),gson.toJson(entry.getValue()));
			} 
			pipeline.sync();
   		  }catch(JedisConnectionException je){
		 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	   		        _log.errorTrace(methodName, je);
	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordCache[updateServiceKeywords]", "", "", "", "JedisConnectionException :" + je.getMessage());
	   		        throw new BTSLBaseException(ServiceKeywordCache.class.getName(), methodName,je.getMessage());
			 }catch(NoSuchElementException  ex){
			 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	   		        _log.errorTrace(methodName, ex);
	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordCache[updateServiceKeywords]", "", "", "", "NoSuchElementException :" + ex.getMessage());
	   		        throw new BTSLBaseException(ServiceKeywordCache.class.getName(), methodName,ex.getMessage());
			 }catch (Exception e) {
			 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
			 	_log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordCache[updateServiceKeywords]", "", "", "", "Exception :" + e.getMessage());
				throw new BTSLBaseException(ServiceKeywordCache.class.getName(), methodName,e.getMessage());
			 }finally {
	        	if (jedis != null) {
	        	jedis.close();
	        	}
	        }
   	   	 RedisActivityLog.log("ServiceKeywordCache->updateServiceKeywords->End");
	    }  else  {
	        if ((_serviceKeywordMap != null) && (_serviceKeywordMap.size() > 0)) {
	            compareMaps(_serviceKeywordMap, currentMap);
	        }
	        _serviceKeywordMap = currentMap;
	        _serviceTypeMap = loadServiceTypes();
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
        		_log.debug("updateData()", PretupsI.EXITED + _serviceKeywordMap.size());
        	}
        }
    }

    /**
     * 
     * it returns the serviceKeywordVo agaubct the passed arguments
     * 
     * @param p_keyword
     * @param p_module
     * @param p_requestInterfaceType
     * @param p_servicePort
     * @return ServiceKeywordCacheVO
     */
    public static ServiceKeywordCacheVO getServiceKeywordObj(String p_keyword, String p_module, String p_requestInterfaceType, String p_servicePort) {

        if (_log.isDebugEnabled()) {
            _log.debug("getServiceKeywordObj()", "Entered p_keyword: " + p_keyword + " p_module: " + p_module + " p_requestInterfaceType: " + p_requestInterfaceType + " p_servicePort: " + p_servicePort);
        }
        String methodName = "getServiceKeywordObj";
        ServiceKeywordCacheVO serviceKeywordCacheVO = null;

        String key = p_keyword + "_" + p_module + "_" + p_requestInterfaceType + "_" + p_servicePort;

        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        	try {
				serviceKeywordCacheVO = (ServiceKeywordCacheVO)serviceKeywordMemo.get(key);
			}catch (ExecutionException e) {
				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
   		        _log.errorTrace(methodName, e);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordCache[getServiceKeywordObj]", "", "", "", "ExecutionException :" + e.getMessage());
			}
        	catch (InvalidCacheLoadException e) { 
				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
   		        _log.errorTrace(methodName, e);
			}
        } else {
    		serviceKeywordCacheVO = (ServiceKeywordCacheVO) _serviceKeywordMap.get(key);
        }

        if (_log.isDebugEnabled()) {
            _log.debug("getServiceKeywordObj()", "Exiting serviceKeywordCacheVO: " + serviceKeywordCacheVO);
        }

        return serviceKeywordCacheVO;
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
        if (_log.isDebugEnabled()) {
            _log.debug("compareMaps()", "Entered p_previousMap: " + p_previousMap + "  p_currentMap: " + p_currentMap);
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

            // to check whether any new network added or not but size of
            boolean isNewAdded = false;

            while (iterator != null && iterator.hasNext()) {
                String key = (String) iterator.next();
                ServiceKeywordCacheVO prevkeywordCacheVO = (ServiceKeywordCacheVO) p_previousMap.get(key);
                ServiceKeywordCacheVO curkeywordCacheVO = (ServiceKeywordCacheVO) p_currentMap.get(key);

                if ((prevkeywordCacheVO != null) && (curkeywordCacheVO == null)) {
                    isNewAdded = true;
                    CacheOperationLog.log("ServiceKeywordCache", BTSLUtil.formatMessage("Delete", prevkeywordCacheVO.getServiceTypeKeyword(), prevkeywordCacheVO.logInfo()));
                } else if ((prevkeywordCacheVO == null) && (curkeywordCacheVO != null)) {
                    CacheOperationLog.log("ServiceKeywordCache", BTSLUtil.formatMessage("Add", curkeywordCacheVO.getServiceTypeKeyword(), curkeywordCacheVO.logInfo()));
                } else if ((prevkeywordCacheVO != null) && (curkeywordCacheVO != null)) {
                    if (!curkeywordCacheVO.equalsServiceKeywordCacheVO(prevkeywordCacheVO)) {
                        CacheOperationLog.log("ServiceKeywordCache", BTSLUtil.formatMessage("Modify", curkeywordCacheVO.getServiceTypeKeyword(), curkeywordCacheVO.differences(prevkeywordCacheVO)));
                    }
                }
            }

            /**
             * Note: this case arises when same number of network added and
             * deleted as well
             */
            if ((p_previousMap.size() == p_currentMap.size()) && isNewAdded) {
                HashMap tempMap = new HashMap(p_currentMap);

                while (copiedIterator.hasNext()) {
                    tempMap.remove((String) copiedIterator.next());
                }

                Iterator iterator2 = tempMap.keySet().iterator();

                while (iterator2.hasNext()) {
                    // new added
                    ServiceKeywordCacheVO keywordCacheVO = (ServiceKeywordCacheVO) p_currentMap.get(iterator2.next());
                    CacheOperationLog.log("ServiceKeywordCache", BTSLUtil.formatMessage("Add", keywordCacheVO.getServiceTypeKeyword(), keywordCacheVO.logInfo()));
                }
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("compareMaps()", "Exited");
        }
    }

    public static HashMap getServiceKeywordMap() {
    	String methodName = "getServiceKeywordMap";
    	if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
      	   	 RedisActivityLog.log("ServiceKeywordCache->getServiceKeywordMap->Start");
      	try (Jedis jedis = RedisConnectionPool.getPoolInstance().getResource()) {
      		try {
      			_serviceKeywordMap = serviceKeywordMemoMap.get(hKeyServiceKeyWord);
			} catch (ExecutionException e) {
				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
   		        _log.errorTrace(methodName, e);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordCache[getServiceKeywordMap]", "", "", "", "ExecutionException :" + e.getMessage());
			}
        	catch (InvalidCacheLoadException e) { 
				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
   		        _log.errorTrace(methodName, e);
			}
      	}
    	}	 
        return _serviceKeywordMap;
    }

    /**
     * Method to load the service keywod Type based on cache
     * 
     * @param p_requestVO
     * @return ServiceKeywordCacheVO
     *         CR 000009 Sub Keyword Logic and removing the sub keyword from
     *         request message
     */
    public static ServiceKeywordCacheVO getServiceKeywordObj(RequestVO p_requestVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("getServiceKeywordObj()", "Entered p_requestVO: ");
        }
        ServiceKeywordCacheVO serviceKeywordCacheVO = null;
        String methodName = "getServiceKeywordObj";
        String key = p_requestVO.getRequestMessageArray()[0].toUpperCase() + "_" + p_requestVO.getModule() + "_" + p_requestVO.getRequestGatewayType() + "_" + p_requestVO.getServicePort();
        if (_log.isDebugEnabled()) {
            _log.debug("getServiceKeywordObj()", "key=" + key);
        }
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        	try {
				serviceKeywordCacheVO = (ServiceKeywordCacheVO)serviceKeywordMemo.get(key);
			}catch (ExecutionException e) {
				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
   		        _log.errorTrace(methodName, e);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordCache[getServiceKeywordObj]", "", "", "", "ExecutionException :" + e.getMessage());
			}
        	catch (InvalidCacheLoadException e) { 
				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
   		        _log.errorTrace(methodName, e);
			}
        } else {
    		serviceKeywordCacheVO = (ServiceKeywordCacheVO) _serviceKeywordMap.get(key);
        }
        if (serviceKeywordCacheVO != null) {
            ArrayList subKeywordList = serviceKeywordCacheVO.getSubKeywordList();
            if (subKeywordList != null && !subKeywordList.isEmpty()) {

                int size = subKeywordList.size();
                if (_log.isDebugEnabled()) {
                    _log.debug("getServiceKeywordObj()", "Sub Keyword =" + p_requestVO.getRequestMessageArray()[1].toUpperCase());
                }
                serviceKeywordCacheVO = null;
                for (int i = 0; i < size; i++) {
                    serviceKeywordCacheVO = (ServiceKeywordCacheVO) subKeywordList.get(i);
                    if (serviceKeywordCacheVO.getKeyword().equals(p_requestVO.getRequestMessageArray()[0].toUpperCase()) && serviceKeywordCacheVO.getSubKeyword().equals(p_requestVO.getRequestMessageArray()[1].toUpperCase()) && serviceKeywordCacheVO.getModule().equals(p_requestVO.getModule()) && serviceKeywordCacheVO.getRequestInterfaceType().equals(p_requestVO.getRequestGatewayType()) && serviceKeywordCacheVO.getServerPort().equals(p_requestVO.getServicePort())) {
                        break;
                    }
                    serviceKeywordCacheVO = null;
                }
                if (serviceKeywordCacheVO != null) {
                    String[] source = p_requestVO.getRequestMessageArray();
                    String[] target = new String[source.length - 1];
                    target[0] = serviceKeywordCacheVO.getServiceType();
                    System.arraycopy(source, 2, target, 1, source.length - 2);
                    p_requestVO.setRequestMessageArray(target);
                }
            } else if (serviceKeywordCacheVO.isSubKeywordApplicable()) {
                if (_log.isDebugEnabled()) {
                    _log.debug("getServiceKeywordObj()", "Sub Keyword =" + p_requestVO.getRequestMessageArray()[1].toUpperCase());
                }
                if (serviceKeywordCacheVO.getKeyword().equals(p_requestVO.getRequestMessageArray()[0].toUpperCase()) && serviceKeywordCacheVO.getSubKeyword().equals(p_requestVO.getRequestMessageArray()[1].toUpperCase()) && serviceKeywordCacheVO.getModule().equals(p_requestVO.getModule()) && serviceKeywordCacheVO.getRequestInterfaceType().equals(p_requestVO.getRequestGatewayType()) && serviceKeywordCacheVO.getServerPort().equals(p_requestVO.getServicePort())) {
                    String[] source = p_requestVO.getRequestMessageArray();
                    String[] target = new String[source.length - 1];
                    target[0] = serviceKeywordCacheVO.getServiceType();
                    System.arraycopy(source, 2, target, 1, source.length - 2);
                    p_requestVO.setRequestMessageArray(target);
                } else {
                    serviceKeywordCacheVO = null;
                }
            }else{
            	
                 /** if (serviceKeywordCacheVO.getKeyword().equals(p_requestVO.getRequestMessageArray()[0].toUpperCase())&& serviceKeywordCacheVO.getModule().equals(p_requestVO.getModule()) && serviceKeywordCacheVO.getRequestInterfaceType().equals(p_requestVO.getRequestGatewayType()) && serviceKeywordCacheVO.getServerPort().equals(p_requestVO.getServicePort())) {
                      String[] source = p_requestVO.getRequestMessageArray();
                      //String[] target = new String[source.length - 1];
                      //target[0] = serviceKeywordCacheVO.getServiceType();
                      source[0] = serviceKeywordCacheVO.getServiceType();
                      //System.arraycopy(source, 2, target, 1, source.length );
                      p_requestVO.setRequestMessageArray(source);
                  } else {
                      serviceKeywordCacheVO = null;
                  }*/
            }
        }

        if (_log.isDebugEnabled()) {
            _log.debug("getServiceKeywordObj()", "Exiting serviceKeywordCacheVO: " + serviceKeywordCacheVO);
        }
        return serviceKeywordCacheVO;
    }

    /**
     * Method to get the service type object
     * 
     * @param p_serviceTypes
     * @param p_module
     * @return
     */
    public static ServiceKeywordCacheVO getServiceTypeObject(String p_serviceTypes, String p_module) {
        if (_log.isDebugEnabled()) {
            _log.debug("getServiceTypeObject()", "Entered p_serviceTypes: " + p_serviceTypes + " p_module:" + p_module);
        }
        ServiceKeywordCacheVO serviceKeywordCacheVO = null;
        String key = p_serviceTypes + "_" + p_module;
        String methodName = "getServiceTypeObject";
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        	try {
				serviceKeywordCacheVO = (ServiceKeywordCacheVO)serviceTypeMemo.get(key);
			}catch (ExecutionException e) {
				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
   		        _log.errorTrace(methodName, e);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordCache[getServiceTypeObject]", "", "", "", "ExecutionException :" + e.getMessage());
			}
        	catch (InvalidCacheLoadException e) { 
				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
   		        _log.errorTrace(methodName, e);
			}
        } else {
    		 serviceKeywordCacheVO = (ServiceKeywordCacheVO) _serviceTypeMap.get(key);
        }
       
        if (_log.isDebugEnabled()) {
            _log.debug("getServiceTypeObject()", "Exiting serviceKeywordCacheVO: " + serviceKeywordCacheVO);
        }
        return serviceKeywordCacheVO;
    }
    public static WebServiceKeywordCacheVO getWebServiceTypeObject(String p_serviceTypes)
	{
		System.out.println("-----_webServiceTypeMap--1----"+p_serviceTypes);
		System.out.println("--------------------_webServiceTypeMap-----------------"+_webServiceTypeMap.toString());
        if (_log.isDebugEnabled())_log.debug("getWebServiceTypeObject()", "Entered p_serviceTypes: "+p_serviceTypes);
        WebServiceKeywordCacheVO serviceKeywordCacheVO = null;
        String methodName = "getWebServiceTypeObject";
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        	try {
        		 serviceKeywordCacheVO = (WebServiceKeywordCacheVO) webServiceTypeMemo.get(p_serviceTypes);
			}catch (ExecutionException e) {
				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
   		        _log.errorTrace(methodName, e);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordCache[getWebServiceTypeObject]", "", "", "", "ExecutionException :" + e.getMessage());
			}
        	catch (InvalidCacheLoadException e) { 
				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
   		        _log.errorTrace(methodName, e);
			}
        } else {
    		   serviceKeywordCacheVO = (WebServiceKeywordCacheVO) _webServiceTypeMap.get(p_serviceTypes);
        }
     
        if (_log.isDebugEnabled()) _log.debug("getWebServiceTypeObject()", "Exiting WebServiceTypeObject: "+serviceKeywordCacheVO);
        return serviceKeywordCacheVO;
	}
    public static ServiceKeywordCacheVO getServiceKeywordObjFromRedis(String key) throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("getServiceKeywordObjFromRedis()", "Entered key: " + key );
        }
        String methodName = "getServiceKeywordObjFromRedis";
        ServiceKeywordCacheVO serviceKeywordCacheVO = null; 
      	RedisActivityLog.log("ServiceKeywordCache->getServiceKeywordObjFromRedis->Start");	
      	Jedis jedis = null;
			 try {
				 jedis = RedisConnectionPool.getPoolInstance().getResource();
				 String serviceKeyObj = jedis.hget(hKeyServiceKeyWord, key) ;
				if(!BTSLUtil.isNullString(serviceKeyObj))
			    serviceKeywordCacheVO = (ServiceKeywordCacheVO)gson.fromJson(serviceKeyObj,ServiceKeywordCacheVO.class);
			 }catch(JedisConnectionException je){
			 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	   		        _log.errorTrace(methodName, je);
	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordCache[getPreferenceObjectFromRedis]", "", "", "", "JedisConnectionException :" + je.getMessage());
	   		        throw new BTSLBaseException(ServiceKeywordCache.class.getName(), methodName,je.getMessage());
			 }catch(NoSuchElementException  ex){
			 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	   		        _log.errorTrace(methodName, ex);
	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordCache[getPreferenceObjectFromRedis]", "", "", "", "NoSuchElementException :" + ex.getMessage());
	   		        throw new BTSLBaseException(ServiceKeywordCache.class.getName(), methodName,ex.getMessage());
			 }catch (Exception e) {
			 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
			 	_log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordCache[getPreferenceObjectFromRedis]", "", "", "", "Exception :" + e.getMessage());
				throw new BTSLBaseException(ServiceKeywordCache.class.getName(), methodName,e.getMessage());
			 }
			 finally {
		        	if (jedis != null) {
		        	jedis.close();
		        	}
		        }
       	   	 RedisActivityLog.log("ServiceKeywordCache->getServiceKeywordObjFromRedis->End");

        return serviceKeywordCacheVO;
    }
    public static WebServiceKeywordCacheVO getWebServiceTypeObjectFromRedis(String key) throws BTSLBaseException
   	{
   		
           if (_log.isDebugEnabled())_log.debug("getWebServiceTypeObjectFromRedis()", "Entered p_serviceTypes: "+key);
           WebServiceKeywordCacheVO serviceKeywordCacheVO = null;
           String methodName = "getWebServiceTypeObjectFromRedis";
           RedisActivityLog.log("ServiceKeywordCache->getWebServiceTypeObjectFromRedis->Start");
           Jedis jedis = null;
			 try {
				 jedis = RedisConnectionPool.getPoolInstance().getResource();
				 String webServiceTypeObj = jedis.hget(hKeyWebServiceType, key);
   				if(!BTSLUtil.isNullString(webServiceTypeObj))
   			    serviceKeywordCacheVO = (WebServiceKeywordCacheVO)gson.fromJson(webServiceTypeObj,WebServiceKeywordCacheVO.class);
   			 }catch(JedisConnectionException je){
			 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	   		        _log.errorTrace(methodName, je);
	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordCache[getWebServiceTypeObjectFromRedis]", "", "", "", "JedisConnectionException :" + je.getMessage());
			 }catch(NoSuchElementException  ex){
			 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	   		        _log.errorTrace(methodName, ex);
	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordCache[getWebServiceTypeObjectFromRedis]", "", "", "", "NoSuchElementException :" + ex.getMessage());
			 }catch (Exception e) {
			 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
			 	_log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordCache[getWebServiceTypeObjectFromRedis]", "", "", "", "Exception :" + e.getMessage());
			 }
			 finally {
		        	if (jedis != null) {
		        	jedis.close();
		        	}
		        }
          	   	 RedisActivityLog.log("ServiceKeywordCache->getWebServiceTypeObjectFromRedis->End");
       	
           if (_log.isDebugEnabled()) _log.debug("getWebServiceTypeObjectFromRedis()", "Exiting serviceKeywordCacheVO: "+serviceKeywordCacheVO);
           return serviceKeywordCacheVO;
   	}    
    
    public static ServiceKeywordCacheVO getServiceTypeObjectFromRedis(String key) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("getServiceTypeObjectFromRedis()", "Entered key " + key);
        }
        ServiceKeywordCacheVO serviceKeywordCacheVO = null;
        RedisActivityLog.log("ServiceKeywordCache->getServiceTypeObjectFromRedis->Start");	
        String methodName = "getServiceTypeObjectFromRedis";
        Jedis jedis = null;
		 try {
			 jedis = RedisConnectionPool.getPoolInstance().getResource();
			 String serviceTypeObj = jedis.hget(hKeyServiceType, key);
				if(!BTSLUtil.isNullString(serviceTypeObj))
			    serviceKeywordCacheVO = (ServiceKeywordCacheVO)gson.fromJson(serviceTypeObj,ServiceKeywordCacheVO.class);
			 }catch(JedisConnectionException je){
			 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	   		        _log.errorTrace(methodName, je);
	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordCache[getServiceTypeObjectFromRedis]", "", "", "", "JedisConnectionException :" + je.getMessage());
			 }catch(NoSuchElementException  ex){
			 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	   		        _log.errorTrace(methodName, ex);
	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordCache[getServiceTypeObjectFromRedis]", "", "", "", "NoSuchElementException :" + ex.getMessage());
			 }catch (Exception e) {
			 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
			 	_log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordCache[getServiceTypeObjectFromRedis]", "", "", "", "Exception :" + e.getMessage());
			 }
			 finally {
		        	if (jedis != null) {
		        	jedis.close();
		        	}
		        }
       	   	 RedisActivityLog.log("ServiceKeywordCache->getServiceTypeObjectFromRedis->End");
    	
       
        if (_log.isDebugEnabled()) {
            _log.debug("getServiceTypeObjectFromRedis()", "Exiting serviceKeywordCacheVO: " + serviceKeywordCacheVO);
        }
        return serviceKeywordCacheVO;
    }
    public static HashMap<String, ServiceKeywordCacheVO> getServiceKeywordMapFromRedis(String key) throws BTSLBaseException {
      	   	 RedisActivityLog.log("ServiceKeywordCache->getServiceKeywordMapFromRedis->Start");
      	   String methodName = "getServiceTypeObjectFromRedis";
           Jedis jedis = null;
           HashMap<String, ServiceKeywordCacheVO> cachedMap = null;
   		 try {
	    	jedis = RedisConnectionPool.getPoolInstance().getResource();
   			Map<String, String> serviceKeyWordMap = jedis.hgetAll(key);
   			if(serviceKeyWordMap!= null && !serviceKeyWordMap.isEmpty()) {
   				cachedMap = new HashMap<String, ServiceKeywordCacheVO>();
   			for (Entry<String,String> entry:serviceKeyWordMap.entrySet()) {
   				cachedMap.put(entry.getKey(), gson.fromJson(entry.getValue(), ServiceKeywordCacheVO.class));
			}
   		}
   		 }catch(JedisConnectionException je){
		 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
   		        _log.errorTrace(methodName, je);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordCache[getServiceTypeObjectFromRedis]", "", "", "", "JedisConnectionException :" + je.getMessage());
		 }catch(NoSuchElementException  ex){
		 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
   		        _log.errorTrace(methodName, ex);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordCache[getServiceTypeObjectFromRedis]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		 }catch (Exception e) {
		 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
		 	_log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordCache[getServiceTypeObjectFromRedis]", "", "", "", "Exception :" + e.getMessage());
		 }
		 finally {
	        	if (jedis != null) {
	        	jedis.close();
	        	}
	        }
   	 return cachedMap;
    }

    
}
