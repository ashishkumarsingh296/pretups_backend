package com.btsl.pretups.master.businesslogic;

/*
 * @(#)LocaleMasterCache.java
 * Copyright(c) 2005, Bharti Telesoft Ltd.
 * All Rights Reserved
 * ------------------------------------------------------------------------------
 * -------------------
 * Author Date History
 * ------------------------------------------------------------------------------
 * -------------------
 * Gurjeet Singh Nov 04, 2005 Initital Creation
 * Ankit Zindal Nov 20,2006 ChangeID=LOCALEMASTER
 * ------------------------------------------------------------------------------
 * -------------------
 * Cache for locale
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

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
import com.btsl.util.MessagesCaches;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheLoader.InvalidCacheLoadException;
import com.google.common.cache.LoadingCache;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class LocaleMasterCache  implements Runnable{

    public void run() {
        try {
            Thread.sleep(50);
            refreshLocaleMasterCache();
            MessagesCaches.load(LocaleMasterCache.getLocaleList());
        } catch (Exception e) {
        	 _log.error("LocaleMasterCache init() Exception ", e);
        }
    }
    private static Log _log = LogFactory.getLog(LocaleMasterCache.class.getName());
    private static HashMap<String,Locale> _localeMasterMap = new HashMap<String,Locale>();
    private static HashMap<Locale,LocaleMasterVO> _localeMasterDetailsMap = new HashMap<Locale,LocaleMasterVO>();
    private static LocaleMasterDAO _localeMasterDAO = new LocaleMasterDAO();
    private static String redisEnable = BTSLUtil.NullToString(Constants.getProperty("REDIS_ENABLE"));
    private static final String hKeyLocaleMasterMap = "LocaleMasterMap";
    private static final String hKeyLocaleMasterDetailsMap = "LocaleMasterDetailsMap";
    private static  int keyTimer = Integer.parseInt(Constants.getProperty("KEYTIMER"));
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss:mss").create();

    private static LoadingCache<String,Locale>  localeMasterCacheMap = CacheBuilder.newBuilder()
	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)     
	    .build(new CacheLoader<String, Locale>(){
			@Override
			public Locale load(String key) throws Exception {
				return getLocaleMastertObjectFromRedis(key);
			}
	     });

    private static LoadingCache<String,LocaleMasterVO>  localeMasterDetailObjCacheMap = CacheBuilder.newBuilder()
    	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)     
    	    .build(new CacheLoader<String, LocaleMasterVO>(){
    			@Override
    			public LocaleMasterVO load(String key) throws Exception {
    				return getLocaleMastertDetailObjectFromRedis(key);
    			}
    	     });

    private static LoadingCache<String,HashMap<Locale,LocaleMasterVO>>  localeMasterDetailCacheMap = CacheBuilder.newBuilder()
    	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)     
    	    .build(new CacheLoader<String, HashMap<Locale,LocaleMasterVO>>(){
    			@Override
    			public HashMap<Locale,LocaleMasterVO> load(String key) throws Exception {
    				return getLocaleMasterDetailFromRedis(key);
    			}
    	     });

    
    public static void refreshLocaleMasterCache() {
        final String methodName = "refreshLocaleMasterCache";
        if (_log.isDebugEnabled()) {
            _log.debug("refreshLocaleMasterCache", "Entered: ");
        }
        try {
            HashMap<String,Locale> tempMap = null;
            // String loadType=null; Not used so comment by ankit Jindal
            if (_log.isDebugEnabled()) {
                _log.debug("refreshLocaleMasterCache", " Before loading:" + _localeMasterMap);
            }
            tempMap = _localeMasterDAO.loadLocaleMasterCache();
            if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
            Jedis jedis = null; 
            try {
                RedisActivityLog.log("LocaleMasterCache->refreshLocaleMasterCache->Start");
   	   		    jedis = RedisConnectionPool.getPoolInstance().getResource();
   	   		    Pipeline pipeline = jedis.pipelined();
  				//If key is already present in redis db do not reload
  				if(!jedis.exists(hKeyLocaleMasterDetailsMap)) {
  					HashMap<Locale,LocaleMasterVO> _localeMasterDetailsMap1 = _localeMasterDAO.loadLocaleDetailsAtStartUp(); 				 
  	  				for (Entry<Locale, LocaleMasterVO> entry : _localeMasterDetailsMap1.entrySet())  {
  	  						pipeline.hset(hKeyLocaleMasterDetailsMap,  gson.toJson(entry.getKey()), gson.toJson(entry.getValue()));
  	  				  }
  				} 
				for (Entry<String, Locale> entry : tempMap.entrySet())  {
				      pipeline.hset(hKeyLocaleMasterMap, entry.getKey(), gson.toJson(entry.getValue()));
				} 
				pipeline.sync();
	   	   		RedisActivityLog.log("LocaleMasterCache->refreshLocaleMasterCache->End");
   	   		 }catch(JedisConnectionException je){
				_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
   		        _log.errorTrace(methodName, je);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterCache[refreshLocaleMasterCache]", "", "", "", "JedisConnectionException :" + je.getMessage());
			}catch(NoSuchElementException  ex){
				_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
   		        _log.errorTrace(methodName, ex);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterCache[refreshLocaleMasterCache]", "", "", "", "NoSuchElementException :" + ex.getMessage());
			}catch (Exception e){
				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
   		        _log.errorTrace(methodName, e);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterCache[refreshLocaleMasterCache]", "", "", "", "Exception :" + e.getMessage());
			}finally{
				if(jedis != null)
					jedis.close();
			}
      	  } else {	 
      		 _localeMasterDetailsMap = _localeMasterDAO.loadLocaleDetailsAtStartUp();
             compareMaps(_localeMasterMap, tempMap);
             _localeMasterMap = tempMap;
      	  }
          if (_log.isDebugEnabled()) {
              _log.debug("refreshLocaleMasterCache", " After loading:" + _localeMasterMap.size());
          }
        } catch (Exception e) {
            _log.error("refreshLocaleMasterCache", "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
        }
    }

    /**
     * To compare Maps
     * 
     * @param p_previousMap
     * @param p_currentMap
     */
    private static void compareMaps(HashMap p_previousMap, HashMap p_currentMap) {
        final String METHOD_NAME = "compareMaps";
        if (_log.isDebugEnabled()) {
            _log.debug("compareMaps()", "Entered PreviousMap " + p_previousMap + "  Current Map" + p_currentMap);
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

            boolean isNewAdded = false;
            while (iterator != null && iterator.hasNext()) {
                String key = (String) iterator.next();
                Locale prevValue = (Locale) p_previousMap.get(key);
                Locale currValue = (Locale) p_currentMap.get(key);

                if (prevValue != null && currValue == null) {
                    isNewAdded = true;
                    CacheOperationLog.log("LocaleMasterCache", BTSLUtil.formatMessage("Delete", key, prevValue.toString()));
                } else if (prevValue == null && currValue != null) {
                    CacheOperationLog.log("LocaleMasterCache", BTSLUtil.formatMessage("Add", key, currValue.toString()));
                } else if (prevValue != null && currValue != null) {
                    if (!currValue.equals(prevValue)) {
                        CacheOperationLog.log("LocaleMasterCache", BTSLUtil.formatMessage("Modify", key, "From :" + prevValue + " To:" + currValue));
                    }
                }
            }

            // Note: this case arises when same number of element added and
            // deleted as well
            if (p_previousMap.size() == p_currentMap.size() && isNewAdded) {
                HashMap tempMap = new HashMap(p_currentMap);
                while (copiedIterator.hasNext()) {
                    tempMap.remove((String) copiedIterator.next());
                }

                Iterator iterator2 = tempMap.keySet().iterator();
                while (iterator2.hasNext()) {
                    Locale mappingVO = (Locale) p_currentMap.get(iterator2.next());
                    CacheOperationLog.log("LocaleMasterCache", BTSLUtil.formatMessage("Add", (String) iterator2.next(), mappingVO.toString()));
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
     * Method to get the Locale from cache
     * 
     * @param p_key
     * @return Locale
     */
    public static Locale getLocaleFromCodeDetails(String p_key) {
    	Locale localeObject = null;
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        	try{
        		localeObject = localeMasterCacheMap.get(p_key);
        	}catch (InvalidCacheLoadException e) { 
	    		 _log.error("getLocaleFromCodeDetails", PretupsI.EXCEPTION + e.getMessage());
	   		     _log.errorTrace("getLocaleFromCodeDetails", e);
	   	        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterCache[getLocaleFromCodeDetails]", "", "", "", "InvalidCacheLoadException :" + e.getMessage());	
           }catch(ExecutionException ex){
	         	_log.error("getLocaleFromCodeDetails", PretupsI.EXCEPTION + ex.getMessage());
	   	        _log.errorTrace("getLocaleFromCodeDetails", ex);
               EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterCache[getLocaleFromCodeDetails]", "", "", "", "ExecutionException :" + ex.getMessage());
           } catch (Exception e) {
               _log.errorTrace("Exception in getLocaleFromCodeDetails() ", e);
               EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterCache[getLocaleFromCodeDetails]", "", "", "", "Exception:" + e.getMessage());
           }
        } else {
    		localeObject = (Locale) _localeMasterMap.get(p_key);
        }
        return localeObject;
    }

    /**
     * Method to get the Locale details from cache
     * ChangeID=LOCALEMASTER
     * 
     * @param p_key
     * @return LocaleMasterVO
     */
    public static LocaleMasterVO getLocaleDetailsFromlocale(Locale p_key) {
        String methodname = "getLocaleDetailsFromlocale";
    	if (_log.isDebugEnabled()) {
            _log.debug("getLocaleDetailsFromlocale()", "Entered p_key=" + p_key.toString());
        }
        LocaleMasterVO localeMasterVO = null;
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        	try{
        		localeMasterVO = localeMasterDetailObjCacheMap.get(gson.toJson(p_key));
        	}catch (InvalidCacheLoadException e) { 
	    		 _log.error(methodname, PretupsI.EXCEPTION + e.getMessage());
	   		     _log.errorTrace(methodname, e);
	   	        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterCache[getLocaleDetailsFromlocale]", "", "", "", "InvalidCacheLoadException :" + e.getMessage());	
           }catch(ExecutionException ex){
	         	_log.error(methodname ,PretupsI.EXCEPTION + ex.getMessage());
	   	        _log.errorTrace(methodname, ex);
               EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterCache[getLocaleDetailsFromlocale]", "", "", "", "ExecutionException :" + ex.getMessage());
           } catch (Exception e) {
               _log.errorTrace("Exception in getLocaleFromCodeDetails() ", e);
               EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterCache[getLocaleDetailsFromlocale]", "", "", "", "Exception:" + e.getMessage());
           }
        } else {
    		localeMasterVO = (LocaleMasterVO) _localeMasterDetailsMap.get(p_key);
        }
        return localeMasterVO;
    }

    /**
     * Method to get the Locales applicable for SMS.
     * ChangeID=LOCALEMASTER
     * 
     * @return ArrayList
     */
    public static ArrayList<Locale> getLocaleListForSMS() {
    	String methodName = "getLocaleListForSMS";
        ArrayList<Locale> list = new ArrayList<Locale>();
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
     	try {
  	        HashMap<Locale, LocaleMasterVO> cachedMap = localeMasterDetailCacheMap.get(hKeyLocaleMasterDetailsMap);
  	        Locale key= null;
  	        LocaleMasterVO localeVO = null;
  	        for (Entry<Locale, LocaleMasterVO> entry : cachedMap.entrySet())  {
              key = entry.getKey();
              localeVO = entry.getValue();
              if (PretupsI.SMS_LOCALE.equals(localeVO.getType()) || PretupsI.BOTH_LOCALE.equals(localeVO.getType())) {
                  list.add(key);
              }
			}
       }catch (InvalidCacheLoadException e) { 
			_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
		     _log.errorTrace(methodName, e);
	        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterCache[getLocaleListForSMS]", "", "", "", "InvalidCacheLoadException :" + e.getMessage());	
       }catch(ExecutionException ex){
     		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	        _log.errorTrace(methodName, ex);
           EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterCache[getLocaleListForSMS]", "", "", "", "ExecutionException :" + ex.getMessage());
       }catch (Exception e) {
		 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
		 	_log.errorTrace(methodName, e);
	        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterCache[getLocaleListForSMS]", "", "", "", "Exception :" + e.getMessage());
		 }
   	} else {
        if (_localeMasterDetailsMap != null && _localeMasterDetailsMap.size() > 0) {
            Iterator iterator = _localeMasterDetailsMap.keySet().iterator();
  	        LocaleMasterVO localeVO = null;
            while (iterator.hasNext()) {
                Locale key = (Locale) iterator.next();
                localeVO = (LocaleMasterVO) _localeMasterDetailsMap.get(key);
                if (PretupsI.SMS_LOCALE.equals(localeVO.getType()) || PretupsI.BOTH_LOCALE.equals(localeVO.getType())) {
                    list.add(key);
                }
            }
        }
   	}
        return list;
    }

    /**
     * Method to get the Locales applicable for WEB.
     * ChangeID=LOCALEMASTER
     * 
     * @return ArrayList
     */
    public static ArrayList<Locale> getLocaleListForWEB() {
        ArrayList<Locale> list = new ArrayList<Locale>();
        ArrayList<LocaleMasterVO> templist = new ArrayList<LocaleMasterVO>();
        String methodName = "getLocaleListForWEB";
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
         try {
      	        HashMap<Locale, LocaleMasterVO> cachedMap = localeMasterDetailCacheMap.get(hKeyLocaleMasterDetailsMap);
      	        LocaleMasterVO localeVO = null;
      	        for (Entry<Locale, LocaleMasterVO> entry : cachedMap.entrySet())  {
	              localeVO = entry.getValue();
	               if (PretupsI.WEB_LOCALE.equals(localeVO.getType()) || PretupsI.BOTH_LOCALE.equals(localeVO.getType())) {
	    	 	        templist.add(localeVO);
	    	 	      }
    			}
    			Collections.sort(templist);
     	            for (int i = 0, j = templist.size(); i < j; i++) {
     	                localeVO = (LocaleMasterVO) templist.get(i);
     	                list.add(new Locale(localeVO.getLanguage(), localeVO.getCountry()));
     	            }
           }catch (InvalidCacheLoadException e) { 
    			_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
    		     _log.errorTrace(methodName, e);
    	        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterCache[getLocaleListForWEB]", "", "", "", "InvalidCacheLoadException :" + e.getMessage());	
           }catch(ExecutionException ex){
         		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
    	        _log.errorTrace(methodName, ex);
               EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterCache[getLocaleListForWEB]", "", "", "", "ExecutionException :" + ex.getMessage());
           }catch (Exception e) {
    		 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
    		 	_log.errorTrace(methodName, e);
    	        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterCache[getLocaleListForWEB]", "", "", "", "Exception :" + e.getMessage());
    		 }
       	
        }else {
        if (_localeMasterDetailsMap != null && _localeMasterDetailsMap.size() > 0) {
            Iterator iterator = _localeMasterDetailsMap.keySet().iterator();
            LocaleMasterVO localeVO = null;
            while (iterator.hasNext()) {
                Locale key = (Locale) iterator.next();
                localeVO = (LocaleMasterVO) _localeMasterDetailsMap.get(key);
                if (PretupsI.WEB_LOCALE.equals(localeVO.getType()) || PretupsI.BOTH_LOCALE.equals(localeVO.getType())) {
                    templist.add(localeVO);
                }
            }
            Collections.sort(templist);
            for (int i = 0, j = templist.size(); i < j; i++) {
                localeVO = (LocaleMasterVO) templist.get(i);
                list.add(new Locale(localeVO.getLanguage(), localeVO.getCountry()));
            }
        }
  	}  
        return list;
    }

    /**
     * Method to get the Locales applicable for WEB,SMS and BOTH.
     * ChangeID=LOCALEMASTER
     * 
     * @return ArrayList
     */
    public static ArrayList<Locale> getLocaleList() throws Exception {
    	String methodName="getLocaleList";
        ArrayList<Locale> list = null;
	        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
            try {
      	        HashMap<Locale, LocaleMasterVO> cachedMap = localeMasterDetailCacheMap.get(hKeyLocaleMasterDetailsMap);
      	        Set<Locale> keySet = cachedMap.keySet();
      	        list = new ArrayList<Locale>(keySet);
            }catch (InvalidCacheLoadException e) { 
    			_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
    		     _log.errorTrace(methodName, e);
    	        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterCache[getLocaleListForWEB]", "", "", "", "InvalidCacheLoadException :" + e.getMessage());	
           }catch(ExecutionException ex){
         		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
    	        _log.errorTrace(methodName, ex);
               EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterCache[getLocaleListForWEB]", "", "", "", "ExecutionException :" + ex.getMessage());
           }catch (Exception e) {
    		 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
    		 	_log.errorTrace(methodName, e);
    	        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterCache[getLocaleListForWEB]", "", "", "", "Exception :" + e.getMessage());
    		 }
	        }
	        else {
                _localeMasterDetailsMap = _localeMasterDAO.loadLocaleDetailsAtStartUp();
	  		 if (_localeMasterDetailsMap != null && _localeMasterDetailsMap.size() > 0) {
	             Iterator iterator = _localeMasterDetailsMap.keySet().iterator();
	             list = new ArrayList<>();
	             while (iterator.hasNext()) {
	                 Locale key = (Locale) iterator.next();
	                 list.add(key);
	             }
	         }
	        }
	        if (_log.isDebugEnabled()) {
	            _log.debug("get list" , list);
	        }
        return list;
    }

    public static ArrayList getLocaleListForALL() {
        ArrayList<Locale> list = new ArrayList<Locale>();
        ArrayList<LocaleMasterVO> templist = new ArrayList<LocaleMasterVO>();
        String methodName = "getLocaleListForALL";
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
         try {
      	        HashMap<Locale, LocaleMasterVO> cachedMap = localeMasterDetailCacheMap.get(hKeyLocaleMasterDetailsMap);
      	        LocaleMasterVO localeVO = null;
      	        for (Entry<Locale, LocaleMasterVO> entry : cachedMap.entrySet())  {
	              localeVO = entry.getValue();
	              if (PretupsI.SMS_LOCALE.equals(localeVO.getType()) || PretupsI.WEB_LOCALE.equals(localeVO.getType()) || PretupsI.BOTH_LOCALE.equals(localeVO.getType())) {
 	                    templist.add(localeVO);
 	                }

    			}
      	      Collections.sort(templist);
              for (int i = 0, j = templist.size(); i < j; i++) {
                  localeVO = (LocaleMasterVO) templist.get(i);
                  list.add(new Locale(localeVO.getLanguage(), localeVO.getCountry()));
              }
           }catch (InvalidCacheLoadException e) { 
    			_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
    		     _log.errorTrace(methodName, e);
    	        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterCache[getLocaleListForALL]", "", "", "", "InvalidCacheLoadException :" + e.getMessage());	
           }catch(ExecutionException ex){
         		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
    	        _log.errorTrace(methodName, ex);
               EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterCache[getLocaleListForALL]", "", "", "", "ExecutionException :" + ex.getMessage());
           }catch (Exception e) {
    		 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
    		 	_log.errorTrace(methodName, e);
    	        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterCache[getLocaleListForALL]", "", "", "", "Exception :" + e.getMessage());
    		 }
       	
        } else {
        if (_localeMasterDetailsMap != null && _localeMasterDetailsMap.size() > 0) {
            Iterator iterator = _localeMasterDetailsMap.keySet().iterator();
            LocaleMasterVO localeVO = null;
            while (iterator.hasNext()) {
                Locale key = (Locale) iterator.next();
                localeVO = (LocaleMasterVO) _localeMasterDetailsMap.get(key);
                if (PretupsI.SMS_LOCALE.equals(localeVO.getType()) || PretupsI.WEB_LOCALE.equals(localeVO.getType()) || PretupsI.BOTH_LOCALE.equals(localeVO.getType())) {
                    templist.add(localeVO);
                }
            }
            Collections.sort(templist);
            for (int i = 0, j = templist.size(); i < j; i++) {
                localeVO = (LocaleMasterVO) templist.get(i);
                list.add(new Locale(localeVO.getLanguage(), localeVO.getCountry()));
            }
        }
 	  }
        return list;
    }
    
    /**
     * @param key
     * @return
     */
    public static Locale getLocaleMastertObjectFromRedis(String key) {
    	String methodName = "getLocaleMastertObjectFromRedis";
    	if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered key: " + key);
        }
    	Jedis jedis = null;
    	Locale localeObject = null;
		 try {
	        RedisActivityLog.log("LocaleMasterCache->getLocaleFromCodeDetails->Start");
			 jedis = RedisConnectionPool.getPoolInstance().getResource();
			 String json = jedis.hget(hKeyLocaleMasterMap, key);
			if(!BTSLUtil.isNullString(json))
				localeObject = gson.fromJson(json, Locale.class); 
			 RedisActivityLog.log("LocaleMasterCache->getLocaleFromCodeDetails->End");
		 }catch(JedisConnectionException je){
		 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
   		        _log.errorTrace(methodName, je);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterCache[getLocaleMastertObjectFromRedis]", "", "", "", "JedisConnectionException :" + je.getMessage());
		 }catch(NoSuchElementException  ex){
		 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
   		        _log.errorTrace(methodName, ex);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterCache[getLocaleMastertObjectFromRedis]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		 }catch (Exception e) {
		 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
		 	_log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterCache[getLocaleMastertObjectFromRedis]", "", "", "", "Exception :" + e.getMessage());
		 }
		 finally {
	        	if (jedis != null) {
	        	jedis.close();
	        	}
	        }
      return localeObject;
    }

    /**
     * @param key
     * @return
     */
    public static LocaleMasterVO getLocaleMastertDetailObjectFromRedis(String key) {
    	String methodName = "getLocaleMastertDetailObjectFromRedis";
    	if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered key: " + key);
        }
    	Jedis jedis = null;
    	LocaleMasterVO localeObject = null;
		 try {
	        RedisActivityLog.log("LocaleMasterCache->getLocaleMastertDetailObjectFromRedis->Start");
			 jedis = RedisConnectionPool.getPoolInstance().getResource();
			 String json = jedis.hget(hKeyLocaleMasterDetailsMap, key);
			if(!BTSLUtil.isNullString(json))
				localeObject = gson.fromJson(json, LocaleMasterVO.class); 
			 RedisActivityLog.log("LocaleMasterCache->getLocaleMastertDetailObjectFromRedis->End");
		 }catch(JedisConnectionException je){
		 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
   		        _log.errorTrace(methodName, je);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterCache[getLocaleMastertDetailObjectFromRedis]", "", "", "", "JedisConnectionException :" + je.getMessage());
		 }catch(NoSuchElementException  ex){
		 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
   		        _log.errorTrace(methodName, ex);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterCache[getLocaleMastertDetailObjectFromRedis]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		 }catch (Exception e) {
		 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
		 	_log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterCache[getLocaleMastertDetailObjectFromRedis]", "", "", "", "Exception :" + e.getMessage());
		 }
		 finally {
	        	if (jedis != null) {
	        	jedis.close();
	        	}
	        }
      return localeObject;
    }

    /**
     * @param key
     * @return
     */
    public static  HashMap<Locale,LocaleMasterVO> getLocaleMasterDetailFromRedis(String key) {
    	String methodName = "getLocaleMasterDetailFromRedis";
    	if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered key: " + key);
        }
    	Jedis jedis = null;
		HashMap<Locale,LocaleMasterVO> masterDetailMap =null;
    	try {
	        RedisActivityLog.log("LocaleMasterCache->getLocaleMasterDetailFromRedis->Start");
			 jedis = RedisConnectionPool.getPoolInstance().getResource();
			 Map<String, String> cachedMap = jedis.hgetAll(key);
			 Locale locale = null;
			 LocaleMasterVO localeVO = null;
			 masterDetailMap = new HashMap<Locale,LocaleMasterVO>();
			 for (Entry<String,String> entry:cachedMap.entrySet()) {
				 locale =gson.fromJson(entry.getKey(), Locale.class);
				 localeVO=gson.fromJson(entry.getValue(), LocaleMasterVO.class);
				 masterDetailMap.put(locale,localeVO);
			}
			RedisActivityLog.log("LocaleMasterCache->getLocaleMasterDetailFromRedis->End");
		 }catch(JedisConnectionException je){
		 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
   		        _log.errorTrace(methodName, je);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterCache[getLocaleMasterDetailFromRedis]", "", "", "", "JedisConnectionException :" + je.getMessage());
		 }catch(NoSuchElementException  ex){
		 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
   		        _log.errorTrace(methodName, ex);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterCache[getLocaleMasterDetailFromRedis]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		 }catch (Exception e) {
		 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
		 	_log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LocaleMasterCache[getLocaleMasterDetailFromRedis]", "", "", "", "Exception :" + e.getMessage());
		 }
		 finally {
	        	if (jedis != null) {
	        	jedis.close();
	        	}
	        	if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Exited method");
	            }
	        }
      return masterDetailMap;
    }

 }
