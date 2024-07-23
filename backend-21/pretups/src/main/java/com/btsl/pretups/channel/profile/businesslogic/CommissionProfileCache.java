package com.btsl.pretups.channel.profile.businesslogic;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.GsonType;
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
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheLoader.InvalidCacheLoadException;
import com.google.common.cache.LoadingCache;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
//import redis.clients.jedis.ScanParams;
//import redis.clients.jedis.ScanResult;
//import redis.clients.jedis.ScanParams;
//import redis.clients.jedis.ScanResult;


import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.exceptions.JedisConnectionException;

//import redis.clients.jedis.params.ScanParams;
//import redis.clients.jedis.resps.ScanResult;
//import redis.clients.jedis.params.ScanParams;
//import redis.clients.jedis.resps.ScanResult;

public class CommissionProfileCache implements Runnable{

    private static Log _log = LogFactory.getLog(CommissionProfileCache.class.getName());
    private static ConcurrentMap<String, ArrayList<CommissionProfileSetVO>> _commProfileSetVersionMap = new ConcurrentHashMap<String, ArrayList<CommissionProfileSetVO>>();
    private static ConcurrentMap<String, ArrayList<Serializable>> _commissionProfilesMap = new ConcurrentHashMap<String, ArrayList<Serializable>>();
    private final static ReentrantLock lock = new ReentrantLock();
    private static final String hKeyCommissionProfile = "CommissionProfile";
    private static final String hKeyCommProfileSetVersion = "CommProfileSetVersion";
    private static String redisEnable = BTSLUtil.NullToString(Constants.getProperty("REDIS_ENABLE"));
    private static Gson gson = new GsonBuilder()
    		.setDateFormat("yyyy-MM-dd HH:mm:ss:mss")
    		.create();
    
    private static Type listofSerializable= new TypeToken< ArrayList<Serializable>>() {}.getType();
    private static GsonBuilder builder = new GsonBuilder().registerTypeAdapter(listofSerializable, new GsonType()).setDateFormat("yyyy-MM-dd HH:mm:ss:mss");
    private static Gson gsonType = builder.create();
    
    
    private static final int keyTimer = Integer.parseInt(Constants.getProperty("KEYTIMER"));
    
    private static LoadingCache<String, ArrayList<CommissionProfileSetVO>> commProfileSetVersionMemo = CacheBuilder.newBuilder()
    	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)
    	    .build(new CacheLoader<String, ArrayList<CommissionProfileSetVO>>(){
    			@Override
    			public ArrayList<CommissionProfileSetVO> load(String key) throws Exception {
    				return getCommProfileSetVersionListFormRedis(key);
    			}
    	     });
    private static LoadingCache<String, ArrayList<Serializable>> commissionProfilesMemo = CacheBuilder.newBuilder()
    	    .expireAfterWrite(keyTimer, TimeUnit.MILLISECONDS)
    	    .build(new CacheLoader<String, ArrayList<Serializable>>(){
    			@Override
    			public ArrayList<Serializable> load(String key) throws Exception {
    				return getCommissionProfileList(key);
    			}
    	     });
    

    /*private static CommissionProfileCache commissionProfilesMap = new CommissionProfileCache(Integer.parseInt(Constants.getProperty("MAX_COMM_PROFILES")));
    private static int capacity = 0;
    public CommissionProfileCache(int capacity) {
        super(capacity + 1, 1.1f, true);
        this.capacity = capacity;
    }*/
    
    public CommissionProfileCache() {
    }
    
    public void run() {
    	   _log.info("ConfigServlet", "ConfigServlet Start CommissionProfileCache loading ................... ");
        try {
            Thread.sleep(50);
            loadCommissionProfilesAtStartup();
        } catch (Exception e) {
        	 _log.error("CommissionProfileCache init() Exception ", e);
        }
        _log.info("configServlet", "ConfigServlet End CommissionProfileCache loading................... ");
    }
    
    public static void loadCommissionProfilesAtStartup() throws BTSLBaseException {
    	final String methodName = "loadCommissionProfilesAtStartup";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
    	CommissionProfileDAO commissionprofileDAO = null;
    	try{
    		commissionprofileDAO = new CommissionProfileDAO();
    		if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
     		   RedisActivityLog.log("CommissionProfileCache->loadCommissionProfilesAtStartup->Start");
     		   Jedis jedis = null;
 			 try {
 				 jedis = RedisConnectionPool.getPoolInstance().getResource();
  				if(!jedis.exists(hKeyCommProfileSetVersion)) {
  					 Pipeline pipeline = jedis.pipelined();
  					 ConcurrentMap<String, ArrayList<CommissionProfileSetVO>> commProDetMap = commissionprofileDAO.loadCommissionProfileCache();
 					 for (Entry<String, ArrayList<CommissionProfileSetVO>> entry : commProDetMap.entrySet())  {
 				      pipeline.hset(hKeyCommProfileSetVersion, entry.getKey(), gson.toJson(entry.getValue()));
 					 }
 					 pipeline.sync(); 
 					CacheOperationLog.log("CommissionProfileCache#OnStartup", commProDetMap);
 				 }
 			 }catch(JedisConnectionException je){
 			 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
 	   		        _log.errorTrace(methodName, je);
 	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileCache[loadCommissionProfilesAtStartup]", "", "", "", "JedisConnectionException :" + je.getMessage());
 	   		        throw new BTSLBaseException(CommissionProfileCache.class.getName(), methodName,je.getMessage());
 			 }catch(NoSuchElementException  ex){
 			 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
 	   		        _log.errorTrace(methodName, ex);
 	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileCache[loadCommissionProfilesAtStartup]", "", "", "", "NoSuchElementException :" + ex.getMessage());
 	   		        throw new BTSLBaseException(CommissionProfileCache.class.getName(), methodName,ex.getMessage());
 			 }catch (Exception e) {
 			 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
 			 	_log.errorTrace(methodName, e);
 	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileCache[loadCommissionProfilesAtStartup]", "", "", "", "Exception :" + e.getMessage());
 				throw new BTSLBaseException(CommissionProfileCache.class.getName(), methodName,e.getMessage());
 			 }finally {
   	        	if (jedis != null) {
   	        	jedis.close();
   	        	}
   	        }
 			   RedisActivityLog.log("CommissionProfileCache->loadPrefrencesOnStartUp->End");
     	}else {
    		_commProfileSetVersionMap = commissionprofileDAO.loadCommissionProfileCache();
    		CacheOperationLog.log("CommissionProfileCache#OnStartup", _commProfileSetVersionMap);
     	 }	
    	}
    	catch(BTSLBaseException be) {
    		_log.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
    		_log.errorTrace(methodName, be);
    		throw new BTSLBaseException(be);
    	}
    	catch (Exception e)
    	{
    		_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
    		_log.errorTrace(methodName, e);
    		throw new BTSLBaseException(e);
    	} finally {
    		if (_log.isDebugEnabled()) {
    			_log.debug(methodName, PretupsI.EXITED + _commProfileSetVersionMap.size());
    		}
    	}
    }
    
    public static void loadCommissionProfilesDetails(String setId, String profileVersion) {
        if (_log.isDebugEnabled()) {
            _log.debug("loadCommissionProfilesDetails", "entered");
        }
        String[] mapKey = null;
        CommissionProfileDAO commissionprofileDAO = null;
        ArrayList currSetVersionList = null;
        String methodName = "loadCommissionProfilesDetails";
        lock.lock();
        try {
        	if(PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
        		 RedisActivityLog.log("CommissionProfileCache->loadCommissionProfilesAtStartup->Start");
       		   Jedis jedis = null;
   			 try {
   				 jedis = RedisConnectionPool.getPoolInstance().getResource();
   				 String prefObj = jedis.hget(hKeyCommissionProfile, setId+"_"+profileVersion);
   				if(!BTSLUtil.isNullString(prefObj))
   					currSetVersionList = gsonType.fromJson(prefObj,listofSerializable);
   			 
   				 if(BTSLUtil.isNullString(jedis.hget(hKeyCommissionProfile, setId+"_"+profileVersion))) {
   					currSetVersionList = commissionprofileDAO.loadCommissionProfileDetailsCache(setId, profileVersion);
   	        		CacheOperationLog.log("CommissionProfileCache#OnTransaction", currSetVersionList);
   	        		String cursor = "0";
   	        	    ScanParams sp = new ScanParams();
   	        	    sp.match(setId+"_*");
   	        	   List<Entry<String, String>> result = null;
   	        	    do{
   	        	      ScanResult<Entry<String, String>> ret = jedis.hscan(hKeyCommissionProfile,cursor, sp);
   	        	       result = ret.getResult();
   	        	      if(result != null &&!result.isEmpty()) {
   	        	    	  jedis.hdel(result.get(1).getKey(), result.get(1).getValue());
   	        	      }
   	        	      cursor = String.valueOf(ret.getCursor());
   	        	    }while(!cursor.equals("0"));
   	        		jedis.hset(hKeyCommissionProfile, setId+"_"+profileVersion, gsonType.toJson(currSetVersionList));
  				 }
        	}catch(JedisConnectionException je){
		 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
   		        _log.errorTrace(methodName, je);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileCache[loadCommissionProfilesDetails]", "", "", "", "JedisConnectionException :" + je.getMessage());
   		        throw new BTSLBaseException(CommissionProfileCache.class.getName(), methodName,je.getMessage());
		 }catch(NoSuchElementException  ex){
		 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
   		        _log.errorTrace(methodName, ex);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileCache[loadCommissionProfilesDetails]", "", "", "", "NoSuchElementException :" + ex.getMessage());
   		        throw new BTSLBaseException(CommissionProfileCache.class.getName(), methodName,ex.getMessage());
		 }catch (Exception e) {
		 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
		 	_log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileCache[loadCommissionProfilesDetails]", "", "", "", "Exception :" + e.getMessage());
			throw new BTSLBaseException(CommissionProfileCache.class.getName(), methodName,e.getMessage());
		 }finally {
	        	if (jedis != null) {
	        	jedis.close();
	        	}
	        }
   	   }   else {
        	if(_commissionProfilesMap.get(setId+"_"+profileVersion) == null){
            	commissionprofileDAO = new CommissionProfileDAO();
        		currSetVersionList = commissionprofileDAO.loadCommissionProfileDetailsCache(setId, profileVersion);
        		CacheOperationLog.log("CommissionProfileCache#OnTransaction", currSetVersionList);
        		Iterator itr = _commissionProfilesMap.keySet().iterator();
        		while (itr.hasNext()) {
        			mapKey = ((String)itr.next()).split("_");
        			if (mapKey[0].equals(setId)) {
        				itr.remove();
        			}
        		}
        		_commissionProfilesMap.put(setId+"_"+profileVersion, currSetVersionList);
        	}
          }	
        } catch (BTSLBaseException e) {
            _log.errorTrace("loadCommissionProfilesDetails", e);
        }finally {
            lock.unlock();
            CacheOperationLog.log("CommissionProfileCache#OnTransaction", _commissionProfilesMap);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("loadCommissionProfilesDetails", "exited");
        }
    }

    public static Object getObject(String setID, Date date) {

        if (_log.isDebugEnabled()) {
            _log.debug("getObject", "Entered:setID=" + setID + "Date" + date);
        }
        String methodName = "getObject";
        CommissionProfileSetVO closestVO = null;
        ArrayList<CommissionProfileSetVO> commProfileList = new ArrayList<CommissionProfileSetVO>();
        final Date currenttimestamp = BTSLUtil.getSQLDateTimeFromUtilDate(date);
    	if(PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
    		try {
				commProfileList = commProfileSetVersionMemo.get(setID);
			} catch (ExecutionException e) {
				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
   		        _log.errorTrace(methodName, e);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileCache[getObject]", "", "", "", "ExecutionException :" + e.getMessage());
			}catch (InvalidCacheLoadException e) { 
	    		_log.info(methodName, e);
			}

    	} else {
    		commProfileList = _commProfileSetVersionMap.get(setID);
    	}
        
        long min = 0;
        for (final CommissionProfileSetVO commProfileVO : commProfileList) {
            if (((min == 0) || (currenttimestamp.getTime() - commProfileVO.getApplicableFrom().getTime() < min)) && (currenttimestamp.compareTo(commProfileVO.getApplicableFrom()) >= 0)) {
                min = (long) currenttimestamp.getTime() - commProfileVO.getApplicableFrom().getTime();
                closestVO = commProfileVO;
            }
        }
        return closestVO;

    }

    public static ArrayList<Serializable> getObject(String setID, String profileVersion) {
        if (_log.isDebugEnabled()) {
            _log.debug("getObject", "Entered:setID=" + setID + "profileVersion" + profileVersion);
        }
        ArrayList<Serializable> al = null;
        String methodName = "getObject";
        if(PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
    		try {
				al = commissionProfilesMemo.get(setID);
			} catch (ExecutionException e) {
				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
   		        _log.errorTrace(methodName, e);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileCache[getObject]", "", "", "", "ExecutionException :" + e.getMessage());
			}catch (InvalidCacheLoadException e) { 
	    		_log.info(methodName, e);
			}

    	} else {
    		 al = (ArrayList) _commissionProfilesMap.get(setID + "_" + profileVersion);
    	}
        return al;
    }

    public static void updateCommissionProfileMapping() throws BTSLBaseException {
    	final String methodName = "updateCommissionProfileMapping";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, PretupsI.ENTERED);
    	}
    	CommissionProfileDAO commissionprofileDAO = null;
    	ConcurrentMap<String, ArrayList<CommissionProfileSetVO>> currentMap = null;
    	try{
    		commissionprofileDAO = new CommissionProfileDAO();
    		currentMap = commissionprofileDAO.loadCommissionProfileCache();
    		if (PretupsI.REDIS_ENABLE.equals(redisEnable.trim())) {
   	   		 RedisActivityLog.log("CommissionProfileCache->updateCommissionProfileMapping->Start");
   	   		 Jedis jedis = null;
   			 try {
   				 jedis = RedisConnectionPool.getPoolInstance().getResource();
   				  Pipeline pipeline = jedis.pipelined();
   				  pipeline.del(hKeyCommissionProfile);
   	     		   for (Entry<String, ArrayList<CommissionProfileSetVO>> entry : currentMap.entrySet())  {
   		  		       pipeline.hset(hKeyCommissionProfile, entry.getKey(),gsonType.toJson(entry.getValue()));
   		  			}
   		  			pipeline.sync();
    			 }catch(JedisConnectionException je){
    				 _log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
    				 _log.errorTrace(methodName, je);
    	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileCache[updateCommissionProfileMapping]", "", "", "", "JedisConnectionException :" + je.getMessage());
    	   		        throw new BTSLBaseException(CommissionProfileMinCache.class.getName(), methodName,je.getMessage());
    			 }catch(NoSuchElementException  ex){
    				 _log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
    				 _log.errorTrace(methodName, ex);
    	   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileCache[updateCommissionProfileMapping]", "", "", "", "NoSuchElementException :" + ex.getMessage());
    	   		        throw new BTSLBaseException(CommissionProfileMinCache.class.getName(), methodName,ex.getMessage());
    			 }catch (Exception e) {
    				 _log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
    				 _log.errorTrace(methodName, e);
    	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileCache[updateCommissionProfileMapping]", "", "", "", "Exception :" + e.getMessage());
    				throw new BTSLBaseException(CommissionProfileMinCache.class.getName(), methodName,e.getMessage());
    			 }
    			 finally {
    		        	if (jedis != null) {
    		        	jedis.close();
    		        	}
    		        }
   	   	RedisActivityLog.log("CommissionProfileCache->updateCommissionProfileMapping->End");
     	   } else {
       		_commProfileSetVersionMap = currentMap;
       		CacheOperationLog.log("CommissionProfileCache#OnUpdateCache", _commissionProfilesMap);
       		CommissionProfileMinCache.updateMinCommProfileMapping();
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
        	throw new BTSLBaseException("CommissionProfileCache", methodName, "");
        } finally {
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, PretupsI.EXITED + _commProfileSetVersionMap.size());
        	}
        }
    }
    public static ArrayList<CommissionProfileSetVO> getCommProfileSetVersionListFormRedis(String key) throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("getCommProfileSetVersionListFormRedis", "Entered:key=" + key);
        }
        String methodName = "getCommProfileSetVersionListFormRedis";
        ArrayList<CommissionProfileSetVO> commProfileSetVerList = new ArrayList<CommissionProfileSetVO>();
        RedisActivityLog.log("CommissionProfileCache->getCommProfileSetVersionListFormRedis->Start");
    	Jedis jedis = null;
		 try {
			 jedis = RedisConnectionPool.getPoolInstance().getResource();
			 String prefObj = jedis.hget(hKeyCommProfileSetVersion, key);
			if(!BTSLUtil.isNullString(prefObj))
				commProfileSetVerList = gson.fromJson(prefObj,new TypeToken<ArrayList<CommissionProfileSetVO>>(){}.getType());
		 }catch(JedisConnectionException je){
		 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
   		        _log.errorTrace(methodName, je);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileCache[getCommProfileSetVersionListFormRedis]", "", "", "", "JedisConnectionException :" + je.getMessage());
   		        throw new BTSLBaseException(CommissionProfileCache.class.getName(), methodName,je.getMessage());
		 }catch(NoSuchElementException  ex){
		 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
   		        _log.errorTrace(methodName, ex);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileCache[getCommProfileSetVersionListFormRedis]", "", "", "", "NoSuchElementException :" + ex.getMessage());
   		        throw new BTSLBaseException(CommissionProfileCache.class.getName(), methodName,ex.getMessage());
		 }catch (Exception e) {
		 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
		 	_log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileCache[getCommProfileSetVersionListFormRedis]", "", "", "", "Exception :" + e.getMessage());
			throw new BTSLBaseException(CommissionProfileCache.class.getName(), methodName,e.getMessage());
		 }
		 finally {
	        	if (jedis != null) {
	        	jedis.close();
	        	}
	        }
		 RedisActivityLog.log("CommissionProfileCache->getCommProfileSetVersionListFormRedis->End");
        return commProfileSetVerList;
    }

    public static ArrayList<Serializable> getCommissionProfileList(String key) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("getObject", "Entered:key=" + key );
        }
        String methodName = "getCommissionProfileList";
        ArrayList<Serializable> commProfileList = new ArrayList<Serializable>();
        RedisActivityLog.log("CommissionProfileCache->getCommissionProfileList->Start");
    	Jedis jedis = null;
		 try {
			 jedis = RedisConnectionPool.getPoolInstance().getResource();
			 String prefObj = jedis.hget(hKeyCommissionProfile, key);
			if(!BTSLUtil.isNullString(prefObj))
				commProfileList = gsonType.fromJson(prefObj,listofSerializable);
		 }catch(JedisConnectionException je){
		 		_log.error(methodName, PretupsI.EXCEPTION + je.getMessage());
   		        _log.errorTrace(methodName, je);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileCache[getCommissionProfileList]", "", "", "", "JedisConnectionException :" + je.getMessage());
   		        throw new BTSLBaseException(CommissionProfileCache.class.getName(), methodName,je.getMessage());
		 }catch(NoSuchElementException  ex){
		 		_log.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
   		        _log.errorTrace(methodName, ex);
   	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileCache[getCommissionProfileList]", "", "", "", "NoSuchElementException :" + ex.getMessage());
   		        throw new BTSLBaseException(CommissionProfileCache.class.getName(), methodName,ex.getMessage());
		 }catch (Exception e) {
		 	_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
		 	_log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileCache[getCommissionProfileList]", "", "", "", "Exception :" + e.getMessage());
			throw new BTSLBaseException(CommissionProfileCache.class.getName(), methodName,e.getMessage());
		 }
		 finally {
	        	if (jedis != null) {
	        	jedis.close();
	        	}
	        }
		 RedisActivityLog.log("CommissionProfileCache->getCommissionProfileList->End");
        return commProfileList;

    }  
}
