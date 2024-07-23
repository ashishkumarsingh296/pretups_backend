package com.btsl.util;

/**
 * @(#)MessagesCaches.java
 *                         Copyright(c) 2005, Bharti Telesoft Int. Public Ltd.
 *                         All Rights Reserved
 *                         This class is used to store System Preferences for
 *                         Pretups System.
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Author Date History
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Abhijit Chauhan June 10,2005 Initial Creation
 *                         ----------------------------------------------------
 *                         --------------------------------------------
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;

public class MessagesCaches implements Runnable {

    private static Log _log = LogFactory.getLog(MessagesCaches.class.getName());
    private static HashMap _map = new HashMap();
    private static String redisEnable = BTSLUtil.NullToString(Constants.getProperty("REDIS_ENABLE"));
    private static final String hKeyMessagesCachesMap = "MessagesCaches";
    //private static JedisPool jedisPool = null;
    /*static{
    	if(PretupsI.REDIS_ENABLE.equals(redisEnable.trim())){	
    		jedisPool = RedisConnectionPool.getPoolInstance();
    	} 
    }
   */
    public void run() {
        try {
            Thread.sleep(50);
            load(LocaleMasterCache.getLocaleList());
        } catch (Exception e) {
        	 _log.error("MessagesCaches init() Exception ", e);
        }
    }

    /**
     * Get a string from the underlying resource bundle.
     * 
     * @param key
     */
    public static void load(ArrayList localeList) {
    	final String methodName = "load";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        
            try {
            	/*if (!PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
       			 try (Jedis jedis = jedisPool.getResource()) {
       				//Get the byte array for the hash key
       				byte[] hKeyBytes = RedisUtil.serialize(hKeyMessagesCachesMap);
       				
       				for (int i = 0, j = localeList.size(); i < j; i++) {
    				      jedis.hset(hKeyBytes, RedisUtil.serialize((Locale)localeList.get(i)), RedisUtil.serialize(new MessagesCache((Locale) localeList.get(i))));
            			}
       				jedis.close();
        		 }
      		 } else {	 */
      			for (int i = 0, j = localeList.size(); i < j; i++) {
      			  _map.put(localeList.get(i), new MessagesCache((Locale) localeList.get(i)));
      			  }
      	        //}
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

    public static void reload(ArrayList localeList) {
    	final String methodName = "reload";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
        try {
        	load(localeList);
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

    /*
     * (non-Javadoc)
     * 
     * @see java.util.HashMap#get(java.lang.Object)
     */
    public static MessagesCache get(Object p_locale) {
    	if (_log.isDebugEnabled()) {
			_log.debug("get", "p_locale: " + p_locale);
		}
   /*     if (!PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
			 try (Jedis jedis = jedisPool.getResource()) {
				 MessagesCache localeObject  = null;
				//Get the byte array for the hash key
				byte[] hKeyBytes = RedisUtil.serialize(hKeyMessagesCachesMap);
				if(jedis.hget(hKeyBytes, RedisUtil.serialize((Locale) p_locale)) != null)
				localeObject = (MessagesCache) RedisUtil.deserialize(jedis.hget(hKeyBytes, RedisUtil.serialize((Locale)p_locale)));
	            jedis.close();
	        	if (_log.isDebugEnabled()) {
	        			_log.debug("get", "localeObject: " + localeObject);
	        		}

	            return localeObject;
			 }
   	   } else {
*/   	      return (MessagesCache) _map.get(p_locale);
//       }
       
    }
}
