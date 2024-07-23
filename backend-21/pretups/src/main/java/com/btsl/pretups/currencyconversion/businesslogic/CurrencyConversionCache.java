
package com.btsl.pretups.currencyconversion.businesslogic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
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
import com.btsl.redis.util.RedisUtil;
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

public class CurrencyConversionCache implements Runnable {

	private static final Log LOG = LogFactory.getLog(CurrencyConversionCache.class.getName());
	private static Map<String, CurrencyConversionVO> _currencyConversionDetailMap = new HashMap<String, CurrencyConversionVO>();
	private static final String hKeyCurrencyConversionDetailMap = "CurrencyConversionCache";
	private static String redisEnable = BTSLUtil.NullToString(Constants.getProperty("REDIS_ENABLE"));
	private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss:mss").create();
	private static final int keyTimer = Integer.parseInt(Constants.getProperty("KEYTIMER"));
	private static LoadingCache<String, CurrencyConversionVO> currencyConversionDetailMemo = CacheBuilder.newBuilder()
	    	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)
	    	    .build(new CacheLoader<String, CurrencyConversionVO>(){
	    			@Override
	    			public CurrencyConversionVO load(String key) throws Exception {
	    				return getObjectFromRedis(key);
	    			}
	    	     });
	 public void run() {
	        try {
	            Thread.sleep(50);
	            loadCurrencyConversionDetailsAtStartUp();
	        } catch (Exception e) {
	        	LOG.error("CurrencyConversionCache init() Exception ", e);
	        }
	    }
	public static void loadCurrencyConversionDetailsAtStartUp() throws BTSLBaseException {
		final String methodName = "loadCurrencyConversionDetailsAtStartUp";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, PretupsI.ENTERED);
		}
		try{
			if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
	            RedisActivityLog.log("CurrencyConversionCache->loadCurrencyConversionDetailsAtStartUp->Start");
	            Jedis jedis  = null;
	   			 try {
	   				 jedis =  RedisConnectionPool.getPoolInstance().getResource();
	   				//Get the byte array for the hash key
	   				//byte[] hKeyBytes = RedisUtil.serialize(hKeyCurrencyConversionDetailMap);
	   				//If key is already present in redis db do not reload
    				 if(!jedis.exists(hKeyCurrencyConversionDetailMap)) {
    					 Pipeline pipeline = jedis.pipelined();
	    				HashMap<String, CurrencyConversionVO> currencyConversionDetailMap = (HashMap<String, CurrencyConversionVO>) loadMapping();
	   					 for (Entry<String, CurrencyConversionVO> entry : currencyConversionDetailMap.entrySet())  {
	   						 pipeline.hset(hKeyCurrencyConversionDetailMap, entry.getKey(), gson.toJson(entry.getValue()));
	   					} 
	   				 pipeline.sync();
	    			}
	   			 }catch(JedisConnectionException je){
	   				LOG.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	   				LOG.errorTrace(methodName, je);
	 	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CurrencyConversionCache[loadServicesAtStartup]", "", "", "", "JedisConnectionException :" + je.getMessage());
	 	   	        throw new BTSLBaseException(CurrencyConversionCache.class.getName(), methodName,je.getMessage());
	   			 }catch(NoSuchElementException  ex){
	 				LOG.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	 				LOG.errorTrace(methodName, ex);
	 	   	         EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CurrencyConversionCache[loadServicesAtStartup]", "", "", "", "NoSuchElementException :" + ex.getMessage());
	 	   	     throw new BTSLBaseException(CurrencyConversionCache.class.getName(), methodName,ex.getMessage());
	   			 }catch (Exception e) {
	 				LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
	 				LOG.errorTrace(methodName, e);
	 	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CurrencyConversionCache[loadServicesAtStartup]", "", "", "", "Exception :" + e.getMessage());
	 	           throw new BTSLBaseException(CurrencyConversionCache.class.getName(), methodName,e.getMessage());
	   			 }
	 			 finally {
	 		        	if (jedis != null) {
	 		        	jedis.close();
	 		        }	
	             RedisActivityLog.log("CurrencyConversionCache->loadCurrencyConversionDetailsAtStartUp->End");
			} 
	   	}else {	 
				_currencyConversionDetailMap = loadMapping();
		   }
		}
		catch(BTSLBaseException be) {
			LOG.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
			LOG.errorTrace(methodName, be);
			throw be;
		}
		catch (Exception e)
		{
			LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
			LOG.errorTrace(methodName, e);
			new BTSLBaseException("CurrencyConversionCache", methodName, "");
		} finally {
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, PretupsI.EXITED);
			}
		}
	}

	public static Map<String, CurrencyConversionVO> loadMapping() throws BTSLBaseException {
		final String methodName = "loadMapping";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, PretupsI.ENTERED);
		}

		CurrencyConversionDAO currencyConversionDAO = null;
		try {
			currencyConversionDAO = new CurrencyConversionDAO();
			_currencyConversionDetailMap = currencyConversionDAO.loadCurrencyConversionDetails();
		}
		catch(BTSLBaseException be) {
			LOG.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
			LOG.errorTrace(methodName, be);
			throw be;
		}
		catch (Exception e)
		{
			LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
			LOG.errorTrace(methodName, e);
			new BTSLBaseException("CurrencyConversionCache", methodName, "");
		} finally {
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, PretupsI.EXITED);
			}
		}
		return _currencyConversionDetailMap;
	}

	public static Object getObject(String p_receiverNWID, String p_senderNWID,String p_country) {

		CurrencyConversionVO currencyConversionVO = null;
		String methodName = "getObject";
		String key = null;
		System.out.println("#########################"+_currencyConversionDetailMap.size());
		if (LOG.isDebugEnabled()) {
			LOG.debug("getObject()", "Entered  p_receiverNWID=" + p_receiverNWID + " p_senderNWID: " + p_senderNWID +" p_country: "+p_country);
		}
		key = p_receiverNWID + "_" + p_senderNWID+"_"+p_country;
		 if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
			 try {
				currencyConversionVO = (CurrencyConversionVO) currencyConversionDetailMemo.get(key);
			}catch (ExecutionException e) {
				LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
				LOG.errorTrace(methodName, e);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserServicesCache[getObject]", "", "", "", "ExecutionException :" + e.getMessage());
			}
        	catch (InvalidCacheLoadException e) { 
        		LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
        		LOG.errorTrace(methodName, e);
			}
		 } else {
        	currencyConversionVO = _currencyConversionDetailMap.get(key);
        }
		if (LOG.isDebugEnabled()) {
			LOG.debug("getObject()", "Exited " + currencyConversionVO);
		}
		return currencyConversionVO;
	}

	public static void updateCurrencyConversionMapping() throws BTSLBaseException {
		final String methodName = "updateCurrencyConversionMapping";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, PretupsI.ENTERED);
		}
		try{
		Map<String, CurrencyConversionVO> currentMap = loadMapping();
    	if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
            Jedis jedis  = null;
 			 try {
 	            RedisActivityLog.log("CurrencyConversionCache->updateCurrencyConversionMapping->Start");
 				jedis =  RedisConnectionPool.getPoolInstance().getResource();
 				Pipeline pipeline = jedis.pipelined();
 				pipeline.del(hKeyCurrencyConversionDetailMap);
			    for (Entry<String, CurrencyConversionVO> entry : currentMap.entrySet())  {
				     pipeline.hset(hKeyCurrencyConversionDetailMap, entry.getKey(), gson.toJson(entry.getValue()));
					 }
			    pipeline.sync();
	            RedisActivityLog.log("CurrencyConversionCache->updateCurrencyConversionMapping->End");
  			 }catch(JedisConnectionException je){
  				LOG.error(methodName, PretupsI.EXCEPTION + je.getMessage());
  				LOG.errorTrace(methodName, je);
	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CurrencyConversionCache[updateCurrencyConversionMapping]", "", "", "", "JedisConnectionException :" + je.getMessage());
	   	        throw new BTSLBaseException(CurrencyConversionCache.class.getName(), methodName,je.getMessage());
  			 }catch(NoSuchElementException  ex){
				LOG.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
				LOG.errorTrace(methodName, ex);
	   	         EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CurrencyConversionCache[updateCurrencyConversionMapping]", "", "", "", "NoSuchElementException :" + ex.getMessage());
	   	     throw new BTSLBaseException(CurrencyConversionCache.class.getName(), methodName,ex.getMessage());
  			 }catch (Exception e) {
				LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
				LOG.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CurrencyConversionCache[updateCurrencyConversionMapping]", "", "", "", "Exception :" + e.getMessage());
	           throw new BTSLBaseException(CurrencyConversionCache.class.getName(), methodName,e.getMessage());
  			 }
			 finally {
		        	if (jedis != null) {
		        	jedis.close();
		        }	
		     }
	    	 } else {
	    		 _currencyConversionDetailMap = currentMap;
    	 }
	  }
		catch(BTSLBaseException be) {
			LOG.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
			LOG.errorTrace(methodName, be);
			throw be;
		}
		catch (Exception e)
		{
			LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
			LOG.errorTrace(methodName, e);
			new BTSLBaseException("CurrencyConversionCache", methodName, "");
		} finally {
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, PretupsI.EXITED + _currencyConversionDetailMap.size());
			}
		}
	}


	/**
	 * CurrencyConversionCache.java
	 * @param p_targetCurrencyCode
	 * @param p_targetCountry
	 * @return
	 * ArrayList<ListValueVO>
	 * akanksha.gupta
	 * 01-Sep-2016 4:12:59 pm
	 */
	@SuppressWarnings("finally")
	public static ArrayList<ListValueVO> loadTargetCurrencyMapping(String p_targetCurrencyCode,String p_targetCountry) { final String method_name = "loadTargetCurrencyMapping";
		if (LOG.isDebugEnabled()) {
			LOG.debug(method_name, "Entered p_targetCurrencyCode"+p_targetCurrencyCode+"p_targetCountry"+p_targetCountry);
		}
		ArrayList<ListValueVO> 	_sourceCurrencyList = new ArrayList<ListValueVO>();	
		Jedis jedis = null;
		try {
			CurrencyConversionVO currencyVO = null;
			 ListValueVO listVO = null;
			 Set<Map.Entry<String, CurrencyConversionVO>> entrySet = null;		
			 if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
		            RedisActivityLog.log("CurrencyConversionCache->loadTargetCurrencyMapping->Start");
					jedis =  RedisConnectionPool.getPoolInstance().getResource();
					Map<String,String> currentMap = jedis.hgetAll(hKeyCurrencyConversionDetailMap);
					for(Entry<String,String> entry : currentMap.entrySet()){
						_currencyConversionDetailMap.put(entry.getKey(), gson.fromJson(entry.getValue(), CurrencyConversionVO.class));
					}
			       RedisActivityLog.log("CurrencyConversionCache->loadTargetCurrencyMapping->End");
				} 

				entrySet = _currencyConversionDetailMap.entrySet();
				for (Map.Entry<String, CurrencyConversionVO> entry : entrySet)
				{
					currencyVO=null;
					if(entry.getKey().endsWith("_"+p_targetCurrencyCode+"_"+p_targetCountry))
						 currencyVO = entry.getValue();
				if(currencyVO!= null)
				{
					listVO = new ListValueVO(currencyVO.getSourceCurrencyName(),currencyVO.getSourceCurrencyCode());
					_sourceCurrencyList.add(listVO);
				   }
			   }
	    
			} catch(NoSuchElementException  ex){
	   			LOG.error(method_name, PretupsI.EXCEPTION + ex.getMessage());
	   			LOG.errorTrace(method_name, ex);
	   			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CurrencyConversionCache[loadTargetCurrencyMapping]", "", "", "", "NoSuchElementException :" + ex.getMessage());
	   		 }catch (Exception e) {
	 			LOG.error(method_name, "Exception e:" + e.getMessage());
				LOG.errorTrace(method_name, e);
			}
			finally{
				if (jedis != null) {
 		        	jedis.close();
				if (LOG.isDebugEnabled()) {
					LOG.debug(method_name, "Exiting with _sourceCurrencyList"+_sourceCurrencyList);
				}
			}
		}
		return _sourceCurrencyList;
	}
	
	public static CurrencyConversionVO getObjectFromRedis(String key) throws BTSLBaseException {

		CurrencyConversionVO currencyConversionVO = null;
		if (LOG.isDebugEnabled()) {
			LOG.debug("getObjectFromRedis()", "Entered Key=" + key);
		}
		String methodName ="getObjectFromRedis";
	    Jedis jedis = null;
		 try{
			RedisActivityLog.log("CurrencyConversionCache->getObjectFromRedis->Start");
			jedis =  RedisConnectionPool.getPoolInstance().getResource();
			String currencyConvObj = jedis.hget(hKeyCurrencyConversionDetailMap, key);
			if(!BTSLUtil.isNullString(currencyConvObj))
				currencyConversionVO = gson.fromJson(currencyConvObj,CurrencyConversionVO.class);
		    RedisActivityLog.log("CurrencyConversionCache->getObjectFromRedis->End");
		  }catch(JedisConnectionException je){
			LOG.error(methodName, PretupsI.EXCEPTION + je.getMessage());
			LOG.errorTrace(methodName, je);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CurrencyConversionCache[getObjectFromRedis]", "", "", "", "JedisConnectionException :" + je.getMessage());
   	        throw new BTSLBaseException(CurrencyConversionCache.class.getName(), methodName,je.getMessage());
   		  }catch(NoSuchElementException  ex){
   			LOG.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
   			LOG.errorTrace(methodName, ex);
 	   	    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CurrencyConversionCache[getObjectFromRedis]", "", "", "", "NoSuchElementException :" + ex.getMessage());
 	   	    throw new BTSLBaseException(CurrencyConversionCache.class.getName(), methodName,ex.getMessage()); 	 
   		 }catch (Exception e) {
   			LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
   			LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CurrencyConversionCache[getObjectFromRedis]", "", "", "", "Exception :" + e.getMessage());
            throw new BTSLBaseException(CurrencyConversionCache.class.getName(), methodName,e.getMessage());
   		 }finally {
	        	if (jedis != null) {
	        	jedis.close();
	        }
        	if (LOG.isDebugEnabled()) {
    			LOG.debug("getObjectFromRedis()", "Exited " + currencyConversionVO);
    		}
		 }
		return currencyConversionVO;
	}
}

