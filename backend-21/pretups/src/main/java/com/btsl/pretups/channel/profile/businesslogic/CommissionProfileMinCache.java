package com.btsl.pretups.channel.profile.businesslogic;

import java.util.HashMap;
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
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.redis.pool.RedisConnectionPool;
import com.btsl.redis.util.RedisActivityLog;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class CommissionProfileMinCache implements Runnable {
	
	private static final  Log LOG = LogFactory.getLog(CommissionProfileMinCache.class.getName());
	private static Map<String,ChannelTransferItemsVO> _minCommDetailMap = new HashMap<String, ChannelTransferItemsVO>();
	private static String redisEnable = BTSLUtil.NullToString(Constants.getProperty("REDIS_ENABLE"));
	private static final String hKeyMinCommDetailMap = "CommissionProfileMinCache";
	private static Gson gson = new GsonBuilder()
    		.setDateFormat("yyyy-MM-dd HH:mm:ss:mss")
    		.create();
    private static final int keyTimer = Integer.parseInt(Constants.getProperty("KEYTIMER"));
    private static LoadingCache<String,ChannelTransferItemsVO> _minCommDetailMemo = CacheBuilder.newBuilder()
    	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)
    	    .build(new CacheLoader<String,ChannelTransferItemsVO>(){
    			@Override
    			public ChannelTransferItemsVO load(String key) throws Exception {
    				return getObjectFromRedis(key);
    			}
    	     });
    
    
	 public void run() {
	        try {
	            Thread.sleep(50);
	            loadMinCommissionDetailsAtStartUp();
	        } catch (Exception e) {
	        	LOG.error("CommissionProfileMinCache init() Exception ", e);
	        }
	    }
	public static void loadMinCommissionDetailsAtStartUp() {
		if(LOG.isDebugEnabled())
			LOG.debug("loadMinCommissionDetailsAtStartUp", "Entered");
		String  methodName = "loadMinCommissionDetailsAtStartUp";
		if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
	   		 RedisActivityLog.log("CommissionProfileMinCache->loadMinCommissionDetailsAtStartUp->Start");
	   		 Jedis jedis = null;
			 try {
				 jedis = RedisConnectionPool.getPoolInstance().getResource();
  				    Pipeline pipeline = jedis.pipelined();
  					Map<String,ChannelTransferItemsVO> _minCommDetailMap1 = loadMapping();
  					for (Entry<String, ChannelTransferItemsVO> entry : _minCommDetailMap1.entrySet())  {
 				      pipeline.hset(hKeyMinCommDetailMap, entry.getKey(), gson.toJson(entry.getValue()));
  					pipeline.sync();
  			     }
 			 }catch(JedisConnectionException je){
 				 LOG.error(methodName, PretupsI.EXCEPTION + je.getMessage());
 				 LOG.errorTrace(methodName, je);
 	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileMinCache[loadMinCommissionDetailsAtStartUp]", "", "", "", "JedisConnectionException :" + je.getMessage());
 			 }catch(NoSuchElementException  ex){
 				 LOG.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
 				 LOG.errorTrace(methodName, ex);
 	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileMinCache[loadMinCommissionDetailsAtStartUp]", "", "", "", "NoSuchElementException :" + ex.getMessage());
 			 }catch (Exception e) {
 				 LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
 				 LOG.errorTrace(methodName, e);
 	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileMinCache[loadMinCommissionDetailsAtStartUp]", "", "", "", "Exception :" + e.getMessage());
 			 }
 			 finally {
 		        	if (jedis != null) {
 		        	jedis.close();
 		        	}
 		        }
	   		 RedisActivityLog.log("CommissionProfileMinCache->loadMinCommissionDetailsAtStartUp->End");
		 } else {	 
			_minCommDetailMap = loadMapping();
	   }
		if (LOG.isDebugEnabled())LOG.debug("loadUserStatusDetailsAtStartUp", "Exited");
		
	}
	
	public static Map<String,ChannelTransferItemsVO> loadMapping(){
		if(LOG.isDebugEnabled())
			LOG.debug("loadMapping", "Entered");
		
		CommissionProfileDAO commissionProfileDAO = null;
		try
		{
			commissionProfileDAO = new CommissionProfileDAO();
			_minCommDetailMap = commissionProfileDAO.loadMinCommProfiles();
		}
		
		catch(Exception e)
		{
			LOG.error("loadMapping", "Exception e:"+e.getMessage());
			LOG.errorTrace("loadMapping", e);
		}
		
		return _minCommDetailMap;
	}
	
	public static Object getObject(String p_setId, String p_version){
		String methodName = "getObject";
		ChannelTransferItemsVO channelTransferItemsVO =  null;
        if (LOG.isDebugEnabled()) {
            LOG.debug("getObject()","Entered  p_setId="+p_setId+" p_version: "+p_version);
        }
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        	try {
				channelTransferItemsVO = _minCommDetailMemo.get(p_setId+"_"+p_version);
			} catch (ExecutionException e) {
				LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
   		        LOG.errorTrace(methodName, e);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileMinCache[methodName]", "", "", "", "ExecutionException :" + e.getMessage());
			}
      } else {
    	  channelTransferItemsVO=_minCommDetailMap.get(p_setId+"_"+p_version);
      }
        if (LOG.isDebugEnabled())LOG.debug("getObject()","Exited "+channelTransferItemsVO );
		return channelTransferItemsVO;
	}
	
	public static void updateMinCommProfileMapping() throws BTSLBaseException {
		if (LOG.isDebugEnabled())
			LOG.debug("updateMinCommProfileMapping", " Entered");
		String  methodName = "updateMinCommProfileMapping";
		Map<String,ChannelTransferItemsVO> currentMap = loadMapping();
		if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
	   		 RedisActivityLog.log("CommissionProfileMinCache->updateMinCommProfileMapping->Start");
	   		 Jedis jedis = null;
			 try {
				 jedis = RedisConnectionPool.getPoolInstance().getResource();
				  Pipeline pipeline = jedis.pipelined();
				  pipeline.del(hKeyMinCommDetailMap);
	     		   for (Entry<String, ChannelTransferItemsVO> entry : currentMap.entrySet())  {
		  		       pipeline.hset(hKeyMinCommDetailMap, entry.getKey(),gson.toJson(entry.getValue()));
		  			}
		  			pipeline.sync();
 			 }catch(JedisConnectionException je){
 				 LOG.error(methodName, PretupsI.EXCEPTION + je.getMessage());
 				 LOG.errorTrace(methodName, je);
 	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileMinCache[updateMinCommProfileMapping]", "", "", "", "JedisConnectionException :" + je.getMessage());
 	   		        throw new BTSLBaseException(CommissionProfileMinCache.class.getName(), methodName,je.getMessage());
 			 }catch(NoSuchElementException  ex){
 				 LOG.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
 				 LOG.errorTrace(methodName, ex);
 	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileMinCache[updateMinCommProfileMapping]", "", "", "", "NoSuchElementException :" + ex.getMessage());
 	   		        throw new BTSLBaseException(CommissionProfileMinCache.class.getName(), methodName,ex.getMessage());
 			 }catch (Exception e) {
 				 LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
 				 LOG.errorTrace(methodName, e);
 	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileMinCache[updateMinCommProfileMapping]", "", "", "", "Exception :" + e.getMessage());
 				throw new BTSLBaseException(CommissionProfileMinCache.class.getName(), methodName,e.getMessage());
 			 }
 			 finally {
 		        	if (jedis != null) {
 		        	jedis.close();
 		        	}
 		        }
	   	RedisActivityLog.log("CommissionProfileMinCache->updateMinCommProfileMapping->End");
  	   } else {
       _minCommDetailMap = currentMap;
  	   }
		if (LOG.isDebugEnabled())
			LOG.debug("updateMinCommProfileMapping", "exited "
					+ _minCommDetailMap.size());
	}
	

	public static ChannelTransferItemsVO getObjectFromRedis(String key) throws BTSLBaseException{
        if (LOG.isDebugEnabled()) {
            LOG.debug("getObject()","Entered  key="+key);
        }
        String methodName = "getObjectFromRedis";
	   	RedisActivityLog.log("CommissionProfileMinCache->getObjectFromRedis->Start");
	   	Jedis jedis = null;
		ChannelTransferItemsVO channelTransferItemsVO =  null;
       try {
            jedis = RedisConnectionPool.getPoolInstance().getResource();
            String jsonObj = jedis.hget(hKeyMinCommDetailMap,key);
	 	    if(!BTSLUtil.isNullString(jsonObj))
			  channelTransferItemsVO = (ChannelTransferItemsVO)gson.fromJson(jsonObj,ChannelTransferItemsVO.class);
		 }catch(JedisConnectionException je){
			 LOG.error(methodName, PretupsI.EXCEPTION + je.getMessage());
			 LOG.errorTrace(methodName, je);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileMinCache[getPreferenceObjectFromRedis]", "", "", "", "JedisConnectionException :" + je.getMessage());
   		        throw new BTSLBaseException(CommissionProfileMinCache.class.getName(), methodName,je.getMessage());
		 }catch(NoSuchElementException  ex){
			 LOG.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
			 LOG.errorTrace(methodName, ex);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileMinCache[getPreferenceObjectFromRedis]", "", "", "", "NoSuchElementException :" + ex.getMessage());
   		        throw new BTSLBaseException(CommissionProfileMinCache.class.getName(), methodName,ex.getMessage());
		 }catch (Exception e) {
			 LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
			 LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileMinCache[getPreferenceObjectFromRedis]", "", "", "", "Exception :" + e.getMessage());
			throw new BTSLBaseException(CommissionProfileMinCache.class.getName(), methodName,e.getMessage());
		 }
		 finally {
	        	if (jedis != null) {
	        	jedis.close();
	        	}
	        }
	   	RedisActivityLog.log("CommissionProfileMinCache->getObjectFromRedis->End");
		return channelTransferItemsVO;
	}
	

}
