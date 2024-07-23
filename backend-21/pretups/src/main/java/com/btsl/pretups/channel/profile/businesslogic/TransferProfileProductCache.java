package com.btsl.pretups.channel.profile.businesslogic;

import java.util.HashMap;
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
import com.btsl.pretups.common.PretupsErrorCodesI;
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

public class TransferProfileProductCache implements Runnable{
    private static Log _log = LogFactory.getLog(TransferProfileProductCache.class.getName());
    private final static String CLASS_NAME = "TransferProfileProductCache";
    private static HashMap<String, TransferProfileProductVO> _transferProfileProductMap = new HashMap<String, TransferProfileProductVO>();
    private static final String hKeyTrfProfileCache = "TransferProfileProductCache";
    private static String redisEnable = BTSLUtil.NullToString(Constants.getProperty("REDIS_ENABLE"));
    private static  int keyTimer = Integer.parseInt(Constants.getProperty("KEYTIMER"));
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss:mss").create();

    private static LoadingCache<String,TransferProfileProductVO>  trfProfileProductCacheMap = CacheBuilder.newBuilder()
	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)     
	    .build(new CacheLoader<String, TransferProfileProductVO>(){
			@Override
			public TransferProfileProductVO load(String key) throws Exception {
				return getTrfProfileProductObjectFromRedis(key);
			}
	     });

    public void run() {
        try {
            Thread.sleep(50);
            loadTransferProfileProductsAtStartup();
        } catch (Exception e) {
        	 _log.error("TransferProfileProductCache init() Exception ", e);
        }
    }
    /**
     * @author sachin.sharma
     *         Description : This method loads the transfer profile product
     *         cache at startup
     *         Method : loadTransferProfileProductsAtStartup
     * @return
     * @throws Exception 
     */
    public static void loadTransferProfileProductsAtStartup() throws BTSLBaseException {
    	final String methodName = "loadTransferProfileProductsAtStartup";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        try{
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
          Jedis jedis = null;
  	   	  RedisActivityLog.log("TransferProfileProductCache->loadTransferProfileProductsAtStartup->Start");
			 try {
				 jedis = RedisConnectionPool.getPoolInstance().getResource();
				 Pipeline pipeline = jedis.pipelined();
				//If key is already present in redis db do not reload
				if(!jedis.exists(hKeyTrfProfileCache)) {
					HashMap<String, TransferProfileProductVO> transferProfileMap = loadMapping();
					 for (Entry<String, TransferProfileProductVO> entry : transferProfileMap.entrySet())  {
				      pipeline.hset(hKeyTrfProfileCache, entry.getKey(), gson.toJson(entry.getValue()));
					 }
					 pipeline.sync();
				 }
				 }catch(JedisConnectionException je){
						_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
		   		        _log.errorTrace(methodName, je);
		   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileProductCache[loadTransferProfileProductsAtStartup]", "", "", "", "JedisConnectionException :" + je.getMessage());
		   		        throw new BTSLBaseException(TransferProfileProductCache.class.getName(), methodName,je.getMessage());
				}catch(NoSuchElementException  ex){
					_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	   		        _log.errorTrace(methodName, ex);
	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileProductCache[loadTransferProfileProductsAtStartup]", "", "", "", "NoSuchElementException :" + ex.getMessage());
	   		        throw new BTSLBaseException(TransferProfileProductCache.class.getName(), methodName,ex.getMessage());
				}catch (Exception e){
					_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
	   		        _log.errorTrace(methodName, e);
	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileProductCache[loadTransferProfileProductsAtStartup]", "", "", "", "Exception :" + e.getMessage());
	   		        throw new BTSLBaseException(TransferProfileProductCache.class.getName(), methodName,e.getMessage());
				}finally{
					if(jedis != null)
						jedis.close();
				}
		   	  RedisActivityLog.log("TransferProfileProductCache->loadTransferProfileProductsAtStartup->End");
	   	} else {
	        _transferProfileProductMap = loadMapping();
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
     * @author sachin.sharma
     *         Description : This method loads the transfer profile product
     *         mapping
     *         Method : loadMapping
     * @return HashMap
     * @throws Exception 
     */
    private static HashMap<String, TransferProfileProductVO> loadMapping() throws BTSLBaseException {
    	final String methodName = "loadMapping";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        HashMap<String, TransferProfileProductVO> transferProfileProductMap = null;
        TransferProfileDAO transferProfileDAO = null;
        try {
            transferProfileDAO = new TransferProfileDAO();
            transferProfileProductMap = transferProfileDAO.loadTransferProfileProducts();
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
            	_log.debug(methodName, PretupsI.EXITED + ". transferProfileProductMap.size()=" + transferProfileProductMap.size());
            }
        }
        return transferProfileProductMap;
    }

    /**
     * @author sachin.sharma
     *         Description : This method returns the details of transfer profile
     *         product based on p_profileID,p_productCode,
     *         from the _transferProfileProductMap
     *         Method : _transferProfileProductMap
     * @return TransferProfileProductVO
     */
    public static TransferProfileProductVO getTransferProfileDetails(String p_profileID, String p_productCode) throws BTSLBaseException {
    	final String METHOD_NAME = "getTransferProfileDetails";
    	StringBuilder loggerValue= new StringBuilder(); 
    	if (_log.isDebugEnabled()) {
    		loggerValue.setLength(0);
    		loggerValue.append("Entered p_profileID=");
    		loggerValue.append(p_profileID);
    		loggerValue.append(" p_productCode=");
    		loggerValue.append(p_productCode);
            _log.debug("getTransferProfileDetails",  loggerValue);
        }
        TransferProfileProductVO transferProfileProductVO = null;
        try {
            if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
   			 transferProfileProductVO = trfProfileProductCacheMap.get(p_profileID + "_" + p_productCode);
           } else {
            transferProfileProductVO = _transferProfileProductMap.get(p_profileID + "_" + p_productCode);
           }
            if (transferProfileProductVO == null) {
                throw new BTSLBaseException("TransferProfileProductCache", "getTransferProfileDetails", PretupsErrorCodesI.ERROR_TRANSFER_PROFILE_PRODUCT_FOR_ID_NOTFOUND, 0,
                    null);
            }
        }catch(ExecutionException ex){
      		_log.error("getTransferProfileDetails", PretupsI.EXCEPTION + ex.getMessage());
	        _log.errorTrace("getTransferProfileDetails", ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileProductCache[getTransferProfileDetails]", "", "", "", "ExecutionException :" + ex.getMessage());
            throw new BTSLBaseException("TransferProfileProductCache", "getTransferProfileDetails", "ExecutionException :",ex);
        }catch (InvalidCacheLoadException e) { 
			_log.error("getTransferProfileDetails", PretupsI.EXCEPTION + e.getMessage());
		     _log.errorTrace("getTransferProfileDetails", e);
	        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileProductCache[getTransferProfileDetails]", "", "", "", "InvalidCacheLoadException :" + e.getMessage());	
		} catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            loggerValue.setLength(0);
    		loggerValue.append( "Exception:");
    		loggerValue.append( e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "TransferProfileProductCache[getTransferProfileDetails]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException("TransferProfileProductCache", "getTransferProfileDetails", "error.general.processing",e);
        }
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
    		loggerValue.append( "Exiting. transferProfileProductVO=");
    		loggerValue.append( transferProfileProductVO.toString());
            _log.debug("getTransferProfileDetails", loggerValue );
        }
        return transferProfileProductVO;
    }
    public static void updateTransferProfileProducts() throws BTSLBaseException {
    	final String methodName = "updateTransferProfileProducts";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        try{
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
  	   	  RedisActivityLog.log("TransferProfileProductCache->updateTransferProfileProducts->Start");
  	   	  Jedis jedis =null; 
  	   	  try {
				 jedis = RedisConnectionPool.getPoolInstance().getResource();
				 Pipeline pipeline = jedis.pipelined();
				//If key is already present in redis db do not reload
				 pipeline.del(hKeyTrfProfileCache);
			    HashMap<String, TransferProfileProductVO> transferProfileMap = loadMapping();
				for (Entry<String, TransferProfileProductVO> entry : transferProfileMap.entrySet())  {
					pipeline.hset(hKeyTrfProfileCache,entry.getKey(), gson.toJson(entry.getValue()));
				}
				pipeline.sync();
		    }catch(JedisConnectionException je){
				_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
   		        _log.errorTrace(methodName, je);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileProductCache[updateTransferProfileProducts]", "", "", "", "JedisConnectionException :" + je.getMessage());
   		        throw new BTSLBaseException(TransferProfileProductCache.class.getName(), methodName,je.getMessage());
			}catch(NoSuchElementException  ex){
				_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
   		        _log.errorTrace(methodName, ex);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileProductCache[updateTransferProfileProducts]", "", "", "", "NoSuchElementException :" + ex.getMessage());
   		        throw new BTSLBaseException(TransferProfileProductCache.class.getName(), methodName,ex.getMessage());
			}catch (Exception e){
				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
   		        _log.errorTrace(methodName, e);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileProductCache[updateTransferProfileProducts]", "", "", "", "Exception :" + e.getMessage());
   		        throw new BTSLBaseException(TransferProfileProductCache.class.getName(), methodName,e.getMessage());
			}finally{
				if(jedis != null)
					jedis.close();
			}
		RedisActivityLog.log("TransferProfileProductCache->updateTransferProfileProducts->End");
	   	 } else {
	        _transferProfileProductMap = loadMapping();
	   	  }
        }catch(BTSLBaseException be) {
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
     * @param key
     * @return
     */
    public static TransferProfileProductVO getTrfProfileProductObjectFromRedis(String key) {
    	String methodName = "getSvcSltIntfObjectFromRedis";
    	if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered key: " + key);
        }
        RedisActivityLog.log("TransferProfileProductCache->getTrfProfileProductObjectFromRedis->Start");
    	Jedis jedis = null;
    	TransferProfileProductVO transferProfileProductVO = null;
		 try {
			 jedis = RedisConnectionPool.getPoolInstance().getResource();
			 String json = jedis.hget(hKeyTrfProfileCache, key);
			if(!BTSLUtil.isNullString(json))
				transferProfileProductVO = gson.fromJson(json, TransferProfileProductVO.class); 
		 }catch(JedisConnectionException je){
		 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
   		        _log.errorTrace(methodName, je);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileProductCache[getTrfProfileProductObjectFromRedis]", "", "", "", "JedisConnectionException :" + je.getMessage());
		 }catch(NoSuchElementException  ex){
		 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
   		        _log.errorTrace(methodName, ex);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileProductCache[getTrfProfileProductObjectFromRedis]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		 }catch (Exception e) {
		 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
		 	_log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileProductCache[getTrfProfileProductObjectFromRedis]", "", "", "", "Exception :" + e.getMessage());
		 }
		 finally {
	        	if (jedis != null) {
	        	jedis.close();
	        	}
	        }
		 RedisActivityLog.log("TransferProfileProductCache->getTrfProfileProductObjectFromRedis->End");
      return transferProfileProductVO;
    }

}
