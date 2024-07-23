package com.btsl.pretups.product.businesslogic;

import java.lang.reflect.Type;
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
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
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

public class NetworkProductCache implements Runnable{
    private static Log _log = LogFactory.getLog(NetworkProductCache.class.getName());
    private static HashMap<String, ProductVO> _networkProductMap = null;
    private static String redisEnable = BTSLUtil.NullToString(Constants.getProperty("REDIS_ENABLE"));
    private static final String hKeyNetworkProductCache = "NetworkProductCache";
    private static  int keyTimer = Integer.parseInt(Constants.getProperty("KEYTIMER"));
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss:mss").create();

    private static LoadingCache<String,HashMap<String,ProductVO>>  networkProductCacheMap = CacheBuilder.newBuilder()
    	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)     
    	    .build(new CacheLoader<String, HashMap<String,ProductVO>>(){
    			@Override
    			public HashMap<String,ProductVO> load(String key) throws Exception {
    				return getNetworkProductCacheFromRedis(key);
    			}
    	     });
    public void run() {
        try {
            Thread.sleep(50);
            loadNetworkProductMapAtStartup();
        } catch (Exception e) {
        	 _log.error("NetworkProductCache init() Exception ", e);
        }
    }
    
    /**
     * @author ankur.dhawan
     *         Description : This method loads the network product cache at
     *         startup
     *         Method : loadNetworkProductMapAtStartup
     * @return
     * @throws Exception 
     */
    public static void loadNetworkProductMapAtStartup() throws BTSLBaseException {
    	final String methodName = "loadNetworkProductMapAtStartup";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        try{
	        	if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
	        		RedisActivityLog.log("NetworkProductCache->loadNetworkProductMapAtStartup->Start"); 
	        		Jedis jedis = null;
	        		try {
	          		    jedis = RedisConnectionPool.getPoolInstance().getResource();
	       				//If key is already present in redis db do not reload
	       				if(!jedis.exists(hKeyNetworkProductCache)) {
	       					HashMap<String, ProductVO> _networkProductMap1 = loadMapping();
	       					jedis.set(hKeyNetworkProductCache, gson.toJson(_networkProductMap1));
	       				}
	          	   }catch(JedisConnectionException je){
   						_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
   		   		        _log.errorTrace(methodName, je);
   		   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkProductCache[loadNetworkProductMapAtStartup]", "", "", "", "JedisConnectionException :" + je.getMessage());
   		   		        throw new BTSLBaseException(NetworkProductCache.class.getName(), methodName,je.getMessage());
	   				}catch(NoSuchElementException  ex){
	   					_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	   	   		        _log.errorTrace(methodName, ex);
	   	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkProductCache[loadNetworkProductMapAtStartup]", "", "", "", "NoSuchElementException :" + ex.getMessage());
	   	   		        throw new BTSLBaseException(NetworkProductCache.class.getName(), methodName,ex.getMessage());
	   				}catch (Exception e){
	   					_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
	   	   		        _log.errorTrace(methodName, e);
	   	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkProductCache[loadNetworkProductMapAtStartup]", "", "", "", "Exception :" + e.getMessage());
	   	   		        throw new BTSLBaseException(NetworkProductCache.class.getName(), methodName,e.getMessage());
	   				}finally{
	   					if(jedis != null)
	   						jedis.close();
	   				}
        		RedisActivityLog.log("NetworkProductCache->loadNetworkProductMapAtStartup->Stop"); 	 
	        } else {
	        	_networkProductMap = loadMapping();
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
        	throw new BTSLBaseException("NetworkProductCache", methodName, "");
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED);
        	}
        }
    }

    /**
     * @author ankur.dhawan
     *         Description : This method loads the network product mapping
     *         Method : loadMapping
     * @return HashMap
     * @throws Exception 
     */
    private static HashMap<String, ProductVO> loadMapping() throws BTSLBaseException {
    	final String methodName = "loadMapping";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        HashMap<String, ProductVO> productMap = new HashMap<String, ProductVO>();
        NetworkProductDAO networkProductDAO = null;
        try {
            networkProductDAO = new NetworkProductDAO();
            productMap = networkProductDAO.loadNetworkProductCache();
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
        	throw new BTSLBaseException("NetworkProductCache", methodName, "");
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug("loadMapping", PretupsI.EXITED + ". productMap.size()=" + productMap.size());
        	}
        }
        return productMap;
    }

    /**
     * @author ankur.dhawan
     *         Description : This method returns an object from
     *         networkProductMap
     *         Method : getObject
     * @param ptype TODO
     * @param prodCode TODO
     * @return ProductVO
     */
    public static ProductVO getObject(String p_module, String p_productType, long p_requestAmt, String ptype, String prodCode) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("getObject", "Entered"+p_module+p_productType+p_requestAmt+ptype+prodCode);
        }
        final String methodName = "getObject";
        ProductVO productVO = null;
        Boolean productFound = false;
        try {
        	 if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        		 HashMap<String, ProductVO> networkProductMap1 = networkProductCacheMap.get(hKeyNetworkProductCache);
        		 if(networkProductMap1 != null) {
     				for (Entry<String, ProductVO> entry : networkProductMap1.entrySet())  {
     	                productVO = entry.getValue();
     	                if(PretupsI.SERVICE_TYPE_POSTPAID_BILL_PAYMENT.equals(ptype))
     	                {
     	                	if (productVO.getProductCode().equals(prodCode)&&productVO.getModuleCode().equals(p_module) && productVO.getProductType().equals(p_productType) && !productVO.getStatus().equals(PretupsI.NO) && ((productVO.getProductCategory().equals(PretupsI.PRODUCT_CATEGORY_FIXED) && productVO.getUnitValue() == p_requestAmt) || productVO.getProductCategory().equals(PretupsI.PRODUCT_CATEGORY_FLEX))) {
         	                    productFound = true;
         	                    break;
         	                }
     	                }
     	                else if (productVO.getModuleCode().equals(p_module) && productVO.getProductType().equals(p_productType) && !productVO.getStatus().equals(PretupsI.NO) && ((productVO.getProductCategory().equals(PretupsI.PRODUCT_CATEGORY_FIXED) && productVO.getUnitValue() == p_requestAmt) || productVO.getProductCategory().equals(PretupsI.PRODUCT_CATEGORY_FLEX))) {
     	                    productFound = true;
     	                    break;
     	                }
     				}
     			}
        	 } else {
            Iterator<String> mapIterator = _networkProductMap.keySet().iterator();
            while (mapIterator.hasNext()) {
                productVO = (ProductVO) _networkProductMap.get(mapIterator.next());
                if(PretupsI.SERVICE_TYPE_POSTPAID_BILL_PAYMENT.equals(ptype))
	                {
	                	if (productVO.getProductCode().equals(prodCode)&&productVO.getModuleCode().equals(p_module) && productVO.getProductType().equals(p_productType) && !productVO.getStatus().equals(PretupsI.NO) && ((productVO.getProductCategory().equals(PretupsI.PRODUCT_CATEGORY_FIXED) && productVO.getUnitValue() == p_requestAmt) || productVO.getProductCategory().equals(PretupsI.PRODUCT_CATEGORY_FLEX))) {
 	                    productFound = true;
 	                    break;
 	                }
	                }
	               
                else if (productVO.getModuleCode().equals(p_module) && productVO.getProductType().equals(p_productType) && !productVO.getStatus().equals(PretupsI.NO) && ((productVO.getProductCategory().equals(PretupsI.PRODUCT_CATEGORY_FIXED) && productVO.getUnitValue() == p_requestAmt) || productVO.getProductCategory().equals(PretupsI.PRODUCT_CATEGORY_FLEX))) {
                    productFound = true;
                    break;
                }
            }
    	}
        }catch (InvalidCacheLoadException e) { 
			_log.error("getObject", PretupsI.EXCEPTION + e.getMessage());
		     _log.errorTrace("getObject", e);
	        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkProductCache[getObject]", "", "", "", "InvalidCacheLoadException :" + e.getMessage());	
       }catch(ExecutionException ex){
     		_log.error("getObject", PretupsI.EXCEPTION + ex.getMessage());
	        _log.errorTrace("getObject", ex);
           EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkProductCache[getObject]", "", "", "", "ExecutionException :" + ex.getMessage());
           throw new BTSLBaseException("NetworkProductCache", "getObject", "ExecutionException :",ex);
       }catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkProductCache[getObject]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("NetworkProductCache", "getObject", "error.general.processing");
        }
        if (_log.isDebugEnabled()) {
            _log.debug("getObject", "Exiting. productFound=" + productFound);
        }

        if (productFound) {
            return productVO;
        } else {
            return null;
        }
    }

    /**
     * @author Ashutosh
     *         Description : This method returns an object from
     *         networkProductMap taking product code as argument
     *         Method : getObject
     * @return ProductVO
     */
    public static ProductVO getObject(String p_productCode) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("getObject", "Entered p_productCode:" + p_productCode);
        }

        ProductVO productVO = null;
        try {
        	 if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        		 productVO = networkProductCacheMap.get(hKeyNetworkProductCache).get(p_productCode);
        	 } else {
    	            productVO = (ProductVO) _networkProductMap.get(p_productCode);
    		 }
        }catch (InvalidCacheLoadException e) { 
			_log.error("getObject", PretupsI.EXCEPTION + e.getMessage());
		     _log.errorTrace("getObject", e);
	        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkProductCache[getObject]", "", "", "", "InvalidCacheLoadException :" + e.getMessage());	
            throw new BTSLBaseException("NetworkProductCache", "getObject", "InvalidCacheLoadException :",e);
        }catch(ExecutionException ex){
      		_log.error("getObject", PretupsI.EXCEPTION + ex.getMessage());
	        _log.errorTrace("getObject", ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkProductCache[getObject]", "", "", "", "ExecutionException :" + ex.getMessage());
            throw new BTSLBaseException("NetworkProductCache", "getObject", "ExecutionException :",ex);
        } catch (Exception e) {
            _log.errorTrace("Exception in getObject() ", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkProductCache[getObject]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("NetworkProductCache", "getObject", "error.general.processing");
        }
        if (_log.isDebugEnabled()) {
            _log.debug("getObject", "Exiting. productVO=" + productVO);
        }

        return productVO;
    }
  
       /**
     * @throws BTSLBaseException
     */
    public static void updateNetworkProductMap() throws BTSLBaseException {
    	final String methodName = "updateNetworkProductMap";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
    try{
    	if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
    		RedisActivityLog.log("NetworkProductCache->updateNetworkProductMap->Start");
      		Jedis jedis = null;
    		try {
      		    jedis = RedisConnectionPool.getPoolInstance().getResource();
      		    Pipeline pipeline = jedis.pipelined();
      		    pipeline.del(hKeyNetworkProductCache);
   				HashMap<String, ProductVO> _networkProductMap1 = loadMapping();
   				 pipeline.set(hKeyNetworkProductCache,gson.toJson(_networkProductMap1));
   				 pipeline.sync();
      	   }catch(JedisConnectionException je){
				_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
   		        _log.errorTrace(methodName, je);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkProductCache[updateNetworkProductMap]", "", "", "", "JedisConnectionException :" + je.getMessage());
   		        throw new BTSLBaseException(NetworkProductCache.class.getName(), methodName,je.getMessage());
			}catch(NoSuchElementException  ex){
				_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
   		        _log.errorTrace(methodName, ex);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkProductCache[updateNetworkProductMap]", "", "", "", "NoSuchElementException :" + ex.getMessage());
   		        throw new BTSLBaseException(NetworkProductCache.class.getName(), methodName,ex.getMessage());
			}catch (Exception e){
				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
   		        _log.errorTrace(methodName, e);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkProductCache[updateNetworkProductMap]", "", "", "", "Exception :" + e.getMessage());
   		        throw new BTSLBaseException(NetworkProductCache.class.getName(), methodName,e.getMessage());
			}finally{
				if(jedis != null)
					jedis.close();
			}
      		RedisActivityLog.log("NetworkProductCache->updateNetworkProductMap->Stop"); 
	    } else {
	        _networkProductMap = loadMapping();
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
        	throw new BTSLBaseException("NetworkProductCache", methodName, "");
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED);
        	}
        }
    }
    
    /**
     * @param key
     * @return
     */
    public static HashMap<String,ProductVO> getNetworkProductCacheFromRedis(String key) {
    	String methodName = "getNetworkProductCacheFromRedis";
    	if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered key: " + key);
        }
    	Jedis jedis = null;
    	HashMap<String, ProductVO> networkProMap = new HashMap<String,ProductVO>();
    	try {
    		 RedisActivityLog.log("NetworkProductCache->getNetworkProductCacheFromRedis->Start");
			 jedis = RedisConnectionPool.getPoolInstance().getResource();
			 String json = jedis.get(key);
				Type classType = new TypeToken<HashMap<String, ProductVO>>() {}.getType();
				if(json != null)
					networkProMap=gson.fromJson(json, classType);
	 	 RedisActivityLog.log("NetworkProductCache->getNetworkProductCacheFromRedis->End");
    	}catch(JedisConnectionException je){
	 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
		        _log.errorTrace(methodName, je);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkProductCache[getNetworkProductCacheFromRedis]", "", "", "", "JedisConnectionException :" + je.getMessage());
		 }catch(NoSuchElementException  ex){
		 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
			        _log.errorTrace(methodName, ex);
		            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkProductCache[getNetworkProductCacheFromRedis]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		 }catch (Exception e) {
		 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
		 	_log.errorTrace(methodName, e);
		    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkProductCache[getNetworkProductCacheFromRedis]", "", "", "", "Exception :" + e.getMessage());
		 }
		 finally {
		    	if (jedis != null) {
		    	jedis.close();
		    	}
		    }
      return networkProMap;
    }

    

}
