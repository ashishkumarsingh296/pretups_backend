package com.btsl.user.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
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
import com.btsl.util.OracleUtil;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheLoader.InvalidCacheLoadException;
import com.google.common.cache.LoadingCache;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class CellIdCache implements Runnable {

    private static final Log LOG = LogFactory.getLog(CellIdCache.class.getName());
    private static Map<String, String> _cellIdMap = new HashMap<String, String>();
    private static String redisEnable = BTSLUtil.NullToString(Constants.getProperty("REDIS_ENABLE"));
    private static final String hKeyCellIdMap = "CellIdCache";
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss:mss").create();
    private static final int keyTimer = Integer.parseInt(Constants.getProperty("KEYTIMER"));
    
    private static LoadingCache<String, String> cellIdCache = CacheBuilder.newBuilder()
    	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)
    	    .build(new CacheLoader<String, String>(){
    			@Override
    			public String load(String key) throws Exception {
    				return getCellIdFromRedis(key);
    			}
    	     });   
    public void run() {
        try {
            Thread.sleep(50);
            loadCellIdAtStartUp();
        } catch (Exception e) {
        	LOG.error("CellIdCache init() Exception ", e);
        }
    }
    public static void loadCellIdAtStartUp() {
    	String methodName= "loadCellIdAtStartUp";
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadCellIdAtStartUp", "Entered");
        }
        if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        	 RedisActivityLog.log("CellIdCache->loadCellIdAtStartUp->Start");
        	 Jedis jedis = null;
  			 try {
  			    jedis = RedisConnectionPool.getPoolInstance().getResource(); 
  				Pipeline pipeline = jedis.pipelined();
  				pipeline.del(hKeyCellIdMap);
   				Map<String, String> _cellIdMap1 =  loadMapping();
  				for (Entry<String, String> entry : _cellIdMap1.entrySet())  {
  				        pipeline.hset(hKeyCellIdMap, entry.getKey(), gson.toJson(entry.getValue()));
   		         	}
  				pipeline.sync();
  			 }catch(JedisConnectionException je){
  	        	LOG.error(methodName, PretupsI.EXCEPTION + je.getMessage());
  		        LOG.errorTrace(methodName, je);
  	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CellIdCache[loadCellIdAtStartUp]", "", "", "", "JedisConnectionException :" + je.getMessage());
  			 }catch(NoSuchElementException  ex){
  		 		LOG.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
  		        LOG.errorTrace(methodName, ex);
  	            EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CellIdCache[loadCellIdAtStartUp]", "", "", "", "NoSuchElementException :" + ex.getMessage());
  			 }catch (Exception e) {
  				LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
  		    	LOG.errorTrace(methodName, e);
  			 }finally {
  		    	if (jedis != null) {
  		    	jedis.close();
  		    	}
  			 RedisActivityLog.log("CellIdCache->loadCellIdAtStartUp->End");
  			 }
		} else {	 
			 _cellIdMap = loadMapping();
	   }
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadCellIdAtStartUp", "Exited");
        }

    }

    public static Map<String, String> loadMapping() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadMapping", "Entered");
        }

        try {
	   _cellIdMap = loadCellId();
        } catch (Exception e) {
            LOG.error("loadMapping", "Exception e:" + e.getMessage());
            LOG.errorTrace("loadMapping", e);
        }

        return _cellIdMap;
    }

    public static Object getObject(String p_cellid, String p_grph_domain_code) {

        String key = null;
        String geog_domain_type = null;
        if (LOG.isDebugEnabled()) {
            LOG.debug("getObject()", "Entered  p_cellid=" + p_cellid + " p_grph_domain_code: " + p_grph_domain_code);
        }
        key = p_cellid + "_" + p_grph_domain_code;
      try{
    	  if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        	geog_domain_type =  cellIdCache.get(key);
    	  } else {
    	   geog_domain_type = _cellIdMap.get(key);
    	  }
       }catch (ExecutionException e) {
			LOG.error("getObject", PretupsI.EXCEPTION + e.getMessage());
		    LOG.errorTrace("getObject", e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO,"CellIdCache[getObject]", "", "", "", "Exception :" + e.getMessage());
		 }catch(InvalidCacheLoadException  ex){
			LOG.error("getObject", PretupsI.EXCEPTION + ex.getMessage());
        	LOG.errorTrace("getObject", ex);
        	EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "CellIdCache[getObject]", "", "", "", "InvalidCacheLoadException :" + ex.getMessage());
		 }
        if (LOG.isDebugEnabled()) {
            LOG.debug("getObject()", "Exited " + geog_domain_type);
        }
        return geog_domain_type;
    }

    //
    public static Map<String, String> loadCellId() throws BTSLBaseException {

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        Connection con = null;
        Map<String, String> map = new HashMap<String, String>();
         
        CellIdCacheQry cellIdCacheQry = (CellIdCacheQry) ObjectProducer.getObject(QueryConstants.CELL_ID_CACHE_QRY, QueryConstants.QUERY_PRODUCER);
        final String sqlSelect = cellIdCacheQry.loadCellIdQry();
        String methodName = "loadCellId";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            con = OracleUtil.getSingleConnection();
            pstmtSelect = con.prepareStatement(sqlSelect);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                map.put(rs.getString("grph_cellid") + "_" + rs.getString("grph_domain_code"), rs.getString("GRPH_DOMAIN_type"));

            }

        } catch (SQLException sqe) {
            LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CellIdCache[loadCellId]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException("CellIdCache", methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CellIdCache[loadCellId]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException("CellIdCache", methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
        }
        return map;
    }

    public static String getCellIdFromRedis(String key){
        if (LOG.isDebugEnabled()) {
            LOG.debug("getCellIdFromRedis()", "Entered key: "+key);
        }
        String methodName ="getCellIdFromRedis";
        String celid = "";
        Jedis jedis = null;
		 try {
	        RedisActivityLog.log("CellIdCache->getCellIdFromRedis->Start DEEPA");
			jedis = RedisConnectionPool.getPoolInstance().getResource();
			celid = jedis.hget(hKeyCellIdMap,key);
	        RedisActivityLog.log("CellIdCache->getCellIdFromRedis->End DEEPA");
		 }catch(JedisConnectionException je){
			LOG.error(methodName, PretupsI.EXCEPTION + je.getMessage());
	        LOG.errorTrace(methodName, je);
           EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "CellIdCache[" + methodName +"]", "", "", "", "JedisConnectionException :" + je.getMessage());
		}catch(NoSuchElementException  ex){
			LOG.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
	        LOG.errorTrace(methodName, ex);
           EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "CellIdCache[" + methodName +"]", "", "", "", "NoSuchElementException :" + ex.getMessage());
		}catch (Exception e) {
			LOG.error(methodName, PretupsI.EXCEPTION + e.getMessage());
	        LOG.errorTrace(methodName, e);
           EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "CellIdCache[" + methodName +"]", "", "", "", "Exception :" + e.getMessage());
		}finally {
    		 if(jedis != null ) {
    			 jedis.close();
    		 }
	        if (LOG.isDebugEnabled()) {
	            LOG.debug("getCellIdFromRedis()", "Exited celid: "+celid);
	        }
    	   }
		 return celid;
		 }
}
