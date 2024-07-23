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

public class TransferProfileCache implements Runnable{
    private static Log _log = LogFactory.getLog(TransferProfileCache.class.getName());
    private final static String CLASS_NAME = "TransferProfileCache";
    private static HashMap<String, TransferProfileVO> _transferProfileMap = new HashMap<String, TransferProfileVO>();
    private static String redisEnable = BTSLUtil.NullToString(Constants.getProperty("REDIS_ENABLE"));
    private static final String hKeyTrfProfileCache = "TransferProfileCache";
    private static  int keyTimer = Integer.parseInt(Constants.getProperty("KEYTIMER"));
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss:mss").create();

    private static LoadingCache<String,TransferProfileVO>  trfProfileCacheMap = CacheBuilder.newBuilder()
	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)     
	    .build(new CacheLoader<String, TransferProfileVO>(){
			@Override
			public TransferProfileVO load(String key) throws Exception {
				return getTrfProfileProductObjectFromRedis(key);
			}
	     });

    public void run() {
        try {
            Thread.sleep(50);
            loadTransferProfileAtStartup();
        } catch (Exception e) {
        	 _log.error("TransferProfileCache init() Exception ", e);
        }
    }
    
    /**
     * @author sachin.sharma
     *         Description : This method loads the Transfer Profile cache at
     *         startup
     *         Method : loadTransferProfileAtStartup
     * @return
     * @throws Exception 
     */
    public static void loadTransferProfileAtStartup() throws BTSLBaseException {
    	final String methodName = "loadTransferProfileAtStartup";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        try{
        	 if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
                 Jedis jedis = null;
         	   	  RedisActivityLog.log("TransferProfileCache->loadTransferProfileAtStartup->Start");
       			 try {
       				 jedis = RedisConnectionPool.getPoolInstance().getResource();
       				 Pipeline pipeline = jedis.pipelined();
    				 pipeline.del(hKeyTrfProfileCache);
       					HashMap<String, TransferProfileVO> transferProfileMap = loadMapping();
       					 for (Entry<String, TransferProfileVO> entry : transferProfileMap.entrySet())  {
       				      pipeline.hset(hKeyTrfProfileCache, entry.getKey(), gson.toJson(entry.getValue()));
       					 }
       				  pipeline.sync();
   				 }catch(JedisConnectionException je){
   						_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
   		   		        _log.errorTrace(methodName, je);
   		   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileCache[loadTransferProfileAtStartup]", "", "", "", "JedisConnectionException :" + je.getMessage());
   		   		        throw new BTSLBaseException(TransferProfileCache.class.getName(), methodName,je.getMessage());
   				}catch(NoSuchElementException  ex){
   					_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
   	   		        _log.errorTrace(methodName, ex);
   	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileCache[loadTransferProfileAtStartup]", "", "", "", "NoSuchElementException :" + ex.getMessage());
   	   		        throw new BTSLBaseException(TransferProfileCache.class.getName(), methodName,ex.getMessage());
   				}catch (Exception e){
   					_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
   	   		        _log.errorTrace(methodName, e);
   	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileCache[loadTransferProfileAtStartup]", "", "", "", "Exception :" + e.getMessage());
   	   		        throw new BTSLBaseException(TransferProfileCache.class.getName(), methodName,e.getMessage());
   				}finally{
   					if(jedis != null)
   						jedis.close();
   				}
       		   	  RedisActivityLog.log("TransferProfileCache->loadTransferProfileAtStartup->End");
       	   	} else {
       	   		_transferProfileMap = loadMapping();
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
     * @author sachin.sharma
     *         Description : This method loads the Transfer Profile mapping
     *         Method : loadMapping
     * @return HashMap
     * @throws Exception 
     */
    private static HashMap<String, TransferProfileVO> loadMapping() throws BTSLBaseException {
    	final String methodName = "loadMapping";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        HashMap<String, TransferProfileVO> transferProfileMap = null;
        TransferProfileDAO transferProfileDAO = null;
        try {
            transferProfileDAO = new TransferProfileDAO();
            transferProfileMap = transferProfileDAO.loadTransferProfile();
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
            	_log.debug(methodName, PretupsI.EXITED + ". transferProfileProductMap.size()=" + transferProfileMap.size());
            }
        }
        return transferProfileMap;
    }

    /**
     * @author sachin.sharma
     *         Description : This method returns the details of transfer profile
     *         based on p_profileID,p_networkCode,
     *         from the _transferProfileMap
     *         Method : getTransferProfileDetails
     * @return TransferProfileVO
     */
    public static TransferProfileVO getTransferProfileDetails(String p_profileID, String p_networkCode) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("getTransferProfileDetails", "Entered p_profileID=" + p_profileID + " p_productCode=" + p_networkCode);
        }
        final String METHOD_NAME = "getTransferProfileDetails";
        TransferProfileVO transferProfileVO = null;
        try {
        	 if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        		 transferProfileVO = trfProfileCacheMap.get(p_profileID + "_" + p_networkCode);
               } else {
                   transferProfileVO = _transferProfileMap.get(p_profileID + "_" + p_networkCode);
               }
            if (transferProfileVO == null) {
                throw new BTSLBaseException("TransferProfileCache", "getTransferProfileDetails", PretupsErrorCodesI.ERROR_TRANSFER_PROFILE_FOR_ID_NOTFOUND, 0, null);
            }
        }catch(ExecutionException ex){
      		_log.error("getTransferProfileDetails", PretupsI.EXCEPTION + ex.getMessage());
	        _log.errorTrace("getTransferProfileDetails", ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileCache[getTransferProfileDetails]", "", "", "", "ExecutionException :" + ex.getMessage());
            throw new BTSLBaseException("TransferProfileCache", "getTransferProfileDetails", "ExecutionException :",ex);
        }catch (InvalidCacheLoadException e) { 
			_log.error("getTransferProfileDetails", PretupsI.EXCEPTION + e.getMessage());
		     _log.errorTrace("getTransferProfileDetails", e);
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileCache[getTransferProfileDetails]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("TransferProfileCache", "getTransferProfileDetails", "error.general.processing");
        }
        if (_log.isDebugEnabled()) {
            _log.debug("getTransferProfileDetails", "Exiting. transferProfileProductVO=" + transferProfileVO.toString());
        }
        return transferProfileVO;
    }
    
    /**
     * @param key
     * @return
     */
    public static TransferProfileVO getTrfProfileProductObjectFromRedis(String key) {
    	String methodName = "getTrfProfileProductObjectFromRedis";
    	if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered key: " + key);
        }
        RedisActivityLog.log("TransferProfileProductCache->getTrfProfileProductObjectFromRedis->Start");
    	Jedis jedis = null;
    	TransferProfileVO transferProfileVO = null;
		 try {
			 jedis = RedisConnectionPool.getPoolInstance().getResource();
			 String json = jedis.hget(hKeyTrfProfileCache, key);
			if(!BTSLUtil.isNullString(json))
				transferProfileVO = gson.fromJson(json, TransferProfileVO.class); 
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
      return transferProfileVO;
    }

}
