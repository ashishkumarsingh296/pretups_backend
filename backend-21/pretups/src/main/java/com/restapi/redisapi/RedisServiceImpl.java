package com.restapi.redisapi;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.springframework.stereotype.Service;

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
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.SubLookUpVO;
import com.btsl.pretups.master.businesslogic.SubLookupsCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.redis.pool.RedisConnectionPool;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

@Service("RedisServiceI")
public class RedisServiceImpl implements RedisServiceI {
	public static final Log log = LogFactory.getLog(RedisServiceImpl.class.getName());
	public static final String  classname = "RedisServiceImpl";

	@Override
	public LookupsCacheResponse lookupsCache(Connection con, LookupsCacheResponse response,
			HttpServletResponse responseSwag) {

		final String methodName = "lookupsCache";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered");
		}

		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);

		try {

			Map map = null;
			String redisEnable = BTSLUtil.NullToString(Constants.getProperty("REDIS_ENABLE"));
			if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
				map = getCacheMapFromRedis("lookupMapCacheClient");
			} else {
				map = LookupsCache.get_lookupMap();
			}
			response.setMap(map);
			response.setStatus(PretupsI.RESPONSE_SUCCESS);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(new Locale(lang, country), PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);
		} catch (BTSLBaseException be) {
			log.error("", "Exceptin:e=" + be);
			log.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(new Locale(lang, country), be.getMessageKey(), null);
			response.setMessageCode(be.getMessageKey());
			response.setMessage(msg);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		} catch (Exception e) {
			response.setStatus(PretupsI.RESPONSE_FAIL);
			response.setMessageCode(e.getMessage());
			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
		} finally {
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting");
			}
		}
		return response;
	}
	
	@Override
	public SublookupsCacheResponse sublookupsCache(Connection con, SublookupsCacheResponse response,
			HttpServletResponse responseSwag) {
		final String methodName = "sublookupsCache";
	     if (log.isDebugEnabled()) {
	         log.debug(methodName, "Entered");
	     }
	     
	     String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		 String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
	     
	     try {
	    	 HashMap<String,ArrayList<SubLookUpVO>>  map = null;
	    	 map = SubLookupsCache.getSubLookupMap();
	    	 response.setMap(map);
	    	 response.setStatus(PretupsI.RESPONSE_SUCCESS);
			 response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			 String resmsg = RestAPIStringParser.getMessage(new Locale(lang,country),
						PretupsErrorCodesI.SUCCESS, null);
			 response.setMessage(resmsg);
	    	 
	     }catch (Exception e) {
			 	response.setStatus(PretupsI.RESPONSE_FAIL);
				response.setMessageCode(e.getMessage());
				responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
			}
	     finally {
		        if (log.isDebugEnabled()) {
		            log.debug(methodName, "Exiting");
		        	}
		    	}	
	     
		return response;
	}

	@Override
	public PreferenceCacheResponse preferenceCache(Connection con, PreferenceCacheResponse response,
			HttpServletResponse responseSwag) {
		final String methodName = "preferenceCache";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered");
		}
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);

		try {
			Map map = null;
			String redisEnable = BTSLUtil.NullToString(Constants.getProperty("REDIS_ENABLE"));
			if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
				map = getCacheMapFromRedis("prefrenceMapCacheClient");
			} else {
				map = PreferenceCache.get_preferenceMapRedisBackup();
			}

			response.setMap(map);
			response.setStatus(PretupsI.RESPONSE_SUCCESS);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(new Locale(lang, country), PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);

		} catch (BTSLBaseException be) {
			log.error("", "Exceptin:e=" + be);
			log.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(new Locale(lang, country), be.getMessageKey(), null);
			response.setMessageCode(be.getMessageKey());
			response.setMessage(msg);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		} catch (Exception e) {
			response.setStatus(PretupsI.RESPONSE_FAIL);
			response.setMessageCode(e.getMessage());
			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
		} finally {
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting");
			}
		}
		return response;
	}
	
		public Map getCacheMapFromRedis(String key) throws BTSLBaseException{
			final String METHOD_NAME = "getCacheMapFromRedis";
			if (log.isDebugEnabled()) {
	             log.debug(METHOD_NAME, "Entered key: "+key);
	         }
			Jedis jedis = null;
			Map map = null;
			try {
				jedis = RedisConnectionPool.getPoolInstance().getResource();
				map = jedis.hgetAll(key);
			} catch (JedisConnectionException je) {
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RedisServiceImpl[" + METHOD_NAME + "]", "", "", "", "JedisConnectionException :" + je.getMessage());
   		        throw new BTSLBaseException(PreferenceCache.class.getName(), METHOD_NAME,je.getMessage());
			} finally {
				if (jedis != null) {
					jedis.close();
				}
				if (log.isDebugEnabled()) {
					log.debug(METHOD_NAME, "Exited");
				}
			}
			return map;
		}

	}
