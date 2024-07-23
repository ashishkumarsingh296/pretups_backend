package com.btsl.pretups.channel.user.businesslogic.wallet;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
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

public class UserProductWalletMappingCache implements Runnable {

    private static final Log LOG = LogFactory.getLog(UserProductWalletMappingCache.class.getName());

    private static List<UserProductWalletMappingVO> _userProductWalletMappingList = new ArrayList<UserProductWalletMappingVO>();
    private static String redisEnable = BTSLUtil.NullToString(Constants.getProperty("REDIS_ENABLE"));
	private static final String hKeyUserProductWalletMappingList = "UserProductWalletMappingCache";

	 private static Gson gson = new GsonBuilder()
		.setDateFormat("yyyy-MM-dd HH:mm:ss:mss")
		.create();
	private static final int keyTimer = Integer.parseInt(Constants.getProperty("KEYTIMER"));
	
	private static LoadingCache<String, List<UserProductWalletMappingVO>> UserProductWalletMappingCache = CacheBuilder.newBuilder()
	 .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)
	 .build(new CacheLoader<String,List<UserProductWalletMappingVO>>(){
			@Override
			public List<UserProductWalletMappingVO> load(String key) throws Exception {
				return getUserProductWalletMappingCache(key);
			}
	  });

	public void run() {
        try {
            Thread.sleep(50);
            loadUserProductWalletMappingOnStartUp();
        } catch (Exception e) {
        	LOG.error("UserProductWalletMappingCache init() Exception ", e);
        }
    }
    public static List<UserProductWalletMappingVO> getUserProductWalletMappingList() {
    	String methodName="getUserProductWalletMappingList";
    	try{
    		if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
    		_userProductWalletMappingList = UserProductWalletMappingCache.get(hKeyUserProductWalletMappingList);
    	}
    	}catch (ExecutionException e) {
    		LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
        	LOG.errorTrace(methodName, e);
   	        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MessageGatewayCache[getObject]", "", "", "", "ExecutionException :" + e.getMessage());
			}
    	catch (InvalidCacheLoadException e) { 
    		LOG.info(methodName, e);
		}
   	 return _userProductWalletMappingList;
    }

    public static void loadUserProductWalletMappingOnStartUp() throws BTSLBaseException {
    	final String methodName = "loadUserProductWalletMappingOnStartUp";
    	if (LOG.isDebugEnabled()) {
    		LOG.debug(methodName, PretupsI.ENTERED);
    	}
    	Jedis jedis = null;
        try{
        	if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        		 
        		RedisActivityLog.log("UserProductWalletMappingCache->loadUserProductWalletMappingOnStartUp->Start");
        		 jedis = RedisConnectionPool.getPoolInstance().getResource();
 				 Pipeline pipeline = jedis.pipelined();
				 pipeline.del(hKeyUserProductWalletMappingList);
				 List<UserProductWalletMappingVO> userProductWalletMappingList = loadMapping();
 				 //store data in redis list 
				 pipeline.set(hKeyUserProductWalletMappingList,gson.toJson(userProductWalletMappingList));
 			     pipeline.sync();
    			 RedisActivityLog.log("UserProductWalletMappingCache->loadUserProductWalletMappingOnStartUp->End");
   		 } else {	 
   			 _userProductWalletMappingList = loadMapping();
   	    }
      }
        catch(BTSLBaseException be) {
        	LOG.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
        	LOG.errorTrace(methodName, be);
        	throw be;
        } catch(JedisConnectionException je){
			LOG.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	        LOG.errorTrace(methodName, je);
           EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "UserProductWalletMappingCache[" + methodName + "]", "", "", "", "JedisConnectionException :" + je.getMessage());
		}catch(NoSuchElementException  ex){
			LOG.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	        LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "UserProductWalletMappingCache[" + methodName + "]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		}catch (Exception e){
        	LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
        	LOG.errorTrace(methodName, e);
        } finally {
        	if(jedis!= null)
        		jedis.close();
        	if (LOG.isDebugEnabled()) {
        		LOG.debug(methodName, PretupsI.EXITED);
        	}
        }
    }

    private static List<UserProductWalletMappingVO> loadMapping() throws BTSLBaseException {
    	final String methodName = "loadMapping";
    	if (LOG.isDebugEnabled()) {
    		LOG.debug(methodName, PretupsI.ENTERED);
    	}

        List<UserProductWalletMappingVO> userProdcutWalletMappingList = null;
        UserProductWalletMappingDAO userProductWalletDAO = null;
        try {
            userProductWalletDAO = new UserProductWalletMappingDAO();
            userProdcutWalletMappingList = userProductWalletDAO.loadUserProductWalletMappingList(false);

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
        	
        } finally {
        	if (LOG.isDebugEnabled()) {
        		LOG.debug(methodName, PretupsI.EXITED);
        	}
        }
        return userProdcutWalletMappingList;
    }

    public static List<UserProductWalletMappingVO> getUserProductWalletMappingCache(String key){
        if (LOG.isDebugEnabled()) {
            LOG.debug("getPrefixIfMapFromRedis()", "Entered p_serviceTypes: "+key);
        }
        String methodName ="getUserProductWalletMappingCache";
        List<UserProductWalletMappingVO> mappingVO = null;
        Jedis jedis = null;
		 try {
	        RedisActivityLog.log("UserProductWalletMappingCache->getUserProductWalletMappingCache->Start");
			jedis = RedisConnectionPool.getPoolInstance().getResource();
			String json = jedis.get(key);
			Type classType = new TypeToken<List<UserProductWalletMappingVO>>() {}.getType();
			if(json != null)
			  mappingVO=gson.fromJson(json, classType);
	         RedisActivityLog.log("UserProductWalletMappingCache->getUserProductWalletMappingCache->End");
		 }catch(JedisConnectionException je){
			LOG.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	        LOG.errorTrace(methodName, je);
           EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "UserProductWalletMappingCache[" + methodName +"]", "", "", "", "JedisConnectionException :" + je.getMessage());
		}catch(NoSuchElementException  ex){
			LOG.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	        LOG.errorTrace(methodName, ex);
           EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "UserProductWalletMappingCache[" + methodName +"]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		}catch (Exception e) {
			LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
	        LOG.errorTrace(methodName, e);
           EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "UserProductWalletMappingCache[" + methodName +"]", "", "", "", "Exception :" + e.getMessage());
		}
    	 finally {
    		 if(jedis != null ) {
    			 jedis.close();
    		 }
    	   }
		return mappingVO;
	}
}
