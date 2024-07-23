package com.btsl.pretups.gateway.businesslogic;

import java.lang.reflect.Type;
import java.util.ArrayList;
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
import com.btsl.pretups.domain.businesslogic.CategoryReqGtwTypeDAO;
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

public class MessageGatewayForCategoryCache implements Runnable {

    private static Log _log = LogFactory.getLog(MessageGatewayForCategoryCache.class.getName());
    private static HashMap<String, ArrayList<String>> _messageGatewayForCategoryMap = new HashMap<String, ArrayList<String>>();
    private static final String hKeyCategoryCache = "messageGatewayForCategory";
    private static String redisEnable = BTSLUtil.NullToString(Constants.getProperty("REDIS_ENABLE"));
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss:mss").create();
    private static final int keyTimer = Integer.parseInt(Constants.getProperty("KEYTIMER"));
    
    private static LoadingCache<String,ArrayList<String>> messageKeywordMemo = CacheBuilder.newBuilder()
    	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)
    	    .build(new CacheLoader<String,ArrayList<String>>(){
    			@Override
    			public ArrayList<String> load(String key) throws Exception {
    				return getMessageGatewayMapCacheFromRedis(key);
    			}
    	     });
  
    /**
     * Description : This method loads the message gateway for category cache at
     * startup
     * Method : loadMeassageGatewayForCategoryMapAtStartup
     * 
     * @return
     * @throws Exception 
     */
    public void run() {
        try {
            Thread.sleep(50);
            loadMeassageGatewayForCategoryMapAtStartup();
        } catch (Exception e) {
        	 _log.error("MessageGatewayForCategoryCache init() Exception ", e);
        }
    }
    public static void loadMeassageGatewayForCategoryMapAtStartup() throws BTSLBaseException {
    	final String methodName = "loadMeassageGatewayForCategoryMapAtStartup";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
    	Jedis jedis = null;
        try{
        	if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
          	    RedisActivityLog.log("MessageGatewayForCategoryCache->loadMeassageGatewayForCategoryMapAtStartup->Start");
          	    jedis = RedisConnectionPool.getPoolInstance().getResource();
          	    Pipeline pipeline = jedis.pipelined();
          	    //If key is already present in redis db do not reload
				if(!jedis.exists(hKeyCategoryCache)) {
    			     HashMap<String, ArrayList<String>> messageGatewayForCategoryMap = loadMapping();
    				 for (Entry<String, ArrayList<String>> entry : messageGatewayForCategoryMap.entrySet())  {
    					 pipeline.hset(hKeyCategoryCache, entry.getKey(), gson.toJson(entry.getValue()));
    			    } 
    			    pipeline.sync();
    				}
       	   	   RedisActivityLog.log("MessageGatewayForCategoryCache->loadMeassageGatewayForCategoryMapAtStartup->End");
       		   } else {
                 _messageGatewayForCategoryMap = loadMapping();
       		}
        }
        catch(BTSLBaseException be) {
        	_log.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
        	_log.errorTrace(methodName, be);
        	throw be;
        }catch(JedisConnectionException je){
        	_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	        _log.errorTrace(methodName, je);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayForCategoryCache[loadMeassageGatewayForCategoryMapAtStartup]", "", "", "", "JedisConnectionException :" + je.getMessage());
		 }catch(NoSuchElementException  ex){
	 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	        _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayForCategoryCache[loadMeassageGatewayForCategoryMapAtStartup]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		 }catch (Exception e) {
			_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
	    	_log.errorTrace(methodName, e);
		 }finally {
	    	if (jedis != null) {
	    	jedis.close();
	    	}
	    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.EXITED);
    	}
      }
    }

    /**
     * Description : This method loads the message gateway for category mapping
     * Method : loadMapping
     * 
     * @return HashMap
     * @throws Exception 
     */
    private static HashMap<String, ArrayList<String>> loadMapping() throws BTSLBaseException {
    	final String methodName = "loadMapping";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        HashMap<String, ArrayList<String>> messageGatewayForCategoryMap = null;
        CategoryReqGtwTypeDAO categoryReqGtwTypeDAO = null;
        try {
            categoryReqGtwTypeDAO = new CategoryReqGtwTypeDAO();
            messageGatewayForCategoryMap = categoryReqGtwTypeDAO.loadMessageGatewayTypeListForCategory();
        }
        catch(BTSLBaseException be) {
        	_log.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
        	_log.errorTrace(methodName, be);
        	throw be;
        }
        catch (Exception e){
        	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
        	_log.errorTrace(methodName, e);
        	
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED + ". messageGatewayForCategoryMap.size()=" + messageGatewayForCategoryMap.size());
        	}
        }
        return messageGatewayForCategoryMap;
    }

    /**
     * get the messagegatewayforcategory list from cache
     * 
     * @param p_categoryCode
     * @return ArrayList<String>
     */
    public static ArrayList<String> getMessagegatewayforcategoryList(String p_categoryCode) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("getMessagegatewayforcategoryList()", "entered p_categoryCode: " + p_categoryCode);
        }
        final String METHOD_NAME = "getMessagegatewayforcategoryList";
        ArrayList<String> messageGatewayForCategoryList = null;
        try {
        	
        	if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        		messageGatewayForCategoryList=messageKeywordMemo.get(p_categoryCode);
        	} else {
                messageGatewayForCategoryList = (ArrayList<String>) _messageGatewayForCategoryMap.get(p_categoryCode);
            }
        	
            if (messageGatewayForCategoryList == null) {
                throw new BTSLBaseException("MessageGatewayForCategoryCache", "getMessagegatewayforcategoryList", PretupsErrorCodesI.ERROR_NOTFOUND_MESSAGE_GATEWAY_FOR_CATEGORY, 0, null);
            }
        } catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        }catch (ExecutionException e) {
			_log.error("getMessagegatewayforcategoryList", PretupsI.EXCEPTION + e.getMessage());
	        _log.errorTrace("getMessagegatewayforcategoryList", e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MessageGatewayForCategoryCache[getMessagegatewayforcategoryList]", "", "", "", "Exception :" + e.getMessage());
            throw new BTSLBaseException("MessageGatewayForCategoryCache", "getMessagegatewayforcategoryList", e.getMessage());
        }catch(InvalidCacheLoadException  ex){
 			_log.error("getMessagegatewayforcategoryList", PretupsI.EXCEPTION + ex.getMessage());
	        _log.errorTrace("getMessagegatewayforcategoryList", ex);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MessageGatewayForCategoryCache[getMessagegatewayforcategoryList]", "", "", "", "InvalidCacheLoadException :" + ex.getMessage());
  		 } catch (Exception e) {
            _log.error("getMessagegatewayforcategoryList", "SQLException " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayForCategoryCache[getMessagegatewayforcategoryList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("MessageGatewayForCategoryCache", "getMessagegatewayforcategoryList", "error.general.processing");
        }
        if (_log.isDebugEnabled()) {
            _log.debug("getMessagegatewayforcategoryList()", "exited messageGatewayForCategoryList: " + messageGatewayForCategoryList);
        }
        return messageGatewayForCategoryList;
    }
    public static void updateMeassageGatewayForCategoryMap() throws BTSLBaseException {
    	final String methodName = "updateMeassageGatewayForCategoryMap";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
    	Jedis jedis = null;
        try{
        	if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
       	   	 RedisActivityLog.log("MessageGatewayForCategoryCache->updateMeassageGatewayForCategoryMap->Start");
       	     jedis = RedisConnectionPool.getPoolInstance().getResource();
       	     Pipeline pipeline = jedis.pipelined();
		     pipeline.del(hKeyCategoryCache);
			 HashMap<String, ArrayList<String>> messageGatewayForCategoryMap = loadMapping();
			 for (Entry<String, ArrayList<String>> entry : messageGatewayForCategoryMap.entrySet())  {
			    pipeline.hset(hKeyCategoryCache, entry.getKey(), gson.toJson(entry.getValue()));
			 }
			 pipeline.sync();
    	   	 RedisActivityLog.log("MessageGatewayForCategoryCache->updateMeassageGatewayForCategoryMap->End");
       		} else {
                 _messageGatewayForCategoryMap = loadMapping();
       		}
        }
        catch(BTSLBaseException be) {
        	_log.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
        	_log.errorTrace(methodName, be);
        	throw be;
        }catch(JedisConnectionException je){
        	_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	        _log.errorTrace(methodName, je);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayForCategoryCache[loadMeassageGatewayForCategoryMapAtStartup]", "", "", "", "JedisConnectionException :" + je.getMessage());
		 }catch(NoSuchElementException  ex){
	 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	        _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MessageGatewayForCategoryCache[loadMeassageGatewayForCategoryMapAtStartup]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		 }catch (Exception e) {
			_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
	        _log.errorTrace(methodName, e);
		 }finally {
	    	if (jedis != null) {
	    	jedis.close();
	    	}
	    	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED);
        	}
    	}
      }
    
    public static ArrayList<String> getMessageGatewayMapCacheFromRedis(String key){
        if (_log.isDebugEnabled()) {
            _log.debug("getMessageGatewayMapCacheFromRedis()", "Entered p_serviceTypes: "+key);
        }
        String methodName ="getMessageGatewayMapCacheFromRedis";
        ArrayList<String> mappingVO = null;
        Jedis jedis = null;
		 try {
	        RedisActivityLog.log("MessageGatewayForCategoryCache->getMessageGatewayMapCacheFromRedis->Start");
			jedis = RedisConnectionPool.getPoolInstance().getResource();
			String json = jedis.hget(hKeyCategoryCache,key);
			Type classType = new TypeToken< ArrayList<String>>() {}.getType();
			if(json != null)
			  mappingVO=gson.fromJson(json, classType);
	         RedisActivityLog.log("MessageGatewayForCategoryCache->getMessageGatewayMapCacheFromRedis->End");
		 }catch(JedisConnectionException je){
			_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	        _log.errorTrace(methodName, je);
           EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MessageGatewayForCategoryCache[" + methodName +"]", "", "", "", "JedisConnectionException :" + je.getMessage());
		}catch(NoSuchElementException  ex){
			_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	        _log.errorTrace(methodName, ex);
           EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MessageGatewayForCategoryCache[" + methodName +"]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		}catch (Exception e) {
			_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
	        _log.errorTrace(methodName, e);
           EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MessageGatewayForCategoryCache[" + methodName +"]", "", "", "", "Exception :" + e.getMessage());
		}finally {
    		 if(jedis != null ) {
    			 jedis.close();
    		 }
    	   }
		 return mappingVO;
		 }

}
